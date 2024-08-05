package com.aceroute.mobile.software.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.HeaderList;
import com.aceroute.mobile.software.ListItem;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.SplashII;
import com.aceroute.mobile.software.StatusList;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.camera.Gridview_MainActivity;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderNotes;
import com.aceroute.mobile.software.component.reference.CheckinOut;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.OrderPriority;
import com.aceroute.mobile.software.component.reference.OrderStatus;
import com.aceroute.mobile.software.component.reference.OrderTypeList;
import com.aceroute.mobile.software.database.DBHandler;
import com.aceroute.mobile.software.dialog.CustomListner;
import com.aceroute.mobile.software.dialog.CustomStatusDialog;
import com.aceroute.mobile.software.dialog.CustomTimePickerDialog;
import com.aceroute.mobile.software.dialog.DatePickerInterface;
import com.aceroute.mobile.software.dialog.MyDatePickerDialog;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.MySearchDialogWithHeader;
import com.aceroute.mobile.software.dialog.MySearchDiologHeaderInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.fragment.CustomerDetailFragment;
import com.aceroute.mobile.software.fragment.GoogleMapFragment;
import com.aceroute.mobile.software.fragment.MapFragment;
import com.aceroute.mobile.software.fragment.MessageSos;
import com.aceroute.mobile.software.fragment.OrderAssetFragment;
import com.aceroute.mobile.software.fragment.OrderDetailFrag;
import com.aceroute.mobile.software.fragment.OrderListMainFragment;
import com.aceroute.mobile.software.fragment.OrderPartsFragment;
import com.aceroute.mobile.software.fragment.OrderFormsFragment;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.model.MediaCountModel;
import com.aceroute.mobile.software.notes.AlertnNoteFrag;
import com.aceroute.mobile.software.requests.ClockInOutRequest;
import com.aceroute.mobile.software.requests.updateOrderRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.bugsnag.android.Bugsnag;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


public class OrderListAdapter extends BaseAdapter implements HeaderInterface, OnClickListener, RespCBandServST, DatePickerInterface {
    LayoutInflater mInflater;
    Context mContext;
    public static boolean ordersyncmessage = false;
    ArrayList<String> mMessageArryList = new ArrayList<String>();
    ArrayList<StatusList> mStatusNmArryList = new ArrayList<StatusList>();
    ArrayList<Long> mStatusIdArryList = new ArrayList<Long>();
    public HashMap<Long, Order> OrderDataMap = null;
    BaseTabActivity mActivity;
    HashMap<Long, TextView> OrderDisTextViewList = null;
    Long[] mKeys;
    HashMap<Long, OrderTypeList> mapOrderType;
    HashMap<Long, OrderStatus> mapOdrStatus;
    Map<Long, OrderPriority> mapOdrPriority = Collections.synchronizedMap(new LinkedHashMap<Long, OrderPriority>());
    Map<Long, OrderPriority> mapOdrPriority1 = Collections.synchronizedMap(new LinkedHashMap<Long, OrderPriority>());
    ArrayList<String> mOrderList = new ArrayList<String>();
    Long[] pKeys;
    MyDialog dialog = null;
    CustomStatusDialog statusDialog = null;
    HashMap<Long, Order> TestOrderHashMap;
    private int mheight = 500;
    OrderListMainFragment odrListobj = null;
    private int SAVEMESSAGE = 1;
    private int SAVE_ORDER_TIME = 4;
    private int SAVEORDERFIELD_STATUS_ALERT = 7;
    private int SAVEORDERFIELD_STATUS_CATEGORY = 8;
    private int SAVENOTE = 9;
    private int CHECKIN_REQ = 10;

    String previousTypeID = "";
    String previousPoVal = "";
    String previousInvNm = "";
    String previousDesc = "";
    String previousAlert = "";
    String previousNote = "";
    private String finalDateStr;
    private String previousCatName, previousPriorityId, previousPriorityName;
    private String newCategory, newCategoryId, newPriorityNm, newPriorityId, newDesc, newSSD, newRoute, newAlert, newNote;
    private int selectedOrderPosition;
    private TextView txtViewAlert, txtViewOrderName, txtViewOrderType, txtViewOrderDesc, txtViewNote, txtViewOrderSepra;

    private ArrayList<String> mTypeArryList;
    Long[] catkeys;

    private ArrayList<String> mPriorityArryList;
    Long[] Pkeys;

    private String orderFromDate, orderToDate;
    private TextView mTxtVwStartDT, mTxtVwEndDT, mTxtStrtCal, mTxtVwEndCal;
    private Button mBtnTime, mBtnCalender;
    private LinearLayout mStartTimeBg, mEndTimeBg;
    private Date gridStartDate, gridEndDate;

    double speed = 48.24;
    int count = 0;
    //public
    // static HashMap<Long, Integer> custAssetCount =  new HashMap<Long, Integer>();//YD using for asset count

    public OrderListAdapter(Context context, OrderListMainFragment odrListobj, BaseTabActivity mActivity, HashMap<Long, Order> dataObj) {
        super();
        Utilities.log(mActivity, "OrderListAdapter is called with data size :" + dataObj.size());
        System.out.println("value OrderListAdapter is called with data size :" + dataObj.size());
        mContext = context;
        this.odrListobj = odrListobj;
        mInflater = LayoutInflater.from(mContext);
        this.mActivity = mActivity;
        this.OrderDataMap = dataObj;
        OrderDisTextViewList = new HashMap<Long, TextView>();
        mapOrderType = (HashMap<Long, OrderTypeList>) DataObject.orderTypeXmlDataStore;
        if (DataObject.orderTypeXmlDataStore == null)
            Utilities.log(mActivity, "NO data found for order type list_cal");
        mActivity.no_of_order.setText(String.valueOf(OrderDataMap.size()));

        //updatin note table
/*
		HashMap<Long , OrderNotes> notesMap = (HashMap<Long , OrderNotes>)DataObject.orderNoteDataStore;
		String previousNote;
		if (notesMap!=null){
			OrderNotes noteObj = notesMap.get(((Order) getItem(0)).getId());

			if (noteObj!=null)
				previousNote = noteObj.getOrdernote();
			else
				previousNote = "";
		}
		else
			previousNote = "";
		ContentValues cv = new ContentValues();
		String length = "0";
		if (previousNote.length() > 0)
			length = "1";
		cv.put(Order.ORDER_NOTE, length);
		cv.put(Order.ORDER_NOTES, previousNote);
		String whereClause = Order.ORDER_ID + "=" + ((Order) getItem(0)).getId();
	  int result = DBHandler.updateTable(DBHandler.orderListTable, cv, whereClause, null);*/
        setMessageData();

    }

