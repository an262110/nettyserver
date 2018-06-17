package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运营商注册接收 需要修改 没有 body2_16
 * @author liuh
 *
 */
public final class Body0xB1 extends MegUtil implements BodyI 
{
   private byte[] body1_2;
   private byte[] body2_50;
   private byte[] body3_10;
   private byte[] body4_4;
   private byte[] body5_2;
   private byte[] body6_4;
   private byte[] body7_2;
   
   private String body_hexstr;
   public final static int len = 74;
    
   public Body0xB1() {
	
   }
   //通过字节数组实例化对象
   public Body0xB1(byte[] meg) {
	   System.out.println(bytesToHexString(meg));
		body1_2=   copyBytes(meg, 27, 2);
		body2_50 = copyBytes(meg, 29, 50);
		body3_10 = copyBytes(meg, 79, 10);
		body4_4 =  copyBytes(meg, 89, 4);
		body5_2=   copyBytes(meg, 93, 2);
		body6_4=   copyBytes(meg, 95, 4);
		body7_2=   copyBytes(meg, 99, 2);
		
		body_hexstr = bytesToHexString(this.getByte());
   }

   
//合并报文头和信息体
   public byte[] getByte() 
   {   
	   return appendByte(appendBytes());
   }
   
   public List<byte[]> appendBytes(){
	   List<byte[]> list = new ArrayList<byte[]>();
	   
	   list.add(body1_2);
	   list.add(body2_50);
	   
	   list.add(body3_10);
	   list.add(body4_4);
	   list.add(body5_2);
	   
	   list.add(body6_4);
	   list.add(body7_2);
	   
	   return list;
   }

@Override
public int getBodyLen() {
	// TODO Auto-generated method stub
	return len;
}

public Map<String, String> bytesToMap(){
	Map<String, String> map = new HashMap<String, String>();
	map.put("body1_2", BCDtointStr(body1_2));
	map.put("body2_50", bytesToHexString(body2_50));//转汉字
	map.put("body3_10", bytesToHexString(body3_10));
	map.put("body4_4", BytesToip(body4_4));
	map.put("body5_2", BytesToint(bytesReverseOrder(body5_2))+"");
	map.put("body6_4", BytesToip(body6_4));
	map.put("body7_2", BytesToint(bytesReverseOrder(body7_2))+"");

	return map;
}
//发送前合并byte[] 封装
public ByteBuf getSendBuf(){
	   return obtainSendBuf(getByte());
}
public byte[] getBody1_2() {
	return body1_2;
}
public void setBody1_2(byte[] body1_2) {
	this.body1_2 = body1_2;
}
public byte[] getBody2_50() {
	return body2_50;
}
public void setBody2_50(byte[] body2_50) {
	this.body2_50 = body2_50;
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
public byte[] getBody5_2() {
	return body5_2;
}
public void setBody5_2(byte[] body5_2) {
	this.body5_2 = body5_2;
}
public byte[] getBody6_4() {
	return body6_4;
}
public void setBody6_4(byte[] body6_4) {
	this.body6_4 = body6_4;
}
public byte[] getBody7_2() {
	return body7_2;
}
public void setBody7_2(byte[] body7_2) {
	this.body7_2 = body7_2;
}
public String getBody_hexstr() {
	return body_hexstr;
}
public void setBody_hexstr(String body_hexstr) {
	this.body_hexstr = body_hexstr;
}



}
