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

public class OrderStatus extends DataObject implements RespCBandServST {

	public final static String STATUS_TYPE_STAT = "stat";
	public final static String STATUS_ID = "id";
	public final static String STATUS_TYPE_ISGRP = "isgrp";
	public final static String STATUS_TYPE_GRPSEQ = "grpseq";
	public final static String STATUS_TYPE_GRPID = "grpid";
	public final static String STATUS_TYPE_SEQ = "seq";
	public final static String STATUS_TYPE_NM = "nm";
	public final static String STATUS_TYPE_ABR = "abr";
	public final static String STATUS_TYPE_ISVIS = "isvis";

	public final static String ACTION_STATUS_TYPE = "getstatuslist";//YD using for server request

	public final static String ACTION_GET_STATUS = "getorderstatus";

	/*public final static String ORDER_PARENT_TAG = "orderstatus";
	public final static String ORDER_ID = "orderstatus_id";
	public final static String ORDER_NAME = "orderstatus_name";*///YD using these when fetching hardcoded status list_cal
	
	static OrderStatus orderStatus = new OrderStatus();
	static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();
	
	private long id =1;
	private String nm = null;

	int isgroup;
	int grpSeq;
	int grpId;
	int seq;
	String abbrevation;
	int isVisible;
	private String modified = "";


	public String getModified() {
		return modified;
	}
	public void setModified(String modified) {
		this.modified = modified;
	}
	
	
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
		this.nm = nm;
	}

	public int getIsgroup() {
		return isgroup;
	}

	public void setIsgroup(int isgroup) {
		this.isgroup = isgroup;
	}

	public int getGrpSeq() {
		return grpSeq;
	}

	public void setGrpSeq(int grpSeq) {
		this.grpSeq = grpSeq;
	}

	public int getGrpId() {
		return grpId;
	}

	public void setGrpId(int grpId) {
		this.grpId = grpId;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getAbbrevation() {
		return abbrevation;
	}

	public void setAbbrevation(String abbrevation) {
		this.abbrevation = abbrevation;
	}

	public int getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(int isVisible) {
		this.isVisible = isVisible;
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
        mBundle.putString(AceRouteService.KEY_ACTION, "getstatustype");

        intent.putExtras(mBundle);  
		requestHandler.ServiceStarterLoc(Activityobj, intent,orderStatus ,currentMilli);
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
		if (orderStatusTypeXmlDataStore!=null){
			Response response = makeResponseObject(reqId,orderStatusTypeXmlDataStore);
			callbackObj.setResponseCBActivity(response);
			return 1;
		}
		else {
			getData(reqObj,Activityobj, callbackObj ,reqId);
			return 2;
		}
	}
}
