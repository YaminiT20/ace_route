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
 * Created by xelium on 11/18/16.
 */
public class ClientSite extends DataObject implements RespCBandServST {

    public final static String CLIENTSITE_PARENT_TAG = "loc";
    public final static String CLIENTSITE_ID = "id";
    public final static String CLIENTSITE_NAME = "nm";
    public final static String CLIENTSITE_ADR = "adr";
    public final static String CLIENTSITE_ADR2 = "adr2";
    public final static String CLIENTSITE_TZ = "tz";
    public final static String CLIENTSITE_GEO = "geo";
    public final static String CLIENTSITE_XID = "xid";
    public final static String CLIENTSITE_UPD = "upd";
    public final static String CLIENTSITE_BY = "by";

    public static final String ACTION_GET_CLIENT_SITE = "getclientsite" ;


    private long id;
    private String nm;
    private String adr;
    private String adr2;
    private String tz;
    private String geo;
    private long xid;
    private long upd;
    private String by;
    static ClientSite clientsiteObj = new ClientSite();
    static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();


    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

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

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getTz() {
        return tz;
    }

    public void setTz(String tz) {
        this.tz = tz;
    }

    public String getAdr2() {
        return adr2;
    }

    public void setAdr2(String adr2) {
        this.adr2 = adr2;
    }

    public String getAdr() {
        return adr;
    }

    public void setAdr(String adr) {
        this.adr = adr;
    }

    public String getNm() {
        return nm;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }




    @Override
    public int getObjectDataStore(RequestObject reqObj, RespCBandServST Activityobj, RespCBandServST callbackObj, int reqId) {
        return 0;
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
        mBundle.putString(AceRouteService.KEY_ACTION, ACTION_GET_CLIENT_SITE);

        intent.putExtras(mBundle);
        requestHandler.ServiceStarterLoc(Activityobj , intent ,clientsiteObj,currentMilli);
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
}
