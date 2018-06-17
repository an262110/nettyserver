package com.hc.app.dao;

import com.hc.common.database.BaseJdbcDao;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class AccountDao extends BaseJdbcDao {
  
	public Map findByUserId(String userId) throws Exception{
		String sql="select * from HC_ACCT_INFO where USER_ID=? ";
		String[] args={userId};
		return super.querySingleRow(sql,args);
	}
	
	public void updateStatus(String userId,String status) throws Exception{
		String sql="update HC_ACCT_INFO set STATUS=? where USER_ID=? ";
		String[] args={status,userId};
		super.update(sql,args);
	}
	
}
