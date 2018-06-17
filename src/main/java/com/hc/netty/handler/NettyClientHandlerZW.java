package com.hc.netty.handler;

import com.hc.app.action.zw.ZWActionI;
import com.hc.app.model.BodyI;
import com.hc.app.model.Head;
import com.hc.app.model.Meg;
import com.hc.common.utils.hk.ZWLogUtils;
import com.hc.spring.SpringApplicationContext;
import com.yao.NettyChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.jpos.iso.ISOUtil;

public class NettyClientHandlerZW extends ChannelHandlerAdapter {
   
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		//处理远程主机强迫关闭了一个现有的连接（原因客户端断开）
		System.out.println("处理远程主机强迫关闭了一个现有的连接！");
		cause.printStackTrace();
		NettyChannelMap.remove((SocketChannel)ctx.channel());
		ctx.close();
		
		ZWLogUtils.error(cause);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("处理连接已经不可用！");
		NettyChannelMap.remove((SocketChannel)ctx.channel());
		ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
    
         ByteBuf buf =(ByteBuf)msg;
         byte[] req=new byte[buf.readableBytes()];
         buf.readBytes(req); 
         
         String reqStr= ISOUtil.hexString(req);
         ZWLogUtils.info("接收报文>>>>>>content="+reqStr);
         
         Head head = new Head(req);
         //head.writeBytesToFile(req, 1);//智网报文存储
         ZWLogUtils.info("智网回复报文>>>>>>head="+head.getHead_hexstr());

         String actionName="ZW0x"+head.getHead8_1_hexstr();
         ZWActionI zwActionI = (ZWActionI) SpringApplicationContext.getService(actionName);
         
        // boolean is_Debug = true; //true 报文调试  false 整合appserver
         boolean is_Debug = false; //true 报文调试  false 整合appserver
         String  is_suc   = head.getHead5_3_hexstr(); //报文头是否成功
         BodyI body = null;
         if("000000".equals(is_suc)){
        	 body = zwActionI.receive_or_send(ctx, head, req,is_Debug);
        	 ZWLogUtils.info("报文头错误码参考dn_succ("+is_suc+") 报文接口号"+head.getHead8_1_hexstr());
         }else{
        	 ZWLogUtils.info("报文头错误码参考dn_error("+is_suc+") 报文接口号"+head.getHead8_1_hexstr());
        	 return;
         }
         Meg meg = Meg.message(head, body); 
         meg.writeBytesToFile(req, 1);//智网报文存储
         boolean is_true = zwActionI.business_todb(meg,is_Debug);
         
         if(is_true){
        	 
         }
         
         if(meg!=null){
        	 meg=null;
         }
         if(head!=null){
        	 head=null;
         }
         if(body!=null){
        	 body=null;
         }

     }
	

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
    
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			ZWLogUtils.info("IDEL 检测 此时的连接:"+ctx.channel());
			
			//读写超时，关闭连接
			if (event.state() == IdleState.ALL_IDLE||event.state() == IdleState.READER_IDLE||event.state() == IdleState.WRITER_IDLE){
				ZWLogUtils.info("此链接已经很长时间没活动了，需要移除;连接信息:"+ctx.channel());				
				 ctx.close();
				 NettyChannelMap.remove((SocketChannel)ctx.channel());
				 
				 ZWLogUtils.info("读写超时移除连接！");
				 
//				NettyClientBootstrapZW bootstrap=new NettyClientBootstrapZW(8033,"127.0.0.1");
//		    	byte[] ret = "idn_send".getBytes();
//		    	
//		    	ByteBuf resp= Unpooled.copiedBuffer(ret);  	
//		    	bootstrap.getSocketChannel().writeAndFlush(resp);
//		    	HKLogUtils.info("重新连接智网！");
			}
			
		}
		
		//定时扫描关闭连接
//     	BaseAction baseAction = (BaseAction) SpringApplicationContext.getService("HK9900");
//     	
//     	baseAction.createSendInfo(ctx, null);
//		
//		System.out.println("监听事件执行时间:"+Calendar.getInstance().toString());
	}
	
}

