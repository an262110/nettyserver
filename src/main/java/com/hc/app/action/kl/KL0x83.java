package com.hc.app.action.kl;

import com.hc.app.service.KlGunOrderStatusServiceImpl;
import com.hc.app.utils.HardwareFault;
import com.hc.app.utils.TimeUtils;
import com.hc.app.utils.ToolUtil;
import com.hc.common.utils.KL.KLLogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.jpos.iso.ISOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 终端正常应答主站的读充电记录数据，或者主动上报充电记录数据
 */
@Component("KL0x83")
public class KL0x83 implements KLActionI {

    @Autowired
    private KlGunOrderStatusServiceImpl klGunOrderStatusServiceImpl;

    @Override
    public byte[] createSendInfo(ChannelHandlerContext ctx, byte[] req, Map<String, Object> map) throws Exception {
        String reqStr = ISOUtil.hexString(req);
        System.out.println("发送过来的报文为： " + reqStr);

        /**
         * 数据逻辑地址
         */
        byte[] address = Arrays.copyOfRange(req, 1, 7);
        String query_address = ToolUtil.bytesToHexString(ToolUtil.bytesReverseOrder(address));

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
        byte[] query_kinds = Arrays.copyOfRange(dataContent, 0, 1);
        String query = ISOUtil.hexString(query_kinds);
        System.out.println("查询方式为 =" + query);

        //从数据中获取记录条数
        byte[] count_bytes = Arrays.copyOfRange(dataContent, 1, 2);
        String count = ISOUtil.hexString(count_bytes);
        System.out.println("获取记录条数=" + count);

        //从数据中获取发生时间 BCD
        byte[] time_bytes = Arrays.copyOfRange(dataContent, 2, 8);
        String time = ISOUtil.hexString(ToolUtil.bytesReverseOrder(time_bytes));
        System.out.println("获取发生时间=" + time);


        //从数据中获取交易卡号
        byte[] dealCard_bytes = Arrays.copyOfRange(dataContent, 8, 16);
        String card = ISOUtil.hexString(ToolUtil.bytesReverseOrder(dealCard_bytes));
        System.out.println("交易卡号=" + card);


        //从数据中获取交易流水号
        byte[] dealls_bytes = Arrays.copyOfRange(dataContent, 16, 31);
        String dealls = ISOUtil.hexString(ToolUtil.bytesReverseOrder(dealls_bytes));
        System.out.println("交易流水号=" + dealls);



        //从数据中获取充电桩逻辑地址
        byte[] ljaddress_bytes = Arrays.copyOfRange(dataContent, 31, 37);
        String address_lj = ISOUtil.hexString(ljaddress_bytes);
        System.out.println("充电桩逻辑地址=" + address_lj);

        //从数据中获取充电接口标识
        byte[] flag_bytes = Arrays.copyOfRange(dataContent, 37, 38);
        String flag = ISOUtil.hexString(flag_bytes);
        System.out.println("获取充电接口标识=" + flag);

        //从数据中获取用户账号
        byte[] account_bytes = Arrays.copyOfRange(dataContent, 38, 46);
        String acc = ISOUtil.hexString(ToolUtil.bytesReverseOrder(account_bytes));
        System.out.println("获取用户账号=" + acc);


        //从数据中获取充电交易类型
        byte[] dealkinds_bytes = Arrays.copyOfRange(dataContent, 46, 47);
        String dekind = ISOUtil.hexString(dealkinds_bytes);
        System.out.println("充电交易类型=" + dekind);


        //从数据中获取充电启动来源
        byte[] startFrom_bytes = Arrays.copyOfRange(dataContent, 47, 48);
        String startFrom = ISOUtil.hexString(startFrom_bytes);
        System.out.println("充电启动来源=" + startFrom);

        //从数据中获取充电结束来源
        byte[] endFrom_bytes = Arrays.copyOfRange(dataContent, 48, 49);
        String endFrom = ISOUtil.hexString(endFrom_bytes);
        System.out.println("充电结束来源=" + endFrom);

        //从数据中获取充电记录上传来源
        byte[] jiluFrom_bytes = Arrays.copyOfRange(dataContent, 49, 50);
        String jiluFrom = ISOUtil.hexString(jiluFrom_bytes);
        System.out.println("充电记录上传来源=" + jiluFrom);


        //从数据中获取电动汽车唯一标识
        byte[] carFlag_bytes = Arrays.copyOfRange(dataContent, 50, 67);
        String carFlag = ISOUtil.hexString(ToolUtil.bytesReverseOrder(carFlag_bytes));
        System.out.println("电动汽车唯一标识=" + carFlag);

        //从数据中获取开始时间
        byte[] beginTime_bytes = Arrays.copyOfRange(dataContent, 67, 73);
        String beginTime = ISOUtil.hexString(ToolUtil.bytesReverseOrder(beginTime_bytes));
        System.out.println("获取开始时间=" + beginTime);

        //从数据中获取结束时间
        byte[] endTime_bytes = Arrays.copyOfRange(dataContent, 73, 79);
        String endTime = ISOUtil.hexString(ToolUtil.bytesReverseOrder(endTime_bytes));
        System.out.println("获取结束时间=" + endTime);

        /***
         * 准备循环取出n个费率的值---------------------开始
         */
        //从数据中获取费率个数
        byte[] feilvCount_bytes = Arrays.copyOfRange(dataContent, 79, 80);
        int feilvCount = ToolUtil.BytesToint(feilvCount_bytes);
        System.out.println("获取费率个数=" + feilvCount);
        /**
         * 计算出的费率字节数
         */
        int childLength = 81 + (feilvCount*19) + 8;

        byte[] feilvInfo_bytes = Arrays.copyOfRange(dataContent, 80, childLength);

        /**
         * 总起始示值
         */
        byte[] beginValue_bytes = Arrays.copyOfRange(feilvInfo_bytes, 0, 1);
        System.out.println("总起始示值=" + ToolUtil.BytesToint(beginValue_bytes));
        /**
         * 总结束示值
         */
        byte[] endValue_bytes = Arrays.copyOfRange(feilvInfo_bytes, 1, 2);
        System.out.println("总结束示值=" + ToolUtil.BytesToint(endValue_bytes));

        //获取费率的总的数组
        byte[] fl_bytes = Arrays.copyOfRange(feilvInfo_bytes, 2, feilvInfo_bytes.length);

        //计算出第一个费率单价的开始位置
        int fl = feilvCount * 8 - 2;

        for(int i = 0; i < feilvCount; i++){
            byte[] bytes = Arrays.copyOfRange(fl_bytes, i, i * 8 + 8);
            byte[] power_begin_bytes = Arrays.copyOfRange(bytes, 0, 4);
            byte[] power_end_bytes = Arrays.copyOfRange(bytes, 4, 8);
            int begin = ToolUtil.BytesToint(ToolUtil.bytesReverseOrder(power_begin_bytes));
            int end = ToolUtil.BytesToint(ToolUtil.bytesReverseOrder(power_end_bytes));
            System.out.println("费率" +(i+1) +"电量起始值=" +begin);
            System.out.println("费率" +(i+1) +"电量结束值=" +end);
            byte[] flValue = Arrays.copyOfRange(fl_bytes, fl+i*11, fl + i * 11 + 11);
            byte[] fl_price_bytes = Arrays.copyOfRange(flValue, 0, 3);
            byte[] fl_power_bytes = Arrays.copyOfRange(flValue, 3, 7);
            byte[] fl_amount_bytes = Arrays.copyOfRange(flValue, 7, 11);
            int peice = ToolUtil.BytesToint(ToolUtil.bytesReverseOrder(fl_price_bytes));
            int power = ToolUtil.BytesToint(ToolUtil.bytesReverseOrder(fl_power_bytes));
            int amount = ToolUtil.BytesToint(ToolUtil.bytesReverseOrder(fl_amount_bytes));
            System.out.println("费率" +(i+1) +"单价=" +peice);
            System.out.println("费率" +(i+1) +"电量=" +power);
            System.out.println("费率" +(i+1) +"电量金额=" +amount);
        }

        /***
         * 准备循环取出n个费率的值---------------------结束
         */

        //获取数据中的总电量
        byte[] total_power_bytes = Arrays.copyOfRange(dataContent, childLength, childLength+4);
        int total_power = ToolUtil.BytesToint(ToolUtil.bytesReverseOrder(total_power_bytes));
        System.out.println("总电量=" + total_power);

        //获取数据中的总电费
        byte[] total_powerMoney_bytes = Arrays.copyOfRange(dataContent, childLength+4, childLength+8);
        int total_powerMoney = ToolUtil.BytesToint(ToolUtil.bytesReverseOrder(total_powerMoney_bytes));
        System.out.println("总电费=" + total_powerMoney);

        //获取数据中的充电服务费金额
        byte[] server_Money_bytes = Arrays.copyOfRange(dataContent, childLength+8, childLength+12);
        int server_Money = ToolUtil.BytesToint(ToolUtil.bytesReverseOrder(server_Money_bytes));
        System.out.println("充电服务费金额=" + server_Money);

        //获取数据中的消费总金额
        byte[] total_money_bytes = Arrays.copyOfRange(dataContent, childLength+12, childLength+16);
        int total_money = ToolUtil.BytesToint(ToolUtil.bytesReverseOrder(total_money_bytes));
        System.out.println("获取数据中的消费总金额=" + total_money);

        //获取数据中的扣款前钱包余额
        byte[] before_money_bytes = Arrays.copyOfRange(dataContent, childLength+16, childLength+20);
        int before_money = ToolUtil.BytesToint(ToolUtil.bytesReverseOrder(before_money_bytes));
        System.out.println("获取数据中的扣款前钱包余额=" + before_money);

        //获取数据中的扣款后钱包余额
        byte[] after_money_bytes = Arrays.copyOfRange(dataContent, childLength+20, childLength+24);
        int after_money = ToolUtil.BytesToint(ToolUtil.bytesReverseOrder(after_money_bytes));
        System.out.println("获取数据中的扣款后钱包余额=" + after_money);
        //获取数据中的终止荷电状态SOC
        byte[] soc_bytes = Arrays.copyOfRange(dataContent, childLength+24, childLength+25);
        int soc = ToolUtil.BytesToint(soc_bytes);
        System.out.println("获取数据中的终止荷电状态SOC=" + soc);


        if("00".equals(jiluFrom)){ //开始充电
            ToolUtil.addValue(dealls_bytes,query_address + flag);
            klGunOrderStatusServiceImpl.peachUpdate(query_address, ToolUtil.BytesToint(flag_bytes)+"","02");
        }else if("01".equals(jiluFrom)){ //停止充电
            klGunOrderStatusServiceImpl.peachUpdate(query_address, ToolUtil.BytesToint(flag_bytes)+"","01");
        }



        //第4部   16H	1	结束码
        byte[] end = Arrays.copyOfRange(req, req.length - 1, req.length);
        String end_str = ISOUtil.hexString(end);
//        System.out.println("end_str=" + end_str);

        /***
         * 存放长连接信息
         */

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
        byte[] ret_c = new byte[]{(byte) 0x05};   //控制码

        /**
         * 第二部 终端逻辑地址
         * 获取对应的1-7的值  不需转换 只需要从头到尾调换位置
         */
        byte[] ljbyteaddress = Arrays.copyOfRange(req,1,7);

        //交易流水号 30位十进制卡号 15  交易流水号为：时间戳(YYMMDDHHMMSS)+桩逻辑地址(6字节)+流水生成来源(1字节)+交易随机数(2字节)

        // 时间戳
        byte[] trade_time_bytes = ToolUtil.hexStringToBytes(TimeUtils.getTimestap());

        //流水生成来源(1字节)
        byte[] ret_from = new byte[]{(byte) 0x01};
        //交易随机数(2字节)
        byte[] suiJiNo = HardwareFault.getSuiJiNo();

        List<byte[]> list = new ArrayList<byte[]>();
        list.add(trade_time_bytes);
        list.add(ljbyteaddress);
        list.add(ret_from);
        list.add(suiJiNo);
        //生成的完整的倒叙交易流水号
        byte[] ret_time_pre = ToolUtil.bytesReverseOrder(ToolUtil.appendByte(list));

        //获取下发指令中报文的数据长度
        byte[] ret_length = ToolUtil.bytesReverseOrder(ToolUtil.intToBytes(ret_time_pre.length, 2, 1));

        List<byte[]> cs_list = new ArrayList<byte[]>();
        cs_list.add(ret_h);
        cs_list.add(ret_add);
        cs_list.add(hex_orderno);
        cs_list.add(ret_begin);
        cs_list.add(ret_c);
        cs_list.add(ret_length);
        cs_list.add(ret_time_pre);
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
        KLLogUtils.info("--->>>--- send " + end_sendStr);


        //发送给客户端
        ByteBuf resp = Unpooled.copiedBuffer(end_send);
        ctx.writeAndFlush(resp);
        return null;
    }

    public static void main(String[] args) {
//        byte[] feilvCount_bytes = Arrays.copyOfRange(dataContent, 78, 79);
        byte[] ret_end = new byte[]{(byte) 0x0f};
        int feilvCount = ToolUtil.BytesToint(ret_end);
        System.out.println("获取费率个数=" + feilvCount);
    }
}
