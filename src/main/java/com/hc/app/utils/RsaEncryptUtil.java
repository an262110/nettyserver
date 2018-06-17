package com.hc.app.utils;

import com.hc.common.config.AppConfig;
import com.hc.common.utils.LogUtils;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.security.*;

/**
 * RSA密钥管理类
 * @author xuyuhao
 *
 */
public class RsaEncryptUtil {
	
	private static String PublicKeyFilePath = AppConfig.getMessage("mpos.publicKeyFilePath");
	private static String PrivateKeyFilePath = AppConfig.getMessage("mpos.privateKeyFilePath");

	/** 
     * 生成密钥 
     * 自动产生RSA1024位密钥；并保持到文件里 
     * rsaPublicKeyFilePath 公钥的文件路径名，例如：d:\publickey.cer 
     * rsaPrivateKeyFilePath 私钥的文件路径名，例如：d:\privatekey.pfx 
     * @throws NoSuchAlgorithmException  
     * @throws IOException  
     */  
    public static void getAutoCreateRSA(String rsaPublicKeyFilePath,String rsaPrivateKeyFilePath) throws NoSuchAlgorithmException, IOException{  
    	SecureRandom random = new SecureRandom();
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");  
        kpg.initialize(1024, random);
        KeyPair kp = kpg.genKeyPair();  
        PublicKey puk = kp.getPublic();  
        PrivateKey prk = kp.getPrivate();  
        System.out.println("puk:"+puk.toString());
        //生成公钥
        FileOutputStream pufos = new FileOutputStream(rsaPublicKeyFilePath);  
        ObjectOutputStream puoos = new ObjectOutputStream(pufos);  
        puoos.writeObject(puk);  
        puoos.flush();  
        puoos.close();  

        //生成私钥
        FileOutputStream prfos = new FileOutputStream(rsaPrivateKeyFilePath);  
        ObjectOutputStream proos = new ObjectOutputStream(prfos);  
        proos.writeObject(prk);  
        proos.flush();  
        proos.close();  
    }
   /**
    * 生成签名
    * @param msg
    * @return
    */
    public static String getCheckValue(String msg){

		try {
			FileInputStream fis = new FileInputStream(PrivateKeyFilePath);
			ObjectInputStream ois = new ObjectInputStream(fis);  
		    PrivateKey privateKey = (PrivateKey)ois.readObject();
		    //签名
	        Signature signature  = Signature.getInstance("MD5withRSA");
	        signature.initSign(privateKey);
	        signature.update(msg.getBytes("utf-8"));
	        byte[] signData = signature.sign();
	        //转换base64
	        String checkValue = Base64.encodeBase64String(signData).replaceAll("(\r\n|\r|\n|\n\r)", "");
	        System.out.println("签名成功");
	        return checkValue;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.print("生成签名失败");
			LogUtils.printStackTrace(e);
		}  
        
    	return "";
    }
    /**
     * 验证签名
     * @param msg
     * @param checkValue
     * @return
     */
    public static boolean verify(String msg,String checkValue){
    	
		try {
			FileInputStream fis2 = new FileInputStream(PublicKeyFilePath);
			ObjectInputStream ois2= new ObjectInputStream(fis2);  
	        PublicKey publicKey = (PublicKey)ois2.readObject();  
	        //验证签名
	        Signature signature  = Signature.getInstance("MD5withRSA");
	        signature.initVerify(publicKey);
	        signature.update(Base64.decodeBase64(msg));
	        boolean flag = signature.verify(Base64.decodeBase64(checkValue));
	        return flag;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.print("验证签名出错");
			LogUtils.printStackTrace(e);
		}  
        return false;
    	
    }
      
    
	public static void main(String[] args) throws Exception {
		//生成密钥对
		//getAutoCreateRSA(PublicKeyFilePath,PrivateKeyFilePath); 
		
		
	}
}
