package com.hc.app.action.hk;

import com.hc.app.action.BaseAction;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component("HK6501")
public class HK6501 implements BaseAction {

	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		RequestObject ob=(RequestObject)msg;

		return null;
	}

}
