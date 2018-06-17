package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运营商注册发送
 * @author liuh
 *
 */
public final class Body0xB0 extends MegUtil implements BodyI 
{
   private byte[] body1_2;
   private byte[] body2_16;
   private byte[] body3_50;
   private byte[] body4_10;
   private byte[] body5_4;
   private byte[] body6_2;
   private byte[] body7_4;
   private byte[] body8_2;
   
   public final static int len = 90;
   
   public Body0xB0() throws UnsupportedEncodingException {
	   String oper_code_2 = "1001";
	   String oper_pwd_16 = "21232f297a57a5a743894a0e4a801fc3";
	   String oper_name_50 = "广州爱电牛互联网科技有限公司\r\n";
		
	   String oper_zone_10 = "";
	   String ip_4 = "120.76.77.245";
	   int port_2=8044;
		
	   String ip_bak_4 = "120.76.77.245";
	   int port_bak_2=8044;
	   
	   body1_2 = fill0x00(hexStringToBytes(oper_code_2), 2);
	   body2_16 = fill0x00(hexStringToBytes(oper_pwd_16),16);
		body3_50 = fill0x00(oper_name_50.getBytes("gbk"),50);
		body4_10 = fill0x00(oper_zone_10.getBytes(),10);
		body5_4 = ipToBytes(ip_4);
		body6_2 = intToBytes(port_2, 2, 1);
		body7_4 = ipToBytes(ip_bak_4);
		body8_2 = intToBytes(port_bak_2, 2, 1);
	
	
   }
   //通过字节数组实例化对象
   public Body0xB0(byte[] meg) {
		body1_2 = copyBytes(meg, 27, 2);
		body2_16 = copyBytes(meg, 29, 16);
		body3_50 = copyBytes(meg, 45, 50);
		body4_10 = copyBytes(meg, 95, 10);
		body5_4 = copyBytes(meg, 105, 4);
		body6_2 = copyBytes(meg, 109, 2);
		body7_4 = copyBytes(meg, 111, 4);
		body8_2 = copyBytes(meg, 115, 2);
   }

   
//合并报文头和信息体
   public byte[] getByte() 
   {   
	   return appendByte(appendBytes());
   }
   
   public List<byte[]> appendBytes(){
	   List<byte[]> list = new ArrayList<byte[]>();
	   
	   list.add(body1_2);
	   list.add(body2_16);
	   list.add(body3_50);
	   
	   list.add(body4_10);
	   list.add(body5_4);
	   list.add(body6_2);
	   
	   list.add(body7_4);
	   list.add(body8_2);
	   
	   return list;
   }


public byte[] getBody1_2() {
	return body1_2;
}


public void setBody1_2(byte[] body1_2) {
	this.body1_2 = body1_2;
}


public byte[] getBody2_16() {
	return body2_16;
}


public void setBody2_16(byte[] body2_16) {
	this.body2_16 = body2_16;
}


public byte[] getBody3_50() {
	return body3_50;
}


public void setBody3_50(byte[] body3_50) {
	this.body3_50 = body3_50;
}


public byte[] getBody4_10() {
	return body4_10;
}


public void setBody4_10(byte[] body4_10) {
	this.body4_10 = body4_10;
}


public byte[] getBody5_4() {
	return body5_4;
}


public void setBody5_4(byte[] body5_4) {
	this.body5_4 = body5_4;
}


public byte[] getBody6_2() {
	return body6_2;
}


public void setBody6_2(byte[] body6_2) {
	this.body6_2 = body6_2;
}


public byte[] getBody7_4() {
	return body7_4;
}


public void setBody7_4(byte[] body7_4) {
	this.body7_4 = body7_4;
}


public byte[] getBody8_2() {
	return body8_2;
}


public void setBody8_2(byte[] body8_2) {
	this.body8_2 = body8_2;
}


@Override
public int getBodyLen() {
	// TODO Auto-generated method stub
	return len;
}

public Map<String, String> bytesToMap(){
	Map<String, String> map = new HashMap<String, String>();
	map.put("body1_2", BCDtointStr(body1_2));
	map.put("body2_16", bytesToHexString(body2_16));
	map.put("body3_50", bytesToHexString(body3_50));//转汉字
	map.put("body4_10", bytesToHexString(body4_10));
	map.put("body5_4", BytesToip(body5_4));
	map.put("body6_2", BytesToint(bytesReverseOrder(body6_2))+"");
	map.put("body7_4", BytesToip(body7_4));
	map.put("body8_2", BytesToint(bytesReverseOrder(body8_2))+"");

	return map;
}
//发送前合并byte[] 封装
public ByteBuf getSendBuf(){
	   return obtainSendBuf(getByte());
}

}
