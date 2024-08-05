package com.aceroute.mobile.software.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.SplashII;
import com.aceroute.mobile.software.async.IActionOKCancel;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.camera.Gridview_MainActivity;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderMedia;
import com.aceroute.mobile.software.component.OrderTask;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.OrderPriority;
import com.aceroute.mobile.software.component.reference.ServiceType;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.dialog.CustomDialog;
import com.aceroute.mobile.software.dialog.CustomDialog.DIALOG_TYPE;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.SaveMediaRequest;
import com.aceroute.mobile.software.requests.SaveTaskDataRequest;
import com.aceroute.mobile.software.requests.Save_DeleteTaskRequest;
import com.aceroute.mobile.software.requests.updateOrderRequest;
import com.aceroute.mobile.software.utilities.JSONHandler;
import com.aceroute.mobile.software.utilities.OnSwipeTouchListener;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.aceroute.mobile.software.validation.ValidationEngine;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import org.json.JSONException;

import java.io.File;
import java.text.ParseException;
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
import java.util.TimeZone;

public class AddEditTaskOrderFragment extends BaseFragment implements RespCBandServST, HeaderInterface, IActionOKCancel {
    private static final int REQUEST_CODE_ACTIVITY_PICK_CAMERA = 22;
    public static int fieldUpdated = 0;
    private static int SAVE_TASK = 1;
    final ArrayList<Long> seletedItems = new ArrayList<Long>();// ListofRestriction
    ValidationEngine validation;
    MyDialog dialog;
    ArrayList<String> mTreeSpeciesArryList;
    ArrayList<String> mTrimTypeArryList;
    ArrayList<String> mTrimTypeArryListId;
    ArrayList<String> mPriorityArryList;
    ArrayList<String> mStatusTypeArryList, mStatusTypeArryListId;//YD need to change later not the good way
    ArrayList<String> mTreeCrewTypeArryList;
    ArrayList<String> mRestrictionArryList;
    ArrayList<String> mNotificationArryList;
    HashMap<Long, ServiceType> mapOrderTask;
    Long[] Tkeys;
    HashMap<Long, SiteType> mapOrderSite;
    Long[] Skeys;
    Map<Long, OrderPriority> mapPriority = Collections.synchronizedMap(new LinkedHashMap<Long, OrderPriority>());
    Long[] Pkeys;
    String treeGeo = "";
    ArrayList<String> accessTreeGeo = new ArrayList<String>();
    String taskId = null;
    private Context mContext;
    private SeekBar add_odrTask_access_complexity, add_odrTask_setup_complexity_skbar, add_odrTask_pres_complexity_skbar, add_odrTask_way_point_skbar;
    private TextView add_odrTask_page_type_txtvw, add_odrTask_tree_species_txtvw, add_odrTask_trim_type_txtvw, add_odrTask_priority_txtvw,
            add_odrTask_access_complexity_value_txtvw, add_odrTask_setup_complexity_value_txtvw, add_odrTask_perscription_complexity_value_txtvw,
            add_odrTask_record_tree_geocode_txtvw, add_odrTask_log_way_point_txtvw, add_odrTask_log_access_path_txtvw,
            add_odrTask_way_point_value_txtvw, add_odrTask_way_point_list_txtvw, add_odrTask_snap_it_txtvw, add_odrTask_snaps_txtvw,
            odr_tree_status, odr_tree_crew_type, odr_tree_restriction, odr_tree_notification;
    private EditText add_odrTask_estimated_count_edt, add_odrTask_actual_count_edt, add_odrTask_Height_edt,
            add_odrTask_diametere_edt, add_odrTask_hv_clearance, add_odrTask_cycle_edt,
            add_odrTask_tree_comment_edt, add_odrTask_pres_comment_edt, add_odrTask_alerts_adt, add_odrTask_notes_edt;
    private CheckBox chk_customer_at_location;

    private RelativeLayout parentView;
    private SwipeListView add_odr_odrLogPointList;
    private boolean logAccesspath = true;
    private boolean recordTreeGeoCode = true;
    private String currentOdrId, currentOdrName;
    private long gorderEndTime;
    private String gorderEndDate, requestType;
    private WebView webviewAddOrderTask;
    private OnSwipeTouchListener mOnSwipeTouchListener;
    private int mheight = 500;
    private Uri uri;
    private String orderIdTaskPic;
    private String taskIdTaskPic;
    private String typeSaveTaskPic;
    private String RestrictionLst;
    private int SAVEPICTURE = 2;
    private Order activeOrderObj;
    //	private int GET_ALL_ORDER_TASK=3;
    private int GET_CURRENT_ODR_TASKS = 20;
    private int SAVEORDERFIELD_STATUS_PRIORITY = 5;
    private boolean isDifferent = false;
    private String previousTreeSpecies = "";
    private String previousTrimType = "";
    private String previousPriority = "";
    private String previousStatus = "";
    private String previousTreeCrewType = "";
    private String previousCustomerAtLocation = "0";
    private String previousRestriction = "";
    private String previousNotification = "";
    private String previousEstimatedCount = "";
    private String previousActualCount = "0";
    private String previousHeight = "";
    private String previousDiameterBh = "";
    private String previousHvClearance = "";
    private String previousCycle = "";
    private String previousTreeComment = "";
    private String previousPrescriptioComment = "";
    private String previousAlerts = "";
    private String previousNotes = "";
    private String previousLogTreeGeoCode = "";

    // Sorting hashMap
    private static HashMap<Long, ServiceType> sortSeviceTypeLst(HashMap<Long, ServiceType> unsortMap) {

        HashMap<Long, ServiceType> sortedMap = new LinkedHashMap<Long, ServiceType>();

        if (unsortMap != null) {
            // Convert Map to List
            List<Entry<Long, ServiceType>> list =
                    new LinkedList<Entry<Long, ServiceType>>(unsortMap.entrySet());

            // Sort list_cal with comparator, to compare the Map values
            Collections.sort(list, new Comparator<Entry<Long, ServiceType>>() {
                public int compare(Entry<Long, ServiceType> o1,
                                   Entry<Long, ServiceType> o2) {
                    return (o1.getValue().getNm()).compareTo(o2.getValue().getNm());
                }
            });

            // Convert sorted map back to a Map
            for (Iterator<Entry<Long, ServiceType>> it = list.iterator(); it.hasNext(); ) {
                Entry<Long, ServiceType> entry = it.next();
                sortedMap.put(entry.getKey(), entry.getValue());
            }
        }
        return sortedMap;
    }

