package com.hc.app.action.jt;

import com.hc.app.model.jt.Data_0107;
import com.hc.app.model.jt.MegJT;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ChargePileBillService;
import com.hc.app.service.JtService;
import com.hc.common.utils.hk.CaculateUtil;
import com.hc.common.utils.hk.JTLogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("JT0x0107")
public class JT0x0107 implements JTActionI {
	@Autowired
	private ChargePileBillService chargePileBillServiceImpl;

	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Autowired
	private JtService jtService;
	@Override
	public void receive_send_data(ChannelHandlerContext ctx, byte[] meg, boolean debug) {
		Data_0107 data_0107 = new Data_0107(meg);
		MegJT megJT = new MegJT(data_0107, meg);
		Map data = megJT.getMap();
		byte[] bytes = megJT.getBytes();
		String chargepile = String.valueOf(data.get("charge_pile"));
		if (debug) {
			JTLogUtils.info("收到的报文>>>>>>>>>>>>>>>>>>>>>>" + megJT.bytesToHexString(meg));
			megJT.JTwriteBytesToFile(meg, 1);
			megJT.JTwriteBytesToFile(bytes, 0);
			JTLogUtils.info("发动的报文>>>>>>>>>>>>>>>>>>>>>>" + megJT.bytesToHexString(bytes));
		}else{
			String orderID = String.valueOf(data.get("data2_16"));
			int gunNo = Integer.valueOf(String.valueOf(data.get("data3_1")));

			try{
				Map orderInfo=chargeOrderServiceImpl.findByOrderSeril(orderID);
				String orderId=(String)orderInfo.get("CHARGE_ORDER_ID");
				String orderState=(String)orderInfo.get("ORDER_STATE");

				int CHARGE_ELE_QUANTITY=Integer.valueOf(String.valueOf(data.get("CHARGE_QUANTITY")))*1000;//已充电量

				double cele=Double.valueOf(CHARGE_ELE_QUANTITY);

				cele=cele*1.05;
				CHARGE_ELE_QUANTITY=new java.math.BigDecimal(cele).setScale(0,java.math.BigDecimal.ROUND_UP).intValue();
				if(!"04".equals(orderState)){
					//计算费用
					Map<String,Double> feeInfo= CaculateUtil.calculateFee(CHARGE_ELE_QUANTITY, orderInfo);
					double servicePay =feeInfo.get("servicePay");//当前电费
					double elePay=feeInfo.get("elePay");//当前服务费

					//订单状态更改为 03 充电结束
					Map paramMap = new HashMap();
					paramMap.put("ORDER_STATE", "03");
					paramMap.put("CHARGE_USERID_9", orderID);
					chargeOrderServiceImpl.updateInfoHk(paramMap);

					chargeOrderServiceImpl.singleCheckBill(orderId);//结算
				}else{
					JTLogUtils.info("重复账单,重复结算!");
				}

				chargePileBillServiceImpl.savehk(data);
			}catch(Exception e){
				JTLogUtils.error(e.getMessage());
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
		jtService.addAccounts(map);

	}

}
