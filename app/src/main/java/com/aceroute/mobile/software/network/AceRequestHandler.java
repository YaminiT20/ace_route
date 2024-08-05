package com.aceroute.mobile.software.network;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import androidx.fragment.app.FragmentActivity;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.aceroute.mobile.software.AceOfflineSyncEngine;
import com.aceroute.mobile.software.AceRouteApplication;
import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.SplashII;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderMedia;
import com.aceroute.mobile.software.component.OrderNotes;
import com.aceroute.mobile.software.component.OrderPart;
import com.aceroute.mobile.software.component.OrderTask;
import com.aceroute.mobile.software.component.OrdersMessage;
import com.aceroute.mobile.software.component.reference.Assets;
import com.aceroute.mobile.software.component.reference.AssetsType;
import com.aceroute.mobile.software.component.reference.ClientSite;
import com.aceroute.mobile.software.component.reference.CustHistoryToken;
import com.aceroute.mobile.software.component.reference.CustHistoryTokenGroup;
import com.aceroute.mobile.software.component.reference.Customer;
import com.aceroute.mobile.software.component.reference.CustomerContact;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.Form;
import com.aceroute.mobile.software.component.reference.MessagePanic;
import com.aceroute.mobile.software.component.reference.OrderTypeList;
import com.aceroute.mobile.software.component.reference.Parts;
import com.aceroute.mobile.software.component.reference.ServiceType;
import com.aceroute.mobile.software.component.reference.Shifts;
import com.aceroute.mobile.software.component.reference.Site;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.component.reference.Worker;
import com.aceroute.mobile.software.database.DBEngine;
import com.aceroute.mobile.software.database.DBHandler;
import com.aceroute.mobile.software.fragment.GoogleMapFragment;
import com.aceroute.mobile.software.fragment.MapAllFragment;
import com.aceroute.mobile.software.fragment.MapFragment;
import com.aceroute.mobile.software.http.Api;
import com.aceroute.mobile.software.http.HttpConnection;
import com.aceroute.mobile.software.http.RequestObject;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.ChangePassword;
import com.aceroute.mobile.software.requests.CommonSevenReq;
import com.aceroute.mobile.software.requests.DeleteMediaRequest;
import com.aceroute.mobile.software.requests.EditSiteReq;
import com.aceroute.mobile.software.requests.GeoSyncAlarmRequest;
import com.aceroute.mobile.software.requests.GetAsset;
import com.aceroute.mobile.software.requests.GetContacts;
import com.aceroute.mobile.software.requests.GetFileMetaRequest;
import com.aceroute.mobile.software.requests.GetPart_Task_FormRequest;
import com.aceroute.mobile.software.requests.GetSiteRequest;
import com.aceroute.mobile.software.requests.GetSiteTypeRequest;
import com.aceroute.mobile.software.requests.LogoutRequest;
import com.aceroute.mobile.software.requests.PubnubRequest;
import com.aceroute.mobile.software.requests.SaveShiftReq;
import com.aceroute.mobile.software.requests.SaveSiteRequest;
import com.aceroute.mobile.software.requests.Save_DeletePartRequest;
import com.aceroute.mobile.software.requests.Save_DeleteTaskRequest;
import com.aceroute.mobile.software.requests.SyncRO;
import com.aceroute.mobile.software.requests.updateOrderRequest;
import com.aceroute.mobile.software.response.Login;
import com.aceroute.mobile.software.utilities.ObjectHandler;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.PubnubHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.aceroute.mobile.software.utilities.XMLHandler;

