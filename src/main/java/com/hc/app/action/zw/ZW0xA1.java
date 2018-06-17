package com.hc.app.action.zw;

import com.hc.app.model.*;
import com.hc.app.service.ChargePileService;
import com.hc.app.service.ZWService;
import com.hc.common.utils.hk.ZWLogUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 桩注册接受
 * @author liuh
 *
 */

@Component("ZW0xA1")
public class ZW0xA1 implements ZWActionI {
	@Autowired
	private ZWService zwServiceImpl;
	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Override
	public BodyI receive_or_send(ChannelHandlerContext ctx, Head head,
								 byte[] msg, boolean is_Debug) {
		Body0xA1 body = new Body0xA1(msg);

		if(is_Debug){ // 为true 调试报文用
			head.setHead8_1(new byte[]{(byte)0xA2});
			Meg meg = Meg.message(head,new Body0xA2(head));
			ctx.writeAndFlush(meg.getSendBuf());
			return body;
		}

		//Map<String, String> bMap = body.bytesToMap();
		Map<String, String> hMap = head.bytesToMap();

		//桩编号
		String pileno_zw = hMap.get("head7_8");
		//桩是否在我们平台
		//充电桩状态01 空闲 02 告警 03空闲 04充电中 05 完成 06 预约 07 等待 00离线 08 签单 09 交换密钥
		try{
			if(chargePileServiceImpl.countByPileNo(pileno_zw)==1){
				chargePileServiceImpl.updateStatus(pileno_zw,"08","0");
				chargePileServiceImpl.updateGunStatus(pileno_zw,"08");
			}
		} catch (Exception e) {
			ZWLogUtils.info("桩注册签到A1更新异常>>>>>>"+e.getMessage());
			return body;
		}

		//返回给智网
		head.setHead8_1(new byte[]{(byte)0xA2});
		Meg meg = Meg.message(head,new Body0xA2(head));
		ctx.writeAndFlush(meg.getSendBuf());

		return body;
	}

	@Override
	public boolean business_todb(Meg meg, boolean is_Debug) {

		ZWLogUtils.info("写入数据库>>>>>>meg="+meg);
		ZWLogUtils.info("写入数据库>>>>>>meg.bytesToMap()="+meg.bytesToMap());
		if(is_Debug){
			zwServiceImpl.addBody0xA1(meg.bytesToMap());
			return true;
		}

		return true;
	}

}
