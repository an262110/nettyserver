package com.hc.netty.server;

import org.apache.commons.lang.ArrayUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;


public class HcAppClient_hk {
	private static HcAppClient_hk idnAppClient = new HcAppClient_hk();
	private static String ip = "127.0.0.1";//AppConfig.getMessage("idn.pre.ip");
	private static int port = 8066;//Integer.valueOf(AppConfig.getMessage("idn.pre.port"));//服务器监听端口
	
	public static HcAppClient_hk getInstance() {
		return idnAppClient;
	}
	public static void main(String[] args){

		
		 //String ret=IdnAppClient_hk.getInstance().send(6100,"20160816181047000774");
		
		 String ret= HcAppClient_hk.getInstance().sendStop(9900, "IDN200000800");
		 
		 System.out.println("RETURN CODE ==="+ret);
	
	}
	
	public String send(int type,String msg){
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
	 
	 public String sendStop(int type,String msg){
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
			   int lenth=msg.length();
			   //byte[] len=toByteArray(lenth, 2);
			   byte[] head=new byte[8];
			   head[0]=(byte)0x7E;
			   head[1]=(byte)0x99;
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
}
