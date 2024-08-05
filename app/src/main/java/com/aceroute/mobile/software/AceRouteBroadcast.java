package com.aceroute.mobile.software;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.aceroute.mobile.software.http.Api;
import com.aceroute.mobile.software.requests.GeoSyncAlarmRequest;
import com.aceroute.mobile.software.requests.SyncRO;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.Utilities;


import java.util.Calendar;
import java.util.Date;

public class AceRouteBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Utilities.TAG, "onReceive of AceRouteHeartBeatBroadcast.");
        String action = intent.getAction();
        //Logger.i("AceRoute Geo" , "BroadCastReceiver called for action : "+action);

        Calendar calendar = Utilities.getCalendarInstance(context);
        // calendar.setTimeInMillis(System.currentTimeMillis());
        long currenttime = calendar.getTimeInMillis();

        if (action != null) {
            if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                Log.i(Utilities.TAG, "Aceroute connectivity change");
                NetworkInfo info = intent
                        .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI
                            || info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        Log.i(Utilities.TAG,
                                "connectivity change TYPE_MOBILE");
                        Bundle bundle = new Bundle();
                        Intent bootIntent = new Intent(context,
                                AceRouteService.class);
                        bundle.putInt(AceRouteService.KEY_ID,
                                Utilities.DEFAULT_START_BG_SYNC);
                        bundle.putString(AceRouteService.KEY_MESSAGE, "{\"action\":\"" + Api.API_ACTION_SYNCBGDATA + "\"}");
                        bundle.putLong(AceRouteService.KEY_TIME, new Date().getTime());
                        bundle.putInt(AceRouteService.KEY_SYNCALL_FLAG, 1);
                        bootIntent.putExtras(bundle);
                        Utilities.log(context.getApplicationContext(), "Starting BG Sync.");
                        //context.startService(bootIntent);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(bootIntent);
                        } else {
                            context.startService(bootIntent);
                        }
                    }
                }
            } else if (action.equals("android.intent.action.BOOT_COMPLETED")) {  //YD TODO
                Log.i(Utilities.TAG, "Aceroute BootReciever completed.");
                //Toast.makeText(context, "onReceive of AceRoutePubnub BroadCast in broadcast class", Toast.LENGTH_LONG).show();

                if (PreferenceHandler.getMtoken(context) != null
                        && PreferenceHandler.getCompanyId(context) != null
                        && PreferenceHandler.getResId(context) > 0) {
                    Log.i(Utilities.TAG, "Starting service boot time.");
                    Intent bootIntent = new Intent(context,
                            AceRouteService.class);
                    bootIntent.setAction(AceRouteService.ACTION_CONN_PUBNUB_PB_ONBOOT);
                    //context.startService(bootIntent);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(bootIntent);
                    } else {
                        context.startService(bootIntent);
                    }
                } else {
                    Log.e(Utilities.TAG, "Not starting service boot time.");
                }
            } else if (action.equals(Utilities.ACE_HEART_BEAT_ACTION)) {

            } else if (action.equals(Utilities.ACE_LISTEN_DATE_CHANGE)) {

                BaseTabActivity.nextDaySyncFlag = true;  //YD flag is getting true when the date is changed

            } else if (action.equals(Utilities.ACE_GEO_SYNC_ACTION)) {
                try {
                    Log.i(Utilities.TAG,
                            "*********************In broadcast geo syc action called.");
                    boolean alarmState = PreferenceHandler
                            .getAlarmState(context);
                    if (alarmState == false) {
                        Log.i(Utilities.TAG,
                                "Geo Alarm state is false so returning");
                        return;
                    }
                    Log.i(Utilities.TAG, "Aceroute geo sync");
                    Long time = PreferenceHandler.getAcerouteGeoTime(context);
                    Log.i(Utilities.TAG, "last geobeat time : " + time);

                    boolean tidStatus = PreferenceHandler.getTidStatus(context);
                    if (PreferenceHandler.getMtoken(context) != null
                            && PreferenceHandler.getMtoken(context).length() != 0
                            && PreferenceHandler.getCompanyId(context) != null
                            && PreferenceHandler.getCompanyId(context).length() != 0
                            && tidStatus == true) {
                        Log.i(Utilities.TAG,
                                "setting current location");
                        String location = Utilities
                                .getLocation(context);
                        Log.i(Utilities.TAG,
                                "current location : " + location);
                        if (location != null
                                && location.length() > 1) {
                            String loc[] = location.split(",");

                            PreferenceHandler.setPrefLat(context,
                                    loc[0]);

                            PreferenceHandler.setPrefLong(context,
                                    loc[1]);

                            Log.i(Utilities.TAG,
                                    "Submitting requestobject for sending resource geocode");
                            Bundle bundle = new Bundle();
                            int id = Utilities.DEFAULT_NOCALLBACK_REQUEST_ID;
                            bundle.putInt(AceRouteService.KEY_ID,
                                    id);
                            int syncFlag = AceRouteService.VALUE_NOT_SYNCALL;
                            bundle.putInt(
                                    AceRouteService.KEY_SYNCALL_FLAG,
                                    syncFlag);

                            /*******************YD*s*********************/
                            bundle.putInt(AceRouteService.FLAG_FOR_CAMERA, 0);
                            bundle.putString(AceRouteService.KEY_ACTION, Api.API_ACTION_SAVE_RES_GEO);

                            String templocation = location.replace("#", ",");
                            GeoSyncAlarmRequest geoSyncAlarmreq = new GeoSyncAlarmRequest();
                            geoSyncAlarmreq.setAction(Api.API_ACTION_SAVE_RES_GEO);
                            geoSyncAlarmreq.setData(templocation);

                            //Logger.i("AceRoute Geo" , "save current Geo as : "+ templocation);
                            bundle.putParcelable("OBJECT", geoSyncAlarmreq);
                            /*******************YD*e*********************/

                            Intent mIntent = new Intent(
                                    context.getApplicationContext(),
                                    com.aceroute.mobile.software.AceRouteService.class);
                            mIntent.putExtras(bundle);
                            Utilities
                                    .log(context
                                                    .getApplicationContext(),
                                            "Starting acerouteservice from broadcast. "
                                                    + mIntent
                                                    .getExtras()
                                                    .getString(
                                                            AceRouteService.KEY_MESSAGE));
                            //context.startService(mIntent);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(mIntent);
                            } else {
                                context.startService(mIntent);
                            }
                        }
                    }

                    if (tidStatus) {
                        try {
                            Log.i(Utilities.TAG, "Starting geo alarm for "
                                    + currenttime);
                            setGeoTimeAndAlarm(context, currenttime);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i(Utilities.TAG,
                        "*********************In broadcast geo syc end.");
            } else if (action.equals(Utilities.ACE_GEO_SYNC_ACTION_TEST)) {
                String newGeo = intent.getStringExtra("geoExtra");

                boolean ShouldSyncToServer = intent.getBooleanExtra("shouldSyncToServer", false);

                boolean tidStatus = PreferenceHandler.getTidStatus(context);
                if (PreferenceHandler.getMtoken(context) != null
                        && PreferenceHandler.getMtoken(context).length() != 0
                        && PreferenceHandler.getCompanyId(context) != null
                        && PreferenceHandler.getCompanyId(context).length() != 0
                        && tidStatus == true) {


                    Bundle bundle = new Bundle();
                    int id = Utilities.DEFAULT_NOCALLBACK_REQUEST_ID;
                    bundle.putInt(AceRouteService.KEY_ID, id);
                    int syncFlag = AceRouteService.VALUE_NOT_SYNCALL;
                    bundle.putInt(AceRouteService.KEY_SYNCALL_FLAG, syncFlag);

                    /*******************YD*s*********************/
                    bundle.putInt(AceRouteService.FLAG_FOR_CAMERA, 0);
                    bundle.putString(AceRouteService.KEY_ACTION, Api.API_ACTION_SAVE_RES_GEO);

                    String templocation = newGeo;
                    GeoSyncAlarmRequest geoSyncAlarmreq = new GeoSyncAlarmRequest();
                    geoSyncAlarmreq.setAction(Api.API_ACTION_SAVE_RES_GEO);
                    geoSyncAlarmreq.setData(templocation);
                    if (ShouldSyncToServer)
                        geoSyncAlarmreq.setSyncToServer("true");
                    else
                        geoSyncAlarmreq.setSyncToServer("false");

                    //Logger.i("AceRoute Geo" , "save current Geo as : "+ templocation);
                    bundle.putParcelable("OBJECT", geoSyncAlarmreq);
                    /*******************YD*e*********************/

                    Intent mIntent = new Intent(context.getApplicationContext(),
                            com.aceroute.mobile.software.AceRouteService.class);
                    mIntent.putExtras(bundle);
                    Utilities.log(context.getApplicationContext(),

                            "Starting acerouteservice from broadcast. "
                                    + mIntent
                                    .getExtras()
                                    .getString(
                                            AceRouteService.KEY_MESSAGE));
                    //context.startService(mIntent);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(mIntent);
                    } else {
                        context.startService(mIntent);
                    }
                }

                /*NotificationManager manager = createNotificationChannel(context);
                Intent notificationIntent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context,
                        1, notificationIntent, 0);

                Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle("Foreground Service")
                        .setContentText(x++ +" <<>> "+ newGeo)
                        .setSmallIcon(R.drawable.ic_chooser)
                        .setContentIntent(pendingIntent)
                        .build();


                notification.defaults |= Notification.DEFAULT_VIBRATE;
                notification.defaults |= Notification.DEFAULT_SOUND;
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                manager.notify(0, notification);*/
            } else if (action.equals(Utilities.ACE_AUTO_SYNC)) {

                int id = Utilities.DEFAULT_NOCALLBACK_REQUEST_ID;
                Long currentMilli = PreferenceHandler.getPrefQueueRequestId(context);

                SyncRO syncObj = new SyncRO();
                syncObj.setType("partial");

                Bundle mBundle = new Bundle();
                mBundle.putParcelable("OBJECT", syncObj);
                mBundle.putLong(AceRouteService.KEY_TIME, currentMilli);
                mBundle.putInt(AceRouteService.KEY_SYNCALL_FLAG, AceRouteService.VALUE_NOT_SYNCALL);
                mBundle.putInt(AceRouteService.FLAG_FOR_CAMERA, 0);
                mBundle.putInt(AceRouteService.KEY_ID, id);
                mBundle.putString(AceRouteService.KEY_ACTION, "syncalldataforoffline");

                Intent mIntent = new Intent(
                        context.getApplicationContext(),
                        com.aceroute.mobile.software.AceRouteService.class);
                mIntent.putExtras(mBundle);
              //  context.startService(mIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(mIntent);
                } else {
                    context.startService(mIntent);
                }
            }
        }
    }

    public static final String CHANNEL_ID = "ForegroundServiceChannel1";
    public static int x = 0;

    private NotificationManager createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
            return manager;
        }
        return null;
    }

    public static void setGeoTimeAndAlarm(Context context, long currenttime) {
        Log.i(Utilities.TAG, "For now not setting geobeat : " + currenttime);
        PreferenceHandler.setAcerouteGeoTime(context, currenttime);
        Log.i(Utilities.TAG, "Setting geo alarm again.");
        //Utilities.startGeoSyncAlarm(context);
    }

}
