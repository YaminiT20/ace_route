package com.aceroute.mobile.software.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aceroute.mobile.software.StatusList;

import java.util.List;

public class StatusAlertAdapter extends ArrayAdapter<StatusList> {
	
	Context ctx=null;
    private LayoutInflater mInflater=null;
   
    public StatusAlertAdapter(Context context, List<StatusList> list) {
    	super(context, 0, list);
		
		this.ctx = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	} 
        
    public enum RowType //YD using for getting count of number of elements in the list_cal check please
	{		
        LIST_ITEM, HEADER_ITEM
    }
	
	@Override
    public int getViewTypeCount()
	{
        return RowType.values().length;
    }

    @Override
    public int getItemViewType(int position) 
    {
    	return getItem(position).getViewType();      
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return getItem(position).getView(mInflater, convertView);
    }
}


