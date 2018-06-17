package com.hc.app.action;

import com.hc.app.utils.MAC;
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
import org.springframework.stereotype.Component;

@Component("TX1030")
public class TX1030 implements BaseAction {

	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		byte[] req=(byte[])msg;
		ISOMsg request=CommonUtil.parsePackage(req);

		ISOMsg sendMsg=new ISOMsg();
		//String header="0082303030303030";//130
		String header="303030303030";
		sendMsg.setHeader(ISOUtil.hex2byte(header));

		sendMsg.set("39","00");
		sendMsg.set("23",request.getString("11"));
		sendMsg.set("40","1");
		sendMsg.set("61","0000000000");
		sendMsg.set("63",request.getString("63"));
		sendMsg.set("0","1031");
		ISOPackager packager = new GenericPackager(CommonUtil.getConfigPath(request.getString(0),"OUT"));
		sendMsg.setPackager(packager);



		byte[] res=sendMsg.pack();
		byte[] both = (byte[]) ArrayUtils.addAll(ISOUtil.hex2byte(header),res);
		byte[] mb= new byte[both.length-16];
		System.arraycopy(both,0,mb,0, both.length-16);
		String mac= new MAC().encrypt(mb);
		sendMsg.set("63",mac);
		res=sendMsg.pack();
		both = (byte[]) ArrayUtils.addAll(ISOUtil.hex2byte(header),res);
		both=CommonUtil.addLength(both);

		sendMsg.dump(System.out, "");
		LogUtils.info("--------------------------(TX1030:黑名单 )响应的报文--------------------------");
		LogUtils.info(ISOUtil.hexString(both));

		ByteBuf resp= Unpooled.copiedBuffer(both);
		ctx.writeAndFlush(resp);

		CommonUtil.dumpInfo(sendMsg);

		return both;
	}

}
