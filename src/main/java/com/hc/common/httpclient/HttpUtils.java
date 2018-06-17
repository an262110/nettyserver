package com.hc.common.httpclient;

import com.alibaba.fastjson.JSON;
import com.hc.common.security.Base64Utils;
import com.hc.common.utils.LogUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * httpclient使用工具类
 * 
 * @author Zed
 *
 */
public class HttpUtils {

	//public static final String URL = "http://127.0.0.1:8844";
	//public static final String URL = AppConfig.getMessage("server.url");

	/**
	 * 发送请求
	 * 
	 * @param url
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public static Map sendReqAndResp(String url, Map paramMap) throws Exception {

		// 转换成json格式
		String jsonString = JSON.toJSONString(paramMap);
		LogUtils.info("preServer--发送到智付的请求数据:"+jsonString);

		jsonString = Base64Utils.encodedSafe(jsonString);// 加密后上传
		LogUtils.info("preServer--发送到智付的请求数据(加密后):"+jsonString);

		StringEntity entity = new StringEntity(jsonString);
		HttpPost post = new HttpPost(url);
		post.setEntity(entity);
		HttpClient client = MyHttpClient.getNewHttpClient();
		HttpResponse reponse = client.execute(post);
		HttpEntity resEntity = reponse.getEntity();

		String responseStr = EntityUtils.toString(resEntity);
		LogUtils.info("preServer--智付服务器返回的数据:"+responseStr);

		responseStr = Base64Utils.decode(responseStr);//
		LogUtils.info("preServer--智付服务器返回的数据(解密后):"+responseStr);
		
		Map resMap = new HashMap();
		if(null!=responseStr){
			resMap = (Map) JSON.parse(responseStr);
		}
		
		return resMap;
	}
	
	/**
	 * 发送GET请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String sendGet(String url, String params) throws Exception {
		
		if (StringUtils.isNotEmpty(params)) {
			url += "?" + params;
		}
		HttpGet get = new HttpGet(url);
		HttpClient client = MyHttpClient.getNewHttpClient();
		HttpResponse reponse = client.execute(get);
		HttpEntity resEntity = reponse.getEntity();
		return EntityUtils.toString(resEntity);
	}
	
	/**
	 * 使用示例
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		Map paramMap = new HashMap();
		paramMap.put("channel_type", "01");// 渠道号
		paramMap.put("interface_id", "A101");//接口编号
		
		List paramSubList = new ArrayList();
		Map paramSubMap = new HashMap();
		paramSubMap.put("name", "lizhizhong");
		paramSubMap.put("nick_name", "Zed");
		paramSubMap.put("mobile_no", "13632903543");
		paramSubList.add(paramSubMap);
		paramMap.put("param",paramSubList);
		
		Map resMap = HttpUtils.sendReqAndResp("http://127.0.0.1:8844/Web/testDemo.do", paramMap);
		System.out.println(resMap);
	}
	
}
