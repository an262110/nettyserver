package com.hc.app.action.zw;

import com.hc.app.model.*;
import com.hc.app.service.ChargeOrderServiceImpl;
import com.hc.app.service.ZWService;
import com.hc.common.utils.hk.ZWLogUtils;
import com.yao.NettyChannelMap;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 
 *<p>title :返回取消充电请求 (平台回复)</p>
 *<p>Description : </p>
 *<p>Company : 广州爱电牛科技有限公司</p>
 * @date 2017年2月7日
 * @author 小吴
 */
@Component("ZW0x23")
public class ZW0x23 implements ZWActionI {
	@Autowired
	private ZWService zwServiceImpl;
	@Autowired
	private ChargeOrderServiceImpl chargeOrderServiceImpl;
	@Override
	public BodyI receive_or_send(ChannelHandlerContext ctx, Head head, byte[] msg, boolean is_Debug) {
		Body0x23 body = new Body0x23(msg);
		
		if(is_Debug){
			head.setHead8_1(new byte[]{0x12});
			byte b = 0x04;
			Body0x12 body12 = new Body0x12(body.getBody1_1(),body.getBody2_10(),new byte[]{b});
			Meg meg = Meg.message(head,body12);
			NettyChannelMap.get("1001").writeAndFlush(meg.getSendBuf());
			return body;
		}
		
		Map<String, String> bMap = body.bytesToMap();
        Map<String, String> hMap = head.bytesToMap();
        
		String order_id = bMap.get("body2_10");
		String status = bMap.get("body3_1");

		//充电结束状态 1111 0000  成功 发12 停止充电
		if(status.equals("0F")){
			head.setHead8_1(new byte[]{0x12});
			byte b = 0x04;
			Body0x12 body12 = new Body0x12(body.getBody1_1(),body.getBody2_10(),new byte[]{b});
			Meg meg = Meg.message(head,body12);
			NettyChannelMap.get("1001").writeAndFlush(meg.getSendBuf());
		}
		
		return body;
	}

	@Override
	public boolean business_todb(Meg meg, boolean is_Debug) {
		
		ZWLogUtils.info("写入数据库>>>>>>meg="+meg);
		ZWLogUtils.info("写入数据库>>>>>>meg.bytesToMap()="+meg.bytesToMap());
		 if(is_Debug){
	    	//zwServiceImpl.addBody0x23(meg.bytesToMap());
	    	return true;
		 }
		return true;				
	}

}
