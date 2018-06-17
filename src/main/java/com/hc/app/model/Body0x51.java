package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 心跳报文revi
 * @author liuh
 * alt+shift+a 区域选择 取消也一样
 */
public final class Body0x51 extends MegUtil implements BodyI 
{
   private byte[] body1_1 ;
   private byte[] body2_1 ;
   private byte[] body3_4 ;
   private byte[] body4_10 ;
   
   private String body1_1_str;
   private String body2_1_str;

   
   public static final int len = 16;
   
   public Body0x51() {	  
	  		
	     //高4位：表示充电口 0-A口，1-B口，2-C口……
	     //低4位：表示该口状态：0- 空闲 1- 充电 2- 预约
	     body1_1   = new byte[]{0x00};  //桩状态
		 body2_1   = new byte[]{0x00};  //枪状态
		 body3_4   = fill0x00(4);  //故障码
		 body4_10  = fill0x00(10);  
   }
   //通过字节数组实例化对象
   public Body0x51(byte[] meg) {
		 body1_1   =  copyBytes(meg, 27, 1);
		 body2_1   =  copyBytes(meg, 28, 1);
		 body3_4   =  copyBytes(meg, 29, 4);
		 body4_10  =  copyBytes(meg, 33, 10);
		 
		 //body1_1_str = bytesToBits(body1_1);     //充电口状态字符串
		 body1_1_str = bytesToHexString(body1_1);  //充电口状态字符串 16进制字符串，高4位 与低4位
		 body2_1_str = bytesToBits(body2_1);      //充电枪状态字符串
   }

   
//合并报文头和信息体
   public byte[] getByte() 
   {   
	   return appendByte(appendBytes());
   }
   
   public List<byte[]> appendBytes(){
	   List<byte[]> list = new ArrayList<byte[]>();
	   
	   list.add(body1_1);
	   list.add(body2_1);
	   list.add(body3_4);	   
	   list.add(body4_10);

	   
	   return list;
   }

@Override
public int getBodyLen() {
	return len;
}

public Map<String, String> bytesToMap(){
	Map<String, String> map = new HashMap<String, String>();
	map.put("body1_1", bytesToHexString(bytesReverseOrder(body1_1))+"");
	map.put("body2_1", BytesToint(bytesReverseOrder(body2_1))+"");
	map.put("body3_4", bytesToHexString(body3_4));
	map.put("body4_10", bytesToHexString(body4_10));

	return map;
}

//发送前合并byte[] 封装
public ByteBuf getSendBuf(){
	   return obtainSendBuf(getByte());
}

public byte[] getBody1_1() {
	return body1_1;
}
public void setBody1_1(byte[] body1_1) {
	this.body1_1 = body1_1;
}
public byte[] getBody2_1() {
	return body2_1;
}
public void setBody2_1(byte[] body2_1) {
	this.body2_1 = body2_1;
}
public byte[] getBody3_4() {
	return body3_4;
}
public void setBody3_4(byte[] body3_4) {
	this.body3_4 = body3_4;
}
public byte[] getBody4_10() {
	return body4_10;
}
public void setBody4_10(byte[] body4_10) {
	this.body4_10 = body4_10;
}
public String getBody1_1_str() {
	return body1_1_str;
}
public void setBody2_1_str(String body1_1_str) {
	this.body1_1_str = body1_1_str;
}
public String getBody2_1_str() {
	return body2_1_str;
}
public void setBody1_1_str(String body1_1_str) {
	this.body1_1_str = body1_1_str;
}


}
