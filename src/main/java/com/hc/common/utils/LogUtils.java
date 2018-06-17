package com.hc.common.utils;

import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * log4j 封装
 * 
 * @author Zed
 *
 */
public class LogUtils {
	
	private static Logger logger = Logger.getLogger("IDN");
	

	/**
	 * 打log到log4j中指定的文件
	 * @param message
	 */
	public static void info(Object message) {

		logger.info(message);
	}
	
	public static void debug(Object message) {

		logger.debug(message);
	}

	public static void error(Object message) {

		logger.error(message);
	}
	public static void error(Object message,Throwable e) {

		logger.error(message,e);
	}
	
	public static void printStackTrace(Throwable t) {
		StringWriter stringWriter= new StringWriter();  
        PrintWriter writer= new PrintWriter(stringWriter);  
        t.printStackTrace(writer);  
        StringBuffer buffer= stringWriter.getBuffer();  
		logger.error("异常信息:" + buffer.toString());
	}
	
}
