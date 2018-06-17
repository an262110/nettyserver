package com.hc.netty.server;

import com.hc.app.model.Body0x12;
import com.hc.app.model.Body0x20;
import com.hc.app.model.Head;
import com.hc.app.model.Meg;
import com.hc.common.config.AppConfig;
import org.apache.commons.lang.ArrayUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;


public class HcAppClient {
	private static HcAppClient idnAppClient = new HcAppClient();
	private static String ip = AppConfig.getMessage("idn.pre.ip");
	private static int port = Integer.valueOf(AppConfig.getMessage("idn.pre.port"));//服务器监听端口
	
	public static HcAppClient getInstance() {
		return idnAppClient;
	}
	public static void main(String[] args){
		String ret= HcAppClient.getInstance().sendPre("20160810110905000674", "30000001", "1");
		System.out.println(ret);
	}
	
	/*
	 * charge_order_id 订单编号
	 * gun_code        二维码编号
	 * charge_type     充电指令 1 充电 2 停止充电
	 * 
	 * ret 100 连接超时 200 发送成功 300 订单不存在 400 桩通讯断开
	 */
	public String sendPre(String charge_order_id,String gun_code,String charge_type){
		String ret = null;
		String factory_code = gun_code.substring(0,2);
		//10 易事特  20 合康 30 智网
		if("10".equals(factory_code)){
			String head = "0000003010";
			if("1".equals(charge_type)){
				head += "01";
			}else if("2".equals(charge_type)){
				head += "02";
			}
			ret = send(head+charge_order_id);
		}else if ("20".equals(factory_code)){
			int cmd_code = 6100 ;
			if("2".equals(charge_type)){
				cmd_code = 6200;
			}
			ret = sendHK(cmd_code,charge_order_id);
		}else if("30".equals(factory_code)){
			sendZW(charge_order_id,gun_code,charge_type);
		}
		
		return ret;
	}
	

	public static String send(String msg){
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
		   int lenth=msg.length()+2;
		   byte[] len=toByteArray(lenth, 2);
		   byte[] sendMsg=(byte[]) ArrayUtils.addAll(len,msg.getBytes("ascii"));
		  
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
