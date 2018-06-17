package com.hc.app.model.jt;

import com.hc.app.model.MegUtil;

import java.util.HashMap;
import java.util.Map;

/** 3.2 0x010x01 Charger登录指令
 * 
 */
public class Data_0101 extends MegUtil implements DataI {

	Map map = new HashMap();
	private byte[] meg;
	public Data_0101(byte[] msg) {
		// TODO Auto-generated constructor stub
		this.meg= meg;
	}
	
	@Override
	public Map getMap() {
		// TODO Auto-generated method stub
		return map;
	}
	@Override
	public byte[] getMeg3_2() {
		// TODO Auto-generated method stub
		return new byte[]{(byte) 0xA1,0x01};
	}
	@Override
	public byte[] getMeg4_1() {
		// TODO Auto-generated method stub
		return new byte[]{0x01};
	}
	
	
	

   

}