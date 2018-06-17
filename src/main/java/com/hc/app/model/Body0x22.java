package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 *<p>title :第三方系统下令取消充电请求</p>
 *<p>Description : </p>
 *<p>Company : 广州爱电牛科技有限公司</p>
 * @date 2017年2月6日
 * @author 小吴
 */
public final class Body0x22 extends MegUtil implements BodyI {

	private byte[] body1_1;
	private byte[] body2_10;
	private static final int len = 11;
	
	public Body0x22(byte[] meg){
		body1_1 = copyBytes(meg,27, 1);
		body2_10 = copyBytes(meg,28, 10);
	}
	
	public Body0x22() {
		body1_1 = new byte[]{0x00};
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = format.format(new Date());
		body2_10 = hexStringToBytes(date+"12345678");
	}
	
	public Body0x22(String body2_10) {
		body1_1 = new byte[]{0x00};
		this.body2_10 = hexStringToBytes(body2_10);
	}
	
	public Body0x22(byte[] body1_1, String body2_10) {
		this.body1_1 = body1_1;
		this.body2_10 = hexStringToBytes(body2_10);
	}

	private List<byte[]> addByte(){
		List<byte[]> list = new ArrayList<byte[]>();
		list.add(body1_1);
		list.add(body2_10);
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
		Map<String,String> map = new HashMap<String,String>();
		map.put("body1_1",bytesToHexString(body1_1));
		map.put("body2_10", BCDtointStr(body2_10));
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
	
	public void setBody1_1_int(int gun) {
		this.body1_1 = intToBytes(gun,1,0);;
	}

}
