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

@Component("KL0x01")
public class KL0x01 implements BaseAction {
    //	@Autowired
//	private ChargePileService chargePileServiceImpl;
    @Override
    public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buf =(ByteBuf)msg;
        byte[] req=new byte[buf.readableBytes()];
        buf.readBytes(req);

        //第1部   L	2	数据长度  ,正常情况，都是需要去校验数据域长度的，也可以通过后面的校验码去校验
        byte[] len = ToolUtil.bytesReverseOrder(Arrays.copyOfRange(req,11,13));
        String len_str= ISOUtil.hexString(len);
        System.out.println("len_str="+len_str);
        //数据域的长度
        int len_data = ToolUtil.byteToInt(len[0]);
        System.out.println("len_data="+len_data);


        //第2部   DATA	变长	数据域
        byte[] data=Arrays.copyOfRange(req,11,req.length-2);
        String data_str= ISOUtil.hexString(data);
        System.out.println("data_str="+data_str);

        //按照协议的项，好像是只有三个两个字节 TN和 DI
        //这个好像只需要解析成16进制字符串
        byte[] tn = Arrays.copyOfRange(data,0,1);  //测量点号TN：0固定为终端；FEH表示所有测量点；FFH表示终端和所有测量点。
        System.out.println("data_tn="+ ToolUtil.bytesToHexString(tn));

        //这两个字节我不知道用途，解析成16进制字符串，去问桩协议人员看是什么意思
        byte[] di =Arrays.copyOfRange(data,1,2);
        System.out.println("di_tn="+ ToolUtil.bytesToHexString(di));


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
        //报文协议文档可以看出，返回的控制码是0x81，我们组装报文，然后发送给桩

        //前面10个字节可以援用收到的报文
        //?疑问 ：数据长度：L=02H+X（所有应答数据标识与数据内容的总长度）  这个长度怎么标识
        byte[] ret_h = Arrays.copyOfRange(req,0,10);
        //控制码0x81
        byte[] ret_c =new byte[]{(byte)0x81};   //控制码
        byte[] ret_l = ToolUtil.intToBytes(3, 2, 1);  //整数3 ，两个字节存储，倒叙赋值
        byte[] ret_data = new byte[3];          //tn一个字节，di两个字节 （没有说具体内容填充0x00）
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
