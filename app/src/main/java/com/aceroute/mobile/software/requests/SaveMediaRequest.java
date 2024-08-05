package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class SaveMediaRequest extends RequestObject implements Parcelable {
	/*str = "{\"url\":\""+Api.API_BASE_URL+"\",\"type\":\"post\",\"data\":{\"id\":\"0\",\"oid\":\""+orderId+"\"," +
			"\"tid\":\"1\",\"file\":\""+""+"\",\"dtl\":\""+file_desc+"\",\"action\":\"savefile\"," +
			"\"stmp\":\""+tistamp+"\",\"mime\":\"jpg\",\"metapath\":\""+actualImgSav+"\",\"copy\":\"no\"}}";
*/
	
	String url;
	String type;
	String id;
	String orderId;
	String orderGeo;
	String tid;
	String file;
	String dtl;
	String timestamp;
	String mime;
	String metapath;
	String copy;
	String upd;
	String frmkey;
	String frmfldkey;
	 long localid =1;;

	public long getLocalid() {
		return localid;
	}

	public void setLocalid(long localid) {
		this.localid = localid;
	}

	public String getFrmkey() {
		return frmkey;
	}

	public void setFrmkey(String frmkey) {

		this.frmkey = frmkey;


	}

	public String getFrmfldkey() {
		return frmfldkey;
	}

	public void setFrmfldkey(String frmfldkey) {
		this.frmfldkey = frmfldkey;
	}

	public String getUpd() {
		return upd;
	}
	public void setUpd(String upd) {
		this.upd = upd;
	}

	public String getOrderGeo() {
		return orderGeo;
	}
	public void setOrderGeo(String orderGeo) {
		this.orderGeo = orderGeo;
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
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getDtl() {
		return dtl;
	}
	public void setDtl(String dtl) {
		this.dtl = dtl;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getMime() {
		return mime;
	}
	public void setMime(String mime) {
		this.mime = mime;
	}
	public String getMetapath() {
		return metapath;
	}
	public void setMetapath(String metapath) {
		this.metapath = metapath;
	}
	public String getCopy() {
		return copy;
	}
	public void setCopy(String copy) {
		this.copy = copy;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(url);  
		parcel.writeString(type); 
		parcel.writeString(id);  
		parcel.writeString(orderId);
		parcel.writeString(orderGeo);
		parcel.writeString(tid);  
		parcel.writeString(file);
		parcel.writeString(dtl);  
		parcel.writeString(timestamp);
		parcel.writeString(mime);  
		parcel.writeString(metapath);
		parcel.writeString(copy);
		parcel.writeString(frmkey);
		parcel.writeString(frmfldkey);
	}
	
	public static final Creator<SaveMediaRequest> CREATOR = new Creator<SaveMediaRequest>() {
		 public SaveMediaRequest createFromParcel(Parcel source) {
			 SaveMediaRequest saveImg = new SaveMediaRequest();  
			 saveImg.url = source.readString();  
			 saveImg.type = source.readString();
			 saveImg.id = source.readString();  
			 saveImg.orderId = source.readString();
			 saveImg.orderGeo = source.readString(); 
			 saveImg.tid = source.readString();  
			 saveImg.file = source.readString(); 
			 saveImg.dtl = source.readString();  
			 saveImg.timestamp = source.readString(); 
			 saveImg.mime = source.readString();  
			 saveImg.metapath = source.readString(); 
			 saveImg.copy = source.readString();
			 saveImg.frmkey = source.readString();
			 saveImg.frmfldkey = source.readString();
			 return saveImg;
		 }

		@Override
		public SaveMediaRequest[] newArray(int size) {
			return new SaveMediaRequest[size];  
		}
	}; 	
}
