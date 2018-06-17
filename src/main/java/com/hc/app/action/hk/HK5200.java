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

/**
 * 充电桩信息注册
 * @Title:HK5200.java
 * @Package:com.hc.app.action.hk
 * @Description:TODO
 * @author zhifanglong
 * @Date 2016年8月10日 上午9:35:04
 * @Version V1.0
 */
@Component("HK5200")
public class HK5200 implements BaseAction {
	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		RequestObject ob=(RequestObject)msg;
        Map data=ob.getData();
        
        String pileNo=(String)data.get("SN");
        
		int retCode=1;
        if(chargePileServiceImpl.countByPileNo(pileNo)==1){
			retCode=0;
			
			   
		    //跟新充电桩的状态
		        
           chargePileServiceImpl.updateStatus(pileNo,"09","0");//充电桩状态01 空闲 02 告警 03空闲 04充电中 05 完成 
				                                                //06 预约 07 等待 
           chargePileServiceImpl.updateGunStatus(pileNo,"09");	                                                //00离线 08 签单 09 交换密钥
		}
     
			 		
	    byte[] sendData=buildData(retCode);
	    byte[] ret= ParsePackage.buildHeader(5201, sendData);
		
	    chargePileServiceImpl.addHk5200(data);
	   HKLogUtils.info("=================================发送的数据=======================");
	   HKLogUtils.info(ISOUtil.hexString(ret));
				         
	   ByteBuf resp= Unpooled.copiedBuffer(ret);
	   ctx.writeAndFlush(resp);
		         
				return null;
			}
			
			private byte[] buildData(int retCode) throws UnsupportedEncodingException{
				//DATA 200 ascii
				byte[] val=new byte[1];
				val[0]=(byte) ((retCode >>>0) & 0xff);
			    return val;
			}

}
