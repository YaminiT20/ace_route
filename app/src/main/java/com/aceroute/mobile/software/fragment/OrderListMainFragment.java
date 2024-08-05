package com.aceroute.mobile.software.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aceroute.mobile.software.AceOfflineSyncEngine;
import com.aceroute.mobile.software.AceRouteService;
import com.aceroute.mobile.software.AppLoginPage;
import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.CommonServManager;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.SplashII;
import com.aceroute.mobile.software.adaptor.OrderListAdapter;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.reference.CheckinOut;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.Form;
import com.aceroute.mobile.software.component.reference.OrderStatus;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.dialog.CustomListner;
import com.aceroute.mobile.software.dialog.CustomStatusDialog;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.StatusDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.geodesy.Ellipsoid;
import com.aceroute.mobile.software.geodesy.GeodeticCalculator;
import com.aceroute.mobile.software.geodesy.GlobalPosition;
import com.aceroute.mobile.software.http.HttpConnection;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.network.AceRequestHandler;
import com.aceroute.mobile.software.offline.CountryListFragment;
import com.aceroute.mobile.software.requests.ClockInOutRequest;
import com.aceroute.mobile.software.requests.GetOrdersRO;
import com.aceroute.mobile.software.requests.GetPart_Task_FormRequest;
import com.aceroute.mobile.software.requests.SyncRO;
import com.aceroute.mobile.software.requests.updateOrderRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.aceroute.mobile.software.utilities.XMLHandler;
import com.splunk.mint.Mint;
//import com.crashlytics.android.Crashlytics;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

//import io.fabric.sdk.android.Fabric;

public class OrderListMainFragment extends BaseFragment implements HeaderInterface, RespCBandServST {
    int counter = 0;
    private static ListView mLstVwJob;

    TextView txt_networkconnection;
    public static boolean updateOrderFrmPb = false;
    public static long rescheduleId = -1;
    private OrderListAdapter mOrderListAdapter;
    public static int optionRefreshState = 0;
    LinkedHashMap<Long, Order> odrDataMapLinkedHM;
    ArrayList<Long> sid, prev_id;
    public static boolean shouldCloseApp = false;
    private int SYNC_OFFLINE_DATA = 1;
    private int LOAD_REF_DATA = 2;
    private int GET_ORDER_REQ = 3;
    private int GET_FORMS = 19;
    private int SAVEORDERFIELD_STATUS_PG = 394;
    private int SYNC_OFFLINE_DATA_LOGOUT = 9;

    public static boolean datarefresh = false;
    private int CHECKIN_REQ = 10;
    public static boolean changeSequenceNoOnOrder = true;
    HashMap<Long, Order> odrDataMap;
    CustomStatusDialog customDialog;
    MyDialog dialog = null;
    MyDialog syncDialog;
    FrameLayout mViewContainer;

    private int mheight = 400;//using for popup height
    public static String versionCheckState;
    static boolean shouldFinish = false;
    public static int doNotShowView = 0;
    boolean skipalgorunning = false;
    public HashMap<Integer, updateOrderRequest> updateorder_status = new HashMap<>();
    int listcount = 0;
    int skipId = 55;
    updateOrderRequest original_OrdChng;
    double speed = 48.24;

    public OrderListMainFragment() {
        Log.i("Aceroute", "inside olmp constructor");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e("AceRoute", "onAttach called for OrderListMainFragment");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("AceRoute", "onCreate1 called for OrderListMainFragment");
        logUser();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("AceRoute", "onActivityCreated called for OrderListMainFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("AceRoute", "onCreateView called for OrderListMainFragment");
        View v = inflater.inflate(R.layout.fragment_create, null);

        TypeFaceFont.overrideFonts(mActivity, v);
        mViewContainer = (FrameLayout) v.findViewById(R.id.map_container_olmp);
		/*boolean f = false;
		//f = checkIfArcMapReq();
		//YD code for arcgis map checking if the tpk is available on the path below
		//File f = new File( mActivity.getExternalFilesDir(null).toString()+"/" + DATA_RELATIVE_PATH);
		//new File( mActivity.getExternalFilesDir(null).toString()+"/" + "SKMaps").exists();
		if (new File( mActivity.getExternalFilesDir(null).toString()+"/" + DATA_RELATIVE_PATH).exists()){
			PreferenceHandler.setArcgisMapTpkState(mActivity, true);
		} else{
			PreferenceHandler.setArcgisMapTpkState(mActivity, false);
		}*/ //YD commented arcmap

        BaseTabActivity.currentActiveFrag = this;
        mActivity.registerHeader(this);
        if (doNotShowView == 1) {
            doNotShowView = 0;
            return null;
        }
        //setRetainInstance(true);

        initiViewReference(v);
        return v;
    }

    private void logUser() {//Account, Worker Name, Worker ID, Date, Time
        // TODO: Use the current user's information
        // You can call any combination of these three methods
		/*if (!Fabric.isInitialized()) {
			Fabric.with(mActivity, new Crashlytics());
		}
		Crashlytics.setUserIdentifier(String.valueOf(PreferenceHandler.getResId(mActivity)));//worker id
		Crashlytics.setUserEmail(PreferenceHandler.getCompanyId(mActivity));// account
		Crashlytics.setUserName(PreferenceHandler.getWorkerNm(mActivity));// worker name
		Crashlytics.log(1, "FabricDemo", "Time : "+ Utilities.getCurrentTime());*///YD 2020
        Mint.initAndStartSession(mActivity.getApplication(), "3eb08b6d");

        /*String a = null;
        a.toString();*/ //YD 2020 code to test splunk mint with nullpointer exception
    }

    @Override
    public void onResume() {
        Log.e("AceRoute", "onResume called for OrderListMainFragment");
        // TODO Auto-generated method stub
        try {
            setAdapter();
        } catch (Exception e) {


        }

		/*new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				setAdapter();

			}
		},3000);*/

        super.onResume();
        if (SplashII.shouldShowProgress) {
            BaseTabActivity.mProgress.setVisibility(View.VISIBLE);
        }

        TaskAction(mActivity);
        //if (optionRefreshState!= 2) { //YD this check is because user is changing the date from the option menu and then there is no need to check system/mobile date
        // YD showing dialog when the date of device is changing
        //if (PreferenceHandler.getOdrGetDate(mActivity)==null || PreferenceHandler.getOdrGetDate(mActivity).equals("")){ //YD was causing popup never to display that is why commented this
        if (!Utilities.isTodayDate(mActivity, new Date(Long.valueOf(PreferenceHandler.getlastsynctime(mActivity))))) {//YD if last sync is not equal today date
            if (!Utilities.isTodayDate(mActivity, new Date(PreferenceHandler.getSyncPopupCancelTm(mActivity)))) {
                syncDialog = new MyDialog(mActivity, "Sync Data", "Do you want to sync data with server?", "YES");
                syncDialog.setkeyListender(new MyDiologInterface() {

                    @Override
                    public void onPositiveClick() throws JSONException {
                        if (PreferenceHandler.getOdrGetDate(mActivity) == null || PreferenceHandler.getOdrGetDate(mActivity).equals("")) {//YD Clicking option no and only setting the geodrgetdata if it is null other wise we use date of customer seletcted date
                            Date d = new Date(PreferenceHandler.getlastsynctime(mActivity));
                            String mCurrentDate = Utilities.chkAndGetCustDt(mActivity, d);
                            PreferenceHandler.setOdrGetDate(mActivity, mCurrentDate);//YD setting this because now we need to always sync data for the last sync date so as we are syncing using this pref obj in aceofflincsyncengine class we are doing this.
                            PreferenceHandler.setodrLastSyncForDt(mActivity, Utilities.getCustDtDefaultTm());//YD setting this for the date because if the setOdrgetdate is not null the we make date from this
                        }
                        PreferenceHandler.setSyncPopupCancelTm(mActivity, Utilities.getCurrentTime());
                        syncDialog.dismiss();
                        notifyDataChange(mActivity);//413f6390


                    }

                    @Override
                    public void onNegativeClick() {//yes
                        PreferenceHandler.setOdrGetDate(mActivity, null);
                        sendOfflineDataRequest("partial");
                        syncDialog.dismiss();

                    }
                });
                syncDialog.onCreate(null);
                syncDialog.show();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (syncDialog != null && syncDialog.isShowing()) {
                            syncDialog.dismiss();
                            PreferenceHandler.setOdrGetDate(mActivity, null);
                            sendOfflineDataRequest("partial");
                        }
                    }
                }, 60000);
            }
        }

        if (PreferenceHandler.getAppDataChanged(mActivity)) {
            PreferenceHandler.setAppDataChanged(mActivity, false);
            notifyDataChange(mActivity);//413f6390
        }
        //}


		/*repeathandler= new Handler();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				repeatrunnable.run();
			}
		},UPDATE_INTERVAL/5);

		repeatrunnable = new Runnable() {
			public void run() {
				repeatChnageinTime();
				repeathandler.postDelayed(this, UPDATE_INTERVAL);
			}
		};*/


    }

    public void TaskAction(Activity act) {

        if (mActivity == null) {
            mActivity = (BaseTabActivity) act;
        }
        if (optionRefreshState == 1)//YD using this to using if coming back from Option frag when refresh is clicked
        {
            optionRefreshState = 0;
            if (!Utilities.checkInternetConnection(((BaseTabActivity) act), false))
                showNoInternetDialog("Slight problem with data", "No internet connection");
            else
                sendOfflineDataRequest("full");
        } else if (optionRefreshState == 2)//YD using this to using if coming back from Option frag when offline is clicked
        {
            openCountryListFrag();
            optionRefreshState = 0;
        } else if (optionRefreshState == 3) {
            optionRefreshState = 0;
            CreateOrderFragment createOdrFrag = new CreateOrderFragment();
            BaseTabActivity.pushFragToStackRequired = 1;//YD using because push is called twice once from here and another from the ontabChange
            mActivity.pushFragments(Utilities.JOBS, createOdrFrag, true, true, BaseTabActivity.UI_Thread);
        } else if (optionRefreshState == 4) { // using for route date
            optionRefreshState = 0;
            sendOfflineDataRequest("partial");
        } else if (updateOrderFrmPb) { //YD using for pb task and part and (asset count update when cust list_cal is downloaded etc)
            updateOrderFrmPb = false;
            loadOrdersAtBoot();
        } else if (optionRefreshState == 5) { //YD using for sync data option
            optionRefreshState = 0;
            if (!Utilities.checkInternetConnection(((BaseTabActivity) act), false))
                showNoInternetDialog("Slight problem with data", "No internet connection");
            else {
                if (shouldCloseApp)
                    sendOfflineDataRequest("semipartial");
                else
                    sendOfflineDataRequest("partial");
            }
        } else if (optionRefreshState == 6) { //YD using  for dynamic font size
            optionRefreshState = 0;
            showRadioDialogFontSize();
        } else if (optionRefreshState == 7) { //YD using  for ABOUT US
            optionRefreshState = 0;
            try {
                PackageManager manager = ((BaseTabActivity) act).getPackageManager();
                PackageInfo info = manager.getPackageInfo(((BaseTabActivity) act).getPackageName(), 0);
                String version = info.versionName;
                String msg = act.getResources().getString(R.string.msg_field_service_management) + "\n" + act.getResources().getString(R.string.msg_right_person_time) + "\n\n" + "Version: " + version;

                showAboutDialog(((BaseTabActivity) act).getResources().getString(R.string.msg_about_aceroute), msg, act); //MY about Application
                //mActivity.popFragments(mActivity.UI_Thread);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (optionRefreshState == 8) { //YD using  for SYNC DATA
            optionRefreshState = 0;
            if (!Utilities.checkInternetConnection(((BaseTabActivity) act), false))
                showNoInternetDialog("Slight problem with data", "No internet connection");
            else
                checkVersionForApp();
        }

    }

    private void initiViewReference(View v) {

        PreferenceHandler.setTaskTypeOld(getActivity(), "1");// MY for order task change

        setSyncTimeOnTitle();
        mLstVwJob = (ListView) v.findViewById(R.id.job_list_vw);
        txt_networkconnection = (TextView) v.findViewById(R.id.textview);
		
		/*webviewOrderListMain = (WebView) v.findViewById(R.id.webviewOrderListMain);			 
		webviewOrderListMain.setBackgroundColor(Color.TRANSPARENT);    
		webviewOrderListMain.loadUrl("file:///android_asset/loading.html");*/

        Log.d("init", "initial");

        //	webviewOrderListMain.setVisibility(View.VISIBLE);

        // after the data came from server YD
        try {
            setAdapter();
        } catch (Exception e) {

        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                try {

                    mOrderListAdapter.notifyDataSetChanged();


                } catch (Exception e) {

                }

            }
        }, 2000);

        final Handler handler = new Handler();
        final Timer timer = new Timer();
        counter = 0;
        TimerTask timertask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {

                        boolean connected = false;
                        ConnectivityManager connectivityManager = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
                        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                            //we are connected to a network
                            connected = true;
                            txt_networkconnection.setVisibility(View.GONE);

                        } else {
                            connected = false;

                            txt_networkconnection.setVisibility(View.VISIBLE);
                        }

                        Log.d("datarefresh", "false" + datarefresh);
                        //counter = counter+1;
                        //Toast.makeText(mActivity,"testt",Toast.LENGTH_LONG);
                        if (datarefresh) {

                            setAdapter();
                            datarefresh = false;
                            Log.d("datarefresh", "true" + datarefresh);

                            //	datarefresh=false;
                            //Toast.makeText(mActivity,"notifychangedatatestt",Toast.LENGTH_LONG);
                            //timer.cancel();
                        }

                        //	mOrderListAdapter.notifyDataSetChanged();


						/*if(counter >=25) {
							timer.cancel();
						}*/

                    }

                });

            }

        };

        timer.schedule(timertask, 0, 2500);



		/*final Handler handlerr = new Handler();
		final Timer timerr = new Timer();
		counter = 0;
		TimerTask timertaskk = new TimerTask() {
			@Override
			public void run() {
				handlerr.post(new Runnable() {
					public void run() {
						counter = counter+1;
						//Toast.makeText(mActivity,"testt",Toast.LENGTH_LONG);
						//setAdapter();
						mOrderListAdapter.notifyDataSetChanged();
						if(counter >=25) {
							timerr.cancel();
						}

					}

				});

			}

		};

		timerr.schedule(timertaskk, 0, 5000);*/


    }


    @Override
    public void onPause() {
        super.onPause();
        //repeathandler.removeCallbacks(repeatrunnable);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
		/*mActivity.registerHeader(this);
		mActivity.mImgSyncBtn.setVisibility(View.VISIBLE);*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (syncDialog != null && syncDialog.isShowing()) {
            syncDialog.dismiss();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setSyncTimeOnTitle() {

        long lastSyncTime = PreferenceHandler.getlastsynctime(mActivity);
        Date date;
		/*Long lastPubnubUpdateTime =PreferenceHandler.getLastPubnubUpdated(mActivity.getApplicationContext());
		if(Long.valueOf(lastSyncTime)>lastPubnubUpdateTime)*/
        date = new Date(Long.valueOf(lastSyncTime));

        if (PreferenceHandler.getOdrGetDate(mActivity) != null && !PreferenceHandler.getOdrGetDate(mActivity).equals(""))
            date = new Date(PreferenceHandler.getodrLastSyncForDt(mActivity));
		/*else
			date = new Date (Long.valueOf(lastPubnubUpdateTime));*/

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d yyyy");//Jun 21 2015
        final String headerDate = simpleDateFormat.format(date);
        String headerTime = Utilities.convertDateToAmPM(date.getHours(), date.getMinutes());
        final String headerDay = BaseTabActivity.getDayFrmDate(date.getDay());
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            BaseTabActivity.setHeaderTitle(headerDate, "", headerDay);
        } else {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BaseTabActivity.setHeaderTitle(headerDate, "", headerDay);
                    //setAdapter();
                }
            });
        }
