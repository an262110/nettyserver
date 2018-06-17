package com.hc.app.action.zw;

import com.hc.app.model.Body0x13;
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
 * 
 *<p>title :平台停止指令返回</p>
 *<p>Description : </p>
 *<p>Company : 广州爱电牛科技有限公司</p>
 * @date 2017年2月7日
 * @author 小吴
 */
@Component("ZW0x13")
public class ZW0x13 implements ZWActionI {
	@Autowired
	private ZWService zwServiceImpl;
    @Autowired
	private ChargeOrderService chargeOrderService;
	@Override
	public BodyI receive_or_send(ChannelHandlerContext ctx, Head head, byte[] msg, boolean is_Debug) {
		Body0x13 body = new Body0x13(msg);
		if(is_Debug){
			return body;
		}
		
		Map<String, String> bodyMap = body.bytesToMap();
		
		String charge_order_id = bodyMap.get("body2_10");
		
		Map params = new HashMap();
		params.put("CHARGE_ORDER_ID", charge_order_id);
		params.put("ORDER_STATE", "03");
		params.put("END_CHARGE_TIME","1");
		ZWLogUtils.info("结束充电，写入数据库>>>>>>="+params);
		try {
			chargeOrderService.updateInfo(params);
		} catch (Exception e) {
			ZWLogUtils.error("充电结束13更改订单状态："+params);
			return body;
		}
						
		return body;
	}

	@Override
	public boolean business_todb(Meg meg, boolean is_Debug) {
		ZWLogUtils.info("0x13写入数据库>>>>>>meg.bytesToMap()="+meg.bytesToMap());
		if(is_Debug){
			//zwServiceImpl.addBody0x13(meg.bytesToMap());
		    return true;
		}
		
		return true;
	}

}
