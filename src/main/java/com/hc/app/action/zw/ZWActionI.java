package com.hc.app.action.zw;

import com.hc.app.model.BodyI;
import com.hc.app.model.Head;
import com.hc.app.model.Meg;
import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @author liuh
 *
 * 
 */
public interface ZWActionI {
//	Map<String, String> receive_or_send(ChannelHandlerContext ctx,Head head, byte[] msg);
	BodyI receive_or_send(ChannelHandlerContext ctx, Head head, byte[] msg, boolean is_Debug);

//	boolean business_todb(Map<String, String> head, Map<String, String> body);
	boolean business_todb(Meg meg, boolean is_Debug);
	
}
