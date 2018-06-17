package com.hc.app.utils;

import com.hc.common.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;


public class ParsePileData {
	
	public static Map parsePileData(String pileDataHex,String type){
		LogUtils.info("充电桩数据长度===="+pileDataHex.length());
		if("03".equals(type)){
			return parsePileData03(pileDataHex);
		}else if("01".equals(type)){
			return parsePileData01(pileDataHex);
		}else if("02".equals(type)){
			return parsePileData02(pileDataHex);
		}
		else
		{
			return null;
		}
	}
	/**
	 * 单相交流充电桩实时数据
	 * @param pileDataHex
	 * @return
	 */
   public  static Map parsePileData02(String pileDataHex){
	   Map result=new HashMap();
	   if(pileDataHex.length()!=52){
		   return null;
	   }else{
		 
		   result.put("PILE_DATA_1",pileDataHex.substring(0,2));//连接确认 0x00连接 0x01断开
		   /*
		    * 工作状态 0x01-故障
		    *  0x02-告警 03-空闲 0x04-充电中 
		    *  0x05-完成 0x06预约 0x07-等待 
		    */
		   result.put("PILE_DATA_2",pileDataHex.substring(2,4));
		   
		   result.put("PILE_DATA_3",pileDataHex.substring(4,6));//交流输入过压告警  布尔型 0不过压，1过压
		   result.put("PILE_DATA_4",pileDataHex.substring(6,8));//交流输入欠压告警
		   
		   
		   result.put("PILE_DATA_9",pileDataHex.substring(8,10));//充电电流过负荷告警  布尔型 0不过负荷，1过负荷
		   
		   result.put("PILE_DATA_10",Integer.valueOf(pileDataHex.substring(10,14),16).toString());//充电输出电压  单位：V,精确到小数点后一位
	       
		   result.put("PILE_DATA_11",Integer.valueOf(pileDataHex.substring(14,18),16).toString());//充电输出电流 单位：A,精确到小数点后二位
		   
		   
		   result.put("PILE_DATA_16",pileDataHex.substring(18,20));//输出继电器状态  布尔型 0关，1开
		   result.put("PILE_DATA_17",Integer.valueOf(pileDataHex.substring(20,28),16).toString());//有功总电度  单位：度,精确到小数点后二位
		   result.put("PILE_DATA_18",Integer.valueOf(pileDataHex.substring(28,32),16).toString());//累计充电时间  单位：min
		   result.put("PILE_DATA_19",pileDataHex.substring(32,34));//防雷器故障  布尔型 0正常 1故障
		   result.put("PILE_DATA_20",pileDataHex.substring(34,36));//急停按钮按下  布尔型 0正常 1按下
		   result.put("PILE_DATA_21",pileDataHex.substring(36,38));//接地故障   布尔型 0正常 1故障
		   result.put("PILE_DATA_22",pileDataHex.substring(38,40));//充电枪连接状态  布尔型 0未连接 1已连接
		   result.put("PILE_DATA_23",pileDataHex.substring(40,42));//车辆充电准备好状态  布尔型 0未准备好 1准备好 
		   result.put("PILE_DATA_24",pileDataHex.substring(42,44));//RTC时钟故障 布尔型 0正常 1故障
		   result.put("PILE_DATA_25",pileDataHex.substring(44,46));//电表通信故障 布尔型 0正常 1故障
		   result.put("PILE_DATA_26",pileDataHex.substring(46,48));//刷机通信故障 布尔型 0正常 1故障
		   result.put("PILE_DATA_27",pileDataHex.substring(48,50));//屏幕通信故障 布尔型 0正常 1故障
		   result.put("PILE_DATA_28",pileDataHex.substring(50,52));//交易记录满 0不满1记录满
		   
		   return result;
	   
	   }
   }
	/**
	 * 三相交流充电桩实时数据
	 * @param pileDataHex
	 * @return
	 */
   public  static Map parsePileData03(String pileDataHex){
	   Map result=new HashMap();
	   if(pileDataHex.length()!=76){
		   return null;
	   }else{
		 
		   result.put("PILE_DATA_1",pileDataHex.substring(0,2));//连接确认 0x00连接 0x01断开
		   /*
		    * 工作状态 0x01-故障
		    *  0x02-告警 03-空闲 0x04-充电中 
		    *  0x05-完成 0x06预约 0x07-等待 
		    */
		   result.put("PILE_DATA_2",pileDataHex.substring(2,4));
		   
		   result.put("PILE_DATA_3",pileDataHex.substring(4,6));//交流输入A相过压告警  布尔型 0不过压，1过压
		   result.put("PILE_DATA_4",pileDataHex.substring(6,8));//交流输入A相欠压告警
		   
		   result.put("PILE_DATA_5",pileDataHex.substring(8,10));//交流输入B相过压告警  布尔型 0不过压，1过压
		   result.put("PILE_DATA_6",pileDataHex.substring(10,12));//交流输入B相欠压告警
		   
		   result.put("PILE_DATA_7",pileDataHex.substring(12,14));//交流输入C相过压告警  布尔型 0不过压，1过压
		   result.put("PILE_DATA_8",pileDataHex.substring(14,16));//交流输入C相欠压告警
		   
		   result.put("PILE_DATA_9",pileDataHex.substring(16,18));//充电电流过负荷告警  布尔型 0不过负荷，1过负荷
		   
		   result.put("PILE_DATA_10",Integer.valueOf(pileDataHex.substring(18,22),16).toString());//充电输出A相电压  单位：V,精确到小数点后一位
	       
		   result.put("PILE_DATA_11",Integer.valueOf(pileDataHex.substring(22,26),16).toString());//充电输出A相电流 单位：A,精确到小数点后二位
		   
           result.put("PILE_DATA_12",Integer.valueOf(pileDataHex.substring(26,30),16).toString());//充电输出B相电压  单位：V,精确到小数点后一位
	       
		   result.put("PILE_DATA_13",Integer.valueOf(pileDataHex.substring(30,34),16).toString());//充电输出B相电流 单位：A,精确到小数点后二位
		   
           result.put("PILE_DATA_14",Integer.valueOf(pileDataHex.substring(34,38),16).toString());//充电输出C相电压  单位：V,精确到小数点后一位
	       
		   result.put("PILE_DATA_15",Integer.valueOf(pileDataHex.substring(38,42),16).toString());//充电输出C相电流 单位：A,精确到小数点后二位
		   
		   result.put("PILE_DATA_16",pileDataHex.substring(42,44));//输出继电器状态  布尔型 0关，1开
		   result.put("PILE_DATA_17",Integer.valueOf(pileDataHex.substring(44,52),16).toString());//有功总电度  单位：度,精确到小数点后二位
		   result.put("PILE_DATA_18",Integer.valueOf(pileDataHex.substring(52,56),16).toString());//累计充电时间  单位：min
		   result.put("PILE_DATA_19",pileDataHex.substring(56,58));//防雷器故障  布尔型 0正常 1故障
		   result.put("PILE_DATA_20",pileDataHex.substring(58,60));//急停按钮按下  布尔型 0正常 1按下
		   result.put("PILE_DATA_21",pileDataHex.substring(60,62));//接地故障   布尔型 0正常 1故障
		   result.put("PILE_DATA_22",pileDataHex.substring(62,64));//充电枪连接状态  布尔型 0未连接 1已连接
		   result.put("PILE_DATA_23",pileDataHex.substring(64,66));//车辆充电准备好状态  布尔型 0未准备好 1准备好 
		   result.put("PILE_DATA_24",pileDataHex.substring(66,68));//RTC时钟故障 布尔型 0正常 1故障
		   result.put("PILE_DATA_25",pileDataHex.substring(68,70));//电表通信故障 布尔型 0正常 1故障
		   result.put("PILE_DATA_26",pileDataHex.substring(70,72));//刷机通信故障 布尔型 0正常 1故障
		   result.put("PILE_DATA_27",pileDataHex.substring(72,74));//屏幕通信故障 布尔型 0正常 1故障
		   result.put("PILE_DATA_28",pileDataHex.substring(74,76));//交易记录满 0不满1记录满
		   
		   return result;
	   
	   }
   }
   
