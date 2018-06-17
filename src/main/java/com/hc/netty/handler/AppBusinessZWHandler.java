package com.hc.netty.handler;

import com.hc.app.model.Head;
import com.hc.common.utils.hk.ZWLogUtils;
import com.yao.NettyChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

public class AppBusinessZWHandler extends ChannelHandlerAdapter {
   
	private final String NORMAL_HEAD="7E68";
	private final String EXTRAL_HEAD="7F79";
	private final String CLIENT_HEAD="7E70";
	private final String S_HEAD="7E99";
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		//处理远程主机强迫关闭了一个现有的连接（原因客户端断开）
		System.out.println("处理远程主机强迫关闭了一个现有的连接！");
		cause.printStackTrace();
		NettyChannelMap.remove((SocketChannel)ctx.channel());
		ctx.close();
		
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		//super.channelInactive(ctx);
		System.out.println("处理连接已经不可用！");
		NettyChannelMap.remove((SocketChannel)ctx.channel());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
    
         ByteBuf buf =(ByteBuf)msg;
         byte[] req=new byte[buf.readableBytes()];
         buf.readBytes(req);
         
//         byte[] header=Arrays.copyOfRange(req,0,2);
//         String headeStr= ISOUtil.hexString(header);
        
//        if(!(NORMAL_HEAD.equals(headeStr)||EXTRAL_HEAD.equals(headeStr)||CLIENT_HEAD.equals(headeStr)||S_HEAD.equals(headeStr))){
//        	 HKLogUtils.error("错误的报文头>>>>>"+headeStr);
//        	 return;
//        }
        
//        String nowHead=NORMAL_HEAD;
//        if(EXTRAL_HEAD.equals(headeStr)) nowHead=EXTRAL_HEAD;
//        else if(CLIENT_HEAD.equals(headeStr)) nowHead=CLIENT_HEAD;
//        else if(S_HEAD.equals(headeStr)) nowHead=S_HEAD;
        
         String reqStr= new String(req);//ISOUtil.hexString(req);
         Head head = new Head(req);
         ZWLogUtils.info("服务器回复报文智网>>>>>>head="+head.bytesToHexString(head.getByte()));
         //服务器保存的连接
         //NettyChannelMap.add("1001",(SocketChannel)ctx.channel());
//         HKLogUtils.info("服务器建立的连接>>>>>>="+NettyChannelMap.get("1001"));
         
         ByteBuf resp= Unpooled.copiedBuffer(req);
     	 ctx.writeAndFlush(resp);
         
         //测试
         if("idn_send".equals(reqStr)){
        	 byte[] ret = "zw_huifu".getBytes();
          	
         	 resp= Unpooled.copiedBuffer(ret);
         	 ctx.writeAndFlush(resp);
         }
         
         if("idn_chongdian_zhuangfa".equals(reqStr)){
        	 byte[] ret = "idn_chongdian_zhuangfa_huifu".getBytes();
           	
         	 resp= Unpooled.copiedBuffer(ret);
         	 ctx.writeAndFlush(resp);
         }
         
//         String[] reqArray=reqStr.split(nowHead);
        
//         for(String res:reqArray){
//        	 if(!"".equals(res)){
//        		 res=nowHead+res;
//        		 //HKLogUtils.info("数据包>>>>>>content="+res); 
//        		 byte[] resByte=ISOUtil.hex2byte(res);
//		         RequestObject request=DomUtil.parseByteToObject(resByte);
//		         if(request!=null){
//			           HKLogUtils.info("交易码>>>>>>>>txcode="+request.getTxcode()); 
//			           HKLogUtils.info(request);
//				      if(!"".equals(request.getTxcode())){
//					        //逻辑分发
//					        String actionName="HK"+request.getTxcode();
//					     	BaseAction baseAction = (BaseAction) SpringApplicationContext.getService(actionName);
//					     	
//					     	baseAction.createSendInfo(ctx, request);
//					     	
//					     	if("20".equals(request.getTxcode())){
//					     		
//					     		HKLogUtils.info("20交易,只处理第一个数据包!"); 
//					     		break;
//					     	}
//					     	
//				     }
//		        }
//        	 }
//         }
     }
	

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
    
//	@Override
//	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//		if (evt instanceof IdleStateEvent) {
//			IdleStateEvent event = (IdleStateEvent) evt;
//			HKLogUtils.info("IDEL 检测 此时的连接:"+ctx.channel());
//			
//			//读写超时，关闭连接
//			if (event.state() == IdleState.ALL_IDLE||event.state() == IdleState.READER_IDLE||event.state() == IdleState.WRITER_IDLE){
//				 HKLogUtils.info("此链接已经很长时间没活动了，需要移除;连接信息:"+ctx.channel());				
//				 ctx.channel().close();
//				 NettyChannelMap.remove((SocketChannel)ctx.channel());				 
//				 
//				 HKLogUtils.info("读写超时移除连接！");
//			}
//			
//		}
		
		//定时扫描关闭连接
//     	BaseAction baseAction = (BaseAction) SpringApplicationContext.getService("HK9900");
//     	
//     	baseAction.createSendInfo(ctx, null);
//		
//		System.out.println("监听事件执行时间:"+Calendar.getInstance().toString());
//	}
	
}

