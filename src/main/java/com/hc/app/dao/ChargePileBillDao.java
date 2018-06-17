package com.hc.app.dao;

import com.hc.common.database.BaseJdbcDao;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class ChargePileBillDao extends BaseJdbcDao {
	public void addRecord(Map paramMap) throws Exception{		
		super.add("HC_charge_pile_bill", paramMap);
	}
	
	public int countBill(String orderSeril) throws Exception{
		String sql="select count(1) total from HC_charge_pile_bill where BUSINESS_NO_11 = ?";
	    Map m=super.querySingleRow(sql,new String[]{orderSeril});
	    return Integer.valueOf(m.get("TOTAL").toString());
	}
}
