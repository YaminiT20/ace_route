package com.aceroute.mobile.software;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aceroute.mobile.software.utilities.JSONHandler;
import com.aceroute.mobile.software.utilities.Utilities;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class AcerouteMap extends Activity implements LocationListener {

	public final static String MAP_JSON = "json_string";
	private GoogleMap map;

	HashMap<String, String> infoMap = new HashMap<String, String>();
	JSONArray jarray = null;
	
	private TextView tv_map_day;
	private TextView tv_map_date;
	private ImageView mBackButton;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.aceroute_map);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.fr_map))
				.getMap();
		
		tv_map_day = (TextView) findViewById(R.id.tv_map_header_day);
		tv_map_date = (TextView) findViewById(R.id.tv_map_header_date);
		
		try {
			Typeface font = Typeface.createFromAsset(getAssets(), "lato-bold-webfont.ttf");
			tv_map_day.setTypeface(font);
			tv_map_date.setTypeface(font);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		mBackButton = (ImageView) findViewById(R.id.back_button);
		
		GPSTracker mGPS = new GPSTracker(this);
		if (mGPS.canGetLocation) {
			mGPS.getLocation();
		} else {
			System.out.println("Unable");
		}

		try {
			map.setMyLocationEnabled(true);
			String json = getIntent().getStringExtra(MAP_JSON);
			Log.i(Utilities.TAG, "json string : " + json);
			if (json == null) {
				Log.i( Utilities.TAG, "json is null finishing activity");
				Toast.makeText(getApplicationContext(), "JSON ERROR",
						Toast.LENGTH_LONG).show();
				finish();
			}

			String day = null;
			String date = null;
			
			jarray = JSONHandler.getJsonArray(json);
			Log.i(Utilities.TAG, "Map json array size : " + jarray.size());
			int i = 0;
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			while (i < jarray.size()) {
				JSONObject jobject = (JSONObject) jarray.get(i);
				String geo = (String) jobject.get("geo");
				String sequence = (String) jobject.get("sequence") == null ? ""
						: (String) jobject.get("sequence");
				String start_time = (String) jobject.get("start_time") == null ? ""
						: (String) jobject.get("start_time");
				String end_time = (String) jobject.get("end_time") == null ? ""
						: (String) jobject.get("end_time");
				String po = (String) jobject.get("po") == null ? ""
						: (String) jobject.get("po");
				String nm = (String) jobject.get("nm") == null ? ""
						: (String) jobject.get("nm");
				String id = (String) jobject.get("id") == null ? ""
						: (String) jobject.get("id");
				String adr = (String) jobject.get("adr") == null ? ""
						: (String) jobject.get("adr");
				String res_name = (String) jobject.get("res_name") == null ? ""
						: (String) jobject.get("res_name");
				String cnm = (String) jobject.get("cnm") == null ? ""
						: (String) jobject.get("cnm");
				date = (String) jobject.get("date") == null ? ""
						: (String) jobject.get("date");
				day= (String) jobject.get("day") == null ? ""
						: (String) jobject.get("day");

				infoMap.put(id, sequence + "#&#" + start_time + " - " + end_time
						+ "#&#" + po + "#&#" + nm + "#&#" + adr + "#&#" + res_name
						+ "#&#" + cnm);

				String geoLoc[] = geo.split(",");
				float lat = 0;
				float lon = 0;
				if (geoLoc.length == 2) {
					// to confirm lat long is available
					lat = Float.parseFloat(geoLoc[0]);
					lon = Float.parseFloat(geoLoc[1]);
				}
				LatLng latlng = new LatLng(lat, lon);

				if (lat != 0 && lon != 0) {
					Marker kiel = map.addMarker(new MarkerOptions()
							.position(latlng)
							.title(id)
							.visible(true)
							.icon(BitmapDescriptorFactory
									.fromBitmap(writeTextOnDrawable(
											R.drawable.marker, sequence))));

					builder.include(kiel.getPosition());
				}
				
				// Move the camera instantly to hamburg with a zoom of 15.
				
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 6));
				i++;
			}
			
			try {
				tv_map_day.setText(day);
				tv_map_date.setText(date);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			try {
				LatLng latlng = new LatLng(mGPS.getLatitude(), mGPS.getLongitude());
				//LatLng latlng = new LatLng(37.589812, -122.340657);
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mBackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		try {
			map.setOnMarkerClickListener(new OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(Marker marker) {
					if (marker.getTitle().equals("Aceroute order")) {
						Log.i(Utilities.TAG,
								"software order clicked."
										+ marker.isInfoWindowShown());
						if (marker.isInfoWindowShown())
							marker.hideInfoWindow();
						else
							marker.showInfoWindow();
					}
					return false;
				}
			});

			map.setInfoWindowAdapter(new InfoWindowAdapter() {
				@Override
				public View getInfoWindow(Marker marker) {
					return null;
				}

				@Override
				public View getInfoContents(final Marker marker) {
					View popup = getLayoutInflater().inflate(
							R.layout.marker_dialog, null);
					try {
						// TextView tv_title = (TextView) popup
						// .findViewById(R.id.tv_markerTitle);

						TextView tv_orderID = (TextView) popup
								.findViewById(R.id.tv_marker_cust_order_id);
						TextView tv_name = (TextView) popup
								.findViewById(R.id.tv_marker_cust_name);
						TextView tv_address = (TextView) popup
								.findViewById(R.id.tv_marker_cust_address);
						TextView tv_time = (TextView) popup
								.findViewById(R.id.tv_marker_cust_time);
						ImageView iv_closeDialog = (ImageView) popup
								.findViewById(R.id.iv_marker_close);
						iv_closeDialog.setVisibility(popup.GONE);
						String title = marker.getTitle();
						String info = infoMap.get(title);
						if (info != null) {
							String information[] = info.split("#&#");

							try {
								tv_time.setText(information[1]);
							} catch (Exception e) {
								e.printStackTrace();
							}
							try {
								tv_orderID.setText(title);
							} catch (Exception e) {
								e.printStackTrace();
							}
							try {
								tv_name.setText(information[3]);
							} catch (Exception e) {
								e.printStackTrace();
							}
							try {
								String address = information[4];
								if (address != null && !address.equals("")) {
									address = address.replace("<br/>", "\n");
									tv_address.setLines(2);
									tv_address.setText(address);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						iv_closeDialog.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Log.i(Utilities.TAG, "Closing marker diaLogger.");
								marker.hideInfoWindow();
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
					return (popup);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	private Bitmap writeTextOnDrawable(int drawableId, String text) {

		try {
			Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId)
					.copy(Bitmap.Config.ARGB_8888, true);

			Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);
			Context context = getApplicationContext();
			Paint paint = new Paint();
			paint.setStyle(Style.FILL);
			paint.setColor(Color.WHITE);
			paint.setTypeface(tf);
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(convertToPixels(context, 11));

			Rect textRect = new Rect();
			paint.getTextBounds(text, 0, text.length(), textRect);

			Canvas canvas = new Canvas(bm);

			// If the text is bigger than the canvas , reduce the font size
			if (textRect.width() >= (canvas.getWidth() - 4)) // the padding on
																// either sides is
																// considered as 4,
																// so as to
																// appropriately fit
																// in the text
				paint.setTextSize(convertToPixels(context, 7)); // Scaling needs to
																// be used for
																// different dpi's

			// Calculate the positions
			int xPos = (canvas.getWidth() / 2) - 2; // -2 is for regulating the x
													// position offset
			int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint
					.ascent()) / 2));
			canvas.drawText(text, xPos, yPos - 10, paint);

			return bm;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int convertToPixels(Context context, int nDP) {
		final float conversionScale = context.getResources()
				.getDisplayMetrics().density;

		return (int) ((nDP * conversionScale) + 0.5f);

	}

}
