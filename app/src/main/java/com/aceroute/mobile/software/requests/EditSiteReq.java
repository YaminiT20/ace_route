package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class EditSiteReq extends RequestObject implements Parcelable {

	String action;
	long id;
	String name;
	String adr;

	String geo;// using in mapfragment for drag
	String isOnlyUpdate = "false";

	String adr2;
	String description;

	public String getIsOnlyUpdate() {
		return isOnlyUpdate;
	}

	public void setIsOnlyUpdate(String isOnlyUpdate) {
		this.isOnlyUpdate = isOnlyUpdate;
	}

	public String getGeo() {
		return geo;
	}

	public void setGeo(String geo) {
		this.geo = geo;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	public String getAdr2() {
		return adr2;
	}

	public void setAdr2(String adr2) {
		this.adr2 = adr2;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAdr() {
		return adr;
	}
	public void setAdr(String adr) {
		this.adr = adr;
	}
	
	public String getDesc() {
		return description;
	}
	public void setDesc(String desc) {
		this.description = desc;
	}	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(action);  
		parcel.writeLong(id);
		parcel.writeString(name); 
		parcel.writeString(adr);
		parcel.writeString(adr2);
		parcel.writeString(description);
		parcel.writeString(geo);
		parcel.writeString(isOnlyUpdate);
	}
	
	public static final Creator<EditSiteReq> CREATOR = new Creator<EditSiteReq>() {
		 public EditSiteReq createFromParcel(Parcel source) {
			 EditSiteReq edtContact = new EditSiteReq();  
			 edtContact.action = source.readString();  
			 edtContact.id = source.readLong();
			 edtContact.name = source.readString();  
			 edtContact.adr = source.readString();
			 edtContact.adr2 = source.readString();
			 edtContact.description = source.readString();
			 edtContact.geo = source.readString();
			 edtContact.isOnlyUpdate = source.readString();
		     return edtContact;  
		 }

		@Override
		public EditSiteReq[] newArray(int size) {
			return new EditSiteReq[size];  
		}
	}; 	 
}
