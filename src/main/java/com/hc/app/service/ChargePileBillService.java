package com.hc.app.service;

import org.jpos.iso.ISOMsg;

import java.util.Map;

public interface ChargePileBillService {
  public void save(ISOMsg resq, ISOMsg resp) throws Exception;
  public void savehk(Map data) throws Exception;
  public void saveZW(Map head, Map body, int body6_4) throws Exception;
}
