package com.aceroute.mobile.software.component;

public class OrdersMessage {
	public final static String MESSAGE_ID = "id";
	public final static String MESSAGE_TYPE_ID = "tid";
	public final static String MESSAGE_ORDER_ID = "oid";
	public final static String MESSAGE_CUST_ID = "cid";
	public final static String MESSAGE_CONTACT_ID = "cntid";
	public final static String MESSAGE_TELL = "tel";
	public final static String MESSAGE_TIMEZONE = "tz";
	public final static String MESSAGE_GEO = "geo";
	public final static String MESSAGE_TIMESTAMP = "stmp";
	
	public final static String ACTION_MESSAGE_SAVE = "sendmsg";
	public final static int TYPE_SAVE = 151;
	
	String action;
	long tid;
	long oid;
	long cid;
	long cntid;
	long tel;
	String tz;
	String geo;
	String stmp;
	
	public String getStmp() {
		return stmp;
	}
	public void setStmp(String stmp) {
		this.stmp = stmp;
	}
	private String modified = "";

	public String getModified() {
		return modified;
	}
	public void setModified(String modified) {
		this.modified = modified;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public long getTid() {
		return tid;
	}
	public void setTid(long tid) {
		this.tid = tid;
	}
	public long getOid() {
		return oid;
	}
	public void setOid(long oid) {
		this.oid = oid;
	}
	public long getCid() {
		return cid;
	}
	public void setCid(long cid) {
		this.cid = cid;
	}
	public long getCntid() {
		return cntid;
	}
	public void setCntid(long cntid) {
		this.cntid = cntid;
	}
	public long getTel() {
		return tel;
	}
	public void setTel(long tel) {
		this.tel = tel;
	}
	public String getTz() {
		return tz;
	}
	public void setTz(String tz) {
		this.tz = tz;
	}
	public String getGeo() {
		return geo;
	}
	public void setGeo(String geo) {
		this.geo = geo;
	}
}
