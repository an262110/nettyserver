package com.yao;


import com.hc.common.utils.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by yaozb on 15-4-11.
 */
public class NettyServerHandlerHc extends ChannelHandlerAdapter {
	public static ByteBuf replyMsg;
	public static ByteBuf replyMsg_a;
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		//处理远程主机强迫关闭了一个现有的连接（原因客户端断开）
		System.out.println("处理远程主机强迫关闭了一个现有的连接！");
		//cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		ByteBuf buf =(ByteBuf)msg;
        byte[] req=new byte[buf.readableBytes()];
        buf.readBytes(req);
        String reqStr=parseByte2HexStr(req);
        LogUtils.info("server>>>>>>>"+reqStr);
        
        if("31".equals(reqStr)){
	       	 NettyChannelMap.add("101", (SocketChannel)ctx.channel());
	       	 System.out.println("client1 登录成功");
        }else if("32".equals(reqStr)){
        	 byte[] reqqqq="kkkk".getBytes();
	       	 ByteBuf resp= Unpooled.copiedBuffer(reqqqq);
	       	 replyMsg = Unpooled.buffer(reqqqq.length);
	       	 replyMsg.writeBytes(resp);
	       	 if(NettyChannelMap.get("101")!=null){
	       		 NettyChannelMap.get("101").writeAndFlush(replyMsg).addListener(new ChannelFutureListener(){

					@Override
					public void operationComplete(ChannelFuture future)
							throws Exception {
					if(!future.isSuccess()){
        					//
        					System.out.println("app发送pre到桩失败");
        				}else {
        					System.out.println("app发送pre到桩成功");
        				}
						
					}
	       			 
	       		 });
	       		 
	       	//给当前客户端发送消息
	        	byte[] ret="liu".getBytes();
	 	       	 ByteBuf retq= Unpooled.copiedBuffer(ret);
	 	       	replyMsg_a = Unpooled.buffer(ret.length);
	 	       replyMsg_a.writeBytes(retq);
	        		ctx.channel().writeAndFlush(replyMsg_a).addListener(new ChannelFutureListener(){

						@Override
						public void operationComplete(ChannelFuture future)
								throws Exception {
						if(!future.isSuccess()){
	        					//
	        					System.out.println("pre发送app返回失败");
	        				}else {
	        					System.out.println("pre发送app返回成功");
	        				}
							
						}
		       			 
		       		 });
	        		ctx.close();
	       		 
	       	 }else{
	       		System.out.println("充电桩：101 未连接");
	       	 }
	       	 
	       

        }else{
        	 
        	System.out.println("未知："+reqStr);
        	
        }
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	 private static String parseByte2HexStr(byte buf[]) {  
	        StringBuffer sb = new StringBuffer();  
	        for (int i = 0; i < buf.length; i++) {  
	                String hex = Integer.toHexString(buf[i] & 0xFF);  
	                if (hex.length() == 1) {  
	                        hex = '0' + hex;  
	                }  
	                sb.append(hex.toUpperCase());  
	        }  
	        return sb.toString();  
	} 
}
