package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class SaveNewOrder extends RequestObject implements Parcelable {
	
	public SaveNewOrder() { ; }
	
	public SaveNewOrder(Parcel in) {
		readFromParcel(in); 
	}
	
	/*{"url":"'+AceRoute.appUrl+'",'+
	'"type": "post",'+
		'"data":{"id": "'+order_id+'",'+
		'"cid":"'+cust_id+'",'+
		'"cnm":"'+cust_nm+'",'+
		'"lid":"'+cust_siteid+'",'+
		'"cntid":"'+cust_contactid+'",'+
		'"geo":"'+event_geocode+'",'+
		'"start_date":"'+start_date+'",'+
		'"end_date":"'+end_date+'",'+
		'"dtl":"'+order_desc+'",'+
		'"orderStartTime":"'+orderStartTime+'",'+
		'"orderEndTime":"'+orderEndTime+'",'+
		'"nm":"'+order_name+'",'+
		'"po":"'+order_po+'",'+
		'"tid":"'+order_typeid+'",'+
		'"pid":"'+order_prtid+'",'+
		'"inv":"'+order_inst+'",'+
		'"wkf":"'+order_wkfid+'",'+
		'"note":"'+order_notes+'",'+
		'"flg":"'+order_flg+'",'+
		'"ltpnm":"'+siteTypeName+'",'+
		'"ctpnm":"'+custTypeName+'",'+
		'"rid":"'+res_id+'",'+
		'"rid2":"'+order_notes+'",'+
		'"reset":"'+reset+'",'+
		'"type":"'+type+'",'+
		'"rpt":"'+""+'",'+
		'"est":"'+order_startwin+'",'+
		'"lst":"'+order_endwin+'",'+
	    '"etm":"'+tstamp+'",'+	
		'"wkfupd":"'+wkfid_upd+'",'+
		'"action": "'+action+'"}}*/
	
	
	String url="";
	String type ="";
	String id = "";

	String custId ="";//cid
	String custNm ="";//cnm
	String locationId ="";//lid
	String custContactid ="";//cntid
	String geo ="";
	
	String start_date ="";
	String end_date ="";
	String orderDetail ="";//dtl
	String orderStartTime ="";
	String orderEndTime ="";
	String orderNm ="";//nm
	
	String alert =""; //alert
	String ssd = ""; //ssd
	String po =""; //route

	String tid ="";
	String priorityId ="";//pid
	String invoiceNum ="";//inv
	String orderStatus ="";//wkf
	String note ="";//cnot
	
	String flg ="";
	String locationTypNm ="";//ltpnm
	String custTypNm ="";//ctpnm
	String resId ="";//rid
	String rid2 ="";
	String reset ="";
	
	String typeD ="";
	String rpt ="";
	String est ="";//order_startwin
	String lst ="";//order_endwin
	String etm ="";
	String wkfupd ="";//wkfid_upd

	//extras
	String partCount="";//cprt
	String ServiceCount ="";//cserv
	String mediaCount="";
	String epochtime_etm="";
	
	String audio="";//caud
	String signature ="";//csig
	String images = "";//cimg
	
	public String getAlert() {
		return alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	public String getSsd() {
		return ssd;
	}

	public void setSsd(String ssd) {
		this.ssd = ssd;
	}
	
	public String getId() {
		return id;
	}
	
	public String getPartCount() {
		return partCount;
	}

	public void setPartCount(String partCount) {
		this.partCount = partCount;
	}

	public String getServiceCount() {
		return ServiceCount;
	}

	public void setServiceCount(String serviceCount) {
		ServiceCount = serviceCount;
	}

	public String getMediaCount() {
		return mediaCount;
	}

	public void setMediaCount(String mediaCount) {
		this.mediaCount = mediaCount;
	}

	public String getEpochtime_etm() {
		return epochtime_etm;
	}

	public void setEpochtime_etm(String epochtime_etm) {
		this.epochtime_etm = epochtime_etm;
	}

	public String getAudio() {
		return audio;
	}

	public void setAudio(String audio) {
		this.audio = audio;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}


	public void setId(String id) {
		this.id = id;
	}
	
	public String getOrderDetail() {
		return orderDetail;
	}

	public void setOrderDetail(String orderDetail) {
		this.orderDetail = orderDetail;
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
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getCustNm() {
		return custNm;
	}
	public void setCustNm(String custNm) {
		this.custNm = custNm;
	}
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public String getCustContactid() {
		return custContactid;
	}
	public void setCustContactid(String custContactid) {
		this.custContactid = custContactid;
	}
	public String getGeo() {
		return geo;
	}
	public void setGeo(String geo) {
		this.geo = geo;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getOrderStartTime() {
		return orderStartTime;
	}
	public void setOrderStartTime(String orderStartTime) {
		this.orderStartTime = orderStartTime;
	}
	public String getOrderEndTime() {
		return orderEndTime;
	}
	public void setOrderEndTime(String orderEndTime) {
		this.orderEndTime = orderEndTime;
	}
	public String getOrderNm() {
		return orderNm;
	}
	public void setOrderNm(String orderNm) {
		this.orderNm = orderNm;
	}
	public String getPo() {
		return po;
	}
	public void setPo(String po) {
		this.po = po;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getPriorityId() {
		return priorityId;
	}
	public void setPriorityId(String priorityId) {
		this.priorityId = priorityId;
	}
	public String getInvoiceNum() {
		return invoiceNum;
	}
	public void setInvoiceNum(String invoiceNum) {
		this.invoiceNum = invoiceNum;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getFlg() {
		return flg;
	}
	public void setFlg(String flg) {
		this.flg = flg;
	}
	public String getLocationTypNm() {
		return locationTypNm;
	}
	public void setLocationTypNm(String locationTypNm) {
		this.locationTypNm = locationTypNm;
	}
	public String getCustTypNm() {
		return custTypNm;
	}
	public void setCustTypNm(String custTypNm) {
		this.custTypNm = custTypNm;
	}
	public String getResId() {
		return resId;
	}
	public void setResId(String resId) {
		this.resId = resId;
	}
	public String getRid2() {
		return rid2;
	}
	public void setRid2(String rid2) {
		this.rid2 = rid2;
	}
	public String getReset() {
		return reset;
	}
	public void setReset(String reset) {
		this.reset = reset;
	}
	public String getTypeD() {
		return typeD;
	}
	public void setTypeD(String typeD) {
		this.typeD = typeD;
	}
	public String getRpt() {
		return rpt;
	}
	public void setRpt(String rpt) {
		this.rpt = rpt;
	}
	public String getEst() {
		return est;
	}
	public void setEst(String est) {
		this.est = est;
	}
	public String getLst() {
		return lst;
	}
	public void setLst(String lst) {
		this.lst = lst;
	}
	public String getEtm() {
		return etm;
	}
	public void setEtm(String etm) {
		this.etm = etm;
	}
	public String getWkfupd() {
		return wkfupd;
	}
	public void setWkfupd(String wkfupd) {
		this.wkfupd = wkfupd;
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
		 parcel.writeString(custId); 
		 parcel.writeString(custNm);  
		 parcel.writeString(locationId); 
		 parcel.writeString(custContactid); 
		 parcel.writeString(geo);  

		 parcel.writeString(start_date); 
		 parcel.writeString(end_date);
		 parcel.writeString(orderDetail);
		 parcel.writeString(orderStartTime);  
		 parcel.writeString(orderEndTime); 
		 parcel.writeString(orderNm); 
		
		 parcel.writeString(alert);
		 parcel.writeString(ssd);
		 parcel.writeString(po);  
		 parcel.writeString(tid); 
		 parcel.writeString(priorityId); 
		 parcel.writeString(invoiceNum); 
		 parcel.writeString(orderStatus);  
		 parcel.writeString(note); 
		 
		 parcel.writeString(flg);  
		 parcel.writeString(locationTypNm); 
		 parcel.writeString(custTypNm); 
		 parcel.writeString(resId); 
		 parcel.writeString(rid2);  
		 parcel.writeString(reset);
		 
		 parcel.writeString(typeD); 
		 parcel.writeString(rpt); 
		 parcel.writeString(est); 
		 parcel.writeString(lst);  
		 parcel.writeString(etm);
		 parcel.writeString(wkfupd);
		 parcel.writeString(id);
		 
		//extras
		 
		 parcel.writeString(partCount); 
		 parcel.writeString(ServiceCount); 
		 parcel.writeString(mediaCount); 
		 parcel.writeString(epochtime_etm);  
		 parcel.writeString(audio);
		 parcel.writeString(signature);
		 parcel.writeString(images);
	}
	
	public static final Creator CREATOR = new Creator() {
		
		public SaveNewOrder createFromParcel(Parcel in) {
			return new SaveNewOrder(in); 
			} 
		
		public SaveNewOrder[] newArray(int size) { 
			return new SaveNewOrder[size];
			} 
		}; 
	
	
	private void readFromParcel(Parcel in) {
		
		url = in.readString();  
		type = in.readString();  
		custId = in.readString();  
		custNm = in.readString();  
		locationId = in.readString();  
		custContactid = in.readString();  
		geo = in.readString();  
		
		start_date = in.readString();  
		end_date = in.readString();
		orderDetail = in.readString();
		orderStartTime = in.readString();  
		orderEndTime = in.readString();  
		orderNm = in.readString();  
		
		alert = in.readString();
		ssd = in.readString();
		po = in.readString();  
		tid = in.readString();  
		priorityId = in.readString();  
		invoiceNum = in.readString();  
		orderStatus = in.readString();  
		note = in.readString();  
		
		flg = in.readString();  
		locationTypNm = in.readString();  
		custTypNm = in.readString();  
		resId = in.readString();  
		rid2 = in.readString();  
		reset = in.readString();  
		
		typeD = in.readString();  
		rpt = in.readString();  
		est = in.readString();  
		lst = in.readString();  
		etm = in.readString();  
		wkfupd = in.readString();  
		id = in.readString(); 
		
		//extras
		partCount = in.readString();  
		ServiceCount = in.readString();  
		mediaCount = in.readString();  
		epochtime_etm = in.readString();  
		audio = in.readString();  
		signature = in.readString();  
		images = in.readString();
	}
	
}
