
package com.aceroute.mobile.software.fragment;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.SplashII;
import com.aceroute.mobile.software.adaptor.DirectorySwipeAdapter;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.reference.CustomerContact;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.OrderPriority;
import com.aceroute.mobile.software.component.reference.OrderTypeList;
import com.aceroute.mobile.software.component.reference.SegmentModel;
import com.aceroute.mobile.software.component.reference.Site;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.component.reference.Worker;
import com.aceroute.mobile.software.database.DBEngine;
import com.aceroute.mobile.software.database.DBHandler;
import com.aceroute.mobile.software.dialog.CustomTimePickerDialog;
import com.aceroute.mobile.software.dialog.DatePickerInterface;
import com.aceroute.mobile.software.dialog.MyDatePickerDialog;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.CommonSevenReq;
import com.aceroute.mobile.software.requests.EditContactReq;
import com.aceroute.mobile.software.requests.EditSiteReq;
import com.aceroute.mobile.software.requests.GetContacts;
import com.aceroute.mobile.software.requests.GetSiteRequest;
import com.aceroute.mobile.software.requests.GetSiteTypeRequest;
import com.aceroute.mobile.software.requests.SaveSiteRequest;
import com.aceroute.mobile.software.requests.updateOrderRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import java.util.TimeZone;

public class CustomerDetailFragment extends BaseFragment implements RespCBandServST, OnClickListener ,DatePickerInterface , HeaderInterface{

	public static boolean SYNC_CONTACT = false;
	public static boolean SYNC_SITE= false;
	public static String Fix_Contact= "fixed_contact";
	public static String Fix_Location= "fixed_location";
	public static String Fix_Non_Contact= "nonfixed_contact";
	public static String Fix_Non_Location= "nonfixed_location";
	public static String Fix_Segment= "fixed_segment";

	private TextView cust_detail_Name_txtVw, cust_detail_contactName_txtVw, cust_detail_contactNumber_txtVw, cust_detail_phone_type,
	cust_detail_customer_Type_Name, cust_detail_customer_Addr, cust_detail_edit_contact, txt_phoneType, cust_detail_cnt_call;

	private TextView odr_detail_category, odr_detail_category_edit, odr_detail_description, odr_detail_priority, odr_detail_ssd, odr_detail_route,
	odr_detail_alert, odr_detail_alert_edit, odr_detail_date_time, odr_detail_time, odr_detail_time_edit, odr_detail_duration, 
	odr_detail_Worker, odr_detail_worker_edit;

	private TextView mTxtVwStartDT, mTxtVwEndDT, mTxtStrtCal, mTxtVwEndCal, mTxtOrderreminTimes, mTxtOrderTimes, mTxtOrderStartDate;
	private Button mBtnTime, mBtnCalender;
	private LinearLayout mStartTimeBg, mEndTimeBg, odr_deatil_add_worker;
	private Date gridStartDate, gridEndDate;
	private MyDialog dialog = null;

	private String currentOdrId;
	private int SAVEORDERFIELD_STATUS_ALERT =7;
	private int SAVEORDERFIELD_STATUS_CATEGORY=8;
	private int SAVEORDERFIELD_WORKER=6;
	public int GET_ORDER_TYPE_REQ=5;
	public int GET_PRIORITY_REQ=9;
	private int SAVE_ORDER_TIME=11;
	private int GET_SITE_REQ = 1;
	private int GET_GENTYPE_REQ = 54;
	protected int UPDATE_CONTACT=2;
	protected int UPDATE_SITE=3;
	private int SAVESITE = 12;
	private int GET_CONTACT_REQ=13;
	private int mheight = 500;
	private LinearLayout container,containerContact;
	private Long customerId=null;
	private boolean showSite = true;
	Order activeOrderObj=null;


	int typeOfContct =  -1;
	private RelativeLayout siteRelLayout = null;
	private RelativeLayout siteContactLayout = null;
	private String site_name="", site_description="", site_addr="" , site_addr2="";
	private String contactName="", contactPhone="", contactTypeId ,contactTypeNm, conatctEmail;
	private String orderFromDate, orderToDate;
	protected String workerLst ,headerText;
	private String finalDateStr;
	private String previousCatName,workerPrimaryId , previousPriorityId, previousPriorityName;
	private String newCategory,newCategoryId, newPriorityNm,newPriorityId, newDesc, newSSD, newRoute, newAlert;

	String previousTypeID= "";
    String previousPoVal= "";
    String previousInvNm = "";
    String previousDesc= "";
    String previousAlert= "";

	TextView siteNm_main ;
	TextView siteDetails_main;
	TextView siteAddress_main ;
	TextView cust_detail_edit_site_main ;

	private ArrayList<String> mTypeArryList;
	Long[] catkeys;

	private ArrayList<String> mPriorityArryList;
	Long[] Pkeys;
	ArrayList<Items> slist = new ArrayList<>();
	Items fix_location,fix_contact;
	ArrayList<Items> listloc,listcont, listSegment;
	int updatetype=0;

	HashMap< Long, Site> siteListMap;
	HashMap< Long, SiteType> siteTypeListMap;
	HashMap<Long, OrderTypeList> mapOrderType;
	HashMap<Long, OrderPriority> mapOdrPriority;
	HashMap<Long, Worker> mapWorker;

