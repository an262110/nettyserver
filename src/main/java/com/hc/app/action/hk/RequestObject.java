package com.hc.app.action.hk;

import java.util.Map;

public class RequestObject {
   private String txcode="";
   private Map controlHeader;
   private Map header;
   private Map data;
   private String orderId;
   
public String getOrderId() {
	return orderId;
}
public void setOrderId(String orderId) {
	this.orderId = orderId;
}
public String getTxcode() {
	return txcode;
}
public void setTxcode(String txcode) {
	this.txcode = txcode;
}
public Map getControlHeader() {
	return controlHeader;
}
public void setControlHeader(Map controlHeader) {
	this.controlHeader = controlHeader;
}
public Map getHeader() {
	return header;
}
public void setHeader(Map header) {
	this.header = header;
}
public Map getData() {
	return data;
}
public void setData(Map data) {
	this.data = data;
}
   
}
