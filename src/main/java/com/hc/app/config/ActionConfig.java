package com.hc.app.config;

import com.hc.common.utils.LogUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * 业务逻辑接口类 配置文件类
 * 
 * @author Zed
 *
 */
public class ActionConfig {

	/*
	 * config文件
	 */
	private static String ActionConfigFile= "ActionConfig.properties";
	
	private static Configuration configuration ;

	/*
	 * 获取PropertiesConfiguration实例
	 */
	public static Configuration getInstance() {

		try {
			
			if(configuration == null){
				configuration = new PropertiesConfiguration(ActionConfigFile);
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
	public static String getActionName(String key){
		
		String actionName = ActionConfig.getInstance().getString(key);
		
		return actionName;
	}
	
	/**
	 * 是否包含该key
	 * 
	 * @param key
	 * @return
	 */
    public static Boolean contains(String key){
				
		return ActionConfig.getInstance().containsKey(key);
	}

	/**
	 * 使用示例
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		System.out.println(ActionConfig.getActionName("A101"));
		System.out.println(ActionConfig.getActionName("A102"));
	}
	
}
