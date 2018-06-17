package com.hc.app.dao;

import com.hc.common.database.BaseJdbcDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TariffPolicyDao extends BaseJdbcDao {
   public Map findTariffPolicy(String policyId) throws Exception{
	   String sql="select TARIFF_POLICY_ID,CHARGE_SERVICE_FEE,SERVICE_FEE_METHOD,COMMON_ELEC_PRICE,VERSION,MAX_PRICE,HIGH_PRICE,AVG_PRICE,LOW_PRICE "
	   		+ " from HC_TARIFF_POLICY where TARIFF_POLICY_ID=?";
	   String[] args={policyId};
	   return this.querySingleRow(sql, args);
   }
   
   public List findDivisionPrice(String policyId) throws Exception{
	   String sql="select DIVISION_START_TIME,DIVISION_END_TIME,DIVISION_TYPE from "
	   		+ " HC_DIVISION_TIME d "
	   		+ "where d.TARIFF_POLICY_ID=? order by d.SORT";
	   String[] args={policyId};
	   return this.query(sql, args);
   }
}
