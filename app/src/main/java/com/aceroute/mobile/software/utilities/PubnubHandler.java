package com.aceroute.mobile.software.utilities;

import android.util.Log;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.PubnubRequest;

public class PubnubHandler {
	
	static PubnubHandler pbHandle;
	private static BaseTabActivity uihandle;
	
	public static PubnubHandler getInstance()
	{
		if (pbHandle==null){
			pbHandle = new PubnubHandler();
		}
		return pbHandle;
	}

	public void setResponse(Response response, PubnubRequest reqDataObject) {
		if (uihandle!= null && reqDataObject!=null && response.getStatus().equals("success"))
		{
			int objectType = Integer.valueOf(reqDataObject.getObjtypeforpn());
			int actionType = reqDataObject.getObjecttype();
			String additionalData = reqDataObject.getXml();
			/*if( objectType == Order.TYPE || objectType == ServiceType.TYPE_UPDATE || objectType == Parts.TYPE_UPDATE || objectType == OrderMedia.TYPE_PUBNUB_UPDATE || objectType == Customer.TYPE){// YD TODO check if this if statement required else delete it 
				if( actionType == DBHandler.DB_ACTION_DELETE || actionType == 0){
					uihandle.callbackpncb(objectType,actionType,additionalData,response);
				}else{
					if (uihandle!=null)
						uihandle.callbackpncb(objectType,actionType,additionalData,response);
				}
			}*/
			uihandle.callbackpncb(objectType,actionType,additionalData,response);
		}
		else if (reqDataObject!=null && response.getStatus().equals("failure"))
		{
			Log.i("AceRoute", "Pubnub Process fail for : objectType="+reqDataObject.getObjtypeforpn()+" Action"+
					reqDataObject.getObjecttype());
		}
		else {
			Log.i("AceRoute", "Pubnub Process fail for some reason");
		}
		
	}
	
	public static void setUiHandle(BaseTabActivity basetab)
	{
		uihandle= basetab;
	}

}
