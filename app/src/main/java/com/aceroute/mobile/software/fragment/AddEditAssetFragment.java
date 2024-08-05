package com.aceroute.mobile.software.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.reference.Assets;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.Parts;
import com.aceroute.mobile.software.dialog.CustomListner;
import com.aceroute.mobile.software.dialog.DatePickerInterface;
import com.aceroute.mobile.software.dialog.MyDatePickerDialog;
import com.aceroute.mobile.software.dialog.MySearchDialog;
import com.aceroute.mobile.software.dialog.MySearchDiologInterface;
import com.aceroute.mobile.software.dialog.MySearchDiologInterfaceAlphaNum;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.SaveFormListRequest;
import com.aceroute.mobile.software.requests.SaveFormRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.RadioGroupPlus;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
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

public class AddEditAssetFragment extends BaseFragment implements HeaderInterface, RespCBandServST, DatePickerInterface {

    LinearLayout linearLayout;
    EditText editText;
    private int mheight = 500;
    ArrayList<View> allviews = new ArrayList<>(); //YD this could be as below
    //private HashMap<Long, HashMap<View, String>> allviews;  where long is view id from json, View will be the View itself and string will be value we store for each view.
    //TextView textViewForDate, textViewFortime, spinnerText;
    MySearchDialog searchDialg;
    long elemtSelectedInSearchDlog = -1;
    ArrayList<String> mCategoryArryList;
    ArrayList<Long> mCategoryArryListId;
    private JSONObject finaljson;
    private long fid;
    private int SAVE_ASSET =3;//YD randomly given the value
    private String currentOdrId;
    private String formMode;
    long assetId;
    public Order activeOrderObj;
    int topMarginForAllView = 30;
    int bottomMarginForAllView = 20;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.dynamic_form_layout_assets, null);
        TypeFaceFont.overrideFonts(mActivity, vi);
        mActivity.registerHeader(this);

        try {
            initiViewReference(vi);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vi;
    }

    private void initiViewReference(View v) throws JSONException {

        mActivity.setHeaderTitle("", "Asset", "");
        linearLayout = v.findViewById(R.id.linear_layout_assets);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        Bundle bundle = this.getArguments();
        String formData = bundle.getString("formData");
        fid = bundle.getLong("fid");
        currentOdrId = bundle.getString("OrderId");
        formMode = bundle.getString("AssetType");
        if(this.formMode.equals("EDIT ASSET"))
            assetId = bundle.getLong("AssetId");

        JSONObject obj = new JSONObject(formData);
        JSONArray m_jArry = obj.getJSONArray("frm");
        for (int i = 0; i < m_jArry.length(); i++) {
            JSONObject jo_inside = m_jArry.getJSONObject(i);

            //JSONObject elementJson = new JSONObject();
            int fieldTypeId = Integer.valueOf(jo_inside.getString("tid"));

            switch (fieldTypeId){
                case 1:
                    createEditText(jo_inside,  false, i);
                    break;

                case 2:
                    createEditTextArea(jo_inside, i);
                    break;

                case 3:
                    createEditText(jo_inside, true, i);
                    break;

                case 4:
                    createTextDateTime(jo_inside,  true, i);
                    break;

                case 5:
                    createTextDateTime(jo_inside,  false, i);
                    break;

                case 6:
                    createDropDown(jo_inside, false, i);
                    break;

                case 7:
                    createDropDown(jo_inside, true, i);
                    break;

                case 8:
                    createRadioButtons(jo_inside, false, i);
                    break;

                case 9:
                    createCheckBoxes(jo_inside, true, i);
                    break;

                case 10:
                    createHeader(jo_inside, true, i);
                    break;

                case 11:
                    CreateGeoButton(jo_inside,  true, i);
                    break;

                case 12:
                    CreatePartType(jo_inside, true, i);
                    break;
            }
        }
    }

    private void createEditText(JSONObject jsonObject, boolean isValidNumber, int position) throws JSONException {
        LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null)
            textView.setText(jsonObject.getString("lbl"));
        paramsTop.setMargins(15, topMarginForAllView, 15, 0);
        textView.setLayoutParams(paramsTop);
        textView.setTextSize(20);
        linearLayout.addView(textView);

        LinearLayout.LayoutParams paramsBottom = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        EditText editText = new EditText(mActivity);
        editText.setTag(jsonObject);
        editText.setSingleLine(true);
        if(isValidNumber)
            editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        paramsBottom.setMargins(15, 5, 15, topMarginForAllView);
        editText.setLayoutParams(paramsBottom);
        editText.setBackground(getResources().getDrawable(R.drawable.roundable_edittext));

        if(formMode.equals("EDIT ASSET")){
            editText.setText(jsonObject.getString("val"));
        }
        allviews.add(editText);
        linearLayout.addView(editText);
    }

    private void createEditTextArea(final JSONObject jsonObject,  int position) throws JSONException {
        LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null)
            textView.setText(jsonObject.getString("lbl"));
        paramsTop.setMargins(15, topMarginForAllView, 15, 0);
        textView.setLayoutParams(paramsTop);
        textView.setTextSize(20);
        linearLayout.addView(textView);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 230); // Width , height
        final EditText editText = new EditText(mActivity);
