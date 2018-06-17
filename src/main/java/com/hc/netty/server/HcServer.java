
package com.hc.netty.server;

import com.hc.netty.handler.HcAppBusinessHandler;
import com.hc.spring.SpringApplicationContext;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;


/**
 * Created by 刘慧平
 * 充电桩入口类，
 * 以及技术说明
 */
public class HcServer {
    private int port;
    public HcServer(int port) throws InterruptedException {
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
        bootstrap.childOption(ChannelOption.SO_RCVBUF,2048);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);//长连接
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();

                //服务器检测长连接，是否，可读 ，可写  ，可读可写  90s 心跳检测，如果发现有都没有读写状态，
                //服务器会断开长连接，等待充电桩重连
                //什么时候建立长连接？长连接怎么保存
                //答：在服务器和充电桩处于心跳连接中时，保存长连接，如果长连接存在，则获取长连接进行ping - pong
                //   保存长连接 使用ConcurrentHashMap  线程安全，具体，参考之前心跳连接  类 HK5800 
                p.addLast("idleStateHandler", new IdleStateHandler(90,90,90,TimeUnit.SECONDS));

                p.addLast(new HcAppBusinessHandler());
            }
        });
        ChannelFuture f= bootstrap.bind(port).sync();
        if(f.isSuccess()){
            System.out.println("server start---------------"+port);
        }
    }
    public static void main(String []args) throws InterruptedException {
        SpringApplicationContext.init();

    	HcServer IdnPreHKServer = new HcServer(2015);

    }
}
