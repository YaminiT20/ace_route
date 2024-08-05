package com.aceroute.mobile.software.component;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.aceroute.mobile.software.AceRouteService;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.http.RequestObject;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.network.AceRequestHandler;
import com.aceroute.mobile.software.utilities.PreferenceHandler;

import java.util.HashMap;




public class OrderTaskOld extends DataObject implements RespCBandServST {
	
	//xml parent element idetifier
//	public final static String ORDER_PARENT_TAG = "osrv";
	
	
	
	public final static String ORDER_ID = "oid";
	public final static String ORDER_TASK_ID = "id";
	public final static String ORDER_TASK_TYPE_ID = "order_task_type_id";
	public final static String ORDER_TASK_RATE = "order_task_RATE";
//	public final static String ORDER_UPDATE_TIME = "upd_time";
	public final static String ORDER_TIMESTAMP = "stmp";
		
	public final static String NEW_ORDER_TASKS = "0";
	
	public final static int TYPE = 1010;
	
	public final static String ACTION_SAVE_ORDER_TASK_OLD = "saveordertaskold";
	public final static String ACTION_DELETE_ORDER_TASK_OLD = "deleteordertaskold";
	public final static String ACTION_UPDATE_ORDER_TASK_OLD = "updateordertaskold";
	public static final String ACTION_GET_ORDER_TASK_OLD = "getordertaskold";
	
	public final static String ORDER_SERV_UPDATE_TIME = "upd";
	
	static OrderTaskOld orderTask = new OrderTaskOld();
	static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();
	
	
	private long oid;
	private long order_task_id;
	private long order_task_type_id;
	private String order_task_RATE;
	private long upd_time;
	private String modified = "";
	
	
	public long getOid() {
		return oid;
	}
	
	public void setOid(long id) {
		this.oid = id;
	}
	
	public long getOrder_task_id() {
		return order_task_id;
	}
	
	public void setOrder_task_id(long order_task_id) {
		this.order_task_id = order_task_id;
	}
	
	public long getTask_type_id() {
		return order_task_type_id;
	}
	
	public void setTask_type_id(long task_id) {
		this.order_task_type_id = task_id;
	}
	
	public String getOrder_task_RATE() {
		return order_task_RATE;
	}
	
	public void setOrder_task_RATE(String order_task_RATE) {
		this.order_task_RATE = order_task_RATE;
	}
	
	public long getUpd_time() {
		return upd_time;
	}
	
	public void setUpd_time(long upd_time) {
		this.upd_time = upd_time;
	}
	
	public String getModified() {
		return modified;
	}
	
	public void setModified(String modified) {
		this.modified = modified;
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
        mBundle.putString(AceRouteService.KEY_ACTION, reqObj.getAction());

        intent.putExtras(mBundle);  
		requestHandler.ServiceStarterLoc(Activityobj , intent, orderTask, currentMilli);
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
		// TODO Auto-generated method stub
		if (response!=null)
		{
			RespCBandServST callBackOBJ = objForCallbkWithId.get(response.getId());
			objForCallbkWithId.remove(response.getId());
			callBackOBJ.setResponseCBActivity(response);
		}
	}

	@Override
	public int getObjectDataStore(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj, int reqId) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public static void saveData(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj, int reqId)
	{
		getData(reqObj,Activityobj,callbackObj,reqId);
	}
	
	public static void deleteData(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj, int reqId)
	{
		getData(reqObj,Activityobj,callbackObj,reqId);
	}
}

