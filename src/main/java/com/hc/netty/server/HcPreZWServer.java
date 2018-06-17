
package com.hc.netty.server;

import com.hc.netty.handler.AppBusinessZWHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


/**
 * Created by lhui
 */
public class HcPreZWServer {
    private int port;
    public HcPreZWServer(int port) throws InterruptedException {
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
        bootstrap.option(ChannelOption.SO_LINGER,0);
        bootstrap.childOption(ChannelOption.SO_LINGER,0);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();
                //p.addLast("idleStateHandler", new IdleStateHandler(150,150,150,TimeUnit.SECONDS));
                //p.addLast(new HKDecoder());
                p.addLast(new AppBusinessZWHandler());
            }
        });
        ChannelFuture f= bootstrap.bind(port).sync();
        if(f.isSuccess()){
            System.out.println("server start---------------"+port);
        }
    }
    public static void main(String []args) throws InterruptedException {
    	
        
    	HcPreZWServer IdnPreZWServer=new HcPreZWServer(8033);

    }
}
