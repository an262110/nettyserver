package com.hc.netty.handler;


import com.hc.common.utils.LogUtils;
import com.hc.common.utils.hk.ParseUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.jpos.iso.ISOUtil;

import java.util.Arrays;


/**
 * Created by yaozb on 15-4-11.
 */
public class AppBusinessHandler_gy extends ChannelHandlerAdapter {
	private static int count=0;
	private static Channel sc=null;
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		//处理远程主机强迫关闭了一个现有的连接（原因客户端断开）
		System.out.println("处理远程主机强迫关闭了一个现有的连接！");
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
       	 
         ByteBuf buf =(ByteBuf)msg;
         byte[] receive=new byte[buf.readableBytes()];
         buf.readBytes(receive);
                  
         String reqStr= ISOUtil.hexString(receive);
         LogUtils.info("接收报文>>>>>>;content="+reqStr);
         
         
         String head1 = ParseUtil.bytesToHexString(Arrays.copyOfRange(receive,
					0, 1));// 包头1
         String head2 = ParseUtil.bytesToHexString(Arrays.copyOfRange(receive,
					1, 2));// 包头2
         
        // if()
         

     }
	

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	
}
