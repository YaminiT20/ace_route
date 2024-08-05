package com.aceroute.mobile.software.adaptor;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.component.reference.OfflineCountry_City;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.utilities.PreferenceHandler;

import java.util.ArrayList;
import java.util.Locale;

public class OfflineCtryCtyAdpt extends BaseAdapter {
	
	Context mContext;
	LayoutInflater inflater;
	private ArrayList<OfflineCountry_City> offlineCtry_ctyList;
	
	private ArrayList<OfflineCountry_City> offlineCtry_ctyListChange;
	int flag;
	Typeface tf;
	
	public OfflineCtryCtyAdpt(Context context, ArrayList<OfflineCountry_City> offlineCtry_cty, int flag) {
		mContext = context;
		this.offlineCtry_ctyList= offlineCtry_cty;
		this.flag= flag;
		inflater = LayoutInflater.from(mContext);
		
		this.offlineCtry_ctyListChange = new ArrayList<OfflineCountry_City>();
		this.offlineCtry_ctyListChange.addAll(offlineCtry_ctyList);
		
		tf = TypeFaceFont.getCustomTypeface(context);
	}
	public class ViewHolder {
		TextView cnm,cid,typenmls;
		ImageView ctryCitySelection;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return offlineCtry_ctyList.size();
	}

	@Override
	public OfflineCountry_City getItem(int position) {
		// TODO Auto-generated method stub
		return offlineCtry_ctyList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.country_rowlist, null);
				// Locate the TextViews in listview_item.xml
				holder.cnm = (TextView) view.findViewById(R.id.textView_ctry_city_nm);
				holder.cnm.setTypeface(tf);
				holder.ctryCitySelection = (ImageView)view.findViewById(R.id.ctry_cty_selection);
				
				holder.cid = (TextView) view.findViewById(R.id.textView_cid_ctry);
				holder.typenmls = (TextView) view.findViewById(R.id.textView_custypnm_ctry);
				//holder.id = (TextView) view.findViewById(R.id.textView2);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			// Set the results into TextViews
			holder.cnm.setText(offlineCtry_ctyList.get(position).getName().toString());
		    holder.cnm.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));

			holder.cid.setText("");
			holder.cid.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));

			holder.typenmls.setText("");
			holder.typenmls.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
			if (offlineCtry_ctyList.get(position).isDownloaded())
 				holder.ctryCitySelection.setVisibility(View.VISIBLE);
			else
				holder.ctryCitySelection.setVisibility(View.GONE);
		
			return view;
	}
	
	
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		//nameList.clear();  check earlier not commented and working properly
		if (charText.length() == 0) {
			offlineCtry_ctyList.addAll(offlineCtry_ctyListChange);
		} 
		else 
		{
			offlineCtry_ctyList.clear();
			
			for (int i=0;i<offlineCtry_ctyListChange.size(); i ++)
			{
				String wrdMatch = offlineCtry_ctyListChange.get(i).getName();
				if (wrdMatch.toString().toLowerCase(Locale.getDefault()).contains(charText))
				{
					offlineCtry_ctyList.add(offlineCtry_ctyListChange.get(i));
				}
			}
			
			/*for (String wp : changeNmList) 
			{
				if (wp.toString().toLowerCase(Locale.getDefault()).contains(charText)) 
				{
					nameList.add(wp);
					idList.add(object)
				}
			}*/
		}
		notifyDataSetChanged();
	}
	
	

}
