package com.hc.app.service;

import java.util.Map;

public interface ZWService {
	  public String addHead(Map<String, String> map);
	  public String addBody0xB0(Map<String, String> map);
	  public String addBody0xA1(Map<String, String> map);
	  
	  public String addBody0x51(Map<String, String> map);
	  public String addBody0x54(Map<String, String> map);
	  public String addBody0x5A(Map<String, String> map);
	  public String addBody0x11(Map<String, String> map);
	  public String addBody0x21(Map<String, String> map);
	  public String addBody0x20(Map<String, String> map);
	  public String addBody0x22(Map<String, String> map);
	  
	  public String addBody0x23(Map<String, String> map);
	  public String addBody0x13(Map<String, String> map);
	  public String addBody0x58(Map<String, String> map);
	  public Map queryOrderIfExist(String charge_order_id);
}