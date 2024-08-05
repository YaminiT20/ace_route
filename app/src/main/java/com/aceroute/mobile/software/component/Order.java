package com.aceroute.mobile.software.component;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.aceroute.mobile.software.AceRouteService;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.http.RequestObject;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.network.AceRequestHandler;
import com.aceroute.mobile.software.utilities.PreferenceHandler;

import java.util.HashMap;

public class Order extends DataObject implements RespCBandServST, Parcelable {
	
	//xml parent element idetifier
	public final static String ORDER_PARENT_TAG = "event";
	public final static String ORDER_ID = "id";
	public final static String ORDER_START_DATE = "start_date";
	public final static String ORDER_START_TIME = "orderStartTime";
	public final static String ORDER_END_TIME = "orderEndTime";
	public final static String ORDER_END_DATE = "end_date";
	public final static String ORDER_NAME = "nm";
	public final static String ORDER_TIME = "order_time";
	public final static String ORDER_STATUS_ID = "wkf";
	public final static String ORDER_ALERT = "alt";
	public final static String ORDER_STATUS_PO_NUMBER = "po";
	public final static String ORDER_STATUS_INVOICE_NUMBER = "inv";
	public final static String ORDER_CUSTOMER_CTID = "ctid";
	public final static String ORDER_STATUS_ORDER_TYPE_ID = "tid";
	public final static String ORDER_STATUS_PRIORITY_TYPE_ID = "pid";
	public final static String ORDER_STATUS_PRIMARY_WORKER_ID = "rid";
//	public final static String ORDER_STATUS_LIST_ADD_WORKER_PIPED = "rid2";
	public final static String ORDER_STATUS_SUMMARY = "dtl";
	public final static String ORDER_CUSTOMER_ID = "cid";
	public final static String ORDER_CUSTOMER_NAME = "cnm";
	public final static String ORDER_CUSTOMER_SITE_ID = "lid";
	public final static String ORDER_CUSTOMER_SITE_STREET_ADDRESS = "adr";
	public final static String ORDER_CUSTOMER_SITE_SUITE_ADDRESS = "adr2";
	public final static String ORDER_CUSTOMER_SITE_GEOCODE = "geo";
	public final static String ORDER_CUSTOMER_SITE_C_GEOCODE = "egeo";
	public final static String ORDER_CUSTOMER_CONTACT_ID = "cntid";
	public final static String ORDER_CUSTOMER_CONTACT_NAME = "cntnm";
	public final static String ORDER_CUSTOMER_CONTACT_NUMBER = "tel";
	public final static String ORDER_CUSTOMER_EMAIL = "eml";
	public final static String ORDER_CUSTOMER_PART_COUNT = "cprt";
	public final static String ORDER_CUSTOMER_SERVICE_COUNT = "ctsk";//YD cserv earlier
	public final static String ORDER_CUSTOMER_TYPE_NAME = "ctpnm";

	public final static String ORDER_SITE_TYPE_NAME = "ltpnm";
	public final static String ORDER_CUSTOMER_MEDIA_COUNT = "fmeta";
	public final static String ORDER_NOTES = "note";
	public final static String ORDER_FLG = "flg";
	public final static String ORDER_RESET = "reset";
	public final static String ORDER_TYPE = "type";
	public final static String ORDER_RPT = "rpt";
	public final static String ORDER_EST = "est";
	public final static String ORDER_LST = "lst";
	public final static String ORDER_WKFUPD = "wkfupd";
	public final static String ORDER_EPOCH_TIME = "etm";
	public static final String KEY_NAME = "name";
	public static final String KEY_VALUE = "value";
	public static final String KEY_DTLIST = "dtlist";
	public static final String KEY_GEO = "geo";
	public static final String ORDER_TMSTAMP = "stmp";//earlier tstamp
	public static final String ORDER_SORTINDEX = "sind";
	public static final String ORDER_AUDIO = "caud";
	public static final String ORDER_SIG = "csig";
	public static final String ORDER_IMG = "cimg";
	public static final String ORDER_NOTE = "cnot";
	public static final String ORDER_TEL_TYPEID = "ttid";
	public static final String ORDER_DOCS = "cdoc";
	public static final String ORDER_ASSET_COUNT = "asts";//YD using for just for keeping count in the table of asset otherwise asset are coming in customer xml
    public static final String ORDER_CUSTOMER_FORM_COUNT = "cfrm";


