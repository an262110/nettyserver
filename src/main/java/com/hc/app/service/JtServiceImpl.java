package com.hc.app.service;

import com.hc.app.dao.JtDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JtServiceImpl implements JtService {
    @Autowired
    private JtDao jtDao;
	@Override
	public void addLogin(Map map) throws Exception {
		jtDao.addLogin(map);
	}

	@Override
	public void addRegister(Map map) throws Exception {
		jtDao.addRegister(map);
	}

	@Override
	public void addState(Map map) throws Exception {
		jtDao.addState(map);
	}

	@Override
	public void addWorkDate(Map map) throws Exception {
		jtDao.addWorkDate(map);
	}

	@Override
	public void addEvent(Map map) throws Exception {
		jtDao.addEvent(map);
	}

	@Override
	public void addAccounts(Map map) throws Exception {
		jtDao.addAccounts(map);
	}

	@Override
	public String findSeq() throws Exception {
		return jtDao.findSql();
	}

}
