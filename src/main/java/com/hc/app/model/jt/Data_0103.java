package com.hc.app.model.jt;

import com.hc.app.model.MegUtil;
import com.hc.common.utils.hk.JTLogUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 3.4Charge状态指令
 */
public class Data_0103 extends MegUtil implements DataI {
	private Map map = new HashMap();
	private byte[] meg;
	private byte[] data1_16;
	private byte[] data2_1;
	private byte[] data3_1;
	private byte[] data4_2;
	private byte[] data5_2;
	private byte[] data6_2;
	private byte[] data7_2;
	private byte[] data8_1;
	private byte[] data9_8;
	private byte[] data10_2;
	private byte[] data11_2;

	public Data_0103(byte[] meg) {
		this.meg = meg;
			data1_16 = bytesReverseOrder(copyBytes(meg, 27, 16));
			data2_1 = bytesReverseOrder(copyBytes(meg, 43, 1));
			data3_1 = bytesReverseOrder(copyBytes(meg, 44, 1));
			data4_2 = bytesReverseOrder(copyBytes(meg, 45, 2));
			data5_2 = copyBytes(meg, 47, 2);
			data6_2 = copyBytes(meg, 49, 2);
			data7_2 = bytesReverseOrder(copyBytes(meg, 51, 2));
			data8_1 = bytesReverseOrder(copyBytes(meg, 53, 1));
			data9_8 = bytesReverseOrder(copyBytes(meg, 54, 8));
			data10_2 = bytesReverseOrder(copyBytes(meg, 62, 2));
			data11_2 = bytesReverseOrder(copyBytes(meg, 64, 2));
	}

	@Override
	public byte[] getMeg3_2() {
		// TODO Auto-generated method stub
		return new byte[] { (byte) 0xA1, 0x03 };
	}

	@Override
	public byte[] getMeg4_1() {
		// TODO Auto-generated method stub
		return new byte[] { 0x01 };
	}

	@Override
	public Map getMap() {
			try {
				map.put("data1_16", new String(data1_16, "ascii"));
				map.put("data2_1", BytesToint(data2_1));
				map.put("data3_1", BytesToint(data3_1));
				map.put("data4_2", bytesToHexString(data4_2));
				map.put("data5_2", bytesToHexString(data5_2));
				map.put("data6_2", bytesToHexString(data6_2));
				map.put("data7_2", bytesToHexString(data7_2));
				map.put("data8_1", BytesToint(data7_2));
				map.put("data9_8", BytesToint(data9_8));
				map.put("data10_2", BytesToint(data10_2));
				map.put("data11_2", BytesToint(data11_2));
			} catch (UnsupportedEncodingException e) {
				JTLogUtils.error("转换ascii出现错误>>>>>>>>>>>>>>>>>>" + e.getMessage());
			}
		return map;
	}

}