    public final static int TYPE = 14;
    public final static int TYPE_UPDATE = 102;
    public final static int TYPE_CUST_TOKEN = 55;//YD using for customer history.

    public final static String ACTION_ORDER = "getorders";
    public final static String ACTION_SAVE_ORDER = "saveorder";
    public final static String ACTION_SAVE_ORDER_FLD = "saveorderfld";
    public final static String ACTION_CUSTOMER_HISTORY = "getrpt";//YD using for customer history.


    static Order order = new Order();
	static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();

	private String custemal = "";
	private String custids = null;
	private long id =1;
	private long localid =1;
	private String nm = null;
	private String toDate = "";
	private String fromDate = "";
	private long statusId = -1;
	private String alert = null;
	private String poNumber = null;
	private String invoiceNumber = null;
	private long orderTypeId = -1;
	private long startTime = -1;
	private long endTime = -1;
	private long priorityTypeId = -1;
	private String primaryWorkerId = null;
	//private String listAddWorkerPiped = null;
	private String summary = null;
	private String custName = null;
	private long customerid = -1;
	private String custSiteStreeAdd = null;
	private String custSiteSuiteAddress = null;
	private String custSiteGeocode = null;
	private String eml = null;
	private long contactId = -1;
	private String custContactName = null;
	private String custContactNumber = null;
	private String custSiteId = null;
	private String ordeNotes = null;
	private long custPartCount = -1;
	//private long custServiceCount = -1;
    private long custFormCount =0;
	private long custMetaCount = -1;
	private long epochTime = -1;
	private long sortindex = -1;
	private long audioCount = -1;
	private long sigCount = -1;
	private long imgCount = -1;
	private long notCount = -1;
	private long docCount = -1;
	private String type;// YD pubnub type
	private String modified = "";
	private String est = null;
	private String lst = null;
	private String flg = null;
	private String custypename = null;
	private String sitetypename = null;
	private long telTypeId = -1;
	private int sequenceNumber =-1;
	private long assetsCount = 0;
	private String distanceAndTime = "";
	private double distanceForSkip = -1;//YD using when skipping order and changing the order status if it is best to skip
	private long distanceFmOrder = 0;//YD using when skipping order and changing the order status if it is best to skip
	private long timeFmOrder = -1;//YD using when skipping order and changing the order status if it is best to skip

