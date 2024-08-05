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

public class OrderMedia extends DataObject implements RespCBandServST{

	//xml parent element idetifier
	public final static String ORDER_PARENT_TAG = "fmeta";
	    
	public final static String ID = "id";
	public final static String ORDER_ID = "oid";
	public final static String ORDER_GEO = "geo";
	public final static String ORDER_MEDIA_TYPE = "tid";
	public final static String ORDER_FILE_DATA = "file";
	public final static String ORDER_FILE_DESC = "dtl";
	public final static String ORDER_FILE_MIME_TYPE = "mime";
	public final static String ORDER_META_UPDATE_TIME = "upd";
	public final static String ORDER_TIMESTAMP = "stmp";
	public final static String ORDER_META_PATH = "metapath";
	public final static String ORDER_FILE_NM = "fname";
	public final static String NEW_ORDER_MEDIA = "0";
	public final static String FORM_KEY = "frmkey";
	public final static String FORM_FIELD_ID = "frmfldid";

	private String frmkey = "";
	private String frmfiledid = "";
	private long id = 0;
	private long orderid = 0;
	private long localid = -1;
	private long localOid = -1;
	private String geocode;
	private int mediatype;
	private byte[] data;
	private String file_desc;
	private String mimetype;
	private long upd_time;
	private String file;
	private String metapath;
	private String modify = "";
	private String fileName = "";
	public final static int TYPE_SAVE = 110;
	public final static int TYPE_DELETE = 111;
	public final static int TYPE_UPDATE = 112;
	
	public final static int TYPE_PUBNUB_UPDATE = 19;

	public final static String ACTION_MEDIA_SAVE = "savefile";
	public final static String ACTION_MEDIA_DELETE = "deletefile";
	public final static String ACTION_GET_MEDIA = "getfilemeta";
	public final static String ACTION_GET_MEDIA_DATA = "getfile";
	public final static String ACTION_META_UPDATE_TYPE = "metaupdate";
	
	static OrderMedia orderMedia = new OrderMedia();
	static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();

	public String getFormKey() {return frmkey;}

	public void setFrmkey(String frmkey) {
		this.frmkey = frmkey;
	}

	public String getFrmfiledid() {return frmfiledid;}

	public void setFrmfiledid(String frmfiledid) {
		this.frmfiledid = frmfiledid;
	}

	public long getOrderid() {
		return orderid;
	}
	public void setOrderid(long orderid) {
		this.orderid = orderid;
	}
	public long getLocalOid() {
		return localOid;
	}
	public void setLocalOid(long localOid) {
		this.localOid = localOid;
	}
	public String getGeocode() {
		return geocode;
	}
	public void setGeocode(String geocode) {
		this.geocode = geocode;
	}
	public int getMediatype() {
		return mediatype;
	}
	public void setMediatype(int mediatype) {
		this.mediatype = mediatype;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getFile_desc() {
		return file_desc;
	}
	public void setFile_desc(String file_desc) {
		this.file_desc = file_desc;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getLocalId() {
		return localid;
	}
	public void setLocalId(long l) {
		this.localid = l;
	}
	public long getUpd_time() {
		return upd_time;
	}
	public void setUpd_time(long upd_time) {
		this.upd_time = upd_time;
	}
	public String getMetapath() {
		return metapath;
	}
	public void setMetapath(String metapath) {
		this.metapath = metapath;
	}
	public String getModify() {
		return modify;
	}
	public void setModify(String modify) {
		this.modify = modify;
	}
	
	public static void getData(RequestObject reqObj, RespCBandServST Activityobj,RespCBandServST callbackObj ,int reqId)
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
		requestHandler.ServiceStarterLoc(Activityobj, intent,orderMedia, currentMilli);
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
	public int getObjectDataStore(RequestObject reqObj, RespCBandServST Activityobj,
			RespCBandServST callbackObj ,int reqId)
	{
		if (orderPicsXmlStore!=null){
			RespCBandServST callBackOBJ = objForCallbkWithId.get(reqId);
			Response response = makeResponseObject(reqId,orderPicsXmlStore);
			callBackOBJ.setResponseCBActivity(response);
			return 1;
		}
		else {
			getData(reqObj,Activityobj,callbackObj ,reqId);
			return 2;
		}
	}
	
	public static void saveData(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj ,int reqId)
	{
		getData(reqObj,Activityobj,callbackObj,reqId);
	}
	public static void deleteData(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj ,int reqId)
	{
		getData(reqObj,Activityobj,callbackObj,reqId);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
