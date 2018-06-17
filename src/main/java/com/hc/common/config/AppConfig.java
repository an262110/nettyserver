package com.hc.common.config;

import com.hc.common.utils.LogUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * 系统配置文件类
 * 
 * @author Zed
 * 
 */
public class AppConfig {

	/**
	 * 配置文件
	 */
	private static String CONFIG_FILE = "config.properties";
	
	private static Configuration configuration ;

	/*
	 * 获取PropertiesConfiguration实例
	 */
	public static Configuration getInstance() {

		try {
			
			if(configuration == null){
				configuration = new PropertiesConfiguration(CONFIG_FILE);
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
		
		String message = AppConfig.getInstance().getString(key);
		
		return message;
	}
	
	
	/**
	 * 使用示例
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		System.out.println(AppConfig.getMessage("key"));
	}

}
