package com.aceroute.mobile.software;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import com.aceroute.mobile.software.database.DBEngine;
import com.aceroute.mobile.software.fragment.OrderDetailFrag;
import com.aceroute.mobile.software.utilities.UpdateLocationService;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTabHost;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.audio.OrderDetailAudioListFragment;
import com.aceroute.mobile.software.audio.PlayAudioFragment;
import com.aceroute.mobile.software.audio.RecordAudioFragment;
import com.aceroute.mobile.software.camera.Addsign;
import com.aceroute.mobile.software.camera.CaptureSignature;
import com.aceroute.mobile.software.camera.Gridview_MainActivity;
import com.aceroute.mobile.software.camera.OrderPicSaveActivity;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderMedia;
import com.aceroute.mobile.software.component.OrderNotes;
import com.aceroute.mobile.software.component.OrderPart;
import com.aceroute.mobile.software.component.OrderTask;
import com.aceroute.mobile.software.component.reference.Assets;
import com.aceroute.mobile.software.component.reference.CheckinOut;
import com.aceroute.mobile.software.component.reference.Customer;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.OrderTypeList;
import com.aceroute.mobile.software.component.reference.Parts;
import com.aceroute.mobile.software.component.reference.ServiceType;
import com.aceroute.mobile.software.component.reference.Site;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.component.reference.Worker;
import com.aceroute.mobile.software.customersite.AddSiteActy;
import com.aceroute.mobile.software.customersite.CustomerListActivity;
import com.aceroute.mobile.software.customersite.Sitelist;
import com.aceroute.mobile.software.dialog.CustomStatusDialog;
import com.aceroute.mobile.software.dialog.DatePickerInterface;
import com.aceroute.mobile.software.dialog.MyDatePickerDialog;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.StatusDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.fragment.AddEditAssetFragment;
import com.aceroute.mobile.software.fragment.AddEditFormFragment;
import com.aceroute.mobile.software.fragment.AddEditPartsFragment;
import com.aceroute.mobile.software.fragment.AddEditTaskOrderFragment;
import com.aceroute.mobile.software.fragment.AddTaskSpeciesFrag;
import com.aceroute.mobile.software.fragment.BaseFragment;
import com.aceroute.mobile.software.fragment.ClockInFragment;
import com.aceroute.mobile.software.fragment.CreateOrderFragment;
import com.aceroute.mobile.software.fragment.CustomerDetailFragment;
import com.aceroute.mobile.software.fragment.EditContactFrag;
import com.aceroute.mobile.software.fragment.GoogleMapFragment;
import com.aceroute.mobile.software.fragment.ManagePassword;
import com.aceroute.mobile.software.fragment.ManageShift;
import com.aceroute.mobile.software.fragment.MapAllFragment;
import com.aceroute.mobile.software.fragment.MapFragment;
import com.aceroute.mobile.software.fragment.MessageSos;
import com.aceroute.mobile.software.fragment.OptionFragment;
import com.aceroute.mobile.software.fragment.OrderAssetFragment;
import com.aceroute.mobile.software.fragment.OrderDetailFragment;
import com.aceroute.mobile.software.fragment.OrderListMainFragment;
import com.aceroute.mobile.software.fragment.OrderPartsFragment;
import com.aceroute.mobile.software.fragment.OrderFormsFragment;
import com.aceroute.mobile.software.fragment.OrderTasksOldFragment;
import com.aceroute.mobile.software.fragment.ScheduledDetailFragment;
import com.aceroute.mobile.software.fragment.ShifthourFragment;
import com.aceroute.mobile.software.fragment.SwapingFragment;
import com.aceroute.mobile.software.fragment.TimeOffFragment;
import com.aceroute.mobile.software.fragment.UnavailableFragment;
import com.aceroute.mobile.software.http.HttpConnection;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.network.AceRequestHandler;
import com.aceroute.mobile.software.notes.AlertnNoteFrag;
import com.aceroute.mobile.software.notes.Createnote;
import com.aceroute.mobile.software.notes.OrderNote;
import com.aceroute.mobile.software.offline.CityListFragment;
import com.aceroute.mobile.software.offline.CountryListFragment;
import com.aceroute.mobile.software.requests.ClockInOutRequest;
import com.aceroute.mobile.software.requests.CommonSevenReq;
import com.aceroute.mobile.software.requests.GetFileMetaRequest;
import com.aceroute.mobile.software.requests.GetOrdersRO;
import com.aceroute.mobile.software.requests.GetPart_Task_FormRequest;
import com.aceroute.mobile.software.requests.LogoutRequest;
import com.aceroute.mobile.software.requests.SyncRO;
import com.aceroute.mobile.software.requests.updateOrderRequest;
import com.aceroute.mobile.software.utilities.CustomTypefaceSpan;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.PubnubHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.aceroute.mobile.software.utilities.XMLHandler;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class BaseTabActivity extends FragmentActivity implements
        OnTabChangeListener, RespCBandServST, OnClickListener, NavigationView.OnNavigationItemSelectedListener, DatePickerInterface {

    public static final int GET_CUST_REQ_PB = 3;
    public static final int GET_ORDER_TYPE_REQ_PB = 4;
    public static final int GET_PARTS_REQ_PB = 5;
    public static final int GET_RES_REQ_PB = 6;
    public static final int GET_TASKS_REQ_PB = 7;
    public static final int GET_ALL_NOTE_REQ = 13;
    public static final int SYNC_OFFLINE_DATA_LOGOUT = 201;
    public static final int GET_ACCESS_FINE_LOCATION_PERM = 1;
    private static final int SYNC_LOCATION_JOB_ID = 123;
    public static BaseTabActivity mBaseTabActivity;
    public static HashMap<String, Stack<Fragment>> mStacks;
    public static String mCurrentTab = null;
    public static int tabId = 1;
    public static FragmentManager fragmentManager;
    public static ImageView mBtnMapSetting, mBtnMapSetting_tree;
    public static int pushFragToStackRequired = 0;
    public static Fragment currentActiveFrag;
    public static String HeaderDonePressed = "1";
    public static String HeaderPlusPressed = "2";
    public static String HeaderPanicPressed = "3";
    public static String HeaderSyncPressed = "4";
    public static String HeaderTaskTreePressed = "5";
    public static String HeaderTaskUserPressed = "6";
    public static String HeaderMapAllPressed = "7";
    public static String HeaderlastSyncPressed = "8";
    public static String HeaderMapOrderTaskPressed = "9";
    public static String HeaderMapSettingPressed = "10";
    public static String HeaderBackPressed = "11";
    public static String HeaderMapSettingPressed_Main = "12";
    public static String HeaderVolumeButton = "13";
    public static String HeaderSwapButton = "14";
    public static String HeaderDeleteButton = "15";
    public static int bootLoadedItems = 0;
    public static int maxBootLoadItems = 10;
    public static boolean refreshOrdersListfromCache = false;
    public static int UI_Thread = 0;
    public static int SERVICE_Thread = 1;
    // for Audio
    public static int RECORDER_RECORD_STATE = 0;
    public static int RECORDER_STOP_STATE = 1;
    public static int PLAYER_STOP_STATE = 3;
    public static int PLAYER_PLAY_STATE = 4;
    //Pubnub request
    public static int GET_ORDER_REQ_PB = 9;
    public static int GET_PARTS_UPDATE_PB = 8;// for pubnub 18
    public static int GET_TASKS_UPDATE_PB = 20;// for pubnub 17
    public static int GETFILEMETA_PB = 21;// for pubnub 19
    public static int GET_SITE_TYPE = 24;
    public static int LOGOUT_REQ = 202;
    public static Handler UIHandler = new Handler(Looper.getMainLooper());
    public static int currentApplicationState;
    public static int FOREGROUND = 1;
    public static int BACKGROUND = 0;
    public static int RESTARTED = -1;
    public static long OrderTaskBackOdrId = 0;
    public static long OrderEditTaskBackOdrId = 0;
    public static String LOGNAME = "AceRoute";
    public static boolean nextDaySyncFlag = false;
    public static boolean addedNewMap = false;
    public static ProgressBar mProgress;
    public static String versionCheckState;
    public static boolean shouldCloseApp = false;
    public static boolean firstskoobler = false;
    public static boolean logout = false;
    public static int PUBNUB_SYNC = 59;
    public static int PUBNUB_LOGOUT = 58;
    public static HashMap<Long, SiteType> siteGenTypeMapTid_10 = new HashMap<Long, SiteType>();
    public static int buttonClickedId = 0; // YD using for buttons
    static boolean shouldFinish = false;
    private static TextView mTxtVwHeaderTop;
    private static TextView mTxtVwSubHeaderTop;
    private static TextView mTxtVwHeaderMiddle;
    public ImageView mImgSyncBtn;
    public TextView no_of_order;
    public RelativeLayout headerApp, Bck_button_main;
    public ImageView mBtnDone, mSwapButton, mBtnTrash;
    public boolean calenderclick = false;
    public DrawerLayout drawer;
    public RelativeLayout headerMain;
    HeaderInterface headerEventbtn;
    Order activeOrderObj = null;
    MyDialog dialog; //YD dialog for sync when date change
    CustomStatusDialog customDialog;
    private FragmentTabHost mTabHost;
    private ClockInFragment mClockInFragment;
    private MapFragment mMapFragment;
    private OrderListMainFragment mJobFragment;
    private OptionFragment mopOptionFragment;
    private ImageView mBtnPlus, mImgBack, mImgPanic, mImgLastSync, mImgVolumeBtn;
    private Button mBtnMapAll, mBtnMapOdrTask, mBtnTaskTreeMap, mBtnTaskUserMap;
    private FrameLayout mainView;
    private int CHECKIN_REQ = 2001;
    private int CHECKOUT_REQ = 2002;
    private int CHECKOUT_REQ_LOGOUT = 2003;
    private int clockTab = 1;
    private int SYNC_OFFLINE_DATA = 203;
    private int LOAD_REF_DATA = 204;
    private int GET_ORDER_REQ = 205;
    private HashMap<Long, SiteType> siteGenTypeMapTid_11 = new HashMap<Long, SiteType>();
    private boolean isActivityVisisble = false;

    public static void setHeaderTitle(final String headerTop, final String headerMiddle, final String headerSubHeader) {
        try {
            mTxtVwHeaderTop.setText(headerTop);
            mTxtVwHeaderMiddle.setText(headerMiddle);
            mTxtVwHeaderMiddle.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mBaseTabActivity)));
            mTxtVwSubHeaderTop.setText(headerSubHeader);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setHeaderTitle4OLMP(String headerTop, String headerMiddle, String headerSubHeader) {
        try {
            mTxtVwHeaderTop.setText(headerTop);
            //mTxtVwHeaderTop.setTextSize(19 + (PreferenceHandler.getCurrrentFontSzForApp(mBaseTabActivity)));
            mTxtVwHeaderMiddle.setText(headerMiddle);
            mTxtVwHeaderMiddle.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mBaseTabActivity)));
		/*	if(headerMiddle.equals("Create New Order"))
				mTxtVwHeaderMiddle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			else
				mTxtVwHeaderMiddle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);*/
            mTxtVwSubHeaderTop.setText(headerSubHeader);
            mTxtVwSubHeaderTop.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(mBaseTabActivity)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTmeFrOLMPHeader(Context context) {
        if (currentActiveFrag instanceof OrderListMainFragment && currentApplicationState == FOREGROUND) {

            long lastSyncTime = PreferenceHandler.getlastsynctime(context);
            long lastPubnubUpdateTime = PreferenceHandler.getLastPubnubUpdated(context.getApplicationContext());
            Date date;
            /*if(Long.valueOf(lastSyncTime)>lastPubnubUpdateTime)*/
            if (Utilities.isTodayDate(context, new Date(Long.valueOf(lastSyncTime))) &&
                    Utilities.isTodayDate(context, new Date(Long.valueOf(lastPubnubUpdateTime)))) {
                if (Long.valueOf(lastSyncTime) > lastPubnubUpdateTime)
                    date = new Date(Long.valueOf(lastSyncTime));
                else
                    date = new Date(Long.valueOf(lastPubnubUpdateTime));
            } else {
                date = new Date(Long.valueOf(lastSyncTime));
            }

            if (PreferenceHandler.getOdrGetDate(context) != null && !PreferenceHandler.getOdrGetDate(context).equals(""))// YD showing custom date for which order has been downloaded
                date = new Date(PreferenceHandler.getodrLastSyncForDt(context));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d yyyy");//Jun 21 2015
            final String headerDate = simpleDateFormat.format(date);
            final String headerTime = Utilities.convertDateToAmPM(date.getHours(), date.getMinutes());
            final String headerDay = getDayFrmDate(date.getDay());

            UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    setHeaderTitle4OLMP(headerDate, "", headerDay);
                }
            });
        }
    }

    public static String getDayFrmDate(int day) {
        if (day == 0)
            return "Sunday";
        else if (day == 1)
            return "Monday";
        else if (day == 2)
            return "Tuesday";
        else if (day == 3)
            return "Wednesday";
        else if (day == 4)
            return "Thursday";
        else if (day == 5)
            return "Friday";
        else if (day == 6)
            return "Saturday";

        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseFragment.mActivity = this;
        Log.e("Aceroute", "onCreate1 called for BaseTabActivity");
        logout = false;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ace_main);

        if (SplashII.wrk_tid == 0) {

            Log.d("checkwhylogout", "why" + SplashII.wrk_tid);

            syncDataBeforeLogout("semipartial_logout");
        }

        mBaseTabActivity = this;
        firstskoobler = true;
        fragmentManager = getSupportFragmentManager();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // startService(new Intent(this, ServiceFloating.class)); //Ashish Commented

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemTextAppearance(Utilities.getDialogTextSize(this));

        View headerLayout =
                navigationView.inflateHeaderView(R.layout.nav_header_main);//YD inflating the header for drawer, not inflating from xml because not getting the header click listener
        LinearLayout drawer_back = (LinearLayout) headerLayout.findViewById(R.id.back_drawer);
        TypeFaceFont.overrideFonts(this, headerLayout);

        Menu m = navigationView.getMenu();
        int shiftId;
        if (DataObject.resourceXmlDataStore != null) {
            Worker resObj = ((HashMap<Long, Worker>) DataObject.resourceXmlDataStore).get(PreferenceHandler.getResId(this));
            if (resObj != null)
                shiftId = resObj.getWorkerShift();
        }

        shiftId = 2;
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    if (shiftId < 2 && subMenuItem.getItemId() == R.id.manage_shift) {
                        subMenuItem.setVisible(false);
                    }
                    CustomTypefaceSpan.applyFontToMenuItem(BaseTabActivity.this, subMenuItem);
                }
            }
            if (mi.getItemId() == R.id.manage_shift && PreferenceHandler.getUiconfigShift(this).equals("0")) {
                mi.setVisible(false);
            }
            if (mi.getItemId() == R.id.drawer_route_date && PreferenceHandler.getUiconfigRouteDate(this).equals("0")) {
                mi.setVisible(false);
            }
