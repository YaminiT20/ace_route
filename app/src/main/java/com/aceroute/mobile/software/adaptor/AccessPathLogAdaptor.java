package com.aceroute.mobile.software.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aceroute.mobile.software.R;

import java.util.ArrayList;

public class AccessPathLogAdaptor extends BaseAdapter {
	
	LayoutInflater mInflater;
	ArrayList<String> geoCodesArrLst;
	
	
	public AccessPathLogAdaptor(Context mContext,  ArrayList<String> geoCode) {
		mInflater = LayoutInflater.from(mContext);
		this.geoCodesArrLst = geoCode;
	}

	@Override
	public int getCount() {
		return geoCodesArrLst.size();
	}

	@Override
	public String getItem(int position) {
		return geoCodesArrLst.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	class ViewHolder{
		TextView geocode_txt;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		ViewHolder holder;

		if (convertView == null) {
		  vi = mInflater.inflate(R.layout.accesspath_logwaypoints, null);
		  holder = new ViewHolder();
		  holder.geocode_txt = (TextView)vi.findViewById(R.id.geocode_txtv);
		  vi.setTag(holder);
		}
		else
			holder = (ViewHolder) vi.getTag();

		holder.geocode_txt.setText(geoCodesArrLst.get(position));
		
		return vi;
	}

}
