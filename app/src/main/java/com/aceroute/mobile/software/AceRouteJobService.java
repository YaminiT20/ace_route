package com.aceroute.mobile.software;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;

public class AceRouteJobService extends JobService {

    Bundle bundle;
    String action;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        bundle = intent.getExtras();
        action = intent.getAction();

        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {


        Intent service = new Intent(getApplicationContext(), AceRouteService.class);
        service.putExtras(bundle);
        service.setAction(action);
        //getApplicationContext().startService(service);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplicationContext().startForegroundService(service);
        } else {
            getApplicationContext().startService(service);
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public static Bundle toBundle(PersistableBundle persistableBundle) {
        if (persistableBundle == null) {
            return null;
        }
        Bundle bundle = new Bundle();
        bundle.putAll(persistableBundle);
        return bundle;
    }
}
