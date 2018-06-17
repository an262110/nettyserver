package com.hc.app.service;

import java.util.Map;

public interface AccountInfoService {
	public Map findByUserId(String userId) throws Exception;
	public void update(String userId, String status) throws Exception;
}
