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

public class OrderTask extends DataObject implements RespCBandServST{
	
	//xml parent element idetifier
	public final static String ORDER_PARENT_TAG = "osrv";

	public final static String ORDER_ID = "oid";
	public final static String ORDER_TASK_ID = "id";
	//public final static String ORDER_TASK_TYPE_ID = "ordertask_typeid";
	public final static String TASK_ID = "tid";
	public final static String TRIM_TYPE = "ltid";
	public final static String PRIORITY = "pid";
	public final static String ACTION_STATUS = "stat";
	public final static String TREE_OWNER = "note5";
	public final static String TREE_HT = "num1";
	public final static String TREE_DIA = "num2";
	public final static String TREE_CLEARENCE = "num3";
	public final static String TREE_CYCLE = "num4";
	public final static String TREE_EXPCOUNT = "ehrs";
	public final static String TREE_ACTUALCOUNT = "hrs";
	public final static String TREE_TIMESPENT = "num5";
	public final static String TREE_TM = "num6";
	public final static String TREE_MSC = "note6";
	public final static String TREE_COMMENT = "note1";
	public final static String TREE_PCOMMENT = "note2";
	public final static String TREE_ALERT = "note3";
	public final static String TREE_NOTE = "note4";
	public final static String TREE_GEO = "geo";
	public final static String TREE_ACCESSPATH = "glist";
	public final static String TREE_CT1 = "ct1";
	public final static String TREE_CT2 = "ct2";
	public final static String TREE_CT3 = "ct3";
	public final static String ORDER_TIMESTAMP = "stmp";
	public final static String ORDER_END_TIME = "orderEndTime";
	public final static String ORDER_END_DATE = "orderEndDate";
	
	public final static String NEW_ORDER_TASK = "0";
	public final static String ORDER_SERV_UPDATE_TIME = "upd";
	
	public final static int TYPE = 101;
	
	public final static String ACTION_SAVE_ORDER_TASK = "saveordertask";
	// for future use
	public final static String ACTION_DELETE_ORDER_TASK = "deleteordertask";
	public final static String ACTION_UPDATE_ORDER_TASK = "updateordertask";
	public static final String ACTION_GET_ORDER_TASK = "getordertask";
	public static final String ACTION_GET_ALL_ORDER_TASK = "getallordertask";
	
	static OrderTask orderTask = new OrderTask();
	static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();
	
