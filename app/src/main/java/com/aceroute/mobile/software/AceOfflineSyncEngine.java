package com.aceroute.mobile.software;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderMedia;
import com.aceroute.mobile.software.component.OrderNotes;
import com.aceroute.mobile.software.component.OrderPart;
import com.aceroute.mobile.software.component.OrderTask;
import com.aceroute.mobile.software.component.OrdersMessage;
import com.aceroute.mobile.software.component.reference.Assets;
import com.aceroute.mobile.software.component.reference.CheckinOut;
import com.aceroute.mobile.software.component.reference.CustomerContact;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.Form;
import com.aceroute.mobile.software.component.reference.MessagePanic;
import com.aceroute.mobile.software.component.reference.ResponseResult;
import com.aceroute.mobile.software.component.reference.Shifts;
import com.aceroute.mobile.software.component.reference.Site;
import com.aceroute.mobile.software.database.DBEngine;
import com.aceroute.mobile.software.database.DBHandler;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.fragment.OrderListMainFragment;
import com.aceroute.mobile.software.fragment.SwapingFragment;
import com.aceroute.mobile.software.http.Api;
import com.aceroute.mobile.software.http.HttpConnection;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.network.AceRequestHandler;
import com.aceroute.mobile.software.requests.PubnubRequest;
import com.aceroute.mobile.software.requests.updateOrderRequest;
import com.aceroute.mobile.software.utilities.JSONHandler;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServerRequestHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.aceroute.mobile.software.utilities.XMLHandler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AceOfflineSyncEngine {

    public static long orderIdToCheckSyncCount = 0;
    private static AceOfflineSyncEngine instance = null;
    boolean errorCheck;
    MyDialog dialog;
    private Context context;
    private String syncType = "full";
    private boolean pause;

    public AceOfflineSyncEngine(Context ctxt) {
        context = ctxt;
    }

    public static AceOfflineSyncEngine getAceOfflineSyncEngineInstance(Context ctxt) {
        if (AceOfflineSyncEngine.instance == null)
            AceOfflineSyncEngine.instance = new AceOfflineSyncEngine(ctxt);

        return AceOfflineSyncEngine.instance;
    }

    public void setSynctoPause(RespCBandServST actFragContxt, boolean p)// throws IOException  // YD using when pausing request from the sync dialog
    {
        pause = p;
        if (p == false) {
			/*try {
				syncAllDataForOffline(actFragContxt ,"full");
			} catch (IOException e) {
				e.printStackTrace();
			}*///YD TODO later
        } else
            Utilities.sendmessageUpdatetoserver(actFragContxt, "Pausing the Process", SplashII.MESSAGE_SENDUPDATE_TO_UI);
    }

    public Response startBgSync(RespCBandServST actFragContxt)// throws IOException
    {
        Response response = new Response();
        if (!Utilities.checkInternetConnection(context, false)) {
            response.setStatus("success");
            response.setErrorcode(String.valueOf(JSONHandler.ERROR_CODE_RESPONSE_SYNC_PAUSED));
            return response;
            //return XMLHandler.getXMLForErrorCode(context, JSONHandler.ERROR_CODE_INTERNET_CONNECTION);
        }
        response = syncresgeo(actFragContxt, true);
        //if(response !=null && Utilities.IsResponseError(response))

        if (response != null && response.getStatus().equals("failure"))
            return response;
        response = syncstatus();

        if (response != null && response.getStatus().equals("failure"))
            return response;

        response.setStatus("success");
        return response;
        //return XMLHandler.XML_DATA_SUCCESS_MESSAGE;// vicky need to handle data response well;

    }

    public Response syncAllDataForOffline(RespCBandServST actFragContxt, String type) throws IOException {

        Response response = null;
        try {

            errorCheck = false;
            pause = false;
            syncType = type;
            if (!Utilities.checkInternetConnection(context, false)) {
                Utilities.sendmessageUpdatetoserver(actFragContxt, "No Internet Connection", SplashII.MESSAGE_SENDUPDATE_TO_UI);
                response = new Response();
                response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_INTERNET_CONNECTION));//also send success failure
                response.setStatus("failure");
                return response;
                //return XMLHandler.getXMLForErrorCode(context, JSONHandler.ERROR_CODE_INTERNET_CONNECTION);
            }

            //CustomStatusDialog.setHeightListView();//yd commenting because giving illegalargument exception in fabric
            Utilities.sendmessageUpdatetoserver(actFragContxt, "Process Start", SplashII.MESSAGE_SENDUPDATE_TO_UI);
            //for (int i =0 ;i <20; i++)
            Utilities.sendmessageUpdatetoserver(actFragContxt, "Upload Start", SplashII.MESSAGE_SENDUPDATE_TO_UI);
            //Downloading App String
            //Utilities.downloadFileToSDCard(context, "http://webexperts.info/yash/Strings.js", Api.APP_BASE_PATH, "", "String.js",0);
            //Utilities.downloadFileToSDCard(context, "http://webexperts.info/yash/dialogstring", Api.APP_BASE_PATH, "", "dialogstring",1);
             response = syncOrderMessages(actFragContxt);
            if (response != null && response.getStatus().equals("failure"))
                return response;

            response = UploadSiteToServer(actFragContxt);
            if (response != null && response.getStatus().equals("failure"))
                return response;

            response = UploadOrdersToServer(actFragContxt);//YD
            if (response != null && response.getStatus().equals("failure"))
                return response;

            if (PreferenceHandler.getTasksConfig(context).equals("1")) {
                response = UploadTaskToServer(actFragContxt);//YD
                if (response != null && response.getStatus().equals("failure"))
                    return response;
            }

            response = UploadPartToServer(actFragContxt);//YD
            if (response != null && response.getStatus().equals("failure"))
                return response;

            if (PreferenceHandler.getTasksConfig(context).equals("1")) {
                response = UploadAssetToServer(actFragContxt);//YD
                if (response != null && response.getStatus().equals("failure"))
                    return response;
            }

            response = UploadNoteToServer(actFragContxt);
            if (response != null && response.getStatus().equals("failure"))
                return response;

            response = UploadFormToServer(actFragContxt);
            if (response != null && response.getStatus().equals("failure"))
                return response;

            response = UploadMediaToServer(actFragContxt);
            if (response != null && response.getStatus().equals("failure"))
                return response;

            response = UploadShiftToServer(actFragContxt);
            if (response != null && response.getStatus().equals("failure"))
                return response;

            response = syncresgeo(actFragContxt, false);
            if (response != null && response.getStatus().equals("failure"))
                return response;
            if (pause) {
                response = new Response();
                response.setStatus("failure");
                response.setErrorcode(String.valueOf(JSONHandler.ERROR_CODE_RESPONSE_SYNC_PAUSED));
                return response;
            }
            response = syncCheckinout(actFragContxt, context);
            if (response != null && response.getStatus().equals("failure"))
                return response;
            if (pause) {
                response = new Response();
                response.setStatus("failure");
                response.setErrorcode(String.valueOf(JSONHandler.ERROR_CODE_RESPONSE_SYNC_PAUSED));
                return response;
            }

            Utilities.sendmessageUpdatetoserver(actFragContxt, "Upload Complete", SplashII.MESSAGE_SENDUPDATE_TO_UI);

            //YD Deleting media folder files
            String compfilename = null;
            File file;
            if (!type.equals("semipartial")) {
//                if (Environment.getExternalStorageState().equals(
//                        Environment.MEDIA_MOUNTED)) {
//                    compfilename = Environment
//                            .getExternalStorageDirectory()
//                            .getAbsolutePath()
//                            + "/AceRoute";
//
//                } else {
//                    compfilename = Environment.getDataDirectory()
//                            .getAbsolutePath() + "/AceRoute/";
//                }
                //changes by Nitika
                compfilename = context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        .getAbsolutePath()
                        + "/AceRoute";
                file = new File(compfilename);
                if (file.isDirectory() && file.exists()) {
                    String[] children = file.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(file, children[i]).delete();
                    }
                }
            }
            if (!type.equals("semipartial_logout")) {
                //DBEngine.getHeaderStrFrmServ(context);//YD getting headers for application [getlabels request]
                DBEngine.getHeaderStrFrmServForPg(context);//YD getting headers for application [getterm request]
                DBEngine.getHeaderStrFrmServForAssets(context);//YD getting headers for application [getastterm request]
            } else {//YD important code to delete logs file
//                if (Environment.getExternalStorageState().equals(
//                        Environment.MEDIA_MOUNTED)) {
//                    compfilename = Environment
//                            .getExternalStorageDirectory()
//                            .getAbsolutePath()
//                            + "/XLogss";
//
//                } else {
//                    compfilename = Environment.getDataDirectory()
//                            .getAbsolutePath() + "/XLogss/";
//                }
                //changes by Nitika
                compfilename = context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        .getAbsolutePath()
                        + "/XLogss";
                file = new File(compfilename);
                if (file.isDirectory() && file.exists()) {
                    String[] children = file.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(file, children[i]).delete();
                    }
                }
            }
            if (type.equals("partial") || type.equals("full")) {
                if (type.equals("full")) {
                    PreferenceHandler.setCustListDownloadComplete(context, false); // YD setting this because need to download custlist again
                    PreferenceHandler.setIsCustListDownloading(context, true);
                }
                response = syncdownloadOfflineData(actFragContxt, context);
                if (response != null && response.getStatus().equals("failure"))
                    return response;
            }
            //Downloading Map for california is now in splashII activity
		/*if (PreferenceHandler.getMapCountry(context)!=null && PreferenceHandler.getMapState(context)!=null)
		{
			downloadMap();
		}*/
        } catch (Exception e) {
            e.printStackTrace();
            //return XMLHandler.XML_NO_DATA;
        }
        if (response == null)
            response = new Response();
        response.setStatus("success");
        response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
        return response;
        //return XMLHandler.XML_DATA_SUCCESS_MESSAGE;// vicky need to handle data response well;
    }

    public Response UploadMediaToServer(RespCBandServST actFragContxt) {

        Response response = null;
        try {
            response = syncofflineOrderSupportItems(actFragContxt, DBHandler.orderFileMeta, OrderMedia.ACTION_MEDIA_SAVE, JSONHandler.KEY_REQUEST_TYPE_POST_MULTIPART,
                    OrderMedia.ID, OrderMedia.ORDER_ID, OrderMedia.TYPE_UPDATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null && response.getStatus().equals("failure"))
            return response;
        if (pause) {
          //  response.setStatus("failure");
          //  response.setErrorcode(String.valueOf(JSONHandler.ERROR_CODE_RESPONSE_SYNC_PAUSED));
            response.setStatus("failure");
            response.setErrorcode(String.valueOf(JSONHandler.ERROR_CODE_RESPONSE_SYNC_PAUSED));
            return response;
        }
        return response;
    }

    /*public Response UploadFormToServer(RespCBandServST actFragContxt) {

        Response response = null;
        try {
            response = syncofflineShift(actFragContxt, DBHandler.formTable, Form.ACTION_SAVE_FORM, JSONHandler.KEY_REQUEST_TYPE_POST,
                    Form.FORM_ID, Form.TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null && response.getStatus().equals("failure"))
            return response;
        if (pause) {
            response.setStatus("failure");
            response.setErrorcode(String.valueOf(JSONHandler.ERROR_CODE_RESPONSE_SYNC_PAUSED));
            return response;
        }
        return response;

    }
*/
    public Response UploadNoteToServer(RespCBandServST actFragContxt) {

        Response response = null;
        try {
            response = syncofflineOrderSupportItems(actFragContxt, DBHandler.orderFileNotes, OrderNotes.ACTION_NOTES_SAVE, JSONHandler.KEY_REQUEST_TYPE_POST,
                    OrderNotes.ORDER_ID, "", OrderNotes.TYPE_SAVE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null && response.getStatus().equals("failure"))
            return response;
        if (pause) {
            response.setStatus("failure");
            response.setErrorcode(String.valueOf(JSONHandler.ERROR_CODE_RESPONSE_SYNC_PAUSED));
            return response;
        }
        return response;
    }

    public Response UploadSiteToServer(RespCBandServST actFragContxt) {

        Response response = null;
        try {
            response = syncofflineOrderSupportItems(actFragContxt, DBHandler.siteTable, Site.ACTION_SAVE_SITE, JSONHandler.KEY_REQUEST_TYPE_POST,
                    Site.SITE_ID, Site.SITE_CID, Site.TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null && response.getStatus().equals("failure"))
            return response;
        if (pause) {
            response.setStatus("failure");
            response.setErrorcode(String.valueOf(JSONHandler.ERROR_CODE_RESPONSE_SYNC_PAUSED));
            return response;
        }
        return response;
    }

    public Response UploadOrdersToServer(RespCBandServST actFragContxt) {

        Response response = null;
        try {
            response = syncofflineOrders(actFragContxt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null && response.getStatus().equals("failure"))
            return response;
        if (pause) {
            response.setStatus("failure");
            response.setErrorcode(String.valueOf(JSONHandler.ERROR_CODE_RESPONSE_SYNC_PAUSED));
            return response;
        }
        return response;
    }

    public Response UploadPartToServer(RespCBandServST actFragContxt) {

        Response response = null;
        try {
            response = syncofflineOrderSupportItems(actFragContxt, DBHandler.orderPartTable, OrderPart.ACTION_SAVE_ORDER_PART, JSONHandler.KEY_REQUEST_TYPE_POST,
                    OrderPart.ORDER_PART_ID, OrderPart.ORDER_ID, OrderPart.TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null && response.getStatus().equals("failure"))
            return response;
        if (pause) {
            response.setStatus("failure");
            response.setErrorcode(String.valueOf(JSONHandler.ERROR_CODE_RESPONSE_SYNC_PAUSED));
            return response;
        }
        return response;

    }

    public Response UploadFormToServer(RespCBandServST actFragContxt) {

        Response response = null;
        try {
            response = syncofflineOrderSupportItems(actFragContxt, DBHandler.formTable, Form.ACTION_SAVE_FORM, JSONHandler.KEY_REQUEST_TYPE_POST,
                    Form.FORM_ID, Form.FORM_OID, Form.TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null && response.getStatus().equals("failure"))
            return response;
        if (pause) {
            response.setStatus("failure");
            response.setErrorcode(String.valueOf(JSONHandler.ERROR_CODE_RESPONSE_SYNC_PAUSED));
            return response;
        }
        return response;

    }

    public Response UploadShiftToServer(RespCBandServST actFragContxt) {

        Response response = null;
        try {
            response = syncofflineShift(actFragContxt, DBHandler.shiftTable, Shifts.ACTION_SYNC_SHIFTS, JSONHandler.KEY_REQUEST_TYPE_POST,
                    Shifts.SHIFT_ID, Shifts.TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null && response.getStatus().equals("failure"))
            return response;
        if (pause) {
            response.setStatus("failure");
            response.setErrorcode(String.valueOf(JSONHandler.ERROR_CODE_RESPONSE_SYNC_PAUSED));
            return response;
        }
        return response;

    }

    public Response UploadMessageToServer(RespCBandServST actFragContxt) {

        Response response = null;
        try {
            response = syncMessages(actFragContxt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null && response.getStatus().equals("failure"))
            return response;
        if (pause) {
            response.setStatus("failure");
            response.setErrorcode(String.valueOf(JSONHandler.ERROR_CODE_RESPONSE_SYNC_PAUSED));
            return response;
        }
        return response;

    }

    public Response UploadAssetToServer(RespCBandServST actFragContxt) {

        Response response = null;
        try {
            response = syncofflineOrderSupportItems(actFragContxt, DBHandler.assestTable, Assets.ACTION_SAVE_ORDER_ASSETS, JSONHandler.KEY_REQUEST_TYPE_POST,
                    Assets.ASSET_ID, Assets.ASSET_OID, Assets.TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null && response.getStatus().equals("failure"))
            return response;
        if (pause) {
            response.setStatus("failure");
            response.setErrorcode(String.valueOf(JSONHandler.ERROR_CODE_RESPONSE_SYNC_PAUSED));
            return response;
        }
        return response;

    }

    public Response UploadTaskToServer(RespCBandServST actFragContxt) {

        Response response = null;
        try {
            response = syncofflineOrderSupportItems(actFragContxt, DBHandler.orderTaskTable, OrderTask.ACTION_SAVE_ORDER_TASK, JSONHandler.KEY_REQUEST_TYPE_POST,
                    OrderTask.ORDER_TASK_ID, OrderTask.ORDER_ID, OrderTask.TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null && response.getStatus().equals("failure"))
            return response;
        if (pause) {
            response.setStatus("failure");
            response.setErrorcode(String.valueOf(JSONHandler.ERROR_CODE_RESPONSE_SYNC_PAUSED));
            return response;
        }
        return response;

    }

    public Response syncUpdatedCustomerContact(RespCBandServST actFragContxt) {

        Response response = null;
        try {
            response = syncofflineOrderSupportItems(actFragContxt, DBHandler.customercontactTable, CustomerContact.ACTION_CONTACT_EDIT, JSONHandler.KEY_REQUEST_TYPE_POST,
                    CustomerContact.CONTACT_ID, "", CustomerContact.TYPE_EDIT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null && response.getStatus().equals("failure"))
            return response;
        if (pause) {
            response.setStatus("failure");
            response.setErrorcode(String.valueOf(JSONHandler.ERROR_CODE_RESPONSE_SYNC_PAUSED));
            return response;
        }
        return response;

    }

    public Response syncOrderMessages(RespCBandServST actFragContxt) throws IOException {

        Map<String, String> params = null;
        String response = null;
        Response responseObj = null;
        boolean firsttime = true;
        int count = 0;
        int total_records = 0;
        int delcount = 0;
        do {// trying this because, may be the cursor gets corrupted if some record is deleted.
            Cursor cursor = DBHandler.getOfflineModifiedRecords(context, DBHandler.orderMessageTable);
            if (cursor != null && cursor.getCount() > 0) {

                cursor.moveToFirst();
                int records = cursor.getCount();

                if (firsttime) {
                    firsttime = false;
                    total_records = records;

                }


                if (records > 0) {
                    count++;
                    Utilities.sendmessageUpdatetoserver(actFragContxt, getMessageTobeDisplayed(OrdersMessage.ACTION_MESSAGE_SAVE) + " " + count + " of " + total_records, SplashII.MESSAGE_SENDUPDATE_TO_UI);
                    params = new HashMap<String, String>();

                    params = ServerRequestHandler.createparamsForRequest(cursor, OrdersMessage.ACTION_MESSAGE_SAVE, context, params);
                    response = HttpConnection
                            .post(context, "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", params);

                    String whereClause = OrdersMessage.MESSAGE_TIMESTAMP + "=" + params.get(OrdersMessage.MESSAGE_TIMESTAMP);
                    ;
                    if (response != null) {
                        Utilities.log(context, "server response : " + response);
                        XMLHandler xmlhandler = new XMLHandler(context);
                        Document doc = xmlhandler.getDomElement(response);
                        NodeList nl = doc
                                .getElementsByTagName(XMLHandler.KEY_DATA);
                        for (int i = 0; i < nl.getLength(); i++) {// loop should not be requiredhere
                            Element e = (Element) nl.item(i);
                            String success = xmlhandler.getValue(e,
                                    XMLHandler.KEY_DATA_SUCCESS);

                            if (!success.equals(XMLHandler.KEY_DATA_RESP_FAIL)) {
                                // delete old record

                                delcount = DBHandler.delete(context,
                                        DBHandler.orderMessageTable, whereClause);
                                if (delcount > 0) {

                                } else {
                                    delcount = DBHandler.delete(context,
                                            DBHandler.checkincheckoutTable, whereClause);// still deleting it. Will save as xml later.
                                    // delete response is invalid
                                }


                            } else {
                                if (Utilities.getResponseErrorCode(response) == null) {
                                    syncbackupData(actFragContxt, CheckinOut.CC_ACTION, cursor, "CheckinInfo", "");
                                    delcount = DBHandler.delete(context,
                                            DBHandler.checkincheckoutTable, whereClause);
                                    response = XMLHandler.XML_DATA_SUCCESS_MESSAGE;// make response as successful as error handled now
                                } else {
                                    freeCursor(cursor);
                                    return responseObj;
                                    //return response;
                                }
                            }
                        }// for loop end

                    } else {// YD null response received
                        syncbackupData(actFragContxt, CheckinOut.CC_ACTION, cursor, "CheckinInfo", "");
                        delcount = DBHandler.delete(context,
                                DBHandler.checkincheckoutTable, whereClause);
                        freeCursor(cursor);
                        responseObj = new Response();
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_DATA));
                        responseObj.setStatus("failure");
                        return responseObj;
                        //return XMLHandler.XML_NO_DATA;// make response as successful as error handled now
                    }

                }
                freeCursor(cursor);
            } else
                break;
        } while (true);
        return responseObj;
    }

    private Response syncresgeo(RespCBandServST actFragContxt, boolean bg) {// YD 2020 being called from AceRequestHandler for sync background geo
        Response responseObj = new Response();
        try {
            String geolist = DBHandler.getValueFromTable(context, DBHandler.resgeoTable, "geo", null);

            if (geolist != null && !geolist.isEmpty()) {
                //if(!bg)
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Geo", SplashII.MESSAGE_SENDUPDATE_TO_UI);
                String tstmplist = DBHandler.getValueFromTable(context, DBHandler.resgeoTable, "timestamp", null);
                Map<String, String> getOrderParams = new HashMap<String, String>();

                getOrderParams.put("stmp", tstmplist);
                getOrderParams.put("geo", geolist);
                getOrderParams.put("lstoid","76588");
                getOrderParams.put("nxtoid","45678");

                getOrderParams.put("action", Api.API_ACTION_SAVE_RES_GEO);
                // changed get to post 12-04-14

                String response = HttpConnection.get(context, "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi",
                        getOrderParams);
                //Logger.i("AceRoute Geo" , "saved current Geo response : "+ response);
                if (Utilities.IsResponseError(response)) {
                    // YD error response
                    responseObj.setStatus("failure");
                    responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.HTTP_REQUEST_FAILED));
                    return responseObj;
//	return response;
                } else
                    DBHandler.deleteTable(DBHandler.resgeoTable);

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // sync the data to storage.

        }
        responseObj.setStatus("success");
        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
        return responseObj;
//		return XMLHandler.XML_DATA_SUCCESS_MESSAGE;

    }

    private Response syncstatus() {
        Response responseObj = new Response();
        try {
            String statuslist = DBHandler.getValueFromTable(context, DBHandler.statusupdatetable, "wkf", null);
            if (statuslist != null && !statuslist.isEmpty()) {

                String orderlist = DBHandler.getValueFromTable(context, DBHandler.statusupdatetable, "id", null);
                Map<String, String> getOrderParams = new HashMap<String, String>();

                getOrderParams.put("id", orderlist);
                getOrderParams.put("wkf", statuslist);
                getOrderParams.put("action", Api.API_ACTION_SAVE_ORDER_STATUS);
                // changed get to post 12-04-14

                String response = HttpConnection.post(context, "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi",
                        getOrderParams);
                if (Utilities.IsResponseError(response)) {
                    responseObj.setResString(response);
                    return responseObj;
                } else
                    DBHandler.deleteTable(DBHandler.statusupdatetable);

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // sync the data to storage.

        }

        responseObj.setStatus("success");
        return responseObj;
        //return XMLHandler.XML_DATA_SUCCESS_MESSAGE;

    }

    public Response syncCheckinout(RespCBandServST actFragContxt, Context context) throws IOException {

        Map<String, String> params = null;
        String response = null;
        Response responseObj = null;
        boolean firsttime = true;
        int count = 0;
        int total_records = 0;
        int delcount = 0;
        do {// trying this because, may be the cursor gets corrupted if some record is deleted.
            Cursor cursor = DBHandler.getOfflineModifiedRecords(context, DBHandler.checkincheckoutTable);
            if (cursor != null && cursor.getCount() > 0) {

                cursor.moveToFirst();
                int records = cursor.getCount();

                if (firsttime) {
                    firsttime = false;
                    total_records = records;

                }


                if (records > 0) {
                    count++;
                    Utilities.sendmessageUpdatetoserver(actFragContxt, getMessageTobeDisplayed(CheckinOut.CC_ACTION) + " " + count + " of " + total_records, SplashII.MESSAGE_SENDUPDATE_TO_UI);
                    params = new HashMap<String, String>();

                    params = ServerRequestHandler.createparamsForRequest(cursor, CheckinOut.CC_ACTION, context, params);
                    //	String url = HttpConnection.getRequestURL(context);

                    Log.d("paramscheckinout",""+params);
                    response = HttpConnection
                            .get(context, "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", params);
                    Log.d("paramsresponse",""+params);

                    String whereClause = CheckinOut.CC_TSTAMP + "=" + params.get(CheckinOut.CC_TSTAMP);
                    ;
                    if (response != null) {
                        Utilities.log(context, "server response : " + response);
                        XMLHandler xmlhandler = new XMLHandler(context);
                        Document doc = xmlhandler.getDomElement(response);
                        NodeList nl = doc
                                .getElementsByTagName(XMLHandler.KEY_DATA);
                        for (int i = 0; i < nl.getLength(); i++) {// loop should not be requiredhere
                            Element e = (Element) nl.item(i);
                            String success = xmlhandler.getValue(e,
                                    XMLHandler.KEY_DATA_SUCCESS);

                            if (!success.equals(XMLHandler.KEY_DATA_RESP_FAIL)) {
                                // delete old record

                                delcount = DBHandler.delete(context,
                                        DBHandler.checkincheckoutTable, whereClause);
                                if (delcount > 0) {

                                } else {
                                    delcount = DBHandler.delete(context,
                                            DBHandler.checkincheckoutTable, whereClause);// still deleting it. Will save as xml later.
                                    // delete response is invalid
                                }


                            } else {
                                if (Utilities.getResponseErrorCode(response) == null) {
                                    syncbackupData(actFragContxt, CheckinOut.CC_ACTION, cursor, "CheckinInfo", "");
                                    delcount = DBHandler.delete(context,
                                            DBHandler.checkincheckoutTable, whereClause);
                                    response = XMLHandler.XML_DATA_SUCCESS_MESSAGE;// make response as successful as error handled now
                                } else {
                                    freeCursor(cursor);
                                    return responseObj;
//return response;
                                }
                            }
                        }// for loop end

                    } else {// YD null response received
                        syncbackupData(actFragContxt, CheckinOut.CC_ACTION, cursor, "CheckinInfo", "");
                        delcount = DBHandler.delete(context,
                                DBHandler.checkincheckoutTable, whereClause);
                        freeCursor(cursor);
                        responseObj = new Response();
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_DATA));
                        responseObj.setStatus("failure");
                        return responseObj;
                        //return XMLHandler.XML_NO_DATA;// make response as successful as error handled now
                    }

                }
                freeCursor(cursor);
            } else
                break;
        } while (true);
        return responseObj;
    }

    private Response syncdownloadOfflineData(RespCBandServST actFragContxt, Context context) {
        Object[] orderlist = null;
        StringBuilder st = new StringBuilder();
        Response responseObj = new Response();
        orderlist = syncdownloadOfflineOrders(actFragContxt, st);
        String result = st.toString();
	/*if(Utilities.IsResponseError(result))
		return result;*///later
        if (pause) {
            responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.RESPONSE_SYNC_PAUSED));
            responseObj.setStatus("failure");
            return responseObj;
        }
        List<Long> cids = new ArrayList<>();
        String response;
        if (orderlist != null) {
            int counter = 0;
            int tempct = 0;

            DBHandler.deleteTable(DBHandler.orderPartTable);
            while (counter < orderlist.length) {
                Order orderobj = (Order) orderlist[counter];
                if (!cids.contains(orderobj.getCustomerid())) {
                    cids.add(orderobj.getCustomerid());
                }
                tempct = counter + 1;
                if (orderobj.getCustPartCount() > 0) {
                    Utilities.sendmessageUpdatetoserver(actFragContxt, "Order Parts for Order " + tempct, SplashII.MESSAGE_SENDUPDATE_TO_UI);
                    response = DBEngine.getOrderPartfromServerandSave(context, Long.toString(orderobj.getId()), DBHandler.QUERY_FOR_ORIG);
                    if (Utilities.IsResponseError(response)) {
                        errorCheck = true;
                        Utilities.sendmessageUpdatetoserver(actFragContxt, "Error in Downloading Parts for order " + tempct, SplashII.MESSAGE_SENDERRORUPDATE_TO_UI);
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.HTTP_REQUEST_FAILED));
                        responseObj.setStatus("failure");
                        return responseObj;
                    }
                }
                counter++;
            }
            if (pause) {
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.RESPONSE_SYNC_PAUSED));
                responseObj.setStatus("failure");
                return responseObj;
            }
            counter = 0;
            tempct = 0;
            DBHandler.deleteTable(DBHandler.formTable);
            while (counter < orderlist.length) {
                Order orderobj = (Order) orderlist[counter];
                tempct = counter + 1;
                if (orderobj.getCustFormCount() > 0) {
                    Utilities.sendmessageUpdatetoserver(actFragContxt, "Order Form for Order " + tempct, SplashII.MESSAGE_SENDUPDATE_TO_UI);
                    response = DBEngine.getOrderFormfromServerandSave(context, Long.toString(orderobj.getId()), DBHandler.QUERY_FOR_ORIG);
                    if (Utilities.IsResponseError(response)) {
                        errorCheck = true;
                        Utilities.sendmessageUpdatetoserver(actFragContxt, "Error in Downloading Form for order " + tempct, SplashII.MESSAGE_SENDERRORUPDATE_TO_UI);
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.HTTP_REQUEST_FAILED));
                        responseObj.setStatus("failure");
                        return responseObj;
                    }
                }
                counter++;
            }
            if (pause) {
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.RESPONSE_SYNC_PAUSED));
                responseObj.setStatus("failure");
                return responseObj;
            }

            DBHandler.deleteTable(DBHandler.orderFileMeta);

            getAllOrderMedia(actFragContxt, context, orderlist);

            counter = 0;
            tempct = 0;
            DBHandler.deleteTable(DBHandler.orderFileNotes);
            while (counter < orderlist.length) {
                Order orderobj = (Order) orderlist[counter];
                tempct = counter + 1;
                DBEngine.getOrderNotefromServerandSave(context, Long.toString(orderobj.getId()), DBHandler.QUERY_FOR_ORIG);
                counter++;
            }
            if (pause) {
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.RESPONSE_SYNC_PAUSED));
                responseObj.setStatus("failure");
                return responseObj;
            }

            counter = 0;
            tempct = 0;
            PreferenceHandler.clearPref(context, PreferenceHandler.PREF_CUST_TOKEN);// YD clearing the data for customer id and its token
            while (counter < orderlist.length) {
                Order orderobj = (Order) orderlist[counter];
                tempct = counter + 1;
                DBEngine.getCustTokenfromServerandSave(context, Long.toString(orderobj.getCustomerid()), DBHandler.QUERY_FOR_ORIG);
                counter++;
            }
            if (pause) {
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.RESPONSE_SYNC_PAUSED));
                responseObj.setStatus("failure");
                return responseObj;

            }

            fillAssetCount(actFragContxt, context);// YD using for updating count of assets

            if (PreferenceHandler.getUiconfigAddorder(context).equals("1")) {
                syncCustList(actFragContxt, cids);
                Log.d("MAB", "FIRST");
            }

        }

        if (isFullsyncrequired(actFragContxt)) {
            long time = new Date().getTime();


            Utilities.sendmessageUpdatetoserver(actFragContxt, "Part Type List", SplashII.MESSAGE_SENDUPDATE_TO_UI);
            responseObj = DBEngine.getPartType(actFragContxt, context, time, DBEngine.DATA_SOURCE_SERVER);
            if (responseObj != null && responseObj.getStatus().equals("failure")) {
                //errorCheck = true;
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Error Downloading Part Type List", SplashII.MESSAGE_SENDERRORUPDATE_TO_UI);
                //return responseObj;
            }
            if (pause) {
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.RESPONSE_SYNC_PAUSED));
                responseObj.setStatus("failure");
                ;
                return responseObj;
            }

            /*Utilities.sendmessageUpdatetoserver(actFragContxt, "Worker List", SplashII.MESSAGE_SENDUPDATE_TO_UI);
            responseObj = DBEngine.getRes(actFragContxt, context, time, DBEngine.DATA_SOURCE_SERVER);
            Log.d("TAG866",responseObj.toString());
            if (responseObj != null && responseObj.getStatus().equals("failure")) {
                //errorCheck = true;
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Error Downloading Worker List", SplashII.MESSAGE_SENDERRORUPDATE_TO_UI);
                //return responseObj;
            }
            if (pause) {
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.RESPONSE_SYNC_PAUSED));
                responseObj.setStatus("failure");
                return responseObj;
            }
*/
            /*if (PreferenceHandler.getTasksConfig(context).equals("1")) {
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Task Type List", SplashII.MESSAGE_SENDUPDATE_TO_UI);
                responseObj = DBEngine.getServiceType(actFragContxt, context, time, DBEngine.DATA_SOURCE_SERVER);
                if (responseObj != null && responseObj.getStatus().equals("failure")) {
                    //errorCheck = true;
                    Utilities.sendmessageUpdatetoserver(actFragContxt, "Error Downloading Service Type List", SplashII.MESSAGE_SENDERRORUPDATE_TO_UI);
                    //return responseObj;
                }
                if (pause) {
                    responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.RESPONSE_SYNC_PAUSED));
                    responseObj.setStatus("failure");
                    return responseObj;
                }
            }*///YD 2020
            Utilities.sendmessageUpdatetoserver(actFragContxt, "Customer Type List", SplashII.MESSAGE_SENDUPDATE_TO_UI);
            responseObj = DBEngine.getCusType(actFragContxt, context, time, DBEngine.DATA_SOURCE_SERVER);
            if (responseObj != null && responseObj.getStatus().equals("failure")) {
                //errorCheck = true;
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Error Downloading Customer Type List", SplashII.MESSAGE_SENDERRORUPDATE_TO_UI);
                //return responseObj;
            }
            if (pause) {
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.RESPONSE_SYNC_PAUSED));
                responseObj.setStatus("failure");
                return responseObj;
            }


            Utilities.sendmessageUpdatetoserver(actFragContxt, "Site Type List", SplashII.MESSAGE_SENDUPDATE_TO_UI);
            responseObj = DBEngine.getSiteType(actFragContxt, context, time, DBEngine.DATA_SOURCE_SERVER);
            if (responseObj != null && responseObj.getStatus().equals("failure")) {
                //errorCheck = true;
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Error Site Type List", SplashII.MESSAGE_SENDERRORUPDATE_TO_UI);
                //return responseObj;
            }
            if (pause) {
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.RESPONSE_SYNC_PAUSED));
                responseObj.setStatus("failure");
                return responseObj;
            }

            Utilities.sendmessageUpdatetoserver(actFragContxt, "Order Type List", SplashII.MESSAGE_SENDUPDATE_TO_UI);
            responseObj = DBEngine.getOrderType(actFragContxt, context, time, DBEngine.DATA_SOURCE_SERVER);
            if (responseObj != null && responseObj.getStatus().equals("failure")) {
                //errorCheck = true;
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Error Downloading Order Type List", SplashII.MESSAGE_SENDERRORUPDATE_TO_UI);
                //return responseObj;
            }
            if (pause) {
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.RESPONSE_SYNC_PAUSED));
                responseObj.setStatus("failure");
                return responseObj;
            }
            if (PreferenceHandler.getAssetsConfig(context).equals("1")) {
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Assets Type List", SplashII.MESSAGE_SENDUPDATE_TO_UI);
                responseObj = DBEngine.getAssetsType(actFragContxt, context, time, DBEngine.DATA_SOURCE_SERVER);
                if (responseObj != null && responseObj.getStatus().equals("failure")) {
                    //errorCheck = true;
                    Utilities.sendmessageUpdatetoserver(actFragContxt, "Error Downloading Asset Type List", SplashII.MESSAGE_SENDERRORUPDATE_TO_UI);
                    //return responseObj; //YD commenting because unable to login when assets type is empty
                }
                if (pause) {
                    responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.RESPONSE_SYNC_PAUSED));
                    responseObj.setStatus("failure");
                    return responseObj;
                }
            }

            Utilities.sendmessageUpdatetoserver(actFragContxt, "Status Type List", SplashII.MESSAGE_SENDUPDATE_TO_UI);
            responseObj = DBEngine.getStatusType(actFragContxt, context, time, DBEngine.DATA_SOURCE_SERVER);
            if (responseObj != null && responseObj.getStatus().equals("failure")) {
                //errorCheck = true;
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Error Downloading Status Type List", SplashII.MESSAGE_SENDERRORUPDATE_TO_UI);
                //return responseObj;
            }
            if (pause) {
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.RESPONSE_SYNC_PAUSED));
                responseObj.setStatus("failure");
                return responseObj;
            }

            Utilities.sendmessageUpdatetoserver(actFragContxt, "Client Site List", SplashII.MESSAGE_SENDUPDATE_TO_UI);
            responseObj = DBEngine.getcmpyclientsite(actFragContxt, context, time, DBEngine.DATA_SOURCE_SERVER);
            if (responseObj != null && responseObj.getStatus().equals("failure")) {
                //errorCheck = true;
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Error Downloading Client Site List", SplashII.MESSAGE_SENDERRORUPDATE_TO_UI);
                //return responseObj;
            }
            if (pause) {
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.RESPONSE_SYNC_PAUSED));
                responseObj.setStatus("failure");
                return responseObj;
            }


            Utilities.sendmessageUpdatetoserver(actFragContxt, "Client Shift List", SplashII.MESSAGE_SENDUPDATE_TO_UI);
            responseObj = DBEngine.getshifts(actFragContxt, context, time, DBEngine.DATA_SOURCE_SERVER);
            if (responseObj != null && responseObj.getStatus().equals("failure")) {
                //errorCheck = true;
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Error Downloading Shift List", SplashII.MESSAGE_SENDERRORUPDATE_TO_UI);
                //return responseObj;
            }
            if (pause) {
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.RESPONSE_SYNC_PAUSED));
                responseObj.setStatus("failure");
                return responseObj;
            }

            if (PreferenceHandler.getUiconfigAddorder(context).equals("1")) {
                syncCustList(actFragContxt, cids);
                Log.d("MAB", "FIRST");
            }
            else {
                syncCustList(actFragContxt, cids, time, true);
            }


		/*Utilities.sendmessageUpdatetoserver(actFragContxt, "Customer List", SplashII.MESSAGE_SENDUPDATE_TO_UI);
		responseObj = DBEngine.getCustList(actFragContxt ,context, time,DBEngine.DATA_SOURCE_SERVER);
		if(responseObj !=null  && responseObj.getStatus().equals("failure"))
		{
			errorCheck = true;
			Utilities.sendmessageUpdatetoserver(actFragContxt,"Error Customer List", SplashII.MESSAGE_SENDERRORUPDATE_TO_UI);
			return responseObj;
		}
		if(pause){
			//responseObj.setErrorcode(String.valueOf(JSONHandler.ERROR_CODE_RESPONSE_SYNC_PAUSED));
			responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.RESPONSE_SYNC_PAUSED));
			responseObj.setStatus("failure");
			return responseObj;
		}*///YD removing customer list_cal functionality for being downloaded on main thread

        }
        if (errorCheck == true)
            Utilities.sendmessageUpdatetoserver(actFragContxt, "Please try again", SplashII.MESSAGE_SENDUPDATE_TO_UI);
        else
            Utilities.sendmessageUpdatetoserver(actFragContxt, "Download Complete", SplashII.MESSAGE_SENDUPDATE_TO_UI);
        responseObj.setStatus("success");
        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
        return responseObj;
        //return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
    }

    /**
     * YD For getting media for one order only in different thread when order pubnub comes in.
     *
     * @param actFragContxt
     * @param context
     * @param requestObject only one object containg order xml
     */
    public void getAllOrderMedia(final RespCBandServST actFragContxt, final Context context, Object requestObject) {

        PubnubRequest savOdrReq = (PubnubRequest) requestObject;
        Object[] orderList = new XMLHandler(context).getOrderValuesFromXML(savOdrReq.getXml());
        getAllOrderMedia(actFragContxt, context, orderList);
    }

    public void getAllOrderMedia(final RespCBandServST actFragContxt, final Context context, final Object[] orderlist) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                int counter = 0;
                int tempct = 0;
                String response;
                PreferenceHandler.setOrderMedia_DownloadingStart(context, true);
                DBHandler.deleteTable(DBHandler.orderFileMeta);
                boolean isBreak = false;
                while (counter < orderlist.length) {

                    showHideProgress(actFragContxt, "show");

                    Order orderobj = (Order) orderlist[counter];
                    tempct = counter + 1;
                    if (orderobj.getCustMetaCount() > 0) {
                        //Utilities.sendmessageUpdatetoserver(actFragContxt, "Order Media for Order " + tempct, SplashII.MESSAGE_SENDUPDATE_TO_UI);
                        if (Utilities.checkInternetConnection(context, false)) {
                            response = DBEngine.getOrderMediafromServerandSave(context, Long.toString(orderobj.getId()), DBHandler.QUERY_FOR_ORIG);
                            if (response != null) {
                                /*<data><success>false</success><id>0</id><errorcode></errorcode></data>*/
                                XMLHandler xmlhandler = new XMLHandler(context);
                                Document doc = xmlhandler.getDomElement(response);//xmlhandler.parseXML(response,XMLHandler.KEY_DATA);
                                NodeList nl = doc
                                        .getElementsByTagName(XMLHandler.KEY_DATA);
                                for (int i = 0; i < nl.getLength(); i++) {// loop should not be requiredhere
                                    Element e = (Element) nl.item(i);

                                    String success = xmlhandler.getValue(e,
                                            XMLHandler.KEY_DATA_SUCCESS);
                                    String id = xmlhandler.getValue(e,
                                            XMLHandler.KEY_DATA_ID);
                                    if (success.equals(XMLHandler.KEY_DATA_RESP_SUCCESS)) {
                                        if (id.equals("400"))
                                            isBreak = true;
                                    }
                                }
                            }
                        } else {
                            isBreak = true;
                        }
							/*if(Utilities.IsResponseError(response))
				{
					errorCheck = true;
					Utilities.sendmessageUpdatetoserver(actFragContxt ,"Error in Downloading Media for order "+ tempct,SplashII.MESSAGE_SENDERRORUPDATE_TO_UI);
					responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.HTTP_REQUEST_FAILED));
					responseObj.setStatus("failure");
					return responseObj;
				}*/
                    }
                    counter++;
                }
                if (!isBreak) {
                    PreferenceHandler.setOrderMedia_DownloadingStart(context, false);
                }
                showHideProgress(actFragContxt, "hide");
		/*if(pause){
			responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.RESPONSE_SYNC_PAUSED));
			responseObj.setStatus("failure");
			return responseObj;
		}*/
            }
        }).start();
    }

    private void showHideProgress(RespCBandServST actFragContxt, final String showHide) {

        if (actFragContxt == null && BaseTabActivity.currentActiveFrag != null) {
            actFragContxt = BaseTabActivity.mBaseTabActivity;
        }
        if (actFragContxt instanceof Activity) {
            ((Activity) actFragContxt).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (showHide.equals("show")) {
                        if (BaseTabActivity.mProgress != null) {
                            BaseTabActivity.mProgress.setVisibility(View.VISIBLE);
                            SplashII.shouldShowProgress = true;
                        } else
                            SplashII.shouldShowProgress = true;
                    }
                    if (showHide.equals("hide")) {
                        if (BaseTabActivity.mProgress != null) {
                            BaseTabActivity.mProgress.setVisibility(View.GONE);
                            SplashII.shouldShowProgress = false;
                        } else
                            SplashII.shouldShowProgress = false;
                    }
                }
            });
        }
    }

    private void syncCustList(final RespCBandServST actFragContxt, final List<Long> ncids) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response responseObj = DBEngine.getCustList(actFragContxt, context, ncids, DBEngine.DATA_SOURCE_SERVER);
                if (responseObj != null && !responseObj.getStatus().equals("failure")) {

                    Response response = DBEngine.getCustList(null, context, ncids, "localonly");
                    DataObject.customerXmlDataStore = response.getResponseMap();//YD doing this because of differnt thread.

                    PreferenceHandler.setCustListDownloadComplete(context, true);
                    PreferenceHandler.setIsCustListDownloading(context, false);

                    //YD get order and set asset count for each order in the order list_cal.
                    fillAssetCount(actFragContxt, context);
                } else {
                    //YD show popup to resync again
                    ((Activity) actFragContxt).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //showSyncCustListDialog(actFragContxt, context);
                        }
                    });
                }
            }
        }).start();
    }

    public void syncCustList(final RespCBandServST actFragContxt, final List<Long> cids, final long time, final boolean syncall) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (cids.size() > 0) {
                    if (syncall)
                        DBHandler.clearTables();

                    Utilities.sendmessageUpdatetoserver(actFragContxt, "Downloading Customer List", SplashII.MESSAGE_SENDUPDATE_TO_UI);
                    int count = 0;
                    for (final long cid : cids) {
                        String response = DBEngine.getCust(actFragContxt, context, cid, DBEngine.DATA_SOURCE_SERVER);
                        if (response != null) {
                            if (response.length() < 100) // vicky added check to avoid illogical parsing of data if the data has come
                            {
                                ResponseResult rr = new ResponseResult(context);
                                if (rr.parse(response)) {
                                    String success = rr.getSuccess();
                                    if (success != null) {    // vicky if success variable has come, means no data has come. So just return and let the caller handle the error.
                                        continue; //return response;
                                    }
                                }
                            }
                            int result = DBHandler.saveCustomer(context, response);
                            if (result > 0) {
                                count++;
                            }

						/*else{
							responseObj.setStatus("failure");
							responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.IO_ERROR));
							return responseObj;
							//return XMLHandler.getXMLForErrorCode(context, JSONHandler.ERROR_CODE_IO_ERROR);laterYD
						}	*/  // YD commented temporarily
                        }

						/*if (responseObj != null && !responseObj.getStatus().equals("failure")) {

							Response response = DBEngine.getCustList(null, context, time, "localonly");
							DataObject.customerXmlDataStore = response.getResponseMap();//YD doing this because of differnt thread.

							PreferenceHandler.setCustListDownloadComplete(context, true);
							PreferenceHandler.setIsCustListDownloading(context, false);

							//YD get order and set asset count for each order in the order list_cal.
							fillAssetCount(actFragContxt, context);
						} else {
							//YD show popup to resync again
							((Activity) actFragContxt).runOnUiThread(new Runnable() {
								@Override
								public void run() {
									//showSyncCustListDialog(actFragContxt, context);
								}
							});
						}*/
                    }
                    if (count > 0) {
                        //BaseTabActivity.refreshCustList();
                        Response response = DBEngine.getCustList(null, context, cids, "localonly");
                        DataObject.customerXmlDataStore = response.getResponseMap();//YD doing this because of differnt thread.

                    }
                    //DBEngine.createTextFileForService(response);
                    DBHandler.clearList();
                }
            }
        }).start();


    }

    public void fillAssetCount(RespCBandServST actFragContxt, Context context) {
        HashMap<Long, Object> obj = DBEngine.getOrdersfromOfflineInMap(context);

        if (obj != null) {
            Long keys[] = obj.keySet().toArray(new Long[obj.size()]);
            for (int i = 0; i < obj.size(); i++) {
                Order odrObj = (Order) obj.get(keys[i]);
                Response res = DBEngine.getCustAssets(context, String.valueOf(odrObj.getCustomerid()));
                HashMap<Long, Object> assetLst = (HashMap<Long, Object>) res.getResponseMap();
                if (assetLst != null) {
                    updateOrderRequest req = getRequestObjAssestSave(odrObj.getId(), "assetcount", assetLst.size());
                    String resp_update = DBEngine.updateData(context, req,
                            Order.TYPE_UPDATE, 0);
                }
            }
            if (actFragContxt != null && actFragContxt instanceof BaseTabActivity) {
                Log.i(Utilities.TAG, "actFragContxt is instanceof BaseTabActivity so assest is refreshed");
                ((BaseTabActivity) actFragContxt).refreshAssestCount();
            } else {//YD set the flag to true so that it refresh the order list_cal main page when move from one page to another.
                Log.i(Utilities.TAG, "actFragContxt does not instanceof BaseTabActivity so assest count not refreshed");
                OrderListMainFragment.updateOrderFrmPb = true;
            }
        }
    }

    private updateOrderRequest getRequestObjAssestSave(long orderId, String column, int value) {
        updateOrderRequest req = new updateOrderRequest();
        req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi");
        req.setType("post");
        req.setId(String.valueOf(orderId));
        req.setName(column);
        req.setValue("" + value);
        req.setAction(Order.ACTION_SAVE_ORDER_FLD);

        return req;
    }

    private boolean isFullsyncrequired(RespCBandServST actFragContxt) {
        long time = new Date().getTime();
        //	String response;
        Response response;
        if (!syncType.equals("full")) {
            response = DBEngine.getRes(actFragContxt, context, time, DBEngine.DATA_SOURCE_LOCAL);
            if (response == null || response.equals(XMLHandler.XML_EMPTY_DATA))
                return true;
            //response = DBEngine.getCustList(context, time,DBEngine.DATA_SOURCE_LOCAL);//laterYD
            if (response == null || response.equals(XMLHandler.XML_EMPTY_DATA))
                return true;
            return false;
        }

        return true;
    }

    private Object[] syncdownloadOfflineOrders(RespCBandServST actFragContxt, StringBuilder st) {
        Utilities.sendmessageUpdatetoserver(actFragContxt, "Download Start", SplashII.MESSAGE_SENDUPDATE_TO_UI);
        Object[] orderlist = null;
        // Calendar calendar = Calendar.getInstance();
        Log.i(Utilities.TAG, "syncdownloadOfflineData : ");
        Date date = null;
        String getOdrOfDate = PreferenceHandler.getOdrGetDate(context); //YD TODO write the code to get date from pubnub
        if (getOdrOfDate != null && !getOdrOfDate.equals("")) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                date = simpleDateFormat.parse(getOdrOfDate);
            } catch (ParseException e) {
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

        String timezone = Utilities.getTimeZone();

        try {

            Utilities.sendmessageUpdatetoserver(actFragContxt, "Today's Orders", SplashII.MESSAGE_SENDUPDATE_TO_UI);

            orderlist = DBEngine.getOrdersForOffline(timezone, Utilities.getCurrentTimeInMillis(), fdate, todate, context, st);
            String result = st.toString();
            if (Utilities.IsResponseError(result)) {
                errorCheck = true;
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Error in Downloading orders", SplashII.MESSAGE_SENDERRORUPDATE_TO_UI);
                return orderlist;
            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return orderlist;

    }

    public Response syncofflineOrderSupportItems(RespCBandServST actFragContxt, String tablename, String action, String reqtype,
                                                 String uniqueKey, String parentid, int recordtype) throws IOException {


        Map<String, String> params = null;
        String response = null;
        Response responseObj = null;
        int delcount = 0;
        boolean firsttime = true;
        int total_records = 0;
        int count = 0;

        do {
            Cursor cursor = DBHandler.getOfflineModifiedRecords(context, tablename);
            if (cursor != null && cursor.getCount() > 0) {
                AceRequestHandler.setNumOfItemAdded = 0;
                cursor.moveToFirst();
                int records = cursor.getCount();

                if (firsttime) {
                    firsttime = false;
                    total_records = records;

                }


                String metapathname = "";
                if (records > 0) {
                    count++;
                    Utilities.sendmessageUpdatetoserver(actFragContxt, getMessageTobeDisplayed(action) + " " + count + " of " + total_records, SplashII.MESSAGE_SENDUPDATE_TO_UI);
                    params = new HashMap<String, String>();
                    String modifieddata = DBHandler.getvaluefromCursor(cursor, DBHandler.modifiedField);
                    if (modifieddata != null) {
                        action = getActionDuetoModify(modifieddata, tablename);
                        params = ServerRequestHandler.createparamsForRequest(cursor, action, context, params);
                    }

                    //YD
                    String localOid = "-1";
                    if (cursor != null) {
                        localOid = DBHandler.getvaluefromCursor(cursor, "oid");  //YD saving this to localOid because so that when the row is inserted again i can put again to the row in db
                        //localOid = DBHandler.getvaluefromCursor(cursor,DBHandler.localOidField);//YD problem when order and part is created offline and then app goes to online to sync the data thenn the part created before didnot get visible
                    }
                    String fakeid = params.get(uniqueKey);
                    String pid = params.get(parentid);
                    if (modifieddata != null && modifieddata.equals(DBHandler.modifiedNew))
                        params.put(uniqueKey, "0");

                    if (tablename.equals(DBHandler.orderPartTable) && orderIdToCheckSyncCount == Long.valueOf(localOid))//YD adding
                    {
                        AceRequestHandler.setNumOfItemAdded = count;
                    }
                    if (action == OrderMedia.ACTION_MEDIA_DELETE) {
                        reqtype = JSONHandler.KEY_REQUEST_TYPE_POST;
                    }
                    if (reqtype.equals(JSONHandler.KEY_REQUEST_TYPE_POST))
                        response = HttpConnection
                                .post(context, "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", params);
                    else if (reqtype.equals(JSONHandler.KEY_REQUEST_TYPE_POST_MULTIPART)) {
                        metapathname = params.get(OrderMedia.ORDER_META_PATH);
                        File file = new File(metapathname);
                        params.remove(OrderMedia.ORDER_META_PATH);
                        params.remove(OrderMedia.ID);
                        response = HttpConnection.postHTTPsMultipart(context, "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/fileupload", params, file, params.get(OrderMedia.ORDER_FILE_MIME_TYPE));
                    } //dtl=uuuuuu, geo=28.628532,77.073951, stmp=1441647080623, oid=42518051, action=savefile, mime=jpg, tid=1} //YD MEDIA REQUEST
                    else
                        response = HttpConnection.get(context, "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", params);
                    String whereClause = uniqueKey + "=" + fakeid;
                    if (response != null) {
                        Utilities.log(context, "server response : " + response);
                        XMLHandler xmlhandler = new XMLHandler(context);
                        Document doc = xmlhandler.getDomElement(response);
                        NodeList nl = doc
                                .getElementsByTagName(XMLHandler.KEY_DATA);
                        for (int i = 0; i < nl.getLength(); i++) {// loop should not be requiredhere
                            Element e = (Element) nl.item(i);
                            String success = xmlhandler.getValue(e,
                                    XMLHandler.KEY_DATA_SUCCESS);

                            if (!success.equals(XMLHandler.KEY_DATA_RESP_FAIL)) {
                                // delete old record
                                if (modifieddata.equals(DBHandler.modifiedNew)) {

                                    delcount = DBHandler.delete(context,
                                            tablename, whereClause);
                                    Log.i(Utilities.TAG, recordtype
                                            + " delete result : " + delcount);
                                    if (delcount > 0) {

                                        Object[] list = xmlhandler.getObjectValuesfromXML(response, action);
                                        if (list != null) {
                                            responseObj = new Response();
                                            responseObj.setResponseObj(Utilities.setObjectList(list));
                                            responseObj.setStatus("success");
                                            responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                                            Object obj = list[0];
                                            if (obj instanceof OrderMedia) {
                                                OrderMedia odmedia = (OrderMedia) obj;
                                                odmedia.setLocalId(Long.parseLong(fakeid));
                                                odmedia.setLocalOid(Long.parseLong(localOid));
                                                odmedia.setMetapath(metapathname);
                                            } else if (obj instanceof OrderPart) {
                                                OrderPart odpart = (OrderPart) obj;
                                                odpart.setLocalId(Long.parseLong(fakeid));
                                                odpart.setLocalOid(Long.parseLong(localOid));
                                            }else if (obj instanceof Form) {
                                                Form odpart = (Form) obj;
                                                odpart.setLocalid(Long.parseLong(fakeid));
                                                odpart.setLocalOid(Long.parseLong(localOid));
                                            } else if (obj instanceof OrderTask) {
                                                OrderTask odtask = (OrderTask) obj;
                                                odtask.setLocalId(Long.parseLong(fakeid));
                                                odtask.setLocalOid(Long.parseLong(localOid));
                                            } else if (obj instanceof Assets) {
                                                Assets odrAssets = (Assets) obj;
                                                odrAssets.setLocalid(Long.parseLong(fakeid));
                                                //odrAssets.setLocalOid(Long.parseLong(localOid));
                                            } else if (obj instanceof Site) {
                                                Site custSite = (Site) obj;
                                                custSite.setLocalid(Long.parseLong(fakeid));
                                            } else if (obj instanceof CustomerContact) {
                                                CustomerContact custCont = (CustomerContact) obj;
                                                custCont.setLocalid(Long.parseLong(fakeid));
                                            }

                                            int result = 0;
                                            result = DBHandler.insertRecord(context, tablename, list, DBHandler.QUERY_FOR_ORIG);
                                            if (result > 0) {
                                                Log.i(Utilities.TAG, recordtype
                                                        + "Inserted successfully");
                                                // since the sites are updated with new id from server. We need to update orders with that site
                                                if (action == Site.ACTION_SAVE_SITE) {
                                                    Site st = (Site) list[0];
                                                    updaterelatedItemstoOrder(DBHandler.orderListTable, Order.ORDER_CUSTOMER_SITE_ID, Long.toString(st.getId()), fakeid);
                                                }
                                            } else {// insert failed
                                            }
                                        } else {
                                            // no list_cal found
                                        }

                                    } else {
                                        // delete response is invalid
                                    }

                                } else if (modifieddata.equals(DBHandler.deleted)) {
                                    delcount = DBHandler.delete(context, tablename, whereClause);
                                    Log.i(Utilities.TAG, "delcount Fields in table " + delcount);
                                } else {
                                    // the record is not added as new. Just update it
                                    String resp_update = updateFieldsinTable(tablename, DBHandler.modifiedField,
                                            "", uniqueKey, params.get(uniqueKey));
                                    Log.i(Utilities.TAG, "Update Fields in table " + recordtype);
                                }


                            } else {
                                if (Utilities.getResponseErrorCode(response) == null) {
                                    syncbackupData(actFragContxt, action, cursor, pid, fakeid);
                                    delcount = DBHandler.delete(context,
                                            tablename, whereClause);
                                    response = XMLHandler.XML_DATA_SUCCESS_MESSAGE;// make response as successful as error handled now
                                } else {
                                    freeCursor(cursor);
                                    return responseObj;
                                }
                            }
                        }// for if end

                    } else {// null response received
                        syncbackupData(actFragContxt, action, cursor, pid, fakeid);
                        delcount = DBHandler.delete(context,
                                tablename, whereClause);
                        responseObj = new Response();
                        responseObj.setStatus("success");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        //response = XMLHandler.XML_DATA_SUCCESS_MESSAGE;// make response as successful as error handled now
                        freeCursor(cursor);
                    }
                    records--;
                }

            } else {
                break;
            }
        } while (true);
        return responseObj;
    }

    private Cursor freeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        return cursor;
    }

    public String syncbackupData(RespCBandServST actFragContxt, String action, Cursor cursor, String identifier1, String identifier2) {
        String msg = "";
        if (identifier1 == null || identifier1.equals(""))
            msg = action + "_" + identifier2;
        else if (identifier2 == null || identifier2.equals(""))
            msg = action + "_" + identifier1;
        else
            msg = action + "_" + identifier1 + "_" + identifier2;
        errorCheck = true;
        Utilities.sendmessageUpdatetoserver(actFragContxt, "Error. Storing " + getMessageTobeDisplayed(action) + " to storage", SplashII.MESSAGE_SENDERRORUPDATE_TO_UI);
        ServerRequestHandler.dumpXMLFromDB(cursor, action, this.context, msg, identifier1);
        return "";
    }

    //YD Uploading orders to server
    public Response syncofflineOrders(RespCBandServST actFragContxt) throws IOException {

        Map<String, String> params = null;
        Map<String, String> uploadparams = null;
        Response responseObj = null;
        String req_type = "post";
        String req_url = "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi";
        String response = null;
        int delcount = 0;
        boolean firsttime = true;
        int count = 0;
        int total_records = 0;
        do {
            Cursor cursor = DBHandler.getOfflineModifiedRecords(context, DBHandler.orderListTable);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                int records = cursor.getCount();

                if (firsttime) {
                    firsttime = false;
                    total_records = records;
                }
                if (records > 0) {
                    count++;
                    Utilities.sendmessageUpdatetoserver(actFragContxt, getMessageTobeDisplayed(Order.ACTION_SAVE_ORDER) + " " + count + " of " + total_records, SplashII.MESSAGE_SENDUPDATE_TO_UI);
                    params = new HashMap<String, String>();
                    String modifieddata = DBHandler.getvaluefromCursor(cursor, DBHandler.modifiedField);
                    params = ServerRequestHandler.createparamsForRequest(cursor, Order.ACTION_SAVE_ORDER, context, params);

                    String fakeorderid = params.get(Order.ORDER_ID);
                    String temp = params.get(Order.ORDER_START_DATE).toString();
                    temp = temp.replace("-", "/");
                    temp = temp.replace("/00", "-00");
                    params.put(Order.ORDER_START_DATE, temp);
                    temp = params.get(Order.ORDER_END_DATE).toString();
                    temp = temp.replace("-", "/");
                    temp = temp.replace("/00", "-00");
                    params.put(Order.ORDER_END_DATE, temp);
                    params.put(Order.ORDER_RESET, "0");

                    if (modifieddata.compareTo(DBHandler.modifiedNew) != 0) {
                        Long tstmp = new Date().getTime();
                        params.put(Order.ORDER_TMSTAMP, String.valueOf(tstmp));
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm Z");//YD changed to "/" from "-"(earlier using "-") in code because getting /'s in date here
                        Date parsedDate;
                        try {
                            parsedDate = formatter.parse(params.get(Order.ORDER_START_DATE).toString());
                            params.put(Order.ORDER_START_TIME, String.valueOf(parsedDate.getTime()));
                            parsedDate = formatter.parse(params.get(Order.ORDER_END_DATE).toString());
                            params.put(Order.ORDER_END_TIME, String.valueOf(parsedDate.getTime()));
                            params.remove(Order.ORDER_TYPE);
                            params.remove(Order.ORDER_EPOCH_TIME);
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        uploadparams = params;
                    } else// YD if the modified field id new
                    {
                        uploadparams = params;
                        params.put(Order.ORDER_ID, "0");
                        params.put(Order.ORDER_FLG, "0|");
                        params.put(Order.ORDER_WKFUPD, "0");
                    }

                    String whereClause = Order.ORDER_ID + " = " + fakeorderid;
                    if (req_type.equals(JSONHandler.KEY_REQUEST_TYPE_POST))
                        response = HttpConnection.post(context, "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", uploadparams);
                    else
                        response = HttpConnection.get(context, "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", uploadparams);

                    if (response != null) {
                        Utilities.log(context, "server response : " + response);
                        XMLHandler xmlhandler = new XMLHandler(context);
                        Document doc = xmlhandler.getDomElement(response);
                        NodeList nl = doc
                                .getElementsByTagName(XMLHandler.KEY_DATA);
                        for (int i = 0; i < nl.getLength(); i++) {// loop should not be requiredhere
                            Element e = (Element) nl.item(i);
                            String success = xmlhandler.getValue(e,
                                    XMLHandler.KEY_DATA_SUCCESS);
                            if (!success.equals(XMLHandler.KEY_DATA_RESP_FAIL)) {

                                if (modifieddata.compareTo(DBHandler.modifiedNew) == 0) {
                                    delcount = DBHandler.delete(context,
                                            DBHandler.orderListTable, whereClause);
                                    Log.i(Utilities.TAG, Order.TYPE
                                            + " delete result : " + delcount);
                                    if (delcount > 0) {
                                        Log.i(Utilities.TAG, Order.TYPE +
                                                "delete result" + delcount);
                                        Object[] orderList = xmlhandler.getOrderValuesFromXML(response);
                                        int result = 0;
                                        if (orderList != null) {
                                            responseObj = new Response();
                                            responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                                            responseObj.setStatus("success");
                                            responseObj.setResponseObj(Utilities.setObjectList(orderList));
                                            Order odrobj = (Order) orderList[0];
                                            odrobj = updateOrderObjectBeforeInsert(odrobj, params, fakeorderid);//YD adding fakeid to localid
                                            result = DBHandler.insertRecord(context, DBHandler.orderListTable, orderList, DBHandler.QUERY_FOR_ORIG);
                                            if (odrobj != null && result > 0) {

                                                if (odrobj.getCustFormCount() > 0) {
                                                    //	updateFieldsinTable(DBHandler.orderTaskTable, DBHandler.localOidField, fakeorderid, OrderTask.ORDER_ID, fakeorderid);
                                                    updaterelatedItemstoOrder(DBHandler.formTable, Form.FORM_OID, Long.toString(odrobj.getId()), fakeorderid);
                                                }
                                                if (odrobj.getCustPartCount() > 0) {
                                                    //	updateFieldsinTable(DBHandler.orderPartTable, DBHandler.localOidField, fakeorderid, OrderPart.ORDER_ID, fakeorderid);//YD first update the local oid field with local values
                                                    updaterelatedItemstoOrder(DBHandler.orderPartTable, OrderPart.ORDER_ID, Long.toString(odrobj.getId()), fakeorderid);
                                                }
                                                if (odrobj.getCustMetaCount() > 0) {
                                                    //updateFieldsinTable(DBHandler.orderFileMeta, DBHandler.localOidField, fakeorderid, OrderMedia.ORDER_ID, fakeorderid);
                                                    updaterelatedItemstoOrder(DBHandler.orderFileMeta, OrderMedia.ORDER_ID, Long.toString(odrobj.getId()), fakeorderid);
                                                }
                                                updaterelatedItemstoOrder(DBHandler.orderFileNotes, OrderNotes.ORDER_ID, Long.toString(odrobj.getId()), fakeorderid);

                                                //sfsd

                                            } else {
                                                // log the insert has failed.
                                            }
                                        } else {
                                        }
                                    }
                                } else {
                                    // updateOrderNoteWhileSync(fakeorderid); //YD if server order than it works fine
                                    String resp_update = updateFieldsinTable(DBHandler.orderListTable, DBHandler.modifiedField,
                                            "", Order.ORDER_ID, params.get(Order.ORDER_ID));
                                    Log.i(Utilities.TAG, "Update Fields in table " + DBHandler.orderListTable);


                                }
                            } else {
                                if (Utilities.getResponseErrorCode(response) == null) {
                                    syncbackupData(actFragContxt, Order.ACTION_SAVE_ORDER, cursor, fakeorderid, "");// checkYD
                                    delcount = DBHandler.delete(context,
                                            DBHandler.orderListTable, whereClause);
                                    response = XMLHandler.XML_DATA_SUCCESS_MESSAGE;// make response as successful as error handled now
                                } else {
                                    freeCursor(cursor);
                                    return responseObj;
                                }

                            }


                        }// end for
                    } else {
                        syncbackupData(actFragContxt, Order.ACTION_SAVE_ORDER, cursor, fakeorderid, "");
                        delcount = DBHandler.delete(context,
                                DBHandler.orderListTable, whereClause);
                        response = XMLHandler.XML_DATA_SUCCESS_MESSAGE;// make response as successful as error handled now
                        freeCursor(cursor);
                    }
                    records--;
                }// end if
                freeCursor(cursor);
            }// if cursor
            else {
                break;
            }
        } while (true);

        return responseObj;
    }

    public void updateOrderNoteWhileSync(String id) {
        int operationcount = 0;
        OrderNotes onote = new OrderNotes();
        onote.setId(Long.parseLong(id));
        //onote.setOrdernote(reqObj.getValue());
        onote.setModified(DBHandler.updated);
        OrderNotes[] ordernotes = {onote};
        operationcount = DBHandler.updateRecord(context,
                DBHandler.orderFileNotes,
                ordernotes, DBHandler.QUERY_FOR_ORIG);
			/*if(operationcount<=0)// insert note if update failed.
			{
				operationcount = DBHandler.insertRecord(context,
						DBHandler.orderFileNotes,
						ordernotes,  DBHandler.QUERY_FOR_ORIG);
			}*/
			/*ContentValues cv = new ContentValues();
			String length="0";
			if(onote.getOrdernote().length()>0)
				length="1";
			cv.put(Order.ORDER_NOTE, length);
			String whereClause = Order.ORDER_ID + "=" + onote.getId();
			int result = DBHandler.updateTable(DBHandler.orderListTable,cv,whereClause,null);*/
        if (operationcount > 0)
            DBEngine.syncDataToSever(context, OrderNotes.TYPE_SAVE);

    }

    public String updaterelatedItemstoOrder(String tablename, String columnname, String columnvalue, String key) throws IOException {

        ContentValues cv = null;
        try {
            cv = new ContentValues();
            cv.put(columnname, columnvalue);
            String whereClause = columnname + "=" + key;
            int result = DBHandler.updateTable(tablename, cv, whereClause, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String updateFieldsinTable(String tablename, String columnname, String columnvalue, String keyname, String keyvalue) throws IOException {

        ContentValues cv = null;
        try {
            cv = new ContentValues();
            cv.put(columnname, columnvalue);
            String whereClause = keyname + "=" + keyvalue;
            int result = DBHandler.updateTable(tablename, cv, whereClause, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
        // TODO: handle exception
    }

    private Order updateOrderObjectBeforeInsert(Order order, Map<String, String> params, String localId) {
        if (order != null) {
            order.setCustMetaCount(Long.parseLong(params.get(Order.ORDER_CUSTOMER_MEDIA_COUNT)));
            order.setCustPartCount(Long.parseLong(params.get(Order.ORDER_CUSTOMER_PART_COUNT)));
            order.setCustFormCount(Long.parseLong(params.get(Order.ORDER_CUSTOMER_FORM_COUNT)));
            order.setLocalId(Long.parseLong(localId));

            if (order.getId() != Long.valueOf(localId))
                updateOrderNoteWhileSync(String.valueOf(order.getId())); //YD if server order than it works fine
            else
                updateOrderNoteWhileSync(localId); //YD if server order than it works fine
        }
        return order;

    }

    private String getActionDuetoModify(String modifieddata, String tablename) {
        if (tablename.equals(DBHandler.orderTaskTable)) {
            if (modifieddata.equals(DBHandler.modifiedNew) || modifieddata.equals(DBHandler.updated)) {
                return OrderTask.ACTION_SAVE_ORDER_TASK;
            }
            if (modifieddata.equals(DBHandler.deleted)) {
                return OrderTask.ACTION_DELETE_ORDER_TASK;
            }

        }
        if (tablename.equals(DBHandler.orderPartTable)) {
            if (modifieddata.equals(DBHandler.modifiedNew) || modifieddata.equals(DBHandler.updated)) {
                return OrderPart.ACTION_SAVE_ORDER_PART;
            }
            if (modifieddata.equals(DBHandler.deleted)) {
                return OrderPart.ACTION_DELETE_ORDER_PART;
            }
        }
        if (tablename.equals(DBHandler.formTable)) {
            if (modifieddata.equals(DBHandler.modifiedNew) || modifieddata.equals(DBHandler.updated)) {
                return Form.ACTION_SAVE_FORM;
            }
            if (modifieddata.equals(DBHandler.deleted)) {
                return Form.ACTION_DELETE_ORDER_FORM;
            }

        }
        if (tablename.equals(DBHandler.orderFileNotes)) {
            return OrderNotes.ACTION_NOTES_SAVE;
        }
        if (tablename.equals(DBHandler.orderFileMeta)) {
            if (modifieddata.equals(DBHandler.modifiedNew) || modifieddata.equals(DBHandler.updated)) {
                return OrderMedia.ACTION_MEDIA_SAVE;
            }
            if (modifieddata.equals(DBHandler.deleted)) {
                return OrderMedia.ACTION_MEDIA_DELETE;
            }

        }
        if (tablename.equals(DBHandler.siteTable)) {
            if (modifieddata.equals(DBHandler.modifiedNew) || modifieddata.equals(DBHandler.updated)) {
                return Site.ACTION_SAVE_SITE;
            }
            if (modifieddata.equals(DBHandler.uupdated)) {
                return Site.ACTION_EDIT_SITE;
            }
            if (modifieddata.equals(DBHandler.deleted)) {
                return Site.ACTION_DELETE_SITE;
            }

        }
        if (tablename.equals(DBHandler.customercontactTable)) {
            if (modifieddata.equals(DBHandler.uupdated)) {
                return CustomerContact.ACTION_CONTACT_EDIT;
            }
            if (modifieddata.equals(DBHandler.modifiedNew)) {
                return CustomerContact.ACTION_SAVE_CONTACT;
            }
        }
        if (tablename.equals(DBHandler.shiftTable)) {
            if (modifieddata.equals(DBHandler.deleted)) {
                return Shifts.ACTION_DELETE_SHIFTS;
            }

            if (modifieddata.equals(DBHandler.multipledeleted)) {
                return Shifts.DELETE_MULTIPLE_SHIFTS;
            }

            if (modifieddata.equals(DBHandler.updated)) {
                return Shifts.ACTION_EDIT_SHIFTS;
            }
            if (modifieddata.equals(DBHandler.modifiedNew)) {
                return Shifts.ACTION_SYNC_SHIFTS;
            }
        }
        if (tablename.equals(DBHandler.assestTable)) {
            if (modifieddata.equals(DBHandler.modifiedNew) || modifieddata.equals(DBHandler.updated)) {
                return Assets.ACTION_SAVE_ORDER_ASSETS;
            }
        }
        return "";
    }

    public String getMessageTobeDisplayed(String message) {
        if (message.equals(OrderNotes.ACTION_NOTES_SAVE)) {
            if (PreferenceHandler.getOrderHead(context) != null && !PreferenceHandler.getOrderHead(context).trim().equals("")) {
                return PreferenceHandler.getOrderHead(context) + " Notes";
            } else {
                return "Order Notes";
            }

        }
        if (message.equals(OrderTask.ACTION_SAVE_ORDER_TASK) || message.equals(OrderTask.ACTION_DELETE_ORDER_TASK)) {
            if (PreferenceHandler.getOrderHead(context) != null && !PreferenceHandler.getOrderHead(context).trim().equals("")) {
                return PreferenceHandler.getOrderHead(context) + " Services";
            } else {
                return "Order Services";
            }
        }
        if (message.equals(OrderPart.ACTION_SAVE_ORDER_PART) || message.equals(OrderPart.ACTION_DELETE_ORDER_PART)) {
            if (PreferenceHandler.getOrderHead(context) != null && !PreferenceHandler.getOrderHead(context).trim().equals("")) {
                return PreferenceHandler.getOrderHead(context) + " Parts";
            } else {
                return "Order Parts";
            }
        }
        if (message.equals(OrderMedia.ACTION_MEDIA_SAVE) || message.equals(OrderMedia.ACTION_MEDIA_DELETE)) {
            if (PreferenceHandler.getOrderHead(context) != null && !PreferenceHandler.getOrderHead(context).trim().equals("")) {
                return PreferenceHandler.getOrderHead(context) + " Media";
            } else {
                return "Order Media";
            }
        }
        if (message.equals(Order.ACTION_SAVE_ORDER)) {
            if (PreferenceHandler.getOrderHead(context) != null && !PreferenceHandler.getOrderHead(context).trim().equals("")) {
                return PreferenceHandler.getOrderHead(context);
            } else {
                return "Order";
            }
        }
        if (message.equals(Site.ACTION_SAVE_SITE)) {
            if (PreferenceHandler.getCustomerHead(context) != null && !PreferenceHandler.getCustomerHead(context).trim().equals("")) {
                return PreferenceHandler.getCustomerHead(context) + " Locations";
            } else {
                return "Customer Locations";
            }
        }
        if (message.equals(CheckinOut.CC_ACTION)) {
            return "Timecard";
        }
        if (message.equals(OrdersMessage.ACTION_MESSAGE_SAVE)) {
            return "Sending Message";
        }
        if (message.equals(Shifts.ACTION_SYNC_SHIFTS)) {
            return "Sending Shifts";
        }
        if (message.equals(Form.ACTION_SAVE_FORM))
            return "Sending Order Form";

        return "";
    }

    public Response syncofflineShift(RespCBandServST actFragContxt, String tablename, String action, String reqtype, String uniqueKey, int recordtype) throws IOException {


        Map<String, String> params = null;
        String response = null;
        Response responseObj = null;
        int delcount = 0;
        boolean firsttime = true;
        int count = 0;


        Cursor cursormodify = DBHandler.getOfflineModifiedShift(context, tablename, "");
        int records = cursormodify.getCount();
        if (cursormodify.moveToFirst()) {
            do {
                count++;
                //Utilities.sendmessageUpdatetoserver(actFragContxt,getMessageTobeDisplayed(action)+" " + count+" of "+ records,SplashII.MESSAGE_SENDUPDATE_TO_UI);
                params = new HashMap<String, String>();
                String modifieddata = DBHandler.getvaluefromCursor(cursormodify, DBHandler.modifiedField);
                if (modifieddata != null) {
                    action = getActionDuetoModify(modifieddata, tablename);
                    if (SwapingFragment.isSwappingFragment)
                        action = getActionDuetoModify("multipedeleted", tablename);
                    params = ServerRequestHandler.createparamsForRequest(cursormodify, action, context, params);
                    if (reqtype.equals(JSONHandler.KEY_REQUEST_TYPE_POST))
                        response = HttpConnection.post(context, "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", params);
                } //dtl=uuuuuu, geo=28.628532,77.073951, stmp=1441647080623, oid=42518051, action=savefile, mime=jpg, tid=1} //YD MEDIA REQUEST
                else
                    response = HttpConnection.get(context, "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", params);

                if (response != null) {
                    Utilities.log(context, "server response : " + response);
                    XMLHandler xmlhandler = new XMLHandler(context);
                    Document doc = xmlhandler.getDomElement(response);
                    NodeList nl = doc
                            .getElementsByTagName(XMLHandler.KEY_DATA);
                    for (int i = 0; i < nl.getLength(); i++) {// loop should not be requiredhere
                        Element e = (Element) nl.item(i);
                        String success = xmlhandler.getValue(e,
                                XMLHandler.KEY_DATA_SUCCESS);
                        if (!success.equals(XMLHandler.KEY_DATA_RESP_FAIL)) {
                            if (modifieddata.equals(DBHandler.updated)) {
                                ContentValues cv = null;
                                try {
                                    cv = new ContentValues();
                                    cv.put(DBHandler.modifiedField, "");
                                    String whereClause = Shifts.SHIFT_ID + "='" + cursormodify.getLong(0) + "'";
                                    int result = DBHandler.updateTable(tablename, cv, whereClause, null);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                try {
                                    String whereClause = Shifts.SHIFT_ID + "='" + cursormodify.getLong(0) + "'";
                                    DBHandler.delete(context, tablename, whereClause);
                                } catch (Exception execp) {
                                    execp.printStackTrace();
                                }
                            }

                        } else {

                        }
                    }// for if end

                } else {

                }

            } while (cursormodify.moveToNext());
        }

        Cursor cursor = DBHandler.getOfflineModifiedShift(context, tablename, DBHandler.modifiedNew);
        //if (cursor != null && cursor.moveToFirst()) {
        //AceRequestHandler.setNumOfItemAdded=0;
        String metapathname = "";
        int newrecords = cursor.getCount();
        if (cursor.moveToFirst()) {
            //Utilities.sendmessageUpdatetoserver(actFragContxt,getMessageTobeDisplayed(action)+"/ New Shifts",SplashII.MESSAGE_SENDUPDATE_TO_UI);
            params = new HashMap<String, String>();
            String modifieddata = DBHandler.getvaluefromCursor(cursor, DBHandler.modifiedField);
            if (modifieddata != null) {
                action = getActionDuetoModify(modifieddata, tablename);
                params = ServerRequestHandler.createparamsForRequest(cursor, action, context, params);

                if (reqtype.equals(JSONHandler.KEY_REQUEST_TYPE_POST))
                    response = HttpConnection
                            .post(context, "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", params);
            } //dtl=uuuuuu, geo=28.628532,77.073951, stmp=1441647080623, oid=42518051, action=savefile, mime=jpg, tid=1} //YD MEDIA REQUEST
            else
                response = HttpConnection.get(context, "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", params);
            //String whereClause = uniqueKey + "="+fakeid;
            if (response != null) {
                Utilities.log(context, "server response : " + response);
                XMLHandler xmlhandler = new XMLHandler(context);
                Document doc = xmlhandler.getDomElement(response);
                NodeList nl = doc
                        .getElementsByTagName(XMLHandler.KEY_DATA);
                for (int i = 0; i < nl.getLength(); i++) {// loop should not be requiredhere
                    Element e = (Element) nl.item(i);
                    String success = xmlhandler.getValue(e,
                            XMLHandler.KEY_DATA_SUCCESS);
                    if (!success.equals(XMLHandler.KEY_DATA_RESP_FAIL)) {
                        ContentValues cv = null;
                        try {
                            cv = new ContentValues();
                            cv.put(DBHandler.modifiedField, "");
                            String whereClause = DBHandler.modifiedField + "='" + DBHandler.modifiedNew + "'";
                            int result = DBHandler.updateTable(tablename, cv, whereClause, null);
                            long time = new Date().getTime();
                            DBEngine.getshifts(actFragContxt, context, time, DBEngine.DATA_SOURCE_SERVER);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {

                    }
                }// for if end

            } else {

            }
        }


        return responseObj;
    }

    public Response syncMessages(RespCBandServST actFragContxt) throws IOException {

        Map<String, String> params = null;
        String response = null;
        Response responseObj = null;
        boolean firsttime = true;
        int count = 0;
        int total_records = 0;
        int delcount = 0;
        do {// trying this because, may be the cursor gets corrupted if some record is deleted.
            Cursor cursor = DBHandler.getOfflineModifiedRecords(context, DBHandler.MessageTable);
            if (cursor != null && cursor.getCount() > 0) {

                cursor.moveToFirst();
                int records = cursor.getCount();

                if (firsttime) {
                    firsttime = false;
                    total_records = records;

                }


                if (records > 0) {
                    count++;
                    Utilities.sendmessageUpdatetoserver(actFragContxt, getMessageTobeDisplayed(MessagePanic.ACTION_WORKER_MESSAGE_PANIC) + " " + count + " of " + total_records, SplashII.MESSAGE_SENDUPDATE_TO_UI);
                    params = new HashMap<String, String>();

                    params = ServerRequestHandler.createparamsForRequest(cursor, MessagePanic.ACTION_WORKER_MESSAGE_PANIC, context, params);
                    response = HttpConnection
                            .post(context, "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", params);

                    String whereClause = MessagePanic.MESSAGE_TIMESTAMP + "=" + params.get(MessagePanic.MESSAGE_TIMESTAMP);
                    ;
                    if (response != null) {
                        Utilities.log(context, "server response : " + response);
                        XMLHandler xmlhandler = new XMLHandler(context);
                        Document doc = xmlhandler.getDomElement(response);
                        NodeList nl = doc
                                .getElementsByTagName(XMLHandler.KEY_DATA);
                        for (int i = 0; i < nl.getLength(); i++) {// loop should not be requiredhere
                            Element e = (Element) nl.item(i);
                            String success = xmlhandler.getValue(e,
                                    XMLHandler.KEY_DATA_SUCCESS);

                            if (!success.equals(XMLHandler.KEY_DATA_RESP_FAIL)) {
                                // delete old record

                                delcount = DBHandler.delete(context,
                                        DBHandler.MessageTable, whereClause);
                                if (delcount > 0) {

                                } else {
                                    delcount = DBHandler.delete(context,
                                            DBHandler.MessageTable, whereClause);// still deleting it. Will save as xml later.
                                    // delete response is invalid
                                }


                            } else {
                                if (Utilities.getResponseErrorCode(response) == null) {
                                    syncbackupData(actFragContxt, CheckinOut.CC_ACTION, cursor, "CheckinInfo", "");
                                    delcount = DBHandler.delete(context,
                                            DBHandler.MessageTable, whereClause);
                                    response = XMLHandler.XML_DATA_SUCCESS_MESSAGE;// make response as successful as error handled now
                                } else {
                                    freeCursor(cursor);
                                    return responseObj;
                                    //return response;
                                }
                            }
                        }// for loop end

                    } else {// YD null response received
                        syncbackupData(actFragContxt, CheckinOut.CC_ACTION, cursor, "CheckinInfo", "");
                        delcount = DBHandler.delete(context,
                                DBHandler.checkincheckoutTable, whereClause);
                        freeCursor(cursor);
                        responseObj = new Response();
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_DATA));
                        responseObj.setStatus("failure");
                        return responseObj;
                        //return XMLHandler.XML_NO_DATA;// make response as successful as error handled now
                    }

                }
                freeCursor(cursor);
            } else
                break;
        } while (true);
        return responseObj;
    }

}
