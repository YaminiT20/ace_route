package com.aceroute.mobile.software.adaptor;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.component.reference.Assets;
import com.aceroute.mobile.software.component.reference.AssetsType;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.Parts;
import com.aceroute.mobile.software.component.reference.SiteType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class AssetAdapter extends BaseAdapter {
	LayoutInflater mInflater;
	Context mContext;
	 HashMap<Long, Assets> orderAssetsListMap;
	 Long[] keys;
	 HashMap<Long, AssetsType> AssetsTypeList;
	HashMap<Long, SiteType> siteAndGenTypeList;

	public AssetAdapter(Context context, Object orderAssetListMap, HashMap<Long, SiteType> siteAndGenTypeList ) {

		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		this.orderAssetsListMap = (HashMap<Long, Assets>) orderAssetListMap;
		keys = this.orderAssetsListMap.keySet().toArray(new Long[this.orderAssetsListMap.size()]);
		this.siteAndGenTypeList = siteAndGenTypeList;
		AssetsTypeList = (HashMap<Long, AssetsType>) DataObject.assetsTypeXmlDataStore;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return orderAssetsListMap.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return orderAssetsListMap.get(keys[position]);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	class ViewHolder {
			TextView HeaderText;
			RecyclerView verticalLayoutTxtView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		ViewHolder holder;

        if(convertView == null){
            vi = mInflater.inflate(R.layout.row_assets, null);
            holder = new ViewHolder();

            holder.HeaderText = (TextView)vi.findViewById(R.id.order_part_pname_asset);
            holder.verticalLayoutTxtView = (RecyclerView)vi.findViewById(R.id.vertical_layout_txt_view_assets);
            holder.verticalLayoutTxtView.setLayoutManager(new LinearLayoutManager(mContext));
            vi.setTag(holder);
        }else
            holder = (ViewHolder) vi.getTag();

        try {
            if(siteAndGenTypeList.get(Long.valueOf(((Assets)getItem(position)).getFtid()))!= null)
                holder.HeaderText.setText(siteAndGenTypeList.get(Long.valueOf(((Assets)getItem(position)).getFtid())).getNm());
            JSONObject formData = new JSONObject (((Assets)getItem(position)).getFdata());
            JSONArray formViewsList = formData.getJSONArray("frm");

            final ArrayList<String> valueToShow =  new ArrayList<String>();
            final ArrayList<String> fontList =  new ArrayList<String>();

            for(int i=0; i<formViewsList.length(); i++) {
                JSONObject singleView = (JSONObject) formViewsList.get(i);

                if (singleView.getString("lst").equals("1") || i==0 || i==1 || i==2) {
                    String viewTid = singleView.getString("tid");
                    String valToSetInAdaptor = getvalueByTid(singleView, viewTid);

                    if(!valToSetInAdaptor.equals("")){
                        fontList.add(singleView.getString("fnt"));
                        valueToShow.add(valToSetInAdaptor);//YD fontList will get fill in this method and will be used here
                    }
                }
            }
            FormListElementAdp frmListEleAdp = new FormListElementAdp(mContext, valueToShow, fontList);

                /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, valueToShow){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);

                        //String fontRequiredOnTextView = fontList.get(position);

                        TextView textView = (TextView)view.findViewById(android.R.id.text1);
                        textView.setTextSize(18 + PreferenceHandler.getCurrrentFontSzForApp(mContext));
                        textView.setTextColor(mContext.getResources().getColor(R.color.light_gray));

                        return view;
                    }
                };*/
            holder.verticalLayoutTxtView.setAdapter(frmListEleAdp);
            //holder.verticalLayoutTxtView.setDivider(null);
            holder.verticalLayoutTxtView.setClickable(false);
            holder.verticalLayoutTxtView.setScrollContainer(false);
            holder.verticalLayoutTxtView.setFocusable(false);
            holder.verticalLayoutTxtView.setVerticalScrollBarEnabled(false);


        } catch (JSONException e) {
            e.printStackTrace();
        }

		/*if (convertView == null) {
			vi = mInflater.inflate(R.layout.row_assets, null);
			holder = new ViewHolder();
			holder.assetName = (TextView)vi.findViewById(R.id.order_part_pname_asset);
			holder.assetNote2 = (TextView)vi.findViewById(R.id.order_note_asset);
			holder.assetStat = (TextView)vi.findViewById(R.id.order_note_asset_thirdText);

			holder.assetQunatity = (TextView)vi.findViewById(R.id.order_part_pquantity_asset);
			Typeface tf = TypeFaceFont.getCustomTypeface(mContext);
			holder.assetQunatity.setTypeface(tf ,  Typeface.BOLD);

			holder.backview_edit = (TextView) vi.findViewById(R.id.back_asset_dummy);
			holder.backview_edit_bottom = (TextView) vi.findViewById(R.id.back_asset_dummy_bottom);
			holder.backview_del = (TextView) vi.findViewById(R.id.back_asset_dummy1);
			holder.backview_del_bottom = (TextView) vi.findViewById(R.id.back_asset_dummy1_bottom);

			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		String assetNm = "";
		*//*if (AssetsTypeList.get(((Assets)getItem(position)).getTid())!=null)
			assetNm = AssetsTypeList.get(((Assets)getItem(position)).getTid()).getName();*//*//YD 2020

		holder.assetName.setText(assetNm);
		holder.assetName.setTag(((Assets) getItem(position)).getId());
		holder.assetName.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));

		//holder.assetNote2.setText(((Assets) getItem(position)).getNote2());//YD 2020
		holder.assetNote2.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));

		if (siteAndGenTypeList!= null*//* && ((Assets) getItem(position)).getStatus()!=null
			&&	!((Assets) getItem(position)).getStatus().equals("")
			&& siteAndGenTypeList.get(Long.valueOf(((Assets) getItem(position)).getStatus()))!= null*//*) {//YD 2020
			//holder.assetStat.setText(siteAndGenTypeList.get(Long.valueOf(((Assets) getItem(position)).getStatus())).getNm());
			holder.assetStat.setVisibility(View.VISIBLE);
			holder.assetStat.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
		}else {
			holder.assetStat.setText("");
			holder.assetStat.setVisibility(View.INVISIBLE);
			holder.assetStat.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
		}
		//holder.assetStat.setTypeface(null, Typeface.BOLD);

		//holder.assetQunatity.setText(String.valueOf(((Assets) getItem(position)).getContact1()));
		holder.assetQunatity.setTextColor(Color.parseColor("#7ABA3A"));
		holder.assetQunatity.setTextSize(50 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));

		holder.backview_edit.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
		holder.backview_edit_bottom.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
		holder.backview_del.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
		holder.backview_del_bottom.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));*/

		return vi;
	}

    private String getvalueByTid(JSONObject singleView, String viewTid) {
        int fieldTypeId = Integer.valueOf(viewTid);

        try {
            switch (fieldTypeId) {
                case 1:
                    return singleView.getString("val");


                case 2:
                    return singleView.getString("val");


                case 3:
                    return singleView.getString("val");


                case 4:
                    //createTextDateTime(jo_inside, params, true, i);
                    break;

                case 5:
                    // createTextDateTime(jo_inside, params, false, i);
                    break;

                case 6:
                    String selectedValueOfRadio = singleView.getString("val");
                    String[] optionValArr = singleView.getString("ddnval").split("\\,");
                    String[] optionStrArr = singleView.getString("ddn").split("\\,");

                    for(int i=0; i<optionValArr.length; i++){
                        if(optionValArr[i].equals(selectedValueOfRadio)){
                            return optionStrArr[i];
                        }
                    }
                    break;

                case 7:
                    String[] selectedValueOfCheckbox = !singleView.getString("val").equals("") ? singleView.getString("val").split("\\,") : null;
                    String[] optionValArrOfCheckbox = singleView.getString("ddnval").split("\\,");
                    String[] optionStrArrOfCheckbox = singleView.getString("ddn").split("\\,");
                    String finalString = "";

                    if(selectedValueOfCheckbox!= null){
                        for(int i=0; i<selectedValueOfCheckbox.length; i++){

                            for(int j=0; j<optionValArrOfCheckbox.length; j++){
                                if(optionValArrOfCheckbox[j].equals(selectedValueOfCheckbox[i])){
                                    if(finalString.equals(""))
                                        finalString =  optionStrArrOfCheckbox[j];
                                    else
                                        finalString =  finalString+ "," + optionStrArrOfCheckbox[i];
                                }
                            }

                        }
                        return finalString;
                    }
                    break;

                case 8:
                    String selectedValueOfRadioBtn = singleView.getString("val");
                    String[] optionValArrOfRadioBtn = singleView.getString("ddnval").split("\\,");
                    String[] optionStrArrOfRadioBtn = singleView.getString("ddn").split("\\,");

                    for(int i=0; i<optionValArrOfRadioBtn.length; i++){
                        if(optionValArrOfRadioBtn[i].equals(selectedValueOfRadioBtn)){
                            return optionStrArrOfRadioBtn[i];
                        }
                    }
                    break;

                case 9:
                    String[] selectedValueOfCheckboxBtn = !singleView.getString("val").equals("") ? singleView.getString("val").split("\\,") : null;
                    String[] optionValArrOfCheckboxBtn = singleView.getString("ddnval").split("\\,");
                    String[] optionStrArrOfCheckboxBtn = singleView.getString("ddn").split("\\,");
                    String finalStringOfCheckboxBtn = "";

                    if(selectedValueOfCheckboxBtn!= null){
                        for(int i=0; i<selectedValueOfCheckboxBtn.length; i++){

                            for(int j=0; j>optionValArrOfCheckboxBtn.length; j++){
                                if(optionValArrOfCheckboxBtn[j].equals(selectedValueOfCheckboxBtn[i])){
                                    if(finalStringOfCheckboxBtn.equals(""))
                                        finalStringOfCheckboxBtn =  optionStrArrOfCheckboxBtn[i];
                                    else
                                        finalStringOfCheckboxBtn =  finalStringOfCheckboxBtn+ "," + optionStrArrOfCheckboxBtn[i];
                                }
                            }

                        }
                        return finalStringOfCheckboxBtn;
                    }
                    break;

                case 10:
                    //YD will not come as it show header on UI
                    break;

                case 11:
                    return singleView.getString("val");


                case 12:
                    HashMap<Long, Parts> unsortedPartList = (HashMap<Long, Parts>) DataObject.partTypeXmlDataStore;

                    Long selectedValueOfPtype = Long.valueOf(singleView.getString("val"));
                    return unsortedPartList.get(selectedValueOfPtype).getName();

            }
        }catch (JSONException j){
            j.printStackTrace();
        }
        return "";
    }


}
