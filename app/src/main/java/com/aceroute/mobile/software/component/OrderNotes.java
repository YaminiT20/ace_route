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

public class OrderNotes extends DataObject implements RespCBandServST {

	public final static String ORDER_PARENT_TAG = "onotes";
	public final static String NOTES_ORDER_ID = "oid";
	public final static String ORDER_ID = "id";
	public final static String ORDER_NOTE = "onotes";
	public final static String ORDER_NOTE_FIELDNAME = "note";
	
	//using at the time of saveorderfield for save note.
	public final static String ORDER_NOTE_VALUE = "value";
	public final static String ORDER_NOTE_FIELD_NAME = "name";

	public final static String ORDER_CUSTOMER_SITE_C_GEOCODE = "cgeo";
	public static final String ORDER_TMSTAMP = "stmp";

	private long id = 0;
	private String ordernote;
	
	static OrderNotes orderNote = new OrderNotes();
	static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();
	//public static long hashNoteKey =1;
	
	public final static int TYPENOTE =13;
	
	public final static int TYPE_SAVE = 120;
	public final static int TYPE_DELETE = 121;
	public final static int TYPE_UPDATE = 122;
	
	public final static String ACTION_NOTES_SAVE = "saveordernotes";
	public final static String ACTION_NOTES_DELETE = "deleteordernotes";
	public final static String ACTION_GET_NOTES = "getordernotes";
	private String modified = "";
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getOrdernote() {
		return ordernote;
	}
	public void setOrdernote(String ordernote) {
		this.ordernote = ordernote;
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
		requestHandler.ServiceStarterLoc(Activityobj , intent ,orderNote,currentMilli);
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
}
