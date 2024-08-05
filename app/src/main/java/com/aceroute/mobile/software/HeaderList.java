package com.aceroute.mobile.software;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.aceroute.mobile.software.utilities.PreferenceHandler;

public class HeaderList implements StatusList {
	private final String name;
	Context context;
	
	public HeaderList(String _name, Context contx)
	{
		this.name = _name;
		context = contx;
	}
	
	public int getViewType() 
	{		
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(LayoutInflater inflater, View convertView)
	{	
		convertView=null;       
        View view = (View) inflater.inflate(R.layout.header, null);

        TextView text = (TextView) view.findViewById(R.id.separator);
        text.setText(Html.fromHtml(name).toString());
		text.setTextSize(22 + PreferenceHandler.getCurrrentFontSzForApp(context));
                
        return view;
	}
}
