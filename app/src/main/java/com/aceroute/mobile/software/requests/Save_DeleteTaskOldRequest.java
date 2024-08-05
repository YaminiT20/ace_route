package com.aceroute.mobile.software.requests;

/*
public class Save_DeleteTaskOldRequest extends RequestObject implements Parcelable {
	public Save_DeleteTaskOldRequest(){ ; }
	
	public Save_DeleteTaskOldRequest(Parcel in) {
		readFromParcel(in); 
	}
	
	String url;
	String type;
	SaveTaskOldDataRequest dataObj;
	
	
	public String getUrl(){
		return url;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	
	public String getType (){
		return type;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public SaveTaskOldDataRequest getDataObj() {		
		return dataObj;		
	}	
	
	public void setDataObj(SaveTaskOldDataRequest data){
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
	}
	
	private void readFromParcel(Parcel in) {
		 url = in.readString();  
		 type = in.readString();  
		 dataObj = in.readParcelable(SaveTaskOldDataRequest.class.getClassLoader());		
	}
	
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Save_DeleteTaskOldRequest createFromParcel(Parcel in) {
			return new Save_DeleteTaskOldRequest(in); 
		}   
		
		public Save_DeleteTaskOldRequest[] newArray(int size) { 
			return new Save_DeleteTaskOldRequest[size]; 
		} 
	};
	
}
*/
