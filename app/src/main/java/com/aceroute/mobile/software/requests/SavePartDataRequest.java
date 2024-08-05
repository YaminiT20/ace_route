package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class SavePartDataRequest extends RequestObject implements Parcelable {
	
	
	public SavePartDataRequest() { ; };
	
	public SavePartDataRequest(Parcel in) {
		readFromParcel(in); 
	}
	
	String oid;
	String partId;
	String tid;
	String quantity;
	String stmp;
	String action;
	String timeStamp;
	String upd;
	String barcode;

	public String getSetbarcode() {
		return barcode;
	}

	public void setSetbarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	
	public String getUpd() {
		return upd;
	}

	public void setUpd(String upd) {
		this.upd = upd;
	}

	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getPartId() {
		return partId;
	}
	public void setPartId(String partId) {
		this.partId = partId;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getStmp() {
		return stmp;
	}
	public void setStmp(String stmp) {
		this.stmp = stmp;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(oid);  
		 parcel.writeString(partId); 
		 parcel.writeString(tid); 
		 parcel.writeString(quantity);
		 parcel.writeString(action);  
		 parcel.writeString(stmp); 
		 parcel.writeString(timeStamp);
		 parcel.writeString(barcode);

	}
	
	/*public static final Parcelable.Creator<DataRequest> CREATOR = new Creator<DataRequest>() {  
		 public DataRequest createFromParcel(Parcel source) {  
			 DataRequest savePart = new DataRequest();  
			 savePart.oid = source.readString();  
			 savePart.partId = source.readString();  
			 savePart.tid = source.readString();  
			 savePart.quantity = source.readString();  
			 savePart.stmp = source.readString();  
		     return savePart;  
		 }

		@Override
		public DataRequest[] newArray(int size) {
			return new DataRequest[size];  
		}
	};*/
	
	
	public static final Creator CREATOR = new Creator() {
		
		public SavePartDataRequest createFromParcel(Parcel in) {
			return new SavePartDataRequest(in); 
			} 
		
		public SavePartDataRequest[] newArray(int size) { 
			return new SavePartDataRequest[size];
			} 
		}; 
	
	
	private void readFromParcel(Parcel in) {
		
		 oid = in.readString();  
		 partId = in.readString();  
		 tid = in.readString();  
		 quantity = in.readString();  
		 stmp = in.readString(); 
		 action = in.readString();
		 timeStamp = in.readString();
		 barcode = in.readString();

	}
	
	
	/*data":{"oid": "'+orderId+'",'+	
	'"id": "'+orderpart_id+'",'+
	'"tid": "'+partTypeId+'",'+
	'"qty": "'+partQtyAdd+'",'+
	'"stmp": "'+tstamp+'",'+
	'"action": "'+action+'"}}';*/

}
