package com.hc.app.exception;

/**
 * 业务异常类
 * 
 * @author Zed
 *
 */
public class ServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 错误码
	 */
	private String errorCode;
	
	/**
	 * 错误信息
	 */
	private String errorMsg;
	

	public ServiceException(String errorCode,String errorMsg){
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}
    
    public String getErrorCode(){
    	return this.errorCode;
    }
    
    public String getErrorMsg(){
    	return this.errorMsg;
    }
	
}
