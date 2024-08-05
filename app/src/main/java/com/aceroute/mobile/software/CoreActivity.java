package com.aceroute.mobile.software;

import android.app.Activity;
import android.content.Intent;
import androidx.core.app.ActivityCompat;
import android.util.Log;

import com.aceroute.mobile.software.utilities.ActivitytRunTimePermissionEvent;

import org.greenrobot.eventbus.EventBus;

public class CoreActivity extends Activity {

    public String activityName;
    int requestCode;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

    }

    public void checkAndRequestPermissions(final int requestCode, Object[] listPermissionsNeeded, String activityName) {


        if (listPermissionsNeeded.length > 0) {
            this.activityName = activityName;
            this.requestCode = requestCode;

            Log.e("permission size", listPermissionsNeeded.toString());

            String[] data = new String[listPermissionsNeeded.length];

            for (int i = 0; i < listPermissionsNeeded.length; i++) {
                data[i] = (String) listPermissionsNeeded[i];
            }

            ActivityCompat.requestPermissions(this, data, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        ActivitytRunTimePermissionEvent dt = new ActivitytRunTimePermissionEvent(activityName, this.requestCode, permissions, grantResults);

        EventBus.getDefault().post(dt);


    }

}
