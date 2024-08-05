package com.aceroute.mobile.software.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.HeaderList;
import com.aceroute.mobile.software.ListItem;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.StatusList;
import com.aceroute.mobile.software.adaptor.StatusAlertAdapter;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.reference.OrderPriority;
import com.aceroute.mobile.software.component.reference.OrderStatus;
import com.aceroute.mobile.software.component.reference.OrderTypeList;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.CommonSevenReq;
import com.aceroute.mobile.software.requests.updateOrderRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class ScheduledDetailFragment extends BaseFragment implements HeaderInterface, RespCBandServST,OnClickListener {
	private static Context mContext;
	private TextView odr_details_status_txtvw, odr_details_priority_txtvw, odr_details_type_txtvw;
	private EditText odr_details_location_comments_edt, odr_details_alerts_edt, odr_details_invoice_number_edt,odr_details_ponum_edt;
	private String currentOdrId;
	private WebView webviewOrderSchdule;
	private RelativeLayout parentView;
	
	private ArrayList<String> mStatusArryList;
	private ArrayList<String> mPriorityArryList;
	private ArrayList<String> mTypeArryList;

	static HashMap<Long, OrderStatus> mapStatus;
	Long[] statuskeys;
	HashMap<Long, OrderPriority> mapPriority;
	Long[] prioritykeys;
	HashMap<Long, OrderTypeList> mapOrderType;
	Long[] typekeys;
	private Order activeOrderObj;
	public static final int GET_ORDER_TYPE_REQ=1;
	public static final int GET_ORDER_STATUS_REQ=2;
	public static final int GET_PRIORITY_REQ=3;
	private static final int SAVEORDERFIELD_STATUS_PG=4;
	
	public static final int NumOfDataToLoad = 3;
	public static int itemLoaded = 0 ; 
	private static int mheight = 500;
	
	String previousStatusId="";
	String previousPriorityId="";
	String previousTypeID= "";
    String previousPoVal= "";
    String previousAlert= "";
    String previousDesc= "";
    String previousInvNm = "";
	
	public static String tempnewValueStr;
	private static BaseTabActivity mActivityStat;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity.registerHeader(this);
		
		View v = inflater.inflate(R.layout.activity_scheduled_detail, null);
		TypeFaceFont.overrideFonts(mActivity, v);
		mContext = mActivity;
		mActivityStat = mActivity;
		
		initiViewReference(v);
		
		return v;
	}

	private void initiViewReference(View v) {
		try{		
			
			Bundle mBundle = this.getArguments();
			currentOdrId = mBundle.getString("OrderId");
					
			webviewOrderSchdule = (WebView) v.findViewById(R.id.webviewOrderSchdule);
			webviewOrderSchdule.setBackgroundColor(Color.TRANSPARENT);
			webviewOrderSchdule.loadUrl("file:///android_asset/loading.html");
		
			
			parentView = (RelativeLayout) v.findViewById(R.id.parentView);
			odr_details_status_txtvw = (TextView) v.findViewById(R.id.odr_details_status);
			odr_details_priority_txtvw = (TextView) v.findViewById(R.id.odr_details_priority);
			odr_details_type_txtvw = (TextView) v.findViewById(R.id.odr_details_type);
			odr_details_location_comments_edt = (EditText) v.findViewById(R.id.odr_details_location_comments);
			odr_details_alerts_edt = (EditText) v.findViewById(R.id.odr_details_alerts);
			odr_details_ponum_edt = (EditText) v.findViewById(R.id.odr_details_poNum);//YD ssd field
			odr_details_invoice_number_edt = (EditText) v.findViewById(R.id.odr_details_ssd);//YD route field
			
			//YD start
			loadDataNeededForPage();
			//YD end
				
		}catch(Exception e){
			e.printStackTrace();
		}	
		
		odr_details_status_txtvw.setOnClickListener(this);
		odr_details_priority_txtvw.setOnClickListener(this);
		odr_details_type_txtvw.setOnClickListener(this);
		
		parentView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideSoftKeyboard();
				return false;
			}
		});
	}
	
	private void loadDataNeededForPage() {
		OrderStatus odrStatObj = new OrderStatus();
		OrderPriority odrPrioObj = new OrderPriority();
		OrderTypeList odrTypeObj = new OrderTypeList();
		
		CommonSevenReq CommonReqObj = new CommonSevenReq();
		CommonReqObj.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		CommonReqObj.setSource("localonly");
		
		odrStatObj.getObjectDataStore(CommonReqObj, mActivity, this, GET_ORDER_STATUS_REQ);
		odrPrioObj.getObjectDataStore(CommonReqObj, mActivity, this, GET_PRIORITY_REQ);
		odrTypeObj.getObjectDataStore(CommonReqObj, mActivity, this, GET_ORDER_TYPE_REQ);
		
	}
	
	public void SetPage()
	{
		itemLoaded=0;
		
		if(activeOrderObj!=null)
		{				
			mActivity.setHeaderTitle(String.valueOf(activeOrderObj.getNm()), "", String.valueOf(activeOrderObj.getId()));
			
			String statusNm = mapStatus.get(activeOrderObj.getStatusId()).getNm();
			odr_details_status_txtvw.setText(statusNm);
			previousStatusId= String.valueOf(activeOrderObj.getStatusId());
			odr_details_status_txtvw.setTag(activeOrderObj.getStatusId());
			
			String priorityNm = mapPriority.get(activeOrderObj.getPriorityTypeId()).getNm();
			odr_details_priority_txtvw.setText(priorityNm);
			previousPriorityId = String.valueOf(activeOrderObj.getPriorityTypeId());
			odr_details_priority_txtvw.setTag(activeOrderObj.getPriorityTypeId());
			
			String typeNm ="";
			if (activeOrderObj.getOrderTypeId()!=0 )
				typeNm = mapOrderType.get(activeOrderObj.getOrderTypeId()).getNm();
			odr_details_type_txtvw.setText(typeNm);
			previousTypeID = String.valueOf(activeOrderObj.getOrderTypeId());
			odr_details_type_txtvw.setTag(activeOrderObj.getOrderTypeId());
				
			odr_details_location_comments_edt.setText(String.valueOf(activeOrderObj.getSummary()));
			previousDesc = String.valueOf(activeOrderObj.getSummary());
			
			odr_details_alerts_edt.setText(String.valueOf(activeOrderObj.getOrderAlert()));
			previousAlert = activeOrderObj.getOrderAlert();
			
			odr_details_invoice_number_edt.setText(String.valueOf(activeOrderObj.getInvoiceNumber()));
			previousInvNm = String.valueOf(activeOrderObj.getInvoiceNumber());
			
			//YD new field is added
			odr_details_ponum_edt.setText(String.valueOf(activeOrderObj.getPoNumber()));
			previousPoVal = String.valueOf(activeOrderObj.getPoNumber());
		}
	}

	private void showDialog(final ArrayList<String> mArrayList, final TextView mTextView, final String title){
		try{
			String dialogTitle = title;
			ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity) mContext, android.R.layout.select_dialog_item, mArrayList);
			AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mContext);
			builder.setTitle(dialogTitle);
			builder.setCancelable(true);		
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				@SuppressLint("NewApi")
				public void onClick(DialogInterface dialog, int position) {
					if(title.equals("Status")){
						mTextView.setText(mArrayList.get(position));
						mTextView.setTag(mapStatus.get(statuskeys[position]).getId());
					}else if(title.equals("Priority")){
						mTextView.setText(mArrayList.get(position));	
						mTextView.setTag(mapPriority.get(prioritykeys[position]).getId());
					}else if(title.equals("Type")){
						mTextView.setText(mArrayList.get(position));	
						mTextView.setTag(mapOrderType.get(typekeys[position]).getId());
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
			
			Utilities.setDividerTitleColor(dialog, mheight,mActivity);
			
			Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			Utilities.setDefaultFont_12(button_negative);	
			Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Utilities.setDefaultFont_12(button_positive);
		}catch(Exception e){
			e.printStackTrace();
		}
	}




	public static void showStatusDialogAddOdr(Context mContext, HashMap<Long, OrderStatus> mapOdrStatus, TextView mTextView){
		try{
			mActivityStat = (BaseTabActivity)mContext;
			final TextView txtVwStatus = mTextView;
			ListView listView = new ListView(mContext);

			ArrayList<StatusList> mStatusNmArryList = new ArrayList<StatusList>();
			HashMap<Long , ArrayList<OrderStatus> > statusMapp = new HashMap<Long , ArrayList<OrderStatus>>(); //YD hashmap to keep header id as a keey and elements as an arraylist
			ArrayList<OrderStatus> headerArraylist = new ArrayList<OrderStatus>();

			//	pKeys = mapOdrStatus.keySet().toArray(new Long[mapOdrStatus.size()]);
			if(mapOdrStatus!=null) {
				for (Long key : mapOdrStatus.keySet()) {
					//mapTemp.put(String.valueOf(mapOdrStatus.get(key).getId()), mapOdrStatus.get(key).getNm());//YD commning because status is not hardcoded now.

					if (mapOdrStatus.get(key).getIsgroup() == 1) {
						if (statusMapp.get(Long.valueOf(mapOdrStatus.get(key).getId())) != null) {
							headerArraylist.add(mapOdrStatus.get(key));// YD add just to make new header list_cal which will get sort later
						} else {
							statusMapp.put(mapOdrStatus.get(key).getId(), new ArrayList<OrderStatus>());
							headerArraylist.add(mapOdrStatus.get(key));
						}
					} else {
						if (statusMapp.get(Long.valueOf(mapOdrStatus.get(key).getGrpId())) != null) {
							ArrayList<OrderStatus> tempArrList = statusMapp.get(Long.valueOf(mapOdrStatus.get(key).getGrpId()));
							tempArrList.add(mapOdrStatus.get(key));
						} else {
							statusMapp.put(Long.valueOf(mapOdrStatus.get(key).getGrpId()), new ArrayList<OrderStatus>());
							ArrayList<OrderStatus> tempArrList = statusMapp.get(Long.valueOf(mapOdrStatus.get(key).getGrpId()));
							tempArrList.add(mapOdrStatus.get(key));
						}
					}

				}
				ArrayList<OrderStatus> sortedHeaderArrLst = sortArrayList(headerArraylist);// YD sorting headerlist so that it can be sequenced when we display to ui

				for (Long key : statusMapp.keySet()) {//YD sorting the arraylist kept in hashmap
					sortstatusElement(statusMapp.get(key));
				}

				for (OrderStatus temp : sortedHeaderArrLst) {//YD Now making the list_cal to be displayed
					if (temp.getIsVisible() == 0) {//YD check if the current heading need to be displayed or not
						continue;
					} else if (temp.getIsVisible() == 2 || temp.getIsVisible() == 3) {
						mStatusNmArryList.add(new HeaderList(temp.getNm(),mActivityStat));
						ArrayList<OrderStatus> addelements = statusMapp.get(temp.getId());
						for (OrderStatus addelem : addelements) {
							if (addelem.getIsVisible() == 0) {//YD check if the current status need to be displayed or not
								continue;
							} else if (addelem.getIsVisible() == 2 || addelem.getIsVisible() == 3) {
								boolean checked=false;
								if(txtVwStatus.getText().toString().trim().toLowerCase().equals(addelem.getNm().toLowerCase())){
									checked=true;
								}
								mStatusNmArryList.add(new ListItem(String.valueOf(addelem.getId()), addelem.getNm(),mActivityStat,true,checked));
							} else {
								continue;
							}
						}
					} else {
						continue;
					}
				}
			}
			StatusAlertAdapter adapter = new StatusAlertAdapter(mContext, mStatusNmArryList);
			AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mContext, AlertDialog.THEME_HOLO_LIGHT);
			builder.setCancelable(true);
			builder.setTitle("Status");
			listView.setAdapter(adapter);
			builder.setView(listView);

			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});

			final AlertDialog dialog = builder.create();
			dialog.show();

			listView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					if(view.findViewById(R.id.txtTitle) != null){
						TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
						Integer statusId = Integer.parseInt((String) txtTitle.getTag());

						txtVwStatus.setText(String.valueOf(txtTitle.getText()));
						txtVwStatus.setTag(statusId);

						dialog.dismiss();
					}else if(view.findViewById(R.id.separator) != null){
						//	TextView txtHeader = (TextView) view.findViewById(R.id.separator);
					}
				}
			});

			Utilities.setDividerTitleColor(dialog, mheight, mActivityStat);
			Button button_Negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			Utilities.setDefaultFont_12(button_Negative);
			Button button_Positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Utilities.setDefaultFont_12(button_Positive);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

		// Sorting Arraylist for status header
		public static ArrayList<OrderStatus> sortArrayList(List<OrderStatus> unsortList) {

			// Sort list_cal with comparator, to compare the Map values
			Collections.sort(unsortList, new Comparator<OrderStatus>() {
				public int compare(OrderStatus o1,
								   OrderStatus o2) {
					return o1.getGrpSeq() - o2.getGrpSeq();
				}
			});
			return (ArrayList)unsortList;
		}

		//YD sorting arraylist for status header elements
		public static ArrayList<OrderStatus> sortstatusElement(List<OrderStatus> unsortList) {

			// Sort list_cal with comparator, to compare the Map values
			Collections.sort(unsortList, new Comparator<OrderStatus>() {
				public int compare(OrderStatus o1,
								   OrderStatus o2) {
					return o1.getSeq() - o2.getSeq();
				}
			});
			return (ArrayList)unsortList;
		}
	
	public static void showStatusDialog(Context mContext, HashMap<Long, OrderStatus> hasMapStatus, TextView mTextView){
		try{
			final TextView txtVwStatus = mTextView;
			ListView listView = new ListView(mContext);
			
			ArrayList<StatusList> mStatusNmArryList = new ArrayList<StatusList>();
			HashMap<String, String> mapTemp = new HashMap<String, String>();
			for (Long key : hasMapStatus.keySet())
			{	
				mapTemp.put(String.valueOf(hasMapStatus.get(key).getId()), hasMapStatus.get(key).getNm());
			}	

			mStatusNmArryList.add(new HeaderList(mContext.getResources().getString(R.string.title_operational), mContext));
			mStatusNmArryList.add(new ListItem("2", mapTemp.get("2"), mContext,false,false));
			mStatusNmArryList.add(new ListItem("0", mapTemp.get("0"), mContext,false,false));
			mStatusNmArryList.add(new ListItem("3", mapTemp.get("3"), mContext,false,false));
			mStatusNmArryList.add(new ListItem("4", mapTemp.get("4"), mContext,false,false));
			mStatusNmArryList.add(new HeaderList(mContext.getResources().getString(R.string.title_exception), mContext));
			mStatusNmArryList.add(new ListItem("32", mapTemp.get("32"), mContext,false,false));
			mStatusNmArryList.add(new ListItem("33", mapTemp.get("33"), mContext,false,false));
			mStatusNmArryList.add(new ListItem("31", mapTemp.get("31"), mContext,false,false));
			mStatusNmArryList.add(new HeaderList(mContext.getResources().getString(R.string.title_planning), mContext));
			mStatusNmArryList.add(new ListItem("1", mapTemp.get("1"), mContext,false,false));
			mStatusNmArryList.add(new ListItem("7", mapTemp.get("7"), mContext,false,false));
			mStatusNmArryList.add(new ListItem("8", mapTemp.get("8"), mContext,false,false));
			mStatusNmArryList.add(new HeaderList(mContext.getResources().getString(R.string.title_billing), mContext));
			mStatusNmArryList.add(new ListItem("50", mapTemp.get("50"), mContext,false,false));
			mStatusNmArryList.add(new ListItem("52", mapTemp.get("52"), mContext,false,false));
			mStatusNmArryList.add(new ListItem("5", mapTemp.get("5"), mContext,false,false));
			
			StatusAlertAdapter adapter = new StatusAlertAdapter(mContext, mStatusNmArryList);
			AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mContext);
			builder.setCancelable(true);
			builder.setTitle("Status");		
			listView.setAdapter(adapter);		
			builder.setView(listView);
			
			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	            	dialog.dismiss(); 
	            }
			});
			
			final AlertDialog dialog = builder.create();
			dialog.show();
			
			listView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub											
					if(view.findViewById(R.id.txtTitle) != null){
						TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
						Integer statusId = Integer.parseInt((String) txtTitle.getTag());
						
						txtVwStatus.setText(String.valueOf(txtTitle.getText()));
						txtVwStatus.setTag(statusId);						
						
						dialog.dismiss();	
					}else if(view.findViewById(R.id.separator) != null){
						//	TextView txtHeader = (TextView) view.findViewById(R.id.separator);
					}		
				}			 
			});

			Utilities.setDividerTitleColor(dialog, mheight, mActivityStat);
			Button button_Negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			Utilities.setDefaultFont_12(button_Negative);
			Button button_Positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Utilities.setDefaultFont_12(button_Positive);		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.odr_details_status:	
			/*	mStatusArryList = new ArrayList<String>();
				statuskeys = mapStatus.keySet().toArray(new Long[mapStatus.size()]);
				
				if (mStatusArryList.size()<1){
					for(int i = 0; i<mapStatus.size(); i++) 
					{			
						OrderStatus odrStatus = mapStatus.get(statuskeys[i]);											
						mStatusArryList.add(String.valueOf(odrStatus.getNm()));			
					}	
				}
				showDialog(mStatusArryList, odr_details_status_txtvw, "Status");*/			
				
				hideSoftKeyboard();
				
				showStatusDialog(mContext, mapStatus, odr_details_status_txtvw);
				
				break;
			case R.id.odr_details_priority:
				hideSoftKeyboard();
				mPriorityArryList = new ArrayList<String>();
				prioritykeys = mapPriority.keySet().toArray(new Long[mapPriority.size()]);
				
				if (mPriorityArryList.size()<1){
					for(int i = 0; i<mapPriority.size(); i++) 
					{			
						OrderPriority odrPriorty = mapPriority.get(prioritykeys[i]);											
						mPriorityArryList.add(String.valueOf(odrPriorty.getNm()));
					}
				}
				showDialog(mPriorityArryList, odr_details_priority_txtvw, "Priority");
				
				break;
			case R.id.odr_details_type:
				hideSoftKeyboard();
				mTypeArryList = new ArrayList<String>();
				typekeys = mapOrderType.keySet().toArray(new Long[mapOrderType.size()]);
				
				if (mTypeArryList.size()<1){
					for(int i = 0; i<mapOrderType.size(); i++) 
					{			
						OrderTypeList odrType = mapOrderType.get(typekeys[i]);											
						mTypeArryList.add(String.valueOf(odrType.getNm()));
					}
				}
				showDialog(mTypeArryList, odr_details_type_txtvw, "Type");
				
				break;			
			default:				
				break;
		}
	}
	
