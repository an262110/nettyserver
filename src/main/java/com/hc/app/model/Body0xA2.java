package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备注册revi
 * @author liuh
 * alt+shift+a 区域选择 取消也一样
 */
public final class Body0xA2 extends MegUtil implements BodyI 
{
   private byte[] body1_2 ;
   private byte[] body2_8 ;
   private byte[] body3_8 ;
   private byte[] body4_1 ;
   private byte[] body5_1 ;
   private byte[] body6_1 ;
   private byte[] body7_2 ;
   private byte[] body8_10;
   
   public final static int len = 33;
   
   public Body0xA2() {	  
	  		
		 body1_2  = fill0x00(hexStringToBytes("1001"), 2);
		 body2_8  = fill0x00(8);
		 body3_8  = fill0x00(8);
		 body4_1  = new byte[]{0x30}; 
		 body5_1  = new byte[]{0x30}; 
		 body6_1  = new byte[]{0x30}; 
		 body7_2  = new byte[]{0x03,0x11}; 
		 body8_10 = fill0x00(10);
   }
   public Body0xA2(Head head) {	  
 		
		 body1_2  = fill0x00(hexStringToBytes("1001"), 2);
		 body2_8  = head.getHead7_8();
		 body3_8  = fill0x00(8);
		 body4_1  = new byte[]{0x30}; 
		 body5_1  = new byte[]{0x30}; 
		 body6_1  = new byte[]{0x30}; 
		 body7_2  = new byte[]{0x03,0x11}; 
		 body8_10 = fill0x00(10);
 }
   //通过字节数组实例化对象
   public Body0xA2(byte[] meg) {
		 body1_2  =  copyBytes(meg, 27, 2);
		 body2_8  =  copyBytes(meg, 29, 8);
		 body3_8  =  copyBytes(meg, 37, 8);
		 body4_1  =  copyBytes(meg, 45, 1);
		 body5_1  =  copyBytes(meg, 46, 1);
		 body6_1  =  copyBytes(meg, 47, 1);
		 body7_2  =  copyBytes(meg, 48, 2);
		 body8_10 =  copyBytes(meg, 50, 10);
   }

   
//合并报文头和信息体
   public byte[] getByte() 
   {   
	   return appendByte(appendBytes());
   }
   
   public List<byte[]> appendBytes(){
	   List<byte[]> list = new ArrayList<byte[]>();
	   
	   list.add(body1_2);
	   list.add(body2_8);
	   list.add(body3_8);	   
	   list.add(body4_1);
	   list.add(body5_1);
	   list.add(body6_1);	   
	   list.add(body7_2);
	   list.add(body8_10);
	   
	   return list;
   }

@Override
public int getBodyLen() {
	return len;
}

public Map<String, String> bytesToMap(){
	Map<String, String> map = new HashMap<String, String>();
	map.put("body1_2", BCDtointStr(body1_2));
	map.put("body2_8", BCDtointStr(body2_8));
	map.put("body3_8", BCDtointStr(body3_8));
	map.put("body4_1", BytesToint(bytesReverseOrder(body4_1))+"");
	map.put("body5_1", BytesToint(bytesReverseOrder(body5_1))+"");
	map.put("body6_1", BytesToint(bytesReverseOrder(body6_1))+"");
	map.put("body7_2", BCDtointStr(body7_2));
	map.put("body8_10", BCDtointStr(body8_10));

	return map;
}
public byte[] getBody1_2() {
	return body1_2;
}
public void setBody1_2(byte[] body1_2) {
	this.body1_2 = body1_2;
}
public byte[] getBody2_8() {
	return body2_8;
}
public void setBody2_8(byte[] body2_8) {
	this.body2_8 = body2_8;
}
public byte[] getBody3_8() {
	return body3_8;
}
public void setBody3_8(byte[] body3_8) {
	this.body3_8 = body3_8;
}
public byte[] getBody4_1() {
	return body4_1;
}
public void setBody4_1(byte[] body4_1) {
	this.body4_1 = body4_1;
}
public byte[] getBody5_1() {
	return body5_1;
}
public void setBody5_1(byte[] body5_1) {
	this.body5_1 = body5_1;
}
public byte[] getBody6_1() {
	return body6_1;
}
public void setBody6_1(byte[] body6_1) {
	this.body6_1 = body6_1;
}
public byte[] getBody7_2() {
	return body7_2;
}
public void setBody7_2(byte[] body7_2) {
	this.body7_2 = body7_2;
}
public byte[] getBody8_10() {
	return body8_10;
}
public void setBody8_10(byte[] body8_10) {
	this.body8_10 = body8_10;
}

//发送前合并byte[] 封装
public ByteBuf getSendBuf(){
	   return obtainSendBuf(getByte());
}

}
