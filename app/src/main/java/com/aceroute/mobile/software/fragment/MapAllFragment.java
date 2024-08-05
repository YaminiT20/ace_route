package com.aceroute.mobile.software.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aceroute.mobile.software.AceRouteApplication;
import com.aceroute.mobile.software.BaseTabActivity;
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
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.offline.SurfaceViewMap;
import com.aceroute.mobile.software.offline.application.Channel;
import com.aceroute.mobile.software.requests.CommonSevenReq;
import com.aceroute.mobile.software.requests.EditSiteReq;
import com.aceroute.mobile.software.requests.GetAsset;
import com.aceroute.mobile.software.requests.GetSiteRequest;
import com.aceroute.mobile.software.requests.SaveSiteRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

@SuppressLint("NewApi")
public class MapAllFragment extends BaseFragment implements  RespCBandServST,
 SensorEventListener, HeaderInterface,OnClickListener, IActionOKCancel {

	private static final String TAG = "MapActivity";
	private String headerText;
	private enum MapOption {
		MAP_DISPLAY, ROUTING_AND_NAVIGATION, ANNOTATIONS
	}
	public static final int TRACKS = 1;
	private MyDialog dialog;

	// callbacks
	int SAVEORDERFIELD_STATUS_PG =1;
	int SAVESITE_ACCESS_POINT = 2;
	int SAVESITE_WAY_PATH = 5;
	private int GET_SITE_REQ = 3;
	static int GET_TASKS=4;
	int GET_ASSETS=6;
	int GETSITETYPE = 7;
	int DELETE_SITE = 9;

	private int EDN_FOR_DETAIL_TASK = 599; // For Deatil Task Screen
	private int EDN_FOR_REGULAR_TASK = 600; // For Regular Task Screen

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
	// Custom callout view description
	private TextView popupDescriptionView;
	// Custom view for adding an annotation
	private TextView popup_order_name, popup_order_Id, popup_customer_name, popup_address, total_tree_count_sk;
	private RelativeLayout customView;
	Map<Integer, Channel> channelMap = new TreeMap<Integer, Channel>();
	static String annotatOdr_TaskId= "";
	static String annotatOdr_AssetId= "";
	//Tells if a navigation is ongoing
	private boolean navigationInProgress;
	String response;
	//object annotation
	private Button positionMeButton,headingButton,bottomButton, btnLogwayPoint, btnSaveChanges,
	popup_anno_btn, popup_anno_btn_cam;

	ImageView backbtn , buttonClose, Bt_editList, popup_zoom_btn;
	private static String currentOdrId, currentOdrName;
	private RelativeLayout bottom_show;
	private WebView mWebview;

	private CustomDialog customDialog;
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
	HashMap<Long, Assets> assetsLst = null;
	HashMap<Long, SiteType> siteTypeList = null;

	private Integer Edition;

	static String accesspathpoints=null;
	String annotatOdr_OrderId="0";
	String annotatOdr_OrderNm="";

	SurfaceViewMap surfaceVw;
	HashMap<Long, Site> tempSiteMap = new HashMap<Long, Site>();
	HashMap<Long, Site> allPoleMap = new HashMap<Long, Site>();

	String orderIDForTasklst="0";
	String typeOfMap="";

	public static String maptype;

	public static String lstAccPathStr;

	private HashMap<Integer, String> mapOfAcesPth = null;
	private HashMap<Integer, Integer> mapTmpAcesPath = null;
	private boolean btnUpdateShow = false;
	private static int total_accessPath;

	protected int AccPathId2Delete;
	ImageView navigateArrow ,currentLocBtn;

	private Long[] orderxmlKeys;
	private Long[] tasksxmlKeys;
	private Long[] custSitexmlKeys;
	private Long[] siteTypexmlKeys;
	private Long[] allSitexmlKeys;

	public static int FIELD_UPDATED=0;
	private boolean isDialogForUpdate = false;
	private boolean isDialogForUpdate_OnBack = false;
	private int isEditTree = 0;

	private static long waypointId = 0;
	private static long accessPointId = 0;
	private long odr_customerId = 0;
	private int breadcrumbs_counter = 0;

	private String access_point_geo = null;
	private String accesspath_lat_long = null;
	ArrayList<String> mStatusTypeArryList, mStatusTypeArryListId ;//YD need to change later not the good way
	/*String[] strStatusType = {"Open", "Pending", "Refusal", "Review", "Complete","False Detection", "No Tree"};
	String[] strStatusTypeIds = {"0","1", "2", "3", "4","5","6"};*/

	String[] strStatusType = {"Open", /*"Pending",*/ "Closed","False Detection", "No Tree","Refusal", "Review"};
	String[] strStatusTypeIds = {"0",/*"1",*/ "4", "5", "6","2","3"};
	private boolean shouldDelSite = false;//YD Using to check if we want to delete the site having breadcrumbs

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Edition = PreferenceHandler.getPrefEditionForGeo(getActivity());
		Log.e("Edition :", String.valueOf(Edition));
	}


	View viewfrag;
	@Override
	public View onCreateView(LayoutInflater inflaterfrag, ViewGroup container, Bundle savedInstanceState) {
		viewfrag = inflaterfrag.inflate(R.layout.mapfragment, container, false);
		return viewfrag;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Utilities.log(mActivity, "onStart called for mapAllFragment");
		((LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE)).requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				1000 * 10, 1, new Utilities());
		//Utilities.getLocation(mActivity);
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

		Site.saveData(req, mActivity, this, SAVESITE_WAY_PATH);
	}


	// Save Access Point
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

	// get Customer Site
	private void getCustSites(long customerid) {
		// TODO Auto-generated method stub
		GetSiteRequest req = new GetSiteRequest();
		req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		req.setCid(String.valueOf(customerid));
		req.setSource("localonly");
		req.setAction("getsite");

		Site.getData(req,mActivity, this, GET_SITE_REQ);
	}


	@Override
	public void onResume() {
		super.onResume();
		Log.i("data", "onResume");
		if (headingOn) {
			startOrientationSensor();
		}
	}


	@Override
	public void onPause() {
		super.onPause();
		((LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE)).requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				1000 * 60, 5, new Utilities());

		Log.i("data", "onpause");
		if (headingOn) {
			stopOrientationSensor();
		}
		if (mSensorManager!=null)
			mSensorManager.unregisterListener(this);
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
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("data", "onDestroy");
		//SKMaps.getInstance().destroySKMaps();
		//android.os.Process.killProcess(android.os.Process.myPid());
	}


	private SensorManager mSensorManager;
	Sensor accelerometer;
	Sensor magnetometer;
	/**
	 * Activates the orientation sensor
	 */
	private void startOrientationSensor() {
		/*	SensorManager sensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        Sensor orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_UI);*/

		// Register the sensor listeners
		mSensorManager = (SensorManager)mActivity.getSystemService(mActivity.SENSOR_SERVICE);
		accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
	}

	/**
	 * Deactivates the orientation sensor
	 */
	private void stopOrientationSensor() {
		SensorManager sensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
		sensorManager.unregisterListener(this);
	}

	/**
	 * Launches a single route calculation
	 */
	private void launchRouteCalculation() {
//		SKRouteSettings route = new SKRouteSettings();
//		SKCoordinate currentCoordinate = new SKCoordinate();;
//		//SKPosition currentPosition = mapView.getCurrentGPSPosition(false);
//		SKPosition currentPosition = SKPositionerManager.getInstance().getCurrentGPSPosition(false);
//		currentCoordinate.setLatitude(currentPosition.getCoordinate().getLatitude());
//		currentCoordinate.setLongitude(currentPosition.getCoordinate().getLongitude());
//		route.setStartCoordinate(currentCoordinate);
//		route.setDestinationCoordinate(new SKCoordinate(79.01929969999992, 30.066753));
//		//route.setNoOfRoutes(1);
//		route.setMaximumReturnedRoutes(1);
//		route.setRouteMode(SKRouteSettings.SKRouteMode.CAR_FASTEST);
//		route.setRouteExposed(true);
//		SKRouteManager.getInstance().setRouteListener(this);
//		SKRouteManager.getInstance().calculateRoute(route);
	}

	/**
	 * Launches a navigation on the current route
	 *

	/**
	 * Stops the navigation
	 */
	private void stopNavigation() {
		navigationInProgress = false;
		clearMap();
//		SKNavigationManager.getInstance().stopNavigation();

		bottomButton.setVisibility(View.GONE);
	}


	public void showOrderTrees(HashMap<Long, OrderTask> datalist, boolean zoom)
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
							orderStatus = String.valueOf(order.getStatusId());
							break;
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
							for (int k =0 ;k <mStatusTypeArryListId.size();k++){
								if (mStatusTypeArryListId.get(k).equals(ordertask.getAction_status()))
									elementStatId = k;
							}
							channel.setAdressid(mStatusTypeArryList.get(elementStatId));  // Tree Status
						}

						channelMap.put(i+2000, channel);

						preparemapshowmarker(Double.parseDouble(spltarray[0]),
								Double.parseDouble(spltarray[1]),
								i+2000,"1",orderStatus,typeOfMap, "", zoom, treeStatus);
					}
				}
			}
		}
	}


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
							orderStatus = String.valueOf(order.getStatusId());
						}
					}
				}

				if(custSite !=null){

					String siteName = custSite.getNm();
					String siteNew = siteName.toLowerCase();

					long siteTid = custSite.getTid();
					//if(siteNew.contains("*default")){
					if(siteTid == 90003)
					{
						String geo = custSite.getGeo();
						if (geo!=null && geo!=""){
							String geoPoles[] = geo.split(",");

							preparemapshowmarker(Double.parseDouble(geoPoles[0].trim()), Double.parseDouble(geoPoles[1].trim()),
									i+70000,"0", orderStatus,typeOfMap, siteNew, mapZoom,"");

							Channel channel = new Channel();
							channel.setO_nameid(String.valueOf(custSite.getNm())); // get Contact Name after discussion
							channelMap.put(i+70000, channel);
						}
					}
					//}else if (siteNew.contains("*pole")) {
					else if (siteTid == 90002) {
						String geo = custSite.getGeo();
						if (geo!=null && geo!=""){
							String geoPoles[] = geo.split(",");

							/*if(orderStatus.equals("")){
								nodes.add(new SKCoordinate(Double.parseDouble(geoPoles[1].trim()), Double.parseDouble(geoPoles[0].trim())));
							}else if(orderStatus.equals("0") || orderStatus.equals("1") || orderStatus.equals("2") || orderStatus.equals("3")){
								nodes.add(new SKCoordinate(Double.parseDouble(geoPoles[1].trim()), Double.parseDouble(geoPoles[0].trim())));
							}*/

							preparemapshowmarker(Double.parseDouble(geoPoles[0].trim()), Double.parseDouble(geoPoles[1].trim()),
									i+70000,"0", orderStatus,typeOfMap, siteNew, mapZoom,"");

							Channel channel = new Channel();
							channel.setO_nameid(String.valueOf(custSite.getNm()));// putting Tree Type here important
							channelMap.put(i+70000, channel);
						}
					}else if(siteNew.contains("*access point")){
						String geo = custSite.getGeo();
						if (geo!=null && geo!=""){
							String geoPoles[] = geo.split(",");

							access_point_geo = geo;

							if(accesspath_lat_long !=null && accesspath_lat_long!=""){
								// if user drag access point
								String geoNew[] = accesspath_lat_long.split(",");
								preparemapshowmarker(Double.parseDouble(geoNew[0].trim()), Double.parseDouble(geoNew[1].trim()),
										i+70000,"0", orderStatus,typeOfMap, siteNew, mapZoom,"");
							}else{
							preparemapshowmarker(Double.parseDouble(geoPoles[0].trim()), Double.parseDouble(geoPoles[1].trim()),
									i+70000,"0", orderStatus,typeOfMap, siteNew, mapZoom,"");
							}

							Channel channel = new Channel();
							channel.setO_nameid(String.valueOf(custSite.getNm()));// putting Tree Type here important
							channelMap.put(i+70000, channel);
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

									preparemapshowmarker(Double.parseDouble(geoPoles[0].trim()), Double.parseDouble(geoPoles[1].trim()), j+10000, "", orderStatus, "accesspath", "", mapZoom,"");
									breadcrumbs_counter ++;
								}
							}
							total_accessPath = mapOfAcesPth.size(); // total Way Point
						}
					}
				}
			}
			/*polygon.setNodes(nodes);
			// set the outline size
			polygon.setOutlineSize(3);
			// set colors used to render the polygon
			//	polygon.setOutlineColor(new float[] { 1f, 0f, 0f, 1f }); //red color
			//	polygon.setColor(new float[] { 1f, 0f, 0f, 0.2f }); // red color

			polygon.setColor(new float[] { 1f, 1f, 0.5f, 0.47f }); // black color
			polygon.setOutlineColor(new float[] { 0f, 0f, 0f, 1f }); // black color

			// render the polygon on the map
			mapView.addPolygon(polygon);*/
		}
	}

	private void showOrderAssets(HashMap<Long, Assets> assetsLst, boolean b) {
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
				channelMap.put(i + 90000, channel);

				if (spltarray!=null && spltarray.length>1)
				{
					preparemapshowmarker(Double.parseDouble(spltarray[0]),
							Double.parseDouble(spltarray[1]),
							i + 90000, "0", "" + activeOrderObj.getStatusId(), typeOfMap, "", b,""/* orderAst.getStatus()*/);//YD 2020

				}
				i++;
			}
		}
	}

	public void showOrdersOnmap()
	{
		if(orderxml !=null){
			typeOfMap = "orders";

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
				channelMap.put(i, channel);

				if (maptype.equals("OrderList"))
				{
					preparemapshowmarker(Double.parseDouble(spltarray[0]),
							Double.parseDouble(spltarray[1]),
							i,"0",""+order.getStatusId(),typeOfMap, "", true,"");

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


	private void preparetionTreeMapmarkershow(boolean mapZoom){
//		mapView.deleteAllAnnotationsAndCustomPOIs();
//		mapView.clearAllOverlays();

		showOrder(mapZoom);
	}


	private void preparetionordermapmarkershow(){
		tempSiteMap.clear();

		showOrdersOnmap();
		showOrderTrees(alltasksxml, true);
		showCustSitePoles(tempSiteMap, 1);
	}

private void preparemapshowmarker(double lat,double lng,int i, String focusSet, String orderStat, String typeOfMap, String siteType, boolean mapZoom, String treeStat) {

//		SKAnnotation annotationDrawable = new SKAnnotation(i);
		objectannomation(i);
		// set unique id used for rendering the annotation
//		annotationDrawable.setUniqueID(i);

//		//yash
//		SKCoordinate currentCoordinate = new SKCoordinate(lng,lat);

		// set annotation location
//		annotationDrawable.setLocation(currentCoordinate);
//		//annotation.setLocation(new SKCoordinate(lng,lat));
//		// set minimum zoom level at which the annotation should be visible
//		annotationDrawable.setMininumZoomLevel(5);

		//  annotationDrawable.setOffset(new SKScreenPoint(64, 64));
//		SKAnnotationView annotationView = new SKAnnotationView();
		// getting  super view to use its innerviews like imageview , button etc
		customView = (RelativeLayout) ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.customannotation, null, false);
		ImageView imageview = (ImageView)customView.findViewById(R.id.customView);

		if (typeOfMap.equals("orders")){
			/*if(orderStat.equals("")){
				if(Edition < 600){
					imageview.setImageResource(R.drawable.balloon_b);
				}
			}else if (orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				if(Edition < 600){
					imageview.setImageResource(R.drawable.balloon_b);
				}
			}
			else if (Integer.parseInt(orderStat)>3){
				if(Edition < 600){
					imageview.setImageResource(R.drawable.balloon_grey);
				}
			}*/
			imageview.setImageResource(R.drawable.access_point);//YD
		}
		else if (typeOfMap.equals("tasks_pole")){
			//if(orderStat.equals("")){
				if(siteType.contains("*pole")){
					imageview.setImageResource(R.drawable.tower);
				}else if(siteType.contains("*default")){
					imageview.setImageResource(R.drawable.home_b);
				}else if(siteType.contains("*access point")){
					//imageview.setImageResource(R.drawable.access_point);//YD COMMENTING because making 2 taskpole one for order and one for site accespoint
				}
			/*}else if(orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")) {
				if(siteType.contains("*pole")){
					imageview.setImageResource(R.drawable.tower);
				}else if(siteType.contains("*default")){
					imageview.setImageResource(R.drawable.home_b);
				}else if(siteType.contains("*access point")){
					imageview.setImageResource(R.drawable.access_point);
				}
			}*///YD commenting because no need to show site irrespective of any order status
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
				imageview.setImageResource(treeIcon);
			}
		}
		else  if (typeOfMap.equals("tasks_path")){
			if(orderStat.equals("") || orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				imageview.setImageResource(R.drawable.dot);
			}
		}
		else if (typeOfMap.equals("tasks_path_complete")){
			if(orderStat.equals("") || orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				imageview.setImageResource(R.drawable.gray_dot_accpath);
			}
		}
		else  if (typeOfMap.equals("accesspath")){
			//if(orderStat.equals("") || orderStat.equals("0") || orderStat.equals("1") || orderStat.equals("2") || orderStat.equals("3")){
				imageview.setImageResource(R.drawable.dot);
			//}//YD removing if because need to show dot irrespective of any order status
		}
		else if (typeOfMap.equals("assets")) {
			if (PreferenceHandler.getPrefEditionForGeo(mActivity)==900) {// show assest icon
				int treeIconAst ;// YD green for null and open
				if (treeStat.equals("8")) {
					treeIconAst = R.drawable.tree_new_1;
				} else if (treeStat.equals("9")) {
					treeIconAst = R.drawable.tree_new_2;// No Tree or False Detection
				} else if (treeStat != null && treeStat.equals("")) {
					treeIconAst = R.drawable.tree_new_3; // Closed
				} else{
					treeIconAst = R.drawable.tree_new_4;
				}
				imageview.setImageResource(treeIconAst);
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
				imageview.setImageResource(treeIconAst);
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
				imageview.setImageResource(treeIconAst);
			}
		}
			// set the width and height of the image in pixels (they have to be
		// powers of 2)
		//annotationView.setWidth(32);
		//annotationView.setHeight(32);
//		annotationView.setView(customView);
//		annotationDrawable.setAnnotationView(annotationView);
//		// render annotation on map
//		mapView.addAnnotation(annotationDrawable, SKAnimationSettings.ANIMATION_NONE);
//		// set map zoom level
//
//		//yash
//		if (/*focusSet.equals("1")*/mapZoom)
//		{
//			currentCoordinate.setLatitude(lat);
//			currentCoordinate.setLongitude(lng);
//			mapView.setPositionAsCurrent(currentCoordinate, 0, true);
//		}

		if(maptype.equals("TreeList")) {
//			if(mapZoom)
//				mapView.setZoom(10);//YD 18
		}
		else{
//			if(mapZoom)
//				mapView.setZoom(10);//YD 12
		}
	}

	private void objectannomation(int i) {
		// TODO Auto-generated method stub
//		annotation= new SKAnnotation(i);

	}

	@SuppressLint("ResourceAsColor")
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Bt_zoom:
//			mapPopup.setVisibility(View.GONE);
			if(maptype.equals("OrderList")){
				if(Edition < EDN_FOR_REGULAR_TASK){
					OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
					Bundle mBundle=new Bundle();
					mBundle.putString("OrderId", String.valueOf(annotatOdr_OrderId));
					orderDetailFragment.setArguments(mBundle);

					mActivity.pushFragments(Utilities.JOBS, orderDetailFragment, true, true,BaseTabActivity.UI_Thread);
				}
			}else if(maptype.equals("TreeList")){
				if(Edition < EDN_FOR_REGULAR_TASK){
					OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
					Bundle mBundle=new Bundle();
					mBundle.putString("OrderId", String.valueOf(annotatOdr_OrderId));
					orderDetailFragment.setArguments(mBundle);

					mActivity.pushFragments(Utilities.JOBS, orderDetailFragment, true, true,BaseTabActivity.UI_Thread);
				}
			}

			break;
		case R.id.annotationMapBtn:

//			mapPopup.setVisibility(View.GONE);
			if(maptype.equals("OrderList")){
				//activityMn.callJsFrmMapAnnot(annotatOdr_TaskId ,"OrderListOpenTask");// YD later TODO;
			}

			break;

		case R.id.annotationMapBtnCam:
//			mapPopup.setVisibility(View.GONE);
			if(maptype.equals("OrderList")){

			}
			break;

		case R.id.right_image:
//			mapPopup.setVisibility(View.GONE);

			Assets astObj = assetsLst.get(Long.valueOf(annotatOdr_AssetId));
			AddEditAssetFragment_OLD addEditAssetFragment = new AddEditAssetFragment_OLD();
			Bundle bundle = new Bundle();
			bundle.putString("AssetType", "EDIT ASSET");
			bundle.putString("OrderId", currentOdrId);
			bundle.putString("OrderName", currentOdrName);

			//bundle.putLong("AssetTypeId", astObj.getTid());
            //bundle.putLong("partQuantity", astObj.getContact1());//YD 2020
            bundle.putLong("orderAssetId", astObj.getId());

			addEditAssetFragment.setAssetObject(astObj);
			addEditAssetFragment.setActiveOrderObject(activeOrderObj);
			addEditAssetFragment.setArguments(bundle);
			mActivity.pushFragments(Utilities.JOBS, addEditAssetFragment, true, true, BaseTabActivity.UI_Thread);
			break;

		case R.id.btnLogwayPoint_sk:

			String currentPoint = Utilities.getLocation(mActivity.getApplicationContext());
			Log.e("Current GPS :", String.valueOf(currentPoint));

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mWebview.setVisibility(View.VISIBLE);
				}
			});

