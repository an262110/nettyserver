package com.hc.app.service;

import com.hc.app.dao.ChargeOrderDao;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChargeOrderServiceImpl implements ChargeOrderService {
    @Autowired
    private ChargeOrderDao chargeOrderDao;
    
    public Map findByOrderSeril(String orderSeril) throws Exception{
    	return chargeOrderDao.findByOrderSeril(orderSeril);
    }
    public Map findByOrderId(String orderId) throws Exception{
    	return chargeOrderDao.findByOrderId(orderId);
    }
	@Override
	public void updateInfo(Map params) throws Exception {
		
		chargeOrderDao.updateInfo(params);
	}
	
	@Override
	public void updateInfoHk(Map params) throws Exception {
		
		chargeOrderDao.updateInfoHk(params);
	}

	@Override
	public Map findLatest(String pileNo) throws Exception {
		
		return chargeOrderDao.findLatest(pileNo);
	}

	@Override
	public Map searchOrderDetail(String business_no,String status) throws Exception {
		
		return chargeOrderDao.searchOrderDetail(business_no,status);
	}

	@Override
	public String obtainUserSeq(String dn_charge_userid) throws Exception {
		return chargeOrderDao.obtainUserSeq(dn_charge_userid);
	}

	@Override
	public String addDnCharge(ISOMsg sendMsg) throws Exception {
		Map paramMap = new HashMap();
		//paramMap.put("CHARGE_ID", );
		paramMap.put("CHARGE_TYPE_4", sendMsg.getString(4));
		paramMap.put("CHARGE_MOUTH_5", sendMsg.getString(5));
		paramMap.put("CHARGE_CONTROL_6", sendMsg.getString(6));
		paramMap.put("CHARGE_LIMIT_7", sendMsg.getString(7));
		paramMap.put("CHARGE_TIMING_8", sendMsg.getString(8));
		paramMap.put("CHARGE_USERID_9", sendMsg.getString(9));
		paramMap.put("CHARGE_BALANCE_10", sendMsg.getString(10));
		paramMap.put("CHARGE_BUSINESSNO_11", sendMsg.getString(11));
		paramMap.put("CHARGE_PILENO_41", sendMsg.getString(41));
		paramMap.put("CHARGE_MAC_63", sendMsg.getString(63));

		Map map = chargeOrderDao.searchHCChargeDetail(sendMsg.getString(9));
		if(map!=null){
			return sendMsg.getString(9).toString();
		}
		return  chargeOrderDao.addHCCharge(paramMap);
	}

	@Override
	public Map searchDnChargeDetail(String charge_userid) throws Exception {
		return chargeOrderDao.searchHCChargeDetail(charge_userid);
	}

	@Override
	public String updateDnCharge(ISOMsg sendMsg) throws Exception {
		java.util.Date utilDate = new java.util.Date();
//		java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		
		Map paramMap = new HashMap();			
		paramMap.put("CHARGE_RET_39", sendMsg.getString(39));	
		paramMap.put("CHARGE_USERID_9", sendMsg.getString(9));	
		paramMap.put("CHARGE_RET_USERID_9", sendMsg.getString(9));
		paramMap.put("CHARGE_RET_PILENO_41", sendMsg.getString(41));
		paramMap.put("CHARGE_RET_PILENO_63", sendMsg.getString(63));
		//paramMap.put("CHARGE_TYPE_4",sendMsg.getString(4));
		//paramMap.put("UPDATE_TIME", sqlDate);
		return chargeOrderDao.updateHCCharge(paramMap);
	}

	@Override
	public Map findLatestByGunCode(String gunCode) throws Exception {
		// TODO Auto-generated method stub
		return chargeOrderDao.findLatestByGunCode(gunCode);
	}
	@Override
	public void updateDnChargeType(String userFlagNo, String type) throws Exception {
		// TODO Auto-generated method stub
		chargeOrderDao.updateHCChargeType(userFlagNo, type);
	}
	@Override
	public void singleCheckBill(String orderId) throws Exception {
		chargeOrderDao.singleCheckBill(orderId);		
	}
	@Override
	public Map findByOrderStatus(String pileno_zw, int gun_no,
			String order_status) throws Exception {
		
		return chargeOrderDao.findByOrderStatus(pileno_zw, gun_no,order_status);
	}


}
