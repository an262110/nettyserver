package com.hc.app.action;

import com.hc.app.service.ChargePileService;
import com.hc.app.utils.TimeUtils;
import com.hc.common.utils.CommonUtil;
import com.hc.common.utils.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang.ArrayUtils;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 工作密钥交易处理类
 * @Title:TX2030.java
 * @Package:com.hc.app.action
 * @Description:TODO
 * @author zhifanglong
 * @Date 2016年6月20日 下午3:22:17
 * @Version V1.0
 */
@Component("TX2030")
public class TX2030 implements BaseAction {

	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		byte[] req=(byte[])msg;
		ISOMsg request= CommonUtil.parsePackage(req);

		ISOMsg sendMsg=new ISOMsg();
		//Integer length=134;
		//String header="0034303030303030";
		String header="303030303030";
		sendMsg.setHeader(ISOUtil.hex2byte(header));

		sendMsg.set("39","00");
		sendMsg.set("40","3333333333333333");
		sendMsg.set("61", TimeUtils.getCurrentTime());

		sendMsg.set("0","2031");
		ISOPackager packager = new GenericPackager(CommonUtil.getConfigPath(request.getString(0),"OUT"));
		sendMsg.setPackager(packager);



		byte[] res=sendMsg.pack();
		byte[] both = (byte[]) ArrayUtils.addAll(ISOUtil.hex2byte(header),res);
		both= CommonUtil.addLength(both);

		LogUtils.info("--------------------------(TX2030:工作密钥 )响应的报文--------------------------");
		LogUtils.info(ISOUtil.hexString(both));

		ByteBuf resp= Unpooled.copiedBuffer(both);
		ctx.writeAndFlush(resp);
		CommonUtil.dumpInfo(sendMsg);

		chargePileServiceImpl.updateStatus(request.getString(41),"09");//充电桩状态01 空闲 02 告警 03空闲 04充电中 05 完成
		//06 预约 07 等待
		chargePileServiceImpl.updateGunStatus(request.getString(41),"09");                                                        //00离线 08 签单 09 交换密钥

		return both;

	}

}
