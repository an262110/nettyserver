package com.hc.netty.handler;

import com.hc.app.config.RetMsgContants;
import com.hc.common.config.AppConfig;
import com.hc.common.utils.LogUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 过滤器处理
 * 
 * @author Zed
 *
 */
public class AppFilterHandler extends ChannelInboundHandlerAdapter {
 
    @Override  
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
	    FullHttpRequest request = (FullHttpRequest) msg;

	    //校验客户端IP
		String clientIP = request.headers().get("X-Forwarded-For");
		if (clientIP == null) {
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
			clientIP = insocket.getAddress().getHostAddress();
		}
		LogUtils.info("访问的客户端IP:" + clientIP);
		
		if(!allowClient(clientIP)){
			// 错误客户端访问
			Map resDataMap = new HashMap();
			resDataMap.put("return_code", RetMsgContants.Code0001);
			resDataMap.put("return_msg", RetMsgContants.CodeMsg0001);
        	ctx.write(resDataMap);  
        	
		}else{
        	// 通知执行下一个InboundHandler  
            ctx.fireChannelRead(msg); 	
        }  
        
    }  
  
    @Override  
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();  
    }  
    
    
    /**
     * 允许进行访问的客户端集合
     */
    private static List allowClientList;
    
    /**
     * 检查访问客户端IP
     * 
     * @param req_code
     * @return
     * @throws Exception
     */
    private boolean allowClient(String clientIP) throws Exception {
    	
    	if(null == allowClientList){
    		allowClientList = new ArrayList();
    		String allowClientIPStr = AppConfig.getMessage("allow.ip");
    		String ipStr[] = allowClientIPStr.split(";");
    		for(int i=0; i<ipStr.length;i++){
    			allowClientList.add(ipStr[i]);
    		}    		
    	}
    	
    	if(allowClientList.contains(clientIP)){
    		return true;
    	}
    
    	return false;
    }
    
}  