private String[] getArrListFrmMap(Object list,String type ) {
		
		String[] StrList;
		int i = 0;
		
		if (type.equals("orderType"))
		{
			HashMap< Long, OrderTypeList> odrTypeList = (HashMap< Long, OrderTypeList>)list;
			StrList = new String[odrTypeList.size()];
			
			for (Long key : odrTypeList.keySet()) {
				   Log.i("AceRouteN","key: " + key + " value: " + odrTypeList.get(key));
				   
				   StrList[i] = odrTypeList.get(key).getNm();
				   i++;
				}
			return StrList;
		}
		else if (type.equals("orderStatus"))
		{
			HashMap< Long, OrderStatus> odrStatusList = (HashMap< Long, OrderStatus>)list;
			StrList = new String[odrStatusList.size()];
			
			for (Long key : odrStatusList.keySet()) {
				   Log.i("AceRouteN","key: " + key + " value: " + odrStatusList.get(key));
				   
				   StrList[i] = odrStatusList.get(key).getNm();
				   i++;
				}
			return StrList;
		}
		else if (type.equals("orderPriority"))
		{
			HashMap< Long, OrderPriority> odrpriorityList = (HashMap< Long, OrderPriority>)list;
			StrList = new String[odrpriorityList.size()];
			
			for (Long key : odrpriorityList.keySet()) {
				   Log.i("AceRouteN","key: " + key + " value: " + odrpriorityList.get(key));
				   
				   StrList[i] = odrpriorityList.get(key).getNm();
				   i++;
				}
			return StrList;
		}
		return null;
	}

	@Override
	public void headerClickListener(String callingId) {
		if(callingId.equals("1")){			

			odr_details_status_txtvw.getTag();
			odr_details_priority_txtvw.getTag();
			odr_details_type_txtvw.getTag();
			
			odr_details_location_comments_edt.getText();
			odr_details_alerts_edt.getText();					
			odr_details_invoice_number_edt.getText();
			
			String currentStatus = String.valueOf(odr_details_status_txtvw.getTag());
			String currentPriority = String.valueOf(odr_details_priority_txtvw.getTag());
			String currentOdrType = String.valueOf(odr_details_type_txtvw.getTag());
			String currentPoVal = odr_details_ponum_edt.getText().toString();
			String currentAlert = odr_details_alerts_edt.getText().toString();
			String currentDesc =  odr_details_location_comments_edt.getText().toString();
			String currentInv =  odr_details_invoice_number_edt.getText().toString();
			
		     
		  /*---------Triming All The Values(current & previous)---------------*/   
			if (previousStatusId!=null && !previousStatusId.equals(""))
				previousStatusId =previousStatusId.trim();
		    currentStatus =currentStatus.trim();
		    
		    if (previousPriorityId!=null && !previousPriorityId.equals(""))
		    	previousPriorityId =previousPriorityId.trim();
		    currentPriority =currentPriority.trim();
		     
		    if (previousTypeID!=null && !previousTypeID.equals(""))
		        previousTypeID =previousTypeID.trim();
		    currentOdrType =currentOdrType.trim();
		    
		    if (previousPoVal!=null && !previousPoVal.equals(""))     
		      previousPoVal =previousPoVal.trim();
		    currentPoVal =currentPoVal.trim();
		   
		    
		    if (previousAlert!=null && !previousAlert.equals(""))     
		    	previousAlert =previousAlert.trim();
		    else 
		    	previousAlert = " ";
		    currentAlert =currentAlert.trim();
		    
		    if (previousDesc!=null && !previousDesc.equals(""))
		      previousDesc =previousDesc.trim();
		    currentDesc =currentDesc.trim();
		         
		    String currentDesctemp = currentDesc.replace("/\n/g","\\\\n"); // check this boss YD
		    if (previousInvNm!=null && !previousInvNm.equals(""))
		      previousInvNm = previousInvNm.trim();
		    currentInv  = currentInv.trim(); 
		  /*------------Triming End---------------*/  
		     boolean isDifferent = false;
		     boolean isDifferent1 = false;
		      
		     String oldValStr = previousStatusId+"|"+previousPriorityId+"|"+previousTypeID+"|"+previousPoVal+"|"+previousDesc+"|"+previousInvNm+"|"+previousAlert;
		     String newValStr = currentStatus+"|"+currentPriority+"|"+currentOdrType+"|"+currentPoVal+"|"+currentDesctemp+"|"+currentInv+"|"+currentAlert;
		     
		     String[] oldValStrSplit = oldValStr.split("\\|");
		     String[] newValStrSplit = newValStr.split("\\|");
		     
		     /*if (oldValStr.substring(oldValStr.length()-1).equals("|")){
		    	 oldValStrSplit[oldValStrSplit.length] = "";
		     }
		     if (newValStr.substring(oldValStr.length()-1).equals("|")){
		    	 newValStrSplit[newValStrSplit.length] = "";
		     }*/
		     
		     for (int i=0;i<oldValStrSplit.length;i++)
		     {
		        if(!(oldValStrSplit[i].equals(newValStrSplit[i])))
		        {
		            isDifferent=true;
		            break;
		        }
		        
		   }
		     
		   String key = "statusId|proirityId|TypeID|PoVal|descript|inv|alt";
		    if(isDifferent){
		    	
		    	try {
		    		long durationstr = getOrderDuration(activeOrderObj.getFromDate(), activeOrderObj.getToDate());
					addDateChangeandsend("2",key,newValStr,currentStatus,durationstr,false);
				} 
		    	catch (ParseException e) {e.printStackTrace();}
		    	
		    	// YD later TODO to refersh order list_cal according to Status changed
		    	/*if((currentStatus=="8") || (previousStatusId == "8") ||
		 		       (currentStatus=="4") || (previousStatusId == "4") ||
		 		       (currentStatus=="5") || (previousStatusId == "5"))
		 		       		refreshOrdersList = true;
		 		       else		
		 		 			refreshOrdersListfromCache = true;*/
		    	
		     }
		    else 
		    {
		    	goBack(mActivity.UI_Thread);
		    }
			webviewOrderSchdule.setVisibility(View.VISIBLE);
		}
		InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(odr_details_location_comments_edt.getWindowToken(), 0);
	}
	
	public long getOrderDuration(String startDate, String endDate){// YD 2015-06-15 4:00 -00:00
		 
		  String newStDate = startDate.substring(0,16);// YD 2015-06-15 4:00
		  String newEdDate = endDate.substring(0,16);  // YD 2015-06-15 4:30
		  
		try {
			SimpleDateFormat convStrToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date sd = (Date) convStrToDate.parseObject(newStDate);
			Date ed = (Date) convStrToDate.parseObject(newEdDate);
			
			//setting calendar for Starttime and get time in milliseconds
			Calendar cal = Calendar.getInstance();
			cal.setTime(sd);
			long orderStartTime = cal.getTimeInMillis();
			
			// setting calendar for endtime and get time in milliseconds
			cal.setTime(ed);
			long orderEndTime = cal.getTimeInMillis();
			
			long diffSecs = (orderEndTime - orderStartTime)/1000;
			
			return diffSecs/60;
		} 
		catch (ParseException e) {e.printStackTrace();}
		return 0;
	}
	
	String new_status_end_date;
	String new_status_start_date;
	
	public String addDateChangeandsend(String howtosend,String key, String newValStr,String currentStatusrecv,
			long durationstr,boolean isstautsstart) throws ParseException
	{
		String newkey;
		//String  tempnewValueStr;
		 if(currentStatusrecv.equals("8"))
	       {
	    
			 	long  duration = 0;
	       		if(durationstr != 0)
	       		    duration = durationstr*60*1000;
	       		Log.i("duration", String.valueOf(duration));
	       		
	       		 // For start date and time
	       		String orderStartDatetemp = "2003-01-14 14:00 -00:00";
	       		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");	// returns before 12pm : Sat Jun 20 09:30:00 IST 2015 and after 12pm it returns: Sat Jun 20 23:30:00 IST 2015
	       		Date orderStartDate = simpleDateFormat.parse(orderStartDatetemp);
	       		long orderStarttime =   orderStartDate.getTime();
	       		 
				// For End date and time
	       		Date orderEndDateTemp = new Date(orderStarttime+duration);
	       		String orderEndDate = simpleDateFormat.format(orderEndDateTemp);
	       		Log.i("orderEndDate", String.valueOf(orderEndDate));
	       		long orderEndtime =  orderEndDateTemp.getTime();
	       		Log.i("orderEndtime", String.valueOf(orderEndtime));
	       		 
	       		new_status_end_date = convertDateToUtc(orderEndtime);
	       		new_status_start_date = "2003-01-14 14:00 -00:00";
	       		 
	       		if(howtosend=="2")
	       			newkey = key+"|start_date|end_date|orderStartTime|orderEndTime";
	       		else
	       			newkey = key;	
	       		tempnewValueStr = newValStr+"|"+new_status_start_date+"|"+new_status_end_date+"|"+orderStarttime+"|"+orderEndtime;
	       }
	       else
	       {
	           newkey = key;
	           tempnewValueStr = newValStr;
	           
	       }
	       if(howtosend.equals("1"))
	       {
	    	   return  newkey+"##"+tempnewValueStr;
	       }
	       else
	       {
	        	/*{"url":"'+AceRoute.appUrl+'",'+'"type": "post",'+'"data":{"id": "'+orderId+'",'+
    			'"name": "'+fieldToUpdate+'",'+'"value": "'+fieldValue+'",'+'"action": "'+saveorderfld+'"}}*/		    	
		    	
	        	updateOrderRequest req = new updateOrderRequest();
				req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
				req.setType("post");
				req.setId(String.valueOf(activeOrderObj.getId()));
				req.setName(newkey);
				req.setValue(tempnewValueStr);
				req.setAction(Order.ACTION_SAVE_ORDER_FLD);
				
				Order.saveOrderField(req, mActivity, this, SAVEORDERFIELD_STATUS_PG);// YD saving to data base
				return "";
	       }
	}
	
	// called on success of saveOrderStatus when howtosend variable above ==1
	public void  onOrderSUpdateover(String data)// YD later TODO
	{
				
		
		/*jsinterface.log("after onOrderSUpdateover, "+data);
		var isOperationSuccessful = parseOperationResponse(data);
						showMessageAndUpdateOrder(isOperationSuccessful,sessionStorage.order_id,key,newValStr);
		if (pagetogoto == "refreshorderonCurrentpage"){
		   fetchOrdersFromServer();	
		   return;
		}
		else if(pagetogoto !=""){
			gotoPage(pagetogoto);
		}
		hideAjaxLoader();*/
	
	}
	
	// currently NOt converting to utc as per req
	private String convertDateToUtc(long milliseconds) {
		Date date = new Date(milliseconds);
		
		SimpleDateFormat convStrToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");//have to send "2015/06/02 11:25 -00:00"
		convStrToDate.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		String dateToSend = convStrToDate.format(date);
		dateToSend = dateToSend+" -00:00";
		return dateToSend;
		
		
	}
	
	private void goBack(int threadType) {
		mActivity.popFragments(threadType);
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				webviewOrderSchdule.destroy();
			}
		});
	}
	
	public void setActiveOrderObject(Order activeOrderObj) {
		
		this.activeOrderObj = activeOrderObj;
		/*HashMap<Long, Order> order = (HashMap<Long, Order>)DataObject.ordersXmlDataStore;
		DataObject.ordersXmlDataStore= null;*/
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
		if (response!=null)
		{
			if (response.getStatus().equals("success")&& 
					response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED)))
			{
				if (response.getId()==GET_ORDER_STATUS_REQ)
				{
					mapStatus = (HashMap<Long, OrderStatus>)response.getResponseMap();
					itemLoaded++;
					if (NumOfDataToLoad==itemLoaded){SetPage();}
				}
				else if (response.getId()==GET_PRIORITY_REQ)
				{
					mapPriority = (HashMap<Long, OrderPriority>)response.getResponseMap();
					itemLoaded++;
					if (NumOfDataToLoad==itemLoaded){SetPage();}
				}
				else if (response.getId()==GET_ORDER_TYPE_REQ)
				{
					mapOrderType = (HashMap<Long, OrderTypeList>)response.getResponseMap();
					itemLoaded++;
					if (NumOfDataToLoad==itemLoaded){SetPage();}
				}
				else if (response.getId()==SAVEORDERFIELD_STATUS_PG)
				{
					 //String tempnewValueStr = currentStatus+"|"+currentPriority+"|"+currentOdrType+"|"+currentPoVal+"|"+currentDesctemp+"|"+currentInv;
					String[] valToSave = tempnewValueStr.split("\\|");
					activeOrderObj.setStatusId(Long.valueOf(valToSave[0]));
					activeOrderObj.setPriorityTypeId(Long.valueOf(valToSave[1]));
					
					if (valToSave[2] != null && !valToSave[2].equals("0")){
						activeOrderObj.setOrderTypeId(Long.valueOf(valToSave[2]));
					}
					else 
						activeOrderObj.setOrderTypeId(0);
					
					if (valToSave[3]!=null)
						activeOrderObj.setPoNumber(valToSave[3]);
					else 
						activeOrderObj.setPoNumber("");
					
					if (valToSave[4]!=null)
						activeOrderObj.setSummary(valToSave[4]);
					else 
						activeOrderObj.setSummary("");
					
					if (valToSave[5]!=null)
						activeOrderObj.setInvoiceNumber(valToSave[5]);
					else
						activeOrderObj.setInvoiceNumber("");
					
					if (valToSave[6]!=null)
						activeOrderObj.setOrderAlert(valToSave[6]);
					else
						activeOrderObj.setOrderAlert("");
					
					if (valToSave[0].equals("8"))
					{
						activeOrderObj.setFromDate(valToSave[7]);
						activeOrderObj.setToDate(valToSave[8]);
						// YD TODO also check for start and end time if necessary to save
					}
					
					goBack(mActivity.SERVICE_Thread);
				}
			}
			else if(response.getStatus().equals("success")&& 
					response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
			{
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						webviewOrderSchdule.destroy();
					}
				});
			}
		}	
		
	}	
	public void hideSoftKeyboard() {
		if(mActivity.getCurrentFocus()!=null){
		InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
		}
	}

	public void loadDataOnBack(BaseTabActivity context) {
		// TODO Auto-generated method stub
		
	}
}

