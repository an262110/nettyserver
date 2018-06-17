package com.hc.app.service;

import com.hc.app.dao.AccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AccountInfoServiceImpl implements AccountInfoService{
  @Autowired
  private AccountDao accountDao;
	@Override
	public Map findByUserId(String userId) throws Exception {
		// TODO Auto-generated method stub
		return accountDao.findByUserId(userId);
	}
	@Override
	public void update(String userId, String status) throws Exception {
		// TODO Auto-generated method stub
		accountDao.updateStatus(userId, status);
	}

}
