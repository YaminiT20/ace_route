package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class EditContactReq  extends RequestObject implements Parcelable {

		String action;
		long id;
		String name;
		String tell;
		int tid;
		long cid;
		String eml;

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}


		public int getTid() {
			return tid;
		}
		public void setTid(int tid) {
			this.tid = tid;
		}
		public String getAction() {
			return action;
		}
		public void setAction(String action) {
			this.action = action;
		}
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getTell() {
			return tell;
		}
		public void setTell(String tell) {
			this.tell = tell;
		}

		public String getEmail() {
			return eml;
		}

		public void setEmail(String email) {
			this.eml = email;
		}

	@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public void writeToParcel(Parcel parcel, int flags) {
			parcel.writeString(action);  
			parcel.writeLong(id);
			parcel.writeString(name); 
			parcel.writeString(tell);
			parcel.writeInt(tid);
			parcel.writeLong(cid);
			parcel.writeString(eml);

		}
		
		public static final Creator<EditContactReq> CREATOR = new Creator<EditContactReq>() {
			 public EditContactReq createFromParcel(Parcel source) {
				 EditContactReq edtContact = new EditContactReq();  
				 edtContact.action = source.readString();
				 edtContact.id = source.readLong();
				 edtContact.name = source.readString();  
				 edtContact.tell = source.readString();
				 edtContact.tid = source.readInt();
				 edtContact.cid = source.readLong();
				 edtContact.eml = source.readString();
				 return edtContact;
			 }

			@Override
			public EditContactReq[] newArray(int size) {
				return new EditContactReq[size];  
			}
		}; 	 
}