package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;
//REmove Tasks from the App (Ashish 04042019)

public class GetPart_Task_FormRequest extends RequestObject implements Parcelable {
	String url;
	String oid;
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(url);  
		 parcel.writeString(oid); 
		
	}
	
	public static final Creator<GetPart_Task_FormRequest> CREATOR = new Creator<GetPart_Task_FormRequest>() {
		 public GetPart_Task_FormRequest createFromParcel(Parcel source) {
			 GetPart_Task_FormRequest custLst = new GetPart_Task_FormRequest();
			 custLst.url = source.readString();  
			 custLst.oid = source.readString();  
		     return custLst;  
		 }

		@Override
		public GetPart_Task_FormRequest[] newArray(int size) {
			return new GetPart_Task_FormRequest[size];
		}
	}; 	 
	
	
}

