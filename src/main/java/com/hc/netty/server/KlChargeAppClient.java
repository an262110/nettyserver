package com.hc.netty.server;

import com.hc.app.model.Body0x12;
import com.hc.app.model.Body0x20;
import com.hc.app.model.Head;
import com.hc.app.model.Meg;
import com.hc.app.utils.HardwareFault;
import com.hc.app.utils.TimeUtils;
import com.hc.app.utils.ToolUtil;
import com.hc.common.config.AppConfig;
import com.hc.common.utils.LogUtils;
import org.apache.commons.lang.ArrayUtils;
import org.jpos.iso.ISOUtil;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 充电启停指令
 */
public class KlChargeAppClient {
	private static KlChargeAppClient idnAppClient = new KlChargeAppClient();
	private static String ip = AppConfig.getMessage("idn.pre.ip");
	private static int port = Integer.valueOf(AppConfig.getMessage("idn.pre.port"));//服务器监听端口
	
	public static KlChargeAppClient getInstance() {
		return idnAppClient;
	}
	public static void main(String[] args){
		//222222111111
		//111111222224
		String ret = KlChargeAppClient.getInstance().sendPre("111111222224", "3", "000000","13812345678","9999999999999999","01","00");
		System.out.println(ret);
	}


	/**
	 *
	 * @param ljaddress 终端逻辑地址 正序传过去的 解析后需要倒叙
	 * @param charge_type 充电指令 2 充电 3 停止充电
	 * @param pwd 密码 默认 111111
	 * @param account 用户账号 11位手机号
	 * @param dealCardNo 交易卡号 16位卡号 不足16位前面加0补齐
	 * @param gunNo 0-255之间 枪序号  1000000101
	 * @param chargeType 充电类型 暂时为00， 00：充满为止 01：充电持续时间HHMM 02：充电金额 03：充电电量
	 * @return ret 100 连接超时 200 发送成功 300 订单不存在 400 桩通讯断开
	 */
	public String sendPre(String ljaddress,String charge_type,String pwd,
						  String account,String dealCardNo,
						  String gunNo,String chargeType){
		String ret = null;
		byte[] ret_begin = new byte[]{(byte) 0x68};
		//终端逻辑地址
		byte[] ljbyteaddress = ToolUtil.hexStringToBytes(ljaddress);
		//d倒叙终端逻辑地址
//		byte[] lj_address = ToolUtil.bytesReverseOrder(ljbyteaddress);
		//主站地址与命令序号 起始字符（68H）
		byte[] ret_add = new byte[]{(byte) 0xC1,0x01,0x68};
		/*if("2".equals(charge_type)){
			ret_add = new byte[]{(byte) 0xC1,0x01,0x68};
		}else{
			ret_add = new byte[]{(byte) 0xC1,0x01,0x68};
		}*/
		//控制码0xB7
		byte[] ret_c = new byte[]{(byte) 0xB7};

		//权限等级
		byte[] ret_level = new byte[]{(byte) 0x11};
		//密码 111111
		byte[] bytes = ToolUtil.hexStringToBytes(pwd);
		//倒叙后的密码
		byte[] pwd_byte = ToolUtil.bytesReverseOrder(bytes);

		/**
		 * 启动充电数据项DATA格式 ---------------------------k开始
		 */

		//充电命令;
		byte[] ret_onOff = null;
		if("2".equals(charge_type)){
			//2：充电开始
			ret_onOff = new byte[]{(byte) 0x02};
		}else if("3".equals(charge_type)){
			//3：停止充电
			ret_onOff = new byte[]{(byte) 0x03};
		}

		/**
		 * 用户帐号 问题 1  现在账号是11位手机号  不能转成8字节
		 * 解决方案 把手机号码前面的1截掉
		 */
		//倒叙后的账户
		byte[] account_bytes = null;
		if(null != account && !"".equals(account)){
			account_bytes = ToolUtil.account_deal("00000" + account,2);
		}else{
			account_bytes = ToolUtil.fill0x00(8);
		}

		//交易卡号 19位十进制卡号 8
//		byte[] dealCardNo_bytes = ToolUtil.hexStringToBytes(ToolUtil.bin2HexString(Integer.toBinaryString(Integer.parseInt(dealCardNo))));

		//倒叙后的交易卡号
		byte[] dealCardNo_bytes = null;
		if(null != dealCardNo && !"".equals(dealCardNo)){
			dealCardNo_bytes = ToolUtil.bytesReverseOrder(ToolUtil.hexStringToBytes(new StringBuffer(dealCardNo).reverse().toString()));
		}else{
			dealCardNo_bytes = ToolUtil.fill0x00(8);
		}


		//交易流水号 19位十进制卡号 15  交易流水号为：时间戳(YYMMDDHHMMSS)+桩逻辑地址(6字节)+流水生成来源(1字节)+交易随机数(2字节)

		// 时间戳
		byte[] time_bytes = ToolUtil.hexStringToBytes(TimeUtils.getTimestap());

		//流水生成来源(1字节)
		byte[] ret_from = new byte[]{(byte) 0x00};
		//交易随机数(2字节)
		byte[] suiJiNo = HardwareFault.getSuiJiNo();

		List<byte[]> list = new ArrayList<byte[]>();
		list.add(time_bytes);
		list.add(ljbyteaddress);
		list.add(ret_from);
		list.add(suiJiNo);
		//生成的完整的倒叙交易流水号
		byte[] ret_cs_pre = ToolUtil.bytesReverseOrder(ToolUtil.appendByte(list));

		//充电枪编号  长度1-255
//		byte[] bytes_gun = ToolUtil.hexStringToBytes(ToolUtil.bin2HexString(ToolUtil.int2Bin(gunNo)));
		String gun_no_hex = ToolUtil.dec2HexString(gunNo);
		if(Integer.parseInt(gunNo) <= 15){
			gun_no_hex = "0" + gun_no_hex;
		}
		byte[] bytes_gun = ToolUtil.hexStringToBytes(gun_no_hex);


		/**
		 * 充电类型
		 */
		byte[] bytes_chargeType = null;
		if(null != chargeType && !"".equals(chargeType)){
			bytes_chargeType = ToolUtil.hexStringToBytes(chargeType);
		}else{
			bytes_chargeType = ToolUtil.fill0xFF(1);
		}


		/**
		 * 充电参数 倒叙
		 */
		byte[] bytes_cansu = ToolUtil.bytesReverseOrder(ToolUtil.fill0x00(4));

		//卡内余额 FFFFFFFF
//		byte[] bytes_yue = ToolUtil.fill0xFF(4);
		byte[] bytes_yue = new byte[]{(byte) 0xFE,(byte) 0x61,(byte) 0x00,(byte) 0x00};

		//组装数据
		List<byte[]> data = new ArrayList<byte[]>();
		data.add(ret_level);
		data.add(pwd_byte);
		data.add(ret_onOff);
//		data.add(account_bytes);
		data.add(account_bytes);//用户账号
		data.add(account_bytes);//交易卡号
		data.add(ret_cs_pre);
		data.add(bytes_gun);
		data.add(bytes_chargeType);
		data.add(bytes_cansu);
		data.add(bytes_yue);
		//处理好的完成数据数组
		byte[] data_bytes = ToolUtil.appendByte(data);
		/**
		 * 启动充电数据项DATA格式 ---------------------------结束
		 */

		//计算得出的倒叙数据长度
		byte[] ret_length = ToolUtil.intToBytes(data_bytes.length, 2, 1);

		System.out.println("长度=" + Arrays.toString(ret_length));

		//结束码
		byte[] ret_end = new byte[]{(byte) 0x16};

		List<byte[]> cs_list = new ArrayList<byte[]>();
		cs_list.add(ret_begin);
		cs_list.add(ljbyteaddress);
		cs_list.add(ret_add);
		cs_list.add(ret_c);
		cs_list.add(ret_length);
		cs_list.add(data_bytes);

		byte[] ret_cs_pres = ToolUtil.appendByte(cs_list);
		byte[] ret_cs = new byte[]{(byte) ToolUtil.getCS(ret_cs_pres)};

		List<byte[]> cent_list = new ArrayList<byte[]>();
		cent_list.add(ret_cs_pres);
		cent_list.add(ret_cs);
		cent_list.add(ret_end);

		//最终发送出去的数据
		byte[] sent_bytes = ToolUtil.appendByte(cent_list);

		ret = send(sent_bytes);
		return ret;
	}
	

