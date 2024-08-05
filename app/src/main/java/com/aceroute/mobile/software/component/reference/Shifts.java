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
import java.util.regex.Pattern;

/**
 * Created by xelium on 21/11/16.
 */


public class Shifts extends DataObject implements RespCBandServST {

    public final static String SHIFT_PARENT_TAG = "shf";
    public final static String SHIFT_SEND_TAG = "recs";
    public static final String ACTION_GET_SHIFTS = "getshift";
    public static final String ACTION_SYNC_SHIFTS = "worker.shift.save";
    public static final String ACTION_DELETE_SHIFTS = "deleteshift";
    public static final String ACTION_EDIT_SHIFTS = "editshift";
    public final static String SHIFT_ID = "id";
    public final static String SHIFT_TID = "tid";
    public final static String SHIFT_NM = "nm";
    public final static String SHIFT_ADR = "adr";
    public final static String SHIFT_BRKSLOT = "brkslot";
    public final static String SHIFT_TMSLOT = "tmslot";
    public final static String SHIFT_TERRI = "terrgeo";
    public final static String SHIFT_DT = "dt";
    public final static String SHIFT_LID = "lid";
    public final static String SHIFT_CRT = "crt";
    public final static String SHIFT_GACC = "gacc";
    public static final String DELETE_MULTIPLE_SHIFTS = "worker.shift.delete";


    public final static String ACTION_SAVE_SHIFTS = "saveshifts";
    // for future use
    public final static int TYPE = 163;
    public final static int PUBNUB_TYPE = 30;
    public final static int DELETE_TYPE = 164;
    public long id = 0;
    public long tid = 0;
    public String nm = null;
    public String tmslot;
    public String terrgeo;
    public String dt;
    public long lid;
    public long localid = -1;
    public String brkSlot;
    public String adr;
    public int sMin = 0;
    public int eMin = 0;
    public String stime = null;
    public String etime = null;
    public String sbreaktime = null;
    public String ebreaktime = null;

    public long getGacc() {
        return gacc;
    }

    public void setGacc(long gacc) {
        this.gacc = gacc;
    }

    public long gacc = 0;

    public String getTerri() {
        return terrgeo;
    }

    public void setTerri(String terrgeo) {
        this.terrgeo = terrgeo;
    }

    public String modified = "";

    static Shifts shiftObj = new Shifts();
    static HashMap<Integer, RespCBandServST> objForCallbkWithId = new HashMap<Integer, RespCBandServST>();

    public void setId(long id) {
        this.id = id;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public void setTmslot(String tmslot) {
        this.tmslot = tmslot;
        settimevalue(tmslot);
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public void setLid(long lid) {
        this.lid = lid;
    }

    public void setLocalid(long localid) {
        this.localid = localid;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public void setBrkSlot(String brkSlot) {
        this.brkSlot = brkSlot;
        setBreaktimevalue(brkSlot);
    }

    private void setBreaktimevalue(String brkSlot) {
        String hrarr[];
        if (brkSlot.contains("|"))
            hrarr = brkSlot.split(Pattern.quote("|"));
        else
            hrarr = brkSlot.split(Pattern.quote(","));
        if (!hrarr[0].trim().equals("")) {
            sMin = Integer.parseInt(hrarr[0]);
            int min = sMin % 60;
            int hr = (sMin - min) / 60;
            String st = "am";
            if (hr > 12) {
                hr = hr - 12;
                st = "pm";
            }
            sbreaktime = hr + ":" + (min > 9 ? min : "0" + min) + " " + st;

            eMin = Integer.parseInt(hrarr[1]);
            min = eMin % 60;
            hr = (eMin - min) / 60;
            String st1 = "am";
            if (hr > 12) {
                hr = hr - 12;
                st1 = "pm";
            }
            ebreaktime = hr + ":" + (min > 9 ? min : "0" + min) + " " + st1;
        }
    }


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
        requestHandler.ServiceStarterLoc(Activityobj, intent, shiftObj, currentMilli);
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

    public String getStime() {
        return stime;
    }

    public String getEtime() {
        return etime;
    }

    public String getBrkStime() {
        return sbreaktime;
    }

    public String getBrkEtime() {
        return ebreaktime;
    }

    public void setAddress(String address) {
        this.adr = address;
    }

    void settimevalue(String tm) {
        String hrarr[];
        if (tm.contains("|"))
            hrarr = tm.split(Pattern.quote("|"));
        else
            hrarr = tm.split(Pattern.quote(","));
        if (!hrarr[0].trim().equals("")) {
            sMin = Integer.parseInt(hrarr[0]);
            int min = sMin % 60;
            int hr = (sMin - min) / 60;
            String st = "am";
            if (hr > 12) {
                hr = hr - 12;
                st = "pm";
            }
            stime = hr + ":" + (min > 9 ? min : "0" + min) + " " + st;

            eMin = Integer.parseInt(hrarr[1]);
            min = eMin % 60;
            hr = (eMin - min) / 60;
            String st1 = "am";
            if (hr > 12) {
                hr = hr - 12;
                st1 = "pm";
            }
            etime = hr + ":" + (min > 9 ? min : "0" + min) + " " + st1;
        }
    }

    public String getdt() {
        return dt;
    }
}
