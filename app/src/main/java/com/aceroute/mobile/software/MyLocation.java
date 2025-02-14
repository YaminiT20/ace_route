package com.aceroute.mobile.software;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.aceroute.mobile.software.utilities.Utilities;

import java.util.Timer;
import java.util.TimerTask;

public class MyLocation {
    Timer timer1;
    LocationManager lm;
    LocationResult locationResult;
    boolean gps_enabled=false;
    boolean network_enabled=false;
    Context context = null;
    
    public MyLocation(Context context){
    	this.context = context;
    }

    public boolean getLocation(Context context, LocationResult result) {
    	//Log.i( Utilities.TAG, "---------------Getting location");
        //I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult=result;
        if(lm==null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        
        //exceptions will be thrown if provider is not permitted.
        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

        //don't start listeners if no provider is enabled
        if(!gps_enabled && !network_enabled)
            return false;

        if(gps_enabled)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        if(network_enabled)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
       // Log.i( Utilities.TAG, "---------------Setting timer");
        timer1=new Timer();
        timer1.schedule(new GetLastLocation(), 20000, 2 * 60 * 1000);
        return true;
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
        	//Log.i( Utilities.TAG, "---------------location changed in gps.");
            //timer1.cancel();
            locationResult.gotLocation(location);
            //lm.removeUpdates(this);
            //lm.removeUpdates(locationListenerNetwork);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            //timer1.cancel();
        	//Log.i( Utilities.TAG, "---------------location changed in network.");
            locationResult.gotLocation(location);
            //lm.removeUpdates(this);
            //lm.removeUpdates(locationListenerGps);
        }
        public void onProviderDisabled(String provider) {
            Log.i( Utilities.TAG, "---------------onProviderDisabled timer for gps...");}
        public void onProviderEnabled(String provider) {Log.i( Utilities.TAG, "---------------onProviderEnabled timer for gps...");}
        public void onStatusChanged(String provider, int status, Bundle extras) {Log.i( Utilities.TAG, "---------------onStatusChanged timer for gps...");}
    };

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            // lm.removeUpdates(locationListenerGps);
            // lm.removeUpdates(locationListenerNetwork);
             //Log.i( Utilities.TAG, "---------------Executing timer.");
             Location net_loc=null, gps_loc=null;
             if(gps_enabled)
                 gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
             if(network_enabled)
                 net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

             //if there are both values use the latest one
             if(gps_loc!=null && net_loc!=null){
                 if(gps_loc.getTime()>net_loc.getTime()) {
                	// Log.i( Utilities.TAG, "---------------gps_loc");
                     locationResult.gotLocation(gps_loc);
                 }
                 else {
                	 //Log.i( Utilities.TAG, "---------------net_loc");
                     locationResult.gotLocation(net_loc);
                 }
                 return;
             }

             if(gps_loc!=null){
            	// Log.i( Utilities.TAG, "---------------getting location from gps");
                 locationResult.gotLocation(gps_loc);
                 return;
             }
             if(net_loc!=null){
            	// Log.i( Utilities.TAG, "---------------getting location from network");
                 locationResult.gotLocation(net_loc);
                 return;
             }
             locationResult.gotLocation(null);
             
             if(gps_enabled)
                 lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
             if(network_enabled)
                 lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        }
    }
    
    public Location isLocationAccurate(){
	    LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	    
	    Criteria criteria = new Criteria();
	    criteria.setAccuracy(Criteria.ACCURACY_FINE);
	    criteria.setAltitudeRequired(false);
	    criteria.setBearingRequired(false);
	    criteria.setCostAllowed(true);
	    String strLocationProvider = lm.getBestProvider(criteria, true);
	
	   // System.out.println("strLocationProvider=" + strLocationProvider);
	    Location location = lm.getLastKnownLocation(strLocationProvider);
	    if(location != null){
	       return location;
	    }
		return null;
    }
    
    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
    }
}