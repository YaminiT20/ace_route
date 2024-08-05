package com.aceroute.mobile.software.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.Utilities;

import java.util.ArrayList;

/**
 * Created by xelium on 7/24/16.
 */
public class MySearchDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener, TextWatcher {
    String popupTitle, popuptyp;
    private Context context;
    private MySearchDiologInterface dClick = null;
    private MySearchDiologInterfaceAlphaNum dClickAlphaNum = null;

    EditText edt_search_item;
    ListView itemList;
    Button btnok;
    ArrayList<Long> idsList, finalIdsLst;
    ArrayList<String> idsListAlphaNum, finalIdsLstAlphaNum;
    ArrayList<String> nmsList, finalNmsLst;
    private int textlength;
    long elemtSelectedInSearchDlog;
    String elemtSelectedInSearchDlogAlphaNum;
    boolean isAlphaNumeric = false;

    public MySearchDialog(Context context, String titleStr, String popuptype, ArrayList<Long> idsLst, ArrayList<String> nmsLst) {
        super(context);

        this.context = context;
        this.popupTitle = titleStr;
        this.popuptyp = popuptype;


        this.idsList = new ArrayList<Long>();
        this.nmsList = new ArrayList<String>();

        this.idsList.addAll(idsLst);
        this.nmsList.addAll(nmsLst);

        finalIdsLst = idsLst;
        finalNmsLst = nmsLst;
    }

    public MySearchDialog(Context context, String titleStr, String popuptype, ArrayList<Long> idsLst, ArrayList<String> nmsLst, long idSelected) {
        super(context, R.style.commonDialogTheme);

        this.context = context;
        this.popupTitle = titleStr;
        this.popuptyp = popuptype;


        this.idsList = new ArrayList<Long>();
        this.nmsList = new ArrayList<String>();

        try {
            this.idsList.addAll(idsLst);

            this.nmsList.addAll(nmsLst);

            finalIdsLst = idsLst;
            finalNmsLst = nmsLst;
            elemtSelectedInSearchDlog = idSelected;
        } catch (Exception e) {

        }

    }

    /**
     * YD creating for Alphanumeric ids
     *
     * @param context
     * @param titleStr
     * @param popuptype
     * @param idsLst
     * @param nmsLst
     * @param idSelected
     */
    public MySearchDialog(Context context, String titleStr, String popuptype, ArrayList<String> idsLst, ArrayList<String> nmsLst, String idSelected, boolean isAlphaNumeric) {
        super(context, R.style.commonDialogTheme);

        this.isAlphaNumeric = isAlphaNumeric;
        this.context = context;
        this.popupTitle = titleStr;
        this.popuptyp = popuptype;


        this.idsListAlphaNum = new ArrayList<String>();
        this.nmsList = new ArrayList<String>();

        this.idsListAlphaNum.addAll(idsLst);
        this.nmsList.addAll(nmsLst);

        finalIdsLstAlphaNum = idsLst;
        finalNmsLst = nmsLst;
        elemtSelectedInSearchDlogAlphaNum = idSelected;
    }

    public void setkeyListender(MySearchDiologInterface dClick) {
        this.dClick = dClick;
    }

    public void setkeyListenderAlphaNum(MySearchDiologInterfaceAlphaNum dClick) {
        this.dClickAlphaNum = dClick;
    }

    public void onCreate1(Bundle savedInstanceState) {//YD changed onCreate to onCreate1 to check if i was actually using oncreate overrided method of dialog
        super.onCreate(savedInstanceState);

        setContentView(R.layout.showsearchdialog);// same layout is used in MySeekBar class also .
        //setTitle(Html.fromHtml("<b><font color='#1abc9c'>" + popupTitle + "</font></b>"));
        setTitle(popupTitle);
        setCancelable(false);

        edt_search_item = (EditText) findViewById(R.id.search_editText_item);
        edt_search_item.setTextSize(20 + (PreferenceHandler.getCurrrentFontSzForApp(context)));
        edt_search_item.addTextChangedListener(this);
        itemList = (ListView) findViewById(R.id.search_item_list);
        itemList.setOnItemClickListener(this);

        if (isAlphaNumeric)
            itemList.setAdapter(new CustomSearchAdapter(context, idsListAlphaNum, nmsList, elemtSelectedInSearchDlogAlphaNum, isAlphaNumeric));
        else
            itemList.setAdapter(new CustomSearchAdapter(context, idsList, nmsList, elemtSelectedInSearchDlog));

        btnok = (Button) findViewById(R.id.search_button_ok);
        btnok.setOnClickListener(this);
        setCanceledOnTouchOutside(true);


        Utilities.setDefaultFont_12(btnok);
        // Utilities.setDividerTitleColor(this, 500, context);
        // Title divider
        final int titleDividerId = context.getResources().getIdentifier("titleDivider", "id", "android");
        final View titleDivider = findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(context.getResources().getColor(R.color.dlg_light_green));
        }

        //int textViewId = context.getResources().getIdentifier("alertTitle", null, null);
        TextView tv = (TextView) this.findViewById(android.R.id.title);
        if (tv != null) {
            tv.setTextColor(context.getResources().getColor(R.color.dlg_light_green));
            //tv.setTypeface(null, Typeface.BOLD);
            tv.setTextSize(24 + (PreferenceHandler.getCurrrentFontSzForApp(context)));
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.search_button_ok:
                try {
                    if (isAlphaNumeric) {
                        dClickAlphaNum.onButtonClick();
                    }
                    dClick.onButtonClick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (itemList == parent)// YD putting this because there may be more than one listview for some cases
        {
            String nameSelected = nmsList.get(position);

            if (isAlphaNumeric) {
                String idSelected = idsListAlphaNum.get(position);
                dClickAlphaNum.onItemSelected(idSelected, nameSelected);
            } else {
                Long idSelected = idsList.get(position);
                dClick.onItemSelected(idSelected, nameSelected);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        textlength = edt_search_item.getText().length();//lets 2 be the length

        try {
            edt_search_item.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            if (idsList != null) {
                idsList.clear();
            } else {
                this.idsList = new ArrayList<Long>();
            }
            if (nmsList != null) {
                nmsList.clear();
            } else {
                this.nmsList = new ArrayList<String>();
            }

            for (int i = 0; i < finalNmsLst.size(); i++) {
                if (textlength <= finalNmsLst.get(i).length()) {

                    if (finalNmsLst.get(i).toLowerCase().contains(edt_search_item.getText().toString().toLowerCase().trim())) {
                        idsList.add(finalIdsLst.get(i));
                        nmsList.add(finalNmsLst.get(i));
                    }
                }
            }
            itemList.setAdapter(new CustomSearchAdapter(context, idsList, nmsList, elemtSelectedInSearchDlog));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
