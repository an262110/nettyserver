package com.hc.jpos;

import com.hc.app.config.RetMsgContants;
import com.hc.common.config.AppConfig;
import com.hc.common.utils.LogUtils;
import com.hc.jpos.encrypt.MacUtil;
import org.apache.commons.lang.ArrayUtils;
import org.jpos.iso.*;
import org.jpos.iso.channel.PostChannel;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.tlv.TLVList;

import java.util.HashMap;
import java.util.Map;

/**
 * ISO 8583 工具类
 * 
 * @author Zed
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class ISO8583Utils {

	private static String ip = AppConfig.getMessage("mpos.ISO8583.ip");//"58.67.143.33";
	private static int port = Integer.valueOf(AppConfig.getMessage("mpos.ISO8583.port"));//10002;
	private static String ISO8583_XML = AppConfig.getMessage("mpos.ISO8583.xml");//"config/iso8583.xml";
	private static String ISO8583_XML_REPO = AppConfig.getMessage("IDN.ISO8583.REPO.xml");
	// private static String merchant_id = "303581041311204";
	// private static String mpos_no = "00000001";

	/**
	 * 测试
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
	/*	
		String transNo ="000001"; 
		String batchNo ="000001"; 
		String mpos_no ="00000001";
		String merchant_id ="303581041311204";
		
		//String field62 = "9f0605df000000019f220101df9981809856b146699bd3cb838d2b4dd449c6245d4a6dc37e383bd6011ab5b9cf128f536bbcceb6f37670634d935f52783c86960c5ae8372996d4d99bdda4cf9fb3dfb69d8d22c12981aab95b9efaf1cb06d4edc606e87d26cbf79203b952a3df4842fed2da7f74c0ba083f7bfeb7c7f6d185f1a3fedade833c63a4797f2f2e51bc8f57";
		//LogUtils.info(field62.length());//288
		
		//签到
		String field62 = "00B4CED9EE4113E09645271F37EB3F7D8B447B39E26A33C49B0560C76D3FDB8BD3F01B08A333EB9DF623A4DD364F566C276E9295EFE24A79A2BA251882";
		LogUtils.info(field62.length());//122
		LogUtils.info(ISOUtil.hexString(field62.getBytes()));
		LogUtils.info(ISOUtil.hexString(field62.getBytes()).length());	
		//sign(transNo,batchNo,mpos_no,merchant_id);
		//LogUtils.info("319F0605DF000000019F220101DF02104D66BE049F5CE7854B962E83D9A142D2".length());
		
		//生成Mac
		//上送报文，服务器端拼接,二进制
		String data = "02 00702004 8000c098 11166226 15028381 67811900 00000000 00012300 00050110 82303030 30303030 31333033 35383130 34313331 31323034 3135369c f133cdbb 976c9c26 00000000 00000000 08000000 03";
		data = data.replace(" ", "");	
		//LogUtils.info("测试返回＝"+gemMac(transNo,mpos_no,merchant_id,data));
	*/
	
	}
	
	public static byte[] sign(ISOMsg msgInfo) throws ISOException {
		ISOMsg sendMsg=new ISOMsg();
		Integer length=134;
		String header="0086303030303030";
		sendMsg.setHeader(hexStringToBytes(header));
		
		sendMsg.set("39","00");
		sendMsg.set("4","1111");
		sendMsg.set("5","192.168.2.39");
		
		sendMsg.set("0","2021");
		 ISOPackager packager = new GenericPackager(ISO8583_XML_REPO);
		 sendMsg.setPackager(packager);
		
		
		
		byte[] res=sendMsg.pack();
		byte[] both = (byte[]) ArrayUtils.addAll(hexStringToBytes(header),res);
		sendMsg.dump(System.out, "");
		LogUtils.info("--------------------------(S101:签到 )发送的报文--------------------------");
		LogUtils.info(ISOUtil.hexString(both));
		
		return both;
	}

	/**
	 * 签到 ,获取终端密钥
	 * 
	 * @param transNo
	 *            流水号(6位)
	 * @param batchNo
	 *            批次号(6位)
	 * @param mpos_no
	 *            终端号
	 * @param merchant_id
	 *            商户号
	 * @throws Exception
	 */
	public static Map sign(String transNo, String batchNo, String mpos_no,
			String merchant_id) throws Exception {

		ISOMsg sendMsg = new ISOMsg();

		// 设置报文头
		String header = "6000000000603100000000";
		sendMsg.setHeader(hexStringToBytes(header));

		// 设置报文内容
		sendMsg.set(0, "0800");// 交易类型
		sendMsg.set(11, transNo);// 流水号
		sendMsg.set(41, mpos_no);// 终端号
		sendMsg.set(42, merchant_id);// 商户号
		String field60 = "00" + batchNo + "003";// "00000001003"
		sendMsg.set(60, field60);// 自定义域＝交易类型码(固定)+批次号+网络管理信息码(固定)
		sendMsg.set(63, "01 ");// 操作员代码(固定)

		ISOPackager packager = new GenericPackager(ISO8583_XML);// xml文件会附在附件中
		sendMsg.setPackager(packager);

		LogUtils.info("--------------------------(S101:签到 )发送的报文--------------------------");
		LogUtils.info(ISOUtil.hexString(sendMsg.pack()));
		sendMsg.dump(System.out, "");

		ISOChannel channel = new PostChannel(ip, port, packager, header);
		channel.connect();
		channel.send(sendMsg);// 发送

		ISOMsg receiveMsg = channel.receive();// 接收
		LogUtils.info("--------------------------(S101:签到 )返回的报文--------------------------");
		LogUtils.info(ISOUtil.hexString(receiveMsg.pack()));
		receiveMsg.dump(System.out, ""); // 打印成xml报文的格式

		String field39 = receiveMsg.getString(39);
		String field62 = receiveMsg.getString(62);
		
		LogUtils.info("39域(S101:签到--返回码)=" + field39);
		LogUtils.info("62域(S101:签到--终端密钥)=" + receiveMsg.getString(62));//终端密钥
		//LogUtils.info("62域=" + receiveMsg.getString(62).length());
		//lrw  20150805  若receiveMsg.getString(62)  为null时，获取长度会报错，为了快速定位问题，修改为
		String _field62 = (null!=receiveMsg.getString(62))?(String)receiveMsg.getString(62):"";
		LogUtils.info("62域(S101:签到--终端密钥)长度=" + _field62.length());
		
		
		channel.disconnect();
		
		//交易应答码中仅"00"为交易成功，其它为交易不成功。
//		if("00".equals(field39)){
//			return field62;//返回 终端密钥
//		}else{
//			return "";
//		}	
		
		Map resDataMap = new HashMap();
		resDataMap.put("return_code", field39);
		resDataMap.put("return_msg", AppConfig.getMessage("mpos.rescode."+field39));
	
		if("00".equals(field39)){//如果返回成功，则返回相关数据
			resDataMap.put("return_code", RetMsgContants.Code0000);
			resDataMap.put("terminal_key", field62);
		}
		
		return resDataMap;
	}
	
	/**
	 * 更新主密钥(下载主密钥)
	 * 
	 * @param transNo 流水号 
	 * @param terminalKey 终端密钥
	 * @param terminalSeq 终端序列号
	 * @param mpos_no 终端号
	 * @param merchant_id 商户号
	 * @throws Exception
	 */
	public static Map updateMasterkey(String transNo, String batchNo,String terminalKey,String terminalSeq, String mpos_no,
			String merchant_id) throws Exception {
		
		ISOMsg sendMsg = new ISOMsg();
		
		// 设置报文头
		String header = "6000000000603100000000";
		sendMsg.setHeader(hexStringToBytes(header));
		
		// 设置报文内容
		sendMsg.set(0,"0800");//交易类型
		sendMsg.set(11,transNo);//流水号
		sendMsg.set(41,mpos_no);//终端号
		sendMsg.set(42,merchant_id);//商户号
		sendMsg.set(60,"99000000000");//自定义域(11位)＝交易类型码(固定值99)+批次号(无法获取批次号时，固定填充”000000”)+网络管理信息码(固定值000)
		
		//String field62 = "9f0605df000000019f220101df9981809856b146699bd3cb838d2b4dd449c6245d4a6dc37e383bd6011ab5b9cf128f536bbcceb6f37670634d935f52783c86960c5ae8372996d4d99bdda4cf9fb3dfb69d8d22c12981aab95b9efaf1cb06d4edc606e87d26cbf79203b952a3df4842fed2da7f74c0ba083f7bfeb7c7f6d185f1a3fedade833c63a4797f2f2e51bc8f57";
		//sendMsg.set(62,ISOUtil.hex2byte(field62));//终端密钥
		sendMsg.set(62, ISOUtil.hex2byte(terminalKey));//终端密钥
		
		String field63 = "99 " + terminalSeq ;// 操作员代码(3位) + 机身序列号(8位) 
		sendMsg.set(63,field63);//"99 12345678"
		
		ISOPackager packager = new GenericPackager(ISO8583_XML);//xml文件会附在附件中
		sendMsg.setPackager(packager);
		
		
		LogUtils.info("--------------------------发送的报文(S102:下载主秘钥)--------------------------");
		LogUtils.info(ISOUtil.hexString(sendMsg.pack()));
		sendMsg.dump(System.out,"");  
		
		ISOChannel channel = new PostChannel(ip, port, packager, header);
		channel.connect();
		channel.send(sendMsg);//发送
		
		ISOMsg receiveMsg = channel.receive();//接收
		LogUtils.info("--------------------------接收的报文(S102:下载主秘钥)--------------------------");
		LogUtils.info(ISOUtil.hexString(receiveMsg.pack()));
		receiveMsg.dump(System.out,"");  

		//62域 ： 319F0605DF000000019F220101DF02104D66BE049F5CE7854B962E83D9A142D2
		
		String field39 = receiveMsg.getString(39);
		LogUtils.info("(S102:下载主秘钥)39域--返回码："+field39);
	
		channel.disconnect();
		
		//交易应答码中仅"00"为交易成功，其它为交易不成功。
//		if("00".equals(field39)){
//			return receiveMsg.getString(62);//返回 终端密钥
//		}else{
//			return "";
//		}	
//		
		Map resDataMap = new HashMap();
		resDataMap.put("return_code", field39);
		resDataMap.put("return_msg", AppConfig.getMessage("mpos.rescode."+field39));
	
		if("00".equals(field39)){//如果返回成功，则返回相关数据
			resDataMap.put("return_code", RetMsgContants.Code0000);
			resDataMap.put("terminal_key", receiveMsg.getString(62));
		}
		
		return resDataMap;
	}
	

	/**
	 * 消费
	 * 
	 * 需要输入参数：主帐号，交易金额(12位，精确到分)，交易流水，终端号，商户号，银行卡号，批次号，二磁道数据,密码，订单号，经度，纬度，55域
	 * 
	 * @param accountNo
	 * @param amount
	 * @param trans_no
	 * @param mpos_no
	 * @param merchant_id
	 * @param card_no
	 * @param trackTwoData
	 * @param password
	 * @param order_no 订单号
	 * @param longitude 经度
	 * @param latitude 纬度
	 * @param field55
	 * @throws Exception
	 */
	public static Map trans(String accountNo, String amount, String trans_no,String batch_no,
			String mpos_no,String seq_no, String merchant_id, String card_no,
			String trackTwoData, String password,String order_no,String longitude,String latitude,String dateExpire,String mcc,String merchant_mobile_no)
			throws Exception {

		ISOMsg sendMsg = new ISOMsg();
		
		// 设置报文头
		String header = "6000000000603100000000";
		sendMsg.setHeader(hexStringToBytes(header));
		
		sendMsg.set(0,"0200");//交易类型
		//sendMsg.set(2,card_no);//卡号(智付说  磁条卡不用送第二域)
		sendMsg.set(3, "190000");//交易处理码
		sendMsg.set(4, amount);//交易金额
		sendMsg.set(11,trans_no);//POS终端交易流水
		sendMsg.set(14,dateExpire);//卡有效期
		sendMsg.set(22,"021");//服务点输入方式码
		sendMsg.set(25,"82");//服务点条件码
		sendMsg.set(26,"12");//密码最大长度
		sendMsg.set(35, ISOUtil.hex2byte(trackTwoData));//二磁道数据，，Track two data
		sendMsg.set(41,mpos_no);//终端号
		sendMsg.set(42,merchant_id);//商户号
		sendMsg.set(49,"156");//交易货币代码
		sendMsg.set(52, ISOUtil.hex2byte(password));//个人标识码数据
		sendMsg.set(53, ISOUtil.hex2byte("2600000000000000"));//刷卡时输入密码才有53域，最长16位，针对信用卡不输密码的情况
		
		//NSString *pinkey = @"0be6e3318a8334026b462c949138802c";//52域
	    //NSString *macKey = @"70f2f2d6c76d26768c19e6a707d5c7e5";//64域
	    //NSString *trkKey = @"eff79eba613ed07079d60df29d540726";//35,36域
	/*	
//55	IC卡数据域	最大255字节数据 ，，，以下子域中的M仅当该交易为IC卡交易时有效
		TLVList tlvList = new TLVList();
		tlvList.append(0X9F26, "111");//tag-
		tlvList.append(0X9F27, "111");//
		tlvList.append(0X9F10, "111");//
		tlvList.append(0X9F37, "111");//
		tlvList.append(0X9F36, "111");//
		tlvList.append(0X95, "111");//
		tlvList.append(0X9A, "111");//
		tlvList.append(0X9C, "111");//
		tlvList.append(0X9F02, "111");//
		tlvList.append(0X5F2A, "111");//
		tlvList.append(0X82, "111");//
		tlvList.append(0X9F1A, "111");//
		tlvList.append(0X9F03, "111");//
		tlvList.append(0X9F33, "111");//
		tlvList.append(0X9F34, "111");//
		tlvList.append(0X9F35, "111");//
		tlvList.append(0X9F1E, "111");//
		tlvList.append(0X84, "111");//
		tlvList.append(0X9F09, "111");//
		tlvList.append(0X9F41, "111");//
		tlvList.append(0X9F63, "111");//
		tlvList.append(0X91, "111");//
		tlvList.append(0X71, "111");//
		tlvList.append(0X72, "111");//
		//sendMsg.set(55,tlvList.pack());//TLV格式
		
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
		*/
		
		//sendMsg.set(60,"00123456");//自定义域 : 交易类型码+批次号
		//lrw add 20150909
		//磁条卡
		//sendMsg.set(60,"22"+batch_no+"081500000000");//需要验证BCD码
		sendMsg.set(60,"22"+batch_no+"08150"+mcc+"000"); //个人消费接口修改
		//String field63_2="13631324647";
		String field63 = "0  "+seq_no + "|"+ order_no + "|"+ longitude+","+latitude+"|"+merchant_mobile_no;
		sendMsg.set(63,field63);//经纬度
		//System.out.println("--------------------63kkkkkkkkkk"+field63);
		sendMsg.set(64,"12345678".getBytes());//MAC
		
		System.out.println("====lrw=0====="+sendMsg.toString());

		ISOPackager packager = new GenericPackager(ISO8583_XML);//xml文件会附在附件中
		sendMsg.setPackager(packager);
		
		//计算Mac值
		byte[] sendMsgByte = sendMsg.pack();
		byte[] sendMsgByte1 = new byte[sendMsg.pack().length - 8];
		System.arraycopy(sendMsgByte, 0, sendMsgByte1, 0, sendMsgByte1.length);
		LogUtils.info(ISOUtil.hexString(sendMsgByte1));// 用来计算MacBlock的报文
		//String field64 = gemMac(trans_no,mpos_no,merchant_id,ISOUtil.hexString(sendMsgByte1));
		Map macMap = gemMac(trans_no,mpos_no,merchant_id, ISOUtil.hexString(sendMsgByte1));
		String res_code = (String)macMap.get("res_code");
		if(!"00".equals(res_code)){
			Map resDataMap = new HashMap();
			resDataMap.put("return_code", res_code);
			resDataMap.put("return_msg", AppConfig.getMessage("mpos.rescode."+res_code));
			return resDataMap;
		}
		String field64 = (String)macMap.get("field62");
		LogUtils.info("生成的Mac值field64="+field64);
		byte[] recByte = ISOUtil.hex2byte(field64);//64域值
		sendMsg.set(64, recByte);//设置Mac值

		LogUtils.info("--------------------------发送的报文(S103:磁条卡消费)--------------------------");
		System.out.println("lrw====1====="+sendMsg.toString());
		System.out.println("lrw====2====="+sendMsg.pack());
		
		LogUtils.info(ISOUtil.hexString(sendMsg.pack()));
		sendMsg.dump(System.out,"");  
		
		ISOChannel channel = new PostChannel(ip, port, packager,header);
		channel.connect();
		channel.send(sendMsg);//发送
		
		ISOMsg receiveMsg = channel.receive();//接收
		LogUtils.info("--------------------------接收的报文(S103:磁条卡消费)--------------------------");
		LogUtils.info(ISOUtil.hexString(receiveMsg.pack()));
		receiveMsg.dump(System.out,"");  
				
//		<isomsg direction="incoming">
//		  <!-- org.jpos.iso.packager.GenericPackager[config/iso8583.xml] -->
//		  <header>6000000000603100000000</header>
//		  <field id="0" value="0210"/>
//		  <field id="2" value="1111111111111111"/>主账号
//		  <field id="3" value="190000"/> //交易处理码
//		  <field id="4" value="000000000010"/>交易金额
//		  <field id="11" value="000001"/>POS终端交易流水
//		  <field id="12" value="172855"/>受卡方所在地时间
//		  <field id="13" value="0720"/>受卡方所在地日期
//		  <field id="25" value="82"/>服务点条件码
//		  <field id="37" value="000001172855"/>POS中心系统流水号
//		  <field id="39" value="A0"/>应答码
//		  <field id="41" value="00000001"/>终端代码
//		  <field id="42" value="303581041311204"/>商户代码
//		  <field id="44" value="                      "/>附加响应数据
//		44.1 发卡机构码
//		44.2 收单机构码
		//37 系统参考号 
		//38	授权码
//		  <field id="49" value="156"/>交易货币代码
//		  <field id="53" value="2600000000000000" type="binary"/>安全控制信息
//		  <field id="60" value="00123456"/>交易类型码+批次号
//		  <field id="64" value="3444324438443835" type="binary"/> MAC
//		</isomsg>
		
		String field39 = receiveMsg.getString(39);
		LogUtils.info("(S103：磁条卡消费)39域[返回码]=" + field39);
		
		channel.disconnect();
			
		//
		Map resDataMap = new HashMap();
		resDataMap.put("return_code", field39);
		resDataMap.put("return_msg", AppConfig.getMessage("mpos.rescode."+field39));
	
		if("00".equals(field39)){//如果返回成功，则返回相关数据
			resDataMap.put("field2", receiveMsg.getString(2));//主账号
			resDataMap.put("field3", receiveMsg.getString(3));//交易处理码
			resDataMap.put("field4", receiveMsg.getString(4));//交易金额
			resDataMap.put("field11", receiveMsg.getString(11));//POS终端交易流水
			resDataMap.put("field12", receiveMsg.getString(12));//受卡方所在地时间
			resDataMap.put("field13", receiveMsg.getString(13));//受卡方所在地日期
			resDataMap.put("field15", receiveMsg.getString(15));//清算日期 --- add by Zed 2015-09-16
			resDataMap.put("field37", receiveMsg.getString(37));//系统参考号
			resDataMap.put("field38", receiveMsg.getString(38));//授权码
			resDataMap.put("field39", receiveMsg.getString(39));//应答码
			resDataMap.put("field441", receiveMsg.getString(44).substring(0,8));//发卡机构码
			resDataMap.put("field442", receiveMsg.getString(44).substring(8));//收单机构码
			//resDataMap.put("", receiveMsg.getString(2));
			
			//新增加的值
			resDataMap.put("field00",receiveMsg.getString(0));
			resDataMap.put("field23",receiveMsg.getString(23));
			resDataMap.put("field25",receiveMsg.getString(25)) ;
			resDataMap.put("field32",receiveMsg.getString(32)) ;
			resDataMap.put( "field41",receiveMsg.getString(41)) ;
			resDataMap.put("field42",receiveMsg.getString(42)) ;
			resDataMap.put("field44",receiveMsg.getString(44)) ;
			resDataMap.put("field49",receiveMsg.getString(49)) ;
			resDataMap.put("field53",receiveMsg.getString(53)) ;
			resDataMap.put("field55",receiveMsg.getString(55));
			resDataMap.put("field60",receiveMsg.getString(60)) ;
			resDataMap.put("field63",receiveMsg.getString(63));
			resDataMap.put("field64",receiveMsg.getString(64)) ;
			
			resDataMap.put("return_code", RetMsgContants.Code0000);
		}
		
		//交易应答码中仅"00"为交易成功，其它为交易不成功。
		return resDataMap;
	}

