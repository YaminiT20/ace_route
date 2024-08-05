package com.aceroute.mobile.software.camera;


import static com.aceroute.mobile.software.http.Api.context;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderMedia;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.fragment.AddEditFormFragment;
import com.aceroute.mobile.software.fragment.BaseFragment;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.SaveMediaRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;


public class Addsign  extends BaseFragment implements View.OnClickListener , HeaderInterface , RespCBandServST  {
	
	ImageView imgbck;
	Button done;
	Signature mSignature;
    Paint paint;
    LinearLayout mContent;
    EditText et1;
    static String orderid;
    TextView header,subHeader,headsign,tv_sign;
    int checkIfDrawn=0;
    String fileId="";
	private RelativeLayout parentView;
    
    Order activeOrderObj= null;
    
    MyDialog dialog= null;
	final Context contextdialog = mActivity;
	private int SAVESIGNATURE=1;
	String value="";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
/*		super.onCreate1(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setConten
		tView(R.layout.add_sign);
*/		
		View v = inflater.inflate(R.layout.add_sign, null);

		try {
			value = getArguments().getString("YourKey");
			fileId = getArguments().getString("fileId");
			//AddEditFormFragment.edtdSignedImg1(null, "", 1);
		}
		catch (Exception e)
		{

		}

		try {
			AddEditFormFragment.edtdSignedImg1(null, "", 1);

		}
		catch (Exception e)
		{

		}


		TypeFaceFont.overrideFonts(mActivity, v);
		mActivity.registerHeader(this);
		
		mActivity.setHeaderTitle("", "ADD "+PreferenceHandler.getSignatureHead(getActivity()), "");
		
		//final String orderId = PreferenceHandler.getOrderId4Js(mActivity);
		Typeface tf = TypeFaceFont.getCustomTypeface(mActivity.getApplicationContext());
		/*header=(TextView) v.findViewById(R.id.header_textView1);
			header.setTypeface(tf);
		
		subHeader=(TextView) v.findViewById(R.id.sub_header_textView2);
			subHeader.setTypeface(tf);*///YD later TODO
			
		headsign = (TextView)v.findViewById(R.id.add_sign_head);
		headsign.setTypeface(tf, Typeface.BOLD);
		
			/*header.setText(PreferenceHandler.getOrderId4Js(mActivity));
			String S_header=PreferenceHandler.getOrderName4Js(mActivity);
			//	subHeader.setText(PreferenceHandler.getOrderName4Js(this));
				if(S_header.length()<=15){
					subHeader.setText(S_header);
				}else {
					subHeader.setText(S_header.substring(0 , 17)+"..");
				}
		//subHeader.setText(PreferenceHandler.getOrderName4Js(this));
		done=(Button) v.findViewById(R.id.btn_save_sign); 
		done.setTypeface(tf);*/// YD later TODO
		 //done.setEnabled(false);
		 mContent = (LinearLayout) v.findViewById(R.id.mysignature_sign);
		 et1=(EditText)v.findViewById(R.id.editText_signit);
		 et1.setTypeface(tf);

		if (!value.equals(""))
		{
			et1.setVisibility(View.GONE);
		}
		 mSignature = new Signature(mActivity,null);
	        mContent.addView(mSignature);
	        orderid=PreferenceHandler.getOrderId4Js(mActivity);
	        tv_sign=(TextView) v.findViewById(R.id.add_sign_head);
	        tv_sign.setText("ADD SIGNATURE"/*Utilities.getStringForAppAndr(mActivity,4)*/);
		
