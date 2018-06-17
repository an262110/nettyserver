
package com.hc.netty.server;

import com.hc.netty.handler.AppBusinessHKHandler;
import com.hc.netty.handler.HKDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


/**
 * Created by 
 */
public class HcPreBKEServer {
    private int port;
    public HcPreBKEServer(int port) throws InterruptedException {
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
                //p.addLast("idleStateHandler", new IdleStateHandler(40,40, 40, TimeUnit.SECONDS));
                p.addLast(new HKDecoder());
                p.addLast(new AppBusinessHKHandler());
            }
        });
        ChannelFuture f= bootstrap.bind(port).sync();
        if(f.isSuccess()){
            System.out.println("server start---------------"+port);
        }
    }
    public static void main(String []args) throws InterruptedException {
    	
        
    	HcPreBKEServer IdnPreHKServer=new HcPreBKEServer(8011);

    }
}
