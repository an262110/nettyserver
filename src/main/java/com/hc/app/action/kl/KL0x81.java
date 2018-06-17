package com.hc.app.action.kl;

import com.hc.app.utils.ToolUtil;
import io.netty.channel.ChannelHandlerContext;
import org.jpos.iso.ISOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

/***
 * 终端/前置机正常应答帧  ---> 主站请求帧 请求读终端数据或者前置机的参数
 */
@Component("KL0x81")
public class KL0x81 implements KLActionI {

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    private byte[] req;

    private ChannelHandlerContext ctx;

    @Override
    public byte[] createSendInfo(ChannelHandlerContext ctx1, byte[] req1, Map<String, Object> map) throws Exception {

        req = req1;
        ctx = ctx1;
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
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
                byte[] dataContent = Arrays.copyOfRange(req, 13, 13 + len_str);
                //从数据中获取信息点DA
                byte[] info_point = Arrays.copyOfRange(dataContent, 0, 2);
                String infopoint = ISOUtil.hexString(info_point);
                System.out.println("信息点DA=" + infopoint);

                //从数据中获取总的数据
                byte[] data_all = Arrays.copyOfRange(dataContent, 2, dataContent.length);
                String alldata = ISOUtil.hexString(data_all);
                System.out.println("总的数据=" + alldata);


                //第4部   16H	1	结束码
                byte[] end = Arrays.copyOfRange(req, req.length - 1, req.length);
                String end_str = ISOUtil.hexString(end);


            }
        });
        return null;
    }
}
