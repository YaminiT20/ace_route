package com.aceroute.mobile.software;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.utilities.PreferenceHandler;

public class ServiceFloating extends JobIntentService {

    private static TextView floaterTotalMsgCount;
    public RelativeLayout floaterRelOut;
    boolean mHasDoubleClicked = false;
    long lastPressTime;
    private WindowManager windowManager;
    private ImageView chatHead;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floaterRelOut != null) windowManager.removeView(floaterRelOut);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Typeface tf = TypeFaceFont.getCustomTypeface(getApplicationContext());
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        floaterRelOut = new RelativeLayout(this);
        //YD set image view
        chatHead = new ImageView(this);
        chatHead.setImageResource(R.drawable.floaternew);
        floaterTotalMsgCount = new TextView(this);
        floaterTotalMsgCount.setText("");
        floaterTotalMsgCount.setTextColor(Color.parseColor("#34495E"));
        floaterTotalMsgCount.setTypeface(tf, Typeface.BOLD);
        floaterRelOut.addView(chatHead);
        floaterRelOut.addView(floaterTotalMsgCount);// YD it means floaterTotalMsgCount should be like textlp
        RelativeLayout.LayoutParams Param = (RelativeLayout.LayoutParams) floaterTotalMsgCount.getLayoutParams();
        Param.topMargin = 26;
        Param.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        Param.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        Param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        Param.addRule(RelativeLayout.CENTER_HORIZONTAL);

        floaterTotalMsgCount.setLayoutParams(Param);

        /*final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);*/
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= 23) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        windowManager.addView(floaterRelOut, params);
        if (PreferenceHandler.getUiconfigMessage(this).equals("0")) {
            floaterRelOut.setVisibility(View.GONE);
        }

        try {
            floaterRelOut.setOnTouchListener(new View.OnTouchListener() {
                private WindowManager.LayoutParams paramsF = params;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            // Get current time in nano seconds.
                            long pressTime = System.currentTimeMillis();


                            // If double click...
                            if (pressTime - lastPressTime <= 300) {
                                //createNotification();//YD
                                ServiceFloating.this.stopSelf();
                                mHasDoubleClicked = true;
                            } else {     // If not double click....
                                mHasDoubleClicked = false;
                            }
                            lastPressTime = pressTime;
                            initialX = paramsF.x;
                            initialY = paramsF.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                            paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(floaterRelOut, paramsF);
                            break;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
        }

        floaterRelOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //TODO
            }
        });
    }
}