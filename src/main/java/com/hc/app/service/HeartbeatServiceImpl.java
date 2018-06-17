package com.hc.app.service;

import com.hc.app.dao.HeartbeatDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class HeartbeatServiceImpl implements HeartbeatService {
    
	@Autowired
	private HeartbeatDao heartbeatDao;
	@Override
	public void save(Map params) throws Exception {
		// TODO Auto-generated method stub
		 heartbeatDao.addRecord(params);
	}

}
