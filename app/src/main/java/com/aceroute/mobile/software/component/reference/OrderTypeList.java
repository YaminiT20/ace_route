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

public class OrderTypeList extends DataObject implements RespCBandServST{
	
	//xml parent element idetifier
	public final static String ORDERTYPE_PARENT_TAG = "otype";

	public final static String ORDER_TYPE_ID = "id";
	public final static String ORDER_TYPE_NAME = "nm";
	
	public final static int TYPE = 5;
	
	public static final String ACTION_ORDER_TYPE = "getordertype";
	static OrderTypeList orderTypeList= new OrderTypeList();
	static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();
	
	private long id;
	private String nm;
	
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
        mBundle.putString(AceRouteService.KEY_ACTION, "getordertype");

        intent.putExtras(mBundle);  
		requestHandler.ServiceStarterLoc(Activityobj, intent, orderTypeList,currentMilli);
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
		if (orderTypeXmlDataStore!=null){
			Response response = makeResponseObject(reqId,orderTypeXmlDataStore);
			callbackObj.setResponseCBActivity(response);
			return 1;
		}
		else {
			getData(reqObj,Activityobj,callbackObj ,reqId);
			return 2;
		}
	}
}
