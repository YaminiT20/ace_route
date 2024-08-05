package com.aceroute.mobile.software.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.utilities.PreferenceHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class ViewImage extends Activity {
	// Declare Variable
	TextView text;
	ImageView backbtn,button_rotate;
	RelativeLayout R_imgebck;
	private ViewPager vi;
	ArrayList<String> filepath;
	ArrayList<String> filename;
	TouchImageView img;
	Intent i;

	static  boolean imagerotation=false;
	//YD using for zooming image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	PointF startPoint = new PointF();
	PointF midPoint = new PointF();
	float oldDist = 1f;
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	boolean img_visibility;
	boolean inbox_rotate;
	public static int headingFlag = 0;
	int position1;
	private ImageView back_button;
	public Activity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				//WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.view_image);
		activity = this;
		button_rotate = (ImageView)findViewById(R.id.button_rotate);
		TextView tvHeader = (TextView)findViewById(R.id.header_imageGl);
		TypeFaceFont.overrideFonts(this, tvHeader);
		tvHeader.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(this)));

		if(headingFlag==1){
			button_rotate.setVisibility(View.VISIBLE);
			tvHeader.setText("VIEW "+PreferenceHandler.getPictureHead(this));
			//headingFlag==0;
		}

		else if(headingFlag==2){
			tvHeader.setText("VIEW "+PreferenceHandler.getSignatureHead(this));
			//headingFlag==0;
		}


		backbtn = (ImageView)findViewById(R.id.back_button_viewimgs);

		R_imgebck=(RelativeLayout) findViewById(R.id.Bck_button_view);

		 vi=(ViewPager) findViewById(R.id.view_pager);


		try {
			Field mScroller;
			mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
			interpolator.setAccessible(true);
			FixedSpeedScroller scroller = new FixedSpeedScroller(vi.getContext());
			// scroller.setFixedDuration(5000);
			mScroller.set(vi, scroller);
		} catch (NoSuchFieldException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}

		 i = getIntent();

		     position1 = i.getExtras().getInt("position");
			 filepath = i.getStringArrayListExtra("filepath");
			 filename = i.getStringArrayListExtra("filename");
		     img_visibility= i.getBooleanExtra("visibleimage",false);
		     inbox_rotate= i.getBooleanExtra("inboxrotate",false);
		   //  Log.d("visibility",""+img_visibility);
//		if(position1 != 0){
//			position1 = position1 -1;
//		}

		final ImageAdapter adapter = new ImageAdapter(this,filepath,filename,img_visibility);
		vi.setAdapter(adapter);

		vi.setCurrentItem(position1);
/*
		if(img_visibility || inbox_rotate) {
			button_rotate.setVisibility(View.VISIBLE);

		}
		else
		{

			button_rotate.setVisibility(View.GONE);
		}*/
		R_imgebck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();

			}
		});




		button_rotate.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
			//	Toast.makeText(activity,"aaa",Toast.LENGTH_LONG).show();
				//adapter.RotateImage(img);
adapter.rotateImage(vi.getCurrentItem());
//				View CurrentView = vi.getChildAt(vi.getCurrentItem());
//				ImageView imageView = (TouchImageView) CurrentView.findViewById(R.id.image);
//				imageView.setRotation(imageView.getRotation()+90);
	//	vi.setRotation(vi.getRotation() + 90);
				//imageView.setRotation(imageView.getRotation() + 90);


			}

		});


	}

	public class FixedSpeedScroller extends Scroller {

		private int mDuration = 800;

		public FixedSpeedScroller(Context context) {
			super(context);
		}

		public FixedSpeedScroller(Context context, Interpolator interpolator) {
			super(context, interpolator);
		}

		public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
			super(context, interpolator, flywheel);
		}


		@Override
		public void startScroll(int startX, int startY, int dx, int dy, int duration) {
			// Ignore received duration, use fixed one instead
			super.startScroll(startX, startY, dx, dy, mDuration);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy) {
			// Ignore received duration, use fixed one instead
			super.startScroll(startX, startY, dx, dy, mDuration);
		}
	}



}