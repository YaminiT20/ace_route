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

public class Parts extends DataObject implements RespCBandServST {
	
	//xml parent element idetifier
	public final static String PART_PARENT_TAG = "ptype";

	public final static String PART_TYPE_ID = "id";
	public final static String PART_TYPE_NAME = "nm";
	public final static String PART_TYPE_DESC = "dtl";
	public final static String PART_TYPE_CTID = "ctid";
	
	public final static int TYPE = 6;
	public final static int TYPE_UPDATE = 18;
	
	public final static String ACTION_PART_TYPE = "getparttype";
	public final static String ACTION_PART_UPDATE_TYPE = "partupdate";
	static Parts parts = new Parts();
	static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();
	
	private long id;
	private String name;
	private String desc;
	private String ctid;;

	public String getCtid() {return ctid;}
	public void setCtid(String ctid) {this.ctid = ctid;}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
        mBundle.putString(AceRouteService.KEY_ACTION, "getparttype");

        intent.putExtras(mBundle);  
		requestHandler.ServiceStarterLoc(Activityobj, intent , parts, currentMilli);
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
			RespCBandServST Activityobj, RespCBandServST callbackObj , int reqId) {
		if (partTypeXmlDataStore!=null){
			RespCBandServST callBackOBJ = objForCallbkWithId.get(reqId);
			Response response = makeResponseObject(reqId,partTypeXmlDataStore);
			callBackOBJ.setResponseCBActivity(response);
			return 1;
		}
		else {
			getData(reqObj,Activityobj,callbackObj ,reqId);
			return 2;
		}
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
