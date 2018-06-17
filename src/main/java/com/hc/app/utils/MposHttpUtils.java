package com.hc.app.utils;

import com.alibaba.fastjson.JSON;
import com.hc.common.config.AppConfig;
import com.hc.common.utils.LogUtils;
import org.apache.commons.codec.binary.Base64;
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

/**
 * 智付http工具类
 * 
 * @author Zed
 *
 */
public class MposHttpUtils {
	
	
	private static String zf_http_url = AppConfig.getMessage("mpos.zf.url");

	//成功响应码
	private static String success_code = "0000";

	//测试
	public static void main(String[] args) throws Exception{
		
		//商户入网申请
		//applyMerchant();
		
		//在线增加终端
		String member_no="0002";//会员号
		String merchant_no="303602057221190";//商户号
		String seq_s="SN-878-7878787,22";//MPOS硬件序列号 多个请用逗号,隔开
		addMpos(member_no,merchant_no,seq_s);
		
		//校验终端
		String seq = "22";
		//verifyMpos(seq);
		
		//注销终端
		String memberNo="0002";//会员号
		String mchtNo="303602057221190";//商户号
		String termNo="00000003";//终端号
		String seq2="N-878-7878787";//MPOS序列号
		//cancelMpos(memberNo,mchtNo,termNo,seq2);
		
	}
	
	
	/**
	 * 商户入网申请
	 * @throws Exception
	 */
	public static Map applyMerchant(Map dataObj) throws Exception{
		
		String reqCode = "M101";//接口编号  

		//组装报文数据
//		HashMap<String,String> dataObj = new HashMap<String,String>();
//		dataObj.put("memberNo","0005");//会员号
//		dataObj.put("shopName","广州智付6");//店铺名称
//		dataObj.put("manager","0001");//法人代表姓名
//		dataObj.put("docType","01");//法人代表证件类型
//		dataObj.put("idCardNo","0003");//法人代表身份证号码
//		dataObj.put("phoneNo","1341686111");//法人手机号
//		dataObj.put("bankName","农业银行广州天河支行");//开户银行名称（具体支行）
//		dataObj.put("inAreaNo","0004");//开户省份代码
//		dataObj.put("inCityNo","0005");//开户城市代码
//		dataObj.put("settleAcctName","张三");//收款人名称
//		dataObj.put("settleAcct","622512345678900");//收款人账号
//		dataObj.put("licenceUrl","20150715_001_123001.jpg");//营业执照图片名   日期_app商户号_*.*
//		dataObj.put("idCardUrl_a","20150715_001_1123002.jpg");//法人身份证正面图片名
//		dataObj.put("idCardUrl_b","20150715_001_123003.jpg");//法人身份证反面图片名
//		dataObj.put("idCardUrl_c","20150715_001_123004.jpg");//法人手持身份证照片图片名
//		dataObj.put("operationUrl","20150715_001_123005.jpg");//经营场所照片图片名
//		dataObj.put("seq","SN-878-7878787");//硬件序列号
		
		Map resDataMap = sendRequest(reqCode,dataObj);
			
		return resDataMap; 
	}
	/**
	 * 个人用户入网申请
	 * @throws Exception
	 */
	public static Map applyPersonal(Map dataObj) throws Exception{
		
		String reqCode = "M105";//接口编号  
		
		Map resDataMap = sendRequest(reqCode,dataObj);
			
		return resDataMap; 
	}
	
	/**
	 * 商户信息修改
	 * @throws Exception
	 */
	public static Map modifyM(Map dataObj) throws Exception{
		
		String reqCode = "M106";//接口编号  
		
		Map resDataMap = sendRequest(reqCode,dataObj);
			
		return resDataMap; 
	}
	
