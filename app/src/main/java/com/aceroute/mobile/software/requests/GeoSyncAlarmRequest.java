package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class GeoSyncAlarmRequest extends RequestObject implements Parcelable {

	public GeoSyncAlarmRequest() {
	 }
	
	private GeoSyncAlarmRequest(Parcel in) {
		action = in.readString();
	 }
	
	String data;

    public String getSyncToServer() {
        return syncToServer;
    }

    public void setSyncToServer(String syncToServer) {
        this.syncToServer = syncToServer;
    }

    String syncToServer;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(action);
		 parcel.writeString(data);
		 parcel.writeString(syncToServer);
	}
	
	public static final Creator<GeoSyncAlarmRequest> CREATOR = new Creator<GeoSyncAlarmRequest>() {
		 public GeoSyncAlarmRequest createFromParcel(Parcel source) {
			 GeoSyncAlarmRequest clkInOut = new GeoSyncAlarmRequest(source);  
			 clkInOut.data = source.readString();
			 clkInOut.syncToServer = source.readString();
		     return clkInOut;  
		 }

		@Override
		public GeoSyncAlarmRequest[] newArray(int size) {
			return new GeoSyncAlarmRequest[size];  
		}
	}; 	 
	
}
