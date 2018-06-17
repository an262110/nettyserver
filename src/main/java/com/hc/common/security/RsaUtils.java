package com.hc.common.security;

import javax.crypto.Cipher;
import java.security.*;

public class RsaUtils {
	
	public static void main(String[] args) throws Exception {
		
		test();
	}

	public static void test1() throws Exception {
		
		// 1. 创建密钥对KeyPair：
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("rsa");
		keyPairGenerator.initialize(1024); // 密钥长度推荐为1024位.
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		// 2. 获取公钥/私钥：
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();
		    
		// 3. 服务器数据使用私钥加密：
		String plainText = "123456";
		Cipher cipher = Cipher.getInstance("rsa");
		cipher.init(Cipher.ENCRYPT_MODE, privateKey, new SecureRandom());
		byte[] cipherData = cipher.doFinal(plainText.getBytes());

		// 4. 用户使用公钥解密：
		cipher.init(Cipher.DECRYPT_MODE, publicKey, new SecureRandom());
		byte[] plainData = cipher.doFinal(cipherData);
	    System.out.println( "plainData : "  + new String(plainData));  
		 
		// 5. 服务器根据私钥和加密数据生成数字签名：
		Signature signature = Signature.getInstance("MD5withRSA");
		signature.initSign(privateKey);
		signature.update(cipherData);
		byte[] signData = signature.sign();

		// 6. 用户根据公钥、加密数据验证数据是否被修改过：
		signature.initVerify(publicKey);
		signature.update(cipherData);
		boolean status = signature.verify(signData);
		 System.out.println( "status : "  + status);  
	}
	

    /**  
     * 功能简述: 使用RSA非对称加密/解密.  
     * @throws Exception  
     */   
    public static  void  test()  throws  Exception {  
        String plainText =  "Hello , world !" ;  
          
     // 1. 创建密钥对KeyPair：
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("rsa");  
        keyPairGenerator.initialize( 1024 );  
        KeyPair keyPair = keyPairGenerator.generateKeyPair();  
          
     // 2. 获取公钥/私钥：
        PublicKey publicKey = keyPair.getPublic();  
        PrivateKey privateKey = keyPair.getPrivate();  
         
     // 3. 服务器数据使用私钥加密：
        Cipher cipher = Cipher.getInstance("rsa");  
        SecureRandom random =  new  SecureRandom();  
        cipher.init(Cipher.ENCRYPT_MODE, privateKey, random);  
        byte [] cipherData = cipher.doFinal(plainText.getBytes());  
       // System.out.println( "cipherText : "  +  Base64Utils.encoded(cipherData));  
         //yQ+vHwHqXhuzZ/N8iNg=   
      
      // 4. 用户使用公钥解密：
        cipher.init(Cipher.DECRYPT_MODE, publicKey, random);  
         byte [] plainData = cipher.doFinal(cipherData);  
        System.out.println( "plainText : "  +  new  String(plainData));  
         //Hello , world !   
          
     // 5. 服务器根据私钥和加密数据生成数字签名：
        Signature signature  = Signature.getInstance("MD5withRSA");  
        signature.initSign(privateKey);  
        signature.update(cipherData);  
         byte [] signData = signature.sign();  
        //System.out.println( "signature : "  +  new  BASE64Encoder().encode(signData));  
         ///t9ewo+KYCWKOgvu5QQ=   
      
      // 6. 用户根据公钥、加密数据验证数据是否被修改过：
        signature.initVerify(publicKey);  
        signature.update(cipherData);  
         boolean  status = signature.verify(signData);  
        System.out.println( "status : "  + status);  
         //true   
    }  

    

}