//	/**
//	 * POS参数传递（IC卡交易需要厂商公钥）
//	 * 
//	 * @param transNo 流水号 
//	 * @param terminalKey 终端密钥
//	 * @param terminalSeq 终端序列号
//	 * @param mpos_no 终端号
//	 * @param merchant_id 商户号
//	 * @throws Exception
//	 */
//	public static Map requestPosParams(String terminalKey,String terminalSeq, String mpos_no,
//			String merchant_id) throws Exception {
//		
//		ISOMsg sendMsg = new ISOMsg();
//		
//		// 设置报文头
//		String header = "6000000000603100000000";
//		sendMsg.setHeader(hexStringToBytes(header));
//		
//		// 设置报文内容
//		sendMsg.set(0,"0800");//交易类型
//		sendMsg.set(41,mpos_no);//终端号
//		sendMsg.set(42,merchant_id);//商户号
//		sendMsg.set(60,"96000000390");//交易类型码+批次号+网络管理信息码
//		
//		//终端参数信息
//		sendMsg.set(62,ISOUtil.hex2byte(terminalKey));
//		
//		ISOPackager packager = new GenericPackager(ISO8583_XML);//xml文件会附在附件中
//		sendMsg.setPackager(packager);
//		
//		LogUtils.info("---发送的报文：");
//		LogUtils.info(ISOUtil.hexString(sendMsg.pack())); 
//		sendMsg.dump(System.out,"");  
//		
//		ISOChannel channel = new PostChannel(ip, port, packager, header);
//		channel.connect();
//		channel.send(sendMsg);//发送
//		
//		ISOMsg receiveMsg = channel.receive();//接收
//		LogUtils.info("---接收的报文：");
//		LogUtils.info(ISOUtil.hexString(receiveMsg.pack())); 
//		receiveMsg.dump(System.out,"");  
//		
//		String field39 = receiveMsg.getString(39);
//	
//		channel.disconnect();
//		
//		//交易应答码中仅"00"为交易成功，其它为交易不成功。
////		if("00".equals(field39)){
////			return receiveMsg.getString(62);//返回 终端密钥
////		}else{
////			return "";
////		}	
////		
//		Map resDataMap = new HashMap();
//		resDataMap.put("return_code", field39);
//		resDataMap.put("return_msg", AppConfig.getMessage("mpos.rescode."+field39));
//	
//		if("00".equals(field39)){//如果返回成功，则返回相关数据
//			resDataMap.put("return_code", RetMsgContants.Code0000);
//			resDataMap.put("terminal_key", receiveMsg.getString(62));
//		}
//		
//		return resDataMap;
//	}
	
	/**
	 * 消费 (IC卡交易)
	 * 
	 * 需要输入参数：主帐号，交易金额(12位，精确到分)，交易流水，终端号，商户号，银行卡号，批次号，二磁道数据,密码，订单号，经度，纬度，55域
	 * 
	 * @param accountNo
	 * @param amount
	 * @param trans_no
	 * @param mpos_no
	 * @param merchant_id
	 * @param card_no
	 * @param trackTwoData
	 * @param password
	 * @param order_no 订单号
	 * @param longitude 经度
	 * @param latitude 纬度
	 * @param field55 IC卡数据域
	 * @param field23 卡版序列号
	 * @throws Exception
	 */
	public static Map transByICCard(String accountNo, String amount, String trans_no,String batch_no,
			String mpos_no,String seq_no, String merchant_id, String card_no,
			String trackTwoData, String password,String order_no,String longitude,String latitude, String field55, String field23,String dateExpire,String mcc,String merchant_mobile_no)
			throws Exception {

		ISOMsg sendMsg = new ISOMsg();
		
		// 设置报文头
		String header = "6000000000603100000000";
		sendMsg.setHeader(hexStringToBytes(header));
		
		sendMsg.set(0,"0200");//交易类型
		sendMsg.set(2,card_no);//卡号
		sendMsg.set(3, "190000");//交易处理码
		sendMsg.set(4, amount);//交易金额
		sendMsg.set(11,trans_no);//POS终端交易流水
		sendMsg.set(14,dateExpire);//卡有效期
		sendMsg.set(22,"051");//服务点输入方式码
		sendMsg.set(25,"82");//服务点条件码
		sendMsg.set(26,"12");//密码最大长度
		sendMsg.set(35, ISOUtil.hex2byte(trackTwoData));//二磁道数据，，Track two data
		sendMsg.set(41,mpos_no);//终端号
		sendMsg.set(42,merchant_id);//商户号
		sendMsg.set(49,"156");//交易货币代码
		sendMsg.set(52, ISOUtil.hex2byte(password));//个人标识码数据
		sendMsg.set(53, ISOUtil.hex2byte("2600000000000000"));//刷卡时输入密码才有53域，最长16位，针对信用卡不输密码的情况
		
		//NSString *pinkey = @"0be6e3318a8334026b462c949138802c";//52域
	    //NSString *macKey = @"70f2f2d6c76d26768c19e6a707d5c7e5";//64域
	    //NSString *trkKey = @"eff79eba613ed07079d60df29d540726";//35,36域
		
//55	IC卡数据域	最大255字节数据 ，，，以下子域中的M仅当该交易为IC卡交易时有效
		//sendMsg.set(55,field55.getBytes());//TLV格式
		sendMsg.set(55, ISOUtil.hex2byte(field55));//

		sendMsg.set(22,"051");//服务点输入方式码
		sendMsg.set(23,field23);//卡片序列号

		//IC卡
		//60.6  lrw   20151026  
		//sendMsg.set(60,"22"+batch_no+"08150"+mcc+"000"); //个人消费接口修改
		//sendMsg.set(60,"22"+batch_no+"081500000000");
		sendMsg.set(60,"22"+batch_no+"08150"+mcc+"000");
		String field63 = "0  "+seq_no + "|"+ order_no + "|"+ longitude+","+latitude+ "|"+merchant_mobile_no;
		sendMsg.set(63,field63);//经纬度
		//System.out.println("-----------------------field63"+field63);
		sendMsg.set(64,"12345678".getBytes());//MAC

		ISOPackager packager = new GenericPackager(ISO8583_XML);//xml文件会附在附件中
		sendMsg.setPackager(packager);
		
		//计算Mac值
		byte[] sendMsgByte = sendMsg.pack();
		byte[] sendMsgByte1 = new byte[sendMsg.pack().length - 8];
		System.arraycopy(sendMsgByte, 0, sendMsgByte1, 0, sendMsgByte1.length);
		LogUtils.info(ISOUtil.hexString(sendMsgByte1));// 用来计算MacBlock的报文
		//String field64 = gemMac(trans_no,mpos_no,merchant_id,ISOUtil.hexString(sendMsgByte1));
		Map macMap = gemMac(trans_no,mpos_no,merchant_id, ISOUtil.hexString(sendMsgByte1));
		String res_code = (String)macMap.get("res_code");
		if(!"00".equals(res_code)){
			Map resDataMap = new HashMap();
			resDataMap.put("return_code", res_code);
			resDataMap.put("return_msg", AppConfig.getMessage("mpos.rescode."+res_code));
			return resDataMap;
		}
		String field64 = (String)macMap.get("field62");
		
		LogUtils.info("生成的Mac值field64="+field64);
		byte[] recByte = ISOUtil.hex2byte(field64);//64域值
		sendMsg.set(64, recByte);//设置Mac值

		LogUtils.info("--------------------------发送的报文(S103：IC卡消费)--------------------------");
		LogUtils.info(ISOUtil.hexString(sendMsg.pack()));
		sendMsg.dump(System.out,"");  
		
		ISOChannel channel = new PostChannel(ip, port, packager,header);
		channel.connect();
		channel.send(sendMsg);//发送
		
		ISOMsg receiveMsg = channel.receive();//接收
		LogUtils.info("--------------------------接收的报文(S103：IC卡消费)--------------------------");
		LogUtils.info(ISOUtil.hexString(receiveMsg.pack()));
		receiveMsg.dump(System.out,"");  

		String field39 = receiveMsg.getString(39);
		LogUtils.info("(S103：IC卡消费)39域[返回码]：" + field39);
		
		channel.disconnect();
			
		//
		Map resDataMap = new HashMap();
		resDataMap.put("return_code", field39);
		resDataMap.put("return_msg", AppConfig.getMessage("mpos.rescode."+field39));
	
		if("00".equals(field39)){//如果返回成功，则返回相关数据
			resDataMap.put("field2", receiveMsg.getString(2));//主账号
			resDataMap.put("field3", receiveMsg.getString(3));//交易处理码
			resDataMap.put("field4", receiveMsg.getString(4));//交易金额
			resDataMap.put("field11", receiveMsg.getString(11));//POS终端交易流水
			resDataMap.put("field12", receiveMsg.getString(12));//受卡方所在地时间
			resDataMap.put("field13", receiveMsg.getString(13));//受卡方所在地日期
			resDataMap.put("field15", receiveMsg.getString(15));//清算日期 --- add by Zed 2015-09-16
			resDataMap.put("field37", receiveMsg.getString(37));//系统参考号
			resDataMap.put("field38", receiveMsg.getString(38));//授权码
			resDataMap.put("field39", receiveMsg.getString(39));//应答码
			resDataMap.put("field441", receiveMsg.getString(44).substring(0,8));//发卡机构码
			resDataMap.put("field442", receiveMsg.getString(44).substring(8));//收单机构码
			//resDataMap.put("", receiveMsg.getString(2));
			
			
			//新增加的值
			resDataMap.put("field00",receiveMsg.getString(0));
			resDataMap.put("field23",receiveMsg.getString(23));
			resDataMap.put("field25",receiveMsg.getString(25)) ;
			resDataMap.put("field32",receiveMsg.getString(32)) ;
			resDataMap.put( "field41",receiveMsg.getString(41)) ;
			resDataMap.put("field42",receiveMsg.getString(42)) ;
			resDataMap.put("field44",receiveMsg.getString(44)) ;
			resDataMap.put("field49",receiveMsg.getString(49)) ;
			resDataMap.put("field53",receiveMsg.getString(53)) ;
			resDataMap.put("field55",receiveMsg.getString(55));
			resDataMap.put("field60",receiveMsg.getString(60)) ;
			resDataMap.put("field63",receiveMsg.getString(63));
			resDataMap.put("field64",receiveMsg.getString(64)) ;
			
			resDataMap.put("return_code", RetMsgContants.Code0000);
		}
		
		//交易应答码中仅"00"为交易成功，其它为交易不成功。
		return resDataMap;
	}
	
	/**
	 * 上传电子签名
	 * 
	 * 需要输入的参数：银行卡号，交易金额(13位)，流水号，清算日期(4位),终端号，商户号，电子签名，55域
	 * 
	 * @param cardNo
	 * @param amount
	 * @param trans_no
	 * @param settleDate
	 * @param mpos_no
	 * @param merchant_id
	 * @throws Exception
	 */
	public static Map uploadEsign(String cardNo, String amount,
			String trans_no,String batch_no, String settleDate, String mpos_no,
			String merchant_id,String electronicSign,byte[] field55,String filed37,Map  map55) throws Exception {

		ISOMsg sendMsg = new ISOMsg();

		// 设置报文头
		String header = "6000000000603100000000";
		sendMsg.setHeader(hexStringToBytes(header));

		sendMsg.set(0, "0820");// 交易类型
		sendMsg.set(2, cardNo);// 卡号
		sendMsg.set(4, amount);// 交易金额
		sendMsg.set(11, trans_no);// POS终端交易流水
		sendMsg.set(15, settleDate);// 清算日期
		//sendMsg.set(37, "123456789123");// 交易参考号
		sendMsg.set(37, filed37);//20150915智付确认37域传系统参考号
		sendMsg.set(41, mpos_no);// 终端号
		sendMsg.set(42, merchant_id);// 商户号

		// 55 IC卡数据域 最大255字节数据
		//需要传的值 
		//一、交易通用信息
		//二、 IC 卡有关信息
		//四、 原交易信息
		//五、终端统计信息
		TLVList tlvList = new TLVList();
		//一、交易通用信息（以下域均指原交易应答报文）
		String merchant_name = (String)map55.get("MERCHANT_NAME");
		//String merchant_name = "kanongkeji";
		LogUtils.info("===============merchant_name============"+merchant_name);
		String merchant_name_Hex = ISOUtil.hexString(merchant_name.getBytes());
		LogUtils.info("===============merchant_name_Hex(ISOUtil.hexString(merchant_name.getBytes()))============"+merchant_name_Hex);
		tlvList.append(0XFF00,merchant_name_Hex);//商户名称 M   后台已经判断  必输值
		String tansValuesHex = ISOUtil.hexString("消费".getBytes());
		LogUtils.info("==========tansValuesHex========"+tansValuesHex);
		tlvList.append(0XFF01,tansValuesHex);//交易类型M 消费交易要素   参考文档
		tlvList.append(0XFF02,"01");//操作员号M	
		String acquiring_institution_no = (null!=map55.get("ACQUIRING_INSTITUTION_NO"))?(String)map55.get("ACQUIRING_INSTITUTION_NO"):"";//收单机构码  11位  不足的补零
		String card_issuers_no = (null!=map55.get("CARD_ISSUERS_NO"))?(String)map55.get("CARD_ISSUERS_NO"):"";//发卡机构码 11位  不足的补零
		LogUtils.info("===============acquiring_institution_no============"+acquiring_institution_no);
		LogUtils.info("===============card_issuers_no============"+card_issuers_no);
		tlvList.append(0XFF03,acquiring_institution_no.trim());//收单机构
		tlvList.append(0XFF04,card_issuers_no.trim());//发卡机构
		String date_expire =  (null!=map55.get("DATE_EXPIRE"))?(String)map55.get("DATE_EXPIRE"):"" ;
		LogUtils.info("==========date_expire======"+date_expire);
		if(!date_expire.isEmpty())
		{
		tlvList.append(0XFF05,date_expire );//有效期
		}
		String  F13 = (null!=map55.get("CARD_LOCAL_DATE"))?(String)map55.get("CARD_LOCAL_DATE"):"" ;
		String  F12 = (null!=map55.get("CARD_LOCAL_TIME"))?(String)map55.get("CARD_LOCAL_TIME"):"" ;
		LogUtils.info("==========F13============="+F13);
		LogUtils.info("===========F12============"+F12);
		tlvList.append(0XFF06,F13+F12 );//日期时间
		String  auth_code = (null!= map55.get("AUTH_CODE"))?(String)map55.get("AUTH_CODE"):"";
		if(!auth_code.isEmpty())
		{
		tlvList.append(0XFF07,auth_code);//授权码
		tlvList.append(0XFF64,auth_code);
		}
//		tlvList.append(0XFF08,"");//小费金额
		String F63 = (null!= map55.get("F63"))?(String)map55.get("F63"):"";
		LogUtils.info("===========F63============"+F63);
//		tlvList.append(0XFF09,F63);//卡组织
		String  f63Hex = ISOUtil.hexString(F63.getBytes());
		LogUtils.info("============f63Hex======="+f63Hex);
		tlvList.append(0XFF09,f63Hex);//卡组织
		
		
		String F49 = (null!= map55.get("F49"))?(String)map55.get("F49"):"";
		LogUtils.info("===========F49============"+F49);
		tlvList.append(0XFF0A,F49);//交易币种
//		tlvList.append(0XFF0B,"111" );//持卡人手机号码
//		tlvList.append(0XFF0C,"");//收单机构中文
//		tlvList.append(0XFF0D,"");//发卡机构中文

		//二、 IC 卡有关信息（以下域均指原交易请求报文）
//		tlvList.append(0XFF20,"");//应用标签（终端不再使用，系统支持，为兼容存量终端）
//		tlvList.append(0XFF21,"");//应用名称（终端不再使用，系统支持，为兼容存量终端）
//		tlvList.append(0XFF22, "111" );//应用标识
//		tlvList.append(0XFF23, "111" );//应用密文
//		tlvList.append(0XFF24,"");//充值后卡片余额
//		tlvList.append(0XFF25,"");//转入卡卡号  62域消费交易没有
//		tlvList.append(0XFF26, "111" );//不可预知数
//		tlvList.append(0XFF27,"");//应用交互特征
//		tlvList.append(0XFF28,"");//终端验证结果
//		tlvList.append(0XFF29,"");//交易状态信息
//		tlvList.append(0XFF30,"111"  );//应用标签
//		tlvList.append(0XFF31, "111" );//应用名称
//		tlvList.append(0XFF2A,"");//应用交易计数器
//		tlvList.append(0XFF2B,"");//发卡应用数据
//		//四、 原交易信息（以下域均指原交易请求报文）
//		tlvList.append(0XFF60, "111");//原凭证号
//		tlvList.append(0XFF61,batch_no);//原批次号
//		tlvList.append(0XFF62,"111");//原参考号
//		tlvList.append(0XFF63,"111");//原交易日期
//		tlvList.append(0XFF64,auth_code);//原授权码    移到0XFF07后面
//		tlvList.append(0XFF65,"");//原终端号
//		//五、终端统计信息
//		tlvList.append(0XFF70,"");//当前交易打印张数
		
		sendMsg.set(55, tlvList.pack());// TLV格式
		//String field60 = "";
		sendMsg.set(60, "07000001800");// 自定义域 : 交易类型码+批次号+网络管理码
		
		String electronicSignHex = ISOUtil.hexString(electronicSign.getBytes());
		sendMsg.set(62, ISOUtil.hex2byte(electronicSignHex));// 电子签名 < 999byte
		
		sendMsg.set(64, "12345678".getBytes());// 调用计算Mac的接口生成的Mac
		
		ISOPackager p = new GenericPackager(ISO8583_XML);// xml文件会附在附件中
		sendMsg.setPackager(p);
		
		//计算Mac值
		byte[] sendMsgByte = sendMsg.pack();
		byte[] sendMsgByte1 = new byte[sendMsg.pack().length - 8];
		System.arraycopy(sendMsgByte, 0, sendMsgByte1, 0, sendMsgByte1.length);
		LogUtils.info(ISOUtil.hexString(sendMsgByte1));// 用来计算MacBlock的报文
		//String field64 = gemMac(trans_no,mpos_no,merchant_id,ISOUtil.hexString(sendMsgByte1));
		Map macMap = gemMac(trans_no,mpos_no,merchant_id, ISOUtil.hexString(sendMsgByte1));
		String res_code = (String)macMap.get("res_code");
		if(!"00".equals(res_code)){
			Map resDataMap = new HashMap();
			resDataMap.put("return_code", res_code);
			resDataMap.put("return_msg", AppConfig.getMessage("mpos.rescode."+res_code));
			return resDataMap;
		}
		String field64 = (String)macMap.get("field62");
		
		LogUtils.info("生成的Mac值field64="+field64);
		byte[] recByte = ISOUtil.hex2byte(field64);//64域值
		sendMsg.set(64, recByte);//设置Mac值

		LogUtils.info("--------------------------发送的报文(S104：上传电子签名)--------------------------");
		LogUtils.info(ISOUtil.hexString(sendMsg.pack()));
		sendMsg.dump(System.out, "");

		ISOChannel channel = new PostChannel(ip, port, p, header);
		channel.connect();
		channel.send(sendMsg);// 发送

		ISOMsg receiveMsg = channel.receive();// 接收
		LogUtils.info("--------------------------接收的报文(S104：上传电子签名)--------------------------");
		LogUtils.info(ISOUtil.hexString(receiveMsg.pack()));
		receiveMsg.dump(System.out, "");

		String field39 = receiveMsg.getString(39);
		LogUtils.info("(S104：上传电子签名)39域[返回码]：" + field39);
		
		channel.disconnect();
		
		Map resDataMap = new HashMap();
		resDataMap.put("return_code", field39);
		resDataMap.put("return_msg", AppConfig.getMessage("mpos.rescode."+field39));
		
		if("00".equals(field39)){//如果返回成功，则返回相关数据
			resDataMap.put("return_code", RetMsgContants.Code0000);
		}
		
		//交易应答码中仅"00"为交易成功，其它为交易不成功。
		return resDataMap;
	}
	
	/**
	 * 生成Mac值
	 * 
	 * 流水号，终端号，商户号，上送报文(二进制)
	 * 
	 * @param transNo
	 * @param mpos_no
	 * @param merchant_id
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static Map gemMac(String transNo,String mpos_no,String merchant_id,String data) throws Exception {

		ISOMsg sendMsg = new ISOMsg();

		// 设置报文头
		String header = "6000000000603100000000";
		sendMsg.setHeader(hexStringToBytes(header));

		// 设置报文内容
		sendMsg.set(0, "0720");// 交易类型
		sendMsg.set(3, "000000");//
		sendMsg.set(11, transNo);// 流水号
		sendMsg.set(41, mpos_no);// 终端号
		sendMsg.set(42, merchant_id);// 商户号

		// 上送报文，服务器端拼接,二进制
		String macBlock = MacUtil.gemMacBlock(data);
		LogUtils.info("(获取智付的mac)生成Mac Block为：" + macBlock);// 84548DE1AA580EBE
		sendMsg.set(62, macBlock.getBytes());// 返回的62域值，ASCII码 3543453435444344 转成hexTostring

		ISOPackager packager = new GenericPackager(ISO8583_XML);// xml文件会附在附件中
		sendMsg.setPackager(packager);

		LogUtils.info("--------------------------发送的报文(获取智付mac)--------------------------");
		LogUtils.info(ISOUtil.hexString(sendMsg.pack()));
		sendMsg.dump(System.out, "");

		ISOChannel channel = new PostChannel(ip, port, packager, header);
		channel.connect();
		channel.send(sendMsg);// 发送

		ISOMsg receiveMsg = channel.receive();// 接收
		LogUtils.info("--------------------------接收的报文(获取智付mac)--------------------------");
		LogUtils.info(ISOUtil.hexString(receiveMsg.pack()));
		receiveMsg.dump(System.out, ""); // 打印成xml报文的格式

		String field39 = receiveMsg.getString(39);
		String field62 = receiveMsg.getString(62);
		LogUtils.info("(获取智付mac)39域[返回码]：" + receiveMsg.getString(39));
		LogUtils.info("(获取智付mac)62域[mac值,此值对应其他交易64域的值]：" + receiveMsg.getString(62));//Mac
		LogUtils.info("(获取智付mac)62域[mac值的长度]：" + receiveMsg.getString(62).length());

		channel.disconnect();
		
		Map resMap = new HashMap();
		//交易应答码中仅"00"为交易成功，其它为交易不成功。
		LogUtils.info("gemMac field39="+field39);
		LogUtils.info("gemMac field62="+field62);

		if("00".equals(field39)){
			resMap.put("res_code", "00");
			resMap.put("field62", field62);//返回 Mac
		}else{
			resMap.put("res_code", field39);
			//resMap.put("field62", field62);//返回 Mac
		}
		return resMap;
	}
	
	/**
	 * 查询余额 (IC卡余额)
	 * 
	 * 需要输入参数：交易流水，终端号，商户号，银行卡号，批次号，二磁道数据,密码，经度，纬度，55域
	 * 
	 * @param trans_no
	 * @param mpos_no
	 * @param merchant_id
	 * @param card_no
	 * @param trackTwoData
	 * @param password
	 * @param order_no 订单号
	 * @param longitude 经度
	 * @param latitude 纬度
	 * @param field55 IC卡数据域
	 * @param field23 卡版序列号
	 * @throws Exception
	 */
	public static Map queryBalanceByICCard(String trans_no,String batch_no,
			String mpos_no,String seq_no, String merchant_id, String card_no,
			String trackTwoData, String password,String longitude,String latitude, String field55, String field23,String dateExpire,String mcc,String merchant_mobile_no)
			throws Exception {

		ISOMsg sendMsg = new ISOMsg();
		
		// 设置报文头
		String header = "6000000000603100000000";
		sendMsg.setHeader(hexStringToBytes(header));
		
		sendMsg.set(0,"0200");//交易类型
		sendMsg.set(2,card_no);//卡号
		sendMsg.set(3, "300000");//交易处理码
		//sendMsg.set(4, amount);//交易金额
		sendMsg.set(11,trans_no);//POS终端交易流水
		sendMsg.set(14,dateExpire);//卡有效期
		sendMsg.set(22,"051");//服务点输入方式码
		sendMsg.set(23,field23);//卡片序列号
		sendMsg.set(25,"00");//服务点条件码
		sendMsg.set(26,"12");//密码最大长度
		sendMsg.set(35, ISOUtil.hex2byte(trackTwoData));//二磁道数据，，Track two data
		sendMsg.set(41,mpos_no);//终端号
		sendMsg.set(42,merchant_id);//商户号
		sendMsg.set(49,"156");//交易货币代码
		sendMsg.set(52, ISOUtil.hex2byte(password));//个人标识码数据
		sendMsg.set(53, ISOUtil.hex2byte("2600000000000000"));//刷卡时输入密码才有53域，最长16位，针对信用卡不输密码的情况
		
		sendMsg.set(55, ISOUtil.hex2byte(field55));//
		

		//IC卡
		//60.6  lrw   20151026  
		//sendMsg.set(60,"22"+batch_no+"08150"+mcc+"000"); //个人消费接口修改
		//sendMsg.set(60,"22"+batch_no+"081500000000");
		sendMsg.set(60,"01"+batch_no+"08150910400");
		String field63 = "0  "+seq_no + "|"+  longitude+","+latitude+ "|"+merchant_mobile_no;
		sendMsg.set(63,field63);//经纬度
		//System.out.println("-----------------------field63"+field63);
		sendMsg.set(64,"12345678".getBytes());//MAC

		ISOPackager packager = new GenericPackager(ISO8583_XML);//xml文件会附在附件中
		sendMsg.setPackager(packager);
		
		//计算Mac值
		byte[] sendMsgByte = sendMsg.pack();
		byte[] sendMsgByte1 = new byte[sendMsg.pack().length - 8];
		System.arraycopy(sendMsgByte, 0, sendMsgByte1, 0, sendMsgByte1.length);
		LogUtils.info(ISOUtil.hexString(sendMsgByte1));// 用来计算MacBlock的报文
		//String field64 = gemMac(trans_no,mpos_no,merchant_id,ISOUtil.hexString(sendMsgByte1));
		Map macMap = gemMac(trans_no,mpos_no,merchant_id, ISOUtil.hexString(sendMsgByte1));
		String res_code = (String)macMap.get("res_code");
		if(!"00".equals(res_code)){
			Map resDataMap = new HashMap();
			resDataMap.put("return_code", res_code);
			resDataMap.put("return_msg", AppConfig.getMessage("mpos.rescode."+res_code));
			return resDataMap;
		}
		String field64 = (String)macMap.get("field62");
		
		LogUtils.info("生成的Mac值field64="+field64);
		byte[] recByte = ISOUtil.hex2byte(field64);//64域值
		sendMsg.set(64, recByte);//设置Mac值

		LogUtils.info("--------------------------发送的报文(S105：IC卡余额查询)--------------------------");
		LogUtils.info(ISOUtil.hexString(sendMsg.pack()));
		sendMsg.dump(System.out,"");  
		
		ISOChannel channel = new PostChannel(ip, port, packager,header);
		channel.connect();
		channel.send(sendMsg);//发送
		
		ISOMsg receiveMsg = channel.receive();//接收
		LogUtils.info("--------------------------接收的报文(S105：IC卡余额查询)--------------------------");
		LogUtils.info(ISOUtil.hexString(receiveMsg.pack()));
		receiveMsg.dump(System.out,"");  

		String field39 = receiveMsg.getString(39);
		LogUtils.info("(S105：IC卡余额查询交易)39域[返回码]：" + field39);
		
		channel.disconnect();
			
		//
		Map resDataMap = new HashMap();
		resDataMap.put("return_code", field39);
		resDataMap.put("return_msg", AppConfig.getMessage("mpos.rescode."+field39));
	
		if("00".equals(field39)){//如果返回成功，则返回相关数据
			resDataMap.put("field2", receiveMsg.getString(2));//主账号
			resDataMap.put("field3", receiveMsg.getString(3));//交易处理码
			resDataMap.put("field4", receiveMsg.getString(4));//交易金额
			resDataMap.put("field11", receiveMsg.getString(11));//POS终端交易流水
			resDataMap.put("field12", receiveMsg.getString(12));//受卡方所在地时间
			resDataMap.put("field13", receiveMsg.getString(13));//受卡方所在地日期
			resDataMap.put("field15", receiveMsg.getString(15));//清算日期 --- add by Zed 2015-09-16
			resDataMap.put("field37", receiveMsg.getString(37));//系统参考号
			resDataMap.put("field38", receiveMsg.getString(38));//授权码
			resDataMap.put("field39", receiveMsg.getString(39));//应答码
			resDataMap.put("field441", receiveMsg.getString(44).substring(0,8));//发卡机构码
			resDataMap.put("field442", receiveMsg.getString(44).substring(8));//收单机构码
			//resDataMap.put("", receiveMsg.getString(2));
			
			
			//新增加的值
			resDataMap.put("field00",receiveMsg.getString(0));
			resDataMap.put("field23",receiveMsg.getString(23));
			resDataMap.put("field25",receiveMsg.getString(25)) ;
			resDataMap.put("field32",receiveMsg.getString(32)) ;
			resDataMap.put( "field41",receiveMsg.getString(41)) ;
			resDataMap.put("field42",receiveMsg.getString(42)) ;
			resDataMap.put("field44",receiveMsg.getString(44)) ;
			resDataMap.put("field49",receiveMsg.getString(49)) ;
			resDataMap.put("field53",receiveMsg.getString(53)) ;
			resDataMap.put("field54",receiveMsg.getString(54)) ;
			resDataMap.put("field55",receiveMsg.getString(55));
			resDataMap.put("field60",receiveMsg.getString(60)) ;
			resDataMap.put("field63",receiveMsg.getString(63));
			resDataMap.put("field64",receiveMsg.getString(64)) ;
			
			resDataMap.put("return_code", RetMsgContants.Code0000);
		}
		
		//交易应答码中仅"00"为交易成功，其它为交易不成功。
		return resDataMap;
	}
	
	/**
	 * 查询余额
	 * 
	 * 需要输入参数：交易流水，终端号，商户号，银行卡号，批次号，二磁道数据,密码，订单号，经度，纬度，55域
	 * 
	 * @param trans_no
	 * @param mpos_no
	 * @param merchant_id
	 * @param card_no
	 * @param trackTwoData
	 * @param password
	 * @param longitude 经度
	 * @param latitude 纬度
	 * @param field55
	 * @throws Exception
	 */
	public static Map queryBalance(String trans_no,String batch_no,
			String mpos_no,String seq_no, String merchant_id, String card_no,
			String trackTwoData, String password,String longitude,String latitude,String dateExpire,String mcc,String merchant_mobile_no)
			throws Exception {

		ISOMsg sendMsg = new ISOMsg();
		
		// 设置报文头
		String header = "6000000000603100000000";
		sendMsg.setHeader(hexStringToBytes(header));
		
		sendMsg.set(0,"0200");//交易类型
		//sendMsg.set(2,card_no);//卡号(智付说  磁条卡不用送第二域)
		sendMsg.set(3, "300000");//交易处理码
		sendMsg.set(11,trans_no);//POS终端交易流水
		sendMsg.set(14,dateExpire);//卡有效期
		sendMsg.set(22,"021");//服务点输入方式码
		sendMsg.set(25,"00");//服务点条件码
		sendMsg.set(26,"12");//密码最大长度
		sendMsg.set(35, ISOUtil.hex2byte(trackTwoData));//二磁道数据，，Track two data
		sendMsg.set(41,mpos_no);//终端号
		sendMsg.set(42,merchant_id);//商户号
		sendMsg.set(49,"156");//交易货币代码
		sendMsg.set(52, ISOUtil.hex2byte(password));//个人标识码数据
		sendMsg.set(53, ISOUtil.hex2byte("2600000000000000"));//刷卡时输入密码才有53域，最长16位，针对信用卡不输密码的情况
		
		//NSString *pinkey = @"0be6e3318a8334026b462c949138802c";//52域
	    //NSString *macKey = @"70f2f2d6c76d26768c19e6a707d5c7e5";//64域
	    //NSString *trkKey = @"eff79eba613ed07079d60df29d540726";//35,36域
	/*	
//55	IC卡数据域	最大255字节数据 ，，，以下子域中的M仅当该交易为IC卡交易时有效
		TLVList tlvList = new TLVList();
		tlvList.append(0X9F26, "111");//tag-
		tlvList.append(0X9F27, "111");//
		tlvList.append(0X9F10, "111");//
		tlvList.append(0X9F37, "111");//
		tlvList.append(0X9F36, "111");//
		tlvList.append(0X95, "111");//
		tlvList.append(0X9A, "111");//
		tlvList.append(0X9C, "111");//
		tlvList.append(0X9F02, "111");//
		tlvList.append(0X5F2A, "111");//
		tlvList.append(0X82, "111");//
		tlvList.append(0X9F1A, "111");//
		tlvList.append(0X9F03, "111");//
		tlvList.append(0X9F33, "111");//
		tlvList.append(0X9F34, "111");//
		tlvList.append(0X9F35, "111");//
		tlvList.append(0X9F1E, "111");//
		tlvList.append(0X84, "111");//
		tlvList.append(0X9F09, "111");//
		tlvList.append(0X9F41, "111");//
		tlvList.append(0X9F63, "111");//
		tlvList.append(0X91, "111");//
		tlvList.append(0X71, "111");//
		tlvList.append(0X72, "111");//
		//sendMsg.set(55,tlvList.pack());//TLV格式
		
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
		*/
		
		//sendMsg.set(60,"00123456");//自定义域 : 交易类型码+批次号
		//lrw add 20150909
		//磁条卡
		//sendMsg.set(60,"22"+batch_no+"081500000000");//需要验证BCD码
		sendMsg.set(60,"01"+batch_no+"08150910400"); //个人消费接口修改
		//String field63_2="13631324647";
		String field63 = "0  "+seq_no + "|"+ longitude+","+latitude+"|"+merchant_mobile_no;
		sendMsg.set(63,field63);//经纬度
		//System.out.println("--------------------63kkkkkkkkkk"+field63);
		sendMsg.set(64,"12345678".getBytes());//MAC
		
		System.out.println("====lrw=0====="+sendMsg.toString());

		ISOPackager packager = new GenericPackager(ISO8583_XML);//xml文件会附在附件中
		sendMsg.setPackager(packager);
		
		//计算Mac值
		byte[] sendMsgByte = sendMsg.pack();
		byte[] sendMsgByte1 = new byte[sendMsg.pack().length - 8];
		System.arraycopy(sendMsgByte, 0, sendMsgByte1, 0, sendMsgByte1.length);
		LogUtils.info(ISOUtil.hexString(sendMsgByte1));// 用来计算MacBlock的报文
		//String field64 = gemMac(trans_no,mpos_no,merchant_id,ISOUtil.hexString(sendMsgByte1));
		Map macMap = gemMac(trans_no,mpos_no,merchant_id, ISOUtil.hexString(sendMsgByte1));
		String res_code = (String)macMap.get("res_code");
		if(!"00".equals(res_code)){
			Map resDataMap = new HashMap();
			resDataMap.put("return_code", res_code);
			resDataMap.put("return_msg", AppConfig.getMessage("mpos.rescode."+res_code));
			return resDataMap;
		}
		String field64 = (String)macMap.get("field62");
		LogUtils.info("生成的Mac值field64="+field64);
		byte[] recByte = ISOUtil.hex2byte(field64);//64域值
		sendMsg.set(64, recByte);//设置Mac值

		LogUtils.info("--------------------------发送的报文(S105:磁条卡余额查询)--------------------------");
		System.out.println("lrw====1====="+sendMsg.toString());
		System.out.println("lrw====2====="+sendMsg.pack());
		
		LogUtils.info(ISOUtil.hexString(sendMsg.pack()));
		sendMsg.dump(System.out,"");  
		
		ISOChannel channel = new PostChannel(ip, port, packager,header);
		channel.connect();
		channel.send(sendMsg);//发送
		
		ISOMsg receiveMsg = channel.receive();//接收
		LogUtils.info("--------------------------接收的报文(S105:磁条卡余额查询)--------------------------");
		LogUtils.info(ISOUtil.hexString(receiveMsg.pack()));
		receiveMsg.dump(System.out,"");  
				
//		<isomsg direction="incoming">
//		  <!-- org.jpos.iso.packager.GenericPackager[config/iso8583.xml] -->
//		  <header>6000000000603100000000</header>
//		  <field id="0" value="0210"/>
//		  <field id="2" value="1111111111111111"/>主账号
//		  <field id="3" value="190000"/> //交易处理码
//		  <field id="4" value="000000000010"/>交易金额
//		  <field id="11" value="000001"/>POS终端交易流水
//		  <field id="12" value="172855"/>受卡方所在地时间
//		  <field id="13" value="0720"/>受卡方所在地日期
//		  <field id="25" value="82"/>服务点条件码
//		  <field id="37" value="000001172855"/>POS中心系统流水号
//		  <field id="39" value="A0"/>应答码
//		  <field id="41" value="00000001"/>终端代码
//		  <field id="42" value="303581041311204"/>商户代码
//		  <field id="44" value="                      "/>附加响应数据
//		44.1 发卡机构码
//		44.2 收单机构码
		//37 系统参考号 
		//38	授权码
//		  <field id="49" value="156"/>交易货币代码
//		  <field id="53" value="2600000000000000" type="binary"/>安全控制信息
//		  <field id="60" value="00123456"/>交易类型码+批次号
//		  <field id="64" value="3444324438443835" type="binary"/> MAC
//		</isomsg>
		
		String field39 = receiveMsg.getString(39);
		LogUtils.info("(S105：磁条卡余额查询)39域[返回码]=" + field39);
		
		channel.disconnect();
			
		//
		Map resDataMap = new HashMap();
		resDataMap.put("return_code", field39);
		resDataMap.put("return_msg", AppConfig.getMessage("mpos.rescode."+field39));
	
		if("00".equals(field39)){//如果返回成功，则返回相关数据
			resDataMap.put("field2", receiveMsg.getString(2));//主账号
			resDataMap.put("field3", receiveMsg.getString(3));//交易处理码
			resDataMap.put("field4", receiveMsg.getString(4));//交易金额
			resDataMap.put("field11", receiveMsg.getString(11));//POS终端交易流水
			resDataMap.put("field12", receiveMsg.getString(12));//受卡方所在地时间
			resDataMap.put("field13", receiveMsg.getString(13));//受卡方所在地日期
			resDataMap.put("field15", receiveMsg.getString(15));//清算日期 --- add by Zed 2015-09-16
			resDataMap.put("field37", receiveMsg.getString(37));//系统参考号
			resDataMap.put("field38", receiveMsg.getString(38));//授权码
			resDataMap.put("field39", receiveMsg.getString(39));//应答码
			resDataMap.put("field441", receiveMsg.getString(44).substring(0,8));//发卡机构码
			resDataMap.put("field442", receiveMsg.getString(44).substring(8));//收单机构码
			//resDataMap.put("", receiveMsg.getString(2));
			
			//新增加的值
			resDataMap.put("field00",receiveMsg.getString(0));
			resDataMap.put("field23",receiveMsg.getString(23));
			resDataMap.put("field25",receiveMsg.getString(25)) ;
			resDataMap.put("field32",receiveMsg.getString(32)) ;
			resDataMap.put( "field41",receiveMsg.getString(41)) ;
			resDataMap.put("field42",receiveMsg.getString(42)) ;
			resDataMap.put("field44",receiveMsg.getString(44)) ;
			resDataMap.put("field49",receiveMsg.getString(49)) ;
			resDataMap.put("field53",receiveMsg.getString(53)) ;
			resDataMap.put("field54",receiveMsg.getString(54)) ;
			resDataMap.put("field55",receiveMsg.getString(55));
			resDataMap.put("field60",receiveMsg.getString(60)) ;
			resDataMap.put("field63",receiveMsg.getString(63));
			resDataMap.put("field64",receiveMsg.getString(64)) ;
			
			resDataMap.put("return_code", RetMsgContants.Code0000);
		}
		
		//交易应答码中仅"00"为交易成功，其它为交易不成功。
		return resDataMap;
	}
	
	//----------- 以下是通用方法

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
