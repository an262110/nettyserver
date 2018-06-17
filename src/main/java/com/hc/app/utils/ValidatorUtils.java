package com.hc.app.utils;

import org.apache.commons.lang.StringUtils;

/**
 * 数据校验
 * 
 * <p>
 * User Jasme
 * <p>
 * Date 2015年5月27日 上午10:19:32
 *
 */
public class ValidatorUtils {

	/**
	 * 为空校验
	 * 
	 * @param fields
	 * @return
	 */
	public static boolean isNotEmpty(String... fields) {
		
		for (String field : fields) {
			if (StringUtils.isEmpty(field)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 检查手机号码格式并过滤
	 * 
	 * @param mobiles
	 * @return
	 */
	public static String scanMobile(String mobiles, char separator) {
		String[] mobileArray = StringUtils.split(mobiles, separator);
		if (null == mobileArray || mobileArray.length == 0) {
			return null;
		}
		String str = "";
		for (String mobile : mobileArray) {
			if (isMobile(mobile)) {
				str += mobile + ",";
			}
		}
		int length = str.length();
		if (length > 0) {
			str = str.substring(0, length - 1);
		}
		return str;
	}
	
	/**
	 * 手机号码校验
	 * 
	 * @param mobile
	 * @return
	 */
	public static boolean isMobile(String mobile) {
		if (StringUtils.isNotEmpty(mobile)) {

			return mobile.matches("^0?(13[0-9]|15[012356789]|18[0-9]|14[57])[0-9]{8}$");
		}
		return false;
	}
	
}
