package com.hc.app.dao;

import com.hc.common.database.BaseJdbcDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ChargeHKDao extends BaseJdbcDao {
	
public void addRecord(Map paramMap) throws Exception{
		
		Map filedValueMap = new HashMap();
		filedValueMap.putAll(paramMap);
		filedValueMap.put("id",Integer.valueOf(this.querySequence("HC_CHARGE_HK_ID")));
		
		super.add("HC_CHARGE_HK", filedValueMap);
	}
public Map findByMessageId(int messageId) throws Exception{
	 String sql="select * from HC_charge_hk where message_id=?";
	 Integer[] args={messageId};
	 
	 return super.querySingleRow(sql,args);
}

public void update(int id,int status) throws Exception{
	
	String sql="update HC_charge_hk set status=?,update_time=sysdate where id=?";
	Integer[] args={status,id};
	
	super.update(sql,args);
 }

public int findMax() throws NumberFormatException, Exception{
	String sql="select  NVL(max(message_id),1) ma from HC_charge_hk";
	return Integer.valueOf((super.querySingleRow(sql,null)).get("MA").toString());
}

}
