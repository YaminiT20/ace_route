package com.aceroute.mobile.software.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.aceroute.mobile.software.AceOfflineSyncEngine;
import com.aceroute.mobile.software.AceRouteService;
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
import com.aceroute.mobile.software.component.reference.CheckinOut;
import com.aceroute.mobile.software.component.reference.ClientSite;
import com.aceroute.mobile.software.component.reference.CustHistoryToken;
import com.aceroute.mobile.software.component.reference.Customer;
import com.aceroute.mobile.software.component.reference.CustomerContact;
import com.aceroute.mobile.software.component.reference.CustomerType;
import com.aceroute.mobile.software.component.reference.Form;
import com.aceroute.mobile.software.component.reference.MessagePanic;
import com.aceroute.mobile.software.component.reference.OrderStatus;
import com.aceroute.mobile.software.component.reference.OrderTypeList;
import com.aceroute.mobile.software.component.reference.Parts;
import com.aceroute.mobile.software.component.reference.ResponseResult;
import com.aceroute.mobile.software.component.reference.ServiceType;
import com.aceroute.mobile.software.component.reference.Shifts;
import com.aceroute.mobile.software.component.reference.Site;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.component.reference.Worker;
import com.aceroute.mobile.software.http.Api;
import com.aceroute.mobile.software.http.HttpConnection;
import com.aceroute.mobile.software.http.RequestObject;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.ClockInOutRequest;
import com.aceroute.mobile.software.requests.EditContactReq;
import com.aceroute.mobile.software.requests.EditSiteReq;
import com.aceroute.mobile.software.requests.OrderMessage;
import com.aceroute.mobile.software.requests.PubnubRequest;
import com.aceroute.mobile.software.requests.SaveFormListRequest;
import com.aceroute.mobile.software.requests.SaveFormRequest;
import com.aceroute.mobile.software.requests.SaveMediaRequest;
import com.aceroute.mobile.software.requests.SaveNewOrder;
import com.aceroute.mobile.software.requests.SavePartDataRequest;
import com.aceroute.mobile.software.requests.SaveShiftListRequest;
import com.aceroute.mobile.software.requests.SaveShiftReq;
import com.aceroute.mobile.software.requests.SaveSiteRequest;
import com.aceroute.mobile.software.requests.SaveTaskDataRequest;
import com.aceroute.mobile.software.requests.Save_DeletePartRequest;
import com.aceroute.mobile.software.requests.Save_DeleteTaskRequest;
import com.aceroute.mobile.software.requests.updateOrderRequest;
import com.aceroute.mobile.software.utilities.JSONHandler;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.aceroute.mobile.software.utilities.XMLHandler;

