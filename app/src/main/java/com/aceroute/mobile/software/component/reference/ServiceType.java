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

public class ServiceType extends DataObject implements RespCBandServST{
	
	//xml parent element idetifier
	public final static String SERVICETYPE_PARENT_TAG = "stype";

	public final static String SERVICE_TYPE_ID = "id";
	public final static String SERVICE_TYPE_NAME = "nm";
	public final static String SERVICE_TYPE_DTL = "dtl";
	public final static String SERVICE_TYPE_CMT = "no";

	public final static int TYPE = 7;
	public final static int TYPE_UPDATE = 17;
	
	public final static String ACTION_SERVICE_TYPE = "gettasktype";
	public final static String ACTION_SERVICE_UPDATE_TYPE = "serviceupdate";
	static ServiceType serviceType = new ServiceType();
	static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();
	
	private long id;
	private String nm;

	private String dtl;
	private String cmt;

	public String getCmt() {
		return cmt;
	}

	public void setCmt(String cmt) {
		this.cmt = cmt;
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
        mBundle.putString(AceRouteService.KEY_ACTION, "gettasktype");

        intent.putExtras(mBundle);  
		requestHandler.ServiceStarterLoc(Activityobj, intent,serviceType, currentMilli);
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
		if (taskTypeXmlDataStore!=null){
			RespCBandServST callBackOBJ = objForCallbkWithId.get(reqId);
			Response response = makeResponseObject(reqId,taskTypeXmlDataStore);
			callBackOBJ.setResponseCBActivity(response);
			return 1;
		}
		else {
			getData(reqObj,Activityobj,callbackObj ,reqId);
			return 2;
		}
	}

	public String getDtl() {
		return dtl;
	}

	public void setDtl(String dtl) {
		this.dtl = dtl;
	}
}
