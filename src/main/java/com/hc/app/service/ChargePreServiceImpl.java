package com.hc.app.service;

import com.hc.app.dao.ChargePreDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChargePreServiceImpl implements ChargePreService{
    @Autowired
    private ChargePreDao chargePreDao;
	@Override
	public Map searchOrderDetail(String charge_order_id) throws Exception {
		
		return chargePreDao.searchOrderDetail(charge_order_id);
	}

}