   /**
	 * 单枪直流充电桩实时数据
	 * @param pileDataHex
	 * @return
	 */
  public  static Map parsePileData01(String pileDataHex){
	   Map result=new HashMap();
	
		  
		   result.put("PILE_DATA_1",pileDataHex.substring(0,2));//连接确认 0x00连接 0x01断开
		   /*
		    * 工作状态 0x01-故障
		    *  0x02-告警 03-空闲 0x04-充电中 
		    *  0x05-完成 0x06预约 0x07-等待 
		    */
		   result.put("PILE_DATA_2",pileDataHex.substring(2,4));
		   
		   result.put("PILE_DATA_3",pileDataHex.substring(4,6));//数据保留
		   
		   result.put("PILE_DATA_4",Integer.valueOf(pileDataHex.substring(6,10),16).toString());//充电机输出电压 精确到小数点后一位
		   
		   result.put("PILE_DATA_5",Integer.valueOf(pileDataHex.substring(10,12),16).toString());//充电机输出电流 精确到小说点后二位
		   result.put("PILE_DATA_6",Integer.valueOf(pileDataHex.substring(12,14),16).toString());//SOC
		   
           result.put("PILE_DATA_10",Integer.valueOf(pileDataHex.substring(30,38),16).toString());//充电输出A相电压  单位：V,精确到小数点后一位
	       
		   result.put("PILE_DATA_11",Integer.valueOf(pileDataHex.substring(38,42),16).toString());//充电输出A相电流 单位：A,精确到小数点后二位
		   
		   return result;
	   
	   
  }
}
