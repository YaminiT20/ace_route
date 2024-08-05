package com.aceroute.mobile.software.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.utilities.Utilities;

import org.json.JSONException;

/**
 * Created by yash on 4/2/16.
 */
public class MySeekBar extends Dialog implements View.OnClickListener, SeekBar.OnSeekBarChangeListener  {

    public Button yes, no;
    TextView heading , contentstr;
    String popupTitle, content ,popuptyp;
    private SeekBar auto_sync_opt_time_interval;
    private Context context;
    private MySeekDialogInterface dClick = null;
    private LinearLayout ll_min_lst;
    int progress;

    public MySeekBar(Context context,String titleStr,String contenTxt, String popuptype , int progress) {
        super(context);

        this.context= context;
        this.popupTitle= titleStr;
        this.content = contenTxt;
        this.popuptyp = popuptype;
        this.progress = progress;
    }

    public void setkeyListender(MySeekDialogInterface dClick) {
        this.dClick = dClick;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.showdialog); // same layout is used in Mydialog class also .
        setTitle(Html.fromHtml("<b><font color='#1abc9c'>" + popupTitle + "</font></b>"));
        setCancelable(false);

        contentstr = (TextView) findViewById(R.id.text_mesg_popup);
        contentstr.setTextColor(Color.parseColor("#4c4c4e"));
        contentstr.setTextSize(22);

        contentstr.setText(content);

        yes = (Button) findViewById(R.id.butt_yes);
        no = (Button) findViewById(R.id.butt_no_ok);
        auto_sync_opt_time_interval = (SeekBar) findViewById(R.id.auto_sync_opt_time_interval);
        ll_min_lst = (LinearLayout) findViewById(R.id.ll_min_lst);

        auto_sync_opt_time_interval.setProgress(progress);
        Utilities.setDefaultFont_12(yes);
        Utilities.setDefaultFont_12(no);

        // Title divider
        final int titleDividerId = context.getResources().getIdentifier("titleDivider", "id", "android");
        final View titleDivider = findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(context.getResources().getColor(R.color.dlg_light_green));
        }

        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        auto_sync_opt_time_interval.setOnSeekBarChangeListener(this);

        if (popuptyp.equals("DELETE"))
        {
            yes.setVisibility(View.VISIBLE);
            no.setVisibility(View.VISIBLE);
        }
        else if(popuptyp.equals("OK"))
        {
            no.setVisibility(View.VISIBLE);
            no.setText("OK");
        }else if(popuptyp.equals("YES")){
            yes.setVisibility(View.VISIBLE);
            no.setVisibility(View.VISIBLE);
            yes.setText("No");
            no.setText("Yes");
        }
        else if(popuptyp.equals("SYNC_SEEK")){
            yes.setVisibility(View.VISIBLE);
            no.setVisibility(View.VISIBLE);
            contentstr.setVisibility(View.GONE);
            ll_min_lst.setVisibility(View.VISIBLE);
            auto_sync_opt_time_interval.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.butt_yes:
                try {
                    dClick.onPositiveClick();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.butt_no_ok:
                dClick.onNegativeClick();
                break;
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        dClick.onProgressChanged(seekBar, progress, fromUser);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        dClick.onStartTrackingTouch(seekBar);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        dClick.onStopTrackingTouch(seekBar);
    }
}
