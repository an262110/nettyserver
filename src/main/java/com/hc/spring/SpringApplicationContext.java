package com.hc.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring配置文件类
 * 
 * @author Zed
 *
 */
public class SpringApplicationContext {
	
	
	private static String applicationContextPath = "applicationContext.xml";
	private static String databasePath = "applicationContext-database.xml";
	private static String servicePath = "applicationContext-service.xml";
		
	
	private static ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{applicationContextPath,databasePath,servicePath});
	
	private SpringApplicationContext() {
	}
	
	public static void init() {
		getApplicationContext();
	}
	
	/**
	 * 获取对象实例
	 * @return
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	/**
	 * 获取bean实例
	 * 
	 * @param serviceName
	 * @return
	 */
	public static Object getService(String serviceName) {
		return applicationContext.getBean(serviceName);
	}	
	
 
}
