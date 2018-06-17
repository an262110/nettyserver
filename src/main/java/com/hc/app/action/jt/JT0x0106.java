package com.hc.app.action.jt;

import com.hc.app.model.jt.Data_0106;
import com.hc.app.model.jt.Data_0205;
import com.hc.app.model.jt.MegJT;
import com.hc.app.service.AccountInfoService;
import com.hc.app.service.ChargeHKService;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ChargePileService;
import com.hc.common.utils.hk.CaculateUtil;
import com.hc.common.utils.hk.JTLogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * <p>
 * title :金霆报文处理
 * </p>
 * <p>
 * Description :工作数据指令
 * </p>
 * <p>
 * Company : 广州爱电牛科技有限公司
 * </p>
 * 
 * @date 2017年3月21日
 * @author 小吴
 */
@Component("JT0x0106")
public class JT0x0106 implements JTActionI {

	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Autowired
	private AccountInfoService accountInfoServiceImpl;
	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Autowired
	private ChargeHKService chargeHKServiceImpl;
	@Autowired
	private com.hc.app.service.JtService JtService;

	@Override
	public void receive_send_data(ChannelHandlerContext ctx, byte[] meg, boolean debug) {
		Data_0106 data_0106 = new Data_0106(meg);
		MegJT megJT = new MegJT(data_0106, meg);
		Map map = megJT.getMap();
		byte[] bytes = megJT.getBytes();
		if (debug) {
			JTLogUtils.info("收到报文:" + megJT.bytesToHexString(meg));
			megJT.JTwriteBytesToFile(meg, 1);
			JTLogUtils.info("发送报文" + megJT.bytesToHexString(bytes));
			megJT.JTwriteBytesToFile(bytes, 0);
		} else {
			// 获取订单号
			String orderID = String.valueOf(map.get("data2_16"));
			// 电流 电压
			int v_value = Integer.valueOf(String.valueOf(map.get("data7_2"))) * 10;
			int a_value = Integer.valueOf(String.valueOf(map.get("data8_2"))) * 10;
			int charge_value = Integer.valueOf(String.valueOf(map.get("data9_2"))) * 1000;
			int time_value = Integer.valueOf(String.valueOf(map.get("data10_2")));
			String start = String.valueOf(map.get("data5_6"));
			StringBuilder text = new StringBuilder();
			hexToint(start, text, start.length());
			start = text.toString();
			if (time_value == 0) {
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
				String date = df.format(new Date()).substring(0, 2);
				try {
					Date begin = df.parse(date + text.toString());
					Date end = Calendar.getInstance().getTime();
					long between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒
					// System.out.println(between/60);
					time_value = (int) (between / 60);
				} catch (Exception e) {
					JTLogUtils.error("出现异常了>>>>>>>>>>>>>>>>>" + e.getMessage());
				}
			}
			double cele = Double.valueOf(charge_value);
			cele = cele * 1.05;
			charge_value = new java.math.BigDecimal(cele).setScale(0, java.math.BigDecimal.ROUND_UP).intValue();

			int SOC = Integer.valueOf(String.valueOf(map.get("data12_1")));// 已充电量

			// 订单
			Map orderInfo = null;
			try {
				orderInfo = chargeOrderServiceImpl.findByOrderSeril(orderID);
			} catch (Exception e) {
				JTLogUtils.error(e.getMessage());
			}
			String orderId = (String) orderInfo.get("CHARGE_ORDER_ID");
			String gun_code = (String) orderInfo.get("GUN_CODE");
			String orderState = (String) orderInfo.get("ORDER_STATE");

			if (!"04".equals(orderState)) {

				// 计算费用
				Map<String, Double> feeInfo = null;
				try {
					feeInfo = CaculateUtil.calculateFee(charge_value, orderInfo);
				} catch (Exception e1) {
					JTLogUtils.error(e1.getMessage());
				}

				double servicePay = feeInfo.get("servicePay");// 当前电费
				double elePay = feeInfo.get("elePay");// 当前服务费

				double fee = servicePay + elePay;// 总费用
				// 更新资费信息到订单
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("CURRENT_V", String.valueOf(v_value));// *10
				params.put("CURRENT_A", String.valueOf(a_value));
				params.put("TOTAL_CHARGE_TIMES", String.valueOf(time_value));
				params.put("TOTAL_CHARGE_QUANTITY", String.valueOf(charge_value));
				params.put("TOTAL_CHARGE_MONEY", String.valueOf(elePay));
				params.put("TOTAL_SERVICE_MONEY", String.valueOf(servicePay));
				params.put("ORI_SERVICE_MONEY", feeInfo.get("oriServicePay"));
				params.put("ORI_CHARGE_MONEY", feeInfo.get("oriElePay"));
				params.put("SOC", String.valueOf(SOC));
				params.put("END_CHARGE_TIME", "1");
				// params.put("BMS_TYPE",String.valueOf(map.get("BMS_TYPE")));
				params.put("BMS_TYPE", "jt");
				params.put("CHARGE_ORDER_ID", orderId);

				if ("01".equals(orderState)) {
					params.put("ORDER_STATE", "02");
				}
				try {
					chargeOrderServiceImpl.updateInfo(params);
				} catch (Exception e) {
					JTLogUtils.error("出现异常了>>>>>>>>>>>>>>>>>>>>>" + e.getMessage());
				}

				// 验证账户余额是否足够本次支付
				int balance = 0;
				String payType = (String) orderInfo.get("ORDER_TYPE");
				if (!"2".equals(payType)) {
					Map account = null;
					try {
						account = accountInfoServiceImpl.findByUserId(orderInfo.get("USER_ID").toString());
					} catch (Exception e) {
						JTLogUtils.error(e.getMessage());
					}
					balance = Integer.valueOf(account.get("FROZEN_MONEY").toString());
					JTLogUtils.info("FROZEN_MONEY: " + balance);
				} else {
					balance = Integer.valueOf(orderInfo.get("PAY_MONEY").toString());
					JTLogUtils.info("PAY_MONEY: " + balance);
				}
				if (fee > 0 && fee >= balance) {
					JTLogUtils.info("info>>>>>>>>余额不足");
					JTLogUtils.info("info>>>>>>>>账户余额" + balance);

					JTLogUtils.info("info>>>>>>>>消费金额" + fee);
					JTLogUtils.info("info>>>>>>>>电费" + elePay);
					JTLogUtils.info("info>>>>>>>>服务费" + servicePay);
					JTLogUtils.info("info>>>>>>>>orderid" + orderId);
					JTLogUtils.info("info>>>>>>>>gun_code" + gun_code);
					// 这里要写停止充电指令
					Data_0205 data_0205 = new Data_0205(meg);
					data_0205.setData1_16(data_0106.getData1_16());
					data_0205.setData2_16(data_0106.getData2_16());
					data_0205.setData3_1(data_0106.getData3_1());
					data_0205.setData4_1(new byte[] { 0x01 });
					data_0205.setData5_8(data_0106.getData4_8());
					data_0205.setData6_1(new byte[] { 0x01 });
					data_0205.setData7_2(new byte[] { (byte) 0xff, (byte) 0xff });
					data_0205.setData8_4(new byte[] { 0x01, 0x00, 0x00 });
					MegJT megs = new MegJT(data_0205, meg);
					ByteBuf buffer = Unpooled.copiedBuffer(megs.getBytes());
					ctx.writeAndFlush(buffer);
				}
			} else {
				JTLogUtils.info("订单已结算，延迟的状态数据包");
				JTLogUtils.info("数据包电量信息，TOTAL_CHARGE_QUANTITY=" + charge_value);
				JTLogUtils.info("订单电量信息，TOTAL_CHARGE_QUANTITY=" + orderInfo.get("TOTAL_CHARGE_QUANTITY").toString());
			}
		}
		ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
		ctx.writeAndFlush(byteBuf);
		try {
			data_persistence(megJT);
		} catch (Exception e) {
			JTLogUtils.error(e.getMessage());
		}
	}

	@Override
	public void data_persistence(MegJT meg) throws Exception {
		Map map = meg.getMap();
		JtService.addWorkDate(map);
	}

	// 递归
	private void hexToint(String time, StringBuilder text, int flag) {
		flag = time.length();
		String t = time.substring(2);
		flag -= 2;
		time = time.substring(0, 2);
		Integer i = Integer.valueOf(time, 16);
		String pix = i < 10 ? "0" + String.valueOf(i) : String.valueOf(i);
		text.append(pix);
		if (flag > 0)
			hexToint(t, text, flag);
	}

}
