package com.hc.app.dao;

import com.hc.common.database.BaseJdbcDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class CheckInDao extends BaseJdbcDao {
  public void addRecord(Map paramMap) throws Exception{
		
		Map filedValueMap = new HashMap();
		filedValueMap.putAll(paramMap);
		filedValueMap.put("id",Integer.valueOf(this.querySequence("HC_check_in")));
		
		super.add("HC_check_in", filedValueMap);
	}
  
}
