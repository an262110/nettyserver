package com.hc.app.action.kl;

import com.hc.app.utils.HardwareFault;
import com.hc.app.utils.ToolUtil;
import com.hc.common.utils.KL.KLLogUtils;
import com.yao.NettyChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.apache.log4j.Logger;
import org.jpos.iso.ISOUtil;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 登录
 */
@Component("KL0xA1")
public class KL0xA1 implements KLActionI {

    private  static Logger logger = Logger.getLogger("IDNKL_CONNECT");
    @Override
    public byte[] createSendInfo(ChannelHandlerContext ctx, byte[] req, Map<String, Object> map) throws Exception {




//		ByteBuf buf =(ByteBuf)msg;        
//        byte[] req=new byte[buf.readableBytes()];
//        buf.readBytes(req);
        /**
         *
         * 68
         * 222222111111
         * C100
         * 68
         * A1
         * 1100
         * 111111
         * 12
         * 05
         * 1E
         * 09
         * 2D
         * 06
         * 0000000000000000
         * 80
         * 16
         *
         *
         *
         *
         */
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
        //从数据中获取密码
        byte[] pwd = Arrays.copyOfRange(dataContent, 0, 3);
        String hexPwd = ISOUtil.hexString(pwd);
//        System.out.println("hexPwd=" + hexPwd);

        //从数据中获取年份
        byte[] year = Arrays.copyOfRange(dataContent, 3, 4);
        String decYear = ToolUtil.hexString2Dec(ISOUtil.hexString(year));
//        System.out.println("decYear=" + decYear);

        //从数据中获取月份
        byte[] month = Arrays.copyOfRange(dataContent, 4, 5);
        String decMonth = ToolUtil.hexString2Dec(ISOUtil.hexString(month));
//        System.out.println("decMonth=" + decMonth);

        //从数据中获取日
        byte[] day = Arrays.copyOfRange(dataContent, 5, 6);
        String decDay = ToolUtil.hexString2Dec(ISOUtil.hexString(day));
//        System.out.println("decDay=" + decDay);

        //从数据中获取时
        byte[] hour = Arrays.copyOfRange(dataContent, 6, 7);
        String decHour = ToolUtil.hexString2Dec(ISOUtil.hexString(hour));
//        System.out.println("decHour=" + decHour);

        //从数据中获取分
        byte[] min = Arrays.copyOfRange(dataContent, 7, 8);
        String decMin = ToolUtil.hexString2Dec(ISOUtil.hexString(min));
//        System.out.println("decHour=" + decMin);

        //从数据中获取秒
        byte[] sec = Arrays.copyOfRange(dataContent, 8, 9);
        String decSec = ToolUtil.hexString2Dec(ISOUtil.hexString(sec));
//        System.out.println("decHour=" + decSec);

        //从数据中获取 4字节硬件遥信+4字节软件遥信
        byte[] yx = Arrays.copyOfRange(dataContent, 9, dataContent.length);
        /**
         * 位置调换
         */
        byte[] yx_order = ToolUtil.bytesReverseOrder(yx);
        String hex_yx = ISOUtil.hexString(yx_order);
//        System.out.println("16进制遥信数据 ：" + hex_yx);

        String errMsg = "";
        if(!"0000000000000000".equals(hex_yx)){
            byte[] y_fault = Arrays.copyOfRange(yx_order, 0, 4);//硬件故障
            byte[] r_fault = Arrays.copyOfRange(yx_order, 4, yx.length);//软件故障
            String y = HardwareFault.analyzer(y_fault, "y");
            String r = HardwareFault.analyzer(r_fault, "r");
            if(null != y && !"".equals(y)){
                errMsg += y;
            }
            if(null != r && !"".equals(r)){
                errMsg += r;
            }


        }





        //数据域的长度1
//        int len_data1 = ToolUtil.byteToInt(len[0]);
//        System.out.println("len_data1=" + len_data1);
        //数据域的长度2
//        int len_data2 = ToolUtil.byteToInt(len[1]);
//        System.out.println("len_data2=" + len_data2);


        //第2部   DATA	变长	数据域
//        byte[] data = Arrays.copyOfRange(req, 14, 32);
//        String data_str = ISOUtil.hexString(data);
//        System.out.println("data_str=" + data_str);

        //任务号（JN）  ?协议没说是什么类型，int，or str or char
        /*
          密码（PW）	3字节BCD码（使用低级权限密码）
        年	建议带时标，方便后台发现时钟异常就对时
        月
        日
        时
        分
        秒
        桩状态字	4字节硬件遥信+4字节软件遥信
         */
//        byte[] PW = Arrays.copyOfRange(data, 0, 3);
//        System.out.println("PW=" + ToolUtil.BCDtointStr(PW));


//        byte[] year = Arrays.copyOfRange(data, 3, 13);
//        System.out.println("year=" + year);
//        String y_str = ToolUtil.BCDtointStr(year);
//        System.out.println("y_str=" + y_str);
//
//        byte[] statusWord = Arrays.copyOfRange(data, 13, 17);
//        System.out.println("statusWord=" + statusWord);
//        String statusWord_str = ToolUtil.BCDtointStr(statusWord);
//        System.out.println("statusWord_str=" + statusWord_str);


        //第3部   CS	1	校验码
        //说明，正常情况下接受报文是要去校验的，可以今后再去校验客户端的数据
        //，但是如果是服务器发给桩，那必须要生成校验码，给客户端做校验
        //我们可以今后再做校验，现在打印16进制字符串，供客户端和服务器报文比对
       /* byte[] cs=Arrays.copyOfRange(req,req.length-2,req.length-1);
        String cs_str= ISOUtil.hexString(cs);
        System.out.println("cs_str="+cs_str);*/

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
         * 位置调换
         */
        byte[] address = ToolUtil.bytesReverseOrder(rtua);
        String rtua_str = ISOUtil.hexString(address);//终端逻辑地址

        Channel oldSocket= NettyChannelMap.get(rtua_str);
        if(oldSocket!=null){//删除旧的连接
            if(oldSocket.isActive()||oldSocket.isOpen()){
                oldSocket.close();
            }
            NettyChannelMap.remove(rtua_str);
        }

        NettyChannelMap.add(rtua_str, (SocketChannel)ctx.channel());
//        LogUtils.info("info>>>>>>>>保存了一个长连接"+NettyChannelMap.get(rtua_str).toString());
//        KLLogUtils.info("info>>>>>>>>保存了一个长连接"+NettyChannelMap.get(rtua_str).toString());
        logger.info("info>>>>>>>>保存了一个长连接"+NettyChannelMap.get(rtua_str).toString());
        logger.info("rtua_str:"+rtua_str+";CONNECT:"+ctx.channel());

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
        byte[] ret_c = new byte[]{(byte) 0x21};   //控制码
        byte[] ret_length = new byte[]{(byte) 0x00};   //数据长度
        byte[] ret_data = new byte[]{(byte) 0x00};   //数据



//        byte[] ret_l = ToolUtil.intToBytes(3, 2, 1);
//        byte[] ret_datasad = new byte[3];
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

        String end_sendStr = ISOUtil.hex2String(end_send);
        KLLogUtils.info("--->>>--- send " + end_sendStr);


        //发送给客户端
        ByteBuf resp = Unpooled.copiedBuffer(end_send);
        ctx.writeAndFlush(resp);
		
        return null;
    }

    private byte[] buildData() throws UnsupportedEncodingException {
        //DATA 200 ascii
        byte[] val = "20000000".getBytes("ascii");
        return val;
    }


    public static void main(String[] args) {
        String c = "0000000000000000";
        char[] chars = c.toCharArray();
        System.out.println(c.toCharArray().toString());

    }


}
