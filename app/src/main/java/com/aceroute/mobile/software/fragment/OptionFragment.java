package com.aceroute.mobile.software.fragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;

import com.aceroute.mobile.software.AceRouteBroadcast;
import com.aceroute.mobile.software.AceRouteService;
import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.Worker;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.MySeekBar;
import com.aceroute.mobile.software.dialog.MySeekDialogInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.network.AceRequestHandler;
import com.aceroute.mobile.software.requests.SyncRO;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;


public class OptionFragment extends BaseFragment implements RespCBandServST {
	
	protected int itemPosClicked=-1;
	private int mheight = 400;
	

	MyDialog dialog = null;
	MySeekBar seekBarDialog = null;


	public static OptionFragment optFragObj;

	public static OptionFragment getInstance(){
		if (optFragObj== null)
			optFragObj = new OptionFragment();
		return optFragObj;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_oprion, null);
		TypeFaceFont.overrideFonts(mActivity, v);
		if(!mActivity.drawer.isDrawerOpen(Gravity.RIGHT)) {
			mActivity.drawer.openDrawer(Gravity.RIGHT);
		}else{
			mActivity.drawer.closeDrawer(GravityCompat.END);
		}
		mActivity.popFragments(mActivity.UI_Thread);
		return null;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		String workerName ="";
		try {
			if (DataObject.resourceXmlDataStore != null)
				workerName = ((HashMap<Long, Worker>) DataObject.resourceXmlDataStore).get(PreferenceHandler.getResId(mActivity)).getNm();

			ArrayList<String> options = new ArrayList<String>();
			//options.add("Create Order");
			//options.add("Offline Setup");
			options.add("About");//0
			options.add("Add Order");//3
			options.add("Sync Data");
			options.add("Setup Font Size");
			options.add("Setup Offline Map");
			options.add("Setup Route Date");//2
			options.add("Logout " + workerName);//4
			//options.add("Refresh Data");//1
			//options.add("Setup Auto Sync");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, options);
		}catch (Exception e){
			e.printStackTrace();
		}
		/*OrderListMainFragment.optionRefreshState = 99;
		mActivity.popFragments(mActivity.UI_Thread);
		return;*/


