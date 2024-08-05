package com.aceroute.mobile.software.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aceroute.mobile.software.R;

import java.util.ArrayList;


public class AudioListAdapter extends BaseAdapter {
	LayoutInflater mInflater;
	Context mContext;
	java.util.ArrayList<String> ArrayList = new ArrayList<String>();

	public AudioListAdapter(Context context, java.util.ArrayList<String> ArrayList) {

		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		this.ArrayList = ArrayList;
		// for demo only use according to your need

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ArrayList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	class ViewHolder {
		TextView filename,count;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		ViewHolder holder = null;

		if (convertView == null) {
			vi = mInflater.inflate(R.layout.row_material_audio, null);
			holder = new ViewHolder();
			holder.filename = (TextView) vi.findViewById(R.id.filename_txtvw);
			holder.count = (TextView)vi.findViewById(R.id.row_counter_txtvw);
			holder.count.setVisibility(View.GONE);
			// ((TextView)vi).findViewById(R.id.row_counter_txtvw).setVisibility(View.GONE);

			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		//holder.filename.setText(ArrayList.get(position).split(".3gp")[0]);
		holder.filename.setText(ArrayList.get(position).split(".m4a")[0]);
		return vi;
	}

}