//        jsonObject.put("val", editText.getText().toString());
        editText.setTag(jsonObject);
        if(formMode.equals("EDIT ASSET")){
            editText.setText(jsonObject.getString("val"));
        }
        lparams.setMargins(15, 5, 15, bottomMarginForAllView);
        editText.setBackground(getResources().getDrawable(R.drawable.roundable_edittext));
        editText.setLayoutParams(lparams);
        allviews.add(editText);
        linearLayout.addView(editText);
    }

    private void createTextDateTime(final JSONObject jsonObject, boolean isDate, int position) throws JSONException {

        if (!isDate) {
            LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
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
            paramsTop.setMargins(15, topMarginForAllView, 15, 0);
            textView.setLayoutParams(paramsTop);
            textView.setTextSize(20);
            linearLayout.addView(textView);

            LinearLayout.LayoutParams paramsBottom = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            final TextView textViewFortime = new TextView(mActivity);
//            jsonObject.put("val", textViewFortime.getText().toString());
//            mainjson.put("data", jsonObject);
            textViewFortime.setTag(R.string.parent_value, jsonObject);//YD doing this only for AddEditFormFragment
            paramsBottom.setMargins(10, 5, 10, bottomMarginForAllView);
            textViewFortime.setHeight((int) convertPixelsToDp(340.0f, getActivity()));
            textViewFortime.setPadding(10, 10, 10, 10);
            textViewFortime.setTextSize(22.0f);
            textViewFortime.setBackground(getResources().getDrawable(R.drawable.rounded_green_fill_bg));
            textViewFortime.setLayoutParams(paramsBottom);
            textViewFortime.setTextColor(getResources().getColor(R.color.white));
            if(formMode.equals("EDIT ASSET"))
                textViewFortime.setText(getDateOrTimeToShow(jsonObject.getString("val"), false));
            allviews.add(textViewFortime);
            linearLayout.addView(textViewFortime);
            textViewFortime.setTag(stime);
            textViewFortime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utilities.getTimeRange(getActivity(), Utilities.TIME_TYPE_START, ((TextView) v), textViewFortime, new CustomListner(){

                        @Override
                        public void onClick(View view) {
                            try {
                                JSONObject jsonObj = (JSONObject) view.getTag(R.string.parent_value);
                                Date CurrentValue = (Date) view.getTag();
                                String dateToSave = Utilities.convertDateToUtc(CurrentValue.getTime());

                                jsonObj.put("val", dateToSave);// 1969/12/31 20:30 -00:00
                            }catch (JSONException j){
                                j.printStackTrace();
                            }
                        }
                    });
                }
            });
        } else {
            LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            TextView textView = new TextView(mActivity);
            if (jsonObject.getString("lbl") != null)
                textView.setText(jsonObject.getString("lbl"));
            paramsTop.setMargins(15, topMarginForAllView, 15, 0);
            textView.setLayoutParams(paramsTop);
            textView.setTextSize(20);
            linearLayout.addView(textView);
            TextView textViewForDate = new TextView(mActivity);

            LinearLayout.LayoutParams paramsBottom = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            paramsBottom.setMargins(10, 5, 10, bottomMarginForAllView);
            textViewForDate.setHeight((int) convertPixelsToDp(340.0f, getActivity()));
            textViewForDate.setPadding(10, 10, 10, 10);
            textViewForDate.setTextSize(22.0f);
            textViewForDate.setBackground(getResources().getDrawable(R.drawable.rounded_green_fill_bg));
            textViewForDate.setLayoutParams(paramsBottom);
            textViewForDate.setTextColor(getResources().getColor(R.color.white));
            textViewForDate.setTag(jsonObject);
            if(formMode.equals("EDIT ASSET"))
                textViewForDate.setText(getDateOrTimeToShow(jsonObject.getString("val"), true));
            allviews.add(textViewForDate);
            linearLayout.addView(textViewForDate);
            textViewForDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard();
                    getCalender((TextView) v);
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

    private String getDateOrTimeToShow(String newOrderStartDate, boolean isDate) {//2020/07/15 6:15 -00:00 OR 2020/07/15 18:30 -00:00

        if( newOrderStartDate != null){
            // handle date if status is 8 yash TODO
            SimpleDateFormat convStrToDate = new SimpleDateFormat("yyyy/MM/dd HH:mm Z");
            //SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf_date = new SimpleDateFormat("MMM dd yyyy");
            SimpleDateFormat sdf_time = new SimpleDateFormat("hh:mm a");
            Date date;
            try {
                date = convStrToDate.parse(newOrderStartDate);
                if(date!= null && isDate)
                    return sdf_date.format(date);
                else if(date!= null)
                    return sdf_time.format(date);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
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

            MyDatePickerDialog dialog = new MyDatePickerDialog(mActivity, new mDateSetListener(view), mYear, mMonth, mDay, true, AddEditAssetFragment.this, sizeDialogStyleID);
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

    private void createDropDown(final JSONObject jsonObject, final boolean isCheckbox, final int position) throws JSONException {
        LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null)
            textView.setText(jsonObject.getString("lbl"));
        paramsTop.setMargins(10, topMarginForAllView, 10, 0);
        textView.setLayoutParams(paramsTop);
        textView.setAllCaps(true);
        textView.setTextSize(20);
        linearLayout.addView(textView);
        final TextView textViewOpt = new TextView(mActivity);
        //spinnerText.setTag(position);
        //jsonObject.put("val", jsonObject.getString("ddnval"));//        mainjson.put("position", position);

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        lparams.setMargins(10, 5, 10, bottomMarginForAllView);
        textViewOpt.setHeight((int) convertPixelsToDp(340.0f, getActivity()));
        textViewOpt.setPadding(10, 1, 10, 15);
        textViewOpt.setTextSize(22.0f);

        HashMap<String,String> selectedStrAndIdMap;
        String idSelectedForRadio = null;
        String[] keySet = null;
        if(formMode.equals("EDIT FORM")) {
            selectedStrAndIdMap = getDropDownVals(jsonObject,jsonObject.getString("val"));
            if(selectedStrAndIdMap.size() >0) {
                keySet = selectedStrAndIdMap.keySet().toArray(new String[selectedStrAndIdMap.size()]);
                idSelectedForRadio = keySet[0];
                String stringToShow = selectedStrAndIdMap.get(keySet[keySet.length - 1]);
                textViewOpt.setText(stringToShow);
            }
        }
        textViewOpt.setBackground(getResources().getDrawable(R.drawable.rounded_green_fill_bg));
        textViewOpt.setLayoutParams(lparams);
        textViewOpt.setTextColor(getResources().getColor(R.color.white));
        textViewOpt.setTag(jsonObject);
        textViewOpt.setId( Integer.valueOf(jsonObject.getString("id")));
        allviews.add(textViewOpt);
        linearLayout.addView(textViewOpt);

        final int pkid = !jsonObject.getString("pkid").equals("") ? Integer.valueOf(jsonObject.getString("pkid")) : -1;

        final String finalIdSelectedForRadio = idSelectedForRadio;
        final String[] finalKeySet = keySet;
        textViewOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> arrayList = new ArrayList<String>();
                final ArrayList<String> idslist = new ArrayList<String>();//YD ddnva
                final TextView tempTextview = textViewOpt;
                try {
                    if (jsonObject.getString("ddn") != null) {
                        String str[] = jsonObject.getString("ddn").split(",");
                        String ids[] = jsonObject.getString("ddnval").split(",");
                        String fkids[] = !jsonObject.getString("ddnfk").equals("") ? jsonObject.getString("ddnfk").split(","): null;

                        String[] parentSelectedVal = tempTextview.getTag(R.string.parent_value)!= null ? tempTextview.getTag(R.string.parent_value).toString().split("\\,") : null;

                        if(parentSelectedVal== null && formMode.equals("EDIT FORM") && pkid != -1) {
                            parentSelectedVal = getParentSelectedValues(pkid);
                        }
                        for (int k = 0; k < str.length; k++) {

                            if(parentSelectedVal!= null && parentSelectedVal.length > 0 ){//will get the selected value of parent

                                for(int i=0; i< parentSelectedVal.length; i++) {
                                    if (fkids[k].equals( parentSelectedVal[i])) {
                                        idslist.add(ids[k]);//YD ddnva
                                        arrayList.add(str[k]);
                                    }
                                }
                            }
                            else {
                                idslist.add(ids[k]);//YD ddnva
                                arrayList.add(str[k]);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideSoftKeyboard();
                if(isCheckbox)
                    openCheckboxDialog(idslist, arrayList, jsonObject,tempTextview);
                else
                    showRadioDialog(idslist, arrayList, jsonObject,tempTextview, finalIdSelectedForRadio);
            }
        });
    }

    private String[] getParentSelectedValues( int pkid) {
        try {
            for (View vii : allviews) {
                if (vii.getId() == pkid) {
                    TextView textView  = ((TextView) vii);
                    JSONObject tempJsonObject = (JSONObject) textView.getTag();
                    return !tempJsonObject.getString("val").equals("") ? tempJsonObject.getString("val").split("\\,"): null;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    private HashMap<String,String> getDropDownVals(JSONObject jsonObject, String val) {
        try {
            String str[] = jsonObject.getString("ddn").split(",");
            String ids[] = jsonObject.getString("ddnval").split(",");
            String[] selectedVal = val != null && !val.equals("") ? val.split("\\,") : null;

            String idSelected = null;
            String completeStrToDisplay= null;
            HashMap<String,String> stringListAndSelectedIdsMap = new HashMap<String,String>();
            int strLen = 0;

            for (int k = 0; k < ids.length; k++) {

                if (selectedVal != null && selectedVal.length > 0) {

                    for (int i = 0; i < selectedVal.length; i++) {

                        if (ids[k].equals(selectedVal[i])) {
                            if(strLen ==0) {
                                idSelected = ids[k];
                                completeStrToDisplay = str[k];

                                stringListAndSelectedIdsMap.put(idSelected, completeStrToDisplay);
                            }
                            else {
                                idSelected = ids[k];
                                completeStrToDisplay = completeStrToDisplay + ", " + str[k];
                                stringListAndSelectedIdsMap.put(idSelected, completeStrToDisplay);
                            }

                            strLen++;
                        }
                    }
                }
                else break;
            }
            return stringListAndSelectedIdsMap;
        }
        catch (JSONException je){
            je.printStackTrace();
        }
        return null;
    }

    private void showRadioDialog(ArrayList<String> idslist, ArrayList<String> nmsLst, final JSONObject jsonObject, final TextView textview, String elemtSelectedInSearchDlog) {
        searchDialg = new MySearchDialog(mActivity, "Category", "", idslist, nmsLst, elemtSelectedInSearchDlog, true);

        searchDialg.setkeyListenderAlphaNum(new MySearchDiologInterfaceAlphaNum() {
            @Override
            public void onButtonClick() {
                super.onButtonClick();
                searchDialg.cancel();
            }

            @Override
            public void onItemSelected(String idSelected, String nameSelected) {
                super.onItemSelected(idSelected, nameSelected);

                String data = idSelected.toString();
                try {
                    JSONObject mainJson = new JSONObject();
                    textview.setText(nameSelected);
                    jsonObject.put("val", data);
                    //mainJson.put("fld", jsonObject);
                    textview.setTag(jsonObject);
                    searchDialg.cancel();
                    //elemtSelectedInSearchDlog = idSelected;

                    checkAndChangeValOfEffectedDropDwn(jsonObject);
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

    private void checkAndChangeValOfEffectedDropDwn(final JSONObject jsonObject) {

        try {
            String childIdOfCurrentDropDwn ;
            int i= 0;
            childIdOfCurrentDropDwn = jsonObject.get("ckid").toString();
            boolean viewNotAvailable;//YD this flag will test if the child view is available or not

            do {
                if (!childIdOfCurrentDropDwn.equals("")) {
                    viewNotAvailable = true;
                    int dropDownIdEffected = Integer.valueOf(childIdOfCurrentDropDwn);

                    for (View vii : allviews) {
                        if (vii.getId() == dropDownIdEffected) {
                            viewNotAvailable = false;
                            TextView textView  = ((TextView) vii);
                            textView.setText("");
                            JSONObject tempJsonObject = (JSONObject) textView.getTag();
                            childIdOfCurrentDropDwn = tempJsonObject.get("ckid").toString();
                            //childIdOfCurrentDropDwn = ((JSONObject)tempJsonObject.get("fld")).get("ckid").toString();

                            if(i == 0)
                                vii.setTag(R.string.parent_value, jsonObject.get("val").toString());
                            i++;
                            break;
                        }
                    }
                    if(viewNotAvailable)
                        return;
                }
            }while(!childIdOfCurrentDropDwn.equals(""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void openCheckboxDialog(final ArrayList<String> idslist, final ArrayList<String> nmsLst, final JSONObject jsonObject, final TextView textview){//YD ddnva
        try{
            final LinkedHashMap<String, String> seletedItems = new LinkedHashMap<String, String> ();


            //ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_multiple_choice,textview.getId(),nmsLst){};
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_multiple_choice, nmsLst){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    TextView textView = (TextView)view.findViewById(android.R.id.text1);
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
            String valuesSelected = jsonObject.get("val").toString();
            String[] strValue = !valuesSelected.equals("") ? valuesSelected.split("\\,", -1) : null;

            for(int i=0; strValue != null && i<strValue.length; i++){
                int indexSelect = idslist.indexOf(strValue[i]);
                if(indexSelect>=0) {
                    listView.setItemChecked(Integer.valueOf(indexSelect), true);
                    seletedItems.put(strValue[i], nmsLst.get(indexSelect));
                }
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
                    CheckedTextView item = (CheckedTextView) view;
                    if(item.isChecked()) {
                        if(seletedItems.get(idslist.get(position)) == null)
                            seletedItems.put(idslist.get(position), nmsLst.get(position));
                    }
                    else{
                        //if(seletedItems.contains(idslist.get(position)))
                        seletedItems.remove(idslist.get(position));
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
                        String selectedOptions ="";
                        String selectedNames = "";
                        int i = 0;

                        for(String key : seletedItems.keySet()){
                            if(i==0) {
                                selectedOptions = key;
                                selectedNames = String.valueOf(seletedItems.get(key));
                            }
                            else {
                                selectedOptions += "," + key;
                                selectedNames =  selectedNames +", "+ String.valueOf(seletedItems.get(key));
                            }
                            i++;
                        }
                        Log.i("Total Workers: ", String.valueOf(seletedItems.size()));

                        //textview.setTag(R.string.parent_value, seletedItems );
                        //odr_detail_worker_edit.setText(workerNames);


                        try {
                            JSONObject mainJson = new JSONObject();
                            textview.setText(selectedNames);
                            jsonObject.put("val", selectedOptions);
                            //mainJson.put("fld", jsonObject);
                            textview.setTag(jsonObject);
                            //searchDialg.cancel();
                            //elemtSelectedInSearchDlog = idSelected;

                            checkAndChangeValOfEffectedDropDwn(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    private void createRadioButtons(JSONObject jsonObject, boolean b, int position) throws JSONException {
        LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null)
            textView.setText(jsonObject.getString("lbl"));
        paramsTop.setMargins(15, topMarginForAllView, 15, 0);
        textView.setLayoutParams(paramsTop);
        textView.setTextSize(20);
        linearLayout.addView(textView);

//Radio start
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(0,0,0,bottomMarginForAllView);//40 was the old value for bottom

        RadioGroupPlus.LayoutParams radioGroupLayParams = new RadioGroupPlus.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams lparamsTbRow = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparamsTbRow.setMargins(0,0,0,10);

        TableRow.LayoutParams tblRowLayParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tblRowLayParams.setMargins(0,20,0,0);
        tblRowLayParams.weight = 1;

        LinearLayout allTableRowViews = new LinearLayout(mActivity);
        allTableRowViews.setOrientation(LinearLayout.VERTICAL);
        allTableRowViews.setLayoutParams(radioGroupLayParams);

        String[] optionList = jsonObject.getString("ddn").split(",");
        String[] idsList = jsonObject.getString("ddnval").split(",");
        String selectedId = jsonObject.getString("val");

        TableRow tableRow = null;
        int selectedIdForRadioPlus = -1;

        for(int i=0; i<optionList.length; i++) {

            if (i % 2 == 0) {
                tableRow = new TableRow(mActivity);
                tableRow.setLayoutParams(lparamsTbRow);
                allTableRowViews.addView(tableRow);
            }
            RadioButton radioButton = new RadioButton(mActivity);
            radioButton.setText(optionList[i]);
            radioButton.setId(Integer.valueOf(idsList[i]));
            if(formMode.equals("EDIT ASSET") && Integer.valueOf(selectedId).equals(Integer.valueOf(idsList[i]))) {
                selectedIdForRadioPlus = Integer.valueOf(idsList[i]);
                radioButton.setChecked(true);
            }
            else
                radioButton.setChecked(false);
            radioButton.setTextColor(Color.parseColor("#000000"));
            radioButton.setTextSize(20);
            radioButton.setLayoutParams(tblRowLayParams);
            tableRow.addView(radioButton);
        }

        RadioGroupPlus radioGroupPlus = null;
        if(formMode.equals("EDIT ASSET"))
            radioGroupPlus = new RadioGroupPlus(mActivity, selectedIdForRadioPlus);
        else
            radioGroupPlus = new RadioGroupPlus(mActivity, -1);

        radioGroupPlus.setLayoutParams(lparams);

        radioGroupPlus.setTag(jsonObject);
        radioGroupPlus.addView(allTableRowViews);

        allviews.add(radioGroupPlus);
        linearLayout.addView(radioGroupPlus);

        radioGroupPlus.setOnCheckedChangeListener(new RadioGroupPlus.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroupPlus group, int checkedId) {
                try {
                    JSONObject jsonObj = (JSONObject) group.getTag();
                    jsonObj.put("val", checkedId);

                    Toast.makeText(mActivity, "button clicked ", Toast.LENGTH_LONG);
                }catch (Exception e){

                }
            }
        });

    }


    private void createCheckBoxes(JSONObject jsonObject, boolean b, int position) throws JSONException  {
        LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null)
            textView.setText(jsonObject.getString("lbl"));
        paramsTop.setMargins(15, topMarginForAllView, 15, 0);
        textView.setLayoutParams(paramsTop);
        textView.setTextSize(20);
        linearLayout.addView(textView);

// Checkbox start
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(0,5,0,bottomMarginForAllView);//40 was the old value for bottom

        LinearLayout.LayoutParams tableRowLlparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TableRow.LayoutParams tblRowLayParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tblRowLayParams.setMargins(0,20,0,0);
        tblRowLayParams.weight = 1;

        int controlID = Integer.valueOf(jsonObject.getString("id"));

        LinearLayout allTableRowViews = new LinearLayout(mActivity);
        allTableRowViews.setOrientation(LinearLayout.VERTICAL);
        allTableRowViews.setLayoutParams(lparams);
        allTableRowViews.setId(controlID);
        allTableRowViews.setTag(jsonObject);

        String[] optionList = jsonObject.getString("ddn").split(",");
        String[] idsList = jsonObject.getString("ddnval").split(",");
        String[] selectedIds = jsonObject.getString("val").split(",");

        TableRow tableRow = null;

        for(int i=0; i<optionList.length; i++) {

            if (i % 2 == 0) {
                tableRow = new TableRow(mActivity);
                tableRow.setLayoutParams(tableRowLlparams);
                allTableRowViews.addView(tableRow);
            }
            CheckBox checkBox = new CheckBox(mActivity);
            checkBox.setText(optionList[i]);
            checkBox.setId(Integer.valueOf(idsList[i]));
            if(formMode.equals("EDIT ASSET")){
                for (int j=0; j<selectedIds.length; j++){
                    if(Long.valueOf(selectedIds[j]).equals(Long.valueOf(idsList[i]))){
                        checkBox.setChecked(true);
                    }
                }
            }
            else
                checkBox.setChecked(false);
            checkBox.setTextColor(Color.parseColor("#000000"));
            checkBox.setTextSize(20);

            //YD logic to set width if there is only one element in the last row.
            if(i == optionList.length-1 && i % 2 == 0){

                TableRow.LayoutParams tblRowLayParamsLstCheckBox = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tblRowLayParamsLstCheckBox.setMargins(0,20,0,0);
                checkBox.setLayoutParams(tblRowLayParamsLstCheckBox);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                checkBox.setWidth(width/2);
            }
            else
                checkBox.setLayoutParams(tblRowLayParams);

            tableRow.addView(checkBox);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int layoutID = ((LinearLayout)buttonView.getParent().getParent()).getId();
                    try {
                        for(int i=0; i<allviews.size(); i++){
                            if (allviews.get(i).getId() == layoutID){
                                JSONObject jsonObj = (JSONObject) ((LinearLayout) allviews.get(i)).getTag();
                                String[] finalValue = !jsonObj.get("val").toString().equals("") ? jsonObj.get("val").toString().split("\\,") : null;


                                if(finalValue != null){
                                    ArrayList<String> finalValueList = new ArrayList<String>(Arrays.asList(finalValue));
                                    boolean valueRemoved = false;
                                    for(int j=0; j<finalValueList.size(); j++){
                                        if(Integer.valueOf(finalValueList.get(j)).equals(buttonView.getId())){
                                            if(isChecked){}
                                            else{
                                                finalValueList.remove(j);
                                                valueRemoved = true;
                                            }
                                        }
                                    }
                                    if(!valueRemoved){
                                        //Value was not present in the finalValueList
                                        if (isChecked) {
                                            finalValueList.add(String.valueOf(buttonView.getId()));
                                        }
                                    }

                                    String finalValToSav = "";
                                    if(finalValueList.size()==0)//this will be called when last value get remove form the list.
                                        jsonObj.put("val", finalValToSav);

                                    for(int k=0; k<finalValueList.size(); k++){
                                        finalValToSav = k== 0 ? finalValueList.get(k) : finalValToSav +"," + finalValueList.get(k);
                                    }
                                    jsonObj.put("val", finalValToSav);

                                }
                                else{
                                    if(isChecked)
                                        jsonObj.put("val", buttonView.getId());
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(mActivity,String.valueOf(buttonView.getId()),Toast.LENGTH_LONG);
                }
            });
        }

        allviews.add(allTableRowViews);
        linearLayout.addView(allTableRowViews);

    }


    private void createHeader(JSONObject jsonObject,  boolean b, int position) throws JSONException {
        LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null)
            textView.setText(jsonObject.getString("lbl"));
        paramsTop.setMargins(15, topMarginForAllView, 15, bottomMarginForAllView);
        textView.setLayoutParams(paramsTop);
        textView.setTextSize(24);
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        linearLayout.addView(textView);
        allviews.add(textView);
    }

    private void CreateGeoButton(JSONObject jsonObject,  boolean b, int i) throws JSONException {

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null) {
            textView.setText(jsonObject.getString("lbl"));
        }
        lparams.setMargins(15, topMarginForAllView, 15, bottomMarginForAllView);
        textView.setHeight((int) convertPixelsToDp(340.0f, getActivity()));
        textView.setLayoutParams(lparams);
        textView.setTextSize(20);
        if(formMode.equals("EDIT ASSET") && !jsonObject.getString("val") .equals("") ){
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
        }
        textView.setPadding(10, 20, 30, 10);
        textView.setBackground(getResources().getDrawable(R.drawable.rounded_green_fill_bg));
        //textView.setCheckMarkDrawable(R.drawable.check);
        //textView.drawableHotspotChanged(-30, -50);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setTag(jsonObject);
        linearLayout.addView(textView);
        allviews.add(textView);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)v).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
                String currentGeoLocation = Utilities.getLocation(mActivity);
                try {
                    if (currentGeoLocation != null && !currentGeoLocation.equals("")) {
                        JSONObject jsonObj = (JSONObject) (((TextView) v).getTag());
                        jsonObj.put("val", currentGeoLocation);
                    } else{
                        JSONObject jsonObj = (JSONObject) (((TextView) v).getTag());
                        jsonObj.put("val","");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void CreatePartType(JSONObject jsonObject,  boolean b, int position) throws JSONException {
        LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        HashMap<Long, Parts> unsortedPartList = (HashMap<Long, Parts>) DataObject.partTypeXmlDataStore;
        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null) {
            textView.setText(jsonObject.getString("lbl"));
        }
        paramsTop.setMargins(10, topMarginForAllView, 10, 0);
        textView.setLayoutParams(paramsTop);
        textView.setAllCaps(true);
        textView.setTextSize(20);
        linearLayout.addView(textView);

//DropDown
        final TextView textViewOpt = new TextView(mActivity);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        lparams.setMargins(10, 5, 10, bottomMarginForAllView);
        textViewOpt.setHeight((int) convertPixelsToDp(340.0f, getActivity()));
        textViewOpt.setPadding(10, 1, 10, 15);
        textViewOpt.setTextSize(22.0f);
        textViewOpt.setText("");//YD TODO
        textViewOpt.setBackground(getResources().getDrawable(R.drawable.rounded_green_fill_bg));
        textViewOpt.setLayoutParams(lparams);
        if(formMode.equals("EDIT ASSET") && !jsonObject.getString("val") .equals("") ) {
            textViewOpt.setText(unsortedPartList.get(Long.valueOf(jsonObject.getString("val"))).getName());
        }
        textViewOpt.setTextColor(getResources().getColor(R.color.white));
        textViewOpt.setTag(jsonObject);
        textViewOpt.setId( Integer.valueOf(jsonObject.getString("id")));
        allviews.add(textViewOpt);
        linearLayout.addView(textViewOpt);

        HashMap<Long, Parts> partTypeList = sortPartCatagoryLst(unsortedPartList);

        Long[] keys = partTypeList.keySet().toArray(new Long[partTypeList.size()]);

        mCategoryArryList= new ArrayList<String>();
        mCategoryArryListId = new ArrayList<Long>();

        for(int i = 0; i < partTypeList.size(); i++)
        {
            Parts parttypeObj = partTypeList.get(keys[i]);
            mCategoryArryList.add(parttypeObj.getName());
            mCategoryArryListId.add(parttypeObj.getId());
        }


        textViewOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog(mCategoryArryList, mCategoryArryListId, textViewOpt);
            }
        });
    }

    private static HashMap<Long, Parts> sortPartCatagoryLst(HashMap<Long, Parts> unsortMap) {

        // Convert Map to List
        List<Map.Entry<Long, Parts>> list =
                new LinkedList<Map.Entry<Long, Parts>>(unsortMap.entrySet());

        // Sort list_cal with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<Long, Parts>>() {
            public int compare(Map.Entry<Long, Parts> o1,
                               Map.Entry<Long, Parts> o2) {
                return (o1.getValue().getName()).compareTo(o2.getValue().getName());
            }
        });

        // Convert sorted map back to a Map
        HashMap<Long, Parts> sortedMap = new LinkedHashMap<Long, Parts>();
        for (Iterator<Map.Entry<Long, Parts>> it = list.iterator(); it.hasNext();) {
            Map.Entry<Long, Parts> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    private void showCategoryDialog(ArrayList<String> mCategoryArryList, ArrayList<Long> mCategoryArryListId, final TextView textViewOpt)
    {
        searchDialg = new MySearchDialog(mActivity, "Category", "", mCategoryArryListId,mCategoryArryList , elemtSelectedInSearchDlog);

        searchDialg.setkeyListender(new MySearchDiologInterface(){
            @Override
            public void onButtonClick() {
                super.onButtonClick();
                searchDialg.cancel();
            }

            @Override
            public void onItemSelected(Long idSelected, String nameSelected) {
                super.onItemSelected(idSelected, nameSelected);

                try {
                    ((TextView) textViewOpt).setText(nameSelected);
                    JSONObject jsonObj = (JSONObject) (((TextView) textViewOpt).getTag());
                    jsonObj.put("val", idSelected);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

               /* edtMaterialCategory.setText(nameSelected);
                Parts parttypeObj = partTypeList.get(idSelected);
                edtMaterialCategory.setTag(String.valueOf(parttypeObj.getId()));
                edtMaterialDesc.setText(parttypeObj.getDesc());
                elemtSelectedInSearchDlog = idSelected;*/
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

    public void hideSoftKeyboard() {
        if (mActivity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void headerClickListener(String callingId) {
        if(callingId.equals(BaseTabActivity.HeaderDonePressed)){
            try {
                if (DataClicked())
                    SubmittedAssettoServer(finaljson);
                Log.d("Final Json :", finaljson + "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void SubmittedAssettoServer(JSONObject finaljson) {
        SaveFormListRequest req = new SaveFormListRequest();
        req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(getActivity()) + "/mobi");
        req.setType("post");
        req.setAction(Assets.ACTION_SAVE_ORDER_ASSETS);
        ArrayList<SaveFormRequest> list = getFormlist();
        if (list.size() > 0) {
            req.setDataObj(list);
            Assets.saveData(req, mActivity, AddEditAssetFragment.this, SAVE_ASSET);

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
        saveFormRequest.setFtid(fid + "");
        if(formMode.equals("EDIT ASSET"))
            saveFormRequest.setId(assetId+"");
        else
            saveFormRequest.setId("0");
        saveFormRequest.setOid(currentOdrId);
        saveFormRequest.setCustId("" + activeOrderObj.getCustomerid());
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
                    String lbl = jsonObject.getString("lbl");
                    String min = jsonObject.getString("min");
                    String max = jsonObject.getString("max");
                    String req = jsonObject.getString("req");
                    String editdata = ((EditText) view).getText().toString();
                    if (editdata.isEmpty()) {
                        if (Integer.valueOf(req) == 1) {
                            showErrorMessage(true, lbl, "","");
                            return false;
                        } else
                            jsonObject.put("val", "");

                    } else {
                        if (editdata.length() < Integer.valueOf(min)) {
                            showErrorMessage(false, lbl, min, max);
                            return false;
                        } else if (editdata.length() > Integer.valueOf(max)) {
                            showErrorMessage(false, lbl, min, max);
                            return false;
                        } else
                            jsonObject.put("val", editdata);
                    }
                    mainJson.put(jsonObject);
                }
            } else if (view instanceof TextView) {
                if(view.getTag() instanceof JSONObject)
                    jsonObject = (JSONObject) view.getTag();
                else
                    jsonObject = (JSONObject) view.getTag(R.string.parent_value);

                if(jsonObject!= null) {
                    String lbl = jsonObject.getString("lbl");
                    String req = jsonObject.getString("req");
                    String tid = jsonObject.getString("tid");
                    String val = jsonObject.getString("val");

                    if(Integer.valueOf(tid) == 11 && val.equals("") && Integer.valueOf(req) == 1){
                        showErrorStringMessage(lbl, "Select Geo For this Form");
                        return false;
                    }
                    else if ((((TextView) view).getText() == null || ((TextView) view).getText().equals("")) && Integer.valueOf(req) == 1) {
                        showErrorMessage(true, lbl, "", "");
                        Log.e("Error", "This Feild is Empty " + jsonObject.getString("lbl"));
                        return false;
                    } else {
                        mainJson.put(jsonObject);
                    }
                }
            } else  if(view instanceof RadioGroupPlus || view instanceof LinearLayout){
                jsonObject = view.getTag()!=null && view.getTag() != null ? (JSONObject) view.getTag() : null;
                if(jsonObject != null){
                    String lbl = jsonObject.getString("lbl");
                    String req = jsonObject.getString("req");

                    String selectedValue = jsonObject.get("val").toString();
                    if (selectedValue != null && !selectedValue.equals("")) { mainJson.put(jsonObject);}
                    else if (Integer.valueOf(req) == 1) {
                        showErrorMessage(true, lbl,"", "");
                        return false;
                    }
                }
            }
        }
        finaljson.put("frm", mainJson);
        return true;
    }

    private void showErrorMessage(boolean isDataValueEmpty, String label, String minStr, String maxStr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("Error");
        if (isDataValueEmpty)
            builder.setMessage(label + " is missing required data. Please enter to continue.");
        else {
            builder.setMessage(label + " must be between " + minStr + " and "+ maxStr+ " characters in length. Please enter required data to continue.");
        }
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        //YD 2020 doing UI setting for dialog
        Utilities.setDividerTitleColor(alertDialog, mheight, mActivity);
        Button button_positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Utilities.setDefaultFont_12(button_positive);

    }

    private void showErrorStringMessage(String label, String StringMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("Error");
        builder.setMessage(label +" "+ StringMsg);
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
        if (response!=null) {
            if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED))) {
                if (response.getId() == SAVE_ASSET) {
                    if(formMode.equals("ADD ASSET"))
                        getAndUpdateNumberOfOrderAssets(currentOdrId);
                    goBack(mActivity.SERVICE_Thread);
                }
            }
        }
    }

    private void getAndUpdateNumberOfOrderAssets(String orderId) {
        HashMap< Long , Order> orderMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
        Order odrObj = orderMap.get(Long.parseLong(orderId));
        odrObj.setAssetsCount(odrObj.getAssetsCount()+1);

        DataObject.ordersXmlDataStore=orderMap;
    }

    private void goBack(final int threadType) {

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.popFragments(threadType);
            }
        });
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

            String dateToSave = null;
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

                Calendar cal = Calendar.getInstance();
                cal.setTime(curDate);
                dateToSave = Utilities.convertDateToUtc(cal.getTimeInMillis());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String mCurrentDate = new SimpleDateFormat("MMM").format(curDate) + " " + editDate + " " + mYear;
            txt.setText(mCurrentDate);

            try {
                JSONObject jsonObj = (JSONObject) txt.getTag();
                jsonObj.put("val", dateToSave);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setActiveOrderObject(Order orderObj)
    {
        activeOrderObj = orderObj;
    }
}
