package com.hc.app.action.kl;

import com.hc.app.utils.ToolUtil;
import io.netty.channel.ChannelHandlerContext;
import org.jpos.iso.ISOUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

/**
 * 用于充电桩响应后台控制启动充电命令失败原因
 */
@Component("KL0xCA")
public class KL0xCA implements KLActionI {
    @Override
    public byte[] createSendInfo(ChannelHandlerContext ctx, byte[] req, Map<String, Object> map) throws Exception {
        String reqStr = ISOUtil.hexString(req);
        System.out.println("发送过来的报文为： " + reqStr);

        /**
         * 数据长度L（12H）
         */
        byte[] len = Arrays.copyOfRange(req, 11, 13);
        byte[] reverseOrder = ToolUtil.bytesReverseOrder(len);
        int len_str = ToolUtil.BytesToint(reverseOrder);
//      System.out.println("len_str=" + len_str);

        // 获取总的数据
        byte[] dataContent = Arrays.copyOfRange(req, 13, 13+len_str);
        //从数据中获取充电失败原因 有：01：桩故障 02：枪故障
        byte[] fail_bytes = Arrays.copyOfRange(dataContent, 0, 1);
        String fail = ISOUtil.hexString(fail_bytes);
        System.out.println("充电失败原因=" + fail);

        //从数据中获取桩硬件遥信状态字
        byte[] yjyx_bytes = Arrays.copyOfRange(dataContent, 1, 4);
        String yjyx = ISOUtil.hexString(ToolUtil.bytesReverseOrder(yjyx_bytes));
        System.out.println("读取到的桩硬件遥信状态字=" + yjyx);

        //从数据中获取软件遥信状态字
        byte[] softyx_bytes = Arrays.copyOfRange(dataContent, 4, 6);
        String softyx = ISOUtil.hexString(ToolUtil.bytesReverseOrder(softyx_bytes));
        System.out.println("读取到的软件遥信状态字=" + softyx);

        //从数据中获取枪硬件遥信状态字
        byte[] gunyjyx_bytes = Arrays.copyOfRange(dataContent, 6, 8);
        String gunyjyx = ISOUtil.hexString(ToolUtil.bytesReverseOrder(gunyjyx_bytes));
        System.out.println("读取到的枪硬件遥信状态字=" + gunyjyx);

        //从数据中获取枪软件遥信状态字
        byte[] gunsoftyx_bytes = Arrays.copyOfRange(dataContent, 8, 10);
        String gunsoftyx = ISOUtil.hexString(ToolUtil.bytesReverseOrder(gunsoftyx_bytes));
        System.out.println("读取到的枪软件遥信状态字=" + gunsoftyx);


        //第4部   16H	1	结束码
        byte[] end = Arrays.copyOfRange(req, req.length - 1, req.length);
        String end_str = ISOUtil.hexString(end);
        System.out.println("end_str=" + end_str);
        return null;
    }
}
