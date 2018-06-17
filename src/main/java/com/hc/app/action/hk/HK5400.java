package com.hc.app.action.hk;

import com.hc.app.action.BaseAction;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ChargePileBillService;
import com.hc.common.utils.hk.CaculateUtil;
import com.hc.common.utils.hk.HKLogUtils;
import com.hc.common.utils.hk.ParsePackage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.jpos.iso.ISOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 充电结算信息
 * @Title:HK5400.java
 * @Package:com.hc.app.action.hk
 * @Description:TODO
 * @author zhifanglong
 * @Date 2016年7月25日 下午4:30:08
 * @Version V1.0
 */
@Component("HK5400")
public class HK5400 implements BaseAction {

	@Autowired
	private ChargePileBillService chargePileBillServiceImpl;

	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {

		RequestObject ob=(RequestObject)msg;
		Map data=ob.getData();
		/************业务逻辑************************************************/

		String orderSeril=(String)data.get("ORDER_NO");//订单序列号(用户标识号)
		String pileNo=(String)data.get("SN");//充电桩序列号
		int gunNo=(Integer)data.get("GUN_NO");//枪号

		/********************************响应逻辑********************************/
		byte[] sendData=buildData(gunNo);
		byte[] ret= ParsePackage.buildHeader(5401, sendData);

		HKLogUtils.info("=================================发送的数据=======================");
		HKLogUtils.info(ISOUtil.hexString(ret));

		ByteBuf resp= Unpooled.copiedBuffer(ret);
		ctx.writeAndFlush(resp);

		//响应成功后写入账单表，更新订单表
		//账单数据入库，并实时更新订单相关数据，

		Map orderInfo=chargeOrderServiceImpl.findByOrderSeril(orderSeril);
		String orderId=(String)orderInfo.get("CHARGE_ORDER_ID");
		String orderState=(String)orderInfo.get("ORDER_STATE");

		int CHARGE_ELE_QUANTITY=(Integer)data.get("CHARGE_QUANTITY")*10;//已充电量

		double cele=Double.valueOf(CHARGE_ELE_QUANTITY);

		cele=cele*1.05;
		CHARGE_ELE_QUANTITY=new java.math.BigDecimal(cele).setScale(0,java.math.BigDecimal.ROUND_UP).intValue();
		if(!"04".equals(orderState)){
			//计算费用
			Map<String,Double> feeInfo=CaculateUtil.calculateFee(CHARGE_ELE_QUANTITY, orderInfo);
			double servicePay =feeInfo.get("servicePay");//当前电费
			double elePay=feeInfo.get("elePay");//当前服务费

			//订单状态更改为 03 充电结束
			Map paramMap = new HashMap();
			paramMap.put("ORDER_STATE", "03");
			paramMap.put("CHARGE_USERID_9", orderSeril);
		  /*paramMap.put("END_CHARGE_TIME", "1");
		  paramMap.put("TOTAL_CHARGE_QUANTITY",String.valueOf(CHARGE_ELE_QUANTITY));
		  paramMap.put("TOTAL_CHARGE_MONEY",String.valueOf(elePay));
		  paramMap.put("TOTAL_SERVICE_MONEY",String.valueOf(servicePay));
		  paramMap.put("ORI_SERVICE_MONEY",feeInfo.get("oriServicePay"));
		  paramMap.put("ORI_CHARGE_MONEY", feeInfo.get("oriElePay"));*/

			chargeOrderServiceImpl.updateInfoHk(paramMap);

			chargeOrderServiceImpl.singleCheckBill(orderId);//结算
		}else{
			HKLogUtils.info("重复账单,重复结算!");
		}

		chargePileBillServiceImpl.savehk(data);

		return null;
	}

	private byte[] buildData(int gunNo) throws UnsupportedEncodingException{
		//DATA 200 ascii
		byte[] val=new byte[2];
		val[0]=(byte) ((0 >>>0) & 0xff);
		val[1]=(byte) ((gunNo >>>0) & 0xff);

		return val;
	}

}


