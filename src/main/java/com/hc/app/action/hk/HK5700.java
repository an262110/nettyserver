package com.hc.app.action.hk;


import com.hc.app.action.BaseAction;
import com.hc.app.service.AccountInfoService;
import com.hc.app.service.ChargeHKService;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ChargePileService;
import com.hc.common.utils.CommonUtil;
import com.hc.common.utils.hk.CaculateUtil;
import com.hc.common.utils.hk.HKLogUtils;
import com.hc.common.utils.hk.ParsePackage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.jpos.iso.ISOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component("HK5700")
public class HK5700 implements BaseAction{
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Autowired
	private AccountInfoService accountInfoServiceImpl;
	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Autowired
	private ChargeHKService chargeHKServiceImpl;
	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		RequestObject ob=(RequestObject)msg;
        Map data=ob.getData();
        
        chargePileServiceImpl.addChargeStatus(data);
        
        String orderSeril=(String)data.get("ORDER_NO");
		int V_AC_VALUE=(Integer)data.get("V_AC_VALUE");    
		int A_AC_VALUE=(Integer)data.get("A_AC_VALUE");
		int V_DC_VALUE=(Integer)data.get("V_DC_VALUE");    
		int A_DC_VALUE=(Integer)data.get("A_DC_VALUE");
		int chargeTime=(Integer)data.get("CHARGE_TIME");//已充时间
		int CHARGE_ELE_QUANTITY=(Integer)data.get("CHARGE_ELE_QUANTITY")*10;//已充电量
		String CHARGE_START = data.get("CHARGE_START").toString();
		
		if(chargeTime==0){
			SimpleDateFormat   df   =   new   SimpleDateFormat("yyyyMMddHHmmss");   
			  Date   begin=df.parse(CHARGE_START);   
			  Date   end   =   Calendar.getInstance().getTime();
			  long   between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒   
			  //System.out.println(between/60);
			  chargeTime = (int) (between/60);
		}
		
		double cele=Double.valueOf(CHARGE_ELE_QUANTITY);
		
		cele=cele*1.05;
		CHARGE_ELE_QUANTITY=new java.math.BigDecimal(cele).setScale(0,java.math.BigDecimal.ROUND_UP).intValue();
		
		int SOC=(Integer)data.get("CURR_SOC");//已充电量
		
		//订单
		Map orderInfo=chargeOrderServiceImpl.findByOrderSeril(orderSeril);
		String orderId=(String)orderInfo.get("CHARGE_ORDER_ID");
		String gun_code=(String)orderInfo.get("GUN_CODE");
		String orderState=(String)orderInfo.get("ORDER_STATE");
		
