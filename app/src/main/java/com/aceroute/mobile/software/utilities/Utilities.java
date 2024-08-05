package com.aceroute.mobile.software.utilities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aceroute.mobile.software.AceRouteApplication;
import com.aceroute.mobile.software.AceRouteBroadcast;
import com.aceroute.mobile.software.AceRouteService;
import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.adaptor.OrderListAdapter;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.dialog.CustomListner;
import com.aceroute.mobile.software.dialog.CustomStatusDialog;
import com.aceroute.mobile.software.dialog.CustomTimePickerDialog;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Api;
import com.aceroute.mobile.software.http.RequestObject;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.network.AceRequestHandler;
import com.aceroute.mobile.software.requests.GeoSyncAlarmRequest;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

@SuppressLint("NewApi")
public class Utilities implements LocationListener {

    // constants for media
	/*public static final String MEDIA_TYPE_CAPTURE_IMAGE = "pickimage";
	public static final String MEDIA_TYPE_OPEN_VOICE_RECORDER = "voicerecorder";

	public static final int MEDIA_FETCH_SUCCESS = 1;
	public static final int MEDIA_FETCH_FAILURE = 0;*/

    /*
     * used for setting ids for acerequest handler like if 0 then send pubnub
     * callback and if -1 then send no callback.
     */

    public static final int DEFAULT_PUBNUB_REQUEST_ID = 0;
    public static final int DEFAULT_NOCALLBACK_REQUEST_ID = -1;
    public static final int DEFAULT_ACTIVITY_REQUEST_ID_GETFMETA = -2;
    public static final int DEFAULT_ACTIVITY_REQUEST_ID_SAVECPIC = -3;
    public static final int DEFAULT_ACTIVITY_REQUEST_ID_DELETECPIC = -4;
    public static final int DEFAULT_ACTIVITY_REQUEST_ID_GETNOTES = -5;
    public static final int DEFAULT_ACTIVITY_REQUEST_ID_SAVENOTES = -6;
    public static final int DEFAULT_ACTIVITY_REQUEST_ID_SAVESIGN = -7;
    public static final int DEFAULT_ACTIVITY_REQUEST_ID_GETCUSTLIST = -8;
    public static final int DEFAULT_ACTIVITY_REQUEST_ID_GETCUSTSITE = -9;
    public static final int DEFAULT_ACTIVITY_REQUEST_ID_SAVECUSTSITE = -10;
    public static final int DEFAULT_ACTIVITY_REQUEST_ID_GETSITETYPE = -11;
    public static final int DEFAULT_START_BG_SYNC = -13;
    public static final int DEFAULT_ACTIVITY_REQUEST_ID_CUST_FOR_MAP = -14;
    public static final int DEFAULT_ACTIVITY_REQUEST_ID_GETTASK = -15;
    public static final int DEFAULT_ACTIVITY_REQUEST_PANIC_REQUEST = -16;
    public static final int DEFAULT_ACTIVITY_REQUEST_ID_SAVECPICFORTASK = -17;
    public static final int ACEROUTE_NOTIFICATION_ID = 123456;
    public static final int ACEROUTE_SMS_NOTIFICATION_ID = 123457;
    public static final String ACEROUTE_NOTIFICATION_DEF_TITLE = "Aceroute";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final int DEFAULT_BOUNDARY_DAYS = 14;
    public static final int DEFAULT_OFFLINE_SYNC_BOUNDARY_DAYS = 1;
    public static final int DEFAULT_WEEK_DAYS = 7;
    public static final int TIME_TYPE_START = 1;
    public static final int TIME_TYPE_END = 2;
    public static final int DEFAULT_EDITION_PARAMETER = 299;
    /**
     * The below 1 constant used for log.TAG purpose.
     */
    public static final String TAG = "software";
    public static final String TAG_PLAYBK = "aceroute_playbk";
    public static final String JS_TAG = "software-javascript";
    /**
     * Default heartbeat for software
     */
    public final static int HEART_BEAT_TIME = 18;
    public final static int HEART_BEAT_DEBUG_TIME = 2;
    public final static long TIME_MINUTE = 60 * 1000;
    private static final int SYNC_LOCATION_JOB_ID = 123;
    /**
     * The below 4 constant are used to define the service status.
     */
    /*
     * public static final byte SERVICE_NOT_STARTED = 0; public static final
     * byte SERVICE_ALREADY_RUNNING = 1; public static final byte
     * SERVICE_STARTED = 2; public static final byte SERVICE_STOPPED = 3;
     */
    public final static long ACE_HEART_BEAT = HEART_BEAT_TIME * TIME_MINUTE;
    public final static long ACE_HEART_BEAT_DEBUG = HEART_BEAT_DEBUG_TIME
            * TIME_MINUTE;
    public final static String ACE_HEART_BEAT_ACTION = "com.software.mobile.software.action.heartbeat_broadcast";
    public final static String ACE_GEO_SYNC_ACTION = "com.software.mobile.software.action.geosync_broadcast";
    public final static String ACE_GEO_SYNC_ACTION_TEST = "com.software.mobile.software.action.geosync_broadcast_TEST";
    public final static String ACE_LISTEN_DATE_CHANGE = "com.software.mobile.software.action.datechange_broadcast";
    public final static String ACE_AUTO_SYNC = "com.software.mobile.software.action.autosync_broadcast";
    public final static String ACE_LOCATION_UPDATE_ACTION = "com.software.mobile.software.action.location_update_broadcast";
    static final String SERVER_URL = "url";
    private static final String KEY_MESSAGE_SUSSESE_TRUE = "success";
    private static final int TWO_MINUTES = 1000 * 2;
    public static String shftlock;
    public static Context context;
    /************************/
    //public static String CREATEORDER = "New";
    public static String MAP = "Map";
    public static String JOBS = "Inbox";
    public static String CLOCKIN = "Clock In";
    public static String OPTION = "More";
    private static BroadcastReceiver smsDeliveredReceiver;
    private static BroadcastReceiver smsSentReceiver;

    /************************/

