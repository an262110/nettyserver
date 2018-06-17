package com.hc.app.action.zw;

import com.hc.app.model.*;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ChargePileService;
import com.hc.app.service.ZWService;
import com.hc.common.utils.hk.ZWLogUtils;
import com.yao.NettyChannelMap;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 下发充电请求
 * @author zc
 *
 */

@Component("ZW0x20")
public class ZW0x20 extends MegUtil implements ZWActionI {
	@Autowired
	private ZWService zwServiceImpl;
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Override
	public BodyI receive_or_send(ChannelHandlerContext ctx, Head head,
                                 byte[] msg, boolean is_Debug) {
		Body0x20 body = new Body0x20(msg);
		
		if(is_Debug){ // 为true 调试报文用
			Meg meg = Meg.message(head,body);
			System.out.println("发送20:"+meg.getHexString());
			System.out.println(NettyChannelMap.get("1001").toString());
			NettyChannelMap.get("1001").writeAndFlush(meg.getSendBuf());
			return body;
		}       
        
		Map<String, String> bMap = body.bytesToMap();
        Map<String, String> hMap = head.bytesToMap();
		
        //订单号  充电桩编码
        String business_no=bMap.get("body2_10");
        //String pileNo = hMap.get("head7_8");
        
        
        /********************构建命令数据**************************/
        Map mapOrder=null;
		try {
			mapOrder = chargeOrderServiceImpl.searchOrderDetail(business_no,"01");
		} catch (Exception e) {
			ZWLogUtils.info("充电订单不存在 订单号01："+business_no);
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

        body.setBody1_1_from_int(gun);
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
	    	//zwServiceImpl.addBody0x20(meg.bytesToMap());
	    	return true;
	    }
		
		return true;
	}
	
}
