package com.hc.app.service;

import com.hc.app.dao.ChargeHKDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChargeHKServiceImpl implements ChargeHKService {
    @Autowired
    private ChargeHKDao chargeHKDao;
	@Override
	public void save(Map paramMap) throws Exception {
		// TODO Auto-generated method stub
		chargeHKDao.addRecord(paramMap);
	}

	@Override
	public Map findByMessageId(int messageId) throws Exception {
		// TODO Auto-generated method stub
		return chargeHKDao.findByMessageId(messageId);
	}

	@Override
	public void update(int id, int status) throws Exception {
		// TODO Auto-generated method stub
		chargeHKDao.update(id, status);
	}

}
