package com.hc.app.dao;

import com.hc.common.database.BaseJdbcDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ChargePileDao extends BaseJdbcDao {
   public Map findByPileNo(String pileNo) throws Exception{
	   String sqlStr="select CHARGE_PILE_SOFTW_VERSION,CHARGE_PILE_POWER_MAX,CHARGE_PILE_POWER_AVG,FEE_VERSION,TARIFF_POLICY_ID from HC_CHARGE_PILE where CHARGE_PILE_SERI=?";
	   String[] args={pileNo};
	   return this.querySingleRow(sqlStr,args);
   }
   
   public int countByPileNo(String pileNo)throws Exception{
	   String sqlStr="select count(1) total from HC_CHARGE_PILE where CHARGE_PILE_SERI=? and CAN_USE!='1' ";
	   String[] args={pileNo};
	   return Integer.valueOf(this.querySingleRow(sqlStr,args).get("TOTAL").toString());
   }
   
   public void updateStatus(String pileNo,String status,String errFlag)throws Exception{
	   String sqlStr="update HC_CHARGE_PILE set PILE_STATE=?,ERR_FLAG=?,";
	   if("0".equals(errFlag)){
		   sqlStr+="is_noty='0',";
	   }
	   	   sqlStr+= "UPDATE_TIME=sysdate where CHARGE_PILE_SERI=?";
	   
	   String[] args={status,errFlag,pileNo};
	   this.update(sqlStr,args);
   }
   public void updateStatus(String pileNo,String status)throws Exception{
	   String sqlStr="update HC_CHARGE_PILE set PILE_STATE=?,ERR_FLAG='0',UPDATE_TIME=sysdate where CHARGE_PILE_SERI=?";
	   String[] args={status,pileNo};
	   this.update(sqlStr,args);
   }
   public void updateGunStatus(String pileNo,String chargeStatus,String status,String showStatus,int gunNo)throws Exception{
	   String sqlStr="update HC_CHARGE_GUN set CHARGE_STATUS=?,STATUS=?,show_status=?,UPDATE_TIME=sysdate where PILE_SERI=? and GUN_NO=?";
	   Object[] args={chargeStatus,status,showStatus,pileNo,gunNo};
	   this.update(sqlStr,args);
   }
   public void updateGunStatus(String pileNo,String status)throws Exception{
	   String sqlStr="update HC_CHARGE_GUN set show_status=?,UPDATE_TIME=sysdate where PILE_SERI=?";
	   Object[] args={status,pileNo};
	   this.update(sqlStr,args);
   }
   
   public int countGun(String pileNo,String chargeStatus)throws Exception{
	   String sqlStr="select count(1) total from HC_CHARGE_GUN where PILE_SERI=? AND CHARGE_STATUS=?";
	   String[] args={pileNo,chargeStatus};
	   return Integer.valueOf(this.querySingleRow(sqlStr,args).get("TOTAL").toString());
   }
   
   public Map findByPileNoAndGunNo(String pileNo,int gunNo) throws Exception{
	   String sqlStr="select GUN_NO,PILE_SERI,GUN_CODE from HC_CHARGE_GUN where PILE_SERI=? AND GUN_NO = ? ";
	   Object[] args={pileNo,gunNo};
	   return this.querySingleRow(sqlStr,args);
   }
   public Map findByGunCode(String gunCode) throws Exception{
	   String sqlStr="select PILE_SERI,GUN_NO,POLICY_ID from HC_CHARGE_GUN where gun_code=? ";
	   Object[] args={gunCode};
	   return this.querySingleRow(sqlStr,args);
   }
    
   public String addHeartBeat(Map map) throws Exception{
	   String heart_id=this.querySequence("HC_heartbeat_hk");
	   map.put("HEART_ID", heart_id);
	   String sqlStr="select ceil((sysdate-hk.create_time)*24*60*60) ctime from HC_heartbeat_hk hk where hk.sn=? order by hk.heart_id desc ";
       
	   Map mapPer = this.querySingleRow(sqlStr,new Object[]{map.get("SN")});

	   if(mapPer!=null){
		   int pre_cur = Integer.parseInt(mapPer.get("CTIME").toString());
		   //System.out.println(pre_cur);
		   if(pre_cur<30||pre_cur>200){
			   map.put("IS_SAME", 1);
		   }
		   map.put("IS_SAME_time", pre_cur);
	   }
	   //System.out.println(map.toString());
	   super.add("HC_HEARTBEAT_HK", map);
	   return heart_id;
   }
   
   public String addHk20(Map map) throws Exception{
	   String hk20_id=this.querySequence("HC_hk20");
	   map.put("HK20_ID", hk20_id);
	  
	   super.add("HC_Hk20", map);
	   return hk20_id;
   }
   
   public String addHk5200(Map map) throws Exception{
	   String hk5200_id=this.querySequence("HC_hk5200");
	   map.put("HK5200_ID", hk5200_id);
	   
	   super.add("HC_Hk5200", map);
	   return hk5200_id;
   }
   
   public String addChargeStatus(Map map) throws Exception{
	   String hk5700_id=this.querySequence("HC_hk5700");
	   map.put("HK5700_ID", hk5700_id);
	  
	   super.add("HC_HK5700", map);
	   return hk5700_id;
   }

	public List searchPileList() throws Exception {
		String sqlStr="select * from HC_charge_pile dp where dp.pile_state='00'";		
		return this.query(sqlStr,null);
	}
   
}