	public String getCustemal() {return custemal;}
	public void setCustemal(String custemal) {this.custemal = custemal;}
	public String getCustids() {return custids;}
	public void setCustids(String custids){
		this.custids = custids;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	public long getContactId() {
		return contactId;
	}
	public void setContactId(long contactId) {
		this.contactId = contactId;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long l) {
		this.id = l;
	}
	public long getLocalId() {
		return localid;
	}
	public void setLocalId(long l) {
		this.localid = l;
	}
	public String getNm() {
		return nm;
	}
	public void setNm(String nm) {
		this.nm = nm;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public long getStatusId() {
		return statusId;
	}
	public void setStatusId(long statusId) {
		this.statusId = statusId;
	}
	public String getOrderAlert() {
		return alert;
	}
	public void setOrderAlert(String alert) {
		this.alert = alert;
	}
	public String getPoNumber() {
		return poNumber;
	}
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	public long getOrderTypeId() {
		return orderTypeId;
	}
	public void setOrderTypeId(long orderTypeId) {
		this.orderTypeId = orderTypeId;
	}
	public long getPriorityTypeId() {
		return priorityTypeId;
	}
	public void setPriorityTypeId(long priorityTypeId) {
		this.priorityTypeId = priorityTypeId;
	}
	public String getPrimaryWorkerId() {
		return primaryWorkerId;
	}
	public void setPrimaryWorkerId(String primaryWorkerId) {
		this.primaryWorkerId = primaryWorkerId;
	}
	/*public String getListAddWorkerPiped() {
		return listAddWorkerPiped;
	}
	public void setListAddWorkerPiped(String listAddWorkerPiped) {
		this.listAddWorkerPiped = listAddWorkerPiped;
	}*/
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getCustSiteStreeAdd() {
		return custSiteStreeAdd;
	}
	public void setCustSiteStreeAdd(String custSiteStreeAdd) {
		this.custSiteStreeAdd = custSiteStreeAdd;
	}
	public String getCustSiteSuiteAddress() {
		return custSiteSuiteAddress;
	}
	public void setCustSiteSuiteAddress(String custSiteSuiteAddress) {
		this.custSiteSuiteAddress = custSiteSuiteAddress;
	}
	public String getCustSiteGeocode() {
		return custSiteGeocode;
	}
	public void setCustSiteGeocode(String custSiteGeocode) {
		this.custSiteGeocode = custSiteGeocode;
	}
	public String getCustContactName() {
		return custContactName;
	}
	public void setCustContactName(String custContactName) {
		this.custContactName = custContactName;
	}
	public String getCustContactNumber() {
		return custContactNumber;
	}
	public void setCustContactNumber(String custContactNumber) {
		this.custContactNumber = custContactNumber;
	}
	public long getCustPartCount() {
		return custPartCount;
	}
	public void setCustPartCount(long custPartCount) {
		this.custPartCount = custPartCount;
	}
	/*public long getCustServiceCount() {
		return custServiceCount;
	}
	public void setCustServiceCount(long custServiceCount) {
		this.custServiceCount = custServiceCount;
	}*/

    public long getCustFormCount() {
        return custFormCount;
    }
    public void setCustFormCount(long custServiceCount) {
        this.custFormCount = custServiceCount;
    }
	public String getOrdeNotes() {
		return ordeNotes;
	}
	public void   setOrdeNotes(String ordeNotes) {
		this.ordeNotes = ordeNotes;
	}
	public long getCustomerid() {
		return customerid;
	}

	public void setCustomerid(long customerid) {
		this.customerid = customerid;
	}
	public long getCustMetaCount() {
		return custMetaCount;
	}
	public void setCustMetaCount(long custMetaCount) {
		this.custMetaCount = custMetaCount;
	}
	public long getEpochTime() {
		return epochTime;
	}
	public void setEpochTime(long epochTime) {
		this.epochTime = epochTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public String getCustSiteId() {
		return custSiteId;
	}
	public void setCustSiteId(String custSiteId) {
		this.custSiteId = custSiteId;
	}
	public String getModified() {
		return modified;
	}
	public void setModified(String modified) {
		this.modified = modified;
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
	public String getFlg() {
		return flg;
	}
	public void setFlg(String flg) {
		this.flg = flg;
	}
	public String getCustypename() {
		return custypename;
	}
	public void setCustypename(String custypename) {
		this.custypename = custypename;
	}
	public String getSitetypename() {
		return sitetypename;
	}
	public void setSitetypename(String sitetypename) {
		this.sitetypename = sitetypename;
	}
	public long getSortindex() {
		return sortindex;
	}
	public void setSortindex(long sortindex) {
		this.sortindex = sortindex;
	}
	public long getAudioCount() {
		return audioCount;
	}
	public void setAudioCount(long audioCount) {
		this.audioCount = audioCount;
	}
	public long getSigCount() {
		return sigCount;
	}
	public void setSigCount(long sigCount) {
		this.sigCount = sigCount;
	}
	public long getImgCount() {
		return imgCount;
	}
	public void setImgCount(long imgCount) {
		this.imgCount = imgCount;
	}
	public long getNotCount() {
		return notCount;
	}
	public void setNotCount(long notCount) {
		this.notCount = notCount;
	}
	
	public long getTelTypeId() {
		return telTypeId;
	}
	public void setTelTypeId(long telTypeId) {
		this.telTypeId = telTypeId;
	}
	public long getDocCount() {
		return docCount;
	}

	public void setDocCount(long docCount) {
		this.docCount = docCount;
	}

	public String getEml() {
		return eml;
	}

	public void setEml(String eml) {
		this.eml = eml;
	}

	public static void getData(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj , int reqId)
	{
		objForCallbkWithId.put(reqId, callbackObj);
		
		AceRequestHandler requestHandler=null;
		Intent intent = null;
		Long currentMilli =null;
		
		if (Activityobj instanceof Activity){
			requestHandler = new AceRequestHandler(((Activity)Activityobj).getApplicationContext());
			 intent = new Intent(((Activity)Activityobj).getApplicationContext(),AceRouteService.class);
			 currentMilli = PreferenceHandler.getPrefQueueRequestId(((Activity)Activityobj).getApplicationContext());
		}
		else if (Activityobj instanceof FragmentActivity){
			requestHandler = new AceRequestHandler(((FragmentActivity)Activityobj).getApplicationContext());
			intent = new Intent(((FragmentActivity)Activityobj).getApplicationContext(),AceRouteService.class);
			currentMilli = PreferenceHandler.getPrefQueueRequestId(((FragmentActivity)Activityobj).getApplicationContext());
		}
		
		Bundle mBundle = new Bundle();
        mBundle.putParcelable("OBJECT", reqObj);  
        mBundle.putLong(AceRouteService.KEY_TIME, currentMilli);
        mBundle.putInt(AceRouteService.KEY_SYNCALL_FLAG, AceRouteService.VALUE_NOT_SYNCALL);
        mBundle.putInt(AceRouteService.FLAG_FOR_CAMERA, 0);
        mBundle.putInt(AceRouteService.KEY_ID, reqId);
        mBundle.putString(AceRouteService.KEY_ACTION, reqObj.getAction());

        intent.putExtras(mBundle);  
		requestHandler.ServiceStarterLoc(Activityobj, intent,order, currentMilli);
	}
	@Override
	public void ServiceStarter(RespCBandServST activity, Intent intent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setResponseCallback(String response, Integer reqId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setResponseCBActivity(Response response) {
		if (response!=null)
		{
			try {
				RespCBandServST callBackOBJ = objForCallbkWithId.get(response.getId());
				objForCallbkWithId.remove(response.getId());
				callBackOBJ.setResponseCBActivity(response);
			}catch (NullPointerException e){
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public int getObjectDataStore(RequestObject reqObj, RespCBandServST Activityobj,
			RespCBandServST callbackObj ,int reqId)
	{
		if (ordersXmlDataStore!=null){

			Log.d("Dataobject","niki"+ordersXmlDataStore);
			Response response = makeResponseObject(reqId,ordersXmlDataStore);
			callbackObj.setResponseCBActivity(response);
			return 1;
		}
		else {
			getData(reqObj,Activityobj,callbackObj ,reqId);
			return 2;
		}
	}
	
	public static void saveOrderField(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj ,int reqId)
	{
		getData(reqObj,Activityobj,callbackObj,reqId);
	}
	public static void saveOrder(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj ,int reqId)
	{
		getData(reqObj,Activityobj,callbackObj,reqId);
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		 parcel.writeLong(id);
		 parcel.writeLong(localid);  
		 parcel.writeString(nm);
		 parcel.writeString(toDate);
		 parcel.writeString(fromDate);
		 
		 parcel.writeLong(statusId);
		 parcel.writeString(alert);
		 parcel.writeString(poNumber);  
		 parcel.writeString(invoiceNumber);
		 parcel.writeLong(orderTypeId);
		 
		 parcel.writeLong(startTime);  
		 parcel.writeLong(endTime);
		 parcel.writeLong(priorityTypeId);
		 parcel.writeString(primaryWorkerId);  
		 parcel.writeString(summary);
		 
		 parcel.writeString(custName);
		 parcel.writeLong(customerid);  
		 parcel.writeString(custSiteStreeAdd);
		 parcel.writeString(custSiteSuiteAddress);
		 parcel.writeString(custSiteGeocode);
		 
		 parcel.writeLong(contactId);
		 parcel.writeString(custContactName);
		 parcel.writeString(custContactNumber);  
		 parcel.writeString(custSiteId);
		 parcel.writeString(ordeNotes);
		 
		 parcel.writeLong(custPartCount);  
		 parcel.writeLong(custFormCount);
		 parcel.writeLong(custMetaCount);
		 parcel.writeLong(epochTime);  
		 parcel.writeLong(sortindex);
		 
		 parcel.writeLong(audioCount);
		 parcel.writeLong(sigCount);  
		 parcel.writeLong(imgCount);
		 parcel.writeLong(notCount);
		 parcel.writeString(type);
		 
		 parcel.writeString(modified);
		 parcel.writeString(est);  
		 parcel.writeString(lst);
		 parcel.writeString(flg);
		 
		 parcel.writeString(custypename);  
		 parcel.writeString(sitetypename);
		 parcel.writeLong(telTypeId);
		 parcel.writeInt(sequenceNumber);
		 parcel.writeLong(docCount);
		
	}
	
	public static final Creator<Order> CREATOR = new Creator<Order>() {
		 public Order createFromParcel(Parcel source) {
			 Order clkInOut = new Order();  
			 
			 clkInOut.id = source.readLong();
			 clkInOut.localid =source.readLong();
			 clkInOut.nm = source.readString();
			 clkInOut.toDate = source.readString();
			 clkInOut.fromDate = source.readString();
			 clkInOut.statusId = source.readLong();
			 clkInOut.alert = source.readString();
			 clkInOut.poNumber = source.readString();
			 clkInOut.invoiceNumber = source.readString();
			 clkInOut.orderTypeId = source.readLong();
			 clkInOut.startTime =source.readLong();
			 clkInOut.endTime = source.readLong();
			 clkInOut.priorityTypeId = source.readLong();
			 clkInOut.primaryWorkerId = source.readString();
			 clkInOut.summary = source.readString();
			 clkInOut.custName = source.readString();
			 clkInOut.customerid = source.readLong();
			 clkInOut.custSiteStreeAdd = source.readString();
			 clkInOut.custSiteSuiteAddress = source.readString();
			 clkInOut.custSiteGeocode = source.readString();
			 clkInOut.contactId = source.readLong();
			 clkInOut.custContactName = source.readString();
			 clkInOut.custContactNumber = source.readString();
			 clkInOut.custSiteId = source.readString();
			 clkInOut.ordeNotes = source.readString();
			 clkInOut.custPartCount = source.readLong();
			 clkInOut.custFormCount = source.readLong();
			 clkInOut.custMetaCount = source.readLong();
			 clkInOut.epochTime = source.readLong();
			 clkInOut.sortindex = source.readLong();;
			 clkInOut.audioCount = source.readLong();
			 clkInOut.sigCount = source.readLong();
			 clkInOut.imgCount = source.readLong();
			 clkInOut.notCount = source.readLong();
			 clkInOut.docCount = source.readLong();
			 clkInOut.type = source.readString();
			 clkInOut.modified = source.readString();
			 clkInOut.est = source.readString();
			 clkInOut.lst = source.readString();
			 clkInOut.flg = source.readString();
			 clkInOut.custypename = source.readString();
			 clkInOut.sitetypename = source.readString();
			 clkInOut.telTypeId = source.readLong();
			 clkInOut.sequenceNumber =source.readInt();
		     return clkInOut;  
		 }

		@Override
		public Order[] newArray(int size) {
			return new Order[size];  
		}
	};

	public long getAssetsCount() {
		return assetsCount;
	}

	public void setAssetsCount(long assetsCount) {
		this.assetsCount = assetsCount;
	}

	public String getDistanceAndTime() {
		return distanceAndTime;
	}

	public void setDistanceAndTime(String distanceAndTime) {
		this.distanceAndTime = distanceAndTime;
	}

	public double getDistanceForSkip() {
		return distanceForSkip;
	}

	public void setDistanceForSkip(double distanceForSkip) {
		this.distanceForSkip = distanceForSkip;
	}

	public long getDistanceFmOrderX_Id() {
		return distanceFmOrder;
	}

	public void setDistanceFmOrder(long distanceFmOrder) {
		this.distanceFmOrder = distanceFmOrder;
	}

	public long getTimeFmOrder() {
		return timeFmOrder;
	}

	public void setTimeFmOrder(long timeFmOrder) {
		this.timeFmOrder = timeFmOrder;
	}
}