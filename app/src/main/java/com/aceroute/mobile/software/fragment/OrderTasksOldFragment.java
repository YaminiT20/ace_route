package com.aceroute.mobile.software.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.IActionOKCancel;
import com.aceroute.mobile.software.async.RespCBandServST;
//import com.software.mobile.software.component.OrderTaskOld;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.dialog.CustomDialog;
import com.aceroute.mobile.software.dialog.CustomDialog.DIALOG_TYPE;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
//import com.software.mobile.software.requests.GetPart_TaskRequest;
/*import com.software.mobile.software.requests.SaveTaskOldDataRequest;
import com.software.mobile.software.requests.Save_DeleteTaskOldRequest;*/
import com.aceroute.mobile.software.utilities.OnSwipeTouchListener;
import com.aceroute.mobile.software.utilities.Utilities;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.util.HashMap;

public class OrderTasksOldFragment extends BaseFragment implements IActionOKCancel, HeaderInterface, RespCBandServST {

	private SwipeListView mLstVwOrderTaskList;
	OnSwipeTouchListener mOnSwipeTouchListener;
	private CustomDialog customDialog;
	String currentOdrId, currentOdrName;
	private WebView webviewOrderTaskOld;
	
	static int GET_TASKS=1;
	static int DELETETASK=2;

//	HashMap<Long, OrderTaskOld> orderTaskListMap;
	Long[] keys;
	int positionLastEdited=-1;

	private HashMap<Long, SiteType> siteAndGenTypeList;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity.registerHeader(this);
		
