package com.aceroute.mobile.software.audio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.camera.Gridview_MainActivity;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderMedia;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.fragment.BaseFragment;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.SaveMediaRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;

import org.json.JSONException;

import java.io.File;

public class RecordAudioFragment extends BaseFragment implements HeaderInterface, RespCBandServST {

//	private GifView gifView;
	private TextView mAudioRecordCunter, mTxtVwStart/*, mTxtVwRightNav*/;
	private EditText mEdtVwFileName;
	CountDownTimer counterTimer;
	private ImageView mImgViewAudio;
	private WebView mWebViewAudio, webviewRecordAudios;
	private LinearLayout parentView;

	private ImageView mImgVwPlcHolder;
	File storagePath;
	
	int audioplayerState;
	protected boolean recorderEvent=false;
	private String gfilename;
	private int audioStartedOnce;
	private Order activeOrderObj;
	private int SAVEAUDIO=1;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_audio_record, null);
		mActivity.registerHeader(this);
		audioplayerState = BaseTabActivity.RECORDER_STOP_STATE;
		initiViewReference(v);

		return v;
	}

	private void initiViewReference(View v) {
		
		BaseTabActivity.setHeaderTitle("", "ADD AUDIO", "");	
		mEdtVwFileName = (EditText) v.findViewById(R.id.filename_txtvw);

		mWebViewAudio = (WebView) v.findViewById(R.id.webviewRecordAud);
		mWebViewAudio.setBackgroundColor(Color.TRANSPARENT);
		mWebViewAudio.loadUrl("file:///android_asset/movingbars.html");
		
		parentView = (LinearLayout) v.findViewById(R.id.parentView);
		webviewRecordAudios = (WebView) v.findViewById(R.id.webviewRecordAudios);
		webviewRecordAudios.setBackgroundColor(Color.TRANSPARENT);
		webviewRecordAudios.loadUrl("file:///android_asset/loading.html");
	//	webviewRecordAudios.setVisibility(View.VISIBLE);		
		
		mImgViewAudio = (ImageView) v.findViewById(R.id.imgRecordAud);
		mImgViewAudio.setVisibility(View.VISIBLE);

	//	gifView = (GifView) v.findViewById(R.id.gif_view);
	//	mImgVwPlcHolder = (ImageView) v.findViewById(R.id.img);
		mAudioRecordCunter = (TextView) v.findViewById(R.id.counter_txtvw);
		mTxtVwStart = (TextView) v.findViewById(R.id.start_stop_txtvw);
		mTxtVwStart.setTag(BaseTabActivity.RECORDER_STOP_STATE);

		parentView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideSoftKeyboard();
				return false;
			}
		});

		mTxtVwStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {

				if ((Integer) v.getTag() == BaseTabActivity.RECORDER_STOP_STATE) {
					mWebViewAudio.setVisibility(View.VISIBLE);
					mImgViewAudio.setVisibility(View.GONE);
					recorderEvent = true;
					Utilities.stop_player();
					recordAudio("aceroute_orderaudio.mp3");
					audioplayerState = BaseTabActivity.RECORDER_RECORD_STATE;
					//YD TODO start animating image of bar
					
				//	mImgVwPlcHolder.setVisibility(View.GONE);
					mTxtVwStart.setBackgroundColor(getResources().getColor(R.color.color_dark_red));
					((TextView) v).setTag(BaseTabActivity.RECORDER_RECORD_STATE);
					((TextView) v).setText(getResources().getString(R.string.lbl_stop));
					((TextView) v).setBackground(getResources().getDrawable(R.drawable.round_red_background));
					counterTimer = new CountDownTimer(60000, 1000) {

						public void onTick(long millisUntilFinished) {
							mAudioRecordCunter.setText(""
									+ (millisUntilFinished / 1000));
						}

						public void onFinish() {
							//mTxtVwRightNav.setEnabled(true);YD 
							Utilities.stop_record(mActivity.getApplicationContext());
							audioplayerState = BaseTabActivity.RECORDER_STOP_STATE;
							((TextView) v).setText(getResources().getString(R.string.lbl_start));
							((TextView) v).setTag(BaseTabActivity.RECORDER_STOP_STATE);
							((TextView) v).setBackground(getResources().getDrawable(R.drawable.rounded_green_fill_bg));
							mImgViewAudio.setVisibility(View.VISIBLE);
							mWebViewAudio.setVisibility(View.GONE);
						}
					}.start();
				} else {
					mImgViewAudio.setVisibility(View.VISIBLE);
					mWebViewAudio.setVisibility(View.GONE);
					//mTxtVwRightNav.setEnabled(true);YD 
				//	mImgVwPlcHolder.setVisibility(View.VISIBLE);
					//stopRecording();
					Utilities.stop_record(mActivity.getApplicationContext());
					//YD TODO stop the animation of bar
					mTxtVwStart.setBackgroundColor(getResources().getColor(R.color.bdr_green));
					((TextView) v).setText(getResources().getString(R.string.lbl_start));
					((TextView) v).setBackground(getResources().getDrawable(R.drawable.rounded_green_fill_bg));
					((TextView) v).setTag(BaseTabActivity.RECORDER_STOP_STATE);
					audioplayerState = BaseTabActivity.RECORDER_STOP_STATE;
					counterTimer.cancel();

				}

			}
		});

	}

	protected void recordAudio(String string) {
		//gfilename = "AceRoute"+"/"+Utilities.getCurrentTime()+".3gp"; //YD replaced Aceroute with BaseTabActivity.appBasePath because this is not the correct folder where audio should be saved
		gfilename = "AceRoute"+"/"+Utilities.getCurrentTime()+".m4a";
		String path = Utilities.getCompleteFilePathforApp(gfilename);
		gfilename =  path;
		File file = new File(path);
		if(file.exists()) {
			Log.i(Utilities.TAG, "Deleting recording file : "+gfilename);
			file.delete();
		}
		Log.i( Utilities.TAG, "Creating recording file : "+gfilename);
		Toast.makeText(getActivity(),gfilename,Toast.LENGTH_SHORT).show();
		Utilities.start_record(mActivity.getApplicationContext(), file);
	    audioStartedOnce=1;   
		
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
				if (response.getId()==SAVEAUDIO)
				{
					activeOrderObj.setCustMetaCount(activeOrderObj.getCustMetaCount()+1);
					activeOrderObj.setAudioCount(activeOrderObj.getAudioCount()+1);
					Gridview_MainActivity.isSyncMedia = true;
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mActivity.popFragments(mActivity.SERVICE_Thread);
		//					mActivity.popFragments(mActivity.SERVICE_Thread);
						}
					});
				}
			}

			else if(response.getStatus().equals("success")&& 
					response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
			{
			}
		}
		
	}

	@Override
	public void headerClickListener(String callingId) {
		if(callingId.equals(BaseTabActivity.HeaderDonePressed))
		{
			 if (audioStartedOnce==0)
			 {
				 try{
					final MyDialog dialog = new MyDialog(mActivity, getResources().getString(R.string.msg_slight_problem), getResources().getString(R.string.msg_record_audio) ,"OK");					
					dialog.setkeyListender(new MyDiologInterface() {
						@Override
						public void onPositiveClick() throws JSONException {
							dialog.dismiss();							
						}
	
						@Override
						public void onNegativeClick() {
							// TODO Auto-generated method stub
							dialog.dismiss();	
						}						
					});							
					dialog.onCreate(null);					
					dialog.show();
								
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
					lp.copyFrom(dialog.getWindow().getAttributes());
					lp.width = WindowManager.LayoutParams.MATCH_PARENT;
					lp.gravity = Gravity.CENTER;
					dialog.getWindow().setAttributes(lp);						
				}catch(Exception ex){
						ex.printStackTrace();
				}
			 }
			 else
			 {
			    audioStartedOnce=0;
			    if (audioplayerState == BaseTabActivity.RECORDER_RECORD_STATE) 
			    {
			    	Utilities.stop_record(mActivity.getApplicationContext());
			        // YD TODO stop the animation of bar
			        audioplayerState = BaseTabActivity.RECORDER_STOP_STATE; 
			    }
			    if (recorderEvent){
			    saveAudioFile();
			    }
			}
		}
		else if(callingId.equals(BaseTabActivity.HeaderBackPressed))
		{
			//YD Stop the recording if already running while moving back to other page 
			if (audioplayerState == BaseTabActivity.RECORDER_RECORD_STATE) 
		    {
		    	Utilities.stop_record(mActivity.getApplicationContext());
		        // YD TODO stop the animation of bar
		        audioplayerState = BaseTabActivity.RECORDER_STOP_STATE; 
		    }
		}
	}

	private void saveAudioFile() {
		
		String mimeType = "amr";
		String file_desc = mEdtVwFileName.getText().toString();
		file_desc=file_desc.trim();
		
		String missingDataFields = validateData1(file_desc);
		if(missingDataFields != ""){ 
			// YD TODO show error msg dialog 
		}
		String tstamp = String.valueOf(Utilities.getCurrentTime());
		
		/*{"url":"'+AceRoute.appUrlPost+'",'+'"type": "post",'+	'"data":{'+'"id":"0",'+'"oid":"'+orderId+'",'+
			'"tid":"'+file_type+'",'+'"metapath":"'+jpegImage+'",'+'"file":"'+""+'",'+'"dtl":"'+file_desc+'",'+
			'"action":"'+action+'",'+'"stmp":"'+tistamp+'",'+'"mime":"'+mimeType+'"}}*/		

		SaveMediaRequest req = new SaveMediaRequest();
		req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
		req.setType("post");
		req.setId("0");
		req.setOrderId(String.valueOf(activeOrderObj.getId()));
		req.setTid("3");
		req.setFile("");
		req.setDtl(file_desc);
		req.setAction(OrderMedia.ACTION_MEDIA_SAVE);
		req.setTimestamp(tstamp);
		req.setMime(mimeType);
		req.setMetapath(gfilename);
		
		OrderMedia.saveData(req, mActivity, RecordAudioFragment.this, SAVEAUDIO);
		if(mActivity.getCurrentFocus()!=null){
		InputMethodManager mgr = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
	    mgr.hideSoftInputFromWindow(mEdtVwFileName.getWindowToken(), 0);				
	}
	}
	
	public String validateData1(String file_desc){
		String missingData = "";
		if(file_desc != "" && file_desc.length() > 50){
			missingData = "Description cannot be more than 50 character.";
		}
		return missingData;
	}
	
	public void setActiveOrderObj(Order activeOrderObj) {
			
			this.activeOrderObj = activeOrderObj;
	}

	public void hideSoftKeyboard() {
		if(mActivity.getCurrentFocus()!=null){
			InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
		}
	}

	public void loadDataOnBack(BaseTabActivity mActivity) {
		mActivity.registerHeader(this);
		
	}

}
