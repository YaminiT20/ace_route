package com.aceroute.mobile.software.notes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.SplashII;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderNotes;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.fragment.BaseFragment;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.updateOrderRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;

import org.json.JSONException;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by xelium on 4/18/16.
 */

public class AlertnNoteFrag extends BaseFragment implements HeaderInterface, RespCBandServST {

    EditText edt_alt, edt_po, edt_dtl, edt_inv;
    EditText edt_note;
    private MyDialog dialog;
    String previousAlert, prviouseModel, previouseReg, previouseOdometer;
    String previousNote;
    private Order activeOdrObj;
    private int SAVEORDERFIELD_STATUS_ALERT = 1;
    private int SAVEORDERFIELD_STATUS_CATEGORY = 2;
    private int SAVENOTE = 2;
    static int noOfReqSent = 0;
    static int noOfReqCompleted = 0;
    ScrollView scrollAlertNote;
    TextView txt_note, txt_alt, txt_po, txt_dtl, txt_inv;
    private LinearLayout parentView;
    String[] splitrules;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.alert_note, null);
        initiViewReference(v);
        mActivity.registerHeader(this);
        if (PreferenceHandler.getSummaryHead(mActivity) != null && !PreferenceHandler.getPrefOrderGroup(mActivity).equals(""))
            mActivity.setHeaderTitle("", PreferenceHandler.getPrefOrderGroup(getActivity()).toUpperCase(), "");
        else
            mActivity.setHeaderTitle("", PreferenceHandler.getPrefOrderGroup(mActivity), "");
        return v;
    }

    private void initiViewReference(View v) {

        Bundle b = this.getArguments();
        previousAlert = b.getString("PREVIOUS_ALERT");
        previousNote = b.getString("PREVIOUS_NOTE");
        prviouseModel = b.getString("PREVIOUS_DTL");
        previouseReg = b.getString("PREVIOUS_PO");
        previouseOdometer = b.getString("PREVIOUS_ODO");
        parentView = (LinearLayout) v.findViewById(R.id.parentview_alertNote);

        txt_alt = (TextView) v.findViewById(R.id.txt_alt);
        txt_note = (TextView) v.findViewById(R.id.txt_note);
        txt_dtl = (TextView) v.findViewById(R.id.txt_name_dtl);
        txt_po = (TextView) v.findViewById(R.id.txt_name_po);
        txt_inv = (TextView) v.findViewById(R.id.txt_name_inv);

        edt_alt = (EditText) v.findViewById(R.id.edt_alt);
        edt_note = (EditText) v.findViewById(R.id.edt_note);
        edt_dtl = (EditText) v.findViewById(R.id.edt_route_odp);
        edt_po = (EditText) v.findViewById(R.id.edt_ssd_odp);
        edt_inv = (EditText) v.findViewById(R.id.edt_description_odp);

        scrollAlertNote = (ScrollView) v.findViewById(R.id.scrollAlertNote);

        if (PreferenceHandler.getAlertHead(mActivity) != null && !PreferenceHandler.getAlertHead(mActivity).equals(""))
            txt_alt.setText(PreferenceHandler.getAlertHead(mActivity).toUpperCase());
        else
            txt_alt.setText(getString(R.string.lbl_dlg_alert));

        if (PreferenceHandler.getNoteHead(mActivity) != null && !PreferenceHandler.getNoteHead(mActivity).equals(""))
            txt_note.setText(PreferenceHandler.getNoteHead(mActivity).toUpperCase());
        else
            txt_note.setText(getString(R.string.lbl_note));

        if (PreferenceHandler.getNoteHead(mActivity) != null && !PreferenceHandler.getOrderDescHead(mActivity).equals(""))
            txt_dtl.setText(PreferenceHandler.getOrderDescHead(mActivity).toUpperCase());
        else
            txt_dtl.setText(getString(R.string.lbl_dtl));

        if (PreferenceHandler.getNoteHead(mActivity) != null && !PreferenceHandler.getPOHead(mActivity).equals(""))
            txt_po.setText(PreferenceHandler.getPOHead(mActivity).toUpperCase());
        else
            txt_po.setText(getString(R.string.lbl_po));

        if (PreferenceHandler.getNoteHead(mActivity) != null && !PreferenceHandler.getInvHead(mActivity).equals(""))
            txt_inv.setText(PreferenceHandler.getInvHead(mActivity).toUpperCase());
        else
            txt_inv.setText(getString(R.string.lbl_inv));


        implementedRulesOnFiled();

        edt_alt.setText(previousAlert);
        edt_note.setText(previousNote);
        edt_dtl.setText(prviouseModel);
        edt_po.setText(previouseReg);
        edt_inv.setText(
                previouseOdometer);
        edt_alt.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mActivity)));
        edt_note.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mActivity)));
        txt_alt.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mActivity)));
        txt_note.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mActivity)));

        if (SplashII.wrk_tid >= 7) {
            edt_alt.setEnabled(false);
            edt_note.setEnabled(false);
        }

        parentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                hideSoftKeyboard();
                return false;
            }
        });
        scrollAlertNote.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideSoftKeyboard();
                }
                return false;
            }
        });

    }

    private void implementedRulesOnFiled() {

        String rules = PreferenceHandler.getPrefFieldRules(mActivity);
        splitrules = rules.split(Pattern.quote("|"));
        String[] splitbyfield;
        String rulessprarater;
        rulessprarater = splitrules[0];
        splitbyfield = rulessprarater.split(Pattern.quote(","));

        if (splitbyfield[1].equalsIgnoreCase("1")) {
            edt_dtl.setVisibility(View.VISIBLE);
            txt_dtl.setVisibility(View.VISIBLE);
        } else {
            edt_dtl.setVisibility(View.GONE);
            txt_dtl.setVisibility(View.GONE);
        }

        if (splitbyfield[2].equalsIgnoreCase("1")) {
            edt_dtl.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_NUMBER);
        } else {
            edt_dtl.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
        }

        rulessprarater = splitrules[1];
        splitbyfield = rulessprarater.split(Pattern.quote(","));


        if (splitbyfield[1].equalsIgnoreCase("1")) {
            edt_po.setVisibility(View.VISIBLE);
            txt_po.setVisibility(View.VISIBLE);
        } else {
            edt_po.setVisibility(View.GONE);
            txt_po.setVisibility(View.GONE);
        }

        if (splitbyfield[2].equalsIgnoreCase("1")) {
            edt_po.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            edt_po.setInputType(InputType.TYPE_CLASS_TEXT);
        }

        rulessprarater = splitrules[2];
        splitbyfield = rulessprarater.split(Pattern.quote(","));


        if (splitbyfield[1].equalsIgnoreCase("1")) {
            edt_inv.setVisibility(View.VISIBLE);
            txt_inv.setVisibility(View.VISIBLE);
        } else {
            edt_inv.setVisibility(View.GONE);
            txt_inv.setVisibility(View.GONE);
        }

        if (splitbyfield[2].equalsIgnoreCase("1")) {
            edt_inv.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            edt_inv.setInputType(InputType.TYPE_CLASS_TEXT);
        }

        rulessprarater = splitrules[3];
        splitbyfield = rulessprarater.split(Pattern.quote(","));


        if (splitbyfield[1].equalsIgnoreCase("1")) {
            edt_alt.setVisibility(View.VISIBLE);
            txt_alt.setVisibility(View.VISIBLE);
        } else {
            edt_alt.setVisibility(View.GONE);
            txt_alt.setVisibility(View.GONE);
        }

        if (splitbyfield[2].equalsIgnoreCase("1")) {
            edt_alt.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_NUMBER);
        } else {
            edt_alt.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
        }


        rulessprarater = splitrules[4];
        splitbyfield = rulessprarater.split(Pattern.quote(","));


        if (splitbyfield[1].equalsIgnoreCase("1")) {
            edt_note.setVisibility(View.VISIBLE);
            txt_note.setVisibility(View.VISIBLE);
        } else {
            edt_note.setVisibility(View.GONE);
            txt_note.setVisibility(View.GONE);
        }

        if (splitbyfield[2].equalsIgnoreCase("1")) {
            edt_note.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_NUMBER);
        } else {
            edt_note.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
        }
    }


    @Override
    public void headerClickListener(String callingId) {
        if (callingId.equals(BaseTabActivity.HeaderDonePressed)) {

            String alert = edt_alt.getText().toString();
            String note = edt_note.getText().toString();
            String model = edt_dtl.getText().toString();
            String reg = edt_po.getText().toString();
            String inv = edt_inv.getText().toString();
            Long orderId = activeOdrObj.getId(); // MY selected Order Id

            String[] splitbyfieldValidation;
            String rulesspraraterValidation;

            rulesspraraterValidation = splitrules[0];
            splitbyfieldValidation = rulesspraraterValidation.split(Pattern.quote(","));

            if (splitbyfieldValidation[3].equalsIgnoreCase("1")) {
                if (model.equalsIgnoreCase("") || model.isEmpty()) {
                    showErrorDialog(txt_dtl.getText().toString() + " is required field");
                    return;
                }
            }

            rulesspraraterValidation = splitrules[1];
            splitbyfieldValidation = rulesspraraterValidation.split(Pattern.quote(","));

            if (splitbyfieldValidation[3].equalsIgnoreCase("1")) {
                if (reg.equalsIgnoreCase("") || reg.isEmpty()) {
                    showErrorDialog(txt_po.getText().toString() + " is required field");
                    return;
                }
            }

            rulesspraraterValidation = splitrules[2];
            splitbyfieldValidation = rulesspraraterValidation.split(Pattern.quote(","));

            if (splitbyfieldValidation[3].equalsIgnoreCase("1")) {
                if (inv.equalsIgnoreCase("") || inv.isEmpty()) {
                    showErrorDialog(txt_inv.getText().toString() + " is requir" +
                            "ed field");
                    return;
                }
            }

            rulesspraraterValidation = splitrules[3];
            splitbyfieldValidation = rulesspraraterValidation.split(Pattern.quote(","));

            if (splitbyfieldValidation[3].equalsIgnoreCase("1")) {
                if (alert.equalsIgnoreCase("") || alert.isEmpty()) {
                    showErrorDialog(txt_alt.getText().toString() + " is required field");
                    return;
                }
            }

            rulesspraraterValidation = splitrules[4];
            splitbyfieldValidation = rulesspraraterValidation.split(Pattern.quote(","));

            if (splitbyfieldValidation[3].equalsIgnoreCase("1")) {
                if (note.equalsIgnoreCase("") || note.isEmpty()) {
                    showErrorDialog(txt_note.getText().toString() + " is required field");
                    return;
                }
            }
//            if (alert.length()>150){
//                showErrorDialog("Alert should be less than 150 characters");
//                return;
//            }
//            if (note.length()>5000){
//                showErrorDialog("Note should be less than 5000 characters");
//                return;
//            }

            else if (!previousAlert.equals(alert) || !previousNote.equals(note) || !prviouseModel.equals(model) || !previouseReg.equals(reg) || !previouseOdometer.equals(inv)) {//YD doing this because may be nothing is change and user click on done button
                if (!previousAlert.equals(alert) || !prviouseModel.equals(model) || !previouseReg.equals(reg) || !previouseOdometer.equals(inv) || !previousNote.equals(note)) {
                    noOfReqSent++;


                    String key = "alt|PoVal|descript|inv|note";
                    String newValStr = alert + "|" + model + "|" + reg + "|" + inv + "|" + note;
                    String oldValStr = previousAlert + "|" + prviouseModel + "|" + previouseReg + "|" + previouseOdometer + "|" + previousNote;


                    boolean isDifferent = false;
                    String[] oldValStrSplit = oldValStr.split("\\|");
                    String[] newValStrSplit = newValStr.split("\\|");

                    try {
                        if (oldValStrSplit.length > 0 && newValStrSplit.length > 0) {
                            for (int i = 0; i < newValStrSplit.length - 1; i++) {
                                if (!(oldValStrSplit[i].equals(newValStrSplit[i]))) {
                                    isDifferent = true;
                                    break;
                                }
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                    if (isDifferent) {
                        updateOrderRequest req = new updateOrderRequest();
                        req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(getActivity()) + "/mobi");
                        req.setType("post");
                        req.setId(String.valueOf(orderId));
                        req.setName(key);
                        req.setValue(newValStr);
                        req.setAction(Order.ACTION_SAVE_ORDER_FLD);
                        Order.saveOrderField(req, mActivity, AlertnNoteFrag.this, SAVEORDERFIELD_STATUS_ALERT);// YD saving to data base

                    }
                }

                if (!previousNote.equals(note)) {

                    noOfReqSent++;
                    updateOrderRequest req = new updateOrderRequest();
                    req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(getActivity()) + "/mobi");
                    req.setType("post");
                    req.setId(String.valueOf(orderId));
                    req.setName("note");
                    req.setValue(note);
                    req.setAction(Order.ACTION_SAVE_ORDER_FLD);
                    OrderNotes.saveData(req, mActivity, AlertnNoteFrag.this, SAVENOTE);
                }

                mActivity.popFragments(BaseTabActivity.UI_Thread);

            } else
                mActivity.popFragments(BaseTabActivity.UI_Thread);
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
            e.printStackTrace();
        }
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
                if (response.getId() == SAVEORDERFIELD_STATUS_ALERT) {

                    noOfReqCompleted++;
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activeOdrObj.setOrderAlert(edt_alt.getText().toString());
                            activeOdrObj.setSummary(edt_dtl.getText().toString());
                            activeOdrObj.setPoNumber(edt_po.getText().toString());
                            activeOdrObj.setInvoiceNumber(edt_inv.getText().toString());
                            if (noOfReqCompleted == noOfReqSent)//YD doing this because two requests are being made on headerdonepress
                                mActivity.popFragments(BaseTabActivity.UI_Thread);
                        }
                    });
                }

                if (response.getId() == SAVENOTE) {
                    noOfReqCompleted++;
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap<Long, OrderNotes> notesMap = (HashMap<Long, OrderNotes>) DataObject.orderNoteDataStore;
                            if (notesMap != null && notesMap.size() > 0) {
                                OrderNotes noteObj = notesMap.get(activeOdrObj.getId());
                                if (noteObj != null) {
                                    noteObj.setOrdernote(edt_note.getText().toString());
                                    if (noOfReqCompleted == noOfReqSent)
                                        mActivity.popFragments(BaseTabActivity.UI_Thread);
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    public void hideSoftKeyboard() {
        if (mActivity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void setActiveOrderObject(Order order) {
        activeOdrObj = order;
    }
}
