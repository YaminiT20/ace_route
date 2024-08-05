package com.aceroute.mobile.software.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.aceroute.mobile.software.SplashII;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderCustSite;
import com.aceroute.mobile.software.component.OrderMedia;
import com.aceroute.mobile.software.component.OrderNotes;
import com.aceroute.mobile.software.component.OrderPart;
import com.aceroute.mobile.software.component.OrderTask;
import com.aceroute.mobile.software.component.reference.AssetLabel;
import com.aceroute.mobile.software.component.reference.Assets;
import com.aceroute.mobile.software.component.reference.AssetsType;
import com.aceroute.mobile.software.component.reference.ClientLocation;
import com.aceroute.mobile.software.component.reference.ClientSite;
import com.aceroute.mobile.software.component.reference.Customer;
import com.aceroute.mobile.software.component.reference.CustomerContact;
import com.aceroute.mobile.software.component.reference.CustomerType;
import com.aceroute.mobile.software.component.reference.Form;
import com.aceroute.mobile.software.component.reference.OrderPriority;
import com.aceroute.mobile.software.component.reference.OrderStatus;
import com.aceroute.mobile.software.component.reference.OrderTypeList;
import com.aceroute.mobile.software.component.reference.Parts;
import com.aceroute.mobile.software.component.reference.ServiceType;
import com.aceroute.mobile.software.component.reference.Shifts;
import com.aceroute.mobile.software.component.reference.Site;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.component.reference.Worker;
import com.aceroute.mobile.software.database.DBEngine;
import com.aceroute.mobile.software.database.DBHandler;
import com.aceroute.mobile.software.network.AceRequestHandler;
import com.aceroute.mobile.software.response.BaseResponse;
import com.aceroute.mobile.software.response.ErrorResponse;
import com.aceroute.mobile.software.response.Login;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

public class XMLHandler {

    Context context;
    static final String xmlHeader = ""; // <?xml version=\"1.0\"// encoding=\"UTF-8\"?>
    static final String KEY_STYPE = "ttype";// YD earlier it was sttype
    static final String KEY_RES = "res";
    static final String KEY_PTYPE = "ptype";
    static final String KEY_EVENT = "event";
    static final String KEY_CST = "cst";
    static final String KEY_LOC = "loc";
    static final String KEY_CTYPE = "ctype";
    static final String KEY_OTYPE = "otype";
    static final String KEY_STATTYPE = "stat";
    static final String KEY_OSRV = "otsk";// YD earlier it was osrv
    static final String KEY_OPRT = "oprt";
    static final String KEY_OFRM = "ofrm";
    static final String KEY_ID = "id";
    static final String KEY_LABELS = "lbls";
    static final String KEY_TERM = "trm";// YD using for header for page
    static final String KEY_AST_TERM = "asttrm";// YD using for header for page
    static final String KEY_CLIENTSITE = "loc";
    ;
	/*static final String KEY_OSTAT = "orderstatus";
	static final String KEY_PRIOR = "orderpriority";*/

    // keys helps to parsing success or fail response from server
    public static final String KEY_DATA = "data";
    public static final String KEY_DATA_SUCCESS = "success";
    public static final String KEY_DATA_ID = "id";
    public static final String KEY_DATA_ERROR = "errorcode";
    public static final String KEY_DATA_RESP_SUCCESS = "true";
    public static final String KEY_DATA_RESP_FAIL = "false";

    public static final String XML_EMPTY_DATA = "<data></data>";
    public static final String XML_NO_DATA = "<data><success>false</success><id>0</id></data>";
    public static final String XML_DATA_ERROR = "<data><success>false</success><id>0</id><errorcode></errorcode></data>";
    public static final String XML_DATA_SUCCESS_ERROR_MSG = "<data><success>true</success><id>0</id><errorcode></errorcode></data>";
    public static final String XML_DATA_ERRORCODE_END = "</errorcode></data>";
    public static final String XML_DATA_SUCCESS_MESSAGE = "<data><success>true</success><id>0</id></data>";
    public static final String XML_DATA_SUCCESS_MESSAGE_DOWNLOAD = "<data><success>true</success><id>400</id></data>";

    public XMLHandler(Context context) {
        this.context = context;
    }

    public XMLHandler() {
    }

    public String handleXML() {
        return "";
    }

    public NodeList parseXML(String xmlString, String key) {
        Document doc = getDomElement(xmlString);
        //doc.getElementsByTagName(key);
        NodeList nl = null;
        try {
            nl = doc.getElementsByTagName(key);
        } catch (NullPointerException e) {
            nl = new NodeList() {
                @Override
                public Node item(int index) {
                    return null;
                }

                @Override
                public int getLength() {
                    return 0;
                }
            };
            e.printStackTrace();
        }
        return nl;
    }

