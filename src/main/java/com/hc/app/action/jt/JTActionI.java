package com.hc.app.action.jt;

import com.hc.app.model.jt.MegJT;
import io.netty.channel.ChannelHandlerContext;

public interface JTActionI {
     public void receive_send_data(ChannelHandlerContext ctx, byte[] meg, boolean debug);
     
     public void data_persistence(MegJT meg)throws Exception;
}
