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

import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.Form;
import com.aceroute.mobile.software.component.reference.Parts;
import com.aceroute.mobile.software.component.reference.ServiceType;
import com.aceroute.mobile.software.component.reference.SiteType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class OrderFormAdapter extends BaseAdapter {
	LayoutInflater mInflater;
	Context mContext;
	HashMap<Long, Form> orderFormListMap;
	Long[] keys;
	HashMap<Long, ServiceType> taskTypeList;

	private HashMap<Long, SiteType> mapSiteType;


	public OrderFormAdapter(Context context, Object orderFormListMap, HashMap<Long, SiteType> siteTypeHashMap) {

		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		this.orderFormListMap = (HashMap<Long, Form>) orderFormListMap;

		keys = this.orderFormListMap.keySet().toArray(new Long[this.orderFormListMap.size()]);
		
		taskTypeList = (HashMap<Long, ServiceType>) DataObject.taskTypeXmlDataStore;

		this.mapSiteType = siteTypeHashMap;


/*this.mapOrderTaskStatus = new HashMap<Long, SiteType>();
		for (Map.Entry entry:mapOrderTaskStatus.entrySet()) {
			if(mapOrderTaskStatus.get(entry.getKey()).getTid()!=9) {
				this.mapOrderTaskStatus.put((Long)entry.getKey(), (SiteType) entry.getValue());
			}
		}*/

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return orderFormListMap.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return orderFormListMap.get(keys[position]);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	class ViewHolder {

		TextView HeaderText;
        RecyclerView verticalLayoutTxtView;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View vi = convertView;
		ViewHolder holder;

		if(convertView == null){
            vi = mInflater.inflate(R.layout.row_form, null);
            holder = new ViewHolder();

            holder.HeaderText = (TextView)vi.findViewById(R.id.order_form_header_Txt);
            holder.verticalLayoutTxtView = (RecyclerView)vi.findViewById(R.id.vertical_layout_txt_view);
            holder.verticalLayoutTxtView.setLayoutManager(new LinearLayoutManager(mContext));
            vi.setTag(holder);
        }else
            holder = (ViewHolder) vi.getTag();

        try {
            SiteType siteObj = mapSiteType.get(Long.valueOf(((Form)getItem(position)).getFtid()));
            if(siteObj != null)
                holder.HeaderText.setText(siteObj.getNm());
            JSONObject formData = new JSONObject (((Form)getItem(position)).getFdata());
            JSONArray formViewsList = formData.getJSONArray("frm");

            final ArrayList<String> valueToShow =  new ArrayList<String>();
            final ArrayList<String> fontList =  new ArrayList<String>();

            /*JSONObject siteObjJson = new JSONObject(siteObj.getDtl());
            JSONArray siteObjJsonArr = siteObjJson.getJSONArray("frm");*/

            for(int i=0; i<formViewsList.length(); i++) {
                JSONObject singleView = (JSONObject) formViewsList.get(i);

                if ( singleView.getString("lst").equals("1")) {
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
			vi = mInflater.inflate(R.layout.row_form, null);
			holder = new ViewHolder();
			holder.taskType = (TextView)vi.findViewById(R.id.order_part_pname_task);
			holder.taskdtl = (TextView)vi.findViewById(R.id.order_note_task);
			holder.taskStat = (TextView)vi.findViewById(R.id.order_note_task_thirdText);
			holder.taskRate = (TextView)vi.findViewById(R.id.order_part_pquantity_task);
			holder.taskcmt = (TextView)vi.findViewById(R.id.order_note_task_forthText);
			Typeface tf = TypeFaceFont.getCustomTypeface(mContext);
			holder.taskRate.setTypeface(tf,  Typeface.BOLD);

			holder.backview_edit = (TextView) vi.findViewById(R.id.back_task_dummy);
			holder.backview_edit_bottom = (TextView) vi.findViewById(R.id.back_task_dummy_bottom);
			holder.backview_del = (TextView) vi.findViewById(R.id.back_task_dummy1);
			holder.backview_del_bottom = (TextView) vi.findViewById(R.id.back_task_dummy1_bottom);

			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		holder.taskType.setText(taskTypeList.get(((OrderTask) getItem(position)).getTask_id()).getNm());
		holder.taskType.setTag(((OrderTask) getItem(position)).getTask_id());
		holder.taskType.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));

		String dtl = taskTypeList.get(((OrderTask) getItem(position)).getTask_id()).getDtl();
		if(dtl!=null && !dtl.trim().equals("")) {
			holder.taskdtl.setVisibility(View.VISIBLE);
			holder.taskdtl.setText(dtl);
			holder.taskdtl.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
		}else{
			holder.taskdtl.setVisibility(View.GONE);
		}

		String cmt = ((OrderTask) getItem(position)).getTree_comment();
		if(cmt!=null && !cmt.trim().equals("")) {
			holder.taskcmt.setVisibility(View.VISIBLE);
			holder.taskcmt.setText(cmt);
			holder.taskcmt.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
		}else{
			holder.taskcmt.setVisibility(View.GONE);
		}


		if (mapOrderTaskStatus!= null && ((OrderTask) getItem(position)).getAction_status()!=null && !((OrderTask) getItem(position)).getAction_status().equals("") &&
				mapOrderTaskStatus.get(Long.valueOf(((OrderTask) getItem(position)).getAction_status()))!= null ) {
			if(mapOrderTaskStatus.get(Long.valueOf(((OrderTask) getItem(position)).getAction_status())).getTid()==9) {
				holder.taskStat.setText(mapOrderTaskStatus.get(Long.valueOf(((OrderTask) getItem(position)).getAction_status())).getNm());
				holder.taskStat.setVisibility(View.VISIBLE);
				holder.taskStat.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
			}else{
				holder.taskStat.setText("");
				holder.taskStat.setVisibility(View.GONE);
			}
		}

		else {
			holder.taskStat.setText("");
			holder.taskStat.setVisibility(View.GONE);
		}

		holder.taskRate.setText(((OrderTask) getItem(position)).getTree_actualcount());
		holder.taskRate.setTextColor(Color.parseColor("#3498DB"));
		holder.taskRate.setTextSize(50 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));

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
                                        finalString =  optionStrArrOfCheckbox[i];
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
                                        finalStringOfCheckboxBtn =  optionStrArrOfCheckboxBtn[j];
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


                case 15:

                    return singleView.getString("val");

            }

        }

        catch (JSONException j){
            j.printStackTrace();
        }
        return "";
    }
}

