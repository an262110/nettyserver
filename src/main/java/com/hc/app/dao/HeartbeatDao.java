package com.hc.app.dao;

import com.hc.common.database.BaseJdbcDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class HeartbeatDao extends BaseJdbcDao {
	 public void addRecord(Map paramMap) throws Exception{
			
			Map filedValueMap = new HashMap();
			filedValueMap.putAll(paramMap);
			filedValueMap.put("PILE_HEARTBEAT_ID",Integer.valueOf(this.querySequence("HC_HEARTBEAT")));
			
			super.add("HC_HEARTBEAT", filedValueMap);
		}
}
