package com.hc.netty.handler;


import com.hc.app.action.kl.KLActionI;
import com.hc.app.utils.ToolUtil;
import com.hc.common.utils.KL.KLLogUtils;
import com.hc.common.utils.hk.HKLogUtils;
import com.hc.spring.SpringApplicationContext;
import com.yao.NettyChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.jpos.iso.ISOUtil;

import java.util.Arrays;

public class HcAppBusinessHandler extends ChannelHandlerAdapter {

//	private final String NORMAL_HEAD="7E68";
//	private final String EXTRAL_HEAD="7F79";
//	private final String CLIENT_HEAD="7E70";
//	private final String S_HEAD="7E99";
private final String NORMAL_HEAD="68";
@Override
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
        throws Exception {
    //处理远程主机强迫关闭了一个现有的连接（原因客户端断开）
    System.out.println("处理远程主机强迫关闭了一个现有的连接！");
    cause.printStackTrace();
    //长连接移除
    NettyChannelMap.remove((SocketChannel)ctx.channel());
    ctx.close();

    HKLogUtils.error(cause);
}

@Override
public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    // TODO Auto-generated method stub
    //super.channelInactive(ctx);
    System.out.println("处理连接已经不可用！");
    NettyChannelMap.remove((SocketChannel)ctx.channel());
//		System.out.println(ctx.channel().isActive());
}

