package com.hc.app.action.zw;

import com.hc.app.model.Body0x54;
import com.hc.app.model.BodyI;
import com.hc.app.model.Head;
import com.hc.app.model.Meg;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ChargePileBillService;
import com.hc.app.service.ZWService;
import com.hc.common.utils.hk.ZWLogUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 账单信息上送
 *
 * @author liuh
 *
 */

@Component("ZW0x54")
public class ZW0x54 implements ZWActionI {
	@Autowired
	private ZWService zwServiceImpl;
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Autowired
	private ChargePileBillService chargePileBillServiceImpl;

	@Override
	public BodyI receive_or_send(ChannelHandlerContext ctx, Head head,
								 byte[] msg, boolean is_Debug) {
		Body0x54 body = new Body0x54(msg);

		// 为true 调试报文用
		if(is_Debug){
			head.setHead8_1(new byte[]{0x55});
			Meg meg = Meg.message(head,body);
			ctx.writeAndFlush(meg.getSendBuf());
			return body;
		}

		Map<String, String> bMap = body.bytesToMap();
		Map<String, String> hMap = head.bytesToMap();

		//桩编号
		String pileno_zw = hMap.get("head7_8");
		String order_id = bMap.get("body3_10");

		// 响应成功后写入账单表，更新订单表
		// 账单数据入库，并实时更新订单相关数据，

		Map orderInfo = null;
		try {
			orderInfo = chargeOrderServiceImpl.findByOrderId(order_id);
		} catch (Exception e) {
			ZWLogUtils.info("账单查询订单异常>>>>>>"+e.getMessage());
			return body;
		}
		String orderState = (String) orderInfo.get("ORDER_STATE");

		int body6_4_int = body.getBody6_4_int();  //业务处理用 body3_4 (原始)
//	     double body6_4_dou = new Integer(body6_4_int).doubleValue()*105/100;
//	     int body3_4_elequantity = new BigDecimal(body6_4_dou).setScale(0,0).intValue();

		if (!"04".equals(orderState)) {
			// 订单状态更改为 03 充电结束
			Map paramMap = new HashMap();
			paramMap.put("ORDER_STATE", "03");
			paramMap.put("CHARGE_USERID_9", order_id);

			try {
				chargeOrderServiceImpl.updateInfoHk(paramMap);
				chargeOrderServiceImpl.singleCheckBill(order_id);
			} catch (Exception e) {
				ZWLogUtils.info("账单更新订单异常"+e.getMessage());
				return body;
			}
		}

		//保存智网账单
		try {
			chargePileBillServiceImpl.saveZW(hMap, bMap,body6_4_int);
		} catch (Exception e) {
			ZWLogUtils.info("保存智网账单异常"+e.getMessage());
			return body;
		}

		//返回给智网
		head.setHead8_1(new byte[]{0x55});
		Meg meg = Meg.message(head,body);
		ctx.writeAndFlush(meg.getSendBuf());

		return body;
	}

	@Override
	public boolean business_todb(Meg meg, boolean is_Debug) {

		ZWLogUtils.info("写入数据库>>>>>>meg.bytesToMap()="+meg.bytesToMap());
		//if(is_Debug){
		zwServiceImpl.addBody0x54(meg.bytesToMap());
		//  return true;
		//}
		return true;
	}

}
