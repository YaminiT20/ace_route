package com.aceroute.mobile.software.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
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
import com.aceroute.mobile.software.dialog.MySearchDialog;
import com.aceroute.mobile.software.dialog.MySearchDiologInterface;
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
public class UnavailableFragment extends BaseFragment implements RespCBandServST, HeaderInterface, View.OnClickListener,DatePickerInterface {

    private static final int SAVE_PART = 1;
    Date currentdate;
    CheckBox skipweeked;
    EditText txt_tname;
    TextView starttime,endtime,fm_date,to_date,location,pattern_txt,recurr_txt;
    MySearchDialog searchDialg;
    ArrayList<Long> clientlocation_ids ;
    final String loc_str="Location - Optional";
     static final String sdate_str="Start Date";
     static final String edate_str="End Date";
    ArrayList<String> clientlocation_nm;
    public UnavailableFragment() {
    }

    @SuppressLint("ValidFragment")
    public UnavailableFragment(Date date) {
        // Required empty public constructor
        currentdate= date;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_unavailable, container, false);
        mActivity.registerHeader(this);
        mActivity.setHeaderTitle("", "ADD UNAVAILABLE", "");
        TypeFaceFont.overrideFonts(mActivity, view);

        clientlocation_ids=new ArrayList<Long>();
        clientlocation_nm=new ArrayList<String>();
        HashMap<Long, ClientSite> list =   (HashMap<Long, ClientSite>) DataObject.compClientSiteTypeXmlDataStore;
        if(list!=null)
        for(HashMap.Entry<Long, ClientSite> entry : list.entrySet()) {
            clientlocation_ids.add(entry.getKey());
            ClientSite value = entry.getValue();
            clientlocation_nm.add(value.getNm());

        }

        starttime = (TextView)view.findViewById(R.id.starttime);
        endtime = (TextView)view.findViewById(R.id.endtime);
        fm_date = (TextView)view.findViewById(R.id.fm_date);
        to_date = (TextView)view.findViewById(R.id.to_date);
        location = (TextView)view.findViewById(R.id.location);
        txt_tname = (EditText) view.findViewById(R.id.txt_tname);
        location.setText(loc_str);

