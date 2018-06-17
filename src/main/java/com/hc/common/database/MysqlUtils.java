package com.hc.common.database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Mysql 工具类
 * 
 * @author Zed
 *
 */
public class MysqlUtils {

	/**
	 * 获取mysql表主键
	 * 
	 * @param tableFlag
	 * @return
	 */
	public static String getTableSequence(String tableFlag){
		
		String tableSequence = "";
		String tableKeyFalg = "";
		if(tableFlag != null){
			tableKeyFalg = tableFlag;
		}
		
		SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMddHHmmssSSSS");
		String currentTime = formatter.format(new Date());
		
		Random random = new Random();
		int randomInt = random.nextInt(9999);
		
		//不足四位的，后面补0  ********* modify by Zed 2014-01-25
		String randomStr = randomInt + "";
		if(randomStr.length() < 4){
			for(int i=randomStr.length(); i<4; i++){
				randomStr = randomStr + "0";
			}
		}
		
		tableSequence = tableKeyFalg + currentTime + randomStr ;
		
		return tableSequence;
	}
	
	/**
	 * 测试
	 * @param args
	 */
	public static void main(String[] args){
		
		for(int i=0;i<1000;i++){
			System.out.println(getTableSequence("ZZ"));
		}
	}
	
}
