package com.aceroute.mobile.software.customersite;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.reference.Site;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.fragment.BaseFragment;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.CommonSevenReq;
import com.aceroute.mobile.software.requests.SaveSiteRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;

public class AddSiteActy extends BaseFragment implements OnItemSelectedListener, HeaderInterface, RespCBandServST{

	EditText adr1 , adr2,detail , suiteApt;
	CheckBox useCurrentLoc;
	TextView T_adr1,T_adr2;
	String custId , custNm,name,id;
	MyDialog dialog;
	String currentGeo= null;
	TextView nameHead, addre, detailHead, siteTypeHead ,odr_duration_txt , txt_description;
	Spinner siteType;
	RelativeLayout parentView;
	int selecteditem = -1;
	//public static Handler siteTypeResHandler;
	ArrayList<String> siteTypeId, sitetypeNm;
	private int GETSITETYPE=1;
	private int SAVESITE=2;
	
	private HashMap<Long, SiteType> siteListMap;
	Custom_spinner_adap dataAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.add_site_acty);*/
		
		View v = inflater.inflate(R.layout.add_site_acty,null);

		mActivity.registerHeader(this);
		mActivity.setHeaderTitle("", "ADD LOCATION", "");

		Bundle extra = getArguments();
		custId = extra.get("CID").toString();
		custNm = extra.get("CNM").toString();
		
		
	//	header = (TextView)v.findViewById(R.id.header_save_site);
		Typeface tf = TypeFaceFont.getCustomTypeface(mActivity.getApplicationContext());
    //    header.setTypeface(tf);
        
		/*subHeader = (TextView)findViewById(R.id.sub_header_save_site);
		subHeader.setTypeface(tf);*/
		
		parentView = (RelativeLayout) v.findViewById(R.id.parentView);
		
		nameHead = (TextView)v.findViewById(R.id.save_address_one);
		nameHead.setTypeface(tf);
		
		detailHead= (TextView)v.findViewById(R.id.save_detail_site);
		detailHead.setTypeface(tf);
		
		siteTypeHead =(TextView)v.findViewById(R.id.save_site_type);
		siteTypeHead.setTypeface(tf);
		
		addre = (TextView)v.findViewById(R.id.save_address_two);
		addre.setTypeface(tf);
		
		useCurrentLoc = (CheckBox)v.findViewById(R.id.get_current_loc_btn);
		useCurrentLoc.setTypeface(tf);
		
            //    T_adr1=(TextView) v.findViewById(R.id.save_address_one);
		//T_adr2=(TextView) v.findViewById(R.id.save_address_two);
		//T_adr1.setText(Utilities.getStringForAppAndr(mActivity,13));  to do YD
		//T_adr2.setText(Utilities.getStringForAppAndr(mActivity,16));  to do YD
	/*	if(custNm!=null)
			header.setText("Add Location");*/
		/*if(custId!=null)
			subHeader.setText(custId);*/
		
		adr1 = (EditText)v.findViewById(R.id.save_address_one_etext);
		adr1.setTypeface(tf);
		adr2 = (EditText)v.findViewById(R.id.save_address_two_etext);
		adr2.setTypeface(tf);
		detail = (EditText)v.findViewById(R.id.save_detail_site_etext);
		detail.setTypeface(tf);

		suiteApt = (EditText)v.findViewById(R.id.save_address_twoo_etext);
		suiteApt.setTypeface(tf);

		siteType = (Spinner)v.findViewById(R.id.save_site_type_spinner);
		siteType.setOnItemSelectedListener(this);
		siteType.setBackgroundColor(Color.TRANSPARENT);
		
	//	saveAdr = (Button) v.findViewById(R.id.btn_save_site);
	//	saveAdr.setTypeface(tf);
		
	//	backbtn = (ImageView)v.findViewById(R.id.back_button_addsite);
	//	R_imgebck=(RelativeLayout) v.findViewById(R.id.Bck_button_site);
		
		// Request for spinner data 
		siteTypeId = new ArrayList<String>();
		sitetypeNm = new ArrayList<String>();

		odr_duration_txt = (TextView) v.findViewById(R.id.save_address_twoo);

		setDyanmicFont();
		getSitetypeList();
		
		// to handle site type data response
		/*siteTypeResHandler = new Handler()
		{
			@Override
			public void handleMessage(Message message) {
				super.handleMessage(message);
				
				if (message != null) {
					Bundle data = message.getData();
					if (data != null) {
						int doAction = data.getInt(AceRoute.REQUEST_ACTION);
						switch (doAction) {
						case 1:
							String FinalresultXml = data.getString("ResultForsitetype");
							
							Element elckh[] = Utilities.parseXmls(FinalresultXml,"data");
							NodeList node = elckh[0].getElementsByTagName("success");
							
							if (node!=null && node.getLength()>0)
							{
							}
							else
							{	
								Element el[] =Utilities.parseXmls(FinalresultXml,"ltype");
						         for(int i = 0 ; i < el.length;i++) {
	
						        	 fillData(el[i]);
						        	 
									}
						        
						      Custom_spinner_adap dataAdapter = new Custom_spinner_adap(AddSiteActy.this,R.layout.cust_spinner, sitetypeNm);
						        // dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						       siteType.setAdapter(dataAdapter);
						      
						         if(!sitetypeNm.isEmpty())
						         {
						        	 siteType.setSelection(0);
						         }
						         // code to set spinner adaptor here
							}
							break;
						}
					}
				}
			}
		};*/
		
		
		/*saveAdr.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String name =null;
				String addrs= null;
				
				if (!(useCurrentLoc.isChecked())&& adr2.getText().toString().length()<1 &&
						adr1.getText().toString().length()<3){
					
					Toast.makeText(mActivity, "Nothing to save.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if (useCurrentLoc.isChecked())
					currentGeo = Utilities.getLocation(mActivity);
				else
					currentGeo = "0,0";
				if(adr1.getText().toString().length()<3)
					name="Default";
				else
					name=adr1.getText().toString();
				if(adr2.getText().toString().length()<3)
					addrs="Location Geocode";
				else
					addrs=adr2.getText().toString();
					
					
					
					String tistamp = String.valueOf(getCurrentTime());
					String requestStr = "{\"url\":\"\",\"action\":\"savesite\",\"data\":{\"adr\":\""+addrs+"\"," +
							"\"nm\":\""+name+"\",\"cid\":\""+custId+"\"," +
									"\"tstamp\":\""+tistamp+"\",\"id\":\"0\",\"geo\":\""+currentGeo+"\",\"dtl\":\""+detail.getText().toString() +"\",\"tid\":\""+siteTypeId.get(selecteditem)+"\"," +
											"\"ltpnm\":\""+sitetypeNm.get(selecteditem)+"\"}}"; 
					AceRouteService.jsInterface.saveSite(requestStr);
				
				}
				else
				{
					
					String D_title,D_desc,OK;
					D_title=Utilities.getStringForAppAndr(AddSiteActy.this,10);
					D_desc=Utilities.getStringForAppAndr(AddSiteActy.this,6);
					dialog = new MyDialog(AddSiteActy.this,D_title,D_desc , "OK");
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setkeyListender(new MyDiologInterface() {
						
						@Override
						public void onPositiveClick() throws JSONException {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onNegativeClick() {
							dialog.dismiss();
						}
					});
					
					dialog.onCreate1(null);
					dialog.show();
				}
						
			}
		});*/
		
		useCurrentLoc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				currentGeo = Utilities.getLocation(mActivity);
			}
		});
		
		parentView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideSoftKeyboard();
				
				return false;
			}
		});
	
		return v;
	}


	private void setDyanmicFont() {

		int size = PreferenceHandler.getCurrrentFontSzForApp(mActivity);
		nameHead.setTextSize(22 + (size));
		addre.setTextSize(22 + (size));
		odr_duration_txt.setTextSize(22 + (size));
		detailHead.setTextSize(22 + (size));
		siteTypeHead.setTextSize(22 + (size));
		useCurrentLoc.setTextSize(22 + (size));

		adr1.setTextSize(22 + (size));
		adr2.setTextSize(22 + (size));
		suiteApt.setTextSize(22 + (size));
		detail.setTextSize(22 + (size));
	}
	private void getSitetypeList() {
		/*String requestStr = "{\"url\":\"\",\"action\":\"getsitetype\",\"source\":\"localonly\"}"; 
		AceRouteService.jsInterface.getsitetype(requestStr);*/
		CommonSevenReq getSiteTypeList = new CommonSevenReq();
		getSiteTypeList.setAction("getgentype");  // YD earlier getsitetype
		getSiteTypeList.setSource("localonly");
		getSiteTypeList.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		
		SiteType siteTypObj = new SiteType();
		siteTypObj.getObjectDataStore(getSiteTypeList, mActivity, this, GETSITETYPE);
		
	}

	private void fillData(Element empEl) {
		if (getTextValue(empEl,"nm")!=null)
			name=getTextValue(empEl,"nm");
		else 
			name = "No Name";

		id = getTextValue(empEl, "id");
			siteTypeId.add(id);
			sitetypeNm.add(name);
	}
	private void fillData(SiteType siteTypeObj) {
		
		name=siteTypeObj.getNm();
		id = String.valueOf(siteTypeObj.getId());
		siteTypeId.add(id);
		sitetypeNm.add(name);
		
	}
	 private String getTextValue(Element ele, String tagName) {
			String textVal = null;
			NodeList nl = ele.getElementsByTagName(tagName);
			if(nl != null && nl.getLength() > 0) {
				Element el = (Element)nl.item(0);
				//if (tagName=="nm" && el.getFirstChild().getNodeValue()==null)
				if ((tagName=="nm" ||tagName=="id") && !(el.getChildNodes().getLength() > 0))
					textVal = null;
				else
				textVal = el.getFirstChild().getNodeValue();
			}
			return textVal;
	 	}
	
	protected long getCurrentTime() {
		long sinceMidnight = System.currentTimeMillis();
		return sinceMidnight;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		selecteditem = position;
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	
	class Custom_spinner_adap extends ArrayAdapter<String> {
		
		ArrayList<String> Spinner_data;
		Typeface tf = TypeFaceFont.getCustomTypeface(mActivity.getApplicationContext());
        

		public Custom_spinner_adap(AddSiteActy addSiteActy, int custSpinnerId,
				ArrayList<String> sitetypeNm) {
			// TODO Auto-generated constructor stub
			super(mActivity,custSpinnerId,sitetypeNm);
			this.Spinner_data=sitetypeNm;
			
		}
			
			public View getCustomView(int position, View row,
					ViewGroup parent) {
				LayoutInflater inflater = mActivity.getLayoutInflater();
				View layout = inflater.inflate(R.layout.cust_spinner, parent, false);
				//layout.setPadding(layout.getPaddingLeft(), layout.getPaddingTop(), layout.getPaddingRight()+2, layout.getPaddingBottom());
				TextView tvspn = (TextView) layout
						.findViewById(R.id.text_spinner);
				tvspn.setText(sitetypeNm.get(position));
			    //tvspn.setTextColor(Color.BLACK);
				tvspn.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mActivity)));
			    tvspn.setTypeface(tf);

					
						return layout;

				}
                    
                    

					
					@Override
					public View getDropDownView(int position, View row,
					ViewGroup parent) {
					return getCustomView(position, row, parent);
					}

					
					@Override
					public View getView(int position, View row, ViewGroup parent) {
					return getCustomView(position, row, parent);
					}
					
			
			
				/*	public class SpinnerModel {
						String Sitetypename;
						public void setsitetypeName(String StypeName)
				        {
				            this.Sitetypename = StypeName;
				        }
						 public String getsiteName()
					        {
					            return this.Sitetypename;
					        }
					}*/
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
			if (response.getStatus().equals("success")&& 
					response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED)))
			{
				if (response.getId()==GETSITETYPE)
				{
					siteListMap = (HashMap< Long, SiteType>)response.getResponseMap();
			        Long[] keys = siteListMap.keySet().toArray(new Long[siteListMap.size()]);
			        for(int i = 0 ; i < siteListMap.size();i++) {

			        	SiteType odrSite = siteListMap.get(keys[i]);	
						if (odrSite.getTid()!=0 && odrSite.getTid()==1){
							fillData(odrSite);
						}
					}
			        
			        
					dataAdapter = new Custom_spinner_adap(AddSiteActy.this,R.layout.cust_spinner, sitetypeNm);
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							siteType.setAdapter(dataAdapter);
						}
					});
					
				}
				if (response.getId()==SAVESITE)
				{
					 goBack(mActivity.SERVICE_Thread);
				}
			}
			else if(response.getStatus().equals("success")&& 
				response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
		    {

		    }
		}
	}
	
	private void goBack(int threadType) {
		mActivity.popFragments(threadType);
	}
	
	@Override
	public void headerClickListener(String callingId) {
		// TODO Auto-generated method stub
		if(callingId.equals(mActivity.HeaderDonePressed)){
			String name =null;
			String addrs= null;

			if (!(useCurrentLoc.isChecked())&& adr2.getText().toString().length()<1 &&
					adr1.getText().toString().length()<3){
				
				Toast.makeText(mActivity, "Nothing to save.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (useCurrentLoc.isChecked())
				currentGeo = Utilities.getLocation(mActivity);
			else if (currentGeo == null || currentGeo.equals(""))
				currentGeo = "0,0";
			if(adr1.getText().toString().length()<3)
				name="Default";
			else
				name=adr1.getText().toString();
			if(adr2.getText().toString().length()<3)
				addrs="Location Geocode";
			else
				addrs=adr2.getText().toString();
				
			String tistamp = String.valueOf(getCurrentTime());
			
			SaveSiteRequest req = new SaveSiteRequest();
			
			req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
			req.setAction(Site.ACTION_SAVE_SITE);
			req.setAdr(addrs);
			req.setNm(name);
			req.setCid(custId);
			req.setTstamp(tistamp);
			req.setId("0");
			req.setGeo(currentGeo);
			req.setDtl(detail.getText().toString());
			if (selecteditem>=0) {
				req.setTid(siteTypeId.get(selecteditem));
				req.setLtpnm(sitetypeNm.get(selecteditem));
			}
			Site.saveData(req, mActivity, this, SAVESITE);
			
			
			/*"{\"url\":\"\",\"action\":\"savesite\",\"data\":{\"adr\":\""+addrs+"\"," +
				"\"nm\":\""+name+"\",\"cid\":\""+custId+"\"," +
				"\"tstamp\":\""+tistamp+"\",\"id\":\"0\",\"geo\":\""+currentGeo+"\",
				\"dtl\":\""+detail.getText().toString() +"\",\"tid\":\""+siteTypeId.get(selecteditem)+"\"," +
				"\"ltpnm\":\""+sitetypeNm.get(selecteditem)+"\"}}" */
			
			hideSoftKeyboard();
		}
	}
	
	
	public void hideSoftKeyboard() {
		if(mActivity.getCurrentFocus()!=null){
			InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
		}
	}

	public void loadDataOnBack(BaseTabActivity mActivity) {
		mActivity.registerHeader(this);
		mActivity.setHeaderTitle("", "ADD LOCATION", "");
		
	}

}
