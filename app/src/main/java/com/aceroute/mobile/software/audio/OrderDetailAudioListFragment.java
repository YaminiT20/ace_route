package com.aceroute.mobile.software.audio;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
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
import com.aceroute.mobile.software.adaptor.AudioListAdapter;
import com.aceroute.mobile.software.async.IActionOKCancel;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.camera.Gridview_MainActivity;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderMedia;
import com.aceroute.mobile.software.database.DBEngine;
import com.aceroute.mobile.software.dialog.CustomDialog;
import com.aceroute.mobile.software.dialog.CustomDialog.DIALOG_TYPE;
import com.aceroute.mobile.software.fragment.BaseFragment;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.DeleteMediaRequest;
import com.aceroute.mobile.software.requests.GetFileMetaRequest;
import com.aceroute.mobile.software.utilities.OnSwipeTouchListener;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderDetailAudioListFragment extends BaseFragment implements
		IActionOKCancel , RespCBandServST, HeaderInterface{

	private SwipeListView mLstVwMaterialList;
	private AudioListAdapter mOrderAudioListListAdapter;
	OnSwipeTouchListener mOnSwipeTouchListener;
	private CustomDialog customDialog;
	private TextView mTxtVwTitle;
	private WebView webviewJobAudios;
	private File rootfile;
	private int positionLastEdited=0;
	private Order activeOrderObj;
	private int fmetaType;
	private int GETFILEMETA=1;
	private int DELETEMEDIA=2;
	
	String headerText;
	private OrderMedia[] orderAudioArr;
	private ArrayList<String> FilePathStrings;
	private ArrayList<String> FileNameStrings;
	private ArrayList<String> FileIdsList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_material_audio, null);
		mActivity.registerHeader(this);
		
		Bundle extra = this.getArguments();
		//3= audio 
		fmetaType = extra.getInt("fmetaType");
		headerText = extra.getString("HeaderText");
		BaseTabActivity.setHeaderTitle("", headerText, "");		

		getSDCardPath();
		initiViewReference(v);
		return v;
	}

	private void getSDCardPath() {
		rootfile = new File(Environment.getExternalStorageDirectory()
				+ "/Accerrouteaudio");

	}

	@Override
	public void onResume() {
		super.onResume();
		if (Gridview_MainActivity.isSyncMedia)
		{
			DBEngine.syncDataToSever(mActivity.getApplicationContext(),OrderMedia.TYPE_SAVE);
			Gridview_MainActivity.isSyncMedia = false;
		}
	}
	private void initiViewReference(View v) {
	
		mTxtVwTitle = (TextView) v.findViewById(R.id.title_txtvw);
		mTxtVwTitle.setText(getResources().getString(R.string.title_audio));
		mLstVwMaterialList = (SwipeListView) v
				.findViewById(R.id.order_detail_material_lstvw);
		webviewJobAudios = (WebView) v.findViewById(R.id.webviewJobAudios);
		webviewJobAudios.setBackgroundColor(Color.TRANSPARENT);
		webviewJobAudios.loadUrl("file:///android_asset/loading.html");
	//	webviewJobAudios.setVisibility(View.VISIBLE);
		
		mOnSwipeTouchListener = new OnSwipeTouchListener();

		doListViewSettings();
	}

	@Override
	public void onStart() {
		super.onStart();
		getFileMeta();
	}
	
	private void getFileMeta() {
		long numberOfMeta = getNumberOfOrderMeta();
		if (numberOfMeta>0)
		{
			/*{"url":"'+AceRoute.appUrl+'",'+ '"action": "getfilemeta",' +'"oid":"' +orderId+'"}*/
			GetFileMetaRequest req = new GetFileMetaRequest();
			req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(mActivity)+"/mobi");
			req.setAction(OrderMedia.ACTION_GET_MEDIA);
			req.setOid(String.valueOf(activeOrderObj.getId()));// passing orderid
			
			OrderMedia.getData(req, mActivity, this, GETFILEMETA);
		}	
		
	}
	
	private long getNumberOfOrderMeta() {
		return activeOrderObj.getCustMetaCount();
	}
	
	private void setAdapter(Object data) {
		
		HashMap<Long,OrderMedia> responseMap = (HashMap<Long,OrderMedia>)data;
		Long[] keys = null;
		if (responseMap!=null){   // MY because of null pointer exception 
			keys = responseMap.keySet().toArray(new Long[responseMap.size()]);
			orderAudioArr = new OrderMedia[responseMap.size()];
		}
		FilePathStrings = new ArrayList<String>();
		FileNameStrings = new ArrayList<String>();
		FileIdsList = new ArrayList<String>();
		
		

		if (responseMap!=null){	
			for (int i = 0; i < responseMap.size(); i++) {
				orderAudioArr[i] = responseMap.get(keys[i]);
	
				if (orderAudioArr[i].getMediatype()== fmetaType){


					FilePathStrings.add(orderAudioArr[i].getMetapath());
					FileIdsList.add(String.valueOf(orderAudioArr[i].getId()));
					if (orderAudioArr[i].getFile_desc() != null && !orderAudioArr[i].getFile_desc().trim().equals("") )
						FileNameStrings.add(orderAudioArr[i].getFile_desc());
					else
						FileNameStrings.add("Default");
				}
			}
		}	
		mOrderAudioListListAdapter = new AudioListAdapter(mActivity,FileNameStrings);
		mLstVwMaterialList.setAdapter(mOrderAudioListListAdapter);
			

	}

	private void doListViewSettings() {
		mLstVwMaterialList
				.setSwipeListViewListener(new BaseSwipeListViewListener() {
					@Override
					public void onOpened(int position, boolean toRight) {

						mLstVwMaterialList.closeAnimate(position);
						positionLastEdited=position;
					}

					@Override
					public void onClosed(int position, boolean fromRight) {
						View rowItem = Utilities.getViewOfListByPosition(position,
								mLstVwMaterialList);
						positionLastEdited=position;
						RelativeLayout backlayout = (RelativeLayout) rowItem
								.findViewById(R.id.back);
						backlayout.setBackgroundColor(getResources().getColor(
								R.color.color_white));
						TextView chat = (TextView) rowItem
								.findViewById(R.id.back_view_chat_textview);
						TextView invite = (TextView) rowItem
								.findViewById(R.id.back_view_invite_textview);
						invite.setText(getResources().getString(
								R.string.lbl_play));
						chat.setVisibility(View.INVISIBLE);
						invite.setVisibility(View.INVISIBLE);

						if (fromRight) {
							/*customDialog = CustomDialog
									.getInstance(
											mActivity,
											OrderDetailAudioListFragment.this,
											getResources().getString(
													R.string.msg_play),
											getResources().getString(
													R.string.app_name),
											DIALOG_TYPE.OK_CANCEL, 1);
							customDialog.setCancellable(false);
							customDialog.show();*/
							
							String filename = FilePathStrings.get(positionLastEdited);
							String fileid   = FileIdsList.get(positionLastEdited);
							Bundle bundle=new Bundle();
							bundle.putString("path", filename);
							bundle.putString("fileid", fileid);

							PlayAudioFragment playAudioFragment	= new PlayAudioFragment();
							playAudioFragment.setActiveOrderObj(activeOrderObj);
							playAudioFragment.setArguments(bundle);
							mActivity.pushFragments(Utilities.JOBS, playAudioFragment, true, true,BaseTabActivity.UI_Thread);
						} else {
							// openChatScreen(position);
							customDialog = CustomDialog
									.getInstance(
											mActivity,
											OrderDetailAudioListFragment.this,
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
					public void onStartOpen(int position, int action,
							boolean right) {
						positionLastEdited=position;
						View rowItem = Utilities.getViewOfListByPosition(position,
								mLstVwMaterialList);

						RelativeLayout backlayout = (RelativeLayout) rowItem
								.findViewById(R.id.back);
						TextView chat = (TextView) rowItem
								.findViewById(R.id.back_view_chat_textview);
						TextView invite = (TextView) rowItem
								.findViewById(R.id.back_view_invite_textview);
						invite.setText(getResources().getString(
								R.string.lbl_play));

						if (right) {
							backlayout.setBackgroundColor(getResources()
									.getColor(R.color.bdr_green));
							chat.setVisibility(View.GONE);
							invite.setVisibility(View.VISIBLE);

						} else {

							backlayout.setBackgroundColor(getResources()

							.getColor(R.color.color_red));
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

		mLstVwMaterialList.setSwipeMode(SwipeListView.SWIPE_MODE_DEFAULT); // there
		mLstVwMaterialList.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_NONE); // there
		mLstVwMaterialList.setSwipeActionRight(SwipeListView.SWIPE_ACTION_NONE);
		mLstVwMaterialList.setOffsetLeft(convertDpToPixel()); // left side
		mLstVwMaterialList.setOffsetRight(convertDpToPixel()); // right side
		mLstVwMaterialList.setSwipeCloseAllItemsWhenMoveList(true);
		mLstVwMaterialList.setAnimationTime(100); // Animation time
		mLstVwMaterialList.setSwipeOpenOnLongPress(false); // enable or disable
		// SwipeOpenOnLongPress
	}

	public int convertDpToPixel() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		/*
		 * float px = dp * (metrics.densityDpi / 160f); return (int) px;
		 */
		return metrics.widthPixels;
	}

	@Override
	public void onActionOk(int requestCode) {
	/*	if (requestCode == 1) {
			File list_cal[] = rootfile.listFiles();
			String filename= list_cal[positionLastEdited].getAbsolutePath();
			String filename = FilePathStrings.get(positionLastEdited);
			String fileid   = FileIdsList.get(positionLastEdited);
			Bundle bundle=new Bundle();
			bundle.putString("path", filename);
			bundle.putString("fileid", fileid);
			
			PlayAudioFragment playAudioFragment	= new PlayAudioFragment();
			playAudioFragment.setActiveOrderObj(activeOrderObj);
			playAudioFragment.setArguments(bundle);
			mActivity.pushFragments(Utilities.JOBS, playAudioFragment, true, true,BaseTabActivity.UI_Thread);
			

		} else {*/
			// code to delete audio
			/*{"url":"'+AceRoute.appUrlPost+'",'+'"type": "post",'+'"data":{"action": "'+action+'",'+
				'"oid":"'+orderId+'",'+'"id":"'+fileId+'"}}*/
			String fileid   = FileIdsList.get(positionLastEdited);
			
			DeleteMediaRequest req = new DeleteMediaRequest();
			  req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(mActivity)+"/mobi");
			  req.setType("post");
			  req.setOid(String.valueOf(activeOrderObj.getId()));
			  req.setAction(OrderMedia.ACTION_MEDIA_DELETE);
			  req.setFileId(String.valueOf(fileid));
			  
			  OrderMedia.deleteData(req, mActivity, OrderDetailAudioListFragment.this, DELETEMEDIA);
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
	public void headerClickListener(String callingId) {
		if (callingId.equals(BaseTabActivity.HeaderPlusPressed))
		{
			RecordAudioFragment recordFragment = new RecordAudioFragment();
			recordFragment.setActiveOrderObj(activeOrderObj);
			mActivity.pushFragments(Utilities.JOBS, recordFragment, true,
					true, BaseTabActivity.UI_Thread);
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
				if (response.getId()==GETFILEMETA)
				{
					final Object respMap = response.getResponseMap();
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setAdapter(respMap);
						}
					});
				}
				if (response.getId()==DELETEMEDIA)
					getFileMeta();
			}
			else if(response.getStatus().equals("success")&& 
					response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
			{
				if (response.getId()==GETFILEMETA)
				{
					final Object respMap = response.getResponseMap();
					mActivity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							setAdapter(respMap);
						}
					});
				}
			}
		}
	}
	
	public void setActiveOrderObj(Order activeOrderObj) {
		
		this.activeOrderObj = activeOrderObj;
		/*HashMap<Long, Order> order = (HashMap<Long, Order>)DataObject.ordersXmlDataStore;
		DataObject.ordersXmlDataStore= null;*/
	}

	public void loadDataOnBack(BaseTabActivity mActivity) {
		mActivity.registerHeader(this);
		BaseTabActivity.setHeaderTitle("", headerText, "");		
		getFileMeta();
		if (Gridview_MainActivity.isSyncMedia)
		{
			DBEngine.syncDataToSever(mActivity.getApplicationContext(),OrderMedia.TYPE_SAVE);
			Gridview_MainActivity.isSyncMedia = false;
		}
	}	 

}
