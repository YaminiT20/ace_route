package com.aceroute.mobile.software.http;

import android.content.Context;

import com.aceroute.mobile.software.component.OrderPart;
import com.aceroute.mobile.software.component.OrderTask;
import com.aceroute.mobile.software.component.reference.AssetsType;
import com.aceroute.mobile.software.component.reference.ClientLocation;
import com.aceroute.mobile.software.component.reference.ClientSite;
import com.aceroute.mobile.software.component.reference.Customer;
import com.aceroute.mobile.software.component.reference.CustomerType;
import com.aceroute.mobile.software.component.reference.OrderStatus;
import com.aceroute.mobile.software.component.reference.OrderTypeList;
import com.aceroute.mobile.software.component.reference.Parts;
import com.aceroute.mobile.software.component.reference.ServiceType;
import com.aceroute.mobile.software.component.reference.Shifts;
import com.aceroute.mobile.software.component.reference.Site;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.component.reference.Worker;
import com.aceroute.mobile.software.utilities.PreferenceHandler;

public class Api {

    public static Context context;
    //	 public  static String API_BASE_URL = "https://qa.aceroute.com/mobi"; //YD(Ashish) used this for local (latest)


    /*.................Use Dynamic url instead of this...............*/
   //public static String API_BASE_URL = "https://portal.aceroute.com/mobi";
    //public static String PUBNUB_SUBSCRIBE_KEY_MTGOX = "sub-c-424c2436-49c8-11e5-b018-0619f8945a4f";
//    public static String API_BASE_URL_UPLOAD = "https://portal.aceroute.com/fileupload"


    public static String BASE_URL = "https://portal.aceroute.com/login";
   ;//YD
//
    //	 public  static String PUBNUB_SUBSCRIBE_KEY_MTGOX = "sub-c-de54bccc-3ab9-11e4-87bf-02ee2ddab7fe";//YD new non production
    public static boolean SECONDARY = false;
    public static String API_BASE_URL_SEC = "https://air.aceroute.com/mobi"; //YD used by online client air
    public static String API_BASE_URL_UPLOAD_SEC = "https://air.aceroute.com/mobi/fileupload";//YD used by online client air
    public static String PUBNUB_SUBSCRIBE_KEY_MTGOX_SEC = "sub-c-d5f3e420-3ab6-11e4-9201-02ee2ddab7fe";
    public final static String APP_BASE_PATH = "software";
    public final static String API_SEPRATOR = "?";
    public final static String API_AUTH_TOKEN = "token";
    public final static String API_NSPACE = "nspace";
    public final static String API_RID = "rid";
    public final static String API_ACTION_SYNCALLDATA = "syncalldata";
    public final static String API_ACTION_SYNCALLFOROFFLINE = "syncalldataforoffline";
    public final static String API_ACTION_SYNCALLFOROFFLINE_PUbNUB = "syncalldataforoffline_pb";
    public final static String API_ACTION_SYNCBGDATA = "syncbgdata";
    public final static String API_ACTION_LOGIN_USER = "mlogin";
    public final static String API_ACTION_LOGIN="login";
    public final static String API_ACTION_SAVE_RES_GEO = "saveresgeo";
    public final static String API_ACTION_STATUS_TYPE = "getstatustype";
    public final static String API_ACTION_PRIORITY_TYPE = "getprioritytype";
    public final static String API_ACTION_SEND_SMS = "sendsms";
    public final static String API_ACTION_SAVE_CHECK_IN_OUT = "savetcrd";
    public final static String API_ACTION_HANDLE_EXTRA_PUBNUB = "getextrapubnubrequest";
    public final static String API_ACTION_HANDLE_DISPLAY_DATE_DATA = "handleDisplayedDateData";
    public static final int INT_API_ACTION_HANDLE_EXTRA_PUBNUB = 1001;
    public static final int INT_API_ACTION_HANDLE_DISPLAYED_DATE_DATA = 1002;
    public static final int INT_API_ACTION_NOPES = 1003;
    public static final String API_ACTION_SAVE_ORDER_FIELD = "saveorderfld";
    public static final String API_ACTION_UPDATE_ORDER_FIELD = "saveorderfld";
    public static final String API_ACTION_UPDATE_ORDER_FIELDS = "saveorderflds";
    public static final String API_ACTION_SAVE_ORDER_TASK = "saveordertask";
    public static final String API_ACTION_SAVE_ORDER_STATUS = "saveorderstatus";
    public final static String API_ACTION_DOWNLOAD_AUDIO_FILE = "getfileencBasetobin";
    public final static String API_ACTION_LOGOUT = "logout";

    public static String getCustTypeListApi(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + CustomerType.ACTION_CUST_TYPE; //+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

    public static String getSiteTypeListApi(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + SiteType.ACTION_SITE_TYPE; //+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

    public static String getOrderTypeApi(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + OrderTypeList.ACTION_ORDER_TYPE; //+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

    public static String getStatusTypeApi(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + OrderStatus.ACTION_STATUS_TYPE; //+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

    public static String getclientsite(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + ClientSite.ACTION_GET_CLIENT_SITE; //+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

    public static String getshift(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + Shifts.ACTION_GET_SHIFTS; //KB+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }


    public static String getAssets(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + AssetsType.ACTION_GET_ASSETS_TYPE; //+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

    public static String getHeadersApiForPg(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=getterm"; //+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

    public static String getHeadersApiForAssets(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=getastterm"; //+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

    public static String getHeadersApi(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=getlabels"; //+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }//YD making it hardcode because this api is been called only once when user get login

    public static String getPartTypeApi(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + Parts.ACTION_PART_TYPE; //+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

    public static String getServiceTypeApi(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + ServiceType.ACTION_SERVICE_TYPE; // + API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

    public static String getClientLocApi(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + ClientLocation.ACTION_CLIENT_LOCATION; //+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

    public static String getSiteApi(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + Site.ACTION_SITE; //+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

    public static String getWorkerListApi(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + Worker.ACTION_WORKER_LIST; //+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

    public static String getCustomerListApi(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + Customer.ACTION_CUSTOMER_LIST; //+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

    public static String getCustomerApi(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + Customer.ACTION_CUSTOMER; //+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

    public static String getLogoutApi(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + API_ACTION_LOGOUT; // + API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

//Currently Not used


    public static String getOrderTaskApi(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + OrderTask.ACTION_GET_ORDER_TASK; //+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

    public static String getOrderPartApi(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + OrderPart.ACTION_GET_ORDER_PART; // + API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

    public static String deleteshift(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + Shifts.ACTION_DELETE_SHIFTS; //KB+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }

    public static String editshift(Context context) {
        return "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + API_SEPRATOR + "action=" + Shifts.ACTION_EDIT_SHIFTS; //KB+ API_AUTH_TOKEN + PreferenceHandler.getMtoken(context) + API_NSPACE + PreferenceHandler.getCompanyId(context) +  API_RID+PreferenceHandler.getResId(context);
    }
}