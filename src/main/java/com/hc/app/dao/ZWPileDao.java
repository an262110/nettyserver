package com.hc.app.dao;

import com.hc.common.database.BaseJdbcDao;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class ZWPileDao extends BaseJdbcDao {
	 public String addHead(Map<String, String> map) throws Exception{
		   //String hk20_id=this.querySequence("HC_hk20");
		   //map.put("HK20_ID", hk20_id);
		  
		   super.add("HC_HEAD", map);
		   return "suc";
	   }
   
	 public String addBody0xB0(Map<String, String> map) throws Exception{
		   //String hk20_id=this.querySequence("HC_hk20");
		   //map.put("HK20_ID", hk20_id);
		  
		   super.add("HC_BODY0XB1", map);
		   return "suc";
	   }
	 public String addBody0xA1(Map<String, String> map) throws Exception{
		  
		   super.add("HC_BODY0xA1", map);
		   return "suc";
	   }
	 public String addBody0x51(Map<String, String> map) throws Exception{
		  
		   super.add("HC_BODY0x51", map);
		   return "suc";
	   }
	 public String addBody0x54(Map<String, String> map) throws Exception{
		  
		   super.add("HC_BODY0x54", map);
		   return "suc";
	   }
	 
	 public String addBody0x5A(Map<String, String> map) throws Exception{
		  
		   super.add("HC_BODY0x5A", map);
		   return "suc";
	   }
	 public String addBody0x11(Map<String, String> map) throws Exception{
		  
		   super.add("HC_BODY0x11", map);
		   return "suc";
	   }
	 public String addBody0x21(Map<String, String> map) throws Exception{
		  
		   super.add("HC_BODY0x21", map);
		   return "suc";
	   }
	 
	 public String addBody0x20(Map<String, String> map) throws Exception{
		  
		   super.add("HC_BODY0x20", map);
		   return "suc";
	   }
	 public String addBody0x22(Map<String, String> map) throws Exception{
		  
		   super.add("HC_BODY0x22", map);
		   return "suc";
	   }
	 public String addBody0x23(Map<String, String> map) throws Exception{
		  
		   super.add("HC_BODY0x23", map);
		   return "suc";
	   }
	 public String addBody0x13(Map<String, String> map) throws Exception{
		  
		   super.add("HC_BODY0x13", map);
		   return "suc";
	   }
	 public String addBody0x58(Map<String,String> map) throws Exception{
		 super.add("HC_BODY0X58", map);
		 return "suc";
	 }
	 public Map queryOrderIfExist(String charge_order_id) throws Exception{
		 String sql="select * from HC_charge_order where charge_order_id=?";
		 return super.querySingleRow(sql, new String[]{charge_order_id});
	 }
}
