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
import java.util.List;

/**
 * 下发结算信息(命令0x16)指令
 */
public class KlSettlementAppClient {
	private static KlSettlementAppClient idnAppClient = new KlSettlementAppClient();
	private static String ip = AppConfig.getMessage("idn.pre.ip");
	private static int port = Integer.valueOf(AppConfig.getMessage("idn.pre.port"));//服务器监听端口
	
	public static KlSettlementAppClient getInstance() {
		return idnAppClient;
	}
	public static void main(String[] args){
		//222222111111
		//111111222224
		String ret= KlSettlementAppClient.getInstance().sendPre("111111222222", "180604163840",
				"111111","1","1250","50","250","1000","500");
		System.out.println(ret);


	}


	/***
	 *
	 * @param ljaddress 终端逻辑地址 正序传过去的 解析后需要倒叙
	 * @param jieSuanTime 结算时间 格式为年月日时分秒 180604163840
	 * @param dealCardNo 交易卡号 16位卡号 不足16位前面加0补齐
	 * @param gunNo 充电接口标识 充电桩为一桩多充时用来标记接口号，一桩一充时此项为0。多个接口时顺序对每个接口进行编号
	 * @param beginYe 启动账户余额
	 * @param usePower 结算电量
	 * @param useMoney 结算金额
	 * @param endYue 结算后账户余额
	 * @param serverMoney 结算服务费
	 * @return ret 100 连接超时 200 发送成功 300 订单不存在 400 桩通讯断开
	 *
	 */
	public String sendPre(String ljaddress,String jieSuanTime, String dealCardNo,String gunNo,
						  String beginYe,String usePower,String useMoney,
						  String endYue,String serverMoney){
		String ret = null;
		byte[] ret_begin = new byte[]{(byte) 0x68};
		//终端逻辑地址
		byte[] ljbyteaddress = ToolUtil.hexStringToBytes(ljaddress);

		//主站地址与命令序号 起始字符（68H）
		byte[] ret_add = new byte[]{(byte) 0xC1,0x00,0x68};
		//控制码0xB7
		byte[] ret_c = new byte[]{(byte) 0xB9};

		/**
		 * 启动充电数据项DATA格式 ---------------------------k开始
		 */


		/**
		 * 倒序后的结算时间
		 */
		byte[] jiesuanTime_bytes = ToolUtil.bytesReverseOrder(ToolUtil.hexStringToBytes(jieSuanTime));


		//交易卡号 16位十进制卡号

		//倒叙后的交易卡号
		byte[] dealCardNo_bytes = null;
		if(null != dealCardNo && !"".equals(dealCardNo)){
			dealCardNo_bytes = ToolUtil.bytesReverseOrder(ToolUtil.hexStringToBytes(new StringBuffer(dealCardNo).reverse().toString()));
		}else{
			dealCardNo_bytes = ToolUtil.fill0x00(8);
		}


		//交易流水号 30位十进制卡号 15  交易流水号为：时间戳(YYMMDDHHMMSS)+桩逻辑地址(6字节)+流水生成来源(1字节)+交易随机数(2字节)

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

		//d倒叙终端逻辑地址
		byte[] lj_address = ToolUtil.bytesReverseOrder(ljbyteaddress);

		//充电接口标识  长度1-255
//		byte[] bytes_gun = ToolUtil.hexStringToBytes(ToolUtil.bin2HexString(ToolUtil.int2Bin(gunNo)));
		String gun_no_hex = ToolUtil.dec2HexString(gunNo);
		if("0".equals(gunNo)){
			gun_no_hex = "00";
		}
		if(Integer.parseInt(gunNo) <= 15 && Integer.parseInt(gunNo) > 0){
			gun_no_hex = "0" + gun_no_hex;
		}
		//计算完后的充电接口标识
		byte[] bytes_gun = ToolUtil.hexStringToBytes(gun_no_hex);

		/**
		 * 结算标志
		 */
		byte[] jiesuan_flag = ToolUtil.fill0x00(1);
		/**
		 * 倒叙启动账户余额
		 */
		byte[] beginYe_bytes = ToolUtil.bytesReverseOrder(ToolUtil.hexStringToBytes(HardwareFault.diGui(ToolUtil.dec2HexString(beginYe), 8)));
		/**
		 * 倒叙结算电量
		 */
		byte[] usePower_bytes = ToolUtil.bytesReverseOrder(ToolUtil.hexStringToBytes(HardwareFault.diGui(ToolUtil.dec2HexString(usePower), 8)));
		/**
		 * 倒叙结算金额
		 */
		byte[] useMoney_bytes = ToolUtil.bytesReverseOrder(ToolUtil.hexStringToBytes(HardwareFault.diGui(ToolUtil.dec2HexString(useMoney), 8)));

		/**
		 * 倒叙结算后账户余额
		 */
		byte[] endYue_bytes = ToolUtil.bytesReverseOrder(ToolUtil.hexStringToBytes(HardwareFault.diGui(ToolUtil.dec2HexString(endYue), 8)));
		/**
		 * 倒叙结算服务费
		 */
		byte[] serverMoney_bytes = ToolUtil.bytesReverseOrder(ToolUtil.hexStringToBytes(HardwareFault.diGui(ToolUtil.dec2HexString(serverMoney), 8)));


		/**
		 * 保留数据
		 */
		byte[] keep_bytes = ToolUtil.fill0x00(2);


		//组装数据
		List<byte[]> data = new ArrayList<byte[]>();
		data.add(jiesuanTime_bytes);
		data.add(dealCardNo_bytes);
		data.add(ret_cs_pre);
		data.add(lj_address);
		data.add(bytes_gun);
		data.add(jiesuan_flag);
		data.add(beginYe_bytes);
		data.add(usePower_bytes);
		data.add(useMoney_bytes);
		data.add(endYue_bytes);
		data.add(serverMoney_bytes);
		data.add(keep_bytes);
		//处理好的完成数据数组
		byte[] data_bytes = ToolUtil.appendByte(data);
		/**
		 * 启动充电数据项DATA格式 ---------------------------结束
		 */

		//计算得出的倒叙数据长度
		byte[] ret_length = ToolUtil.intToBytes(data_bytes.length, 2, 0);
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
