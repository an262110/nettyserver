package com.hc.app.action.zw;

import com.hc.app.model.*;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ZWService;
import com.hc.common.utils.hk.ZWLogUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 充电桩主动停止充电
 * @author jone
 *
 */
@Component("ZW0x58")
public class ZW0x58 implements ZWActionI {
	@Autowired
	private ZWService zwServiceImpl;
	@Autowired
	private ChargeOrderService chargeOrderService;

	@Override
	public BodyI receive_or_send(ChannelHandlerContext ctx, Head head, byte[] msg, boolean is_Debug) {
		Body0x58 body=new Body0x58(msg);
		if(is_Debug){
			head.setHead8_1(new byte[]{0x59});
			head.setHead2_2(new byte[]{0x0b});
			Meg meg = Meg.message(head,new Body0x59(body));
			ctx.writeAndFlush(meg.getSendBuf());
			
			//ctx.writeAndFlush(body.getSendBuf());
			return body;
		}
		Map<String, String> bMap = body.bytesToMap();
        Map<String, String> hMap = head.bytesToMap(); 
        String charge_order_id=bMap.get("body2_10");
        //先判断该订单是否为App订单
        Map orderMap=null;
        try {
			orderMap=zwServiceImpl.queryOrderIfExist(charge_order_id);
		} catch (Exception e) {
			System.out.println("查询订单是否存在异常");
			
		}
        //订单存在
        if(orderMap!=null){
        	Map params = new HashMap();
    		params.put("CHARGE_ORDER_ID", charge_order_id);
    		params.put("ORDER_STATE", "03");
    		params.put("END_CHARGE_TIME","1");
    		ZWLogUtils.info("结束充电，写入数据库>>>>>>="+params);
    		try {
    			chargeOrderService.updateInfo(params);
    		} catch (Exception e) {
    			ZWLogUtils.error("充电桩主动停止充电更改订单状态："+params);
    			return body;
    		}
        	
        }
        
        
		return body;
	}

	@Override
	public boolean business_todb(Meg meg, boolean is_Debug) {
		 ZWLogUtils.info("0x58写入数据库>>>>>>meg.bytesToMap()="+meg.bytesToMap());
		 // if(is_Debug){
		    	zwServiceImpl.addBody0x58(meg.bytesToMap());
		    //	return true;
		   // }
			
			return true;
	}

}