		mContent.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(mActivity.getCurrentFocus()!=null){
					hideSoftKeyboard();
				}
				return false;
			}
		});
		
		parentView = (RelativeLayout) v.findViewById(R.id.parentView);
		parentView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideSoftKeyboard();
			
				return false;
			}
		});
	
        return v;
 	}

	
        class Signature extends View {
            static final float STROKE_WIDTH = 10f;
            static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
            Paint paint = new Paint();
            Path path = new Path();
     
            float lastTouchX;
            float lastTouchY;
            final RectF dirtyRect = new RectF();
    		private File filename;

            public Signature(Context context, AttributeSet attrs) {
                super(context, attrs);
                paint.setAntiAlias(true);
                paint.setColor(Color.BLUE);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeJoin(Paint.Join.ROUND);
                paint.setStrokeWidth(STROKE_WIDTH);
            }

            FileOutputStream out;
            public void save() throws IOException {

				//editText.onEditorAction(EditorInfo.IME_ACTION_DONE);

				//hideSoftKeyboard();
            	String time = String.valueOf(System.currentTimeMillis());
                Bitmap returnedBitmap = Bitmap.createBitmap(mContent.getWidth(),
						mContent.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(returnedBitmap);
                Drawable bgDrawable = mContent.getBackground();
                if (bgDrawable != null)
                    bgDrawable.draw(canvas);
                else
                    canvas.drawColor(Color.WHITE);
                mContent.draw(canvas);
                String name_edit= et1.getText().toString();
                /*File f = new File(Environment.getExternalStorageDirectory()
                        + File.separator + "Aceroute/temp_"+time+".jpg");
               String path= Environment.getExternalStorageDirectory()
                       + File.separator + "Aceroute/temp_"+time+".jpg";*/  //YD commented because it was not creating folder , also dumping pics in wrong folder i.e:Aceroute

				String compfilename = null;
				File file;
				String basepath = null;

//				if (Environment.getExternalStorageState().equals(
//						Environment.M
//
//		++++++				EDIA_MOUNTED)) {
////					compfilename = Environment
////							.getExternalStorageDirectory()
////							.getAbsolutePath()
////							+ "/AceRoute";
//
//
//
//					compfilename = getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DCIM)
//							.getAbsolutePath()
//							+ "/AceRoute";
//
//				//	File mydir = context.getDir("mydir", Context.MODE_PRIVATE); //Creating an internal dir;
//				//	File fileWithinMyDir = new File(mydir, "myfile"); //Getting a file within the dir.
//					//FileOutputStream out = new FileOutputStream(fileWithinMyDir);
//
//					//Log.d("niki","onCreate()"+fileWithinMyDir);
//
//
//
//				} else {
//					compfilename = getActivity().getApplicationContext().getFilesDir()
//							.getAbsolutePath()
//							+ "/AceRoute";
//				}
//				//}
				compfilename = getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
							.getAbsolutePath()
							+ "/AceRoute";
				file = new File(compfilename);
				if(!file.exists()){
					file.mkdir();
				}

				basepath = file.getAbsolutePath();

				File f = new File(basepath+"/temp_"+time+".jpg");
				//uri=f.getAbsolutePath();
				String path= basepath+"/temp_"+time+".jpg";
				AddEditFormFragment.edtdSignedImg1(f, path, 1);
               
              //  f.createNewFile();
    		    out = new FileOutputStream(f);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                returnedBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                out.write(bs.toByteArray());
                out.close();
                
                String tistamp = String.valueOf(getCurrentTime());
				String file_desc = et1.getText().toString();

				if(checkIfDrawn==0){
					//Utilities.showCustomDialog(Addsign.this, ((AceRouteApplication)getApplicationContext()).ErrorStringArr.get(1));
					showAlertDialog(getResources().getString(R.string.msg_signature_problem));
				}
				else{
				
				/*String str = "{\"url\":\""+"https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi"+"\",\"type\":\"post\",\"data\":{\"id\":\"0\",\"oid\":\""+Addsign.orderid+"\"," +
						"\"tid\":\"2\",\"file\":\"\",\"dtl\":\""+file_desc+"\",\"action\":\"savefile\"," +
								"\"stmp\":\""+tistamp+"\",\"mime\":\"jpg\",\"metapath\":\""+path+"\"}}";
				AceRouteService.jsInterface.saveImageSign(str);		*/


				SaveMediaRequest req = new SaveMediaRequest();
				req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
				req.setType("post");
				req.setId("0");
				req.setOrderId(String.valueOf(activeOrderObj.getId()));
				req.setTid("2");
				req.setFile("");
				req.setDtl(file_desc);
				req.setAction(OrderMedia.ACTION_MEDIA_SAVE);
				req.setTimestamp(tistamp);
				req.setMime("jpg");
				req.setMetapath(path);

				if (!value.equals("")) {
					req.setFrmkey(value);
				}

				if (!fileId.equals("")) {
					req.setFrmfldkey(fileId);
				}

					value="";
					fileId="";
				OrderMedia.saveData(req, mActivity, Addsign.this, SAVESIGNATURE);

                //finish(); YD later TODO
				}
                
            }
     
            @Override
            protected void onDraw(Canvas canvas) {
                canvas.drawPath(path, paint);
            }
     
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                float eventX = event.getX();
                float eventY = event.getY();
                //done.setEnabled(true);
                checkIfDrawn= 1;
                
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;
     
                case MotionEvent.ACTION_MOVE:
     
                case MotionEvent.ACTION_UP:
     
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;
                }
     
                invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                        (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                        (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                        (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));
     
                lastTouchX = eventX;
                lastTouchY = eventY;
     
                return true;
            }
     
            private void resetDirtyRect(float eventX, float eventY) {
                dirtyRect.left = Math.min(lastTouchX, eventX);
                dirtyRect.right = Math.max(lastTouchX, eventX);
                dirtyRect.top = Math.min(lastTouchY, eventY);
                dirtyRect.bottom = Math.max(lastTouchY, eventY);
            }
        }
        protected long getCurrentTime() {
    		Calendar rightNow = Calendar.getInstance();

    		// offset to add since we're not UTC

    		/*long offset = rightNow.get(Calendar.ZONE_OFFSET) +
    		    rightNow.get(Calendar.DST_OFFSET);

    		long sinceMidnight = (rightNow.getTimeInMillis() + offset) %
    		    (24 * 60 * 60 * 1000);*/
    		long sinceMidnight = System.currentTimeMillis();
    		return sinceMidnight;
    		
    	}
		@Override
		public void ServiceStarter(RespCBandServST activity, Intent intent) {
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
					if (response.getId()==SAVESIGNATURE)
					{
						activeOrderObj.setCustMetaCount(activeOrderObj.getCustMetaCount()+1);
						activeOrderObj.setSigCount(activeOrderObj.getSigCount()+1);
						Gridview_MainActivity.isSyncMedia = true;
						goBack(mActivity.SERVICE_Thread);
					}
				}
				else if(response.getStatus().equals("success")&& 
						response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
				{
				}
			}



		}
		
		private void goBack(final int threadType) {
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mActivity.popFragments(threadType);
				}
			});
		}
		
		
		@Override
		public void headerClickListener(String callingId) {

			if (callingId.equals(BaseTabActivity.HeaderDonePressed))
			{
				try {

					hideSoftKeyboard();

					new Handler(
					).postDelayed(new Runnable() {
						@Override
						public void run() {
							try {
								mSignature.save();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					},500);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		/*	if (callingId.equals(mActivity.HeaderDonePressed))
			{
				try {
					mSignature.save();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}  */
		/*	InputMethodManager mgr = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		    mgr.hideSoftInputFromWindow(et1.getWindowToken(), 0);*/
			hideSoftKeyboard();
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
		
		public void setActiveOrderObject(Order orderObj)
		{
			activeOrderObj = orderObj;
		}

		
		private void showAlertDialog(String message){
			try{			
				dialog = new MyDialog(mActivity, getResources().getString(R.string.msg_slight_problem), message,"OK");		
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
				
			}catch(Exception e){
				e.printStackTrace();
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
			
		}
}


