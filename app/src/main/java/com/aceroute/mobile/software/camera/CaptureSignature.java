package com.aceroute.mobile.software.camera;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.fragment.AddEditFormFragment;
import com.aceroute.mobile.software.fragment.BaseFragment;
import com.aceroute.mobile.software.http.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CaptureSignature extends BaseFragment implements View.OnClickListener , HeaderInterface , RespCBandServST  {
	signature mSignature;
    Paint paint;
    LinearLayout mContent;
    RelativeLayout R_imgebck;
    Button clear;
    //, save;
	//private EditText edit_name;
	//private ImageView back_button;
	int Flag;
	Dialog waitDialog;
	private int saveFlag;





















































	public String isfromform;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/*super.onCreate1(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		*/
		View v = inflater.inflate(R.layout.capturesignature, null);
		mActivity.registerHeader(this);
		BaseTabActivity.setHeaderTitle("", "ANNOTATE", ""); //Annotate Picture

        /************** waiting dialog***************/
        waitDialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
		waitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		waitDialog.setContentView(R.layout.waitprogress_bar);
		waitDialog.setCanceledOnTouchOutside(false);
		/***********End******************/

		Typeface tf = TypeFaceFont.getCustomTypeface(mActivity.getApplicationContext());

 		Bundle i = getArguments();
 		// Get the position
 		int position = i.getInt("position");
 		// Get String arrays FilePathStrings
 		ArrayList<String> filepath = i.getStringArrayList("filepath");
 		// Get String arrays FileNameStrings
 		ArrayList<String> filename = i.getStringArrayList("filename");
 		if (i.getString("isFromForm") != null) {
 		    isfromform = i.getString("isFromForm");
        }

 		String flag = i.getString("Flag");
 		if (flag!=null && flag!="")
 			Flag = Integer.valueOf(i.getString("Flag"));

        clear = (Button) v.findViewById(R.id.clear);
        clear.setTypeface(tf, Typeface.BOLD);

        mContent = (LinearLayout) v.findViewById(R.id.mysignature);
        //edit_name=(EditText)findViewById(R.id.edit_name);
        mSignature = new signature(mActivity, null);
        if (Flag !=1)
        	mContent.addView(mSignature);
        //edit_name.setText(filename.get(position));

        Uri uri = Uri.parse(filepath.get(0));
        File imgFile = new File(uri.getPath());
        BitmapFactory.Options options = new BitmapFactory.Options();
       // options.inJustDecodeBounds = true;
        options.inPreferredConfig = Config.RGB_565;
        options.inSampleSize=2;
    	//Bitmap bmp = BitmapFactory.decodeFile(filepath.get(position),options);himanshu
        Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);


	    	BitmapDrawable background = new BitmapDrawable(bmp);
	    	//mContent.setBackgroundDrawable(background);
        mContent.setBackground(background);
        /*mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity,"Image clicked",Toast.LENGTH_LONG).show();
            }
        });*/

		// Set the decoded bitmap into ImageView

        //save.setOnClickListener(onButtonClick);
        clear.setOnClickListener(onButtonClick);
        //R_imgebck.setOnClickListener(onButtonClick);
		return v;
    }

    Button.OnClickListener onButtonClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v == clear) {
                mSignature.clear();
            }
        }

		private void backress() {
			// TODO Auto-generated method stub
			//finish(); YD TODO
		}
    };


    public class signature extends View {
        static final float STROKE_WIDTH = 10f;
        static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        Paint paint = new Paint();
        Path path = new Path();

        float lastTouchX;
        float lastTouchY;
        final RectF dirtyRect = new RectF();
		private File filename;
		Context context;

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.context = context;
            paint.setAntiAlias(true);
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void clear() {
            path.reset();
            invalidate();
            saveFlag=0;
        }
        FileOutputStream out;
        public void save() throws IOException {

        	long time = System.currentTimeMillis();
            Bitmap returnedBitmap = Bitmap.createBitmap(mContent.getWidth(),
                    mContent.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            Drawable bgDrawable = mContent.getBackground();
            if (bgDrawable != null)
                bgDrawable.draw(canvas);
            else
                canvas.drawColor(Color.WHITE);
            mContent.draw(canvas);
            //String name_edit= edit_name.getText().toString();
            // File f = new File(Environment.getExternalStorageDirectory()
            //       + File.separator + "/DCIM/Camera"+"/"+name_edit); himanshu
            
            
            File f = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                    + File.separator + "AceRoute/temp_" + time + ".jpg");
            String path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                    + File.separator + "AceRoute/temp_" + time + ".jpg";
            f.createNewFile();
           
		    out = new FileOutputStream(f);
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            returnedBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
            out.write(bs.toByteArray());
            out.close();

            if (isfromform != null && isfromform.equalsIgnoreCase("1")) {
                AddEditFormFragment.edtdSignedImg1(f, path, 1);
            } else {
                OrderPicSaveActivity.edtdSignedImg(f , path,1);
            }

            waitDialog.dismiss();
            mActivity.popFragments(mActivity.UI_Thread);
            //Gridview_MainActivity.adapter.notifyDataSetChanged();
           /* Intent intent = new Intent();
            intent.putExtra("byteArray", bs.toByteArray());
            setResult(1, intent);*/
            //finish(); YD TODO
            
        }
 
        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            canvas.drawPath(path, paint);
        }
 
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            saveFlag=1;
 
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void headerClickListener(String callingId) {
		if (callingId.equals(mActivity.HeaderDonePressed))
		{
			if(Flag!=1 &&  saveFlag==1){
        		waitDialog.show();
        		try {
					mSignature.save();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	public void loadDataOnBack(BaseTabActivity mActivity) {
		mActivity.registerHeader(this);
		
	}
}
