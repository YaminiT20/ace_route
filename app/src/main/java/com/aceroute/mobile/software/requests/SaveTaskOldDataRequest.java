package com.aceroute.mobile.software.requests;

/*
public class SaveTaskOldDataRequest extends RequestObject implements Parcelable {
	
	public SaveTaskOldDataRequest(){ ; }
	
	public SaveTaskOldDataRequest(Parcel in) {
		readFromParcel(in); 
	}
	
	String orderId;//oid
	String taskId;//id
	String taskTypeId;//tid
	String rate; //rate
	String stmp;
	String action;
	String upd = "0";
	
	public String getUpd(){
		return upd;
	}
	
	public void setUpd(String strUpd){
		this.upd = strUpd;
	}
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskTypeId() {
		return taskTypeId;
	}
	
	public void setTaskTypeId(String taskTypeId) {
		this.taskTypeId = taskTypeId;
	}
	
	public String getRate(){
		return rate;
	}
	
	public void setRate(String rate){
		this.rate = rate;
	}
	
	public String getStmp() {
		return stmp;
	}

	public void setStmp(String stmp) {
		this.stmp = stmp;
	}
	
	public String getAction(){
		return action;
	}
	
	public void setAction(String action){
		this.action = action;
	}	
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(orderId);  
		parcel.writeString(taskId); 
		parcel.writeString(taskTypeId); 
		parcel.writeString(rate);		
		parcel.writeString(stmp);		
		parcel.writeString(action);  
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		
		public SaveTaskOldDataRequest createFromParcel(Parcel in) {
			return new SaveTaskOldDataRequest(in); 
		}  
		
		public SaveTaskOldDataRequest[] newArray(int size) { 
			return new SaveTaskOldDataRequest[size];
		} 
	}; 

	private void readFromParcel(Parcel in) {
		orderId = in.readString();  
		taskId = in.readString();  
		taskTypeId = in.readString();  
		rate = in.readString();  
		stmp = in.readString(); 
		action = in.readString();	
	}
}
*/
