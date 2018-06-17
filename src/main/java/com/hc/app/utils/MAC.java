package com.hc.app.utils;

import org.apache.log4j.Logger;
import org.jpos.iso.ISOUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MAC {
	private static final Logger logger = Logger.getLogger(MAC.class);
    
	public static void main(String args[]) throws NoSuchAlgorithmException{
		//String data="303130383830313033303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303430464144384245303030303030303046464646464646463030303030303030303030303030303030303030303030303031303030303030303030313230323031363036303731353535353145303545383846434641413539343445";
		String data="00AD3030303030303130323000000000BC800006303130383830333037303030433830303345383030303030303030303030303030303030303030303030303030303030303030303044433244423534304534423731363430303030303030303046464646464646463030303030303030303030303030303030303030303030303031303030303030303030303031323031363036303831373031313431343744433631414346323935464144";
		String data2=data.substring(0,data.length()-32);
		System.out.println(data2);
		byte[] d= ISOUtil.hex2byte(data2);
		String re=new MAC().encrypt(d);
		System.out.println(">>>>"+re);
	}
	public String encrypt(byte[] data) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("MD5");
		String password="3333333333333333";
		byte[] b=md.digest(data);
		byte[] b2=new byte[8];
		for(int i=0,j=0;i<b.length;i++){
			if(i%2==1){
				b2[j]=b[i];
				j++;
			}
		}
		byte[] result = DesUtil.encrypt(b2,password);
		System.out.println("b2="+b2.length+";result="+result.length);
		
		String hex= ISOUtil.hexString(result);
		
		return hex;
		
	}
//	/** 
//	* macKey：mackey的密文（242D4FDB878DCA6A87DCA42022BF9D12），data：源数据，key：主密钥的密文（459807324AA7FCFC35C18961FB13ECDE） 
//	*/ 
//	public String MAC(String mackey,String data,String key) { 
//
//	byte[] primarykey = DesUtil.decrypt(key.getBytes(),"3535353535353535");  //des算法用8个5作为key解密主密钥 
//	mackey = new String(DesUtil.decrypt(mackey.getBytes(), new String(primarykey))); //用主密钥的明文解密mackey得到mackey的明文 
//
//	data = CGPTool.StringToHexForDes(data); //数据转成十六进制 
//
//	// 最后一组不组 16 补 0 
//	String des = ""; 
//	try { 
//	String[] datas = retData(data);// 将macdata 分数值，每组16位(8 byte()) 
//
//	for (int i = 0; i < datas.length; i++) { 
//	if (i == 0) { 
//	// 第一次只做DES加密 
//	des = DesUtil.encrypt(datas[i],mackey.substring(0,16)); 
//	} else { 
//	// 用上一次 DES加密结果对 第 i 组数据做异或 
//	des = this.XOR(des, datas[i]); 
//	// 对异或后的数据做DES加密 
//	des = DesUtil.encrypt(des,mackey.substring(0,16)); 
//	} 
//	} 
//
//	// des 加密最终结果用mackey后16位解密 
//	des = DesUtil.decrypt(des,mackey.substring(16)); 
//	// 解密后 再用mackey前16位加密 
//	des = DesUtil.encrypt(des,mackey.substring(0,16)); 
//	} catch (Exception e) { 
//	logger.error("MAC计算异常：",e); 
//	} 
//	return des; 
//	} 
//
//	/* 
//	* 将macdata 进行分组 每 16 字符 8byte 一组 
//	*/ 
//	public String[] retData(String data) { 
//	int len = 0; 
//	if (data.length() % 16 == 0) { 
//	len = data.length() / 16; 
//	} else { 
//	len = (int)(data.length()/16) + 1; 
//	} 
//
//	String[] datas = new String[len]; 
//	for (int i = 0; i < datas.length; i++) { 
//	if (data.length() >= 16) { 
//	datas[i] = data.substring(0, 16); 
//	data = data.substring(16); 
//	} else { 
//	datas[i] = this.moveRigZero(data, 16, "0"); 
//	break; 
//	} 
//	} 
//	return datas; 
//	} 
//
//	// 右补 0 
//	public String moveRigZero(String args, int len, String str) { 
//	if (args.length() < len) { 
//	while (args.length() < len) { 
//	args = args + str; 
//	} 
//	} 
//	return args; 
//	} 
//
//	/* 
//	* 数据异或 
//	*/ 
//	public String XOR(String str1, String str2) { 
//	String hex = ""; 
//	if (str1.length() != str2.length()) { 
//	logger.error("异或数据长度不等"); 
//	} 
//	for (int i = 0; i < str1.length(); i++) { 
//	hex = hex 
//	+ Integer.toHexString((Integer.parseInt( 
//	str1.charAt(i) + "", 16) ^ Integer.parseInt(str2 
//	.charAt(i) 
//	+ "", 16))); 
//	} 
//	return hex.toUpperCase(); 
//	} 
}
