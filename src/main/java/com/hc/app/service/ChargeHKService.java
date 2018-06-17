package com.hc.app.service;

import java.util.Map;

public interface ChargeHKService {
	public void save(Map paramMap) throws Exception;
	public Map findByMessageId(int messageId) throws Exception;
	public void update(int id, int status) throws Exception;
}
