package com.yao;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by 
 */
public class NettyServerBootstrapHc {
    private int port;
    private SocketChannel socketChannel;
    public NettyServerBootstrapHc(int port) throws InterruptedException {
        this.port = port;
        bind();
    }

    private void bind() throws InterruptedException {
        EventLoopGroup boss=new NioEventLoopGroup();
        EventLoopGroup worker=new NioEventLoopGroup();
        ServerBootstrap bootstrap=new ServerBootstrap();
        bootstrap.group(boss,worker);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 128);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();
                //p.addLast(new ObjectEncoder());
                //p.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                p.addLast(new NettyServerHandlerHc());
            }
        });
        ChannelFuture f= bootstrap.bind(port).sync();
        if(f.isSuccess()){
            System.out.println("server start---------------"+port);
        }
    }
    public static void main(String []args) throws InterruptedException {
    	
    	//NettyServerBootstrap_liu bootstrap2=new NettyServerBootstrap_liu(8845);
        
    	NettyServerBootstrapHc bootstrap=new NettyServerBootstrapHc(9999);
//        while (true){
//            SocketChannel channel=(SocketChannel)NettyChannelMap.get("001");
//            if(channel!=null){
//                AskMsg askMsg=new AskMsg();
//                channel.writeAndFlush(askMsg);
//            }
//            TimeUnit.SECONDS.sleep(10);
//        }
    }
}
