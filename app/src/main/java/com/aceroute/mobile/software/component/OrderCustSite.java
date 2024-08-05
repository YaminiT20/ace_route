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

public class OrderCustSite extends DataObject implements RespCBandServST {
	
	private String cust_location="";
	private String cust_siteid="";
	private String cust_id="";
	private String cust_name="";
	private String cust_adr="";
	private String cust_upd="";
	private String cust_dtl="";
	private String cust_tid="";
	private String cust_ltpnm="";
	private String cust_geo="";
	
	static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();
	static OrderCustSite orderCustSite = new OrderCustSite();
	
	public String getCust_location() {
		return cust_location;
	}
	public void setCust_location(String cust_location) {
		this.cust_location = cust_location;
	}
	public String getCust_siteid() {
		return cust_siteid;
	}
	public void setCust_siteid(String cust_siteid) {
		this.cust_siteid = cust_siteid;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getCust_name() {
		return cust_name;
	}
	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}
	public String getCust_adr() {
		return cust_adr;
	}
	public void setCust_adr(String cust_adr) {
		this.cust_adr = cust_adr;
	}
	public String getCust_upd() {
		return cust_upd;
	}
	public void setCust_upd(String cust_upd) {
		this.cust_upd = cust_upd;
	}
	public String getCust_dtl() {
		return cust_dtl;
	}
	public void setCust_dtl(String cust_dtl) {
		this.cust_dtl = cust_dtl;
	}
	public String getCust_tid() {
		return cust_tid;
	}
	public void setCust_tid(String cust_tid) {
		this.cust_tid = cust_tid;
	}
	public String getCust_ltpnm() {
		return cust_ltpnm;
	}
	public void setCust_ltpnm(String cust_ltpnm) {
		this.cust_ltpnm = cust_ltpnm;
	}
	public String getCust_geo() {
		return cust_geo;
	}
	public void setCust_geo(String cust_geo) {
		this.cust_geo = cust_geo;
	}
	
	public static void getData(RequestObject reqObj, RespCBandServST Activityobj,int reqId)
	{
		
		objForCallbkWithId.put(reqId, Activityobj);
		
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
		requestHandler.ServiceStarterLoc(Activityobj, intent,orderCustSite, currentMilli);
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
			RespCBandServST Activityobj,RespCBandServST callbackObj , int reqId) {
		// TODO Auto-generated method stub
		return 0;
	}

}
