package com.hc.app.model.jt;

import com.hc.app.model.MegUtil;
import com.hc.common.utils.hk.JTLogUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 *<p>title :金霆报文解析bean</p>
 *<p>Description : 3.5事件上传指令</p>
 *<p>Company : 广州爱电牛科技有限公司</p>
 * @date 2017年3月16日
 * @author 小吴
 */
public class Data_0104 extends MegUtil implements DataI {
	
    private byte[] meg;
    private byte[] data1_16;
    private byte[] data2_6;
    private byte[] data3_1;
    private byte[] data4_1;
    private byte[] data5_2;
    private Map map = new HashMap();
    
    public Data_0104(byte[] meg) {
	    this.meg = meg;
	    	data1_16 = bytesReverseOrder(copyBytes(meg, 27, 16));
	    	data2_6 = bytesReverseOrder(copyBytes(meg, 43, 6));
	    	data3_1 = bytesReverseOrder(copyBytes(meg, 49, 1));
	    	data4_1 = bytesReverseOrder(copyBytes(meg, 50, 1));
	    	data5_2 = copyBytes(meg, 51, 2);
	}
	@Override
	public byte[] getMeg3_2() {
		// TODO Auto-generated method stub
		return new byte[]{(byte) 0xA1,0x04};
	}

	@Override
	public byte[] getMeg4_1() {
		// TODO Auto-generated method stub
		return new byte[]{0x01};
	}

	@Override
	public Map getMap() {
		// TODO Auto-generated method stub
		try {
			map.put("data1_16", new String(data1_16,"ascii"));
			map.put("data2_6", bytesToHexString(data2_6));
			map.put("data3_1", BytesToint(data3_1));
			map.put("data4_1",BytesToint(data4_1));
			map.put("data5_2", bytesToHexString(data5_2));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			JTLogUtils.error("转换ascii时出现问题了>>>>>>>" + e.getMessage());
		}
		return map;
	}

}
