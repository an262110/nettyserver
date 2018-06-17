package com.hc.app.action.kl;

import com.hc.app.utils.ToolUtil;
import com.hc.common.utils.KL.KLLogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.jpos.iso.ISOUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component("KL0x94")
public class KL0x94 implements KLActionI {
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
//        System.out.println("len_str=" + len_str);

        // 获取总的数据
        byte[] dataContent = Arrays.copyOfRange(req, 13, 13+len_str);
        //从数据中获取查询方式
//        byte[] query_kinds = Arrays.copyOfRange(dataContent, 0, 1);
//        String query = ISOUtil.hexString(query_kinds);
//        System.out.println("查询方式为 =" + query);

        //从数据中获取记录条数
        byte[] count_bytes = Arrays.copyOfRange(dataContent, 0, 2);
        String count = ISOUtil.hexString(count_bytes);
        System.out.println("获取记录条数=" + count);

        //从数据中获取结算时间 BCD
        byte[] time_bytes = Arrays.copyOfRange(dataContent, 2, 8);
        String time = ISOUtil.hexString(ToolUtil.bytesReverseOrder(time_bytes));
        System.out.println("获取发生时间=" + time);

        //从数据中获取交易卡号
        byte[] dealCard_bytes = Arrays.copyOfRange(dataContent, 8, 23);
        String card = ISOUtil.hexString(ToolUtil.bytesReverseOrder(dealCard_bytes));
        System.out.println("交易卡号=" + card);

        //从数据中获取充电桩逻辑地址
        byte[] dealls_bytes = Arrays.copyOfRange(dataContent, 23, 29);
        String deal_ls = ISOUtil.hexString(ToolUtil.bytesReverseOrder(dealls_bytes));
        System.out.println("获取充电桩逻辑地址=" + deal_ls);

        //从数据中获取充电接口标识
        byte[] flag_bytes = Arrays.copyOfRange(dataContent, 29, 30);
        String flag_inter = ISOUtil.hexString(flag_bytes);
        System.out.println("充电接口标识=" + flag_inter);

        //从数据中获取结算标志
        byte[] cual_bytes = Arrays.copyOfRange(dataContent, 30, 31);
        String flag = ISOUtil.hexString(cual_bytes);
        System.out.println("结算标志=" + flag);

        //从数据中获取启动账户余额
        byte[] yue_bytes = Arrays.copyOfRange(dataContent, 31, 35);
        String yue = ISOUtil.hexString(ToolUtil.bytesReverseOrder(yue_bytes));
        System.out.println("启动账户余额=" + yue);


        //从数据中获取结算电量
        byte[] power_bytes = Arrays.copyOfRange(dataContent, 35, 39);
        String power = ISOUtil.hexString(power_bytes);
        System.out.println("结算电量=" + power);


        //从数据中获取结算金额
        byte[] money_bytes = Arrays.copyOfRange(dataContent, 39, 43);
        String money = ISOUtil.hexString(money_bytes);
        System.out.println("结算金额=" + money);

        //从数据中获取结算后账户余额
        byte[] aftermoney_bytes = Arrays.copyOfRange(dataContent, 43, 47);
        String aftermoney = ISOUtil.hexString(aftermoney_bytes);
        System.out.println("结算后账户余额=" + aftermoney);

        //从数据中获取结算服务费
        byte[] servermoney_bytes = Arrays.copyOfRange(dataContent, 47, 51);
        String servermoney = ISOUtil.hexString(servermoney_bytes);
        System.out.println("结算服务费=" + servermoney);



        //第4部   16H	1	结束码
        byte[] end = Arrays.copyOfRange(req, req.length - 1, req.length);
        String end_str = ISOUtil.hexString(end);
//        System.out.println("end_str=" + end_str);


        /**
         * 第二部 终端逻辑地址
         * 获取对应的1-7的值  不需转换 只需要从头到尾调换位置
         */
        byte[] rtua=Arrays.copyOfRange(req,1,7);


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
        System.out.println("msg1 D7 = "+D7);
        System.out.println("msg1 D6 = "+D6);


        /**
         * 对主站地址D7 D6进行解析 当是一下情况时无需回复
         * D7  ： D6
         * 0   ：0
         * 1 ：	0
         */
        if(("0".equals(D7) && "0".equals(D6)) || ("1".equals(D7) && "0".equals(D6))){
            return null;
        }


        //获取0到5的二进制字符串,把6和7 设置为0，反向生成 byt[] 为主站地址，可以看报文理解到，是个int





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
        byte[] ret_begin = new byte[]{(byte) 0x68};   //控制码

        //控制码0x82
        byte[] ret_c = new byte[]{(byte) 0x15};   //控制码

        List<byte[]> content_list = new ArrayList<>();
        content_list.add(count_bytes);
        content_list.add(dealCard_bytes);
        //完整的数据信息
        byte[] all_content_bytes = ToolUtil.appendByte(content_list);


        //获取下发指令中报文的数据长度
        byte[] ret_length = ToolUtil.bytesReverseOrder(ToolUtil.intToBytes(all_content_bytes.length, 2, 1));

        List<byte[]> cs_list = new ArrayList<byte[]>();
        cs_list.add(ret_h);
        cs_list.add(ret_add);
        cs_list.add(hex_orderno);
        cs_list.add(ret_begin);
        cs_list.add(ret_c);
        cs_list.add(ret_length);
        cs_list.add(all_content_bytes);
        //校验码
        //从第一个帧起始符开始到校验码之前的所有各字节的和模256的余。即各字节二进制算术和，不计超过256的溢出值。
        //首先合并校验位前的数组
        byte[] ret_cs_pre = ToolUtil.appendByte(cs_list);
        byte[] ret_cs = new byte[]{(byte) ToolUtil.getCS(ret_cs_pre)};
        byte[] ret_end = new byte[]{(byte) 0x16};   //结束码
        //瓶装结束，发给客户端

        List<byte[]> ret_list = new ArrayList<byte[]>();
        ret_list.add(ret_cs_pre);
        ret_list.add(ret_cs);
        ret_list.add(ret_end);

        //最后拼装的要发送的字节
        byte[] end_send = ToolUtil.appendByte(ret_list);

        String end_sendStr = ISOUtil.hex2String(end_send);
        KLLogUtils.info("--->>>--- 返回给充电桩的数据 send " + end_sendStr);

        //发送给客户端
        ByteBuf resp = Unpooled.copiedBuffer(end_send);
        ctx.writeAndFlush(resp);
        return null;
    }
}
