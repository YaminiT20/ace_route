package com.aceroute.mobile.software.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.adaptor.OrderFormAdapter;
import com.aceroute.mobile.software.async.IActionOKCancel;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderMedia;
import com.aceroute.mobile.software.component.OrderTask;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.Form;
import com.aceroute.mobile.software.component.reference.ServiceType;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.database.DBEngine;
import com.aceroute.mobile.software.dialog.CustomDialog;
import com.aceroute.mobile.software.dialog.CustomDialog.DIALOG_TYPE;
import com.aceroute.mobile.software.dialog.DatePickerInterface;
import com.aceroute.mobile.software.dialog.MyDatePickerDialog;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.CommonSevenReq;
import com.aceroute.mobile.software.requests.DeleteMediaRequest;
import com.aceroute.mobile.software.requests.GetFileMetaRequest;
import com.aceroute.mobile.software.requests.GetPart_Task_FormRequest;
import com.aceroute.mobile.software.requests.SaveTaskDataRequest;
import com.aceroute.mobile.software.requests.Save_DeleteTaskRequest;
import com.aceroute.mobile.software.utilities.OnSwipeTouchListener;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

@SuppressWarnings("serial")
public class
OrderFormsFragment extends BaseFragment implements IActionOKCancel, HeaderInterface, RespCBandServST, DatePickerInterface {
    private static final int GET_FORMS = 1;
    private static final int DELETEFORMS = 2;
    private int DELETEMEDIA = 9;
    private int GETSITETYPE = 3;
    public static boolean isSyncMedia = false;

    public WebView webviewOrderTask;
    public Order activeOrderObj;


    OnSwipeTouchListener mOnSwipeTouchListener;
    String currentOdrId, currentOdrName, workerid;
    int assetPosition;
    LinearLayout linearLayout;
    TextView textViewForDate, textViewFortime;
    int positionLastEdited = -1;
    HashMap<Long, ServiceType> taskTypeList;
    ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();
    ArrayList<String> optionLst = new ArrayList<String>();
    ArrayList<SiteType> siteTypeArrayList = new ArrayList<SiteType>();
    private int GETFILEMETA = 12;
    private ArrayList<Long> FileIdsList = new ArrayList<>();

    private OrderFormAdapter orderFormListAdapter;
    private SwipeListView orderFormSwipeLstView;
    private CustomDialog customDialog;
    private HashMap<Long, OrderTask> orderTaskListMap;
    private Long[] keys;
    private int mheight = 500;
    private String headerText;
    private HashMap<Long, SiteType> siteAndGenTypeList;
    private HashMap<Long, SiteType> siteGenTypeMapTid_10;
    private HashMap<Long, SiteType> siteGenTypeMapTid_10_trial;
    ArrayList<String> mOrderList = new ArrayList<String>();
    HashMap<Long, Form> orderFormListMap;
    private String formkeys;
    private ArrayList<Integer> positioning = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity.registerHeader(this);
        View v = inflater.inflate(R.layout.activity_customer_order_list, null);
        TypeFaceFont.overrideFonts(mActivity, v);
        initiViewReference(v);
        getSiteOrGenType();
        //setGenoptionList();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getForms();
        if (isSyncMedia) {
            DBEngine.syncDataToSever(mActivity.getApplicationContext(), OrderMedia.TYPE_SAVE);
            isSyncMedia = false;
        }
        //    getFileMeta();
    }

    private void getForms() {
        long numberOfForms = getNumberOfOrderForms(PreferenceHandler.getACTIVE_ORDERid(mActivity));

        if (numberOfForms >= 0) {
            //Get OrderPart req : {"url":"'+AceRoute.appUrl+'",'+'"action":"getorderpart",'+'"oid":"'+orderId+'"}
            GetPart_Task_FormRequest req = new GetPart_Task_FormRequest();
            req.setAction(Form.ACTION_GET_ORDER_FORM);
            req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(getActivity()) + "/mobi");
            req.setOid(currentOdrId);

            Form.getData(req, mActivity, OrderFormsFragment.this, GET_FORMS);
        }
    }

    private Long getNumberOfOrderForms(String currentOdrId2) {

        HashMap<Long, Order> orderList = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
        Order odrObj = orderList.get(Long.valueOf(currentOdrId2));
        //workerid = odrObj.getPrimaryWorkerId();
        workerid = String.valueOf(PreferenceHandler.getResId(mActivity));
        if (odrObj != null) {
            return odrObj.getCustFormCount();
        } else
            return 0L;
    }

    private void setGenoptionList() {
        //siteGenTypeMapTid_10 = BaseTabActivity.siteGenTypeMapTid_10;//YD if any issue comes with data then need to get data from db and used it
        if (siteGenTypeMapTid_10 != null) {
            LinkedHashMap<Long, SiteType> sortedMap = sortSiteTypeListOnFormNames(siteGenTypeMapTid_10);
            for (Map.Entry<Long, SiteType> entry : sortedMap.entrySet()) {
                String name = entry.getValue().getNm();
                Log.d("TAG", name);
                optionLst.add(name);
                System.out.println("Value of option list " + optionLst.size());
                siteTypeArrayList.add(entry.getValue());
            }

        }
    }

    private LinkedHashMap<Long, SiteType> sortSiteTypeListOnFormNames(HashMap<Long, SiteType> siteGenTypeMapTid_10) {

        LinkedHashMap<Long, SiteType> sortedMap = new LinkedHashMap<Long, SiteType>();

        // Convert Map to List
        List<Map.Entry<Long, SiteType>> list =
                new LinkedList<Map.Entry<Long, SiteType>>(siteGenTypeMapTid_10.entrySet());

        // Sort list_cal with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<Long, SiteType>>() {
            public int compare(Map.Entry<Long, SiteType> o1,
                               Map.Entry<Long, SiteType> o2) {
                return o1.getValue().getNm().compareTo(o2.getValue().getNm());
            }
        });

        // Convert sorted map back to a Map

        for (Iterator<Map.Entry<Long, SiteType>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<Long, SiteType> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());

        }

        return sortedMap;

    }

    private void getCalender(TextView view) {
        try {

            Calendar cal = Calendar.getInstance();
            int mDay;
            int mYear;
            if (!view.getText().toString().trim().equals("") && !view.getText().toString().trim().equals(UnavailableFragment.sdate_str) && !view.getText().toString().trim().equals(UnavailableFragment.edate_str)) {
                Date date = new SimpleDateFormat("MMM").parse(view.getText().toString().split(" ")[0]);
                cal.setTime(date);
                mDay = Integer.valueOf(view.getText().toString().split(" ")[1]);
                mYear = Integer.valueOf(view.getText().toString().split(" ")[2]);
            } else {
                mDay = cal.get(Calendar.DAY_OF_MONTH);
                mYear = cal.get(Calendar.YEAR);
            }

            int mMonth = Integer.valueOf(cal.get(Calendar.MONTH));


            int sizeDialogStyleID = Utilities.getDialogTextSize(mActivity);

            MyDatePickerDialog dialog = new MyDatePickerDialog(mActivity, new mDateSetListener(view), mYear, mMonth, mDay, false, OrderFormsFragment.this, sizeDialogStyleID);
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getSiteOrGenType() {
        CommonSevenReq getSiteTypeList = new CommonSevenReq();
        getSiteTypeList.setAction("getgentype");  // YD earlier getsitetype
        getSiteTypeList.setSource("localonly");
        getSiteTypeList.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(getActivity()) + "/mobi");
        SiteType siteTypObj = new SiteType();
        siteTypObj.getObjectDataStore(getSiteTypeList, mActivity, this, GETSITETYPE);
    }

    public void hideSoftKeyboard() {
        if (mActivity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void initiViewReference(View v) {
        final Bundle mBundle = this.getArguments();
        currentOdrId = mBundle.getString("OrderId");
        assetPosition = mBundle.getInt("Position");
        PreferenceHandler.setACTIVE_ORDERid(mActivity, currentOdrId);
        currentOdrName = mBundle.getString("OrderName");
        headerText = PreferenceHandler.getFormHead(mActivity);//mBundle.getString("HeaderText");
        if (headerText != null && !headerText.equals(""))
            headerText = headerText /*+ "S"*/;
        else
            headerText = "FORM";// YD just for backup
        mActivity.setHeaderTitle("", headerText, "");
        orderFormSwipeLstView = (SwipeListView) v.findViewById(R.id.order_detail_lstvw);
        mOnSwipeTouchListener = new OnSwipeTouchListener();
        webviewOrderTask = (WebView) v.findViewById(R.id.webviewOrderTask);
        webviewOrderTask.setBackgroundColor(Color.TRANSPARENT);

        webviewOrderTask.loadUrl("file:///android_asset/loading.html");
    }

    @Override
    public void onActionOk(int requestCode) {//YD 2020 cancel
        if (positionLastEdited != -1) {
            Log.i("AceRoute", "positionLastEdited  for task to delete" + positionLastEdited);
            Form odrFormObj = orderFormListMap.get(keys[positionLastEdited]);

            Save_DeleteTaskRequest delFormReqObj = new Save_DeleteTaskRequest();
            delFormReqObj.setAction(Form.ACTION_DELETE_ORDER_FORM);
            delFormReqObj.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(getActivity()) + "/mobi");
            delFormReqObj.setType("post");

            SaveTaskDataRequest innDelFormReq = new SaveTaskDataRequest();
            innDelFormReq.setTaskId(String.valueOf(odrFormObj.getId()));
            innDelFormReq.setAction(Form.ACTION_DELETE_ORDER_FORM);

            delFormReqObj.setDataObj(innDelFormReq);
            Form.deleteData(delFormReqObj, mActivity, this, DELETEFORMS);
            setTimer();

        }

        //	}
    }

    public void setTimer() {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(mActivity);
        dialog.setMessage("Deleting..., Please Wait.");
        dialog.show();
        final int interval = 2200; // 1 Second
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            public void run() {

                dialog.dismiss();
                getForms();
                //  Toast.makeText(mActivity, "C'Mom no hands!", Toast.LENGTH_SHORT).show();
            }

        };

        handler.postAtTime(runnable, System.currentTimeMillis() + interval);
        handler.postDelayed(runnable, interval);

    }

    @Override
    public void onActionCancel(int requestCode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onActionNeutral(int requestCode) {
        // TODO Auto-generated method stub

    }

    private void doListViewSettings() {
        orderFormSwipeLstView
                .setSwipeListViewListener(new BaseSwipeListViewListener() {
                    @Override
                    public void onOpened(int position, boolean toRight) {

                        orderFormSwipeLstView.closeAnimate(position);
                    }

                    @Override
                    public void onClosed(int position, boolean fromRight) {
                        View rowItem = Utilities.getViewOfListByPosition(position,
                                orderFormSwipeLstView);

                        positionLastEdited = position;

                        RelativeLayout backlayout = (RelativeLayout) rowItem
                                .findViewById(R.id.back_task);
                        backlayout.setBackgroundColor(getResources().getColor(
                                R.color.color_white));
                        TextView chat = (TextView) rowItem
                                .findViewById(R.id.back_view_chat_textview_task);
                        TextView invite = (TextView) rowItem
                                .findViewById(R.id.back_view_invite_textview_task);
                        chat.setVisibility(View.INVISIBLE);
                        invite.setVisibility(View.INVISIBLE);

                        if (fromRight) {
                            //YD using for non-pge app
                            Form odrFormObj = orderFormListMap.get(keys[positionLastEdited]);

                            AddEditFormFragment addEditFormFragment = new AddEditFormFragment();
                            Bundle bundle = new Bundle();
                            if (odrFormObj.getFormkeyonly() != null) {
                                bundle.putString("FormType", "EDIT FORM");
                                bundle.putString("formData", odrFormObj.getFdata());
                                bundle.putLong("FormId", odrFormObj.getId());
                                bundle.putLong("fid", Long.valueOf(odrFormObj.getFtid()));
                                bundle.putString("OrderId", currentOdrId);
                                bundle.putString("OrderName", currentOdrName);
                                bundle.putString("formkey", odrFormObj.getFormkeyonly());
                                bundle.putString("workerid", workerid);
                                try {
                                    bundle.putString("FRM_NM", siteGenTypeMapTid_10.get(Long.valueOf(odrFormObj.getFtid())).getNm());
                                } catch (Exception e) {

                                }
                            } else {
                                bundle.putString("FormType", "EDIT FORM");
                                bundle.putString("formData", odrFormObj.getFdata());
                                bundle.putLong("FormId", odrFormObj.getId());
                                bundle.putLong("fid", Long.valueOf(odrFormObj.getFtid()));
                                bundle.putString("OrderId", currentOdrId);
                                bundle.putString("OrderName", currentOdrName);
                                bundle.putString("FRM_NM", siteGenTypeMapTid_10.get(Long.valueOf(odrFormObj.getFtid())).getNm());

                            }
							/*bundle.putLong("taskTypeId", odrFormObj.getTask_id());
							bundle.putLong("orderTaskId", odrFormObj.getOrder_task_id());
							bundle.putString("taskRate", odrFormObj.getTree_actualcount());
							bundle.putString("taskStatus", odrFormObj.getAction_status());
							bundle.putString("taskComment", odrFormObj.getTree_comment());*/

                            addEditFormFragment.setArguments(bundle);
                            mActivity.pushFragments(Utilities.JOBS, addEditFormFragment, true, true, BaseTabActivity.UI_Thread);
                        } else {
                            // openChatScreen(position);
                            Form odrFormObj = orderFormListMap.get(keys[positionLastEdited]);
                            formkeys = odrFormObj.getFormkeyonly();
                            getFileMeta();

                            customDialog = CustomDialog
                                    .getInstance(
                                            mActivity,
                                            OrderFormsFragment.this,
                                            getResources().getString(
                                                    R.string.msg_delete),
                                            getResources().getString(
                                                    R.string.msg_title),
                                            DIALOG_TYPE.OK_CANCEL, 2, mActivity);
                            customDialog.setCancellable(false);
                            customDialog.show();
                        }
                    }

                    @Override
                    public void onListChanged() {
                    }

                    @Override
                    public void onMove(int position, float x) {
                    }

                    @Override
                    public void onStartOpen(int position, int action,
                                            boolean right) {
                        View rowItem = Utilities.getViewOfListByPosition(position,
                                orderFormSwipeLstView);

                        RelativeLayout backlayout = (RelativeLayout) rowItem
                                .findViewById(R.id.back_task);
                        TextView chat = (TextView) rowItem
                                .findViewById(R.id.back_view_chat_textview_task);
                        TextView chat1 = (TextView) rowItem
                                .findViewById(R.id.back_task_dummy1);//YD chat 1 and chat 2 text view are used to increase the Height of the background view of delete and edit
                        TextView chat2 = (TextView) rowItem
                                .findViewById(R.id.back_task_dummy1_bottom);


                        TextView invite = (TextView) rowItem
                                .findViewById(R.id.back_view_invite_textview_task);
                        TextView invite1 = (TextView) rowItem
                                .findViewById(R.id.back_task_dummy);//YD invite 1 and invite 2 text view are used to increase the Height of the background view of delete and edit
                        TextView invite2 = (TextView) rowItem
                                .findViewById(R.id.back_task_dummy_bottom);


                        if (right) {
                            backlayout.setBackgroundColor(getResources().getColor(R.color.bdr_green));
                            chat.setVisibility(View.GONE);
                            chat1.setVisibility(View.GONE);
                            chat2.setVisibility(View.GONE);

                            invite.setVisibility(View.VISIBLE);
                            invite1.setVisibility(View.VISIBLE);
                            invite2.setVisibility(View.VISIBLE);

                        } else {

                            backlayout.setBackgroundColor(getResources().getColor(R.color.color_red));
                            chat.setVisibility(View.VISIBLE);
                            chat1.setVisibility(View.VISIBLE);
                            chat2.setVisibility(View.VISIBLE);

                            invite.setVisibility(View.GONE);
                            invite1.setVisibility(View.GONE);
                            invite2.setVisibility(View.GONE);

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

        //orderTaskSwipeLstView.setSwipeMode(SwipeListView.SWIPE_MODE_DEFAULT); // there

        orderFormSwipeLstView.setSwipeMode(SwipeListView.SWIPE_MODE_DEFAULT);

//        if (SplashII.wrk_tid >= 4)//YD for delete
//            orderFormSwipeLstView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS);
//        else
        orderFormSwipeLstView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_NONE); // there

//        if (SplashII.wrk_tid >= 6)
//            orderFormSwipeLstView.setSwipeActionRight(SwipeListView.SWIPE_ACTION_DISMISS);
//        else
        orderFormSwipeLstView.setSwipeActionRight(SwipeListView.SWIPE_ACTION_NONE);

        orderFormSwipeLstView.setOffsetLeft(convertDpToPixel()); // left side
        // offset
        orderFormSwipeLstView.setOffsetRight(convertDpToPixel()); // right
        // side
        // offset
        orderFormSwipeLstView.setSwipeCloseAllItemsWhenMoveList(true);
        orderFormSwipeLstView.setAnimationTime(100); // Animation time
        orderFormSwipeLstView.setSwipeOpenOnLongPress(false); // enable or
        // disable
        // SwipeOpenOnLongPress
    }

    public int convertDpToPixel() {
        DisplayMetrics metrics = orderFormSwipeLstView.getContext().getResources().getDisplayMetrics();
        /*
         * float px = dp * (metrics.densityDpi / 160f); return (int) px;
         */
        return metrics.widthPixels;
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


        if (response != null) {
            //        Log.d("TAG1",response.getResponseMap().toString());

            if (response.getStatus().equals("success") && response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED))) {
                if (response.getId() == GETSITETYPE) {
                    DataObject.siteTypeXmlDataStore = response.getResponseMap();
                    siteGenTypeMapTid_10 = new HashMap<Long, SiteType>();
                    siteGenTypeMapTid_10_trial = new HashMap<Long, SiteType>();
                    siteAndGenTypeList = (HashMap<Long, SiteType>) response.getResponseMap();
                    Long keys[] = siteAndGenTypeList.keySet().toArray(new Long[siteAndGenTypeList.size()]);
                    for (int i = 0; i < siteAndGenTypeList.size(); i++) {
                        int tid = siteAndGenTypeList.get(keys[i]).getTid();
                        System.out.println("value  tid " + tid);
                        SiteType siteType = siteAndGenTypeList.get(keys[i]);
                        switch (tid) {
                            case 10: {
                                System.out.println("value cap " + siteType.getCap());
                                String[] arr = new String[0];
                                ArrayList<String> list = new ArrayList<>();

                                if (siteType.getCap().contains("|")) {
                                    arr = siteType.getCap().split(Pattern.quote("|"));
                                    list.addAll(Arrays.asList(arr));

                                } else {
                                    list.add(siteType.getCap());
                                }
                                mOrderList.clear();
                                mOrderList = PreferenceHandler.getArrayPrefs("OrderList", mActivity);
                                System.out.println("value checking preference list  " + mOrderList.toString());

                                for (int a = 0; a < list.size(); a++) {
                                    String str = "";
                                    str = list.get(a);
                                    System.out.println("value checking cap " + str);

                                    System.out.println("value checking position " + assetPosition);
                                    for (int p = 0; p < mOrderList.size(); p++) {
                                        if (assetPosition < mOrderList.size()) {
                                            if (str.equalsIgnoreCase(mOrderList.get(assetPosition))) {
                                                siteGenTypeMapTid_10.put(keys[i], siteType);
                                                System.out.println("value added to siteGenTypeMapTid_10 " + siteType.getNm());
                                            }
                                        }
                                    }

//                                    for (int p = 0; p < mOrderList.size(); p++) {
//                                        if (str.equalsIgnoreCase(mOrderList.get(p))) {
//                                            siteGenTypeMapTid_10.put(keys[i], siteType);
//                                            System.out.println("value added to siteGenTypeMapTid_10_trial " + siteType.getNm());
//                                        }
//                                    }
                                    System.out.println("value added to siteGenTypeMapTid_10 " + siteGenTypeMapTid_10.size());

//                                    if (str.equalsIgnoreCase(PreferenceHandler.getPrefCatid(mActivity))) {
//                                        System.out.println("value added to siteGenTypeMapTid_10");
//                                        siteGenTypeMapTid_10.put(keys[i], siteType);
//                                    }
                                }
//                                if (siteType.getCap().contains("|")) {
//                                    String[] arr = siteType.getCap().split(Pattern.quote("|"));
//                                    String str = "";
//
//                                    System.out.println("value split" + arr);
//                                }

//                                if (siteType.getCap().equalsIgnoreCase("") || siteType.getCap().equalsIgnoreCase(PreferenceHandler.getPrefCatid(mActivity))) {
//                                    siteGenTypeMapTid_10.put(keys[i], siteType);
//                                }

                            }
                        }
                    }
                    setGenoptionList();
                }
                if (response.getId() == GETFILEMETA) {
                    final Object respMap = response.getResponseMap();
                    DataObject.orderPicsXmlStore = response.getResponseMap();
                    getimagesdata(respMap);


                }
                if (response.getId() == GET_FORMS) {
                    Log.i("AceRoute", "Data Found After Getting Task");
                    displayOrderFormList(response);
                }
                if (response.getId() == DELETEFORMS) {
                    Log.i("AceRoute", "NO Action Required After Deleting Task");
                    getAndUpdateNumberOfOrderForms(currentOdrId);
                    for (int a = 0; a < odid.size(); a++) {
                        medialdeleted(odid.get(a));
                    }

                }
                if (response.getId() == DELETEMEDIA) {
                    DBEngine.syncDataToSever(mActivity.getApplicationContext(), OrderMedia.TYPE_SAVE);
                }
            } else if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA))) {
                if (response.getId() == GET_FORMS) {

                    Log.i("AceRoute", "NO Data Found After While Getting Task");
//					 displayOrderTasksList(response);
                }
            }
        }

    }

    public OrderMedia orderPicsArr[];
    public ArrayList<String> odid = new ArrayList<>();

    private void getimagesdata(Object respMap) {
        HashMap<Long, OrderMedia> odrData = (HashMap<Long, OrderMedia>) respMap;
        Map<Long, OrderMedia> responseMap = new LinkedHashMap<Long, OrderMedia>();
        responseMap = sortHashmap(odrData);

        Long[] keys = null;
        if (odrData != null) {
            keys = responseMap.keySet().toArray(new Long[responseMap.size()]);
            orderPicsArr = new OrderMedia[odrData.size()];
        }

        if (responseMap != null) {
            for (int i = 0; i < responseMap.size(); i++) {
                OrderMedia orderPicsArr = odrData.get(keys[i]);
                odid.add(String.valueOf(orderPicsArr.getId()));
            }
        }
    }

    private static HashMap<Long, OrderMedia> sortHashmap(HashMap<Long, OrderMedia> unsortMap) {

        HashMap<Long, OrderMedia> sortedMap = new LinkedHashMap<Long, OrderMedia>();

        if (unsortMap != null) {
            // Convert Map to List
            List<Map.Entry<Long, OrderMedia>> list =
                    new LinkedList<Map.Entry<Long, OrderMedia>>(unsortMap.entrySet());

            // Sort list_cal with comparator, to compare the Map values
            Collections.sort(list, new Comparator<Map.Entry<Long, OrderMedia>>() {
                public int compare(Map.Entry<Long, OrderMedia> o1,
                                   Map.Entry<Long, OrderMedia> o2) {
                    return Double.compare(o2.getValue().getUpd_time(), o1.getValue().getUpd_time());
                }
            });

            // Convert sorted map back to a Map
            for (Iterator<Map.Entry<Long, OrderMedia>> it = list.iterator(); it.hasNext(); ) {
                Map.Entry<Long, OrderMedia> entry = it.next();
                sortedMap.put(entry.getKey(), entry.getValue());
            }
        }
        return sortedMap;
    }

    public void medialdeleted(String field) {

        DeleteMediaRequest req = new DeleteMediaRequest();
        req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(getActivity()) + "/mobi");
        req.setType("post");
        req.setOid(String.valueOf(activeOrderObj.getId()));
        req.setAction(OrderMedia.ACTION_MEDIA_DELETE);
        req.setFileId(String.valueOf(field));

        OrderMedia.deleteData(req, mActivity, OrderFormsFragment.this, DELETEMEDIA);

    }

    private void getAndUpdateNumberOfOrderForms(String orderId) {
        HashMap<Long, Order> orderMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
        Order odrObj = orderMap.get(Long.parseLong(orderId));
        odrObj.setCustFormCount(odrObj.getCustFormCount() - 1);
        getForms();
    }

    private void displayOrderFormList(Response response) {
        if (response.getResponseMap() != null) {
            //orderPartListMap = sortHashMapOnTid_TmeNew((HashMap<Long, OrderPart>) response.getResponseMap());
            orderFormListMap = (HashMap<Long, Form>) response.getResponseMap();
            DataObject.orderFormsXmlStore = orderFormListMap;
            keys = orderFormListMap.keySet().toArray(new Long[orderFormListMap.size()]);

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // notifyDataChange(response,mActivity);
                    orderFormListAdapter = new OrderFormAdapter(mActivity, orderFormListMap, siteGenTypeMapTid_10);
                    orderFormSwipeLstView.setAdapter(orderFormListAdapter);
                    doListViewSettings();
                }
            });

        } else {
            orderFormListMap = new HashMap<Long, Form>();
            orderFormListAdapter = new OrderFormAdapter(mActivity, orderFormListMap, siteGenTypeMapTid_10);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    orderFormSwipeLstView.setAdapter(orderFormListAdapter);
                    doListViewSettings();
                }
            });

        }
    }


