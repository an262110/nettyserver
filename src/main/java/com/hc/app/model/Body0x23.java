package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *<p>title :返回取消充电请求</p>
 *<p>Description : </p>
 *<p>Company : 广州爱电牛科技有限公司</p>
 * @date 2017年2月6日
 * @author 小吴
 */
public final class Body0x23 extends MegUtil implements BodyI {

	private byte[] body1_1;
	private byte[] body2_10;
	private byte[] body3_1;
	private static final int len = 12;
	
	public Body0x23(byte[] data) {
		// TODO Auto-generated constructor stub
		body1_1 = copyBytes(data, 27, 1);
		body2_10 = copyBytes(data, 28, 10);
		body3_1 = copyBytes(data, 38, 1);
	}
	
	private List<byte[]> addByte(){
		List<byte[]> list = new ArrayList<byte[]>();
		list.add(body1_1);
		list.add(body2_10);
		list.add(body3_1);
		return list;
	}
	
	
	@Override
	public byte[] getByte() {
		return appendByte(addByte());
	}

	@Override
	public int getBodyLen() {
		return len;
	}

	@Override
	public Map<String, String> bytesToMap() {
		Map<String,String> map = new HashMap<String,String>();
		map.put("body1_1", bytesToHexString(body1_1));
		map.put("body2_10",BCDtointStr(body2_10));
		map.put("body3_1", bytesToHexString(body3_1));
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

}
