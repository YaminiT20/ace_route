
package com.aceroute.mobile.software.utilities;

import static com.aceroute.mobile.software.AceRouteApplication.CHANNEL_ID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.aceroute.mobile.software.AceRouteBroadcast;
import com.aceroute.mobile.software.AceRouteService;
import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.http.Api;
import com.aceroute.mobile.software.requests.GeoSyncAlarmRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.logging.Logger;

import java.util.Calendar;

public class UpdateLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private LocationRequest mLocationRequest1;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    NotificationManager manager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Logger.i("AceRoute Geo" , "Udpatelocation service being called. ");
        createNotificationChannel();
        //final GPSTracker gpsTracker = new GPSTracker(this);
        Intent notificationIntent = new Intent(this, BaseTabActivity.class);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            pendingIntent = PendingIntent.getActivity(this, 1, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 1, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("AceRoute")
                .setContentText("GPS Tracking Service Enabled")
                .setSmallIcon(R.drawable.appicon)
                .setSound(null)
                .setNotificationSilent()
                .setContentIntent(pendingIntent)
                .build();

        startForeground(3, notification);
        // Toast.makeText(this, "Latitude = " + gpsTracker.getLatitude() + "Longitude = " + gpsTracker.getLatitude(), Toast.LENGTH_SHORT).show();
        //Log.i(Utilities.TAG,"Ashish get location : " + gpsTracker.getLocation());
        //updateLocation(this, gpsTracker.getLatitude(), gpsTracker.getLongitude());//YD 2020 written by Ashish

        //updateLocation(this);//YD 2020 written by yash (code taken from AceRouteBroadcast)// YD 2020 by yash
        //doWork();
        //startLocationUpdates();

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.d("TAG555", "DESTROY");
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        Log.d("TAG555", "ll");
        stopForeground(true);
        stopSelf();
        super.unbindService(conn);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("TAG555", "ll");
        stopForeground(true);
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void doWork() {

        AlarmManager alarmManager = (AlarmManager) getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {

            Intent intent = new Intent(getApplicationContext(), AceRouteBroadcast.class);
            intent.setAction(Utilities.ACE_GEO_SYNC_ACTION);
            PendingIntent geosyncIndent = PendingIntent.getBroadcast(getApplicationContext(), 2,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

           /* boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), 2,new Intent(ACE_GEO_SYNC_ACTION),PendingIntent.FLAG_NO_CREATE) != null);
            if(!alarmUp)*/
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 1, 1000 * 10, geosyncIndent);
        }
    }

    private void updateLocation(final Context context) {
        Logger.i("AceRoute Geo", "UpdateLocation called in updatelocation service with context as : " + context);
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

                String templocation = location.replace(
                        "#", ",");
                GeoSyncAlarmRequest geoSyncAlarmreq = new GeoSyncAlarmRequest();
                geoSyncAlarmreq.setAction(Api.API_ACTION_SAVE_RES_GEO);
                geoSyncAlarmreq.setData(templocation);

                Logger.i("AceRoute Geo", "save current Geo as : " + templocation);
                bundle.putParcelable("OBJECT", geoSyncAlarmreq);
                /*******************YD*e*********************/

                Intent mIntent = new Intent(
                        context.getApplicationContext(),
                        AceRouteService.class);
                mIntent.putExtras(bundle);
                Utilities
                        .log(context
                                        .getApplicationContext(),
                                "Starting acerouteservice from broadcast. "
                                        + mIntent
                                        .getExtras()
                                        .getString(
                                                AceRouteService.KEY_MESSAGE));
                context.startService(mIntent);
            }
        }

        Calendar calendar = Utilities.getCalendarInstance(context);
        long currenttime = calendar.getTimeInMillis();

        if (tidStatus) {
            try {
                Log.i(Utilities.TAG, "Starting geo alarm for "
                        + currenttime);
                setGeoTimeAndAlarm(context, currenttime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setGeoTimeAndAlarm(Context context, long currenttime) {
        Log.i(Utilities.TAG, "For now not setting geobeat : " + currenttime);
        PreferenceHandler.setAcerouteGeoTime(context, currenttime);
        Log.i(Utilities.TAG, "Setting geo alarm again.");
        //Utilities.startGeoSyncAlarm(context);
    }

    private void updateLocation(final Context context, double latitude, double longitude) {
        Logger.i("AceRoute Geo", "UpdateLocation called in updatelocation service with context asa : " + context);
        boolean tidStatus = PreferenceHandler.getTidStatus(context);
    /*    if (PreferenceHandler.getMtoken(context) != null
                && PreferenceHandler.getMtoken(context).length() != 0
                && PreferenceHandler.getCompanyId(context) != null
                && PreferenceHandler.getCompanyId(context).length() != 
0
                && tidStatus == true) {*/
//            Log.i(Utilities.TAG,
//                    "setting current location");

        Log.i(Utilities.TAG,
                "Ashish current location : " + latitude + " :" + longitude);
        if (latitude != 0
                && longitude != 0) {
            PreferenceHandler.setPrefLat(context,
                    latitude + "");
            PreferenceHandler.setPrefLong(context,
                    longitude + "");

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
            String templocation = latitude + "," + longitude;
            GeoSyncAlarmRequest geoSyncAlarmreq = new GeoSyncAlarmRequest();
            geoSyncAlarmreq.setAction(Api.API_ACTION_SAVE_RES_GEO);
            geoSyncAlarmreq.setData(templocation);

            bundle.putParcelable("OBJECT", geoSyncAlarmreq);
            /*******************YD*e*********************/

            Intent mIntent = new Intent(
                    context.getApplicationContext(), //YD can be used also instead of getapplicationContext -> getBaseContext()
                    AceRouteService.class);
            mIntent.putExtras(bundle);
            Utilities
                    .log(context
                                    .getApplicationContext(),
                            "Starting acerouteservice from broadcast. "
                                    + mIntent
                                    .getExtras()
                                    .getString(
                                            AceRouteService.KEY_MESSAGE));
            context.startService(mIntent);
           /* new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //stopSelf();
                }
            }, 3000);*///YD commented
        }
//        }
    }
    /*Test Start 2 with Ash */

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        if (PreferenceHandler.getGeoSyncTimeToServer(getApplicationContext()) > 0)
            PreferenceHandler.setGeoSyncTimeToServer(getApplicationContext(), 0);
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onConnected(Bundle bundle) {//YD this get automatically called after connected by addConnectionCallbacks method above
        Log.i("AceRoute", "onConnected" + bundle);

        Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (l != null) {
            Log.i("AceRoute", "lat " + l.getLatitude());
            Log.i("AceRoute", "lng " + l.getLongitude());

        }

        startLocationUpdate();
    }

    private void startLocationUpdate() {
        initLocationRequest();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval((PreferenceHandler.getPrefGpsSync(getApplicationContext()) / 2) * 60 * 1000);
        mLocationRequest.setFastestInterval(1000 * 60 * 4);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //Logger.i("AceRoute Geo" , "onLocationChanged called with new lat-long as :  "+location.getLatitude() +""+ location.getLongitude());
//        location.getLatitude();
//        location.getLongitude();
        //EventBus.getDefault().post(location);
        boolean shouldSyncToServer = false;

        if (PreferenceHandler.getGeoSyncTimeToServer(getApplicationContext()) <= 0) {
            //Logger.i("AceRoute Geo" , "Found last geo sync Time to server as 0 or null ");
            PreferenceHandler.setGeoSyncTimeToServer(getApplicationContext(), Utilities.getCurrentTimeInMillis());
            shouldSyncToServer = true;
        } else {
            long currentTime = Utilities.getCurrentTimeInMillis();
          /*  Logger.i("AceRoute Geo" , "Found last geo sync Time to server as :  "
                    +PreferenceHandler.getGeoSyncTimeToServer(getApplicationContext()) +" and current time as "+ currentTime+
                    " and min time Difference Required is : "+PreferenceHandler.getPrefGpsSync(getApplicationContext()));*/

            if (currentTime - PreferenceHandler.getGeoSyncTimeToServer(getApplicationContext()) >
                    PreferenceHandler.getPrefGpsSync(getApplicationContext())) {
                PreferenceHandler.setGeoSyncTimeToServer(getApplicationContext(), currentTime);
                shouldSyncToServer = true;
            }
        }
        shouldSyncToServer = true;
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Utilities.ACE_GEO_SYNC_ACTION_TEST);
        broadcastIntent.setClass(this, AceRouteBroadcast.class);
        broadcastIntent.putExtra("geoExtra", location.getLatitude() + "," + location.getLongitude());
        broadcastIntent.putExtra("shouldSyncToServer", shouldSyncToServer);
        //Logger.i("AceRoute Geo" , "shouldSyncToServer :  "+shouldSyncToServer );
        this.sendBroadcast(broadcastIntent);
    }
    /* Test end 2 */

    /*YD Test Start 1 Own */

    /*protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest1 = new LocationRequest();
        mLocationRequest1.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest1.setInterval(UPDATE_INTERVAL);
        mLocationRequest1.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest1);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest1, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChangedd(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChangedd(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Utilities.ACE_GEO_SYNC_ACTION);
        broadcastIntent.setClass(this, AceRouteBroadcast.class);
        broadcastIntent.putExtra("geoExtra", location.getLatitude() + "-" + location.getLongitude());
        this.sendBroadcast(broadcastIntent);
    }

    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }*/
    /* Test end 1 */
}
