package com.aceroute.mobile.software;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.aceroute.mobile.software.adaptor.StatusAlertAdapter.RowType;
import com.aceroute.mobile.software.utilities.PreferenceHandler;

public class ListItem implements StatusList{	
	private String id;

	public String getDesc() {
		return desc;
	}

	private String desc;
	Context context;
	boolean showrbtn;
	public boolean ischecked=false;
		
    public ListItem(String Id, String Desc, Context contx, boolean showrbtn,boolean ischecked)
    {         
        this.id = Id;
        this.desc = Desc;
		context = contx;
		this.showrbtn=showrbtn;
		this.ischecked=ischecked;
    }
            	
	@Override
	public int getViewType() 
	{
		return RowType.LIST_ITEM.ordinal();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(LayoutInflater inflater, View convertView)
	{
		convertView = null;	    
		View view = (View) inflater.inflate(R.layout.row_items, null);
	    TextView txtStatus = (TextView) view.findViewById(R.id.txtTitle);
		if(showrbtn){
			if(ischecked) {
				txtStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apptheme_btn_radio_on_holo_light, 0);
			}else{
				txtStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apptheme_btn_radio_off_holo_light, 0);
			}
		}
	   
	    txtStatus.setTag(id);
	    txtStatus.setText(Html.fromHtml(desc));
		txtStatus.setTextSize(22 + PreferenceHandler.getCurrrentFontSzForApp(context));
	    
	    return view;
	}
}
