package com.hc.app.action.jt;

import com.hc.app.model.jt.Data_0109;
import com.hc.app.model.jt.MegJT;
import com.hc.common.utils.hk.JTLogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 *<p>title :</p>
 *<p>Description :Charge费率上报 </p>
 *<p>Company : 广州爱电牛科技有限公司</p>
 * @date 2017年3月22日
 * @author 小吴
 */
@Component("JT0x0109")
public class JT0x0109 implements JTActionI {

	@Override
	public void receive_send_data(ChannelHandlerContext ctx, byte[] meg, boolean debug) {
		Data_0109 data_0109 = new Data_0109(meg);
		MegJT megJT = new MegJT(data_0109, meg);
		Map data = megJT.getMap();
		String chargepile = (String) data.get("charge_pile");
		byte[] bytes = megJT.getBytes();
		if (debug) {
			JTLogUtils.info("收到的报文>>>>>>>>>>>>>>>>>>>>>>" + megJT.bytesToHexString(meg));
			megJT.JTwriteBytesToFile(meg, 1);
			JTLogUtils.info("发动的报文>>>>>>>>>>>>>>>>>>>>>>" + megJT.bytesToHexString(bytes));
			ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
			megJT.JTwriteBytesToFile(bytes, 0);
		}else{}
		ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
		ctx.writeAndFlush(byteBuf);
	}


	@Override
	public void data_persistence(MegJT meg) throws Exception {
		// TODO Auto-generated method stub

	}

}
