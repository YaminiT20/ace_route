package com.aceroute.mobile.software;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.aceroute.mobile.software.component.Bulkpubnub;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.database.DBEngine;
import com.aceroute.mobile.software.database.DBHandler;
import com.aceroute.mobile.software.http.Api;
import com.aceroute.mobile.software.http.RequestObject;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.network.AceRequestHandler;
import com.aceroute.mobile.software.requests.PubnubRequest;
import com.aceroute.mobile.software.utilities.JSONHandler;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.Utilities;
import com.aceroute.mobile.software.utilities.XMLHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zzi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.TransformerFactoryConfigurationError;

public  class AceRouteService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleApiClient {

    Handler mHandler = new Handler();
    private Thread requestHandler;

    public final static String ACTION_RESET_PUBNUB = "com.software.mobile.acerouteService.RESET_PUBNUB";
    public final static String ACTION_CONN_PUBNUB = "com.software.mobile.acerouteService.CONN_PBNUB";
    public static final String ACTION_CONN_PUBNUB_PB_ONBOOT = "com.software.mobile.acerouteService.CONN_PBNUB_PB_ONBOOT";
    public final static String ACTION_SYNC_DATA_ON_INTERNET_RECEIVED = "com.software.mobile.acerouteService.SYNC_DATA_ON_INTERNET_RECEIVED";
    public final static String KEY_ID = "id";
    public final static String KEY_ACTION = "action";
    public final static String KEY_MESSAGE = "message";
    public final static String KEY_TIME = "time";
    public final static String KEY_SYNCALL_FLAG = "syncFlag";
    public final static String FLAG_FOR_CAMERA = "FlagForCame";

    public final static int VALUE_SYNCALL = 0;
    public final static int VALUE_NOT_SYNCALL = 1;
    private Location location = null;
    private GoogleApiClient mLocationClient = null;
    private LocationRequest mLocationRequest = null;

    LocationManager locationManager = null;
    BroadcastReceiver pbBroadcast;
    PNConfiguration pnConfiguration = new PNConfiguration();
    PubNub pubnub;
    public Location prev_location = null;
    private int pubnubBroadcaseFirstTime = 0;

