package com.hc.app.model.jt;

import com.hc.app.model.MegUtil;

import java.util.HashMap;
import java.util.Map;

public class Data_A205 extends MegUtil implements DataI {

	private byte[] meg;
	private byte[] data1_1;
	private byte[] data2_1;
	private Map map = new HashMap();
	
	public Data_A205(byte[] meg) {
		this.meg = meg;
			data1_1 = bytesReverseOrder(copyBytes(meg, 27, 2));
			data2_1 = bytesReverseOrder(copyBytes(meg, 28, 1));
	}
	
	@Override
	public byte[] getMeg3_2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getMeg4_1() {
		// TODO Auto-generated method stub
		return bytesReverseOrder(copyBytes(meg, 4, 1));
	}

	@Override
	public Map getMap() {
		map.put("data1_2",BytesToint(data1_1));
		map.put("data2_1",BytesToint(data2_1));
		return map;
	}

}
