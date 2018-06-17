package com.hc.common.utils.hk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ParseUtil {

	private static final Logger log = LoggerFactory.getLogger(ParseUtil.class);

	/**
	 * Convert byte[] to hex string.
	 * 
	 * @param src
	 *            byte[] data
	 * @return hex string
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	
	public static String byteToHex(byte src) {
		StringBuilder stringBuilder = new StringBuilder("");

			int v = src & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		return stringBuilder.toString();
	}

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert binary string to int
	 * 
	 * @param binaryString
	 * 
	 * @return int
	 */
	public static int binaryStringToInt(String binaryString) {
		if (binaryString == null || binaryString.equals("")) {
			return -1;
		} else {
			return Integer.parseInt(binaryString, 2);
		}

	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * 二进制字符串转成byte
	 * 
	 * @param bString
	 * @return
	 */
	public static byte bit2byte(String bString) {
		byte result = 0;
		for (int i = bString.length() - 1, j = 0; i >= 0; i--, j++) {
			result += (Byte.parseByte(bString.charAt(i) + "") * Math.pow(2, j));
		}
		return result;
	}

	/**
	 * byte转成二进制字符串
	 * 
	 * @param bString
	 * @return
	 */
	public static String byte2Bits(byte b) {

		int z = b;
		z |= 256;
		String str = Integer.toBinaryString(z);
		int len = str.length();
		return str.substring(len - 8, len);
	}

	/**
	 * byte数组转成二进制字符串
	 * 
	 * @param bs
	 * @return
	 */
	public static String bytes2Str(byte[] bs) {
		String value = "";
		for (byte t : bs) {
			value = value + ParseUtil.byte2Bits(t);
		}
		// if(value.length()>=32) //截取4个字节长度
		// value=value.substring(1, 32);//忽略二进制字符串的符号位
		return value;
	}

	// /**
	// * byte 数组与 int 的相互转换
	// *
	// * @param b
	// * 4个字节
	// * @return
	// */
	//
	// public static int FourByteToInt(byte[] b) {
	// return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16
	// | (b[0] & 0xFF) << 24;
	// }

	/**
	 * byte 数组转成int
	 * 
	 * @param b
	 * 
	 * @return
	 */
	public static int bytesToInt(byte[] bs) {

		String value = ParseUtil.bytes2Str(bs);

		if (value.length() >= 32) // 截取4个字节长度
			value = value.substring(1, 32);// 忽略二进制字符串的符号位

		return Integer.parseInt(value, 2);

	}

	/**
	 * 字符串首字母大写
	 * 
	 * @param name
	 * @return
	 */
	public static String captureName(String name) {
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		return name;

		// /**
		// * 以下方法当首字母为A时，则错误
		// */
		// char[] cs = name.toCharArray();
		// cs[0] -= 32;
		// return String.valueOf(cs);

	}
	
	/**
	 * 转换UTF-8字符
	 * @param string
	 * @return
	 */
	public static String getUTF8String(String str) {

		String utf8Str = null;
		try {
			utf8Str = new String(str.getBytes("UTF-8"));
			utf8Str = URLEncoder.encode(utf8Str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
		}  
		return utf8Str;
	}
	/**
	 * 获取本地时间      edit by hjz 
	 * key-year，value-year 
	 * Key-month,value-month
	 * key-day,value-day 
	 * key-hour，value-hour 
	 * Key-minute,value-minute
	 * key-second,value-second
	 * @return
	 */
	public static Map<String, Integer> getDate() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		Calendar c = Calendar.getInstance();
		map.put("year", c.get(Calendar.YEAR));
		map.put("month", c.get(Calendar.MONTH) + 1);
		map.put("day", c.get(Calendar.DAY_OF_MONTH));
		map.put("hour", c.get(Calendar.HOUR_OF_DAY));
		map.put("minute", c.get(Calendar.MINUTE));
		map.put("second", c.get(Calendar.SECOND));
		return map;

	}

	/***
	 * String不足length时，前面补位0
	 */
	public static String getFixString(String nexVal,int length) {
		int i=length-nexVal.length();
		if(i==0){
			return nexVal;
		}else{
			for(int temp=0;temp<i;temp++){
				nexVal="0"+nexVal;
			}
			return nexVal;
		}
	}
		/**
		 * 输入时间格式获取当前时间字符串
		 * @param format
		 * @return
		 */
	public static String getCurrentTime(String format){
		Date current = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String currentTime = Long.parseLong((formatter.format(current))) + "";
		return currentTime;
	}
	

	
	public static void main(String[] args) {
//		byte[] bs = { -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 1 };
//		byte[] bt = { 1, 1, 1, 1};
//		System.out.println(ParseUtil.bytesToInt(bs));
//		System.out.println(ParseUtil.bytesToInt(bt));
//		// System.out.println(Integer.parseInt("01110000000000000000000000000000",
//		// 2));
		
		System.out.println(getCurrentTime("YYYYMMddHHmmssSSS"));
		 
	}

}
