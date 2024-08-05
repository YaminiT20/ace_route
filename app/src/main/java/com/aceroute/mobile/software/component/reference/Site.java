package com.aceroute.mobile.software.component.reference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.aceroute.mobile.software.AceRouteService;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.http.RequestObject;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.network.AceRequestHandler;
import com.aceroute.mobile.software.utilities.PreferenceHandler;

import java.util.HashMap;

public class Site extends DataObject implements RespCBandServST  {
		//xml parent element idetifier
		public final static String SITE_PARENT_TAG = "loc";
		
		public final static String SITE_ID = "id";
		public final static String SITE_CID = "cid";
		public final static String SITE_NAME = "nm";
		public final static String SITE_ADDRESS = "adr";
		public final static String SITE_ADR2 = "adr2";
		public final static String SITE_ST = "st";
		public final static String SITE_UPD = "upd";
		public final static String SITE_TSTMP = "tstamp";
		public final static String SITE_GEO = "geo";
		public final static String SITE_TYPE_ID = "tid";
		public final static String SITE_TYPE_NM = "ltpnm";
		public final static String SITE_DTL = "dtl";
		public final static int TYPE = 11;
		
		public final static String ACTION_SITE = "getsite";
		public final static String ACTION_SAVE_SITE = "savesite";
		public final static String ACTION_GET_ALL_SITE = "getallsite";
		public final static String ACTION_DELETE_SITE = "deletesite";
		
		public final static String ACTION_EDIT_SITE = "editsite";
		
		static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();
		static Site odrCustSite = new Site();
		
		private long id;
		private long cid;
		private String nm;
		private String adr;
		private String adr2;
		private String geo;
		private String detail;

		private long tid;
		private String sitetypenm;
		private String modify = "";
		private long telNo;
		private long ttypeid;
		private long localid;
		private String st;
		private long upd;
		private String email;

		public long getTtypeid() {
			return ttypeid;
		}
		public void setTtypeid(long ttypeid) {
			this.ttypeid = ttypeid;
		}
		public long getTelNo() {
			return telNo;
		}
		public void setTelNo(long telNo) {
			this.telNo = telNo;
		}
		public String getGeo() {
			return geo;
		}
		public void setGeo(String geo) {
			this.geo = geo;
		}
		public String getAdr2() {
			return adr2;
		}
		public void setAdr2(String adr2) {
			this.adr2 = adr2;
		}
		public String getSt() {
			return st;
		}
		public void setSt(String st) {
			this.st = st;
		}

		
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public long getCid() {
			return cid;
		}
		public void setCid(long cid) {
			this.cid = cid;
		}
		public String getNm() {
			return nm;
		}
		public void setNm(String nm) {
			this.nm = nm;
		}
		public String getAdr() {
			return adr;
		}
		public void setAdr(String adr) {
			this.adr = adr;
		}
		public long getUpd() {
			return upd;
		}
		public void setUpd(long upd) {
			this.upd = upd;
		}
		public String getModify() {
			return modify;
		}
		public void setModify(String modify) {
			this.modify = modify;
		}
		public String getDetail() {
			return detail;
		}
		public void setDetail(String detail) {
			this.detail = detail;
		}
		public long getTid() {
			return tid;
		}
		public void setTid(long tid) {
			this.tid = tid;
		}
		public String getSitetypenm() {
			return sitetypenm;
		}
		public void setSitetypenm(String sitetypenm) {
			this.sitetypenm = sitetypenm;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}


		public static void getData(RequestObject reqObj, RespCBandServST Activityobj , RespCBandServST callbackObj ,int reqId)
		{
			
			objForCallbkWithId.put(reqId, callbackObj);
			
			AceRequestHandler requestHandler=null;
			Intent intent = null;
			Long currentMilli = null;
			
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
	        mBundle.putString(AceRouteService.KEY_ACTION, reqObj.getAction());//getsite
		
	        intent.putExtras(mBundle);  
			requestHandler.ServiceStarterLoc(Activityobj, intent,odrCustSite, currentMilli);
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
				RespCBandServST callBackOBJ = objForCallbkWithId.get(response.getId());
				objForCallbkWithId.remove(response.getId());
				callBackOBJ.setResponseCBActivity(response);
			}
			
		}
		@Override
		public int getObjectDataStore(RequestObject reqObj,
				RespCBandServST Activityobj, RespCBandServST callbackObj ,int reqId) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		public static void saveData(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj ,int reqId)
		{
			getData(reqObj,Activityobj,callbackObj,reqId);
		}

	public static void deleteData(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj ,int reqId)
	{
		getData(reqObj,Activityobj,callbackObj,reqId);
	}

	public long getLocalid() {
		return localid;
	}

	public void setLocalid(long localid) {
		this.localid = localid;
	}
}
