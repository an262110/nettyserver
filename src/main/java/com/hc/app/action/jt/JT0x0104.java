package com.hc.app.action.jt;

import com.hc.app.model.jt.Data_0104;
import com.hc.app.model.jt.MegJT;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ChargePileService;
import com.hc.app.service.JtService;
import com.hc.common.utils.hk.JTLogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * <p>
 * title :报文的业务处理
 * </p>
 * <p>
 * Description :事件上传指令
 * </p>
 * <p>
 * Company : 广州爱电牛科技有限公司
 * </p>
 * 
 * @date 2017年3月21日
 * @author 小吴
 */
@Component("JTOx0104")
public class JT0x0104 implements JTActionI {
	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Autowired
	private JtService jtService;

	@Override
	public void receive_send_data(ChannelHandlerContext ctx, byte[] meg, boolean debug) {
		Data_0104 data_0104 = new Data_0104(meg);
		MegJT megJT = new MegJT(data_0104, meg);
		byte[] bs = megJT.getBytes();
		Map data = megJT.getMap();
		String chargepile = String.valueOf(data.get("charge_pile"));
		if (debug) {
			JTLogUtils.info("收到的报文>>>>>>>>>>>>>>>>>>>>>>" + megJT.bytesToHexString(meg));
			megJT.JTwriteBytesToFile(meg, 1);
			JTLogUtils.info("发动的报文>>>>>>>>>>>>>>>>>>>>>>" + megJT.bytesToHexString(bs));
			megJT.JTwriteBytesToFile(bs, 0);
		} else {
			Integer gunNO = Integer.valueOf(data.get("data4_1")+"");
			String err = String.valueOf(data.get("data5_2"));
			try {
				chargePileServiceImpl.updateGunStatus(chargepile, "1", "3", "0", gunNO);
				Map gunInfo = chargePileServiceImpl.findByPileNoAndGunNo(chargepile, gunNO);
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
			} catch (Exception e) {
				JTLogUtils.error(e.getMessage());
			}
		}
		ByteBuf byteBuf = Unpooled.copiedBuffer(bs);
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
		jtService.addState(map);

	}

}
