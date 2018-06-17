package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账单回复
 * @author liuh
 */
public final class Body0x55 extends MegUtil implements BodyI 
{
   private byte[] body1_7  ;
   private byte[] body2_7  ;
   private byte[] body3_10 ;
   private byte[] body4_4  ;
   private byte[] body5_4  ;
   private byte[] body6_4  ;
   private byte[] body7_4  ;
   private byte[] body8_4  ;
   private byte[] body9_4  ;
   private byte[] body10_4 ;
   private byte[] body11_4 ;
   private byte[] body12_1 ;
   private byte[] body13_10;
   
   public final static int len = 67;
   
   public Body0x55() {	  
//	      账单性质
//	   0x00:由桩开启充电的当前账单
//	   0x01:由平台开启充电的当前账单
//	   0x02:上一次未支付的账单
//	   0x03:由第三方系统开启充电的账单	
		 body1_7  = hexStringToBytes("20170129142958"); //充电开始时间
		 body2_7  = hexStringToBytes("20170129152958"); //充电结束时间
		 body3_10 = hexStringToBytes(getTradeNum());	//充电流水号
		 body4_4  = intToBytes(123456789, 4, 1);		//*充电前电表读数*100上送
		 body5_4  = intToBytes(123456799, 4, 1);		//*充电后电表读数
		 body6_4  = intToBytes(1000, 4, 1); 		//*本次充电电量
		 body7_4  = intToBytes(0, 4, 1); 		//*本次充电金额
		 body8_4  = intToBytes(0, 4, 1); 		//*充电前卡余额
		 body9_4  = intToBytes(0, 4, 1);		//*充电后卡余额
		 body10_4 = intToBytes(0, 4, 1);		//*服务费金额
		 body11_4 = intToBytes(0, 4, 1);		//*消费金额
		 body12_1 = new byte[]{0x30};									// 
		 body13_10 =fill0x00(10);									//
		 
		 //第一个字节表示充电口号，00表示A口，01表示B口……，其他字节预留
   }
   //通过字节数组实例化对象
   public Body0x55(byte[] meg) {
		body1_7  = copyBytes(meg, 27, 7);
		body2_7  = copyBytes(meg, 34, 7);
		body3_10 = copyBytes(meg, 41, 10);
		body4_4  = copyBytes(meg, 51, 4);
		body5_4  = copyBytes(meg, 55, 4);
		body6_4  = copyBytes(meg, 59, 4);
		body7_4  = copyBytes(meg, 63, 4);
		body8_4  = copyBytes(meg, 67, 4);
		body9_4  = copyBytes(meg, 71, 4);
		body10_4 = copyBytes(meg, 75, 4);
		body11_4 = copyBytes(meg, 79, 4);
		body12_1 = copyBytes(meg, 83, 1);
		body13_10= copyBytes(meg, 84, 10);
   }

   
//合并报文头和信息体
   public byte[] getByte() 
   {   
	   return appendByte(appendBytes());
   }
   
   public List<byte[]> appendBytes(){
	   List<byte[]> list = new ArrayList<byte[]>();
	   
	   list.add(body1_7);
	   list.add(body2_7);
	   list.add(body3_10);	   
	   list.add(body4_4);
	   list.add(body5_4);
	   list.add(body6_4);	   
	   list.add(body7_4);
	   list.add(body8_4);
	   list.add(body9_4);
	   list.add(body10_4);
	   list.add(body11_4);
	   list.add(body12_1);
	   list.add(body13_10);
	   
	   return list;
   }

@Override
public int getBodyLen() {
	return len;
}

public Map<String, String> bytesToMap(){
	Map<String, String> map = new HashMap<String, String>();
	map.put("body1_7", BCDtointStr(body1_7));//bytesToHexString
	map.put("body2_7", BCDtointStr(body2_7));
	map.put("body3_10", BCDtointStr(body3_10));
	map.put("body4_4", BytesToint(bytesReverseOrder(body4_4))+"");
	map.put("body5_4", BytesToint(bytesReverseOrder(body5_4))+"");
	map.put("body6_4", BytesToint(bytesReverseOrder(body6_4))+"");
	map.put("body7_4", BytesToint(bytesReverseOrder(body7_4))+"");
	map.put("body8_4", BytesToint(bytesReverseOrder(body8_4))+"");
	map.put("body9_4", BytesToint(bytesReverseOrder(body9_4))+"");
	map.put("body10_4", BytesToint(bytesReverseOrder(body10_4))+"");
	map.put("body11_4", BytesToint(bytesReverseOrder(body11_4))+"");
	map.put("body12_1", BytesToint(bytesReverseOrder(body12_1))+"");
	map.put("body13_10",BCDtointStr(body13_10));

	return map;
}

//发送前合并byte[] 封装
public ByteBuf getSendBuf(){
	   return obtainSendBuf(getByte());
}

public byte[] getBody1_7() {
	return body1_7;
}
public void setBody1_7(byte[] body1_7) {
	this.body1_7 = body1_7;
}
public byte[] getBody2_7() {
	return body2_7;
}
public void setBody2_7(byte[] body2_7) {
	this.body2_7 = body2_7;
}
public byte[] getBody3_10() {
	return body3_10;
}
public void setBody3_10(byte[] body3_10) {
	this.body3_10 = body3_10;
}
public byte[] getBody4_4() {
	return body4_4;
}
public void setBody4_4(byte[] body4_4) {
	this.body4_4 = body4_4;
}
public byte[] getBody5_4() {
	return body5_4;
}
public void setBody5_4(byte[] body5_4) {
	this.body5_4 = body5_4;
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
public byte[] getBody8_4() {
	return body8_4;
}
public void setBody8_4(byte[] body8_4) {
	this.body8_4 = body8_4;
}
public byte[] getBody9_4() {
	return body9_4;
}
public void setBody9_4(byte[] body9_4) {
	this.body9_4 = body9_4;
}
public byte[] getBody10_4() {
	return body10_4;
}
public void setBody10_4(byte[] body10_4) {
	this.body10_4 = body10_4;
}
public byte[] getBody11_4() {
	return body11_4;
}
public void setBody11_4(byte[] body11_4) {
	this.body11_4 = body11_4;
}
public byte[] getBody12_1() {
	return body12_1;
}
public void setBody12_1(byte[] body12_1) {
	this.body12_1 = body12_1;
}
public byte[] getBody13_10() {
	return body13_10;
}
public void setBody13_10(byte[] body13_10) {
	this.body13_10 = body13_10;
}



}
