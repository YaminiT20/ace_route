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
 * Created by YD on 5/18/16.
 */
public class Assets extends DataObject implements RespCBandServST {


    public final static String ASSET_PARENT_TAG = "ast";

    public final static String ASSET_ID = "id";///
    public final static String ASSET_CID = "cid";///
    public final static String ASSET_GEO = "geo";
    public final static String ASSET_FTYPE_ID = "ftid";///
    public final static String ASSET_FDATA = "fdata";///
    public final static String ASSET_UPD = "upd";///

   /* public final static String ASSET_TYPE_ID2 = "tid2";
    public final static String ASSET_PID = "pid";
    public final static String ASSET_STAT = "stat";
    public final static String ASSET_CONT1 = "cnt1";
    public final static String ASSET_CONT2 = "cnt2";
    public final static String ASSET_NUM1 = "num1";
    public final static String ASSET_NUM2 = "num2";
    public final static String ASSET_NUM3 = "num3";
    public final static String ASSET_NUM4 = "num4";
    public final static String ASSET_NUM5 = "num5";
    public final static String ASSET_NUM6 = "num6";
    public final static String ASSET_NOTE1 = "note1";
    public final static String ASSET_NOTE2 = "note2";
    public final static String ASSET_NOTE3 = "note3";
    public final static String ASSET_NOTE4 = "note4";
    public final static String ASSET_NOTE5 = "note5";
    public final static String ASSET_NOTE6 = "note6";
    public final static String ASSET_CT1 = "ct1";
    public final static String ASSET_CT2 = "ct2";
    public final static String ASSET_CT3 = "ct3";*/

    //Extras
    public final static String ASSET_OID = "oid";
    public final static String ASSET_TSTMP = "stmp";

    public final static int TYPE_PUBNUB = 29;// YD this is asset which is coming in customer xml
    public final static String ACTION_GET_ASSETS = "getasset";

    public final static String ACTION_SAVE_ORDER_ASSETS = "saveasset";
    public final static int TYPE = 155;

    private long id;
    private long cid;
    private String geoLoc = "";
    private long ftid;
    private String fdata;
    private long upd;

    /*private long tid2;
    private long pid;
    private String status = "";
    private long contact1;
    private long contact2;
    private long number1;
    private long number2;
    private long number3;
    private long number4;
    private long number5;
    private long number6;
    private String note1 = "";
    private String note2 = "";
    private String note3 = "";
    private String note4 = "";
    private String note5 = "";
    private String note6 = "";
    private String ct1 = "";
    private String ct2 = "";
    private String ct3 = "";*/
    private String modify = "";
    private long localid;

    //Extras
    private long Oid;
    private long TimeStmp;

    public final static String NEW_ORDER_ASSET = "0";

    static Assets assetObj = new Assets();
    static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public String getGeoLoc() {
        return geoLoc;
    }

    public void setGeoLoc(String geoLoc) {
        this.geoLoc = geoLoc;
    }

    public String getModify() {
        return modify;
    }

    public void setUpd(long upd) {
        this.upd = upd;
    }
    public long getUpd() {
        return upd;
    }

    public void setModify(String modify) {
        this.modify = modify;
    }

    public long getLocalid() {
        return localid;
    }

    public void setLocalid(long localid) {
        this.localid = localid;
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
        mBundle.putString(AceRouteService.KEY_ACTION, reqObj.getAction());

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

    public long getOid() {
        return Oid;
    }

    public void setOid(long oid) {
        Oid = oid;
    }

    public long getTimeStmp() {
        return TimeStmp;
    }

    public void setTimeStmp(long timeStmp) {
        TimeStmp = timeStmp;
    }

    public long getFtid() {
        return ftid;
    }

    public void setFtid(long ftid) {
        this.ftid = ftid;
    }

    public String getFdata() {
        return fdata;
    }

    public void setFdata(String fdata) {
        this.fdata = fdata;
    }
}
