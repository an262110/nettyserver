package com.hc.app.action;

import io.netty.channel.ChannelHandlerContext;

/**
 * Action基类， 用于反射对象
 * 
 * @author Zed
 *
 */
public interface BaseAction {
	byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception;
	
}
