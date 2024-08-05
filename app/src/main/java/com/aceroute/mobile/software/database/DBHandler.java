package com.aceroute.mobile.software.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Log;

import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderMedia;
import com.aceroute.mobile.software.component.OrderNotes;
import com.aceroute.mobile.software.component.OrderPart;
import com.aceroute.mobile.software.component.OrderTask;
import com.aceroute.mobile.software.component.OrdersMessage;
import com.aceroute.mobile.software.component.reference.Assets;
import com.aceroute.mobile.software.component.reference.AssetsType;
import com.aceroute.mobile.software.component.reference.CheckinOut;
import com.aceroute.mobile.software.component.reference.ClientLocation;
import com.aceroute.mobile.software.component.reference.ClientSite;
import com.aceroute.mobile.software.component.reference.Customer;
import com.aceroute.mobile.software.component.reference.CustomerContact;
import com.aceroute.mobile.software.component.reference.CustomerType;
import com.aceroute.mobile.software.component.reference.Form;
import com.aceroute.mobile.software.component.reference.MessagePanic;
import com.aceroute.mobile.software.component.reference.OrderStatus;
import com.aceroute.mobile.software.component.reference.OrderTypeList;
import com.aceroute.mobile.software.component.reference.Parts;
import com.aceroute.mobile.software.component.reference.ServiceType;
import com.aceroute.mobile.software.component.reference.Shifts;
import com.aceroute.mobile.software.component.reference.Site;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.component.reference.Worker;
import com.aceroute.mobile.software.http.HttpConnection;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.model.MediaCountModel;
import com.aceroute.mobile.software.requests.EditContactReq;
import com.aceroute.mobile.software.requests.EditSiteReq;
import com.aceroute.mobile.software.requests.OrderMessage;
import com.aceroute.mobile.software.requests.PubnubRequest;
import com.aceroute.mobile.software.requests.SaveFormRequest;
import com.aceroute.mobile.software.requests.SaveMediaRequest;
import com.aceroute.mobile.software.requests.SaveNewOrder;
import com.aceroute.mobile.software.requests.SavePartDataRequest;
import com.aceroute.mobile.software.requests.SaveShiftReq;
import com.aceroute.mobile.software.requests.SaveSiteRequest;
import com.aceroute.mobile.software.requests.SaveTaskDataRequest;
import com.aceroute.mobile.software.utilities.JSONHandler;
import com.aceroute.mobile.software.utilities.ObjectHandler;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.RequestObjectHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.aceroute.mobile.software.utilities.XMLHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
//import android.database.CursorWindow;
//import android.database.sqlite.SQLiteCursor;

public class DBHandler extends SQLiteOpenHelper {

    private Context context;
    public static final String dbName = "aceroutedb";
    private final static int DATABASE_VERSION = 1;

    public static final int QUERY_FOR_TEMP = 1;
    public static final int QUERY_FOR_ORIG = 2;
    public static final String tempTablePrefix = "temp_";

    public final static int DB_ACTION_REFRESH = 0;
    public final static int DB_ACTION_INSERT = 1;
    public final static int DB_ACTION_DELETE = 2;
    public final static int DB_ACTION_UPDATE = 3;

    public final static int DB_GET_ONLY_CURRENT_RECORD = 1;
    public final static int DB_GET_ALL_RECORD = 2;
    /**
     * orderlist table
     */

    public static final String orderListTable = "orderlist";

    /**
     * customerlist table
     */
    static final String customerListTable = "customerlist";

    /**
     * ordertypelist table
     */
    static final String orderTypeListTable = "ordertypelist";

    /**
     * customertypelist table
     */
    static final String customerTypeTable = "customertype";

    /**
     * sitetypelist table
     */
    static final String siteTypeTable = "sitetype";
    /*
     * parttypelist table
     */
    static final String partTypeListTable = "parttypelist";

    /**
     * clientsite table
     */
    static final String clientSiteTable = "clientsite";

    /**
     * res table
     */
    public static final String resTable = "res";

    /**
     * tasktype table
     */
//    static final String taskTypeTable = "tasktype";

    /**
     * ordertask table
     */
    public static final String orderTaskTable = "ordertask";

    /**
     * ordertaskOld table
     */
    public static final String orderTaskTableOld = "ordertaskold"; //by mandeep

    /**
     * orderpart table
     */
    public static final String orderPartTable = "orderpart";

    /**
     * orderpart table
     */
    public static final String orderStatusTable = "orderstatus";

    /**
     * orderfilemeta table
     */
    public static final String orderFileMeta = "orderfilemeta";

    /**
     * orderfilenotes table
     */
    public static final String orderFileNotes = "orderfilenotes";

    /**
     * clientsite table
     */
    public static final String siteTable = "site";
    public static final String formTable = "form";

    /**
     * clientAssests table
     */
    public static final String assestTable = "custassests";


    /**
     * clientAssestsType table
     */
    public static final String assestTypeTable = "custasseststype";

    /**
     * resgeo table
     */
    public static final String resgeoTable = "resgeolist";

    /**
     * resgeo table
     */
    public static final String statusupdatetable = "statusupdatetable";
    /**
     * resgeo table
     */
    public static final String customercontactTable = "ccontacttable";

    /**
     * orderMessage table
     */
    public static final String orderMessageTable = "ordermessage";

    /**
     * optionMessage table
     */
    public static final String MessageTable = "message";

    /**
     * clientSite table
     */
    public static final String companyClientSiteTable = "companyclientsite";
    public static final String shiftTable = "shifthour";

    public static final String checkincheckoutTable = "cinouttable";
    public static final String modifiedField = "modified";
    public static final String localidField = "localid";// YD using as local id of particulars
    public static final String localOidField = "localoid"; //YD using as local orderId for particular for eg task , part but not for order

    public static final String modifiedNew = "new";

    public static final String deleted = "deleted";
    public static final String multipledeleted = "multipedeleted";
    public static final String updated = "update";
    public static final String uupdated = "uupdate";
    // YD using only to update site and customer contact


    /**
     * Table Counter and max tables
     */
    static final int totalTablesToCreate = (int) 12;
    static int tableCounts = (int) 0;

    static final int recordsNotInserted = (int) 0;
    static final int recordsCannotInserted_NoData = (int) -2;

    static final int recordsInsertedSuccessfully = (int) 1;

    public static SQLiteDatabase db = null;
    private static DBHandler database = null;
    static XMLHandler xmlHandler;

    protected DBHandler(Context context) {
        super(context, dbName, null, DATABASE_VERSION);
        try {
            Utilities.log(context, "Inside DatabaseHelper constructor.");
            this.context = context;
            if (db != null) {
                db.close();
                db = null;
            }

//need comment

        /*if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
              String dataBasePath = Environment
                        .getExternalStorageDirectory()
                        .getAbsolutePath()
                        + "/temDB.db";

                  db = SQLiteDatabase.openOrCreateDatabase(new File(dataBasePath).getAbsolutePath(), null);
                  onCreate(db);

            }*/
//need comment

        db = getWritableDatabase();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DBHandler getDataBase(Context context) {
        if (database != null && db != null) {
            Log.i( Utilities.TAG, "returning old database object");
            return database;
        } else {
            Log.w(Utilities.TAG, "returning new database object");
            database = new DBHandler(context);
            return database;
        }
    }

    public static boolean isDBAvailable(Context context) {
        //DBHandler.getDataBase(context);
        DBHandler dbHandler = new DBHandler(context);
        String tables[] = getAllTables();
        if (db == null) {
            return false;
        }
        if (tables.length < 12) {
            return false;
        }
        return true;
    }

    public static String[] getAllTables() {
        Log.i(Utilities.TAG, "Database : " + db);
        Cursor c = db.rawQuery(
                "SELECT * FROM sqlite_master WHERE type='table'", null);
        String[] tables = new String[c.getCount() + 1];
        int i = 0;
        Log.i(Utilities.TAG, "cursor size : " + c.getCount());
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                tables[i] = (String) c.getString(1);
                i++;
                c.moveToNext();
            }
        }
        c.close();
        return tables;
    }

    public static boolean deleteTable(String table) {
        if (db != null) {
            Log.i(Utilities.TAG, "Deleting table " + table);
            if (table != null && !table.equals("table")) {
                int delResult = db.delete(table, null, null);
                return delResult > 0 ? true : false;
            }
        }
        return false;
    }

    public static boolean deleteDatabase(Context context) {
        return context.deleteDatabase(dbName);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
      //  Log.d("niki","onCreate()");

        Utilities.log(context, "Inside oncreate sqlitedatabase.");
        createAcerouteTables(db);
        createAcerouteTempTables(db);
    }


    private void createAcerouteTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + orderListTable + "( " + Order.ORDER_ID
                + " INTEGER PRIMARY KEY, " + Order.ORDER_START_DATE
                + " DATETIME, " + Order.ORDER_END_DATE + " DATETIME, "
                + Order.ORDER_NAME + " TEXT, " + Order.ORDER_STATUS_ID
                + " INTEGER,"
                + Order.ORDER_STATUS_PO_NUMBER + " TEXT, "
                + Order.ORDER_STATUS_INVOICE_NUMBER + " TEXT, "
                + Order.ORDER_STATUS_ORDER_TYPE_ID + " INTEGER, "
                + Order.ORDER_STATUS_PRIORITY_TYPE_ID + " INTEGER, "
                + Order.ORDER_STATUS_PRIMARY_WORKER_ID + " TEXT, "
                /* + Order.ORDER_STATUS_LIST_ADD_WORKER_PIPED + " INTEGER, " */
                + Order.ORDER_STATUS_SUMMARY + " TEXT, " + Order.ORDER_NOTES
                + " TEXT, " + Order.ORDER_CUSTOMER_ID + " INTEGER, "
                + Order.ORDER_CUSTOMER_NAME + " TEXT, "
                + Order.ORDER_CUSTOMER_SITE_STREET_ADDRESS + " TEXT, "
                + Order.ORDER_CUSTOMER_SITE_SUITE_ADDRESS + " TEXT, "
                + Order.ORDER_CUSTOMER_SITE_GEOCODE + " TEXT, "
                + Order.ORDER_CUSTOMER_CONTACT_NAME + " TEXT,"
                + Order.ORDER_CUSTOMER_CONTACT_NUMBER + " TEXT, "
                + Order.ORDER_CUSTOMER_PART_COUNT + " INTEGER, "
                + Order.ORDER_CUSTOMER_MEDIA_COUNT + " INTEGER, "
                + Order.ORDER_CUSTOMER_SERVICE_COUNT + " INTEGER,"
                + Order.ORDER_EPOCH_TIME + " INTEGER ," + modifiedField + " TEXT,"
                + Order.ORDER_TYPE + " TEXT,"
                + Order.ORDER_START_TIME + " INTEGER,"
                + Order.ORDER_CUSTOMER_SITE_ID + " TEXT,"
                + Order.ORDER_END_TIME + " INTEGER,"
                + Order.ORDER_EST + " TEXT,"
                + Order.ORDER_LST + " TEXT,"
                + Order.ORDER_FLG + " TEXT,"
                + Order.ORDER_CUSTOMER_TYPE_NAME + " TEXT,"
                + Order.ORDER_SITE_TYPE_NAME + " TEXT,"
                + Order.ORDER_SORTINDEX + " INTEGER,"
                + Order.ORDER_SIG + " INTEGER,"
                + Order.ORDER_AUDIO + " INTEGER,"
                + Order.ORDER_IMG + " INTEGER,"
                + Order.ORDER_NOTE + " INTEGER,"
                + Order.ORDER_ALERT + " TEXT,"
                + localidField + " INTEGER,"
                + Order.ORDER_CUSTOMER_CONTACT_ID + " INTEGER,"
                + Order.ORDER_TEL_TYPEID + " INTEGER,"
                + Order.ORDER_DOCS + " INTEGER,"
                + Order.ORDER_ASSET_COUNT + " INTEGER,"
                + Order.ORDER_CUSTOMER_FORM_COUNT + " INTEGER,"
                + Order.ORDER_CUSTOMER_CTID + " INTEGER)");

        //Here I created modified coloum


        PreferenceHandler.setTableCounter(tableCounts++);


        db.execSQL("CREATE TABLE " + customerListTable + "( "
                + Customer.CUSTOMER_ID + " INTEGER PRIMARY KEY, "
                + Customer.CUSTOMER_TYPE + " INTEGER, "
                + Customer.CUSTOMER_TYPE_STR + " TEXT, "
                + Customer.CUSTOMER_NAME + " TEXT )");
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + orderTypeListTable + "( "
                + OrderTypeList.ORDER_TYPE_ID + " INTEGER PRIMARY KEY, "
                + OrderTypeList.ORDER_TYPE_NAME + " TEXT )");
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + customerTypeTable + "( "
                + CustomerType.CUSTOMER_TYPE_ID + " INTEGER PRIMARY KEY, "
                + CustomerType.CUSTOMER_TYPE_NAME + " TEXT )");
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + siteTypeTable + "( "
                + SiteType.SITE_TYPE_ID + " INTEGER PRIMARY KEY, "
                + SiteType.SITE_TYPE_NAME + " TEXT,"
                + SiteType.SITE_TYPE_TID + " INTEGER,"
                + SiteType.SITE_TYPE_UPD + " INTEGER ,"
                + SiteType.SITE_TYPE_dtl + " TEXT,"
                + SiteType.SITE_TYPE_CAP + " INTEGER)");
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + partTypeListTable + "( "
                + Parts.PART_TYPE_ID + " INTEGER PRIMARY KEY, "
                + Parts.PART_TYPE_NAME + " TEXT ,"
                + Parts.PART_TYPE_DESC + " TEXT ,"
                + Parts.PART_TYPE_CTID + " INTEGER)");
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + clientSiteTable + "( "
                + ClientLocation.CLIENT_ID + " INTEGER PRIMARY KEY, "
                + ClientLocation.Geo.CLIENT_LAT + " TEXT ,"
                + ClientLocation.Geo.CLIENT_LONG + " TEXT)");
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + resTable
                + "( " + Worker.WORKER_ID
                + " INTEGER PRIMARY KEY, "
                + Worker.WORKER_NAME + " TEXT, "
                + Worker.WORKER_LID + " INTEGER, "
                + Worker.WORKER_WEEK + " TEXT, "
                + Worker.WORKER_DWEEK + " TEXT,"
                + Worker.WORKER_BRK + " TEXT,"
                //Created modified coloum for worker
                + modifiedField + " TEXT,"
                + Worker.WORKER_SHIFT + " INTEGER,"
                + Worker.WORKER_TID + " INTEGER, "
                + Worker.WORKER_VHLID + " TEXT)");

        PreferenceHandler.setTableCounter(tableCounts++);

       /* db.execSQL("CREATE TABLE " + taskTypeTable + "( "
                + ServiceType.SERVICE_TYPE_ID + " INTEGER PRIMARY KEY, "
                + ServiceType.SERVICE_TYPE_NAME + " TEXT ,"
                + ServiceType.SERVICE_TYPE_DTL + " TEXT)");
        PreferenceHandler.setTableCounter(tableCounts++);*/
        /*db.execSQL("CREATE TABLE " + formTable
                + "( " + Form.FORM_OID + " TEXT , "
                + Form.FORM_ID + " TEXT, "
                + Form.FORM_TID + " TEXT, "
                + Form.FORM_FDATA + " TEXT, "
                + Form.FORM_UPD + " TEXT,"
                //Created modified coloum for worker
                + modifiedField + " TEXT,"
                + localidField + " INTEGER,"
                + localOidField + " INTEGER)");*/

        db.execSQL("CREATE TABLE " + formTable + "( " + Form.FORM_OID
                + " INTEGER , " + Form.FORM_ID
                + " INTEGER PRIMARY KEY, " + Form.FORM_TID + " INTEGER , "
                + Form.FORM_FDATA + " TEXT, "
                + Form.FORM_UPD + " INTEGER,"
                + modifiedField + " TEXT,"
                + localidField + " INTEGER,"
                + localOidField + " INTEGER,"
                + Form.FORM_KEY_ONLY + " TEXT)");

        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + orderTaskTable + "( " + OrderTask.ORDER_ID
                + " INTEGER , " + OrderTask.ORDER_TASK_ID + " INTEGER PRIMARY KEY, "
                + OrderTask.TASK_ID + " INTEGER , "
                + OrderTask.ORDER_SERV_UPDATE_TIME + " INTEGER,"
                + modifiedField + " TEXT,"
                + OrderTask.TRIM_TYPE + " TEXT,"
                + OrderTask.PRIORITY + " TEXT,"
                + OrderTask.ACTION_STATUS + " TEXT,"
                + OrderTask.TREE_OWNER + " TEXT,"
                + OrderTask.TREE_HT + " TEXT,"
                + OrderTask.TREE_DIA + " TEXT,"
                + OrderTask.TREE_CLEARENCE + " TEXT,"
                + OrderTask.TREE_CYCLE + " TEXT,"
                + OrderTask.TREE_EXPCOUNT + " TEXT,"
                + OrderTask.TREE_ACTUALCOUNT + " TEXT,"
                + OrderTask.TREE_TIMESPENT + " TEXT,"
                + OrderTask.TREE_TM + " TEXT,"
                + OrderTask.TREE_MSC + " TEXT,"
                + OrderTask.TREE_COMMENT + " TEXT,"
                + OrderTask.TREE_PCOMMENT + " TEXT,"
                + OrderTask.TREE_ALERT + " TEXT,"
                + OrderTask.TREE_NOTE + " TEXT,"
                + OrderTask.TREE_GEO + " TEXT,"
                + OrderTask.TREE_ACCESSPATH + " TEXT,"
                + OrderTask.TREE_CT1 + " TEXT,"
                + OrderTask.TREE_CT2 + " TEXT,"
                + OrderTask.TREE_CT3 + " TEXT,"
                + localidField + " INTEGER,"
                + localOidField + " INTEGER)");

// Created modifed coloum in
        PreferenceHandler.setTableCounter(tableCounts++);
// By Mandeep for OrderTaskOld
       /* db.execSQL("CREATE TABLE " + orderTaskTableOld + "( " + OrderTaskOld.ORDER_ID
                + " INTEGER , " + OrderTaskOld.ORDER_TASK_ID
                + " INTEGER PRIMARY KEY, " + OrderTaskOld.ORDER_TASK_TYPE_ID + " INTEGER , "
                + OrderTaskOld.ORDER_TASK_RATE + " TEXT, "
                + OrderTaskOld.ORDER_SERV_UPDATE_TIME + " INTEGER," + modifiedField + " TEXT)");
        //Created coloum for modified part
        PreferenceHandler.setTableCounter(tableCounts++);*/


        db.execSQL("CREATE TABLE " + orderPartTable + "( " + OrderPart.ORDER_ID
                + " INTEGER , " + OrderPart.ORDER_PART_ID
                + " INTEGER PRIMARY KEY, " + OrderPart.PART_TID + " INTEGER , "
                + OrderPart.ORDER_PART_QTY + " TEXT, "
                + OrderPart.ORDER_BARCODE + " TEXT, "
                + OrderPart.ORDER_UPDATE_TIME + " INTEGER," + modifiedField + " TEXT,"
                + localidField + " INTEGER,"
                + localOidField + " INTEGER)");
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + orderFileMeta + "( " + OrderMedia.ID
                + " INTEGER PRIMARY KEY, " + OrderMedia.ORDER_ID + " INTEGER, "
                + OrderMedia.ORDER_MEDIA_TYPE + " INTEGER , "
                + OrderMedia.ORDER_GEO + " TEXT , "
                + OrderMedia.ORDER_FILE_DESC + " TEXT , "
                + OrderMedia.ORDER_FILE_MIME_TYPE + " TEXT , "
                + OrderMedia.ORDER_FILE_DATA + " BLOB , "
                + OrderMedia.ORDER_META_UPDATE_TIME + " INTEGER,"
                + modifiedField + " TEXT,"
                + OrderMedia.ORDER_META_PATH + " TEXT,"
                + localidField + " INTEGER,"
                + localOidField + " INTEGER,"
                + OrderMedia.ORDER_FILE_NM + " TEXT,"
                + OrderMedia.FORM_KEY + " TEXT,"
                + OrderMedia.FORM_FIELD_ID + " INTEGER)");
        //Created modify coloum in order file meta

        PreferenceHandler.setTableCounter(tableCounts++);


        db.execSQL("CREATE TABLE " + orderStatusTable + "( "
                + OrderStatus.STATUS_ID + " INTEGER PRIMARY KEY, "
                + OrderStatus.STATUS_TYPE_ISGRP + " INTEGER, "
                + OrderStatus.STATUS_TYPE_GRPSEQ + " INTEGER , "
                + OrderStatus.STATUS_TYPE_GRPID + " INTEGER , "
                + OrderStatus.STATUS_TYPE_SEQ + " INTEGER,"
                + OrderStatus.STATUS_TYPE_NM + " TEXT , "
                + OrderStatus.STATUS_TYPE_ABR + " TEXT , "
                + OrderStatus.STATUS_TYPE_ISVIS + " INTEGER,"
                + modifiedField + " TEXT)");
        //Creared modified coloum for status
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + orderMessageTable + "( "
                + OrdersMessage.MESSAGE_TIMESTAMP + " INTEGER PRIMARY KEY, "
                + OrdersMessage.MESSAGE_TYPE_ID + " TEXT, "
                + OrdersMessage.MESSAGE_ORDER_ID + " TEXT , "
                + OrdersMessage.MESSAGE_CUST_ID + " TEXT , "
                + OrdersMessage.MESSAGE_CONTACT_ID + " TEXT, "
                + OrdersMessage.MESSAGE_TELL + " TEXT , "
                + OrdersMessage.MESSAGE_TIMEZONE + " TEXT , "
                + OrdersMessage.MESSAGE_GEO + " TEXT,"
                + modifiedField + " TEXT)");
        //Creared modified coloum for status
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + MessageTable + "( "
                + MessagePanic.MESSAGE_TIMESTAMP + " INTEGER PRIMARY KEY, "
                + MessagePanic.MESSAGE_TYPE_ID + " TEXT, "
                + MessagePanic.MESSAGE_ORDER_ID + " TEXT , "
                + MessagePanic.MESSAGE_GEO + " TEXT,"
                + MessagePanic.MESSAGE_MESSAGE + " TEXT,"
                + modifiedField + " TEXT)");
        //Creared modified coloum for status
        PreferenceHandler.setTableCounter(tableCounts++);


        db.execSQL("CREATE TABLE " + orderFileNotes + "( "
                + OrderNotes.ORDER_ID + " INTEGER, " + OrderNotes.ORDER_NOTE
                + " TEXT," + modifiedField + " TEXT)");
        //created for modified coloum for notes
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + siteTable + "( " + Site.SITE_ID
                + " INTEGER PRIMARY KEY, " + Site.SITE_CID + " INTEGER ,"
                + Site.SITE_NAME + " TEXT ," + Site.SITE_ADDRESS + " TEXT ," + Site.SITE_ADR2 + " TEXT ,"
                + Site.SITE_ST + " TEXT ,"
                + Site.SITE_GEO + " TEXT ,"
                + DBHandler.modifiedField + " TEXT ,"
                + Site.SITE_UPD + " INTEGER ,"
                + Site.SITE_DTL + " TEXT ,"
                + Site.SITE_TYPE_ID + " INTEGER,"
                + Site.SITE_TYPE_NM + " TEXT,"
                + localidField + " INTEGER)");
        PreferenceHandler.setTableCounter(tableCounts++);

        String que = "CREATE TABLE " + assestTable + "( " + Assets.ASSET_ID
                + " INTEGER PRIMARY KEY, " + Assets.ASSET_CID + " INTEGER ,"
                + Assets.ASSET_GEO + " TEXT ,"
                + DBHandler.modifiedField + " TEXT ,"
                + Assets.ASSET_UPD + " INTEGER ,"
                + Assets.ASSET_FTYPE_ID + " INTEGER,"
                + Assets.ASSET_FDATA + " TEXT,"
                + localidField + " INTEGER ,"
                + Assets.ASSET_OID + " INTEGER)";

        db.execSQL(que);
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + assestTypeTable + "( " + AssetsType.ASSETS_TYPE_ID
                + " INTEGER PRIMARY KEY, " + AssetsType.ASSETS_TYPE_XID + " INTEGER ,"
                + AssetsType.ASSETS_TYPE_NAME + " TEXT ,"
                + AssetsType.ASSETS_TYPE_UPD + " INTEGER)");
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + resgeoTable + "( "
                + "geo" + " TEXT, " + "timestamp" + " TEXT, " + "lstoid" + " TEXT, " + "nxtoid"
                + " TEXT)");
        //created for modified coloum for notes
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + statusupdatetable + "( "
                + "id" + " TEXT, " + "wkf"
                + " TEXT)");
        //created for modified coloum for notes
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + customercontactTable + "( " + CustomerContact.CONTACT_ID
                + " INTEGER, " + CustomerContact.CUSTOMER_ID + " INTEGER ,"
                + CustomerContact.CONTACT_TEL + " TEXT ,"
                + CustomerContact.CONTACT_NAME + " TEXT,"
                + CustomerContact.CONTACT_TYPE + " INTEGER,"
                + " TEXT," + modifiedField + " TEXT,"
                + localidField + " INTEGER,"
                + CustomerContact.CONTACT_EML + " TEXT)");
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + checkincheckoutTable + "( " + CheckinOut.CC_TSTAMP
                + " INTEGER PRIMARY KEY, " + DBHandler.modifiedField + " TEXT ,"
                + CheckinOut.CC_GEO + " TEXT ,"
                + CheckinOut.CC_TID + " TEXT ,"
                + CheckinOut.CC_LSTOID + " TEXT ,"
                + CheckinOut.CC_NXTOID + " TEXT )");
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + companyClientSiteTable + "( " + ClientSite.CLIENTSITE_ID
                + " INTEGER PRIMARY KEY, " + ClientSite.CLIENTSITE_NAME + " TEXT ,"
                + ClientSite.CLIENTSITE_ADR + " TEXT ,"
                + ClientSite.CLIENTSITE_ADR2 + " TEXT ,"
                + ClientSite.CLIENTSITE_TZ + " TEXT ,"
                + ClientSite.CLIENTSITE_GEO + " TEXT ,"
                + ClientSite.CLIENTSITE_XID + " INTEGER ,"
                + ClientSite.CLIENTSITE_UPD + " INTEGER ,"
                + ClientSite.CLIENTSITE_BY + " TEXT )");
