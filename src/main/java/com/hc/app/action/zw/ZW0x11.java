package com.hc.app.action.zw;

import com.hc.app.model.Body0x11;
import com.hc.app.model.BodyI;
import com.hc.app.model.Head;
import com.hc.app.model.Meg;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ZWService;
import com.hc.common.utils.hk.ZWLogUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 开启充电指令:平台返回
 * @author zc
 *
 */

@Component("ZW0x11")
public class ZW0x11 implements ZWActionI {
	@Autowired
	private ZWService zwServiceImpl;
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;

	@Override
	public BodyI receive_or_send(ChannelHandlerContext ctx, Head head,
								 byte[] msg, boolean is_Debug) {
		Body0x11 body = new Body0x11(msg);
		if(is_Debug){ // 为true 调试报文用
			//不需要处理
			//NettyChannelMap.get("1001").writeAndFlush(head.getSendBuf());
			return body;
		}
		Map<String, String> bodyMap = body.bytesToMap();

		String order_id = (String) bodyMap.get("body5_10");
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("CHARGE_ORDER_ID", order_id);
		paraMap.put("START_CHARGE_TIME", "1");
		paraMap.put("ORDER_STATE", "02");

		String charge_gun=bodyMap.get("body1_1");
		if("00".equals(charge_gun)){
			charge_gun="1";
		}else if("01".equals(charge_gun)){
			charge_gun="2";
		}
		paraMap.put("CHARGE_GUN", charge_gun);
		try {
			chargeOrderServiceImpl.updateInfo(paraMap);
		} catch (Exception e) {
			ZWLogUtils.info("充电启动返回11更新失败："+paraMap);
			return body;
		}

		return body;
	}

	@Override
	public boolean business_todb(Meg meg, boolean is_Debug) {

		ZWLogUtils.info("写入数据库>>>>>>meg="+meg);
		ZWLogUtils.info("0x11写入数据库>>>>>>meg.bytesToMap()="+meg.bytesToMap());
		if(is_Debug){
			//	zwServiceImpl.addBody0x11(meg.bytesToMap());


			return true;
		}

		return true;
	}

}
