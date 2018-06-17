package com.hc.app.action.hk;

import com.hc.app.action.BaseAction;
import com.hc.app.service.ChargePileService;
import com.hc.common.utils.hk.HKLogUtils;
import com.hc.common.utils.hk.ParsePackage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.jpos.iso.ISOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Component("HK20")
public class HK20 implements BaseAction {
	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		        
             RequestObject ob=(RequestObject)msg;
             Map data=ob.getData();
	       
	         byte[] sendData=buildData();
	         byte[] ret= ParsePackage.buildHeader(0,sendData.length , 21, buildData());
	         String pileNo=(String)data.get("SN");
	         chargePileServiceImpl.addHk20(data);
	         
	         if(chargePileServiceImpl.countByPileNo(pileNo)==1){
	        //跟新充电桩的状态
				
			 chargePileServiceImpl.updateStatus(pileNo,"08","0");//充电桩状态01 空闲 02 告警 03空闲 04充电中 05 完成 
				                                                //06 预约 07 等待 
				                                                //00离线 08 签单 09 交换密钥
			 chargePileServiceImpl.updateGunStatus(pileNo,"08");
	         }
	         HKLogUtils.info("=================================发送的数据=======================");
	         HKLogUtils.info(ISOUtil.hexString(ret));
	         
	         ByteBuf resp= Unpooled.copiedBuffer(ret);
	 		 ctx.writeAndFlush(resp);
         
		return null;
	}
	
	private byte[] buildData() throws UnsupportedEncodingException{
		//DATA 200 ascii
		byte[] val="20000000".getBytes("ascii");
	    return val;
	}

	
	
	

}
