package com.aceroute.mobile.software.component.reference;

import java.util.List;

public class OfflineCountry_City {
	
	String name;
	String code;
	String parentCode;
	String type;
	List<String> children;
	long size;
	boolean isDownloaded;
	
	
	public boolean isDownloaded() {
		return isDownloaded;
	}
	public void setDownloaded(boolean isDownloaded) {
		this.isDownloaded = isDownloaded;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<String> getChildren() {
		return children;
	}
	public void setChildren(List<String> children) {
		this.children = children;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}

}
