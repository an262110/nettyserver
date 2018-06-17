package com.hc.app.action.jt;

import com.hc.app.model.jt.Data_0106;
import com.hc.app.model.jt.MegJT;
import com.hc.app.service.AccountInfoService;
import com.hc.app.service.ChargeHKService;
import com.hc.app.service.ChargeOrderService;
import com.hc.common.utils.hk.JTLogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("JT0xA205")
public class JT0xA205 implements JTActionI {

	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Autowired
	private ChargeHKService chargeHKServiceImpl;
	@Autowired
	private AccountInfoService accountInfoServiceImpl;
	@Override
	public void receive_send_data(ChannelHandlerContext ctx, byte[] meg, boolean debug) {
		Data_0106 data_0106 = new Data_0106(meg);
		MegJT megJT = new MegJT(data_0106, meg);
		Map map = megJT.getMap();
		byte[] bytes = megJT.getBytes();
		String msgID = String.valueOf(map.get("msgid"));
		if (debug) {
			JTLogUtils.info("收到报文:" + megJT.bytesToHexString(meg));
			megJT.JTwriteBytesToFile(meg, 1);
			
			JTLogUtils.info("发送报文" + megJT.bytesToHexString(bytes));
			megJT.JTwriteBytesToFile(bytes, 0);
			
		}else{
			  Map mapCharge = null;
			  try {
				mapCharge = chargeHKServiceImpl.findByMessageId(Integer.valueOf((String)map.get("msgid")));
		        String orderId=(String)mapCharge.get("ORDER_ID");
				Map paramMap = new HashMap();
				paramMap.put("CHARGE_ORDER_ID", orderId);
				if(megJT.getMeg4_1()[0] == 0x04)
					 stopCharge(mapCharge,paramMap);
				else if(megJT.getMeg4_1()[0] == 0x03)
					startCharge(mapCharge,paramMap);
				else if(megJT.getMeg4_1()[0] ==13 || megJT.getMeg4_1()[0] ==21)
					startChargeLose(mapCharge,paramMap);
				else if(megJT.getMeg4_1()[0] ==14 || megJT.getMeg4_1()[0] ==22)
					stopChargeLose(mapCharge,paramMap);		
					chargeHKServiceImpl.update(Integer.valueOf(mapCharge.get("ID").toString()),0);
				} catch (Exception e) {
					JTLogUtils.error(e.getMessage());
				}
			  ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
			  ctx.writeAndFlush(byteBuf);
		}

	}

	@Override
	public void data_persistence(MegJT meg) throws Exception {
		// TODO Auto-generated method stub

	}
	
	private void startCharge(Map mapCharge,Map paramMap) throws Exception{
		String orderId=(String)mapCharge.get("ORDER_ID");
	        paramMap.put("START_CHARGE_TIME", "1");
			paramMap.put("ORDER_STATE", "02");
			paramMap.put("CHARGE_GUN",mapCharge.get("GUN_NO").toString());
			chargeOrderServiceImpl.updateInfo(paramMap);	
			//冻结账户
			Map orderInfo=chargeOrderServiceImpl.searchOrderDetail(orderId,"02");
			String payType=(String)orderInfo.get("ORDER_TYPE");
			if(!"2".equals(payType)){//微信公众号支付不用冻结账户
			   accountInfoServiceImpl.update(orderInfo.get("USER_ID").toString(),"1");
			}
		     chargeHKServiceImpl.update(Integer.valueOf(mapCharge.get("ID").toString()),0);
				chargeOrderServiceImpl.updateInfo(paramMap);
	}
	
	private void startChargeLose(Map mapCharge,Map paramMap)throws Exception{
		paramMap.put("CHARGE_RET_39", "10");
		paramMap.put("CHARGE_RET_39_DESC", "金霆充电桩启动充电失败");
		
		chargeOrderServiceImpl.updateInfo(paramMap);
	}
	
	private void stopChargeLose(Map mapCharge,Map paramMap)throws Exception{
		paramMap.put("CHARGE_RET_39", "10");
		paramMap.put("CHARGE_RET_39_DESC", "金霆充电桩停止充电失败");
		chargeOrderServiceImpl.updateInfo(paramMap);
	}
	
	private void stopCharge(Map mapCharge,Map paramMap)throws Exception{
		paramMap.put("END_CHARGE_TIME", "1");
		paramMap.put("ORDER_STATE", "03");	
		chargeOrderServiceImpl.updateInfo(paramMap);
	}

}
