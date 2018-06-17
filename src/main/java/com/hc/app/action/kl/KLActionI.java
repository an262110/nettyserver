package com.hc.app.action.kl;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

/**
 * 
 * @author liuh
 *
 * 
 */
public interface KLActionI {
	byte[] createSendInfo(ChannelHandlerContext ctx, byte[] req, Map<String, Object> map) throws Exception;

	
}
