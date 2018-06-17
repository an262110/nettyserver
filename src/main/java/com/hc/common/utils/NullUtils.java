package com.hc.common.utils;

/**
 * 空对象处理类
 * 
 * @author Zed
 *
 */
public class NullUtils {

	/**
	 * 空对象String转""
	 * 
	 * @param object
	 * @return
	 */
	public static String null2String(Object object) {

		if (object == null) {
			return "";
		}

		return object.toString();
	}
	
	/**
	 * 空对象 Integer转0
	 * 
	 * @param object
	 * @return
	 */
	public static int null2Zero(Object object) {

		if (object == null || "".equals(object.toString())) {
			return 0;
		}

		return Integer.valueOf(object.toString());
	}
	
	/**
	 * 空对象Double转0
	 * 
	 * @param object
	 * @return
	 */
	public static double null2DoubleZero(Object object) {

		if (object == null || "".equals(object.toString())) {
			return 0;
		}

		return Double.valueOf(object.toString());
	}


}