		//showDialog(adapter);
	}

	
	private void showDialog(ArrayAdapter< String> adapter ) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("More");
		builder.setCancelable(false);
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@SuppressLint("NewApi")
			public void onClick(DialogInterface dialog, int position) {
				itemPosClicked = position;
				//takeAction();

				return;
			}
		});


		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mActivity.popFragments(mActivity.UI_Thread);
				dialog.dismiss();
			}
		});
		
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				mActivity.popFragments(mActivity.UI_Thread);

			}
		});
		
		final AlertDialog dialog = builder.create();
		Utilities.setAlertDialogRowOptionPopup(dialog, mActivity);
		dialog.show();

		//YD increasing the height of the popup if the resolution is high
		/*DisplayMetrics dm = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int dens=dm.densityDpi;
		if (dens> DisplayMetrics.DENSITY_XHIGH)
			mheight = 4000;*/

		Utilities.setDividerTitleColor(dialog, 700,mActivity);
		
		Button neutral_button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		Utilities.setDefaultFont_12(neutral_button);
	}

	public void takeAction(int itemPosClicked, BaseTabActivity context) {

		mActivity  = context;
		/*if (itemPosClicked==0){
			OrderListMainFragment.optionRefreshState=3;  // YD using for case of CreateNewOrder. 
			mActivity.popFragments(mActivity.UI_Thread);
		}
		else*/ /*if (itemPosClicked==0){
			OrderListMainFragment.optionRefreshState=2;  // YD using for case of offline map. 
			mActivity.popFragments(mActivity.UI_Thread);
		}
		else*/
		if (itemPosClicked==0){			//YD for about
			//OrderListMainFragment.optionRefreshState=7;
			//mActivity.popFragments(mActivity.UI_Thread);
		}
		else if (itemPosClicked==3){//yd for dynamic font size
			OrderListMainFragment.optionRefreshState=6;
		//	mActivity.popFragments(mActivity.UI_Thread);
		}
		else if (itemPosClicked==4){
			OrderListMainFragment.optionRefreshState=2;//YD opening countryList
			//mActivity.popFragments(mActivity.UI_Thread);
		}
		else if (itemPosClicked==2){  // YD using for case of sync data.
			OrderListMainFragment.versionCheckState = "SYNC";
			OrderListMainFragment.optionRefreshState=8;//YD opening countryList
			//mActivity.popFragments(mActivity.UI_Thread);


			//showRefreshConfirmationDialog();
		}/*else if(itemPosClicked==2){
			ArrayList<String> items = new ArrayList<String>();			
			items.add("Default");
			items.add("High Contrast"); 		
			ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);
			showDialogTheme(mAdapter);
		}*/
		else if(itemPosClicked==5){//YD for route date
			/*Date date;
			if (PreferenceHandler.getOdrGetDate(mActivity)==null || PreferenceHandler.getOdrGetDate(mActivity).equals(""))
				date  =  new Date(PreferenceHandler.getlastsynctime(mActivity));
			else
				date  =  new Date(PreferenceHandler.getodrLastSyncForDt(mActivity));
			int[]  datestr = Utilities.getDateFromDateObj(date);
			openDatePicker(datestr[0] , datestr[1] , datestr[2]);*/

			//OrderListMainFragment.getOdrOfDate = "2016-01-09";
		}

		else if (itemPosClicked==1){
			OrderListMainFragment.optionRefreshState=3;// YD using for case of createNewOrder.
		//	mActivity.popFragments(mActivity.UI_Thread);
		}
		/*else if (itemPosClicked==4){// for Auto Sync
				showAutoSyncDialog();
		}*/
		else if(itemPosClicked==6){ // YD for logout
			//logout();
		//	stopUser();
			OrderListMainFragment.optionRefreshState=9;
			//mActivity.popFragments(mActivity.UI_Thread);

			/*"{\"id\":"+PreferenceHandler.getResId(activity.getApplicationContext())+",
			 * \"action\":\"getres\",\"objtypeforpn\":9,\"appexit\":1,\"objecttype\":2,\"x\":\"\"}";
			 */
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

				}
			}
	}

	public void loadDataOnBack(BaseTabActivity context) {
		// TODO Auto-generated method stub

	}

	private void showNoInternetDialog(){

		/*String D_title = BaseTabActivity.getResources().getString(R.string.msg_slight_problem);
		String D_desc = getResources().getString(R.string.msg_internet_problem);*/
		dialog = new MyDialog(mActivity, "Unable to Logout", "No internet connection is available to upload updates to server. Please try again to logout when you have stable network connection","OK");
		//YD CODE FOR SETTING HEIGHT OF DIALOG
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;
		dialog.getWindow().setAttributes(lp);

		dialog.setkeyListender(new MyDiologInterface() {
			@Override
			public void onPositiveClick() throws JSONException {
				dialog.dismiss();
			}

			@Override
			public void onNegativeClick() {
				mActivity.popFragments(mActivity.UI_Thread);
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



	private void showAutoSyncDialog() {
		int progress = 0;
		long autoSyncTmeInterval = PreferenceHandler.getAutoSyncInterval(mActivity);
		if (autoSyncTmeInterval == 0)
			progress=0;
		else if (autoSyncTmeInterval == 10)
			progress=1;
		else if (autoSyncTmeInterval == 30)
			progress=2;
		else if (autoSyncTmeInterval == 60)
			progress=3;

		seekBarDialog = new MySeekBar(mActivity, getResources().getString(R.string.lbl_autosync_title), "", "SYNC_SEEK",progress);
		seekBarDialog.setkeyListender(new MySeekDialogInterface() {

			@Override
			public void onPositiveClick() throws JSONException {
				// on No button click
				mActivity.popFragments(mActivity.UI_Thread);
				seekBarDialog.dismiss();
			}

			@Override
			public void onNegativeClick() {
				// on Yes button click
				if (PreferenceHandler.getAutoSyncInterval(mActivity)!=0) {
					AlarmManager syncAlarmManager = (AlarmManager) mActivity.getApplicationContext().getSystemService(mActivity.ALARM_SERVICE);
					Intent intent = new Intent(mActivity, AceRouteBroadcast.class);
					intent.setAction(Utilities.ACE_AUTO_SYNC);
					PendingIntent syncAlarmIntent = PendingIntent.getBroadcast(mActivity, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					syncAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,Utilities.getCurrentTime(),
							PreferenceHandler.getAutoSyncInterval(mActivity)*1000*60,syncAlarmIntent);
				}
				else {
					//YD stop the autosync
					/*Intent intentstop2 = new Intent(context, AceRouteBroadcast.class);
					PendingIntent senderstop2 = PendingIntent.getBroadcast(context, 2,
							intentstop2, PendingIntent.FLAG_CANCEL_CURRENT);
					AlarmManager alarmManagerstop2 = (AlarmManager) context
							.getSystemService(Context.ALARM_SERVICE);
					alarmManagerstop2.cancel(senderstop2);*/
				}
				mActivity.popFragments(mActivity.UI_Thread);
				seekBarDialog.dismiss();
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (progress==0)
					PreferenceHandler.setAutoSyncInterval(mActivity, 0);
				if (progress==1)
					PreferenceHandler.setAutoSyncInterval(mActivity, 10);
				if (progress==2)
					PreferenceHandler.setAutoSyncInterval(mActivity, 30);
				if (progress==3)
					PreferenceHandler.setAutoSyncInterval(mActivity, 60);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
		seekBarDialog.onCreate(null);
		seekBarDialog.show();
	}


	private void syncOfflineData(SyncRO syncObj ,int reqId) {
		AceRequestHandler requestHandler=null;
		Intent intent = null;

		requestHandler = new AceRequestHandler(mActivity);
		intent = new Intent(mActivity,AceRouteService.class);

		Long currentMilli = PreferenceHandler.getPrefQueueRequestId(mActivity);

		Bundle mBundle = new Bundle();
		mBundle.putParcelable("OBJECT", syncObj);
		mBundle.putLong(AceRouteService.KEY_TIME, currentMilli);
		mBundle.putInt(AceRouteService.KEY_SYNCALL_FLAG, AceRouteService.VALUE_NOT_SYNCALL);
		mBundle.putInt(AceRouteService.FLAG_FOR_CAMERA, 0);
		mBundle.putInt(AceRouteService.KEY_ID, reqId);
		mBundle.putString(AceRouteService.KEY_ACTION, "syncalldataforoffline");

		intent.putExtras(mBundle);
        requestHandler.ServiceStarterLoc(mActivity, intent, this, currentMilli);

	}

	private void showDialogTheme(ArrayAdapter<String> adapter) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Font Size");
		builder.setCancelable(false);

		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@SuppressLint("NewApi")
			public void onClick(DialogInterface dialog, int position) {
				if (position == 0) {
					PreferenceHandler.setCurrrentFontSzForApp(mActivity, -6);
					mActivity.popFragments(mActivity.UI_Thread);
				} else if (position == 1) {
					PreferenceHandler.setCurrrentFontSzForApp(mActivity, -4);
					mActivity.popFragments(mActivity.UI_Thread);
				} else if (position == 2) {
					PreferenceHandler.setCurrrentFontSzForApp(mActivity, -2);
					mActivity.popFragments(mActivity.UI_Thread);
				} else if (position == 3) {
					PreferenceHandler.setCurrrentFontSzForApp(mActivity, 0);
					mActivity.popFragments(mActivity.UI_Thread);
				} else if (position == 4) {
					PreferenceHandler.setCurrrentFontSzForApp(mActivity, 2);
					mActivity.popFragments(mActivity.UI_Thread);
				} else if (position == 5) {
					PreferenceHandler.setCurrrentFontSzForApp(mActivity, 4);
					mActivity.popFragments(mActivity.UI_Thread);
				} else if (position == 6) {
					PreferenceHandler.setCurrrentFontSzForApp(mActivity, 6);
					mActivity.popFragments(mActivity.UI_Thread);
				}

				return;
			}
		});

		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				mActivity.popFragments(mActivity.UI_Thread);
				dialog.dismiss();
			}
		});

		final AlertDialog dialog = builder.create();
		Utilities.setAlertDialogRow(dialog, mActivity);
		dialog.show();

		Utilities.setDividerTitleColor(dialog, mheight,mActivity);

		Button neutral_button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		Utilities.setDefaultFont_12(neutral_button);
	}





}
