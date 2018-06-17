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

/***
 * 后台控制充电启/停充电
 */
@Component("KL0xB7")
public class KL0xB7 implements KLActionI {
    @Override
    public byte[] createSendInfo(ChannelHandlerContext ctx, byte[] req, Map<String, Object> map) throws Exception {
        String reqStr = ISOUtil.hex2String(req);
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
        //充电命令
        String order = ISOUtil.hexString(Arrays.copyOfRange(dataContent, 4, 5));

        //获取枪编号
        String gun_no = ISOUtil.hexString(Arrays.copyOfRange(dataContent, 37, 38));


        //从map中获取的之前下发充电的流水号并把之前的数据整合到一起
        byte[] con_bytes = null;
        if("02".equals(order)){
            //从报文中获取流水号
            byte[] dealno = Arrays.copyOfRange(dataContent, 21, 36);
            ToolUtil.addValue(dealno,query_address+gun_no);
            System.out.println("开始充电交易流水号=" + Arrays.toString(dealno));
        }else{
            byte[] value1 = ToolUtil.getValue(query_address + gun_no);

            System.out.println("停止充电交易流水号=" + Arrays.toString(value1));

            List<byte[]> conlist = new ArrayList<>();
            /**
             * 添加数据中交易流水号以前的数据
             */
            conlist.add(Arrays.copyOfRange(dataContent, 0, 21));
            /**
             * 添加从map数据的交易流水号
             */
            conlist.add(value1);
            /**
             * 添加数据中交易流水号以后的数据
             */
            conlist.add(Arrays.copyOfRange(dataContent, 36, dataContent.length));
            con_bytes = ToolUtil.appendByte(conlist);
        }









        //第4部   16H	1	结束码
//        byte[] end = Arrays.copyOfRange(req, req.length - 1, req.length);
//        String end_str = ISOUtil.hexString(end);
//        System.out.println("end_str=" + end_str);


        //以上解析完


        byte[] ret_begin = new byte[]{(byte) 0x68};


        //控制码0x0A
        byte[] ret_c = new byte[]{(byte) 0x0A};   //控制码

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
        if("02".equals(order)){ //充电
            list.add(dataContent);//数据
        }else{ //停止充电
            list.add(con_bytes);//数据
        }
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
        /*if("03".equals(query_address+gun_no)){//充电结束后删除交易号
            ToolUtil.remove(query_address+gun_no);
        }*/
        return null;
    }

    public static void main(String[] args) {


        String c = "0000013812345678";
        test(c,2);


    }

    public static void test(String c,int length){

        int t = c.length()/length;
        byte [] acc = new byte[t];
        List< byte[]> relist = new ArrayList<>();
        for (int i = 0; i < t; i++) {
            String substring = c.substring(i * length, (i * length) + 2);
            relist.add(ToolUtil.hexStringToBytes(substring));
        }
        byte[] bytes = ToolUtil.appendByte(relist);
        byte[] res_bytes = ToolUtil.bytesReverseOrder(bytes);
        System.out.println(Arrays.toString(res_bytes));
        System.out.println(ToolUtil.bytesToHexString(res_bytes));

    }
}
