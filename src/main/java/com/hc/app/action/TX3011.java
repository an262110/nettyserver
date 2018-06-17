package com.hc.app.action;

import com.hc.app.service.ChargeOrderService;
import com.hc.common.utils.CommonUtil;
import io.netty.channel.ChannelHandlerContext;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("TX3011")
public class TX3011 implements BaseAction {
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		byte[] req=(byte[])msg;
		ISOMsg sendMsg= CommonUtil.parsePackage(req);

		//更新充电表
		String charge_userid = sendMsg.getString(9); //用户标识
		Map mapCharge = chargeOrderServiceImpl.searchDnChargeDetail(charge_userid);
		String CHARGE_BUSINESSNO_11 =mapCharge.get("CHARGE_BUSINESSNO_11").toString();
		//（00：成功；01：MAC校验失败，02：充电桩序列号不对应，03：未插枪，04：充电桩已被使用，无法充电，
		// 05：非当前用户，无法取消充电, 06：充电桩故障无法充电,07:充电桩忙, 08:余额不足，09:车辆未准备好
		String CHARGE_RET_39 = sendMsg.getString(39).toString();
		if(!CHARGE_RET_39.equals("00")){
			System.out.println("充电桩返回错误 错误码为："+CHARGE_RET_39);
			Map paramMap = new HashMap();
			//paramMap.put("ORDER_STATE", "03");
			paramMap.put("CHARGE_RET_39", CHARGE_RET_39);
			paramMap.put("CHARGE_RET_39_DESC", CommonUtil.infoCodeDesc(CHARGE_RET_39));
			paramMap.put("CHARGE_ORDER_ID", CHARGE_BUSINESSNO_11);
			chargeOrderServiceImpl.updateInfo(paramMap);
			return req;
		}
		//01 充电 02 停止 实时更新订单状态
		Map paramMap = new HashMap();
		if("02".equals(mapCharge.get("CHARGE_TYPE_4"))){
			//订单状态更改为 03 充电结束
			paramMap.put("ORDER_STATE", "03");
			paramMap.put("END_CHARGE_TIME", "1");
		}else {
			paramMap.put("START_CHARGE_TIME", "1");
			paramMap.put("ORDER_STATE", "02");
		}
		paramMap.put("CHARGE_ORDER_ID", CHARGE_BUSINESSNO_11);
		chargeOrderServiceImpl.updateInfo(paramMap);
		chargeOrderServiceImpl.updateDnCharge(sendMsg);
		return req;
	}

}
