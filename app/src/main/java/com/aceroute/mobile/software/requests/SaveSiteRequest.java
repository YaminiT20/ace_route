package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class SaveSiteRequest extends RequestObject implements Parcelable {

	/*"{\"url\":\"\",\"action\":\"savesite\",\"data\":{\"adr\":\""+addrs+"\"," +
	"\"nm\":\""+name+"\",\"cid\":\""+custId+"\"," +
			"\"tstamp\":\""+tistamp+"\",\"id\":\"0\",\"geo\":\""+currentGeo+"\",
			\"dtl\":\""+detail.getText().toString() +"\",\"tid\":\""+siteTypeId.get(selecteditem)+"\"," +
					"\"ltpnm\":\""+sitetypeNm.get(selecteditem)+"\"}}" */
	
	String url;
	String adr;
	String nm;
	String cid;
	String tstamp;
	String id;
	String geo;
	String dtl;
	String tid;
	String ltpnm;
	String upd;

	public String getUpd() {
		return upd;
	}
	public void setUpd(String upd) {
		this.upd = upd;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAdr() {
		return adr;
	}
	public void setAdr(String adr) {
		this.adr = adr;
	}
	public String getNm() {
		return nm;
	}
	public void setNm(String nm) {
		this.nm = nm;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getTstamp() {
		return tstamp;
	}
	public void setTstamp(String tstamp) {
		this.tstamp = tstamp;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGeo() {
		return geo;
	}
	public void setGeo(String geo) {
		this.geo = geo;
	}
	public String getDtl() {
		return dtl;
	}
	public void setDtl(String dtl) {
		this.dtl = dtl;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getLtpnm() {
		return ltpnm;
	}
	public void setLtpnm(String ltpnm) {
		this.ltpnm = ltpnm;
	}
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		 parcel.writeString(url);  
		 parcel.writeString(adr); 
		 parcel.writeString(nm); 
		 parcel.writeString(cid); 
		 parcel.writeString(tstamp);  
		 parcel.writeString(id); 
		 parcel.writeString(geo); 
		 parcel.writeString(dtl); 
		 parcel.writeString(tid); 
		 parcel.writeString(ltpnm); 
		 parcel.writeString(upd);
	}
	
	public static final Creator<SaveSiteRequest> CREATOR = new Creator<SaveSiteRequest>() {
		 public SaveSiteRequest createFromParcel(Parcel source) {
			 SaveSiteRequest saveSite = new SaveSiteRequest();  
			 saveSite.url = source.readString();  
			 saveSite.adr = source.readString();
			 saveSite.nm = source.readString();
			 saveSite.cid = source.readString();
			 saveSite.tstamp = source.readString();  
			 saveSite.id = source.readString();
			 saveSite.geo = source.readString();
			 saveSite.dtl = source.readString();
			 saveSite.tid = source.readString();  
			 saveSite.ltpnm = source.readString();
			 saveSite.upd = source.readString();
		     return saveSite;
		 }

		@Override
		public SaveSiteRequest[] newArray(int size) {
			return new SaveSiteRequest[size];  
		}
	}; 	 
	
	
}
