package com.aceroute.mobile.software.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.TeritorryActivity;
import com.aceroute.mobile.software.adaptor.ShiftSwipeAdapter;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.reference.ClientSite;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.Shifts;
import com.aceroute.mobile.software.component.reference.Worker;
import com.aceroute.mobile.software.database.DBEngine;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.MySearchDialog;
import com.aceroute.mobile.software.dialog.MySearchDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.CommonSevenReq;
import com.aceroute.mobile.software.requests.SaveShiftReq;
import com.aceroute.mobile.software.utilities.CustomCalendarView;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.ShiftDateModel;
import com.aceroute.mobile.software.utilities.Utilities;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;


public class ManageShift extends BaseFragment implements RespCBandServST, HeaderInterface, View.OnClickListener {

    private static final int GET_SHIFT = 2;
    private static final int DELETE_SHIFT = 12;
    private static final int EDIT_SHIFT = 13;
    private static final int GET_RES_REQ = 6;
    private static int GET_LATLNG = 1;
    private int mheight = 500;
    public String stime = null;
    String workerWeek[];
    ArrayList<LatLng> arrayListLatlng = new ArrayList<>();
    TextView textStartTime, textEndTime, textAddress, textLocation, textStartBreakTime, textEndBreaktime, textduration, textName, textShift, textBreak, textTerritory, editTerritory;
    EditText editname, editAddress;
    HashMap<Long, ShiftDateModel> events;
    static HashMap<Long, ShiftDateModel> shiftevents;

    CustomCalendarView cv;
    ArrayList<String> optionLst = new ArrayList<String>();
    ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();
    HashMap<Long, Worker> Workerlist = new HashMap<Long, Worker>();
    ArrayList<Long> clientlocation_ids;
    ArrayList<String> clientlocation_nm;
    MySearchDialog searchDialg;
    long elemtSelectedInSearchDlog = -1;
    SwipeListView mLstShiftList;
    ShiftSwipeAdapter shiftListAdapter;
    public Dialog dialog_list;
    public Dialog dialogedit;
    final String loc_str = "Optional Location";
    ShiftDateModel shiftDtMdl;
    String territoryString;
    String shftlock;
    String[] minsArr = {"0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60"};
    LinearLayout linear_shift, linear_break;

    public ManageShift() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("ProcessShift", "" +
                "View " + Utilities.getCurrentTime());
        getWorkerlist();
        getShiftlist();
        View view = inflater.inflate(R.layout.fragment_manage_shift, container, false);
        mActivity.mSwapButton.setVisibility(View.VISIBLE);
        TypeFaceFont.overrideFonts(mActivity, view);
        events = new HashMap<Long, ShiftDateModel>();
        mActivity.registerHeader(this);
        shftlock = PreferenceHandler.getShiftLock(mActivity);
        setUIconfigShift();
        cv = ((CustomCalendarView) view.findViewById(R.id.calendar_view));
        cv.setenablelongpress(false);
        cv.setenablePerivousmonths(false);
        cv.setenablePerivousDate(false);

