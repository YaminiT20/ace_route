package com.aceroute.mobile.software.offline;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.adaptor.OfflineCtryCtyAdpt;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.reference.OfflineCountry_City;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.fragment.BaseFragment;
import com.aceroute.mobile.software.http.Response;

import java.util.ArrayList;

public class CityListFragment extends BaseFragment implements HeaderInterface, RespCBandServST {
	
	ArrayList<String> cityCodeList;
	ArrayList<OfflineCountry_City> cityLst;

	ListView siteListview;
	OfflineCtryCtyAdpt adapter;  
	int flag;
	public static int GET_SITE_REQ=1;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.site,null); // YD also using same layout for customer site list_cal
		mActivity.registerHeader(this);
		mActivity.setHeaderTitle("", "SELECT MAP AREA", "");
		
        Typeface tf = TypeFaceFont.getCustomTypeface(mActivity.getApplicationContext());
		siteListview = (ListView)v.findViewById(R.id.list_view_custsite);
		
		
		Bundle extra = getArguments();
		cityCodeList = extra.getStringArrayList("LIST_OF_CITIES");
		
		MapPackagesListActivity  mapPackLstObj = new MapPackagesListActivity();
		if (extra.getString("IS_CHILD_AVAILABLE").equals("1")){
			cityLst = mapPackLstObj.getCities(cityCodeList);
		}
		else {
			cityLst = mapPackLstObj.getCountryById(cityCodeList);
			cityLst.get(0).setName("All Countries");
		}
        
		adapter= new OfflineCtryCtyAdpt(mActivity, cityLst,1);  
        siteListview.setAdapter(adapter);
		
		siteListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				OfflineCountry_City x = (OfflineCountry_City) siteListview.getItemAtPosition(position);
				
				if (!x.isDownloaded())//YD if not already downloaded
				{
					x.setDownloaded(true);
					view.findViewById(R.id.ctry_cty_selection).setVisibility(View.VISIBLE);
					
					MapPackagesListActivity downloadmap = new MapPackagesListActivity();
					downloadmap.downloadOfflineMap(mActivity.getApplicationContext(),x.getCode());
				}
				else {
					//Toast.makeText(mActivity, "Map already downloaded", Toast.LENGTH_SHORT);
					MapPackagesListActivity downloadmap = new MapPackagesListActivity();
					downloadmap.deleteOfflineMapById(x.getCode());
					x.setDownloaded(false);
					view.findViewById(R.id.ctry_cty_selection).setVisibility(View.GONE);
				}
			}
		});
		
		return v;
		
	}

	@Override
	public void onStart() {
		super.onStart();
		//fetchSitesForCust();
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void ServiceStarter(RespCBandServST activity, Intent intent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setResponseCallback(String response, Integer reqId) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setResponseCBActivity(Response response) {
		
	}

	@Override
	public void headerClickListener(String callingId) {
		// TODO Auto-generated method stub
		if(callingId.equals(mActivity.HeaderPlusPressed)){

			/*AddSiteActy addSiteFragment = new AddSiteActy();
			Bundle extra = new Bundle();
			extra.putString("CID", cid);
			extra.putString("CNM", cnm);
			
			addSiteFragment.setArguments(extra);
			mActivity.pushFragments(Utilities.CREATEORDER, addSiteFragment, true, true,BaseTabActivity.UI_Thread); //use this code YD
		*/  //YD TODO
		}
	}

	public void loadDataOnBack(BaseTabActivity context) {
		mActivity.setHeaderTitle("", "SELECT MAP AREA", "");
	}

}
