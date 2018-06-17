package com.hc.app.dao;

import com.hc.common.database.BaseJdbcDao;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class JtDao extends BaseJdbcDao {
     public void addLogin(Map map)throws Exception{
    	 add("HC_JT_LOGIN",map);
     }
     
     public void addRegister(Map map)throws Exception{
    	 add("HC_JT_REGISTER", map);
     }
     
     public void addState(Map map)throws Exception{
    	 add("HC_JT_STATE",map);
     } 
     
     public void addAccounts(Map map)throws Exception{
    	 add("HC_JT_CHARGE_ACCOUNTS",map);
     }
     
     public void addEvent(Map map)throws Exception{
    	 add("HC_JT_EVENT",map);
     }
     
     public void addWorkDate(Map map)throws Exception{
    	 add("HC_JT_WORK_DATA",map);
     }
     
     public String findSql()throws Exception{
    	 String sequence = querySequence("HC_jt_state");
    	 return sequence;
     }
}
