package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class CommonSevenReq extends RequestObject implements Parcelable {

	
	String url;
	String source;
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(url);  
		 parcel.writeString(source); 
		
	}
	
	public static final Creator<CommonSevenReq> CREATOR = new Creator<CommonSevenReq>() {
		 public CommonSevenReq createFromParcel(Parcel source) {
			 CommonSevenReq custLst = new CommonSevenReq();  
			 custLst.url = source.readString();  
			 custLst.source = source.readString();  
		     return custLst;  
		 }

		@Override
		public CommonSevenReq[] newArray(int size) {
			return new CommonSevenReq[size];  
		}
	}; 	 
}
