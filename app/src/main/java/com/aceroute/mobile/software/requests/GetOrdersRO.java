package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class GetOrdersRO extends RequestObject implements Parcelable {

	
	String url;
	String tz;
	String from;
	String to;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getTz() {
		return tz;
	}
	public void setTz(String tz) {
		this.tz = tz;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		 parcel.writeString(url);  
		 parcel.writeString(tz); 
		 parcel.writeString(to); 
		 parcel.writeString(from); 
		
	}
	
	public static final Creator<GetOrdersRO> CREATOR = new Creator<GetOrdersRO>() {
		 public GetOrdersRO createFromParcel(Parcel source) {
			 GetOrdersRO odrLst = new GetOrdersRO();  
			 odrLst.url = source.readString();  
			 odrLst.tz = source.readString();
			 odrLst.to = source.readString();
			 odrLst.from = source.readString();
		     return odrLst;  
		 }

		@Override
		public GetOrdersRO[] newArray(int size) {
			return new GetOrdersRO[size];  
		}
	}; 	 
}