		View v = inflater.inflate(R.layout.activity_order_tasks_old, null);
		TypeFaceFont.overrideFonts(mActivity, v);
		initiViewReference(v);
		return v;
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
//		getTasks(); //Remove Tasks from App(Ashish)
	}

	/*private void getTasks() {
		long numberOfTasks = getNumberOfOrderTasks(currentOdrId);
		
		if (numberOfTasks>0)
		{			
			GetPart_TaskRequest  req = new GetPart_TaskRequest();
			req.setAction(OrderTaskOld.ACTION_GET_ORDER_TASK_OLD);
			req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
			req.setOid(currentOdrId);
			
			OrderTaskOld.getData(req, mActivity, this, GET_TASKS);
		}
	}
*/
//	private Long getNumberOfOrderTasks(String orderId) {
//		HashMap< Long , Order> orderList = (HashMap< Long , Order>)DataObject.ordersXmlDataStore;
//		Order odrObj = orderList.get(Long.valueOf(orderId));
//		return odrObj.getCustServiceCount();
//	}

	private void initiViewReference(View v) {
		
		Bundle mBundle = this.getArguments();
		currentOdrId = mBundle.getString("OrderId");
		currentOdrName = mBundle.getString("OrderName");
        mActivity.setHeaderTitle(String.valueOf(currentOdrId), "", String.valueOf(currentOdrName));

        mLstVwOrderTaskList = (SwipeListView) v.findViewById(R.id.order_detail_old_lstvw);
        
        webviewOrderTaskOld = (WebView) v.findViewById(R.id.webviewOrderTaskOld);
        webviewOrderTaskOld.setBackgroundColor(Color.TRANSPARENT);
        webviewOrderTaskOld.loadUrl("file:///android_asset/loading.html");
	//	webviewOrderPart.setVisibility(View.VISIBLE);
        
		mOnSwipeTouchListener = new OnSwipeTouchListener();      
	}

	

	private void doListViewSettings() {		
		mLstVwOrderTaskList.setSwipeListViewListener(new BaseSwipeListViewListener() {			
			@Override
			public void onOpened(int position, boolean toRight) {
				mLstVwOrderTaskList.closeAnimate(position);
			}

			@Override
			public void onClosed(int position, boolean fromRight) {
				View rowItem = Utilities.getViewOfListByPosition(position, mLstVwOrderTaskList);
				positionLastEdited = position;
				RelativeLayout backlayout = (RelativeLayout) rowItem.findViewById(R.id.back);
				backlayout.setBackgroundColor(getResources().getColor(R.color.color_white));
				TextView chat = (TextView) rowItem.findViewById(R.id.back_view_chat_textview);
				TextView invite = (TextView) rowItem.findViewById(R.id.back_view_invite_textview);
				chat.setVisibility(View.INVISIBLE);
				invite.setVisibility(View.INVISIBLE);

				 if (fromRight){// code to open custom dialog YD
					/*customDialog = CustomDialog.getInstance(mActivity, OrderTasksOldFragment.this, getResources().getString(
							R.string.msg_edit),getResources().getString(R.string.app_name), DIALOG_TYPE.OK_CANCEL, 1);*/
					customDialog.setCancellable(false);
					customDialog.show();
				 }				
				 else{			
					customDialog = CustomDialog.getInstance(mActivity, OrderTasksOldFragment.this, getResources().getString(
							R.string.msg_delete), getResources().getString(R.string.app_name), DIALOG_TYPE.OK_CANCEL, 2, mActivity);
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
			public void onStartOpen(int position, int action, boolean right) { // called when swipe starts				
				positionLastEdited = position;
				View rowItem = Utilities.getViewOfListByPosition(position, mLstVwOrderTaskList);
				RelativeLayout backlayout = (RelativeLayout) rowItem.findViewById(R.id.back);
				TextView chat = (TextView) rowItem.findViewById(R.id.back_view_chat_textview);
				TextView invite = (TextView) rowItem.findViewById(R.id.back_view_invite_textview);
				if (right) {
					backlayout.setBackgroundColor(getResources().getColor(R.color.bdr_green));
					chat.setVisibility(View.GONE);
					invite.setVisibility(View.VISIBLE);
				} else {
					backlayout.setBackgroundColor(getResources().getColor(R.color.color_red));
					chat.setVisibility(View.VISIBLE);
					invite.setVisibility(View.GONE);
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

		mLstVwOrderTaskList.setSwipeMode(SwipeListView.SWIPE_MODE_DEFAULT); // there

		mLstVwOrderTaskList.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_NONE); // there

		mLstVwOrderTaskList.setSwipeActionRight(SwipeListView.SWIPE_ACTION_NONE);
		mLstVwOrderTaskList.setOffsetLeft(convertDpToPixel()); // left side offset
		mLstVwOrderTaskList.setOffsetRight(convertDpToPixel()); // right side
																// offset
		mLstVwOrderTaskList.setSwipeCloseAllItemsWhenMoveList(true);
		mLstVwOrderTaskList.setAnimationTime(100); // Animation time
		mLstVwOrderTaskList.setSwipeOpenOnLongPress(false); // enable or disable	
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
		if (requestCode == 1) {
			// use array list_cal for data and notify the list_cal
			long taskTypeId=-1, orderTaskId = 0, orderId = 0, orderUpdatedTime = 0;
			String taskRate=null;
			
			/*if (positionLastEdited!=-1)
			{
				OrderTaskOld odrTskObj = orderTaskListMap.get(keys[positionLastEdited]);			
				orderTaskId = odrTskObj.getOrder_task_id();
				
				taskTypeId = odrTskObj.getTask_type_id();
				taskRate = odrTskObj.getOrder_task_RATE();
			}*/
			
			/*AddEditTaskOldFragment addEditTaskFragment = new AddEditTaskOldFragment();
			Bundle bundle = new Bundle();
			bundle.putString("TaskType", "EDIT TASK");
			bundle.putString("OrderId", currentOdrId);
			bundle.putString("OrderName", currentOdrName);		
			
			bundle.putLong("taskTypeId", taskTypeId);
			bundle.putLong("orderTaskId", orderTaskId);				
			bundle.putString("taskRate", taskRate);

			addEditTaskFragment.setArguments(bundle);
			mActivity.pushFragments(Utilities.JOBS, addEditTaskFragment, true, true, BaseTabActivity.UI_Thread);*/
			
		} else {			
			
			if (positionLastEdited!=-1)
			{				
				/*OrderTaskOld odrTaskObj = orderTaskListMap.get(keys[positionLastEdited]);
				
				Save_DeleteTaskOldRequest delTaskReqObj = new Save_DeleteTaskOldRequest();
				
				delTaskReqObj.setAction(OrderTaskOld.ACTION_DELETE_ORDER_TASK_OLD);
				delTaskReqObj.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
				delTaskReqObj.setType("post");
				
				SaveTaskOldDataRequest innDelTaskReq = new SaveTaskOldDataRequest();				
				innDelTaskReq.setTaskId(String.valueOf(odrTaskObj.getOrder_task_id()));
				innDelTaskReq.setAction(OrderTaskOld.ACTION_DELETE_ORDER_TASK_OLD);
				
				delTaskReqObj.setDataObj(innDelTaskReq);
				
				OrderTaskOld.deleteData(delTaskReqObj, mActivity, this, DELETETASK);	*/
			}
		}
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

	}

	/*@Override
	public void setResponseCBActivity(Response response) {
		 if (response!=null)
			{
				if (response.getStatus().equals("success")&& 
						response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED)))
				{
					if (response.getId()==GET_TASKS)					
						//displayOrderTaskList(response);
					if (response.getId()==DELETETASK){					
					//	getAndUpdateNumberOfOrderTasks(currentOdrId);
					}
				}
				 else if(response.getStatus().equals("success")&& 
							response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
				  {
					 if (response.getId()==GET_TASKS)
						 //displayOrderTaskList(response);
				  }
			}
	}*/

	/*private void displayOrderTaskList(Response response) {
		
		if (response.getResponseMap()!=null)
		{
			orderTaskListMap = (HashMap<Long, OrderTaskOld>)response.getResponseMap();
			DataObject.orderTasksXmlStore = orderTaskListMap;
			
			keys = this.orderTaskListMap.keySet().toArray(new Long[this.orderTaskListMap.size()]);
			
			mOrderTaskListAdapter = new OrderTaskOldAdapter(mActivity, response.getResponseMap(),siteAndGenTypeList);//YD siteAndGenTypeList should be filled with data of getgentype where tid =9
			mActivity.runOnUiThread(new Runnable() {
			     @Override
			     public void run() {
			    	 mLstVwOrderTaskList.setAdapter(mOrderTaskListAdapter);
			    }
			});
			doListViewSettings();	
		}
		else 
		{
			orderTaskListMap = new HashMap<Long, OrderTaskOld>();
			mOrderTaskListAdapter = new OrderTaskOldAdapter(mActivity, orderTaskListMap, siteAndGenTypeList);//YD siteAndGenTypeList should be filled with data of getgentype where tid =9
			mActivity.runOnUiThread(new Runnable() {
			     @Override
			     public void run() {
			    	 mLstVwOrderTaskList.setAdapter(mOrderTaskListAdapter);
			    }
			});
			doListViewSettings();	
		}		
	}

	private void getAndUpdateNumberOfOrderTasks(String orderId) {
		HashMap< Long , Order> orderMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
		Order odrObj = orderMap.get(Long.parseLong(orderId));
		odrObj.setCustServiceCount(odrObj.getCustServiceCount()-1);
		
		getTasks();
	}
*/
	
	@Override
	public void headerClickListener(String callingId) {
		// TODO Auto-generated method stub
		/*if(callingId.equals(mActivity.HeaderPlusPressed)){
			AddEditTaskOldFragment addEditTaskFragment = new AddEditTaskOldFragment();
			Bundle bundle = new Bundle();
			bundle.putString("TaskType", "ADD TASK");
			bundle.putString("OrderId", currentOdrId);
			bundle.putString("OrderName", currentOdrName);			
			addEditTaskFragment.setArguments(bundle);		
					
			mActivity.pushFragments(Utilities.JOBS, addEditTaskFragment, true, true,BaseTabActivity.UI_Thread);
		}else*/ if(callingId.equals(mActivity.HeaderMapOrderTaskPressed)){
			if(Utilities.checkInternetConnection(mActivity,false)){
	            GoogleMapFragment.maptype = "TasksList";
	            GoogleMapFragment GooglemapFragment = new GoogleMapFragment();
				mActivity.pushFragments(Utilities.JOBS, GooglemapFragment, true, true, BaseTabActivity.UI_Thread);
			}/*else {
				MapAllFragment.maptype = "TasksList";
				MapAllFragment mFragment = new MapAllFragment();
				mActivity.pushFragments(Utilities.JOBS, mFragment, true, true, BaseTabActivity.UI_Thread);
			}*///YD 2020
		}
	}

	public void loadDataOnBack(BaseTabActivity context) {
		// TODO Auto-generated method stub
		
	}
}