	public static String send(byte[] end_send){
		InputStream 	in		= null;
		OutputStream 	out		= null;
		Socket 			socket	= null;
		String 			ret 	= null;
		try{
		   System.out.println("ip:"+ip);
		   System.out.println("port:"+port);
		   socket=new Socket(); 		   
		   socket.connect(new InetSocketAddress(ip,port),500);
		   socket.setKeepAlive(false);   //是否长连接
		   out=socket.getOutputStream();

			String end_sendStr = ISOUtil.hex2String(end_send);
			LogUtils.info("app调用后发送给后台的报文--------->>>"+end_sendStr);

		   out.write(end_send);
		   out.flush();	
		   
		   in=socket.getInputStream();
		   BufferedReader r=new BufferedReader(new InputStreamReader(in));
		   ret=r.readLine();

		}catch(Exception e){
			e.printStackTrace();
			ret = "100";
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					ret = "100";
				}
			}
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
					ret = "100";
				}
				out=null;
			}			
			if(socket!=null){
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					ret = "100";
				}
				socket=null;
			}
		}
		System.out.println(ret);
		   return ret;
	}
	
	public static String sendHK(int type,String msg){
		InputStream 	in		= null;
		OutputStream 	out		= null;
		Socket 			socket	= null;
		String 			ret 	= null;
		try{
		   //System.out.println("ip:"+ip);
		   //System.out.println("port:"+port);
		   socket=new Socket(); 		   
		   socket.connect(new InetSocketAddress(ip,8066),500);
		   socket.setKeepAlive(false);   //是否长连接

		   out=socket.getOutputStream();
		   int lenth=msg.length();
		   //byte[] len=toByteArray(lenth, 2);
		   byte[] head=new byte[8];
		   head[0]=(byte)0x7E;
		   head[1]=(byte)0x70;
		   head[2]=(byte) ((type >>> 8) & 0xff);
		   head[3]=(byte) ((type >>> 0) & 0xff);
		   head[4]=(byte) ((lenth >>> 24) & 0xff);
		   head[5]=(byte) ((lenth >>> 16) & 0xff);
		   head[6]=(byte) ((lenth >>> 8) & 0xff);
		   head[7]=(byte) ((lenth >>> 0) & 0xff);
		   byte[] sendMsg=head;
		   if(!"".equals(msg))
		     sendMsg=(byte[]) ArrayUtils.addAll(head,msg.getBytes("ascii"));
		   
		   out.write(sendMsg);
		   out.flush();	
		   
		   in=socket.getInputStream();
		   BufferedReader r=new BufferedReader(new InputStreamReader(in));
		   ret=r.readLine();			
			
		}catch(Exception e){
			e.printStackTrace();
			ret = "100";
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					ret = "100";
				}
			}
			if(out!=null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
					ret = "100";
				}
				out=null;
			}			
			if(socket!=null){
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					ret = "100";
				}
				socket=null;
			}
		}
		   return ret;
	}

	 public static byte[] toByteArray(int iSource, int iArrayLen) {
		    
		    int len=4;
		    if(iArrayLen<4){
		    	len=iArrayLen;
		    }
		    byte[] bLocalArr = new byte[len];
		    for (int i = len-1,j=0;i>=0 ; i--,j++) {
		        bLocalArr[i] = (byte) (iSource >> 8 * j & 0xFF);
		    }
		    return bLocalArr;
		}
	 
	 //发送智网充电
	public static String sendZW(String charge_order_id, String gun_code,
			String charge_type){
			InputStream 	in		= null;
			OutputStream 	out		= null;
			Socket 			socket	= null;
			String 			ret 	= null;
			try{
			   socket=new Socket(); 		   
			   socket.connect(new InetSocketAddress("192.168.0.147",8044),500);
			   socket.setKeepAlive(false);   //是否长连接
			   out=socket.getOutputStream();
			   
			   //head 写枪 body 写订单流水号
			   Head head = null;
			   Meg meg = null;
			   if("1".equals(charge_type)){
				   head = new Head("1702221536134538",new byte[]{0x20});
				   Body0x20 body = new Body0x20(charge_order_id);
				   meg = Meg.message(head, body);
			   }else{
				   // 停止充电
				   head = new Head("1702221536134538",new byte[]{0x12});
				   //Body0x22 body = new Body0x22(charge_order_id);
				   Body0x12 body = new Body0x12(charge_order_id);
				   meg = Meg.message(head, body);
			   }			   
			   
//			   int lenth=msg.length();
//			   byte[] head=new byte[8];
//			   head[0]=(byte)0x7E;
//			   head[1]=(byte)0x70;
//			   head[2]=(byte) ((type >>> 8) & 0xff);
//			   head[3]=(byte) ((type >>> 0) & 0xff);
//			   head[4]=(byte) ((lenth >>> 24) & 0xff);
//			   head[5]=(byte) ((lenth >>> 16) & 0xff);
//			   head[6]=(byte) ((lenth >>> 8) & 0xff);
//			   head[7]=(byte) ((lenth >>> 0) & 0xff);
//			   byte[] sendMsg=head;
//			   if(!"".equals(msg))
//			     sendMsg=(byte[]) ArrayUtils.addAll(head,msg.getBytes("ascii"));
			   
			   out.write(meg.getByte());
			   out.flush();	
			   
			   in=socket.getInputStream();
			   BufferedReader r=new BufferedReader(new InputStreamReader(in));
			   ret=r.readLine();			
				
			}catch(Exception e){
				e.printStackTrace();
				ret = "100";
			}finally{
				if(in!=null){
					try {
						in.close();
					} catch (IOException e1) {
						e1.printStackTrace();
						ret = "100";
					}
				}
				if(out!=null){
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
						ret = "100";
					}
					out=null;
				}			
				if(socket!=null){
					try {
						socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
						ret = "100";
					}
					socket=null;
				}
			}
			   return ret;
		}



}
