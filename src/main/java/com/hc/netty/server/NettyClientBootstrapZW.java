package com.hc.netty.server;

import com.hc.app.model.Body0xB0;
import com.hc.app.model.Head;
import com.hc.app.model.Meg;
import com.hc.netty.handler.NettyClientHandlerZW;
import com.yao.NettyChannelMap;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.TimeUnit;

/**
 * Created by yaozb on 15-4-11.
 */
public class NettyClientBootstrapZW {
    private int port;
    private String host;
    private SocketChannel socketChannel;
    
    public SocketChannel getSocketChannel() {
		return socketChannel;
	}   
    
	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}
    public NettyClientBootstrapZW(int port, String host)  {
        this.port = port;
        this.host = host;
        start();
    }
    public void start() {
    	EventLoopGroup eventLoopGroup=new NioEventLoopGroup();
    	Bootstrap bootstrap=new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
        bootstrap.group(eventLoopGroup);
        bootstrap.remoteAddress(host,port);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
//                socketChannel.pipeline().addLast(new IdleStateHandler(20,10,0));
//                socketChannel.pipeline().addLast(new ObjectEncoder());
//                socketChannel.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                socketChannel.pipeline().addLast(new NettyClientHandlerZW());
            }
        });
        ChannelFuture future = null;
		try {
			future = bootstrap.connect(host,port).sync();
			if (future.isSuccess()) {
	            socketChannel = (SocketChannel)future.channel();
	            System.out.println("connect server  成功---------");
	            Meg meg = Meg.message(new Head(),new Body0xB0());
	            socketChannel.writeAndFlush(meg.getSendBuf());
	        }
			 future.channel().closeFuture().sync(); 
			//return true;
		} catch (Exception e) {
			e.printStackTrace();
//			System.out.println("rong rong");
			eventLoopGroup.shutdownGracefully();
		}finally{
			if (null != future) {  
//	              if (future.channel() != null && future.channel().isOpen()) {  
//	                  future.channel().close(); 
//	                  eventLoopGroup.shutdownGracefully();
//	              }  
	          }
			          
	          if(NettyChannelMap.get("1001")==null){ 
	        	  try {
	  				TimeUnit.SECONDS.sleep(5);
		  		  } catch (InterruptedException e) {
		  			e.printStackTrace();
		  		  }
	        	  System.out.println("准备重连");
	        	  start();
	        	  System.out.println("重连成功");
	          }
	            
		}
		
		//future.
       //return false;
    }
    
	public static void main(String[]args) throws InterruptedException {
//        Constants.setClientId("001");
//        NettyClientBootstrapZW bootstrap=new NettyClientBootstrapZW(9999,"127.0.0.1");
//
//        LoginMsg loginMsg=new LoginMsg();
//        loginMsg.setPassword("yao");
//        loginMsg.setUserName("robin");
//        bootstrap.socketChannel.writeAndFlush(loginMsg);
//        int i=0;
//        while (true){
//            TimeUnit.SECONDS.sleep(3);
//            AskMsg askMsg=new AskMsg();
//            AskParams askParams=new AskParams();
//            askParams.setAuth("authToken");
//            askMsg.setParams(askParams);
//            bootstrap.socketChannel.writeAndFlush(askMsg);
//            i++;
//            System.out.println("client "+i);
//        }
    	
    	NettyClientBootstrapZW bootstrap=new NettyClientBootstrapZW(8044,"127.0.0.1");
    	byte[] ret = "idn_chongdian".getBytes();
    	
    	ByteBuf resp= Unpooled.copiedBuffer(ret);
    	bootstrap.socketChannel.writeAndFlush(resp);
    	//bootstrap.socketChannel.close();
    }
}
