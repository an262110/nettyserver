package com.hc.app.service;

import java.util.List;
import java.util.Map;

public interface ChargePileService {
	  public int countByPileNo(String pileNo) throws Exception;
	  public Map findByPileNoAndGunNo(String pileNo, int gunNo) throws Exception;
	  public Map findByGunCode(String gunCode) throws Exception;
	  public int countGun(String pileNo, String chargeStatus)throws Exception;
	  public void updateStatus(String pileNo, String status, String errFlag) throws Exception;
	  public void updateStatus(String pileNo, String status) throws Exception;
	  public Map findByPileNo(String pileNo) throws Exception;
	  public void updateGunStatus(String pileNo, String chargeStatus, String status, String showStatus, int gunNo)throws Exception;
	  public void updateGunStatus(String pileNo, String status)throws Exception;
	  public String addHeartBeat(Map map) throws Exception;
	  
	  public String addHk20(Map map) throws Exception;
	  public String addHk5200(Map map) throws Exception;
	  public String addChargeStatus(Map map) throws Exception;
	  
	  public List searchPileList() throws Exception;
}