package com.hc.app.utils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * @author TTF
 * @date 2015年6月12日
 * @version 1.0 
 */
public class NumberUtil {
	/**
	 * 将对象转换为Long类型返回，如果没有记录则返回0
	 * @param obj
	 * @return
	 */
	public static  long getLongFromObject(Object obj) {
		long oid = 0;
		if (obj == null) {
			return oid;
		}
		if (obj instanceof Number) {
			oid = ((Number) obj).longValue();
		}
		return oid;
	}
	
	public static int getTotalPage(int totalNo,int pageSize) {
		int totalPage = totalNo / pageSize;
		if (totalNo % pageSize != 0) {
			totalPage += 1;
		}
		return totalPage;
	}
	
	/**
	 * 
	 * @Title: bytesToHexString 
	 * @Description: TODO(把字节数组转换成16进制字符串) 
	 * @param bArray 字节数组
	 * @return String
	 * @throws
	 */
	public static final String bytesToHexString(byte[] bArray) {
	    StringBuffer sb = new StringBuffer(bArray.length);
	    String sTemp;
	    for (int i = 0; i < bArray.length; i++) {
	      sTemp = Integer.toHexString(0xFF & bArray[i]);
	      if (sTemp.length() < 2){
	        sb.append(0);
	      }
	      sb.append(sTemp.toUpperCase());
	    }
	    return sb.toString();
	}
	
	public static BigDecimal getDecimal(String literal) {
		if (literal == null || literal.length() == 0) {
			return new BigDecimal("0");// 改成"0"，为了兼容Java 1.4
		}
		if (literal.charAt(0) == '.') {
			return new BigDecimal("0" + literal);
		}
		return new BigDecimal(literal);
	}
	
	// 加法运算
	public static String addBigDecimal(String decimalLiteral1,
			String decimalLiteral2) {
		decimalLiteral1 = decimalLiteral1.replaceAll(",", "");
		decimalLiteral2 = decimalLiteral2.replaceAll(",", "");
		return getDecimal(decimalLiteral1).add(getDecimal(decimalLiteral2))
				.toString();
	}
	
	// 减法运算
	public static String subBigDecimal(String decimalLiteral1,
			String decimalLiteral2) {
		decimalLiteral1 = decimalLiteral1.replaceAll(",", "");
		decimalLiteral2 = decimalLiteral2.replaceAll(",", "");
		return getDecimal(decimalLiteral1)
				.subtract(getDecimal(decimalLiteral2)).toString();
	}
	
	// 乘法运算
	public static String mulBigDecimal(String decimalLiteral1,
			String decimalLiteral2) {
		decimalLiteral1 = decimalLiteral1.replaceAll(",", "");
		decimalLiteral2 = decimalLiteral2.replaceAll(",", "");
		return getDecimal(decimalLiteral1)
				.multiply(getDecimal(decimalLiteral2)).toString();
	}
	
	// 除法运算  num 小数点位数
	public static String divBigDecimal(String decimalLiteral1,
			String decimalLiteral2,int num) {
		decimalLiteral1 = decimalLiteral1.replaceAll(",", "");
		decimalLiteral2 = decimalLiteral2.replaceAll(",", "");
		return getDecimal(decimalLiteral1).divide(getDecimal(decimalLiteral2),num, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	/**
	 * 判断是都全部为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){ 
		if(NumberUtil.isEmpty(str)) {
			return false ;
		}
		 Pattern pattern = Pattern.compile("[0-9]*"); 
		 return pattern.matcher(str).matches(); 
	 } 
	
	/**
	 * 
	 * <DL>
	 * <DT><B> 字符串的比较 </B></DT>
	 * <p>
	 * <DD> 详细介绍:字符串的比较 .</DD>
	 * </DL>
	 * <p>
	 * 
	 * @param decimalLiteral1
	 *            字符串一
	 * @param decimalLiteral2
	 *            字符串二
	 * @return 比较两个数字的大小，相等返回0，前者大于后者返回1，若小于返回-1
	 */
	public static int compare(String decimalLiteral1, String decimalLiteral2) {
		decimalLiteral1 = decimalLiteral1.replaceAll(",", "");
		decimalLiteral2 = decimalLiteral2.replaceAll(",", "");
		return getDecimal(decimalLiteral1).compareTo(
				getDecimal(decimalLiteral2));
	}
	/**
	 * 判断字符串是否为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		if(str==null||str.trim().length()==0)
			return true;
		else
			return false;
	}
}
