package com.aceroute.mobile.software.http;

import java.util.ArrayList;

public class Response {

	
	int id;
	String Status;
	ArrayList<Object> responseObj;
	Object responseMap;
	String resString;
	String errorcode;
	boolean isSyncToServer = false;
	
	public boolean getSyncToServer() {
		return isSyncToServer;
	}
	public void setSyncToServer(boolean isSyncToServer) {
		this.isSyncToServer = isSyncToServer;
	}
	public String getResString() {
		return resString;
	}
	public void setResString(String resString) {
		this.resString = resString;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ArrayList<Object> getResponseObj() {
		return responseObj;
	}
	public void setResponseObj(ArrayList<Object> responseObj) {
		this.responseObj = responseObj;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String resString) {
		this.Status = resString;
	}
	public String getErrorcode() {
		return errorcode;
	}
	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}
	public Object getResponseMap() {
		return responseMap;
	}
	public void setResponseMap(Object responseMap) {
		this.responseMap = responseMap;
	}
}
