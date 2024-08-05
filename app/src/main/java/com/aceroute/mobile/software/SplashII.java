package com.aceroute.mobile.software;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.database.DBEngine;
import com.aceroute.mobile.software.database.DBHandler;
import com.aceroute.mobile.software.dialog.CustomStatusDialog;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.StatusDiologInterface;
import com.aceroute.mobile.software.http.HttpConnection;
import com.aceroute.mobile.software.http.RequestObject;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.network.AceRequestHandler;
import com.aceroute.mobile.software.requests.GetOrdersRO;
import com.aceroute.mobile.software.requests.SyncRO;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.UpdateLocationService;
import com.aceroute.mobile.software.utilities.Utilities;
import com.aceroute.mobile.software.utilities.XMLHandler;
import com.bugsnag.android.Bugsnag;
import com.splunk.mint.Mint;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class SplashII extends Activity implements RespCBandServST {
    public static final int GET_ORDER_REQ = 1;
    public static final int SYNC_OFFLINE_DATA = 2;
    public static final int LOAD_REF_DATA = 3;
    public static final String MESSAGE_SENDUPDATE_TO_UI = "progressupdate";
    public static final String MESSAGE_SENDERRORUPDATE_TO_UI = "errorupdate";
    public static final int REQUEST_CODE_SHOW_PICK_IMAGE_OPTIONS = 11;
    static boolean shouldFinish = false;
    private static final int PERMISSION_REQUEST_CODE = 200;
    MyDialog dialog = null;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    public static int numberOfDaysForwardAndBackward = 0;
    public static boolean shouldShowProgress = false;
    public static int wrk_tid = 5;
    public static boolean pubnublogout = false;
    boolean overlayAccepted, locationAccepted, WriteStorageAccpeted, cameraAccepted;
    CustomStatusDialog customDialog;
    ArrayList<String> permmisionlist = new ArrayList<>();
    boolean isLoginRequired = false;

    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
            Log.e("TAG :", "Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride);
        }
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            } else if (!locationAccepted) {
                overlayAccepted = true;
                requestPermission();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bugsnag.start(this);
        Mint.initAndStartSession(this.getApplication(), "3eb08b6d");
        //Fabric.with(this, new Crashlytics());//YD 2020
        PreferenceHandler.tcode = null;
        overrideFont(getApplicationContext(), "serif", "fonts/lato-regular-webfont.ttf"); // font from assets:
        pubnublogout = false;
        setContentView(R.layout.activity_splash_ii);
        PackageManager manager = getApplicationContext().getPackageManager();
        PackageInfo info = null;

        try {
            info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }

        Log.d("nnnnnnnnn", "sccccccccccc");
        int appVersion = Integer.valueOf(info.versionCode);

        Log.d("appversioin", "" + appVersion);

        if (PreferenceHandler.getPrefVersion(this) < appVersion) {
            Log.d("sharedversion", "abc" + PreferenceHandler.getPrefVersion(this));
            isLoginRequired = true;
            DBHandler.deleteDatabase(this);
            PreferenceHandler.setPrefVersion(this, appVersion);
            Log.d("newsharedversion", "" + PreferenceHandler.getPrefVersion(this));

            try {
                File dir = new File(Environment
                        .getExternalStorageDirectory()
                        .getAbsolutePath()
                        + "/AceRoute");

                if (dir.isDirectory()) {
                    String[] children = dir.list();
                    for (int i = 0; i < children.length; i++) {
                        new File(dir, children[i]).delete();
                    }
                }


                if (dir.list().length == 0) {
                    dir.delete();
                }
            } catch (Exception e) {

            }

            resetData();

            // checkVersionForApp();

            Log.d("sharedpreversion", "" + PreferenceHandler.getPrefVersion(this));

        }

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        if (PreferenceHandler.getlastsynctime(this) == 0 && width >= 2560 && height >= 1440) {
            Log.d("vvvvvvvv", "sccccccccccc");
            PreferenceHandler.setCurrrentFontSzForApp(this, -5);
        }

        if (PreferenceHandler.getIsCalledFromLoginPage(this) != null &&
                PreferenceHandler.getIsCalledFromLoginPage(this).equals("true")) {

            Log.d("checkkk", "sccccccccccc");
            PreferenceHandler.setIsCalledFromLoginPage(this, "false");
            PreferenceHandler.setIsNewLogin(this, true);
            startSycningData();
            logUser();

        } else if (Build.VERSION.SDK_INT < 23) {
            CheckForAppHomePage();
        } else {

            showNeedPermission();

        }
    }

    private void checkVersionForApp() {
        Asyncc asyncc = new Asyncc();
        asyncc.execute();
    }

    public void resetData() {
        stopService(new Intent(this, UpdateLocationService.class));
        DBEngine.sendLogoutrequest(this.getApplicationContext());
        stopService(new Intent(this, AceRouteService.class));
//        PreferenceHandler.setCompanyId(this.getApplicationContext(), null);
//        PreferenceHandler.setMtoken(this.getApplicationContext(), null);
//        PreferenceHandler.setResId(this.getApplicationContext(), 0L);
        PreferenceHandler.setIsUserLoggedIn(this, false);
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
        settings.edit().clear().commit();
        Utilities.stopAlarmManager(this); //YD 2020


    }

    private void showNeedPermission() {
        final boolean isLogin = PreferenceHandler.getIsUserLoggedIn(this);
        AlertDialog alertDialog = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT).create();
        alertDialog.setMessage("This App collects Location data to enable Schedule Optimization, Assign Work by Proximity, and send ETA Notifications even when the app is closed or in background. To disable Location tracking, please click “Clockout” within the App or Logout of the App. No Location data will be captured in above two scenarios.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> {
                    if (!PreferenceHandler.getIsNewLogin(getApplicationContext())) {
                        if (!checkLocationPermission()) {
                            if (isLogin) {
                                startSycningData();
                            } else {
                                checkPermission();
                            }

                        }
           /* else {
         .       if(isLoginRequired){
                    CheckForAppHomePage();
                }
            }*/
                    } else {
                        CheckForAppHomePage();
                    }

                    dialog.dismiss();
                });
        alertDialog.show();

    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkLocationPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_BACKGROUND_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        try {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA, CALL_PHONE},
                    PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            Log.d("exception ", e.toString());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            permmisionlist.add(permissions[i]);

                        }
                    }

                    if (permmisionlist.contains("android.permission.ACCESS_FINE_LOCATION"))
                        locationAccepted = true;
                    if (permmisionlist.contains("android.permission.CAMERA"))
                        cameraAccepted = true;
                    if (permmisionlist.contains("android.permission.WRITE_EXTERNAL_STORAGE"))
                        WriteStorageAccpeted = true;
                    if (permmisionlist.contains("android.permission.CALL_PHONE"))
                        WriteStorageAccpeted = true;
                    if (permmisionlist.contains("android.permission.ACCESS_BACKGROUND_LOCATION"))
                        WriteStorageAccpeted = true;


