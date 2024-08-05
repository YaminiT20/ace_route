package com.aceroute.mobile.software.customersite;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.reference.Site;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.fragment.BaseFragment;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.GetSiteRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.Utilities;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Sitelist extends BaseFragment implements HeaderInterface, RespCBandServST {
	
	String cid;
	String cnm,cTypenm ;
	ArrayList<String> adr, blankList;
	ArrayList<Long> id;
	//RelativeLayout R_imgebck;
	ListView siteListview;
	Viewholder adapter;
//	Button addSite;
//	ImageView backbtn;
//	TextView header, subheader;
	//TextView header , subHeader;
	int flag;
	public static int GET_SITE_REQ=1;
	
	HashMap< Long, Site> siteListMap;
	
	/*@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		String dataFrmXml = intent.getStringExtra("XmlData");
		int Flag1 = intent.getIntExtra("Flag", 0);
		
		if (Flag1==0)
		{
			// response for list_cal of sites for customer
			adr.clear();
			id.clear();
			blankList.clear();
			Element el[] =Utilities.parseXmls(dataFrmXml,"loc");
	         for(int i = 0 ; i < el.length;i++) {

	        	 fillData(el[i]);
				}
	         
	         adapter= new Viewholder(this, adr,id,blankList,1);
	         siteListview.setAdapter(adapter);
		}
		else if (Flag1==1)
		{
			// site saved
			fetchSitesForCust();
		}
	}*/
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		/*super.onCreate1(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.site);*/
		
		View v = inflater.inflate(R.layout.site,null);
		
		mActivity.registerHeader(this);
		
		mActivity.setHeaderTitle("", "SELECT LOCATION", "");
		
	//	header = (TextView)v.findViewById(R.id.header_site);
        Typeface tf = TypeFaceFont.getCustomTypeface(mActivity.getApplicationContext());
     //   header.setTypeface(tf);
        
      //  subheader=(TextView)findViewById(R.id.sub_header_site);
     //   subheader.setTypeface(tf);
		
	//	addSite = (Button) v.findViewById(R.id.btn_site_addgo);
		siteListview = (ListView)v.findViewById(R.id.list_view_custsite);
	//	backbtn = (ImageView)v.findViewById(R.id.site_back_button);
	//	R_imgebck=(RelativeLayout) v.findViewById(R.id.Bck_button_site);
		
		adr = new ArrayList<String>();
		id = new ArrayList<Long>();
		blankList = new ArrayList<String>();
		
		Bundle extra = getArguments();
		cid = extra.get("CID").toString();
		cnm = extra.get("CNM").toString();
		cTypenm = extra.get("CTYPENM").toString();
		flag = extra.getInt("flag");
		
		/*header = (TextView)findViewById(R.id.header_site);
		subHeader = (TextView)findViewById(R.id.sub_header_site);
		if(cnm!=null)
			header.setText(cnm);
		if(cid!=null)
			subHeader.setText(cid);*/
		
		/*addSite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent i = new Intent(mActivity.getApplicationContext(),AddSiteActy.class);
				i.putExtra("CID", cid);
				i.putExtra("CNM", cnm);
				startActivity(i);
				
				AddSiteActy addSiteFragment = new AddSiteActy();
				Bundle extra = new Bundle();
				extra.putString("CID", cid);
				extra.putString("CNM", cnm);
				
				addSiteFragment.setArguments(extra);
				mActivity.pushFragments(Utilities.CREATEORDER, addSiteFragment, true, true); //use this code YD
			}
		});*/
		
		siteListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos,
					long id) {
				if (flag ==0)
				{
					String x = (String)siteListview.getItemAtPosition(pos);
					 x = x+"##"+cid+"##"+cnm+"##"+cTypenm;
					//47398003##Main - Apt#100, 5691 Bay St, Emeryville CA####47348003##Emeryville ATT##
					PreferenceHandler.setCurtSteCustdat(mActivity.getApplicationContext(),1);
					PreferenceHandler.setCurtSteCustdatForcustList(mActivity.getApplicationContext(),1);
					PreferenceHandler.setCustSiteData(mActivity.getApplicationContext(), x);
					mActivity.popFragments(mActivity.UI_Thread);
				}
				/*else if (flag==1)
				{
					// get called directly for the site from create order page 
					String x = (String)siteListview.getItemAtPosition(pos);
					 x = x+"#"+cid+"#"+cnm+"#"+cTypenm;
					 
					 Message msg = new Message();
						Bundle bundle = new Bundle();
						bundle.putInt(AceRoute.REQUEST_ACTION, 12);
						bundle.putString("ResultForJs", x);
						msg.setData(bundle);
						AceRoute.handler.sendMessage(msg);
					//finish(); to do YD
				}*/
			}
		});
		
		/*R_imgebck.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			//	finish();
				
			}
		});*/
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
		fetchSitesForCust();
	}
	
	public void onHiddenChanged(boolean hidden) {
	    super.onHiddenChanged(hidden);
	    if (hidden) {
	        //do when hidden
	    } else {
	    	//do when show 
	    	fetchSitesForCust();
	    }
	}
	
	private void fetchSitesForCust() {
		/*String requestStr = "{\"url\":\"\"," +
				"\"action\":\"getsite\"," +
				"\"cid\":\""+cid+"\"," +
				"\"source\":\"localonly\"}"; 
		AceRouteService.jsInterface.getsite(requestStr);*/
		
		GetSiteRequest req = new GetSiteRequest();
		req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		req.setCid(cid);
		req.setSource("localonly");
		req.setAction("getsite");
		Site.getData(req,mActivity, this,GET_SITE_REQ );
	}
    
    private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			if ((tagName=="nm" ||tagName=="adr"||tagName=="ltpnm") && !(el.getChildNodes().getLength() > 0))
				textVal = null;
			else
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
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
		
		if (response!=null)
		{
			adr.clear();
			id.clear();
			blankList.clear();
			
			
			siteListMap = (HashMap< Long, Site>)response.getResponseMap();
			siteListMap = sortSiteLst(siteListMap);
	        Long[] keys = siteListMap.keySet().toArray(new Long[siteListMap.size()]);
	        for(int i = 0 ; i < siteListMap.size();i++) {

	       	 fillData(siteListMap.get(keys[i]));
			 }
	        
	         adapter= new Viewholder(mActivity, adr,id,blankList,1);
	        
	         mActivity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					siteListview.setAdapter(adapter);
				}
			});
	         
		}
	}

	// Sorting hashMap
	private static HashMap<Long, Site> sortSiteLst(HashMap<Long, Site> unsortMap) {

		HashMap<Long, Site> sortedMap = new LinkedHashMap<Long, Site>();

		if (unsortMap!=null){
			// Convert Map to List
			List<Map.Entry<Long, Site>> list =
					new LinkedList<Map.Entry<Long, Site>>(unsortMap.entrySet());

			// Sort list_cal with comparator, to compare the Map values
			Collections.sort(list, new Comparator<Map.Entry<Long, Site>>() {
				public int compare(Map.Entry<Long, Site> o1,
								   Map.Entry<Long, Site> o2) {
					return (o1.getValue().getNm()).compareTo(o2.getValue().getNm());
				}
			});

			// Convert sorted map back to a Map
			for (Iterator<Map.Entry<Long, Site>> it = list.iterator(); it.hasNext();) {
				Map.Entry<Long, Site> entry = it.next();
				sortedMap.put(entry.getKey(), entry.getValue());
			}
		}
		return sortedMap;
	}

	private void fillData(Site odrsiteObj) {
		String streetAdr = odrsiteObj.getAdr();
		String suiteAdr = odrsiteObj.getAdr2();
		if (suiteAdr!= null && suiteAdr.length()>0)
			streetAdr = suiteAdr+", "+streetAdr;

		if(!odrsiteObj.getSitetypenm().equals("") && !streetAdr.equals(""))
			adr.add(odrsiteObj.getSitetypenm()+" - "+odrsiteObj.getNm()+" - "+streetAdr);
		else if(!odrsiteObj.getSitetypenm().equals("") && streetAdr.equals(""))
			adr.add(odrsiteObj.getSitetypenm()+" - "+odrsiteObj.getNm());
		else if(odrsiteObj.getSitetypenm().equals("") && !streetAdr.equals(""))
			adr.add(odrsiteObj.getNm()+" - "+streetAdr);
		else if(odrsiteObj.getSitetypenm().equals("") && streetAdr.equals(""))
			adr.add(odrsiteObj.getNm());
		
	//	adr.add(odrsiteObj.getSitetypenm()+" - "+odrsiteObj.getNm()+" - "+odrsiteObj.getAdr()); // commented by mandeep
		id.add(odrsiteObj.getId());
		String ltp = odrsiteObj.getSitetypenm();
		blankList.add(ltp);
		Log.i("software", "name :" + adr.get(0) + "id :" + adr.get(0));
		
	}

	@Override
	public void headerClickListener(String callingId) {
		// TODO Auto-generated method stub
		if(callingId.equals(mActivity.HeaderPlusPressed)){

			AddSiteActy addSiteFragment = new AddSiteActy();
			Bundle extra = new Bundle();
			extra.putString("CID", cid);
			extra.putString("CNM", cnm);
			
			addSiteFragment.setArguments(extra);
			mActivity.pushFragments(Utilities.JOBS, addSiteFragment, true, true,BaseTabActivity.UI_Thread); //use this code YD
		}
	}

	public void loadDataOnBack(BaseTabActivity mActivity) {
		mActivity.registerHeader(this);
		mActivity.setHeaderTitle("", "SELECT LOCATION", "");
		fetchSitesForCust();
	}

}
