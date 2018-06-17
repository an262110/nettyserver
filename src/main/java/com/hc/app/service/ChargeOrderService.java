package com.hc.app.service;

import org.jpos.iso.ISOMsg;

import java.util.Map;

public interface ChargeOrderService {
	public Map findByOrderSeril(String orderSeril) throws Exception;
	 public Map findByOrderId(String orderId) throws Exception;
  public void updateInfo(Map params) throws Exception;
  public void updateInfoHk(Map params) throws Exception;
  public void updateDnChargeType(String userFlagNo, String type) throws Exception;

  public Map findLatest(String pileNo) throws Exception;
  public Map findLatestByGunCode(String gunCode)throws Exception;

  //根据订单号，获取订单详情
  public Map searchOrderDetail(String business_no, String status) throws Exception;
  //获取用户标识号(序列号)
  public String obtainUserSeq(String dn_charge_userid) throws Exception;
  //添加充电表
  public String addDnCharge(ISOMsg sendMsg) throws Exception;
  //更新充电表
  public String updateDnCharge(ISOMsg sendMsg) throws Exception;
  //根据用户标识号，查询充电表
  public Map searchDnChargeDetail(String charge_userid) throws Exception;
//  //更新订单状态
//  public void updateDnChargeOrder(String business_no, String order_status);
  //单笔结算 用户标识号
  public void singleCheckBill(String orderId) throws Exception;

  public Map findByOrderStatus(String pileno_zw, int gun_no, String order_status) throws Exception;
}
