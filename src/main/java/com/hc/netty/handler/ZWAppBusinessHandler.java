package com.hc.netty.handler;

import com.hc.common.utils.hk.HKLogUtils;
import com.hc.common.utils.hk.ZWLogUtils;
import com.yao.NettyChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ZWAppBusinessHandler extends ChannelHandlerAdapter {
   
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
         ZWLogUtils.info("爱电牛接收>>>>>>content="+reqStr);
         
         if(reqStr.equals("idn_chongdian")){
        	byte[] msg1 = "idn_chongdian_zhuangfa".getBytes();
          	ByteBuf msg_ss= Unpooled.copiedBuffer(msg1);
            NettyChannelMap.get("001").writeAndFlush(msg_ss);
         }
        
         
//         byte[] ret = "zw_huifu".getBytes();
//     	
//     	 ByteBuf resp= Unpooled.copiedBuffer(ret); 
//     	 ctx.writeAndFlush(resp);
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
    
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			HKLogUtils.info("IDEL 检测 此时的连接:"+ctx.channel());
			
			//读写超时，关闭连接
			if (event.state() == IdleState.ALL_IDLE||event.state() == IdleState.READER_IDLE||event.state() == IdleState.WRITER_IDLE){
				 HKLogUtils.info("此链接已经很长时间没活动了，需要移除;连接信息:"+ctx.channel());
				 ctx.close();
				 NettyChannelMap.remove((SocketChannel)ctx.channel());
				 
				 HKLogUtils.info("读写超时移除连接！");
				 
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

