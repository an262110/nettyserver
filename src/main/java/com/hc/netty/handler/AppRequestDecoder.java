package com.hc.netty.handler;

import com.alibaba.fastjson.JSON;
import com.hc.app.config.RetMsgContants;
import com.hc.common.config.AppConfig;
import com.hc.common.security.Base64Utils;
import com.hc.common.utils.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理客户端上传的数据
 * 
 * @author Zed
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked"})
public class AppRequestDecoder extends ChannelInboundHandlerAdapter {
	
	/**
	 * Web访问路径
	 */
	private static String mobiUrl = AppConfig.getMessage("server.mobi.url");
	  
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {

		FullHttpRequest request = (FullHttpRequest) msg;
		
		//判断request请求是否是post请求
//        if (request.getMethod().equals(HttpMethod.POST)) {
//        }

		String uri = request.getUri();
		LogUtils.info("preServer--客户端访问uri:" + uri);
		
	    if(uri.startsWith(mobiUrl)){
	    	
			ByteBuf buf = request.content();

			String httpContent = buf.toString(io.netty.util.CharsetUtil.UTF_8);
			//LogUtils.info("客户端上送的数据:" + httpContent);

			//String decodeDataStr=httpContent;
			String decodeDataStr = Base64Utils.decode(httpContent);
			//LogUtils.info("preServer--客户端上送的数据(解密后):" + decodeDataStr);

			// 将json字符串解析成Map，进行传递
			Map paramMap = (Map) JSON.parse(decodeDataStr);
			buf.release();
			
			//日志处理  lrw  add  20150929  begin
			Map logMap  =  new HashMap();
			logMap.putAll(paramMap);
			logMap.remove("password");//交易密码
			logMap.remove("track_two_data");//二磁道
			logMap.remove("terminal_key");//终端秘钥			
			LogUtils.info("preServer--客户端上送的数据(解密后):" + logMap.toString());
			//日志处理  lrw  add  20150929  end

			// 传给下一个handler
			ctx.fireChannelRead(paramMap);
			
		}else{
        	Map resDataMap = new HashMap();
        	resDataMap.put("return_code", RetMsgContants.Code0002);
        	resDataMap.put("return_msg", RetMsgContants.CodeMsg0002);//非法访问路径
        	ctx.write(resDataMap);  
        	
        	// 传给下一个handler
        	//ctx.fireChannelRead(msg);
		}

	}
  
}  