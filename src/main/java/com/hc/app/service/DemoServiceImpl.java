package com.hc.app.service;

import com.hc.app.dao.DemoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DemoServiceImpl implements DemoService {

	@Autowired
	private DemoDao demoDao;
    
	public void addRecord(Map paramMap) throws Exception{
		
		demoDao.addRecord(paramMap);
	}
	
	public List queryDataList() throws Exception {
		
		return demoDao.queryDataList();
	}
	
}
