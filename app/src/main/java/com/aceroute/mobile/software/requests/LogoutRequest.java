package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class LogoutRequest extends RequestObject implements Parcelable {

	String id;
	String objtypeforpn;
	String x_element;
	long appexit;
	
	public long getAppexit() {
		return appexit;
	}
	public void setAppexit(long appexit) {
		this.appexit = appexit;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getObjtypeforpn() {
		return objtypeforpn;
	}
	public void setObjtypeforpn(String objtypeforpn) {
		this.objtypeforpn = objtypeforpn;
	}
	
	public String getX_element() {
		return x_element;
	}
	public void setX_element(String x_element) {
		this.x_element = x_element;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		 parcel.writeString(id);
		 parcel.writeString(objtypeforpn); 
		 parcel.writeString(x_element);
		 parcel.writeLong(appexit); 
	}
	
	
	public static final Creator<LogoutRequest> CREATOR = new Creator<LogoutRequest>() {
		 public LogoutRequest createFromParcel(Parcel source) {
			 LogoutRequest pubnub = new LogoutRequest();  
			 pubnub.id = source.readString();
			 pubnub.objtypeforpn = source.readString(); 
			 pubnub.x_element = source.readString();
			 pubnub.appexit = source.readLong(); 
		     return pubnub;  
		 }

		@Override
		public LogoutRequest[] newArray(int size) {
			return new LogoutRequest[size];  
		}
	}; 	 
}
