package com.hc.netty.server;

import com.hc.app.model.Body0x12;
import com.hc.app.model.Body0x20;
import com.hc.app.model.Head;
import com.hc.app.model.Meg;
import com.hc.app.utils.HardwareFault;
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
 * 读当前数据
 */
public class KlReadCurrentDataAppClient {
	private static KlReadCurrentDataAppClient idnAppClient = new KlReadCurrentDataAppClient();
	private static String ip = AppConfig.getMessage("idn.pre.ip");
	private static int port = Integer.valueOf(AppConfig.getMessage("idn.pre.port"));//服务器监听端口
	
	public static KlReadCurrentDataAppClient getInstance() {
		return idnAppClient;
	}
	public static void main(String[] args){
		//222222111111
		//111111222224
		String ret= KlReadCurrentDataAppClient.getInstance().sendPre("111111222224", "01");
		System.out.println(ret);
	}


	/***
	 *
	 * @param ljaddress 终端逻辑地址 正序传过去的 解析后需要倒叙
	 * @param gunNo 0-255之间 枪序号
	 * @return
	 */
	public String sendPre(String ljaddress,String gunNo){
		String ret = null;
		byte[] ret_begin = new byte[]{(byte) 0x68};
		//终端逻辑地址
		byte[] ljbyteaddress = ToolUtil.hexStringToBytes(ljaddress);
		//d倒叙终端逻辑地址
//		byte[] lj_address = ToolUtil.bytesReverseOrder(ljbyteaddress);
		//主站地址与命令序号 起始字符（68H）
		byte[] ret_add = new byte[]{(byte) 0xC1,0x00,0x68};
		//控制码0xB8
		byte[] ret_c = new byte[]{(byte) 0xB8};

//		byte[] ret_length = new byte[]{(byte) 0x00};

		//充电枪编号  长度1-255
//		byte[] bytes_gun = ToolUtil.hexStringToBytes(ToolUtil.bin2HexString(ToolUtil.int2Bin(gunNo)));
//		String gun_no_hex = ToolUtil.dec2HexString(gunNo);
//		if(Integer.parseInt(gunNo) <= 15){
//			gun_no_hex = "0" + gun_no_hex;
//		}
//		byte[] bytes_gun = ToolUtil.hexStringToBytes(gun_no_hex);

		byte[] bytes_gun = HardwareFault.getInfoPoint(Integer.parseInt(gunNo));


		byte[] ret_length = ToolUtil.intToBytes(bytes_gun.length, 2, 0);


		List<byte[]> content_list = new ArrayList<byte[]>();
		content_list.add(ret_begin);
		content_list.add(ljbyteaddress);
		content_list.add(ret_add);
		content_list.add(ret_c);
		content_list.add(ret_length);
		content_list.add(bytes_gun);
		byte[] ret_end = new byte[]{(byte) 0x16};

		byte[] ret_cs_pres = ToolUtil.appendByte(content_list);
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
