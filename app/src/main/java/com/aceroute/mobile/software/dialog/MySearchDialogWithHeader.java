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

import com.aceroute.mobile.software.HeaderList;
import com.aceroute.mobile.software.ListItem;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.StatusList;
import com.aceroute.mobile.software.adaptor.StatusAlertAdapter;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.Utilities;

import java.util.ArrayList;

/**
 * Created by xelium on 7/24/16.
 */
public class MySearchDialogWithHeader extends Dialog implements View.OnClickListener , AdapterView.OnItemClickListener , TextWatcher {
    String popupTitle ,popuptyp;
    private Context context;
    private MySearchDiologHeaderInterface dClick = null;

    EditText edt_search_item;
    ListView itemList;
    Button  btnok;
    private int textlength;
    long elemtSelectedInSearchDlog;
    ArrayList<StatusList> mStatusNmArryList,finalmStatusNmArryList;
    public MySearchDialogWithHeader(Context context , String titleStr , String popuptype,ArrayList<StatusList> mStatusNmArryList) {
        super(context);

        this.context= context;
        this.popupTitle= titleStr;
        this.popuptyp = popuptype;
        this.mStatusNmArryList= new ArrayList<StatusList> ();
        this.finalmStatusNmArryList=new ArrayList<StatusList> ();
        this.mStatusNmArryList.addAll(mStatusNmArryList);
        this.finalmStatusNmArryList.addAll(mStatusNmArryList);


    }

    public MySearchDialogWithHeader(Context context , String titleStr , String popuptype,ArrayList<StatusList> mStatusNmArryList, long idSelected ) {
        super(context , R.style.commonDialogTheme);

        this.context= context;
        this.popupTitle= titleStr;
        this.popuptyp = popuptype;
        this.mStatusNmArryList= new ArrayList<StatusList> ();
        this.finalmStatusNmArryList=new ArrayList<StatusList> ();
        this.mStatusNmArryList.addAll(mStatusNmArryList);
        this.finalmStatusNmArryList.addAll(mStatusNmArryList);
        elemtSelectedInSearchDlog = idSelected;
    }

    public void setkeyListender(MySearchDiologHeaderInterface dClick) {
        this.dClick = dClick;
    }

    public void onCreate1(Bundle savedInstanceState) {//YD changed onCreate to onCreate1 to check if i was actually using oncreate overrided method of dialog
        super.onCreate(savedInstanceState);

        setContentView(R.layout.showsearchdialog);// same layout is used in MySeekBar class also .
        //setTitle(Html.fromHtml("<b><font color='#1abc9c'>" + popupTitle + "</font></b>"));
        setTitle(popupTitle);
                setCancelable(false);

        edt_search_item = (EditText)findViewById(R.id.search_editText_item);
        edt_search_item.setTextSize(20 + (PreferenceHandler.getCurrrentFontSzForApp(context)));
        edt_search_item.addTextChangedListener(this);
        itemList = (ListView)findViewById(R.id.search_item_list);
        itemList.setOnItemClickListener(this);
        itemList.setAdapter(new StatusAlertAdapter(context, mStatusNmArryList));

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
        if(tv!=null){
            tv.setTextColor(context.getResources().getColor(R.color.dlg_light_green));
            //tv.setTypeface(null, Typeface.BOLD);
            tv.setTextSize(24 + (PreferenceHandler.getCurrrentFontSzForApp(context)));
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.search_button_ok:
                try {
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
         //   Long idSelected = idsList.get(position);
            //String nameSelected = nmsList.get(position);

            dClick.onItemSelected(view);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
            textlength = edt_search_item.getText().length();//lets 2 be the length

        edt_search_item.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        mStatusNmArryList.clear();

        if(textlength>0) {
            HeaderList tempheader=null;
            for (int i = 0; i < finalmStatusNmArryList.size(); i++) {

                if(finalmStatusNmArryList.get(i) instanceof HeaderList){
                    tempheader= (HeaderList) finalmStatusNmArryList.get(i);
                }

                if (finalmStatusNmArryList.get(i) instanceof ListItem) {
                   /* if (textlength <= ((ListItem) finalmStatusNmArryList.get(i)).getDesc().length()) {
                        // ((ListItem) mStatusNmArryList.get(i)).ischecked = true;*/
                        if (((ListItem) finalmStatusNmArryList.get(i)).getDesc().toLowerCase().contains(edt_search_item.getText().toString().toLowerCase().trim())) {
                           if(tempheader!=null){
                               mStatusNmArryList.add(tempheader);
                               tempheader=null;
                           }
                            mStatusNmArryList.add(finalmStatusNmArryList.get(i));
                        }
                   // }
                }

            }
        }else{
            mStatusNmArryList.addAll(finalmStatusNmArryList);
        }
        itemList.setAdapter(new StatusAlertAdapter(context,mStatusNmArryList));

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
