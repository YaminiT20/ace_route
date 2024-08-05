package com.aceroute.mobile.software.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.AceOfflineSyncEngine;
import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.reference.AssetLabel;
import com.aceroute.mobile.software.component.reference.Assets;
import com.aceroute.mobile.software.component.reference.AssetsType;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.MySearchDialog;
import com.aceroute.mobile.software.dialog.MySearchDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.CommonSevenReq;
import com.aceroute.mobile.software.requests.SaveAsset;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AddEditAssetFragment_OLD extends BaseFragment implements HeaderInterface, RespCBandServST,OnClickListener{
	Context mContext;
	private TextView edttx_status, edttx_pid, edttx_Tid, edttx_Tid2, txtVw_Tid,  txtVw_Tid2, txtVw_note2, txtVw_status, txtVw_pid, txtVw_note3, txtVw_num1, txtVw_num2, txtVw_num6, txtVw_cnt1, txtVw_cnt2, txtVw_note4, txtVw_note5, txtVw_note6, txtVw_num3, txtVw_num4, txtVw_num5, txtVw_note1, txtVw_ct1, txtVw_ct2, txtVw_ct3, edttx_num3, edttx_num4, edttx_num5 ,txtVw_geoCode;
	private EditText  edttx_note2, edttx_note3, edttx_num1, edttx_num2, edttx_num6, edttx_cnt1, edttx_cnt2, edttx_note4, edttx_note5, edttx_note6, edttx_note1, edttx_ct1, edttx_ct2, edttx_ct3;
	private LinearLayout firstbox, secondbox, thirdbox, fourthbox, fifthbox, sixthbox, seventhbox, eightbox;
	private RelativeLayout parentView;
	
	HashMap<Long, AssetsType> assetTypeList;
	String currentOdrId ,headerText;

	public Order activeOrderObj;
	ArrayList<String> mCategoryArryList;
	ArrayList<Long> mCategoryArryListId;
	Long[] keys;
	long partTypeId=-1;
	String partQuant=null;
	String partId = null;
	
	private static int SAVE_ASSET = 1;
	private static int GETSITETYPE = 2;
	private int mheight = 500;
	private HashMap<Long, SiteType> siteGenTypeMapTid2 = new HashMap<Long, SiteType>();
	private HashMap<Long, SiteType> siteGenTypeMapStat = new HashMap<Long, SiteType>();
	private HashMap<Long, SiteType> siteGenTypeMapPid = new HashMap<Long, SiteType>();
	private HashMap<Long, SiteType> siteGenTypeMapNum3 = new HashMap<Long, SiteType>();
	private HashMap<Long, SiteType> siteGenTypeMapNum4 = new HashMap<Long, SiteType>();
	private HashMap<Long, SiteType> siteGenTypeMapNum5 = new HashMap<Long, SiteType>();
	private int itemPosClicked=-1;
	private Long[] sitekeys;
	private Assets activeOdrAssetObject;

	private long elemtSelectedInSearchDlgCategory= -1;
	private long elemtSelectedInSearchDlgTid2= -1;
	private long elemtSelectedInSearchDlgNum3= -1;
	private long elemtSelectedInSearchDlgNum4= -1;
	private long elemtSelectedInSearchDlgNum5= -1;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utilities.log(mActivity, "onCreate1 called for AddEditPartsFragment");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Utilities.log(mActivity, "onCreateView called for AddEditPartsFragment");	
		View v = inflater.inflate(R.layout.add_edit_assest_frag, null);
		TypeFaceFont.overrideFonts(mActivity, v);
		mActivity.registerHeader(this);
		mContext = mActivity;
		initiViewReference(v);		
		
		return v;
	}	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Utilities.log(mActivity, "onActivityCreated called for AddEditPartsFragment");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Utilities.log(mActivity, "onStart called for AddEditPartsFragment");
	}
	@Override
	public void onResume() {
		super.onResume();
		Utilities.log(mActivity, "onResume called for AddEditPartsFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		Utilities.log(mActivity, "onPause called for AddEditPartsFragment");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Utilities.log(mActivity, "onStop called for AddEditPartsFragment");
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Utilities.log(mActivity, "onDestroyView called for AddEditPartsFragment");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Utilities.log(mActivity, "onDestroy called for AddEditPartsFragment");
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		Utilities.log(mActivity, "onDetach called for AddEditPartsFragment");
	}

	public void setAssetObject(Assets assetObj){
		activeOdrAssetObject = assetObj;
	}
	
    private void initiViewReference(View v)
	{		
		final Bundle bundle = this.getArguments();
		currentOdrId = bundle.getString("OrderId");

		headerText = bundle.getString("AssetType");// YD just for backup

		mActivity.setHeaderTitle("", headerText, "");
		parentView = (RelativeLayout) v.findViewById(R.id.parentView_addedit_asset);

		HashMap<Long, AssetsType> unsortedPartList = (HashMap<Long, AssetsType>) DataObject.assetsTypeXmlDataStore;//YD TODO check for null
	        assetTypeList = sortAssetCatagoryLst(unsortedPartList);

		keys = assetTypeList.keySet().toArray(new Long[assetTypeList.size()]);

		mCategoryArryListId  = new ArrayList<Long>();
		mCategoryArryList = new ArrayList<String>();
		for(int i = 0; i < assetTypeList.size(); i++)
		{
			AssetsType assettypeObj = assetTypeList.get(keys[i]);
			mCategoryArryListId.add(assettypeObj.getId());
			mCategoryArryList.add(assettypeObj.getName());
		}
		setTextViews(v);
		setEditTexts(v);
		setDyanmicFont();
		setLinearLayout(v);
		getSiteOrGenType();

		/*txtMaterialCategoryTitle = (TextView) v.findViewById(R.id.txtMaterialCategoryTitle);
		txtMaterialCategoryTitle.setText(bundle.getString("MaterialType"));

		edtMaterialCategory = (EditText) v.findViewById(R.id.edtMaterialCategoryName);
		edtMaterialQty = (EditText) v.findViewById(R.id.edtMaterialQuantity);
		edtMaterialDesc = (TextView) v.findViewById(R.id.edtMaterialDesc);*/



		
	/*	if (tempheaderText.equals("EDIT PART"))
		{	
			partTypeId = bundle.getLong("partTypeId");
			partQuant = bundle.get("partQuantity").toString();
			partId = bundle.get("orderPartId").toString();

			if (assetTypeList.get(partTypeId)!=null) {
				edtMaterialCategory.setText(assetTypeList.get(partTypeId).getName());
				edtMaterialCategory.setTag(assetTypeList.get(partTypeId).getId());
				edtMaterialQty.setText(partQuant);
			}
			else{
				Map.Entry<Long, Parts> entry1 = assetTypeList.entrySet().iterator().next();
				edtMaterialCategory.setText(entry1.getValue().getName());
				edtMaterialCategory.setTag(entry1.getValue().getId());
			}
		}
		else{
			Map.Entry<Long, Parts> entry1 = assetTypeList.entrySet().iterator().next();
			edtMaterialCategory.setText(entry1.getValue().getName());
			edtMaterialCategory.setTag(entry1.getValue().getId());
			edtMaterialQty.setText("1");
		}

		edtMaterialCategory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideSoftKeyboard();
				showCategoryDialog();
			}
		});	*/
		
		parentView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideSoftKeyboard();
				return false;
			}
		});

	}

	private void setLinearLayout(View v) {
		firstbox = (LinearLayout) v.findViewById(R.id.add_edt_asset_firstbox);
		secondbox = (LinearLayout) v.findViewById(R.id.add_edt_asset_secondbox);
		thirdbox = (LinearLayout) v.findViewById(R.id.add_edt_asset_thirdbox);
		fourthbox = (LinearLayout) v.findViewById(R.id.add_edt_asset_fourthbox);
		fifthbox = (LinearLayout) v.findViewById(R.id.add_edt_asset_fifthbox);
		sixthbox = (LinearLayout) v.findViewById(R.id.add_edt_asset_sixthbox);
		seventhbox = (LinearLayout) v.findViewById(R.id.add_edt_asset_seventhbox);
		eightbox = (LinearLayout) v.findViewById(R.id.add_edt_asset_eighthbox);
	}

	private void getSiteOrGenType() {
		CommonSevenReq getSiteTypeList = new CommonSevenReq();
		getSiteTypeList.setAction("getgentype");  // YD earlier getsitetype
		getSiteTypeList.setSource("localonly");
		getSiteTypeList.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");

		SiteType siteTypObj = new SiteType();
		siteTypObj.getObjectDataStore(getSiteTypeList, mActivity, this, GETSITETYPE);
	}

	private void setDyanmicFont() {

		int size = PreferenceHandler.getCurrrentFontSzForApp(mActivity);
		txtVw_Tid.setTextSize(22 + (size));
		txtVw_Tid2.setTextSize(22 + (size));
		txtVw_note2.setTextSize(22 + (size));
		txtVw_status.setTextSize(22 + (size));
		txtVw_pid.setTextSize(22 + (size));
		txtVw_note3.setTextSize(22 + (size));
		txtVw_num1.setTextSize(22 + (size));
		txtVw_num2.setTextSize(22 + (size));
		txtVw_num6.setTextSize(22 + (size));
		txtVw_cnt1.setTextSize(22 + (size));
		txtVw_cnt2.setTextSize(22 + (size));
		txtVw_note4.setTextSize(22 + (size));
		txtVw_note5.setTextSize(22 + (size));
		txtVw_note6.setTextSize(22 + (size));
		txtVw_num3.setTextSize(22 + (size));
		txtVw_num4.setTextSize(22 + (size));
		txtVw_num5.setTextSize(22 + (size));
		txtVw_note1.setTextSize(22 + (size));
		txtVw_ct1.setTextSize(22 + (size));
		txtVw_ct2.setTextSize(22 + (size));
		txtVw_ct3.setTextSize(22 + (size));
		txtVw_geoCode.setTextSize(24 + (size));

		edttx_Tid.setTextSize(22 + (size));
		edttx_Tid2.setTextSize(22 + (size));
		edttx_num3.setTextSize(22 + (size));
		edttx_num4.setTextSize(22 + (size));
		edttx_num5.setTextSize(22 + (size));
		edttx_status.setTextSize(22 + (size));
		edttx_pid.setTextSize(22 + (size));
		edttx_note2.setTextSize(22 + (size));
		edttx_note3.setTextSize(22 + (size));
		edttx_num1.setTextSize(22 + (size));
		edttx_num2.setTextSize(22 + (size));
		edttx_num6.setTextSize(22 + (size));
		edttx_cnt1.setTextSize(22 + (size));
		edttx_cnt2.setTextSize(22 + (size));
		edttx_note4.setTextSize(22 + (size));
		edttx_note5.setTextSize(22 + (size));
		edttx_note6.setTextSize(22 + (size));
		edttx_note1.setTextSize(22 + (size));
		edttx_ct1.setTextSize(22 + (size));
		edttx_ct2.setTextSize(22 + (size));
		edttx_ct3.setTextSize(22 + (size));
	}

	private void setTextViews(View v) {
		txtVw_Tid = (TextView) v.findViewById(R.id.add_edt_asset_tid_txtvw);
		txtVw_Tid2 = (TextView) v.findViewById(R.id.add_edt_asset_tid2_txtvw);
		txtVw_note2 = (TextView) v.findViewById(R.id.add_edt_asset_note2_txtvw);
		txtVw_status = (TextView) v.findViewById(R.id.add_edt_asset_status_txtvw);
		txtVw_pid = (TextView) v.findViewById(R.id.add_edt_asset_pid_txtvw);
		txtVw_note3 = (TextView) v.findViewById(R.id.add_edt_asset_note3_txtvw);
		txtVw_num1 = (TextView) v.findViewById(R.id.add_edt_asset_num1_txtvw);
		txtVw_num2 = (TextView) v.findViewById(R.id.add_edt_asset_num2_txtvw);
		txtVw_num6 = (TextView) v.findViewById(R.id.add_edt_asset_num6_txtvw);
		txtVw_cnt1 = (TextView) v.findViewById(R.id.add_edt_asset_cnt1_txtvw);
		txtVw_cnt2 = (TextView) v.findViewById(R.id.add_edt_asset_cnt2_txtvw);
		txtVw_note4 = (TextView) v.findViewById(R.id.add_edt_asset_note4_txtvw);
		txtVw_note5 = (TextView) v.findViewById(R.id.add_edt_asset_note5_txtvw);
		txtVw_note6 = (TextView) v.findViewById(R.id.add_edt_asset_note6_txtvw);
		txtVw_num3 = (TextView) v.findViewById(R.id.add_edt_asset_num3_txtvw);
		txtVw_num4 = (TextView) v.findViewById(R.id.add_edt_asset_num4_txtvw);
		txtVw_num5 = (TextView) v.findViewById(R.id.add_edt_asset_num5_txtvw);
		txtVw_note1 = (TextView) v.findViewById(R.id.add_edt_asset_note1_txtvw);
		txtVw_ct1 = (TextView) v.findViewById(R.id.add_edt_asset_ct1_txtvw);
		txtVw_ct2 = (TextView) v.findViewById(R.id.add_edt_asset_ct2_txtvw);
		txtVw_ct3 = (TextView) v.findViewById(R.id.add_edt_asset_ct3_txtvw);
		txtVw_geoCode = (TextView) v.findViewById(R.id.odr_assets_record_geocode);
		txtVw_geoCode.setOnClickListener(this);
	}

	private void setEditTexts(View v) {
		edttx_Tid = (TextView) v.findViewById(R.id.add_edt_asset_tid_edttx);
		edttx_Tid2 = (TextView) v.findViewById(R.id.add_edt_asset_tid2_edttx);
		edttx_num3 = (TextView) v.findViewById(R.id.add_edt_asset_num3_edttx);
		edttx_num4 = (TextView) v.findViewById(R.id.add_edt_asset_num4_edttx);
		edttx_num5 = (TextView) v.findViewById(R.id.add_edt_asset_num5_edttx);
		edttx_status = (TextView) v.findViewById(R.id.add_edt_asset_status_edttx);
		edttx_pid = (TextView) v.findViewById(R.id.add_edt_asset_pid_edttx);

		edttx_note2 = (EditText) v.findViewById(R.id.add_edt_asset_note2_edttx);
		edttx_note3 = (EditText) v.findViewById(R.id.add_edt_asset_note3_edttx);
		edttx_num1 = (EditText) v.findViewById(R.id.add_edt_asset_num1_edttx);
		edttx_num2 = (EditText) v.findViewById(R.id.add_edt_asset_num2_edttx);
		edttx_num6 = (EditText) v.findViewById(R.id.add_edt_asset_num6_edttx);
		edttx_cnt1 = (EditText) v.findViewById(R.id.add_edt_asset_cnt1_edttx);
		edttx_cnt2 = (EditText) v.findViewById(R.id.add_edt_asset_cnt2_edttx);
		edttx_note4 = (EditText) v.findViewById(R.id.add_edt_asset_note4_edttx);
		edttx_note5 = (EditText) v.findViewById(R.id.add_edt_asset_note5_edttx);
		edttx_note6 = (EditText) v.findViewById(R.id.add_edt_asset_note6_edttx);
		edttx_note1 = (EditText) v.findViewById(R.id.add_edt_asset_note1_edttx);
		edttx_ct1 = (EditText) v.findViewById(R.id.add_edt_asset_ct1_edttx);
		edttx_ct2 = (EditText) v.findViewById(R.id.add_edt_asset_ct2_edttx);
		edttx_ct3 = (EditText) v.findViewById(R.id.add_edt_asset_ct3_edttx);
	}

	AssetLabel astLabel;

	/**
	 * YD this function is used for deciding which element to show on the page and which are not needed to show,
	 * also setting up the listener for the edittext fields
	 * also settign up the values in the edittext which are dropdown
	 */
	private void showFields() {
		astLabel = PreferenceHandler.getAssetLabels(mActivity);
		if (astLabel!=null) {
			if (astLabel.getTid() != null  && !astLabel.getTid().equals("")) {//YD checking if this has to show or not using asset labels coming from server
				firstbox.setVisibility(View.VISIBLE);
				txtVw_Tid.setText(astLabel.getTid());
				txtVw_Tid.setVisibility(View.VISIBLE);
				edttx_Tid.setVisibility(View.VISIBLE);
				edttx_Tid.setOnClickListener(this);

				if (assetTypeList!=null) {
					if (headerText.equals("EDIT ASSET")){
						/*edttx_Tid.setText(assetTypeList.get(activeOdrAssetObject.getTid()).getName());
						edttx_Tid.setTag(assetTypeList.get(activeOdrAssetObject.getTid()).getId());
						elemtSelectedInSearchDlgCategory = assetTypeList.get(activeOdrAssetObject.getTid()).getId();*///YD 2020
					}
					else {
						Map.Entry<Long, AssetsType> entry = assetTypeList.entrySet().iterator().next();// YD getting the first element of the list_cal
						edttx_Tid.setText(entry.getValue().getName());
						edttx_Tid.setTag(entry.getValue().getId());
						elemtSelectedInSearchDlgCategory = entry.getValue().getId();
					}
				}
			}
			if (astLabel.getTid2() != null && !astLabel.getTid2().equals("")) {
				firstbox.setVisibility(View.VISIBLE);
				txtVw_Tid2.setText(astLabel.getTid2());
				txtVw_Tid2.setVisibility(View.VISIBLE);
				edttx_Tid2.setVisibility(View.VISIBLE);
				edttx_Tid2.setOnClickListener(this);

				if (siteGenTypeMapTid2!=null) {
					if (headerText.equals("EDIT ASSET")/* && siteGenTypeMapTid2.get(activeOdrAssetObject.getTid2())!=null*/){
						/*edttx_Tid2.setText(siteGenTypeMapTid2.get(activeOdrAssetObject.getTid2()).getNm());
						edttx_Tid2.setTag(siteGenTypeMapTid2.get(activeOdrAssetObject.getTid2()).getId());
						elemtSelectedInSearchDlgTid2 = siteGenTypeMapTid2.get(activeOdrAssetObject.getTid2()).getId();*/
					}
					else {//YD commenting because no prepopulated data is requrired
						/*Map.Entry<Long, SiteType> entry = siteGenTypeMapTid2.entrySet().iterator().next();// YD getting the first element of the list_cal
						edttx_Tid2.setText(entry.getValue().getNm());
						edttx_Tid2.setTag(entry.getValue().getId());*/
						edttx_Tid2.setTag("");
						elemtSelectedInSearchDlgTid2 = -1; //-1 because can be empty also
					}
				}
			}
			if (astLabel.getNote2() != null && !astLabel.getNote2().equals("")) {
				firstbox.setVisibility(View.VISIBLE);
				txtVw_note2.setText(astLabel.getNote2());
				txtVw_note2.setVisibility(View.VISIBLE);
				edttx_note2.setVisibility(View.VISIBLE);

				if (headerText.equals("EDIT ASSET")){
					//edttx_note2.setText(activeOdrAssetObject.getNote2());//YD 2020
				}
			}
			if (astLabel.getStat() != null && !astLabel.getStat().equals("")) {
				secondbox.setVisibility(View.VISIBLE);
				txtVw_status.setText(astLabel.getStat());
				txtVw_status.setVisibility(View.VISIBLE);
				edttx_status.setVisibility(View.VISIBLE);
				edttx_status.setOnClickListener(this);

				if (siteGenTypeMapStat!=null) {
					if (headerText.equals("EDIT ASSET") /*&& activeOdrAssetObject.getStatus()!= null && !activeOdrAssetObject.getStatus().equals("") && siteGenTypeMapStat.get(Long.valueOf(activeOdrAssetObject.getStatus()))!=null*/){
						/*edttx_status.setText(siteGenTypeMapStat.get(Long.valueOf(activeOdrAssetObject.getStatus())).getNm());
						edttx_status.setTag(siteGenTypeMapStat.get(Long.valueOf(activeOdrAssetObject.getStatus())).getId());*/
					}
					else {//YD commenting because no prepopulated data is requrired
						/*Map.Entry<Long, SiteType> entry = siteGenTypeMapStat.entrySet().iterator().next();// YD getting the first element of the list_cal
						edttx_status.setText(entry.getValue().getNm());
						edttx_status.setTag(entry.getValue().getId());*/
						edttx_status.setTag("");
					}
				}
			}
			if (astLabel.getPid() != null && !astLabel.getPid().equals("")) {
				secondbox.setVisibility(View.VISIBLE);
				txtVw_pid.setText(astLabel.getPid());
				txtVw_pid.setVisibility(View.VISIBLE);
				edttx_pid.setVisibility(View.VISIBLE);
				edttx_pid.setOnClickListener(this);

				if (siteGenTypeMapPid!=null) {
					if (headerText.equals("EDIT ASSET") /*&& siteGenTypeMapPid.get(activeOdrAssetObject.getPid())!= null*/){
						/*edttx_pid.setText(siteGenTypeMapPid.get(activeOdrAssetObject.getPid()).getNm());
						edttx_pid.setTag(siteGenTypeMapPid.get(activeOdrAssetObject.getPid()).getId());*///YD 2020
					}
					else {//YD commenting because no prepopulated data is requrired
						/*Map.Entry<Long, SiteType> entry = siteGenTypeMapPid.entrySet().iterator().next();// YD getting the first element of the list_cal
						edttx_pid.setText(entry.getValue().getNm());
						edttx_pid.setTag(entry.getValue().getId());*/
						edttx_pid.setTag("");
					}
				}
			}
			if (astLabel.getNote3() != null && !astLabel.getNote3().equals("")) {
				thirdbox.setVisibility(View.VISIBLE);
				txtVw_note3.setText(astLabel.getNote3());
				txtVw_note3.setVisibility(View.VISIBLE);
				edttx_note3.setVisibility(View.VISIBLE);

				if (headerText.equals("EDIT ASSET")){
					//edttx_note3.setText(activeOdrAssetObject.getNote3());//YD 2020
				}
			}
			if (astLabel.getNum1() != null && !astLabel.getNum1().equals("")) {
				thirdbox.setVisibility(View.VISIBLE);
				txtVw_num1.setText(astLabel.getNum1());
				txtVw_num1.setVisibility(View.VISIBLE);
				edttx_num1.setVisibility(View.VISIBLE);

				if (headerText.equals("EDIT ASSET")){
					/*if (activeOdrAssetObject.getNumber1()>0)
						edttx_num1.setText(""+activeOdrAssetObject.getNumber1());
					else
						edttx_num1.setText("");*///YD 2020
				}
			}
			if (astLabel.getNum2() != null && !astLabel.getNum2().equals("")) {
				thirdbox.setVisibility(View.VISIBLE);
				txtVw_num2.setText(astLabel.getNum2());
				txtVw_num2.setVisibility(View.VISIBLE);
				edttx_num2.setVisibility(View.VISIBLE);

				if (headerText.equals("EDIT ASSET")){
					/*if (activeOdrAssetObject.getNumber2()>0)
						edttx_num2.setText(""+activeOdrAssetObject.getNumber2());
					else
						edttx_num2.setText("");*///YD 2020
				}
			}
			if (astLabel.getNum6() != null && !astLabel.getNum6().equals("")) {
				thirdbox.setVisibility(View.VISIBLE);
				txtVw_num6.setText(astLabel.getNum6());
				txtVw_num6.setVisibility(View.VISIBLE);
				edttx_num6.setVisibility(View.VISIBLE);

				if (headerText.equals("EDIT ASSET")){
					/*if (activeOdrAssetObject.getNumber6()>0)
						edttx_num6.setText(""+activeOdrAssetObject.getNumber6());
					else
						edttx_num6.setText("");*///YD 2020
				}
			}
			if (astLabel.getCnt1() != null && !astLabel.getCnt1().equals("")) {
				fourthbox.setVisibility(View.VISIBLE);
				txtVw_cnt1.setText(astLabel.getCnt1());
				txtVw_cnt1.setVisibility(View.VISIBLE);
				edttx_cnt1.setVisibility(View.VISIBLE);

				if (headerText.equals("EDIT ASSET")){
					/*if (activeOdrAssetObject.getContact1()>0)
						edttx_cnt1.setText(""+activeOdrAssetObject.getContact1());
					else
						edttx_cnt1.setText("");*///YD 2020
				}
			}
			if (astLabel.getCnt2() != null && !astLabel.getCnt2().equals("")) {
				fourthbox.setVisibility(View.VISIBLE);
				txtVw_cnt2.setText(astLabel.getCnt2());
				txtVw_cnt2.setVisibility(View.VISIBLE);
				edttx_cnt2.setVisibility(View.VISIBLE);

				if (headerText.equals("EDIT ASSET")){
					/*if (activeOdrAssetObject.getContact2()>0)
						edttx_cnt2.setText(""+activeOdrAssetObject.getContact2());
					else
						edttx_cnt2.setText("");*///YD 2020
				}
			}
			if (astLabel.getNote4() != null && !astLabel.getNote4().equals("")) {
				fifthbox.setVisibility(View.VISIBLE);
				txtVw_note4.setText(astLabel.getNote4());
				txtVw_note4.setVisibility(View.VISIBLE);
				edttx_note4.setVisibility(View.VISIBLE);

				if (headerText.equals("EDIT ASSET")){
					//edttx_note4.setText(""+activeOdrAssetObject.getNote4());//YD 2020
				}
			}
			if (astLabel.getNote5() != null && !astLabel.getNote5().equals("")) {
				fifthbox.setVisibility(View.VISIBLE);
				txtVw_note5.setText(astLabel.getNote5());
				txtVw_note5.setVisibility(View.VISIBLE);
				edttx_note5.setVisibility(View.VISIBLE);

				if (headerText.equals("EDIT ASSET")){
					//edttx_note5.setText(""+activeOdrAssetObject.getNote5());//YD 2020
				}
			}
			if (astLabel.getNote6() != null && !astLabel.getNote6().equals("")) {
				fifthbox.setVisibility(View.VISIBLE);
				txtVw_note6.setText(astLabel.getNote6());
				txtVw_note6.setVisibility(View.VISIBLE);
				edttx_note6.setVisibility(View.VISIBLE);

				if (headerText.equals("EDIT ASSET")){
					//edttx_note6.setText(""+activeOdrAssetObject.getNote6());//YD 2020
				}
			}
			if (astLabel.getNum3() != null && !astLabel.getNum3().equals("")) {
				sixthbox.setVisibility(View.VISIBLE);
				txtVw_num3.setText(astLabel.getNum3());
				txtVw_num3.setVisibility(View.VISIBLE);
				edttx_num3.setVisibility(View.VISIBLE);
				edttx_num3.setOnClickListener(this);

				if (siteGenTypeMapNum3!=null) {

					if (headerText.equals("EDIT ASSET")/* && siteGenTypeMapNum3.get(activeOdrAssetObject.getNumber3())!=null*/){
						/*edttx_num3.setText(siteGenTypeMapNum3.get(activeOdrAssetObject.getNumber3()).getNm());
						edttx_num3.setTag(siteGenTypeMapNum3.get(activeOdrAssetObject.getNumber3()).getId());
						elemtSelectedInSearchDlgNum3 = siteGenTypeMapNum3.get(activeOdrAssetObject.getNumber3()).getId();*///YD 2020
					}
					else {//YD commenting because no prepopulated data is requrired
						/*Map.Entry<Long, SiteType> entry = siteGenTypeMapNum3.entrySet().iterator().next();// YD getting the first element of the list_cal
						edttx_num3.setText(entry.getValue().getNm());
						edttx_num3.setTag(entry.getValue().getId());*/
						edttx_num3.setTag("");
						elemtSelectedInSearchDlgNum3 = -1;
					}
				}
			}
			if (astLabel.getNum4() != null && !astLabel.getNum4().equals("")) {
				sixthbox.setVisibility(View.VISIBLE);
				txtVw_num4.setText(astLabel.getNum4());
				txtVw_num4.setVisibility(View.VISIBLE);
				edttx_num4.setVisibility(View.VISIBLE);
				edttx_num4.setOnClickListener(this);

				if (siteGenTypeMapNum4!=null) {
					if (headerText.equals("EDIT ASSET") /*&& siteGenTypeMapNum4.get(activeOdrAssetObject.getNumber4())!=null*/){
						/*edttx_num4.setText(siteGenTypeMapNum4.get(activeOdrAssetObject.getNumber4()).getNm());
						edttx_num4.setTag(siteGenTypeMapNum4.get(activeOdrAssetObject.getNumber4()).getId());
						elemtSelectedInSearchDlgNum4 = siteGenTypeMapNum4.get(activeOdrAssetObject.getNumber4()).getId();*///YD 2020
					}
					else {//YD commenting because no prepopulated data is requrired
						/*Map.Entry<Long, SiteType> entry = siteGenTypeMapNum4.entrySet().iterator().next();// YD getting the first element of the list_cal
						edttx_num4.setText(entry.getValue().getNm());
						edttx_num4.setTag(entry.getValue().getId());*/
						edttx_num4.setTag("");
						elemtSelectedInSearchDlgNum3 = -1;
					}
				}
			}
			if (astLabel.getNum5() != null && !astLabel.getNum5().equals("")) {
				sixthbox.setVisibility(View.VISIBLE);
				txtVw_num5.setText(astLabel.getNum5());
				txtVw_num5.setVisibility(View.VISIBLE);
				edttx_num5.setVisibility(View.VISIBLE);
				edttx_num5.setOnClickListener(this);

				if (siteGenTypeMapNum5!=null) {
					if (headerText.equals("EDIT ASSET") /*&& siteGenTypeMapNum5.get(activeOdrAssetObject.getNumber5())!=null*/){
						/*edttx_num5.setText(siteGenTypeMapNum5.get(activeOdrAssetObject.getNumber5()).getNm());
						edttx_num5.setTag(siteGenTypeMapNum5.get(activeOdrAssetObject.getNumber5()).getId());
						elemtSelectedInSearchDlgNum5 = siteGenTypeMapNum5.get(activeOdrAssetObject.getNumber5()).getId();*/
					} else {//YD commenting because no prepopulated data is requrired
						/*Map.Entry<Long, SiteType> entry = siteGenTypeMapNum5.entrySet().iterator().next();// YD getting the first element of the list_cal
						edttx_num5.setText(entry.getValue().getNm());
						edttx_num5.setTag(entry.getValue().getId());*/
						edttx_num5.setTag("");
						elemtSelectedInSearchDlgNum3 = -1;
					}
				}
			}
			if (astLabel.getNote1() != null && !astLabel.getNote1().equals("")) {
				seventhbox.setVisibility(View.VISIBLE);
				txtVw_note1.setText(astLabel.getNote1());
				txtVw_note1.setVisibility(View.VISIBLE);
				edttx_note1.setVisibility(View.VISIBLE);

				if (headerText.equals("EDIT ASSET")){
					//edttx_note1.setText(""+activeOdrAssetObject.getNote1());//YD 2020
				}
			}
			if (astLabel.getCt1() != null && !astLabel.getCt1().equals("")) {
				eightbox.setVisibility(View.VISIBLE);
				txtVw_ct1.setText(astLabel.getCt1());
				txtVw_ct1.setVisibility(View.VISIBLE);
				edttx_ct1.setVisibility(View.VISIBLE);
				if (headerText.equals("EDIT ASSET")){
					//edttx_ct1.setText(""+activeOdrAssetObject.getCt1());//YD 2020
				}
			}
			if (astLabel.getCt2() != null && !astLabel.getCt2().equals("")) {
				eightbox.setVisibility(View.VISIBLE);
				txtVw_ct2.setText(astLabel.getCt2());
				txtVw_ct2.setVisibility(View.VISIBLE);
				edttx_ct2.setVisibility(View.VISIBLE);

				if (headerText.equals("EDIT ASSET")){
					//edttx_ct2.setText(""+activeOdrAssetObject.getCt2());//YD 2020
				}
			}
			if (astLabel.getCt3() != null && !astLabel.getCt3().equals("")) {
				eightbox.setVisibility(View.VISIBLE);
				txtVw_ct3.setText(astLabel.getCt3());
				txtVw_ct3.setVisibility(View.VISIBLE);
				edttx_ct3.setVisibility(View.VISIBLE);

				if (headerText.equals("EDIT ASSET")){
                    //YD 2020 edttx_ct3.setText(""+activeOdrAssetObject.getCt3());
				}
			}
			if (headerText.equals("EDIT ASSET")){
				if (activeOdrAssetObject.getGeoLoc()!=null){
					txtVw_geoCode.setTag(activeOdrAssetObject.getGeoLoc());
					txtVw_geoCode.setText("Update Geocode");
				}
			}
		}
	}

	MySearchDialog searchDialg;
	private void showCategoryDialog()
	{
		searchDialg = new MySearchDialog(mContext, "Category", "", mCategoryArryListId,mCategoryArryList , elemtSelectedInSearchDlgCategory);

		searchDialg.setkeyListender(new MySearchDiologInterface(){
			@Override
			public void onButtonClick() {
				super.onButtonClick();
				searchDialg.cancel();
			}

			@Override
			public void onItemSelected(Long idSelected, String nameSelected) {
				super.onItemSelected(idSelected, nameSelected);
				edttx_Tid.setText(nameSelected);

				AssetsType parttypeObj = assetTypeList.get(idSelected);
				edttx_Tid.setTag(String.valueOf(parttypeObj.getId()));
				elemtSelectedInSearchDlgCategory = idSelected;
				searchDialg.cancel();

			}
		});
		searchDialg.setCanceledOnTouchOutside(true);
		searchDialg.onCreate1(null);
		searchDialg.show();

		Utilities.setDividerTitleColor(searchDialg, mheight, mActivity);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(searchDialg.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.gravity = Gravity.CENTER;
		searchDialg.getWindow().setAttributes(lp);

	}

	/*private void showCategoryDialog()
	{
		try{
			ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity) mContext, android.R.layout.select_dialog_item, mCategoryArryList);
			AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mContext);
		        builder.setCancelable(true);
			builder.setTitle("Category");
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				@SuppressLint("NewApi")
				public void onClick(DialogInterface dialog, int position) {
					edttx_Tid.setText(mCategoryArryList.get(position));

					AssetsType parttypeObj = assetTypeList.get(keys[position]);
					edttx_Tid.setTag(String.valueOf(parttypeObj.getId()));
				}
			});
					
			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			
			final AlertDialog dialog = builder.create();
			Utilities.setAlertDialogRow(dialog, mActivity);
			dialog.show();
			//dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, 1170);// YD width , height

			Utilities.setDividerTitleColor(dialog, mheight, mActivity);
				
			Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			Utilities.setDefaultFont_12(button_negative);	
			Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Utilities.setDefaultFont_12(button_positive);
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/


	private void showCommonDialog(ArrayList<Long> idsLst, ArrayList<String> nmsLst, final String type, String title, long elemtSelectedInSearchDlg)
	{
		searchDialg = new MySearchDialog(mContext,title, "", idsLst,nmsLst, elemtSelectedInSearchDlg);

		searchDialg.setkeyListender(new MySearchDiologInterface() {
			@Override
			public void onButtonClick() {
				super.onButtonClick();
				searchDialg.cancel();
			}

			@Override
			public void onItemSelected(Long idSelected, String nameSelected) {
				super.onItemSelected(idSelected, nameSelected);
				setValueInViewPopup(type, idSelected, nameSelected);
				searchDialg.cancel();

			}
		});
		searchDialg.setCanceledOnTouchOutside(true);
		searchDialg.onCreate1(null);
		searchDialg.show();

		Utilities.setDividerTitleColor(searchDialg, mheight, mActivity);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(searchDialg.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.gravity = Gravity.CENTER;
		searchDialg.getWindow().setAttributes(lp);

	}

	private void setValueInViewPopup(String type , Long idSelected, String nameSelected) {
		if (type.equals("siteTid2Lst")) {
			edttx_Tid2.setText(nameSelected);
			edttx_Tid2.setTag(idSelected);
			elemtSelectedInSearchDlgTid2 = idSelected;
		}
		if(type.equals("siteNum3Lst")){
			edttx_num3.setText(nameSelected);
			edttx_num3.setTag(idSelected);
			elemtSelectedInSearchDlgNum3 = idSelected;
		}
		if(type.equals("siteNum4Lst")){
			edttx_num4.setText(nameSelected);
			edttx_num4.setTag(idSelected);
			elemtSelectedInSearchDlgNum4 = idSelected;
		}
		if(type.equals("siteNum5Lst")){
			edttx_num5.setText(nameSelected);
			edttx_num5.setTag(idSelected);
			elemtSelectedInSearchDlgNum5 = idSelected;
		}
	}

	/**
	 * YD common dailog for the all sitetype or gentype element
	 * @param adapter : string adapter to set the value in dialog
	 * @param type    : is used for further identifing for which this dialog is created and for which edittext to change the values
	 * @param title   : Title of dialog
	 */
	private void showDialog(ArrayAdapter< String> adapter , final String type, String title) {
		try{
			AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mContext, AlertDialog.THEME_HOLO_LIGHT);
			builder.setCancelable(true);
			builder.setTitle(title);
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				@SuppressLint("NewApi")
				public void onClick(DialogInterface dialog, int position) {
					itemPosClicked = position;
					setValueInView(type);

					return;
				}
			});

			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});

			final AlertDialog dialog = builder.create();
			Utilities.setAlertDialogRow(dialog, mActivity);
			dialog.show();
			//dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, 1170);// YD width , height

			Utilities.setDividerTitleColor(dialog, mheight, mActivity);

			Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			Utilities.setDefaultFont_12(button_negative);
			/*Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Utilities.setDefaultFont_12(button_positive);*/
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * YD Use this function when need to set the data in edittext when selected from dialog element
	 * @param type
	 */
	private void setValueInView(String type) {
		if(type.equals("siteTid2Lst")){
			edttx_Tid2.setText(siteGenTypeMapTid2.get(sitekeys[itemPosClicked]).getNm());
			edttx_Tid2.setTag(siteGenTypeMapTid2.get(sitekeys[itemPosClicked]).getId());
		}
		if(type.equals("siteStatLst")){
			edttx_status.setText(siteGenTypeMapStat.get(sitekeys[itemPosClicked]).getNm());
			edttx_status.setTag(siteGenTypeMapStat.get(sitekeys[itemPosClicked]).getId());
		}
		if(type.equals("sitePidLst")){
			edttx_pid.setText(siteGenTypeMapPid.get(sitekeys[itemPosClicked]).getNm());
			edttx_pid.setTag(siteGenTypeMapPid.get(sitekeys[itemPosClicked]).getId());
		}
		if(type.equals("siteNum3Lst")){
			edttx_num3.setText(siteGenTypeMapNum3.get(sitekeys[itemPosClicked]).getNm());
			edttx_num3.setTag(siteGenTypeMapNum3.get(sitekeys[itemPosClicked]).getId());
		}
		if(type.equals("siteNum4Lst")){
			edttx_num4.setText(siteGenTypeMapNum4.get(sitekeys[itemPosClicked]).getNm());
			edttx_num4.setTag(siteGenTypeMapNum4.get(sitekeys[itemPosClicked]).getId());
		}
		if(type.equals("siteNum5Lst")){
			edttx_num5.setText(siteGenTypeMapNum5.get(sitekeys[itemPosClicked]).getNm());
			edttx_num5.setTag(siteGenTypeMapNum5.get(sitekeys[itemPosClicked]).getId());
		}
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
					if (response.getId()== SAVE_ASSET)
					{
						if (headerText.equals("ADD ASSET"))
							getAndUpdateNumberOfOrderAsset(currentOdrId);
						goBack(mActivity.SERVICE_Thread);
					}

					if (response.getId()==GETSITETYPE)
					{
						if (response.getResponseMap()!=null)
							seperateListOnTid((HashMap<Long, SiteType> )response.getResponseMap());

						mActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showFields();
							}
						});
					}
				}
				 else if(response.getStatus().equals("success")&& 
							response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
				  {
					 if (response.getId()== SAVE_ASSET)
						 goBack(mActivity.SERVICE_Thread);
				  }
			}
		
	}

	private void seperateListOnTid(HashMap<Long, SiteType> responseMap) {
		Long keys[] = responseMap.keySet().toArray(new Long[responseMap.size()]);
		for (int i=0; i<responseMap.size(); i++){
			int tid = responseMap.get(keys[i]).getTid();
			switch (tid){
				case 2:
					siteGenTypeMapTid2.put(keys[i], responseMap.get(keys[i]));
					break;
				case 3:
					siteGenTypeMapNum3.put(keys[i], responseMap.get(keys[i]));
					break;
				case 4:
					siteGenTypeMapNum4.put(keys[i], responseMap.get(keys[i]));
					break;
				case 5:
					siteGenTypeMapNum5.put(keys[i], responseMap.get(keys[i]));
					break;
				case 6:
					siteGenTypeMapStat.put(keys[i], responseMap.get(keys[i]));
					break;
				case 7:
					siteGenTypeMapPid.put(keys[i], responseMap.get(keys[i]));
					break;
			}
		}
		siteGenTypeMapTid2 = sortSiteTypeLst(siteGenTypeMapTid2);
		siteGenTypeMapNum3 = sortSiteTypeLst(siteGenTypeMapNum3);
		siteGenTypeMapNum4 = sortSiteTypeLst(siteGenTypeMapNum4);
		siteGenTypeMapNum5 = sortSiteTypeLst(siteGenTypeMapNum5);
		siteGenTypeMapStat = sortSiteTypeLst(siteGenTypeMapStat);
		siteGenTypeMapPid = sortSiteTypeLst(siteGenTypeMapPid);
	}


	// Sorting hashMap
	private static HashMap<Long, SiteType> sortSiteTypeLst(HashMap<Long, SiteType> unsortMap) {

		HashMap<Long, SiteType> sortedMap = new LinkedHashMap<Long, SiteType>();

		if (unsortMap!=null){
			// Convert Map to List
			List<Map.Entry<Long, SiteType>> list =
					new LinkedList<Map.Entry<Long, SiteType>>(unsortMap.entrySet());

			// Sort list_cal with comparator, to compare the Map values
			Collections.sort(list, new Comparator<Map.Entry<Long, SiteType>>() {
				public int compare(Map.Entry<Long, SiteType> o1,
								   Map.Entry<Long, SiteType> o2) {
					return (o1.getValue().getNm()).compareTo(o2.getValue().getNm());
				}
			});

			// Convert sorted map back to a Map
			for (Iterator<Map.Entry<Long, SiteType>> it = list.iterator(); it.hasNext();) {
				Map.Entry<Long, SiteType> entry = it.next();
				sortedMap.put(entry.getKey(), entry.getValue());
			}
		}
		return sortedMap;
	}

	private void getAndUpdateNumberOfOrderAsset(String orderId) {

		AceOfflineSyncEngine offlinesyncengine = AceOfflineSyncEngine.getAceOfflineSyncEngineInstance(mActivity);
		offlinesyncengine.fillAssetCount(this, mActivity);//YD update the asset count again

		if (mActivity instanceof BaseTabActivity) {
			Log.i( Utilities.TAG, "actFragContxt is instanceof BaseTabActivity so assest is refreshed");
			((BaseTabActivity) mActivity).refreshAssestCount();
		} else {//YD set the flag to true so that it refresh the order list_cal main page when move from one page to another.
			Log.i( Utilities.TAG, "actFragContxt does not instanceof BaseTabActivity so assest count not refreshed");
			PreferenceHandler.setAppDataChanged(mActivity, true);
		}
	}

	private void goBack(final int threadType) {

		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mActivity.popFragments(threadType);
				/*cont_webviewAddOrderPart.removeAllViews();
				webviewAddOrderPart.destroy();	*/
			}
		});
	}



	@Override
	public void headerClickListener(String callingId) {
		// TODO Auto-generated method stub
		if(callingId.equals(BaseTabActivity.HeaderDonePressed)){

			long tstamp = new Date().getTime();
			SaveAsset req = new SaveAsset();

			req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
			req.setType("post");
			req.setAction(Assets.ACTION_SAVE_ORDER_ASSETS);

			if (headerText.equals("EDIT ASSET")){
				req.setId(""+activeOdrAssetObject.getId());
			}
			else
				req.setId("0");

			req.setCustId("" + activeOrderObj.getCustomerid());
			req.setOdrId("" + activeOrderObj.getId());
			req.setTimeStmp("" + tstamp);

			if (!headerText.equals("EDIT ASSET")) {//YD checking for add asset only
				if(txtVw_geoCode.getTag() != null && !txtVw_geoCode.getTag().equals("") ){
				}
				else
					txtVw_geoCode.setTag(activeOrderObj.getCustSiteGeocode());
			}
			else {//YD in case of edit assets
				if(txtVw_geoCode.getTag() == null || txtVw_geoCode.getTag().equals("") ){
					txtVw_geoCode.setTag("");
				}
			}
			req.setGeo(txtVw_geoCode.getTag().toString());

			req.setTid("" + edttx_Tid.getTag());

			String temp;
			if ( edttx_Tid2.getTag()!= null) {
				temp = edttx_Tid2.getTag().toString();
				if (temp != null && !temp.equals(""))
					req.setTid2("" + edttx_Tid2.getTag());
				else
					req.setTid2("");
			}

			if ( edttx_status.getTag()!= null) {
				temp = edttx_status.getTag().toString();
				if (temp != null && !temp.equals(""))
					req.setStatus("" + edttx_status.getTag());
				else
					req.setStatus("");
			}

			if ( edttx_pid.getTag()!= null) {
				temp = edttx_pid.getTag().toString();
				if (temp != null && !temp.equals(""))
					req.setPriorityId("" + edttx_pid.getTag());
				else
					req.setPriorityId("");
			}

			req.setCnt1(edttx_cnt1.getText().toString());
			req.setCnt2(edttx_cnt2.getText().toString());

			if ( edttx_num1.getTag()!= null) {
				temp = edttx_num1.getText().toString();
				if (temp != null && !temp.equals(""))
					req.setNum1(edttx_num1.getText().toString());
				else
					req.setNum1("");
			}

			if ( edttx_num2.getTag()!= null) {
				temp = edttx_num2.getText().toString();
				if (temp != null && !temp.equals(""))
					req.setNum2(edttx_num2.getText().toString());
				else
					req.setNum2("");
			}

			if ( edttx_num3.getTag()!= null) {
				temp = edttx_num3.getTag().toString();
				if (temp != null && !temp.equals(""))
					req.setNum3("" + edttx_num3.getTag());
				else
					req.setNum3("");
			}

			if ( edttx_num4.getTag()!= null) {
				temp = edttx_num4.getTag().toString();
				if (temp != null && !temp.equals(""))
					req.setNum4("" + edttx_num4.getTag());
				else
					req.setNum4("");
			}

			if ( edttx_num5.getTag()!= null) {
				temp = edttx_num5.getTag().toString();
				if (temp != null && !temp.equals(""))
					req.setNum5("" + edttx_num5.getTag());
				else
					req.setNum5("");
			}

			if ( edttx_num6.getTag()!= null) {
				temp = edttx_num6.getText().toString();
				if (temp != null && !temp.equals(""))
					req.setNum6("" + edttx_num6.getText().toString());
				else
					req.setNum6("");
			}

			req.setNote1(edttx_note1.getText().toString());
			if (!edttx_note2.getText().equals(""))
				req.setNote2(edttx_note2.getText().toString());
			else if(!edttx_Tid.getTag().equals(""))
				req.setNote2(edttx_Tid.getTag().toString());
			else
				req.setNote2("Untitled");

			req.setNote3(edttx_note3.getText().toString());
			req.setNote4(edttx_note4.getText().toString());
			req.setNote5(edttx_note5.getText().toString());
			req.setNote6(edttx_note6.getText().toString());

			req.setCt1(edttx_ct1.getText().toString());
			req.setCt2(edttx_ct2.getText().toString());
			req.setCt3(edttx_ct3.getText().toString());


			Assets.saveData(req, mActivity, AddEditAssetFragment_OLD.this, SAVE_ASSET);
		}
	}

	public void hideSoftKeyboard() {
		if(mActivity.getCurrentFocus()!=null){
		InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
		}
	}
	
	// Sorting hashMap
		private static HashMap<Long, AssetsType> sortAssetCatagoryLst(HashMap<Long, AssetsType> unsortMap) {

			// Convert Map to List
			if (unsortMap != null) {
				List<Map.Entry<Long, AssetsType>> list =
						new LinkedList<Map.Entry<Long, AssetsType>>(unsortMap.entrySet());

				// Sort list_cal with comparator, to compare the Map values
				Collections.sort(list, new Comparator<Map.Entry<Long, AssetsType>>() {
					public int compare(Map.Entry<Long, AssetsType> o1,
									   Map.Entry<Long, AssetsType> o2) {
						return (o1.getValue().getName()).compareTo(o2.getValue().getName());
					}
				});

				// Convert sorted map back to a Map
				HashMap<Long, AssetsType> sortedMap = new LinkedHashMap<Long, AssetsType>();
				for (Iterator<Map.Entry<Long, AssetsType>> it = list.iterator(); it.hasNext(); ) {
					Map.Entry<Long, AssetsType> entry = it.next();
					sortedMap.put(entry.getKey(), entry.getValue());
				}

				return sortedMap;
			}
			return null;
		}

		public void loadDataOnBack(BaseTabActivity context) {
			context.registerHeader(this);
			
		}

	public void setActiveOrderObject(Order orderObj)
	{
		activeOrderObj = orderObj;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.add_edt_asset_tid_edttx:
				showCategoryDialog();
				break;

			case R.id.add_edt_asset_tid2_edttx:
				/*sitekeys = siteGenTypeMapTid2.keySet().toArray(new Long[siteGenTypeMapTid2.size()]);
				String[] list_cal = getArrListFrmMap(siteGenTypeMapTid2 ,"siteType" );// YD sending type just like this but can be reused this fuction if the hashmap is of some other type
				ArrayAdapter<String> arrAdap = new ArrayAdapter<String>(mActivity, android.R.layout.select_dialog_item, list_cal);
				showDialog(arrAdap, "siteTid2Lst", astLabel.getTid2());*/

				ArrayList<Long> idsLst = new ArrayList<Long>();
				ArrayList<String> nmsLst = new ArrayList<String>();

				for (Long keys : siteGenTypeMapTid2.keySet()){
					SiteType  siteObj = siteGenTypeMapTid2.get(keys);
					idsLst.add(siteObj.getId());
					nmsLst.add(siteObj.getNm());
				}

				showCommonDialog(idsLst, nmsLst ,"siteTid2Lst", astLabel.getTid2(), elemtSelectedInSearchDlgTid2 );
				break;

			case R.id.add_edt_asset_status_edttx:
				sitekeys = siteGenTypeMapStat.keySet().toArray(new Long[siteGenTypeMapStat.size()]);
				String[] Statlist = getArrListFrmMap(siteGenTypeMapStat ,"siteType" );
				ArrayAdapter<String> arrAdapStat = new ArrayAdapter<String>(mActivity, android.R.layout.select_dialog_item, Statlist);
				showDialog(arrAdapStat,"siteStatLst", astLabel.getStat());
				break;

			case R.id.add_edt_asset_pid_edttx:
				sitekeys = siteGenTypeMapPid.keySet().toArray(new Long[siteGenTypeMapPid.size()]);
				String[] pidlist = getArrListFrmMap(siteGenTypeMapPid ,"siteType" );
				ArrayAdapter<String> arrAdapPid = new ArrayAdapter<String>(mActivity, android.R.layout.select_dialog_item, pidlist);
				showDialog(arrAdapPid,"sitePidLst", astLabel.getPid());
				break;

			case R.id.add_edt_asset_num3_edttx:
				/*sitekeys = siteGenTypeMapNum3.keySet().toArray(new Long[siteGenTypeMapNum3.size()]);
				String[] num3list = getArrListFrmMap(siteGenTypeMapNum3 ,"siteType" );
				ArrayAdapter<String> arrAdapNum3 = new ArrayAdapter<String>(mActivity, android.R.layout.select_dialog_item, num3list);
				showDialog(arrAdapNum3,"siteNum3Lst", astLabel.getTid2());
*/
				ArrayList<Long> idsLstNum3 = new ArrayList<Long>();
				ArrayList<String> nmsLstNum3 = new ArrayList<String>();

				for (Long keys : siteGenTypeMapNum3.keySet()){
					SiteType  siteObj = siteGenTypeMapNum3.get(keys);
					idsLstNum3.add(siteObj.getId());
					nmsLstNum3.add(siteObj.getNm());
				}

				showCommonDialog(idsLstNum3, nmsLstNum3, "siteNum3Lst", astLabel.getNum3(), elemtSelectedInSearchDlgNum3);

				break;

			case R.id.add_edt_asset_num4_edttx:
				/*sitekeys = siteGenTypeMapNum4.keySet().toArray(new Long[siteGenTypeMapNum4.size()]);
				String[] num4list = getArrListFrmMap(siteGenTypeMapNum4, "siteType");
				ArrayAdapter<String> arrAdapNum4 = new ArrayAdapter<String>(mActivity, android.R.layout.select_dialog_item, num4list);
				showDialog(arrAdapNum4,"siteNum4Lst", astLabel.getTid2());*/

				ArrayList<Long> idsLstNum4 = new ArrayList<Long>();
				ArrayList<String> nmsLstNum4 = new ArrayList<String>();

				for (Long keys : siteGenTypeMapNum4.keySet()){
					SiteType  siteObj = siteGenTypeMapNum4.get(keys);
					idsLstNum4.add(siteObj.getId());
					nmsLstNum4.add(siteObj.getNm());
				}

				showCommonDialog(idsLstNum4, nmsLstNum4, "siteNum4Lst", astLabel.getNum4(), elemtSelectedInSearchDlgNum4);
				break;

			case R.id.add_edt_asset_num5_edttx:
			/*	sitekeys = siteGenTypeMapNum5.keySet().toArray(new Long[siteGenTypeMapNum5.size()]);
				String[] num5list = getArrListFrmMap(siteGenTypeMapNum5 ,"siteType" );
				ArrayAdapter<String> arrAdapNum5 = new ArrayAdapter<String>(mActivity, android.R.layout.select_dialog_item, num5list);
				showDialog(arrAdapNum5,"siteNum5Lst", astLabel.getTid2());*/

				ArrayList<Long> idsLstNum5 = new ArrayList<Long>();
				ArrayList<String> nmsLstNum5 = new ArrayList<String>();

				for (Long keys : siteGenTypeMapNum5.keySet()){
					SiteType  siteObj = siteGenTypeMapNum5.get(keys);
					idsLstNum5.add(siteObj.getId());
					nmsLstNum5.add(siteObj.getNm());
				}

				showCommonDialog(idsLstNum5, nmsLstNum5, "siteNum5Lst", astLabel.getNum5(), elemtSelectedInSearchDlgNum5);
				break;

			case R.id.odr_assets_record_geocode:
				if (headerText.equals("EDIT ASSET")){
					if (activeOdrAssetObject.getGeoLoc()!= null && !activeOdrAssetObject.equals(""))
					{
						showInfoDialog("Update Geocode", "Would you like to continue?");
					}
					else{
						//YD earlier geo is not present
						String currentGeoLocation = Utilities.getLocation(mContext);
						if (currentGeoLocation!= null && !currentGeoLocation.equals(""))
							txtVw_geoCode.setTag(currentGeoLocation);
						else
							txtVw_geoCode.setTag("");
					}
				}
				else {
					String currentGeoLocation = Utilities.getLocation(mContext);
					if (currentGeoLocation!= null && !currentGeoLocation.equals(""))
						txtVw_geoCode.setTag(currentGeoLocation);
					else
						txtVw_geoCode.setTag("");
				}

				break;
		}
	}

	MyDialog dialog ;
	private void showInfoDialog(String D_title , String D_desc){

		dialog = new MyDialog(mActivity, D_title, D_desc,"DELETE");
		//YD TODO CODE FOR SETTING HEIGHT OF DIALOG
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;
		dialog.getWindow().setAttributes(lp);

		dialog.setkeyListender(new MyDiologInterface() {
			@Override
			public void onPositiveClick() throws JSONException {
				txtVw_geoCode.setTag(activeOdrAssetObject.getGeoLoc());
				dialog.dismiss();
			}

			@Override
			public void onNegativeClick() {
				String currentGeoLocation = Utilities.getLocation(mContext);
				if (currentGeoLocation != null && !currentGeoLocation.equals(""))
					txtVw_geoCode.setTag(currentGeoLocation);
				else
					txtVw_geoCode.setTag("");

				dialog.dismiss();
			}
		});
		dialog.onCreate(null);
		dialog.show();
		Utilities.setDividerTitleColor(dialog, 0, mActivity);
		/*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;

		lp.gravity = Gravity.CENTER;
		dialog.getWindow().setAttributes(lp);*/

	}

	private String[] getArrListFrmMap(Object list,String type ) {

		String[] StrList;
		int i = 0;

		if (type.equals("siteType")) {
			HashMap<Long, SiteType> siteTypeList = (HashMap<Long, SiteType>) list;
			StrList = new String[siteTypeList.size()];

			for (Long key : siteTypeList.keySet()) {
				Log.i("AceRouteN", "key: " + key + " value: " + siteTypeList.get(key));

				StrList[i] = siteTypeList.get(key).getNm();
				i++;
			}
			return StrList;
		}
		return null;
	}
}