//		mOrderListAdapter.notifyDataSetChanged();
        //setAdapter();

    }

    private void setAdapter() {
        if (DataObject.ordersXmlDataStore != null) {
            Log.d("Niki", "data" + DataObject.ordersXmlDataStore);
            Utilities.log(mActivity, "Showing OrderList from Local DataObject");
            odrDataMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
            odrDataMapLinkedHM = sortOrders(odrDataMap, mActivity);
            mOrderListAdapter = new OrderListAdapter(mActivity, this, mActivity, odrDataMapLinkedHM);
            mLstVwJob.setAdapter(mOrderListAdapter);
            //datarefresh=false;
        } else if (DataObject.orderTypeXmlDataStore == null && DataObject.orderStatusTypeXmlDataStore == null &&
                DataObject.orderPriorityTypeXmlDataStore == null) {
            Utilities.log(mActivity, "Showing OrderList from DataBase");

            PreferenceHandler.setIsCalledFromLoginPage(mActivity, "true");
            Intent intent = new Intent(mActivity, SplashII.class);//YD problem can be that if user is not remembered at time of login (then what will happen)
            mActivity.startActivity(intent);
        } else {
            // YD TODO check if the ordersXmlDataStore is null but db has order then get it from the db [vvv.imp]

            if (odrDataMap != null) {
                try {

                    odrDataMap = new HashMap<Long, Order>();
                    mOrderListAdapter = new OrderListAdapter(getActivity(), this, mActivity, odrDataMap);
                    mLstVwJob.setAdapter(mOrderListAdapter);
                } catch (Exception e) {

                }
            }
        }
    }

    private LinkedHashMap<Long, Order> sortOrders(HashMap<Long, Order> odrDataMapLoc, final BaseTabActivity ref) {

        Map<Long, Order> odrDataMapLinkedHmTmeSort = new LinkedHashMap<Long, Order>();
        Map<Long, Order> odrDataMapLinkedHMup = new LinkedHashMap<Long, Order>();
        Map<Long, Order> odrDataMapLinkedHMdw = new LinkedHashMap<Long, Order>();
        sid = new ArrayList<Long>();
        prev_id = new ArrayList<Long>();
        if (odrDataMapLoc != null && odrDataMapLoc.size() > 0) {
            odrDataMapLoc = getOrderForCurrentWorker(ref, (HashMap<Long, Order>) odrDataMapLoc.clone());

            if (odrDataMapLoc.size() > 0) {//YD check again because map may not have any order after check for current res
                odrDataMapLinkedHmTmeSort = getSortedList(odrDataMapLoc, ref);
                if (changeSequenceNoOnOrder) {
                    int i = 1;
                    for (Entry entry : odrDataMapLinkedHmTmeSort.entrySet()) {//YD adding sequence before status sort because no need to show order whose status>3
                        ((Order) entry.getValue()).setSequenceNumber(i);
                        i++;
                    }
                } else {
                    changeSequenceNoOnOrder = true;
                    MapFragment.changeSequenceNoOnOrder = true;
                }

                for (Long key : odrDataMapLinkedHmTmeSort.keySet()) {
                    Order order = odrDataMapLinkedHmTmeSort.get(key);
                    long statusID = order.getStatusId();

                    if (statusID > 3) {
                        odrDataMapLinkedHMdw.put(key, odrDataMapLinkedHmTmeSort.get(key));
                    } else {
                        odrDataMapLinkedHMup.put(key, odrDataMapLinkedHmTmeSort.get(key));
                    }
                }

                odrDataMapLinkedHMup.putAll(odrDataMapLinkedHMdw);
                showDistance(new ArrayList<Order>(odrDataMapLinkedHMup.values()));
            }
        }
        return (LinkedHashMap<Long, Order>) odrDataMapLinkedHMup;
    }

    private void showDistance(final List<Order> odrData) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 1;
                Order previousOdr = null;
                for (Order entry : odrData) {


                    //Log.d("tmplist","start:"+entry.getId());
                    if (!sid.contains(entry.getId())) {
                        sid.add(entry.getId());
                        //Log.d("tmplist","inside:"+entry.getId());
                        if (i > 1) {
                            prev_id.add(previousOdr.getId());
                            calculateDistAndTimeBtwOdrs(previousOdr.getCustSiteGeocode(), entry.getCustSiteGeocode()/*,odrData*/);
                        } else {
                            prev_id.add(0l);
                            calculateDistAndTimeBtwOdrs(Utilities.getLocation(mActivity.getApplicationContext()).trim(), entry.getCustSiteGeocode()/*,odrData*/);
                        }
                        //break;
                    }

                    previousOdr = entry;
                    i++;
                }
            }
        }).start();
		/*new Handler().post(new Runnable() {
			@Override
			public void run() {
				int i = 1;
				Order previousOdr = null;
				for (Entry entry : odrDataMapLinkedHMup.entrySet()) {


						Order order = ((Order) entry.getValue());
						if(!sid.contains((Long) entry.getKey())) {
							sid.add((Long) entry.getKey());

							if (i > 1) {
								prev_id.add(previousOdr.getId());
								calculateDistAndTimeBtwOdrs(previousOdr.getCustSiteGeocode(), order.getCustSiteGeocode(),odrDataMapLinkedHMup);
							} else {
								prev_id.add(0l);
								calculateDistAndTimeBtwOdrs(Utilities.getLocation(mActivity.getApplicationContext()).trim(), order.getCustSiteGeocode(),odrDataMapLinkedHMup);
							}
							break;
						}

						previousOdr = (Order) entry.getValue();

					i++;
				}
			}
		});*/

    }

    private HashMap<Long, Order> getOrderForCurrentWorker(BaseTabActivity ref, HashMap<Long, Order> odrDataMapLoc) {

        HashMap<Long, Order> orderDatamap = new HashMap<Long, Order>();
        for (Long key : odrDataMapLoc.keySet()) {
            Order order = odrDataMapLoc.get(key);
            if (order != null && order.getPrimaryWorkerId() != null && order.getPrimaryWorkerId().contains(String.valueOf(PreferenceHandler.getResId(ref)))) {
                orderDatamap.put(key, order);
            }
        }
        return orderDatamap;
    }

    //YD sorting based on time first (check again)
    private Map<Long, Order> getSortedList(Map<Long, Order> listToSort, BaseTabActivity ref) {
        ArrayList<Order> unsortedLst = new ArrayList<Order>();
        for (Long key : listToSort.keySet()) {

            Order order = listToSort.get(key);
            long statusID = order.getStatusId();
            Date startdate = getStatDate(order.getFromDate());

            if (PreferenceHandler.getOdrGetDate(ref) != null && !PreferenceHandler.getOdrGetDate(ref).equals("") && statusID != 8) {
                if (Utilities.chkDtEqualCurrentDt(ref, startdate)) {
                    unsortedLst.add(listToSort.get(key));
                }
            } else if (statusID == 8 || !Utilities.isTodayDate(ref, startdate)) {// YD Checking if the order date has change from current to some other through web)
                Log.i(BaseTabActivity.LOGNAME, "Order Status is Unscheduled");
            } else {
                unsortedLst.add(listToSort.get(key));
            }
        }

        Collections.sort(unsortedLst, new Comparator<Order>() {
            @Override
            public int compare(Order odr1, Order odr2) {
                long time1 = 0, time2 = 0;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
                try {
                    Date date1 = simpleDateFormat.parse(odr1.getFromDate());
                    Date date2 = simpleDateFormat.parse(odr2.getFromDate());
                    time1 = date1.getTime();
                    time2 = date2.getTime();


                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return Double.compare(time1, time2);
                //  return  odr1.getStartTime().compareTo(odr2.getStartTime());
            }
        });
		
		
		/*for (int i =0; i<unsortedLst.size();i++){
			long startTime = unsortedLst.get(i).getStartTime();
			Order order ;
			for (int j =i+1; j<unsortedLst.size();j++){
				if (startTime>unsortedLst.get(j).getStartTime())
				{
					order = unsortedLst.get(i);
					unsortedLst.add(i, unsortedLst.get(j));
					unsortedLst.add(j, order);
				}
			}
		}*/
        Map<Long, Order> localMap = new LinkedHashMap<Long, Order>();
        for (int i = 0; i < unsortedLst.size(); i++) {
            localMap.put(unsortedLst.get(i).getId(), unsortedLst.get(i));
        }

        return localMap;
    }

    // YD function to convert String date to the date object
    public Date getStatDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
        Date startdate = null;
        try {
            startdate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return startdate;
    }

    public void showNoInternetDialog(String title, String content) {

		/*String D_title = BaseTabActivity.getResources().getString(R.string.msg_slight_problem);
		String D_desc = getResources().getString(R.string.msg_internet_problem);*/
        dialog = new MyDialog(mActivity, title, content, "OK");
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

        if (!((Activity) mActivity).isFinishing()) {
            //show dialog
            try {
                dialog.show();
            } catch (WindowManager.BadTokenException e) {
                //use a log message
            }
        }

        Utilities.setDividerTitleColor(dialog, 0, mActivity);
		/*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;

		lp.gravity = Gravity.CENTER;
		dialog.getWindow().setAttributes(lp);*/
    }

    @Override
    public void headerClickListener(String callingId) {
        if (callingId.equals(mActivity.HeaderSyncPressed)) {
			/*if(!Utilities.checkInternetConnection(mActivity,false))
			{
				showNoInternetDialog("Slight problem with data" , "No internet connection");
			}
			else{
			sendOfflineDataRequest("partial" );
			}*/
        }

        if (callingId.equals(mActivity.HeaderMapAllPressed)) {
            if (Utilities.checkInternetConnection(mActivity, false)) {
                GoogleMapFragment.maptype = "OrderList";
                GoogleMapFragment GooglemapFragment = new GoogleMapFragment();
                mActivity.pushFragments(Utilities.JOBS, GooglemapFragment, true, true, BaseTabActivity.UI_Thread);
            }/*else{
				MapAllFragment.maptype = "OrderList";
			MapAllFragment mFragment = new MapAllFragment();
			mActivity.pushFragments(Utilities.JOBS, mFragment, true, true,BaseTabActivity.UI_Thread);
			}*///YD 2020
        }
    }


    public void sendOfflineDataRequest(String typeOfSync) {// YD using refActivity to handle null intent when calling from option frag
        openSyncDialog();
        SyncRO syncObj = new SyncRO();
        syncObj.setType(typeOfSync);
        //syncObj.setOdrGetDate (date);
        syncOfflineData(syncObj, SYNC_OFFLINE_DATA);

    }

    public void onlysendOfflineDataRequest(String typeOfSync) {// YD using refActivity to handle null intent when calling from option frag
        //openSyncDialog();
        SyncRO syncObj = new SyncRO();
        syncObj.setType(typeOfSync);
        //syncObj.setOdrGetDate (date);
        syncOfflineData(syncObj, SYNC_OFFLINE_DATA);

    }

    private void openSyncDialog() {

        try {
            //  if(!((Activity) BaseTabActivity.mBaseTabActivity).isFinishing()) {
            //show dialog

            customDialog = new CustomStatusDialog(BaseTabActivity.mBaseTabActivity, "Syncing Data", "OLMP");
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            customDialog.setkeyListender(new StatusDiologInterface() {

                @Override
                public void onPositiveClick(String btnTxt) throws JSONException {

                    //codecomment
				/*if (btnTxt.equals("Pause"))
				{
					customDialog.disablePauseBtn(false);//YD disabling the pause button 
					AceOfflineSyncEngine offlinesyncengine = AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(mActivity);
					offlinesyncengine.setSynctoPause(mActivity, true);
					*//*final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
					  @Override 
					  public void run() { 
						  customDialog.disablePauseBtn(true);//YD enabling the pause button 
					  } 
					}, 3000); *//*// YD adding delay for enabling the pause button
				}
				else {
					AceOfflineSyncEngine offlinesyncengine = AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(mActivity);
					offlinesyncengine.setSynctoPause(mActivity, false);
					customDialog.dismiss();
				}*/
                    //code comment
                }

                @Override
                public void onNegativeClick(String btnTxt) {
                    // TODO Auto-generated method stub
                }
            });
            //customDialog.onCreate(null);
            customDialog.disablePauseBtn(true);
            customDialog.show();

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(customDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            //	lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            // lp.height = mheight;

            lp.gravity = Gravity.CENTER;
            //set the dim level of the background
            //	lp.dimAmount=0.1f; //change this value for more or less dimming
            customDialog.getWindow().setAttributes(lp);
            //   }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void syncOfflineData(SyncRO syncObj, int reqId) {
        AceRequestHandler requestHandler = null;
        Intent intent = null;

        requestHandler = new AceRequestHandler(mActivity);
        intent = new Intent(mActivity, AceRouteService.class);

        Long currentMilli = PreferenceHandler.getPrefQueueRequestId(mActivity);

        Bundle mBundle = new Bundle();
        mBundle.putParcelable("OBJECT", syncObj);
        mBundle.putLong(AceRouteService.KEY_TIME, currentMilli);
        mBundle.putInt(AceRouteService.KEY_SYNCALL_FLAG, AceRouteService.VALUE_NOT_SYNCALL);
        mBundle.putInt(AceRouteService.FLAG_FOR_CAMERA, 0);
        mBundle.putInt(AceRouteService.KEY_ID, reqId);
        mBundle.putString(AceRouteService.KEY_ACTION, "syncalldataforoffline");

        intent.putExtras(mBundle);
        requestHandler.ServiceStarterLoc(mActivity, intent, this, currentMilli);
    }

    private void checkBootflow() {

        Date today = new Date();
        loadOrdersAtBoot();
        BaseTabActivity.refreshOrdersListfromCache = true;
    }

    public void loadOrdersAtBoot() {


        Date today = new Date();// later use to set header
        int timeForwardAndBackward = SplashII.numberOfDaysForwardAndBackward * 24 * 60 * 60 * 1000; // currently date forward and backword is zero

        if (mActivity == null)
            mActivity = BaseTabActivity.mBaseTabActivity;

        if (PreferenceHandler.getOdrGetDate(mActivity) != null && !PreferenceHandler.getOdrGetDate(mActivity).equals("")) {
            today = new Date(Utilities.getCustDtDefaultTm());
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateToFrom = dateFormat.format(today);

        GetOrdersRO odrReqObj = new GetOrdersRO();
        odrReqObj.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(mActivity) + "/mobi");
        odrReqObj.setTz("Asia/Calcutta");
        odrReqObj.setFrom(dateToFrom);
        odrReqObj.setTo(dateToFrom);
        odrReqObj.setAction("getorders");

        Order.getData(odrReqObj, mActivity, this, GET_ORDER_REQ);
        datarefresh = true;

    }

    ArrayList<Object> statusObjForForm;
    ArrayList<SiteType> lstOfFormsOdrShouldHave;
    ArrayList<String> mOrderList = new ArrayList<String>();

    // updating order status
    public boolean updateStatus(final Order order, final long currentStatId, final HashMap<Long, Order> odrDataMapLinkedHM,
                                String statusTxt, boolean shouldCheckForForm, String aPosition, CustomListner customListener) {
        boolean canChange = true;

        if (String.valueOf(PreferenceHandler.getPrefFormListCount(mActivity)) != null
                && PreferenceHandler.getPrefFormListCount(mActivity) <= 0 && currentStatId < 5 && currentStatId < 0) {
            if (currentStatId == 4) {
                PreferenceHandler.setPrefFormListCount(mActivity, 0);
                chkIfStatsCanChangBasedOnFrm(order, currentStatId, aPosition);

                return false;
            } else if (String.valueOf(PreferenceHandler.getPrefFormListCount(mActivity)) != null && currentStatId == 3 && PreferenceHandler.getPrefFormListCount(mActivity) <= 0) {
                PreferenceHandler.setPrefFormListCount(mActivity, 0);
                chkIfStatsCanChangBasedOnFrm(order, currentStatId, aPosition);

                return false;
            } else if (String.valueOf(PreferenceHandler.getPrefFormListCount(mActivity)) != null && currentStatId == 2 && PreferenceHandler.getPrefFormListCount(mActivity) <= 0) {
                PreferenceHandler.setPrefFormListCount(mActivity, 0);
                chkIfStatsCanChangBasedOnFrm(order, currentStatId, aPosition);

                return false;
            } else {
                PreferenceHandler.setPrefFormListCount(mActivity, 0);
                chkIfStatsCanChangBasedOnFrm(order, currentStatId, aPosition);

                return false;
            }
        } else {
            if (shouldCheckForForm) {
                statusObjForForm = new ArrayList<Object>();
                statusObjForForm.add(order);
                statusObjForForm.add(currentStatId);
                statusObjForForm.add(odrDataMapLinkedHM);
                statusObjForForm.add(statusTxt);
                statusObjForForm.add(customListener);
                canChange = chkIfStatsCanChangBasedOnFrm(order, currentStatId, aPosition);
            }
            if (canChange) {
                //String statusTxt = null;// YD this should be argument in this function.
                long orderId = order.getId();

                //int  event_geocode = 1;
                long oldwkf = order.getStatusId();
                if (oldwkf == currentStatId)
                    return false;

                String start_date_utc = order.getFromDate();//start_date_utc = "2013-08-30 23:15 -00:00" // already utc time
                if (start_date_utc != null)
                    start_date_utc = start_date_utc.trim();

                String end_date_utc = order.getToDate();//end_date_utc = "2013-09-31 0:40 -00:00"
                if (end_date_utc != null)
                    end_date_utc = end_date_utc.trim();

                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");    // returns before 12pm : Sat Jun 20 09:30:00 IST 2015 and after 12pm it returns: Sat Jun 20 23:30:00 IST 2015
                    Date start = simpleDateFormat.parse(start_date_utc);
                    final Date end = simpleDateFormat.parse(end_date_utc);

                    long temp_duration = (end.getTime() - start.getTime()) / 60000; //temp_duration = 44725

                    String startDateUtc = start_date_utc; //CommonUtil.convertToUTC(startDateTemp);
                    String endDateUtc = end_date_utc;

                    long orderStartTime = start.getTime();  //orderStartTime = 1377904500000
                    long orderEndTime = end.getTime();      //orderEndTime = 1380588000000
                    boolean flag = false;
                    if (PreferenceHandler.getUiconfigAutoDur(mActivity).equals("1") && statusTxt.trim().toUpperCase().equals("started")) {
                        flag = true;
                    }
                    String fieldToUpdate = "wkf";/*'order_wkfid'*/
                    ;


                    String date_list = startDateUtc + "|" + endDateUtc + "|" + orderStartTime + "|" + orderEndTime;    //date_list = "2013/08/30 23:15 -00:00|2013/09/31 0:40 -00:00|1377904500000|1380588000000", startDateUtc = "2013/08/30 23:15 -00:00", endDateUtc = "2013/09/31 0:40 -00:00", orderStartTime = 1377904500000, orderEndTime = 1380588000000

                    String valToSend = new ScheduledDetailFragment().
                            addDateChangeandsend("1", fieldToUpdate, String.valueOf(currentStatId), String.valueOf(currentStatId),
                                    temp_duration, flag);


                    String[] val = valToSend.split(Pattern.quote("##"));

                    if (customListener != null) {
                        customListener.onClick(null);
                    }
			
			/*{"url":"'+AceRoute.appUrl+'",'+'"type": "post",'+'"data":{"id": "'+orderId+'",'+
				'"name":"'+fieldToUpdate+'",'+'"value":"'+fieldValue+'",'+'"dtlist":"'+date_list+'",'+
				'"action": "'+action+'"}}*/
                    updateOrderRequest req = new updateOrderRequest();
                    req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(mActivity) + "/mobi");
                    req.setType("post");
                    req.setId(String.valueOf(orderId));
                    req.setName(val[0]);
                    req.setValue(val[1]);
                    req.setAction(Order.ACTION_SAVE_ORDER_FLD);
                    listcount = updateorder_status.size();
                    int reqno = SAVEORDERFIELD_STATUS_PG + listcount;
                    if (odrDataMapLinkedHM != null) {
                        original_OrdChng = req;
                    }
                    updateorder_status.put(reqno, req);

                    //YD logic of eviction start here
                    if (PreferenceHandler.getUiconfigDelaycolle(mActivity) != 0 && odrDataMapLinkedHM != null) {
                        skipalgorunning = true;
                        checkForEvictionOfOrder(order, currentStatId, start, end, odrDataMapLinkedHM, new ArrayList<Long>());
                    } else if (PreferenceHandler.getUiconfigDelaycolle(mActivity) == 0 && odrDataMapLinkedHM != null) {
                        updateorder_status.clear();
                        listcount = 0;
                        Order.saveOrderField(req, mActivity, this, SAVEORDERFIELD_STATUS_PG);

                    }

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return true;
        }
    }

    /**
     * YD function to check if status can be changed based on forms available for that status ID
     *
     * @param currentStatId
     * @return
     */


    private boolean chkIfStatsCanChangBasedOnFrm(Order order, long currentStatId, String pos) {

        try {
            HashMap<Long, SiteType> siteAndGenTypeList = (HashMap<Long, SiteType>) DataObject.siteTypeXmlDataStore;
            HashMap<Long, Form> savedforms = (HashMap<Long, Form>) DataObject.orderFormsXmlStore;

            HashMap<Long, SiteType> siteGenTypeMapTid_10 = new HashMap<Long, SiteType>();
            Long keys[] = siteAndGenTypeList.keySet().toArray(new Long[siteAndGenTypeList.size()]);

            lstOfFormsOdrShouldHave = new ArrayList<SiteType>();
            for (int i = 0; i < siteAndGenTypeList.size(); i++) {
                SiteType siteType = siteAndGenTypeList.get(keys[i]);
                System.out.println(" cap value type id" + siteType.getTid());
                switch (siteType.getTid()) {
                    case 10:
                        String[] arr = new String[0];
                        ArrayList<String> list = new ArrayList<>();

                        if (siteType.getCap().contains("|")) {
                            arr = siteType.getCap().split(Pattern.quote("|"));
                            list.addAll(Arrays.asList(arr));

                        } else {
                            list.add(siteType.getCap());
                        }


                        mOrderList = PreferenceHandler.getArrayPrefs("OrderList", mActivity);
                        System.out.println("value checking preference list  " + mOrderList.toString());

                        for (int capIndex = 0; capIndex < list.size(); capIndex++) {
//                            for (int p = 0; p < mOrderList.size(); p++) {
                            if (list.get(capIndex).equalsIgnoreCase(mOrderList.get(Integer.parseInt(pos)))) {
                                JSONObject mainJsonForSiteType = new JSONObject(siteType.getDtl());
                                if (mainJsonForSiteType.opt("rules") != null && mainJsonForSiteType.getJSONObject("rules") != null) {
                                    JSONObject ruleObj = mainJsonForSiteType.getJSONObject("rules");
                                    String[] sts = ruleObj.getString("sts").split("\\,");

                                    for (int j = 0; j < sts.length; j++) {
                                        if (Long.parseLong(sts[j]) == currentStatId) {
                                            lstOfFormsOdrShouldHave.add(siteType);
                                        }
                                    }
                                }
//                                }
                            }
                        }


//                        for (int capIndex = 0; capIndex < list.size(); capIndex++) {
//                            System.out.println(" cap value index" + list.get(capIndex));
//                            if (list.get(capIndex).equalsIgnoreCase("")
//                                    || list.get(capIndex).equalsIgnoreCase(PreferenceHandler.getPrefCatid(mActivity))) {
//                                JSONObject mainJsonForSiteType = new JSONObject(siteType.getDtl());
//
//                                if (mainJsonForSiteType.opt("rules") != null && mainJsonForSiteType.getJSONObject("rules") != null) {
//                                    JSONObject ruleObj = mainJsonForSiteType.getJSONObject("rules");
//                                    String[] sts = ruleObj.getString("sts").split("\\,");
//
//                                    for (int j = 0; j < sts.length; j++) {
//                                        if (Long.valueOf(sts[j]) == currentStatId) {
//                                            lstOfFormsOdrShouldHave.add(siteType);
//                                        }
//                                    }
//                                }
//                            }
//                        }

                }
            }
            //YD 2020 now check if all the forms are available for this order or not

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (lstOfFormsOdrShouldHave.size() > 0) {
            getForms(order);
            return false;
        }
        return true;
    }

    private void getForms(Order order) {
        long numberOfForms = order.getCustFormCount();

        if (numberOfForms >= 0) {
            //Get OrderPart req : {"url":"'+AceRoute.appUrl+'",'+'"action":"getorderpart",'+'"oid":"'+orderId+'"}
            GetPart_Task_FormRequest req = new GetPart_Task_FormRequest();
            req.setAction(Form.ACTION_GET_ORDER_FORM);
            req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(mActivity) + "/mobi");
            req.setOid(order.getId() + "");

            Form.getData(req, mActivity, OrderListMainFragment.this, GET_FORMS);
        }
    }

    private void updateStatusList() {
        if (updateorder_status.size() > 0) {
            for (Entry entry : updateorder_status.entrySet()) {
                //Log.d("tmplist","rcode:"+(Integer) entry.getKey());
                Order.saveOrderField((updateOrderRequest) entry.getValue(), mActivity, OrderListMainFragment.this, (Integer) entry.getKey());
                break;
            }
        }
        skipalgorunning = false;
    }

    private void updateStatus_onSkipFailed() {
        Order.saveOrderField(original_OrdChng, mActivity, this, SAVEORDERFIELD_STATUS_PG);
        skipalgorunning = false;
    }

    HashMap<Long, OrderStatus> mapOdrStatus = (HashMap<Long, OrderStatus>) DataObject.orderStatusTypeXmlDataStore;

    private boolean checkForEvictionOfOrder(final Order order, final long currentStatId, final Date start, final Date end, final HashMap<Long, Order> odrDataMapLinkedHM, final ArrayList<Long> skipped) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (currentStatId == 4 || currentStatId == 5 || currentStatId == 6 || currentStatId == 9 || currentStatId == 10 || currentStatId > 29) {
                    //if (currentStatId == 4 || currentStatId ==5 || currentStatId == 6 || currentStatId >30){
                    //status 4,5,6,9,10,>29
                    distance_A2B = 0;
                    long currentMillis = Utilities.getCurrentTime();
                    long workerLateOnOrder = 0;
                    if (end.getTime() < currentMillis && currentMillis - end.getTime() > (PreferenceHandler.getUiconfigDelaycolle(mActivity)) * 60 * 1000) {
                        workerLateOnOrder = currentMillis - end.getTime();//10 mins * millis
                        ArrayList<Order> orderSubList = new ArrayList<Order>();//YD sublist of order from where the worker gets late and till the next high priority order
                        boolean saveOdr = false;
                        for (Long ids : odrDataMapLinkedHM.keySet()) {
                            if (skipped.contains(ids)) {
                                continue;
                            }
                            if (ids == order.getId()) {
                                saveOdr = true;
                                orderSubList.add(odrDataMapLinkedHM.get(ids));
                            }
                            //<Arti: Anything less than 4 shd be added
                            else if (saveOdr && (odrDataMapLinkedHM.get(ids).getPriorityTypeId() == 0 || odrDataMapLinkedHM.get(ids).getPriorityTypeId() == 1 ||
                                    odrDataMapLinkedHM.get(ids).getPriorityTypeId() == 2 || odrDataMapLinkedHM.get(ids).getPriorityTypeId() == 3)) {
                                orderSubList.add(odrDataMapLinkedHM.get(ids));
                            } else if (saveOdr) {
                                orderSubList.add(odrDataMapLinkedHM.get(ids));
                                break;
                            }

                        }
                        if (orderSubList.size() > 2) { //YD checking with one because sublist is itself including the late order
                            //loop to check if order with the high priority is delayed
                            long orderDelayedBy = 0;

                            for (int i = 0; i < orderSubList.size() - 1; i++) {
                                Order odr = orderSubList.get(i);
                                Order nxtOdr = orderSubList.get(i + 1);

                                if (i == 0)
                                    orderDelayedBy = checkIfOrderIsLate(odr, nxtOdr, false, "", workerLateOnOrder);
                                else if (orderDelayedBy > 0)//YD if less than zero that means order is delayed
                                    orderDelayedBy = checkIfOrderIsLate(odr, nxtOdr, false, "", orderDelayedBy);
                                else
                                    orderDelayedBy = checkIfOrderIsLate(odr, nxtOdr, false, "", 0);

                                if (orderDelayedBy == -2) {
                                    updateorder_status.clear();
                                    orderSubList.clear();
                                    orderDelayedBy = 0;
                                    updateStatus_onSkipFailed();
                                    break;
                                }
                            }
                            // YD skipping the orders
                            if (orderDelayedBy > 0) {
                                for (int j = 1; j < orderSubList.size() - 1; j++) {
                                    Order pointA;
                                    Order pointB;
                                    boolean flagA = false;//yd flag to maintain the loop logic
                                    boolean flagB = false;
                                    double distanceAToZ = 0;
                                    boolean anyError = false;
                                    for (int k = 0; k < orderSubList.size() - 2; k++) {
                                        if (k == j || flagA) {
                                            flagA = true;
                                            pointA = orderSubList.get(k + 1);
                                        } else
                                            pointA = orderSubList.get(k);

                                        if (k + 1 == j || flagB) {
                                            flagB = true;
                                            pointB = orderSubList.get(k + 2);
                                        } else
                                            pointB = orderSubList.get(k + 1);

                                        //YD again check if the order is getting delay or not after skipping orders from the list
                                        if (k == 0) {
                                            orderDelayedBy = checkIfOrderIsLate(pointA, pointB, false, "", workerLateOnOrder);
                                            distanceAToZ = distance_A2B;
                                        } else if (orderDelayedBy > 0) {
                                            orderDelayedBy = checkIfOrderIsLate(pointA, pointB, false, "", orderDelayedBy);
                                            distanceAToZ = distanceAToZ + distance_A2B;
                                        } else {
                                            orderDelayedBy = checkIfOrderIsLate(pointA, pointB, false, "", 0);
                                            distanceAToZ = distanceAToZ + distance_A2B;
                                        }
                                        //YD now check again is the last order is getting delayed or not
                                        //if delayed then the skipped order is not feasable else
                                        // get the distance travel and keep it in consideration
                                        if (orderDelayedBy == -2) {
                                            orderSubList.clear();
                                            updateorder_status.clear();
                                            anyError = true;
                                            break;
                                        }
                                    }//k for loop end

                                    if (anyError) {
                                        updateStatus_onSkipFailed();
                                        return;
                                    }

                                    if (orderDelayedBy < 0) {
                                        // skipped order is not feasiable to skip
                                        orderSubList.get(j).setDistanceForSkip(-1);
                                    } else {
                                        orderSubList.get(j).setDistanceForSkip(distanceAToZ);
                                    }
                                }//j loop end
                                int lastknownHighDistForOdr = -1;
                                double lastKnownDistance = -1;
                                //List<In>
                                //find the best order to remove and change the status to reschedule
                                for (int i = 0; i < orderSubList.size(); i++) {
                                    Order localOdr = orderSubList.get(i);

                                    if (localOdr.getDistanceForSkip() != -1 && i == 0) {
                                        //lastknownHighDistForOdr = i;  //YD keeping the order index
                                        //lastKnownDistance = localOdr.getDistanceForSkip();
                                    } else if (localOdr.getDistanceForSkip() != -1 && localOdr.getDistanceForSkip() > lastKnownDistance) {
                                        lastknownHighDistForOdr = i;  //YD keeping the order index
                                        lastKnownDistance = localOdr.getDistanceForSkip();
                                    }
                                }

                                if (lastknownHighDistForOdr != -1) {
                                    // YD change status of the order to reschedule
                                    //updateStatus(orderSubList.get(lastknownHighDistForOdr), rescheduleId, null);
									/*HashMap<Long, Order> reSubList = new HashMap<Long, Order>();
									for (int k = 2; k < orderSubList.size(); k++) {
										reSubList.put(orderSubList.get(k).getId(),orderSubList.get(k));
									}
									reSubList.put(order.getId(),orderSubList.get(0));
									reSubList.put(orderSubList.get(lastknownHighDistForOdr).getId(),orderSubList.get(lastknownHighDistForOdr));
									*/
                                    updateStatus(orderSubList.get(lastknownHighDistForOdr), skipId, null,
                                            mapOdrStatus.get(currentStatId).getNm(), false, "", null);//YD 2020
									/*Order ord = orderSubList.get(lastknownHighDistForOdr);
									String start_date_utc = ord.getFromDate();//start_date_utc = "2013-08-30 23:15 -00:00" // already utc time
									if( start_date_utc != null)
										start_date_utc = start_date_utc.trim();

									String end_date_utc = ord.getToDate();//end_date_utc = "2013-09-31 0:40 -00:00"
									if( end_date_utc != null )
										end_date_utc = end_date_utc.trim();

										SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");	// returns before 12pm : Sat Jun 20 09:30:00 IST 2015 and after 12pm it returns: Sat Jun 20 23:30:00 IST 2015
									Date start = null;
									Date end = null;
									try {
										start = simpleDateFormat.parse(start_date_utc);
										end = simpleDateFormat.parse(end_date_utc);
										checkForEvictionOfOrder(ord,55,start,end,odrDataMapLinkedHM);
									} catch (ParseException e) {
										e.printStackTrace();
									}*/
                                    skipped.add((orderSubList.get(lastknownHighDistForOdr)).getId());
                                    checkForEvictionOfOrder(order, currentStatId, start, end, odrDataMapLinkedHM, skipped);
                                } else {
                                    //call update
                                    updateStatusList();
                                }
                            } else {
                                //call update
                                updateStatusList();
                            }
                        } else {
                            //Yd no order to skip
                            //call update
                            updateStatusList();
                        }
                    } else {
                        //call update
                        updateStatusList();
                    }
                } else {
                    //call update
                    updateStatusList();
                }
            }
        }).start();
        return false;
    }

    //KB for calculating distance and time between orders
    double distance_A2B;
    long time_A2B;

    private long checkIfOrderIsLate(Order vOrder_A, Order vOrder_B, boolean is_last_order, String type, long workerLateOnOrder) {
        distance_A2B = 0;
        time_A2B = 0;
        String point = null;
        String[] geoCodeArray_A = vOrder_A.getCustSiteGeocode().split(","); // geocode of virtual order
        String[] geoCodeArray_B = vOrder_B.getCustSiteGeocode().split(",");

        //Travel time From A2B
        //distance_A2B = getDistancebetween(Double.valueOf(geoCodeArray_A[0]),Double.valueOf(geoCodeArray_A[1]),
        //		Double.valueOf(geoCodeArray_B[0]), Double.valueOf(geoCodeArray_B[1]));

        long travelTimeA2B = 0;

        calculateDistAndTimeBtwOdrs(vOrder_A.getCustSiteGeocode(), vOrder_B.getCustSiteGeocode());

		/*boolean errorinTime=false;
		if(vOrder_B.getDistanceFmOrderX_Id()==vOrder_A.getId()){
			travelTimeA2B=vOrder_B.getTimeFmOrder();
			if(travelTimeA2B!=-1){
				errorinTime=true;
			}
		}


		if(!errorinTime){

			//YD if within 5 second skobbler doesnt respond then dont wait for response and move on (check while code)
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					SkipHandler= new Handler();
					SkipRunnable = new Runnable() {
						public void run() {
							SkipHandler.removeCallbacks(SkipRunnable);
							if(time_A2B==0){
								time_A2B=-1;
							}
						}
					};
					SkipHandler.postDelayed(SkipRunnable, 5000);
				}
			});

			getDistancebetweenBySK(Double.valueOf(geoCodeArray_A[0]),Double.valueOf(geoCodeArray_A[1]),
					Double.valueOf(geoCodeArray_B[0]), Double.valueOf(geoCodeArray_B[1]));
			while (true) {
				//Log.d("kubt","while:"+time_A2B);
				if (time_A2B != 0) {
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							SkipHandler.removeCallbacks(SkipRunnable);
						}
					});

					if (time_A2B == -1) {
						return -2;
					} else {
						travelTimeA2B = time_A2B;
						time_A2B = -1;
					}
					break;
				}
			}

		}*/
        //YD 40 is the speed  T=d/s
        //System.out.println("Befor Insertion Travel Time From A2B is : "+travelTimeA2B);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
        long waitTimeA2B = 0;
        try {
            Date date1 = simpleDateFormat.parse(vOrder_B.getFromDate());
            Date date2 = simpleDateFormat.parse(vOrder_A.getToDate());

            //Wait Time From A2B
            waitTimeA2B = (long) (date1.getTime() - (travelTimeA2B + date2.getTime() + workerLateOnOrder));
            //System.out.println("Before Insertion Wait Time From A2B is : "+waitTimeA2B);
            if (waitTimeA2B < 0) {
                return (travelTimeA2B + date2.getTime() + workerLateOnOrder) - date1.getTime(); // this means order is delayed by the returned number of millis
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;//waitTimeA2B;

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
    public void setResponseCBActivity(Response response) {
        Utilities.log(mActivity, "OLMP response Received with Id :" + response.getId());
        if (response != null) {
            Log.d("TAG11", String.valueOf(response.getId()));
            if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED))) {
                if (response.getId() == GET_FORMS) {

                    HashMap<Long, Form> orderFormListMap = (HashMap<Long, Form>) response.getResponseMap();
                    final ArrayList<SiteType> tempformsshouldhave = new ArrayList<>();

                    if (orderFormListMap != null && orderFormListMap.size() > 0) {
                        Long[] keys = orderFormListMap.keySet().toArray(new Long[orderFormListMap.size()]);
                        boolean isAvailable = false;
                        int isform = 0;
                        long notfoundGenTypeId = 0;

                        for (int i = 0; i < lstOfFormsOdrShouldHave.size(); i++) {
                            isAvailable = false;

                            long genTypeId = lstOfFormsOdrShouldHave.get(i).getId();//YD gentype tell us which all form we can create.
                            notfoundGenTypeId = 0;
                            for (long key : orderFormListMap.keySet()) {
                                if (Long.valueOf(orderFormListMap.get(key).getFtid()) == genTypeId) {
                                    isAvailable = true;
                                    isform = isform + 1;
                                    notfoundGenTypeId = genTypeId;
                                }
                            }
                            if (isAvailable == false) {
                                tempformsshouldhave.add(lstOfFormsOdrShouldHave.get(i));
                            }

                        }

                        if (isform != lstOfFormsOdrShouldHave.size()) {
                            final long finalNotfoundGenTypeId = notfoundGenTypeId;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int k = 0;
                                    String errorString = null;

                                    for (SiteType siteType : tempformsshouldhave) {
                                        if (finalNotfoundGenTypeId != 0 && siteType.getId() != finalNotfoundGenTypeId) {
                                            errorString = k == 0 ? siteType.getNm() : errorString + ", " + siteType.getNm();
                                        } else {
                                            errorString = k == 0 ? siteType.getNm() : errorString + ", " + siteType.getNm();
                                        }
                                        k++;
                                    }
                                    lstOfFormsOdrShouldHave.removeAll(lstOfFormsOdrShouldHave);
                                    showNoInternetDialog("Attach Forms", "Please attach " + errorString + " before selecting the status");

                                }
                            });
                            return;
                        }

                        Order order = (Order) statusObjForForm.get(0);
                        long currentStatId = (long) statusObjForForm.get(1);
                        HashMap<Long, Order> odrDataMapLinkedHM = (HashMap<Long, Order>) statusObjForForm.get(2);
                        String statusTxt = (String) statusObjForForm.get(3);
                        CustomListner customListner = (CustomListner) statusObjForForm.get(4);

                        updateStatus(order, currentStatId, odrDataMapLinkedHM, statusTxt, false, "", customListner);//YD 2020
                    } else {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int k = 0;
                                String errorString = null;

                                for (SiteType siteType : lstOfFormsOdrShouldHave) {
                                    errorString = k == 0 ? siteType.getNm() : errorString + ", " + siteType.getNm();
                                    k++;
                                }
                                lstOfFormsOdrShouldHave.removeAll(lstOfFormsOdrShouldHave);
                                showNoInternetDialog("Attach Forms", "Please attach " + errorString + " before selecting the status");
                            }
                        });
                        return;
                    }
                }

                //Log.d("tmplist","rcode/get:"+response.getId());
                if (response.getId() == SYNC_OFFLINE_DATA) {

                    datarefresh = true;
                    Log.d("Niki", "sycn data received");
                    //setAdapter();
					/*final Handler handler = new Handler();
					final Timer timer = new Timer();
					counter = 0;
					TimerTask timertask = new TimerTask() {
						@Override
						public void run() {
							handler.post(new Runnable() {
								public void run() {
									Log.d("Niki","adaper calling");
									//counter = counter+1;
									//Toast.makeText(mActivity,"testt",Toast.LENGTH_LONG);
									//setAdapter();
									Toast.makeText(mActivity,"testt",Toast.LENGTH_LONG);
									setAdapter();
									//timer.cancel();
						*//*if(counter >=25) {

						}*//*

								}

							});

						}

					};

					timer.schedule(timertask, 0, 1800);*/

                    //refreshOrderList();
                    //mOrderListAdapter.notifyDataSetChanged();
                    //setAdapter();

//					new Handler().postDelayed(new Runnable() {
//						@Override
//						public void run() {
//
//							Toast.makeText(mActivity,"pubnub counter issue",Toast.LENGTH_LONG).show();
//							setAdapter();
//
//						}
//					},2500);

                    PreferenceHandler.setSyncPopupCancelTm(mActivity, 0L);//YD making it zero because may the date has been changed
                    if (PreferenceHandler.getOdrGetDate(mActivity) != null && !PreferenceHandler.getOdrGetDate(mActivity).equals("")) {
                        PreferenceHandler.setodrLastSyncForDt(mActivity, Utilities.getCustDtDefaultTm());
                        PreferenceHandler.setlastsynctime(mActivity, Utilities.getCurrentTime());

                        // YD if the date changed is the current date then clear the preference
                        if (Utilities.isTodayDate(mActivity, new Date(Long.valueOf(PreferenceHandler.getodrLastSyncForDt(mActivity))))) {
                            PreferenceHandler.setOdrGetDate(mActivity, null);
                        }
                    } else
                        PreferenceHandler.setlastsynctime(mActivity, Utilities.getCurrentTime());

                    BaseTabActivity.setTmeFrOLMPHeader(mActivity.getApplicationContext());
                    if (customDialog != null)
                        mActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                customDialog.showHideView();
                                customDialog.disablePauseBtn(true);
                                setSyncTimeOnTitle();
                            }
                        });


                    // YD closing the app when version is not equal "on option fragment"
                    if (shouldCloseApp) {
                        shouldCloseApp = false;
                        mActivity.finish();
                    }
                    CommonServManager comMang = new CommonServManager(mActivity, mActivity, this);
                    comMang.loadRefData("localonly", LOAD_REF_DATA);
                    //
                    //	refreshOrderList();
                    //notifyDataChange(mActivity);
					/*mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {

						}
					});*/
                    //	setAdapter();
                }
                if (response.getId() == LOAD_REF_DATA) {
                    checkBootflow();
                }

                if (response.getId() == GET_ORDER_REQ) {
                    DataObject.ordersXmlDataStore = null;
                    DataObject.ordersXmlDataStore = response.getResponseMap();
                    refreshOrderList();
                }
				/*if (response.getId()==GET_ORDER_TYPE_REQ)
				{
					totalDataResForAdp++;
					DataObject.orderTypeXmlDataStore = response.getResponseMap();
					if (totalDataResForAdp==totalDataReqForAdp){
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (checkIfReqStoreHasData()){
									mOrderListAdapter = new OrderListAdapter(mActivitygetActivity(),OrderListMainFragment.this,mActivity,odrDataMapLinkedHM);
									mLstVwJob.setAdapter(mOrderListAdapter);
									//mOrderListAdapter.notifyDataSetChanged(); // YD commented for running pubnub order list_cal refreshing
								}

							}
						});
					}
				}
				if (response.getId()==GET_ORDER_STATUS_REQ)
				{
					totalDataResForAdp++;
					DataObject.orderStatusTypeXmlDataStore = response.getResponseMap();
					if (totalDataResForAdp==totalDataReqForAdp){
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (checkIfReqStoreHasData()){
									mOrderListAdapter = new OrderListAdapter(mActivitygetActivity(),OrderListMainFragment.this,mActivity,odrDataMapLinkedHM);
									mLstVwJob.setAdapter(mOrderListAdapter);
									//mOrderListAdapter.notifyDataSetChanged(); // YD commented for running pubnub order list_cal refreshing
								}
							}
						});
					}
				}
				if (response.getId()==GET_PRIORITY_REQ)
				{
					totalDataResForAdp++;
					DataObject.orderPriorityTypeXmlDataStore = response.getResponseMap();
					if (totalDataResForAdp==totalDataReqForAdp){
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (checkIfReqStoreHasData()){
									mOrderListAdapter = new OrderListAdapter(mActivitygetActivity(),OrderListMainFragment.this,mActivity,odrDataMapLinkedHM);
									mLstVwJob.setAdapter(mOrderListAdapter);
									//mOrderListAdapter.notifyDataSetChanged(); // YD commented for running pubnub order list_cal refreshing
								}
							}
						});
					}
				}*/
				/*if (response.getId()==SAVEORDERFIELD_STATUS_PG)
				{
					OrderListMainFragment.changeSequenceNoOnOrder=true;
					MapFragment.changeSequenceNoOnOrder=true;

					if (!(PreferenceHandler.getClockInStat(mActivity)!=null && PreferenceHandler.getClockInStat(mActivity).equals(CheckinOut.CLOCK_IN)))
					{
						//KB Request for checkin
						ClockInOutRequest clkInOut = new ClockInOutRequest();
						clkInOut.setTid("1");
						clkInOut.setType("post");
						clkInOut.setAction(CheckinOut.CC_ACTION);
						CheckinOut.getData(clkInOut, mActivity, OrderListMainFragment.this, CHECKIN_REQ);
					}
					else {
						loadOrdersAtBoot();
					}
				}*/
                if (response.getId() >= SAVEORDERFIELD_STATUS_PG && response.getId() <= (SAVEORDERFIELD_STATUS_PG + listcount)) {
                    OrderListMainFragment.changeSequenceNoOnOrder = true;
                    MapFragment.changeSequenceNoOnOrder = true;
                    if (updateorder_status.size() <= 1) {
                        if (!(PreferenceHandler.getClockInStat(mActivity) != null && PreferenceHandler.getClockInStat(mActivity).equals(CheckinOut.CLOCK_IN))) {
                            //KB Request for checkin
                            ClockInOutRequest clkInOut = new ClockInOutRequest();
                            clkInOut.setTid("1");
                            clkInOut.setType("post");
                            clkInOut.setAction(CheckinOut.CC_ACTION);
                            CheckinOut.getData(clkInOut, mActivity, OrderListMainFragment.this, CHECKIN_REQ);
                        } else {
                            loadOrdersAtBoot();
                        }
                        updateorder_status.clear();
                    } else {
                        updateorder_status.remove(response.getId());
                        updateStatusList();
                    }

                }

                if (response.getId() == CHECKIN_REQ) {
                    PreferenceHandler.setClockInStat(mActivity, "1");
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.ChangeCheckout(0);
                        }
                    });
                    loadOrdersAtBoot();
                }
            }
            //********************** failure **********************
            else if (response.getStatus().equals("failure")) {

                if (response.getId() == SYNC_OFFLINE_DATA) {
                    if (response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_INTERNET_CONNECTION)))
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (customDialog != null) {
                                    customDialog.showHideView();
                                    customDialog.disablePauseBtn(true);
                                }
                            }
                        });

                    if (response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.RESPONSE_SYNC_PAUSED))) {
                        Utilities.sendmessageUpdatetoserver(mActivity, "Paused", SplashII.MESSAGE_SENDUPDATE_TO_UI);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                customDialog.disablePauseBtn(true);
                                customDialog.showHideView();
                            }
                        });
                    }
                    if (response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.IO_ERROR))) {
                        Utilities.sendmessageUpdatetoserver(mActivity, "Paused", SplashII.MESSAGE_SENDUPDATE_TO_UI);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                customDialog.disablePauseBtn(true);
                                customDialog.showHideView();
                            }
                        });
                    }
                }

                if (response.getId() == LOAD_REF_DATA) {
                    Intent intent = new Intent(mActivity, AppLoginPage.class);
                    startActivity(intent);
                }
                if (response.getId() == GET_ORDER_REQ) {
                    if (response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA))) {
                        DataObject.ordersXmlDataStore = null;
                        DataObject.ordersXmlDataStore = response.getResponseMap();

                        if (odrDataMap == null)
                            odrDataMap = new HashMap<Long, Order>();
                        else
                            odrDataMap.clear();

                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mOrderListAdapter = new OrderListAdapter(mActivity, OrderListMainFragment.this, mActivity, odrDataMap);
                                mLstVwJob.setAdapter(mOrderListAdapter);
                                //mOrderListAdapter.notifyDataSetChanged(); // YD commented for running pubnub order list_cal refreshing
                            }
                        });
                    }
                }
                if (response.getId() == CHECKIN_REQ) {
                    loadOrdersAtBoot();
                }
                if (response.getId() <= SAVEORDERFIELD_STATUS_PG && response.getId() >= (SAVEORDERFIELD_STATUS_PG + updateorder_status.size())) {
                    if (updateorder_status.size() >= 1) {
                        if (!(PreferenceHandler.getClockInStat(mActivity) != null && PreferenceHandler.getClockInStat(mActivity).equals(CheckinOut.CLOCK_IN))) {
                            //KB Request for checkin
                            ClockInOutRequest clkInOut = new ClockInOutRequest();
                            clkInOut.setTid("1");
                            clkInOut.setType("post");
                            clkInOut.setAction(CheckinOut.CC_ACTION);
                            CheckinOut.getData(clkInOut, mActivity, OrderListMainFragment.this, CHECKIN_REQ);
                        } else {
                            loadOrdersAtBoot();
                        }
                        updateorder_status.clear();
                    } else {
                        updateorder_status.remove(response.getId());
                        updateStatusList();
                    }

                }
            }
        }
    }

    public void refreshOrderList() {
        Log.e("refreesh", "isize");
        datarefresh = true;


        Log.d("check", "" + datarefresh);
        Toast.makeText(mActivity, "Data Updated", Toast.LENGTH_LONG).show();
        if (odrDataMap == null)
            odrDataMap = new HashMap<Long, Order>();
        else
            odrDataMap.clear();

        odrDataMap.putAll((HashMap<Long, Order>) DataObject.ordersXmlDataStore);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                odrDataMapLinkedHM = sortOrders(odrDataMap, mActivity);
                MapFragment.setMapOrderXMLGM(odrDataMapLinkedHM);
                //YD TODO call MapFragment.setMapOrderXMLGM to set new sequence for map annotations
                Utilities.log(mActivity, "Response Recievied with data size :" + odrDataMapLinkedHM.size());
                mOrderListAdapter = new OrderListAdapter(mActivity, OrderListMainFragment.this, mActivity, odrDataMapLinkedHM);
                Utilities.log(mActivity, "list_cal view object is :" + mLstVwJob);
                mLstVwJob.setAdapter(mOrderListAdapter);

                //	datarefresh=true;
                //mOrderListAdapter.notifyDataSetChanged(); // YD commented for running pubnub order list_cal refreshing


            }
        });
    }

    public void notifyDataChange(Response response, BaseTabActivity ref) {

        onlysendOfflineDataRequest("full");
        DataObject.ordersXmlDataStore = null;
        DataObject.ordersXmlDataStore = response.getResponseMap();
        LinkedHashMap<Long, Order> odrDataMapLinkedHM = sortOrders((HashMap<Long, Order>) DataObject.ordersXmlDataStore, ref);
        mOrderListAdapter = new OrderListAdapter(ref, this, ref, odrDataMapLinkedHM);
        mLstVwJob.setAdapter(mOrderListAdapter);

		/*new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				Toast.makeText(mActivity,"Data is Back",Toast.LENGTH_LONG).show();
				notifyDataChange(mActivity);

			}
		},90000);*/



		/*Timer t = new Timer();
		TimerTask tt = new TimerTask() {
			@Override
			public void run() {

				Toast.makeText(mActivity,"actttt",Toast.LENGTH_LONG).show();
				mOrderListAdapter.notifyDataSetChanged();
				System.out.println("Task Timer is on");

			};
		};
		t.scheduleAtFixedRate(tt, new Date(), 3000);*/
    }

