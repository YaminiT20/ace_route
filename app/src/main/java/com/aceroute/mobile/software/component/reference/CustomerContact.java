
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

public class CustomerContact extends DataObject implements RespCBandServST  {
	
	//xml parent element identifier
	public final static String CUSTOMERCONTACT_PARENT_TAG = "cnt";
	
	public final static String CONTACT_ID = "id";
	public final static String CONTACT_NAME = "nm";
	public final static String CUSTOMER_ID = "cid";
	public final static String CONTACT_TEL = "tel";
	public final static String CONTACT_EML = "eml";
	public final static String CONTACT_TYPE = "ttid";
	
	public final static String ACTION_CONTACT_EDIT = "editcontact";
	public final static String ACTION_GET_CONTACT = "getcontact";
	public final static String ACTION_SAVE_CONTACT= "savecontact";
	public final static int TYPE_EDIT = 152;
	public final static String NEW_ORDER_CONTACT = "0";
	public final static int TYPE = 12;
	public final static int TYPE_PUBNUB = 12;
	
	static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();
	static CustomerContact odrCustContact = new CustomerContact();

	private long id;
	private long customerid;
	private String contactname;
	private String contacttel;
	private long contactType;
	private String modify;
	private long localid;

	private String eml;

	public String getModify() {
		return modify;
	}
	public void setModify(String modify) {
		this.modify = modify;
	}
	public long getContactType() {
		return contactType;
	}
	public void setContactType(long contactType) {
		this.contactType = contactType;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getCustomerid() {
		return customerid;
	}
	public void setCustomerid(long customerid) {
		this.customerid = customerid;
	}
	public String getContactname() {
		return contactname;
	}
	public void setContactname(String contactname) {
		this.contactname = contactname;
	}
	public String getContacttel() {
		return contacttel;
	}
	public void setContacttel(String contacttel) {
		this.contacttel = contacttel;
	}
	public long getLocalid() {
		return localid;
	}

	public void setLocalid(long localid) {
		this.localid = localid;
	}

	public String getContactEml() {
		return eml;
	}

	public void setContactEml(String eml) {
		this.eml = eml;
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
		requestHandler.ServiceStarterLoc(Activityobj, intent,odrCustContact, currentMilli);
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
