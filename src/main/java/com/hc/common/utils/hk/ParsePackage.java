package com.hc.common.utils.hk;

import com.hc.common.database.BaseJdbcDao;
import com.hc.spring.SpringApplicationContext;
import org.jpos.iso.ISOUtil;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParsePackage {
	public static int count=0;
	public static int other=0;
	public static Map<String,Object> parseHeader(byte[] req){
		 Map<String,Object> back=new LinkedHashMap<String,Object>();
		 Map<String,Object> result=new LinkedHashMap<String,Object>();
		 
		 /********A.标准数据*******************************************/
        //1.起始码STX
        byte[] stxArray=new byte[2];
        System.arraycopy(req,0,stxArray,0,2);
        result.put("STX", ISOUtil.hexString(stxArray));
        
        //2.应用ID APP_ID
        byte[] appIdArray=new byte[1];
        System.arraycopy(req,2,appIdArray,0,1);
        result.put("APP_ID", ISOUtil.hexString(appIdArray));
        
        //3.协议版本VER
        byte[] verArray=new byte[1];
        System.arraycopy(req,3,verArray,0,1);
        result.put("VER", ISOUtil.hexString(verArray));
        
        //4.数据长度LENGTH
        byte[] lenArray=new byte[4];
        System.arraycopy(req,4,lenArray,0,4);
        int LENGTH=ParseUtil.bytesToInt(lenArray);
        result.put("LENGTH",LENGTH);
        
        //5.消息类型MESSAGE_TYPE
        byte[] mtArray=new byte[2];
        System.arraycopy(req,8,mtArray,0,2);
        result.put("MESSAGE_TYPE",ParseUtil.bytesToInt(mtArray));
        
        //6.消息ID MESSAGE_ID
        byte[] midArray=new byte[4];
        System.arraycopy(req,10,midArray,0,4);
        result.put("MESSAGE_ID",ParseUtil.bytesToInt(midArray));
        
        //7.终止码ETX
        
        result.put("ETX", ISOUtil.hexString(Arrays.copyOfRange(req,req.length-2,req.length-1)));
        
        //8.校验码LRC
        result.put("LRC", ISOUtil.hexString(Arrays.copyOfRange(req,req.length-1,req.length)));
        
        byte[] DATA=Arrays.copyOfRange(req,14,req.length-2);
        
        back.put("data",DATA);
        back.put("head",result);
        back.put("LRC", result.get("LRC"));
        
        
        return back;
	}
	
	public static byte[] buildHeader(int messType,byte[] dataArray) throws NumberFormatException, Exception{
		return buildHeader(4,dataArray.length,messType,dataArray);
	}
	public static byte[] buildHeader(int appId,int dataLength,int messType,byte[] dataArray) throws NumberFormatException, Exception{
		other++;
		int messageId=other;
		if(messType==6100||messType==6200){
		  BaseJdbcDao baseDao = (BaseJdbcDao) SpringApplicationContext.getService("baseJdbcDao");
     	
		  count=Integer.valueOf(baseDao.querySequence("DN_MESSAGE_ID"));
		  messageId=count;
		}
		dataLength+=2;
		int size=14+dataLength;
		byte[] data = new byte[size];
		// 包头2字节STX
		data[0] = 0x7E;
		data[1] = 0x68;
		//应用ID APP_ID
		data[2] = (byte)((appId >>> 0) & 0xff);
		//VER
		data[3] = 0x01;
		// 数据长度4字节
		data[4] = (byte) ((dataLength >>> 24) & 0xff);
		data[5] = (byte) ((dataLength >>> 16) & 0xff);
		data[6] = (byte) ((dataLength >>> 8) & 0xff);
		data[7] = (byte) ((dataLength >>> 0) & 0xff);
		
		//消息类型21
		data[8] = (byte) ((messType >>> 8) & 0xff);
		data[9] = (byte) ((messType >>> 0) & 0xff);
		
		//消息ID MESSAGE_ID
		data[10] = (byte) ((messageId >>> 24) & 0xff);
		data[11] = (byte) ((messageId >>> 16) & 0xff);
		data[12] = (byte) ((messageId >>> 8) & 0xff);
		data[13] = (byte) ((messageId >>> 0) & 0xff);
		
		
		System.arraycopy(dataArray,0,data,14,dataArray.length);
		
		
		// 包尾2字节
		data[size-2] = (byte)0xcc;
		data[size-1] = ParsePackage.getEOR(dataArray);
		
		return data;
	}
	/**
	 * 添加控制头字节数据
	 * @param data 标准数据
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static byte[] buildControlHeader(byte[] data) throws UnsupportedEncodingException{
		/*
		 *这里我们固定控制头链路数据URL长度为4字节 0000
		 * 
		 */
		byte[] result=new byte[7+data.length];
		// 包头2字节STC
		result[0] = 0x7F;
		result[1] = 0x79;
		result[2] = (byte) ((4 >>> 0) & 0xff);
		
		byte[] url="0000".getBytes("ascii");
		System.arraycopy(url,0,result,3,url.length);
		
		System.arraycopy(data,0,result,7,data.length);
		
		return result;
		
	}
	
	/**
	 * 解析控制头
	 * @param req
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static Map parseControlHeader(byte[] req) throws UnsupportedEncodingException{
		Map<String,Object> back=new LinkedHashMap<String,Object>();
		 Map<String,Object> result=new LinkedHashMap<String,Object>();
		 
		 /********A.标准数据*******************************************/
       //1.起始码
       byte[] stxArray=new byte[2];
       System.arraycopy(req,0,stxArray,0,2);
       result.put("STC", ISOUtil.hexString(stxArray));
       
       //2.数据长度LENGTH
       byte[] lenArray=new byte[1];
       System.arraycopy(req,2,lenArray,0,1);
       int LENGTH=ParseUtil.bytesToInt(lenArray);
       result.put("LENGTH",LENGTH);
       
       //3.链路数据
       byte[] urlArray=new byte[LENGTH];
       System.arraycopy(req,3,urlArray,0,LENGTH);
       result.put("URL",new String(urlArray,"ascii"));
       
       //4.标准数据
       byte[] data=Arrays.copyOfRange(req,7,req.length);
       

		back.put("ordi_data",data);
        back.put("control_head",result);
        
        return back;
       
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException{
		String ss="7F7904303030307E6804010000000319650000003001CCCD";
		byte[] a= ISOUtil.hex2byte(ss);
		
		Map control=parseControlHeader(a);
		
		 byte[] ordi_data=(byte[])control.get("ordi_data");
		 
		 System.out.println(ISOUtil.hexString(ordi_data));
	}
	public static byte getEOR(byte[] data){
		byte r=0x00;
		for(byte b:data){
			r^=b;
		}
		r^=(byte)0xcc;
		return r;
	}
}
