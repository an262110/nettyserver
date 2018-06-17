package com.hc.app.action.kl;

import com.hc.app.utils.HardwareFault;
import com.hc.app.utils.ToolUtil;
import com.hc.common.utils.KL.KLLogUtils;
import com.yao.NettyChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.jpos.iso.ISOUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 下发结算信息(命令0x16)
 */
@Component("KL0xB9")
public class KL0xB9 implements KLActionI {
    @Override
    public byte[] createSendInfo(ChannelHandlerContext ctx, byte[] req, Map<String, Object> map) throws Exception {
        String reqStr = ISOUtil.hexString(req);
        System.out.println("发送过来的报文为： " + reqStr);

        /**
         * 数据逻辑地址
         */
        byte[] address = Arrays.copyOfRange(req, 1, 7);
        byte[] dx_address = ToolUtil.bytesReverseOrder(address);
        String query_address = ToolUtil.bytesToHexString(address);
//        System.out.println("len_str=" + query_address);
        //主站地址
        byte[] zz_address = Arrays.copyOfRange(req, 7, 8);

        //命令序号
        byte[] value = HardwareFault.getValue();
        /**
         * 数据长度L（12H）
         */
        byte[] len = ToolUtil.bytesReverseOrder(Arrays.copyOfRange(req, 11, 13));
        int len_str = ToolUtil.BytesToint(len);
        System.out.println("数据长度=" + len_str);
        // 获取总的数据 直接用
        byte[] dataContent = Arrays.copyOfRange(req, 13, 13+len_str);

        //第4部   16H	1	结束码
//        byte[] end = Arrays.copyOfRange(req, req.length - 1, req.length);
//        String end_str = ISOUtil.hexString(end);
//        System.out.println("end_str=" + end_str);


        //以上解析完

        //以下拼装报文返回给充电桩
        //报文协议文档可以看出，返回的控制码是0x82，我们组装报文，然后发送给桩

        //前面10个字节可以援用收到的报文
        //?疑问 ：L=09H+X（所有应答数据标识与数据内容的总长度）  这个长度怎么标识


        byte[] ret_begin = new byte[]{(byte) 0x68};


        //控制码0x0A
        byte[] ret_c = new byte[]{(byte) 0x16};   //控制码

        //获取下发指令中报文的数据长度
        byte[] ret_length = ToolUtil.intToBytes(dataContent.length, 2, 1);

        List<byte[]> list = new ArrayList<byte[]>();
        list.add(ret_begin); //开始
        list.add(dx_address);//倒叙逻辑地址
        list.add(zz_address);//主站地址
        list.add(value);//命令序号
        list.add(ret_begin);//第二个开始
        list.add(ret_c);//控制码0x0A
        list.add(ret_length);//数据长度
        list.add(dataContent);//数据
        //校验码
        //从第一个帧起始符开始到校验码之前的所有各字节的和模256的余。即各字节二进制算术和，不计超过256的溢出值。
        //首先合并校验位前的数组
        byte[] ret_cs_pre = ToolUtil.appendByte(list);
        byte[] ret_cs = new byte[]{(byte) ToolUtil.getCS(ret_cs_pre)};
        byte[] ret_end = new byte[]{(byte) 0x16};   //结束码
        //瓶装结束，发给客户端

        List<byte[]> ret_list = new ArrayList<byte[]>();
        ret_list.add(ret_cs_pre);
        ret_list.add(ret_cs);
        ret_list.add(ret_end);

        //最后拼装的要发送的字节
        byte[] end_send = ToolUtil.appendByte(ret_list);


        ByteBuf resp= Unpooled.copiedBuffer(end_send);

        KLLogUtils.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>app调用接口后后台发给桩的数据<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        KLLogUtils.info(ISOUtil.hex2String(end_send));



        Channel sc= NettyChannelMap.get(query_address);
        KLLogUtils.info("发送指令的连接通道：====="+sc.toString());


        if(sc!=null){
            if(sc.isActive()||sc.isOpen()){
                KLLogUtils.info("活动的连接！");
            }else{
                KLLogUtils.info("不活动的连接！");
            }
            sc.writeAndFlush(resp).addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture arg0) throws Exception {
                    // TODO Auto-generated method stub
                    if(arg0.isSuccess()){

                        KLLogUtils.info("info>>>>>>>>>>>>>发送成功");

                    }else{

                        KLLogUtils.info("info>>>>>>>>>>>>>发送失败");
                        KLLogUtils.info("error is:"+arg0.cause());
                    }
                }
            });
        }else {

            KLLogUtils.error("[400]没有连接桩！");
            ByteBuf resp_400= Unpooled.copiedBuffer("400".getBytes());
            ctx.writeAndFlush(resp_400);
            ctx.close();
            return null;
        }

        ByteBuf resp_200= Unpooled.copiedBuffer("200".getBytes());
        ctx.writeAndFlush(resp_200);
        ctx.close();
        return null;
    }
}
