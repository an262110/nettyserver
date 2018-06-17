package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *<p>title : 第三方系统下发充电停止指令</p>
 *<p>Description : </p>
 *<p>Company : 广州爱电牛科技有限公司</p>
 * @date 2017年2月6日
 * @author 小吴
 */
public final class Body0x12 extends MegUtil implements BodyI {

	private byte[] body1_1; 
	private byte[] body2_10;
	private byte[] body3_1;
	private byte[] body4_5;
	private static final int len = 17; 
	public Body0x12(){
		String date = getSerialNum().substring(4);
		setBody4_5(intStrToBCD(date));
	}
	
	public Body0x12(String body2_10) {
		body1_1 = new byte[]{0x00};
		this.body2_10 = hexStringToBytes(body2_10);
		this.body3_1 = new byte[0x04];
		this.body4_5 = new byte[]{0x02,0x27,0x18,0x25,0x39};
	}
	
	public Body0x12(byte[] body1_1, byte[] body2_10, byte[] body3_1) {
		this.body1_1 = body1_1;
		this.body2_10 = body2_10;
		this.body3_1 = body3_1;
		//this.body4_5 = intStrToBCD(getCurNum());  //MMddHHmmss
		this.body4_5 = new byte[]{0x02,0x27,0x18,0x25,0x39};
	}



	public Body0x12(byte[] msg){
		body1_1 = copyBytes(msg, 27, 1);
	    body2_10 = copyBytes(msg, 28, 10);
 	    body3_1 = copyBytes(msg, 38, 1);
	    body4_5 = copyBytes(msg, 39, 5);
	}
	
	private List<byte[]> addByte(){
		List<byte[]> list = new ArrayList<byte[]>();
		list.add(body1_1);
		list.add(body2_10);
		list.add(body3_1);
		list.add(body4_5);
		return list;
	}
	@Override
	public byte[] getByte() {
		// TODO Auto-generated method stub
		return appendByte(addByte());
	}

	@Override
	public int getBodyLen() {
		// TODO Auto-generated method stub
		return len;
	}

	@Override
	public Map<String, String> bytesToMap() {
		// TODO Auto-generated method stub
		Map<String,String> map = new HashMap<String,String>();
		map.put("body1_1",bytesToHexString(body1_1));
		map.put("body2_10", BCDtointStr(body2_10));
		map.put("body3_1", bytesToHexString(body3_1));
		map.put("body4_5", BCDtointStr(body4_5));
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

	
}