	/**
	 * 商户信息修改
	 * @throws Exception
	 */
	public static Map cardVerify(Map dataObj) throws Exception{
		
		String reqCode = "M107";//接口编号  
		
		Map resDataMap = sendRequest(reqCode,dataObj);
			
		return resDataMap; 
	}
	
	
	/**
	 * 在线增加终端
	 * 
	 * @param memberNo  会员号
	 * @param merchantNo 商户号
	 * @param seq //MPOS硬件序列号 多个请用逗号,隔开
	 * @throws Exception
	 */
	public static Map addMpos(String member_no,String merchant_no,String seq_s) throws Exception{
		
		String reqCode = "M102";//接口编号  

		//组装报文数据
		HashMap<String,String> dataObj = new HashMap<String,String>();
		dataObj.put("memberNo",member_no);//会员号
		dataObj.put("mchtNo",merchant_no);//商户号
		dataObj.put("seq",seq_s);//MPOS硬件序列号 多个请用逗号,隔开
		
        Map resDataMap = sendRequest(reqCode,dataObj);
		
		return resDataMap; 
	}
	
	
	/**
	 * 校验终端序列号
	 * 
	 * @param seq_s
	 * @return
	 * @throws Exception
	 */
	public static Map verifyMpos(String seq_s) throws Exception{
		
		String reqCode = "M103";//接口编号  
		
		//组装报文数据
		HashMap<String,String> dataObj = new HashMap<String,String>();
		dataObj.put("seq",seq_s);//硬件序列号
		
        Map resDataMap = sendRequest(reqCode,dataObj);
		
		return resDataMap;
	}
	
	
	/**
	 * 终端注销
	 * 
	 * @param memberNo 会员号
	 * @param mchtNo 商户号
	 * @param termNo 终端号
	 * @param seq MPOS序列号
	 * @return
	 * @throws Exception
	 */
	public static Map cancelMpos(String p_memberNo,String p_mchtNo,String p_termNo,String p_seq) throws Exception{
		
		String reqCode = "M104";//接口编号  
	
		HashMap<String,String> dataObj = new HashMap<String,String>();
		dataObj.put("memberNo",p_memberNo);//会员号
		dataObj.put("mchtNo",p_mchtNo);//商户号
		dataObj.put("termNo",p_termNo);//终端号
		dataObj.put("seq",p_seq);//MPOS序列号
		
		Map resDataMap = sendRequest(reqCode,dataObj);
		
		return resDataMap;
	}
	
	
    public static Map sendRequest(String reqCode,Map dataObj) throws Exception{
    	
		//String reqCode = "M104";//接口编号  
		String reqData = "";//报文内容
		String checkValue = "";//签名字段
	
		//转成json字符串
		reqData = JSON.toJSONString(dataObj);
		//签名
		checkValue = RsaEncryptUtil.getCheckValue(reqData);
		//base64加密
		reqData =  Base64.encodeBase64String(reqData.getBytes("utf-8")).replaceAll("(\r\n|\r|\n|\n\r)", "");;
		
		//设置发送表单参数
		 Map params_s = new HashMap();
		 params_s.put("req_code",reqCode);//接口编号
		 params_s.put("reqData",reqData);//
		 params_s.put("checkValue",checkValue);///
		 
		 LogUtils.info("send [resCode]:"+reqCode);
		 LogUtils.info("send [reqData]:"+reqData);
		 LogUtils.info("send [checkValue]:"+checkValue);
		
		 //请求接口，获取返回内容
		 Map resContentMap = sendDataToZF(params_s);
		 LogUtils.info("response data="+resContentMap);
		 
		//获取各字段内容
		String resCode = (String)resContentMap.get("resCode");
		String resMsg = (String)resContentMap.get("resMsg");
		String resData = (String)resContentMap.get("resData");
		String checkV = (String)resContentMap.get("checkValue");
	
		LogUtils.info("return [resCode]:"+resCode);
		LogUtils.info("return [resMsg]:"+resMsg);
		LogUtils.info("return [resData] 密文:"+resData);
		LogUtils.info("return [checkValue]:"+checkV);
		
		//返回给客户端的数据 
		Map resClientMap = new HashMap();
				
		//验证签名
		boolean flag = RsaEncryptUtil.verify(resData, checkV);
		if(flag){
			LogUtils.info("验签成功");
			if(!success_code.equals(resCode)){
				LogUtils.info("操作失败 resMsg："+resMsg);
				resClientMap.put("return_code", resCode);//返回码
				resClientMap.put("return_msg", resMsg);//返回提示信息
				return resClientMap;
			}
			//将报文转为明文
			resData = new String(Base64.decodeBase64(resData.getBytes()),"utf-8");
			LogUtils.info("return [resData] 明文 :"+resData);
			
			Map resDataMap = (Map) JSON.parse(resData);
			LogUtils.info("return [resData]:"+resDataMap);
			
			//返回给客户端的数据 
			//Map resClientMap = new HashMap();
			resClientMap.put("return_code", resCode);//返回码
			resClientMap.put("return_msg", resMsg);//返回提示信息
			resClientMap.putAll(resDataMap);
			return resClientMap;
			
		}else{
			LogUtils.info("签名不正确");
			resClientMap.put("return_code", resCode);//返回码
			resClientMap.put("return_msg", "["+reqCode+"]"+resMsg);//返回提示信息
			return resClientMap;
		}
		
	}

	
	//------------------------------- 以下为 HttpClient工具类 ----------------------------
	
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
		//请求超时  智付超时时间在60~75s之间，修改请求超时时间为75s
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 75*1000);
		//读取超时  智付超时时间在60~75s之间，修改读取超时时间为75s
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 75*1000);
		
		HttpResponse reponse = client.execute(post);
		HttpEntity resEntity = reponse.getEntity();
		
		String responseStr = EntityUtils.toString(resEntity);
		LogUtils.info("智付系统返回的数据: "+responseStr);

		//转成Map
		Map resMap = (Map) JSON.parse(responseStr);
				
		return resMap;
	}
	  
	/**
	 * 获取对象实例
	 * @return
	 */
	@SuppressWarnings("deprecation")
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

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

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
	
}
