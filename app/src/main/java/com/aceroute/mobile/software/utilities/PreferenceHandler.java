package com.aceroute.mobile.software.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.util.Log;

import com.aceroute.mobile.software.component.reference.AssetLabel;
import com.aceroute.mobile.software.component.reference.CustHistoryToken;
import com.aceroute.mobile.software.component.reference.CustHistoryTokenGroup;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PreferenceHandler {

    public final static String PREF_EFORM_IMAGES_COUNT = "prefeformimagescount";
    public final static String PREF_UNIQUE_URIS = "prefuniqueuris";
    public final static String PREF_ACT_IMG_COUNT = "imagcount";
    public final static String PREF_LSTOID = "lstoid";
    public final static String PREF_NXTOID = "nxtoid";
    public final static String PREF_CTID = "prefctid";
    public final static String PREF_CATID = "9001";
    public final static String PREF_MTOKEN = "mtoken";
    public final static String PREF = "GEOcode";
    public final static String PREF_CUST_TOKEN = "customer_token";
    public final static String PREF_WORKER_OFF_GEO = "officegeo";
    public final static String PREF_UICONFIG_OFFLMAP = "uiconfig_offmap";
    public final static String PREF_UICONFIG_SHIFT = "uiconfig_shift";
    public final static String PREF_UICONFIG_ADDORDER = "uiconfig_addorder";
    public final static String PREF_UICONFIG_DELAYCOLLE = "uiconfig_delaycoll";
    public final static String PREF_UICONFIG_READONLYTIME = "uiconfig_readonlytime";
    public final static String PREF_UICONFIG_GEOCORRECTOR = "uiconfig_geocorrector";
    public final static String PREF_UICONFIG_AUTODUR = "uiconfig_autoDur";
    public final static String PREF_PIC_HEAD = "picHead";
    public final static String PREF_AUD_HEAD = "audHead";
    public final static String PREF_SIG_HEAD = "sigHead";
    public final static String PREF_FIL_HEAD = "fileHead";
    public final static String PREF_WORKER_HEAD = "workerHead";
    public final static String PREF_CUST_HEAD = "custHead";
    public final static String PREF_ORDER_HEAD = "orderHead";
    public final static String PREF_ORDERNM_HEAD = "odrNmHead";
    public final static String PREF_DESC_ORDER = "dtlHead";
    public final static String PREF_ALERT_HEAD = "alertHead";
    public final static String PREF_NOTE_HEAD = "noteHead";
    public final static String PREF_SUM_HEAD = "sumHead";
    public final static String MOBILOGINAGAIN = "loginagain";
    public final static String DialogMOBILOGINAGAIN = "Dialogloginagain";
    public final static String UICONFIG_ROUTEDATE = "routedate_";
    public final static String UICONFIG_Auto_Clock_IN = "AutoClockIN";
    public final static String UICONFIG_PASSWORD = "password_drawer";
    public final static String UICONFIG_MESSAGE = "message_drawer";
    public final static String UICONFIG_STATUS_CHANGED_ALLOWED = "message_drawer";
    private final static String PREF_HEARTBEAT = "aceroute_heartbeat";
    // used for storing geotime in milliseconds.
    private final static String PREF_GEOBEAT = "aceroute_geobeat";
    private final static String PREF_LAT = "lat";
    private final static String PREF_OLD_DATA_CLEAN = "aceroute_old_data_clean";
    private final static String PREF_GEO_SYNC_TIME = "geosynctime";
    private final static String PREF_GEO_SYNC_TIME_TO_SERVER = "geosynctimetoServer";
    private final static String PREF_RES_ID = "resid";
    private final static String PREF_NSPACE = "nspace";

    private final static String PREF_VERSION = "nversion";
    private final static String PREF_PCODE = "pcode";
    private final static String PREF_APP_DATA_CHANGED = "gpssync";
    // for maintaining alarm state temporary
    // TODO cancel alarms properly this is a temporary solution to maintain flag
    private final static String PREF_IS_ALARM_DEAD = "isAlarmDead";
    private final static String PREF_MESSAGE = "smstmpl";

    // it is used to store fromDate of current (from-to) range.
    private final static String PREF_FROM_DATE = "fromdate";
    // it is used to store toDate of current (from-to) range.
    private final static String PREF_TO_DATE = "todate";
    private final static String PREF_TEMP_DATE = "tempdate";
    private final static String PREF_CURRENT_LOC = "currentlocation";
    private final static String PREF_USER_REMEMBER = "userlogin";
    private final static String PREF_IS_USER_LOGIN = "isuserlogin";

    // setting edn(edition) for tracking geocode
    private final static String PREF_EDITION_FOR_GEO = "edition";
    private final static String PREF_TID_STATUS = "tid_status";
    private final static String PREF_IS_GEO_FIRST = "is_geoFirst";
    private final static String PREF_NSP_ID = "nspid";//YD using for pubnub channel
    private final static String PREF_ORDER_ID_JS = "orderid";
    private final static String PREF_CUST_SITE_DATA = "customerSiteData";
    private final static String PREF_TECHNAME_DATA = "customerSiteData";
    private final static String PREF_CUST_SITE_DATA_active = "customerSiteDataActive";
    private final static String PREF_CUST_SITE_DATA_ACTIVE_FORCUST_LST = "customerSiteDataActiveForCustLst";
    private final static String PREF_CUST_MAP_COUNTRY = "mapcountry";
    private final static String PREF_CUST_MAP_STATE = "mapstate";
    private final static String PREF_PIC_SIGN_URI = "picAndSignUri";
    private final static String PREF_LAST_SYNC_TIME = "lastsynctime";
    private final static String PREF_BASE_URL = "url";
    private final static String PREF_BASE_NSP = "nsp";
    private final static String PREF_BASE_SUBKEY = "subkey";
    private final static String PREF_APP_TYPE = "appType";
    private final static String PREF_LOGIN_UICONFIG = "uiconfigg";
    private final static String PREF_WORKER_WEEK = "WorkerWeek";
    private final static String PREF_LOCCHAN = "locchg";
    private final static String PREF_SHIFTLOCK = "shfdtlock";
    private final static String PREF_CLOCKIN_OUT_STAT = "clockin_out";
    private final static String PREF_ORDER_TASK_STAT_ID = "OrderTaskType";
    private final static String LAST_PUBNUB_UPDATE_TM = "lastPubnubUpdatedTm";
    private final static String LAST_SYNC_POPUP_CANCEL_TM = "SyncPopupCancelTm";
    private final static String PREF_TPK_STATE = "arcgis_tpk_availability";
    private final static String PREF_DATE_CHANGE = "optionDateChange";
    private final static String PREF_WHEN_SYNC_TIME = "odrDateSyncedDt";
    private final static String PREF_AUTO_SYNC_INTERVALS = "odrDateSyncedDt";
    private final static String PREF_LOGIN_NEW = "isNewLogin";
    private final static String PREF_PLAYBACK_END_TIME = "pubnubPBendTme";
    private final static String PREF_PLAYBACK_START_TIME = "pubnubPBStartTme";
    private final static String PREF_PLAYBACK_CURRENTSTART_TIME = "pubnubPBCurrentStartTme";
    private final static String PREF_PLAYBACK_INTERVAL_TIME = "pubnubPBIntervalTme";
    private final static String PREF_CUSTLIST_DOWNLOAD_COMPLETE = "custlistdownloadComplete";
    private final static String PREF_IS_CUSTLIST_DOWNLOADING = "iscustlistdownloading";
    private final static String PREF_PLAYBACK_CURRENTEND_TIME = "pubnubPBCurrentEndTme";
    private final static String PREF_SERVICE_HEAD = "pubnubServiceHead";
    private final static String PREF_PART_HEAD = "pubnubPartHead";
    private final static String PREF_ASSET_HEAD = "pubnubAssetHead";
    private final static String PREF_PO_HEAD = "pubnubPoHead";
    private final static String PREF_INV_HEAD = "pubnubInvHead";
    private final static String PREF_CAM_GAL_OPT = "cam_gal_option";
    private final static String PREF_WORKER_NM = "workername";
    private final static String PREF_CUST_ODR_HIS = "deletecustorderHistory";
    private final static String PREF_ASSET_LABEL = "assetlabels";
    private final static String PREF_APP_FONT_SIZE = "currentFontSize";
    private final static String PREF_APP_ORDER_MEDIAFILES = "ordermediafiles";
    private static final String PREF_CONFIG_TASKS = "prefConfigTasks";
    private static final String PREF_CONFIG_ASSETS = "prefConfigAssets";
    private static final String PREF_FORM_LIST_COUNT = "prefformListCount";
    public static String tcode = null;
    static long id = 1;
    private static int tableCounter = 0;
    private static String PREF_IS_CALLED_FROM_LOGIN_PG = "isCalledFromLoginPage";
    private static final String PREF_FIELD_RULES = "fieldrules";
    private static final String PREF_ORDER_GROUP = "lordgrp";
    private static final String FORM_KEY_VALUE = "frmkeyvalue";
    private static final String IMAGEVIEWSTATUS = "imagevewstate";
    public static final String ACTIVE_ORDERid = "activeorderid";

    public static void setPrefUniqueUris(Context context, String uniqfromque) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_UNIQUE_URIS,uniqfromque).commit();
    }

    public static String getPrefUniqueUris(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_UNIQUE_URIS,null);
    }

    public static void setPrefEformImagesCount(Context context, int imgcount) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_EFORM_IMAGES_COUNT,imgcount).commit();
    }

    public static String getPrefEformImagesCount(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_EFORM_IMAGES_COUNT,null);
    }



    public static void setACTIVE_ORDERid(Context context, String orderid) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(ACTIVE_ORDERid,orderid).commit();
    }

    public static String getACTIVE_ORDERid(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(ACTIVE_ORDERid,null);
    }

    public static void setImageviewstatus(Context context, ArrayList<String> imagstatus){
        Set<String> tempset = new HashSet<String>();
        tempset.addAll(imagstatus);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(IMAGEVIEWSTATUS,tempset).commit();

    }

    public static Set<String> getImageviewstatus(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getStringSet(IMAGEVIEWSTATUS,null);
    }

    public static void setPrefActImgCount(Context context, String imgcount){
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_ACT_IMG_COUNT,imgcount).commit();
    }

    public static String getPrefActImgCount(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_ACT_IMG_COUNT,null);
    }

    public static void setFormKeyValue(Context context, String frmkeyvalue){
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(FORM_KEY_VALUE,frmkeyvalue).commit();
    }

    public static String getFormKeyValue(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(FORM_KEY_VALUE,null);
    }

    public static void setPrefNxtoid(Context context, String nxtoid){
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_NXTOID,nxtoid).commit();
    }

    public static String getPrefNxtoid(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_NXTOID,"0");
    }

    public static void setPrefLstoid(Context context, String lstoid) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_LSTOID,lstoid).commit();
    }

    public static String getPrefLstoid(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LSTOID,"0");
    }

    public static void setPrefOrderGroup(Context context, String ordergrp) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_ORDER_GROUP,ordergrp).commit();
    }


    public static String getPrefOrderGroup(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_ORDER_GROUP,null);
    }


    public static void setPrefFieldRules(Context context, String fieldrules) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_FIELD_RULES,fieldrules).commit();
    }

    public static String getPrefFieldRules(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_FIELD_RULES,null);
    }

    public static void setPrefCtid(Context context, String ctid){
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_CTID,ctid).commit();
    }

    public static String getPrefCtid(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_CTID, null);
    }

    public static void setPrefFormListCount(Context context, int count) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(PREF_FORM_LIST_COUNT, count).commit();
    }

    public static int getPrefFormListCount(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(
                PREF_FORM_LIST_COUNT, 0);
    }

    public static void setAutoSyncInterval(Context context, long time) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(PREF_AUTO_SYNC_INTERVALS, time).commit();
    }

    public static long getAutoSyncInterval(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(
                PREF_AUTO_SYNC_INTERVALS, 0L);
    }

    public static void setCurtSteCustdat(Context context, long time) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(PREF_CUST_SITE_DATA_active, time).commit();
    }

    public static long getCurtSteCustdat(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(
                PREF_CUST_SITE_DATA_active, 0L);
    }

    public static void setCurtSteCustdatForcustList(Context context, long time) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(PREF_CUST_SITE_DATA_ACTIVE_FORCUST_LST, time).commit();
    }

    public static long getCurtSteCustdatForcustList(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(
                PREF_CUST_SITE_DATA_ACTIVE_FORCUST_LST, 0L);
    }

    public static void setCustSiteData(Context context, String data) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().
                putString(PREF_CUST_SITE_DATA, data).commit();
    }

    public static String getCustSiteData(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(
                PREF_CUST_SITE_DATA, null);
    }

    public static void setPrefTechnameData(Context context, String data) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().
                putString(PREF_TECHNAME_DATA, data).commit();
    }

    public static String getPrefTechnameData(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(
                PREF_TECHNAME_DATA, null);
    }

    public static String getOrderId4Js(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(
                PREF_ORDER_ID_JS, null);
    }

    public static String getPrefLocchan(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(
                PREF_LOCCHAN, null);
    }

    public static void setgeoFlagFirst(Context context, boolean flag) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(PREF_IS_GEO_FIRST, flag).commit();
    }

    public static boolean getgeoFlagFirst(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                PREF_IS_GEO_FIRST, false);
    }

    public static void setRemember(Context context, boolean paramResid) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(PREF_USER_REMEMBER, paramResid).commit();
    }

    public static boolean getRemember(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_USER_REMEMBER, false);
    }

    public static void setResId(Context context, long paramResid) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(PREF_RES_ID, paramResid).commit();
    }

    public static long getResId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(
                PREF_RES_ID, 0L);
    }

    public static void setMtoken(Context context, String paramString) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_MTOKEN, paramString).commit();
    }

    public static String getMtoken(Context context) {

        String encyptcode = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_MTOKEN, null);
        return encyptcode;
    }

    public static void setWorkerOffGeo(Context context, String paramString) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_WORKER_OFF_GEO, paramString).commit();
    }

    public static String getWorkerOffGeo(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_WORKER_OFF_GEO, null);
    }

    public static void setCompanyId(Context context, String paramString) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_NSPACE, paramString).commit();
    }

    public static String getCompanyId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_NSPACE, null);
    }
    //PREF_VERSION

    public static void setPrefVersion(Context context, Integer paramString) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(PREF_VERSION, paramString).commit();
    }

    public static Integer getPrefVersion(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(PREF_VERSION, 0);
    }

    public static void setPassCode(Context context, String paramString) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_PCODE, paramString).commit();
    }

    public static String getPassCode(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_PCODE, null);
    }

    public static void setAppDataChanged(Context context, boolean paramString) { //YD using this because when app is in background and app data changed from pubnub
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(PREF_APP_DATA_CHANGED, paramString).commit();
    }

    public static boolean getAppDataChanged(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_APP_DATA_CHANGED, false);
    }

    public static void setTableCounter(int tableCounter) {
        PreferenceHandler.tableCounter = tableCounter;
    }

    public static void setPrefLat(Context context, String lat) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_LAT, lat).commit();
    }

    public static void setPrefLong(Context context, String longi) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_LAT, longi).commit();
    }

    /**
     * setter and getter methods for heartbeat and geocode sync time.
     */

    public static void setAcerouteHeartbeat(Context context, long longi) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(PREF_HEARTBEAT, longi).commit();
    }

    /**
     * These methods for using to record software geo timing heartbeat.
     */

    public static void setAcerouteGeoTime(Context context, long time) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(PREF_GEOBEAT, time).commit();
    }

    public static long getAcerouteGeoTime(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(
                PREF_GEOBEAT, 0L);
    }

    public static synchronized long getPrefQueueRequestId(Context context) {
        //int id = PreferenceManager.getDefaultSharedPreferences(context).getInt(
        //		PREF_QUEUE_REQUEST_ID, 0);
        id = id + 1;
        //PreferenceManager.getDefaultSharedPreferences(context).edit()
        //			.putInt(PREF_QUEUE_REQUEST_ID, id).commit();
        return id;
    }

    public static int getPrefGpsSync(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(
                PREF_GEO_SYNC_TIME, 0);
    }

    public static void setPrefGpsSync(Context context, int time) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(PREF_GEO_SYNC_TIME, time).commit();
    }

    public static long getGeoSyncTimeToServer(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(
                PREF_GEO_SYNC_TIME_TO_SERVER, 0);
    }

    public static void setGeoSyncTimeToServer(Context context, long time) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(PREF_GEO_SYNC_TIME_TO_SERVER, time).commit();
    }


    public static String getFromDate(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_FROM_DATE, null);
    }


    public static String getToDate(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_TO_DATE, null);
    }

    public static void setPrefRangeDates(Context context, String fromDate,
                                         String toDate) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_FROM_DATE, fromDate).commit();
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_TO_DATE, toDate).commit();
    }

    /**
     * These methods used for storing last time setted for old data clean up
     */

    public static void setOldDataCleanTime(Context context, long time) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(PREF_OLD_DATA_CLEAN, time).commit();
    }

    public static long getOldDataCleanTime(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(
                PREF_OLD_DATA_CLEAN, 0L);
    }

    public static void setAlarmState(Context context, boolean state) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(PREF_IS_ALARM_DEAD, state).commit();
    }

    public static boolean getAlarmState(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_ALARM_DEAD, false);
    }

    public static void setTemplate(Context context, String message) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_MESSAGE, message).commit();
    }

    public static String getTemplate(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_MESSAGE, null);
    }

    public static void setTempDate(Context context, String message) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_TEMP_DATE, message).commit();
    }

    public static String getTempDate(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_TEMP_DATE, null);
    }

    public static void setTempLastLocation(Context context, String message) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_CURRENT_LOC, message).commit();
    }

    public static String getTempLastLocation(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_CURRENT_LOC, null);
    }

    public static int getPrefEditionForGeo(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(PREF_EDITION_FOR_GEO, -1);
    }

    public static void setPrefEditionForGeo(Context context, int edition) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(PREF_EDITION_FOR_GEO, edition).commit();
    }

    public static boolean getTidStatus(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_TID_STATUS, false);
    }

    public static void setTidStatus(Context context, boolean edition) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(PREF_TID_STATUS, edition).commit();
    }

    public static void clearPref(Context context, String pref_key) {
        getPreference(context).edit().remove(pref_key).commit();
    }

    public static SharedPreferences getPreference(Context context) {
        return context.getSharedPreferences(PREF, 0);

    }

    public static void setMapCountry(Context context, String mapCountry) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().
                putString(PREF_CUST_MAP_COUNTRY, mapCountry).commit();

    }

    public static void setMapState(Context context, String mapState) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().
                putString(PREF_CUST_MAP_STATE, mapState).commit();

    }

    public static void setUriOfPic_sign(Context context, String uri) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_PIC_SIGN_URI, uri).commit();

    }

    public static String getUriOfPic_sign(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PREF_PIC_SIGN_URI, null);
    }

    public static void setlastsynctime(Context context, long lastsynctime) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(PREF_LAST_SYNC_TIME, lastsynctime).commit();

    }

    public static long getlastsynctime(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getLong(PREF_LAST_SYNC_TIME, 0);

    }

    public static void setAppType(Context context, String apptype) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_APP_TYPE, apptype).commit();
        Log.i("software ", apptype);

    }

    public static String getAppType(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PREF_APP_TYPE, null);

    }

    public static void setUiConfig(Context context, String apptype) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_LOGIN_UICONFIG, apptype).commit();
        Log.i("software ", apptype);

    }

    public static String getUiConfig(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PREF_LOGIN_UICONFIG, null);

    }

    public static void setWorkerWeek(Context context, String workerWeek) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_WORKER_WEEK, workerWeek).commit();
        Log.i("software", workerWeek);

    }

    public static void setPrefLocchan(Context context, String locchan) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_LOCCHAN, locchan).commit();
        Log.i("software", locchan);

    }

    public static void setShiftLock(Context context, String shfdlock) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_SHIFTLOCK, shfdlock).commit();
        Log.i("software", shfdlock);

    }

    public static String getShiftLock(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PREF_SHIFTLOCK, null);

    }

    public static void setIsCalledFromLoginPage(Context context, String Status) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_IS_CALLED_FROM_LOGIN_PG, Status).commit();
        Log.i("software ", Status);
    }

    public static String getIsCalledFromLoginPage(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PREF_IS_CALLED_FROM_LOGIN_PG, null);

    }

    public static void setClockInStat(Context context, String clockin_outStatus) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_CLOCKIN_OUT_STAT, clockin_outStatus).commit();
        Log.i("software ", clockin_outStatus);
    }

    public static String getClockInStat(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PREF_CLOCKIN_OUT_STAT, null);

    }

    public static void setTaskTypeOld(Context context, String taskStatusId) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_ORDER_TASK_STAT_ID, taskStatusId).commit();
        Log.i("software ", taskStatusId);
    }

    public static String getTaskTypeOld(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PREF_ORDER_TASK_STAT_ID, null);

    }

    public static void setLastPubnubUpdated(Context context, long time) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(LAST_PUBNUB_UPDATE_TM, time).commit();
    }

    public static long getLastPubnubUpdated(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getLong(LAST_PUBNUB_UPDATE_TM, 0);

    }

    public static void setSyncPopupCancelTm(Context context, long currentTime) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(LAST_SYNC_POPUP_CANCEL_TM, currentTime).commit();

    }

    public static long getSyncPopupCancelTm(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getLong(LAST_SYNC_POPUP_CANCEL_TM, 0);
    }

    public static boolean getArcgisMapTpkState(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_TPK_STATE, false);
    }

    public static String getOdrGetDate(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_DATE_CHANGE, null);
    }

    public static void setOdrGetDate(Context context, String odrGetDate) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_DATE_CHANGE, odrGetDate).commit();
    }

    public static void setodrLastSyncForDt(Context context, long lastsynctime) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(PREF_WHEN_SYNC_TIME, lastsynctime).commit();

    }

    public static long getodrLastSyncForDt(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getLong(PREF_WHEN_SYNC_TIME, 0);

    }

    public static boolean getIsNewLogin(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_LOGIN_NEW, false);
    }

    public static void setIsNewLogin(Context context, boolean isLoginNew) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(PREF_LOGIN_NEW, isLoginNew).commit();
    }

    public static void setPubnubPBendTime(Context context, long lastsynctime) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(PREF_PLAYBACK_END_TIME, lastsynctime).commit();

    }

    public static long getPubnubPBendTime(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getLong(PREF_PLAYBACK_END_TIME, 0);

    }

    public static void setIntervalTime(Context context, long lastsynctime) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(PREF_PLAYBACK_INTERVAL_TIME, lastsynctime).commit();

    }

    public static long getIntervalTime(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getLong(PREF_PLAYBACK_INTERVAL_TIME, 0);

    }

    public static void setPubnubPBStartTime(Context context, long lastsynctime) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(PREF_PLAYBACK_START_TIME, lastsynctime).commit();

    }

    public static void setPubnubPBCurrentStartTime(Context context, long lastsynctime) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(PREF_PLAYBACK_CURRENTSTART_TIME, lastsynctime).commit();

    }

    public static long getPubnubPBStartTime(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getLong(PREF_PLAYBACK_START_TIME, 0);

    }

    public static long getPubnubPBCurrentStartTime(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getLong(PREF_PLAYBACK_CURRENTSTART_TIME, 0);

    }

    public static boolean getCustListDownloadComplete(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_CUSTLIST_DOWNLOAD_COMPLETE, false);
    }

    public static void setCustListDownloadComplete(Context context, boolean status) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(PREF_CUSTLIST_DOWNLOAD_COMPLETE, status).commit();
    }

    public static boolean getIsCustListDownloading(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_CUSTLIST_DOWNLOADING, false);
    }

    public static void setIsCustListDownloading(Context context, boolean status) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(PREF_IS_CUSTLIST_DOWNLOADING, status).commit();
    }

    public static long getcurrentEndTme(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getLong(PREF_PLAYBACK_CURRENTEND_TIME, 0);

    }

    public static void setcurrentEndTme(Context context, long currentEndTme) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(PREF_PLAYBACK_CURRENTEND_TIME, currentEndTme).commit();

    }

    public static void setFormHead(Context context, String workerWeek) {//YD changed from service to Form
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_SERVICE_HEAD, workerWeek).commit();
        Log.i("software", workerWeek);

    }

    public static String getFormHead(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PREF_SERVICE_HEAD, null);

    }

    public static void setPartHead(Context context, String workerWeek) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_PART_HEAD, workerWeek).commit();
        Log.i("software", workerWeek);

    }

    public static String getPartHead(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PREF_PART_HEAD, null);

    }

    public static void setPOHead(Context context, String workerWeek) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_PO_HEAD, workerWeek).commit();
        Log.i("software", workerWeek);

    }

    public static String getPOHead(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PREF_PO_HEAD, null);

    }

    public static void setInvHead(Context context, String workerWeek) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_INV_HEAD, workerWeek).commit();
        Log.i("software", workerWeek);

    }

    public static String getInvHead(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PREF_INV_HEAD, null);

    }

    public static void setOptionSelectedForImg(Context context, String passwd) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_CAM_GAL_OPT, passwd).commit();

    }

    public static String getOptionSelectedForImg(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PREF_CAM_GAL_OPT, null);

    }

    public static void setWorkerNm(Context context, String passwd) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_WORKER_NM, passwd).commit();

    }

    public static String getWorkerNm(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PREF_WORKER_NM, null);

    }

    public static void setAssetLabels(Context context, AssetLabel obj) {
        Gson gson = new Gson();
        String json = gson.toJson(obj);

        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_ASSET_LABEL, json).commit();

    }

    public static AssetLabel getAssetLabels(Context mContext) {
        String json = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PREF_ASSET_LABEL, null);

        Gson gson = new Gson();
        AssetLabel obj = gson.fromJson(json, AssetLabel.class);
        return obj;
    }

    public static void setCustomerTokens(Context mContext, CustHistoryToken CustHistToken_OBJ, String action) {

        CustHistoryTokenGroup CustHistTokenGrp_OBJ = PreferenceHandler.getCustomerTokens(mContext);

        if (CustHistTokenGrp_OBJ != null && CustHistTokenGrp_OBJ.getCustHistTokenGrp() != null && CustHistTokenGrp_OBJ.getCustHistTokenGrp().size() > 0)//size check should be remove otherwise may instance with get created when order list_cal size goes to zero
            CustHistTokenGrp_OBJ.getCustHistTokenGrp().put(CustHistToken_OBJ.getCustId(), CustHistToken_OBJ);
        else {
            CustHistTokenGrp_OBJ = new CustHistoryTokenGroup();
            HashMap<Long, CustHistoryToken> custHistToknOBJ = new HashMap<Long, CustHistoryToken>();
            custHistToknOBJ.put(CustHistToken_OBJ.getCustId(), CustHistToken_OBJ);
            CustHistTokenGrp_OBJ.setCustHistTokenGrp(custHistToknOBJ);
        }

        Gson gson = new Gson();
        String json = gson.toJson(CustHistTokenGrp_OBJ);

        PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                .putString(PREF_CUST_TOKEN, json).commit();
    }

    public static void UpdateCustomerTokens(Context mContext, CustHistoryTokenGroup CustHistTokenGrp_OBJ) {

        Gson gson = new Gson();
        String json = gson.toJson(CustHistTokenGrp_OBJ);

        PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                .putString(PREF_CUST_TOKEN, json).commit();
    }

    public static void delCustomerTokens(Context mContext, String cid) {

        CustHistoryTokenGroup CustHistTokenGrp_OBJ = PreferenceHandler.getCustomerTokens(mContext);

        if (CustHistTokenGrp_OBJ != null && CustHistTokenGrp_OBJ.getCustHistTokenGrp() != null && CustHistTokenGrp_OBJ.getCustHistTokenGrp().size() > 0
                && CustHistTokenGrp_OBJ.getCustHistTokenGrp().get(Long.valueOf(cid)) != null)//size check should be remove otherwise may instance with get created when order list_cal size goes to zero
        {
            PreferenceHandler.setcustomerOdrHisDelnm(mContext, CustHistTokenGrp_OBJ.getCustHistTokenGrp().get(Long.valueOf(cid)).getPath());
            CustHistTokenGrp_OBJ.getCustHistTokenGrp().remove(Long.valueOf(cid));
        } else {
            Log.i("AceRoute", "token not available for the cid to delete");
        }

        Gson gson = new Gson();
        String json = gson.toJson(CustHistTokenGrp_OBJ);

        PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                .putString(PREF_CUST_TOKEN, json).commit();
    }

    public static CustHistoryTokenGroup getCustomerTokens(Context context) {
        String json = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_CUST_TOKEN, null);

        Gson gson = new Gson();
        CustHistoryTokenGroup obj = gson.fromJson(json, CustHistoryTokenGroup.class);
        return obj;

    }

    public static void setcustomerOdrHisDelnm(Context context, String path) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_CUST_ODR_HIS, path).commit();

    }

    public static String getcustomerOdrHisDelnm(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PREF_CUST_ODR_HIS, null);

    }

    public static void setCurrrentFontSzForApp(Context context, int fontSize) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(PREF_APP_FONT_SIZE, fontSize).commit();
    }

    public static int getCurrrentFontSzForApp(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getInt(PREF_APP_FONT_SIZE, 0);

    }

    public static void setAssetsHead(Context context, String headText) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_ASSET_HEAD, headText).commit();
        Log.i("software", headText);

    }

    public static String getAssetsHead(Context mContext) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(PREF_ASSET_HEAD, null);

    }

    public static void setNspId(Context context, long paramNspid) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(PREF_NSP_ID, paramNspid).commit();
    }

    public static long getNspId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(
                PREF_NSP_ID, 0L);
    }

    public static void setOrderMedia_DownloadingStart(Context context, boolean flag) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(PREF_APP_ORDER_MEDIAFILES, flag).commit();
    }

    public static boolean getOrderMedia_DownloadingStatus(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                PREF_APP_ORDER_MEDIAFILES, false);
    }

    public static int getUiconfigDelaycolle(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(PREF_UICONFIG_DELAYCOLLE, 0);
    }

    public static String getUiconfigAddorder(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_UICONFIG_ADDORDER, "1");
    }

    public static String getUiconfigShift(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_UICONFIG_SHIFT, "1");
    }

    public static void setUiconfigDelaycolle(Context context, int path) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(PREF_UICONFIG_DELAYCOLLE, path).commit();

    }

    public static void setUiconfigAddorder(Context context, String path) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_UICONFIG_ADDORDER, path).commit();

    }

    public static void setUiconfigShift(Context context, String path) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_UICONFIG_SHIFT, path).commit();

    }

    public static void setUiconfigOfflmap(Context context, String path) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_UICONFIG_OFFLMAP, path).commit();

    }

    public static String getUiconfigReadOnlyTime(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_UICONFIG_READONLYTIME, "0");
    }

    public static void setUiconfigReadOnlyTime(Context context, String path) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_UICONFIG_READONLYTIME, path).commit();

    }

    public static String getUiconfigGeoCorrector(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_UICONFIG_GEOCORRECTOR, "1");
    }

    public static void setUiconfigGeoCorrector(Context context, String path) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_UICONFIG_GEOCORRECTOR, path).commit();

    }

    public static String getUiconfigAutoDur(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_UICONFIG_AUTODUR, "0");
    }

    public static void setUiconfigAutoDur(Context context, String path) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_UICONFIG_AUTODUR, path).commit();

    }

    public static void setPictureHead(Context context, String lpic) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_PIC_HEAD, lpic).commit();
    }

    public static String getPictureHead(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_PIC_HEAD, null);
    }

    public static void setAudioHead(Context context, String laud) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_AUD_HEAD, laud).commit();
    }

    public static String getAudioHead(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_AUD_HEAD, null);
    }

    public static void setArrayPrefs(String arrayName, ArrayList<String> array, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferencectid", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"_size", array.size());
        for(int i=0;i<array.size();i++)
            editor.putString(arrayName + "_" + i, array.get(i));
        editor.apply();
    }
    public static ArrayList<String> getArrayPrefs(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferencectid", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        ArrayList<String> array = new ArrayList<>(size);
        for(int i=0;i<size;i++)
            array.add(prefs.getString(arrayName + "_" + i, null));
        return array;
    }

    public static void setPrefCatid(Context context, String catid) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_CATID, catid).commit();
    }

    public static String getPrefCatid(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_CATID, null);
    }

    public static void setSignatureHead(Context context, String lsig) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_SIG_HEAD, lsig).commit();
    }

    public static String getSignatureHead(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SIG_HEAD, null);
    }

    public static void setFileHead(Context context, String lfil) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_FIL_HEAD, lfil).commit();
    }

    public static String getFileHead(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_FIL_HEAD, null);
    }

    public static void setWorkerHead(Context context, String lwrk) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_WORKER_HEAD, lwrk).commit();
    }

    public static String getWorkerHead(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_WORKER_HEAD, null);
    }

    public static void setCustomerHead(Context context, String lcst) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_CUST_HEAD, lcst).commit();
    }

    public static String getCustomerHead(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_CUST_HEAD, null);
    }

    public static void setOrderHead(Context context, String lord) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_ORDER_HEAD, lord).commit();
    }

    public static String getOrderHead(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_ORDER_HEAD, null);
    }

    public static void setOrderNameHead(Context context, String lordnm) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_ORDERNM_HEAD, lordnm).commit();
    }

    public static String getOrderNameHead(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_ORDERNM_HEAD, null);
    }

    public static void setOrderDescHead(Context context, String ldtl) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_DESC_ORDER, ldtl).commit();
    }

    public static String getOrderDescHead(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_DESC_ORDER, null);
    }

    public static void setAlertHead(Context context, String lalt) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_ALERT_HEAD, lalt).commit();
    }

    public static String getAlertHead(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_ALERT_HEAD, null);
    }

    public static void setNoteHead(Context context, String lnot) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_NOTE_HEAD, lnot).commit();
    }

    public static String getNoteHead(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_NOTE_HEAD, null);
    }

    public static void setSummaryHead(Context context, String lsum) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_SUM_HEAD, lsum).commit();

    }

    public static String getSummaryHead(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SUM_HEAD, null);
    }

    public static boolean getMOBILOGINAGAIN(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(MOBILOGINAGAIN, false);
    }

    public static void setMOBILOGINAGAIN(Context context, boolean lsum) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(MOBILOGINAGAIN, lsum).commit();

    }

    public static boolean getDialogMOBILOGINAGAIN(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(DialogMOBILOGINAGAIN, false);
    }

    public static void setDialogMOBILOGINAGAIN(Context context, boolean lsum) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(DialogMOBILOGINAGAIN, lsum).commit();

    }

    public static void setUiconfigRouteDate(Context context, String lsum) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(UICONFIG_ROUTEDATE, lsum).commit();

    }

    public static String getUiconfigRouteDate(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(UICONFIG_ROUTEDATE, "1");
    }

    public static void setUiconfigAuto_Clock_IN(Context context, String lsum) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(UICONFIG_Auto_Clock_IN, lsum).commit();

    }

    public static String getUiconfigUAuto_Clock_IN(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(UICONFIG_Auto_Clock_IN, "0");
    }

    public static void setUiconfigPassword(Context context, String password) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(UICONFIG_PASSWORD, password).commit();

    }

    public static void setUiconfigMessage(Context context, String lsum) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(UICONFIG_MESSAGE, lsum).commit();

    }

    public static String getUiconfigMessage(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(UICONFIG_MESSAGE, "0");
    }

    public static void setUiconfigStatusChangeAllowed(Context context, String lsum) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(UICONFIG_STATUS_CHANGED_ALLOWED, lsum).commit();

    }

    public static String getUiconfigStatusChangeAllowed(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(UICONFIG_STATUS_CHANGED_ALLOWED, "0");
    }

    public static void setUrl(Context context, String url) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_BASE_URL, url).commit();

    }

    public static void setNsp(Context context, String url) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_BASE_NSP, url).commit();

    }

    public static void setSubkey(Context context, String url) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_BASE_SUBKEY, url).commit();

    }

    public static String getPrefBaseUrl(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_BASE_URL, null);
    }

    public static String getPrefNsp(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_BASE_NSP, null);
    }

    public static String getPrefBaseSubkey(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_BASE_SUBKEY, null);
    }


    public static void setIsUserLoggedIn(Context context, boolean paramResid) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(PREF_IS_USER_LOGIN, paramResid).commit();
    }

    public static boolean getIsUserLoggedIn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_USER_LOGIN, false);
    }

    public static void setFormConfig(Context context, String s) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_CONFIG_TASKS, s).commit();
    }

    public static String getTasksConfig(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_CONFIG_TASKS, null);
    }

    public static void setAssetsConfig(Context context, String s) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(PREF_CONFIG_ASSETS, s).commit();
    }

    public static String getAssetsConfig(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_CONFIG_ASSETS, null);
    }
}