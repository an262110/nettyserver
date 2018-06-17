package com.hc.app.action.hk;

import com.hc.app.action.BaseAction;
import com.hc.app.service.ChargeHKService;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ChargePileService;
import com.hc.common.utils.CommonUtil;
import com.hc.common.utils.LogUtils;
import com.hc.common.utils.hk.HKLogUtils;
import com.hc.common.utils.hk.ParsePackage;
import com.yao.NettyChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.jpos.iso.ISOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 充电启动指令
 * @Title:HK6100.java
 * @Package:com.hc.app.action.hk
 * @Description:TODO
 * @author zhifanglong
 * @Date 2016年7月25日 下午12:31:31
 * @Version V1.0
 */

@Component("HK6100")
public class HK6100 implements BaseAction {
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Autowired
	private ChargeHKService chargeHKServiceImpl;
	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		RequestObject ob=(RequestObject)msg;

		HKLogUtils.info("操作类型>>>>>>充电启动;交易码==6100");
		HKLogUtils.info("SOURCE>>>>>>source=appServer");


		String business_no=ob.getOrderId();
		HKLogUtils.info("business_no>>>>>>="+business_no);

		/********************构建命令数据**************************/
		Map mapOrder = chargeOrderServiceImpl.searchOrderDetail(business_no,"01");

		String retClient = "200\r\n";
		if(mapOrder == null){
			retClient = "300\r\n"; //订单不存在
			System.out.println("[300]订单不存在！");
			HKLogUtils.error("[300]订单不存在！");
			ByteBuf resp= Unpooled.copiedBuffer(retClient.getBytes());
			ctx.writeAndFlush(resp);
			//ctx.close();

			return retClient.getBytes();
		}

		//辅助电压
		int gunV=1;//1:12V 2:24V
		String gunFlag=(mapOrder.get("ASSIST_VOLTAGE")==null?"1":mapOrder.get("ASSIST_VOLTAGE").toString());
		if("2".equals(gunFlag)) gunV=2;

		//充电桩序列号与枪号
		String  gunCode= mapOrder.get("GUN_CODE").toString();

		Map gunInfo=chargePileServiceImpl.findByGunCode(gunCode);

		if(gunInfo==null){
			retClient = "500\r\n"; //充电枪不存在
			System.out.println("[500]充电枪不存在！");
			HKLogUtils.error("[500]充电枪不存在！");
			ByteBuf resp= Unpooled.copiedBuffer(retClient.getBytes());
			ctx.writeAndFlush(resp);
			//ctx.close();

			return retClient.getBytes();
		}


		//获取用户标识号(启动停止需要唯一)
		String charge_num_str = chargeOrderServiceImpl.obtainUserSeq("dn_charge_order_seril");

		//int charge_num = Integer.valueOf(charge_num_str).intValue();

		String CHARGE_USERID = CommonUtil.buildString(
				charge_num_str,8
		);
		HKLogUtils.info("开始充电用户标识号："+CHARGE_USERID);

		String pileNo=(String)gunInfo.get("PILE_SERI");
		String gunNo=gunInfo.get("GUN_NO").toString();
		HKLogUtils.info("桩序列号："+pileNo);
		HKLogUtils.info("枪编号："+gunNo);
		HKLogUtils.info("辅助电压："+gunV);

		byte[] data=buildData(CHARGE_USERID,pileNo,Integer.valueOf(gunNo),gunV);

		//1.添加标准头
		byte[] head_data= ParsePackage.buildHeader(6100,data);

		//2.添加下发控制头

		byte[] sendData= ParsePackage.buildControlHeader(head_data);
		ByteBuf resp= Unpooled.copiedBuffer(sendData);
		HKLogUtils.info("=================================发送的数据=======================");
		HKLogUtils.info(ISOUtil.hexString(sendData));

		Channel sc= NettyChannelMap.get(pileNo);

		if(sc!=null){
			LogUtils.info("发送指令的连接通道：====="+sc.toString());
			if(sc.isActive()||sc.isOpen()){
				HKLogUtils.info("活动的连接！");
			}else{
				HKLogUtils.info("不活动的连接！");
			}
			sc.writeAndFlush(resp).addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(ChannelFuture arg0) throws Exception {
					// TODO Auto-generated method stub
					if(arg0.isSuccess()){
						HKLogUtils.info("info>>>>>>>>>>>>>发送成功");

					}else{
						HKLogUtils.info("info>>>>>>>>>>>>>发送失败");
					}
				}
			});
		}else {

			HKLogUtils.error("[400]没有连接桩！");
			ByteBuf resp_400= Unpooled.copiedBuffer("400\r\n".getBytes());
			ctx.writeAndFlush(resp_400);
			//ctx.close();
			return null;
		}

		ByteBuf resp_200= Unpooled.copiedBuffer("200\r\n".getBytes());
		ctx.writeAndFlush(resp_200);
		//ctx.close();

		//更新订单表的用户标识号
		Map paramMap = new HashMap();

		paramMap.put("CHARGE_USERID_9", CHARGE_USERID);
		paramMap.put("CHARGE_ORDER_ID", business_no);
		paramMap.put("POLICY_ID",gunInfo.get("POLICY_ID").toString());
		chargeOrderServiceImpl.updateInfo(paramMap);
		HKLogUtils.info("已更新用户标识到订单表 以及插入充电表"+business_no +"标识号："+CHARGE_USERID);

		//保存指令数据
		int messageId= ParsePackage.count;
		Map chargeParams=new HashMap();
		chargeParams.put("MESSAGE_ID",messageId);
		chargeParams.put("MESSAGE_TYPE",6100);
		chargeParams.put("ORDER_ID",business_no);
		chargeParams.put("FACTORY_ID","0001");
		chargeParams.put("PILE_SERI",pileNo);
		chargeParams.put("GUN_NO",gunNo);

		chargeHKServiceImpl.save(chargeParams);
		return null;
	}


	private byte[] buildData(String orderSeril,String pileNoStr,int gunNo,int gunV) throws UnsupportedEncodingException{
		byte[] data = new byte[63];

		byte[] orderNo=orderSeril.getBytes("ascii");
		System.arraycopy(orderNo,0,data,0,8);

		byte[] factoryNo="0001".getBytes("ascii");
		System.arraycopy(factoryNo,0,data,8,4);

		//byte[] pileNo="SXTJ_AC10010".getBytes("ascii");
		byte[] pileNo=pileNoStr.getBytes("ascii");
		System.arraycopy(pileNo,0,data,12,12);

		//充电枪编号
		data[24] =(byte)((gunNo >>> 0) & 0xff);

		byte[] userId= CommonUtil.buildString("88888888888",20).getBytes("ascii");
		System.arraycopy(userId,0,data,25,20);

		byte[] phoneNo="88888888888".getBytes("ascii");
		System.arraycopy(phoneNo,0,data,45,11);

		data[56] = (byte)((0 >>> 8) & 0xff);//不限时长
		data[57] = (byte)((0 >>> 0) & 0xff);

		data[58] = (byte)((0 >>> 8) & 0xff);//不限电量
		data[59] = (byte)((0 >>> 0) & 0xff);//不限电量


		data[60] = (byte)((0 >>> 8) & 0xff);//不限金额
		data[61] = (byte)((0 >>> 0) & 0xff);//不限金额

		//RMS辅助电压
		data[62] = (byte)((gunV >>> 0) & 0xff);


		return data;

	}
}

