package com.hc.app.action.zw;

import com.hc.app.model.*;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ZWService;
import com.hc.common.utils.CommonUtil;
import com.hc.common.utils.hk.ZWLogUtils;
import com.yao.NettyChannelMap;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 下发充电请求：平台回复
 * 
 * @author zc
 * 
 */

@Component("ZW0x21")
public class ZW0x21 implements ZWActionI {
	@Autowired
	private ZWService zwServiceImpl;
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;

	@Override
	public BodyI receive_or_send(ChannelHandlerContext ctx, Head head,
                                 byte[] msg, boolean is_Debug) {
		Body0x21 body = new Body0x21(msg);
		
		// 为true 调试报文用 (byte)0x04 自动充满
		if(is_Debug){ 
			head.setHead8_1(new byte[]{0x10});
			byte b = 0x04;
			Body0x10 body10 = new Body0x10(body.getBody1_1(),b,body.getBody2_10());
			Meg meg = Meg.message(head,body10);
			NettyChannelMap.get("1001").writeAndFlush(meg.getSendBuf());
			return body;
		}
		
		Map<String, String> bMap = body.bytesToMap();
        Map<String, String> hMap = head.bytesToMap();
        
		String order_id = bMap.get("body2_10");
		String state = bMap.get("body3_1");

		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("CHARGE_ORDER_ID", order_id);
		// （00：成功；01：MAC校验失败，02：充电桩序列号不对应，03：未插枪，04：充电桩已被使用，无法充电，
		// 05：非当前用户，无法取消充电, 06：充电桩故障无法充电,07:充电桩忙, 08:余额不足，09:车辆未准备好(zw:bms通信不正常)
		if (!"07".equals(state)) {
			String err = "01";
			if ("0".equals(state.charAt(1))) {
				err = "09";
			} else if ("1".equals(state.charAt(4))) {
				err = "03";
			}
			paraMap.put("CHARGE_RET_39", err);
			paraMap.put("CHARGE_RET_39_DESC", CommonUtil.infoCodeDesc(err));

			try {
				chargeOrderServiceImpl.updateInfo(paraMap);
			} catch (Exception e) {
				ZWLogUtils.info("充电请求返回21错误码码更新失败："+state);
	  			return body;
			}
		}
		
		//0x10的业务处理，下发充电开启指令
		Body0x10 body_0x10 = new Body0x10(body.getBody1_1(), (byte) 0x04,body.getBody2_10());// 拼发送数据
		head.setHead8_1(new byte[]{0x10});
		Meg meg = Meg.message(head, body_0x10);
		NettyChannelMap.get("1001").writeAndFlush(meg.getSendBuf());
		return body;
	}

	// 数据库写入，有逻辑，比如心跳和账单，所以分开写
	@Override
	public boolean business_todb(Meg meg, boolean is_Debug) {

		 ZWLogUtils.info("写入数据库>>>>>>meg="+meg);
		 ZWLogUtils.info("写入数据库>>>>>>meg.bytesToMap()="+meg.bytesToMap());
		 if(is_Debug){
	    	//zwServiceImpl.addBody0x21(meg.bytesToMap());
	    	return true;
		 }
		return true;
	}

}
