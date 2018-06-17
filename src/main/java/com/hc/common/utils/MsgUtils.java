package com.hc.common.utils;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * 提示消息工具类
 * 
 * @author Zed
 *
 */
public class MsgUtils {

	/*
	 * config文件
	 */
	private static String MESSAGES_FILE = "messages.properties";
	
	private static Configuration configuration ;

	/*
	 * 获取PropertiesConfiguration实例
	 */
	public static Configuration getInstance() {

		try {
			
			if(configuration == null){
				configuration = new PropertiesConfiguration(MESSAGES_FILE);
			}

		} catch (ConfigurationException e) {
			
			LogUtils.printStackTrace(e);
		}

		return configuration;
	}
	
	/**
	 * 获取信息
	 * 
	 * @param key
	 * @return
	 */
	public static String getMessage(String key){
		
		String message = MsgUtils.getInstance().getString(key);
		
		return message;
	}

	/**
	 * 使用示例
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		System.out.println(MsgUtils.getMessage("key"));
	}
	
}
