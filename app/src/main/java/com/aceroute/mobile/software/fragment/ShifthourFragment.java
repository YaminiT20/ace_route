package com.aceroute.mobile.software.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.TeritorryActivity;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.reference.ClientSite;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.Shifts;
import com.aceroute.mobile.software.dialog.DatePickerInterface;
import com.aceroute.mobile.software.dialog.MyDatePickerDialog;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.MySearchDialog;
import com.aceroute.mobile.software.dialog.MySearchDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.SaveShiftListRequest;
import com.aceroute.mobile.software.requests.SaveShiftReq;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.ShiftDateModel;
import com.aceroute.mobile.software.utilities.Utilities;
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

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShifthourFragment extends BaseFragment implements RespCBandServST, HeaderInterface, View.OnClickListener, DatePickerInterface {

    EditText editname, editAddress;
    TextView textStartTime, textEndTime, textAddress, textLocation, textStartBreakTime, textEndBreaktime, textduration, textFromDate, textToDate,
            textName, textShift, textBreak, textRecurrence, textTerritory;
    public static final int REQUEST_CODE = 1;
    private static int GET_LATLNG = 1;
    LinearLayout linear_break, linear_shift;
    StringBuilder stringTerri = new StringBuilder();
    RelativeLayout relativeTeritory;
    ArrayList<LatLng> arrayListLatlng=new ArrayList<>();
    CheckBox checkBoxMonday, checkBoxTuesday, checkBoxWednesday, checkBoxThursday, checkBoxFriday, checkBoxSaturday, checkBoxSunday;
    final String loc_str = "Optional Location";
    MySearchDialog searchDialg;
    String territoryString;
    long elemtSelectedInSearchDlog = -1;
    ArrayList<Long> clientlocation_ids;
    ArrayList<String> clientlocation_nm;
    AppCompatButton btnTerritory;
    int shiftType;//YD using for available and unavailable
    String[] minsArr = {"0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60"};
    private static final int SAVE_PART = 1;
    public boolean isMarkerGeometryClosed;


    public ShifthourFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_personal, container, false);
        ;
        Bundle b = getArguments();
        shiftType = b.getInt("SHIFTYPE");
        if (shiftType == 2)
            mActivity.setHeaderTitle("", "ADD SHIFT", "");
        else {
            mActivity.setHeaderTitle("", "ADD PERSONAL TIME", "");
        }
        initPersonalView(view);

        TypeFaceFont.overrideFonts(mActivity, view);
        mActivity.registerHeader(this);
        return view;
    }

    private void initPersonalView(View view) {
        isMarkerGeometryClosed = true;
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
        textRecurrence = (TextView) view.findViewById(R.id.tframe_txt);
        textFromDate = (TextView) view.findViewById(R.id.fmdate_frame);
        textToDate = (TextView) view.findViewById(R.id.todate_frame);
        checkBoxMonday = (CheckBox) view.findViewById(R.id.mon_txt);
        checkBoxTuesday = (CheckBox) view.findViewById(R.id.tue_txt);
        checkBoxWednesday = (CheckBox) view.findViewById(R.id.wed_txt);
        checkBoxThursday = (CheckBox) view.findViewById(R.id.thu_txt);
        checkBoxFriday = (CheckBox) view.findViewById(R.id.fri_txt);
        checkBoxSaturday = (CheckBox) view.findViewById(R.id.sat_txt);
        checkBoxSunday = (CheckBox) view.findViewById(R.id.sun_txt);
        textTerritory = (TextView) view.findViewById(R.id.text_territory);
        relativeTeritory = (RelativeLayout) view.findViewById(R.id.relativeTeritory);
        btnTerritory = (AppCompatButton) view.findViewById(R.id.btnDrawPolygon);
        textLocation.setText(loc_str);
        if (shiftType == 3) {
            linear_shift.setVisibility(View.GONE);
            editAddress.setVisibility(View.VISIBLE);
            textAddress.setVisibility(View.VISIBLE);
            textTerritory.setVisibility(View.GONE);
            btnTerritory.setVisibility(View.GONE);
            relativeTeritory.setVisibility(View.GONE);
        }

        if (PreferenceHandler.getUiconfigShift(mActivity).equals("112") || PreferenceHandler.getUiconfigShift(mActivity).equals("11"))
            linear_break.setVisibility(View.VISIBLE);

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
            textStartBreakTime.setTag(brkDate);
            textduration.setTag(String.valueOf(durtime.getTime()));
            textEndBreaktime.setTag(brkDate);
            textduration.setText(minsArr[12] + " mins");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
        Date st = new Date();
        String sdat = sdf.format(st);
        textFromDate.setText(sdat);
        textToDate.setText(sdat);

        try {
            textFromDate.setTag(sdf.parse(sdat));
            textToDate.setTag(sdf.parse(sdat));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        eventInit();
    }

    void eventInit() {

        clientlocation_ids = new ArrayList<Long>();
        clientlocation_nm = new ArrayList<String>();
        HashMap<Long, ClientSite> list = (HashMap<Long, ClientSite>) DataObject.compClientSiteTypeXmlDataStore;
        if (list != null)
            for (HashMap.Entry<Long, ClientSite> entry : list.entrySet()) {
                clientlocation_ids.add(entry.getKey());
                ClientSite value = entry.getValue();
                clientlocation_nm.add(value.getNm());
            }

        int txtsize = 20 + PreferenceHandler.getCurrrentFontSzForApp(getActivity());
        textName.setTextSize(txtsize);
        editname.setTextSize(txtsize);
        textShift.setTextSize(txtsize);
        textStartTime.setTextSize(txtsize);
        textEndTime.setTextSize(txtsize);
        textBreak.setTextSize(txtsize);
        textStartBreakTime.setTextSize(txtsize);
        textEndBreaktime.setTextSize(txtsize);
        textduration.setTextSize(txtsize);
        textAddress.setTextSize(txtsize);
        editAddress.setTextSize(txtsize);
        textRecurrence.setTextSize(txtsize);
        textLocation.setTextSize(txtsize);
        textFromDate.setTextSize(txtsize);
        textToDate.setTextSize(txtsize);
        checkBoxMonday.setTextSize(txtsize);
        checkBoxTuesday.setTextSize(txtsize);
        checkBoxWednesday.setTextSize(txtsize);
        checkBoxThursday.setTextSize(txtsize);
        checkBoxFriday.setTextSize(txtsize);
        checkBoxSaturday.setTextSize(txtsize);
        checkBoxSunday.setTextSize(txtsize);
        textTerritory.setTextSize(txtsize);
        btnTerritory.setTextSize(txtsize);
        textStartTime.setOnClickListener(this);
        textEndTime.setOnClickListener(this);
        textduration.setOnClickListener(this);
        textFromDate.setOnClickListener(this);
        btnTerritory.setOnClickListener(this);
        textLocation.setOnClickListener(this);
        textToDate.setOnClickListener(this);
        textStartBreakTime.setOnClickListener(this);
        textEndBreaktime.setOnClickListener(this);
        textName.setTypeface(null, Typeface.BOLD);
        textShift.setTypeface(null, Typeface.BOLD);
        textBreak.setTypeface(null, Typeface.BOLD);
        textTerritory.setTypeface(null, Typeface.BOLD);
        textRecurrence.setTypeface(null, Typeface.BOLD);

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
                if (response.getId() == SAVE_PART) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.popFragments(15);
                        }
                    });
                }
            } else if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA))) {
                if (response.getId() == SAVE_PART) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.popFragments(15);
                        }
                    });
                }

            }
        }
    }

    @Override
    public void headerClickListener(String callingId) {

        if (callingId.equals(BaseTabActivity.HeaderDonePressed)) {

            if (textFromDate.getTag() == null || textToDate.getTag() == null) {
                DisplayErrorMessage("Slight problem with data", "Please provide Start and End date for repeat pattern", "OK");
                return;
            }
            if (validate_data()) {
                SaveShiftListRequest req = new SaveShiftListRequest();
                req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
                req.setType("post");
                req.setAction(Shifts.ACTION_SAVE_SHIFTS);
                ArrayList<SaveShiftReq> list = getShiftlist();
                if (list.size() > 0) {
                    req.setDataObj(list);
                    Shifts.saveData(req, mActivity, ShifthourFragment.this, SAVE_PART);

                } else {
                    Utilities.noPatterns(getActivity());
                }
            }
        }
    }

    private boolean validate_data() {
        String name = editname.getText().toString();
        Date stshift = (Date) textStartTime.getTag();
        Date endshift = (Date) textEndTime.getTag();
        Date brekstshift = (Date) textStartBreakTime.getTag();
        Date brekendshift = (Date) textEndBreaktime.getTag();


        if (TextUtils.isEmpty(name)) {
            DisplayErrorMessage("Error", "Please enter the name", "OK");
            return false;
        }

        if (endshift.getTime() <= stshift.getTime()) {
            DisplayErrorMessage("Error", "Make Sure Shift End Time is Greater than Shift Start time", "OK");
            return false;
        }

        if (brekendshift.getTime() < brekstshift.getTime()) {
            DisplayErrorMessage("Error", "Make Sure Break End Time is Greater than Break Start time", "OK");
            return false;
        }

        if (shiftType == 3) {
            String stringduration[] = textduration.getText().toString().split(" ");
            if (Integer.valueOf(stringduration[0]) <= 0) {
                DisplayErrorMessage("Error", "Make Sure Duration is Greater than 0", "OK");
                return false;
            }


        }

        Date start = (Date) textFromDate.getTag();
        Date end = (Date) textToDate.getTag();
        if ((start.getDate() == end.getDate())) {
            SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week spelled out completely
            String day = simpleDateformat.format(start);
            ArrayList<String> checkedday = new ArrayList<>();
            if (checkBoxSunday.isChecked())
                checkedday.add(checkBoxSunday.getText().toString());
            if (checkBoxMonday.isChecked())
                checkedday.add(checkBoxMonday.getText().toString());
            if (checkBoxTuesday.isChecked())
                checkedday.add(checkBoxTuesday.getText().toString());
            if (checkBoxWednesday.isChecked())
                checkedday.add(checkBoxWednesday.getText().toString());
            if (checkBoxThursday.isChecked())
                checkedday.add(checkBoxThursday.getText().toString());
            if (checkBoxFriday.isChecked())
                checkedday.add(checkBoxFriday.getText().toString());
            if (checkBoxSaturday.isChecked())
                checkedday.add(checkBoxSaturday.getText().toString());

            if (!checkedday.isEmpty()) {
                for (int i = 0; i < checkedday.size(); i++) {
                    if (!checkedday.contains(day.toUpperCase())) {
                        DisplayErrorMessage("Error", "Unable to create shift.No selected days within specified range", "OK");
                        return false;
                    }
                }
            } else {
                DisplayErrorMessage("Error", "Please select the week Day", "OK");
                return false;
            }
        }


        HashMap<Long, ShiftDateModel> shiftevents = ManageShift.shiftevents;
        Date initDate = null;
        Calendar c = Calendar.getInstance();
        try {
            Date firstDate = getFirstDay(c.getTime());
            Date lastDate = null;
            ArrayList<SaveShiftReq> list = getShiftlist();
            lastDate = getLastDay(c.getTime());
            for (initDate = firstDate; initDate.before(lastDate); c.setTime(initDate), c.add(Calendar.DATE, 1), initDate = c.getTime()) {
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        String date = list.get(i).getDt();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date shiftdate = sdf.parse(date);
                        ShiftDateModel shiftDateModel = shiftevents.get(initDate.getTime());
                        if (shiftDateModel != null) {
                            if (shiftDateModel.shift != null) {
                                if (shiftDateModel.shift.tid == 1 && shiftdate.getTime() == initDate.getTime()) {
                                    DisplayErrorMessage("Error", "You are not able to create a shift for a day", "OK");
                                    return false;
                                }
                            }
                        }
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
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


    public ArrayList<SaveShiftReq> getShiftlist() {
        ArrayList<SaveShiftReq> list = new ArrayList<>();

        Calendar start = Calendar.getInstance();
        start.setTime((Date) textFromDate.getTag());
        Calendar end = Calendar.getInstance();
        end.setTime((Date) textToDate.getTag());

        DateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat formatter = new SimpleDateFormat("EEEE");

        for (Date date = start.getTime(); (start.before(end) || start.equals(end)); start.add(Calendar.DATE, 1), date = start.getTime()) {
            long lid = 0;
            String tmslot = "";
            String day = formatter.format(date);
            boolean validateChecked = false;
            if (day.equalsIgnoreCase("Monday") && checkBoxMonday.isChecked()) {
                validateChecked = true;
            } else if (day.equalsIgnoreCase("Tuesday") && checkBoxTuesday.isChecked()) {
                validateChecked = true;
            } else if (day.equalsIgnoreCase("Wednesday") && checkBoxWednesday.isChecked()) {
                validateChecked = true;
            } else if (day.equalsIgnoreCase("Thursday") && checkBoxThursday.isChecked()) {
                validateChecked = true;
            } else if (day.equalsIgnoreCase("Friday") && checkBoxFriday.isChecked()) {
                validateChecked = true;
            } else if (day.equalsIgnoreCase("Saturday") && checkBoxSaturday.isChecked()) {
                validateChecked = true;
            } else if (day.equalsIgnoreCase("Sunday") && checkBoxSunday.isChecked()) {
                validateChecked = true;
            }
            if (validateChecked) {
                String brkslot = "";
                Date st = (Date) textStartTime.getTag();
                Date ed = (Date) textEndTime.getTag();
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
                tmslot = String.valueOf(st.getHours() * 60 + st.getMinutes());
                tmslot += "|" + String.valueOf(ed.getHours() * 60 + ed.getMinutes());
                if (!tmslot.trim().equals("")) {
                    SaveShiftReq obj = new SaveShiftReq();
                    obj.setId(0);
                    obj.setTmslot(tmslot);
                    obj.setNm(editname.getText().toString());
                    if (shiftType == 3)
                        obj.setAddress(editAddress.getText().toString());
                    else if (shiftType == 2) {
                        if (lid != 0) {
                            obj.setLid(lid);
                        } else {
                            obj.setLid(0);
                        }
                    }
                    obj.setDt(datefmt.format(date));
                    obj.setBrkslot(brkslot);
                    obj.setTid(shiftType);
                    obj.setTerri(territoryString);

                    list.add(obj);


                }
            }
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fmdate_frame: {
                hideSoftKeyboard();
                getCalender((TextView) v);
                break;
            }
            case R.id.todate_frame: {
                hideSoftKeyboard();
                getCalender((TextView) v);
                break;
            }
            case R.id.location: {
                showCategoryDialog(clientlocation_ids, clientlocation_nm, (TextView) v);
                break;
            }
            case R.id.starttime: {
                Utilities.getTimeRange(getActivity(), Utilities.TIME_TYPE_START, ((TextView) v), textStartTime, null);
                break;
            }
            case R.id.endtime: {
                Utilities.getTimeRange(getActivity(), Utilities.TIME_TYPE_END, ((TextView) v), textEndTime, null);
                break;
            }
            case R.id.duration: {
                showDialogForDur(v.getTag());
                break;
            }
            case R.id.startBreaktime: {
                Utilities.getTimeRange(getActivity(), Utilities.TIME_TYPE_START, ((TextView) v), textStartBreakTime, null);
                break;
            }
            case R.id.endBreaktime: {
                Utilities.getTimeRange(getActivity(), Utilities.TIME_TYPE_END, ((TextView) v), textEndBreaktime, null);
                break;
            }
            case R.id.btnDrawPolygon:
                Intent intent = new Intent(mActivity, TeritorryActivity.class);
                intent.putExtra("mapvalue", true);
                intent.putExtra("makeEdit", false);
                startActivityForResult(intent, GET_LATLNG);
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
    public void onCancelledBtn() {

    }

    private void showCategoryDialog(ArrayList<Long> idsLst, ArrayList<String> nmsLst, final TextView view) {
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

            MyDatePickerDialog dialog = new MyDatePickerDialog(mActivity, new mDateSetListener(view), mYear, mMonth, mDay, false, ShifthourFragment.this, sizeDialogStyleID);
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

    public void hideSoftKeyboard() {
        if (mActivity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    boolean checkdate(Date date, TextView txt) {
        if (txt.getId() == R.id.fmdate_frame) {
            if (textToDate.getTag() == null) {
                if (checkMaxdate_fmCurrDate(date)) {
                    return true;

                } else {
                    return false;
                }
            } else {
                if (date.equals(textToDate.getTag()) || date.before((Date) (textToDate.getTag()))) {
                    return true;
                } else {
                    return false;
                }
            }
        } else if (txt.getId() == R.id.todate_frame) {

            if (textFromDate.getTag() == null) {
                if (checkMaxdate_fmCurrDate(date)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (!(checkMaxdate(date))) {
                    return false;
                }
                if (date.equals(textFromDate.getTag()) || date.after((Date) (textFromDate.getTag()))) {
                    return true;
                } else {
                    return false;
                }
            }

        }
        return false;
    }

    boolean checkMaxdate_fmCurrDate(Date pdate) {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.MONTH, 6);
        if (pdate.after(date.getTime())) {
            return false;
        } else {
            return true;
        }
    }

    boolean checkMaxdate(Date pdate) {
        Calendar date = Calendar.getInstance();
        date.setTime((Date) textFromDate.getTag());
        date.add(Calendar.MONTH, 6);

        if (pdate.after(date.getTime())) {
            return false;
        } else {
            return true;
        }
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
            if (curDate != null && checkdate(curDate, txt)) {
                String mCurrentDate = new SimpleDateFormat("MMM").format(curDate) + " " + editDate + " " + mYear;
                txt.setText(mCurrentDate);
                txt.setTag(curDate);
                if (txt.getId() == R.id.fmdate_frame && textToDate.getTag() == null) {
                    textToDate.setText(mCurrentDate);
                    textToDate.setTag(curDate);
                }
            }
        }
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_LATLNG) {
            if (resultCode == RESULT_OK) {
                 arrayListLatlng = data.getParcelableArrayListExtra("arrayListLatlng");
                for (int i = 0; i < arrayListLatlng.size(); i++) {
                    stringTerri.append(arrayListLatlng.get(i).latitude + "," + arrayListLatlng.get(i).longitude + "|");
                }
                territoryString = stringTerri.deleteCharAt(stringTerri.length() - 1).toString();
            }
        }
    }
}