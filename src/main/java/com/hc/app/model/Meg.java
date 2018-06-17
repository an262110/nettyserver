package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.util.Map;

/**
 * 消息体head + body
 * @author liuh
 * 优化1.尽量设计最小外部访问 final 
 *     2.尽量设计不改变类本身，使其线程安全
 *     3.消息类是个工厂，可以产生不同的消息，但是都是同一对象（已优化）
 */
public final class Meg extends MegUtil implements BodyI
{
   private  Head head;
   private  BodyI body;
   
   private static Meg meg = null;
   
   //初始化产生
   static{
	   meg = new Meg();
   }
   
   //不可外部实例化
   private Meg(){
	   
   }
  
//   public Meg(Head head, BodyI body) {
//		this.head = head;
//		this.body = body;
//	}
   
   public static Meg message(Head head, BodyI body){
	   meg.setHead(head);
	   meg.setBody(body);
	   return meg;
   }

	//合并报文头和信息体
   public byte[] getByte() {
	   //设置head 的 body的长度
	   head.setHead2_2(intToBytes(body.getBodyLen(), 2, 1));
	   return mergeByte(head.getByte(),body.getByte());
   }
   
   //发送前合并byte[] 封装
   public ByteBuf getSendBuf(){
//	   byte[] ret = this.getByte();
//	   //写日志
//    	writeBytesToFile(ret,0);             	
//    	ByteBuf resp= Unpooled.copiedBuffer(ret);
//    	return resp;
	   return obtainSendBuf(getByte());
   }
	
	@Override
	public int getBodyLen() {
	
		return getByte().length;
	}

	@Override
	public Map<String, String> bytesToMap() {
		Map<String,String> headMap = head.bytesToMap();
		Map<String,String> bodyMap = body.bytesToMap();
		headMap.putAll(bodyMap);
		return headMap;
	}

	public Head getHead() {
		return head;
	}

	public void setHead(Head head) {
		this.head = head;
	}

	public BodyI getBody() {
		return body;
	}

	public void setBody(BodyI body) {
		this.body = body;
	}
	
	public String getHexString(){
		return bytesToHexString(head.getByte())+" 报文体："+bytesToHexString(body.getByte());
	}
}
