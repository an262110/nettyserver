package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Body0x59 extends MegUtil implements BodyI {
	private byte[] body1_1;
	private byte[] body2_10;

	
	public static final int len=11;
	
	public Body0x59(byte[] msg){
		 body1_1=copyBytes(msg,27,1); 
		 body2_10=copyBytes(msg,28,10);
	
	}
	public Body0x59(Body0x58 body){
		 body1_1=body.getBody1_1();
		 body2_10=body.getBody2_10();
	
	}
	
	@Override
	public byte[] getByte() {
		 return appendByte(appendBytes());
	}
	public List<byte[]> appendBytes(){
		   List<byte[]> list = new ArrayList<byte[]>();
		   list.add(body1_1);
		   list.add(body2_10);
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

	

	
}
