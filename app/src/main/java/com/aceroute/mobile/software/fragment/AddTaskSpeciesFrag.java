package com.aceroute.mobile.software.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.ServiceType;
import com.aceroute.mobile.software.customersite.Viewholder;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.utilities.PreferenceHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddTaskSpeciesFrag extends BaseFragment implements HeaderInterface {

	HashMap<Long, ServiceType> mapOrderTask;
	Long[] Tkeys;
	ArrayList<String> mTreeSpeciesArryList;
	ArrayList<Long> mTreeSpeciesArryListId;
	private EditText mSearchView;
	 private ListView mListView;
	 ArrayList<String> blankList;
	 Viewholder adapter;
	 
	 private ArrayAdapter<String> mAdapter;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mActivity.setHeaderTitle("", "Select Species", "");
    	
        View v = inflater.inflate(R.layout.activity_add_task_species_frag,null);
		TypeFaceFont.overrideFonts(mActivity, v);
        mTreeSpeciesArryList = new ArrayList<String>();
        mTreeSpeciesArryListId = new ArrayList<Long>();
        blankList = new ArrayList<String>();
        
        
        HashMap<Long, ServiceType> unsortedOrderTask = (HashMap<Long, ServiceType>) DataObject.taskTypeXmlDataStore;
		mapOrderTask = sortSeviceTypeLst(unsortedOrderTask);
		Tkeys = mapOrderTask.keySet().toArray(new Long[mapOrderTask.size()]);
		for(int i = 0; i <mapOrderTask.size(); i++) 
		{			
			ServiceType odrService = mapOrderTask.get(Tkeys[i]);			
			mTreeSpeciesArryList.add(String.valueOf(odrService.getNm()));
			mTreeSpeciesArryListId.add(odrService.getId());
			blankList.add("");
		}
        
        
        
        mSearchView = (EditText) v.findViewById(R.id.searchbox_add_species);
        mListView = (ListView) v.findViewById(R.id.list_view_add_species);
        
        adapter= new Viewholder(mActivity, mTreeSpeciesArryList, mTreeSpeciesArryListId, blankList,0);
        mListView.setAdapter(adapter);
        
        mSearchView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String text=mSearchView.getText().toString().toLowerCase(Locale.getDefault());
				adapter.filter(text);
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
        
		mListView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				hideSoftKeyboard();
				return false;
			}
		});
        
        mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				
				String x = (String) mListView.getItemAtPosition(position);
				
				PreferenceHandler.setCurtSteCustdatForcustList(mActivity.getApplicationContext(),1);
				PreferenceHandler.setCustSiteData(mActivity.getApplicationContext(), x);
				mActivity.popFragments(mActivity.UI_Thread);
				
				
				hideSoftKeyboard();
			}
            });
        return v;
	}

	public void hideSoftKeyboard() {
		if(mActivity.getCurrentFocus()!=null){
			InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
		}
	}
	
	// Sorting hashMap
			private static HashMap<Long, ServiceType> sortSeviceTypeLst(HashMap<Long, ServiceType> unsortMap) {

				HashMap<Long, ServiceType> sortedMap = new LinkedHashMap<Long, ServiceType>();

				if (unsortMap!=null){
					// Convert Map to List
					List<Map.Entry<Long, ServiceType>> list =
						new LinkedList<Map.Entry<Long, ServiceType>>(unsortMap.entrySet());
		
					// Sort list_cal with comparator, to compare the Map values
					Collections.sort(list, new Comparator<Map.Entry<Long, ServiceType>>() {
						public int compare(Map.Entry<Long, ServiceType> o1,
										   Map.Entry<Long, ServiceType> o2) {
							return (o1.getValue().getNm()).compareTo(o2.getValue().getNm());
						}
					});
		
					// Convert sorted map back to a Map
					for (Iterator<Map.Entry<Long, ServiceType>> it = list.iterator(); it.hasNext();) {
						Map.Entry<Long, ServiceType> entry = it.next();
						sortedMap.put(entry.getKey(), entry.getValue());
					}
				}
				return sortedMap;
			}


			@Override
			public void headerClickListener(String callingId) {
				// TODO Auto-generated method stub
				
			}
			public void loadDataOnBack(BaseTabActivity mActivity) {
				mActivity.registerHeader(this);
				
			}
}
