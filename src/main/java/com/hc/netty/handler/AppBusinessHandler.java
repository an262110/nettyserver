package com.hc.netty.handler;


import com.hc.app.action.BaseAction;
import com.hc.common.utils.LogUtils;
import com.hc.common.utils.hk.HKLogUtils;
import com.hc.spring.SpringApplicationContext;
import com.yao.NettyChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.jpos.iso.ISOUtil;

import java.lang.reflect.Method;


/**
 * Created by yaozb on 15-4-11.
 */
public class AppBusinessHandler extends ChannelHandlerAdapter {
	private static int count=0;
	private static Channel sc=null;
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		//处理远程主机强迫关闭了一个现有的连接（原因客户端断开）
		System.out.println("处理远程主机强迫关闭了一个现有的连接！");
		cause.printStackTrace();
		NettyChannelMap.remove((SocketChannel)ctx.channel());
		ctx.close();
		
		HKLogUtils.error(cause);
		
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
    
    	 
         ByteBuf buf =(ByteBuf)msg;
         byte[] req=new byte[buf.readableBytes()];
         buf.readBytes(req);
                 
         String txcode=new String(req);//交易码         
                 
        //1.获取交易码
         byte[] txcodeArray=new byte[4];
         System.arraycopy(req,8,txcodeArray,0,4);
         
         txcode=new String(txcodeArray,"ascii");
         System.arraycopy(req,8,txcodeArray,0,4);
         
         String reqStr= ISOUtil.hexString(req);
         LogUtils.info("接收报文>>>>>>txcode="+txcode+";content="+reqStr);
         
        
	    
        //逻辑分发
        String actionName="TX"+txcode;
     	BaseAction baseAction = (BaseAction) SpringApplicationContext.getService(actionName);
     	
     	//通过反射，获取该Action的操作方法	
     	Method method = baseAction.getClass().getMethod("createSendInfo",ChannelHandlerContext.class,Object.class);
     	method.invoke(baseAction,ctx,req);
     	

     }
	

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
    
//	@Override
//	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//		if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
//			IdleStateEvent event = (IdleStateEvent) evt;
//			 LogUtils.info("IDEL 检测。。。");
//			 LogUtils.info("此时的连接:"+ctx.channel());
//			 if (event.state() == IdleState.READER_IDLE)
//				 LogUtils.info("读空闲");
//				else if (event.state() == IdleState.WRITER_IDLE)
//					LogUtils.info("写空闲");
//			 
//			if (event.state() == IdleState.ALL_IDLE){
//				 LogUtils.info("此链接已经很长时间没活动了，需要移除;连接信息:"+ctx.channel());
//				 NettyChannelMap.remove((SocketChannel)ctx.channel());
//				 SocketChannel sc=(SocketChannel)ctx.channel();
//				 ctx.close();
//				 sc.close();
//				 
//				 if(sc.isActive()){
//					 LogUtils.info("是活动的");
//				 }
//				 if(sc.isOpen()){
//					 LogUtils.info("是打开的");
//				 }
//				 LogUtils.info("成功移除");
//			}
//		}
//	}
	
}