    public static String getTimeZone() {
        String summerStr = "30-06-2010";
        String winterStr = "30-12-2010";

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date summerDate = null;
        Date winterDate = null;
        try {
            summerDate = dateFormat.parse(summerStr);
            winterDate = dateFormat.parse(winterStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        TimeZone tz = Calendar.getInstance().getTimeZone();
        int so;
        int wo;
        if (summerDate != null && winterDate != null) {
            so = /*-1**/(tz.getOffset(summerDate.getTime()) / (60 * 1000));
            wo = /*-1**/(tz.getOffset(winterDate.getTime()) / (60 * 1000));


            if (-660 == so && -660 == wo) return "Pacific/Midway";
            if (-600 == so && -600 == wo) return "Pacific/Tahiti";
            if (-570 == so && -570 == wo) return "Pacific/Marquesas";
            if (-540 == so && -600 == wo) return "America/Adak";
            if (-540 == so && -540 == wo) return "Pacific/Gambier";
            if (-480 == so && -540 == wo) return "US/Alaska";
            if (-480 == so && -480 == wo) return "Pacific/Pitcairn";
            if (-420 == so && -480 == wo) return "US/Pacific";
            if (-420 == so && -420 == wo) return "US/Arizona";
            if (-360 == so && -420 == wo) return "US/Mountain";
            if (-360 == so && -360 == wo) return "America/Guatemala";
            if (-360 == so && -300 == wo) return "Pacific/Easter";
            if (-300 == so && -360 == wo) return "US/Central";
            if (-300 == so && -300 == wo) return "America/Bogota";
            if (-240 == so && -300 == wo) return "US/Eastern";
            if (-240 == so && -240 == wo) return "America/Caracas";
            if (-240 == so && -180 == wo) return "America/Santiago";
            if (-180 == so && -240 == wo) return "Canada/Atlantic";
            if (-180 == so && -180 == wo) return "America/Montevideo";
            if (-180 == so && -120 == wo) return "America/Sao_Paulo";
            if (-150 == so && -210 == wo) return "America/St_Johns";
            if (-120 == so && -180 == wo) return "America/Godthab";
            if (-120 == so && -120 == wo) return "America/Noronha";
            if (-60 == so && -60 == wo) return "Atlantic/Cape_Verde";
            if (0 == so && -60 == wo) return "Atlantic/Azores";
            if (0 == so && 0 == wo) return "Africa/Casablanca";
            if (60 == so && 0 == wo) return "Europe/London";
            if (60 == so && 60 == wo) return "Africa/Algiers";
            if (60 == so && 120 == wo) return "Africa/Windhoek";
            if (120 == so && 60 == wo) return "Europe/Amsterdam";
            if (120 == so && 120 == wo) return "Africa/Harare";
            if (180 == so && 120 == wo) return "Europe/Athens";
            if (180 == so && 180 == wo) return "Africa/Nairobi";
            if (240 == so && 180 == wo) return "Europe/Moscow";
            if (240 == so && 240 == wo) return "Asia/Dubai";
            if (270 == so && 210 == wo) return "Asia/Tehran";
            if (270 == so && 270 == wo) return "Asia/Kabul";
            if (300 == so && 240 == wo) return "Asia/Baku";
            if (300 == so && 300 == wo) return "Asia/Karachi";
            if (330 == so && 330 == wo) return "Asia/Calcutta";
            if (345 == so && 345 == wo) return "Asia/Katmandu";
            if (360 == so && 300 == wo) return "Asia/Yekaterinburg";
            if (360 == so && 360 == wo) return "Asia/Colombo";
            if (390 == so && 390 == wo) return "Asia/Rangoon";
            if (420 == so && 360 == wo) return "Asia/Almaty";
            if (420 == so && 420 == wo) return "Asia/Bangkok";
            if (480 == so && 420 == wo) return "Asia/Krasnoyarsk";
            if (480 == so && 480 == wo) return "Australia/Perth";
            if (540 == so && 480 == wo) return "Asia/Irkutsk";
            if (540 == so && 540 == wo) return "Asia/Tokyo";
            if (570 == so && 570 == wo) return "Australia/Darwin";
            if (570 == so && 630 == wo) return "Australia/Adelaide";
            if (600 == so && 540 == wo) return "Asia/Yakutsk";
            if (600 == so && 600 == wo) return "Australia/Brisbane";
            if (600 == so && 660 == wo) return "Australia/Sydney";
            if (630 == so && 660 == wo) return "Australia/Lord_Howe";
            if (660 == so && 600 == wo) return "Asia/Vladivostok";
            if (660 == so && 660 == wo) return "Pacific/Guadalcanal";
            if (690 == so && 690 == wo) return "Pacific/Norfolk";
            if (720 == so && 660 == wo) return "Asia/Magadan";
            if (720 == so && 720 == wo) return "Pacific/Fiji";
            if (720 == so && 780 == wo) return "Pacific/Auckland";
            if (765 == so && 825 == wo) return "Pacific/Chatham";
            if (780 == so && 780 == wo) return "Pacific/Enderbury";
            if (840 == so && 840 == wo) return "Pacific/Kiritimati";
        }
        return "US/Pacific";
    }

    public static void log(Context context, String text) {
        Log.i(TAG, text);
        Utilities.context = context;
    }

    public static long getCurrentTimeInMillis() {
        return getCalendarInstance(context).getTimeInMillis();
    }

    public static String convertDateToUtc(long milliseconds) {
        Date date = new Date(milliseconds);

        SimpleDateFormat convStrToDate = new SimpleDateFormat("yyyy/MM/dd HH:mm");//have to send "2015/06/02 11:25 -00:00"
        convStrToDate.setTimeZone(TimeZone.getTimeZone("UTC"));

        String dateToSend = convStrToDate.format(date);
        dateToSend = dateToSend + " -00:00";
        return dateToSend;
    }

    public static String convertDateToUtcWithDateFormat(long milliseconds, SimpleDateFormat convStrToDate) {
        Date date = new Date(milliseconds);

        convStrToDate.setTimeZone(TimeZone.getTimeZone("UTC"));

        String dateToSend = convStrToDate.format(date);
        dateToSend = dateToSend + " -00:00";
        return dateToSend;
    }

    public static String convertElementToString(Node element)
            throws TransformerConfigurationException,
            TransformerFactoryConfigurationError {
        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        // initialize StreamResult with File object to save to file
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(element);

        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            Log.e(
                    Utilities.TAG,
                    "CONVERT_ELEMENT_TO_STRING"
                            + "converting element to string failed. Aborting : "
                            + e.getMessage());
        }

        String xmlString = result.getWriter().toString();
        xmlString = xmlString.replace(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
        xmlString = xmlString.replace("\n", "");
        return xmlString;
    }

    public static void stopHeartbeatAlarm(Context context) {
        Intent intentstop = new Intent(context, AceRouteBroadcast.class);
        PendingIntent senderstop = PendingIntent.getBroadcast(context, 1,
                intentstop, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManagerstop = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManagerstop.cancel(senderstop);
    }

    public static void stopGeoAlarm(Context context) {
        Intent intentstop2 = new Intent(context, AceRouteBroadcast.class);
        PendingIntent senderstop2 = PendingIntent.getBroadcast(context, 2,
                intentstop2, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManagerstop2 = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManagerstop2.cancel(senderstop2);
    }

    public static void stopAlarms(Context context) {
        stopHeartbeatAlarm(context);
        stopGeoAlarm(context);
    }

    public static void startGeoSyncAlarm(Context context) {
        //Logger.i("AceRoute Geo" , "Start Geo Sync Alarm.");
        AlarmManager manager = (AlarmManager) context.getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AceRouteBroadcast.class);
        intent.setAction(ACE_GEO_SYNC_ACTION);
        PendingIntent geosyncIndent = PendingIntent.getBroadcast(context, 2,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        int time = PreferenceHandler.getPrefGpsSync(context);
        Log.i(Utilities.TAG, "setting geo sync alarm: " + time);
        //Logger.i("AceRoute Geo" , "setting geo sync alarm: " + time);
        //long intervalTime = time * TIME_MINUTE * 1000;
        long triggerTime = System.currentTimeMillis() + time * TIME_MINUTE;
        //long triggerTime = System.currentTimeMillis() + 1 * TIME_MINUTE;
        long intervalTime = triggerTime;
        Log.i(Utilities.TAG, "setting geo alarm : " + intervalTime);
        //manager.setRepeating(AlarmManager.RTC_WAKEUP, intervalTime, time * TIME_MINUTE, geosyncIndent);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 1, 1, geosyncIndent);//YD 2020 used for testing
        if (PreferenceHandler.getgeoFlagFirst(context)) {
            manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 200, geosyncIndent);
            PreferenceHandler.setgeoFlagFirst(context, false);
        }
    }

    public static void startAlarmManager(Context context) {
        /*PendingIntent pendingIntent;
        Logger.i("AceRoute Geo" , "Current version is : "+ android.os.Build.VERSION.SDK_INT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            pendingIntent = PendingIntent.getForegroundService(context, SYNC_LOCATION_JOB_ID, new Intent(context, UpdateLocationService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getService(context, SYNC_LOCATION_JOB_ID, new Intent(context, UpdateLocationService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            int time = PreferenceHandler.getPrefGpsSync(context);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),time * TIME_MINUTE, pendingIntent);
        }*/
        Intent serviceIntent = new Intent(context, UpdateLocationService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(context, serviceIntent);
    }

    public static void stopAlarmManager(Context context) {
        PendingIntent pendingIntent;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            pendingIntent = PendingIntent.getForegroundService(context, SYNC_LOCATION_JOB_ID, new Intent(context, UpdateLocationService.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getService(context, SYNC_LOCATION_JOB_ID, new Intent(context, UpdateLocationService.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public static boolean checkInternetConnection(Context context, boolean showMsg) {

        int k = 1;
        if (k == 2)
            return false;

        ConnectivityManager localConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((localConnectivityManager.getActiveNetworkInfo() != null)
                && (localConnectivityManager.getActiveNetworkInfo()
                .isAvailable())
                && (localConnectivityManager.getActiveNetworkInfo()
                .isConnected())) {
            return true;
        } else {
            try {
                if (showMsg)
                    Toast.makeText(context, "No internet connection",
                            Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public static String getLocation(Context mContext) {// YD 2020 called from BroadcastReceiver
        try {
            Utilities.context = context;
            Log.i(Utilities.TAG, "####Getting location####");
            double longitude = 0L;
            double latitude = 0L;

            Location location = null;
            LocationManager locationManager = (LocationManager) mContext
                    .getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // if (!isGPSEnabled) { // no network provider is enabled
            // Logger.i(context, Utilities.TAG,
            // "no network provider is enabled nor gps.");

            // First get location from GPS Provider
            // if GPS Enabled get lat/long using GPS Services

            //if (isGPSEnabled) {
            if (location == null) {
                Log.d("GPS Enabled", "GPS Enabled");
                if (locationManager != null) {
                    // if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    // }


                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d("GPS Enabled", "lat : " + latitude
                                + "# long : " + longitude);
                    }

                    /*
                     * try { locationManager.requestLocationUpdates(
                     * LocationManager.GPS_PROVIDER, 1000 * 60 * 10, 1, new
                     * Utilities()); } catch (Exception e) {
                     * e.printStackTrace();
                     *
                     * }
                     */
                }
            }
            //}

            if (latitude == 0 && longitude == 0) {
                //if (isNetworkEnabled) {
                // if Network enabled get lat/long using GPS services
                Log.d("Network", "Network");
                if (locationManager != null) {
                    if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }

                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d("Network", "lat : " + latitude
                                + "# long : " + longitude);
                    }
                }
            }
            if (latitude == 0 && longitude == 0) {
                try {
                    String newLoc = PreferenceHandler.getTempLastLocation(mContext);
                    if (newLoc != null) {
                        String newLocs[] = newLoc.split(",");
                        latitude = Double.parseDouble(newLocs[0]);
                        longitude = Double.parseDouble(newLocs[1]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            final String loc = String.valueOf(latitude) + ","
                    + String.valueOf(longitude);
			/*try {
				AceRoute.handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, "Location : "+loc, Toast.LENGTH_LONG).show();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}*/
            Log.i(Utilities.TAG, "####returning location : " + loc
                    + "####");
            return loc;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static boolean isValidCurrentDate(Context context, Date currentDate) {// currentDate = Sat Jun 27 14:00:00 GMT+05:30 2015
        Date date = new Date();
        if (PreferenceHandler.getOdrGetDate(context) != null && !PreferenceHandler.getOdrGetDate(context).equals("")) {
            return Utilities.chkDtEqualCurrentDt(context, currentDate);
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String c_date = dateFormat.format(currentDate);
        String t_date = dateFormat.format(date);
        if (c_date.equals(t_date)) {
            return true;
        }
        return false;
    }

    /**
     * Checking if the date is between existing two week data.  YD to check boundary data very important
     */
    public static boolean isValidDate(Date currentDate) {// currentDate = Sat Jun 27 14:00:00 GMT+05:30 2015
        Calendar calendar = getCalendarInstance(context);
        // calendar.setTimeInMillis(getCurrentTimeInMillis());
        // calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        // calendar.add(Calendar.DAY_OF_WEEK, 1);
        Date firstDate = calendar.getTime();
        int date1, date2;

        Date date = new Date();
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTime(date);
        int day = tempCalendar.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SUNDAY) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            calendar.add(Calendar.DAY_OF_MONTH, -6);
            firstDate = calendar.getTime();
        }

        date1 = calendar.get(Calendar.DATE);

        long firstMilli = calendar.getTimeInMillis();
        int firstDay = calendar.get(Calendar.DAY_OF_MONTH);
        int firstDateMonth = calendar.get(Calendar.MONTH);
        int firstDateYEAR = calendar.get(Calendar.YEAR);

        // subtracting one because boundary end date should be after 14 days
        // from starting date but end date after 14 days is not in boundary,
        // so we have to make request for this.
        calendar.add(Calendar.DAY_OF_MONTH, DEFAULT_BOUNDARY_DAYS - 1);
        Date lastDate = calendar.getTime();
        calendar.setTime(lastDate);

        date2 = calendar.get(Calendar.DATE);

        long lastMilli = calendar.getTimeInMillis();
        int lastDay = calendar.get(Calendar.DAY_OF_MONTH);
        int lastDateMonth = calendar.get(Calendar.MONTH);
        int lastDateYEAR = calendar.get(Calendar.YEAR);

        calendar.setTime(currentDate);

        long currentMilli = calendar.getTimeInMillis();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentDateMonth = calendar.get(Calendar.MONTH);
        int currentDateYEAR = calendar.get(Calendar.YEAR);

        Log.i(Utilities.TAG, "to : " + lastMilli);
        Log.i(Utilities.TAG, "current : " + currentMilli);
        Log.i(Utilities.TAG, "From : " + firstMilli);

        Log.i(Utilities.TAG, "firstDate : " + firstDate.toGMTString());
        Log.i(Utilities.TAG, "LastDate : " + lastDate.toGMTString());

        boolean isInBoundary = false;
        /*
         * if(currentMilli >= lastMilli && currentMilli <= firstMilli){
         * Logger.i(context, Utilities.TAG, "Milliseconds condition passed");
         * isInBoundary = true; }
         */
        isInBoundary = firstDate.compareTo(currentDate)
                * currentDate.compareTo(lastDate) > 0;

        if (isInBoundary == false && currentDay == lastDay
                && currentDateMonth == lastDateMonth
                && currentDateYEAR == lastDateYEAR) {
            isInBoundary = true;
        }

        if (!isInBoundary) {
            if (currentDateYEAR == firstDateYEAR) {
                Log.i(Utilities.TAG, "Year matched");
                if (currentDateMonth == firstDateMonth) {
                    Log.i(Utilities.TAG, "Equal Month matched");
                    if (currentDay == firstDay) {
                        isInBoundary = true;
                    }
                }
            }
        }
        Log.i(Utilities.TAG, "isInBoundary : " + isInBoundary);
        return isInBoundary;
    }

    public static boolean isTodayDate(Context context, Date date) {
        Calendar calendar1 = getCalendarInstance(context);
        calendar1.setTime(date);

        Calendar calendar2 = getCalendarInstance(context);
        calendar2.setTimeInMillis(getCurrentTime());

        if (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.MONTH) == calendar2
                .get(Calendar.MONTH)
                && calendar1.get(Calendar.DATE) == calendar2.get(Calendar.DATE)) {
            return true;
        }
        return false;
    }

    //YD checking if the date send is equal to the date changed using footer option menu
    public static boolean chkDtEqualCurrentDt(Context context, Date date) {
        Calendar calendar1 = getCalendarInstance(context);
        calendar1.setTime(date);

        Calendar calendar2 = getCalendarInstance(context);
        calendar2.setTimeInMillis(PreferenceHandler.getodrLastSyncForDt(context));

        if (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.MONTH) == calendar2
                .get(Calendar.MONTH)
                && calendar1.get(Calendar.DATE) == calendar2.get(Calendar.DATE)) {
            return true;
        }
        return false;
    }

    //YD convert date to custom date because date.getyear etc are depricated now
    public static String chkAndGetCustDt(Context context, Date date) {
        Calendar calendar1 = getCalendarInstance(context);
        calendar1.setTime(date);
        int yr = calendar1.get(Calendar.YEAR);
        int mth = calendar1.get(Calendar.MONTH) + 1;
        int dt = calendar1.get(Calendar.DATE);

        return yr + "-" + mth + "-" + dt;
    }

    public static String getStringFromDate(Context context, Date date,
                                           String format) {
        SimpleDateFormat smp = new SimpleDateFormat(format);
        return smp.format(date);
    }

    private static void showNotification1(Context context, String title,
                                          String message) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            Notification notification = new NotificationCompat.Builder(context)
                    .setContentTitle(title).setContentText(message).setNotificationSilent()
                    .setSmallIcon(R.drawable.ic_launcher).build();
            notificationManager.cancelAll();
            notificationManager.notify(ACEROUTE_SMS_NOTIFICATION_ID,
                    notification);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getRealPathFromURI(Activity activity, Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(contentUri,
                    proj, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
            String[] values = contentUri.toString().split("/");
            if (values.length > 0) {
                String name = values[values.length - 1];
                Log.i(Utilities.TAG,
                        "returning filename : " + name);
                return name;
            } else {
                Log.i(Utilities.TAG,
                        "ERROR : not finding file name");
                return null;
            }
        }
    }

    public static Uri getPathfromUri(String path) {
        return Uri.fromFile(new File(path));
    }

    // sound recording and playing methods.
    public static synchronized void start_record(Context context, File file) {
        try {
            Log.i(Utilities.TAG, "1");
            AceRouteApplication acerouteapp = AceRouteApplication.getInstance();
            Log.i(Utilities.TAG, "2");
            acerouteapp.setRecorderObject();
            Log.i(Utilities.TAG, "2.5");
            MediaRecorder recorder = acerouteapp.getRecorderObject();
            Log.i(Utilities.TAG, "3");
            //if (recorder != null) {
            //	recorder.stop();
            //	recorder.release();
            //}

            recorder.setAudioSource(AudioSource.MIC);
            Log.i(Utilities.TAG, "4");
            //recorder.setOutputFormat(OutputFormat.THREE_GPP);
            recorder.setOutputFormat(OutputFormat.MPEG_4);
            Log.i(Utilities.TAG, "5");
            //recorder.setAudioEncoder(AudioEncoder.AMR_NB);//YD use for 3gp format
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);//YD changing for m4a format
            Log.i(Utilities.TAG, "6");
            recorder.setOutputFile(file.getAbsolutePath());
            Log.i(Utilities.TAG, "7");
            Log.i(Utilities.TAG, "Recording in file : " + file.getAbsolutePath());
            try {
                Log.i(Utilities.TAG, "8");
                recorder.prepare();
                Log.i(Utilities.TAG, "9");
                recorder.start();
                Log.i(Utilities.TAG, "10");
            } catch (IOException e) {
                Log.e(
                        Utilities.TAG,
                        "io problems while preparing ["
                                + file.getAbsolutePath() + "]: "
                                + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void stop_record(Context context) {
        try {
            AceRouteApplication acerouteapp = AceRouteApplication.getInstance();
            MediaRecorder recorder = acerouteapp.getRecorderObject();
            if (recorder != null) {
                Log.i(Utilities.TAG, "Release and stop recording.");
                recorder.stop();
                Log.i(Utilities.TAG, "stop 1");
                recorder.reset();
                Log.i(Utilities.TAG, "stop 2");
                recorder.release();
                Log.i(Utilities.TAG, "stop 3");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MediaPlayer play_recording(Context context, String uriString) {
        if (uriString == null)
            return null;
        try {
            Uri uri = Uri.parse(uriString);
            AceRouteApplication acerouteapp = AceRouteApplication.getInstance();
            MediaPlayer mplayer = acerouteapp.getPlayerObject(context, uri);
            Log.i(Utilities.TAG, "playing file in looping");
            mplayer.setLooping(false);
            mplayer.start();
            Log.i(Utilities.TAG, "Media player state : " + mplayer.isPlaying());
            return mplayer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void stop_player() {
        try {
            AceRouteApplication acerouteapp = AceRouteApplication.getInstance();
            MediaPlayer mplayer = acerouteapp.getPlayerObject();
            if (mplayer != null && mplayer.isPlaying()) {
                mplayer.stop();
                mplayer.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized String getRealPathFromURI(Context context,
                                                         Uri contentURI) {
        Cursor cursor = context.getContentResolver().query(contentURI, null,
                null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public static Calendar getCalendarInstance(Context context) {
        Calendar calendar = null;
        try {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            Log.i(Utilities.TAG,
                    "First day of week : " + calendar.getFirstDayOfWeek());
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            if (calendar.getFirstDayOfWeek() == Calendar.SUNDAY)
                calendar.add(Calendar.DAY_OF_WEEK, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendar;
    }

    /*************** For download audio file *****************/
    public static String DownloadFiles(Context context, String url1, String basepath,
                                       String fileName) { //YD
        try {
            URL url = new URL(url1);// vicky this needs to be revised
            URLConnection conexion = url.openConnection();
            conexion.connect();
            //int lenghtOfFile = conexion.getContentLength();//YD making first call on server.
            InputStream is = url.openStream(); // YD making second call on server.
            File testDirectory = null;
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                testDirectory = new File(
                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                                + "/" + basepath);
				/*testDirectory = new File(   //YD commented because it was creating new directory in picture/AceRoute folder
						Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
								+ "/"+basepath);*/
            } else {
				/*testDirectory = new File(context.getCacheDir(),
						"/"+basepath);*/ // YD commenting because there is no need for cacheDirectory instead make data directory
                testDirectory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        .getAbsolutePath() + "/" + basepath);
                testDirectory.mkdir();
                Log.i(Utilities.TAG,
                        "Audio internal directory path :"
                                + testDirectory.getAbsolutePath());
            }
            if (!testDirectory.exists()) {
                Log.i(Utilities.TAG,
                        "Making directory :"
                                + testDirectory.getAbsolutePath());
                testDirectory.mkdirs();
            }
            //File checkFile = new File(testDirectory + "/" + fileName);
            File checkFile = new File(testDirectory + fileName);//YD
            if (checkFile.isFile()) {
                Log.i(Utilities.TAG,
                        "File exists Deleting :"
                                + checkFile.getAbsolutePath());
                checkFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(testDirectory + "/"
                    + fileName);
            byte data[] = new byte[1024];
            int count = 0;
            long total = 0;
            int progress = 0;
            while ((count = is.read(data)) != -1) {
                total += count;
				/*int progress_temp = (int) total * 100 / lenghtOfFile;
				if (progress_temp % 10 == 0 && progress != progress_temp) {
					progress = progress_temp;
				}*///YD comment because it was causing two call to be made on server
                fos.write(data, 0, count);
            }
            is.close();
            fos.close();

            return testDirectory.toString() + "/"
                    + fileName;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    /*************** For save file *****************/
    public static String saveFile(Context context, String basepath,
                                  String fileName, String buffer) {

        File testDirectory = null;
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                testDirectory = new File(
                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                                + "/" + basepath);
                //			testDirectory = new File(
                //				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                //							+ "/"+basepath);
            } else {
                testDirectory = new File(context.getCacheDir(),
                        "/" + basepath);
                testDirectory.mkdir();
                Log.i(
                        Utilities.TAG,
                        "Audio internal directory path :"
                                + testDirectory.getAbsolutePath());
            }
            if (!testDirectory.exists()) {
                Log.i(
                        Utilities.TAG,
                        "Making directory :"
                                + testDirectory.getAbsolutePath());
                testDirectory.mkdir();
            }
            //File checkFile = new File(testDirectory + "/" + fileName);
            File checkFile = new File(testDirectory + fileName);
            if (checkFile.isFile()) {
                Log.i(
                        Utilities.TAG,
                        "File exists Deleting :"
                                + checkFile.getAbsolutePath());
                checkFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(testDirectory + "/"
                    + fileName);
            byte[] data = buffer.getBytes("UTF-8");

            fos.write(data);

            fos.close();

            return testDirectory.toString() + "/"
                    + fileName;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean deleteFile(Context context, String sourcePath) {

        File checkFile = new File(sourcePath);
        if (checkFile.isFile()) {
            checkFile.delete();
        }
        return false;
    }

    public static String moveFile(Context context, String basepath,
                                  String fileName, String sourcePath) {

        File testDirectory = null;
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                testDirectory = new File(
                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                                + "/" + basepath);
                //			testDirectory = new File(
                //				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                //							+ "/"+basepath);
            } else {
                testDirectory = new File(context.getCacheDir(),
                        "/" + basepath);
                testDirectory.mkdir();
                Log.i(Utilities.TAG,
                        "Audio internal directory path :"
                                + testDirectory.getAbsolutePath());
            }
            if (!testDirectory.exists()) {
                Log.i(Utilities.TAG,
                        "Making directory :"
                                + testDirectory.getAbsolutePath());
                testDirectory.mkdir();
            }
            //File checkFile = new File(testDirectory + "/" + fileName);
            File checkFile = new File(testDirectory + fileName);
            if (checkFile.isFile()) {
                Log.i(Utilities.TAG,
                        "File exists Deleting :"
                                + checkFile.getAbsolutePath());
                checkFile.delete();
            }
            InputStream in = new FileInputStream(sourcePath);
            FileOutputStream fos = new FileOutputStream(testDirectory + "/"
                    + fileName);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
            in.close();
            in = null;

            fos.flush();
            fos.close();
            new File(sourcePath).delete();
            return testDirectory.toString() + "/"
                    + fileName;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // send sms code directly sending sms from device
    public static void sendSMS(final Context context, String message,
                               String phoneNumber) throws InterruptedException {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        showNotification1(context, ACEROUTE_NOTIFICATION_DEF_TITLE,
                "Sending message ...");

        final NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent sentPI = PendingIntent.getBroadcast(
                context.getApplicationContext(), 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(
                context.getApplicationContext(), 0, new Intent(DELIVERED), 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    public static int getDisplayHeigth(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getDisplayWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static String getCompleteFilePathforApp(String filename) {
        String compfilename = null;
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            compfilename = Environment.getExternalStorageDirectory().getAbsolutePath();
//        } else {
//            compfilename = Environment.getDataDirectory().getAbsolutePath();
//
//        }
        compfilename = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();

        compfilename = compfilename + "/" + filename;
        return compfilename;
    }

    public static boolean IsResponseError(String response) {

        XMLHandler xmlhandler = new XMLHandler(context);
        Document doc = xmlhandler.getDomElement(response);
        if (doc != null) {
            NodeList nl = doc
                    .getElementsByTagName(XMLHandler.KEY_DATA);
            for (int i = 0; i < nl.getLength(); i++) {// loop should not be requiredhere
                Element e = (Element) nl.item(i);
                String success = xmlhandler.getValue(e,
                        XMLHandler.KEY_DATA_SUCCESS);
                if (!success.equals(XMLHandler.KEY_DATA_RESP_FAIL))
                    return false;

            }
        }
        return true;

    }

    public static String getResponseErrorCode(String response) {
        String errorcode = null;
        XMLHandler xmlhandler = new XMLHandler(context);
        Document doc = xmlhandler.getDomElement(response);
        NodeList nl = doc
                .getElementsByTagName(XMLHandler.KEY_DATA_ERROR);
        for (int i = 0; i < nl.getLength(); i++) {// loop should not be requiredhere
            Element e = (Element) nl.item(i);
            errorcode = xmlhandler.getValue(e,
                    XMLHandler.KEY_DATA_ERROR);
            return errorcode;
        }
        return errorcode;

    }

    public static void sendmessageUpdatetoserver(RespCBandServST context, final String str, final String type)// type is for error and normal msg and if error then in red color
    {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (CustomStatusDialog.msgQueue != null && CustomStatusDialog.msgQueueAdapter != null) {
                        //CustomStatusDialog.ErrorMsg err = new CustomStatusDialog.ErrorMsg(str, type);

                        if (!(str.contains("Part Type List") || str.contains("Worker List") || str.contains("Site Type List") || str.contains("Customer Type List") || str.contains("Order Type List") || str.contains("Assets Type List") || str.contains("Status Type List") || str.contains("Client Site List") ||
                                str.contains("Downloading Customer List") || str.contains("Geo") || str.contains("Downloading Part Type")
                                || str.contains("Downloading Site Type") || str.contains("Downloading Assets Type")
                                || str.contains("Downloading Status Type") || str.contains("Downloading Client Site Type") || str.contains("Downloading Shifts Type")
                                || str.contains("Downloading sites") || str.contains("Downloading Task Type") || str.contains("Downloading Tech")
                                || str.contains("Downloading Job List") || str.contains("Client Shift List") || str.contains("Error Downloading Asset Type List")
                                || str.contains("Downloading sites")

                        )
                        ) {
                            if (OrderListAdapter.ordersyncmessage) {
                                if (!(str.contains("Process Start") || str.contains("Upload Start") || str.contains("Download Start")
                                        || str.contains("Upload Complete") || str.contains("Today's Orders") || str.contains("Download Complete"))) {

                                    CustomStatusDialog.msgQueue.add(str);
                                    OrderListAdapter.ordersyncmessage = false;
                                }
                            } else {
                                CustomStatusDialog.msgQueue.add(str);
                            }
                        }


                        CustomStatusDialog.msgQueueAdapter.notifyDataSetChanged();
                        if (str.equals("No Internet Connection")) {
                            CustomStatusDialog.setHeightPopup();
                        }
                    }
                }
            });
        }
    }

    /*************************yash*********************************/
    public static long getCurrentTime() {
        Calendar rightNow = Calendar.getInstance();

        // offset to add since we're not UTC

	/*long offset = rightNow.get(Calendar.ZONE_OFFSET) +
	    rightNow.get(Calendar.DST_OFFSET);

	long sinceMidnight = (rightNow.getTimeInMillis() + offset) %
	    (24 * 60 * 60 * 1000);*/
        long sinceMidnight = System.currentTimeMillis();
        return sinceMidnight;

    }

    public static long getCustDtDefaultTm() {

        long milliOfToday = getCurrentTime();
        Date currentDt = new Date(milliOfToday);
        String curentTme = currentDt.getHours() + ":" + currentDt.getMinutes();


        String savedCustDt = PreferenceHandler.getOdrGetDate(context) + " " + curentTme;

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date custDate = simpleDateFormat.parse(savedCustDt);
            return custDate.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return 0;
    }

    public static int[] getDateFromDateObj(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        //Add one to month {0 - 11}
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int[] dateArr = new int[3];
        dateArr[0] = year;
        dateArr[1] = month;
        dateArr[2] = day;
        return dateArr;
    }

    public static String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaColumns.DATA};
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static Response createResponse(String status, Object obj, String errorcode) {
        Response response = null;
        if (obj == null) {
            response = new Response();
            if (status.equals("success")) {
                response.setStatus("success");
                response.setErrorcode(errorcode);
            } else if (status.equals("failure")) {
                response.setStatus("failure");
                response.setErrorcode(errorcode);
            }
        } else if (obj instanceof HashMap<?, ?>) {
            HashMap<Long, Object> obj1 = (HashMap<Long, Object>) obj;

            response = new Response();
            if (status.equals("success")) {
                response.setStatus("success");
                response.setResponseMap(obj1);
                response.setErrorcode(errorcode);
            } else if (status.equals("failure")) {
                response.setStatus("failure");
                response.setErrorcode(errorcode);
                response.setResponseObj(null);
            }
        } else if (obj instanceof ArrayList<?>) {
            ArrayList<Object> obj1 = (ArrayList<Object>) obj;

            response = new Response();
            if (status.equals("success")) {
                response.setStatus("success");
                response.setResponseObj(obj1);
                response.setErrorcode(errorcode);
            } else if (status.equals("failure")) {
                response.setStatus("failure");
                response.setErrorcode(errorcode);
                response.setResponseObj(null);
            }
        }

        return response;
    }

    public static ArrayList<Object> setObjectList(Object[] list) {

        ArrayList<Object> newlist = new ArrayList<Object>();
        for (int i = 0; i < list.length; i++) {
            newlist.add(list[i]);
        }
        return newlist;
    }

    public static boolean isSimExists(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        int SIM_STATE = telephonyManager.getSimState();

        if (SIM_STATE == TelephonyManager.SIM_STATE_READY)
            return true;
        else {
            switch (SIM_STATE) {
                case TelephonyManager.SIM_STATE_ABSENT: // SimState =
                    // "No Sim Found!";
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED: // SimState =
                    // "Network Locked!";
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED: // SimState =
                    // "PIN Required to access SIM!";
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED: // SimState =
                    // "PUK Required to access SIM!";
                    // // Personal
                    // Unblocking Code
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN: // SimState =
                    // "Unknown SIM State!";
                    break;
            }
            return false;
        }
    }

    public static View getViewOfListByPosition(int position, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition
                + listView.getChildCount() - 1;

        if (position < firstListItemPosition || position > lastListItemPosition) {
            return listView.getAdapter().getView(position, null, listView);
        } else {
            final int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public static String convertDateToAmPM(int hours, int minutes) {
        String hour = String.valueOf(hours);
        String minute = String.valueOf(minutes);

        if (hours <= 9)
            hour = "0" + hours;
        if (minutes <= 9)
            minute = "0" + minutes;

        if (hour.equals("00")) {
            hour = "12:" + minute + " am";
        } else if (hour.equals("12")) {
            hour = "12:" + minute + " pm";
        } else if (Integer.parseInt(hour) < 12) {
            hour = hour + ":" + minute + " am";
        } else if (Integer.parseInt(hour) > 12) {
            hour = String.valueOf(hours - 12);
            if (Integer.valueOf(hour) <= 9)
                hour = "0" + hour + ":" + minute + " pm";
            else
                hour = hour + ":" + minute + " pm";
        }
        return hour;
    }

    public static String convertDateToAmPMWithout_Zero(int hours, int minutes) {
        String hour = String.valueOf(hours);
        String minute = String.valueOf(minutes);

/*	if (hours <= 9)
		hour = "0"+hours;*/
        if (minutes <= 9)
            minute = "0" + minutes;

        if (hour.equals("0")) {
            hour = "12:" + minute + " am";
        } else if (hour.equals("12")) {
            hour = "12:" + minute + " pm";
        } else if (Integer.parseInt(hour) < 12) {
            hour = hour + ":" + minute + " am";
        } else if (Integer.parseInt(hour) > 12) {
            hour = String.valueOf(hours - 12);
		/*if (Integer.valueOf(hour)<=9)
			hour = "0"+hour+":"+minute+" pm";
		else*/
            hour = hour + ":" + minute + " pm";
        }
        return hour;
    }

    public static void setDefaultFont_12(Button button) {
        try {
            if (button != null) {
                button.setBackgroundColor(button.getContext().getColor(R.color.btn_color_gray));
                button.setTextColor(Color.parseColor("#34495F"));
                button.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(context)));
                //	button.setTextSize(context.getResources().getDimension(R.dimen.text_size_14));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setDividerTitleColor(Dialog dialog, int mheight, Context mActivity) {
        try {
            context = mActivity;
            // set divider color
            int dividerId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
            View divider = dialog.findViewById(dividerId);
            if (divider != null)
                divider.setBackgroundColor(context.getResources().getColor(R.color.dlg_light_green));

            // set title color
            int textViewId = dialog.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
            TextView tv = (TextView) dialog.findViewById(textViewId);
            if (tv != null) {
                tv.setTextColor(context.getResources().getColor(R.color.dlg_light_green));
                //tv.setTypeface(null, Typeface.BOLD);
                tv.setTextSize(24 + (PreferenceHandler.getCurrrentFontSzForApp(mActivity)));
                TypeFaceFont.overrideFonts(context, tv);
                tv.setShadowLayer(0.1f, 0.1f, 0.1f, Color.parseColor("#00ffffff"));
            }

            // set Dialog Height Width

            if (mheight == 0) {
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;
                dialog.getWindow().setAttributes(lp);
            } else {
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                //	lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                //lp.height = mheight;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;

                //set the dim level of the background
                //	lp.dimAmount=0.1f; //change this value for more or less dimming
                dialog.getWindow().setAttributes(lp);
                //add a blur/dim flags
                //	dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND | WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setAlertDialogRow(AlertDialog dialog, final BaseTabActivity mActivity) {
        try {
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    ListView listView = ((AlertDialog) dialogInterface).getListView();
                    final ListAdapter originalAdapter = listView.getAdapter();

                    listView.setAdapter(new ListAdapter() {
                        @Override
                        public int getCount() {
                            return originalAdapter.getCount();
                        }

                        @Override
                        public Object getItem(int id) {
                            return originalAdapter.getItem(id);
                        }

                        @Override
                        public long getItemId(int id) {
                            return originalAdapter.getItemId(id);
                        }

                        @Override
                        public int getItemViewType(int id) {
                            return originalAdapter.getItemViewType(id);
                        }

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = originalAdapter.getView(position, convertView, parent);
                            TextView textView = (TextView) view;
                            // textView.setTextSize(context.getResources().getDimension(R.dimen.text_size_14));
                            textView.setTextSize(24 + (PreferenceHandler.getCurrrentFontSzForApp(mActivity)));//set text size programmatically if needed
                            textView.setTextColor(context.getResources().getColor(R.color.light_gray));
                            int paddingLft = 20;
                            TypeFaceFont.overrideFonts(context, textView);
                            //YD increasing the padding of the text in row if the resolution is high
                            DisplayMetrics dm = new DisplayMetrics();
                            mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
                            int dens = dm.densityDpi;
                            if (dens > DisplayMetrics.DENSITY_XHIGH)
                                paddingLft = 60;

                            textView.setPadding(paddingLft, 8, 10, 8);

                            // textView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 50 /* this is item height */));
                            return view;
                        }

                        @Override
                        public int getViewTypeCount() {
                            return originalAdapter.getViewTypeCount();
                        }

                        @Override
                        public boolean hasStableIds() {
                            return originalAdapter.hasStableIds();
                        }

                        @Override
                        public boolean isEmpty() {
                            return originalAdapter.isEmpty();
                        }


                        @Override
                        public boolean areAllItemsEnabled() {
                            return originalAdapter.areAllItemsEnabled();
                        }

                        @Override
                        public boolean isEnabled(int position) {
                            return originalAdapter.isEnabled(position);
                        }

                        @Override
                        public void registerDataSetObserver(DataSetObserver observer) {
                            // TODO Auto-generated method stub
                            originalAdapter.registerDataSetObserver(observer);
                        }

                        @Override
                        public void unregisterDataSetObserver(DataSetObserver observer) {
                            // TODO Auto-generated method stub
                            originalAdapter.unregisterDataSetObserver(observer);
                        }

                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //YD this is for alert dialog textview rows setting
    public static void setAlertDialogRowOptionPopup(AlertDialog dialog, final BaseTabActivity mActivity) {
        try {
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    ListView listView = ((AlertDialog) dialogInterface).getListView();
                    final ListAdapter originalAdapter = listView.getAdapter();

                    listView.setAdapter(new ListAdapter() {
                        @Override
                        public int getCount() {
                            return originalAdapter.getCount();
                        }

                        @Override
                        public Object getItem(int id) {
                            return originalAdapter.getItem(id);
                        }

                        @Override
                        public long getItemId(int id) {
                            return originalAdapter.getItemId(id);
                        }

                        @Override
                        public int getItemViewType(int id) {
                            return originalAdapter.getItemViewType(id);
                        }

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = originalAdapter.getView(position, convertView, parent);
                            TextView textView = (TextView) view;
                            // textView.setTextSize(context.getResources().getDimension(R.dimen.text_size_14));
                            textView.setTextSize(24 + (PreferenceHandler.getCurrrentFontSzForApp(mActivity))); //YD set text size programmatically if needed
                            textView.setTextColor(context.getResources().getColor(R.color.light_gray));
                            int paddingLft = 20;

                            //YD increasing the padding of the text in row if the resolution is high
                            DisplayMetrics dm = new DisplayMetrics();
                            mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
                            int dens = dm.densityDpi;
                            if (dens > DisplayMetrics.DENSITY_XHIGH)
                                paddingLft = 60;

                            textView.setPadding(paddingLft, 8, 10, 8);

                            // textView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 50 /* this is item height */));
                            return view;
                        }

                        @Override
                        public int getViewTypeCount() {
                            return originalAdapter.getViewTypeCount();
                        }

                        @Override
                        public boolean hasStableIds() {
                            return originalAdapter.hasStableIds();
                        }

                        @Override
                        public boolean isEmpty() {
                            return originalAdapter.isEmpty();
                        }


                        @Override
                        public boolean areAllItemsEnabled() {
                            return originalAdapter.areAllItemsEnabled();
                        }

                        @Override
                        public boolean isEnabled(int position) {
                            return originalAdapter.isEnabled(position);
                        }

                        @Override
                        public void registerDataSetObserver(DataSetObserver observer) {
                            // TODO Auto-generated method stub
                            originalAdapter.registerDataSetObserver(observer);
                        }

                        @Override
                        public void unregisterDataSetObserver(DataSetObserver observer) {
                            // TODO Auto-generated method stub
                            originalAdapter.unregisterDataSetObserver(observer);
                        }

                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getTitleBarHeight(Context cn) {
        int viewTop = ((Activity) cn).getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        return viewTop;
    }

    public static PopupWindow show_Map_popupmenu(Context cn, int height) {
        WindowManager wm = (WindowManager) cn.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        LayoutInflater layoutInflater = (LayoutInflater) cn.getSystemService(cn.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popupmenu_map, null);
        TypeFaceFont.overrideFontsMenu(cn, view);
        PopupWindow pwindo = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);//YD IMPORTANTt
        pwindo.setFocusable(true);
        pwindo.setBackgroundDrawable(new BitmapDrawable());
        pwindo.showAtLocation(view, Gravity.NO_GRAVITY, 20 + (display.getWidth() / 2), getTitleBarHeight(cn) + height);

        return pwindo;

    }

    public static void setMaxlengthET(EditText mEditText, int limit) {

        InputFilter[] lengthFilter = new InputFilter[1];
        lengthFilter[0] = new InputFilter.LengthFilter(limit); //Filter to 9 characters
        mEditText.setFilters(lengthFilter);
    }

    public static int getDialogTextSize(Context mActivity) {
		/*if ( PreferenceHandler.getCurrrentFontSzForApp(mActivity) == -6) {
			return R.style.DialogTheme6Neg;
		}
		else*/
        if (PreferenceHandler.getCurrrentFontSzForApp(mActivity) == -10) {
            return R.style.DialogTheme4Neg;
        } else if (PreferenceHandler.getCurrrentFontSzForApp(mActivity) == -5) {
            return R.style.DialogTheme2Neg;
        } else if (PreferenceHandler.getCurrrentFontSzForApp(mActivity) == 0) {
            return R.style.DialogTheme0;
        } else if (PreferenceHandler.getCurrrentFontSzForApp(mActivity) == 5) {
            return R.style.DialogTheme2Pos;
        } else if (PreferenceHandler.getCurrrentFontSzForApp(mActivity) == 10) {
            return R.style.DialogTheme4Pos;
        }
		/*else if (PreferenceHandler.getCurrrentFontSzForApp(mActivity) == 6) {
			return R.style.DialogTheme6Pos;
		}*/
        return 0;
    }

    public static void getTimeRange(final Context cn, final int timetype, final TextView view, final TextView otherview, final CustomListner clickListener) {
        int temphour = 0;
        int hour = 0;
        int minute = 0;
        String time = view.getText().toString().trim();
        final Calendar mcurrentTime = Calendar.getInstance();
        mcurrentTime.setTime((Date) view.getTag());
        if (!time.isEmpty()) {
            if (time.split(":")[1].split(" ")[1].equalsIgnoreCase("am")) {
                temphour = Integer.valueOf(time.split(":")[0]);
            } else {
                temphour = Integer.valueOf(time.split(":")[0]) + 12;
            }

            hour = temphour;
            minute = Integer.valueOf(time.split(":")[1].split(" ")[0]);
        }
        int sizeDialogStyleID = Utilities.getDialogTextSize(cn);

        CustomTimePickerDialog dialog = new CustomTimePickerDialog(cn, new CustomTimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                // TODO Auto-generated method stub
                String am_pm = "";
                mcurrentTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                mcurrentTime.set(Calendar.MINUTE, selectedMinute);
                mcurrentTime.set(Calendar.SECOND, 0);

                if (mcurrentTime.get(Calendar.AM_PM) == Calendar.AM)
                    am_pm = "am";
                else if (mcurrentTime.get(Calendar.AM_PM) == Calendar.PM)
                    am_pm = "pm";

                String strHrsToShow = (mcurrentTime.get(Calendar.HOUR) == 0) ? "12"
                        : mcurrentTime.get(Calendar.HOUR) + "";

                String strMinToShow = String.valueOf(mcurrentTime.get(Calendar.MINUTE));

                int mHour = Integer.parseInt(strHrsToShow);
                int mMin = mcurrentTime.get(Calendar.MINUTE);
                if (mHour < 10)
                    strHrsToShow = "0" + strHrsToShow;

                if (mMin < 10)
                    strMinToShow = "0" + strMinToShow;

                //	if() {
                view.setText(strHrsToShow + ":"
                        + strMinToShow + " "
                        + am_pm);
                view.setTag(mcurrentTime.getTime());

                if (clickListener != null)
                    clickListener.onClick(view);

                Log.e("time", mcurrentTime.getTime().toString());
                //	}

            }

        }, hour, minute, false, sizeDialogStyleID);

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", dialog);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", dialog);

        dialog.setTitle("Set Time");
        dialog.show();

        Utilities.setDividerTitleColor(dialog, 0, cn);
        Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Utilities.setDefaultFont_12(button_negative);
        Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Utilities.setDefaultFont_12(button_positive);

    }

    public static int[] gettimes(String str) {
        int arrtime[] = new int[4];
        String hrarr[];
        if (str.contains("|")) {
            hrarr = str.split(Pattern.quote("|"));
        } else {
            hrarr = str.split(Pattern.quote(","));
        }
        int time = Integer.parseInt(hrarr[0]);
        int min = time % 60;
        int hr = (time - min) / 60;
        arrtime[0] = hr;
        arrtime[1] = min;

        time = Integer.parseInt(hrarr[1]);
        min = time % 60;
        hr = (time - min) / 60;
        arrtime[2] = hr;
        arrtime[3] = min;
        return arrtime;
    }

    public static void noPatterns(Context cn) {
        final MyDialog dialog = new MyDialog(cn, "No Pattern Found", "No Pattern is found, Please match selected Dates and Days", "OK");
        dialog.setkeyListender(new MyDiologInterface() {

            @Override
            public void onPositiveClick() throws JSONException {
                // on No button click
                dialog.dismiss();
            }

            @Override
            public void onNegativeClick() {
                // on Yes button click
                dialog.dismiss();
            }
        });
        dialog.onCreate(null);
        dialog.show();
        return;
    }

    public static Date shiftLockDate(Context mActivity) {
        final Calendar c = Calendar.getInstance();
        c.setTime(yesterday());
        shftlock = PreferenceHandler.getShiftLock(mActivity);
        if (shftlock != null)
            c.add(Calendar.DATE, Integer.parseInt(shftlock));
        else
            c.add(Calendar.DATE, 0);
        return c.getTime();
    }

    public static Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

   /*
   For job scheduler...

   public static void scheduleJob(Context context) {
            ComponentName serviceComponent = new ComponentName(context, AceRouteJobService.class);
            JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
            builder.setOverrideDeadline(0); // maximum delay
            JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
            jobScheduler.schedule(builder.build());
        }

        */

    @Override
    public void onLocationChanged(Location location) {
        try {//YD 2020 this is working.
            //Toast.makeText(context, "###U : location changed :" + location.getLatitude() + "-" + location.getLongitude(), Toast.LENGTH_LONG).show();
            Log.i(Utilities.TAG, "###U : location changed : " + location.getLatitude() + "-" + location.getLongitude());
            //Logger.i("AceRoute Geo" , "###U : location changed : "+ location.getLatitude() + "-" + location.getLongitude());

            String previous = PreferenceHandler.getTempLastLocation(context);
            if (previous != null) {
                Utilities.log(context, "My new Location" + location);
                getDifferencebetweenLatLng(previous, location);
            } else {
                Utilities.log(context, "My first Location" + location);
                PreferenceHandler.setTempLastLocation(context, location.getLatitude() + "," + location.getLongitude());
            }

//            Toast.makeText(context, "onLocationChanged in utils" + String.valueOf(location.getLatitude()) + "-" + String.valueOf(location.getLongitude()), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDifferencebetweenLatLng(String previous, Location location) {
        //Logger.i("AceRoute Geo" , "getDifferencebetweenLatLng for prev and next. ");
        Double prevLat = null, prevLang = null, nextLat = null, nxtLang = null;
        if (previous != null) {
            String newLocs[] = previous.split(",");
            prevLat = Double.parseDouble(newLocs[0]);
            prevLang = Double.parseDouble(newLocs[1]);
        }
        if (location != null) {
            nextLat = location.getLatitude();
            nxtLang = location.getLongitude();
        }

        float distanceinmtrs = (float) distance(prevLat, prevLang, nextLat, nxtLang);
        String locinMtrs = PreferenceHandler.getPrefLocchan(context);
        if (locinMtrs != null)
            if (distanceinmtrs > Float.parseFloat(locinMtrs)) {
                PreferenceHandler.setTempLastLocation(context, location.getLatitude() + "," + location.getLongitude());
                saveGeoForGeoSync(location, context);
            }
        //Logger.i("AceRoute Geo" , "FirstPoint : "+ prevLat+" , "+ prevLang + "  SecondPoint : "+nextLat+" , "+ nxtLang);
        //Logger.i("AceRoute Geo" , "Distance in Metres between two points are : "+ distanceinmtrs + "  and min distance require to save is : "+locinMtrs);
        //Utilities.log(context, "Distance in Metres" + distanceinmtrs + "");

    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private void saveGeoForGeoSync(Location location, Context context) {
        Log.i(Utilities.TAG, "### : location changed : " + location.getLatitude() + "-" + location.getLongitude());
        GeoSyncAlarmRequest geoSyncAlarmreq = new GeoSyncAlarmRequest();
        geoSyncAlarmreq.setAction(Api.API_ACTION_SAVE_RES_GEO);
        geoSyncAlarmreq.setData(location.getLatitude() + "," + location.getLongitude());
        sendReqToRequestHandlerWithNoCB(geoSyncAlarmreq, context);
    }

    private void sendReqToRequestHandlerWithNoCB(RequestObject reqObj, Context context) {

        AceRequestHandler requestHandler = null;
        Intent intent = null;
        int id = Utilities.DEFAULT_NOCALLBACK_REQUEST_ID;

        requestHandler = new AceRequestHandler(context);
        intent = new Intent(context, AceRouteService.class);

        Long currentMilli = PreferenceHandler.getPrefQueueRequestId(context);

        Bundle mBundle = new Bundle();
        mBundle.putParcelable("OBJECT", reqObj);
        mBundle.putLong(AceRouteService.KEY_TIME, currentMilli);
        mBundle.putInt(AceRouteService.KEY_SYNCALL_FLAG, AceRouteService.VALUE_NOT_SYNCALL);
        mBundle.putInt(AceRouteService.FLAG_FOR_CAMERA, 0);
        mBundle.putInt(AceRouteService.KEY_ID, id);
        mBundle.putString(AceRouteService.KEY_ACTION, Api.API_ACTION_SAVE_RES_GEO);

        intent.putExtras(mBundle);
        context.startService(intent);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }
}