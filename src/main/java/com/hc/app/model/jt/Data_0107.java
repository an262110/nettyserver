package com.hc.app.model.jt;

import com.hc.app.model.MegUtil;
import com.hc.common.utils.hk.JTLogUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 *<p>title :金霆报文解析</p>
 *<p>Description : 3.8充电结算指令</p>
 *<p>Company : 广州爱电牛科技有限公司</p>
 * @date 2017年3月17日
 * @author 小吴
 */
public class Data_0107 extends MegUtil implements DataI {

	private Map map = new HashMap();
	private byte[] meg;
	private byte[] data1_16;
	private byte[] data2_16;
	private byte[] data3_1;
	private byte[] data4_8;
	private byte[] data5_6;
	private byte[] data6_6;
	private byte[] data7_6;
	private byte[] data8_4;
	private byte[] data9_4;
	private byte[] data10_1;
	private byte[] data11_1;
	private byte[] data12_8;
	private byte[] data13_4;
	private byte[] data14_2;
	private byte[] data15_4;
	private byte[] data16_1;
	private byte[] data17_1;
	public Data_0107(byte[] meg) {
		this.meg = meg;
			data1_16 = bytesReverseOrder(copyBytes(meg, 27, 16));
			data2_16 = bytesReverseOrder(copyBytes(meg, 43, 16));
			data3_1 = bytesReverseOrder(copyBytes(meg, 59, 1));
			data4_8 = bytesReverseOrder(copyBytes(meg, 60, 8));
			data5_6 = bytesReverseOrder(copyBytes(meg, 68, 6));
			data6_6 = bytesReverseOrder(copyBytes(meg, 72, 6));
			data7_6 = bytesReverseOrder(copyBytes(meg, 78, 6));
			data8_4 = bytesReverseOrder(copyBytes(meg, 84, 4));
			data9_4 = bytesReverseOrder(copyBytes(meg, 88, 4));
			data10_1 = bytesReverseOrder(copyBytes(meg, 92, 1));
			data11_1 = bytesReverseOrder(copyBytes(meg, 93, 1));
			data12_8 = bytesReverseOrder(copyBytes(meg, 94, 8));
			data13_4 = bytesReverseOrder(copyBytes(meg, 102, 4));
			data14_2 = bytesReverseOrder(copyBytes(meg, 106, 2));
			data15_4 = bytesReverseOrder(copyBytes(meg, 108, 4));
			data16_1 = bytesReverseOrder(copyBytes(meg, 112, 1));
			data17_1 = bytesReverseOrder(copyBytes(meg, 113, 1));	
	}
	 
	@Override
	public byte[] getMeg3_2() {
		// TODO Auto-generated method stub
		return new byte[]{(byte) 0xA1,0x07};
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
			map.put("data3_1", BytesToint(data3_1));
			map.put("data4_8", new String(data4_8,"ascii"));
			map.put("data5_6", bytesToHexString(data5_6));
			map.put("data6_6", bytesToHexString(data6_6));
			map.put("data7_6", bytesToHexString(data7_6));
			map.put("data8_4", BytesToint(data8_4));
			map.put("data9_4", BytesToint(data9_4));
			map.put("data10_1",BytesToint(data10_1));
			map.put("data11_1", BytesToint(data11_1));
			map.put("data12_8", BytesToint(data12_8));
			map.put("data13_4", BytesToint(data13_4));
			map.put("data14_2", BytesToint(data14_2));
			map.put("data15_4", BytesToint(data16_1));
			map.put("data17_1", BytesToint(data17_1)) ;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			JTLogUtils.error("ascii转换异常>>>>>>>>>>>>"+e.getMessage());
		}
		return map;
	}

}
