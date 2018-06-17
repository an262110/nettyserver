package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Body0x11 extends MegUtil implements BodyI {
	private byte[] body1_1;
	private byte[] body2_1;
	private byte[] body3_4;
	private byte[] body4_5;
	private byte[] body5_10;
	private byte[] body6_1;
	private byte[] body7_17;
	private byte[] body8_1;
	private byte[] body9_4;
	private byte[] body10_4;
	private byte[] body11_2;
	private byte[] body12_3;
	private byte[] body13_1;
	private byte[] body14_10;
	
	private static final int len=64;
	
	public Body0x11() {
		
	}
	public Body0x11(byte[] msg){
		body1_1=copyBytes(msg,27,1);  
		body2_1=copyBytes(msg,28,1);  
		body3_4=copyBytes(msg,29,4);  
		body4_5=copyBytes(msg,33,5);
		body5_10=copyBytes(msg,38,10);
		body6_1=copyBytes(msg,48,1);  
		body7_17=copyBytes(msg,49,17); 
		body8_1=copyBytes(msg,66,1);  
		body9_4=copyBytes(msg,67,4);  
		body10_4=copyBytes(msg,71,4);  
		body11_2=copyBytes(msg,75,2); 
		body12_3=copyBytes(msg,77,3); 
		body13_1=copyBytes(msg,80,1); 
		body14_10=copyBytes(msg,81,10);
	}
	
	@Override
	//合并信息体
	   public byte[] getByte() 
	   {   
		   return appendByte(appendBytes());
	   }
	   
	   public List<byte[]> appendBytes(){
		   List<byte[]> list = new ArrayList<byte[]>();
		   list.add(body1_1);
		   list.add(body2_1);
		   list.add(body3_4);
		   list.add(body4_5);
		   list.add(body5_10);
		   list.add(body6_1);
		   list.add(body7_17);
		   list.add(body8_1);
		   list.add(body9_4);
		   list.add(body10_4);
		   list.add(body11_2);
		   list.add(body12_3);
		   list.add(body13_1);
		   list.add(body14_10);
		   return list;
	   }

	@Override
	public int getBodyLen() {
		return len;
	}

	@Override
	public Map<String, String> bytesToMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("body1_1", bytesToHexString(body1_1));  
		map.put("body2_1", bytesToHexString(body2_1)); 
		map.put("body3_4", BytesToint(bytesReverseOrder(body3_4))+""); 
		map.put("body4_5", BCDtointStr(body4_5)); 
		map.put("body5_10", BCDtointStr(body5_10)); 
		map.put("body6_1", bytesToHexString(body6_1)); 
		map.put("body7_17", bytesToHexString(body7_17)); 
		map.put("body8_1", bytesToHexString(body8_1)); 
		map.put("body9_4", bytesToHexString(body9_4)); 
		map.put("body10_4", bytesToHexString(body10_4)); 
		map.put("body11_2", bytesToHexString(body11_2)); 
		map.put("body12_3", bytesToHexString(body12_3)); 
		map.put("body13_1", bytesToHexString(body13_1)); 
		map.put("body14_10", bytesToHexString(body14_10)); 
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



	public byte[] getBody4_5() {
		return body4_5;
	}



	public void setBody4_5(byte[] body4_5) {
		this.body4_5 = body4_5;
	}



	public byte[] getBody5_10() {
		return body5_10;
	}



	public void setBody5_10(byte[] body5_10) {
		this.body5_10 = body5_10;
	}



	public byte[] getBody6_1() {
		return body6_1;
	}



	public void setBody6_1(byte[] body6_1) {
		this.body6_1 = body6_1;
	}



	public byte[] getBody7_17() {
		return body7_17;
	}



	public void setBody7_17(byte[] body7_17) {
		this.body7_17 = body7_17;
	}



	public byte[] getBody8_1() {
		return body8_1;
	}



	public void setBody8_1(byte[] body8_1) {
		this.body8_1 = body8_1;
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



	public byte[] getBody11_2() {
		return body11_2;
	}



	public void setBody11_2(byte[] body11_2) {
		this.body11_2 = body11_2;
	}



	public byte[] getBody12_3() {
		return body12_3;
	}



	public void setBody12_3(byte[] body12_3) {
		this.body12_3 = body12_3;
	}



	public byte[] getBody13_1() {
		return body13_1;
	}



	public void setBody13_1(byte[] body13_1) {
		this.body13_1 = body13_1;
	}



	public byte[] getBody14_10() {
		return body14_10;
	}



	public void setBody14_10(byte[] body14_10) {
		this.body14_10 = body14_10;
	}


}
