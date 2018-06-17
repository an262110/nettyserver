package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备注册send
 * @author liuh
 * alt+shift+a 区域选择 取消也一样
 */
public final class Body0xA1 extends MegUtil implements BodyI 
{
   private byte[] body1_4;
   private byte[] body2_4;
   private byte[] body3_1;
   private byte[] body4_30;
   private byte[] body5_10;
   private byte[] body6_4;
   private byte[] body7_4;
   private byte[] body8_2;
   private byte[] body9_2;
   private byte[] body10_10;
   
   public final static int len = 71;
   
   public Body0xA1() {	  
	  		
		 body1_4  = fill0x00(hexStringToBytes("12345678"), 4);
		 body2_4  = fill0x00(hexStringToBytes("12345678"), 4);
		 body3_1  = new byte[]{0x12};  //高字节4位 0 交流 1 直流 0x12 表示 直流 双枪
		 body4_30 = fill0x00(30);
		 body5_10 = hexStringToBytes("2017012914295801");
		 body6_4  = intToBytes(104039154, 4, 1); 
		 body7_4  = intToBytes(104039154, 4, 1); 
		 body8_2  = new byte[]{0x03,0x11};  
		 body9_2  = new byte[]{0x01,0x02};
		 body10_10= fill0x00(10);
   }
   //通过字节数组实例化对象
   public Body0xA1(byte[] meg) {
		body1_4  = copyBytes(meg, 27, 4);
		body2_4  = copyBytes(meg, 31, 4);
		body3_1  = copyBytes(meg, 35, 1);
		body4_30 = copyBytes(meg, 36, 30);
		body5_10 = copyBytes(meg, 66, 10);
		body6_4  = copyBytes(meg, 76, 4);
		body7_4  = copyBytes(meg, 80, 4);
		body8_2  = copyBytes(meg, 84, 2);
		body9_2  = copyBytes(meg, 86, 2);
		body10_10= copyBytes(meg, 88, 10);
		
   }

   
//合并报文头和信息体
   public byte[] getByte() 
   {   
	   return appendByte(appendBytes());
   }
   
   public List<byte[]> appendBytes(){
	   List<byte[]> list = new ArrayList<byte[]>();
	   
	   list.add(body1_4);
	   list.add(body2_4);
	   list.add(body3_1);	   
	   list.add(body4_30);
	   list.add(body5_10);
	   list.add(body6_4);	   
	   list.add(body7_4);
	   list.add(body8_2);
	   list.add(body9_2);
	   list.add(body10_10);
	   
	   return list;
   }

@Override
public int getBodyLen() {
	return len;
}

public Map<String, String> bytesToMap(){
	Map<String, String> map = new HashMap<String, String>();
	map.put("body1_4", bytesToHexString(body1_4));
	map.put("body2_4", bytesToHexString(body2_4));
	map.put("body3_1", bytesToHexString(body3_1));
	map.put("body4_30", bytesToHexString(body4_30));
	map.put("body5_10", bytesToHexString(body5_10));
	map.put("body6_4", bytesToHexString(bytesReverseOrder(body6_4))+"");
	map.put("body7_4", bytesToHexString(bytesReverseOrder(body7_4))+"");
	map.put("body8_2", bytesToHexString(bytesReverseOrder(body8_2))+"");
	map.put("body9_2", bytesToHexString(bytesReverseOrder(body9_2))+"");
	map.put("body10_10", bytesToHexString(bytesReverseOrder(body10_10))+"");

	return map;
}
public byte[] getBody1_4() {
	return body1_4;
}
public void setBody1_4(byte[] body1_4) {
	this.body1_4 = body1_4;
}
public byte[] getBody2_4() {
	return body2_4;
}
public void setBody2_4(byte[] body2_4) {
	this.body2_4 = body2_4;
}
public byte[] getBody3_1() {
	return body3_1;
}
public void setBody3_1(byte[] body3_1) {
	this.body3_1 = body3_1;
}
public byte[] getBody4_30() {
	return body4_30;
}
public void setBody4_30(byte[] body4_30) {
	this.body4_30 = body4_30;
}
public byte[] getBody5_10() {
	return body5_10;
}
public void setBody5_10(byte[] body5_10) {
	this.body5_10 = body5_10;
}
public byte[] getBody6_4() {
	return body6_4;
}
public void setBody6_4(byte[] body6_4) {
	this.body6_4 = body6_4;
}
public byte[] getBody7_4() {
	return body7_4;
}
public void setBody7_4(byte[] body7_4) {
	this.body7_4 = body7_4;
}
public byte[] getBody8_2() {
	return body8_2;
}
public void setBody8_2(byte[] body8_2) {
	this.body8_2 = body8_2;
}
public byte[] getBody9_2() {
	return body9_2;
}
public void setBody9_2(byte[] body9_2) {
	this.body9_2 = body9_2;
}
public byte[] getBody10_10() {
	return body10_10;
}
public void setBody10_10(byte[] body10_10) {
	this.body10_10 = body10_10;
}

//发送前合并byte[] 封装
public ByteBuf getSendBuf(){
	   return obtainSendBuf(getByte());
}

}