		if(!"04".equals(orderState)){
			
			//计算费用
			Map<String,Double> feeInfo=CaculateUtil.calculateFee(CHARGE_ELE_QUANTITY, orderInfo);
			
			double servicePay =feeInfo.get("servicePay");//当前电费
			double elePay=feeInfo.get("elePay");//当前服务费
			
		    double fee=servicePay+elePay;//总费用
			//更新资费信息到订单
			 Map<String,Object> params = new HashMap<String,Object>();
			 //if(A_AC_VALUE==0){
				V_AC_VALUE=V_DC_VALUE;
				 A_AC_VALUE=A_DC_VALUE;
			 //}
			 
			 
			 
			 params.put("CURRENT_V",String.valueOf(V_AC_VALUE));
			 params.put("CURRENT_A",String.valueOf(A_AC_VALUE*10));
			 params.put("TOTAL_CHARGE_TIMES",String.valueOf(chargeTime));
			 params.put("TOTAL_CHARGE_QUANTITY",String.valueOf(CHARGE_ELE_QUANTITY));
			 params.put("TOTAL_CHARGE_MONEY",String.valueOf(elePay));
			 params.put("TOTAL_SERVICE_MONEY",String.valueOf(servicePay));
			 params.put("ORI_SERVICE_MONEY",feeInfo.get("oriServicePay"));
			 params.put("ORI_CHARGE_MONEY", feeInfo.get("oriElePay"));
			 params.put("SOC",String.valueOf(SOC));
			 params.put("END_CHARGE_TIME", "1");
			 params.put("BMS_TYPE",String.valueOf(data.get("BMS_TYPE")));
			 params.put("CHARGE_ORDER_ID", orderId);
			 
			 if("01".equals(orderState)){
				 params.put("ORDER_STATE","02");
			 }
			 
			 chargeOrderServiceImpl.updateInfo(params);
			 
			//验证账户余额是否足够本次支付
			 int balance=0;
			 String payType=(String)orderInfo.get("ORDER_TYPE");
			if(!"2".equals(payType)){
			   Map account=accountInfoServiceImpl.findByUserId(orderInfo.get("USER_ID").toString());
			   balance=Integer.valueOf(account.get("FROZEN_MONEY").toString());
			   System.out.println("FROZEN_MONEY: "+balance);
			}else{
				balance=Integer.valueOf(orderInfo.get("PAY_MONEY").toString());
				HKLogUtils.info("PAY_MONEY: "+balance);
			}
			if(fee>0&&fee>=balance){
				HKLogUtils.info("info>>>>>>>>余额不足");
				HKLogUtils.info("info>>>>>>>>账户余额"+balance);
				
				HKLogUtils.info("info>>>>>>>>消费金额"+fee);
				HKLogUtils.info("info>>>>>>>>电费"+elePay);
				HKLogUtils.info("info>>>>>>>>服务费"+servicePay);
				HKLogUtils.info("info>>>>>>>>orderid"+orderId);
				HKLogUtils.info("info>>>>>>>>gun_code"+gun_code);
				
				//发送停止充电指令
	//			BaseAction baseAction = (BaseAction) SpringApplicationContext.getService("HK6200");
	//	     	
	//			RequestObject request=new RequestObject();
	//			request.setOrderId(orderId);
	//	        request.setTxcode("6200");
	//	     	baseAction.createSendInfo(ctx,request);
				
				//String ret=HcAppClient.getInstance().sendPre(orderId, gun_code, "2");
	
	
				
				
				String ret = send6200(ctx,orderInfo);
				System.out.println("自动停  止返回："+ret);
		     	
			 }
		}else{
			HKLogUtils.info("订单已结算，延迟的状态数据包");
			HKLogUtils.info("数据包电量信息，TOTAL_CHARGE_QUANTITY="+CHARGE_ELE_QUANTITY);
			HKLogUtils.info("订单电量信息，TOTAL_CHARGE_QUANTITY="+orderInfo.get("TOTAL_CHARGE_QUANTITY").toString());
		}
		 	return null;
		}
	 
	private String send6200(ChannelHandlerContext ctx, Map mapOrder) throws Exception{
		 //Map mapOrder = chargeOrderServiceImpl.findByOrderId(business_no);
		 
		 String retClient = "200";
         
         //充电桩序列号与枪号
         String  gunCode= mapOrder.get("GUN_CODE").toString();        
         Map gunInfo=chargePileServiceImpl.findByGunCode(gunCode);
         
         if(gunInfo==null){
         	retClient = "500"; //充电枪不存在
           	System.out.println("[500]充电枪不存在！");
           	HKLogUtils.error("[500]充电枪不存在！");
           	ByteBuf resp= Unpooled.copiedBuffer(retClient.getBytes());
        	ctx.writeAndFlush(resp);
       	    //ctx.close();
       	    return retClient;
         }
         
       //获取用户标识号(停止需要唯一)
         String CHARGE_USERID =mapOrder.get("CHARGE_USERID_9").toString();
         HKLogUtils.info("充电用户标识号："+CHARGE_USERID);
        
         String pileNo=(String)gunInfo.get("PILE_SERI");
         String gunNo=gunInfo.get("GUN_NO").toString();
         HKLogUtils.info("桩序列号："+pileNo);
         HKLogUtils.info("枪编号："+gunNo);
         
         /********************构建命令数据**************************/
         
         byte[] data=buildData(CHARGE_USERID,pileNo,Integer.valueOf(gunNo));
         //1.添加标准头
         byte[] head_data= ParsePackage.buildHeader(6200,data);
         
         //2.添加下发控制头
         
         byte[] sendData= ParsePackage.buildControlHeader(head_data);
         ByteBuf resp= Unpooled.copiedBuffer(sendData);
         HKLogUtils.info("=================================发送的数据=======================");
         HKLogUtils.info(ISOUtil.hexString(sendData));
         
 		//Channel sc=NettyChannelMap.get(pileNo);	
 		
// 		if(ctx!=null){
// 			LogUtils.info("发送指令的连接通道：====="+sc.toString());
// 			if(ctx.isActive()||ctx.isOpen()){
// 				HKLogUtils.info("活动的连接！");
// 			}else{
// 				HKLogUtils.info("不活动的连接！");
// 			}
 			ctx.writeAndFlush(resp).addListener(new ChannelFutureListener() {
 				
 				@Override
 				public void operationComplete(ChannelFuture arg0) throws Exception {
 					if(arg0.isSuccess()){
 						HKLogUtils.info("info>>>>>>>>>>>>>发送成功");
 						
 					}else{
 						HKLogUtils.info("info>>>>>>>>>>>>>发送失败");
 					}
 				}
 			});

 			int messageId= ParsePackage.count;
			Map chargeParams=new HashMap();
			chargeParams.put("MESSAGE_ID",messageId);
			chargeParams.put("MESSAGE_TYPE",6200);
			chargeParams.put("ORDER_ID",mapOrder.get("CHARGE_ORDER_ID"));
			chargeParams.put("FACTORY_ID","0001");
			chargeParams.put("PILE_SERI",pileNo);
			chargeParams.put("GUN_NO",gunNo);
			
			chargeHKServiceImpl.save(chargeParams);

         
		 return retClient;
	 }
	 
	 private byte[] buildData(String orderSeril,String pileNoStr,int gunNo) throws UnsupportedEncodingException{
		 byte[] data = new byte[56];
		 
		 byte[] orderNo=orderSeril.getBytes("ascii");
		 System.arraycopy(orderNo,0,data,0,8);
		 
		 byte[] factoryNo="0001".getBytes("ascii");
		 System.arraycopy(factoryNo,0,data,8,4);
		 
		 byte[] pileNo=pileNoStr.getBytes("ascii");
		 System.arraycopy(pileNo,0,data,12,12);
		 
		 //充电枪编号
		 data[24] =(byte)((gunNo >>> 0) & 0xff);
		 
		 byte[] userId=CommonUtil.buildString("88888888888",20).getBytes("ascii");
		 System.arraycopy(userId,0,data,25,20);
		 
		 byte[] phoneNo="88888888888".getBytes("ascii");
		 System.arraycopy(phoneNo,0,data,45,11);		 	 		 
		 
	    return data;
		
		}
  
}
