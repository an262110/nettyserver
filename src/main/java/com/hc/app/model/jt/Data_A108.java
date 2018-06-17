package com.hc.app.model.jt;

import com.hc.app.model.MegUtil;
import com.hc.app.utils.TimeUtils;

import java.util.HashMap;
import java.util.Map;

public class Data_A108 extends MegUtil implements DataI {

	private Map map = new HashMap();
	@Override
	public byte[] getMeg3_2() {
		// TODO Auto-generated method stub
		return new byte[]{(byte) 0xA1,0x08};
	}

	@Override
	public byte[] getMeg4_1() {
		// TODO Auto-generated method stub
		return new byte[]{0x00};
	}

	@Override
	public Map getMap() {
		// TODO Auto-generated method stub
		return map;
	}
	
	public byte[] getMeg8_n(){
	   String time = TimeUtils.getCurrentTime();
	   System.out.println("--------------------");
	   int year = Integer.valueOf(time.substring(2, 4));
	   int month = Integer.valueOf(time.substring(4,6));
	   int day = Integer.valueOf(time.substring(6,8));
	   int hour = Integer.valueOf(time.substring(6,8));
	   int min = Integer.valueOf(time.substring(8,10));
	   int ss = Integer.valueOf(time.substring(10,12));
	   System.out.println("year:"+year+" month:"+month+" hour:"+hour+" min:"+min+" ss:"+ss);
	   byte[] data = new byte[]{(byte) (year & 0xff),(byte)(month & 0xff),(byte) (day & 0xff),(byte)(hour & 0xff),(byte) (min & 0xff),(byte) (ss & 0xff) };
	   return bytesReverseOrder(data);
	}
	
	public static void main(String[] args) {
		 String time = TimeUtils.getCurrentTime();
		   System.out.println("--------------------");
		   int year = Integer.valueOf(time.substring(2, 4));
		   int month = Integer.valueOf(time.substring(4,6));
		   int day = Integer.valueOf(time.substring(6,8));
		   int hour = Integer.valueOf(time.substring(8,10));
		   int min = Integer.valueOf(time.substring(10,12));
		   int ss = Integer.valueOf(time.substring(12,14));
		
		   byte yy = (byte) (year & 0xff);
		   byte mm = (byte)(month & 0xff);
		   byte dd = (byte) (day & 0xff);
		   byte hh = (byte)(hour & 0xff);
		   byte mi = (byte) (min & 0xff);
		   byte s = (byte)(ss & 0xff);
		   //System.out.println("year:"+yy+" month:"+mm+ " day:"+dd+" hour:"+hh+" min:"+mi+" ss:"+s);
		   
	}
   
}
