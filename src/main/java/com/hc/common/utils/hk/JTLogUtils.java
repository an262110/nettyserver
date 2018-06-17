package com.hc.common.utils.hk;

import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class JTLogUtils {
	private static Logger logger = Logger.getLogger("IDNJT");

	public static void info(Object message) {
		if (message != null) {
			if (message instanceof Map) {
				System.out.println("进入这里--------------");
				Map m = (Map) message;
				Set keys = m.keySet();
				Iterator keylist = keys.iterator();
				while (keylist.hasNext()) {
					String k = (String) keylist.next();
					logger.info(k + ":" + m.get(k));
				}
			}else {
				logger.info(message);
			}

		}
	}
	public  static void debug(Object message) {
		 if(message instanceof Map){
	        	
	        	Map m=(Map)message;
	        	Set keys=m.keySet();
	        	
	        	Iterator keylist=keys.iterator();
	        	while(keylist.hasNext()){
	        		String k=(String)keylist.next();
	        	  logger.debug(k+":"+m.get(k));
	        	}
	        }else{

		logger.debug(message);
	        }
	}

	public static void error(Object message) {
      
		 if(message instanceof Map){
	        	
	        	Map m=(Map)message;
	        	Set keys=m.keySet();
	        	
	        	Iterator keylist=keys.iterator();
	        	while(keylist.hasNext()){
	        		String k=(String)keylist.next();
	        	  logger.error(k+":"+m.get(k));
	        	}
	        }else{
		logger.error(message);
	        }
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