    public String getValuefromXML(String xmlString, String tagname) {
        Document doc = getDomElement(xmlString);
        NodeList nl = doc.getElementsByTagName(tagname);
        Element e = (Element) nl.item(0);
        return getElementValue(e);
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

    public String getValueCData(Element item, String str) {
        try {
            NodeList n = item.getElementsByTagName(str);
            String value = this.getElementValueCData(n.item(0));
            if (value == null || !(value.length() > 0))
                return "";
            else
                return value;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static final String getElementValue(Node elem) {
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

    public final String getElementValueCData(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child
                        .getNextSibling()) {
                    if (child.getNodeType() == Node.CDATA_SECTION_NODE) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    public static String createXMLFromDB(Cursor cursor, String action,
                                         Context context, int type) {
        String xml = null;
        if (action.equals(CustomerType.ACTION_CUST_TYPE)) {
            xml = createXMLForCusType(cursor, context, type);
            return xml;
        } else if (action.equals(ClientLocation.ACTION_CLIENT_LOCATION)) {
            xml = createXMLForClientLocation(cursor, context, type);
            return xml;
        } else if (action.equals(Customer.ACTION_CUSTOMER_LIST)) {
            xml = createXMLForCustomerList(cursor, context, type);
            return xml;
        } else if (action.equals(Order.ACTION_ORDER)) {
            xml = createXMLForOrder(cursor, context, type);

            Log.d("createxml","order"+xml);
            return xml;
        } else if (action.equals(Parts.ACTION_PART_TYPE)) {
            xml = createXMLForPartType(cursor, context, type);
            return xml;
        } else if (action.equals(Worker.ACTION_WORKER_LIST)) {
            xml = createXMLForWorkerList(cursor, context, type);
            return xml;
        } else if (action.equals(ServiceType.ACTION_SERVICE_TYPE)) {
            xml = createXMLForServiceType(cursor, context, type);
            return xml;
        } else if (action.equals(OrderTypeList.ACTION_ORDER_TYPE)) {
            xml = createXMLForOrderType(cursor, context, type);
            return xml;
        } else if (action.equals(OrderTask.ACTION_GET_ORDER_TASK)) {
            xml = createXMLForOrderTask(cursor, context, type);
            return xml;
        } else if (action.equals(OrderPart.ACTION_GET_ORDER_PART)) {
            xml = createXMLForOrderPart(cursor, context, type);
            return xml;
        } else if (action.equals(OrderMedia.ACTION_GET_MEDIA)) {
            xml = createXMLForOrderMedia(cursor, context, type);
            return xml;
        } else if (action.equals(OrderNotes.ACTION_GET_NOTES)) {
            xml = createXMLForOrderNotes(cursor, context, type);
            return xml;
        } else if (action.equals(Site.ACTION_SITE)) {
            xml = createXMLForSite(cursor, context, type);
            return xml;
        } else if (action.equals(SiteType.ACTION_SITE_TYPE)) {
            xml = createXMLForSiteType(cursor, context, type);
            return xml;
        }
        return null;
    }

    public static String createXMLFromObjects(Object[] object, String action,
                                              Context context) {
        String xml = null;

        if (action.equals(OrderTask.ACTION_GET_ORDER_TASK)) {
            xml = createXMLForOrderTaskfromObject(object, context);
            return xml;
        } else if (action.equals(OrderPart.ACTION_GET_ORDER_PART)) {
            xml = createXMLForOrderPartfromObject(object, context);
            return xml;
        }
        if (action.equals(Order.ACTION_ORDER)) {
            xml = createXMLForOrderfromObject(object, context);
            return xml;
        }
        if (action.equals(OrderMedia.ACTION_GET_MEDIA)) {
            xml = createXMLForOrderMediafromObject(object, context);

            return xml;
        }

	/*	if (action.equals(CustomerType.ACTION_CUST_TYPE)) {
			xml = createXMLForCusType(cursor, context);
			return xml;
		} else if (action.equals(ClientLocation.ACTION_CLIENT_LOCATION)) {
			xml = createXMLForClientLocation(cursor, context);
			return xml;
		} else if (action.equals(Customer.ACTION_CUSTOMER_LIST)) {
			xml = createXMLForCustomerList(cursor, context);
			return xml;
		} else if (action.equals(Order.ACTION_ORDER)) {
			xml = createXMLForOrder(cursor, context);
			return xml;
		} else if (action.equals(Parts.ACTION_PART_TYPE)) {
			xml = createXMLForPartType(cursor, context);
			return xml;
		} else if (action.equals(Worker.ACTION_WORKER_LIST)) {
			xml = createXMLForWorkerList(cursor, context);
			return xml;
		} else if (action.equals(ServiceType.ACTION_SERVICE_TYPE)) {
			xml = createXMLForServiceType(cursor, context);
			return xml;
		} else if (action.equals(OrderTypeList.ACTION_ORDER_TYPE)) {
			xml = createXMLForOrderType(cursor, context);
			return xml;
		} else if (action.equals(OrderTask.ACTION_GET_ORDER_TASK)) {
			xml = createXMLForOrderTask(cursor, context);
			return xml;
		} else if (action.equals(OrderPart.ACTION_GET_ORDER_PART)) {
			xml = createXMLForOrderPart(cursor, context);
			return xml;
		} else if (action.equals(OrderMedia.ACTION_GET_MEDIA)) {
			xml = createXMLForOrderMedia(cursor, context);
			return xml;
		} else if (action.equals(OrderNotes.ACTION_GET_NOTES)) {
			xml = createXMLForOrderNotes(cursor, context);
			return xml;
		} else if (action.equals(Site.ACTION_SITE)) {
			xml = createXMLForSite(cursor, context);
			return xml;
		}*/
        return null;
    }

    private static String createXMLForSite(Cursor cursor, Context context2, int type) {
        String tempxml = XML_NO_DATA;
        try {
            if (cursor.moveToFirst()) {
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");
                do {
                    xml.append("<" + Site.SITE_PARENT_TAG + ">");
                    xml.append("<" + Site.SITE_ID + ">" + cursor.getLong(0)
                            + "</" + Site.SITE_ID + ">");
                    xml.append("<" + Site.SITE_CID + ">" + cursor.getLong(1)
                            + "</" + Site.SITE_CID + ">");
                    xml.append("<" + Site.SITE_NAME + ">" + cursor.getString(2)
                            + "</" + Site.SITE_NAME + ">");
                    xml.append("<" + Site.SITE_ADDRESS + ">"
                            + cursor.getString(3) + "</" + Site.SITE_ADDRESS
                            + ">");
                    xml.append("<" + Site.SITE_UPD + ">" + cursor.getString(8)
                            + "</" + Site.SITE_UPD + ">");
                    String temp = cursor.getString(9);
                    if (temp == null) temp = "";
                    xml.append("<" + Site.SITE_DTL + ">" + temp
                            + "</" + Site.SITE_DTL + ">");
                    temp = cursor.getString(10);
                    if (temp == null) temp = "";
                    xml.append("<" + Site.SITE_TYPE_ID + ">" + temp
                            + "</" + Site.SITE_TYPE_ID + ">");
                    temp = cursor.getString(11);
                    if (temp == null) temp = "";
                    xml.append("<" + Site.SITE_TYPE_NM + ">" + temp
                            + "</" + Site.SITE_TYPE_NM + ">");
                    temp = cursor.getString(6);
                    if (temp == null) temp = "";
                    xml.append("<" + Site.SITE_GEO + ">" + temp
                            + "</" + Site.SITE_GEO + ">");
                    xml.append("</" + Site.SITE_PARENT_TAG + ">");
                }
                while (type == DBHandler.DB_GET_ONLY_CURRENT_RECORD ? false : cursor.moveToNext());

                xml.append("</data>");

                return xml.toString();
                // TODO return the original xml. Below is temp xml.
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return tempxml;
        } finally {
            cursor.close();
        }
    }

    public static String createXMLForOrderPart(Cursor cursor, Context context, int type) {
        String tempxml = "<data/>";
        try {
            if (cursor.moveToFirst()) {
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");
                do {
                    xml.append("<" + OrderPart.ORDER_PARENT_TAG + ">");
                    xml.append("<" + OrderPart.ORDER_ID + ">"
                            + cursor.getLong(0) + "</" + OrderPart.ORDER_ID
                            + ">");
                    xml.append("<" + OrderPart.ORDER_PART_ID + ">"
                            + cursor.getLong(1) + "</"
                            + OrderPart.ORDER_PART_ID + ">");
                    xml.append("<" + OrderPart.PART_TID + ">"
                            + cursor.getLong(2) + "</" + OrderPart.PART_TID
                            + ">");
                    xml.append("<" + OrderPart.ORDER_PART_QTY + ">"
                            + cursor.getString(3) + "</"
                            + OrderPart.ORDER_PART_QTY + ">");
                    xml.append("<" + OrderPart.ORDER_UPDATE_TIME + ">"
                            + cursor.getLong(4) + "</"
                            + OrderPart.ORDER_UPDATE_TIME + ">");
                    xml.append("</" + OrderPart.ORDER_PARENT_TAG + ">");
                }
                while (type == DBHandler.DB_GET_ONLY_CURRENT_RECORD ? false : cursor.moveToNext());

                xml.append("</data>");

                return xml.toString();
                // TODO return the original xml. Below is temp xml.
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return tempxml;
        } finally {
            cursor.close();
        }
    }

    public static String createXMLForOrderMedia(Cursor cursor, Context context, int type) {
        String tempxml = "<data/>";
        try {
            if (cursor.moveToFirst()) {
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");

                do {
                    xml.append("<" + OrderMedia.ORDER_PARENT_TAG + ">");
                    xml.append("<" + OrderMedia.ID + ">" + cursor.getLong(0)
                            + "</" + OrderMedia.ID + ">");
                    xml.append("<" + OrderMedia.ORDER_ID + ">"
                            + cursor.getLong(1) + "</" + OrderMedia.ORDER_ID
                            + ">");
                    xml.append("<" + OrderMedia.ORDER_MEDIA_TYPE + ">"
                            + cursor.getInt(2) + "</"
                            + OrderMedia.ORDER_MEDIA_TYPE + ">");
                    xml.append("<" + OrderMedia.ORDER_GEO + ">"
                            + cursor.getString(3) + "</" + OrderMedia.ORDER_GEO
                            + ">");
                    xml.append("<" + OrderMedia.ORDER_FILE_DESC + ">"
                            + cursor.getString(4) + "</"
                            + OrderMedia.ORDER_FILE_DESC + ">");
                    xml.append("<" + OrderMedia.ORDER_FILE_MIME_TYPE + ">"
                            + cursor.getString(5) + "</"
                            + OrderMedia.ORDER_FILE_MIME_TYPE + ">");
                    xml.append("<" + OrderMedia.ORDER_FILE_DATA + ">"
                            + cursor.getString(6) + "</"
                            + OrderMedia.ORDER_FILE_DATA + ">");
                    xml.append("<" + OrderMedia.ORDER_META_UPDATE_TIME + ">"
                            + cursor.getLong(7) + "</"
                            + OrderMedia.ORDER_META_UPDATE_TIME + ">");
                    xml.append("<" + OrderMedia.ORDER_META_PATH + ">"
                            + cursor.getString(9) + "</"
                            + OrderMedia.ORDER_META_PATH + ">");
                    xml.append("<" + OrderMedia.FORM_KEY + ">"
                            + cursor.getString(10) + "</"
                            + OrderMedia.FORM_KEY + ">");
                    xml.append("<" + OrderMedia.FORM_FIELD_ID + ">"
                            + cursor.getString(11) + "</"
                            + OrderMedia.FORM_FIELD_ID + ">");
                    xml.append("</" + OrderMedia.ORDER_PARENT_TAG + ">");
                    //             Log.d("TAG77",xml.toString());

                }
                while (type == DBHandler.DB_GET_ONLY_CURRENT_RECORD ? false : cursor.moveToNext());
                xml.append("</data>");
                return xml.toString();
            } else
                return tempxml;
        } catch (Exception e) {
            e.printStackTrace();
            return tempxml;
        } finally {
            cursor.close();
        }
    }

    public static String createXMLForOrderTask(Cursor cursor, Context context, int type) {
        String tempxml = "<data/>";
        try {
            if (cursor.moveToFirst()) {
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");
                do {
                    xml.append("<" + OrderTask.ORDER_PARENT_TAG + ">");
                    xml.append("<" + OrderTask.ORDER_ID + ">"
                            + cursor.getLong(0) + "</" + OrderTask.ORDER_ID
                            + ">");
                    xml.append("<" + OrderTask.ORDER_TASK_ID + ">"
                            + cursor.getLong(1) + "</"
                            + OrderTask.ORDER_TASK_ID + ">");
                    xml.append("<" + OrderTask.TASK_ID + ">"
                            + cursor.getLong(2) + "</"
                            + OrderTask.TASK_ID + ">");
                    xml.append("<" + OrderTask.ORDER_SERV_UPDATE_TIME + ">"
                            + cursor.getLong(3) + "</"
                            + OrderTask.ORDER_SERV_UPDATE_TIME + ">");
                    xml.append("<" + OrderTask.TRIM_TYPE + ">" + cursor.getString(5) + "</" + OrderTask.TRIM_TYPE + ">");
                    xml.append("<" + OrderTask.PRIORITY + ">" + cursor.getString(6) + "</" + OrderTask.PRIORITY + ">");
                    xml.append("<" + OrderTask.ACTION_STATUS + ">" + cursor.getString(7) + "</" + OrderTask.ACTION_STATUS + ">");
                    xml.append("<" + OrderTask.TREE_OWNER + ">" + cursor.getString(8) + "</" + OrderTask.TREE_OWNER + ">");
                    xml.append("<" + OrderTask.TREE_HT + ">" + cursor.getString(9) + "</" + OrderTask.TREE_HT + ">");
                    xml.append("<" + OrderTask.TREE_DIA + ">" + cursor.getString(10) + "</" + OrderTask.TREE_DIA + ">");
                    xml.append("<" + OrderTask.TREE_CLEARENCE + ">" + cursor.getString(11) + "</" + OrderTask.TREE_CLEARENCE + ">");
                    xml.append("<" + OrderTask.TREE_CYCLE + ">" + cursor.getString(12) + "</" + OrderTask.TREE_CYCLE + ">");
                    xml.append("<" + OrderTask.TREE_EXPCOUNT + ">" + cursor.getString(13) + "</" + OrderTask.TREE_EXPCOUNT + ">");
                    xml.append("<" + OrderTask.TREE_ACTUALCOUNT + ">" + cursor.getString(14) + "</" + OrderTask.TREE_ACTUALCOUNT + ">");
                    xml.append("<" + OrderTask.TREE_TIMESPENT + ">" + cursor.getString(15) + "</" + OrderTask.TREE_TIMESPENT + ">");
                    xml.append("<" + OrderTask.TREE_TM + ">" + cursor.getString(16) + "</" + OrderTask.TREE_TM + ">");
                    xml.append("<" + OrderTask.TREE_MSC + ">" + cursor.getString(17) + "</" + OrderTask.TREE_MSC + ">");
                    xml.append("<" + OrderTask.TREE_COMMENT + ">" + cursor.getString(18) + "</" + OrderTask.TREE_COMMENT + ">");
                    xml.append("<" + OrderTask.TREE_PCOMMENT + ">" + cursor.getString(19) + "</" + OrderTask.TREE_PCOMMENT + ">");
                    xml.append("<" + OrderTask.TREE_ALERT + ">" + cursor.getString(20) + "</" + OrderTask.TREE_ALERT + ">");
                    xml.append("<" + OrderTask.TREE_NOTE + ">" + cursor.getString(21) + "</" + OrderTask.TREE_NOTE + ">");
                    xml.append("<" + OrderTask.TREE_GEO + ">" + cursor.getString(22) + "</" + OrderTask.TREE_GEO + ">");
                    xml.append("<" + OrderTask.TREE_ACCESSPATH + ">" + cursor.getString(23) + "</" + OrderTask.TREE_ACCESSPATH + ">");
                    xml.append("<" + OrderTask.TREE_CT1 + ">" + cursor.getString(24) + "</" + OrderTask.TREE_CT1 + ">");
                    xml.append("<" + OrderTask.TREE_CT2 + ">" + cursor.getString(25) + "</" + OrderTask.TREE_CT2 + ">");
                    xml.append("<" + OrderTask.TREE_CT3 + ">" + cursor.getString(26) + "</" + OrderTask.TREE_CT3 + ">");

                    xml.append("</" + OrderTask.ORDER_PARENT_TAG + ">");
                }
                while (type == DBHandler.DB_GET_ONLY_CURRENT_RECORD ? false : cursor.moveToNext());

                xml.append("</data>");

                return xml.toString();
                // TODO return the original xml. Below is temp xml.
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return tempxml;
        } finally {
            cursor.close();
        }
    }

    private static String createXMLForOrderTaskfromObject(Object[] object, Context context) {
        String tempxml = "<data/>";

        try {
            if (object.length > 0) {
                OrderTask orderTask = (OrderTask) object[0];// handling only one record for now
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");

                xml.append("<" + OrderTask.ORDER_PARENT_TAG + ">");
                xml.append("<" + OrderTask.ORDER_ID + ">"
                        + orderTask.getId() + "</" + OrderTask.ORDER_ID
                        + ">");
                xml.append("<" + OrderTask.ORDER_TASK_ID + ">"
                        + orderTask.getOrder_task_id() + "</"
                        + OrderTask.ORDER_TASK_ID + ">");
                xml.append("<" + OrderTask.TASK_ID + ">"
                        + orderTask.getTask_id() + "</" + OrderTask.TASK_ID
                        + ">");
                xml.append("<" + OrderTask.ORDER_SERV_UPDATE_TIME + ">"
                        + orderTask.getUpd_time() + "</"
                        + OrderTask.ORDER_SERV_UPDATE_TIME + ">");

                xml.append("<" + OrderTask.TREE_CYCLE + ">" + orderTask.getTree_cycle() + "</" + OrderTask.TREE_CYCLE + ">");
                xml.append("<" + OrderTask.TRIM_TYPE + ">" + orderTask.getTree_type() + "</" + OrderTask.TRIM_TYPE + ">");
                xml.append("<" + OrderTask.PRIORITY + ">" + orderTask.getPriority() + "</" + OrderTask.PRIORITY + ">");
                xml.append("<" + OrderTask.ACTION_STATUS + ">" + orderTask.getAction_status() + "</" + OrderTask.ACTION_STATUS + ">");
                xml.append("<" + OrderTask.TREE_OWNER + ">" + orderTask.getTree_owner() + "</" + OrderTask.TREE_OWNER + ">");
                xml.append("<" + OrderTask.TREE_HT + ">" + orderTask.getTree_ht() + "</" + OrderTask.TREE_HT + ">");
                xml.append("<" + OrderTask.TREE_DIA + ">" + orderTask.getTree_dia() + "</" + OrderTask.TREE_DIA + ">");
                xml.append("<" + OrderTask.TREE_CLEARENCE + ">" + orderTask.getTree_clearence() + "</" + OrderTask.TREE_CLEARENCE + ">");
                xml.append("<" + OrderTask.TREE_EXPCOUNT + ">" + orderTask.getTree_expcount() + "</" + OrderTask.TREE_EXPCOUNT + ">");
                xml.append("<" + OrderTask.TREE_ACTUALCOUNT + ">" + orderTask.getTree_actualcount() + "</" + OrderTask.TREE_ACTUALCOUNT + ">");
                xml.append("<" + OrderTask.TREE_TIMESPENT + ">" + orderTask.getTree_timespent() + "</" + OrderTask.TREE_TIMESPENT + ">");
                xml.append("<" + OrderTask.TREE_TM + ">" + orderTask.getTree_tm() + "</" + OrderTask.TREE_TM + ">");
                xml.append("<" + OrderTask.TREE_MSC + ">" + orderTask.getTree_msc() + "</" + OrderTask.TREE_MSC + ">");
                xml.append("<" + OrderTask.TREE_COMMENT + ">" + orderTask.getTree_comment() + "</" + OrderTask.TREE_COMMENT + ">");
                xml.append("<" + OrderTask.TREE_PCOMMENT + ">" + orderTask.getTree_pcomment() + "</" + OrderTask.TREE_PCOMMENT + ">");
                xml.append("<" + OrderTask.TREE_ALERT + ">" + orderTask.getTree_alert() + "</" + OrderTask.TREE_ALERT + ">");
                xml.append("<" + OrderTask.TREE_NOTE + ">" + orderTask.getTree_note() + "</" + OrderTask.TREE_NOTE + ">");
                xml.append("<" + OrderTask.TREE_GEO + ">" + orderTask.getTree_geo() + "</" + OrderTask.TREE_GEO + ">");
                xml.append("<" + OrderTask.TREE_ACCESSPATH + ">" + orderTask.getTree_accesspath() + "</" + OrderTask.TREE_ACCESSPATH + ">");
                xml.append("<" + OrderTask.TREE_CT1 + ">" + orderTask.getTree_ct1() + "</" + OrderTask.TREE_CT1 + ">");
                xml.append("<" + OrderTask.TREE_CT2 + ">" + orderTask.getTree_ct2() + "</" + OrderTask.TREE_CT2 + ">");
                xml.append("<" + OrderTask.TREE_CT3 + ">" + orderTask.getTree_ct3() + "</" + OrderTask.TREE_CT3 + ">");


                xml.append("</" + OrderTask.ORDER_PARENT_TAG + ">");


                xml.append("</data>");

                return xml.toString();
                // TODO return the original xml. Below is temp xml.
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return tempxml;
        } finally {

        }
    }

    private static String createXMLForOrderPartfromObject(Object[] object, Context context) {
        String tempxml = "<data/>";

        try {
            if (object.length > 0) {
                OrderPart currentObject = (OrderPart) object[0];// handling only one record for now
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");

                xml.append("<" + OrderPart.ORDER_PARENT_TAG + ">");
                xml.append("<" + OrderPart.ORDER_ID + ">"
                        + currentObject.getOid() + "</" + OrderPart.ORDER_ID
                        + ">");
                xml.append("<" + OrderPart.ORDER_PART_ID + ">"
                        + currentObject.getOrder_part_id() + "</"
                        + OrderPart.ORDER_PART_ID + ">");
                xml.append("<" + OrderPart.PART_TID + ">"
                        + currentObject.PART_TID + "</" + OrderPart.PART_TID
                        + ">");
                xml.append("<" + OrderPart.ORDER_PART_QTY + ">"
                        + currentObject.getOrder_part_QTY() + "</"
                        + OrderPart.ORDER_PART_QTY + ">");
                xml.append("<" + OrderPart.ORDER_BARCODE + ">"
                        + currentObject.getPart_barcode() + "</"
                        + OrderPart.ORDER_BARCODE + ">");

                xml.append("<" + OrderPart.ORDER_UPDATE_TIME + ">"
                        + currentObject.getUpd_time() + "</"
                        + OrderPart.ORDER_UPDATE_TIME + ">");
                xml.append("</" + OrderPart.ORDER_PARENT_TAG + ">");


                xml.append("</data>");

                return xml.toString();
                // TODO return the original xml. Below is temp xml.
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return tempxml;
        } finally {

        }
    }

    private static String createXMLForOrderfromObject(Object[] object, Context context) {//YD

        StringBuffer xml = new StringBuffer(xmlHeader + "<data>");
        try {


            if (object.length > 0) {

                Order order = (Order) (object[0]);// assumig only one record


                xml.append("<event>");
                xml.append("<id>" + order.getId() + "</id>");
                xml.append("<start_date>" + order.getFromDate()
                        + "</start_date>");
                xml.append("<end_date>" + order.getToDate()
                        + "</end_date>");
                xml.append("<nm>" + order.getNm() + "</nm>");
                xml.append("<wkf>" + order.getStatusId() + "</wkf>");
                String po = order.getPoNumber();
                if (po != null)
                    xml.append("<po>" + po + "</po>");
                else
                    xml.append("<po>" + 0 + "</po>");
                if (order.getInvoiceNumber() != null)
                    xml.append("<inv>" + order.getInvoiceNumber() + "</inv>");
                else
                    xml.append("<inv></inv>");
                xml.append("<tid>" + order.getOrderTypeId() + "</tid>");
                xml.append("<pid>" + order.getPriorityTypeId() + "</pid>");
                if (order.getPrimaryWorkerId() != null)
                    xml.append("<rid>" + order.getPrimaryWorkerId() + "</rid>");
                else
                    xml.append("<rid>" + null + "</rid>");
                xml.append("<dtl>" + order.getSummary() + "</dtl>");
                // xml.append("<note>" + cursor.getString(11) + "</note>");
                xml.append("<" + Order.ORDER_CUSTOMER_ID + ">"
                        + order.getCustomerid() + "</"
                        + Order.ORDER_CUSTOMER_ID + ">");
                String temp = order.getCustName();
                if (temp == null)
                    temp = "";
                xml.append("<cnm>" + temp + "</cnm>");
                temp = order.getCustSiteStreeAdd();
                if (temp == null)
                    temp = "";
                xml.append("<adr>" + temp + "</adr>");
                temp = order.getCustSiteSuiteAddress();
                if (temp == null)
                    temp = "";
                xml.append("<adr2>" + temp + "</adr2>");
                temp = order.getCustSiteGeocode();
                if (temp == null)
                    temp = "";
                xml.append("<geo>" + temp + "</geo>");
                temp = order.getCustContactName();
                if (temp == null)
                    temp = "";
                xml.append("<cntnm>" + temp + "</cntnm>");
                temp = order.getCustContactNumber();
                if (temp == null)
                    temp = "";
                xml.append("<tel>" + temp + "</tel>");
                if (order.getCustPartCount() > 0)
                    xml.append("<cprt>" + order.getCustPartCount() + "</cprt>");
                else
                    xml.append("<cprt>" + 0 + "</cprt>");

                if (order.getCustMetaCount() > 0)
                    xml.append("<" + Order.ORDER_CUSTOMER_MEDIA_COUNT + ">"
                            + order.getCustMetaCount() + "</"
                            + Order.ORDER_CUSTOMER_MEDIA_COUNT + ">");
                else
                    xml.append("<" + Order.ORDER_CUSTOMER_MEDIA_COUNT + ">"
                            + 0 + "</" + Order.ORDER_CUSTOMER_MEDIA_COUNT
                            + ">");

                if (order.getCustFormCount() > 0)
                    xml.append("<cserv>" + order.getCustFormCount() + "</cserv>");
                else
                    xml.append("<cserv>" + 0 + "</cserv>");

                xml.append("<" + Order.ORDER_EPOCH_TIME + ">"
                        + order.getEpochTime() + "</"
                        + Order.ORDER_EPOCH_TIME + ">");
                if (order.getAudioCount() > 0)
                    xml.append("<" + Order.ORDER_AUDIO + ">"
                            + order.getAudioCount() + "</"
                            + Order.ORDER_AUDIO + ">");
                else
                    xml.append("<" + Order.ORDER_AUDIO + ">"
                            + 0 + "</" + Order.ORDER_AUDIO
                            + ">");
                if (order.getSigCount() > 0)
                    xml.append("<" + Order.ORDER_SIG + ">"
                            + order.getSigCount() + "</"
                            + Order.ORDER_SIG + ">");
                else
                    xml.append("<" + Order.ORDER_SIG + ">"
                            + 0 + "</" + Order.ORDER_SIG
                            + ">");
                if (order.getImgCount() > 0)
                    xml.append("<" + Order.ORDER_IMG + ">"
                            + order.getImgCount() + "</"
                            + Order.ORDER_IMG + ">");
                else
                    xml.append("<" + Order.ORDER_IMG + ">"
                            + 0 + "</" + Order.ORDER_IMG
                            + ">");
                if (order.getNotCount() > 0)
                    xml.append("<" + Order.ORDER_NOTE + ">"
                            + order.getNotCount() + "</"
                            + Order.ORDER_NOTE + ">");
                else
                    xml.append("<" + Order.ORDER_NOTE + ">"
                            + 0 + "</" + Order.ORDER_NOTE
                            + ">");
                xml.append("</event>");
            }

            xml.append("</data>");

            return xml.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String createXMLForOrderMediafromObject(Object[] object, Context context) {
        String tempxml = "<data/>";
        Log.d("TAG78",tempxml);
        try {
            if (object.length > 0) {
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");

                OrderMedia odmedia = (OrderMedia) object[0];
                xml.append("<" + OrderMedia.ORDER_PARENT_TAG + ">");
                xml.append("<" + OrderMedia.ID + ">" + odmedia.getId()
                        + "</" + OrderMedia.ID + ">");
                xml.append("<" + OrderMedia.ORDER_ID + ">"
                        + odmedia.getOrderid() + "</" + OrderMedia.ORDER_ID
                        + ">");
                xml.append("<" + OrderMedia.ORDER_MEDIA_TYPE + ">"
                        + odmedia.getMediatype() + "</"
                        + OrderMedia.ORDER_MEDIA_TYPE + ">");
                xml.append("<" + OrderMedia.ORDER_GEO + ">"
                        + odmedia.getGeocode() + "</" + OrderMedia.ORDER_GEO
                        + ">");
                xml.append("<" + OrderMedia.ORDER_FILE_DESC + ">"
                        + odmedia.getFile_desc() + "</"
                        + OrderMedia.ORDER_FILE_DESC + ">");
                xml.append("<" + OrderMedia.ORDER_FILE_MIME_TYPE + ">"
                        + odmedia.getMimetype() + "</"
                        + OrderMedia.ORDER_FILE_MIME_TYPE + ">");
                xml.append("<" + OrderMedia.ORDER_FILE_DATA + ">"
                        + odmedia.getData() + "</"
                        + OrderMedia.ORDER_FILE_DATA + ">");
                xml.append("<" + OrderMedia.ORDER_META_UPDATE_TIME + ">"
                        + odmedia.getUpd_time() + "</"
                        + OrderMedia.ORDER_META_UPDATE_TIME + ">");
                xml.append("<" + OrderMedia.ORDER_META_PATH + ">"
                        + odmedia.getMetapath() + "</"
                        + OrderMedia.ORDER_META_PATH + ">");
                xml.append("<" + OrderMedia.FORM_KEY + ">"
                        + odmedia.getFormKey() + "</"
                        + OrderMedia.FORM_KEY + ">");
                xml.append("<" + OrderMedia.FORM_FIELD_ID + ">"
                        + odmedia.getFrmfiledid() + "</"
                        + OrderMedia.FORM_FIELD_ID + ">");
                xml.append("</" + OrderMedia.ORDER_PARENT_TAG + ">");

                xml.append("</data>");
                return xml.toString();
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return tempxml;
        }
    }

    private static String createXMLForOrderType(Cursor cursor, Context context2, int type) {
        try {
            if (cursor.moveToFirst()) {
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");
                do {
                    xml.append("<" + OrderTypeList.ORDERTYPE_PARENT_TAG + ">");
                    xml.append("<" + OrderTypeList.ORDER_TYPE_ID + ">"
                            + cursor.getLong(0) + "</"
                            + OrderTypeList.ORDER_TYPE_ID + ">");
                    xml.append("<" + OrderTypeList.ORDER_TYPE_NAME + ">"
                            + cursor.getString(1) + "</"
                            + OrderTypeList.ORDER_TYPE_NAME + ">");
                    xml.append("</" + OrderTypeList.ORDERTYPE_PARENT_TAG + ">");
                }
                while (type == DBHandler.DB_GET_ONLY_CURRENT_RECORD ? false : cursor.moveToNext());

                xml.append("</data>");

                return xml.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return null;
    }

    private static String createXMLForServiceType(Cursor cursor,
                                                  Context context2, int type) {
        try {
            if (cursor.moveToFirst()) {
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");
                do {
                    xml.append("<stype>");
                    xml.append("<id>" + cursor.getLong(0) + "</id>");
                    xml.append("<nm>" + cursor.getString(1) + "</nm>");
                    xml.append("</stype>");
                }
                while (type == DBHandler.DB_GET_ONLY_CURRENT_RECORD ? false : cursor.moveToNext());

                xml.append("</data>");

                return xml.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return null;
    }

    private static String createXMLForWorkerList(Cursor cursor, Context context2, int type) {
        try {
            if (cursor.moveToFirst()) {
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");
                do {
                    xml.append("<res>");
                    xml.append("<id>" + cursor.getLong(0) + "</id>");
                    xml.append("<nm>" + cursor.getString(1) + "</nm>");
                    xml.append("<lid>" + cursor.getLong(2) + "</lid>");
                    xml.append("<" + Worker.WORKER_WEEK + ">"
                            + cursor.getString(3) + "</" + Worker.WORKER_WEEK
                            + ">");
                    xml.append("<" + Worker.WORKER_DWEEK + ">"
                            + cursor.getString(4) + "</" + Worker.WORKER_DWEEK
                            + ">");
                    xml.append("<" + Worker.WORKER_BRK + ">"
                            + cursor.getString(5) + "</" + Worker.WORKER_BRK
                            + ">");
                    xml.append("</res>");
                }
                while (type == DBHandler.DB_GET_ONLY_CURRENT_RECORD ? false : cursor.moveToNext());

                xml.append("</data>");

                return xml.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return null;
    }

    private static String createXMLForPartType(Cursor cursor, Context context2, int type) {
        try {
            if (cursor.moveToFirst()) {
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");
                do {
                    xml.append("<ptype>");
                    xml.append("<id>" + cursor.getLong(0) + "</id>");
                    xml.append("<nm>" + cursor.getString(1) + "</nm>");
                    xml.append("<desc>" + cursor.getString(2) + "</desc>");
                    xml.append("<ctid>" + cursor.getString(2) + "</ctid>");
                    xml.append("</ptype>");
                }
                while (type == DBHandler.DB_GET_ONLY_CURRENT_RECORD ? false : cursor.moveToNext());

                xml.append("</data>");

                return xml.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return null;
    }

    public static String createXMLForOrder(Cursor cursor, Context context2, int type) {//yash note
        try {
            if (cursor.moveToFirst()) {
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");
                Log.d("allxml",""+xml);
                do {
                    xml.append("<event>");
                    xml.append("<id>" + cursor.getLong(0) + "</id>");
                    xml.append("<start_date>" + cursor.getString(1)
                            + "</start_date>");
                    xml.append("<end_date>" + cursor.getString(2)
                            + "</end_date>");
                    xml.append("<nm>" + cursor.getString(3) + "</nm>");
                    xml.append("<wkf>" + cursor.getString(4) + "</wkf>");
                    String po = cursor.getString(5);
                    if (po != null)
                        xml.append("<po>" + po + "</po>");
                    else
                        xml.append("<po>" + 0 + "</po>");
                    String temp = cursor.getString(6);
                    if (temp == null) temp = "";
                    xml.append("<inv>" + temp + "</inv>");
                    temp = cursor.getString(7);
                    if (temp == null) temp = "";
                    xml.append("<tid>" + temp + "</tid>");
                    temp = cursor.getString(8);
                    if (temp == null) temp = "";

                    xml.append("<pid>" + temp + "</pid>");
                    temp = cursor.getString(9);
                    if (temp == null) temp = "";


                    xml.append("<rid>" + temp + "</rid>");
                    temp = cursor.getString(10);
                    if (temp == null) temp = "";

                    xml.append("<dtl>" + temp + "</dtl>");
                    // xml.append("<note>" + cursor.getString(11) + "</note>");
                    xml.append("<" + Order.ORDER_CUSTOMER_ID + ">"
                            + cursor.getLong(12) + "</"
                            + Order.ORDER_CUSTOMER_ID + ">");


                    temp = cursor.getString(13);
                    if (temp == null) temp = "";
                    xml.append("<cnm>" + temp + "</cnm>");
                    temp = cursor.getString(14);
                    if (temp == null) temp = "";
                    xml.append("<adr>" + temp + "</adr>");
                    temp = cursor.getString(15);
                    if (temp == null) temp = "";
                    xml.append("<adr2>" + temp + "</adr2>");
                    temp = cursor.getString(16);
                    if (temp == null) temp = "";
                    xml.append("<geo>" + temp + "</geo>");
                    temp = cursor.getString(17);
                    if (temp == null) temp = "";
                    xml.append("<cntnm>" + temp + "</cntnm>");
                    temp = cursor.getString(18);
                    if (temp == null) temp = "";
                    xml.append("<tel>" + temp + "</tel>");
                    if (cursor.getLong(19) > 0)
                        xml.append("<cprt>" + cursor.getLong(19) + "</cprt>");
                    else
                        xml.append("<cprt>" + 0 + "</cprt>");

                    if (cursor.getLong(20) > 0)
                        xml.append("<" + Order.ORDER_CUSTOMER_MEDIA_COUNT + ">"
                                + cursor.getLong(20) + "</"
                                + Order.ORDER_CUSTOMER_MEDIA_COUNT + ">");
                    else
                        xml.append("<" + Order.ORDER_CUSTOMER_MEDIA_COUNT + ">"
                                + 0 + "</" + Order.ORDER_CUSTOMER_MEDIA_COUNT
                                + ">");

                    if (cursor.getLong(21) > 0)
                        xml.append("<cserv>" + cursor.getLong(21) + "</cserv>");
                    else
                        xml.append("<cserv>" + 0 + "</cserv>");

                    xml.append("<" + Order.ORDER_EPOCH_TIME + ">"
                            + cursor.getLong(22) + "</"
                            + Order.ORDER_EPOCH_TIME + ">");
                    temp = cursor.getString(31);
                    if (temp == null) temp = "";
                    xml.append("<ctpnm>" + temp + "</ctpnm>");
                    temp = cursor.getString(26);
                    if (temp == null) temp = "";
                    xml.append("<lid>" + temp + "</lid>");
                    temp = cursor.getString(32);
                    if (temp == null) temp = "";
                    xml.append("<ltpnm>" + temp + "</ltpnm>");
                    temp = cursor.getString(34);
                    if (temp == null) temp = "0";
                    xml.append("<csig>" + temp + "</csig>");
                    temp = cursor.getString(35);
                    if (temp == null) temp = "0";
                    xml.append("<caud>" + temp + "</caud>");
                    temp = cursor.getString(36);
                    if (temp == null) temp = "0";
                    xml.append("<cimg>" + temp + "</cimg>");
                    temp = cursor.getString(37);
                    if (temp == null) temp = "0";
                    xml.append("<cnot>" + temp + "</cnot>");
                    xml.append("</event>");

                }
                while (type == DBHandler.DB_GET_ONLY_CURRENT_RECORD ? false : cursor.moveToNext());

                xml.append("</data>");

                return xml.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return null;
    }

    private static String createXMLForCustomerList(Cursor cursor,
                                                   Context context2, int type) {
        String tempxml = XML_NO_DATA;
        try {
            if (cursor.moveToFirst()) {
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");
                do {
                    xml.append("<cst>");
                    xml.append("<id>" + cursor.getLong(0) + "</id>");
                    xml.append("<tid>" + cursor.getLong(1) + "</tid>");
                    String temp = cursor.getString(2);
                    if (temp == null) temp = "";
                    xml.append("<ctpnm>" + temp + "</ctpnm>");
                    xml.append("<nm>" + cursor.getString(3) + "</nm>");
                    xml.append("</cst>");
                }
                while (type == DBHandler.DB_GET_ONLY_CURRENT_RECORD ? false : cursor.moveToNext());

                xml.append("</data>");

                return xml.toString();
            } else
                return tempxml;
        } catch (Exception e) {
            e.printStackTrace();
            return tempxml;
        } finally {
            cursor.close();
        }
    }

    public static String createXMLForSHIFTList(Cursor cursor) {
        try {
            if (cursor.moveToFirst()) {

                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");
                do {
                    xml.append("<shf>");
                    xml.append("<id>" + 0 + "</id>");
                    xml.append("<tid>" + cursor.getLong(1) + "</tid>");
                    xml.append("<nm>" + cursor.getString(2) + "</nm>");
                    xml.append("<tmslot>" + cursor.getString(3) + "</tmslot>");
                    xml.append("<brkslot>" + cursor.getString(8) + "</brkslot>");
                    xml.append("<adr>" + cursor.getString(9) + "</adr>");
                    xml.append("<dt>" + cursor.getString(4) + "</dt>");
                    xml.append("<terrgeo>" + cursor.getString(10) + "</terrgeo>");
                    xml.append("<stmp>" + cursor.getLong(7) + "</stmp>");
                    if (cursor.getLong(5) == 0) {
                        xml.append("<lib> </lib>");
                    } else {
                        xml.append("<lib>" + cursor.getLong(5) + "</lib>");
                    }

                    xml.append("</shf>");
                } while (cursor.moveToNext());

                xml.append("</data>");

                return xml.toString();
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            cursor.close();
        }
    }

    private static String createXMLForClientLocation(Cursor cursor,
                                                     Context context2, int type) {
        String tempxml = XML_NO_DATA;
        try {
            if (cursor.moveToFirst()) {
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");
                do {
                    xml.append("<loc>");
                    xml.append("<id>" + cursor.getLong(0) + "</id>");
                    xml.append("<geo>" + cursor.getString(1) + ","
                            + cursor.getString(2) + "</geo>");
                    xml.append("</loc>");
                }
                while (type == DBHandler.DB_GET_ONLY_CURRENT_RECORD ? false : cursor.moveToNext());

                xml.append("</data>");

                return xml.toString();
            } else
                return tempxml;
        } catch (Exception e) {
            e.printStackTrace();
            return tempxml;
        } finally {
            cursor.close();
        }
    }

    private static String createXMLForCusType(Cursor cursor, Context context, int type) {
        String tempxml = XML_NO_DATA;
        try {
            if (cursor.moveToFirst()) {
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");
                do {
                    xml.append("<ctype>");
                    xml.append("<id>" + cursor.getLong(0) + "</id>");
                    xml.append("<nm>" + cursor.getString(1) + "</nm>");
                    xml.append("</ctype>");
                }
                while (type == DBHandler.DB_GET_ONLY_CURRENT_RECORD ? false : cursor.moveToNext());

                xml.append("</data>");

                return xml.toString();
            } else
                return tempxml;
        } catch (Exception e) {
            e.printStackTrace();
            return tempxml;
        } finally {
            cursor.close();
        }
    }

    private static String createXMLForSiteType(Cursor cursor, Context context, int type) {
        String tempxml = XML_NO_DATA;
        try {
            if (cursor.moveToFirst()) {
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");
                do {
                    xml.append("<ltype>");
                    xml.append("<id>" + cursor.getLong(0) + "</id>");
                    xml.append("<nm>" + cursor.getString(1) + "</nm>");
                    xml.append("</ltype>");
                }
                while (type == DBHandler.DB_GET_ONLY_CURRENT_RECORD ? false : cursor.moveToNext());

                xml.append("</data>");

                return xml.toString();
            } else
                return tempxml;
        } catch (Exception e) {
            e.printStackTrace();
            return tempxml;
        } finally {
            cursor.close();
        }
    }

    public static String getXMLForErrorCode(Context context, String mErrorCode) {
        return XML_DATA_ERROR /*+ mErrorCode + XML_DATA_ERRORCODE_END*/;// YD commenting because i have change the xml in XML_DATA_ERROR
        //return "<data><success>false</success><id>0</id></data>";
    }

    public static String getXMLForSuccessCode(Context context, int mErrorCode) {
        return XML_DATA_SUCCESS_ERROR_MSG + mErrorCode + XML_DATA_ERRORCODE_END;
        //return "<data><success>false</success><id>0</id></data>";
    }

    public static String createXMLForOrderNotes(Cursor cursor, Context context, int type) {
        String tempxml = XML_NO_DATA;
        try {
            if (cursor.moveToFirst()) {
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");
                // xml.append("<![CDATA[");
                do {
                    //xml.append("<id>");
                    //xml.append(""+cursor.getInt(0));
                    //xml.append("</id>");
                    //xml.append("<nm>"+OrderNotes.ORDER_NOTE_FIELDNAME+"</nm>");
                    //xml.append("<value>"+cursor.getString(1)+"</value>");
                    String str = cursor.getString(1);
                    if (str == null)
                        str = "";
                    xml.append(str);

                }
                while (type == DBHandler.DB_GET_ONLY_CURRENT_RECORD ? false : cursor.moveToNext());
                // xml.append("]]>");
                xml.append("</data>");
                return xml.toString();
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return tempxml;
        } finally {
            cursor.close();
        }
    }

    public static String createXMLForOrderNotesSave(Cursor cursor, Context context, int type) {
        String tempxml = XML_NO_DATA;
        try {
            if (cursor.moveToFirst()) {
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");
                // xml.append("<![CDATA[");
                do {
                    xml.append("<id>");
                    xml.append("" + cursor.getInt(0));
                    xml.append("</id>");
                    xml.append("<nm>" + OrderNotes.ORDER_NOTE_FIELDNAME + "</nm>");
                    xml.append("<value>" + cursor.getString(1) + "</value>");

                }
                while (type == DBHandler.DB_GET_ONLY_CURRENT_RECORD ? false : cursor.moveToNext());
                // xml.append("]]>");
                xml.append("</data>");
                return xml.toString();
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return tempxml;
        } finally {
            cursor.close();
        }
    }

    public Object[] getServiceTypeValuesFromXML(String xml) {
        NodeList nl =
                parseXML(xml, XMLHandler.KEY_STYPE);
        Object[] serviceTypeList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            ServiceType sType = new ServiceType();
            sType.setId(Long.parseLong(getValue(element,
                    ServiceType.SERVICE_TYPE_ID)));
            sType.setNm(getValue(element, ServiceType.SERVICE_TYPE_NAME));
            sType.setDtl(getValue(element, ServiceType.SERVICE_TYPE_DTL));
            serviceTypeList[i] = sType;
        }
        return serviceTypeList;
    }

    public Object[] getWorkerListValuesFromXML(String xml) {
        NodeList nl = parseXML(xml, XMLHandler.KEY_RES);
        Object[] workerList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            try {
                Element element = (Element) nl.item(i);
                Worker worker = new Worker();
                worker.setId(Long
                        .parseLong(getValue(element, Worker.WORKER_ID)));
                worker.setNm(getValue(element, Worker.WORKER_NAME));
                worker.setDwrkwk(getValue(element, Worker.WORKER_DWEEK));
                worker.setWrkwk(getValue(element, Worker.WORKER_WEEK));
                worker.setBrk(getValue(element, Worker.WORKER_BRK));
                worker.setVehicleId(getValue(element, Worker.WORKER_VHLID) == null ? "" : getValue(element, Worker.WORKER_VHLID));
                try {
                    worker.setTid(Integer.parseInt(getValue(element, Worker.WORKER_TID)));
                    if (PreferenceHandler.getResId(context) == worker.getId()) {
                        SplashII.wrk_tid = worker.getTid();
                    }
                } catch (Exception e) {

                }
                worker.setWorkerShift(getValue(element, Worker.WORKER_SHIFT) == null
                        ? 0
                        : Integer.parseInt(getValue(element, Worker.WORKER_SHIFT)));
                Log.i( Utilities.TAG,
                        "res id : " + getValue(element, Worker.WORKER_LID));
                try {
                    worker.setLid(Long.parseLong(getValue(element,
                            Worker.WORKER_LID)));
                } catch (Exception e) {
                    worker.setLid(0);
                    e.printStackTrace();
                }
                workerList[i] = worker;
            } catch (Exception e) {
                e.printStackTrace();
                i--;
            }
        }
        return workerList;
    }


    public Object[] getPartTypeValuesFromXML(String xml) {// working YD
        NodeList nl = parseXML(xml, XMLHandler.KEY_PTYPE);
        Object[] partTypeList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            Parts part = new Parts();
            part.setId(Long.parseLong(getValue(element, Parts.PART_TYPE_ID)));
            part.setName(getValue(element, Parts.PART_TYPE_NAME));
            part.setDesc(getValue(element, Parts.PART_TYPE_DESC));
            part.setCtid(getValue(element, Parts.PART_TYPE_CTID));
            partTypeList[i] = part;
        }
        return partTypeList;
    }

    public Object[] getOrderValuesFromXML(String xml) {  //YD note
        Log.d("TAG555",xml);
        NodeList nl = parseXML(xml, XMLHandler.KEY_EVENT);
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
                AceRequestHandler.customerid = cid;
                temp = getValue(element, Order.ORDER_CUSTOMER_NAME);
                if (temp != null && temp != "") {
                    order.setCustName(getValue(element, Order.ORDER_CUSTOMER_NAME));
                }

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
                temp = getValue(element, Order.ORDER_CUSTOMER_SITE_GEOCODE);
                if (temp != "") {
                    order.setCustSiteGeocode(getValue(element,
                            Order.ORDER_CUSTOMER_SITE_GEOCODE));
                }

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
                if (temp != null) {
                    order.setInvoiceNumber(temp);
                } else {
                    order.setInvoiceNumber("");
                }
                /*
                 * order.setListAddWorkerPiped(getValue(element,
                 * Order.ORDER_STATUS_PRIMARY_WORKER_ID));
                 */
                order.setNm(getValue(element, Order.ORDER_NAME));
                temp = getValue(element, Order.ORDER_STATUS_ORDER_TYPE_ID);
                if (temp != null && temp != "") {
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
                if (order.getStatusId() > 3)
                    order.setSortindex(1);
                else
                    order.setSortindex(0);

                temp = getValue(element, Order.ORDER_ALERT);
                if (temp != null)
                    order.setOrderAlert(temp);
                else
                    order.setOrderAlert("");

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

                temp = getValue(element, Order.ORDER_CUSTOMER_SITE_ID);
                if (temp != "") {
                    order.setCustSiteId(temp);
                }
                temp = getValue(element, Order.ORDER_EST);
                if (temp != "") {
                    order.setEst(temp);
                }
                temp = getValue(element, Order.ORDER_LST);
                if (temp != "") {
                    order.setLst(temp);
                }
                temp = getValue(element, Order.ORDER_FLG);
                if (temp != "") {
                    order.setFlg(temp);
                }
                temp = getValue(element, Order.ORDER_CUSTOMER_TYPE_NAME);
                if (temp != null && temp != "") {
                    order.setCustypename(temp);
                }
                temp = getValue(element, Order.ORDER_SITE_TYPE_NAME);
                if (temp != null && temp != "") {
                    order.setSitetypename(temp);
                }
                temp = getValue(element, Order.ORDER_START_TIME);
                if (temp != null && temp != "") {
                    cid = Long.parseLong(temp);
                    order.setStartTime(cid);
                }
                temp = getValue(element, Order.ORDER_SITE_TYPE_NAME);
                if (temp != null && temp != "") {

                    order.setSitetypename(temp);
                }
                temp = getValue(element, Order.ORDER_AUDIO);
                if (temp != null && temp != "") {
                    cid = Long.parseLong(temp);
                    order.setAudioCount(cid);
                }
                temp = getValue(element, Order.ORDER_SIG);
                if (temp != null && temp != "") {
                    cid = Long.parseLong(temp);
                     order.setSigCount(cid);
                }
                temp = getValue(element, Order.ORDER_IMG);
                if (temp != null && temp != "") {
                    cid = Long.parseLong(temp);
                    order.setImgCount(cid);
                }
                temp = getValue(element, Order.ORDER_NOTE);
                if (temp != null && temp != "") {
                    cid = Long.parseLong(temp);
                    order.setNotCount(cid);
                }
                if(Order.ORDER_CUSTOMER_CONTACT_ID!=null) {
                    temp = getValue(element, Order.ORDER_CUSTOMER_CONTACT_ID);
                    if (temp != null && temp != ""&& !temp.equalsIgnoreCase("null")) {
                        cid = Long.parseLong(temp);
                        order.setContactId(cid);
                    }
                }

                temp = getValue(element, Order.ORDER_TEL_TYPEID);
                if (temp != null && temp != "") {
                    cid = Long.parseLong(temp);
                    order.setTelTypeId(cid);
                }

                temp = getValue(element, Order.ORDER_DOCS);
                if (temp != null && temp != "") {
                    cid = Long.parseLong(temp);
                    order.setDocCount(cid);
                }

//                temp = getValue(element, Order.ORDER_CUSTOMER_CTID);
//                if (temp != null && temp != "") {
//                    order.setCtids(temp);
//                }

                temp = getValue(element, Order.ORDER_CUSTOMER_CTID);
                if (temp != null && temp != "") {
                    order.setCustids(temp);
                }

                orderListValues[i] = order;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e( Utilities.TAG, "ERROR IN ORDER XML : " + oid);
            }
        }
        return orderListValues;
    }

    public Object[] getCustomerListValuesFromXML(String xml) {

        NodeList nl = parseXML(xml, XMLHandler.KEY_CST);
        Object[] customerList = new Object[nl.getLength()];
        try {
            for (int i = 0; i < nl.getLength(); i++) {
                Element element = (Element) nl.item(i);
                Customer customer = new Customer();
                String temp = getValue(element,
                        Customer.CUSTOMER_ID);
                if (temp != null)
                    customer.setId(Long.parseLong(temp));
                else
                    customer.setId(0);
                temp = getValue(element, Customer.CUSTOMER_TYPE);
                if (temp != null && temp != "")
                    customer.setTid(Long.parseLong(temp));
                else
                    customer.setTid(0);
                customer.setNm(getValue(element, Customer.CUSTOMER_NAME));
                customerList[i] = customer;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customerList;
    }

    public Object[] getClientLocationValuesFromXML(String xml) {
        NodeList nl = parseXML(xml, XMLHandler.KEY_LOC);
        Object[] clientLocationList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            ClientLocation cLoc = new ClientLocation();
            cLoc.setId(Long.parseLong(getValue(element,
                    ClientLocation.CLIENT_ID)));
            String[] geo = getValue(element, ClientLocation.CLIENT_GEO).split(
                    ",");
            ClientLocation.Geo geoLL = cLoc.new Geo();
            //ClientLocation.Geo geoLL =new ClientLocation.Geo();
            String lat_string = (String) geo[0];
            String long_string = (String) geo[0];
            if (lat_string != null && lat_string.contains("\"")) {
                lat_string = lat_string.replace("\"", "");
                long lat_ = Long.parseLong(lat_string);
                geoLL.setLatitude(lat_);
            }
            if (long_string != null && long_string.contains("\"")) {
                long_string = lat_string.replace("\"", "");
                long long_ = Long.parseLong(long_string);
                geoLL.setLongitude(long_);
            }
            cLoc.setGeo(geoLL);
            clientLocationList[i] = cLoc;
        }
        return clientLocationList;
    }

    public Object[] getCusTypeValuesFromXML(String xml) {
        NodeList nl = parseXML(xml, CustomerType.CUSTOMERTYPE_PARENT_TAG);
        Object[] custTypeList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            CustomerType customerType = new CustomerType();
            customerType.setId(Long.parseLong(getValue(element,
                    CustomerType.CUSTOMER_TYPE_ID)));
            customerType.setNm(getValue(element,
                    CustomerType.CUSTOMER_TYPE_NAME));
            custTypeList[i] = customerType;
        }
        return custTypeList;
    }


    public Object[] getSiteTypeValuesFromXML(String xml) {
        NodeList nl = parseXML(xml, SiteType.SITETYPE_PARENT_TAG);
        Object[] siteTypeList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            SiteType siteType = new SiteType();
            siteType.setId(Long.parseLong(getValue(element,
                    SiteType.SITE_TYPE_ID)));
            siteType.setNm(getValue(element,
                    SiteType.SITE_TYPE_NAME));
            siteType.setTid(Integer.valueOf(getValue(element,
                    siteType.SITE_TYPE_TID)));
            siteType.setUpd(Long.valueOf(getValue(element,
                    siteType.SITE_TYPE_UPD)));
            siteType.setCap(getValue(element,
                    SiteType.SITE_TYPE_CAP));
            siteType.setDtl(getValueCData(element,SiteType.SITE_TYPE_dtl));
            siteTypeList[i] = siteType;
        }

        return siteTypeList;
    }

    // This method is used to get total ids(one id represent a single record)
    // from the given xmlString.
    public long[] getIds(String xmlString) {
        NodeList nl = parseXML(xmlString, XMLHandler.KEY_CTYPE);
        long ids[] = new long[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            ids[i] = Long.parseLong(getValue(element, KEY_ID));
        }

        return ids;
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

    public Object[] getOrderServiceTypeValuesFromXML(String xml) {
        NodeList nl = parseXML(xml, XMLHandler.KEY_STYPE);
        int length = 1;
        Object[] orderServiceTypeList = new Object[length];

        // TODO fill this ordertask object by parsing the xml.
        /*
         * for( int i=0 ; i< nl.getLength(); i++ ){ Element element = (Element)
         * nl.item(i); OrderTask ordertask = new OrderTask();
         * ordertask.setId(Long.parseLong(getValue(element,
         * OrderTask.SERVICE_TYPE_ID))); sType.setNm(getValue(element,
         * ServiceType.SERVICE_TYPE_NAME)); orderServiceTypeList[i] = ordertask;
         * }
         */

        return orderServiceTypeList;
    }

    public Object[] getOrderTypeValuesFromXML(String xml) {
        NodeList nl = parseXML(xml, XMLHandler.KEY_OTYPE);
        Object[] orderTypeList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            OrderTypeList orderType = new OrderTypeList();
            orderType.setId(Long.parseLong(getValue(element,
                    OrderTypeList.ORDER_TYPE_ID)));
            orderType.setNm(getValue(element, OrderTypeList.ORDER_TYPE_NAME));
            orderTypeList[i] = orderType;
        }
        return orderTypeList;
    }

    public Object[] getStatusTypeValuesFromXML(String xml) {
        NodeList nl = parseXML(xml, XMLHandler.KEY_STATTYPE);
        Object[] orderStatTypeList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            String temp;
            Element element = (Element) nl.item(i);
            OrderStatus orderStatusType = new OrderStatus();
            orderStatusType.setId(Long.parseLong(getValue(element,
                    OrderStatus.STATUS_ID)));
            orderStatusType.setNm(getValue(element, OrderStatus.STATUS_TYPE_NM));
            orderStatusType.setAbbrevation(getValue(element, OrderStatus.STATUS_TYPE_ABR));

            temp = getValue(element, OrderStatus.STATUS_TYPE_GRPID);
            if (temp != null && temp != "") {
                orderStatusType.setGrpId(Integer.parseInt(temp));
            }

            temp = getValue(element, OrderStatus.STATUS_TYPE_GRPSEQ);
            if (temp != null && temp != "") {
                orderStatusType.setGrpSeq(Integer.parseInt(temp));
            }

            temp = getValue(element, OrderStatus.STATUS_TYPE_ISGRP);
            if (temp != null && temp != "") {
                orderStatusType.setIsgroup(Integer.parseInt(temp));
            }

            temp = getValue(element, OrderStatus.STATUS_TYPE_ISVIS);
            if (temp != null && temp != "") {
                orderStatusType.setIsVisible(Integer.parseInt(temp));
            }

            temp = getValue(element, OrderStatus.STATUS_TYPE_SEQ);
            if (temp != null && temp != "") {
                orderStatusType.setSeq(Integer.parseInt(temp));
            }

            orderStatTypeList[i] = orderStatusType;
        }
        return orderStatTypeList;
    }

    public Object[] getClientSiteValuesFromXML(String xml) {//KB for clientsite
        NodeList nl = parseXML(xml, XMLHandler.KEY_CLIENTSITE);
        Object[] clientsiteList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            String temp;
            Element element = (Element) nl.item(i);
            ClientSite clientsite = new ClientSite();// TODO KUBER MAKE CLIENT SITE OBJECT AND FILL DATA
            clientsite.setId(Long.parseLong(getValue(element,
                    ClientSite.CLIENTSITE_ID)));

            temp = getValue(element, ClientSite.CLIENTSITE_NAME);
            if (temp != null && temp != "") {
                clientsite.setNm(temp);
            }

            temp = getValue(element, ClientSite.CLIENTSITE_ADR);
            if (temp != null && temp != "") {
                clientsite.setAdr(temp);
            }

            temp = getValue(element, ClientSite.CLIENTSITE_ADR2);
            if (temp != null && temp != "") {
                clientsite.setAdr2(temp);
            }

            temp = getValue(element, ClientSite.CLIENTSITE_TZ);
            if (temp != null && temp != "") {
                clientsite.setTz(temp);
            }

            temp = getValue(element, ClientSite.CLIENTSITE_XID);
            if (temp != null && temp != "") {
                clientsite.setXid(Long.parseLong(temp));
            }

            temp = getValue(element, ClientSite.CLIENTSITE_GEO);
            if (temp != null && temp != "") {
                clientsite.setGeo(temp);
            }

            temp = getValue(element, ClientSite.CLIENTSITE_BY);
            if (temp != null && temp != "") {
                clientsite.setBy(temp);
            }

            temp = getValue(element, ClientSite.CLIENTSITE_UPD);
            if (temp != null && temp != "") {
                clientsite.setUpd(Long.parseLong(temp));
            }

            clientsiteList[i] = clientsite;
        }
        return clientsiteList;
    }

    public Object[] getShiftValuesFromXML(String xml) {//KB , shift xml parsing
        NodeList nl = parseXML(xml, Shifts.SHIFT_PARENT_TAG);
        Object[] shiftList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            String temp;
            Element element = (Element) nl.item(i);
            Shifts shift = new Shifts();
            shift.setId(Long.parseLong(getValue(element,
                    Shifts.SHIFT_ID)));

            temp = getValue(element, Shifts.SHIFT_TID);
            if (temp != null) {
                if (temp.trim().equals("")) {
                    temp = "0";
                }
                shift.setTid(Long.parseLong(temp));
            }

            temp = getValue(element, Shifts.SHIFT_NM);
            if (temp != null) {
                shift.setNm(temp);
            }

            temp = getValue(element, Shifts.SHIFT_ADR);
            if (temp != null) {
                shift.setAddress(temp);
            }

            temp = getValue(element, Shifts.SHIFT_TMSLOT);
            if (temp != null) {
                shift.setTmslot(temp);
            }

            temp = getValue(element, Shifts.SHIFT_BRKSLOT);
            if (temp != null) {
                shift.setBrkSlot(temp);
            }

            temp = getValue(element, Shifts.SHIFT_TERRI);
            if (temp != null) {
                shift.setTerri(temp);
            }

            temp = getValue(element, Shifts.SHIFT_DT);
            if (temp != null) {
                shift.setDt(temp);
            }

            temp = getValue(element, Shifts.SHIFT_LID);
            if (temp != null) {
                if (temp.trim().equals("")) {
                    temp = "0";
                }
                shift.setLid(Long.parseLong(temp));
            }

            temp = getValue(element, Shifts.SHIFT_GACC);
            if (temp != null) {
                if (temp.trim().equals("")) {
                    temp = "0";
                }
                shift.setGacc(Long.parseLong(temp));
            }

            temp = getValue(element, Shifts.SHIFT_CRT);
            if (temp != null) {
                if (temp.trim().equals("")) {
                    temp = "0";
                }
                shift.setLocalid(Long.parseLong(temp));
            }
            shiftList[i] = shift;
        }
        return shiftList;
    }

    public Object[] getAppHeadersFromXMLForPg(Context context, String xml) {
        Log.d("TAG92",xml);
        NodeList nl = parseXML(xml, XMLHandler.KEY_TERM);
        Object[] orderStatTypeList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            PreferenceHandler.setFormHead(context, getValue(element, "lfrm").toUpperCase());// YD setting header for task
            PreferenceHandler.setPartHead(context, getValue(element, "lprt").toUpperCase()); // YD setting header for part
            PreferenceHandler.setAssetsHead(context, getValue(element, "lass").toUpperCase());//YD setting header for assets
            PreferenceHandler.setPictureHead(context, getValue(element, "lpic").toUpperCase());//YD setting header for pic
            PreferenceHandler.setAudioHead(context, getValue(element, "laud").toUpperCase());//YD setting header for audio
            PreferenceHandler.setSignatureHead(context, getValue(element, "lsig").toUpperCase());//YD setting header for sig
            PreferenceHandler.setFileHead(context, getValue(element, "lfil").toUpperCase());//YD setting header for file
            PreferenceHandler.setWorkerHead(context, getValue(element, "lwrk"));//YD setting header for worker
            PreferenceHandler.setCustomerHead(context, getValue(element, "lcst"));//YD setting header for customer
            PreferenceHandler.setOrderHead(context, getValue(element, "lord"));//YD setting header for order
            PreferenceHandler.setOrderNameHead(context, getValue(element, "lordnm"));//YD setting header for order name
            PreferenceHandler.setPOHead(context, getValue(element, "lpo").toUpperCase());
            PreferenceHandler.setInvHead(context, getValue(element, "linv").toUpperCase());
            PreferenceHandler.setOrderDescHead(context, getValue(element, "ldtl"));//YD setting header for detail
            PreferenceHandler.setAlertHead(context, getValue(element, "lalt"));//YD setting header for alert
            PreferenceHandler.setNoteHead(context, getValue(element, "lnot"));//YD setting header for note
            PreferenceHandler.setSummaryHead(context, getValue(element, "lsum"));//YD setting header for summary
            PreferenceHandler.setPrefOrderGroup(context, getValue(element, "lordgrp"));
            PreferenceHandler.setPrefFieldRules(context, getValue(element, "fldordrls"));

		/*	ltsk = Task
			lprt = Part
			lass = Asset
			lpic = Picture
			laud = Audio
			lsig = Signature
			lfil = File
			lwrk = Worker
			lcst = Customer
			lord = Order
			lordnm = Order Name
					lpo = Order PO
					linv = Order Inv
					ldtl = Order Description
					lalt = Order Alert
					lnot = Order Note
					lsum = Summary (header for Alert and Notes screen)*/
        }

        return orderStatTypeList;
    }

    public Object[] getAppHeadersFromXMLForAssets(Context context, String xml) {
        NodeList nl = parseXML(xml, XMLHandler.KEY_AST_TERM);
        Object[] orderStatTypeList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            Element assetElements = (Element) nl.item(i);
            AssetLabel asstLbl = new AssetLabel();
            asstLbl.setTid(getValue(assetElements, "tid"));
            asstLbl.setTid2(getValue(assetElements, "tid2"));
            asstLbl.setCnt1(getValue(assetElements, "cnt1"));
            asstLbl.setCnt2(getValue(assetElements, "cnt2"));
            asstLbl.setPid(getValue(assetElements, "pid"));
            asstLbl.setStat(getValue(assetElements, "stat"));
            asstLbl.setNum1(getValue(assetElements, "num1"));
            asstLbl.setNum2(getValue(assetElements, "num2"));
            asstLbl.setNum3(getValue(assetElements, "num3"));
            asstLbl.setNum4(getValue(assetElements, "num4"));
            asstLbl.setNum5(getValue(assetElements, "num5"));
            asstLbl.setNum6(getValue(assetElements, "num6"));
            asstLbl.setNote1(getValue(assetElements, "note1"));
            asstLbl.setNote2(getValue(assetElements, "note2"));
            asstLbl.setNote3(getValue(assetElements, "note3"));
            asstLbl.setNote4(getValue(assetElements, "note4"));
            asstLbl.setNote5(getValue(assetElements, "note5"));
            asstLbl.setNote6(getValue(assetElements, "note6"));
            asstLbl.setCt1(getValue(assetElements, "ct1"));
            asstLbl.setCt2(getValue(assetElements, "ct2"));
            asstLbl.setCt3(getValue(assetElements, "ct3"));
            asstLbl.setRecGeo(getValue(assetElements, "recgeo"));

            PreferenceHandler.setAssetLabels(context, asstLbl);
        }

        return orderStatTypeList;
    }

    public Object[] getAppHeadersFromXML(Context context, String xml) {
        NodeList nl = parseXML(xml, XMLHandler.KEY_LABELS);
        Object[] orderStatTypeList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            String temp;
            Element element = (Element) nl.item(i);

            NodeList gen = element.getElementsByTagName("gen");
            for (int j = 0; j < gen.getLength(); j++) {
                Element genElements = (Element) gen.item(j);
                PreferenceHandler.setFormHead(context, getValue(genElements, "tlbl"));// YD setting header for task
                PreferenceHandler.setPartHead(context, getValue(genElements, "plbl")); // YD setting header for part
                PreferenceHandler.setAssetsHead(context, getValue(genElements, "albl"));
            }

            NodeList ord = element.getElementsByTagName("ord");
            for (int k = 0; k < ord.getLength(); k++) {
                Element ordElements = (Element) ord.item(k);
                PreferenceHandler.setPOHead(context, getValue(ordElements, "polbl"));
                PreferenceHandler.setInvHead(context, getValue(ordElements, "invlbl"));
            }

            NodeList asset = element.getElementsByTagName("ast");
            for (int k = 0; k < asset.getLength(); k++) {
                Element assetElements = (Element) asset.item(k);
                AssetLabel asstLbl = new AssetLabel();
                asstLbl.setTid(getValue(assetElements, "tid"));
                asstLbl.setTid2(getValue(assetElements, "tid2"));
                asstLbl.setCnt1(getValue(assetElements, "cnt1"));
                asstLbl.setCnt2(getValue(assetElements, "cnt2"));
                asstLbl.setPid(getValue(assetElements, "pid"));
                asstLbl.setStat(getValue(assetElements, "stat"));
                asstLbl.setNum1(getValue(assetElements, "num1"));
                asstLbl.setNum2(getValue(assetElements, "num2"));
                asstLbl.setNum3(getValue(assetElements, "num3"));
                asstLbl.setNum4(getValue(assetElements, "num4"));
                asstLbl.setNum5(getValue(assetElements, "num5"));
                asstLbl.setNum6(getValue(assetElements, "num6"));
                asstLbl.setNote1(getValue(assetElements, "note1"));
                asstLbl.setNote2(getValue(assetElements, "note2"));
                asstLbl.setNote3(getValue(assetElements, "note3"));
                asstLbl.setNote4(getValue(assetElements, "note4"));
                asstLbl.setNote5(getValue(assetElements, "note5"));
                asstLbl.setNote6(getValue(assetElements, "note6"));
                asstLbl.setCt1(getValue(assetElements, "ct1"));
                asstLbl.setCt2(getValue(assetElements, "ct2"));
                asstLbl.setCt3(getValue(assetElements, "ct3"));

                PreferenceHandler.setAssetLabels(context, asstLbl);
            }

        }
        return orderStatTypeList;
    }

    public Object[] getOrderTaskValuesFromXML(String xml) { // called when download From sever YD
        NodeList nl = parseXML(xml, XMLHandler.KEY_OSRV);
        Object[] orderTaskList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            OrderTask orderTask = new OrderTask();
            orderTask.setId(Long
                    .parseLong(getValue(element, OrderTask.ORDER_ID)));
            orderTask.setOrder_task_id(Long.parseLong(getValue(element,
                    OrderTask.ORDER_TASK_ID)));
            orderTask.setTask_id(Long.parseLong(getValue(element,
                    OrderTask.TASK_ID)));
            if (getValue(element, OrderTask.ORDER_SERV_UPDATE_TIME) != null && !getValue(element, OrderTask.ORDER_SERV_UPDATE_TIME).equals(""))
                orderTask.setUpd_time(Long.parseLong(getValue(element,
                        OrderTask.ORDER_SERV_UPDATE_TIME)));
            else
                orderTask.setUpd_time(0);
            orderTask.setAction_status(getValue(element, OrderTask.ACTION_STATUS));
            orderTask.setPriority(getValue(element, OrderTask.PRIORITY));
            orderTask.setTree_actualcount(getValue(element, OrderTask.TREE_ACTUALCOUNT));
            orderTask.setTree_alert(getValue(element, OrderTask.TREE_ALERT));
            orderTask.setTree_clearence(getValue(element, OrderTask.TREE_CLEARENCE));
            orderTask.setTree_comment(getValue(element, OrderTask.TREE_COMMENT));
            orderTask.setTree_cycle(getValue(element, OrderTask.TREE_CYCLE));
            orderTask.setTree_dia(getValue(element, OrderTask.TREE_DIA));
            orderTask.setTree_expcount(getValue(element, OrderTask.TREE_EXPCOUNT));
            orderTask.setTree_ht(getValue(element, OrderTask.TREE_HT));
            orderTask.setTree_msc(getValue(element, OrderTask.TREE_MSC));
            orderTask.setTree_note(getValue(element, OrderTask.TREE_NOTE));
            orderTask.setTree_owner(getValue(element, OrderTask.TREE_OWNER));
            orderTask.setTree_pcomment(getValue(element, OrderTask.TREE_PCOMMENT));
            orderTask.setTree_timespent(getValue(element, OrderTask.TREE_TIMESPENT));
            orderTask.setTree_tm(getValue(element, OrderTask.TREE_TM));
            orderTask.setTree_type(getValue(element, OrderTask.TRIM_TYPE));
            orderTask.setTree_geo(getValue(element, OrderTask.TREE_GEO));
            orderTask.setTree_accesspath(getValue(element, OrderTask.TREE_ACCESSPATH));

            if (!getValue(element, OrderTask.TREE_CT1).equals(""))
                orderTask.setTree_ct1(getValue(element, OrderTask.TREE_CT1));
            else
                orderTask.setTree_ct1("1");

            if (!getValue(element, OrderTask.TREE_CT2).equals(""))
                orderTask.setTree_ct2(getValue(element, OrderTask.TREE_CT2));
            else
                orderTask.setTree_ct2("1");

            if (!getValue(element, OrderTask.TREE_CT3).equals(""))
                orderTask.setTree_ct3(getValue(element, OrderTask.TREE_CT3));
            else
                orderTask.setTree_ct3("1");

            orderTaskList[i] = orderTask;
        }
        return orderTaskList;
    }

    public Object[] getOrderCustSiteValuesFromXML(String xml) {
        NodeList nl = parseXML(xml, "loc");
        Object[] orderCustSiteList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            OrderCustSite custSite = new OrderCustSite();
            custSite.setCust_siteid(getValue(element, "id"));
            custSite.setCust_id(getValue(element, "cid"));
            custSite.setCust_name(getValue(element, "nm"));
            custSite.setCust_adr(getValue(element, "adr"));
            custSite.setCust_upd(getValue(element, "upd"));
            custSite.setCust_dtl(getValue(element, "dtl"));
            custSite.setCust_tid(getValue(element, "tid"));
            custSite.setCust_ltpnm(getValue(element, "ltpnm"));
            custSite.setCust_geo(getValue(element, "geo"));
            orderCustSiteList[i] = custSite;
        }
        return orderCustSiteList;
    }

    public Object[] getOrderPartValuesFromXML(String xml) {
        NodeList nl = parseXML(xml, XMLHandler.KEY_OPRT);
        Object[] orderPartList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            OrderPart orderPart = new OrderPart();
            orderPart.setOid(Long
                    .parseLong(getValue(element, OrderPart.ORDER_ID)));
            orderPart.setOrder_part_id(Long.parseLong(getValue(element,
                    OrderPart.ORDER_PART_ID)));
            orderPart.setPart_type_id(Long.parseLong(getValue(element,
                    OrderPart.PART_TID)));
            orderPart.setOrder_part_QTY(getValue(element,
                    OrderPart.ORDER_PART_QTY));
            orderPart.setPart_barcode(getValue(element,
                    OrderPart.ORDER_BARCODE));
            orderPart.setUpd_time(Long.valueOf(getValue(element,
                    OrderPart.ORDER_UPDATE_TIME)));
            orderPartList[i] = orderPart;
        }
        return orderPartList;
    }

    public Object[] getOrderFormValuesFromXML(String xml) {
       // Log.d("TAG77",xml);
        NodeList nl = parseXML(xml, XMLHandler.KEY_OFRM);
        Object[] orderFormList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            Form orderForm = new Form();
            orderForm.setOid(Long
                    .parseLong(getValue(element, Form.FORM_OID)));
            orderForm.setId(Long.parseLong(getValue(element,
                    Form.FORM_ID)));
            orderForm.setFtid(getValue(element,
                    Form.FORM_TID));
            orderForm.setFdata(getValueCData(element,
                    Form.FORM_FDATA));
            orderForm.setUpd(Long.valueOf(getValue(element,
                    Form.FORM_UPD)));
            orderForm.setFormkeyonly(getValue(element,
                    Form.FORM_KEY_ONLY));
            orderFormList[i] = orderForm;
        }
        return orderFormList;
    }

	/*public Object[] checkXML(String xml) {
		NodeList nl = parseXML(xml, OrderMedia.ORDER_PARENT_TAG);
		Object[] ordermedialist = new Object[nl.getLength()];
		return ordermedialist;
	}*/

    public Object[] getOrderMediaValuesFromXML(String xml) {
        Log.d("TAG77",xml);
        NodeList nl = parseXML(xml, OrderMedia.ORDER_PARENT_TAG);
        Object[] ordermedialist = new Object[nl.getLength()];
        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            OrderMedia orderMedia = new OrderMedia();
            orderMedia.setId(Long.parseLong(getValue(element, OrderMedia.ID)));
            orderMedia.setOrderid(Long.parseLong(getValue(element,
                    OrderMedia.ORDER_ID)));
            orderMedia.setFile_desc(String.valueOf(getValue(element,
                    OrderMedia.ORDER_FILE_DESC)));
            orderMedia.setMimetype(getValue(element,
                    OrderMedia.ORDER_FILE_MIME_TYPE));
            orderMedia.setGeocode(getValue(element, OrderMedia.ORDER_GEO));
            Log.i( Utilities.TAG,
                    "value : " + getValue(element, OrderMedia.ORDER_MEDIA_TYPE));
            Log.i(
                    Utilities.TAG,
                    "value : "
                            + Integer.parseInt(getValue(element,
                            OrderMedia.ORDER_MEDIA_TYPE)));
            orderMedia.setMediatype(Integer.parseInt(getValue(element,
                    OrderMedia.ORDER_MEDIA_TYPE)));
            orderMedia.setUpd_time(Long.parseLong(getValue(element,
                    OrderMedia.ORDER_META_UPDATE_TIME)));

            if (getValue(element, OrderMedia.ORDER_META_PATH) != null)
                orderMedia.setMetapath((getValue(element, OrderMedia.ORDER_META_PATH)));
            else
                orderMedia.setMetapath(OrderMedia.ORDER_META_PATH);

            orderMedia.setFileName((getValue(element, OrderMedia.ORDER_FILE_NM)));

            Log.d("TAG88",getValue(element,OrderMedia.FORM_KEY));

            if (getValue(element, OrderMedia.FORM_KEY) != null) {
                orderMedia.setFrmkey((getValue(element, OrderMedia.FORM_KEY)));
            }
//            else
//                orderMedia.setFrmkey(OrderMedia.FORM_KEY);


            if (getValue(element, OrderMedia.FORM_FIELD_ID) != null)
                orderMedia.setFrmfiledid((getValue(element,OrderMedia.FORM_FIELD_ID)));
            else
                orderMedia.setFrmfiledid(OrderMedia.FORM_FIELD_ID);
//

            // orderMedia.setData(getValue(element,
            // OrderMedia.ORDER_FILE_DATA).getBytes());
            ordermedialist[i] = orderMedia;
        }
        return ordermedialist;
    }

    public Object[] getOrderNotesValuesFromXML(String xml, long id) {

        Object[] ordernoteslist = new Object[1];
        String note = xml.substring(xml.indexOf("<data>") + 6,
                xml.indexOf("</data>"));
        OrderNotes orderNotes = new OrderNotes();


        orderNotes.setId(id);
        orderNotes.setOrdernote(note);
        Log.d("note",""+note);
        ordernoteslist[0] = orderNotes;
        ContentValues cv = new ContentValues();
        cv.put(Order.ORDER_NOTES, note);
        String whereClause = Order.ORDER_ID + "=" +id;
        DBHandler.updateTable(DBHandler.orderListTable, cv, whereClause, null);
        return ordernoteslist;

    }

    //YD Using for customer history
    public String getCustTokenValuesFromXML(String xml) {
        NodeList nl = parseXML(xml, KEY_DATA);

        Element element = (Element) nl.item(0);
        return getValue(element, Site.SITE_ID);// using site.siteid as we just getting id in xml and we want the content else there is no relation with site
    }

    //YD Using for customer history
    public String getCustTokenValue(String xml) {//YD xml parsing demo vvvimp xml: <fkey>software.com|234001|55|47348003|1464172919548</fkey>
        NodeList nl = parseXML(xml, "fkey");
        Element element = (Element) nl.item(0);
        return element.getChildNodes().item(0).getNodeValue();
    }//parseXML(xml,"fkey").item(0).getChildNodes().item(0).getNodeValue()

    public Object[] getSiteValuesFromXML(String xml) {
        NodeList nl = parseXML(xml, Site.SITE_PARENT_TAG);
        Object[] siteList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            Site site = new Site();
            site.setId(Long.parseLong(getValue(element, Site.SITE_ID)));
            site.setCid(Long.parseLong(getValue(element, Site.SITE_CID)));
            site.setNm(getValue(element, Site.SITE_NAME));
            site.setAdr(getValue(element, Site.SITE_ADDRESS));
            site.setAdr2(getValue(element, Site.SITE_ADR2));
            site.setSt(getValue(element, Site.SITE_ST));
            site.setGeo(getValue(element, Site.SITE_GEO));
            site.setDetail(getValue(element, Site.SITE_DTL));
            site.setTid(Long.parseLong(getValue(element, Site.SITE_TYPE_ID)));
            site.setUpd(Long.parseLong(getValue(element, Site.SITE_UPD)));
            siteList[i] = site;
        }
        return siteList;
    }

    public Object[] getContactValuesFromXML(String xml) {
        NodeList nl = parseXML(xml, CustomerContact.CUSTOMERCONTACT_PARENT_TAG);
        Object[] contactList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            CustomerContact cct = new CustomerContact();
            cct.setId(Long.parseLong(getValue(element, CustomerContact.CONTACT_ID)));
            cct.setCustomerid(Long.parseLong(getValue(element, CustomerContact.CUSTOMER_ID)));
            cct.setContactType(Long.parseLong(getValue(element, CustomerContact.CONTACT_TYPE)));
            cct.setContacttel(getValue(element, CustomerContact.CONTACT_TEL));
            cct.setContactname(getValue(element, CustomerContact.CONTACT_NAME));
            contactList[i] = cct;
        }
        return contactList;
    }

    public Object[] getAssetValuesFromXML(String xml) {
        NodeList nl = parseXML(xml, Assets.ASSET_PARENT_TAG);
        Object[] assetList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            Assets asst = new Assets();
            asst.setId(Long.parseLong(getValue(element, Assets.ASSET_ID)));
            asst.setCid(Long.parseLong(getValue(element, Assets.ASSET_CID)));

            String temp;
           temp = getValue(element, Assets.ASSET_FTYPE_ID);//YD 2020
            if (temp != null && temp != "")
                asst.setFtid(Long.parseLong(temp));
            else
                asst.setFtid(0);

            temp = getValueCData(element, Assets.ASSET_FDATA);
            if (temp != null && temp != "")
                asst.setFdata(temp);
            else
                asst.setFdata("");

            /* temp = getValue(element, Assets.ASSET_PID);
            if (temp != null && temp != "")
                asst.setPid(Long.parseLong(temp));
            else
                asst.setPid(0);

            temp = getValue(element, Assets.ASSET_STAT);
            if (temp != null && temp != "")
                asst.setStatus(temp);
            else
                asst.setStatus("");*/

            temp = getValue(element, Assets.ASSET_GEO);
            if (temp != null && temp != "")
                asst.setGeoLoc(temp);
            else
                asst.setGeoLoc("");

            temp = getValue(element, Assets.ASSET_UPD);
            if (temp != null && temp != "")
                asst.setUpd(Long.parseLong(temp));
            else
                asst.setUpd(0);

            /*temp = getValue(element, Assets.ASSET_CONT1);//YD 2020
            if (temp != null && temp != "")
                asst.setContact1(Long.parseLong(temp));
            else
                asst.setContact1(0);

            temp = getValue(element, Assets.ASSET_CONT2);
            if (temp != null && temp != "")
                asst.setContact2(Long.parseLong(temp));
            else
                asst.setContact2(0);

            temp = getValue(element, Assets.ASSET_NUM1);
            if (temp != null && temp != "" && temp.matches("-?\\d+(\\.\\d+)?"))
                asst.setNumber1(Long.parseLong(temp));
            else
                asst.setNumber1(0);

            temp = getValue(element, Assets.ASSET_NUM2);
            if (temp != null && temp != ""&& temp.matches("-?\\d+(\\.\\d+)?"))
                asst.setNumber2(Long.parseLong(temp));
            else
                asst.setNumber2(0);

            temp = getValue(element, Assets.ASSET_NUM3);
            if (temp != null && temp != ""&&temp.matches("-?\\d+(\\.\\d+)?"))
                try {
                    asst.setNumber3(Long.parseLong(temp));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    asst.setNumber3(0);
                }
            else
                asst.setNumber3(0);

            temp = getValue(element, Assets.ASSET_NUM4);
            if (temp != null && temp != ""&& temp.matches("-?\\d+(\\.\\d+)?"))
                asst.setNumber4(Long.parseLong(temp));
            else
                asst.setNumber4(0);

            temp = getValue(element, Assets.ASSET_NUM5);
            if (temp != null && temp != "" && temp.matches("-?\\d+(\\.\\d+)?"))
                asst.setNumber5(Long.parseLong(temp));
            else
                asst.setNumber5(0);

            temp = getValue(element, Assets.ASSET_NUM6);
            if (temp != null && temp != ""&&temp.matches("-?\\d+(\\.\\d+)?"))
                asst.setNumber6(Long.parseLong(temp));
            else
                asst.setNumber6(0);

            temp = getValue(element, Assets.ASSET_NOTE1);
            if (temp != null && temp != "")
                asst.setNote1(temp);
            else
                asst.setNote1("");

            temp = getValue(element, Assets.ASSET_NOTE2);
            if (temp != null && temp != "")
                asst.setNote2(temp);
            else
                asst.setNote2("");

            temp = getValue(element, Assets.ASSET_NOTE3);
            if (temp != null && temp != "")
                asst.setNote3(temp);
            else
                asst.setNote3("");

            temp = getValue(element, Assets.ASSET_NOTE4);
            if (temp != null && temp != "")
                asst.setNote4(temp);
            else
                asst.setNote4("");

            temp = getValue(element, Assets.ASSET_NOTE5);
            if (temp != null && temp != "")
                asst.setNote5(temp);
            else
                asst.setNote5("");

            temp = getValue(element, Assets.ASSET_NOTE6);
            if (temp != null && temp != "")
                asst.setNote6(temp);
            else
                asst.setNote6("");

            temp = getValue(element, Assets.ASSET_CT1);
            if (temp != null && temp != "")
                asst.setCt1(temp);
            else
                asst.setCt1("");

            temp = getValue(element, Assets.ASSET_CT2);
            if (temp != null && temp != "")
                asst.setCt2(temp);
            else
                asst.setCt2("");

            temp = getValue(element, Assets.ASSET_CT3);
            if (temp != null && temp != "")
                asst.setCt3(temp);
            else
                asst.setCt3("");*/

            assetList[i] = asst;
        }
        return assetList;
    }

    /**
     * YD get asset value type
     *
     * @param xml
     * @return
     */
    public Object[] getAssetTypeValuesFromXML(String xml) {//YD using for asset type
        NodeList nl = parseXML(xml, AssetsType.ASSETSTYPE_PARENT_TAG);
        Object[] assetTypeList = new Object[nl.getLength()];

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            AssetsType assetType = new AssetsType();
            assetType.setId(Long.parseLong(getValue(element,
                    AssetsType.ASSETS_TYPE_ID)));
            String temp;

            temp = getValue(element, AssetsType.ASSETS_TYPE_XID);
            if (temp != null && !temp.equals(""))
                assetType.setXid(Long.parseLong(getValue(element,
                        AssetsType.ASSETS_TYPE_XID)));
            else
                assetType.setXid(0);

            temp = getValue(element, AssetsType.ASSETS_TYPE_UPD);
            if (temp != null && !temp.equals(""))
                assetType.setUpd(Long.parseLong(getValue(element,
                        AssetsType.ASSETS_TYPE_UPD)));
            else
                assetType.setUpd(0);

            assetType.setName(getValue(element, AssetsType.ASSETS_TYPE_NAME));
            assetTypeList[i] = assetType;
        }
        return assetTypeList;
    }

    /**
     * This method checks resid in rid and rid2.
     *
     * @param x_element
     * @param cur_res_id
     * @return returns string containing xml of contained resids.
     * @throws TransformerConfigurationException
     * @throws TransformerFactoryConfigurationError
     */
    public String searchResId(String x_element, long cur_res_id)
            throws TransformerConfigurationException,
            TransformerFactoryConfigurationError {
        NodeList nl = parseXML(x_element, Order.ORDER_PARENT_TAG);
        StringBuffer tempXml = new StringBuffer();

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            long rid = 0;
            /*
             * String temp = getValue(element,
             * Order.ORDER_STATUS_PRIMARY_WORKER_ID); if (temp.length() > 1) {
             * rid = Long.parseLong(temp); }
             */

            /*
             * boolean isInBoundaryDate = false; try { String startDate =
             * getValue(element, Order.ORDER_START_DATE);
             * Log.i( Utilities.TAG, "date : "+startDate); Date date = new
             * Date(startDate); isInBoundaryDate = Utilities.isValidDate(date);
             * Log.i( Utilities.TAG, "start Date valid : " +
             * isInBoundaryDate); } catch (Exception e) { e.printStackTrace();
             * isInBoundaryDate = false; }
             */
            // if (rid == cur_res_id) {
            /*
             * if (isInBoundaryDate) { String xml =
             * Utilities.convertElementToString(element); tempXml.append(xml); }
             *
             * if (!shouldBoundaryConsider) {
             */
            // String xml = Utilities.convertElementToString(element);
            // tempXml.append(xml);
            /*
             * else { String oid = String.valueOf(getValue(element,
             * Order.ORDER_ID)); Log.i( Utilities.TAG, "2"); String xml =
             * DBEngine.getOrders(context, oid); if (xml != null) {
             * Log.i( Utilities.TAG, "3"); String xml2 =
             * Utilities.convertElementToString(element); tempXml.append(xml2);
             * } }
             */
            // } else {
            String temp = getValue(element,
                    Order.ORDER_STATUS_PRIMARY_WORKER_ID);
            if (temp != null && !temp.equals("")) {
                // if (isInBoundaryDate) {
                // why we used it here (puneet)
                String tempIds[] = null;
                if (temp.contains("|")) {
                    temp = temp.replace("|", ",");
                    tempIds = temp.split(",");
                    Log.i( Utilities.TAG, temp + " : " + tempIds.length);
                } else {
                    String temps[] = {temp};
                    tempIds = temps;
                }
                int tidcounter = 0;
                long rid2 = 0;
                while (tidcounter < tempIds.length) {
                    rid2 = Long.parseLong(tempIds[tidcounter]);
                    if (rid2 == cur_res_id) {
                        String xml = Utilities.convertElementToString(element);
                        Log.w(Utilities.TAG, "Adding xml to refined xml : "
                                + xml);
                        tempXml.append(xml);
                    }
                    // Decreasing counts in order table.
                    tidcounter++;
                }
                // }

                // XXX do something for this code
                /*
                 * if (!shouldBoundaryConsider) { String tempIds[] = null; if
                 * (temp != null && temp.contains("|")) { temp =
                 * temp.replace("|", ","); tempIds = temp.split(",");
                 * Log.i( Utilities.TAG, temp + " : " + tempIds.length); }
                 * else { String temps[] = { temp }; tempIds = temps; } byte
                 * tidcounter = 0; long rid2 = 0; while (tidcounter <
                 * tempIds.length) { rid2 = Long.parseLong(tempIds[tidcounter]);
                 * if (rid2 == cur_res_id) { String xml = Utilities
                 * .convertElementToString(element); tempXml.append(xml); } //
                 * Decreasing counts in order table. tidcounter++; }
                 */
            } /*
             * else { String oid = String.valueOf(getValue(element,
             * Order.ORDER_ID)); String xml = DBEngine.getOrders(context, oid);
             * if (xml != null) { String xml2 = Utilities
             * .convertElementToString(element); tempXml.append(xml2); } }
             */
            // }
            // }
        }
        if (tempXml != null && tempXml.length() > 0) {
            String finalXml = "<data>" + tempXml + "</data>";
            Log.i( Utilities.TAG, "Returning xml from searchResId : "
                    + finalXml);
            return finalXml;
        }
        return null;
    }

    public String checkIfOrderInDB(String x_element)
            throws TransformerConfigurationException,
            TransformerFactoryConfigurationError {
        NodeList nl = parseXML(x_element, Order.ORDER_PARENT_TAG);
        StringBuffer tempXml = new StringBuffer();
        Log.i( Utilities.TAG, "Searching for order in xml");
        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            String oid = String.valueOf(getValue(element, Order.ORDER_ID));
            Log.i( Utilities.TAG, "Searching for order id : " + oid);
            HashMap<Long, Object> xml = DBEngine.getOrders(context, oid);
            if (xml == null) {
                Log.i( Utilities.TAG,
                        "Order xml is null" + element.toString());
                String xml2 = Utilities.convertElementToString(element);
                tempXml.append(xml2);
            }
        }
        if (tempXml.length() <= 0) {
            return null;
        } else {
            String finalXml = "<data>" + tempXml + "</data>";
            return finalXml;
        }
    }

    public String searchOrderId(String x_element)
            throws TransformerConfigurationException,
            TransformerFactoryConfigurationError {
        NodeList nl = parseXML(x_element, Order.ORDER_PARENT_TAG);
        StringBuffer tempXml = new StringBuffer();

        for (int i = 0; i < nl.getLength(); i++) {
            Element element = (Element) nl.item(i);
            String temp = getValue(element, Order.ORDER_ID);
            return temp;
        }
        return null;
    }

    public String getOrderDate(String x_element)
            throws TransformerConfigurationException,
            TransformerFactoryConfigurationError {
        try {
            if (x_element != null) {
                NodeList nl = parseXML(x_element, Order.ORDER_PARENT_TAG);
                Element element = (Element) nl.item(0);
                String date = getValue(element, Order.ORDER_START_DATE);
                return date;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object[] getObjectValuesfromXML(String xml, String type) {
        if (type == OrderPart.ACTION_SAVE_ORDER_PART)
            return getOrderPartValuesFromXML(xml);
        if (type == Form.ACTION_SAVE_FORM) {
            Log.d("TAG72", xml);
            return getOrderFormValuesFromXML(xml);
        }
        else if (type == OrderTask.ACTION_SAVE_ORDER_TASK)
            return getOrderTaskValuesFromXML(xml);
        else if (type == OrderMedia.ACTION_MEDIA_SAVE)
            return getOrderMediaValuesFromXML(xml);
        else if (type == Site.ACTION_SAVE_SITE)
            return getSiteValuesFromXML(xml);
        else if (type == CustomerContact.ACTION_SAVE_CONTACT)
            return getContactValuesFromXML(xml);
        else if (type == Assets.ACTION_SAVE_ORDER_ASSETS)
            return getAssetValuesFromXML(xml);
        else if (type == Shifts.ACTION_SYNC_SHIFTS)
            return getShiftValuesFromXML(xml);
        return null;
    }

    public Object getBaseValuesfromXML(String xml){
        NodeList nl = parseXML(xml, XMLHandler.KEY_DATA);
        Object[] BaseListValues = new Object[nl.getLength()];
        String temp = "";
        String oid = "";
        for (int i = 0; i < nl.getLength(); i++) {
            try {
                Element element = (Element) nl.item(i);
                BaseResponse baseResponse = new BaseResponse();

                if (getValue(element, "success").equals("false")) {
                    String errorcode = getValue(element, "errorcode");
                    if (errorcode == null || errorcode.equals(""))
                    {
                        errorcode = ServiceError.getEnumValstr(ServiceError.ERROR_CODE_RESPONSE_ERROR);
                    }
                    baseResponse.setBaseError(errorcode);
                } else {
                    baseResponse.setNsp(getValue(element,
                            BaseResponse.LOGIN_NSP));
                    baseResponse.setUrl(getValue(element,
                            BaseResponse.LOGIN_URL));
                    baseResponse.setSubkey(getValue(element,
                            BaseResponse.LOGIN_SUBKEY));
                }
                BaseListValues[i] = baseResponse;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e( Utilities.TAG, "ERROR IN ORDER XML : " + oid);
            }
        }
        return BaseListValues[0];
    }

    public Object getLoginValuesFromXML(String xml) {  //YD
        NodeList nl = parseXML(xml, XMLHandler.KEY_DATA);
        Object[] LoginListValues = new Object[nl.getLength()];
        String temp = "";
        String oid = "";
        for (int i = 0; i < nl.getLength(); i++) {
            try {
                Element element = (Element) nl.item(i);
                Login login = new Login();

                if (getValue(element, "success").equals("false")) {
                    String errorcode = getValue(element, "errorcode");
                    if (errorcode == null || errorcode.equals("")) {
                        errorcode = ServiceError.getEnumValstr(ServiceError.ERROR_CODE_RESPONSE_ERROR);
                    }
                    login.setLoginError(errorcode);
                } else {
                    login.setRid(Long.parseLong(getValue(element,
                            Login.LOGIN_RES_ID)));

                    login.setResnm(getValue(element,
                            Login.LOGIN_TECH_NAME));
                    login.setGpssync(getValue(element,
                            Login.LOGIN_GPS_SYNC));

                    login.setBizhr(getValue(element,
                            Login.LOGIN_BIZ_HRS));

                    login.setEdn(Integer.parseInt(getValue(element,
                            Login.LOGIN_EDN)));

                    login.setWorkerWeek(getValue(element,
                            Login.LOGIN_WORKERWEEK));

                    login.setSmstmpl0(getValue(element,
                            Login.LOGIN_SMS_TEMP_ZERO));

                    login.setSmstmpl1(getValue(element,
                            Login.LOGIN_SMS_TEMP_ONE));

                    login.setSmstmpl2(getValue(element,
                            Login.LOGIN_SMS_TEMP_TWO));

                    login.setSmstmpl3(getValue(element,
                            Login.LOGIN_SMS_TEMP_THREE));

                    login.setSmstmpl4(getValue(element,
                            Login.LOGIN_SMS_TEMP_FOUR));

                    login.setMapctry(getValue(element,
                            Login.LOGIN_MAP_COUNTRY));

                    login.setMaprgn(getValue(element,
                            Login.LOGIN_MAP_REGION));

                    login.setToken(getValueCData(element,
                            Login.LOGIN_MTOKEN));

                    login.setServiceHead(getValue(element,
                            Login.LOGIN_SERVICE_HEADING));

                    login.setPartHead(getValue(element,
                            Login.LOGIN_PART_HEADING));

                    login.setUiConfig(getValue(element,
                            Login.LOGIN_OLPM_UICONFIG));

                    login.setOfficeGeo(getValue(element,
                            Login.LOGIN_OFFICE_GEO));
                    login.setLocChan(getValue(element,Login.LOCCHAN));

                    login.setshftlock(getValue(element, Login.LOGIN_SHFTLOK));

                    if (getValue(element, Login.LOGIN_NSPID) != null)
                        login.setNspid(Long.parseLong(getValue(element,
                                Login.LOGIN_NSPID)));
                    else
                        login.setNspid(0L);

                    if (getValue(element, Login.LOGIN_SPEED) != null)
                        login.setSpeed(Integer.parseInt(getValue(element,
                                Login.LOGIN_SPEED)));
                    else
                        login.setSpeed(0);


                }
                LoginListValues[i] = login;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e( Utilities.TAG, "ERROR IN ORDER XML : " + oid);
            }
        }
        return LoginListValues[0];
    }

    public Object getErrorValuesFromXML(String xml) {  //YD
        NodeList nl = parseXML(xml, XMLHandler.KEY_DATA);
        Object[] ErrorListValues = new Object[nl.getLength()];
        String temp = "";
        String oid = "";
        for (int i = 0; i < nl.getLength(); i++) {
            try {
                Element element = (Element) nl.item(i);

                ErrorResponse errRes = new ErrorResponse();
                errRes.setSuccess(getValue(element,
                        "success"));

                errRes.setId(getValue(element,
                        "id"));

                errRes.setErrorcode(getValue(element,
                        "errorcode"));


                ErrorListValues[i] = errRes;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e( Utilities.TAG, "ERROR IN ORDER XML : " + oid);
            }
        }
        return ErrorListValues[0];
    }

	/*public HashMap<Long, Object> getOrderStatusValuesFromXML(String xml) {  //YD using when status is kept hardcoded
		NodeList nl = parseXML(xml, OrderStatus.ORDER_PARENT_TAG);
		HashMap<Long, Object> OrderStatusListValues = new HashMap<Long, Object>();
		String temp = "";
		String oid = "";
		for (int i = 0; i < nl.getLength(); i++) {
			try {
				Element element = (Element) nl.item(i);

				OrderStatus odrStat = new OrderStatus();
				odrStat.setId(Long.parseLong(getValueCData(element,
						OrderStatus.ORDER_ID)));

				odrStat.setNm(getValueCData(element,
						OrderStatus.ORDER_NAME));



				OrderStatusListValues.put(Long.parseLong(getValueCData(element,
						OrderStatus.ORDER_ID)),odrStat);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e( Utilities.TAG, "ERROR IN ORDER XML : " + oid);
			}
		}
		return OrderStatusListValues;
	}*/

    public HashMap<Long, Object> getOrderPriorityValuesFromXML(String xml) {  //yash note
        NodeList nl = parseXML(xml, OrderPriority.ORDER_PARENT_TAG);
        HashMap<Long, Object> OrderStatusListValues = new HashMap<Long, Object>();
        String temp = "";
        String oid = "";
        for (int i = 0; i < nl.getLength(); i++) {
            try {
                Element element = (Element) nl.item(i);

                OrderPriority odrpriority = new OrderPriority();
                odrpriority.setId(Long.parseLong(getValueCData(element,
                        odrpriority.ORDER_PRIORITY_ID)));

                odrpriority.setNm(getValueCData(element,
                        odrpriority.ORDER_PRIORITY_NAME));


                OrderStatusListValues.put(Long.parseLong(getValueCData(element,
                        odrpriority.ORDER_PRIORITY_ID)), odrpriority);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e( Utilities.TAG, "ERROR IN ORDER XML : " + oid);
            }
        }
        return OrderStatusListValues;
    }


    public static String createXMLForDeleteSHIFT(Cursor cursor) {
        String tempxml = XML_NO_DATA;
        try {
            if (cursor.moveToFirst()) {
                StringBuffer xml = new StringBuffer(xmlHeader + "<data>");
                do {
                    xml.append("<shf><id>" + cursor.getLong(0) + "</id></shf>");
                }
                while (cursor.moveToNext());

                xml.append("</data>");

                return xml.toString();
            } else
                return tempxml;
        } catch (Exception e) {
            e.printStackTrace();
            return tempxml;
        } finally {
            cursor.close();
        }
    }
}