    // Sorting hashMap
    private static HashMap<Long, SiteType> sortSiteTypeLst(HashMap<Long, SiteType> unsortMap) {

        // Convert Map to List
        List<Entry<Long, SiteType>> list =
                new LinkedList<Entry<Long, SiteType>>(unsortMap.entrySet());

        // Sort list_cal with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Entry<Long, SiteType>>() {
            public int compare(Entry<Long, SiteType> o1,
                               Entry<Long, SiteType> o2) {
                return (o1.getValue().getNm()).compareTo(o2.getValue().getNm());
            }
        });

        // Convert sorted map back to a Map
        HashMap<Long, SiteType> sortedMap = new LinkedHashMap<Long, SiteType>();
        for (Iterator<Entry<Long, SiteType>> it = list.iterator(); it.hasNext(); ) {
            Entry<Long, SiteType> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    //	private String oldT_M_Val="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("AceRoute", "onCreateView called for addedittaskfrag");
        mActivity.registerHeader(this);

        View v = inflater.inflate(R.layout.activity_add_customer_order, null);

        mContext = mActivity;

        validation = ValidationEngine.getInstance(mActivity);
        validation.initValidation("AddEditTaskOrderFragment");

        initiViewReference(v);
        return v;
    }

    private void initiViewReference(View v) {
        Bundle bundle = this.getArguments();
        currentOdrId = bundle.getString("OrderId");
        currentOdrName = bundle.getString("OrderName");
        requestType = bundle.getString("TreeType");//YD also using for displaying header name
        mActivity.setHeaderTitle("", requestType, "");

        mTreeSpeciesArryList = new ArrayList<String>();
        mTrimTypeArryList = new ArrayList<String>();
        mTrimTypeArryListId = new ArrayList<String>();
        mPriorityArryList = new ArrayList<String>();
        mStatusTypeArryList = new ArrayList<String>();
        mStatusTypeArryListId = new ArrayList<String>();
        mTreeCrewTypeArryList = new ArrayList<String>();
        mRestrictionArryList = new ArrayList<String>();
        mNotificationArryList = new ArrayList<String>();

        String[] strStatusType = {"Open", "Closed", "False Detection", "No Tree", "Refusal", "Review"};
        String[] strStatusTypeIds = {"0", "4", "5", "6", "2", "3"};
        for (int k = 0; k < strStatusType.length; k++) {
            mStatusTypeArryList.add(strStatusType[k]);
            mStatusTypeArryListId.add(strStatusTypeIds[k]);
        }

        String[] strCrewType = {"None", "Routine Climb", "Routine Lift", "Hazard Tree"};
        for (int j = 0; j < strCrewType.length; j++) {
            mTreeCrewTypeArryList.add(strCrewType[j]);
        }

        String[] strRestriction = {"None", "Access", "Access Permit", "Bad Dog", "Bee/Wasp", "CAT 1", "CAT 2", "CAT 3", "Concerned Customer",
                "Concurrent Patrol-LOC", "Concurrent Patrol-SYS", "Dog", "Eagle", "Endangered Species", "Environmental BMP", "Existing New Planting",
                "Existing Trans Mit Plan", "Fire Threat Zone", "HCP Mapbook Zone", "HOC", "Livestock", "Locked Gate", "Mid Span Clear", "Mitigation Plan",
                "Nest BMP", "New Planting", "Notify First", "Past Nest Review", "Past R-Review", "Past Refusal", "PI Notify First", "Poison-Oak", "Riparian",
                "Traffic Issue", "Tree House", "VELB Site", "Whole Span Clear"};

        for (int m = 0; m < strRestriction.length; m++) {
            mRestrictionArryList.add(strRestriction[m]);
        }

        String[] strNotification = {"None", "ByDoorTag", "ByPhone", "ByPermit", "InPerson"};
        for (int m = 0; m < strNotification.length; m++) {
            mNotificationArryList.add(strNotification[m]);
        }
        HashMap<Long, ServiceType> unsortedOrderTask = (HashMap<Long, ServiceType>) DataObject.taskTypeXmlDataStore;
        mapOrderTask = sortSeviceTypeLst(unsortedOrderTask);
        Tkeys = mapOrderTask.keySet().toArray(new Long[mapOrderTask.size()]);
        for (int i = 0; i < mapOrderTask.size(); i++) {
            ServiceType odrService = mapOrderTask.get(Tkeys[i]);
            mTreeSpeciesArryList.add(String.valueOf(odrService.getNm()));
        }

        HashMap<Long, SiteType> unsortedSiteType = (HashMap<Long, SiteType>) DataObject.siteTypeXmlDataStore;
        mapOrderSite = sortSiteTypeLst(unsortedSiteType);
        Skeys = mapOrderSite.keySet().toArray(new Long[mapOrderSite.size()]);
        for (int i = 0; i < mapOrderSite.size(); i++) {
            SiteType odrSite = mapOrderSite.get(Skeys[i]);
            if (odrSite.getTid() == 3) {
                mTrimTypeArryList.add(String.valueOf(odrSite.getNm()));
                mTrimTypeArryListId.add(String.valueOf(odrSite.getId()));
            }
        }

        sortPriorty((HashMap<Long, OrderPriority>) DataObject.orderPriorityTypeXmlDataStore);
        Pkeys = mapPriority.keySet().toArray(new Long[mapPriority.size()]);

        for (int i = 0; i < mapPriority.size(); i++) {
            OrderPriority odrPriorty = mapPriority.get(Pkeys[i]);
            mPriorityArryList.add(String.valueOf(odrPriorty.getNm()));
        }

        webviewAddOrderTask = (WebView) v.findViewById(R.id.webviewAddOrderTask);
        webviewAddOrderTask.setBackgroundColor(Color.TRANSPARENT);
        webviewAddOrderTask.loadUrl("file:///android_asset/loading.html");

        parentView = (RelativeLayout) v.findViewById(R.id.parentView);

        add_odrTask_access_complexity = (SeekBar) v.findViewById(R.id.odr_task_access_complexity);
        add_odrTask_setup_complexity_skbar = (SeekBar) v.findViewById(R.id.odr_task_setup_complexity);
        add_odrTask_pres_complexity_skbar = (SeekBar) v.findViewById(R.id.odr_task_pres_complexity);
        add_odrTask_way_point_skbar = (SeekBar) v.findViewById(R.id.odr_task_way_point);

        add_odrTask_page_type_txtvw = (TextView) v.findViewById(R.id.page_type);
        add_odrTask_tree_species_txtvw = (TextView) v.findViewById(R.id.odr_task_tree_species);
        add_odrTask_trim_type_txtvw = (TextView) v.findViewById(R.id.odr_task_trim_type);
        add_odrTask_priority_txtvw = (TextView) v.findViewById(R.id.odr_task_priority);
        odr_tree_status = (TextView) v.findViewById(R.id.odr_tree_status);
        odr_tree_crew_type = (TextView) v.findViewById(R.id.odr_tree_crew_type);
        odr_tree_restriction = (TextView) v.findViewById(R.id.odr_tree_restriction);
        odr_tree_notification = (TextView) v.findViewById(R.id.odr_tree_notification);

        add_odrTask_access_complexity_value_txtvw = (TextView) v.findViewById(R.id.access_complexity_count);
        add_odrTask_setup_complexity_value_txtvw = (TextView) v.findViewById(R.id.setup_complexity_count);
        add_odrTask_perscription_complexity_value_txtvw = (TextView) v.findViewById(R.id.prescription_complexity_count);
        add_odrTask_record_tree_geocode_txtvw = (TextView) v.findViewById(R.id.odr_task_record_tree_geocode);
        add_odrTask_log_way_point_txtvw = (TextView) v.findViewById(R.id.odr_task_log_way_point);
        add_odrTask_log_access_path_txtvw = (TextView) v.findViewById(R.id.odr_task_log_access_path);
        add_odrTask_way_point_value_txtvw = (TextView) v.findViewById(R.id.way_point_count);
        add_odrTask_way_point_list_txtvw = (TextView) v.findViewById(R.id.way_point_list);
        add_odrTask_snap_it_txtvw = (TextView) v.findViewById(R.id.odr_task_snap_it);
        add_odrTask_snaps_txtvw = (TextView) v.findViewById(R.id.odr_task_snaps);

        add_odrTask_estimated_count_edt = (EditText) v.findViewById(R.id.odr_task_estimate_count);
        add_odrTask_actual_count_edt = (EditText) v.findViewById(R.id.odr_task_actual_count);
        add_odrTask_Height_edt = (EditText) v.findViewById(R.id.odr_task_height);
        add_odrTask_diametere_edt = (EditText) v.findViewById(R.id.odr_task_diameter);
        add_odrTask_hv_clearance = (EditText) v.findViewById(R.id.odr_task_hv_clear);
        add_odrTask_cycle_edt = (EditText) v.findViewById(R.id.odr_task_cycle);
        //	add_odrTask_hours_edt = (EditText) v.findViewById(R.id.odr_task_hours);
//		add_odrTask_t_and_m_edt = (EditText) v.findViewById(R.id.odr_task_t_and_m);
        //	add_odrTask_ot_edt = (EditText) v.findViewById(R.id.odr_task_ot);
        add_odrTask_tree_comment_edt = (EditText) v.findViewById(R.id.odr_task_tree_comment);
        add_odrTask_pres_comment_edt = (EditText) v.findViewById(R.id.odr_task_pres_comment);
        add_odrTask_alerts_adt = (EditText) v.findViewById(R.id.odr_task_alerts);
        add_odrTask_notes_edt = (EditText) v.findViewById(R.id.odr_task_notes);

        chk_customer_at_location = (CheckBox) v.findViewById(R.id.chk_customer_at_location);

        add_odrTask_page_type_txtvw.setText(bundle.getString("TreeType"));

        if (requestType.equals("EDIT TASK")) {
            taskId = String.valueOf(bundle.getLong("Id"));

            //	if(bundle.getString("Status").equals("1"))

            add_odrTask_tree_species_txtvw.setText(mapOrderTask.get(bundle.getLong("ServiceId")).getNm());
            add_odrTask_tree_species_txtvw.setTag(mapOrderTask.get(bundle.getLong("ServiceId")).getId());
            previousTreeSpecies = "" + mapOrderTask.get(bundle.getLong("ServiceId")).getId(); // Tree Species
            previousTreeSpecies = previousTreeSpecies.trim();
            if (bundle.getLong("SiteId") != 0) {
                //YD assuming that bydefault getting the currect site which is of type tid=3
                add_odrTask_trim_type_txtvw.setText(mapOrderSite.get(bundle.getLong("SiteId")).getNm());
                add_odrTask_trim_type_txtvw.setTag(mapOrderSite.get(bundle.getLong("SiteId")).getId());

                previousTrimType = "" + mapOrderSite.get(bundle.getLong("SiteId")).getId(); // Trim Type
                previousTrimType = previousTrimType.trim();
            }
            add_odrTask_priority_txtvw.setText(mapPriority.get(bundle.getLong("PriorityId")).getNm());
            add_odrTask_priority_txtvw.setTag(mapPriority.get(bundle.getLong("PriorityId")).getId());

            previousPriority = "" + mapPriority.get(bundle.getLong("PriorityId")).getId(); // Priority
            previousPriority = previousPriority.trim();

            //	add_odrTask_owner_edt.setText(bundle.getString("Owner"));
            add_odrTask_estimated_count_edt.setText(bundle.getString("EstimatedCount"));

            previousEstimatedCount = bundle.getString("EstimatedCount");  // Estimated Count
            previousEstimatedCount = previousEstimatedCount.trim();

            add_odrTask_actual_count_edt.setText(bundle.getString("ActualCount"));
            previousActualCount = bundle.getString("ActualCount");   // Actual Count
            previousActualCount = previousActualCount.trim();

            add_odrTask_Height_edt.setText(bundle.getString("TreeHeight"));
            previousHeight = bundle.getString("TreeHeight");  // Tree Height
            previousHeight = previousHeight.trim();

            add_odrTask_diametere_edt.setText(bundle.getString("DiameterBH"));
            previousDiameterBh = bundle.getString("DiameterBH");  // Diameter BH
            previousDiameterBh = previousDiameterBh.trim();

            add_odrTask_hv_clearance.setText(bundle.getString("HvClearance"));
            previousHvClearance = bundle.getString("HvClearance");  // HV Clearance
            previousHvClearance = previousHvClearance.trim();

            add_odrTask_cycle_edt.setText(bundle.getString("Cycle"));
            previousCycle = bundle.getString("Cycle"); // Cycle
            previousCycle = previousCycle.trim();

            //		add_odrTask_hours_edt.setText(bundle.getString("Hours"));
            //		add_odrTask_t_and_m_edt.setText(bundle.getString("TandM"));
            //		add_odrTask_ot_edt.setText(bundle.getString("OT"));
            add_odrTask_tree_comment_edt.setText(bundle.getString("TreeComment"));
            previousTreeComment = bundle.getString("TreeComment");  // Tree Comment
            previousTreeComment = previousTreeComment.trim();

            add_odrTask_pres_comment_edt.setText(bundle.getString("PrescirptionComment"));
            previousPrescriptioComment = bundle.getString("PrescirptionComment");  // Prescirption Comment
            previousPrescriptioComment = previousPrescriptioComment.trim();

            add_odrTask_alerts_adt.setText(bundle.getString("Alerts"));
            previousAlerts = bundle.getString("Alerts"); // Alerts
            previousAlerts = previousAlerts.trim();

            add_odrTask_notes_edt.setText(bundle.getString("Note"));
            previousNotes = bundle.getString("Note"); // Notes
            previousNotes = previousNotes.trim();

            if (bundle.getString("Hours").equals("1")) { // Customer At Location
                chk_customer_at_location.setChecked(true);
                previousCustomerAtLocation = "" + bundle.getString("Hours");
            } else {
                chk_customer_at_location.setChecked(false);
            }
            previousCustomerAtLocation = previousCustomerAtLocation.trim();

            int elementStatId = -1;
            for (int k = 0; k < mStatusTypeArryListId.size(); k++) {
                if (mStatusTypeArryListId.get(k).equals(bundle.getString("Status")))
                    elementStatId = k;
            }
            if (!bundle.getString("Status").equals("") && bundle.getString("Status") != null && !bundle.getString("Status").equals("null")) {
                odr_tree_status.setText(mStatusTypeArryList.get(elementStatId));
                odr_tree_status.setTag(Integer.parseInt(bundle.getString("Status")));
                previousStatus = bundle.getString("Status");   // Status
                previousStatus = previousStatus.trim();
            } else {
                odr_tree_status.setText(mStatusTypeArryList.get(0));        // if not get status value set empty text
                odr_tree_status.setTag(0);            // if not get status value set tag -1
                previousStatus = "0";
                previousStatus = previousStatus.trim();
            }

            // Tree Crew Type
            if (bundle.getString("Owner") != null && !bundle.getString("Owner").equals("") && !bundle.getString("Owner").equals("null")) {
                odr_tree_crew_type.setText(mTreeCrewTypeArryList.get(Integer.parseInt(bundle.getString("Owner"))));    // Tree Crew Type
                odr_tree_crew_type.setTag(Integer.parseInt(bundle.getString("Owner")));
                previousTreeCrewType = bundle.getString("Owner"); // Tree Crew Type
                previousTreeCrewType = previousTreeCrewType.trim();
            } else {
                odr_tree_crew_type.setText("");  // if Tree Crew Type not get any value set empty text
            }

            // Restriction
            if (!bundle.getString("TandM").toString().equals("") && bundle.getString("TandM").toString() != null && !bundle.getString("TandM").toString().equals("null")) {
                String resId = bundle.getString("TandM");
                String resName = "";
                String[] strResList = resId.split("\\|", -1);
                for (int i = 0; i < strResList.length; i++) {
                    if (i == 0) {
                        resName = mRestrictionArryList.get(Integer.parseInt(strResList[i].toString()));
                    } else {
                        resName += ", " + mRestrictionArryList.get(Integer.parseInt(strResList[i].toString()));
                    }
                }
                odr_tree_restriction.setText(resName);
                odr_tree_restriction.setTag(resId);

                previousRestriction = bundle.getString("TandM"); // Restriction
                previousRestriction = previousRestriction.trim();
            } else {
                odr_tree_restriction.setText("");        // if nothing selected then set empty text and tag -1
                odr_tree_restriction.setTag(-1);        // if nothing selected then set tag -1
                previousRestriction = "-1";
                previousRestriction = previousRestriction.trim();
            }

            // Notification
            if (!bundle.getString("OT").equals("") && bundle.getString("OT") != null && !bundle.getString("OT").equals("null")) {
                odr_tree_notification.setText(mNotificationArryList.get(Integer.parseInt(bundle.getString("OT"))));
                odr_tree_notification.setTag(Integer.parseInt(bundle.getString("OT")));

                previousNotification = bundle.getString("OT");
                previousNotification = previousNotification.trim();
            } else {
                odr_tree_notification.setText("");
                odr_tree_notification.setTag(-1);
                previousNotification = "-1";
                previousNotification = previousNotification.trim();
            }

            if (bundle.getString("TreeGeo") != null && !bundle.getString("TreeGeo").equals("")) {
                add_odrTask_record_tree_geocode_txtvw.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
                add_odrTask_record_tree_geocode_txtvw.setCompoundDrawablePadding(5);
                add_odrTask_record_tree_geocode_txtvw.setTag("");
                treeGeo = bundle.getString("TreeGeo");
                previousLogTreeGeoCode = bundle.getString("TreeGeo");
                previousLogTreeGeoCode = previousLogTreeGeoCode.trim();
            }

            add_odrTask_access_complexity_value_txtvw.setText(bundle.getString("AccessComplexity"));
            add_odrTask_access_complexity.setProgress(Integer.valueOf(bundle.getString("AccessComplexity")) - 1);
            add_odrTask_setup_complexity_value_txtvw.setText(bundle.getString("SetupComplexity"));
            add_odrTask_setup_complexity_skbar.setProgress(Integer.valueOf(bundle.getString("SetupComplexity")) - 1);
            add_odrTask_perscription_complexity_value_txtvw.setText(bundle.getString("PrescriptionComplexity"));
            add_odrTask_pres_complexity_skbar.setProgress(Integer.valueOf(bundle.getString("PrescriptionComplexity")) - 1);
            add_odrTask_way_point_skbar.setProgress(0);
            bundle.getString("AccessPath");
        } else {
            Entry<Long, ServiceType> entryService = mapOrderTask.entrySet().iterator().next();
            add_odrTask_tree_species_txtvw.setText(entryService.getValue().getNm());
            add_odrTask_tree_species_txtvw.setTag(entryService.getValue().getId());

            add_odrTask_trim_type_txtvw.setText(mTrimTypeArryList.get(0));
            add_odrTask_trim_type_txtvw.setTag(mTrimTypeArryListId.get(0));

            Entry<Long, OrderPriority> entry = mapPriority.entrySet().iterator().next();
            add_odrTask_priority_txtvw.setText(entry.getValue().getNm());
            add_odrTask_priority_txtvw.setTag(entry.getValue().getId());

            odr_tree_status.setText(mStatusTypeArryList.get(0));
            odr_tree_status.setTag(0);
            odr_tree_crew_type.setText("");
            odr_tree_restriction.setText("");
            odr_tree_restriction.setTag(-1);
            odr_tree_notification.setText("");
            odr_tree_notification.setTag(-1);
        }

        odr_tree_status.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                showCustomDialog(mStatusTypeArryList, "Status", 1);
            }
        });


        odr_tree_crew_type.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                showCustomDialog(mTreeCrewTypeArryList, "Tree Crew Type", 2);
            }
        });

        odr_tree_restriction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                String Id = odr_tree_restriction.getTag().toString();
                showRestrictionDialog(Id);
            }
        });

        odr_tree_notification.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                showCustomDialog(mNotificationArryList, "Notification", 4);
            }
        });


        add_odrTask_tree_species_txtvw.setOnClickListener(new OnClickListener() {    //YD Treespecies
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                if (BaseTabActivity.mCurrentTab.equals("Map")) {
                    AddTaskSpeciesFrag taskSpeciesFrag = new AddTaskSpeciesFrag();
                    mActivity.pushFragments(Utilities.MAP, taskSpeciesFrag, true, true, BaseTabActivity.UI_Thread);
                } else if (BaseTabActivity.mCurrentTab.equals("Jobs")) {
                    AddTaskSpeciesFrag taskSpeciesFrag = new AddTaskSpeciesFrag();
                    mActivity.pushFragments(Utilities.JOBS, taskSpeciesFrag, true, true, BaseTabActivity.UI_Thread);
                }
            }
        });

        add_odrTask_trim_type_txtvw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                showOrderTrimTypeDialog();
            }
        });

        add_odrTask_priority_txtvw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                showOrderPriorityDialog();
            }
        });

        add_odrTask_record_tree_geocode_txtvw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                hideSoftKeyboard();

                if (recordTreeGeoCode) {
                    add_odrTask_record_tree_geocode_txtvw.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
                    add_odrTask_record_tree_geocode_txtvw.setCompoundDrawablePadding(5);
                    add_odrTask_record_tree_geocode_txtvw.setTag("");
                    recordTreeGeoCode = false;
                    treeGeo = Utilities.getLocation(mActivity.getApplicationContext());
                } else {
                    dialog = new MyDialog(mActivity, "Confirmation", "Do you want to continue?", "DELETE");
                    dialog.setkeyListender(new MyDiologInterface() {

                        @Override
                        public void onPositiveClick() throws JSONException {
                            dialog.dismiss();//YD dismissing because swithched ok button from left to right side
                        }

                        @Override
                        public void onNegativeClick() {
                            treeGeo = Utilities.getLocation(mActivity.getApplicationContext());
                            dialog.dismiss();
                        }
                    });
                    dialog.onCreate(null);
                    dialog.show();
                }

            }
        });

        add_odrTask_log_way_point_txtvw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                String currentGeoLocation = Utilities.getLocation(mContext);
            }
        });


        add_odrTask_log_access_path_txtvw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                if (logAccesspath) {
                    add_odrTask_log_access_path_txtvw.setBackgroundResource(R.drawable.rounded_green_fill_bg);
                    add_odrTask_log_access_path_txtvw.setText("Log Access Point");
                    logAccesspath = false;
                } else {
                    add_odrTask_log_access_path_txtvw.setBackgroundResource(R.drawable.rounded_red_fill_bg);
                    add_odrTask_log_access_path_txtvw.setText("Stop Access Point");
                    logAccesspath = true;
                }
            }
        });

        add_odrTask_way_point_list_txtvw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();

                String accessPath = null;

                String listAccPath = null;
                if (accessTreeGeo != null) {
                    for (int m = 0; m < accessTreeGeo.size(); m++) {
                        listAccPath = accessTreeGeo.get(m);
                    }
                }
                accessPath = listAccPath;
                if (!treeGeo.equals(""))
                    accessPath += String.valueOf("#" + treeGeo);
                else
                    accessPath += String.valueOf("#" + null);

                HashMap<Long, Order> orderMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
                Order odrObj = orderMap.get(Long.parseLong(currentOdrId));
                accessPath += String.valueOf("#" + odrObj.getCustSiteGeocode());
                String currentGeoLocation = Utilities.getLocation(mContext);

                if (Utilities.checkInternetConnection(mContext, false)) {
                    GoogleMapFragment.maptype = "AccessPathList";
                    GoogleMapFragment.setAccesspathpointsForTreeMap(accessPath);

                    GoogleMapFragment mFragment = new GoogleMapFragment();
                    mActivity.pushFragments(Utilities.JOBS, mFragment, true, true, BaseTabActivity.UI_Thread);
                } /*else {
                    MapAllFragment.maptype = "AccessPathList";
                    MapAllFragment.setAccesspathpointsForTreeMap(accessPath);

                    MapAllFragment mFragment = new MapAllFragment();
                    mActivity.pushFragments(Utilities.JOBS, mFragment, true, true, BaseTabActivity.UI_Thread);
                }*///YD 2020
                //	Log.e("Latitude-Longitude :", String.valueOf(currentGeoLocation));
            }
        });

        add_odrTask_snap_it_txtvw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                if (requestType.equals("ADD TREE"))
                    openCamera(currentOdrId, "0", "Addsave");
                else if (requestType.equals("EDIT TREE"))
                    openCamera(currentOdrId, taskId, "Editsave");
            }
        });


        add_odrTask_snaps_txtvw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                Gridview_MainActivity gridView_MainFrag = new Gridview_MainActivity();
                gridView_MainFrag.setActiveOrderObject(activeOrderObj);

                Bundle b = new Bundle();
                b.putString("OrderId", String.valueOf(activeOrderObj.getId()));
                b.putString("OrderName", String.valueOf(activeOrderObj.getNm()));
                b.putInt("fmetaType", 1);

                gridView_MainFrag.setArguments(b);
                mActivity.pushFragments(Utilities.JOBS, gridView_MainFrag, true, true, BaseTabActivity.UI_Thread);
            }
        });

        add_odrTask_access_complexity.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progressChanged = 1;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress + 1;
                add_odrTask_access_complexity_value_txtvw.setText(String.valueOf(progressChanged));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                //	Toast.makeText(mActivity,"seek bar progress:"+progressChanged, Toast.LENGTH_SHORT).show();
            }
        });

        add_odrTask_setup_complexity_skbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progressChanged = 1;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress + 1;
                add_odrTask_setup_complexity_value_txtvw.setText(String.valueOf(progressChanged));
            }
        });


        add_odrTask_pres_complexity_skbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progressChanged = 1;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress + 1;
                add_odrTask_perscription_complexity_value_txtvw.setText(String.valueOf(progressChanged));
            }

        });

        add_odrTask_way_point_skbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progressChanged = 1;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress + 1;
                add_odrTask_way_point_value_txtvw.setText(String.valueOf(progressChanged));
            }
        });

        parentView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                hideSoftKeyboard();
                return false;
            }
        });
    }

    private void sortPriorty(HashMap<Long, OrderPriority> odrPriorityListTemp) {
        long[] x = {0, 1, 4, 5, 6};

        for (int i = 0; i < x.length; i++) {
            mapPriority.put(x[i], odrPriorityListTemp.get(x[i]));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e("AceRoute", "onAttach called for addedittaskfrag");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("AceRoute", "onCreate1 called for addedittaskfrag");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("AceRoute", "onActivityCreated called for addedittaskfrag");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("AceRoute", "onResume called for addedittaskfrag");
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.e("AceRoute", "onPause called for addedittaskfrag");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("AceRoute", "onDestroyView called for addedittaskfrag");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("AceRoute", "onDestroy called for addedittaskfrag");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("AceRoute", "onStop called for addedittaskfrag");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("AceRoute", "onDetach called for addedittaskfrag");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("AceRoute", "onStart called for addedittaskfrag");
        if (PreferenceHandler.getCurtSteCustdatForcustList(mActivity) == 1) {
            PreferenceHandler.setCurtSteCustdatForcustList(mActivity, 0);
            String x = PreferenceHandler.getCustSiteData(mActivity);

            String data[] = x.split("#");
            String speciesId = "";
            String speciesNm = "";
            String blank = "";
            if (data.length > 0)
                speciesId = data[0];
            if (data.length > 1)
                speciesNm = data[1];
            if (data.length > 2)
                blank = data[2];

            add_odrTask_tree_species_txtvw.setText(speciesNm);
            add_odrTask_tree_species_txtvw.setTag(speciesId);

        }
    }

    private void showOrderTreeSpeciesDialog() {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity) mContext, android.R.layout.select_dialog_item, mTreeSpeciesArryList);
            AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mContext);
            builder.setTitle("Tree Species");
            builder.setCancelable(true);
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @SuppressLint("NewApi")
                public void onClick(DialogInterface dialog, int position) {
                    add_odrTask_tree_species_txtvw.setText(mTreeSpeciesArryList.get(position));
                    add_odrTask_tree_species_txtvw.setTag(mapOrderTask.get(Tkeys[position]).getId());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showOrderTrimTypeDialog() {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity) mContext, android.R.layout.select_dialog_item, mTrimTypeArryList);
            AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mContext);
            builder.setTitle("Trim Type");
            builder.setCancelable(true);
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @SuppressLint("NewApi")
                public void onClick(DialogInterface dialog, int position) {
                    add_odrTask_trim_type_txtvw.setText(mTrimTypeArryList.get(position));
                    add_odrTask_trim_type_txtvw.setTag(mTrimTypeArryListId.get(position));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showOrderPriorityDialog() {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity) mContext, android.R.layout.select_dialog_item, mPriorityArryList);
            AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mContext);
            builder.setTitle("Priority");
            builder.setCancelable(true);
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @SuppressLint("NewApi")
                public void onClick(DialogInterface dialog, int position) {

                    add_odrTask_priority_txtvw.setText(mPriorityArryList.get(position));
                    add_odrTask_priority_txtvw.setTag(Long.valueOf(mapPriority.get(Pkeys[position]).getId()));
                    Log.e("Id:", mapPriority.get(Pkeys[position]).getId() + " Value: " + mapPriority.get(Pkeys[position]).getNm());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRestrictionDialog(String selectedId) {
        try {
            seletedItems.clear();

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_multiple_choice, mRestrictionArryList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    TextView textView = (TextView) view.findViewById(android.R.id.text1);
                    textView.setTextSize(22);
                    textView.setTextColor(getResources().getColor(R.color.light_gray));

                    return view;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("Restriction");

            LayoutInflater inflater = mActivity.getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);
            builder.setView(dialogView);

            ListView listView = (ListView) dialogView.findViewById(R.id.list_dialog);

            listView.setAdapter(arrayAdapter);

            if (!selectedId.equals("-1")) {
                String[] strRestriction = selectedId.split("\\|", -1);

                for (int i = 0; i < strRestriction.length; i++) {
                    listView.setItemChecked(Integer.valueOf(strRestriction[i]), true);
                    seletedItems.add(Long.valueOf(strRestriction[i]));
                }
            }

            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
                    CheckedTextView item = (CheckedTextView) view;
                    if (item.isChecked()) {
                        seletedItems.add(id);
                    } else {
                        seletedItems.remove(id);
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
                    if (seletedItems.size() > 0) {
                        RestrictionLst = "";
                        String restrictionName = "";
                        for (int i = 0; i < seletedItems.size(); i++) {
                            if (i == 0) {
                                RestrictionLst = String.valueOf(seletedItems.get(i));
                                restrictionName = mRestrictionArryList.get(Integer.parseInt(seletedItems.get(i).toString()));
                            } else {
                                RestrictionLst += "|" + String.valueOf(seletedItems.get(i));
                                restrictionName += ", " + mRestrictionArryList.get(Integer.parseInt(seletedItems.get(i).toString()));
                            }
                        }
                        odr_tree_restriction.setText(restrictionName);
                        odr_tree_restriction.setTag(RestrictionLst);
                    } else {
                        odr_tree_restriction.setText(""); // If nothing selected set empty text
                        odr_tree_restriction.setTag(-1);
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
        } catch (Exception e) {
            Log.e(BaseTabActivity.LOGNAME, e.getMessage());
            e.printStackTrace();
        }
    }

    private void showCustomDialog(final ArrayList<String> mArryList, String strTitle, final int type) {
        final int mType = type;
        //	ArrayList<String> arrList = null;
        try {

            ///	arrList = new ArrayList<String>();
            //	arrList = mArryList;
            ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity) mContext, android.R.layout.select_dialog_item, mArryList);
            AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mContext);
            builder.setTitle(strTitle);
            builder.setCancelable(true);
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @SuppressLint("NewApi")
                public void onClick(DialogInterface dialog, int position) {
                    if (mType == 1) {
                        odr_tree_status.setText(mArryList.get(position));
                        odr_tree_status.setTag(mStatusTypeArryListId.get(position));
                    } else if (mType == 2) {
                        odr_tree_crew_type.setText(mArryList.get(position));
                        odr_tree_crew_type.setTag(position);
                    } else if (mType == 4) {
                        odr_tree_notification.setText(mArryList.get(position));
                        odr_tree_notification.setTag(position);
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

            Utilities.setDividerTitleColor(dialog, mheight, mActivity);

            Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            Utilities.setDefaultFont_12(button_negative);
            Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Utilities.setDefaultFont_12(button_positive);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addAccessPathforAdd() {
        String geoCode = "";
        geoCode = Utilities.getLocation(mActivity.getApplicationContext());

        accessTreeGeo.add(geoCode.trim());
        //  createaccesspathlist('order-tasks-add-page-access-geo');
        //handleListAccessGeo("order-tasks-add-page-access-geo-lst",handleAddGeoDelete,null,'Edit','pencil');
    }

    public String generatepipedaccesspath(ArrayList<String> accesspathlist) {
        String glist = "";
        for (int i = 0; i < accesspathlist.size(); i++) {
            if (i == 0)
                glist = accesspathlist.get(i);
            else
                glist = glist + "|" + accesspathlist.get(i);
        }
        return glist;
    }

    public void calculateOrderduration(String orderTaskId, String ct1, String ct2, String ct3) throws ParseException {
        int orderduration = 0;
        int nct1, nct2, nct3;
        orderTaskId = orderTaskId.trim();
        nct1 = Integer.valueOf(ct1);
        nct2 = Integer.valueOf(ct2);
        nct3 = Integer.valueOf(ct3);
        long norderduration = (nct1 + nct2 + nct3) * 5;


      /*  HashMap<Long, OrderTask> orderTaskXmlMap = (HashMap<Long, OrderTask>) DataObject.orderTasksXmlStore;
        if (orderTaskXmlMap != null) {
            Set<Entry<Long, OrderTask>> entrySet = orderTaskXmlMap.entrySet();
            for (Entry entry : entrySet) {
                OrderTask odrTaskObj = (OrderTask) entry.getValue();

                if ((Long.parseLong(orderTaskId)) == (odrTaskObj.getId())) {
                } else {
                    ct1 = odrTaskObj.getTree_ct1();
                    ct2 = odrTaskObj.getTree_ct2();
                    ct3 = odrTaskObj.getTree_ct3();
                    nct1 = Integer.valueOf(ct1);
                    nct2 = Integer.valueOf(ct2);
                    nct3 = Integer.valueOf(ct3);
                    norderduration = norderduration + (nct1 + nct2 + nct3) * 5;
                }
            }
        }*/

        if (norderduration < 15)
            norderduration = 15;

        long orderId = Long.parseLong(currentOdrId);
        HashMap<Long, Order> orderXmlMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
        Order orderObj = orderXmlMap.get(orderId);

        String start_date_utc = orderObj.getFromDate();//2015-07-15 4:30 -00:00 getting
        SimpleDateFormat convStrToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
        Date date = convStrToDate.parse(start_date_utc.trim());//Wed Jul 15 10:00:00 GMT+05:30 2015
        long orderStartTime = date.getTime();

        gorderEndTime = orderStartTime + norderduration * 60 * 1000;
        Date temporderEndDate = new Date(gorderEndTime);//Tue Jun 02 06:45:00 GMT+05:30 2015
        gorderEndDate = convertDateToUtc(gorderEndTime);

    }

    private String convertDateToUtc(long milliseconds) {
        Date date = new Date(milliseconds);

        SimpleDateFormat convStrToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");//have to send "2015/06/02 11:25 -00:00"
        convStrToDate.setTimeZone(TimeZone.getTimeZone("UTC"));

        String dateToSend = convStrToDate.format(date);
        dateToSend = dateToSend + " -00:00";
        return dateToSend;


    }

    private void doListViewSettings() {

        add_odr_odrLogPointList.setSwipeListViewListener(new BaseSwipeListViewListener() {

            private CustomDialog customDialog;

            @Override
            public void onOpened(int position, boolean toRight) {

                add_odr_odrLogPointList.closeAnimate(position);
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
                View rowItem = Utilities.getViewOfListByPosition(position,
                        add_odr_odrLogPointList);

                RelativeLayout backlayout = (RelativeLayout) rowItem
                        .findViewById(R.id.back);
                backlayout.setBackgroundColor(getResources().getColor(
                        R.color.color_white));
                TextView chat = (TextView) rowItem
                        .findViewById(R.id.back_view_chat_txtvw);
                TextView invite = (TextView) rowItem
                        .findViewById(R.id.back_view_invite_txtvw);
                chat.setVisibility(View.INVISIBLE);
                invite.setVisibility(View.INVISIBLE);

                if (fromRight) {
                } else {
                    customDialog = CustomDialog
                            .getInstance(
                                    mActivity,
                                    AddEditTaskOrderFragment.this,
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
                        add_odr_odrLogPointList);

                RelativeLayout backlayout = (RelativeLayout) rowItem
                        .findViewById(R.id.back);
                TextView chat = (TextView) rowItem
                        .findViewById(R.id.back_view_chat_txtvw);
                TextView invite = (TextView) rowItem
                        .findViewById(R.id.back_view_invite_txtvw);

                if (right) {
                    backlayout.setBackgroundColor(getResources()
                            .getColor(R.color.bdr_green));
                    chat.setVisibility(View.GONE);
                    invite.setVisibility(View.VISIBLE);

                } else {

                    backlayout.setBackgroundColor(getResources()

                            .getColor(R.color.color_red));
                    chat.setVisibility(View.VISIBLE);
                    invite.setVisibility(View.GONE);
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


        add_odr_odrLogPointList.setSwipeMode(SwipeListView.SWIPE_MODE_DEFAULT); // there

        add_odr_odrLogPointList
                .setSwipeActionLeft(SwipeListView.SWIPE_ACTION_NONE); // there

        add_odr_odrLogPointList
                .setSwipeActionRight(SwipeListView.SWIPE_ACTION_NONE);
        add_odr_odrLogPointList.setOffsetLeft(convertDpToPixel()); // left side
        // offset
        add_odr_odrLogPointList.setOffsetRight(convertDpToPixel()); // right
        // side
        // offset
        add_odr_odrLogPointList.setSwipeCloseAllItemsWhenMoveList(true);
        add_odr_odrLogPointList.setAnimationTime(100); // Animation time
        add_odr_odrLogPointList.setSwipeOpenOnLongPress(false);
    }

    public int convertDpToPixel() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
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
            if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED))) {
                if (response.getId() == SAVE_TASK) {
                    if (requestType.equals("ADD TREE")) {
//                        getTaskForCurrentOrder();
                        getAndUpdateNumberOfOrderParts(currentOdrId);
                    }
                    goBack(mActivity.SERVICE_Thread);
                }
                if (response.getId() == SAVEPICTURE) {
                    activeOrderObj.setImgCount(activeOrderObj.getImgCount() + 1);
                    activeOrderObj.setCustMetaCount(activeOrderObj.getCustMetaCount() + 1);
                }
                if (response.getId() == GET_CURRENT_ODR_TASKS) {

                    Log.i(BaseTabActivity.LOGNAME, "Response for currentorder task after saving task to DB");
                    HashMap<Long, OrderTask> orderTaskListMap = (HashMap<Long, OrderTask>) response.getResponseMap();
                    Long[] key = orderTaskListMap.keySet().toArray(new Long[orderTaskListMap.size()]);
                    int lastVal = 0;
                    String conStrFrT_M = "";

                    if (orderTaskListMap.size() > 1) {
                        for (int i = 0; i < orderTaskListMap.size(); i++) {
                            OrderTask odrTask;
                            odrTask = orderTaskListMap.get(key[i]);
                            if (Integer.valueOf(odrTask.getPriority()) > lastVal) {
                                lastVal = Integer.valueOf(odrTask.getPriority());
                            }
                            if (Integer.valueOf(odrTask.getTree_tm()) > 0) {
                                if (conStrFrT_M.equals(""))
                                    conStrFrT_M = mRestrictionArryList.get(Integer.valueOf(odrTask.getTree_tm()));
                                else {
                                    conStrFrT_M = conStrFrT_M + ", " + mRestrictionArryList.get(Integer.valueOf(odrTask.getTree_tm()));
                                }
                            }

                        }
                        int isDiff = activeOrderObj.getSummary().compareTo(conStrFrT_M);
                        if (lastVal > 2 || (isDiff != 0)) {
                            updateOrderRequest req = new updateOrderRequest();
                            req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(mContext) + "/mobi");
                            req.setType("post");
                            req.setId(String.valueOf(activeOrderObj.getId()));
                            req.setName("priority_AND_restriction");
                            req.setValue(String.valueOf(lastVal + "|" + conStrFrT_M));
                            req.setAction(Order.ACTION_SAVE_ORDER_FLD);

                            Order.saveOrderField(req, mActivity, this, SAVEORDERFIELD_STATUS_PRIORITY);
                        }
                    }
                }

            } else if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA))) {
                if (response.getId() == SAVE_TASK)
                    goBack(mActivity.SERVICE_Thread);
            }
        }

    }

   /* private void getTaskForCurrentOrder() {
        GetPart_TaskRequest req = new GetPart_TaskRequest();
        req.setAction(OrderTask.ACTION_GET_ORDER_TASK);
        req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(mContext) + "/mobi");
        req.setOid(String.valueOf(activeOrderObj.getId()));

        OrderTask.getData(req, mActivity, this, GET_CURRENT_ODR_TASKS);

    }*/

    @Override
    public void onActionOk(int requestCode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onActionCancel(int requestCode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onActionNeutral(int requestCode) {
        // TODO Auto-generated method stub

    }

    private void getAndUpdateNumberOfOrderParts(String orderId) {
        HashMap<Long, Order> orderMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
        Order odrObj = orderMap.get(Long.parseLong(orderId));
        //odrObj.setCustServiceCount(odrObj.getCustServiceCount() + 1);  //YD 2020 ordertask is not in app anymore

    }

    // YD code to open camera

    @Override
    public void headerClickListener(String callingId) {
        // TODO Auto-generated method stub
        if (callingId.equals(mActivity.HeaderDonePressed)) {
            if (odr_tree_crew_type.getText() != null && !odr_tree_crew_type.getText().equals("")) {
                int chk = chk_customer_at_location.isChecked() ? 1 : 0;

                String height = "";
                if (add_odrTask_Height_edt.getText().toString() != null && add_odrTask_Height_edt.getText().toString() != "")
                    height = add_odrTask_Height_edt.getText().toString();
                else height = "";

                String diameter = "";
                if (add_odrTask_diametere_edt.getText().toString() != null && add_odrTask_diametere_edt.getText().toString() != "")
                    diameter = add_odrTask_diametere_edt.getText().toString();
                else diameter = "";

                String hvClear = "";
                if (add_odrTask_hv_clearance.getText().toString() != null && add_odrTask_hv_clearance.getText().toString() != "")
                    hvClear = add_odrTask_hv_clearance.getText().toString();
                else hvClear = "";

                String cycle = "";
                if (add_odrTask_cycle_edt.getText().toString() != null && add_odrTask_cycle_edt.getText().toString() != "")
                    cycle = add_odrTask_cycle_edt.getText().toString();
                else cycle = "";

                String estHrs = "";
                if (add_odrTask_estimated_count_edt.getText().toString() != null && add_odrTask_estimated_count_edt.getText().toString() != "")
                    estHrs = add_odrTask_estimated_count_edt.getText().toString();
                else estHrs = "";

                String ActHrs = "";
                if (add_odrTask_actual_count_edt.getText().toString() != null && !add_odrTask_actual_count_edt.getText().toString().equals(""))
                    ActHrs = add_odrTask_actual_count_edt.getText().toString();
                else
                    ActHrs = "0";

                String treeComm = "";
                if (add_odrTask_tree_comment_edt.getText().toString() != null && add_odrTask_tree_comment_edt.getText().toString() != "")
                    treeComm = add_odrTask_tree_comment_edt.getText().toString();
                else treeComm = "";

                String prescComm = "";
                if (add_odrTask_pres_comment_edt.getText().toString() != null && add_odrTask_pres_comment_edt.getText().toString() != "")
                    prescComm = add_odrTask_pres_comment_edt.getText().toString();
                else prescComm = "";

                String treeAlert = "";
                if (add_odrTask_alerts_adt.getText().toString() != null && add_odrTask_alerts_adt.getText().toString() != "")
                    treeAlert = add_odrTask_alerts_adt.getText().toString();
                else treeAlert = "";

                String treeNotes = "";
                if (add_odrTask_notes_edt.getText().toString() != null && add_odrTask_notes_edt.getText().toString() != "")
                    treeNotes = add_odrTask_notes_edt.getText().toString();
                else treeNotes = "";
                Save_DeleteTaskRequest req = new Save_DeleteTaskRequest();
                req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(mContext) + "/mobi");
                req.setType("post");
                req.setAction(OrderTask.ACTION_SAVE_ORDER_TASK);

                SaveTaskDataRequest dataReq = new SaveTaskDataRequest();
                dataReq.setOrderId(currentOdrId);
                if (requestType.equals("ADD TREE"))
                    dataReq.setTaskId("0");
                else if (requestType.equals("EDIT TREE"))
                    dataReq.setTaskId(taskId);

                dataReq.setTaskTypeId(add_odrTask_tree_species_txtvw.getTag().toString());
                dataReq.setTreeWorkPresc(add_odrTask_trim_type_txtvw.getTag().toString());
                dataReq.setTree_priority(String.valueOf(add_odrTask_priority_txtvw.getTag()));

                //	dataReq.setTree_action_stat(String.valueOf(chk));
                //	dataReq.setTree_owner(owner);
                if (!odr_tree_status.getTag().toString().equals("-1"))
                    dataReq.setTree_action_stat(odr_tree_status.getTag().toString());
                else
                    dataReq.setTree_action_stat("");
                if (!odr_tree_crew_type.getTag().toString().equals("-1"))
                    dataReq.setTree_owner(odr_tree_crew_type.getTag().toString());
                else
                    dataReq.setTree_owner("");

                dataReq.setTree_ht(height);
                dataReq.setTree_dia(diameter);
                dataReq.setTree_clearance(hvClear);
                dataReq.setTree_cycle(cycle);
                dataReq.setEstimated_hrs(estHrs);
                dataReq.setActual_hrs(ActHrs);
                //	dataReq.setTree_time_spent(TimeSpent);
                //	dataReq.setTreeT_M(treeT_M); //
                dataReq.setTree_time_spent(String.valueOf(chk));
                //dataReq.setTreeT_M(odr_tree_restriction.getTag().toString()); //YD earlier for single
                if (!odr_tree_restriction.getTag().toString().equals("-1"))
                    dataReq.setTreeT_M(odr_tree_restriction.getTag().toString());
                else
                    dataReq.setTreeT_M("");

                //	dataReq.setTreeT_M(RestrictionLst); // MY
                if (!odr_tree_notification.getTag().toString().equals("-1"))
                    dataReq.setTree_Msc(odr_tree_notification.getTag().toString());
                else
                    dataReq.setTree_Msc("");
                dataReq.setTree_comment(treeComm);
                dataReq.setTree_presc_comm(prescComm);
                dataReq.setTree_Alert(treeAlert);
                dataReq.setTree_Notes(treeNotes);

                //	String ct1 = add_odrTask_access_complexity_value_txtvw.getText().toString();
                //	dataReq.setTree_access_comp(ct1);
                String ct1 = "0";
                dataReq.setTree_access_comp(ct1);

                //	String ct2 = add_odrTask_setup_complexity_value_txtvw.getText().toString();
                //	dataReq.setTree_setup_comp(ct2);
                String ct2 = "0";
                dataReq.setTree_setup_comp(ct2);

                //	String ct3 = add_odrTask_perscription_complexity_value_txtvw.getText().toString();
                //	dataReq.setTree_pres_comp(ct3);
                String ct3 = "0";
                dataReq.setTree_pres_comp(ct3);

                if (treeGeo != null && !treeGeo.equals(""))
                    dataReq.setTree_geo(treeGeo);
                else
                    dataReq.setTree_geo(activeOrderObj.getCustSiteGeocode());

                if (accessTreeGeo != null)
                    dataReq.setTree_access_pathLst(generatepipedaccesspath(accessTreeGeo));
                else
                    dataReq.setTree_access_pathLst("");


                try {
                    if (requestType.equals("ADD TREE"))
                        calculateOrderduration("0", ct1, ct2, ct3);
                    else if (requestType.equals("EDIT TREE"))
                        calculateOrderduration(taskId, ct1, ct2, ct3);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    Log.e(BaseTabActivity.LOGNAME, e.getMessage());
                    e.printStackTrace();
                }

                dataReq.setOrderEndTime(String.valueOf(gorderEndTime));
                dataReq.setOrderEndDate(gorderEndDate);

                dataReq.setStmp(String.valueOf(Utilities.getCurrentTime()));
                dataReq.setAction(OrderTask.ACTION_SAVE_ORDER_TASK);

                req.setDataObj(dataReq);
                OrderTask.saveData(req, mActivity, AddEditTaskOrderFragment.this, SAVE_TASK);

                webviewAddOrderTask.setVisibility(View.VISIBLE);
            } else {
                try {
                    String D_title = getResources().getString(R.string.msg_required_field);
                    String D_desc = "Please select Tree Crew Type";
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
                    Log.e(BaseTabActivity.LOGNAME, e.getMessage());
                    e.printStackTrace();
                }
            }
            hideSoftKeyboard();
        } else if (callingId.equals(BaseTabActivity.HeaderBackPressed)) {
            if (requestType.equals("EDIT TREE")) {
                // Tree Species
                String currentTreeSpecies = add_odrTask_tree_species_txtvw.getTag().toString();
                currentTreeSpecies = currentTreeSpecies.trim();
                // Trim Type
                String currentTrimType = add_odrTask_trim_type_txtvw.getTag().toString();
                currentTrimType = currentTrimType.trim();
                // Priority
                String currentPriority = add_odrTask_priority_txtvw.getTag().toString();
                currentPriority = currentPriority.trim();
                // Estimated Count
                String currentEstCount = add_odrTask_estimated_count_edt.getText().toString();
                currentEstCount = currentEstCount.trim();
                // Actual Count
                String currentActualCount = "0";
                if (add_odrTask_actual_count_edt.getText().toString() != null && !add_odrTask_actual_count_edt.getText().toString().equals("")) {
                    currentActualCount = add_odrTask_actual_count_edt.getText().toString();
                }
                currentActualCount = currentActualCount.trim();
                // Tree Height
                String currentHeight = add_odrTask_Height_edt.getText().toString();
                currentHeight = currentHeight.trim();
                // Diameter BH
                String currentDiameterBH = add_odrTask_diametere_edt.getText().toString();
                currentDiameterBH = currentDiameterBH.trim();
                // HV Clearance
                String currentHVCleareance = add_odrTask_hv_clearance.getText().toString();
                currentHVCleareance = currentHVCleareance.trim();
                // Cycle
                String currentCycle = add_odrTask_cycle_edt.getText().toString();
                currentCycle = currentCycle.trim();
                // Tree Comment
                String currentTreeComment = add_odrTask_tree_comment_edt.getText().toString();
                currentTreeComment = currentTreeComment.trim();
                // Prescirption Comment
                String currentPresciptionComment = add_odrTask_pres_comment_edt.getText().toString();
                currentPresciptionComment = currentPresciptionComment.trim();
                // Alerts
                String currentAlert = add_odrTask_alerts_adt.getText().toString();
                currentAlert = currentAlert.trim();
                // Notes
                String currentNotes = add_odrTask_notes_edt.getText().toString();
                currentNotes = currentNotes.trim();
                //	Customer At Location
                int chk = chk_customer_at_location.isChecked() ? 1 : 0;
                String currentCustomerAtLocation = "" + chk;
                currentCustomerAtLocation = currentCustomerAtLocation.trim();
                // Status
                String currentStatus = odr_tree_status.getTag().toString();
                currentStatus = currentStatus.trim();
                // Tree Crew Type
                String currentTreeCrewType = "";
                if (odr_tree_crew_type.getTag() != null && !odr_tree_crew_type.getTag().equals("") && !odr_tree_crew_type.getTag().equals("null")) {
                    currentTreeCrewType = odr_tree_crew_type.getTag().toString();
                    currentTreeCrewType = currentTreeCrewType.trim();
                }
                // Restriction
                String currentRestriction = odr_tree_restriction.getTag().toString();
                currentRestriction = currentRestriction.trim();
                // Notification
                String currentNotification = odr_tree_notification.getTag().toString();
                currentNotification = currentNotification.trim();
                // Log Tree GeoCode
                String currentLogTreeGeoCode = "";
                if (treeGeo != null && !treeGeo.equals(""))
                    currentLogTreeGeoCode = treeGeo;
                else
                    currentLogTreeGeoCode = activeOrderObj.getCustSiteGeocode();
                currentLogTreeGeoCode = currentLogTreeGeoCode.trim();

                String previousValues = previousTreeSpecies + "||" + previousTrimType + "||" + previousPriority + "||" + previousStatus + "||" + previousTreeCrewType + "||" + previousCustomerAtLocation + "||" + previousRestriction
                        + "||" + previousNotification + "||" + previousEstimatedCount + "||" + previousActualCount + "||" + previousHeight + "||" + previousDiameterBh + "||" + previousHvClearance + "||" + previousCycle + "||" + previousTreeComment
                        + "||" + previousPrescriptioComment + "||" + previousAlerts + "||" + previousNotes + "||" + previousLogTreeGeoCode + "||" + "-";
                String newValues = currentTreeSpecies + "||" + currentTrimType + "||" + currentPriority + "||" + currentStatus + "||" + currentTreeCrewType + "||" + currentCustomerAtLocation + "||" + currentRestriction
                        + "||" + currentNotification + "||" + currentEstCount + "||" + currentActualCount + "||" + currentHeight + "||" + currentDiameterBH + "||" + currentHVCleareance + "||" + currentCycle + "||" + currentTreeComment
                        + "||" + currentPresciptionComment + "||" + currentAlert + "||" + currentNotes + "||" + currentLogTreeGeoCode + "||" + "-";

                String oldValStr[] = previousValues.toString().split("\\||");
                String newValStr[] = newValues.split("\\||");

                for (int k = 0; k < oldValStr.length; k++) {
                    if (!(oldValStr[k].equals(newValStr[k]))) {
                        isDifferent = true;
                        fieldUpdated = 1;
                        break;
                    }
                }

                if (isDifferent == true) {
                    dialog = new MyDialog(mActivity, getResources().getString(R.string.lbl_tree_updates), getResources().getString(R.string.lbl_upd_message), "YES");
                    dialog.setkeyListender(new MyDiologInterface() {

                        @Override
                        public void onPositiveClick() throws JSONException {
                            isDifferent = false;
                            //	fieldUpdated = 0;
                            dialog.dismiss(); //MY dismissing because swithched ok button from left to right side
                            goBack(mActivity.SERVICE_Thread);
                        }

                        @Override
                        public void onNegativeClick() {
                            if (odr_tree_crew_type.getText() != null && !odr_tree_crew_type.getText().equals("")) {

                                int chk = chk_customer_at_location.isChecked() ? 1 : 0;

                                String height = "";
                                if (add_odrTask_Height_edt.getText().toString() != null && add_odrTask_Height_edt.getText().toString() != "")
                                    height = add_odrTask_Height_edt.getText().toString();
                                else height = "";

                                String diameter = "";
                                if (add_odrTask_diametere_edt.getText().toString() != null && add_odrTask_diametere_edt.getText().toString() != "")
                                    diameter = add_odrTask_diametere_edt.getText().toString();
                                else diameter = "";

                                String hvClear = "";
                                if (add_odrTask_hv_clearance.getText().toString() != null && add_odrTask_hv_clearance.getText().toString() != "")
                                    hvClear = add_odrTask_hv_clearance.getText().toString();
                                else hvClear = "";

                                String cycle = "";
                                if (add_odrTask_cycle_edt.getText().toString() != null && add_odrTask_cycle_edt.getText().toString() != "")
                                    cycle = add_odrTask_cycle_edt.getText().toString();
                                else cycle = "";

                                String estHrs = "";
                                if (add_odrTask_estimated_count_edt.getText().toString() != null && add_odrTask_estimated_count_edt.getText().toString() != "")
                                    estHrs = add_odrTask_estimated_count_edt.getText().toString();
                                else estHrs = "";

                                String ActHrs = "";
                                if (add_odrTask_actual_count_edt.getText().toString() != null && !add_odrTask_actual_count_edt.getText().toString().equals(""))
                                    ActHrs = add_odrTask_actual_count_edt.getText().toString();
                                else
                                    ActHrs = "0";
                                String treeComm = "";
                                if (add_odrTask_tree_comment_edt.getText().toString() != null && add_odrTask_tree_comment_edt.getText().toString() != "")
                                    treeComm = add_odrTask_tree_comment_edt.getText().toString();
                                else treeComm = "";

                                String prescComm = "";
                                if (add_odrTask_pres_comment_edt.getText().toString() != null && add_odrTask_pres_comment_edt.getText().toString() != "")
                                    prescComm = add_odrTask_pres_comment_edt.getText().toString();
                                else prescComm = "";

                                String treeAlert = "";
                                if (add_odrTask_alerts_adt.getText().toString() != null && add_odrTask_alerts_adt.getText().toString() != "")
                                    treeAlert = add_odrTask_alerts_adt.getText().toString();
                                else treeAlert = "";

                                String treeNotes = "";
                                if (add_odrTask_notes_edt.getText().toString() != null && add_odrTask_notes_edt.getText().toString() != "")
                                    treeNotes = add_odrTask_notes_edt.getText().toString();
                                else treeNotes = "";

                                Save_DeleteTaskRequest req = new Save_DeleteTaskRequest();
                                req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(mContext) + "/mobi");
                                req.setType("post");
                                req.setAction(OrderTask.ACTION_SAVE_ORDER_TASK);

                                SaveTaskDataRequest dataReq = new SaveTaskDataRequest();
                                dataReq.setOrderId(currentOdrId);
                                if (requestType.equals("ADD TREE"))
                                    dataReq.setTaskId("0");
                                else if (requestType.equals("EDIT TREE"))
                                    dataReq.setTaskId(taskId);

                                dataReq.setTaskTypeId(add_odrTask_tree_species_txtvw.getTag().toString());
                                dataReq.setTreeWorkPresc(add_odrTask_trim_type_txtvw.getTag().toString());
                                dataReq.setTree_priority(String.valueOf(add_odrTask_priority_txtvw.getTag()));

                                //	dataReq.setTree_action_stat(String.valueOf(chk));
                                //	dataReq.setTree_owner(owner);
                                if (!odr_tree_status.getTag().toString().equals("-1"))
                                    dataReq.setTree_action_stat(odr_tree_status.getTag().toString());
                                else
                                    dataReq.setTree_action_stat("");
                                if (!odr_tree_crew_type.getTag().toString().equals("-1"))
                                    dataReq.setTree_owner(odr_tree_crew_type.getTag().toString());
                                else
                                    dataReq.setTree_owner("");

                                dataReq.setTree_ht(height);
                                dataReq.setTree_dia(diameter);
                                dataReq.setTree_clearance(hvClear);
                                dataReq.setTree_cycle(cycle);
                                dataReq.setEstimated_hrs(estHrs);
                                dataReq.setActual_hrs(ActHrs);
                                //	dataReq.setTree_time_spent(TimeSpent);
                                //	dataReq.setTreeT_M(treeT_M); //
                                dataReq.setTree_time_spent(String.valueOf(chk));
                                //dataReq.setTreeT_M(odr_tree_restriction.getTag().toString()); //YD earlier for single
                                if (!odr_tree_restriction.getTag().toString().equals("-1"))
                                    dataReq.setTreeT_M(odr_tree_restriction.getTag().toString());
                                else
                                    dataReq.setTreeT_M("");

                                //	dataReq.setTreeT_M(RestrictionLst); // MY
                                if (!odr_tree_notification.getTag().toString().equals("-1"))
                                    dataReq.setTree_Msc(odr_tree_notification.getTag().toString());
                                else
                                    dataReq.setTree_Msc("");
                                dataReq.setTree_comment(treeComm);
                                dataReq.setTree_presc_comm(prescComm);
                                dataReq.setTree_Alert(treeAlert);
                                dataReq.setTree_Notes(treeNotes);

                                //	String ct1 = add_odrTask_access_complexity_value_txtvw.getText().toString();
                                //	dataReq.setTree_access_comp(ct1);
                                String ct1 = "0";
                                dataReq.setTree_access_comp(ct1);

                                //	String ct2 = add_odrTask_setup_complexity_value_txtvw.getText().toString();
                                //	dataReq.setTree_setup_comp(ct2);
                                String ct2 = "0";
                                dataReq.setTree_setup_comp(ct2);

                                //	String ct3 = add_odrTask_perscription_complexity_value_txtvw.getText().toString();
                                //	dataReq.setTree_pres_comp(ct3);
                                String ct3 = "0";
                                dataReq.setTree_pres_comp(ct3);

                                if (treeGeo != null && !treeGeo.equals(""))
                                    dataReq.setTree_geo(treeGeo);
                                else
                                    dataReq.setTree_geo(activeOrderObj.getCustSiteGeocode());

                                if (accessTreeGeo != null)
                                    dataReq.setTree_access_pathLst(generatepipedaccesspath(accessTreeGeo));
                                else
                                    dataReq.setTree_access_pathLst("");

                                try {
                                    if (requestType.equals("ADD TREE"))
                                        calculateOrderduration("0", ct1, ct2, ct3);
                                    else if (requestType.equals("EDIT TREE"))
                                        calculateOrderduration(taskId, ct1, ct2, ct3);
                                } catch (ParseException e) {
                                    // TODO Auto-generated catch block
                                    Log.e(BaseTabActivity.LOGNAME, e.getMessage());
                                    e.printStackTrace();
                                }

                                dataReq.setOrderEndTime(String.valueOf(gorderEndTime));
                                dataReq.setOrderEndDate(gorderEndDate);

                                dataReq.setStmp(String.valueOf(Utilities.getCurrentTime()));
                                dataReq.setAction(OrderTask.ACTION_SAVE_ORDER_TASK);

                                req.setDataObj(dataReq);
                                OrderTask.saveData(req, mActivity, AddEditTaskOrderFragment.this, SAVE_TASK);

                                webviewAddOrderTask.setVisibility(View.VISIBLE);
                            } else {
                                try {
                                    String D_title = getResources().getString(R.string.msg_required_field);
                                    String D_desc = "Please select Tree Crew Type";
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
                                    Log.e(BaseTabActivity.LOGNAME, e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                            //	fieldUpdated = 0;
                            //	goBack(mActivity.SERVICE_Thread);
                            isDifferent = false;
                            dialog.dismiss();
                        }
                    });
                    dialog.onCreate(null);
                    dialog.show();
                }
            } else {
                fieldUpdated = 0;
            }
        }

    }

    private void goBack(int threadType) {
        mActivity.popFragments(threadType);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webviewAddOrderTask.destroy();
            }
        });
        fieldUpdated = 0;
    }

    public void openCamera(String order_id, String taskId, String typeOfsav) {
        final String ordercopy = order_id;
        orderIdTaskPic = order_id;
        taskIdTaskPic = taskId;
        typeSaveTaskPic = typeOfsav;
        Intent cameraIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);

        String compfilename = null;
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            compfilename = Environment
//                    .getExternalStorageDirectory()
//                    .getAbsolutePath()
//                    + "/AceRoute";
//        } else {
//            compfilename = Environment.getDataDirectory()
//                    .getAbsolutePath() + "/AceRoute";
//        }
        compfilename = getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath()
                + "/AceRoute";
        File file = new File(compfilename);

        long time = System.currentTimeMillis();

        file.delete();

        try {
            file.mkdirs();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        String basepath = file.getAbsolutePath();

        try {
            file = new File(basepath + "/temp.jpg");
            if (file.delete()) {
                file.createNewFile();
            } else {
                if (typeOfsav.equals("Editsave"))
                    file = new File(basepath + "/" + taskId + ".jpg");
                else if (typeOfsav.equals("Addsave"))
                    file = new File(basepath + "/temp_" + time + ".jpg");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("check",
                "path:" + file.getAbsolutePath());
        Uri uri = Uri.fromFile(file);
        Log.i("check",
                "uri:" + uri.toString());
        this.uri = uri;

        PreferenceHandler.setUriOfPic_sign(mActivity, uri.toString());
        String urinew = PreferenceHandler.getUriOfPic_sign(mActivity);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        cameraIntent
                .putExtra(
                        MediaStore.EXTRA_SCREEN_ORIENTATION,
                        getResources().getConfiguration().orientation);
        int height = Utilities
                .getDisplayHeigth(mActivity.getApplicationContext());
        int width = Utilities
                .getDisplayWidth(mActivity.getApplicationContext());
        cameraIntent.putExtra("aspectX", width);
        cameraIntent.putExtra("aspectY", height);
        cameraIntent.putExtra("outputX", width);
        cameraIntent.putExtra("outputY", height);
        cameraIntent.putExtra("OrderId", ordercopy);
        startActivityForResult(cameraIntent,
                REQUEST_CODE_ACTIVITY_PICK_CAMERA);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        Uri selectedImage;
        if (resultCode == mActivity.RESULT_OK) {
            switch (requestCode) {

                case REQUEST_CODE_ACTIVITY_PICK_CAMERA:
                    selectedImage = uri;
                    if (selectedImage != null) {
                        try {
                            String orderId = orderIdTaskPic;

                            // calculating size of an image
                            File f = new File(selectedImage.getPath());
                            long sizeofImg = f.length();
                            if (typeSaveTaskPic.equals("Addsave")) {
                                // listOfUri.add(uri.getPath()); use this when saving image as taskid name // to do YD
                                doActioncb(this.uri, "CAM");// temporary YD
                            } else if (typeSaveTaskPic.equals("Editsave"))
                                doActioncb(uri, "CAM");

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.i(Utilities.TAG,
                                "Error : uri null.");
                    }
                    break;
            }
        } else {
            Log.e(Utilities.TAG,
                    "RESULT CODE ERROR for request code : " + requestCode);
            WebviewFragment.cwv.loadUrl("javascript:doActioncb("
                    + SplashII.REQUEST_CODE_SHOW_PICK_IMAGE_OPTIONS + ","
                    + JSONHandler.JSON_FAILURE + JSONHandler.JSON_END + ")");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void doActioncb(Uri data, String type) {

        String actualImgSav = "";
        if (type.equals("CAM")) {
            actualImgSav = uri.getPath();
        } else if (type.equals("GAL")) {
            actualImgSav = Utilities.getPath(uri, mActivity);
        }

        String tistamp = String.valueOf(Utilities.getCurrentTime());
        //String str="";

        SaveMediaRequest req = new SaveMediaRequest();
        req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(mContext) + "/mobi");
        req.setType("post");
        req.setId("0");
        req.setOrderId(String.valueOf(orderIdTaskPic));
        req.setTid("1");
        req.setFile("");
        req.setDtl("");
        req.setAction(OrderMedia.ACTION_MEDIA_SAVE);
        req.setTimestamp(tistamp);
        req.setMime("jpg");
        req.setMetapath(actualImgSav);
        req.setCopy("no"); //YD no because image is taken through camera

        OrderMedia.saveData(req, mActivity, this, SAVEPICTURE);
    }

    public void setActiveOrderObj(Order activeOrderObj) {

        this.activeOrderObj = activeOrderObj;
    }

    public void hideSoftKeyboard() {
        if (mActivity != null && mActivity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null)
                inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void loadDataOnBack(BaseTabActivity mActivity) {
        mActivity.registerHeader(this);
        mActivity.setHeaderTitle("", requestType, "");

        if (PreferenceHandler.getCurtSteCustdatForcustList(mActivity) == 1) {
            PreferenceHandler.setCurtSteCustdatForcustList(mActivity, 0);
            String x = PreferenceHandler.getCustSiteData(mActivity);

            String data[] = x.split("#");
            String speciesId = "";
            String speciesNm = "";
            String blank = "";
            if (data.length > 0)
                speciesId = data[0];
            if (data.length > 1)
                speciesNm = data[1];
            if (data.length > 2)
                blank = data[2];

            add_odrTask_tree_species_txtvw.setText(speciesNm);
            add_odrTask_tree_species_txtvw.setTag(speciesId);

        }

    }
}
