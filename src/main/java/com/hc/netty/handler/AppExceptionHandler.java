package com.hc.netty.handler;

import com.hc.common.utils.LogUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 异常处理
 * 
 * @author Zed
 *
 */
public class AppExceptionHandler extends ChannelInboundHandlerAdapter {
      
    @Override  
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
    	   /*
            Map paramMap =(Map)msg; 
    		Map resDataMap = new HashMap();
    	    resDataMap.put("return_code", "9999");
    		resDataMap.put("return_msg", "接口编号没有上传");
    		*/
    		
    	    LogUtils.info("JJSExceptionHandler...");
    		
    		//返回数据
    		ctx.write(msg);  

        	// 通知执行下一个InboundHandler  
            // ctx.fireChannelRead(msg); 	
    }  
  
    @Override  
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();  
    }  
    
}  