	ArrayList<String> optionLst = new ArrayList<String>();
	final ArrayList<Long> seletedItems = new ArrayList<Long>();// ListofWorkers
	ArrayList<String> arrLstPhoneType = new ArrayList<>();
	public static String[] arrPhoneType = {"Unknown","Mobile", "Landline", "Email",  "Not Reachable", "Do Not Contact"};//MY
	private LinearLayout parentView;
	SwipeListView mLstdList;
	DirectorySwipeAdapter dListAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_customer_detail, null);
		TypeFaceFont.overrideFonts(mActivity, v);

		mLstdList = (SwipeListView) v.findViewById(R.id.dirc_material_lstvw);

		slist=new ArrayList<>();
		listloc=new ArrayList<>();
		listcont=new ArrayList<>();
		listSegment=new ArrayList<>();
		initiViewReference(v);

		dListAdapter = new DirectorySwipeAdapter(mActivity, slist,CustomerDetailFragment.this,activeOrderObj,arrLstPhoneType);
		mLstdList.setAdapter(dListAdapter);


		intiswipesetting();
		return v;
	}

	private void intiswipesetting(){

		mLstdList.setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
			public void onOpened(int position, boolean toRight) {
				mLstdList.closeAnimate(position);
			}

			@Override
			public void onClosed(int position, boolean fromRight) {
				View rowItem = Utilities.getViewOfListByPosition(position,
						mLstdList);


				RelativeLayout backlayout = (RelativeLayout) rowItem
						.findViewById(R.id.back_dirc);
				backlayout.setBackgroundColor(getResources().getColor(
						R.color.color_white));
				TextView chat = (TextView) rowItem
						.findViewById(R.id.back_view_chat_textview_dirc);
				TextView invite = (TextView) rowItem
						.findViewById(R.id.back_view_invite_textview_dirc);
				chat.setVisibility(View.INVISIBLE);
				invite.setVisibility(View.INVISIBLE);
				if (fromRight){
					//showeditshiftHr(slist.get(position),false);
				}else{
					//deleteShift_UnAvil(slist.get(position),false);
				}

				Items itm = slist.get(position);
				if(itm.item!=null){
					if(itm.item instanceof Site){
						Site custSite = (Site) itm.item;
						EditContactFrag contFrag = new EditContactFrag();
						Bundle b = new Bundle();
						b.putLong("CUSTID", custSite.getCid());
						b.putLong("SITE_ID", custSite.getId());
						b.putString("HeaderText", String.valueOf("EDIT LOCATION"));
						b.putString("SITE_NM", custSite.getNm());
						b.putString("SITE_ADR", custSite.getAdr());
						b.putString("SITE_ADR2", custSite.getAdr2());
						b.putString("SITE_DETAIL", custSite.getDetail());
						contFrag.setArguments(b);
						contFrag.setActiveOrderObj(activeOrderObj);
						updatetype=1;
						BaseTabActivity.mBaseTabActivity.pushFragments(Utilities.JOBS, contFrag, false, true, BaseTabActivity.UI_Thread);

					}else if(itm.item instanceof Order){
						EditContactFrag contFrag = new EditContactFrag();
						Bundle b = new Bundle();
						b.putLong("CUSTID", activeOrderObj.getCustomerid());
						b.putLong("CONTACT_ID", activeOrderObj.getContactId());
						b.putString("HeaderText", String.valueOf("EDIT CONTACT"));
						b.putString("CONTACT_NM", activeOrderObj.getCustContactName());
						b.putString("TEL", activeOrderObj.getCustContactNumber());
						b.putLong("CONTACT_TELTYPE", activeOrderObj.getTelTypeId());
						b.putString("CONTACT_EMAIL", activeOrderObj.getEml());
						contFrag.setArguments(b);
						contFrag.setActiveOrderObj(activeOrderObj);
						updatetype=2;
						BaseTabActivity.mBaseTabActivity.pushFragments(Utilities.JOBS, contFrag, false, true, BaseTabActivity.UI_Thread);
					}else if(itm.item instanceof CustomerContact){
						CustomerContact custContact = (CustomerContact)itm.item;
						EditContactFrag contFrag = new EditContactFrag();

						Bundle b = new Bundle();
						b.putLong("CUSTID", custContact.getCustomerid());
						b.putLong("CONTACT_ID", custContact.getId());
						b.putString("HeaderText", String.valueOf("EDIT CONTACT"));
						b.putString("CONTACT_NM", custContact.getContactname());
						b.putString("TEL", custContact.getContacttel());
						b.putLong("CONTACT_TELTYPE", custContact.getContactType());
						b.putString("CONTACT_EMAIL", custContact.getContactEml());
						contFrag.setArguments(b);
						contFrag.setActiveOrderObj(activeOrderObj);
						updatetype=3;
						BaseTabActivity.mBaseTabActivity.pushFragments(Utilities.JOBS, contFrag, false, true, BaseTabActivity.UI_Thread);

					}
				}





			}

			@Override
			public void onListChanged() {

			}

			@Override
			public void onMove(int position, float x) {

			}

			@Override
			public void onStartOpen(int position, int action, boolean right) {

				View rowItem = Utilities.getViewOfListByPosition(
						position, mLstdList);

				RelativeLayout backlayout = (RelativeLayout) rowItem
						.findViewById(R.id.back_dirc);
				TextView chat = (TextView) rowItem
						.findViewById(R.id.back_view_chat_textview_dirc);
				TextView chat1 = (TextView) rowItem
						.findViewById(R.id.back_view_dummy1_dirc);

				TextView invite = (TextView) rowItem
						.findViewById(R.id.back_view_invite_textview_dirc);
				TextView invite1 = (TextView) rowItem
						.findViewById(R.id.back_view_dummy_dirc);

				if (right) {
					backlayout.setBackgroundColor(getResources()
							.getColor(R.color.bdr_green));
					chat.setVisibility(View.GONE);
					chat1.setVisibility(View.GONE);
					invite.setVisibility(View.VISIBLE);
					invite1.setVisibility(View.VISIBLE);

				} else {

					backlayout.setBackgroundColor(getResources()

							.getColor(R.color.bdr_green));
					chat.setVisibility(View.VISIBLE);
					chat1.setVisibility(View.VISIBLE);

					invite.setVisibility(View.GONE);
					invite1.setVisibility(View.GONE);
				}
			}

			@Override
			public void onStartClose(int position, boolean right) {

			}

			@Override
			public void onClickFrontView(int position) {

			}

			@Override
			public void onClickBackView(int position) {

			}

			@Override
			public void onDismiss(int[] reverseSortedPositions) {

			}

		});

		if(SplashII.wrk_tid >=3)
			//Toast.makeText(getContext(),"aaa",Toast.LENGTH_LONG).show();
			mLstdList.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);


		else
			mLstdList.setSwipeMode(SwipeListView.SWIPE_MODE_DEFAULT); // there

		mLstdList.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_NONE); // there
		mLstdList.setSwipeActionRight(SwipeListView.SWIPE_ACTION_NONE);

		//Toast.makeText(getContext(),"aaa",Toast.LENGTH_LONG).show();
		mLstdList.setOffsetLeft(convertDpToPixel()); // left side offset
		mLstdList.setOffsetRight(convertDpToPixel()); // right side
		// offset
		mLstdList.setSwipeCloseAllItemsWhenMoveList(true);
		mLstdList.setAnimationTime(100); // Animation time
		mLstdList.setSwipeOpenOnLongPress(false);
	}
	public int convertDpToPixel() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		/*
		 * float px = dp * (metrics.densityDpi / 160f); return (int) px;
		 */
		return metrics.widthPixels;
	}

	private void initiViewReference(View v) {
		try{		
			Bundle mBundle = this.getArguments();
			currentOdrId = mBundle.getString("OrderId");
			headerText = mBundle.getString("HeaderText");
			mActivity.setHeaderTitle("",headerText.toUpperCase(), "");
			mActivity.registerHeader(this);
			parentView = (LinearLayout) v.findViewById(R.id.parentView_custDtl);

			mPriorityArryList = new ArrayList<String>();

			container = (LinearLayout) v.findViewById(R.id.container_view);
			containerContact = (LinearLayout) v.findViewById(R.id.container_view_contact);

			odr_deatil_add_worker = (LinearLayout) v.findViewById(R.id.odr_deatil_add_worker);

			cust_detail_customer_Type_Name = (TextView) v.findViewById(R.id.cust_detail_customer_Type_Name);
			cust_detail_Name_txtVw = (TextView) v.findViewById(R.id.cust_detail_customer_Name);
			cust_detail_customer_Addr = (TextView) v.findViewById(R.id.cust_detail_customer_Addr);
			cust_detail_edit_contact = (TextView) v.findViewById(R.id.cust_detail_edit_contact);
			cust_detail_cnt_call = (TextView) v.findViewById(R.id.cust_detail_cnt_call);
			cust_detail_contactName_txtVw = (TextView) v.findViewById(R.id.cust_detail_customer_contactName);
			cust_detail_contactNumber_txtVw = (TextView) v.findViewById(R.id.cust_detail_customer_contactNumber);
			cust_detail_phone_type  = (TextView) v.findViewById(R.id.cust_detail_phone_type);
			odr_detail_category = (TextView)v.findViewById(R.id.odr_detail_category);
			odr_detail_category_edit = (TextView)v.findViewById(R.id.odr_detail_category_edit);
			odr_detail_description = (TextView)v.findViewById(R.id.odr_detail_description);
			odr_detail_priority = (TextView) v.findViewById(R.id.odr_detail_priority);
			odr_detail_ssd = (TextView)v.findViewById(R.id.odr_detail_ssd);
			odr_detail_route = (TextView)v.findViewById(R.id.odr_detail_route);
			odr_detail_alert = (TextView)v.findViewById(R.id.odr_detail_alert);
			odr_detail_alert_edit = (TextView)v.findViewById(R.id.odr_detail_alert_edit);
			odr_detail_date_time = (TextView)v.findViewById(R.id.odr_detail_date_time);
			odr_detail_time = (TextView)v.findViewById(R.id.odr_detail_time);
			odr_detail_time_edit = (TextView)v.findViewById(R.id.odr_detail_time_edit);
			odr_detail_duration = (TextView)v.findViewById(R.id.odr_detail_duration);
			odr_detail_Worker = (TextView)v.findViewById(R.id.odr_detail_Worker);
			odr_detail_worker_edit = (TextView)v.findViewById(R.id.odr_detail_worker_edit);

			mTxtOrderreminTimes = (TextView) v.findViewById(R.id.order_rem_mints_txtvw);
			mTxtOrderTimes = (TextView) v.findViewById(R.id.order_time_txtvw);
			mTxtOrderStartDate = (TextView) v.findViewById(R.id.order_date_txtvw);

			/*YD using for main site of order*/
			siteNm_main = (TextView) v.findViewById(R.id.cust_detail_customer_site_main);
			siteDetails_main = (TextView) v.findViewById(R.id.cust_site_details_main);
			siteAddress_main = (TextView) v.findViewById(R.id.cust_detail_customer_SiteSAddress_main);
			cust_detail_edit_site_main = (TextView) v.findViewById(R.id.cust_detail_edit_site_main);

			odr_detail_category_edit.setOnClickListener(this);			
			odr_detail_alert_edit.setOnClickListener(this);
			odr_detail_time_edit.setOnClickListener(this);
			odr_detail_worker_edit.setOnClickListener(this);
			cust_detail_cnt_call.setOnClickListener(this);

			optionLst.add("Add Contact");
			optionLst.add("Add Location");

			arrLstPhoneType = new ArrayList<String>();
			for(int i=0; i<arrPhoneType.length; i++){
				arrLstPhoneType.add(arrPhoneType[i]);
			}

			/*HashMap<Long, Order> odrDataMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;		

			for (Entry<Long, Order> entry : odrDataMap.entrySet()) {
				final Order mCustomerDetail = entry.getValue();		*/	
			if (activeOrderObj!=null){  // MY R later
				if(String.valueOf(activeOrderObj.getId()).equals(currentOdrId)){

					/*	mActivity.setHeaderTitle(String.valueOf(activeOrderObj.getNm()), "", String.valueOf(activeOrderObj.getId()));

					if (activeOrderObj.getCustypename()==null)
						cust_detail_customer_Type_Name.setText("");
					else
						cust_detail_customer_Type_Name.setText(String.valueOf(activeOrderObj.getCustypename()));
					cust_detail_Name_txtVw.setText(String.valueOf(activeOrderObj.getCustName()));						

					cust_detail_contactName_txtVw.setText(String.valueOf(activeOrderObj.getCustContactName()));
					cust_detail_contactNumber_txtVw.setText(String.valueOf(activeOrderObj.getCustContactNumber()));

						odr_detail_description.setText(activeOrderObj.getSummary());
					odr_detail_ssd.setText(activeOrderObj.getPoNumber());
					odr_detail_route.setText(activeOrderObj.getInvoiceNumber());					
					odr_detail_alert.setText(activeOrderObj.getOrderAlert());

					mWorkersId = String.valueOf(activeOrderObj.getPrimaryWorkerId());

					cust_detail_edit_contact.setOnClickListener(new OnClickListener() {						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub						
							showDialog(activeOrderObj.getContactId(), activeOrderObj.getCustContactName(), activeOrderObj.getCustContactNumber());
						}
					}); */

					//	Long cid = mCustomerDetail.getCustomerid();
					customerId = activeOrderObj.getCustomerid();

					getCustSites(customerId);
					Log.d("MAB",String.valueOf(customerId));
					getCustContact(customerId);
					getCustSegment(activeOrderObj.getCustids());
					loadDataNeededForPage();
				}
				parentView.setOnTouchListener(new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						hideSoftKeyboard();
						return false;
					}
				});
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void loadDataNeededForPage() {
		/*OrderStatus odrStatObj = new OrderStatus();*/
		OrderPriority odrPrioObj = new OrderPriority();
		OrderTypeList odrTypeObj = new OrderTypeList();

		CommonSevenReq CommonReqObj = new CommonSevenReq();
		CommonReqObj.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		CommonReqObj.setSource("localonly");

		/*odrStatObj.getObjectDataStore(CommonReqObj, mActivity, this, GET_ORDER_STATUS_REQ);*/
		odrPrioObj.getObjectDataStore(CommonReqObj, mActivity, this, GET_PRIORITY_REQ);
		odrTypeObj.getObjectDataStore(CommonReqObj, mActivity, this, GET_ORDER_TYPE_REQ);		
	}

	private void getCustSites(Long cid) {
		GetSiteRequest req = new GetSiteRequest();
		req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		req.setCid(String.valueOf(cid));
		req.setSource("localonly");
		req.setAction("getsite");

		Site.getData(req, mActivity, this, GET_SITE_REQ);
	}

	private void getCustSegment(String cid) {
		GetSiteTypeRequest req = new GetSiteTypeRequest();
		req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		req.setCid(String.valueOf(cid));
		req.setSource("localonly");
		req.setAction(SiteType.ACTION_SITE_TYPE);

		SiteType.getData(req, mActivity, this, GET_GENTYPE_REQ);
	}

	private void getCustContact(Long cid) {
		GetContacts req = new GetContacts();
		req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		req.setCid(String.valueOf(cid));
		req.setAction(CustomerContact.ACTION_GET_CONTACT);

		CustomerContact.getData(req, mActivity, this, GET_CONTACT_REQ);
	}

	public void updatelist(){
		updatetype=0;
		slist.clear();
		/*if(fix_location!=null) {
			slist.add(fix_location);
		}

		if(fix_contact!=null) {
			slist.add(fix_contact);
		}*/

		ArrayList<Items> actList = new ArrayList<>();
		ArrayList<Items> nonList = new ArrayList<>();
		ArrayList<Items> lastList = new ArrayList<>();

		/*SegmentModel segmentModel = new SegmentModel();
		segmentModel.setSegmentName(activeOrderObj.getCustypename());
		segmentModel.setSegmentId(activeOrderObj.getCustomerid());
		segmentModel.setSegmentNumber(activeOrderObj.getCustContactNumber());

		lastList.add(new Items(segmentModel, Fix_Segment, 3424L));*/

		for(Items items : listloc){
			Site custSite = (Site) items.item;
			if (activeOrderObj.getCustSiteId() != null && custSite.getId() == Long.valueOf(activeOrderObj.getCustSiteId())) {
				actList.add(items);
			}else{
				nonList.add(items);
			}
		}

		for(Items items : listcont){
			CustomerContact custCont = (CustomerContact) items.item;
			if(activeOrderObj.getContactId() == custCont.getId()) {
				actList.add(items);
			}else{
				nonList.add(items);
			}
		}

		slist.addAll(actList);
		slist.addAll(nonList);

		slist.addAll(listSegment);

		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				dListAdapter.notifyDataSetChanged();
			}
		});

	}



	public void SetPage()
	{		
		if(activeOrderObj!=null)
		{
			if(activeOrderObj.getCustContactName()!=null && !activeOrderObj.getCustContactName().trim().equals("")) {
				fix_contact = new Items(activeOrderObj, Fix_Contact, 1l);
			}
			updatelist();
			if (activeOrderObj.getCustypename()==null)
				cust_detail_customer_Type_Name.setText("");
			else
				cust_detail_customer_Type_Name.setText(String.valueOf(activeOrderObj.getCustypename()));
			cust_detail_Name_txtVw.setText(String.valueOf(activeOrderObj.getCustName()));
			cust_detail_customer_Addr.setText(String.valueOf(activeOrderObj.getCustSiteStreeAdd()));
//YD setting up the da
			String typeNm ="";
			if (activeOrderObj.getOrderTypeId()!=0 && activeOrderObj.getOrderTypeId()!=-1)
				typeNm = mapOrderType.get(activeOrderObj.getOrderTypeId()).getNm();
			odr_detail_category.setText(typeNm);
			odr_detail_category.setTag(activeOrderObj.getOrderTypeId());
			previousTypeID = String.valueOf(activeOrderObj.getOrderTypeId());
			previousCatName = String.valueOf(typeNm);

			odr_detail_description.setText(String.valueOf(activeOrderObj.getSummary()));
			previousDesc = String.valueOf(activeOrderObj.getSummary());
			if(previousDesc.equals(""))
				previousDesc =" ";
			
			odr_detail_alert.setText(String.valueOf(activeOrderObj.getOrderAlert()));
			previousAlert = String.valueOf(activeOrderObj.getOrderAlert());

			odr_detail_route.setText(String.valueOf(activeOrderObj.getInvoiceNumber()));
			previousInvNm = String.valueOf(activeOrderObj.getInvoiceNumber());//YD using for route
			if(previousInvNm.equals(""))
				previousInvNm =" ";

			odr_detail_priority.setText(String.valueOf(mapOdrPriority.get(activeOrderObj.getPriorityTypeId()).getNm()));

			previousPriorityId = String.valueOf(activeOrderObj.getPriorityTypeId());
			previousPriorityName = String.valueOf(mapOdrPriority.get(activeOrderObj.getPriorityTypeId()).getNm());
			
			//YD new field is added
			odr_detail_ssd.setText(String.valueOf(activeOrderObj.getPoNumber()));
			previousPoVal = String.valueOf(activeOrderObj.getPoNumber());//YD using for #ssd
			if(previousPoVal.equals(""))
				previousPoVal =" ";
			 

			try{
				orderFromDate = activeOrderObj.getFromDate();//2015-06-21 4:30 -00:00
				orderToDate = activeOrderObj.getToDate();   //2015-06-21 5:00 -00:00
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");

				gridStartDate = simpleDateFormat.parse(orderFromDate);  //Sun Jun 21 10:00:00 IST 2015
				gridEndDate = simpleDateFormat.parse(orderToDate);	  //Sun Jun 21 10:30:00 IST 2015

				SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MMM dd yyyy");//Jun 21 2015
				String minToShow = String.valueOf((gridEndDate.getTime() - gridStartDate.getTime()) / 60000)+" Mins";
				String stEdToShow = Utilities.convertDateToAmPM(gridStartDate.getHours(),gridStartDate.getMinutes())+"-"+
						Utilities.convertDateToAmPM(gridEndDate.getHours(),gridEndDate.getMinutes());
				String dateToShow =  simpleDateFormat1.format(gridStartDate);
				odr_detail_date_time.setText(dateToShow);
				odr_detail_time.setText(stEdToShow);
				odr_detail_duration.setText(minToShow);	

				workerPrimaryId = String.valueOf(activeOrderObj.getPrimaryWorkerId());
				mapWorker = (HashMap<Long, Worker>) DataObject.resourceXmlDataStore;
				///	odr_detail_Worker.setText(String.valueOf(mapWorker.get(Long.parseLong(mWorkersId)).getNm()));

				String[] strWorkers = workerPrimaryId.split("\\|", -1);
				for(int i=0; i<strWorkers.length; i++){
					/*if(i==0){
					odr_detail_Worker.setText(String.valueOf(mapWorker.get(Long.parseLong(strWorkers[i])).getNm()));							
					}else{*/
						LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View ll = inflater.inflate(R.layout.custom_textview, null);
						TextView txtNewWorker = (TextView) ll.findViewById(R.id.odr_detail_new_worker);
						txtNewWorker.setText(String.valueOf(mapWorker.get(Long.parseLong(strWorkers[i])).getNm()));
						
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
						odr_deatil_add_worker.addView(ll, params);						
				//	}
				}
			}catch(Exception e){
				e.printStackTrace();
			}	
		}
	}

	private void showPriorityDialog(final TextView mTextView)
	{
		try{
			ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity) mActivity, android.R.layout.select_dialog_item, mPriorityArryList);
			AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mActivity);
			builder.setTitle("Priority");
			builder.setCancelable(true);
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				@SuppressLint("NewApi")
				public void onClick(DialogInterface dialog, int position) {
					mTextView.setText(mPriorityArryList.get(position));						
					mTextView.setTag(Long.valueOf(mapOdrPriority.get(Pkeys[position]).getId()));
				}
			});
			
			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
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
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

	private void showDialogCategory(final ArrayList<String> mArrayList, final TextView mTextView, final String title){
		try{
			String dialogTitle = title;
			ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity) mActivity, android.R.layout.select_dialog_item, mArrayList);
			AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mActivity, AlertDialog.THEME_HOLO_LIGHT);
			builder.setTitle(dialogTitle);
			builder.setCancelable(true);		
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				@SuppressLint("NewApi")
				public void onClick(DialogInterface dialog, int position) {
					if(title.equals("Category")){
						mTextView.setText(mArrayList.get(position));	
						mTextView.setTag(mapOrderType.get(catkeys[position]).getId());
					}
				}
			});

			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();            	
				}
			});	

			final AlertDialog dialog = builder.create();
			Utilities.setAlertDialogRow(dialog,mActivity);
			dialog.show();		

			Utilities.setDividerTitleColor(dialog, mheight, mActivity);

			Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			Utilities.setDefaultFont_12(button_negative);	
			Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Utilities.setDefaultFont_12(button_positive);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void showHeaderDialog()
	{
		try{
			ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity) mActivity, android.R.layout.select_dialog_item, optionLst);
			AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mActivity, AlertDialog.THEME_HOLO_LIGHT);
			builder.setCancelable(true);
			builder.setTitle("Select Option");
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				@SuppressLint("NewApi")
				public void onClick(DialogInterface dialog, int position) {
					if(position == 0){
						//showDialog(null, activeOrderObj.getCustomerid(), 0L, "", "", "Add Contact");

						EditContactFrag contFrag =  new EditContactFrag();

						Bundle b = new Bundle();
						b.putLong("CUSTID",activeOrderObj.getCustomerid());
						b.putLong("CONTACT_ID", 0l);
						b.putString("HeaderText", String.valueOf("ADD CONTACT"));
						b.putString("CONTACT_NM","");
						b.putString("TEL","");
						b.putLong("CONTACT_TELTYPE", 0l);
						contFrag.setArguments(b);
						contFrag.setActiveOrderObj(activeOrderObj);
						mActivity.pushFragments(Utilities.JOBS,contFrag,false,true,BaseTabActivity.UI_Thread);

						dialog.dismiss();
					}
					else if(position == 1){
						//showDialogForSite(null, 0L, "", "", "", "","Add Location");

						EditContactFrag contFrag =  new EditContactFrag();

						Bundle b = new Bundle();
						b.putLong("CUSTID",activeOrderObj.getCustomerid());
						b.putLong("SITE_ID",0);
						b.putString("HeaderText", String.valueOf("ADD LOCATION"));
						b.putString("SITE_NM","");
						b.putString("SITE_ADR", "");
						b.putString("SITE_ADR2", "");
						b.putString("SITE_DETAIL", "");
						contFrag.setArguments(b);
						contFrag.setActiveOrderObj(activeOrderObj);
						mActivity.pushFragments(Utilities.JOBS, contFrag, false, true, BaseTabActivity.UI_Thread);
					}
				}
			});

			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});

			final AlertDialog dialog = builder.create();
			Utilities.setAlertDialogRow(dialog, mActivity);
			dialog.show();
			//dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, 1170);// YD width , height

			Utilities.setDividerTitleColor(dialog, mheight, mActivity);

			Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			Utilities.setDefaultFont_12(button_negative);
			Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Utilities.setDefaultFont_12(button_positive);
		}catch(Exception e){
			e.printStackTrace();
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

	/**
	 *
	 * @param response
	 */
	@Override
	public void setResponseCBActivity(Response response) {
		if (response!=null)
		{
			if (response.getStatus().equals("success")&& 
					response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED)))
			{

				if (response.getId()==GET_ORDER_TYPE_REQ)
				{
					HashMap<Long, OrderTypeList> unsortedOrderTypeList = (HashMap<Long, OrderTypeList>)response.getResponseMap();
					mapOrderType = sortOdrCatagoryLst(unsortedOrderTypeList);
					Handler handler = new Handler(Looper.getMainLooper());//YD TODO set delay because mapOrderType stays null in setpage()
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							SetPage();
						}
					},200);



				}
				if(response.getId()==GET_PRIORITY_REQ){		
					mapOdrPriority = (HashMap<Long, OrderPriority>) response.getResponseMap();
					Pkeys = mapOdrPriority.keySet().toArray(new Long[mapOdrPriority.size()]);
					for(int i = 0; i <mapOdrPriority.size(); i++) 
					{			
						OrderPriority odrPriorty = mapOdrPriority.get(Pkeys[i]);
						mPriorityArryList.add(String.valueOf(odrPriorty.getNm()));
					}

				}				
				if (response.getId()==GET_SITE_REQ)
				{
					siteListMap = (HashMap)response.getResponseMap();

					if(showSite){
						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								setSiteLayout();
								showSite= false;
							}
						});
					}
				}

				if (response.getId()==GET_GENTYPE_REQ)
				{
					siteTypeListMap = (HashMap)response.getResponseMap();


					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setSiteTypeLayout();
						}
					});

				}

				if(response.getId()==UPDATE_SITE){
				/*	mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {					

							TextView txtName = (TextView) siteRelLayout.getChildAt(0); // Site Name
							if(site_name!=null){
								txtName.setText(site_name.toString());
							}			

							LinearLayout lyt = (LinearLayout) siteRelLayout.getParent();
							TextView txtDetail = (TextView) lyt.getChildAt(1);    // Site Description
							if(site_description!=null){
								txtDetail.setText(site_description.toString());
							}

							TextView txtAddress = (TextView) lyt.getChildAt(2);   // Site Address
							if(site_addr!=null && site_addr2!=null){
								txtAddress.setText(site_addr.toString()+", "+site_addr2.toString());
							}
							else if(site_addr!=null){
								txtAddress.setText(site_addr.toString());
							}
						}
					});*/

					getCustSites(customerId);	
				}

				if(response.getId()== SAVESITE){
					getCustSites(customerId);
				}

				if(response.getId()== GET_CONTACT_REQ) {
					final Response res = response;
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setContactLayout((HashMap<Long, CustomerContact>) res.getResponseMap());
						}
					});
				}
				if(response.getId()==UPDATE_CONTACT)
				{
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {

							if (typeOfContct == 1) {
								if (contactName != null) {
									activeOrderObj.setCustContactName(contactName);
									cust_detail_contactName_txtVw.setText(contactName);  // contact name
								}
								if (contactPhone != null) {
									activeOrderObj.setCustContactNumber(contactPhone);
									cust_detail_contactNumber_txtVw.setText(contactPhone);  // contact phone
								}
								if (contactTypeId != null) {
									activeOrderObj.setTelTypeId(Long.valueOf(contactTypeId));
									cust_detail_phone_type.setText(contactTypeNm);
									cust_detail_phone_type.setTag(contactTypeId);
								}
							}
							if (typeOfContct == 2) {
								TextView txtName = (TextView) siteContactLayout.getChildAt(0); // Site Name
								if (contactName != null) {
									txtName.setText(contactName.toString());
								}

								LinearLayout lyt = (LinearLayout) siteContactLayout.getParent();
								TextView txtDetail = (TextView) lyt.getChildAt(1);    // Site Description
								if (contactTypeId != null) {
									txtDetail.setText(contactTypeNm);
									txtDetail.setTag(contactTypeId);
								}

								TextView txtAddress = (TextView) lyt.getChildAt(2);   // Site Address
								if (contactPhone != null) {
									txtAddress.setText(contactPhone);
								}
							} else {
								getCustContact(customerId);
							}
						}
					});
				}
				if (response.getId()==SAVEORDERFIELD_WORKER)
				{					
					getAndUpdateNumberOfOrderRes(currentOdrId , workerLst);
					activeOrderObj.setPrimaryWorkerId(workerLst);
				}
				if (response.getId()==SAVE_ORDER_TIME)
				{
					getAndUpdateOrderDateTime();
				}
				if(response.getId()==SAVEORDERFIELD_STATUS_CATEGORY){
					
					previousCatName = newCategory ;
					previousTypeID = newCategoryId ;
					previousPriorityName = newPriorityNm;
					previousPriorityId = newPriorityId;
					previousPoVal = newSSD ;
					previousInvNm = newRoute ;
					previousDesc = newDesc;
					
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activeOrderObj.setOrderTypeId(Long.parseLong(newCategoryId));
							odr_detail_category.setText(newCategory);
							odr_detail_category.setTag(newCategoryId);

							activeOrderObj.setPriorityTypeId(Long.parseLong(newPriorityId));
							odr_detail_priority.setText(newPriorityNm);
							odr_detail_priority.setTag(newPriorityId);

							activeOrderObj.setSummary(newDesc);
							odr_detail_description.setText(newDesc);

							activeOrderObj.setPoNumber(newSSD);
							odr_detail_ssd.setText(newSSD);

							activeOrderObj.setInvoiceNumber(newRoute);
							odr_detail_route.setText(newRoute);
						}
					});						
				}if(response.getId()==SAVEORDERFIELD_STATUS_ALERT){
					previousAlert = newAlert;
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {	
							activeOrderObj.setOrderAlert(newAlert);	
							odr_detail_alert.setText(newAlert);
						}
					});
				}			
			}
		}
		else if(response.getStatus().equals("success")&& 
				response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
		{
		}
	}


	private void getAndUpdateOrderDateTime() {		
		String[] value = finalDateStr.split(",");
		activeOrderObj.setStartTime(Long.valueOf(value[0]));
		activeOrderObj.setEndTime(Long.valueOf(value[1]));
		activeOrderObj.setFromDate(value[2].replace("/", "-"));
		activeOrderObj.setToDate(value[3].replace("/", "-"));
		
		Date startdate = getStatDate(value[2].replace("/", "-"));
		if (!Utilities.isTodayDate(mActivity ,startdate)){// YD removing order if the order date is not of current date
			((HashMap<Long, Order>)DataObject.ordersXmlDataStore).remove(activeOrderObj.getId());
			mActivity.popFragments(mActivity.SERVICE_Thread);
		}		
	}

	// YD function to convert String date to the date object 
	public Date getStatDate(String date){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
		Date startdate = null;
		try {
			startdate = simpleDateFormat.parse(date);
		} catch (ParseException e) {e.printStackTrace();}

		return startdate;
	}

	private void setSiteLayout(){
		if(siteListMap!=null){
			listloc.clear();
			//Long[] custSitexmlKeys = siteListMap.keySet().toArray(new Long[siteListMap.size()]);

			for(Long custSitexmlKeys : siteListMap.keySet()) {
			//	container.setVisibility(View.VISIBLE);

				final Site custSite = (Site) siteListMap.get(custSitexmlKeys);
				String siteName = custSite.getNm().toLowerCase();

//				if (activeOrderObj.getCustSiteId() != null && custSite.getId() == Long.valueOf(activeOrderObj.getCustSiteId())) {// checking if the current site is the order main site

				if (activeOrderObj.getCustSiteId() != null ) {// checking if the current site is the order main site
						//fix_location= new Items(custSite,Fix_Location,custSitexmlKeys);
					listloc.add(new Items(custSite,Fix_Non_Location,custSitexmlKeys));
				}
				else {
					if (!siteName.contains("*default") && !siteName.contains("*pole") && !siteName.contains("*access point") && !siteName.contains("*breadcrumbs")) {
						listloc.add(new Items(custSite,Fix_Non_Location,custSitexmlKeys));
					}
				}
			}
		}
	//	if(updatetype!=0){
			updatelist();
	//	}
	}

	private void setSiteTypeLayout(){
		if(siteTypeListMap!=null){
			listSegment.clear();
			//Long[] custSitexmlKeys = siteListMap.keySet().toArray(new Long[siteListMap.size()]);

			for(Long custSitexmlKeys : siteTypeListMap.keySet()) {
				//	container.setVisibility(View.VISIBLE);

				final SiteType custSite = (SiteType) siteTypeListMap.get(custSitexmlKeys);

				listSegment.add(new Items(custSite,Fix_Segment,custSitexmlKeys));
			}
		}

		updatelist();
	}


	private void setContactLayout(final HashMap<Long, CustomerContact> custContMap){
		if(custContMap!=null){
			listcont.clear();
		//	Long[] custContxmlKeys = custContMap.keySet().toArray(new Long[custContMap.size()]);

			for(Long custContxmlKeys : custContMap.keySet()) {
				//containerContact.setVisibility(View.VISIBLE);

				final CustomerContact custCont = (CustomerContact) custContMap.get(custContxmlKeys);

				/*if (activeOrderObj != null && custCont.getId() == Long.valueOf(activeOrderObj.getContactId())) {// checking if the current contact is the order main contact
					//YD no need to display any thing
				}
				else{*/
					listcont.add(new Items(custCont,Fix_Non_Contact,custContxmlKeys));
				//}
			}
			//if(updatetype!=0){
				updatelist();
			//}
		}
	}

	private void getAndUpdateNumberOfOrderRes(String orderId, String workerIds) {
		workerPrimaryId = workerIds; // YD updating local ids for popup
		HashMap< Long , Order> orderMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
		Order odrObj = orderMap.get(Long.parseLong(orderId));
		odrObj.setPrimaryWorkerId(workerIds);
		//	mWorkersId = workerIds;
		if(workerIds.contains(String.valueOf(PreferenceHandler.getResId(mActivity)))){}
		else{
			String resp_delete = DBEngine.deleteData(mActivity, String.valueOf(activeOrderObj.getId()),
					Order.TYPE, DBHandler.QUERY_FOR_ORIG);
			((HashMap<Long, Order>)DataObject.ordersXmlDataStore).remove(activeOrderObj.getId());
			mActivity.popFragments(mActivity.SERVICE_Thread);
		}

		final String[] strWorkers = workerIds.split("\\|", -1);

		mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
				/*for(int j=0; j<odr_deatil_add_worker.getChildCount(); j++){
					if(j!=0){
						odr_deatil_add_worker.removeViewAt(j);
					}
				}				*/

				odr_deatil_add_worker.removeAllViews();

				for(int i=0; i<strWorkers.length; i++){
				/*	if(i==0){
						odr_detail_Worker.setText(String.valueOf(mapWorker.get(Long.parseLong(strWorkers[i])).getNm()));
					}else{*/
						LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View ll = inflater.inflate(R.layout.custom_textview, null);
						TextView txtNewWorker = (TextView) ll.findViewById(R.id.odr_detail_new_worker);
						txtNewWorker.setText(String.valueOf(mapWorker.get(Long.parseLong(strWorkers[i])).getNm()));

						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
						odr_deatil_add_worker.addView(ll, params);
				//	}
				}
		}
		});


	}

	private void showCategoryDialog(String catId, String catNm, String pId, String pName, String desc, String ssd, String route, String alert, String title, final int typeId){
		try{
			LayoutInflater li = LayoutInflater.from(mActivity);
			View promptsView = li.inflate(R.layout.category_details_prompts, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
			alertDialogBuilder.setView(promptsView);

			LinearLayout lyt_order_route = (LinearLayout) promptsView.findViewById(R.id.lyt_order_route);
			LinearLayout lyt_order_priority = (LinearLayout) promptsView.findViewById(R.id.lyt_order_priority);
			LinearLayout lyt_order_ssd = (LinearLayout) promptsView.findViewById(R.id.lyt_order_ssd);
			LinearLayout lyt_order_desc = (LinearLayout) promptsView.findViewById(R.id.lyt_order_desc);
			LinearLayout lyt_order_category = (LinearLayout) promptsView.findViewById(R.id.lyt_order_category);
			LinearLayout lyt_order_alert = (LinearLayout) promptsView.findViewById(R.id.lyt_order_alert);

			final EditText edt_description = (EditText) promptsView.findViewById(R.id.edt_description);
			final EditText edt_ssd = (EditText) promptsView.findViewById(R.id.edt_ssd);
			final EditText edt_route = (EditText) promptsView.findViewById(R.id.edt_route);
			final EditText edt_alert = (EditText) promptsView.findViewById(R.id.edt_order_alert);
			final TextView txt_category = (TextView) promptsView.findViewById(R.id.category_type);
			final TextView txt_Priority = (TextView) promptsView.findViewById(R.id.category_priority);

			if(typeId==1){//YD using this for when showing only alert edittext
				lyt_order_route.setVisibility(View.GONE);
				lyt_order_ssd.setVisibility(View.GONE);
				lyt_order_desc.setVisibility(View.GONE);
				lyt_order_category.setVisibility(View.GONE);
				lyt_order_priority.setVisibility(View.GONE);
				lyt_order_alert.setVisibility(View.VISIBLE);
			}

			txt_Priority.setText(pName);
			txt_Priority.setTag(pId);

			txt_Priority.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showPriorityDialog(txt_Priority);
				}
			});

			txt_category.setText(catNm);
			txt_category.setTag(catId);
			txt_category.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mTypeArryList = new ArrayList<String>();
					catkeys = mapOrderType.keySet().toArray(new Long[mapOrderType.size()]);

					if (mTypeArryList.size() < 1) {
						for (int i = 0; i < mapOrderType.size(); i++) {
							OrderTypeList odrType = mapOrderType.get(catkeys[i]);
							mTypeArryList.add(String.valueOf(odrType.getNm()));
						}
					}
					showDialogCategory(mTypeArryList, txt_category, "Category");
				}

			});

			edt_description.setText(desc);
			edt_ssd.setText(ssd);
			edt_route.setText(route);
			edt_alert.setText(alert);
			// set dialog message
			alertDialogBuilder.setCancelable(false)
			.setTitle(title)
			.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					if(typeId==0){

						String categoryId = String.valueOf(txt_category.getTag());
						String priorityId = String.valueOf(txt_Priority.getTag());
						String ssd = edt_ssd.getText().toString();
						String desc = edt_description.getText().toString();
						String route = edt_route.getText().toString();

						newCategory = txt_category.getText().toString();
						newCategoryId = categoryId;
						newPriorityNm = txt_Priority.getText().toString();
						newPriorityId = priorityId;
						newSSD = ssd;
						newRoute = route;
						newDesc = desc;

						// YD Doing this for checking difference because in previous if it is blank then i keep " " in it.
						if(ssd.equals(""))
							ssd =" ";
						if(desc.equals(""))
							desc =" ";
						if(route.equals(""))
							route =" ";

						String key = "TypeID|PoVal|descript|inv";
						String oldValStr = previousPriorityId+"|"+previousTypeID+"|"+previousPoVal+"|"+previousDesc+"|"+previousInvNm;
						String newValStr = priorityId+"|"+categoryId+"|"+ssd+"|"+desc+"|"+route;

						boolean isDifferent = false;
						String[] oldValStrSplit = oldValStr.split("\\|");
					     String[] newValStrSplit = newValStr.split("\\|");

						for (int i=0;i<newValStrSplit.length;i++)
					     {
					        if(!(oldValStrSplit[i].equals(newValStrSplit[i])))
					        {
					            isDifferent=true;
					            break;
					        }
					   }
						 if (isDifferent){
							 	updateOrderRequest req = new updateOrderRequest();
								req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
								req.setType("post");
								req.setId(String.valueOf(activeOrderObj.getId()));
								req.setName(key);
								req.setValue(newValStr);
								req.setAction(Order.ACTION_SAVE_ORDER_FLD);

								Order.saveOrderField(req, mActivity, CustomerDetailFragment.this, SAVEORDERFIELD_STATUS_CATEGORY);// YD saving to data base
						 }
					}else if(typeId==1){
						String alert = edt_alert.getText().toString();
						newAlert = alert;

						if (!previousAlert.equals(newAlert))
						{
							updateOrderRequest req = new updateOrderRequest();
							req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
							req.setType("post");
							req.setId(String.valueOf(activeOrderObj.getId()));
							req.setName("alt");
							req.setValue(alert);
							req.setAction(Order.ACTION_SAVE_ORDER_FLD);

							Order.saveOrderField(req, mActivity, CustomerDetailFragment.this, SAVEORDERFIELD_STATUS_ALERT);// YD saving to data base
						}
					}
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();

			Utilities.setDividerTitleColor(alertDialog, 0, mActivity);

			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			lp.copyFrom(alertDialog.getWindow().getAttributes());
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.gravity = Gravity.CENTER;
			alertDialog.getWindow().setAttributes(lp);

			Button button_negative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			Utilities.setDefaultFont_12(button_negative);
			Button button_positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Utilities.setDefaultFont_12(button_positive);
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	//Update Contact
	private void showDialog(TextView txtview, final Long cid , final Long ContactID, String name, String phone, final String title){
		try{

			if (txtview!=null)
				siteContactLayout = (RelativeLayout) txtview.getParent();

			// get prompts.xml view
			LayoutInflater li = LayoutInflater.from(mActivity);
			View promptsView = li.inflate(R.layout.customer_details_prompts, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
			// set prompts.xml to alertdialog builder
			alertDialogBuilder.setView(promptsView);

			final EditText edt_name = (EditText) promptsView.findViewById(R.id.edt_contact_name);
			final EditText edt_phone = (EditText) promptsView.findViewById(R.id.edt_contact_phone);
			final LinearLayout address2LL = (LinearLayout) promptsView.findViewById(R.id.cust_detail_adrr2);
			address2LL.setVisibility(View.GONE);

			txt_phoneType = (TextView) promptsView.findViewById(R.id.phone_type);
			txt_phoneType.setText(arrLstPhoneType.get((int) activeOrderObj.getTelTypeId()));
			txt_phoneType.setTag(activeOrderObj.getTelTypeId());
			txt_phoneType.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showCustomDialog();
				}
			});

			edt_name.setText(name);
			edt_phone.setText(phone);
			// set dialog message
			alertDialogBuilder.setCancelable(false)
			.setTitle(title)
			.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// get user input and set it to result
					// edit text

					if (title.equals("Edit Contact"))
						typeOfContct = 1;
					if (title.equals("Edit Contact ")){
						typeOfContct = 2;
					}
					else
						typeOfContct = 0;

					contactName=edt_name.getText().toString();
					if (contactName.equals(""))
						contactName = "Untitled";

					contactPhone=edt_phone.getText().toString();

					contactTypeId = txt_phoneType.getTag().toString();
					contactTypeNm = txt_phoneType.getText().toString();

					EditContactReq req = new EditContactReq();
					req.setAction(CustomerContact.ACTION_CONTACT_EDIT);
					req.setName(contactName);
					req.setTell(String.valueOf(contactPhone));
					req.setId(ContactID);
					req.setCid(cid);
					req.setTid(Integer.valueOf(txt_phoneType.getTag().toString()));

					Site.getData(req, mActivity, CustomerDetailFragment.this, UPDATE_CONTACT);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				}
			});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();

			Utilities.setDividerTitleColor(alertDialog, 0, mActivity);

			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			lp.copyFrom(alertDialog.getWindow().getAttributes());
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.gravity = Gravity.CENTER;
			alertDialog.getWindow().setAttributes(lp);

			Button button_negative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			Utilities.setDefaultFont_12(button_negative);
			Button button_positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Utilities.setDefaultFont_12(button_positive);

		}catch(Exception e){
			e.printStackTrace();
		}
	}


	// Update Site
	private void showDialogForSite(TextView txtViewEdit, final Long siteId, String name, String address,String address2, String description, final String title){
		try{
			if (txtViewEdit!=null)
				siteRelLayout = (RelativeLayout) txtViewEdit.getParent();

			LayoutInflater li = LayoutInflater.from(mActivity);
			View promptsView = li.inflate(R.layout.customer_details_prompts, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
			// set prompts.xml to alertdialog builder
			alertDialogBuilder.setView(promptsView);
			TextView txt_name = (TextView) promptsView.findViewById(R.id.txt_name);
			TextView txt_name_address = (TextView) promptsView.findViewById(R.id.txt_name_address);
			txt_name.setText("NAME");
			txt_name_address.setText("ADDRESS");

			LinearLayout lytPhoneType = (LinearLayout) promptsView.findViewById(R.id.lyt_phoneType);
			lytPhoneType.setVisibility(View.GONE);

			LinearLayout lytSiteDescription = (LinearLayout) promptsView.findViewById(R.id.lyt_site_description);
			lytSiteDescription.setVisibility(View.VISIBLE);

			final EditText edt_name = (EditText) promptsView.findViewById(R.id.edt_contact_name);
			final EditText edt_site_description = (EditText) promptsView.findViewById(R.id.edt_site_description);
			final EditText edt_address = (EditText) promptsView.findViewById(R.id.edt_contact_phone);
			final EditText edt_address2 = (EditText) promptsView.findViewById(R.id.edt_contact_phone2);// YD using for address2 in custlist/site xml
			edt_address.setInputType(InputType.TYPE_CLASS_TEXT);
			edt_address2.setInputType(InputType.TYPE_CLASS_TEXT);

			edt_name.setText(name);
			edt_address.setText(address);
			if (address2!=null)
				edt_address2.setText(address2);// YD using for address2 in custlist/site xml
			else
				edt_address2.setText("");// YD using for address2 in custlist/site xml
			edt_site_description.setText(description);

			// set dialog message
			alertDialogBuilder.setCancelable(false)
			.setTitle(title)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					if (title.equals("Edit Location")) {
						site_name = edt_name.getText().toString();
						if (site_name.equals(""))
							site_name = "Untitled";

						site_description = edt_site_description.getText().toString();
						site_addr = edt_address.getText().toString();
						if (site_addr.equals(""))
							site_addr = "United States";

						site_addr2 = edt_address2.getText().toString();

						EditSiteReq req = new EditSiteReq();
						req.setAction(Site.ACTION_EDIT_SITE);
						req.setName(site_name);
						req.setAdr(site_addr);
						req.setAdr2(site_addr2);
						req.setDesc(edt_site_description.getText().toString());
						req.setId(siteId);
						Site.getData(req, mActivity, CustomerDetailFragment.this, UPDATE_SITE);
					}
					else {
							String site_addr ,site_name;
						String tistamp = String.valueOf(Utilities.getCurrentTime());

						site_name = edt_name.getText().toString();
						if (edt_address.getText()== null || site_name.equals(""))
							site_name = "Untitled";
						else
							site_name = edt_name.getText().toString();

						if (edt_address.getText()== null || edt_address.getText().toString().equals(""))
							site_addr = "United States";
						else
							site_addr = edt_address.getText().toString();

						SaveSiteRequest req = new SaveSiteRequest();
						req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
						req.setAction(Site.ACTION_SAVE_SITE);
						req.setAdr(site_addr);
						req.setNm(site_name);
						req.setCid(String.valueOf(activeOrderObj.getCustomerid()));
						req.setTstamp(tistamp);
						req.setId("0");
						req.setGeo("0,0");
						req.setDtl(edt_site_description.getText().toString());
						req.setTid("");
						req.setLtpnm("");

						Site.saveData(req, mActivity, CustomerDetailFragment.this, SAVESITE);
					}
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();

			Utilities.setDividerTitleColor(alertDialog, 0, mActivity);

			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			lp.copyFrom(alertDialog.getWindow().getAttributes());
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.gravity = Gravity.CENTER;
			alertDialog.getWindow().setAttributes(lp);

			Button button_negative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			Utilities.setDefaultFont_12(button_negative);
			Button button_positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Utilities.setDefaultFont_12(button_positive);
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	// Show Phone Type Dialog
	private void showCustomDialog(){
		try{
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, android.R.layout.select_dialog_item, arrLstPhoneType);
			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
			builder.setTitle("Phone Type");
			builder.setCancelable(true);

			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				@SuppressLint("NewApi")
				public void onClick(DialogInterface dialog, int position) {
					txt_phoneType.setText(arrLstPhoneType.get(position));
					txt_phoneType.setTag(position);
				}
			});

			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
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

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void setActiveOrderObj(Order activeOrderObj) {

		this.activeOrderObj = activeOrderObj;
		/*HashMap<Long, Order> order = (HashMap<Long, Order>)DataObject.ordersXmlDataStore;
		DataObject.ordersXmlDataStore= null;*/
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.odr_detail_category_edit:
			showCategoryDialog(previousTypeID, previousCatName, previousPriorityId, previousPriorityName,previousDesc,previousPoVal,previousInvNm,previousAlert,"Edit Order Information", 0);
			break;
		case R.id.odr_detail_time_edit:
			String stDate = null,
			stTime = null,
			edDate = null,
			edTime = null;
			try{
				HashMap<Long, Order> odrDataMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
				for (Entry<Long, Order> entry : odrDataMap.entrySet()) {
					Order mOrder = entry.getValue();
					if(String.valueOf(mOrder.getId()).equals(currentOdrId))
					{
						orderFromDate = mOrder.getFromDate();//2015-06-21 4:30 -00:00
						orderToDate = mOrder.getToDate();   //2015-06-21 5:00 -00:00
					}
				}

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");

				Date startDate = simpleDateFormat.parse(orderFromDate);  //Sun Jun 21 10:00:00 GMT+05:30 2015
				Date endDate = simpleDateFormat.parse(orderToDate);	  //Sun Jun 21 10:30:00 GMT+05:30 2015

				SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yyyy");//Jun 21 2015
				stDate = simpleDateFormat1.format(startDate);
				edDate = simpleDateFormat1.format(endDate);

				stTime = Utilities.convertDateToAmPM(startDate.getHours(),startDate.getMinutes());
				edTime = Utilities.convertDateToAmPM(endDate.getHours(),endDate.getMinutes());

				openAlertDialog(stDate, stTime, edDate, edTime);
			}catch(Exception e){
				e.printStackTrace();
			}
			break;

		case R.id.odr_detail_alert_edit:
			showCategoryDialog("","","","","","","",previousAlert,"Edit Alert", 1);
			break;
		case R.id.odr_detail_worker_edit:

			openResourseDialog(workerPrimaryId);

			break;
		case R.id.cust_detail_cnt_call:
			break;

		default:
			break;
		}
	}

	// Sorting hashMap
	private static HashMap<Long, Worker> sortByComparator(HashMap<Long, Worker> unsortMap) {

		// Convert Map to List
		List<Entry<Long, Worker>> list =
			new LinkedList<Entry<Long, Worker>>(unsortMap.entrySet());

		// Sort list_cal with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Entry<Long, Worker>>() {
			public int compare(Entry<Long, Worker> o1,
                               Entry<Long, Worker> o2) {
				return (o1.getValue().getNm()).compareTo(o2.getValue().getNm());
			}
		});

		// Convert sorted map back to a Map
		HashMap<Long, Worker> sortedMap = new LinkedHashMap<Long, Worker>();
		for (Iterator<Entry<Long, Worker>> it = list.iterator(); it.hasNext();) {
			Entry<Long, Worker> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}


	private void openResourseDialog(String workerId){
		try{
			seletedItems.clear();
			ArrayList<String> arrList = new ArrayList<String>();
			ArrayList<String> arrworkerId = new ArrayList<String>();

			final HashMap<Long, Worker> mapWorkerLoc = (HashMap<Long, Worker>) DataObject.resourceXmlDataStore;
			final HashMap<Long, Worker> sortedMap = sortByComparator(mapWorkerLoc);
			final Long[] keys = sortedMap.keySet().toArray(new Long[sortedMap.size()]);
			// YD adding all workers to the arraylist's
			for(int i=0; i < sortedMap.size(); i++){
				Worker workerObj = sortedMap.get(keys[i]);
				arrworkerId.add(String.valueOf(workerObj.getId()));
				arrList.add(String.valueOf(workerObj.getNm()));
			}

			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_multiple_choice, arrList){
				@Override
			    public View getView(int position, View convertView, ViewGroup parent) {
					View view = super.getView(position, convertView, parent);

					TextView textView = (TextView)view.findViewById(android.R.id.text1);
					/*LayoutParams params = textView.getLayoutParams();
			        if(params.height > 0)
			        {
			            int height = params.height;
			            params.height = LayoutParams.WRAP_CONTENT;
			            textView.setLayoutParams(params);
			            textView.setMinHeight(height);
			        }*/

			        textView.setTextSize(22);
	                textView.setTextColor(getResources().getColor(R.color.light_gray));

			        return view;
				}
			};

			AlertDialog.Builder builder =  new AlertDialog.Builder(mActivity);
			if(PreferenceHandler.getWorkerHead(mActivity)!=null && !PreferenceHandler.getWorkerHead(mActivity).equals("")){
				builder.setTitle("Select "+PreferenceHandler.getWorkerHead(mActivity));
			}else
				builder.setTitle("Select Workers");

			LayoutInflater inflater = mActivity.getLayoutInflater();

			View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);
			builder.setView(dialogView);

			ListView listView = (ListView) dialogView.findViewById(R.id.list_dialog);

			listView.setAdapter(arrayAdapter);
			String[] strWorkers = workerId.split("\\|", -1);

			for(int i=0; i<strWorkers.length; i++){
				listView.setItemChecked(Integer.valueOf(arrworkerId.indexOf(strWorkers[i])), true);
				seletedItems.add(Long.valueOf(strWorkers[i]));
			}

			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					CheckedTextView item = (CheckedTextView) view;
					if(item.isChecked()) {
						if(!(seletedItems.contains(sortedMap.get(keys[position]).getId())))
							seletedItems.add(sortedMap.get(keys[position]).getId());
					}
					else{
						if(seletedItems.contains(sortedMap.get(keys[position]).getId()))
							seletedItems.remove(sortedMap.get(keys[position]).getId());
				}
				}
			});

			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			}).setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					// TODO Auto-generated method stub
					if(seletedItems.size()>0){
						workerLst ="";
						for(int i=0; i<seletedItems.size(); i++){
							if(i==0)
								workerLst = String.valueOf(seletedItems.get(i));
							else
								workerLst += "|" + String.valueOf(seletedItems.get(i));
						}
						Log.e("Total Workers: ", String.valueOf(seletedItems.size()));

						/*{"url":"'+AceRoute.appUrl+'",'+'"type": "post",'+'"data":{"id": "'+orderId+'",'+
					'"name": "'+fieldToUpdate+'",'+'"value": "'+fieldValue+'",'+'"action": "'+action+'"}}*/
						updateOrderRequest req = new updateOrderRequest();

						req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
						req.setType("post");
						req.setId(currentOdrId);
						req.setName("routeid");
						req.setValue(workerLst);
						req.setAction(Order.ACTION_SAVE_ORDER_FLD);

						Order.saveOrderField(req, mActivity, CustomerDetailFragment.this, SAVEORDERFIELD_WORKER); // MY R later
						//	webviewOrderDetail.setVisibility(View.VISIBLE);
					}
					else {
						// popup to show atleast one worker should be there
					}
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();

			Utilities.setDividerTitleColor(dialog, mheight, mActivity);
			Button neutral_button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			Utilities.setDefaultFont_12(neutral_button_negative);
			Button neutral_button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Utilities.setDefaultFont_12(neutral_button_positive);
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	private void openAlertDialog(final String stDate, String stTime, String edDate, String edTime) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		LayoutInflater inflater = mActivity.getLayoutInflater();
		View dialog = inflater.inflate(R.layout.dialoge_time_calender, null);
		builder.setTitle("Set Date, Time and Duration");
		builder.setView(dialog);

		mTxtVwStartDT = (TextView) dialog
				.findViewById(R.id.dialoge_strt_tmdt_txtvw);
		mTxtVwEndDT = (TextView) dialog
				.findViewById(R.id.dialoge_end_tmdt_txtvw);

		mTxtStrtCal = (TextView) dialog
				.findViewById(R.id.dialoge_strt_cal_txtvw);

		mTxtVwEndCal = (TextView) dialog
				.findViewById(R.id.dialoge_end_cal_txtvw);

		mBtnTime = (Button) dialog.findViewById(R.id.dialog_time_btn);
		mBtnCalender = (Button) dialog.findViewById(R.id.dialog_cal_btn);
		mStartTimeBg = (LinearLayout) dialog.findViewById(R.id.start_time_lnrlyt);
		mEndTimeBg = (LinearLayout) dialog.findViewById(R.id.end_time_lnrlyt);
		mStartTimeBg.setBackgroundColor(getResources().getColor(R.color.color_light_gray));
		mStartTimeBg.setTag(1);

		mTxtVwStartDT.setText(stTime);
		mTxtStrtCal.setText(changeDateFormat(stDate));
		mTxtStrtCal.setTag(stDate);
		mTxtVwEndCal.setText(changeDateFormat(edDate));
		mTxtVwEndCal.setTag(edDate);
		mTxtVwEndDT.setText(edTime);

		mStartTimeBg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mStartTimeBg.setBackgroundColor(getResources().getColor(R.color.color_light_gray));
				mStartTimeBg.setTag(1);
				mEndTimeBg.setBackgroundResource(0);
				mEndTimeBg.setTag(0);
			}
		});
		mEndTimeBg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mEndTimeBg.setBackgroundColor(getResources().getColor(R.color.color_light_gray));
				mEndTimeBg.setTag(1);
				mStartTimeBg.setBackgroundResource(0);
				mStartTimeBg.setTag(0);
			}
		});

		// call for ReminderSelectItemClick Listener
		// reminderSelectItemClick(reminderSelectTime);

		mBtnTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LinearLayout lnr;

				if(mStartTimeBg.getTag().equals(1))
					lnr = (LinearLayout) mStartTimeBg.getChildAt(1);
				else
					lnr = (LinearLayout) mEndTimeBg.getChildAt(1);

				int temphour = 0;

				String strTime = ((TextView) lnr.getChildAt(1)).getText().toString();
				if (strTime.split(":")[1].split(" ")[1].toString().equalsIgnoreCase("am")) {
					temphour = Integer.valueOf(strTime.split(":")[0]);
				} else {
					temphour = Integer.valueOf(strTime.split(":")[0]) + 12;
				}

				int hour = temphour;
				int minute = Integer.valueOf(((TextView) lnr.getChildAt(1)).getText().toString().split(" ")[0].split(":")[1]);

				int sizeDialogStyleID = Utilities.getDialogTextSize(mActivity);
				CustomTimePickerDialog dialog = new CustomTimePickerDialog(mActivity, new CustomTimePickerDialog.OnTimeSetListener(){
					@Override
					public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
						// TODO Auto-generated method stub
						String selMinute = "", selHour = "", am_pm = "";
						Calendar mcurrentTime = Calendar.getInstance();

						if (selectedMinute < 10)
							selMinute = "0" + selectedMinute;
						else
							selMinute = selectedMinute + "";

						if(selectedHour < 10)
							selHour = "0" + selectedHour;
						else
							selHour = String.valueOf(selectedHour);

						mcurrentTime.set(Calendar.HOUR_OF_DAY,
								Integer.valueOf(selHour));
						mcurrentTime.set(Calendar.MINUTE,
								Integer.valueOf(selMinute));
						mcurrentTime.set(Calendar.SECOND, 0);

						if (mcurrentTime.get(Calendar.AM_PM) == Calendar.AM)
							am_pm = "am";
						else if (mcurrentTime.get(Calendar.AM_PM) == Calendar.PM)
							am_pm = "pm";

						String strHrsToShow = (mcurrentTime
								.get(Calendar.HOUR) == 0) ? "12"
										: mcurrentTime.get(Calendar.HOUR) + "";

						if(Integer.valueOf(strHrsToShow) < 10)
							strHrsToShow = "0"+strHrsToShow;

						if (mStartTimeBg.getTag().equals(1)) {
							((TextView) mTxtVwStartDT).setText(strHrsToShow
									+ ":"
									+ selMinute
									+ " " + am_pm);
						} else {
							((TextView) mTxtVwEndDT).setText(strHrsToShow
									+ ":"
									+ selMinute
									+ " " + am_pm);
						}
					}

				}, hour, minute, false,sizeDialogStyleID);

				dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", dialog);
				dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", dialog);

				dialog.setTitle("Set Time");
				dialog.show();

				Utilities.setDividerTitleColor(dialog, 0, mActivity);
				Button button_Negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
				Utilities.setDefaultFont_12(button_Negative);
				Button button_Positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
				Utilities.setDefaultFont_12(button_Positive);
			}
		});

		// call for ReminderTextClick Listener
		// reminderTextClick(mReminderTextView);

		mBtnCalender.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LinearLayout lnr;

				if(mStartTimeBg.getTag().equals(1))
					lnr = (LinearLayout) mStartTimeBg.getChildAt(1);
				else
					lnr = (LinearLayout) mEndTimeBg.getChildAt(1);

				int mYear = Integer.valueOf(((TextView) lnr.getChildAt(0)).getTag().toString().split("-")[2]);
				int mMonth = Integer.valueOf(((TextView) lnr.getChildAt(0)).getTag().toString().split("-")[1])-1;
				int mDay = Integer.valueOf(((TextView) lnr.getChildAt(0)).getTag().toString().split("-")[0]);

				int sizeDialogStyleID = Utilities.getDialogTextSize(mActivity);

				MyDatePickerDialog dialog = new MyDatePickerDialog(mActivity, new mDateSetListener(), mYear, mMonth, mDay, false, CustomerDetailFragment.this,sizeDialogStyleID);
				dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", dialog);
				dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", dialog);

				if (Build.VERSION.SDK_INT >= 11) {
					dialog.getDatePicker().setCalendarViewShown(false);
				}
				dialog.setTitle("Set Date");
				dialog.show();

				Utilities.setDividerTitleColor(dialog, 0, mActivity);
				Button button_Negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
				Utilities.setDefaultFont_12(button_Negative);
				Button button_Positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
				Utilities.setDefaultFont_12(button_Positive);
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				try{
					//	mTxtStrtCal.getText();  // Start Date: 21-06-2015
					String sttime = (String) mTxtVwStartDT.getText();  // Start Time:  10:03 am
					if (sttime.split(" ")[1].equals("pm"))
					{
						int time =0;
						if (Integer.valueOf(sttime.split(":")[0])==12)
							time = Integer.valueOf(sttime.split(":")[0]);
						else
							time = Integer.valueOf(sttime.split(":")[0])+12;
						sttime = String.valueOf(time)+":"+sttime.split(":")[1];
					}

					//		mTxtVwEndCal.getText(); // End Date	: 22-06-2015
					String edtime = (String) mTxtVwEndDT.getText(); //  End Time :10:35 am
					if (edtime.split(" ")[1].equals("pm"))
					{
						int time =0;
						if (Integer.valueOf(edtime.split(":")[0])==12)
							time = Integer.valueOf(edtime.split(":")[0]);
						else
							time = Integer.valueOf(edtime.split(":")[0])+12;   //YD TODO CHECK IF TIME 12:00
						edtime = String.valueOf(time)+":"+edtime.split(":")[1];
					}

					String startDateStr = mTxtStrtCal.getTag()+" "+sttime;//21-06-2015 10:03 am
					String startEndStr = mTxtVwEndCal.getTag()+" "+edtime;//21-06-2015 10:35 am

					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

					Date stDate = simpleDateFormat.parse(startDateStr);//Sun Jun 21 10:05:00 GMT+05:30 2015
					Date endDate = simpleDateFormat.parse(startEndStr);//Mon Jun 22 10:35:00 GMT+05:30 2015

					handleDateRangechange(stDate,endDate);

					Log.e("Start Date :", String.valueOf(stDate));
					Log.e("End Date :", String.valueOf(endDate));

					//mandeep TODO setting up the date in the grid_cal UI
					long finalDiffInMin = (endDate.getTime()- stDate.getTime())/60000;

					if (finalDiffInMin>=5){
						SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MMM dd yyyy");//Jun 21 2015
						String minToShow = String.valueOf((endDate.getTime() - stDate.getTime()) / 60000)+" Mins";
						String stEdToShow = Utilities.convertDateToAmPM(stDate.getHours(),stDate.getMinutes())+"-"+
								Utilities.convertDateToAmPM(endDate.getHours(),endDate.getMinutes());
						String dateToShow =  simpleDateFormat1.format(stDate);

						odr_detail_date_time.setText(dateToShow);
						odr_detail_time.setText(stEdToShow);
						odr_detail_duration.setText(minToShow);

						dialog.dismiss();
					}else{
						showMessageDialog("Start date/time should be less than end date/time");
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();

		Utilities.setDividerTitleColor(alertDialog, 0, mActivity);

		Button neutral_button_negative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		Utilities.setDefaultFont_12(neutral_button_negative);
		Button neutral_button_positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		Utilities.setDefaultFont_12(neutral_button_positive);
	}


	private String changeDateFormat(String date){
		String newDate="";
		SimpleDateFormat input = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat output = new SimpleDateFormat("MMM  dd, yyyy");
		try {
			Date vDate = input.parse(date);                 // parse input
			newDate = output.format(vDate);    // format output
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return newDate;
	}

	@Override
	public void onCancelledBtn() {

	}

	@Override
	public void headerClickListener(String callingId) {
		if (callingId.equals(BaseTabActivity.HeaderPlusPressed)){
			showHeaderDialog();
		}
	}

	class mDateSetListener implements DatePickerDialog.OnDateSetListener {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// getCalender();
			int mYear = year;
			int mMonth = monthOfYear + 1;
			int mDay = dayOfMonth;
			String editMonth;
			String editDate;
			if (mMonth < 10)
				editMonth = "0" + mMonth;
			else
				editMonth = mMonth + "";

			if (mDay < 10)
				editDate = "0" + mDay;
			else
				editDate = mDay + "";

			String mCurrentDate = editDate + "-" + editMonth + "-" + mYear;
			if (mStartTimeBg.getTag().equals(1)) {
				mTxtStrtCal.setText(changeDateFormat(mCurrentDate));
				mTxtStrtCal.setTag(mCurrentDate);
			} else {
				mTxtVwEndCal.setText(changeDateFormat(mCurrentDate));
				mTxtVwEndCal.setTag(mCurrentDate);
			}
		}
	}


	public void handleDateRangechange(Date startDate, Date endDate)  // on startDate: Tue Jun 22 10:05:00 GMT+05:30 2015
	{                                          						 // on endDate  : Wed Jun 23 10:35:00 GMT+05:30 2015
		long finalDiffInMin =    (endDate.getTime()- startDate.getTime())/60000;

		if(startDate.equals(gridStartDate))
		{
			if(endDate.equals(gridEndDate))
				return ;
		}
		if (finalDiffInMin>=5){

			String startDateUtc = convertDateToUtc(startDate.getTime());//2015/06/21 04:35 -00:00  // server Date(coming this way)
			String endDateUtc = convertDateToUtc(endDate.getTime());	//2015/06/21 05:00 -00:00

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm Z");
			Date stUtc;
			try {
				stUtc = simpleDateFormat.parse(startDateUtc);

				Date edUtc = simpleDateFormat.parse(endDateUtc);

				String orderStartTime = String.valueOf(stUtc.getTime());
				String orderEndTime = String.valueOf(edUtc.getTime());

				String action = "saveorderfld";
				String fieldToUpdate = "order_time";
				finalDateStr = orderStartTime+","+orderEndTime+","+startDateUtc+","+endDateUtc;  //dateStr = "1377947100000,1380588000000,2013/08/31 11:05 -00:00,2013/10/01 0:40 -00:00", orderStartTime = 1377947100000, orderEndTime = 1380588000000, startD

				/*{"url":"'+AceRoute.appUrl+'",'+'"type": "post",'+'"data":{"id": "'+orderId+'",'+
		    		'"name": "'+fieldToUpdate+'",'+'"value": "'+fieldValue+'",'+'"action": "'+action+'"}}*/

				updateOrderRequest req = new updateOrderRequest();
				req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
				req.setType("post");
				req.setId(String.valueOf(activeOrderObj.getId()));
				req.setName(fieldToUpdate);
				req.setValue(finalDateStr);
				req.setAction(Order.ACTION_SAVE_ORDER_FLD);

				Order.getData(req, mActivity, CustomerDetailFragment.this, SAVE_ORDER_TIME);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			//open alert dialog to show that the order time difference between start and end time should be atleast 5 mins

		}
	}

	private void showMessageDialog(String strMsg){
		try{
			String D_title = getResources().getString(R.string.msg_slight_problem);
			String D_desc = strMsg;
			dialog = new MyDialog(mActivity, D_title, D_desc,"OK");
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

			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			lp.copyFrom(dialog.getWindow().getAttributes());
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.gravity = Gravity.CENTER;
			dialog.getWindow().setAttributes(lp);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private String convertDateToUtc(long milliseconds) {
		Date date = new Date(milliseconds);

		SimpleDateFormat convStrToDate = new SimpleDateFormat("yyyy/MM/dd HH:mm");//have to send "2015/06/02 11:25 -00:00"
		convStrToDate.setTimeZone(TimeZone.getTimeZone("UTC"));

		String dateToSend = convStrToDate.format(date);
		dateToSend = dateToSend+" -00:00";
		return dateToSend;


	}


	public void ShowMessageDialog(String Strdesc){
		try{
			dialog = new MyDialog(mActivity, getResources().getString(R.string.msg_slight_problem), Strdesc,"OK");
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

			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			lp.copyFrom(dialog.getWindow().getAttributes());
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.gravity = Gravity.CENTER;
			dialog.getWindow().setAttributes(lp);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	// Sorting hashMap
			private static HashMap<Long, OrderTypeList> sortOdrCatagoryLst(HashMap<Long, OrderTypeList> unsortMap) {

				// Convert Map to List
				List<Entry<Long, OrderTypeList>> list =
					new LinkedList<Entry<Long, OrderTypeList>>(unsortMap.entrySet());

				// Sort list_cal with comparator, to compare the Map values
				Collections.sort(list, new Comparator<Entry<Long, OrderTypeList>>() {
					public int compare(Entry<Long, OrderTypeList> o1,
                                       Entry<Long, OrderTypeList> o2) {
						return (o1.getValue().getNm()).compareTo(o2.getValue().getNm());
					}
				});

				// Convert sorted map back to a Map
				HashMap<Long, OrderTypeList> sortedMap = new LinkedHashMap<Long, OrderTypeList>();
				for (Iterator<Entry<Long, OrderTypeList>> it = list.iterator(); it.hasNext();) {
					Entry<Long, OrderTypeList> entry = it.next();
					sortedMap.put(entry.getKey(), entry.getValue());
				}
				return sortedMap;
			}

	public void loadDataOnBack(final BaseTabActivity mActivity){
		super.onResume();
		mActivity.registerHeader(this);
		mActivity.setHeaderTitle("", headerText.toUpperCase(), "");
		if (SYNC_CONTACT==true) {
			SYNC_CONTACT = false;
			updatetype=1;
			getCustContact(activeOrderObj.getCustomerid());
			/*if (activeOrderObj!=null) {
				*//*cust_detail_contactName_txtVw.setText(activeOrderObj.getCustContactName());
				cust_detail_contactNumber_txtVw.setText(activeOrderObj.getCustContactNumber());
				cust_detail_phone_type.setText(arrLstPhoneType.get((int) activeOrderObj.getTelTypeId()));*//*
				fix_contact = new Items(activeOrderObj,Fix_Contact,1l);

			}*/
		}
		else if(SYNC_SITE==true) {
			SYNC_SITE = false;
			updatetype=1;
			getCustSites(activeOrderObj.getCustomerid());
			showSite = true;
		}
	}

	public void hideSoftKeyboard() {
		if (mActivity.getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
		}
	}

	public class Items{
		public  Object item;
		public String type;
		public Long id;
		public boolean isSwipeEnable = true;

		public Items(){

		}

		public Items(Object item, String type, Long id) {
			this.item = item;
			this.type = type;
			this.id = id;
		}
	}
	
}

