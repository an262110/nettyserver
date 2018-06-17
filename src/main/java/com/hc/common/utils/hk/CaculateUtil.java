package com.hc.common.utils.hk;

import com.hc.app.service.TariffPolicyService;
import com.hc.spring.SpringApplicationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaculateUtil {
	public static Map<String,Double> calculateFee(int eleQuantity,Map orderInfo) throws Exception{
		TariffPolicyService tariffPolicyServiceImpl = (TariffPolicyService) SpringApplicationContext.getService("tariffPolicyServiceImpl");
		 String  policyId=orderInfo.get("POLICY_ID").toString();
		 //上时段总电量
		 int chargedQuantity=Integer.valueOf(orderInfo.get("TOTAL_CHARGE_QUANTITY").toString());
		 //已用充电金额
		 double lastElePay=Double.valueOf(orderInfo.get("TOTAL_CHARGE_MONEY").toString());
		 //已用服务费
		 double lastServicePay=Double.valueOf(orderInfo.get("TOTAL_SERVICE_MONEY").toString());
		 
		//未打折充电金额
		 double oriLastElePay=Double.valueOf(orderInfo.get("ORI_CHARGE_MONEY").toString());
		 //未打折已用服务费
		 double oriLastServicePay=Double.valueOf(orderInfo.get("ORI_SERVICE_MONEY").toString());
		 
		 
		 //打折标志
		 String discountFlag=orderInfo.get("DISCOUNT_FLAG").toString();//0不打折 1打折
		 //电费折扣
		 int eleDiscount=Integer.valueOf(orderInfo.get("ELE_DISCOUNT").toString());
		 //服务费折扣
		 int serviceDiscount=Integer.valueOf(orderInfo.get("SERVICE_DISCOUNT").toString());
		 
		 eleQuantity-=chargedQuantity;//本时间段内充的电量=当前电量-上次充的电量
		 Map<String,Double> fee=new HashMap<String,Double>();
		 DateFormat df=new SimpleDateFormat("HHmm");
		 String now=df.format(new Date());
		//查找资费策略
			Map policyInfo=tariffPolicyServiceImpl.findTariffPolicy(policyId);
			List priceInfoList=tariffPolicyServiceImpl.findDivisionPrice(policyId);
			
			//电价
			int eleFee=Integer.valueOf(policyInfo.get("COMMON_ELEC_PRICE").toString());
			//服务费
			int serviceFee=Integer.valueOf(policyInfo.get("CHARGE_SERVICE_FEE").toString());
			//计算当前时间的分时电价
			for(int i=0;i<priceInfoList.size();i++){
				Map priceInfo=(Map)priceInfoList.get(i);

				String startTime=priceInfo.get("DIVISION_START_TIME").toString();
				String endTime=priceInfo.get("DIVISION_END_TIME").toString();
				
				if(now.compareTo(startTime)>=0&&now.compareTo(endTime)<0){
					String colname=(String)priceInfo.get("DIVISION_TYPE");
					eleFee=Integer.valueOf(policyInfo.get(colname.toUpperCase()).toString());
					break;
				}
				
				
			}
		  
		 //费用打折处理
		 if(!"1".equals(discountFlag)){//不打折
			 eleDiscount=100;
			 serviceDiscount=100;
		 }
		  //计算费用	 由于电量是两位小数，所以最终值需除以100,保留四位小数 （单位 分） 
		 
		  double servicePay=serviceFee*eleQuantity*serviceDiscount;
		  double elePay=eleFee*eleQuantity*eleDiscount;
		  
		  double oriServicePay=serviceFee*eleQuantity;
		  double oriElePay=eleFee*eleQuantity;
		  
		  BigDecimal sp=new BigDecimal(servicePay);
		  BigDecimal ep=new BigDecimal(elePay);
		  
		  BigDecimal lastSP=new BigDecimal(lastServicePay);
		  BigDecimal lastEP=new BigDecimal(lastElePay);
		  
		  
		  
		  BigDecimal d=new BigDecimal(10000);//电量扩大了100倍，折扣扩大了100倍
		  servicePay=(sp.divide(d,4, RoundingMode.HALF_UP)).add(lastSP).setScale(4, RoundingMode.HALF_UP).doubleValue();
		  elePay=(ep.divide(d,4, RoundingMode.HALF_UP)).add(lastEP).setScale(4, RoundingMode.HALF_UP).doubleValue();
		  
		  fee.put("servicePay",servicePay);
		  fee.put("elePay",elePay);
		  
		  //计算原价
		 BigDecimal oriSP=new BigDecimal(oriServicePay);
		  BigDecimal oriEP=new BigDecimal(oriElePay);
		  
		  BigDecimal oriLastSP=new BigDecimal(oriLastServicePay);
		  BigDecimal oriLastEP=new BigDecimal(oriLastElePay);
		  
		  BigDecimal dOri=new BigDecimal(100);//电量扩大了100倍
		  oriServicePay=(oriSP.divide(dOri,4, RoundingMode.HALF_UP)).add(oriLastSP).setScale(4, RoundingMode.HALF_UP).doubleValue();
		  oriElePay=(oriEP.divide(dOri,4, RoundingMode.HALF_UP)).add(oriLastEP).setScale(4, RoundingMode.HALF_UP).doubleValue();
		  
		  fee.put("oriServicePay",oriServicePay);
		  fee.put("oriElePay",oriElePay);
		  
		  HKLogUtils.info("servicePay: "+servicePay);
		  HKLogUtils.info("elePay: "+elePay);
		  HKLogUtils.info("oriServicePay: "+oriServicePay);
		  HKLogUtils.info("oriElePay: "+oriElePay);
		  return fee;
	 }
}
