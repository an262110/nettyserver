package com.hc.app.action.jt;

import com.hc.app.model.jt.Data_A108;
import com.hc.app.model.jt.MegJT;
import com.hc.common.utils.hk.JTLogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("JT0x0108")
public class JT0x0108 implements JTActionI {

	@Override
	public void receive_send_data(ChannelHandlerContext ctx, byte[] meg, boolean debug) {
		Data_A108 data_A108 = new Data_A108();
		MegJT megJT = new MegJT(data_A108, meg);
		byte[] meg8_n = data_A108.getMeg8_n();
		megJT.setMeg8_n(meg8_n);
		Map data = megJT.getMap();
		String chargepile = (String) data.get("charge_pile");
		byte[] bytes = megJT.getBytes();
		byte[] toBytes = megJT.intToBytes(meg8_n.length, 2, 1);
		megJT.setMeg7_2(toBytes);
		if (debug) {
			JTLogUtils.info("收到的报文>>>>>>>>>>>>>>>>>>>>>>" + megJT.bytesToHexString(meg));
			megJT.JTwriteBytesToFile(meg, 1);
			JTLogUtils.info("发动的报文>>>>>>>>>>>>>>>>>>>>>>" + megJT.bytesToHexString(bytes));
			ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
			megJT.JTwriteBytesToFile(toBytes, 0);
		}else{}
		ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
		ctx.writeAndFlush(byteBuf);       
	}

	@Override
	public void data_persistence(MegJT meg) throws Exception {
		// TODO Auto-generated method stub

	}

}