//	private void displayOrderTasksList(Response response) {
//		if (response.getResponseMap()!=null)
//		{
//			orderTaskListMap = sortHashMapOnTid_Tme((HashMap<Long, OrderTask>) response.getResponseMap());
//			//orderTaskListMap = (HashMap<Long, OrderTask>) response.getResponseMap();
//			DataObject.orderTasksXmlStore = orderTaskListMap;
//
//			keys = this.orderTaskListMap.keySet().toArray(new Long[this.orderTaskListMap.size()]);
//
//			orderTaskListAdapter = new OrderTaskOldAdapter(mActivity, orderTaskListMap, siteAndGenTypeList);
//			mActivity.runOnUiThread(new Runnable() {
//			     @Override
//			     public void run() {
//			    	 orderTaskSwipeLstView.setAdapter(orderTaskListAdapter);
//			    }
//			});
//			doListViewSettings();
//		}
//		else
//		{
//			orderTaskListMap = new HashMap<Long, OrderTask>();
//			orderTaskListAdapter = new OrderTaskOldAdapter(mActivity,orderTaskListMap ,siteAndGenTypeList );
//			mActivity.runOnUiThread(new Runnable() {
//			     @Override
//			     public void run() {
//			    	 orderTaskSwipeLstView.setAdapter(orderTaskListAdapter);
//			    }
//			});
//			doListViewSettings();
//		}
//
//
//	}

    /**
     * YD This method is first making the different list_cal based on tid in hashmap and then sorting base on time of task created
     *
     * @param responseMap
     * @return
     */
    private HashMap<Long, OrderTask> sortHashMapOnTid_Tme(HashMap<Long, OrderTask> responseMap) {

        if (responseMap != null && responseMap.size() > 1) {
            HashMap<String, ArrayList<OrderTask>> odrTaskTidBasedMap = new HashMap<String, ArrayList<OrderTask>>();
            //YD first seperate the tasks based on tid
            for (Long keys : responseMap.keySet()) {
                OrderTask odrTaskObj = responseMap.get(keys);

                if (odrTaskTidBasedMap.get(taskTypeList.get(odrTaskObj.getTask_id()).getNm()) == null)// YD if tid is not available in the hashmap (for ex: first time)
                {
                    ArrayList<OrderTask> arrlstOdrTask = new ArrayList<OrderTask>();
                    arrlstOdrTask.add(odrTaskObj);
                    odrTaskTidBasedMap.put(taskTypeList.get(odrTaskObj.getTask_id()).getNm(), arrlstOdrTask);
                } else {// YD if the tid is already availabel in the hashmap then just get the list_cal and save in the old list_cal
                    ArrayList<OrderTask> arrlstOdrTask = odrTaskTidBasedMap.get(taskTypeList.get(odrTaskObj.getTask_id()).getNm());
                    arrlstOdrTask.add(odrTaskObj);
                }
            }

            //YD  now first sort the list_cal on keys of hashmap so the have sort based on tid text
            LinkedHashMap<String, ArrayList<OrderTask>> odrTaskTidBasedMapSorted = sortHashMapTidText(odrTaskTidBasedMap);

            //YD TODO now sort the data of hashmap based on time of task created
            LinkedHashMap<Long, OrderTask> finalMap = new LinkedHashMap<Long, OrderTask>();

            for (String tid : odrTaskTidBasedMapSorted.keySet()) {
                ArrayList<OrderTask> arrOderTask = odrTaskTidBasedMap.get(tid);
                sortArrayList(arrOderTask);//YD Sorted list_cal based on reference.

                for (int i = 0; i < arrOderTask.size(); i++) {//  check first if the arroderTask is working on ref or not of ordertasktid based map
                    finalMap.put(arrOderTask.get(i).getOrder_task_id(), arrOderTask.get(i));
                }
            }
            return finalMap;
        } else
            return responseMap;
    }

    private LinkedHashMap<String, ArrayList<OrderTask>> sortHashMapTidText(HashMap<String, ArrayList<OrderTask>> unsortMap) {

        LinkedHashMap<String, ArrayList<OrderTask>> sortedMap = new LinkedHashMap<String, ArrayList<OrderTask>>();

        if (unsortMap != null) {
            // Convert Map to List
            List<Map.Entry<String, ArrayList<OrderTask>>> list =
                    new LinkedList<Map.Entry<String, ArrayList<OrderTask>>>(unsortMap.entrySet());

            // Sort list_cal with comparator, to compare the Map values
            Collections.sort(list, new Comparator<Map.Entry<String, ArrayList<OrderTask>>>() {
                public int compare(Map.Entry<String, ArrayList<OrderTask>> o1,
                                   Map.Entry<String, ArrayList<OrderTask>> o2) {
                    return o1.getKey().compareTo(o2.getKey());
                }
            });

            // Convert sorted map back to a Map
            for (Iterator<Map.Entry<String, ArrayList<OrderTask>>> it = list.iterator(); it.hasNext(); ) {
                Map.Entry<String, ArrayList<OrderTask>> entry = it.next();
                sortedMap.put(entry.getKey(), entry.getValue());
            }
        }
        return sortedMap;
    }

    // Sorting Arraylist for status header
    public ArrayList<OrderTask> sortArrayList(List<OrderTask> unsortList) {

        // Sort list_cal with comparator, to compare the Map values
        Collections.sort(unsortList, new Comparator<OrderTask>() {
            public int compare(OrderTask o1,
                               OrderTask o2) {
                return Double.compare(o2.getUpd_time(), o1.getUpd_time());
            }
        });
        return (ArrayList) unsortList;
    }

    @Override
    public void headerClickListener(String callingId) {
        if (callingId.equals(mActivity.HeaderMapOrderTaskPressed)) {
            if (Utilities.checkInternetConnection(mActivity, false)) {
                GoogleMapFragment.maptype = "TasksList";
                GoogleMapFragment GooglemapFragment = new GoogleMapFragment();
                mActivity.pushFragments(Utilities.JOBS, GooglemapFragment, true, true, BaseTabActivity.UI_Thread);
            }

            /*else {
                MapAllFragment.maptype = "TasksList";
                MapAllFragment mFragment = new MapAllFragment();
                mActivity.pushFragments(Utilities.JOBS, mFragment, true, true, BaseTabActivity.UI_Thread);
            }*///YD 2020

        } else if (callingId.equals(mActivity.HeaderPlusPressed)) {

            showHeaderDialog();

        }
    }

    private void showHeaderDialog() {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity) mActivity, android.R.layout.select_dialog_item, optionLst);
            AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mActivity, AlertDialog.THEME_HOLO_LIGHT);
            builder.setCancelable(true);
            builder.setTitle(Html.fromHtml("<font color='#10c195'><b>SELECT " + headerText + "</b></font>"));
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @SuppressLint("NewApi")
                public void onClick(DialogInterface dialog, int position) {
                    SiteType siteType = siteTypeArrayList.get(position);
                    Form formObj = getFormModeToOpen(siteType);
                    AddEditFormFragment addOrderFormFragment = new AddEditFormFragment();//AddEditTaskOldFragment
                    Bundle bundle = new Bundle();
                    if (formObj != null) {
                        bundle.putString("formData", formObj.getFdata());
                        bundle.putLong("fid", Long.valueOf(formObj.getFtid()));
                        bundle.putString("FormType", "EDIT FORM");
                        bundle.putString("OrderId", currentOdrId);
                        bundle.putString("FRM_NM", siteType.getNm());//YD 2020 This will remain common for both if and else
                        bundle.putLong("FormId", formObj.getId());
                        bundle.putString("workerid", workerid);
                        bundle.putString("OrderName", currentOdrName);

                        if (formObj.getFormkeyonly() != null) {
                            bundle.putString("formkey", formObj.getFormkeyonly());
                        }


                    } else {
                        bundle.putString("formData", siteType.getDtl());
                        bundle.putLong("fid", Long.valueOf(siteType.getId()));
                        bundle.putString("FormType", "ADD FORM");
                        bundle.putString("OrderId", currentOdrId);
                        bundle.putString("workerid", workerid);
                        bundle.putString("FRM_NM", siteType.getNm()); //YD 2020 This will remain common for both if and else
                    }
//            bundle.putString("OrderId", currentOdrId);
//            bundle.putString("OrderName", currentOdrName);
                    addOrderFormFragment.setArguments(bundle);
//            addCustomerOrderFragment.setActiveOrderObj(activeOrderObj);
                    mActivity.pushFragments(Utilities.JOBS, addOrderFormFragment, true, true, BaseTabActivity.UI_Thread);


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

    private Form getFormModeToOpen(SiteType siteType) {
        try {
            JSONObject mainJsonForSiteType = new JSONObject(siteType.getDtl());

            if (mainJsonForSiteType.has("rules")) {
                JSONObject ruleObj = mainJsonForSiteType.getJSONObject("rules");
                int cnt = Integer.valueOf(ruleObj.getString("cnt"));
                long formId = siteType.getId();


                if (orderFormListMap != null && orderFormListMap.size() > 0 && cnt == 1) {
                    for (long id : orderFormListMap.keySet()) {
                        if (formId == Long.valueOf(orderFormListMap.get(id).getFtid())) {
                            return orderFormListMap.get(id);
                        }
                    }
                } else
                    return null;
            } else
                return null;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void setActiveOrderObj(Order activeOrderObj) {
        this.activeOrderObj = activeOrderObj;
        HashMap<Long, Order> order = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
        //DataObject.ordersXmlDataStore = null;
    }

    private void getFileMeta() {
//        long numberOfMeta = getNumberOfOrderMeta();
//        Log.e("Aceroute", "getFileMeta called for Grid view main activity at"+Utilities.getCurrentTimeInMillis());
//        if (numberOfMeta >= 0) {
//            /*{"url":"'+AceRoute.appUrl+'",'+ '"action": "getfilemeta",' +'"oid":"' +orderId+'"}*/
        GetFileMetaRequest req = new GetFileMetaRequest();
        req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(getActivity()) + "/mobi");
        req.setAction(OrderMedia.ACTION_GET_MEDIA);
        req.setOid(currentOdrId);
        req.setFrmkey(formkeys);
        OrderMedia.getData(req, mActivity, this, GETFILEMETA);
        //       }
    }


    public void notifyDataChange(Response response, BaseTabActivity ref) {//YD 2020 check from where this function is being called
        DataObject.orderTasksXmlStore = null;
        DataObject.orderTasksXmlStore = response.getResponseMap();

        orderFormListMap = (HashMap<Long, Form>) response.getResponseMap();
        keys = this.orderFormListMap.keySet().toArray(new Long[this.orderFormListMap.size()]);

        orderFormListAdapter = new OrderFormAdapter(ref, response.getResponseMap(), siteAndGenTypeList);
        orderFormSwipeLstView.setAdapter(orderFormListAdapter);

    }

    public void loadDataOnBack(BaseTabActivity context) {
        mActivity.setHeaderTitle("", headerText, "");
        mActivity.registerHeader(this);
        getForms();
//        getFileMeta();
        if (isSyncMedia) {
            DBEngine.syncDataToSever(mActivity.getApplicationContext(), OrderMedia.TYPE_SAVE);
            isSyncMedia = false;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        //     getForms();

//        getFileMeta();

    }

    @Override
    public void onCancelledBtn() {

    }

    @Override
    public void onPause() {
        super.onPause();
        //    getForms();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    class mDateSetListener implements DatePickerDialog.OnDateSetListener {

        TextView txt;

        public mDateSetListener(TextView view) {
            this.txt = view;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date curDate = null;
            try {
                curDate = sdf.parse(mYear + "-" + editMonth + "-" + editDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String mCurrentDate = new SimpleDateFormat("MMM").format(curDate) + " " + editDate + " " + mYear;
            txt.setText(mCurrentDate);
            txt.setTag(curDate);
            if (textViewForDate.getTag() == null) {
                textViewForDate.setText(mCurrentDate);
                textViewForDate.setTag(curDate);
            }

        }
    }

}
