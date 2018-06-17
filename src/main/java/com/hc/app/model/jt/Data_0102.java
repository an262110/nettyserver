package com.hc.app.model.jt;

import com.hc.app.model.MegUtil;
import com.hc.common.utils.hk.JTLogUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 3.2Charge注册指令
 * 
 */
public class Data_0102 extends MegUtil implements DataI {
	Map map = new HashMap();
	private byte[] meg;
	private boolean flag;
	private byte[] data1_1;
	private byte[] data2_1;
	private byte[] data3_16;
	private byte[] data4_16;
	private byte[] data5_16;
	private byte[] data6_6;
	private byte[] data7_16;
	private byte[] data8_16;
	private byte[] data9_16;
	private byte[] data10_16;
	private byte[] data11_16;
	private byte[] data12_16;
	private byte[] data13_1;
	private byte[] data14_2;
	private byte[] data15_2;
	private byte[] data16_1;
	private byte[] data17_1;
	private byte[] data18_1;
	private byte[] data19_1;
	private byte[] data20_1;
	private byte[] data21_16;
	private byte[] data22_16;

	public Data_0102(byte[] meg) {
		this.meg = meg;
		data1_1 = bytesReverseOrder(copyBytes(meg, 27, 1));
		data2_1 = bytesReverseOrder(copyBytes(meg, 28, 1));
		data3_16 = bytesReverseOrder(copyBytes(meg, 29, 16));
		data4_16 = bytesReverseOrder(copyBytes(meg, 45, 16));
		data5_16 = bytesReverseOrder(copyBytes(meg, 61, 16));
		data6_6 = bytesReverseOrder(copyBytes(meg, 77, 6));
		data7_16 = bytesReverseOrder(copyBytes(meg, 83, 16));
		data8_16 = bytesReverseOrder(copyBytes(meg, 99, 16));
		data9_16 = bytesReverseOrder(copyBytes(meg, 115, 16));
		data10_16 = bytesReverseOrder(copyBytes(meg, 131, 16));
		data11_16 = bytesReverseOrder(copyBytes(meg, 147, 16));
		data12_16 = bytesReverseOrder(copyBytes(meg, 163, 16));
		data13_1 = bytesReverseOrder(copyBytes(meg, 179, 1));
		data14_2 = bytesReverseOrder(copyBytes(meg, 180, 2));
		data15_2 = bytesReverseOrder(copyBytes(meg, 182, 2));
		data16_1 = bytesReverseOrder(copyBytes(meg, 184, 1));
		data17_1 = bytesReverseOrder(copyBytes(meg, 185, 1));
		data18_1 = bytesReverseOrder(copyBytes(meg, 186, 1));
		data19_1 = bytesReverseOrder(copyBytes(meg, 187, 1));
		data20_1 = bytesReverseOrder(copyBytes(meg, 188, 1));
		data21_16 = bytesReverseOrder(copyBytes(meg, 189, 16));
		data22_16 = bytesReverseOrder(copyBytes(meg, 205, 16));
	}

	@Override
	public byte[] getMeg3_2() {
		// TODO Auto-generated method stub
		return new byte[] { (byte) 0xA1, 0x02 };
	}

	@Override
	public byte[] getMeg4_1() {
		// TODO Auto-generated method stub
		return new byte[] { 0x01 };
	}

	@Override
	public Map getMap() {
		// TODO Auto-generated method stub
		map.put("data1_1", bytesToHexString(data1_1));
		map.put("data2_1", bytesToHexString(data2_1));
		try {
			map.put("data3_16", new String(data3_16, "ascii"));
			map.put("data4_16", new String(data4_16, "ascii"));
			map.put("data5_16", new String(data5_16, "ascii"));
			map.put("data6_6",bytesToHexString(data7_16));
			map.put("data7_16", new String(data7_16, "ascii"));
			map.put("data8_16", new String(data8_16, "ascii"));
			map.put("data9_16", new String(data9_16, "ascii"));
			map.put("data10_16", new String(data10_16, "ascii"));
			map.put("data11_16", new String(data11_16, "ascii"));
			map.put("data12_16", new String(data12_16, "ascii"));
			map.put("data13_1", BytesToint(data13_1));
			map.put("data14_2", BytesToint(data14_2));
			map.put("data15_2", BytesToint(data15_2));
			map.put("data16_1", BytesToint(data16_1));
			map.put("data17_1", BytesToint(data17_1));
			map.put("data18_1", BytesToint(data18_1));
			map.put("data19_1", BytesToint(data19_1));
			map.put("data20_1", BytesToint(data20_1));
			map.put("data21_16", new String(data21_16, "ascii"));
			map.put("data22_16", new String(data22_16, "ascii"));
		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			JTLogUtils.error("转换ascii时出现问题了>>>>>>>" + e.getMessage());
		}
		return map;
	}

}