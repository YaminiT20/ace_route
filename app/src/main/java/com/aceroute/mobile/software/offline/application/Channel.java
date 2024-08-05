package com.aceroute.mobile.software.offline.application;

public class Channel {
	private int cid;
	private String orderId;
	private String city_name;
	private String adressid;
	private Double map_lat;
	private Double map_log;
	private String O_name;
	private String O_time;
	private String sequenceNum;
	private String setFocus;
	private String setOrderStat;
	private String taskId;
	private String customer_Name;
	private String odrStartDt;

	public String getOdrStartDt() {
		return odrStartDt;
	}

	public void setOdrStartDt(String odrStartDt) {
		this.odrStartDt = odrStartDt;
	}


	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public String getCity_name() {
		return city_name;
	}
	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}
	public Double getMap_lat() {
		return map_lat;
	}
	public void setMap_lat(Double map_lat) {
		this.map_lat = map_lat;
	}
	public Double getMap_log() {
		return map_log;
	}
	public void setMap_log(Double map_log) {
		this.map_log = map_log;
	}
	public String getAdressid() {
		return adressid;
	}
	public void setAdressid(String adressid) {
		this.adressid = adressid;
	}
	public String getO_nameid() {
		return O_name;
	}
	public void setO_nameid(String O_name) {
		this.O_name = O_name;
	}
	public String getO_timeid() {
		return O_time;
	}
	public void setO_timeid(String O_time) {
		this.O_time = O_time;
	}
	public String getSequenceNo() {
		return sequenceNum;
	}
	public void setSequenceNo(String seqNum) {
		this.sequenceNum = seqNum;
	}
	public String getFocusOnMap() {
		return setFocus;
	}
	public void setFocusOnMap(String focus) {
		this.setFocus = focus;
	}
	public void setOrderStatus(String oderStat) {
		setOrderStat=oderStat;
		
	}
	public String getOrderStatus() {
		return setOrderStat;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public void setCustomer(String customerName){
		this.customer_Name = customerName;
	}
	public String getCustomer(){
		return customer_Name;
	}
	
	
}
