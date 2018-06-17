package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 充电信息上送
 * @author liuh
 */
public final class Body0x5A extends MegUtil implements BodyI 
{
   private byte[] body1_1  ;
   private byte[] body2_5  ;
   private byte[] body3_4  ;
   private byte[] body4_4  ;
   private byte[] body5_4  ;
   private byte[] body6_4  ;
   private byte[] body7_3  ;
   private byte[] body8_3  ;
   private byte[] body9_3  ;
   private byte[] body10_3 ;
   private byte[] body11_1 ;
   private byte[] body12_1 ;
   private byte[] body13_2 ;
   private byte[] body14_4 ;
   private byte[] body15_10;
   
   private int body3_4_int;
   
   public final static int len = 52;
   
   public Body0x5A() {	  
//	    body1_1 高4位：表示充电口0-A口，1-B口，2-C口……
//	      低4位：表示该口状态：空闲 充电 预约
	      
		 body1_1 = new byte[]{0x00}; //平台状态
		 body2_5 = hexStringToBytes(getCurNum()); //当前时间
		 body3_4 = intToBytes(1000, 4, 1);	//*当前充电电量*100
		 body4_4 = intToBytes(0, 4, 1);		//当前充电金额
		 body5_4 = intToBytes(0, 4, 1);		//当前服务费金额
		 body6_4 = intToBytes(0, 4, 1); 			//当前消费金额
		 body7_3 = intToBytes(22001, 3, 1); 			//充电电压(V)
		 body8_3 = intToBytes(3200, 3, 1); 			//充电电流(A)
		 body9_3 = intToBytes(60, 3, 1);			//充电时间(60分)
		 body10_3= intToBytes(7000, 4, 1);				//输出功率(W)
		 body11_1= new byte[]{0x00};				//充电枪状态
		 body12_1= new byte[]{0x30};			   // 当前荷电状态SOC
		 body13_2 =intToBytes(30, 2, 1);				   //估算剩余充电时
		 body14_4 =fill0x00(4);                               //详细故障代码
		 body15_10=fill0x00(10);                           //
   }
   //通过字节数组实例化对象
   public Body0x5A(byte[] meg) {
		body1_1 =  copyBytes(meg, 27, 1);
		body2_5 =  copyBytes(meg, 28, 5);
		body3_4 =  copyBytes(meg, 33, 4);
		body4_4 =  copyBytes(meg, 37, 4);
		body5_4 =  copyBytes(meg, 41, 4);
		body6_4 =  copyBytes(meg, 45, 4);
		body7_3 =  copyBytes(meg, 49, 3);
		body8_3 =  copyBytes(meg, 52, 3);
		body9_3 =  copyBytes(meg, 55, 3);
		body10_3=  copyBytes(meg, 58, 3);
		body11_1=  copyBytes(meg, 61, 1);
		body12_1=  copyBytes(meg, 62, 1);
		body13_2 = copyBytes(meg, 63, 2);
		body14_4 = copyBytes(meg, 65, 4);
		body15_10= copyBytes(meg, 69, 10);
		
		body3_4_int = BytesToint(bytesReverseOrder(body3_4));
   }

   
//合并报文头和信息体
   public byte[] getByte() 
   {   
	   return appendByte(appendBytes());
   }
   
   public List<byte[]> appendBytes(){
	   List<byte[]> list = new ArrayList<byte[]>();
	   
	   list.add(body1_1);
	   list.add(body2_5);
	   list.add(body3_4);	   
	   list.add(body4_4);
	   list.add(body5_4);
	   list.add(body6_4);	   
	   list.add(body7_3);
	   list.add(body8_3);
	   list.add(body9_3);
	   list.add(body10_3);
	   list.add(body11_1);
	   list.add(body12_1);
	   list.add(body13_2);
	   list.add(body14_4);
	   list.add(body15_10);
	   
	   
	   return list;
   }

@Override
public int getBodyLen() {
	return len;
}

public Map<String, String> bytesToMap(){
	Map<String, String> map = new HashMap<String, String>();
	map.put("body1_1", BytesToint(bytesReverseOrder(body1_1))+"");
	map.put("body2_5", bytesToHexString(body2_5));
	map.put("body3_4", BytesToint(bytesReverseOrder(body3_4))+"");
	map.put("body4_4", BytesToint(bytesReverseOrder(body4_4))+"");
	map.put("body5_4", BytesToint(bytesReverseOrder(body5_4))+"");
	map.put("body6_4", BytesToint(bytesReverseOrder(body6_4))+"");
	map.put("body7_3", BytesToint(bytesReverseOrder(body7_3))/100+"");
	map.put("body8_3", BytesToint(bytesReverseOrder(body8_3))/100+"");
	map.put("body9_3", BytesToint(bytesReverseOrder(body9_3))*60+"");
	map.put("body10_3", BytesToint(bytesReverseOrder(body10_3))+"");
	map.put("body11_1", BytesToint(bytesReverseOrder(body11_1))+"");
	map.put("body12_1", BytesToint(bytesReverseOrder(body12_1))+"");
	map.put("body13_2",BytesToint(bytesReverseOrder(body13_2))+"");
	map.put("body14_4",BytesToint(bytesReverseOrder(body14_4))+"");
	map.put("body15_10",BytesToint(bytesReverseOrder(body15_10))+"");

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
public byte[] getBody2_5() {
	return body2_5;
}
public void setBody2_5(byte[] body2_5) {
	this.body2_5 = body2_5;
}
public byte[] getBody3_4() {
	return body3_4;
}
public void setBody3_4(byte[] body3_4) {
	this.body3_4 = body3_4;
}
public byte[] getBody4_4() {
	return body4_4;
}
public void setBody4_4(byte[] body4_4) {
	this.body4_4 = body4_4;
}
public byte[] getBody5_4() {
	return body5_4;
}
public void setBody5_4(byte[] body5_4) {
	this.body5_4 = body5_4;
}
public byte[] getBody6_4() {
	return body6_4;
}
public void setBody6_4(byte[] body6_4) {
	this.body6_4 = body6_4;
}
public byte[] getBody7_3() {
	return body7_3;
}
public void setBody7_3(byte[] body7_3) {
	this.body7_3 = body7_3;
}
public byte[] getBody8_3() {
	return body8_3;
}
public void setBody8_3(byte[] body8_3) {
	this.body8_3 = body8_3;
}
public byte[] getBody9_3() {
	return body9_3;
}
public void setBody9_3(byte[] body9_3) {
	this.body9_3 = body9_3;
}
public byte[] getBody10_3() {
	return body10_3;
}
public void setBody10_3(byte[] body10_3) {
	this.body10_3 = body10_3;
}
public byte[] getBody11_1() {
	return body11_1;
}
public void setBody11_1(byte[] body11_1) {
	this.body11_1 = body11_1;
}
public byte[] getBody12_1() {
	return body12_1;
}
public void setBody12_1(byte[] body12_1) {
	this.body12_1 = body12_1;
}
public byte[] getBody13_2() {
	return body13_2;
}
public void setBody13_2(byte[] body13_2) {
	this.body13_2 = body13_2;
}
public byte[] getBody14_4() {
	return body14_4;
}
public void setBody14_4(byte[] body14_4) {
	this.body14_4 = body14_4;
}
public byte[] getBody15_10() {
	return body15_10;
}
public void setBody15_10(byte[] body15_10) {
	this.body15_10 = body15_10;
}
public int getBody3_4_int() {
	return body3_4_int;
}
public void setBody3_4_int(int body3_4_int) {
	this.body3_4_int = body3_4_int;
}


}
