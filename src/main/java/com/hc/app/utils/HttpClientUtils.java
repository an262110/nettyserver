package com.hc.app.utils;

import com.alibaba.fastjson.JSON;
import com.hc.common.config.AppConfig;
import com.hc.common.utils.LogUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

@SuppressWarnings("deprecation")
public class HttpClientUtils {
	
	private static String zf_http_url = AppConfig.getMessage("mpos.zf.url");
		
	/**
	 * 发送智付Pos p系统
	 * 
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public static Map sendDataToZF(Map paramMap) throws Exception {
		
		if(null == paramMap){
			return null;
		}
		
		List<BasicNameValuePair> datas = new ArrayList<BasicNameValuePair>();
		
		Set keys = paramMap.keySet();		
        Iterator iterator = paramMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String)iterator.next();
			String value = paramMap.get(key).toString();
			
			datas.add(new BasicNameValuePair(key,value));
		}	
		LogUtils.info("发送至智付系统的数据: "+paramMap);

		HttpEntity entity = new UrlEncodedFormEntity(datas, "utf-8");
		HttpPost post = new HttpPost(zf_http_url);
		post.setEntity(entity);
		
		HttpClient client = getNewHttpClient();
		//请求超时
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60*1000);
		//读取超时
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
		
		HttpResponse reponse = client.execute(post);
		HttpEntity resEntity = reponse.getEntity();
		
		String responseStr = EntityUtils.toString(resEntity);
		LogUtils.info("智付系统返回的数据: "+responseStr);
//		示例：
//		{
//		    "resCode":"0000",
//		    "resData":{"acceptNo":"s00001"｝,
//		    "resMsg":"申请入网操作成功",
//		    "checkValue":"ADfoeKQn6eEHgLF8ETMXan3TfFO03R5"
//		}
		
		Map resMap = (Map) JSON.parse(responseStr);
				
		return resMap;
	}
	  
	/**
	 * 获取对象实例
	 * @return
	 */
	public static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			LogUtils.printStackTrace(e);
			return new DefaultHttpClient();
		}
	}
		
		
	/**
	 * SSL配置
	 * 
	 * @author Zed
	 *
	 */
	private static class MySSLSocketFactory extends SSLSocketFactory {

		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}

	}

   public static void main(String[] args) throws Exception {
		
		Map dataMap = new HashMap();
		dataMap.put("AAA", "aaaa");
		dataMap.put("BBB", "bbbb");
		Set keys = dataMap.keySet();
		
        Iterator iterator = dataMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String)iterator.next();
			System.out.println(key);
			System.out.println(dataMap.get(key).toString());
		}	
		
	}

	public static void test() throws Exception {
		
		//配置https
		//System.setProperty("javax.net.ssl.keyStore",keyStore);
    	//System.setProperty("javax.net.ssl.keyStorePassword",keyStorePassword);
    	
		try {
			
			BasicNameValuePair req_code = new BasicNameValuePair("req_code", "S101");
			BasicNameValuePair cust_no = new BasicNameValuePair("cust_no", "13632903");
			BasicNameValuePair mobile_no = new BasicNameValuePair("mobile_no", "13632903543");
			
			List<BasicNameValuePair> datas = new ArrayList<BasicNameValuePair>();
			datas.add(req_code);
			datas.add(cust_no);
			datas.add(mobile_no); 

			HttpEntity entity = new UrlEncodedFormEntity(datas, "utf-8");
			//HttpPost post = new HttpPost("http://192.168.199.249:8844/UnionPay/callbackForSync");
			HttpPost post = new HttpPost("http://192.168.199.249:8844/UnionPay/callbackForSync.do");
			//HttpPost post = new HttpPost("http://192.168.199.249:8844/UnionPay/callbackForAsync");
			post.setEntity(entity);
			
			HttpClient client = getNewHttpClient();
			//请求超时
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60*1000);
			//读取超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
			
			HttpResponse reponse = client.execute(post);
			HttpEntity resEntity = reponse.getEntity();
			System.out.println(EntityUtils.toString(resEntity));
			
		} catch (Exception ex) {
			LogUtils.printStackTrace(ex);
		}
	}
	

}
