package com.hc.netty.handler;

import com.alibaba.fastjson.JSON;
import com.hc.common.security.Base64Utils;
import com.hc.common.utils.LogUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Values;

import java.util.*;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * 处理返回给客户端的数据
 * 
 * @author Zed
 *
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class AppResponseEncoder extends ChannelOutboundHandlerAdapter {
	  

	@Override  
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

    	Map resDataMap = (Map) msg;  
        
    	resDataMap = toLowerKey(resDataMap);//将大写key转成小写 --- add by Zed 20150617
    	
        String resDataJson = JSON.toJSONString(resDataMap);
        //resDataJson = resDataJson.toLowerCase();//将大写key转成小写
        //LogUtils.info("preServer--返回给客户端的数据:"+resDataJson);
        //日志处理  add  lrw  20150929 begin
        Map logMap  =  new HashMap();
		logMap.putAll(resDataMap);
		logMap.remove("terminal_key");//终端秘钥			
		LogUtils.info("preServer--客户端上送的数据(解密后):" + logMap.toString());
        //日志处理  add  lrw  20150929 end
        
        resDataJson = Base64Utils.encodedSafe(resDataJson);
        //LogUtils.info("返回给客户端的数据(加密后):"+resDataJson);
		
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(resDataJson.getBytes("utf-8")));
        //response.headers().set(CONTENT_TYPE, "text/plain;charset=utf-8");  
        response.headers().set(CONTENT_TYPE, "text/json;charset=utf-8");  
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());  
        response.headers().set(CONNECTION, Values.KEEP_ALIVE);
        ctx.write(response);  
        ctx.flush();  
    }  
    
    /**
     * 将Map中大写的key转成小写 --- add by Zed 20150617
     * 
     * @param dataMap
     * @return
     * @throws Exception
     */
    private Map toLowerKey(Map dataMap) throws Exception {
    	
    	if(dataMap==null){
    		return null;
    	}
    	
	    Map newDataMap = new HashMap();
    	
    	Set keySet = dataMap.keySet();
		Iterator it = keySet.iterator();
		while(it.hasNext()){
			String key = (String) it.next();

			if(dataMap.get(key) instanceof List){//Map中包含List结构
				List dataList = (List)dataMap.get(key);
				List newDataList = new ArrayList();
				for(int i=0; i<dataList.size();i++){
					Map map0 = (Map)dataList.get(i);
					Map newMap0 = toLowerKey(map0);
					newDataList.add(newMap0);
				}
				newDataMap.put(key.toLowerCase(), newDataList);//将Map中大写的key转成小写
			}else {
				newDataMap.put(key.toLowerCase(), dataMap.get(key));//将Map中大写的key转成小写
			}
			
		}
			
		return newDataMap;
    }
    
}  
