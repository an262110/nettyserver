package com.netty.pac.server.listener;

import com.hc.netty.handler.HcAppBusinessHandler;
import com.netty.pac.rpc.util.ObjectCodec;
import com.netty.pac.server.adapter.ServerChannelHandlerAdapter;
import com.netty.pac.server.config.NettyServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Netty服务器监听器
 * <p>
 * create by 叶云轩 at 2018/3/3-下午12:21
 * contact by tdg_yyx@foxmail.com
 */
@Component
public class NettyServerListener {
    /**
     * NettyServerListener 日志控制器
     * Create by 叶云轩 at 2018/3/3 下午12:21
     * Concat at tdg_yyx@foxmail.com
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerListener.class);

    /**
     * 创建bootstrap
     */
    private ServerBootstrap serverBootstrap = new ServerBootstrap();
    /**
     * BOSS
     */
    private EventLoopGroup boss = new NioEventLoopGroup();
    /**
     * Worker
     */
    private EventLoopGroup work = new NioEventLoopGroup();
    /**
     * 通道适配器
     */
    @Resource
    private ServerChannelHandlerAdapter channelHandlerAdapter;
    /**
     * NETT服务器配置类
     */
    @Resource
    private NettyServerConfig nettyConfig;



    /**
     * 关闭服务器方法
     */
    @PreDestroy
    public void close() {
        LOGGER.info("关闭服务器....");
        //优雅退出
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }

    /**
     * 开启及服务线程
     */
//    @PostConstruct
    /*public void start() {
        // 从配置文件中(application.yml)获取服务端监听端口号
        int port = nettyConfig.getPort();
        serverBootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO));
        try {
            //设置事件处理
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new LengthFieldBasedFrameDecoder(nettyConfig.getMaxFrameLength()
                            , 0, 2, 0, 2));
                    pipeline.addLast(new LengthFieldPrepender(2));
                    pipeline.addLast(new ObjectCodec());

                    pipeline.addLast(channelHandlerAdapter);
                }
            });
            LOGGER.info("netty服务器在[{}]端口启动监听", port);
            ChannelFuture f = serverBootstrap.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.info("[出现异常] 释放资源");
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }*/

    public void start() throws InterruptedException{
        // 从配置文件中(application.yml)获取服务端监听端口号
        int port = nettyConfig.getPort();
        /*serverBootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO));
        try {
            //设置事件处理
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new LengthFieldBasedFrameDecoder(nettyConfig.getMaxFrameLength()
                            , 0, 2, 0, 2));
                    pipeline.addLast(new LengthFieldPrepender(2));
                    pipeline.addLast(new ObjectCodec());

                    pipeline.addLast(channelHandlerAdapter);
                }
            });
            LOGGER.info("netty服务器在[{}]端口启动监听", port);
            ChannelFuture f = serverBootstrap.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.info("[出现异常] 释放资源");
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }*/




//        EventLoopGroup boss=new NioEventLoopGroup();
//        EventLoopGroup worker=new NioEventLoopGroup();
//        ServerBootstrap bootstrap=new ServerBootstrap();
        serverBootstrap.group(boss,work);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 128); //连接数
        serverBootstrap.option(ChannelOption.TCP_NODELAY, true); //不延迟，消息立即发送
        serverBootstrap.option(ChannelOption.SO_LINGER,0);
        serverBootstrap.childOption(ChannelOption.SO_LINGER,0);
        serverBootstrap.childOption(ChannelOption.SO_RCVBUF,2048);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);//长连接
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline p = socketChannel.pipeline();

                //服务器检测长连接，是否，可读 ，可写  ，可读可写  90s 心跳检测，如果发现有都没有读写状态，
                //服务器会断开长连接，等待充电桩重连
                //什么时候建立长连接？长连接怎么保存
                //答：在服务器和充电桩处于心跳连接中时，保存长连接，如果长连接存在，则获取长连接进行ping - pong
                //   保存长连接 使用ConcurrentHashMap  线程安全，具体，参考之前心跳连接  类 HK5800
                p.addLast("idleStateHandler", new IdleStateHandler(90,90,90, TimeUnit.SECONDS));

                p.addLast(new HcAppBusinessHandler());
            }
        });
        ChannelFuture f= serverBootstrap.bind(port).sync();
        if(f.isSuccess()){
            System.out.println("server start---------------"+port);
        }
    }
}
