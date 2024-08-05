package com.aceroute.mobile.software.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.audio.OrderDetailAudioListFragment;
import com.aceroute.mobile.software.camera.Gridview_MainActivity;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.OrderPriority;
import com.aceroute.mobile.software.component.reference.OrderStatus;
import com.aceroute.mobile.software.component.reference.OrderTypeList;
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
import com.aceroute.mobile.software.notes.OrderNote;
import com.aceroute.mobile.software.requests.updateOrderRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TimeZone;


@SuppressLint("ResourceAsColor")
public class OrderDetailFragment extends BaseFragment implements DatePickerInterface,
		OnClickListener, RespCBandServST {
	private LinearLayout odrDetail_customer_layout, odrDetail_schedule_layout, odrDetail_schedule_date_time_layout, odrDetail_customer_tree_layout, odrDetail_material_layout, odrDetail_worker_layout;
	private TextView mTxtOrderHeaderDetail, mTxtOrderCustomername, mTxtOrderCustomerAddress, mTxtOrderStatus, mTxtOrderPriority, mTxtOrderInvoiceNumber,mTxtOrderPoNumber, mTxtOrderreminTimes, mTxtOrderTimes, mTxtOrderStartDate, mTxtOrderdeatilsType, mTxtOrderdeatilsSummary,
			mTxtOrderdeatilsAlert, mTxtOrderdeatilTotalTrees, mTxtOrderdeatilTotalMaterials, mTxtOrderdeatilTotalWorkers, mTxtOrderdeatilTotalSnap, mTxtOrderdeatilTotalSay, mTxtOrderdeatilTotalType, mTxtOrderdeatilTotalSign;	
	// dialogue variables
	private RelativeLayout snapIt, sayIt, typeIt, signIt;
	private WebView webviewOrderDetail;
	
	private TextView mTxtVwStartDT, mTxtVwEndDT, mTxtStrtCal, mTxtVwEndCal;
	private Button mBtnTime, mBtnCalender, mBtnDone, mBtnCancel;
	private LinearLayout mStartTimeBg, mEndTimeBg;
//	private Dialog dialog;
	private String mCurrentOrderId, mCurrentOrderName, mWorkersId, orderFromDate, orderToDate;
	
	private int SAVEORDERFIELD_WORKER=1;
	private int SAVE_ORDER_TIME=2;
	private Integer Edition;
	protected String workerLst;
	Order activeOrderObj= null;
	private Date gridStartDate;
	private Date gridEndDate;
	private String finalDateStr;
		private int mheight = 500;
	private MyDialog dialog = null;
	final ArrayList<Long> seletedItems = new ArrayList<Long>();// ListofWorkers
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.order_detail_activity, null);
		TypeFaceFont.overrideFonts(mActivity, v);
		Bundle bundle = this.getArguments();
		mCurrentOrderId = bundle.getString("OrderId");	
		
	//	PreferenceHandler.setTaskTypeOld(mActivity, "1000");

		initiViewReference(v);

		return v;
	}

	private void initiViewReference(View v) {
		webviewOrderDetail = (WebView) v.findViewById(R.id.webviewOrderDetail);
		odrDetail_customer_layout = (LinearLayout) v.findViewById(R.id.order_detail_customer_lyt);
		odrDetail_schedule_layout = (LinearLayout) v.findViewById(R.id.order_detail_schedule_lyt);
		odrDetail_schedule_date_time_layout = (LinearLayout) v.findViewById(R.id.order_detail_schedule_time_lyt);
		odrDetail_customer_tree_layout = (LinearLayout) v.findViewById(R.id.order_detail_tree_lyt);
		odrDetail_material_layout = (LinearLayout) v.findViewById(R.id.order_detail_material_lyt);
		odrDetail_worker_layout = (LinearLayout) v.findViewById(R.id.order_detail_worker_lyt);

		
		mTxtOrderCustomername = (TextView) v.findViewById(R.id.order_detail_customername_txtvw);
		mTxtOrderCustomerAddress = (TextView) v.findViewById(R.id.order_detail_customeraddress_txtvw);
		mTxtOrderStatus = (TextView) v.findViewById(R.id.order_detail_status_txtvw);
		mTxtOrderPriority = (TextView) v.findViewById(R.id.order_detail_priority_txtvw);
		mTxtOrderInvoiceNumber = (TextView) v.findViewById(R.id.order_detail_invoice_number_txtvw);
		mTxtOrderPoNumber = (TextView) v.findViewById(R.id.order_detail_po_number_txtvw);
		mTxtOrderreminTimes = (TextView) v.findViewById(R.id.order_rem_mints_txtvw);
		mTxtOrderTimes = (TextView) v.findViewById(R.id.order_time_txtvw);
		mTxtOrderStartDate = (TextView) v.findViewById(R.id.order_date_txtvw);
		mTxtOrderdeatilTotalTrees = (TextView) v.findViewById(R.id.order_detail_Totaltree_txtvw);
		mTxtOrderdeatilTotalMaterials = (TextView) v.findViewById(R.id.order_detail_Totalmaterial_txtvw);
		mTxtOrderdeatilTotalWorkers = (TextView) v.findViewById(R.id.order_detail_Totalworker_txtvw);
		
		mTxtOrderdeatilsType = (TextView) v.findViewById(R.id.order_details_ordertype_txtvw);
		mTxtOrderdeatilsSummary = (TextView) v.findViewById(R.id.order_details_orderdesc_txtvw);
		mTxtOrderdeatilsAlert = (TextView) v.findViewById(R.id.order_details_alert_txtvw);
		
		mTxtOrderdeatilTotalSnap = (TextView) v.findViewById(R.id.order_detail_Totalsnap_txtvw);
		mTxtOrderdeatilTotalSay = (TextView) v.findViewById(R.id.order_detail_TotalSayit_txtvw);
		mTxtOrderdeatilTotalType = (TextView) v.findViewById(R.id.order_detail_Totaltypeit_txtvw);
		mTxtOrderdeatilTotalSign = (TextView) v.findViewById(R.id.order_detail_Totalsignit_txtvw);
		
		snapIt = (RelativeLayout) v.findViewById(R.id.order_detail_snapit);
		sayIt = (RelativeLayout) v.findViewById(R.id.order_detail_sayit);
		typeIt = (RelativeLayout) v.findViewById(R.id.order_detail_typeit);
		signIt = (RelativeLayout) v.findViewById(R.id.order_detail_signit);
		
		snapIt.setOnClickListener(this);
		sayIt.setOnClickListener(this);
		typeIt.setOnClickListener(this);
		signIt.setOnClickListener(this);
		
		webviewOrderDetail.setBackgroundColor(Color.TRANSPARENT);
		webviewOrderDetail.loadUrl("file:///android_asset/loading.html");
		
		odrDetail_customer_layout.setOnClickListener(this);
		odrDetail_schedule_layout.setOnClickListener(this);
		odrDetail_schedule_date_time_layout.setOnClickListener(this);

		odrDetail_customer_tree_layout.setOnClickListener(this);
		odrDetail_material_layout.setOnClickListener(this);
		odrDetail_worker_layout.setOnClickListener(this);		
	}

	@Override
	public void onResume() {
		super.onResume();
		
		HashMap<Long ,OrderStatus> mapStatus = (HashMap<Long ,OrderStatus>) DataObject.orderStatusTypeXmlDataStore;// have to correct it later  YD
		 HashMap<Long ,OrderPriority> mapPriority = (HashMap<Long ,OrderPriority>) DataObject.orderPriorityTypeXmlDataStore;// have to correct it later YD
		 HashMap<Long, OrderTypeList> mapOrderType = (HashMap<Long, OrderTypeList>) DataObject.orderTypeXmlDataStore;
		 
		HashMap<Long, Order> odrDataMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
		for (Entry<Long, Order> entry : odrDataMap.entrySet()) {
	         Order mOrder = entry.getValue();			
			if(String.valueOf(mOrder.getId()).equals(mCurrentOrderId))
			{							
				try{				
					mCurrentOrderName = mOrder.getNm();
					mActivity.setHeaderTitle(String.valueOf(mOrder.getNm()), "", String.valueOf(mOrder.getId()));
													
					orderFromDate = mOrder.getFromDate();//2015-06-21 4:30 -00:00
					orderToDate = mOrder.getToDate();   //2015-06-21 5:00 -00:00
					
					/*String [] strFromDate = orderFromDate.split(" ");				
					String [] strToDate = orderToDate.split(" ");*/	//YD
					// converts the date to local gmt if the z is included
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
					
					gridStartDate = simpleDateFormat.parse(orderFromDate);  //Sun Jun 21 10:00:00 IST 2015
					gridEndDate = simpleDateFormat.parse(orderToDate);	  //Sun Jun 21 10:30:00 IST 2015
					
					SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MMM dd yyyy");//Jun 21 2015
					String minToShow = String.valueOf((gridEndDate.getTime() - gridStartDate.getTime()) / 60000)+" Mins";
					String stEdToShow = Utilities.convertDateToAmPM(gridStartDate.getHours(),gridStartDate.getMinutes())+"-"+
										Utilities.convertDateToAmPM(gridEndDate.getHours(),gridEndDate.getMinutes());
					String dateToShow =  simpleDateFormat1.format(gridStartDate);
					
/*//					DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
					SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");	
					Date dtSt = simpleDateFormat1.parse(strFromDate[0]+" "+strFromDate[1]+" "+strFromDate[2]);
					Date dtEnd = simpleDateFormat1.parse(strToDate[0]+" "+strToDate[1]+" "+strToDate[2]);
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
				    String date = sdf.format(dtSt);
				    Date myDate = sdf.parse(date);
				    // Append the string "UTC" to the date
				    if(!date.contains("UTC")) {
				        date += " UTC";
				    }			    
					
				   // String dtStartTime = date;
				   // String dtEndTime = myDate.toString();
*/				    
				    /*
					String dtStartTime = getTime(dtSt);
					String dtEndTime = getTime(dtEnd);*/
										
					//Utilities
					/*TimeZone gmtTime = TimeZone.getTimeZone("GMT");				
					simpleDateFormat.setTimeZone(gmtTime);				
					simpleDateFormat.format(stDate);
					simpleDateFormat.format(stDate);
					
					String [] tDate = String.valueOf(stDate).split(" ");*/ //YD 				
					/***********Grid 1 start*****************/
					if(mOrder.getCustypename()!=null)
				        mTxtOrderCustomername.setText(String.valueOf(mOrder.getCustypename()));
					else 
						mTxtOrderCustomername.setText("");
					
					String customerNm  , customerCnm , customerNum;
					if (mOrder.getCustName()== null)
						customerNm = "";
					else 
						customerNm = mOrder.getCustName();
					
					if (mOrder.getCustContactName()== null)
						customerCnm = "";
					else 
						customerCnm = mOrder.getCustContactName();
					
					if (mOrder.getCustContactNumber()== null)
						customerNum = "";
					else 
						customerNum = mOrder.getCustContactNumber();
					
					mTxtOrderCustomerAddress.setText(String.valueOf(customerNm + "\n" + customerCnm + "\n" + customerNum));
					
					/***********Grid 2 start*****************/
					String statusNm = mapStatus.get(mOrder.getStatusId()).getNm();
						mTxtOrderStatus.setText(statusNm);
					String priorityNm = mapPriority.get(mOrder.getPriorityTypeId()).getNm();
						mTxtOrderPriority.setText(priorityNm);
					mTxtOrderInvoiceNumber.setText(String.valueOf(mOrder.getInvoiceNumber()));
					mTxtOrderPoNumber.setText(String.valueOf(mOrder.getPoNumber()));
					/***********Grid 2 end*****************/
					
					int countOrderNoteType = (int) mOrder.getNotCount();
					if(countOrderNoteType>0)
						mTxtOrderdeatilTotalType.setBackgroundResource(R.drawable.ryt_tick);				 
									
					mTxtOrderreminTimes.setText(minToShow);
					mTxtOrderTimes.setText(stEdToShow);
					mTxtOrderStartDate.setText(dateToShow);					
				//	mTxtOrderTimes.setText(String.valueOf(dtStartTime) +"-"+ String.valueOf(dtEndTime));
					
					//mTxtOrderdeatilTotalTrees.setText(String.valueOf(mOrder.getCustServiceCount()));  //YD 2020 ordertask is not in app anymore
					mTxtOrderdeatilTotalMaterials.setText(String.valueOf(mOrder.getCustPartCount()));
					mTxtOrderdeatilTotalWorkers.setText(String.valueOf(mOrder.getPrimaryWorkerId().split("\\|", -1).length));
					if (mOrder.getOrderTypeId()!=0 && mOrder.getOrderTypeId()!=-1)
					mTxtOrderdeatilsType.setText(String.valueOf(mapOrderType.get(mOrder.getOrderTypeId()).getNm()));
					else
						mTxtOrderdeatilsType.setText("");
					
					mTxtOrderdeatilsSummary.setText(String.valueOf(mOrder.getSummary()));
					mTxtOrderdeatilsAlert.setText(String.valueOf(mOrder.getOrderAlert()));
// counter check
					if (mOrder.getImgCount() <= 0) {
						mTxtOrderdeatilTotalSnap.setText("0");
					} else {
						mTxtOrderdeatilTotalSnap.setText(String.valueOf(mOrder.getImgCount()));
					}
					mTxtOrderdeatilTotalSay.setText(String.valueOf(mOrder.getAudioCount()));
					mTxtOrderdeatilTotalSign.setText(String.valueOf(mOrder.getSigCount()));
					
					mWorkersId = String.valueOf(mOrder.getPrimaryWorkerId());
				}catch (Exception e) {
					e.printStackTrace();	
				}
			}
		}		
		
	}
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.order_detail_schedule_lyt:

			ScheduledDetailFragment scheduledDetailFragment = new ScheduledDetailFragment();
			scheduledDetailFragment.setActiveOrderObject(activeOrderObj);
			Bundle bundle=new Bundle();
			bundle.putString("OrderId", String.valueOf(mCurrentOrderId));
			bundle.putString("OrderName", String.valueOf(mCurrentOrderName));
			scheduledDetailFragment.setArguments(bundle);
			mActivity.pushFragments(Utilities.JOBS, scheduledDetailFragment, true, true,BaseTabActivity.UI_Thread);
			break;

		case R.id.order_detail_customer_lyt:

			CustomerDetailFragment detailFragment = new CustomerDetailFragment();
			Bundle mBundle=new Bundle();
			mBundle.putString("OrderId", String.valueOf(mCurrentOrderId));
			mBundle.putString("OrderName", String.valueOf(mCurrentOrderName));
			detailFragment.setArguments(mBundle);
			detailFragment.setActiveOrderObj(activeOrderObj);
			mActivity.pushFragments(Utilities.JOBS, detailFragment, true, true,BaseTabActivity.UI_Thread);
			break;

		case R.id.order_detail_schedule_time_lyt:
						
			String stDate = null,
			stTime = null,
			edDate = null,
			edTime = null;
			try{
				HashMap<Long, Order> odrDataMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
				for (Entry<Long, Order> entry : odrDataMap.entrySet()) {
					Order mOrder = entry.getValue();			
					if(String.valueOf(mOrder.getId()).equals(mCurrentOrderId))
					{							
						/*try{				
							mCurrentOrderName = mOrder.getNm();
							mActivity.setHeaderTitle(String.valueOf(mOrder.getNm()), "", String.valueOf(mOrder.getId()));*/

							orderFromDate = mOrder.getFromDate();//2015-06-21 4:30 -00:00
							orderToDate = mOrder.getToDate();   //2015-06-21 5:00 -00:00
						/*}catch(Exception e){
							e.printStackTrace();
						}*/
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

		case R.id.order_detail_tree_lyt:
			if(PreferenceHandler.getTaskTypeOld(getActivity())!=null ){
				Edition = PreferenceHandler.getPrefEditionForGeo(getActivity());				
				if(Edition>599){
					OrderFormsFragment orderDetailTreeFragment = new OrderFormsFragment();
					Bundle taskBundle=new Bundle();
					taskBundle.putString("OrderId", String.valueOf(mCurrentOrderId));
					taskBundle.putString("OrderName", String.valueOf(mCurrentOrderName));
					orderDetailTreeFragment.setArguments(taskBundle);
					orderDetailTreeFragment.setActiveOrderObj(activeOrderObj);
					mActivity.pushFragments(Utilities.JOBS, orderDetailTreeFragment, true, true,BaseTabActivity.UI_Thread);
				}else{
					OrderTasksOldFragment ordertaskFragment = new OrderTasksOldFragment();
					Bundle taskBundle=new Bundle();
					taskBundle.putString("OrderId", String.valueOf(mCurrentOrderId));
					taskBundle.putString("OrderName", String.valueOf(mCurrentOrderName));
					ordertaskFragment.setArguments(taskBundle);
					mActivity.pushFragments(Utilities.JOBS, ordertaskFragment, true, true,BaseTabActivity.UI_Thread);	
				}
			}
			break;

		case R.id.order_detail_material_lyt:
			OrderPartsFragment orderDetailMaterialFragment = new OrderPartsFragment();
			Bundle partsBundle = new Bundle();
			partsBundle.putString("OrderId", String.valueOf(mCurrentOrderId));
			partsBundle.putString("OrderName", String.valueOf(mCurrentOrderName));
			orderDetailMaterialFragment.setArguments(partsBundle);
			mActivity.pushFragments(Utilities.JOBS,	orderDetailMaterialFragment, true, true,BaseTabActivity.UI_Thread);

			// Intent orderMAterialIntent = new Intent(OrderDetailFragment.this,
			// OrderDetailMaterialActivity.class);
			// startActivity(orderMAterialIntent);
			break;

		case R.id.order_detail_worker_lyt:
				
				openResourseDialog(mWorkersId);

			break;
			
		case R.id.order_detail_snapit:
			
			Gridview_MainActivity gridView_MainFrag = new Gridview_MainActivity();
			gridView_MainFrag.setActiveOrderObject(activeOrderObj);
			
			Bundle b = new Bundle();
			b.putString("OrderId", String.valueOf(mCurrentOrderId));
			b.putString("OrderName", String.valueOf(mCurrentOrderName));
			b.putInt("fmetaType", 1);
			
			gridView_MainFrag.setArguments(b);
			mActivity.pushFragments(Utilities.JOBS, gridView_MainFrag, true, true,BaseTabActivity.UI_Thread);
			

		break;
		case R.id.order_detail_sayit:
			OrderDetailAudioListFragment audioList_MainFrag = new OrderDetailAudioListFragment();
			audioList_MainFrag.setActiveOrderObj(activeOrderObj);
			
			Bundle bundlee = new Bundle();
			bundlee.putInt("fmetaType", 3);
			audioList_MainFrag.setArguments(bundlee);
			
			mActivity.pushFragments(Utilities.JOBS, audioList_MainFrag, true, true,BaseTabActivity.UI_Thread);

		break;
		case R.id.order_detail_typeit:
			OrderNote odrNoteFrag = new OrderNote();
			odrNoteFrag.setActiveOrderObject(activeOrderObj);
			mActivity.pushFragments(Utilities.JOBS, odrNoteFrag, true, true,BaseTabActivity.UI_Thread);

		break;
		case R.id.order_detail_signit:
			Gridview_MainActivity gridView_MainFragSig = new Gridview_MainActivity();
			gridView_MainFragSig.setActiveOrderObject(activeOrderObj);
			
			Bundle bundl = new Bundle();
			bundl.putString("OrderId", String.valueOf(mCurrentOrderId));
			bundl.putString("OrderName", String.valueOf(mCurrentOrderName));
			bundl.putInt("fmetaType", 2);
			
			gridView_MainFragSig.setArguments(bundl);
			mActivity.pushFragments(Utilities.JOBS, gridView_MainFragSig, true, true,BaseTabActivity.UI_Thread);
			

		break;

		default:
			break;
		}

	}

		
	private void openResourseDialog(String workerId){
		try{					
			seletedItems.clear();		
			ArrayList<String> arrList = new ArrayList<String>();
			ArrayList<String> arrworkerId = new ArrayList<String>();
			
			final HashMap<Long, Worker> mapWorker = (HashMap<Long, Worker>) DataObject.resourceXmlDataStore;
			final Long[] keys = mapWorker.keySet().toArray(new Long[mapWorker.size()]);
			// YD add all workers to the arraylist's	
			for(int i=0; i < mapWorker.size(); i++){				
				Worker workerObj = mapWorker.get(keys[i]);				
				arrworkerId.add(String.valueOf(workerObj.getId()));
				arrList.add(String.valueOf(workerObj.getNm()));
			}			
			
			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_multiple_choice, arrList);
			AlertDialog.Builder builder =  new AlertDialog.Builder(mActivity);
			builder.setTitle("Workers");	
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
						 if(!(seletedItems.contains(mapWorker.get(keys[position]).getId())))
							 seletedItems.add(mapWorker.get(keys[position]).getId());														
					 }
					 else{
						 if(seletedItems.contains(mapWorker.get(keys[position]).getId()))
							 seletedItems.remove(mapWorker.get(keys[position]).getId());
						 seletedItems.size();
						// use below if instead of above if
						 /* if (seletedItems.size()>1)
							 seletedItems.remove(mapWorker.get(keys[position]).getId());
						 else{
							// YD atleast one worker is required popup 
						 }*/ 
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
						req.setId(mCurrentOrderId);
						req.setName("routeid");
						req.setValue(workerLst);
						req.setAction(Order.ACTION_SAVE_ORDER_FLD);
						
						Order.saveOrderField(req, mActivity, OrderDetailFragment.this, SAVEORDERFIELD_WORKER);
						webviewOrderDetail.setVisibility(View.VISIBLE);
					}		
					else {
						// popup to show atleast one worker should be there 
					}
				}
		});	
			AlertDialog dialog = builder.create();
			dialog.show();   
			
			Utilities.setDividerTitleColor(dialog, mheight,mActivity);
			Button neutral_button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			Utilities.setDefaultFont_12(neutral_button_negative);
			Button neutral_button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Utilities.setDefaultFont_12(neutral_button_positive);			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void getAndUpdateNumberOfOrderRes(String orderId, String workerIds) {
		HashMap< Long , Order> orderMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
		Order odrObj = orderMap.get(Long.parseLong(orderId));
		odrObj.setPrimaryWorkerId(workerIds);
		mWorkersId = workerIds;
		if(workerIds.contains(String.valueOf(PreferenceHandler.getResId(mActivity)))){}
		else{
			String resp_delete = DBEngine.deleteData(mActivity, String.valueOf(activeOrderObj.getId()),
					Order.TYPE, DBHandler.QUERY_FOR_ORIG);
			((HashMap<Long, Order>)DataObject.ordersXmlDataStore).remove(activeOrderObj.getId());
			mActivity.popFragments(mActivity.SERVICE_Thread);
		}mTxtOrderdeatilTotalWorkers.setText(String.valueOf(seletedItems.size()));
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
				
				Utilities.setDividerTitleColor(dialog, 0,mActivity);
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

				MyDatePickerDialog dialog = new MyDatePickerDialog(mActivity, new mDateSetListener(), mYear, mMonth, mDay, false,OrderDetailFragment.this,sizeDialogStyleID);
				dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", dialog);
				dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", dialog);
				
				if (Build.VERSION.SDK_INT >= 11) {
					dialog.getDatePicker().setCalendarViewShown(false);
				}
				dialog.setTitle("Set Date");
				dialog.show();
								
				Utilities.setDividerTitleColor(dialog, 0,mActivity);
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
					mTxtOrderreminTimes.setText(minToShow);
					mTxtOrderTimes.setText(stEdToShow);
					mTxtOrderStartDate.setText(dateToShow);	
					
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
	
		Utilities.setDividerTitleColor(alertDialog, 0,mActivity);
		
		Button neutral_button_negative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		Utilities.setDefaultFont_12(neutral_button_negative);
		Button neutral_button_positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		Utilities.setDefaultFont_12(neutral_button_positive);
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
		    	
		    	Order.getData(req, mActivity, this, SAVE_ORDER_TIME);
		    	
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }
	    else{
	       //open alert dialog to show that the order time difference between start and end time should be atleast 5 mins 
	       
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

	@Override
	public void onCancelledBtn() {

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

	
	/*private String getDisplayDate(String mDate)
	{		
		String[] ymd = mDate.split("-");
		int year = Integer.parseInt(ymd[0]);
		int month = Integer.parseInt(ymd[1]);
		int day = 0;
		try {
			day = Integer.parseInt(ymd[2]);
		} catch (NumberFormatException e) {
			String[] arrday = ymd[2].split(" ");
			day = Integer.parseInt(arrday[0]);
		}
		return day + "-" + month + "-" + year;
	}
*/
	public String getTimeAmPm(String inputDate){
		String mTime = "";
		try {		
			DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm X");
			DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy KK:mm a");
			String[] strTime = outputFormat.format(inputFormat.parse(inputDate)).split(" ");
			mTime = strTime[1]+" "+strTime[2];			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mTime;
	}
	
	public int minutesDifference(String startDate, String endDate) {
		int mins = 0;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm X");
			Date date1 = simpleDateFormat.parse(startDate);
			Date date2 = simpleDateFormat.parse(endDate);
			
			long diffInSec = (date2.getTime() - date1.getTime()); 		
			long diffSeconds = diffInSec / 1000 % 60;
			long diffMinutes = diffInSec / (60 * 1000) % 60;
			long diffHours = diffInSec / (60 * 60 * 1000) % 24;
			long diffDays = diffInSec / (24 * 60 * 60 * 1000);			
			
			mins = (int) (diffMinutes + diffHours*60 + diffDays*24*60);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return mins;
	}
	
	
	/*@SuppressWarnings("deprecation")
	private String getTime(Date date){
		String start_date = Utilities.convertLocalDateUTCDateStr(date);
		String startDate = start_date;
		int indexOfZero = startDate.indexOf("-00");
		String startDateSub = startDate.substring(0,indexOfZero);
		startDateSub = startDateSub+"GMT+0000";
		
		Date startDateLocal = new Date(startDateSub);
		
		int hours = startDateLocal.getHours();
		boolean am = true;
		if (hours > 12) {
			am = false;
			hours = hours - 12;
		} else if (hours == 12) {
			am = false;
		} else if (hours == 0) {
			hours = 12;
		} 
		
		int minutes = startDateLocal.getMinutes();
		String strMinutes = String.valueOf(minutes);
		if(minutes < 10){			
			strMinutes = "0"+String.valueOf(minutes);
		}	
		
		String hourMinStr = hours+":"+strMinutes+" "+(am ? "am" : "pm");
		
		return hourMinStr;
	}*/

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
					if (response.getId()==SAVEORDERFIELD_WORKER)
					{
						getAndUpdateNumberOfOrderRes(mCurrentOrderId , workerLst);
					}
					
					if (response.getId()==SAVE_ORDER_TIME)
					{
						getAndUpdateOrderDateTime();
					}
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							webviewOrderDetail.setVisibility(View.GONE);
						}
					});
					
				}
				else if(response.getStatus().equals("success")&& 
						response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
				{
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							webviewOrderDetail.setVisibility(View.GONE);
						}
					});
				}
			}
		
	}

	public void setActiveOrderObj(Order activeOrderObj) {
		
		this.activeOrderObj = activeOrderObj;
		/*HashMap<Long, Order> order = (HashMap<Long, Order>)DataObject.ordersXmlDataStore;
		DataObject.ordersXmlDataStore= null;*/
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

	public void loadDataOnBack(BaseTabActivity context) {
		// TODO Auto-generated method stub
		
	}	


}