        pattern_txt = (TextView) view.findViewById(R.id.pattern_txt);
        recurr_txt = (TextView) view.findViewById(R.id.recurr_txt);
        int txtsize=20+PreferenceHandler.getCurrrentFontSzForApp(getActivity());
        pattern_txt.setTextSize(txtsize);
        recurr_txt.setTextSize(txtsize);
        starttime.setTextSize(txtsize);
        endtime.setTextSize(txtsize);
        fm_date.setTextSize(txtsize);
        to_date.setTextSize(txtsize);
        location.setTextSize(txtsize);
        txt_tname.setTextSize(txtsize);
       // fm_date.setText(sdate_str);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
        Date st = new Date();
        String sdat= sdf.format(st);
        fm_date.setText(sdat);
        to_date.setText(sdat);
        try {
            fm_date.setTag(sdf.parse(sdat));
            to_date.setTag(sdf.parse(sdat));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //to_date.setText(edate_str);

        location.setTag((long)0);
        skipweeked = (CheckBox) view.findViewById(R.id.skipweeked);
        skipweeked.setTextSize(txtsize);

        //fm_date.setTag(currentdate);
        //to_date.setTag(currentdate);

        String stm = "06:00 pm";
        String etm = "07:00 pm";
        DateFormat inputFormat = new SimpleDateFormat("hh:mm a");


        try {
            starttime.setTag(inputFormat.parse(stm));
            endtime.setTag(inputFormat.parse(etm));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        starttime.setOnClickListener(this);
        endtime.setOnClickListener(this);
        fm_date.setOnClickListener(this);
        to_date.setOnClickListener(this);
        location.setOnClickListener(this);


       // SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");

        //fm_date.setText(String.valueOf(sdf.format(currentdate)));//Jun 03 2015
        //to_date.setText(String.valueOf(sdf.format(currentdate)));//Jun 03 2015

        return view;
    }

    @Override
    public void headerClickListener(String callingId) {
        if(callingId.equals(BaseTabActivity.HeaderDonePressed)){
            if(fm_date.getTag()==null || to_date.getTag()==null){
                final MyDialog dialog = new MyDialog(getActivity(),"Slight problem with data", "Please provide Start and End date for repeat pattern", "OK");
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
                SaveShiftListRequest req = new SaveShiftListRequest();
                req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
                req.setType("post");
                req.setAction(Shifts.ACTION_SAVE_SHIFTS);

            ArrayList<SaveShiftReq> list = getShiftlist();
            if(list.size()>0) {
                req.setDataObj(list);
                Shifts.saveData(req, mActivity, UnavailableFragment.this, SAVE_PART);
            }else{
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

        for (Date date = start.getTime();(start.before(end) || start.equals(end)); start.add(Calendar.DATE, 1), date = start.getTime()) {
            // Do your job here with `date`.


            String day = formatter.format(date);
            long lid=0;
            String tmslot="";
            if(  !(skipweeked.isChecked() &&  (day.equalsIgnoreCase("Saturday") || day.equalsIgnoreCase("Sunday")) ) ){
                lid = (long)location.getTag();
                Date st = (Date)starttime.getTag();
                Date ed = (Date)endtime.getTag();
                tmslot  = String.valueOf(st.getHours()*60+ st.getMinutes());
                tmslot += "|" + String.valueOf(ed.getHours()*60+ ed.getMinutes());

            }else{
                continue;
            }

            SaveShiftReq obj = new SaveShiftReq();
            obj.setId(0);
            obj.setTmslot(tmslot);
            obj.setNm(txt_tname.getText().toString());
            obj.setDt(datefmt.format(date));
            obj.setTid(2);
            if(lid!=0) {
                obj.setLid(lid);
            }else{
                obj.setLid(0);
            }
            list.add(obj);


        }
        return list;
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
                if (response.getId() == SAVE_PART){
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fm_date:
                hideSoftKeyboard();
                getCalender((TextView) v);
            break;
            case R.id.to_date:
                hideSoftKeyboard();
                getCalender((TextView) v);
            break;
            case R.id.starttime:
                Utilities.getTimeRange(getActivity(),Utilities.TIME_TYPE_START,((TextView)v),endtime, null);
            break;
            case R.id.endtime:
                Utilities.getTimeRange(getActivity(),Utilities.TIME_TYPE_END,((TextView)v),starttime, null);
            break;
            case R.id.location:
                showCategoryDialog(clientlocation_ids, clientlocation_nm,(TextView)v);
            break;
        }
    }

    private void showCategoryDialog(ArrayList<Long> idsLst, ArrayList<String> nmsLst, final TextView view)
    {
        int elemtSelectedInSearchDlog=-1;
        if(!view.getText().toString().trim().equals("") && !view.getText().toString().trim().equals(loc_str)) {
            elemtSelectedInSearchDlog = nmsLst.indexOf(view.getText().toString());
        }
        searchDialg = new MySearchDialog(mActivity, "Select Location", "", idsLst,nmsLst, elemtSelectedInSearchDlog);

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

        int mheight=500;
        Utilities.setDividerTitleColor(searchDialg, mheight, mActivity);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(searchDialg.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        searchDialg.getWindow().setAttributes(lp);

    }

    private void getCalender(TextView view) {
        try{

            Calendar cal = Calendar.getInstance();
            int mDay;
            int mYear;
            if(!view.getText().toString().trim().equals("") && !view.getText().toString().trim().equals(sdate_str) && !view.getText().toString().trim().equals(edate_str)) {
                Date date = new SimpleDateFormat("MMM").parse(view.getText().toString().split(" ")[0]);
                cal.setTime(date);
                mDay = Integer.valueOf(view.getText().toString().split(" ")[1]);
                mYear = Integer.valueOf(view.getText().toString().split(" ")[2]);
            }else {
                mDay= cal.get(Calendar.DAY_OF_MONTH);
                mYear=cal.get(Calendar.YEAR);
            }
            int mMonth = Integer.valueOf(cal.get(Calendar.MONTH));

            int sizeDialogStyleID = Utilities.getDialogTextSize(mActivity);

            MyDatePickerDialog dialog = new MyDatePickerDialog(mActivity, new mDateSetListener(view), mYear, mMonth, mDay, false,UnavailableFragment.this,sizeDialogStyleID);
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

    public void hideSoftKeyboard() {
        if(mActivity.getCurrentFocus()!=null){
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onCancelledBtn() {

    }

/*    boolean checkdate(Date date,TextView txt){
        if(txt.getId()==R.id.fm_date){

            if(date.equals(to_date.getTag()) || date.before((Date)(to_date.getTag())) ){
                return true;
            }else{
                return false;
            }
        }else if(txt.getId()==R.id.to_date){
            if(!(checkMaxdate(date)) ){
                return false;
            }
            if(date.equals(fm_date.getTag()) || date.after((Date)(fm_date.getTag())) ){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }*/


    boolean checkdate(Date date,TextView txt){
        if(txt.getId()==R.id.fm_date) {
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
        }else if(txt.getId()==R.id.to_date){

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

    boolean checkMaxdate_fmCurrDate(Date pdate){
        Calendar date = Calendar.getInstance();
        date.add(Calendar.MONTH,6);
        if(pdate.after(date.getTime())){
            return false;
        }else{
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
            this.txt=view;
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
                if(txt.getId()==R.id.fm_date && to_date.getTag() == null){
                        to_date.setText(mCurrentDate);
                        to_date.setTag(curDate);
                }
            }else{
               // Toast.makeText(txt.getContext(),"Invalid Date",Toast.LENGTH_SHORT).show();
            }
        }
    }




}
