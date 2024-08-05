package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class GetFileMetaRequest extends RequestObject implements Parcelable {

	/*{"url":"'+AceRoute.appUrl+'",'+ '"action": "getfilemeta",' +'"oid":"' +orderId+'"}*/
	
	String url = "";
	String oid = "";
	String frmkey = "";

	public String getFrmkey() {
		return frmkey;
	}
	public void setFrmkey(String frmkey) {
		this.frmkey = frmkey;
	}

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
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(url);  
		parcel.writeString(oid);
		parcel.writeString(frmkey);
	}
	
	public static final Creator<GetFileMetaRequest> CREATOR = new Creator<GetFileMetaRequest>() {
		 public GetFileMetaRequest createFromParcel(Parcel source) {
			 GetFileMetaRequest fmeta = new GetFileMetaRequest();  
			 fmeta.url = source.readString();  
			 fmeta.oid = source.readString();
			 fmeta.frmkey = source.readString();
			 
		     return fmeta;  
		 }

		@Override
		public GetFileMetaRequest[] newArray(int size) {
			return new GetFileMetaRequest[size];  
		}
	}; 	
}
