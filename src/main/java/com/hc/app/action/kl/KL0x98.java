package com.hc.app.action.kl;

import com.hc.app.service.KlGunOrderStatusServiceImpl;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 充电枪信息上报
 */
@Component("KL0x98")
public class KL0x98 implements KLActionI {

    @Autowired
    private KlGunOrderStatusServiceImpl klGunOrderStatusServiceImpl;

    @Override
    public byte[] createSendInfo(ChannelHandlerContext ctx, byte[] req, Map<String, Object> map) throws Exception {

        String reqStr = ISOUtil.hexString(req);
        System.out.println("发送的报文问： " + reqStr);


        /**
         * 数据逻辑地址
         */
        byte[] address = Arrays.copyOfRange(req, 1, 7);
        String query_address = ToolUtil.bytesToHexString(ToolUtil.bytesReverseOrder(address));


        //主站地址
        byte[] zz_address = Arrays.copyOfRange(req, 7, 8);

        //命令序号
        byte[] value = HardwareFault.getValue();

        /**
         * 数据长度L（12H）
         */
        byte[] len = Arrays.copyOfRange(req, 11, 13);
        byte[] reverseOrder = ToolUtil.bytesReverseOrder(len);
        int len_str = ToolUtil.BytesToint(reverseOrder);
        System.out.println("len_str=" + len_str);

        // 获取总的数据
        byte[] dataContent = Arrays.copyOfRange(req, 13, 13+len_str);
        //从数据中获取枪号 对应数据库 hc_charge_gun 的 GUN_NO字段
        byte[] gun_no = Arrays.copyOfRange(dataContent, 0, 1);
        int gun_no_int = ToolUtil.BytesToint(gun_no);
        System.out.println("枪号 = " + gun_no_int);

        /**
         *
         * 0x00:未投运，0x01:空闲状态
         * 0x02:充电状态，0x03:故障状态----> 充电结束命令
         * 0x04:等待连枪状态，0x05:绝缘检测
         * 0x06:充电结束中，0x07:请求资源等待状态
         * 0x08:占用状态，0x09:预约状态
         * 0x0A:结算状态，0x0B:插枪未充电状态
         * 07->05->02  开始充电顺序
         * 06->0a->0b  停止充电顺序
         *
         *
         */


        //从数据中获取枪状态
        byte[] gun_status = Arrays.copyOfRange(dataContent, 1, 2);
        String gun_status_hex = ISOUtil.hexString(gun_status);
        System.out.println("返回的枪状态码=" + gun_status_hex);



        if("03".equals(gun_status_hex)){
            for(int i = 0; i < 3; i++){

                KLLogUtils.error("KL0x98:因枪状态码=" + gun_status_hex + ",所以开始进行循环关闭充电操作。 当前操作的次数为第" + i +"次！");
                //以上解析完
                byte[] ret_begin = new byte[]{(byte) 0x68};
                //控制码0x0A
                byte[] ret_c = new byte[]{(byte) 0x0A};   //控制码
                //权限等级
                byte[] ret_level = new byte[]{(byte) 0x11};
                //密码 111111
                byte[] pwd_bytes = ToolUtil.bytesReverseOrder(ToolUtil.hexStringToBytes("000000"));

                //充电停止命令
                byte[] ret_onOff = new byte[]{(byte) 0x03};

                /**
                 * 获取用户账号
                 */
                byte[] cardNo_bytes = ToolUtil.getUserValue(query_address + ToolUtil.bytesToHexString(gun_no));
                /**
                 * 获取交易流水号
                 */
                byte[] deal_novalue1 = ToolUtil.getValue(query_address);
                /**
                 * 充电类型 充电参数
                 */
                byte[] ret_kind = new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00};
                //金额
                byte[] bytes_yue = new byte[]{(byte) 0xFE,(byte) 0x61,(byte) 0x00,(byte) 0x00};

                List<byte []> conList = new ArrayList<>();
                conList.add(ret_level);
                conList.add(pwd_bytes);
                conList.add(ret_onOff);
                conList.add(cardNo_bytes);
                conList.add(cardNo_bytes);
                conList.add(deal_novalue1);
                conList.add(gun_no);
                conList.add(ret_kind);
                conList.add(bytes_yue);
                byte[] content_bytes = ToolUtil.appendByte(conList);

                //获取下发指令中报文的数据长度
                byte[] ret_length = ToolUtil.intToBytes(content_bytes.length, 2, 1);
                List<byte[]> list = new ArrayList<byte[]>();
                list.add(ret_begin); //开始
                list.add(address);//倒叙逻辑地址
                list.add(zz_address);//主站地址
                list.add(value);//命令序号
                list.add(ret_begin);//第二个开始
                list.add(ret_c);//控制码0x0A
                list.add(ret_length);//数据长度
                list.add(content_bytes);//数据

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
                KLLogUtils.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>桩充电上报调用KL0x98时返回枪状态码"+ gun_status_hex +"时下发充电关闭命令的报文<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
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
//                    ByteBuf resp_400= Unpooled.copiedBuffer("400".getBytes());
//                    ctx.writeAndFlush(resp_400);
//                    ctx.close();
                }
                //延迟2秒执行第二次
                Thread.sleep(2000);
            }
            /**
             * 更改枪的状态
             */
            klGunOrderStatusServiceImpl.update(query_address, ToolUtil.BytesToint(gun_no)+"",gun_status_hex);
        }


        /**
         * 更改枪的状态
         */
        klGunOrderStatusServiceImpl.update(query_address, ToolUtil.BytesToint(gun_no)+"",gun_status_hex);

        //第4部   16H	1	结束码
        byte[] end = Arrays.copyOfRange(req, req.length - 1, req.length);
        String end_str = ISOUtil.hexString(end);
        System.out.println("end_str=" + end_str);



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
        System.out.println("msg1 二进制字符串="+msg1_str);
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





        //以上解析完

        //以下拼装报文返回给充电桩
        //报文协议文档可以看出，返回的控制码是0x82，我们组装报文，然后发送给桩

        //前面10个字节可以援用收到的报文
        //?疑问 ：L=09H+X（所有应答数据标识与数据内容的总长度）  这个长度怎么标识
        byte[] ret_h = Arrays.copyOfRange(req, 0, 7);

        //主站地址与命令序号
        byte[] ret_add = new byte[]{(byte) 0xD5};
        //获取第二个字节msg2，打印字符串（类似msg1）
        /**
         * 命令序号处理开始
         */
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
        /**
         * 命令序号处理结束
         */
        //帧起始符
        byte[] ret_begin = new byte[]{(byte) 0x68};
        //控制码0x82
        byte[] ret_c = new byte[]{(byte) 0x18};   //控制码
//        byte[] ret_length = new byte[]{(byte) 0x00};   //数据长度
//        byte[] ret_l = ToolUtil.intToBytes(3, 2, 1);
//        byte[] ret_data = new byte[3];
//        ret_data = ToolUtil.fill0x00(3);

        byte[] ret_length = ToolUtil.intToBytes(1, 2, 1);
        byte[] ret_data = new byte[]{(byte) 0x00};

        List<byte[]> list = new ArrayList<byte[]>();
        list.add(ret_h);
        list.add(ret_add);
        list.add(hex_orderno);
        list.add(ret_begin);
        list.add(ret_c);
        list.add(ret_length);
//        list.add(ret_l);
        list.add(ret_data);
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
