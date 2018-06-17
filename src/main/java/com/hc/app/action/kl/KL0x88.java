package com.hc.app.action.kl;

import com.hc.app.utils.ToolUtil;
import io.netty.channel.ChannelHandlerContext;
import org.jpos.iso.ISOUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

/**
 * 写对象参数(命令0x08) ----> 终端/前置机应答帧
 * 下发二维码后的终端应答
 */
@Component("KL0x88")
public class KL0x88 implements KLActionI {
    @Override
    public byte[] createSendInfo(ChannelHandlerContext ctx, byte[] req, Map<String, Object> map) throws Exception {

        String reqStr = ISOUtil.hex2String(req);
        System.out.println("发送过来的报文为： " + reqStr);

        /**
         * 数据长度L（12H）
         */
        byte[] len = Arrays.copyOfRange(req, 11, 13);
        byte[] reverseOrder = ToolUtil.bytesReverseOrder(len);
        int len_str = ToolUtil.BytesToint(reverseOrder);
//        System.out.println("len_str=" + len_str);

        // 获取总的数据
        byte[] dataContent = Arrays.copyOfRange(req, 13, 13+len_str);

        String hexString = ISOUtil.hexString(dataContent);

        System.out.println("获取到的总的数据信息为="+hexString);



        return null;
    }
}
