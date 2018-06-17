package com.hc.app.service;

import com.hc.app.dao.TariffPolicyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TariffPolicyServiceImpl implements TariffPolicyService {
    @Autowired
    private TariffPolicyDao tariffPolicyDao;
	@Override
	public Map findTariffPolicy(String policyId) throws Exception {
		// TODO Auto-generated method stub
		return tariffPolicyDao.findTariffPolicy(policyId);
	}

	@Override
	public List findDivisionPrice(String policyId) throws Exception {
		// TODO Auto-generated method stub
		return tariffPolicyDao.findDivisionPrice(policyId);
	}

}
