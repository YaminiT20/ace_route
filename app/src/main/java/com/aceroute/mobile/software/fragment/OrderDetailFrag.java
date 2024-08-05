package com.aceroute.mobile.software.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.SplashII;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.OrderPriority;
import com.aceroute.mobile.software.component.reference.OrderTypeList;
import com.aceroute.mobile.software.component.reference.Worker;
import com.aceroute.mobile.software.database.DBEngine;
import com.aceroute.mobile.software.database.DBHandler;
import com.aceroute.mobile.software.dialog.CustomTimePickerDialog;
import com.aceroute.mobile.software.dialog.DatePickerInterface;
import com.aceroute.mobile.software.dialog.MyDatePickerDialog;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.MySearchDialog;
import com.aceroute.mobile.software.dialog.MySearchDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.updateOrderRequest;
import com.aceroute.mobile.software.utilities.ArrayAdapterwithRadiobutton;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;

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

/**
 * Created by yash on 4/19/16.
 */
public class OrderDetailFrag extends BaseFragment implements HeaderInterface, RespCBandServST, View.OnClickListener ,DatePickerInterface {

    private Order activeOdrobj;
    private String previousTypeID;
    private String previousCatName;
    private String previousPriorityId;
    private String previousPriorityName;
    private String previousDesc;
    private String previousPoVal;
    private String previousInvNm;
    private String previousAlert;
    private String workerPrimaryId;
    Long[] Pkeys;
    Long[] catkeys;
    private int mheight = 500;
    Date previousStartDt = null;
    Date previousEndDt = null;

    String new_start_date_odr, new_end_date_odr;

    HashMap<Long, OrderTypeList> mapOrderType;
    private HashMap<Long, Worker> mapWorker;
    Map<Long, OrderPriority> mapOdrPriority = Collections.synchronizedMap(new LinkedHashMap<Long, OrderPriority>());
    private ArrayList<String> mPriorityArryList;
    private ArrayList<String> mTypeArryList;
    private ArrayList<Long> mCategoryArryListId;
    final ArrayList<Long> seletedItems = new ArrayList<Long>();// ListofWorkers

    //private LinearLayout odr_deatil_add_worker;
    private TextView odr_schedule_date_edt, odr_start_time_edt, odr_duration_edt, /*odr_detail_Worker,*/ odr_detail_worker_edit, txt_category,odr_detail_po_text,odr_detail_inv_text
         ,   txt_Priority, txt_orderid;
    EditText edt_description, edt_ssd, edt_route;
    private String workerLst;
    private int SAVEORDERFIELD_WORKER = 1;
    private LinearLayout parentView;
    private MyDialog dialog;
    private int SAVEORDERFIELD_STATUS_CATEGORY = 2;

   //YD using below variable to just update order objects when done is pressed and receive success
    private String newCategory;
    private String newCategoryId;
    private String newPriorityNm;
    private String newPriorityId;
    private String newSSD;
    private String newRoute;
    private String newDesc;
    Date stUtc = null, edUtc = null;

    private long elemtSelectedInSearchDlog= -1;
    boolean isReadonlyTime=false;
    //String[] minsArr =  new String[]{"0", "5", "10","15", "20", "25", "30", "35", "40", "45", "50", "55"};
    String[] minsArr  =  new String[60];


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity.setHeaderTitle("",PreferenceHandler.getSummaryHead(mActivity).toUpperCase(), "");