    private void setMessageData() {

        HashMap<String, String> mapTemp = new HashMap<String, String>();

        for (int i = 5; i <= 120; i += 5) {
            mMessageArryList.add(i + " Mins Away");
        }

        mapOdrStatus = (HashMap<Long, OrderStatus>) DataObject.orderStatusTypeXmlDataStore;
        HashMap<Long, ArrayList<OrderStatus>> statusMapp = new HashMap<Long, ArrayList<OrderStatus>>(); //YD hashmap to keep header id as a keey and elements as an arraylist
        ArrayList<OrderStatus> headerArraylist = new ArrayList<OrderStatus>();

        //	pKeys = mapOdrStatus.keySet().toArray(new Long[mapOdrStatus.size()]);
        if (mapOdrStatus != null) {
            for (Long key : mapOdrStatus.keySet()) {
                //mapTemp.put(String.valueOf(mapOdrStatus.get(key).getId()), mapOdrStatus.get(key).getNm());//YD commning because status is not hardcoded now.

                if (mapOdrStatus.get(key).getIsgroup() == 1) {
                    if (statusMapp.get(Long.valueOf(mapOdrStatus.get(key).getId())) != null) {
                        headerArraylist.add(mapOdrStatus.get(key));// YD add just to make new header list_cal which will get sort later
                    } else {
                        headerArraylist.add(mapOdrStatus.get(key));
                        statusMapp.put(mapOdrStatus.get(key).getId(), new ArrayList<OrderStatus>());
                    }
                } else {
                    if (statusMapp.get(Long.valueOf(mapOdrStatus.get(key).getGrpId())) != null) {
                        ArrayList<OrderStatus> tempArrList = statusMapp.get(Long.valueOf(mapOdrStatus.get(key).getGrpId()));
                        tempArrList.add(mapOdrStatus.get(key));
                        //YD using for eviction
                        if (mapOdrStatus.get(key).getNm().contains("reschedule"))
                            OrderListMainFragment.rescheduleId = mapOdrStatus.get(key).getId();
                    } else {
                        statusMapp.put(Long.valueOf(mapOdrStatus.get(key).getGrpId()), new ArrayList<OrderStatus>());
                        ArrayList<OrderStatus> tempArrList = statusMapp.get(Long.valueOf(mapOdrStatus.get(key).getGrpId()));
                        tempArrList.add(mapOdrStatus.get(key));
                        //YD using for eviction
                        if (mapOdrStatus.get(key).getNm().contains("reschedule"))
                            OrderListMainFragment.rescheduleId = mapOdrStatus.get(key).getId();
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
                } else if (temp.getIsVisible() == 1 || temp.getIsVisible() == 3) {
                    mStatusNmArryList.add(new HeaderList(temp.getNm(), mActivity));
                    ArrayList<OrderStatus> addelements = statusMapp.get(temp.getId());
                    for (OrderStatus addelem : addelements) {
                        if (addelem.getIsVisible() == 0) {//YD check if the current status need to be displayed or not
                            continue;
                        } else if (addelem.getIsVisible() == 1 || addelem.getIsVisible() == 3) {
                            mStatusNmArryList.add(new ListItem(String.valueOf(addelem.getId()), addelem.getNm(), mActivity, true, false));
                        } else {
                            continue;
                        }
                    }
                } else {
                    continue;
                }
            }

			/*mStatusNmArryList.add(new HeaderList(mContext.getResources().getString(R.string.title_operational)));
			mStatusNmArryList.add(new ListItem("2", mapTemp.get("2")));
			mStatusNmArryList.add(new ListItem("0", mapTemp.get("0")));
			mStatusNmArryList.add(new ListItem("3", mapTemp.get("3")));
			mStatusNmArryList.add(new ListItem("4", mapTemp.get("4")));
			mStatusNmArryList.add(new HeaderList(mContext.getResources().getString(R.string.title_exception)));
			mStatusNmArryList.add(new ListItem("31", mapTemp.get("31"))); // Access Permit
			mStatusNmArryList.add(new ListItem("32", mapTemp.get("32"))); // Follow-up
			mStatusNmArryList.add(new ListItem("33", mapTemp.get("33"))); // Inaccessible
			mStatusNmArryList.add(new HeaderList(mContext.getResources().getString(R.string.title_billing)));
			mStatusNmArryList.add(new ListItem("50", mapTemp.get("50")));
			mStatusNmArryList.add(new ListItem("52", mapTemp.get("52")));
			mStatusNmArryList.add(new ListItem("5", mapTemp.get("5")));
			mStatusNmArryList.add(new HeaderList(mContext.getResources().getString(R.string.title_planning)));
			mStatusNmArryList.add(new ListItem("1", mapTemp.get("1")));
			mStatusNmArryList.add(new ListItem("7", mapTemp.get("7")));*/
            //	mStatusNmArryList.add(new ListItem("8", mapTemp.get("8"))); // Unscheduled

            //YD making order priority list_cal
            sortPriorty((HashMap<Long, OrderPriority>) DataObject.orderPriorityTypeXmlDataStore);

            if (mapOdrPriority != null) {
                mPriorityArryList = new ArrayList<String>();
                Pkeys = mapOdrPriority.keySet().toArray(new Long[mapOdrPriority.size()]);

                for (int i = 0; i < mapOdrPriority.size(); i++) {
                    OrderPriority odrPriorty = mapOdrPriority.get(Pkeys[i]);
                    mPriorityArryList.add(String.valueOf(odrPriorty.getNm()));
                }
            }
        }
    }

    // Sorting Arraylist for status header

    public ArrayList<OrderStatus> sortArrayList(List<OrderStatus> unsortList) {

        // Sort list_cal with comparator, to compare the Map values
        Collections.sort(unsortList, new Comparator<OrderStatus>() {
            public int compare(OrderStatus o1,
                               OrderStatus o2) {
                return o1.getGrpSeq() - o2.getGrpSeq();
            }
        });
        return (ArrayList) unsortList;
    }

    //YD sorting arraylist for status header elements
    public ArrayList<OrderStatus> sortstatusElement(List<OrderStatus> unsortList) {

        // Sort list_cal with comparator, to compare the Map values
        Collections.sort(unsortList, new Comparator<OrderStatus>() {
            public int compare(OrderStatus o1,
                               OrderStatus o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });
        return (ArrayList) unsortList;
    }

    @Override
    public int getCount() {
        Utilities.log(mActivity, "orderlistadaptor getCount called with size :" + OrderDataMap.size());


        if (OrderDataMap.size() > 0) {


            Log.d("size", "tell" + ordersyncmessage);
            mKeys = OrderDataMap.keySet().toArray(new Long[OrderDataMap.size()]);
            updateClockInOutApiData();
        } else {


            ordersyncmessage = true;
        }
        return OrderDataMap.size();
    }

    public void updateClockInOutApiData() {
        if (OrderDataMap.size() == 0) {

            PreferenceHandler.setPrefLstoid(mContext, "0");
            PreferenceHandler.setPrefNxtoid(mContext, "0");

        } else if (OrderDataMap.size() == 1) {
            Order order = (Order) getItem(0);
            if (order.getStatusId() <= 3) {
                PreferenceHandler.setPrefLstoid(mContext, "0");
                PreferenceHandler.setPrefNxtoid(mContext, String.valueOf(order.getId()));
            } else {
                PreferenceHandler.setPrefLstoid(mContext, String.valueOf(order.getId()));
                PreferenceHandler.setPrefNxtoid(mContext, "0");
            }
        } else if (OrderDataMap.size() == 2) {
            Order order = (Order) getItem(0);
            Order SecondOrder = (Order) getItem(1);
            if (((Order) getItem(0)).getStatusId() <= 3) {
                PreferenceHandler.setPrefLstoid(mActivity, "0");
                PreferenceHandler.setPrefNxtoid(mActivity, String.valueOf(order.getId()));
            } else if (order.getStatusId() > 3 && SecondOrder.getStatusId() > 3) {

                PreferenceHandler.setPrefNxtoid(mActivity, "0");
                PreferenceHandler.setPrefLstoid(mActivity, String.valueOf(SecondOrder.getId()));

            } else if (order.getStatusId() > 3 && SecondOrder.getStatusId() <= 3) {

                PreferenceHandler.setPrefNxtoid(mActivity, String.valueOf(SecondOrder.getId()));
                PreferenceHandler.setPrefLstoid(mActivity, String.valueOf(order.getId()));

            }
        } else if (OrderDataMap.size() >= 3) {


            Order order = (Order) getItem(0);
            Order SecondOrder = (Order) getItem(1);
            if (((Order) getItem(0)).getStatusId() <= 3) {
                PreferenceHandler.setPrefLstoid(mActivity, "0");
                PreferenceHandler.setPrefNxtoid(mActivity, String.valueOf(order.getId()));
            } else if (order.getStatusId() > 3 && SecondOrder.getStatusId() <= 3) {

                PreferenceHandler.setPrefNxtoid(mActivity, String.valueOf(SecondOrder.getId()));
                PreferenceHandler.setPrefLstoid(mActivity, String.valueOf(order.getId()));

            } else {
                int lastIndex = -1;
                int searchIndex = 0;
                for (int i = 0; i < mKeys.length; i++) {
                    if (((Order) getItem(i)).getStatusId() <= 3) {
                        lastIndex = i - 1;
                        searchIndex = i;
                        break;
                    }
                }
                if (lastIndex == -1) {
                    PreferenceHandler.setPrefNxtoid(mContext, "0");
                    PreferenceHandler.setPrefLstoid(mContext, String.valueOf(OrderDataMap.get(mKeys[searchIndex]).getId()));
                } else {
                    PreferenceHandler.setPrefNxtoid(mContext, String.valueOf(OrderDataMap.get(mKeys[searchIndex]).getId()));
                    PreferenceHandler.setPrefLstoid(mContext, String.valueOf(OrderDataMap.get(mKeys[lastIndex]).getId()));
                }

            }

        }


    }


    @Override
    public Object getItem(int position) {
        return OrderDataMap.get(mKeys[position]);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public void onCancelledBtn() {

    }

    class ViewHolder {
        //	ImageView orderDetailImageView;
        TextView odrStatus, odrPriority, odrName, odrId, time_dis_lable,
                odrCustName, odrCustAddr, odrType, odrAlert, odrInv, odrCustContNm,/*odrtype_desc_sepr, odrDescription,*/
                odrSchduleTime, odrSchduleType, odrTreecount, odrAssetcount,
                odrPartcount, odrSnapcount, odrSayitcount, odrSigncount, odrNotecount, odrDocCount;
        LinearLayout row_job_call_viewasset, row_job_call_vieweform, row_job_call_viewepart, mCallImageView, mTreeMapImageView, mMsgImageView, orderDetailImageView, mFileView, mStatusView;
        LinearLayout rel_sch_time;
        ImageView mAssetView, mPartsView, mTreeMap, mGlobeMapImageView, erroricon, odr_lst_odr_img_customer, odr_lst_odr_img_order, odr_lst_odr_img_alert/*, odr_lst_odr_img_note*/;
        View order_end_view, time_dis_lable_view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Utilities.log(mActivity, "orderlistadaptor getview called for position :" + position);
        System.out.println("value orderlistadaptor getview called for position :" + position);
        View vi = convertView;
        final ViewHolder holder;
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int dens = dm.densityDpi;
        //DisplayMetrics.DENSITY_HIGH;
        double wi = (double) width / (double) dens;
        double hi = (double) height / (double) dens;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);
        if (convertView == null) {

            vi = mInflater.inflate(R.layout.row_job_list, null);
            TypeFaceFont.overrideFonts(mActivity, vi);
            holder = new ViewHolder();

            holder.mGlobeMapImageView = (ImageView) vi.findViewById(R.id.row_job_globemap_view);
            holder.rel_sch_time = (LinearLayout) vi.findViewById(R.id.tme_above);//YD earlier using for RLayout for time change and id was rel_sch_time
            //holder.orderDetailImageView = (LinearLayout) vi.findViewById(R.id.row_job_detail_imgvw);

            //row_job_call_viewasset,row_job_call_vieweform,row_job_call_viewepart
            holder.row_job_call_vieweform = (LinearLayout) vi.findViewById(R.id.row_job_call_vieweform);
            holder.row_job_call_viewasset = (LinearLayout) vi.findViewById(R.id.row_job_call_viewasset);
            holder.row_job_call_viewepart = (LinearLayout) vi.findViewById(R.id.row_job_call_viewepart);
            holder.mCallImageView = (LinearLayout) vi.findViewById(R.id.row_job_call_view);
            holder.mMsgImageView = (LinearLayout) vi.findViewById(R.id.row_job_msg_view);
            holder.mTreeMapImageView = (LinearLayout) vi.findViewById(R.id.row_job_treemap_view);
            //holder.mFileView = (LinearLayout)vi.findViewById(R.id.row_job_File_view);

            holder.mTreeMap = (ImageView) vi.findViewById(R.id.odr_lst_odr_lyt_view_tree);
            holder.mPartsView = (ImageView) vi.findViewById(R.id.odr_lst_odr_lyt_view_part);
            holder.mAssetView = (ImageView) vi.findViewById(R.id.odr_lst_odr_lyt_view_asset_cnt);

            holder.mStatusView = (LinearLayout) vi.findViewById(R.id.odr_lst_odr_lyt_view_status);
            holder.odrStatus = (TextView) vi.findViewById(R.id.row_job_schd_txtvw);
            holder.odrPriority = (TextView) vi.findViewById(R.id.row_job_priority_txtvw);

            holder.odrSchduleTime = (TextView) vi.findViewById(R.id.textViewTime);
            holder.odrSchduleType = (TextView) vi.findViewById(R.id.textViewTypeAMPM);
            // holder.odrSchduleTimeDiff = (TextView) vi.findViewById(R.id.textViewDifference);
            //	holder.odrSchduleDiffinMin = (TextView) vi.findViewById(R.id.textViewMinute);
            holder.time_dis_lable = (TextView) vi.findViewById(R.id.time_dis_lable);
            holder.time_dis_lable_view = (View) vi.findViewById(R.id.time_dis_lable_view);

            //holder.odrName = (TextView)vi.findViewById(R.id.odr_lst_odr_name);
            // MY R later
            //	holder.odrId = (TextView)vi.findViewById(R.id.odr_lst_odr_id);

            holder.odrCustName = (TextView) vi.findViewById(R.id.odr_lst_odr_cust_name);
            holder.odrCustAddr = (TextView) vi.findViewById(R.id.odr_lst_odr_cust_address);
            holder.odrCustContNm = (TextView) vi.findViewById(R.id.odr_lst_odr_cust_contactnm);

            holder.odrName = (TextView) vi.findViewById(R.id.odr_lst_odr_name);
            holder.odrType = (TextView) vi.findViewById(R.id.odr_lst_odr_type);
			/*holder.odrtype_desc_sepr = (TextView)vi.findViewById(R.id.odrtype_desc_sepr);
			holder.odrDescription = (TextView)vi.findViewById(R.id.odr_lst_odr_description);*/

            holder.odrAlert = (TextView) vi.findViewById(R.id.odr_lst_odr_txt_alert);
            holder.odrNotecount = (TextView) vi.findViewById(R.id.odr_lst_odr_txt_note);
            holder.odrInv = (TextView) vi.findViewById(R.id.odr_lst_odr_txt_inv);

//			holder.odrDocCount = (TextView)vi.findViewById(R.id.order_detail_File_txtvw);

            holder.odrTreecount = (TextView) vi.findViewById(R.id.odr_lst_odr_tree_count);
            holder.odrAssetcount = (TextView) vi.findViewById(R.id.odr_lst_odr_asset_cnt);
            holder.odrPartcount = (TextView) vi.findViewById(R.id.odr_lst_odr_part_count);
            holder.odrSnapcount = (TextView) vi.findViewById(R.id.order_detail_Totalsnap_txtvw);
            holder.odrSayitcount = (TextView) vi.findViewById(R.id.order_detail_TotalSayit_txtvw);
            holder.odrSigncount = (TextView) vi.findViewById(R.id.order_detail_Totalsignit_txtvw);

            holder.odr_lst_odr_img_customer = (ImageView) vi.findViewById(R.id.odr_lst_odr_img_customer);
            holder.odr_lst_odr_img_order = (ImageView) vi.findViewById(R.id.odr_lst_odr_img_order);
            holder.erroricon = (ImageView) vi.findViewById(R.id.erroricon);
            holder.odr_lst_odr_img_alert = (ImageView) vi.findViewById(R.id.odr_lst_odr_img_alert);
            holder.order_end_view = (View) vi.findViewById(R.id.order_end_view);
            //holder.odr_lst_odr_img_note = (ImageView) vi.findViewById(R.id.odr_lst_odr_img_note);

            //Ashish hide the task and Assets.
            System.out.println("preference " + PreferenceHandler.getAssetsConfig(mContext));
            if (PreferenceHandler.getTasksConfig(mContext).equals("0"))
                holder.row_job_call_vieweform.setVisibility(View.GONE);
            if (PreferenceHandler.getAssetsConfig(mContext).equals("0"))
                holder.row_job_call_viewasset.setVisibility(View.GONE);

            vi.setTag(holder);
        } else

            holder = (ViewHolder) vi.getTag();

        //holder.odrName.setText(((Order)getItem(position)).getNm());
        // YD check which all ui to show from task|part|asset
        //task | part | asset | photo | audio | signature | file
        //Asset, Form, Part, Photo, Signature, Audio
        //row_job_call_viewasset,row_job_call_vieweform,row_job_call_viewepart
        String uiConfig = PreferenceHandler.getUiConfig(mContext);
        int listcount = 3;
        if (uiConfig != null && !uiConfig.equals("")) {
            String config[] = uiConfig.split("\\|");
            for (int i = 0; i < config.length; i++) {
                if (i == 0 && config[i].equals("0")) {
                    holder.row_job_call_vieweform.setVisibility(View.GONE);
                    listcount--;
                }
                if (i == 1 && config[i].equals("0")) {
                    holder.row_job_call_viewepart.setVisibility(View.GONE);
                    listcount--;
                }
                if (i == 2 && config[i].equals("0")) {
                    holder.row_job_call_viewasset.setVisibility(View.GONE);
                    listcount--;
                }
                if (i == 3 && config[i].equals("0"))
                    holder.mCallImageView.setVisibility(View.GONE);
                if (i == 4 && config[i].equals("0"))
                    holder.mMsgImageView.setVisibility(View.GONE);
                if (i == 5 && config[i].equals("0"))
                    holder.mTreeMapImageView.setVisibility(View.GONE);
                if (i == 6 && config[i].equals("0")) {
                    //holder.mFileView.setVisibility(View.GONE);
                }
                if (i == 11 && config[i].equals("0")) {
                    holder.time_dis_lable_view.setVisibility(View.GONE);
                    holder.time_dis_lable.setVisibility(View.GONE);
                }
            }
        }
        int s = PreferenceHandler.getCurrrentFontSzForApp(mActivity);
        if (position == 0)//yd using for message request on messagesos
            MessageSos.firstOrderId = ((Order) getItem(position)).getId();
        holder.odrTreecount.setText(String.valueOf(((Order) getItem(position)).getCustFormCount()));
        //holder.odrTreecount.setTextSize(50 +(s));
        if (((Order) getItem(position)).getAssetsCount() != 0)
            holder.odrAssetcount.setText(Long.toString(((Order) getItem(position)).getAssetsCount()));
        else
            holder.odrAssetcount.setText("0");
        //holder.odrAssetcount.setTextSize(50 +(s));


        holder.odrPartcount.setText(String.valueOf(((Order) getItem(position)).getCustPartCount()));
        //holder.odrPartcount.setTextSize(50 + (s));

        if (((Order) getItem(position)).getImgCount() <= 0) {
            holder.odrSnapcount.setText("0");
        } else {
            holder.odrSnapcount.setText(String.valueOf(((Order) getItem(position)).getImgCount()));
            //	holder.odrSnapcount.setText(PreferenceHandler.getPrefActImgCount(mActivity));
        }

        holder.odrSayitcount.setText(String.valueOf(((Order) getItem(position)).getAudioCount()));
        holder.odrSigncount.setText(String.valueOf(((Order) getItem(position)).getSigCount()));


        holder.odrSnapcount.setTextSize(19 + s);
        holder.odrSayitcount.setTextSize(19 + s);
        holder.odrSigncount.setTextSize(19 + s);
//		holder.odrDocCount.setTextSize(19 + s);

        //((Order)getItem(position)).setSequenceNumber(position);  // YD this seq no is for showing number on accesspath on map

        //YD below code is for showing tick sign when order note is available
		/*if(((Order)getItem(position)).getNotCount()>0)
			holder.odrNotecount.setBackgroundResource(R.drawable.ryt_tick);	
		else
			holder.odrNotecount.setBackgroundResource(R.drawable.circle_new);*/

//		if (DataObject.orderNoteDataStore !=null ){
//			HashMap<Long , OrderNotes> notesMap = (HashMap<Long , OrderNotes>)DataObject.orderNoteDataStore;
//			OrderNotes noteObj = notesMap.get(((Order) getItem(position)).getId());
//			if (noteObj!=null)
//				if (screenInches>=6.8) {
//					if(noteObj.getOrdernote().length()>95)
//						holder.odrNotecount.setText(noteObj.getOrdernote().substring(0, 95) + "...");
//					else
//						holder.odrNotecount.setText(noteObj.getOrdernote());
//				}

//				else {
//					if(noteObj.getOrdernote().length()>45)
//						holder.odrNotecount.setText(noteObj.getOrdernote().substring(0, 45) + "...");
//					else
//						holder.odrNotecount.setText(noteObj.getOrdernote());
//
//				}
//			else
//				holder.odrNotecount.setText("");
//		}
        if (String.valueOf(((Order) getItem(position)).getPoNumber()) != null) {
            holder.odrNotecount.setText(String.valueOf(((Order) getItem(position)).getPoNumber()));

        } else {
            holder.odrNotecount.setVisibility(View.GONE);
        }

        holder.odrNotecount.setTextSize(22 + s);

        if (String.valueOf(((Order) getItem(position)).getInvoiceNumber()) != null) {
            holder.odrInv.setText(String.valueOf(((Order) getItem(position)).getInvoiceNumber()));
        } else {
            holder.odrInv.setVisibility(View.GONE);
        }

        holder.odrInv.setTextSize(22 + s);

		/*if (((Order) getItem(position)).getDocCount()>=0)
//			holder.odrDocCount.setText(String.valueOf(((Order) getItem(position)).getDocCount()));

		else
			*/

//			holder.odrDocCount.setText("0");


        // MY R later

        //	holder.odrId.setText(String.valueOf(((Order)getItem(position)).getId()));

        holder.odrCustName.setText(String.valueOf(((Order) getItem(position)).getCustName()));

        holder.odrCustName.setTextSize(22 + s);

        //Toast.makeText(mActivity,""+String.valueOf(((Order) getItem(position)).getFlg()),Toast.LENGTH_LONG).show();

        String error_validation = String.valueOf(((Order) getItem(position)).getFlg());
        char last = error_validation.charAt(error_validation.length() - 1);
        //Toast.makeText(mActivity,"aa"+last,Toast.LENGTH_LONG).show();

        if (last == '1') {
            //Toast.makeText(mActivity,"aa"+last,Toast.LENGTH_LONG).show();
            holder.erroricon.setVisibility(View.VISIBLE);
        }


        final MediaCountModel mediaCountModel = DBHandler.getOrderMediaCount(mContext, ((Order) getItem(position)).getId());
        //Log.d("Niki","media coount "+mediaCountModel.getImgCount() );
        //Toast.makeText(mActivity,"Data Not Updated....",Toast.LENGTH_LONG).show();
        if (mediaCountModel == null) {
            holder.odrSnapcount.setText("0");
            holder.odrSayitcount.setText("0");
            holder.odrSigncount.setText("0");
            //holder.odrDocCount.setText("0");
        } else {
            //	Toast.makeText(mActivity,"Data Updated....",Toast.LENGTH_LONG).show();
            holder.odrSnapcount.setText(String.valueOf(mediaCountModel.getImgCount()));
            holder.odrSayitcount.setText(String.valueOf(mediaCountModel.getAudCount()));
            holder.odrSigncount.setText(String.valueOf(mediaCountModel.getSigCount()));
//					holder.odrDocCount.setText(String.valueOf(mediaCountModel.getFileCount()));

        }

        String streetAdr = String.valueOf(((Order) getItem(position)).getCustSiteStreeAdd());
        String suiteAdr = String.valueOf(((Order) getItem(position)).getCustSiteSuiteAddress());
        if (suiteAdr != null && suiteAdr.length() > 0) {
            if (streetAdr != null && streetAdr.length() > 0) {
                streetAdr = suiteAdr + ", " + streetAdr;
            } else {
                streetAdr = suiteAdr;
            }
        }

        holder.odrCustAddr.setText(streetAdr);
        //holder.odrCustAddr.setTextSize(22 + s);
        OrderDisTextViewList.put(((Order) getItem(position)).getId(), holder.time_dis_lable);
        holder.time_dis_lable.setText(((Order) getItem(position)).getDistanceAndTime());
        holder.time_dis_lable.setTextSize(19 + s);

        String contact_phoneStr = "";
        if (((Order) getItem(position)).getCustContactName() != null && ((Order) getItem(position)).getCustContactName().length() > 0) {
            contact_phoneStr = ((Order) getItem(position)).getCustContactName();
            if (((Order) getItem(position)).getCustContactNumber() != null && ((Order) getItem(position)).getCustContactNumber().length() > 0) {
                contact_phoneStr = contact_phoneStr + ": " + ((Order) getItem(position)).getCustContactNumber();
                holder.odrCustContNm.setText(contact_phoneStr);
                holder.odrCustContNm.setTextSize(22 + s);
            } else {
                holder.odrCustContNm.setText(contact_phoneStr);
                holder.odrCustContNm.setTextSize(22 + s);
            }
        } else {
            contact_phoneStr = ((Order) getItem(position)).getCustContactNumber();
            holder.odrCustContNm.setText(contact_phoneStr);
            holder.odrCustContNm.setTextSize(22 + s);
        }
        //	holder.odrCustContNm.setVisibility(View.GONE);
        //holder.odrName.setText(String.valueOf(((Order) getItem(position)).getId() + ": " + ((Order) getItem(position)).getNm()));
        holder.odrName.setText(((Order) getItem(position)).getNm());
        holder.odrName.setTextSize(22 + s);
        String abs1;
        sortodrPriorty((HashMap<Long, OrderPriority>) DataObject.orderPriorityTypeXmlDataStore);
        if (mapOdrPriority1.get(((Order) getItem(position)).getPriorityTypeId()).getNm() != null) {
            abs1 = mapOdrPriority1.get(((Order) getItem(position)).getPriorityTypeId()).getNm();
        } else {

            abs1 = " ";

        }

        String orderTypDescStr = "";

        System.out.println("Value Order " + ((Order) getItem(position)).getOrderTypeId());
        System.out.println("Value position " + position);
        if (((Order) getItem(position)).getOrderTypeId() != 0 && ((Order) getItem(position)).getOrderTypeId() != -1 && mapOrderType != null
                && mapOrderType.containsKey(((Order) getItem(position)).getOrderTypeId())) {
            orderTypDescStr = mapOrderType.get(((Order) getItem(position)).getOrderTypeId()).getNm();
            System.out.println("value size " + OrderDataMap.size());
            System.out.println("value adding preference list  " + String.valueOf(mapOrderType.get(((Order) getItem(position)).getOrderTypeId()).getId()));
            mOrderList.add(String.valueOf(mapOrderType.get(((Order) getItem(position)).getOrderTypeId()).getId()));
            if (count < OrderDataMap.size()) {
                PreferenceHandler.setArrayPrefs("OrderList", mOrderList, mContext);
                System.out.println("value count " + count++);
            }
            System.out.println("value checking preference list0  " + mOrderList.toString());
            PreferenceHandler.setPrefCatid(mContext, String.valueOf(mapOrderType.get(((Order) getItem(position)).getOrderTypeId()).getId()));

            if (orderTypDescStr.length() > 0 && ((Order) getItem(position)).getSummary() != null && ((Order) getItem(position)).getSummary().length() > 0) {
                //holder.odrType.setText(Html.fromHtml(orderTypDescStr+ ": "+ "<font color='#006400'>" + ((Order) getItem(position)).getSummary() + "</font>"));

                String abs = mapOdrPriority1.get(((Order) getItem(position)).getPriorityTypeId()).getNm();
                String text2 = orderTypDescStr + ": " + abs;
				/*Spannable spannable = new SpannableString(text2);
				spannable.setSpan(new ForegroundColorSpan(Color.RED), orderTypDescStr.length(), (orderTypDescStr + ": "+ ((Order) getItem(position)).getSummary()).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				holder.odrType.setText(spannable, TextView.BufferType.SPANNABLE);*/

                if (abs.equalsIgnoreCase("")) {
                    holder.odrType.setText(orderTypDescStr);
                    holder.odrType.setTextSize(22 + s);
                } else {
                    holder.odrType.setText(text2);
                    holder.odrType.setTextSize(22 + s);
                }

//
//				if(!((Order) getItem(position)).getSummary().trim().equals("")) {
//
//				}
            } else {//YD if order type id is available but the summary is not
                holder.odrType.setText(orderTypDescStr);
                holder.odrType.setTextSize(22 + s);
            }
        } else {//YD is order type id is not availabe but atleast check if summary is available

            orderTypDescStr = ((Order) getItem(position)).getSummary();
            //	holder.odrType.setText(orderTypDescStr);
            holder.odrType.setText(abs1);
            holder.odrType.setTextSize(22 + s);

        }

        if (holder.odrType.getText().toString().trim().equals("")) {

            holder.odrType.setVisibility(View.GONE);
        }

		/*holder.odrDescription.setText(String.valueOf(((Order) getItem(position)).getSummary()));
		if (holder.odrType.getVisibility() == View.VISIBLE && !String.valueOf(((Order) getItem(position)).getSummary()).equals(""))
			holder.odrtype_desc_sepr.setVisibility(View.VISIBLE);*/

        if (((Order) getItem(position)).getOrderAlert() != null && ((Order) getItem(position)).getOrderAlert().length() > 0) {
            holder.odrAlert.setVisibility(View.VISIBLE);
            //holder.odrAlert.setText(String.valueOf(((Order) getItem(position)).getOrderAlert()));
            holder.odrAlert.setTextSize(22 + s);
            String alsetmsg = String.valueOf(((Order) getItem(position)).getSummary());
			/*if (screenInches>=6.8) {
				if(alsetmsg.length()>95)
					holder.odrAlert.setText(alsetmsg.substring(0, 95) + "...");
				else
					holder.odrAlert.setText(alsetmsg);
			}
			else {
				if(alsetmsg.length()>45)
					holder.odrAlert.setText(alsetmsg.substring(0, 45) + "...");
				else
					holder.odrAlert.setText(alsetmsg);

			}*/
            holder.odrAlert.setText(alsetmsg);
        } else {
            holder.odrAlert.setVisibility(View.GONE);
            holder.odrAlert.setTextSize(22 + s);
        }

        holder.mStatusView.setBackgroundResource(R.drawable.custom_odr_status);  // MY Change Status Box border
        GradientDrawable drawable = (GradientDrawable) holder.mStatusView.getBackground();
        // YD Changing color inside the time and status
        LinearLayout lyt = ((LinearLayout) holder.odrStatus.getParent().getParent());
        if (((Order) getItem(position)).getStatusId() == 4 ||
                ((Order) getItem(position)).getStatusId() == 5 || ((Order) getItem(position)).getStatusId() == 6 ||
                ((Order) getItem(position)).getStatusId() == 9 || ((Order) getItem(position)).getStatusId() == 10) {
            //drawable.setStroke(10, mContext.getResources().getColor(R.color.bdr_green));
            //holder.odrStatus.setTextColor(mContext.getResources().getColor(R.color.bdr_green));//YD earilier just setting up the text color for status
            holder.mStatusView.setBackgroundResource(R.drawable.login_rounded_green_fill_bg);
            holder.odrStatus.setTextColor(Color.parseColor("#ffffff"));
            holder.odrStatus.setTextSize(28);
            holder.odrStatus.setTypeface(Typeface.DEFAULT_BOLD);
            //((RelativeLayout)lyt.getChildAt(0)).setBackgroundResource(R.drawable.rounded_green_fill_bg);//YD earlier using for time and duration box background as it was on 0 index

        } else if (((Order) getItem(position)).getStatusId() == 7) {
            //drawable.setStroke(10, mContext.getResources().getColor(R.color.bg_sky_blue));
            holder.mStatusView.setBackgroundResource(R.drawable.round_fill_bg);// YD converted to yellow

            //holder.odrStatus.setTextColor(mContext.getResources().getColor(R.color.bg_sky_blue));
            holder.odrStatus.setTextColor(Color.parseColor("#ffffff"));
            holder.odrStatus.setTextSize(28);
            holder.odrStatus.setTypeface(Typeface.DEFAULT_BOLD);
            //((RelativeLayout)lyt.getChildAt(0)).setBackgroundResource(R.drawable.rounded_blue_fill_bg);
        } else if (((Order) getItem(position)).getStatusId() > 29) {
            //	drawable.setStroke(10, mContext.getResources().getColor(R.color.color_red));
            //holder.odrStatus.setTextColor(mContext.getResources().getColor(R.color.color_red));
            holder.mStatusView.setBackgroundResource(R.drawable.rounded_blue_fill_bg);// YD converted to pink
            holder.odrStatus.setTextColor(Color.parseColor("#ffffff"));
            holder.odrStatus.setTextSize(28);
            holder.odrStatus.setTypeface(Typeface.DEFAULT_BOLD);
            //((RelativeLayout)lyt.getChildAt(0)).setBackgroundResource(R.drawable.rounded_red_fill_bg);
        } else {
            holder.mStatusView.setBackgroundResource(R.drawable.round_blue_time_bg);
            holder.odrStatus.setTextColor(Color.parseColor("#ffffff"));
            holder.odrStatus.setTextSize(28);
            holder.odrStatus.setTypeface(Typeface.DEFAULT_BOLD);
        }

        //holder.odrPriority.setText(mapOdrPriority.get(((Order)getItem(position)).getPriorityTypeId()).getNm());
        holder.odrPriority.setText(String.valueOf(((Order) getItem(position)).getSequenceNumber())); // MY show order sequence instead of priority because of client requirement
        holder.odrPriority.setTextSize(24 + s);
        try {
            String textValue = "";
            if (getItem(position) != null && mapOdrStatus != null) {
                textValue = mapOdrStatus.get(((Order) getItem(position)).getStatusId()).getNm();
                if (textValue == null)
                    textValue = "";
                holder.odrStatus.setText(textValue);
                holder.odrStatus.setTextSize(28);
                holder.odrStatus.setTag(textValue);

            }
        } catch (NullPointerException e) {
            Bugsnag.notify(e);
        }
		/*holder.odrDetail.setText(String.valueOf(((Order)getItem(position)).getSummary()));
		holder.odrAlert.setText(String.valueOf(((Order)getItem(position)).getOrderAlert()));*/

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");    // returns before 12pm : Sat Jun 20 09:30:00 IST 2015 and after 12pm it returns: Sat Jun 20 23:30:00 IST 2015
            String strFromDate = ((Order) getItem(position)).getFromDate();// 2015-06-20 07:55 -00:00
            String strToDate = ((Order) getItem(position)).getToDate();

            Date start = simpleDateFormat.parse(strFromDate);
            Date end = simpleDateFormat.parse(strToDate);
            String startDt[] = Utilities.convertDateToAmPMWithout_Zero(start.getHours(), start.getMinutes()).split(" ");
            if (startDt.length > 0 && startDt[0] != null && !startDt[0].isEmpty())
                holder.odrSchduleTime.setText(startDt[0].toString());
            if (startDt.length > 0 && startDt[1] != null && !startDt[1].isEmpty())
                holder.odrSchduleType.setText(startDt[1]);
            // holder.odrSchduleTimeDiff.setText(getTextValue(end, start).toString());
            // holder.odrSchduleDiffinMin.setText("min");
            holder.odrSchduleTime.setTextSize(20 + s);
            holder.odrSchduleType.setTextSize(24 + s);
            // holder.odrSchduleTimeDiff.setTextSize(24 + s);
            //  holder.odrSchduleDiffinMin.setTextSize(24 + s);

        } catch (Exception e) {
            Bugsnag.notify(e);
            e.printStackTrace();
        }

        //	holder.msgImageView.setTag(position);
        holder.mStatusView.setTag(position);
        holder.mStatusView.setTag(R.string.tag_sch, holder.mStatusView);
        //	holder.odrStatus.setTag(position); // MY R later
        //   holder.odrStatus.setTag(R.string.tag_sch, holder.odrStatus); // MY R later
        holder.rel_sch_time.setTag(position);
        holder.rel_sch_time.setTag(R.string.tag_status, holder.rel_sch_time);
		/*if(pid==6){
			clr = "#9B59B6";
		} else if(pid==5){
			clr = "#E74C3C";
		} else if(pid==4){
			clr = "#E67E22";
		} else if(pid==3){
			clr = "#F1C40F";
		} else if(pid==2){
			clr = "#2ECC71";
		} else if(pid==1){
			clr = "#BDC3C7";
		} else if(pid==0){
			clr = "#ECF0F1";
		}*/

//		 if(((Order) getItem(position)).getPriorityTypeId() == 0)
//			holder.rel_sch_time.setBackgroundColor(Color.parseColor("#ECF0F1"));
//		else if(((Order) getItem(position)).getPriorityTypeId() == 1)
//			holder.rel_sch_time.setBackgroundColor(Color.parseColor("#BDC3C7"));
//		else if(((Order) getItem(position)).getPriorityTypeId() == 2)
//			holder.rel_sch_time.setBackgroundColor(Color.parseColor("#95A5A6"));
//		else if(((Order) getItem(position)).getPriorityTypeId() == 3)
//			holder.rel_sch_time.setBackgroundColor(Color.parseColor("#F1C40F"));
//		else if(((Order) getItem(position)).getPriorityTypeId() == 4)
//			holder.rel_sch_time.setBackgroundColor(Color.parseColor("#F39C12"));
//		else if(((Order) getItem(position)).getPriorityTypeId() == 5)
//			holder.rel_sch_time.setBackgroundColor(Color.parseColor("#E74C3C"));
//		else if(((Order) getItem(position)).getPriorityTypeId() == 6)
//			holder.rel_sch_time.setBackgroundColor(Color.parseColor("#9B59B6"));
//		else
//			holder.rel_sch_time.setBackgroundColor(Color.parseColor("#ECF0F1"));

        holder.rel_sch_time.setBackgroundResource(R.drawable.round_gray_time_bg);

        if (position == OrderDataMap.size() - 1)
            holder.order_end_view.setVisibility(View.VISIBLE);
        else
            holder.order_end_view.setVisibility(View.GONE);

        //	holder.orderDetailImageView.setTag(position);
        holder.mCallImageView.setTag(position);
        holder.mGlobeMapImageView.setTag(position);
        holder.mTreeMapImageView.setTag(position);
//		holder.mFileView.setTag(position);
        holder.mMsgImageView.setTag(position);
        holder.row_job_call_vieweform.setTag(position);
        holder.row_job_call_viewepart.setTag(position);
        holder.row_job_call_viewasset.setTag(position);

        holder.odr_lst_odr_img_customer.setTag(position);
        holder.odr_lst_odr_img_order.setTag(position);
        holder.odr_lst_odr_img_order.setTag(R.string.tag_order, holder.odr_lst_odr_img_order);
        holder.odr_lst_odr_img_alert.setTag(position);
        holder.odr_lst_odr_img_alert.setTag(R.string.tag_alert, holder.odr_lst_odr_img_alert);
		/*holder.odr_lst_odr_img_note.setTag(position);
		holder.odr_lst_odr_img_note.setTag(R.string.tag_notee, holder.odr_lst_odr_img_note);*/

        holder.mCallImageView.setOnClickListener(this);
        holder.mGlobeMapImageView.setOnClickListener(this);
        holder.mTreeMapImageView.setOnClickListener(this);
        holder.mMsgImageView.setOnClickListener(this);
        //	holder.odrStatus.setOnClickListener(this); //MY R later
//		holder.mFileView.setOnClickListener(this);
        holder.row_job_call_vieweform.setOnClickListener(this);
        holder.row_job_call_viewepart.setOnClickListener(this);
        holder.row_job_call_viewasset.setOnClickListener(this);
        holder.rel_sch_time.setOnClickListener(this);
        holder.mStatusView.setOnClickListener(this);

        holder.odr_lst_odr_img_customer.setOnClickListener(this);
        holder.odr_lst_odr_img_order.setOnClickListener(this);
        holder.odr_lst_odr_img_alert.setOnClickListener(this);
        //holder.odr_lst_odr_img_note.setOnClickListener(this);


        if (screenInches < 6.8) {
            //	holder.odrTreecount.setTextSize(30 + s);
            //	holder.odrPartcount.setTextSize(30 + s);
            //	holder.odrAssetcount.setTextSize(30 +s);


            if (listcount == 1) {
                holder.odrAssetcount.setBackgroundResource(0);
                if (PreferenceHandler.getPrefEditionForGeo(mContext) < 900) {
                    holder.odrAssetcount.setBackgroundResource(R.drawable.ord_topmenu_new);
                } else {
                    holder.odrAssetcount.setBackgroundResource(R.drawable.tree_olmp);//YD put transparent tree image here.
                }
                holder.odrTreecount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.task_olmp_blue, 0);//YD removing the image from the right of textview
                holder.odrTreecount.setBackgroundResource(0); //YD putting the image in center/background of textview

                holder.odrPartcount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.inven_new_orange, 0);
                holder.odrPartcount.setBackgroundResource(0);
            }
			/*if(listcount>2) {
				holder.odrTreecount.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);//YD removing the image from the right of textview
				holder.odrTreecount.setBackgroundResource(R.drawable.task_trans); //YD putting the image in center/background of textview

				holder.odrPartcount.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				holder.odrPartcount.setBackgroundResource(R.drawable.inventory_trans);

				holder.odrAssetcount.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				if (PreferenceHandler.getPrefEditionForGeo(mContext) < 900) {
					holder.odrAssetcount.setBackgroundResource(R.drawable.ord_topmenu_new_small);
				} else {
					holder.odrAssetcount.setBackgroundResource(R.drawable.tree_trans);//YD put transparent tree image here.
				}
			}*///YD 2020 removing back ground images

			/*holder.odrSchduleType.setVisibility(View.GONE);
			holder.odrSchduleDiffinMin.setVisibility(View.GONE);*///YD using to hide am pm and mins
			/*holder.odrSchduleType.setTextSize(12);
			holder.odrSchduleDiffinMin.setTextSize(12);

			holder.odrSchduleTime.setTextSize(14);
			holder.odrSchduleTimeDiff.setTextSize(14);

			if (holder.odrSchduleTimeDiff.getText().length()>2){ //YD if for eg mins are 340
				holder.odrSchduleTime.setTextSize(12);
				holder.odrSchduleTimeDiff.setTextSize(12);
			}*/// YD commenting because now time and duration is not in the box
            try {
                String textValue = "";
                if (getItem(position) != null && mapOdrStatus != null) {
                    if (listcount >= 2) {//kb
                        if (getAbbreviationText(((Order) getItem(position)).getStatusId()) != null) {
                            textValue = mapOdrStatus.get(((Order) getItem(position)).getStatusId()).getNm();
                            if (textValue == null)
                                textValue = "";
                            holder.odrStatus.setText(textValue);
                        }
                    } else {
                        if (getAbbreviationText(((Order) getItem(position)).getStatusId()) != null) {
                            textValue = mapOdrStatus.get(((Order) getItem(position)).getStatusId()).getNm();
                            if (textValue == null)
                                textValue = "";
                            holder.odrStatus.setText(textValue);
                        }
                    }
                    if (getAbbreviationText(((Order) getItem(position)).getStatusId()) != null) {
                        textValue = mapOdrStatus.get(((Order) getItem(position)).getStatusId()).getNm();
                        if (textValue == null)
                            textValue = "";
                        holder.odrStatus.setTag(textValue);
                    }

                }
            } catch (Exception e) {
                Bugsnag.notify(e);
                e.printStackTrace();
            }
        } else {
            if (PreferenceHandler.getPrefEditionForGeo(mContext) >= 900) {
                holder.odrAssetcount.setBackgroundResource(0);
                holder.odrAssetcount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tree_olmp, 0);
            }
        }

        return vi;
    }


    private void sortodrPriorty(HashMap<Long, OrderPriority> odrPriorityListTemp) {
      /*  long[] x = {0,1,4,5,6};

        for (int i=0; i<x.length;i++){
            mapOdrPriority.put(x[i], odrPriorityListTemp.get(x[i]));
        }*/

        long y = 0;
        for (int i = 0; i < odrPriorityListTemp.size(); ) {
            OrderPriority obj = odrPriorityListTemp.get(y);
            if (obj != null) {
                mapOdrPriority1.put(y, odrPriorityListTemp.get(y));
                i++;
                y++;
            } else {
                y++;
            }
        }
    }

    private String getAbbreviationText(long statusId) {
        if (mapOdrStatus != null && mapOdrStatus.get(statusId) != null && mapOdrStatus.get(statusId).getAbbrevation() != null) {
            return mapOdrStatus.get(statusId).getAbbrevation();
        } else
            return "";
    }

    public void setDistanceText(long id, long prev_id, double distance) {
        //String timeD= "" + (int) (skRouteInfo.getEstimatedTime()/60) + " Mins, " + (skRouteInfo.getDistance()/1760) + " Miles";
        String timeD = "" + (int) (distance / speed * 60) + " Mins";
        OrderDataMap.get(id).setDistanceAndTime(timeD);
        OrderDataMap.get(id).setDistanceFmOrder(prev_id);
        OrderDataMap.get(id).setTimeFmOrder(Math.round(distance / speed * 60 * 60 * 1000));
        TextView view = OrderDisTextViewList.get(id);
        if (view != null) {
            view.setText(timeD);
            view.invalidate();
        }
    }

    /**
     * @param v
     */

    @Override
    public void onClick(View v) {
        int poss = (Integer) v.getTag();
        switch (v.getId()) {

            case R.id.row_job_msg_view: //YD R Done


                Gridview_MainActivity gridView_MainFragAudio = new Gridview_MainActivity();
                Bundle b = new Bundle();
                b.putString("OrderId", String.valueOf(((Order) getItem(poss)).getId()));
                //b.putString("HeaderText", String.valueOf("AUDIOS"));
                b.putString("HeaderText", String.valueOf(PreferenceHandler.getAudioHead(mContext)));
                b.putString("OrderName", String.valueOf(((Order) getItem(poss)).getNm()));
                b.putInt("fmetaType", 3);
                gridView_MainFragAudio.setActiveOrderObject(OrderDataMap.get(((Order) getItem(poss)).getId()));
                gridView_MainFragAudio.setArguments(b);
                mActivity.pushFragments(Utilities.JOBS, gridView_MainFragAudio, true, true, BaseTabActivity.UI_Thread);
                break;

            //	case R.id.row_job_schd_txtvw:
            case R.id.odr_lst_odr_lyt_view_status:
                //	showSchedulePopupList((TextView) v.getTag(R.string.tag_sch), (LinearLayout) v.getTag(R.string.tag_sch));
                //showSchedulePopupList((LinearLayout) v.getTag(R.string.tag_sch));

//			PreferenceHandler.setPrefLstoid(mActivity, String.valueOf(((Order) getItem(poss)).getId()));
//			PreferenceHandler.setPrefNxtoid(mActivity, String.valueOf(((Order) getItem(poss + 1)).getId()));

                if (SplashII.wrk_tid >= 9)
                    break;

                if (v.getTag(R.string.tag_sch) != null)
                    showStatusDialog((LinearLayout) v.getTag(R.string.tag_sch));
                break;

            case R.id.row_job_call_view://YD R DONE
//                throw new RuntimeException("This is a crash YD123");
                Gridview_MainActivity gridView_MainFrag = new Gridview_MainActivity();
                gridView_MainFrag.setActiveOrderObject(OrderDataMap.get(((Order) getItem(poss)).getId()));

                Bundle ba = new Bundle();
                ba.putString("OrderId", String.valueOf(((Order) getItem(poss)).getId()));
                //ba.putString("HeaderText", String.valueOf("PICTURES"));
                ba.putString("HeaderText", PreferenceHandler.getPictureHead(mContext));
                ba.putString("OrderName", String.valueOf(((Order) getItem(poss)).getNm()));
                ba.putInt("fmetaType", 1);

                gridView_MainFrag.setArguments(ba);
                mActivity.pushFragments(Utilities.JOBS, gridView_MainFrag, true, true, BaseTabActivity.UI_Thread);
                break;

            case R.id.row_job_globemap_view:    //YD R still needed
                Utilities.log(mContext, "open google Navigation for orderId:" + ((Order) getItem(poss)).getId());
                String geoArray[] = ((Order) getItem(poss)).getCustSiteGeocode().split(",");
                String startLat = geoArray[0];
                String startLon = geoArray[1];
                if ((startLat != null && startLon != null) && !geoArray[0].equals("") && !geoArray[1].equals(""))
                    openGoogleNavigation(Double.parseDouble(geoArray[0]), Double.parseDouble(geoArray[1]));
                else
                    openGoogleNavigation(0.0, 0.0);

                if (!(PreferenceHandler.getClockInStat(mActivity) != null && PreferenceHandler.getClockInStat(mActivity).equals(CheckinOut.CLOCK_IN))) {
                    // Request for checkin
                    ClockInOutRequest clkInOut = new ClockInOutRequest();
                    clkInOut.setTid("1");
                    clkInOut.setType("post");
                    clkInOut.setAction(CheckinOut.CC_ACTION);

                    CheckinOut.getData(clkInOut, mActivity, OrderListAdapter.this, CHECKIN_REQ);
                }
                break;

            case R.id.row_job_treemap_view:
                // YD using to open signature
                Gridview_MainActivity gridView_MainFragSig = new Gridview_MainActivity();
                gridView_MainFragSig.setActiveOrderObject(OrderDataMap.get(((Order) getItem(poss)).getId()));

                Bundle bundl = new Bundle();
                bundl.putString("OrderId", String.valueOf(((Order) getItem(poss)).getId()));
                //bundl.putString("HeaderText", String.valueOf("SIGNATURES"));
                bundl.putString("HeaderText", String.valueOf(PreferenceHandler.getSignatureHead(mContext) == null ? "SIGNATURES" : PreferenceHandler.getSignatureHead(mContext)));
                bundl.putString("OrderName", String.valueOf(((Order) getItem(poss)).getNm()));
                bundl.putInt("fmetaType", 2);

                gridView_MainFragSig.setArguments(bundl);
                mActivity.pushFragments(Utilities.JOBS, gridView_MainFragSig, true, true, BaseTabActivity.UI_Thread);
                break;



		/*case R.id.row_job_File_view:
			Gridview_MainActivity gridView_MainFragFile = new Gridview_MainActivity();
			gridView_MainFragFile.setActiveOrderObject(OrderDataMap.get(((Order)getItem(poss)).getId()));
			Bundle bp = new Bundle();
			bp.putString("OrderId", String.valueOf(((Order) getItem(poss)).getId()));
			//bp.putString("HeaderText", String.valueOf("FILES"));
			bp.putString("HeaderText", String.valueOf(PreferenceHandler.getFileHead(mContext)==null?"FILES":PreferenceHandler.getFileHead(mContext)));
			bp.putString("OrderName", String.valueOf(((Order) getItem(poss)).getNm()));
			bp.putInt("fmetaType", 4);
			gridView_MainFragFile.setArguments(bp);
			mActivity.pushFragments(Utilities.JOBS, gridView_MainFragFile, true, true,BaseTabActivity.UI_Thread);
			break;*/


            case R.id.row_job_call_vieweform:
                try {
                    //YD Using this for non- pge application
                    Order odrObjForTask = OrderDataMap.get(((Order) getItem(poss)).getId());
                    OrderFormsFragment orderDetailTreeFragment = new OrderFormsFragment();
                    Bundle taskBundle = new Bundle();
                    taskBundle.putString("OrderId", String.valueOf(odrObjForTask.getId()));
                    taskBundle.putString("OrderName", String.valueOf(odrObjForTask.getNm()));
                    taskBundle.putInt("Position", poss);
                    orderDetailTreeFragment.setArguments(taskBundle);
                    orderDetailTreeFragment.setActiveOrderObj(odrObjForTask);
                    mActivity.pushFragments(Utilities.JOBS, orderDetailTreeFragment, true, true, BaseTabActivity.UI_Thread);
                } catch (Exception e) {
                    Bugsnag.notify(e);
                }


                //YD using this for pge application
		/*if(DataObject.ordersXmlDataStore!=null){
			Order odrObj = OrderDataMap.get(((Order)getItem(poss)).getId());

			Bundle mBundles =new Bundle();
			mBundles.putString("OrderId", String.valueOf(odrObj.getId()));
			mBundles.putString("OrderName", String.valueOf(odrObj.getNm()));

			if(Utilities.checkInternetConnection(mActivity,false)  &&  new SpeedTestLauncher().bindListeners()>20){
	            GoogleMapFragment.maptype = "TreeList";


	            GoogleMapFragment GooglemapFragment = new GoogleMapFragment();
				GooglemapFragment.setArguments(mBundles);
				GooglemapFragment.setActiveOrderObject(odrObj);
				mActivity.pushFragments(Utilities.JOBS, GooglemapFragment, true, true,BaseTabActivity.UI_Thread);
			}else {
				MapAllFragment.maptype = "TreeList";

				MapAllFragment mFragment = new MapAllFragment();
				mFragment.setArguments(mBundles);
				mFragment.setActiveOrderObject(odrObj);
				mActivity.pushFragments(Utilities.JOBS, mFragment, true, true,BaseTabActivity.UI_Thread);
			}
		}*/
                break;
            case R.id.row_job_call_viewepart:
                Order odrObjForpart = OrderDataMap.get(((Order) getItem(poss)).getId());
                PreferenceHandler.setPrefCtid(mContext, odrObjForpart.getCustids());

                OrderPartsFragment orderDetailMaterialFragment = new OrderPartsFragment();
                Bundle partsBundle = new Bundle();
                partsBundle.putString("OrderId", String.valueOf(((Order) getItem(poss)).getId()));
                //partsBundle.putString("HeaderText", String.valueOf("PARTS"));
                partsBundle.putString("HeaderText", String.valueOf(PreferenceHandler.getPartHead(mContext)).toUpperCase());
                partsBundle.putString("OrderName", String.valueOf(((Order) getItem(poss)).getNm()));
                orderDetailMaterialFragment.setArguments(partsBundle);
                orderDetailMaterialFragment.setActiveOrderObject(odrObjForpart);
                mActivity.pushFragments(Utilities.JOBS, orderDetailMaterialFragment, true, true, BaseTabActivity.UI_Thread);
                break;

            case R.id.row_job_call_viewasset:
                Order odrObjForAsset = OrderDataMap.get(((Order) getItem(poss)).getId());

                if (PreferenceHandler.getPrefEditionForGeo(mContext) >= 600) {
                    if (DataObject.ordersXmlDataStore != null) {
                        Order odrObj = OrderDataMap.get(((Order) getItem(poss)).getId());

                        Bundle mBundles = new Bundle();
                        mBundles.putString("OrderId", String.valueOf(odrObj.getId()));
                        mBundles.putString("OrderName", String.valueOf(odrObj.getNm()));

                        if (Utilities.checkInternetConnection(mActivity, false)  /*&&  new SpeedTestLauncher().bindListeners()>20*/) {
                            GoogleMapFragment.maptype = "TreeList";

                            GoogleMapFragment GooglemapFragment = new GoogleMapFragment();
                            GooglemapFragment.setArguments(mBundles);
                            GooglemapFragment.setActiveOrderObject(odrObj);
                            mActivity.pushFragments(Utilities.JOBS, GooglemapFragment, true, true, BaseTabActivity.UI_Thread);
                        } else {
					/*	MapAllFragment.maptype = "TreeList";

						MapAllFragment mFragment = new MapAllFragment();
						mFragment.setArguments(mBundles);
						mFragment.setActiveOrderObject(odrObj);
						mActivity.pushFragments(Utilities.JOBS, mFragment, true, true,BaseTabActivity.UI_Thread);
					*/

                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
                            alertDialogBuilder.setMessage("No Internet Connection");
                            alertDialogBuilder.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            arg0.dismiss();
                                        }
                                    });


                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();


                        }//YD 2020
                    }
                } else {
                    String headerText = PreferenceHandler.getAssetsHead(mActivity);//mBundle.getString("HeaderText");
                    if (headerText != null && !headerText.equals(""))
                        headerText = headerText + "S";
                    else
                        headerText = "ASSETS";// YD just for backup

                    OrderAssetFragment assetFragment = new OrderAssetFragment();
                    Bundle assetBundle = new Bundle();
                    assetBundle.putString("OrderId", String.valueOf(((Order) getItem(poss)).getId()));
                    assetBundle.putString("HeaderText", String.valueOf(headerText));
                    assetBundle.putString("OrderName", String.valueOf(((Order) getItem(poss)).getNm()));
                    assetFragment.setArguments(assetBundle);
                    assetFragment.setActiveOrderObject(odrObjForAsset);
                    mActivity.pushFragments(Utilities.JOBS, assetFragment, true, true, BaseTabActivity.UI_Thread);
                }
                break;

            case R.id.tme_above:
                if (true) return;
                selectedOrderPosition = poss;

                orderFromDate = ((Order) getItem(poss)).getFromDate();//2015-06-21 4:30 -00:00
                orderToDate = ((Order) getItem(poss)).getToDate();   //2015-06-21 5:00 -00:00
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");

                try {
                    gridStartDate = simpleDateFormat.parse(orderFromDate);
                    gridEndDate = simpleDateFormat.parse(orderToDate);      //Sun Jun 21 10:30:00 IST 2015
                } catch (ParseException e1) {
                    Bugsnag.notify(e1);
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }  //Sun Jun 21 10:00:00 IST 2015

                String stDate = null,
                        stTime = null,
                        edDate = null,
                        edTime = null;
                try {
				/*HashMap<Long, Order> odrDataMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;			
				for (Entry<Long, Order> entry : odrDataMap.entrySet()) {			
					Order mOrder = entry.getValue();			
					if(String.valueOf(mOrder.getId()).equals(((Order)getItem(poss)).getId()))
					{			
						orderFromDate = mOrder.getFromDate();//2015-06-21 4:30 -00:00
						orderToDate = mOrder.getToDate();   //2015-06-21 5:00 -00:00					
					}
				}	*/

                    SimpleDateFormat simpleDateFrmt = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");

                    Date startDate = simpleDateFrmt.parse(orderFromDate);  //Sun Jun 21 10:00:00 GMT+05:30 2015
                    Date endDate = simpleDateFrmt.parse(orderToDate);      //Sun Jun 21 10:30:00 GMT+05:30 2015

                    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd-MM-yyyy");//Jun 21 2015
                    stDate = simpleDateFormat2.format(startDate);
                    edDate = simpleDateFormat2.format(endDate);

                    stTime = Utilities.convertDateToAmPM(startDate.getHours(), startDate.getMinutes());
                    edTime = Utilities.convertDateToAmPM(endDate.getHours(), endDate.getMinutes());

                    openAlertDialog((RelativeLayout) v.getTag(R.string.tag_status), stDate, stTime, edDate, edTime);
                } catch (Exception e) {
                    Bugsnag.notify(e);
                    e.printStackTrace();
                }
                break;


            case R.id.odr_lst_odr_img_customer:
                if (DataObject.ordersXmlDataStore != null) {
                    Order odrObj = OrderDataMap.get(((Order) getItem(poss)).getId());
                    CustomerDetailFragment detailFragment = new CustomerDetailFragment();
                    Bundle mBundle = new Bundle();
                    mBundle.putString("OrderId", String.valueOf(odrObj.getId()));
                    mBundle.putString("HeaderText", String.valueOf("Directory"));
                    mBundle.putString("OrderName", String.valueOf(odrObj.getNm()));
                    detailFragment.setArguments(mBundle);
                    detailFragment.setActiveOrderObj(odrObj);
                    mActivity.pushFragments(Utilities.JOBS, detailFragment, true, true, BaseTabActivity.UI_Thread);
                }
                break;

            case R.id.odr_lst_odr_img_order:
			/*selectedOrderPosition = poss;
			ImageView imgView = (ImageView) v.getTag(R.string.tag_order);
			LinearLayout lynlyt = (LinearLayout) imgView.getParent();
			LinearLayout lytVwMain = (LinearLayout) lynlyt.getChildAt(1);
			
		//	txtViewOrderName = (TextView) lytVwMain.getChildAt(0); // Order Name
			txtViewOrderType = (TextView) lytVwMain.getChildAt(1); // Order Type
			txtViewOrderSepra = (TextView) ((LinearLayout) lytVwMain.getChildAt(2)).getChildAt(1); // Order Description
			txtViewOrderDesc = (TextView) ((LinearLayout) lytVwMain.getChildAt(2)).getChildAt(2); // Order Description
			
			previousTypeID  = String.valueOf(((Order) getItem(poss)).getOrderTypeId());
			String typeNm ="";
			if (((Order)getItem(poss)).getOrderTypeId()>0  && mapOrderType!=null)
				typeNm = mapOrderType.get(((Order)getItem(poss)).getOrderTypeId()).getNm();
			previousCatName = typeNm;
			
			previousPriorityId = String.valueOf(((Order) getItem(poss)).getPriorityTypeId());
			previousPriorityName = String.valueOf(mapOdrPriority.get(((Order) getItem(poss)).getPriorityTypeId()).getNm());
			
			previousDesc = ((Order)getItem(poss)).getSummary();
			previousPoVal = ((Order)getItem(poss)).getPoNumber();
			previousInvNm = ((Order)getItem(poss)).getInvoiceNumber(); 
			previousAlert = ((Order)getItem(poss)).getOrderAlert();
			
			showCategoryDialog(previousTypeID, previousCatName, previousPriorityId, previousPriorityName,previousDesc,previousPoVal,previousInvNm,previousAlert,"Edit Order Information", 0);
*/

                Order odrObj = OrderDataMap.get(((Order) getItem(poss)).getId());

                OrderDetailFrag odrdetailFragment = new OrderDetailFrag();
			/*Bundle mBundle=new Bundle();
			mBundle.putString("OrderId", String.valueOf(odrObj.getId()));
			mBundle.putString("HeaderText", String.valueOf("LOCATIONS & CONTACTS"));
			mBundle.putString("OrderName", String.valueOf(odrObj.getNm()));
			odrdetailFragment.setArguments(mBundle);*/
                odrdetailFragment.setActiveOrderObj(odrObj);
                mActivity.pushFragments(Utilities.JOBS, odrdetailFragment, true, true, BaseTabActivity.UI_Thread);

                break;

            case R.id.odr_lst_odr_img_alert:
                selectedOrderPosition = poss;

                String previousAlert = ((Order) getItem(poss)).getOrderAlert().toString();
			
			/*ImageView imgVw = (ImageView) v.getTag(R.string.tag_alert);
			LinearLayout lyt = (LinearLayout) imgVw.getParent();
			LinearLayout lytVw = (LinearLayout) lyt.getChildAt(1);
			txtViewAlert = (TextView) lytVw.getChildAt(0);
			
			showCategoryDialog("","","","","","","", previousAlert, "Edit Alert", 1);*/

                HashMap<Long, OrderNotes> notesMap = (HashMap<Long, OrderNotes>) DataObject.orderNoteDataStore;
                String previousNote;
                if (notesMap != null) {
                    OrderNotes noteObj = notesMap.get(((Order) getItem(poss)).getId());

                    if (noteObj != null)
                        previousNote = noteObj.getOrdernote();
                    else
                        previousNote = "";
                } else
                    previousNote = "";

                AlertnNoteFrag altNoteFrag = new AlertnNoteFrag();

                Bundle altNotBd = new Bundle();
                altNotBd.putString("PREVIOUS_NOTE", previousNote);
                altNotBd.putString("PREVIOUS_ALERT", previousAlert);
                altNotBd.putString("PREVIOUS_DTL", ((Order) getItem(poss)).getSummary().toString());
                altNotBd.putString("PREVIOUS_PO", ((Order) getItem(poss)).getPoNumber().toString());
                altNotBd.putString("PREVIOUS_ODO", ((Order) getItem(poss)).getInvoiceNumber().toString());
                altNoteFrag.setArguments(altNotBd);
                altNoteFrag.setActiveOrderObject(OrderDataMap.get(((Order) getItem(poss)).getId()));

                mActivity.pushFragments(Utilities.JOBS, altNoteFrag, true, true, BaseTabActivity.UI_Thread);
                break;
