package com.hc.jpos;

import com.hc.jpos.encrypt.MacUtil;
import org.jpos.iso.ISOChannel;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.channel.PostChannel;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.tlv.TLVList;
import org.jpos.util.LogEvent;
import org.jpos.util.Logger;
import org.jpos.util.SimpleLogListener;

/**
 * 消费
 * 
 * @author Zed
 *
 */
public class TestUploadSign {
	
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
//		for(int i=0;i<bb.length;i++){
//			System.out.printf("%02x",bb[i]);
//		}
		
		sendMsg.setHeader(bb);
		System.out.println();
		
		sendMsg.set(0,"0820");//交易类型
		sendMsg.set(2,"6227003324180183067");//卡号
	    //sendMsg.set(3,"190000");//主帐号
		sendMsg.set(4,"000000000010");//交易金额
		sendMsg.set(11,"000001");//POS终端交易流水
		sendMsg.set(15,"0718");//清算日期
		//sendMsg.set(22,"011");//服务点输入方式码
		sendMsg.set(37,"123456789123");//交易参考号
		sendMsg.set(41,mpos_no);//终端号
		sendMsg.set(42,merchant_id);//商户号
//		sendMsg.set(49,"156");//交易货币代码
//
//		sendMsg.set(26,"12");//密码最大长度
//		sendMsg.set(35,ISOUtil.hex2byte("754a7714f333ed0c91b0f3bcc109c4f1bc423acabc417cdd"));//二磁道数据
//		sendMsg.set(52,ISOUtil.hex2byte("a3a80aad9dc0279e"));//
//		sendMsg.set(53,ISOUtil.hex2byte("2600000000000000"));//刷卡时输入密码才有53域
		
		//NSString *pinkey = @"0be6e3318a8334026b462c949138802c";//52域
	    //NSString *macKey = @"70f2f2d6c76d26768c19e6a707d5c7e5";//64域
	    //NSString *trkKey = @"eff79eba613ed07079d60df29d540726";//35,36域
		
//55	IC卡数据域	最大255字节数据
		TLVList tlvList = new TLVList();
		tlvList.append(0XFF00, "111");//tag-
		tlvList.append(0XFF01, "111");//
		tlvList.append(0XFF02, "111");//
		tlvList.append(0XFF03, "111");//
		tlvList.append(0XFF04, "111");//
		tlvList.append(0XFF05, "111");//
		tlvList.append(0XFF06, "111");//
		tlvList.append(0XFF07, "111");//
		tlvList.append(0XFF09, "111");//
		tlvList.append(0XFF0A, "111");//
		tlvList.append(0XFF0B, "111");//
		tlvList.append(0XFF30, "111");//
		tlvList.append(0XFF31, "111");//
		tlvList.append(0XFF22, "111");//
		tlvList.append(0XFF23, "111");//
		tlvList.append(0XFF26, "111");//
		tlvList.append(0XFF60, "111");//
		tlvList.append(0XFF61, "111");//
		tlvList.append(0XFF62, "111");//
		tlvList.append(0XFF63, "111");//
		tlvList.append(0XFF64, "111");//
		sendMsg.set(55,tlvList.pack());//TLV格式
		
//		9F26（tag）	应用密文	b64
//		9F27（tag）	应用信息数据	b8
//		9F10（tag）	发卡行应用数据	b…256
//		9F37（tag）	不可预知数	b32
//		9F36（tag）	应用交易计数器	b16
//		95（tag）	终端验证结果	b40
//		9A（tag）	交易日期	n6
//		9C（tag）	交易类型	n2
//		9F02（tag）	交易金额	n12
//		5F2A（tag）	交易货币代码	n3
//		82（tag）	应用交互特征	b16
//		9F1A（tag）	终端国家代码	n3
//		9F03（tag）	其它金额	n12
//		9F33（tag）	终端性能	b24
//		9F34（tag）	持卡人验证结果	b24
//		9F35（tag）	终端类型	n2
//		9F1E（tag）	接口设备序列号	an8
//		84（tag）	专用文件名称	b…128
//		9F09（tag）	应用版本号	b16
//		9F41（tag）	交易序列计数器	n…4
//		9F63	卡类型	
//		91（tag）	发卡行认证数据	b…128
//		71（tag）	发卡行脚本1	b…1024
//		72（tag）	发卡行脚本2	b…1024		
		
		sendMsg.set(60,"07000001800");//自定义域 : 交易类型码+批次号+网络管理码
		sendMsg.set(62, ISOUtil.hex2byte("123456789"));//电子签名 < 999byte
		sendMsg.set(64,"12345678".getBytes());//MAC block

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
		//sendMsg.dump(System.out,"");  
		
		byte[] blockStr = sendMsg.pack();
		byte[] blockStr1 = new byte[sendMsg.pack().length-8];
		System.arraycopy(blockStr, 0, blockStr1, 0, blockStr1.length);
		System.out.println(ISOUtil.hexString(blockStr1));//用来计算MacBlock的报文
		
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
	
	/**
	 * 
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String calMac() throws Exception {
		
		String str = "02 00702004 8000c098 11166226 15028381 67811900 00000000 00012300 00050110 82303030 30303030 31333033 35383130 34313331 31323034 3135369c f133cdbb 976c9c26 00000000 00000000 08000000 03";
	    str = str.replace(" ", "");	
	    
		String mac = MacUtil.MAC_ASC("3751fddf23758c9ed015f75efe34e6ba","00000000000000000000000000000000",str);
		System.out.println("生成Mac Block为：" + mac);//84548DE1AA580EBE
		
		
		return mac;
	}
	
}
