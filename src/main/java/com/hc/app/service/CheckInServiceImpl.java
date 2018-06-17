package com.hc.app.service;

import com.hc.app.dao.CheckInDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CheckInServiceImpl implements CheckInService {
    
	@Autowired
	private CheckInDao checkInDao;
	@Override
	public void save(Map params) throws Exception {
		// TODO Auto-generated method stub
		checkInDao.addRecord(params);
	}
   
}
