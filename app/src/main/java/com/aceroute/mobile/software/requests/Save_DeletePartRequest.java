package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class Save_DeletePartRequest extends RequestObject implements Parcelable {
	
	public Save_DeletePartRequest() { ; }
	
	public Save_DeletePartRequest(Parcel in) {
		readFromParcel(in); 
	} 
	
	String url;
	String type;
	SavePartDataRequest dataObj;
	
	
	
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
	public SavePartDataRequest getDataObj() {
		return dataObj;
	}
	public void setDataObj(SavePartDataRequest data) {
		this.dataObj = data;
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
		 parcel.writeParcelable(dataObj, flags);
		 //parcel.writeString(type); 
		
	}
	
	/*public static final Parcelable.Creator<SavePartReq> CREATOR = new Creator<SavePartReq>() {  
		 public SavePartReq createFromParcel(Parcel source) {  
			 SavePartReq savePart = new SavePartReq();  
			 savePart.url = source.readString();  
			 savePart.type = source.readString();  
			 source.readParcelable(DataRequest.class.getClassLoader());
		     return savePart;  
		 }

		@Override
		public SavePartReq[] newArray(int size) {
			return new SavePartReq[size];  
		}
	};*/
	
	
	private void readFromParcel(Parcel in) {
		
		 url = in.readString();  
		 type = in.readString();  
		 dataObj = in.readParcelable(SavePartDataRequest.class.getClassLoader());
		
	}
	
	public static final Creator CREATOR = new Creator() {
		public Save_DeletePartRequest createFromParcel(Parcel in) {
			return new Save_DeletePartRequest(in); 
			}   
		
		public Save_DeletePartRequest[] newArray(int size) { 
			return new Save_DeletePartRequest[size]; 
			} 
		};

}
/*var str = '{"url":"'+Api.API_BASE_URL+'",'+'"type": "post",'+
'"action": "'+action+'",'+
'"data":{"oid": "'+orderId+'",'+	
'"id": "'+orderpart_id+'",'+
'"tid": "'+partTypeId+'",'+
'"qty": "'+partQtyAdd+'",'+
'"stmp": "'+tstamp+'",'+
'"action": "'+action+'"}}';*/