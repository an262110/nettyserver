package com.hc.app.dao;

import com.hc.common.database.BaseJdbcDao;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DemoDao extends BaseJdbcDao {

	public void addRecord(Map paramMap) throws Exception{
		
		Map filedValueMap = new HashMap();
		filedValueMap.putAll(paramMap);
		
		super.add("demo_table_name", filedValueMap);
	}
	
	public List queryDataList() throws Exception {
		
		String sql = "";
		
		List paramList = new ArrayList();
		paramList.add("");
		
		return super.query(sql, paramList.toArray());
	}
	
}
