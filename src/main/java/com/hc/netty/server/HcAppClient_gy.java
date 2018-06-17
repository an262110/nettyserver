package com.hc.netty.server;

import org.apache.commons.lang.ArrayUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;


public class HcAppClient_gy {
	private static HcAppClient_gy idnAppClient = new HcAppClient_gy();
	private static String ip = "192.168.2.100";//AppConfig.getMessage("idn.pre.ip");
	private static int port = 8099;//Integer.valueOf(AppConfig.getMessage("idn.pre.port"));//服务器监听端口
	
	public static HcAppClient_gy getInstance() {
		return idnAppClient;
	}
	public static void main(String[] args){
		//充电接口说明
		//1c000000 电牛报文头，1c 16进制长度 后面默认6个0
		//3010         充电接口
		//01             操作
		//00000000000000000001   订单流水号
		//ret  100 失败  200 成功  22 000000 3010 01 00000000000000000001
		//String ret=IdnAppClient.getInstance().send("DN000000301001000000000001");
		
		// 正确数据格式： 000000 3010 01 20160627161647000000   
		for (int i=0;i<1000000;i++){
			
			String ret= HcAppClient_gy.getInstance().send("00000030100120160706155605000000");
			System.out.println("client--recieve>>>>>>>"+ret);
		}
//		if(ret!=null&&ret.endsWith("200")){
//			System.out.println("成功>>>>>>>"+ret);
//		}else {
//			System.out.println("失败>>>>>>>"+ret);
//		}
//		System.out.println("client--recieve>>>>>>>"+ret);	
	}
	
	public String send(String msg){
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
}
