package com.hc.app.action.zw;


import com.hc.app.model.*;
import com.hc.app.service.AccountInfoService;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ZWService;
import com.hc.common.utils.hk.CaculateUtil;
import com.hc.common.utils.hk.ZWLogUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 充电信息上传
 * @author liuh
 *
 */

@Component("ZW0x5A")
public class ZW0x5A implements ZWActionI {
	@Autowired
	private ZWService zwServiceImpl;
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Autowired
	private AccountInfoService accountInfoServiceImpl;
	@Override
	public BodyI receive_or_send(ChannelHandlerContext ctx, Head head,
								 byte[] msg, boolean is_Debug) {
		Body0x5A body = new Body0x5A(msg);

		if(is_Debug){ // 为true 调试报文用
			head.setHead2_2(new byte[]{0x00,0x00});
			head.setHead8_1(new byte[]{0x5b});
			//NettyChannelMap.get("1001").writeAndFlush(head.getSendBuf());
			ctx.writeAndFlush(head.getSendBuf());
			return body;
		}

		Map<String, String> bMap = body.bytesToMap();
		Map<String, String> hMap = head.bytesToMap();

		String pileno_zw = hMap.get("head7_8");   //充电桩序列号

		String body1_1   =  bMap.get("body1_1");   //平台状态BIN
		String body2_5   =  bMap.get("body2_5");   //当前时间BCD码
		String body3_4   =  bMap.get("body3_4");   //*当前充电电量(度)
		String body4_4   =  bMap.get("body4_4");   //*当前充电金额(元)
		String body5_4   =  bMap.get("body5_4");   //*当前服务费金额
		String body6_4   =  bMap.get("body6_4");   //*当前消费金额
		String body7_3   =  bMap.get("body7_3");   //*充电电压(V)
		String body8_3   =  bMap.get("body8_3");   //*充电电流(A)
		String body9_3   =  bMap.get("body9_3");   //*充电时间(分)
		String body10_3  =  bMap.get("body10_3");  //*输出功率(W)
		String body11_1  =  bMap.get("body11_1");  //充电枪状态BIN
		String body12_1  =  bMap.get("body12_1");  //当前荷电状态SOC(%)
		String body13_2  =  bMap.get("body13_2");  //*估算剩余充电时间
		String body14_4  =  bMap.get("body14_4");  //详细故障代码
		String body15_10 =  bMap.get("body15_10"); //预留

		int body3_4_int = body.getBody3_4_int();  //业务处理用 body3_4 (原始)
		double body3_4_dou = new Integer(body3_4_int).doubleValue()*105/100;
		int body3_4_elequantity = new BigDecimal(body3_4_dou).setScale(0,0).intValue();

		//枪口 高4位 0 A口 1 B口 低4位 0空闲 1充电 +1代表对应枪表的 枪1和枪2
		char[] gun_no_arr = body1_1.toCharArray();
		int gun_no = Character.getNumericValue(gun_no_arr[0])+1;

		//查询订单 如果智网不改动情况下查找 充电中订单
		//通过 seri(智网)+枪口+充电中
		String order_status = "02";
		Map orderInfo = null;
		try {
			orderInfo=chargeOrderServiceImpl.findByOrderStatus(pileno_zw,gun_no,order_status);
		} catch (Exception e) {
			ZWLogUtils.info("充电信息上传查询异常>>>>>>"+e.getMessage());
			return body;
		}
		if(orderInfo==null){
			ZWLogUtils.info("充电信息为空：信息上传桩编号："+pileno_zw+" 充电枪口："+gun_no);
			return body;
		}

		String orderId=(String)orderInfo.get("CHARGE_ORDER_ID");
		String gun_code=(String)orderInfo.get("GUN_CODE");
		String orderState=(String)orderInfo.get("ORDER_STATE");

		//计算费用
		Map<String, Double> feeInfo = null;
		try {
			feeInfo = CaculateUtil.calculateFee(body3_4_elequantity, orderInfo);
		} catch (Exception e) {
			ZWLogUtils.info("充电信息上传计算费用异常"+e.getMessage());
			return body;
		}
		double servicePay =feeInfo.get("servicePay");//当前电费
		double elePay=feeInfo.get("elePay");//当前服务费
		double fee=servicePay+elePay;//总费用

		Map<String,Object> params = new HashMap<String,Object>();
		params.put("CURRENT_V",body7_3);
		params.put("CURRENT_A",body8_3);
		params.put("TOTAL_CHARGE_TIMES",body9_3);
		params.put("TOTAL_CHARGE_QUANTITY",String.valueOf(body3_4_elequantity));
		params.put("TOTAL_CHARGE_MONEY",String.valueOf(elePay));
		params.put("TOTAL_SERVICE_MONEY",String.valueOf(servicePay));
		params.put("ORI_SERVICE_MONEY",feeInfo.get("oriServicePay"));
		params.put("ORI_CHARGE_MONEY", feeInfo.get("oriElePay"));
		params.put("SOC",body12_1);
		params.put("END_CHARGE_TIME", "1");
//		params.put("BMS_TYPE",String.valueOf(data.get("BMS_TYPE"))); //智网未给出
		params.put("CHARGE_ORDER_ID", orderId);

		try {
			chargeOrderServiceImpl.updateInfo(params);
		} catch (Exception e) {
			ZWLogUtils.info("充电信息上传更新订单异常"+params);
			return body;
		}

		//验证账户余额是否足够本次支付
		int balance=0;
		String payType=(String)orderInfo.get("ORDER_TYPE");
		if(!"2".equals(payType)){
			Map account = null;
			try {
				account = accountInfoServiceImpl.findByUserId(orderInfo.get("USER_ID").toString());
			} catch (Exception e) {
				ZWLogUtils.info("查询用户账户异常 userid: "+orderInfo.get("USER_ID").toString());
				return body;
			}
			balance=Integer.valueOf(account.get("FROZEN_MONEY").toString());
		}else{
			balance=Integer.valueOf(orderInfo.get("PAY_MONEY").toString());
		}

		ZWLogUtils.info("枪编码："+gun_code+" 订单id："+orderId+" 充电余额："+balance+" 消费金额: "+fee+" 电费:"+elePay+"服务费："+servicePay);

		if(fee>0&&fee>=balance){
			//发送停止充电指令
			Body0x22 body22 = new Body0x22(body.getBody1_1(), orderId);
			Meg meg = Meg.message(head,body22);
			ctx.writeAndFlush(meg.getSendBuf());
			ZWLogUtils.info("充电信息上传判断账户余额不足停止充电 订单号："+orderId);
		}

		return body;
	}

	//数据库写入，有逻辑，比如心跳和账单，所以分开写
	@Override
	public boolean business_todb(Meg meg, boolean is_Debug) {

		ZWLogUtils.info("写入数据库>>>>>>meg="+meg);
		ZWLogUtils.info("写入数据库>>>>>>meg.bytesToMap()="+meg.bytesToMap());
		// if(is_Debug){
		zwServiceImpl.addBody0x5A(meg.bytesToMap());
		//return true;
		//  }

		return true;
	}

}
