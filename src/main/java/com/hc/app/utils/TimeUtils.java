package com.hc.app.utils;

import com.hc.common.utils.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 时间工具类
 * 
 * @author Zed
 *
 */
public class TimeUtils {

	private static Calendar calendar = Calendar.getInstance();

	
	public static String getSimpleCurrentTime(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM-dd-HH-mm-ss");
		String currentTime = formatter.format(new Date());	
		return currentTime;
	}
	
	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getCurrentTime(){
		
		//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String currentTime = formatter.format(new Date());
		
		return currentTime;
	}
	
	
	/**
	 * 获取指定格式的当前时间
	 * 
	 * @return
	 */
	public static String getCurrentTime(String format){
		
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String currentTime = formatter.format(new Date());
		
		return currentTime;
	}
	
	/**
	 * 获取当前日期+当前时分秒
	 * 
	 * @return
	 */
	public static String[] getCurrentDateArray() {

		Date date = new Date();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");//当前日期格式 
		String dateStr = formatter.format(date);
		
		formatter = new SimpleDateFormat("HHmmss");//当前时分秒
		String timeStr = formatter.format(date);
		
		formatter = new SimpleDateFormat("yyyyMMddHHmmss");//当前时分秒
		String dayStr = formatter.format(date);
		
		String[] dateArray = new String[]{ dateStr,timeStr,dayStr };
		
		//yyyyMMdd dateArray[0]
		//HHmmdd dataArray[1]
		//yyyyMMddHHmmss dataArray[2]
		
		return dateArray;
	}
	
	/**
	 * 获取指定时间格式，指定月份的日期
	 * 
	 * @param startDate
	 * @param count
	 * @param dateFormat
	 * @return
	 * @throws Exception
	 */
	public static String getDate(String startDate,int count,String dateFormat) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = sdf.parse(startDate);//日期
		Calendar calendar = Calendar.getInstance();//日历对象
		calendar.setTime(date);//设置日期
		calendar.add(Calendar.MONTH, count);//+月份
		
		return sdf.format(calendar.getTime());
	}
	
	
	/**
	 * 获取指定日期的相差days的日期
	 * 
	 * @param dateStr
	 * @param formatStr
	 * @param days
	 * @return
	 */
	public static String getDateFromDays(String dateStr, String formatStr,
			int days) {
		String resDate = "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
			Date date = formatter.parse(dateStr);

			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			calendar.add(calendar.DATE, days);
			date = calendar.getTime(); // 
			
			resDate = formatter.format(date);

		} catch (ParseException e) {
			LogUtils.printStackTrace(e);
		}
		return resDate;
	}
	
	/**
	 * 比较两个时间
	 * 
	 * @param date1
	 * @param date2
	 * @param format
	 * @return
	 */
	public static int compareDate(String date1, String date2, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		try {
			Date dt1 = df.parse(date1);
			Date dt2 = df.parse(date2);
			if (dt1.getTime() > dt2.getTime()) {
				//System.out.println("dt1 在dt2前");
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				//System.out.println("dt1在dt2后");
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			LogUtils.printStackTrace(exception);
		}
		return 0;
	}

	public static String getTimestap(){

		String timestap = String.valueOf(calendar.get(Calendar.YEAR)).substring(2,4);
		int month = (calendar.get(Calendar.MONTH)+1);
		if(month < 10){
			timestap += "0" + (calendar.get(Calendar.MONTH)+1);
		}else{
			timestap += (calendar.get(Calendar.MONTH)+1);
		}
		int day = calendar.get(Calendar.DATE);
		if(day < 10){
			timestap += "0" + calendar.get(Calendar.DATE);
		}else{
			timestap += calendar.get(Calendar.DATE);
		}
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if(hour < 10){
			timestap += "0" + calendar.get(Calendar.HOUR_OF_DAY);
		}else{
			timestap += calendar.get(Calendar.HOUR_OF_DAY);
		}
		int min = calendar.get(Calendar.MINUTE);
		if(min < 10){
			timestap += "0" + calendar.get(Calendar.MINUTE);
		}else{
			timestap += calendar.get(Calendar.MINUTE);
		}
		int sec = calendar.get(Calendar.SECOND);
		System.out.println(calendar.get(Calendar.SECOND));
		if(sec < 10){
			timestap += "0" + calendar.get(Calendar.SECOND);
		}else{
			timestap += calendar.get(Calendar.SECOND);
		}
		return timestap;
	}

	
	/**
	 * 测试使用
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws Exception {

		System.out.println(compareDate("20150623","20150613","yyyyMMdd"));
		
	}

}
