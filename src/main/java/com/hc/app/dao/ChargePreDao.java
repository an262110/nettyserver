package com.hc.app.dao;

import com.hc.common.database.BaseJdbcDao;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class ChargePreDao extends BaseJdbcDao {
   
   public Map searchOrderDetail(String charge_order_id) throws Exception{
	   String sqlStr="select * from HC_charge_order co where co.charge_order_id = ? ";
	   return this.querySingleRow(sqlStr,new String[]{charge_order_id});
	  
   } 
   
}