//            if (mi.getItemId() == R.id.drawer_addOrder) {
//
//
//                if (PreferenceHandler.getUiconfigAddorder(this).equals("0")) {
//                    mi.setVisible(false);
//                } else {
//                    if (PreferenceHandler.getOrderHead(this) != null && !PreferenceHandler.getOrderHead(this).trim().equals("")) {
//                        mi.setTitle("Add " + PreferenceHandler.getOrderHead(this));
//                    }
//                }
//            }
            if (mi.getItemId() == R.id.drawer_message)
                if (PreferenceHandler.getUiconfigMessage(this).equals("0")) {
                    mi.setVisible(false);
                }
            CustomTypefaceSpan.applyFontToMenuItem(BaseTabActivity.this, mi);
        }

        drawer_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.END);
            }
        });

        TextView res_name_drawer = (TextView) headerLayout.findViewById(R.id.res_name_drawer);

        String TechName = PreferenceHandler.getPrefTechnameData(this);
        res_name_drawer.setText(TechName);

        if (DataObject.resourceXmlDataStore != null) {
            try {
                String uname = ((HashMap<Long, Worker>) DataObject.resourceXmlDataStore).get(PreferenceHandler.getResId(this)).getNm();
                res_name_drawer.setText(uname);
            } catch (Exception e) {

            }
        }

        TextView vehicleName = (TextView) headerLayout.findViewById(R.id.vehicle_nm);
        if (DataObject.siteTypeXmlDataStore != null && DataObject.resourceXmlDataStore != null) {
            String vehicleIdLst = ((HashMap<Long, Worker>) DataObject.resourceXmlDataStore).get(PreferenceHandler.getResId(this)).getVehicleId();
            seperateListOnTid((HashMap<Long, SiteType>) DataObject.siteTypeXmlDataStore);

            int dayIndexFromSun = getCurrentDayCount();

            if (dayIndexFromSun != -1 && vehicleIdLst != null && !vehicleIdLst.equals("")) {
                String vehicleDayWise[] = vehicleIdLst.split("\\|");

                String vechicleId;

                if (dayIndexFromSun == 0)
                    vechicleId = vehicleDayWise[6];
                else
                    vechicleId = vehicleDayWise[dayIndexFromSun - 1];

                String uname = siteGenTypeMapTid_11.get(vechicleId).getNm();
                vehicleName.setText(uname);
            }
        }


        TextView txt_syn_now = (TextView) headerLayout.findViewById(R.id.txt_syn_now);
        TextView Logout = (TextView) headerLayout.findViewById(R.id.logout_drawer);
        Logout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utilities.checkInternetConnection(BaseTabActivity.this, false)) {
                    showNoInternetDialog("Unable to Logout", "No internet connection is available to upload updates to server. Please try again to logout when you have stable network connection");
                } else {
                    showLogoutDialog();
                }
                drawer.closeDrawer(GravityCompat.END);
            }
        });

        LinearLayout sync_drawer = (LinearLayout) headerLayout.findViewById(R.id.layoutdrawer_sync);
        sync_drawer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utilities.checkInternetConnection(BaseTabActivity.this, false))
                    showNoInternetDialog("Slight problem with data", "No internet connection");
                else {
                    versionCheckState = "SYNC";
                    checkVersionForApp();
                }
                drawer.closeDrawer(GravityCompat.END);
            }
        });
        res_name_drawer.setTextSize(19 + (PreferenceHandler.getCurrrentFontSzForApp(this)));
        txt_syn_now.setTextSize(20 + (PreferenceHandler.getCurrrentFontSzForApp(this)));
        Logout.setTextSize(15 + (PreferenceHandler.getCurrrentFontSzForApp(this)));
        headerMain = (RelativeLayout) findViewById(R.id.header_Show);
        currentApplicationState = FOREGROUND;
        headerApp = (RelativeLayout) findViewById(R.id.ace_main_header);
        Bck_button_main = (RelativeLayout) findViewById(R.id.Bck_button_main);
        mainView = (FrameLayout) findViewById(R.id.realtabcontent);

        mImgBack = (ImageView) findViewById(R.id.back_bttn);
        mImgPanic = (ImageView) findViewById(R.id.panic_btn);
        mImgLastSync = (ImageView) findViewById(R.id.lastsync);
        mImgSyncBtn = (ImageView) findViewById(R.id.syncbtn);
        mImgVolumeBtn = (ImageView) findViewById(R.id.volumebtn);

        mTxtVwHeaderTop = (TextView) findViewById(R.id.header_top);
        mTxtVwSubHeaderTop = (TextView) findViewById(R.id.sub_header_subtop);
        mTxtVwHeaderMiddle = (TextView) findViewById(R.id.header_middle);

        TypeFaceFont.overrideFonts(this, mTxtVwHeaderTop);
        TypeFaceFont.overrideFonts(this, mTxtVwSubHeaderTop);
        TypeFaceFont.overrideFonts(this, mTxtVwHeaderMiddle);
        no_of_order = (TextView) findViewById(R.id.no_of_order);
        TypeFaceFont.overrideFonts(this, no_of_order);
        no_of_order.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(this)));

        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mBtnDone = (ImageView) findViewById(R.id.btn_Done);
        mBtnPlus = (ImageView) findViewById(R.id.B_plus);
        mSwapButton = (ImageView) findViewById(R.id.calenderSwap);
        mBtnMapAll = (Button) findViewById(R.id.btn_mapall);
        mBtnTrash = (ImageView) findViewById(R.id.trash);
        mBtnMapSetting = (ImageView) findViewById(R.id.btn_mapstyle);
        mBtnMapSetting_tree = (ImageView) findViewById(R.id.btn_mapstyle_tree);

        mBtnMapOdrTask = (Button) findViewById(R.id.btn_mapodrtask);
        mBtnTaskTreeMap = (Button) findViewById(R.id.btn_tasktreeMap);
        mBtnTaskUserMap = (Button) findViewById(R.id.btn_taskuserMap);

        //	mImgBack.setOnClickListener(this);
        Bck_button_main.setOnClickListener(this);
        mImgPanic.setOnClickListener(this);
        mImgLastSync.setOnClickListener(this);
        mImgSyncBtn.setOnClickListener(this);
        mBtnDone.setOnClickListener(this);
        mBtnPlus.setOnClickListener(this);
        mBtnMapAll.setOnClickListener(this);
        mBtnMapSetting.setOnClickListener(this);
        mBtnMapSetting_tree.setOnClickListener(this);
        mBtnMapOdrTask.setOnClickListener(this);
        mBtnTaskTreeMap.setOnClickListener(this);
        mBtnTaskUserMap.setOnClickListener(this);
        mImgVolumeBtn.setOnClickListener(this);
        mSwapButton.setOnClickListener(this);
        mBtnTrash.setOnClickListener(this);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

        if (savedInstanceState == null) {
            mJobFragment = new OrderListMainFragment();//419c7260
            mMapFragment = new MapFragment();
            mopOptionFragment = new OptionFragment();
            mClockInFragment = new ClockInFragment();
            mStacks = new HashMap<String, Stack<Fragment>>();

            mStacks.put(Utilities.JOBS,
                    new Stack<Fragment>());
            mStacks.put(Utilities.MAP,
                    new Stack<Fragment>());
            mStacks.put(Utilities.CLOCKIN,
                    new Stack<Fragment>());
            mStacks.put(Utilities.OPTION,
                    new Stack<Fragment>());

            mTabHost.setOnTabChangedListener(this);
        } else {
            mJobFragment = new OrderListMainFragment();
            mMapFragment = new MapFragment();
            mopOptionFragment = new OptionFragment();
            mClockInFragment = new ClockInFragment();
            mStacks = new HashMap<String, Stack<Fragment>>();

            mStacks.put(Utilities.JOBS,
                    new Stack<Fragment>());
            mStacks.put(Utilities.MAP,
                    new Stack<Fragment>());
            mStacks.put(Utilities.CLOCKIN,
                    new Stack<Fragment>());
            mStacks.put(Utilities.OPTION,
                    new Stack<Fragment>());
            mTabHost.setOnTabChangedListener(this);
            mJobFragment = (OrderListMainFragment) getSupportFragmentManager()
                    .findFragmentByTag(Utilities.JOBS);
            mMapFragment = (MapFragment) getSupportFragmentManager()
                    .findFragmentByTag(
                            Utilities.MAP);
            mClockInFragment = (ClockInFragment) getSupportFragmentManager()
                    .findFragmentByTag(Utilities.CLOCKIN);
            mopOptionFragment = (OptionFragment) getSupportFragmentManager()
                    .findFragmentByTag(Utilities.OPTION);

        }

        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        initializeTabs();
        TypeFaceFont.overrideFonts(this, mTabHost);
        setSyncTimeOnTitle();
    }

    private void seperateListOnTid(HashMap<Long, SiteType> responseMap) {
        Long keys[] = responseMap.keySet().toArray(new Long[responseMap.size()]);
        for (int i = 0; i < responseMap.size(); i++) {
            int tid = responseMap.get(keys[i]).getTid();
            switch (tid) {
                case 11:
                    siteGenTypeMapTid_11.put(keys[i], responseMap.get(keys[i]));
                    break;
                case 10:
                    siteGenTypeMapTid_10.put(keys[i], responseMap.get(keys[i]));
            }
        }
    }

    private void setSyncTimeOnTitle() {

        long lastSyncTime = PreferenceHandler.getlastsynctime(this);
        if (lastSyncTime != 0) {
            Date date;
            date = new Date(Long.valueOf(lastSyncTime));

            if (PreferenceHandler.getOdrGetDate(this) != null && !PreferenceHandler.getOdrGetDate(this).equals(""))
                date = new Date(PreferenceHandler.getodrLastSyncForDt(this));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d yyyy");//Jun 21 2015
            String headerDate = simpleDateFormat.format(date);
            String headerTime = Utilities.convertDateToAmPM(date.getHours(), date.getMinutes());
            final String headerDay = BaseTabActivity.getDayFrmDate(date.getDay());
            setHeaderTitle(headerDate, "", headerDay);
        }
    }

    private int getCurrentDayCount() {

        long lastSyncTime = PreferenceHandler.getlastsynctime(this);
        if (lastSyncTime != 0) {
            Date date;
			/*Long lastPubnubUpdateTime =PreferenceHandler.getLastPubnubUpdated(mActivity.getApplicationContext());
			if(Long.valueOf(lastSyncTime)>lastPubnubUpdateTime)*/
            date = new Date(Long.valueOf(lastSyncTime));

            if (PreferenceHandler.getOdrGetDate(this) != null && !PreferenceHandler.getOdrGetDate(this).equals(""))
                date = new Date(PreferenceHandler.getodrLastSyncForDt(this));
            return date.getDay();
        }
        return -1;
    }

    @Override
    protected void onStart() {
        super.onStart();
        settingsrequest();
        Log.e("Aceroute", "onStart called for BaseTabActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityVisisble = true;
        Log.e("Aceroute", "onResume called for BaseTabActivity");
        //Logger.enableLogs(/*((BaseTabActivity) this.getActivity()).*/getApplicationContext(), true);//YD also remove code for delete the logs from aceofflineSyncEngine
        PubnubHandler.setUiHandle(this);
        currentApplicationState = FOREGROUND;
        //  startService(new Intent(this, ServiceFloating.class));
        AceRequestHandler.lstOfOdrUpdated.clear(); //YD clear the list_cal in which storing the notification messages

    }

    @SuppressLint("NewApi")
    private void initializeTabs() {
        Bundle bundle = new Bundle();
        bundle.putString("key", Utilities.JOBS);
        bundle.putString("REQUESTTYPEPAGE", "INBOX");
        TabHost.TabSpec spec = mTabHost.newTabSpec(Utilities.JOBS);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });

        mTabHost.addTab(mTabHost.newTabSpec(getResources().getString(R.string.tab_job)).setIndicator(
                        getTabIndicator(mTabHost.getContext(), R.string.tab_job, R.drawable.tab_job_selector)),
                OrderListMainFragment.class, bundle);

        no_of_order.setVisibility(View.VISIBLE);

        // creating create order tab
        bundle = new Bundle();
        bundle.putString("key", Utilities.MAP);
        bundle.putString("x", "1");
        spec = mTabHost.newTabSpec(Utilities.MAP);

        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });

        mTabHost.addTab(mTabHost.newTabSpec(getResources().getString(R.string.tab_map)).setIndicator(getTabIndicator(mTabHost.getContext(), R.string.tab_map, R.drawable.tab_map_selector)), MapFragment.class, bundle);

        // creating create clock in tab
        bundle = new Bundle();
        bundle.putString("key", Utilities.CLOCKIN);
        bundle.putString("REQUESTTYPEPAGE", "CLOCKIN");
        spec = mTabHost.newTabSpec(Utilities.CLOCKIN);

        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });

        mTabHost.addTab(mTabHost.newTabSpec(getResources().getString(R.string.tab_clockin)).setIndicator(getTabIndicator(mTabHost.getContext(), getTabString(), getTabImage())), ClockInFragment.class, bundle);


        // creating create option tab
        bundle = new Bundle();
        bundle.putString("key", Utilities.OPTION);
        spec = mTabHost.newTabSpec(Utilities.OPTION);

        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.realtabcontent);
            }
        });
        mTabHost.addTab(
                mTabHost.newTabSpec(getResources().getString(R.string.tab_opt)).setIndicator(
                        getTabIndicator(mTabHost.getContext(), R.string.tab_opt, R.drawable.tab_options_selector)),
                OptionFragment.class, bundle);

        // setting properties of tab
        mTabHost.setCurrentTab(0);
        mTabHost.getTabWidget().setStripEnabled(false);
        mTabHost.getTabWidget().setDividerDrawable(null);
        mTabHost.getTabWidget().setFadingEdgeLength(0);

        if (Integer.parseInt(Build.VERSION.SDK) >= Build.VERSION_CODES.HONEYCOMB) {
            mTabHost.getTabWidget()
                    .setShowDividers(TabWidget.SHOW_DIVIDER_NONE);
        }
        View moreview = mTabHost.getTabWidget().getChildAt(3);
        moreview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawer.isDrawerOpen(Gravity.RIGHT)) {
                    drawer.openDrawer(Gravity.RIGHT);
                } else {
                    drawer.closeDrawer(GravityCompat.END);
                }
            }
        });

        View clockoutview = mTabHost.getTabWidget().getChildAt(2);
        clockoutview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceHandler.getUiconfigUAuto_Clock_IN(BaseTabActivity.this) != null && PreferenceHandler.getUiconfigUAuto_Clock_IN(BaseTabActivity.this).trim().equals("1")) {
                    return;
                }

                if (PreferenceHandler.getClockInStat(BaseTabActivity.this) != null && PreferenceHandler.getClockInStat(BaseTabActivity.this).equals(CheckinOut.CLOCK_IN)) {
                    ClockInOutRequest clkInOut = new ClockInOutRequest();
                    clkInOut.setTid("0");
                    clkInOut.setType("post");
                    clkInOut.setAction(CheckinOut.CC_ACTION);
                    CheckinOut.getData(clkInOut, BaseTabActivity.this, BaseTabActivity.this, CHECKOUT_REQ);
                } else {
                    // Request for checkin
                    ClockInOutRequest clkInOut = new ClockInOutRequest();

                    clkInOut.setTid("1");
                    clkInOut.setType("post");
                    clkInOut.setAction(CheckinOut.CC_ACTION);
                    CheckinOut.getData(clkInOut, BaseTabActivity.this, BaseTabActivity.this, CHECKIN_REQ);


                }
            }
        });

        if (PreferenceHandler.getUiconfigUAuto_Clock_IN(this) != null && PreferenceHandler.getUiconfigUAuto_Clock_IN(this).trim().equals("1")) {
            final Handler h1 = new Handler();
            h1.post(new Runnable() {
                @Override
                public void run() {
                    requestCheckIn();
                    h1.removeCallbacks(this);
                }
            });
        }
    }

    private void requestCheckIn() {
        // Request for checkin
        ClockInOutRequest clkInOut = new ClockInOutRequest();
        clkInOut.setTid("1");
        clkInOut.setType("post");
        clkInOut.setAction(CheckinOut.CC_ACTION);
        CheckinOut.getData(clkInOut, BaseTabActivity.this, BaseTabActivity.this, CHECKIN_REQ);
    }

    private int getTabString() {
        if (PreferenceHandler.getClockInStat(this) != null && PreferenceHandler.getClockInStat(this).equals(CheckinOut.CLOCK_IN)) {
            int edition = PreferenceHandler.getPrefEditionForGeo(this);
            if (edition > 299) {
                //Logger.i("AceRoute Geo" , "<<<<<<<<<<<<<<<<<Starting new Alarm >>>>>>>>>>>>>>>>>>>>>>>>>>>");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Utilities.startAlarmManager(this);
                } else {
                    Utilities.startGeoSyncAlarm(getApplicationContext());//YD 2020 commenting because doze restriction to save battery has introduced by google which restricts the alarm manager to work when app goes in background but it does work when device is connect to charging.
                }
            }
            return R.string.tab_clockin;
        } else
            return R.string.tab_clockout;
    }

    private int getTabImage() {
        if (PreferenceHandler.getClockInStat(this) != null && PreferenceHandler.getClockInStat(this).equals(CheckinOut.CLOCK_IN)) {
            int edition = PreferenceHandler.getPrefEditionForGeo(this);
            if (edition > 299) {
                //Logger.i("AceRoute Geo" , "<<<<<<<<<<<<<<<<<Starting new Alarm >>>>>>>>>>>>>>>>>>>>>>>>>>>");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Utilities.startAlarmManager(this);
                } else {
                    Utilities.startGeoSyncAlarm(getApplicationContext());//YD 2020 commenting because doze restriction to save battery has introduced by google which restricts the alarm manager to work when app goes in background but it does work when device is connect to charging.
                }
            }
            return R.drawable.clock_active;
        } else
            return R.drawable.clock_nonactive;
    }

    private View getTabIndicator(Context context, int title, int icon) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        ImageView iv = (ImageView) view.findViewById(R.id.tabHostbottomimgVw);
        iv.setImageResource(icon);
        TextView tv = (TextView) view.findViewById(R.id.tabHostbottomtxtVew);
        tv.setText(title);
        tv.setTextSize(15 + (PreferenceHandler.getCurrrentFontSzForApp(mBaseTabActivity)));
        return view;
    }

    public void ChangeCheckout(int type) {
        final TextView t1 = (TextView) mTabHost.getTabWidget().getChildAt(2).findViewById(R.id.tabHostbottomtxtVew);

        final ImageView t2 = (ImageView) mTabHost.getTabWidget().getChildAt(2).findViewById(R.id.tabHostbottomimgVw);
        if (type == 0) {
            t1.setText(getString(R.string.tab_clockin));
            t2.setImageResource(R.drawable.clock_active);
            t1.setTextColor(Color.parseColor("#1ABC9C"));


        } else {
            t1.setText(getString(R.string.tab_clockout));
            t2.setImageResource(R.drawable.clock_nonactive);
            t1.setTextColor(Color.parseColor("#ffffff"));

        }
    }

    @Override
    public void onTabChanged(String tabId) {
        Log.e("AceRoute", "CurrentTab :" + String.valueOf(tabId));
        mTabHost.getCurrentTab();
        mCurrentTab = tabId;

        try {
            if (tabId.equals(Utilities.CLOCKIN)) {
                setTitle(tabId);
                if (PreferenceHandler.getClockInStat(this) != null && PreferenceHandler.getClockInStat(this).equals(CheckinOut.CLOCK_IN)) {
                    final TextView t1 = (TextView) mTabHost.getTabWidget().getChildAt(2).findViewById(R.id.tabHostbottomtxtVew);
                    t1.setText("Clock In");
                    final ImageView t2 = (ImageView) mTabHost.getTabWidget().getChildAt(2).findViewById(R.id.tabHostbottomimgVw);
                    t2.setImageResource(R.drawable.clock_nonactive);
                } else {
                    final TextView t1 = (TextView) mTabHost.getTabWidget().getChildAt(2).findViewById(R.id.tabHostbottomtxtVew);
                    t1.setText("Clock Out");
                    // t1.setTextColor(Color.parseColor("#000"));
                }
            }


            if (mStacks.get(tabId).size() == 0) {
                Log.e("AceRoute", "Stack Size of " + tabId + "  is :" + mStacks.get(tabId).size());

                if (tabId.equals(Utilities.JOBS)) {
                    no_of_order.setVisibility(View.VISIBLE);
                    mStacks.get(tabId).push(mJobFragment);
                    setHeaderForFragments(mJobFragment);
                    currentActiveFrag = mJobFragment;
                    mTabHost.setVisibility(View.VISIBLE);
                    //registerHeader((HeaderInterface)mJobFragment);// YD Removing because making mactivity on olmp null when opend first time
                    setTmeFrOLMPHeader(getApplicationContext());
                    mImgSyncBtn.setVisibility(View.GONE);
                } else if (tabId.equals(Utilities.MAP)) {
                    if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                        drawer.closeDrawer(GravityCompat.END);
                    }
                    no_of_order.setVisibility(View.VISIBLE);
                    mStacks.get(tabId).push(mMapFragment);
                    mMapFragment.changedOdrLocMap.clear();
                    setHeaderForFragments(mMapFragment);
                    currentActiveFrag = mMapFragment;
                    //pushFragments(tabId, mCreateOrderFragment, false, true);
                } else if (tabId.equals(Utilities.CLOCKIN)) {
                    if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                        drawer.closeDrawer(GravityCompat.END);
                    }
                    Log.e("TAG :", "Clock In");
                    no_of_order.setVisibility(View.VISIBLE);
                    mStacks.get(tabId).push(mClockInFragment);
                    setHeaderForFragments(mClockInFragment);
                    currentActiveFrag = mClockInFragment;
                    //pushFragments(tabId, mClockInFragment, false, true);
                } else if (tabId.equals(Utilities.OPTION)) {
                    Log.e("TAG :", "Option Out");
                    no_of_order.setVisibility(View.VISIBLE);
                    mStacks.get(tabId).push(mopOptionFragment);
                    setHeaderForFragments(mopOptionFragment);
                    currentActiveFrag = mopOptionFragment;
                    //pushFragments(tabId, mopOptionFragment, false, true);
                }
            } else {

                if (mStacks.get(tabId).size() == 1) {
                    // YD no need to push fragment if the size is zero otherwise fragment will be called twice
                    if (tabId.equals(Utilities.JOBS)) {
                        setHeaderForFragments(mJobFragment);
                        mImgSyncBtn.setVisibility(View.GONE);//YD GONE because comeing back from footer map showing syncBtn
                        currentActiveFrag = mJobFragment;
                        mTabHost.setVisibility(View.VISIBLE);
                    } else if (tabId.equals(Utilities.MAP)) {
                        setHeaderForFragments(mMapFragment);
                        currentActiveFrag = mMapFragment;
                        mTabHost.setVisibility(View.VISIBLE);
                    }
                } else if (pushFragToStackRequired == 0)  //YD for handling ODP when opening from new order creation
                {
                    pushFragments(tabId, mStacks.get(tabId).lastElement(), false,
                            false, UI_Thread);
                } else {
                    pushFragToStackRequired = 0;
                }
            }
            setTabTextColor(tabId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setTabTextColor(String tabId) {
        if (!tabId.equals(Utilities.CLOCKIN)) {
            for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
                TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(R.id.tabHostbottomtxtVew);
                tv.setTextColor(Color.parseColor("#ffffff")); // unselected
            }
            TextView t1 = null;
            if (tabId.equals(Utilities.JOBS))
                t1 = (TextView) mTabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabHostbottomtxtVew);
            else if (tabId.equals(Utilities.MAP))
                t1 = (TextView) mTabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabHostbottomtxtVew);
            else if (tabId.equals(Utilities.OPTION))
                t1 = (TextView) mTabHost.getTabWidget().getChildAt(3).findViewById(R.id.tabHostbottomtxtVew);

            t1.setTextColor(Color.parseColor("#1ABC9C"));
        }
    }

    public void pushFragments(String tag, Fragment fragment, boolean shouldAnimate, boolean shouldAdd, int requestFrom) {

        Log.e("AceRoute", "pushFragments called for :" + fragment + " Should add to stack : " + shouldAdd);
    /*    if(!isActivityVisisble) {
            return;
        }*/
        // YD doing work for map option
        if ((fragment instanceof GoogleMapFragment || fragment instanceof MapAllFragment /*||   //YD commented arcmap
				fragment instanceof ArcgisMap*/ || fragment instanceof OrderAssetFragment) && addedNewMap) {
            BaseTabActivity.addedNewMap = false;
            Fragment fragmentprev = mStacks.get(mCurrentTab).elementAt(
                    mStacks.get(mCurrentTab).size() - 1);
            if (fragmentprev instanceof GoogleMapFragment || fragmentprev instanceof MapAllFragment /*||   //YD commented arcmap
					fragmentprev instanceof ArcgisMap*/ || fragmentprev instanceof OrderAssetFragment) {
                mStacks.get(mCurrentTab).pop();

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.addToBackStack(null);
                ft.remove(fragmentprev);
                ft.commit();
			/*	fragmentprev = mStacks.get(mCurrentTab).elementAt(
						mStacks.get(mCurrentTab).size() - 1);*/ //YD using because we have 3 maps now
            }
        }

        // check if activeOrderobj changes reflect in the original orderMapDataStore
        if (fragment instanceof ManageShift || fragment instanceof CountryListFragment || fragment instanceof CreateOrderFragment) {
            if (fragment instanceof OrderDetailFragment)
                ((OrderDetailFragment) fragment).setActiveOrderObj(activeOrderObj);

            if (mTabHost.getCurrentTab() != 0) {
                if (requestFrom == BaseTabActivity.SERVICE_Thread) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTabHost.setCurrentTab(0);
                        }
                    });
                } else
                    mTabHost.setCurrentTab(0); // YD Using when saving order and moving toward OrderDetailPage to maintain stack and fragment states
            }
        }


        currentActiveFrag = fragment;
        if (shouldAdd)
            mStacks.get(tag).push(fragment);// adding to hashmap
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.addToBackStack(null);
        /*
         * if (shouldAnimate) ft.setCustomAnimations(R.anim.slide_in_right,
         * R.anim.slide_out_left);
         */

        ft.add(R.id.realtabcontent, fragment);
        //ft.replace(R.id.realtabcontent, fragment);
        ft.commit();

        if (fragment instanceof AddEditTaskOrderFragment
                || fragment instanceof AddEditPartsFragment
                || fragment instanceof AddEditFormFragment
                || fragment instanceof CustomerDetailFragment
                || fragment instanceof OrderPartsFragment
                || fragment instanceof OrderDetailFragment
                || fragment instanceof OrderFormsFragment
                || fragment instanceof ScheduledDetailFragment
                || fragment instanceof CustomerListActivity
                || fragment instanceof Sitelist
                || fragment instanceof AddSiteActy
                || fragment instanceof Gridview_MainActivity
                || fragment instanceof OrderPicSaveActivity
                || fragment instanceof Addsign
                || fragment instanceof MapAllFragment
                || fragment instanceof GoogleMapFragment
                || fragment instanceof OrderNote
                || fragment instanceof Createnote
                || fragment instanceof OrderDetailAudioListFragment
                || fragment instanceof RecordAudioFragment
                || fragment instanceof PlayAudioFragment
//                || fragment instanceof AddEditTaskOldFragment
                || fragment instanceof OrderTasksOldFragment
                || fragment instanceof CaptureSignature
                || fragment instanceof CountryListFragment
                || fragment instanceof CityListFragment
                || fragment instanceof EditContactFrag
                //|| fragment instanceof ArcgisMap   //YD commented arcmap
                || fragment instanceof AddTaskSpeciesFrag
                || fragment instanceof AlertnNoteFrag
                || fragment instanceof OrderDetailFrag
                || fragment instanceof OrderAssetFragment
                || fragment instanceof AddEditAssetFragment
                || fragment instanceof CreateOrderFragment
                || fragment instanceof ManageShift
                || fragment instanceof ShifthourFragment
                || fragment instanceof MessageSos
                || fragment instanceof ManagePassword
                || fragment instanceof UnavailableFragment
                || fragment instanceof SwapingFragment
                || fragment instanceof TimeOffFragment) {
            /*
             * for (int i = 0; i < 4; i++) {
             * mTabHost.getTabWidget().getChildTabViewAt(i)
             * .setVisibility(View.GONE);
             *
             * }
             */
            stopService(new Intent(this, ServiceFloating.class)); //Ashish Commented
            if (requestFrom == 1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTabHost.setVisibility(View.GONE);
                    }
                });
            } else
                mTabHost.setVisibility(View.GONE);


        } else {
            /*
             * for (int i = 0; i < 4; i++) {
             * mTabHost.getTabWidget().getChildTabViewAt(i)
             * .setVisibility(View.VISIBLE);
             *
             * }
             */
            //  startService(new Intent(this, ServiceFloating.class)); //Ashish Comment
            if (requestFrom == BaseTabActivity.SERVICE_Thread) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTabHost.setVisibility(View.VISIBLE);
                    }
                });
            } else
                mTabHost.setVisibility(View.VISIBLE);
        }
        setHeaderForFragments(fragment);


        if (fragment instanceof OrderListMainFragment) { // YD doing this because some time oncreateview of olmp is not get called see the issue and fix it later
            mImgSyncBtn.setVisibility(View.GONE);
            no_of_order.setVisibility(View.VISIBLE);
            setTmeFrOLMPHeader(getApplicationContext());
        }

        Log.i("Aceroute", "Done manupulating object");
    }

    public Fragment popFragments(int requestFrom) {
        try {
            if (mStacks.get(mCurrentTab).size() == 1) {
                if (mCurrentTab.equals(Utilities.MAP) ||
                        mCurrentTab.equals(Utilities.CLOCKIN) ||
                        mCurrentTab.equals(Utilities.OPTION)) {
                    Log.e("AceRoute", "PoPFragments called for withTab size one, Tab is : " + mCurrentTab);
                    mStacks.get(mCurrentTab).pop();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTabHost.setCurrentTab(0);
                            no_of_order.setVisibility(View.VISIBLE);
								/*if(!tempmCurrentTab.equals(Utilities.CLOCKIN)) // YD if the current tab clicked is clock the no need to call resume function
									mStacks.get(mCurrentTab).elementAt(0).onResume();// YD calling onresume of olmp
*/
                        }
                    });
                }

				/*else
					finish();*/
            } else {


                Fragment fragment = mStacks.get(mCurrentTab).elementAt(mStacks.get(mCurrentTab).size() - 2);
                Fragment fragment2Replace = mStacks.get(mCurrentTab).elementAt(mStacks.get(mCurrentTab).size() - 1);

                Log.e("AceRoute", "PoPFragments called replacing with :" + fragment + " from Tab : " + mCurrentTab);
                currentActiveFrag = fragment;

                if (fragment instanceof AddEditTaskOrderFragment
                        || fragment instanceof AddEditPartsFragment
                        || fragment instanceof AddEditFormFragment
                        || fragment instanceof CustomerDetailFragment
                        || fragment instanceof OrderPartsFragment
                        || fragment instanceof OrderDetailFragment
                        || fragment instanceof OrderFormsFragment
                        || fragment instanceof ScheduledDetailFragment
                        || fragment instanceof CustomerListActivity
                        || fragment instanceof Sitelist
                        || fragment instanceof AddSiteActy
                        || fragment instanceof Gridview_MainActivity
                        || fragment instanceof OrderPicSaveActivity
                        || fragment instanceof Addsign
                        || fragment instanceof MapAllFragment
                        || fragment instanceof GoogleMapFragment
                        || fragment instanceof OrderNote
                        || fragment instanceof Createnote
                        || fragment instanceof OrderDetailAudioListFragment
                        || fragment instanceof RecordAudioFragment
                        || fragment instanceof PlayAudioFragment
//                        || fragment instanceof AddEditTaskOldFragment
                        || fragment instanceof OrderTasksOldFragment
                        || fragment instanceof CaptureSignature
                        || fragment instanceof CountryListFragment
                        || fragment instanceof CityListFragment
                        || fragment instanceof AddTaskSpeciesFrag
                        || fragment instanceof EditContactFrag
                        || fragment instanceof AlertnNoteFrag
                        || fragment instanceof OrderDetailFrag
                        || fragment instanceof OrderAssetFragment
                        || fragment instanceof AddEditAssetFragment
                        || fragment instanceof ManageShift
                        || fragment instanceof ShifthourFragment
                        || fragment instanceof UnavailableFragment
                        || fragment instanceof TimeOffFragment
                        || fragment instanceof SwapingFragment
                        //|| fragment instanceof ArcgisMap    //YD commen
                        || fragment instanceof CreateOrderFragment) {

                    mTabHost.setVisibility(View.GONE);
                    stopService(new Intent(this, ServiceFloating.class)); //Ashish comment

                } else {
                    // startService(new Intent(this, ServiceFloating.class)); //Ashish Commented
                    if (requestFrom == 1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTabHost.setVisibility(View.VISIBLE);
                            }
                        });
                    } else
                        mTabHost.setVisibility(View.VISIBLE);

                }

                mStacks.get(mCurrentTab).pop();

                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.addToBackStack(null);
                // ft.setCustomAnimations(R.anim.slide_in_left,
                // R.anim.slide_out_right);
                ft.remove(fragment2Replace);
                //ft.add(R.id.realtabcontent, fragment);//YD
                //   ft.replace(R.id.realtabcontent, fragment);
                ft.commit();
                setHeaderForFragments(fragment);
                //fragment.onResume();YD

                if (requestFrom == 15) {
                    if (fragment instanceof ManageShift) {
                        ((ManageShift) fragment).getrefreshList();
                        Log.i("SHIFT", "REFRESHLIST FROM Basetabactivoity");
                    }
                    if (fragment instanceof SwapingFragment) {
                        ((SwapingFragment) fragment).getrefreshList();
                        Log.i("SHIFT", "REFRESHLIST FROM Basetabactivoity");
                    }

                }

                if (fragment instanceof OrderListMainFragment) { // YD doing this because some time oncreateview of olmp is not get called see the issue and fix it later

                    if (requestFrom == 1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                currentApplicationState = 1;
                                mImgSyncBtn.setVisibility(View.GONE);
                                setTmeFrOLMPHeader(getApplicationContext());
                            }
                        });
                    } else {
                        mImgSyncBtn.setVisibility(View.GONE);
                        setTmeFrOLMPHeader(getApplicationContext());
                    }
                }
                loadData(fragment, mBaseTabActivity, fragment2Replace);
                return fragment;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadData(final Fragment fragment, final BaseTabActivity context, Fragment fragment2Replace) {
        setHeaderforFrag(fragment, context, fragment2Replace);

        Handler handler = new Handler(Looper.getMainLooper());// YD hiding loading after one second (Client requirement)
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (fragment instanceof OrderListMainFragment) {
                    ((OrderListMainFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof OptionFragment) {
                    ((OptionFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof ClockInFragment) {
                    ((ClockInFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof AddEditTaskOrderFragment) {
                    ((AddEditTaskOrderFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof AddEditPartsFragment) {
                    ((AddEditPartsFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof CustomerDetailFragment) {
                    if (SplashII.wrk_tid >= 2) {
                        mBtnPlus.setVisibility(View.GONE);
                    }
                    ((CustomerDetailFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof OrderPartsFragment) {
                    if (SplashII.wrk_tid >= 5) {
                        //          mBtnPlus.setVisibility(View.GONE);
                    }
                    ((OrderPartsFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof OrderDetailFragment) {
                    ((OrderDetailFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof OrderFormsFragment) {
                   // if (SplashII.wrk_tid >= 5)
                        //  mBtnPlus.setVisibility(View.GONE);
                        ((OrderFormsFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof ScheduledDetailFragment) {
                    ((ScheduledDetailFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof CustomerListActivity) {
                    ((CustomerListActivity) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof Sitelist) {
                    ((Sitelist) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof AddSiteActy) {
                    ((AddSiteActy) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof Gridview_MainActivity) {
                    if (SplashII.wrk_tid >= 8)
                        mBtnPlus.setVisibility(View.GONE);
                    else
                        mBtnPlus.setVisibility(View.VISIBLE);
                    ((Gridview_MainActivity) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof OrderPicSaveActivity) {
                    ((OrderPicSaveActivity) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof AddEditFormFragment) {
                    ((AddEditFormFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof Addsign) {
                    ((Addsign) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof OrderNote) {
                    ((OrderNote) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof MapAllFragment) {
                    ((MapAllFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof GoogleMapFragment) {
                    ((GoogleMapFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof Createnote) {
                    ((Createnote) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof OrderDetailAudioListFragment) {
                    ((OrderDetailAudioListFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof RecordAudioFragment) {
                    ((RecordAudioFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof PlayAudioFragment) {
                    ((PlayAudioFragment) fragment).loadDataOnBack(context);
                }
               /* if (fragment instanceof AddEditTaskOldFragment) {
                    ((AddEditTaskOldFragment) fragment).loadDataOnBack(context);
                }*/
                if (fragment instanceof OrderTasksOldFragment) {
                    ((OrderTasksOldFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof CaptureSignature) {
                    ((CaptureSignature) fragment).loadDataOnBack(context);
                }

                if (fragment instanceof CountryListFragment) {
                    ((CountryListFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof CityListFragment) {
                    ((CityListFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof AddTaskSpeciesFrag) {
                    ((AddTaskSpeciesFrag) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof CreateOrderFragment) {
                    ((CreateOrderFragment) fragment).loadDataOnBack(context);
                }
                if (fragment instanceof OrderAssetFragment) {
                    if (SplashII.wrk_tid >= 5)
                        mBtnPlus.setVisibility(View.GONE);
                    ((OrderAssetFragment) fragment).loadDataOnBack(context);
                }
				/*if ( fragment instanceof ArcgisMap){
				 ((ArcgisMap)fragment).loadDataOnBack(context);}*/  //YD commented arcmap
            }
        }, 200);
        if (fragment instanceof MapFragment) {
            ((MapFragment) fragment).loadDataOnBack(context, false);
        }
    }

    //YD using because causing delay to header text when coming back to some fragments
    private void setHeaderforFrag(Fragment fragment, BaseTabActivity context, Fragment fragment2Replace) {
        String headerText = "";

        if (fragment instanceof OrderFormsFragment) {
            //YD adding because causing delay when coming back to order task from addeditTaskfrag
            headerText = PreferenceHandler.getFormHead(getApplicationContext());//mBundle.getString("HeaderText");
            if (headerText != null && !headerText.equals(""))
                headerText = headerText + "S";
        }
        if (fragment instanceof OrderPartsFragment) {
            //YD adding because causing delay when coming back to order part from addeditpartfrag
            headerText = PreferenceHandler.getPartHead(getApplicationContext());//mBundle.getString("HeaderText");
            if (headerText != null && !headerText.equals(""))
                headerText = headerText + "S";
        }
        if (fragment instanceof OrderAssetFragment) {
            headerText = PreferenceHandler.getAssetsHead(getApplicationContext());//mBundle.getString("HeaderText");
            if (headerText != null && !headerText.equals(""))
                headerText = headerText + "S";
        }
        if (fragment instanceof Gridview_MainActivity) {
            if (fragment2Replace instanceof PlayAudioFragment || fragment2Replace instanceof RecordAudioFragment) {
                //headerText = "AUDIOS";
                headerText = PreferenceHandler.getAudioHead(this);
            }
            if (fragment2Replace instanceof Addsign) {
                headerText = PreferenceHandler.getSignatureHead(this);
            }
        }

        if (!headerText.equals(""))
            setHeaderTitle("", headerText, "");
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.RIGHT)) {
            drawer.closeDrawer(GravityCompat.END);
            return;
        }

        if (mStacks.get(mCurrentTab) != null && mStacks.get(mCurrentTab).lastElement() != null
                && ((BaseFragment) mStacks.get(mCurrentTab).lastElement()).onBackPressed() == false) {

            if (mStacks.get(mCurrentTab).size() == 1) {
                if (mCurrentTab.equals(Utilities.MAP) ||
                        mCurrentTab.equals(Utilities.CLOCKIN) ||
                        mCurrentTab.equals(Utilities.OPTION)) {
                    Log.e("AceRoute", "onBackPressed called for withTab size one, Tab is : " + mCurrentTab);

                    mStacks.get(mCurrentTab).pop();
                    if (mCurrentTab.equals(Utilities.MAP)) {
                        mStacks.get(Utilities.JOBS).clear();
                    }

                    mTabHost.setCurrentTab(0);
                } else
                    finish();
            } else {
                Log.e("AceRoute", "onBackPressed called for withTab size more than one, Tab is : " + mCurrentTab);
                if (((BaseFragment) mStacks.get(mCurrentTab).lastElement()) instanceof RecordAudioFragment ||
                        ((BaseFragment) mStacks.get(mCurrentTab).lastElement()) instanceof PlayAudioFragment ||
                        ((BaseFragment) mStacks.get(mCurrentTab).lastElement()) instanceof OrderPartsFragment ||
                        ((BaseFragment) mStacks.get(mCurrentTab).lastElement()) instanceof MapAllFragment ||
                        ((BaseFragment) mStacks.get(mCurrentTab).lastElement()) instanceof GoogleMapFragment ||
                        ((BaseFragment) mStacks.get(mCurrentTab).lastElement()) instanceof AddEditTaskOrderFragment) {
                    headerEventbtn.headerClickListener(HeaderBackPressed);
                }

                Fragment frag = ((BaseFragment) mStacks.get(mCurrentTab).lastElement());
                if (!(frag instanceof AddEditTaskOrderFragment) &&
                        !(frag instanceof MapAllFragment) &&
                        !(frag instanceof GoogleMapFragment)) {
                    popFragments(UI_Thread);
                } else {
                    if (((BaseFragment)
                            mStacks.get(mCurrentTab).lastElement()) instanceof
                            AddEditTaskOrderFragment && AddEditTaskOrderFragment.fieldUpdated == 0) {
                        popFragments(UI_Thread);
                    } else if (((BaseFragment)
                            mStacks.get(mCurrentTab).lastElement()) instanceof
                            MapAllFragment && MapAllFragment.FIELD_UPDATED == 0) {
                        popFragments(UI_Thread);
                    } else if (((BaseFragment)
                            mStacks.get(mCurrentTab).lastElement()) instanceof
                            GoogleMapFragment && GoogleMapFragment.FIELD_UPDATED == 0) {
                        popFragments(UI_Thread);
                    }
                }
            }
        }
    }

    @Override
    public void ServiceStarter(RespCBandServST activity, Intent intent) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setResponseCallback(String response, Integer reqId) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        isActivityVisisble = false;
        Log.e("Aceroute", "onPause called for BaseTabActivity");
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        Log.e("Aceroute", "onRestart called for BaseTabActivity");
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Log.e("Aceroute", "onStop called for BaseTabActivity");
        //PubnubHandler.setUiHandle(null);
        currentApplicationState = BACKGROUND;
        stopService(new Intent(this, ServiceFloating.class)); //Ashish Commented
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.e("Aceroute", "onDestroy called for BaseTabActivity");

        if (PreferenceHandler.getRemember(this) == false) {
            Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.clear();
            editor.commit();
        }
        if (customDialog != null) {
            customDialog.dismiss();
            customDialog = null;
        }
    }

    private void setHeaderForFragments(final Fragment fragment) {


        if (fragment instanceof OrderListMainFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    no_of_order.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setTextSize(19 + (PreferenceHandler.getCurrrentFontSzForApp(mBaseTabActivity)));
                    mTxtVwSubHeaderTop.setVisibility(View.VISIBLE);
                    mTxtVwSubHeaderTop.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(mBaseTabActivity)));
                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    //	mImgBack.setVisibility(View.GONE);
                    Bck_button_main.setVisibility(View.GONE);
                    mTxtVwHeaderMiddle.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);

                    if (PreferenceHandler.getCurrrentFontSzForApp(mBaseTabActivity) == 6) {
                        //headerMain.
                    }
                }
            });
        } else if (fragment instanceof ManageShift) {
            runOnUiThread(new Runnable() {
                public void run() {
                    headerApp.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    registerHeader((HeaderInterface) fragment);
                    setHeaderTitle("", "SHIFT MANAGER", "");
                    //	mImgBack.setVisibility(View.VISIBLE);
                    Bck_button_main.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);

                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.VISIBLE);
                    mSwapButton.setVisibility(View.VISIBLE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof ShifthourFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    headerApp.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    Bck_button_main.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);

                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.VISIBLE);
                    mSwapButton.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof UnavailableFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    headerApp.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    Bck_button_main.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);

                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.VISIBLE);
                    mProgress.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof TimeOffFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    headerApp.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    Bck_button_main.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);

                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.VISIBLE);
                    mProgress.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof SwapingFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    headerApp.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    Bck_button_main.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);

                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.VISIBLE);
                    mProgress.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.VISIBLE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof OrderDetailFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    headerApp.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    Bck_button_main.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setVisibility(View.VISIBLE);
                    mTxtVwSubHeaderTop.setVisibility(View.VISIBLE);

                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mTxtVwHeaderMiddle.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof ScheduledDetailFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    //	mImgBack.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    Bck_button_main.setVisibility(View.VISIBLE);
                    mBtnDone.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setVisibility(View.VISIBLE);
                    mTxtVwSubHeaderTop.setVisibility(View.VISIBLE);

                    mProgress.setVisibility(View.GONE);
                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mTxtVwHeaderMiddle.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof CustomerDetailFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    if (SplashII.wrk_tid >= 2)
                        mBtnPlus.setVisibility(View.GONE);
                    else
                        mBtnPlus.setVisibility(View.VISIBLE);

                    mBtnDone.setVisibility(View.GONE);
                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof OrderFormsFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    if (SplashII.wrk_tid >= 5)
                        mBtnPlus.setVisibility(View.GONE);
                    else
                        mBtnPlus.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.VISIBLE);
                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof OrderPartsFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
//                    if (SplashII.wrk_tid >= 5)
//                        mBtnPlus.setVisibility(View.GONE);
//                    else
                    mBtnPlus.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setVisibility(View.VISIBLE);
                    mTxtVwSubHeaderTop.setVisibility(View.VISIBLE);

                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof OrderAssetFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //mImgBack.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    if (SplashII.wrk_tid >= 5)
                        mBtnPlus.setVisibility(View.GONE);
                    else
                        mBtnPlus.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setVisibility(View.VISIBLE);
                    mTxtVwSubHeaderTop.setVisibility(View.VISIBLE);

                    if (PreferenceHandler.getPrefEditionForGeo(BaseTabActivity.this) >= 600)//YD using when we have to open map when asset box is clicked on OLMP
                        mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.VISIBLE);
                }
            });
        } else if (fragment instanceof AddEditTaskOrderFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    mBtnDone.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    mTxtVwSubHeaderTop.setVisibility(View.VISIBLE);

                    mProgress.setVisibility(View.GONE);
                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof AddEditPartsFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);

                    mProgress.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof AddEditFormFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);

                    mProgress.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof AddEditAssetFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    mBtnDone.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof CreateOrderFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mBtnDone.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof EditContactFrag) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mBtnDone.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof AlertnNoteFrag) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mBtnDone.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);

                    mProgress.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof OrderDetailFrag) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mBtnDone.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof GoogleMapFragment) {
            final String mapType = GoogleMapFragment.maptype;
            runOnUiThread(new Runnable() {
                public void run() {
                    no_of_order.setVisibility(View.GONE);
                    if (mapType.equals("OrderList")) {
                        mBtnMapSetting_tree.setVisibility(View.VISIBLE);
                        mBtnMapSetting.setVisibility(View.GONE);
                        mTxtVwHeaderTop.setVisibility(View.VISIBLE);
                        mTxtVwSubHeaderTop.setVisibility(View.VISIBLE);
                        mImgLastSync.setVisibility(View.VISIBLE);

                        mBtnTaskUserMap.setVisibility(View.GONE);
                        mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                        //mImgBack.setVisibility(View.VISIBLE);
                        Bck_button_main.setVisibility(View.GONE);
                        mBtnPlus.setVisibility(View.GONE);
                        mBtnDone.setVisibility(View.GONE);
                        mSwapButton.setVisibility(View.GONE);
                    } else if (mapType.equals("TasksList")) {
                        mBtnMapSetting.setVisibility(View.VISIBLE);
                        mBtnTaskUserMap.setVisibility(View.VISIBLE);
                        mImgLastSync.setVisibility(View.VISIBLE);
                        mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                        mTxtVwHeaderTop.setVisibility(View.VISIBLE);
                        mTxtVwSubHeaderTop.setVisibility(View.VISIBLE);
                        //		mImgBack.setVisibility(View.GONE);
                        Bck_button_main.setVisibility(View.GONE);
                        mBtnPlus.setVisibility(View.GONE);
                        mSwapButton.setVisibility(View.GONE);
                    } else if (mapType.equals("TreeList")) {
                        mBtnPlus.setVisibility(View.VISIBLE);//YD
                        mBtnMapSetting_tree.setVisibility(View.GONE);//YD 2020
                        mSwapButton.setVisibility(View.VISIBLE);
                        mBtnMapSetting.setVisibility(View.GONE);
                        mBtnTaskUserMap.setVisibility(View.GONE);
                        mImgLastSync.setVisibility(View.GONE);
                        mTxtVwHeaderTop.setVisibility(View.VISIBLE);
                        mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                        Bck_button_main.setVisibility(View.VISIBLE);
                    } else if (mapType.equals("AccessPathList")) {
                        mBtnMapSetting.setVisibility(View.VISIBLE);
                        //	mImgBack.setVisibility(View.VISIBLE);
                        mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                        mTxtVwHeaderTop.setVisibility(View.VISIBLE);
                        mBtnTaskUserMap.setVisibility(View.GONE);
                        mImgLastSync.setVisibility(View.GONE);
                        Bck_button_main.setVisibility(View.VISIBLE);
                        mBtnPlus.setVisibility(View.GONE);
                        mSwapButton.setVisibility(View.GONE);
                    }
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    //mSwapButton.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);

                }
            });
        } else if (fragment instanceof MapAllFragment) {
            final String mapType = MapAllFragment.maptype;
            runOnUiThread(new Runnable() {
                public void run() {
                    no_of_order.setVisibility(View.GONE);
                    if (mapType.equals("OrderList")) {
                        mBtnPlus.setVisibility(View.GONE);
                        mTxtVwHeaderTop.setVisibility(View.VISIBLE);
                        mTxtVwSubHeaderTop.setVisibility(View.VISIBLE);
                        mImgLastSync.setVisibility(View.GONE);
                        mBtnTaskUserMap.setVisibility(View.GONE);
                        //mImgBack.setVisibility(View.GONE);
                        Bck_button_main.setVisibility(View.GONE);
                        mTxtVwHeaderMiddle.setVisibility(View.GONE);
                        mBtnMapSetting_tree.setVisibility(View.GONE);
                    }
                    if (mapType.equals("TasksList")) {
                        mBtnPlus.setVisibility(View.GONE);
                        mBtnTaskUserMap.setVisibility(View.VISIBLE);
                        mImgLastSync.setVisibility(View.VISIBLE);
                        mTxtVwHeaderMiddle.setVisibility(View.GONE);
                        //	mImgBack.setVisibility(View.GONE);
                        Bck_button_main.setVisibility(View.GONE);
                        mTxtVwHeaderTop.setVisibility(View.GONE);
                        mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    }
                    if (mapType.equals("TreeList")) {
                        mBtnPlus.setVisibility(View.VISIBLE);//YD
                        mBtnTaskUserMap.setVisibility(View.GONE);
                        mImgLastSync.setVisibility(View.GONE);
                        mTxtVwHeaderTop.setVisibility(View.GONE);
                        mTxtVwSubHeaderTop.setVisibility(View.GONE);
                        mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                        mBtnMapSetting_tree.setVisibility(View.VISIBLE);
                        //	mImgBack.setVisibility(View.GONE);
                        Bck_button_main.setVisibility(View.VISIBLE);
                    }
                    if (mapType.equals("AccessPathList")) {
                        mBtnPlus.setVisibility(View.GONE);
                        //	mImgBack.setVisibility(View.VISIBLE);
                        Bck_button_main.setVisibility(View.VISIBLE);
                        mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                        mBtnTaskUserMap.setVisibility(View.GONE);
                        mImgLastSync.setVisibility(View.GONE);
                        mTxtVwHeaderTop.setVisibility(View.GONE);
                        mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    }
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof MapFragment) {

            runOnUiThread(new Runnable() {
                public void run() {
                    mBtnMapSetting.setVisibility(View.VISIBLE);
					/*if(!Utilities.checkInternetConnection(BaseTabActivity.this,false)){
						mBtnMapSetting.setVisibility(View.GONE);
					}*/
                    no_of_order.setVisibility(View.VISIBLE);
                    mBtnPlus.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setTextSize(19 + (PreferenceHandler.getCurrrentFontSzForApp(mBaseTabActivity)));
                    mTxtVwSubHeaderTop.setVisibility(View.VISIBLE);
                    mTxtVwSubHeaderTop.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(mBaseTabActivity)));
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    mImgLastSync.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    Bck_button_main.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof CustomerListActivity) {
            runOnUiThread(new Runnable() {
                public void run() {
                    //    mImgBack.setVisibility(View.VISIBLE);
                    Bck_button_main.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    //	mImgPanic.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof Sitelist) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    // mImgBack.setVisibility(View.VISIBLE);
                    mBtnPlus.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    //	mImgPanic.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof AddSiteActy) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    mBtnDone.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    //	mImgPanic.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof Gridview_MainActivity) {
            runOnUiThread(new Runnable() {
                public void run() {
                    //	mImgBack.setVisibility(View.VISIBLE);
                    Bck_button_main.setVisibility(View.VISIBLE);
                    if (SplashII.wrk_tid >= 8)
                        mBtnPlus.setVisibility(View.GONE);
                    else
                        mBtnPlus.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setVisibility(View.VISIBLE);
                    mTxtVwSubHeaderTop.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof OrderPicSaveActivity) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    mBtnDone.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    no_of_order.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof Addsign) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    mBtnDone.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof OrderNote) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    mBtnPlus.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof Createnote) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    mBtnDone.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof OrderDetailAudioListFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    //	mImgBack.setVisibility(View.VISIBLE);
                    Bck_button_main.setVisibility(View.VISIBLE);
                    mBtnPlus.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    mTxtVwSubHeaderTop.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof RecordAudioFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    mBtnDone.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof PlayAudioFragment) {
            runOnUiThread(new Runnable() {
                public void run() {

                    Bck_button_main.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    mImgVolumeBtn.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        }/* else if (fragment instanceof AddEditTaskOldFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    mBtnDone.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    no_of_order.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        }*/ else if (fragment instanceof OrderTasksOldFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    mBtnMapOdrTask.setVisibility(View.VISIBLE);
                    mBtnPlus.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof CaptureSignature) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mImgBack.setVisibility(View.VISIBLE);
                    mBtnDone.setVisibility(View.VISIBLE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    no_of_order.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    //	mImgPanic.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof CountryListFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    //    mImgBack.setVisibility(View.VISIBLE);
                    Bck_button_main.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    //	mImgPanic.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof CityListFragment) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    // mImgBack.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    //	mImgPanic.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.GONE);
                    mProgress.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        } else if (fragment instanceof AddTaskSpeciesFrag) {
            Bck_button_main.setVisibility(View.VISIBLE);
            mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);
            no_of_order.setVisibility(View.GONE);
            mBtnPlus.setVisibility(View.GONE);
            mBtnMapSetting.setVisibility(View.GONE);
            mBtnMapOdrTask.setVisibility(View.GONE);
            mBtnTaskUserMap.setVisibility(View.GONE);
            mImgLastSync.setVisibility(View.GONE);
            mBtnDone.setVisibility(View.GONE);
            mProgress.setVisibility(View.GONE);
            mImgSyncBtn.setVisibility(View.GONE);
            mTxtVwHeaderTop.setVisibility(View.GONE);
            mTxtVwSubHeaderTop.setVisibility(View.GONE);
            mBtnTrash.setVisibility(View.GONE);
            mBtnTaskTreeMap.setVisibility(View.GONE);
            mBtnMapAll.setVisibility(View.GONE);
            mImgVolumeBtn.setVisibility(View.GONE);
            mSwapButton.setVisibility(View.GONE);
        } else if (fragment instanceof MessageSos) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);

                    mProgress.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);

                }
            });
        } else if (fragment instanceof ManagePassword) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Bck_button_main.setVisibility(View.VISIBLE);
                    //	mImgBack.setVisibility(View.VISIBLE);
                    no_of_order.setVisibility(View.GONE);
                    mBtnDone.setVisibility(View.VISIBLE);
                    mTxtVwHeaderMiddle.setVisibility(View.VISIBLE);

                    mProgress.setVisibility(View.GONE);
                    mTxtVwSubHeaderTop.setVisibility(View.GONE);
                    mTxtVwHeaderTop.setVisibility(View.GONE);
                    mBtnMapSetting_tree.setVisibility(View.GONE);
                    mBtnMapSetting.setVisibility(View.GONE);
                    mBtnTrash.setVisibility(View.GONE);
                    mBtnMapOdrTask.setVisibility(View.GONE);
                    mBtnTaskUserMap.setVisibility(View.GONE);
                    mImgLastSync.setVisibility(View.GONE);
                    mImgSyncBtn.setVisibility(View.GONE);
                    mBtnTaskTreeMap.setVisibility(View.GONE);
                    mBtnMapAll.setVisibility(View.GONE);
                    mBtnPlus.setVisibility(View.GONE);
                    mImgVolumeBtn.setVisibility(View.GONE);
                    mSwapButton.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.Bck_button_main:
                if (((BaseFragment) mStacks.get(mCurrentTab).lastElement()).onBackPressed() == false) {
                    if (mStacks.get(mCurrentTab).size() == 1) {
                        finish();
                    } else {
                        if (((BaseFragment) mStacks.get(mCurrentTab).lastElement()) instanceof RecordAudioFragment ||
                                ((BaseFragment) mStacks.get(mCurrentTab).lastElement()) instanceof PlayAudioFragment ||
                                ((BaseFragment) mStacks.get(mCurrentTab).lastElement()) instanceof OrderPartsFragment ||
                                ((BaseFragment) mStacks.get(mCurrentTab).lastElement()) instanceof MapAllFragment ||
                                ((BaseFragment) mStacks.get(mCurrentTab).lastElement()) instanceof GoogleMapFragment ||
                                ((BaseFragment) mStacks.get(mCurrentTab).lastElement()) instanceof CustomerListActivity ||
                                ((BaseFragment) mStacks.get(mCurrentTab).lastElement()) instanceof AddEditTaskOrderFragment || // YD using for audio record frag to handle back
                                ((BaseFragment) mStacks.get(mCurrentTab).lastElement()) instanceof SwapingFragment) // YD using for audio record frag to handle back

                        {
                            headerEventbtn.headerClickListener(HeaderBackPressed);
                        }
                        Fragment frag = ((BaseFragment) mStacks.get(mCurrentTab).lastElement());
                        if (!(frag instanceof AddEditTaskOrderFragment) &&
                                !(frag instanceof MapAllFragment) &&
                                !(frag instanceof GoogleMapFragment)) {
                            currentApplicationState = 1;
                            popFragments(UI_Thread);
                        } else {
                            if (((BaseFragment)
                                    mStacks.get(mCurrentTab).lastElement()) instanceof
                                    AddEditTaskOrderFragment && AddEditTaskOrderFragment.fieldUpdated == 0) {
                                popFragments(UI_Thread);
                            } else if (((BaseFragment)
                                    mStacks.get(mCurrentTab).lastElement()) instanceof
                                    MapAllFragment && MapAllFragment.FIELD_UPDATED == 0) {
                                popFragments(UI_Thread);
                            } else if (((BaseFragment)
                                    mStacks.get(mCurrentTab).lastElement()) instanceof
                                    GoogleMapFragment && GoogleMapFragment.FIELD_UPDATED == 0) {
                                popFragments(UI_Thread);
                            }
                        }
                        //}

                    }
                    hideSoftKeyboard();
                }
                break;
            case R.id.btn_Done:
                hideSoftKeyboard();
                headerEventbtn.headerClickListener(HeaderDonePressed);
                break;
            case R.id.B_plus:
                hideSoftKeyboard();
                headerEventbtn.headerClickListener(HeaderPlusPressed);
                break;
            case R.id.panic_btn:
                headerEventbtn.headerClickListener(HeaderPanicPressed);
                break;
            case R.id.syncbtn:
                headerEventbtn.headerClickListener(HeaderSyncPressed);
                break;
            case R.id.volumebtn:
                hideSoftKeyboard();
                headerEventbtn.headerClickListener(HeaderVolumeButton);
                break;
            case R.id.calenderSwap:
                hideSoftKeyboard();
                headerEventbtn.headerClickListener(HeaderSwapButton);
                break;
            case R.id.btn_mapodrtask:
                headerEventbtn.headerClickListener(HeaderMapOrderTaskPressed);
                break;
            case R.id.trash:
                headerEventbtn.headerClickListener(HeaderDeleteButton);
                break;
            case R.id.lastsync:
                headerEventbtn.headerClickListener(HeaderlastSyncPressed);
                break;
            case R.id.btn_taskuserMap:
                headerEventbtn.headerClickListener(HeaderTaskUserPressed);
                break;
            case R.id.btn_mapall:
                headerEventbtn.headerClickListener(HeaderMapAllPressed);
                break;
            case R.id.btn_mapstyle:
                headerEventbtn.headerClickListener(HeaderMapSettingPressed_Main);
            case R.id.btn_mapstyle_tree:
                headerEventbtn.headerClickListener(HeaderMapSettingPressed);
            default:
                break;
        }
    }

    public void replaceBtnHeader(String button) {

        if (button.equals("plusDone")) {
            mBtnPlus.setVisibility(View.GONE);
            mBtnDone.setVisibility(View.VISIBLE);
        } else if (button.equals("donePlus")) {
            mBtnDone.setVisibility(View.GONE);
            mBtnPlus.setVisibility(View.VISIBLE);
        }

    }

    public void registerHeader(HeaderInterface fnName) {
        headerEventbtn = fnName;
    }

    public void setActiveOrderObject(Order orderObj) {
        activeOrderObj = orderObj;
    }

    public void callbackpncb(int objtype, int actionType, String additionalData, Response response) {
        CommonSevenReq CommonReqObj = null;
        if (objtype == 5 || objtype == 6 || objtype == 7 || objtype == 9 || objtype == 10 || objtype == 24) {
            CommonReqObj = new CommonSevenReq();
            CommonReqObj.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(this) + "/mobi");
            CommonReqObj.setSource("localonly");
        }

        if (objtype == Order.TYPE) {// using in case of add ,edit, delete
            getOrders();
        }
        if (objtype == 3) {
            Log.i("AceRoute", "Nothing to update");
        } else if (objtype == 5) {
            getOrderType(CommonReqObj);
        } else if (objtype == 6) {
            getPartsType(CommonReqObj);
        } /*else if (objtype == 7) {
            getServiceType(CommonReqObj);
        } */ else if (objtype == Worker.TYPE) {//9
            getResources(CommonReqObj);
        } else if (objtype == 10) {
            getCustomer(CommonReqObj);
        } else if (objtype == 13) {
            getAllOrderNotes("getordernotes");
        } else if (objtype == ServiceType.TYPE_UPDATE) {
            handlePbTaskUpdate(response);
        } else if (objtype == Parts.TYPE_UPDATE) {
            handlePbPartUpdate(response);
        } else if (objtype == OrderMedia.TYPE_PUBNUB_UPDATE) {
            handlePbMetaUpdate(response);
        } else if (objtype == Site.TYPE) {
            // YD this is for site pubnub now handle for site data refresh on customer detail page
        } else if (objtype == SiteType.TYPE) {
            getSiteType(CommonReqObj);
        } else if (objtype == Assets.TYPE_PUBNUB) {
            AceOfflineSyncEngine offlinesyncengine = AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(this);
            offlinesyncengine.fillAssetCount(this, this);//YD update the asset count again
        } else if (objtype == Worker.PUBNUB_TYPE) {

            Log.d("punnublogout", "pubbb" + objtype);

            if (!BaseTabActivity.logout) {
                BaseTabActivity.logout = true;
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(BaseTabActivity.this, AppLoginPage.class);
                        startActivity(i);
                    }
                });
                stopUser();
                //syncDataBeforeLogout("semipartial_logout");
            }
        } else if (objtype == PUBNUB_SYNC) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    versionCheckState = "SYNC";
                    checkVersionForApp();
                }
            });

        } else if (objtype == PUBNUB_LOGOUT) {

            Log.d("notttttpunnublogout", "pubbb" + objtype);
            syncDataBeforeLogout("semipartial_logout");
        }

    }

    private void getAllOrderNotes(String action) {
        updateOrderRequest req = new updateOrderRequest();
        req.setId("0");
        req.setAction(action);
        OrderNotes.getData(req, this, this, GET_ALL_NOTE_REQ);
    }

   /* private void getServiceType(CommonSevenReq CommonReqObj) {
        ServiceType.getData(CommonReqObj, this, this, GET_TASKS_REQ_PB);
    }*/

    private void getOrders() {
        Date today = new Date();// later use to set header
        int timeForwardAndBackward = SplashII.numberOfDaysForwardAndBackward * 24 * 60 * 60 * 1000; // currently date forward and backword is zero

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateToFrom = dateFormat.format(today);

        GetOrdersRO odrReqObj = new GetOrdersRO(); //YD this object is never used in service
        odrReqObj.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(this) + "/mobi");
        odrReqObj.setTz("Asia/Calcutta");
        odrReqObj.setFrom(dateToFrom);
        odrReqObj.setTo(dateToFrom);
        odrReqObj.setAction("getorders");

        Order.getData(odrReqObj, this, this, GET_ORDER_REQ_PB);
    }

    private void getOrderType(CommonSevenReq CommonReqObj) {
        OrderTypeList.getData(CommonReqObj, this, this, GET_ORDER_TYPE_REQ_PB);
    }

    private void getPartsType(CommonSevenReq CommonReqObj) {
        Parts.getData(CommonReqObj, this, this, GET_PARTS_REQ_PB);
    }

    private void getResources(CommonSevenReq CommonReqObj) {
        Worker.getData(CommonReqObj, this, this, GET_RES_REQ_PB);
    }

    private void getCustomer(CommonSevenReq CommonReqObj) {
        Customer.getData(CommonReqObj, this, this, GET_CUST_REQ_PB);
    }

    private void getSiteType(CommonSevenReq CommonReqObj) {
        SiteType.getData(CommonReqObj, this, this, GET_SITE_TYPE);
    }

    private void handlePbPartUpdate(Response response) {
        if (currentActiveFrag instanceof OrderPartsFragment) {
            GetPart_Task_FormRequest req = new GetPart_Task_FormRequest();
            req.setAction(OrderPart.ACTION_GET_ORDER_PART);
            req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(this) + "/mobi");
            //req.setOid(String.valueOf(((Order)orderMap.entrySet().iterator().next().getValue()).getId()));
            req.setOid(String.valueOf(((OrderPartsFragment) currentActiveFrag).activeOrderObj.getId()));

            OrderPart.getData(req, this, this, GET_PARTS_UPDATE_PB);
        } else if (currentActiveFrag instanceof OrderListMainFragment && currentApplicationState == FOREGROUND) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    OrderListMainFragment.updateOrderFrmPb = true;
                    if (currentActiveFrag instanceof OrderListMainFragment && currentApplicationState == FOREGROUND) {
                        ((OrderListMainFragment) currentActiveFrag).notifyDataUpdate(BaseTabActivity.this);//413f6390
                    }
                }
            });
        } else
            OrderListMainFragment.updateOrderFrmPb = true;
    }

    private void handlePbTaskUpdate(Response response) {
        if (currentActiveFrag instanceof OrderFormsFragment) {

            GetPart_Task_FormRequest req = new GetPart_Task_FormRequest();
            req.setAction(OrderTask.ACTION_GET_ORDER_TASK);
            req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(this) + "/mobi");
            //req.setOid(String.valueOf(((Order)orderMap.entrySet().iterator().next().getValue()).getId()));
            req.setOid(String.valueOf(((OrderFormsFragment) currentActiveFrag).activeOrderObj.getId()));

            OrderTask.getData(req, this, this, GET_TASKS_UPDATE_PB);
        } else if (currentActiveFrag instanceof OrderListMainFragment && currentApplicationState == FOREGROUND) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    OrderListMainFragment.updateOrderFrmPb = true;
                    if (currentActiveFrag instanceof OrderListMainFragment && currentApplicationState == FOREGROUND) {
                        ((OrderListMainFragment) currentActiveFrag).notifyDataUpdate(BaseTabActivity.this);//413f6390
                    }
                }
            });
        } else
            OrderListMainFragment.updateOrderFrmPb = true;
    }

    private void handlePbMetaUpdate(Response response) {
        if (currentActiveFrag instanceof Gridview_MainActivity && currentApplicationState == FOREGROUND) {

            GetFileMetaRequest req = new GetFileMetaRequest();
            req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(this) + "/mobi");
            req.setAction(OrderMedia.ACTION_GET_MEDIA);
            req.setOid(String.valueOf(((Gridview_MainActivity) currentActiveFrag).activeOrderObj.getId()));

            OrderMedia.getData(req, this, this, GETFILEMETA_PB);
        } else if (currentActiveFrag instanceof OrderListMainFragment && currentApplicationState == FOREGROUND) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    OrderListMainFragment.updateOrderFrmPb = true;
                    if (currentActiveFrag instanceof OrderListMainFragment && currentApplicationState == FOREGROUND) {
                        ((OrderListMainFragment) currentActiveFrag).notifyDataUpdate(BaseTabActivity.this);//413f6390
                    }
                }
            });
        } else
            OrderListMainFragment.updateOrderFrmPb = true;
    }

    public void refreshAssestCount() {
        if (currentActiveFrag instanceof OrderListMainFragment && currentApplicationState == FOREGROUND) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (currentActiveFrag instanceof OrderListMainFragment && currentApplicationState == FOREGROUND) {
                        ((OrderListMainFragment) currentActiveFrag).notifyDataUpdate(BaseTabActivity.this);//413f6390
                    }
                }
            });
        } else
            OrderListMainFragment.updateOrderFrmPb = true;
    }

    @Override
    public void setResponseCBActivity(final Response response) {
        if (response != null) {
            if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED))) {
                if (response.getId() == GET_ORDER_REQ_PB) {
                    if (currentActiveFrag instanceof OrderListMainFragment && currentApplicationState == FOREGROUND) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (currentActiveFrag instanceof OrderListMainFragment && currentApplicationState == FOREGROUND) {
                                    ((OrderListMainFragment) currentActiveFrag).notifyDataChange(response, BaseTabActivity.this);//413f6390
                                }
                            }
                        });
                    } else {
                        if (currentApplicationState == BACKGROUND)
                            PreferenceHandler.setAppDataChanged(this, true);
                        DataObject.ordersXmlDataStore = null;
                        DataObject.ordersXmlDataStore = response.getResponseMap();
                        if (currentActiveFrag instanceof MapFragment && currentApplicationState == FOREGROUND) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    no_of_order.setText(String.valueOf(((HashMap<Long, OrderTypeList>) DataObject.ordersXmlDataStore).size()));
                                    ((MapFragment) currentActiveFrag).loadDataOnBack(BaseTabActivity.this, true);
                                }
                            });

                        }
                    }
                } else if (response.getId() == CHECKIN_REQ) {
                    PreferenceHandler.setClockInStat(BaseTabActivity.this, "1");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ChangeCheckout(0);
                        }
                    });

                } else if (response.getId() == CHECKOUT_REQ) {
                    PreferenceHandler.setClockInStat(BaseTabActivity.this, "0");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ChangeCheckout(1);
                        }
                    });
                } else if (response.getId() == GET_ORDER_TYPE_REQ_PB) {//YD TODO correct the failure in aceReqHandler
                    DataObject.orderTypeXmlDataStore = null;
                    DataObject.orderTypeXmlDataStore = response.getResponseMap();
                } else if (response.getId() == GET_PARTS_REQ_PB) {
                    DataObject.partTypeXmlDataStore = null;
                    DataObject.partTypeXmlDataStore = response.getResponseMap();
                } /*else if (response.getId() == GET_TASKS_REQ_PB) {
                    DataObject.taskTypeXmlDataStore = null;
                    DataObject.taskTypeXmlDataStore = response.getResponseMap();
                } */ else if (response.getId() == GET_RES_REQ_PB) {
                    DataObject.resourceXmlDataStore = null;
                    DataObject.resourceXmlDataStore = response.getResponseMap();

                } else if (response.getId() == GET_CUST_REQ_PB) {
                    DataObject.customerXmlDataStore = null;
                    DataObject.customerXmlDataStore = response.getResponseMap();

                    getOrders();
                } else if (response.getId() == GET_SITE_TYPE) {
                    DataObject.siteTypeXmlDataStore = null;
                    DataObject.siteTypeXmlDataStore = response.getResponseMap();
                } else if (response.getId() == GET_ALL_NOTE_REQ) {
                    DataObject.orderNoteDataStore = null;
                    DataObject.orderNoteDataStore = response.getResponseMap();
                    getOrders(); // because have to refresh the order list_cal screen with new order note data
                }
                if (response.getId() == GET_PARTS_UPDATE_PB && currentApplicationState == FOREGROUND) {
                    if (currentActiveFrag instanceof OrderPartsFragment) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((OrderPartsFragment) currentActiveFrag).notifyDataChange(response, BaseTabActivity.this);//413f6390
                            }
                        });
                    } else {
                        if (currentApplicationState == BACKGROUND)
                            PreferenceHandler.setAppDataChanged(this, true);
                        DataObject.orderPartsXmlStore = null;
                        DataObject.orderPartsXmlStore = response.getResponseMap();
                    }
                    OrderListMainFragment.updateOrderFrmPb = true;
                    //getOrders();
                }
                if (response.getId() == GET_TASKS_UPDATE_PB && currentApplicationState == FOREGROUND) {
                    if (currentActiveFrag instanceof OrderFormsFragment) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((OrderFormsFragment) currentActiveFrag).notifyDataChange(response, BaseTabActivity.this);//413f6390
                            }
                        });
                    }/* else {
                        if (currentApplicationState == BACKGROUND)
                            PreferenceHandler.setAppDataChanged(this, true);
                        DataObject.orderTasksXmlStore = null;
                        DataObject.orderTasksXmlStore = response.getResponseMap();
                    }*/
                    OrderListMainFragment.updateOrderFrmPb = true;
                    //getOrders();
                }
                if (response.getId() == GETFILEMETA_PB && currentApplicationState == FOREGROUND) {
                    if (currentActiveFrag instanceof Gridview_MainActivity) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((Gridview_MainActivity) currentActiveFrag).notifyDataChange(response, BaseTabActivity.this);//413f6390
                            }
                        });
                    } else {
                        if (currentApplicationState == BACKGROUND)
                            PreferenceHandler.setAppDataChanged(this, true);
						/*DataObject.orderTasksXmlStore = null;
						DataObject.orderTasksXmlStore = response.getResponseMap();*/
                    }
                    OrderListMainFragment.updateOrderFrmPb = true;
                    //getOrders();
                }
                if (response.getId() == SYNC_OFFLINE_DATA_LOGOUT) {
                    //YD sending checkout request at logout

                    final Handler h1 = new Handler(Looper.getMainLooper());
                    h1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ClockInOutRequest clkInOut = new ClockInOutRequest();
                            clkInOut.setTid("0");
                            clkInOut.setType("post");
                            clkInOut.setAction(CheckinOut.CC_ACTION);
                            CheckinOut.getData(clkInOut, BaseTabActivity.this, BaseTabActivity.this, CHECKOUT_REQ_LOGOUT);
                            h1.removeCallbacks(this);
                        }
                    }, 300);


                }
                if (response.getId() == CHECKOUT_REQ_LOGOUT) {
                    stopUser();
                }
                if (response.getId() == LOGOUT_REQ) {
                    logout();
                }

                if (response.getId() == SYNC_OFFLINE_DATA) {
                    PreferenceHandler.setSyncPopupCancelTm(this, 0L);//YD making it zero because may the date has been changed
                    if (PreferenceHandler.getOdrGetDate(this) != null && !PreferenceHandler.getOdrGetDate(this).equals("")) {
                        PreferenceHandler.setodrLastSyncForDt(this, Utilities.getCustDtDefaultTm());
                        PreferenceHandler.setlastsynctime(this, Utilities.getCurrentTime());

                        // YD if the date changed is the current date then clear the preference
                        if (Utilities.isTodayDate(this, new Date(Long.valueOf(PreferenceHandler.getodrLastSyncForDt(this))))) {
                            PreferenceHandler.setOdrGetDate(this, null);
                        }
                    } else
                        PreferenceHandler.setlastsynctime(this, Utilities.getCurrentTime());

                    BaseTabActivity.setTmeFrOLMPHeader(this.getApplicationContext());
                    if (customDialog != null)
                        this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                customDialog.showHideView();
                                //  customDialog.disablePauseBtn(true);
                                //setSyncTimeOnTitle();TODO later for sync
                            }
                        });

                    // YD closing the app when version is not equal "on option fragment"
                    if (shouldCloseApp) {
                        shouldCloseApp = false;
                        finish();
                    }
                    CommonServManager comMang = new CommonServManager(this, this, this);
                    comMang.loadRefData("localonly", LOAD_REF_DATA);
                }
                if (response.getId() == LOAD_REF_DATA) {
                    loadOrdersAtBoot();
                }
                if (response.getId() == GET_ORDER_REQ) {
				/*	DataObject.ordersXmlDataStore = null;
					DataObject.ordersXmlDataStore = response.getResponseMap();*/
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (currentActiveFrag instanceof OrderListMainFragment) {
                                ((OrderListMainFragment) currentActiveFrag).notifyDataChange(response, BaseTabActivity.this);
                            }
                        }
                    });

                    //
                }
            } else if (response.getStatus().equals("failure") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA))) {
                if (response.getId() == GET_ORDER_REQ_PB || response.getId() == GET_ORDER_REQ) {
                    if (currentActiveFrag instanceof OrderListMainFragment && currentApplicationState == FOREGROUND) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (currentActiveFrag instanceof OrderListMainFragment && currentApplicationState == FOREGROUND) {
                                    ((OrderListMainFragment) currentActiveFrag).notifyDataChange(response, BaseTabActivity.this);//413f6390
                                }
                            }
                        });
                    } else {
                        if (currentApplicationState == BACKGROUND)
                            PreferenceHandler.setAppDataChanged(this, true);
                        DataObject.ordersXmlDataStore = null;
                        DataObject.ordersXmlDataStore = response.getResponseMap();
                    }
                }
                if (response.getId() == GET_PARTS_UPDATE_PB && currentApplicationState == FOREGROUND) {
                    if (currentActiveFrag instanceof OrderPartsFragment) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((OrderPartsFragment) currentActiveFrag).notifyDataChange(response, BaseTabActivity.this);//413f6390
                            }
                        });
                    } else {
                        if (currentApplicationState == BACKGROUND)
                            PreferenceHandler.setAppDataChanged(this, true);
                        DataObject.orderPartsXmlStore = null;
                        DataObject.orderPartsXmlStore = response.getResponseMap();
                    }
                }
                /*if (response.getId() == GET_TASKS_UPDATE_PB && currentApplicationState == FOREGROUND) {
                    if (currentActiveFrag instanceof OrderTasksFragment) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((OrderTasksFragment) currentActiveFrag).notifyDataChange(response, BaseTabActivity.this);//413f6390
                            }
                        });
                    }*//* else {
                        if (currentApplicationState == BACKGROUND)
                            PreferenceHandler.setAppDataChanged(this, true);
                        DataObject.orderTasksXmlStore = null;
                        DataObject.orderTasksXmlStore = response.getResponseMap();
                    }*//*
                }*/
            } else if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA))) {
                if (response.getId() == GETFILEMETA_PB && currentApplicationState == FOREGROUND) {
                    if (currentActiveFrag instanceof Gridview_MainActivity) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((Gridview_MainActivity) currentActiveFrag).notifyDataChange(response, BaseTabActivity.this);//413f6390
                            }
                        });
                    } else {
                        if (currentApplicationState == BACKGROUND)
                            PreferenceHandler.setAppDataChanged(this, true);
						/*DataObject.orderTasksXmlStore = null;
						DataObject.orderTasksXmlStore = response.getResponseMap();*/
                    }
                }
            }
        }
    }

    public void hideSoftKeyboard() {
        try {
            if (getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void finishAct() {
        finish();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case GET_ACCESS_FINE_LOCATION_PERM: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }


    }

    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
//        if (id == R.id.drawer_addOrder) {
//            OrderListMainFragment.optionRefreshState = 3;
//            mJobFragment.TaskAction(this);
//        } else
        if (id == R.id.drawer_fontsize) {
            OrderListMainFragment.optionRefreshState = 6;
            mJobFragment.TaskAction(this);
        } else if (id == R.id.drawer_route_date) {
            drawerRouteToDate();
        } else if (id == R.id.nav_to_start) {
            navigationToStart();
        } else if (id == R.id.drawer_about) {
            OrderListMainFragment.optionRefreshState = 7;
            mJobFragment.TaskAction(this);
        } else if (id == R.id.manage_shift) {
            manageShiftTapped();
        } else if (id == R.id.drawer_message) {
            drawerMessageTapped();
        } else if (id == R.id.drawer_password) {
            drawerPasswordTapped();
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                drawer.closeDrawer(GravityCompat.END);
            }
        });
        return true;
    }

    private void drawerRouteToDate() {
        Date date;
        if (PreferenceHandler.getOdrGetDate(this) == null || PreferenceHandler.getOdrGetDate(this).equals(""))
            date = new Date(PreferenceHandler.getlastsynctime(this));
        else
            date = new Date(PreferenceHandler.getodrLastSyncForDt(this));
        int[] datestr = Utilities.getDateFromDateObj(date);
        openDatePicker(datestr[0], datestr[1], datestr[2]);
    }

    private void navigationToStart() {
        Utilities.log(this, "open google Navigation for worker office:" + PreferenceHandler.getWorkerOffGeo(this));
        if (PreferenceHandler.getWorkerOffGeo(this) != null) {
            String geoArray[] = PreferenceHandler.getWorkerOffGeo(this).split(",");
            String startLat = geoArray[0];
            String startLon = geoArray[1];
            if (startLat != null && startLon != null)
                //openGoogleNavigation(Double.parseDouble(geoArray[0]), Double.parseDouble(geoArray[1]));
                openGoogleNavigation(Double.parseDouble(startLat), Double.parseDouble(startLon));
            else
                openGoogleNavigation(0.0, 0.0);
        } else
            openGoogleNavigation(0.0, 0.0);
    }

    private void drawerPasswordTapped() {
        ManagePassword mngpassword = new ManagePassword();
        pushFragToStackRequired = 1;//YD using because push is called twice once from here and another from the ontabChange
        Log.i("ProcessShift", "click start/Push func " + Utilities.getCurrentTime());
        pushFragments(Utilities.JOBS, mngpassword, true, true, BaseTabActivity.UI_Thread);
    }

    private void drawerMessageTapped() {
        MessageSos message_sos = new MessageSos();
        pushFragToStackRequired = 1;//YD using because push is called twice once from here and another from the ontabChange
        Log.i("ProcessShift", "click start/Push func " + Utilities.getCurrentTime());
        pushFragments(Utilities.JOBS, message_sos, true, true, BaseTabActivity.UI_Thread);
    }

    private void manageShiftTapped() {
        if ((!(currentActiveFrag instanceof ManageShift))) {
            ManageShift mngshift = new ManageShift();
            pushFragToStackRequired = 1;//YD using because push is called twice once from here and another from the ontabChange
            Log.i("ProcessShift", "click start/Push func " + Utilities.getCurrentTime());
            pushFragments(Utilities.JOBS, mngshift, true, true, BaseTabActivity.UI_Thread);
            Log.i("ProcessShift", "End push func " + Utilities.getCurrentTime());

        }
    }

    private void openDatePicker(int mYear, int mMonth, int mDate) {
        int sizeDialogStyleID = Utilities.getDialogTextSize(this);//YD getting text size for dialog

        MyDatePickerDialog dialog = new MyDatePickerDialog(this, new mDateSetListener(), mYear, mMonth - 1, mDate, true, this, sizeDialogStyleID);//YD subtracting 1 from month because month index start from zero in calender
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", dialog);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", dialog);

        if (Build.VERSION.SDK_INT >= 11) {
            dialog.getDatePicker().setCalendarViewShown(false);
        }
        dialog.setTitle("Set Route Date");
        if ((!(BaseTabActivity.mBaseTabActivity).isFinishing()) && !dialog.isShowing()) {
            dialog.show();
        }
        Utilities.setDividerTitleColor(dialog, 0, this);
        Button button_Negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Utilities.setDefaultFont_12(button_Negative);
        Button button_Positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Utilities.setDefaultFont_12(button_Positive);

    }

    @Override

    public void onCancelledBtn() {

    }

    public void showDialogMlogin(String title, String content, final String typeOfSync) {

        dialog = new MyDialog(this, title, content, "OK");
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

        dialog.setkeyListender(new MyDiologInterface() {
            @Override
            public void onPositiveClick() throws JSONException {
                dialog.dismiss();
            }

            @Override
            public void onNegativeClick() {
                // TODO Auto-generated method stub
                dialog.dismiss();
                SyncRO syncObj = new SyncRO();
                syncObj.setType(typeOfSync);
                syncOfflineData(syncObj, SYNC_OFFLINE_DATA_LOGOUT);
                Intent i = new Intent(BaseTabActivity.this, AppLoginPage.class);
                startActivity(i);

            }
        });
        dialog.onCreate(null);
        if ((!(BaseTabActivity.mBaseTabActivity).isFinishing()) && !dialog.isShowing()) {
            dialog.show();
        }
        Utilities.setDividerTitleColor(dialog, 0, this);
    }

    public void showNoInternetDialog(String title, String content) {

		/*String D_title = BaseTabActivity.getResources().getString(R.string.msg_slight_problem);
		String D_desc = getResources().getString(R.string.msg_internet_problem);*/
        dialog = new MyDialog(this, title, content, "OK");
        //YD CODE FOR SETTING HEIGHT OF DIALOG
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

        dialog.setkeyListender(new MyDiologInterface() {
            @Override
            public void onPositiveClick() throws JSONException {
                dialog.dismiss();
            }

            @Override
            public void onNegativeClick() {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        dialog.onCreate(null);
        if ((!(BaseTabActivity.mBaseTabActivity).isFinishing()) && !dialog.isShowing()) {
            dialog.show();
        }
        Utilities.setDividerTitleColor(dialog, 0, this);
		/*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;

		lp.gravity = Gravity.CENTER;
		dialog.getWindow().setAttributes(lp);*/
    }

    public void showLogoutDialog() {
        dialog = new MyDialog(this, this.getResources().getString(R.string.lbl_logout_title), this.getResources().getString(R.string.lbl_logout_message), "YES");
        dialog.setkeyListender(new MyDiologInterface() {

            @Override
            public void onPositiveClick() throws JSONException {
                // on No button click

                PreferenceHandler.setPassCode(getApplicationContext(), "");
                logout();
                finish();
                //      stopService(new Intent(getApplicationContext(), UpdateLocationService.class));
                //
                dialog.dismiss();
            }

            @Override
            public void onNegativeClick() {
                // on Yes button click

                if (!Utilities.checkInternetConnection(BaseTabActivity.this, false)) {
                    showNoInternetDialog("Slight problem with data", "No internet connection");
                    dialog.dismiss();
                } else {
                    finishAffinity();
                    dialog.dismiss();
                    logout();
                    syncDataBeforeLogout("semipartial_logout");


                }


            }
        });
        dialog.onCreate(null);
        if ((!(BaseTabActivity.mBaseTabActivity).isFinishing()) && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void syncDataBeforeLogoutAndMlogin(final String typeOfSync) {// YD using refActivity to handle null intent when calling from option frag
        //	openSyncLogoutDialog();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                showDialogMlogin("Contact your adminstrator", "Something is not quite right.", typeOfSync);
            }
        });

    }

    public void syncDataBeforeLogout(String typeOfSync) {// YD using refActivity to handle null intent when calling from option frag
        //	openSyncLogoutDialog();

        SyncRO syncObj = new SyncRO();
        syncObj.setType(typeOfSync);
        syncOfflineData(syncObj, SYNC_OFFLINE_DATA_LOGOUT);
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(BaseTabActivity.this, AppLoginPage.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                //  finishAffinity();
            }
        });

    }

    private void syncOfflineData(SyncRO syncObj, int reqId) {
        AceRequestHandler requestHandler = null;
        Intent intent = null;

        requestHandler = new AceRequestHandler(this);
        intent = new Intent(this, AceRouteService.class);

        Long currentMilli = PreferenceHandler.getPrefQueueRequestId(this);

        Bundle mBundle = new Bundle();
        mBundle.putParcelable("OBJECT", syncObj);
        mBundle.putLong(AceRouteService.KEY_TIME, currentMilli);
        mBundle.putInt(AceRouteService.KEY_SYNCALL_FLAG, AceRouteService.VALUE_NOT_SYNCALL);
        mBundle.putInt(AceRouteService.FLAG_FOR_CAMERA, 0);
        mBundle.putInt(AceRouteService.KEY_ID, reqId);
        mBundle.putString(AceRouteService.KEY_ACTION, "syncalldataforoffline");

        intent.putExtras(mBundle);
        requestHandler.ServiceStarterLoc(this, intent, this, currentMilli);
    }

    public void stopUser() {
        // String jsonGetWorkerListDelete = "{\"action\":\"" + Worker.ACTION_WORKER_LIST + "\","+"\"objecttype\":"+"\"2\",\"id\":"+PreferenceHandler.getResId(activity.getApplicationContext())+"}";

//YD first showing up the login page and deleting the user data in background


        LogoutRequest req = new LogoutRequest();
        req.setId(String.valueOf(PreferenceHandler.getResId(this.getApplicationContext())));
        req.setAction(Worker.ACTION_WORKER_LOGOUT);
        req.setObjtypeforpn("9");
        req.setAppexit(1);
        req.setObjecttype(2);
        req.setX_element("");

        AceRequestHandler requestHandler = null;
        Intent intent = null;

        requestHandler = new AceRequestHandler(this);
        intent = new Intent(this, AceRouteService.class);

        Long currentMilli = PreferenceHandler.getPrefQueueRequestId(this);

        Bundle mBundle = new Bundle();
        mBundle.putParcelable("OBJECT", req);
        mBundle.putLong(AceRouteService.KEY_TIME, currentMilli);
        mBundle.putInt(AceRouteService.KEY_SYNCALL_FLAG, AceRouteService.VALUE_NOT_SYNCALL);
        mBundle.putInt(AceRouteService.FLAG_FOR_CAMERA, 0);
        mBundle.putInt(AceRouteService.KEY_ID, LOGOUT_REQ);
        mBundle.putString(AceRouteService.KEY_ACTION, Worker.ACTION_WORKER_LOGOUT);

        intent.putExtras(mBundle);
        requestHandler.ServiceStarterLoc(this, intent, this, currentMilli);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //	Intent i = new Intent(BaseTabActivity.this, AppLoginPage.class);
                //	startActivity(i);
                //YD clear the preference completely
				 /*SharedPreferences settings = getSharedPreferences("PreferencesName", Context.MODE_PRIVATE);
				settings.edit().clear().commit();*/
                finish();
            }
        });
    }

    public void logout() {
        stopService(new Intent(this, UpdateLocationService.class));
        DBEngine.sendLogoutrequest(this.getApplicationContext());
        stopService(new Intent(this, AceRouteService.class));
//        PreferenceHandler.setCompanyId(this.getApplicationContext(), null);
//        PreferenceHandler.setMtoken(this.getApplicationContext(), null);
//        PreferenceHandler.setResId(this.getApplicationContext(), 0L);


        PreferenceHandler.setIsUserLoggedIn(this, false);

        //getIsNewLogin(getApplicationContext()
        //     PreferenceHandler.setIsNewLogin(this, false);


        PreferenceHandler.setlastsynctime(this, 0);
        PreferenceHandler.setOptionSelectedForImg(this, null);
        PreferenceHandler.setCurrrentFontSzForApp(this, 0);
        PreferenceHandler.setClockInStat(this, "0");
        PreferenceHandler.setOdrGetDate(this, null);
        PreferenceHandler.setodrLastSyncForDt(this, 0);
        PreferenceHandler.setPrefCtid(this, null);
        PreferenceHandler.setPrefCatid(this, null);
        ArrayList<String> mOrderList = new ArrayList<String>();
        PreferenceHandler.setArrayPrefs("OrderList", mOrderList, this);

        SharedPreferences settings = getSharedPreferences("PreferencesName", Context.MODE_PRIVATE);
        settings.edit().clear().apply();
        Utilities.stopAlarmManager(this); //YD 2020

    }

   /* private void stopAlarmManager() {
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            pendingIntent = PendingIntent.getForegroundService(BaseTabActivity.this, SYNC_LOCATION_JOB_ID, new Intent(BaseTabActivity.this, UpdateLocationService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getService(BaseTabActivity.this, SYNC_LOCATION_JOB_ID, new Intent(BaseTabActivity.this, UpdateLocationService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }*/

    private void checkVersionForApp() {
        Asyncc asyncc = new Asyncc();
        asyncc.execute();
    }

    public void sendOfflineDataRequest(String typeOfSync) {// YD using refActivity to handle null intent when calling from option frag
        openSyncDialog();
        SyncRO syncObj = new SyncRO();
        syncObj.setType(typeOfSync);
        //syncObj.setOdrGetDate (date);
        syncOfflineData(syncObj, SYNC_OFFLINE_DATA);

    }

    private void openSyncDialog() {
        try {
            customDialog = new CustomStatusDialog(BaseTabActivity.mBaseTabActivity, "Syncing Data", "OLMP");//YD changed context
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            customDialog.setkeyListender(new StatusDiologInterface() {

                @Override
                public void onPositiveClick(String btnTxt) throws JSONException {
                    if (btnTxt.equals("Pause")) {
                        customDialog.disablePauseBtn(false);//YD disabling the pause button
                        AceOfflineSyncEngine offlinesyncengine = AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(BaseTabActivity.this);
                        offlinesyncengine.setSynctoPause(BaseTabActivity.this, true);
                    } else {
                        AceOfflineSyncEngine offlinesyncengine = AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(BaseTabActivity.this);
                        offlinesyncengine.setSynctoPause(BaseTabActivity.this, false);
                        customDialog.dismiss();
                    }
                }

                @Override
                public void onNegativeClick(String btnTxt) {
                    // TODO Auto-generated method stub
                }
            });
            customDialog.onCreate(null);
            customDialog.disablePauseBtn(true);

            if ((!(BaseTabActivity.mBaseTabActivity).isFinishing()) && !customDialog.isShowing()) {
                customDialog.show();
            }

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(customDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;

            lp.gravity = Gravity.CENTER;
            customDialog.getWindow().setAttributes(lp);
            customDialog.setHeightPopup();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showDialogForVersion() {

        try {
            String D_title = "Please upgrade";
            String D_desc = " A new version of AceRoute app is available. Please download from Google Play Store.";

            dialog = new MyDialog(this, D_title, D_desc, "OK");
            dialog.setkeyListender(new MyDiologInterface() {
                @Override
                public void onPositiveClick() throws JSONException {
                    dialog.dismiss();
                }

                @Override
                public void onNegativeClick() {
                    if (shouldFinish == true) {
                        shouldFinish = false;

                        if (!Utilities.checkInternetConnection(BaseTabActivity.this, false))
                            showNoInternetDialog("Slight problem with data", "No internet connection");
                        else {
                            if (shouldCloseApp)
                                sendOfflineDataRequest("semipartial");
                            else
                                sendOfflineDataRequest("partial");
                        }

                        OrderListMainFragment.shouldCloseApp = true;
                        dialog.dismiss();
                    } else {
                        if (versionCheckState.equals("SYNC")) {
                            if (!Utilities.checkInternetConnection(BaseTabActivity.this, false))
                                showNoInternetDialog("Slight problem with data", "No internet connection");
                            else {
                                if (shouldCloseApp)
                                    sendOfflineDataRequest("semipartial");
                                else
                                    sendOfflineDataRequest("partial");
                            }
                            dialog.dismiss();
                        } else if (versionCheckState.equals("REFRESH")) {
                            dialog.dismiss();
                            showRefreshConfirmationDialog();//YD for refresh
                        }
                    }
                }
            });
            dialog.onCreate(null);
            if ((!(BaseTabActivity.mBaseTabActivity).isFinishing()) && !dialog.isShowing()) {
                dialog.show();
            }

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRefreshConfirmationDialog() {
        dialog = new MyDialog(this, this.getResources().getString(R.string.lbl_refresh_title), this.getResources().getString(R.string.lbl_logout_message), "YES");
        dialog.setkeyListender(new MyDiologInterface() {

            @Override
            public void onPositiveClick() throws JSONException {
                // on No button click
                //mActivity.popFragments(mActivity.UI_Thread); //YD when option data transfered
                dialog.dismiss();
            }

            @Override
            public void onNegativeClick() {
                // on Yes button click

                if (!Utilities.checkInternetConnection(BaseTabActivity.this, false))
                    showNoInternetDialog("Slight problem with data", "No internet connection");
                else
                    sendOfflineDataRequest("full");// YD using for case of Refresh.
                // mActivity.popFragments(mActivity.UI_Thread); //YD when option data transfered
                dialog.dismiss();
            }
        });
        dialog.onCreate(null);
        if ((!(BaseTabActivity.mBaseTabActivity).isFinishing()) && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void loadOrdersAtBoot() {

        Date today = new Date();// later use to set header

        if (PreferenceHandler.getOdrGetDate(this) != null && !PreferenceHandler.getOdrGetDate(this).equals("")) {
            today = new Date(Utilities.getCustDtDefaultTm());
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateToFrom = dateFormat.format(today);

        GetOrdersRO odrReqObj = new GetOrdersRO();
        odrReqObj.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(this) + "/mobi");
        odrReqObj.setTz("Asia/Calcutta");
        odrReqObj.setFrom(dateToFrom);
        odrReqObj.setTo(dateToFrom);
        odrReqObj.setAction("getorders");

        Order.getData(odrReqObj, this, this, GET_ORDER_REQ);
    }

    public void settingsrequest() {

        LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            try {
                String D_title = "Goto Settings Page To Enable GPS";
                String D_desc = "GPS is disabled in your device. Would you like to enable it?";

                final MyDialog dialog = new MyDialog(this, D_title, D_desc, "OK");
                dialog.setkeyListender(new MyDiologInterface() {
                    @Override
                    public void onPositiveClick() throws JSONException {
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegativeClick() {
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                        dialog.dismiss();
                    }
                });
                dialog.onCreate(null);
                if ((!(BaseTabActivity.mBaseTabActivity).isFinishing()) && !dialog.isShowing()) {
                    dialog.show();
                }

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;
                dialog.getWindow().setAttributes(lp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openGoogleNavigation(Double latitude, Double longitude) {
        String geo = Utilities.getLocation(this);
        String geoArr[] = geo.split(",");
        Double startLat = 0.0, startLong = 0.0;
        if (geoArr[0] != null && !geoArr[0].isEmpty())
            startLat = Double.valueOf(geoArr[0]);
        if (geoArr[1] != null && !geoArr[0].isEmpty())
            startLong = Double.valueOf(geoArr[1]);

        Utilities.log(this, "opening map navigation with startpoint as saddr=" + startLat + "," + startLong + "&daddr=" + latitude + "," + longitude);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + "saddr=" + startLat + "," + startLong + "&daddr=" + latitude + "," + longitude));
        startActivity(intent);
    }

    class mDateSetListener implements DatePickerDialog.OnDateSetListener {


        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (!view.isShown()) {
                return;
            }
            if (buttonClickedId == 1) {
                if (OrderListMainFragment.optionRefreshState == 4) {
                    return;
                }
                OrderListMainFragment.optionRefreshState = 4;
                int mYear = year;
                int mMonth = monthOfYear + 1;
                int mDay = dayOfMonth;
                String editMonth;
                String editDate;
                if (mMonth < 10)
                    editMonth = "0" + mMonth;
                else
                    editMonth = mMonth + "";

                if (mDay < 10)
                    editDate = "0" + mDay;
                else
                    editDate = mDay + "";

                String mCurrentDate = mYear + "-" + editMonth + "-" + editDate;
                // YD using for case of change date option.8
                PreferenceHandler.setOdrGetDate(BaseTabActivity.this, mCurrentDate);
                mJobFragment.TaskAction(BaseTabActivity.this);
                //mActivity.popFragments(mActivity.UI_Thread);
			/*if (mStartTimeBg.getTag().equals(1)) {
				mTxtStrtCal.setText(changeDateFormat(mCurrentDate));
				mTxtStrtCal.setTag(mCurrentDate);
			} else {
				mTxtVwEndCal.setText(changeDateFormat(mCurrentDate));
				mTxtVwEndCal.setTag(mCurrentDate);
			}*/
            }
        }
    }

    class Asyncc extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {

            Log.i(Utilities.TAG, "Async task doInBackground called for getmversion");
            Map<String, String> getOrderParams = new HashMap<String, String>();
            getOrderParams.put("action", "getmversion");
            getOrderParams.put("tid", "apk");

            String response = null;
            try {
                response = HttpConnection.get(BaseTabActivity.this,
                        "https://" + PreferenceHandler.getPrefBaseUrl(getApplicationContext()) + "/mobi", getOrderParams);//<data><success>true</success><id>2.3</id></data>
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return XMLHandler.XML_DATA_ERROR;
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (response != null) {
                Utilities.log(BaseTabActivity.this, "server response : " + response);
                XMLHandler xmlhandler = new XMLHandler(BaseTabActivity.this);
                Document doc = xmlhandler.getDomElement(response);

                NodeList nl = doc
                        .getElementsByTagName(XMLHandler.KEY_DATA);
                Element e = (Element) nl.item(0);
                String success = xmlhandler.getValue(e,
                        XMLHandler.KEY_DATA_SUCCESS);

                if (success.equals(XMLHandler.KEY_DATA_RESP_FAIL) || response.contains("MobiLoginAgain")) {
                    //versionerror();
                } else {
                    String serverVersion = xmlhandler.getValue(e,
                            XMLHandler.KEY_DATA_ID);
                    double serverVersionNum = Double.valueOf(serverVersion);

                    PackageManager manager = BaseTabActivity.this.getPackageManager();
                    PackageInfo info = null;
                    try {
                        info = manager.getPackageInfo(BaseTabActivity.this.getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e1) {
                        e1.printStackTrace();
                    }

                    String appVersion = info.versionName;
                    double appVersionNum = Double.valueOf(appVersion);

                    //YD Main logic
                    if (appVersionNum >= serverVersionNum) {
                        if (versionCheckState.equals("SYNC")) {
                            if (!Utilities.checkInternetConnection(BaseTabActivity.this, false))
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showNoInternetDialog("Slight problem with data", "No internet connection");
                                    }
                                });
                            else {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (shouldCloseApp)
                                            sendOfflineDataRequest("semipartial");
                                        else
                                            sendOfflineDataRequest("partial");
                                    }
                                });
                            }
                        } else if (versionCheckState.equals("REFRESH")) {
                            showRefreshConfirmationDialog();//YD for refresh
                        }
                    } else {
                        shouldFinish = true;
                        showDialogForVersion();
                    }
                }
            } else {

            }
        }
    }
}
