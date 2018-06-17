package com.hc.app.action;

import com.hc.app.service.ChargePileService;
import com.hc.app.service.CheckInService;
import com.hc.common.utils.CommonUtil;
import com.hc.common.utils.LogUtils;
import com.hc.common.utils.RandomString;
import com.yao.NettyChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.apache.commons.lang.ArrayUtils;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 签到交易处理类
 * @Title:TX2020.java
 * @Package:com.hc.app.action
 * @Description:TODO
 * @author zhifanglong
 * @Date 2016年6月20日 下午1:21:37
 * @Version V1.0
 */
@Component("TX2020")
public class TX2020 implements BaseAction {

	@Autowired
	private CheckInService checkInServiceImpl;
	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		byte[] req=(byte[])msg;
		ISOMsg request= CommonUtil.parsePackage(req);

		ISOMsg sendMsg=new ISOMsg();
		//Integer length=134;
		//String header="0086303030303030";
		String header="303030303030";
		sendMsg.setHeader(ISOUtil.hex2byte(header));

		String pileNo=request.getString(41);
		String status="01";
		String randomStr=RandomString.getRandomString(32);
		String appAddr="http://www.shanrongkeji.com?pilecode=";

		if(chargePileServiceImpl.countByPileNo(pileNo)==1){
			status="00";
		}
		sendMsg.set("39",status);//00 成功 01无效的充电桩序列号 02无效终端类型
		//03协议版本号高于系统，拒绝签到 04 站级签到

		sendMsg.set("4",randomStr);//随机字符串
		sendMsg.set("6",appAddr);//APP下载地址
		sendMsg.set("5","192.168.2.39");//IP列表

		sendMsg.set("0","2021");//协议版本号

		ISOPackager packager = new GenericPackager(CommonUtil.getConfigPath(request.getString("0"),"OUT"));
		sendMsg.setPackager(packager);



		byte[] res=sendMsg.pack();
		byte[] both = (byte[]) ArrayUtils.addAll(ISOUtil.hex2byte(header),res);
		both= CommonUtil.addLength(both);
		sendMsg.dump(System.out, "");
		LogUtils.info("--------------------------(TX2020:签到 )响应的报文--------------------------");
		LogUtils.info(ISOUtil.hexString(both));

		ByteBuf resp= Unpooled.copiedBuffer(both);
		ctx.writeAndFlush(resp);
		//ctx.flush();

		Channel oldSocket= NettyChannelMap.get(pileNo);
		if(oldSocket!=null){//删除旧的连接
			if(oldSocket.isActive()||oldSocket.isOpen()){
				oldSocket.close();
			}
			NettyChannelMap.remove(pileNo);
		}

		NettyChannelMap.add(pileNo, (SocketChannel)ctx.channel());
		LogUtils.info("info>>>>>>>>保存了一个长连接"+ NettyChannelMap.get(pileNo).toString());
		CommonUtil.dumpInfo(sendMsg);

		//签到数据存入数据库
		Map values=new HashMap();
		values.put("ENCRYPT",request.getString(38));
		values.put("PROTOCOL_VERSION", request.getString(39));
		values.put("TERMINAL_TYPE", request.getString(40));
		values.put("TERMINAL_NO", request.getString(41));
		values.put("STATUS",status);
		values.put("RANDOM_STR",randomStr);
		values.put("APP_ADDRESS",appAddr);

		//跟新充电桩的状态
		if("00".equals(status))
			chargePileServiceImpl.updateStatus(pileNo,"08");//充电桩状态01 空闲 02 告警 03空闲 04充电中 05 完成
		//06 预约 07 等待
		//00离线 08 签单 09 交换密钥
		chargePileServiceImpl.updateGunStatus(pileNo,"08");
		checkInServiceImpl.save(values);

		return both;

	}

}
