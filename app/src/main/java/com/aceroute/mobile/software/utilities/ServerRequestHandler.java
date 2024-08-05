package com.aceroute.mobile.software.utilities;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderMedia;
import com.aceroute.mobile.software.component.OrderNotes;
import com.aceroute.mobile.software.component.OrderPart;
import com.aceroute.mobile.software.component.OrderTask;
import com.aceroute.mobile.software.component.OrdersMessage;
import com.aceroute.mobile.software.component.reference.Assets;
import com.aceroute.mobile.software.component.reference.CheckinOut;
import com.aceroute.mobile.software.component.reference.CustomerContact;
import com.aceroute.mobile.software.component.reference.Form;
import com.aceroute.mobile.software.component.reference.MessagePanic;
import com.aceroute.mobile.software.component.reference.Shifts;
import com.aceroute.mobile.software.component.reference.Site;
import com.aceroute.mobile.software.database.DBHandler;
import com.aceroute.mobile.software.http.Api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ServerRequestHandler {

    Context context;
    static final String KEY_EVENT = "event";

    // keys helps to parsing success or fail response from server
    public static final String KEY_DATA = "data";
    public static final String KEY_DATA_SUCCESS = "success";
    public static final String KEY_DATA_ID = "id";
    public static final String KEY_DATA_RESP_FAIL = "false";

    public static final String XML_EMPTY_DATA = "<data></data>";
    public static final String XML_NO_DATA = "<data><success>false</success><id>0</id></data>";
    public static final String XML_DATA_ERROR = "<data><success>false</success><id>0</id><errorcode>";
    public static final String XML_DATA_ERROR_END = "</errorcode></data>";
    public static final String XML_DATA_SUCCESS_MESSAGE = "<data><success>true</success><id>0</id></data>";

    public ServerRequestHandler(Context context) {
        this.context = context;
    }

    public NodeList parseXML(String xmlString, String key) {
        Document doc = getDomElement(xmlString);
        doc.getElementsByTagName(key);
        NodeList nl = doc.getElementsByTagName(key);
        return nl;
    }

    public synchronized Document getDomElement(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);
        } catch (ParserConfigurationException e) {
            Utilities.log(context, "Error: " + e.getMessage());
            return null;
        } catch (SAXException e) {
            Utilities.log(context, "Error: " + e.getMessage());
            return null;
        } catch (IOException e) {
            Utilities.log(context, "Error: " + e.getMessage());
            return null;
        }
        return doc;
    }

    public String getValue(Element item, String str) {
        try {
            NodeList n = item.getElementsByTagName(str);
            String value = this.getElementValue(n.item(0));
            if (value == null || !(value.length() > 0))
                return "";
            else
                return value;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public final String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child
                        .getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    public static Map<String, String> createparamsForRequest(Cursor cursor, String action,
                                                             Context context, Map<String, String> params1) {
        Map<String, String> params = null;

        if (action.equals(Order.ACTION_SAVE_ORDER))
            params = createparamsForOrder(cursor, action, params1, context);
        else if (action.equals(OrderPart.ACTION_SAVE_ORDER_PART))
            params = createparamsForOrderParts(cursor, action, params1);
        else if (action.equals(OrderTask.ACTION_SAVE_ORDER_TASK))
            params = createparamsForOrderServices(cursor, action, params1);
        else if (action.equals(OrderPart.ACTION_DELETE_ORDER_PART))
            params = createparamsForOrderPartsDelete(cursor, action, params1);
        else if (action.equals(OrderTask.ACTION_DELETE_ORDER_TASK))
            params = createparamsForOrderServicesDelete(cursor, action, params1);
        else if (action.equals(Form.ACTION_DELETE_ORDER_FORM))
            params = createparamsForOrderFormDelete(cursor, action, params1);
        else if (action.equals(OrderMedia.ACTION_MEDIA_SAVE))
            params = createparamsForOrderMeta(cursor, action, params1);
        else if (action.equals(OrderMedia.ACTION_MEDIA_DELETE))
            params = createparamsForOrderMetaDelete(cursor, action, params1);
        else if (action.equals(OrderNotes.ACTION_NOTES_SAVE))
            params = createparamsForOrderNotes(cursor, action, params1);
        else if (action.equals(Site.ACTION_SAVE_SITE))
            params = createparamsForSite(cursor, action, params1);
        else if (action.equals(Site.ACTION_EDIT_SITE))
            params = createparamsForEditSite(cursor, action, params1);
        else if (action.equals(Site.ACTION_DELETE_SITE))
            params = createparamsForDeleteSite(cursor, action, params1);
        else if (action.equals(CheckinOut.CC_ACTION))
            params = createparamsForCheckinout(cursor, action, params1);
        else if (action.equals(OrdersMessage.ACTION_MESSAGE_SAVE))
            params = createparamsForOrderMessage(cursor, action, params1);
        else if (action.equals(MessagePanic.ACTION_WORKER_MESSAGE_PANIC))
            params = createparamsForMessage(cursor, action, params1);
        else if (action.equals(CustomerContact.ACTION_CONTACT_EDIT) || action.equals(CustomerContact.ACTION_SAVE_CONTACT))
            params = createparamsForCustContUpdate(cursor, action, params1);
        else if (action.equals(Assets.ACTION_SAVE_ORDER_ASSETS))
            params = createparamsForOrderAssets(cursor, action, params1);
        else if (action.equals(Shifts.ACTION_SYNC_SHIFTS) || action.equals(Shifts.ACTION_EDIT_SHIFTS) || action.equals(Shifts.ACTION_DELETE_SHIFTS))
            params = createparamsForShift(cursor, action, params1);
        else if (action.equals(Shifts.DELETE_MULTIPLE_SHIFTS))
            params = createparamsForDeleteShift(cursor, action, params1);
        else if(action.equals(Form.ACTION_SAVE_FORM))
            params=createparamsForFormSave(cursor,action,params1);
        return params;
    }

    private static Map<String, String> createparamsForFormSave(Cursor cursor, String action, Map<String, String> params) {
        if (cursor != null) {
            params.put(JSONHandler.KEY_ACTION, Form.ACTION_SAVE_FORM);

            String oid = DBHandler.getvaluefromCursor(cursor, Form.FORM_OID);
            String WhereClause = "id =" + oid + " OR localid =" + oid;
            String Oid = DBHandler.getServerOrderId(DBHandler.orderListTable, Order.ORDER_ID, WhereClause);
            params.put(Form.FORM_OID, Oid);

            params.put(Form.FORM_ID, DBHandler.getvaluefromCursor(cursor, Form.FORM_ID));
            params.put(Form.FORM_TID, DBHandler.getvaluefromCursor(cursor, Form.FORM_TID));
            params.put(Form.FORM_FDATA, DBHandler.getvaluefromCursor(cursor,Form.FORM_FDATA));
            params.put(Form.FORM_STMP, DBHandler.getvaluefromCursor(cursor,Form.FORM_STMP));
            params.put(Form.FORM_KEY_ONLY, DBHandler.getvaluefromCursor(cursor,Form.FORM_KEY_ONLY));
            return params;
        }
        return null;
    }

    private static Map<String, String> createparamsForDeleteShift(Cursor cursor, String action, Map<String, String> params1) {
        if (cursor != null) {
            params1.put(JSONHandler.KEY_ACTION, action);
            params1.put(Shifts.SHIFT_SEND_TAG, XMLHandler.createXMLForDeleteSHIFT(cursor));
            return params1;
        }
        return null;

    }

    public static Map<String, String> createparamsForOrder(Cursor cursor, String action, Map<String, String> params, Context context) {

        try {
            if (cursor != null) {
                params.put(JSONHandler.KEY_ACTION, action);
                params.put(Order.ORDER_ID, DBHandler.getvaluefromCursor(cursor, Order.ORDER_ID));
                params.put(Order.ORDER_CUSTOMER_ID, DBHandler.getvaluefromCursor(cursor, Order.ORDER_CUSTOMER_ID));
                params.put(Order.ORDER_CUSTOMER_SITE_ID, DBHandler.getvaluefromCursor(cursor, Order.ORDER_CUSTOMER_SITE_ID));
                params.put(Order.ORDER_CUSTOMER_CONTACT_ID,
                        DBHandler.getvaluefromCursor(cursor, Order.ORDER_CUSTOMER_CONTACT_ID));
                //params.put(Order.ORDER_CUSTOMER_SITE_C_GEOCODE, DBHandler.getvaluefromCursor(cursor,Order.ORDER_CUSTOMER_SITE_GEOCODE));
                params.put(Order.ORDER_CUSTOMER_SITE_C_GEOCODE, Utilities.getLocation(context));
                params.put(Order.ORDER_START_DATE, DBHandler.getvaluefromCursor(cursor, Order.ORDER_START_DATE));
                params.put(Order.ORDER_END_DATE, DBHandler.getvaluefromCursor(cursor, Order.ORDER_END_DATE));
                String temp = DBHandler.getvaluefromCursor(cursor, Order.ORDER_STATUS_SUMMARY);
                String temp1 = temp.replaceAll("\\n", "\n");
                params.put(Order.ORDER_STATUS_SUMMARY, temp1);

                params.put(Order.ORDER_START_TIME, DBHandler.getvaluefromCursor(cursor, Order.ORDER_START_TIME));
                params.put(Order.ORDER_END_TIME, DBHandler.getvaluefromCursor(cursor, Order.ORDER_END_TIME));
                params.put(Order.ORDER_NAME, DBHandler.getvaluefromCursor(cursor, Order.ORDER_NAME));
                params.put(Order.ORDER_STATUS_PO_NUMBER, DBHandler.getvaluefromCursor(cursor, Order.ORDER_STATUS_PO_NUMBER));

                temp = DBHandler.getvaluefromCursor(cursor, Order.ORDER_STATUS_ORDER_TYPE_ID);
                if (temp == null || temp.equals("0"))
                    temp = "";
                params.put(Order.ORDER_STATUS_ORDER_TYPE_ID, temp);
                params.put(Order.ORDER_STATUS_PRIORITY_TYPE_ID, DBHandler.getvaluefromCursor(cursor, Order.ORDER_STATUS_PRIORITY_TYPE_ID));
                temp = DBHandler.getvaluefromCursor(cursor, Order.ORDER_STATUS_INVOICE_NUMBER);
                if (temp == null || temp.equals("0"))
                    temp = "";
                params.put(Order.ORDER_STATUS_INVOICE_NUMBER, temp);
                params.put(Order.ORDER_STATUS_ID, DBHandler.getvaluefromCursor(cursor, Order.ORDER_STATUS_ID));
                params.put(Order.ORDER_NOTES, DBHandler.getvaluefromCursor(cursor, Order.ORDER_NOTES));

                temp = DBHandler.getvaluefromCursor(cursor, Order.ORDER_FLG);
                if (temp == null || temp.equals("0"))
                    temp = "";
                params.put(Order.ORDER_FLG, temp);
                //params.put(Order.ORDER_STATUS_PRIMARY_WORKER_ID, DBHandler.getvaluefromCursor(cursor,Order.ORDER_STATUS_PRIMARY_WORKER_ID)); YD commenting becasuse giving eror at http request
                params.put(Order.ORDER_RESET, DBHandler.getvaluefromCursor(cursor, Order.ORDER_RESET));

                temp = DBHandler.getvaluefromCursor(cursor, Order.ORDER_TYPE);
                if (temp == null || temp.equals("0"))
                    temp = "";
                params.put(Order.ORDER_TYPE, temp);
                params.put(Order.ORDER_RPT, DBHandler.getvaluefromCursor(cursor, Order.ORDER_RPT));
                params.put(Order.ORDER_EST, DBHandler.getvaluefromCursor(cursor, Order.ORDER_EST));
                params.put(Order.ORDER_LST, DBHandler.getvaluefromCursor(cursor, Order.ORDER_LST));
                params.put(Order.ORDER_WKFUPD, DBHandler.getvaluefromCursor(cursor, Order.ORDER_WKFUPD));
                params.put(Order.ORDER_EPOCH_TIME, DBHandler.getvaluefromCursor(cursor, Order.ORDER_EPOCH_TIME));
                params.put(Order.ORDER_CUSTOMER_MEDIA_COUNT, DBHandler.getvaluefromCursor(cursor, Order.ORDER_CUSTOMER_MEDIA_COUNT));
                params.put(Order.ORDER_CUSTOMER_PART_COUNT, DBHandler.getvaluefromCursor(cursor, Order.ORDER_CUSTOMER_PART_COUNT));
                params.put(Order.ORDER_CUSTOMER_FORM_COUNT, DBHandler.getvaluefromCursor(cursor, Order.ORDER_CUSTOMER_FORM_COUNT));
                params.put(Order.ORDER_AUDIO, DBHandler.getvaluefromCursor(cursor, Order.ORDER_AUDIO));
                params.put(Order.ORDER_IMG, DBHandler.getvaluefromCursor(cursor, Order.ORDER_IMG));
                params.put(Order.ORDER_SIG, DBHandler.getvaluefromCursor(cursor, Order.ORDER_SIG));
                params.put(Order.ORDER_NOTE, DBHandler.getvaluefromCursor(cursor, Order.ORDER_NOTE));
                params.put(Order.ORDER_ALERT, DBHandler.getvaluefromCursor(cursor, Order.ORDER_ALERT));
                params.put(Order.ORDER_CUSTOMER_CTID, DBHandler.getvaluefromCursor(cursor, Order.ORDER_CUSTOMER_CTID));

                params.put(DBHandler.modifiedField, DBHandler.getvaluefromCursor(cursor, DBHandler.modifiedField));
            }

            return params;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> createparamsForOrderParts(Cursor cursor, String action, Map<String, String> params) {

        if (cursor != null) {
            params.put(JSONHandler.KEY_ACTION, action);
            String oid = DBHandler.getvaluefromCursor(cursor, OrderPart.ORDER_ID);
            String WhereClause = "id =" + oid + " OR localid =" + oid;
            String Oid = DBHandler.getServerOrderId(DBHandler.orderListTable, Order.ORDER_ID, WhereClause);
            params.put(OrderPart.ORDER_ID, Oid);

            params.put(OrderPart.ORDER_PART_QTY, DBHandler.getvaluefromCursor(cursor, OrderPart.ORDER_PART_QTY));
            params.put(OrderPart.ORDER_BARCODE, DBHandler.getvaluefromCursor(cursor, OrderPart.ORDER_BARCODE));
            ;
            params.put(OrderPart.ORDER_PART_ID, DBHandler.getvaluefromCursor(cursor, OrderPart.ORDER_PART_ID));
            params.put(OrderPart.PART_TID, DBHandler.getvaluefromCursor(cursor, OrderPart.PART_TID));
            params.put(OrderPart.ORDER_TIMESTAMP, DBHandler.getvaluefromCursor(cursor, OrderPart.ORDER_UPDATE_TIME));
            params.put(DBHandler.modifiedField, DBHandler.getvaluefromCursor(cursor, DBHandler.modifiedField));

            return params;
        }
        return null;
    }

    public static Map<String, String> createparamsForOrderPartsDelete(Cursor cursor, String action, Map<String, String> params) {

        if (cursor != null) {

            params.put(JSONHandler.KEY_ACTION, action);
            params.put(OrderPart.ORDER_PART_ID, DBHandler.getvaluefromCursor(cursor, OrderPart.ORDER_PART_ID));
            params.put(OrderPart.ORDER_ID, DBHandler.getvaluefromCursor(cursor, OrderPart.ORDER_ID));

            return params;
        }
        return null;
    }

    public static Map<String, String> createparamsForOrderAssets(Cursor cursor, String action, Map<String, String> params) {

        if (cursor != null) {

            params.put(JSONHandler.KEY_ACTION, action);
            params.put(Assets.ASSET_ID, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_ID));
            params.put(Assets.ASSET_OID, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_OID));
            params.put(Assets.ASSET_FTYPE_ID, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_FTYPE_ID));
            params.put(Assets.ASSET_FDATA, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_FDATA));
            params.put(Assets.ASSET_TSTMP, "" + Utilities.getCurrentTime());
            params.put(Assets.ASSET_CID, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_CID));
            params.put(Assets.ASSET_GEO, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_GEO));
            /*params.put(Assets.ASSET_TYPE_ID, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_TYPE_ID));
            params.put(Assets.ASSET_TYPE_ID2, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_TYPE_ID2));
            params.put(Assets.ASSET_STAT, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_STAT));
            params.put(Assets.ASSET_PID, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_PID));
            params.put(Assets.ASSET_CONT1, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_CONT1));
            params.put(Assets.ASSET_CONT2, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_CONT2));

            params.put(Assets.ASSET_NUM1, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_NUM1));
            params.put(Assets.ASSET_NUM2, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_NUM2));
            params.put(Assets.ASSET_NUM3, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_NUM3));
            params.put(Assets.ASSET_NUM4, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_NUM4));
            params.put(Assets.ASSET_NUM5, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_NUM5));
            params.put(Assets.ASSET_NUM6, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_NUM6));

            params.put(Assets.ASSET_NOTE1, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_NOTE1));
            params.put(Assets.ASSET_NOTE2, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_NOTE2));
            params.put(Assets.ASSET_NOTE3, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_NOTE3));
            params.put(Assets.ASSET_NOTE4, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_NOTE4));
            params.put(Assets.ASSET_NOTE5, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_NOTE5));
            params.put(Assets.ASSET_NOTE6, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_NOTE6));

            params.put(Assets.ASSET_CT1, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_CT1));
            params.put(Assets.ASSET_CT2, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_CT2));
            params.put(Assets.ASSET_CT3, DBHandler.getvaluefromCursor(cursor, Assets.ASSET_CT3));*///YD 2020

            return params;
        }
        return null;
    }

    public static Map<String, String> createparamsForOrderNotes(Cursor cursor, String action, Map<String, String> params) {

        if (cursor != null) {
            params.put(JSONHandler.KEY_ACTION, Order.ACTION_SAVE_ORDER_FLD);
            params.put(OrderNotes.ORDER_ID, DBHandler.getvaluefromCursor(cursor, OrderNotes.ORDER_ID));
            params.put(OrderNotes.ORDER_NOTE_VALUE, DBHandler.getvaluefromCursor(cursor, OrderNotes.ORDER_NOTE));
            params.put(Order.KEY_NAME, OrderNotes.ORDER_NOTE_FIELDNAME);
            params.put(OrderNotes.ORDER_CUSTOMER_SITE_C_GEOCODE, "");
            params.put(OrderNotes.ORDER_TMSTAMP, "");


            return params;
        }
        return null;
    }

    public static Map<String, String> createparamsForShift(Cursor cursor, String action, Map<String, String> params) {

        if (cursor != null) {
            params.put(JSONHandler.KEY_ACTION, action);
            if (action.equals(Shifts.ACTION_SYNC_SHIFTS)) {
                params.put(Shifts.SHIFT_SEND_TAG, XMLHandler.createXMLForSHIFTList(cursor));
            } else if (action.equals(Shifts.ACTION_DELETE_SHIFTS)) {
                params.put("id", String.valueOf(cursor.getLong(0)));
            } else if (action.equals(Shifts.ACTION_EDIT_SHIFTS)) {
                params.put("id", String.valueOf(cursor.getLong(0)));
                params.put("lid",String.valueOf(cursor.getLong(5)));
//                String lid = "";
//                if (cursor.getString(5) != null) {
//                    lid = String.valueOf(cursor.getLong(5));
//                } else
//                    lid = String.valueOf(cursor.getLong(5));
//                params.put("lid", lid);
                String nm = "";
                if (cursor.getString(2) != null) {
                    nm = cursor.getString(2);
                }
                params.put("nm", String.valueOf(nm));
                params.put("tmslot", String.valueOf(cursor.getString(3)));
                params.put("brkslot", String.valueOf(cursor.getString(8)));
                String adr = "";
                if (cursor.getString(9) != null) {
                    adr = cursor.getString(9);
                }
                String tid = "";
                if (cursor.getString(1) != null) {
                    tid = cursor.getString(1);
                }
                String terri="";
                if (cursor.getString(10) != null) {
                    terri = cursor.getString(10);
                }
                params.put("adr", String.valueOf(adr));
                params.put("terrgeo",String.valueOf(terri));
                params.put("tid", String.valueOf(tid));
            }
            return params;
        }
        return null;
    }

    public static Map<String, String> createparamsForSite(Cursor cursor, String action, Map<String, String> params) {

        if (cursor != null) {
            params.put(JSONHandler.KEY_ACTION, action);
            params.put(Site.SITE_ID, DBHandler.getvaluefromCursor(cursor, Site.SITE_ID));
            params.put(Site.SITE_ADDRESS, DBHandler.getvaluefromCursor(cursor, Site.SITE_ADDRESS));
            params.put(Site.SITE_ADR2, DBHandler.getvaluefromCursor(cursor, Site.SITE_ADR2));// YD check if adr2 is causing any issue while editing site
            params.put(Site.SITE_CID, DBHandler.getvaluefromCursor(cursor, Site.SITE_CID));
            params.put(Site.SITE_GEO, DBHandler.getvaluefromCursor(cursor, Site.SITE_GEO));
            params.put(Site.SITE_NAME, DBHandler.getvaluefromCursor(cursor, Site.SITE_NAME));
            params.put(Site.SITE_TSTMP, DBHandler.getvaluefromCursor(cursor, Site.SITE_UPD));
            params.put(Site.SITE_DTL, DBHandler.getvaluefromCursor(cursor, Site.SITE_DTL));
            params.put(Site.SITE_TYPE_ID, DBHandler.getvaluefromCursor(cursor, Site.SITE_TYPE_ID));

            return params;
        }
        return null;
    }


    public static Map<String, String> createparamsForEditSite(Cursor cursor, String action, Map<String, String> params) {

        if (cursor != null) {
            params.put(JSONHandler.KEY_ACTION, action);
            params.put(Site.SITE_ID, DBHandler.getvaluefromCursor(cursor, Site.SITE_ID));
            params.put(Site.SITE_ADDRESS, DBHandler.getvaluefromCursor(cursor, Site.SITE_ADDRESS));
            params.put(Site.SITE_ADR2, DBHandler.getvaluefromCursor(cursor, Site.SITE_ADR2));
            params.put(Site.SITE_NAME, DBHandler.getvaluefromCursor(cursor, Site.SITE_NAME));
            params.put(Site.SITE_DTL, DBHandler.getvaluefromCursor(cursor, Site.SITE_DTL));
            params.put(Site.SITE_GEO, DBHandler.getvaluefromCursor(cursor, Site.SITE_GEO));

            return params;
        }
        return null;
    }

    public static Map<String, String> createparamsForDeleteSite(Cursor cursor, String action, Map<String, String> params) {

        if (cursor != null) {
            params.put(JSONHandler.KEY_ACTION, action);
            params.put(Site.SITE_ID, DBHandler.getvaluefromCursor(cursor, Site.SITE_ID));

            return params;
        }
        return null;
    }

    public static Map<String, String> createparamsForCheckinout(Cursor cursor, String action, Map<String, String> params) {

        if (cursor != null) {
            params.put(JSONHandler.KEY_ACTION, action);
            params.put(CheckinOut.CC_GEO, DBHandler.getvaluefromCursor(cursor, CheckinOut.CC_GEO));
            params.put(CheckinOut.CC_TID, DBHandler.getvaluefromCursor(cursor, CheckinOut.CC_TID));
            params.put(CheckinOut.CC_TSTAMP, DBHandler.getvaluefromCursor(cursor, CheckinOut.CC_TSTAMP));
            params.put("adr", "");
            if (DBHandler.getvaluefromCursor(cursor, CheckinOut.CC_LSTOID) != null){
                params.put(CheckinOut.CC_LSTOID, DBHandler.getvaluefromCursor(cursor, CheckinOut.CC_LSTOID));



            } else {
                params.put(CheckinOut.CC_LSTOID, "00000");

            }
            if (DBHandler.getvaluefromCursor(cursor, CheckinOut.CC_NXTOID) != null){
                params.put(CheckinOut.CC_NXTOID, DBHandler.getvaluefromCursor(cursor, CheckinOut.CC_NXTOID));

            } else {
                params.put(CheckinOut.CC_NXTOID, "00000");

            }

            return params;
        }
        return null;
    }

    public static Map<String, String> createparamsForOrderMessage(Cursor cursor, String action, Map<String, String> params) {

        if (cursor != null) {
            params.put(JSONHandler.KEY_ACTION, action);
            params.put(OrdersMessage.MESSAGE_TYPE_ID, DBHandler.getvaluefromCursor(cursor, OrdersMessage.MESSAGE_TYPE_ID));
            params.put(OrdersMessage.MESSAGE_ORDER_ID, DBHandler.getvaluefromCursor(cursor, OrdersMessage.MESSAGE_ORDER_ID));
            params.put(OrdersMessage.MESSAGE_CUST_ID, DBHandler.getvaluefromCursor(cursor, OrdersMessage.MESSAGE_CUST_ID));
            params.put(OrdersMessage.MESSAGE_CONTACT_ID, DBHandler.getvaluefromCursor(cursor, OrdersMessage.MESSAGE_CONTACT_ID));
            params.put(OrdersMessage.MESSAGE_TELL, DBHandler.getvaluefromCursor(cursor, OrdersMessage.MESSAGE_TELL));
            params.put(OrdersMessage.MESSAGE_TIMEZONE, DBHandler.getvaluefromCursor(cursor, OrdersMessage.MESSAGE_TIMEZONE));
            params.put(OrdersMessage.MESSAGE_GEO, DBHandler.getvaluefromCursor(cursor, OrdersMessage.MESSAGE_GEO));
            params.put(OrdersMessage.MESSAGE_TIMESTAMP, DBHandler.getvaluefromCursor(cursor, OrdersMessage.MESSAGE_TIMESTAMP));

            return params;
        }
        return null;
    }

    public static Map<String, String> createparamsForMessage(Cursor cursor, String action, Map<String, String> params) {

        if (cursor != null) {
            params.put(JSONHandler.KEY_ACTION, action);
            params.put(MessagePanic.MESSAGE_TYPE_ID, DBHandler.getvaluefromCursor(cursor, MessagePanic.MESSAGE_TYPE_ID));
            params.put(MessagePanic.MESSAGE_ORDER_ID, DBHandler.getvaluefromCursor(cursor, MessagePanic.MESSAGE_ORDER_ID));
            params.put(MessagePanic.MESSAGE_GEO, DBHandler.getvaluefromCursor(cursor, MessagePanic.MESSAGE_GEO));
            params.put(MessagePanic.MESSAGE_TIMESTAMP, DBHandler.getvaluefromCursor(cursor, MessagePanic.MESSAGE_TIMESTAMP));
            params.put(MessagePanic.MESSAGE_MESSAGE, DBHandler.getvaluefromCursor(cursor, MessagePanic.MESSAGE_MESSAGE));

            return params;
        }
        return null;
    }

    public static Map<String, String> createparamsForCustContUpdate(Cursor cursor, String action, Map<String, String> params) {

        if (cursor != null) {
            params.put(JSONHandler.KEY_ACTION, action);
            params.put(CustomerContact.CONTACT_ID, DBHandler.getvaluefromCursor(cursor, CustomerContact.CONTACT_ID));
            params.put(CustomerContact.CONTACT_NAME, DBHandler.getvaluefromCursor(cursor, CustomerContact.CONTACT_NAME));
            params.put(CustomerContact.CONTACT_TEL, DBHandler.getvaluefromCursor(cursor, CustomerContact.CONTACT_TEL));
            params.put(CustomerContact.CONTACT_TYPE, DBHandler.getvaluefromCursor(cursor, CustomerContact.CONTACT_TYPE));
            params.put(CustomerContact.CUSTOMER_ID, DBHandler.getvaluefromCursor(cursor, CustomerContact.CUSTOMER_ID));
            params.put(CustomerContact.CONTACT_EML, DBHandler.getvaluefromCursor(cursor, CustomerContact.CONTACT_EML));

            return params;
        }
        return null;
    }

    public static Map<String, String> createparamsForOrderServices(Cursor cursor, String action, Map<String, String> params) {

        if (cursor != null) {
            params.put(JSONHandler.KEY_ACTION, action);

            String oid = DBHandler.getvaluefromCursor(cursor, OrderTask.ORDER_ID);
            String WhereClause = "id =" + oid + " OR localid =" + oid;
            String Oid = DBHandler.getServerOrderId(DBHandler.orderListTable, Order.ORDER_ID, WhereClause);

            params.put(OrderTask.ORDER_ID, Oid);
            params.put(OrderTask.ORDER_TASK_ID, DBHandler.getvaluefromCursor(cursor, OrderTask.ORDER_TASK_ID));
            // params.put(OrderTask.ORDER_TASK_TYPE_ID,
            //  DBHandler.getvaluefromCursor(cursor,OrderTask.ORDER_TASK_TYPE_ID).toString());
            params.put(OrderTask.TASK_ID, DBHandler.getvaluefromCursor(cursor, OrderTask.TASK_ID));
            // params.put(OrderTask.ORDER_TIMESTAMP,

            params.put(OrderTask.ORDER_TIMESTAMP, DBHandler.getvaluefromCursor(cursor, OrderTask.ORDER_SERV_UPDATE_TIME));
            params.put(DBHandler.modifiedField, DBHandler.getvaluefromCursor(cursor, DBHandler.modifiedField));

            params.put(OrderTask.TREE_CYCLE, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_CYCLE));
            params.put(OrderTask.TRIM_TYPE, DBHandler.getvaluefromCursor(cursor, OrderTask.TRIM_TYPE));
            params.put(OrderTask.PRIORITY, DBHandler.getvaluefromCursor(cursor, OrderTask.PRIORITY));
            params.put(OrderTask.ACTION_STATUS, DBHandler.getvaluefromCursor(cursor, OrderTask.ACTION_STATUS));
            params.put(OrderTask.TREE_OWNER, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_OWNER));
            params.put(OrderTask.TREE_HT, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_HT));
            params.put(OrderTask.TREE_DIA, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_DIA));
            params.put(OrderTask.TREE_CLEARENCE, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_CLEARENCE));
            params.put(OrderTask.TREE_EXPCOUNT, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_EXPCOUNT));
            params.put(OrderTask.TREE_ACTUALCOUNT, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_ACTUALCOUNT));
            params.put(OrderTask.TREE_TIMESPENT, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_TIMESPENT));
            params.put(OrderTask.TREE_TM, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_TM));
            params.put(OrderTask.TREE_MSC, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_MSC));
            params.put(OrderTask.TREE_COMMENT, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_COMMENT));
            params.put(OrderTask.TREE_PCOMMENT, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_PCOMMENT));
            params.put(OrderTask.TREE_ALERT, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_ALERT));
            params.put(OrderTask.TREE_NOTE, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_NOTE));
            params.put(OrderTask.TREE_GEO, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_GEO));
            params.put(OrderTask.TREE_ACCESSPATH, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_ACCESSPATH));
            params.put(OrderTask.TREE_CT1, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_CT1));
            params.put(OrderTask.TREE_CT2, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_CT2));
            params.put(OrderTask.TREE_CT3, DBHandler.getvaluefromCursor(cursor, OrderTask.TREE_CT3));

            return params;
        }
        return null;
    }

    public static Map<String, String> createparamsForOrderServicesDelete(Cursor cursor, String action, Map<String, String> params) {

        if (cursor != null) {
            params.put(JSONHandler.KEY_ACTION, action);
            params.put(OrderTask.ORDER_TASK_ID, DBHandler.getvaluefromCursor(cursor, OrderTask.ORDER_TASK_ID));
            params.put(OrderTask.ORDER_ID, DBHandler.getvaluefromCursor(cursor, OrderTask.ORDER_ID));
            return params;
        }
        return null;
    }

    public static Map<String, String> createparamsForOrderFormDelete(Cursor cursor, String action, Map<String, String> params) {

        if (cursor != null) {
            params.put(JSONHandler.KEY_ACTION, action);
            params.put(Form.FORM_ID, DBHandler.getvaluefromCursor(cursor, Form.FORM_ID));
            params.put(Form.FORM_OID, DBHandler.getvaluefromCursor(cursor, Form.FORM_OID));
            return params;
        }
        return null;
    }

    public static Map<String, String> createparamsForOrderMeta(Cursor cursor, String action, Map<String, String> params) {


        params.put(JSONHandler.KEY_ACTION,
                OrderMedia.ACTION_MEDIA_SAVE);
        params.put(OrderMedia.ID, DBHandler.getvaluefromCursor(cursor, OrderMedia.ID));
        String tid = DBHandler.getvaluefromCursor(cursor, OrderMedia.ORDER_MEDIA_TYPE)
                .toString();
        params.put(OrderMedia.ORDER_MEDIA_TYPE, tid);
        // params.put(OrderMedia.ORDER_GEO,
        // dataObject.get(OrderMedia.ORDER_GEO).toString());
        params.put(OrderMedia.ORDER_GEO, DBHandler.getvaluefromCursor(cursor, OrderMedia.ORDER_GEO));

        // YD getting sever/local id from orderlisttable which so ever is main.
        String oid = DBHandler.getvaluefromCursor(cursor, OrderMedia.ORDER_ID);
        String WhereClause = "id =" + oid + " OR localid =" + oid;
        String Oid = DBHandler.getServerOrderId(DBHandler.orderListTable, Order.ORDER_ID, WhereClause);

        params.put(OrderMedia.ORDER_ID, Oid);

        String mime_type = DBHandler.getvaluefromCursor(cursor, OrderMedia.ORDER_FILE_MIME_TYPE);
        params.put(OrderMedia.ORDER_FILE_MIME_TYPE, mime_type);

        params.put(OrderMedia.ORDER_FILE_DESC,
                DBHandler.getvaluefromCursor(cursor, OrderMedia.ORDER_FILE_DESC));

        params.put(OrderMedia.ORDER_META_PATH,
                DBHandler.getvaluefromCursor(cursor, OrderMedia.ORDER_META_PATH));
        params.put(OrderMedia.ORDER_TIMESTAMP,
                DBHandler.getvaluefromCursor(cursor, OrderMedia.ORDER_META_UPDATE_TIME));
        params.put(OrderMedia.FORM_KEY,
                DBHandler.getvaluefromCursor(cursor, OrderMedia.FORM_KEY));
        params.put(OrderMedia.FORM_FIELD_ID,
                DBHandler.getvaluefromCursor(cursor, OrderMedia.FORM_FIELD_ID));

        return params;
    }

    public static Map<String, String> createparamsForOrderMetaDelete(Cursor cursor, String action, Map<String, String> params) {


        params.put(JSONHandler.KEY_ACTION,
                OrderMedia.ACTION_MEDIA_DELETE);
        params.put(OrderMedia.ID, DBHandler.getvaluefromCursor(cursor, OrderMedia.ID));
        return params;
    }

    public Object[] getOrderValuesFromXML(String xml) {
        NodeList nl = parseXML(xml, ServerRequestHandler.KEY_EVENT);
        Object[] orderListValues = new Object[nl.getLength()];
        String temp = "";
        String oid = "";
        for (int i = 0; i < nl.getLength(); i++) {
            try {
                Element element = (Element) nl.item(i);

                Order order = new Order();
                order.setCustContactName(getValue(element,
                        Order.ORDER_CUSTOMER_CONTACT_NAME));
                order.setCustContactNumber(getValue(element,
                        Order.ORDER_CUSTOMER_CONTACT_NUMBER));
                String id = getValue(element, Order.ORDER_CUSTOMER_ID);
                long cid = Long.parseLong(id);
                order.setCustomerid(cid);
                order.setCustName(getValue(element, Order.ORDER_CUSTOMER_NAME));
                temp = getValue(element, Order.ORDER_CUSTOMER_PART_COUNT);
                if (temp != "") {
                    order.setCustPartCount(Long.parseLong(temp));
                }
                temp = getValue(element, Order.ORDER_CUSTOMER_FORM_COUNT);
                if (temp != "") {
                    order.setCustFormCount(Long.parseLong(temp));
                }
                temp = getValue(element, Order.ORDER_CUSTOMER_MEDIA_COUNT);
                if (temp != "") {
                    order.setCustMetaCount(Long.parseLong(temp));
                }

                order.setCustSiteGeocode(getValue(element,
                        Order.ORDER_CUSTOMER_SITE_GEOCODE));

                order.setCustSiteStreeAdd(getValue(element,
                        Order.ORDER_CUSTOMER_SITE_STREET_ADDRESS));
                order.setCustSiteSuiteAddress(getValue(element,
                        Order.ORDER_CUSTOMER_SITE_SUITE_ADDRESS));
                oid = getValue(element, Order.ORDER_ID);
                if (oid != "") {
                    order.setId(Long.parseLong(oid));
                }

                temp = getValue(element, Order.ORDER_NOTES);
                if (temp != null) {
                    order.setOrdeNotes(temp);
                }

                temp = getValue(element, Order.ORDER_STATUS_INVOICE_NUMBER);

                if (temp != null)
                    order.setInvoiceNumber(temp);
                else
                    order.setInvoiceNumber("");
                /*
                 * order.setListAddWorkerPiped(getValue(element,
                 * Order.ORDER_STATUS_PRIMARY_WORKER_ID));
                 */
                order.setNm(getValue(element, Order.ORDER_NAME));
                temp = getValue(element, Order.ORDER_STATUS_ORDER_TYPE_ID);
                if (temp != "") {
                    order.setOrderTypeId(Long.parseLong(temp));
                }
                temp = getValue(element, Order.ORDER_STATUS_PO_NUMBER);
                if (temp != null) {
                    order.setPoNumber(temp);
                }
                temp = getValue(element, Order.ORDER_STATUS_PRIMARY_WORKER_ID);
                if (temp != null && !temp.equals("")) {
                    order.setPrimaryWorkerId(temp);
                }
                temp = getValue(element, Order.ORDER_STATUS_PRIORITY_TYPE_ID);
                if (temp != "") {
                    order.setPriorityTypeId(Long.parseLong(temp));
                }
                temp = getValue(element, Order.ORDER_STATUS_ID);
                if (temp != "") {
                    order.setStatusId(Long.parseLong(temp));
                }
                order.setSummary(getValue(element, Order.ORDER_STATUS_SUMMARY));
                String end_date = getValue(element, Order.ORDER_END_DATE);
                try {
                    end_date = end_date.replace("/", "-");
                } catch (Exception e) {
                    e.printStackTrace();
                    end_date = getValue(element, Order.ORDER_END_DATE);
                }
                order.setToDate(end_date);

                String start_date = getValue(element, Order.ORDER_START_DATE);
                try {
                    start_date = start_date.replace("/", "-");
                } catch (Exception e) {
                    e.printStackTrace();
                    start_date = getValue(element, Order.ORDER_START_DATE);
                }
                order.setFromDate(start_date);

                String etm = getValue(element, Order.ORDER_EPOCH_TIME);
                long epochtime = Long.valueOf(etm);
                order.setEpochTime(epochtime);

                orderListValues[i] = order;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(Utilities.TAG, "ERROR IN ORDER XML : " + oid);
            }
        }
        return orderListValues;
    }

    public String getXml(Object params) {
        StringBuilder xml = new StringBuilder("");
        xml.append("<data><event>");
        HashMap<String, String> par = (HashMap<String, String>) params;
        Iterator<Entry<String, String>> iterator = par.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            if (!param.getKey().equals(JSONHandler.KEY_ACTION))
                xml.append("<" + param.getKey() + ">" + param.getValue() + "</"
                        + param.getKey() + ">");
        }
        xml.append("</event></data>");

        return xml.toString();
    }

    public static String dumpXMLFromDB(Cursor cursor, String action,
                                       Context context, String filename, String parentdir) {
        String xml = null;

        if (action.equals(OrderTask.ACTION_SAVE_ORDER_TASK) || action.equals(OrderTask.ACTION_DELETE_ORDER_TASK)) {
            xml = XMLHandler.createXMLForOrderTask(cursor, context, DBHandler.DB_GET_ONLY_CURRENT_RECORD);
        }
        if (action.equals(OrderPart.ACTION_SAVE_ORDER_PART) || action.equals(OrderPart.ACTION_DELETE_ORDER_PART)) {
            xml = XMLHandler.createXMLForOrderPart(cursor, context, DBHandler.DB_GET_ONLY_CURRENT_RECORD);
        }
        if (action.equals(OrderMedia.ACTION_MEDIA_SAVE)) {
            xml = XMLHandler.createXMLForOrderMedia(cursor, context, DBHandler.DB_GET_ONLY_CURRENT_RECORD);
        //    Log.d("TAG77",xml);
        }
        if (action.equals(OrderNotes.ACTION_NOTES_SAVE)) {
            xml = XMLHandler.createXMLForOrderNotesSave(cursor, context, DBHandler.DB_GET_ONLY_CURRENT_RECORD);
        }
        if (action.equals(Order.ACTION_SAVE_ORDER)) {
            xml = XMLHandler.createXMLForOrder(cursor, context, DBHandler.DB_GET_ONLY_CURRENT_RECORD);
            Log.d("xml","order"+xml);
        }
        if (xml != null) {
            Date date = new Date();
            Calendar tempCalendar = Calendar.getInstance();
            tempCalendar.setTime(date);
            SimpleDateFormat smp = new SimpleDateFormat(
                    "yyyyMMdd");
            String fdate = smp.format(date);
            String basepath = Api.APP_BASE_PATH + "/" + fdate;
            if (parentdir != null && !parentdir.equals(""))
                basepath = basepath + "/" + parentdir;
            Utilities.saveFile(context, basepath, filename, xml);
            if (action.equals(OrderMedia.ACTION_MEDIA_SAVE)) { // vicky leaving for now.
                XMLHandler xmlhandler = new XMLHandler(context);
                String sourcefile = xmlhandler.getValuefromXML(xml, OrderMedia.ORDER_META_PATH);
                String ordermimetype = xmlhandler.getValuefromXML(xml, OrderMedia.ORDER_FILE_MIME_TYPE);
                String timestamp = xmlhandler.getValuefromXML(xml, OrderMedia.ORDER_META_UPDATE_TIME);
                if (timestamp == null || timestamp == "") {
                    timestamp = xmlhandler.getValuefromXML(xml, OrderMedia.ORDER_TIMESTAMP);
                }
                String type = "";
                String ext = "";
                if (ordermimetype.equals("3")) {
                    type = "AUD";
                    ext = ".3gp";
                }
                if (ordermimetype.equals("2")) {
                    type = "SIG";
                    ext = ".jpg";
                }
                if (ordermimetype.equals("1")) {
                    type = "PIC";
                    ext = ".jpg";
                }
                String filenametodump = parentdir + "_" + type + "_" + timestamp + ext;
                Utilities.moveFile(context, basepath, filenametodump, sourcefile);
            }

        }
        return null;
    }
}