package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class GetSiteRequest extends RequestObject implements Parcelable {

	String url;
	String cid;
	String source;
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
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
		parcel.writeString(cid);
		 parcel.writeString(source); 
		
	}
	
	public static final Creator<GetSiteRequest> CREATOR = new Creator<GetSiteRequest>() {
		 public GetSiteRequest createFromParcel(Parcel source) {
			 GetSiteRequest siteLst = new GetSiteRequest();  
			 siteLst.url = source.readString();  
			 siteLst.cid = source.readString();
			 siteLst.source = source.readString();
		     return siteLst;  
		 }

		@Override
		public GetSiteRequest[] newArray(int size) {
			return new GetSiteRequest[size];  
		}
	}; 	 
	
	
}
