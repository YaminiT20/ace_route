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

public class CheckinOut extends DataObject implements RespCBandServST{
	
	//xml parent element idetifier

	public final static String CLOCK_IN = "1";
	public final static String CLOCK_OUT = "0";
	
	public final static String CC_ACTION = "savetcrd";

	public final static String CC_TID = "tid";
	public final static String CC_GEO = "geo";
	public final static String CC_TSTAMP = "stmp";
	public final static String CC_LSTOID = "lstoid";
	public final static String CC_NXTOID = "nxtoid";
	public final static int CC_ACTIONBGSYNC = 99;
	
	
	static CheckinOut checkInOut = new CheckinOut();
	static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getGeo() {
		return geo;
	}
	public void setGeo(String geo) {
		this.geo = geo;
	}
	public long getStmp() {
		return stmp;
	}
	public void setStmp(long stmp) {
		this.stmp = stmp;
	}
	public String getModify() {
		return modify;
	}
	public void setModify(String modify) {
		this.modify = modify;
	}

	public String getLstoid() {
		return lstoid;
	}

	public void setLstoid(String lstoid) {
		this.lstoid = lstoid;
	}

	public String getNxtoid() {
		return nxtoid;
	}

	public void setNxtoid(String nxtoid) {
		this.nxtoid = nxtoid;
	}

	private String action;
	private String tid;
	private String geo;
	private String lstoid;
	private String nxtoid;
	private long stmp;
	private String modify="";

	
	public static void getData(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj ,int reqId)
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
        mBundle.putString(AceRouteService.KEY_ACTION, reqObj.getAction());

        intent.putExtras(mBundle);  
		requestHandler.ServiceStarterLoc(Activityobj, intent,checkInOut, currentMilli);
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
			RespCBandServST Activityobj, RespCBandServST callbackObj, int reqId) {
		// TODO Auto-generated method stub
		return 0;
	}

}
