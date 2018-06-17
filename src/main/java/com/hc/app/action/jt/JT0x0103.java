package com.hc.app.action.jt;

import com.hc.app.model.jt.Data_0103;
import com.hc.app.model.jt.MegJT;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ChargePileService;
import com.hc.common.utils.CommonUtil;
import com.hc.common.utils.hk.JTLogUtils;
import com.yao.NettyChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *<p>title :</p>
 *<p>Description : charge状态指令</p>
 *<p>Company : 广州爱电牛科技有限公司</p>
 * @date 2017年3月23日
 * @author 小吴
 */
@Component("JT0x0103")
public class JT0x0103 implements JTActionI {

	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Autowired
	private com.hc.app.service.JtService JtService;

	@Override
	public void receive_send_data(ChannelHandlerContext ctx, byte[] meg, boolean debug) {
		Data_0103 data_0103 = new Data_0103(meg);
		MegJT megJT = new MegJT(data_0103, meg);
		Map data = megJT.getMap();
		String chargepile = (String) data.get("charge_pile");
		byte[] bytes = megJT.getBytes();
		if (debug) {
			JTLogUtils.info("收到的报文>>>>>>>>>>>>>>>>>>>>>>" + megJT.bytesToHexString(meg));
			megJT.JTwriteBytesToFile(meg, 1);
			megJT.JTwriteBytesToFile(bytes, 0);
			JTLogUtils.info("发动的报文>>>>>>>>>>>>>>>>>>>>>>" + megJT.bytesToHexString(bytes));
		} else {
			JTLogUtils.info("当前的长连接>>>>>>>>>>>>>>>" + chargepile);
			SocketChannel channel = (SocketChannel) NettyChannelMap.get(chargepile);
			if (channel == null) {
				NettyChannelMap.add("chargepile", channel);
				JTLogUtils.info("保存一个新的长连接>>>>>>>>>>>>>>>>>>" + chargepile);
			} else {
				NettyChannelMap.remove(channel);
				NettyChannelMap.add(chargepile, channel);
			}
			/***************************** 业务逻辑处理 ********************************/

			int err = (int) data.get("data2_1");
			int gunNo;
			String gunCharge = null; // 插枪状态
			String gunS; // 充电枪状态
			// 判断是否已经擦枪了
			Object data5_2 = data.get("data5_2");
			Object data4_2 = data.get("data4_2");
			String chargejob = CommonUtil.toBinaryString((String) data5_2);
			String chargeStatus = CommonUtil.toBinaryString((String) data4_2);
			int chargeCount = (int) data.get("data3_1");
			// 合桩的状态的枪的最终显示状态
			String showStatus = "03";
			if (err == 1)
				showStatus = "01";// 故障
			gunS = "3";
			try {
				if (chargeCount == 1) {
					gunNo = 1;
					if ("1".equals(data4_2) && "0".equals(data5_2)) {
						gunCharge = "1";// 插抢
					}

					else if ("1".equals(data5_2) && "1".equals(data4_2)) {
						showStatus = "04"; // 充电中
						gunS = "2";
					}

					else {
						gunCharge = "0";
						gunS = "0";
					}
					chargePileServiceImpl.updateGunStatus(chargepile, gunCharge, gunS, showStatus, gunNo);
					updateGunStatus(showStatus, chargepile, gunNo);
				} else {// 双枪以上
					for (int i = 1; i <= chargeCount; i++) {
						gunNo = i;
						char c = chargeStatus.charAt(chargeStatus.length() - i);
						char b = chargejob.charAt(chargejob.length() - i);
						String gun = (c == '1' ? "1" : "0");
						String gunjob = (b == '1' ? "1" : "0");
						if ("1".equals(gun) && "0".equals(gunjob)) {
							gunCharge = "1";// 插抢
						} else if ("1".equals(gun) && "1".equals(gunjob)) {
							showStatus = "04"; // 充电中
							gunS = "2";
						} else {
							gunCharge = "0";
							gunS = "0";
						}

						chargePileServiceImpl.updateGunStatus(chargepile, gunCharge, gunS, showStatus, gunNo);
						updateGunStatus(showStatus, chargepile, gunNo);
					}

				}
				// 跟新充电桩的状态
				String pileStatus="03";
				int noUseGun = chargePileServiceImpl.countGun(chargepile, "0");
				if (!"0".equals(showStatus)) {
					pileStatus = "0";
				} else if (noUseGun == 0) {
					pileStatus = "07";// 等待，就是所有枪都被占用
				}

				chargePileServiceImpl.updateStatus(chargepile, pileStatus, "0");
			} catch (Exception e) {
				JTLogUtils.error("出现异常了>>>>>>>>>>>>>>>>" + e.getMessage());
			}
		}
		ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
		ctx.writeAndFlush(byteBuf);
		try {
			data_persistence(megJT);
		} catch (Exception e) {
			JTLogUtils.error(e.getMessage());
		}
	}

	@Override
	public void data_persistence(MegJT meg) throws Exception {
		Map map = meg.getMap();
		JtService.addState(map);
	}

	private void updateGunStatus(String stauts, String chargepile, int gunNo) throws Exception {
		if ("2".equals(stauts)) {// 充电完成状态跟新订单
			Map gunInfo = chargePileServiceImpl.findByPileNoAndGunNo(chargepile, gunNo);
			String gunCode = (String) gunInfo.get("GUN_CODE");
			Map rm = chargeOrderServiceImpl.findLatestByGunCode(gunCode);
			if (rm != null) {
				String orderId = (String) rm.get("CHARGE_ORDER_ID");
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("ORDER_STATE", "03");
				params.put("END_CHARGE_TIME", "1");
				params.put("CHARGE_ORDER_ID", orderId);
				chargeOrderServiceImpl.updateInfo(params);
			}
		}
	}

}
