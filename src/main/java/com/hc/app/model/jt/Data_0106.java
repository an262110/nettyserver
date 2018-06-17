package com.hc.app.model.jt;

import com.hc.app.model.MegUtil;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 *<p>title :金霆解析报文的bean</p>
 *<p>Description :3.7工作数据指令 </p>
 *<p>Company : 广州爱电牛科技有限公司</p>
 * @date 2017年3月16日
 * @author 小吴
 */
public class Data_0106 extends MegUtil implements DataI {

	private Map map = new HashMap();
	private byte[] meg;
	private boolean flag;
	private byte[] data1_16;
	private byte[] data2_16;
	private byte[] data3_1;
	private byte[] data4_8;
	private byte[] data5_6;
	private byte[] data6_8;
	private byte[] data7_2;
	private byte[] data8_2;
	private byte[] data9_4;
	private byte[] data10_2;
	private byte[] data11_4;
	private byte[] data12_1;
	private byte[] data13_1;
	private byte[] data14_2;
	private byte[] data15_2;
	private byte[] data16_2;
	private byte[] data17_2;
	private byte[] data18_2;
	private byte[] data19_2;
	private byte[] data20_1;
	private byte[] data21_1;
	private byte[] data22_2;
	private byte[] data23_2;
	private byte[] data24_1;
	private byte[] data25_2;
	private byte[] data26_1;
	private byte[] data27_2;
	private byte[] data28_1;
	private byte[] data29_2;
	private byte[] data30_1;
	private byte[] data31_2;
	private byte[] data32_1;
	private byte[] data33_1;
	public Data_0106(byte[] meg) {
		this.meg = meg;
	    data1_16 = bytesReverseOrder(copyBytes(meg, 27, 16));
	    data2_16 = bytesReverseOrder(copyBytes(meg, 43, 16));
	    data3_1 = bytesReverseOrder(copyBytes(meg, 59, 1));
	    data4_8 = bytesReverseOrder(copyBytes(meg, 60, 8));
	    data5_6 = bytesReverseOrder(copyBytes(meg, 68, 6));
	    data6_8 = bytesReverseOrder(copyBytes(meg, 74, 8));
	    data7_2 = bytesReverseOrder(copyBytes(meg, 82, 2));
	    data8_2 = bytesReverseOrder(copyBytes(meg, 84, 2));
	    data9_4 = bytesReverseOrder(copyBytes(meg, 86, 4));
	    data10_2 = bytesReverseOrder(copyBytes(meg, 90, 2));
	    data11_4 = bytesReverseOrder(copyBytes(meg, 92, 4));
	    data12_1 = bytesReverseOrder(copyBytes(meg, 96, 1));
	    data13_1 = bytesReverseOrder(copyBytes(meg, 97, 1));
	    data14_2 = bytesReverseOrder(copyBytes(meg, 98, 2));
	    data15_2 = bytesReverseOrder(copyBytes(meg, 100, 2));
	    data16_2 = bytesReverseOrder(copyBytes(meg, 102, 2));
	    data17_2 = bytesReverseOrder(copyBytes(meg, 104, 2));
	    data18_2 = bytesReverseOrder(copyBytes(meg, 106, 2));
	    data19_2 = bytesReverseOrder(copyBytes(meg, 108, 2));
	    data20_1 = bytesReverseOrder(copyBytes(meg, 110, 1));
	    data21_1 = bytesReverseOrder(copyBytes(meg, 111, 1));
	    data22_2 = bytesReverseOrder(copyBytes(meg, 112, 2));
	    data23_2 = bytesReverseOrder(copyBytes(meg, 114, 2));
	    data24_1 = bytesReverseOrder(copyBytes(meg, 116, 1));
	    data25_2 = bytesReverseOrder(copyBytes(meg, 117, 2));
	    data26_1 = bytesReverseOrder(copyBytes(meg, 119, 1));
	    data27_2 = bytesReverseOrder(copyBytes(meg, 120, 2));
	    data28_1 = bytesReverseOrder(copyBytes(meg, 122, 1));
	    data29_2 = bytesReverseOrder(copyBytes(meg, 123, 2));
	    data30_1 = bytesReverseOrder(copyBytes(meg, 125, 1));
	    data31_2 = bytesReverseOrder(copyBytes(meg, 126, 2));
	    data32_1 = bytesReverseOrder(copyBytes(meg, 128, 1));
	    data33_1 = bytesReverseOrder(copyBytes(meg, 129, 1));    
	}
	@Override
	public byte[] getMeg3_2() {
		// TODO Auto-generated method stub
		return new byte[]{(byte) 0xA1,0x06};
	}

	@Override
	public byte[] getMeg4_1() {
		// TODO Auto-generated method stub
		return new byte[]{0x01};
	}

	@Override
	public Map getMap() {
		try {
			map.put("data1_16",new String(data1_16,"ascii"));
			map.put("data2_16",new String(data2_16,"ascii"));
			map.put("data3_1",bytesToHexString(data3_1));
			map.put("data4_8", new String(data4_8,"ascii"));
			map.put("data5_6", bytesToHexString(data5_6));
			map.put("data6_8", BytesToint(data6_8));
			map.put("data7_2", BytesToint(data7_2));
			map.put("data8_2", BytesToint(data8_2));
			map.put("data9_4", bytesToBits(data9_4));
			map.put("data10_2", BytesToint(data10_2));
			map.put("data11_4", BytesToint(data11_4));
			map.put("data12_1", BytesToint(data12_1));
			map.put("data13_1", BytesToint(data13_1));
			map.put("data14_2", BytesToint(data14_2));
			map.put("data15_2", BytesToint(data15_2));
			map.put("data16_2", BytesToint(data16_2));
			map.put("data17_2", BytesToint(data17_2));
			map.put("data18_2", BytesToint(data18_2));
			map.put("data19_2", BytesToint(data19_2));
			map.put("data20_1", BytesToint(data20_1));
			map.put("data21_1", BytesToint(data21_1));
			map.put("data22_2", BytesToint(data22_2));
			map.put("data23_2", BytesToint(data23_2));
			map.put("data24_1", BytesToint(data24_1));
			map.put("data25_2", BytesToint(data25_2));
			map.put("data26_1", BytesToint(data26_1));
			map.put("data27_2", BytesToint(data27_2));
			map.put("data28_1", BytesToint(data28_1));
			map.put("data29_2", BytesToint(data29_2));
			map.put("data30_1", BytesToint(data30_1));
			map.put("data31_2", BytesToint(data31_2));
			map.put("data32_1", BytesToint(data32_1));
			map.put("data33_1", BytesToint(data33_1));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	public byte[] getData1_16() {
		return data1_16;
	}
	public void setData1_16(byte[] data1_16) {
		this.data1_16 = data1_16;
	}
	public byte[] getData2_16() {
		return data2_16;
	}
	public void setData2_16(byte[] data2_16) {
		this.data2_16 = data2_16;
	}
	public byte[] getData3_1() {
		return data3_1;
	}
	public void setData3_1(byte[] data3_1) {
		this.data3_1 = data3_1;
	}
	public byte[] getData4_8() {
		return data4_8;
	}
	public void setData4_8(byte[] data4_8) {
		this.data4_8 = data4_8;
	}

	

}
