package com.hc.app.action.jt;

import com.hc.app.model.jt.Data_0102;
import com.hc.app.model.jt.MegJT;
import com.hc.app.service.ChargePileService;
import com.hc.app.service.JtService;
import com.hc.common.utils.hk.JTLogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("JT0x0102")
public class JT0x0102 implements JTActionI {
	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Autowired
	private JtService jtService;

	@Override
	public void receive_send_data(ChannelHandlerContext ctx, byte[] meg, boolean debug) {
		Data_0102 data_0102 = new Data_0102(meg);
		MegJT megJT = new MegJT(data_0102, meg);
		byte[] bytes = megJT.getBytes();
		if (debug) {
			JTLogUtils.info("收到报文:" + megJT.bytesToHexString(meg));
			megJT.JTwriteBytesToFile(meg, 0);
			megJT.JTwriteBytesToFile(bytes, 1);
			JTLogUtils.info("发送报文"+megJT.bytesToHexString(bytes));
		} else {
			Map<String, String> map = megJT.getMap();
			String charge_pile = map.get("charge_pile");
			try {
				if (chargePileServiceImpl.countByPileNo(charge_pile) == 1) {
					chargePileServiceImpl.updateStatus(charge_pile, "08", "0");
					chargePileServiceImpl.updateGunStatus(charge_pile, "08");
				}
			} catch (Exception e) {
				JTLogUtils.error("注册出现异常>>>>>>>>>>>>>" + e.getMessage());
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
		jtService.addRegister(map);
	}

}
