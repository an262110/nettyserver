package com.hc.app.action.hk;

import com.hc.app.action.BaseAction;
import com.hc.common.utils.hk.HKLogUtils;
import com.hc.common.utils.hk.ParsePackage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.jpos.iso.ISOUtil;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 充电启动消息
 * @Title:HK5300.java
 * @Package:com.hc.app.action.hk
 * @Description:TODO
 * @author zhifanglong
 * @Date 2016年7月25日 下午4:29:39
 * @Version V1.0
 */
@Component("HK5300")
public class HK5300 implements BaseAction {

	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		 RequestObject ob=(RequestObject)msg;
         Map data=ob.getData();
         int gunNo=(Integer)data.get("GUN_NO");//枪号
         
	     byte[] sendData=buildData(gunNo);
	     byte[] ret= ParsePackage.buildHeader(5301, sendData);
	     
	     HKLogUtils.info("=================================发送的数据=======================");
	     HKLogUtils.info(ISOUtil.hexString(ret));
	     
	     ByteBuf resp= Unpooled.copiedBuffer(ret);
		 ctx.writeAndFlush(resp);
		         
				return null;
			}
			
			private byte[] buildData(int gunNo) throws UnsupportedEncodingException{
				//DATA 200 ascii
				byte[] val=new byte[2];
				val[0]=(byte) ((0 >>>0) & 0xff);
				val[1]=(byte) ((gunNo >>>0) & 0xff);
				
			    return val;
			}

}

