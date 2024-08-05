package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class DeleteMediaRequest extends RequestObject implements Parcelable {

	/*String requestStr = "{\"url\":\"" + Api.API_BASE_URL
			+ "\",\"type\":\"post\",\"data\":{\"oid\":\"" + activeOrderObj.getId()
			+ "\"," + "\"action\":\"deletefile\"," + "\"id\":\""+ fileId + "\"}}";*/
	
	String url;
	String type;
	String Oid;
	String fileId;

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOid() {
		return Oid;
	}
	public void setOid(String oid) {
		Oid = oid;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(url);  
		parcel.writeString(type); 
		parcel.writeString(Oid);  
		parcel.writeString(fileId); 
		
	}
	
	public static final Creator<DeleteMediaRequest> CREATOR = new Creator<DeleteMediaRequest>() {
		 public DeleteMediaRequest createFromParcel(Parcel source) {
			 DeleteMediaRequest custLst = new DeleteMediaRequest();  
			 custLst.url = source.readString();  
			 custLst.type = source.readString();  
			 custLst.Oid = source.readString();  
			 custLst.fileId = source.readString(); 
		     return custLst;  
		 }

		@Override
		public DeleteMediaRequest[] newArray(int size) {
			return new DeleteMediaRequest[size];  
		}
	}; 	 
}
