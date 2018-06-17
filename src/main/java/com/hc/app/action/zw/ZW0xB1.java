package com.hc.app.action.zw;

import com.hc.app.model.Body0xB1;
import com.hc.app.model.BodyI;
import com.hc.app.model.Head;
import com.hc.app.model.Meg;
import com.hc.app.service.ZWService;
import com.hc.common.utils.hk.ZWLogUtils;
import com.yao.NettyChannelMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 运营商验证
 * @author liuh
 *
 */

@Component("ZW0xB1")
public class ZW0xB1 implements ZWActionI {
	@Autowired
	private ZWService zwServiceImpl;
	@Override
	public BodyI receive_or_send(ChannelHandlerContext ctx, Head head,
								 byte[] msg, boolean is_Debug) {
		Body0xB1 body = new Body0xB1(msg);
		ZWLogUtils.info("智网回复报文>>>>>>body="+body.getBody_hexstr());
		//正常保存长连接 异常重新连接
		NettyChannelMap.add("1001",(SocketChannel)ctx.channel());
		ZWLogUtils.info("建立的连接>>>>>>="+ NettyChannelMap.get("1001"));

		return body;
	}

	@Override
	public boolean business_todb(Meg meg, boolean is_Debug) {

		ZWLogUtils.info("写入数据库>>>>>>meg="+meg);
		ZWLogUtils.info("写入数据库>>>>>>meg.bytesToMap()="+meg.bytesToMap());
		if(is_Debug){
			//zwServiceImpl.addBody0xB0(meg.bytesToMap());
			return true;
		}

		return true;
	}

}
