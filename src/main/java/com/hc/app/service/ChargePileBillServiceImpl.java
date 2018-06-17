package com.hc.app.service;

import com.hc.app.dao.ChargeOrderDao;
import com.hc.app.dao.ChargePileBillDao;
import com.hc.common.utils.hk.HKLogUtils;
import com.hc.common.utils.hk.ZWLogUtils;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChargePileBillServiceImpl implements ChargePileBillService {
    @Autowired
    private ChargePileBillDao chargePileBillDao;
    @Autowired
    private ChargeOrderDao chargeOrderDao;

    //易事特账单结算
	@Override
	public void save(ISOMsg in, ISOMsg out) throws Exception {
		String business_no_11 = in.getString(11);
		int c=chargePileBillDao.countBill(business_no_11);
		if(c==0){
			Map paramMap = new HashMap();
			paramMap.put("MSG_TYPE", in.getString(0));
			paramMap.put("user_id_2", in.getString(2));
			paramMap.put("status_3", in.getString(3));
			paramMap.put("charge_amount_4", in.getString(4));
			paramMap.put("ele_quantity_5", in.getString(5));
			paramMap.put("service_charge_6", in.getString(6));
			paramMap.put("amount_7", in.getString(7));
			paramMap.put("charge_type_8", in.getString(8));
			paramMap.put("service_type_9", in.getString(9));
			paramMap.put("business_no_11", in.getString(11));
			paramMap.put("account_balance_30", in.getString(30));
			paramMap.put("pile_no_41", in.getString(41));
			paramMap.put("ammeter_start_46", in.getString(46));
			paramMap.put("ammeter_end_47", in.getString(47));
			paramMap.put("average_price_48", in.getString(48));
			paramMap.put("price_one_49", in.getString(49));
			paramMap.put("price_two_50", in.getString(50));
			paramMap.put("price_three_51", in.getString(51));
			paramMap.put("price_four_52", in.getString(52));
			paramMap.put("car_no_60", in.getString(60));
			paramMap.put("charge_start_61", in.getString(61));
			paramMap.put("charge_end_59", in.getString(59));
			paramMap.put("terminal_time_62", in.getString(62));
			paramMap.put("mac_63", in.getString(63));
			paramMap.put("ret_status_39", out.getString(39));
			paramMap.put("ret_mac_63", out.getString(63));
			
			chargePileBillDao.addRecord(paramMap);
		}else{
			HKLogUtils.info("收到重复账单，丢弃;business_no_11=="+business_no_11);
		}
	}

	//合康
		@Override
		public void savehk(Map data) throws Exception {
			
			int CHARGE_ELE_QUANTITY=(Integer)data.get("CHARGE_QUANTITY")*10;//已充电量
			 double cele=Double.valueOf(CHARGE_ELE_QUANTITY);
				
				cele=cele*1.05;
				CHARGE_ELE_QUANTITY=new BigDecimal(cele).setScale(0, BigDecimal.ROUND_UP).intValue();
				
			String business_no_11 = data.get("ORDER_NO").toString();
			int c=chargePileBillDao.countBill(business_no_11);
			if(c==0){
			Map map = chargeOrderDao.searchOrderDetailHk(business_no_11);
			
			Map paramMap = new HashMap();
			paramMap.put("MSG_TYPE", "5400");
			paramMap.put("user_id_2", data.get("ORDER_NO"));
			paramMap.put("status_3", data.get("STOP_TYPE"));
			paramMap.put("charge_amount_4", data.get("NEED_MONEY"));
			paramMap.put("ele_quantity_5",CHARGE_ELE_QUANTITY );
			paramMap.put("service_charge_6", map.get("TOTAL_SERVICE_MONEY"));//服务费（订单表查询）
			Object totalChargeManey=map.get("TOTAL_CHARGE_MONEY");
			Object totalServiceManey=map.get("TOTAL_SERVICE_MONEY");
			
			 totalChargeManey= (totalChargeManey==null?"0": totalChargeManey);
			 totalServiceManey=(totalServiceManey==null?"0":totalServiceManey);
			 
			Double total=Double.valueOf(totalChargeManey.toString())+Double.valueOf(totalServiceManey.toString());
			total+=0.5;
			paramMap.put("amount_7", total.intValue());  //订单表 当前电量费用+累计服务费
			paramMap.put("charge_type_8", data.get("PILE_MODE"));
			paramMap.put("service_type_9", "03");
			paramMap.put("business_no_11",business_no_11);
			paramMap.put("account_balance_30", "0");
			paramMap.put("pile_no_41", data.get("SN"));
			paramMap.put("ammeter_start_46", "0");
			paramMap.put("ammeter_end_47","0");
			paramMap.put("average_price_48", "0");
			
			paramMap.put("PLATFORM", 1);//合康
			paramMap.put("ret_status_39", "0");
			
			chargePileBillDao.addRecord(paramMap);
			}else{
				HKLogUtils.info("收到重复账单，丢弃;business_no_11=="+business_no_11);
			}
		}
	/**
	 * 智网账单保存
	 * @throws Exception 
	 */
	@Override
	public void saveZW(Map head,Map body,int body6_4) throws Exception {
		
	    double body6_4_dou = new Integer(body6_4).doubleValue()*105/100;        
	    int body3_4_elequantity = new BigDecimal(body6_4_dou).setScale(0,0).intValue();
	     
		String business_no_11 = body.get("body3_10").toString();
		int c=chargePileBillDao.countBill(business_no_11);
		
		if(c>0){
			ZWLogUtils.info("收到重复账单，丢弃;business_no_11=="+business_no_11);
			return;
		}
		
		Map map = chargeOrderDao.searchOrderDetailHk(business_no_11);
		
		Map paramMap = new HashMap();
		paramMap.put("MSG_TYPE", "0x54");
		paramMap.put("user_id_2", business_no_11);
		paramMap.put("status_3", "02");
		paramMap.put("charge_amount_4", body.get("body11_4"));
		paramMap.put("ele_quantity_5",body6_4 );
		paramMap.put("service_charge_6", body.get("body10_4"));//服务费（订单表查询）
		
		paramMap.put("amount_7", body.get("body11_4"));  

		paramMap.put("business_no_11",business_no_11);
		paramMap.put("account_balance_30", body.get("body8_4"));
		paramMap.put("pile_no_41", head.get("head7_8"));
		paramMap.put("ammeter_start_46", "0");
		paramMap.put("ammeter_end_47","0");
		paramMap.put("average_price_48", "0");
		
		paramMap.put("PLATFORM", 2);//智网
		paramMap.put("ret_status_39", "0");
		
		chargePileBillDao.addRecord(paramMap);
		
		
		
	}
		
}
