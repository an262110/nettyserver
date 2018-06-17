package com.hc.app.action.kl;

import com.hc.app.utils.ToolUtil;
import com.hc.common.utils.KL.KLLogUtils;
import com.hc.common.utils.LogUtils;
import com.yao.NettyChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.jpos.iso.ISOUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 登录退出
 */
@Component("KL0xA2")
public class KL0xA2 implements KLActionI {
    @Override
    public byte[] createSendInfo(ChannelHandlerContext ctx, byte[] req, Map<String, Object> map) throws Exception {

        String reqStr = ISOUtil.hexString(req);
        System.out.println("业务逻辑" + reqStr);

        /**
         * 第二部 终端逻辑地址
         * 获取对应的1-7的值  不需转换 只需要从头到尾调换位置
         */
        byte[] rtua=Arrays.copyOfRange(req,1,7);
        /**
         * 位置调换
         */
        byte[] reverseOrder = ToolUtil.bytesReverseOrder(rtua);
        String rtua_str= ISOUtil.hexString(reverseOrder);

        /**
         * 第六部分  数据长度L（12H）
         */
        /*byte[] len = Arrays.copyOfRange(req, 11, 13);
        byte[] reverseOrder = ToolUtil.bytesReverseOrder(len);
        int len_str = ToolUtil.BytesToint(reverseOrder);
        System.out.println("len_str=" + len_str);*/



        //第8部   16H	1	结束码
        byte[] end = Arrays.copyOfRange(req, req.length - 1, req.length);
        String end_str = ISOUtil.hexString(end);
//        System.out.println("end_str=" + end_str);


        Channel oldSocket= NettyChannelMap.get(rtua_str);
        if(oldSocket!=null){//删除旧的连接
            if(oldSocket.isActive()||oldSocket.isOpen()){
                oldSocket.close();
            }
            NettyChannelMap.remove(rtua_str);
        }

        LogUtils.info("info>>>>>>>>删除了一个长连接 "+ctx.channel().toString());

        /**
         * 从报文中获取D7 D6数据
         */
        byte[] msta = Arrays.copyOfRange(req,7,9);
//        String msta_str= ISOUtil.hexString(msta);
//        System.out.println("16进制主站地址与命令序号： = "+msta_str);
        //获取第一个字节msg1，打印字符串
        byte[] msg1 = Arrays.copyOfRange(msta,0,1);
        String msg1_str = ToolUtil.bytesToBits(msg1);
        String D7 = msg1_str.substring(0,1);//获取D7
        String D6 = msg1_str.substring(1,2);//获取D6
//        System.out.println("msg1 二进制字符串="+msg1_str);
//        System.out.println("msg1 D7 = "+D7);
//        System.out.println("msg1 D6 = "+D6);


        /**
         * 对主站地址D7 D6进行解析 当是一下情况时无需回复
         * D7  ： D6
         * 0   ：0
         * 1 ：	0
         */
        if(("0".equals(D7) && "0".equals(D6)) || ("1".equals(D7) && "0".equals(D6))){
            return null;
        }


        //以上解析完

        //以下拼装报文返回给充电桩
        //报文协议文档可以看出，返回的控制码是0x82，我们组装报文，然后发送给桩

        //前面10个字节可以援用收到的报文
        //?疑问 ：L=09H+X（所有应答数据标识与数据内容的总长度）  这个长度怎么标识
        byte[] ret_h = Arrays.copyOfRange(req, 0, 7);

        //主站地址与命令序号
        byte[] ret_add = new byte[]{(byte) 0xC1};
        //获取第二个字节msg2，打印字符串（类似msg1）
        byte[] msg2=Arrays.copyOfRange(msta,1,2);
        String msg2_str = ToolUtil.bytesToBits(msg2);
        String bitFuncCode = msg2_str.substring(3,8);//二进制功能码需要转16进制
        byte[] hex_orderno = null;
        if("00000".equals(bitFuncCode)){//当全完0 直接赋值为00
            hex_orderno =  new byte[]{(byte) 0x00};
        }else{
            String bin_orderno = "000" + bitFuncCode;
            hex_orderno = ToolUtil.hexStringToBytes(ToolUtil.bin2HexString(bin_orderno));
        }
        //帧起始符
        byte[] ret_begin = new byte[]{(byte) 0x68};
        //控制码0x82
        byte[] ret_c = new byte[]{(byte) 0x22};   //控制码
        byte[] ret_length = new byte[]{(byte) 0x00};   //数据长度
        byte[] ret_data = new byte[]{(byte) 0x00};   //数据长度
//        byte[] ret_l = ToolUtil.intToBytes(3, 2, 1);
//        byte[] ret_data = new byte[3];
//        ret_data = ToolUtil.fill0x00(3);


        List<byte[]> list = new ArrayList<byte[]>();
        list.add(ret_h);
        list.add(ret_add);
        list.add(hex_orderno);
        list.add(ret_begin);
        list.add(ret_c);
        list.add(ret_length);
        list.add(ret_data);
//        list.add(ret_l);
//        list.add(ret_data);
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



        //返回信息
        String end_sendStr = ISOUtil.hex2String(end_send);
        KLLogUtils.info("--->>>--- send " + end_sendStr);


        //发送给客户端
        ByteBuf resp = Unpooled.copiedBuffer(end_send);
        ctx.writeAndFlush(resp);

        return null;

    }
}