import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class AceRequestHandler implements Runnable {
    public static int setNumOfItemAdded;
    public static HashMap<Long, Integer> lstOfOdrUpdated = new HashMap<Long, Integer>();
    public static long customerid = 0;
    static RespCBandServST objForCallbk;
    static HashMap<Long, RespCBandServST> objForCallbkWithId = new HashMap<Long, RespCBandServST>();
    static Intent notificationIntent;
    int noOforderUpdated, noOforderAdded, noOforderDeleted = 0;
    private Context context;
    private BlockingQueue<RequestObject> messageQueue;

    public AceRequestHandler(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        try {
            Utilities.log(context, "running AceRequestHandlerThread");
            Context context1 = context.getApplicationContext();
            Utilities.log(context, "context is : " + context1.getClass().getName());
            messageQueue = AceRouteApplication.getInstance().getMessageQueue();
            while (true) {
                try {
                    RequestObject object = (RequestObject) messageQueue.poll((long) 18 * 60 * 1000, TimeUnit.MILLISECONDS);
                    if (object != null) {
                        Response xml = processQueue(context, object);
                        Log.i("TAG50", "xml recieved after processQueue : " + xml);
                        if (xml != null) {
                            if (object.getReqId() > Utilities.DEFAULT_PUBNUB_REQUEST_ID) {
                                Utilities.log(context, "Making callback for request : " + object.getReqId());
                                xml.setId(object.getReqId());
                                Utilities.log(context, "Sending Response with millis as : " + object.getTimeInMilliSeconds());
                                RespCBandServST callBackOBJ = objForCallbkWithId.get(object.getTimeInMilliSeconds());
                                Utilities.log(context, "Sending Response to object : " + callBackOBJ);
                                 objForCallbkWithId.remove(object.getTimeInMilliSeconds());
                                callBackOBJ.setResponseCBActivity(xml);
                            } else if (object.getReqId() == Utilities.DEFAULT_PUBNUB_REQUEST_ID) {
                                PubnubHandler pbHandle = PubnubHandler.getInstance();
                                pbHandle.setResponse(xml, (PubnubRequest) object.getReqDataObject());
                            } else if (object.getReqId() == Utilities.DEFAULT_NOCALLBACK_REQUEST_ID)
                                Utilities.log(context, "Not making callback for request : " + object.getReqId());
                        } else
                            Utilities.log(context, "ERROR : response not received");
                    } else
                        continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response processQueue(Context context, RequestObject object) throws JSONException, ParseException {
        Utilities.log(context, "processQueue called");
        String action = null;
        int extraAction = 0;
        int queryType = 0;
        int exaction = object.getExtraAction();
        if (exaction != -1)
            extraAction = exaction;
        action = object.getAction();
        if (extraAction == Api.INT_API_ACTION_HANDLE_DISPLAYED_DATE_DATA) {
            queryType = DBHandler.QUERY_FOR_TEMP;
            object.setReqId(0);//laterYD
        } else
            queryType = DBHandler.QUERY_FOR_ORIG;
        boolean isFirstTime = false;
        long time = -1;
        if (time <= 0)
            time = object.getTimeInMilliSeconds();
        long jid = object.getIdPb();
        String message = "";
        String number = "";
        Log.e(Utilities.TAG, "Process queue message :" + message + " number :" + number);
        long appExitFlag = -1;
        int objectType = object.getObjecttype();
        Utilities.log(context, "action and objecttype(operation) in processQueue : " + action + " : " + objectType);
        if (action == null)
            return null;
        if (action.equals(Api.API_ACTION_SYNCALLFOROFFLINE)) {
            Log.i(Utilities.TAG, Api.API_ACTION_SYNCALLFOROFFLINE + " time : " + time);
            Response response = null;
            AceOfflineSyncEngine offlinesyncengine = AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(context);
            try {
                String synctype = ((SyncRO) object.getReqDataObject()).getType();
                response = offlinesyncengine.syncAllDataForOffline(objForCallbk, synctype);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.w(Utilities.TAG, "AceRequestHandler : Deleting all rows from database.");
            return response;
        }

        if (action.equals(Api.API_ACTION_SYNCALLFOROFFLINE_PUbNUB)) {
            Log.i(Utilities.TAG, Api.API_ACTION_SYNCALLFOROFFLINE_PUbNUB + " time : " + time);
            Response response = null;
            AceOfflineSyncEngine offlinesyncengine = AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(context);
            try {
                String synctype = "partial";
                response = offlinesyncengine.syncAllDataForOffline(objForCallbk, synctype);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.w(Utilities.TAG, "AceRequestHandler : Deleting all rows from database.");
            return response;
        }

        if (action.equals(Api.API_ACTION_SYNCBGDATA)) {
            Log.i(Utilities.TAG, Api.API_ACTION_SYNCBGDATA + " time : " + time);
            AceOfflineSyncEngine offlinesyncengine = AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(context);
            try {
                offlinesyncengine.startBgSync(objForCallbk);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.w(Utilities.TAG, "AceRequestHandler : Deleting all rows from database.");
            return null;
        }

        if (action.equals(Api.API_ACTION_SYNCALLDATA)) {
            Log.i(Utilities.TAG, Api.API_ACTION_SYNCALLDATA + " time : " + time);
            action = Order.ACTION_ORDER;
            objectType = 0;
            time = System.currentTimeMillis();
            isFirstTime = true;
            Log.w(Utilities.TAG, "AceRequestHandler : Deleting all rows from database.");
            DBEngine.emptyDatabase(context);
        }

        if (action.equals(Api.API_ACTION_SAVE_RES_GEO)) {
            try {
                String geoString = ((GeoSyncAlarmRequest) object.getReqDataObject()).getData();
                String ShouldSyncToServer = ((GeoSyncAlarmRequest) object.getReqDataObject()).getSyncToServer();
                Log.i(Utilities.TAG, "Sending request for saving res geo : " + geoString);
                String address = " ";
                String response = DBEngine.saveGeoOnServer(context, geoString, address, ShouldSyncToServer);
                Log.i(Utilities.TAG, "Result for geo save : " + response);
                if (response != null)
                    return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                else
                    return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (action.equals(SiteType.ACTION_SITE_TYPE)) {
            Response response = null;
            switch (objectType) {
                case DBHandler.DB_ACTION_INSERT:
                    response = DBEngine.insertData(context, object.getReqDataObject(), SiteType.TYPE, queryType);
                    Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE + " insert result : ");
                    if (response != null)
                        return response;
                    else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));//YD TODO insertion failed error handling

                case DBHandler.DB_ACTION_UPDATE:
                    String resp_update = DBEngine.updateData(context, object.getReqDataObject(), SiteType.TYPE, queryType);
                    Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE + " update result : " + resp_update);
                    int _update_res = Integer.parseInt(resp_update);
                    if (_update_res > 0)
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                case DBHandler.DB_ACTION_DELETE:
                    String resp_delete = DBEngine.deleteData(context, String.valueOf(jid), SiteType.TYPE, queryType);
                    Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE + " delete result : " + resp_delete);
                    if (resp_delete != null && resp_delete != "") {
                        Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE + "resp_delete != null && resp_delete != '' ");
                        int _delete_res = Integer.parseInt(resp_delete);
                        if (_delete_res > 0) {
                            Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE + "_delete_res > 0 :: " + _delete_res);
                            response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                            response.setResString(String.valueOf(jid));
                            return response;
                        } else
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                    }
                    break;
                default:
                    if(object.getReqDataObject() instanceof GetSiteTypeRequest){
                        String source = ((GetSiteTypeRequest) object.getReqDataObject()).getSource();
                        Log.i(Utilities.TAG, "getting xml for " + SiteType.ACTION_SITE_TYPE + "from " + source);
                        String cid = ((GetSiteTypeRequest) object.getReqDataObject()).getCid();
                        response = DBEngine.getSiteType(objForCallbk, context, time, cid, source);
                        if (response != null)
                            return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        else
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));

                    }else {
                        String source = ((CommonSevenReq) object.getReqDataObject()).getSource();
                        Log.i(Utilities.TAG, "getting xml for " + SiteType.ACTION_SITE_TYPE + "from " + source);
                        response = DBEngine.getSiteType(objForCallbk, context, time, source);
                        if (response != null) {
                            MapAllFragment.setMapOrderSiteType((HashMap<Long, SiteType>) response.getResponseMap());
                            GoogleMapFragment.setMapOrderSiteTypeGM((HashMap<Long, SiteType>) response.getResponseMap());
                            MapFragment.setMapOrderSiteTypeGM((HashMap<Long, SiteType>) response.getResponseMap());
                            MapFragment.setMapOrderSiteType_SK((HashMap<Long, SiteType>) response.getResponseMap());
                            return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        } else
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                    }
            }

        } else if (action.equals(Parts.ACTION_PART_UPDATE_TYPE)) {
            Response response = null;
            switch (objectType) {
                case DBHandler.DB_ACTION_INSERT:
                    response = DBEngine.insertData(context, object.getReqDataObject(), Parts.TYPE_UPDATE, queryType);
                    if (response != null)
                        return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));

                case DBHandler.DB_ACTION_UPDATE:
                    String resp_update = DBEngine.updateData(context, object.getReqDataObject(), Parts.TYPE_UPDATE, queryType);
                    Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE + " update result : " + resp_update);
                    int _update_res = Integer.parseInt(resp_update);
                    if (_update_res > 0)
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));

                case DBHandler.DB_ACTION_DELETE:
                    String resp_delete = DBEngine.deleteData(context, String.valueOf(jid), Parts.TYPE_UPDATE, queryType);
                    Log.i(Utilities.TAG, Parts.TYPE_UPDATE + " delete result : " + resp_delete);
                    if (resp_delete != null && resp_delete != "") {
                        Log.i(Utilities.TAG, Parts.TYPE_UPDATE + "resp_delete != null && resp_delete != '' ");
                        int _delete_res = Integer.parseInt(resp_delete);
                        if (_delete_res > 0) {
                            Log.i(Utilities.TAG, Parts.TYPE_UPDATE + "_delete_res > 0 :: " + _delete_res);
                            response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                            response.setResString(String.valueOf(jid));
                            return response;
                        } else
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                    }
                    break;
            }
        } else if (action.equals(ServiceType.ACTION_SERVICE_UPDATE_TYPE)) {
            Response response = null;
            switch (objectType) {
                case DBHandler.DB_ACTION_INSERT:
                    response = DBEngine.insertData(context, object.getReqDataObject(), ServiceType.TYPE_UPDATE, queryType);
                    if (response != null)
                        return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));


                case DBHandler.DB_ACTION_UPDATE:
                    String resp_update = DBEngine.updateData(context, object.getReqDataObject(), ServiceType.TYPE_UPDATE, queryType);
                    Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE + " update result : " + resp_update);
                    if (resp_update != null) {
                        int _update_res = Integer.parseInt(resp_update);
                        if (_update_res > 0)
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    } else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));

                case DBHandler.DB_ACTION_DELETE:
                    String resp_delete = DBEngine.deleteData(context, String.valueOf(jid), ServiceType.TYPE_UPDATE, queryType);
                    Log.i(Utilities.TAG, Parts.TYPE_UPDATE + " delete result : " + resp_delete);
                    if (resp_delete != null && resp_delete != "") {
                        Log.i(Utilities.TAG, Parts.TYPE_UPDATE + "resp_delete != null && resp_delete != '' ");
                        int _delete_res = Integer.parseInt(resp_delete);
                        if (_delete_res > 0) {
                            Log.i(Utilities.TAG, Parts.TYPE_UPDATE + "_delete_res > 0 :: " + _delete_res);
                            response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                            response.setResString(String.valueOf(jid));
                            return response;
                        } else
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                    }
                    break;
                default:
                    break;
            }
        } else if (action.equals(Customer.ACTION_CUSTOMER_LIST)) {
            Response response = null;
            switch (objectType) {
                case DBHandler.DB_ACTION_INSERT:
                    final long timee = new Date().getTime();
                    final Context contextf = context;
                    final RequestObject objectf = object;
                    final List<Long> ncids = new ArrayList<>();
                    if (PreferenceHandler.getUiconfigAddorder(context).equals("1")) {
                        new Thread(new Runnable() {
                            public void run() {
                                final Response responsef = DBEngine.getCustList(null, contextf, ncids, DBEngine.DATA_SOURCE_SERVER);
                                if (responsef != null) {
                                    PubnubHandler pbHandle = PubnubHandler.getInstance();
                                    pbHandle.setResponse(responsef, (PubnubRequest) objectf.getReqDataObject());
                                }
                            }
                        }).start();
                    }
                    Log.i(Utilities.TAG, Customer.ACTION_CUSTOMER_LIST + " insert result : ");
                    break;
                case DBHandler.DB_ACTION_UPDATE:
                    String resp_update = DBEngine.updateData(context, object.getReqDataObject(), Customer.TYPE, queryType);
                    Log.i(Utilities.TAG, Customer.ACTION_CUSTOMER_LIST + "update result : " + resp_update);
                    int _update_res = Integer.parseInt(resp_update);
                    if (_update_res > 0)
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                case DBHandler.DB_ACTION_DELETE:
                    String resp_delete = DBEngine.deleteData(context, String.valueOf(jid), Customer.TYPE, queryType);
                    Log.i(Utilities.TAG, Customer.ACTION_CUSTOMER_LIST + "delete result : " + resp_delete);
                    if (resp_delete != null && resp_delete != "") {
                        Log.i(Utilities.TAG, Customer.ACTION_CUSTOMER_LIST + "resp_delete != null && resp_delete != '' ");
                        int _delete_res = Integer.parseInt(resp_delete);
                        if (_delete_res > 0) {
                            Log.i(Utilities.TAG, Customer.ACTION_CUSTOMER_LIST + "_delete_res > 0 :: " + _delete_res);
                            response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                            response.setResString(String.valueOf(jid));
                            return response;
                        } else
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                    }
                    break;
                default:
                    List<Long> temcids = new ArrayList<>();
                    String source = ((CommonSevenReq) object.getReqDataObject()).getSource();
                    Log.i(Utilities.TAG, "getting xml for " + Customer.ACTION_CUSTOMER_LIST + " from " + source);
                    response = DBEngine.getCustList(null, context, temcids, source);
                    if (response != null && response.getResponseMap() != null)
                        return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
            }
        } else if (action.equals(Order.ACTION_ORDER)) {
            if (BaseTabActivity.currentApplicationState == BaseTabActivity.RESTARTED)
                DataObject.ordersXmlDataStore = DBEngine.getOrdersfromOfflineInMap(context);
            Response response = null;
            switch (objectType) {
                case DBHandler.DB_ACTION_INSERT:
                   // customerid = 0;
                    response = DBEngine.insertData(context, object.getReqDataObject(), Order.TYPE, queryType);
                    Log.i(Utilities.TAG, Order.ACTION_ORDER + "insert");
                    if (response != null) {
                        AceOfflineSyncEngine offlinesyncengine = AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(context);
                        offlinesyncengine.fillAssetCount(objForCallbk, context);
                        if (PreferenceHandler.getUiconfigAddorder(context).equals("0")) {
                            HashMap<Long, Customer> custListMap = (HashMap<Long, Customer>) DataObject.customerXmlDataStore;
                            if (customerid != 0) {
                                if (custListMap == null || (custListMap != null && !custListMap.containsKey(customerid))) {
                                    List<Long> cid = new ArrayList<>();
                                    cid.add(customerid);
                                    AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(context).syncCustList(objForCallbk, cid, time, false);
                                }
                            }
                        }
                        DBEngine.getOrderNotefromServerandSave(context, Long.toString(jid), DBHandler.QUERY_FOR_ORIG);
                        DataObject.orderNoteDataStore = DBHandler.getOrderNotes(context, 0, OrderNotes.ACTION_GET_NOTES, queryType);
                        DBEngine.getOrderPartfromServerandSave(context, Long.toString(jid), DBHandler.QUERY_FOR_ORIG);
                        DBEngine.getOrderFormfromServerandSave(context, Long.toString(jid), DBHandler.QUERY_FOR_ORIG);

                        getCustomerListData();

                        AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(context).getAllOrderMedia(objForCallbk, context, object.getReqDataObject());
                        if (BaseTabActivity.currentApplicationState == BaseTabActivity.BACKGROUND || BaseTabActivity.currentApplicationState == BaseTabActivity.RESTARTED) {
                            lstOfOdrUpdated.put(jid, 1);
                            if (((HashMap<Long, Order>) DataObject.ordersXmlDataStore) != null && ((HashMap<Long, Order>) DataObject.ordersXmlDataStore).size() > 0)
                                showNotification(context, ((HashMap<Long, Order>) DataObject.ordersXmlDataStore).size() + 1);
                            else
                                showNotification(context, 1);
                        }
                        return response;
                    } else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));

                case DBHandler.DB_ACTION_UPDATE:
                    boolean shouldDelete = false;
                    HashMap<Long, Object> xml2 = DBEngine.getOrders(context, Long.toString(jid));
                    Log.i(Utilities.TAG, "update query data : " + xml2);
                    if (xml2 != null && xml2.size() > 0) {
                        String updateOdrXml = ((PubnubRequest) object.getReqDataObject()).getXml();
                        Object[] orderList = new XMLHandler().getOrderValuesFromXML(updateOdrXml);
                        long noteCount = ((Order) orderList[0]).getNotCount();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
                        Date startdate = null;
                        try {
                            startdate = simpleDateFormat.parse(((Order) orderList[0]).getFromDate());
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }
                        if (PreferenceHandler.getOdrGetDate(context) == null) {
                            if (!Utilities.isTodayDate(context, startdate) ||
                                    !((Order) orderList[0]).getPrimaryWorkerId().contains(String.valueOf(PreferenceHandler.getResId(context)))) {
                                shouldDelete = true;
                            }
                        } else if (PreferenceHandler.getOdrGetDate(context) != null && !PreferenceHandler.getOdrGetDate(context).equals("")) {
                            if (!Utilities.chkDtEqualCurrentDt(context, startdate) ||
                                    !((Order) orderList[0]).getPrimaryWorkerId().contains(String.valueOf(PreferenceHandler.getResId(context)))) {
                                shouldDelete = true;
                            }

                        }
                        if (shouldDelete) {
                            String resp_delete = DBEngine.deleteData(context, String.valueOf(jid),
                                    Order.TYPE, DBHandler.QUERY_FOR_ORIG);
                            if (resp_delete != null && resp_delete != "") {
                                Log.i(Utilities.TAG, Order.ACTION_ORDER
                                        + "resp_delete != null && resp_delete != '' ");

                                deleteMediaForOrder(context, jid);//YD deleting media for the order

                                int _delete_res = Integer.parseInt(resp_delete);
                                if (_delete_res > 0) {
                                    Log.i(Utilities.TAG, Order.ACTION_ORDER
                                            + "_delete_res > 0 :: " + _delete_res);
                                    boolean delOrderHistory = true;
                                    HashMap<Long, Object> obj = DBEngine.getOrdersfromOfflineInMap(context);
                                    if (obj != null) {//YD check if some other order have same cid if yes then don't delete the customer order history file
                                        Order odrObj = (Order) xml2.get(jid);
                                        long localCid = odrObj.getCustomerid();

                                        Long keys[] = obj.keySet().toArray(new Long[obj.size()]);
                                        for (int i = 0; i < obj.size(); i++) {
                                            Order odrObjj = (Order) obj.get(keys[i]);
                                            if (odrObjj.getCustomerid() == localCid) {
                                                delOrderHistory = false;
                                            }
                                        }
                                        if (delOrderHistory == true) {// YD true to delete the file false if the order for same cid exist for the day
                                            PreferenceHandler.delCustomerTokens(context, "" + localCid);//YD saving to pref
                                            if (PreferenceHandler.getcustomerOdrHisDelnm(context) != null) {
                                                File file = new File(PreferenceHandler.getcustomerOdrHisDelnm(context));
                                                boolean deleted = file.delete();
                                            }
                                        }
                                    } else {//YD means no other order is there in the list_cal now
                                        PreferenceHandler.delCustomerTokens(context, "" + ((Order) xml2.get(jid)).getCustomerid());
                                        if (PreferenceHandler.getcustomerOdrHisDelnm(context) != null) {// this pref is filled in the above line of addcustomer token
                                            File file = new File(PreferenceHandler.getcustomerOdrHisDelnm(context));
                                            boolean deleted = file.delete();
                                        }
                                    }
                                    //YD below code is to delete the order history END
                                    response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                                    response.setResString(String.valueOf(jid));
                                    if (BaseTabActivity.currentApplicationState == BaseTabActivity.BACKGROUND ||
                                            BaseTabActivity.currentApplicationState == BaseTabActivity.RESTARTED) {
                                        lstOfOdrUpdated.put(jid, 2);
                                        showNotification(context, ((HashMap<Long, Order>) DataObject.ordersXmlDataStore).size() - 1);
                                    }
                                    return response;
                                } else {
                                    return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                                }
                            }
                        } else {
                            String resp_update = DBEngine.updateData(context,
                                    object.getReqDataObject(), Order.TYPE, queryType);
                            Log.i(Utilities.TAG, Order.ACTION_ORDER
                                    + "update result : " + resp_update);

                            Log.i(Utilities.TAG, Order.ACTION_ORDER
                                    + "delete order note in update for " + jid);
                            if (resp_update != null) {
                                if (noteCount > 0) {
                                    DBEngine.getOrderNotefromServerandSave(context, Long.toString(jid), DBHandler.QUERY_FOR_ORIG);//YD commented as mr arvind said
                                    DataObject.orderNoteDataStore = DBHandler.getOrderNotes(context, 0,
                                            OrderNotes.ACTION_GET_NOTES, queryType);// Yd now getting all orders and saving the data object *currently not rememeber the reason of making data object for note
                                }
                                AceOfflineSyncEngine offlinesyncengine = AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(context);
                                offlinesyncengine.fillAssetCount(objForCallbk, context);//YD update the asset count again
                                try {
                                    if (BaseTabActivity.currentApplicationState == BaseTabActivity.BACKGROUND ||
                                            BaseTabActivity.currentApplicationState == BaseTabActivity.RESTARTED) {
                                        lstOfOdrUpdated.put(jid, 3);
                                        showNotification(context, ((HashMap<Long, Order>) DataObject.ordersXmlDataStore).size());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                            } else {
                                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                            }
                        }
                    } else {// YD Check the date before inserting an order to database
                        response = DBEngine.insertData(context,
                                object.getReqDataObject(), Order.TYPE, queryType);
                        Log.i(Utilities.TAG, Order.ACTION_ORDER
                                + " insert result : ");
                        if (response != null) {
                            //YD updating order assets
                            AceOfflineSyncEngine offlinesyncengine = AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(context);
                            offlinesyncengine.fillAssetCount(objForCallbk, context);//YD update the asset count again

                            DBEngine.getOrderNotefromServerandSave(context, Long.toString(jid), DBHandler.QUERY_FOR_ORIG);
                            DataObject.orderNoteDataStore = DBHandler.getOrderNotes(context, 0,
                                    OrderNotes.ACTION_GET_NOTES, queryType);// Yd now getting all orders and saving the data object *currently not rememeber the reason of making data object for note

                            DBEngine.getOrderPartfromServerandSave(context, Long.toString(jid), DBHandler.QUERY_FOR_ORIG);
                            DBEngine.getOrderFormfromServerandSave(context, Long.toString(jid), DBHandler.QUERY_FOR_ORIG);
                            AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(context).getAllOrderMedia(objForCallbk, context, object.getReqDataObject());


                            getCustomerListData();

                            if (BaseTabActivity.currentApplicationState == BaseTabActivity.BACKGROUND ||
                                    BaseTabActivity.currentApplicationState == BaseTabActivity.RESTARTED) {
                                lstOfOdrUpdated.put(jid, 1);
                                if (((HashMap<Long, Order>) DataObject.ordersXmlDataStore) != null && ((HashMap<Long, Order>) DataObject.ordersXmlDataStore).size() > 0)
                                    showNotification(context, ((HashMap<Long, Order>) DataObject.ordersXmlDataStore).size() + 1);
                                else
                                    showNotification(context, 1);//YD where there was no order getting the orderxmlDatastore null
                            }
                            return response;
                        } else {
                            Log.w(Utilities.TAG, "insertion fail again...");
                        }
                    }
                    break;
                case DBHandler.DB_ACTION_DELETE:
                    HashMap<Long, Object> odrToDelete = DBEngine.getOrders(context, Long.toString(jid));
                    String resp_delete = DBEngine.deleteData(context, String.valueOf(jid),
                            Order.TYPE, DBHandler.QUERY_FOR_ORIG);
                    Log.i(Utilities.TAG, Order.ACTION_ORDER + "delete result : "
                            + resp_delete);
                    if (resp_delete != null && resp_delete != "") {
                        boolean delOrderHistory = true;
                        HashMap<Long, Object> obj = DBEngine.getOrdersfromOfflineInMap(context);
                        if (obj != null) {
                            Order odrObj = (Order) odrToDelete.get(jid);
                            long localCid = odrObj.getCustomerid();

                            Long keys[] = obj.keySet().toArray(new Long[obj.size()]);
                            for (int i = 0; i < obj.size(); i++) {
                                Order odrObjj = (Order) obj.get(keys[i]);
                                if (odrObjj.getCustomerid() == localCid) {
                                    delOrderHistory = false;
                                }
                            }
                            if (delOrderHistory == true) {// YD true to delete the file false if the order for same cid exist for the day
                                PreferenceHandler.delCustomerTokens(context, "" + localCid);//YD saving to pref
                                if (PreferenceHandler.getcustomerOdrHisDelnm(context) != null) {
                                    File file = new File(PreferenceHandler.getcustomerOdrHisDelnm(context));
                                    boolean deleted = file.delete();
                                }
                            }
                        } else {//YD means no other order is there in the list_cal now
                            PreferenceHandler.delCustomerTokens(context, "" + ((Order) odrToDelete.get(jid)).getCustomerid());//YD saving to pref
                            if (PreferenceHandler.getcustomerOdrHisDelnm(context) != null) {// this pref is filled in the above line of addcustomer token
                                File file = new File(PreferenceHandler.getcustomerOdrHisDelnm(context));
                                boolean deleted = file.delete();
                            }
                        }
                        Log.i(Utilities.TAG, Order.ACTION_ORDER
                                + "resp_delete != null && resp_delete != '' ");
                        int _delete_res = Integer.parseInt(resp_delete);
                        if (_delete_res > 0) {
                            deleteMediaForOrder(context, jid);
                            Log.i(Utilities.TAG, Order.ACTION_ORDER
                                    + "_delete_res > 0 :: " + _delete_res);
                            response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                            response.setResString(String.valueOf(jid));
                            if (BaseTabActivity.currentApplicationState == BaseTabActivity.BACKGROUND ||
                                    BaseTabActivity.currentApplicationState == BaseTabActivity.RESTARTED) {
                                DataObject.ordersXmlDataStore = DBEngine.getOrdersfromOfflineInMap(context);//yd added this because user is getting zero order at time of delete only

                                lstOfOdrUpdated.put(jid, 2);
                                showNotification(context, ((HashMap<Long, Order>) DataObject.ordersXmlDataStore).size());
                            }
                            return response;
                        } else {
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                        }
                    }
                    break;
                default:
                    HashMap<Long, Object> obj = DBEngine.getOrdersfromOfflineInMap(context);
                    if (obj != null) {
                        response = Utilities.createResponse("success", obj, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        MapAllFragment.setMapOrderXML((HashMap<Long, Order>) response.getResponseMap()); //setting map orderxml formap
                        GoogleMapFragment.setMapOrderXMLGM((HashMap<Long, Order>) response.getResponseMap()); // By MY
                        MapFragment.setMapOrderXMLGM((HashMap<Long, Order>) response.getResponseMap()); // By MY
                        return response;
                    } else {// setting null because lets if the order has been removed then the hash in map should also be get blank
                        MapAllFragment.setMapOrderXML(null);
                        GoogleMapFragment.setMapOrderXMLGM(null);
                        MapFragment.setMapOrderXMLGM(null);
                        return Utilities.createResponse("failure", obj, ServiceError.getEnumValstr(ServiceError.NO_DATA));// ServiceError.getEnumVal(Enumm.LOGIN_REQUEST)
                    }
            }
        } else if (action.equals(Api.API_ACTION_UPDATE_ORDER_FIELD)) {//"saveorderfld" ACTION_SAVE_ORDER_FLD
            Response response = null;
            Log.i(Utilities.TAG, "updation for "
                    + Api.API_ACTION_UPDATE_ORDER_FIELD);
            Log.e(Utilities.TAG, "queryType : " + queryType);
            String resp_update = DBEngine.updateData(context, object.getReqDataObject(),
                    Order.TYPE_UPDATE, queryType);
            if (resp_update != null) {

                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            } else {
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
            }
        } else if (action.equals(Order.ACTION_SAVE_ORDER)) {
            Response response = null;
            Log.i(Utilities.TAG, "saving order with action "
                    + Order.ACTION_SAVE_ORDER);
            response = DBEngine.insertData(context, object.getReqDataObject(),
                    Order.TYPE_UPDATE, queryType);
            return response;
        } else if (action.equals(Order.ACTION_CUSTOMER_HISTORY)) {// get file for customer history
            String fkey = new XMLHandler(context).getCustTokenValue(((PubnubRequest) object.getReqDataObject()).getXml());//YD parsing token got from pubnub
            CustHistoryTokenGroup availableTokens = PreferenceHandler.getCustomerTokens(context);//YD list_cal of token saved when inserting order to db
            if (availableTokens != null && availableTokens.getCustHistTokenGrp() != null) {
                for (Long key : availableTokens.getCustHistTokenGrp().keySet()) {
                    if (availableTokens.getCustHistTokenGrp().get(key).getCustToken().equals(fkey)) {
                        CustHistoryToken CurrentCustHistTokenObj = availableTokens.getCustHistTokenGrp().get(key);
                        String cid = String.valueOf(CurrentCustHistTokenObj.getCustId());
                        String filePath = DBEngine.getCustHistoryXlsfromServer(context, cid, fkey);
                        if (filePath != null) {
                            CurrentCustHistTokenObj.setPath(filePath);
                            PreferenceHandler.UpdateCustomerTokens(context, availableTokens);
                        }
                    }
                }
            }

            Response response = null;
            return response;
        } else if (action.equals(ClientSite.ACTION_GET_CLIENT_SITE)) {// YD for part type pubnub
            Response response = null;
            switch (objectType) {
                case DBHandler.DB_ACTION_INSERT:
                    response = DBEngine.insertData(context, object.getReqDataObject(),
                            Parts.TYPE, queryType);
                    Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                            + " insert result : ");
                    if (response != null) {
                        return response;
                    } else {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));//YD TODO insertion failed error handling
                    }

                case DBHandler.DB_ACTION_UPDATE:
                    String resp_update = DBEngine.updateData(context, object.getReqDataObject(),
                            Parts.TYPE, queryType);
                    Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                            + " update result : " + resp_update);
                    int _update_res = Integer.parseInt(resp_update);
                    if (_update_res > 0)
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                case DBHandler.DB_ACTION_DELETE:
                    String resp_delete = DBEngine.deleteData(context,
                            String.valueOf(jid), Parts.TYPE, queryType);
                    Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                            + " delete result : " + resp_delete);
                    if (resp_delete != null && resp_delete != "") {
                        Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                                + "resp_delete != null && resp_delete != '' ");
                        int _delete_res = Integer.parseInt(resp_delete);
                        if (_delete_res > 0) {
                            Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                                    + "_delete_res > 0 :: " + _delete_res);
                            response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                            response.setResString(String.valueOf(jid));
                            return response;
                        } else {
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                        }
                    }
                    break;
                default:
                    String source = ((CommonSevenReq) object.getReqDataObject()).getSource();
                    Log.i(Utilities.TAG, "getting xml for "
                            + ClientSite.ACTION_GET_CLIENT_SITE + "from source=" + source);
                    response = DBEngine.getcmpyclientsite(objForCallbk, context, time, source);
                    if (response != null) {
                        return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    } else {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                    }
            }
        } else if (action.equals(Shifts.ACTION_GET_SHIFTS)) {// KB for shift type pubnub
            Response response = null;
            switch (objectType) {
                case DBHandler.DB_ACTION_INSERT:
                    response = DBEngine.insertData(context, object.getReqDataObject(),
                            Shifts.TYPE, queryType);
                    Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                            + " insert result : ");
                    if (response != null) {
                        return response;
                    } else {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));//YD TODO insertion failed error handling
                    }

                case DBHandler.DB_ACTION_UPDATE:
                    String resp_update = DBEngine.updateData(context, object.getReqDataObject(),
                            Parts.TYPE, queryType);
                    Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                            + " update result : " + resp_update);
                    int _update_res = Integer.parseInt(resp_update);
                    if (_update_res > 0)
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                case DBHandler.DB_ACTION_DELETE:
                    String resp_delete = DBEngine.deleteData(context,
                            String.valueOf(jid), Parts.TYPE, queryType);
                    Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                            + " delete result : " + resp_delete);
                    if (resp_delete != null && resp_delete != "") {
                        Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                                + "resp_delete != null && resp_delete != '' ");
                        int _delete_res = Integer.parseInt(resp_delete);
                        if (_delete_res > 0) {
                            Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                                    + "_delete_res > 0 :: " + _delete_res);
                            response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                            response.setResString(String.valueOf(jid));
                            return response;
                        } else {
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                        }
                    }
                    break;
                case DBHandler.DB_ACTION_REFRESH:
                    response = DBEngine.getshifts(objForCallbk, context, time, DBEngine.DATA_SOURCE_SERVER);
                    break;
                default:
                    String source = ((CommonSevenReq) object.getReqDataObject()).getSource();
                    Log.i(Utilities.TAG, "getting xml for "
                            + Shifts.ACTION_GET_SHIFTS + "from source=" + source);
                    response = DBEngine.getshifts(objForCallbk, context, time, source);
                    if (response != null) {
                        return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    } else {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                    }
            }
        } else if (action.equals(Parts.ACTION_PART_TYPE)) {// YD for part type pubnub
            Response response = null;
            switch (objectType) {
                case DBHandler.DB_ACTION_INSERT:
                    response = DBEngine.insertData(context, object.getReqDataObject(),
                            Parts.TYPE, queryType);
                    Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                            + " insert result : ");
                    if (response != null) {
                        return response;
                    } else {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));//YD TODO insertion failed error handling
                    }

                case DBHandler.DB_ACTION_UPDATE:
                    String resp_update = DBEngine.updateData(context, object.getReqDataObject(),
                            Parts.TYPE, queryType);
                    Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                            + " update result : " + resp_update);
                    int _update_res = Integer.parseInt(resp_update);
                    if (_update_res > 0)
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                case DBHandler.DB_ACTION_DELETE:
                    String resp_delete = DBEngine.deleteData(context,
                            String.valueOf(jid), Parts.TYPE, queryType);
                    Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                            + " delete result : " + resp_delete);
                    if (resp_delete != null && resp_delete != "") {
                        Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                                + "resp_delete != null && resp_delete != '' ");
                        int _delete_res = Integer.parseInt(resp_delete);
                        if (_delete_res > 0) {
                            Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                                    + "_delete_res > 0 :: " + _delete_res);
                            response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                            response.setResString(String.valueOf(jid));
                            return response;
                        } else {
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                        }
                    }
                    break;
                default:
                    String source = ((CommonSevenReq) object.getReqDataObject()).getSource();
                    Log.i(Utilities.TAG, "getting xml for "
                            + Parts.ACTION_PART_TYPE + "from source=" + source);
                    response = DBEngine.getPartType(objForCallbk, context, time, source);
                    if (response != null) {
                        return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    } else {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                    }
            }
        } else if (action.equals(ServiceType.ACTION_SERVICE_TYPE)) {// YD for ServiceType pubnub
            Response response = null;
            switch (objectType) {
                case DBHandler.DB_ACTION_INSERT:
                    response = DBEngine.insertData(context, object.getReqDataObject(),
                            ServiceType.TYPE, queryType);
                    Log.i(Utilities.TAG, ServiceType.ACTION_SERVICE_TYPE
                            + " insert result : ");
                    if (response != null) {
                        return response;
                    } else {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));//YD TODO insertion failed error handling
                    }
                case DBHandler.DB_ACTION_UPDATE:
                    String resp_update = DBEngine.updateData(context, object.getReqDataObject(),
                            ServiceType.TYPE, queryType);
                    Log.i(Utilities.TAG, ServiceType.ACTION_SERVICE_TYPE
                            + " update result : " + resp_update);
                    int _update_res = Integer.parseInt(resp_update);
                    if (_update_res > 0)
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                case DBHandler.DB_ACTION_DELETE:
                    String resp_delete = DBEngine.deleteData(context,
                            String.valueOf(jid), ServiceType.TYPE, queryType);
                    Log.i(Utilities.TAG, ServiceType.ACTION_SERVICE_TYPE
                            + " delete result : " + resp_delete);
                    if (resp_delete != null && resp_delete != "") {
                        Log.i(Utilities.TAG, ServiceType.ACTION_SERVICE_TYPE
                                + "resp_delete != null && resp_delete != '' ");
                        int _delete_res = Integer.parseInt(resp_delete);
                        if (_delete_res > 0) {
                            Log.i(Utilities.TAG, ServiceType.ACTION_SERVICE_TYPE
                                    + "_delete_res > 0 :: " + _delete_res);
                            response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                            response.setResString(String.valueOf(jid));
                            return response;
                        } else {
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                        }
                    }
                    break;
                default:
                    String source = ((CommonSevenReq) object.getReqDataObject()).getSource();
                    Log.i(Utilities.TAG, "getting xml for "
                            + ServiceType.ACTION_SERVICE_TYPE + "Source = " + source);
                    response = DBEngine.getServiceType(objForCallbk, context, time, source);
                    if (response != null) {
                        MapAllFragment.setMapTasktypeXML((HashMap<Long, ServiceType>) response.getResponseMap());
                        GoogleMapFragment.setMapTasktypeXMLGM((HashMap<Long, ServiceType>) response.getResponseMap());
                        MapFragment.setMapTasktypeXMLGM((HashMap<Long, ServiceType>) response.getResponseMap());
                        MapFragment.setMapTasktypeXML_SK((HashMap<Long, ServiceType>) response.getResponseMap());
                        return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    } else {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                    }
            }
        } else if (action.equals(OrderTypeList.ACTION_ORDER_TYPE)) {// YD for order type pubnub
            Response response = null;
            switch (objectType) {
                case DBHandler.DB_ACTION_INSERT:
                    response = DBEngine.insertData(context, object.getReqDataObject(),//YD querytype is added later
                            OrderTypeList.TYPE, queryType);
                    Log.i(Utilities.TAG, OrderTypeList.ACTION_ORDER_TYPE
                            + " insert result : ");
                    if (response != null) {
                        return response;
                    } else {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));//YD TODO insertion failed
                    }
                case DBHandler.DB_ACTION_UPDATE:
                    String resp_update = DBEngine.updateData(context, object.getReqDataObject(),
                            OrderTypeList.TYPE, queryType);
                    Log.i(Utilities.TAG, OrderTypeList.ACTION_ORDER_TYPE
                            + " update result : " + resp_update);

                    int _update_res = Integer.parseInt(resp_update);
                    if (_update_res > 0)
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                case DBHandler.DB_ACTION_DELETE:
                    String resp_delete = DBEngine.deleteData(context,
                            String.valueOf(jid), OrderTypeList.TYPE, queryType);
                    Log.i(Utilities.TAG, OrderTypeList.ACTION_ORDER_TYPE
                            + " delete result : " + resp_delete);
                    if (resp_delete != null && resp_delete != "") {
                        Log.i(Utilities.TAG, OrderTypeList.ACTION_ORDER_TYPE
                                + "resp_delete != null && resp_delete != '' ");
                        int _delete_res = Integer.parseInt(resp_delete);
                        if (_delete_res > 0) {
                            Log.i(Utilities.TAG, OrderTypeList.ACTION_ORDER_TYPE
                                    + "_delete_res > 0 :: " + _delete_res);
                            response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                            response.setResString(String.valueOf(jid));
                            return response;
                        } else {
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                        }

                    }
                    break;
                default:
                    String source = ((CommonSevenReq) object.getReqDataObject()).getSource();
                    Log.i(Utilities.TAG, "getting xml for "
                            + OrderTypeList.ACTION_ORDER_TYPE + " Source=" + source);
                    response = DBEngine.getOrderType(objForCallbk, context, time, source);
                    if (response != null) {
                        return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    } else {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                    }
            }
        } else if (action.equals(Worker.ACTION_WORKER_LIST)) {// YD for Worker pubnub (getres)
            Response response = null;
            switch (objectType) {
                case DBHandler.DB_ACTION_INSERT:
                    response = DBEngine.insertData(context, object.getReqDataObject(),
                            Worker.TYPE, queryType);
                    Log.i(Utilities.TAG, Worker.ACTION_WORKER_LIST
                            + " insert result : ");
                    if (response != null) {
                        return response;
                    } else {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));//YD TODO insertion failed
                    }
                case DBHandler.DB_ACTION_UPDATE:
                    String resp_update = DBEngine.updateData(context, object.getReqDataObject(),
                            Worker.TYPE, queryType);
                    Log.i(Utilities.TAG, Worker.ACTION_WORKER_LIST
                            + "update result : " + resp_update);
                    int _update_res = Integer.parseInt(resp_update);
                    if (_update_res > 0)
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                case DBHandler.DB_ACTION_DELETE:
                    long cur_res_id = PreferenceHandler.getResId(context);
                    if (jid == cur_res_id) {
                        Log.e(Utilities.TAG, "Deleting data of current res_id");
                        Log.e(Utilities.TAG,
                                "current res delete request : Deleting all rows from database.");
                        DBEngine.emptyDatabase(context);

                        Log.e(Utilities.TAG, "Making user preference empty.");
                      //  PreferenceHandler.setResId(context, 0);
                       // PreferenceHandler.setCompanyId(context, null);
                        PreferenceHandler.setMtoken(context, null);
                        PreferenceHandler.setPrefRangeDates(context, null, null);
                        PreferenceHandler.setAcerouteGeoTime(context, 0);
                        PreferenceHandler.setAcerouteHeartbeat(context, 0);
                        PreferenceHandler.setOldDataCleanTime(context, 0);

                        Log.e(Utilities.TAG, "Stopping service.");
                        Intent stopServiceIntent = new Intent(
                                context.getApplicationContext(),
                                com.aceroute.mobile.software.AceRouteService.class);
                        context.stopService(stopServiceIntent);

                        if (appExitFlag == 1) {// YD TODO
                            Log.i(Utilities.TAG, "Not exiting from app.");
                        } else {
                            Log.e(Utilities.TAG, "Stopping activity.");
                            AceRouteApplication.getInstance()  //YD check
                                    .stopApplication();
                        }
                        try {
                            messageQueue.clear();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.e(Utilities.TAG, "Stopping associated alarms.");
                        PreferenceHandler.setAlarmState(context, false);
                        PreferenceHandler.setTidStatus(context, false);
                        Utilities.stopAlarms(context);
                        if (object != null) {
                            object.setReqId(Utilities.DEFAULT_NOCALLBACK_REQUEST_ID);
                        }
                        response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        response.setResString(String.valueOf(jid));
                        return response;
                    } else {
                        String resp_delete = DBEngine.deleteData(context,
                                String.valueOf(jid), Worker.TYPE, queryType);
                        Log.i(Utilities.TAG, Worker.ACTION_WORKER_LIST
                                + "delete result : " + resp_delete);
                        if (resp_delete != null && !resp_delete.equals("")) {
                            Log.i(Utilities.TAG, Worker.ACTION_WORKER_LIST
                                    + "resp_delete != null && resp_delete != '' ");
                            int _delete_res = Integer.parseInt(resp_delete);
                            if (_delete_res > 0) {
                                Log.i(Utilities.TAG, Worker.ACTION_WORKER_LIST
                                        + "_delete_res > 0 :: " + _delete_res);
                                response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                                response.setResString(String.valueOf(jid));
                                return response;
                            } else {
                                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                            }
                        }
                    }
                    break;
                default:
                    String source = ((CommonSevenReq) object.getReqDataObject()).getSource();
                    Log.i(Utilities.TAG, "getting xml for "
                            + Worker.ACTION_WORKER_LIST + "Source=" + source);
                    response = DBEngine.getRes(objForCallbk, context, time, source);
                    if (response != null) {
                        return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    } else {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                    }
            }
        } else if (action.equals(Worker.ACTION_WORKER_LOGOUT_PUBNUB)) {
            Response response = null;
            if (object.getIdPb() != -1 && PreferenceHandler.getResId(context) == object.getIdPb()) {
                AceOfflineSyncEngine offlinesyncengine = AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(context);//YD first syncing the data at time of logout
                try {
                    String synctype = "semipartial_logout";
                    response = offlinesyncengine.syncAllDataForOffline(objForCallbk, synctype);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                Log.e(Utilities.TAG, "logout pubnub is for some other user");
            return response;


        } else if (action.equals(Worker.ACTION_WORKER_LOGOUT)) {

            Response response = null;
            long cur_res_id = PreferenceHandler.getResId(context);
            jid = Long.valueOf(((LogoutRequest) object.getReqDataObject()).getId());//YD
            if (jid == cur_res_id) {
                Log.e(Utilities.TAG, "Deleting data of current res_id");
                Log.e(Utilities.TAG,
                        "current res delete request : Deleting all rows from database.");
                DBEngine.emptyDatabase(context);

                Log.e(Utilities.TAG, "Making user preference empty.");
                Log.e(Utilities.TAG, "Stopping service.");
                Intent stopServiceIntent = new Intent(
                        context.getApplicationContext(),
                        com.aceroute.mobile.software.AceRouteService.class);
                context.stopService(stopServiceIntent);

                appExitFlag = ((LogoutRequest) object.getReqDataObject()).getAppexit();//YD
                if (appExitFlag == 1) {
                    Log.i(Utilities.TAG, "Not exiting from app.");
                } else {
                    Log.e(Utilities.TAG, "Stopping activity.");
                    AceRouteApplication.getInstance().stopApplication();   //YD check
                }
                try {
                    messageQueue.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.e(Utilities.TAG, "Stopping associated alarms");
                PreferenceHandler.setAlarmState(context, false);
                PreferenceHandler.setTidStatus(context, false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Utilities.stopAlarmManager(context);
                }
                else {
                    Utilities.stopAlarms(context);
                }

                Map<String, String> reqMap = new HashMap<String, String>();
                Log.e(Utilities.TAG,
                        "Internet is connected waiting for login..");
                reqMap.put("geo", Utilities.getLocation(context));
                reqMap.put("stmp", "" + Utilities.getCurrentTime());
                reqMap.put("action", Worker.ACTION_WORKER_MLOGOUT_SERVER);

                try {
                    HttpConnection.get(context, "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", reqMap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                response.setResString(String.valueOf(jid));
                return response;
            } else {
                String resp_delete = DBEngine.deleteData(context,
                        String.valueOf(jid), Worker.TYPE, queryType);
                Log.i(Utilities.TAG, Worker.ACTION_WORKER_LIST
                        + "delete result : " + resp_delete);
                if (resp_delete != null && !resp_delete.equals("")) {
                    Log.i(Utilities.TAG, Worker.ACTION_WORKER_LIST
                            + "resp_delete != null && resp_delete != '' ");
                    int _delete_res = Integer.parseInt(resp_delete);
                    if (_delete_res > 0) {
                        Log.i(Utilities.TAG, Worker.ACTION_WORKER_LIST
                                + "_delete_res > 0 :: " + _delete_res);
                        response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        response.setResString(String.valueOf(jid));
                        return response;
                    } else {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                    }
                }
            }

        } else if (action.equals(Login.CHANGE_PASSWORD)) {
            Response response = null;
            Log.i(Utilities.TAG, "getting xml for "
                    + Api.API_ACTION_SAVE_ORDER_TASK);
            ChangePassword chngPwd = (ChangePassword) object.getReqDataObject();

            Map<String, String> reqMap = new HashMap<String, String>();
            Log.e(Utilities.TAG,
                    "Internet is connected waiting for login..");
            reqMap.put("pcd", chngPwd.getNewPassword());
            reqMap.put("action", Login.CHANGE_PASSWORD);
            try {
                HttpConnection.get(context, "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", reqMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            response.setResString(String.valueOf(jid));
            return response;
//        } else if (action.equals(Api.API_ACTION_SAVE_ORDER_TASK) && PreferenceHandler.getTasksConfig(context) != null && PreferenceHandler.getTasksConfig(context).equals("1")) {
        } else if (action.equals(Api.API_ACTION_SAVE_ORDER_TASK) ) {
            //OrderTask.ACTION_SAVE_ORDER_TASK
            Response response = null;
            Log.i(Utilities.TAG, "getting xml for "
                    + Api.API_ACTION_SAVE_ORDER_TASK);
            response = DBEngine.insertData(context, object.getReqDataObject(),
                    OrderTask.TYPE, queryType);
            if (response != null) {
                Response alltask = DBEngine.getOrderTaskforOffline(context, "0");
                MapFragment.setMapAllTasksXML_SK((HashMap<Long, OrderTask>) alltask.getResponseMap());
                MapFragment.setMapAllTasksXMLGM((HashMap<Long, OrderTask>) alltask.getResponseMap()); //By MY
                return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            } else
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
        } else if (action.equals(OrderTask.ACTION_GET_ORDER_TASK)) {
//        } else if (action.equals(OrderTask.ACTION_GET_ORDER_TASK) && PreferenceHandler.getTasksConfig(context) != null && PreferenceHandler.getTasksConfig(context).equals("1")) {
            Response response = null;
            Log.i(Utilities.TAG, "getting xml for "
                    + OrderTask.ACTION_GET_ORDER_TASK);
            String oid = ((GetPart_Task_FormRequest) object.getReqDataObject()).getOid();
            queryType = DBHandler.QUERY_FOR_ORIG;
            response = DBEngine
                    .getOrderTaskforOffline(context, oid);
            MapAllFragment.setMapTasksXML((HashMap<Long, OrderTask>) response.getResponseMap());
            GoogleMapFragment.setMapTasksXMLGM((HashMap<Long, OrderTask>) response.getResponseMap()); // By MY
            MapFragment.setMapTasksXMLGM((HashMap<Long, OrderTask>) response.getResponseMap()); // By MY
            MapFragment.setMapTasksXML_SK((HashMap<Long, OrderTask>) response.getResponseMap()); // By MY
            return response;
//        } else if (action.equals(OrderTask.ACTION_GET_ALL_ORDER_TASK) && PreferenceHandler.getTasksConfig(context) != null && PreferenceHandler.getTasksConfig(context).equals("1")) {
        } else if (action.equals(OrderTask.ACTION_GET_ALL_ORDER_TASK)) {
            Response response = null;
            Log.i(Utilities.TAG, "getting xml for "
                    + OrderTask.ACTION_GET_ALL_ORDER_TASK);

            response = DBEngine
                    .getOrderTaskforOffline(context, "0");
            MapFragment.setMapAllTasksXMLGM((HashMap<Long, OrderTask>) response.getResponseMap()); //By MY
            MapFragment.setMapAllTasksXML_SK((HashMap<Long, OrderTask>) response.getResponseMap()); //By MY
            return response;
//        } else if (action.equals(OrderTask.ACTION_DELETE_ORDER_TASK) && PreferenceHandler.getTasksConfig(context) != null && PreferenceHandler.getTasksConfig(context).equals("1")) {
        } else if (action.equals(OrderTask.ACTION_DELETE_ORDER_TASK)) {
            Response response = null;
            Log.i(Utilities.TAG, "getting xml for "
                    + OrderTask.ACTION_DELETE_ORDER_TASK);
            String id = ((Save_DeleteTaskRequest) object.getReqDataObject()).getDataObj().getTaskId();

            if (id != null && !id.equals("")) {
                String resp = DBEngine.deleteData(context, id, OrderTask.TYPE,
                        queryType);
                if (resp != null) {
                    response = DBEngine.getOrderTaskforOffline(context, "0");// returns the ordertaskData
                    MapFragment.setMapAllTasksXMLGM((HashMap<Long, OrderTask>) response.getResponseMap()); //By MY
                    MapFragment.setMapAllTasksXML_SK((HashMap<Long, OrderTask>) response.getResponseMap()); //By MY
                    return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));//kept null instead of map because it only returns string while deleting the part
                } else {
                    return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                }
            }
        } else if (action.equals(OrderPart.ACTION_SAVE_ORDER_PART)) {
            Response response = null;
            Log.i(Utilities.TAG, "getting xml for "
                    + OrderPart.ACTION_SAVE_ORDER_PART);
            response = DBEngine.insertData(context, object.getReqDataObject(),
                    OrderPart.TYPE, queryType);
            if (response != null) {
                return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            } else {
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
            }
        } else if (action.equals(MessagePanic.ACTION_WORKER_MESSAGE_PANIC)) {
            Response response = null;
            Log.i(Utilities.TAG, "getting xml for "
                    + MessagePanic.ACTION_WORKER_MESSAGE_PANIC);
            response = DBEngine.insertData(context, object.getReqDataObject(),
                    MessagePanic.TYPE, queryType);
            if (response != null) {
                return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            } else {
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
            }
        } else if (action.equals(Shifts.ACTION_SAVE_SHIFTS)) {//kuber
            Response response = null;
            Log.i(Utilities.TAG, "getting xml for "
                    + Shifts.ACTION_SAVE_SHIFTS);
            response = DBEngine.insertData(context, object.getReqDataObject(),
                    Shifts.TYPE, queryType);
            if (response != null) {
                return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            } else {
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
            }
        } else if (action.equals(Shifts.ACTION_DELETE_SHIFTS)) {//kuber

            long id = ((SaveShiftReq) object.getReqDataObject()).getId();

            String result = DBEngine.deleteData(context,
                    String.valueOf(id), Shifts.TYPE, queryType);

            XMLHandler xmlhandler = new XMLHandler(context);
            Document doc = xmlhandler.getDomElement(result);
            NodeList nl = doc.getElementsByTagName(XMLHandler.KEY_DATA);
            for (int i = 0; i < nl.getLength(); i++) {// loop should not be requiredhere
                Element e = (Element) nl.item(i);
                String success = xmlhandler.getValue(e,
                        XMLHandler.KEY_DATA_SUCCESS);
                if (!success.equals(XMLHandler.KEY_DATA_RESP_FAIL)) {

                    if (Utilities.checkInternetConnection(context, false)) {
                        DBEngine.syncDataToSever(context, Shifts.TYPE);
                    }
                    Response responseObj = new Response();
                    responseObj.setStatus("success");
                    responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    return responseObj;
                }
            }
            Log.i(Utilities.TAG, Shifts.ACTION_DELETE_SHIFTS + "delete result : "
                    + result);
        } else if (action.equals(Shifts.DELETE_MULTIPLE_SHIFTS)) {//kuber
            String id = ((SaveShiftReq) object.getReqDataObject()).getRecs();

            String result = DBEngine.deleteData(context,
                    String.valueOf(id), Shifts.TYPE, queryType);

            XMLHandler xmlhandler = new XMLHandler(context);
            Document doc = xmlhandler.getDomElement(result);
            NodeList nl = doc.getElementsByTagName(XMLHandler.KEY_DATA);
            for (int i = 0; i < nl.getLength(); i++) {// loop should not be requiredhere
                Element e = (Element) nl.item(i);
                String success = xmlhandler.getValue(e,
                        XMLHandler.KEY_DATA_SUCCESS);
                if (!success.equals(XMLHandler.KEY_DATA_RESP_FAIL)) {

                    if (Utilities.checkInternetConnection(context, false)) {
                        DBEngine.syncDataToSever(context, Shifts.TYPE);
                    }
                    Response responseObj = new Response();
                    responseObj.setStatus("success");
                    responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    return responseObj;
                }
            }
            Log.i(Utilities.TAG, Shifts.DELETE_MULTIPLE_SHIFTS + "delete result : "
                    + result);
        } else if (action.equals(Shifts.ACTION_EDIT_SHIFTS)) {//kuber
            SaveShiftReq shiftReq = ((SaveShiftReq) object.getReqDataObject());
            String resp_update = DBEngine.updateData(context, shiftReq, Shifts.TYPE, queryType);
            int _update_res = 0;
            if (resp_update != null && !resp_update.trim().equals("")) {
                _update_res = Integer.parseInt(resp_update);
                if (_update_res > 0) {
                    if (Utilities.checkInternetConnection(context, false)) {
                        DBEngine.syncDataToSever(context, Shifts.TYPE);
                    }
                    Response responseObj = new Response();
                    responseObj.setStatus("success");
                    responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    return responseObj;
                }
            }
        } else if (action.equals(OrderPart.ACTION_GET_ORDER_PART)) {
            Response response = null;
            Log.i(Utilities.TAG, "getting xml for "
                    + OrderPart.ACTION_GET_ORDER_PART);
            String oid = ((GetPart_Task_FormRequest) object.getReqDataObject()).getOid();
            response = DBEngine
                    .getOrderPartforOffline(context, oid);

            if (response != null) {
                return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            } else {
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
            }
        } else if (action.equals(OrderPart.ACTION_DELETE_ORDER_PART)) {
            Response response = null;
            Log.i(Utilities.TAG, "getting xml for "
                    + OrderPart.ACTION_DELETE_ORDER_PART);
            String id = ((Save_DeletePartRequest) object.getReqDataObject()).getDataObj().getPartId();
            Utilities.log(context, "id to delete : " + id);
            if (id != null && !id.equals("")) {
                String resp = DBEngine.deleteData(context, id, OrderPart.TYPE,
                        queryType);
                if (resp != null) {
                    return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));//kept null instead of map because it only returns string while deleting the part
                } else {
                    return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                }
            }
        }
        else if (action.equals(Form.ACTION_GET_ORDER_FORM)) {
            Response response = null;
            Log.i(Utilities.TAG, "getting xml for "
                    + Form.ACTION_GET_ORDER_FORM);
            String oid = ((GetPart_Task_FormRequest) object.getReqDataObject()).getOid();
            response = DBEngine
                    .getOrderFormforOffline(context, oid);

            if (response != null) {
                return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            } else {
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
            }
        }
        else if (action.equals(Form.ACTION_DELETE_ORDER_FORM)) {
            Response response = null;
            Log.i(Utilities.TAG, "getting xml for "
                    + Form.ACTION_DELETE_ORDER_FORM);
            String id = ((Save_DeleteTaskRequest) object.getReqDataObject()).getDataObj().getTaskId();
            Utilities.log(context, "id to delete : " + id);
            if (id != null && !id.equals("")) {
                String resp = DBEngine.deleteData(context, id, Form.TYPE,
                        queryType);
                if (resp != null) {
                    return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));//kept null instead of map because it only returns string while deleting the part
                } else {
                    return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                }
            }
        }
        else if (action.equals(Assets.ACTION_SAVE_ORDER_ASSETS)) {
            Response response = null;
            Log.i(Utilities.TAG, "getting xml for "
                    + Assets.ACTION_SAVE_ORDER_ASSETS);
            response = DBEngine.insertData(context, object.getReqDataObject(),
                    Assets.TYPE, queryType);
            if (response != null) {
                return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            } else {
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
            }
        } else if (action.equals(Api.API_ACTION_STATUS_TYPE)) {
            Log.i(Utilities.TAG, "called for getstatustype");
            Response response = null;
            String source = ((CommonSevenReq) object.getReqDataObject()).getSource();
            response = DBEngine.getStatusType(objForCallbk, context, time, source);
            if (response != null) {
                return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            } else {
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
            }

        } else if (action.equals(Api.API_ACTION_PRIORITY_TYPE)) {
            return ObjectHandler.getObjectForOrderStat_Prio("priority");
        } else if (action.equals(OrderMedia.ACTION_MEDIA_SAVE)) {//YD
            DataObject.orderPicsXmlStore = DBEngine.getOrdersfromOfflineInMap(context);
            Response response = null;
            Log.i(Utilities.TAG, "getting xml for " + OrderMedia.ACTION_MEDIA_SAVE);
            response = DBEngine.insertData(context, object.getReqDataObject(), OrderMedia.TYPE_SAVE, queryType);

            if (response != null) {
                return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            } else {
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
            }
        } else if (action.equals(OrderMedia.ACTION_MEDIA_DELETE)) {
            Log.i(Utilities.TAG, "getting xml for "
                    + OrderMedia.ACTION_MEDIA_DELETE);
            String id = ((DeleteMediaRequest) object.getReqDataObject()).getFileId();
            Utilities.log(context, "id to delete : " + id);
            String resp = null;
            if (id != null && !id.equals("")) {
                resp = "" + DBEngine.deleteData(context, id, OrderMedia.TYPE_DELETE, queryType);
                if (resp != null)
                    return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                else
                    return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
            }
        } else if (action.equals(OrderMedia.ACTION_GET_MEDIA)) {
            try {

                String id = ((GetFileMetaRequest) object.getReqDataObject()).getOid();
                String frmkey = ((GetFileMetaRequest) object.getReqDataObject()).getFrmkey();
                HashMap<Long, Object> map;
                if (!frmkey.equalsIgnoreCase("") || !frmkey.equals("")) {
                    map = DBEngine.getOrderMediaformforOffline(context, id,frmkey);
                } else {
                    map = DBEngine.getOrderMediaforOffline(context, id);
                }

                if (map != null) {
                    return Utilities.createResponse("success", map, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                } else {
                    return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (action.equals(OrderNotes.ACTION_GET_NOTES)) {
            switch (objectType) {
                case DBHandler.DB_ACTION_UPDATE:
                    String response = DBEngine.getOrderNotefromServerandSave(context, Long.toString(jid), DBHandler.QUERY_FOR_ORIG);
                    if (response != null) {
                        // YD first make response null if there is failure in getOrderNotefromServerandSave
                        int _update_res = Integer.parseInt(response);
                        if (_update_res > 0)
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        else
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                    }
                    break;
                default:
                    try {
                        String Oid = ((updateOrderRequest) object.getReqDataObject()).getId();
                        long long_id = Long.valueOf(Oid);
                        HashMap<Long, Object> note = DBEngine.getOrderNotes(context, long_id,
                                OrderNotes.ACTION_GET_NOTES, queryType);
                        if (note != null) {
                            return Utilities.createResponse("success", note, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        } else {
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        } else if (action.equals(OrderNotes.ACTION_NOTES_SAVE)) {
            Response response = null;
            Log.i(Utilities.TAG, "getting xml for "
                    + OrderNotes.ACTION_NOTES_SAVE);
            response = DBEngine.insertData(context, object.getReqDataObject(),
                    OrderNotes.TYPE_SAVE, queryType);

            if (response != null)
                return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            else
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
        } else if (action.equals(Site.ACTION_SITE)) {//YD pubnub for getsite
            Response response = null;
            switch (objectType) {
                case DBHandler.DB_ACTION_INSERT:
                    response = DBEngine.insertData(context, object.getReqDataObject(), Site.TYPE, queryType);
                    // vicky added to update mapview
                    response = DBEngine.getSite(objForCallbk, context, time, 0, DBEngine.DATA_SOURCE_LOCAL);
                    MapAllFragment.setMapCustSiteXML((HashMap<Long, Site>) response.getResponseMap());
                    //ArcgisMap.setMapCustSiteXML((HashMap<Long, Site>) response.getResponseMap());  //YD commented arcmap
                    MapFragment.setMapCustSiteXML_SK((HashMap<Long, Site>) response.getResponseMap());
                    if (response != null)
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    else {
                        Log.w(Utilities.TAG, "insertion fail, updating...");
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                    }

                case DBHandler.DB_ACTION_UPDATE:
                    String resp_update = DBEngine.updateData(context, object.getReqDataObject(),
                            Site.TYPE, queryType);
                    Log.i(Utilities.TAG, Site.ACTION_SITE + "update result : "
                            + resp_update);

                    int _update_res = 0;
                    if (resp_update != null && !resp_update.trim().equals("")) {
                        _update_res = Integer.parseInt(resp_update);
                    }
                    if (_update_res > 0)
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));

                case DBHandler.DB_ACTION_DELETE:
                    String resp_delete = DBEngine.deleteData(context,
                            String.valueOf(jid), Site.TYPE, queryType);
                    Log.i(Utilities.TAG, Site.ACTION_SITE + "delete result : "
                            + resp_delete);
                    break;
                default:

                    String source = ((GetSiteRequest) object.getReqDataObject()).getSource();
                    Log.i(Utilities.TAG, "getting xml for " + Site.ACTION_SITE);
                    long cid = Long.parseLong(((GetSiteRequest) object.getReqDataObject()).getCid());
                    response = DBEngine.getSite(objForCallbk, context, time, cid, source);
                    if (response != null)
                        return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
            }
        } else if (action.equals(Site.ACTION_GET_ALL_SITE)) { //YD getallsite
            Response response = null;
            String source = ((CommonSevenReq) object.getReqDataObject()).getSource();
            Log.i(Utilities.TAG, "getting xml for " + Site.ACTION_GET_ALL_SITE);

            response = DBEngine.getSite(objForCallbk, context, time, 0, source);
            if (response != null)
                return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            else
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
        } else if (action.equals(Site.ACTION_SAVE_SITE)) {
            Response response = null;
            String xml = DBEngine.saveSitefromUI(context, (SaveSiteRequest) object.getReqDataObject());
            if (xml != null)
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            else
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
        } else if (action.equals(Api.API_ACTION_SEND_SMS)) {
            try {
                if (!number.equals("undefined") || !number.equals("")) {
                    String template = PreferenceHandler.getTemplate(context);
                    template = template.replace("MINUTES", message);
                    Utilities.sendSMS(context, template, number);
                }
                Log.i(Utilities.TAG, "Send sms :" + message + " and number"
                        + number);
            } catch (Exception e) {
                Log.i(Utilities.TAG, "Error in send sms :");
                e.printStackTrace();
            }
        } else if (action.equals(Api.API_ACTION_HANDLE_EXTRA_PUBNUB)) {
            try {
                Response response = null;
                Long unUsedOrderID = jid;
                if (BaseTabActivity.currentApplicationState == BaseTabActivity.RESTARTED) {
                    DataObject.ordersXmlDataStore = DBEngine.getOrdersfromOfflineInMap(context); // YD check if it is not causing any issue in front end
                }

                HashMap<Long, Object> xml = null;
                if (queryType == DBHandler.QUERY_FOR_ORIG) {
                    xml = DBEngine.getOrders(context, "" + unUsedOrderID, queryType);
                    if (xml != null && xml.size() > 0) {
                        String resp_delete = DBEngine.deleteData(context,
                                String.valueOf(unUsedOrderID), Order.TYPE,
                                DBHandler.QUERY_FOR_ORIG);
                        Log.i(Utilities.TAG, Order.ACTION_ORDER
                                + "delete result : " + resp_delete);
                        if (resp_delete != null && resp_delete != "") {
                            Log.i(Utilities.TAG, Order.ACTION_ORDER
                                    + "resp_delete != null && resp_delete != '' ");

                            deleteMediaForOrder(context, jid);//YD deleting media for the order

                            //YD below code is to delete the order history file only if there is no other order for same customer (cid)
                            boolean delOrderHistory = true;
                            HashMap<Long, Object> obj = DBEngine.getOrdersfromOfflineInMap(context);
                            if (obj != null) {//YD check if some other order have same cid if yes then don't delete the customer order history file
                                Order odrObj = (Order) xml.get(jid);
                                long localCid = odrObj.getCustomerid();

                                Long keys[] = obj.keySet().toArray(new Long[obj.size()]);
                                for (int i = 0; i < obj.size(); i++) {
                                    Order odrObjj = (Order) obj.get(keys[i]);
                                    if (odrObjj.getCustomerid() == localCid) {
                                        delOrderHistory = false;
                                    }
                                }
                                if (delOrderHistory == true) {// YD true to delete the file false if the order for same cid exist for the day
                                    PreferenceHandler.delCustomerTokens(context, "" + localCid);//YD saving to pref
                                    if (PreferenceHandler.getcustomerOdrHisDelnm(context) != null) {
                                        File file = new File(PreferenceHandler.getcustomerOdrHisDelnm(context));
                                        boolean deleted = file.delete();
                                    }
                                }
                            } else {//YD means no other order is there in the list_cal now
                                PreferenceHandler.delCustomerTokens(context, "" + ((Order) xml.get(jid)).getCustomerid());//YD saving to pref
                                if (PreferenceHandler.getcustomerOdrHisDelnm(context) != null) {// this pref is filled in the above line of addcustomer token
                                    File file = new File(PreferenceHandler.getcustomerOdrHisDelnm(context));
                                    boolean deleted = file.delete();
                                }
                            }
                            //YD below code is to delete the order history END

                            int _delete_res = Integer.parseInt(resp_delete);
                            if (_delete_res > 0) {
                                Log.i(Utilities.TAG, Order.ACTION_ORDER
                                        + "_delete_res > 0 :: " + _delete_res);
                                response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                                response.setResString(String.valueOf(jid));
                                if (BaseTabActivity.currentApplicationState == BaseTabActivity.BACKGROUND ||
                                        BaseTabActivity.currentApplicationState == BaseTabActivity.RESTARTED) {
                                    lstOfOdrUpdated.put(jid, 2);
                                    showNotification(context, ((HashMap<Long, Order>) DataObject.ordersXmlDataStore).size() - 1);
                                }
                                return response;
                            }
                        }
                    } else {
                        if (object != null) {
                            object.setReqId(Utilities.DEFAULT_NOCALLBACK_REQUEST_ID);
                        }
                        Log.e(
                                Utilities.TAG,
                                "Not making any action and not making any callback because call is extra pubnub");
                    }
                } else {
                    //YD checking in temp table
                    Log.i(Utilities.TAG,
                            "Checking in temp database and deleting");
                    xml = DBEngine.getOrders(context, "" + unUsedOrderID,
                            queryType);
                    if (xml != null && xml.size() > 0) {//yash
                        String resp_delete = DBEngine.deleteData(context,
                                String.valueOf(unUsedOrderID), Order.TYPE,
                                queryType);
                        Log.i(Utilities.TAG, Order.ACTION_ORDER
                                + "delete result : " + resp_delete);
                        if (resp_delete != null && resp_delete != "") {
                            Log.i(Utilities.TAG, Order.ACTION_ORDER
                                    + "resp_delete != null && resp_delete != '' ");
                            int _delete_res = Integer.parseInt(resp_delete);
                            if (_delete_res > 0) {
                                Log.i(Utilities.TAG, Order.ACTION_ORDER
                                        + "_delete_res > 0 :: " + _delete_res);
                            }
                        }
                    } else {
                        if (object != null) {
                            object.setReqId(Utilities.DEFAULT_NOCALLBACK_REQUEST_ID);
                        }
                        Log.e(Utilities.TAG, "Not making any action and not making any callback because call is extra pubnub and checked in temp tables.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (action.equals(OrderMedia.ACTION_META_UPDATE_TYPE)) {
            Response response;
            String resp_delete = DBEngine.deleteData(context,
                    String.valueOf(jid), OrderMedia.TYPE_DELETE, queryType);
            Log.i(Utilities.TAG, Parts.TYPE_UPDATE
                    + " delete result : " + resp_delete);
            if (resp_delete != null && resp_delete != "") {
                Log.i(Utilities.TAG, Parts.TYPE_UPDATE
                        + "resp_delete != null && resp_delete != '' ");
                int _delete_res = Integer.parseInt(resp_delete);
                if (_delete_res > 0) {
                    Log.i(Utilities.TAG, Parts.TYPE_UPDATE
                            + "_delete_res > 0 :: " + _delete_res);
                    response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    response.setResString(String.valueOf(jid));
                    return response;
                } else {
                    return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                }
            }

        } else if (action.equals(Api.API_ACTION_SAVE_CHECK_IN_OUT)) {
            Log.i(Utilities.TAG, "savetcrd action");
            try {
                String response = DBEngine.sendCheckINOUT(context, object.getReqDataObject());
                if (response != null)
                    return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                else
                    return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (action.equals(OrdersMessage.ACTION_MESSAGE_SAVE)) {
            Response response = null;
            Log.i(Utilities.TAG, "save send message");
            response = DBEngine.insertData(context, object.getReqDataObject(),
                    OrdersMessage.TYPE_SAVE, queryType);
            if (response != null)
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            else
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
        } else if (action.equals(CustomerContact.ACTION_CONTACT_EDIT)) {
            Response response = null;
            Log.i(Utilities.TAG, "save send message");
            response = DBEngine.insertData(context, object.getReqDataObject(),
                    CustomerContact.TYPE_EDIT, queryType);
            if (response != null)
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            else
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
        } else if (action.equals(CustomerContact.ACTION_GET_CONTACT)) {
            Response response = null;
            switch (objectType) {
                case DBHandler.DB_ACTION_INSERT:

                    response = DBEngine.insertData(context, object.getReqDataObject(),
                            CustomerContact.TYPE, queryType);

                    Log.i(Utilities.TAG, CustomerContact.ACTION_GET_CONTACT + "insert");
                    if (response != null) {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    } else {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                    }

                case DBHandler.DB_ACTION_UPDATE:
                    String resp_update = DBEngine.updateData(context,
                            object.getReqDataObject(), CustomerContact.TYPE, queryType);
                    Log.i(Utilities.TAG, CustomerContact.TYPE + "update result : " + resp_update);
                    Log.i(Utilities.TAG, CustomerContact.TYPE + "delete cust contact in update for " + jid);
                    if (resp_update != null)
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));

                case DBHandler.DB_ACTION_DELETE:
                    String resp_delete = DBEngine.deleteData(context, String.valueOf(jid),
                            CustomerContact.TYPE, DBHandler.QUERY_FOR_ORIG);
                    Log.i(Utilities.TAG, CustomerContact.TYPE + "delete result : "
                            + resp_delete);
                    if (resp_delete != null && resp_delete != "") {
                        Log.i(Utilities.TAG, Order.ACTION_ORDER
                                + "resp_delete != null && resp_delete != '' ");
                        int _delete_res = Integer.parseInt(resp_delete);
                        if (_delete_res > 0) {
                            Log.i(Utilities.TAG, Order.ACTION_ORDER
                                    + "_delete_res > 0 :: " + _delete_res);
                            response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                            response.setResString(String.valueOf(jid));
                            return response;
                        } else {
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                        }
                    }
                    break;
                default:
                    Log.i(Utilities.TAG, "getting xml for "
                            + CustomerContact.ACTION_GET_CONTACT);
                    String cid = ((GetContacts) object.getReqDataObject()).getCid();
                    queryType = DBHandler.QUERY_FOR_ORIG;
                    response = DBEngine
                            .getCustContact(context, cid);

                    return response;
            }
        } else if (action.equals(Assets.ACTION_GET_ASSETS)) {// for getting asset and handling pubnub
            Response response = null;
            switch (objectType) {
                case DBHandler.DB_ACTION_INSERT:

                    response = DBEngine.insertData(context, object.getReqDataObject(),
                            Assets.TYPE_PUBNUB, queryType);

                    Log.i(Utilities.TAG, Assets.ACTION_GET_ASSETS + "insert");
                    if (response != null) {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    } else {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                    }

                case DBHandler.DB_ACTION_UPDATE:
                    String resp_update = DBEngine.updateData(context,
                            object.getReqDataObject(), Assets.TYPE_PUBNUB, queryType);
                    Log.i(Utilities.TAG, CustomerContact.TYPE
                            + "update result : " + resp_update);

                    Log.i(Utilities.TAG, CustomerContact.TYPE
                            + "delete cust contact in update for " + jid);

                    if (resp_update != null) {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    } else {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                    }

                case DBHandler.DB_ACTION_DELETE:
                    String resp_delete = DBEngine.deleteData(context, String.valueOf(jid),
                            Assets.TYPE_PUBNUB, DBHandler.QUERY_FOR_ORIG);
                    Log.i(Utilities.TAG, CustomerContact.TYPE + "delete result : "
                            + resp_delete);
                    if (resp_delete != null && resp_delete != "") {
                        Log.i(Utilities.TAG, Order.ACTION_ORDER
                                + "resp_delete != null && resp_delete != '' ");
                        int _delete_res = Integer.parseInt(resp_delete);
                        if (_delete_res > 0) {
                            Log.i(Utilities.TAG, Order.ACTION_ORDER
                                    + "_delete_res > 0 :: " + _delete_res);
                            response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                            response.setResString(String.valueOf(jid));
                            return response;
                        } else {
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                        }
                    }
                    break;
                default:
                    Log.i(Utilities.TAG, "getting xml for "
                            + Assets.ACTION_GET_ASSETS);
                    String cid = ((GetAsset) object.getReqDataObject()).getCid();
                    response = DBEngine
                            .getCustAssets(context, cid);

                    return response;
            }
        } else if (action.equals(AssetsType.ACTION_GET_ASSETS_TYPE)) {
            Response response = null;
            switch (objectType) {
                case DBHandler.DB_ACTION_INSERT:
                    response = DBEngine.insertData(context, object.getReqDataObject(),
                            AssetsType.TYPE_PUBNUB_ASSET_TYPE, queryType);
                    Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                            + " insert result : ");
                    if (response != null) {
                        return response;
                    } else {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));//YD TODO insertion failed error handling
                    }

                case DBHandler.DB_ACTION_UPDATE:
                    String resp_update = DBEngine.updateData(context, object.getReqDataObject(),
                            AssetsType.TYPE_PUBNUB_ASSET_TYPE, queryType);
                    Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                            + " update result : " + resp_update);
                    int _update_res = Integer.parseInt(resp_update);
                    if (_update_res > 0)
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    else
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                case DBHandler.DB_ACTION_DELETE:
                    String resp_delete = DBEngine.deleteData(context,
                            String.valueOf(jid), AssetsType.TYPE_PUBNUB_ASSET_TYPE, queryType);
                    Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                            + " delete result : " + resp_delete);
                    if (resp_delete != null && resp_delete != "") {
                        Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                                + "resp_delete != null && resp_delete != '' ");
                        int _delete_res = Integer.parseInt(resp_delete);
                        if (_delete_res > 0) {
                            Log.i(Utilities.TAG, Parts.ACTION_PART_TYPE
                                    + "_delete_res > 0 :: " + _delete_res);
                            response = Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                            response.setResString(String.valueOf(jid));
                            return response;
                        } else {
                            return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                        }
                    }
                    break;
                default:
                    String source = ((CommonSevenReq) object.getReqDataObject()).getSource();
                    Log.i(Utilities.TAG, "getting xml for "
                            + AssetsType.TYPE_PUBNUB_ASSET_TYPE + "from " + source);
                    response = DBEngine.getAssetsType(objForCallbk, context, time, source);
                    if (response != null) {
                        return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    } else {
                        return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                    }
            }

        } else if (action.equals(Site.ACTION_EDIT_SITE)) {
            Log.i(Utilities.TAG, "Edit contact request");

            String xml = DBEngine.update_Site(context, (EditSiteReq) object.getReqDataObject());

            if (xml != null)
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            else
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
        } else if (action.equals(Site.ACTION_DELETE_SITE)) {
            Response response = null;
            Log.i(Utilities.TAG, "getting xml for "
                    + Site.ACTION_DELETE_SITE);
            String id = "" + ((EditSiteReq) object.getReqDataObject()).getId();

            if (id != null && !id.equals("")) {
                String resp = DBEngine.deleteData(context, id, Site.TYPE,
                        queryType);
                if (resp != null) {
                    response = DBEngine.getOrderTaskforOffline(context, "0");// returns the ordertaskData
                    MapFragment.setMapAllTasksXMLGM((HashMap<Long, OrderTask>) response.getResponseMap()); //By MY
                    MapFragment.setMapAllTasksXML_SK((HashMap<Long, OrderTask>) response.getResponseMap()); //By MY
                    return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));//kept null instead of map because it only returns string while deleting the part
                } else {
                    return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
                }
            }
        } else if (action.equals(Form.ACTION_SAVE_FORM)) {
            Response response = null;
            Log.i(Utilities.TAG, "getting xml for "
                    + Form.ACTION_SAVE_FORM);
            response = DBEngine.insertData(context, object.getReqDataObject(),
                    Form.TYPE, queryType);
            if (response != null) {
                return Utilities.createResponse("success", response.getResponseMap(), ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            } else {
                return Utilities.createResponse("success", null, ServiceError.getEnumValstr(ServiceError.NO_DATA));
            }

        }


        return null;
    }

    private void getCustomerListData(){

        Date date = null;
        String getOdrOfDate = PreferenceHandler.getOdrGetDate(context); //YD TODO write the code to get date from pubnub
        if (getOdrOfDate != null && !getOdrOfDate.equals("")) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                date = simpleDateFormat.parse(getOdrOfDate);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        } else
            date = new Date();
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTime(date);
        SimpleDateFormat smp = new SimpleDateFormat(
                "yyyy-MM-dd");
        String fdate = smp.format(date);

        // getting next week's last day.
        tempCalendar.add(Calendar.DATE,
                Utilities.DEFAULT_OFFLINE_SYNC_BOUNDARY_DAYS);
        Date nextDate = tempCalendar.getTime();

        String todate = smp.format(nextDate);


        try {
            StringBuilder st = new StringBuilder();
            Object[] orderlist = DBEngine.getOrdersForOffline(Utilities.getTimeZone(), Utilities.getCurrentTimeInMillis(), fdate, todate, context, st);
            List<Long> cids = new ArrayList<>();

            for (Object o : orderlist) {
                Order orderobj = (Order) o;
                cids.add(orderobj.getCustomerid());
            }

            if (customerid != 0) {
                DBEngine.getCustList(null, context, cids, DBEngine.DATA_SOURCE_SERVER);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteMediaForOrder(Context context, long jid) {

        HashMap<Long, Object> map = DBEngine.getOrderMediaforOffline(context, "" + jid);
        if (map != null && map.size() > 0) {
            for (Long key : map.keySet()) {
                String metaPath;
                if (((OrderMedia) map.get(key)).getMediatype() == 1) {//YD this if is for pictures thumbs image to delete
                    metaPath = getThumbPath(((OrderMedia) map.get(key)).getMetapath());
                    if (metaPath != null) {
                        deleteMedia(metaPath, jid);
                    }
                }
                //YD below code is to delete the media file one by one
                metaPath = ((OrderMedia) map.get(key)).getMetapath();
                if (metaPath != null) {
                    deleteMedia(metaPath, jid);
                }
            }
        }
    }


    private String getThumbPath(String str) {

        if (str != null) {///storage/emulated/0/AceRoute/temp_1462527798949.jpg
            String array[];
            array = str.split("\\.");

            return array[0] + "_Thumb." + array[1];
        }
        return null;
    }

    private void deleteMedia(String metaPath, long jid) {
        File file = new File(metaPath);
        boolean deleted = file.delete();
        if (deleted) {
            Log.i(Utilities.TAG, "Media deleted successfully for order : " + jid + " media name : " + metaPath);
        } else {
            Log.i(Utilities.TAG, "Media deletion unsuccessful for order : " + jid + " media name : " + metaPath);
        }
    }

    public void ServiceStarterLoc(RespCBandServST activity, Intent intent) {
        objForCallbk = activity;
        if (activity instanceof Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ((Activity) activity).startForegroundService(intent);
            } else {
                ((Activity) activity).startService(intent);
            }
            // vebs fixed this issue on version based
           // ((Activity) activity).startService(intent);
        }
        if (activity instanceof FragmentActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ((FragmentActivity) activity).startForegroundService(intent);
            } else {
                ((FragmentActivity) activity).startService(intent);
            }
            // vebs fixed this issue on version based
           // ((FragmentActivity) activity).startService(intent);
        }
    }

    public void ServiceStarterLoc(RespCBandServST activity, Intent intent, RespCBandServST callbackObj, Long currentMilli) {
        objForCallbk = activity;
        objForCallbkWithId.put(currentMilli, callbackObj);
        if (activity instanceof Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ((Activity) activity).startForegroundService(intent);
            } else {
                ((Activity) activity).startService(intent);
            }
            // vebs fixed this issue on version based
           // ((Activity) activity).startService(intent);
           /* ComponentName componentName =  new ComponentName(((Activity) activity), AceRouteJobService.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                JobInfo info = new JobInfo.Builder(123, componentName).setMinimumLatency(3000).setPersisted(true).build();

                JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
                scheduler.schedule(info);
            }else{
                ((Activity) activity).startService(intent);
            }*/
        } else if (activity instanceof FragmentActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ((FragmentActivity) activity).startForegroundService(intent);
            } else {
                ((FragmentActivity) activity).startService(intent);
            }
            // vebs fixed this issue on version based
            //((FragmentActivity) activity).startService(intent);
           /* ComponentName componentName =  new ComponentName(  ((FragmentActivity) activity), AceRouteJobService.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                JobInfo info = new JobInfo.Builder(123, componentName).setOverrideDeadline(0).setPersisted(true).build();

                JobScheduler scheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
                scheduler.schedule(info);
            }else{
                ((FragmentActivity) activity).startService(intent);
            }*/

        }


    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public void showNotification(Context context, int totalNumOfOrders) {

        noOforderAdded = noOforderDeleted = noOforderUpdated = 0;

        for (Long key : lstOfOdrUpdated.keySet()) {
            if (lstOfOdrUpdated.get(key) == 1)
                noOforderAdded++;
            if (lstOfOdrUpdated.get(key) == 2)
                noOforderDeleted++;
            if (lstOfOdrUpdated.get(key) == 3)
                noOforderUpdated++;
        }

        //YD Making notification to show
        notificationIntent = new Intent(context, SplashII.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        String workerName = "";
        workerName = PreferenceHandler.getWorkerNm(context);
        PendingIntent intent;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
             intent = PendingIntent.getActivity(context, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }else{
             intent = PendingIntent.getActivity(context, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }


        DataObject.ordersXmlDataStore = DBEngine.getOrdersfromOfflineInMap(context);

        totalNumOfOrders = DataObject.ordersXmlDataStore == null ? 1 : ((HashMap<Long, Order>) DataObject.ordersXmlDataStore).size();

        String content = "";
        if (totalNumOfOrders > 1)
            content = totalNumOfOrders + " orders assigned to " + workerName;
        else
            content = totalNumOfOrders + " order assigned to " + workerName;

        String message = getMsgNotification();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.iconbigb))
                .setContentTitle("AceRoute")
                .setNotificationSilent()
                .setSound(null)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content + "\n" + message))
                .setContentInfo("")
                .setColor(Color.rgb(201, 247, 215))
                .setContentIntent(intent);
        Notification notification = mBuilder.build();
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

    private String getMsgNotification() {
        String orderTextAdd = getOrderText(noOforderAdded); //YD checking orders or order
        String orderTextUpd = getOrderText(noOforderUpdated);//YD checking orders or order
        String orderTextDel = getOrderText(noOforderDeleted);//YD checking orders or order
        String message = "";
        if (noOforderAdded > 0 && noOforderUpdated > 0 && noOforderDeleted > 0) {
            message = noOforderAdded + " " + orderTextAdd + " added, " + noOforderUpdated + " " + orderTextUpd + " updated, " + noOforderDeleted + " " + orderTextDel + " deleted";
        } else if (noOforderAdded > 0 && noOforderUpdated > 0) {
            message = noOforderAdded + " " + orderTextAdd + " added, " + noOforderUpdated + " " + orderTextUpd + " updated";
        } else if (noOforderUpdated > 0 && noOforderDeleted > 0) {
            message = noOforderUpdated + " " + orderTextUpd + " updated, " + noOforderDeleted + " " + orderTextDel + " deleted";
        } else if (noOforderAdded > 0 && noOforderDeleted > 0) {
            message = noOforderAdded + " " + orderTextAdd + " added, " + noOforderDeleted + " " + orderTextDel + " deleted";
        } else if (noOforderAdded > 0) {
            message = noOforderAdded + "  " + orderTextAdd + " added";
        } else if (noOforderUpdated > 0) {
            message = noOforderUpdated + " " + orderTextUpd + " updated";
        } else if (noOforderDeleted > 0) {
            message = noOforderDeleted + " " + orderTextDel + " deleted";
        }
        return message;
    }

    private String getOrderText(int noOfOrders) {
        if (noOfOrders > 1)
            return "orders";
        else
            return "order";
    }
}