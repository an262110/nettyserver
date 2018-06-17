package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author liuh
 * final 最终类，禁止扩展
 */
public final class Body0x10 extends MegUtil implements BodyI {
	private byte[] body1_1;
	private byte[] body2_1;
	private byte[] body3_4;
	private byte[] body4_5;
	private byte[] body5_10;

	private static final int len=21;
	
	public Body0x10() {
		
	}
	
	public Body0x10(byte[] body1_1,byte body2_1,byte[] body5_10){
		this.body1_1=body1_1;  
		this.body2_1=new byte[]{body2_1};  
		this.body3_4=new byte[]{0x00,0x00,0x00,0x00};  
		this.body4_5=new byte[]{0x00,0x00,0x00,0x00,0x00};
		this.body5_10=body5_10;
	}
	
	public Body0x10(byte[] msg){
		body1_1=copyBytes(msg,27,1);  
		body2_1=copyBytes(msg,28,1);  
		body3_4=copyBytes(msg,29,4);  
		body4_5=copyBytes(msg,33,5);
		body5_10=copyBytes(msg, 38, 10);
		
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

}
