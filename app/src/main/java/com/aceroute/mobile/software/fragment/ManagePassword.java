package com.aceroute.mobile.software.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.aceroute.mobile.software.AceRouteService;
import com.aceroute.mobile.software.AppLoginPage;
import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.reference.CheckinOut;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.network.AceRequestHandler;
import com.aceroute.mobile.software.requests.ChangePassword;
import com.aceroute.mobile.software.requests.ClockInOutRequest;
import com.aceroute.mobile.software.requests.SyncRO;
import com.aceroute.mobile.software.response.Login;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;

import org.json.JSONException;

/**
 * Created by root on 2/1/18.
 */

public class ManagePassword  extends  BaseFragment implements RespCBandServST, HeaderInterface {

    MyDialog dialog;
    public static final int ReqChangePassword =1;
    public static final int SYNC_OFFLINE_DATA_LOGOUT=2;
    private int CHECKOUT_REQ_LOGOUT=3;
    private static final int LOGOUT_REQ = 4;

    EditText currentPassword ;
    EditText newPassword ;
    EditText retypePassword ;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.manage_password_frag , container, false);
        mActivity.registerHeader(this);
        mActivity.setHeaderTitle("", "SETUP PASSCODE", "");

        currentPassword = (EditText) view.findViewById(R.id.edt_current_pwd_mngp);
        newPassword = (EditText) view.findViewById(R.id.edt_new_pwd_mngp);
        retypePassword = (EditText) view.findViewById(R.id.edt_retype_pwd_mngp);
        return view;
    }


    public void showNoInternetDialog(String title, String content){

		/*String D_title = BaseTabActivity.getResources().getString(R.string.msg_slight_problem);
		String D_desc = getResources().getString(R.string.msg_internet_problem);*/
        dialog = new MyDialog(mActivity,title, content,"OK");
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
        dialog.show();
        Utilities.setDividerTitleColor(dialog, 0 ,  mActivity);
		/*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;

		lp.gravity = Gravity.CENTER;
		dialog.getWindow().setAttributes(lp);*/
    }

    private void showErrorDialog(String message){
        try{
            dialog = new MyDialog(mActivity, mActivity.getResources().getString(R.string.msg_slight_problem),message,"OK");
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
            dialog.show();

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void headerClickListener(String callingId) {

        if(callingId.equals(BaseTabActivity.HeaderDonePressed)) {

            String currentpwd = currentPassword.getText().toString();
            String newpwd = newPassword.getText().toString();
            String retypepwd = retypePassword.getText().toString();

            if(currentpwd!=null && newpwd!=null && retypepwd!=null &&
                 !currentpwd.equals("") && !newpwd.equals("") && !retypepwd.equals("")) {
                if (currentpwd.equals(PreferenceHandler.getPassCode(mActivity))){
                    if (newpwd.equals(retypepwd) ) {
                        if (newpwd.length()>3 ) {
                            if (!newpwd.contains(" ")) {
                                if (!Utilities.checkInternetConnection(mActivity, false)) {
                                    showNoInternetDialog("Unable to Logout", "No internet connection is available to upload updates to server. Please try again to logout when you have stable network connection");
                                } else {
                                    sendRequestChangePassword();
                                }
                            }
                            else{
                                showErrorDialog("Passcode should not contain space");
                            }
                        }else{
                            showErrorDialog("Passcode length should be greater than three characters");
                        }
                    } else {
                        showErrorDialog("New passcode do not match");
                    }
                }
                else{
                    showErrorDialog("Current passcode is not valid");
                }
            }
            else{
                showErrorDialog("All fields are mandatory");
            }

        }
    }

    private void sendRequestChangePassword() {

        ChangePassword changrpwd = new ChangePassword();
        changrpwd.setCurrentPassword(currentPassword.getText().toString());
        changrpwd.setNewPassword(newPassword.getText().toString());
        changrpwd.setRetypePassword(retypePassword.getText().toString());

        AceRequestHandler requestHandler=null;
        Intent intent = null;

        requestHandler = new AceRequestHandler(mActivity);
        intent = new Intent(mActivity,AceRouteService.class);

        Long currentMilli = PreferenceHandler.getPrefQueueRequestId(mActivity);

        Bundle mBundle = new Bundle();
        mBundle.putParcelable("OBJECT", changrpwd);
        mBundle.putLong(AceRouteService.KEY_TIME, currentMilli);
        mBundle.putInt(AceRouteService.KEY_SYNCALL_FLAG, AceRouteService.VALUE_NOT_SYNCALL);
        mBundle.putInt(AceRouteService.FLAG_FOR_CAMERA, 0);
        mBundle.putInt(AceRouteService.KEY_ID, ReqChangePassword);
        mBundle.putString(AceRouteService.KEY_ACTION, Login.CHANGE_PASSWORD);

        intent.putExtras(mBundle);
        requestHandler.ServiceStarterLoc(mActivity, intent, this, currentMilli);
    }

    @Override
    public void ServiceStarter(RespCBandServST activity, Intent intent) {

    }

    @Override
    public void setResponseCallback(String response, Integer reqId) {

    }

    @Override
    public void setResponseCBActivity(Response response) {
        if (response!=null) {
            if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED))) {
                if (response.getId() == ReqChangePassword) {
                    syncDataBeforeLogout("semipartial_logout");
                }
                if (response.getId()==SYNC_OFFLINE_DATA_LOGOUT){
                    //YD sending checkout request at logout

                    final Handler h1 =  new Handler(Looper.getMainLooper());
                    h1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ClockInOutRequest clkInOut = new ClockInOutRequest();
                            clkInOut.setTid("0");
                            clkInOut.setType("post");
                            clkInOut.setAction(CheckinOut.CC_ACTION);
                            CheckinOut.getData(clkInOut, mActivity,mActivity, CHECKOUT_REQ_LOGOUT);
                            h1.removeCallbacks(this);
                        }
                    }, 300);

                }
                if (response.getId()==CHECKOUT_REQ_LOGOUT) {
                    mActivity.stopUser();
                }
                if (response.getId()==LOGOUT_REQ){
                    mActivity.logout();
                }
            }
            else if(response.getStatus().equals("failure")){
                if (response.getId()==ReqChangePassword)
                {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showErrorDialog("Unable to change passcode");
                        }
                        });
                }
            }
        }

    }

    public void syncDataBeforeLogout(String typeOfSync) {// YD using refActivity to handle null intent when calling from option frag
        //	openSyncLogoutDialog();
        SyncRO syncObj = new SyncRO();
        syncObj.setType(typeOfSync);
        syncOfflineData(syncObj, SYNC_OFFLINE_DATA_LOGOUT);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(mActivity, AppLoginPage.class);
                startActivity(i);
                //YD clear the preference completely
				/*SharedPreferences settings = getSharedPreferences("PreferencesName", Context.MODE_PRIVATE);
				settings.edit().clear().commit();*/
                //finish();
            }
        });

    }

    private void syncOfflineData(SyncRO syncObj ,int reqId) {
        AceRequestHandler requestHandler=null;
        Intent intent = null;

        requestHandler = new AceRequestHandler(mActivity);
        intent = new Intent(mActivity,AceRouteService.class);

        Long currentMilli = PreferenceHandler.getPrefQueueRequestId(mActivity);

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

}
