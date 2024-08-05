package com.aceroute.mobile.software.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.reference.CustomerContact;
import com.aceroute.mobile.software.component.reference.Site;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.EditContactReq;
import com.aceroute.mobile.software.requests.EditSiteReq;
import com.aceroute.mobile.software.requests.SaveSiteRequest;
import com.aceroute.mobile.software.utilities.ArrayAdapterwithRadiobutton;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.aceroute.mobile.software.validation.ValidationEngine;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by xelium on 4/15/16.
 */
public class EditContactFrag extends BaseFragment implements RespCBandServST, HeaderInterface
{

    private static final int UPDATE_CONTACT = 1;
    private static final int UPDATE_SITE=2;
    private int SAVESITE = 3;
    ValidationEngine validation;
    private TextView txt_phoneType;
    ArrayList<String> arrLstPhoneType;
    private EditText edt_name;
    private EditText edt_phone_siteAdr;
    private LinearLayout address2LL,parentContactedit,lay_email_edcontact;
    private long custId;
    private long custContactId;
    private Order activeOrderObj;
    private long siteID;
    String typeofPage = "";//YD for stie use SITE
    String email="";
    private EditText edt_site_description;
    private EditText edt_address2;
    private EditText edt_email;
    MyDialog dialog = null;
    boolean isclicked=false;

