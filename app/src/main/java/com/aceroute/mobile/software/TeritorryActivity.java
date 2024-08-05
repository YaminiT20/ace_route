package com.aceroute.mobile.software;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.Utilities;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class TeritorryActivity extends Activity implements GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, LocationListener, GoogleMap.OnMarkerDragListener, View.OnClickListener {

    private GoogleMap googleMap;
    public static ArrayList<LatLng> latLang = new ArrayList<LatLng>();
    ArrayList<Marker> markerArrayList = new ArrayList<Marker>();
    boolean isGeometryClosed = false;
    Polygon polygon;
    Location location;
    double latitude;
    double longitude;
    Context context = TeritorryActivity.this;
    boolean isStartGeometry = false;
    public boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    public boolean canGetLocation = false;
    ImageView backImage, trashImage, doneImage;
    protected LocationManager locationManager;
    int position;
    TextView headerText;
    MarkerOptions marker;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teritorry);
        backImage = (ImageView) findViewById(R.id.back_bttn);
        trashImage = (ImageView) findViewById(R.id.trash);
        doneImage = (ImageView) findViewById(R.id.btn_Done);
        headerText = (TextView) findViewById(R.id.header_top);
        headerText.setTextSize(20 + (PreferenceHandler.getCurrrentFontSzForApp(this)));
        backImage.setOnClickListener(this);
        trashImage.setOnClickListener(this);
        doneImage.setOnClickListener(this);
        isStartGeometry = getIntent().getBooleanExtra("mapvalue", false);
        isGeometryClosed = getIntent().getBooleanExtra("makeEdit", false);
        initilizeMap();
    }

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            if (googleMap == null) {
                Toast.makeText(this, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isStartGeometry)
                latLang = new ArrayList<LatLng>();
            else {
                latLang = getIntent().getParcelableArrayListExtra("latLang");
                if (latLang.size() > 0) {
                    for (int i = 0; i < latLang.size(); i++) {
                        Marker marker = googleMap.addMarker(new MarkerOptions().position(latLang.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon)).draggable(true));
                        markerArrayList.add(marker);
                    }
                    drawPolygon(latLang);
                }
            }
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(false);
            googleMap.getUiSettings().setRotateGesturesEnabled(false);
            googleMap.getUiSettings().setZoomGesturesEnabled(false);
//            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.setOnMarkerDragListener(this);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getLocation().getLatitude(), getLocation().getLongitude()), 11f));
            googleMap.setOnMapClickListener(this);
            googleMap.setOnMarkerClickListener(this);
        }
    }

    @Override
    public void onMapClick(LatLng latlan) {
        latLang.add(latlan);
        Marker marker = googleMap.addMarker(new MarkerOptions().position(latlan).icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon)).draggable(true));
        markerArrayList.add(marker);
        Log.d("Marker Add", marker.getId());
        if (latLang.size() > 1) {
            PolygonOptions rectOptions = new PolygonOptions();
            rectOptions.addAll(latLang);
            rectOptions.strokeColor(Color.GRAY);
            rectOptions.fillColor(Color.LTGRAY);
            rectOptions.strokeWidth(5);
            polygon = googleMap.addPolygon(rectOptions);
        }
    }

    private void drawPolygon(List<LatLng> latLngList) {
        if (polygon != null) {
            polygon.remove();
        }
        if (latLngList.size() >= 1) {
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.strokeColor(Color.GRAY);
            polygonOptions.fillColor(Color.LTGRAY);
            polygonOptions.strokeWidth(5);
            polygonOptions.addAll(latLngList);
            markerArrayList = new ArrayList<>();
            for (int i = 0; i < latLngList.size(); i++) {
                Marker marker = googleMap.addMarker(new MarkerOptions().position(latLngList.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon)).draggable(true));
                markerArrayList.add(marker);
            }
            polygon = googleMap.addPolygon(polygonOptions);
        } else {
            latLang = new ArrayList<LatLng>();
            markerArrayList = new ArrayList<Marker>();
        }
    }

    public void clearCanvas(View view) {
        googleMap.clear();
        latLang = new ArrayList<>();
        markerArrayList = new ArrayList<>();
       /* Intent intent = new Intent();
        intent.putExtra("arrayListLatlng", latLang);
        setResult(RESULT_OK, intent);
        finish();*/
      /*  if (latLang.size() > 0) {
            if (latLang.size() == 1) {
                googleMap.clear();
                latLang = new ArrayList<LatLng>();
                markerArrayList = new ArrayList<Marker>();
            } else {
                Marker marker = markerArrayList.get(markerArrayList.size() - 1);
                marker.remove();
                markerArrayList.remove(marker);
                latLang.remove(latLang.size() - 1);
                if (latLang.size() > 0) {
                    googleMap.clear();
                    drawPolygon(latLang);
                }
            }
        }*/
    }

    public void savePolygon() {
        Intent intent = new Intent();
        intent.putExtra("arrayListLatlng", latLang);
        setResult(RESULT_OK, intent);
        finish();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.i( Utilities.TAG, "isGPSEnabled" + "=" + isGPSEnabled);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.i( Utilities.TAG, "isNetworkEnabled" + "=" + isNetworkEnabled);
            if (isGPSEnabled == false && isNetworkEnabled == false) {
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    location = null;
                    int currentapiVersion = Build.VERSION.SDK_INT;
                    if (currentapiVersion >= Build.VERSION_CODES.M) {
                        // Do something for marshmallow and above versions
                        int permissionCheck = this.checkSelfPermission(
                                Manifest.permission.ACCESS_FINE_LOCATION);

                        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                            ((Activity) context).requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, BaseTabActivity.GET_ACCESS_FINE_LOCATION_PERM);//YD check for context
                        } else {
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        }
                    } else {
                        // do something for phones running an SDK before Marshmellow
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    }
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (isGPSEnabled && location == null) {
                    location = null;
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }


    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        markerArrayList.remove(marker);
        latLang.remove(marker.getPosition());
        googleMap.clear();
        drawPolygon(latLang);
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        position = markerArrayList.indexOf(marker.getId());
        Log.d("Marker", marker.getId());
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.d("Marker End", marker.getId());
        String temp = marker.getId();
        int pos = -1;
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        for (int i = 0; i < markerArrayList.size(); i++) {
            if (markerArrayList.get(i).getId().equals(temp))
                pos = i;
        }
        markerArrayList.set(pos, marker);
        latLang.set(pos, marker.getPosition());
        googleMap.clear();
        drawPolygon(latLang);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_bttn:
                onBackPressed();
                break;
            case R.id.trash:
                clearCanvas(v);
                break;
            case R.id.btn_Done:
                savePolygon();
                break;

        }
    }
}