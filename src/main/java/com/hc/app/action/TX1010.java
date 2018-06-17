package com.hc.app.action;

import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ChargePileBillService;
import com.hc.app.utils.MAC;
import com.hc.common.utils.CommonUtil;
import com.hc.common.utils.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang.ArrayUtils;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Component("TX1010")
public class TX1010 implements BaseAction {

	@Autowired
	private ChargePileBillService chargePileBillServiceImpl;
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		byte[] req=(byte[])msg;
		ISOMsg request=CommonUtil.parsePackage(req);
				
		ISOMsg sendMsg=new ISOMsg();
		//String header="0026303030303030";
		String header="303030303030";
		sendMsg.setHeader(ISOUtil.hex2byte(header));
		sendMsg.set("39","00");
		sendMsg.set("63","00000");
		sendMsg.set("0","1011");
		ISOPackager packager = new GenericPackager(CommonUtil.getConfigPath(request.getString(0),"OUT"));
		sendMsg.setPackager(packager);
				
		
		byte[] res=sendMsg.pack();
		byte[] both = (byte[]) ArrayUtils.addAll(ISOUtil.hex2byte(header),res);
		byte[] mb= new byte[both.length-16];
				System.arraycopy(both,0,mb,0, both.length-16);
		String mac= new MAC().encrypt(mb);
		sendMsg.set("63",mac);
		 res=sendMsg.pack();
		 both = (byte[]) ArrayUtils.addAll(ISOUtil.hex2byte(header),res);
		 both=CommonUtil.addLength(both);
		 
		sendMsg.dump(System.out, "");
		LogUtils.info("--------------------------(TX1010:结束充电上送接口 )响应的报文--------------------------");
		LogUtils.info(ISOUtil.hexString(both));
		
		ByteBuf resp= Unpooled.copiedBuffer(both);
		ctx.writeAndFlush(resp);
		
		CommonUtil.dumpInfo(sendMsg);
		
		String orderId=request.getString(11);
		//账单数据入库，并实时更新订单相关数据，
		chargePileBillServiceImpl.save(request, sendMsg);
		Map orderInfo=chargeOrderServiceImpl.findByOrderId(orderId);
		String order_state=(String)orderInfo.get("ORDER_STATE");
		if(!"04".equals(order_state)){
			//订单状态更改为 03 充电结束
			Map paramMap = new HashMap();
			paramMap.put("ORDER_STATE", "03");
			paramMap.put("CHARGE_ORDER_ID", orderId);
			//累计充电量
			paramMap.put("TOTAL_CHARGE_QUANTITY", request.getString(5)); 
			//累计充电时间
			//paramMap.put("TOTAL_CHARGE_TIMES", request.getString(11));
			//累计电量金额
			paramMap.put("TOTAL_CHARGE_MONEY", request.getString(4));
			//累计服务费金额
			paramMap.put("TOTAL_SERVICE_MONEY", request.getString(6));
			paramMap.put("END_CHARGE_TIME", "1");
			
			String startTime=request.getString("61");
			String endTime=request.getString("59");
			
			//累计充电时间
			DateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");
			Calendar cal = Calendar.getInstance();    
	        cal.setTime(df.parse(startTime));    
	        long time1 = cal.getTimeInMillis();           
	        cal.setTime(df.parse(endTime));    
	        long time2 = cal.getTimeInMillis();         
	        long between=(time2-time1)/(1000*60); 
	        paramMap.put("TOTAL_CHARGE_TIMES", String.valueOf(between));
			chargeOrderServiceImpl.updateInfo(paramMap);
			
			chargeOrderServiceImpl.singleCheckBill(orderId);//结算
		}else{
			LogUtils.info("重复账单,重复结算!");
		}
		return both;
	}

}
