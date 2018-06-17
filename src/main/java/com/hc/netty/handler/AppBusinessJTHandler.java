package com.hc.netty.handler;

import com.hc.app.action.jt.JTActionI;
import com.hc.app.model.jt.JTHead;
import com.hc.common.utils.hk.JTLogUtils;
import com.hc.spring.SpringApplicationContext;
import com.yao.NettyChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.jpos.iso.ISOUtil;

public class AppBusinessJTHandler extends ChannelHandlerAdapter {

	
	/*@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("----------------channelRegistered");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("-----------------channelInactive");
		SocketChannel channel = (SocketChannel) ctx.channel();
		NettyChannelMap.remove(channel);
	}
	

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelActive-----");
	}

	@Override
	public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress,
			ChannelPromise promise) throws Exception {
		System.out.println("------------------connect");
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		System.out.println("write----------------------------------");
		super.write(ctx, msg, promise);
	}

	@Override
	public void flush(ChannelHandlerContext ctx) throws Exception {
		System.out.println("flush-----------------");
	}
	 */

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		SocketChannel sc = (SocketChannel) ctx.channel();
		cause.printStackTrace();
		JTLogUtils.error("处理远程主机强迫关闭了一个现有的连接..............");
		NettyChannelMap.remove(sc);
		ctx.close();
		JTLogUtils.error(cause.getMessage());
	}


	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg;
		byte[] buf = new byte[byteBuf.readableBytes()]; 
		byteBuf.readBytes(buf);
		JTHead head = new JTHead(buf);
		if(!head.getResult()){
			JTLogUtils.error("crc校验码不正确");
			return ; //crc验证不通过 不解析
		}
		String str = ISOUtil.hexString(buf);
		String code = head.getCode();
		if(str!=null){
			JTLogUtils.info("操作码>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+code);
			JTLogUtils.info("全部的报文>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+str);
		}
		//启动解析接口bean
		if(code!=null && !code.isEmpty()){
			String actionName="JT0x"+code.toLowerCase();
			JTLogUtils.info("进入执行了，准备启动SpringBean："+actionName);
			JTActionI action = (JTActionI) SpringApplicationContext.getService(actionName);
			action.receive_send_data(ctx, buf, true);
		}

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	

}
