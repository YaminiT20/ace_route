package com.aceroute.mobile.software;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.OrderNotes;
import com.aceroute.mobile.software.component.reference.AssetsType;
import com.aceroute.mobile.software.component.reference.ClientSite;
import com.aceroute.mobile.software.component.reference.Customer;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.OrderPriority;
import com.aceroute.mobile.software.component.reference.OrderStatus;
import com.aceroute.mobile.software.component.reference.OrderTypeList;
import com.aceroute.mobile.software.component.reference.Parts;
import com.aceroute.mobile.software.component.reference.ServiceType;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.component.reference.Worker;
import com.aceroute.mobile.software.fragment.OrderListMainFragment;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.network.AceRequestHandler;
import com.aceroute.mobile.software.requests.CommonSevenReq;
import com.aceroute.mobile.software.requests.updateOrderRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;

import java.util.HashMap;

public class CommonServManager implements RespCBandServST {

	public static final int GET_ORDER_REQ=1;
	public static final int SYNC_OFFLINE_DATA=2;
	public static final int GET_CUST_REQ=3;
	public static final int GET_ORDER_TYPE_REQ=4;
	public static final int GET_PARTS_REQ=5;
	public static final int GET_RES_REQ=6;
	public static final int GET_TASKS_REQ=7;
	public static final int GET_ORDER_STATUS_REQ=8;




	public static final int GET_PRIORITY_REQ=9;
	public static final int GET_SITE_TYPE_REQ=10;
	public static final int GET_ALL_TASK_REQ=11;
	public static final int GET_ALL_SITE_REQ=12;
	public static final int GET_ALL_NOTE_REQ=13;
	public static final int GET_ASSET_TYPE=14;
	public static final int GET_COMPANY_CLI_TYPE=15;


	Context mcontext ;
	private static int reqID;
	RespCBandServST actAndFragInst;
	RespCBandServST callBkInst;
	
	public CommonServManager(Context context,RespCBandServST actAndFragInst, RespCBandServST callBkInst) {
		mcontext =context ;
		this.actAndFragInst = actAndFragInst;
		this.callBkInst = callBkInst;
	}
	
	public void loadRefData(String source , int reqId){
		BaseTabActivity.bootLoadedItems = 0;
		OrderListMainFragment.datarefresh= true;
		reqID = reqId;

		CommonSevenReq CommonReqObj = new CommonSevenReq();
		CommonReqObj.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(mcontext)+"/mobi");
		CommonReqObj.setSource(source);
		
		// set action for each request here it self; //to make the get data generic
		//if(PreferenceHandler.getUiconfigAddorder(mcontext).equals("1"))
		Customer.getData(CommonReqObj, actAndFragInst , this, GET_CUST_REQ);

