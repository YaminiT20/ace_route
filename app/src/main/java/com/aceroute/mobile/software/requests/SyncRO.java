package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class SyncRO extends RequestObject implements Parcelable {
	
	String type;
	//String odrGetDate;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	/*public String getOdrGetDate() {
		return odrGetDate;
	}

	public void setOdrGetDate(String odrGetDate) {
		this.odrGetDate = odrGetDate;
	}*/

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		 parcel.writeString(type);
		//parcel.writeString(odrGetDate);

	}
	
	public static final Creator<SyncRO> CREATOR = new Creator<SyncRO>() {
		 public SyncRO createFromParcel(Parcel source) {
			 SyncRO syncReq = new SyncRO();  
			 syncReq.type = source.readString();
			// syncReq.odrGetDate = source.readString();
			 return syncReq;
		 }

		@Override
		public SyncRO[] newArray(int size) {
			return new SyncRO[size];  
		}
	}; 	 

}
