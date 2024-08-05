package com.aceroute.mobile.software.fragment;

import android.app.Activity;
import android.content.Context;
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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aceroute.mobile.software.AceRouteApplication;
import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.GPSTracker;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.SpeedTestLauncher;
import com.aceroute.mobile.software.async.IActionOKCancel;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderTask;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.ServiceType;
import com.aceroute.mobile.software.component.reference.Site;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.offline.SurfaceViewMap;
import com.aceroute.mobile.software.offline.application.Channel;
import com.aceroute.mobile.software.requests.CommonSevenReq;
import com.aceroute.mobile.software.requests.EditSiteReq;
import com.aceroute.mobile.software.requests.SaveSiteRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.simple.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MapFragment extends BaseFragment implements RespCBandServST, LocationListener, HeaderInterface, OnClickListener, IActionOKCancel, SensorEventListener {

	public final static String MAP_JSON = "json_string";
	public static GoogleMap map = null;

	// callbacks
	int SAVEORDERFIELD_STATUS_PG =1;
	int SAVESITE_ACCESS_POINT = 2;
	int SAVESITE_WAY_PATH = 5;	
	private int GET_SITE_REQ = 3;
	static int GET_TASKS=4;
	int SAVEORDERFIELD_SITE_GEO =6;
	static int SAVESITE_ORDER = 100; //YD vvvvvimp because using the variable as a incremented value see from where the code is reqeusting

	private int EDN_FOR_DETAIL_TASK = 599; // For Deatil Task Screen
	private int EDN_FOR_REGULAR_TASK = 600; // For Regular Task Screen

	HashMap<Integer, Channel> infoMap = new HashMap<Integer, Channel>();
	JSONArray jarray = null;

	//public BaseTabActivity mActivity;
	//public static String maptype;
//	private CustomDialog customDialog;

	public static String lstAccPathStr;
	//	ArrayList<String> listOfAcesPth = null;
//	private ArrayList<Integer> arrlstTemPath = null;
//	private HashMap<Integer, Integer> mapTemPath = null;
//	private boolean btnUpdateShow = false;

	private HashMap<Integer, String> mapOfAcesPth = null;
	private HashMap<Integer, Integer> mapTmpAcesPath = null;
	private static int total_accessPath;
//	private int initAccPathLstLen=0;

	String typeOfMap="";
	String orderIDForTasklst="0";
	private Marker Selectedmarker=null;
	int Edition;
	protected int AccPathId2Delete;	
	LatLng crntLocationLatLng = null;


	private String currentOdrId, currentOdrName;
	private TextView popup_odrtm ,popup_order_name, popup_order_Id, popup_customer_name, popup_address;
//	private Button btnLogwayPoint, btnSaveChanges;	
	private ImageView btn_zoom, btn_editList;
//	private RelativeLayout bottom_show;
//	private WebView mWebview_G;

	private Long[] orderxmlKeys;
	private Long[] tasksxmlKeys;
	private Long[] custSitexmlKeys;
	private Long[] siteTypexmlKeys;
	private Long[] allSitexmlKeys;

	HashMap<Long, Site> siteMapCurrentOdrs = new HashMap<Long, Site>();
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

	static Order OrderJsonTreeMap = null;
	static String accesspathpoints=null;

//	private ArrayList<LatLng> arrayPoints = null;
	PolylineOptions polylineOptions;

	String annotatOdr_OrderId="0";
	String annotatOdr_OrderNm="";

	private static long waypointId = 0;
	private static long accessPointId = 0;
	private long odr_customerId = 0;
	private String access_point_geo = null;

	private String accesspath_lat_long = null;
	static String annotatOdr_TaskId= "";

	static String mapScreen = "";
	Location currentLocation=null;
	com.google.android.gms.maps.MapView mapView_G;
	public HashMap<String,String> changedOdrLocMap = new HashMap<String, String>();
	int changedOrder=0;
	int internetSpeedrequestId = 101;

	private static final String TAG = "MapActivity";


	private enum MapOption {
		MAP_DISPLAY, ROUTING_AND_NAVIGATION, ANNOTATIONS
	}
	public static final int TRACKS = 1;
	// callbacks
	int SAVEORDERFIELD_STATUS_PG_SK =1;
	int SAVESITE_ACCESS_POINT_SK = 2;
	int SAVESITE_WAY_PATH_SK = 5;
	private int GET_SITE_REQ_SK = 3;
	static int GET_TASKS_SK=4;

	private int EDN_FOR_DETAIL_TASK_SK = 599; // For Deatil Task Screen
	private int EDN_FOR_REGULAR_TASK_SK = 600; // For Regular Task Screen

	/**
	 * Current option selected
	 */
	private MapOption currentMapOption = MapOption.MAP_DISPLAY;	
	/**
	 * Ids for alternative routes
	 */
	private List<Integer> routeIds = new ArrayList<Integer>();
	//  Application context object
	private AceRouteApplication app;
	//  Current position provider

	//Options menu
	private View menu;
	//Tells if heading is currently active
	private boolean headingOn;
	//The map popup view
	//Custom callout view title
//	private TextView popupTitleView;
	// Custom callout view description
	private TextView popupDescriptionView;
	// Custom view for adding an annotation
	private TextView popup_order_time_SK, popup_order_name_SK, popup_order_Id_SK, popup_customer_name_SK, popup_address_SK;
	private RelativeLayout customView;
	Map<Integer, Channel> channelMap = new TreeMap<Integer, Channel>();
	static String annotatOdr_TaskId_SK= "";
	//Tells if a navigation is ongoing
	private boolean navigationInProgress;
	String response;
	//object annotation

	private Button positionMeButton,headingButton,bottomButton,// btnLogwayPoint_SK, btnSaveChanges_SK,
	popup_anno_btn, popup_anno_btn_cam;	

	ImageView backbtn , buttonClose, Bt_editList, popup_zoom_btn;
	private String currentOdrId_SK, currentOdrName_SK;
//	private RelativeLayout bottom_show_SK;
	private WebView mWebview_SK;

//	private CustomDialog customDialog_SK;

//	private int currentUnIDAccPth=0;
	//static HashMap<Long, Order> orderxml_SK = null; YD
	//static long orderxmlversion_SK=0;  YD
	static long orderxmlusedversion_SK=0;

	static HashMap<Long, OrderTask> tasksxml_SK = null;
	static long tasksxmlversion_SK=0;
	static long tasksxmlusedversion_SK=0;

	static HashMap<Long, OrderTask> alltasksxml_SK = null;
	static long alltasksxmlversion_SK=0;
	static long alltasksxmlusedversion_SK=0;

	static HashMap<Long, Site> custSitexml_SK = null;
	static long custSitexmlversion_SK=0;
	static long custSitexmlusedversion_SK=0;   

	static HashMap<Long, Site> allSitexml_SK = null;
	static long allSitexmlversion_SK=0;
	static long allSitexmlusedversion_SK=0;   

	static HashMap<Long,ServiceType> taskTypexml_SK = null;

	static HashMap<Long, SiteType> siteTypexml_SK = null;

	private int Edition_SK;

	static Order OrderJsonTreeMap_SK = null;    
	static String accesspathpoints_SK=null;
	static long OrderJsonTreeMapversion=0;
	static long OrderJsonTreeMapusedversion=0; 
	String annotatOdr_OrderId_SK="0";
	String annotatOdr_OrderNm_SK="";

	SurfaceViewMap surfaceVw;  
	HashMap<Long, Site> tempSiteMap_SK = new HashMap<Long, Site>();
	HashMap<Long, Site> allPoleMap_SK = new HashMap<Long, Site>();

	String orderIDForTasklst_SK="0";
	String typeOfMap_SK="";

	//public static String maptype_SK;

	int counter_GM = 0 ;
	
	//public BaseTabActivity mActivity;
	public static String lstAccPathStr_SK;
//	ArrayList<String> listOfAcesPth;
	private HashMap<Integer, String> mapOfAcesPth_SK = null;
//	private HashMap<Integer, Integer> mapTmpAcesPath_SK = null;
//	private boolean btnUpdateShow_SK = false;	
//	private static int total_accessPath_SK;
	
	protected int AccPathId2Delete_SK;	

	private Long[] orderxmlKeys_SK;
	private Long[] tasksxmlKeys_SK;
	private Long[] custSitexmlKeys_SK;
	private Long[] siteTypexmlKeys_SK;
	private Long[] allSitexmlKeys_SK;

	private boolean showMapMenu = false;
	ImageView currentLocBtn_sk , imageViewNavArrRound;
	private Button positionMeButtonARC,headingButtonARC,bottomButtonARC, btnLogwayPointARC, btnSaveChangesARC;
	private RelativeLayout bottom_showARC;
	ImageView currentLocBtnARC;
	private TextView total_tree_countARC;
	ArrayList<String> mStatusTypeArryListARC, mStatusTypeArryListIdARC ;
	public FrameLayout mViewContainerARC;
	String[] strStatusTypeARC = {"Open", /*"Pending",*/ "Closed","False Detection", "No Tree","Refusal", "Review"};
	String[] strStatusTypeIdsARC = {"0",/*"1",*/ "4", "5", "6","2","3"};
	Map<Integer, Channel> channelMapARC = new TreeMap<Integer, Channel>();
	private static final String DATA_RELATIVE_PATH = "PGE.tpk";
	private boolean btnUpdateShow_ARC = false;
	private boolean isDialogForUpdate_ARC = false;
	private MyDialog dialog_ARC;
	HashMap<Long, Site> tempSiteMap_ARC = new HashMap<Long, Site>();
	public static int FIELD_UPDATED_ARC=0;
	private boolean isDialogForUpdate_OnBack_ARC = false;
	private HashMap<Integer, Integer> mapTmpAcesPath_ARC = null;
	private int breadcrumbs_counter_ARC = 0;
	private Long[] allSitexmlKeys_ARC;
	HashMap<Long, Site> allPoleMap_ARC = new HashMap<Long, Site>();
	private int isEditTree_ARC = 0;
	private Long[] orderxmlKeys_ARC;
	private Long[] tasksxmlKeys_ARC;
	private HashMap<Integer, String> mapOfAcesPth_ARC = null;

	int SAVESITE_ACCESS_POINT_ARC = 2;
	private int GET_SITE_REQ_ARC = 3;
	static int GET_TASKS_ARC=4;
	int SAVESITE_WAY_PATH_ARC = 5;
	/**********************ARCMAP*e********************************/

	public static boolean changeSequenceNoOnOrder= true;//YD added because of -1 issue on marker
	public boolean isDragEnable=true;
    MyDialog dialog;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.e("AceRoute", "onAttach called for MapFragment");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("AceRoute", "onCreate1 called for MapFragment");
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.e("AceRoute", "onActivityCreated called for MapFragment");
	}

    public void showNoInternetDialog(String title, String content) {

        dialog = new MyDialog(mActivity, title, content, "OK");
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
        Utilities.setDividerTitleColor(dialog, 0, mActivity);
		/*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;

		lp.gravity = Gravity.CENTER;
		dialog.getWindow().setAttributes(lp);*/
    }
	
	static View vFrag = null;
	static View vFrag1 = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		Log.e("AceRoute", "onCreateView called for MapFragment");

		if(PreferenceHandler.getUiconfigGeoCorrector(getActivity()).equals("0")){
			isDragEnable=false;
		}
		
		if(Utilities.checkInternetConnection(mActivity,false))
			mapScreen = "google";
		else
            showNoInternetDialog("Maps is Offline" , "Check your network connection to enable online map functionality.");

		if (mapScreen.equals("google") ){

			new SpeedTestLauncher().bindListeners(this, internetSpeedrequestId);
			long lastSyncTime = PreferenceHandler.getlastsynctime(mActivity);
			Date date = new Date(Long.valueOf(lastSyncTime));

			if (PreferenceHandler.getOdrGetDate(mActivity)!=null && !PreferenceHandler.getOdrGetDate(mActivity).equals(""))// YD showing custom date for which order has been downloaded
				date  =  new Date(PreferenceHandler.getodrLastSyncForDt(mActivity));

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d yyyy");//Jun 21 2015
			String headerDate = simpleDateFormat.format(date);
			String headerTime = Utilities.convertDateToAmPM(date.getHours(), date.getMinutes());
			final String headerDay = BaseTabActivity.getDayFrmDate(date.getDay());
			mActivity.setHeaderTitle4OLMP(headerDate,"",headerDay);
			Edition = PreferenceHandler.getPrefEditionForGeo(getActivity());
			
			Log.i(BaseTabActivity.LOGNAME, "Map is active and creating new view.");
			// YD removing view by its parent
			if (vFrag != null) {
		        ViewGroup parent = (ViewGroup) vFrag.getParent();
		        if (parent != null)
		            parent.removeView(vFrag);
		    } 
			try {
				vFrag = inflater.inflate(R.layout.map_view, container, false);
				mapView_G = ((com.google.android.gms.maps.MapView) vFrag.findViewById(R.id.gmap_fr_map));
				mapView_G.onCreate(savedInstanceState);
				map = mapView_G.getMap();
				vFrag1 = vFrag;

		    } catch (InflateException e) {
		    	if(vFrag1!=null && vFrag1!=vFrag)
		    	{
		    		vFrag=vFrag1;
		    	}
		    	else
		    	{
					vFrag1=vFrag;
		    		Utilities.log(mActivity, "View handles, vFrag1 = "+vFrag1+"vFRag = "+vFrag);
		    		e.printStackTrace();
		    	}
		    } 
			
			//vFrag = inflater.inflate(R.layout.map_view, container, false);		
			mActivity.registerHeader(this);  //YD
		//	arrayPoints = new ArrayList<LatLng>();
		
			//if (map == null) {
				//map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.gmap_fr_map)).getMap();//YD commented later then below line
				//map = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.gmap_fr_map)).getMap();

				if(map!=null){
					map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					MapsInitializer.initialize(this.getActivity());//YD intializing the map for mapView class
					map.setMyLocationEnabled(true);
					map.getUiSettings().setCompassEnabled(true);
					map.getUiSettings().setAllGesturesEnabled(true);
				}else{
					final Bundle savedInstanceStateFinal = savedInstanceState;
					Toast.makeText(getActivity(), "else part", Toast.LENGTH_LONG).show();
					final Handler mHandler = new Handler();
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							//map = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.gmap_fr_map)).getMap();
							mapView_G = ((com.google.android.gms.maps.MapView) vFrag.findViewById(R.id.gmap_fr_map));
							mapView_G.onCreate(savedInstanceStateFinal);
							map = mapView_G.getMap();

							if (map != null) {
								map.setMyLocationEnabled(true);
								map.getUiSettings().setMyLocationButtonEnabled(false);
								map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
								map.setMyLocationEnabled(true);
								map.getUiSettings().setCompassEnabled(true);
								map.getUiSettings().setAllGesturesEnabled(true);

							} else mHandler.post(this);
						}
					});
			}
			//}

			GPSTracker mGPS = new GPSTracker(mActivity);
			if (mGPS.canGetLocation) {
				currentLocation = mGPS.getLocation();
				if(currentLocation != null && currentLocation.getLatitude()<1 && currentLocation.getLongitude()<1){
					currentLocation.setLatitude(36.7783);
					currentLocation.setLongitude(119.4179);
				}
			} else {		
				System.out.println("Unable");
			}
	
			try {		
				
				map.clear();
				if(Edition > EDN_FOR_DETAIL_TASK){
					getAllSites_G();
				} else if(Edition < EDN_FOR_REGULAR_TASK){
					showOrdersOnmap_G(mActivity);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// YD using this for waypoint to add .
			try {
				/*map.setOnMapLongClickListener(new OnMapLongClickListener(){
					@Override
					public void onMapLongClick(LatLng point) {
						if (maptype.equals("TreeList") && Edition > EDN_FOR_DETAIL_TASK){

							btnUpdateShow = true;
							lstAccPathStr="";

							if(mapOfAcesPth==null){
								mapOfAcesPth = new HashMap<Integer, String>();
							}

							initAccPathLstLen = mapOfAcesPth.size();
							mapOfAcesPth.put(initAccPathLstLen+10000, Double.parseDouble(String.valueOf(point.latitude))+","+Double.parseDouble(String.valueOf(point.longitude)));

							int i=0;
							Iterator<Map.Entry<Integer,String>> iter = mapOfAcesPth.entrySet().iterator();
							while (iter.hasNext()) {
								Entry<Integer, String> entry = iter.next();
								if(i==0)
									lstAccPathStr = mapOfAcesPth.get(entry.getKey());
								else
									lstAccPathStr= lstAccPathStr+"|"+mapOfAcesPth.get(entry.getKey());
								i++;
							}
							preparemapshowmarker_G(Double.parseDouble(String.valueOf(point.latitude)), Double.parseDouble(String.valueOf(point.longitude)), initAccPathLstLen+10000, "0", "", "accesspath", "", false);

							if(mapTmpAcesPath==null){
								mapTmpAcesPath = new HashMap<Integer, Integer>();
							}

							mapTmpAcesPath.put(initAccPathLstLen+10000, initAccPathLstLen+10000);

							btnSaveChanges.setEnabled(true);
							btnSaveChanges.setBackground(getResources().getDrawable(R.drawable.btn_save_update_selector));
						}
					}
				}); */

				map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
					@Override
					public void onMarkerDragStart(Marker marker) {
						// TODO Auto-generated method stub					
						Log.e("System out", "onMarkerDragStart..."+marker.getPosition().latitude+"..."+marker.getPosition().longitude);
					}
	
					@Override
					public void onMarkerDragEnd(Marker markerPoint) {
					Log.e("System out", "DragEnd@..." + markerPoint.getPosition().latitude + "..." + markerPoint.getPosition().longitude);
						String uniqueId  = markerPoint.getTitle();
						if (uniqueId!= null && Integer.valueOf(uniqueId)<1000){//YD <1000 means for order
							Channel info = infoMap.get(Integer.valueOf(markerPoint
									.getTitle()));

							changedOdrLocMap.put(info.getOrderId(), markerPoint.getPosition().latitude + "," + markerPoint.getPosition().longitude);//YD if the location changed twice then there is no need to check as hashmap will handle automatically
							if (changedOdrLocMap.size()>=1) {
								mActivity.mBtnDone.setVisibility(View.VISIBLE);
								mActivity.no_of_order.setVisibility(View.GONE);
							}
						}
					}
	
					@Override
					public void onMarkerDrag(Marker arg0) {
						// TODO Auto-generated method stub					
						Log.e("System out", "Drag@..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
					}				
				});
	

				map.setOnMarkerClickListener(new OnMarkerClickListener() {
					@Override
					public boolean onMarkerClick(Marker marker) {

						int zm = (int) map.getCameraPosition().zoom;
						CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude+(double)90/Math.pow(2,zm),marker.getPosition().longitude),zm);
						map.animateCamera(cameraUpdate);
						if (marker.getTitle().equals("Aceroute order")) {
							Log.i(Utilities.TAG,"software order clicked."
									+ marker.isInfoWindowShown());						
							if (marker.isInfoWindowShown())
								marker.hideInfoWindow();
							else
								marker.showInfoWindow();						
						}
	
							Log.i(BaseTabActivity.LOGNAME, "Annotation clicked GM_MF");
							if(Edition > 599){
								if(Integer.valueOf(marker.getTitle())>=10000 && Integer.valueOf(marker.getTitle()) <20000){
									marker.hideInfoWindow();
								}else {
									marker.showInfoWindow();
								}
							}else{
								marker.showInfoWindow();
							}
						//	marker.hideInfoWindow();
						return true;
					}
				});
	
	
				// Marker Window Click Event Handling 
				map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
					@Override
					public void onInfoWindowClick(Marker marker) {
						// TODO Auto-generated method stub
						Channel info = infoMap.get(Integer.valueOf(marker.getTitle()));
	
						if(Integer.valueOf(marker.getTitle()) >=2000 && Integer.valueOf(marker.getTitle()) <10000){     // On Tree Icon Popup Click
							if(Edition > EDN_FOR_DETAIL_TASK){							
								OrderTask odrTaskObj;
	
																
								odrTaskObj = (OrderTask) alltasksxml.get(Long.valueOf(info.getTaskId()));
	
								if(odrTaskObj!=null){
									Log.i(BaseTabActivity.LOGNAME, "Opening tree edit Screen");
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
									mActivity.pushFragments(Utilities.MAP, addCustomerOrderFragment, true, true,BaseTabActivity.UI_Thread);
								}
								else {
									Log.e(BaseTabActivity.LOGNAME, "Found null task object for taskId" + info.getTaskId());
								}
							}
						}			
	
						if(Integer.valueOf(marker.getTitle()) >=0 && Integer.valueOf(marker.getTitle()) <2000){
							if(Edition < EDN_FOR_REGULAR_TASK){
								/*OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
								Bundle mBundle=new Bundle();
								mBundle.putString("OrderId", String.valueOf(info.getOrderId()));
	
								orderDetailFragment.setArguments(mBundle);	    			
								mActivity.pushFragments(Utilities.JOBS, orderDetailFragment, true, true,BaseTabActivity.UI_Thread);*/// YD commented because giving crash 31-05-2016
							}						
						}					
					}				
				});
	
	
				map.setInfoWindowAdapter(new InfoWindowAdapter() {
					@Override
					public View getInfoWindow(Marker marker) {
	
						return null;
					}								
	
					@Override   //YD create the view for the marker popup
					public View getInfoContents(Marker marker) {
						Log.i(BaseTabActivity.LOGNAME, "Google InfoWindow open");
						View view = mActivity.getLayoutInflater().inflate(R.layout.googlemap_layout_popup, null);
						try {					
							Typeface tf = TypeFaceFont.getCustomTypeface(mActivity.getApplicationContext());
							popup_odrtm = (TextView) view.findViewById(R.id.order_time);
							popup_odrtm.setTypeface(tf, Typeface.BOLD);
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
							popup_odrtm.setTextSize(19 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
	
							btn_zoom = (ImageView) view.findViewById(R.id.Bt_zoom);
							btn_editList = (ImageView) view.findViewById(R.id.Bt_editList);
							popup_odrtm.setVisibility(View.GONE);
	
							String title = marker.getTitle();
	
							Log.e("Annotation :", String.valueOf(marker.getTitle()));
	
								if(Edition > EDN_FOR_DETAIL_TASK){
									if(Integer.valueOf(title) >=0 && Integer.valueOf(title) < 2000){ // Orders
										popup_odrtm.setVisibility(View.VISIBLE);     	// OrdersOnmap
										popup_order_Id.setVisibility(View.VISIBLE);
										popup_order_name.setVisibility(View.VISIBLE);
										popup_address.setVisibility(View.VISIBLE);
										popup_customer_name.setVisibility(View.GONE);
									}
									else if(Integer.valueOf(title)>=2000 && Integer.valueOf(title)<10000){ // For Order Show Tree Details
										popup_order_name.setVisibility(View.VISIBLE);
										popup_address.setVisibility(View.VISIBLE);
										btn_editList.setVisibility(View.VISIBLE);
									}else if(Integer.valueOf(title)>=70000 && Integer.valueOf(title) <80000) // For Site Poles Details
									{
										popup_order_name.setVisibility(View.VISIBLE);
									}else{ // Order Details								
										popup_customer_name.setVisibility(View.VISIBLE); //YD commenting for nonpge App
									}  
								}else if(Edition < EDN_FOR_REGULAR_TASK){
									popup_order_name.setVisibility(View.VISIBLE);
									popup_order_Id.setVisibility(View.VISIBLE);
									//btn_zoom.setVisibility(View.VISIBLE);
									if(Integer.valueOf(title) >=0 && Integer.valueOf(title) < 2000){ // Orders //YD adding later for orderPopUp
										popup_odrtm.setVisibility(View.VISIBLE);     	// OrdersOnmap
										popup_order_Id.setVisibility(View.VISIBLE);
										popup_order_name.setVisibility(View.VISIBLE);
										popup_address.setVisibility(View.VISIBLE);
										popup_customer_name.setVisibility(View.GONE);
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
								else
								{
									if (info.getTaskId()!=null)
										annotatOdr_TaskId = info.getOrderId()+"#"+info.getTaskId();
									else 
										annotatOdr_TaskId = info.getOrderId()+"#0";
								}
	
								if(Integer.valueOf(title) >=0 && Integer.valueOf(title) < 2000)
								{
									//if(Edition > EDN_FOR_DETAIL_TASK){ //YD Commenting if else for non-pge App
										//popup_customer_name.setText(info.getCustomer());
									//}else if(Edition < EDN_FOR_REGULAR_TASK){
										popup_odrtm.setText(getOdrTm(info.getOdrStartDt()));
										popup_order_name.setText(info.getO_nameid());
										popup_order_Id.setText(info.getOrderId());
										popup_address.setText(info.getAdressid());
								//	}
								}
	
								if(Integer.valueOf(marker.getTitle()) >=2000 && Integer.valueOf(marker.getTitle()) <10000){
									if(Edition > EDN_FOR_DETAIL_TASK){
										popup_order_name.setText(info.getO_nameid());							
										popup_address.setText(info.getCustomer());
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
			showMapMenu = true;
		}
		return vFrag;
	}

	private String getOdrTm(String odrStartDt) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");	// returns before 12pm : Sat Jun 20 09:30:00 IST 2015 and after 12pm it returns: Sat Jun 20 23:30:00 IST 2015
		String strFromDate = odrStartDt;// 2015-06-20 07:55 -00:00

		Date start = null;
		try {
			start = simpleDateFormat.parse(strFromDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return  Utilities.convertDateToAmPMWithout_Zero(start.getHours(),start.getMinutes());
	}



	private void getAllSites_G() {
		// TODO Auto-generated method stub
		CommonSevenReq req = new CommonSevenReq();
		req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		req.setSource("localonly");
		req.setAction("getallsite");

		Site.getData(req, mActivity, this, GET_SITE_REQ);
	} 

	@Override
	public void onResume() {
		super.onResume();
		if(PreferenceHandler.getUiconfigGeoCorrector(getActivity()).equals("0")){
			isDragEnable=false;
		}
		Log.e("AceRoute", "onResume called for MapFragment");
		if (mapScreen.equals("google")){
			mapView_G.onResume();
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.e("AceRoute", "onDetach called for MapFragment");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Log.e("AceRoute", "onStop called for MapFragment");
	}
	@Override
	public void onStart() {
		super.onStart();
		Log.e("AceRoute", "onStart called for MapFragment");
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.e("AceRoute", "onDestroy called for MapFragment");
		super.onDestroy(); //MY uncommmented because offline map was crashing
	}
	
	@Override
	public void onDestroyView() {
		Log.e("AceRoute", "onDestroyView called for MapFragment");
		super.onDestroyView();
		if (mapScreen.equals("google")){
			/*com.google.android.gms.maps.SupportMapFragment f = (com.google.android.gms.maps.SupportMapFragment) getFragmentManager()
					.findFragmentById(R.id.gmap_fr_map);
			if (f != null) 
			{	
				//getFragmentManager().beginTransaction().hide(f).commit();
				getFragmentManager().beginTransaction().remove(f).commit(); //YD used because creating issue when open map 2nd time (Inflator exception issue was there)
				map=null;
				//getFragmentManager().beginTransaction().remove(f).commit(); //YD earlier using this but causing map to hang up 
			}*/	// YD commenting because now using mapView instead of SupportMapFragment
			
		}
	}


	@Override
	public void onPause() {
		super.onPause();
		Log.e("AceRoute", "onPause called for MapFragment");
		if (mapScreen.equals("google")){
			SupportMapFragment f = (SupportMapFragment) getFragmentManager()
					.findFragmentById(R.id.gmap_fr_map);
			if (f != null)
			{
				getFragmentManager().beginTransaction().hide(f).commit();
			}
			
		}
	}


	@Override
	public void onLocationChanged(Location location) {
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

	private Bitmap writeTextOnDrawable_G(int drawableId, String text) {

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
			paint.setTextSize(convertToPixels_G(context, 20));

			Rect textRect = new Rect();
			paint.getTextBounds(text, 0, text.length(), textRect);

			Canvas canvas = new Canvas(bm);

			// If the text is bigger than the canvas , reduce the font size
			if (textRect.width() >= (canvas.getWidth() - 4))
				paint.setTextSize(convertToPixels_G(context, 7));

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

	public static int convertToPixels_G(Context context, int nDP) {
		final float conversionScale = context.getResources()
				.getDisplayMetrics().density;

		return (int) ((nDP * conversionScale) + 0.5f);

	}

	private void checkforOrderDataLoading_G()
	{    	
		parseallSiteXML_G();
		preparetionordermapmarkershow_G();
	}

	private void preparetionordermapmarkershow_G()
	{ 	
		if (map!=null)
			map.clear();		
		siteMapCurrentOdrs.clear();

		showOrdersOnmap_G(mActivity);
		showTreesAndDots_G(alltasksxml, true);
	}

	private void showOrder_G(boolean mapZoom) {
		// TODO Auto-generated method stub
		if(OrderJsonTreeMap!=null){
			int i=0;
			String geo[] = OrderJsonTreeMap.getCustSiteGeocode().split(",");
			String lat = geo[0];
			String lng = geo[1];
			String oderStat = String.valueOf(OrderJsonTreeMap.getStatusId());

			Channel channel = new Channel();
			channel.setCid(i);
			channel.setOrderId(String.valueOf(OrderJsonTreeMap.getId()));
			channel.setO_nameid(OrderJsonTreeMap.getNm());
			channel.setCustomer(OrderJsonTreeMap.getCustName());
			channel.setAdressid(OrderJsonTreeMap.getCustSiteStreeAdd());				
			infoMap.put(i, channel);				

			preparemapshowmarker_G(Double.parseDouble(lat), Double.parseDouble(lng), i, "1", oderStat, "orders", "", mapZoom ,-1);
		}
	}

	public static int accessPathIncr=0;

	// showing Task and accessPath
	public void showTreesAndDots_G(HashMap<Long, OrderTask> datalist, boolean zoom)//YD only showing trees from here not dots
	{		
		accessPathIncr=0;
		if(datalist !=null)
		{
			tasksxmlKeys = datalist.keySet().toArray(new Long[datalist.size()]);
			orderxmlKeys = orderxml.keySet().toArray(new Long[orderxml.size()]); // Order Keys

			for(int i=0;i<datalist.size();i++)
			{
				typeOfMap = "tasks_tree";
				OrderTask ordertask = (OrderTask) datalist.get(tasksxmlKeys[i]);   	
				if (orderxml.get(ordertask.getId())==null){
					continue;
				}
				
				String geo = ordertask.getTree_geo();
			
				String orderStatus = "";

				for(int j=0; j<orderxml.size(); j++)				
				{
					Order order = (Order) orderxml.get(orderxmlKeys[j]);
					if(ordertask.getId()==order.getId()){//YD changed						
						orderStatus = String.valueOf(order.getStatusId()); break;
					}
				}			

				if (geo!=null && !geo.equals(""))
				{	
					String spltarray[] = geo.split(",");
					if (Double.parseDouble(spltarray[0])!=0 && Double.parseDouble(spltarray[1])!=0)
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
						channel.setAdressid("");					
						channel.setO_nameid(String.valueOf(TreeSpecies)); // Tree Species
						channel.setCustomer("");  

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

						infoMap.put(i+2000, channel);

						preparemapshowmarker_G(Double.parseDouble(spltarray[0]),
								Double.parseDouble(spltarray[1]),
								i+2000,"0",orderStatus,typeOfMap, "", zoom ,-1);
					}
				}

				/*	String accessPath = ordertask.getTree_accesspath();

				if (accessPath!=null && !accessPath.equals(""))
				{	
					String splitPaths[] = accessPath.split("\\|");
					if (status.equals("1"))
						typeOfMap = "tasks_path_complete";
					else 
						typeOfMap = "tasks_path";
					for (int j=0 ; j<splitPaths.length; j++)
					{
						if (i==0 && j==0)
							accessPathIncr = accessPathIncr;
						else
							accessPathIncr = accessPathIncr+1 ;

						String geoAccPath[] = splitPaths[j].split(",");
						if (geoAccPath.length>1)
							preparemapshowmarker(Double.parseDouble(geoAccPath[0].trim()),
									Double.parseDouble(geoAccPath[1].trim()), 
									accessPathIncr+10000,"0",orderStatus,typeOfMap, "", true);
					}
				}*/
			}
		}
		else {
			Log.i(BaseTabActivity.LOGNAME, "No Task Available");
		}
	}

	public void showOrdersOnmap_G(BaseTabActivity mActivity1)
	{
		this.mActivity = mActivity1;
		if(orderxml !=null)			 
		{
			LinkedHashMap<Long, Order> odrDataMapLinkedHM = sortOrders(orderxml,mActivity);

			typeOfMap = "orders";
			String[] lastKnownGeo = null;
			int lastKnownSeqNo = -1;
			orderxmlKeys = odrDataMapLinkedHM.keySet().toArray(new Long[odrDataMapLinkedHM.size()]);

			for(int i=0;i<odrDataMapLinkedHM.size();i++)
			{
				Order order = (Order) odrDataMapLinkedHM.get(orderxmlKeys[i]);
				String geo = order.getCustSiteGeocode();
				String spltarray[] = geo.split(",");

				Channel channel = new Channel();
				channel.setCid(i);
				channel.setOrderId(String.valueOf(order.getId()));
				channel.setO_nameid(order.getNm());
				channel.setCustomer(order.getCustName());

				String streetAdr = order.getCustSiteStreeAdd();
				String suiteAdr = order.getCustSiteSuiteAddress();
				if (suiteAdr!= null && suiteAdr.length()>0)
					streetAdr = suiteAdr+", "+streetAdr;

				channel.setAdressid(streetAdr);
				channel.setOdrStartDt(order.getFromDate());
				infoMap.put(i, channel);

				if ((order.getStatusId()==0 || order.getStatusId()==1 || order.getStatusId()==2 || order.getStatusId()==3))//YD logic to show first open item from list_cal of orders
				{
					if (lastKnownSeqNo==-1 || order.getSequenceNumber()<lastKnownSeqNo){
						lastKnownSeqNo = order.getSequenceNumber();
						lastKnownGeo = spltarray;
					}
				}
				preparemapshowmarker_G(Double.parseDouble(spltarray[0]), Double.parseDouble(spltarray[1]),
						i, "0", "" + order.getStatusId(), typeOfMap, "", false, order.getSequenceNumber());

				//if (maptype.equals("OrderList")){
					/*if (flag4showOdr==1 && (order.getStatusId()==0 || order.getStatusId()==1 || order.getStatusId()==2 || order.getStatusId()==3)){
						flag4showOdr = 0;
						preparemapshowmarker_G(Double.parseDouble(spltarray[0]), Double.parseDouble(spltarray[1]),
								i,"1",""+order.getStatusId(),typeOfMap ,"", true ,order.getSequenceNumber());
					}
					else {
						preparemapshowmarker_G(Double.parseDouble(spltarray[0]), Double.parseDouble(spltarray[1]),
								i, "0", "" + order.getStatusId(), typeOfMap, "", false, order.getSequenceNumber());
					}*/
					String cid  = String.valueOf(order.getCustomerid()); // getting Customer Id

					if(allPoleMap !=null){
						Long[] mKeysPole = allPoleMap.keySet().toArray(new Long[allPoleMap.size()]);

						for(int m=0; m<allPoleMap.size(); m++){

							Site custSite = (Site) allPoleMap.get(mKeysPole[m]);

							if(custSite==null)									
								break;

							String custid = String.valueOf(custSite.getCid()); //getting Customer Id

							if(custid!=null && custid.length()>0){									
								if(cid.equals(custid))
								{										
									siteMapCurrentOdrs.put(mKeysPole[m], custSite);						
								}
							}
						}							
					}				
				//}
			}
			if (lastKnownGeo!=null){
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng( Double.parseDouble(lastKnownGeo[0]),
																			 Double.parseDouble(lastKnownGeo[1])), 13));
			}else{
				if(currentLocation!=null) {
					LatLng latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));
				}
			}
		}
		else {

			//YD setting up the current location if there is no order
			if (currentLocation!=null){
				LatLng latlng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));
			}
		}

	}

	private void preparemapshowmarker_G(double lat, double lng, int i, String focusSet, String orderStat, String typeOfMap, String siteType, boolean mapZoom ,int seqNoAccessPth) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		LatLng latlng = null;
		try{
		latlng = new LatLng(lat, lng);		
		if (typeOfMap.equals("orders")){	// Yd order Geo		  
			if(orderStat.equals("")){
				if(Edition < 600){
					Marker kiel = map.addMarker(new MarkerOptions().position(latlng)
							.title(String.valueOf(i))
							.visible(true)
							.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.access_point, "" + seqNoAccessPth))));//YD changed form balloon_grey to access_point for non-pge app
					builder.include(kiel.getPosition());

					kiel.setDraggable(isDragEnable);
				}/*else{
					if(maptype.equals("TreeList")){
						Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
								.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.triangle, ""))));
						builder.include(kiel.getPosition());
					}
				}*/
			}
			else if (orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){	        
				//if(Edition < 600)	{
					Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
							.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.access_point, "" + seqNoAccessPth))));//YD changed form balloon_grey to access_point for non-pge app
					builder.include(kiel.getPosition());
				kiel.setDraggable(isDragEnable);
			//	}
			/*else{
					if(maptype.equals("TreeList")){
						Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
								.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.triangle, ""))));
						builder.include(kiel.getPosition());
					}else{
						Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
								.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.triangle, ""))));
						builder.include(kiel.getPosition());
					}     				 
				}*/
			}
			else if (orderStat.equals("4" ) || orderStat.equals("5" )  || Integer.parseInt(orderStat)> 49){
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.access_point_gray, "" + seqNoAccessPth))));//YD changed form balloon_grey to access_point for non-pge app
				builder.include(kiel.getPosition());
				kiel.setDraggable(isDragEnable);
			}
			else if (orderStat.equals("7" ) || (Integer.parseInt(orderStat)> 29 && Integer.parseInt(orderStat)< 50)){
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.access_point_red, "" + seqNoAccessPth))));//YD changed form balloon_grey to access_point for non-pge app
				builder.include(kiel.getPosition());
				kiel.setDraggable(isDragEnable);
			}
			/*else if (Integer.parseInt(orderStat) > 3){
				if(Edition < 600){
					Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
							.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.access_point, ""+seqNoAccessPth))));//YD changed form balloon_grey to access_point for non-pge app
					builder.include(kiel.getPosition());
				}*/// YD commenting for non-page app
				/*else{
					if(maptype.equals("TreeList")){
						Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
								.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.triangle, ""))));
						builder.include(kiel.getPosition());
					}
				}*/
			//}
		} 
		else  if (typeOfMap.equals("tasks_pole")){			
			if(orderStat.equals("")){
				if(siteType.contains("*pole")){				
					Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
							.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.tower, ""))));
					builder.include(kiel.getPosition());
				}else if(siteType.contains("*default")){
					Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
							.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.home_b, ""))));
					builder.include(kiel.getPosition());				
				}else if(siteType.contains("*access point")){  // Put Access Point New Icon
						Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
								.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.access_point, ""+seqNoAccessPth))));
						builder.include(kiel.getPosition());
				}
			}else if(orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				if(siteType.contains("*pole")){				
					Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
							.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.tower, ""))));
					builder.include(kiel.getPosition());
				}else if(siteType.contains("*default")){
					Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
							.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.home_b, ""))));
					builder.include(kiel.getPosition());				
				}else if(siteType.contains("*access point")){  // Put Access Point New Icon					
					Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
							.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.access_point, ""+seqNoAccessPth))));
					builder.include(kiel.getPosition());
				}
			}			
		}
		else  if (typeOfMap.equals("tasks_tree")){
			if(orderStat.equals("")){
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.tree_map, ""))));
				builder.include(kiel.getPosition());
			}else if(orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.tree_map, ""))));
				builder.include(kiel.getPosition());
			}
		}
		else  if (typeOfMap.equals("tasks_path")){
			if(orderStat.equals("")){
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.dot, ""))));
				builder.include(kiel.getPosition());
			}else if(orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.dot, ""))));
				builder.include(kiel.getPosition());
			}
		}else  if (typeOfMap.equals("tasks_path_complete")){
			if(orderStat.equals("")){
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.gray_dot_accpath, ""))));
				builder.include(kiel.getPosition());
			}else if(orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.gray_dot_accpath, ""))));
				builder.include(kiel.getPosition());
			}
		}
		else  if (typeOfMap.equals("accesspath")){//YD using for breadCrumb
			if(orderStat.equals("")){
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.dot, ""))));
				builder.include(kiel.getPosition());
			}else if(orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				Marker kiel = map.addMarker(new MarkerOptions().position(latlng).title(String.valueOf(i)).visible(true)
						.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable_G(R.drawable.dot, ""))));
				builder.include(kiel.getPosition());
			}
		}		

			if(map!=null && mapZoom)				
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));
		i++;
		}
		catch(Exception e){
			Log.i(BaseTabActivity.LOGNAME, "exception in preparemapshowmarker in googlemap fragment");
		}
	}
	

	public static void setMapOrderXMLGM(HashMap<Long, Order> xml)
	{
		int i =1;
		MapFragment mfrag = new MapFragment();
		orderxml = xml;
		/*orderxml = mfrag.sortOrders(xml);
		for (Entry entry : orderxml.entrySet()){
			((Order)entry.getValue()).setSequenceNumber(i);
			i++;
		}*/
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

	/*private LinkedHashMap<Long, Order> sortOrders(HashMap<Long, Order> odrDataMap) {

		Map<Long, Order> odrDataMapLinkedHMup = new LinkedHashMap<Long, Order>();
		Map<Long, Order> odrDataMapLinkedHMdw = new LinkedHashMap<Long, Order>();
		if (odrDataMap!=null && odrDataMap.size()>0){
			for (Long key : odrDataMap.keySet()){
				Order order = odrDataMap.get(key);
				long statusID = order.getStatusId();
				Date startdate = getStatDate(order.getFromDate());
				
				if (statusID==8 || !Utilities.isTodayDate(mActivity ,startdate))// YD Checking if the order date has change from current to some other through web)
					{
						Log.i(BaseTabActivity.LOGNAME, "Order Status is Unscheduled");}
				else if (statusID>3)
					odrDataMapLinkedHMdw.put(key, odrDataMap.get(key));
				else
					odrDataMapLinkedHMup.put(key, odrDataMap.get(key));
			}
			
			odrDataMapLinkedHMup = getSortedList(odrDataMapLinkedHMup);
			odrDataMapLinkedHMdw = getSortedList(odrDataMapLinkedHMdw);
			
			odrDataMapLinkedHMup.putAll(odrDataMapLinkedHMdw);
		}
		return (LinkedHashMap<Long, Order>) odrDataMapLinkedHMup;
	}*/
	
	/*private Map<Long, Order> getSortedList(Map<Long, Order> listToSort )
	{
		ArrayList<Order> unsortedLst = new ArrayList<Order>();
		for (Long key : listToSort.keySet() ){
			unsortedLst.add(listToSort.get(key));
		}
		
		Collections.sort(unsortedLst, new Comparator<Order>() {
			@Override
			public int compare(Order odr1, Order odr2) {
				long time1 = 0, time2 = 0;
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
				try {
					Date date1 = simpleDateFormat.parse(odr1.getFromDate());
					Date date2 = simpleDateFormat.parse(odr2.getFromDate());
					time1 = date1.getTime();
					time2 = date2.getTime();


				} catch (ParseException e) {
					e.printStackTrace();
				}
				return Double.compare(time1, time2);
				//  return  odr1.getStartTime().compareTo(odr2.getStartTime());
			}
		});
		
		
		*//*for (int i =0; i<unsortedLst.size();i++){
			long startTime = unsortedLst.get(i).getStartTime();
			Order order ;
			for (int j =i+1; j<unsortedLst.size();j++){
				if (startTime>unsortedLst.get(j).getStartTime())
				{
					order = unsortedLst.get(i);
					unsortedLst.add(i, unsortedLst.get(j));
					unsortedLst.add(j, order);
				}
			}
		}*//*//YD already commented when commenting whole function
		Map<Long, Order> localMap = new LinkedHashMap<Long, Order>();
		for (int i =0; i<unsortedLst.size();i++){
			localMap.put(unsortedLst.get(i).getId(), unsortedLst.get(i));
		}
		
		return localMap;
	}*/
	
	public Date getStatDate(String date){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
		Date startdate = null;
		try {
			startdate = simpleDateFormat.parse(date);
		} catch (ParseException e) {e.printStackTrace();}

		return startdate;
	}

	@Override
	public void headerClickListener(String callingId) {//YD
		if (mapScreen.equals("arcgis_map")){
			if(callingId.equals(BaseTabActivity.HeaderMapSettingPressed)){

				final PopupWindow popview = Utilities.show_Map_popupmenu(getActivity(),mActivity.headerApp.getHeight());
				View view = popview.getContentView();
				TextView menu_normal = (TextView)view.findViewById(R.id.menu_normal);
				TextView menu_satellite = (TextView)view.findViewById(R.id.menu_satellite);
				TextView menu_hybrid = (TextView)view.findViewById(R.id.menu_hybrid);
				//TextView menu_offline = (TextView)view.findViewById(R.id.menu_offline);
				TextView menu_offline_imagery = (TextView)view.findViewById(R.id.menu_offline_imagery);

					menu_normal.setText("Online Street");
					menu_satellite.setVisibility(View.GONE);
					menu_hybrid.setVisibility(View.GONE);
					//menu_offline.setText("Offline");
					menu_offline_imagery.setVisibility(View.GONE);


				menu_normal.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						popview.dismiss();
						if ( Utilities.checkInternetConnection(mActivity, false)) {
							showMapMenu = false;
							setViewLayout(R.layout.map_view, "google");
						} else {
							//if (Utilities.checkInternetConnection(mActivity, false)) {
								Toast.makeText(mActivity, "No Internet Connection.", Toast.LENGTH_LONG).show();
							 /*}else {
								Toast.makeText(mActivity, "Low Network Speed.", Toast.LENGTH_LONG).show();
							}*/
						}
					}
				});
				menu_satellite.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						popview.dismiss();
					}
				});
				menu_hybrid.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						popview.dismiss();
					}
				});
				menu_offline_imagery.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						popview.dismiss();


					}
				});

			}
			else if(callingId.equals(mActivity.HeaderlastSyncPressed)){
					mActivity.onBackPressed();

			}else if(callingId.equals(mActivity.HeaderTaskUserPressed)){

			}else if (callingId.equals(BaseTabActivity.HeaderPlusPressed)){//YD
				if(!btnUpdateShow_ARC){
					AddEditTaskOrderFragment addCustomerOrderFragment = new AddEditTaskOrderFragment();
					Bundle bundle = new Bundle();
					bundle.putString("TreeType", "ADD TASK");
					bundle.putString("OrderId", currentOdrId);
					bundle.putString("OrderName", currentOdrName);
					addCustomerOrderFragment.setArguments(bundle);
					addCustomerOrderFragment.setActiveOrderObj(activeOrderObj);
					mActivity.pushFragments(Utilities.JOBS, addCustomerOrderFragment, true, true,BaseTabActivity.UI_Thread);
				}else{
					isDialogForUpdate_ARC = true;
					dialog_ARC = new MyDialog(mActivity, getResources().getString(R.string.lbl_tree_updates), getResources().getString(R.string.lbl_upd_message), "YES");
					dialog_ARC.setkeyListender(new MyDiologInterface() {
						@Override
						public void onPositiveClick() throws JSONException {
							isDialogForUpdate_ARC = false;
							btnUpdateShow_ARC=false;
							FIELD_UPDATED_ARC=0;
							dialog_ARC.dismiss();

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
									//mMapView.setVisibility(View.VISIBLE);//YD commented arcmap
								}
							});
							dialog_ARC.dismiss();
						}
					});
					dialog_ARC.onCreate(null);
					dialog_ARC.show();
				}
			}else if(callingId.equals(BaseTabActivity.HeaderBackPressed))
			{
				if(btnUpdateShow_ARC){
					isDialogForUpdate_OnBack_ARC = true;
					dialog_ARC = new MyDialog(mActivity, getResources().getString(R.string.lbl_tree_updates), getResources().getString(R.string.lbl_upd_message), "YES");
					dialog_ARC.setkeyListender(new MyDiologInterface() {
						@Override
						public void onPositiveClick() throws JSONException {
							// No Button Click
							isDialogForUpdate_OnBack_ARC = false;
							btnUpdateShow_ARC=false;
							FIELD_UPDATED_ARC=0;
							goBack(mActivity.SERVICE_Thread);
							dialog_ARC.dismiss();
						}

						@Override
						public void onNegativeClick() {
							// Yes Button Click
							btnUpdateShow_ARC=false;
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
									//mMapView.setVisibility(View.VISIBLE); //YD commented arcmap
								}
							});
							dialog_ARC.dismiss();
						}
					});
					dialog_ARC.onCreate(null);
					dialog_ARC.show();
				}else{
					bottom_showARC.setVisibility(View.GONE);

					if(mapTmpAcesPath!=null && mapTmpAcesPath.size()>0 && mapOfAcesPth.size()>0){
						// mMapView.clearAllOverlays();
						lstAccPathStr ="";
						breadcrumbs_counter_ARC=0;
						mapTmpAcesPath_ARC.clear();
						mapOfAcesPth.clear();
					}


					if(access_point_geo!=null && accesspath_lat_long!=null){
						accesspath_lat_long ="";
					}

					btnSaveChangesARC.setEnabled(false);
					final int sdk = android.os.Build.VERSION.SDK_INT;
					if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
						btnSaveChangesARC.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_logwaypoint_selector));
					} else {
						btnSaveChangesARC.setBackground(getResources().getDrawable(R.drawable.btn_logwaypoint_selector));
					}
				}
			}
		}

		//YD For google
		if (mapScreen.equals("google")){
			Log.e("callingId :", String.valueOf(callingId));
			// TODO Auto-generated method stub

			if (callingId.equals(BaseTabActivity.HeaderDonePressed)){
				//YD currently using for saving the order new location changed using drag functionality
				String keys[] = changedOdrLocMap.keySet().toArray(new String[changedOdrLocMap.size()]);

				for (int i=0 ; i<changedOdrLocMap.size(); i++) {
					String tistamp = String.valueOf(Utilities.getCurrentTime());
					Order order = orderxml.get(Long.valueOf(keys[i]));

					/*updateOrderRequest req = new updateOrderRequest();
					req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi");
					req.setType("post");
					req.setId(keys[i]);//YD order id
					req.setName(Order.ORDER_CUSTOMER_SITE_GEOCODE);
					req.setValue(changedOdrLocMap.get(keys[i]));// sending the changed location using drag
					req.setAction(Order.ACTION_SAVE_ORDER_FLD);

					Order.saveOrderField(req, mActivity, this, SAVEORDERFIELD_SITE_GEO);*/

					EditSiteReq req = new EditSiteReq();
					req.setAction(Site.ACTION_EDIT_SITE);
					req.setId(Long.valueOf(order.getCustSiteId()));
					req.setGeo(changedOdrLocMap.get(keys[i]));
					req.setIsOnlyUpdate("true");
					Site.getData(req, mActivity, this, SAVESITE_ORDER+i);//YD adding i because sending multiple requests
				}
			}
			else if(callingId.equals(BaseTabActivity.HeaderMapSettingPressed_Main)){
				if(!showMapMenu){
					return;
				}
				final PopupWindow popview = Utilities.show_Map_popupmenu(getActivity(),mActivity.headerApp.getHeight());
				View view = popview.getContentView();
				TextView menu_normal = (TextView)view.findViewById(R.id.menu_normal);
				TextView menu_satellite = (TextView)view.findViewById(R.id.menu_satellite);
				TextView menu_hybrid = (TextView)view.findViewById(R.id.menu_hybrid);
				//TextView menu_offline = (TextView)view.findViewById(R.id.menu_offline);
				TextView menu_offline_imagery = (TextView)view.findViewById(R.id.menu_offline_imagery);

				if (PreferenceHandler.getArcgisMapTpkState(mActivity)) {
					menu_offline_imagery.setVisibility(View.VISIBLE);
				}
				else{
					menu_offline_imagery.setVisibility(View.GONE);
				}

				menu_normal.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						showMapMenu = true;
						Utilities.log(mActivity, "Google normal map clicked");
						map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
						popview.dismiss();
					}
				});
				menu_satellite.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						showMapMenu = true;
						Utilities.log(mActivity, "Google satellite map clicked");
						map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
						popview.dismiss();
					}
				});
				menu_hybrid.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						showMapMenu = true;
						Utilities.log(mActivity, "Google hybrid map clicked");
						map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
						popview.dismiss();
					}
				});
				menu_offline_imagery.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						popview.dismiss();
						showMapMenu = false;
						setViewLayout(R.layout.activity_arcgis_map, "arcgis_map");
					}
				});


			}else if(callingId.equals(BaseTabActivity.HeaderlastSyncPressed)){
					mActivity.onBackPressed();
			}/*else if(callingId.equals(BaseTabActivity.HeaderTaskUserPressed)){
				if(maptype.equals("TreeList")){
					mActivity.onBackPressed();
				}
			}*/else if (callingId.equals(BaseTabActivity.HeaderPlusPressed)){//YD
				AddEditTaskOrderFragment addCustomerOrderFragment = new AddEditTaskOrderFragment();
				Bundle bundle = new Bundle();
				bundle.putString("TreeType", "ADD TASK");
				bundle.putString("OrderId", currentOdrId);
				bundle.putString("OrderName", currentOdrName);
				addCustomerOrderFragment.setArguments(bundle);
				addCustomerOrderFragment.setActiveOrderObj(activeOrderObj);
				mActivity.pushFragments(Utilities.MAP, addCustomerOrderFragment, true, true,BaseTabActivity.UI_Thread);
			}
		}
	}

	/*public static void setOrderJsonForTreeMap(Order odrObj) {
		activeOrderObj = odrObj;
	}*/

	public static void setAccesspathpointsForTreeMap_G(String pathPoint) {

		accesspathpoints = pathPoint;
	}


	// To Do By Mandeep
	private void parseallSiteXML_G()
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
		/*case R.id.btnLogwayPoint:
			String selectedPoint = Utilities.getLocation(mActivity.getApplicationContext());
			//	int size = 0 ;
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mWebview_G.setVisibility(View.VISIBLE);
					mWebview_G.setFocusable(true);
					mWebview_G.requestFocus();
				}
			});

			Log.e("Current GPS :", String.valueOf(selectedPoint));

			if(selectedPoint!=null){
				String geo[] = selectedPoint.toString().split(",");
				if(!geo[0].equals("0.0") || !geo[1].equals("0.0"))
				{
					btnSaveChanges.setEnabled(true);
					btnSaveChanges.setBackground(getResources().getDrawable(R.drawable.btn_save_update_selector));

					lstAccPathStr="";
					if(mapOfAcesPth==null)
						mapOfAcesPth = new HashMap<Integer, String>();

					initAccPathLstLen = mapOfAcesPth.size();
					mapOfAcesPth.put(initAccPathLstLen+10000, selectedPoint);

					for(int i=0; i <mapOfAcesPth.size(); i++){
						if(i==0)
							lstAccPathStr = mapOfAcesPth.get(i);
						else
							lstAccPathStr = lstAccPathStr+"|"+mapOfAcesPth.get(i);
					}
					preparemapshowmarker_G(Double.parseDouble(geo[0]), Double.parseDouble(geo[1]), initAccPathLstLen+10000, "0", "", "accesspath", "", false);

					if(mapTmpAcesPath==null){
						mapTmpAcesPath = new HashMap<Integer, Integer>();
					}

					mapTmpAcesPath.put(initAccPathLstLen+10000, initAccPathLstLen+10000);

										if(listOfAcesPth==null){
						listOfAcesPth = new ArrayList<String>();
					}else{
						size = listOfAcesPth.size(); // MY
					}

					listOfAcesPth.add(size, selectedPoint);

					for (int i=0; i<listOfAcesPth.size();i++)
					{
						if(i==0)
							lstAccPathStr = listOfAcesPth.get(i);
						else
							lstAccPathStr= lstAccPathStr+"|"+listOfAcesPth.get(i);
					}
					preparemapshowmarker(Double.parseDouble(geo[0]), Double.parseDouble(geo[1]), size+10000, "0", "", "accesspath", "", false);

					// zoom camara on Current Location
					//	LatLng myCoordinates = new LatLng(Double.parseDouble(geo[0]), Double.parseDouble(geo[1]));
					//	map.animateCamera(CameraUpdateFactory.newLatLngZoom(myCoordinates, 12));
				}
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
							mWebview_G.setVisibility(View.GONE);
							mWebview_G.setFocusable(false);
						}
					}, 500);
				}
			});

			break;			*/

		/*case R.id.btnSaveChanges:
			//mapTmpAcesPath.clear();

			if(access_point_geo!=null && accesspath_lat_long!=null)
			{
				String arrTemp[] = access_point_geo.split(",");
				String arrGeo[] = accesspath_lat_long.split(",");

				if(!arrTemp[0].equals(arrGeo[0]) && !arrTemp[1].equals(arrGeo[1])){
					saveAccessPointLocation_G(accesspath_lat_long, odr_customerId, accessPointId); // Save Access Point
				}
			}

			if(mapTmpAcesPath!=null && mapTmpAcesPath.size()>0 || mapOfAcesPth!=null && mapOfAcesPth.size()!= total_accessPath){
				saveNewSiteForWaypoints_G(lstAccPathStr, odr_customerId, waypointId); // Save Waypoints
			}

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mWebview_G.setVisibility(View.VISIBLE);
					mWebview_G.setFocusable(true);
					mWebview_G.requestFocus();
				}
			});
			break;*/


		/*case R.id.btnLogwayPoint_sk:

			String currentPoint = Utilities.getLocation(mActivity.getApplicationContext());
			Log.e("Current GPS :", String.valueOf(currentPoint));

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mWebview_SK.setVisibility(View.VISIBLE);
					mWebview_SK.setFocusable(true);
					mWebview_SK.requestFocus();
				}
			});

			SKCoordinate currentCoordinate = new SKCoordinate();

			if(currentPoint!=null){
				String spltarray[] = currentPoint.toString().split(",");
				if(!spltarray[0].equals("0.0") || !spltarray[1].equals("0.0"))
				{
					btnSaveChanges.setEnabled(true);
					btnSaveChanges.setBackground(getResources().getDrawable(R.drawable.btn_save_update_selector));

					lstAccPathStr="";
					if(mapOfAcesPth==null)
						mapOfAcesPth = new HashMap<Integer, String>();

					initAccPathLstLen = mapOfAcesPth.size();
					mapOfAcesPth.put(initAccPathLstLen+10000, currentPoint);

					for(int i=0; i < mapOfAcesPth.size(); i++){
						if(i==0)
							lstAccPathStr = mapOfAcesPth.get(i);
						else
							lstAccPathStr = lstAccPathStr+"|"+mapOfAcesPth.get(i);
					}

					preparemapshowmarker_SK(Double.parseDouble(spltarray[0]), Double.parseDouble(spltarray[1]), initAccPathLstLen+10000, "0", "", "accesspath", "", false);

					if(mapTmpAcesPath==null){
				    	mapTmpAcesPath = new HashMap<Integer, Integer>();
				    }

				    mapTmpAcesPath.put(initAccPathLstLen+10000, initAccPathLstLen+10000);

					// commented by mandeep

					currentCoordinate.setLatitude(Double.parseDouble(spltarray[0]));
					currentCoordinate.setLongitude(Double.parseDouble(spltarray[1]));
					mapView.setPositionAsCurrent(currentCoordinate, 0, true);
					mapView.setZoom(13);
				}
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
							mWebview_SK.setVisibility(View.GONE);
							mWebview_SK.setFocusable(false);
						}
					}, 500);
				}
			});

			break;*/

		case R.id.Bt_editList:

			if(Edition_SK> 599){
				OrderTask odrTaskObj;

				Log.e("Task-Id :", String.valueOf(annotatOdr_TaskId_SK));


					odrTaskObj = (OrderTask) alltasksxml.get(Long.valueOf(annotatOdr_TaskId_SK));

				if(odrTaskObj!=null){
					Log.i(BaseTabActivity.LOGNAME, "Opening tree edit Screen");
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
					mActivity.pushFragments(Utilities.MAP, addCustomerOrderFragment, true, true,BaseTabActivity.UI_Thread);
				}
				else {
					Log.i(BaseTabActivity.LOGNAME, "task detail not available in map");
				}
			}
			break;

		case R.id.btnSaveChanges_sk:
			//mapTmpAcesPath.clear();

			if(access_point_geo!=null && accesspath_lat_long!=null)
			{
				String arrTemp[] = access_point_geo.split(",");
				String arrGeo[] = accesspath_lat_long.split(",");

				if(!arrTemp[0].equals(arrGeo[0]) && !arrTemp[1].equals(arrGeo[1])){
					saveAccessPointLocation_SK(accesspath_lat_long, odr_customerId, accessPointId); // Save Access Point
				}
			}

			if(mapTmpAcesPath!=null && mapTmpAcesPath.size()>0 || mapOfAcesPth!=null && mapOfAcesPth.size()!= total_accessPath){
				saveNewSiteForWaypoints_SK(lstAccPathStr, odr_customerId, waypointId); // Save Waypoints
			}

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mWebview_SK.setVisibility(View.VISIBLE);
					mWebview_SK.setFocusable(true);
					mWebview_SK.requestFocus();
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
		if(reqId == internetSpeedrequestId){
			/*if (response!= null && Double.valueOf(response)<20){
				showMapMenu = false;
				setViewLayout(R.layout.mapfragment, "skobbler");
			}*/
		}

	}

	@Override
	public void setResponseCBActivity(Response response) {
		if (response!=null)
		{
			if (response.getStatus().equals("success")&&
					response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED)))
			{
				if (mapScreen.equals("arcgis_map")){
					if(response.getId()==GET_SITE_REQ_ARC){
						allSitexml = (HashMap<Long, Site>)response.getResponseMap();
						mActivity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									checkforOrderDataLoading_ARC();
								}
							});
					}

					if(response.getId()==GET_TASKS_ARC){
						tasksxml = (HashMap<Long, OrderTask>)response.getResponseMap();
						mActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								showOrderTrees_ARC(tasksxml, true);
							}
						});
					}

					if(response.getId()==SAVESITE_WAY_PATH || response.getId()==SAVESITE_ACCESS_POINT){
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Handler handler = new Handler();// YD hiding loading after one second (Client requirement)
								handler.postDelayed(new Runnable()
								{
									@Override
									public void run()
									{
										mWebview_SK.setVisibility(View.GONE);
										btnSaveChangesARC.setEnabled(false);
										final int sdk = android.os.Build.VERSION.SDK_INT;
										if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
											btnSaveChangesARC.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_logwaypoint_selector));
										} else {
											btnSaveChangesARC.setBackground(getResources().getDrawable(R.drawable.btn_logwaypoint_selector));
										}
									}
								}, 1000);
							}
						});
						FIELD_UPDATED_ARC=0;
						btnUpdateShow_ARC=false;
						if(isDialogForUpdate_ARC){
							isDialogForUpdate_ARC = false;
							if(isEditTree_ARC ==0){
								AddEditTaskOrderFragment addCustomerOrderFragment = new AddEditTaskOrderFragment();
								Bundle bundle = new Bundle();
								bundle.putString("TreeType", "ADD TASK");
								bundle.putString("OrderId", currentOdrId);
								bundle.putString("OrderName", currentOdrName);
								addCustomerOrderFragment.setArguments(bundle);
								addCustomerOrderFragment.setActiveOrderObj(activeOrderObj);
								mActivity.pushFragments(Utilities.JOBS, addCustomerOrderFragment, true, true,BaseTabActivity.UI_Thread);
							}else{
								isEditTree_ARC=0;

								if (Edition > 599) {
									OrderTask odrTaskObj;
									if (tasksxml != null) {
										odrTaskObj = (OrderTask) tasksxml.get(Long
												.valueOf(annotatOdr_TaskId));
									} else {
										odrTaskObj = (OrderTask) alltasksxml.get(Long
												.valueOf(annotatOdr_TaskId));
									}

									if (odrTaskObj != null) {
										Bundle bundle = new Bundle();
										bundle.putString("TreeType", "EDIT TASK");

										bundle.putString("OrderId",
												String.valueOf(odrTaskObj.getId()));
										Order order = ((HashMap<Long, Order>) DataObject.ordersXmlDataStore)
												.get(Long.valueOf(odrTaskObj.getId()));
										bundle.putString("OrderName", order.getNm());

										bundle.putLong("Id", odrTaskObj.getOrder_task_id());
										bundle.putLong("ServiceId", odrTaskObj.getTask_id());
										bundle.putLong("SiteId",
												Long.parseLong(odrTaskObj.getTree_type()));
										bundle.putLong("PriorityId",
												Long.parseLong(odrTaskObj.getPriority()));
										bundle.putString("Status",
												String.valueOf(odrTaskObj.getAction_status()));
										if (odrTaskObj.getTree_owner() != null)// YD check null as
											// mr a said
											bundle.putString("Owner", odrTaskObj.getTree_owner());
										else
											bundle.putString("Owner", "");
										bundle.putString("EstimatedCount",
												String.valueOf(odrTaskObj.getTree_expcount()));
										bundle.putString("ActualCount",
												String.valueOf(odrTaskObj.getTree_actualcount()));
										bundle.putString("TreeHeight",
												String.valueOf(odrTaskObj.getTree_ht()));
										bundle.putString("DiameterBH",
												String.valueOf(odrTaskObj.getTree_dia()));
										bundle.putString("HvClearance",
												String.valueOf(odrTaskObj.getTree_clearence()));
										bundle.putString("Cycle",
												String.valueOf(odrTaskObj.getTree_cycle()));
										bundle.putString("Hours",
												String.valueOf(odrTaskObj.getTree_timespent()));
										bundle.putString("TandM",
												String.valueOf(odrTaskObj.getTree_tm()));
										bundle.putString("OT",
												String.valueOf(odrTaskObj.getTree_msc()));
										bundle.putString("TreeComment",
												String.valueOf(odrTaskObj.getTree_comment()));
										bundle.putString("PrescirptionComment",
												String.valueOf(odrTaskObj.getTree_pcomment()));
										bundle.putString("Alerts",
												String.valueOf(odrTaskObj.getTree_alert()));
										bundle.putString("Note",
												String.valueOf(odrTaskObj.getTree_note()));
										bundle.putString("AccessComplexity",
												String.valueOf(odrTaskObj.getTree_ct1()));
										bundle.putString("SetupComplexity",
												String.valueOf(odrTaskObj.getTree_ct2()));
										bundle.putString("PrescriptionComplexity",
												String.valueOf(odrTaskObj.getTree_ct3()));
										bundle.putString("AccessPath",
												String.valueOf(odrTaskObj.getTree_accesspath()));
										bundle.putString("TreeGeo",
												String.valueOf(odrTaskObj.getTree_geo()));

										mActivity.OrderEditTaskBackOdrId = Long.valueOf(odrTaskObj
												.getId());
										AddEditTaskOrderFragment addCustomerOrderFragment = new AddEditTaskOrderFragment();
										addCustomerOrderFragment.setArguments(bundle);
										mActivity.pushFragments(Utilities.JOBS,
												addCustomerOrderFragment, true, true,
												BaseTabActivity.UI_Thread);
									}
								}
							}
						}else if(isDialogForUpdate_OnBack_ARC){
							isDialogForUpdate_OnBack_ARC = false;
							goBack(mActivity.SERVICE_Thread);
						}
					}
				}
				if (mapScreen.equals("google")){
				if(response.getId()==GET_SITE_REQ){
					Log.i(BaseTabActivity.LOGNAME, "Received Success For GetSiteRequest in GM_MF");

					allSitexml = (HashMap<Long, Site>)response.getResponseMap();

					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							checkforOrderDataLoading_G();
					//		mWebview_G.setVisibility(View.GONE);
						}
					});
				}

				if(response.getId()==GET_TASKS){
					Log.i(BaseTabActivity.LOGNAME, "Received Success For GetTaskRequest in GM_MF");
					tasksxml = (HashMap<Long, OrderTask>)response.getResponseMap();

					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							showTreesAndDots_G(tasksxml, true);
						}
					});
				}

				if(response.getId()>=SAVESITE_ORDER){
					Log.i(BaseTabActivity.LOGNAME, "Received Success For saveLocation in GM_MF");
					changedOrder++;
					if (changedOrder == changedOdrLocMap.size())//checking if the SAVESITE_ORDER is called for the same number of time the number of order lcoation changed
					{
						String keys[] = changedOdrLocMap.keySet().toArray(new String[changedOdrLocMap.size()]);

						for (int i=0; i<changedOdrLocMap.size(); i++){
							Order order = orderxml.get(Long.valueOf(keys[i]));
							order.setCustSiteGeocode(changedOdrLocMap.get(keys[i]));
						}
						changedOdrLocMap.clear();// clearing the map having changed geo locations
						changedOrder = 0;
						OrderListMainFragment.optionRefreshState = 100;

						mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mActivity.mBtnDone.setVisibility(View.GONE);
							mActivity.no_of_order.setVisibility(View.VISIBLE);
							}
						});
					}

				}

				if(response.getId()==SAVESITE_WAY_PATH || response.getId()==SAVESITE_ACCESS_POINT){

					Log.i(BaseTabActivity.LOGNAME, "Received Success For SaveAccessPoint and SaveSite Request in GM_MF");
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Handler handler = new Handler();// YD hiding loading after one second (Client requirement)
							handler.postDelayed(new Runnable()
							{
								@Override
								public void run()
								{
							//		mWebview_G.setVisibility(View.GONE);
								//	btnSaveChanges.setEnabled(false);
									final int sdk = android.os.Build.VERSION.SDK_INT;
									if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
									//	btnSaveChanges.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_logwaypoint_selector));
									} else {
								//		btnSaveChanges.setBackground(getResources().getDrawable(R.drawable.btn_logwaypoint_selector));
									}
								}
							}, 1000);
						}
					});
				}
			}
			}
		}
		else if(response.getStatus().equals("success")&& 
				response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
		{
			if (mapScreen.equals("google")) {
				if (response.getId() == GET_SITE_REQ) {
					Log.i(BaseTabActivity.LOGNAME, "Received Success For GetSiteRequest in GM_MF");
					allSitexml = (HashMap<Long, Site>) response.getResponseMap();

					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							checkforOrderDataLoading_G();
							//		mWebview_G.setVisibility(View.GONE);
						}
					});
				}
			}
		}	
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	// Save WayPoints 
		private void saveNewSiteForWaypoints_SK(String waypointsLst, long custId, long waypointId)
		{
			Log.i(BaseTabActivity.LOGNAME, "Saving *WayPoints");
			String tistamp = String.valueOf(Utilities.getCurrentTime());

			SaveSiteRequest req = new SaveSiteRequest();
			req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
			req.setAction(Site.ACTION_SAVE_SITE);
			req.setAdr("United States");
			req.setNm("*Breadcrumbs");
			req.setCid(""+custId);
			req.setTstamp(tistamp);
			if (waypointId!=0)
				req.setId(String.valueOf(waypointId));
			else
				req.setId("0");
			req.setGeo("0,0");
			req.setDtl(waypointsLst);
			req.setTid("0");
			req.setLtpnm("0");

			Site.saveData(req, mActivity, this, SAVESITE_WAY_PATH_SK);
		}


		// Save Access Point
		protected void saveAccessPointLocation_SK(String accessPoint, long custId, long accessPointId) {
			Log.i(BaseTabActivity.LOGNAME, "Saving *AccessPoint");
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

			Site.saveData(req, mActivity, this, SAVESITE_ACCESS_POINT_SK);// YD for order geo 
		}


		ImageView imageViewNavArr;

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);

			int RESULT_OK=-1;
			if (resultCode == RESULT_OK) {
				switch (requestCode) {
				case TRACKS:
					break;

				default:
					break;
				}
			}else if(requestCode == 21){

			}
		}
		
		private SensorManager mSensorManager;
		Sensor accelerometer;
		Sensor magnetometer;
		/**
		 * Activates the orientation sensor
		 */
		private void stopNavigation_SK() {
			navigationInProgress = false;
//			clearMap_SK();

			bottomButton.setVisibility(View.GONE);
		}


		int initAccPathLstLen_SK=0;

