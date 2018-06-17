package com.hc.app.action;

import com.hc.app.service.ChargeOrderService;
import com.hc.app.utils.MAC;
import com.hc.common.utils.CommonUtil;
import com.hc.common.utils.LogUtils;
import com.yao.NettyChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang.ArrayUtils;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("TX3010")
public class TX3010 implements BaseAction {
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;

	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		byte[] req=(byte[])msg;
		byte[] opArray=new byte[2];
		System.arraycopy(req,12,opArray,0,2);
		String op=new String(opArray,"ascii");
		LogUtils.info("操作类型>>>>>>op="+op);
		LogUtils.info("SOURCE>>>>>>source=appServer");

		byte[] codeArray=new byte[20];
		System.arraycopy(req,14,codeArray,0,20);
		String business_no=new String(codeArray,"ascii");
		LogUtils.info("business_no>>>>>>="+business_no);

		//通过business_no 交易流水号，查询桩号以及，账户余额 写入dn_charge充电表
		String pileNo = "";
		String orderStatus="01";
		if("01".equals(op)){
			LogUtils.info("recieve op==01");
		}
		if("02".equals(op)){
			orderStatus="02";
		}

		Map mapOrder = chargeOrderServiceImpl.searchOrderDetail(business_no,orderStatus);

		String retClient = "200";
		if(mapOrder == null){
			retClient = "300"; //订单不存在
			System.out.println("[300]订单不存在！");
			LogUtils.error("[300]订单不存在！");
			ByteBuf resp= Unpooled.copiedBuffer(retClient.getBytes());
			ctx.writeAndFlush(resp);
			ctx.close();
			return retClient.getBytes();
		}

		//充电桩序列号
		String CHARGE_PILE_SERI = mapOrder.get("CHARGE_PILE_SERI").toString();
		//充电桩金额（订单金额）
		String PAY_MONEY = mapOrder.get("PAY_MONEY").toString();

		//获取用户标识号(启动停止需要唯一)
		String CHARGE_USERID = "";
		if("01".equals(op)){
			CHARGE_USERID = chargeOrderServiceImpl.obtainUserSeq("dn_charge_userid");
			LogUtils.info("开始充电用户标识号："+CHARGE_USERID);
		}else {
			CHARGE_USERID = mapOrder.get("CHARGE_USERID_9").toString();
			LogUtils.info("停止充电用户标识号："+CHARGE_USERID);
		}



		pileNo = mapOrder.get("CHARGE_PILE_SERI").toString();



		ISOMsg sendMsg=new ISOMsg();
		//String header="0073303030303030";
		String header="303030303030";
		sendMsg.setHeader(ISOUtil.hex2byte(header));

		sendMsg.set("4",op);
		sendMsg.set("5","01");
		sendMsg.set("6","003");
		sendMsg.set("7", CommonUtil.buildString(PAY_MONEY, 8));
		sendMsg.set("8","00000000");
		//用户标识号 从序列号中获取
		sendMsg.set("9",CHARGE_USERID);
		//订单金额=接口账户余额（本次最多充值金额）
		sendMsg.set("10", CommonUtil.buildString(PAY_MONEY, 8));
		//订单号=接口流水号
		sendMsg.set("11",business_no);
		//充电桩序列号
		sendMsg.set("41",CHARGE_PILE_SERI);
		sendMsg.set("63","00000");
		sendMsg.set("0","3010");

		ISOPackager packager = new GenericPackager(CommonUtil.getConfigPath("3010","OUT"));
		sendMsg.setPackager(packager);

		byte[] res=sendMsg.pack();
		byte[] both = (byte[]) ArrayUtils.addAll(ISOUtil.hex2byte(header),res);
		byte[] mb= new byte[both.length-16];
		System.arraycopy(both,0,mb,0, both.length-16);
		String mac= new MAC().encrypt(mb);
		sendMsg.set("63",mac);
		res=sendMsg.pack();
		both = (byte[]) ArrayUtils.addAll(ISOUtil.hex2byte(header),res);
		both= CommonUtil.addLength(both);

		sendMsg.dump(System.out, "");
		LogUtils.info("--------------------------(TX3010:启停充电 )发送的报文--------------------------");
		LogUtils.info(ISOUtil.hexString(both));

		CommonUtil.dumpInfo(sendMsg);

		ByteBuf pileResponse= Unpooled.copiedBuffer(both);
		Channel sc=NettyChannelMap.get(pileNo);

		if(sc!=null){
			LogUtils.info("发送指令的连接通道：====="+sc.toString());
			if(sc.isActive()||sc.isOpen()){
				LogUtils.info("活动的连接！");
			}else{
				LogUtils.info("不活动的连接！");
			}
			sc.writeAndFlush(pileResponse).addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(ChannelFuture arg0) throws Exception {
					// TODO Auto-generated method stub
					if(arg0.isSuccess()){
						LogUtils.info("info>>>>>>>>>>>>>发送成功");

					}else{
						LogUtils.info("info>>>>>>>>>>>>>发送失败");
					}
				}
			});
		}else {
			retClient = "400"; //没有连接桩
			System.out.println("[400]没有连接桩！");
			LogUtils.error("[400]没有连接桩！");
			ByteBuf resp_400= Unpooled.copiedBuffer(retClient.getBytes());
			ctx.writeAndFlush(resp_400);
			ctx.close();
			return retClient.getBytes();
		}

		//返回给客户端
		ByteBuf resp= Unpooled.copiedBuffer(retClient.getBytes());
		ctx.writeAndFlush(resp);
		ctx.close();

		//插入充电表
		chargeOrderServiceImpl.addDnCharge(sendMsg);
		if("01".equals(op)){
			//更新订单表的用户标识号
			Map paramMap = new HashMap();

			paramMap.put("CHARGE_USERID_9", CHARGE_USERID);
			paramMap.put("CHARGE_ORDER_ID", business_no);
			chargeOrderServiceImpl.updateInfo(paramMap);
			LogUtils.info("已更新用户标识到订单表 以及插入充电表"+business_no +"标识号："+CHARGE_USERID);
		}else {

			LogUtils.info("更新充电表 ");
		}

		chargeOrderServiceImpl.updateDnChargeType(CHARGE_USERID,op);

		return both;
	}


}
