package com.aceroute.mobile.software.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.Utilities;

import org.json.JSONException;


public class MyDialog extends AlertDialog implements View.OnClickListener {

	public Button yes, no;
	String popupTitle, content ,popuptyp;
	private MyDiologInterface dClick = null;
	TextView heading , contentstr;
	private Context context;
	
	public MyDialog(Context context,String titleStr,String contenTxt, String popuptype) {
		super(context, R.style.commonDialogTheme);

		this.context= context;
		this.popupTitle= titleStr;
		this.content = contenTxt;
		this.popuptyp = popuptype;
	}
	
	public void setkeyListender(MyDiologInterface dClick) {
		this.dClick = dClick;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		//super.onCreate(savedInstanceState);

		setContentView(R.layout.showdialog);// same layout is used in MySeekBar class also .
		//int fsize= (int) ((24 + (PreferenceHandler.getCurrrentFontSzForApp(context)))/context.getResources().getDisplayMetrics().scaledDensity);
		setTitle(Html.fromHtml("<b><font color='#1abc9c'>" + popupTitle + "</font></b>"));
	    setCancelable(false);

	    contentstr = (TextView) findViewById(R.id.text_mesg_popup);
		contentstr.setTextColor(getContext().getResources().getColor(R.color.alertcolor));
	    contentstr.setTextSize((PreferenceHandler.getCurrrentFontSzForApp(context))+22);

	  	contentstr.setText(content);
		TypeFaceFont.overrideFonts(context,contentstr);
	    
	    yes = (Button) findViewById(R.id.butt_yes);
	 	no = (Button) findViewById(R.id.butt_no_ok);

		TypeFaceFont.overrideFonts(context,yes);
		TypeFaceFont.overrideFonts(context,no);

	    Utilities.setDefaultFont_12(yes);
	    Utilities.setDefaultFont_12(no);
	   
      // Title divider
        final int titleDividerId = context.getResources().getIdentifier("titleDivider", "id", "android");
        final View titleDivider = findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(context.getResources().getColor(R.color.dlg_light_green));
        }

		int textViewId = context.getResources().getIdentifier("android:id/alertTitle",  null,null);
		TextView tv = (TextView) findViewById(textViewId);
		if(tv==null){
			tv = (TextView)findViewById(android.R.id.title);
		}
		if(tv!=null){
			tv.setTextColor(context.getResources().getColor(R.color.dlg_light_green));
			tv.setTypeface(null, Typeface.BOLD);
			tv.setTextSize(24 + (PreferenceHandler.getCurrrentFontSzForApp(context)));
			TypeFaceFont.overrideFonts(context,tv);
			tv.setShadowLayer(0.1f,0.1f,0.1f,Color.parseColor("#00ffffff"));
		}
	    
	    yes.setOnClickListener(this);
	    no.setOnClickListener(this);
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
		//Utilities.setDividerTitleColor(this, 0 ,  getContext());
		//super.onCreate(savedInstanceState);
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
}
