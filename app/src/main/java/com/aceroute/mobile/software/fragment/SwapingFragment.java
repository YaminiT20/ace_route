package com.aceroute.mobile.software.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.reference.Shifts;
import com.aceroute.mobile.software.database.DBEngine;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.CommonSevenReq;
import com.aceroute.mobile.software.requests.SaveShiftReq;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.ShiftDateModel;
import com.aceroute.mobile.software.utilities.SwapCalenderView;
import com.aceroute.mobile.software.utilities.Utilities;
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

public class SwapingFragment extends BaseFragment implements RespCBandServST, HeaderInterface, View.OnClickListener {
    private static final int GET_SHIFT = 2;
    private static final int DELETE_SHIFT = 12;
    private static final int DELETE_MULTIPLE_SHIFT = 18;

    private static final int EDIT_SHIFT = 13;
    public static boolean isSwappingFragment;
    final String loc_str = "Optional Location";
    public String stime = null;
    public long id = 0;
    StringBuilder stringTerri = new StringBuilder();
    ArrayList<Long> clientlocation_ids;
    ArrayList<String> clientlocation_nm;
    public Dialog dialogedit;
    TextView textStartTime, textEndTime, textAddress, textLocation, textStartBreakTime, textEndBreaktime, textduration, textName, textShift, textBreak;
    EditText editname, editAddress;
    HashMap<Long, ShiftDateModel> events;
    LinearLayout linear_shift, linear_break;
    String[] minsArr = {"0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60"};
    SwapCalenderView cv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swaping_calender, container, false);
        mActivity.registerHeader(this);
        events = new HashMap<Long, ShiftDateModel>();
        cv = ((SwapCalenderView) view.findViewById(R.id.calendar_view));
        getAllShift();
        cv.setEventHandler(new SwapCalenderView.EventHandler() {
            @Override
            public void onDayClick(Date date, boolean isprevious) {

            }

            @Override
            public void onShiftClick(Shifts shiftDateModel) {
                Date firstDate = Utilities.yesterday();
                Date lastDate = Utilities.shiftLockDate(mActivity);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date shiftDate = sdf.parse(shiftDateModel.getdt());
                    if (!(firstDate.compareTo(shiftDate) * shiftDate.compareTo(lastDate) > 0)) {
                        if (shiftDateModel != null) {
                            if (shiftDateModel.tid != 1)
                                showeditshiftHr(shiftDateModel, true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


//        String response = HttpConnection.get(this,Api.getshift(mActivity), params);
        return view;
    }

    public void showeditshiftHr(final Shifts shift, final boolean direct) {

        dialogedit = new Dialog(getActivity(), R.style.commonDialogTheme);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.edit_shifts_hrs, null, false);
        dialogedit.setContentView(view);
        dialogedit.setCanceledOnTouchOutside(false);
        TypeFaceFont.overrideFonts(getActivity(), view);

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
        textName.setTypeface(null, Typeface.BOLD);
        textShift.setTypeface(null, Typeface.BOLD);
        textBreak.setTypeface(null, Typeface.BOLD);
        textStartTime.setOnClickListener(this);
        textEndTime.setOnClickListener(this);
        textStartBreakTime.setOnClickListener(this);
        textEndBreaktime.setOnClickListener(this);
        textduration.setOnClickListener(this);
        if (shift.tid == 2)
            dialogedit.setTitle("Set Shift Hours");
        else if (shift.tid == 3) {
            dialogedit.setTitle("Set Personal Hours");
            linear_shift.setVisibility(View.GONE);
            editAddress.setVisibility(View.VISIBLE);
            textAddress.setVisibility(View.VISIBLE);
        }


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

        _di_btn_cancel.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
        _di_btn_ok.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(getActivity())));
//

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
                    req.setTerri(stringTerri.toString());
                    req.setLid((Long) textLocation.getTag());
                } else if (shift.tid == 3) {
                    req.setId(shift.id);
                    req.setNm(editname.getText().toString());
                    req.setAddress(editAddress.getText().toString());
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
                Shifts.saveData(req, mActivity, SwapingFragment.this, EDIT_SHIFT);
                dialogedit.dismiss();
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
                // on No button click
                dialog.dismiss();
            }

            @Override
            public void onNegativeClick() {
                // on Yes button click
                dialog.dismiss();
            }
        });
        dialog.onCreate(null);
        dialog.show();
        return;
    }

    private void getAllShift() {
        CommonSevenReq req = new CommonSevenReq();
        req.setAction(Shifts.ACTION_GET_SHIFTS);
        req.setSource(DBEngine.DATA_SOURCE_LOCAL);
        req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(mActivity)+"/mobi");
        Log.i("ProcessShift", "getshift " + Utilities.getCurrentTime());
        Shifts.getData(req, mActivity, this, GET_SHIFT);

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
                }
                if (response.getId() == EDIT_SHIFT) {
                    Log.i("SHIFT", "getData from server");
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getAllShift();
                        }
                    });
                }
            }
        }
    }

    void displayShiftList(Response response) {
        events.clear();
        long today;
        Date initDate = null;
        Log.i("ProcessShift", "get Responce " + Utilities.getCurrentTime());
        if (response.getResponseMap() != null) {
            events = (HashMap<Long, ShiftDateModel>) response.getResponseMap();
            today = Utilities.getCurrentTimeInMillis();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(today);
            try {
                Date firstDate = getFirstDay(cal.getTime());
                Date lastDate = getLastDay(cal.getTime());
                Calendar c = Calendar.getInstance();
                for (initDate = firstDate; initDate.before(lastDate); c.setTime(initDate), c.add(Calendar.DATE, 1), initDate = c.getTime()) {
                    if (events.get(initDate.getTime()) != null) {
                        ShiftDateModel shiftDateModel = events.get(initDate.getTime());
                        if (shiftDateModel.shift == null) {
                            //todo remove that row.
                        }
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }
        Log.i("SHIFT", "Calender update");
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cv.updateCalendar(events);
            }
        });
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

    public Date getFirstDay(Date d) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date dddd = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dddd = sdf.parse(sdf.format(dddd));
        return dddd;
    }

    @Override
    public void headerClickListener(String callingId) {
        if (callingId.equals(BaseTabActivity.HeaderDeleteButton)) {
            ArrayList<Shifts> deleteShift = SwapCalenderView.shiftsArrayList;
            if (deleteShift.size() > 0)
                deleteShift_UnAvil(deleteShift, true);
            else
                DisplayErrorMessage("Error", "Please select atleast one shift to delete", "OK");
        } else if (callingId.equals(BaseTabActivity.HeaderSwapButton)) {
            mActivity.popFragments(15);
        } else if (callingId.equals(BaseTabActivity.HeaderBackPressed)) {
            Fragment fragment = BaseTabActivity.mStacks.get(Utilities.JOBS).elementAt(
                    BaseTabActivity.mStacks.get(Utilities.JOBS).size() - 2);
            if (fragment instanceof ManageShift)
                ((ManageShift) fragment).getrefreshList();
        }
    }

    public void deleteShift_UnAvil(final ArrayList<Shifts> shift, final boolean direct) {
        final MyDialog dialog = new MyDialog(getActivity(), "Delete Confirmation", "Are you sure want to delete?", "YES");
        dialog.setkeyListender(new MyDiologInterface() {

            @Override
            public void onPositiveClick() throws JSONException {
                // on No button click
                dialog.dismiss();
            }

            @Override
            public void onNegativeClick() {
                // on Yes button click

                if (validateToDelete(shift, dialog)) {
                    isSwappingFragment = true;
                    SaveShiftReq req = new SaveShiftReq();
                    req.setAction(Shifts.DELETE_MULTIPLE_SHIFTS);
                    req.setRecs(createxmlforDeleteShifts(shift));
                    Shifts.deleteData(req, mActivity, SwapingFragment.this, DELETE_SHIFT);
                    dialog.dismiss();
                    getAllShift();
                    cv.updateCalendar();
                }
            }
        });
        dialog.onCreate(null);
        dialog.show();
    }

    private boolean validateToDelete(ArrayList<Shifts> shifts, Dialog dialog) {
        if (shifts != null) {
            for (int i = 0; i < shifts.size(); i++) {
                Date firstDate = Utilities.yesterday();
                Date lastDate = Utilities.shiftLockDate(mActivity);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date shiftDate = sdf.parse(shifts.get(i).getdt());
                    if ((firstDate.compareTo(shiftDate) * shiftDate.compareTo(lastDate) > 0)) {
                        DisplayErrorMessage("Error", "You are not able to delete the shift " + shifts.get(i).getdt(), "OK");
                        dialog.dismiss();
                        SwapCalenderView.shiftsArrayList = null;
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        return false;
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
        minPck.setMaxValue(12);// YD value should be number of elements in an array for ex in string array below
        minPck.setMinValue(0);
        //minPck.setValue((arrayOfTme[1]/5)+1);//YD it automatically set the value from an array which is being displayed //when diff was of 5 mins
        /*
        Ashish : setting min O .
         minPck.setValue(arrayOfTme[1]+1);//YD it automatically set the value from an array which is being displayed
        */
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

    public static String createxmlforDeleteShifts(ArrayList<Shifts> shift) {
        StringBuffer xml = new StringBuffer();
        try {
            for (int i = 0; i < shift.size(); i++) {
                xml.append(shift.get(i).id + "|");
            }


            return xml.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void getrefreshList() {
        getAllShift();
    }
}