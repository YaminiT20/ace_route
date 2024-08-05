package com.aceroute.mobile.software.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.SplashII;
import com.aceroute.mobile.software.adaptor.AssetAdapter;
import com.aceroute.mobile.software.async.IActionOKCancel;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.reference.Assets;
import com.aceroute.mobile.software.component.reference.AssetsType;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.dialog.CustomDialog;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.CommonSevenReq;
import com.aceroute.mobile.software.requests.GetAsset;
import com.aceroute.mobile.software.utilities.OnSwipeTouchListener;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OrderAssetFragment extends BaseFragment implements IActionOKCancel, HeaderInterface, RespCBandServST {

	private SwipeListView mLstVwAssetsList;
	private AssetAdapter mOrderAssetListAdapter;
	OnSwipeTouchListener mOnSwipeTouchListener;
	private CustomDialog customDialog;
	String currentOdrId, currentOdrName , headerText;

    private HashMap<Long, SiteType> siteGenTypeMapTid_9;
    ArrayList<String> optionLst = new ArrayList<String>();
    ArrayList<SiteType> siteTypeArrayList = new ArrayList<SiteType>();
    private int mheight = 500;


    static int GET_ASSETS =1;
	static int DELETEPART=2;
	static int GETSITETYPE=3;

	LinkedHashMap<Long, Assets> orderAssetsListMap;
	Long[] keys;
	int positionLastEdited=-1;
	public Order activeOrderObj;
	private HashMap<Long, SiteType> siteAndGenTypeList;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Utilities.log(this.getActivity(), "onAttach called for OrderPartsFragment");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utilities.log(this.getActivity(), "onCreate1 called for OrderPartsFragment");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Utilities.log(this.getActivity(), "onCreateView called for OrderPartsFragment");
		mActivity.registerHeader(this);
		View v = inflater.inflate(R.layout.assets_list_frag, null);
		TypeFaceFont.overrideFonts(mActivity, v);
		initiViewReference(v);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Utilities.log(this.getActivity(), "onActivityCreated called for OrderPartsFragment");
	}

	@Override
	public void onStart() {
		Utilities.log(this.getActivity(), "onStart called for OrderPartsFragment");
		// check if the view is not null then only notify dataset change YD
		super.onStart();
		getSiteOrGenType();
	}
	@Override
	public void onResume() {
		super.onResume();
		Utilities.log(this.getActivity(), "onResume called for OrderPartsFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		Utilities.log(this.getActivity(), "onPause called for OrderPartsFragment");
	}

	@Override
	public void onStop() {
		super.onStop();
		Utilities.log(this.getActivity(), "onStop called for OrderPartsFragment");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Utilities.log(this.getActivity(), "onDestroyView called for OrderPartsFragment");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Utilities.log(this.getActivity(), "onDestroy called for OrderPartsFragment");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Utilities.log(this.getActivity(), "onDetach called for OrderPartsFragment");
	}

	private void getAsset() {

		GetAsset req = new GetAsset();
		req.setAction(Assets.ACTION_GET_ASSETS);
		req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		if (activeOrderObj.getCustomerid()>0)
			req.setCid(String.valueOf(activeOrderObj.getCustomerid()));
		else
			req.setCid("0");

		Assets.getData(req, mActivity, this, GET_ASSETS);
	}

	private void getSiteOrGenType() {
		CommonSevenReq getSiteTypeList = new CommonSevenReq();
		getSiteTypeList.setAction("getgentype");  // YD earlier getsitetype
		getSiteTypeList.setSource("localonly");
		getSiteTypeList.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");

		SiteType siteTypObj = new SiteType();
		siteTypObj.getObjectDataStore(getSiteTypeList, mActivity, this, GETSITETYPE);
	}

	private void initiViewReference(View v) {
		final Bundle mBundle = this.getArguments();
		currentOdrId = mBundle.getString("OrderId");

	/*	headerText = PreferenceHandler.getPartHead(mActivity);//mBundle.getString("HeaderText");
		if (headerText!=null && !headerText.equals(""))
			headerText = headerText+"S";
		else*/
			headerText = mBundle.getString("HeaderText");// YD just for backup

		currentOdrName = mBundle.getString("OrderName");
        BaseTabActivity.setHeaderTitle("", headerText, "");

        mLstVwAssetsList = (SwipeListView) v.findViewById(R.id.order_detail_asset_lstvw);
		mOnSwipeTouchListener = new OnSwipeTouchListener();
	}



	private void doListViewSettings() {
		mLstVwAssetsList
				.setSwipeListViewListener(new BaseSwipeListViewListener() {
					@Override
					public void onOpened(int position, boolean toRight) {

						mLstVwAssetsList.closeAnimate(position);
					}

					@Override
					public void onClosed(int position, boolean fromRight) {
						View rowItem = Utilities.getViewOfListByPosition(//YD return the view of the row
								position, mLstVwAssetsList);
						positionLastEdited = position;
						RelativeLayout backlayout = (RelativeLayout) rowItem
								.findViewById(R.id.back_asset);
						backlayout.setBackgroundColor(getResources().getColor(
								R.color.color_white));
						TextView chat = (TextView) rowItem
								.findViewById(R.id.back_view_chat_textview_asset);
						TextView invite = (TextView) rowItem
								.findViewById(R.id.back_view_invite_textview_asset);
						chat.setVisibility(View.INVISIBLE);
						invite.setVisibility(View.INVISIBLE);

						if (fromRight) {
							long AssetTypeId = -1, orderAssetId = 0, AssetQuant = 0, orderId = 0, orderUpdatedTime = 0;
							Assets odrAssetObj = null;
							if (positionLastEdited != -1) {
								odrAssetObj = orderAssetsListMap.get(keys[positionLastEdited]);
								orderAssetId = odrAssetObj.getId();

								/*AssetTypeId = odrAssetObj.getTid();
								AssetQuant = odrAssetObj.getNumber1();*///YD 2020
							}

							AddEditAssetFragment addEditAssetFragment = new AddEditAssetFragment();

							Bundle bundle = new Bundle();
                            bundle.putString("formData", odrAssetObj.getFdata());
                            bundle.putLong("fid", odrAssetObj.getFtid());
                            bundle.putString("AssetType", "EDIT ASSET");
                            bundle.putLong("AssetId", odrAssetObj.getId());
                            bundle.putString("OrderId", currentOdrId);
                            bundle.putString("OrderName", currentOdrName);
                            addEditAssetFragment.setArguments(bundle);
							//addEditAssetFragment.setAssetObject(odrAssetObj);
							addEditAssetFragment.setActiveOrderObject(activeOrderObj);
							addEditAssetFragment.setArguments(bundle);
							mActivity.pushFragments(Utilities.JOBS, addEditAssetFragment, true, true, BaseTabActivity.UI_Thread);
						}
						else {//Yd earlier using else of delete
                            long AssetTypeId = -1, orderAssetId = 0, AssetQuant = 0, orderId = 0, orderUpdatedTime = 0;
                            Assets odrAssetObj = null;
                            if (positionLastEdited != -1) {
                                odrAssetObj = orderAssetsListMap.get(keys[positionLastEdited]);
                                orderAssetId = odrAssetObj.getId();

								/*AssetTypeId = odrAssetObj.getTid();
								AssetQuant = odrAssetObj.getNumber1();*///YD 2020
                            }

                            AddEditAssetFragment addEditAssetFragment = new AddEditAssetFragment();

                            Bundle bundle = new Bundle();
                            bundle.putString("formData", odrAssetObj.getFdata());
                            bundle.putLong("fid", odrAssetObj.getFtid());
                            bundle.putString("AssetType", "EDIT ASSET");
                            bundle.putLong("AssetId", odrAssetObj.getId());
                            bundle.putString("OrderId", currentOdrId);
                            bundle.putString("OrderName", currentOdrName);
                            addEditAssetFragment.setArguments(bundle);
                            //addEditAssetFragment.setAssetObject(odrAssetObj);
                            addEditAssetFragment.setActiveOrderObject(activeOrderObj);
                            addEditAssetFragment.setArguments(bundle);
                            mActivity.pushFragments(Utilities.JOBS, addEditAssetFragment, true, true, BaseTabActivity.UI_Thread);
                        }
					}

					@Override
					public void onListChanged() {
					}

					@Override
					public void onMove(int position, float x) {
					}

					@Override
					public void onStartOpen(int position, int action,// called when swipe starts
											boolean right) {

						positionLastEdited = position;
						View rowItem = Utilities.getViewOfListByPosition(
								position, mLstVwAssetsList);//YD returning the item view from the listview data

						RelativeLayout backlayout = (RelativeLayout) rowItem
								.findViewById(R.id.back_asset);
						TextView chat = (TextView) rowItem
								.findViewById(R.id.back_view_chat_textview_asset);
						TextView chat1 = (TextView) rowItem
								.findViewById(R.id.back_asset_dummy1);
						TextView chat2 = (TextView) rowItem
								.findViewById(R.id.back_asset_dummy1_bottom);

						TextView invite = (TextView) rowItem
								.findViewById(R.id.back_view_invite_textview_asset);
						TextView invite1 = (TextView) rowItem
								.findViewById(R.id.back_asset_dummy);
						TextView invite2 = (TextView) rowItem
								.findViewById(R.id.back_asset_dummy_bottom);

						if (right) {
							backlayout.setBackgroundColor(getResources()
									.getColor(R.color.bdr_green));
							chat.setVisibility(View.GONE);
							chat1.setVisibility(View.GONE);
							chat2.setVisibility(View.GONE);

							invite.setVisibility(View.VISIBLE);
							invite1.setVisibility(View.VISIBLE);
							invite2.setVisibility(View.VISIBLE);

						} else {

							backlayout.setBackgroundColor(getResources()

									.getColor(R.color.color_red));
							chat.setVisibility(View.VISIBLE);
							chat1.setVisibility(View.VISIBLE);
							chat2.setVisibility(View.VISIBLE);

							invite.setVisibility(View.GONE);
							invite1.setVisibility(View.GONE);
							invite2.setVisibility(View.GONE);
						}

					}

					@Override
					public void onStartClose(int position, boolean right) {
					}

					@Override
					public void onClickFrontView(int position) {
					}

					@Override
					public void onClickBackView(int position) {
					}

					@Override
					public void onDismiss(int[] reverseSortedPositions) {

					}

				});


		mLstVwAssetsList.setSwipeMode(SwipeListView.SWIPE_MODE_DEFAULT);

		if(SplashII.wrk_tid >=4)
			mLstVwAssetsList.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS);
		else
			mLstVwAssetsList.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_NONE); // there

		if(SplashII.wrk_tid >=6)
			mLstVwAssetsList.setSwipeActionRight(SwipeListView.SWIPE_ACTION_DISMISS);
		else
			mLstVwAssetsList.setSwipeActionRight(SwipeListView.SWIPE_ACTION_NONE);

		mLstVwAssetsList.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_NONE); // there

		mLstVwAssetsList.setSwipeActionRight(SwipeListView.SWIPE_ACTION_NONE);
		mLstVwAssetsList.setOffsetLeft(convertDpToPixel()); // left side offset
		mLstVwAssetsList.setOffsetRight(convertDpToPixel()); // right side
																// offset
		mLstVwAssetsList.setSwipeCloseAllItemsWhenMoveList(true);
		mLstVwAssetsList.setAnimationTime(100); // Animation time
		mLstVwAssetsList.setSwipeOpenOnLongPress(false); // enable or disable
		// SwipeOpenOnLongPress
	}


	public int convertDpToPixel() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		/*
		 * float px = dp * (metrics.densityDpi / 160f); return (int) px;
		 */
		return metrics.widthPixels;
	}


	// called when the pop is clicked
	@Override
	public void onActionOk(int requestCode) {
		if (positionLastEdited!=-1)
		{
			/*Assets odrPartObj = orderAssetsListMap.get(keys[positionLastEdited]);

			Save_DeletePartRequest delPartReqObj = new Save_DeletePartRequest();

			delPartReqObj.setAction(OrderPart.ACTION_DELETE_ORDER_PART);
			delPartReqObj.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi");
			delPartReqObj.setType("post");

			SavePartDataRequest innDelPartReq = new SavePartDataRequest();
			innDelPartReq.setPartId(String.valueOf(odrPartObj.getOrder_part_id()));
			innDelPartReq.setAction(OrderPart.ACTION_DELETE_ORDER_PART);

			delPartReqObj.setDataObj(innDelPartReq);


			OrderPart.deleteData(delPartReqObj, mActivity, this, DELETEPART);*///YD no need to delete in assets
		}
	}

	@Override
	public void onActionCancel(int requestCode) {

	}

	@Override
	public void onActionNeutral(int requestCode) {

	}

	@Override
	public void ServiceStarter(RespCBandServST activity, Intent intent) {

	}

    private void setGenoptionList() {
        //siteGenTypeMapTid_10 = BaseTabActivity.siteGenTypeMapTid_10;//YD if any issue comes with data then need to get data from db and used it
        if(siteGenTypeMapTid_9 != null) {
            LinkedHashMap<Long, SiteType> sortedMap = sortSiteTypeListOnFormNames(siteGenTypeMapTid_9);
            for (Map.Entry<Long, SiteType> entry : sortedMap.entrySet()) {
                String name = entry.getValue().getNm();
                optionLst.add(name);
                siteTypeArrayList.add(entry.getValue());
            }
        }
    }

    private LinkedHashMap<Long, SiteType> sortSiteTypeListOnFormNames(HashMap<Long, SiteType> siteGenTypeMapTid_10) {

        LinkedHashMap<Long, SiteType> sortedMap = new LinkedHashMap<Long, SiteType>();

        // Convert Map to List
        List<Map.Entry<Long, SiteType>> list =
                new LinkedList<Map.Entry<Long, SiteType>>(siteGenTypeMapTid_10.entrySet());

        // Sort list_cal with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<Long, SiteType>>() {
            public int compare(Map.Entry<Long, SiteType> o1,
                               Map.Entry<Long, SiteType> o2) {
                return o1.getValue().getNm().compareTo(o2.getValue().getNm());
            }
        });

        // Convert sorted map back to a Map
        for (Iterator<Map.Entry<Long, SiteType>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<Long, SiteType> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

	@Override
	public void setResponseCallback(String response, Integer reqId) {

	}

	@Override
	public void setResponseCBActivity(Response response) {
		 if (response!=null)
			{
				if (response.getStatus().equals("success")&&
						response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED)))
				{
					if (response.getId()== GETSITETYPE) {
                        DataObject.siteTypeXmlDataStore = response.getResponseMap();
                        siteGenTypeMapTid_9 = new HashMap<Long, SiteType>();
                        siteAndGenTypeList = (HashMap<Long, SiteType>) response.getResponseMap();

                        Long keys[] = siteAndGenTypeList.keySet().toArray(new Long[siteAndGenTypeList.size()]);
                        for (int i = 0; i < siteAndGenTypeList.size(); i++) {
                            int tid = siteAndGenTypeList.get(keys[i]).getTid();
							if (tid == 9) {
								siteGenTypeMapTid_9.put(keys[i], siteAndGenTypeList.get(keys[i]));
							}
                        }
                        setGenoptionList();
						//siteAndGenTypeList = (HashMap<Long, SiteType>) response.getResponseMap(); YD 2020
						getAsset();
					}
					if (response.getId()== GET_ASSETS)
						displayAssetsList(response);
					if (response.getId()==DELETEPART){
						getAndUpdateNumberOfOrderParts(currentOdrId);
					}
				}
				 else if(response.getStatus().equals("success")&&
							response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
				  {
					 if (response.getId()== GET_ASSETS)
						 displayAssetsList(response);
				  }
			}
	}

	private void displayAssetsList(Response response) {

		if (response.getResponseMap()!=null)
		{
			HashMap<Long, Assets> localMap = (HashMap<Long, Assets>)response.getResponseMap();
			//TODO  sort  the list_cal as per arvind sir logic and then
			/*if (localMap!= null)
				orderAssetsListMap = sortAssetList(localMap);*///YD 2020
            this.orderAssetsListMap = new LinkedHashMap<>();
            this.orderAssetsListMap.putAll(localMap);
			keys = this.orderAssetsListMap.keySet().toArray(new Long[this.orderAssetsListMap.size()]);

			mOrderAssetListAdapter = new AssetAdapter(mActivity, orderAssetsListMap,siteAndGenTypeList);
			mActivity.runOnUiThread(new Runnable() {
			     @Override
			     public void run() {
			    	 mLstVwAssetsList.setAdapter(mOrderAssetListAdapter);
			    }
			});
			doListViewSettings();
		}
		else
		{
			orderAssetsListMap = new LinkedHashMap<Long , Assets>();
			mOrderAssetListAdapter = new AssetAdapter(mActivity, orderAssetsListMap, siteAndGenTypeList);
			mActivity.runOnUiThread(new Runnable() {
			     @Override
			     public void run() {
			    	 mLstVwAssetsList.setAdapter(mOrderAssetListAdapter);
			    }
			});
			doListViewSettings();
		}

	}

	HashMap<Long, AssetsType> assetTypeList = (HashMap<Long, AssetsType>) DataObject.assetsTypeXmlDataStore;//YD TODO check for null

	private LinkedHashMap<Long, Assets> sortAssetList(HashMap<Long, Assets> unSortedAssetList) {

		LinkedHashMap<String, ArrayList<Assets>> listToBeSorted = new LinkedHashMap<String, ArrayList<Assets>>();
		LinkedHashMap<Long, Assets> listForStatusEight = new LinkedHashMap<Long, Assets>();
		LinkedHashMap<Long, Assets> listForExtra = new LinkedHashMap<Long, Assets>();
		String key = "";

		for (Long keys : unSortedAssetList.keySet()) {
			Assets astObj = unSortedAssetList.get(keys);

			/*if (astObj.getStatus()!= null && astObj.getStatus().equals("8")){//YD 2020
				listForStatusEight.put(astObj.getId(), astObj);
				continue;
			}
			else if (astObj.getStatus()!= null && !astObj.getStatus().equals("") && siteAndGenTypeList.get(Long.valueOf(astObj.getStatus()))!=null){
				key = siteAndGenTypeList.get(Long.valueOf(astObj.getStatus())).getNm();
			}
			else if (astObj.getTid()>1 && assetTypeList.get(Long.valueOf(astObj.getTid()))!=null){
				key = assetTypeList.get(Long.valueOf(astObj.getTid())).getName();
			}
			else if (astObj.getNote2()!= null){
				key = astObj.getNote2();
			}
			else {*/
				listForExtra.put(astObj.getId(), astObj);
				//continue;
			//}

			if(listToBeSorted.get(key) == null){
				ArrayList<Assets> arrlstOdrTask = new ArrayList<Assets>();
				arrlstOdrTask.add(astObj);

				listToBeSorted.put(key , arrlstOdrTask);
			}
			else{
				ArrayList<Assets> arrlstOdrTask = listToBeSorted.get(key);
				arrlstOdrTask.add(astObj);
			}

		}
		//YD  now first sort the list_cal on keys of hashmap so the have sort based on tid text
		LinkedHashMap<String, ArrayList<Assets>> odrAssetsBasedMapSorted = sortHashMapTidText(listToBeSorted);

		//YD now sort the data of hashmap based on time of task created
		LinkedHashMap<Long , Assets> finalMap = new LinkedHashMap<Long, Assets>();

		for (String tid : odrAssetsBasedMapSorted.keySet()){
			ArrayList<Assets> arrOderAssets = odrAssetsBasedMapSorted.get(tid);

			for (int i=0;  i< arrOderAssets.size(); i++){//  check first if the arroderTask is working on ref or not of ordertasktid based map
				finalMap.put(arrOderAssets.get(i).getId(), arrOderAssets.get(i));
			}
		}

		listForExtra.putAll(listForStatusEight);
		finalMap.putAll(listForExtra);
		return finalMap;
	}

	/*private LinkedHashMap<Long, Assets> sortAssetList(HashMap<Long, Assets> unSortedAssetList) {
		LinkedHashMap<String, ArrayList<Assets>> listToBeSorted = new LinkedHashMap<String, ArrayList<Assets>>();
		LinkedHashMap<Long, Assets> listForStatusEight = new LinkedHashMap<Long, Assets>();
		LinkedHashMap<Long, Assets> listForExtra = new LinkedHashMap<Long, Assets>();

		//YD below code is make hasmap with key as tid, status, note2 as a name and value as a asset object
		for (Long keys : unSortedAssetList.keySet()) {
			Assets astObj = unSortedAssetList.get(keys);

			if (astObj.getStatus()!= null && astObj.getStatus().equals("8")){
				listForStatusEight.put(astObj.getId(), astObj);
			}
			else if (astObj.getTid()<1 && astObj.getStatus()== null && astObj.getNote2()== null && astObj.getUpd()<1 ){
				listForExtra.put(astObj.getId(), astObj);
			}
			else {
				if (listToBeSorted.get(assetTypeList.get(Long.valueOf(astObj.getTid())).getName()) == null
						&& (siteAndGenTypeList.get(Long.valueOf(astObj.getStatus()))==null || listToBeSorted.get(siteAndGenTypeList.get(Long.valueOf(astObj.getStatus())).getNm()) == null)
						&& listToBeSorted.get(astObj.getNote2()) == null)// YD if tid is not available in the hashmap (for ex: first time)
				{
					ArrayList<Assets> arrlstOdrTask = new ArrayList<Assets>();
					arrlstOdrTask.add(astObj);

					if (astObj.getStatus() != null && !astObj.getStatus().equals("") && siteAndGenTypeList.get(Long.valueOf(astObj.getStatus()))!=null) {
						listToBeSorted.put(siteAndGenTypeList.get(Long.valueOf(astObj.getStatus())).getNm(), arrlstOdrTask);
					} else if (astObj.getTid() > 0) {
						listToBeSorted.put(assetTypeList.get(Long.valueOf(astObj.getTid())).getName(), arrlstOdrTask);
					} else if (astObj.getNote2() != null && !astObj.getNote2().equals("")) {
						listToBeSorted.put(astObj.getNote2(), arrlstOdrTask);
					} else {
						listForExtra.put(astObj.getId(), astObj);
					}
				} else {// YD if the tid is already availabel in the hashmap then just get the list_cal and save in the old list_cal

					if (siteAndGenTypeList.get(Long.valueOf(astObj.getStatus()))!=null && listToBeSorted.get(siteAndGenTypeList.get(Long.valueOf(astObj.getStatus())).getNm()) != null) {
						ArrayList<Assets> arrlstOdrTask = listToBeSorted.get(siteAndGenTypeList.get(Long.valueOf(astObj.getStatus())).getNm());
						arrlstOdrTask.add(astObj);
					} else if( listToBeSorted.get(assetTypeList.get(Long.valueOf(astObj.getTid())).getName()) != null) {
						ArrayList<Assets> arrlstOdrTask = listToBeSorted.get(assetTypeList.get(Long.valueOf(astObj.getStatus())).getName());
						arrlstOdrTask.add(astObj);
					} else if (listToBeSorted.get(astObj.getNote2()) == null) {
						ArrayList<Assets> arrlstOdrTask = listToBeSorted.get(astObj.getNote2());
						arrlstOdrTask.add(astObj);
					} else {
						listForExtra.put(astObj.getId(), astObj);
					}
				}
			}
		}

		//YD  now first sort the list_cal on keys of hashmap so the have sort based on tid text
		LinkedHashMap<String, ArrayList<Assets>> odrAssetsBasedMapSorted = sortHashMapTidText(listToBeSorted);

		//YD now sort the data of hashmap based on time of task created
		LinkedHashMap<Long , Assets> finalMap = new LinkedHashMap<Long, Assets>();

		for (String tid : odrAssetsBasedMapSorted.keySet()){
			ArrayList<Assets> arrOderAssets = odrAssetsBasedMapSorted.get(tid);

			for (int i=0;  i< arrOderAssets.size(); i++){//  check first if the arroderTask is working on ref or not of ordertasktid based map
				finalMap.put(arrOderAssets.get(i).getId(), arrOderAssets.get(i));
			}
		}

		listForExtra.putAll(listForStatusEight);
		finalMap.putAll(listForExtra);
		return finalMap;
	}*/

	private LinkedHashMap<String,ArrayList<Assets>> sortHashMapTidText(HashMap<String, ArrayList<Assets>> unsortMap) {

		LinkedHashMap<String, ArrayList<Assets>> sortedMap = new LinkedHashMap<String, ArrayList<Assets>>();

		if (unsortMap!=null){
			// Convert Map to List
			List<Map.Entry<String, ArrayList<Assets>>> list =
					new LinkedList<Map.Entry<String, ArrayList<Assets>>>(unsortMap.entrySet());

			// Sort list_cal with comparator, to compare the Map values
			Collections.sort(list, new Comparator<Map.Entry<String, ArrayList<Assets>>>() {
				public int compare(Map.Entry<String, ArrayList<Assets>> o1,
								   Map.Entry<String, ArrayList<Assets>> o2) {
					return o1.getKey().compareTo(o2.getKey());
				}
			});

			// Convert sorted map back to a Map
			for (Iterator<Map.Entry<String, ArrayList<Assets>>> it = list.iterator(); it.hasNext();) {
				Map.Entry<String, ArrayList<Assets>> entry = it.next();
				sortedMap.put(entry.getKey(), entry.getValue());
			}
		}
		return sortedMap;
	}


	private void getAndUpdateNumberOfOrderParts(String orderId) {
		HashMap< Long , Order> orderMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
		Order odrObj = orderMap.get(Long.parseLong(orderId));
		odrObj.setCustPartCount(odrObj.getCustPartCount()-1);
		getAsset();
	}


	@Override
	public void headerClickListener(String callingId) {
		if(callingId.equals(BaseTabActivity.HeaderMapSettingPressed)){

			final PopupWindow popview = Utilities.show_Map_popupmenu(getActivity(),mActivity.headerApp.getHeight());
			View view = popview.getContentView();
			TextView menu_normal = (TextView)view.findViewById(R.id.menu_normal);
			TextView menu_satellite = (TextView)view.findViewById(R.id.menu_satellite);
			TextView menu_hybrid = (TextView)view.findViewById(R.id.menu_hybrid);
			//TextView menu_offline = (TextView)view.findViewById(R.id.menu_offline);
			TextView menu_offline_imagery = (TextView)view.findViewById(R.id.menu_offline_imagery);

			menu_offline_imagery.setText("Grid");
			menu_offline_imagery.setVisibility(View.GONE);

			menu_normal.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					popview.dismiss();
					setitemclick(1);
				}
			});
			menu_satellite.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					popview.dismiss();
					setitemclick(2);
				}
			});
			menu_hybrid.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					popview.dismiss();
					setitemclick(3);
				}
			});
			/*menu_offline.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					popview.dismiss();
					setitemclick(4);

				}
			});*/
			menu_offline_imagery.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					popview.dismiss();


				}
			});

		}
		if(callingId.equals(BaseTabActivity.HeaderPlusPressed)){
			/*AddEditAssetFragment addEditAssetFragment = new AddEditAssetFragment();
			Bundle bundle = new Bundle();
			bundle.putString("AssetType", "ADD ASSET");
			bundle.putString("OrderId", currentOdrId);
			bundle.putString("OrderName", currentOdrName);
			addEditAssetFragment.setArguments(bundle);
			addEditAssetFragment.setActiveOrderObject(activeOrderObj);
			mActivity.pushFragments(Utilities.JOBS, addEditAssetFragment, true, true,BaseTabActivity.UI_Thread);*/
            showHeaderDialog();

		}
		if (callingId.equals(BaseTabActivity.HeaderBackPressed)){
			/*cont_webviewOrderPart.removeAllViews();
			webviewOrderPart.destroy();		*/
		}

        else if(callingId.equals(BaseTabActivity.HeaderSwapButton))
        {
            GoogleMapFragment.maptype = "TreeList";
            BaseTabActivity.addedNewMap=true;

            Bundle mBundles =new Bundle();
            mBundles.putString("OrderId", currentOdrId);
            mBundles.putString("OrderName", currentOdrName);

            GoogleMapFragment GooglemapFragment = new GoogleMapFragment();
            GooglemapFragment.setArguments(mBundles);
            GooglemapFragment.setActiveOrderObject(activeOrderObj);
            mActivity.pushFragments(Utilities.JOBS, GooglemapFragment, true, true,BaseTabActivity.UI_Thread);

        }
	}

    private void showHeaderDialog() {
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity) mActivity, android.R.layout.select_dialog_item, optionLst);
            AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mActivity, AlertDialog.THEME_HOLO_LIGHT);
            builder.setCancelable(true);
            builder.setTitle(Html.fromHtml("<font color='#10c195'><b>Select Option</b></font>"));
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @SuppressLint("NewApi")
                public void onClick(DialogInterface dialog, int position) {
                    SiteType siteType = siteTypeArrayList.get(position);

                    AddEditAssetFragment addEditAssetFragment = new AddEditAssetFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("formData", siteType.getDtl());
                    bundle.putLong("fid", Long.valueOf(siteType.getId()));
                    bundle.putString("AssetType", "ADD ASSET");
                    bundle.putString("OrderId", currentOdrId);
                    bundle.putString("OrderName", currentOdrName);
                    addEditAssetFragment.setArguments(bundle);
                    addEditAssetFragment.setActiveOrderObject(activeOrderObj);
                    mActivity.pushFragments(Utilities.JOBS, addEditAssetFragment, true, true, BaseTabActivity.UI_Thread);


                }
            });
            builder.setNegativeButton(Html.fromHtml("<font color='#34495f'><b>Cancel</b></font>"), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            final AlertDialog dialog = builder.create();
            Utilities.setAlertDialogRow(dialog, mActivity);
            dialog.show();
            Utilities.setDividerTitleColor(dialog, mheight, mActivity);

            Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            Utilities.setDefaultFont_12(button_negative);
            Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Utilities.setDefaultFont_12(button_positive);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public void notifyDataChange(Response response, BaseTabActivity ref) {

		if (response.getResponseMap()==null)
		{
			HashMap<Long, Assets> orderAssetList = new HashMap<Long, Assets>();
			response.setResponseMap(orderAssetList);
		}
		mOrderAssetListAdapter = new AssetAdapter(ref, (HashMap<Long, Assets>)response.getResponseMap(),siteAndGenTypeList);
		mLstVwAssetsList.setAdapter(mOrderAssetListAdapter);

		/*odrDataMap.clear();
		odrDataMap.putAll((HashMap<Long, Order>)DataObject.ordersXmlDataStore);
		mOrderListAdapter.notifyDataSetChanged(); */

	}

	void setitemclick(int type){
		Order odrObj = activeOrderObj;

		Bundle mBundles =new Bundle();
		mBundles.putString("OrderId", String.valueOf(odrObj.getId()));
		mBundles.putString("OrderName", String.valueOf(odrObj.getNm()));

		if(Utilities.checkInternetConnection(mActivity,false)  /*&&  new SpeedTestLauncher().bindListeners()>20*/){
			GoogleMapFragment.maptype = "TreeList";
			BaseTabActivity.addedNewMap=true;

			GoogleMapFragment GooglemapFragment = new GoogleMapFragment();
			int maptype=-1;
			if(type==1){
				maptype=GoogleMap.MAP_TYPE_NORMAL;
			}
			else if(type==2) {
				maptype=GoogleMap.MAP_TYPE_SATELLITE;
			}
			else if(type==3) {
				maptype=GoogleMap.MAP_TYPE_HYBRID;
			}
			else if(type==4) {
				maptype = 0;
			}
			GooglemapFragment.set_init_Maptype(maptype);
			GooglemapFragment.setArguments(mBundles);
			GooglemapFragment.setActiveOrderObject(odrObj);
			mActivity.pushFragments(Utilities.JOBS, GooglemapFragment, true, true,BaseTabActivity.UI_Thread);
		}/*else {
			MapAllFragment.maptype = "TreeList";
			BaseTabActivity.addedNewMap=true;

			MapAllFragment mFragment = new MapAllFragment();
			mFragment.setArguments(mBundles);
			mFragment.setActiveOrderObject(odrObj);
			mActivity.pushFragments(Utilities.JOBS, mFragment, true, true,BaseTabActivity.UI_Thread);
		}*///YD 2020
	}

	public void setActiveOrderObject(Order orderObj)
	{
		activeOrderObj = orderObj;
	}

	public void loadDataOnBack(BaseTabActivity mActivity) {
		mActivity.registerHeader(this);
		BaseTabActivity.setHeaderTitle("", headerText, "");
		getAsset();
	}
}