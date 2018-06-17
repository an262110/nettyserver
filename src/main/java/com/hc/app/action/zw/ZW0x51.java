package com.hc.app.action.zw;

import com.hc.app.model.Body0x51;
import com.hc.app.model.BodyI;
import com.hc.app.model.Head;
import com.hc.app.model.Meg;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ChargePileService;
import com.hc.app.service.ZWService;
import com.hc.common.utils.hk.ZWLogUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 心跳报文
 * @author liuh
 *
 */

@Component("ZW0x51")
public class ZW0x51 implements ZWActionI {
	@Autowired
	private ZWService zwServiceImpl;
	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Override
	public BodyI receive_or_send(ChannelHandlerContext ctx, Head head,
                                 byte[] msg, boolean is_Debug) {
		
		Body0x51 body = new Body0x51(msg);
		//3分钟+10s没有心跳或者3分钟没有充电信息上送，则判断桩离线
		
		
		if(is_Debug){ // 为true 调试报文用
			head.setHead8_1(new byte[]{0x52});
			head.setHead2_2(new byte[]{0x00,0x00});
			System.out.println("心跳连接状态："+((SocketChannel)ctx.channel()).toString());
			System.out.println(head.getHead_hexstr());
			ctx.writeAndFlush(head.getSendBuf());
			return body;
		}
		
		Map<String, String> bMap = body.bytesToMap();
        Map<String, String> hMap = head.bytesToMap(); 
        
        //桩编号 桩错误码
		String is_body_suc = bMap.get("body3_4");
		String pileno_zw = hMap.get("head7_8");
		
		//枪口
		String body1_1_str = body.getBody1_1_str();//高4位 0 A口 1 B口 低4位 0空闲 1充电
		char[] gun_no_arr = body1_1_str.toCharArray();
		int gun_no = Character.getNumericValue(gun_no_arr[0]);
		
		//插枪状态
		String body2_1_str = body.getBody2_1_str();//充电枪状态
		char[] pile_no_arr = body2_1_str.toCharArray();
		String chargeStatus = String.valueOf(pile_no_arr[0]);
		
		//枪口状态 1 等待 2 充电
		String gun_status = String.valueOf(pile_no_arr[3]);
		
		//桩状态
		String pile_status = "01";
		if(!"000000".equals(is_body_suc)){
			gun_status = "3";  //故障						
		}
		
		if("0".equals(gun_status)){
			pile_status = "03";   //空闲
		}else if("1".equals(gun_status)){
			pile_status = "04";   //充电中
		}
				
		try {
			//更新充电桩和枪为故障 枪编码 擦枪状态  枪状态  最终状态 枪口
			chargePileServiceImpl.updateGunStatus(pileno_zw, chargeStatus, gun_status, pile_status, gun_no);
			//更新桩状态为 故障
			chargePileServiceImpl.updateStatus(pileno_zw, pile_status);
		} catch (Exception e) {
			//e.printStackTrace();
			 ZWLogUtils.info("心跳包更新异常>>>>>>"+e.getMessage());
			 return body;
		}
                         
		return body;
	}
	
	//数据库写入，有逻辑，比如心跳和账单，所以分开写
	@Override
	public boolean business_todb(Meg meg, boolean is_Debug) {
	    ZWLogUtils.info("写入数据库>>>>>>meg.bytesToMap()="+meg.bytesToMap());
	   // if(is_Debug){
	    	zwServiceImpl.addBody0x51(meg.bytesToMap());
	    	//return true;
	   // }
		
		return true;
	}

}
