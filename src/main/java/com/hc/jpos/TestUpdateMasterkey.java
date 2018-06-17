package com.hc.jpos;

import org.jpos.iso.ISOChannel;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.channel.PostChannel;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.util.LogEvent;
import org.jpos.util.Logger;
import org.jpos.util.SimpleLogListener;

/**
 * 下载主密钥
 * 
 * @author Zed
 *
 */
public class TestUpdateMasterkey {
	
	private static String ip = "58.67.143.33";
	private static int port = 10002;
	private static String merchant_id = "303581041311204";
	private static String mpos_no = "00000001";
	
	
	public static void main(String[] args) throws Exception {
		ISOMsg sendMsg = new ISOMsg();
		
		String tpdu = "6000000000";//16进制的字符串 ，，转成16进制
		String head = "603100000000";//
		byte[] tpduByte = hexStringToBytes(tpdu);
		//System.out.println(""+tpduByte.length);
		byte[] headByte = hexStringToBytes(head);
		//System.out.println(headByte.length);
		byte[] bb = new byte[tpduByte.length+headByte.length];
		
		System.arraycopy(tpduByte, 0, bb, 0, tpduByte.length);
		System.arraycopy(headByte, 0, bb, tpduByte.length, headByte.length);
		//System.arraycopy(b, 0, bb, tpduByte.length+headByte.length, b.length);	
		for(int i=0;i<bb.length;i++){
			System.out.printf("%02x",bb[i]);
		}
		sendMsg.setHeader(bb);
		System.out.println();
		
		//sendMsg.setDirection(2);
		sendMsg.set(0,"0800");//交易类型
		sendMsg.set(11,"000001");//流水号
		sendMsg.set(41,mpos_no);//终端号
		sendMsg.set(42,merchant_id);//商户号
		sendMsg.set(60,"99000000000");
		String fld = "9f0605df 00000001 9f220101 df998180 9856b146 699bd3cb 838d2b4d d449c624 5d4a6dc3 7e383bd6 011ab5b9 cf128f53 6bbcceb6 f3767063 4d935f52 783c8696 0c5ae837 2996d4d9 9bdda4cf 9fb3dfb6 9d8d22c1 2981aab9 5b9efaf1 cb06d4ed c606e87d 26cbf792 03b952a3 df4842fe d2da7f74 c0ba083f 7bfeb7c7 f6d185f1 a3fedade 833c63a4 797f2f2e 51bc8f57";
		//fld = fld.replace(" ", "");
		fld = "9f0605df000000019f220101df9981809856b146699bd3cb838d2b4dd449c6245d4a6dc37e383bd6011ab5b9cf128f536bbcceb6f37670634d935f52783c86960c5ae8372996d4d99bdda4cf9fb3dfb69d8d22c12981aab95b9efaf1cb06d4edc606e87d26cbf79203b952a3df4842fed2da7f74c0ba083f7bfeb7c7f6d185f1a3fedade833c63a4797f2f2e51bc8f57";
		//System.out.println("62fld="+fld);
		sendMsg.set(62, ISOUtil.hex2byte(fld));
		sendMsg.set(63,"99 12345678");
		
		ISOPackager p = new GenericPackager("/Users/Zed/Canong/workspace_web1/Jpos8583/src/com/icanong/iso8583/iso8583.xml");//xml文件会附在附件中
		sendMsg.setPackager(p);
		
		byte[] b = sendMsg.pack();//这一步仅仅是为了在控制台输出结果而写。可以省略
		for(int i=0;i<b.length;i++){
			System.out.printf("%x",b[i]);
		}
	
		System.out.println();
		Logger logger = new Logger();//这一步仅仅是为了在控制台输出结果而写。可以省略
		logger.addListener (new SimpleLogListener(System.out));//这一步仅仅是为了在控制台输出结果而写。可以省略
		LogEvent evt = new LogEvent("TEST", "Test");//这一步仅仅是为了在控制台输出结果而写。可以省略
		evt.addMessage(ISOUtil.hexString(b));//这一步仅仅是为了在控制台输出结果而写。可以省略
		evt.addMessage(sendMsg);//这一步仅仅是为了在控制台输出结果而写。可以省略
		System.out.println("---发送报文：");
		System.out.println(ISOUtil.hexString(sendMsg.pack()));
		sendMsg.dump(System.out,"");  
		
		//ISOChannel channel = new PostChannel(ip, port, p);
		ISOChannel channel = new PostChannel(ip, port, p,tpdu+head);
		channel.connect();
		channel.send(sendMsg);//发送
		
		ISOMsg receiveMsg = channel.receive();//接收
		//receiveMsg.setDirection(2);
		System.out.println("---接收返回报文：");
		System.out.println(ISOUtil.hexString(receiveMsg.pack()));
		//byte[] recByte = receiveMsg.pack();//这一步仅仅是为了在控制台输出结果而写。可以省略
//		for(int i=0;i<recByte.length;i++){
//			System.out.printf("%x",recByte[i]);
//		}
		//62域 ： 319F0605DF000000019F220101DF02104D66BE049F5CE7854B962E83D9A142D2
		
		receiveMsg.dump(System.out,"");  
		
		evt.addMessage(receiveMsg);//这一步仅仅是为了在控制台输出结果而写。可以省略
		Logger.log (evt);//这一步仅仅是为了在控制台输出结果而写。可以省略
		channel.disconnect();
	}
	
	public static byte[] hexStringToBytes(String hexString) {   
	    if (hexString == null || hexString.equals("")) {   
	        return null;   
	    }   
	    hexString = hexString.toUpperCase();   
	    int length = hexString.length() / 2;   
	    char[] hexChars = hexString.toCharArray();   
	    byte[] d = new byte[length];   
	    for (int i = 0; i < length; i++) {   
	        int pos = i * 2;   
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));   
	    }   
	    return d;   
	} 
	
	private static byte charToByte(char c) {   
	    return (byte) "0123456789ABCDEF".indexOf(c);   
	}  
	
	
	
}
