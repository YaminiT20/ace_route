package com.aceroute.mobile.software.audio;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.fragment.BaseFragment;
import com.aceroute.mobile.software.utilities.Utilities;
import com.aceroute.mobile.software.volumeWidget.VerticalSeekBar;

import org.json.JSONException;

public class PlayAudioFragment extends BaseFragment implements HeaderInterface {
	private String fileId;
	private TextView mTxtVwPlayPause, mAudioRecordCunter;
	MediaPlayer mp;
	private ImageView mImgVwPlcHolder;
	private Order activeOrderObj;
	int audioplayerState;
	private String gfilename;
	private WebView mWebviewRecordPlayAud;
	private ImageView mImgRecordPlayAud;
	CountDownTimer counterTimer;
	long audioPlayTotalTime;
	//YD for volume control
	private static int seekBtnViewStat=0;
	private VerticalSeekBar volumeSeekbar = null;
	private AudioManager audioManager = null;
	SettingsContentObserver mSettingsContentObserver;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		gfilename = getArguments().getString("path");// gfilename is file path 
		fileId = getArguments().getString("fileid");
		audioplayerState =   BaseTabActivity.PLAYER_STOP_STATE;
		
		//mp = new MediaPlayer();//YD
		View v = inflater.inflate(R.layout.playaudio, null);
		initiViewReference(v);

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		 mSettingsContentObserver = new SettingsContentObserver(mActivity.getApplicationContext(),new Handler());
		mActivity.getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver );
	}

	@Override
	public void onPause() {
		super.onPause();
		if(mSettingsContentObserver!=null){
			mActivity.getApplicationContext().getContentResolver().unregisterContentObserver(mSettingsContentObserver);
		}
	}

	private void initiViewReference(View v) {

		 try {
			/*mp.setDataSource(filePath);
			 mp.prepare(); *///YD
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}/* catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//Write your location here  
*/   //YD      
	//	 mImgVwPlcHolder = (ImageView) v.findViewById(R.id.img);
		mActivity.registerHeader(this);
		BaseTabActivity.setHeaderTitle("", "PLAY AUDIO", ""); 
		mImgRecordPlayAud = (ImageView) v.findViewById(R.id.imgRecordPlayAud);
		mImgRecordPlayAud.setVisibility(View.VISIBLE);
		mWebviewRecordPlayAud = (WebView) v.findViewById(R.id.webviewRecordPlayAud);
		mWebviewRecordPlayAud.setBackgroundColor(Color.TRANSPARENT);
		mWebviewRecordPlayAud.loadUrl("file:///android_asset/movingbars.html");

		mAudioRecordCunter = (TextView) v.findViewById(R.id.counter_txtvw);
		mTxtVwPlayPause = (TextView) v.findViewById(R.id.start_stop_txtvw);
		mTxtVwPlayPause.setTag(BaseTabActivity.PLAYER_STOP_STATE);
		mTxtVwPlayPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {

				if ((Integer) mTxtVwPlayPause.getTag() == BaseTabActivity.PLAYER_STOP_STATE) {
					mTxtVwPlayPause.setTag(BaseTabActivity.PLAYER_PLAY_STATE);
					audioplayerState = BaseTabActivity.PLAYER_PLAY_STATE;
					stopAudioEdit();
					playAudioEdit("AudioEditFile/aceroute_orderaudio_edit.mp3");
					mWebviewRecordPlayAud.setVisibility(View.VISIBLE);
					mImgRecordPlayAud.setVisibility(View.GONE);
					mTxtVwPlayPause.setText("Pause");
					volumeSeekbar.setProgress(audioManager
							.getStreamVolume(AudioManager.STREAM_MUSIC));
					((TextView) v).setTag(BaseTabActivity.RECORDER_RECORD_STATE);
					((TextView) v).setText(getResources().getString(R.string.lbl_stop));
					((TextView) v).setBackground(getResources().getDrawable(R.drawable.round_red_background));
					counterTimer = new CountDownTimer(audioPlayTotalTime, 1000) {
						public void onTick(long millisUntilFinished) {
							mAudioRecordCunter.setText("" + (millisUntilFinished / 1000));
						}

						public void onFinish() {
							stopAudioEdit();
							audioplayerState = BaseTabActivity.PLAYER_STOP_STATE;
							mTxtVwPlayPause.setTag(BaseTabActivity.PLAYER_STOP_STATE);
							mTxtVwPlayPause.setBackground(getResources().getDrawable(R.drawable.rounded_green_fill_bg));
							mTxtVwPlayPause.setText("Play");
							mAudioRecordCunter.setText("0");
							mWebviewRecordPlayAud.setVisibility(View.GONE);
							mImgRecordPlayAud.setVisibility(View.VISIBLE);
						}
					}.start();
				} else {
					stopAudioEdit();
					mWebviewRecordPlayAud.setVisibility(View.GONE);
					mImgRecordPlayAud.setVisibility(View.VISIBLE);
					audioplayerState = BaseTabActivity.PLAYER_STOP_STATE;
					mTxtVwPlayPause.setTag(BaseTabActivity.PLAYER_STOP_STATE);
					mAudioRecordCunter.setText("0");
					mTxtVwPlayPause.setBackground(getResources().getDrawable(R.drawable.rounded_green_fill_bg));
					mTxtVwPlayPause.setText("Play");
				}

			}
		});
		volumeSeekbar = (VerticalSeekBar) v.findViewById(R.id.seekBar1);
		audioManager = (AudioManager)mActivity.getSystemService(mActivity.AUDIO_SERVICE);
		volumeSeekbar.setMax(audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		volumeSeekbar.setProgress(audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC));

		volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
				if(arg2)
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress, 0);
			}
		});
	}

	protected void playAudioEdit(String filename) {
		Uri uri = Utilities.getPathfromUri(gfilename);
		Log.i( Utilities.TAG, "Playing file : "+uri.toString());
		Utilities.play_recording(mActivity.getApplicationContext(), uri.toString());
		MediaPlayer mp = MediaPlayer.create(mActivity, Uri.parse(uri.toString()));
		if(mp !=null){
		int duration = mp.getDuration();
		mp.release();
				
		audioPlayTotalTime = duration;
		}else{
			 try{				 
				final MyDialog dialog = new MyDialog(mActivity, getResources().getString(R.string.msg_slight_problem), getResources().getString(R.string.msg_audio_play_problem) ,"OK");					
				dialog.setkeyListender(new MyDiologInterface() {
					@Override
					public void onPositiveClick() throws JSONException {
						dialog.dismiss();							
					}

					@Override
					public void onNegativeClick() {
						dialog.dismiss();
					}						
				});							
				dialog.onCreate(null);					
				dialog.show();
				
				Utilities.setDividerTitleColor(dialog, 0, mActivity);
				
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialog.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				lp.gravity = Gravity.CENTER;
				dialog.getWindow().setAttributes(lp);			
				
			}catch(Exception ex){
					ex.printStackTrace();
			}
		}
	}

	protected void stopAudioEdit() {
		Utilities.stop_player();
	}

	@Override
	public boolean onBackPressed() {
		return super.onBackPressed();
	}
	
	public void setActiveOrderObj(Order activeOrderObj) {
		this.activeOrderObj = activeOrderObj;
		}

	@Override
	public void headerClickListener(String callingId) {
		if (callingId.equals(BaseTabActivity.HeaderBackPressed))
		{
			Utilities.stop_player();
		}
		if (callingId.equals(BaseTabActivity.HeaderVolumeButton))
		{
			if (seekBtnViewStat == 0) {
				seekBtnViewStat=1;
				volumeSeekbar.setVisibility(View.VISIBLE);
			}
			else {
				seekBtnViewStat=0;
				volumeSeekbar.setVisibility(View.GONE);
			}
		}
	}

	public void loadDataOnBack(BaseTabActivity context) {
		// TODO Auto-generated method stub
		
	}

	public class SettingsContentObserver extends ContentObserver {
		Context context;

		public SettingsContentObserver(Context c, Handler handler) {
			super(handler);
			context=c;
		}

		@Override
		public boolean deliverSelfNotifications() {
			return super.deliverSelfNotifications();
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			if(!selfChange)
				volumeSeekbar.setProgress(audio.getStreamVolume(AudioManager.STREAM_MUSIC));

		}
	}
}