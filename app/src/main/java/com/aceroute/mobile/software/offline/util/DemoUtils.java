package com.aceroute.mobile.software.offline.util;


import android.content.Context;
import android.location.LocationManager;


public class DemoUtils {

    
    /**
     * Checks if the current device has a GPS module (hardware)
     * @return true if the current device has GPS
     */
    public static boolean hasGpsModule(final Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        for (final String provider : locationManager.getAllProviders()) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                return true;
            }
        }
        return false;
    }
}