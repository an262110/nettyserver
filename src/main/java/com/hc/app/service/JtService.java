package com.hc.app.service;

import java.util.Map;

public interface JtService {
  public void addLogin(Map map)throws Exception;
  
  public void addRegister(Map map)throws Exception;
  
  public void addState(Map map)throws Exception;
  
  public void addWorkDate(Map map)throws Exception;
  
  public void addEvent(Map map)throws Exception;
  
  public void addAccounts(Map map)throws Exception;
  
  public String findSeq()throws Exception;
  
  
}
