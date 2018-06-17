package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Body0x58 extends MegUtil implements BodyI {
	private byte[] body1_1;
	private byte[] body2_10;
	private byte[] body3_1;
	private byte[] body4_5;
	private byte[] body5_2;
	private byte[] body6_1;
	
	public static final int len=20;
	
	public Body0x58(byte[] msg){
		 body1_1=copyBytes(msg,27,1); 
		 body2_10=copyBytes(msg,28,10);
		 body3_1=copyBytes(msg,38,1); 
		 body4_5=copyBytes(msg,39,5); 
		 body5_2=copyBytes(msg,44,2); 
		 body6_1=copyBytes(msg,46,1); 
	}
	
	@Override
	public byte[] getByte() {
		 return appendByte(appendBytes());
	}
	public List<byte[]> appendBytes(){
		   List<byte[]> list = new ArrayList<byte[]>();
		   list.add(body1_1);
		   list.add(body2_10);
		   list.add(body3_1);	   
		   list.add(body4_5);
		   list.add(body5_2);
		   list.add(body6_1);
		   return list;
	   }

	@Override
	public int getBodyLen() {
		return len;
	}

	@Override
	public Map<String, String> bytesToMap() {
		Map<String,String> map=new HashMap<String,String>();
		map.put("body1_1", bytesToHexString(body1_1));
		map.put("body2_10", bytesToHexString(body2_10));
		map.put("body3_1", bytesToHexString(body3_1));
		map.put("body4_5", bytesToHexString(body4_5));
		map.put("body5_2", BytesToint(body5_2)+"");
		map.put("body6_1", BytesToint(body6_1)+"");
		return null;
	}

	@Override
	public ByteBuf getSendBuf() {
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

	public byte[] getBody3_1() {
		return body3_1;
	}

	public void setBody3_1(byte[] body3_1) {
		this.body3_1 = body3_1;
	}

	public byte[] getBody4_5() {
		return body4_5;
	}

	public void setBody4_5(byte[] body4_5) {
		this.body4_5 = body4_5;
	}

	public byte[] getBody5_2() {
		return body5_2;
	}

	public void setBody5_2(byte[] body5_2) {
		this.body5_2 = body5_2;
	}

	public byte[] getBody6_1() {
		return body6_1;
	}

	public void setBody6_1(byte[] body6_1) {
		this.body6_1 = body6_1;
	}


	
}
