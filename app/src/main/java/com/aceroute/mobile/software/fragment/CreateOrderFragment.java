package com.aceroute.mobile.software.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.OrderPriority;
import com.aceroute.mobile.software.component.reference.OrderStatus;
import com.aceroute.mobile.software.component.reference.OrderTypeList;
import com.aceroute.mobile.software.component.reference.Worker;
import com.aceroute.mobile.software.customersite.CustomerListActivity;
import com.aceroute.mobile.software.customersite.Sitelist;
import com.aceroute.mobile.software.dialog.CustomTimePickerDialog;
import com.aceroute.mobile.software.dialog.DatePickerInterface;
import com.aceroute.mobile.software.dialog.MyDatePickerDialog;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.MySearchDialog;
import com.aceroute.mobile.software.dialog.MySearchDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.SaveNewOrder;
import com.aceroute.mobile.software.utilities.ArrayAdapterwithRadiobutton;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.aceroute.mobile.software.validation.ValidationEngine;

import org.json.JSONException;

import java.lang.reflect.Field;
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
import java.util.TimeZone;


public class CreateOrderFragment extends BaseFragment implements View.OnClickListener , HeaderInterface , RespCBandServST ,DatePickerInterface {

	ValidationEngine validation;
	String sitetypeNam , custTypeNm ,siteid ,siteNm ,custId ,custNm;
	ArrayList<String> minArray;

	static int itemPosClicked=-1;
	static int TIME_PICKER_INTERVAL=5;

	TextView odr_nm_txt, odr_loc_comment_txt, odr_type_txt, odr_custnm_txt, odr_location_txt,
				odr_schedule_date_txt ,odr_start_time_txt, odr_duration_txt, odr_alert_txt,
				 odrnew_status_txt, odrnew_priority_txt, odrnew_worker_txt;

	TextView odr_type_edt, odr_schedule_date_edt, odr_start_time_edt, odr_duration_edt,odrnew_status_edt,
				odrnew_priority_edt, odrnew_invoice_txt, odrnew_po_txt, odrnew_worker_edt;

	EditText odr_nm_edt, odr_loc_comment_edt, odr_custnm_edt ,odr_location_edt ,odrnew_alert_edt,
	odrnew_ssd_edt, odrnew_route_edt;

	HashMap< Long, OrderTypeList> odrTypeList;
	HashMap< Long, OrderStatus> odrStatusList;
	HashMap< Long, OrderPriority> odrPriorityListTemp;
	HashMap<Long, Worker> odrWorkerList;

	Map<Long, OrderPriority> odrPriorityList = Collections.synchronizedMap(new LinkedHashMap<Long, OrderPriority>());

	Long keys[];
	private int SAVENEWORDER=1;
	MyDialog dialog = null;
	HashMap<Long, Order> odrDataMap;
	private int mHeight = 500;
	private static String orderStatSaved = "";
	private static String orderStartDateSaved = "";
	private int getSiteFlag = 0;

	long elemtSelectedInSearchDlog =-1;
	String[] minsArr  =  new String[60];

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View v = inflater.inflate(R.layout.fragment_job, null);
		TypeFaceFont.overrideFonts(mActivity, v);
		mActivity.registerHeader(this);
		String mheader="ADD ORDER";
		if(PreferenceHandler.getOrderHead(getActivity())!=null && !PreferenceHandler.getOrderHead(getActivity()).trim().equals("")){
			mheader= "ADD "+PreferenceHandler.getOrderHead(getActivity()).toUpperCase();
		}
		mActivity.setHeaderTitle("",mheader , "");

		validation = ValidationEngine.getInstance(mActivity);
		validation.initValidation("CreateOrderFragment");

		initiViewReference(v);

		return v;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

