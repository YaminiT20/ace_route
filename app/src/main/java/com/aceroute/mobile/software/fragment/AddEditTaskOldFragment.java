package com.aceroute.mobile.software.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.reference.Form;
import com.aceroute.mobile.software.dialog.DatePickerInterface;
import com.aceroute.mobile.software.dialog.MyDatePickerDialog;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MySearchDialog;
import com.aceroute.mobile.software.dialog.MySearchDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.SaveFormListRequest;
import com.aceroute.mobile.software.requests.SaveFormRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddEditTaskOldFragment extends BaseFragment implements HeaderInterface, RespCBandServST, DatePickerInterface {
    private static final int SAVE_PART = 1;
    LinearLayout linearLayout;
    TextView textViewForDate, textViewFortime, spinnerText;
    long elemtSelectedInSearchDlog = -1;
    ArrayList<JSONObject> formList = new ArrayList<JSONObject>();
    ArrayList<View> allviews = new ArrayList<>();
    MySearchDialog searchDialg;
    MyDialog dialog = null;
    EditText editText;
    Bundle bundle;
    boolean isDate = false;
    String min, max, req;
    String formData;
    JSONObject finaljson;
    int ftid;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dynamic_form_layout, null);
        TypeFaceFont.overrideFonts(mActivity, v);
        mActivity.registerHeader(this);

        mContext = mActivity;
        try {
            AshishInitiViewReference(v);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return v;
    }

    private void AshishInitiViewReference(View v) throws JSONException {

        bundle = this.getArguments();
        formData = bundle.getString("formData");
        ftid = bundle.getInt("ftid");
        dataDisplay(formData);
        mActivity.setHeaderTitle("", "Form", "");
        linearLayout = v.findViewById(R.id.linear_layout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        for (int i = 0; i < formList.size(); i++) {
            JSONObject mainjson = new JSONObject();
            int position = i;
            if (formList.get(i).getString("tid").equalsIgnoreCase("1")) {
                createEditText(formList.get(i), params, mainjson, false, position);
            } else if (formList.get(i).getString("tid").equalsIgnoreCase("2")) {
                createEditTextArea(formList.get(i), params, mainjson, position);
            } else if (formList.get(i).getString("tid").equalsIgnoreCase("3")) {
                createEditText(formList.get(i), params, mainjson, true, position);
            } else if (formList.get(i).getString("tid").equalsIgnoreCase("4")) {
                isDate = true;
                createTextDateTime(formList.get(i), params, mainjson, isDate, position);
            } else if (formList.get(i).getString("tid").equalsIgnoreCase("5")) {
                isDate = false;
                createTextDateTime(formList.get(i), params, mainjson, isDate, position);
            } else if (formList.get(i).getString("tid").equalsIgnoreCase("6")) {
                createSpinner(formList.get(i), params, mainjson, isDate, position);
            }
        }
    }

    private void createSpinner(final JSONObject jsonObject, LinearLayout.LayoutParams params, JSONObject mainjson, boolean isDate, final int position) throws JSONException {
        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null)
            textView.setText(jsonObject.getString("lbl"));
        params.setMargins(10, 0, 10, 0);
        textView.setLayoutParams(params);
        textView.setAllCaps(true);
        textView.setTextSize(20);
        linearLayout.addView(textView);
        spinnerText = new TextView(mActivity);
        //spinnerText.setTag(position);
        jsonObject.put("val", jsonObject.getString("ddnval"));//        mainjson.put("position", position);
//        mainjson.put("data", jsonObject.getString("ddnval"));
//        mainjson.put("id", jsonObject.getString("id"));
        mainjson.put("fld", jsonObject);

        params.setMargins(10, 0, 10, 0);
        spinnerText.setHeight((int) convertPixelsToDp(240.0f, getActivity()));
        spinnerText.setPadding(10, 1, 10, 15);
        spinnerText.setTextSize(22.0f);
        spinnerText.setBackground(getResources().getDrawable(R.drawable.rounded_green_fill_bg));
        spinnerText.setLayoutParams(params);
        spinnerText.setTextColor(getResources().getColor(R.color.white));
        spinnerText.setTag(mainjson);
        allviews.add(spinnerText);
        linearLayout.addView(spinnerText);
        spinnerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> arrayList = new ArrayList();
                final ArrayList<Long> idslist = new ArrayList<>();
                try {
                    if (jsonObject.getString("ddn") != null) {
                        String str[] = jsonObject.getString("ddn").split(",");
                        String ids[] = jsonObject.getString("ddnval").split(",");
                        for (int k = 0; k < str.length; k++) {
                            idslist.add(Long.valueOf(ids[k]));
                            arrayList.add(str[k]);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideSoftKeyboard();
                showCategoryDialog(idslist, arrayList, jsonObject);
            }
        });


    }

    private void createTextDateTime(final JSONObject jsonObject, LinearLayout.LayoutParams params, final JSONObject mainjson, boolean isDate, int position) throws JSONException {
        if (!isDate) {
            String stm = "08:00 am";
            DateFormat inputFormat = new SimpleDateFormat("hh:mm a");
            Date stime = null;
            try {
                stime = inputFormat.parse(stm);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            TextView textView = new TextView(mActivity);
            if (jsonObject.getString("lbl") != null)
                textView.setText(jsonObject.getString("lbl"));
            params.setMargins(15, 15, 15, 15);
            textView.setLayoutParams(params);
            textView.setTextSize(20);
            linearLayout.addView(textView);
            textViewFortime = new TextView(mActivity);
//            jsonObject.put("val", textViewFortime.getText().toString());
//            mainjson.put("data", jsonObject);
            textViewFortime.setTag(mainjson.toString());
            params.setMargins(10, 0, 10, 0);
            textViewFortime.setHeight((int) convertPixelsToDp(240.0f, getActivity()));
            textViewFortime.setPadding(10, 10, 10, 10);
            textViewFortime.setTextSize(22.0f);
            textViewFortime.setBackground(getResources().getDrawable(R.drawable.rounded_green_fill_bg));
            textViewFortime.setLayoutParams(params);
            textViewFortime.setTextColor(getResources().getColor(R.color.white));
            allviews.add(textViewFortime);
            linearLayout.addView(textViewFortime);
            textViewFortime.setTag(stime);
            textViewFortime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utilities.getTimeRange(getActivity(), Utilities.TIME_TYPE_START, ((TextView) v), textViewFortime, null);
                }
            });
        } else {
            TextView textView = new TextView(mActivity);
            if (jsonObject.getString("lbl") != null)
                textView.setText(jsonObject.getString("lbl"));
            params.setMargins(15, 15, 15, 15);
            textView.setLayoutParams(params);
            textView.setTextSize(20);
            linearLayout.addView(textView);
            textViewForDate = new TextView(mActivity);


            params.setMargins(10, 0, 10, 0);
            textViewForDate.setHeight((int) convertPixelsToDp(240.0f, getActivity()));
            textViewForDate.setPadding(10, 10, 10, 10);
            textViewForDate.setTextSize(22.0f);
            textViewForDate.setBackground(getResources().getDrawable(R.drawable.rounded_green_fill_bg));
            textViewForDate.setLayoutParams(params);
            textViewForDate.setTextColor(getResources().getColor(R.color.white));
            allviews.add(textViewForDate);
            linearLayout.addView(textViewForDate);
            textViewForDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard();
                    getCalender((TextView) textViewForDate);
                    /* try {
                     *//*jsonObject.put("val", textViewForDate.getText().toString());
                        mainjson.put("data", jsonObject);
                        textViewForDate.setTag(mainjson);*//*
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }
            });
        }


    }

    private void createEditTextArea(final JSONObject jsonObject, LinearLayout.LayoutParams params, final JSONObject mainjson, int position) throws JSONException {
        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null)
            textView.setText(jsonObject.getString("lbl"));
        params.setMargins(15, 15, 15, 15);
        textView.setLayoutParams(params);
        textView.setTextSize(20);
        linearLayout.addView(textView);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 230); // Width , height
        final EditText editText = new EditText(mActivity);
        editText.setSingleLine(true);
//        jsonObject.put("val", editText.getText().toString());
        mainjson.put("fld", jsonObject);
        editText.setTag(mainjson);
        params.setMargins(10, 0, 10, 0);
        editText.setBackground(getResources().getDrawable(R.drawable.roundable_edittext));
        editText.setLayoutParams(lparams);
        allviews.add(editText);
        linearLayout.addView(editText);
    }

    private void createEditText(JSONObject jsonObject, LinearLayout.LayoutParams params, JSONObject mainjson, boolean isValidNumber, int position) throws JSONException {
        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null)
            textView.setText(jsonObject.getString("lbl"));
        params.setMargins(15, 10, 15, 10);
        textView.setLayoutParams(params);
        textView.setTextSize(20);
        linearLayout.addView(textView);
        editText = new EditText(mActivity);
        mainjson.put("fld", jsonObject);
        editText.setTag(mainjson);
        params.setMargins(15, 10, 15, 10);
        editText.setLayoutParams(params);
        editText.setBackground(getResources().getDrawable(R.drawable.roundable_edittext));
        allviews.add(editText);
        linearLayout.addView(editText);
    }

    private void showCategoryDialog(ArrayList<Long> idslist, ArrayList<String> nmsLst, final JSONObject jsonObject) {
        searchDialg = new MySearchDialog(mActivity, "Category", "", idslist, nmsLst, elemtSelectedInSearchDlog);

        searchDialg.setkeyListender(new MySearchDiologInterface() {
            @Override
            public void onButtonClick() {
                super.onButtonClick();
                searchDialg.cancel();
            }

            @Override
            public void onItemSelected(Long idSelected, String nameSelected) {

                super.onItemSelected(idSelected, nameSelected);
                String data = idSelected.toString();
                try {
                    JSONObject mainJson = new JSONObject();
                    spinnerText.setText(nameSelected);
                    jsonObject.put("val", data);
                    mainJson.put("fld", jsonObject);
                    spinnerText.setTag(mainJson);
                    searchDialg.cancel();
                    elemtSelectedInSearchDlog = idSelected;
                } catch (JSONException e) {
                    e.printStackTrace();
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

    public float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private void dataDisplay(String loadJSONFromAsset) {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset);
            JSONArray m_jArry = obj.getJSONArray("frm");
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                formList.add(jo_inside);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

            MyDatePickerDialog dialog = new MyDatePickerDialog(mActivity, new mDateSetListener(view), mYear, mMonth, mDay, false, AddEditTaskOldFragment.this, sizeDialogStyleID);
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

    @Override
    public void headerClickListener(String callingId) {
        try {
            if (DataClicked())
                SubmittedFormtoServer(finaljson);
            Log.d("Final Json :", finaljson + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void SubmittedFormtoServer(JSONObject finaljson) {
        SaveFormListRequest req = new SaveFormListRequest();
        req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(getActivity()) + "/mobi");
        req.setType("post");
        req.setAction(Form.ACTION_SAVE_FORM);
        ArrayList<SaveFormRequest> list = getFormlist();
        if (list.size() > 0) {
            req.setDataObj(list);
            Form.saveData(req, mActivity, AddEditTaskOldFragment.this, SAVE_PART);

        } else {
            Utilities.noPatterns(getActivity());
        }

    }

    private ArrayList<SaveFormRequest> getFormlist() {
        ArrayList<SaveFormRequest> arrayList = new ArrayList<>();
        long tmpid = Utilities.getCurrentTimeInMillis();
        SaveFormRequest saveFormRequest = new SaveFormRequest();
        saveFormRequest.setFdata(finaljson + "");
        saveFormRequest.setStmp(tmpid + "");
        saveFormRequest.setFtid(ftid + "");
        saveFormRequest.setId("0");
        saveFormRequest.setOid("");
        arrayList.add(saveFormRequest);
        return arrayList;
    }

    public boolean DataClicked() throws JSONException {
        JSONArray mainJson = new JSONArray();
        finaljson = new JSONObject();
        JSONObject jsonObject;
        for (int i = 0; i < allviews.size(); i++) {
            View view = allviews.get(i);
            if (view instanceof EditText) {
                    jsonObject = (JSONObject) view.getTag();
                if (jsonObject != null) {
                    String lbl = jsonObject.getJSONObject("fld").getString("lbl");
                    min = jsonObject.getJSONObject("fld").getString("min");
                    max = jsonObject.getJSONObject("fld").getString("max");
                    req = jsonObject.getJSONObject("fld").getString("req");
                    String editdata = ((EditText) view).getText().toString();
                    if (editdata.isEmpty()) {
                        if (Integer.valueOf(req) == 1) {
                            showErrorMessage(true, lbl, "");
                            return false;
                        } else
                            jsonObject.getJSONObject("fld").put("val", "");

                    } else {
                        if (editdata.length() < Integer.valueOf(min)) {
                            showErrorMessage(false, lbl, min);
                            return false;
                        } else if (editdata.length() > Integer.valueOf(max)) {
                            showErrorMessage(false, lbl, max);
                            return false;
                        } else
                            jsonObject.getJSONObject("fld").put("val", editdata);
                    }
                    mainJson.put(jsonObject);
                }
            } else if (view instanceof TextView) {
                jsonObject = (JSONObject) view.getTag();
                if ((((TextView) view).getText() != null)) {
                    mainJson.put(jsonObject);
                } else {
                    Log.e("Error", "This Feild is Empty " + jsonObject.getJSONObject("data").getString("lbl"));
                }
            }
        }
        finaljson.put("frm", mainJson);
        return true;
    }

    private void showErrorMessage(boolean isDataValueEmpty, String label, String character) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mContext, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("Error");
        if (isDataValueEmpty)
            builder.setMessage(label + " feild is required");
        else
            builder.setMessage(label + " feild must contain atleast " + character + " characrters");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    @Override
    public void ServiceStarter(RespCBandServST activity, Intent intent) {

    }

    @Override
    public void setResponseCallback(String response, Integer reqId) {

    }

    @Override
    public void setResponseCBActivity(Response response) {

    }

    @Override
    public void onCancelledBtn() {

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

            if (textViewForDate.getTag() == null) {
                textViewForDate.setText(mCurrentDate);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("data", curDate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                textViewForDate.setTag(jsonObject);
                txt.setTag(jsonObject);
            }

        }
    }
}