package com.aceroute.mobile.software.adaptor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.component.OrderPart;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.Parts;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.utilities.PreferenceHandler;

import java.util.HashMap;


public class MaterialAdapter extends BaseAdapter {
	LayoutInflater mInflater;
	Context mContext;
	 HashMap<Long, OrderPart> orderPartListMap;
	 Long[] keys;
	 HashMap<Long, Parts> partTypeList;

	public MaterialAdapter(Context context, Object orderPartListMap ) {

		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		this.orderPartListMap = (HashMap<Long, OrderPart>) orderPartListMap;
		keys = this.orderPartListMap.keySet().toArray(new Long[this.orderPartListMap.size()]);
		
		partTypeList = (HashMap<Long, Parts>) DataObject.partTypeXmlDataStore;
		//to do check for partypexmldatastore empty YD

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return orderPartListMap.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return orderPartListMap.get(keys[position]);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	class ViewHolder {
			TextView partName, barcodevalue,partQunatity,  partDtl, backview_del, backview_edit;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		ViewHolder holder;

		if (convertView == null) {
			vi = mInflater.inflate(R.layout.row_material, null);
			holder = new ViewHolder();
			holder.partName = (TextView)vi.findViewById(R.id.order_part_pname);
			holder.partQunatity = (TextView)vi.findViewById(R.id.order_part_pquantity);
			holder.barcodevalue = (TextView)vi.findViewById(R.id.order_part_barcode);
			Typeface tf = TypeFaceFont.getCustomTypeface(mContext);
			holder.partQunatity.setTypeface(tf,  Typeface.BOLD);
			holder.partDtl = (TextView)vi.findViewById(R.id.order_part_dtl);

			holder.backview_edit = (TextView) vi.findViewById(R.id.back_view_dummy);
			holder.backview_del = (TextView) vi.findViewById(R.id.back_view_dummy1);
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		String partNm = "";
		if(partTypeList!=null) {
			if (partTypeList.get(((OrderPart) getItem(position)).getPart_type_id()) != null)
				partNm = partTypeList.get(((OrderPart) getItem(position)).getPart_type_id()).getName();

			holder.partName.setText(partNm);
			holder.partName.setTag(((OrderPart) getItem(position)).getOrder_part_id());
			holder.partName.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));

			holder.partQunatity.setText(((OrderPart) getItem(position)).getOrder_part_QTY());
			holder.partQunatity.setTextColor(Color.parseColor("#E67E22"));
			holder.partQunatity.setTextSize(50 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));

			holder.barcodevalue.setText(((OrderPart) getItem(position)).getPart_barcode());
			//holder.partQunatity.setTextColor(Color.parseColor("#E67E22"));
			holder.barcodevalue.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
			if(getItem(position) !=null){
				if (partTypeList.get(((OrderPart) getItem(position)).getPart_type_id()).getDesc().equals("")) {
					holder.partDtl.setVisibility(View.GONE);
				} else {
					holder.partDtl.setText(partTypeList.get(((OrderPart) getItem(position)).getPart_type_id()).getDesc());
					holder.partDtl.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
				}
			}


			//YD setting this because showing red line between part list_cal when text is decreased
			holder.backview_edit.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
			holder.backview_del.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
		}
		return vi;
	}

	


}