//			SKCoordinate currentCoordinate = new SKCoordinate();

			if(currentPoint!=null && !currentPoint.toLowerCase().contains("null") && !currentPoint.equals("")){
				String spltarray[] = currentPoint.toString().split(",");
				if(!spltarray[0].equals("0.0") || !spltarray[1].equals("0.0"))
				{
					btnSaveChanges.setEnabled(true);
					btnSaveChanges.setBackground(getResources().getDrawable(R.drawable.btn_save_update_selector));

					lstAccPathStr="";
					FIELD_UPDATED = 1;
					btnUpdateShow=true;

					if(mapOfAcesPth==null)
						mapOfAcesPth = new HashMap<Integer, String>();

				//	initAccPathLstLen = mapOfAcesPth.size();
				//	mapOfAcesPth.put(initAccPathLstLen+10000, currentPoint);
					mapOfAcesPth.put(breadcrumbs_counter+10000, currentPoint);

					for(int i=0; i < mapOfAcesPth.size(); i++){
						if(i==0)
							lstAccPathStr = mapOfAcesPth.get(i);
						else
							lstAccPathStr = lstAccPathStr+"|"+mapOfAcesPth.get(i);
					}

				//	preparemapshowmarker(Double.parseDouble(spltarray[0]), Double.parseDouble(spltarray[1]), initAccPathLstLen+10000, "0", "", "accesspath", "", false);
					preparemapshowmarker(Double.parseDouble(spltarray[0]), Double.parseDouble(spltarray[1]), breadcrumbs_counter+10000, "0", "", "accesspath", "", false,"");

					if(mapTmpAcesPath==null){
				    	mapTmpAcesPath = new HashMap<Integer, Integer>();
				    }

				    //mapTmpAcesPath.put(initAccPathLstLen+10000, initAccPathLstLen+10000);
					mapTmpAcesPath.put(breadcrumbs_counter+10000, breadcrumbs_counter+10000);

					breadcrumbs_counter ++;

					// commented by mandeep
					/*
					currentCoordinate.setLatitude(Double.parseDouble(spltarray[0]));
					currentCoordinate.setLongitude(Double.parseDouble(spltarray[1]));
					mapView.setPositionAsCurrent(currentCoordinate, 0, true);
					mapView.setZoom(13);*/
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

		case R.id.Bt_editList:

			/*if (!btnUpdateShow) {
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
										odr_customerId, accessPointId); // Save
																		// Access
																		// Point
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
				dialog.onCreate1(null);
				dialog.show();
			}*///YD earlier using this to open task page on info window button click listener
			break;

		case R.id.btnSaveChanges_sk:
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

		case R.id.currentLocationIcon:
//			SKCoordinate currentCoord = new SKCoordinate();
//			//SKPosition currentPosition = mapView.getCurrentGPSPosition(false);
//			SKPosition currentPosition = SKPositionerManager.getInstance().getCurrentGPSPosition(false);
//
//			if (currentPosition.getCoordinate().getLatitude()!=0 && currentPosition.getCoordinate().getLongitude()!=0)
//			{
//				currentCoord.setLatitude(currentPosition.getCoordinate().getLatitude());//(36.778261);
//				currentCoord.setLongitude(currentPosition.getCoordinate().getLongitude());//(-119.417932);//currentPosition.getLongitude()
//				mapView.setPositionAsCurrent(currentCoord, 0, true);
//				mapView.setZoom(10);
//			}

		break;
		default:
			break;
		}
	}


	private void clearMap() {
		setHeading(false);
		switch (currentMapOption) {
		case MAP_DISPLAY:
			break;
		case ROUTING_AND_NAVIGATION:
			bottomButton.setVisibility(View.GONE);
			if (navigationInProgress) {
				// stop navigation if ongoing
				stopNavigation();
//				SKRouteManager.getInstance().clearCurrentRoute();
				routeIds.clear();
			}
			break;
			/*  case ANNOTATIONS:
		    mapPopup.setVisibility(View.GONE);
		    // removes the annotations and custom POIs currently rendered
		    mapView.deleteAllAnnotationsAndCustomPOIs();
		    break;*/
		default:
			break;
		}
		currentMapOption = MapOption.MAP_DISPLAY;
		positionMeButton.setVisibility(View.VISIBLE);
		headingButton.setVisibility(View.VISIBLE);
	}

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
			}
		}
		//mCustomDrawableView.invalidate();
		if(azimut!=null)
		{
			float degree = (-azimut*360/(2*3.14159f));
			rotate(degree);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	private void setHeading(boolean enabled) {
		if (enabled) {
			headingOn = true;
			startOrientationSensor();
		} else {
			headingOn = false;
			stopOrientationSensor();
		}
	}


	// get called automatically when fragment (map fragment) is show/hide(CASE : HIDE/SHOW)
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden) {
			//do when hidden
		} else {
			//do when show
			if(maptype.equals("OrderList"))
				checkforOrderDataLoading();
			if(maptype.equals("TreeList")){
				checkforTasksDataLoading();
			//	checkforAccessPathDataLoading();
			}


		}
	}

	//loading********************************
	private void checkforOrderDataLoading()
	{
		parseallSiteXML();
		preparetionordermapmarkershow();
	}

	private void checkforTasksDataLoading()
	{
		if(tasksxmlversion!=tasksxmlusedversion || custSitexmlversion!=custSitexmlusedversion)
		{
			tasksxmlusedversion = tasksxmlversion;
			custSitexmlusedversion = custSitexmlversion;
			orderxmlusedversion = orderxmlversion;

		}
		if (maptype.equals("TreeList")){
			preparetionTreeMapmarkershow(true);
			if(Edition > EDN_FOR_DETAIL_TASK){
				bottom_show.setVisibility(View.VISIBLE);
			}
		}
	}

	//setting up Dataobject**********************
	public static void setMapOrderXML(HashMap<Long, Order> xml)
	{
		orderxml = xml;
		orderxmlversion++;
	}

	public static void setMapTasksXML(HashMap<Long, OrderTask> xml)
	{
		tasksxml = xml;
		//tasksxml="<data><osrv><oid>23598003</oid><id>1427708500306</id><tid>22478002</tid><upd>1428140498060</upd><ltid>22808003</ltid><prev_id>1</prev_id><stat>0</stat><note5></note5><num1></num1><num2></num2><num3></num3><num4></num4><ehrs>0</ehrs><hrs>0</hrs><num5></num5><num6></num6><note6></note6><note1></note1><note2></note2><note3></note3><note4></note4><geo>28.6387530,77.0738030</geo><glist></glist><ct1>1</ct1><ct2>1</ct2><ct3>1</ct3></osrv></data>";
		tasksxmlversion++;
	}


	public static void setMapCustSiteXML(HashMap<Long, Site> xml)
	{
		custSitexml = xml;
		//custSitexml ="<data><loc><id>22928008</id><cid>23008012</cid><nm>*Default</nm><adr>2800 Napa Road, Sonoma, CA pole</adr><upd>1427359311382</upd><tid>0</tid><ltpnm /><geo>28.6366437,77.0775366</geo></loc><loc><id>22928008</id><cid>23008012</cid><nm>*Default</nm><adr>2800 Napa Road, Sonoma, CA pole</adr><upd>1427359311382</upd><tid>0</tid><ltpnm /><geo>28.6397888,77.0716787</geo></loc><loc><id>22928008</id><cid>23008012</cid><nm>*Default</nm><adr>2800 Napa Road, Sonoma, CA pole</adr><upd>1427359311382</upd><tid>0</tid><ltpnm /><geo>28.6375665,77.0724941</geo></loc></data>";
		custSitexmlversion++;
	}

	public static void setMapTasktypeXML(HashMap<Long,ServiceType> xml)
	{
		taskTypexml = xml;
	}

	public static void setMapOrderSiteType(HashMap<Long, SiteType> xml)
	{
		siteTypexml = xml;
		//custSitexml ="<data><loc><id>22928008</id><cid>23008012</cid><nm>*Default</nm><adr>2800 Napa Road, Sonoma, CA pole</adr><upd>1427359311382</upd><tid>0</tid><ltpnm /><geo>28.6366437,77.0775366</geo></loc><loc><id>22928008</id><cid>23008012</cid><nm>*Default</nm><adr>2800 Napa Road, Sonoma, CA pole</adr><upd>1427359311382</upd><tid>0</tid><ltpnm /><geo>28.6397888,77.0716787</geo></loc><loc><id>22928008</id><cid>23008012</cid><nm>*Default</nm><adr>2800 Napa Road, Sonoma, CA pole</adr><upd>1427359311382</upd><tid>0</tid><ltpnm /><geo>28.6375665,77.0724941</geo></loc></data>";
	}

	public static void setAccesspathpointsForTreeMap(String pathPoint) {

		accesspathpoints = pathPoint;
	}

	// TODO Mandeep
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

	private void showOrder(boolean mapZoom){
		if(activeOrderObj!=null){
			int i=0;
			String tempDevs[] = activeOrderObj.getCustSiteGeocode().split(",");
			String lat = tempDevs[0];
			String lng=tempDevs[1];
			String oderStat = String.valueOf(activeOrderObj.getStatusId());

			Channel channel = new Channel();
			channel.setCid(i);
			channel.setOrderId(String.valueOf(activeOrderObj.getId()));
			channel.setO_nameid(activeOrderObj.getNm());
			channel.setCustomer(activeOrderObj.getCustName());
			channel.setAdressid(activeOrderObj.getCustSiteStreeAdd());
			channelMap.put(i, channel);

			preparemapshowmarker(Double.parseDouble(lat), Double.parseDouble(lng), i, "1", oderStat, "orders", "", mapZoom,"");
		}
	}

	float currentdegree = 0;
	private Order activeOrderObj;
	private void rotate(float degree) {

		float diff = 0;
		if(currentdegree>degree)
			diff = currentdegree-degree;
		else
			diff = degree-currentdegree;
		if(diff>10)
		{


			final RotateAnimation rotateAnim = new RotateAnimation(currentdegree, degree,
					RotateAnimation.RELATIVE_TO_SELF, 0.5f,
					RotateAnimation.RELATIVE_TO_SELF, 0.5f);

			rotateAnim.setDuration(0);
			rotateAnim.setFillAfter(true);
			navigateArrow.startAnimation(rotateAnim);
			currentdegree =degree;
		}
	}

	@Override
	public void headerClickListener(String callingId) {
		// TODO Auto-generated method stub
		if(callingId.equals(BaseTabActivity.HeaderMapSettingPressed)){


			final PopupWindow popview = Utilities.show_Map_popupmenu(getActivity(),mActivity.headerApp.getHeight());
			View view = popview.getContentView();
			TextView menu_normal = (TextView)view.findViewById(R.id.menu_normal);
			TextView menu_satellite = (TextView)view.findViewById(R.id.menu_satellite);
			TextView menu_hybrid = (TextView)view.findViewById(R.id.menu_hybrid);
			//TextView menu_offline = (TextView)view.findViewById(R.id.menu_offline);
			TextView menu_offline_imagery = (TextView)view.findViewById(R.id.menu_offline_imagery);

			if (PreferenceHandler.getArcgisMapTpkState(mActivity)) {
				menu_normal.setText("Online Street");
				menu_satellite.setVisibility(View.GONE);
				menu_hybrid.setVisibility(View.GONE);
				//menu_offline.setVisibility(View.GONE);
				menu_offline_imagery.setVisibility(View.VISIBLE);
			}
			else{
				menu_normal.setVisibility(View.GONE);
				menu_satellite.setVisibility(View.GONE);
				menu_hybrid.setVisibility(View.GONE);
				//menu_offline.setText("Online Street");
				menu_offline_imagery.setVisibility(View.GONE);
			}

			menu_normal.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (Utilities.checkInternetConnection(mActivity, false) /*&& new SpeedTestLauncher().bindListeners() > 20*/) {
						Fragment fragment = BaseTabActivity.mStacks.get(Utilities.JOBS).elementAt(
								BaseTabActivity.mStacks.get(Utilities.JOBS).size() - 2);

						if ( fragment!=null && fragment instanceof GoogleMapFragment){
							mActivity.popFragments(BaseTabActivity.UI_Thread);
						}
						else {

							BaseTabActivity.addedNewMap = true;

							Bundle mBundles = new Bundle();
							mBundles.putString("OrderId", String.valueOf(activeOrderObj.getId()));
							mBundles.putString("OrderName", String.valueOf(activeOrderObj.getNm()));

							GoogleMapFragment.maptype = "TreeList";
							GoogleMapFragment GooglemapFragment = new GoogleMapFragment();
							GooglemapFragment.setArguments(mBundles);
							GooglemapFragment.setActiveOrderObject(activeOrderObj);
							mActivity.pushFragments(Utilities.JOBS, GooglemapFragment, true, true, BaseTabActivity.UI_Thread);
						}
					} else {
						if (Utilities.checkInternetConnection(mActivity, false)) {
							Toast.makeText(mActivity, "No Internet Connection.", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(mActivity, "Low Network Speed.", Toast.LENGTH_LONG).show();
						}
					}
					popview.dismiss();
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
					BaseTabActivity.addedNewMap = true;

					Bundle mBundles = new Bundle();
					mBundles.putString("OrderId", String.valueOf(activeOrderObj.getId()));
					mBundles.putString("OrderName", String.valueOf(activeOrderObj.getNm()));
				}
			});

		}
		else if(callingId.equals(mActivity.HeaderlastSyncPressed)){
			if(maptype.equals("OrderList")){
				mActivity.onBackPressed();
			}else if(maptype.equals("TreeList")){
				OrderFormsFragment orderDetailTreeFragment = new OrderFormsFragment();
				mActivity.OrderTaskBackOdrId = Long.valueOf(currentOdrId);

				Bundle taskBundle=new Bundle();
				taskBundle.putString("OrderId", String.valueOf(currentOdrId));
				taskBundle.putString("OrderName", String.valueOf(currentOdrName));
				orderDetailTreeFragment.setArguments(taskBundle);
				mActivity.pushFragments(Utilities.JOBS, orderDetailTreeFragment, true, true,BaseTabActivity.UI_Thread);
			}
		}else if(callingId.equals(mActivity.HeaderTaskUserPressed)){
			if(maptype.equals("TreeList")){
				mActivity.onBackPressed();
			}
		}else if (callingId.equals(BaseTabActivity.HeaderPlusPressed)){//YD
			if(!btnUpdateShow){
				AddEditAssetFragment_OLD addEditAssetFragment = new AddEditAssetFragment_OLD();
				Bundle bundle = new Bundle();
				bundle.putString("AssetType", "ADD ASSET");
				bundle.putString("OrderId", currentOdrId);
				bundle.putString("OrderName", currentOdrName);
				addEditAssetFragment.setArguments(bundle);
				addEditAssetFragment.setActiveOrderObject(activeOrderObj);
				mActivity.pushFragments(Utilities.JOBS, addEditAssetFragment, true, true,BaseTabActivity.UI_Thread);
			}else{//YD show dialog if breadcrumb is added but not saved yet
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
		}else if(callingId.equals(BaseTabActivity.HeaderBackPressed))
		{
			if(btnUpdateShow){//YD show dialog if breadcrumb etc is added but not saved yet
				isDialogForUpdate_OnBack = true;
				dialog = new MyDialog(mActivity, getResources().getString(R.string.lbl_tree_updates), getResources().getString(R.string.lbl_upd_message), "YES");
				dialog.setkeyListender(new MyDiologInterface() {
					@Override
					public void onPositiveClick() throws JSONException {
						// No Button Click
						isDialogForUpdate_OnBack = false;
						btnUpdateShow=false;
						FIELD_UPDATED=0;
						goBack(mActivity.SERVICE_Thread);
						dialog.dismiss();
					}

					@Override
					public void onNegativeClick() {
						// Yes Button Click
						btnUpdateShow=false;
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
			}else{
				bottom_show.setVisibility(View.GONE);

				if(mapTmpAcesPath!=null && mapTmpAcesPath.size()>0 && mapOfAcesPth.size()>0){
//					mapView.clearAllOverlays();
					lstAccPathStr ="";
					breadcrumbs_counter=0;
					mapTmpAcesPath.clear();
					mapOfAcesPth.clear();
				}


				if(access_point_geo!=null && accesspath_lat_long!=null){
					accesspath_lat_long ="";
				}

				btnSaveChanges.setEnabled(false);
				final int sdk = android.os.Build.VERSION.SDK_INT;
				if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
					btnSaveChanges.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_logwaypoint_selector));
				} else {
					btnSaveChanges.setBackground(getResources().getDrawable(R.drawable.btn_logwaypoint_selector));
				}
			}
		}
		else if(callingId.equals(BaseTabActivity.HeaderDonePressed))
		{
			//YD code taken from btnSaveChanges as we have to now save on header done btn
			/*if(access_point_geo!=null && accesspath_lat_long!=null)
			{
				String arrTemp[] = access_point_geo.split(",");
				String arrGeo[] = accesspath_lat_long.split(",");

				if(!arrTemp[0].equals(arrGeo[0]) && !arrTemp[1].equals(arrGeo[1])){
					saveAccessPointLocation(accesspath_lat_long, odr_customerId, accessPointId); // Save Access Point
				}
			}*///yd no need of this as now we r not allowing to change assess point

			/*****************YD no need of drag logic implementation as drag is not possible in skobbler****************/

			if(/*mapTmpAcesPath!=null && mapTmpAcesPath.size()>0 ||*/ mapOfAcesPth!=null && mapOfAcesPth.size()!= total_accessPath && mapOfAcesPth.size()>0){
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

				Site.deleteData(req, mActivity, MapAllFragment.this, DELETE_SITE);
			}

			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mWebview.setVisibility(View.VISIBLE);
				}
			});
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
			mapOfAcesPth.remove(AccPathId2Delete);
			int i=0;
			Iterator<Entry<Integer,String>> iter = mapOfAcesPth.entrySet().iterator();
		    while (iter.hasNext()) {
		    	Entry<Integer, String> entry = iter.next();

		    	String geo[] = mapOfAcesPth.get(entry.getKey()).split(",");
		    	if(i==0)
					lstAccPathStr = mapOfAcesPth.get(entry.getKey());
				else
					lstAccPathStr= lstAccPathStr+"|"+mapOfAcesPth.get(entry.getKey());

		        preparemapshowmarker(Double.parseDouble(geo[0]), Double.parseDouble(geo[1]), entry.getKey(), "", "", "accesspath", "", false,"");
		        i++;
		    }

		    if(AccPathId2Delete>=10000 && AccPathId2Delete<20000 && mapTmpAcesPath!=null && mapTmpAcesPath.size()>0){
				Iterator<Entry<Integer,Integer>> itrTemp = mapTmpAcesPath.entrySet().iterator();
			    while (itrTemp.hasNext()) {
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

			showOrder(false);
			showOrderAssets(assetsLst, false);
			showCustSitePoles(custSitexml, 0);

			if(btnUpdateShow) {
				mActivity.replaceBtnHeader("plusDone");
			}else {
				mActivity.replaceBtnHeader("donePlus");
			}

			 AccPathId2Delete = 0;
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
						//mWebview.setVisibility(View.GONE);
						mActivity.replaceBtnHeader("donePlus");
					}
				}, 1000);
			}
		});
	}

	@Override
	public void onActionCancel(int requestCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActionNeutral(int requestCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void ServiceStarter(RespCBandServST activity, Intent intent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setResponseCallback(String response, Integer reqId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setResponseCBActivity(Response response) {
		// TODO Auto-generated method stub
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
					}else if(maptype.equals("TreeList")){
						custSitexml = (HashMap< Long, Site>)response.getResponseMap();
						mActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								showCustSitePoles(custSitexml, 1);
							}
						});
					}
				}

				/*if(response.getId()==GET_TASKS){
					tasksxml = (HashMap<Long, OrderTask>)response.getResponseMap();
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							showOrderTrees(tasksxml, true);
						}
					});
				}*/

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
						getAsset();
					}
				}

				if (response.getId()==DELETE_SITE) {
					shouldDelSite = false;
					waypointId = 0;
					total_accessPath = mapOfAcesPth.size();
					stopLoading();
					btnUpdateShow= false;
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
									mWebview.setVisibility(View.GONE);
									btnSaveChanges.setEnabled(false);
									final int sdk = android.os.Build.VERSION.SDK_INT;
									if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
										btnSaveChanges.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_logwaypoint_selector));
									} else {
										btnSaveChanges.setBackground(getResources().getDrawable(R.drawable.btn_logwaypoint_selector));
									}
								}
							}, 1000);
						}
					});

					if (response.getId()==SAVESITE_WAY_PATH){
						total_accessPath = mapOfAcesPth.size();
						if (waypointId ==0)
							getCustSites(odr_customerId);
					}

					stopLoading();

					FIELD_UPDATED=0;
					btnUpdateShow=false;
					if(isDialogForUpdate){
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
					}else if(isDialogForUpdate_OnBack){
						isDialogForUpdate_OnBack = false;
						goBack(mActivity.SERVICE_Thread);
					}
				}
			}
		}
		else if(response.getStatus().equals("success")&&
				response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
		{
		}
	}

	public void loadDataOnBack(BaseTabActivity mActivity) {
		mActivity.registerHeader(this);
		BaseTabActivity.setHeaderTitle("", headerText, "");

		btnSaveChanges.setEnabled(false);
		btnSaveChanges.setBackground(getResources().getDrawable(R.drawable.btn_logwaypoint_selector));

		if (headingOn) {
			startOrientationSensor();
		}
		if(maptype.equals("TreeList")){

			Order order = ((HashMap<Long, Order>)DataObject.ordersXmlDataStore).get(Long.valueOf(currentOdrId));
			waypointId = 0;
			accessPointId = 0;
			odr_customerId = order.getCustomerid();

			if(Edition > EDN_FOR_DETAIL_TASK){
				showOrder(true);
				getCustSites(odr_customerId);
				getSiteOrGenType();
			}else if(Edition < EDN_FOR_REGULAR_TASK){
				showOrder(true);
			}
		}

		if(activeOrderObj!=null){
			//total_tree_count_sk.setText(String.valueOf(activeOrderObj.getCustServiceCount()));  // MY Show total tree count //YD 2020 ordertask is not in app anymore
		}else{
			total_tree_count_sk.setText("");
		}
	}

	private void goBack(int threadType) {
		mActivity.popFragments(threadType);
	}

}