/*
		case R.id.odr_lst_odr_img_note:

			selectedOrderPosition = poss;

			HashMap<Long , OrderNotes> notesMap = (HashMap<Long , OrderNotes>)DataObject.orderNoteDataStore;
			OrderNotes noteObj = notesMap.get(((Order) getItem(poss)).getId());
			String previousNote;
			if (noteObj!=null)
				previousNote = noteObj.getOrdernote();
			else
				previousNote = "";


			ImageView imgVw1 = (ImageView) v.getTag(R.string.tag_notee);
			LinearLayout lyt1 = (LinearLayout) imgVw1.getParent();
			LinearLayout lytVw1 = (LinearLayout) lyt1.getChildAt(1);
			txtViewNote = (TextView) lytVw1.getChildAt(0); //YD making it global because at the end we have to change the text when update to note occur

			this.previousNote = previousNote;
			showCategoryDialog("","","","","","","", previousNote, "Edit Note", 2);
			break;*/

            default:
                break;
        }
    }


    private void openAlertDialog(RelativeLayout lytTimeStatus, final String stDate, String stTime, String edDate, String edTime) {
        final RelativeLayout lytVwTimeStatus = lytTimeStatus;
        final int orderPos = (Integer) lytVwTimeStatus.getTag();

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialog = inflater.inflate(R.layout.dialoge_time_calender, null);
        builder.setTitle("Set Date, Time and Duration");
        builder.setView(dialog);

        mTxtVwStartDT = (TextView) dialog.findViewById(R.id.dialoge_strt_tmdt_txtvw);
        mTxtVwEndDT = (TextView) dialog.findViewById(R.id.dialoge_end_tmdt_txtvw);

        mTxtStrtCal = (TextView) dialog.findViewById(R.id.dialoge_strt_cal_txtvw);

        mTxtVwEndCal = (TextView) dialog.findViewById(R.id.dialoge_end_cal_txtvw);

        mBtnTime = (Button) dialog.findViewById(R.id.dialog_time_btn);
        mBtnTime.setVisibility(dialog.VISIBLE);

        mBtnCalender = (Button) dialog.findViewById(R.id.dialog_cal_btn);
        mStartTimeBg = (LinearLayout) dialog.findViewById(R.id.start_time_lnrlyt);
        mEndTimeBg = (LinearLayout) dialog.findViewById(R.id.end_time_lnrlyt);
        mStartTimeBg.setBackgroundColor(mActivity.getResources().getColor(R.color.color_light_gray));
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
                mStartTimeBg.setBackgroundColor(mActivity.getResources().getColor(R.color.color_light_gray));
                mStartTimeBg.setTag(1);
                mEndTimeBg.setBackgroundResource(0);
                mEndTimeBg.setTag(0);
            }
        });
        mEndTimeBg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mEndTimeBg.setBackgroundColor(mActivity.getResources().getColor(R.color.color_light_gray));
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

                if (mStartTimeBg.getTag().equals(1))
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

                CustomTimePickerDialog dialog = new CustomTimePickerDialog(mActivity, new CustomTimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        // TODO Auto-generated method stub
                        String selMinute = "", selHour = "", am_pm = "";
                        Calendar mcurrentTime = Calendar.getInstance();

                        if (selectedMinute < 10)
                            selMinute = "0" + selectedMinute;
                        else
                            selMinute = selectedMinute + "";

                        if (selectedHour < 10)
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

                        if (Integer.valueOf(strHrsToShow) < 10)
                            strHrsToShow = "0" + strHrsToShow;

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

                }, hour, minute, false, sizeDialogStyleID);

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

                if (mStartTimeBg.getTag().equals(1))
                    lnr = (LinearLayout) mStartTimeBg.getChildAt(1);
                else
                    lnr = (LinearLayout) mEndTimeBg.getChildAt(1);

                int mYear = Integer.valueOf(((TextView) lnr.getChildAt(0)).getTag().toString().split("-")[2]);
                int mMonth = Integer.valueOf(((TextView) lnr.getChildAt(0)).getTag().toString().split("-")[1]) - 1;
                int mDay = Integer.valueOf(((TextView) lnr.getChildAt(0)).getTag().toString().split("-")[0]);

                int sizeDialogStyleID = Utilities.getDialogTextSize(mActivity);

                MyDatePickerDialog dialog = new MyDatePickerDialog(mActivity, new mDateSetListener(), mYear, mMonth, mDay, false, OrderListAdapter.this, sizeDialogStyleID);
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
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String sttime = (String) mTxtVwStartDT.getText();  // Start Time:  10:03 am
                    if (sttime.split(" ")[1].equals("pm")) {
                        int time = 0;
                        if (Integer.valueOf(sttime.split(":")[0]) == 12)
                            time = Integer.valueOf(sttime.split(":")[0]);
                        else
                            time = Integer.valueOf(sttime.split(":")[0]) + 12;
                        sttime = String.valueOf(time) + ":" + sttime.split(":")[1];
                    }

                    String edtime = (String) mTxtVwEndDT.getText(); //  End Time :10:35 am
                    if (edtime.split(" ")[1].equals("pm")) {
                        int time = 0;
                        if (Integer.valueOf(edtime.split(":")[0]) == 12)
                            time = Integer.valueOf(edtime.split(":")[0]);
                        else
                            time = Integer.valueOf(edtime.split(":")[0]) + 12;   //YD TODO CHECK IF TIME 12:00
                        edtime = String.valueOf(time) + ":" + edtime.split(":")[1];
                    }

                    String startDateStr = mTxtStrtCal.getTag() + " " + sttime;//21-06-2015 10:03 am
                    String startEndStr = mTxtVwEndCal.getTag() + " " + edtime;//21-06-2015 10:35 am

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

                    Date stDate = simpleDateFormat.parse(startDateStr);//Sun Jun 21 10:05:00 GMT+05:30 2015
                    Date endDate = simpleDateFormat.parse(startEndStr);//Mon Jun 22 10:35:00 GMT+05:30 2015

                    handleDateRangechange(orderPos, stDate, endDate);//YD making call to save the changed time

                    //mandeep TODO setting up the date in the grid_cal UI
                    long finalDiffInMin = (endDate.getTime() - stDate.getTime()) / 60000;
                    if (finalDiffInMin >= 5) {
                        String minToShow = String.valueOf((endDate.getTime() - stDate.getTime()) / 60000);
                        String stEdToShow[] = Utilities.convertDateToAmPMWithout_Zero(stDate.getHours(), stDate.getMinutes()).split(" ");

                        TextView txtSTime = (TextView) lytVwTimeStatus.getChildAt(0); // Time
                        txtSTime.setText(stEdToShow[0]);
                        TextView txtAMPM = (TextView) lytVwTimeStatus.getChildAt(1);
                        txtAMPM.setText(stEdToShow[1]);        // AMPM
                        TextView txtdiff = (TextView) lytVwTimeStatus.getChildAt(2);
                        txtdiff.setText(minToShow);    // difference

                        dialog.dismiss();
                    } else {
                        showMessageDialog("Start date/time should be less than end date/time");
                    }
                } catch (Exception e) {
                    Bugsnag.notify(e);
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


    class mDateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
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


    public void handleDateRangechange(int position, Date startDate, Date endDate)  // on startDate: Tue Jun 22 10:05:00 GMT+05:30 2015
    {                                                                 // on endDate  : Wed Jun 23 10:35:00 GMT+05:30 2015
        long finalDiffInMin = (endDate.getTime() - startDate.getTime()) / 60000;

        if (startDate.equals(gridStartDate)) {
            if (endDate.equals(gridEndDate))
                return;
        }
        if (finalDiffInMin >= 5) {

            String startDateUtc = convertDateToUtc(startDate.getTime());//2015/06/21 04:35 -00:00  // server Date(coming this way)
            String endDateUtc = convertDateToUtc(endDate.getTime());    //2015/06/21 05:00 -00:00

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm Z");
            Date stUtc;
            try {
                stUtc = simpleDateFormat.parse(startDateUtc);

                Date edUtc = simpleDateFormat.parse(endDateUtc);

                String orderStartTime = String.valueOf(stUtc.getTime());
                String orderEndTime = String.valueOf(edUtc.getTime());

                String action = "saveorderfld";
                String fieldToUpdate = "order_time";

                finalDateStr = orderStartTime + "," + orderEndTime + "," + startDateUtc + "," + endDateUtc;  //dateStr = "1377947100000,1380588000000,2013/08/31 11:05 -00:00,2013/10/01 0:40 -00:00", orderStartTime = 1377947100000, orderEndTime = 1380588000000, startD

                Long orderId = ((Order) getItem(position)).getId(); // MY selected Order Id

                updateOrderRequest req = new updateOrderRequest();
                req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(mContext) + "/mobi");
                req.setType("post");
                req.setId(String.valueOf(orderId));
                req.setName(fieldToUpdate);
                req.setValue(finalDateStr);
                req.setAction(Order.ACTION_SAVE_ORDER_FLD);

                Order.getData(req, mActivity, OrderListAdapter.this, SAVE_ORDER_TIME);

            } catch (ParseException e) {
                Bugsnag.notify(e);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //open alert dialog to show that the order time difference between start and end time should be atleast 5 mins
        }
    }

    private String changeDateFormat(String date) {
        String newDate = "";
        SimpleDateFormat input = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat output = new SimpleDateFormat("MMM  dd, yyyy");
        try {
            Date vDate = input.parse(date);                 // parse input
            newDate = output.format(vDate);    // format output
        } catch (ParseException e) {
            Bugsnag.notify(e);
            e.printStackTrace();
        }
        return newDate;
    }


    private String convertDateToUtc(long milliseconds) {
        Date date = new Date(milliseconds);

        SimpleDateFormat convStrToDate = new SimpleDateFormat("yyyy/MM/dd HH:mm");//have to send "2015/06/02 11:25 -00:00"
        convStrToDate.setTimeZone(TimeZone.getTimeZone("UTC"));

        String dateToSend = convStrToDate.format(date);
        dateToSend = dateToSend + " -00:00";
        return dateToSend;
    }

    private void showMessageDialog(String strMsg) {
        try {
            String D_title = mActivity.getResources().getString(R.string.msg_slight_problem);
            String D_desc = strMsg;
            dialog = new MyDialog(mActivity, D_title, D_desc, "OK");
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
        } catch (Exception e) {
            Bugsnag.notify(e);
            e.printStackTrace();
        }
    }


    private void showCategoryDialog(String catId, String catNm, String pId, String pName, String desc, String ssd, String route, String alert, String title, final int typeId) {
        try {
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
            final TextView txt_category = (TextView) promptsView.findViewById(R.id.category_type);
            final TextView txt_Priority = (TextView) promptsView.findViewById(R.id.category_priority);
            final EditText edt_alert = (EditText) promptsView.findViewById(R.id.edt_order_alert);

            if (typeId == 1 || typeId == 2) {//YD using this for when showing only alert edittext
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
                    if (mapOrderType != null) {
                        mTypeArryList = new ArrayList<String>();
                        catkeys = mapOrderType.keySet().toArray(new Long[mapOrderType.size()]);

                        if (mTypeArryList.size() < 1) {
                            for (int i = 0; i < mapOrderType.size(); i++) {
                                OrderTypeList odrType = mapOrderType.get(catkeys[i]);
                                mTypeArryList.add(String.valueOf(odrType.getNm()));
                            }
                        }
                        showDialogCategory(mTypeArryList, txt_category, "Category");
                    } else
                        Toast.makeText(mContext, "No orderType Available", Toast.LENGTH_SHORT).show();

                }

            });

            edt_description.setText(desc);
            edt_ssd.setText(route);
            edt_route.setText(ssd);
            edt_alert.setText(alert);
            // set dialog message
            alertDialogBuilder.setCancelable(false)
                    .setTitle(title)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

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

            if (typeId == 1 || typeId == 2)
                Utilities.setDividerTitleColor(alertDialog, 320, mActivity);
            else
                Utilities.setDividerTitleColor(alertDialog, 700, mActivity);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alertDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            alertDialog.getWindow().setAttributes(lp);

            Button button_negative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            Utilities.setDefaultFont_12(button_negative);
            Button button_positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            final AlertDialog alertDialognew = alertDialog;
            button_positive.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (typeId == 0) {

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

                        if (desc.length() > 200) {
                            showErrorDialog("Description should be less than 200 characters");
                            return;
                        }

                        // YD Doing this for checking difference because in previous if it is blank then i keep " " in it.
                        if (ssd.equals(""))
                            ssd = " ";
                        if (desc.equals(""))
                            desc = " ";
                        if (route.equals(""))
                            route = " ";

                        String key = "TypeID|PoVal|descript|inv";
                        String oldValStr = previousPriorityId + "|" + previousTypeID + "|" + previousPoVal + "|" + previousDesc + "|" + previousInvNm + "|" + "-";
                        String newValStr = priorityId + "|" + categoryId + "|" + route + "|" + desc + "|" + ssd + "|" + "-";

                        boolean isDifferent = false;
                        String[] oldValStrSplit = oldValStr.split("\\|");
                        String[] newValStrSplit = newValStr.split("\\|");

                        for (int i = 0; i < newValStrSplit.length; i++) {
                            if (!(oldValStrSplit[i].equals(newValStrSplit[i]))) {
                                isDifferent = true;
                                break;
                            }
                        }
                        if (isDifferent) {
                            Long orderId = ((Order) getItem(selectedOrderPosition)).getId(); // MY selected Order Id

                            updateOrderRequest req = new updateOrderRequest();
                            req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(mContext) + "/mobi");
                            req.setType("post");
                            req.setId(String.valueOf(orderId));
                            req.setName(key);
                            req.setValue(newValStr);
                            req.setAction(Order.ACTION_SAVE_ORDER_FLD);

                            Order.saveOrderField(req, mActivity, OrderListAdapter.this, SAVEORDERFIELD_STATUS_CATEGORY);// YD saving to data base
                        }
                        alertDialognew.dismiss();
                    } else if (typeId == 1) {
                        String alert = edt_alert.getText().toString();
                        newAlert = alert;

                        if (alert.length() > 150) {
                            showErrorDialog("Alert should be less than 150 characters");
                            //return;
                        } else if (!previousAlert.equals(newAlert)) {
                            Long orderId = ((Order) getItem(selectedOrderPosition)).getId(); // MY selected Order Id

                            updateOrderRequest req = new updateOrderRequest();
                            req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(mContext) + "/mobi");
                            req.setType("post");
                            req.setId(String.valueOf(orderId));
                            req.setName("alt");
                            req.setValue(alert);
                            req.setAction(Order.ACTION_SAVE_ORDER_FLD);

                            Order.saveOrderField(req, mActivity, OrderListAdapter.this, SAVEORDERFIELD_STATUS_ALERT);// YD saving to data base
                            alertDialognew.dismiss();
                        }
                    } else if (typeId == 2) {
                        String note = edt_alert.getText().toString();
                        newNote = note;

                        if (!previousNote.equals(newNote)) {
                            Long orderId = ((Order) getItem(selectedOrderPosition)).getId(); // MY selected Order Id

                            updateOrderRequest req = new updateOrderRequest();
                            req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(mContext) + "/mobi");
                            req.setType("post");
                            req.setId(String.valueOf(orderId));
                            req.setName("note");
                            req.setValue(note);
                            req.setAction(Order.ACTION_SAVE_ORDER_FLD);

                            OrderNotes.saveData(req, mActivity, OrderListAdapter.this, SAVENOTE);
                            alertDialognew.dismiss();
                        }
                    }
                }
            });
            Utilities.setDefaultFont_12(button_positive);
        } catch (Exception e) {
            Bugsnag.notify(e);
            e.printStackTrace();
        }
    }


    private void showPriorityDialog(final TextView mTextView) {
        try {
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

            //YD increasing the height of the popup if the resolution is high
            DisplayMetrics dm = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int dens = dm.densityDpi;
            if (dens > DisplayMetrics.DENSITY_XHIGH)
                mheight = 800;


            Utilities.setDividerTitleColor(dialog, mheight, mActivity);

            Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            Utilities.setDefaultFont_12(button_negative);
            Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Utilities.setDefaultFont_12(button_positive);
        } catch (Exception e) {
            Bugsnag.notify(e);
            e.printStackTrace();
        }
    }

    private void showDialogCategory(final ArrayList<String> mArrayList, final TextView mTextView, final String title) {
        try {
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
            int dens = dm.densityDpi;
            if (dens > DisplayMetrics.DENSITY_XHIGH)
                mheight = 800;

            Utilities.setDividerTitleColor(dialog, mheight, mActivity);

            Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            Utilities.setDefaultFont_12(button_negative);
            Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Utilities.setDefaultFont_12(button_positive);
        } catch (Exception e) {
            Bugsnag.notify(e);
            e.printStackTrace();
        }
    }

    //My R later
	
	/*private void showMsgPopupList(final String number, final String msg) {

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				(Activity) mContext, android.R.layout.select_dialog_item,
				mMessageArryList);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				(Activity) mContext);
		builder.setTitle("Duration");
		builder.setCancelable(true);
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int arg2) {
				Log.e("poss", "is:" + arg2);

				try {
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(number, null, msg
							+ mMessageArryList.get(arg2), null, null);

					Utilities.showToastMessage(mContext, mContext.getResources()
							.getString(R.string.msg_sms_sent));

				} catch (Exception e) {
					Utilities.showToastMessage(mContext, mContext.getResources()
							.getString(R.string.msg_sms_failed));
					e.printStackTrace();
				}

			}
		});
		
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {         	
            	dialog.dismiss();            	
            }
        });
		
		final AlertDialog dialog = builder.create();
		Utilities.setAlertDialogRow(dialog);
		dialog.show();

		Utilities.setDividerTitleColor(dialog, mheight);
		Button button_Negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		Utilities.setDefaultFont_12(button_Negative);
		Button button_Positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		Utilities.setDefaultFont_12(button_Positive);
	}*/

    private void showSchedulePopupList(LinearLayout lytStatus) {//YD lytStatus is the holder.statusview itself  and called when clicked on order status box
        final LinearLayout lytVwStatus = lytStatus;
        final int orderPos = (Integer) lytVwStatus.getTag();
        ListView listView = new ListView(mContext);
        //	ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity) mContext, android.R.layout.select_dialog_item, mStatusNmArryList);
        try {
            TextView row_job_schd_txtvw = (TextView) lytStatus.findViewById(R.id.row_job_schd_txtvw);
            if (!row_job_schd_txtvw.getTag().toString().trim().equals("")) {//YD checking for text in the ui to show radio button
                for (int i = 0; i < mStatusNmArryList.size(); i++) {
                    if (mStatusNmArryList.get(i) instanceof ListItem) {
                        if (((ListItem) mStatusNmArryList.get(i)).getDesc().trim().toLowerCase().equals(row_job_schd_txtvw.getTag().toString().trim().toLowerCase())) {
                            ((ListItem) mStatusNmArryList.get(i)).ischecked = true;
                        } else {
                            ((ListItem) mStatusNmArryList.get(i)).ischecked = false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Bugsnag.notify(e);
            e.printStackTrace();
        }

        final StatusAlertAdapter adapter = new StatusAlertAdapter(mContext, mStatusNmArryList);
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mContext, R.style.commonDialogTheme);
        builder.setCancelable(true);
        builder.setTitle("Status");
        listView.setAdapter(adapter);
        builder.setView(listView);
		/*
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@SuppressLint("NewApi")
			public void onClick(DialogInterface dialog, int position) {
				
				LinearLayout lyt = ((LinearLayout)txtVwStatus.getParent().getParent());
				
				if (mStatusIdArryList.get(position)==4 || mStatusIdArryList.get(position)==5) {	
					txtVwStatus.setTextColor(mContext.getResources().getColor(R.color.bdr_green));
					((RelativeLayout)lyt.getChildAt(0)).setBackgroundResource(R.drawable.rounded_green_fill_bg);
				} else if (mStatusIdArryList.get(position)==0 || mStatusIdArryList.get(position)==1 
							|| mStatusIdArryList.get(position)==2 || mStatusIdArryList.get(position)==3) {					
					txtVwStatus.setTextColor(mContext.getResources().getColor(R.color.bg_sky_blue));
					((RelativeLayout)lyt.getChildAt(0)).setBackgroundResource(R.drawable.rounded_blue_fill_bg);
				} else if (mStatusIdArryList.get(position)>5){
					txtVwStatus.setTextColor(mContext.getResources().getColor(R.color.color_red));
					((RelativeLayout)lyt.getChildAt(0)).setBackgroundResource(R.drawable.rounded_red_fill_bg);
				}				
				
				txtVwStatus.setText(String.valueOf(mapOdrStatus.get(pKeys[position]).getNm()));	
				long currentStatId = mapOdrStatus.get(pKeys[position]).getId();
				// getting current order clicked
				Order order = OrderDataMap.get(mKeys[orderPos]);	
				odrListobj.updateStatus(order,currentStatId);

			//	notifyDataSetChanged();
			}
		});
*/

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        //	Utilities.setAlertDialogRow(dialog);
        dialog.show();

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                if (view.findViewById(R.id.txtTitle) != null) {
                    TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
                    Integer statusId = Integer.parseInt((String) txtTitle.getTag());

                    long currentStatId = statusId;
                    // getting current order clicked
                    Order order = OrderDataMap.get(mKeys[orderPos]);
                    odrListobj.updateorder_status.clear();
                    boolean ab = odrListobj.updateStatus(order, currentStatId, OrderDataMap, mapOdrStatus.get(currentStatId).getNm(),
                            false, String.valueOf(orderPos), null);


                    TextView txtSts = (TextView) lytVwStatus.getChildAt(0);

                    if (((Order) getItem(position)).getStatusId() == 4 ||
                            ((Order) getItem(position)).getStatusId() == 5 || ((Order) getItem(position)).getStatusId() == 6 ||
                            ((Order) getItem(position)).getStatusId() == 9 || ((Order) getItem(position)).getStatusId() == 10) {
                        //drawable.setStroke(10, mContext.getResources().getColor(R.color.bdr_green));
                        //holder.odrStatus.setTextColor(mContext.getResources().getColor(R.color.bdr_green));//YD earilier just setting up the text color for status
                        lytVwStatus.setBackgroundResource(R.drawable.login_rounded_green_fill_bg);

                        //holder.odrStatus.setTextColor(Color.parseColor("#ffffff"));
                        //holder.odrStatus.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mActivity)));
                        //((RelativeLayout)lyt.getChildAt(0)).setBackgroundResource(R.drawable.rounded_green_fill_bg);//YD earlier using for time and duration box background as it was on 0 index

                    } else if (((Order) getItem(position)).getStatusId() == 7) {
                        //drawable.setStroke(10, mContext.getResources().getColor(R.color.bg_sky_blue));
                        lytVwStatus.setBackgroundResource(R.drawable.round_purple_background_text);// YD converted to yellow

                        //holder.odrStatus.setTextColor(mContext.getResources().getColor(R.color.bg_sky_blue));
                        //lytVwStatus.setTextColor(Color.parseColor("#ffffff"));
                        //lytVwStatus.setTextSize(22 + s);
                        //((RelativeLayout)lyt.getChildAt(0)).setBackgroundResource(R.drawable.rounded_blue_fill_bg);
                    } else if (((Order) getItem(position)).getStatusId() > 29) {
                        //	drawable.setStroke(10, mContext.getResources().getColor(R.color.color_red));
                        //holder.odrStatus.setTextColor(mContext.getResources().getColor(R.color.color_red));
                        lytVwStatus.setBackgroundResource(R.drawable.rounded_blue_fill_bg);// YD converted to pink
                        //holder.odrStatus.setTextColor(Color.parseColor("#ffffff"));
                        //holder.odrStatus.setTextSize(22 + s);
                        //holder.odrStatus.setTypeface(Typeface.DEFAULT_BOLD);
                        //((RelativeLayout)lyt.getChildAt(0)).setBackgroundResource(R.drawable.rounded_red_fill_bg);
                    } else {
                        lytVwStatus.setBackgroundResource(R.drawable.round_blue_time_bg);
                        //holder.odrStatus.setTextColor(Color.parseColor("#ffffff"));
                        //holder.odrStatus.setTextSize(22 + s);
                        //holder.odrStatus.setTypeface(Typeface.DEFAULT_BOLD);
                    }


					/*if (statusId==4 || statusId==5 || statusId > 49) {

				//		showNoInternetDialog("Attach Forms", "Please attach " + "error st" + " before selecting the status")
				//		if (ab) {
							txtSts.setTextColor(mContext.getResources().getColor(R.color.bdr_green));
				//		}
				//		((RelativeLayout)lyt.getChildAt(0)).setBackgroundResource(R.drawable.rounded_green_fill_bg);
					} else if (statusId==0 || statusId==1 || statusId==2 || statusId==3) {
				//		if (ab) {
							txtSts.setTextColor(mContext.getResources().getColor(R.color.bg_sky_blue));
				//		}
					//	((RelativeLayout)lyt.getChildAt(0)).setBackgroundResource(R.drawable.rounded_blue_fill_bg);
					} else if (statusId>5){
				//		if (ab) {
							txtSts.setTextColor(mContext.getResources().getColor(R.color.color_red));
				//		}
					//	((RelativeLayout)lyt.getChildAt(0)).setBackgroundResource(R.drawable.rounded_red_fill_bg);
					}*/
                    //	txtVwStatus.setText(String.valueOf(mapOdrStatus.get(pKeys[position]).
                    //	getNm()));
                    //	long currentStatId = mapOdrStatus.get(pKeys[position]).getId();
                    txtSts.setTypeface(Typeface.DEFAULT_BOLD);
                    txtSts.setTextSize(28);
                    txtSts.setText(String.valueOf(txtTitle.getText()));

                    //lytVwStatus.setBackgroundResource(R.drawable.rounded_green_fill_bg);
                    dialog.dismiss();
                }
            }
        });

        //YD increasing the height of the popup if the resolution is high
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int dens = dm.densityDpi;
        if (dens > DisplayMetrics.DENSITY_XHIGH)
            mheight = 800;

        Utilities.setDividerTitleColor(dialog, mheight, mActivity);
        Button button_Negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Utilities.setDefaultFont_12(button_Negative);
        Button button_Positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Utilities.setDefaultFont_12(button_Positive);
    }


    MySearchDialogWithHeader searchDialg;
    long elemtSelectedInSearchDlog = -1;

    private void showStatusDialog(LinearLayout lytStatus) {

        final LinearLayout lytVwStatus = lytStatus;
        final int orderPos = (Integer) lytVwStatus.getTag();

        //YD 2020 code to check if the order
        boolean shouldUpdateStatus = true;

        if (orderPos > 0) {
            long statusIdOfPriorOdr = OrderDataMap.get(mKeys[0]).getStatusId();
            if (statusIdOfPriorOdr == 0 || statusIdOfPriorOdr == 1 || statusIdOfPriorOdr == 2) {
                showErrorStringMessage("Assigned work must be completed in scheduled sequence. Please finish prior work item to continue.");
                return;
            }
        }


        ListView listView = new ListView(mContext);
        //	ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity) mContext, android.R.layout.select_dialog_item, mStatusNmArryList);
        try {
            TextView row_job_schd_txtvw = (TextView) lytStatus.findViewById(R.id.row_job_schd_txtvw);
            if(row_job_schd_txtvw!=null && row_job_schd_txtvw.getTag()!=null) {
                if (!row_job_schd_txtvw.getTag().toString().trim().equals("")) {//YD checking for text in the ui to show radio button
                    for (int i = 0; i < mStatusNmArryList.size(); i++) {
                        if (mStatusNmArryList.get(i) instanceof ListItem) {
                            if (((ListItem) mStatusNmArryList.get(i)).getDesc().trim().toLowerCase().equals(row_job_schd_txtvw.getTag().toString().trim().toLowerCase())) {
                                ((ListItem) mStatusNmArryList.get(i)).ischecked = true;
                            } else {
                                ((ListItem) mStatusNmArryList.get(i)).ischecked = false;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Bugsnag.notify(e);
            e.printStackTrace();
        }


        searchDialg = new MySearchDialogWithHeader(BaseTabActivity.mBaseTabActivity, "Status", "", mStatusNmArryList, elemtSelectedInSearchDlog);

        searchDialg.setkeyListender(new MySearchDiologHeaderInterface() {
            @Override
            public void onButtonClick() {
                super.onButtonClick();
                searchDialg.cancel();
            }

            @Override
            public void onItemSelected(View view) {
                super.onItemSelected(view);

                if (view.findViewById(R.id.txtTitle) != null) {
                    final TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
                    final Integer statusId = Integer.parseInt((String) txtTitle.getTag());

                    long currentStatId = statusId;
                    // getting current order clicked
                    System.out.println("Value order position " + orderPos);
                    Order order = OrderDataMap.get(mKeys[orderPos]);
                    odrListobj.updateorder_status.clear();
                    odrListobj.updateStatus(order, currentStatId, OrderDataMap, mapOdrStatus.get(currentStatId).getNm(), true, String.valueOf(orderPos), new CustomListner() {
                        @Override
                        public void onClick(View view) {

                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView txtSts = (TextView) lytVwStatus.getChildAt(0);

                                    if (statusId == 0) {
                                        showErrorStringMessage("Destination address may not be accurate, please verify.");

                                    } else if (statusId == 4 ||
                                            statusId == 5 || statusId == 6 ||
                                            statusId == 9 || statusId == 10) {
                                        //drawable.setStroke(10, mContext.getResources().getColor(R.color.bdr_green));
                                        //holder.odrStatus.setTextColor(mContext.getResources().getColor(R.color.bdr_green));//YD earilier just setting up the text color for status
                                        lytVwStatus.setBackgroundResource(R.drawable.login_rounded_green_fill_bg);
                                        //holder.odrStatus.setTextColor(Color.parseColor("#ffffff"));
                                        //holder.odrStatus.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mActivity)));
                                        //((RelativeLayout)lyt.getChildAt(0)).setBackgroundResource(R.drawable.rounded_green_fill_bg);//YD earlier using for time and duration box background as it was on 0 index

                                    } else if (statusId == 7) {
                                        //drawable.setStroke(10, mContext.getResources().getColor(R.color.bg_sky_blue));
                                        lytVwStatus.setBackgroundResource(R.drawable.round_fill_bg);// YD converted to yellow
                                        //holder.odrStatus.setTextColor(mContext.getResources().getColor(R.color.bg_sky_blue));
                                        //lytVwStatus.setTextColor(Color.parseColor("#ffffff"));
                                        //lytVwStatus.setTextSize(22 + s);
                                        //((RelativeLayout)lyt.getChildAt(0)).setBackgroundResource(R.drawable.rounded_blue_fill_bg);
                                    } else if (statusId > 29) {
                                        //	drawable.setStroke(10, mContext.getResources().getColor(R.color.color_red));
                                        //holder.odrStatus.setTextColor(mContext.getResources().getColor(R.color.color_red));
                                        lytVwStatus.setBackgroundResource(R.drawable.rounded_blue_fill_bg);// YD converted to pink
                                        //holder.odrStatus.setTextColor(Color.parseColor("#ffffff"));
                                        //holder.odrStatus.setTextSize(22 + s);
                                        //holder.odrStatus.setTypeface(Typeface.DEFAULT_BOLD);
                                        //((RelativeLayout)lyt.getChildAt(0)).setBackgroundResource(R.drawable.rounded_red_fill_bg);
                                    } else {
                                        lytVwStatus.setBackgroundResource(R.drawable.round_blue_time_bg);
                                        //holder.odrStatus.setTextColor(Color.parseColor("#ffffff"));
                                        //holder.odrStatus.setTextSize(22 + s);
                                        //holder.odrStatus.setTypeface(Typeface.DEFAULT_BOLD);
                                    }
                                  /*  if (statusId==4 || statusId==5 || statusId > 49) {
                                        txtSts.setTextColor(mContext.getResources().getColor(R.color.bdr_green));
                                        //		((RelativeLayout)lyt.getChildAt(0)).setBackgroundResource(R.drawable.rounded_green_fill_bg);
                                    } else if (statusId==0 || statusId==1 || statusId==2 || statusId==3) {
                                        txtSts.setTextColor(mContext.getResources().getColor(R.color.bg_sky_blue));
                                        //	((RelativeLayout)lyt.getChildAt(0)).setBackgroundResource(R.drawable.rounded_blue_fill_bg);
                                    } else if (statusId>5){
                                        txtSts.setTextColor(mContext.getResources().getColor(R.color.color_red));
                                        //	((RelativeLayout)lyt.getChildAt(0)).setBackgroundResource(R.drawable.rounded_red_fill_bg);
                                    }*/
                                    //	txtVwStatus.setText(String.valueOf(mapOdrStatus.get(pKeys[position]).getNm()));
                                    //	long currentStatId = mapOdrStatus.get(pKeys[position]).getId();
                                    txtSts.setTypeface(Typeface.DEFAULT_BOLD);

                                    txtSts.setTextSize(28);

                                    txtSts.setText(String.valueOf(txtTitle.getText()));

                                }
                            });
                        }
                    });//YD TODO 2020
                    searchDialg.dismiss();
                }
            }
        });
        searchDialg.setCanceledOnTouchOutside(true);
        searchDialg.onCreate1(null);
        searchDialg.show();

        int mheight = 500;
        Utilities.setDividerTitleColor(searchDialg, mheight, mActivity);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(searchDialg.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        searchDialg.getWindow().setAttributes(lp);

    }

    private void showErrorStringMessage(String StringMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("Action Required");
        builder.setMessage(StringMsg);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        //builder.show();
        //YD OR
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        //YD 2020 doing UI setting for dialog
        Utilities.setDividerTitleColor(alertDialog, mheight, mActivity);
        Button button_positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Utilities.setDefaultFont_12(button_positive);

    }

    @Override
    public void headerClickListener(String callingId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setResponseCBActivity(Response response) {
        if (response != null) {
            if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED))) {
                if (response.getId() == SAVEMESSAGE) {
                    mActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Handler handler = new Handler();// YD hiding loading after one second (Client requirement)
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // odrListobj.webviewOrderListMain.setVisibility(View.GONE);
                                }
                            }, 1000);
                        }
                    });
                }
                if (response.getId() == SAVE_ORDER_TIME) {
                    getAndUpdateOrderDateTime();
                }
                if (response.getId() == SAVEORDERFIELD_STATUS_CATEGORY) {

                    previousCatName = newCategory;
                    previousTypeID = newCategoryId;
                    previousPriorityName = newPriorityNm;
                    previousPriorityId = newPriorityId;
                    previousPoVal = newSSD;
                    previousInvNm = newRoute;
                    previousDesc = newDesc;

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Order activeOrderObj = OrderDataMap.get(mKeys[selectedOrderPosition]);
                            activeOrderObj.setOrderTypeId(Long.parseLong(newCategoryId));
                            txtViewOrderType.setText(newCategory);
                            txtViewOrderType.setTag(newCategoryId);

                            if (newCategory.equals(""))
                                txtViewOrderType.setVisibility(View.GONE);
                            else
                                txtViewOrderType.setVisibility(View.VISIBLE);

                            if (newCategory.equals("") || newDesc.equals(""))
                                txtViewOrderSepra.setVisibility(View.GONE);
                            else
                                txtViewOrderSepra.setVisibility(View.VISIBLE);

                            activeOrderObj.setPriorityTypeId(Long.parseLong(newPriorityId));
                            //	odr_detail_priority.setText(newPriorityNm);
                            //	odr_detail_priority.setTag(newPriorityId);

                            activeOrderObj.setSummary(newDesc);
                            txtViewOrderDesc.setText("");
                            txtViewOrderDesc.setText(newDesc);

                            activeOrderObj.setPoNumber(newSSD);
                            //	odr_detail_ssd.setText(newSSD);

                            activeOrderObj.setInvoiceNumber(newRoute);
                            //	odr_detail_route.setText(newRoute);
                        }
                    });
                }
                if (response.getId() == SAVEORDERFIELD_STATUS_ALERT) {
                    previousAlert = newAlert;
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Order activeOrderObj = OrderDataMap.get(mKeys[selectedOrderPosition]);
                            activeOrderObj.setOrderAlert(newAlert);
                            txtViewAlert.setText(newAlert);  // Alert field TextView update
                        }
                    });

                }

                if (response.getId() == SAVENOTE) {
                    previousNote = newNote;
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap<Long, OrderNotes> notesMap = (HashMap<Long, OrderNotes>) DataObject.orderNoteDataStore;
                            OrderNotes noteObj = notesMap.get(((Order) getItem(selectedOrderPosition)).getId());
                            if (noteObj != null)
                                noteObj.setOrdernote(newNote);

                            if (newNote.length() > 136)
                                txtViewNote.setText(newNote.substring(0, 136) + "...");
                            else
                                txtViewNote.setText(newNote);  // Alert field TextView update
                        }
                    });
                }

                if (response.getId() == CHECKIN_REQ) {
                    PreferenceHandler.setClockInStat(mActivity, "1");
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.ChangeCheckout(0);
                        }
                    });

                }
            } else if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA))) {
            }
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

	/*private void ShowMessageDialog(String Strdesc){
		try{		
			dialog = new MyDialog(mActivity, mContext.getResources().getString(R.string.msg_slight_problem), Strdesc,"OK");			
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
			dialog.onCreate1(null);
			dialog.show();
			
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			lp.copyFrom(dialog.getWindow().getAttributes());
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;		
			lp.gravity = Gravity.CENTER;		
			dialog.getWindow().setAttributes(lp);
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/

    private void openGoogleNavigation(Double latitude, Double longitude) {
        // for google maps
		 
		 /*GPSTracker mGPS = new GPSTracker(mActivity);
		 if (mGPS.canGetLocation) {
	     	mGPS.getLocation();
		 } else {		
			 Log.e("TAG :", "Unable to get location");
		 }
		
		 Double startLat = mGPS.getLatitude();
		 Double startLong = mGPS.getLongitude();*/
        String geo = Utilities.getLocation(mActivity);
        String geoArr[] = geo.split(",");
        Double startLat = 0.0, startLong = 0.0;
        if (geoArr[0] != null && !geoArr[0].equals("") && !geoArr[0].isEmpty())
            startLat = Double.valueOf(geoArr[0]);
        if (geoArr[1] != null && !geoArr[1].equals("") && !geoArr[1].isEmpty())
            startLong = Double.valueOf(geoArr[1]);
		
		 /*if(mGPS.getLatitude()==0.0 || mGPS.getLongitude()==0.0){
			 String lastKnownLocation = PreferenceHandler.getTempLastLocation(mActivity);
			 if(lastKnownLocation!=null){
				 startLat = Double.parseDouble(lastKnownLocation.split(",")[0]);
				 startLong = Double.parseDouble(lastKnownLocation.split(",")[1]);
			 }
		 }	*/

        Utilities.log(mActivity, "opening map navigation with startpoint as saddr=" + startLat + "," + startLong + "&daddr=" + latitude + "," + longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + "saddr=" + startLat + "," + startLong + "&daddr=" + latitude + "," + longitude));
        // Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
        // intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        //YD// intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.driveabout.app.NavigationActivity");
        //YD intent.setPackage("com.here.app.maps");
        mActivity.startActivity(intent);
    }

    private void getAndUpdateOrderDateTime() {//
        String[] value = finalDateStr.split(",");
        Order activeOrderObj = OrderDataMap.get(mKeys[selectedOrderPosition]);
        activeOrderObj.setStartTime(Long.valueOf(value[0]));
        activeOrderObj.setEndTime(Long.valueOf(value[1]));
        activeOrderObj.setFromDate(value[2].replace("/", "-"));
        activeOrderObj.setToDate(value[3].replace("/", "-"));

        Date startdate = getStatDate(value[2].replace("/", "-"));

        if (PreferenceHandler.getOdrGetDate(mContext) != null && !PreferenceHandler.getOdrGetDate(mContext).equals("")) {
            if (!Utilities.chkDtEqualCurrentDt(mContext, startdate)) {
                ((HashMap<Long, Order>) DataObject.ordersXmlDataStore).remove(activeOrderObj.getId());
                OrderListMainFragment.changeSequenceNoOnOrder = true;
                MapFragment.changeSequenceNoOnOrder = true;
                odrListobj.loadOrdersAtBoot();
            }

        } else if (!Utilities.isTodayDate(mActivity, startdate)) {// YD removing order if the order date is not of current date
            ((HashMap<Long, Order>) DataObject.ordersXmlDataStore).remove(activeOrderObj.getId());
            OrderListMainFragment.changeSequenceNoOnOrder = true;
            MapFragment.changeSequenceNoOnOrder = true;
            odrListobj.loadOrdersAtBoot();
            //mActivity.popFragments(mActivity.SERVICE_Thread);
        } else {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    // YD function to convert String date to the date object
    public Date getStatDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
        Date startdate = null;
        try {
            startdate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            Bugsnag.notify(e);
            e.printStackTrace();
        }

        return startdate;
    }

    private void sortPriorty(HashMap<Long, OrderPriority> odrPriorityListTemp) {
        long[] x = {0, 1, 4, 5, 6};

        for (int i = 0; i < x.length; i++) {
            mapOdrPriority.put(x[i], odrPriorityListTemp.get(x[i]));
        }
    }

    private void showErrorDialog(String message) {
        try {
            dialog = new MyDialog(mActivity, mActivity.getResources().getString(R.string.msg_slight_problem), message, "OK");
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

        } catch (Exception e) {
            Bugsnag.notify(e);
            e.printStackTrace();
        }
    }

    private String getTextValue(Date endTime, Date startTime) {
        if (startTime != null && endTime != null) {
            return String.valueOf((endTime.getTime() - startTime.getTime()) / 60000);
        } else {
            return "";
        }
    }

}
