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

public class SegmentModel  extends DataObject implements RespCBandServST {

    static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();
    static SegmentModel odrSegment = new SegmentModel();

    private long segmentId;

    public String getSegmentNumber() {
        return segmentNumber;
    }

    public void setSegmentNumber(String segmentNumber) {
        this.segmentNumber = segmentNumber;
    }

    private String segmentNumber;

    public long getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(long segmentId) {
        this.segmentId = segmentId;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    private String segmentName;

    public static void getData(RequestObject reqObj, RespCBandServST Activityobj , RespCBandServST callbackObj ,int reqId)
    {

        objForCallbkWithId.put(reqId, callbackObj);

        AceRequestHandler requestHandler=null;
        Intent intent = null;
        Long currentMilli = null;

        if (Activityobj instanceof Activity){
            requestHandler = new AceRequestHandler(((Activity)Activityobj).getApplicationContext());
            intent = new Intent(((Activity)Activityobj).getApplicationContext(), AceRouteService.class);
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
        requestHandler.ServiceStarterLoc(Activityobj, intent,odrSegment, currentMilli);
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

    @Override
    public int getObjectDataStore(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj, int reqId) {
        return 0;
    }

}
