package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.util.Map;

public interface BodyI 
{
   /**
    * 获取对象的byte数组
    * @return
    */
   public byte[] getByte();
   
   /**
    * 获取body的数组的长度
    * @return
    */
   public int getBodyLen();
   /**
    * 获取body的map对象
    * @return
    */
   public Map<String, String> bytesToMap();
   /**
    * 发送前封装 （netty）
    * @return
    */
   public ByteBuf getSendBuf();
}
