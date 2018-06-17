package com.hc.app.service;

import java.util.Map;

public interface ChargePreService {

  //根据订单号，获取订单详情
  public Map searchOrderDetail(String charge_order_id) throws Exception;
}
