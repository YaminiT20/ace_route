package com.aceroute.mobile.software;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Api;
import com.aceroute.mobile.software.http.HttpConnection;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.mangers.UserManager;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.aceroute.mobile.software.utilities.XMLHandler;
import com.aceroute.mobile.software.validation.ValidationEngine;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AppLoginPage extends Activity implements android.view.View.OnClickListener, RespCBandServST {

    private static final int SYNC_LOCATION_JOB_ID = 123;
//    private static final long LOCATION_TRACKING_TIME = 1000*60*5;
    private static long LOCATION_TRACKING_TIME = 1000*60*5;
     int isRememberUserPassCodeEnabled = 1;
    static boolean shouldFinish = false;
    ValidationEngine validation;
    ToggleButton toggle_rememberMe;
    CheckBox toggle_showpcode;
    Button submit;
    EditText companyId, resId, pCode;
    LinearLayout parentView;
    int activeReqId = 0;
    int activelogin = 0;
    MyDialog dialog = null;
    private WebView mWebView;
    private Dialog alertDialog;

    @Override
    protected void onResume() {
        super.onResume();
        activelogin = 0;
        if (PreferenceHandler.getMOBILOGINAGAIN(this) && !PreferenceHandler.getDialogMOBILOGINAGAIN(this)) {
            PreferenceHandler.setDialogMOBILOGINAGAIN(AppLoginPage.this, true);
            PreferenceHandler.setMOBILOGINAGAIN(this, false);
//            showMessageDialog("Contact your administrator", "Something is not quite right");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        BaseTabActivity.logout = true;
        setContentView(R.layout.apploginpage);
        validation = ValidationEngine.getInstance(AppLoginPage.this);
        validation.initValidation("AppLoginPage");
        PreferenceHandler.tcode = null;
        parentView = (LinearLayout) findViewById(R.id.parentView);
        LOCATION_TRACKING_TIME=PreferenceHandler.getPrefGpsSync(this);
        companyId = (EditText) findViewById(R.id.companyId_Et);
        //companyId.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE); //YD causing next button to get hide
        Typeface tf = TypeFaceFont.getCustomTypefaceSan_sarif(AppLoginPage.this);
        companyId.setTypeface(tf, Typeface.NORMAL);
        companyId.requestFocus();
        resId = (EditText) findViewById(R.id.resId_Et);
        resId.setTypeface(tf, Typeface.NORMAL);
        pCode = (EditText) findViewById(R.id.passCode_Et);
        pCode.setTypeface(tf, Typeface.NORMAL);
        submit = (Button) findViewById(R.id.login_submit);
        submit.setTypeface(tf, Typeface.BOLD);
        toggle_rememberMe = (ToggleButton) findViewById(R.id.toggle_rememberMe);
        toggle_showpcode = (CheckBox) findViewById(R.id.toggle_showpcode);

        toggle_rememberMe.setTypeface(tf, Typeface.NORMAL);
        toggle_showpcode.setTypeface(tf, Typeface.NORMAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(getApplicationContext())) {
                Settings.System.putInt(this.getContentResolver(), Settings.System.TEXT_SHOW_PASSWORD, 1);
            } else {

            }
        } else {
            Settings.System.putInt(this.getContentResolver(), Settings.System.TEXT_SHOW_PASSWORD, 1);
        }

       /* if (PreferenceHandler.getRemember(getApplicationContext()) == true) {
            companyId.setText(PreferenceHandler.getCompanyId(getApplicationContext()));
            resId.setText(PreferenceHandler.getResId(getApplicationContext()) + "");
   //         pCode.setText(PreferenceHandler.getPassCode(getApplicationContext()));
        } else {*/
            companyId.setText("");
            resId.setText("");
            pCode.setText("");
        //}
        toggle_showpcode.setChecked(false);
        submit.setOnClickListener(this);
        toggle_rememberMe.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                Log.e("Status: ", String.valueOf(isChecked));
                if (isChecked == true)
                    isRememberUserPassCodeEnabled = 1;
                else
                    isRememberUserPassCodeEnabled = 0;
            }
        });

        toggle_showpcode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                int start, end;
                if (!isChecked) {
                    start = pCode.getSelectionStart();
                    end = pCode.getSelectionEnd();
                    pCode.setTransformationMethod(new PasswordTransformationMethod());
                    pCode.setSelection(start, end);
                } else {
                    start = pCode.getSelectionStart();
                    end = pCode.getSelectionEnd();
                    pCode.setTransformationMethod(null);
                    pCode.setSelection(start, end);
                }
            }
        });

        parentView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (getCurrentFocus() != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return false;
            }
        });
       //checkVersionForApp();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub\

        switch (v.getId()) {
            case R.id.login_submit:
                PreferenceHandler.setlastsynctime(this, 0);
                if (companyId.getText().toString().trim().equals("") && resId.getText().toString().trim().equals("") && pCode.getText().toString().trim().equals("")) {
                    showMessageDialog("Account Name, Worker ID, Passcode are required fields to log into your AceRoute Account");
                } else {
                    String Errormsg = null;
                    Errormsg = validation.checkValidation(AppLoginPage.this);
                    if (Errormsg == "" && activelogin == 0) {
                        activelogin = 1;
                        activeReqId = 1;

                        String comId = companyId.getText().toString().trim();
                        comId = comId.toLowerCase();
                        final String rId = resId.getText().toString().trim();
                        //String pssCode = pCode.getText().toString().trim();
                        final String pssCode = pCode.getText().toString().trim();
                        final String appType = "Offline";

                        try {
                            if (!Utilities.checkInternetConnection(this, false)) {
                                showMessageDialog("No Internet Connection");
                            } else {
                                // YD showing loading bar on click of login button
                                mWebView = (WebView) findViewById(R.id.webviewLogin);
                                mWebView.loadUrl("file:///android_asset/loading.html");
                                mWebView.setBackgroundColor(0x00000000);
                                if (Build.VERSION.SDK_INT >= 11)
                                    mWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

                                this.mWebView.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public void onPageFinished(WebView view, String url) {
                                        view.setBackgroundColor(0x00000000);
                                        if (Build.VERSION.SDK_INT >= 11)
                                            view.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
                                    }
                                });
                                mWebView.setVisibility(View.VISIBLE);
                                final UserManager userMn = new UserManager(this, AppLoginPage.this,rId,pssCode,isRememberUserPassCodeEnabled);
                                userMn.hitbase(this, comId, activeReqId, Api.BASE_URL);
                                alertDialog = new Dialog(AppLoginPage.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                alertDialog.setContentView(R.layout.applogin_dialog);
                                alertDialog.setCancelable(false);
                                try {
                                    alertDialog.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                boolean isexe = false;
                                if (isexe) {
                                    alertDialog.dismiss();
                                }
                            }

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(pCode.getWindowToken(), 0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showMessageDialog(Errormsg);
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

    private void showNeedPermission() {
        AlertDialog alertDialog = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT).create();
        alertDialog.setMessage("This App collects Location data to enable Schedule Optimization, Assign Work by Proximity, and send ETA Notifications even when the app is closed or in background. To disable Location tracking, please click “Clockout” within the App or Logout of the App. No Location data will be captured in above two scenarios.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        checkVersionForApp();
                        dialog.dismiss();
                    }
                });

        alertDialog.show();

    }

    @Override
    public void setResponseCBActivity(Response response) {

        if (response.getStatus().equals("success")) {
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showNeedPermission();
                }
            });*/
            checkVersionForApp();
            if (response.getId() == 1) {

            }
        } else if (response.getStatus().equals("failure")) {
            activelogin = 0;
            if (response.getId() == 1) {
                String messageToShow = "";

                if (response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.LOGIN_FAIL))) {
                    messageToShow = "The login information you provided doesn't appear to belong to an existing account. Please check entered data and try again.";


                }
                if (response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_INTERNET_CONNECTION))) {
                    messageToShow = "No Internet Connection.";
                }
                if (response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.ERROR_CODE_RESPONSE_ERROR))) {
                    messageToShow = "The login information you provided doesn't appear to belong to an existing account. Please check entered data and try again.";
                }
                final String msg = messageToShow;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (msg != null && msg != "") {
                            showMessageDialog(msg);
                        }
                        alertDialog.dismiss();
                        mWebView.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    private void showMessageDialog(String strMsg) {
        showMessageDialog(getResources().getString(R.string.msg_slight_problem), strMsg);
    }

    private void showMessageDialog(String title, String strMsg) {
        try {
            String D_title = title;
            String D_desc = strMsg;
            dialog = new MyDialog(AppLoginPage.this, D_title, D_desc, "OK");
            dialog.setkeyListender(new MyDiologInterface() {
                @Override
                public void onPositiveClick() throws JSONException {
                    dialog.dismiss();
                    PreferenceHandler.setDialogMOBILOGINAGAIN(AppLoginPage.this, false);
                }

                @Override
                public void onNegativeClick() {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                    PreferenceHandler.setDialogMOBILOGINAGAIN(AppLoginPage.this, false);
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
                     //  PreferenceHandler.setRemember(getApplicationContext(), false);
                       // finishAffinity();
                        //dialog.dismiss();
                        //WebView mywebview = (WebView) findViewById(R.id.webviewLogin);
                        //mywebview.loadUrl("https://play.google.com/store/apps/details?id=com.aceroute.mobile.software");
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


    private void checkVersionForApp() {
        Asyncc asyncc = new Asyncc();
        asyncc.execute();
    }

    private void versionerror() {
      //  PreferenceHandler.setCompanyId(AppLoginPage.this.getApplicationContext(), null);
        PreferenceHandler.setMtoken(AppLoginPage.this.getApplicationContext(), null);
        //PreferenceHandler.setResId(AppLoginPage.this.getApplicationContext(), 0L);
        PreferenceHandler.setRemember(AppLoginPage.this.getApplicationContext(), false);
        PreferenceHandler.setlastsynctime(AppLoginPage.this, 0);
        PreferenceHandler.setOptionSelectedForImg(AppLoginPage.this, null);
        PreferenceHandler.setCurrrentFontSzForApp(AppLoginPage.this, 0);

        PreferenceHandler.setOdrGetDate(AppLoginPage.this, null);
        PreferenceHandler.setodrLastSyncForDt(AppLoginPage.this, 0);
        activelogin = 0;
        alertDialog.dismiss();
        mWebView.destroy();
        PreferenceHandler.setRemember(getApplicationContext(), false);
//        showMessageDialog("Contact your administrator", "Something is not quite right");

    }

    private void startApp() {
        //startAlarmManager(); //YD 2020
        Log.i("software", "Success Repsonse for login");
        PreferenceHandler.setIsCalledFromLoginPage(this, "true");
        Intent intent = new Intent(AppLoginPage.this, SplashII.class);
        startActivity(intent);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
                mWebView.destroy();
            }
        });
        finish();
    }

   /* private void startAlarmManager() {
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            pendingIntent = PendingIntent.getForegroundService(AppLoginPage.this, SYNC_LOCATION_JOB_ID, new Intent(AppLoginPage.this, UpdateLocationService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getService(AppLoginPage.this, SYNC_LOCATION_JOB_ID, new Intent(AppLoginPage.this, UpdateLocationService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),LOCATION_TRACKING_TIME, pendingIntent);
        }
    }*/


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
                Log.d("Exc",""+e);
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

                    Log.d("serialno",""+serverVersionNum);

                    PackageManager manager = getApplicationContext().getPackageManager();
                    PackageInfo info = null;

                    try {
                        info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e1) {
                        e1.printStackTrace();
                    }
                    String appVersion = info.versionName;
                    double appVersionNum = Double.valueOf(appVersion);

                    Log.d("appversionno",""+serverVersionNum);
                    if (appVersionNum >= serverVersionNum) {
                        PreferenceHandler.setlastsynctime(AppLoginPage.this,0);
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
}
