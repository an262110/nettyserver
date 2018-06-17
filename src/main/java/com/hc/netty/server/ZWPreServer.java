
package com.hc.netty.server;

import com.hc.netty.handler.NettyClientHandlerZW;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;


/**
 * Created by liuh
 */
public class ZWPreServer {
    private int port;
    public ZWPreServer(int port) throws InterruptedException {
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
                p.addLast("idleStateHandler", new IdleStateHandler(30,30,30,TimeUnit.SECONDS));
                //p.addLast(new HKDecoder());
                //p.addLast(new ZWAppBusinessHandler());
                p.addLast(new NettyClientHandlerZW());
            }
        });
        ChannelFuture f= bootstrap.bind(port).sync();
        if(f.isSuccess()){
            System.out.println("server start---------------"+port);
        }
    }
    public static void main(String []args) {
    	
        
    	try {
			ZWPreServer zwPreServer=new ZWPreServer(8044);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//智网的ip地址 
    	NettyClientBootstrapZW bootstrap = new NettyClientBootstrapZW(10101,"119.131.117.225");
//    	NettyClientBootstrapZW bootstrap = new NettyClientBootstrapZW(8033,"127.0.0.1");
//    	NettyClientBootstrapZW bootstrap = new NettyClientBootstrapZW(8033,"120.76.77.245");
//    	Meg meg = new Meg(new Head(),new Body0xB0());
//    	NettyClientBootstrapZW bootstrap = null;
//    	bootstrap = new NettyClientBootstrapZW(8033,"127.0.0.1");
//    	 while (true){
//    		 try {
//	 				TimeUnit.SECONDS.sleep(5);
//	 				if(NettyChannelMap.get("1001")==null){            	             	
//	 					if(bootstrap.start()){
//	 						bootstrap.getSocketChannel().writeAndFlush(meg.getSendBuf());	 						
//	 					}
//	 				}
//    		 	} catch (Exception e) {
//					e.printStackTrace();
//					continue;
//				} 
//             
//         }
    	
    	

    }
}
