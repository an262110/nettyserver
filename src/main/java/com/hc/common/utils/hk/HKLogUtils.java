package com.hc.common.utils.hk;

import com.hc.app.action.hk.RequestObject;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * log4j 封装
 * 
 * @author Zed
 *
 */
public class HKLogUtils {
	
	private  static Logger logger = Logger.getLogger("IDNHK");
	
	/**
	 * 打log到log4j中指定的文件
	 * @param message
	 */
	public  static void info(Object message) {
		if(message!=null){
	        if(message instanceof Map){
	        	
	        	Map m=(Map)message;
	        	Set keys=m.keySet();
	        	
	        	Iterator keylist=keys.iterator();
	        	while(keylist.hasNext()){
	        		String k=(String)keylist.next();
	        	  logger.info(k+":"+m.get(k));
	        	}
	        }else if(message instanceof RequestObject){
	        	RequestObject ob=(RequestObject)message;
	        	Map head1=ob.getControlHeader();
	        	Map head2=ob.getHeader();
	        	Map data=ob.getData();
	        	if(head1!=null&&!head1.isEmpty()){
	        	 HKLogUtils.info("=================================控制头信息=======================");
		         HKLogUtils.info(head1);
	        	}
	        	if(head2!=null&&!head2.isEmpty()){
		        	 HKLogUtils.info("=================================标准头信息====================");
			         HKLogUtils.info(head2);
		        	}
	        	if(data!=null&&!data.isEmpty()){
		        	 HKLogUtils.info("=================================数据信息=======================");
			         HKLogUtils.info(data);
		        	}
	        }else{
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
