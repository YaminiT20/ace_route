package com.aceroute.mobile.software.mangers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Window;

import com.aceroute.mobile.software.async.Enumm;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.http.Api;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.response.BaseResponse;
import com.aceroute.mobile.software.response.Login;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.aceroute.mobile.software.utilities.XMLHandler;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserManager extends Manager implements RespCBandServST {
    public static int a = 100;
    static RespCBandServST activeCbk;
    static String nspace, appType;
    private static boolean shouldTry = true;//yd for using second url to send req
    Context mContext;
    Response resActivity;
    MyDialog dialog;
    private int isRemembered;
    private String password;
    private String rid;
    private int activeReqId;

    public UserManager() {
        // TODO Auto-generated constructor stub
    }


    public UserManager(RespCBandServST callback, Activity activity, String rId, String pssCode, int isRememberUserPassCodeEnabled) {
        this.mContext = activity;
        this.activeCbk = callback;
        this.rid = rId;
        this.password = pssCode;
        this.isRemembered = isRememberUserPassCodeEnabled;
    }

    //action will have the fixed action code
    @Override
    public String DoAction(Map reqmap, int action, int reqType, String URL, File file, int activityReqId, RespCBandServST obj) {
        //Request Json passed in async task
        PostRequest postreq = new PostRequest(reqmap, URL, mContext, this);

        switch (action) {

            //Login
            case 101:
                postreq.execute(reqType, activityReqId);
                break;

            //Register
            case 102:
                postreq.execute(reqType, activityReqId);
                break;

            default:
                break;
        }
        return null;
    }

    public void makeLogin(Context context, String rid, String pcode,
                          String nspace, int isRemembered, String appType, int activeReqId, String Url) throws IOException {
        File f = null;

        this.rid = rid;
        this.activeReqId = activeReqId;
        this.nspace = nspace;
        this.appType = "Offline";
        this.password = pcode;
        this.isRemembered = isRemembered;


        Map<String, String> reqMap = new HashMap<String, String>();

        reqMap.put("nspace", nspace);
        reqMap.put("rid", rid);
        reqMap.put("pcode", pcode);
        reqMap.put("geo", Utilities.getLocation(context));
        reqMap.put("os","2");
        reqMap.put("action", Api.API_ACTION_LOGIN_USER);

        DoAction(reqMap, Enumm.getEnumVal(Enumm.LOGIN_REQUEST), 1, Url, f, activeReqId, this);
    }

    public void hitbase(Context context, String nsp, int activeReqId, String Url) throws IOException {
        File f = null;
        this.activeReqId = activeReqId;
        this.nspace = nsp;
        Map<String, String> reqMap = new HashMap<String, String>();
        Log.e(Utilities.TAG,
                "Internet is connected waiting for login..");
        reqMap.put("tid", "mobi");
        reqMap.put("nsp", nspace);
        //reqMap.put("action", Api.API_ACTION_LOGIN);
        DoAction(reqMap, Enumm.getEnumVal(Enumm.BASE_REQUEST), 1, Url, f, activeReqId, this);
    }

    @Override
    public void ServiceStarter(RespCBandServST activity, Intent intent) {
        // TODO Auto-generated method stub
    }


    @Override
    public void setResponseCallback(String response, Integer reqId) {

        if (response != null) {
            //Login Response
            if (reqId == 1) {
                resActivity = new Response();
                resActivity.setId(reqId);

                XMLHandler handle = new XMLHandler(mContext);
                Object ob = handle.getLoginValuesFromXML(response);
                ArrayList<Object> objlist = new ArrayList<Object>();
                Login login = (Login) ob;

                if (login == null) {
//                    if (Api.SECONDARY && shouldTry) {
//                        shouldTryOtherReqUrl();
//                        return;
//                    }
                    shouldTry = true; // YD after trying both url User can try different login credentials
                    resActivity.setResponseObj(null);
                    resActivity.setStatus("failure");
                    resActivity.setErrorcode(ServiceError.getEnumValstr(ServiceError.LOGIN_FAIL));

                } else if (!(login.getLoginError().equals("0"))) {
//                    if (Api.SECONDARY && shouldTry) {
//                        shouldTryOtherReqUrl();
//                        return;
//                    }

                    shouldTry = true; // YD after trying both url User can try different login credentials
                    resActivity.setResponseObj(null);
                    resActivity.setStatus("failure");
                    resActivity.setErrorcode(login.getLoginError());

                }

                else {

                 /*   if (!shouldTry) {

                        Api.API_BASE_URL = Api.API_BASE_URL_SEC;
                        Api.API_BASE_URL_UPLOAD = Api.API_BASE_URL_UPLOAD_SEC;
                        Api.PUBNUB_SUBSCRIBE_KEY_MTGOX = Api.PUBNUB_SUBSCRIBE_KEY_MTGOX_SEC;
                    }
*/
                    // saved in jsinterface

                    PreferenceHandler.setIsUserLoggedIn(((Activity) activeCbk).getApplicationContext(), true);
                    PreferenceHandler.setResId(((Activity) activeCbk).getApplicationContext(), login.getRid());
                    PreferenceHandler.setPrefTechnameData(((Activity) activeCbk).getApplicationContext(), login.getResnm());
                    PreferenceHandler.setNspId(((Activity) activeCbk).getApplicationContext(), login.getNspid());
                    PreferenceHandler.setMtoken(((Activity) activeCbk).getApplicationContext(), login.getToken());
                    PreferenceHandler.setMapCountry(((Activity) activeCbk).getApplicationContext(), login.getMapctry());
                    PreferenceHandler.setMapState(((Activity) activeCbk).getApplicationContext(), "ca");
                    PreferenceHandler.setCompanyId(((Activity) activeCbk).getApplicationContext(), this.nspace);
                    PreferenceHandler.setPassCode(((Activity) activeCbk).getApplicationContext(), this.password);
                    PreferenceHandler.setTemplate(((Activity) activeCbk).getApplicationContext(), login.getSmstmpl0());
                    PreferenceHandler.setPrefEditionForGeo(((Activity) activeCbk).getApplicationContext(), login.getEdn()/*900*/);
                    Log.e("AceRoute", login.getEdn() + ": value of edn. :)");

                    if (this.isRemembered == 1)

                        PreferenceHandler.setRemember(((Activity) activeCbk).getApplicationContext(), true);

                    else
                        PreferenceHandler.setRemember(((Activity) activeCbk).getApplicationContext(), false);
                    PreferenceHandler.setAppType(((Activity) activeCbk).getApplicationContext(), this.appType);
                    PreferenceHandler.setUiConfig(((Activity) activeCbk).getApplicationContext(), login.getUiConfig());

                    if (login.getUiConfig() != null && !login.getUiConfig().equals("")) {
                        String config[] = login.getUiConfig().split("\\|");
                        for (int i = 0; i < config.length; i++) {
                            if (i == 0) {
                                if (config[i].equals("1"))
                                    PreferenceHandler.setFormConfig(((Activity) activeCbk).getApplicationContext(), "1");
                                else
                                    PreferenceHandler.setFormConfig(((Activity) activeCbk).getApplicationContext(), config[i]);
                            }
                            if (i == 2){
                                if (config[i].equals("1"))
                                    PreferenceHandler.setAssetsConfig(((Activity) activeCbk).getApplicationContext(), "1");
                                else
                                    PreferenceHandler.setAssetsConfig(((Activity) activeCbk).getApplicationContext(), config[i]);
                            }
                            if (i == 7)
                                PreferenceHandler.setUiconfigOfflmap(((Activity) activeCbk).getApplicationContext(), config[i]);
                            if (i == 8)
                                PreferenceHandler.setUiconfigShift(((Activity) activeCbk).getApplicationContext(), config[i]);
                            if (i == 9)
                                PreferenceHandler.setUiconfigAddorder(((Activity) activeCbk).getApplicationContext(), config[i]);
                            if (i == 10)
                                PreferenceHandler.setUiconfigRouteDate(((Activity) activeCbk).getApplicationContext(), config[i]);
                            if (i == 12)
                                PreferenceHandler.setUiconfigGeoCorrector(((Activity) activeCbk).getApplicationContext(), config[i]);
                            if (i == 13)
                                PreferenceHandler.setUiconfigDelaycolle(((Activity) activeCbk).getApplicationContext(), Integer.valueOf(config[i]));
                            if (i == 14)
                                PreferenceHandler.setUiconfigAutoDur(((Activity) activeCbk).getApplicationContext(), config[i]);
                            if (i == 15)
                                PreferenceHandler.setUiconfigReadOnlyTime(((Activity) activeCbk).getApplicationContext(), config[i]);
                            if (i == 16)
                                PreferenceHandler.setUiconfigAuto_Clock_IN(((Activity) activeCbk).getApplicationContext(), config[i]);
                            if (i == 17)
                                PreferenceHandler.setUiconfigPassword(((Activity) activeCbk).getApplicationContext(), config[i]);
                            if (i == 18)
                                PreferenceHandler.setUiconfigMessage(((Activity) activeCbk).getApplicationContext(), config[i]);
                            if (i == 19)
                                PreferenceHandler.setUiconfigStatusChangeAllowed(((Activity) activeCbk).getApplicationContext(), config[i]);
                        }
                    }
                    PreferenceHandler.setWorkerWeek(((Activity) activeCbk).getApplicationContext(), login.getWorkerWeek());
                    PreferenceHandler.setPrefGpsSync(((Activity) activeCbk).getApplicationContext(), getGpsSyncVal(login.getGpssync()));
                    PreferenceHandler.setShiftLock(((Activity) activeCbk).getApplicationContext(), login.getShftlock());
                    PreferenceHandler.setWorkerOffGeo(((Activity) activeCbk).getApplicationContext(), login.getOfficeGeo());
                    PreferenceHandler.setPrefLocchan(((Activity) activeCbk).getApplicationContext(), login.getLocChan());

                    objlist.add(login);
                    resActivity.setResponseObj(objlist);
                    resActivity.setStatus("success");

                }

                activeCbk.setResponseCBActivity(resActivity);
            } else if (reqId == 2) {
                resActivity = new Response();
                resActivity.setId(reqId);

                XMLHandler handle = new XMLHandler(mContext);
                Object ob = handle.getBaseValuesfromXML(response);
                ArrayList<Object> objlist = new ArrayList<Object>();
                BaseResponse baseResponse = (BaseResponse) ob;
                resActivity.setResponseObj(null);
                resActivity.setStatus("failure");
                resActivity.setErrorcode(ServiceError.getEnumValstr(ServiceError.LOGIN_FAIL));
                if (baseResponse != null) {
                    PreferenceHandler.setUrl(((Activity) activeCbk).getApplicationContext(), baseResponse.getUrl());
                    PreferenceHandler.setNsp(((Activity) activeCbk).getApplicationContext(), baseResponse.getNsp());
                    PreferenceHandler.setSubkey(((Activity) activeCbk).getApplicationContext(), baseResponse.getSubkey());
                    try {
                        makeLogin(((Activity) activeCbk).getApplicationContext(), rid, password, PreferenceHandler.getPrefNsp(mContext), isRemembered, appType, activeReqId, "https://" + PreferenceHandler.getPrefBaseUrl(mContext) + "/mobi");

                    }

                    catch (IOException e) {
                        e.printStackTrace();

                    }

                }

            }

        }

        else {

//            if (Api.SECONDARY && shouldTry) {
//                shouldTryOtherReqUrl();
//                return;
//            }
            // showErrorDialog();
            shouldTry = true; // YD after trying both url User can try different login credentials
            resActivity = new Response();
            resActivity.setId(reqId);
            resActivity.setStatus("failure");
            resActivity.setErrorcode(ServiceError.getEnumValstr(ServiceError.LOGIN_FAIL));
            activeCbk.setResponseCBActivity(resActivity);
        }

    }


    /*Remove the second Attempt for different url */

  /*  private void shouldTryOtherReqUrl() {

        try {
            shouldTry = false;
            makeLogin(mContext, this.rid, this.password, this.nspace, this.isRemembered, appType, activeReqId, Api.API_BASE_URL_SEC);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private int getGpsSyncVal(String gps_sync) {
        int gpssych = 0;
        if (gps_sync.contains("|")) {
            gps_sync = gps_sync.replace("|", ",");
            String[] time = gps_sync.split(",");
            gps_sync = time[0];
        }
        try {
            gpssych = Integer.parseInt(gps_sync);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gpssych;
    }


    private void showErrorDialog() {
        dialog = new MyDialog((Activity) this.activeCbk, "Slight problem with data",
                "The login information you provided doesn't appear to belong to an existing account. Please check entered data and try again.",
                "OK");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setkeyListender(new MyDiologInterface() {

            @Override
            public void onPositiveClick() throws JSONException {
                // TODO Auto-generated method stub

            }

            @Override
            public void onNegativeClick() {
                dialog.dismiss();

            }
        });
        dialog.onCreate(null);
        dialog.show();

    }


    @Override
    public void setResponseCBActivity(Response response) {
        // TODO Auto-generated method stub

    }
}



/*public class AceRouteService extends JobIntentService {

    static final int JOB_ID = 1000;
    private Thread requestHandler;


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        DBHandler.getDataBase(getApplicationContext());

        // Handlin old data, starting async for old data.

        Utilities.log(getApplicationContext(), "onCreate1 of service");
        if (requestHandler == null) {
            startRequestHandler(requestHandler);
        } else {
            if (!requestHandler.isAlive()) {
                Log.e(Utilities.TAG, "in service thread is not alive");
                // requestHandler.start();
                startRequestHandler(requestHandler);
            } else {
                Log.i(Utilities.TAG, "in service thread is running.");
            }
        }

        subscribeOnPubnub();
        if (PreferenceHandler.getIsNewLogin(getApplicationContext())) {
            PreferenceHandler.setIsNewLogin(getApplicationContext(), false);
            pubnubBroadcaseFirstTime = 1;// YD making this zero because it is causing pubnub playback to stop when coming from background
        }
        subscribeOnPubnubPlayBack();
        initializeLocationManager();
    }

    public void startRequestHandler(Thread requestHandler) {
        AceRequestHandler aceRequestHandler = new AceRequestHandler(
                getApplicationContext());
        requestHandler = new Thread(aceRequestHandler);
        requestHandler.start();
    }

    public void subscribeOnPubnubPlayBack() {
        pbBroadcast = new PubnubBroadcast(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        //filter.addAction("android.intent.action.BOOT_COMPLETED");
        registerReceiver(pbBroadcast, filter);

    }

    public class PubnubBroadcast extends BroadcastReceiver {
        Context context;

        public PubnubBroadcast(Context context) {
            this.context = context;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            Log.i( Utilities.TAG, "onReceive of AceRoutePubnub BroadCast .");
            String action = intent.getAction();
            final Context contextFinal = context;

            if (action != null && pubnubBroadcaseFirstTime == 0) {
                if (action.equals("android.net.conn.CONNECTIVITY_CHANGE") || action.equals("android.intent.action.BOOT_COMPLETED")) {
                    Log.i( Utilities.TAG, "Aceroute connectivity change Pubnub BroadCast");
                    NetworkInfo info = intent
                            .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                    if (info.isAvailable()) {
                        if (info.getType() == ConnectivityManager.TYPE_WIFI
                                || info.getType() == ConnectivityManager.TYPE_MOBILE) {

                            if (info.isConnected()) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            long res_id = PreferenceHandler.getResId(getApplicationContext());
                                            String company_id = PreferenceHandler.getCompanyId(getApplicationContext());

                                            if (company_id != null && res_id > 0) {
                                                AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(getApplicationContext()).syncAllDataForOffline(null, "semipartial");
                                                startTime = PreferenceHandler.getLastPubnubUpdated(getApplicationContext()) - 60000;
                                                        endTime = Utilities.getCurrentTime();

                                                        updatePBPref(context, startTime, endTime, endTime - startTime, startTime, endTime);
                                                        runPubnubPlayBack();
                                                        subscribeOnPubnub();
                                                        }
                                                        if (PreferenceHandler.getOrderMedia_DownloadingStatus(context)) {
                                                        HashMap<Long, Object> orderlist = DBEngine.getOrdersfromOfflineInMap(context);
        AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(getApplicationContext()).getAllOrderMedia(null, context, orderlist.values().toArray());
        }
        if (!PreferenceHandler.getCustListDownloadComplete(getApplicationContext()) && !PreferenceHandler.getIsCustListDownloading(getApplicationContext())) {
        long time = new Date().getTime();
        Response responseObj = DBEngine.getCustList(null, getApplicationContext(), time, DBEngine.DATA_SOURCE_SERVER);
        if (responseObj != null && !responseObj.getStatus().equals("failure"))
        PreferenceHandler.setCustListDownloadComplete(getApplicationContext(), true);
        else {
        //YD show popup to resync again
        }
        }
        } catch (IOException e) {
        e.printStackTrace();
        }
        }
        }).start();
        }
        }
        }
        }
        } else
        pubnubBroadcaseFirstTime = 0;
        }

        }

}*/
