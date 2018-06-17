package com.hc.app.action.hk;

import com.hc.app.action.BaseAction;
import com.hc.app.service.TariffPolicyService;
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

@Component("HK6500")
public class HK6500 implements BaseAction{
    
	@Autowired
	private TariffPolicyService tariffPolicyServiceImpl;
	
	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		RequestObject ob=(RequestObject)msg;
        HKLogUtils.info("操作类型>>>>>>远程费率设置;交易码==6500");
        HKLogUtils.info("SOURCE>>>>>>source=appServer");
        
        /********************构建命令数据**************************/
        
        byte[] data=buildData();
        
        //1.添加标准头
        byte[] head_data=ParsePackage.buildHeader(6500,data);
        
        //2.添加下发控制头
        
        byte[] sendData=ParsePackage.buildControlHeader(head_data);
        ByteBuf resp= Unpooled.copiedBuffer(sendData);
        HKLogUtils.info("=================================发送的数据=======================");
        HKLogUtils.info(ISOUtil.hexString(sendData));
        
		Channel sc=NettyChannelMap.get("SXTJ_AC10010");
		LogUtils.info("发送指令的连接通道：====="+sc.toString());
		
		if(sc!=null){
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
						HKLogUtils.info("error is:"+arg0.cause());
					}
				}
			});
		}else {
			
        	 HKLogUtils.error("[400]没有连接桩！");
        	 ByteBuf resp_400= Unpooled.copiedBuffer("400".getBytes());
     	     ctx.writeAndFlush(resp_400);
    	     ctx.close();
        	 return null;
		}
		
		ByteBuf resp_200= Unpooled.copiedBuffer("200".getBytes());
	     ctx.writeAndFlush(resp_200);
	     ctx.close();
		return null;
	}
    
	
	 private byte[] buildData() throws UnsupportedEncodingException{
		 byte[] data = new byte[65];
		 
		 byte[] factoryNo="0001".getBytes("ascii");
		 System.arraycopy(factoryNo,0,data,0,4);
		 
		 byte[] pileNo="SXTJ_AC10010".getBytes("ascii");
		 System.arraycopy(pileNo,0,data,4,12);
		 
		 data[16] = (byte)((48 >>> 0) & 0xff);
		 
		 for(int i=0;i<48;i++){
			 data[17+i] = (byte)((135 >>> 0) & 0xff);
		 }
	    return data;
		
		}
}
