package com.hc.app.action.hk;


import com.hc.app.action.BaseAction;
import com.hc.app.service.AccountInfoService;
import com.hc.app.service.ChargeHKService;
import com.hc.app.service.ChargeOrderService;
import com.hc.common.utils.CommonUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("HK6101")
public class HK6101 implements BaseAction {
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Autowired
	private ChargeHKService chargeHKServiceImpl;
	@Autowired
	private AccountInfoService accountInfoServiceImpl;
	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		
		RequestObject ob=(RequestObject)msg;
        Map data=ob.getData();
        
        Map header=ob.getHeader();
        
        int messageId=(Integer)header.get("MESSAGE_ID");
        
        System.out.println(messageId);
        Map mapCharge=chargeHKServiceImpl.findByMessageId(messageId);
        String orderId=(String)mapCharge.get("ORDER_ID");
        
        
		//（00：成功；01：MAC校验失败，02：充电桩序列号不对应，03：未插枪，04：充电桩已被使用，无法充电，
		// 05：非当前用户，无法取消充电, 06：充电桩故障无法充电,07:充电桩忙, 08:余额不足，09:车辆未准备好
		int retCode=(Integer)data.get("RETCODE");
		Map paramMap = new HashMap();
		paramMap.put("CHARGE_ORDER_ID", orderId);
		if(retCode!=1){
			String err="01";
			if(retCode==2||retCode==7){
				err="03";
			}else if(retCode==3||retCode==4){
				err="06";
			}else if(retCode==5||retCode==6){
				err="02";
			}
			
			paramMap.put("CHARGE_RET_39", err);
			paramMap.put("CHARGE_RET_39_DESC", CommonUtil.infoCodeDesc(err));
			
			chargeOrderServiceImpl.updateInfo(paramMap);
			
		}else{
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
		}
		
		chargeHKServiceImpl.update(Integer.valueOf(mapCharge.get("ID").toString()),retCode);
        
         return null;
	}
  
}