import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBEngine {

    public final static int DB_ERROR = -1;
    public final static String DATA_SOURCE_LOCAL = "localonly";
    public final static String DATA_SOURCE_SERVER = "serveronly";

    public static boolean emptyDatabase(Context context) {
        try {
            // DBHandler.getDataBase(context);
            String tables[] = DBHandler.getAllTables();
            for (String table : tables) {
                DBHandler.deleteTable(table);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int deleteData(Context context, Date start_date, Date end_date) {
        // DBHandler.getDataBase(context);
        return DBHandler.deleteExpiredData(context, start_date, end_date);
    }

    public static String  saveGeoOnServer(Context context, String geoString, String address,String ShouldSyncToServer) throws IOException {

        String geolist = DBHandler.getValueFromTable(context, DBHandler.resgeoTable, "geo", null);
        String tstmplist = DBHandler.getValueFromTable(context, DBHandler.resgeoTable, "timestamp", null);
        String lstoid = DBHandler.getValueFromTable(context, DBHandler.resgeoTable, "lstoid", null);
        String nxoid = DBHandler.getValueFromTable(context, DBHandler.resgeoTable, "nxtoid", null);

        if (geolist != null && geolist.length() > 0) {
            geolist = geolist + "|" + geoString;
            tstmplist = tstmplist + "|" + new Date().getTime();
        } else {
            geolist = geoString;
            tstmplist = "" + new Date().getTime();
        }
        ContentValues cv = new ContentValues();
        cv.put("geo", geolist);
        cv.put("timestamp", tstmplist);
        cv.put("lstoid",PreferenceHandler.getPrefLstoid(context));
        cv.put("nxtoid",PreferenceHandler.getPrefNxtoid(context));

        //Logger.i("AceRoute Geo" , "saving current Geo as : "+ geolist +" and ShouldSyncToServer as : "+ShouldSyncToServer);
        long result = DBHandler.updateTable(DBHandler.resgeoTable, cv, null, null);
        if (result < 1)
            result = DBHandler.insertTable(DBHandler.resgeoTable, null, cv);

        if(ShouldSyncToServer.equals("true") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) //YD 2020 if android version is >= oreo than check for sync flag
            startBgSync(context);
        else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O )//YD 2020 if android version is < oreo than always run
            startBgSync(context);

        return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
    }

    private static void startBgSync(Context context) {
        //Logger.i("AceRoute Geo" , "Starting BG Sync.");
        Bundle bundle = new Bundle();
        Intent bootIntent = new Intent(context,
                AceRouteService.class);
        bundle.putInt(AceRouteService.KEY_ID,
                Utilities.DEFAULT_START_BG_SYNC);
        bundle.putString(AceRouteService.KEY_ACTION, Api.API_ACTION_SYNCBGDATA);
        //bundle.putString(AceRouteService.KEY_MESSAGE, "{\"action\":\"" +Api.API_ACTION_SYNCBGDATA+"\"}");YD
        bundle.putLong(AceRouteService.KEY_TIME, new Date().getTime());
        bundle.putInt(AceRouteService.KEY_SYNCALL_FLAG, 1);
        bootIntent.putExtras(bundle);
        Utilities.log(context.getApplicationContext(), "Starting BG Sync.");
       // context.startService(bootIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(bootIntent);
        } else {
            context.startService(bootIntent);
        }
    }

    public static synchronized HashMap<Long, Object> getOrders(Context context, String orderid) {
        return getOrders(context, orderid, DBHandler.QUERY_FOR_ORIG);
    }

    public static synchronized HashMap<Long, Object> getOrders(Context context, String orderid, int queryType) {
        // DBHandler.getDataBase(context);
        return DBHandler.checkAndGetOrders(context, Order.ACTION_ORDER,
                orderid, queryType);
    }

    public static synchronized Object[] getOrdersForOffline(String timezone, long time, String from, String to, Context context, StringBuilder st) throws IOException {
        Map<String, String> getOrderParams = new HashMap<String, String>();
        Object[] resultOrders = null;
        getOrderParams.put("tstmp", String.valueOf(time));
        getOrderParams.put("tz", timezone);
        getOrderParams.put("from", from);
        getOrderParams.put("to", to);
        getOrderParams.put("action", Order.ACTION_ORDER);
        String response = HttpConnection.get(context,
                "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", getOrderParams);
        Utilities.log(context, "getOrder xml from server is : \n"
                + response);
        Object resultorders[] = null;

        if (response != null) {
            st.append(response);
            if (!Utilities.IsResponseError(response)) {
                DBHandler.deleteTable(DBHandler.orderListTable);
                resultorders = DBHandler
                        .saveOrdersForoffline(context, response, DBHandler.QUERY_FOR_ORIG);
            }
        }

        return resultorders;

    }

    public static synchronized HashMap<Long, Object> getOrdersfromOfflineInMap(Context context) {
        try {
            HashMap<Long, Object> xml = DBHandler.checkAndGetOrdersfromOfflineInMap(context, Order.ACTION_ORDER);

            Utilities.log(context,
                    "getOrdersfromOffline xml received" + xml);
            return xml;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized HashMap<Long, Object> getOrders(String timezone, Date fDate, String from, String to, long time, boolean isFirstTime, Context context) {
        try {
            String tmpFrom, tmpTo;
            tmpFrom = from;
            tmpTo = to;
            HashMap<Long, Object> xml = null;//yash
            int queryType = 0;
            boolean isInTimeBoundary = Utilities.isValidDate(fDate);
            Log.i(Utilities.TAG, "In DBEngine getOrders fDate:"
                    + fDate);
            if (isInTimeBoundary) {
                Log.i(Utilities.TAG,
                        "In timeboundary in getorders:" + fDate);
                Utilities.log(context, "Getting order list_cal from the database.");
                // DBHandler.getDataBase(context);
                xml = DBHandler.checkAndGetOrders(context, Order.ACTION_ORDER,
                        from, to, DBHandler.QUERY_FOR_ORIG); // timezone
                // parameter has
                // been removed
                // from
                // checkandgetorders, since it is not used
                // into this method.
                queryType = DBHandler.QUERY_FOR_ORIG;
                Utilities.log(context, "Received xml from database is :" + xml);
            } else {
                Log.i(Utilities.TAG,
                        "In timeboundary getting orders from temp:" + fDate);
                Calendar calendar = Utilities.getCalendarInstance(context);
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                calendar.setTime(fDate);
                String tempFrom = formatter.format(calendar.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                String tempTo = formatter.format(calendar.getTime());
                // DBHandler.getDataBase(context);
                queryType = DBHandler.QUERY_FOR_TEMP;
                xml = DBHandler.checkAndGetOrders(context, Order.ACTION_ORDER,
                        tempFrom, tempTo, DBHandler.QUERY_FOR_TEMP);
            }

            if (xml == null) {
                if (isFirstTime || !Utilities.isValidDate(fDate)) {
                    Map<String, String> getOrderParams = new HashMap<String, String>();
                    if (isFirstTime) {
                        Utilities.log(context, "first time is true.");
                    } else {
                        Utilities
                                .log(context,
                                        fDate
                                                + " is not between current range, now fetching from the server.");
                    }
                    if (time > 0) {
                        getOrderParams.put("tstmp", String.valueOf(time));
                    }
                    // making dates from fdate because the passed dates are
                    // according to local system.
                    Calendar calendar = Utilities.getCalendarInstance(context);
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                    if (!isFirstTime) {
                        try {
                            calendar.setTime(fDate);
                            from = formatter.format(calendar.getTime());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            calendar.add(Calendar.DAY_OF_MONTH, 1);
                            to = formatter.format(calendar.getTime());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    getOrderParams.put("tz", timezone);
                    getOrderParams.put("from", from);
                    getOrderParams.put("to", to);
                    getOrderParams.put("action", Order.ACTION_ORDER);
                    // changed get to post 08-04-14
                    String response = HttpConnection.get(context,
                            "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", getOrderParams);
                    Utilities.log(context, "getOrder xml from server is : \n"
                            + response);
                    if (response != null) {
                        long cur_res_id = PreferenceHandler.getResId(context);
                        String refinedXml = new XMLHandler(context)
                                .searchResId(response, cur_res_id);
                        if (refinedXml != null) {
                            // if (isInTimeBoundary) {
                            Log.i(Utilities.TAG,
                                    "in time boundary saving orders");
                            // DBHandler.getDataBase(context);

                            int result = DBHandler
                                    .saveOrders(context, refinedXml, queryType);
                            if (result == DBHandler.recordsCannotInserted_NoData) {
                                //return XMLHandler.XML_EMPTY_DATA;//laterYD
                            } else if (result == DBHandler.recordsCannotInserted_NoData) {
                                //return XMLHandler.XML_EMPTY_DATA;laterYD
                            }
                            // } else {
                            // Log.i( Utilities.TAG,
                            // "Not in timeboudary just returning data from getorders");
                            // }
                            HashMap<Long, Object> ordersList = DBHandler.checkAndGetOrders(context,
                                    Order.ACTION_ORDER, tmpFrom, tmpTo,
                                    queryType);
                            return ordersList;
                        } else {
                            return null;
                            //return XMLHandler.XML_EMPTY_DATA;//laterYD
                        }
                        // Log.e( Utilities.TAG,
                        // "Worker id is not matching.");
                        // return null;
                    }
                } else {
                    Log.e(Utilities.TAG,
                            "Returning no data because date is in boudary and no data in local db.");
                    return null;
                    //return XMLHandler.XML_NO_DATA;laterYD
                }
            } else {
                return xml;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<Long, Object> getOrderNotes(Context context, long id, String action, int queryType) throws IOException {
        try {
            Log.i(Utilities.TAG, "Getting order media : " + id);
            // DBHandler.getDataBase(context);
            HashMap<Long, Object> xml = DBHandler.getOrderNotes(context, id,
                    OrderNotes.ACTION_GET_NOTES, queryType);
            return xml;
        } catch (Exception e) {
            e.printStackTrace();
            //return XMLHandler.XML_NO_DATA;//laterYD
        }
        return null;
    }

    public static Response insertData(Context context, Object reqDataObj, int objecttype, int queryType) {
        Utilities.log(context, "DBEngine insertData called. objecttype : "
                + objecttype);
        Response response = null;
        // DBHandler.getDataBase(context);
        try {
            switch (objecttype) {
			/*case ClientLocation.TYPE:
				DBHandler.saveClientLocation(context, xmlString);
				return xmlString;*///laterYD
                case Customer.TYPE: // YD now not using any more because now directly getting new customer list_cal from server
                    PubnubRequest savCustReq = (PubnubRequest) reqDataObj;
                    int resultCust = DBHandler.saveCustomer(context, savCustReq.getXml());
                    if (resultCust > 0) {
                        response = new Response();
                        response.setResponseMap(null);
                        response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        response.setStatus("success");

                        return response;
                    } else
                        return null;
                case CustomerContact.TYPE:// YD using for pubnub
                    PubnubRequest savCustContReq = (PubnubRequest) reqDataObj;
                    int resultCustCont = DBHandler.saveCustomerContactFromXml(context, savCustContReq.getXml(), OrderTask.NEW_ORDER_TASK, -2);//YD -2 is used just to update the order with new task count
                    if (resultCustCont > 0) {
                        response = new Response();
                        response.setResponseMap(null);
                        response.setResString(resultCustCont + "");
                        response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        response.setStatus("success");
                        return response;
                    } else
                        return null;

                case ServiceType.TYPE_UPDATE:
                    PubnubRequest savOdrTaskReq = (PubnubRequest) reqDataObj;
                    int resultOdrTask = DBHandler.saveDownloadedOrderForm(context, savOdrTaskReq.getXml(), OrderTask.NEW_ORDER_TASK, -2, 2);//YD -2 is used just to update the order with new task count
                    if (resultOdrTask > 0) {
                        response = new Response();
                        response.setResponseMap(null);
                        response.setResString(resultOdrTask + "");
                        response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        response.setStatus("success");
                        return response;
                    } else
                        return null;

                case Assets.TYPE_PUBNUB:
                    PubnubRequest savAssetReq = (PubnubRequest) reqDataObj;
                    int resultAsset = DBHandler.saveDownloadedOrderAssets(context, savAssetReq.getXml());//YD -2 is used just to update the order with new task count
                    if (resultAsset > 0) {
                        response = new Response();
                        response.setResponseMap(null);
                        response.setResString(resultAsset + "");
                        response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        response.setStatus("success");
                        return response;
                    } else
                        return null;

                case AssetsType.TYPE_PUBNUB_ASSET_TYPE:
                    PubnubRequest savAssetTypeReq = (PubnubRequest) reqDataObj;
                    int resultAssetType = DBHandler.saveDownloadedOrderAssetsType(context, savAssetTypeReq.getXml());//YD -2 is used just to update the order with new task count
                    if (resultAssetType > 0) {
                        response = new Response();
                        response.setResponseMap(null);
                        response.setResString(resultAssetType + "");
                        response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        response.setStatus("success");
                        return response;
                    } else
                        return null;

                case Parts.TYPE_UPDATE:
                    PubnubRequest savOdrPartReq = (PubnubRequest) reqDataObj;
                    int resultOdrPart = DBHandler.saveDownloadedOrderPart(context, savOdrPartReq.getXml(), OrderPart.NEW_ORDER_PART, -2, 2);
                    if (resultOdrPart > 0) {
                        response = new Response();
                        response.setResponseMap(null);
                        response.setResString(resultOdrPart + "");
                        response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        response.setStatus("success");
                        return response;
                    } else
                        return null;

                case Order.TYPE:  // YD used for pubnub add
                    PubnubRequest savOdrReq = (PubnubRequest) reqDataObj;
                    int result = DBHandler.saveOrders(context, savOdrReq.getXml(),
                            queryType);
                    if (result > 0) {
                        response = new Response();
                        response.setResponseMap(null);
                        response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        response.setStatus("success");
                        return response;
                    } else
                        return null;
                case Site.TYPE:
                    PubnubRequest saveSiteReq = (PubnubRequest) reqDataObj;
                    int SavSiteresult = DBHandler.saveSite(context, saveSiteReq.getXml());
                    if (SavSiteresult > 0) {
                        response = new Response();
                        response.setResponseMap(null);
                        response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        response.setStatus("success");
                        return response;
                    } else
                        return null;

                case Order.TYPE_UPDATE:
                    try {
					/*Utilities.log(context,
							"Inside insertData, objecttype is saveorder"
									+ xmlString);*/
                        SaveNewOrder req = (SaveNewOrder) reqDataObj;
                        String id = req.getId();


                        response = DBHandler.saveOrderforOffline(context, req);
                        if (response.getSyncToServer() == true)
                            syncDataToSever(context, Order.TYPE_UPDATE);
                        return response;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return response;
                    }

                case OrderTypeList.TYPE:
                    PubnubRequest savOdrTypeReq = (PubnubRequest) reqDataObj;
                    int resultOdrTyp = DBHandler.saveOrderType(context, savOdrTypeReq.getXml());
                    if (resultOdrTyp > 0) {
                        response = new Response();
                        response.setResponseMap(null);
                        response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        response.setStatus("success");
                        return response;
                    } else
                        return null;
                    //return xmlString;

                case Parts.TYPE:
                    PubnubRequest savPartsReq = (PubnubRequest) reqDataObj;
                    int resultparts = DBHandler.savePartType(context, savPartsReq.getXml());
                    if (resultparts > 0) {
                        response = new Response();
                        response.setResponseMap(null);
                        response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        response.setStatus("success");
                        return response;
                    } else
                        return null;
                    //return xmlString;
                case ServiceType.TYPE:
					/*PubnubRequest savServiceTypeReq = (PubnubRequest) reqDataObj;
					int resultSerTyp = DBHandler.saveServiceType(context, savServiceTypeReq.getXml());
					if (resultSerTyp > 0) {
						response = new Response();
						response.setResponseMap(null);
						response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
						response.setStatus("success");
						return response;
					} else
						return null;*/
                    //return xmlString;
                case Worker.TYPE:
                    PubnubRequest savWorkerReq = (PubnubRequest) reqDataObj;
                    int resultWorkerTyp = DBHandler.saveWorker(context, savWorkerReq.getXml());
                    if (resultWorkerTyp > 0) {
                        response = new Response();
                        response.setResponseMap(null);
                        response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        response.setStatus("success");
                        return response;
                    } else
                        return null;
			/*case CustomerType.TYPE:
				DBHandler.saveCusType(context, xmlString);
				return xmlString;*///YD later
                case OrderTask.TYPE:
                    // we have jsonstring instead of xml in xmlString argument
                    try {
                        Utilities.log(context,
                                "Inside insertData, objecttype is saveOrderTask");

                        SaveTaskDataRequest innerReqDataObj = ((Save_DeleteTaskRequest) reqDataObj).getDataObj();

                        String id = innerReqDataObj.getTaskId();
                        Long oid = Long.parseLong(innerReqDataObj.getOrderId());
							/*Long.parseLong(dataObject.get(
							OrderTask.ORDER_ID).toString());*/
                        response = DBHandler.saveOrderTaskforOffline(context,
                                innerReqDataObj, id, oid);
                        if (response.getSyncToServer() == true)
                            syncDataToSever(context, OrderTask.TYPE);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return response;
                    }
                    break;

				/*case OrderTaskOld.TYPE:   /// by mandeep for ordertaskold
					try {
						Utilities.log(context, "Inside insertData, objecttype is saveOrderTaskOld");

						SaveTaskOldDataRequest innerReqDataObject = ((Save_DeleteTaskOldRequest) reqDataObj).getDataObj();

						String id = innerReqDataObject.getTaskId();
						Long orderId = Long.parseLong(innerReqDataObject.getOrderId());
						response = DBHandler.saveOrderTaskOldforOffline(context, innerReqDataObject, id, orderId);
					} catch (Exception e) {
						e.printStackTrace();
						return response;
					}
					break;*/

                case MessagePanic.TYPE:
                    try {
                        Utilities.log(context,
                                "Inside insertData, objecttype is saveOrderTask");

                        HashMap<String, String> dataReq = ((RequestObject) reqDataObj).getRequestMap();
                        response = DBHandler.saveMessageforOffline(context,
                                dataReq);
                        if (response.getSyncToServer() == true)
                            syncDataToSever(context, MessagePanic.TYPE);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return response;
                    }
                    break;
                case OrderPart.TYPE:
                    // we have jsonstring instead of xml in xmlString argument

                    try {
                        Utilities.log(context,
                                "Inside insertData, objecttype is saveOrderTask");
					/*String jsonString = xmlString;
					JSONObject jsonObject = JSONHandler
							.getJsonObject(jsonString);
					String dataString = jsonObject.get(
							JSONHandler.KEY_REQUEST_DATA).toString();
					JSONObject dataObject = JSONHandler
							.getJsonObject(dataString);*/

                        SavePartDataRequest dataReq = ((Save_DeletePartRequest) reqDataObj).getDataObj();
                        String id = ((Save_DeletePartRequest) reqDataObj).getDataObj().getPartId();/*dataObject.get(
							OrderPart.ORDER_PART_ID).toString()*/
                        ;
                        long oid = Long.parseLong(((Save_DeletePartRequest) reqDataObj).getDataObj().getOid());/*Long.parseLong(dataObject.get(OrderPart.ORDER_ID).toString());*/
                        response = DBHandler.saveOrderPartforOffline(context,
                                dataReq, id, oid);
                        if (response.getSyncToServer() == true)
                            syncDataToSever(context, OrderPart.TYPE);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return response;
                    }
                    break;

                case Assets.TYPE:
                    // we have jsonstring instead of xml in xmlString argument

                    try {
                        Utilities.log(context,
                                "Inside insertData, objecttype is saveOrderTask");

                        ArrayList<SaveFormRequest> dataReq = ((SaveFormListRequest) reqDataObj).getDataObj();
                        String id = ((SaveFormListRequest) reqDataObj).getDataObj().get(0).getId();
                        long oid = Long.parseLong(((SaveFormListRequest) reqDataObj).getDataObj().get(0).getOid());
                        response = DBHandler.saveOrderAssetforOffline(context,  //saveFormforOffline
                                dataReq, id, oid);
                        if (response.getSyncToServer() == true)
                            syncDataToSever(context, Assets.TYPE);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return response;
                    }
                    break;

                case OrderMedia.TYPE_SAVE:
                    try {

                        SaveMediaRequest reqdata = (SaveMediaRequest) reqDataObj;

                        String req_type = reqdata.getType();

                        long oid = Long.parseLong(reqdata.getOrderId());
                        String id = reqdata.getId();
                        String frmkey = reqdata.getFrmkey();
                        response = DBHandler.saveOrderMediaforOffline(context, reqdata, id, oid,frmkey);
					/*if (response.getSyncToServer()== true)  //YD not doing here because media takes time and when upload media the meta path are empty in response (require download from  server again with new meta id )
						syncDataToSever(context,OrderMedia.TYPE_SAVE);*/
                    } catch (Exception e) {
                        e.printStackTrace();
                        return response;
                    }
                    break;


                case OrdersMessage.TYPE_SAVE:
                    OrderMessage req = (OrderMessage) reqDataObj;

                    response = DBHandler.saveOrderMessageForOffline(context,
                            req);
                    if (response.getSyncToServer() == true)
                        syncDataToSever(context, OrdersMessage.TYPE_SAVE);
                    return response;

                case CustomerContact.TYPE_EDIT:
                    EditContactReq edtCntReq = (EditContactReq) reqDataObj;

                    response = DBHandler.saveOrderContactForOffline(context,
                            edtCntReq);
                    if (response.getSyncToServer() == true)
                        syncDataToSever(context, CustomerContact.TYPE_EDIT);
                    return response;

                case SiteType.TYPE:
                    PubnubRequest savSiteTypeReq = (PubnubRequest) reqDataObj;//YD pubnub gentype
                    int resultSiteType = DBHandler.
                            saveSiteType(context, savSiteTypeReq.getXml());
                    if (resultSiteType > 0) {
                        response = new Response();
                        response.setResponseMap(null);
                        response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        response.setStatus("success");
                        return response;
                    } else
                        return null;

                case Shifts.TYPE:
                    try {
                        Utilities.log(context,
                                "Inside insertData, objecttype is saveShiftTask");

                        ArrayList<SaveShiftReq> dataReq = ((SaveShiftListRequest) reqDataObj).getDataObj();
                        response = DBHandler.saveShiftforOffline(context, dataReq);
                        if (response.getSyncToServer() == true)
                            syncDataToSever(context, Shifts.TYPE);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return response;
                    }
                    break;
                case Form.TYPE:
                    try {
                        Utilities.log(context,
                                "Inside insertData, objecttype is saveShiftTask");

                        ArrayList<SaveFormRequest> dataReq = ((SaveFormListRequest) reqDataObj).getDataObj();
                        String id = ((SaveFormListRequest) reqDataObj).getDataObj().get(0).getId();
                        long oid = Long.parseLong(((SaveFormListRequest) reqDataObj).getDataObj().get(0).getOid());
                        response = DBHandler.saveFormforOffline(context, dataReq, id, oid);
                        if (response.getSyncToServer() == true)
                            syncDataToSever(context, Form.TYPE);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return response;
                    }
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public static int deleteData(Context context, String tableName, String whereClause, int queryType) {
        // DBHandler.getDataBase(context);
        return DBHandler.delete(context, tableName, whereClause, queryType);
    }

    //YD Function to delete part , task etc
    public static String deleteData(Context context, String id, int objecttype, int queryType) {
        Utilities.log(context, "DBEngine deleteData called. objecttype : "
                + objecttype);
        //String response = XMLHandler.XML_NO_DATA;
        String response = null;
        // DBHandler.getDataBase(context);
        try {
            switch (objecttype) {
			
			/*case ClientLocation.TYPE:   //YD later
				return ""
						+ DBHandler.delete(context, DBHandler.clientSiteTable,
								ClientLocation.CLIENT_ID, id);*/// YD later
                case ServiceType.TYPE_UPDATE: //YD pubnub delete orderTask
                    return ""
                            + DBHandler.delete(context, DBHandler.orderTaskTable,
                            OrderTask.ORDER_TASK_ID, id, queryType);

                case Parts.TYPE_UPDATE:  //YD pubnub delete orderpart
                    return ""
                            + DBHandler.delete(context, DBHandler.orderPartTable,
                            Order.ORDER_ID, id, queryType);

                case CustomerContact.TYPE:
                    return ""
                            + DBHandler.delete(context, DBHandler.customercontactTable,
                            CustomerContact.CONTACT_ID, id, queryType);

                case Assets.TYPE_PUBNUB:
                    return ""
                            + DBHandler.delete(context, DBHandler.assestTable,
                            Assets.ASSET_ID, id, queryType);
                case AssetsType.TYPE_PUBNUB_ASSET_TYPE:
                    return ""
                            + DBHandler.delete(context, DBHandler.assestTypeTable,
                            AssetsType.ASSETS_TYPE_ID, id, queryType);

                case Customer.TYPE:
                    // deleting orders containing deleted customer.
                    Log.w(Utilities.TAG,
                            "Deleting orders containing customer id : " + id);
                    deleteData(context, DBHandler.orderListTable,
                            Order.ORDER_CUSTOMER_ID + "=" + id,
                            DBHandler.QUERY_FOR_ORIG);
				/*deleteData(context, DBHandler.orderListTable,
						Order.ORDER_CUSTOMER_ID + "=" + id,
						DBHandler.QUERY_FOR_TEMP);*/
                    int deleteResult = DBHandler.delete(context,
                            DBHandler.customerListTable, Customer.CUSTOMER_ID, id);
                    DBHandler.delete(context, DBHandler.orderListTable,
                            Order.ORDER_CUSTOMER_ID, id);
                    return String.valueOf(deleteResult);
                case Order.TYPE:
                    return ""
                            + DBHandler.delete(context, DBHandler.orderListTable,
                            Order.ORDER_ID, id, queryType);
                case Site.TYPE:
                    int delCountsite = DBHandler.delete(context, DBHandler.siteTable,
                            Site.SITE_ID, id);
                    if (delCountsite > 0) {
                        syncDataToSever(context, Site.TYPE);
                        return "" + delCountsite;
                    }
                case OrderTypeList.TYPE:
                    return ""
                            + DBHandler.delete(context,
                            DBHandler.orderTypeListTable,
                            OrderTypeList.ORDER_TYPE_ID, id);
			/*case OrderNotes.TYPE_DELETE:
				return ""
						+ DBHandler.delete(context, DBHandler.orderFileNotes,
								OrderNotes.ORDER_ID, id);*/// YD later
                case Parts.TYPE:
                    return ""
                            + DBHandler.delete(context,
                            DBHandler.partTypeListTable,
                            Parts.PART_TYPE_ID, id);
                case SiteType.TYPE:
                    return ""
                            + DBHandler.delete(context,
                            DBHandler.siteTypeTable,
                            SiteType.SITE_TYPE_ID, id);
				/*case ServiceType.TYPE:
					return ""
							+ DBHandler.delete(context, DBHandler.taskTypeTable,
							ServiceType.SERVICE_TYPE_ID, id);*/
			/*case CustomerType.TYPE:
				return ""
						+ DBHandler.delete(context,
								DBHandler.customerTypeTable,
								CustomerType.CUSTOMER_TYPE_ID, id);*/
                case Worker.TYPE:
                    return ""
                            + DBHandler.delete(context, DBHandler.resTable,
                            Worker.WORKER_ID, id);
                case OrderTask.TYPE:
                    int delCount = DBHandler.delete(context,
                            DBHandler.orderTaskTable,
                            OrderTask.ORDER_TASK_ID, id);
                    if (delCount > 0) {
                        syncDataToSever(context, OrderTask.TYPE);
                        return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
                    }
                    return null;

                case Form.TYPE:
                    int delCountForm = DBHandler.delete(context,
                            DBHandler.formTable,
                            Form.FORM_ID, id);
                    if (delCountForm > 0) {
                        syncDataToSever(context, Form.TYPE);
                        return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
                    }
                    return null;
                //return XMLHandler.XML_DATA_ERROR;

                case OrderPart.TYPE:

                    int delcount = DBHandler.delete(context, DBHandler.orderPartTable,
                            OrderPart.ORDER_PART_ID, id);
                    if (delcount > 0) {
                        syncDataToSever(context, OrderPart.TYPE);
                        return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
                    }
                    return null;
                //return XMLHandler.XML_DATA_ERROR;  YD
                case Shifts.TYPE:

                    int delshift = DBHandler.delete(context, DBHandler.shiftTable,
                            Shifts.SHIFT_ID, id);
                    if (delshift > 0) {
                        return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
                    }
                    return null;
                //return XMLHandler.XML_DATA_ERROR;  YD


                case OrderMedia.TYPE_DELETE:

                    String delid[];
                    if (id.contains("|")) {
                        String tempid = id.replace("|", ",");
                        delid = tempid.split(",");
                    } else {
                        String tempid[] = {id};
                        delid = tempid;
                    }
                    for (int j = 0; j < delid.length; j++) {
                        Log.i(Utilities.TAG,
                                "Deletion for id : " + delid[j]);
                        int mediadelcount = DBHandler.delete(context,
                                DBHandler.orderFileMeta, OrderMedia.ID,
                                delid[j]);
                        if (mediadelcount > 0)
                            return "" + mediadelcount;
                        //return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
                        return null;

                    }
                    return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String updateData(Context context, Object requestObj, int objecttype, int queryType) {               //YD

        Utilities.log(context, "DBEngine updateData called. objecttype : "
                + objecttype);
        String response = null;
        // DBHandler.getDataBase(context);
        try {
            int result = 0;
            switch (objecttype) {
			/*case ClientLocation.TYPE:
				result = DBHandler.updateClientLocation(context, xmlString);
				if (result > 0)
					return xmlString;
				else
					return null;*///later YD
                case ServiceType.TYPE_UPDATE:
                    PubnubRequest updateOdrTaskReq = (PubnubRequest) requestObj;
                    result = DBHandler.updateOrderTask(context, updateOdrTaskReq);
                    if (result > 0)
                        return "" + result;
                    else
                        return null;

                case Parts.TYPE_UPDATE:
                    PubnubRequest updateOdrPartReq = (PubnubRequest) requestObj;
                    result = DBHandler.updateOrderPart(context, updateOdrPartReq);
                    if (result > 0)
                        return "" + result;
                    else
                        return null;

                case CustomerContact.TYPE:
                    PubnubRequest updateCustCont = (PubnubRequest) requestObj;
                    result = DBHandler.updateOrderPart(context, updateCustCont);
                    if (result > 0)
                        return "" + result;
                    else
                        return null;

                case Assets.TYPE_PUBNUB:
                    PubnubRequest updateAsset = (PubnubRequest) requestObj;
                    result = DBHandler.updateAsset(context, updateAsset);
                    if (result > 0)
                        return "" + result;
                    else
                        return null;

                case AssetsType.TYPE_PUBNUB_ASSET_TYPE:
                    PubnubRequest updateAssetType = (PubnubRequest) requestObj;
                    result = DBHandler.updateAsset(context, updateAssetType);
                    if (result > 0)
                        return "" + result;
                    else
                        return null;

				/*case CustomerContact.TYPE:// YD using for pubnub
					PubnubRequest  savCustContReq = (PubnubRequest)reqDataObj;
					int resultCustCont = DBHandler.saveCustomerContactFromXml(context, savCustContReq.getXml(), OrderTask.NEW_ORDER_TASK, -2);//YD -2 is used just to update the order with new task count
					if (resultCustCont > 0){
						response = new Response();
						response.setResponseMap(null);
						response.setResString(resultCustCont + "");
						response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
						response.setStatus("success");
						return response;
					}
					else
						return null;*/

                case Customer.TYPE:
                    PubnubRequest updateCustReq = (PubnubRequest) requestObj;
                    result = DBHandler.updateCustomer(context, updateCustReq);
                    if (result > 0)
                        return "" + result;
                    else
                        return null;
                case Order.TYPE:
                    result = DBHandler.updateOrders(context, requestObj, null, 2, "update");
                    if (result > 0)
                        return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
                    else
                        return null;
                case Site.TYPE:// YD site update pubnub
                    PubnubRequest updateSiteReq = (PubnubRequest) requestObj;
                    result = DBHandler.updateSite(context, updateSiteReq.getXml(), null);
                    if (result > 0)
                        return "" + result;
                    else
                        return null;
                case OrderTypeList.TYPE:
                    PubnubRequest updateOdrTypeReq = (PubnubRequest) requestObj;
                    result = DBHandler.updateOrderType(context, updateOdrTypeReq);
                    if (result > 0)
                        return "" + result;
                    else
                        return null;
                case Parts.TYPE: //YD pubnub partType (check also)
                    PubnubRequest updatePartsReq = (PubnubRequest) requestObj;
                    result = DBHandler.updatePartType(context, updatePartsReq);
                    if (result > 0)
                        return "" + result;
                    else
                        return null;

                case SiteType.TYPE:
                    PubnubRequest updateSiteTypeReq = (PubnubRequest) requestObj;
                    result = DBHandler.updateSiteType(context, updateSiteTypeReq);
                    if (result > 0)
                        return "" + result;
                    else
                        return null;
				
			/*case CustomerType.TYPE:
				result = DBHandler.updateCusType(context, xmlString);
				if (result > 0)
					return xmlString;
				else
					return null;*///later YD
				/*case ServiceType.TYPE:
					PubnubRequest updateServiceTypeReq = (PubnubRequest) requestObj;
					result = DBHandler.updateServiceType(context, updateServiceTypeReq);
					if (result > 0)
						return "" + result;
					else
						return null;*/
                case Worker.TYPE:
                    PubnubRequest updateWorkerReq = (PubnubRequest) requestObj;
                    result = DBHandler.updateWorker(context, updateWorkerReq);
                    if (result > 0)
                        return "" + result;
                    else
                        return null;
                case Shifts.TYPE:
                    //PubnubRequest  updateShiftReq = (PubnubRequest)requestObj;
                    result = DBHandler.updateShift(context, requestObj);
                    if (result > 0)
                        return "" + result;
                    else
                        return null;

                case Order.TYPE_UPDATE:
                    try {
                        // we have jsonstring instead of xml in xmlString argument
                        updateOrderRequest reqObj = (updateOrderRequest) requestObj;
                        String req_type = reqObj.getType();
                        String req_url = reqObj.getUrl();
                        String name = reqObj.getName();

                        String id = reqObj.getId();
                        String tempFieldValues = reqObj.getValue();

                        String fieldvalue[] = null;
                        queryType = DBHandler.QUERY_FOR_ORIG;
                        String whereClause = Order.ORDER_ID + "= " + id + " OR " + DBHandler.localidField + "=" + id;
                        String modifystr = DBHandler.getValueFromTable(context, DBHandler.orderListTable, DBHandler.modifiedField, whereClause);
                        int operationcount = 0;
                        if (modifystr != null && modifystr.equals(""))
                            modifystr = DBHandler.updated;
                        if (name.equals(Order.ORDER_NOTES)) {
                            OrderNotes onote = new OrderNotes();
                            onote.setId(Long.parseLong(id));
                            onote.setOrdernote(reqObj.getValue());
                            Log.d("ordernote",""+reqObj.getValue());
                            onote.setModified(DBHandler.updated);
                            OrderNotes[] ordernotes = {onote};
                            operationcount = DBHandler.updateRecord(context,
                                    DBHandler.orderFileNotes,
                                    ordernotes, queryType);
                            if (operationcount <= 0)// insert note if update failed.
                            {
                                operationcount = DBHandler.insertRecord(context,
                                        DBHandler.orderFileNotes,
                                        ordernotes, queryType);
                            }
                            ContentValues cv = new ContentValues();
                            String length = "0";
                            if (onote.getOrdernote().length() > 0)
                                length = "1";

                            cv.put(Order.ORDER_NOTE, length);

                            cv.put(Order.ORDER_NOTES, reqObj.getValue());

                            whereClause = Order.ORDER_ID + "=" + onote.getId();
                            
                            result = DBHandler.updateTable(DBHandler.orderListTable, cv, whereClause, null);
                            if (operationcount > 0)

                                syncDataToSever(context, OrderNotes.TYPE_SAVE);
                            DBEngine.syncDataToSever(context, Order.TYPE_UPDATE);

                        }

                        else if (name.equals("alt")) {
                            Order order = new Order();
                            order.setId(Long.valueOf(id));
                            order.setOrderAlert(tempFieldValues);
                            order.setModified(modifystr);

                            Object[] object = {order};
                            operationcount = DBHandler.updateRecord(context,
                                    DBHandler.orderListTable,
                                    object, queryType);
                            DBEngine.syncDataToSever(context, Order.TYPE_UPDATE);
                        }
                        else if (name.equals("assetcount")) {
                            Order order = new Order();
                            order.setId(Long.valueOf(id));
                            order.setAssetsCount(Long.parseLong(tempFieldValues));
                            //order.setModified(modifystr);

                            Object[] object = {order};
                            operationcount = DBHandler.updateRecord(context,
                                    DBHandler.orderListTable,
                                    object, queryType);
                            //DBEngine.syncDataToSever(context, Order.TYPE_UPDATE); //YD no need to update because saving asset count for using locally
                        }
                        else if (name.equals("accessPathGeo")) {
                            Order order = new Order();
                            order.setId(Long.valueOf(id));
                            order.setCustSiteGeocode(tempFieldValues);

                            Object[] object = {order};
                            operationcount = DBHandler.updateRecord(context,
                                    DBHandler.orderListTable,
                                    object, 3);
                        }
                        else if (name.equals("priority_AND_restriction")) {
                            Order order = new Order();
                            fieldvalue = tempFieldValues.split("\\|");
                            order.setId(Long.valueOf(id));
                            for (int j = 0; j < 2; j++) {
                                switch (j) {
                                    case 0:
                                        order.setPriorityTypeId(Long.valueOf(fieldvalue[j]));
                                        break;
                                    case 1:
                                        order.setSummary(fieldvalue[j]);
                                        break;
                                }
                            }
                            order.setModified(modifystr);
                            Object[] object = {order};
                            operationcount = DBHandler.updateRecord(context,
                                    DBHandler.orderListTable,
                                    object, queryType);
                            DBEngine.syncDataToSever(context, Order.TYPE_UPDATE);
                        }
                        else if (name.contains("|")) {// update status
                            Order order = new Order();
                            fieldvalue = tempFieldValues.split("\\|");
                            order.setId(Long.valueOf(id));
                            int length = fieldvalue.length;
                            if (length == 5) { // RJ:- UPdate dtl.po,inv, alert field from AlertNote
                                for (int a = 1; a<=5; a++) {
                                    switch (a) {
                                        case 1:
                                            try {
                                                order.setOrderAlert(fieldvalue[0]);
                                            } catch (Exception e1) {
                                                order.setOrderAlert("");
                                                e1.printStackTrace();
                                            }
                                            break;
                                        case 2:
                                            try {
                                                order.setSummary(fieldvalue[1]);
                                            } catch (Exception e1) {
                                                order.setSummary("");
                                                e1.printStackTrace();
                                            }
                                            break;
                                        case 3:
                                            try {
                                                order.setPoNumber(fieldvalue[2]);
                                            } catch (Exception e1) {
                                                order.setSummary("");
                                                e1.printStackTrace();
                                            }
                                            break;

                                        case 4:
                                            try {
                                                order.setInvoiceNumber(fieldvalue[3]);
                                            } catch (Exception e1) {
                                                order.setInvoiceNumber("");
                                                e1.printStackTrace();
                                            }
                                            break;

                                        case 5:
                                            try {
                                                order.setOrdeNotes(fieldvalue[4]);
                                            } catch (Exception e1) {
                                                order.setOrdeNotes("");
                                                e1.printStackTrace();
                                            }
                                            break;
                                    }
                                }

                            } else {

                                for (int j = 1; j < 11; j++) {
                                    switch (j) {
									/*case 0:
										try {
											order.setStatusId(Long
													.valueOf(fieldvalue[j]));
										} catch (Exception e5) {
											order.setStatusId(Long
													.valueOf(0));
											e5.printStackTrace();
										}
										break;*/
                                        case 1:
                                            try {
                                                order.setPriorityTypeId(Long
                                                        .valueOf(fieldvalue[0]));
                                            } catch (Exception e4) {
                                                e4.printStackTrace();
                                                order.setPriorityTypeId(Long
                                                        .valueOf(0));
                                            }
                                            break;
                                        case 2:
                                            try {
                                                order.setOrderTypeId(Long
                                                        .valueOf(fieldvalue[1]));
                                            } catch (Exception e3) {
                                                e3.printStackTrace();
                                                order.setOrderTypeId(Long
                                                        .valueOf(0));
                                            }
                                            break;
                                        case 3:
                                            try {
                                                order.setPoNumber(fieldvalue[2]);
                                            } catch (Exception e1) {
                                                order.setPoNumber("");
                                                e1.printStackTrace();
                                            }
                                            break;
                                        case 4:
                                            try {
                                                order.setSummary(fieldvalue[3]);
                                            } catch (Exception e2) {
                                                order.setSummary("");
                                                e2.printStackTrace();
                                            }
                                            break;
                                        case 5:
                                            try {
                                                order.setInvoiceNumber(fieldvalue[4]);
                                            } catch (Exception e2) {
                                                order.setInvoiceNumber("");
                                                e2.printStackTrace();
                                            }
                                            break;
                                        case 6:
                                            try {
                                                String workerLst = fieldvalue[5];
                                                if (fieldvalue[5].contains("##")) {
                                                    workerLst = fieldvalue[5].replaceAll("##", "|");
                                                }
                                                order.setPrimaryWorkerId(workerLst);
                                            } catch (Exception e2) {
                                                order.setPrimaryWorkerId("");
                                                e2.printStackTrace();
                                            }
                                            break;
                                        case 7:
                                            try {
                                                order.setStartTime(Long.valueOf(fieldvalue[6]));
                                                order.setEpochTime(Long.valueOf(fieldvalue[6]));
                                            } catch (Exception e2) {
                                                order.setStartTime(0);
                                                e2.printStackTrace();
                                            }
                                            break;
                                        case 8:
                                            try {
                                                order.setEndTime(Long.valueOf(fieldvalue[7]));
                                            } catch (Exception e2) {
                                                order.setEndTime(0);
                                                e2.printStackTrace();
                                            }
                                            break;
                                        case 9:
                                            try {
                                                order.setFromDate(fieldvalue[8].replace("/", "-"));
                                            } catch (Exception e2) {
                                                order.setFromDate("");
                                                e2.printStackTrace();
                                            }
                                            break;
                                        case 10:
                                            try {
                                                order.setToDate(fieldvalue[9].replace("/", "-"));
                                            } catch (Exception e2) {
                                                order.setToDate("");
                                                e2.printStackTrace();
                                            }
                                            break;
									/*case 6:
										try {
											order.setOrderAlert(fieldvalue[j]);
										} catch (Exception e2) {
											order.setInvoiceNumber("");
											e2.printStackTrace();
										}
										break;	*/

                                    }
                                }
                            }

                                if (order.getStatusId() > 3) {
                                    order.setSortindex(1);
                                } else
                                    order.setSortindex(0);
                                if (order.getStatusId() == 8 && fieldvalue.length > 5)//YD may be using when we have some common page with status to update was there, so check and delete if not using this. And status is updated below in next if/else
                                {
                                    for (int j = 6; j < 10; j++) {
                                        switch (j) {
                                            case 6:
                                                try {
                                                    order.setFromDate(fieldvalue[j]);
                                                } catch (Exception e5) {

                                                    e5.printStackTrace();
                                                }
                                                break;
                                            case 7:
                                                try {
                                                    order.setToDate(fieldvalue[j]);
                                                } catch (Exception e5) {

                                                    e5.printStackTrace();
                                                }
                                                break;
                                            case 8:
                                                try {
                                                    order.setStartTime(Long
                                                            .valueOf(fieldvalue[j]));
                                                } catch (Exception e5) {

                                                    e5.printStackTrace();
                                                }
                                                break;
                                            case 9:
                                                try {
                                                    order.setEndTime(Long
                                                            .valueOf(fieldvalue[j]));
                                                } catch (Exception e5) {

                                                    e5.printStackTrace();
                                                }
                                                break;
                                        }
                                    }
                                }


                            order.setModified(modifystr);
                            Object[] object = {order};
                            operationcount = DBHandler.updateRecord(context,
                                    DBHandler.orderListTable,
                                    object, queryType);
                            DBEngine.syncDataToSever(context, Order.TYPE_UPDATE);
                            //saveStatusOnServer(context,String.valueOf(order.getId()),String.valueOf(order.getStatusId())); //YD commenting because now syncing full data when internet is available earlier only syncing res geo and status
                        }
                        else if (name.equals("wkf")) // for status
                        {
                            Order order = new Order();
                            fieldvalue = tempFieldValues.split("\\|");
                            order.setId(Long.valueOf(id));
                            if (fieldvalue.length > 1) {
                                for (int j = 0; j < 5; j++) {
                                    switch (j) {
                                        case 0:
                                            try {
                                                order.setStatusId(Long
                                                        .valueOf(fieldvalue[j]));
                                            } catch (Exception e5) {
                                                order.setStatusId(Long
                                                        .valueOf(0));
                                                e5.printStackTrace();
                                            }
                                            break;
                                        case 1:
                                            try {
                                                order.setFromDate(fieldvalue[j]);
                                            } catch (Exception e5) {

                                                e5.printStackTrace();
                                            }
                                            break;
                                        case 2:
                                            try {
                                                order.setToDate(fieldvalue[j]);
                                            } catch (Exception e5) {

                                                e5.printStackTrace();
                                            }
                                            break;
                                        case 3:
                                            try {
                                                order.setStartTime(Long
                                                        .valueOf(fieldvalue[j]));
                                            } catch (Exception e5) {

                                                e5.printStackTrace();
                                            }
                                            break;
                                        case 4:
                                            try {
                                                order.setEndTime(Long
                                                        .valueOf(fieldvalue[j]));
                                            } catch (Exception e5) {

                                                e5.printStackTrace();
                                            }
                                            break;
                                    }
                                }
                                if (order.getStatusId() > 3) {
                                    order.setSortindex(1);
                                } else
                                    order.setSortindex(0);
                                order.setModified(modifystr);
                                Object[] object = {order};
                                operationcount = DBHandler.updateRecord(context,
                                        DBHandler.orderListTable,
                                        object, queryType);
                                DBEngine.syncDataToSever(context, Order.TYPE_UPDATE);
                                //saveStatusOnServer(context,String.valueOf(order.getId()),String.valueOf(order.getStatusId()));// YD imp:sending the status field to server by making saveorderstatus call
                            } else {
                                operationcount = DBHandler.updateOrders(context, requestObj,
                                        name, queryType, modifystr);
                                DBEngine.syncDataToSever(context, Order.TYPE_UPDATE);
                                //saveStatusOnServer(context,String.valueOf(order.getId()),tempFieldValues);
                            }
                        } else {
							/*Log.i( Utilities.TAG,"datastring : " + dataString+ " - name : " + name
											+ " - QueryType : "+ queryType);*/
                            operationcount = DBHandler.updateOrders(context, requestObj,
                                    name, queryType, modifystr);
                            DBEngine.syncDataToSever(context, Order.TYPE_UPDATE);
                        }

                        if (operationcount > 0) {

                            response = XMLHandler.XML_DATA_SUCCESS_MESSAGE;
                        } else
                            response = null;
                        //response = XMLHandler.XML_DATA_ERROR;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String sendCheckINOUT(Context context, Object reqObject) throws ParseException, IOException {//YD

        /*
         * String jsonString = xmlString; JSONObject jsonObject = JSONHandler
         * .getJsonObject(jsonString);
         */

        ClockInOutRequest clkInOut = (ClockInOutRequest) reqObject;
        String req_type = clkInOut.getType();

        Map<String, String> params = new HashMap<String, String>();
        String dataString = clkInOut.getAction();
        /*
         * JSONObject dataObject = JSONHandler .getJsonObject(dataString);
         */

        params.put(JSONHandler.KEY_ACTION, dataString);
        int int_tid = 0;
        String tid = null;
        try {
            tid = clkInOut.getTid();
            int_tid = Integer.parseInt(tid);
        } catch (Exception e1) {
            e1.printStackTrace();
            int_tid = -1;
        }

        int edition = PreferenceHandler.getPrefEditionForGeo(context);
        if (int_tid < 0) {
            return JSONHandler.JSON_FAILURE + JSONHandler.JSON_END;
        }

        if (int_tid == 1) {
            if (edition > 299) {
                Log.i(Utilities.TAG, "Starting alarm of Geo tracking");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Utilities.startAlarmManager(context);
                }
                else {
                    Utilities.startGeoSyncAlarm(context.getApplicationContext());//YD 2020 commenting because doze restriction to save battery has introduced by google which restricts the alarm manager to work when app goes in background but it does work when device is connect to charging.
                }
            } else {
                Log.w(Utilities.TAG, "Not starting alarm of Geo tracking because edition in login is " + edition);
            }
            PreferenceHandler.setTidStatus(context, true);
        } else {
            try {
                PreferenceHandler.setgeoFlagFirst(context, true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Utilities.stopAlarmManager(context);
                }
                else {
                    Utilities.stopGeoAlarm(context.getApplicationContext());//YD 2020 commenting because doze restriction to save battery has introduced by google which restricts the alarm manager to work when app goes in background but it does work when device is connect to charging.
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            PreferenceHandler.setTidStatus(context, false);
        }

        CheckinOut ccinoutlist[] = new CheckinOut[1];
        CheckinOut ccinout = new CheckinOut();
        String location = Utilities.getLocation(context);
        location = location.replace("#", ",");
        ccinout.setTid(tid);
        ccinout.setGeo(location);
        ccinout.setStmp(new Date().getTime());
        if (PreferenceHandler.getPrefLstoid(context) != null) {
            ccinout.setLstoid(PreferenceHandler.getPrefLstoid(context));
        } else {
            ccinout.setLstoid("11111");
        }

        if (PreferenceHandler.getPrefNxtoid(context) != null) {
                ccinout.setNxtoid(PreferenceHandler.getPrefNxtoid(context));
        } else {
            ccinout.setNxtoid("11111");
        }
        ccinout.setModify(DBHandler.modifiedNew);

        ccinoutlist[0] = ccinout;
        int result = DBHandler.insertRecord(context,
                DBHandler.checkincheckoutTable, ccinoutlist, DBHandler.QUERY_FOR_ORIG);
        if (result < 1) {
            return null;
            //return XMLHandler.XML_DATA_ERROR;
        }

        DBEngine.syncDataToSever(context, CheckinOut.CC_ACTIONBGSYNC);
        return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
			
/*		String location = Utilities.getLocation(context);
 * 		params.put("tid", tid);
		location = location.replace("#", ",");
		params.put("geo", location);
		String address = "";
		try {
			address = Utilities.getMyLocationAddress(context, location);
			if (address == null)
				address = "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		params.put("adr", address);

		String response = null;
		if (req_type.equals(JSONHandler.KEY_REQUEST_TYPE_POST))
			response = HttpConnection.post(context, "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi", params);
		else
			response = HttpConnection.get(context, "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi", params);

		Log.i( Utilities.TAG, "Response : " + response);
		return response;*/
    }

    public static Response getRes(RespCBandServST actFragContxt, Context context, long time, String source) {

        Response responseObj;

      /*  try {
            Utilities.log(context, "Getting getRes from the database.");
            Response responseObj;

            if (!source.equals(DATA_SOURCE_LOCAL)) {
                String txt = "Downloading Workers";
                if (PreferenceHandler.getWorkerHead(context) != null && !PreferenceHandler.getWorkerHead(context).equals("")) {
                    txt = "Downloading " + PreferenceHandler.getWorkerHead(context);
                }
                Utilities.sendmessageUpdatetoserver(actFragContxt, txt, SplashII.MESSAGE_SENDUPDATE_TO_UI);
                Map<String, String> params = null;
                if (time > 0) {
                    params = new HashMap<String, String>();
                    params.put("tstmp", String.valueOf(time));
                }
                // changed get to post 12-04-14
                String response = HttpConnection.get(context,
                        Api.getWorkerListApi(context), params);
                if (response != null) {
                    responseObj = new Response();
                    if (response.length() < 100) // vicky added check to avoid illogical parsing of data if the data has come
                    {
                        ResponseResult rr = new ResponseResult(context);
                        if (rr.parse(response)) {
                            String success = rr.getSuccess();
                            if (success != null) {    // vicky if success variable has come, means no data has come. So just return and let the caller handle the error.
                                responseObj.setStatus("failure");
                                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.HTTP_REQUEST_FAILED));
                                return responseObj;
                                //return response;//laterYD
                            }
                        }
                    }
                    DBHandler.deleteTable(DBHandler.resTable);
                    int result = DBHandler.saveWorker(context, response);
                    if (result > 0) {
                        responseObj.setStatus("success");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        return responseObj;
                        //return XMLHandler.XML_DATA_SUCCESS_MESSAGE;laterYD
                    } else {
                        responseObj.setStatus("failure");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.IO_ERROR));
                        return responseObj;
                    }
                    //return XMLHandler.getXMLForErrorCode(context, JSONHandler.ERROR_CODE_IO_ERROR);//laterYD
                }
            } else {
                HashMap<Long, Object> xml = DBHandler.checkAndGetRes(context,
                        Worker.ACTION_WORKER_LIST);

                responseObj = new Response();
                responseObj.setResponseMap(xml);
                return responseObj;
                //return xml;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/

        HashMap<Long, Object> xml = DBHandler.checkAndGetRes(context,
                Worker.ACTION_WORKER_LIST);

        responseObj = new Response();
        responseObj.setResponseMap(xml);
        return responseObj;
    }

    public static Response getServiceType(RespCBandServST actFragContxt, Context context, long time, String source) {
        try {
            Utilities.log(context, "Getting getServiceList from the database.");
            Response responseObj;
            if (!source.equals(DATA_SOURCE_LOCAL)) {
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Downloading Task Type", SplashII.MESSAGE_SENDUPDATE_TO_UI);
                Map<String, String> params = null;
                if (time > 0) {
                    params = new HashMap<String, String>();
                    params.put("tstmp", String.valueOf(time));
                }
                // changed get to post 12-04-14
                Log.e(Utilities.TAG, "*********************");
                String response = HttpConnection.get(context,
                        Api.getServiceTypeApi(context), params);
                if (response != null) {
                    responseObj = new Response();
                    if (response.length() < 100) // vicky added check to avoid illogical parsing of data if the data has come
                    {
                        ResponseResult rr = new ResponseResult(context);
                        if (rr.parse(response)) {
                            String success = rr.getSuccess();
                            if (success != null) {    // vicky if success variable has come, means no data has come. So just return and let the caller handle the error.
                                responseObj.setStatus("failure");
                                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.HTTP_REQUEST_FAILED));
                                return responseObj;
                                //	return response;
                            }
                        }
                    }
					/*DBHandler.deleteTable(DBHandler.taskTypeTable);
					int result = DBHandler.saveServiceType(context, response);
					if (result == DBHandler.recordsCannotInserted_NoData) {
						responseObj.setStatus("success");
						responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_DATA));
						return responseObj;
						//return XMLHandler.XML_EMPTY_DATA;
					}
					if (result > 0) {
						responseObj.setStatus("success");
						responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
						return responseObj;
						//return XMLHandler.XML_DATA_SUCCESS_MESSAGE;//laterYD
					} else {
						responseObj.setStatus("failure");
						responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.IO_ERROR));
						return responseObj;
						//return XMLHandler.getXMLForErrorCode(context, JSONHandler.ERROR_CODE_IO_ERROR);laterYD
					}
				}*/
                }
            } else {
                HashMap<Long, Object> xml = DBHandler.checkAndGetServiceType(context,
                        ServiceType.ACTION_SERVICE_TYPE);
                responseObj = new Response();
                responseObj.setResponseMap(xml);
                return responseObj;
                //return xml;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Response getPartType(RespCBandServST actFragContxt, Context context, long time, String source) {
        try {
            Utilities.log(context, "Getting PartTypeList from the database.");
            Response responseObj;
            if (!source.equals(DATA_SOURCE_LOCAL)) {
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Downloading Part Type", SplashII.MESSAGE_SENDUPDATE_TO_UI);
                Map<String, String> params = null;
                if (time > 0) {
                    params = new HashMap<String, String>();
                    params.put("tstmp", String.valueOf(time));
                }
                // changed get to post 12-04-14
                String response = HttpConnection.get(context,
                        Api.getPartTypeApi(context), params);
                //getResponseStatus(response);
                if (response != null) {
                    responseObj = new Response();
                    if (response.length() < 100) // vicky added check to avoid illogical parsing of data if the data has come
                    {
                        ResponseResult rr = new ResponseResult(context);
                        if (rr.parse(response)) {
                            String success = rr.getSuccess();
                            if (success != null) {    // vicky if success variable has come, means no data has come. So just return and let the caller handle the error.
                                responseObj.setStatus("failure");
                                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.HTTP_REQUEST_FAILED));
                                return responseObj;
                                //	return response; laterYD
                            }
                        }
                    }
                    DBHandler.deleteTable(DBHandler.partTypeListTable);
                    int result = DBHandler.savePartType(context, response);
                    if (result > 0) {
                        responseObj.setStatus("success");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        return responseObj;
                        //return XMLHandler.XML_DATA_SUCCESS_MESSAGE; laterYD
                    } else {
                        responseObj.setStatus("failure");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.IO_ERROR));
                        return responseObj;
                        //return XMLHandler.getXMLForErrorCode(context, JSONHandler.ERROR_CODE_IO_ERROR);laterYD
                    }
                }
            } else {
                HashMap<Long, Object> xml = DBHandler.checkAndGetPartType(context,
                        Parts.ACTION_PART_TYPE);
                responseObj = new Response();
                responseObj.setResponseMap(xml);
                return responseObj;
                // return xml;laterYD
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Response getCustList(RespCBandServST actFragContxt, Context context, List<Long> ncids, String source) {
        try {
            Utilities.log(context, "Getting CustList from the database.");
            Response responseObj;
            // DBHandler.getDataBase(context);

            if (!source.equals(DATA_SOURCE_LOCAL)) {
                String txt = "Downloading Customer List";
                if (PreferenceHandler.getCustomerHead(context) != null && !PreferenceHandler.getCustomerHead(context).equals("")) {
                    txt = "Downloading " + PreferenceHandler.getCustomerHead(context) + " List";
                }
                Utilities.sendmessageUpdatetoserver(actFragContxt, txt, SplashII.MESSAGE_SENDUPDATE_TO_UI);
                Map<String, String> params = null;

//                if (time > 0) {
//                    params = new HashMap<String, String>();
//                    params.put("tstmp", String.valueOf(time));
//                }
                StringBuilder temcids = new StringBuilder();
                for (int i = 0; i<ncids.size(); i++) {
                    if(ncids.size()==1) {
                        temcids = new StringBuilder(String.valueOf(ncids.get(i)));
                    }else{
                        if(i==ncids.size()-1) {
                            temcids.append(ncids.get(i));
                        }else{
                            temcids.append(ncids.get(i)).append("|");
                        }
                    }
                }


                if (ncids.size() > 0) {
                    params = new HashMap<String, String>();
                    params.put("id", String.valueOf(temcids.toString()));
                }
                // changed get to post 12-04-14


                String response = HttpConnection.get(context,
                        Api.getCustomerListApi(context), params);

            Log.i("MAB",response);

                createTextFileForService(response);
				/*String response = "<data><cst><id>23048006</id><tid>22808010</tid><ctpnm>Sonoma	1105</ctpnm><nm>1179-105518</nm></cst>" +
						"<cst><id>23048007</id><tid>22808011</tid><ctpnm>Son	1105</ctpnm><nm>1179-105519</nm></cst>"+
						"</data>";*/
                if (response != null) {
                    responseObj = new Response();
                    if (response.length() < 100) // vicky added check to avoid illogical parsing of data if the data has come
                    {
                        ResponseResult rr = new ResponseResult(context);
                        if (rr.parse(response)) {
                            String success = rr.getSuccess();
                            if (success != null) {    // vicky if success variable has come, means no data has come. So just return and let the caller handle the error.
                                responseObj.setStatus("failure");
                                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.HTTP_REQUEST_FAILED));
                                return responseObj;
                                //return response;
                            }
                        }
                    }
                    int result = DBHandler.saveCustomer(context, response);
                    if (result > 0) {
                        responseObj.setStatus("success");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        return responseObj;
                        //return XMLHandler.XML_DATA_SUCCESS_MESSAGE;laterYD
                    }
					/*else{
						responseObj.setStatus("failure");
						responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.IO_ERROR));
						return responseObj;
						//return XMLHandler.getXMLForErrorCode(context, JSONHandler.ERROR_CODE_IO_ERROR);laterYD
					}	*/  // YD commented temporarily 
                }
            } else {
                HashMap<Long, Object> xml = DBHandler.checkAndGetCustList(context,
                        Customer.ACTION_CUSTOMER_LIST);
                responseObj = new Response();
                responseObj.setResponseMap(xml);
                return responseObj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCust(RespCBandServST actFragContxt, Context context, long id, String source) {
        try {
            Utilities.log(context, "Getting CustList from the database.");

            // DBHandler.getDataBase(context);

            //if (!source.equals(DATA_SOURCE_LOCAL)) {

            Map<String, String> params = null;
            if (id > 0) {
                params = new HashMap<String, String>();
                params.put("id", String.valueOf(id));
            }
            // changed get to post 12-04-14
            String response = HttpConnection.get(context,
                    Api.getCustomerApi(context), params);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Response getCusType(RespCBandServST actFragContxt, Context context, long time, String source) {
        try {
            Utilities.log(context, "Getting CusTypeList from the database.");
            Response responseObj;
            // DBHandler.getDataBase(context);

            if (!source.equals(DATA_SOURCE_LOCAL)) {
                Log.d("TAG999","1");
                String txt = "Downloading Customer List";
                if (PreferenceHandler.getCustomerHead(context) != null && !PreferenceHandler.getCustomerHead(context).equals("")) {
                    txt = "Downloading " + PreferenceHandler.getCustomerHead(context) + " List";
                    Log.d("TAG999",txt);
                }

                Utilities.sendmessageUpdatetoserver(actFragContxt, txt, SplashII.MESSAGE_SENDUPDATE_TO_UI);
                Map<String, String> params = null;
                if (time > 0) {
                    params = new HashMap<String, String>();
                    params.put("tstmp", String.valueOf(time));
                }
                // changed get to post 12-04-14
                String response = HttpConnection.get(context,
                        Api.getCustTypeListApi(context), params);

                if (response != null) {
                    responseObj = new Response();

                    if (response.length() < 100) // vicky added check to avoid illogical parsing of data if the data has come
                    {
                        ResponseResult rr = new ResponseResult(context);
                        if (rr.parse(response)) {
                            String success = rr.getSuccess();
                            if (success != null) {    // vicky if success variable has come, means no data has come. So just return and let the caller handle the error.
                                responseObj.setStatus("failure");
                                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.HTTP_REQUEST_FAILED));
                                return responseObj;
                                //	return response;
                            }
                        }
                    }
                    DBHandler.deleteTable(DBHandler.customerTypeTable);
                    int result = DBHandler.saveCusType(context, response);
                    if (result > 0) {
                        responseObj.setStatus("success");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        return responseObj;
                        //return XMLHandler.XML_DATA_SUCCESS_MESSAGE;laterYD
                    } else {
                        responseObj.setStatus("failure");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.IO_ERROR));
                        return responseObj;
                        //return XMLHandler.getXMLForErrorCode(context, JSONHandler.ERROR_CODE_IO_ERROR);laterYD
                    }
                }
            } else {
                HashMap<Long, Object> xml = DBHandler.checkAndGetCusType(context,
                        CustomerType.ACTION_CUST_TYPE);
                responseObj = new Response();
                responseObj.setResponseMap(xml);
                return responseObj;
                // return xml;laterYD
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Response getSiteType(RespCBandServST actFragContxt, Context context, long time, String source) {
        try {
            Utilities.log(context, "Getting CusTypeList from the database.");
            Response responseObj = null;
            // DBHandler.getDataBase(context);


            if (!source.equals(DATA_SOURCE_LOCAL)) {
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Downloading Site Type", SplashII.MESSAGE_SENDUPDATE_TO_UI);
                Map<String, String> params = null;
                if (time > 0) {
                    params = new HashMap<String, String>();
                    params.put("tstmp", String.valueOf(time));
                }
                // changed get to post 12-04-14
                String response = HttpConnection.get(context,
                        Api.getSiteTypeListApi(context), params);
                if (response != null) {
                    responseObj = new Response();
                    if (response.length() < 100) // vicky added check to avoid illogical parsing of data if the data has come
                    {
                        ResponseResult rr = new ResponseResult(context);
                        if (rr.parse(response)) {
                            String success = rr.getSuccess();
                            if (success != null) {    // vicky if success variable has come, means no data has come. So just return and let the caller handle the error.
                                responseObj.setStatus("failure");
                                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.HTTP_REQUEST_FAILED));

                                return responseObj;
                                //return response;
                            }
                        }
                    }
                    DBHandler.deleteTable(DBHandler.siteTypeTable);
                    int result = DBHandler.saveSiteType(context, response);
                    if (result > 0) {
                        responseObj.setStatus("success");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        return responseObj;
                        //return XMLHandler.XML_DATA_SUCCESS_MESSAGE;laterYD
                    } else {
                        responseObj.setStatus("failure");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.IO_ERROR));
                        return responseObj;
                        //return XMLHandler.getXMLForErrorCode(context, JSONHandler.ERROR_CODE_IO_ERROR);laterYD
                    }
                }
            } else {
                HashMap<Long, Object> xml = DBHandler.checkAndGetSiteType(context,
                        SiteType.ACTION_SITE_TYPE);
                responseObj = new Response();
                responseObj.setResponseMap(xml);
                return responseObj;
                //return xml;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Response getSiteType(RespCBandServST actFragContxt, Context context, long time, String cid,  String source) {
        try {
            Utilities.log(context, "Getting CusTypeList from the database.");
            Response responseObj = null;
            // DBHandler.getDataBase(context);


            if (!source.equals(DATA_SOURCE_LOCAL)) {
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Downloading Site Type", SplashII.MESSAGE_SENDUPDATE_TO_UI);
                Map<String, String> params = null;
                if (time > 0) {
                    params = new HashMap<String, String>();
                    params.put("tstmp", String.valueOf(time));
                }
                // changed get to post 12-04-14
                String response = HttpConnection.get(context,
                        Api.getSiteTypeListApi(context), params);
                if (response != null) {
                    responseObj = new Response();
                    if (response.length() < 100) // vicky added check to avoid illogical parsing of data if the data has come
                    {
                        ResponseResult rr = new ResponseResult(context);
                        if (rr.parse(response)) {
                            String success = rr.getSuccess();
                            if (success != null) {    // vicky if success variable has come, means no data has come. So just return and let the caller handle the error.
                                responseObj.setStatus("failure");
                                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.HTTP_REQUEST_FAILED));

                                return responseObj;
                                //return response;
                            }
                        }
                    }
                    DBHandler.deleteTable(DBHandler.siteTypeTable);
                    int result = DBHandler.saveSiteType(context, response);
                    if (result > 0) {
                        responseObj.setStatus("success");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        return responseObj;
                        //return XMLHandler.XML_DATA_SUCCESS_MESSAGE;laterYD
                    } else {
                        responseObj.setStatus("failure");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.IO_ERROR));
                        return responseObj;
                        //return XMLHandler.getXMLForErrorCode(context, JSONHandler.ERROR_CODE_IO_ERROR);laterYD
                    }
                }
            } else {
                HashMap<Long, Object> xml = DBHandler.checkAndGetSiteType(context,
                        SiteType.ACTION_SITE_TYPE, cid);
                responseObj = new Response();
                responseObj.setResponseMap(xml);
                return responseObj;
                //return xml;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Response getOrderType(RespCBandServST actFragContxt, Context context, long time, String source) {
        try {
            Utilities.log(context,
                    "Getting getOrdertypeList from the database.");
            Response responseObj = null;

            if (!source.equals(DATA_SOURCE_LOCAL)) {
                String txt = "Downloading Order List";
                if (PreferenceHandler.getOrderHead(context) != null && !PreferenceHandler.getOrderHead(context).equals("")) {
                    txt = "Downloading " + PreferenceHandler.getOrderHead(context) + " List";
                }
                Utilities.sendmessageUpdatetoserver(actFragContxt, txt, SplashII.MESSAGE_SENDUPDATE_TO_UI);
                Map<String, String> params = null;
                if (time > 0) {
                    params = new HashMap<String, String>();
                    params.put("tstmp", String.valueOf(time));
                }
                // changed get to post 12-04-14
                String response = HttpConnection.get(context,
                        Api.getOrderTypeApi(context), params);
                if (response != null) {
                    responseObj = new Response();
                    if (response.length() < 100) // vicky added check to avoid illogical parsing of data if the data has come
                    {
                        ResponseResult rr = new ResponseResult(context);
                        if (rr.parse(response)) {
                            String success = rr.getSuccess();
                            if (success != null) {// vicky if success variable has come, means no data has come. So just return and let the caller handle the error.
                                responseObj.setStatus("failure");
                                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.HTTP_REQUEST_FAILED));
                                return responseObj;
                                //return response;
                            }
                        }
                    }
                    DBHandler.deleteTable(DBHandler.orderTypeListTable);
                    int result = DBHandler.saveOrderType(context, response);
                    if (result > 0) {
                        responseObj.setStatus("success");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        return responseObj;
                        //return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
                    } else {
                        responseObj.setStatus("failure");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.IO_ERROR));
                        return responseObj;
                        //return XMLHandler.getXMLForErrorCode(context, JSONHandler.ERROR_CODE_IO_ERROR);laterYD
                    }
                }
            } else {
                HashMap<Long, Object> xml = DBHandler.checkAndGetOrderType(context,
                        OrderTypeList.ACTION_ORDER_TYPE);
                responseObj = new Response();
                responseObj.setResponseMap(xml);
                return responseObj;
                //return xml;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Response getAssetsType(RespCBandServST actFragContxt, Context context, long time, String source) {
        try {
            Utilities.log(context,
                    "Getting getAssets from the database.");
            Response responseObj = null;

            if (!source.equals(DATA_SOURCE_LOCAL)) {
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Downloading Assets Type", SplashII.MESSAGE_SENDUPDATE_TO_UI);
                Map<String, String> params = null;
                if (time > 0) {
                    params = new HashMap<String, String>();
                    params.put("tstmp", String.valueOf(time));
                }
                String response = HttpConnection.get(context,
                        Api.getAssets(context), params);
                if (response != null) {
                    responseObj = new Response();
                    if (response.length() < 100) // vicky added check to avoid illogical parsing of data if the data has come
                    {
                        ResponseResult rr = new ResponseResult(context);
                        if (rr.parse(response)) {
                            String success = rr.getSuccess();
                            if (success != null) {// vicky if success variable has come, means no data has come. So just return and let the caller handle the error.
                                responseObj.setStatus("failure");
                                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.HTTP_REQUEST_FAILED));
                                return responseObj;
                                //return response;
                            }
                        }
                    }
                    DBHandler.deleteTable(DBHandler.assestTypeTable);
                    int result = DBHandler.saveAssetsType(context, response);
                    if (result > 0) {
                        responseObj.setStatus("success");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        return responseObj;
                        //return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
                    } else {
                        responseObj.setStatus("failure");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.IO_ERROR));
                        return responseObj;
                        //return XMLHandler.getXMLForErrorCode(context, JSONHandler.ERROR_CODE_IO_ERROR);laterYD
                    }
                }
            } else {
                HashMap<Long, Object> xml = DBHandler.checkAndGetAssetType(context,
                        AssetsType.ACTION_GET_ASSETS_TYPE);
                responseObj = new Response();
                responseObj.setResponseMap(xml);
                return responseObj;
                //return xml;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getHeaderStrFrmServForPg(Context context) {
        Map<String, String> params = null;
        params = new HashMap<String, String>();
        params.put("tstmp", String.valueOf(new Date().getTime()));
        try {
            String response = null;
            response = HttpConnection.get(context,
                    Api.getHeadersApiForPg(context), params);

            Utilities.log(context,
                    "Getting headers from the server." + response);
            if (response != null) {
                ResponseResult rr = new ResponseResult(context);
                if (rr.parse(response)) {
                    String success = rr.getSuccess();
                    if (success == null) {
                        return;
                    }
                }

                new XMLHandler().getAppHeadersFromXMLForPg(context, response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean sendLogoutrequest(Context context) {
        Map<String, String> params = null;
        params = new HashMap<String, String>();
        params.put("tstmp", String.valueOf(new Date().getTime()));
        params.put("egeo", Utilities.getLocation(context));
        String response = HttpConnection.post(context,
                Api.getLogoutApi(context), params);
        Utilities.log(context,
                "Getting headers from the server." + response);
        if (response != null) {
            ResponseResult rr = new ResponseResult(context);
            if (rr.parse(response)) {
                String success = rr.getSuccess();
                if (success != null && success.equals("true")) {
                    return true;
                }
            }

        }
        return false;
    }

    public static void getHeaderStrFrmServForAssets(Context context) {
        Map<String, String> params = null;
        params = new HashMap<String, String>();
        params.put("tstmp", String.valueOf(new Date().getTime()));
        try {
            String response = HttpConnection.get(context,
                    Api.getHeadersApiForAssets(context), params);
            Utilities.log(context,
                    "Getting headers from the server." + response);
            if (response != null) {
                ResponseResult rr = new ResponseResult(context);
                if (rr.parse(response)) {
                    String success = rr.getSuccess();
                    if (success != null) {
                        return;
                    }
                }
                new XMLHandler().getAppHeadersFromXMLForAssets(context, response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Response getStatusType(RespCBandServST actFragContxt, Context context, long time, String source) {
        try {
            Utilities.log(context,
                    "Getting getOrdertypeList from the database.");
            Response responseObj = null;

            if (!source.equals(DATA_SOURCE_LOCAL)) {
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Downloading Status Type", SplashII.MESSAGE_SENDUPDATE_TO_UI);
                Map<String, String> params = null;
                if (time > 0) {
                    params = new HashMap<String, String>();
                    params.put("tstmp", String.valueOf(time));
                }
                // changed get to post 12-04-14
                String response = HttpConnection.get(context,
                        Api.getStatusTypeApi(context), params);
                if (response != null) {
                    responseObj = new Response();
                    if (response.length() < 100) // vicky added check to avoid illogical parsing of data if the data has come
                    {
                        ResponseResult rr = new ResponseResult(context);
                        if (rr.parse(response)) {
                            String success = rr.getSuccess();
                            if (success != null) {// vicky if success variable has come, means no data has come. So just return and let the caller handle the error.
                                responseObj.setStatus("failure");
                                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.HTTP_REQUEST_FAILED));
                                return responseObj;
                                //return response;
                            }
                        }
                    }
                    DBHandler.deleteTable(DBHandler.orderStatusTable);
                    int result = DBHandler.saveStatusType(context, response);
                    if (result > 0) {
                        responseObj.setStatus("success");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        return responseObj;
                        //return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
                    } else {
                        responseObj.setStatus("failure");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.IO_ERROR));
                        return responseObj;
                        //return XMLHandler.getXMLForErrorCode(context, JSONHandler.ERROR_CODE_IO_ERROR);laterYD
                    }
                }
            } else {
                HashMap<Long, Object> xml = DBHandler.checkAndGetOrderStatusType(context,
                        OrderStatus.ACTION_GET_STATUS, DBHandler.QUERY_FOR_ORIG);
                responseObj = new Response();
                responseObj.setResponseMap(xml);
                return responseObj;
                //return xml;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Response getcmpyclientsite(RespCBandServST actFragContxt, Context context, long time, String source) {
        try {
            Utilities.log(context,
                    "Getting getOrdertypeList from the database.");
            Response responseObj = null;

            if (!source.equals(DATA_SOURCE_LOCAL)) {
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Downloading Client Site Type", SplashII.MESSAGE_SENDUPDATE_TO_UI);
                Map<String, String> params = null;
                if (time > 0) {
                    params = new HashMap<String, String>();
                    params.put("tstmp", String.valueOf(time));
                }
                // changed get to post 12-04-14
                String response = HttpConnection.get(context,
                        Api.getclientsite(context), params);
                if (response != null) {
                    responseObj = new Response();
                    if (response.length() < 100) // vicky added check to avoid illogical parsing of data if the data has come
                    {
                        ResponseResult rr = new ResponseResult(context);
                        if (rr.parse(response)) {
                            String success = rr.getSuccess();
                            if (success != null) {// vicky if success variable has come, means no data has come. So just return and let the caller handle the error.
                                responseObj.setStatus("failure");
                                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.HTTP_REQUEST_FAILED));
                                return responseObj;
                                //return response;
                            }
                        }
                    }
                    DBHandler.deleteTable(DBHandler.companyClientSiteTable);
                    int result = DBHandler.saveCompClientSite(context, response);
                    if (result > 0) {
                        responseObj.setStatus("success");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        return responseObj;
                        //return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
                    } else {
                        responseObj.setStatus("failure");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.IO_ERROR));
                        return responseObj;
                        //return XMLHandler.getXMLForErrorCode(context, JSONHandler.ERROR_CODE_IO_ERROR);laterYD
                    }
                }
            } else {
                HashMap<Long, Object> xml = DBHandler.checkAndGetCompyClientSite(context,
                        ClientSite.ACTION_GET_CLIENT_SITE, DBHandler.QUERY_FOR_ORIG);
                responseObj = new Response();
                responseObj.setResponseMap(xml);
                return responseObj;
                //return xml;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Response getshifts(RespCBandServST actFragContxt, Context context, long time, String source) {
        try {
            Utilities.log(context,
                    "Getting getOrdertypeList from the database.");
            Response responseObj = null;

            if (!source.equals(DATA_SOURCE_LOCAL)) {
                if (actFragContxt != null) {
                    Utilities.sendmessageUpdatetoserver(actFragContxt, "Downloading Shifts Type", SplashII.MESSAGE_SENDUPDATE_TO_UI);
                }
                Map<String, String> params = null;

                params = new HashMap<String, String>();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cDate = Calendar.getInstance();
                cDate.set(Calendar.DAY_OF_MONTH, 1);
                params.put("from", sdf.format(cDate.getTime()));

                cDate.add(Calendar.MONTH, 6);

//					params.put("to", sdf.format(cDate.getTime()));
//				params.put("tstmp", String.valueOf(time));
                //params.put("tz", TimeZone.getTimeZone("US/Pacific").getDisplayName());

                String response = HttpConnection.get(context, Api.getshift(context), params);
                if (response != null) {
                    responseObj = new Response();
                    if (response.length() < 100) // vicky added check to avoid illogical parsing of data if the data has come
                    {
                        ResponseResult rr = new ResponseResult(context);
                        if (rr.parse(response)) {
                            String success = rr.getSuccess();
                            if (success != null) {// vicky if success variable has come, means no data has come. So just return and let the caller handle the error.
                                //responseObj.setStatus("failure");
                                //responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.HTTP_REQUEST_FAILED));
                                //return responseObj;
                                //return response;
                            }
                        }
                    }
                    String whereclause = DBHandler.modifiedField + "=''";
                    DBHandler.delete(context, DBHandler.shiftTable, whereclause);
                    int result = DBHandler.saveShifts(context, response);
                    if (result > 0) {
                        responseObj.setStatus("success");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        return responseObj;
                        //return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
                    } else {
                        responseObj.setStatus("success");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                        //responseObj.setStatus("failure");
                        //responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.IO_ERROR));
                        return responseObj;
                        //return XMLHandler.getXMLForErrorCode(context, JSONHandler.ERROR_CODE_IO_ERROR);laterYD
                    }
                }
            } else {
                HashMap<Long, Object> xml = DBHandler.checkAndGetShift(context,
                        Shifts.ACTION_GET_SHIFTS, DBHandler.QUERY_FOR_ORIG);
                responseObj = new Response();
                responseObj.setResponseMap(xml);
                return responseObj;
                //return xml;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Response getCustContact(Context context, String cid) {
        Response responseObj = null;
        try {
            Utilities.log(context, "Getting CustContact from the database.");
            HashMap<Long, Object> xml = DBHandler
                    .checkAndGetCustContact(context,
                            CustomerContact.ACTION_GET_CONTACT, cid,
                            DBHandler.QUERY_FOR_ORIG);
            responseObj = new Response();
            responseObj.setStatus("success");

            if (xml != null) {
                responseObj.setResponseMap(xml);
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                return responseObj;
            } else {
                responseObj.setResponseMap(null);
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_DATA));
                return responseObj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Response getCustAssets(Context context, String cid) {
        Response responseObj = null;
        try {
            Utilities.log(context, "Getting CustContact from the database.");
            HashMap<Long, Object> xml = DBHandler
                    .checkAndGetCustAssets(context,
                            Assets.ACTION_GET_ASSETS, cid,
                            DBHandler.QUERY_FOR_ORIG);
            responseObj = new Response();
            responseObj.setStatus("success");

            if (xml != null) {
                responseObj.setResponseMap(xml);
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                return responseObj;
            } else {
                responseObj.setResponseMap(null);
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_DATA));
                return responseObj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Response getOrderTaskforOffline(Context context, String oid) {
        Response responseObj = null;
        try {
            Utilities.log(context, "Getting OrderTask from the database.");
            HashMap<Long, Object> xml = DBHandler
                    .checkAndGetOrderTask(context,
                            OrderTask.ACTION_GET_ORDER_TASK, oid,
                            DBHandler.QUERY_FOR_ORIG);
            responseObj = new Response();
            responseObj.setStatus("success");

            if (xml != null) {
                responseObj.setResponseMap(xml);
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                return responseObj;
            } else {
                responseObj.setResponseMap(null);
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_DATA));
                return responseObj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Response getOrderPartforOffline(Context context, String oid) {
        try {
            Response responseObj = null;
            Utilities.log(context, "Getting OrderTask from the database.");
            HashMap<Long, Object> xml = DBHandler
                    .checkAndGetOrderPart(context,
                            OrderPart.ACTION_GET_ORDER_PART, oid,
                            DBHandler.QUERY_FOR_ORIG);

            responseObj = new Response();
            responseObj.setStatus("success");

            if (xml != null) {
                responseObj.setResponseMap(xml);
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                return responseObj;
            } else {
                responseObj.setResponseMap(null);
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_DATA));
                return responseObj;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        return null;
    }

    public static Response getOrderFormforOffline(Context context, String oid) {
        try {
            Response responseObj = null;
            Utilities.log(context, "Getting Form from the database.");
            HashMap<Long, Object> xml = DBHandler
                    .checkAndGetOrderForm(context,
                            Form.ACTION_GET_ORDER_FORM, oid,
                            DBHandler.QUERY_FOR_ORIG);

            responseObj = new Response();
            responseObj.setStatus("success");

            if (xml != null) {
                responseObj.setResponseMap(xml);
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                return responseObj;
            } else {
                responseObj.setResponseMap(null);
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_DATA));
                return responseObj;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        return null;
    }

    public static HashMap<Long, Object> getOrderMediaforOffline(Context context, String oid) {
        try {
            Utilities.log(context, "Getting OrderTask from the database.");
            HashMap<Long, Object> xml = DBHandler.getOrderMediaforoffline(context, OrderMedia.ACTION_GET_MEDIA, oid, DBHandler.QUERY_FOR_ORIG);
            return xml;
        } catch (Exception e) {
            e.printStackTrace();

        }

        return null;
    }

    public static HashMap<Long, Object> getOrderMediaformforOffline(Context context, String oid, String frmkey) {
        try {
            Utilities.log(context, "Getting OrderTask from the database.");
            HashMap<Long, Object> xml = DBHandler.getOrderMediaformforoffline(context, OrderMedia.ACTION_GET_MEDIA, oid, frmkey, DBHandler.QUERY_FOR_ORIG);
            return xml;
        } catch (Exception e) {
            e.printStackTrace();

        }

        return null;
    }


    public static String getOrderPartfromServerandSave(Context context, String oid,
                                                       int queryType) {
        Map<String, String> getOrderParams = new HashMap<String, String>();
        getOrderParams.put(JSONHandler.KEY_OID, oid);
        getOrderParams.put("action", OrderPart.ACTION_GET_ORDER_PART);
        // changed get to post 12-04-14
        String response;
        try {
            response = HttpConnection.get(context,
                    "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", getOrderParams);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return XMLHandler.XML_DATA_ERROR;

        }
        Utilities.log(context, "getOrderPartfromServer Received xml from server is : \n"
                + response);

        if (response != null) {
            if (!Utilities.IsResponseError(response)) {

                DBHandler.saveDownloadedOrderPart(context, response,
                        OrderPart.NEW_ORDER_PART, 0, queryType);
                return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
            }
            return response;
        }

        return XMLHandler.XML_DATA_ERROR;
    }

    public static String getOrderMediafromServerandSave(Context context, String oid,
                                                        int queryType) {
        Map<String, String> getOrderParams = new HashMap<String, String>();
        getOrderParams.put(OrderMedia.ORDER_ID, oid);
        getOrderParams.put("action", OrderMedia.ACTION_GET_MEDIA);
        // changed get to post 12-04-14
        String response;
        try {
            response = HttpConnection.get(context,
                    "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", getOrderParams);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return XMLHandler.XML_DATA_ERROR;

        }
        Utilities.log(context, "getOrderPartfromServer Received xml from server is : \n"
                + response);

        if (response != null) {
            if (!Utilities.IsResponseError(response)) {

                int[] respons = DBHandler.saveDownloadedOrderMeta(context, response,
                        OrderPart.NEW_ORDER_PART, 0, queryType);
                if (respons[0] > 0)
                    if (respons[1] > 0)
                        return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
                    else
                        return XMLHandler.XML_DATA_SUCCESS_MESSAGE_DOWNLOAD;
                else
                    return XMLHandler.XML_DATA_ERROR;
            }
            return response;
        }

        return XMLHandler.XML_DATA_ERROR;
    }

    public static String getOrderFormfromServerandSave(Context context, String oid,
                                                       int queryType) {
        Map<String, String> getOrderParams = new HashMap<String, String>();
        getOrderParams.put(JSONHandler.KEY_OID, oid);
        //getOrderParams.put("action", OrderTask.ACTION_GET_ORDER_TASK);
        getOrderParams.put("action", Form.ACTION_GET_ORDER_FORM);
        // changed get to post 12-04-14
        String response;
        try {
            response = HttpConnection.get(context,
                    "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", getOrderParams);

            //createTextFileForService(response);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return XMLHandler.XML_DATA_ERROR;

        }
        Utilities.log(context, "getOrderServicefromServerandSave Received xml from server is : \n"
                + response);

        if (response != null) {
            if (!Utilities.IsResponseError(response)) {

                DBHandler.saveDownloadedOrderForm(context, response,
                        "0", 0, queryType);
                return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
            }
            return response;
        }

        return XMLHandler.XML_DATA_ERROR;

    }

    public static void createTextFileForService(String response) {
        String compfilename = null;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            compfilename = Environment
                    .getExternalStorageDirectory()
                    .getAbsolutePath()
                    + "/AceRoute";
        } else {
            compfilename = Environment.getDataDirectory()
                    .getAbsolutePath() + "/AceRoute";
        }
		
		/*File file = new File(compfilename);

		long time = System.currentTimeMillis();

		file.delete();

		try {
			file.mkdirs();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		String basepath = file.getAbsolutePath();

		try {
			file = new File(basepath + "/the_file.txt");
			if (file.delete()) {
				file.createNewFile();
			} else {
				file = new File(basepath + "/the_file");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/

        PrintWriter writer;
        try {
            writer = new PrintWriter(compfilename + "/the-file-name.txt", "UTF-8");
            writer.println(response);
            writer.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static String getOrderNotefromServerandSave(Context context, String oid,
                                                       int queryType) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(OrderNotes.NOTES_ORDER_ID, oid);
        params.put(JSONHandler.KEY_ACTION, OrderNotes.ACTION_GET_NOTES);

        // changed get to post 12-04-14
        String response;
        try {
            response = HttpConnection.get(context,
                    "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", params);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return XMLHandler.XML_DATA_ERROR;

        }
        Utilities.log(context, "getOrderNotefromServerandSave Received xml from server is : \n"
                + response);

        if (response != null) {
            if (!Utilities.IsResponseError(response)) {

                int result = DBHandler.saveDownloadedOrderNotes(context, response,
                        Long.parseLong(oid), queryType);
                return "" + result;
                //return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
            }
            return response;
        }

        return XMLHandler.XML_DATA_ERROR;
    }

    public static String getCustTokenfromServerandSave(Context context, String Cid,
                                                       int queryType) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(JSONHandler.KEY_REQUEST_TYPE, "55");// YD this KEY_REQUEST_TYPE is only used here.
        params.put(JSONHandler.KEY_ACTION, "initrpt");
        params.put(JSONHandler.KEY_ID, Cid);

        // changed get to post 12-04-14
        String response;
        try {
            response = HttpConnection.get(context,
                    "https://" + PreferenceHandler.getPrefBaseUrl(context) + "/mobi", params);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return XMLHandler.XML_DATA_ERROR;

        }
        Utilities.log(context, "Customer Token Received xml from server is : \n"
                + response);

        if (response != null) {
            if (!Utilities.IsResponseError(response)) {
                String result = new XMLHandler(context)
                        .getCustTokenValuesFromXML(response);
                if (result != null && !result.equals("")) {
                    CustHistoryToken custHistTokenObj = new CustHistoryToken();
                    custHistTokenObj.setCustId(Long.valueOf(Cid));
                    custHistTokenObj.setCustToken(result);
                    custHistTokenObj.setPath("");

                    PreferenceHandler.setCustomerTokens(context, custHistTokenObj, "add");//YD saving to pref
                }
            }
            return response;
        }

        return XMLHandler.XML_DATA_ERROR;
    }

    public static String getCustHistoryXlsfromServer(Context context, String cid, String token)// YD token is the key getting from server
    {
        String url = HttpConnection.getRequestURL(context) + "&" + "action=getrpt" +
                "&chk=0" + "&" + JSONHandler.KEY + "=" + token;

        String timestamp = Long.toString(Utilities.getCurrentTime());
        String filename = cid + "_" + "history" + "_" + timestamp + ".xls";
        String mediapath = Utilities.DownloadFiles(context, url, "AceRoute", filename);

        if (mediapath != null)
            return mediapath;
        else
            return null;
    }

    public static Response getSite(RespCBandServST actFragContxt, Context context, long time, long cid, String source) {
        Response responseObj = null;
     /*   try {
            Utilities.log(context, "Getting SiteList from the database.");
            Response responseObj = null;
            // DBHandler.getDataBase(context);

            if (!source.equals(DATA_SOURCE_LOCAL)) {
                Utilities.sendmessageUpdatetoserver(actFragContxt, "Downloading sites", SplashII.MESSAGE_SENDUPDATE_TO_UI);
                Map<String, String> params = null;
                if (time > 0) {
                    params = new HashMap<String, String>();
                    params.put("tstmp", String.valueOf(time));
                }
                String siteurl = Api.getSiteApi(context) + "&cid=" + cid;
                // changed get to post 12-04-14
                String response = HttpConnection.get(context, siteurl, params);
                if (response != null) {
                    responseObj = new Response();
                    DBHandler.deleteTable(DBHandler.siteTable);
                    int result = DBHandler.saveSite(context, response);
                    if (result > 0) {
                        responseObj.setStatus("success");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_DATA));
                        return responseObj;
                        //return XMLHandler.XML_DATA_SUCCESS_MESSAGE;laterYD
                    } else {
                        responseObj.setStatus("failure");
                        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.IO_ERROR));
                        return responseObj;
                        //return XMLHandler.getXMLForErrorCode(context, JSONHandler.ERROR_CODE_IO_ERROR);laterYD
                    }
                }
            } else {
                HashMap<Long, Object> xml = DBHandler.checkAndGetSite(context, Site.ACTION_SITE,
                        cid);

                responseObj = new Response();
                responseObj.setResponseMap(xml);
                return responseObj;
                //return xml;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;*/
        HashMap<Long, Object> xml = DBHandler.checkAndGetSite(context, Site.ACTION_SITE,
                cid);

        responseObj = new Response();
        responseObj.setResponseMap(xml);
        return responseObj;

    }

    public static String saveSitefromUI(Context context, SaveSiteRequest saveSiteReqObject) {
        Utilities.log(context, "saveSite to database");
        // DBHandler.getDataBase(context);
        //String dataString = jsonObject.get(JSONHandler.KEY_REQUEST_DATA).toString();YD
        int result = DBHandler.saveSiteFromUI(context, saveSiteReqObject);
        if (result > 0 && Utilities.checkInternetConnection(context, false)) {  //YD using for directly save to server
            AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(context).UploadSiteToServer(null);
            return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
        } else if (result > 0) {
            syncDataToSever(context, Site.TYPE);
            return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
        }
        return null;
    }

    public static String update_Site(Context context, EditSiteReq edtSiteReqObj) {
        Utilities.log(context, "saveSite to database");
        // DBHandler.getDataBase(context);
        //String dataString = jsonObject.get(JSONHandler.KEY_REQUEST_DATA).toString();YD
        int result = DBHandler.saveSiteFromUI(context, edtSiteReqObj);

        if (result > 0) {
            syncDataToSever(context, Site.TYPE);
            return XMLHandler.XML_DATA_SUCCESS_MESSAGE;
        }
        return null;
        //return XMLHandler.XML_DATA_ERROR;

    }

    public static void syncDataToSever(Context context, final int action) {
        final Context ctx = context;
        new Thread(new Runnable() {
            @Override
            public void run() {

                switch (action) {

                    case Order.TYPE_UPDATE:
                        AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(ctx).UploadOrdersToServer(null);
                        break;

                    case OrderPart.TYPE:
                        AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(ctx).UploadPartToServer(null);
                        break;

                    case Form.TYPE:
                        Log.d("TAG21","form saveing");
                        AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(ctx).UploadFormToServer(null);
                        break;

                    case Assets.TYPE:
                        if (PreferenceHandler.getTasksConfig(ctx).equals("1"))
                            AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(ctx).UploadAssetToServer(null);
                        break;

                    case OrderTask.TYPE:
                        if (PreferenceHandler.getTasksConfig(ctx).equals("1"))
                            AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(ctx).UploadTaskToServer(null);
                        break;
                    case Site.TYPE:
                        AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(ctx).UploadSiteToServer(null);
                        break;

                    case OrderNotes.TYPE_SAVE:
                        AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(ctx).UploadNoteToServer(null);
                        break;

                    case OrderMedia.TYPE_SAVE:
                        AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(ctx).UploadMediaToServer(null);
                        break;
                    case CheckinOut.CC_ACTIONBGSYNC:
                        try {
                            AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(ctx).syncCheckinout(null, ctx);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case OrdersMessage.TYPE_SAVE:
                        try {
                            AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(ctx).syncOrderMessages(null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case CustomerContact.TYPE_EDIT:
                        AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(ctx).syncUpdatedCustomerContact(null);
                        break;
                    case Shifts.TYPE:
                        AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(ctx).UploadShiftToServer(null);
                        break;

                    case MessagePanic.TYPE:
                        AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(ctx).UploadMessageToServer(null);
                        break;

                    default:
                        break;
                }
            }
        }).start();
    }
}


