package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class ClockInOutRequest extends RequestObject implements Parcelable {

	public ClockInOutRequest() {
	 }
	
	private ClockInOutRequest(Parcel in) {
		action = in.readString();
	 }
	
	String tid;
	String type;
	
	
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(action);
		 parcel.writeString(tid);  
		 parcel.writeString(type);
	}
	
	public static final Creator<ClockInOutRequest> CREATOR = new Creator<ClockInOutRequest>() {
		 public ClockInOutRequest createFromParcel(Parcel source) {
			 ClockInOutRequest clkInOut = new ClockInOutRequest(source);  
			 clkInOut.tid = source.readString();  
			 clkInOut.type = source.readString();
		     return clkInOut;  
		 }

		@Override
		public ClockInOutRequest[] newArray(int size) {
			return new ClockInOutRequest[size];  
		}
	}; 	 
}
