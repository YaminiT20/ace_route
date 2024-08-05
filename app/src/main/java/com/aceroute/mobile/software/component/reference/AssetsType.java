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

/**
 * Created by xelium on 6/2/16.
 */
public class AssetsType extends DataObject implements RespCBandServST {

    public final static String ASSETSTYPE_PARENT_TAG = "atype";

    public final static String ASSETS_TYPE_ID = "id";
    public final static String ASSETS_TYPE_NAME = "nm";
    public final static String ASSETS_TYPE_XID = "xid";
    public final static String ASSETS_TYPE_UPD = "upd";

    public final static int TYPE_PUBNUB_ASSET_TYPE = 28;
    public final static String ACTION_GET_ASSETS_TYPE = "getassettype";//Yd this is asset type which we are getting through call as sync and refresh


    long id;
    String name;
    long xid;
    long upd;

    static AssetsType assetObj = new AssetsType();
    static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();


    public long getUpd() {
        return upd;
    }

    public void setUpd(long upd) {
        this.upd = upd;
    }

    public long getXid() {
        return xid;
    }

    public void setXid(long xid) {
        this.xid = xid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void ServiceStarter(RespCBandServST activity, Intent intent) {

    }

    @Override
    public void setResponseCallback(String response, Integer reqId) {

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
    public static void getData(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj ,int reqId)
    {
        objForCallbkWithId.put(reqId, callbackObj);

        AceRequestHandler requestHandler=null;
        Intent intent = null;
        Long currentMilli = null;

        if (Activityobj instanceof Activity){
            requestHandler = new AceRequestHandler(((Activity)Activityobj).getApplicationContext());
            intent = new Intent(((Activity)Activityobj).getApplicationContext(),AceRouteService.class);
            currentMilli = PreferenceHandler.getPrefQueueRequestId(((Activity) Activityobj).getApplicationContext());
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
        mBundle.putString(AceRouteService.KEY_ACTION, "getassettype");

        intent.putExtras(mBundle);
        requestHandler.ServiceStarterLoc(Activityobj , intent ,assetObj,currentMilli);
    }


    @Override
    public int getObjectDataStore(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj, int reqId) {
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
