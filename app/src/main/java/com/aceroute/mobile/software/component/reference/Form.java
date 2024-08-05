package com.aceroute.mobile.software.component.reference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;

import com.aceroute.mobile.software.AceRouteService;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.http.RequestObject;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.network.AceRequestHandler;
import com.aceroute.mobile.software.utilities.PreferenceHandler;

import java.util.HashMap;

public class Form extends DataObject implements RespCBandServST {

    public final static String FORM_PARENT_TAG = "frm";
    public final static String FORM_ID = "id";
    public final static String FORM_TID = "ftid";
    public final static String FORM_NM = "nm";
    public final static String FORM_OID = "oid";
    public final static String FORM_FDATA = "fdata";
    public final static String FORM_STMP = "stmp";
    public final static String FORM_UPD = "upd";
    public static final String FORM_KEY_ONLY = "frmkey";
    public final static String ACTION_SAVE_FORM = "saveorderform";
    // for future use
    public final static int TYPE = 168;
    public final static int PUBNUB_TYPE = 17;
    public final static int DELETE_TYPE = 164;
    public static final String ACTION_GET_ORDER_FORM = "getorderform";
    public static final String ACTION_DELETE_ORDER_FORM = "deleteorderform";


    public long getOid() {
        return oid;
    }

    public void setOid(long oid) {
        this.oid = oid;
    }

    public long oid;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFtid() {
        return ftid;
    }

    public void setFtid(String ftid) {
        this.ftid = ftid;
    }

    public String getNm() {
        return nm;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public String getFdata() {
        return fdata;
    }

    public void setFdata(String fdata) {
        this.fdata = fdata;
    }

    public long getStmp() {
        return stmp;
    }

    public void setStmp(long stmp) {
        this.stmp = stmp;
    }

    public String getFormkeyonly() {
        return formkeyonly;
    }

    public void setFormkeyonly(String formkeyonly) {
        this.formkeyonly = formkeyonly;
    }

    public long id ;
    public String ftid;
    public String nm = null;
    public String fdata;
    public long stmp;
    public long upd;
    private String modified = "";
    private long localid=-1;
    private long localOid=-1;
    public String formkeyonly;

    static Form formObj = new Form();
    static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();

    public static void getData(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj, int reqId) {
        objForCallbkWithId.put(reqId, callbackObj);

        AceRequestHandler requestHandler = null;
        Intent intent = null;
        Long currentMilli = null;

        if (Activityobj instanceof Activity) {
            requestHandler = new AceRequestHandler(((Activity) Activityobj).getApplicationContext());
            intent = new Intent(((Activity) Activityobj).getApplicationContext(), AceRouteService.class);
            currentMilli = PreferenceHandler.getPrefQueueRequestId(((Activity) Activityobj).getApplicationContext());
        } else if (Activityobj instanceof FragmentActivity) {
            requestHandler = new AceRequestHandler(((FragmentActivity) Activityobj).getApplicationContext());
            intent = new Intent(((FragmentActivity) Activityobj).getApplicationContext(), AceRouteService.class);
            currentMilli = PreferenceHandler.getPrefQueueRequestId(((FragmentActivity) Activityobj).getApplicationContext());
        }

        Bundle mBundle = new Bundle();
        mBundle.putParcelable("OBJECT", reqObj);
        mBundle.putLong(AceRouteService.KEY_TIME, currentMilli);
        mBundle.putInt(AceRouteService.KEY_SYNCALL_FLAG, AceRouteService.VALUE_NOT_SYNCALL);
        mBundle.putInt(AceRouteService.FLAG_FOR_CAMERA, 0);
        mBundle.putInt(AceRouteService.KEY_ID, reqId);
        mBundle.putString(AceRouteService.KEY_ACTION, reqObj.getAction());

        intent.putExtras(mBundle);
        requestHandler.ServiceStarterLoc(Activityobj, intent, formObj, currentMilli);
    }


    @Override
    public int getObjectDataStore(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj, int reqId) {
        return 0;
    }

    @Override
    public void ServiceStarter(RespCBandServST activity, Intent intent) {

    }

    @Override
    public void setResponseCallback(String response, Integer reqId) {

    }

    @Override
    public void setResponseCBActivity(Response response) {
        if (response != null) {
            RespCBandServST callBackOBJ = objForCallbkWithId.get(response.getId());
            objForCallbkWithId.remove(response.getId());
            Log.d("User", response.getId() + "");
            callBackOBJ.setResponseCBActivity(response);
        }
    }

    public static void saveData(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj, int reqId) {
        getData(reqObj, Activityobj, callbackObj, reqId);
    }

    public static void deleteData(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj, int reqId) {
        getData(reqObj, Activityobj, callbackObj, reqId);
    }


    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public long getLocalOid() {
        return localOid;
    }

    public void setLocalOid(long localOid) {
        this.localOid = localOid;
    }

    public long getLocalid() {
        return localid;
    }

    public void setLocalid(long localid) {
        this.localid = localid;
    }

    public long getUpd() {
        return upd;
    }

    public void setUpd(long upd) {
        this.upd = upd;
    }
}
