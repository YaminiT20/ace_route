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

public class OrderPart extends DataObject implements RespCBandServST {
		//xml parent element idetifier
		public final static String ORDER_PARENT_TAG = "oprt";

		public final static String ORDER_ID = "oid";
		public final static String ORDER_PART_ID = "id";
	    public final static String ORDER_BARCODE = "sku";
		public final static String PART_TID = "tid";
		public final static String ORDER_PART_QTY = "qty";
		public final static String ORDER_UPDATE_TIME = "upd";
		public final static String ORDER_TIMESTAMP = "stmp";
		
		public final static String NEW_ORDER_PART = "0";
		
		public final static int TYPE = 103;
		public final static int TYPE_PB = 18;
		
		public final static String ACTION_SAVE_ORDER_PART = "saveorderpart";
		// for future use
		public final static String ACTION_DELETE_ORDER_PART = "deleteorderpart";
		public final static String ACTION_UPDATE_ORDER_PART = "updateorderpart";
		public static final String ACTION_GET_ORDER_PART = "getorderpart";
		
		static OrderPart orderPart = new OrderPart();
		static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();
		
		private long oid=0;
		
		private long order_part_id=0;
		private long localid=-1;
		private long localOid=-1;
		private long part_type_id;

	public String getPart_barcode() {
		return part_barcode;
	}

	public void setPart_barcode(String part_barcode) {
		this.part_barcode = part_barcode;
	}

	private String part_barcode;
		private String order_part_QTY;
		private long upd_time=0;
		private String modified = "";



		public long getOid() {
			return oid;
		}
		public void setOid(long id) {
			this.oid = id;
		}
		public long getOrder_part_id() {
			return order_part_id;
		}
		public void setOrder_part_id(long order_part_id) {
			this.order_part_id = order_part_id;
		}
		public long getLocalId() {
			return localid;
		}
		public void setLocalId(long l) {
			this.localid = l;
		}
		
		public long getLocalOid() {
			return localOid;
		}
		public void setLocalOid(long localOid) {
			this.localOid = localOid;
		}
		
		public long getPart_type_id() {
			return part_type_id;
		}
		public void setPart_type_id(long part_id) {
			this.part_type_id = part_id;
		}
		public String getOrder_part_QTY() {
			return order_part_QTY;
		}
		public void setOrder_part_QTY(String order_part_QTY) {
			this.order_part_QTY = order_part_QTY;
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
			requestHandler.ServiceStarterLoc(Activityobj , intent ,orderPart,currentMilli);
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
