package com.hc.app.utils;

import com.hc.common.config.AppConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * session token工具类
 * 
 * @author Zed
 *
 */
public class TokenUtils {

	/**
	 * 获取session token
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getToken() throws Exception {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
		String token = formatter.format(new Date());
		
		return token;
	}
	
	/**
	 * 检查token是否过期
	 * 
	 * @param custNo
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public static boolean checkToken(String token) throws Exception {
		
		int tokenTimeout = Integer.valueOf(AppConfig.getMessage("token.timeout"));//
		
		//当时时间
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
		Date tokenDate = formatter.parse(token);
		
		long nowTime = new Date().getTime();//
		long tokenTime = tokenDate.getTime();//
	       		
		long compareTime = nowTime - tokenTime;
		if(compareTime >tokenTimeout){//超时了
			return true;
		}

		return false;
	}

}