		OrderTypeList.getData(CommonReqObj,actAndFragInst , this , GET_ORDER_TYPE_REQ);
		Parts.getData(CommonReqObj,actAndFragInst , this, GET_PARTS_REQ);
		Worker.getData(CommonReqObj,actAndFragInst , this, GET_RES_REQ);
		ServiceType.getData(CommonReqObj,actAndFragInst ,this, GET_TASKS_REQ);
		OrderStatus.getData(CommonReqObj,actAndFragInst ,this ,GET_ORDER_STATUS_REQ);
		OrderPriority.getData(CommonReqObj,actAndFragInst ,this, GET_PRIORITY_REQ);
		SiteType.getData(CommonReqObj,actAndFragInst , this, GET_SITE_TYPE_REQ);
		getAllTasks_sites("localonly", GET_ALL_TASK_REQ, "getallordertask");
		getAllTasks_sites("localonly", GET_ALL_SITE_REQ,"getallsite"); // YD have to comment later because now we are getting all sites on map start/load
		getAllOrderNotes("getordernotes");
		AssetsType.getData(CommonReqObj,actAndFragInst,this,GET_ASSET_TYPE);
		ClientSite.getData(CommonReqObj,actAndFragInst,this,GET_COMPANY_CLI_TYPE);

	 }
	
	private void getAllTasks_sites(String source, int reqId,String action) {
		AceRequestHandler requestHandler=null;
		Intent intent = null;
		
		CommonSevenReq CommonReqObj = new CommonSevenReq();
		CommonReqObj.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(mcontext)+"/mobi");
		CommonReqObj.setSource(source);
		
		Long currentMilli = PreferenceHandler.getPrefQueueRequestId(mcontext);
		
		requestHandler = new AceRequestHandler(mcontext);
		intent = new Intent(mcontext,AceRouteService.class);
		 
		Bundle mBundle = new Bundle();
        mBundle.putParcelable("OBJECT", CommonReqObj);  
        mBundle.putLong(AceRouteService.KEY_TIME, currentMilli);
        mBundle.putInt(AceRouteService.KEY_SYNCALL_FLAG, AceRouteService.VALUE_NOT_SYNCALL);
        mBundle.putInt(AceRouteService.FLAG_FOR_CAMERA, 0);
        mBundle.putInt(AceRouteService.KEY_ID, reqId);
        mBundle.putString(AceRouteService.KEY_ACTION, action);

        intent.putExtras(mBundle);  
		requestHandler.ServiceStarterLoc(actAndFragInst, intent, this, currentMilli);
		
	}

	private void getAllOrderNotes(String action)
	{
		updateOrderRequest req = new updateOrderRequest();
		req.setId("0");
		req.setAction(action);

		OrderNotes.getData(req, actAndFragInst, this, GET_ALL_NOTE_REQ);

	}
	
	
	private void checkBootflow() {
		if(BaseTabActivity.bootLoadedItems==BaseTabActivity.maxBootLoadItems)
		{
			Response res = new Response();
			res.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
			res.setId(reqID);
			res.setStatus("success");
			callBkInst.setResponseCBActivity(res);   
		}
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
			if (response.getStatus().equals("success")&& 
					response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED)))
			{
				if (response.getId()==GET_CUST_REQ)
				{
					DataObject.customerXmlDataStore = response.getResponseMap();
					 if(BaseTabActivity.bootLoadedItems<BaseTabActivity.maxBootLoadItems){
						 BaseTabActivity.bootLoadedItems+=1;
					 	checkBootflow();
					 }
				}
				else if (response.getId()==GET_ORDER_TYPE_REQ)
				{
					 DataObject.orderTypeXmlDataStore = response.getResponseMap();
					 if(BaseTabActivity.bootLoadedItems<BaseTabActivity.maxBootLoadItems){
						 BaseTabActivity.bootLoadedItems+=1;
					 	checkBootflow();
					 }
				}
				else if (response.getId()==GET_PARTS_REQ)
				{
					// memchache the data here got in response
					 DataObject.partTypeXmlDataStore = response.getResponseMap();

					 if(BaseTabActivity.bootLoadedItems<BaseTabActivity.maxBootLoadItems){
						 BaseTabActivity.bootLoadedItems+=1;
					 	checkBootflow();
					 }
				}
				else if (response.getId()==GET_RES_REQ)
				{
					 DataObject.resourceXmlDataStore = response.getResponseMap();
					if ( DataObject.resourceXmlDataStore !=null)
						 PreferenceHandler.setWorkerNm(mcontext,((HashMap<Long, Worker>) DataObject.resourceXmlDataStore).get(PreferenceHandler.getResId(mcontext)).getNm());

					 if(BaseTabActivity.bootLoadedItems<BaseTabActivity.maxBootLoadItems){
						 BaseTabActivity.bootLoadedItems+=1;
					 	checkBootflow();
					 }
				}
				else if (response.getId()==GET_TASKS_REQ)
				{
					DataObject.taskTypeXmlDataStore = response.getResponseMap();
					 if(BaseTabActivity.bootLoadedItems<BaseTabActivity.maxBootLoadItems){
						 BaseTabActivity.bootLoadedItems+=1;
					 	checkBootflow();
					 }
				}
				else if (response.getId()==GET_ORDER_STATUS_REQ)
				{
					 DataObject.orderStatusTypeXmlDataStore = response.getResponseMap();
					 if(BaseTabActivity.bootLoadedItems<BaseTabActivity.maxBootLoadItems){
						 BaseTabActivity.bootLoadedItems+=1;
					 	checkBootflow();
					 }
				}
				else if (response.getId()==GET_PRIORITY_REQ)
				{
					 DataObject.orderPriorityTypeXmlDataStore = response.getResponseMap();
					 if(BaseTabActivity.bootLoadedItems<BaseTabActivity.maxBootLoadItems){
						 BaseTabActivity.bootLoadedItems+=1;
					 	checkBootflow();
					 }
				}
				else if (response.getId()==GET_ASSET_TYPE)
				{
					DataObject.assetsTypeXmlDataStore = response.getResponseMap();
					/*if(BaseTabActivity.bootLoadedItems<BaseTabActivity.maxBootLoadItems){
						BaseTabActivity.bootLoadedItems+=1;
						checkBootflow();
					}*/
				}
				else if (response.getId()==GET_COMPANY_CLI_TYPE)
				{
					DataObject.compClientSiteTypeXmlDataStore = response.getResponseMap();
					/*if(BaseTabActivity.bootLoadedItems<BaseTabActivity.maxBootLoadItems){
						BaseTabActivity.bootLoadedItems+=1;
						checkBootflow();
					}*/
				}
				else if (response.getId()==GET_SITE_TYPE_REQ)
				{
					DataObject.siteTypeXmlDataStore = response.getResponseMap();
					 if(BaseTabActivity.bootLoadedItems<BaseTabActivity.maxBootLoadItems){
						 BaseTabActivity.bootLoadedItems+=1;
					 	checkBootflow();
					 }
				}
				else if (response.getId()==GET_ALL_TASK_REQ)
				{
					// memchache the data here got in response
					 if(BaseTabActivity.bootLoadedItems<BaseTabActivity.maxBootLoadItems){
						 BaseTabActivity.bootLoadedItems+=1;
					 	checkBootflow();
					 }
				}
				else if (response.getId()==GET_ALL_SITE_REQ)
				{
					// memchache the data here got in response
					 if(BaseTabActivity.bootLoadedItems<BaseTabActivity.maxBootLoadItems){
						 BaseTabActivity.bootLoadedItems+=1;
					 	checkBootflow();
					 }
				}
				else if (response.getId()==GET_ALL_NOTE_REQ)
				{
					// memchache the data here got in response
					DataObject.orderNoteDataStore = response.getResponseMap();
				}
			}	
			else if(response.getStatus().equals("success")&& 
						response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
			  {
					if ((response.getId()==GET_ORDER_TYPE_REQ)||
							(response.getId()==GET_PARTS_REQ)||(response.getId()==GET_RES_REQ)||
							(response.getId()==GET_TASKS_REQ)||(response.getId()==GET_ORDER_STATUS_REQ)||
							(response.getId()==GET_PRIORITY_REQ)||(response.getId()==GET_SITE_TYPE_REQ)){
						
						Log.i("AceRoute", "LoadRefData Failed");
						
						Response res = new Response();
						res.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_DATA));
						res.setId(reqID);
						res.setStatus("failure");
						callBkInst.setResponseCBActivity(res);
					}	
					
					else if (response.getId()==GET_ALL_TASK_REQ || response.getId()==GET_CUST_REQ)
					{
						 if(BaseTabActivity.bootLoadedItems<BaseTabActivity.maxBootLoadItems){
							 BaseTabActivity.bootLoadedItems+=1;
						 	checkBootflow();
						 }
					}
					else if (response.getId()==GET_ALL_SITE_REQ)
					{
						 if(BaseTabActivity.bootLoadedItems<BaseTabActivity.maxBootLoadItems){
							 BaseTabActivity.bootLoadedItems+=1;
						 	checkBootflow();
						 }
					}
			  	}
			}
		}
	
	

}
