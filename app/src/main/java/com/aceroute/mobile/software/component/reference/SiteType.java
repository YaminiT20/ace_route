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

public class SiteType extends DataObject implements RespCBandServST {
		//xml parent element idetifier
		public final static String SITETYPE_PARENT_TAG = "gtype"; // YD earler ltype

		public final static String SITE_TYPE_ID = "id";
		public final static String SITE_TYPE_NAME = "nm";
		public final static String SITE_TYPE_TID = "tid";
		public final static String SITE_TYPE_CAP = "cap";
		public final static String SITE_TYPE_XID = "xid";
		public final static String SITE_TYPE_UPD = "upd";
		public final static String SITE_TYPE_BY = "by";
		public final static String SITE_TYPE_dtl = "dtl";


		//public final static int TYPE = 15;
		public final static int TYPE = 24;
		
		public final static String ACTION_SITE_TYPE = "getgentype";  // YD earlier:getsitetype
		
		static SiteType siteType = new SiteType();
		static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();
		
		private long id;
		private String nm;
		private int tid;
		private String cap;
		private int xid;
		private long upd;
		private String dtl;
		private String by;

		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public String getNm() {
			return nm;
		}
		public void setNm(String nm) {
			if(nm != null && nm.length() > 30){
				nm = nm.subSequence(0, 29).toString();
			}
			this.nm = nm;
		}

		public String getCap() {
			return cap;
		}
		public void setCap(String cap) {
			this.cap = cap;
		}
		public int getTid() {
			return tid;
		}
		public void setTid(int tid) {
			this.tid = tid;
		}
		public int getXid() {
			return xid;
		}
		public void setXid(int xid) {
			this.xid = xid;
		}
		public long getUpd() {
			return upd;
		}
		public void setUpd(long upd) {
			this.upd = upd;
		}
		public String getBy() {
			return by;
		}
		public void setBy(String by) {
			this.by = by;
		}
	public String getDtl() {
		return dtl;
	}
	public void setDtl(String dtl) {
		this.dtl = dtl;
	}
		
		public static void getData(RequestObject reqObj, RespCBandServST Activityobj,
				RespCBandServST callbackObj ,int reqId)
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
	        mBundle.putString(AceRouteService.KEY_ACTION, "getgentype");// YD earlier :getsitetype

	        intent.putExtras(mBundle);  
			requestHandler.ServiceStarterLoc(Activityobj, intent,siteType, currentMilli);
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
			if (siteTypeXmlDataStore!=null){
				Response response = makeResponseObject(reqId,siteTypeXmlDataStore);
				callbackObj.setResponseCBActivity(response);
				return 1;
			}
			else {
				getData(reqObj,Activityobj, callbackObj ,reqId);
				return 2;
			}
		}
}