//		private void clearMap_SK() {
//			switch (currentMapOption) {
//			case MAP_DISPLAY:
//				break;
//			case ROUTING_AND_NAVIGATION:
//				bottomButton.setVisibility(View.GONE);
//				if (navigationInProgress) {
//					// stop navigation if ongoing
//					stopNavigation_SK();
//					routeIds.clear();
//				}
//				break;
//			default:
//				break;
//			}
//			currentMapOption = MapOption.MAP_DISPLAY;
//			positionMeButton.setVisibility(View.VISIBLE);
//			headingButton.setVisibility(View.VISIBLE);
//		}
		
		float[] mGravity;
		float[] mGeomagnetic;
		Float azimut;  // View to draw a compass

		@Override
		public void onSensorChanged(SensorEvent event) {

			//mapView.rotateTheMapToNorth();  default function to rotate to north
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
				mGravity = event.values;
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
				mGeomagnetic = event.values;
			if (mGravity != null && mGeomagnetic != null) {
				float R[] = new float[9];
				float I[] = new float[9];
				boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
				if (success) {
					float orientation[] = new float[3];
					SensorManager.getOrientation(R, orientation);
					azimut = orientation[0]; // orientation contains: azimut, pitch and roll
					orientation[0] = (float) Math.toDegrees(orientation[0]);
					orientation[1] = (float) Math.toDegrees(orientation[1]);
					orientation[2] = (float) Math.toDegrees(orientation[2]);
					azimut = orientation[0];
				}
			}
			//mCustomDrawableView.invalidate();
			if(azimut!=null)
			{
				float degree = (-azimut*360/(2*3.14159f));
				rotate_SK(degree);
			}
		}


		public static void setMapTasksXML_SK(HashMap<Long, OrderTask> xml)
		{
			tasksxml_SK = xml;    	
			//tasksxml="<data><osrv><oid>23598003</oid><id>1427708500306</id><tid>22478002</tid><upd>1428140498060</upd><ltid>22808003</ltid><prev_id>1</prev_id><stat>0</stat><note5></note5><num1></num1><num2></num2><num3></num3><num4></num4><ehrs>0</ehrs><hrs>0</hrs><num5></num5><num6></num6><note6></note6><note1></note1><note2></note2><note3></note3><note4></note4><geo>28.6387530,77.0738030</geo><glist></glist><ct1>1</ct1><ct2>1</ct2><ct3>1</ct3></osrv></data>";
			tasksxmlversion_SK++;
		}

		public static void setMapAllTasksXML_SK(HashMap<Long, OrderTask> xml)
		{
			alltasksxml_SK = xml;    	
			//tasksxml="<data><osrv><oid>23598003</oid><id>1427708500306</id><tid>22478002</tid><upd>1428140498060</upd><ltid>22808003</ltid><prev_id>1</prev_id><stat>0</stat><note5></note5><num1></num1><num2></num2><num3></num3><num4></num4><ehrs>0</ehrs><hrs>0</hrs><num5></num5><num6></num6><note6></note6><note1></note1><note2></note2><note3></note3><note4></note4><geo>28.6387530,77.0738030</geo><glist></glist><ct1>1</ct1><ct2>1</ct2><ct3>1</ct3></osrv></data>";
			alltasksxmlversion_SK++;
		}
		public static void setMapCustSiteXML_SK(HashMap<Long, Site> xml)
		{
			custSitexml_SK = xml;      
			//custSitexml ="<data><loc><id>22928008</id><cid>23008012</cid><nm>*Default</nm><adr>2800 Napa Road, Sonoma, CA pole</adr><upd>1427359311382</upd><tid>0</tid><ltpnm /><geo>28.6366437,77.0775366</geo></loc><loc><id>22928008</id><cid>23008012</cid><nm>*Default</nm><adr>2800 Napa Road, Sonoma, CA pole</adr><upd>1427359311382</upd><tid>0</tid><ltpnm /><geo>28.6397888,77.0716787</geo></loc><loc><id>22928008</id><cid>23008012</cid><nm>*Default</nm><adr>2800 Napa Road, Sonoma, CA pole</adr><upd>1427359311382</upd><tid>0</tid><ltpnm /><geo>28.6375665,77.0724941</geo></loc></data>";
			custSitexmlversion_SK++;
		}
		public static void setMapTasktypeXML_SK(HashMap<Long,ServiceType> xml)
		{
			taskTypexml_SK = xml;      	
		}
		public static void setMapOrderSiteType_SK(HashMap<Long, SiteType> xml)
		{
			siteTypexml_SK = xml;      
			//custSitexml ="<data><loc><id>22928008</id><cid>23008012</cid><nm>*Default</nm><adr>2800 Napa Road, Sonoma, CA pole</adr><upd>1427359311382</upd><tid>0</tid><ltpnm /><geo>28.6366437,77.0775366</geo></loc><loc><id>22928008</id><cid>23008012</cid><nm>*Default</nm><adr>2800 Napa Road, Sonoma, CA pole</adr><upd>1427359311382</upd><tid>0</tid><ltpnm /><geo>28.6397888,77.0716787</geo></loc><loc><id>22928008</id><cid>23008012</cid><nm>*Default</nm><adr>2800 Napa Road, Sonoma, CA pole</adr><upd>1427359311382</upd><tid>0</tid><ltpnm /><geo>28.6375665,77.0724941</geo></loc></data>";
		}
		float currentdegree = 0;
		private Order activeOrderObj;

	private void rotate_SK(float degree) {

			float diff = 0;
			if(currentdegree>degree)
				diff = currentdegree-degree;
			else
				diff = degree-currentdegree;
			if(diff>25)
			{


				final RotateAnimation rotateAnim = new RotateAnimation(currentdegree, degree,
						RotateAnimation.RELATIVE_TO_SELF, 0.5f,
						RotateAnimation.RELATIVE_TO_SELF, 0.5f);

				rotateAnim.setDuration(0);
				rotateAnim.setFillAfter(true);
				imageViewNavArrRound.startAnimation(rotateAnim);
				currentdegree =degree;  
			}
		}

		@Override
		public void onActionOk(int requestCode) {
			// TODO Auto-generated method stub
			
		}

		public void loadDataOnBack(final BaseTabActivity mActivity, boolean onpubnub) {

			if(!onpubnub) {
				mActivity.registerHeader(this);
				this.mActivity = mActivity;
				mActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						showDate(mActivity);
					}
				});
			}

			if(PreferenceHandler.getUiconfigGeoCorrector(mActivity).equals("0")){
				isDragEnable=false;
			}
			
			if (mapScreen.equals("google")){
				if(!onpubnub) {
					SupportMapFragment f = null;
					if (((SupportMapFragment) getFragmentManager()
							.findFragmentById(R.id.gmap_fr_map)) != null) {
						f = (SupportMapFragment) getFragmentManager()
								.findFragmentById(R.id.gmap_fr_map);
						if (f != null) {
							getFragmentManager().beginTransaction().show(f).commit();
						}
					}
				}
				if(Edition > EDN_FOR_DETAIL_TASK){
					getAllSites_G();
				} else if(Edition < EDN_FOR_REGULAR_TASK){
					showOrdersOnmap_G(mActivity);
				}
			}
		}
		
		
		public void showDate(BaseTabActivity mActivity){
			
			long lastSyncTime = PreferenceHandler.getlastsynctime(mActivity);
			Date date = new Date(Long.valueOf(lastSyncTime));

			if (PreferenceHandler.getOdrGetDate(mActivity)!=null && !PreferenceHandler.getOdrGetDate(mActivity).equals(""))// YD showing custom date for which order has been downloaded
				date  =  new Date(PreferenceHandler.getodrLastSyncForDt(mActivity));

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d yyyy");//Jun 21 2015
			String headerDate = simpleDateFormat.format(date);
			String headerTime = Utilities.convertDateToAmPM(date.getHours(), date.getMinutes());
			final String headerDay = BaseTabActivity.getDayFrmDate(date.getDay());
			BaseTabActivity.setHeaderTitle4OLMP(headerDate, "", headerDay);
		}


		private void setViewLayout(int id, String mapType){
			if(mapType.equals("arcgis_map")){
				mapScreen = mapType;
				Edition = PreferenceHandler.getPrefEditionForGeo(getActivity());
				LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				ViewGroup rootView = (ViewGroup) getView();

				rootView.removeAllViews();
				vFrag = inflater.inflate(id, null);
				rootView.addView(vFrag);

				mActivity.registerHeader(this);

				bottom_showARC = (RelativeLayout) vFrag.findViewById(R.id.bottom_show_arc);
				btnLogwayPointARC = (Button) vFrag.findViewById(R.id.btnLogwayPoint_arc);
				btnSaveChangesARC = (Button) vFrag.findViewById(R.id.btnSaveChanges_arc);
				currentLocBtnARC = (ImageView) vFrag.findViewById(R.id.currentLocationIcon_arc);
				total_tree_countARC = (TextView) vFrag.findViewById(R.id.total_tree_count_arc);
				if(activeOrderObj!=null){
					//total_tree_countARC.setText(String.valueOf(activeOrderObj.getCustServiceCount()));  // MY Show total tree count //YD commenting as there is no more task in the app
				}

				currentLocBtnARC.setOnClickListener(this);
				btnLogwayPointARC.setOnClickListener(this);
				btnSaveChangesARC.setOnClickListener(this);

				mViewContainerARC = (FrameLayout) vFrag.findViewById(R.id.map_container);

				mStatusTypeArryListARC = new ArrayList<String>();
				mStatusTypeArryListIdARC = new ArrayList<String>();
				for(int k=0; k<strStatusTypeARC.length; k++){
					mStatusTypeArryListARC.add(strStatusTypeARC[k]);
					mStatusTypeArryListIdARC.add(strStatusTypeIdsARC[k]);
				}

				if(Edition > EDN_FOR_DETAIL_TASK){
					getAllSites();
				}else if(Edition < EDN_FOR_REGULAR_TASK){
					showOrdersOnmap_ARC();
				}

			}
			 if(mapType.equals("google")){
				Utilities.log(mActivity, "setViewLayout called for google map.");
		    	 mapScreen = "google";
		    	 map = null;
		    	    	 
		    	 LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		   	  
		 	     ViewGroup rootView = (ViewGroup) getView();
				  rootView.removeAllViews();//YD added to remove the views because below commented code was causing map to display blank

		    	 try {				
					vFrag = inflater.inflate(id, null);	
					rootView.addView(vFrag);
					vFrag1 = vFrag;
		    	 } catch (InflateException e) {
					if (vFrag1 != null && vFrag1 != vFrag) {
						vFrag = vFrag1;
						rootView.addView(vFrag);
					} else {
						Utilities.log(mActivity, "View handles, vFrag1 = " + vFrag1
								+ "vFRag = " + vFrag);
						e.printStackTrace();
					}
		    	 } 
				
		    	mActivity.registerHeader(this);
				if (map==null) {
					/*map = ((SupportMapFragment) getActivity()
						.getSupportFragmentManager().findFragmentById(
								R.id.gmap_fr_map)).getMap();*/
					mapView_G = ((com.google.android.gms.maps.MapView) vFrag.findViewById(R.id.gmap_fr_map));
					mapView_G.onCreate(null);
					map = mapView_G.getMap();
					new SpeedTestLauncher().bindListeners(this, internetSpeedrequestId);//YD checking up the network speed.
			if (map != null) {
				map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

				map.setMyLocationEnabled(true);
				map.getUiSettings().setCompassEnabled(true);
				map.getUiSettings().setAllGesturesEnabled(true);
				onResume();
			} else {
				Toast.makeText(getActivity(), "else part",
						Toast.LENGTH_LONG).show();
				final Handler mHandler = new Handler();
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						map = ((SupportMapFragment) getActivity()
								.getSupportFragmentManager()
								.findFragmentById(R.id.gmap_fr_map))
								.getMap();
						if (map != null) {
							map.setMyLocationEnabled(true);
							map.getUiSettings().setMyLocationButtonEnabled(
									false);
							map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
							map.setMyLocationEnabled(true);
							map.getUiSettings().setCompassEnabled(true);
							map.getUiSettings().setAllGesturesEnabled(true);
							onResume();
						} else
							mHandler.post(this);
					}
				});
			}
		}
			
		GPSTracker mGPS = new GPSTracker(mActivity);
		if (mGPS.canGetLocation) {
			currentLocation = mGPS.getLocation();
			if(currentLocation != null && currentLocation.getLatitude()<1 && currentLocation.getLongitude()<1){
				currentLocation.setLatitude(36.7783);
				currentLocation.setLongitude(119.4179);
			}
		} else {
			System.out.println("Unable");
		}

		try {
			map.clear();
			Edition = PreferenceHandler.getPrefEditionForGeo(getActivity());
			if (Edition > EDN_FOR_DETAIL_TASK) {
				getAllSites_G();
			} else if (Edition < EDN_FOR_REGULAR_TASK) {
				showOrdersOnmap_G(mActivity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// YD using this for waypoint to add .
		try {
			map.setOnMarkerClickListener(new OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(Marker marker) {
					int zm = (int) map.getCameraPosition().zoom;
					CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude+(double)90/Math.pow(2,zm),marker.getPosition().longitude),zm);
					map.animateCamera(cameraUpdate);
					if (marker.getTitle().equals("Aceroute order")) {
						Log.i(Utilities.TAG, "software order clicked."
										+ marker.isInfoWindowShown());
						if (marker.isInfoWindowShown())
							marker.hideInfoWindow();
						else
							marker.showInfoWindow();
					}

					Log.i(BaseTabActivity.LOGNAME,
							"Annotation clicked GM_MF");
					if (Edition > 599) {
						if (Integer.valueOf(marker.getTitle()) >= 10000
								&& Integer.valueOf(marker.getTitle()) < 20000) {
							marker.hideInfoWindow();
						} else {
							marker.showInfoWindow();
						}
					} else {
						marker.showInfoWindow();
					}
					// marker.hideInfoWindow();
					return true;
				}
			});

			// Marker Window Click Event Handling
			map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(Marker marker) {
					// TODO Auto-generated method stub
					Channel info = infoMap.get(Integer.valueOf(marker
							.getTitle()));

					if (Integer.valueOf(marker.getTitle()) >= 2000
							&& Integer.valueOf(marker.getTitle()) < 10000) {
						if (Edition > EDN_FOR_DETAIL_TASK) {
							OrderTask odrTaskObj;

							odrTaskObj = (OrderTask) alltasksxml.get(Long
									.valueOf(info.getTaskId()));

							if (odrTaskObj != null) {
								Log.i(BaseTabActivity.LOGNAME,
										"Opening tree edit Screen");
								Bundle bundle = new Bundle();
								bundle.putString("TreeType", "EDIT TASK");

								bundle.putString("OrderId",
										String.valueOf(odrTaskObj.getId()));
								Order order = ((HashMap<Long, Order>) DataObject.ordersXmlDataStore)
										.get(Long.valueOf(odrTaskObj
												.getId()));
								bundle.putString("OrderName", order.getNm());

								bundle.putLong("Id",
										odrTaskObj.getOrder_task_id());
								bundle.putLong("ServiceId",
										odrTaskObj.getTask_id());
								bundle.putLong("SiteId", Long
										.parseLong(odrTaskObj
												.getTree_type()));
								bundle.putLong("PriorityId",
										Long.parseLong(odrTaskObj
												.getPriority()));
								bundle.putString("Status", String
										.valueOf(odrTaskObj
												.getAction_status()));
								if (odrTaskObj.getTree_owner() != null)
									bundle.putString("Owner",
											odrTaskObj.getTree_owner());
								else
									bundle.putString("Owner", "");
								bundle.putString("EstimatedCount", String
										.valueOf(odrTaskObj
												.getTree_expcount()));
								bundle.putString("ActualCount", String
										.valueOf(odrTaskObj
												.getTree_actualcount()));
								bundle.putString("TreeHeight", String
										.valueOf(odrTaskObj.getTree_ht()));
								bundle.putString("DiameterBH", String
										.valueOf(odrTaskObj.getTree_dia()));
								bundle.putString("HvClearance", String
										.valueOf(odrTaskObj
												.getTree_clearence()));
								bundle.putString("Cycle",
										String.valueOf(odrTaskObj
												.getTree_cycle()));
								bundle.putString("Hours", String
										.valueOf(odrTaskObj
												.getTree_timespent()));
								bundle.putString("TandM", String
										.valueOf(odrTaskObj.getTree_tm()));
								bundle.putString("OT", String
										.valueOf(odrTaskObj.getTree_msc()));
								bundle.putString("TreeComment", String
										.valueOf(odrTaskObj
												.getTree_comment()));
								bundle.putString("PrescirptionComment",
										String.valueOf(odrTaskObj
												.getTree_pcomment()));
								bundle.putString("Alerts",
										String.valueOf(odrTaskObj
												.getTree_alert()));
								bundle.putString("Note", String
										.valueOf(odrTaskObj.getTree_note()));
								bundle.putString("AccessComplexity", String
										.valueOf(odrTaskObj.getTree_ct1()));
								bundle.putString("SetupComplexity", String
										.valueOf(odrTaskObj.getTree_ct2()));
								bundle.putString("PrescriptionComplexity",
										String.valueOf(odrTaskObj
												.getTree_ct3()));
								bundle.putString("AccessPath", String
										.valueOf(odrTaskObj
												.getTree_accesspath()));
								bundle.putString("TreeGeo", String
										.valueOf(odrTaskObj.getTree_geo()));

								mActivity.OrderEditTaskBackOdrId = Long
										.valueOf(odrTaskObj.getId());
								AddEditTaskOrderFragment addCustomerOrderFragment = new AddEditTaskOrderFragment();
								addCustomerOrderFragment
										.setArguments(bundle);
								mActivity.pushFragments(Utilities.MAP,
										addCustomerOrderFragment, true,
										true, BaseTabActivity.UI_Thread);
							} else {
								Log.e(BaseTabActivity.LOGNAME, "Found null task object for taskId"
										+ info.getTaskId());
							}
						}
					}

					if (Integer.valueOf(marker.getTitle()) >= 0
							&& Integer.valueOf(marker.getTitle()) < 2000) {
						if (Edition < EDN_FOR_REGULAR_TASK) {
							/*OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
							Bundle mBundle = new Bundle();
							mBundle.putString("OrderId",
									String.valueOf(info.getOrderId()));

							orderDetailFragment.setArguments(mBundle);
							mActivity.pushFragments(Utilities.JOBS,
									orderDetailFragment, true, true,
									BaseTabActivity.UI_Thread);*/// YD commented because giving crash 31-05-2016
						}
					}
				}
			});

			map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
				@Override
				public void onMarkerDragStart(Marker marker) {
					// TODO Auto-generated method stub
					Log.e("System out", "onMarkerDragStart..." + marker.getPosition().latitude + "..." + marker.getPosition().longitude);
				}

				@Override
				public void onMarkerDragEnd(Marker markerPoint) {
					Log.e("System out", "DragEnd@..." + markerPoint.getPosition().latitude + "..." + markerPoint.getPosition().longitude);
					String uniqueId  = markerPoint.getTitle();
					if (uniqueId!= null && Integer.valueOf(uniqueId)<1000){//YD <1000 means for order
						Channel info = infoMap.get(Integer.valueOf(markerPoint
								.getTitle()));

						changedOdrLocMap.put(info.getOrderId(), markerPoint.getPosition().latitude + "," + markerPoint.getPosition().longitude);//YD if the location changed twice then there is no need to check as hashmap will handle automatically
						if (changedOdrLocMap.size()>=1) {
							mActivity.mBtnDone.setVisibility(View.VISIBLE);
							mActivity.no_of_order.setVisibility(View.GONE);
						}
					}
				}

				@Override
				public void onMarkerDrag(Marker arg0) {
					// TODO Auto-generated method stub
					Log.e("System out", "Drag@..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);
				}
			});

			map.setInfoWindowAdapter(new InfoWindowAdapter() {
				@Override
				public View getInfoWindow(Marker marker) {

					return null;
				}

				@Override
				// YD create the view for the marker popup
				public View getInfoContents(Marker marker) {
					Log.i(BaseTabActivity.LOGNAME, "Google InfoWindow open");
					View view = mActivity.getLayoutInflater().inflate(
							R.layout.googlemap_layout_popup, null);
					try {
						Typeface tf = TypeFaceFont
								.getCustomTypeface(mActivity
										.getApplicationContext());
						popup_odrtm = (TextView) view.findViewById(R.id.order_time);
						popup_odrtm.setTypeface(tf);
						popup_order_name = (TextView) view
								.findViewById(R.id.order_name);
						popup_order_name.setTypeface(tf, Typeface.BOLD);
						popup_order_Id = (TextView) view
								.findViewById(R.id.order_Id);
						popup_order_Id.setTypeface(tf);
						popup_customer_name = (TextView) view
								.findViewById(R.id.customer_name);
						popup_customer_name.setTypeface(tf);
						popup_address = (TextView) view
								.findViewById(R.id.address);
						popup_address.setTypeface(tf);

						btn_zoom = (ImageView) view
								.findViewById(R.id.Bt_zoom);
						btn_editList = (ImageView) view
								.findViewById(R.id.Bt_editList);
						popup_odrtm.setVisibility(View.GONE);

						popup_order_name.setTextSize(19 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
						popup_customer_name.setTextSize(19 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
						popup_address.setTextSize(17 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
						popup_order_Id.setTextSize(17 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
						popup_odrtm.setTextSize(19 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));

						String title = marker.getTitle();

						Log.e("Annotation :",
								String.valueOf(marker.getTitle()));

						if (Edition > EDN_FOR_DETAIL_TASK) {
							if (Integer.valueOf(title) >= 0
									&& Integer.valueOf(title) < 2000) { // Orders
								popup_odrtm.setVisibility(View.VISIBLE);     	// OrdersOnmap
								popup_order_Id.setVisibility(View.VISIBLE);
								popup_order_name.setVisibility(View.VISIBLE);
								popup_address.setVisibility(View.VISIBLE);
								popup_customer_name.setVisibility(View.GONE);
							}
							else if (Integer.valueOf(title) >= 2000
									&& Integer.valueOf(title) < 10000) { // For
																			// Order
																			// Show
																			// Tree
																			// Details
								popup_order_name
										.setVisibility(View.VISIBLE);
								popup_address.setVisibility(View.VISIBLE);
								btn_editList.setVisibility(View.VISIBLE);
							} else if (Integer.valueOf(title) >= 70000
									&& Integer.valueOf(title) < 80000) // For
																		// Site
																		// Poles
																		// Details
							{
								popup_order_name
										.setVisibility(View.VISIBLE);
							} else { // Order Details
								popup_customer_name
										.setVisibility(View.VISIBLE);
							}
						} else if(Edition < EDN_FOR_REGULAR_TASK){
							popup_order_name.setVisibility(View.VISIBLE);
							popup_order_Id.setVisibility(View.VISIBLE);
							//btn_zoom.setVisibility(View.VISIBLE);
							if(Integer.valueOf(title) >=0 && Integer.valueOf(title) < 2000){ // Orders //YD adding later for orderPopUp
								popup_odrtm.setVisibility(View.VISIBLE);     	// OrdersOnmap
								popup_order_Id.setVisibility(View.VISIBLE);
								popup_order_name.setVisibility(View.VISIBLE);
								popup_address.setVisibility(View.VISIBLE);
								popup_customer_name.setVisibility(View.GONE);
							}
						}

						if (Integer.valueOf(title) >= 10000
								&& Integer.valueOf(title) < 10500) {
							return null;
						}

						Channel info = infoMap.get(Integer.valueOf(title));

						if (info != null) {
							if (info.getOrderId() != null
									&& info.getO_nameid() != null) {
								annotatOdr_OrderNm = info.getO_nameid();
								annotatOdr_OrderId = info.getOrderId();
							}

							if (Integer.valueOf(title) >= 70000
									&& Integer.valueOf(title) < 80000) {
								popup_order_name.setText(info.getO_nameid());
								popup_order_Id.setText("");
							} else {
								if (info.getTaskId() != null)
									annotatOdr_TaskId = info.getOrderId()
											+ "#" + info.getTaskId();
								else
									annotatOdr_TaskId = info.getOrderId()
											+ "#0";
							}

							if (Integer.valueOf(title) >= 0
									&& Integer.valueOf(title) < 2000) {

								//if(Edition > EDN_FOR_DETAIL_TASK){ //YD Commenting if else for non-pge App
								//popup_customer_name.setText(info.getCustomer());
								//}else if(Edition < EDN_FOR_REGULAR_TASK){
								popup_odrtm.setText(getOdrTm(info.getOdrStartDt()));
								popup_order_name.setText(info.getO_nameid());
								popup_order_Id.setText(info.getOrderId());
								popup_address.setText(info.getAdressid());
								//	}
							}

							if (Integer.valueOf(marker.getTitle()) >= 2000
									&& Integer.valueOf(marker.getTitle()) < 10000) {
								if (Edition > EDN_FOR_DETAIL_TASK) {
									popup_order_name.setText(info
											.getO_nameid());
									popup_address.setText(info
											.getCustomer());
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return (view);
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	  
		showMapMenu = true;
	   }
	}


	private void getAllSites() {
		// TODO Auto-generated method stub
		CommonSevenReq req = new CommonSevenReq();
		req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		req.setSource("localonly");
		req.setAction("getallsite");

		Site.getData(req, mActivity, this, GET_SITE_REQ_ARC);
	}

	public void showOrdersOnmap_ARC()
	{
		if(orderxml !=null){
			typeOfMap = "orders";
			String[] lastKnownGeo = null;
			int lastKnownSeqNo = -1;
			orderxmlKeys = orderxml.keySet().toArray(new Long[orderxml.size()]);

			for(int i=0;i<orderxml.size();i++) {
				Order order = (Order) orderxml.get(orderxmlKeys[i]);

				String geo = order.getCustSiteGeocode();
				String spltarray[] = geo.split(",");

				Channel channel = new Channel();
				channel.setCid(i);
				channel.setOrderId(String.valueOf(order.getId()));
				channel.setO_nameid(order.getNm());
				channel.setCustomer(order.getCustName());
				channel.setAdressid(order.getCustSiteStreeAdd());
				channelMapARC.put(i, channel);

				if ((order.getStatusId()==0 || order.getStatusId()==1 || order.getStatusId()==2 || order.getStatusId()==3))//YD logic to show first open item from list_cal of orders
				{
					if (lastKnownSeqNo==-1){
						lastKnownSeqNo = order.getSequenceNumber();
						lastKnownGeo = spltarray;
					}
					else if (order.getSequenceNumber()<lastKnownSeqNo) {
						lastKnownSeqNo = order.getSequenceNumber();
						lastKnownGeo = spltarray;
					}
				}
				preparemapshowmarker_ARC(Double.parseDouble(spltarray[0]),
						Double.parseDouble(spltarray[1]),
						i, "0", "" + order.getStatusId(), typeOfMap, "", true, "", order.getSequenceNumber());

				String cid  = String.valueOf(order.getCustomerid()); // getting Customer Id

				if(allPoleMap_ARC !=null){

					Long[] mKeys = allPoleMap_ARC.keySet().toArray(new Long[allPoleMap_ARC.size()]);

					for(int m=0; m<allPoleMap_ARC.size(); m++){

						Site custSite = (Site) allPoleMap_ARC.get(mKeys[m]);

						if(custSite==null)
							break;

						String custid = String.valueOf(custSite.getCid()); //getting Customer Id

						if(custid!=null && custid.length()>0){

							if(cid.equals(custid))
							{
								tempSiteMap_ARC.put(mKeys[m], custSite);
							}
						}
					}
				}
			}
			if (lastKnownGeo!=null){
				// YD  TODO show the map focus on the location provide in lastKnownGeo
			}
		}
	}
//YD commented arcmap
	private int preparemapshowmarker_ARC(double lat,double lng,int i, String focusSet, String orderStat, String typeOfMap, String siteType, boolean mapZoom, String treeStat, int orderSequence) {

	/*	int uniqueId = -1;
		if (typeOfMap.equals("orders")){
			if(orderStat.equals("")){
				if(Edition < 600){
					uniqueId = mMapViewHelper.addMarkerGraphic(lat, lng, "Heading",
							"SubHeading", null, getResources().getDrawable(R.drawable.access_point), false, 0); //YD changed form balloon_b to dot for non-pge app
				}
			}else if (orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				//if(Edition < 600){
					uniqueId = mMapViewHelper.addMarkerGraphic(lat, lng, "Heading",
							"SubHeading", null, getResources().getDrawable(R.drawable.access_point), false, 0);//YD changed form balloon_b to dot for non-pge app
			//	}
			}
			*//*else if (Integer.parseInt(orderStat)>3){
				if(Edition < 600){
					uniqueId = mMapViewHelper.addMarkerGraphic(lat, lng, "Heading",
							"SubHeading", null, getResources().getDrawable(R.drawable.access_point), false, 0);//YD changed form balloon_b to dot for non-pge app
				}
			}*//*
		}
		else if (typeOfMap.equals("tasks_pole")){
			if(orderStat.equals("")){
				if(siteType.contains("*pole")){
					uniqueId = mMapViewHelper.addMarkerGraphic(lat, lng, "Heading",
							"SubHeading.", null, getResources().getDrawable(R.drawable.tower), false, 0);
				}else if(siteType.contains("*default")){
					Logger.i(mActivity, Utilities.TAG, "Showing default site for taskPole");
					uniqueId = mMapViewHelper.addMarkerGraphic(lat, lng, "Heading",
							"SubHeading", null, getResources().getDrawable(R.drawable.home_b), false, 0);
				}else if(siteType.contains("*access point")){
					uniqueId = mMapViewHelper.addMarkerGraphic(lat, lng, "Heading",
							"SubHeading", null, getResources().getDrawable(R.drawable.access_point), true, 0);
				}
			}else if(orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")) {
				if(siteType.contains("*pole")){
					uniqueId = mMapViewHelper.addMarkerGraphic(lat, lng, "Heading",
							"SubHeading", null, getResources().getDrawable(R.drawable.tower), false, 0);
				}else if(siteType.contains("*default")){
					uniqueId = mMapViewHelper.addMarkerGraphic(lat, lng, "Heading",
							"SubHeading", null, getResources().getDrawable(R.drawable.home_b), false, 0);
				}else if(siteType.contains("*access point")){
					uniqueId = mMapViewHelper.addMarkerGraphic(lat, lng, "Heading",
							"SubHeading", null, getResources().getDrawable(R.drawable.access_point), true, 0);
				}
			}
		}
		else  if (typeOfMap.equals("tasks_tree")){
			int treeIcon = R.drawable.tree_map;
			if(treeStat.equals("0")){
				treeIcon = R.drawable.tree_map;// Open
			}else if(treeStat.equals("2") || treeStat.equals("3")){
				treeIcon = R.drawable.tree_red;// Refusal or Review
			}else if(treeStat.equals("4")){
				treeIcon = R.drawable.tree_blue; // Closed
			}else if(treeStat.equals("5") || treeStat.equals("6")){
				treeIcon = R.drawable.tree_grey;// No Tree or False Detection
			}

			if(orderStat.equals("") || orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				uniqueId = mMapViewHelper.addMarkerGraphic(lat, lng, "Heading",
						"SubHeading", null, getResources().getDrawable(treeIcon), false, 0);
			}
		}
		else  if (typeOfMap.equals("tasks_path")){
			if(orderStat.equals("") || orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				uniqueId = mMapViewHelper.addMarkerGraphic(lat, lng, "Heading",
						"SubHeading", null, getResources().getDrawable(R.drawable.dot), false, 0);
			}
		}
		else if (typeOfMap.equals("tasks_path_complete")){
			if(orderStat.equals("") || orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				uniqueId = mMapViewHelper.addMarkerGraphic(lat, lng, "Heading",
						"SubHeading", null, getResources().getDrawable(R.drawable.gray_dot_accpath), false, 0);
			}
		}
		else  if (typeOfMap.equals("accesspath")){
			if(orderStat.equals("") || orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				uniqueId = mMapViewHelper.addMarkerGraphic(lat, lng, "Heading",
						"SubHeading", null, getResources().getDrawable(R.drawable.dot), false, 0);
			}
		}

		//yash
		if (focusSet.equals("1"))
		{
			// YD  TODO show the map focus on the location provide in parmas
		}

		return uniqueId;*/ //YD commented arcmap
		return 0;
	}

	protected void saveAccessPointLocation(String accessPoint, long custId, long accessPointId) {
		String tistamp = String.valueOf(Utilities.getCurrentTime());

		SaveSiteRequest req = new SaveSiteRequest();
		req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		req.setAction(Site.ACTION_SAVE_SITE);
		req.setAdr("United States");
		req.setNm("*Access Point");
		req.setCid("" + custId);
		req.setTstamp(tistamp);
		if (accessPointId!=0)
			req.setId(String.valueOf(accessPointId));
		else
			req.setId("0");
		req.setGeo(accessPoint);
		req.setTid("0");
		req.setLtpnm("0");

		Site.saveData(req, mActivity, this, SAVESITE_ACCESS_POINT_ARC);// YD for order geo
	}

	// Save WayPoints
	private void saveNewSiteForWaypoints(String waypointsLst, long custId, long waypointId)
	{
		String tistamp = String.valueOf(Utilities.getCurrentTime());

		SaveSiteRequest req = new SaveSiteRequest();
		req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		req.setAction(Site.ACTION_SAVE_SITE);
		req.setAdr("United States");
		req.setNm("*Breadcrumbs");//YD change to Breadcrumbs from Waypoints
		req.setCid(""+custId);
		req.setTstamp(tistamp);
		if (waypointId!=0)
			req.setId(String.valueOf(waypointId));
		else
			req.setId("0");
		req.setGeo("0,0");
		req.setDtl(waypointsLst);
		req.setTid("0");
		req.setLtpnm("0");

		Site.saveData(req, mActivity, this, SAVESITE_WAY_PATH_ARC);
	}

	private void goBack(int threadType) {
		mActivity.popFragments(threadType);
	}

	private void checkforOrderDataLoading_ARC()
	{
		parseallSiteXML_ARC();
		preparetionordermapmarkershow();
	}

	private void parseallSiteXML_ARC()
	{
		if(allSitexml!=null)
		{
			allSitexmlKeys_ARC = allSitexml.keySet().toArray(new Long[allSitexml.size()]);

			for(int m=0; m<allSitexml.size(); m++)
			{
				Site custSite = (Site) allSitexml.get(allSitexmlKeys_ARC[m]);
				if(custSite!=null)
				{
					String temp = custSite.getNm();
					String temp1 = temp.toLowerCase();
					if (temp1.contains("*pole") || temp1.contains("*default") || temp1.contains("*access point")||temp1.contains("*breadcrumbs"))
					{
						allPoleMap_ARC.put(allSitexmlKeys_ARC[m], custSite);
					}
				}
			}
		}
	}

	public void showOrderTrees_ARC(HashMap<Long, OrderTask> datalist, boolean zoom)
	{
		if(datalist !=null)
		{
			tasksxmlKeys_ARC = datalist.keySet().toArray(new Long[datalist.size()]);
			orderxmlKeys_ARC = orderxml.keySet().toArray(new Long[orderxml.size()]); // Order Keys

			for(int i=0;i<datalist.size();i++)
			{
				typeOfMap = "tasks_tree";
				OrderTask ordertask = (OrderTask) datalist.get(tasksxmlKeys_ARC[i]);

				String geo = ordertask.getTree_geo();
				String treeStatus = ordertask.getAction_status();	//MY tree status

				String orderStatus = "";
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
						channel.setAdressid("");	//Tree Status

						if(siteTypexml!=null)// YD site type to show on map poput
						{
							siteTypexmlKeys = siteTypexml.keySet().toArray(new Long[siteTypexml.size()]);

							for(int m = 0;m<siteTypexml.size();m++)
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
							for (int k =0 ;k <mStatusTypeArryListIdARC.size();k++){
								if (mStatusTypeArryListIdARC.get(k).equals(ordertask.getAction_status()))
									elementStatId = k;
							}
							channel.setAdressid(mStatusTypeArryListARC.get(elementStatId));  // Tree Status
						}

						channelMap.put(i+2000, channel);

						preparemapshowmarker_ARC(Double.parseDouble(spltarray[0]),
								Double.parseDouble(spltarray[1]),
								i+2000,"1",orderStatus,typeOfMap, "", zoom, treeStatus,-1);
					}
				}
			}
		}
	}

	private void preparetionordermapmarkershow(){
		tempSiteMap_ARC.clear();

		showOrdersOnmap_ARC();
		showOrderTrees_ARC(alltasksxml, true);
		//showCustSitePoles_ARC(tempSiteMap_ARC, 1);
	}


	private LinkedHashMap<Long, Order> sortOrders(HashMap<Long, Order> odrDataMap , BaseTabActivity ref ) {

		Map<Long, Order> odrDataMapLinkedHmTmeSort = new LinkedHashMap<Long, Order>();
		Map<Long, Order> odrDataMapLinkedHMup = new LinkedHashMap<Long, Order>();
		Map<Long, Order> odrDataMapLinkedHMdw = new LinkedHashMap<Long, Order>();
		if (odrDataMap!=null && odrDataMap.size()>0) {
			odrDataMap = getOrderForCurrentWorker(odrDataMap);

			if (odrDataMap != null && odrDataMap.size() > 0) {//YD check again because map may not have any order after check for current res
				odrDataMapLinkedHmTmeSort = getSortedList(odrDataMap, ref);
				if (changeSequenceNoOnOrder) {
					int i = 1;
					for (Map.Entry entry : odrDataMapLinkedHmTmeSort.entrySet()) {//YD adding sequence before status sort because no need to show order whose status>3
						((Order) entry.getValue()).setSequenceNumber(i);
						i++;
					}
				} else
					changeSequenceNoOnOrder = true;

				for (Long key : odrDataMapLinkedHmTmeSort.keySet()) {
					Order order = odrDataMapLinkedHmTmeSort.get(key);
					long statusID = order.getStatusId();

					if (statusID > 3) {
						odrDataMapLinkedHMdw.put(key, odrDataMapLinkedHmTmeSort.get(key));
					} else {
						odrDataMapLinkedHMup.put(key, odrDataMapLinkedHmTmeSort.get(key));
					}
				}


				odrDataMapLinkedHMup.putAll(odrDataMapLinkedHMdw);
			}
		}
		return (LinkedHashMap<Long, Order>) odrDataMapLinkedHMup;
	}

	private HashMap<Long, Order> getOrderForCurrentWorker(HashMap<Long, Order> odrDataMap) {

		HashMap<Long, Order> orderDatamap = new HashMap<Long, Order>();
		for (Long key : odrDataMap.keySet())
		{
			Order order = odrDataMap.get(key);
			if(order!=null && order.getPrimaryWorkerId()!=null) {
				if (order.getPrimaryWorkerId().contains(String.valueOf(PreferenceHandler.getResId(mActivity)))) {
					orderDatamap.put(key, order);
				}
			}
		}
		return orderDatamap;
	}


	//YD sorting based on time first (check again)
	private Map<Long, Order> getSortedList(Map<Long, Order> listToSort , BaseTabActivity ref )
	{
		ArrayList<Order> unsortedLst = new ArrayList<Order>();
		for (Long key : listToSort.keySet() ){

			Order order = listToSort.get(key);
			long statusID = order.getStatusId();
			Date startdate = getStatDate(order.getFromDate());

			if (PreferenceHandler.getOdrGetDate(ref)!=null && !PreferenceHandler.getOdrGetDate(ref).equals("") && statusID!=8 ){
				if (Utilities.chkDtEqualCurrentDt(ref , startdate)) {
					unsortedLst.add(listToSort.get(key));
				}
			}
			else if (statusID==8 || !Utilities.isTodayDate(ref ,startdate)){// YD Checking if the order date has change from current to some other through web)
				Log.i(BaseTabActivity.LOGNAME, "Order Status is Unscheduled");
			}
			else{
				unsortedLst.add(listToSort.get(key));
			}
		}

		Collections.sort(unsortedLst, new Comparator<Order>() {
			@Override
			public int compare(Order odr1, Order odr2) {
				long time1 = 0, time2 = 0;
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
				try {
					Date date1 = simpleDateFormat.parse(odr1.getFromDate());
					Date date2 = simpleDateFormat.parse(odr2.getFromDate());
					time1 = date1.getTime();
					time2 = date2.getTime();


				} catch (ParseException e) {
					e.printStackTrace();
				}
				return Double.compare(time1, time2);
				//  return  odr1.getStartTime().compareTo(odr2.getStartTime());
			}
		});

		Map<Long, Order> localMap = new LinkedHashMap<Long, Order>();
		for (int i =0; i<unsortedLst.size();i++){
			localMap.put(unsortedLst.get(i).getId(), unsortedLst.get(i));
		}

		return localMap;
	}

}
