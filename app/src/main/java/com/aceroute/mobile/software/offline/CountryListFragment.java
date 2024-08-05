
package com.aceroute.mobile.software.offline;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.adaptor.OfflineCtryCtyAdpt;
import com.aceroute.mobile.software.component.reference.OfflineCountry_City;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.fragment.BaseFragment;
import com.aceroute.mobile.software.http.RequestObject;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.Utilities;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Locale;


/**
 * Shows a list_cal that can be filtered in-place with a SearchView in non-iconified mode.
 */
@SuppressLint("NewApi")
public class CountryListFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    private static final String TAG = "SearchViewFilterMode";

    private EditText mSearchView;
 //   RelativeLayout R_imgebck;
    private ListView mListView;
    String xml;
    
    

    OfflineCtryCtyAdpt adapter;
    ArrayList<OfflineCountry_City> countryLst;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mActivity.setHeaderTitle("", "SELECT MAP AREA", "");
    	
        View v = inflater.inflate(R.layout.offline_ctrylist,null);
        Typeface tf = TypeFaceFont.getCustomTypeface(getActivity().getApplicationContext());

        final MapPackagesListActivity  mapPackLstObj = new MapPackagesListActivity();
        countryLst = mapPackLstObj.getAllCountriesAvail();

		if (countryLst.size()<=0){//YD doing this because some time country list_cal is coming empty
			 mapPackLstObj.inializePackages(mActivity.getApplicationContext(),false);
			mapPackLstObj.onDownloadComplete(new MapPackagesListActivity.downloadlist() {
				@Override
				public void onCmpt(boolean val) {
					countryLst = mapPackLstObj.getAllCountriesAvail();
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							adapter= new OfflineCtryCtyAdpt(mActivity, countryLst,0);
							mListView.setAdapter(adapter);
						}
					});

				}
			});
		}
        
        mSearchView = (EditText) v.findViewById(R.id.searchbox_countrylist);
        mSearchView.setTypeface(tf);
		mSearchView.setTextSize(20 + (PreferenceHandler.getCurrrentFontSzForApp(mActivity)));
        mListView = (ListView) v.findViewById(R.id.list_view_ctrylist);
        
        adapter= new OfflineCtryCtyAdpt(mActivity, countryLst,0);
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
				if(mActivity.getCurrentFocus()!=null){
					InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
				}
				return false;
			}
		});
        
        
        mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				
				OfflineCountry_City x = (OfflineCountry_City) mListView.getItemAtPosition(position); // YD getting obj which is selected from the row
				ArrayList<String> citylist = new ArrayList<String>();

				if (x!=null && x.getChildren().size()>0)
				{
					for (int i=0; i<x.getChildren().size(); i++){
						citylist.add(x.getChildren().get(i));
					}
					
					CityListFragment cityLstFrag = new CityListFragment();
					Bundle b = new Bundle();
					b.putStringArrayList("LIST_OF_CITIES", citylist);
					b.putString("IS_CHILD_AVAILABLE", "1");
					cityLstFrag.setArguments(b);
					mActivity.pushFragments(Utilities.JOBS, cityLstFrag, true, true,BaseTabActivity.UI_Thread); //use this code YD
				}
				else 
				{
					citylist.add(x.getCode());
					CityListFragment cityLstFrag = new CityListFragment();
					Bundle b = new Bundle();
					b.putStringArrayList("LIST_OF_CITIES", citylist);
					b.putString("IS_CHILD_AVAILABLE", "0");
					cityLstFrag.setArguments(b);
					mActivity.pushFragments(Utilities.JOBS, cityLstFrag, true, true,BaseTabActivity.UI_Thread); //use this code YD
				}
			}
            });
        
		return v;
    }

    
    @Override
    public void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    }
    
    @Override
    public void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	if (PreferenceHandler.getCurtSteCustdat(mActivity)==1)
    	{
    		PreferenceHandler.setCurtSteCustdat(mActivity,0);

    		/*String x=PreferenceHandler.getCustSiteData(mActivity);
    		Intent data = new Intent(mActivity, AceRoute.class);
    		data.putExtra("ResultForJs", x);
    		mActivity.setResult(mActivity.RESULT_OK, data);*/
    		mActivity.popFragments(mActivity.UI_Thread);
    	}
    }
    
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            mListView.clearTextFilter();
        } else {
            mListView.setFilterText(newText.toString());
        }
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }
    
    
    public RequestObject getRequestObject(int id, String messageString,
			long time) {
		RequestObject object = new RequestObject();
		object.setReqId(id);
		object.setData(messageString);
		//object.setJsinterface(jsInterface);
		object.setTimeInMilliSeconds(time);
		return object;
	}
    
    
    private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			if ((tagName=="nm" ||tagName=="id"||tagName=="ctpnm") && !(el.getChildNodes().getLength() > 0))
			{
				textVal = "";
			}
			else 
				textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

    private long getIntValue(Element ele, String tagName) {
		//in production application you would catch the exception
		return Long.parseLong(getTextValue(ele, tagName));
	}


	public void loadDataOnBack(BaseTabActivity context) {
		// TODO Auto-generated method stub
		mActivity.setHeaderTitle("", "SELECT MAP AREA", "");
		
	}
}
