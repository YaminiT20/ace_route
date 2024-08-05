package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class PubnubRequest extends RequestObject implements Parcelable {
	
	
	String objtypeforpn;
	String xml;
	
	
	public String getObjtypeforpn() {
		return objtypeforpn;
	}
	public void setObjtypeforpn(String objtypeforpn) {
		this.objtypeforpn = objtypeforpn;
	}
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		 parcel.writeString(objtypeforpn);
		 parcel.writeString(xml); 
	}
	
	public static final Creator<PubnubRequest> CREATOR = new Creator<PubnubRequest>() {
		 public PubnubRequest createFromParcel(Parcel source) {
			 PubnubRequest pubnub = new PubnubRequest();  
			 pubnub.objtypeforpn = source.readString();
			 pubnub.xml = source.readString(); 
		     return pubnub;  
		 }

		@Override
		public PubnubRequest[] newArray(int size) {
			return new PubnubRequest[size];  
		}
	}; 	 
}



