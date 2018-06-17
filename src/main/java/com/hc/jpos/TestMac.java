package com.hc.jpos;

import com.hc.jpos.encrypt.MacUtil;
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
 * 签到
 * 
 * @author Zed
 *
 */
public class TestMac {
	
	private static String ip = "58.67.143.33";
	private static int port = 10002;
	private static String merchant_id = "303581041311204";
	private static String mpos_no = "00000001";
	
	//需要传入的参数：流水号，批次号(6位)，终端号，商户号，
	public static void main(String[] args) throws Exception {
		
		ISOMsg sendMsg = new ISOMsg();
		
		//设置报文头
		String tpdu = "6000000000";//(固定)16进制的字符串 ，，转成16进制
		String head = "603100000000";//(固定)
		byte[] tpduByte = hexStringToBytes(tpdu);
		byte[] headByte = hexStringToBytes(head);
		byte[] allHeadByte = new byte[tpduByte.length+headByte.length];
		
		System.arraycopy(tpduByte, 0, allHeadByte, 0, tpduByte.length);
		System.arraycopy(headByte, 0, allHeadByte, tpduByte.length, headByte.length);
//		for(int i=0;i<allHeadByte.length;i++){
//			System.out.printf("%02x",allHeadByte[i]);
//		}
		sendMsg.setHeader(allHeadByte);
		System.out.println();
		
		//设置报文内容
		sendMsg.set(0,"0720");//交易类型
		sendMsg.set(3,"000000");//
		sendMsg.set(11,"000001");//流水号
		sendMsg.set(41,mpos_no);//终端号
		sendMsg.set(42,merchant_id);//商户号
		
		//上送报文，服务器端拼接,二进制
		String str = "02 00702004 8000c098 11166226 15028381 67811900 00000000 00012300 00050110 82303030 30303030 31333033 35383130 34313331 31323034 3135369c f133cdbb 976c9c26 00000000 00000000 08000000 03";
	    str = str.replace(" ", "");	
		String mac = MacUtil.gemMacBlock(str);
		System.out.println("生成Mac Block为：" + mac);//84548DE1AA580EBE
		sendMsg.set(62,mac.getBytes());//返回的62域值，ASCII码 3543453435444344 转成hexTostring
		
		ISOPackager packager = new GenericPackager("/Users/Zed/Canong/workspace_web1/Jpos8583/src/com/icanong/iso8583/iso8583.xml");//xml文件会附在附件中
		sendMsg.setPackager(packager);
		
//		byte[] b = sendMsg.pack();//这一步仅仅是为了在控制台输出结果而写。可以省略
//		for(int i=0;i<b.length;i++){
//			System.out.printf("%x",b[i]);
//		}
	
		System.out.println();
		Logger logger = new Logger();//这一步仅仅是为了在控制台输出结果而写。可以省略
		logger.addListener (new SimpleLogListener(System.out));//这一步仅仅是为了在控制台输出结果而写。可以省略
		LogEvent evt = new LogEvent("TEST", "Test");//这一步仅仅是为了在控制台输出结果而写。可以省略
		//evt.addMessage(ISOUtil.hexString(b));//这一步仅仅是为了在控制台输出结果而写。可以省略
		evt.addMessage(sendMsg);//这一步仅仅是为了在控制台输出结果而写。可以省略
		
		System.out.println("---发送报文：");
		System.out.println(ISOUtil.hexString(sendMsg.pack()));
		//sendMsg.dump(System.out,"");  
		
		//ISOChannel channel = new PostChannel("IP地址", "端口", p);
		ISOChannel channel = new PostChannel(ip, port, packager,tpdu+head);
		//ISOChannel channel = new AmexChannel(ip, port, p);
		//ISOChannel channel = new BASE24TCPChannel(ip, port, p);
		//channel.setHeader(bb);
		channel.connect();
		channel.send(sendMsg);//发送
		
		ISOMsg receiveMsg = channel.receive();//接收
		System.out.println("---接收返回报文：");
		System.out.println(ISOUtil.hexString(receiveMsg.pack()));
//      byte[] recByte = receiveMsg.pack();//这一步仅仅是为了在控制台输出结果而写。可以省略
//		for(int i=0;i<recByte.length;i++){
//			System.out.printf("%x",recByte[i]);
//		}
		System.out.println();
		receiveMsg.dump(System.out,"");  //打印成xml报文的格式
		
		System.out.println("39域="+receiveMsg.getString(39));
		System.out.println("62域="+receiveMsg.getString(62));
		//System.out.println(ISOUtil.hex2byte(receiveMsg.getString(62))); 
		byte[] recByte = ISOUtil.hex2byte(receiveMsg.getString(62));//64域值
		for(int i=0;i<recByte.length;i++){
			System.out.printf("%x\n",recByte[i]);
		}
		System.out.println();
		System.out.println("62域="+receiveMsg.getString(62).length());
		
		/*
		System.out.println("\n THIS IS PAY TRAD."+
            "\n [MTI     ]= ["+receiveMsg.getString(0)+"]"+
            "\n [BitMap  ]= ["+receiveMsg.getString(1)+"]"+
            "\n [Pan     ]= ["+receiveMsg.getString(2)+"]"+
            "\n [ProcCode]= ["+receiveMsg.getString(3)+"]"+
            "\n [TranAmt ]= ["+receiveMsg.getString(4)+"]"+
            "\n [TranDtTm]= ["+receiveMsg.getString(7)+"]"+
            "\n [AcqSsn  ]= ["+receiveMsg.getString(11)+"]"+
            "\n [LTime   ]= ["+receiveMsg.getString(12)+"]"+
            "\n [LDate   ]= ["+receiveMsg.getString(13)+"]"+
            "\n [SettDate]= ["+receiveMsg.getString(15)+"]"+
            "\n [CondMode]= ["+receiveMsg.getString(25)+"]"+
            "\n [AcqInst ]= ["+receiveMsg.getString(32)+"]"+
            "\n [ForwInst]= ["+receiveMsg.getString(33)+"]"+
            "\n [IndexNum]= ["+receiveMsg.getString(37)+"]"+
            "\n [TermCode]= ["+receiveMsg.getString(41)+"]"+
            "\n [MercCode]= ["+receiveMsg.getString(42)+"]"+
            "\n [AddiData]= ["+receiveMsg.getString(43)+"]"+
            "\n [BankId  ]= ["+receiveMsg.getString(48)+"]"+
            "\n [TranCurr]= ["+receiveMsg.getString(49)+"]"+
            "\n [DestInst]= ["+receiveMsg.getString(100)+"]"+
            "\n [MAC ]= ["+receiveMsg.getString(128)+"]"+
            "\n ####################PPP REQ TRAD END .###########################");
         */   
	
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