    public RequestObject getRequestObject(int id, Object msgObj,
                                          long time, String action) {
        RequestObject object = new RequestObject();
        object.setReqId(id);
        object.setReqDataObject(msgObj);
        //object.setJsinterface(jsInterface);
        object.setTimeInMilliSeconds(time);
        object.setAction(action);
        //object.setCurrentReqObj(currentReqObj);
        return object;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Utilities.log(getApplicationContext(), "onBind of service");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(myIntent);
        }*/
        //mLocationClient = new LocationClient(this, this, this);
        mLocationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new ConnectionCallbacks() {

                    @Override
                    public void onConnectionSuspended(int arg0) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onConnected(Bundle arg0) {
                        // TODO Auto-generated method stub

                    }
                })
                .addOnConnectionFailedListener(new OnConnectionFailedListener() {

                    @Override
                    public void onConnectionFailed(ConnectionResult arg0) {
                        // TODO Auto-generated method stub

                    }
                })
                .build();


        if (servicesConnected()) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(Utilities.TIME_MINUTE
                    * Utilities.ACE_HEART_BEAT);
            mLocationRequest.setFastestInterval(Utilities.TIME_MINUTE
                    * Utilities.ACE_HEART_BEAT);
        }
        Log.i(Utilities.TAG, "AceRouteService started.");
        /*
         * making database here because if user clear user data and app is in
         * background then automatically database will be created.
         */
        Log.i(Utilities.TAG, "database creating or checking...");
        // DBHandler.getDataBase(getApplicationgetApplicationContext()());

        // Handlin old data, starting async for old data.

        Utilities.log(getApplicationContext(), "onCreate1 of service");
        if (requestHandler == null) {
            startRequestHandler(requestHandler);
        } else {
            if (!requestHandler.isAlive()) {
                Log.e(Utilities.TAG, "in service thread is not alive");
                // requestHandler.start();
                startRequestHandler(requestHandler);
            } else {
                Log.i(Utilities.TAG, "in service thread is running.");
            }
        }

        subscribeOnPubnub();
        if (PreferenceHandler.getIsNewLogin(getApplicationContext())) {
            PreferenceHandler.setIsNewLogin(getApplicationContext(), false);
            pubnubBroadcaseFirstTime = 1;// YD making this zero because it is causing pubnub playback to stop when coming from background
        }
        subscribeOnPubnubPlayBack();
        //initializeLocationManager();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        Log.e(Utilities.TAG, "TASK REMOVED and restarting the service again");
        Bundle mBundle = new Bundle();
        mBundle.putInt(AceRouteService.KEY_SYNCALL_FLAG, AceRouteService.VALUE_NOT_SYNCALL);
        mBundle.putInt(AceRouteService.FLAG_FOR_CAMERA, 0);
        Intent intent = new Intent(getApplicationContext(), AceRouteService.class);
        intent.putExtras(mBundle);

        PendingIntent service = PendingIntent.getService(
                getApplicationContext(),
                1001,
                intent,
                PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);

        //startServiceAgain(getApplicationContext());
    }

    private Thread.UncaughtExceptionHandler defaultUEH;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            Log.d(Utilities.TAG, "Uncaught exception Called! so restarting the service again");
            ex.printStackTrace();

            Bundle mBundle = new Bundle();
            mBundle.putInt(AceRouteService.KEY_SYNCALL_FLAG, AceRouteService.VALUE_NOT_SYNCALL);
            mBundle.putInt(AceRouteService.FLAG_FOR_CAMERA, 0);
            Intent intent = new Intent(getApplicationContext(), AceRouteService.class);
            intent.putExtras(mBundle);

            //Same as done in onTaskRemoved()
            PendingIntent service = PendingIntent.getService(
                    getApplicationContext(),
                    1001,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
            System.exit(2);

            //startServiceAgain(getApplicationContext());
        }
    };

    private void initializeLocationManager() {  //YD
        Utilities.log(getApplicationContext(), "subcribing LocationManager");
        try {
            boolean isGPSEnabled = false;
            boolean isNetworkEnabled = false;
            Location location =  null;

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // No network provider is enabled
                } else {
                    if (isNetworkEnabled){
                        if (PreferenceHandler.getPrefGpsSync(getApplicationContext()) > 0 && PreferenceHandler.getPrefGpsSync(getApplicationContext()) <= 3) {
                            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    PreferenceHandler.getPrefGpsSync(getApplicationContext()) / 2, 20, new Utilities());//152.4 Meters = 500 feet
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        }
                        //YD if gpssync >3
                        else if (PreferenceHandler.getPrefGpsSync(getApplicationContext()) != 0 && PreferenceHandler.getPrefGpsSync(getApplicationContext()) > 3) {
                            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    PreferenceHandler.getPrefGpsSync(getApplicationContext()) / 2, 5 /*100000*/, new Utilities());
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                        //YD if gpssync ==0
                        else {
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    1000 * 60 * 5, /*20*/5, new Utilities());
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                    }

                    //YD 2020 if network is not availabe then get geo from gps
                    if (isGPSEnabled) {
                        if(location != null) {
                            //YD if gpssync <3
                            if (PreferenceHandler.getPrefGpsSync(getApplicationContext()) > 0 && PreferenceHandler.getPrefGpsSync(getApplicationContext()) <= 3) {
                                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                locationManager.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        PreferenceHandler.getPrefGpsSync(getApplicationContext()) / 2, 20, new Utilities());//152.4 Meters = 500 feet
                            }
                            //YD if gpssync >3
                            else if (PreferenceHandler.getPrefGpsSync(getApplicationContext()) != 0 && PreferenceHandler.getPrefGpsSync(getApplicationContext()) > 3) {
                                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                locationManager.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        PreferenceHandler.getPrefGpsSync(getApplicationContext()) / 2, 5 /*100000*/, new Utilities());
                            }
                            //YD if gpssync ==0
                            else {
                                locationManager.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        1000 * 60 * 5, /*20*/5, new Utilities());
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void subscribeOnPubnubPlayBack() {
        pbBroadcast = new PubnubBroadcast(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        //filter.addAction("android.intent.action.BOOT_COMPLETED");
        registerReceiver(pbBroadcast, filter);

    }


    //Thread t1;

    long startTime;
    long endTime;
    static int noOfTimeToLoopPB = 1;
    static long interval = 0;
    boolean loopingcompleted = false;
    boolean isFirstTime = false;

    public synchronized void runPubnubPlayBack() {
        Log.i( Utilities.TAG_PLAYBK, "runPubnubPlayBack called. ");
        final long res_id = PreferenceHandler.getResId(getApplicationContext());
        String company_id = PreferenceHandler.getCompanyId(getApplicationContext());

        final long st_time = PreferenceHandler.getPubnubPBStartTime(getApplicationContext());
        final long ed_time = PreferenceHandler.getPubnubPBendTime(getApplicationContext());
        final long syncinterval = PreferenceHandler.getIntervalTime(getApplicationContext());

        final long cst_time = PreferenceHandler.getPubnubPBCurrentStartTime(getApplicationContext());
        final long ced_time = PreferenceHandler.getcurrentEndTme(getApplicationContext());


        final Context context = getApplicationContext();
        Log.i(Utilities.TAG_PLAYBK, "Playing pubnub playback: StartTime:" + cst_time + ", EndTime:" + ced_time);

        SubscribeCallback callback = new SubscribeCallback() {

            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                    // internet got lost, do some magic and call reconnect when ready
                    pubnub.reconnect();
                } else if (status.getCategory() == PNStatusCategory.PNTimeoutCategory) {
                    // do some magic and call reconnect when ready
                    pubnub.reconnect();
                } else {
                    Log.e(Utilities.TAG, "pubnub status error");
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                // handle incoming presence data
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                BaseTabActivity.setTmeFrOLMPHeader(getApplicationContext());//YD setting time on header
                JSONObject[] pubnubLst = JSONHandler.getPubnubList(message.getMessage().toString());
                JSONObject[] sortedPubnubLst = sortPubnubList(pubnubLst);
                Log.i(Utilities.TAG_PLAYBK, "Received callback for playback with length: " + pubnubLst.length);

                if (pubnubLst.length >= 100) {
                    Log.i(Utilities.TAG_PLAYBK, "ReCalculating interval because pubnub length is: " + pubnubLst.length + "> 100");
                    //loopingcompleted = true; // using just to run the new thread for repetion

                    long intervalTme = syncinterval / 2;
                    Log.i(Utilities.TAG_PLAYBK, "Interval for which order should received: " + intervalTme / 1000 * 60 + " mins");
                    long ced_time1 = ced_time - intervalTme;
                    Log.i( Utilities.TAG_PLAYBK, "New End time when messages are above 100: " + ced_time1);

                    updatePBPref(context, st_time, ed_time, intervalTme, cst_time, ced_time1);
                    runPubnubPlayBack();
                    return;
                }
                PreferenceHandler.setLastPubnubUpdated(getApplicationContext(), ced_time);// settting for pubnub
                for (int i = 0; i < pubnubLst.length; i++) {
                    Log.i( Utilities.TAG_PLAYBK, "insideLoop to run notifyUser: " + i);
                    notifyUser(sortedPubnubLst[i].toString());
                }
                repeatPlayBack(getApplicationContext());
            }

            public void errorCallback(String channel, Object error) {
                System.out.println(error.toString());
            }
        };
        if (company_id != null && res_id > 0) {
        }

    }

    public void repeatPlayBack(final Context context) {
        Log.i( Utilities.TAG_PLAYBK, "repeatPlayBack called. ");

        long st_time = PreferenceHandler.getPubnubPBStartTime(getApplicationContext());
        long ed_time = PreferenceHandler.getPubnubPBendTime(getApplicationContext());
        long syncinterval = PreferenceHandler.getIntervalTime(getApplicationContext());
        long cst_time = PreferenceHandler.getPubnubPBCurrentStartTime(getApplicationContext());
        long ced_time = PreferenceHandler.getcurrentEndTme(getApplicationContext());

        if ((ced_time + 100) >= ed_time)
            return;

        ced_time = ced_time + syncinterval;
        cst_time = cst_time + syncinterval;
        updatePBPref(context, st_time, ed_time, syncinterval, cst_time, ced_time);
        runPubnubPlayBack();
    }

    private void syncAndPlaypubnubPlayBk(final Context context) {
        Log.i( Utilities.TAG_PLAYBK, "Aceroute syncAndPlaypubnubPlayBk  thread started.");

        startTime = PreferenceHandler.getLastPubnubUpdated(getApplicationContext()) - 60000; /*Long.valueOf("1456928999999");*/
        endTime = Utilities.getCurrentTime();

        updatePBPref(context, startTime, endTime, endTime - startTime, startTime, endTime);
        runPubnubPlayBack();
    }

    private JSONObject[] sortPubnubList(JSONObject[] pubnubLst) {
        JSONObject tempJsonObj = null;
        if (pubnubLst != null) {
            for (int i = 0; i < pubnubLst.length; i++) {
                for (int k = i + 1; k < pubnubLst.length; k++) {
                    try {
                        String s_element = JSONHandler.getJSON_S_ELE(pubnubLst[i]);
                        String nxt_s_element = JSONHandler.getJSON_S_ELE(pubnubLst[k]);
                        if (Long.valueOf(s_element) > Long.valueOf(nxt_s_element)) {
                            tempJsonObj = pubnubLst[i];
                            pubnubLst[i] = pubnubLst[k];
                            pubnubLst[k] = tempJsonObj;
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return pubnubLst;
    }

    // pubnub code
    public void subscribeOnPubnub() {
        //if (PreferenceHandler.getIsNewLogin(this)) {
            pnConfiguration.setSubscribeKey(PreferenceHandler.getPrefBaseSubkey(this));
            //pnConfiguration.setPublishKey("PublishKey");
            pnConfiguration.setSecure(true);


            Log.i(Utilities.TAG, "subcribing on pubnub");
            try {
                pubnub = new PubNub(pnConfiguration);
                long res_id = PreferenceHandler.getResId(getApplicationContext());
                String company_id = PreferenceHandler
                        .getCompanyId(getApplicationContext());

                if (res_id > 0 && company_id != null && !company_id.equals("")) {
                    Log.i(Utilities.TAG, "subcribing on pubnub with UUID");

                    pubnub.addListener(new SubscribeCallback() {
                        @Override
                        public void status(PubNub pubnub, PNStatus status) {
                            if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                                // internet got lost, do some magic and call reconnect when ready
                                pubnub.reconnect();
                            } else if (status.getCategory() == PNStatusCategory.PNTimeoutCategory) {
                                // do some magic and call reconnect when ready
                                pubnub.reconnect();
                            } else {
                                Log.e(Utilities.TAG, "pubnub status error");
                            }
                        }

                        @Override
                        public void message(PubNub pubnub, PNMessageResult message) {
                            // handle incoming messages

                            PreferenceHandler.setLastPubnubUpdated(getApplicationContext(), Utilities.getCurrentTime());

                            long lastPbUpdateTm = PreferenceHandler.getLastPubnubUpdated(getApplicationContext());
                            long lastSyncTime = PreferenceHandler.getlastsynctime(getApplicationContext());
                            if (Long.valueOf(lastSyncTime) < lastPbUpdateTm) {
                                PreferenceHandler.setlastsynctime(getApplicationContext(), lastPbUpdateTm);
                            }

                            BaseTabActivity.setTmeFrOLMPHeader(getApplicationContext());
                            notifyUser(message.getMessage().toString());
                        }

                        @Override
                        public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                            // handle incoming presence data
                        }
                    });


                    pubnub.subscribe()
                            .channels(Arrays.asList(String.valueOf(PreferenceHandler.getNspId(getApplicationContext())))) // subscribe to channels
                            .execute();
                } else {
                    Log.e(Utilities.TAG, "Not subcribing on pubnub because company_id and res_id is not available");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        //}
    }

    public void startRequestHandler(Thread requestHandler) {
        AceRequestHandler aceRequestHandler = new AceRequestHandler(
                getApplicationContext());
        requestHandler = new Thread(aceRequestHandler);
        requestHandler.start();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.i( Utilities.TAG, "database creating or checking...");
        DBHandler.getDataBase(getApplicationContext());

        Utilities.log(getApplicationContext(), "onStartCommand of service");
        if (intent != null) {

            Log.i( Utilities.TAG,
                    "intent action in service : " + intent.getAction());
            if (intent.getAction() != null
                    && intent.getAction().equals(ACTION_CONN_PUBNUB)) {
                subscribeOnPubnub();
            } else if (intent.getAction() != null
                    && intent.getAction().equals(ACTION_CONN_PUBNUB_PB_ONBOOT)) {
                Log.i( Utilities.TAG,
                        "intent action in service for pubnub playback onboot : " + intent.getAction());
                BaseTabActivity.currentApplicationState = BaseTabActivity.RESTARTED;
                syncAndPlaypubnubPlayBk(getApplicationContext());
                subscribeOnPubnub();
            } else if (intent.getAction() != null
                    && intent.getAction().equals(ACTION_RESET_PUBNUB)) {
                Log.w( Utilities.TAG,
                        "ACTION_RESET_PUBNUB DISCONNECT AND RESUBSCRIBE.");
                //	pubnub.disconnectAndResubscribe();
            } else if (intent.getAction() != null
                    && intent.getAction().equals(ACTION_SYNC_DATA_ON_INTERNET_RECEIVED)) {
                Log.w( Utilities.TAG,
                        "ACTION_SYNC_DATA_ON_INTERNET_RECEIVED.");
                //	pubnub.disconnectAndResubscribe();
            } else {

                //yash
                Bundle b = intent.getExtras();

                //end
                if (b != null) {
                    long time = b.getLong(KEY_TIME);
                    String message = null;
                    Object msgObj = null;
                    int id = b.getInt(KEY_ID);
                    int syncFlag = b.getInt(KEY_SYNCALL_FLAG);
                    int flag4came = b.getInt(FLAG_FOR_CAMERA);
                    String action = b.getString(KEY_ACTION);


                    if (flag4came == 0) {
                        if (syncFlag != AceRouteService.VALUE_NOT_SYNCALL) {
                            if (syncFlag == AceRouteService.VALUE_SYNCALL) {
                                Log.w( Utilities.TAG,
                                        "farzi Deleting all rows from database");
                                DBEngine.emptyDatabase(getApplicationContext());
                            }

                            Log.i( Utilities.TAG,
                                    "farzi Submitting objects in queue for downloading data");
                        } else {
                            try {
                                msgObj = b.getParcelable("OBJECT");
                                message = b.getString(KEY_MESSAGE);
                                Log.i( Utilities.TAG, "Service message : "
                                        + message);
                                RequestObject currentMessage = getRequestObject(id,
                                            msgObj, time, action);
                                    performRequest(getApplicationContext(),
                                            currentMessage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            message = b.getString(KEY_MESSAGE);
                            Log.i( Utilities.TAG, "Service message : "
                                    + message);
                            RequestObject currentMessage = getRequestObject(id,
                                    message, time, action);
                            Utilities.log(getApplicationContext(),
                                    "calling perform request for message : "
                                            + currentMessage.getData()
                                            .toString());
                            performRequest(getApplicationContext(),
                                    currentMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            Log.w( Utilities.TAG, "Service start command called with null intent");
        }
        return START_STICKY;
    }

    /**
     * This method is submitting request object in queue.
     *
     * @param context
     * @param message
     */
    public synchronized void performRequest(Context context,
                                            RequestObject message) {
        Utilities.log(getApplicationContext(), "inside perform request");
        try {
            if (message != null) {
                Utilities.log(getApplicationContext(),
                        "adding message in queue." + message.toString());
                AceRouteApplication.getInstance().messageQueue
                        .put(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyUser(Object message) {
        Log.i("AceRoute", "NotifyUser function called from pubnub.");
        try {
            //message = "{\"k\":\"54|2|234001\",\"u\":\"demo@software.com\",\"t\":\"7F00000101598CE8476837D743925850\",\"s\":\"1484128422590\",\"x\":\"75868002\"}";
            if (message instanceof JSONObject) {
                final JSONObject obj = (JSONObject) message;
                Toast.makeText(getApplicationContext(), obj.toString(),
                        Toast.LENGTH_LONG).show();
                Log.i("jsonobject Rcved msg:", String.valueOf(obj));
            } else if (message instanceof String) {
                try {

                    try {
                        Log.i("Message from pubnub : ", (String) message);
                    }

                    catch (Exception e) {
                        e.printStackTrace();

                    }

                    final String obj = (String) message;
                    Log.i("String Received msg : ", obj.toString());
                    JSONObject jobject = JSONHandler.getJsonObject(obj);
                    String k_element = JSONHandler.getJSON_K_ELE(jobject);
                    String U_element = JSONHandler.getJSON_U_ELE(jobject);
                    //String temp_U_element = U_element; //YD done for part(18)and task(17) update even if created with the same resId
                    long cur_res_id = PreferenceHandler
                            .getResId(getApplicationContext());

                    k_element = k_element.replace("|", ",");
                    String[] element = k_element.split(",");

					/*if (element[0].equals("18")|| element[0].equals("17")) // YD Done to add part and task updated number to orders object
						U_element="123456789";*/

                    if (!U_element.equals(String.valueOf(cur_res_id))) {
                        String x_element = JSONHandler.getJSON_X_ELE(jobject);

                        Log.i( Utilities.TAG, "k is : " + k_element
                                + " & u is : " + U_element + " & x is : "
                                + x_element);

						/*k_element = k_element.replace("|", ",");

						String[] element = k_element.split(",");*///YD done above
                        Log.i( Utilities.TAG, element.length + " elements : "
                                + element[0] + ":" + element[1] + ":" + element[2]);
                        String objecttype = element[0];
                        String action = element[1];
                        String idOfObject = element[2];

                        Integer int_objecttype = Integer.parseInt(objecttype);
                        Integer int_action = Integer.parseInt(action);
                        Long int_id = 0L;

                        if (idOfObject != null)
                            int_id = Long.parseLong(idOfObject);

                        XMLHandler xmlHandler = new XMLHandler(
                                getApplicationContext());
                        String refinedXml = x_element;
                        // checking delete because delete request does not contain
                        // any xml so we can not search resid in it.
                        PubnubRequest jobjPubnub = null;
                        // for handling bulk pubnub.....
                        if (int_objecttype == Bulkpubnub.Bulk_type) {

                            //	refinedXml= here i am imagin XML is like of otype
                            //now we have to delete db of otype
                            //hit the server to get the fresh data from server
                            if (refinedXml != null) {
                                jobjPubnub = JSONHandler.getJSONForPubNub(
                                        int_action, int_objecttype, int_id,
                                        refinedXml);
                            }
                        } else if (int_objecttype == Order.TYPE
                                && int_action != DBHandler.DB_ACTION_DELETE) { // temporarily checking object type as order  type.
                            refinedXml = xmlHandler.searchResId(x_element,//YD check if the orders received is for the current worker
                                    cur_res_id);
                            if (refinedXml != null) {
                                String current_date = AceRouteApplication.getInstance().getCurrentDisplayDate(getApplicationContext());
                                String orderDate = xmlHandler.getOrderDate(x_element);
                                Date date = new Date(orderDate);
                                boolean isInBoundaryDate = Utilities.isValidCurrentDate(getApplicationContext(), date);
                                if (!isInBoundaryDate) {
                                    Log.i( Utilities.TAG, "No need to save new order as the order is not of current date");
                                    HashMap<Long, Object> xml2 = DBEngine.getOrders(getApplicationContext(), Long.toString(int_id));
                                    if (xml2 != null && xml2.size() > 0) {
                                    }// YD if order is available in db then update else return and for add it always call else part because new order cannot be available before.
                                    else {
                                        return;
                                    }
                                }
                            } else {// if xml does not contain the logged user id, but may be the current user has been moved out from the order so in that case order needs to be deleted

                                HashMap<Long, Object> xml2 = DBEngine.getOrders(getApplicationContext(), Long.toString(int_id));
                                if (xml2 != null && xml2.size() > 0) {
                                } else {
                                    return;
                                }
                            }
                        } else if (int_objecttype == Order.TYPE
                                && int_action == DBHandler.DB_ACTION_DELETE) {//YD making seprately because delete pubnub doesnot come with xml (dataxml) in response of pubnub
                            HashMap<Long, Object> xml2 = DBEngine.getOrders(getApplicationContext(), Long.toString(int_id));
                            if (xml2 != null && xml2.size() > 0) {
                            } else {
                                return;
                            }
                        }
						/*else if(int_objecttype == Shifts.PUBNUB_TYPE){
							long time = new Date().getTime();
							DBEngine.getshifts(null, getApplicationContext(), time, DBEngine.DATA_SOURCE_SERVER);
						}*/

                        if (refinedXml == null && x_element != null) {
                            // if(int_action != DBHandler.DB_ACTION_INSERT){
                            Log.i( Utilities.TAG,
                                    "refined xml is null making extra pubnub message handle");
                            String unusedOrderID = xmlHandler
                                    .searchOrderId(x_element);
                            int_id = Long.parseLong(unusedOrderID);
                            int_objecttype = 2;
                            jobjPubnub = JSONHandler.getJSONForPubNub(
                                    int_objecttype,
                                    Api.INT_API_ACTION_HANDLE_EXTRA_PUBNUB, int_id,
                                    refinedXml, Api.INT_API_ACTION_HANDLE_EXTRA_PUBNUB);
                        } else {
                            if (!U_element.equals(String.valueOf(cur_res_id)) && jobjPubnub == null) {
                                Log.i(Utilities.TAG, "Handling pubnub of Current resource id.");
                                jobjPubnub = JSONHandler.getJSONForPubNub( //YD creating object for sending request to aceRequestHandler
                                        int_action, int_objecttype, int_id,
                                        refinedXml);
                            } else {
                                try {
                                    Log.i(Utilities.TAG, "Handling pubnub of display date data.");
                                    String current_date = AceRouteApplication.getInstance().getCurrentDisplayDate(getApplicationContext());
                                    String orderDate = xmlHandler
                                            .getOrderDate(x_element);
                                    if (orderDate != null && orderDate.contains(current_date)) {
                                        // true if orderdate matched with current displayed date
                                        jobjPubnub = JSONHandler.getJSONForPubNub(
                                                int_objecttype,
                                                int_action, int_id,
                                                refinedXml, Api.INT_API_ACTION_HANDLE_DISPLAYED_DATE_DATA);
                                    } else {
                                        Log.w( Utilities.TAG,
                                                "Not processing message because of "
                                                        + U_element + "(U_element) == "
                                                        + cur_res_id + "(cur_res_id)");
                                    }
                                } catch (TransformerFactoryConfigurationError e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        RequestObject robject = new RequestObject();
                        // setting 0 to identify pubnub request during callback.
                        Log.i( Utilities.TAG, "*******Processing pubnub request");
                        robject.setReqId(Utilities.DEFAULT_PUBNUB_REQUEST_ID);
                        //robject.setJsinterface(jsInterface);
                        //robject.setData(jobjPubnub.toJSONString());earlier for sending json to service
                        robject.setReqDataObject(jobjPubnub);//YD
                        robject.setAction(jobjPubnub.getAction());
                        robject.setExtraAction(jobjPubnub.getExtraAction());
                        robject.setObjecttype(jobjPubnub.getObjecttype());
                        robject.setIdPb(jobjPubnub.getIdPb());
                        // changes for bulk pubnub
                        if (int_objecttype == Order.TYPE
                                && int_action != DBHandler.DB_ACTION_DELETE) {
                            Log.w( Utilities.TAG, "objecttype is Order.TYPE.");
                            if (refinedXml != null) {
                                Log.w( Utilities.TAG,
                                        "refinedXml is not null and contains "
                                                + cur_res_id);
                                // submitting in queue
                                performRequest(getApplicationContext(), robject);
                            } else {
                                Log.w( Utilities.TAG,
                                        "Not processing message because of "
                                                + x_element + " does not contain "
                                                + cur_res_id);
                            }
                        } else {
                            performRequest(getApplicationContext(), robject);
                        }
                    }//YD below else id is being used for customer history as we are getting the same res id in the u element
                    else if (U_element.equals(String.valueOf(cur_res_id)) && (element[0].equals("55") || element[0].equals("30"))) {

                        PubnubRequest jobjPubnub = null;

                        String objecttype = element[0];
                        String action = element[1];
                        String idOfObject = element[2];

                        Integer int_objecttype = Integer.parseInt(objecttype);
                        Integer int_action = Integer.parseInt(action);
                        Long int_id = 0L;

                        String x_element = JSONHandler.getJSON_X_ELE(jobject);

                        Log.i(Utilities.TAG, "Handling pubnub of Current resource id.");
                        jobjPubnub = JSONHandler.getJSONForPubNub( //YD creating object for sending request to aceRequestHandler
                                int_action, int_objecttype, int_id,
                                x_element);

                        RequestObject robject = new RequestObject();
                        Log.i( Utilities.TAG,
                                "**********Processing pubnub request");
                        robject.setReqId(Utilities.DEFAULT_PUBNUB_REQUEST_ID);
                        robject.setReqDataObject(jobjPubnub);//YD
                        robject.setAction(jobjPubnub.getAction());
                        robject.setExtraAction(jobjPubnub.getExtraAction());
                        robject.setObjecttype(jobjPubnub.getObjecttype());
                        robject.setIdPb(jobjPubnub.getIdPb());

                        if (x_element != null) {
                            Log.w( Utilities.TAG,
                                    "refinedXml is not null and contains "
                                            + cur_res_id);
                            // submitting in queue
                            performRequest(getApplicationContext(), robject);
                        } else {
                            Log.w( Utilities.TAG,
                                    "Not processing message because of "
                                            + x_element + " does not contain "
                                            + cur_res_id);
                        }

                    } else {
                        Log.w( Utilities.TAG,
                                "Not processing message because of "
                                        + U_element + "(U_element) == "
                                        + cur_res_id + "(cur_res_id)");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (message instanceof JSONArray) {
                final JSONArray obj = (JSONArray) message;
                Toast.makeText(getApplicationContext(), obj.toString(),
                        Toast.LENGTH_LONG).show();
                Log.i("jsonaray Received msg: ", obj.toString());
                Toast.makeText(getApplicationContext(), obj.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceHandler.setTempDate(getApplicationContext(), null);
        Log.w( Utilities.TAG, "PubnubService destroyed...");
        //Logger.i( "AceRoute Geo", "AceRoute Service destroyed...");

        if (mLocationClient.isConnected()) {
            //LocationServices.FusedLocationApi.removeLocationUpdates(this,this);
        }
        mLocationClient.disconnect();

        if(pubnub!=null) {
            pubnub.unsubscribeAll();
            pubnub = null;
        }

        if (pbBroadcast != null)
            unregisterReceiver(pbBroadcast);
    }


    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1000;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    static void enqueueWork(Context context, Intent work) {
        //enqueueWork(context, AceRouteService.class, JOB_ID, work);
    }


    @Override
    public void onLocationChanged(Location location) {
        try {
            Log.i( Utilities.TAG, "### : location changed : "
                    + location.getLatitude() + "-" +
                    "" + location.getLongitude());
            //Toast.makeText(getApplicationContext(), "onLocationChanged in service"+String.valueOf(location.getLatitude())+"-"+String.valueOf(location.getLongitude()),Toast.LENGTH_LONG).show();
//			//Toast.makeText(getApplicationContext(), location.getLatitude() + "-" + location.getLongitude(), 0).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean servicesConnected() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            // resultCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            // if (errorDialog != null) {
            // errorDialog.show();
            // }
            return false;
        }
    }

	/*@Override
	public void onConnectionFailed(ConnectionResult arg0) {

	}*///YD

    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        mLocationClient.connect();
    }

    @Override
    public void onConnected(Bundle arg0) {//YD from 2 places locations change is bieng taken here and utilities
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(this, mLocationRequest, this);
            location = getCurrentLocation();
            if (location != null) {
                Log.i(Utilities.TAG, "Current location geocode :" + location.getLatitude() + "," + location.getLongitude());
                //PreferenceHandler.setTempLastLocation(getApplicationContext(), location.getLatitude()+","+location.getLongitude());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Location getCurrentLocation() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(this);
        if (location != null) {
            return location;
        } else {
            // checkforGPSAndPromtOpen();
            return null;
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public ConnectionResult blockingConnect() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConnectionResult blockingConnect(long arg0, TimeUnit arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PendingResult<Status> clearDefaultAccountAndReconnect() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void stopAutoManage(FragmentActivity fragmentActivity) {

    }


    @Override
    public void connect() {
        // TODO Auto-generated method stub

    }

    @Override
    public void disconnect() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dump(String arg0, FileDescriptor arg1, PrintWriter arg2,
                     String[] arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public ConnectionResult getConnectionResult(
            com.google.android.gms.common.api.Api<?> arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Context getContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Looper getLooper() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getSessionId() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean hasConnectedApi(com.google.android.gms.common.api.Api<?> arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isConnected() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isConnecting() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isConnectionCallbacksRegistered(ConnectionCallbacks arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isConnectionFailedListenerRegistered(
            OnConnectionFailedListener arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void reconnect() {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerConnectionCallbacks(ConnectionCallbacks arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerConnectionFailedListener(OnConnectionFailedListener arg0) {
        // TODO Auto-generated method stub

    }


    @Override
    public void unregisterConnectionCallbacks(ConnectionCallbacks arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterConnectionFailedListener(
            OnConnectionFailedListener arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public <A extends com.google.android.gms.common.api.Api.zza, R extends Result, T extends com.google.android.gms.common.api.zza.zza<R, A>> T zza(
            T arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <C extends com.google.android.gms.common.api.Api.zza> C zza(
            com.google.android.gms.common.api.Api.zzc<C> arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean zza(com.google.android.gms.common.api.Api<?> arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean zza(Scope arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <A extends com.google.android.gms.common.api.Api.zza, T extends com.google.android.gms.common.api.zza.zza<? extends Result, A>> T zzb(
            T arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <L> zzi<L> zzl(L arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    public class PubnubBroadcast extends BroadcastReceiver {
        Context context;

        public PubnubBroadcast(Context context) {
            this.context = context;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            Log.i( Utilities.TAG, "onReceive of AceRoutePubnub BroadCast .");
            String action = intent.getAction();
            final Context contextFinal = context;

            if (action != null && pubnubBroadcaseFirstTime == 0) {
                if (action.equals("android.net.conn.CONNECTIVITY_CHANGE") || action.equals("android.intent.action.BOOT_COMPLETED")) {
                    Log.i( Utilities.TAG, "Aceroute connectivity change Pubnub BroadCast");
                    NetworkInfo info = intent
                            .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                    if (info.isAvailable()) {
                        if (info.getType() == ConnectivityManager.TYPE_WIFI
                                || info.getType() == ConnectivityManager.TYPE_MOBILE) {

                            if (info.isConnected()) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            long res_id = PreferenceHandler.getResId(getApplicationContext());
                                            String company_id = PreferenceHandler.getCompanyId(getApplicationContext());

                                            if (company_id != null && res_id > 0) {
                                                AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(getApplicationContext()).syncAllDataForOffline(null, "semipartial");

											/*	Handler handle = new Handler(Looper.getMainLooper());
												handle.postDelayed(new Runnable() {
													@Override
													public void run() {*/

                                                startTime = PreferenceHandler.getLastPubnubUpdated(getApplicationContext()) - 60000; /*Long.valueOf("1456928999999");*/
                                                endTime = Utilities.getCurrentTime();

                                                updatePBPref(context, startTime, endTime, endTime - startTime, startTime, endTime);
                                                runPubnubPlayBack();
													/*}
												}, 2000);*/
                                                subscribeOnPubnub();
                                            }
                                            List<Long> cids = new ArrayList<>();
                                            if (PreferenceHandler.getOrderMedia_DownloadingStatus(context)) {
                                                HashMap<Long, Object> orderlist = DBEngine.getOrdersfromOfflineInMap(context);
                                                if (orderlist != null) {
                                                    Long keys[] = orderlist.keySet().toArray(new Long[orderlist.size()]);
                                                    for (int i = 0; i < orderlist.size(); i++) {
                                                        Order odrObj = (Order) orderlist.get(keys[i]);
                                                        cids.add(odrObj.getCustomerid());
                                                    }
                                                }

try {

    AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(getApplicationContext()).getAllOrderMedia(null, context, orderlist.values().toArray());
}
catch (Exception e)
{

}
                                                }
                                            if (!PreferenceHandler.getCustListDownloadComplete(getApplicationContext()) && !PreferenceHandler.getIsCustListDownloading(getApplicationContext())) {
                                                long time = new Date().getTime();
                               //                 Response responseObj = DBEngine.getCustList(null, getApplicationContext(), cids, DBEngine.DATA_SOURCE_SERVER);
                                                Response responseObj = DBEngine.getCustList(null, getApplicationContext(), cids, DBEngine.DATA_SOURCE_SERVER);
                                                if (responseObj != null && !responseObj.getStatus().equals("failure"))
                                                    PreferenceHandler.setCustListDownloadComplete(getApplicationContext(), true);
                                                else {
                                                    //YD show popup to resync again
                                                }
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        }
                    }
                }
            } else
                pubnubBroadcaseFirstTime = 0;
        }

    }

    public void updatePBPref(Context context, long startTme, long endTme, long intervalTme, long currentStartTme, long currentEndTme) {

        Log.i( "updatePBPref", "startTme" + startTme + "endTme" + endTme + "intervalTme" + intervalTme + "currentStartTme" + currentStartTme + "currentEndTme" + currentEndTme);
        PreferenceHandler.setPubnubPBCurrentStartTime(getApplicationContext(), currentStartTme);
        PreferenceHandler.setPubnubPBStartTime(getApplicationContext(), startTme);
        PreferenceHandler.setPubnubPBendTime(getApplicationContext(), endTme);
        PreferenceHandler.setIntervalTime(getApplicationContext(), intervalTme);
        PreferenceHandler.setcurrentEndTme(getApplicationContext(), currentEndTme);
    }

}