package com.hc.app.model.jt;

import com.hc.app.model.MegUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 *<p>title :金霆报文解析</p>
 *<p>Description : 3.10费率上报</p>
 *<p>Company : 广州爱电牛科技有限公司</p>
 * @date 2017年3月17日
 * @author 小吴
 */
public class Data_0109 extends MegUtil implements DataI {
	private Map map = new HashMap();
    private byte[] meg;
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
    
    public Data_0109(byte[] meg) {
		this.meg = meg;
		data1_2 = bytesReverseOrder(copyBytes(meg, 27, 2));
		data2_2 = bytesReverseOrder(copyBytes(meg, 29, 2));
		data3_2 = bytesReverseOrder(copyBytes(meg, 31, 2));
		data4_2 = bytesReverseOrder(copyBytes(meg, 33, 2));
		data5_2 = bytesReverseOrder(copyBytes(meg, 35, 2));
		data6_2 = bytesReverseOrder(copyBytes(meg, 37, 2));
		data7_2 = bytesReverseOrder(copyBytes(meg, 39, 2));
		data8_2 = bytesReverseOrder(copyBytes(meg, 41, 2));
		data9_2 = bytesReverseOrder(copyBytes(meg, 43, 2));
		data10_2 = bytesReverseOrder(copyBytes(meg, 45, 2));
		data11_2 = bytesReverseOrder(copyBytes(meg, 47, 2));
		data12_2 = bytesReverseOrder(copyBytes(meg, 49, 2));
		data13_2 = bytesReverseOrder(copyBytes(meg, 51, 2));
		data14_2 = bytesReverseOrder(copyBytes(meg, 53, 2));
		data15_2 = bytesReverseOrder(copyBytes(meg, 55, 2));
		data16_2 = bytesReverseOrder(copyBytes(meg, 57, 2));
		data17_2 = bytesReverseOrder(copyBytes(meg, 59, 2));
		data18_2 = bytesReverseOrder(copyBytes(meg, 61, 2));
		data19_2 = bytesReverseOrder(copyBytes(meg, 63, 2));
		data20_2 = bytesReverseOrder(copyBytes(meg, 65, 2));
		data21_2 = bytesReverseOrder(copyBytes(meg, 67, 2));
		data22_2 = bytesReverseOrder(copyBytes(meg, 69, 2));
		data23_2 = bytesReverseOrder(copyBytes(meg, 71, 2));
		data24_2 = bytesReverseOrder(copyBytes(meg, 73, 2));
		data25_2 = bytesReverseOrder(copyBytes(meg, 75, 2));
	}                                                  
                                                       
	@Override                                          
	public byte[] getMeg3_2() {
		return new byte[]{(byte) 0xA1,0x09};
	}

	@Override
	public byte[] getMeg4_1() {
		return new byte[]{0x01};
	}

	@Override
	public Map getMap() {
		map.put("data1_2", BytesToint(data1_2));
		map.put("data2_2", BytesToint(data2_2));
		map.put("data3_2", BytesToint(data3_2));
		map.put("data4_2", BytesToint(data4_2));
		map.put("data5_2", BytesToint(data5_2));
		map.put("data6_2", BytesToint(data6_2));
		map.put("data7_2", BytesToint(data7_2));
		map.put("data8_2", BytesToint(data8_2));
		map.put("data9_2", BytesToint(data9_2));
		map.put("data10_2", BytesToint(data10_2));
		map.put("data11_2", BytesToint(data11_2));
		map.put("data12_2", BytesToint(data12_2));
		map.put("data13_2", BytesToint(data13_2));
		map.put("data14_2", BytesToint(data14_2));
		map.put("data15_2", BytesToint(data15_2));
		map.put("data16_2", BytesToint(data16_2));
		map.put("data17_2", BytesToint(data17_2));
		map.put("data18_2", BytesToint(data18_2));
		map.put("data19_2", BytesToint(data19_2));
		map.put("data20_2", BytesToint(data20_2));
		map.put("data21_2", BytesToint(data21_2));
		map.put("data22_2", BytesToint(data22_2));
		map.put("data23_2", BytesToint(data23_2));
		map.put("data24_2", BytesToint(data24_2));
		map.put("data25_2", BytesToint(data25_2));
		// TODO Auto-generated method stub
		return map;
	}

}
