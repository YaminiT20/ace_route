package com.aceroute.mobile.software.dialog;

/**
 * Created by xelium on 6/24/16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.utilities.PreferenceHandler;

import java.util.ArrayList;

public class CustomSearchAdapter extends BaseAdapter{

    Context ctx=null;
    ArrayList<String> listarray=null;
    ArrayList<Long> idlist;
    ArrayList<String> idsListAlphaNum;
    private LayoutInflater mInflater=null;
    long idSelected;
    String idSelectedAlphaNum;
    boolean isAlphaNumeric;

    public CustomSearchAdapter(Context activty, ArrayList<String> list)
    {
        this.ctx=activty;
        mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       // mInflater = activty.getLayoutInflater();
        this.listarray=list;
    }

    public CustomSearchAdapter(Context activty, ArrayList<Long> idlist , ArrayList<String> list, long idSelected)
    {
        this.ctx=activty;
        mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // mInflater = activty.getLayoutInflater();
        this.listarray=list;
        this.idlist = idlist;
        this.idSelected = idSelected;
    }

    public CustomSearchAdapter(Context activty, ArrayList<String> idlist , ArrayList<String> list, String idSelected, boolean isAlphaNumeric)
    {
        this.isAlphaNumeric = isAlphaNumeric;
        this.ctx=activty;
        mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // mInflater = activty.getLayoutInflater();
        this.listarray=list;
        this.idsListAlphaNum = idlist;
        this.idSelectedAlphaNum = idSelected;
    }

    @Override
    public int getCount() {

        return listarray.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        final ViewHolder holder;
        if (convertView == null ) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.alertlistrow, null);

            holder.titlename = (TextView) convertView.findViewById(R.id.textView_search_titllename);
            holder.titlename.setTextSize(24 + (PreferenceHandler.getCurrrentFontSzForApp(ctx)));
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        String datavalue=listarray.get(position);
        holder.titlename.setText(datavalue);
        if(isAlphaNumeric){
            if (idsListAlphaNum != null) {
                if (idsListAlphaNum.get(position) .equals(idSelectedAlphaNum))//YD check which option have the select icon for radio button
                    holder.titlename.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apptheme_btn_radio_on_holo_light, 0);
                else
                    holder.titlename.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apptheme_btn_radio_off_holo_light, 0);
            }
        }
        else {
            if (idlist != null) {
                if (idlist.get(position) == idSelected)//YD check which option have the select icon for radio button
                    holder.titlename.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apptheme_btn_radio_on_holo_light, 0);
                else
                    holder.titlename.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apptheme_btn_radio_off_holo_light, 0);
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView titlename;
    }
}