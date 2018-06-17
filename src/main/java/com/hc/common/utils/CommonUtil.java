package com.hc.common.utils;

import com.hc.common.config.AppConfig;
import org.apache.commons.lang.ArrayUtils;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.GenericPackager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class CommonUtil {
  public static String ISO8583_XML = AppConfig.getMessage("mpos.ISO8583.xml");
  public static String ISO8583_XML_REPO = AppConfig.getMessage("IDN.ISO8583.REPO.xml");
  public static String XML_LOG=AppConfig.getMessage("XML_LOG");
  private static final String PREFIX=AppConfig.getMessage("mpos.ISO8583.dir");
  public static String getConfigPath(String txcode,String type){
	  String filename=type+txcode+".xml";
	  String fp=PREFIX+filename;
	  File f=new File(fp);
	  LogUtils.info(">>>>>>>>>>>>pathorg====="+fp);
	  if(f.exists()){
		  LogUtils.info(">>>>>>>>>>>>path====="+f.getAbsolutePath());
		  return fp;
	  }else{
		  LogUtils.info(">>>>>>>>>>>>path2====="+f.getAbsolutePath());
		  return "IN".equals(type)?ISO8583_XML:ISO8583_XML_REPO ;
	  }
  }
  
  public static ISOMsg parsePackage(byte[] req) throws Exception{
	  //1.获取交易码
      byte[] txcodeArray=new byte[4];
      System.arraycopy(req,8,txcodeArray,0,4);
      
      String txcode=new String(txcodeArray,"ascii");
      System.arraycopy(req,8,txcodeArray,0,4);
	  //解析报文         
      ISOPackager packager = new GenericPackager(CommonUtil.getConfigPath(txcode,"IN"));
      byte[] content=new byte[req.length-8];//报文内容：包括4字节协议码、8字节位图、数据字节
      byte[] header=new byte[8];//报文头：前两个字节是数据长度（按字节）
      ISOMsg msgInfo = new ISOMsg();
      
      System.arraycopy(req,8,content,0, req.length-8);
      System.arraycopy(req,0,header,0,8);
      
      msgInfo.setPackager(packager);
      
      msgInfo.unpack(content);
      msgInfo.setHeader(header);
      
      dumpInfo(msgInfo);
      
      return msgInfo;
  }
  
  public static void dumpInfo(ISOMsg msg){
	  PrintStream is;
	try {
		is = new PrintStream(new FileOutputStream(XML_LOG,true));
		 msg.dump(System.out, ""); // 打印成xml报文的格式
	      msg.dump(is, ""); // 打印成xml报文的格式到输出文件
	      is.close();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
  }
  
  
 public static String buildString(String s,int length){
	 if(s.length()<length){
		 StringBuilder fix=new StringBuilder();
		 for(int i=0;i<length-s.length();i++){
			 fix.append("0");
		 }
		 
		 s=fix.toString()+s;
	 }   
	 
	 return s;
 	}
 
 public static String buildStringRight(String s,int length){
	 if(s.length()<length){
		 StringBuilder fix=new StringBuilder();
		 for(int i=0;i<length-s.length();i++){
			 fix.append("0");
		 }
		 
		 s=s+fix.toString();
	 }   
	 
	 return s;
 	}
 
 public static String infoCodeDesc(String code){
	 //00：成功；01：MAC校验失败，02：充电桩序列号不对应，03：未插枪，04：充电桩已被使用，无法充电，
	 //05：非当前用户，无法取消充电, 06：充电桩故障无法充电,07:充电桩忙, 08:余额不足，09:车辆未准备好
	 Map<String,String> map = new HashMap<String, String>();
		 
	 map.put("00", "成功");
	 map.put("01", "MAC校验失败");
	 map.put("02", "充电桩序列号不对应");
	 map.put("03", "未插枪");
	 map.put("04", "充电桩已被使用，无法充电");
	 map.put("05", "非当前用户，无法取消充电");
	 map.put("06", "充电桩故障无法充电");
	 map.put("07", "充电桩忙");
	 map.put("08", "余额不足");
	 map.put("09", "车辆未准备好");
	 if(map.get(code)==null){
		 return "";
	 }
	 return map.get(code);
 }
 
 public static String toBinaryString(String hexString){
	 return buildString(Integer.toBinaryString(Integer.valueOf(hexString,16)),16);
 }
 
 public static byte[] toByteArray(int iSource, int iArrayLen) {
	    
	    int len=4;
	    if(iArrayLen<4){
	    	len=iArrayLen;
	    }
	    byte[] bLocalArr = new byte[len];
	    for (int i = len-1,j=0;i>=0 ; i--,j++) {
	        bLocalArr[i] = (byte) (iSource >> 8 * j & 0xFF);
	    }
	    return bLocalArr;
	}
 
 public static byte[] addLength(byte[] content){
	 int len=content.length+2;
	 byte[] lenByte=toByteArray(len,2);
	 byte[] both = (byte[]) ArrayUtils.addAll(lenByte,content);
	 
	 return both;
 }
}
