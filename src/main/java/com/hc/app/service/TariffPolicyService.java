package com.hc.app.service;

import java.util.List;
import java.util.Map;

public interface TariffPolicyService {
  public Map findTariffPolicy(String policyId) throws Exception;
  public List findDivisionPrice(String policyId) throws Exception;
}
