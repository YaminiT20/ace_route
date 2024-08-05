package com.aceroute.mobile.software.customersite;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.utilities.PreferenceHandler;

import java.util.ArrayList;
import java.util.Locale;

public class Viewholder extends BaseAdapter {
	
	Context mContext;
	LayoutInflater inflater;
	private ArrayList<String> nameList = null;
	private ArrayList<Long> idList;
	private ArrayList<String> typeNmLst = null;
	
	private ArrayList<String> changeNmList;
	private ArrayList<Long> changeIdList;
	private ArrayList<String> changetypeNmLst;
	int flag;
	Typeface tf;
	
	public Viewholder(Context context, ArrayList<String> name ,ArrayList<Long> id ,ArrayList<String> custTypeName, int flag) {
		mContext = context;
		this.idList= id;
		this.nameList = name;
		this.typeNmLst = custTypeName;
		this.flag= flag;
		inflater = LayoutInflater.from(mContext);
		
		this.changeNmList = new ArrayList<String>();
		this.changetypeNmLst = new ArrayList<String>();
		this.changeIdList = new ArrayList<Long>();
		
		this.changeNmList.addAll(nameList);
		this.changeIdList.addAll(idList);
		this.changetypeNmLst.addAll(typeNmLst);
		tf = TypeFaceFont.getCustomTypeface(context);
	}
	public class ViewHolder {
		TextView cnm,cid,typenmls;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return idList.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return idList.get(position)+"##"+nameList.get(position)+"##"+typeNmLst.get(position);
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
				view = inflater.inflate(R.layout.rowlist, null);
				// Locate the TextViews in listview_item.xml
				holder.cnm = (TextView) view.findViewById(R.id.textView_custname);
				holder.cnm.setTypeface(tf);
				holder.cnm.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));

				holder.cid = (TextView) view.findViewById(R.id.textView_cid);
				holder.typenmls = (TextView) view.findViewById(R.id.textView_custypnm);
				//holder.id = (TextView) view.findViewById(R.id.textView2);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			// Set the results into TextViews
			holder.cnm.setText(nameList.get(position).toString());
			holder.cid.setText(idList.get(position).toString());
			holder.typenmls.setText(typeNmLst.get(position).toString());
		//	holder.id.setText(Searchlist.get(position).gettext());
	         
			/*view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					if (flag==0){
						String id = idList.get(position).toString();
						String nm = nameList.get(position).toString();
						Intent i= new Intent(mContext, Sitelist.class);
						i.putExtra("CID", id);
						i.putExtra("CNM", nm);
						mContext.startActivity(i);
					}
					else if (flag==1)
					{
						
					}
				}
			});*/
			
			
			// listener
		return view;
	}
	
	
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		//nameList.clear();  check earlier not commented and working properly
		if (charText.length() == 0) {
			nameList.addAll(changeNmList);
			idList.addAll(changeIdList);
			typeNmLst.addAll(changetypeNmLst);
		} 
		else 
		{
			nameList.clear();
			idList.clear();
			typeNmLst.clear();
			
			
			for (int i=0;i<changeNmList.size(); i ++)
			{
				String wrdMatch = changeNmList.get(i);
				if (wrdMatch.toString().toLowerCase(Locale.getDefault()).contains(charText))
				{
					nameList.add(wrdMatch);
					idList.add(changeIdList.get(i));
					typeNmLst.add(changetypeNmLst.get(i));
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