//                + ClientSite.CLIENTSITE_UPD + " INTEGER ,"
//                + ClientSite.CLIENTSITE_BY + " TEXT )");
        PreferenceHandler.setTableCounter(tableCounts++);//
        // TODO KUBER CREATE TABLE PARAM FOR CLIENT SITE


        db.execSQL("CREATE TABLE " + shiftTable + "( " + Shifts.SHIFT_ID
                + " INTEGER, "
                + Shifts.SHIFT_TID + " INTEGER ,"
                + Shifts.SHIFT_NM + " TEXT ,"
                + Shifts.SHIFT_TMSLOT + " TEXT ,"
                + Shifts.SHIFT_DT + " TEXT ,"
                + Shifts.SHIFT_LID + " INTEGER ,"
                + DBHandler.modifiedField + " TEXT ,"
                + localidField + " INTEGER ,"
                + Shifts.SHIFT_BRKSLOT + " TEXT ,"
                + Shifts.SHIFT_ADR + " TEXT ,"
                + Shifts.SHIFT_TERRI + " TEXT )");

        PreferenceHandler.setTableCounter(tableCounts++);//KB for shift table

    }

    private void createAcerouteTempTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + tempTablePrefix + orderListTable + "( " + Order.ORDER_ID
                + " INTEGER PRIMARY KEY, " + Order.ORDER_START_DATE
                + " DATETIME, " + Order.ORDER_END_DATE + " DATETIME, "
                + Order.ORDER_NAME + " TEXT, "
                + Order.ORDER_STATUS_ID + " INTEGER,"
                + Order.ORDER_STATUS_PO_NUMBER + " TEXT, "
                + Order.ORDER_STATUS_INVOICE_NUMBER + " INTEGER, "
                + Order.ORDER_STATUS_ORDER_TYPE_ID + " INTEGER, "
                + Order.ORDER_STATUS_PRIORITY_TYPE_ID + " INTEGER, "
                + Order.ORDER_STATUS_PRIMARY_WORKER_ID + " TEXT, "
                /* + Order.ORDER_STATUS_LIST_ADD_WORKER_PIPED + " INTEGER, " */
                + Order.ORDER_STATUS_SUMMARY + " TEXT, "
                + Order.ORDER_NOTES + " TEXT, "
                + Order.ORDER_CUSTOMER_ID + " INTEGER, "
                + Order.ORDER_CUSTOMER_NAME + " TEXT, "
                + Order.ORDER_CUSTOMER_SITE_STREET_ADDRESS + " TEXT, "
                + Order.ORDER_CUSTOMER_SITE_SUITE_ADDRESS + " TEXT, "
                + Order.ORDER_CUSTOMER_SITE_GEOCODE + " TEXT, "
                + Order.ORDER_CUSTOMER_CONTACT_NAME + " TEXT,"
                + Order.ORDER_CUSTOMER_CONTACT_NUMBER + " TEXT, "
                + Order.ORDER_CUSTOMER_PART_COUNT + " INTEGER, "
                + Order.ORDER_CUSTOMER_MEDIA_COUNT + " INTEGER, "
                + Order.ORDER_CUSTOMER_SERVICE_COUNT + " INTEGER,"
                + Order.ORDER_EPOCH_TIME + " INTEGER,"
                + Order.ORDER_ALERT + " TEXT )");
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + tempTablePrefix + customerListTable + "( "
                + Customer.CUSTOMER_ID + " INTEGER PRIMARY KEY, "
                + Customer.CUSTOMER_NAME + " TEXT )");
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + tempTablePrefix + orderTypeListTable + "( "
                + OrderTypeList.ORDER_TYPE_ID + " INTEGER PRIMARY KEY, "
                + OrderTypeList.ORDER_TYPE_NAME + " TEXT )");
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + tempTablePrefix + customerTypeTable + "( "
                + CustomerType.CUSTOMER_TYPE_ID + " INTEGER PRIMARY KEY, "
                + CustomerType.CUSTOMER_TYPE_NAME + " TEXT )");
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + tempTablePrefix + partTypeListTable + "( "
                + Parts.PART_TYPE_ID + " INTEGER PRIMARY KEY, "
                + Parts.PART_TYPE_NAME + " TEXT,"
                + Parts.PART_TYPE_CTID + " INTEGER )");
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + tempTablePrefix + clientSiteTable + "( "
                + ClientLocation.CLIENT_ID + " INTEGER PRIMARY KEY, "
                + ClientLocation.Geo.CLIENT_LAT + " TEXT ,"
                + ClientLocation.Geo.CLIENT_LONG + " TEXT)");
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + tempTablePrefix + resTable
                + "( " + Worker.WORKER_ID
                + " INTEGER PRIMARY KEY, "
                + Worker.WORKER_NAME + " TEXT, "
                + Worker.WORKER_LID + " INTEGER, "
                + Worker.WORKER_WEEK + " TEXT, "
                + Worker.WORKER_DWEEK + " TEXT,"
                + Worker.WORKER_BRK + " TEXT,"
                + Worker.WORKER_VHLID + " INTEGER)");
        PreferenceHandler.setTableCounter(tableCounts++);

       /* db.execSQL("CREATE TABLE " + tempTablePrefix + taskTypeTable + "( "
                + ServiceType.SERVICE_TYPE_ID + " INTEGER PRIMARY KEY, "
                + ServiceType.SERVICE_TYPE_NAME + " TEXT )");
        PreferenceHandler.setTableCounter(tableCounts++);*/

        db.execSQL("CREATE TABLE " + tempTablePrefix + orderTaskTable + "( " + OrderTask.ORDER_ID
                + " INTEGER , " + OrderTask.ORDER_TASK_ID
                + " INTEGER PRIMARY KEY, " + OrderTask.TASK_ID + " INTEGER , "
                + OrderTask.ORDER_SERV_UPDATE_TIME + " INTEGER )");
        PreferenceHandler.setTableCounter(tableCounts++);

       /* // by Mandeep
        db.execSQL("CREATE TABLE " + tempTablePrefix + orderTaskTableOld + "( " + OrderTaskOld.ORDER_ID
                + " INTEGER , " + OrderTaskOld.ORDER_TASK_ID
                + " INTEGER PRIMARY KEY, " + OrderTaskOld.ORDER_TASK_TYPE_ID + " INTEGER , "
                + OrderTaskOld.ORDER_TASK_RATE + " TEXT, "
                + OrderTaskOld.ORDER_SERV_UPDATE_TIME + " INTEGER )");
        PreferenceHandler.setTableCounter(tableCounts++);*/


        db.execSQL("CREATE TABLE " + tempTablePrefix + orderPartTable + "( " + OrderPart.ORDER_ID
                + " INTEGER , " + OrderPart.ORDER_PART_ID
                + " INTEGER PRIMARY KEY, " + OrderPart.PART_TID + " INTEGER , "
                + OrderPart.ORDER_PART_QTY + " TEXT, "
                + OrderPart.ORDER_UPDATE_TIME + " INTEGER )");
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + tempTablePrefix + orderFileMeta + "( " + OrderMedia.ID
                + " INTEGER PRIMARY KEY, " + OrderMedia.ORDER_ID + " INTEGER, "
                + OrderMedia.ORDER_MEDIA_TYPE + " INTEGER , "
                + OrderMedia.ORDER_GEO + " TEXT , "
                + OrderMedia.ORDER_FILE_DESC + " TEXT , "
                + OrderMedia.ORDER_FILE_MIME_TYPE + " TEXT , "
                + OrderMedia.ORDER_FILE_DATA + " BLOB , "
                + OrderMedia.ORDER_META_UPDATE_TIME + " INTEGER )");

        PreferenceHandler.setTableCounter(tableCounts++);

		/*db.execSQL("CREATE TABLE " + tempTablePrefix+orderStatusTable + "( "
				+ OrderStatus.ORDER_ID + " PRIMARY KEY, "
				+ OrderStatus.ORDER_STATUS_NAME + " TEXT, "
				+ OrderStatus.ORDER_STATUS_VALUE + " TEXT , "
				+ OrderStatus.ORDER_STATUS_DTLIST + " TEXT , "
				+ OrderStatus.ORDER_STATUS_GEO + " TEXT  )");
		PreferenceHandler.setTableCounter(tableCounts++);*/

        db.execSQL("CREATE TABLE " + tempTablePrefix + orderFileNotes + "( "
                + OrderNotes.ORDER_ID + " INTEGER, " + OrderNotes.ORDER_NOTE
                + " TEXT )");
        PreferenceHandler.setTableCounter(tableCounts++);

        db.execSQL("CREATE TABLE " + tempTablePrefix + siteTable + "( " + Site.SITE_ID
                + " INTEGER PRIMARY KEY, " + Site.SITE_CID + " INTEGER ,"
                + Site.SITE_NAME + " TEXT ," + Site.SITE_ADDRESS + " TEXT ,"
                + Site.SITE_UPD + " INTEGER,"+ Site.SITE_DTL
                + " TEXT )");
        PreferenceHandler.setTableCounter(tableCounts++);

    }

    public static int insertRecord(Context context, String table,
                                   Object objectTypeList[]) {
        return insertRecord(context, table, objectTypeList, QUERY_FOR_ORIG);
    }

    public static int insertRecord(Context context, String table,
                                   Object objectTypeList[], int queryType) { //YD inserting here order,etc
        //getDataBase(context);
        Utilities.log(context, "DBHandler Inside insertRecords.");
        ContentValues cv = null;
        try {
            String column = "";
            int totalRecordsInserted = 0;
            if (objectTypeList.length == 0)
                return recordsCannotInserted_NoData;

            ArrayList<Long> shiftlist = null;
            ArrayList<Long> formlist=null;
            if (objectTypeList[0] instanceof Shifts) {
                shiftlist = getshiftList(context);
            }else if(objectTypeList[0] instanceof Form)
                formlist=getformlist(context);

            for (int i = 0; i < objectTypeList.length; i++) {
                if (cv == null)
                    cv = new ContentValues();

                if (objectTypeList[i] instanceof OrderTypeList) {
                    OrderTypeList oType = (OrderTypeList) objectTypeList[i];
                    if (oType == null) {
                        continue;
                    }
                    cv.put(OrderTypeList.ORDER_TYPE_ID, oType.getId());
                    cv.put(OrderTypeList.ORDER_TYPE_NAME, oType.getNm());
                    column = OrderTypeList.ORDER_TYPE_ID;

                } else if (objectTypeList[i] instanceof ClientLocation) {
                    ClientLocation cLoc = (ClientLocation) objectTypeList[i];
                    if (cLoc == null) {
                        continue;
                    }
                    cv.put(ClientLocation.CLIENT_ID, cLoc.getId());
                    cv.put(ClientLocation.Geo.CLIENT_LAT, cLoc.getGeo()
                            .getLatitude());
                    cv.put(ClientLocation.Geo.CLIENT_LONG, cLoc.getGeo()
                            .getLongitude());
                    column = ClientLocation.CLIENT_ID;

                } else if (objectTypeList[i] instanceof MessagePanic) {
                    MessagePanic cLoc = (MessagePanic) objectTypeList[i];
                    if (cLoc == null) {
                        continue;
                    }

                    cv.put(MessagePanic.MESSAGE_TYPE_ID, cLoc.getTid());
                    cv.put(MessagePanic.MESSAGE_ORDER_ID, cLoc.getOid());
                    cv.put(MessagePanic.MESSAGE_GEO, cLoc.getGeo());
                    cv.put(MessagePanic.MESSAGE_TIMESTAMP, cLoc.getStmp());
                    cv.put(MessagePanic.MESSAGE_MESSAGE, cLoc.getMessage());
                    column = MessagePanic.MESSAGE_TIMESTAMP;

                } else if (objectTypeList[i] instanceof OrderTypeList) {

                    OrderTypeList orderType = (OrderTypeList) objectTypeList[i];
                    cv.put(OrderTypeList.ORDER_TYPE_ID, orderType.getId());
                    cv.put(OrderTypeList.ORDER_TYPE_NAME, orderType.getNm());
                    column = OrderTypeList.ORDER_TYPE_ID;

                } else if (objectTypeList[i] instanceof Parts) {

                    Parts partType = (Parts) objectTypeList[i];
                    if (partType == null) {
                        continue;
                    }
                    cv.put(Parts.PART_TYPE_ID, partType.getId());
                    cv.put(Parts.PART_TYPE_NAME, partType.getName());
                    cv.put(Parts.PART_TYPE_DESC, partType.getDesc());
                    cv.put(Parts.PART_TYPE_CTID, partType.getCtid());
                    column = Parts.PART_TYPE_NAME;

                } else if (objectTypeList[i] instanceof ServiceType) {

                    ServiceType serviceType = (ServiceType) objectTypeList[i];
                    if (serviceType == null) {
                        continue;
                    }
                    cv.put(ServiceType.SERVICE_TYPE_ID, serviceType.getId());
                    cv.put(ServiceType.SERVICE_TYPE_NAME, serviceType.getNm());
                    cv.put(ServiceType.SERVICE_TYPE_DTL, serviceType.getDtl());

                    column = ServiceType.SERVICE_TYPE_NAME;

                } else if (objectTypeList[i] instanceof Worker) {
                    Worker worker = (Worker) objectTypeList[i];
                    if (worker == null) {
                        continue;
                    }
                    Log.i( Utilities.TAG, "inserting : " + worker.getBrk() + ":" + worker.getDwrkwk() + ":" + worker.getWrkwk());
                    cv.put(Worker.WORKER_ID, worker.getId());
                    cv.put(Worker.WORKER_NAME, worker.getNm());
                    cv.put(Worker.WORKER_LID, worker.getLid());
                    cv.put(Worker.WORKER_WEEK, worker.getWrkwk());
                    cv.put(Worker.WORKER_DWEEK, worker.getDwrkwk());
                    cv.put(Worker.WORKER_BRK, worker.getBrk());
                    cv.put(Worker.WORKER_SHIFT, worker.getWorkerShift());
                    cv.put(Worker.WORKER_TID, worker.getTid());
                    cv.put(Worker.WORKER_VHLID, worker.getVehicleId());
                    column = Worker.WORKER_NAME;

                } else if (objectTypeList[i] instanceof CustomerType) {
                    CustomerType customerType = (CustomerType) objectTypeList[i];
                    if (customerType == null) {
                        continue;
                    }
                    cv.put(CustomerType.CUSTOMER_TYPE_ID, customerType.getId());
                    cv.put(CustomerType.CUSTOMER_TYPE_NAME,
                            customerType.getNm());
                    column = CustomerType.CUSTOMER_TYPE_ID;
                } else if (objectTypeList[i] instanceof Customer) {
                    Customer customer = (Customer) objectTypeList[i];
                    cv.put(Customer.CUSTOMER_ID, customer.getId());
                    cv.put(Customer.CUSTOMER_NAME, customer.getNm());
                    cv.put(Customer.CUSTOMER_TYPE, customer.getTid());
                    cv.put(Customer.CUSTOMER_TYPE_STR, customer.getCustomerType());

                    column = Customer.CUSTOMER_NAME;

                } else if (objectTypeList[i] instanceof Order) {
                    Order order = (Order) objectTypeList[i];
                    if (order == null) {
                        continue;
                    }

                    String startDate = null;
                    boolean isInBoundaryDate = true;
                    queryType = DBHandler.QUERY_FOR_ORIG;


                    cv = (Long) order.getId() != -1 ? putValueInCV(
                            Order.ORDER_ID, order.getId(), cv) : cv;

                    cv = (Long) order.getLocalId() != -1 ? putValueInCV(
                            localidField, order.getLocalId(), cv) : cv;
                    cv = putValueInCV(Order.ORDER_NAME, order.getNm(), cv);
                    cv = (Long) order.getCustomerid() != -1 ? putValueInCV(
                            Order.ORDER_CUSTOMER_ID, order.getCustomerid(),
                            cv) : cv;
                    cv = putValueInCV(Order.ORDER_CUSTOMER_CONTACT_NAME,
                            order.getCustContactName(), cv);
                    cv = putValueInCV(Order.ORDER_CUSTOMER_CONTACT_NUMBER,
                            order.getCustContactNumber(), cv);
                    cv = putValueInCV(Order.ORDER_CUSTOMER_NAME,
                            order.getCustName(), cv);
                    cv = putValueInCV(Order.ORDER_NOTES,
                            order.getOrdeNotes(), cv);
                    cv = (Long) order.getCustPartCount() != -1 ? putValueInCV(
                            Order.ORDER_CUSTOMER_PART_COUNT,
                            order.getCustPartCount(), cv) : cv;
                    cv = (Long) order.getCustFormCount() != -1 ? putValueInCV(
                            Order.ORDER_CUSTOMER_SERVICE_COUNT,
                            order.getCustFormCount(), cv) : cv;
                    cv = putValueInCV(Order.ORDER_CUSTOMER_SITE_GEOCODE,
                            order.getCustSiteGeocode(), cv);
                    cv = putValueInCV(
                            Order.ORDER_CUSTOMER_SITE_STREET_ADDRESS,
                            order.getCustSiteStreeAdd(), cv);
                    cv = putValueInCV(
                            Order.ORDER_CUSTOMER_SITE_SUITE_ADDRESS,
                            order.getCustSiteSuiteAddress(), cv);
                    cv = putValueInCV(Order.ORDER_END_DATE,
                            order.getToDate(), cv);
                    cv = putValueInCV(Order.ORDER_START_DATE,
                            order.getFromDate(), cv);
                    cv = putValueInCV(Order.ORDER_CUSTOMER_SITE_ID,
                            order.getCustSiteId(), cv);
                    cv = (Long) order.getStatusId() != -1 ? putValueInCV(
                            Order.ORDER_STATUS_ID, order.getStatusId(), cv)
                            : cv;


                    cv = order.getInvoiceNumber() != null ? putValueInCV(
                            Order.ORDER_STATUS_INVOICE_NUMBER,
                            order.getInvoiceNumber(), cv) : cv;

                    /*
                     * cv =
                     * putValueInCV(Order.ORDER_STATUS_PRIMARY_WORKER_ID,
                     * order.getListAddWorkerPiped(), cv);
                     */
                    cv = (Long) order.getOrderTypeId() != -1 ? putValueInCV(
                            Order.ORDER_STATUS_ORDER_TYPE_ID,
                            order.getOrderTypeId(), cv) : cv;
                    cv = order.getPoNumber() != null ? putValueInCV(
                            Order.ORDER_STATUS_PO_NUMBER,
                            order.getPoNumber(), cv) : cv;
                    cv = order.getOrderAlert() != null ? putValueInCV(
                            Order.ORDER_ALERT,
                            order.getOrderAlert(), cv) : cv;

                    cv = (String) order.getPrimaryWorkerId() != null ? putValueInCV(
                            Order.ORDER_STATUS_PRIMARY_WORKER_ID,
                            order.getPrimaryWorkerId(), cv) : cv;
                    cv = (Long) order.getPriorityTypeId() != -1 ? putValueInCV(
                            Order.ORDER_STATUS_PRIORITY_TYPE_ID,
                            order.getPriorityTypeId(), cv) : cv;
                    cv = (Long) order.getCustMetaCount() != -1 ? putValueInCV(
                            Order.ORDER_CUSTOMER_MEDIA_COUNT,
                            order.getCustMetaCount(), cv) : cv;
                    cv = putValueInCV(Order.ORDER_STATUS_SUMMARY,
                            order.getSummary(), cv);
                    cv = putValueInCV(Order.ORDER_END_TIME,
                            order.getEndTime(), cv);
                    cv = putValueInCV(Order.ORDER_START_TIME,
                            order.getStartTime(), cv);
                    cv = putValueInCV(Order.ORDER_TYPE,
                            order.getType(), cv);
                    cv.put(modifiedField, order.getModified());
                    cv = putValueInCV(Order.ORDER_FLG,
                            order.getFlg(), cv);
                    cv = putValueInCV(Order.ORDER_START_TIME,
                            order.getStartTime(), cv);
                    cv = putValueInCV(Order.ORDER_END_TIME,
                            order.getEndTime(), cv);
                    cv = putValueInCV(Order.ORDER_EST,
                            order.getEst(), cv);
                    cv = putValueInCV(Order.ORDER_LST,
                            order.getLst(), cv);
                    cv = putValueInCV(Order.ORDER_CUSTOMER_TYPE_NAME,
                            order.getCustypename(), cv);
                    cv = putValueInCV(Order.ORDER_SITE_TYPE_NAME,
                            order.getSitetypename(), cv);
                    cv = (Long) order.getSortindex() != -1 ? putValueInCV(
                            Order.ORDER_SORTINDEX,
                            order.getSortindex(), cv) : cv;
                    cv = (Long) order.getAudioCount() != -1 ? putValueInCV(
                            Order.ORDER_AUDIO,
                            order.getAudioCount(), cv) : cv;
                    cv = (Long) order.getSigCount() != -1 ? putValueInCV(
                            Order.ORDER_SIG,
                            order.getSigCount(), cv) : cv;
                    cv = (Long) order.getImgCount() != -1 ? putValueInCV(
                            Order.ORDER_IMG,
                            order.getImgCount(), cv) : cv;
                    cv = (Long) order.getNotCount() != -1 ? putValueInCV(
                            Order.ORDER_NOTE,
                            order.getNotCount(), cv) : cv;
                    cv = putValueInCV(Order.ORDER_TEL_TYPEID,
                            order.getTelTypeId(), cv);

                    cv = order.getContactId() != -1 ? putValueInCV(Order.ORDER_CUSTOMER_CONTACT_ID,
                            order.getContactId(), cv) : cv;
                    cv = (Long) order.getDocCount() != -1 ? putValueInCV(
                            Order.ORDER_DOCS,
                            order.getDocCount(), cv) : cv;

                    cv = (Long) order.getCustFormCount() != -1 ? putValueInCV(
                            Order.ORDER_CUSTOMER_FORM_COUNT,
                            order.getCustFormCount(), cv) : cv;
                    //} else {
                    //	Logger.e(context, Utilities.TAG, "Insert Data is not in boundary");
                    //	cv = null;
                    //}

                    cv = putValueInCV(Order.ORDER_CUSTOMER_CTID,
                            order.getCustids(), cv);


                    column = Order.ORDER_NAME;
                } else if (objectTypeList[i] instanceof OrderTask) {

                    OrderTask orderTask = (OrderTask) objectTypeList[i];
                    if (orderTask == null) {
                        continue;
                    }
                    cv.put(OrderTask.ORDER_ID, orderTask.getId());
                    cv.put(OrderTask.ORDER_TASK_ID,
                            orderTask.getOrder_task_id());
                    cv = (Long) orderTask.getLocalId() != -1 ? putValueInCV(
                            localidField, orderTask.getLocalId(), cv) : cv;
                    cv.put(OrderTask.TASK_ID, orderTask.getTask_id());
                    cv.put(OrderTask.ORDER_SERV_UPDATE_TIME, orderTask.getUpd_time());
                    cv.put(modifiedField, orderTask.getModified());


                    cv.put(OrderTask.TREE_CYCLE, orderTask.getTree_cycle());
                    cv.put(OrderTask.TRIM_TYPE, orderTask.getTree_type());
                    cv.put(OrderTask.PRIORITY, orderTask.getPriority());
                    cv.put(OrderTask.ACTION_STATUS, orderTask.getAction_status());
                    cv.put(OrderTask.TREE_OWNER, orderTask.getTree_owner());
                    cv.put(OrderTask.TREE_HT, orderTask.getTree_ht());
                    cv.put(OrderTask.TREE_DIA, orderTask.getTree_dia());
                    cv.put(OrderTask.TREE_CLEARENCE, orderTask.getTree_clearence());
                    cv.put(OrderTask.TREE_EXPCOUNT, orderTask.getTree_expcount());
                    cv.put(OrderTask.TREE_ACTUALCOUNT, orderTask.getTree_actualcount());
                    cv.put(OrderTask.TREE_TIMESPENT, orderTask.getTree_timespent());
                    cv.put(OrderTask.TREE_TM, orderTask.getTree_tm());
                    cv.put(OrderTask.TREE_MSC, orderTask.getTree_msc());
                    cv.put(OrderTask.TREE_COMMENT, orderTask.getTree_comment());
                    cv.put(OrderTask.TREE_PCOMMENT, orderTask.getTree_pcomment());
                    cv.put(OrderTask.TREE_ALERT, orderTask.getTree_alert());
                    cv.put(OrderTask.TREE_NOTE, orderTask.getTree_note());
                    cv.put(OrderTask.TREE_GEO, orderTask.getTree_geo());
                    cv.put(OrderTask.TREE_ACCESSPATH, orderTask.getTree_accesspath());
                    cv.put(OrderTask.TREE_CT1, orderTask.getTree_ct1());
                    cv.put(OrderTask.TREE_CT2, orderTask.getTree_ct2());
                    cv.put(OrderTask.TREE_CT3, orderTask.getTree_ct3());
                    cv.put(localOidField, orderTask.getLocalOid());
                    column = OrderTask.ORDER_ID;

                } /*else if (objectTypeList[i] instanceof OrderTaskOld) { // by Mandeep for OrderTaskOld tables
                    OrderTaskOld orderTasks = (OrderTaskOld) objectTypeList[i];
                    if (orderTasks == null) {
                        continue;
                    }
                    cv.put(OrderTaskOld.ORDER_ID, orderTasks.getOid());
                    cv.put(OrderTaskOld.ORDER_TASK_ID, orderTasks.getOrder_task_id());
                    cv.put(OrderTaskOld.ORDER_TASK_TYPE_ID, orderTasks.getTask_type_id());
                    cv.put(OrderTaskOld.ORDER_TASK_RATE, orderTasks.getOrder_task_RATE());
                    cv.put(OrderTaskOld.ORDER_SERV_UPDATE_TIME, orderTasks.getUpd_time());
                    cv.put(modifiedField, orderTasks.getModified());
                    column = OrderTaskOld.ORDER_ID;
                }*/ else if (objectTypeList[i] instanceof OrderPart) {

                    OrderPart orderPart = (OrderPart) objectTypeList[i];
                    if (orderPart == null) {
                        continue;
                    }
                    cv.put(OrderPart.ORDER_ID, orderPart.getOid());
                    cv.put(OrderPart.ORDER_PART_QTY,
                            orderPart.getOrder_part_QTY());
                    cv.put(OrderPart.ORDER_BARCODE,
                            orderPart.getPart_barcode());

                    cv.put(OrderPart.ORDER_PART_ID,
                            orderPart.getOrder_part_id());
                    cv = (Long) orderPart.getLocalId() != -1 ? putValueInCV(
                            localidField, orderPart.getLocalId(), cv) : cv;
                    cv.put(OrderPart.PART_TID, orderPart.getPart_type_id());
                    cv.put(OrderPart.ORDER_UPDATE_TIME, orderPart.getUpd_time());
                    cv.put(modifiedField, orderPart.getModified());
                    cv.put(localOidField, orderPart.getLocalOid());
                    column = OrderPart.ORDER_ID;
                } else if (objectTypeList[i] instanceof OrderMedia) {

                    // not saving file data.
                    OrderMedia orderMedia = (OrderMedia) objectTypeList[i];
                    if (orderMedia == null) {
                        continue;
                    }
                    cv.put(OrderMedia.ID, orderMedia.getId());
                    cv = (Long) orderMedia.getLocalId() != -1 ? putValueInCV(
                            localidField, orderMedia.getLocalId(), cv) : cv;
                    cv.put(OrderMedia.ORDER_ID, orderMedia.getOrderid());
                    cv.put(OrderMedia.ORDER_MEDIA_TYPE,
                            orderMedia.getMediatype());
                    cv.put(OrderMedia.ORDER_GEO, orderMedia.getGeocode());
                    cv.put(OrderMedia.ORDER_FILE_DESC,
                            orderMedia.getFile_desc());
                    cv.put(OrderMedia.ORDER_FILE_MIME_TYPE,
                            orderMedia.getMimetype());
                    cv.put(OrderMedia.ORDER_META_UPDATE_TIME,
                            orderMedia.getUpd_time());
                    cv.put(OrderMedia.ORDER_META_PATH,
                            orderMedia.getMetapath());
                    cv.put(DBHandler.modifiedField,
                            orderMedia.getModify());
                    cv.put(localOidField, orderMedia.getLocalOid());
                    String frmKey = orderMedia.getFormKey();
                    if(frmKey == null) {
                        frmKey = "";
                    }
                    cv.put(OrderMedia.FORM_KEY,
                           frmKey);
                    String frmId = orderMedia.getFrmfiledid();
                    if(frmId == null) {
                        frmId = "";
                    }
                    cv.put(OrderMedia.FORM_FIELD_ID,
                            frmId);
                    column = OrderMedia.ID;
                } else if (objectTypeList[i] instanceof OrderNotes) {

                    OrderNotes orderNotes = (OrderNotes) objectTypeList[i];
                    if (orderNotes == null) {
                        continue;
                    }
                    cv.put(OrderNotes.ORDER_ID, orderNotes.getId());
                    cv.put(OrderNotes.ORDER_NOTE, orderNotes.getOrdernote());
                    cv.put(DBHandler.modifiedField, orderNotes.getModified());
                    column = OrderNotes.ORDER_ID;
                } else if (objectTypeList[i] instanceof Site) {

                    Site site = (Site) objectTypeList[i];
                    if (site == null) {
                        continue;
                    }
                    cv.put(Site.SITE_ID, site.getId());
                    cv.put(Site.SITE_CID, site.getCid());
                    cv.put(Site.SITE_NAME, site.getNm());
                    cv.put(Site.SITE_ADDRESS, site.getAdr());
                    cv.put(Site.SITE_ADR2, site.getAdr2());
                    cv.put(Site.SITE_UPD, site.getUpd());
                    cv.put(Site.SITE_DTL, site.getDetail());
                    cv.put(Site.SITE_TYPE_ID, site.getTid());
                    cv.put(Site.SITE_TYPE_NM, site.getSitetypenm());
                    cv.put(DBHandler.modifiedField, site.getModify());
                    cv.put(localidField, site.getLocalid());
                    cv.put(Site.SITE_GEO, site.getGeo());
                    column = Site.SITE_ID;

                } else if (objectTypeList[i] instanceof Assets) {//YD inserting assets coming in customer xml
                    Assets asset = (Assets) objectTypeList[i];
                    if (asset == null) {
                        continue;
                    }
                    cv.put(Assets.ASSET_ID, asset.getId());
                    cv.put(Assets.ASSET_CID, asset.getCid());
                    cv.put(Assets.ASSET_GEO, asset.getGeoLoc());
                    cv.put(Assets.ASSET_FTYPE_ID, asset.getFtid());
                    cv.put(Assets.ASSET_FDATA, asset.getFdata());
                    cv.put(Assets.ASSET_UPD, asset.getUpd());
                    cv.put(Assets.ASSET_OID, asset.getOid());

                    if (asset.getModify() == null)
                        cv.put(DBHandler.modifiedField, "");
                    else
                        cv.put(DBHandler.modifiedField, asset.getModify());

                    cv.put(localidField, asset.getLocalid());
                    column = Assets.ASSET_ID;
                } else if (objectTypeList[i] instanceof AssetsType) {//YD inserting assets coming in customer xml
                    AssetsType assetType = (AssetsType) objectTypeList[i];
                    if (assetType == null) {
                        continue;
                    }
                    cv.put(AssetsType.ASSETS_TYPE_ID, assetType.getId());
                    cv.put(AssetsType.ASSETS_TYPE_NAME, assetType.getName());
                    cv.put(AssetsType.ASSETS_TYPE_XID, assetType.getXid());
                    cv.put(AssetsType.ASSETS_TYPE_UPD, assetType.getUpd());
                    column = AssetsType.ASSETS_TYPE_ID;
                } else if (objectTypeList[i] instanceof CustomerContact) {

                    CustomerContact cct = (CustomerContact) objectTypeList[i];
                    if (cct == null) {
                        continue;
                    }
                    cv.put(CustomerContact.CONTACT_ID, cct.getId());
                    cv.put(CustomerContact.CUSTOMER_ID, cct.getCustomerid());
                    cv.put(CustomerContact.CONTACT_NAME, cct.getContactname());
                    cv.put(CustomerContact.CONTACT_TEL, cct.getContacttel());
                    cv.put(CustomerContact.CONTACT_TYPE, cct.getContactType());
                    cv.put(CustomerContact.CONTACT_EML, cct.getContactEml());

                    if (cct.getModify() == null)
                        cv.put(DBHandler.modifiedField, "");
                    else
                        cv.put(DBHandler.modifiedField, cct.getModify());
                    column = CustomerContact.CONTACT_ID;
                } else if (objectTypeList[i] instanceof CheckinOut) {

                    CheckinOut ccto = (CheckinOut) objectTypeList[i];
                    if (ccto == null) {
                        continue;
                    }
                    cv.put(CheckinOut.CC_GEO, ccto.getGeo());
                    cv.put(CheckinOut.CC_TID, ccto.getTid());
                    cv.put(CheckinOut.CC_TSTAMP, ccto.getStmp());
                    cv.put(CheckinOut.CC_LSTOID, ccto.getLstoid());// It is generating job status bugs
                   cv.put(CheckinOut.CC_NXTOID, ccto.getNxtoid());
                    cv.put(DBHandler.modifiedField, ccto.getModify());
                    column = CheckinOut.CC_TSTAMP;
                } else if (objectTypeList[i] instanceof SiteType) {

                    SiteType sitetype = (SiteType) objectTypeList[i];
                    if (sitetype == null) {
                        continue;
                    }
                    cv.put(SiteType.SITE_TYPE_ID, sitetype.getId());
                    cv.put(SiteType.SITE_TYPE_NAME, sitetype.getNm());
                    cv.put(SiteType.SITE_TYPE_TID, sitetype.getTid());
                    cv.put(SiteType.SITE_TYPE_UPD, sitetype.getUpd());
                    cv.put(SiteType.SITE_TYPE_dtl,sitetype.getDtl());
                    cv.put(SiteType.SITE_TYPE_CAP, sitetype.getCap());


                    column = SiteType.SITE_TYPE_ID;
                } else if (objectTypeList[i] instanceof OrdersMessage) {

                    OrdersMessage odrMsg = (OrdersMessage) objectTypeList[i];
                    if (odrMsg == null) {
                        continue;
                    }
                    cv.put(OrdersMessage.MESSAGE_TIMESTAMP, odrMsg.getStmp());
                    cv.put(OrdersMessage.MESSAGE_TYPE_ID, odrMsg.getTid());
                    cv.put(OrdersMessage.MESSAGE_ORDER_ID, odrMsg.getOid());
                    cv.put(OrdersMessage.MESSAGE_CUST_ID, odrMsg.getCid());
                    cv.put(OrdersMessage.MESSAGE_CONTACT_ID, odrMsg.getCntid());
                    cv.put(OrdersMessage.MESSAGE_TELL, odrMsg.getTel());
                    cv.put(OrdersMessage.MESSAGE_TIMEZONE, odrMsg.getTz());
                    cv.put(OrdersMessage.MESSAGE_GEO, odrMsg.getGeo());
                    cv.put(modifiedField, odrMsg.getModified());


                    column = OrdersMessage.MESSAGE_ORDER_ID;
                } else if (objectTypeList[i] instanceof OrderStatus) {

                    OrderStatus odrStat = (OrderStatus) objectTypeList[i];
                    if (odrStat == null) {
                        continue;
                    }
                    cv.put(OrderStatus.STATUS_ID, odrStat.getId());
                    cv.put(OrderStatus.STATUS_TYPE_NM, odrStat.getNm());
                    cv.put(OrderStatus.STATUS_TYPE_GRPID, odrStat.getGrpId());
                    cv.put(OrderStatus.STATUS_TYPE_GRPSEQ, odrStat.getGrpSeq());
                    cv.put(OrderStatus.STATUS_TYPE_ISGRP, odrStat.getIsgroup());
                    cv.put(OrderStatus.STATUS_TYPE_SEQ, odrStat.getSeq());
                    cv.put(OrderStatus.STATUS_TYPE_ABR, odrStat.getAbbrevation());
                    cv.put(OrderStatus.STATUS_TYPE_ISVIS, odrStat.getIsVisible());
                    cv.put(modifiedField, odrStat.getModified());


                    column = OrderStatus.STATUS_ID;
                } else if (objectTypeList[i] instanceof ClientSite) {

                    ClientSite client_Site = (ClientSite) objectTypeList[i];
                    if (client_Site == null) {
                        continue;
                    }
                    cv.put(ClientSite.CLIENTSITE_ID, client_Site.getId());
                    cv.put(ClientSite.CLIENTSITE_NAME, client_Site.getNm());
                    cv.put(ClientSite.CLIENTSITE_ADR, client_Site.getAdr());
                    cv.put(ClientSite.CLIENTSITE_ADR2, client_Site.getAdr2());
                    cv.put(ClientSite.CLIENTSITE_TZ, client_Site.getTz());
                    cv.put(ClientSite.CLIENTSITE_GEO, client_Site.getGeo());
                    cv.put(ClientSite.CLIENTSITE_XID, client_Site.getXid());
                    cv.put(ClientSite.CLIENTSITE_UPD, client_Site.getUpd());
                    cv.put(ClientSite.CLIENTSITE_BY, client_Site.getBy());

                    column = ClientSite.CLIENTSITE_ID;

                }else if (objectTypeList[i] instanceof Form) {
                    Form form = (Form) objectTypeList[i];
                    if (form == null) {
                        continue;
                    }
                    if (formlist != null) {
                        cv.put(Form.FORM_ID, form.getId());
                        cv.put(Form.FORM_FDATA, form.getFdata());
                        cv.put(Form.FORM_OID, form.getOid());
                        cv.put(Form.FORM_UPD, form.getUpd());
                        cv.put(Form.FORM_TID, form.getFtid());
                        cv.put(modifiedField, form.getModified());
                        cv = (Long) form.getLocalid() != -1 ? putValueInCV(
                                localidField, form.getLocalid(), cv) : cv;
                        cv.put(localOidField, form.getLocalOid());
                        cv.put(Form.FORM_KEY_ONLY, form.getFormkeyonly());
                        column = Form.FORM_ID;
                    }
                }

                else if (objectTypeList[i] instanceof Shifts) {

                    Shifts shift = (Shifts) objectTypeList[i];
                    if (shift == null) {
                        continue;
                    }
                    if (shiftlist != null && !shiftlist.contains(shift.localid)) {
                        cv.put(Shifts.SHIFT_ID, shift.id);
                        cv.put(Shifts.SHIFT_TID, shift.tid);
                        cv.put(Shifts.SHIFT_NM, shift.nm);
                        cv.put(Shifts.SHIFT_TMSLOT, shift.tmslot);
                        cv.put(Shifts.SHIFT_BRKSLOT, shift.brkSlot);
                        cv.put(Shifts.SHIFT_ADR, shift.adr);
                        cv.put(Shifts.SHIFT_DT, shift.dt);
                        cv.put(Shifts.SHIFT_LID, shift.lid);
                        cv.put(Shifts.SHIFT_TERRI, shift.terrgeo);
                        cv.put(DBHandler.modifiedField, shift.modified);
                        cv.put(localidField, shift.localid);
                        column = Shifts.SHIFT_ID;
                    }
                }

                long result = 0;
                if (cv != null) {
                    if (queryType != -1) {
                        switch (queryType) {
                            case QUERY_FOR_ORIG:
                                Log.i(Utilities.TAG, "Inserting in original table.");
                                result = db.insert(table, column, cv);

                                break;
                            case QUERY_FOR_TEMP:
                                Log.i(Utilities.TAG, "Inserting in temp table.");
                                result = db.insert(tempTablePrefix + table, column, cv);
                                break;
                        }
                    } else {
                        Log.e(Utilities.TAG, "not inserting data becuase query type is unknown.");
                    }
                    if (result != 0 && result != -1)
                        totalRecordsInserted++;
                } else {
                    Log.e(Utilities.TAG, "cv is null");
                }
            }
            Utilities.log(context, totalRecordsInserted + " records inserted.");
            return totalRecordsInserted;
        } catch (Exception e) {
            e.printStackTrace();
            return recordsNotInserted;
        }
    }

    private static ArrayList<Long> getformlist(Context context) {
        Cursor cursormodify = getOfflineModifiedShift(context, DBHandler.formTable, DBHandler.updated);
        ArrayList<Long> list = new ArrayList<>();
        if (cursormodify.moveToFirst()) {
            do {
                list.add(cursormodify.getLong(cursormodify.getColumnIndex(localidField)));
            } while (cursormodify.moveToNext());
        }

        return list;
    }


    public static ArrayList<Long> getshiftList(Context context) {
        Cursor cursormodify = getOfflineModifiedShift(context, DBHandler.shiftTable, DBHandler.updated);
        ArrayList<Long> list = new ArrayList<>();
        if (cursormodify.moveToFirst()) {
            do {
                list.add(cursormodify.getLong(cursormodify.getColumnIndex(localidField)));
            } while (cursormodify.moveToNext());
        }
        return list;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
/*Log.d("niki","old version "+oldVersion + " newVer "+newVersion);
        if(oldVersion ==1) {
            db.execSQL(
                    "ALTER TABLE " +
                            orderPartTable +
                            " ADD " +
                            OrderPart.ORDER_BARCODE +"test"+
                            " TEXT;"
            );
        }*/

    }

    public static int deleteExpiredData(Context context, Date start_date, Date end_date) {
        Utilities.log(context, "inside deleteExpiredData");
        String startDateString = Utilities.getStringFromDate(context, start_date,
                Utilities.DEFAULT_DATE_FORMAT);
        String endDateString = Utilities.getStringFromDate(context, end_date,
                Utilities.DEFAULT_DATE_FORMAT);
        Utilities.log(context,
                "Range has been changed now deleting expired data for start dateString : "
                        + startDateString + " : endDate : " + endDateString);
        //String whereClause = Order.ORDER_START_DATE + " >= '" + startDateString + "' AND " + Order.ORDER_START_DATE + " <= '"+endDateString+"'";
        String whereClause = Order.ORDER_START_DATE + " NOT BETWEEN '" + start_date + "' AND '" + end_date + "'";
        Log.i(Utilities.TAG, "where clause in delete : " + whereClause);
        return delete(context, DBHandler.orderListTable, whereClause);
    }

    public static int delete(Context context, String tableName,
                             String whereClause) {
        return delete(context, tableName, whereClause, QUERY_FOR_ORIG);
    }

    public static int delete(Context context, String tableName,
                             String whereClause, int queryType) {
        int result = 0;
        try {
            switch (queryType) {
                case QUERY_FOR_ORIG:
                    result = db.delete(tableName, whereClause, null);
                    break;
                case QUERY_FOR_TEMP:
                    result = db.delete(tempTablePrefix + tableName, whereClause, null);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

    public static HashMap<Long, Object> select(Context context, String query, String action) {
        Utilities.log(context, "DBHandler select called. Query = " + query);
        HashMap<Long, Object> xml = null;
        Cursor cursor = null;
        try {
            if(query!=null) {
                cursor = db.rawQuery(query, null);
                Log.i(Utilities.TAG, "rows count is : " + cursor.getCount());
                if (cursor != null && cursor.getCount() > 0) {
                    xml = ObjectHandler.createObjectFromDB(cursor, action, context, DBHandler.DB_GET_ALL_RECORD);

                }
            }
            return xml;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static String getValueFromTabletoCheckId(Context context, String tablename, String columnname, String whereClause) {
        String query;
        if (whereClause != null)
            query = "SELECT " + columnname + " from " + tablename + " where " + whereClause;
        else
            query = "SELECT " + columnname + " from " + tablename;
        Cursor cursor = null;
        String value = null;
        try {
            cursor = db.rawQuery(query, null);
            Log.i(Utilities.TAG, "getValueFromTable rows count is : " + cursor.getCount());
            if (cursor != null && cursor.moveToFirst()) {
                value = getvaluefromCursor(cursor, columnname);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return value;
    }


    public static String getValueFromTable(Context context, String tablename, String columnname, String whereClause) {
        String query;
        if (whereClause != null)
            query = "SELECT " + columnname + " from " + tablename + " where " + whereClause;
        else
            query = "SELECT " + columnname + " from " + tablename;
        Cursor cursor = null;
        String value = null;
        try {
            cursor = db.rawQuery(query, null);
            Log.i(Utilities.TAG, "getValueFromTable rows count is : " + cursor.getCount());
            if (cursor != null && cursor.moveToFirst()) {
                value = getvaluefromCursor(cursor, columnname);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return value;
    }

    public static String getServerOrderId(String tablename, String columnname, String whereClause) {
        String query;
        query = "SELECT " + columnname + " from " + tablename + " where " + whereClause;

        Cursor cursor = null;
        String value = null;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                value = getvaluefromCursor(cursor, columnname);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return value;
    }

    /**
     * performing update operation according to id.
     *
     * @param context
     * @param table
     * @return
     */

    public static int updateRecord(Context context, String table,
                                   Object objectTypeList[]) {
        return updateRecord(context, table, objectTypeList, QUERY_FOR_ORIG);
    }

    public static int updateRecord(Context context, String table,  //YD
                                   Object objectTypeList[], int queryType) {
        Utilities.log(context, "Inside update Records.- Querytype: " + queryType);
        ContentValues cv = null;

        try {
            String column = "";
            long id = 0;
            int totalRecordsUpdated = 0;
            // Object objectTypeList[] = new
            // XMLHandler(context).getCustomerListValuesFromXML(xmlString);
            for (int i = 0; i < objectTypeList.length; i++) {
                String whereClause = "";
                if (cv == null)
                    cv = new ContentValues();

                if (objectTypeList[i] instanceof ClientLocation) {
                    ClientLocation cLoc = (ClientLocation) objectTypeList[i];
                    cv.put(ClientLocation.CLIENT_ID, cLoc.getId());
                    cv.put(ClientLocation.Geo.CLIENT_LAT, cLoc.getGeo()
                            .getLatitude());
                    cv.put(ClientLocation.Geo.CLIENT_LONG, cLoc.getGeo()
                            .getLongitude());
                    column = ClientLocation.CLIENT_ID;
                    id = cLoc.getId();

                } else if (objectTypeList[i] instanceof OrderTypeList) {

                    OrderTypeList orderType = (OrderTypeList) objectTypeList[i];
                    cv.put(OrderTypeList.ORDER_TYPE_ID, orderType.getId());
                    cv.put(OrderTypeList.ORDER_TYPE_NAME, orderType.getNm());
                    column = OrderTypeList.ORDER_TYPE_ID;
                    id = orderType.getId();

                } else if (objectTypeList[i] instanceof Parts) {

                    Parts partType = (Parts) objectTypeList[i];
                    cv.put(Parts.PART_TYPE_ID, partType.getId());
                    cv.put(Parts.PART_TYPE_NAME, partType.getName());
                    cv.put(Parts.PART_TYPE_DESC, partType.getDesc());
                    cv.put(Parts.PART_TYPE_CTID, partType.getCtid());
                    column = Parts.PART_TYPE_ID;
                    id = partType.getId();

                } else if (objectTypeList[i] instanceof SiteType) {
                    SiteType sitetype = (SiteType) objectTypeList[i];
                    cv.put(SiteType.SITE_TYPE_ID, sitetype.getId());
                    cv.put(SiteType.SITE_TYPE_NAME, sitetype.getNm());
                    cv.put(SiteType.SITE_TYPE_TID, sitetype.getTid());
                    cv.put(SiteType.SITE_TYPE_UPD, sitetype.getUpd());
                    cv.put(SiteType.SITE_TYPE_CAP, sitetype.getCap());
                } else if (objectTypeList[i] instanceof CustomerType) {

                    CustomerType customerType = (CustomerType) objectTypeList[i];
                    cv.put(CustomerType.CUSTOMER_TYPE_ID, customerType.getId());
                    cv.put(CustomerType.CUSTOMER_TYPE_NAME,
                            customerType.getNm());
                    column = CustomerType.CUSTOMER_TYPE_ID;
                    id = customerType.getId();

                } else if (objectTypeList[i] instanceof ServiceType) {

                    ServiceType serviceType = (ServiceType) objectTypeList[i];
                    cv.put(ServiceType.SERVICE_TYPE_ID, serviceType.getId());
                    cv.put(ServiceType.SERVICE_TYPE_NAME, serviceType.getNm());
                    cv.put(ServiceType.SERVICE_TYPE_DTL, serviceType.getDtl());
                    column = ServiceType.SERVICE_TYPE_ID;
                    id = serviceType.getId();

                } else if (objectTypeList[i] instanceof Worker) {

                    Worker worker = (Worker) objectTypeList[i];
                    cv.put(Worker.WORKER_ID, worker.getId());
                    cv.put(Worker.WORKER_NAME, worker.getNm());
                    cv.put(Worker.WORKER_LID, worker.getLid());
                    cv.put(Worker.WORKER_WEEK, worker.getWrkwk());
                    cv.put(Worker.WORKER_DWEEK, worker.getDwrkwk());
                    cv.put(Worker.WORKER_BRK, worker.getBrk());
                    cv.put(Worker.WORKER_TID, worker.getTid());
                    column = Worker.WORKER_ID;
                    id = worker.getId();

                } else if (objectTypeList[i] instanceof Customer) {

                    Customer customer = (Customer) objectTypeList[i];
                    cv.put(Customer.CUSTOMER_ID, customer.getId());
                    cv.put(Customer.CUSTOMER_NAME, customer.getNm());
                    column = Customer.CUSTOMER_ID;
                    id = customer.getId();

                } else if (objectTypeList[i] instanceof Order) {

                    Order order = (Order) objectTypeList[i];

                    /*
                     * boolean isInBoundaryDate = false; try { Date date = new
                     * Date(order.getFromDate()); isInBoundaryDate =
                     * Utilities.isValidDate(date); Logger.i(context, Utilities.TAG,
                     * "start Date valid : " + isInBoundaryDate); } catch
                     * (Exception e) { e.printStackTrace(); isInBoundaryDate =
                     * false; }
                     */

				/*	cv = (Long) order.getId() != -1 ? putValueInCV(
							Order.ORDER_ID, order.getId(), cv) : cv;*/ // Vicky do not update the orderid. Because it may happen the local Id updates the server id
                    cv = (Long) order.getCustomerid() != -1 ? putValueInCV(
                            Order.ORDER_CUSTOMER_ID, order.getCustomerid(), cv)
                            : cv;
                    cv = order.getNm() != "" ? putValueInCV(Order.ORDER_NAME,
                            order.getNm(), cv) : cv;
                    cv = order.getCustContactName() != "" ? putValueInCV(
                            Order.ORDER_CUSTOMER_CONTACT_NAME,
                            order.getCustContactName(), cv) : cv;
                    cv = order.getCustContactNumber() != "" ? putValueInCV(
                            Order.ORDER_CUSTOMER_CONTACT_NUMBER,
                            order.getCustContactNumber(), cv) : cv;
                    cv = order.getCustName() != "" ? putValueInCV(
                            Order.ORDER_CUSTOMER_NAME, order.getCustName(), cv)
                            : cv;
                    cv = order.getOrdeNotes() != "" ? putValueInCV(
                            Order.ORDER_NOTES, order.getOrdeNotes(), cv) : cv;
                    cv = (Long) order.getCustPartCount() != -1 ? putValueInCV(
                            Order.ORDER_CUSTOMER_PART_COUNT,
                            order.getCustPartCount(), cv) : cv;
                    cv = (Long) order.getCustFormCount() != -1 ? putValueInCV(
                            Order.ORDER_CUSTOMER_SERVICE_COUNT,
                            order.getCustFormCount(), cv) : cv;
                    //String location = Utilities.getLocation(context);
                    //location = location.replace("#", ",");
                    cv = order.getCustSiteGeocode() != "" ? putValueInCV(
                            Order.ORDER_CUSTOMER_SITE_GEOCODE,
                            order.getCustSiteGeocode(), cv) : cv;
					/*cv = order.getCustSiteGeocode() != "" ? putValueInCV(
							Order.ORDER_CUSTOMER_SITE_GEOCODE,
							location, cv) : cv;*/

                    cv = order.getCustSiteStreeAdd() != "" ? putValueInCV(
                            Order.ORDER_CUSTOMER_SITE_STREET_ADDRESS,
                            order.getCustSiteStreeAdd(), cv) : cv;
                    cv = order.getCustSiteSuiteAddress() != "" ? putValueInCV(
                            Order.ORDER_CUSTOMER_SITE_SUITE_ADDRESS,
                            order.getCustSiteSuiteAddress(), cv) : cv;
                    cv = order.getToDate() != "" ? putValueInCV(
                            Order.ORDER_END_DATE, order.getToDate(), cv) : cv;
                    cv = order.getFromDate() != "" ? putValueInCV(
                            Order.ORDER_START_DATE, order.getFromDate(), cv)
                            : cv;
                    cv = order.getStartTime() != -1 ? putValueInCV(
                            Order.ORDER_START_TIME, order.getStartTime(), cv)
                            : cv;
                    cv = order.getStartTime() != -1 ? putValueInCV(
                            Order.ORDER_END_TIME, order.getEndTime(), cv)
                            : cv;
                    cv = (Long) order.getStatusId() != -1 ? putValueInCV(
                            Order.ORDER_STATUS_ID, order.getStatusId(), cv)
                            : cv;

                    cv = order.getOrderAlert() != null ? putValueInCV(
                            Order.ORDER_ALERT, order.getOrderAlert(), cv)
                            : cv;

                    cv = order.getInvoiceNumber() != "" ? putValueInCV(
                            Order.ORDER_STATUS_INVOICE_NUMBER, order.getInvoiceNumber(), cv) :
                            putValueInCV(Order.ORDER_STATUS_INVOICE_NUMBER, order.getInvoiceNumber(), cv);

                    /*
                     * cv = order.getListAddWorkerPiped() != "" ? putValueInCV(
                     * Order.ORDER_STATUS_PRIMARY_WORKER_ID,
                     * order.getListAddWorkerPiped(), cv) : cv;
                     */
                    cv = (Long) order.getOrderTypeId() != -1 ? putValueInCV(
                            Order.ORDER_STATUS_ORDER_TYPE_ID,
                            order.getOrderTypeId(), cv) : cv;
                    cv = order.getPoNumber() != null ? putValueInCV(
                            Order.ORDER_STATUS_PO_NUMBER, order.getPoNumber(),
                            cv) : cv;
                    cv = order.getPrimaryWorkerId() != null ? putValueInCV(
                            Order.ORDER_STATUS_PRIMARY_WORKER_ID,
                            order.getPrimaryWorkerId(), cv) : cv;
                    cv = (Long) order.getPriorityTypeId() != -1 ? putValueInCV(
                            Order.ORDER_STATUS_PRIORITY_TYPE_ID,
                            order.getPriorityTypeId(), cv) : cv;
                    cv = (Long) order.getCustMetaCount() != -1 ? putValueInCV(
                            Order.ORDER_CUSTOMER_MEDIA_COUNT,
                            order.getCustMetaCount(), cv) : cv;
                    cv = order.getSummary() != null ? putValueInCV(
                            Order.ORDER_STATUS_SUMMARY, order.getSummary(), cv)
                            : cv;
                    cv = (Long) order.getEpochTime() != -1 ? putValueInCV(
                            Order.ORDER_EPOCH_TIME,
                            order.getEpochTime(), cv) : cv;

                    if (queryType == 3) {
                        queryType = 2;  // YD just doing it for saving access path geo from googlemap frag TODO fix it later
                    } else {
                        cv = putValueInCV(DBHandler.modifiedField,
                                order.getModified(), cv);
                    }
                    cv = (Long) order.getSortindex() != -1 ? putValueInCV(
                            Order.ORDER_SORTINDEX,
                            order.getSortindex(), cv) : cv;
                    cv = (Long) order.getAudioCount() != -1 ? putValueInCV(
                            Order.ORDER_AUDIO,
                            order.getAudioCount(), cv) : cv;
                    cv = (Long) order.getSigCount() != -1 ? putValueInCV(
                            Order.ORDER_SIG,
                            order.getSigCount(), cv) : cv;
                    cv = (Long) order.getImgCount() != -1 ? putValueInCV(
                            Order.ORDER_IMG,
                            order.getImgCount(), cv) : cv;
                    cv = (Long) order.getNotCount() != -1 ? putValueInCV(
                            Order.ORDER_NOTE,
                            order.getNotCount(), cv) : cv;
                    cv = (Long) order.getDocCount() != -1 ? putValueInCV(
                            Order.ORDER_DOCS,
                            order.getDocCount(), cv) : cv;
                    cv = (Long) order.getAssetsCount() != 0 ? putValueInCV(
                            Order.ORDER_ASSET_COUNT,
                            order.getAssetsCount(), cv) : cv;

                    column = Order.ORDER_ID;
                    id = order.getId();
                    whereClause = localidField + "=" + id + " OR ";

                } else if (objectTypeList[i] instanceof OrderTask) {

                    OrderTask orderTask = (OrderTask) objectTypeList[i];
                    cv.put(OrderTask.ORDER_ID, orderTask.getId());
					/*cv.put(OrderTask.ORDER_TASK_ID,
							orderTask.getOrder_task_id());*/// Vicky do not update the id. Because it may happen the local Id updates the server id

                    cv.put(OrderTask.TASK_ID, orderTask.getTask_id());
                    cv.put(OrderTask.ORDER_SERV_UPDATE_TIME, orderTask.getUpd_time());
                    cv.put(OrderTask.TREE_CYCLE, orderTask.getTree_cycle());
                    cv.put(OrderTask.TRIM_TYPE, orderTask.getTree_type());
                    cv.put(OrderTask.PRIORITY, orderTask.getPriority());
                    cv.put(OrderTask.ACTION_STATUS, orderTask.getAction_status());
                    cv.put(OrderTask.TREE_OWNER, orderTask.getTree_owner());
                    cv.put(OrderTask.TREE_HT, orderTask.getTree_ht());
                    cv.put(OrderTask.TREE_DIA, orderTask.getTree_dia());
                    cv.put(OrderTask.TREE_CLEARENCE, orderTask.getTree_clearence());
                    cv.put(OrderTask.TREE_EXPCOUNT, orderTask.getTree_expcount());
                    cv.put(OrderTask.TREE_ACTUALCOUNT, orderTask.getTree_actualcount());
                    cv.put(OrderTask.TREE_TIMESPENT, orderTask.getTree_timespent());
                    cv.put(OrderTask.TREE_TM, orderTask.getTree_tm());
                    cv.put(OrderTask.TREE_MSC, orderTask.getTree_msc());
                    cv.put(OrderTask.TREE_COMMENT, orderTask.getTree_comment());
                    cv.put(OrderTask.TREE_PCOMMENT, orderTask.getTree_pcomment());
                    cv.put(OrderTask.TREE_ALERT, orderTask.getTree_alert());
                    cv.put(OrderTask.TREE_NOTE, orderTask.getTree_note());
                    cv.put(OrderTask.TREE_GEO, orderTask.getTree_geo());
                    cv.put(OrderTask.TREE_ACCESSPATH, orderTask.getTree_accesspath());
                    cv.put(OrderTask.TREE_CT1, orderTask.getTree_ct1());
                    cv.put(OrderTask.TREE_CT2, orderTask.getTree_ct2());
                    cv.put(OrderTask.TREE_CT3, orderTask.getTree_ct3());
                    String whereclause = OrderTask.ORDER_TASK_ID + "=" + orderTask.getOrder_task_id() + " or " + localidField + "=" + orderTask.getOrder_task_id();
                    String modifystr = getValueFromTable(context, DBHandler.orderTaskTable, DBHandler.modifiedField, whereclause);
                    if (modifystr != null && modifystr.equals(""))
                        modifystr = "update";
                    cv.put(DBHandler.modifiedField, modifystr);
                    column = OrderTask.ORDER_TASK_ID;
                    id = orderTask.getOrder_task_id();
                    whereClause = localidField + "=" + id + " OR ";


                }/* else if (objectTypeList[i] instanceof OrderTaskOld) { // By Mandeep for OrderTaskOld
                    OrderTaskOld orderTasks = (OrderTaskOld) objectTypeList[i];
                    cv.put(OrderTaskOld.ORDER_ID, orderTasks.getOid());
                    cv.put(OrderTaskOld.ORDER_TASK_RATE, orderTasks.getOrder_task_RATE());
                    cv.put(OrderTaskOld.ORDER_TASK_ID, orderTasks.getOrder_task_id());
                    cv.put(OrderTaskOld.ORDER_TASK_TYPE_ID, orderTasks.getTask_type_id());
                    cv.put(OrderTaskOld.ORDER_SERV_UPDATE_TIME, orderTasks.getUpd_time());
                    String whereclause = OrderTaskOld.ORDER_TASK_ID + "=" + orderTasks.getOrder_task_id();
                    String modifystr = getValueFromTable(context, DBHandler.orderTaskTableOld, DBHandler.modifiedField, whereclause);
                    if (modifystr != null && modifystr.equals(""))
                        modifystr = "update";
                    cv.put(DBHandler.modifiedField, modifystr);
                    column = OrderTaskOld.ORDER_TASK_ID;
                    id = orderTasks.getOrder_task_id();
                }*/ else if (objectTypeList[i] instanceof OrderPart) {

                    OrderPart orderPart = (OrderPart) objectTypeList[i];
                    cv.put(OrderPart.ORDER_ID, orderPart.getOid());
                    cv.put(OrderPart.ORDER_PART_QTY,
                            orderPart.getOrder_part_QTY());
                    cv.put(OrderPart.ORDER_BARCODE,
                            orderPart.getPart_barcode());
					/*cv.put(OrderPart.ORDER_PART_ID,
							orderPart.getOrder_part_id());*/// Vicky do not update the id. Because it may happen the local Id updates the server id
                    cv.put(OrderPart.PART_TID, orderPart.getPart_type_id());
                    cv.put(OrderPart.ORDER_UPDATE_TIME, orderPart.getUpd_time());
                    String whereclause = OrderPart.ORDER_PART_ID + "=" + orderPart.getOrder_part_id() + " or " + localidField + "=" + orderPart.getOrder_part_id();
                    String modifystr = getValueFromTable(context, DBHandler.orderPartTable, DBHandler.modifiedField, whereclause);
                    if (modifystr != null && modifystr.equals(""))
                        modifystr = "update";
                    cv.put(DBHandler.modifiedField, modifystr);
                    column = OrderPart.ORDER_PART_ID;
                    id = orderPart.getOrder_part_id();
                    whereClause = localidField + "=" + id + " OR ";
                }

                /*cv.put(OrderPart.ORDER_ID, orderPart.getOid());
                cv.put(OrderPart.ORDER_PART_QTY,
                        orderPart.getOrder_part_QTY());
                cv.put(OrderPart.ORDER_PART_ID,
                        orderPart.getOrder_part_id());
                cv = (Long) orderPart.getLocalId() != -1 ? putValueInCV(
                        localidField, orderPart.getLocalId(), cv) : cv;
                cv.put(OrderPart.PART_ID, orderPart.getPart_type_id());
                cv.put(OrderPart.ORDER_UPDATE_TIME, orderPart.getUpd_time());
                cv.put(modifiedField, orderPart.getModified());
                cv.put(localOidField, orderPart.getLocalOid());

                cv.put(Form.FORM_ID, form.id);
                cv.put(Form.FORM_FDATA, form.fdata);
                cv.put(Form.FORM_OID, form.oid);
                cv.put(Form.FORM_STMP, form.stmp);
                cv.put(Form.FORM_TID, form.ftid);
                cv.put(modifiedField, form.getModified());
                cv = (Long) form.getLocalid() != -1 ? putValueInCV(
                        localidField, form.getLocalid(), cv) : cv;
                cv.put(localOidField, form.getLocalOid());*/


                else if (objectTypeList[i] instanceof Form) {

                    Form orderForm = (Form) objectTypeList[i];
                    cv.put(Form.FORM_OID, orderForm.getOid());
                    cv.put(Form.FORM_FDATA,
                            orderForm.getFdata());
					/*cv.put(OrderPart.ORDER_PART_ID,
							orderPart.getOrder_part_id());*/// Vicky do not update the id. Because it may happen the local Id updates the server id
                    cv.put(Form.FORM_TID, orderForm.getFtid());
                    cv.put(Form.FORM_UPD, orderForm.getStmp());
                    String whereclause = Form.FORM_ID + "=" + orderForm.getId() + " or " + localidField + "=" + orderForm.getLocalid();
                    String modifystr = getValueFromTable(context, DBHandler.formTable, DBHandler.modifiedField, whereclause);
                    if (modifystr != null && modifystr.equals(""))
                        modifystr = "update";
                    cv.put(DBHandler.modifiedField, modifystr);
                    cv.put(Form.FORM_KEY_ONLY, orderForm.getFormkeyonly());
                    column = Form.FORM_ID;
                    id = orderForm.getId();
                    whereClause = localidField + "=" + id + " OR ";
                } /*else if (objectTypeList[i] instanceof OrderStatus) {//YD commenting because have to get status list_cal from server TODO

					OrderStatus orderStatus = (OrderStatus) objectTypeList[i];
					cv.put(OrderStatus.ORDER_ID, orderStatus.getId());
					cv.put(OrderStatus.ORDER_STATUS_NAME, orderStatus.getName());
					cv.put(OrderStatus.ORDER_STATUS_VALUE,
							orderStatus.getValue());
					cv.put(OrderStatus.ORDER_STATUS_DTLIST,
							orderStatus.getDtlist());
					cv.put(OrderStatus.ORDER_STATUS_GEO, orderStatus.getGeo());
					column = OrderStatus.ORDER_ID;
					id = orderStatus.getId();
				}*/ else if (objectTypeList[i] instanceof OrderMedia) {
                    OrderMedia ordermedia = (OrderMedia) objectTypeList[i];
                    /*cv.put(OrderMedia.ID, ordermedia.getId());*/// Vicky do not update the id. Because it may happen the local Id updates the server id
                    cv.put(OrderMedia.ORDER_ID, ordermedia.getOrderid());
                    cv.put(OrderMedia.ORDER_MEDIA_TYPE, ordermedia.getMediatype());
                    cv.put(OrderMedia.ORDER_GEO, ordermedia.getGeocode());
                    cv.put(OrderMedia.ORDER_FILE_DESC, ordermedia.getFile_desc());
                    cv.put(OrderMedia.ORDER_FILE_MIME_TYPE, ordermedia.getMimetype());
                    cv.put(OrderMedia.ORDER_FILE_DATA, ordermedia.getData());
                    cv.put(OrderMedia.ORDER_META_PATH, ordermedia.getMetapath());
                    cv.put(OrderMedia.ORDER_META_UPDATE_TIME, ordermedia.getUpd_time());
                    cv.put(OrderMedia.FORM_KEY, ordermedia.getFormKey());
                    cv.put(OrderMedia.FORM_FIELD_ID, ordermedia.getFrmfiledid());
                    column = OrderMedia.ID;
                    id = ordermedia.getId();
                    whereClause = localidField + "=" + id + " OR ";
                } else if (objectTypeList[i] instanceof OrderNotes) {

                    OrderNotes ordernotes = (OrderNotes) objectTypeList[i];
                    cv.put(OrderNotes.ORDER_ID, ordernotes.getId());
                    if (ordernotes.getOrdernote() != null)
                        cv.put(OrderNotes.ORDER_NOTE, ordernotes.getOrdernote());
                    cv.put(DBHandler.modifiedField, ordernotes.getModified());
                    column = OrderMedia.ID;
                    id = ordernotes.getId();
                } else if (objectTypeList[i] instanceof CustomerContact) {
                    CustomerContact cct = (CustomerContact) objectTypeList[i];

                    cv.put(CustomerContact.CONTACT_ID, cct.getId());
                    cv.put(CustomerContact.CUSTOMER_ID, cct.getCustomerid());
                    cv.put(CustomerContact.CONTACT_NAME, cct.getContactname());
                    cv.put(CustomerContact.CONTACT_TEL, cct.getContacttel());
                    cv.put(CustomerContact.CONTACT_TYPE, cct.getContactType());
                    cv.put(CustomerContact.CONTACT_EML, cct.getContactEml());
                    cv.put(DBHandler.modifiedField, cct.getModify());

                    id = cct.getId();
                    column = CustomerContact.CONTACT_ID;
                    whereClause = localidField + "=" + id + " OR ";
                } else if (objectTypeList[i] instanceof Assets) {//YD inserting assets coming in customer xml
                    Assets asset = (Assets) objectTypeList[i];
                    if (asset == null) {
                        continue;
                    }
                    cv.put(Assets.ASSET_ID, asset.getId());
                    cv.put(Assets.ASSET_CID, asset.getCid());
                    cv.put(Assets.ASSET_FTYPE_ID, asset.getFtid());
                    cv.put(Assets.ASSET_FDATA, asset.getFdata());
                    cv.put(Assets.ASSET_GEO, asset.getGeoLoc());
                    cv.put(Assets.ASSET_UPD, asset.getUpd());
                    cv.put(Assets.ASSET_OID, asset.getOid());

                    if (asset.getModify() == null)
                        cv.put(DBHandler.modifiedField, "");
                    else
                        cv.put(DBHandler.modifiedField, asset.getModify());

                    cv.put(localidField, asset.getLocalid());

                    id = asset.getId();
                    column = Assets.ASSET_ID;
                    whereClause = localidField + "=" + id + " OR ";
                } else if (objectTypeList[i] instanceof AssetsType) {//YD inserting assets coming in customer xml
                    AssetsType assetType = (AssetsType) objectTypeList[i];
                    if (assetType == null) {
                        continue;
                    }
                    cv.put(AssetsType.ASSETS_TYPE_ID, assetType.getId());
                    cv.put(AssetsType.ASSETS_TYPE_NAME, assetType.getName());
                    cv.put(AssetsType.ASSETS_TYPE_XID, assetType.getXid());
                    cv.put(AssetsType.ASSETS_TYPE_UPD, assetType.getUpd());

                    column = AssetsType.ASSETS_TYPE_ID;
                    id = assetType.getId();
                } else if (objectTypeList[i] instanceof Site) {

                    Site site = (Site) objectTypeList[i];

                    cv = site.getId() != -1 ? putValueInCV(Site.SITE_ID, site.getId(), cv) : cv;
                    cv = site.getCid() != -1 && site.getCid() != 0 ? putValueInCV(Site.SITE_CID, site.getCid(), cv) : cv;
                    cv = site.getNm() != null ? putValueInCV(Site.SITE_NAME, site.getNm(), cv) : cv;
                    cv = site.getAdr() != null ? putValueInCV(Site.SITE_ADDRESS, site.getAdr(), cv) : cv;
                    cv = site.getAdr2() != null ? putValueInCV(Site.SITE_ADR2, site.getAdr2(), cv) : cv;
                    cv = site.getUpd() != -1 && site.getUpd() != 0 ? putValueInCV(Site.SITE_UPD, site.getUpd(), cv) : cv;
                    cv = site.getDetail() != null ? putValueInCV(Site.SITE_DTL, site.getDetail(), cv) : cv;
                    cv = site.getModify() != null ? putValueInCV(DBHandler.modifiedField, site.getModify(), cv) : cv;
                    cv = site.getGeo() != null ? putValueInCV(Site.SITE_GEO, site.getGeo(), cv) : cv;

                    column = OrderMedia.ID;
                    id = site.getId();
                } else if (objectTypeList[i] instanceof SaveShiftReq) {
                    //SaveShiftReq shiftReq  = ((SaveShiftReq)object.getReqDataObject());
                    SaveShiftReq shift = (SaveShiftReq) objectTypeList[i];

                    cv = shift.getLid() != 0 ? putValueInCV(Shifts.SHIFT_LID, shift.getLid(), cv) : cv;
                    cv = shift.getTmslot() != null ? putValueInCV(Shifts.SHIFT_TMSLOT, shift.getTmslot(), cv) : cv;
                    cv = shift.getBrkslot() != null ? putValueInCV(Shifts.SHIFT_BRKSLOT, shift.getBrkslot(), cv) : cv;
                    cv = shift.getNm() != null ? putValueInCV(Shifts.SHIFT_NM, shift.getNm(), cv) : cv;
                    cv = shift.getAddress() != null ? putValueInCV(Shifts.SHIFT_ADR, shift.getAddress(), cv) : cv;
                    cv = shift.getTerri() != null ? putValueInCV(Shifts.SHIFT_TERRI, shift.getTerri(), cv) : cv;

                    String where = Shifts.SHIFT_ID + "=" + shift.getId() + " OR " + localidField + "=" + shift.getId();
                    String mofy = getValuebyWhere(context, DBHandler.shiftTable, modifiedField, where);
                    if (mofy != null && !mofy.equals(DBHandler.modifiedNew)) {
                        cv.put(DBHandler.modifiedField, DBHandler.updated);
                    }


                    column = Shifts.SHIFT_ID;
                    id = shift.getId();
                    whereClause = " " + localidField + "=" + id + " OR ";
                }

                whereClause = " " + whereClause + column + "=" + id;
                long result = 0;
                switch (queryType) {
                    case QUERY_FOR_ORIG:
                        Log.i(Utilities.TAG, "where clause : " + whereClause + " : cv : " + cv + " : client : " + table);
                        result = db.update(table, cv, whereClause, null);
                        break;

                    case QUERY_FOR_TEMP:
                        Log.i(Utilities.TAG, "where clause : " + whereClause + " : cv : " + cv + " : client : " + tempTablePrefix + table);
                        result = db.update(tempTablePrefix + table, cv, whereClause, null);
                        break;
                }
                if (result > 0)
                    totalRecordsUpdated++;
            }
            Log.i(Utilities.TAG, totalRecordsUpdated + " records updated.");
            return (int) totalRecordsUpdated;
        } catch (Exception e) {
            e.printStackTrace();
            return DBEngine.DB_ERROR;
        }
    }

    private static ContentValues putValueInCV(String key, Object value,
                                              ContentValues cv) {
        if (value instanceof String) {
            cv.put(key, value.toString());
        } else if (value instanceof Long) {
            cv.put(key, Long.parseLong(value.toString()));
        }
        return cv;
    }

    public static int delete(Context context, String tablename,
                             String columnname, String ids) {
        return delete(context, tablename, columnname, ids, QUERY_FOR_ORIG);
    }

    public static int delete(Context context, String tablename,
                             String columnname, String ids, int queryType) {
        try {

            Utilities.log(context, "DBHandler delete called.");
            long id;
            String tempIds[] = null;
            if (ids != null && ids.contains("|")) {
                ids = ids.replace("|", ",");
                tempIds = ids.split(",");
                Log.i(Utilities.TAG, ids + " : " + tempIds.length);
            } else {
                String temps[] = {ids};
                tempIds = temps;
            }
            int idcounter = 0;
            int tidcounter = 0;
            String medianame = "";
            while (tidcounter < tempIds.length) {
                id = Long.parseLong(tempIds[tidcounter]);
                // Decreasing counts in order table in case of ordertask and
                // parttypelist.

                /***********YD*s*************/
                if (tablename.equals(DBHandler.orderTypeListTable) ||
                        tablename.equals(DBHandler.partTypeListTable) ||
//                        tablename.equals(DBHandler.taskTypeTable) ||
                        tablename.equals(DBHandler.resTable) ||
                        tablename.equals(DBHandler.orderListTable) ||
                        tablename.equals(DBHandler.siteTypeTable) ||
                        //tablename.equals(DBHandler.siteTable)||  //YD earlier commented dont know why but saving modified field as deleted in db
                        tablename.equals(DBHandler.assestTypeTable) ||
                        tablename.equals(DBHandler.customerListTable)) {
                    idcounter += db
                            .delete(tablename, columnname + " = " + id, null);
                    return idcounter;
                }

                /***********YD*e*************/
                if (tablename.equals(DBHandler.orderTaskTable)
                        || tablename.equals(DBHandler.orderTaskTableOld)  // By Mandeep for OrderTaskOld
                        || tablename.equals(DBHandler.orderPartTable)
                        || tablename.equals(DBHandler.formTable)
                        || tablename.equals(DBHandler.orderFileMeta)) {
                    decreaseCountInOrder(context, tablename, columnname, id);
                }

                if (tablename.equals(DBHandler.resTable) || tablename.equals(tempTablePrefix + DBHandler.resTable)) {
                    handleWorkerInOrder(context, tablename, id);
                }
                if (tablename.equals(DBHandler.orderFileNotes) || tablename.equals(tempTablePrefix + DBHandler.orderFileNotes)) {
                    //idcounter += db.updateTable(tablename, columnname + " = " + id, null);
                } else {

                    if (tablename.equals(DBHandler.shiftTable)) {
                        String whereClause = columnname + "=" + id + " OR " + localidField + "= " + id;
                        String mofy = getValuebyWhere(context, tablename, modifiedField, whereClause);
                        if (mofy != null && mofy.equals(DBHandler.modifiedNew)) {
                            db.delete(tablename, columnname + " = " + id, null);
                            return 1;
                        }

                    }


                    queryType = DBHandler.QUERY_FOR_ORIG;
                    String whereClause = columnname + "=" + id + " OR " + localidField + "= " + id;
                    String modifystr = getValueFromTable(context, tablename, DBHandler.modifiedField, whereClause);
                    if (tablename.equals(DBHandler.orderFileMeta))
                        medianame = getValueFromTable(context, tablename, OrderMedia.ORDER_META_PATH, whereClause);
                    if (modifystr != null && !modifystr.equals("")) {
                        idcounter = delete(context, tablename, whereClause);
                    } else {
                        ContentValues cv = new ContentValues();
                        cv.put(DBHandler.modifiedField, DBHandler.deleted);
                        idcounter += DBHandler.updateTable(tablename, cv, whereClause, null);
                    }
                }
                if (tablename.equals(DBHandler.orderFileMeta)) {
                    Utilities.deleteFile(context, medianame);
                }

                tidcounter++;
            }
            Utilities.log(context, "" + idcounter + " records deleted.");
            return idcounter;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static void handleWorkerInOrder(Context context, String tablename,
                                            long id) {
        handleWorkerInOrder(context, tablename, id, QUERY_FOR_ORIG);
    }

    private static void handleWorkerInOrder(Context context, String tablename,
                                            long id, int queryType) {
        tablename = DBHandler.orderListTable;
        Utilities.log(context, "inside handleWorkerInOrder();");
        switch (queryType) {
            case QUERY_FOR_ORIG:
                break;
            case QUERY_FOR_TEMP:
                tablename = tempTablePrefix + tablename;
                break;
        }
        String selectQuery = "SELECT id,rid FROM " + tablename;
        Utilities.log(context, "Select query : " + selectQuery);
        Cursor cursor = null;
        ContentValues cv = new ContentValues();
        try {
            cursor = db.rawQuery(selectQuery, null);
            long oid = 0;
            String rid = null;
            String tempRid = null;
            if (cursor.moveToFirst()) {
                do {
                    oid = cursor.getLong(0);
                    rid = cursor.getString(1);
                    String ridString = String.valueOf(id);
                    if (rid.contains("|")) {
							/*if (!rid.contains("|")) {
								tempRid = rid;
							} else */

                        if (rid.contains(id + "|")) {
                            tempRid = rid.replace(id + "|", "");
                        } else {
                            if (rid.contains("|" + String.valueOf(id))) {
                                tempRid = rid.replace("|" + (String.valueOf(id)), "");
                            }
                        }

								/*tempRid = rid.substring(
										rid.indexOf(ridString),
										rid.indexOf(ridString)
												+ (ridString).length());*/
								/*if ((rid.indexOf(ridString) == 0))
									tempRid = rid.replace(ridString + "|", "");
								else {
									tempRid = rid.replace("|" + ridString, "");
								}*/
                    } else {
                        tempRid = null;
                    }
					/*else if (!rid.contains(ridString)) {
						tempRid = ridString;
						if ((rid.indexOf(ridString) == 0))
							tempRid = rid.replace(ridString + "|", "");
						else if ((rid.indexOf(ridString) > 0)) {
							tempRid = rid.replace("|" + ridString, "");
						}
					}*/

                    if (tempRid != null) {
                        cv.put(Order.ORDER_STATUS_PRIMARY_WORKER_ID, tempRid);
                        String whereclause = Order.ORDER_ID + "=" + oid;
                        Log.i(Utilities.TAG, "Order: " + "Updating " + tablename
                                + " with " + whereclause);
                        long result = db.update(DBHandler.orderListTable, cv,
                                whereclause, null);
                        //if (result != -1)
                        //	Logger.v("", "");
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void decreaseCountInOrder(Context context, String tablename,
                                             String column, long id) {
        decreaseCountInOrder(context, tablename, column, id, QUERY_FOR_ORIG);
    }

    private static void decreaseCountInOrder(Context context, String tablename,
                                             String column, long id, int queryType) {

        switch (queryType) {
            case QUERY_FOR_ORIG:
                break;
            case QUERY_FOR_TEMP:
                tablename = tempTablePrefix + tablename;
                break;
        }

        Utilities.log(context, "inside decreaseCountInOrder();");
        String selectQuery;
        if (tablename.equals(DBHandler.orderFileMeta)) {
            selectQuery = "Select oid,tid FROM " + tablename + " WHERE "
                    + column + " = " + id + " OR " + localidField + "= " + id;
        } else
            selectQuery = "Select oid FROM " + tablename + " WHERE "
                    + column + " = " + id + " OR " + localidField + "= " + id;//YD must check again
        Utilities.log(context, "Select query : " + selectQuery);
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(selectQuery, null);
            long oid = 0;
            long tid = 0;
            if (cursor.moveToFirst()) {
                oid = cursor.getLong(0);
                if (tablename.equals(DBHandler.orderFileMeta))
                    tid = cursor.getLong(1);
            }
            cursor.close();
            if (tablename.equals(DBHandler.orderTaskTable))
                updateOrders(context, Order.ORDER_CUSTOMER_SERVICE_COUNT, oid,
                        (int) -1, DBHandler.orderListTable, tid);
            else if (tablename.equals(DBHandler.orderFileMeta))
                updateOrders(context, Order.ORDER_CUSTOMER_MEDIA_COUNT, oid,
                        (int) -1, DBHandler.orderListTable, tid);
            else if (tablename.equals(DBHandler.formTable))
                updateOrders(context, Order.ORDER_CUSTOMER_FORM_COUNT, oid,
                        (int) -1, DBHandler.orderListTable, tid);
            else
                updateOrders(context, Order.ORDER_CUSTOMER_PART_COUNT, oid,
                        (int) -1, DBHandler.orderListTable, tid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap<Long, Object> getOrderNotes(Context context, long id,
                                                      String actiongetOrderNote) {
        return getOrderNotes(context, id, actiongetOrderNote, QUERY_FOR_ORIG);
    }

    public static HashMap<Long, Object> getOrderNotes(Context context, long id,//yash
                                                      String actiongetOrderNote, int queryType) { // Currently not using the argument but
        // may be useful in future.
        Utilities.log(context, "DBHandler getOrderNotes called.");
        String query = null;

        if (id == 0) {
            query = "SELECT * FROM  " + orderFileNotes;
        } else {
            query = "SELECT *" + " FROM "
                    + orderFileNotes + " WHERE " + OrderNotes.ORDER_ID + " = " + id;
        }
        Log.i(Utilities.TAG, "select query : " + query);
        return select(context, query, actiongetOrderNote);
    }

    public static HashMap<Long, Object> checkAndGetOrders(Context context,//yash
                                                          String actiongetorder, String orderid) {
        return checkAndGetOrders(context, actiongetorder, orderid, QUERY_FOR_ORIG);
    }

    public static HashMap<Long, Object> checkAndGetOrders(Context context,//yash
                                                          String actiongetorder, String orderid, int queryType) {
        Utilities.log(context, "DBHandler checkAndGetOrders called.");
        String query = null;

        query = "SELECT * FROM " + orderListTable + " WHERE "
                + Order.ORDER_ID + "=" + orderid + " OR " + localidField + "= " + orderid;

        Log.i(Utilities.TAG, "checkAndGetOrders query : " + query);
        return select(context, query, actiongetorder);
    }

    public static HashMap<Long, Object> checkAndGetOrders(Context context,//yash
                                                          String actiongetorder, String from, String to) {
        return checkAndGetOrders(context, actiongetorder, from, to, QUERY_FOR_ORIG);
    }

    public static HashMap<Long, Object> checkAndGetOrders(Context context,//yash
                                                          String actiongetorder, String from, String to, int queryType) {
        Utilities.log(context, "DBHandler checkAndGetOrders called.");
        String query = null;
        if (queryType == QUERY_FOR_ORIG) {
            Log.i( Utilities.TAG,
                    "Saving orders in original database");
            query = "SELECT * FROM " + orderListTable + " WHERE "
                    + Order.ORDER_START_DATE + " BETWEEN '" + from + "' AND '" + to
                    + "' ORDER BY " + Order.ORDER_START_TIME + " asc";
        } else {
            Log.i( Utilities.TAG,
                    "Saving order in temp database.");
            query = "SELECT * FROM " + tempTablePrefix + orderListTable + " WHERE "
                    + Order.ORDER_START_DATE + " BETWEEN '" + from + "' AND '" + to
                    + "' ORDER BY " + Order.ORDER_START_TIME + " asc";
        }
        Log.i(Utilities.TAG, "checkAndGetOrders query : " + query);
        return select(context, query, actiongetorder);
    }

    public static HashMap<Long, Object> checkAndGetOrdersfromOffline(Context context,//yash
                                                                     String action) {
        Utilities.log(context, "DBHandler checkAndGetOrders called.");

        String query = "SELECT * FROM " + orderListTable + " ORDER BY " + Order.ORDER_SORTINDEX + " asc, " + Order.ORDER_START_TIME + " asc";

        Log.i( Utilities.TAG, "checkAndGetOrders query : " + query);
        return select(context, query, action);
    }

    public static HashMap<Long, Object> checkAndGetOrdersfromOfflineInMap(Context context,//yash
                                                                          String action) {
        Utilities.log(context, "DBHandler checkAndGetOrders called.");

        String query = "SELECT * FROM " + orderListTable + " ORDER BY " + Order.ORDER_SORTINDEX + " asc, " + Order.ORDER_START_TIME + " asc";

        Log.i(Utilities.TAG, "checkAndGetOrders query : " + query);
        return select(context, query, action);
    }

    public static HashMap<Long, Object> checkAndGetCusType(Context context,//yash
                                                           String actiongetcustype) {
        return checkAndGetCusType(context, actiongetcustype, QUERY_FOR_ORIG);
    }

    public static HashMap<Long, Object> checkAndGetSiteType(Context context,//yash
                                                            String actiongetsitetype) {
        return checkAndGetSiteType(context, actiongetsitetype, QUERY_FOR_ORIG);
    }

    public static HashMap<Long, Object> checkAndGetCusType(Context context,//yash
                                                           String actiongetcustype, int queryType) { // Currently not using the argument but
        // may be useful in future.
        Utilities.log(context, "DBHandler checkAndGetCusType called.");
        String query = null;
        if (queryType == QUERY_FOR_ORIG) {
            query = "SELECT * FROM " + customerTypeTable + " ORDER BY "
                    + CustomerType.CUSTOMER_TYPE_NAME + " COLLATE NOCASE;";
        } else {
            query = "SELECT * FROM " + customerTypeTable + " ORDER BY "


                    + tempTablePrefix + CustomerType.CUSTOMER_TYPE_NAME + " COLLATE NOCASE;";
        }
        return select(context, query, actiongetcustype);
    }

    public static HashMap<Long, Object> checkAndGetSiteType(Context context,//yash
                                                            String actiongetsitetype, int queryType) { // Currently not using the argument but
        // may be useful in future.
        Utilities.log(context, "DBHandler checkAndGetSiteType called.");
        String query = null;
        if (queryType == QUERY_FOR_ORIG) {
            query = "SELECT * FROM " + siteTypeTable + " ORDER BY "
                    + SiteType.SITE_TYPE_NAME + " COLLATE NOCASE;";
        } else {
            query = "SELECT * FROM " + customerTypeTable + " ORDER BY "
                    + tempTablePrefix + CustomerType.CUSTOMER_TYPE_NAME + " COLLATE NOCASE;";
        }
        return select(context, query, actiongetsitetype);
    }

    public static HashMap<Long, Object> checkAndGetSiteType(Context context,String actiongetsitetype, String cid) { // Currently not using the argument but
        // may be useful in future.
        Utilities.log(context, "DBHandler checkAndGetSiteType called.");
        String query = null;
        if (cid!=null || cid.equalsIgnoreCase("0")) {
            query = "SELECT * FROM " + siteTypeTable + " ORDER BY " + SiteType.SITE_TYPE_NAME + " COLLATE NOCASE;";
        } else {
            query = "SELECT * FROM " + customerTypeTable + " ORDER BY "
                    + tempTablePrefix + CustomerType.CUSTOMER_TYPE_NAME + " COLLATE NOCASE;";
        }

        if (cid==null || cid.equalsIgnoreCase("0")) {
            query = "SELECT * FROM  " + siteTypeTable + " WHERE " + DBHandler.modifiedField + "!= '" + DBHandler.deleted + "'";//YD TODO check added where clause at time of deleting site req addition
        } else {
            //        query = "SELECT * FROM " + siteTable + " WHERE cid=" + cid + " OR " + DBHandler.modifiedField + " != '" + DBHandler.deleted + "'";
            query = "SELECT * FROM " + siteTypeTable + " WHERE tid=2 AND id=" + cid + "";

            Log.d("MAG",query);
            Log.d("MAB",String.valueOf(cid));
        }

        return select(context, query, actiongetsitetype);
    }

    public static HashMap<Long, Object> checkAndGetAssetType(Context context,
                                                             String actiongetAssettype) {
        return checkAndGetAssetType(context, actiongetAssettype, QUERY_FOR_ORIG);
    }

    private static HashMap<Long, Object> checkAndGetAssetType(Context context, String actiongetAssettype, int queryType) {
        Utilities.log(context, "DBHandler checkAndGetOrderType called.");
        String query = null;
        if (queryType == QUERY_FOR_ORIG) {
            query = "SELECT * FROM " + assestTypeTable + " ORDER BY "
                    + AssetsType.ASSETS_TYPE_NAME + " COLLATE NOCASE;";
        }
        return select(context, query, actiongetAssettype);
    }


    public static HashMap<Long, Object> checkAndGetOrderType(Context context,
                                                             String actiongetordertype) {
        return checkAndGetOrderType(context, actiongetordertype, QUERY_FOR_ORIG);
    }

    public static HashMap<Long, Object> checkAndGetOrderType(Context context,//yash
                                                             String actiongetordertype, int queryType) { // Currently not using the argument but
        // may be useful in future.
        Utilities.log(context, "DBHandler checkAndGetOrderType called.");
        String query = null;
        if (queryType == QUERY_FOR_ORIG) {
            query = "SELECT * FROM " + orderTypeListTable + " ORDER BY "
                    + OrderTypeList.ORDER_TYPE_NAME + " COLLATE NOCASE;";
        } else {
            query = "SELECT * FROM " + tempTablePrefix + orderTypeListTable + " ORDER BY "
                    + OrderTypeList.ORDER_TYPE_NAME + " COLLATE NOCASE;";
        }
        return select(context, query, actiongetordertype);
    }

    public static HashMap<Long, Object> checkAndGetOrderStatusType(Context context,//yash
                                                                   String actiongetorderStattype, int queryType) { // Currently not using the argument but
        // may be useful in future.
        Utilities.log(context, "DBHandler checkAndGetOrderStatusType called.");
        String query = null;
        if (queryType == QUERY_FOR_ORIG) {
            query = "SELECT * FROM " + orderStatusTable + " ORDER BY "
                    + OrderStatus.STATUS_TYPE_NM + " COLLATE NOCASE;";
        } else {
            query = "SELECT * FROM " + tempTablePrefix + orderStatusTable + " ORDER BY "
                    + OrderTypeList.ORDER_TYPE_NAME + " COLLATE NOCASE;";
        }
        return select(context, query, actiongetorderStattype);
    }

    public static HashMap<Long, Object> checkAndGetClientSite(Context context, String actiongetclientsite) {
        return checkAndGetClientSite(context, actiongetclientsite, QUERY_FOR_ORIG);
    }

    public static HashMap<Long, Object> checkAndGetClientSite(Context context, String actiongetclientsite, int queryType) { // Currently not using the argument
        // but may be useful in future.
        Utilities.log(context, "DBHandler checkAndGetClientSite called.");
        String query = null;
        if (queryType == QUERY_FOR_ORIG) {
            query = "SELECT * FROM " + clientSiteTable;
        } else {
            query = "SELECT * FROM " + tempTablePrefix + clientSiteTable;
        }
        return select(context, query, actiongetclientsite);
    }

    public static HashMap<Long, Object> checkAndGetSite(Context context, String actionGetSite,//yash
                                                        long cid) {
        return checkAndGetSite(context, actionGetSite, cid, QUERY_FOR_ORIG);
    }

    public static HashMap<Long, Object> checkAndGetSite(Context context, String actionGetSite,//yash
                                                        long cid, int queryType) {
        Utilities.log(context, "DBHandler checkAndGetSite called.");
        String query = null;
        if (cid == 0) {
            query = "SELECT * FROM  " + siteTable + " WHERE " + DBHandler.modifiedField + "!= '" + DBHandler.deleted + "'";//YD TODO check added where clause at time of deleting site req addition
        } else {
    //        query = "SELECT * FROM " + siteTable + " WHERE cid=" + cid + " OR " + DBHandler.modifiedField + " != '" + DBHandler.deleted + "'";
            query = "SELECT * FROM " + siteTable + " WHERE cid=" + cid + "";

            Log.d("MAG",query);
            Log.d("MAB",String.valueOf(cid));
        }
        return select(context, query, actionGetSite);
    }


    public static HashMap<Long, Object> getOrderMedia(Context context, long id,//yash
                                                      String actiongetOrderMeta) {
        return getOrderMedia(context, id, actiongetOrderMeta, QUERY_FOR_ORIG);
    }

    public static HashMap<Long, Object> getOrderMedia(Context context, long id,//yash
                                                      String actiongetOrderMeta, int queryType) { // Currently not using the argument but
        // may be useful in future.
        Utilities.log(context, "DBHandler checkAndGetClientSite called.");
        String query = null;
        if (queryType == QUERY_FOR_ORIG) {
            query = "SELECT * FROM " + orderFileMeta + " WHERE "
                    + OrderMedia.ORDER_ID + " = " + id + " ORDER BY " + OrderTask.ORDER_SERV_UPDATE_TIME + " desc";
        } else {
            query = "SELECT * FROM " + tempTablePrefix + orderFileMeta + " WHERE "
                    + OrderMedia.ORDER_ID + " = " + id + " ORDER BY " + OrderTask.ORDER_SERV_UPDATE_TIME + " desc";
        }
        return select(context, query, actiongetOrderMeta);
    }

    public static HashMap<Long, Object> getOrderMediaforoffline(Context context, String actionGetMedia,//YD
                                                                String oid, int queryType) { // Currently not using the argument but
        // may be useful in future.
        Utilities.log(context, "DBHandler checkAndGetClientSite called.");
        String query = null;

		/*//YD getting oid which is primary key in table
		String WhereClause = "id ="+ oid +" OR localid ="+oid;
		String Oid = DBHandler.getServerOrderId(DBHandler.orderListTable, Order.ORDER_ID,WhereClause);*/

   //     query = "SELECT * FROM " + orderFileMeta + " WHERE (" + OrderMedia.ORDER_ID + " = " + oid + " OR " + localOidField + " = " + oid + ") AND " + DBHandler.modifiedField + " != '" + DBHandler.deleted + "' ORDER BY " + OrderTask.ORDER_SERV_UPDATE_TIME + " desc";

   //     query = "SELECT * FROM " + orderFileMeta + " WHERE (" + OrderMedia.ORDER_ID + " = " + oid + " OR " + localOidField + " = " + oid + ") AND " + OrderMedia.FORM_KEY + " = 'null'";

      // query = "SELECT * FROM " + orderFileMeta + " WHERE (" + OrderMedia.ORDER_ID + " = " + oid + " OR " + localOidField + " = " + oid + ") AND " + DBHandler.modifiedField + " != '" + DBHandler.deleted + "' ORDER BY " + OrderTask.ORDER_SERV_UPDATE_TIME + " desc";

    //getting all rows based on order and independent of frmkey

    query = "SELECT * FROM " + orderFileMeta + " WHERE (" + OrderMedia.ORDER_ID + " = " + oid + " OR " + localOidField + " = " + oid + ") AND (" + OrderMedia.FORM_KEY + " = '' OR " + OrderMedia.FORM_KEY + " IS NULL OR "+ OrderMedia.FORM_KEY+ " = 'null' "+")  AND " + DBHandler.modifiedField + " != '" + DBHandler.deleted + "' ORDER BY " + OrderTask.ORDER_SERV_UPDATE_TIME + " desc";

    //   query = "SELECT * FROM " + orderFileMeta + " WHERE (" + OrderMedia.ORDER_ID + " = " + oid + " OR " + localOidField + " = " + oid + ") AND " + DBHandler.modifiedField + " != '" + DBHandler.deleted + "' ORDER BY " + OrderTask.ORDER_SERV_UPDATE_TIME + " desc";
        return select(context, query, actionGetMedia);

    }

    public static HashMap<Long, Object> getOrderMediaformforoffline(Context context, String actionGetMedia,//RJ
                                                                String oid, String frmkey, int queryType) { // Currently not using the argument but
        // may be useful in future.
        Utilities.log(context, "DBHandler checkAndGetClientSite called.");
        String query = null;

		/*//YD getting oid which is primary key in table
		String WhereClause = "id ="+ oid +" OR localid ="+oid;
		String Oid = DBHandler.getServerOrderId(DBHandler.orderListTable, Order.ORDER_ID,WhereClause);*/

       query = "SELECT * FROM " + orderFileMeta + " WHERE (" + OrderMedia.ORDER_ID + " = " + oid + " AND " + OrderMedia.FORM_KEY + " = '" + frmkey + "'" + ") AND " + DBHandler.modifiedField + " != '" + DBHandler.deleted + "' ORDER BY " + OrderTask.ORDER_SERV_UPDATE_TIME + " asc";

        return select(context, query, actionGetMedia);
    }


    public static MediaCountModel getOrderMediaCount(Context context, long id) {
        return getOrderMediaCount(context, id, QUERY_FOR_ORIG);
    }

    public static MediaCountModel getOrderMediaCount(Context context, long id, int queryType) { // Currently not using the argument but
        // may be useful in future.
        MediaCountModel mediaCountModel = null;
        Utilities.log(context, "DBHandler checkAndGetClientSite called.");
        int count = 0;
        try {
            Utilities.log(context, "DBHandler checkAndGetClientSite called.");
            String query = null;
          //  if (queryType == QUERY_FOR_ORIG) {
            //based on order id onlyu ,independent of frmlkey
                query = "SELECT * FROM " + orderFileMeta + " WHERE " + OrderMedia.ORDER_ID + " = " + id + " AND (" + OrderMedia.FORM_KEY + " = '' OR " + OrderMedia.FORM_KEY + " IS NULL OR "+ OrderMedia.FORM_KEY+ " = 'null') ";
                //query = "SELECT * FROM " + orderFileMeta + " WHERE " + OrderMedia.ORDER_ID + " = " + id ;

            /*} else {
                query = "SELECT * FROM " + tempTablePrefix + orderFileMeta + " WHERE " + OrderMedia.ORDER_ID + " = " + id;
            }*/
            Cursor cursor = db.rawQuery(query, null);
            count = cursor.getCount();
            if(count>0) {
                mediaCountModel = getMediaMap(cursor);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaCountModel;
    }

    public static MediaCountModel getMediaMap(Cursor cursor){

        String metaMediaImage = "1";
        String metaMediaSignature = "2";
        String metaMediaAudio = "3";
        String metaMediaFile = "4";

        Integer imgCount = 0, sigCount = 0, audCount = 0, fileCount = 0;

        final int idIndex = cursor.getColumnIndex("tid");

        try {

            if (!cursor.moveToFirst()) {
                // new ArrayList<>();
            }

            do {
                // Read the values of a row in the table using the indexes acquired above
                final String mime = cursor.getString(idIndex);
                if(mime.equalsIgnoreCase(metaMediaImage)){
                    imgCount++;
                }else if(mime.equalsIgnoreCase(metaMediaSignature)){
                    sigCount++;
                }else if(mime.equalsIgnoreCase(metaMediaAudio)){
                    audCount++;
                }else if(mime.equalsIgnoreCase(metaMediaFile)){
                    fileCount++;
                }


            } while (cursor.moveToNext());

        }catch (Exception e){
            e.printStackTrace();
        }

        return new MediaCountModel(imgCount, sigCount, audCount, fileCount);

    }

    public static HashMap<Long, Object> checkAndGetRes(Context context, String actiongetres) {//yash
        return checkAndGetRes(context, actiongetres, QUERY_FOR_ORIG);
    }

    public static HashMap<Long, Object> checkAndGetRes(Context context, String actiongetres, int queryType) {//yash  // Currently
        // not
        // using
        // the
        // argument
        // but
        // may
        // be
        // useful
        // in
        // future.
        Utilities.log(context, "DBHandler checkAndGetRes called.");
        String query = null;
        if (queryType == QUERY_FOR_ORIG) {
            query = "SELECT * FROM " + resTable + " ORDER BY "
                    + Worker.WORKER_NAME + " COLLATE NOCASE;";
        } else {
            query = "SELECT * FROM " + tempTablePrefix + resTable + " ORDER BY "
                    + Worker.WORKER_NAME + " COLLATE NOCASE;";
        }
        return select(context, query, actiongetres);
    }

    public static HashMap<Long, Object> checkAndGetServiceType(Context context,//yash
                                                               String actiongetservicetype) {
        return checkAndGetServiceType(context, actiongetservicetype, QUERY_FOR_ORIG);
    }

    public static HashMap<Long, Object> checkAndGetServiceType(Context context,//yash
                                                               String actiongetservicetype, int queryType) { // Currently not using the argument
        // but may be useful in future.
        Utilities.log(context, "DBHandler checkAndGetServiceType called.");
        String query = null;
        /*if (queryType == QUERY_FOR_ORIG) {
            query = "SELECT * FROM " + taskTypeTable + " ORDER BY "
                    + ServiceType.SERVICE_TYPE_NAME + " COLLATE NOCASE;";
        } else {
            query = "SELECT * FROM " + tempTablePrefix + taskTypeTable + " ORDER BY "
                    + ServiceType.SERVICE_TYPE_NAME + " COLLATE NOCASE;";
        }*/
        return select(context, query, actiongetservicetype);
    }

    public static HashMap<Long, Object> checkAndGetPartType(Context context,//yash
                                                            String actiongetparttype) {
        return checkAndGetPartType(context, actiongetparttype, QUERY_FOR_ORIG);
    }

    public static HashMap<Long, Object> checkAndGetPartType(Context context,//yash
                                                            String actiongetparttype, int queryType) { // Currently not using the argument but
        // may be useful in future.
        Utilities.log(context, "DBHandler checkAndGetPartType called.");
        String query = null;
        if (queryType == QUERY_FOR_ORIG) {
            query = "SELECT * FROM " + partTypeListTable + " ORDER BY "
                    + Parts.PART_TYPE_NAME + " COLLATE NOCASE;";
        } else {
            query = "SELECT * FROM " + tempTablePrefix + partTypeListTable + " ORDER BY "
                    + Parts.PART_TYPE_NAME + " COLLATE NOCASE;";
        }
        return select(context, query, actiongetparttype);
    }

    public static HashMap<Long, Object> checkAndGetCustList(Context context,//yash
                                                            String actiongetcustlist) {
        return checkAndGetCustList(context, actiongetcustlist, QUERY_FOR_ORIG);
    }

    public static HashMap<Long, Object> checkAndGetCustList(Context context,//yash
                                                            String actiongetcustlist, int queryType) { // Currently not using the argument but
        // may be useful in future.
        Utilities.log(context, "DBHandler checkAndGetCustList called.");
        String query = null;
        if (queryType == QUERY_FOR_ORIG) {
            query = "SELECT * FROM " + customerListTable + " ORDER BY "
                    + Customer.CUSTOMER_NAME + " COLLATE NOCASE;";
        } else {
            query = "SELECT * FROM " + tempTablePrefix + customerListTable + " ORDER BY "
                    + Customer.CUSTOMER_NAME + " COLLATE NOCASE;";
        }
        return select(context, query, actiongetcustlist);
    }

    public static HashMap<Long, Object> checkAndGetOrderTask(Context context,//yash
                                                             String actionGetOrderTask, String oid) {
        return checkAndGetOrderTask(context, actionGetOrderTask, oid, QUERY_FOR_ORIG);
    }

    public static HashMap<Long, Object> checkAndGetOrderTask(Context context,//yash
                                                             String actionGetOrderTask, String oid, int queryType) {
        Utilities.log(context, "DBHandler checkAndGetOrderTask called.");
        String query = null;
        if (!oid.equals("0")) {
            query = "SELECT * FROM " + orderTaskTable + " WHERE (oid = "
                    + oid + " OR " + localOidField + " = " + oid + ") AND " + DBHandler.modifiedField + "!= '" + DBHandler.deleted + "' ORDER BY " + OrderTask.ORDER_SERV_UPDATE_TIME + " desc";
        } else {
            query = "SELECT * FROM " + orderTaskTable + " WHERE " +
                    DBHandler.modifiedField + "!= '" + DBHandler.deleted + "' ORDER BY " + OrderTask.ORDER_SERV_UPDATE_TIME + " desc";
        }

        return select(context, query, actionGetOrderTask);
    }

    public static HashMap<Long, Object> checkAndGetCustContact(Context context,//yash
                                                               String actionGetCustContact, String cid, int queryType) {
        Utilities.log(context, "DBHandler checkAndGetCustContact called.");
        String query = null;
        if (!cid.equals("0")) {
            query = "SELECT * FROM " + customercontactTable + " WHERE (cid = "
                    + cid + ") AND " + DBHandler.modifiedField + "!= '" + DBHandler.deleted + "'";
        } else {
            query = "SELECT * FROM " + customercontactTable + " WHERE " +
                    DBHandler.modifiedField + "!= '" + DBHandler.deleted + "'";
        }

        return select(context, query, actionGetCustContact);
    }

    public static HashMap<Long, Object> checkAndGetCustAssets(Context context,//yash
                                                              String actionGetCustContact, String cid, int queryType) {
        Utilities.log(context, "DBHandler checkAndGetCustContact called.");
        String query = null;
        if (!cid.equals("0")) {
            query = "SELECT * FROM " + assestTable + " WHERE (cid = "
                    + cid + ") AND " + DBHandler.modifiedField + "!= '" + DBHandler.deleted + "'";
        } else {
            query = "SELECT * FROM " + assestTable + " WHERE " +
                    DBHandler.modifiedField + "!= '" + DBHandler.deleted + "'";
        }

        return select(context, query, actionGetCustContact);
    }

    // By Mandeep for OrderTaskOld
   /* public static HashMap<Long, Object> checkAndGetOrderTaskOld(Context context, String actionOrderTask, String oid) {
        return checkAndGetOrderTaskOld(context, actionOrderTask, oid, QUERY_FOR_ORIG);
    }

    public static HashMap<Long, Object> checkAndGetOrderTaskOld(Context context, String actionOrderTask, String oid, int queryType) {
        Utilities.log(context, "DBHandler checkAndGetOrderTaskOld called.");
        String query = null;
        if (!oid.equals("0")) {
            query = "SELECT * FROM " + orderTaskTableOld + " WHERE oid = "
                    + oid + " AND " + DBHandler.modifiedField + "!= '" + DBHandler.deleted + "' ORDER BY " + OrderTaskOld.ORDER_SERV_UPDATE_TIME + " desc";
        } else {
            query = "SELECT * FROM " + orderTaskTableOld + " WHERE " +
                    DBHandler.modifiedField + "!= '" + DBHandler.deleted + "' ORDER BY " + OrderTaskOld.ORDER_SERV_UPDATE_TIME + " desc";
        }
        return select(context, query, actionOrderTask);
    }*/

    // End Here

    public static HashMap<Long, Object> checkAndGetOrderPart(Context context,//yash
                                                             String actionGetOrderPart, String oid) {
        return checkAndGetOrderPart(context, actionGetOrderPart, oid, QUERY_FOR_ORIG);
    }

    public static HashMap<Long, Object> checkAndGetOrderPart(Context context,//yash
                                                             String actionGetOrderPart, String oid, int queryType) {
        Utilities.log(context, "DBHandler checkAndGetOrderPart called.");

		/*//YD getting oid which is primary key in table
		String WhereClause = "id ="+ oid +" OR localid ="+oid;
		String Oid = DBHandler.getServerOrderId(DBHandler.orderListTable, Order.ORDER_ID,WhereClause);*/

        String query = null;
        if (queryType == QUERY_FOR_ORIG) {
            query = "SELECT * FROM " + orderPartTable + " WHERE (oid = "
                    + oid + " OR " + localOidField + " = " + oid + ") AND " + DBHandler.modifiedField + "!= '" + DBHandler.deleted + "' ORDER BY " + OrderPart.ORDER_UPDATE_TIME + " desc";
        }

        return select(context, query, actionGetOrderPart);
    }

    public static HashMap<Long, Object> checkAndGetOrderForm(Context context,//yash
                                                             String actionGetOrderForm, String oid, int queryType) {
        Utilities.log(context, "DBHandler checkAndGetOrderForm called.");

		/*//YD getting oid which is primary key in table
		String WhereClause = "id ="+ oid +" OR localid ="+oid;
		String Oid = DBHandler.getServerOrderId(DBHandler.orderListTable, Order.ORDER_ID,WhereClause);*/

        String query = null;
        if (queryType == QUERY_FOR_ORIG) {
            //query = "SELECT * FROM " + formTable + " WHERE (oid = " + oid + " OR " + localOidField + " = " + oid + ") AND " + DBHandler.modifiedField + "!= '" + DBHandler.deleted + "' ORDER BY " + Form.FORM_UPD + " desc";
            query = "SELECT * FROM " + formTable + " WHERE (oid = " + oid + " OR " + localOidField + " = " + oid + ") ORDER BY " + Form.FORM_UPD + " desc";
        }

        return select(context, query, actionGetOrderForm);
    }

    public static HashMap<Long, Object> checkAndGetCompyClientSite(Context context, String actiongetparttype) {
        return checkAndGetCompyClientSite(context, actiongetparttype, QUERY_FOR_ORIG);
    }

    public static HashMap<Long, Object> checkAndGetCompyClientSite(Context context, String actiongetparttype, int queryType) { // Currently not using the argument but
        // may be useful in future.
        Utilities.log(context, "DBHandler checkAndGetClientSite called.");
        String query = null;
        if (queryType == QUERY_FOR_ORIG) {
            query = "SELECT * FROM " + companyClientSiteTable + " ORDER BY "
                    + ClientSite.CLIENTSITE_NAME + " COLLATE NOCASE;";
        }
        return select(context, query, actiongetparttype);
    }

    public static HashMap<Long, Object> checkAndGetShift(Context context, String actiongetparttype, int queryType) { // Currently not using the argument but
        // may be useful in future.
        Utilities.log(context, "DBHandler checkAndGetShift called.");
        String query = null;
        if (queryType == QUERY_FOR_ORIG) {
            query = "SELECT * FROM " + shiftTable + " where " + DBHandler.modifiedField + "!='" + DBHandler.deleted + "' ORDER BY "
                    + Shifts.SHIFT_DT + " desc , " + Shifts.SHIFT_TMSLOT + " desc";
        }
        return select(context, query, actiongetparttype);
    }

    public static int saveOrders(Context context, String xml) {
        return saveOrders(context, xml, QUERY_FOR_ORIG);
    }

    public static int saveOrders(Context context, String xml, int queryType) {
        Utilities.log(context, "DBHandler saveOrders called.");
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);
        Object[] orderList = xmlHandler.getOrderValuesFromXML(xml);

        orderList = fillorderObjectwithSiteData(context, orderList);
        int result = 0;
        if (orderList != null) {
            //if(queryType == QUERY_FOR_ORIG){
            result = insertRecord(context, orderListTable, orderList, queryType);
            //YD using for getting customer token for there history file , but have to check wheather this logic should be implemented here or not  currently done when adding new order through pubnub
            DBEngine.getCustTokenfromServerandSave(context, Long.toString(((Order) orderList[0]).getCustomerid()), DBHandler.QUERY_FOR_ORIG);
            //}
            //else{
            //result = insertRecord(context, tempTablePrefix + orderListTable, orderList);
            //}
        }
        return result;
    }

    private static Object[] fillorderObjectwithSiteData(Context context, Object[] orderListValues) {

        String temp = "";
        for (int i = 0; i < orderListValues.length; i++) {

            Order order = (Order) orderListValues[i];
            String whereclause = Site.SITE_ID + "=" + order.getCustSiteId();
            String query = "SELECT * from " + DBHandler.siteTable + " where " + whereclause;
            Cursor cursor = null;
            cursor = db.rawQuery(query, null);
            Log.i(Utilities.TAG, "query" + query + " rows count is : " + cursor.getCount());
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                order.setCustSiteGeocode(getvaluefromCursor(cursor, Site.SITE_GEO));
                temp = getvaluefromCursor(cursor, Site.SITE_ADDRESS);
                if (temp == null)
                    temp = "";

                order.setCustSiteStreeAdd(temp);
                temp = getvaluefromCursor(cursor, Site.SITE_ADR2);
                if (temp == null)
                    temp = "";
                order.setCustSiteSuiteAddress(temp);
            }
            if (cursor != null)
                cursor.close();

            whereclause = CustomerContact.CUSTOMER_ID + "=" + order.getCustomerid();
            query = "SELECT * from " + DBHandler.customercontactTable + " where " + whereclause;
            cursor = db.rawQuery(query, null);
            Log.i(Utilities.TAG, "query" + query + " rows count is : " + cursor.getCount());
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                temp = getvaluefromCursor(cursor, CustomerContact.CONTACT_NAME);
                if (temp == null)
                    temp = "";
                order.setCustContactName(temp);
                temp = getvaluefromCursor(cursor, CustomerContact.CONTACT_TEL);
                if (temp == null)
                    temp = "";
                order.setCustContactNumber(getvaluefromCursor(cursor, CustomerContact.CONTACT_TEL));
            }
            if (cursor != null)
                cursor.close();
        }
        return orderListValues;


    }

    public static Object[] saveOrdersForoffline(Context context, String xml, int queryType) {
        Utilities.log(context, "DBHandler saveOrders called.");
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);
        Object[] orderList = xmlHandler.getOrderValuesFromXML(xml); //YD in xml 2015/06/20 3:30 -00:00 but in obj / is replaced by -
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
        if (orderList != null && orderList.length > 0) {

            Log.d("work done","orderlist"+orderList.length);
            for (int i = 0; i < orderList.length; i++) {
                Order orderobj = (Order) orderList[i];
                String fromdatestr = orderobj.getFromDate();
                String todatestr = orderobj.getToDate();
                try {
                    Date datefrom = format.parse(fromdatestr);
                    Date dateto = format.parse(todatestr);
                    orderobj.setStartTime(datefrom.getTime());
                    orderobj.setEndTime(dateto.getTime());
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        int result = 0;
        if (orderList != null) {
            //if(queryType == QUERY_FOR_ORIG){
            result = insertRecord(context, orderListTable, orderList, queryType);
            //}
            //else{
            //result = insertRecord(context, tempTablePrefix + orderListTable, orderList);
            //}
        }
        return orderList;
    }
	/*public static int updateOrders(Context context, String dataString,
			String name, String modifystr) {
		return updateOrders(context, dataString, name, QUERY_FOR_ORIG, modifystr);
	}*/

    public static int updateOrders(Context context, Object requestObj,
                                   String name, int queryType, String modifystr) {
        Object[] orderList = null;
        Order order = null;
        if (name != null) {

            orderList = RequestObjectHandler.getOrderValuesFromReqObj(context, requestObj,
                    name);
			/*orderList = JSONHandler.getOrderValuesFromJson(context, dataString,
					name);*///YD
            order = (Order) orderList[0];
            order.setModified(modifystr);
        } else {
            if (xmlHandler == null)
                xmlHandler = new XMLHandler(context);

            String xmlString = ((PubnubRequest) requestObj).getXml();
            orderList = xmlHandler.getOrderValuesFromXML(xmlString); //later YD expecting xml in dataString( changed to requestObj) // check later
            order = (Order) orderList[0];//YD changed because pubnub request is saved as mofified updated
        }

        if (order.getStatusId() > 3) {
            order.setSortindex(1);
        } else
            order.setSortindex(0);

        int result = updateRecord(context, orderListTable, orderList);
        return result;
    }

    public static int saveOrderNotes(Context context, String xml, String id) {
        return saveOrderNotes(context, xml, id, QUERY_FOR_ORIG);
    }

    // saving orderNotes
    public static int saveOrderNotes(Context context, String xml, String id, int queryType) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] orderNotesList = null;
        int result = 0;
        if (id.equals(OrderPart.NEW_ORDER_PART)) {
            orderNotesList = xmlHandler.getOrderNotesValuesFromXML(xml,
                    Long.valueOf(id));
            result = insertRecord(context, orderFileNotes, orderNotesList);
        } else {
            orderNotesList = JSONHandler.getOrderNotesValuesFromJSON(context,
                    xml);

            result = updateRecord(context, orderFileNotes, orderNotesList);

        }
        return result;
    }

    public static int saveClientLocation(Context context, String xml) {
        Utilities.log(context, "DBHandler saveClientLocation called.");
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] clientLocationList = xmlHandler
                .getClientLocationValuesFromXML(xml);
        int result = insertRecord(context, clientSiteTable, clientLocationList);
        return result;
    }

    public static int updateClientLocation(Context context, String xml) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] clientLocationList = xmlHandler
                .getClientLocationValuesFromXML(xml);
        int result = updateRecord(context, clientSiteTable, clientLocationList);
        return result;
    }

    public static int savePartType(Context context, String xml) {
        Utilities.log(context, "DBHandler savePartType called.");
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] partTypeList = xmlHandler.getPartTypeValuesFromXML(xml);
        int result = insertRecord(context, partTypeListTable, partTypeList);
        return result;
    }

    public static int updatePartType(Context context, PubnubRequest reqObj) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] partTypeList = xmlHandler.getPartTypeValuesFromXML(reqObj.getXml());
        int result = updateRecord(context, partTypeListTable, partTypeList);
        return result;
    }

    public static int updateSiteType(Context context, PubnubRequest reqObj) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] SiteTypeList = xmlHandler.getSiteTypeValuesFromXML(reqObj.getXml());

        int result = updateRecord(context, siteTypeTable, SiteTypeList);
        return result;
    }


    /*public static int saveServiceType(Context context, String xml) {
        Utilities.log(context, "DBHandler saveServiceType called.");
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] serviceTypeList = xmlHandler.getServiceTypeValuesFromXML(xml);
        int result = insertRecord(context, taskTypeTable, serviceTypeList);
        return result;
    }

    public static int updateServiceType(Context context, PubnubRequest reqObj) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] serviceTypeList = xmlHandler.getServiceTypeValuesFromXML(reqObj.getXml());
        int result = updateRecord(context, taskTypeTable, serviceTypeList);
        return result;
    }*/

    public static int saveWorker(Context context, String xml) {
        Utilities.log(context, "DBHandler saveWorker called.");
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] workerList = xmlHandler.getWorkerListValuesFromXML(xml);
        int result = insertRecord(context, resTable, workerList);
        return result;
    }

    public static int updateWorker(Context context, PubnubRequest reqObj) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] workerList = xmlHandler.getWorkerListValuesFromXML(reqObj.getXml());
        int result = updateRecord(context, resTable, workerList);
        return result;
    }

    public static int updateShift(Context context, Object reqObj) {
		/*if (xmlHandler == null)
			xmlHandler = new XMLHandler(context);*/

        Object[] shiftList = new Object[1];
        shiftList[0] = reqObj;

        int result = updateRecord(context, shiftTable, shiftList);
        return result;
    }

    public static void clearTables() {
        DBHandler.deleteTable(customerListTable);
        DBHandler.deleteTable(siteTable);
        DBHandler.deleteTable(assestTable);
        DBHandler.deleteTable(customercontactTable);
    }

    public static void clearList() {
        custTypeList = null;
        siteTypeList = null;
    }


    public static int saveCustomer(Context context, String xml) {
        Utilities.log(context, "DBHandler saveCustomer called.");
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] customerList = xmlHandler.getCustomerListValuesFromXML(xml);
        if (custTypeList != null) {
            if (customerList != null) {
                for (int i = 0; i < customerList.length; i++) {
                    Customer cst = (Customer) customerList[i];
                    for (int k = 0; k < custTypeList.length; k++) {
                        CustomerType ctype = (CustomerType) custTypeList[k];
                        if (ctype != null && cst != null) {
                            if (ctype != null && ctype.getId() == cst.getTid()) {
                                cst.setCustomerType(ctype.getNm());
                                break;
                            }
                        }
                    }
                }
            }
        }


        if (PreferenceHandler.getUiconfigAddorder(context).equals("1")) {
            custTypeList = null;// vicky setting to null. No use now.
            DBHandler.deleteTable(customerListTable);
        }

        int result = insertRecord(context, customerListTable, customerList);

        Object[] sitelist = xmlHandler.getSiteValuesFromXML(xml);
        if (siteTypeList != null) {
            if (sitelist != null) {
                for (int i = 0; i < sitelist.length; i++) {
                    Site st = (Site) sitelist[i];
                    for (int k = 0; k < siteTypeList.length; k++) {
                        SiteType stype = (SiteType) siteTypeList[k];
                        if (stype != null && st != null) {
                            if (stype != null && stype.getId() == st.getTid()) {
                                st.setSitetypenm(stype.getNm());
                                break;
                            }
                        }
                    }
					/*if (((Site)sitelist[i]).getCid()== 42418038){
						Utilities.log(context, "SiteName : "+((Site)sitelist[i]).getNm()+" , Geo is:"+
								((Site)sitelist[i]).getGeo()+" And Dtl field is"+((Site)sitelist[i]).getDetail());
					}*/
                }
            }
        }
        if (PreferenceHandler.getUiconfigAddorder(context).equals("1")) {
            siteTypeList = null;// vicky setting to null. No use now.
            DBHandler.deleteTable(siteTable);
        }
        result = insertRecord(context, siteTable, sitelist);

        //YD getting assest from the customer xml
        Object[] assestList = xmlHandler.getAssetValuesFromXML(xml);

        if (PreferenceHandler.getUiconfigAddorder(context).equals("1"))
            DBHandler.deleteTable(assestTable);

        result = insertRecord(context, assestTable, assestList);

        Object[] contactlist = xmlHandler.getContactValuesFromXML(xml);

        if (PreferenceHandler.getUiconfigAddorder(context).equals("1"))
            DBHandler.deleteTable(customercontactTable);

        result = insertRecord(context, customercontactTable, contactlist);


        return result;
    }


	/*public static int saveCustomer(Context context, String xml) {
		Utilities.log(context, "DBHandler saveCustomer called.");
		if (xmlHandler == null)
			xmlHandler = new XMLHandler(context);

		Object[] customerList = xmlHandler.getCustomerListValuesFromXML(xml);
		int result = insertRecord(context, customerListTable, customerList);
		return result;
	}*///YD used in online app

    public static int updateOrderTask(Context context, PubnubRequest reqObj) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] orderTaskList = xmlHandler.getOrderTaskValuesFromXML(reqObj.getXml());
        int result = updateRecord(context, orderTaskTable, orderTaskList);
        return result;
    }

    public static int updateOrderPart(Context context, PubnubRequest reqObj) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] orderPartList = xmlHandler.getOrderPartValuesFromXML(reqObj.getXml());
        int result = updateRecord(context, orderPartTable, orderPartList);
        return result;
    }

    public static int updateAsset(Context context, PubnubRequest reqObj) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] orderPartList = xmlHandler.getAssetValuesFromXML(reqObj.getXml());
        int result = updateRecord(context, assestTable, orderPartList);
        return result;
    }

    public static int updateAssetType(Context context, PubnubRequest reqObj) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] orderAssetTypeList = xmlHandler.getAssetTypeValuesFromXML(reqObj.getXml());
        int result = updateRecord(context, assestTypeTable, orderAssetTypeList);
        return result;
    }

    public static int updateCustContact(Context context, PubnubRequest reqObj) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] orderCustCont = xmlHandler.getContactValuesFromXML(reqObj.getXml());
        int result = updateRecord(context, customercontactTable, orderCustCont);
        return result;
    }

    public static int updateCustomer(Context context, PubnubRequest reqObj) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] customerList = xmlHandler.getCustomerListValuesFromXML(reqObj.getXml());
        int result = updateRecord(context, customerListTable, customerList);
        return result;
    }

    // saving ordertask
    public static int saveOrderTask(Context context, String xml, String id,
                                    long oid) {
        return saveOrderTask(context, xml, id, oid, QUERY_FOR_ORIG);
    }

    public static int saveOrderTask(Context context, String xml, String id,
                                    long oid, int queryType) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] orderTaskList = null;
        int result = 0;
        if (id.equals(OrderTask.NEW_ORDER_TASK)) {
            orderTaskList = xmlHandler.getOrderTaskValuesFromXML(xml);
            if (queryType == QUERY_FOR_TEMP)
                result = insertRecord(context, orderTaskTable, orderTaskList, queryType);
            else
                result = insertRecord(context, orderTaskTable, orderTaskList);
            if (result > 0) {
                if (queryType == QUERY_FOR_TEMP) {
                    updateOrders(context, tempTablePrefix + Order.ORDER_CUSTOMER_SERVICE_COUNT, oid,
                            result, orderListTable, 0);
                } else {
                    updateOrders(context, Order.ORDER_CUSTOMER_SERVICE_COUNT, oid,
                            result, orderListTable, 0);
                }
            }
        } else {
            orderTaskList = JSONHandler
                    .getOrderTaskValuesFromJSON(context, xml);
            result = updateRecord(context, orderTaskTable, orderTaskList, queryType);
        }
        return result;
    }

    public static Response saveOrderTaskforOffline(Context context, SaveTaskDataRequest innerReqDataObj, String id,
                                                   long oid) {

        Response response = null;
        Object[] orderTaskList = null;
        int result = 0;
        //String responsestr = XMLHandler.XML_DATA_ERROR;
        orderTaskList = RequestObjectHandler
                .getOrderTaskValuesFromReqObj(context, innerReqDataObj);//getOrderTaskValuesFromJSON earlier
        OrderTask odtask = (OrderTask) orderTaskList[0];
        if (id.equals(OrderTask.NEW_ORDER_TASK)) {

            odtask.setOrder_task_id(Utilities.getCurrentTimeInMillis());
            odtask.setModified(DBHandler.modifiedNew);
            result = insertRecord(context, orderTaskTable, orderTaskList);
            if (result > 0) {
                response = new Response();
                response.setResponseMap(ObjectHandler.createMapFromObjects(orderTaskList, OrderTask.ACTION_GET_ORDER_TASK, context));//createXmlFromObjects
                response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                response.setStatus("success");
                response.setSyncToServer(true);
                updateOrders(context, Order.ORDER_CUSTOMER_SERVICE_COUNT, oid,
                        result, orderListTable, 0);

            }
        } else {
            String whereclause = OrderTask.ORDER_TASK_ID + "=" + odtask.getOrder_task_id() + " or " + localidField + "=" + odtask.getOrder_task_id();
            String modifystr = getValueFromTable(context, DBHandler.orderTaskTable, DBHandler.modifiedField, whereclause);
            if (modifystr != null && modifystr.equals(""))
                modifystr = "update";
            odtask.setModified(modifystr);
            result = updateRecord(context, orderTaskTable, orderTaskList, QUERY_FOR_ORIG);
            if (result > 0) {
                response = new Response();
                response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                response.setStatus("success");
                response.setSyncToServer(true);
                //responsestr = XMLHandler.XML_DATA_SUCCESS_MESSAGE;
            }
        }

        //updateOrderEndTime(context,oid,odtask.getOrderEndDate(),odtask.getOrderEndTime() );YD TODO commented because storing null to the order end time
        return response;
    }


    // by mandeep for saving ordertaskOld

   /* public static int saveOrderTaskOld(Context context, String xml, String id, long oid) {
        return saveOrderTaskOld(context, xml, id, oid, QUERY_FOR_ORIG);
    }

    public static int saveOrderTaskOld(Context context, String xml, String id, long oid, int queryType) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] orderTaskOldList = null;
        int result = 0;
        if (id.equals(OrderTaskOld.NEW_ORDER_TASKS)) {
            orderTaskOldList = xmlHandler.getOrderTaskValuesFromXML(xml);
            if (queryType == QUERY_FOR_TEMP)
                result = insertRecord(context, orderTaskTableOld, orderTaskOldList, queryType);
            else
                result = insertRecord(context, orderTaskTableOld, orderTaskOldList);
            if (result > 0) {
                if (queryType == QUERY_FOR_TEMP) {
                    updateOrders(context, tempTablePrefix + Order.ORDER_CUSTOMER_SERVICE_COUNT, oid, result, orderListTable, 0);
                } else {
                    updateOrders(context, Order.ORDER_CUSTOMER_SERVICE_COUNT, oid, result, orderListTable, 0);
                }
            }
        } else {
            orderTaskOldList = JSONHandler.getOrderTaskOldValuesFromJSON(context, xml);
            result = updateRecord(context, orderTaskTableOld, orderTaskOldList, queryType);
        }
        return result;
    }*/

    //YD currently this task creation is in use
  /*  public static Response saveOrderTaskOldforOffline(Context context, SaveTaskOldDataRequest inerReqdata, String id, long oid) {
        Response response = null;
        Object[] orderTaskOldList = null;
        int result = 0;
        orderTaskOldList = RequestObjectHandler.getOrderTaskOldValuesFromReqObj(context, inerReqdata);
        OrderTaskOld odr_task = (OrderTaskOld) orderTaskOldList[0];

        if (id.equals(OrderTaskOld.NEW_ORDER_TASKS)) {

            odr_task.setOrder_task_id(Utilities.getCurrentTimeInMillis());
            odr_task.setModified(DBHandler.modifiedNew);
            result = insertRecord(context, orderTaskTableOld, orderTaskOldList);
            if (result > 0) {
                response = new Response();
                response.setResponseMap(ObjectHandler.createMapFromObjects(orderTaskOldList, OrderTaskOld.ACTION_GET_ORDER_TASK_OLD, context));
                response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                response.setStatus("success");
                updateOrders(context, Order.ORDER_CUSTOMER_SERVICE_COUNT, oid, result, orderListTable, 0);
            }
        } else {
            String whereclause = OrderTaskOld.ORDER_TASK_ID + "=" + odr_task.getOrder_task_id();
            String modifystr = getValueFromTable(context, DBHandler.orderTaskTableOld, DBHandler.modifiedField, whereclause);
            if (modifystr != null && modifystr.equals(""))
                modifystr = "update";
            odr_task.setModified(modifystr);
            result = updateRecord(context, orderTaskTable, orderTaskOldList, QUERY_FOR_ORIG);
            if (result > 0) {
                response = new Response();
                response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                response.setStatus("success");
            }
        }
        return response;
    }*/

    // End Here

    public static Response saveOrderMediaforOffline(Context context, SaveMediaRequest reqdata, String id, long oid, String frmkey) {
        Response response = null;
        Object[] orderMediaList = null;
        int result = 0;
        //String responsestr = XMLHandler.XML_DATA_ERROR;

        orderMediaList = RequestObjectHandler   // earlier jsonhandler
                .getOrderMediaValuesFromJSON(context, reqdata);
        OrderMedia odmedia = (OrderMedia) orderMediaList[0];
        String location = Utilities.getLocation(context);
        location = location.replace("#", ",");
        odmedia.setGeocode(location);
        if (id.equals(OrderMedia.NEW_ORDER_MEDIA)) {

            odmedia.setId(Utilities.getCurrentTimeInMillis());
            odmedia.setModify(DBHandler.modifiedNew);
            result = insertRecord(context, orderFileMeta, orderMediaList);
            if (result > 0) {
                response = new Response();
                response.setResponseMap(ObjectHandler.createMapFromObjects(orderMediaList, OrderMedia.ACTION_GET_MEDIA, context));//createMapFromObjects
                response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                response.setSyncToServer(true);
                response.setStatus("success");

                updateOrders(context, Order.ORDER_CUSTOMER_MEDIA_COUNT, oid, result, orderListTable, odmedia.getMediatype());

            }
        } else {
            String whereclause = OrderMedia.ID + "=" + odmedia.getId();
            String modifystr = getValueFromTable(context, DBHandler.orderFileMeta, DBHandler.modifiedField, whereclause);
       //     String isIdExists = getValueFromTabletoCheckId(context, DBHandler.orderFileMeta, OrderMedia.ID, whereclause);

                if (modifystr != null && modifystr.equals(""))
                    modifystr = "update";

                odmedia.setModify(modifystr);

                result = updateRecord(context, orderFileMeta, orderMediaList, QUERY_FOR_ORIG);
                if (result > 0) {
                    response = new Response();
                    response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                    response.setStatus("success");
                    response.setSyncToServer(true);
                    //responsestr = XMLHandler.XML_DATA_SUCCESS_MESSAGE;

            }
        }
        return response;
    }

    public static Response saveOrderforOffline(Context context, SaveNewOrder req) {//YD

        Response responseObj;
        Object[] list = null;

        int result = 0;
        String responsestr = XMLHandler.XML_DATA_ERROR;

        list = new Object[1];
        Order order = (Order) RequestObjectHandler
                .getAllOrderValuesFromReqObj(context, req);
        list[0] = order;
        list = fillorderObjectwithSiteData(context, list);//YD adding site details

        order.setModified(DBHandler.modifiedNew);

        if (order.getStatusId() > 3) {
            order.setSortindex(1);
        } else
            order.setSortindex(0);

        order.setId(Utilities.getCurrentTimeInMillis());// YD setting current time as order id
        list[0] = order;

        result = insertRecord(context, orderListTable, list);
        if (result >= 0) {
            HashMap<Long, Object> res_map = ObjectHandler.createMapFromObjects(list,
                    Order.ACTION_ORDER, context);

            if (res_map != null) {
                responseObj = new Response();
                responseObj.setResponseMap(res_map);
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                responseObj.setStatus("success");
                responseObj.setSyncToServer(true);
                return responseObj;
            } else {
                responseObj = new Response();
                responseObj.setResponseMap(null);
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_DATA));
                responseObj.setStatus("success");
                return responseObj;
            }
        }
        responseObj = new Response();
        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_DATA));
        responseObj.setStatus("success");
        return responseObj;
    }

    public static Response saveOrderMessageForOffline(Context context, OrderMessage req) {
        int result = 0;
        Response responseObj;

        Object[] orderMsgList = null;
        orderMsgList = RequestObjectHandler.getOrderMessageValuesFromRequest(context, req);

        OrdersMessage odrMsg = (OrdersMessage) orderMsgList[0];
        odrMsg.setModified(DBHandler.modifiedNew);

        result = insertRecord(context, orderMessageTable, orderMsgList);
        if (result > 0) {
            responseObj = new Response();
            responseObj.setResponseMap(null);
            responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            responseObj.setStatus("success");
            responseObj.setSyncToServer(true);
            return responseObj;

        }
        responseObj = new Response();
        responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_DATA));
        responseObj.setStatus("success");
        return responseObj;

    }

    public static Response saveOrderContactForOffline(Context context, EditContactReq req) {
        int result = 0;
        Response responseObj = null;

        Object[] orderCntList = null;
        orderCntList = RequestObjectHandler.getOrderContactValuesFromRequest(context, req);

        CustomerContact odrCnt = (CustomerContact) orderCntList[0];
        //odrCnt.setModify(DBHandler.uupdated);//YD uupdated because using for updating contact from customer detail page in app.

        if (String.valueOf(req.getId()).equals(OrderMedia.NEW_ORDER_MEDIA)) {

            odrCnt.setId(Utilities.getCurrentTimeInMillis());
            odrCnt.setModify(DBHandler.modifiedNew);
            result = insertRecord(context, customercontactTable, orderCntList);
            if (result > 0) {
                responseObj = new Response();
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                responseObj.setSyncToServer(true);
                responseObj.setStatus("success");
            }
        } else {
            String whereclause = OrderMedia.ID + "=" + odrCnt.getId();
            String modifystr = getValueFromTable(context, DBHandler.customercontactTable, DBHandler.modifiedField, whereclause);
            if (modifystr != null && modifystr.equals(""))
                modifystr = "uupdate";
            odrCnt.setModify(modifystr);//YD uupdated because using for updating contact from customer detail page in app.
            result = updateRecord(context, customercontactTable, orderCntList, QUERY_FOR_ORIG);
            if (result > 0) {
                responseObj = new Response();
                responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                responseObj.setStatus("success");
                responseObj.setSyncToServer(true);
                //responsestr = XMLHandler.XML_DATA_SUCCESS_MESSAGE;
            }
        }

        if (responseObj == null) {
            responseObj = new Response();
            responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.UNKNOWN));
            responseObj.setStatus("failure");
            responseObj.setSyncToServer(false);
        }
        return responseObj;

    }

    public static Response saveOrderPartforOffline(Context context, SavePartDataRequest dataReq, String id,
                                                   long oid) {
        Response response = null;
        Object[] orderPartList = null;
        int result = 0;
        //String responsestr = XMLHandler.XML_DATA_ERROR;
        orderPartList = RequestObjectHandler
                .getOrderPartValuesFromReqObj(context, dataReq);
        OrderPart odpart = (OrderPart) orderPartList[0];

        if (id.equals(OrderPart.NEW_ORDER_PART)) {
            odpart.setModified(DBHandler.modifiedNew);
            odpart.setOrder_part_id(Utilities.getCurrentTimeInMillis());//YD  id field of part[not of order]
            result = insertRecord(context, orderPartTable, orderPartList);
            if (result >= 0) {
                response = new Response();
                response.setResponseMap(ObjectHandler.createMapFromObjects(orderPartList, OrderPart.ACTION_GET_ORDER_PART, context));//setting order part object
                response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                response.setStatus("success");
                response.setSyncToServer(true);
                updateOrders(context, Order.ORDER_CUSTOMER_PART_COUNT, oid,
                        result, orderListTable, 0);
            }
        } else {
            String whereclause = OrderPart.ORDER_PART_ID + "=" + odpart.getOrder_part_id() + " or " + localidField + "=" + odpart.getOrder_part_id();
            String modifystr = getValueFromTable(context, DBHandler.orderPartTable, DBHandler.modifiedField, whereclause);
            if (modifystr != null && modifystr.equals(""))
                modifystr = DBHandler.updated;
            odpart.setModified(modifystr);

            result = updateRecord(context, orderPartTable, orderPartList, QUERY_FOR_ORIG);
            if (result > 0) {
                response = new Response();
                response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                response.setStatus("success");
                response.setSyncToServer(true);
                //responsestr = XMLHandler.XML_DATA_SUCCESS_MESSAGE;
            }
        }
        return response;
    }

    public static Response saveMessageforOffline(Context context, HashMap<String, String> dataReq) {
        Response response = null;
        Object[] shifttList = null;
        int result = 0;
        //String responsestr = XMLHandler.XML_DATA_ERROR;
        shifttList = RequestObjectHandler
                .getMessageValuesFromReqObj(context, dataReq);
        result = insertRecord(context, shiftTable, shifttList);
        if (result >= 0) {
            response = new Response();
            response.setResponseMap(ObjectHandler.createMapFromObjects(shifttList, OrderPart.ACTION_GET_ORDER_PART, context));//setting order part object
            response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            response.setStatus("success");
            response.setSyncToServer(true);
        }


        return response;
    }

    public static Response saveShiftforOffline(Context context, ArrayList<SaveShiftReq> dataReq) {
        Response response = null;
        Object[] shifttList = null;
        int result = 0;
        //String responsestr = XMLHandler.XML_DATA_ERROR;
        shifttList = RequestObjectHandler
                .getShiftValuesFromReqObj(context, dataReq);
        result = insertRecord(context, shiftTable, shifttList);
        if (result >= 0) {
            response = new Response();
            response.setResponseMap(ObjectHandler.createMapFromObjects(shifttList, OrderPart.ACTION_GET_ORDER_PART, context));//setting order part object
            response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            response.setStatus("success");
            response.setSyncToServer(true);
        }


        return response;
    }

   /* public static Response saveFormforOffline(Context context, ArrayList<SaveFormRequest> dataReq) {
        Response response = null;
        Object[] formList = null;
        int result = 0;
        //String responsestr = XMLHandler.XML_DATA_ERROR;
        formList = RequestObjectHandler
                .getFormValuesFromReqObj(context, dataReq);
        result = insertRecord(context, formTable, formList);
        if (result >= 0) {
            response = new Response();
            response.setResponseMap(ObjectHandler.createMapFromObjects(formList, Form.ACTION_SAVE_FORM, context));//setting order part object
            response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
            response.setStatus("success");
            response.setSyncToServer(true);
        }


        return response;
    }*/

    public static Response saveFormforOffline(Context context, ArrayList<SaveFormRequest> dataReq, String id,
                                                   long oid) {
        Response response = null;
        Object[] formList = null;
        int result = 0;
        //String responsestr = XMLHandler.XML_DATA_ERROR;
        formList = RequestObjectHandler
                .getFormValuesFromReqObj(context, dataReq);
        Form formObj = (Form) formList[0];

        if (id.equals(OrderPart.NEW_ORDER_PART)) {
            formObj.setModified(DBHandler.modifiedNew);
            formObj.setId(Utilities.getCurrentTimeInMillis());//YD  id field of part[not of order]
            result = insertRecord(context, formTable, formList);
            if (result >= 0) {
                response = new Response();
                response.setResponseMap(ObjectHandler.createMapFromObjects(formList, Form.ACTION_GET_ORDER_FORM, context));//setting order part object
                response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                response.setStatus("success");
                response.setSyncToServer(true);
                updateOrders(context, Order.ORDER_CUSTOMER_FORM_COUNT, oid,
                        result, orderListTable, 0);
            }
        } else {
            String whereclause = Form.FORM_ID + "=" + formObj.getId() + " or " + localidField + "=" + formObj.getId();
            String modifystr = getValueFromTable(context, DBHandler.formTable, DBHandler.modifiedField, whereclause);
            if (modifystr != null && modifystr.equals(""))
                modifystr = DBHandler.updated;
            formObj.setModified(modifystr);

            result = updateRecord(context, formTable, formList, QUERY_FOR_ORIG);
            if (result > 0) {
                response = new Response();
                response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                response.setStatus("success");
                response.setSyncToServer(true);
                //responsestr = XMLHandler.XML_DATA_SUCCESS_MESSAGE;
            }
        }
        return response;
    }




    public static Response saveOrderAssetforOffline(Context context, ArrayList<SaveFormRequest> dataReq, String id,
                                                    long oid) {
        Response response = null;
        Object[] orderAssetList = null;
        int result = 0;
        //String responsestr = XMLHandler.XML_DATA_ERROR;
        orderAssetList = RequestObjectHandler
                .getOrderAssetValuesFromReqObj(context, dataReq);
        Assets odrAsset = (Assets) orderAssetList[0];

        if (id.equals(Assets.NEW_ORDER_ASSET)) {
            odrAsset.setModify(DBHandler.modifiedNew);
            odrAsset.setId(Utilities.getCurrentTimeInMillis());//YD  id field of part[not of order]
            result = insertRecord(context, assestTable, orderAssetList);
            if (result >= 0) {
                response = new Response();
                response.setResponseMap(ObjectHandler.createMapFromObjects(orderAssetList, Assets.ACTION_GET_ASSETS, context));//YD TODO SHOULD RETURN THE OBJ CURRENTLY WE ARE NOT RETURNING IT
                response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                response.setStatus("success");
                response.setSyncToServer(true);
                updateOrders(context, Order.ORDER_ASSET_COUNT, oid,
                        result, orderListTable, 0);
            }

        } else {
            String whereclause = Assets.ASSET_ID + "=" + odrAsset.getId() + " or " + localidField + "=" + odrAsset.getId();
            String modifystr = getValueFromTable(context, DBHandler.assestTable, DBHandler.modifiedField, whereclause);
            if (modifystr != null && modifystr.equals(""))
                modifystr = DBHandler.updated;
            odrAsset.setModify(modifystr);

            result = updateRecord(context, assestTable, orderAssetList, QUERY_FOR_ORIG);
            if (result > 0) {
                response = new Response();
                response.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
                response.setStatus("success");
                response.setSyncToServer(true);
                //responsestr = XMLHandler.XML_DATA_SUCCESS_MESSAGE;

            }

        }

        return response;

    }

    private static void updateOrders(Context context, String column,
                                     long orderId, int result, String tablename, long mediatype) {

        String tempcolumn = column;
        if (mediatype == 4)
            tempcolumn = column + " , " + Order.ORDER_DOCS;
        else if (mediatype == 3)
            tempcolumn = column + " , " + Order.ORDER_AUDIO;
        else if (mediatype == 2)
            tempcolumn = column + " , " + Order.ORDER_SIG;
        else if (mediatype == 1)
            tempcolumn = column + " , " + Order.ORDER_IMG;


        String selectQuery = "Select " + tempcolumn + " FROM " + tablename + " WHERE " + Order.ORDER_ID + "=" + orderId + " OR " + localidField + " = " + orderId; // add fake id
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(selectQuery, null);

            long previousCount = 0;
            long previousMediaCount = 0;

            if (cursor.moveToFirst()) {
                previousCount = cursor.getLong(0);
                if (mediatype > 0)
                    previousMediaCount = cursor.getLong(1);
            }

            cursor.close();
            ContentValues cv = new ContentValues();
            cv.put(column, previousCount + result);

            if (mediatype == 4)
                cv.put(Order.ORDER_DOCS, previousMediaCount + result);
            else if (mediatype == 3)
                cv.put(Order.ORDER_AUDIO, previousMediaCount + result);
            else if (mediatype == 2)
                cv.put(Order.ORDER_SIG, previousMediaCount + result);
            else if (mediatype == 1)
                cv.put(Order.ORDER_IMG, previousMediaCount + result);

            String whereClause = Order.ORDER_ID + "=" + orderId + " OR " + localidField + " = " + orderId;
            Log.i( Utilities.TAG, "inside updateOrders with " + previousCount
                    + result + " to update " + column + " : " + whereClause
                    + " : cv : " + cv.toString() + " : in Table : "
                    + tablename);
            long result1 = db.update(tablename, cv, whereClause, null);
            Log.i(Utilities.TAG, "Total records updated : " + result1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // saving ordertask

    public static int saveDownloadedOrderPart(Context context, String xml, String id,
                                              long oid, int queryType) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] orderPartList = null;
        int result = 0;
        orderPartList = xmlHandler.getOrderPartValuesFromXML(xml);
        if (queryType == QUERY_FOR_TEMP)
            result = insertRecord(context, orderPartTable, orderPartList, queryType);
        else
            result = insertRecord(context, orderPartTable, orderPartList);
        if (oid == -2 && result != 0) {
            updateOrders(context, Order.ORDER_CUSTOMER_PART_COUNT, ((OrderPart) orderPartList[0]).getOid(),
                    result, DBHandler.orderListTable, 0);
        }
        return result;
    }

    public static int[] saveDownloadedOrderMeta(Context context, String xml, String id,
                                                long oid, int queryType) {//YD
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Log.d("TAG56",xml);
        Object[] orderMediaList = null;
        int result[] = new int[2];
        orderMediaList = xmlHandler.getOrderMediaValuesFromXML(xml);
        if (orderMediaList != null) {
            for (int i = 0; i < orderMediaList.length; i++) {
                OrderMedia ordermedia = (OrderMedia) orderMediaList[i];
                String url = HttpConnection.getRequestURL(context) + "&" + "action=" + OrderMedia.ACTION_GET_MEDIA_DATA +
                        "&" + OrderMedia.ID + "=" + ordermedia.getId();
                String type = "";
                String ext = "";
                if (ordermedia.getMediatype() == 4) {
                    type = "FILE";
                    ext = "." + ordermedia.getMimetype();
                }
                if (ordermedia.getMediatype() == 3) {
                    type = "AUD";
                    //ext = ".3gp";
                    ext = ".m4a";
                }
                if (ordermedia.getMediatype() == 2) {
                    type = "SIG";
                    ext = ".jpg";
                }
                if (ordermedia.getMediatype() == 1) {
                    type = "PIC";
                    ext = ".jpg";
                }
                String timestamp = Long.toString(ordermedia.getUpd_time());
                String filename = "";
                if (ordermedia.getMediatype() == 1 && ordermedia.getMediatype() == 3)//YD currently doing this because sometime we are not getting extension
                    filename = ordermedia.getOrderid() + "_" + type + "_" + timestamp + ext;//YD using earlier for all mediatype
                else
                    filename = ordermedia.getFileName(); // YD now getting name in fmeta xml

                String mediapath = Utilities.DownloadFiles(context, url, "AceRoute", filename);//YD replaced Aceroute with Api.APP_BASE_PATH because this is not the correct folder where audio should be saved
                if (mediapath != null) {
                    ordermedia.setMetapath(mediapath);
                    result[1] = 1;
                    if (ordermedia.getMediatype() == 1) //YD make thumbnail only for images
                        createThumbImage(mediapath);
                } else {
                    result[1] = 0;
                    // log the media cannot be downloaded.
                }

            }
        }
        result[0] = insertRecord(context, orderFileMeta, orderMediaList);

        return result;
    }

    private static void createThumbImage(String mediapath) {
        String file_path = mediapath;
        String thumbPath = makeThumbPath(file_path);
        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file_path), 70, 100);
        File file = new File(thumbPath);//YD making a file on the path specified

        try {
            FileOutputStream out = new FileOutputStream(file); // YD making the file location writable
            ThumbImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String makeThumbPath(String str) {

        if (str != null) {///storage/emulated/0/AceRoute/temp_1462527798949.jpg
            String array[];
            array = str.split("\\.");

            return array[0] + "_Thumb." + array[1];
        }
        return null;
    }

    public static int saveDownloadedOrderForm(Context context, String xml, String id,
                                              long oid, int queryType) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] orderFormList = null;
        int result = 0;
        orderFormList = xmlHandler.getOrderFormValuesFromXML(xml);
        if (queryType == QUERY_FOR_TEMP) {
            result = insertRecord(context, formTable, orderFormList, queryType);
        } else {
            result = insertRecord(context, formTable, orderFormList);
        }
        if (oid == -2 && result != 0) {
            updateOrders(context, Order.ORDER_CUSTOMER_FORM_COUNT, ((OrderTask) orderFormList[0]).getId(),
                    result, DBHandler.orderListTable, 0);
        }
        return result;
    }

    public static int saveDownloadedOrderAssets(Context context, String xml) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] assetList = null;
        int result = 0;
        assetList = xmlHandler.getAssetValuesFromXML(xml);

        result = insertRecord(context, assestTable, assetList);
        return result;
    }

    public static int saveDownloadedOrderAssetsType(Context context, String xml) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] assetList = null;
        int result = 0;
        assetList = xmlHandler.getAssetTypeValuesFromXML(xml);

        result = insertRecord(context, assestTypeTable, assetList);
        return result;
    }

    public static int saveCustomerContactFromXml(Context context, String xml, String id,
                                                 long oid) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);
        Log.d("TAM",xml);
        Object[] orderContactList = null;
        int result = 0;
        orderContactList = xmlHandler.getContactValuesFromXML(xml);
        result = insertRecord(context, customercontactTable, orderContactList);

        return result;
    }

    public static int saveDownloadedOrderNotes(Context context, String xml, long oid, int queryType) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);
        Object[] orderNotesList = null;
        orderNotesList = new XMLHandler(context)
                .getOrderNotesValuesFromXML(xml, oid);
        int result = insertRecord(context, orderFileNotes, orderNotesList);
        return result;
    }

    public static int saveOrderPart(Context context, String xml, String id,
                                    long oid, int queryType) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] orderPartList = null;
        int result = 0;
        if (id.equals(OrderPart.NEW_ORDER_PART)) {
            orderPartList = xmlHandler.getOrderPartValuesFromXML(xml);
            if (queryType == QUERY_FOR_TEMP)
                result = insertRecord(context, orderPartTable, orderPartList, queryType);
            else
                result = insertRecord(context, orderPartTable, orderPartList);
            if (result > 0) {
                if (queryType == QUERY_FOR_TEMP) {
                    updateOrders(context, Order.ORDER_CUSTOMER_PART_COUNT, oid,
                            result, tempTablePrefix + orderListTable, 0);
                } else {
                    updateOrders(context, Order.ORDER_CUSTOMER_PART_COUNT, oid,
                            result, orderListTable, 0);
                }
            }
        } else {
            orderPartList = JSONHandler
                    .getOrderPartValuesFromJSON(context, xml);
            result = updateRecord(context, orderPartTable, orderPartList, queryType);
        }
        return result;
    }

    // saving orderMedia
    public static int saveOrderMedia(Context context, String xml, String id, int queryType) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] orderMediaList = null;
        int result = 0;
        if (id.equals(OrderPart.NEW_ORDER_PART)) {
            orderMediaList = xmlHandler.getOrderMediaValuesFromXML(xml);
            result = insertRecord(context, orderFileMeta, orderMediaList, queryType);
        } else {
            orderMediaList = JSONHandler.getOrderMediaValuesFromJSON(context,
                    xml);
            result = updateRecord(context, orderFileMeta, orderMediaList, queryType);
        }
        /*
         * orderMediaList = xmlHandler.getOrderMediaValuesFromXML(xml); result =
         * insertRecord(context, orderFileMeta, orderMediaList);
         * Logger.i(context, Utilities.TAG, "inserting data in ordermedia : "+result);
         * if(result < 1){ Logger.i(context, Utilities.TAG,
         * "insertion fail in ordermedia for id : "+id); }
         */
        return result;
    }

    static Object[] custTypeList;// vicky.. making it static so that this can be used later when filling customer

    public static int saveCusType(Context context, String xml) {
        Utilities.log(context, "DBHandler saveCusType called.");
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        custTypeList = xmlHandler.getCusTypeValuesFromXML(xml);

        int result = insertRecord(context, customerTypeTable, custTypeList);
        return result;
    }

    static Object[] siteTypeList;

    public static int saveSiteType(Context context, String xml) {
        Utilities.log(context, "DBHandler saveSiteType called.");
        Log.d("TAG9",xml);

        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        siteTypeList = xmlHandler.
                getSiteTypeValuesFromXML(xml);
        Log.d("TAG3",siteTypeList.toString());
        int result = insertRecord(context, siteTypeTable, siteTypeList);
        return result;
    }

    public static int saveOrderType(Context context, String xml) {
        Utilities.log(context, "DBHandler saveOrderType called.");
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] orderTypeList = xmlHandler.getOrderTypeValuesFromXML(xml);

        int result = insertRecord(context, orderTypeListTable, orderTypeList);
        return result;
    }

    public static int saveAssetsType(Context context, String xml) {
        Utilities.log(context, "DBHandler saveOrderType called.");
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] orderTypeList = xmlHandler.getAssetTypeValuesFromXML(xml);

        int result = insertRecord(context, assestTypeTable, orderTypeList);
        return result;
    }

    public static int saveStatusType(Context context, String xml) {
        Utilities.log(context, "DBHandler saveOrderType called.");
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] orderStatusList = xmlHandler.getStatusTypeValuesFromXML(xml);

        int result = insertRecord(context, orderStatusTable, orderStatusList);
        return result;
    }

    public static int saveCompClientSite(Context context, String xml) {
        Utilities.log(context, "DBHandler saveOrderType called.");
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] clientSiteList = xmlHandler.getClientSiteValuesFromXML(xml);

        int result = insertRecord(context, companyClientSiteTable, clientSiteList);
        return result;
    }


    public static int saveShifts(Context context, String xml) {
        Utilities.log(context, "DBHandler saveOrderType called.");
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] shift = xmlHandler.getShiftValuesFromXML(xml);

        int result = insertRecord(context, shiftTable, shift);
        return result;
    }

    public static int updateOrderType(Context context, PubnubRequest reqObj) {
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] orderTypeList = xmlHandler.getOrderTypeValuesFromXML(reqObj.getXml());
        int result = updateRecord(context, orderTypeListTable, orderTypeList);
        return result;
    }

    public static int saveSite(Context context, String xml) {
        Utilities.log(context, "DBHandler saveSite called.");
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] siteList = xmlHandler.getSiteValuesFromXML(xml);
        int result = insertRecord(context, siteTable, siteList);
        return result;
    }

    public static int updateSite(Context context, String xmlString, Object object) {
        Utilities.log(context, "DBHandler updateSite called.");
        if (xmlHandler == null)
            xmlHandler = new XMLHandler(context);

        Object[] siteList = xmlHandler.getSiteValuesFromXML(xmlString);
        int result = updateRecord(context, siteTable, siteList);
        return result;
    }

    public static int saveSiteFromUI(Context context, SaveSiteRequest saveSiteReqObject) {//YD
        Utilities.log(context, "DBHandler saveSite called.");
        Object[] siteList;

        siteList = RequestObjectHandler.getSiteValuesFromReqObj(context, saveSiteReqObject);//JSONHandler.getSiteValuesFromJSON earlier YD

        Site st = (Site) siteList[0];

        int result;
        if (st.getId() == 0) {

            st.setId(Utilities.getCurrentTimeInMillis());
            st.setModify(DBHandler.modifiedNew);
            result = insertRecord(context, siteTable, siteList);

        } else {
            String whereclause = Site.SITE_ID + "=" + st.getId();
            String modifystr = getValueFromTable(context, DBHandler.siteTable, DBHandler.modifiedField, whereclause);
            if (modifystr != null && modifystr.equals(""))
                modifystr = "update";
            st.setModify(modifystr);
            result = updateRecord(context, siteTable, siteList, QUERY_FOR_ORIG);

        }

        return result;
    }

    public static int saveSiteFromUI(Context context, EditSiteReq edtSiteReqObj) {//YD
        Utilities.log(context, "DBHandler saveSite called.");

        Object[] siteList;

        if (edtSiteReqObj.getIsOnlyUpdate().equals("true"))//YD this for update site on drag on map fragment (all map page)
            siteList = RequestObjectHandler.getSiteValuesFromReqObjForUpdate(context, edtSiteReqObj);
        else
            siteList = RequestObjectHandler.getSiteValuesFromReqObj(context, edtSiteReqObj);//JSONHandler.getSiteValuesFromJSON earlier YD

        Site st = (Site) siteList[0];

        int result;
        if (st.getId() == 0) {

            st.setId(Utilities.getCurrentTimeInMillis());
            st.setModify(DBHandler.modifiedNew);
            result = insertRecord(context, siteTable, siteList);

        } else {
            String whereclause = Site.SITE_ID + "=" + st.getId();
            String modifystr = getValueFromTable(context, DBHandler.siteTable, DBHandler.modifiedField, whereclause);
            if (modifystr != null && modifystr.equals(""))
                modifystr = "uupdate";
            st.setModify(modifystr);
            result = updateRecord(context, siteTable, siteList, QUERY_FOR_ORIG);

        }

        return result;
    }

    @SuppressLint("NewApi")
    public static String getvaluefromCursor(Cursor cursor, String columnname) {
        //String str=cursor.getString(0);

        int index = cursor.getColumnIndex(columnname);
        String str = "";
        if (index >= 0) {
            SQLiteCursor sqLiteCursor = (SQLiteCursor) cursor;
            CursorWindow cursorWindow = sqLiteCursor.getWindow();
            int pos = cursor.getPosition();
            int type = -1;
            type = cursorWindow.getType(pos, index);
            //	int type = cursor.getType(index);
            switch (type) {
                case Cursor.FIELD_TYPE_INTEGER:
                    str = String.valueOf(cursor.getLong(index));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    str = cursor.getString(index);
                    break;
                case Cursor.FIELD_TYPE_NULL:
                    str = cursor.getString(index);
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    str = String.valueOf(cursor.getDouble(index));
                    break;
                default:
                    break;


            }
        }

        return str;
    }

    /*** Vikram*** for offline data***/

    public static Cursor getOfflineModifiedRecords(Context context, String tablename) {
        Utilities.log(context, "DBHandler getOfflineModifiedRecords called.");
        String query = null;
        query = "SELECT * FROM " + tablename + " WHERE "
                + modifiedField + "!=''";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null) {
                cursor.close();
            }
        }
            return null;
    }

    public static String getValuebyWhere(Context context, String tablename, String field, String whereclause) {
        Utilities.log(context, "DBHandler getOfflineModified getValuebyWhere called.");
        String query = null;
        query = "SELECT " + field + " FROM " + tablename + " where " + whereclause;
        Cursor cursor = null;
        String value = null;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                value = cursor.getString(0);
                if (cursor != null) {
                    cursor.close();
                }
                return value;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }

    public static Cursor getOfflineModifiedShift(Context context, String tablename, String modify) {
        Utilities.log(context, "DBHandler getOfflineModifiedShift called.");
        String query = null;
        if (modify.equals(DBHandler.modifiedNew)) {
            query = "SELECT * FROM " + tablename + " WHERE "
                    + modifiedField + "='" + DBHandler.modifiedNew + "'";
        } else if (modify.equals(DBHandler.updated)) {
            query = "SELECT * FROM " + tablename + " WHERE "
                    + modifiedField + "='" + DBHandler.updated + "' ";
        } else {
            query = "SELECT * FROM " + tablename + " WHERE "
                    + modifiedField + " in ('" + DBHandler.deleted + "','" + DBHandler.updated + "')  ";
        }
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null) {
                cursor.close();
            }
        } finally {

        }
        return null;
    }

    public static int updateTable(String table, ContentValues cv, String whereClause, String[] whereArgs) {
        return db.update(table, cv, whereClause, whereArgs);
    }

    public static long insertTable(String table, String column, ContentValues cv) {
        return db.insert(table, column, cv);
    }
}