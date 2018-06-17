package com.hc.app.service;

import com.hc.app.dao.ChargePileDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChargePileServiceImpl implements ChargePileService {
  @Autowired
  private ChargePileDao chargePileDao;
	@Override
	public int countByPileNo(String pileNo) throws Exception {
		// TODO Auto-generated method stub
		return chargePileDao.countByPileNo(pileNo);
	}
	@Override
	public int countGun(String pileNo,String chargeStatus)throws Exception{
		// TODO Auto-generated method stub
		return chargePileDao.countGun(pileNo, chargeStatus);
	}
	@Override
	public void updateStatus(String pileNo, String status,String errFlag) throws Exception {
		// TODO Auto-generated method stub
		chargePileDao.updateStatus(pileNo, status,errFlag);
	}
	@Override
	public void updateStatus(String pileNo, String status) throws Exception {
		// TODO Auto-generated method stub
		chargePileDao.updateStatus(pileNo, status);
	}
	public void updateGunStatus(String pileNo,String chargeStatus,String status,String showStatus,int gunNo )throws Exception {
		chargePileDao.updateGunStatus(pileNo, chargeStatus, status,showStatus,gunNo);
	}
	public void updateGunStatus(String pileNo,String status)throws Exception {
		chargePileDao.updateGunStatus(pileNo,status);
	}
	@Override
	public Map findByPileNo(String pileNo) throws Exception {
		// TODO Auto-generated method stub
		return chargePileDao.findByPileNo(pileNo);
	}
	public Map findByPileNoAndGunNo(String pileNo,int gunNo) throws Exception{
		return chargePileDao.findByPileNoAndGunNo(pileNo, gunNo);
	}
	@Override
	public Map findByGunCode(String gunCode) throws Exception {
		// TODO Auto-generated method stub
		return chargePileDao.findByGunCode(gunCode);
	}
	
	@Override
	public String addHeartBeat(Map map) throws Exception {			
		return chargePileDao.addHeartBeat(map);
	}
	
	@Override
	public String addHk20(Map map) throws Exception {			
		return chargePileDao.addHk20(map);
	}
	
	@Override
	public String addHk5200(Map map) throws Exception {			
		return chargePileDao.addHk5200(map);
	}
	
	@Override
	public String addChargeStatus(Map map) throws Exception {			
		return chargePileDao.addChargeStatus(map);
	}
	@Override
	public List searchPileList() throws Exception {
		return chargePileDao.searchPileList();
	}
}
