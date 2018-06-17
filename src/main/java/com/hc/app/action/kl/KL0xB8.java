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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/***
 *  后台主动发报文用来读当前数据(命令0x01)
 */
@Component("KL0xB8")
public class KL0xB8 implements KLActionI {

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
                KLLogUtils.info(Thread.currentThread().getName() + "调用的线程池=");
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
                byte[] len = Arrays.copyOfRange(req, 11, 13);
                int len_str = ToolUtil.BytesToint(len);







                //以上解析完

                //以下拼装报文返回给充电桩
                //报文协议文档可以看出，返回的控制码是0x82，我们组装报文，然后发送给桩

                //前面10个字节可以援用收到的报文
                //?疑问 ：L=09H+X（所有应答数据标识与数据内容的总长度）  这个长度怎么标识


                byte[] ret_begin = new byte[]{(byte) 0x68};


                //控制码0x01
                byte[] ret_c = new byte[]{(byte) 0x01};   //控制码

                /**
                 * 数据长度需要处理
                 */

                /**
                 * 信息点DA需要处理

                 */
                // 获取总的数据 信息点
                byte[] dataContent = Arrays.copyOfRange(req, 13, 13+len_str);

                //数据项
                byte[] ret_data1 = new byte[]{(byte) 0x60,(byte)0xB8}; //已充电量
                byte[] ret_data2 = new byte[]{(byte) 0x61,(byte)0xB8};//累计充电费用
                byte[] ret_data3 = new byte[]{(byte) 0x62,(byte)0xB8};//累计充电时间
                byte[] ret_data4 = new byte[]{(byte) 0x63,(byte)0xB8};//充电服务费金额
                byte[] ret_data5 = new byte[]{(byte) 0x64,(byte)0xB8};//消费总金额
                byte[] ret_data6 = new byte[]{(byte) 0x20,(byte)0xB9};//充电枪输出电压
                byte[] ret_data7 = new byte[]{(byte) 0x21,(byte)0xB9};//充电枪输出电流, 数据分辨率：0.1 A/位，-400 A偏移量；
                byte[] ret_data8 = new byte[]{(byte) 0x10,(byte)0xB9};//充电枪状态：00：未投运，01:空闲状态, 02:充电状态,03:故障状态, 04:等待枪连接状态(鉴权成功后等待连枪充电)，05: 绝缘检测状态06:充电结束状态07：轮冲等待，08：本地正在操作充电，09：枪预约状态，0A:等待充电结束，FF:其他

                List<byte[]> data_list = new ArrayList<byte[]>();
                data_list.add(dataContent);
                data_list.add(ret_data1);
                data_list.add(ret_data2);
                data_list.add(ret_data3);
                data_list.add(ret_data4);
                data_list.add(ret_data5);
                data_list.add(ret_data6);
                data_list.add(ret_data7);
                data_list.add(ret_data8);
                byte[] ret_data_list = ToolUtil.appendByte(data_list);



                /***
                 * 68
                 * 26 22 22 11 11 11
                 * FD 01
                 * 68
                 * 01
                 * 16 00
                 * 02 01
                 * 9A B9 9B B9 96 B9 95 B9 9D B9 9E B9 20 B9 21 B9 B1 B9 05 B8
                 * 50 16
                 */

                /***
                 * 信息点DA
                 */

                //获取下发指令中报文的数据长度
                byte[] ret_length = ToolUtil.bytesReverseOrder(ToolUtil.intToBytes(ret_data_list.length, 2, 0));

                List<byte[]> list = new ArrayList<byte[]>();
                list.add(ret_begin); //开始
                list.add(dx_address);//倒叙逻辑地址
                list.add(zz_address);//主站地址
                list.add(value);//命令序号
                list.add(ret_begin);//第二个开始
                list.add(ret_c);//控制码0x01
                list.add(ret_length);//数据长度
                list.add(ret_data_list);//数据内容
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
//                    return null;
                }

                ByteBuf resp_200= Unpooled.copiedBuffer("200".getBytes());
                ctx.writeAndFlush(resp_200);
                ctx.close();
//                return null;

            }
        });

        return null;

    }

    public static void main(String[] args) {


        System.out.println(ToolUtil.dec2HexString("00001250"));


    }

}
