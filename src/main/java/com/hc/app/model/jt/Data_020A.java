package com.hc.app.model.jt;

import com.hc.app.model.MegUtil;

import java.util.HashMap;
import java.util.Map;

public class Data_020A extends MegUtil implements DataI {

	private byte[] data1_2;
	private byte[] data2_2;
	private byte[] data3_2;
	private byte[] data4_2;
	private byte[] data5_2;
	private byte[] data6_2;
	private byte[] data7_2;
	private byte[] data8_2;
	private byte[] data9_2;
	private byte[] data10_2;
	private byte[] data11_2;
	private byte[] data12_2;
	private byte[] data13_2;
	private byte[] data14_2;
	private byte[] data15_2;
	private byte[] data16_2;
	private byte[] data17_2;
	private byte[] data18_2;
	private byte[] data19_2;
	private byte[] data20_2;
	private byte[] data21_2;
	private byte[] data22_2;
	private byte[] data23_2;
	private byte[] data24_2;
	private byte[] data25_2;
	private Map map = new HashMap();
	@Override
	public byte[] getMeg3_2() {
		// TODO Auto-generated method stub
		return new byte[]{0x02,0x0A};
	}
	
	

	public byte[] getData1_2() {
		return data1_2;
	}



	public void setData1_2(byte[] data1_2) {
		this.data1_2 = data1_2;
		this.data2_2 = data1_2;
		this.data3_2 = data1_2;
		this.data4_2 = data1_2;
		this.data5_2 = data1_2;
		this.data6_2 = data1_2;
		this.data7_2 = data1_2;
		this.data8_2 = data1_2;
		this.data9_2 = data1_2; 
		this.data10_2 = data1_2;
		this.data11_2 = data1_2;
		this.data12_2 = data1_2;
		this.data13_2 = data1_2;
		this.data14_2 = data1_2;
		this.data15_2 = data1_2;
		this.data16_2 = data1_2;
		this.data17_2 = data1_2;
		this.data18_2 = data1_2;
		this.data19_2 = data1_2;
		this.data20_2 = data1_2;
		this.data21_2 = data1_2;
		this.data22_2 = data1_2;
		this.data23_2 = data1_2;
		this.data24_2 = data1_2;
	}        



	public byte[] getData25_2() {
		return data25_2;
	}



	public void setData25_2(byte[] data25_2) {
		this.data25_2 = data25_2;
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

}
