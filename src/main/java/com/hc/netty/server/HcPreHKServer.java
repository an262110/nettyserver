
package com.hc.netty.server;

import com.hc.netty.handler.AppBusinessHKHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;


/**
 * Created by 
 */
public class HcPreHKServer {
    private int port;
    public HcPreHKServer(int port) throws InterruptedException {
        this.port = port;
        bind();
    }

    private void bind() throws InterruptedException {
        EventLoopGroup boss=new NioEventLoopGroup();
        EventLoopGroup worker=new NioEventLoopGroup();
        ServerBootstrap bootstrap=new ServerBootstrap();
        bootstrap.group(boss,worker);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 128); //连接数
        bootstrap.option(ChannelOption.TCP_NODELAY, true); //不延迟，消息立即发送
        bootstrap.option(ChannelOption.SO_LINGER,0);
        bootstrap.childOption(ChannelOption.SO_LINGER,0);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);//长连接
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();

                p.addLast("idleStateHandler", new IdleStateHandler(90,90,90,TimeUnit.SECONDS));

//                p.addLast(new HKDecoder());
                p.addLast(new AppBusinessHKHandler());
            }
        });
        ChannelFuture f= bootstrap.bind(port).sync();
        if(f.isSuccess()){
            System.out.println("server start---------------"+port);
        }
    }
    public static void main(String []args) throws InterruptedException {
    	
        
    	HcPreHKServer IdnPreHKServer=new HcPreHKServer(8066);

    }
}
