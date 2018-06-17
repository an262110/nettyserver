package com.hc.app.client.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateStr {

	public static void main(String[] args) throws ParseException {
		String tt = "20160913170933";
		
		
		SimpleDateFormat   df   =   new   SimpleDateFormat("yyyyMMddHHmmss");   
		  Date   begin=df.parse("20160913230933");   
		  Date   end   =   Calendar.getInstance().getTime();
		  long   between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒   
//		  int   day=between/(24*3600);   
//		  int   hour=between%(24*3600)/3600;   
//		  int   minute=between%3600/60;   
//		  int   second=between%60; 
		  System.out.println(between/60);
	}

}