//                    locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    WriteStorageAccpeted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//                    cameraAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                            showMessageOKCancel("AceRoute requires Location Tracking to be enabled to handle optimal scheduling. Please select \"Allow All The Time\" option to continue using AceRoute",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, Settings.ACTION_MANAGE_OVERLAY_PERMISSION},
                                                            PERMISSION_REQUEST_CODE);

                                                }
                                            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                                                closeNow();
                                            }

                                        }
                                    });

                            return;

                        } else if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {

                            showMessageOKCancel("Please enable access, this feature is required by AceRoute App.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);

                                                }
                                            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                                                closeNow();
                                            }

                                        }
                                    });
                            return;

                        } else if (shouldShowRequestPermissionRationale(CAMERA)) {
                            showMessageOKCancel("Please enable access, this feature is required by AceRoute App.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            PERMISSION_REQUEST_CODE);

                                                }
                                            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                                                closeNow();
                                            }

                                        }
                                    });
                            return;
                        } else if (shouldShowRequestPermissionRationale(CALL_PHONE)) {
                            showMessageOKCancel("Please enable access, this feature is required by AceRoute App.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CALL_PHONE},
                                                            PERMISSION_REQUEST_CODE);

                                                }
                                            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                                                closeNow();
                                            }

                                        }
                                    });
                            return;
                        } /*else if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                            showMessageOKCancel("AceRoute requires Location Tracking to be enabled to handle optimal scheduling. Please select \"Allow All The Time\" option to continue using AceRoute",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                                            PERMISSION_REQUEST_CODE);

                                                  //  new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION};

                                                }
                                            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                                                closeNow();
                                            }

                                        }
                                    });

                            return;
                        }*/ else {
                            Log.d("location", "Permission");
                            if (ActivityCompat.checkSelfPermission(SplashII.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SplashII.this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(SplashII.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                                return;
                            }

                            if (ActivityCompat.checkSelfPermission(SplashII.this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(SplashII.this, new String[]{CAMERA}, 1);
                                return;
                            }

//                            if (ActivityCompat.checkSelfPermission(SplashII.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                                ActivityCompat.requestPermissions(SplashII.this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
//                                return;
//                            }

                            if (ActivityCompat.checkSelfPermission(SplashII.this, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(SplashII.this, new String[]{CALL_PHONE}, 1);
                                return;

                            }

//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            if (ActivityCompat.checkSelfPermission(SplashII.this, ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                ActivityCompat.requestPermissions(SplashII.this, new String[]{ACCESS_BACKGROUND_LOCATION}, 1);
//                                return;
//                            }
//                            }
                            else {
                                // Write you code here if permission already given.
                                /*if(isLoginRequired){
                                    CheckForAppHomePage();
                                }
                                else*/
                                if (overlayAccepted && locationAccepted && cameraAccepted && WriteStorageAccpeted)
                                    CheckForAppHomePage();
                                else {
                                    Log.d("Apllication", "exit");
                                    closeNow();
                                }
                            }

                            break;
                        }
                    }
                }
        }
    }

    private void closeNow() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            finish();
        }

    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(Html.fromHtml("<font color='#00C29D'>Permission</font>"))
                .setMessage(message)
                .setPositiveButton(Html.fromHtml("<font color='#00C29D'>OK</font>"), okListener)
                .setNegativeButton(Html.fromHtml("<font color='#00C29D'>CANCEL</font>"), okListener);
        //.create()
        //.show();
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Utilities.setDividerTitleColor(alertDialog, 500, this);
        Button button_positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button button_negtaive = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Utilities.setDefaultFont_12(button_negtaive);
        Utilities.setDefaultFont_12(button_positive);

    }

    /*public boolean checkLocationPermission() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)) new AlertDialog.Builder(this)
                    .setTitle("Location Permission")
//                    .setMessage(R.string.text_location_permission)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            requestPermissions(
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        }
                    })
                    .create()
                    .show();
            else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] ==
                         PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (checkSelfPermission(
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        initService();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }*/

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("permisssion ", "Grenated");
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // You don't have permission
                showMessageOKCancel("Please Provide the Overlay Permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            dialog.dismiss();
                            checkPermission();
                        }
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            finish();
                        }
                    }
                });
            } else {
                overlayAccepted = resultCode == PackageManager.PERMISSION_GRANTED;
                requestPermission();
            }

        }
    }

    private void logUser() {
        /*Crashlytics.setUserIdentifier(String.valueOf(PreferenceHandler.getResId(this)));//worker id
        Crashlytics.setUserEmail(PreferenceHandler.getCompanyId(this));// account
        Crashlytics.log(1, "FabricDemo", "Time : " + Utilities.getCurrentTime());*///YD 2020
    }

    private void CheckForAppHomePage() {
        try {
            boolean isUserLoggedIn = PreferenceHandler.getIsUserLoggedIn(this);
            if (isUserLoggedIn) {
                //Logger.enableLogs(getApplicationContext(), true);//YD also remove code for delete the logs from aceofflineSyncEngine
                //showNeedPermission();

            } else {
                Intent intent = new Intent(SplashII.this, AppLoginPage.class);
                startActivity(intent);
                finish();


            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(Utilities.TAG, "jsinterface : setUserData : " + false);
        }
    }

    public void startSycningData() {
        if (PreferenceHandler.getLastPubnubUpdated(getApplicationContext()) == 0)
            PreferenceHandler.setLastPubnubUpdated(getApplicationContext(), Utilities.getCurrentTime());// settting for pubnub
        new Thread() {
            public void run() {
                initService();
            }
        }.start();
        Log.i("AceRoute", ".");
        if (PreferenceHandler.getAppType(this).equals("Offline")) {
            if (PreferenceHandler.getlastsynctime(this) == 0) {
                openSyncDialog(); //YD
                SyncRO syncObj = new SyncRO();
                syncObj.setType("full");
                syncOfflineData(syncObj, SYNC_OFFLINE_DATA);
            } else {
                CommonServManager comMan = new CommonServManager(this, this, this);
                comMan.loadRefData("localonly", LOAD_REF_DATA);
            }
        }
    }

    private void openSyncDialog() {

        customDialog = new CustomStatusDialog(this, "Syncing Data", "OK");
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setkeyListender(new StatusDiologInterface() {

            @Override
            public void onPositiveClick(String btnTxt) throws JSONException {
                // TODO Auto-generated method stub

            }

            @Override
            public void onNegativeClick(String btnTxt) {
                // TODO Auto-generated method stub
            }
        });
        customDialog.onCreate(null);

        customDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(customDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        customDialog.getWindow().setAttributes(lp);
        customDialog.setHeightPopup();
    }

    public void initService() {
        Log.i(Utilities.TAG, "jsinterface init service called.");
        Context context = this.getApplicationContext();
        Intent intent = new Intent(context,
                AceRouteService.class);
        intent.setAction(AceRouteService.ACTION_CONN_PUBNUB);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, intent);
        } else {
            context.startService(intent);
        }
        PreferenceHandler.setgeoFlagFirst(this.getApplicationContext(), true);
        PreferenceHandler.setAlarmState(this.getApplicationContext(), true);
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

    private void loadOrdersAtBoot() {
        Date today = new Date();// later use to set header
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
        if (response != null) {

            if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED))) {
                if (response.getId() == GET_ORDER_REQ) {
                    DataObject.ordersXmlDataStore = response.getResponseMap();


//                    Log.d("TAG1122","JJ"+((HashMap<Long, Worker>) DataObject.resourceXmlDataStore).get(PreferenceHandler.getResId(this)).getNm());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gotomainpage();
                        }
                    });
                }


                if (response.getId() == LOAD_REF_DATA) {
                    Log.d("hhhhhhhhhhhh", "bgf" + response.getStatus());
                    checkBootflow();
                }

                if (response.getId() == SYNC_OFFLINE_DATA) {

                    Log.d("\ffffffgffffffff", "bgf" + response.getStatus());
                    // Toast.makeText(this, "rrrrrrrrrrrrr", Toast.LENGTH_SHORT);
                    CommonServManager comMan = new CommonServManager(this, this, this);
                    comMan.loadRefData("localonly", LOAD_REF_DATA);
                    PreferenceHandler.setlastsynctime(getApplicationContext(), Utilities.getCurrentTime());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (customDialog != null)
                                customDialog.showHideView();
                        }
                    });
                }


            } else if (response.getStatus().equals("failure")) {


                if (response.getId() == SYNC_OFFLINE_DATA) {
                    Log.d("ffffffffffff", "abc" + response.getStatus());
                    if (response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_INTERNET_CONNECTION))) {
                        Toast.makeText(this, "Internet Connection Lost", Toast.LENGTH_SHORT);
                        //YD TODO should show close on sync popup
                    }
                    if (response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.HTTP_REQUEST_FAILED))) {
                        SplashII.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashII.this, AppLoginPage.class);
                                startActivity(intent);
                                SplashII.this.finish();
                            }
                        });

                    }
                }
                if (response.getId() == GET_ORDER_REQ) {
                    if (response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA))) {
                        DataObject.ordersXmlDataStore = response.getResponseMap();
                        Log.d("orderdaat", "xml" + DataObject.orderFormsXmlStore);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gotomainpage();
                            }
                        });


                    }
                }

                if (response.getId() == LOAD_REF_DATA) {
                    BlockingQueue<RequestObject> messageQueue = AceRouteApplication.getInstance().getMessageQueue();
                    messageQueue.clear(); //YD Clearing msgqueue because have to stop any other ongoing request

                    PreferenceHandler.setRemember(getApplicationContext(), false);
                    Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    editor.clear();
                    editor.commit();

                    Intent intent = new Intent(this, AppLoginPage.class);
                    startActivity(intent);
                    this.finish();
                }
            }
        }
    }

    private void checkBootflow() {
        loadOrdersAtBoot();
        BaseTabActivity.refreshOrdersListfromCache = true;
    }

    private void gotomainpage() {
        Intent intent = new Intent(this, BaseTabActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customDialog != null)
            customDialog = null;
    }

    @Override
    protected void onPause() {
        if (customDialog != null && customDialog.isShowing())
            customDialog.dismiss();
        super.onPause();

    }

    class Asyncc extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {
            Map<String, String> getOrderParams = new HashMap<String, String>();
            getOrderParams.put("action", "getmversion");
            getOrderParams.put("tid", "apk");
            String response = null;
            try {
                response = HttpConnection.get(getApplicationContext(),
                        "https://" + PreferenceHandler.getPrefBaseUrl(getApplicationContext()) + "/mobi", getOrderParams);//<data><success>true</success><id>2.3</id></data>
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("Exc", "" + e);
                return XMLHandler.XML_DATA_ERROR;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (response != null) {
                Utilities.log(getApplicationContext(), "server response : " + response);
                XMLHandler xmlhandler = new XMLHandler(getApplicationContext());
                Document doc = xmlhandler.getDomElement(response);

                NodeList nl = doc
                        .getElementsByTagName(XMLHandler.KEY_DATA);
                Element e = (Element) nl.item(0);
                String success = xmlhandler.getValue(e,
                        XMLHandler.KEY_DATA_SUCCESS);

                if (success.equals(XMLHandler.KEY_DATA_RESP_FAIL) || response.contains("MobiLoginAgain")) {
                    versionerror();
                } else {
                    String serverVersion = xmlhandler.getValue(e,
                            XMLHandler.KEY_DATA_ID);
                    double serverVersionNum = Double.valueOf(serverVersion);

                    Log.d("serialno", "" + serverVersionNum);

                    PackageManager manager = getApplicationContext().getPackageManager();
                    PackageInfo info = null;

                    try {
                        info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e1) {
                        e1.printStackTrace();
                    }
                    String appVersion = info.versionName;
                    double appVersionNum = Double.valueOf(appVersion);

                    Log.d("sercerversion", "" + serverVersionNum);
                    if (appVersionNum >= serverVersionNum) {
                        PreferenceHandler.setlastsynctime(SplashII.this, 0);
                        startApp();
                    } else {
                        shouldFinish = true;
                        showDialogForVersion();
                    }
                }
            } else {
                versionerror();
            }
        }
    }

    private void startApp() {
        //startAlarmManager(); //YD 2020
        Log.i("software", "Success Repsonse for login");
        PreferenceHandler.setIsCalledFromLoginPage(this, "true");
        Intent intent = new Intent(SplashII.this, SplashII.class);
        startActivity(intent);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //        alertDialog.dismiss();
                //mWebView.destroy();
            }
        });
        finish();
    }

    private void versionerror() {
        //  PreferenceHandler.setCompanyId(AppLoginPage.this.getApplicationContext(), null);
        PreferenceHandler.setMtoken(SplashII.this.getApplicationContext(), null);
        //PreferenceHandler.setResId(AppLoginPage.this.getApplicationContext(), 0L);
        PreferenceHandler.setRemember(SplashII.this.getApplicationContext(), false);
        PreferenceHandler.setlastsynctime(SplashII.this, 0);
        PreferenceHandler.setOptionSelectedForImg(SplashII.this, null);
        PreferenceHandler.setCurrrentFontSzForApp(SplashII.this, 0);

        PreferenceHandler.setOdrGetDate(SplashII.this, null);
        PreferenceHandler.setodrLastSyncForDt(SplashII.this, 0);
        // activelogin = 0;
        //  alertDialog.dismiss();
        //mWebView.destroy();
        PreferenceHandler.setRemember(getApplicationContext(), false);
//        showMessageDialog("Contact your administrator", "Something is not quite right");

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
                        // PreferenceHandler.setRemember(getApplicationContext(), false);
                        Log.d("gotolink", "" + "gooo");

                        finishAffinity();
                        dialog.dismiss();

                    } else
                        startApp();
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

}