	private long id;
	private long order_task_id;//YD saving id
	private long localid=-1;
	private long localOid=-1;
	private long order_task_type_id;
	private long task_id;//YD saving tid
	private String order_timestamp;
	private long upd_time;
	private String modified = "";
	private String tree_type="";
	private String priority="";
	private String action_status="";
	private String tree_owner="";
	private String tree_ht="";
	private String tree_dia="";
	private String tree_clearence="";
	private String tree_cycle="";
	private String tree_expcount="";
	private String tree_actualcount="";
	private String tree_timespent="";
	private String tree_tm="";
	private String tree_msc="";
	private String tree_comment="";
	private String tree_pcomment="";
	private String tree_alert="";
	private String tree_note="";
	private String tree_geo="";
	private String tree_accesspath = "";
	private String tree_ct1 = "";
	private String tree_ct2 = "";
	private String tree_ct3 = "";
	private String orderEndTime="";
	private String orderEndDate="";
	public String getTree_type() {
		return tree_type;
	}
	public void setTree_type(String tree_type) {
		this.tree_type = tree_type;
	}
	public long getLocalOid() {
		return localOid;
	}
	public void setLocalOid(long localOid) {
		this.localOid = localOid;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getAction_status() {
		return action_status;
	}
	public void setAction_status(String action_status) {
		this.action_status = action_status;
	}
	public String getTree_owner() {
		return tree_owner;
	}
	public void setTree_owner(String tree_owner) {
		this.tree_owner = tree_owner;
	}
	public String getTree_ht() {
		return tree_ht;
	}
	public void setTree_ht(String tree_ht) {
		this.tree_ht = tree_ht;
	}
	public String getTree_dia() {
		return tree_dia;
	}
	public void setTree_dia(String tree_dia) {
		this.tree_dia = tree_dia;
	}
	public String getTree_clearence() {
		return tree_clearence;
	}
	public void setTree_clearence(String tree_clearence) {
		this.tree_clearence = tree_clearence;
	}
	public String getTree_cycle() {
		return tree_cycle;
	}
	public void setTree_cycle(String tree_cycle) {
		this.tree_cycle = tree_cycle;
	}
	public String getTree_expcount() {
		return tree_expcount;
	}
	public void setTree_expcount(String tree_expcount) {
		this.tree_expcount = tree_expcount;
	}
	public String getTree_actualcount() {
		return tree_actualcount;
	}
	public void setTree_actualcount(String tree_actualcount) {
		this.tree_actualcount = tree_actualcount;
	}
	public String getTree_timespent() {
		return tree_timespent;
	}
	public void setTree_timespent(String tree_timespent) {
		this.tree_timespent = tree_timespent;
	}
	public String getTree_tm() {
		return tree_tm;
	}
	public void setTree_tm(String tree_tm) {
		this.tree_tm = tree_tm;
	}
	public String getTree_msc() {
		return tree_msc;
	}
	public void setTree_msc(String tree_msc) {
		this.tree_msc = tree_msc;
	}
	public String getTree_comment() {
		return tree_comment;
	}
	public void setTree_comment(String tree_comment) {
		this.tree_comment = tree_comment;
	}
	public String getTree_pcomment() {
		return tree_pcomment;
	}
	public void setTree_pcomment(String tree_pcomment) {
		this.tree_pcomment = tree_pcomment;
	}
	public String getTree_alert() {
		return tree_alert;
	}
	public void setTree_alert(String tree_alert) {
		this.tree_alert = tree_alert;
	}
	public String getTree_note() {
		return tree_note;
	}
	public void setTree_note(String tree_note) {
		this.tree_note = tree_note;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrder_task_id() {
		return order_task_id;
	}
	public void setOrder_task_id(long order_task_id) {
		this.order_task_id = order_task_id;
	}
	public long getLocalId() {
		return localid;
	}
	public void setLocalId(long l) {
		this.localid = l;
	}
	public long getOrder_task_type_id() {
		return order_task_type_id;
	}
	public void setOrder_task_type_id(long order_task_type_id) {
		this.order_task_type_id = order_task_type_id;
	}
	public long getTask_id() {
		return task_id;
	}
	public void setTask_id(long task_id) {
		this.task_id = task_id;
	}
	public String getOrder_timestamp() {
		return order_timestamp;
	}
	public void setOrder_timestamp(String order_timestamp) {
		this.order_timestamp = order_timestamp;
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
	public String getTree_geo() {
		return tree_geo;
	}
	public void setTree_geo(String tree_geo) {
		this.tree_geo = tree_geo;
	}
	public String getTree_accesspath() {
		return tree_accesspath;
	}
	public void setTree_accesspath(String tree_accesspath) {
		this.tree_accesspath = tree_accesspath;
	}
	public String getTree_ct1() {
		return tree_ct1;
	}
	public void setTree_ct1(String tree_ct1) {
		this.tree_ct1 = tree_ct1;
	}
	public String getTree_ct2() {
		return tree_ct2;
	}
	public void setTree_ct2(String tree_ct2) {
		this.tree_ct2 = tree_ct2;
	}
	public String getTree_ct3() {
		return tree_ct3;
	}
	public void setTree_ct3(String tree_ct3) {
		this.tree_ct3 = tree_ct3;
	}

	public String getOrderEndTime() {
		return orderEndTime;
	}
	public void setOrderEndTime(String orderEndTime) {
		this.orderEndTime = orderEndTime;
	}
	public String getOrderEndDate() {
		return orderEndDate;
	}
	public void setOrderEndDate(String orderEndDate) {
		this.orderEndDate = orderEndDate;
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
		requestHandler.ServiceStarterLoc(Activityobj , intent ,orderTask,currentMilli);
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
	public static void deleteData(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj ,int reqId)
	{
		getData(reqObj,Activityobj,callbackObj,reqId);
	}
	
}
