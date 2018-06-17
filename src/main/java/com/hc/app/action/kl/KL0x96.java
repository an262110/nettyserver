package com.hc.app.action.kl;

import com.hc.app.utils.ToolUtil;
import io.netty.channel.ChannelHandlerContext;
import org.jpos.iso.ISOUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

/**
 * 终端响应主站下发的结算信息
 */
@Component("KL0x96")
public class KL0x96 implements KLActionI {
    @Override
    public byte[] createSendInfo(ChannelHandlerContext ctx, byte[] req, Map<String, Object> map) throws Exception {
        String reqStr = ISOUtil.hexString(req);
        System.out.println("业务逻辑" + reqStr);


        /**
         * 第二部 终端逻辑地址
         * 获取对应的1-7的值  不需转换 只需要从头到尾调换位置
         */
        byte[] rtua= Arrays.copyOfRange(req,1,7);
        /**
         * 位置调换
         */
        byte[] address = ToolUtil.bytesReverseOrder(rtua);
        String rtua_str = ISOUtil.hexString(address);//终端逻辑地址

        /**
         * 数据长度L（12H）
         */
        byte[] len = Arrays.copyOfRange(req, 11, 13);
        byte[] reverseOrder = ToolUtil.bytesReverseOrder(len);
        int len_str = ToolUtil.BytesToint(reverseOrder);
//        System.out.println("len_str=" + len_str);

        // 获取总的数据
        byte[] dataContent_bytes = Arrays.copyOfRange(req, 13, 13+len_str);
        String dataContent = ToolUtil.bytesToHexString(dataContent_bytes);
        System.out.println("终端响应主站下发的结算信息的数据为： " + dataContent);


        //第4部   16H	1	结束码
        byte[] end = Arrays.copyOfRange(req, req.length - 1, req.length);
        String end_str = ISOUtil.hexString(end);
        System.out.println("end_str=" + end_str);
        return null;
    }
}
