package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class updateOrderRequest extends RequestObject implements Parcelable {
	
	/*{"url":"'+AceRoute.appUrl+'",'+'"type": "post",'+'"data":{"id": "'+orderId+'",'+
	'"name": "'+fieldToUpdate+'",'+'"value": "'+fieldValue+'",'+'"action": "'+action+'"}}*/
	
	String url;
	String type;
	String id;
	String name;
	String value;
	String dtl;
	String dtlvalue;
	String po;
	String povalue;
	String inv;
	String invvalue;
	String dtlist;

	public String getDtl() {
		return dtl;
	}

	public void setDtl(String dtl) {
		this.dtl = dtl;
	}

	public String getDtlvalue() {
		return dtlvalue;
	}

	public void setDtlvalue(String dtlvalue) {
		this.dtlvalue = dtlvalue;
	}

	public String getPo() {
		return po;
	}

	public void setPo(String po) {
		this.po = po;
	}

	public String getPovalue() {
		return povalue;
	}

	public void setPovalue(String povalue) {
		this.povalue = povalue;
	}

	public String getInv() {
		return inv;
	}

	public void setInv(String inv) {
		this.inv = inv;
	}

	public String getInvvalue() {
		return invvalue;
	}

	public void setInvvalue(String invvalue) {
		this.invvalue = invvalue;
	}

	public String getDtlist() {
		return dtlist;
	}
	public void setDtlist(String dtlist) {
		this.dtlist = dtlist;
	}
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
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
		 parcel.writeString(id); 
		 parcel.writeString(name); 
		 parcel.writeString(value);
		 parcel.writeString(dtlist);
	}
	
	public static final Creator<updateOrderRequest> CREATOR = new Creator<updateOrderRequest>() {
		 public updateOrderRequest createFromParcel(Parcel source) {
			 updateOrderRequest workerLst = new updateOrderRequest();  
			 workerLst.url = source.readString();  
			 workerLst.type = source.readString();
			 workerLst.id = source.readString();
			 workerLst.name = source.readString();
			 workerLst.value = source.readString();
			 workerLst.dtlist = source.readString();
		     return workerLst;  
		 }

		@Override
		public updateOrderRequest[] newArray(int size) {
			return new updateOrderRequest[size];  
		}
	}; 	 
}