		super.onStart();
		if (PreferenceHandler.getCurtSteCustdatForcustList(mActivity)==1)
    	{
			if (getSiteFlag ==1 ){
				getSiteFlag=0;
				PreferenceHandler.setCurtSteCustdat(mActivity,0);
			}
    		PreferenceHandler.setCurtSteCustdatForcustList(mActivity,0);
    		String data=PreferenceHandler.getCustSiteData(mActivity);

    		String[] resultArr = data.split("##");
    		 siteid = resultArr[0];
    	     siteNm = resultArr[1];
    	     sitetypeNam = resultArr[2];
    	     custId = resultArr[3];
    	     custNm = resultArr[4];
    	     custTypeNm = resultArr[5];

    	    odr_custnm_edt.setTag(custId);
    		odr_custnm_edt.setText(custNm);
    		odr_location_edt.setTag(siteid);
    		odr_location_edt.setText(siteNm);

    	}
	}

	private void initiViewReference(View v) {

		odrTypeList = (HashMap< Long, OrderTypeList>)DataObject.orderTypeXmlDataStore;
		odrStatusList = (HashMap< Long, OrderStatus>) DataObject.orderStatusTypeXmlDataStore;
		odrPriorityListTemp = (HashMap< Long, OrderPriority>)DataObject.orderPriorityTypeXmlDataStore;
		odrWorkerList = (HashMap<Long, Worker>) DataObject.resourceXmlDataStore;

		sortPriorty(odrPriorityListTemp);
		setEditText(v);
		setTextViews(v);
		setDyanmicFont();

		for(int i=0; i<=59; i++){
			minsArr[i]= String.valueOf(i);
		}

		v.findViewById(R.id.parentView).setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideSoftKeyboard();
				return false;
			}
		});
	}

	private void sortPriorty(HashMap<Long, OrderPriority> odrPriorityListTemp) {
		long[] x = {0,1,4,5,6};

		for (int i=0; i<x.length;i++){
			odrPriorityList.put(x[i], odrPriorityListTemp.get(x[i]));
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.odr_custnm_edt:
			hideSoftKeyboard();

			CustomerListActivity customerFragment = new CustomerListActivity();
			mActivity.pushFragments(Utilities.JOBS, customerFragment, true, true,BaseTabActivity.UI_Thread); //use this code YD
			break;

		case R.id.odr_location_edt:
			if (custId!= null && !custId.equals("")) {
				getSiteFlag = 1;
				Sitelist siteFragment = new Sitelist();
				Bundle extra = new Bundle();
				extra.putString("CID", custId);
				extra.putString("CNM", custNm);
				extra.putString("CTYPENM", custTypeNm);
				extra.putInt("flag", 0);

				siteFragment.setArguments(extra);
				mActivity.pushFragments(Utilities.JOBS, siteFragment, true, true, BaseTabActivity.UI_Thread); //use this code YD
			}
			break;

		case R.id.odr_type_edt:
			hideSoftKeyboard();

			/*keys = odrTypeList.keySet().toArray(new Long[odrTypeList.size()]);
			String[] odrTypeArr =  getArrListFrmMap(odrTypeList,"orderType");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, odrTypeArr);
			showDialog(adapter,"odrTypLst", "Category");*/

			odrTypeList = sortHashmap(odrTypeList);
			ArrayList<Long> idsLst = new ArrayList<Long>();
			ArrayList<String> nmsLst = new ArrayList<String>();

			for (Long keys : odrTypeList.keySet()){
				OrderTypeList odrTypeObj = odrTypeList.get(keys);
				idsLst.add(odrTypeObj.getId());
				nmsLst.add(odrTypeObj.getNm());
			}
			showCategoryDialog(idsLst,nmsLst); // sendoing id 0 to set first element

			break;

		case R.id.odr_schedule_date_edt:
			hideSoftKeyboard();
			getCalender();
			break;

		case R.id.odr_start_time_edt:
			hideSoftKeyboard();
			getTime(((TextView) v).getText().toString());
			break;

		case R.id.odr_duration_edt:
			hideSoftKeyboard();
			minArray = getDurationvaluesData();
			ArrayAdapter<String> odrTimeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, minArray);
			//showDialog(odrTimeAdapter,"odrTimeLst", "Duration");//YD old popup of x mins string

			showDialogForDur(v.getTag());
			break;

		case R.id.odrnew_status_edt:
			hideSoftKeyboard();
			/*keys = odrStatusList.keySet().toArray(new Long[odrStatusList.size()]);
			String[] odrStatusArr =  getArrListFrmMap(odrStatusList, "orderStatus");
			ArrayAdapter<String> odrStatAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, odrStatusArr);
			showDialog(odrStatAdapter,"odrStatLst", "Status");*/

			ScheduledDetailFragment.showStatusDialogAddOdr(mActivity, odrStatusList, odrnew_status_edt);

			break;

		case R.id.odrnew_priority_edt:
			hideSoftKeyboard();
			keys = odrPriorityList.keySet().toArray(new Long[odrPriorityList.size()]);
			String[] odrPrioArr =  getArrListFrmMap(odrPriorityList, "orderPriority");

			ArrayAdapter<String> odrPrioAdapter = new ArrayAdapterwithRadiobutton(getActivity(), android.R.layout.select_dialog_item, odrPrioArr,odrnew_priority_edt.getText().toString());

			showDialog(odrPrioAdapter,"odrPrioLst", "Priority");

			break;

		case R.id.odrnew_worker_edt:
			hideSoftKeyboard();
			odrWorkerList = sortHashmapRes(odrWorkerList);
			keys = odrWorkerList.keySet().toArray(new Long[odrWorkerList.size()]);
			String[] odrWorkerArr = getArrListFrmMap(odrWorkerList, "orderWorker");
			ArrayAdapter<String> odrWorkerAdapter = new ArrayAdapterwithRadiobutton(getActivity(), android.R.layout.select_dialog_item, odrWorkerArr,odrnew_worker_edt.getText().toString());
			showDialog(odrWorkerAdapter,"odrWrkrLst", "Assigned Worker");
			if(PreferenceHandler.getWorkerHead(mActivity)!=null && !PreferenceHandler.getWorkerHead(mActivity).equals("")){
				showDialog(odrWorkerAdapter,"odrWrkrLst", "Assigned "+PreferenceHandler.getWorkerHead(mActivity));
			}else
				showDialog(odrWorkerAdapter,"odrWrkrLst", "Assigned Worker");
			break;

		default:
			break;
		}

	}

	private static LinkedHashMap<Long, Worker> sortHashmapRes(HashMap<Long, Worker> unsortMap) {

		LinkedHashMap<Long, Worker> sortedMap = new LinkedHashMap<Long, Worker>();

		if (unsortMap!=null){
			// Convert Map to OrderTypeList
			List<Map.Entry<Long, Worker>> list =
					new LinkedList<Map.Entry<Long, Worker>>(unsortMap.entrySet());

			// Sort list_cal with comparator, to compare the Map values
			Collections.sort(list, new Comparator<Map.Entry<Long, Worker>>() {
				public int compare(Map.Entry<Long, Worker> o1,
								   Map.Entry<Long, Worker> o2) {
					return o1.getValue().getNm().compareTo(o2.getValue().getNm());// sorting ascendingly
				}
			});

			// Convert sorted map back to a Map
			for (Iterator<Map.Entry<Long, Worker>> it = list.iterator(); it.hasNext();) {
				Map.Entry<Long, Worker> entry = it.next();
				sortedMap.put(entry.getKey(), entry.getValue());
			}
		}
		return sortedMap;
	}

	private static HashMap<Long, OrderTypeList> sortHashmap(HashMap<Long, OrderTypeList> unsortMap) {

		LinkedHashMap<Long, OrderTypeList> sortedMap = new LinkedHashMap<Long, OrderTypeList>();

		if (unsortMap!=null){
			// Convert Map to OrderTypeList
			List<Map.Entry<Long, OrderTypeList>> list =
					new LinkedList<Map.Entry<Long, OrderTypeList>>(unsortMap.entrySet());

			// Sort list_cal with comparator, to compare the Map values
			Collections.sort(list, new Comparator<Map.Entry<Long, OrderTypeList>>() {
				public int compare(Map.Entry<Long, OrderTypeList> o1,
								   Map.Entry<Long, OrderTypeList> o2) {
					return o1.getValue().getNm().compareTo(o2.getValue().getNm());// sorting ascendingly
				}
			});

			// Convert sorted map back to a Map
			for (Iterator<Map.Entry<Long, OrderTypeList>> it = list.iterator(); it.hasNext();) {
				Map.Entry<Long, OrderTypeList> entry = it.next();
				sortedMap.put(entry.getKey(), entry.getValue());
			}
		}
		return sortedMap;
	}

	MySearchDialog searchDialg;
	private void showCategoryDialog(ArrayList<Long> idsLst, ArrayList<String> nmsLst)
	{
		searchDialg = new MySearchDialog(mActivity, "Category", "", idsLst,nmsLst, elemtSelectedInSearchDlog);

		searchDialg.setkeyListender(new MySearchDiologInterface() {
			@Override
			public void onButtonClick() {
				super.onButtonClick();
				searchDialg.cancel();
			}

			@Override
			public void onItemSelected(Long idSelected, String nameSelected) {
				super.onItemSelected(idSelected, nameSelected);

				odr_type_edt.setText(nameSelected);
				odr_type_edt.setTag(idSelected);
				searchDialg.cancel();
				elemtSelectedInSearchDlog = idSelected;

			}
		});
		searchDialg.setCanceledOnTouchOutside(true);
		searchDialg.onCreate1(null);
		searchDialg.show();

		int mheight=500;
		Utilities.setDividerTitleColor(searchDialg, mheight, mActivity);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(searchDialg.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.gravity = Gravity.CENTER;
		searchDialg.getWindow().setAttributes(lp);

	}

	private void showDialogForDur(Object tag) {
		int currentSetTime = Integer.valueOf((String)tag);
		int arrayOfTme[] = getHrsMins(currentSetTime);


		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("Set Duration");

		LayoutInflater inflater = mActivity.getLayoutInflater();

		View dialogView = inflater.inflate(R.layout.number_picker, null);
		builder.setView(dialogView);
		final NumberPicker hrPck = (NumberPicker) dialogView.findViewById(R.id.hourPicker);
		hrPck.setMaxValue(99);
		hrPck.setMinValue(0);
		hrPck.setValue(arrayOfTme[0]);
		TypeFaceFont.overrideFonts(getActivity(),hrPck);


		final NumberPicker minPck = (NumberPicker) dialogView.findViewById(R.id.minPicker);
		minPck.setMaxValue(60);// YD value should be number of elements in an array for ex in string array below
		minPck.setMinValue(1);
		//minPck.setValue((arrayOfTme[1]/5)+1);//YD it automatically set the value from an array which is being displayed
		minPck.setValue(arrayOfTme[1]+1);
		minPck.setDisplayedValues(minsArr);
		TypeFaceFont.overrideFonts(getActivity(),minPck);

		Class<?> numberPickerClass = null;
		try {
			numberPickerClass = Class.forName("android.widget.NumberPicker");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Field selectionDivider = null;
		try {
			selectionDivider = numberPickerClass.getDeclaredField("mSelectionDivider");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		try {
			selectionDivider.setAccessible(true);
			selectionDivider.set(hrPck, getActivity().getResources().getDrawable(
					R.drawable.picker_view_holo));
			selectionDivider.set(minPck, getActivity().getResources().getDrawable(
					R.drawable.picker_view_holo));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Resources.NotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
        /*minPck.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                picker.setValue((newVal < oldVal) ? oldVal - 5 : oldVal + 5);
            }

        });*/

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		})
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {

						int hrsSelected = hrPck.getValue();
						int minsSelected = Integer.valueOf(minsArr[minPck.getValue()-1]); //YD -1 becauseminPck.getValue() is returing value 1 greater than the value kept in an array

               /* long hrsInMilli = Long.valueOf(hrsSelected) * 60 * 60 * 1000;
                long minsInMilli = Long.valueOf(minsSelected) * 60 * 1000;
                long totalDurMillis = hrsInMilli + minsInMilli;*/
						if (hrsSelected <1 && minsSelected<5) // YD if the time is less than 5 min select atleast 5 mins automatically
							minsSelected = 5;
						odr_duration_edt.setText((hrsSelected*60)+minsSelected+" minutes");
						odr_duration_edt.setTag(String.valueOf((hrsSelected*60)+minsSelected));

					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();

		Utilities.setDividerTitleColor(dialog, 500, mActivity);
		Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		Utilities.setDefaultFont_12(button_negative);
		Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		Utilities.setDefaultFont_12(button_positive);
	}

	private int[] getHrsMins(int currentSetTime) {
		int currentsetHrsMins[] = new int[2];
		if (currentSetTime>=60){
			currentsetHrsMins[0] = currentSetTime/60;
			currentsetHrsMins[1] = currentSetTime%60;
		}
		else{
			currentsetHrsMins[0] = 0;
			currentsetHrsMins[1] = currentSetTime;
		}
		return currentsetHrsMins;
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
			Map<Long, OrderPriority> odrpriorityList = (Map<Long, OrderPriority>)list;
			StrList = new String[odrpriorityList.size()];

			for (Long key : odrpriorityList.keySet()) {
				   Log.i("AceRouteN","key: " + key + " value: " + odrpriorityList.get(key));

				   StrList[i] = odrpriorityList.get(key).getNm();
				   i++;
				}
			return StrList;
		}
		else if(type.equals("orderWorker")){
			HashMap< Long, Worker> odrworkerList = (HashMap< Long, Worker>)list;
			StrList = new String[odrworkerList.size()];

			for (Long key : odrworkerList.keySet()) {
			   Log.i("AceRouteN","key: " + key + " value: " + odrworkerList.get(key));

			   StrList[i] = odrworkerList.get(key).getNm();
			   i++;
			}
			return StrList;
		}

		return null;
	}

	private void showDialog(ArrayAdapter< String> adapter , final String type, String title) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle(title);
		builder.setCancelable(true);
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@SuppressLint("NewApi")
			public void onClick(DialogInterface dialog, int position) {
				itemPosClicked = position;
				setValueInView(type);

				return;

				/*if (mScheduleArryList.get(position).equalsIgnoreCase("Scheduled")) {
					tv.setBackground(getActivity().getResources().getDrawable(
							R.drawable.rounded_blue_fill_bg));
				} else if (mScheduleArryList.get(position).equalsIgnoreCase(
						"unscheduled")) {
					tv.setBackground(getActivity().getResources().getDrawable(
							R.drawable.rounded_green_fill_bg));
				} else {
					tv.setBackground(getActivity()_red_fill_bg));
				}
				notifyDataSetChanged();*/

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


		Utilities.setDividerTitleColor(dialog, mHeight, mActivity);

		Button button_Negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		Utilities.setDefaultFont_12(button_Negative);
		Button button_Positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		Utilities.setDefaultFont_12(button_Positive);
	}

	// set view when some item get clicked in AlertDialog
	protected void setValueInView(String type) {

		if (type.equals("odrTypLst")){
			odr_type_edt.setText(odrTypeList.get(keys[itemPosClicked]).getNm());
			odr_type_edt.setTag(odrTypeList.get(keys[itemPosClicked]).getId());
		}
		else if (type.equals("odrStatLst")){
			odrnew_status_edt.setText(odrStatusList.get(keys[itemPosClicked]).getNm());
			odrnew_status_edt.setTag(odrStatusList.get(keys[itemPosClicked]).getId());
		}
		else if (type.equals("odrPrioLst")){
			odrnew_priority_edt.setText(odrPriorityList.get(keys[itemPosClicked]).getNm());
			odrnew_priority_edt.setTag(odrPriorityList.get(keys[itemPosClicked]).getId());
		}else if(type.equals("odrWrkrLst")){
			odrnew_worker_edt.setText(odrWorkerList.get(keys[itemPosClicked]).getNm());
			odrnew_worker_edt.setTag(odrWorkerList.get(keys[itemPosClicked]).getId());
		}
		if (type.equals("odrTimeLst")){
			odr_duration_edt.setText(minArray.get(itemPosClicked));
			odr_duration_edt.setTag(minArray.get(itemPosClicked));
		}
	}

	private void setDyanmicFont() {

		int size = PreferenceHandler.getCurrrentFontSzForApp(mActivity);
		odr_nm_txt.setTextSize(22 + (size));
		odr_loc_comment_txt.setTextSize(22 + (size));
		odr_type_txt.setTextSize(22 + (size));
		odr_custnm_txt.setTextSize(22 + (size));
		odr_location_txt.setTextSize(22 + (size));
		odr_schedule_date_txt.setTextSize(22 + (size));
		odr_start_time_txt.setTextSize(22 + (size));
		odr_duration_txt.setTextSize(22 + (size));
		odr_alert_txt.setTextSize(22 + (size));
		odrnew_status_txt.setTextSize(22 + (size));
		odrnew_priority_txt.setTextSize(22 + (size));
		odrnew_worker_txt.setTextSize(22 + (size));
		odrnew_invoice_txt.setTextSize(22 + (size));
		odrnew_po_txt.setTextSize(22 + (size));


		odr_nm_edt.setTextSize(22 + (size));
		odr_loc_comment_edt.setTextSize(22 + (size));
		odr_custnm_edt.setTextSize(22 + (size));
		odr_location_edt.setTextSize(22 + (size));
		odrnew_alert_edt.setTextSize(22 + (size));
		odrnew_ssd_edt.setTextSize(22 + (size));
		odrnew_route_edt.setTextSize(22 + (size));
		odr_type_edt.setTextSize(22 + (size));
		odr_schedule_date_edt.setTextSize(22 + (size));
		odr_start_time_edt.setTextSize(22 + (size));
		odr_duration_edt.setTextSize(22 + (size));
		odrnew_status_edt.setTextSize(22 + (size));
		odrnew_priority_edt.setTextSize(22 + (size));
		odrnew_worker_edt.setTextSize(22 + (size));
	}

	// setting view in starting
	private void setEditText(View v) {
		odr_nm_edt= (EditText)v.findViewById(R.id.odr_nm_edt);
		odr_loc_comment_edt= (EditText)v.findViewById(R.id.odr_loc_comment_edt);
		odr_custnm_edt= (EditText)v.findViewById(R.id.odr_custnm_edt);

		if(PreferenceHandler.getCustomerHead(getActivity())!=null && !PreferenceHandler.getCustomerHead(mActivity).equals("")){
			odr_custnm_edt.setText("Select " + PreferenceHandler.getCustomerHead(getActivity()));
		}

		odr_location_edt= (EditText)v.findViewById(R.id.odr_location_edt);
		odrnew_alert_edt= (EditText)v.findViewById(R.id.odrnew_alert_edt);
		odrnew_ssd_edt= (EditText)v.findViewById(R.id.odrnew_ssd_edt);
		odrnew_route_edt = (EditText) v.findViewById(R.id.odrnew_route_edt);

		odr_custnm_edt.setOnClickListener(this);
		odr_location_edt.setOnClickListener(this);
	}

	private void setTextViews(View v) {
		//YD textview only required for dynamic font
		odr_nm_txt = (TextView)v.findViewById(R.id.odr_nm_txt);
		if(PreferenceHandler.getOrderNameHead(getActivity())!=null && !PreferenceHandler.getOrderNameHead(mActivity).equals("")){
			odr_nm_txt.setText(PreferenceHandler.getOrderNameHead(getActivity()).toUpperCase());
		}
		odr_loc_comment_txt = (TextView)v.findViewById(R.id.odr_loc_comment_txt);
		if(PreferenceHandler.getOrderDescHead(getActivity())!=null && !PreferenceHandler.getOrderDescHead(mActivity).equals("")){
			odr_loc_comment_txt.setText(PreferenceHandler.getOrderDescHead(getActivity()).toUpperCase());
		}
		odr_type_txt = (TextView)v.findViewById(R.id.odr_type_txt);
		odr_custnm_txt  = (TextView)v.findViewById(R.id.odr_custnm_txt);
		if(PreferenceHandler.getCustomerHead(getActivity())!=null && !PreferenceHandler.getCustomerHead(mActivity).equals("")){
			odr_custnm_txt.setText(PreferenceHandler.getCustomerHead(getActivity()).toUpperCase());
		}

		odr_location_txt  = (TextView)v.findViewById(R.id.odr_location_txt);
		odr_schedule_date_txt  = (TextView)v.findViewById(R.id.odr_schedule_date_txt);
		odr_start_time_txt  = (TextView)v.findViewById(R.id.odr_start_time_txt);
		odr_duration_txt  = (TextView)v.findViewById(R.id.odr_duration_txt);
		odr_alert_txt  = (TextView)v.findViewById(R.id.odr_alert_txt);

		if(PreferenceHandler.getAlertHead(getActivity())!=null && !PreferenceHandler.getAlertHead(mActivity).equals("")){
			odr_alert_txt.setText(PreferenceHandler.getAlertHead(getActivity()).toUpperCase());
		}

		odrnew_status_txt = (TextView)v.findViewById(R.id.odrnew_status_txt);
		odrnew_priority_txt = (TextView)v.findViewById(R.id.odrnew_priority_txt);
		odrnew_worker_txt = (TextView)v.findViewById(R.id.odrnew_worker_txt);

		if(PreferenceHandler.getWorkerHead(mActivity)!=null && !PreferenceHandler.getWorkerHead(mActivity).equals("")){
			odrnew_worker_txt.setText(PreferenceHandler.getWorkerHead(mActivity).toUpperCase());
		}
		//YD textview only required for dynamic font------------->END

		odr_type_edt= (TextView)v.findViewById(R.id.odr_type_edt);
		odr_schedule_date_edt= (TextView)v.findViewById(R.id.odr_schedule_date_edt);
		odr_start_time_edt= (TextView)v.findViewById(R.id.odr_start_time_edt);
		odr_duration_edt= (TextView)v.findViewById(R.id.odr_duration_edt);
		odrnew_status_edt= (TextView)v.findViewById(R.id.odrnew_status_edt);
		odrnew_priority_edt= (TextView)v.findViewById(R.id.odrnew_priority_edt);
		odrnew_worker_edt = (TextView)v.findViewById(R.id.odrnew_worker_edt);

		odrnew_invoice_txt = (TextView)v.findViewById(R.id.odrnew_route_txt);
		odrnew_po_txt  = (TextView)v.findViewById(R.id.odrnew_ssd_txt);

		if(PreferenceHandler.getPOHead(mActivity)!=null && !PreferenceHandler.getPOHead(mActivity).equals(""))
			odrnew_po_txt.setText(PreferenceHandler.getPOHead(mActivity).toUpperCase());
		else
			odrnew_po_txt.setText("PO#");

		if(PreferenceHandler.getInvHead(mActivity)!=null && !PreferenceHandler.getInvHead(mActivity).equals(""))
			odrnew_invoice_txt.setText(PreferenceHandler.getInvHead(mActivity).toUpperCase());
		else
			odrnew_invoice_txt.setText("INVOICE#");


		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MMM:dd:yyyy hh:mm a");

		Log.e("Date :", String.valueOf(sdf.format(cal.getTime()).split(" ")[0].replace(":", " ")));
		Log.e("Time :", String.valueOf(sdf.format(cal.getTime()).split(" ")[1] + " " + sdf.format(cal.getTime()).split(" ")[2]));
        //Jun:03:2015 08:42 PM
		odr_schedule_date_edt.setText(String.valueOf(sdf.format(cal.getTime()).split(" ")[0].replace(":", " ")));//Jun 03 2015

		String notat="";
		if ((sdf.format(cal.getTime()).split(" ")[2]).equals("PM"))
			 notat = "pm";
		else if ((sdf.format(cal.getTime()).split(" ")[2]).equals("AM"))
			notat = "am";
		else
			notat = notat = (sdf.format(cal.getTime()).split(" ")[2]);

		int hours = Integer.parseInt(sdf.format(cal.getTime()).split(" ")[1].split(":")[0]);
		int minute = Integer.parseInt(sdf.format(cal.getTime()).split(" ")[1].split(":")[1]);
		String min = String.format("%02d", getRoundedMinute(minute));   // (%02d) if min is less than 10 then add 0 before min

		//odr_start_time_edt.setText(String.valueOf(sdf.format(cal.getTime()).split(" ")[1] + " " + notat)); // commented by mandeep
		odr_start_time_edt.setText(String.valueOf(hours + ":" + min + " " + notat));
		odr_duration_edt.setText("60 min");
		odr_duration_edt.setTag("60");//YD keeping 60 mins as starting time for duration field

		if ( odrTypeList!=null  && ! odrTypeList.isEmpty()) {
            Map.Entry<Long, OrderTypeList> entry = odrTypeList.entrySet().iterator().next();
			odr_type_edt.setText(entry.getValue().getNm());
            odr_type_edt.setTag(entry.getValue().getId());
			elemtSelectedInSearchDlog = entry.getValue().getId();
        }
		odr_type_edt.setOnClickListener(this);
		odr_schedule_date_edt.setOnClickListener(this);
		odr_start_time_edt.setOnClickListener(this);
		odr_duration_edt.setOnClickListener(this);

		if (odrStatusList!= null){
			//commented by mandeep
			/*	Map.Entry<Long, OrderStatus> entry = odrStatusList.entrySet().iterator().next();
			odrnew_status_edt.setText(entry.getValue().getNm());
			odrnew_status_edt.setTag(entry.getValue().getId());*/
			Long[] stsKeys = odrStatusList.keySet().toArray(new Long[odrStatusList.size()]);
			if (odrStatusList.get(Long.valueOf(1))!=null) {
				odrnew_status_edt.setText(odrStatusList.get(Long.valueOf(1)).getNm());
				odrnew_status_edt.setTag(odrStatusList.get(Long.valueOf(1)).getId());
			}else {
				odrnew_status_edt.setText(odrStatusList.get(stsKeys[2]).getNm());
				odrnew_status_edt.setTag(odrStatusList.get(stsKeys[2]).getId());
			}
		}
		odrnew_status_edt.setOnClickListener(this);

		if (odrPriorityList!= null){
			// commented by mandeep
			//	Map.Entry<Long, OrderPriority> entry = odrPriorityList.entrySet().iterator().next();
			//	odrnew_priority_edt.setText(entry.getValue().getNm());
			//	odrnew_priority_edt.setTag(entry.getValue().getId());
			Long[] prtyKeys = odrPriorityList.keySet().toArray(new Long[odrPriorityList.size()]);
			odrnew_priority_edt.setText(odrPriorityList.get(prtyKeys[1]).getNm());
			odrnew_priority_edt.setTag(odrPriorityList.get(prtyKeys[1]).getId());
		}
		odrnew_priority_edt.setOnClickListener(this);

		if(odrWorkerList!=null){
			Map.Entry<Long, Worker> entry = odrWorkerList.entrySet().iterator().next();
			Worker currentRes=odrWorkerList.get(PreferenceHandler.getResId(mActivity));
			if (currentRes!= null){
				odrnew_worker_edt.setText(currentRes.getNm());
				odrnew_worker_edt.setTag(currentRes.getId());
			}else {
				odrnew_worker_edt.setText(entry.getValue().getNm());
				odrnew_worker_edt.setTag(entry.getValue().getId());
			}
		}
		odrnew_worker_edt.setOnClickListener(this);
	}



	private void getTime(String time) {
		int temphour = 0;
		final Calendar mcurrentTime = Calendar.getInstance();
		if (time.split(":")[1].split(" ")[1].equalsIgnoreCase("am")) {
			temphour= Integer.valueOf(time.split(":")[0]);
		} else {
			temphour = Integer.valueOf(time.split(":")[0]) + 12;
		}

		final int hour = temphour;
		final int minute = Integer.valueOf(time.split(":")[1].split(" ")[0]);
		int sizeDialogStyleID = Utilities.getDialogTextSize(mActivity);

		CustomTimePickerDialog dialog = new CustomTimePickerDialog(mActivity, new CustomTimePickerDialog.OnTimeSetListener(){
					@Override
			public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
				// TODO Auto-generated method stub
						String am_pm = "";

						mcurrentTime.set(Calendar.HOUR_OF_DAY, selectedHour);
						mcurrentTime.set(Calendar.MINUTE, selectedMinute);
						mcurrentTime.set(Calendar.SECOND, 0);

						if (mcurrentTime.get(Calendar.AM_PM) == Calendar.AM)
							am_pm = "am";
						else if (mcurrentTime.get(Calendar.AM_PM) == Calendar.PM)
							am_pm = "pm";

						String strHrsToShow = (mcurrentTime.get(Calendar.HOUR) == 0) ? "12"
								: mcurrentTime.get(Calendar.HOUR) + "";

						String strMinToShow = String.valueOf(mcurrentTime.get(Calendar.MINUTE));

						int mHour = Integer.parseInt(strHrsToShow);
						int mMin = mcurrentTime.get(Calendar.MINUTE);
						if(mHour<10)
							strHrsToShow = "0"+strHrsToShow;

						if(mMin<10)
							strMinToShow = "0"+ strMinToShow;

						odr_start_time_edt.setText(strHrsToShow + ":"
								+ strMinToShow + " "
								+ am_pm);
					}

				}, hour, minute, false,sizeDialogStyleID);

		dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", dialog);
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", dialog);

		dialog.setTitle("Set Time");
		dialog.show();

		Utilities.setDividerTitleColor(dialog, 0 ,  mActivity);
		Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		Utilities.setDefaultFont_12(button_negative);
		Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		Utilities.setDefaultFont_12(button_positive);
	}

	private void getCalender() {
		try{
			Date date = new SimpleDateFormat("MMM").parse(odr_schedule_date_edt.getText().toString().split(" ")[0]);
		 Calendar cal = Calendar.getInstance();
	        cal.setTime(date);

			int mMonth = Integer.valueOf(cal.get(Calendar.MONTH));
			int mDay = Integer.valueOf(odr_schedule_date_edt.getText().toString().split(" ")[1]);
			int mYear = Integer.valueOf(odr_schedule_date_edt.getText().toString().split(" ")[2]);

			int sizeDialogStyleID = Utilities.getDialogTextSize(mActivity);

			MyDatePickerDialog dialog = new MyDatePickerDialog(mActivity, new mDateSetListener(), mYear, mMonth, mDay, false,CreateOrderFragment.this,sizeDialogStyleID);
			dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", dialog);
			dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", dialog);
			if (Build.VERSION.SDK_INT >= 11) {
				dialog.getDatePicker().setCalendarViewShown(false);
			}


			dialog.setTitle("Set Date");
			dialog.show();
			Utilities.setDividerTitleColor(dialog, 0, mActivity);

			Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			Utilities.setDefaultFont_12(button_negative);
			Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Utilities.setDefaultFont_12(button_positive);
		}catch(Exception ex){
			ex.printStackTrace();
		}
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

		//	String mCurrentDate = mYear + "-" + editMonth + "-" + editDate;
			Date curDate = new Date(mYear, Integer.valueOf(editMonth)-1, Integer.valueOf(editDate));
			String mCurrentDate = new SimpleDateFormat("MMM").format(curDate) + " " + editDate + " " + mYear;
			odr_schedule_date_edt.setText(mCurrentDate);
		}
	}

	public ArrayList<String> getDurationvaluesData()
	 {
		 int i=5;
		 ArrayList<String> minArray = new ArrayList<String>();
		 for(i=5;i<600;i+=5)
			 minArray.add(i+" mins");
		 return minArray;
	 }

	@Override
	public void headerClickListener(String callingId) {
		// TODO Auto-generated method stub
		if(callingId.equals(mActivity.HeaderDonePressed)){
		/*{"url":"'+AceRoute.appUrl+'",'+
				'"type": "post",'+
					'"data":{"id": "'+order_id+'",'+
					'"cid":"'+cust_id+'",'+
					'"cnm":"'+cust_nm+'",'+
					'"lid":"'+cust_siteid+'",'+
					'"cntid":"'+cust_contactid+'",'+

					'"geo":"'+event_geocode+'",'+
					'"start_date":"'+start_date+'",'+
					'"end_date":"'+end_date+'",'+
					'"dtl":"'+order_desc+'",'+
					'"orderStartTime":"'+orderStartTime+'",'+
					'"orderEndTime":"'+orderEndTime+'",'+
					'"nm":"'+order_name+'",'+
					'"po":"'+order_po+'",'+
					'"tid":"'+order_typeid+'",'+
					'"prev_id":"'+order_prtid+'",'+
					'"inv":"'+order_inst+'",'+
					'"wkf":"'+order_wkfid+'",'+
					'"note":"'+order_notes+'",'+
					'"flg":"'+order_flg+'",'+
					'"ltpnm":"'+siteTypeName+'",'+
					'"ctpnm":"'+custTypeName+'",'+
					'"routeid":"'+res_id+'",'+
					'"rid2":"'+order_notes+'",'+
					'"reset":"'+reset+'",'+
					'"type":"'+type+'",'+
					'"rpt":"'+""+'",'+
					'"est":"'+order_startwin+'",'+
					'"lst":"'+order_endwin+'",'+
				    '"etm":"'+tstamp+'",'+
					'"wkfupd":"'+wkfid_upd+'",'+
					'"action": "'+action+'"}}*/

			String Errormsg=null;
			Errormsg = validation.checkValidation(this , mActivity);
			if(Errormsg.equals("")){
			// for time

				String newOrderStartTime=null;
			String newOrderStartTimeOld = odr_start_time_edt.getText().toString();//4:50 pm
				if(newOrderStartTimeOld.contains("a.m.")||newOrderStartTimeOld.contains("p.m."))
					 newOrderStartTime = newOrderStartTimeOld.replace(".","");
				else
					newOrderStartTime=newOrderStartTimeOld;

		//4:50 pm

			int  startTimeIndexOfColumn = newOrderStartTime.indexOf(":");//1  indexof start from 0
			String startTimeHour = newOrderStartTime.substring(0,startTimeIndexOfColumn);//4
			String startTimeMinute = null;
			int indexOfAmPm = -1;


			if((newOrderStartTime.indexOf("am") > 0) || (newOrderStartTime.indexOf("pm") > 0)){
				if(newOrderStartTime.indexOf("am") > 0){
					indexOfAmPm = newOrderStartTime.indexOf("am");
					if(startTimeHour.equals("12")){ // if startTimeHour is 12 and it's AM, then hour should be basically 0 i.e. midnight
						startTimeHour = "0";
					}
 					startTimeMinute = newOrderStartTime.substring(startTimeIndexOfColumn+1, (indexOfAmPm-1));// see pm wala  if
				}
				if(newOrderStartTime.indexOf("pm") > 0){
					indexOfAmPm = newOrderStartTime.indexOf("pm");//5
					startTimeHour = newOrderStartTime.substring(0,startTimeIndexOfColumn);//4
					//startTimeHour = parseInt(startTimeHour) + 12;
					if(startTimeHour.equals("12")){ // if startTimeHour is 12 and it's PM, then hour should be basically 12 i.e. noon
						startTimeHour = "12";
					}else{
						startTimeHour = String.valueOf((Long.valueOf(startTimeHour)) + 12);
					}
					startTimeMinute = newOrderStartTime.substring(startTimeIndexOfColumn+1, (indexOfAmPm-1));  //3,5  ans 50
				}
			}

			Long startTimeInMilliSec = null;
			if(startTimeHour != "" && startTimeHour != null){
				startTimeInMilliSec = ((Long.valueOf(startTimeHour))*60 + Long.valueOf(startTimeMinute))*60*1000;// start milliseconds for the day i.e. not since january 1970
			}

			if(odrnew_status_edt.getTag().toString().equals("8"))
				startTimeInMilliSec = (long) 0;



			// for start date
			long orderStartTime = 0;
			String newOrderStartDate = odr_schedule_date_edt.getText().toString();
			if(odrnew_status_edt.getTag().toString().equals("8"))
				newOrderStartDate = "2003/01/14 14:00 -00:00";

			if( newOrderStartDate != null){
				// handle date if status is 8 yash TODO
				SimpleDateFormat convStrToDate = new SimpleDateFormat("MMM dd yyyy");
				Date date;
				try {
					date = (Date) convStrToDate.parseObject(newOrderStartDate);// YD imp
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					orderStartTime = cal.getTimeInMillis();// this is the time since 1970 till current day 12am means midnight
				}
				catch (ParseException e) {
					e.printStackTrace();
				}
			}

			long startDateTemp = orderStartTime + startTimeInMilliSec ; // total millisecond since january 1970

			// for end date
			String newOrderDuration =odr_duration_edt.getText().toString().split(" ")[0];

			long  eventTotalTimeInMilliSec = Long.valueOf(newOrderDuration)*60*1000;
			long endDateTemp = startDateTemp + eventTotalTimeInMilliSec;

			// converting date start and end dates to utc
			String start_date = convertDateToUtc(startDateTemp);
			String end_date = convertDateToUtc(endDateTemp);

			SaveNewOrder req = new SaveNewOrder();
			req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
			req.setType("post");
			req.setAction(Order.ACTION_SAVE_ORDER);
			req.setId("0");
			req.setCustId(custId);
			req.setCustNm(custNm);
			req.setLocationId(siteid);
			req.setCustContactid("");

			req.setGeo(Utilities.getLocation(mActivity));
			orderStartDateSaved = start_date;
			req.setStart_date(start_date);
			req.setEnd_date(end_date);
			req.setOrderDetail(odr_loc_comment_edt.getText().toString());
			req.setOrderStartTime(String.valueOf(startDateTemp));
			req.setOrderEndTime(String.valueOf(endDateTemp));
			req.setOrderNm(odr_nm_edt.getText().toString());
			req.setAlert(odrnew_alert_edt.getText().toString());
			req.setInvoiceNum(odrnew_ssd_edt.getText().toString());
			//req.setPo(odrnew_alert_edt.getText().toString());
			req.setTid(odr_type_edt.getTag().toString());
			req.setPriorityId(odrnew_priority_edt.getTag().toString());
			req.setSsd(odrnew_route_edt.getText().toString());
			orderStatSaved = odrnew_status_edt.getTag().toString();
			req.setOrderStatus(odrnew_status_edt.getTag().toString());
			req.setNote("");
			req.setFlg("0|");
			req.setLocationTypNm(sitetypeNam);
			req.setCustTypNm(custTypeNm);
			req.setResId(odrnew_worker_edt.getTag().toString());
			req.setRid2("");
			req.setReset("0");
			req.setTypeD("true");
			req.setRpt("");
			req.setEst("");
			req.setLst("");
			req.setEtm(String.valueOf(Utilities.getCurrentTime()));
			req.setWkfupd("0");

			Order.saveOrder(req, mActivity, this, SAVENEWORDER);
			}else{
				String D_title = getResources().getString(R.string.msg_slight_problem);
				String D_desc = Errormsg;
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

                    Utilities.setDividerTitleColor(dialog, 0, mActivity);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialog.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				lp.gravity = Gravity.CENTER;
				dialog.getWindow().setAttributes(lp);
			}
		}else if(callingId.equals(mActivity.HeaderPanicPressed)){
			Toast.makeText(mActivity, "SENDING HELP REQUEST", Toast.LENGTH_SHORT).show();
		}
		if(mActivity.getCurrentFocus()!=null){
		InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(odr_nm_edt.getWindowToken(), 0);
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
				if (response.getId()==SAVENEWORDER)
				{
					clearAllFieldsData();  // mandeep for clear data of fields
					// YD TODO if there is not order in the ordersXmlDataStore
					Log.i("AceRoute", "New Order Saved Successfully");
					odrDataMap = (HashMap<Long, Order>)DataObject.ordersXmlDataStore;
					if (odrDataMap==null){
						odrDataMap = new HashMap<Long, Order>();
						DataObject.ordersXmlDataStore = (HashMap<Long, Order>)response.getResponseMap();
					}
					odrDataMap.put((((HashMap<Long, Order>)response.getResponseMap()).entrySet().iterator().next()).getValue().getId(),
							(((HashMap<Long, Order>)response.getResponseMap()).entrySet().iterator().next()).getValue()); // YD setting up the order obect as key and orderid as value
					// YD checking if the date is of some other date
						Date startdate = getStatDate(orderStartDateSaved);
					OrderListMainFragment.optionRefreshState=100;
					// send to order details page
					//if (orderStatSaved.equals("8")|| !Utilities.isTodayDate(mActivity ,startdate)){
						mActivity.popFragments(mActivity.SERVICE_Thread);
						/*}
					else{  // YD commented because no need of going to orderdetail page
						OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
						mActivity.setActiveOrderObject((((HashMap<Long, Order>)response.getResponseMap()).entrySet().iterator().next()).getValue());
						Bundle mBundle=new Bundle();
						mBundle.putString("OrderId", String.valueOf((((HashMap<Long, Order>)response.getResponseMap()).entrySet().iterator().next()).getValue().getId()));
						orderDetailFragment.setArguments(mBundle);
						BaseTabActivity.pushFragToStackRequired =1;// YD using this because ODP was getting push for 2 times once from and second from the tab change fn
						OrderListMainFragment.doNotShowView =1;
						mActivity.pushFragments(Utilities.JOBS,orderDetailFragment , true, true,BaseTabActivity.SERVICE_Thread);
						//mActivity.changeCurrentTab(0,orderDetailFragment );
					}*/
				}
			}
			else if(response.getStatus().equals("success")&&
					response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
			{
				if (response.getId()==SAVENEWORDER)
				{
					mActivity.popFragments(mActivity.SERVICE_Thread);
				}
			}
		}

	}

        private void clearAllFieldsData() {
		try{
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					odr_nm_edt.setText(""); // Owner Name
					odr_loc_comment_edt.setText(""); // Description
					if(PreferenceHandler.getCustomerHead(getActivity())!=null && !PreferenceHandler.getCustomerHead(mActivity).equals("")){
						odr_custnm_edt.setText("Select " + PreferenceHandler.getCustomerHead(getActivity()));
					}else {
						odr_custnm_edt.setText("Select Customer"); // Customer Name
					}
					odr_custnm_edt.setTag("");
					odr_location_edt.setText(""); // Location
					odr_location_edt.setTag("");
					odrnew_alert_edt.setText(""); // Alerts
					odrnew_ssd_edt.setText(""); // SSD (Invoice Number)
					odrnew_route_edt.setText("");  // Route
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	// YD function to convert String date to the date object
		public Date getStatDate(String date){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm Z");
			Date startdate = null;
			try {
				startdate = simpleDateFormat.parse(date);
			} catch (ParseException e) {e.printStackTrace();}
			
			return startdate;
		}
	
        // Created by mandeep for round minute value
	public static  int getRoundedMinute(int minute){
         if(minute % TIME_PICKER_INTERVAL != 0){
            int minuteFloor = minute - (minute % TIME_PICKER_INTERVAL);
            minute = minuteFloor + (minute == minuteFloor + 1 ? TIME_PICKER_INTERVAL : 0);
            if (minute == 60)  
            	minute=0;           
         }
        return minute;
    }

	public void hideSoftKeyboard() {
		if(mActivity.getCurrentFocus()!=null){
	    InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
	}
	}

	public void loadDataOnBack(BaseTabActivity context) {

		mActivity.registerHeader(this);
		String mheader="ADD ORDER";
		if(PreferenceHandler.getOrderHead(getActivity())!=null && !PreferenceHandler.getOrderHead(getActivity()).trim().equals("")){
			mheader= "ADD "+PreferenceHandler.getOrderHead(getActivity()).toUpperCase();
		}
		mActivity.setHeaderTitle("",mheader , "");

		if (PreferenceHandler.getCurtSteCustdatForcustList(mActivity)==1)
		{
			if (getSiteFlag ==1 ){
				getSiteFlag=0;
				PreferenceHandler.setCurtSteCustdat(mActivity,0);
			}

			PreferenceHandler.setCurtSteCustdatForcustList(mActivity,0);
			String data=PreferenceHandler.getCustSiteData(mActivity);

			String[] resultArr = data.split("##");
			siteid = resultArr[0];
			siteNm = resultArr[1];
			sitetypeNam = resultArr[2];
			custId = resultArr[3];
			custNm = resultArr[4];
			custTypeNm = resultArr[5];

			odr_custnm_edt.setTag(custId);
			odr_custnm_edt.setText(custNm);
			odr_location_edt.setTag(siteid);
			odr_location_edt.setText(siteNm);

		}
		
	}
	
}
