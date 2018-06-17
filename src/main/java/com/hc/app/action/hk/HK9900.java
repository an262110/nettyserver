package com.hc.app.action.hk;

import com.hc.app.action.BaseAction;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ChargePileService;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component("HK9900")
public class HK9900 implements BaseAction {
	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		    
//		if(msg!=null){
//            RequestObject ob=(RequestObject)msg;
//	        String pileNo=(String)ob.getOrderId();
//	        System.out.println("pileNo: "+pileNo);
//	        
//	        String long_link = null;
//	        
//	        if(NettyChannelMap.get(pileNo)!=null){
//	        	long_link = NettyChannelMap.get(pileNo).toString();
//	        }
//	        HKLogUtils.info("pileNo: "+pileNo+"单独关闭桩"+long_link);
//	        NettyChannelMap.get(pileNo).close();
//	        NettyChannelMap.remove(pileNo);
//        
//	        String retClient = "200"; 
//	       	ByteBuf resp= Unpooled.copiedBuffer(retClient.getBytes());
//	    	ctx.writeAndFlush(resp);
//	   	    ctx.close();
//		}else {
//			List list  = chargePileServiceImpl.searchPileList();
//			if(list!=null&&list.size()>0){
//				Iterator<Map> iter = list.iterator();
//				String pile_no=null;
//				 while(iter.hasNext()) {  
//		            Map map = iter.next();
//		            pile_no=map.get("CHARGE_PILE_SERI").toString();
//
//			     }
//			}
//		}
		
		chargeOrderServiceImpl.singleCheckBill("20170212160103005105");//结算
		
//		 Map orderInfo=chargeOrderServiceImpl.findByOrderSeril("00001413");
//		 double cele=Double.valueOf(10)*1.05;
//		 int CHARGE_ELE_QUANTITY=new java.math.BigDecimal(cele).setScale(0,java.math.BigDecimal.ROUND_UP).intValue();
//		
//		 System.out.println(CHARGE_ELE_QUANTITY);
//		 Map<String,Double> feeInfo=CaculateUtil.calculateFee(CHARGE_ELE_QUANTITY, orderInfo);
//		double servicePay =feeInfo.get("servicePay");//当前电费
//		double elePay=feeInfo.get("elePay");//当前服务费
//		
//		System.out.println(servicePay);
//		System.out.println(elePay);
		
	    return null;
	}
	
	private byte[] buildData() throws UnsupportedEncodingException{
		byte[] val="20000000".getBytes("ascii");
	    return val;
	}

}
