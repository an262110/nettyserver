package com.hc.common.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 客户端测试示例
 * 
 * @author lenovo
 *
 */
public class DemoClient {
	
	public static final String URL = "http://127.0.0.1:8844/Web/testDemo.do";

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
		
		//转换成json格式
		String jsonString = com.alibaba.fastjson.JSON.toJSONString(paramMap); 
		System.out.println(jsonString);
		
		jsonString = com.hc.common.security.Base64Utils.encodedSafe(jsonString);//加密后上传
		System.out.println("加密后="+jsonString);
		
		/*
		BasicNameValuePair paramJson = new BasicNameValuePair("param_json", jsonString);  
		List<BasicNameValuePair> datas = new ArrayList<BasicNameValuePair>();
		datas.add(paramJson);
		HttpEntity entity = new UrlEncodedFormEntity(datas, "utf-8");
		*/
		
		//将JSON进行UTF-8编码,以便传输中文
        //String encoderJson = URLEncoder.encode(jsonString, HTTP.UTF_8);
		StringEntity entity = new StringEntity(jsonString);
	
		HttpPost post = new HttpPost(URL);
		post.setEntity(entity);
		HttpClient client = MyHttpClient.getNewHttpClient();
		HttpResponse reponse = client.execute(post);
		HttpEntity resEntity = reponse.getEntity();
		
		String responseStr = EntityUtils.toString(resEntity);
		System.out.println("服务器返回的数据＝"+responseStr);
		
		responseStr = com.hc.common.security.Base64Utils.decode(responseStr);//
		System.out.println("服务器返回的数据 解密后＝"+responseStr);
	}

}
