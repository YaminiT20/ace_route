package com.aceroute.mobile.software.fragment;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.reference.ClientSite;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.Shifts;
import com.aceroute.mobile.software.dialog.DatePickerInterface;
import com.aceroute.mobile.software.dialog.MyDatePickerDialog;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.SaveShiftListRequest;
import com.aceroute.mobile.software.requests.SaveShiftReq;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeOffFragment extends BaseFragment implements RespCBandServST, HeaderInterface, View.OnClickListener, DatePickerInterface {
    int shiftType;
    CheckBox mon_chk, tue_chk, wed_chk, thu_chk, fri_chk, sat_chk, sun_chk;
    TextView to_date, fm_date, tframe_txt,text_name ;
    EditText editname;
    ArrayList<Long> clientlocation_ids;
    ArrayList<String> clientlocation_nm;
    private static final int SAVE_PART = 1 ;

    public TimeOffFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_time_off, container, false);

        Bundle b = getArguments();
        shiftType = b.getInt("SHIFTYPE");
        if (shiftType == 3)
            mActivity.setHeaderTitle("", "ADD DAYOFF", "");
        initTimeOffView(view);
        TypeFaceFont.overrideFonts(mActivity, view);
        mActivity.registerHeader(this);
        return view;

    }

    private void initTimeOffView(View view) {
        mon_chk = (CheckBox) view.findViewById(R.id.mon_txt);
        tue_chk = (CheckBox) view.findViewById(R.id.tue_txt);
        wed_chk = (CheckBox) view.findViewById(R.id.wed_txt);
        thu_chk = (CheckBox) view.findViewById(R.id.thu_txt);
        fri_chk = (CheckBox) view.findViewById(R.id.fri_txt);
        sat_chk = (CheckBox) view.findViewById(R.id.sat_txt);
        sun_chk = (CheckBox) view.findViewById(R.id.sun_txt);
        text_name=(TextView) view.findViewById(R.id.nm_txt);
        fm_date = (TextView) view.findViewById(R.id.fmdate_frame);
        to_date = (TextView) view.findViewById(R.id.todate_frame);
        tframe_txt = (TextView) view.findViewById(R.id.tframe_txt);
        editname=(EditText)view.findViewById(R.id.nm_edt);
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
        mon_chk.setTextSize(txtsize);
        tue_chk.setTextSize(txtsize);
        wed_chk.setTextSize(txtsize);
        thu_chk.setTextSize(txtsize);
        fri_chk.setTextSize(txtsize);
        sat_chk.setTextSize(txtsize);
        sun_chk.setTextSize(txtsize);
        to_date.setTextSize(txtsize);
        fm_date.setTextSize(txtsize);
        text_name.setTextSize(txtsize);
        tframe_txt.setTextSize(txtsize);
        to_date.setOnClickListener(this);
        fm_date.setOnClickListener(this);
        tframe_txt.setTypeface(null, Typeface.BOLD);
        text_name.setTypeface(null,Typeface.BOLD);
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

            if (fm_date.getTag() == null || to_date.getTag() == null) {
                DisplayErrorMessage("Slight problem with data","Please provide Start and End date for repeat pattern","OK");
                return;
            }
                SaveShiftListRequest req = new SaveShiftListRequest();
                req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
                req.setType("post");
                req.setAction(Shifts.ACTION_SAVE_SHIFTS);
                ArrayList<SaveShiftReq> list = getShiftlist();
                if (list.size() > 0) {
                    req.setDataObj(list);
                    Shifts.saveData(req, mActivity, TimeOffFragment.this, SAVE_PART);
                } else {
                    Utilities.noPatterns(getActivity());
                }
        }
    }

    public ArrayList<SaveShiftReq> getShiftlist(){
        ArrayList<SaveShiftReq> list = new ArrayList<>();

        Calendar start = Calendar.getInstance();
        start.setTime((Date) fm_date.getTag());
        Calendar end = Calendar.getInstance();
        end.setTime((Date) to_date.getTag());
        DateFormat datefmt = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat formatter = new SimpleDateFormat("EEEE");

        for (Date date = start.getTime(); (start.before(end) || start.equals(end)); start.add(Calendar.DATE, 1), date = start.getTime()) {
            // Do your job here with `date`.
            String day = formatter.format(date);
            long lid=0;
            String tmslot="";
            String brkslot="0|0|0";
            boolean validateChecked=false;
            if(day.equalsIgnoreCase("Monday") && mon_chk.isChecked()){
                validateChecked=true;
                Date st = (Date)fm_date.getTag();
                Date ed = (Date)to_date.getTag();
                tmslot  = String.valueOf(st.getHours()*60+ st.getMinutes());
                tmslot += "|" + String.valueOf(ed.getHours()*60+ ed.getMinutes());
            }else if(day.equalsIgnoreCase("Tuesday") && tue_chk.isChecked()){
                validateChecked=true;
                Date st = (Date)fm_date.getTag();
                Date ed = (Date)to_date.getTag();
                tmslot  = String.valueOf(st.getHours()*60+ st.getMinutes());
                tmslot += "|" + String.valueOf(ed.getHours()*60+ ed.getMinutes());
            }else if(day.equalsIgnoreCase("Wednesday") && wed_chk.isChecked()){
                validateChecked=true;
                Date st = (Date)fm_date.getTag();
                Date ed = (Date)to_date.getTag();
                tmslot  = String.valueOf(st.getHours()*60+ st.getMinutes());
                tmslot += "|" + String.valueOf(ed.getHours()*60+ ed.getMinutes());
            }else if(day.equalsIgnoreCase("Thursday") && thu_chk.isChecked()){
                Date st = (Date)fm_date.getTag();
                Date ed = (Date)to_date.getTag();
                tmslot  = String.valueOf(st.getHours()*60+ st.getMinutes());
                tmslot += "|" + String.valueOf(ed.getHours()*60+ ed.getMinutes());
                validateChecked=true;
            }else if(day.equalsIgnoreCase("Friday") && fri_chk.isChecked()){
                Date st = (Date)fm_date.getTag();
                Date ed = (Date)to_date.getTag();
                tmslot  = String.valueOf(st.getHours()*60+ st.getMinutes());
                tmslot += "|" + String.valueOf(ed.getHours()*60+ ed.getMinutes());
                validateChecked=true;
            }else if(day.equalsIgnoreCase("Saturday") && sat_chk.isChecked()){
                Date st = (Date)fm_date.getTag();
                Date ed = (Date)to_date.getTag();
                tmslot  = String.valueOf(st.getHours()*60+ st.getMinutes());
                tmslot += "|" + String.valueOf(ed.getHours()*60+ ed.getMinutes());
                validateChecked=true;
            }else if(day.equalsIgnoreCase("Sunday") && sun_chk.isChecked()){
                Date st = (Date)fm_date.getTag();
                Date ed = (Date)to_date.getTag();
                tmslot  = String.valueOf(st.getHours()*60+ st.getMinutes());
                tmslot += "|" + String.valueOf(ed.getHours()*60+ ed.getMinutes());
                validateChecked=true;
            }

            if(validateChecked) {
                SaveShiftReq obj = new SaveShiftReq();
                obj.setId(0);
                obj.setTmslot(tmslot);
                obj.setNm(editname.getText().toString());
                obj.setBrkslot(brkslot);
                obj.setDt(datefmt.format(date));
                obj.setTid(shiftType);
                if (lid != 0) {
                    obj.setLid(lid);
                } else {
                    obj.setLid(0);
                }
                list.add(obj);
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
        }
    }

    @Override
    public void onCancelledBtn() {

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

            MyDatePickerDialog dialog = new MyDatePickerDialog(mActivity, new mDateSetListener(view), mYear, mMonth, mDay, false, TimeOffFragment.this, sizeDialogStyleID);
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

    boolean checkdate(Date date,TextView txt){
        if(txt.getId()==R.id.fmdate_frame) {
            if (to_date.getTag() == null) {
                if (checkMaxdate_fmCurrDate(date)) {
                    return true;

                } else {
                    return false;
                }
            } else {
                if (date.equals(to_date.getTag()) || date.before((Date) (to_date.getTag()))) {
                    return true;
                } else {
                    return false;
                }
            }
        }else if(txt.getId()==R.id.todate_frame){

            if (fm_date.getTag() == null) {
                if (checkMaxdate_fmCurrDate(date)) {
                    return true;
                } else {
                    return false;
                }
            }
            else{
                if(!(checkMaxdate(date)) ){
                    return false;
                }
                if(date.equals(fm_date.getTag()) || date.after((Date)(fm_date.getTag())) ){
                    return true;
                }else{
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

    boolean checkMaxdate(Date pdate){
        Calendar date = Calendar.getInstance();
        date.setTime((Date) fm_date.getTag());
        date.add(Calendar.MONTH,6);

        if(pdate.after(date.getTime())){
            return false;
        }else{
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date curDate = null;
            try {
                curDate = sdf.parse(mYear+"-"+editMonth+"-"+editDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(curDate!=null && checkdate(curDate,txt)) {
                String mCurrentDate = new SimpleDateFormat("MMM").format(curDate) + " " + editDate + " " + mYear;
                txt.setText(mCurrentDate);
                txt.setTag(curDate);
                if(txt.getId()==R.id.fmdate_frame && to_date.getTag() == null) {
                    to_date.setText(mCurrentDate);
                    to_date.setTag(curDate);
                }
            }else{
                // Toast.makeText(txt.getContext(),"Invalid Date",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void DisplayErrorMessage(String Headremsg,String contentMsg,String BotomMsg) {
        final MyDialog dialog = new MyDialog(getActivity(), Headremsg,contentMsg,BotomMsg);
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
}

























