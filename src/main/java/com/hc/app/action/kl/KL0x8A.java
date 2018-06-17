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
 * 充电桩回答后台控制系统充电命令结果
 */
@Component("KL0x8A")
public class KL0x8A implements KLActionI {
    @Override
    public byte[] createSendInfo(ChannelHandlerContext ctx, byte[] req, Map<String, Object> map) throws Exception {
        String reqStr = ISOUtil.hexString(req);
        System.out.println("发送过来的报文为： " + reqStr);


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
//        System.out.println("len_str=" + len_str);

        // 获取总的数据
        byte[] dataContent = Arrays.copyOfRange(req, 13, 13+len_str);
        //从数据中获取充电命令
        byte[] chanegr_order = Arrays.copyOfRange(dataContent, 0, 1);
        String order = ISOUtil.hexString(chanegr_order);
        System.out.println("下发的充电命令=" + order);

        //从数据中获取卡号/帐号
        byte[] cardNo_bytes = Arrays.copyOfRange(dataContent, 1, 9);
        String cardNo = ISOUtil.hexString(ToolUtil.bytesReverseOrder(cardNo_bytes));
        System.out.println("读取到的卡号账号=" + cardNo);

        //从数据中获取充电枪序号
        byte[] gunNo_bytes = Arrays.copyOfRange(dataContent, 9, 10);
        String gunNo = ISOUtil.hexString(gunNo_bytes);
        System.out.println("读取到的充电枪序号=" + gunNo);

        //从数据中获取命令结果
        byte[] result_bytes = Arrays.copyOfRange(dataContent, 10, 11);
        String result = ISOUtil.hexString(result_bytes);
        System.out.println("读取到的命令结果=" + result);

        //从数据中获取充电失败原因的数据
        byte[] reason_bytes = Arrays.copyOfRange(dataContent, 11, 12);
        String reason = ISOUtil.hexString(reason_bytes);
        System.out.println("读取到的充电失败原因的数据=" + reason);

        //判断返回的命令结果是否是正常   00代表成功 01代表失败
        if(!"00".equals(result) && "FF".equals(reason.toUpperCase())){

            //循环三次进行关闭操作
            for(int i =0; i < 1; i++){
                KLLogUtils.error("KL0x8A 因为命令结果=" + result + ", 失败原因=" + reason + ",所以开始进行循环关闭充电操作。 当前操作的次数为第" + i +"次！");
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
                conList.add(gunNo_bytes);
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
                KLLogUtils.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>KL0x8A 充电命令下发后返回命令结果为"+ result +"时下发充电关闭命令的报文<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                KLLogUtils.info("下发下去的报文"+ ISOUtil.hex2String(end_send));
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
                Thread.sleep(4000);
            }

        }

        /**
         * 存放用户的账户
         */
        ToolUtil.addUserValue(cardNo_bytes,query_address + gunNo);

        //第4部   16H	1	结束码
        byte[] end = Arrays.copyOfRange(req, req.length - 1, req.length);
        String end_str = ISOUtil.hexString(end);
        System.out.println("end_str=" + end_str);
        return null;
    }
}
