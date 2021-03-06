package com.hc.common.security;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

/**
 * 封装Base64的工具类
 * 
 */
public class Base64Utils {

	public final static String ENCODING = "UTF-8";

	// 加密
	public static String encoded(String data) throws UnsupportedEncodingException {
		byte[] b = Base64.encodeBase64(data.getBytes(ENCODING));
		return new String(b, ENCODING);
	}

	// 加密,遵循RFC标准
	public static String encodedSafe(String data) throws UnsupportedEncodingException {
		byte[] b = Base64.encodeBase64(data.getBytes(ENCODING), true);
		return new String(b, ENCODING);
	}

	// 解密
	public static String decode(String data)
			throws UnsupportedEncodingException {
		byte[] b = Base64.decodeBase64(data.getBytes(ENCODING));
		return new String(b, ENCODING);
	}

	/**
	 * 测试类
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		String str = "abc123";
		
		// 加密该字符串
		String encodedString = Base64Utils.encodedSafe(str);
		System.out.println(encodedString);
		
		// 解密该字符串
		String decodedString = Base64Utils.decode(encodedString);
		System.out.println(decodedString);
	}

}
