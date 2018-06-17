package com.hc.app.model.jt;

import com.hc.app.model.MegUtil;
import com.hc.common.utils.hk.JTLogUtils;
import org.jpos.iso.ISOUtil;

import java.io.UnsupportedEncodingException;

public class JTHead extends MegUtil {
	 private byte[] bigData;
	 private Integer len ;
     public JTHead(byte[] bigDate) {
		this.bigData = bigDate;
	}
    
    public int length(){
    	return bigData.length;
    }
    
    /**
     * 报文数据长度
     * @return
     */
    public int getLen(){
    	byte[] bytes = copyBytes(bigData, 25, 2);
    	len = BytesToint(bytesReverseOrder(bytes));
		return len;
    	
    }
    
    public boolean getResult(){
    	getLen();
    	byte[] crc = copyBytes(bigData, 27+len, 2);	
    	int toint = BytesToint(bytesReverseOrder(crc));
    	int c = getCRC(copyBytes(bigData, 0,27+len));
    	return c==toint;
    }
    
    /**
     * 操作码
     * @return
     */
    public String getCode(){
    	byte[] copyBytes = copyBytes(bigData, 2,2);
    	return bytesToHexString(copyBytes);
    }
    
    /**
     * 报文头
     * @return
     */
    public String getHead(){
    	return ISOUtil.hexString(copyBytes(bigData, 0, 27));
    }
    
    /**
     * 报文头
     * @return
     */
    public String getData(){
    	return ISOUtil.hexString(copyBytes(bigData, 27,len));
    }
    
    /**
     * 序列号
     * @return
     */
    public String seriNum(){
    	try {
			return new String(copyBytes(bigData, 21,25),"aciis");
		} catch (UnsupportedEncodingException e) {
			JTLogUtils.error(e.getMessage());
			return null;
		}
    } 
}
