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
 * Created by root on 2/2/18.
 */

public class MessagePanic extends DataObject implements RespCBandServST {

    public final static String MESSAGE_ID = "id";
    public final static String MESSAGE_TYPE_ID = "tid";
    public final static String MESSAGE_ORDER_ID = "oid";
    public final static String MESSAGE_GEO = "geo";
    public final static String MESSAGE_TIMESTAMP = "stmp";
    public final static String MESSAGE_MESSAGE = "msg";

    public final static String ACTION_WORKER_MESSAGE_PANIC = "message";
    public static final int TYPE = 108;

    String action;
    long tid;
    long oid;
    String geo;
    String stmp;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    String message;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public long getOid() {
        return oid;
    }

    public void setOid(long oid) {
        this.oid = oid;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getStmp() {
        return stmp;
    }

    public void setStmp(String stmp) {
        this.stmp = stmp;
    }


    static MessagePanic orderPart = new MessagePanic();
    static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();

    public static void getData(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj , int reqId)
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