//sendOfflineDataRequest("full");

		/*odrDataMap.clear();
		odrDataMap.putAll((HashMap<Long, Order>)DataObject.ordersXmlDataStore);
		mOrderListAdapter.notifyDataSetChanged(); */

    public void notifyDataChange(BaseTabActivity ref) {

        LinkedHashMap<Long, Order> odrDataMapLinkedHM = sortOrders((HashMap<Long, Order>) DataObject.ordersXmlDataStore, ref);
        mOrderListAdapter = new OrderListAdapter(ref, this, ref, odrDataMapLinkedHM);
        mLstVwJob.setAdapter(mOrderListAdapter);

    }

    public void notifyDataUpdate(BaseTabActivity ref) {

        if (updateOrderFrmPb) {
            updateOrderFrmPb = false;
            loadOrdersAtBoot();
        }
    }

    private void openCountryListFrag() {

        CountryListFragment countryLstFrag = new CountryListFragment();
        BaseTabActivity.pushFragToStackRequired = 1;//YD using because push is called twice once from here and another from the ontabChange
        mActivity.pushFragments(Utilities.JOBS, countryLstFrag, true, true, BaseTabActivity.UI_Thread);

    }

    private void showRadioDialogFontSize() {
        try {
            ArrayList<String> items = new ArrayList<String>();
            items.add("Extra Small");
            items.add("Small");
            items.add("Medium");
            items.add("Large");
            items.add("Extra Large");

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT);
            builder.setTitle("Font Size");
            LayoutInflater inflater = mActivity.getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);
            builder.setView(dialogView);

            final ListView listView = (ListView) dialogView.findViewById(R.id.list_dialog);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.select_dialog_item, items) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    TextView textView = (TextView) view.findViewById(android.R.id.text1);
                    textView.setTextColor(mActivity.getResources().getColor(R.color.light_gray));
                    int size = PreferenceHandler.getCurrrentFontSzForApp(mActivity);

                    if (position == 0) {
                        textView.setTextSize(16);
                        if (size == -10) { //YD checking which is currently selected option in the list_cal of choices
                            listView.setItemChecked(0, true);
                            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apptheme_btn_radio_on_holo_light, 0);
                        } else
                            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apptheme_btn_radio_off_holo_light, 0);

                    } else if (position == 1) {
                        textView.setTextSize(18);
                        if (size == -5) {
                            listView.setItemChecked(1, true);
                            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apptheme_btn_radio_on_holo_light, 0);
                        } else
                            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apptheme_btn_radio_off_holo_light, 0);
                    } else if (position == 2) {
                        textView.setTextSize(20);
                        if (size == 0) {
                            listView.setItemChecked(2, true);
                            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apptheme_btn_radio_on_holo_light, 0);
                        } else
                            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apptheme_btn_radio_off_holo_light, 0);
                    } else if (position == 3) {
                        textView.setTextSize(22);
                        if (size == 5) {
                            listView.setItemChecked(3, true);
                            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apptheme_btn_radio_on_holo_light, 0);
                        } else
                            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apptheme_btn_radio_off_holo_light, 0);
                    } else if (position == 4) {
                        textView.setTextSize(24);
                        if (size == 10) {
                            listView.setItemChecked(4, true);
                            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apptheme_btn_radio_on_holo_light, 0);
                        } else
                            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apptheme_btn_radio_off_holo_light, 0);
                    }

                    return view;
                }

            };


            listView.setAdapter(arrayAdapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
					/*CheckedTextView item = (CheckedTextView) view;
					if (item.isChecked()) {
						//YD code here when item is checked in the list_cal
						setFontSizeForApp(position);
					}*/
                    setFontSizeForApp(position);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                    //mActivity.popFragments(mActivity.UI_Thread);//YD when option data transfered
                }
            })/*.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					setFontSizeForApp(currentItemSelected);
					//mActivity.popFragments(mActivity.UI_Thread);

				}
			})*/;
            AlertDialog dialog = builder.create();
            dialog.show();

            Utilities.setDividerTitleColor(dialog, mheight, mActivity);
            Button neutral_button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            Utilities.setDefaultFont_12(neutral_button_negative);
			/*Button neutral_button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Utilities.setDefaultFont_12(neutral_button_positive);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFontSizeForApp(int position) {
        if (position == 0) {
            PreferenceHandler.setCurrrentFontSzForApp(mActivity, -10);
            //mActivity.popFragments(mActivity.UI_Thread);
        } else if (position == 1) {
            PreferenceHandler.setCurrrentFontSzForApp(mActivity, -5);
            //mActivity.popFragments(mActivity.UI_Thread);
        } else if (position == 2) {
            PreferenceHandler.setCurrrentFontSzForApp(mActivity, 0);
            //mActivity.popFragments(mActivity.UI_Thread);
        } else if (position == 3) {
            PreferenceHandler.setCurrrentFontSzForApp(mActivity, 5);
            //mActivity.popFragments(mActivity.UI_Thread);
        } else if (position == 4) {
            PreferenceHandler.setCurrrentFontSzForApp(mActivity, 10);
            //mActivity.popFragments(mActivity.UI_Thread);
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent i = mActivity.getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(mActivity.getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mActivity.startActivity(i);
            }
        });

    }

    private void showAboutDialog(String D_title, String D_desc, Activity act) {
        final Dialog dialog = new Dialog(act, R.style.commonDialogTheme);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle(D_title);
        dialog.setContentView(R.layout.about_dialog);
        dialog.setCanceledOnTouchOutside(false);

        TextView msgView;
        msgView = (TextView) dialog.findViewById(R.id.text_mesg_popup_abt);
        msgView.setText(D_desc);
        msgView.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(act)));
        TypeFaceFont.overrideFonts(mActivity, msgView);
        Button refreshbtn, okBtn;

        refreshbtn = (Button) dialog.findViewById(R.id.refreshbtn_abt);
        refreshbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                versionCheckState = "REFRESH";
                checkVersionForApp();
                dialog.dismiss();
            }
        });
        refreshbtn.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(act)));

        okBtn = (Button) dialog.findViewById(R.id.butt_ok_abt);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mActivity.popFragments(mActivity.UI_Thread); //YD when option data transfered
                dialog.dismiss();
            }
        });
        TypeFaceFont.overrideFonts(mActivity, refreshbtn);
        TypeFaceFont.overrideFonts(mActivity, okBtn);

        Utilities.setDividerTitleColor(dialog, 500, act);
        TextView tv = (TextView) dialog.findViewById(android.R.id.title);
        if (tv != null) {
            tv.setTextColor(act.getResources().getColor(R.color.dlg_light_green));
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextSize(24 + (PreferenceHandler.getCurrrentFontSzForApp(act)));
        }
        TypeFaceFont.overrideFonts(mActivity, tv);
        Utilities.setDefaultFont_12(okBtn);
        dialog.show();

    }

    private void checkVersionForApp() {
        Asyncc asyncc = new Asyncc();
        asyncc.execute();
    }

    class Asyncc extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {

            Map<String, String> getOrderParams = new HashMap<String, String>();
            getOrderParams.put("action", "getmversion");
            getOrderParams.put("tid", "apk");

            String response = null;
            try {
                response = HttpConnection.get(mActivity,
                        "https://" + PreferenceHandler.getPrefBaseUrl(mActivity) + "/mobi", getOrderParams);//<data><success>true</success><id>2.3</id></data>
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
                Utilities.log(mActivity, "server response : " + response);
                XMLHandler xmlhandler = new XMLHandler(mActivity);
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

                    PackageManager manager = mActivity.getPackageManager();
                    PackageInfo info = null;
                    try {
                        info = manager.getPackageInfo(mActivity.getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e1) {
                        e1.printStackTrace();
                    }

                    String appVersion = info.versionName;
                    double appVersionNum = Double.valueOf(appVersion);

                    //YD Main logic
                    if (appVersionNum >= serverVersionNum) {
                        if (versionCheckState.equals("SYNC")) {
                            if (!Utilities.checkInternetConnection(mActivity, false))
                                showNoInternetDialog("Slight problem with data", "No internet connection");
                            else {
                                if (shouldCloseApp)
                                    sendOfflineDataRequest("semipartial");
                                else
                                    sendOfflineDataRequest("partial");
                            }
                        } else if (versionCheckState.equals("REFRESH")) {
                            showRefreshConfirmationDialog();//YD for refresh
                        }
                    } else {
                        shouldFinish = true;
                        showDialogForVersion();
                    }
                }
            }
        }
    }

    private void showDialogForVersion() {//yd don
        try {
            String D_title = "Please upgrade";
            String D_desc = " A new version of AceRoute app is available. Please download from Google Play Store.";

            dialog = new MyDialog(mActivity, D_title, D_desc, "OK");
            dialog.setkeyListender(new MyDiologInterface() {
                @Override
                public void onPositiveClick() throws JSONException {
                    dialog.dismiss();
                }

                @Override
                public void onNegativeClick() {
                    if (shouldFinish == true) {
                        shouldFinish = false;

                        if (!Utilities.checkInternetConnection(mActivity, false))
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
                            if (!Utilities.checkInternetConnection(mActivity, false))
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
            dialog.show();

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
        dialog = new MyDialog(mActivity, mActivity.getResources().getString(R.string.lbl_refresh_title), mActivity.getResources().getString(R.string.lbl_logout_message), "YES");
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

                if (!Utilities.checkInternetConnection(mActivity, false))
                    showNoInternetDialog("Slight problem with data", "No internet connection");
                else
                    sendOfflineDataRequest("full");// YD using for case of Refresh.
                // mActivity.popFragments(mActivity.UI_Thread); //YD when option data transfered
                dialog.dismiss();
            }
        });
        dialog.onCreate(null);
        dialog.show();
    }

    public void loadDataOnBack(final BaseTabActivity mActivity) {
        mActivity.registerHeader(this);
        this.mActivity = mActivity;

        //setAdapter();
        //	Toast.makeText(mActivity,"counter increment",Toast.LENGTH_LONG).show();

        //mOrderListAdapter.notifyDataSetChanged();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //
                // notifyDataChange(mActivity);
            }

        }, 8000);


        System.gc();
        mActivity.mImgSyncBtn.setVisibility(View.GONE);
        Log.e("BackData", "onResume called for OrderListMainFragment");
        // TODO Auto-generated method stub
        //super.onResume();


        if (SplashII.shouldShowProgress) {
            BaseTabActivity.mProgress.setVisibility(View.VISIBLE);
        }

        if (optionRefreshState == 1)//YD using this to using if coming back from Option frag when refresh is clicked
        {
            optionRefreshState = 0;
            if (!Utilities.checkInternetConnection(mActivity, false))
                showNoInternetDialog("Slight problem with data", "No internet connection");
            else
                sendOfflineDataRequest("full");
        } else if (optionRefreshState == 2)//YD using this to using if coming back from Option frag when offline is clicked
        {
            openCountryListFrag();
            optionRefreshState = 0;
        } else if (optionRefreshState == 3) {
            optionRefreshState = 0;
            CreateOrderFragment createOdrFrag = new CreateOrderFragment();
            mActivity.pushFragments(Utilities.JOBS, createOdrFrag, true, true, BaseTabActivity.UI_Thread);
        } else if (optionRefreshState == 4) {
            optionRefreshState = 0;
            sendOfflineDataRequest("partial");
        } else if (optionRefreshState == 5) { //YD using for sync data option
            optionRefreshState = 0;
            if (!Utilities.checkInternetConnection(mActivity, false))
                showNoInternetDialog("Slight problem with data", "No internet connection");
            else {
                if (shouldCloseApp)
                    sendOfflineDataRequest("semipartial");
                else
                    sendOfflineDataRequest("partial");
            }
        } else if (optionRefreshState == 100) { //YD Refreshing the adaptor when creating new order and also using at pb task and part and coming back from footer map
            optionRefreshState = 0;
            setAdapter();

        } else if (updateOrderFrmPb) { //YD using for pb task and part
            updateOrderFrmPb = false;
            loadOrdersAtBoot();
        }

        if (!Utilities.isTodayDate(mActivity, new Date(Long.valueOf(PreferenceHandler.getlastsynctime(mActivity))))) {
            if (!Utilities.isTodayDate(mActivity, new Date(PreferenceHandler.getSyncPopupCancelTm(mActivity)))) {
                syncDialog = new MyDialog(mActivity, "Sync Data", "Do you want to sync", "DELETE");
                syncDialog.setkeyListender(new MyDiologInterface() {

                    @Override
                    public void onPositiveClick() throws JSONException {
                        if (PreferenceHandler.getOdrGetDate(mActivity) == null || PreferenceHandler.getOdrGetDate(mActivity).equals("")) {//no
                            Date d = new Date(PreferenceHandler.getlastsynctime(mActivity));
                            String mCurrentDate = Utilities.chkAndGetCustDt(mActivity, d);
                            PreferenceHandler.setOdrGetDate(mActivity, mCurrentDate);
                            PreferenceHandler.setodrLastSyncForDt(mActivity, Utilities.getCustDtDefaultTm());
                        }
                        PreferenceHandler.setSyncPopupCancelTm(mActivity, Utilities.getCurrentTime());
                        syncDialog.dismiss();
                    }

                    @Override
                    public void onNegativeClick() {
                        PreferenceHandler.setOdrGetDate(mActivity, null);
                        sendOfflineDataRequest("partial");
                        syncDialog.dismiss();
                    }
                });
                syncDialog.onCreate(null);
                syncDialog.show();
            }
        }

        if (PreferenceHandler.getAppDataChanged(mActivity)) {
            PreferenceHandler.setAppDataChanged(mActivity, false);
            notifyDataChange(mActivity);//413f6390
        }
        //}
        //setAdapter();
    }

    double CUSTBIAS_GBL = 1.00; // global variable

    Double getDistancebetween(Double lat, Double lon, Double lat2, Double lon2) {

        GeodeticCalculator geoCalc = new GeodeticCalculator();
        Ellipsoid reference = Ellipsoid.WGS84;
        GlobalPosition pointA = new GlobalPosition(lat, lon, 0.0);
        GlobalPosition pointB = new GlobalPosition(lat2, lon2, 0.0);

        // Distance between Point A and Point B in meters
        double distance = geoCalc.calculateGeodeticCurve(reference, pointB, pointA).getEllipsoidalDistance();

        distance = distance * 0.000621371; // to miles

        // Adjust straight line distance based on curved pathways, traffic and freeway/street speeds since we are not using actual real-time distance api
        // This assumes
        // shorter distances will have more curved pathways and less speed  (move slower)
        // longer distances will be relatively straight and use freeways for higher speed (mover faster)
        if (distance < 1) {
            distance = distance * 4.01;
        } else if (distance < 2) {
            distance = distance * 3.75;
        } else if (distance < 3) {
            distance = distance * 3.51;
        } else if (distance < 4) {
            distance = distance * 3.25;
        } else if (distance < 5) {
            distance = distance * 3.0;
        } else if (distance < 6) {
            distance = distance * 2.75;
        } else if (distance < 7) {
            distance = distance * 2.5;
        } else if (distance < 8.1) {
            distance = distance * 2.25;
        } else if (distance < 9.1) {
            distance = distance * 2.03;
        } else {
            distance = distance * 1.59;
        }

        distance = distance * CUSTBIAS_GBL;
        return roundDouble(distance, 2);
    }

    private double roundDouble(double distance, int i) {
        DecimalFormat format = new DecimalFormat("#");
        format.setMinimumFractionDigits(i);
        return Double.parseDouble(format.format(distance));
    }

    public void calculateDistAndTimeBtwOdrs(String workerOffGeo, String custSiteGeocode/*,final List<Order> odrData*/) {
        if (Utilities.checkInternetConnection(mActivity, false)) {
            try {
                Asynccc asyncc = new Asynccc(workerOffGeo, custSiteGeocode, sid, prev_id);
                asyncc.execute();
            } catch (Exception e) {

            }
        } else {
            try {

                String geo1[] = workerOffGeo.split(",");
                String geo2[] = custSiteGeocode.split(",");

                if (!geo1[0].equals("") && !geo1[1].equals("") && !geo1[0].isEmpty() && !geo1[1].isEmpty()
                        && !geo2[0].equals("") && !geo2[1].equals("") && !geo2[0].isEmpty() && !geo2[1].isEmpty()
                ) {
                    final double distance = getDistancebetween(Double.valueOf(geo1[0]), Double.valueOf(geo1[1]), Double.valueOf(geo2[0]), Double.valueOf(geo2[1]));

                    if (!skipalgorunning) {

                        if (sid.size() > 0) {

                            final long oid = sid.get(sid.size() - 1);
                            final long prid = prev_id.get(prev_id.size() - 1);
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mOrderListAdapter.setDistanceText(oid, prid, distance);
                                }
                            });

                        }
                        //showDistance(odrData);//YD check why calling here
                    } else {//YD using for eviction logic
                        distance_A2B = distance * 0.000621371;
                        time_A2B = Math.round(distance / speed * 60 * 60 * 1000);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    class Asynccc extends AsyncTask<String, Void, String> {

        String geo1;
        String geo2;
        ArrayList<Long> sid_local, prev_id_local;


        public Asynccc(String geo1, String geo2, ArrayList<Long> sid, ArrayList<Long> prev_id) {
            this.geo1 = geo1;
            this.geo2 = geo2;
            this.sid_local = new ArrayList<>(sid);
            this.prev_id_local = new ArrayList<>(prev_id);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            Map<String, String> getOrderParams = new HashMap<String, String>();
            getOrderParams.put("action", "gettraveltime");
            getOrderParams.put("geo1", geo1);
            getOrderParams.put("geo2", geo2);

            String response = null;
            try {
                response = HttpConnection.get(mActivity,
                        "https://" + PreferenceHandler.getPrefBaseUrl(mActivity) + "/mobi", getOrderParams);//<data><success>true</success><id>2.3</id></data>
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
                Utilities.log(mActivity, "server response : " + response);
                XMLHandler xmlhandler = new XMLHandler(mActivity);
                Document doc = xmlhandler.getDomElement(response);

                NodeList nl = doc
                        .getElementsByTagName(XMLHandler.KEY_DATA);
                Element e = (Element) nl.item(0);
                String success = xmlhandler.getValue(e,
                        XMLHandler.KEY_DATA_SUCCESS);

                if (success.equals(XMLHandler.KEY_DATA_RESP_FAIL) || response.contains("MobiLoginAgain")) {

                } else {
                    String distanceStr = doc.getChildNodes().item(0).getTextContent();

                    if (distanceStr != null) {
                        final double distance = Double.valueOf(distanceStr);

                        if (!skipalgorunning) {
                            try {


                                final long oid = sid_local.get(sid_local.size() - 1);
                                final long prid = prev_id_local.get(prev_id_local.size() - 1);
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mOrderListAdapter.setDistanceText(oid, prid, distance);
                                    }
                                });
                            } catch (Exception ee) {

                            }
                            //showDistance(odrData);
                        } else {//YD using for eviction logic
                            distance_A2B = distance * 0.000621371;
                            time_A2B = Math.round(distance / speed * 60 * 60 * 1000);
                        }
                    }
                }
            } else {

            }
        }
    }

}