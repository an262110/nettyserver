package com.hc.app.action.jt;

import com.hc.app.model.jt.Data_0101;
import com.hc.app.model.jt.MegJT;
import com.hc.app.service.JtService;
import com.hc.common.utils.hk.JTLogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("JT0x0101")
public class JT0x0101 implements JTActionI {
    @Autowired
    private JtService jtService;
	@Override
	public void receive_send_data(ChannelHandlerContext ctx, byte[] meg, boolean debug) {
		Data_0101 data_0101 = new Data_0101(meg);
		MegJT megjt = new MegJT(data_0101, meg);
		// rt 接收返回的文件名称 0：发送 1 接收的
		JTLogUtils.info("接收的报文>>>>>>>>>>>>>>>>>>>>>>>>>>" + megjt.bytesToHexString(meg));

		byte[] bytes = megjt.getBytes();
		ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
		JTLogUtils.info("0x0101登陆回复>>>>>>>>>>>>>>>>>>>>>" + megjt.bytesToHexString(bytes));
		if (debug) {
			megjt.JTwriteBytesToFile(meg, 1);
			megjt.JTwriteBytesToFile(bytes, 0);
		}
		ctx.writeAndFlush(byteBuf);
		try {
			data_persistence(megjt);
		} catch (Exception e) {
			JTLogUtils.error(e.getMessage());
		}
	}

	@Override
	public void data_persistence(MegJT meg) throws Exception {
		 Map map = meg.getMap();
		 jtService.addLogin(map);
	}

}
