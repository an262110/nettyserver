package com.hc.app.action.jt;

import com.hc.app.model.jt.Data_0205;
import com.hc.app.model.jt.MegJT;
import com.hc.app.service.ChargeHKService;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ChargePileService;
import com.hc.common.utils.LogUtils;
import com.hc.common.utils.hk.JTLogUtils;
import com.yao.NettyChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * <p>
 * title :
 * </p>
 * <p>
 * Description : 停止充电
 * </p>
 * <p>
 * Company : 广州爱电牛科技有限公司
 * </p>
 * 
 * @date 2017年3月22日
 * @author 小吴
 */
@Component("JT0x0305")
public class JT0x0305 implements JTActionI {

	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Autowired
	private ChargeHKService chargeHKServiceImpl;

	@Override
	public void receive_send_data(ChannelHandlerContext ctx, byte[] meg, boolean debug) {
		Data_0205 data_0205 = new Data_0205(meg);
		data_0205.setHead4_1(new byte[] { 0x04 });
		byte[] msg8_n = data_0205.getMsg8_n();
		MegJT megJT = new MegJT(data_0205, meg);
		megJT.setMeg7_2(megJT.intToBytes(msg8_n.length, 2, 1));
		megJT.setMeg8_n(msg8_n);
		Map data = megJT.getMap();
		String chargepile = String.valueOf(data.get("chargepile"));
		if (debug) {
			JTLogUtils.info("收到的报文>>>>>>>>>>>>>>>>>>>>>>" + megJT.bytesToHexString(meg));
			megJT.JTwriteBytesToFile(meg, 1);
			byte[] bytes = megJT.getBytes();
			megJT.JTwriteBytesToFile(bytes, 0);
			JTLogUtils.info("发动的报文>>>>>>>>>>>>>>>>>>>>>>" + megJT.bytesToHexString(bytes));
			ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
			ctx.writeAndFlush(byteBuf);
		} else {
			Map mapOrder = null;
			String retClient = null;
			try {
				mapOrder = chargeOrderServiceImpl.searchOrderDetail(chargepile, "02");
				retClient = "200\r\n";
				if (mapOrder == null) {
					retClient = "300\r\n"; // 订单不存在
					JTLogUtils.error("[300]订单不存在！");
					ByteBuf resp = Unpooled.copiedBuffer(retClient.getBytes());
					ctx.writeAndFlush(resp);
				}
			} catch (Exception e) {
				JTLogUtils.error(e.getMessage());
			}
			String gunCode = mapOrder.get("GUN_CODE").toString();

			Map gunInfo = null;
			try {
				gunInfo = chargePileServiceImpl.findByGunCode(gunCode);
			} catch (Exception e) {
				JTLogUtils.error(e.getMessage());
			}
			if (gunInfo == null) {
				retClient = "500\r\n"; // 充电枪不存在
				System.out.println("[500]充电枪不存在！");
				JTLogUtils.error("[500]充电枪不存在！");
				ByteBuf resp = Unpooled.copiedBuffer(retClient.getBytes());
				ctx.writeAndFlush(resp);
			}
			String CHARGE_USERID = mapOrder.get("CHARGE_USERID_9").toString();
			JTLogUtils.info("充电用户标识号：" + CHARGE_USERID);

			String pileNo = (String) gunInfo.get("PILE_SERI");
			String gunNo = gunInfo.get("GUN_NO").toString();
			JTLogUtils.info("桩序列号：" + pileNo);
			JTLogUtils.info("枪编号：" + gunNo);
			Channel sc = NettyChannelMap.get(pileNo);

			if (sc != null) {
				LogUtils.info("发送指令的连接通道：=====" + sc.toString());
				if (sc.isActive() || sc.isOpen()) {
					JTLogUtils.info("活动的连接！");
				} else {
					JTLogUtils.info("不活动的连接！");
				}
				// 响应数据给充电桩
				byte[] bytes = megJT.getBytes();
				ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
				sc.writeAndFlush(byteBuf).addListener(new ChannelFutureListener() {

					@Override
					public void operationComplete(ChannelFuture arg0) throws Exception {
						// TODO Auto-generated method stub
						if (arg0.isSuccess()) {
							JTLogUtils.info("info>>>>>>>>>>>>>发送成功");
						} else {
							JTLogUtils.info("info>>>>>>>>>>>>>发送失败");
						}
					}
				});
			} else {

				JTLogUtils.error("[400]没有连接桩！");
				ByteBuf resp_400 = Unpooled.copiedBuffer("400\r\n".getBytes());
				ctx.writeAndFlush(resp_400);
			}

			ByteBuf resp_200 = Unpooled.copiedBuffer("200\r\n".getBytes());
			ctx.writeAndFlush(resp_200);
			Map chargeParams = new HashMap();
			chargeParams.put("MESSAGE_ID", data.get("msgid"));
			chargeParams.put("MESSAGE_TYPE", 0x0305);
			chargeParams.put("ORDER_ID", mapOrder.get("charge_order_id"));
			chargeParams.put("FACTORY_ID", "0001");
			chargeParams.put("PILE_SERI", pileNo);
			chargeParams.put("GUN_NO", gunNo);

			try {
				chargeHKServiceImpl.save(chargeParams);
			} catch (Exception e) {
				JTLogUtils.error(e.getMessage());
			}
			// 保存指令

		}

	}

	@Override
	public void data_persistence(MegJT meg) throws Exception {
		// TODO Auto-generated method stub

	}

}
