package com.hc.app.action.kl;

import com.hc.app.action.BaseAction;
import com.hc.app.utils.ToolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.jpos.iso.ISOUtil;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component("KL0x02")
public class KL0x02 implements BaseAction {
        //	@Autowired
//	private ChargePileService chargePileServiceImpl;
        @Override
        public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {

                ByteBuf buf =(ByteBuf)msg;
                byte[] req=new byte[buf.readableBytes()];
                buf.readBytes(req);

                //数据长度L（08H）
                byte[] len=Arrays.copyOfRange(req,11,13);
                String len_str= ISOUtil.hexString(len);
                System.out.println("len_str="+len_str);
                //数据域的长度
                int len_data = ToolUtil.byteToInt(len[0]);
                System.out.println("len_data="+len_data);


                //第2部   DATA	变长	数据域
                byte[] data=Arrays.copyOfRange(req,11,8);
                String data_str= ISOUtil.hexString(data);
                System.out.println("data_str="+data_str);

                //任务号（JN）  ?协议没说是什么类型，int，or str or char
        /*
       数据起始时间（Year）	1字节BCD码
数据起始时间（Mon）	1字节BCD码
数据起始时间（Day）	1字节BCD码
数据起始时间（Hour）	1字节BCD码
数据起始时间（Min）	1字节BCD码
历史数据点数（Num）	1字节HEX码
数据间隔倍率（FeqN）	上传数据间隔为采样频率的倍率，1字节HEX码
         */
                byte[] JN = Arrays.copyOfRange(data,0,1);
                System.out.println("data_JN="+JN);

                //奇怪，一个字节bcd码怎么存储 2018  如果是bcd的话应该是两个字节 0x20 0x18
                //难道就只是存0x18吗？
                byte[] year = Arrays.copyOfRange(data,1,1);
                System.out.println("year="+year);
                String y_str = ToolUtil.BCDtointStr(year);
                System.out.println("y_str="+y_str);

                byte[] mon = Arrays.copyOfRange(data,2,1);
                System.out.println("mon="+mon);
                String m_str = ToolUtil.BCDtointStr(mon);
                System.out.println("m_str="+m_str);


                byte[] day = Arrays.copyOfRange(data,3,1);
                System.out.println("day="+day);
                String d_str = ToolUtil.BCDtointStr(day);
                System.out.println("d_str="+d_str);

                byte[] Hour = Arrays.copyOfRange(data,4,1);
                System.out.println("Hour="+Hour);
                String h_str = ToolUtil.BCDtointStr(Hour);
                System.out.println("h_str="+h_str);

                byte[] Min = Arrays.copyOfRange(data,5,1);
                System.out.println("Min="+Min);
                String Min_str = ToolUtil.BCDtointStr(Min);
                System.out.println("Min_str="+Min_str);

                //历史测量点，是hex类型的转成int看值是多少就可以了
                byte[] Num = Arrays.copyOfRange(data,6,1);
                System.out.println("Num="+Num);

                //倍率是什么我清楚，需要问
                byte[] FeqN = Arrays.copyOfRange(data,7,1);
                System.out.println("FeqN="+FeqN);


                //第3部   CS	1	校验码
                //说明，正常情况下接受报文是要去校验的，可以今后再去校验客户端的数据
                //，但是如果是服务器发给桩，那必须要生成校验码，给客户端做校验
                //我们可以今后再做校验，现在打印16进制字符串，供客户端和服务器报文比对
                byte[] cs=Arrays.copyOfRange(req,req.length-2,req.length-1);
                String cs_str= ISOUtil.hexString(cs);
                System.out.println("cs_str="+cs_str);

                //第4部   16H	1	结束码
                byte[] end=Arrays.copyOfRange(req,req.length-1,req.length);
                String end_str= ISOUtil.hexString(end);
                System.out.println("end_str="+end_str);



                //以上解析完

                //以下拼装报文返回给充电桩
                //报文协议文档可以看出，返回的控制码是0x82，我们组装报文，然后发送给桩

                //前面10个字节可以援用收到的报文
                //?疑问 ：L=09H+X（所有应答数据标识与数据内容的总长度）  这个长度怎么标识
                byte[] ret_h = Arrays.copyOfRange(req,0,10);
                //控制码0x82
                byte[] ret_c =new byte[]{(byte)0x82};   //控制码
                byte[] ret_l = ToolUtil.intToBytes(3, 2, 1);
                byte[] ret_data = new byte[3];
                ret_data = ToolUtil.fill0x00(3);


                List<byte[]> list = new ArrayList<byte[]>();
                list.add(ret_h);
                list.add(ret_c);
                list.add(ret_l);
                list.add(ret_data);
                //校验码
                //从第一个帧起始符开始到校验码之前的所有各字节的和模256的余。即各字节二进制算术和，不计超过256的溢出值。
                //首先合并校验位前的数组
                byte[] ret_cs_pre = ToolUtil.appendByte(list);
                byte[] ret_cs = new byte[]{(byte) ToolUtil.getCS(ret_cs_pre)};
                byte[] ret_end = new byte[]{(byte)0x16};   //结束码
                //瓶装结束，发给客户端

                List<byte[]> ret_list = new ArrayList<byte[]>();
                ret_list.add(ret_cs_pre);
                ret_list.add(ret_cs);
                ret_list.add(ret_end);

                //最后拼装的要发送的字节
                byte[] end_send = ToolUtil.appendByte(ret_list);

                //发送给客户端
                ByteBuf resp= Unpooled.copiedBuffer(end_send);
                ctx.writeAndFlush(resp);

		/*
             RequestObject ob=(RequestObject)msg;
             Map data=ob.getData();

	         byte[] sendData=buildData();
	         byte[] ret=ParsePackage.buildHeader(0,sendData.length , 21, buildData());
	         String pileNo=(String)data.get("SN");
	         chargePileServiceImpl.addHk20(data);

	         if(chargePileServiceImpl.countByPileNo(pileNo)==1){
	        //跟新充电桩的状态

			 chargePileServiceImpl.updateStatus(pileNo,"08","0");//充电桩状态01 空闲 02 告警 03空闲 04充电中 05 完成
				                                                //06 预约 07 等待
				                                                //00离线 08 签单 09 交换密钥
			 chargePileServiceImpl.updateGunStatus(pileNo,"08");
	         }
	         HKLogUtils.info("=================================发送的数据=======================");
	         HKLogUtils.info(ISOUtil.hexString(ret));

	         ByteBuf resp= Unpooled.copiedBuffer(ret);
	 		 ctx.writeAndFlush(resp);
         */
                return null;
        }

        private byte[] buildData() throws UnsupportedEncodingException{
                //DATA 200 ascii
                byte[] val="20000000".getBytes("ascii");
                return val;
        }





}
