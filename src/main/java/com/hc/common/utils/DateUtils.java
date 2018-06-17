package com.hc.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 时间工具类
 * 
 * @author Z
 *
 */
public class DateUtils {
	
	/**
	 * Calendar类,24小时制,全日期格式
	 */
	public static final String CALENDAR_FULL_FORMAT = "yyyy-MM-dd kk:mm:ss";
	
	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getCurrentTime(){
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = formatter.format(new Date());
		
		return currentTime;
	}
	
	/**
	 * 获取今日的日期字符串
	 * @return
	 */
	public static String getTodayStr(boolean needBegin){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String currentDate = formatter.format(new Date());
		if(needBegin){
			String beginTime = " 00:00:01";
			currentDate+= beginTime;
			return currentDate;
		}else {
			String endTime = " 23:59:59";
			currentDate+= endTime;
			return currentDate;
		}
	}
	
	/**
	 * 获取当月的起始或结束日期字符串
	 * @param needBegin 	true - 获取起始  false - 获取结束
	 * @return
	 */
	public static String getCurMonthStr(boolean needBegin){
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		if (needBegin) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, 0);
			c.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
			String first = format.format(c.getTime());
			String beginTime = " 00:00:01";
			return first + beginTime;
		} else {

			// 获取当前月最后一天
			Calendar ca = Calendar.getInstance();
			ca.set(Calendar.DAY_OF_MONTH,
					ca.getActualMaximum(Calendar.DAY_OF_MONTH));
			String last = format.format(ca.getTime());
			String endTime = " 23:59:59";
			return last + endTime;
		}
	}
	
	/**
	 * 获取给定格式的，当前日期
	 */
	public static String getCurrentDate(String format){
		
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String currentTime = formatter.format(new Date());
		
		return currentTime;
	}
	
	/**
	 * 获取当前月份
	 * 
	 * @return
	 */
	public static String getCurrentMonth(){
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		String currentTime = formatter.format(new Date());
		
		return currentTime;
	}
	
    /**
     * 获取当前时间的time long
     * 
     * @return
     */
	public static synchronized String getCurrentTimeSequence(){
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			
			LogUtils.printStackTrace(e);
		}
		return String.valueOf(new Date().getTime());
	}

/*	
	 * 根据自定义格式化取日期
	 
	public static String getDateFormat(String dateStr,String formatStr) throws Exception{
		
		SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
		Date date = formatter.parse(dateStr);
		
		return formatter.format(date);
	}*/
	
	/**
	 * 将目标日期字符串转换为制定格式
	 * 
	 * @param dateStr
	 *            目标日期字符串
	 * @param formatStr
	 *            原日期字符串格式
	 * @param targetStr
	 *            目标格式
	 * @return 返回转换为目标格式的日期字符串
	 * @throws Exception
	 */
	public static String getDateFormat(String dateStr, String formatStr,
			String targetStr) throws Exception {

		SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
		Date date = formatter.parse(dateStr);
		SimpleDateFormat targetFormatter = new SimpleDateFormat(targetStr);
		return targetFormatter.format(date);
	}
	
	/**
	 * 获取当前时间作为极光推送消息的sendNo
	 * @author jacky
	 */
	public static String getCurrentSendNo(){
		
		SimpleDateFormat formatter = new SimpleDateFormat("MMddhhmmss");
		String currentTime = formatter.format(new Date());
		
		return currentTime;
	}
	
	/**
	 * 获取两个日期间相差天数
	 * 
	 * @param time1
	 * @param time2
	 * @param dateFormatter
	 * @return
	 */
	public static int getDaysBetween(String startDate, String endDate,
			String dateFormatter) {
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat(dateFormatter);
		try {

			Date end = ft.parse(endDate);
			Date start = ft.parse(startDate);

			quot = end.getTime() - start.getTime();
			quot = quot / 1000 / 60 / 60 / 24;

		} catch (ParseException e) {
			
			LogUtils.printStackTrace(e);
		}
		return (int)quot;
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
	 * 根据月份获取yyyy-MM
	 * 
	 * @return
	 */
	public static String getYearAndMonth(String month) {
		Date date;
		String year = "";
		String monthStr = "";
		try {
			SimpleDateFormat yearformatter = new SimpleDateFormat("yyyy");
			year = yearformatter.format(new Date());

			SimpleDateFormat monthFormatter = new SimpleDateFormat("MM");

			date = monthFormatter.parse(month);
			monthStr = monthFormatter.format(date);
			
		} catch (ParseException e) {
			
			LogUtils.printStackTrace(e);
		}

		return year + "-" + monthStr;
	}
	
	/**
	 * 在给定格式的当前日期基础上进行时间增减
	 * @param dateFormat		指定时间格式  !—— 注意Calendar类  kk 代表24小时制的小时,而不是hh!
	 * @param yOrMOrd			使用常量  Calendar.MONTH等			
	 * @param num				增减数
	 * @return	String			返回增减后的日期字符串
	 * @throws ParseException
	 */
	public static String calculateCalendar(String dateFormat,int yOrMOrd,int num) throws ParseException{
		
		SimpleDateFormat sdf=new SimpleDateFormat(dateFormat);
	    String str= getCurrentDate(dateFormat);
	    Date dt = sdf.parse(str);
	    Calendar rightNow = Calendar.getInstance();
	    rightNow.setTime(dt);
	    rightNow.add(yOrMOrd,num);//日期增减
	    Date dt1=rightNow.getTime();
	    String reStr = sdf.format(dt1);
	    return reStr;
	}
	
	/**
	 * 在给定格式的日期基础上进行时间增减
	 * @param targetDateStr		目标日期
	 * @param dateFormat		指定时间格式  !—— 注意Calendar类  kk 代表24小时制的小时,而不是hh!
	 * @param yOrMOrd			使用常量  Calendar.MONTH等			
	 * @param num				增减数
	 * @return	String			返回增减后的日期字符串
	 * @throws ParseException
	 */
	public static String calculateCalendar(String targetDateStr,String dateFormat,int yOrMOrd,int num) throws ParseException{
		
		SimpleDateFormat sdf=new SimpleDateFormat(dateFormat);
	    Date dt = sdf.parse(targetDateStr);
	    Calendar rightNow = Calendar.getInstance();
	    rightNow.setTime(dt);
	    rightNow.add(yOrMOrd,num);//日期增减
	    Date dt1=rightNow.getTime();
	    String reStr = sdf.format(dt1);
	    return reStr;
	}

	/**
	 * 测试使用
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
		System.out.println(calculateCalendar("MM-dd", Calendar.DAY_OF_MONTH, 1));
	}

}
