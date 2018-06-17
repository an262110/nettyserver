package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Body0x21 extends MegUtil implements BodyI {
	private byte[] body1_1;
	private byte[] body2_10;
	private byte[] body3_1;
	private byte[] body4_10;

	private static final int len = 22;

	public Body0x21() {

	}

	public Body0x21(byte[] msg) {
		body1_1 = copyBytes(msg, 27, 1);
		body2_10 = copyBytes(msg, 28, 10);
		body3_1 = copyBytes(msg, 38, 1);
		body4_10 = copyBytes(msg, 39, 10);
	}

	@Override
	// 合并信息体
	public byte[] getByte() {
		return appendByte(appendBytes());
	}

	public List<byte[]> appendBytes() {
		List<byte[]> list = new ArrayList<byte[]>();
		list.add(body1_1);
		list.add(body2_10);
		list.add(body3_1);
		list.add(body4_10);
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
		map.put("body2_10", BCDtointStr(body2_10));
		map.put("body3_1", bytesToHexString(body3_1));
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

	public byte[] getBody4_10() {
		return body4_10;
	}

	public void setBody4_10(byte[] body4_10) {
		this.body4_10 = body4_10;
	}

}
