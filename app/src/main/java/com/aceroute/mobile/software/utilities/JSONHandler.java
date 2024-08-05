package com.aceroute.mobile.software.utilities;

import android.content.Context;
import android.util.Log;

import com.aceroute.mobile.software.SplashII;
import com.aceroute.mobile.software.component.Bulkpubnub;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderMedia;
import com.aceroute.mobile.software.component.OrderNotes;
import com.aceroute.mobile.software.component.OrderPart;
import com.aceroute.mobile.software.component.OrderTask;
import com.aceroute.mobile.software.component.reference.Assets;
import com.aceroute.mobile.software.component.reference.AssetsType;
import com.aceroute.mobile.software.component.reference.ClientLocation;
import com.aceroute.mobile.software.component.reference.Customer;
import com.aceroute.mobile.software.component.reference.CustomerContact;
import com.aceroute.mobile.software.component.reference.CustomerType;
import com.aceroute.mobile.software.component.reference.MessagePanic;
import com.aceroute.mobile.software.component.reference.OrderTypeList;
import com.aceroute.mobile.software.component.reference.Parts;
import com.aceroute.mobile.software.component.reference.ServiceType;
import com.aceroute.mobile.software.component.reference.Shifts;
import com.aceroute.mobile.software.component.reference.Site;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.component.reference.Worker;
import com.aceroute.mobile.software.http.Api;
import com.aceroute.mobile.software.requests.PubnubRequest;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONHandler {

    /**
     * These are the json keys used for extracting the json value.
     */
    public static final String KEY_USER_ID = "u";
    public static final String KEY_USER_PUBNUBTIME = "s";
    public static final String KEY_KEY = "k";

    // this key used in both fetching and storing json.
    public static final String KEY_XML = "x";

    public static final String KEY_ACTION = "action";
    public static final String KEY = "key";
    public static final String KEY_OID = "oid";
    public static final String KEY_ID = "id";
    public static final String KEY_APP_EXIT = "appexit";
    public static final String KEY_CURR_TIME = "time";

    public static final String KEY_REQUEST_TYPE = "type";
    public static final String KEY_REQUEST_TYPE_POST = "post";
    public static final String KEY_REQUEST_TYPE_POST_MULTIPART = "postmulti";
    public static final int ERROR_OBJECT_TYPE = -1;
    public static final int ERROR_CODE_RESPONSE_SYNC_PAUSED = 10;
    public static final String JSON_SUCCESS = "{\"success\":\"true\"";
    public static final String JSON_FAILURE = "{\"success\":\"false\"";
    public static final String JSON_DATA = "\"data\":\"";
    public static final String JSON_DATA_END = "\"";
    public static final String JSON_END = "}";
    static int objectListlength = 1;
    private static String MESSAGE = "msg";
    private static String NUMBER = "phonenum";
    Context context;
    private long id;
    private String key[];
    private String xmlData;

    public JSONHandler(Context context) {
        this.context = context;
    }

    public static JSONObject getJsonObject(String string) throws ParseException {
        JSONParser parser = new JSONParser();
        Object object = parser.parse(string);
        JSONObject jsonObject = (JSONObject) object;
        return jsonObject;
    }

    public static JSONArray getJsonArray(String string) throws ParseException {
        JSONParser parser = new JSONParser();
        Object object = parser.parse(string);
        JSONArray jsonarray = (JSONArray) object;
        return jsonarray;
    }

    /**
     * Parsing and returning data from pubnub channel like x,k,u elements
     *
     * @throws ParseException
     */

    // This method is used for fetching x element from json object
    public static synchronized String getJSON_X_ELE(JSONObject jobject)
            throws ParseException {
        try {
            return jobject.get("x").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // This method is used for fetching x element from json object
    public static synchronized long getJSON_ID(JSONObject jobject)
            throws ParseException {
        try {
            return Long.parseLong(jobject.get(KEY_ID).toString());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static synchronized long getJSON_APPEXIT(JSONObject jobject)
            throws ParseException {
        try {
            return Long.parseLong(jobject.get(KEY_APP_EXIT).toString());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static synchronized String getJSON_K_ELE(JSONObject jobject)
            throws ParseException {
        try {
            return jobject.get("k").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static synchronized String getJSON_S_ELE(JSONObject jobject)
            throws ParseException {
        try {
            return jobject.get(KEY_USER_PUBNUBTIME).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static synchronized String getJSON_U_ELE(JSONObject jobject)
            throws ParseException {
        try {
            return jobject.get(KEY_USER_ID).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static synchronized PubnubRequest getJSONForPubNub(int objType,
                                                              int action, long id, String xml) {//YD
        return getJSONForPubNub(objType, action, id, xml, Api.INT_API_ACTION_NOPES);
    }

    @SuppressWarnings("unchecked")
    public static synchronized PubnubRequest getJSONForPubNub(int action,
                                                              int objecttype, long id, String xml, int extraAction) {//YD pubnub
        PubnubRequest jobject = new PubnubRequest();
        try {
            jobject.setObjecttype(action);// eg: add edit delete
            jobject.setIdPb(id);// eg: order id , ordertypeid
            jobject.setExtraAction(extraAction);//1003
            switch (objecttype) {
                case ClientLocation.TYPE:
                    jobject.setAction(ClientLocation.ACTION_CLIENT_LOCATION);
                    break;
                case Customer.TYPE:
                    jobject.setAction(Customer.ACTION_CUSTOMER_LIST);
                    break;
                case Order.TYPE:
                    jobject.setAction(Order.ACTION_ORDER);
                    break;
                case OrderTypeList.TYPE:
                    jobject.setAction(OrderTypeList.ACTION_ORDER_TYPE);
                    break;
                case Parts.TYPE:
                    jobject.setAction(Parts.ACTION_PART_TYPE);
                    break;
                case Parts.TYPE_UPDATE:
                    jobject.setAction(Parts.ACTION_PART_UPDATE_TYPE);
                    break;
                case ServiceType.TYPE:
                    jobject.setAction(ServiceType.ACTION_SERVICE_TYPE);
                    break;
                case ServiceType.TYPE_UPDATE:
                    jobject.setAction(ServiceType.ACTION_SERVICE_UPDATE_TYPE);
                    break;
                case Worker.TYPE:
                    jobject.setAction(Worker.ACTION_WORKER_LIST);
                    break;
                case CustomerType.TYPE:
                    jobject.setAction(CustomerType.ACTION_CUST_TYPE);
                    break;
                case Site.TYPE:
                    jobject.setAction(Site.ACTION_SITE);
                    break;
                case SiteType.TYPE:
                    jobject.setAction(SiteType.ACTION_SITE_TYPE);
                    break;
                case Shifts.PUBNUB_TYPE:
                    jobject.setAction(Shifts.ACTION_GET_SHIFTS);
                    break;
                // for pubnub bulk messages
                case Bulkpubnub.Bulk_type:
                    jobject.setAction(Bulkpubnub.ACTION_Bulk_ORDER);
                    break;
                case Api.INT_API_ACTION_HANDLE_EXTRA_PUBNUB:
                    Log.i(Utilities.TAG, "setting action "
                            + Api.API_ACTION_HANDLE_EXTRA_PUBNUB);
                    jobject.setAction(Api.API_ACTION_HANDLE_EXTRA_PUBNUB);
                    objecttype = Order.TYPE;
                    break;
                case Api.INT_API_ACTION_HANDLE_DISPLAYED_DATE_DATA:
                    Log.i(Utilities.TAG, "setting action "
                            + Api.API_ACTION_HANDLE_EXTRA_PUBNUB);
                    jobject.setAction(Api.API_ACTION_HANDLE_DISPLAY_DATE_DATA);
                    objecttype = Order.TYPE;
                    break;
                case OrderMedia.TYPE_PUBNUB_UPDATE:
                    jobject.setAction(OrderMedia.ACTION_META_UPDATE_TYPE);
                    break;
                case OrderNotes.TYPENOTE:
                    jobject.setAction(OrderNotes.ACTION_GET_NOTES);
                    break;
                case CustomerContact.TYPE_PUBNUB:
                    jobject.setAction(CustomerContact.ACTION_GET_CONTACT);
                    break;
                case Assets.TYPE_PUBNUB:
                    jobject.setAction(Assets.ACTION_GET_ASSETS);
                    break;
                case AssetsType.TYPE_PUBNUB_ASSET_TYPE:
                    jobject.setAction(AssetsType.ACTION_GET_ASSETS_TYPE);
                    break;
                case Order.TYPE_CUST_TOKEN:
                    jobject.setAction(Order.ACTION_CUSTOMER_HISTORY);
                    break;
                case Worker.PUBNUB_TYPE:
                    if (!SplashII.pubnublogout) {
                        SplashII.pubnublogout = true;
                        Log.d("whylogout","check"+jobject);
                        jobject.setAction(Worker.ACTION_WORKER_LOGOUT_PUBNUB);
                    }
                case MessagePanic.TYPE:
                    //jobject.setAction(MessagePanic.ACTION_WORKER_MESSAGE_PANIC);
                    break;
				/*case 59	:
						jobject.setAction(Api.API_ACTION_SYNCALLFOROFFLINE_PUbNUB);*/

                case Worker.PUBNUB_TYPE_NEW:
                    if (!SplashII.pubnublogout) {
                        Log.d("whylogout","ccccccccheck"+jobject);
                        SplashII.pubnublogout = true;
                        jobject.setAction(Worker.ACTION_WORKER_LOGOUT_PUBNUB);
                    }
                    break;

            }
            jobject.setObjtypeforpn(String.valueOf(objecttype)); // for order,part etc
            jobject.setXml(xml);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Log.d("whylogout","check"+jobject);
        return jobject;
    }

    public static Object[] getOrderTaskValuesFromJSON(Context context,
                                                      String dataString) {
        Object[] orderTaskList = null;
        try {
            orderTaskList = new Object[objectListlength];
            JSONObject dataObject = JSONHandler.getJsonObject(dataString);
            for (int i = 0; i < objectListlength; i++) {
                OrderTask orderTask = new OrderTask();
                orderTask.setId(Long.parseLong(dataObject.get(
                        OrderTask.ORDER_ID).toString()));
                orderTask.setOrder_task_id(Long.parseLong(dataObject.get(
                        OrderTask.ORDER_TASK_ID).toString()));
                orderTask.setTask_id(Long.parseLong(dataObject.get(
                        OrderTask.TASK_ID).toString()));

                orderTask.setAction_status(dataObject.get(OrderTask.ACTION_STATUS).toString());
                orderTask.setPriority(dataObject.get(OrderTask.PRIORITY).toString());
                orderTask.setTree_actualcount(dataObject.get(OrderTask.TREE_ACTUALCOUNT).toString());
                orderTask.setTree_alert(dataObject.get(OrderTask.TREE_ALERT).toString());
                orderTask.setTree_clearence(dataObject.get(OrderTask.TREE_CLEARENCE).toString());
                orderTask.setTree_comment(dataObject.get(OrderTask.TREE_COMMENT).toString());
                orderTask.setTree_cycle(dataObject.get(OrderTask.TREE_CYCLE).toString());
                orderTask.setTree_dia(dataObject.get(OrderTask.TREE_DIA).toString());
                orderTask.setTree_expcount(dataObject.get(OrderTask.TREE_EXPCOUNT).toString());
                orderTask.setTree_ht(dataObject.get(OrderTask.TREE_HT).toString());
                orderTask.setTree_msc(dataObject.get(OrderTask.TREE_MSC).toString());
                orderTask.setTree_note(dataObject.get(OrderTask.TREE_NOTE).toString());
                orderTask.setTree_owner(dataObject.get(OrderTask.TREE_OWNER).toString());
                orderTask.setTree_pcomment(dataObject.get(OrderTask.TREE_PCOMMENT).toString());
                orderTask.setTree_timespent(dataObject.get(OrderTask.TREE_TIMESPENT).toString());
                orderTask.setTree_tm(dataObject.get(OrderTask.TREE_TM).toString());
                orderTask.setTree_type(dataObject.get(OrderTask.TRIM_TYPE).toString());
                orderTask.setTree_geo(dataObject.get(OrderTask.TREE_GEO).toString());
                orderTask.setTree_accesspath(dataObject.get(OrderTask.TREE_ACCESSPATH).toString());
                orderTask.setTree_ct1(dataObject.get(OrderTask.TREE_CT1).toString());
                orderTask.setTree_ct2(dataObject.get(OrderTask.TREE_CT2).toString());
                orderTask.setTree_ct3(dataObject.get(OrderTask.TREE_CT3).toString());
                orderTask.setOrderEndDate(dataObject.get(OrderTask.ORDER_END_DATE).toString());
                orderTask.setOrderEndTime(dataObject.get(OrderTask.ORDER_END_TIME).toString());

                long updatetime = 0;
                try {
                    updatetime = Long.valueOf(dataObject.get(
                            OrderTask.ORDER_SERV_UPDATE_TIME).toString());
                } catch (Exception e) {
                    updatetime = Long.valueOf(dataObject.get(
                            OrderTask.ORDER_TIMESTAMP).toString());
                    e.printStackTrace();
                }
                if (updatetime == 0) {
                    updatetime = Long.valueOf(dataObject.get(
                            OrderTask.ORDER_TIMESTAMP).toString());
                }
                orderTask.setUpd_time(updatetime);
                orderTaskList[i] = orderTask;
            }
        } catch (Exception e) {
            Utilities.log(context,
                    "Exeception while filling OrderTask object : ");
            e.printStackTrace();
        }
        return orderTaskList;
    }

  /*  // by Mandeep for OrderTaskOld
    public static Object[] getOrderTaskOldValuesFromJSON(Context context, String dataString) {
        Object[] orderTaskOldList = null;
        try {
            orderTaskOldList = new Object[objectListlength];
            JSONObject dataObject = JSONHandler.getJsonObject(dataString);
            for (int i = 0; i < objectListlength; i++) {
                OrderTaskOld orderTasks = new OrderTaskOld();
                orderTasks.setOid(Long.parseLong(dataObject.get(OrderTaskOld.ORDER_ID).toString()));
                orderTasks.setOrder_task_id(Long.parseLong(dataObject.get(OrderTaskOld.ORDER_TASK_ID).toString()));
                orderTasks.setTask_type_id(Long.parseLong(dataObject.get(OrderTaskOld.ORDER_TASK_TYPE_ID).toString()));
                orderTasks.setOrder_task_RATE(dataObject.get(OrderTaskOld.ORDER_TASK_RATE).toString());

                long updatetime = 0;
                try {
                    updatetime = Long.valueOf(dataObject.get(OrderTaskOld.ORDER_SERV_UPDATE_TIME).toString());
                } catch (Exception e) {
                    updatetime = Long.valueOf(dataObject.get(OrderTaskOld.ORDER_TIMESTAMP).toString());
                    e.printStackTrace();
                }
                if (updatetime == 0) {
                    updatetime = Long.valueOf(dataObject.get(OrderTaskOld.ORDER_TIMESTAMP).toString());
                }
                orderTasks.setUpd_time(updatetime);
                orderTaskOldList[i] = orderTasks;
            }
        } catch (Exception e) {
            Utilities.log(context, "Exeception while filling OrderTaskOld object : ");
            e.printStackTrace();
        }
        return orderTaskOldList;
    }*/

    public static Object[] getOrderPartValuesFromJSON(Context context,
                                                      String dataString) {
        Object[] orderPartList = null;
        try {
            orderPartList = new Object[objectListlength];
            JSONObject dataObject = JSONHandler.getJsonObject(dataString);

            for (int i = 0; i < objectListlength; i++) {
                OrderPart orderPart = new OrderPart();
                orderPart.setOid(Long.parseLong(dataObject.get(
                        OrderPart.ORDER_ID).toString()));
                orderPart.setOrder_part_id(Long.parseLong(dataObject.get(
                        OrderPart.ORDER_PART_ID).toString()));
                orderPart.setPart_type_id(Long.parseLong(dataObject.get(
                        OrderPart.PART_TID).toString()));
                orderPart.setOrder_part_QTY(dataObject.get(
                        OrderPart.ORDER_PART_QTY).toString());
                orderPart.setPart_barcode(dataObject.get(
                        OrderPart.ORDER_BARCODE).toString());

                long updatetime = 0;
                try {
                    updatetime = Long.valueOf(dataObject.get(
                            OrderPart.ORDER_UPDATE_TIME).toString());
                } catch (Exception e) {
                    updatetime = Long.valueOf(dataObject.get(
                            OrderPart.ORDER_TIMESTAMP).toString());
                    e.printStackTrace();
                }
                if (updatetime == 0) {
                    updatetime = Long.valueOf(dataObject.get(
                            OrderPart.ORDER_TIMESTAMP).toString());
                }

                orderPart.setUpd_time(updatetime);

                orderPartList[i] = orderPart;
            }
        } catch (Exception e) {
            Utilities.log(context,
                    "Exeception while filling OrderPart object : ");
            e.printStackTrace();
        }
        return orderPartList;
    }

    public static Object[] getOrderMediaValuesFromJSON(Context context,
                                                       String dataString) {//YD DONE
        Object[] orderPartList = null;
        try {
            orderPartList = new Object[objectListlength];
            JSONObject dataObject = JSONHandler.getJsonObject(dataString);
            for (int i = 0; i < objectListlength; i++) {
                OrderMedia ordermedia = new OrderMedia();
                ordermedia.setId(Long.parseLong(dataObject.get(OrderMedia.ID)
                        .toString()));
                ordermedia.setOrderid(Long.parseLong(dataObject.get(
                        OrderMedia.ORDER_ID).toString()));
                ordermedia.setFile_desc(String.valueOf(dataObject.get(
                        OrderMedia.ORDER_FILE_DESC).toString()));
                Object temp = dataObject.get(OrderMedia.ORDER_GEO);
                if (temp != null)
                    ordermedia.setGeocode(dataObject.get(OrderMedia.ID)
                            .toString());
                else
                    ordermedia.setGeocode("0#0");
                ordermedia.setMediatype(Integer.valueOf(dataObject.get(
                        OrderMedia.ORDER_MEDIA_TYPE).toString()));
                ordermedia.setMimetype(String.valueOf(dataObject.get(
                        OrderMedia.ORDER_MEDIA_TYPE).toString()));
                ordermedia.setMetapath(dataObject.get(
                        OrderMedia.ORDER_META_PATH).toString());// vicky for he time being

                if (dataObject.get(OrderMedia.FORM_KEY.toString()) != null){
                    ordermedia.setFrmkey(dataObject.get(
                            OrderMedia.FORM_KEY).toString());
                } else {
                    ordermedia.setFrmkey("formkeyss");
                }
                if (dataObject.get(OrderMedia.FORM_FIELD_ID.toString()) != null){
                    ordermedia.setFrmfiledid(dataObject.get(
                            OrderMedia.FORM_FIELD_ID).toString());
                } else {
                    ordermedia.setFrmkey("formfiledkey");
                }
                long time = 0;
                try {
                    time = Long.valueOf(
                            dataObject.get(OrderMedia.ORDER_META_UPDATE_TIME).toString());
                } catch (Exception e) {
                    time = Long.valueOf(
                            dataObject.get(OrderMedia.ORDER_TIMESTAMP).toString());
                    e.printStackTrace();
                }
                if (time == 0) {
                    time = Long.valueOf(
                            dataObject.get(OrderMedia.ORDER_TIMESTAMP).toString());
                }

                ordermedia.setUpd_time(time);
                orderPartList[i] = ordermedia;
            }
        } catch (Exception e) {
            Utilities.log(context,
                    "Exeception while filling OrderMedia object : ");
            e.printStackTrace();
        }
        return orderPartList;
    }

    public static Object[] getOrderNotesValuesFromJSON(Context context,
                                                       String dataString) {
        Object[] orderNotesList = null;
        try {
            orderNotesList = new Object[objectListlength];
            JSONObject dataObject = JSONHandler.getJsonObject(dataString);
            for (int i = 0; i < objectListlength; i++) {
                OrderNotes orderNote = new OrderNotes();
                orderNote.setId(Long.parseLong(dataObject.get(
                        OrderNotes.ORDER_ID).toString()));
                orderNote.setOrdernote(dataObject.get(Order.KEY_VALUE)
                        .toString());
                orderNotesList[i] = orderNote;
            }
        } catch (Exception e) {
            Utilities.log(context,
                    "Exeception while filling OrderNotes object : ");
            e.printStackTrace();
        }
        return orderNotesList;
    }

    public static JSONObject[] getPubnubList(String response) {

        JSONArray outterArr, innerArr;
        try {
            outterArr = getJsonArray(response);
            innerArr = (JSONArray) outterArr.get(0);
            JSONObject[] listOfpubnub = new JSONObject[innerArr.size()];
            for (int i = 0; i < innerArr.size(); i++) {
                listOfpubnub[i] = (JSONObject) innerArr.get(i);
            }
            //if(listOfpubnub!=null && listOfpubnub.length>0)
            return listOfpubnub;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAction(JSONObject jsonObject) throws ParseException,
            JSONException {
        Object object = null;
        try {
            object = jsonObject.get(KEY_ACTION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (object != null) {
            String action = (String) object;
            Utilities.log(context, "return action : " + action);
            return action;
        } else {
            Utilities.log(context, "ERROR : No json found.");
        }
        return null;
    }

    public long getTime(JSONObject jsonobject) {
        try {
            Log.i(Utilities.TAG, "checking value in getObjectType : "
                    + jsonobject.get(KEY_CURR_TIME).toString());
            Long val = (Long) jsonobject.get(KEY_CURR_TIME);
            return Long.valueOf(val);
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR_OBJECT_TYPE;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String[] getKey() {
        return key;
    }

    public void setKey(String key[]) {
        this.key = key;
    }

}
