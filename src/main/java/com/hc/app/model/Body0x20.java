package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Body0x20 extends MegUtil implements BodyI {
	private byte[] body1_1;
	private byte[] body2_10;
	private byte[] body3_2;
	

	private static final int len=13;
	
	public Body0x20() {
		body1_1 = new byte[]{0x00};  //充电口
		body2_10 = hexStringToBytes("201701191425120003");  //交易流水号
		body3_2 = new byte[]{0x00,0x10};  //等待时间
	}
	public Body0x20(String body2_10) {
		body1_1 = new byte[]{0x00};  //充电口
		this.body2_10 = hexStringToBytes(body2_10);  //交易流水号
		body3_2 = new byte[]{0x00,0x10};  //等待时间
	}
	public Body0x20(byte[] msg){
		body1_1=copyBytes(msg,27,1);  
		body2_10=copyBytes(msg,28,10);  
		body3_2=copyBytes(msg,38,2);  
	}
	public Body0x20(byte body1_1,String body2_10) {
		this.body1_1 = new byte[]{body1_1};  //充电口
		this.body2_10 = hexStringToBytes(body2_10);  //交易流水号
		body3_2 = new byte[]{0x00,0x10};  //等待时间
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
		   list.add(body2_10);
		   list.add(body3_2);
		   
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
		map.put("body2_10", bytesToHexString(body2_10)); 
		map.put("body3_2", BytesToint(bytesReverseOrder(body3_2))+""); 
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
	public byte[] getBody2_10() {
		return body2_10;
	}
	public void setBody2_10(byte[] body2_10) {
		this.body2_10 = body2_10;
	}
	public byte[] getBody3_2() {
		return body3_2;
	}
	public void setBody3_2(byte[] body3_2) {
		this.body3_2 = body3_2;
	}

	public void setBody1_1_from_int(int gun) {
		body1_1 = intToBytes(gun,1,0);
	}

	

}
