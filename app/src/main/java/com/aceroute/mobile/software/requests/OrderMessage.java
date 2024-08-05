package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class OrderMessage extends RequestObject implements Parcelable {

	String action;
	long tid;
	long oid;
	long cid;
	long cntid;
	long tel;
	String tz;
	String geo;
	String stmp;

	public String getStmp() {
		return stmp;
	}
	public void setStmp(String stmp) {
		this.stmp = stmp;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public long getTid() {
		return tid;
	}
	public void setTid(long tid) {
		this.tid = tid;
	}
	public long getOid() {
		return oid;
	}
	public void setOid(long oid) {
		this.oid = oid;
	}
	public long getCid() {
		return cid;
	}
	public void setCid(long cid) {
		this.cid = cid;
	}
	public long getCntid() {
		return cntid;
	}
	public void setCntid(long cntid) {
		this.cntid = cntid;
	}
	public long getTel() {
		return tel;
	}
	public void setTel(long tel) {
		this.tel = tel;
	}
	public String getTz() {
		return tz;
	}
	public void setTz(String tz) {
		this.tz = tz;
	}
	public String getGeo() {
		return geo;
	}
	public void setGeo(String geo) {
		this.geo = geo;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(action);
		 parcel.writeLong(tid);  
		 parcel.writeLong(oid);
		 parcel.writeLong(cid);
		 parcel.writeLong(cntid);  
		 parcel.writeLong(tel);
		 parcel.writeString(tz);
		 parcel.writeString(geo);  
		 parcel.writeString(stmp);  
		 
	}
	
	public static final Creator<OrderMessage> CREATOR = new Creator<OrderMessage>() {
		 public OrderMessage createFromParcel(Parcel source) {
			 OrderMessage orderMsg = new OrderMessage();  
			 orderMsg.action = source.readString();  
			 orderMsg.tid = source.readLong();
			 orderMsg.oid = source.readLong();  
			 orderMsg.cid = source.readLong();
			 orderMsg.cntid = source.readLong();  
			 orderMsg.tel = source.readLong();
			 orderMsg.tz = source.readString();  
			 orderMsg.geo = source.readString();
			 orderMsg.stmp = source.readString();
		     return orderMsg;  
		 }

		@Override
		public OrderMessage[] newArray(int size) {
			return new OrderMessage[size];  
		}
	}; 	 
}