        return view;

    }

    private void setUIconfigShift() {
        mActivity.setHeaderTitle("", "SHIFT MANAGER", "");
        if (PreferenceHandler.getUiconfigShift(mActivity).equals("112") || PreferenceHandler.getUiconfigShift(mActivity).equals("102")) {
            optionLst.add("Add Shift Time");
            optionLst.add("Add Personal Time");
            optionLst.add("Add DayOff");
        } else if (PreferenceHandler.getUiconfigShift(mActivity).equals("11") || PreferenceHandler.getUiconfigShift(mActivity).equals("10")) {
            optionLst.add("Add Shift Time");
            optionLst.add("Add DayOff");
        } else if (PreferenceHandler.getUiconfigShift(mActivity).equals("2")) {
            optionLst.add("Add Personal Time");
            optionLst.add("Add DayOff");
        } else {
            optionLst.add("Add Shift Time");
            optionLst.add("Add Personal Time");
            optionLst.add("Add DayOff");
        }
    }

    public void getShiftlist() {
        CommonSevenReq req = new CommonSevenReq();
        req.setAction(Shifts.ACTION_GET_SHIFTS);
        req.setSource(DBEngine.DATA_SOURCE_LOCAL);
        req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(mActivity) + "/mobi");
        Log.i("ProcessShift", "getshift " + Utilities.getCurrentTime());
        Shifts.getData(req, mActivity, this, GET_SHIFT);
    }

    public void getWorkerlist() {
        CommonSevenReq req = new CommonSevenReq();
        req.setAction(Worker.ACTION_WORKER_LIST);
        req.setSource(DBEngine.DATA_SOURCE_LOCAL);
        req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(mActivity) + "/mobi");
        Worker.getData(req, mActivity, this, GET_RES_REQ);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("ProcessShift", "Onstart " + Utilities.getCurrentTime());
        clientlocation_ids = new ArrayList<Long>();
        clientlocation_nm = new ArrayList<String>();
        HashMap<Long, ClientSite> list = (HashMap<Long, ClientSite>) DataObject.compClientSiteTypeXmlDataStore;
        if (list != null)
            for (HashMap.Entry<Long, ClientSite> entry : list.entrySet()) {
                clientlocation_ids.add(entry.getKey());
                clientlocation_nm.add(entry.getValue().getNm());
            }
        cv.setEventHandler(new CustomCalendarView.EventHandler() {
            @Override
            public void onDayClick(Date date, boolean isprevious) {
                events = shiftevents;
                ShiftDateModel sft = events.get(date.getTime());
                if (sft != null && sft.shift != null)
                    if (sft.shift.tid == 1 || sft.shift.tid == 2)
                        showdialog_shift(date, isprevious);
            }

            @Override
            public void onShiftClick(Shifts shiftDateModel) {
                Date firstDate = Utilities.yesterday();
                Date lastDate = Utilities.shiftLockDate(mActivity);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date shiftDate = sdf.parse(shiftDateModel.getdt());
                    if (!(firstDate.compareTo(shiftDate) * shiftDate.compareTo(lastDate) > 0)) {
                        if (shiftDateModel != null)
                            if (shiftDateModel.tid != 1)
                                showeditshiftHr(shiftDateModel, true);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void getrefreshList() {
        Log.i("SHIFT", "Called from request 15");
        getShiftlist();
        getWorkerlist();
    }

    private void showHeaderDialog() {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity) mActivity, android.R.layout.select_dialog_item, optionLst);
            AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mActivity, AlertDialog.THEME_HOLO_LIGHT);
            builder.setCancelable(true);
            builder.setTitle(Html.fromHtml("<font color='#10c195'><b>Select Option</b></font>"));
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @SuppressLint("NewApi")
                public void onClick(DialogInterface dialog, int position) {
                    if (optionLst.get(position).equals("Add Shift Time")) {
                        ShifthourFragment shift = new ShifthourFragment();
                        Bundle b = new Bundle();
                        b.putInt("SHIFTYPE", 2);
                        shift.setArguments(b);
                        mActivity.pushFragments(Utilities.JOBS, shift, true, true, BaseTabActivity.UI_Thread);
                        dialog.dismiss();
                    } else if (optionLst.get(position).equals("Add Personal Time")) {
                        ShifthourFragment unshift = new ShifthourFragment();
                        Bundle b = new Bundle();
                        b.putInt("SHIFTYPE", 3);
                        unshift.setArguments(b);
                        mActivity.pushFragments(Utilities.JOBS, unshift, true, true, BaseTabActivity.UI_Thread);
                        dialog.dismiss();
                    } else if (optionLst.get(position).equals("Add DayOff")) {
                        TimeOffFragment timoff = new TimeOffFragment();
                        Bundle b = new Bundle();
                        b.putInt("SHIFTYPE", 0);
                        timoff.setArguments(b);
                        mActivity.pushFragments(Utilities.JOBS, timoff, true, true, BaseTabActivity.UI_Thread);
                        Log.i("SHIFT", "hitting TimeOFfFragment");
                        dialog.dismiss();
                    }
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

    void showdialog_shift(final Date date, boolean isprevious) {

        dialog_list = new Dialog(getActivity(), R.style.commonDialogTheme);
        final ArrayList<Shifts> slist = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy");
        dialog_list.setTitle(sdf.format(date));

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_shift_add, null, false);
        dialog_list.setContentView(view);
        dialog_list.setCanceledOnTouchOutside(false);

        mLstShiftList = (SwipeListView) view.findViewById(R.id.shift_material_lstvw);
        shiftListAdapter = new ShiftSwipeAdapter(mActivity, slist, ManageShift.this);
        mLstShiftList.setAdapter(shiftListAdapter);

        ShiftDateModel sft = events.get(date.getTime());
        if (sft == null) {
            return;
        }
        TextView _di_btn_cancel = (TextView) view.findViewById(R.id._di_btn_cancel);
        _di_btn_cancel.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
        if (sft.shift != null) {
            slist.add(sft.shift);
        }
        if (sft.unshiftlist != null) {
            for (int i = 0; i < sft.unshiftlist.size(); i++) {
                slist.add(sft.unshiftlist.get(i));
            }
        }
        if (sft.extrashiftlist != null) {
            for (int i = 0; i < sft.extrashiftlist.size(); i++) {
                slist.add(sft.extrashiftlist.get(i));
            }
        }

        mLstShiftList.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
                mLstShiftList.closeAnimate(position);
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
                View rowItem = Utilities.getViewOfListByPosition(position,
                        mLstShiftList);


                RelativeLayout backlayout = (RelativeLayout) rowItem
                        .findViewById(R.id.back_shift);
                backlayout.setBackgroundColor(getResources().getColor(
                        R.color.color_white));
                TextView chat = (TextView) rowItem
                        .findViewById(R.id.back_view_chat_textview_shift);
                TextView invite = (TextView) rowItem
                        .findViewById(R.id.back_view_invite_textview_shift);
                chat.setVisibility(View.INVISIBLE);
                invite.setVisibility(View.INVISIBLE);
                if (fromRight) {
                    showeditshiftHr(slist.get(position), false);
                } else {
                    deleteShift_UnAvil(slist.get(position), false);
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
                        position, mLstShiftList);

                RelativeLayout backlayout = (RelativeLayout) rowItem
                        .findViewById(R.id.back_shift);
                TextView chat = (TextView) rowItem
                        .findViewById(R.id.back_view_chat_textview_shift);
                TextView chat1 = (TextView) rowItem
                        .findViewById(R.id.back_view_dummy1_shift);

                TextView invite = (TextView) rowItem
                        .findViewById(R.id.back_view_invite_textview_shift);
                TextView invite1 = (TextView) rowItem
                        .findViewById(R.id.back_view_dummy_shift);

                if (right) {
                    backlayout.setBackgroundColor(getResources()
                            .getColor(R.color.bdr_green));
                    chat.setVisibility(View.GONE);
                    chat1.setVisibility(View.GONE);
                    invite.setVisibility(View.VISIBLE);
                    invite1.setVisibility(View.VISIBLE);

                } else {

                    backlayout.setBackgroundColor(getResources()

                            .getColor(R.color.color_red));
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
        if (isprevious)
            mLstShiftList.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
        else
            mLstShiftList.setSwipeMode(SwipeListView.SWIPE_MODE_DEFAULT); // there
        mLstShiftList.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_NONE);
        mLstShiftList.setSwipeActionRight(SwipeListView.SWIPE_ACTION_NONE);
        mLstShiftList.setOffsetLeft(convertDpToPixel());
        mLstShiftList.setOffsetRight(convertDpToPixel());
        mLstShiftList.setSwipeCloseAllItemsWhenMoveList(true);
        mLstShiftList.setAnimationTime(100);
        mLstShiftList.setSwipeOpenOnLongPress(false);
        shiftListAdapter.notifyDataSetChanged();
        _di_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_list.dismiss();
            }
        });
        Utilities.setDividerTitleColor(dialog_list, 500, getActivity());
        TextView tv = (TextView) dialog_list.findViewById(android.R.id.title);
        if (tv != null) {
            tv.setTextColor(getActivity().getResources().getColor(R.color.dlg_light_green));
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
        }
        TypeFaceFont.overrideFonts(mActivity, tv);
        dialog_list.show();
    }

    public int convertDpToPixel() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public void deleteShift_UnAvil(final Shifts shift, final boolean direct) {
        final MyDialog dialog = new MyDialog(getActivity(), "Delete Confirmation", "Are you sure want to delete?", "YES");
        dialog.setkeyListender(new MyDiologInterface() {

            @Override
            public void onPositiveClick() throws JSONException {
                dialog.dismiss();
            }

            @Override
            public void onNegativeClick() {
                SaveShiftReq req = new SaveShiftReq();
                req.setAction(Shifts.ACTION_DELETE_SHIFTS);
                req.setId(shift.id);
                Shifts.saveData(req, mActivity, ManageShift.this, DELETE_SHIFT);
                dialog.dismiss();
                if (direct)
                    dialogedit.dismiss();
                else
                    dialog_list.dismiss();
            }
        });
        dialog.onCreate(null);
        dialog.show();
    }

    public void hideSoftKeyboard() {
        if (mActivity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void showeditshiftHr(final Shifts shift, final boolean direct) {

        dialogedit = new Dialog(getActivity(), R.style.commonDialogTheme);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.edit_shifts_hrs, null, false);
        dialogedit.setContentView(view);
        dialogedit.setCanceledOnTouchOutside(false);
        TypeFaceFont.overrideFonts(getActivity(), view);
        arrayListLatlng = new ArrayList<>();
        textName = (TextView) view.findViewById(R.id.nm_txt);
        editname = (EditText) view.findViewById(R.id.nm_edt);
        linear_shift = (LinearLayout) view.findViewById(R.id.linear_shift);
        textShift = (TextView) view.findViewById(R.id.text_shift);
        textStartTime = (TextView) view.findViewById(R.id.starttime);
        textEndTime = (TextView) view.findViewById(R.id.endtime);
        textLocation = (TextView) view.findViewById(R.id.location);
        linear_break = (LinearLayout) view.findViewById(R.id.linear_break);
        textBreak = (TextView) view.findViewById(R.id.text_break);
        textStartBreakTime = (TextView) view.findViewById(R.id.startBreaktime);
        textEndBreaktime = (TextView) view.findViewById(R.id.endBreaktime);
        textduration = (TextView) view.findViewById(R.id.duration);
        textAddress = (TextView) view.findViewById(R.id.add_txt);
        editAddress = (EditText) view.findViewById(R.id.Editadr);
        editTerritory = (TextView) view.findViewById(R.id.editTerritory);
        textTerritory = (TextView) view.findViewById(R.id.Terri_txt);
        final LinearLayout mlayout = (LinearLayout) view.findViewById(R.id.lyout);
        TextView _di_btn_cancel = (TextView) view.findViewById(R.id._di_btn_cancel);
        TextView _di_btn_ok = (TextView) view.findViewById(R.id._di_btn_ok);
        TextView _di_btn_delete = (TextView) view.findViewById(R.id._di_btn_delete);
        textName.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
        textShift.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
        textBreak.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
        textStartTime.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
        textEndTime.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
        textStartBreakTime.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
        textEndBreaktime.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
        textduration.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
        textAddress.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
        editAddress.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
        textLocation.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
        editTerritory.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
        textTerritory.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
        textName.setTypeface(null, Typeface.BOLD);
        textShift.setTypeface(null, Typeface.BOLD);
        textBreak.setTypeface(null, Typeface.BOLD);
        textTerritory.setTypeface(null, Typeface.BOLD);
        textLocation.setOnClickListener(this);
        textStartTime.setOnClickListener(this);

        textEndTime.setOnClickListener(this);
        editTerritory.setOnClickListener(this);
        textStartBreakTime.setOnClickListener(this);
        textEndBreaktime.setOnClickListener(this);
        textduration.setOnClickListener(this);
        if (shift.tid == 2) {
            dialogedit.setTitle("Set Shift Hours");
            editTerritory.setVisibility(View.VISIBLE);
            textTerritory.setVisibility(View.VISIBLE);
        } else if (shift.tid == 3) {
            dialogedit.setTitle("Set Personal Hours");
            linear_shift.setVisibility(View.GONE);
            editAddress.setVisibility(View.VISIBLE);
            textAddress.setVisibility(View.VISIBLE);
        }
        if (arrayListLatlng.size() != 0) {
            for (int i = 0; i < arrayListLatlng.size(); i++) {
                {
                    LatLng latLng = new LatLng(arrayListLatlng.get(i).latitude, arrayListLatlng.get(i).longitude);
                    latLngArrayList.add(latLng);
                }
            }
        } else if (shift.getTerri() != null) {
            String latarray[] = shift.getTerri().split(Pattern.quote("|"));
            latLngArrayList = new ArrayList<>();
            for (int i = 0; i < latarray.length; i++) {
                String temp[] = latarray[i].split(Pattern.quote(","));
                if (temp.length > 0 && !temp[0].isEmpty()) {
                    LatLng latLng = new LatLng(Double.parseDouble(temp[0]), Double.parseDouble(temp[1]));
                    latLngArrayList.add(latLng);
                }
            }
        }
        if (arrayListLatlng.size() != 0 || latLngArrayList.size() != 0)
            editTerritory.setText("Edit Territory");
        else
            editTerritory.setText("Add Territory");

        String stm = "08:00 am";
        String etm = "05:00 pm";
        String dur = "00:00 am";
        String brk = "12:00 pm";
        DateFormat inputFormat = new SimpleDateFormat("hh:mm a");

        try {
            Date stime = inputFormat.parse(stm);
            Date etime = inputFormat.parse(etm);
            Date durtime = inputFormat.parse(dur);
            Date brkDate = inputFormat.parse(brk);
            textStartTime.setTag(stime);
            textEndTime.setTag(etime);

            textStartBreakTime.setText(brk);
            textStartBreakTime.setTag(brkDate);

            textduration.setTag(String.valueOf(durtime.getTime()));
            textduration.setText(minsArr[12] + " mins");

            textEndBreaktime.setText(brk);
            textEndBreaktime.setTag(brkDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (direct) {
            _di_btn_delete.setVisibility(View.VISIBLE);
            _di_btn_delete.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
            _di_btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteShift_UnAvil(shift, true);
                }
            });
        }

        _di_btn_cancel.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
        _di_btn_ok.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));

        mlayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("click", "cl");
                InputMethodManager imm = (InputMethodManager) dialogedit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(dialogedit.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });

        if (shift.nm != null && !shift.nm.trim().equals("")) {
            editname.setText(shift.nm);
        }
        textLocation.setTag(shift.lid);
        if (shift.lid != 0) {
            int index = clientlocation_ids.indexOf(shift.lid);
            textLocation.setText(clientlocation_nm.get(index));
        } else {
            textLocation.setText(loc_str);
        }
        if (shift.tid == 1) {
            textName.setVisibility(View.GONE);

        }

        int[] times;
        int[] breaktimes;
        if (shift.tid == 2) {
            editname.setText(shift.nm);
            times = Utilities.gettimes(shift.tmslot);
            String temp[] = shift.brkSlot.split(Pattern.quote("|"));
            breaktimes = Utilities.gettimes(shift.brkSlot);
            if (breaktimes[0] != 0 && breaktimes[3] != 0) {
                textStartBreakTime.setText(settimevalue(breaktimes[0] + "," + breaktimes[1]));
                textEndBreaktime.setText(settimevalue(breaktimes[2] + "," + breaktimes[3]));
                textduration.setText(temp[2]);
            }
            textStartTime.setText(settimevalue(times[0] + "," + times[1]));
            textEndTime.setText(settimevalue(times[2] + "," + times[3]));
            try {
                Date stime = inputFormat.parse(settimevalue(times[0] + "," + times[1]));
                Date etime = inputFormat.parse(settimevalue(times[2] + "," + times[3]));
                Date sbreaktime = inputFormat.parse(settimevalue(breaktimes[0] + "," + breaktimes[1]));
                Date endbreaktime = inputFormat.parse(settimevalue(breaktimes[2] + "," + breaktimes[3]));
                textStartBreakTime.setTag(sbreaktime);
                textEndBreaktime.setTag(endbreaktime);
                textduration.setTag(temp[2]);
                textStartTime.setTag(stime);
                textEndTime.setTag(etime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (shift.tid == 3) {
            String temp[] = shift.brkSlot.split(Pattern.quote("|"));
            times = Utilities.gettimes(shift.brkSlot);
            textStartBreakTime.setText(settimevalue(times[0] + "," + times[1]));
            textEndBreaktime.setText(settimevalue(times[2] + "," + times[3]));
            textduration.setText(temp[2]);
            editAddress.setText(shift.adr);
            try {
                Date stime = inputFormat.parse(settimevalue(times[0] + "," + times[1]));
                Date etime = inputFormat.parse(settimevalue(times[2] + "," + times[3]));
                textStartBreakTime.setTag(stime);
                textEndBreaktime.setTag(etime);
                textduration.setTag(temp[2]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        _di_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogedit.dismiss();
            }
        });

        _di_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmslot = "";
                String brkslot = "";
                InputMethodManager imm = (InputMethodManager) dialogedit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(dialogedit.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                SaveShiftReq req = new SaveShiftReq();
                req.setAction(Shifts.ACTION_EDIT_SHIFTS);
                if (shift.tid == 2) {
                    req.setId(shift.id);
                    req.setNm(editname.getText().toString().trim());
                    Date st = (Date) textStartTime.getTag();
                    Date ed = (Date) textEndTime.getTag();
                    tmslot = String.valueOf(st.getHours() * 60 + st.getMinutes());
                    tmslot += "|" + String.valueOf(ed.getHours() * 60 + ed.getMinutes());
                    Date stbrk = (Date) textStartBreakTime.getTag();
                    Date endbrk = (Date) textEndBreaktime.getTag();
                    String stringDuration[] = textduration.getText().toString().split(" ");
                    if (stbrk.getTime() == endbrk.getTime()) {
                        brkslot = "0|0|0";
                    } else if (stbrk.getTime() < endbrk.getTime()) {
                        brkslot = String.valueOf(stbrk.getHours() * 60 + stbrk.getMinutes());
                        brkslot += "|" + String.valueOf(endbrk.getHours() * 60 + endbrk.getMinutes());
                        brkslot += "|" + stringDuration[0];
                    } else {
                        if (stbrk.getTime() > endbrk.getTime()) {
                            DisplayErrorMessage("", "Make Sure Break End Time is Greater than Break Start time", "OK");
                        }
                    }
                    req.setBrkslot(brkslot);
                    req.setTmslot(tmslot);
                    req.setTid(shift.tid);
                    req.setLid((Long) textLocation.getTag());
                    req.setTerri(territoryString);
                } else if (shift.tid == 3) {
                    req.setId(shift.id);
                    req.setNm(editname.getText().toString());
                    req.setAddress(editAddress.getText().toString());
                    req.setTid(shift.tid);
                    Date stbrk = (Date) textStartBreakTime.getTag();
                    Date endbrk = (Date) textEndBreaktime.getTag();
                    String stringDuration[] = textduration.getText().toString().split(" ");
                    if (stbrk.getTime() == endbrk.getTime()) {
                        brkslot = "0|0|0";
                    } else if (stbrk.getTime() < endbrk.getTime()) {
                        brkslot = String.valueOf(stbrk.getHours() * 60 + stbrk.getMinutes());
                        brkslot += "|" + String.valueOf(endbrk.getHours() * 60 + endbrk.getMinutes());
                        brkslot += "|" + stringDuration[0];
                    } else {
                        if (stbrk.getTime() > endbrk.getTime()) {
                            DisplayErrorMessage("", "Make Sure Break End Time is Greater than Break Start time", "OK");
                        }
                    }
                    req.setBrkslot(brkslot);

                }
                Shifts.saveData(req, mActivity, ManageShift.this, EDIT_SHIFT);
                dialogedit.dismiss();
                if (!direct)
                    dialog_list.dismiss();
            }
        });

        Utilities.setDividerTitleColor(dialogedit, 500, getActivity());
        TextView tv = (TextView) dialogedit.findViewById(android.R.id.title);
        if (tv != null) {
            tv.setTextColor(getActivity().getResources().getColor(R.color.dlg_light_green));
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
        }
        TypeFaceFont.overrideFonts(mActivity, tv);
        dialogedit.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialogedit.show();

    }

    public void DisplayErrorMessage(String Headremsg, String contentMsg, String BotomMsg) {
        final MyDialog dialog = new MyDialog(getActivity(), Headremsg, contentMsg, BotomMsg);
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
        return;
    }

    @Override
    public void ServiceStarter(RespCBandServST activity, Intent intent) {

    }

    @Override
    public void setResponseCallback(String response, Integer reqId) {

    }

    @Override
    public void setResponseCBActivity(Response response) {
        if (response != null) {
            if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED))) {
                if (response.getId() == GET_SHIFT) {
                    Log.i("SHIFT", "getData from server");
                    displayShiftList(response);

                } else if (response.getId() == GET_RES_REQ) {
                    displayWorkerList(response);
                } else if (response.getId() == DELETE_SHIFT || response.getId() == EDIT_SHIFT) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getShiftlist();
                        }
                    });

                }
            } else if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA))) {
                if (response.getId() == GET_SHIFT)
                    displayShiftList(response);
                else if (response.getId() == DELETE_SHIFT || response.getId() == EDIT_SHIFT) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getShiftlist();
                        }
                    });

                }
            }
        }

    }

    void displayWorkerList(Response response) {
        if (response.getResponseMap() != null) {
            Workerlist = (HashMap<Long, Worker>) response.getResponseMap();
            workerWeek = Workerlist.get(PreferenceHandler.getResId(mActivity)).getWrkwk().split("\\|");
            events = (HashMap<Long, ShiftDateModel>) response.getResponseMap();
        }
    }

    void displayShiftList(Response response) {
        events.clear();
        long today;
        Date initDate = null;
        Log.i("ProcessShift", "get Responce " + Utilities.getCurrentTime());
        if (response.getResponseMap() != null) {
            events = (HashMap<Long, ShiftDateModel>) response.getResponseMap();
            shiftevents = (HashMap<Long, ShiftDateModel>) response.getResponseMap();
            today = Utilities.getCurrentTimeInMillis();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(today);
            try {
                Date firstDate = getFirstDay(cal.getTime());
                cal.add(Calendar.MONTH, 5);
                Date lastDate = getLastDay(cal.getTime());
                Calendar c = Calendar.getInstance();
                for (initDate = firstDate; initDate.before(lastDate); c.setTime(initDate), c.add(Calendar.DATE, 1), initDate = c.getTime()) {
                    if (events.get(initDate.getTime()) != null) {
                        ShiftDateModel shiftDateModel = events.get(initDate.getTime());
                        if (shiftDateModel.unshiftlist != null) {
                            if (shiftDateModel.shift == null) {
                                getDefaultShift(initDate);
                                Worker worker = shiftDtMdl.getWorker();
                                shiftDateModel.setWorker(worker);
                                events.put(initDate.getTime(), shiftDateModel);
                            }
                        }

                    } else {
                        getDefaultShift(initDate);
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }
        } else {
            getDefaultShift(initDate);
        }

        Log.i("SHIFT", "Calender update");
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cv.updateCalendar(events);
            }
        });
    }

    void getDefaultShift(Date dateFromShift) {
        try {
            final HashMap<Long, ShiftDateModel> shiftList = new HashMap<Long, ShiftDateModel>();
            Calendar c = Calendar.getInstance();
            c.setTime(dateFromShift);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            int WorkerDay = getWorkerDay(dayOfWeek);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startDate = "", endDate = "";
            startDate = sdf.format(c.getTime());
            c.add(Calendar.DATE, 6);
            endDate = sdf.format(c.getTime());
            Calendar start = Calendar.getInstance();
            start.setTime(sdf.parse(startDate));
            Calendar end = Calendar.getInstance();
            end.setTime(sdf.parse(endDate));
            String dt = startDate;
            if (dt != null) {
                Date date = sdf.parse(startDate);
                Long dttime = date.getTime();
                Worker worker = new Worker();
                worker.setWrkwk(workerWeek[WorkerDay]);
                ShiftDateModel dtmodel = (ShiftDateModel) shiftList.get(dttime);
                if (dtmodel != null) {
                    dtmodel.setWorker(worker);
                } else {
                    shiftDtMdl = new ShiftDateModel(date);
                    shiftDtMdl.setWorker(worker);
                    shiftList.put(dttime, shiftDtMdl);
                }
            }

            if (!workerWeek[WorkerDay].equals("0,0"))
                events.put(dateFromShift.getTime(), shiftList.get(dateFromShift.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int getWorkerDay(int dayOfWeek) {
        if (dayOfWeek == 1)
            return 6;
        else
            return dayOfWeek - 2;
    }

    public Date getFirstDay(Date d) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date dddd = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dddd = sdf.parse(sdf.format(dddd));
        return dddd;
    }

    String settimevalue(String tm) {
        String hrarr[];
        if (tm.contains("|"))
            hrarr = tm.split(Pattern.quote("|"));
        else
            hrarr = tm.split(Pattern.quote(","));
        if (!hrarr[0].trim().equals("")) {
            int hr = Integer.parseInt(hrarr[0]);
            int minutes = Integer.parseInt(hrarr[1]);
            int min = minutes % 60;
            String st = "am";
            if (hr > 12) {
                hr = hr - 12;
                st = "pm";
            }
            stime = hr + ":" + (min > 9 ? min : "0" + min) + " " + st;
        }
        return stime;
    }

    public Date getLastDay(Date d) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.DATE, 1);
        Date dddd = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dddd = sdf.parse(sdf.format(dddd));
        return dddd;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.mSwapButton.setVisibility(View.GONE);
    }

    @Override
    public void headerClickListener(String callingId) {
        if (callingId.equals(BaseTabActivity.HeaderPlusPressed)) {
            showHeaderDialog();
        } else if (callingId.equals(BaseTabActivity.HeaderSwapButton)) {
            SwapingFragment swapingFragment = new SwapingFragment();
            mActivity.pushFragments(Utilities.JOBS, swapingFragment, true, true, BaseTabActivity.UI_Thread);
        }
        if (callingId.equals(BaseTabActivity.HeaderDeleteButton)) {
        }
    }

    private void showCategoryDialog(ArrayList<Long> idsLst, ArrayList<String> nmsLst,
                                    final TextView view) {
        elemtSelectedInSearchDlog = -1;
        if (!view.getText().toString().trim().equals("") && !view.getText().toString().trim().equals(loc_str)) {
            elemtSelectedInSearchDlog = (long) view.getTag();
        }
        searchDialg = new MySearchDialog(mActivity, "Select Location", "", idsLst, nmsLst, elemtSelectedInSearchDlog);

        searchDialg.setkeyListender(new MySearchDiologInterface() {
            @Override
            public void onButtonClick() {
                super.onButtonClick();
                searchDialg.cancel();
            }

            @Override
            public void onItemSelected(Long idSelected, String nameSelected) {
                super.onItemSelected(idSelected, nameSelected);
                view.setText(nameSelected);
                view.setTag(idSelected);
                searchDialg.cancel();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.starttime:
                Utilities.getTimeRange(getActivity(), Utilities.TIME_TYPE_START, ((TextView) v), textStartTime, null);
                break;
            case R.id.endtime:
                Utilities.getTimeRange(getActivity(), Utilities.TIME_TYPE_END, ((TextView) v), textEndTime, null);
                break;
            case R.id.startBreaktime:
                Utilities.getTimeRange(getActivity(), Utilities.TIME_TYPE_START, ((TextView) v), textStartBreakTime, null);
                break;
            case R.id.endBreaktime:
                Utilities.getTimeRange(getActivity(), Utilities.TIME_TYPE_END, ((TextView) v), textEndBreaktime, null);
                break;
            case R.id.duration:
                showDialogForDur(v.getTag());
                break;
            case R.id.location:
                showCategoryDialog(clientlocation_ids, clientlocation_nm, (TextView) v);
                break;
            case R.id.editTerritory:
                Intent intent = new Intent(mActivity, TeritorryActivity.class);
                intent.putExtra("mapvalue", false);
                intent.putExtra("makeEdit", true);
                if (arrayListLatlng.size() != 0)
                    intent.putExtra("latLang", arrayListLatlng);
                else
                    intent.putExtra("latLang", latLngArrayList);
                startActivityForResult(intent, GET_LATLNG);
                break;
        }
    }

    private void showDialogForDur(Object tag) {
        int currentSetTime = Integer.valueOf((String) tag);
        int arrayOfTme[] = getHrsMins(currentSetTime);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("Set Duration");

        LayoutInflater inflater = mActivity.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.duration_picker, null);
        builder.setView(dialogView);
        final NumberPicker minPck = (NumberPicker) dialogView.findViewById(R.id.minPicker);
        minPck.setMaxValue(12);
        minPck.setMinValue(0);
        minPck.setValue(arrayOfTme[0] + 1);
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
            selectionDivider.setAccessible(true);
            selectionDivider.set(minPck, getActivity().getResources().getDrawable(
                    R.drawable.picker_view_holo));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        int minsSelected = Integer.valueOf(minsArr[minPck.getValue()]); //YD -1 becauseminPck.getValue() is returing value 1 greater than the value kept in an array
                       /*
                       Ashish: to Set the duration O min
                       if (hrsSelected < 1 && minsSelected < 5) // YD if the time is less than 5 min select atleast 5 mins automatically
                            minsSelected = 5;
                            */
                        textduration.setText(minsSelected + " mins");
                        textduration.setTag(String.valueOf(minsSelected));

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        int mheight = 500;
        Utilities.setDividerTitleColor(dialog, mheight, mActivity);
        Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Utilities.setDefaultFont_12(button_negative);
        Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Utilities.setDefaultFont_12(button_positive);
    }

    private int[] getHrsMins(int currentSetTime) {
        int currentsetHrsMins[] = new int[2];
        if (currentSetTime >= 60) {
            currentsetHrsMins[0] = currentSetTime / 60;
            currentsetHrsMins[1] = currentSetTime % 60;
        } else {
            currentsetHrsMins[0] = 0;
            currentsetHrsMins[1] = currentSetTime;
        }
        return currentsetHrsMins;
    }

    @Override
    public void onResume() {
        super.onResume();
        getShiftlist();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        StringBuilder stringTerri = new StringBuilder();
        if (requestCode == GET_LATLNG) {
            if (resultCode == RESULT_OK) {
                arrayListLatlng = data.getParcelableArrayListExtra("arrayListLatlng");
                for (int i = 0; i < arrayListLatlng.size(); i++) {
                    stringTerri.append(arrayListLatlng.get(i).latitude + "," + arrayListLatlng.get(i).longitude + "|");
                }
                if (!stringTerri.toString().equals(""))
                    territoryString = stringTerri.deleteCharAt(stringTerri.length() - 1).toString();
                else
                    territoryString = "";
            }
        }
    }
}
