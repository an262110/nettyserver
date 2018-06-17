package com.hc.app.action.hk;

import com.hc.app.action.BaseAction;
import com.hc.app.service.ChargeHKService;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ChargePileService;
import com.hc.common.utils.CommonUtil;
import com.hc.common.utils.LogUtils;
import com.hc.common.utils.hk.HKLogUtils;
import com.hc.common.utils.hk.ParsePackage;
import com.yao.NettyChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.jpos.iso.ISOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 终止充电指令
 * @Title:HK6200.java
 * @Package:com.hc.app.action.hk
 * @Description:TODO
 * @author zhifanglong
 * @Date 2016年7月25日 下午1:01:48
 * @Version V1.0
 */
@Component("HK6200")
public class HK6200 implements BaseAction {
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Autowired
	private ChargeHKService chargeHKServiceImpl;
	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		RequestObject ob=(RequestObject)msg;
		
        HKLogUtils.info("操作类型>>>>>>充电终止;交易码==6200");
        HKLogUtils.info("SOURCE>>>>>>source=appServer");
        
        
        String business_no=ob.getOrderId();
        HKLogUtils.info("business_no>>>>>>="+business_no);
        
     /********************构建命令数据**************************/
        
 Map mapOrder = chargeOrderServiceImpl.searchOrderDetail(business_no,"02");
        
        String retClient = "200\r\n";
        if(mapOrder == null){
       	 retClient = "300\r\n"; //订单不存在
       	 System.out.println("[300]订单不存在！");
       	 HKLogUtils.error("[300]订单不存在！");
       	 ByteBuf resp= Unpooled.copiedBuffer(retClient.getBytes());
    	     ctx.writeAndFlush(resp);
   	     //ctx.close();
       	 //return retClient.getBytes();
       	  return null;
        }
        
      //充电桩序列号与枪号
        String  gunCode= mapOrder.get("GUN_CODE").toString();
       
        Map gunInfo=chargePileServiceImpl.findByGunCode(gunCode);
        
        if(gunInfo==null){
        	retClient = "500\r\n"; //充电枪不存在
          	 System.out.println("[500]充电枪不存在！");
          	 HKLogUtils.error("[500]充电枪不存在！");
          	 ByteBuf resp= Unpooled.copiedBuffer(retClient.getBytes());
       	     ctx.writeAndFlush(resp);
      	     //ctx.close();
          	 //return retClient.getBytes();
       	  return null;
        }
        
        //获取用户标识号(停止需要唯一)
        String CHARGE_USERID =mapOrder.get("CHARGE_USERID_9").toString();
       	HKLogUtils.info("充电用户标识号："+CHARGE_USERID);
       
        String pileNo=(String)gunInfo.get("PILE_SERI");
        String gunNo=gunInfo.get("GUN_NO").toString();
        HKLogUtils.info("桩序列号："+pileNo);
        HKLogUtils.info("枪编号："+gunNo);
       
        /********************构建命令数据**************************/
        
        byte[] data=buildData(CHARGE_USERID,pileNo,Integer.valueOf(gunNo));
        //1.添加标准头
        byte[] head_data= ParsePackage.buildHeader(6200,data);
        
        //2.添加下发控制头
        
        byte[] sendData= ParsePackage.buildControlHeader(head_data);
        ByteBuf resp= Unpooled.copiedBuffer(sendData);
        HKLogUtils.info("=================================发送的数据=======================");
        HKLogUtils.info(ISOUtil.hexString(sendData));
        
		Channel sc=NettyChannelMap.get(pileNo);
		
		if(sc!=null){
			LogUtils.info("发送指令的连接通道：====="+sc.toString());
			if(sc.isActive()||sc.isOpen()){
				HKLogUtils.info("活动的连接！");
			}else{
				HKLogUtils.info("不活动的连接！");
			}
			sc.writeAndFlush(resp).addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture arg0) throws Exception {
					// TODO Auto-generated method stub
					if(arg0.isSuccess()){
						HKLogUtils.info("info>>>>>>>>>>>>>发送成功");
						
					}else{
						HKLogUtils.info("info>>>>>>>>>>>>>发送失败");
					}
				}
			});
		}else {
			
        	 HKLogUtils.error("[400]没有连接桩！");
        	 ByteBuf resp_400= Unpooled.copiedBuffer("400\r\n".getBytes());
     	     ctx.writeAndFlush(resp_400);
    	     //ctx.close();
        	 return null;
		}
		
		ByteBuf resp_200= Unpooled.copiedBuffer("200\r\n".getBytes());
	     ctx.writeAndFlush(resp_200);
	    
	     //ctx.close();
        
	   //保存指令数据	
			int messageId= ParsePackage.count;
			Map chargeParams=new HashMap();
			chargeParams.put("MESSAGE_ID",messageId);
			chargeParams.put("MESSAGE_TYPE",6200);
			chargeParams.put("ORDER_ID",business_no);
			chargeParams.put("FACTORY_ID","0001");
			chargeParams.put("PILE_SERI",pileNo);
			chargeParams.put("GUN_NO",gunNo);
			
			chargeHKServiceImpl.save(chargeParams);
		return null;
	}
    
	
	 private byte[] buildData(String orderSeril,String pileNoStr,int gunNo) throws UnsupportedEncodingException{
		 byte[] data = new byte[56];
		 
		 byte[] orderNo=orderSeril.getBytes("ascii");
		 System.arraycopy(orderNo,0,data,0,8);
		 
		 byte[] factoryNo="0001".getBytes("ascii");
		 System.arraycopy(factoryNo,0,data,8,4);
		 
		 byte[] pileNo=pileNoStr.getBytes("ascii");
		 System.arraycopy(pileNo,0,data,12,12);
		 
		 //充电枪编号
		 data[24] =(byte)((gunNo >>> 0) & 0xff);
		 
		 byte[] userId=CommonUtil.buildString("88888888888",20).getBytes("ascii");
		 System.arraycopy(userId,0,data,25,20);
		 
		 byte[] phoneNo="88888888888".getBytes("ascii");
		 System.arraycopy(phoneNo,0,data,45,11);
		 
		 
		 
		 
	    return data;
		
		}
}