    private LinearLayout parentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_contact_f, null);
        TypeFaceFont.overrideFonts(mActivity, v);
        mActivity.registerHeader(this);
        initiViewReference(v);
        return v;
    }

    private void initiViewReference(View v)
    {
        TypeFaceFont.overrideFonts(mActivity, v);
        setTextViews(v);
        Bundle b  = this.getArguments();
        String header = b.getString("HeaderText");

        mActivity.setHeaderTitle("", header, "");
        mActivity.registerHeader(this);

        validation = ValidationEngine.getInstance(mActivity);
        if (header.equals("EDIT LOCATION") || header.equals("ADD LOCATION") ){
            validation.initValidation("EditContactFrag_Location");
        }else{
            validation.initValidation("EditContactFrag_Contact");
        }


        parentView = (LinearLayout) v.findViewById(R.id.parentview_edcont);
        lay_email_edcontact = (LinearLayout) v.findViewById(R.id.lay_email_edcontact);
        edt_name = (EditText) v.findViewById(R.id.edt_contact_name_edcontact);
        edt_email = (EditText) v.findViewById(R.id.edt_email_edcontact);
            Utilities.setMaxlengthET(edt_name , 50);
        edt_phone_siteAdr = (EditText) v.findViewById(R.id.edt_contact_phone_edcontact);
        address2LL = (LinearLayout) v.findViewById(R.id.cust_detail_adrr2_edcontact);
        parentContactedit = (LinearLayout) v.findViewById(R.id.parentContactedit);
        txt_phoneType = (TextView) v.findViewById(R.id.phone_type_edcontact);

        //YD setting up dynamic text
        edt_name.setTextSize(22 + PreferenceHandler.getCurrrentFontSzForApp(mActivity));
        edt_phone_siteAdr.setTextSize(22 + PreferenceHandler.getCurrrentFontSzForApp(mActivity));
        txt_phoneType.setTextSize(22 + PreferenceHandler.getCurrrentFontSzForApp(mActivity));

        edt_name.setMaxWidth(50);

        if (header.equals("EDIT LOCATION") || header.equals("ADD LOCATION") ){ //YD using for editing site/ location
            //YD getting data from bundle
            edt_email.setVisibility(View.GONE);
            lay_email_edcontact.setVisibility(View.GONE);
            typeofPage =  "SITE";
            custId = b.getLong("CUSTID");
            siteID = b.getLong("SITE_ID");
            String siteNm = b.getString("SITE_NM");
            String siteAdr = b.getString("SITE_ADR");
            String siteAdr2 = b.getString("SITE_ADR2");
            String siteDtl = b.getString("SITE_DETAIL");

            //YD setting up view according to site fields for text views
            txt_name.setText("NAME");
            txt_name_address.setText("ADDRESS");

            LinearLayout lytPhoneType = (LinearLayout) v.findViewById(R.id.lyt_phoneType_edcontact);
            lytPhoneType.setVisibility(View.GONE);
            LinearLayout lytSiteDescription = (LinearLayout) v.findViewById(R.id.lyt_site_description_edcontact);
            lytSiteDescription.setVisibility(View.VISIBLE);

            edt_site_description = (EditText) v.findViewById(R.id.edt_site_description_edcontact);
            edt_address2 = (EditText) v.findViewById(R.id.edt_contact_phone2_edcontact);
            edt_address2.setTextSize(22 + PreferenceHandler.getCurrrentFontSzForApp(mActivity));
            Utilities.setMaxlengthET(edt_address2, 50);

            edt_phone_siteAdr.setInputType(InputType.TYPE_CLASS_TEXT);
            Utilities.setMaxlengthET(edt_phone_siteAdr, 200);
            edt_address2.setInputType(InputType.TYPE_CLASS_TEXT);

            edt_name.setText(siteNm);

            edt_phone_siteAdr.setText(siteAdr);
            if (siteAdr2!=null)
                edt_address2.setText(siteAdr2);// YD using for address2 in custlist/site xml
            else
                edt_address2.setText("");// YD using for address2 in custlist/site xml
            edt_site_description.setText(siteDtl);

        }
        else {//YD using for Adding and editing customer contacts
            typeofPage = "CONTACT";
            address2LL.setVisibility(View.GONE);

            custContactId = b.getLong("CONTACT_ID");
            String custNm = b.getString("CONTACT_NM");
            String custTel = b.getString("TEL");
            String eml = b.getString("CONTACT_EMAIL");
            long contactType = b.getLong("CONTACT_TELTYPE");
            custId = b.getLong("CUSTID");

            arrLstPhoneType = new ArrayList<String>();
            for (int i = 0; i < CustomerDetailFragment.arrPhoneType.length; i++) {
                arrLstPhoneType.add(CustomerDetailFragment.arrPhoneType[i]);
            }

            //contact type
            txt_phoneType = (TextView) v.findViewById(R.id.phone_type_edcontact);
            if (contactType!=0) {
                txt_phoneType.setText(arrLstPhoneType.get((int) contactType));
                txt_phoneType.setTag(contactType);
            }
            else {//YD setting first as default
                txt_phoneType.setText(arrLstPhoneType.get(0));
                txt_phoneType.setTag(0);
            }
            txt_phoneType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    showCustomDialog();
                }
            });

            edt_name.setText(custNm);
            edt_email.setText(eml);
            edt_phone_siteAdr.setText(custTel);
            Utilities.setMaxlengthET(edt_email, 100);
            Utilities.setMaxlengthET(edt_phone_siteAdr, 50);
        }

        parentContactedit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                hideSoftKeyboard();
                return false;
            }
        });
    }

    TextView txt_name ,txt_name_address ,txt_name_address2 , phoneType;
    private void setTextViews(View v) {

       txt_name = (TextView) v.findViewById(R.id.txt_name_edcontact);
       txt_name_address = (TextView) v.findViewById(R.id.txt_name_address_edcontact);
       txt_name_address2 = (TextView) v.findViewById(R.id.txt_name_address2_edcontact );
       phoneType = (TextView) v.findViewById(R.id.add_edit_phType_txtvw);

        int size = PreferenceHandler.getCurrrentFontSzForApp(mActivity);
        txt_name.setTextSize(22 + size);
        txt_name_address.setTextSize(22 + size);
        txt_name_address2.setTextSize(22 + size);
        phoneType.setTextSize(22 + size);
    }

    public void hideSoftKeyboard() {
        if(mActivity.getCurrentFocus()!=null){
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
        }
    }
    // Show Phone Type Dialog
    private void showCustomDialog(){
        try{
            ArrayAdapter<String> adapter = new ArrayAdapterwithRadiobutton(mActivity, android.R.layout.select_dialog_item, arrLstPhoneType,txt_phoneType.getText().toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT);
            builder.setTitle("Preferred Comm. Method");
            builder.setCancelable(true);

            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @SuppressLint("NewApi")
                public void onClick(DialogInterface dialog, int position) {
                    txt_phoneType.setText(arrLstPhoneType.get(position));
                    txt_phoneType.setTag(position);
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

            Utilities.setDividerTitleColor(dialog, 500, mActivity);

            Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            Utilities.setDefaultFont_12(button_negative);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    String contactName , contactTypeId , contactPhone;//YD keeping it global because need to update active order object
    @Override
    public void headerClickListener(String callingId) {
        if (callingId.equals(BaseTabActivity.HeaderDonePressed)){

            if(isclicked){
               return;
            }
            String Errormsg=null;
            Errormsg = validation.checkValidation(this , mActivity);
            if(Errormsg.equals("")) {
                isclicked=true;
                if (typeofPage.equals("CONTACT")) {
                    contactName = edt_name.getText().toString();
                    if (contactName.equals(""))
                        contactName = "Untitled";

                    contactPhone = edt_phone_siteAdr.getText().toString();

                    contactTypeId = txt_phoneType.getTag().toString();
                    String contactTypeNm = txt_phoneType.getText().toString();

                    EditContactReq req = new EditContactReq();
                    req.setAction(CustomerContact.ACTION_CONTACT_EDIT);
                    req.setName(contactName);
                    req.setTell(String.valueOf(contactPhone));
                    req.setId(custContactId);
                    req.setCid(custId);
                    req.setEmail(edt_email.getText().toString());

                    req.setTid(Integer.valueOf(txt_phoneType.getTag().toString()));
                    Site.getData(req, mActivity, EditContactFrag.this, UPDATE_CONTACT);
                } else if (typeofPage.equals("SITE")) {
                    String site_name = edt_name.getText().toString();
                    if (site_name.trim().equals(""))
                        site_name = "Untitled";

                    String site_description = edt_site_description.getText().toString();
                    String site_addr = edt_phone_siteAdr.getText().toString();
                    if (site_addr.equals(""))
                        site_addr = "United States";

                    String site_addr2 = edt_address2.getText().toString();

                    if (siteID != 0) {
                        EditSiteReq req = new EditSiteReq();
                        req.setAction(Site.ACTION_EDIT_SITE);
                        req.setName(site_name);
                        req.setAdr(site_addr);
                        req.setAdr2(site_addr2);
                        req.setDesc(site_description);
                        req.setId(siteID);
                        Site.getData(req, mActivity, EditContactFrag.this, UPDATE_SITE);

                    } else {
                        String tistamp = String.valueOf(Utilities.getCurrentTime());

                        SaveSiteRequest req = new SaveSiteRequest();
                        req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
                        req.setAction(Site.ACTION_SAVE_SITE);
                        req.setAdr(site_addr);
                        req.setNm(site_name);
                        req.setCid(String.valueOf(custId));
                        req.setTstamp(tistamp);
                        req.setId("0");
                        req.setGeo("0,0");
                        req.setDtl(edt_site_description.getText().toString());
                        req.setTid("");
                        req.setLtpnm("");

                        Site.saveData(req, mActivity, EditContactFrag.this, SAVESITE);
                    }
                }
            }else{
                String D_title = getResources().getString(R.string.msg_slight_problem);
                String D_desc = Errormsg;
                dialog = new MyDialog(mActivity, D_title, D_desc,"OK");
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

                Utilities.setDividerTitleColor(dialog, 0, mActivity);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;
                dialog.getWindow().setAttributes(lp);
            }
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
        if (response!=null) {
            isclicked=false;
            if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED))) {
                if (response.getId()== UPDATE_CONTACT) {
                    CustomerDetailFragment.SYNC_CONTACT = true;
                    if (custContactId!=0 && activeOrderObj.getContactId() ==  custContactId){//YD checking if the contact changed is the contact kept in order object i.e. default contact of customer
                        updateOdrContact();
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.popFragments(BaseTabActivity.UI_Thread);
                        }
                    });
                }
                if (response.getId()== UPDATE_SITE || response.getId()==SAVESITE)
                {
                    CustomerDetailFragment.SYNC_SITE = true;
                    PreferenceHandler.setAppDataChanged(mActivity, true);//YD to make sure that the orderlist get refresh
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.popFragments(BaseTabActivity.UI_Thread);
                        }
                    });
                }
            }
        }
    }

    private void updateOdrContact() {
        if (activeOrderObj!= null) {
            activeOrderObj.setCustContactName(contactName);
            activeOrderObj.setCustContactNumber(contactPhone);
            activeOrderObj.setEml(edt_email.getText().toString());
            if (contactTypeId != null && !contactTypeId.equals(""))
                activeOrderObj.setTelTypeId(Long.valueOf(contactTypeId));
        }
    }

    public void setActiveOrderObj(Order activeOrderObj) {

        this.activeOrderObj = activeOrderObj;
		/*HashMap<Long, Order> order = (HashMap<Long, Order>)DataObject.ordersXmlDataStore;
		DataObject.ordersXmlDataStore= null;*/
    }
}