@Override
public void channelRead(ChannelHandlerContext ctx, Object msg)
        throws Exception {

ByteBuf buf =(ByteBuf)msg;
byte[] req = new byte[buf.readableBytes()];
buf.readBytes(req);

//获取充电桩发过来的报文，并转成16进制字符串（目的方便调试 解析）
String reqStr= ISOUtil.hex2String(req);
//         reqStr = "68 26 22 22 11 11 11 C0 0B 68 89 0C 00 01 08 09 11 28 19 25 05 18 01 00 02 76 16";
//         System.out.println("接收到充电桩发过来的一侦报文="+reqStr);
    KLLogUtils.info("接收的一帧新报文：receive ="+reqStr);
    /**
     * 对接收的报文进行校验
     */
//		System.out.println("-~~~~~~~~~~~~~~~~~~~~~~开始对接收的报文进行校验~~~~~~~~~~~~~~~~~~~~~~");
    byte[] copyOfRange = Arrays.copyOfRange(req, 0, req.length - 2);//截取校验前的所有数据
    String curlstr = ISOUtil.hexString(copyOfRange);//转成十六进制
    String checksum = ToolUtil.makeChecksum(curlstr);//十六进制求和 %256
    byte[] jycode = Arrays.copyOfRange(req, req.length - 2, req.length - 1);
    String yzHCode = ISOUtil.hexString(jycode);//code 转成十六进制
    if(!checksum.equals(yzHCode)){
        KLLogUtils.info("校验未通过, 计算得出的结果为 ： "+checksum + ", 报文中的校验码为 ： " + yzHCode);
        return;
    }
    KLLogUtils.info("恭喜----->校验通过--------->");

//		System.out.println("-~~~~~~~~~~~~~~~~~~~~~~第一部分帧起始符开始~~~~~~~~~~~~~~~~~~~~~~");
//第一步 获取报文头 ox68 帧起始符 1个字节
//疑问，一帧数据为什么会有两个帧起始符，都是0x68
byte[] header=Arrays.copyOfRange(req,0,1);
String headeStr= ISOUtil.hexString(header);
//         System.out.println("第一部分帧起始符="+headeStr);

//第二部 RTUA 终端逻辑地址 6个字节  （与我协议不同处，是以字符存储）
//这6个字节是以二进制的地位在前高位在后的存储方式
//说明：逻辑地址传输时按照低位在前次序传输(B3B2B1A3A2A1)。前面为充电桩桩编号，后3为充电站编号
//疑问：1 设置上面充电桩参数的接口是哪个？
//注意1 这个解析出来是充电桩唯一的出厂序号，是建立长连接的基础（命名就是通过这个来标识不同充电桩）
//注意2 这个是与你二维码对应的编号，也就是说，你二维码对应的充电桩序列号，需要与这个一一对应，否则发送不了充电指令
    //         //解析要点： 平台充电桩需要需要，转成整形，在整形转bin字节数组传入，解析的时候，通过前三后三解析成整形，与平台序号对应
//		System.out.println("~~~~~~~~~~~~~~~~~~~~~~第二部分终端逻辑地址开始~~~~~~~~~~~~~~~~~~~~~~");
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

//		System.out.println("16进制终端逻辑地址处理完后的信息为 = "+rtua_str);

//		System.out.println("~~~~~~~~~~~~~~~~~~~~~~第三部分主站地址与命令序号开始~~~~~~~~~~~~~~~~~~~~~~");

//B3B2B1 充电桩序列号 看报文是二进制的整数，解析出来（这里需要与桩报文解析人员核对，是不是这个数字）
//         int rtua_pre = ToolUtil.BytesToint(Arrays.copyOfRange(rtua,0,3)); //充电桩序列号
//         System.out.println("充电桩序列号 rtua_pre="+rtua_pre);
//         int rtua_end = ToolUtil.BytesToint(Arrays.copyOfRange(rtua,3,6)); //充电站号
//         System.out.println("充电站号 rtua_end="+rtua_end);
//ToolUtil

//第三部   MSTA&SEQ	2	主站地址与命令序号
/*
D15	D14	D13	加密控制
0	0	0	不加密
0	0	1	DES
0	1	0	AES
0	1	1	SM4
1	0	0	MAC

上面报文需要跟桩厂商商量，是否需要上送一侦数据的加密报文，如果需要的话，要其给出加密算法，我们解析报文的第一步就需要解密，再解析报文
D7	 D6	  D5  D4  D3   D2	     D1    D0	MS1    与下面对应       0到5 为主站地址广播地址（不知道用途）D7 D6 也不知道
fir fin  2*5           ...      2*2   2*0       标识的意思自己去看报文，有什么意义
0    0   2的5次方最大64
------
桢标识（意思自己去看文档）

    D15 D14	D13	D12	D11	D10	D9	D8	MS2
    -----------
    加密方式
    取值都是0和1

    总结，d7 d6 只用解析出字节二进制的字符

*/
    /**
     * 第三部   MSTA&SEQ	2	主站地址与命令序号
     *
     *
     *
     *
     */
    byte[] msta=Arrays.copyOfRange(req,7,9);
String msta_str= ISOUtil.hexString(msta);
//         System.out.println("16进制主站地址与命令序号： = "+msta_str);
//打印二进制字符串
//获取第一个字节msg1，打印字符串
byte[] msg1=Arrays.copyOfRange(msta,0,1);
String msg1_str = ToolUtil.bytesToBits(msg1);
    String D7 = msg1_str.substring(0,1);//获取D7
    String D6 = msg1_str.substring(1,2);//获取D6
//		System.out.println("msg1 二进制字符串="+msg1_str);
//		System.out.println("msg1 D7 = "+D7);
//		System.out.println("msg1 D6 = "+D6);

//获取0到5的二进制字符串,把6和7 设置为0，反向生成 byt[] 为主站地址，可以看报文理解到，是个int
int host_msg1 = ToolUtil.bitStrTobyte("00"+msg1_str.substring(2));
//         System.out.println("host_msg1主站地址="+host_msg1);
//获取第二个字节msg2，打印字符串（类似msg1）
byte[] msg2=Arrays.copyOfRange(msta,1,2);
String msg2_str = ToolUtil.bytesToBits(msg2);

    String D15 = msg2_str.substring(0,1);//获取D15
    String D14 = msg2_str.substring(1,2);//获取D14
    String D13 = msg2_str.substring(2,3);//获取D13
    String bitFuncCode = msg2_str.substring(3,8);//二进制功能码需要转16进制


//		System.out.println("msg2 二进制字符串="+msg2_str);
//		System.out.println("msg2 D15 = "+D15);
//		System.out.println("msg2 D14 = "+D14);
//		System.out.println("msg2 D13 = "+D13);

//获取0到4的二进制字符串,把5,6和7 设置为0，反向生成 byt[] 为帧序号，可以看报文理解到，是个int
//         int order_msg2 = ToolUtil.bitStrTobyte("000"+msg1_str.substring(3));
//         System.out.println("order_msg2主站地址="+order_msg2);
//		System.out.println("~~~~~~~~~~~~~~~~~~~~~~第四部分帧起始符开始~~~~~~~~~~~~~~~~~~~~~~");



//第四部  （侦具体内容）   68H	1	帧起始符
byte[] h_head=Arrays.copyOfRange(req,9,10);
String h_head_str= ISOUtil.hexString(h_head);
//         System.out.println("第四部分帧起始符="+h_head_str);
//		System.out.println("~~~~~~~~~~~~~~~~~~~~~~第五部分控制码开始~~~~~~~~~~~~~~~~~~~~~~");

//第五部   C	 1	控制码
byte[] code=Arrays.copyOfRange(req,10,11);
String code_str= ISOUtil.hexString(code);
    String substring = ToolUtil.bytesToBits(code).substring(2, 8);
    System.out.println("控制码 = "+code_str);
    System.out.println("返回控制码为： = " + ToolUtil.bin2HexString(substring));


//以上5部为固定不变，控制码，是充电桩的功能码，从这里做业务分发，使用spring反射原理生成逻辑类
//例如 控制码是0x01 我们可以建立其action类 例如   Kl0x01

String actionName="KL0x"+code_str;

    KLLogUtils.info(">>>>>>>>>>>>>>>>>调用的Action地址为 : "+ actionName +"<<<<<<<<<<<<<<<<<<<<");
//	     BaseAction baseAction = (BaseAction) SpringApplicationContext.getService(actionName);
//	     baseAction.createSendInfo(ctx, req);
//		KLActionI klActionI = (KLActionI) SpringApplicationContext.getService("KL0x98");
    KLActionI klActionI = (KLActionI) SpringApplicationContext.getService(actionName);

    klActionI.createSendInfo(ctx, req,null);

//        String nowHead=NORMAL_HEAD;




/*
for(String res:reqArray){
         if(!"".equals(res)){
             res=nowHead+res;
             //HKLogUtils.info("数据包>>>>>>content="+res);
             byte[] resByte=ISOUtil.hex2byte(res);
             RequestObject request=DomUtil.parseByteToObject(resByte);
             if(request!=null){
                   HKLogUtils.info("交易码>>>>>>>>txcode="+request.getTxcode());
                   HKLogUtils.info(request);
                  if(!"".equals(request.getTxcode())){
                        //逻辑分发
                        String actionName="HK"+request.getTxcode();
                         BaseAction baseAction = (BaseAction) SpringApplicationContext.getService(actionName);

                         baseAction.createSendInfo(ctx, request);

                         if("20".equals(request.getTxcode())){

                             HKLogUtils.info("20交易,只处理第一个数据包!");
                             break;
                         }

                 }
            }
         }
}
*/
}


@Override
public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
}

@Override
public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
        IdleStateEvent event = (IdleStateEvent) evt;
        HKLogUtils.info("IDEL 检测 此时的连接:"+ctx.channel());

        //读写超时，关闭连接
        if (event.state() == IdleState.ALL_IDLE||event.state() == IdleState.READER_IDLE||event.state() == IdleState.WRITER_IDLE){
             HKLogUtils.info("此链接已经很长时间没活动了，需要移除;连接信息:"+ctx.channel());

             ctx.channel().close();
             HKLogUtils.info("桩序列号:"+ NettyChannelMap.remove((SocketChannel)ctx.channel()));
             HKLogUtils.info("读写超时移除连接！");
        }

    }

}

//68222222111111C10068A1110011111112051E0A0D1F00000000000000007A16
public static void main(String[] args) {

}



}

