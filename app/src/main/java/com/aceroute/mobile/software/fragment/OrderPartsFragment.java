package com.aceroute.mobile.software.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.adaptor.MaterialAdapter;
import com.aceroute.mobile.software.async.IActionOKCancel;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderPart;
import com.aceroute.mobile.software.component.OrderTask;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.Parts;
import com.aceroute.mobile.software.dialog.CustomDialog;
import com.aceroute.mobile.software.dialog.CustomDialog.DIALOG_TYPE;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.GetPart_Task_FormRequest;
import com.aceroute.mobile.software.requests.SavePartDataRequest;
import com.aceroute.mobile.software.requests.Save_DeletePartRequest;
import com.aceroute.mobile.software.utilities.OnSwipeTouchListener;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OrderPartsFragment extends BaseFragment implements IActionOKCancel, HeaderInterface, RespCBandServST {

    private SwipeListView mLstVwMaterialList;
    //private FrameLayout cont_webviewOrderPart;
    private MaterialAdapter mOrderMaterialListAdapter;
    OnSwipeTouchListener mOnSwipeTouchListener;
    private CustomDialog customDialog;
    String currentOdrId, currentOdrName, headerText;
    HashMap<Long, Parts> partTypeList;
    //private WebView webviewOrderPart;

    static int GET_PARTS = 1;
    static int DELETEPART = 2;

    HashMap<Long, OrderPart> orderPartListMap;
    Long[] keys;
    int positionLastEdited = -1;
    public Order activeOrderObj;

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
        View v = inflater.inflate(R.layout.activity_material, null);
        TypeFaceFont.overrideFonts(mActivity, v);
        partTypeList = (HashMap<Long, Parts>) DataObject.partTypeXmlDataStore;// should always had part type list_cal so the have acurate data flow.
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
        // TODO Auto-generated method stub
        super.onStart();
        getParts();
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

    private void getParts() {

        long numberOfParts = getNumberOfOrderParts(currentOdrId);

        if (numberOfParts >= 0) {
            //Get OrderPart req : {"url":"'+AceRoute.appUrl+'",'+'"action":"getorderpart",'+'"oid":"'+orderId+'"}
            GetPart_Task_FormRequest req = new GetPart_Task_FormRequest();
            req.setAction(OrderPart.ACTION_GET_ORDER_PART);
            req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(getActivity()) + "/mobi");
            req.setOid(currentOdrId);

            OrderPart.getData(req, mActivity, OrderPartsFragment.this, GET_PARTS);
        }
    }

    private Long getNumberOfOrderParts(String currentOdrId2) {
        HashMap<Long, Order> orderList = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
        Order odrObj = orderList.get(Long.valueOf(currentOdrId2));
        return odrObj.getCustPartCount();
    }

    private void initiViewReference(View v) {
        final Bundle mBundle = this.getArguments();
        currentOdrId = mBundle.getString("OrderId");

        headerText = PreferenceHandler.getPartHead(mActivity);//mBundle.getString("HeaderText");
        if (headerText != null && !headerText.equals(""))
            headerText = headerText/*+"S"*/;
        else
            headerText = mBundle.getString("HeaderText");// YD just for backup

        currentOdrName = mBundle.getString("OrderName");
        BaseTabActivity.setHeaderTitle("", headerText, "");

        mLstVwMaterialList = (SwipeListView) v.findViewById(R.id.order_detail_material_lstvw);
        // cont_webviewOrderPart = (FrameLayout) v.findViewById(R.id.cont_webviewOrderPart);

        // webviewOrderPart = (WebView) v.findViewById(R.id.webviewOrderPart);
        /*webviewOrderPart = new WebView(mActivity.getApplicationContext());
        webviewOrderPart.setBackgroundColor(Color.TRANSPARENT);    
        webviewOrderPart.loadUrl("file:///android_asset/loading.html");
        cont_webviewOrderPart.addView(webviewOrderPart);*/
        //	webviewOrderPart.setVisibility(View.VISIBLE);

        mOnSwipeTouchListener = new OnSwipeTouchListener();
                
		/*H_plus=(Button) v.findViewById(R.id.B_plus);
		bck=(ImageView) v.findViewById(R.id.back_bttn);
		
		H_plus.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				AddEditPartsFragment addEditMaterialFragment = new AddEditPartsFragment();
				Bundle bundle = new Bundle();
				bundle.putString("MaterialType", "ADD MATERIAL");
				bundle.putString("OrderId", mBundle.getString("OrderId"));
				addEditMaterialFragment.setArguments(bundle);				
				mActivity.pushFragments(Utilities.JOBS, addEditMaterialFragment, true, true);				
			}
		});
		
		bck.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {			
				
			}
		});/*
		

		//mOrderMaterialListAdapter = new MaterialAdapter(mActivity, coountArray);  YD

/*		mLstVwMaterialList.setOnTouchListener(mOnSwipeTouchListener);
		mLstVwMaterialList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (mOnSwipeTouchListener.swipeDetected()) {
					if (mOnSwipeTouchListener.getAction() == Action.RL) {

						customDialog = CustomDialog.getInstance(mActivity,
								OrderDetailMaterialFragment.this,
								getResources().getString(R.string.msg_delete),
								getResources().getString(R.string.app_name),
								DIALOG_TYPE.OK_CANCEL, 1);
						CustomDialog.setCancellable(false);
						customDialog.show();

						Utils.showToastMessage(mActivity, "right");

					} else if (mOnSwipeTouchListener.getAction() == Action.LR) {

						customDialog = CustomDialog.getInstance(mActivity,
								OrderDetailMaterialFragment.this,
								getResources().getString(R.string.msg_edit),
								getResources().getString(R.string.app_name),
								DIALOG_TYPE.OK_CANCEL, 2);
						CustomDialog.setCancellable(false);
						customDialog.show();

						Utils.showToastMessage(mActivity, "left");
					}
				}

			}
		});*/
		
		/*mLstVwMaterialList.setAdapter(mOrderMaterialListAdapter);
		doListViewSettings();	*///YD
    }


    private void doListViewSettings() {
        mLstVwMaterialList
                .setSwipeListViewListener(new BaseSwipeListViewListener() {
                    @Override
                    public void onOpened(int position, boolean toRight) {

                        mLstVwMaterialList.closeAnimate(position);
                    }

                    @Override
                    public void onClosed(int position, boolean fromRight) {
                        View rowItem = Utilities.getViewOfListByPosition(
                                position, mLstVwMaterialList);
                        positionLastEdited = position;
                        RelativeLayout backlayout = (RelativeLayout) rowItem
                                .findViewById(R.id.back);
                        backlayout.setBackgroundColor(getResources().getColor(
                                R.color.color_white));
                        TextView chat = (TextView) rowItem
                                .findViewById(R.id.back_view_chat_textview);
                        TextView invite = (TextView) rowItem
                                .findViewById(R.id.back_view_invite_textview);
                        chat.setVisibility(View.INVISIBLE);
                        invite.setVisibility(View.INVISIBLE);

                        if (fromRight) {// code to open custom dialog YD
							 /*customDialog = CustomDialog.getInstance(
										mActivity,
										OrderPartsFragment.this,
										getResources().getString(
												R.string.msg_edit),
										getResources().getString(
												R.string.app_name),
										DIALOG_TYPE.OK_CANCEL, 1);
								customDialog.setCancellable(false);
								customDialog.show();*/
                            // use array list_cal for data and notify the list_cal
                            long partTypeId = -1, orderPartId = 0, orderId = 0, orderUpdatedTime = 0;
                            String partQuant = null;
                            String partbarcode = null;

                            if (positionLastEdited != -1) {
                                OrderPart odrPrtObj = orderPartListMap.get(keys[positionLastEdited]);
                                orderPartId = odrPrtObj.getOrder_part_id();
                                partTypeId = odrPrtObj.getPart_type_id();
                                partQuant = odrPrtObj.getOrder_part_QTY();
                                partbarcode = odrPrtObj.getPart_barcode();
                            }

                            AddEditPartsFragment addEditMaterialFragment = new AddEditPartsFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("MaterialType", "EDIT PART");
                            bundle.putString("OrderId", currentOdrId);
                            bundle.putString("OrderName", currentOdrName);

                            bundle.putLong("partTypeId", partTypeId);
                            bundle.putLong("orderPartId", orderPartId);
                            bundle.putString("partQuantity", partQuant);
                            bundle.putString("partBarcode", partbarcode);

                            addEditMaterialFragment.setArguments(bundle);
                            mActivity.pushFragments(Utilities.JOBS, addEditMaterialFragment, true, true, BaseTabActivity.UI_Thread);
                        } else {
                            // openChatScreen(position);
                            customDialog = CustomDialog.getInstance(
                                    mActivity,
                                    OrderPartsFragment.this,
                                    getResources().getString(
                                            R.string.msg_delete),
                                    getResources().getString(
                                            R.string.msg_title),
                                    DIALOG_TYPE.OK_CANCEL, 2, mActivity);
                            customDialog.setCancellable(false);
                            customDialog.show();
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
                                position, mLstVwMaterialList);

                        RelativeLayout backlayout = (RelativeLayout) rowItem
                                .findViewById(R.id.back);
                        TextView chat = (TextView) rowItem
                                .findViewById(R.id.back_view_chat_textview);
                        TextView chat1 = (TextView) rowItem
                                .findViewById(R.id.back_view_dummy1);

                        TextView invite = (TextView) rowItem
                                .findViewById(R.id.back_view_invite_textview);
                        TextView invite1 = (TextView) rowItem
                                .findViewById(R.id.back_view_dummy);

                        if (right) {
                            backlayout.setBackgroundColor(getResources()
                                    .getColor(R.color.bdr_green));
                            chat.setVisibility(View.GONE);
                            chat1.setVisibility(View.GONE);
                            invite.setVisibility(View.VISIBLE);
                            invite1.setVisibility(View.VISIBLE);

                        } else {

                            backlayout.setBackgroundColor(getResources()

                                    .getColor(R.color.color_red));
                            chat.setVisibility(View.VISIBLE);
                            chat1.setVisibility(View.VISIBLE);

                            invite.setVisibility(View.GONE);
                            invite1.setVisibility(View.GONE);
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

        //mLstVwMaterialList.setSwipeMode(SwipeListView.SWIPE_MODE_DEFAULT); // there

        mLstVwMaterialList.setSwipeMode(SwipeListView.SWIPE_MODE_DEFAULT);

//		if(SplashII.wrk_tid >=4)
//			mLstVwMaterialList.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS);
//		else
        mLstVwMaterialList.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_NONE); // there

//		if(SplashII.wrk_tid >=6)
//			mLstVwMaterialList.setSwipeActionRight(SwipeListView.SWIPE_ACTION_DISMISS);
//		else
        mLstVwMaterialList.setSwipeActionRight(SwipeListView.SWIPE_ACTION_NONE);

        mLstVwMaterialList.setOffsetLeft(convertDpToPixel(mLstVwMaterialList.getContext())); // left side offset
        mLstVwMaterialList.setOffsetRight(convertDpToPixel(mLstVwMaterialList.getContext())); // right side
        // offset
        mLstVwMaterialList.setSwipeCloseAllItemsWhenMoveList(true);
        mLstVwMaterialList.setAnimationTime(100); // Animation time
        mLstVwMaterialList.setSwipeOpenOnLongPress(false); // enable or disable
        // SwipeOpenOnLongPress
    }


    public int convertDpToPixel(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        /*
         * float px = dp * (metrics.densityDpi / 160f); return (int) px;
         */
        return metrics.widthPixels;
    }


    // called when the pop is clicked
    @Override
    public void onActionOk(int requestCode) {
		/*if (requestCode == 1) {
			// use array list_cal for data and notify the list_cal
			long partTypeId=-1, orderPartId = 0, orderId = 0, orderUpdatedTime = 0;
			String partQuant=null;
			
			var str = '{"url":"'+"https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi"+'",'+'"type": "post",'+'"action": "'saveorderpart'",'+
			'"data":{"oid": "'+orderId+'",'+'"id": "'0'",'+'"tid": "'+partTypeId+'",'+'"qty": "'+partQtyAdd+'",'+
	        '"stmp": "'+tstamp+'",'+'"action": "'saveorderpart'"}}';
			if (positionLastEdited!=-1)
			{
				OrderPart odrPrtObj = orderAssetsListMap.get(keys[positionLastEdited]);
			
				//orderId = odrPrtObj.getOid();
				//orderUpdatedTime = odrPrtObj.getUpd_time(); 
				orderPartId = odrPrtObj.getOrder_part_id();
				
				partTypeId = odrPrtObj.getPart_type_id();
				partQuant = odrPrtObj.getOrder_part_QTY();
			}
			
			AddEditPartsFragment addEditMaterialFragment = new AddEditPartsFragment();
			Bundle bundle = new Bundle();
			bundle.putString("MaterialType", "EDIT MATERIAL");
			bundle.putString("OrderId", currentOdrId);
			bundle.putString("OrderName", currentOdrName);		
			
			bundle.putLong("partTypeId",partTypeId );
			bundle.putLong("orderPartId", orderPartId);				
			bundle.putString("partQuantity",partQuant );

			addEditMaterialFragment.setArguments(bundle);
			mActivity.pushFragments(Utilities.JOBS, addEditMaterialFragment, true, true,BaseTabActivity.UI_Thread);
			
		} else {*/
			/*coountArray = coountArray - 1;
			//mOrderMaterialListAdapter.notifyDataSetChanged();
			mOrderMaterialListAdapter = new MaterialAdapter(mActivity, coountArray);
			mLstVwMaterialList.setAdapter(mOrderMaterialListAdapter);*/

        if (positionLastEdited != -1) {
				/*{"url":"'+AceRoute.appUrl+'",'+'"type": "post",'+'"data":{"id": "'+orderPartId+'",+
					"action": "deleteorderpart"}}
*/
            OrderPart odrPartObj = orderPartListMap.get(keys[positionLastEdited]);

            Save_DeletePartRequest delPartReqObj = new Save_DeletePartRequest();

            delPartReqObj.setAction(OrderPart.ACTION_DELETE_ORDER_PART);
            delPartReqObj.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(getActivity()) + "/mobi");
            delPartReqObj.setType("post");

            SavePartDataRequest innDelPartReq = new SavePartDataRequest();
            if (odrPartObj != null) {
                innDelPartReq.setPartId(String.valueOf(odrPartObj.getOrder_part_id()));
            }
            innDelPartReq.setAction(OrderPart.ACTION_DELETE_ORDER_PART);

            delPartReqObj.setDataObj(innDelPartReq);


            OrderPart.deleteData(delPartReqObj, mActivity, this, DELETEPART);
        }
        //	}
    }

    @Override
    public void onActionCancel(int requestCode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onActionNeutral(int requestCode) {
        // TODO Auto-generated method stub

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
        if (response != null) {
            if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED))) {
                if (response.getId() == GET_PARTS)
                    displayOrderPartList(response);
                if (response.getId() == DELETEPART) {
                    getAndUpdateNumberOfOrderParts(currentOdrId);
                }
            } else if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA))) {
                if (response.getId() == GET_PARTS)
                    displayOrderPartList(response);
            }
        }
    }

    private void displayOrderPartList(Response response) {

        if (response.getResponseMap() != null) {
            orderPartListMap = sortHashMapOnTid_TmeNew((HashMap<Long, OrderPart>) response.getResponseMap());
            DataObject.orderPartsXmlStore = orderPartListMap;
            keys = this.orderPartListMap.keySet().toArray(new Long[this.orderPartListMap.size()]);

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // notifyDataChange(response,mActivity);
                    mOrderMaterialListAdapter = new MaterialAdapter(mActivity, orderPartListMap);
                    mLstVwMaterialList.setAdapter(mOrderMaterialListAdapter);
                    doListViewSettings();
                }
            });

        } else {
            orderPartListMap = new HashMap<Long, OrderPart>();
            mOrderMaterialListAdapter = new MaterialAdapter(mActivity, orderPartListMap);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLstVwMaterialList.setAdapter(mOrderMaterialListAdapter);
                    doListViewSettings();
                }
            });

        }

    }


    private HashMap<Long, OrderPart> sortHashMapOnTid_TmeNew(HashMap<Long, OrderPart> responseMap) {

        if (responseMap != null && responseMap.size() > 1) {
            HashMap<String, ArrayList<OrderPart>> odrPartTidBasedMap = new HashMap<String, ArrayList<OrderPart>>();
            //YD first seperate the tasks based on tid
            for (Long keys : responseMap.keySet()) {
                OrderPart odrPartObj = responseMap.get(keys);

                if (odrPartTidBasedMap.get(partTypeList.get(odrPartObj.getPart_type_id()).getName()) == null)// YD if tid is not available in the hashmap (for ex: first time)
                {
                    ArrayList<OrderPart> arrlstOdrTask = new ArrayList<OrderPart>();
                    arrlstOdrTask.add(odrPartObj);
                    odrPartTidBasedMap.put(partTypeList.get(odrPartObj.getPart_type_id()).getName(), arrlstOdrTask);
                } else {// YD if the tid is already availabel in the hashmap then just get the list_cal and save in the old list_cal
                    ArrayList<OrderPart> arrlstOdrTask = odrPartTidBasedMap.get(partTypeList.get(odrPartObj.getPart_type_id()).getName());
                    arrlstOdrTask.add(odrPartObj);
                }
            }

            //YD  now first sort the list_cal on keys of hashmap so the have sort based on tid text
            LinkedHashMap<String, ArrayList<OrderPart>> odrPartTidBasedMapSorted = sortHashMapTidText(odrPartTidBasedMap);

            //YD now sort the data of hashmap based on time of task created
            LinkedHashMap<Long, OrderPart> finalMap = new LinkedHashMap<Long, OrderPart>();

            for (String tid : odrPartTidBasedMapSorted.keySet()) {
                ArrayList<OrderPart> arrOderPart = odrPartTidBasedMapSorted.get(tid);
                sortArrayList(arrOderPart);//YD Sorted list_cal based on reference.

                for (int i = 0; i < arrOderPart.size(); i++) {//  check first if the arroderTask is working on ref or not of ordertasktid based map
                    finalMap.put(arrOderPart.get(i).getOrder_part_id(), arrOderPart.get(i));
                }
            }
            return finalMap;
        } else
            return responseMap;
    }

    /**
     * YD This method is first making the different list_cal based on tid name in hashmap and then sorting base on time of task created
     *
     * @param responseMap
     * @return
     */
    private LinkedHashMap<String, ArrayList<OrderPart>> sortHashMapTidText(HashMap<String, ArrayList<OrderPart>> unsortMap) {

        LinkedHashMap<String, ArrayList<OrderPart>> sortedMap = new LinkedHashMap<String, ArrayList<OrderPart>>();

        if (unsortMap != null) {
            // Convert Map to List
            List<Map.Entry<String, ArrayList<OrderPart>>> list =
                    new LinkedList<Map.Entry<String, ArrayList<OrderPart>>>(unsortMap.entrySet());

            // Sort list_cal with comparator, to compare the Map values
            Collections.sort(list, new Comparator<Map.Entry<String, ArrayList<OrderPart>>>() {
                public int compare(Map.Entry<String, ArrayList<OrderPart>> o1,
                                   Map.Entry<String, ArrayList<OrderPart>> o2) {
                    return o1.getKey().compareTo(o2.getKey());
                }
            });

            // Convert sorted map back to a Map
            for (Iterator<Map.Entry<String, ArrayList<OrderPart>>> it = list.iterator(); it.hasNext(); ) {
                Map.Entry<String, ArrayList<OrderPart>> entry = it.next();
                sortedMap.put(entry.getKey(), entry.getValue());
            }
        }
        return sortedMap;
    }

    // Sorting Arraylist for status header
    public ArrayList<OrderTask> sortArrayList(List<OrderPart> unsortList) {

        // Sort list_cal with comparator, to compare the Map values
        Collections.sort(unsortList, new Comparator<OrderPart>() {
            public int compare(OrderPart o1,
                               OrderPart o2) {
                return Double.compare(o2.getUpd_time(), o1.getUpd_time());
            }
        });
        return (ArrayList) unsortList;
    }

    private void getAndUpdateNumberOfOrderParts(String orderId) {
        HashMap<Long, Order> orderMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
        Order odrObj = orderMap.get(Long.parseLong(orderId));
        odrObj.setCustPartCount(odrObj.getCustPartCount() - 1);
        getParts();
    }


    @Override
    public void headerClickListener(String callingId) {
        // TODO Auto-generated method stub
        if (callingId.equals("2")) {
            AddEditPartsFragment addEditMaterialFragment = new AddEditPartsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("MaterialType", "ADD PART");
            bundle.putString("OrderId", currentOdrId);
            bundle.putString("OrderName", currentOdrName);

            addEditMaterialFragment.setArguments(bundle);
            mActivity.pushFragments(Utilities.JOBS, addEditMaterialFragment, true, true, BaseTabActivity.UI_Thread);
        }
        if (callingId.equals(BaseTabActivity.HeaderBackPressed)) {
			/*cont_webviewOrderPart.removeAllViews();
			webviewOrderPart.destroy();		*/
        }
    }

    public void notifyDataChange(Response response, BaseTabActivity ref) {
        DataObject.orderPartsXmlStore = null;
        DataObject.orderPartsXmlStore = response.getResponseMap();

        if (response.getResponseMap() == null) {
            HashMap<Long, OrderPart> orderPartList = new HashMap<Long, OrderPart>();
            response.setResponseMap(orderPartList);
        }
        mOrderMaterialListAdapter = new MaterialAdapter(ref, response.getResponseMap());
        mLstVwMaterialList.setAdapter(mOrderMaterialListAdapter);
		
		/*odrDataMap.clear();
		odrDataMap.putAll((HashMap<Long, Order>)DataObject.ordersXmlDataStore);
		mOrderListAdapter.notifyDataSetChanged(); */

    }

    public void setActiveOrderObject(Order orderObj) {
        activeOrderObj = orderObj;
    }

    public void loadDataOnBack(BaseTabActivity mActivity) {
        mActivity.registerHeader(this);
        BaseTabActivity.setHeaderTitle("", headerText, "");
        getParts();
    }
}
	
