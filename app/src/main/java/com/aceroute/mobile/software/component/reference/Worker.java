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

public class Worker extends DataObject implements RespCBandServST{
	
	//xml parent element idetifier
	public final static String WORKER_PARENT_TAG = "res";
	
	public final static String WORKER_ID = "id";
	public final static String WORKER_NAME = "nm";
	public final static String WORKER_LID = "lid";
	public final static String WORKER_WEEK = "wrkwk";
	public final static String WORKER_DWEEK = "dwrkwk";
	public final static String WORKER_BRK = "brk";
	public final static String WORKER_SHIFT = "shftid";
	public final static String WORKER_TID = "tid";
	public final static String WORKER_VHLID = "vhlid";

	public final static int TYPE = 9;
	public final static int PUBNUB_TYPE = 54;
	public final static int PUBNUB_TYPE_NEW = 58;

	public final static String ACTION_WORKER_LIST = "getres";
	public final static String ACTION_WORKER_LOGOUT = "reslogout";
	public final static String ACTION_WORKER_MLOGOUT_SERVER = "mlogout";
	public final static String ACTION_WORKER_LOGOUT_PUBNUB = "reslogoutPubnub";

	static Worker worker = new Worker();
	static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();
	
	private long id;
	private String nm;
	private long lid;
	private String wrkwk;
	private String dwrkwk;
	private String brk;
	private int workerShift;
	private int tid=1;
	private String vehicleIdList;


	public String getVehicleId() {
		return vehicleIdList;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleIdList = vehicleId;
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
	public long getLid() {
		return lid;
	}
	public void setLid(long lid) {
		this.lid = lid;
	}
	public String getWrkwk() {
		return wrkwk;
	}
	public void setWrkwk(String wrkwk) {
		this.wrkwk = wrkwk;
	}
	public String getDwrkwk() {
		return dwrkwk;
	}
	public void setDwrkwk(String dwrkwk) {
		this.dwrkwk = dwrkwk;
	}
	public String getBrk() {
		return brk;
	}
	public void setBrk(String brk) {
		this.brk = brk;
	}
	public int getWorkerShift() {
		return workerShift;
	}
	public void setWorkerShift(int workerShift) {
		this.workerShift = workerShift;
	}
	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public static void getData(RequestObject reqObj, RespCBandServST Activityobj,
							   RespCBandServST callbackObj , int reqId)
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
        mBundle.putString(AceRouteService.KEY_ACTION, ACTION_WORKER_LIST);

        intent.putExtras(mBundle);  
		requestHandler.ServiceStarterLoc(Activityobj, intent,worker, currentMilli);
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
			RespCBandServST Activityobj,
			RespCBandServST callbackObj ,int reqId) {
		if (resourceXmlDataStore!=null){
			RespCBandServST callBackOBJ = objForCallbkWithId.get(reqId);
			Response response = makeResponseObject(reqId,customerXmlDataStore);
			callBackOBJ.setResponseCBActivity(response);
			return 1;
		}
		else {
			getData(reqObj,Activityobj, callbackObj ,reqId);
			return 2;
		}
	}


}
