package com.hc.netty.server;

import com.hc.common.utils.hk.JTLogUtils;
import com.hc.netty.handler.AppBusinessJTHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HcPreServerJT {
    private int port;

	public HcPreServerJT(int port) {
		this.port = port;
	}
	
	public static void main(String[] args) {
		HcPreServerJT jt = new HcPreServerJT(38878);
		jt.bind();
	}
    
	private void bind(){
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();
		ServerBootstrap bootstarp = new ServerBootstrap();
		bootstarp.group(boss, worker);
		bootstarp.channel(NioServerSocketChannel.class);
		bootstarp.option(ChannelOption.SO_BACKLOG,128);
		bootstarp.option(ChannelOption.TCP_NODELAY, true);
		bootstarp.option(ChannelOption.SO_LINGER,0);
		bootstarp.childOption(ChannelOption.SO_LINGER, 0);
		bootstarp.childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstarp.childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel channel) throws Exception {
				// TODO Auto-generated method stub
				ChannelPipeline channelPipeline = channel.pipeline();
				channelPipeline.addLast(new AppBusinessJTHandler());
			}
		});
		try {
			ChannelFuture future = bootstarp.bind(port).sync();
			 if(future.isSuccess())
		            JTLogUtils.info("server start---------------"+port);
		} catch (InterruptedException e) {
			JTLogUtils.error(e.getMessage());
		}
	}
}
