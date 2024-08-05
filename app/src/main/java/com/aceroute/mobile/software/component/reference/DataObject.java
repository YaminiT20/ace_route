package com.aceroute.mobile.software.component.reference;

import android.util.Log;

import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.http.RequestObject;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.utilities.ServiceError;

import java.util.HashMap;

public abstract class DataObject {

	public static final String CACHE_ORDER_DATA="odr_data";
	public static final String CACHE_CUST_LST_DATA="cust_data";
	public static final String CACHE_ORDER_TYPE_DATA="order_type_data";
	public static final String CACHE_PARTS_DATA="part_data";
	public static final String CACHE_RES_DATA="res_data";
	public static final String CACHE_TASKS_DATA="task_data";
	public static final String CACHE_ORDER_STATUS_DATA="status_data";// no need as we have in local YD
	public static final String CACHE_PRIORITY_DATA="priority_data";
	public static final String CACHE_SITE_TYPE_DATA="site_type";
	public static final String CACHE_ALL_TASK_DATA="all_task";
	public static final String CACHE_ALL_SITE_DATA="all_site";
	
	
	public static Object ordersXmlDataStore =null;
	public static Object customerXmlDataStore=null;
	public static Object partTypeXmlDataStore=null;
	public static Object resourceXmlDataStore=null;
	public static Object taskTypeXmlDataStore=null;
	public static Object siteTypeXmlDataStore=null;
	public static Object orderTypeXmlDataStore=null;
	public static Object orderStatusTypeXmlDataStore=null;
	public static Object orderPriorityTypeXmlDataStore=null;
	public static Object assetsTypeXmlDataStore=null;

	public static Object compClientSiteTypeXmlDataStore=null;
	//public static Object compShiftTypeXmlDataStore=null;

	public static Object orderNoteDataStore = null; // YD using for data store for notes

	// Data storage for particular order 
	public static Object orderTasksXmlStore = null;
	public static Object orderPartsXmlStore = null;
	public static Object orderFormsXmlStore = null;
	public static Object orderPicsXmlStore = null;
	
	//public abstract void getData(String source, RespCBandServST Activityobj,int reqId);
	
	public abstract int getObjectDataStore(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj, int reqId);
	
	public void memCacheData(HashMap<Long, Object> objToMemCache , String type){
		
	}
	
	public Response makeResponseObject(int reqId, Object XmlDataStore) {

		Log.d("xmldatastore",""+XmlDataStore);

		Response response = new Response();
		response.setId(reqId);
		response.setResponseMap(XmlDataStore);
		response.setStatus("success");
		response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
		
		return response;
	}
	
}
