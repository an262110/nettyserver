package com.hc.app.service;

import com.hc.app.dao.ZwStopChargeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ZwStopChargeServiceImpl implements ZWStopChargeService {
	@Autowired
	private ZwStopChargeDao zwStopChargeDao;
	@Override
	public String getPile(String sn) throws Exception {
		// TODO Auto-generated method stub
		return zwStopChargeDao.getPile(sn);
	}

	@Override
	public String findPileNum(String sn) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
