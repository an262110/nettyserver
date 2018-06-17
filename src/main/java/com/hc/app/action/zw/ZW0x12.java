package com.hc.app.action.zw;

import com.hc.app.model.Body0x12;
import com.hc.app.model.BodyI;
import com.hc.app.model.Head;
import com.hc.app.model.Meg;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ZWService;
import com.hc.common.utils.hk.ZWLogUtils;
import com.yao.NettyChannelMap;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 
 *<p>title :第三方系统下发取消充电请求</p>
 *<p>Description : </p>
 *<p>Company : 广州爱电牛科技有限公司</p>
 * @date 2017年2月7日
 * @author 小吴
 */
@Component("ZW0x12")
public class ZW0x12 implements ZWActionI {
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Autowired
	private ZWService zwServiceImpl;
	
	@Override
	public BodyI receive_or_send(ChannelHandlerContext ctx, Head head, byte[] msg, boolean is_Debug) {
		
		Body0x12 body = new Body0x12(msg);
		if(is_Debug){
			head.setHead8_1(new byte[]{0x12});
			byte b = 0x04;
			Body0x12 body12 = new Body0x12(body.getBody1_1(),body.getBody2_10(),new byte[]{b});
			Meg meg = Meg.message(head,body12);	
			System.out.println("----------------");
			System.out.println(meg.getHexString());
			System.out.println("----------------");
			NettyChannelMap.get("1001").writeAndFlush(meg.getSendBuf());
			return body;
		}
		
		//订单编号
		Map<String, String> bMap = body.bytesToMap();
        Map<String, String> hMap = head.bytesToMap();
		
		String business_no = bMap.get("body2_10");
		Map mapOrder=null;;
		try {
			mapOrder = chargeOrderServiceImpl.searchOrderDetail(business_no,"02");
		} catch (Exception e) {
			ZWLogUtils.info("充电订单不存在 订单号02："+business_no);
  			return body;
		}
		
		 String retClient = "200\r\n";
	        if(mapOrder == null){
		       	 retClient = "300\r\n";	       	
		    	 ctx.writeAndFlush(head.obtainSendBuf(retClient.getBytes()));
		    	 ZWLogUtils.error("[300]订单不存在！");
		       	 return body;
	        }		
		
		//充电桩序列号与枪号
        String  pile_seri = mapOrder.get("charge_pile_seri").toString();
        String gunNo=mapOrder.get("charge_gun").toString();
        
        //-1 智网0 代表1口 1 代表 2口  与库对应
        int gun = Integer.valueOf(gunNo).intValue()-1;
        ZWLogUtils.info("发送给智网订单号："+business_no+" 枪编号："+pile_seri+" 枪口号："+gun);

        //body.setBody1_1_int(gun);
        head.setHead7_8(pile_seri);
        
        //发送给智网数据
        Meg meg = Meg.message(head,body);
		NettyChannelMap.get("1001").writeAndFlush(meg.getSendBuf());
		
		return body;
	}
	@Override
	public boolean business_todb(Meg meg, boolean is_Debug) {
		 ZWLogUtils.info("写入数据库>>>>>>meg="+meg);
		 ZWLogUtils.info("写入数据库>>>>>>meg.bytesToMap()="+meg.bytesToMap());
		    if(is_Debug){
		    	//zwServiceImpl.addBody0x22(meg.bytesToMap());
		    	return true;
		    }
		return true;
	}

	
                                 
}