        View v = inflater.inflate(R.layout.order_detail_frag,container,false);
        TypeFaceFont.overrideFonts(mActivity, v);
        mActivity.registerHeader(this);
        initiViewReference(v);
        return v;
    }

    private void initiViewReference(View v) {
        setTextViews(v);
        setDyanmicFont();
        setAccess();
        if(PreferenceHandler.getUiconfigReadOnlyTime(getActivity()).equals("1")){
            isReadonlyTime=true;
        }
        parentView = (LinearLayout) v.findViewById(R.id.odr_dtl_parentview);
        mapWorker = (HashMap<Long, Worker>) DataObject.resourceXmlDataStore;
        //YD for ordertype
        mapOrderType = (HashMap<Long, OrderTypeList>) DataObject.orderTypeXmlDataStore;

        txt_orderid.setText(""+activeOdrobj.getId()+" , "+activeOdrobj.getNm());
        Log.d("TAG",String.valueOf(activeOdrobj.getId()));
        //YD for priority
        sortPriorty((HashMap<Long, OrderPriority>) DataObject.orderPriorityTypeXmlDataStore);
        if(mapOdrPriority!=null){
            mPriorityArryList = new ArrayList<String>();
            Pkeys = mapOdrPriority.keySet().toArray(new Long[mapOdrPriority.size()]);

            for(int i = 0; i <mapOdrPriority.size(); i++)
            {
                OrderPriority odrPriorty = mapOdrPriority.get(Pkeys[i]);
                mPriorityArryList.add(String.valueOf(odrPriorty.getNm()));
            }
        }

        //YD main initilization start
        if(activeOdrobj!= null){
            previousTypeID  = String.valueOf(activeOdrobj.getOrderTypeId());
            String typeNm ="";
            if (activeOdrobj.getOrderTypeId()>0  && mapOrderType!=null)
                typeNm = mapOrderType.get(activeOdrobj.getOrderTypeId()).getNm();
            previousCatName = typeNm;

            previousPriorityId = String.valueOf(activeOdrobj.getPriorityTypeId());
            previousPriorityName = String.valueOf(mapOdrPriority.get(activeOdrobj.getPriorityTypeId()).getNm());

            previousDesc = activeOdrobj.getSummary();
            previousPoVal = activeOdrobj.getPoNumber();
            previousInvNm = activeOdrobj.getInvoiceNumber();
            previousAlert = activeOdrobj.getOrderAlert();

            try {// YD setting up the time in view
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");	// returns before 12pm : Sat Jun 20 09:30:00 IST 2015 and after 12pm it returns: Sat Jun 20 23:30:00 IST 2015
            String strFromDate = activeOdrobj.getFromDate();//2016-05-16 15:25 -00:00
            String strToDate = activeOdrobj.getToDate();

            previousStartDt = simpleDateFormat.parse(strFromDate);//Mon May 16 20:55:00 GMT+05:30 2016

            previousEndDt = simpleDateFormat.parse(strToDate);
            String startDt[] = Utilities.convertDateToAmPMWithout_Zero(previousStartDt.getHours(), previousStartDt.getMinutes()).split(" ");
            SimpleDateFormat sdf = new SimpleDateFormat("MMM:dd:yyyy hh:mm a");

            odr_start_time_edt.setText(startDt[0]+" "+startDt[1]);
            odr_duration_edt.setText(String.valueOf((previousEndDt.getTime() - previousStartDt.getTime()) / 60000) + " minutes");
                odr_duration_edt.setTag(String.valueOf((previousEndDt.getTime() - previousStartDt.getTime()) / 60000));

            for(int i=0; i<=59; i++){
                minsArr[i]= String.valueOf(i);
            }
            odr_schedule_date_edt.setText(String.valueOf(sdf.format(previousStartDt.getTime()).split(" ")[0].replace(":", " ")));//YD displaying: May 16 2016

            txt_category.setText(previousCatName);
            txt_category.setTag(previousTypeID);
            elemtSelectedInSearchDlog = activeOdrobj.getOrderTypeId();
            txt_Priority.setText(previousPriorityName);
            txt_Priority.setTag(previousPriorityId);

            edt_description.setText(previousDesc);
            edt_ssd.setText(previousInvNm);//invoice
            edt_route.setText(previousPoVal);//po

            workerPrimaryId = String.valueOf(activeOdrobj.getPrimaryWorkerId());

            String[] strWorkers = workerPrimaryId.split("\\|", -1);

                String workerNames = "";
            for(int i=0; i<strWorkers.length; i++)
            {
               /* LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View ll = inflater.inflate(R.layout.custom_textview, null);
                TextView txtNewWorker = (TextView) ll.findViewById(R.id.odr_detail_new_worker);
                txtNewWorker.setText(String.valueOf(mapWorker.get(Long.parseLong(strWorkers[i])).getNm()));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                odr_deatil_add_worker.addView(ll, params);*/

                if (i==0 && mapWorker!= null && mapWorker.size()>0)
                    workerNames = String.valueOf(mapWorker.get(Long.parseLong(strWorkers[i])).getNm());
                else if(mapWorker!= null && mapWorker.size()>0)
                    workerNames = workerNames +", "+ String.valueOf(mapWorker.get(Long.parseLong(strWorkers[i])).getNm());
            }
                odr_detail_worker_edit.setText(workerNames);
                odr_detail_worker_edit.setTag(workerPrimaryId);

            } catch (ParseException e) {
                e.printStackTrace();
            }

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

    private void showPriorityDialog(final TextView mTextView)
    {
        try{
            ArrayAdapter<String> adapter = new ArrayAdapterwithRadiobutton((Activity) mActivity, android.R.layout.select_dialog_item, mPriorityArryList,txt_Priority.getText().toString());
            AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mActivity, AlertDialog.THEME_HOLO_LIGHT);
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

            //YD increasing the height of the popup if the resolution is high
            DisplayMetrics dm = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int dens=dm.densityDpi;
            if (dens> DisplayMetrics.DENSITY_XHIGH)
                mheight = 800;

            Utilities.setDividerTitleColor(dialog, mheight, mActivity);

            Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            Utilities.setDefaultFont_12(button_negative);
            Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Utilities.setDefaultFont_12(button_positive);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    MySearchDialog searchDialg;
    private void showCategoryDialog()
    {
        searchDialg = new MySearchDialog(mActivity, "Category", "", mCategoryArryListId,mTypeArryList, elemtSelectedInSearchDlog);

        searchDialg.setkeyListender(new MySearchDiologInterface(){
            @Override
            public void onButtonClick() {
                super.onButtonClick();
                searchDialg.cancel();
            }

            @Override
            public void onItemSelected(Long idSelected, String nameSelected) {
                super.onItemSelected(idSelected, nameSelected);
                txt_category.setText(nameSelected);

                OrderTypeList odrtypeObj = mapOrderType.get(idSelected);
                txt_category.setTag(String.valueOf(odrtypeObj.getId()));
                elemtSelectedInSearchDlog = idSelected;
                PreferenceHandler.setPrefCatid(mActivity,String.valueOf(odrtypeObj.getId()));
                searchDialg.cancel();

            }
        });
        searchDialg.setCanceledOnTouchOutside(true);
        searchDialg.onCreate1(null);
        searchDialg.show();

        Utilities.setDividerTitleColor(searchDialg, mheight, mActivity);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(searchDialg.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        searchDialg.getWindow().setAttributes(lp);

    }

   /* private void showDialogCategory(final ArrayList<String> mArrayList, final TextView mTextView, final String title){
        try{
            String dialogTitle = title;
            ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity) mActivity, android.R.layout.select_dialog_item, mArrayList);
            AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mActivity);
            builder.setTitle(dialogTitle);
            builder.setCancelable(true);
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @SuppressLint("NewApi")
                public void onClick(DialogInterface dialog, int position) {
                    if (title.equals("Category")) {
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
            Utilities.setAlertDialogRow(dialog, mActivity);
            dialog.show();

            //YD increasing the height of the popup if the resolution is high
            DisplayMetrics dm = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int dens=dm.densityDpi;
            if (dens> DisplayMetrics.DENSITY_XHIGH)
                mheight = 800;

            Utilities.setDividerTitleColor(dialog, mheight, mActivity);

            Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            Utilities.setDefaultFont_12(button_negative);
            Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Utilities.setDefaultFont_12(button_positive);
        }catch(Exception e){
            e.printStackTrace();
        }
    }*/
    TextView odr_schedule_date_txt, odr_start_time, odr_duration_txt ,txt_priority, txtVw_category, txt_description, odr_assign_worker;

    private void setDyanmicFont() {

        int size = PreferenceHandler.getCurrrentFontSzForApp(mActivity);

        odr_schedule_date_txt.setTextSize(22 + (size));
        odr_start_time.setTextSize(22 + (size));
        odr_duration_txt.setTextSize(22 + (size));
        txt_priority.setTextSize(22 + (size));
        txtVw_category.setTextSize(22 + (size));
        txt_description.setTextSize(22 + (size));
        odr_assign_worker.setTextSize(22 + (size));
        odr_detail_po_text.setTextSize(22 + (size));
        odr_detail_inv_text.setTextSize(22 + (size));

        odr_detail_worker_edit.setTextSize(22 + (size));
        odr_start_time_edt.setTextSize(22 + (size));
        odr_duration_edt.setTextSize(22 + (size));
        odr_schedule_date_edt.setTextSize(22 + (size));
        txt_category.setTextSize(22 + (size));
        txt_Priority.setTextSize(22 + (size));
        edt_description.setTextSize(22 + (size));
        edt_ssd.setTextSize(22 + (size));
        edt_route.setTextSize(22 + (size));
    }

    private void setAccess() {

        if(SplashII.wrk_tid >=6){
            edt_route.setEnabled(false);
            edt_ssd.setEnabled(false);
            edt_description.setEnabled(false);
            txt_Priority.setEnabled(false);
            txt_category.setEnabled(false);
            odr_schedule_date_edt.setEnabled(false);
            odr_start_time_edt.setEnabled(false);
            odr_duration_edt.setEnabled(false);
            odr_detail_worker_edit.setEnabled(false);
        }
    }

    private void setTextViews(View v) {

        odr_schedule_date_txt  = (TextView)v.findViewById(R.id.odr_schedule_date_txt_odp);
        odr_start_time  = (TextView)v.findViewById(R.id.odr_start_time_odp);
        odr_duration_txt  = (TextView)v.findViewById(R.id.odr_duration_txt_odp);
        txt_priority  = (TextView)v.findViewById(R.id.txt_priority_odp);
        txtVw_category  = (TextView)v.findViewById(R.id.txt_category_odp);
        txt_description  = (TextView)v.findViewById(R.id.txt_description_odp);
        odr_assign_worker  = (TextView)v.findViewById(R.id.odr_lbl_assign_worker);

        if(PreferenceHandler.getWorkerHead(mActivity)!=null && !PreferenceHandler.getWorkerHead(mActivity).equals("")){
            odr_assign_worker.setText(PreferenceHandler.getWorkerHead(mActivity).toUpperCase());
        }

        if(PreferenceHandler.getOrderDescHead(getActivity())!=null && !PreferenceHandler.getOrderDescHead(mActivity).equals("")){
            txt_description.setText(PreferenceHandler.getOrderDescHead(getActivity()).toUpperCase());
        }

        odr_detail_po_text = (TextView)v.findViewById(R.id.txt_name_route_odp);
        if(PreferenceHandler.getPOHead(mActivity)!=null && !PreferenceHandler.getPOHead(mActivity).equals(""))
            odr_detail_po_text.setText(PreferenceHandler.getPOHead(mActivity).toUpperCase());
        else
            odr_detail_po_text.setText("PO#");

        odr_detail_inv_text= (TextView)v.findViewById(R.id.txt_name_ssd_odp);
        if(PreferenceHandler.getInvHead(mActivity)!=null && !PreferenceHandler.getInvHead(mActivity).equals(""))
            odr_detail_inv_text.setText(PreferenceHandler.getInvHead(mActivity).toUpperCase());
        else
            odr_detail_inv_text.setText("INVOICE#");

        //YD using when we were having edit button to save or add worker
       /* odr_detail_Worker = (TextView)v.findViewById(R.id.odr_detail_Worker_odp);
        odr_detail_worker_edit = (TextView)v.findViewById(R.id.odr_detail_worker_edit_odp);
        odr_deatil_add_worker = (LinearLayout) v.findViewById(R.id.odr_deatil_add_worker_odp);*/

        odr_detail_worker_edit = (TextView)v.findViewById(R.id.odr_assign_worker_edt_odp);//YD worker edit box

        odr_start_time_edt = (TextView)v.findViewById(R.id.odr_start_time_edt_odp);//2
        odr_duration_edt = (TextView)v.findViewById(R.id.odr_duration_edt_odp);//3
        odr_schedule_date_edt = (TextView)v.findViewById(R.id.odr_schedule_date_edt_odp);//1
        txt_orderid = (TextView)v.findViewById(R.id.txt_orderid);

        txt_category = (TextView) v.findViewById(R.id.category_type_odp);

        txt_Priority = (TextView) v.findViewById(R.id.category_priority_odp);
        edt_description = (EditText) v.findViewById(R.id.edt_description_odp);
        edt_ssd = (EditText) v.findViewById(R.id.edt_ssd_odp);//5
        edt_route = (EditText) v.findViewById(R.id.edt_route_odp);//4

        odr_start_time_edt.setOnClickListener(this);
        odr_schedule_date_edt.setOnClickListener(this);
        odr_duration_edt.setOnClickListener(this);

        txt_category.setOnClickListener(this);
        txt_category.setEnabled(false);
        txt_Priority.setOnClickListener(this);
        txt_Priority.setEnabled(false);
        odr_detail_worker_edit.setOnClickListener(this);
    }

    public void setActiveOrderObj(Order odrObj) {
        activeOdrobj = odrObj;
    }

    private void sortPriorty(HashMap<Long, OrderPriority> odrPriorityListTemp) {
      /*  long[] x = {0,1,4,5,6};

        for (int i=0; i<x.length;i++){
            mapOdrPriority.put(x[i], odrPriorityListTemp.get(x[i]));
        }*/

        long y=0;
        for(int i=0;i<odrPriorityListTemp.size();){
            OrderPriority obj = odrPriorityListTemp.get(y);
            if(obj!=null){
                mapOdrPriority.put(y, odrPriorityListTemp.get(y));
                i++;
                y++;
            }else{
                y++;
            }
        }
    }

    private void getAndUpdateNumberOfOrderRes(String orderId, String workerIds) {
        workerPrimaryId = workerIds; // YD updating local ids for popup
        HashMap< Long , Order> orderMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
        Order odrObj = orderMap.get(Long.parseLong(orderId));
        odrObj.setPrimaryWorkerId(workerPrimaryId);
        //	mWorkersId = workerIds;
        if(workerIds.contains(String.valueOf(PreferenceHandler.getResId(mActivity)))){}
        else{
            String resp_delete = DBEngine.deleteData(mActivity, String.valueOf(activeOdrobj.getId()),
                    Order.TYPE, DBHandler.QUERY_FOR_ORIG);
            ((HashMap<Long, Order>)DataObject.ordersXmlDataStore).remove(activeOdrobj.getId());
            mActivity.popFragments(mActivity.SERVICE_Thread);
        }

        final String[] strWorkers = workerIds.split("\\|", -1);

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // odr_deatil_add_worker.removeAllViews();

                String workerNames = "";
                for(int i=0; i<strWorkers.length; i++)
                {
               /* LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View ll = inflater.inflate(R.layout.custom_textview, null);
                TextView txtNewWorker = (TextView) ll.findViewById(R.id.odr_detail_new_worker);
                txtNewWorker.setText(String.valueOf(mapWorker.get(Long.parseLong(strWorkers[i])).getNm()));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                odr_deatil_add_worker.addView(ll, params);*/

                   if(mapWorker!=null) {
                       if (i == 0)
                           workerNames = mapWorker.get(Long.parseLong(strWorkers[i])).getNm();
                       else
                           workerNames = workerNames + ", " + mapWorker.get(Long.parseLong(strWorkers[i])).getNm();

                   }

                }
                odr_detail_worker_edit.setText(workerNames);
            }
        });


    }

    @Override
    public void headerClickListener(String callingId) {
        if(callingId.equals(BaseTabActivity.HeaderDonePressed)){
            boolean isDifferent = false;

            /*****code to check date and time******/
            getOrderTime(); // YD calculation time from the three fields ie starttime, date, duration
            Date stdate = new Date(new_start_date_odr);// YD new_start_date_odr date is created through getOrderTime function//  2016/05/18 18:45 -00:00
            Date eddate = new Date(new_end_date_odr);  // YD new_end_date_odr   date is created through getOrderTime function

            //YD using below try code to just get exact time in milliseconds
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm Z");

            try {
                stUtc = simpleDateFormat.parse(new_start_date_odr);
                edUtc = simpleDateFormat.parse(new_end_date_odr);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            String dateTime = handleDateRangechange(stdate, eddate);
            if (dateTime!= null){
                if (dateTime.equals("0")) {
                    showErrorDialog("order time difference between start and end time should be atleast 5 mins");
                    return;
                }
                else
                    isDifferent= true;
                //YD also pick out time to save in order save request from dateTime variable
            }


            /*****code to WorkerList******/
            boolean isWorkerChanged = getOrderWorkers();
            if (isWorkerChanged)
                isDifferent= true;

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

            if (desc.length()>200){
                showErrorDialog("Description should be less than 200 characters");
                return;
            }

            String workerList ;
            if (odr_detail_worker_edit.getTag()== null){
                showErrorDialog("Select atleast one worker.");
                return;
            }
            else if(odr_detail_worker_edit.getTag().toString().contains("|")){
                workerList = odr_detail_worker_edit.getTag().toString().replaceAll("\\|","##");
            }
            else
                workerList =  odr_detail_worker_edit.getTag().toString();

            // YD Doing this for checking difference because in previous if it is blank then i keep " " in it.
            if(ssd.equals(""))
                ssd =" ";
            if(desc.equals(""))
                desc =" ";
            if(route.equals(""))
                route =" ";

            //YD doing this because may be some time previous values are empty
            if(previousPoVal.equals(""))
                previousPoVal =" ";
            if(previousDesc.equals(""))
                previousDesc =" ";
            if(previousInvNm.equals(""))
                previousInvNm =" ";

            String key = "Proity|TypeID|PoVal|descript|inv|worker|stTimeMill|edTimeMill|stDate|edDate";
            String oldValStr = previousPriorityId+"|"+previousTypeID+"|"+previousPoVal+"|"+previousDesc+"|"+previousInvNm;
            String newValStr = priorityId+"|"+categoryId+"|"+route+"|"+desc+"|"+ssd;

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
                Long orderId = activeOdrobj.getId(); // MY selected Order Id

                updateOrderRequest req = new updateOrderRequest();
                req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
                req.setType("post");
                req.setId(String.valueOf(orderId));
                req.setName(key);
                req.setValue(newValStr+"|"+workerList+"|"+stUtc.getTime()+"|"+edUtc.getTime()+"|"+new_start_date_odr+"|"+new_end_date_odr);
                req.setAction(Order.ACTION_SAVE_ORDER_FLD);

                Order.saveOrderField(req, mActivity, OrderDetailFrag.this, SAVEORDERFIELD_STATUS_CATEGORY);// YD saving to data base
            }
        }

    }

    private boolean getOrderWorkers() {

        if (odr_detail_worker_edit.getTag().equals(workerPrimaryId))
            return false;
        else
            return true;

    }

    private void getOrderTime() {

        String newOrderStartTime = odr_start_time_edt.getText().toString();//4:50 pm

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

        // for start date
        long orderStartTime = 0;
        String newOrderStartDate = odr_schedule_date_edt.getText().toString();

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

        // converting date start and end dates to utc------->// YD using these below dates for saving in order when updating
        new_start_date_odr = convertDateToUtc(startDateTemp);// 2016/05/16 15:25 -00:00
        new_end_date_odr = convertDateToUtc(endDateTemp);// 2016/05/16 16:15 -00:00
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

    }

    @Override
    public void setResponseCallback(String response, Integer reqId) {

    }

    @Override
    public void setResponseCBActivity(Response response) {
        if (response!=null) {
            if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED))) {
                if (response.getId()==SAVEORDERFIELD_STATUS_CATEGORY)
                {
                    getAndUpdateNumberOfOrderRes(String.valueOf(activeOdrobj.getId()), odr_detail_worker_edit.getTag().toString());
                    activeOdrobj.setPrimaryWorkerId(odr_detail_worker_edit.getTag().toString());

                    activeOdrobj.setOrderTypeId(Long.parseLong(newCategoryId));
                    activeOdrobj.setPriorityTypeId(Long.parseLong(newPriorityId));
                    activeOdrobj.setSummary(newDesc);
                    activeOdrobj.setPoNumber(newSSD);
                    activeOdrobj.setInvoiceNumber(newRoute);


                    activeOdrobj.setStartTime(Long.valueOf(stUtc.getTime()));
                    activeOdrobj.setEndTime(Long.valueOf(edUtc.getTime()));
                    activeOdrobj.setFromDate(new_start_date_odr.replace("/", "-"));
                    activeOdrobj.setToDate(new_end_date_odr.replace("/", "-"));
                    PreferenceHandler.setAppDataChanged(mActivity, true);
                    mActivity.popFragments(BaseTabActivity.SERVICE_Thread);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.category_type_odp: {
                if (mapOrderType != null) {
                    mapOrderType = sortHashmap(mapOrderType);
                    mCategoryArryListId = new ArrayList<Long>();
                    mTypeArryList = new ArrayList<String>();
                    catkeys = mapOrderType.keySet().toArray(new Long[mapOrderType.size()]);
                    if (mTypeArryList.size() < 1) {
                        for (int i = 0; i < mapOrderType.size(); i++) {
                            OrderTypeList odrType = mapOrderType.get(catkeys[i]);
                            mCategoryArryListId.add(odrType.getId());
                            mTypeArryList.add(String.valueOf(odrType.getNm()));
                        }
                    }
                    showCategoryDialog();
                } else {
                    Toast.makeText(mActivity, "No orderType Available", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.category_priority_odp:
                showPriorityDialog(txt_Priority);
                break;

            case R.id.odr_start_time_edt_odp:
                if(!isReadonlyTime)
                getTime(((TextView) v).getText().toString());
                break;

            case R.id.odr_duration_edt_odp:
                /*minArray = getDurationvaluesData();
                ArrayAdapter<String> odrTimeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, minArray);*/
                //if(!isReadonlyTime)
                showDialogForDur(v.getTag());
                break;

            case R.id.odr_schedule_date_edt_odp:
                if(!isReadonlyTime)
                getCalender();
                break;

            case R.id.odr_assign_worker_edt_odp:
                openResourseDialog(workerPrimaryId);
                break;

        }
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



    private void showDialogForDur(Object tag) {
        int currentSetTime = Integer.valueOf((String) tag);
        int arrayOfTme[] = getHrsMins(currentSetTime);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("Set Duration");

        LayoutInflater inflater = mActivity.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.number_picker, null);
        builder.setView(dialogView);
        final NumberPicker hrPck = (NumberPicker) dialogView.findViewById(R.id.hourPicker);
        hrPck.setMaxValue(99);
        hrPck.setMinValue(0);
        hrPck.setValue(arrayOfTme[0]);//YD TODO have to customize numberpicker for changing its size

        final NumberPicker minPck = (NumberPicker) dialogView.findViewById(R.id.minPicker);
        minPck.setMaxValue(60);// YD value should be number of elements in an array for ex in string array below
        minPck.setMinValue(1);
        //minPck.setValue((arrayOfTme[1]/5)+1);//YD it automatically set the value from an array which is being displayed //when diff was of 5 mins
        minPck.setValue(arrayOfTme[1]+1);//YD it automatically set the value from an array which is being displayed
        minPck.setDisplayedValues(minsArr);


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
            if (selectionDivider != null) {
                selectionDivider.setAccessible(true);
                selectionDivider.set(hrPck, getActivity().getResources().getDrawable(
                        R.drawable.picker_view_holo));
                selectionDivider.set(minPck, getActivity().getResources().getDrawable(
                        R.drawable.picker_view_holo));
            }
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

        Utilities.setDividerTitleColor(dialog, mheight, mActivity);
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

    static MyDatePickerDialog dialogCalendr;
    private void getCalender() {

        try{
            Date date = new SimpleDateFormat("MMM").parse(odr_schedule_date_edt.getText().toString().split(" ")[0]);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            int mMonth = Integer.valueOf(cal.get(Calendar.MONTH));
            int mDay = Integer.valueOf(odr_schedule_date_edt.getText().toString().split(" ")[1]);
            int mYear = Integer.valueOf(odr_schedule_date_edt.getText().toString().split(" ")[2]);

            int sizeDialogStyleID = Utilities.getDialogTextSize(mActivity);

            dialogCalendr = new MyDatePickerDialog(mActivity, new mDateSetListener(), mYear, mMonth, mDay, false,OrderDetailFrag.this, sizeDialogStyleID);
            dialogCalendr.setButton(DialogInterface.BUTTON_POSITIVE, "OK", dialogCalendr);
            dialogCalendr.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", dialogCalendr);
            if (Build.VERSION.SDK_INT >= 11) {
                dialogCalendr.getDatePicker().setCalendarViewShown(false);
            }
            dialogCalendr.setTitle("Set Date");
            dialogCalendr.show();

            //YD using for changing text size of datepickerdialog but commented because not getting changed at time of onDateChanged() call;
           /* ViewGroup childpicker;
            childpicker = (ViewGroup) dialogCalendr.findViewById(Resources.getSystem().getIdentifier("month" *//*rest is: day, year*//*, "id", "android"));
            setTextColorBlack(childpicker);
            childpicker = (ViewGroup) dialogCalendr.findViewById(Resources.getSystem().getIdentifier("day" *//*rest is: day, year*//*, "id", "android"));
            setTextColorBlack(childpicker);
            childpicker = (ViewGroup) dialogCalendr.findViewById(Resources.getSystem().getIdentifier("year" *//*rest is: day, year*//*, "id", "android"));
            setTextColorBlack(childpicker);*/

            Utilities.setDividerTitleColor(dialogCalendr, 0, mActivity);

            Button button_negative = dialogCalendr.getButton(DialogInterface.BUTTON_NEGATIVE);
            Utilities.setDefaultFont_12(button_negative);
            Button button_positive = dialogCalendr.getButton(DialogInterface.BUTTON_POSITIVE);
            Utilities.setDefaultFont_12(button_positive);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private static void setTextColorBlack(ViewGroup v) {
        int count = v.getChildCount();
        for (int i = 0; i < count; i++) {
            View c = v.getChildAt(i);
            if(c instanceof ViewGroup){
                setTextColorBlack((ViewGroup) c);
            } else
            if(c instanceof TextView){
                ((TextView) c).setTextColor(Color.RED);
                //((TextView) c).setTextSize(50);
            }
        }
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

                    textView.setTextSize(22 + PreferenceHandler.getCurrrentFontSzForApp(mActivity));
                    textView.setTextColor(getResources().getColor(R.color.light_gray));

                    return view;
                }

            };

            AlertDialog.Builder builder =  new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT);
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

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                        String workerNames = "";

                        for(int i=0; i<seletedItems.size(); i++){
                            if(i==0) {
                                workerLst = String.valueOf(seletedItems.get(i));
                                workerNames = String.valueOf(mapWorker.get(seletedItems.get(i)).getNm());
                            }
                            else {
                                workerLst += "|" + String.valueOf(seletedItems.get(i));
                                workerNames =  workerNames +", "+ String.valueOf(mapWorker.get(seletedItems.get(i)).getNm());
                            }
                        }
                        Log.i("Total Workers: ", String.valueOf(seletedItems.size()));

                        odr_detail_worker_edit.setTag(workerLst);
                        odr_detail_worker_edit.setText(workerNames);
                    }
                    else {
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

    // Sorting hashMap
    private static HashMap<Long, Worker> sortByComparator(HashMap<Long, Worker> unsortMap) {

        // Convert Map to List
        List<Map.Entry<Long, Worker>> list =
                new LinkedList<Map.Entry<Long, Worker>>(unsortMap.entrySet());

        // Sort list_cal with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<Long, Worker>>() {
            public int compare(Map.Entry<Long, Worker> o1,
                               Map.Entry<Long, Worker> o2) {
                return (o1.getValue().getNm()).compareTo(o2.getValue().getNm());
            }
        });

        // Convert sorted map back to a Map
        HashMap<Long, Worker> sortedMap = new LinkedHashMap<Long, Worker>();
        for (Iterator<Map.Entry<Long, Worker>> it = list.iterator(); it.hasNext();) {
            Map.Entry<Long, Worker> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
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

    //YD use this function to make final date to save in order when done is clicked
    public String handleDateRangechange(Date startDate, Date endDate)  // on startDate: Tue Jun 22 10:05:00 GMT+05:30 2015
    {                                          						 // on endDate  : Wed Jun 23 10:35:00 GMT+05:30 2015
        long finalDiffInMin = (endDate.getTime()-startDate.getTime())/60000;

        if(startDate.equals(previousStartDt))
        {
            if(endDate.equals(previousEndDt))
                return null;
        }
        if (finalDiffInMin>=5){
            return "1"; //YD commented the below code because already doing from where calling this function
            /*String startDateUtc = convertDateToUtc(startDate.getTime());//2015/06/21 04:35 -00:00  // server Date(coming this way)
            String endDateUtc = convertDateToUtc(endDate.getTime());	//2015/06/21 05:00 -00:00

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm Z");
            Date stUtc;
            try {
                stUtc = simpleDateFormat.parse(startDateUtc);

                Date edUtc = simpleDateFormat.parse(endDateUtc);

                String orderStartTime = String.valueOf(stUtc.getTime());
                String orderEndTime = String.valueOf(edUtc.getTime());

                return orderStartTime+","+orderEndTime+","+startDateUtc+","+endDateUtc;  //dateStr = "1377947100000,1380588000000,2013/08/31 11:05 -00:00,2013/10/01 0:40 -00:00", orderStartTime = 1377947100000, orderEndTime = 1380588000000, startD


            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
        }
        else{
            //open alert dialog to show that the order time difference between start and end time should be atleast 5 mins
            return "0";
        }
    }


    private void showErrorDialog(String message){
        try{
            dialog = new MyDialog(mActivity, mActivity.getResources().getString(R.string.msg_slight_problem),message,"OK");
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

    public void hideSoftKeyboard() {
        if(mActivity.getCurrentFocus()!=null){
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
