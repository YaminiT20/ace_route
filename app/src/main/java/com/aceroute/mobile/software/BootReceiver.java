package com.aceroute.mobile.software;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.aceroute.mobile.software.utilities.Utilities;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent arg1) {
        Log.i(Utilities.TAG, "PubNub BootReceiver Starting");
        Intent intent = new Intent(context, AceRouteService.class);
        //context.startService(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        Log.i(Utilities.TAG, "PubNub BootReceiver Started");
    }

}
