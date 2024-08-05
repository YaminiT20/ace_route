package com.aceroute.mobile.software.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.GPSTracker;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.IActionOKCancel;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderTask;
import com.aceroute.mobile.software.component.reference.Assets;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.ServiceType;
import com.aceroute.mobile.software.component.reference.Site;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.dialog.CustomDialog;
import com.aceroute.mobile.software.dialog.CustomDialog.DIALOG_TYPE;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.offline.application.Channel;
import com.aceroute.mobile.software.requests.CommonSevenReq;
import com.aceroute.mobile.software.requests.EditSiteReq;
import com.aceroute.mobile.software.requests.GetAsset;
import com.aceroute.mobile.software.requests.GetSiteRequest;
import com.aceroute.mobile.software.requests.SaveSiteRequest;
import com.aceroute.mobile.software.requests.updateOrderRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.simple.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class GoogleMapFragment extends BaseFragment implements RespCBandServST, LocationListener, HeaderInterface, OnClickListener, IActionOKCancel {

	public final static String MAP_JSON = "json_string";
	public GoogleMap map = null;
	private String headerText;


	// callbacks
	int SAVEORDERFIELD_STATUS_PG =1;
	int SAVESITE_ACCESS_POINT = 2;
	private int GET_SITE_REQ = 3;
	static int GET_TASKS=4;
	int SAVESITE_WAY_PATH = 5;
	private int SAVEORDERFIELD_ACCESSPATH_GEO=6;
	private int GET_ASSETS=7;
	int SAVESITE_ORDER =8;
	int DELETE_SITE = 9;
	int GETSITETYPE = 10;

	private int EDN_FOR_DETAIL_TASK = 599; // For Deatil Task Screen
	private int EDN_FOR_REGULAR_TASK = 600; // For Regular Task Screen

	HashMap<Integer, Channel> infoMap = new HashMap<Integer, Channel>();
	JSONArray jarray = null;

	//public BaseTabActivity mActivity;
	private MyDialog dialog;
	public static String maptype;
	private CustomDialog customDialog;

	public static String lstAccPathStr;
	
	//	ArrayList<String> listOfAcesPth = null;
	/*private ArrayList<Integer> arrlstTemPath = null;
	private HashMap<Integer, Integer> mapTemPath = null;
	private boolean btnUpdateShow = false;*/

	private HashMap<Integer, String> mapOfAcesPth = null;
	private HashMap<Integer, Integer> mapTmpAcesPath = null;
	private boolean btnUpdateShow = false;	
	private static int total_accessPath;

	String typeOfMap="";
	String orderIDForTasklst="0";
	private Marker Selectedmarker=null;
	int Edition;
	protected int AccPathId2Delete;	
	LatLng crntLocationLatLng = null;

	Order activeOrderObj= null;
    private int mheight = 500;//YD 2020

	private static String currentOdrId, currentOdrName;
	private TextView popup_order_name, popup_order_Id, popup_customer_name, popup_address, total_tree_count;
	private Button btnLogwayPoint, btnSaveChanges;
	private ImageView btn_zoom, btn_editList;
	private RelativeLayout bottom_show;
	private WebView mWebview;

	private Long[] orderxmlKeys;
	private Long[] tasksxmlKeys;
	private Long[] custSitexmlKeys;
	private Long[] siteTypexmlKeys;
	private Long[] allSitexmlKeys;

	public static int FIELD_UPDATED=0;
	private boolean isDialogForUpdate = false;
	private boolean isDialogForUpdate_OnBack = false; 
	private OrderTask odrTaskObj=null;
	private int isEditTree = 0;

	HashMap<Long, Site> tempSiteMap = new HashMap<Long, Site>();
	HashMap<Long, Site> allPoleMap = new HashMap<Long, Site>();

	static HashMap<Long, Order> orderxml = null;
	static long orderxmlversion=0;
	static long orderxmlusedversion=0;

	static HashMap<Long, OrderTask> tasksxml = null;
	static long tasksxmlversion=0;
	static long tasksxmlusedversion=0;

	static HashMap<Long, OrderTask> alltasksxml = null;
	static long alltasksxmlversion=0;
	static long alltasksxmlusedversion=0;

	static HashMap<Long, Site> custSitexml = null;
	static long custSitexmlversion=0;
	static long custSitexmlusedversion=0;   

	static HashMap<Long, Site> allSitexml = null;
	static long allSitexmlversion=0;
	static long allSitexmlusedversion=0;   

	static HashMap<Long,ServiceType> taskTypexml = null;
	static HashMap<Long, SiteType> siteTypexml = null;

	static String accesspathpoints=null;

	private ArrayList<LatLng> arrayPoints = null;
	PolylineOptions polylineOptions;

	String annotatOdr_OrderId="0";
	String annotatOdr_OrderNm="";

	HashMap<Long, Assets> assetsLst = null;
	HashMap<Long, SiteType> siteTypeList = null;

	private static long waypointId = 0;
	private static long accessPointId = 0;
	private long odr_customerId = 0;
	private String access_point_geo = null;

	private int breadcrumbs_counter = 0;

	private String accesspath_lat_long = null;
	static String annotatOdr_TaskId= "";
	String geoMainSite = null;//YD using for main site(i.e geo of orderxml)
	boolean geoMainSiteChange = false;

	ArrayList<String> mStatusTypeArryList, mStatusTypeArryListId ;//YD need to change later not the good way
	/*String[] strStatusType = {"Open", "Pending", "Refusal", "Review", "Complete","False Detection", "No Tree"};
	String[] strStatusTypeIds = {"0","1", "2", "3", "4","5","6"};*/
	
	String[] strStatusType = {"Open", /*"Pending",*/ "Closed","False Detection", "No Tree","Refusal", "Review"};
	String[] strStatusTypeIds = {"0",/*"1",*/ "4", "5", "6","2","3"};
	private boolean shouldDelSite = false;//YD Using to check if we want to delete the site having breadcrumbs

	int mapstyle=-1;
    ArrayList<String> optionLst = new ArrayList<String>();
    private HashMap<Long, SiteType> siteGenTypeMapTid_9;
    ArrayList<SiteType> siteTypeArrayList = new ArrayList<SiteType>();

    private HashMap<Long, SiteType> siteAndGenTypeList;

    public void set_init_Maptype(int mapstyle){
		this.mapstyle=mapstyle;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("AceRoute", "onCreate1 called for GoogleMapFragment");
		//mActivity = (BaseTabActivity) this.getActivity();	
		 

		/*if(Edition <600)
			PreferenceHandler.setPrefEditionForGeo(mActivity, 699);*/

		Log.e("Edition :", String.valueOf(Edition));
	}

	static View gview =null;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.e("AceRoute", "onAttach called for GoogleMapFragment");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.e("AceRoute", "onActivityCreated called for GoogleMapFragment");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		Log.e("AceRoute", "onCreateView called for GoogleMapFragment");
		setRetainInstance(false);
		/*com.google.android.gms.maps.SupportMapFragment f = (com.google.android.gms.maps.SupportMapFragment) getFragmentManager()
				.findFragmentById(R.id.gmap_fr_map);
		if (f != null) 
		{	
			getFragmentManager().beginTransaction().remove(f).commit(); //YD used because creating issue when open map 2nd time (Inflator exception issue was there)
			//getFragmentManager().beginTransaction().remove(f).commit(); //YD earlier using this but causing map to hang up 
		}// YD added by bhaiya here	
*/		
		if (gview != null) {
	        ViewGroup parent = (ViewGroup) gview.getParent();
	        if (parent != null)
	            parent.removeView(gview);
	    } 
	    try { 
	    	gview = inflater.inflate(R.layout.google_map_view, container, false);
	    } catch (InflateException e) {
	        /* map is already there, just return view as it is */ 
	    }
		TypeFaceFont.overrideFonts(mActivity, gview);
		//gview = inflater.inflate(R.layout.google_map_view, container, false);		
		mActivity.registerHeader(this);  //YD
		arrayPoints = new ArrayList<LatLng>();

		bottom_show = (RelativeLayout) gview.findViewById(R.id.bottom_show);
		btnLogwayPoint = (Button) gview.findViewById(R.id.btnLogwayPoint);
		btnSaveChanges = (Button) gview.findViewById(R.id.btnSaveChanges);
		btnSaveChanges.setEnabled(false);			
		btnSaveChanges.setBackground(getResources().getDrawable(R.drawable.btn_logwaypoint_selector));
		total_tree_count = (TextView) gview.findViewById(R.id.total_tree_count);
	 	if(activeOrderObj!=null){
			//total_tree_count.setText(String.valueOf(activeOrderObj.getCustServiceCount()));  // MY Show total tree count //YD 2020 ordertask is not in app anymore
		}

		btnLogwayPoint.setOnClickListener(this);
		btnSaveChanges.setOnClickListener(this);

		mWebview = (WebView) gview.findViewById(R.id.webviewProgress);
		mWebview.loadUrl("file:///android_asset/loading.html");
		mWebview.setBackgroundColor(0x00000000);
		if (Build.VERSION.SDK_INT >= 11)
			mWebview.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

		this.mWebview.setWebViewClient(new WebViewClient()
		{
			@Override
			public void onPageFinished(WebView view, String url)
			{
				view.setBackgroundColor(0x00000000);
				if (Build.VERSION.SDK_INT >= 11)
					view.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
			}
		});

		if (map == null) {
			map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_fr_map)).getMap();
			if(map!=null){
			if(maptype.equals("TreeList"))
				map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			else
				map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

				if(mapstyle>0){
					map.setMapType(mapstyle);
				}
			
			map.setMyLocationEnabled(true);
			map.getUiSettings().setCompassEnabled(true);
			map.getUiSettings().setAllGesturesEnabled(true);
			}else{
				final Handler mHandler = new Handler();
				mHandler.post(new Runnable() {
				            @Override
				            public void run() {
				            	map = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.google_fr_map)).getMap();
				                if (map != null) {
				                    map.setMyLocationEnabled(true);
				                    // INIT HERE
				                    map.getUiSettings().setMyLocationButtonEnabled(false);
				                    // ...
			
				                } else mHandler.post(this);
				            }
				        });
			if(maptype.equals("TreeList"))
				map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			else
				map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			
			map.setMyLocationEnabled(true);
			map.getUiSettings().setCompassEnabled(true);
			map.getUiSettings().setAllGesturesEnabled(true);	
		}
		}

		if(maptype.equals("OrderList")){         // OverAll Map
			long lastSyncTime = PreferenceHandler.getlastsynctime(mActivity);
			Date date = new Date(Long.valueOf(lastSyncTime));
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yyyy");//Jun 21 2015
			String headerDate = simpleDateFormat.format(date);
			String headerTime = Utilities.convertDateToAmPM(date.getHours(),date.getMinutes());
			BaseTabActivity.setHeaderTitle(headerDate,"",headerTime);    	
		}else if(maptype.equals("TreeList")){ 		// Order Map
			Bundle bundle = this.getArguments();
			currentOdrId = bundle.getString("OrderId");
			currentOdrName = bundle.getString("OrderName");
			headerText = PreferenceHandler.getAssetsHead(mActivity);//mBundle.getString("HeaderText");
			if (headerText!=null && !headerText.equals(""))
				headerText = headerText+"S";
			else
				headerText = "Assets List";// YD just for backup

			mActivity.setHeaderTitle("", headerText, "");
		}

		Edition = PreferenceHandler.getPrefEditionForGeo(getActivity());

		/*if(maptype.equals("TreeList"))
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		else
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);*/

		GPSTracker mGPS = new GPSTracker(mActivity);
		if (mGPS.canGetLocation) {
			mGPS.getLocation();
		} else {		
			System.out.println("Unable");
		}

		try {		
			/*map.setMyLocationEnabled(true);
			map.getUiSettings().setCompassEnabled(true);
			map.getUiSettings().setAllGesturesEnabled(true);	*/	
			map.clear();
			if(maptype.equals("OrderList"))   // OverAll map
			{
				if(Edition > EDN_FOR_DETAIL_TASK){
					getAllSites();
				} else if(Edition < EDN_FOR_REGULAR_TASK){
					showOrdersOnmap();	
				}
			}
			else if(maptype.equals("TreeList")){ 	// Order map
				Order order = ((HashMap<Long, Order>)DataObject.ordersXmlDataStore).get(Long.valueOf(currentOdrId));

				waypointId = 0;
				accessPointId = 0;

				odr_customerId = order.getCustomerid();

				if(Edition > EDN_FOR_DETAIL_TASK){
					showOrder(true);
					getCustSites(odr_customerId);
					getSiteOrGenType();

					//getTasks(currentOdrId);

					//checkforTasksDataLoading();
					//	checkforAccessPathDataLoading();
				}else if(Edition < EDN_FOR_REGULAR_TASK){
					showOrder(true); 	
				}							
			}
			
			// MY For Tree Status correct later 			
			mStatusTypeArryList = new ArrayList<String>();
			mStatusTypeArryListId = new ArrayList<String>();
			for(int k=0; k<strStatusType.length; k++){
				mStatusTypeArryList.add(strStatusType[k]);
				mStatusTypeArryListId.add(strStatusTypeIds[k]);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		// YD using this for waypoint to add .
		try {
			map.setOnMapLongClickListener(new OnMapLongClickListener(){
				@Override
				public void onMapLongClick(LatLng point) {
					if (maptype.equals("TreeList") /*&& Edition > EDN_FOR_DETAIL_TASK*/){

						btnUpdateShow = true;
						FIELD_UPDATED=1;
						lstAccPathStr="";  					

						if(mapOfAcesPth==null){
							mapOfAcesPth = new HashMap<Integer, String>();
						}

					/*	initAccPathLstLen = mapOfAcesPth.size();  
						mapOfAcesPth.put(initAccPathLstLen+10000, point.latitude+","+point.longitude);*/
						mapOfAcesPth.put(breadcrumbs_counter+10000, point.latitude+","+point.longitude);

						int i=0;//YD below code is to make final list_cal of breadcrumb to save when done is pressed
						Iterator<Entry<Integer,String>> iter = mapOfAcesPth.entrySet().iterator();
						while (iter.hasNext()) {
							Entry<Integer, String> entry = iter.next();
							if(i==0)
								lstAccPathStr = mapOfAcesPth.get(entry.getKey());
							else
								lstAccPathStr= lstAccPathStr+"|"+mapOfAcesPth.get(entry.getKey());
							i++;
						}//YD below code is to make final list_cal of breadcrumb to save when done is pressed ******end
						preparemapshowmarker(point.latitude, point.longitude, breadcrumbs_counter+10000, "0", "",
                                "accesspath", "", false, "",0);

						if(mapTmpAcesPath==null){
							mapTmpAcesPath = new HashMap<Integer, Integer>();
						}

						mapTmpAcesPath.put(breadcrumbs_counter+10000, breadcrumbs_counter+10000);

						/*		LatLng latlng = null;
						latlng = new LatLng(Double.parseDouble(String.valueOf(point.latitude)), Double.parseDouble(String.valueOf(point.longitude)));
						map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 0));*/

						/*btnSaveChanges.setEnabled(true);
						btnSaveChanges.setBackground(getResources().getDrawable(R.drawable.btn_save_update_selector));*/
						mActivity.replaceBtnHeader("plusDone");//YD replacing the header plus with done button

						breadcrumbs_counter ++;
					}
				}
			});

			map.setOnMarkerDragListener(new OnMarkerDragListener() {
				@Override
				public void onMarkerDragStart(Marker marker) {
					// TODO Auto-generated method stub
				//	Log.e("System out", "onMarkerDragStart..."+marker.getPosition().latitude+"..."+marker.getPosition().longitude);
				}

				@Override
				public void onMarkerDragEnd(Marker markerPoint) {
					//YD Using when dragging access point which is a site of customer.
					/*accesspath_lat_long="";

					accesspath_lat_long = markerPoint.getPosition().latitude+","+markerPoint.getPosition().longitude;

					btnUpdateShow = true;
					FIELD_UPDATED=1;

					btnSaveChanges.setEnabled(true);
					btnSaveChanges.setBackground(getResources().getDrawable(R.drawable.btn_save_update_selector));*/

					FIELD_UPDATED=1;
					geoMainSite="";
					geoMainSite = markerPoint.getPosition().latitude+","+markerPoint.getPosition().longitude;
					geoMainSiteChange = true;
					mActivity.replaceBtnHeader("plusDone");
				}

				@Override
				public void onMarkerDrag(Marker arg0) {
					// TODO Auto-generated method stub
				//	Log.e("System out", "Drag@..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
				}
			});


			map.setOnMarkerClickListener(new OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(Marker marker) {
					if (marker.getTitle().equals("Aceroute order")) {
						Log.i(Utilities.TAG,"software order clicked."
								+ marker.isInfoWindowShown());
						if (marker.isInfoWindowShown())
							marker.hideInfoWindow();
						else
							marker.showInfoWindow();
					}

					if(maptype.equals("OrderList")){
						if(Edition > 599){
							if(Integer.valueOf(marker.getTitle())>=10000 && Integer.valueOf(marker.getTitle()) <20000){
								marker.hideInfoWindow();
							}else {
								marker.showInfoWindow();
							}
						}else{
							marker.showInfoWindow();
						}
					}else if(maptype.equals("TreeList")){
						if(Edition > 599){
							if(Integer.valueOf(marker.getTitle())>=10000 && Integer.valueOf(marker.getTitle()) <20000){
								AccPathId2Delete=0;

								//	Selectedmarker = marker;

								AccPathId2Delete = Integer.valueOf(marker.getTitle());

								Log.e("AccPathId2Delete :", String.valueOf(AccPathId2Delete));

								customDialog = CustomDialog.getInstance(mActivity, GoogleMapFragment.this, getResources().getString(R.string.msg_map_delete),
										getResources().getString(R.string.msg_title), DIALOG_TYPE.OK_CANCEL, 2,  mActivity);
								customDialog.setCancellable(false);
								customDialog.show();
							}else{
								marker.showInfoWindow();
							}
						}else{
							marker.showInfoWindow();
						}
					}else{
						marker.hideInfoWindow();
					}

					return true;
				}
			});


			// Marker Window Click Event Handling
			map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
				@Override
				public void onInfoWindowClick(Marker marker) {
					// TODO Auto-generated method stub
					Channel info = infoMap.get(Integer.valueOf(marker.getTitle()));
					/*if(tasksxml!=null){
						odrTaskObj = (OrderTask) tasksxml.get(Long.valueOf(info.getTaskId()));
					}else{
						odrTaskObj = (OrderTask) alltasksxml.get(Long.valueOf(info.getTaskId()));
					}*///YD no need because may be i was not check for which marker info window is clicked

					if(Integer.valueOf(marker.getTitle()) >=2000 && Integer.valueOf(marker.getTitle()) <10000){     // On Tree Icon Popup Click
						if (!btnUpdateShow) {
							if(Edition > EDN_FOR_DETAIL_TASK){
								if(odrTaskObj!=null){
									Bundle bundle = new Bundle();
									bundle.putString("TreeType", "EDIT TASK");

									bundle.putString("OrderId", String.valueOf(odrTaskObj.getId()));
									Order order = ((HashMap<Long, Order>)DataObject.ordersXmlDataStore).get(Long.valueOf(odrTaskObj.getId()));
									bundle.putString("OrderName", order.getNm());

									bundle.putLong("Id", odrTaskObj.getOrder_task_id());
									bundle.putLong("ServiceId", odrTaskObj.getTask_id());
									bundle.putLong("SiteId", Long.parseLong(odrTaskObj.getTree_type()));
									bundle.putLong("PriorityId", Long.parseLong(odrTaskObj.getPriority()));
									bundle.putString("Status", String.valueOf(odrTaskObj.getAction_status()));
									if (odrTaskObj.getTree_owner()!=null)//YD check null as mr a said
										bundle.putString("Owner", odrTaskObj.getTree_owner());
									else
										bundle.putString("Owner", "");
									bundle.putString("EstimatedCount", String.valueOf(odrTaskObj.getTree_expcount()));
									bundle.putString("ActualCount", String.valueOf(odrTaskObj.getTree_actualcount()));
									bundle.putString("TreeHeight", String.valueOf(odrTaskObj.getTree_ht()));
									bundle.putString("DiameterBH", String.valueOf(odrTaskObj.getTree_dia()));
									bundle.putString("HvClearance", String.valueOf(odrTaskObj.getTree_clearence()));
									bundle.putString("Cycle", String.valueOf(odrTaskObj.getTree_cycle()));
									bundle.putString("Hours", String.valueOf(odrTaskObj.getTree_timespent()));
									bundle.putString("TandM", String.valueOf(odrTaskObj.getTree_tm()));
									bundle.putString("OT", String.valueOf(odrTaskObj.getTree_msc()));
									bundle.putString("TreeComment", String.valueOf(odrTaskObj.getTree_comment()));
									bundle.putString("PrescirptionComment", String.valueOf(odrTaskObj.getTree_pcomment()));
									bundle.putString("Alerts", String.valueOf(odrTaskObj.getTree_alert()));
									bundle.putString("Note", String.valueOf(odrTaskObj.getTree_note()));
									bundle.putString("AccessComplexity", String.valueOf(odrTaskObj.getTree_ct1()));
									bundle.putString("SetupComplexity", String.valueOf(odrTaskObj.getTree_ct2()));
									bundle.putString("PrescriptionComplexity", String.valueOf(odrTaskObj.getTree_ct3()));
									bundle.putString("AccessPath", String.valueOf(odrTaskObj.getTree_accesspath()));
									bundle.putString("TreeGeo", String.valueOf(odrTaskObj.getTree_geo()));

									mActivity.OrderEditTaskBackOdrId = Long.valueOf(odrTaskObj.getId());
									AddEditTaskOrderFragment addCustomerOrderFragment = new AddEditTaskOrderFragment();
									addCustomerOrderFragment.setArguments(bundle);
									mActivity.pushFragments(Utilities.JOBS, addCustomerOrderFragment, true, true,BaseTabActivity.UI_Thread);
								}
							}
						}else{
							isDialogForUpdate = true;
							dialog = new MyDialog(mActivity, getResources().getString(
									R.string.lbl_tree_updates), getResources().getString(
									R.string.lbl_upd_message), "YES");
							dialog.setkeyListender(new MyDiologInterface() {
								@Override
								public void onPositiveClick() throws JSONException {
									isDialogForUpdate = false;
									btnUpdateShow = false;
									FIELD_UPDATED = 0;
									dialog.dismiss();

									if (Edition > EDN_FOR_DETAIL_TASK) {

										if (odrTaskObj != null) {
											Bundle bundle = new Bundle();
											bundle.putString("TreeType", "EDIT TASK");

											bundle.putString("OrderId",
													String.valueOf(odrTaskObj.getId()));
											Order order = ((HashMap<Long, Order>) DataObject.ordersXmlDataStore)
													.get(Long.valueOf(odrTaskObj.getId()));
											bundle.putString("OrderName", order.getNm());

											bundle.putLong("Id",
													odrTaskObj.getOrder_task_id());
											bundle.putLong("ServiceId",
													odrTaskObj.getTask_id());
											bundle.putLong("SiteId", Long
													.parseLong(odrTaskObj.getTree_type()));
											bundle.putLong("PriorityId", Long
													.parseLong(odrTaskObj.getPriority()));
											bundle.putString("Status", String
													.valueOf(odrTaskObj.getAction_status()));
											if (odrTaskObj.getTree_owner() != null)
												bundle.putString("Owner",
														odrTaskObj.getTree_owner());
											else
												bundle.putString("Owner", "");
											bundle.putString("EstimatedCount", String
													.valueOf(odrTaskObj.getTree_expcount()));
											bundle.putString("ActualCount", String
													.valueOf(odrTaskObj
															.getTree_actualcount()));
											bundle.putString("TreeHeight",
													String.valueOf(odrTaskObj.getTree_ht()));
											bundle.putString("DiameterBH", String
													.valueOf(odrTaskObj.getTree_dia()));
											bundle.putString("HvClearance",
													String.valueOf(odrTaskObj
															.getTree_clearence()));
											bundle.putString("Cycle", String
													.valueOf(odrTaskObj.getTree_cycle()));
											bundle.putString("Hours",
													String.valueOf(odrTaskObj
															.getTree_timespent()));
											bundle.putString("TandM",
													String.valueOf(odrTaskObj.getTree_tm()));
											bundle.putString("OT", String
													.valueOf(odrTaskObj.getTree_msc()));
											bundle.putString("TreeComment", String
													.valueOf(odrTaskObj.getTree_comment()));
											bundle.putString("PrescirptionComment", String
													.valueOf(odrTaskObj.getTree_pcomment()));
											bundle.putString("Alerts", String
													.valueOf(odrTaskObj.getTree_alert()));
											bundle.putString("Note", String
													.valueOf(odrTaskObj.getTree_note()));
											bundle.putString("AccessComplexity", String
													.valueOf(odrTaskObj.getTree_ct1()));
											bundle.putString("SetupComplexity", String
													.valueOf(odrTaskObj.getTree_ct2()));
											bundle.putString(
													"PrescriptionComplexity",
													String.valueOf(odrTaskObj.getTree_ct3()));
											bundle.putString("AccessPath", String
													.valueOf(odrTaskObj
															.getTree_accesspath()));
											bundle.putString("TreeGeo", String
													.valueOf(odrTaskObj.getTree_geo()));

											mActivity.OrderEditTaskBackOdrId = Long
													.valueOf(odrTaskObj.getId());
											AddEditTaskOrderFragment addCustomerOrderFragment = new AddEditTaskOrderFragment();
											addCustomerOrderFragment.setArguments(bundle);
											mActivity.pushFragments(Utilities.JOBS,
													addCustomerOrderFragment, true, true,
													BaseTabActivity.UI_Thread);
										}
									}
								}

								@Override
								public void onNegativeClick() {
									isEditTree = 1;

									if (access_point_geo != null
											&& accesspath_lat_long != null) {
										String arrTemp[] = access_point_geo.split(",");
										String arrGeo[] = accesspath_lat_long.split(",");

										if (!arrTemp[0].equals(arrGeo[0])
												&& !arrTemp[1].equals(arrGeo[1])) {
											saveAccessPointLocation(accesspath_lat_long,
													odr_customerId, accessPointId);
										}
									}

									if (mapTmpAcesPath != null && mapTmpAcesPath.size() > 0
											|| mapOfAcesPth != null
											&& mapOfAcesPth.size() != total_accessPath) {
										saveNewSiteForWaypoints(lstAccPathStr,
												odr_customerId, waypointId); // Save
																				// Waypoints
									}

									mActivity.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											// TODO Auto-generated method stub
											mWebview.setVisibility(View.VISIBLE);
										}
									});
									dialog.dismiss();
								}
							});
							dialog.onCreate(null);
							dialog.show();
						}
					}

					if(Integer.valueOf(marker.getTitle()) >=0 && Integer.valueOf(marker.getTitle()) <2000){
						if(Edition < EDN_FOR_REGULAR_TASK){
							OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
							Bundle mBundle=new Bundle();
							mBundle.putString("OrderId", String.valueOf(info.getOrderId()));

							orderDetailFragment.setArguments(mBundle);
							mActivity.pushFragments(Utilities.JOBS, orderDetailFragment, true, true,BaseTabActivity.UI_Thread);
						}
					}
					if(Integer.valueOf(marker.getTitle())>=90000 && Integer.valueOf(marker.getTitle()) <100000)
					{
						Assets astObj = assetsLst.get(Long.valueOf(info.getOrderId()));
						AddEditAssetFragment_OLD addEditAssetFragment = new AddEditAssetFragment_OLD();
						Bundle bundle = new Bundle();
						bundle.putString("AssetType", "EDIT ASSET");
						bundle.putString("OrderId", currentOdrId);
						bundle.putString("OrderName", currentOdrName);

						//bundle.putLong("AssetTypeId", astObj.getTid());//YD 2020
						bundle.putLong("orderAssetId", astObj.getId());
						//bundle.putLong("partQuantity", astObj.getContact1());//YD 2020

						addEditAssetFragment.setAssetObject(astObj);
						addEditAssetFragment.setActiveOrderObject(activeOrderObj);
						addEditAssetFragment.setArguments(bundle);
						mActivity.pushFragments(Utilities.JOBS, addEditAssetFragment, true, true, BaseTabActivity.UI_Thread);
					}
				}
			});


			map.setInfoWindowAdapter(new InfoWindowAdapter() {
				@Override
				public View getInfoWindow(Marker marker) {

					return null;
				}

				@Override
				public View getInfoContents(Marker marker) {
					Log.i(BaseTabActivity.LOGNAME, "Annotation clicked GM");
					View view = mActivity.getLayoutInflater().inflate(R.layout.googlemap_layout_popup, null);
					try {
						Typeface tf = TypeFaceFont.getCustomTypeface(mActivity.getApplicationContext());
						popup_order_name = (TextView) view.findViewById(R.id.order_name);
						popup_order_name.setTypeface(tf, Typeface.BOLD);
						popup_order_Id = (TextView) view.findViewById(R.id.order_Id);
						popup_order_Id.setTypeface(tf);
						popup_customer_name = (TextView) view.findViewById(R.id.customer_name);
						popup_customer_name.setTypeface(tf);
						popup_address = (TextView) view.findViewById(R.id.address);
						popup_address.setTypeface(tf);

						popup_order_name.setTextSize(19 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
						popup_customer_name.setTextSize(19 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
						popup_address.setTextSize(17 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
						popup_order_Id.setTextSize(17 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));

						btn_zoom = (ImageView) view.findViewById(R.id.Bt_zoom);
						btn_editList = (ImageView) view.findViewById(R.id.Bt_editList);

						String title = marker.getTitle();

						Log.e("Annotation :", String.valueOf(marker.getTitle()));

						if(maptype.equals("OrderList")){
							if(Edition > EDN_FOR_DETAIL_TASK){
								if(Integer.valueOf(title) >=0 && Integer.valueOf(title) < 2000){ // Orders
									popup_customer_name.setVisibility(View.VISIBLE);                	// OrdersOnmap
								}
								if(Integer.valueOf(title)>=2000 && Integer.valueOf(title)<10000){ // For Order Show Tree Details
									popup_order_name.setVisibility(View.VISIBLE);
									popup_customer_name.setVisibility(View.VISIBLE);
									popup_address.setVisibility(View.VISIBLE);
									btn_editList.setVisibility(View.GONE);
								}else if(Integer.valueOf(title)>=70000 && Integer.valueOf(title) <80000) // For Site Poles Details
								{
									popup_order_name.setVisibility(View.VISIBLE);
								}else{ // Order Details
									popup_customer_name.setVisibility(View.VISIBLE);
								}
							}else if(Edition < EDN_FOR_REGULAR_TASK){
								popup_order_name.setVisibility(View.VISIBLE);
								popup_order_Id.setVisibility(View.VISIBLE);
								btn_zoom.setVisibility(View.VISIBLE);
							}
						}else if(maptype.equals("TreeList")){

							if(Edition > EDN_FOR_DETAIL_TASK){
								if(Integer.valueOf(title) < 2000){
									popup_customer_name.setVisibility(View.VISIBLE);

								}if(Integer.valueOf(title)>=10000 && Integer.valueOf(title) <20000){// if it is accesspath
									/*AccPathId2Delete=0;

									AccPathId2Delete = Integer.valueOf(title);

									Log.e("AccPathId2Delete :", String.valueOf(AccPathId2Delete));*/

								}if(Integer.valueOf(title)>=2000 && Integer.valueOf(title)<10000){ // For Show Tree Details
									popup_order_name.setVisibility(View.VISIBLE);
									popup_customer_name.setVisibility(View.VISIBLE);
									popup_address.setVisibility(View.VISIBLE);
									btn_editList.setVisibility(View.GONE);
								}else if(Integer.valueOf(title)>=70000 && Integer.valueOf(title) <80000) // For Site Poles Details
								{
									popup_order_name.setVisibility(View.VISIBLE);
								}
								else if(Integer.valueOf(title)>=90000 && Integer.valueOf(title) <100000) // For assets
								{
									popup_order_name.setVisibility(View.VISIBLE);
									popup_order_Id.setVisibility(View.VISIBLE);
									btn_editList.setVisibility(View.GONE);

								}

							}else if(Edition < EDN_FOR_REGULAR_TASK){
								popup_order_name.setVisibility(View.VISIBLE);
								popup_order_Id.setVisibility(View.VISIBLE);
								btn_zoom.setVisibility(View.VISIBLE);
							}
						}

						if (Integer.valueOf(title)>=10000 && Integer.valueOf(title)<10500){
							return null;
						}

						Channel info = infoMap.get(Integer.valueOf(title));

						if(info!=null)
						{
							if (info.getOrderId()!=null && info.getO_nameid()!=null)
							{
								annotatOdr_OrderNm=info.getO_nameid();
								annotatOdr_OrderId= info.getOrderId();
							}

							if(Integer.valueOf(title)>=70000 && Integer.valueOf(title) <80000)
							{
								popup_order_name.setText(info.getO_nameid());
								popup_order_Id.setText("");
							}
							if(Integer.valueOf(title)>=90000 && Integer.valueOf(title) <100000)
							{
								popup_order_name.setText(info.getTaskId());
								popup_order_Id.setText(info.getOrderStatus());
								setTextColor(popup_order_Id, info.getAdressid());
							}

							else
							{
								if (info.getTaskId()!=null)
									annotatOdr_TaskId = info.getOrderId()+"#"+info.getTaskId();
								else
									annotatOdr_TaskId = info.getOrderId()+"#0";
							}

							if(Integer.valueOf(title) >=0 && Integer.valueOf(title) < 2000)
							{
								if(Edition > EDN_FOR_DETAIL_TASK){
									popup_customer_name.setText(info.getCustomer());
								}else if(Edition < EDN_FOR_REGULAR_TASK){
									popup_order_name.setText(info.getO_nameid());
									popup_order_Id.setText(info.getOrderId());
								}
							}

							if(Integer.valueOf(title) >=2000 && Integer.valueOf(title) <10000){
								if(Edition > EDN_FOR_DETAIL_TASK){
									popup_order_name.setText(info.getO_nameid()); // Tree Species
									popup_customer_name.setText(info.getCustomer()); // Trim Type
									popup_address.setText(info.getAdressid()); // Tree Status
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return (view);
				}
			});
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return gview;
	}

	private void setTextColor(TextView popup_order_id, String taskId) {

		if (taskId== null){//YD 0 and this is same color
			popup_order_id.setTextColor(Color.parseColor("#73ae37"));
		}
		else if (taskId.equals("8"))
			popup_order_id.setTextColor(Color.parseColor("#95a5a6"));
		else if (taskId.equals("9"))
			popup_order_id.setTextColor(Color.parseColor("#34495f"));
		else if (taskId.equals("0") || taskId.equals("null"))
			popup_order_id.setTextColor(Color.parseColor("#73ae37"));
		else
			popup_order_id.setTextColor(Color.parseColor("#C0392B"));
	}

	private void getSiteOrGenType() {
		CommonSevenReq getSiteTypeList = new CommonSevenReq();
		getSiteTypeList.setAction("getgentype");  // YD earlier getsitetype
		getSiteTypeList.setSource("localonly");
		getSiteTypeList.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");

		SiteType siteTypObj = new SiteType();
		siteTypObj.getObjectDataStore(getSiteTypeList, mActivity, this, GETSITETYPE);
	}

	private void getAsset() {

		GetAsset req = new GetAsset();
		req.setAction(Assets.ACTION_GET_ASSETS);
		req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		if (activeOrderObj.getCustomerid()>0)
			req.setCid(String.valueOf(activeOrderObj.getCustomerid()));
		else
			req.setCid("0");

		Assets.getData(req, mActivity, this, GET_ASSETS);
	}

	private void saveNewSiteForWaypoints(String waypointsLst, long custId, long waypointId)
	{
		String tistamp = String.valueOf(Utilities.getCurrentTime());

		SaveSiteRequest req = new SaveSiteRequest();
		req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		req.setAction(Site.ACTION_SAVE_SITE);
		req.setAdr("United States");
		req.setNm("*Breadcrumbs");//YD change to Breadcrumbs from Waypoints
		req.setCid(""+custId);//34918002
		req.setTstamp(tistamp);
		if (waypointId!=0)
			req.setId(String.valueOf(waypointId));
		else
			req.setId("0");
		req.setGeo("0,0");
		req.setDtl(waypointsLst);
		req.setTid("0");
		req.setLtpnm("0");

		Site.saveData(req, mActivity, this, SAVESITE_WAY_PATH);
	}

	protected void saveAccessPointLocation(String accessPoint, long custId, long accessPointId) {
		String tistamp = String.valueOf(Utilities.getCurrentTime());

		SaveSiteRequest req = new SaveSiteRequest();
		req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		req.setAction(Site.ACTION_SAVE_SITE);
		req.setAdr("United States");
		req.setNm("*Access Point");
		req.setCid(""+custId);
		req.setTstamp(tistamp);
		if (accessPointId!=0)
			req.setId(String.valueOf(accessPointId));
		else
			req.setId("0");
		req.setGeo(accessPoint);
		req.setTid("0");
		req.setLtpnm("0");

		Site.saveData(req, mActivity, this, SAVESITE_ACCESS_POINT);// YD for order geo
	}


	private void getCustSites(long customerid) {
		// TODO Auto-generated method stub
		GetSiteRequest req = new GetSiteRequest();
		req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		req.setCid(String.valueOf(customerid));
		req.setSource("localonly");
		req.setAction("getsite");

		Site.getData(req,mActivity, this, GET_SITE_REQ);
	}


	private void getAllSites() {
		// TODO Auto-generated method stub
		CommonSevenReq req = new CommonSevenReq();
		req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		req.setSource("localonly");
		req.setAction(Site.ACTION_GET_ALL_SITE);//getallsite

		Site.getData(req, mActivity, this, GET_SITE_REQ);
	}

	/*private void getTasks(String customerId)
	{
		GetPart_TaskRequest reqTask = new GetPart_TaskRequest();
		reqTask.setAction(OrderTask.ACTION_GET_ORDER_TASK);
		reqTask.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		reqTask.setOid(currentOdrId);

		OrderTask.getData(reqTask, mActivity, this, GET_TASKS);
	}*/


	@Override
	public void onResume() {
		Log.e("AceRoute", "onResume called for GoogleMapFragment");
		SupportMapFragment f = (SupportMapFragment) getFragmentManager()
				.findFragmentById(R.id.google_fr_map);
		if (f != null){
			getFragmentManager().beginTransaction().show(f).commit();
		}
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		Log.e("AceRoute", "onDestroyView called for GoogleMapFragment");
		SupportMapFragment f = (SupportMapFragment) getFragmentManager()
				.findFragmentById(R.id.google_fr_map);
		if (f != null)
		{
	//YD TODO	//getFragmentManager().beginTransaction().remove(f).commit(); //YD used because creating issue when open map 2nd time (Inflator exception issue was there)
			map = null;
			//getFragmentManager().beginTransaction().remove(f).commit(); //YD earlier using this but causing map to hang up
		}
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.e("AceRoute", "onDetach called for GoogleMapFragment");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.e("AceRoute", "onStop called for GoogleMapFragment");
	}
	@Override
	public void onStart() {
		super.onStart();
		Log.e("AceRoute", "onStart called for GoogleMapFragment");
	}


	@Override
	public void onPause() {
		Log.e("AceRoute", "onPause called for GoogleMapFragment");
		// TODO Auto-generated method stub
		SupportMapFragment f = (SupportMapFragment) getFragmentManager()
				.findFragmentById(R.id.google_fr_map);
		if (f != null)
		{
			getFragmentManager().beginTransaction().hide(f).commit();

		}
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("AceRoute", "onDestroy called for GoogleMapFragment");
	}

	@Override
	public void onLocationChanged(Location location) {
		/*LatLng latlong = new LatLng(textLocation.getLatitude(),
				textLocation.getLongitude());

		map.setMyLocationEnabled(true);

		CameraPosition cp = new CameraPosition.Builder().target(latlong)
				.zoom(15).build();

		map.moveCamera(CameraUpdateFactory.newCameraPosition(cp));*/
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
			Context context = mActivity.getApplicationContext();
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

			// "- ((paint.descent() + paint.ascent()) / 2)" is the distance from the
			// baseline to the center.
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

	private void checkforOrderDataLoading()
	{
		parseallSiteXML();
		preparetionordermapmarkershow();
	}

	private void checkforTasksDataLoading()
	{
		tasksxmlusedversion = tasksxmlversion;
		custSitexmlusedversion = custSitexmlversion;
		orderxmlusedversion = orderxmlversion;

		if (maptype.equals("TreeList")){
			preparetionTreeMapmarkershow(true);

			if(Edition > 599){
				bottom_show.setVisibility(View.VISIBLE);
			}
		}
	}

	/*private void checkforAccessPathDataLoading()
	{
		preparetionAccessPathMapmarkershow();
	}
	 */

	private void preparetionordermapmarkershow()
	{
		map.clear();
		tempSiteMap.clear();

		showOrdersOnmap();
		showOrderTrees(alltasksxml, true);
		showCustSitePoles(tempSiteMap, 1);
	}


	private void preparetionTreeMapmarkershow(boolean mapZoom)
	{
		showOrder(mapZoom);
	}

	private void showOrder(boolean mapZoom) {
		// TODO Auto-generated method stub
		if(activeOrderObj!=null){
			int i=0;
			String geo[] = activeOrderObj.getCustSiteGeocode().split(",");
			String lat = geo[0];
			String lng = geo[1];
			String oderStat = String.valueOf(activeOrderObj.getStatusId());

			Channel channel = new Channel();
			channel.setCid(i);
			channel.setOrderId(String.valueOf(activeOrderObj.getId()));
			channel.setO_nameid(activeOrderObj.getNm());
			channel.setCustomer(activeOrderObj.getCustName());
			channel.setAdressid(activeOrderObj.getCustSiteStreeAdd());
			infoMap.put(i, channel);

			preparemapshowmarker(Double.parseDouble(lat), Double.parseDouble(lng), i, "1", oderStat, "orders",
                    "", mapZoom, "",0);
		}
	}

	// showing Task and accessPath
	public void showOrderTrees(HashMap<Long, OrderTask> datalist, boolean zoom)//YD only showing trees from here not dots
	{
		if(datalist !=null)
		{
			tasksxmlKeys = datalist.keySet().toArray(new Long[datalist.size()]);
			orderxmlKeys = orderxml.keySet().toArray(new Long[orderxml.size()]); // Order Keys

			for(int i=0;i<datalist.size();i++)
			{
				typeOfMap = "tasks_tree";
				OrderTask ordertask = (OrderTask) datalist.get(tasksxmlKeys[i]);

				String geo = ordertask.getTree_geo();
				String treeStatus = ordertask.getAction_status();	//MY tree status

				String orderStatus = "";
				/*if(activeOrderObj!=null){
					orderStatus = String.valueOf(activeOrderObj.getStatusId()); // MY Order status
				}*/
				//MY no use beacuse now getting Order Status from activeOrderObject
				/*if(maptype.equals("OrderList")){
					for(int j=0; j<orderxml.size(); j++)
					{
						Order order = (Order) orderxml.get(orderxmlKeys[j]);
						if(ordertask.getId()==order.getId()){//YD changed
							orderStatus = String.valueOf(order.getStatusId()); break;
						}
					}
				}*/

				if (geo!=null && !geo.equals(""))
				{
					String spltarray[] = geo.split(",");
					if (Double.parseDouble(spltarray[0])!=0 && Double.parseDouble(spltarray[1])!=0) // MY show Tree if geo is not 0,0
					{
						String TreeSpecies = "";

						ServiceType species = ((HashMap<Long, ServiceType>) DataObject.taskTypeXmlDataStore).get(ordertask.getTask_id());
						if(species !=null){
							TreeSpecies = species.getNm();
						}

						Channel channel = new Channel();
						channel.setCid(i);
						channel.setOrderId(String.valueOf(ordertask.getId()));//for order id
						channel.setTaskId(String.valueOf(ordertask.getOrder_task_id()));// putting task id here important
					        channel.setO_nameid(String.valueOf(TreeSpecies)); // Tree Species
						channel.setCustomer("");  // Trim Type
						channel.setAdressid("");	// Tree Status

						if(siteTypexml!=null)
						{
							siteTypexmlKeys = siteTypexml.keySet().toArray(new Long[siteTypexml.size()]);
							for(int m = 0; m<siteTypexml.size(); m++)
							{
								SiteType sitetype = (SiteType) siteTypexml.get(siteTypexmlKeys[m]);
								if(String.valueOf(sitetype.getId()).equals(ordertask.getTree_type()))
								{
									channel.setCustomer(sitetype.getNm()); // trim type
									break;
								}
							}
						}

						// Tree Status
						if(!ordertask.getAction_status().equals("") && ordertask.getAction_status()!=null && !ordertask.getAction_status().equals("null")){
							int elementStatId= -1;
							for (int k =0 ;k <mStatusTypeArryListId.size();k++){
								if (mStatusTypeArryListId.get(k).equals(ordertask.getAction_status()))
									elementStatId = k;
							}
							channel.setAdressid(mStatusTypeArryList.get(elementStatId));  // Tree Status
						}

						infoMap.put(i+2000, channel);

						preparemapshowmarker(Double.parseDouble(spltarray[0]),
								Double.parseDouble(spltarray[1]),
								i+2000,"0",orderStatus,typeOfMap, "", zoom, treeStatus,0);
				}
                            }
			}
		}
	}


	// For Customer Site Shown with Poles
	public void showCustSitePoles(HashMap<Long, Site> sitedatalist, int showWayPnt)
	{
		boolean mapZoom = false;
		if(showWayPnt==1){
			mapZoom = true;
		}

		if (sitedatalist!= null && sitedatalist.size()>0)
		{
			orderxmlKeys = orderxml.keySet().toArray(new Long[orderxml.size()]); // Order Keys

			custSitexmlKeys = sitedatalist.keySet().toArray(new Long[sitedatalist.size()]);

			for(int i=0;i<sitedatalist.size();i++)
			{
				typeOfMap = "tasks_pole";

				Site custSite = (Site) sitedatalist.get(custSitexmlKeys[i]);

				String orderStatus = "";

				if(maptype.equals("OrderList")){
					Long customerId = custSite.getCid();
					for(int j=0; j<orderxml.size(); j++)
					{
						Order order = (Order) orderxml.get(orderxmlKeys[j]);
						if(customerId==order.getCustomerid()){
							orderStatus = String.valueOf(order.getStatusId());  break;
						}
					}
				}

				if(custSite!=null){
					String siteName = custSite.getNm();
					String siteNew = siteName.toLowerCase();
					long siteTid = custSite.getTid();
					//if(siteNew.contains("*default")){

                    int iconToShow = getIconToShow(siteTid);

                    if(siteTid == 901){
                        //YD use DTL data to show the drawable
                        String dtl = custSite.getDetail();
                        String[] geoArr =  dtl.split("\\|");

                        breadcrumbs_counter = 0;
                        mapOfAcesPth = new HashMap<Integer, String>();

                        for (int j=0; j < geoArr.length; j++)
                        {
                            String geo = geoArr[j];
                            if (geo!=null && !geo.equals("") && !geo.equals("null")){
                                mapOfAcesPth.put(j+10000, geo);

                                String geoPoles[] = geo.split(",");

                                preparemapshowmarker(Double.parseDouble(geoPoles[0].trim()), Double.parseDouble(geoPoles[1].trim()), j+10000,
                                        "1", orderStatus, "accesspath", "", mapZoom, "", iconToShow);
                                breadcrumbs_counter ++;
                            }
                        }
                        total_accessPath = mapOfAcesPth.size(); // total Way Point
                    }
                    else{
                        String geo = custSite.getGeo();
                        if (geo!=null && geo!=""){
                            String geoPoles[] = geo.split(",");
                            preparemapshowmarker(Double.parseDouble(geoPoles[0].trim()), Double.parseDouble(geoPoles[1].trim()),
                                    i+70000,"0", orderStatus, typeOfMap, siteNew, mapZoom, "", iconToShow);

                            Channel channel = new Channel();
                            channel.setO_nameid(String.valueOf(custSite.getNm())); // get Contact Name after discussion
                            infoMap.put(i+70000, channel);
                        }
                    }

                    //YD 2020 Commenting as implementing new logic for custsite now.
					/*if(siteTid == 90003){
						String geo = custSite.getGeo();
						if (geo!=null && geo!=""){
							String geoPoles[] = geo.split(",");

							preparemapshowmarker(Double.parseDouble(geoPoles[0].trim()), Double.parseDouble(geoPoles[1].trim()),
									i+70000,"0", orderStatus, typeOfMap, siteNew, mapZoom, "");

							Channel channel = new Channel();
							channel.setO_nameid(String.valueOf(custSite.getNm())); // get Contact Name after discussion
							infoMap.put(i+70000, channel);
						}
					//}else if (siteNew.contains("*pole")) {
					}else if (siteTid == 90002) {
						String geo = custSite.getGeo();
						if (geo!=null && geo!=""){
							String geoPoles[] = geo.split(",");

//								if(orderStatus.equals("")){
//								arrayPoints.add(new LatLng(Double.parseDouble(geoPoles[0].trim()), Double.parseDouble(geoPoles[1].trim())));
//							}else if(orderStatus.equals("0") || orderStatus.equals("1") || orderStatus.equals("2") || orderStatus.equals("3")){
//								arrayPoints.add(new LatLng(Double.parseDouble(geoPoles[0].trim()), Double.parseDouble(geoPoles[1].trim())));
//							}

							preparemapshowmarker(Double.parseDouble(geoPoles[0].trim()), Double.parseDouble(geoPoles[1].trim()),
									i+70000,"0", orderStatus,typeOfMap, siteNew, mapZoom, "");

							Channel channel = new Channel();
							channel.setO_nameid(String.valueOf(custSite.getNm()));// putting Tree Type here important
							infoMap.put(i+70000, channel);
						}
					}else if(siteNew.contains("*access point")){// YD (imp:recheck) may be access point is only one in the list_cal of site
						String geo = custSite.getGeo();
						if (geo!=null && geo!=""){
							String geoPoles[] = geo.split(",");

							access_point_geo = geo;
							accessPointId  = custSite.getId();

							if(accesspath_lat_long !=null && accesspath_lat_long!=""){
								// if user drag access point
								String geoNew[] = accesspath_lat_long.split(",");
								preparemapshowmarker(Double.parseDouble(geoNew[0].trim()), Double.parseDouble(geoNew[1].trim()),
										i+70000,"0", orderStatus,typeOfMap, siteNew, mapZoom, "");
							}else{
							preparemapshowmarker(Double.parseDouble(geoPoles[0].trim()), Double.parseDouble(geoPoles[1].trim()),
									i+70000,"0", orderStatus,typeOfMap, siteNew, mapZoom, "");
							}

							Channel channel = new Channel();
							channel.setO_nameid(String.valueOf(custSite.getNm()));// putting Tree Type here important
							infoMap.put(i+70000, channel);
						}
					}
					if(showWayPnt==1){
						//if(siteNew.contains("*breadcrumbs")){
						if(siteTid == 90001){
							waypointId  = custSite.getId();//YD add for saving waypoint in existing list_cal
							String dtl = custSite.getDetail();
							String[] geoArr =  dtl.split("\\|");

							breadcrumbs_counter = 0;
							mapOfAcesPth = new HashMap<Integer, String>();

							for (int j=0; j < geoArr.length; j++)
							{
								String geo = geoArr[j];
								if (geo!=null && !geo.equals("") && !geo.equals("null")){
									mapOfAcesPth.put(j+10000, geo);

									String geoPoles[] = geo.split(",");

									preparemapshowmarker(Double.parseDouble(geoPoles[0].trim()), Double.parseDouble(geoPoles[1].trim()), j+10000, "1", orderStatus, "accesspath", "", mapZoom, "");
									breadcrumbs_counter ++;
								}
							}
							total_accessPath = mapOfAcesPth.size(); // total Way Point
						}
					}*/
				}
			}

			// settin polyline in the map
			/*polylineOptions = new PolylineOptions();
			polylineOptions.color(Color.GRAY);
			polylineOptions.width(3);
			polylineOptions.addAll(arrayPoints);
			map.addPolyline(polylineOptions);	*/
		}
	}

    private int getIconToShow(long siteTid) {

	    switch ((int) siteTid){
            case 900:
                return R.drawable.tree;

            case 901:
                return R.drawable.dot;

            case 902:
                return R.drawable.tower;

            case 903:
                return R.drawable.house;

            case 904:
                return R.drawable.power_line;

            case 905:
                return R.drawable.street_light;

            case 906:
                return R.drawable.antenna;

            case 907:
                return R.drawable.pole;

            case 908:
                return R.drawable.traffic_light;

            case 909:
                return R.drawable.transformer;

            case 910:
                return R.drawable.solar_panel;

            case 911:
                return R.drawable.tree;

            case 912:
                return R.drawable.parking_meter;

            case 913:
                return R.drawable.sewage;

            case 914:
                return R.drawable.pipe_valve;

            case 915:
                return R.drawable.oil_pump;

            case 916:
                return R.drawable.server;

            case 917:
                return R.drawable.pin_square;

            case 918:
                return R.drawable.pin_round;

            default:
                return R.drawable.pin_round;
        }
    }

    private void showOrderAssets(HashMap<Long, Assets> assetsLst, boolean b) {
		try
		{
			if(assetsLst !=null && assetsLst.size()>0){
				typeOfMap = "assets";
				int i = 0;

				for(Long keys : assetsLst.keySet() ) {
					Assets orderAst = assetsLst.get(keys);

					String geo = orderAst.getGeoLoc();
					String spltarray[] = geo.split(",");

					Channel channel = new Channel();
					channel.setOrderId(String.valueOf(orderAst.getId()));
					/*channel.setTaskId(((HashMap<Long, AssetsType>) DataObject.assetsTypeXmlDataStore).get(orderAst.getTid()).getName());//YD tid

					if (orderAst.getStatus()!=null && !orderAst.getStatus().equals("") && siteTypeList.get(Long.valueOf(orderAst.getStatus()))!=null) {
						channel.setAdressid(orderAst.getStatus());//YD saving status id
						channel.setOrderStatus(siteTypeList.get(Long.valueOf(orderAst.getStatus())).getNm());//YD status
					}*///YD 2020
					infoMap.put(i + 90000, channel);
					int edition = PreferenceHandler.getPrefEditionForGeo(mActivity);
					int imageToShow = getIconToShow(edition);

					if (spltarray!=null && spltarray.length>1)
					{
						preparemapshowmarker(Double.parseDouble(spltarray[0]),
								Double.parseDouble(spltarray[1]),
								i + 90000, "0", "" + activeOrderObj.getStatusId(), typeOfMap, "", b, ""/*orderAst.getStatus()*/,imageToShow);//YD 2020
					}
					i++;
				}
			}
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void showOrdersOnmap()
	{
		if(orderxml !=null)
		{
			typeOfMap = "orders";

			orderxmlKeys = orderxml.keySet().toArray(new Long[orderxml.size()]);

			for(int i=0;i<orderxml.size();i++)
			{
				Order order = (Order) orderxml.get(orderxmlKeys[i]);
				String geo = order.getCustSiteGeocode();
				String spltarray[] = geo.split(",");

				Channel channel = new Channel();
				channel.setCid(i);
				channel.setOrderId(String.valueOf(order.getId()));
				channel.setO_nameid(order.getNm());
				channel.setCustomer(order.getCustName());
				channel.setAdressid(order.getCustSiteStreeAdd());
				infoMap.put(i, channel);

				if (maptype.equals("OrderList"))
				{
					preparemapshowmarker(Double.parseDouble(spltarray[0]), Double.parseDouble(spltarray[1]),
							i,"1",""+order.getStatusId(),typeOfMap ,"", true, "",0);

					String cid  = String.valueOf(order.getCustomerid()); // getting Customer Id

					if(allPoleMap !=null){
						Long[] mKeys = allPoleMap.keySet().toArray(new Long[allPoleMap.size()]);

						for(int m=0; m<allPoleMap.size(); m++){

							Site custSite = (Site) allPoleMap.get(mKeys[m]);

							if(custSite==null)
								break;

							String custid = String.valueOf(custSite.getCid()); //getting Customer Id

							if(custid!=null && custid.length()>0){
								if(cid.equals(custid))
								{
									tempSiteMap.put(mKeys[m], custSite);
								}
							}
						}
					}
				}
			}
		}
	}


	/*private void preparetionAccessPathMapmarkershow() {
		if (accesspathpoints!=null)
		{
			String details[] = accesspathpoints.split("#");

			if(details!=null)
			{
				if(details[0]!=null){
					String lstOfPath[]= details[0].split("\\|");
					listOfAcesPth = new ArrayList<String>();
					for (int z=0; z<lstOfPath.length;z++)
					{
						listOfAcesPth.add(lstOfPath[z]);
					}
				}
				if(details[2] !=null)
				{
					String geoLst[] = details[2].split(",");
					if(geoLst!=null)
						preparemapshowmarker(Double.parseDouble(geoLst[0].trim()),
								Double.parseDouble(geoLst[1].trim()),
								0,"0","","orders","", true);
				}
				if(details[1] !=null)
				{
					String treegeoLst[] = details[1].split(",");
					if(treegeoLst!=null && treegeoLst.length>1)
						preparemapshowmarker(Double.parseDouble(treegeoLst[0].trim()),
								Double.parseDouble(treegeoLst[1].trim()),
								0+2000,"0","","tasks_tree", "", true);
				}
			}

			showaccesspathpoints();
		}

	}
	 */

	// YD MY showing waypoint using this
	/*	private void showaccesspathpoints()
	{
		for (int i=0; i<listOfAcesPth.size(); i++)
		{
			typeOfMap = "accesspath";
			String geoLst[] = listOfAcesPth.get(i).split(",");
			if (geoLst.length>1)

				preparemapshowmarker(Double.parseDouble(geoLst[0].trim()),
						Double.parseDouble(geoLst[1].trim()),
						i+10000,"0","",typeOfMap, "", true);

		}
	}*/


	private void preparemapshowmarker(double lat, double lng, int i, String focusSet, String orderStat, String typeOfMap, String siteType,
                                      boolean mapZoom, String treeStat, int drawableImage) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		LatLng latlng = null;
		try{
		latlng = new LatLng(lat, lng);
		if (typeOfMap.equals("orders")){
			/*if(orderStat.equals("")){
				if(Edition < 600){
					Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
							.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.balloon_b, ""))));
					builder.include(kiel.getPosition());
				}*//*else{
					if(maptype.equals("TreeList")){
						Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
								.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.triangle, ""))));
						builder.include(kiel.getPosition());
					}
				}*//*
			}
			else if (orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				if(Edition < 600)	{
					Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
							.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.balloon_b, ""))));
					builder.include(kiel.getPosition());
				}*//*else{
					if(maptype.equals("TreeList")){
						Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
								.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.triangle, ""))));
						builder.include(kiel.getPosition());
					}else{
						Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
								.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.triangle, ""))));
						builder.include(kiel.getPosition());
					}
				}*//*


			}
			else if (Integer.parseInt(orderStat) > 3){
				if(Edition < 600){
					Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
							.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.balloon_grey, ""))));
					builder.include(kiel.getPosition());
				}*//*else{
					if(maptype.equals("TreeList")){
						Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
								.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.triangle, ""))));
						builder.include(kiel.getPosition());
					}
				}*//*
			}*/
			Marker kiel = map.addMarker(new MarkerOptions().position(latlng).draggable(true).title(String.valueOf(i)).visible(true)
					.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.access_point, ""))));
			builder.include(kiel.getPosition());
		}
		else  if (typeOfMap.equals("tasks_pole")){
			if(orderStat.equals("")){
                Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
                        .icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(drawableImage, ""))));
                builder.include(kiel.getPosition());
				/*if(siteType.contains("*pole")){
					Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
							.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.tower, ""))));
					builder.include(kiel.getPosition());
				}else if(siteType.contains("*default")){
					Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
							.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.home_b, ""))));
					builder.include(kiel.getPosition());
				}else if(siteType.contains("*access point")){  // Put Access Point New Icon
					if(maptype.equals("TreeList")){
						*//*Marker kiel = map.addMarker(new MarkerOptions().position(latlng).draggable(true).title(String.valueOf(i)).visible(true)
								.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.access_point, ""))));
						builder.include(kiel.getPosition());*//*//YD COMMENTING because making 2 taskpole one for order and one for site accespoint
					}else {
						Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
								.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.access_point, ""))));
						builder.include(kiel.getPosition());
					}
				}*/
			}else if(orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				if(siteType.contains("*pole")){
					Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
							.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.tower, ""))));
					builder.include(kiel.getPosition());
				}else if(siteType.contains("*default")){
					Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
							.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.home_b, ""))));
					builder.include(kiel.getPosition());
				}else if(siteType.contains("*access point")){  // Put Access Point New Icon
					Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
							.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.access_point, ""))));
					builder.include(kiel.getPosition());
				}
			}
		}
		else  if (typeOfMap.equals("tasks_tree")){
			int treeIcon = R.drawable.tree_map;

			if(treeStat==null ){
				treeIcon = R.drawable.tree_map;// Open
			}else if(treeStat.equals("0") || treeStat.equals("null")|| treeStat.equals("")){
				treeIcon = R.drawable.tree_map;// Refusal or Review
			}else if(treeStat.equals("2") || treeStat.equals("3")){
				treeIcon = R.drawable.tree_red;// Refusal or Review
			}else if(treeStat.equals("4")){
				treeIcon = R.drawable.tree_blue; // Closed
			}else if(treeStat.equals("9")){
				treeIcon = R.drawable.tree_grey; // Closed
			}else if(treeStat.equals("5") || treeStat.equals("6")){
				treeIcon = R.drawable.tree_grey;// No Tree or False Detection
			}

			if(orderStat.equals("")){
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(treeIcon, ""))));
				builder.include(kiel.getPosition());
			}else if(orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(treeIcon, ""))));
				builder.include(kiel.getPosition());
			}
		}
		else  if (typeOfMap.equals("tasks_path")){
			if(orderStat.equals("")){
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.dot, ""))));
				builder.include(kiel.getPosition());
			}else if(orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.dot, ""))));
				builder.include(kiel.getPosition());
			}
		}else  if (typeOfMap.equals("tasks_path_complete")){
			if(orderStat.equals("")){
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.gray_dot_accpath, ""))));
				builder.include(kiel.getPosition());
			}else if(orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.gray_dot_accpath, ""))));
				builder.include(kiel.getPosition());
			}
		}
		else  if (typeOfMap.equals("accesspath")){
			if(orderStat.equals("")){
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.dot, ""))));
				builder.include(kiel.getPosition());
			}else if(orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.dot, ""))));
				builder.include(kiel.getPosition());
			}
		}
		else if (typeOfMap.equals("assets")){
            Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(drawableImage, ""))));
            builder.include(kiel.getPosition());

			/*if (PreferenceHandler.getPrefEditionForGeo(mActivity)==900) {// show assest icon
				int treeIconAst ;// YD green for null and open
				if (treeStat.equals("8")) {
					treeIconAst = R.drawable.tree_new_1;
				} else if (treeStat.equals("9")) {
					treeIconAst = R.drawable.tree_new_1;// No Tree or False Detection
				} else if (treeStat != null ) {
					treeIconAst = R.drawable.tree_new_3; // Closed
				}else if ( treeStat.equals("") || treeStat.equals("0") || treeStat.equals("null")) {
					treeIconAst = R.drawable.tree_new_3; // Closed
				} else{
					treeIconAst = R.drawable.tree_new_4;
				}
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(treeIconAst, ""))));
				builder.include(kiel.getPosition());
			}else if (PreferenceHandler.getPrefEditionForGeo(mActivity)<900 && PreferenceHandler.getPrefEditionForGeo(mActivity) >300) {// show assest icon
				int treeIconAst ;// YD green for null and open
				if (treeStat.equals("8")) {
					treeIconAst = R.drawable.box_1;
				} else if (treeStat.equals("9")) {
					treeIconAst = R.drawable.box_2;// No Tree or False Detection
				} else if (treeStat != null && treeStat.equals("")) {
					treeIconAst = R.drawable.box_3; // Closed
				} else{
					treeIconAst = R.drawable.box_4;
				}
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(treeIconAst, ""))));
				builder.include(kiel.getPosition());
			}else {// show tree iconif(treeStat.equals("0")){
				int treeIconAst ;// YD green for null and open
				if (treeStat.equals("8")) {
					treeIconAst = R.drawable.pin_1;
				} else if (treeStat.equals("9")) {
					treeIconAst = R.drawable.pin_2;// No Tree or False Detection
				} else if (treeStat != null && treeStat.equals("")) {
					treeIconAst = R.drawable.pin_3; // Closed
				} else{
					treeIconAst = R.drawable.pin_4;
				}
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(treeIconAst, ""))));
				builder.include(kiel.getPosition());
			}*///YD 2020
		}

		if(maptype.equals("TreeList")) {
			if(mapZoom)
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18));
		}
		else{
			if(mapZoom)
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));
		}

		i++;
		}
		catch(Exception e){
			Log.i(BaseTabActivity.LOGNAME, "exception in preparemapshowmarker in googlemap fragment");
		}
	}


	public static void setMapOrderXMLGM(HashMap<Long, Order> xml)// YD filled at the time of getorders for OLMP
	{
		orderxml = xml;
		orderxmlversion++;
	}

	public static void setMapTasksXMLGM(HashMap<Long, OrderTask> xml)
	{
		tasksxml = xml;
		tasksxmlversion++;
	}

	public static void setMapAllTasksXMLGM(HashMap<Long, OrderTask> xml)
	{
		alltasksxml = xml;
		alltasksxmlversion++;
	}

	public static void setMapCustSiteXMLGM(HashMap<Long, Site> xml)
	{
		custSitexml = xml;
		custSitexmlversion++;
	}

	/*public static void setMapAllSiteXMLGM(HashMap<Long, Site> xml)
	{
		allSitexml = xml;
		allSitexmlversion++;
	}*/

	public static void setMapTasktypeXMLGM(HashMap<Long,ServiceType> xml)
	{
		taskTypexml = xml;
	}

	public static void setMapOrderSiteTypeGM(HashMap<Long, SiteType> xml)
	{
		siteTypexml = xml;
	}



	@Override
	public void headerClickListener(String callingId) {
		if(callingId.equals(BaseTabActivity.HeaderMapSettingPressed)){
			//PopupMenu popupSetting = new PopupMenu(mActivity, mActivity.mBtnMapSetting_tree);
			//Inflating the Popup using xml file
			//popupSetting.getMenuInflater().inflate(R.menu.map_setting_menu, popupSetting.getMenu());


			final PopupWindow popview = Utilities.show_Map_popupmenu(getActivity(),mActivity.headerApp.getHeight());
			View view = popview.getContentView();
			TextView menu_normal = (TextView)view.findViewById(R.id.menu_normal);
			TextView menu_satellite = (TextView)view.findViewById(R.id.menu_satellite);
			TextView menu_hybrid = (TextView)view.findViewById(R.id.menu_hybrid);
			//TextView menu_offline = (TextView)view.findViewById(R.id.menu_offline);
			TextView menu_offline_imagery = (TextView)view.findViewById(R.id.menu_offline_imagery);
			menu_offline_imagery.setText("Grid");

			menu_normal.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					popview.dismiss();
				}
			});
			menu_satellite.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
					popview.dismiss();
				}
			});
			menu_hybrid.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
					popview.dismiss();
				}
			});
			/*menu_offline.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popview.dismiss();
					Fragment fragment = BaseTabActivity.mStacks.get(Utilities.JOBS).elementAt(
							BaseTabActivity.mStacks.get(Utilities.JOBS).size() - 2);
					if ( fragment!=null	&& fragment instanceof MapAllFragment){
						mActivity.popFragments(BaseTabActivity.UI_Thread);
					}
					else {

						BaseTabActivity.addedNewMap=true;

						Bundle mBundles =new Bundle();
						mBundles.putString("OrderId", String.valueOf(activeOrderObj.getId()));
						mBundles.putString("OrderName", String.valueOf(activeOrderObj.getNm()));

						MapAllFragment.maptype = "TreeList";

						MapAllFragment mFragment = new MapAllFragment();
						mFragment.setArguments(mBundles);
						mFragment.setActiveOrderObject(activeOrderObj);
						mActivity.pushFragments(Utilities.JOBS, mFragment, true, true,BaseTabActivity.UI_Thread);
					}
				}
			});*/
			menu_offline_imagery.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popview.dismiss();
					String headerText = PreferenceHandler.getAssetsHead(mActivity);//mBundle.getString("HeaderText");
					if (headerText!=null && !headerText.equals(""))
						headerText = headerText+"S";
					else
						headerText = "ASSETS";// YD just for backup

					BaseTabActivity.addedNewMap=true;

					OrderAssetFragment assetFragment = new OrderAssetFragment();
					Bundle assetBundle = new Bundle();
					assetBundle.putString("OrderId", String.valueOf(activeOrderObj.getId()));
					assetBundle.putString("HeaderText", String.valueOf(headerText));
					assetBundle.putString("OrderName", activeOrderObj.getNm());
					assetFragment.setArguments(assetBundle);
					assetFragment.setActiveOrderObject(activeOrderObj);
					mActivity.pushFragments(Utilities.JOBS, assetFragment, true, true, BaseTabActivity.UI_Thread);

				}
			});

			//popupSetting.getMenu().findItem(R.id.map_on_off_line_Img).setVisible(true);
			//popupSetting.getMenu().findItem(R.id.map_on_off_line_Img).setTitle("Grid");//TODO arcgis issue


			//registering popup with OnMenuItemClickListener
		/*	popupSetting.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem items) {
					// TODO Auto-generated method stub
					if(items.getTitle().equals("Normal")){
						map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					}
					else if(items.getTitle().equals("Satellite"))
					{
						map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
					}
					else if(items.getTitle().equals("Hybrid"))
					{
						map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
					}
					else if(items.getTitle().equals("Offline"))
					{
						Fragment fragment = BaseTabActivity.mStacks.get("Jobs").elementAt(
								BaseTabActivity.mStacks.get("Jobs").size() - 2);
						if ( fragment!=null	&& fragment instanceof MapAllFragment){
							mActivity.popFragments(BaseTabActivity.UI_Thread);
						}
						else {

							BaseTabActivity.addedNewMap=true;

							Bundle mBundles =new Bundle();
							mBundles.putString("OrderId", String.valueOf(activeOrderObj.getId()));
							mBundles.putString("OrderName", String.valueOf(activeOrderObj.getNm()));

							MapAllFragment.maptype = "TreeList";

							MapAllFragment mFragment = new MapAllFragment();
							mFragment.setArguments(mBundles);
							mFragment.setActiveOrderObject(activeOrderObj);
							mActivity.pushFragments(Utilities.JOBS, mFragment, true, true,BaseTabActivity.UI_Thread);
						}
					}
					else if(items.getTitle().equals("Grid"))
					{
						String headerText = PreferenceHandler.getAssetsHead(mActivity);//mBundle.getString("HeaderText");
						if (headerText!=null && !headerText.equals(""))
							headerText = headerText+"S";
						else
							headerText = "ASSETS";// YD just for backup

						BaseTabActivity.addedNewMap=true;

						OrderAssetFragment assetFragment = new OrderAssetFragment();
						Bundle assetBundle = new Bundle();
						assetBundle.putString("OrderId", String.valueOf(activeOrderObj.getId()));
						assetBundle.putString("HeaderText", String.valueOf(headerText));
						assetBundle.putString("OrderName", activeOrderObj.getNm());
						assetFragment.setArguments(assetBundle);
						assetFragment.setActiveOrderObject(activeOrderObj);
						mActivity.pushFragments(Utilities.JOBS, assetFragment, true, true, BaseTabActivity.UI_Thread);
					}
					return true;
				}
			});
*/
			//popupSetting.show();



		}else if(callingId.equals(BaseTabActivity.HeaderlastSyncPressed)){
			if(maptype.equals("OrderList")){
				mActivity.onBackPressed();
			}else if(maptype.equals("TreeList")){
				OrderFormsFragment orderDetailTreeFragment = new OrderFormsFragment();
				mActivity.OrderTaskBackOdrId = Long.valueOf(currentOdrId);

				Bundle taskBundle=new Bundle();
				taskBundle.putString("OrderId", String.valueOf(currentOdrId));
				taskBundle.putString("OrderName", String.valueOf(currentOdrName));
				orderDetailTreeFragment.setArguments(taskBundle);
				orderDetailTreeFragment.setActiveOrderObj(activeOrderObj);
				mActivity.pushFragments(Utilities.JOBS, orderDetailTreeFragment, true, true,BaseTabActivity.UI_Thread);
			}
		}else if(callingId.equals(BaseTabActivity.HeaderTaskUserPressed)){
			if(maptype.equals("TreeList")){
				mActivity.onBackPressed();
			}
		}else if (callingId.equals(BaseTabActivity.HeaderPlusPressed)){//YD
			if(!btnUpdateShow){		// savechange button is active(means orange)
				/*AddEditTaskOrderFragment addCustomerOrderFragment = new AddEditTaskOrderFragment();
				Bundle bundle = new Bundle();
				bundle.putString("TreeType", "ADD TASK");
				bundle.putString("OrderId", currentOdrId);
				bundle.putString("OrderName", currentOdrName);
				addCustomerOrderFragment.setArguments(bundle);
				addCustomerOrderFragment.setActiveOrderObj(activeOrderObj);
				mActivity.pushFragments(Utilities.JOBS, addCustomerOrderFragment, true, true,BaseTabActivity.UI_Thread);*/
				/*AddEditAssetFragment_OLD addEditAssetFragment = new AddEditAssetFragment_OLD();
				Bundle bundle = new Bundle();
				bundle.putString("AssetType", "ADD ASSET");
				bundle.putString("OrderId", currentOdrId);
				bundle.putString("OrderName", currentOdrName);
				addEditAssetFragment.setArguments(bundle);
				addEditAssetFragment.setActiveOrderObject(activeOrderObj);
				mActivity.pushFragments(Utilities.JOBS, addEditAssetFragment, true, true,BaseTabActivity.UI_Thread);
			*///YD 2020
                showHeaderDialog();
			}else{
				isDialogForUpdate = true;
				dialog = new MyDialog(mActivity, getResources().getString(R.string.lbl_tree_updates), getResources().getString(R.string.lbl_upd_message), "YES");
				dialog.setkeyListender(new MyDiologInterface() {
					@Override
					public void onPositiveClick() throws JSONException {
						isDialogForUpdate = false;
						btnUpdateShow=false;
						FIELD_UPDATED=0;
						dialog.dismiss();

						AddEditTaskOrderFragment addCustomerOrderFragment = new AddEditTaskOrderFragment();
						Bundle bundle = new Bundle();
						bundle.putString("TreeType", "ADD TASK");
						bundle.putString("OrderId", currentOdrId);
						bundle.putString("OrderName", currentOdrName);
						addCustomerOrderFragment.setArguments(bundle);
						addCustomerOrderFragment.setActiveOrderObj(activeOrderObj);
						mActivity.pushFragments(Utilities.JOBS, addCustomerOrderFragment, true, true,BaseTabActivity.UI_Thread);
					}

					@Override
					public void onNegativeClick() {
						if(access_point_geo!=null && accesspath_lat_long!=null)
						{
							String arrTemp[] = access_point_geo.split(",");
							String arrGeo[] = accesspath_lat_long.split(",");

							if(!arrTemp[0].equals(arrGeo[0]) && !arrTemp[1].equals(arrGeo[1])){
								saveAccessPointLocation(accesspath_lat_long, odr_customerId, accessPointId); // Save Access Point
							}
						}

						if(mapTmpAcesPath!=null && mapTmpAcesPath.size()>0 || mapOfAcesPth!=null && mapOfAcesPth.size()!= total_accessPath){
							saveNewSiteForWaypoints(lstAccPathStr, odr_customerId, waypointId); // Save Waypoints
						}

						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								mWebview.setVisibility(View.VISIBLE);
							}
						});
						dialog.dismiss();
					}
				});
				dialog.onCreate(null);
				dialog.show();
			}
		}
		else if(callingId.equals(BaseTabActivity.HeaderBackPressed))
		{
			if(btnUpdateShow || geoMainSiteChange){
				isDialogForUpdate_OnBack = true;
				dialog = new MyDialog(mActivity, getResources().getString(R.string.lbl_tree_updates), getResources().getString(R.string.update_message), "YES");
				dialog.setkeyListender(new MyDiologInterface() {
					@Override
					public void onPositiveClick() throws JSONException {
						// No Button Click
						btnUpdateShow=false;
						isDialogForUpdate_OnBack = false;
						FIELD_UPDATED=0;
						goBack(BaseTabActivity.SERVICE_Thread);
						dialog.dismiss();
					}

					@Override
					public void onNegativeClick() {
						// Yes Button Click

						if (geoMainSiteChange) {
							geoMainSiteChange = false;//YD request to change order main site change using drag functionality
							EditSiteReq req = new EditSiteReq();
							req.setAction(Site.ACTION_EDIT_SITE);
							req.setId(Long.valueOf(activeOrderObj.getCustSiteId()));
							req.setGeo(geoMainSite);
							req.setIsOnlyUpdate("true");
							Site.getData(req, mActivity, GoogleMapFragment.this, SAVESITE_ORDER);//YD adding i because sending multiple requests
						}
						if (btnUpdateShow){
							btnUpdateShow = false;
							if (access_point_geo != null && accesspath_lat_long != null) {
								String arrTemp[] = access_point_geo.split(",");
								String arrGeo[] = accesspath_lat_long.split(",");

								if (!arrTemp[0].equals(arrGeo[0]) && !arrTemp[1].equals(arrGeo[1])) {
									saveAccessPointLocation(accesspath_lat_long, odr_customerId, accessPointId); // Save Access Point
								}
							}

							if (mapTmpAcesPath != null && mapTmpAcesPath.size() > 0 || mapOfAcesPth != null && mapOfAcesPth.size() != total_accessPath) {
								saveNewSiteForWaypoints(lstAccPathStr, odr_customerId, waypointId); // Save Waypoints
							}
						}
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								mWebview.setVisibility(View.VISIBLE);
							}
						});
						dialog.dismiss();

					}
				});
				dialog.onCreate(null);
				dialog.show();
			}else{
				bottom_show.setVisibility(View.GONE);

				if(mapTmpAcesPath!=null && mapTmpAcesPath.size()>0 && mapOfAcesPth.size()>0){
					map.clear();
					lstAccPathStr ="";
					breadcrumbs_counter=0;
					mapTmpAcesPath.clear();
					mapOfAcesPth.clear();
				}


				if(access_point_geo!=null && accesspath_lat_long!=null){
					accesspath_lat_long ="";
				}

				btnSaveChanges.setEnabled(false);
				final int sdk = Build.VERSION.SDK_INT;
				if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
					btnSaveChanges.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_logwaypoint_selector));
				} else {
					btnSaveChanges.setBackground(getResources().getDrawable(R.drawable.btn_logwaypoint_selector));
				}
				goBack(BaseTabActivity.SERVICE_Thread);
			}
		}
		else if(callingId.equals(BaseTabActivity.HeaderDonePressed))
		{
			if (geoMainSiteChange){
				geoMainSiteChange = false;//YD request to change order main site change using drag functionality
				EditSiteReq req = new EditSiteReq();
				req.setAction(Site.ACTION_EDIT_SITE);
				req.setId(Long.valueOf(activeOrderObj.getCustSiteId()));
				req.setGeo(geoMainSite);
				req.setIsOnlyUpdate("true");
				Site.getData(req, mActivity, this, SAVESITE_ORDER);
			}

			//YD updating the breadcrumb of the order
			if(mapTmpAcesPath!=null && mapTmpAcesPath.size()>0 || mapOfAcesPth!=null && mapOfAcesPth.size()!= total_accessPath && mapOfAcesPth.size()>0){
				saveNewSiteForWaypoints(lstAccPathStr, odr_customerId, waypointId); // Save Waypoints
				if(shouldDelSite)//YD because when one breadcrumb is added and also deleted then shoulddelSite get true so now as we have added new breadcurmb so now no need to make delete req
					shouldDelSite = false;
			}
			else if ((mapOfAcesPth == null || mapOfAcesPth.size()==0) && shouldDelSite){
				//YD delete the site with name Breadcrumbs
				shouldDelSite = false;
				EditSiteReq req = new EditSiteReq();
				req.setAction(Site.ACTION_DELETE_SITE);
				req.setId(waypointId);

				Site.deleteData(req, mActivity, GoogleMapFragment.this, DELETE_SITE);
			}

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//mWebview.setVisibility(View.VISIBLE);
				}
			});
		}
        else if(callingId.equals(BaseTabActivity.HeaderSwapButton))
        {
            BaseTabActivity.addedNewMap=true;

            OrderAssetFragment assetFragment = new OrderAssetFragment();
            Bundle assetBundle = new Bundle();
            assetBundle.putString("OrderId", String.valueOf(activeOrderObj.getId()));
            assetBundle.putString("HeaderText", String.valueOf(headerText));
            assetBundle.putString("OrderName", activeOrderObj.getNm());
            assetFragment.setArguments(assetBundle);
            assetFragment.setActiveOrderObject(activeOrderObj);
            mActivity.pushFragments(Utilities.JOBS, assetFragment, true, true, BaseTabActivity.UI_Thread);

        }
	}

    private void showHeaderDialog() {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity) mActivity, android.R.layout.select_dialog_item, optionLst);
            AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mActivity, AlertDialog.THEME_HOLO_LIGHT);
            builder.setCancelable(true);
            builder.setTitle(Html.fromHtml("<font color='#10c195'><b>Select Option</b></font>"));
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @SuppressLint("NewApi")
                public void onClick(DialogInterface dialog, int position) {
                    SiteType siteType = siteTypeArrayList.get(position);

                    AddEditAssetFragment addEditAssetFragment = new AddEditAssetFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("formData", siteType.getDtl());
                    bundle.putLong("fid", Long.valueOf(siteType.getId()));
                    bundle.putString("AssetType", "ADD ASSET");
                    bundle.putString("OrderId", currentOdrId);
                    bundle.putString("OrderName", currentOdrName);
                    addEditAssetFragment.setArguments(bundle);
                    addEditAssetFragment.setActiveOrderObject(activeOrderObj);
                    mActivity.pushFragments(Utilities.JOBS, addEditAssetFragment, true, true, BaseTabActivity.UI_Thread);


                }
            });
            builder.setNegativeButton(Html.fromHtml("<font color='#34495f'><b>Cancel</b></font>"), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            final AlertDialog dialog = builder.create();
            Utilities.setAlertDialogRow(dialog, mActivity);
            dialog.show();
            Utilities.setDividerTitleColor(dialog, mheight, mActivity);

            Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            Utilities.setDefaultFont_12(button_negative);
            Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Utilities.setDefaultFont_12(button_positive);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/*public static void setOrderJsonForTreeMap(Order odrObj) {
		activeOrderObj = odrObj;
	}*/

	public static void setAccesspathpointsForTreeMap(String pathPoint) {

		accesspathpoints = pathPoint;
	}


	// To Do By Mandeep
	private void parseallSiteXML()
	{
		if(allSitexml!=null)
		{
			allSitexmlKeys = allSitexml.keySet().toArray(new Long[allSitexml.size()]);

			for(int m=0; m<allSitexml.size(); m++)
			{
				Site custSite = (Site) allSitexml.get(allSitexmlKeys[m]);
				if(custSite!=null)
				{
					String temp = custSite.getNm();
					String temp1 = temp.toLowerCase();
					if (temp1.contains("*pole") || temp1.contains("*default") || temp1.contains("*access point")||temp1.contains("*breadcrumbs"))
					{
						allPoleMap.put(allSitexmlKeys[m], custSite);
					}
				}
			}
		}
	}


	public void setActiveOrderObject(Order orderObj)
	{
		activeOrderObj = orderObj;
	}


	@Override
	public void onActionOk(int requestCode) {
		if(maptype.equals("TreeList")){
			lstAccPathStr="";
			/*	int id2Delete = 0;
			if (AccPathId2Delete>=10000 && AccPathId2Delete<20000)
			{
				id2Delete = AccPathId2Delete-10000;
			}
			listOfAcesPth.remove(id2Delete);*/

			//	Selectedmarker.remove(); // remove selected marker from map

			mapOfAcesPth.remove(AccPathId2Delete);
			map.clear();

			int i=0;//YD below is the code to make new list_cal which should be used when done is pressed to save breadcrumbs/ access path
			Iterator<Entry<Integer,String>> iter = mapOfAcesPth.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Integer, String> entry = iter.next();

				String geo[] = mapOfAcesPth.get(entry.getKey()).split(",");
				if(i==0)
					lstAccPathStr = mapOfAcesPth.get(entry.getKey());
				else
					lstAccPathStr= lstAccPathStr+"|"+mapOfAcesPth.get(entry.getKey());

				preparemapshowmarker(Double.parseDouble(geo[0]), Double.parseDouble(geo[1]), entry.getKey(), "", "",
                        "accesspath", "", false, "",0);
				i++;
			}

			if(AccPathId2Delete>=10000 && AccPathId2Delete<20000 && mapTmpAcesPath!=null && mapTmpAcesPath.size()>0){
				Iterator<Entry<Integer,Integer>> itrTemp = mapTmpAcesPath.entrySet().iterator();
				while (itrTemp.hasNext()) {//YD checking if the id deleted also in mapTmpAcesPath, if it is then delete it
					Entry<Integer,Integer> entry = itrTemp.next();

					int pathId = entry.getValue();
					if(pathId==AccPathId2Delete){
						itrTemp.remove();
						break;
					}
				}

				if(mapTmpAcesPath.size()==0 && mapOfAcesPth.size()==total_accessPath && accesspath_lat_long==null){
					btnUpdateShow = false;
					FIELD_UPDATED=0;
				}else{
					btnUpdateShow = true;
					FIELD_UPDATED=1;
				}
			}
			else{
				btnUpdateShow = true;
				FIELD_UPDATED=1;
			}

			if (mapOfAcesPth.size()==0 && (mapTmpAcesPath==null || mapTmpAcesPath.size()==0))
				shouldDelSite = true;//YD to delete the site

			/*	for (int i=0; i<listOfAcesPth.size(); i++)
			{
				String geo[] = listOfAcesPth.get(i).split(",");

				if(i==0)
					lstAccPathStr = listOfAcesPth.get(i);
				else
					lstAccPathStr= lstAccPathStr+"|"+listOfAcesPth.get(i);

				preparemapshowmarker(Double.parseDouble(geo[0]), Double.parseDouble(geo[1]), i+10000, "", "", "accesspath", "", false);
			}*/
			showOrder(false);
			showOrderAssets(assetsLst, false);
			showCustSitePoles(custSitexml, 0);
			//showOrderTrees(tasksxml, false);//YD no need to show task currently as we have made it for asset

			if(btnUpdateShow){
				mActivity.replaceBtnHeader("plusDone");
				/*btnSaveChanges.setEnabled(true);
				btnSaveChanges.setBackground(getResources().getDrawable(R.drawable.btn_save_update_selector));*/
			}else{
				mActivity.replaceBtnHeader("donePlus");
				/*btnSaveChanges.setEnabled(false);
				btnSaveChanges.setBackground(getResources().getDrawable(R.drawable.btn_logwaypoint_selector));*/
			}

			AccPathId2Delete=0;
		}
	}

	@Override
	public void onActionCancel(int requestCode) {
		// TODO Auto-generated method stub
		AccPathId2Delete=0;
	}

	@Override
	public void onActionNeutral(int requestCode) {
		// TODO Auto-generated method stub
		AccPathId2Delete=0;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		// adding Waypoint on current GPS Location
		case R.id.btnLogwayPoint:
			String selectedPoint = Utilities.getLocation(mActivity.getApplicationContext());

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mWebview.setVisibility(View.VISIBLE);
				}
			});

			Log.e("Current GPS :", String.valueOf(selectedPoint));

			if(selectedPoint!=null && !selectedPoint.toLowerCase().contains("null") && !selectedPoint.equals("")){
				String geo[] = selectedPoint.toString().split(",");
				if(!geo[0].equals("0.0") || !geo[1].equals("0.0"))
				{
					btnSaveChanges.setEnabled(true);
					btnSaveChanges.setBackground(getResources().getDrawable(R.drawable.btn_save_update_selector));

					lstAccPathStr="";
					FIELD_UPDATED = 1;
					btnUpdateShow=true;

					if(mapOfAcesPth==null)
						mapOfAcesPth = new HashMap<Integer, String>();

				//	initAccPathLstLen = mapOfAcesPth.size();
					mapOfAcesPth.put(breadcrumbs_counter+10000, selectedPoint);

					for(int i=0; i <mapOfAcesPth.size(); i++){
						if(i==0)
							lstAccPathStr = mapOfAcesPth.get(i);
						else
							lstAccPathStr = lstAccPathStr+"|"+mapOfAcesPth.get(i);
					}
					preparemapshowmarker(Double.parseDouble(geo[0]), Double.parseDouble(geo[1]), breadcrumbs_counter+10000, "0",
                            "", "accesspath", "", false,"",0);

					if(mapTmpAcesPath==null){
						mapTmpAcesPath = new HashMap<Integer, Integer>();
					}

					mapTmpAcesPath.put(breadcrumbs_counter+10000, breadcrumbs_counter+10000);

					breadcrumbs_counter ++;

					// zoom camara on Current Location
					//	LatLng myCoordinates = new LatLng(Double.parseDouble(geo[0]), Double.parseDouble(geo[1]));
					//	map.animateCamera(CameraUpdateFactory.newLatLngZoom(myCoordinates, 12));
				}
			}else{
				String title = getResources().getString(R.string.lbl_breadcrumb);
				String message = getResources().getString(R.string.lbl_breadcrumb_message);
				dialog = new MyDialog(mActivity, title, message, "OK");
				dialog.setkeyListender(new MyDiologInterface() {

					@Override
					public void onPositiveClick() throws JSONException {
						dialog.dismiss();
					}

					@Override
					public void onNegativeClick() {
						dialog.dismiss();
					}
				});
				dialog.onCreate(null);
				dialog.show();
			}

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Handler handler = new Handler();
					handler.postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							mWebview.setVisibility(View.GONE);
						}
					}, 500);
				}
			});

			break;

		case R.id.btnSaveChanges:
			//mapTmpAcesPath.clear();

			if(access_point_geo!=null && accesspath_lat_long!=null)
			{
				String arrTemp[] = access_point_geo.split(",");
				String arrGeo[] = accesspath_lat_long.split(",");

				if(!arrTemp[0].equals(arrGeo[0]) && !arrTemp[1].equals(arrGeo[1])){
					saveAccessPointLocation(accesspath_lat_long, odr_customerId, accessPointId); // Save Access Point
				}
			}

			if(mapTmpAcesPath!=null && mapTmpAcesPath.size()>0 || mapOfAcesPth!=null && mapOfAcesPth.size()!= total_accessPath){
				saveNewSiteForWaypoints(lstAccPathStr, odr_customerId, waypointId); // Save Waypoints
			}

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mWebview.setVisibility(View.VISIBLE);
				}
			});
			break;
		default:
			break;

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

    private void setGenoptionList() {
	    optionLst.clear();
	    siteTypeArrayList.clear();
        //siteGenTypeMapTid_10 = BaseTabActivity.siteGenTypeMapTid_10;//YD if any issue comes with data then need to get data from db and used it
        if(siteGenTypeMapTid_9 != null) {
            LinkedHashMap<Long, SiteType> sortedMap = sortSiteTypeListOnFormNames(siteGenTypeMapTid_9);
            for (Entry<Long, SiteType> entry : sortedMap.entrySet()) {
                String name = entry.getValue().getNm();
                optionLst.add(name);
                siteTypeArrayList.add(entry.getValue());
            }
        }
    }

    private LinkedHashMap<Long, SiteType> sortSiteTypeListOnFormNames(HashMap<Long, SiteType> siteGenTypeMapTid_10) {

        LinkedHashMap<Long, SiteType> sortedMap = new LinkedHashMap<Long, SiteType>();

        // Convert Map to List
        List<Entry<Long, SiteType>> list =
                new LinkedList<Entry<Long, SiteType>>(siteGenTypeMapTid_10.entrySet());

        // Sort list_cal with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Entry<Long, SiteType>>() {
            public int compare(Entry<Long, SiteType> o1,
                               Entry<Long, SiteType> o2) {
                return o1.getValue().getNm().compareTo(o2.getValue().getNm());
            }
        });

        // Convert sorted map back to a Map
        for (Iterator<Entry<Long, SiteType>> it = list.iterator(); it.hasNext(); ) {
            Entry<Long, SiteType> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

	@Override
	public void setResponseCBActivity(Response response) {
		if (response!=null)
		{
			if (response.getStatus().equals("success")&&
					response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED)))
			{
				if(response.getId()==GET_SITE_REQ){

					if(maptype.equals("OrderList")){
						allSitexml = (HashMap<Long, Site>)response.getResponseMap();

						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								checkforOrderDataLoading();
							}
						});
					}

					if(maptype.equals("TreeList")){
						custSitexml = (HashMap< Long, Site>)response.getResponseMap();

						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showCustSitePoles(custSitexml, 1);
							}
						});
					}
				}

				if(response.getId()==GET_TASKS){
					tasksxml = (HashMap<Long, OrderTask>)response.getResponseMap();

					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							showOrderTrees(tasksxml, true);
						}
					});
				}

				if(response.getId()==GET_ASSETS){
					assetsLst = (HashMap<Long, Assets>)response.getResponseMap();
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							showOrderAssets(assetsLst, true);
						}
					});
				}
				if (response.getId()==GETSITETYPE)
				{
					if (response.getResponseMap()!=null) {
                        siteTypeList = (HashMap<Long, SiteType>) response.getResponseMap();
                        DataObject.siteTypeXmlDataStore = response.getResponseMap();
                        siteGenTypeMapTid_9 = new HashMap<Long, SiteType>();
                        siteAndGenTypeList = (HashMap<Long, SiteType>) response.getResponseMap();

                        Long keys[] = siteAndGenTypeList.keySet().toArray(new Long[siteAndGenTypeList.size()]);
                        for (int i = 0; i < siteAndGenTypeList.size(); i++) {
                            int tid = siteAndGenTypeList.get(keys[i]).getTid();
                            switch (tid) {
                                case 9:
                                    siteGenTypeMapTid_9.put(keys[i], siteAndGenTypeList.get(keys[i]));
                            }
                        }
                        setGenoptionList();
                    }
					getAsset();
				}

				if (response.getId()==SAVEORDERFIELD_ACCESSPATH_GEO){
					activeOrderObj.setCustSiteGeocode(accesspath_lat_long);
				}
				if (response.getId()==SAVESITE_ORDER){
					activeOrderObj.setCustSiteGeocode(geoMainSite);//YD updating the order site in the order object
					//mActivity.replaceBtnHeader("donePlus");
					FIELD_UPDATED=0;
					stopLoading();
				}
				if (response.getId()==DELETE_SITE) {
					shouldDelSite = false;
					waypointId = 0;
					total_accessPath = mapOfAcesPth.size();
					stopLoading();
					btnUpdateShow= false;
				}

				if(response.getId()==SAVESITE_WAY_PATH || response.getId()==SAVESITE_ACCESS_POINT){ //YD waypath breadcrumbs and accesspoint is access pole
					if (response.getId()==SAVESITE_ACCESS_POINT){
						updateOrderRequest req = new updateOrderRequest();
						req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
						req.setType("post");
						req.setId(String.valueOf(activeOrderObj.getId()));
						req.setName("accessPathGeo");
						req.setValue(accesspath_lat_long);
						req.setAction(Order.ACTION_SAVE_ORDER_FLD);

						Order.saveOrderField(req, mActivity, GoogleMapFragment.this, SAVEORDERFIELD_ACCESSPATH_GEO);
					}
					if (response.getId()==SAVESITE_WAY_PATH){
						total_accessPath = mapOfAcesPth.size();
						if (waypointId ==0)
							getCustSites(odr_customerId);
					}

					stopLoading();

					btnUpdateShow= false;
					FIELD_UPDATED=0; 
					if(isDialogForUpdate){  //YD when user press yes to save changes once clicked on plus button
						isDialogForUpdate = false;
						if(isEditTree==0){	
							AddEditTaskOrderFragment addCustomerOrderFragment = new AddEditTaskOrderFragment();				
							Bundle bundle = new Bundle();
							bundle.putString("TreeType", "ADD TASK");
							bundle.putString("OrderId", currentOdrId);
							bundle.putString("OrderName", currentOdrName);			
							addCustomerOrderFragment.setArguments(bundle);		
							addCustomerOrderFragment.setActiveOrderObj(activeOrderObj);
							mActivity.pushFragments(Utilities.JOBS, addCustomerOrderFragment, true, true,BaseTabActivity.UI_Thread);
						}else{
							isEditTree=0;
							if (Edition > EDN_FOR_DETAIL_TASK) {	
								if (odrTaskObj != null) {
									Bundle bundle = new Bundle();
									bundle.putString("TreeType", "EDIT TASK");
	
									bundle.putString("OrderId",
											String.valueOf(odrTaskObj.getId()));
									Order order = ((HashMap<Long, Order>) DataObject.ordersXmlDataStore)
											.get(Long.valueOf(odrTaskObj.getId()));
									bundle.putString("OrderName", order.getNm());
	
									bundle.putLong("Id",
											odrTaskObj.getOrder_task_id());
									bundle.putLong("ServiceId",
											odrTaskObj.getTask_id());
									bundle.putLong("SiteId", Long
											.parseLong(odrTaskObj.getTree_type()));
									bundle.putLong("PriorityId", Long
											.parseLong(odrTaskObj.getPriority()));
									bundle.putString("Status", String
											.valueOf(odrTaskObj.getAction_status()));
									if (odrTaskObj.getTree_owner() != null)
										bundle.putString("Owner",
												odrTaskObj.getTree_owner());
									else
										bundle.putString("Owner", "");
									bundle.putString("EstimatedCount", String
											.valueOf(odrTaskObj.getTree_expcount()));
									bundle.putString("ActualCount", String
											.valueOf(odrTaskObj
													.getTree_actualcount()));
									bundle.putString("TreeHeight",
											String.valueOf(odrTaskObj.getTree_ht()));
									bundle.putString("DiameterBH", String
											.valueOf(odrTaskObj.getTree_dia()));
									bundle.putString("HvClearance",
											String.valueOf(odrTaskObj
													.getTree_clearence()));
									bundle.putString("Cycle", String
											.valueOf(odrTaskObj.getTree_cycle()));
									bundle.putString("Hours",
											String.valueOf(odrTaskObj
													.getTree_timespent()));
									bundle.putString("TandM",
											String.valueOf(odrTaskObj.getTree_tm()));
									bundle.putString("OT", String
											.valueOf(odrTaskObj.getTree_msc()));
									bundle.putString("TreeComment", String
											.valueOf(odrTaskObj.getTree_comment()));
									bundle.putString("PrescirptionComment", String
											.valueOf(odrTaskObj.getTree_pcomment()));
									bundle.putString("Alerts", String
											.valueOf(odrTaskObj.getTree_alert()));
									bundle.putString("Note", String
											.valueOf(odrTaskObj.getTree_note()));
									bundle.putString("AccessComplexity", String
											.valueOf(odrTaskObj.getTree_ct1()));
									bundle.putString("SetupComplexity", String
											.valueOf(odrTaskObj.getTree_ct2()));
									bundle.putString(
											"PrescriptionComplexity",
											String.valueOf(odrTaskObj.getTree_ct3()));
									bundle.putString("AccessPath", String
											.valueOf(odrTaskObj
													.getTree_accesspath()));
									bundle.putString("TreeGeo", String
											.valueOf(odrTaskObj.getTree_geo()));
	
									mActivity.OrderEditTaskBackOdrId = Long
											.valueOf(odrTaskObj.getId());
									AddEditTaskOrderFragment addCustomerOrderFragment = new AddEditTaskOrderFragment();
									addCustomerOrderFragment.setArguments(bundle);
									mActivity.pushFragments(Utilities.JOBS,
											addCustomerOrderFragment, true, true,
											BaseTabActivity.UI_Thread);
								}
							}						
						}
					}else if(isDialogForUpdate_OnBack){ //YD when user press on back button and coming back after saving the data.
						isDialogForUpdate_OnBack = false;
						goBack(BaseTabActivity.SERVICE_Thread);						
					}
				}
			}
			else if(response.getStatus().equals("success")&&
					response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
			{
				if(response.getId()==SAVESITE_WAY_PATH || response.getId()==SAVESITE_ORDER){//YD not checked yet
					stopLoading();
				}
			}
		}
	}

	private void stopLoading() {

		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Handler handler = new Handler();// YD hiding loading after one second (Client requirement)
				handler.postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						//mWebview.setVisibility(View.GONE);//YD stop showing loading bar
						mActivity.replaceBtnHeader("donePlus");
									/*btnSaveChanges.setEnabled(false);
									final int sdk = android.os.Build.VERSION.SDK_INT;
									if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
										btnSaveChanges.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_logwaypoint_selector));
									} else {
										btnSaveChanges.setBackground(getResources().getDrawable(R.drawable.btn_logwaypoint_selector));
									}	*/
					}
				}, 1000);
			}
		});
	}

	public void loadDataOnBack(BaseTabActivity mActivity) {
		mActivity.registerHeader(this);  //YD
		btnSaveChanges.setEnabled(false);			
		btnSaveChanges.setBackground(getResources().getDrawable(R.drawable.btn_logwaypoint_selector));
		
		SupportMapFragment f = null;
		if (((SupportMapFragment) getFragmentManager()
				.findFragmentById(R.id.google_fr_map))!=null){
			 f = (SupportMapFragment) getFragmentManager()
					.findFragmentById(R.id.google_fr_map);
			if (f != null){
				getFragmentManager().beginTransaction().show(f).commit();
			}
		}
		if (map!=null)
		{	
			mActivity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Log.e(BaseTabActivity.LOGNAME, "Clearing Map in google treeMap");
					BaseTabActivity.setHeaderTitle("", headerText, "");
					map.clear();
				}
			});
			
			
		}
		if(maptype.equals("OrderList"))   // OverAll map
		{
			if(Edition > EDN_FOR_DETAIL_TASK){
				getAllSites();
			} else if(Edition < EDN_FOR_REGULAR_TASK){
				showOrdersOnmap();	
			}
		}
		else if(maptype.equals("TreeList")){ 	// Order map
			Order order = ((HashMap<Long, Order>)DataObject.ordersXmlDataStore).get(Long.valueOf(currentOdrId));

			waypointId = 0;
			accessPointId = 0;

			odr_customerId = order.getCustomerid();

			if(Edition > EDN_FOR_DETAIL_TASK){
				showOrder(true);
				getCustSites(odr_customerId);
				getSiteOrGenType();
				//getTasks(currentOdrId);

				//checkforTasksDataLoading();
				//	checkforAccessPathDataLoading();
			}else if(Edition < EDN_FOR_REGULAR_TASK){
				showOrder(true);
			}
		}
		
		if(activeOrderObj!=null){
			//total_tree_count.setText(String.valueOf(activeOrderObj.getCustServiceCount()));  // MY Show total tree count  //YD 2020 ordertask is not in app anymore
		}else{
			total_tree_count.setText("");
		}
		super.onResume();
		
	}
	
	private void goBack(int threadType) {
		mActivity.popFragments(threadType);
	}
}


