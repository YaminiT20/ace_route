package com.aceroute.mobile.software.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.utilities.PreferenceHandler;

import org.json.JSONException;

import java.util.ArrayList;


public class CustomStatusDialog extends Dialog implements View.OnClickListener {

    private static CustomStatusDialog custDialog;
    public Button cancel;
    public Button done;
    private String popupTitle, popuptyp;
    private StatusDiologInterface dClick = null;
    private TextView heading;
    private Context mContext;
    View line;
    public static ArrayList<String> msgQueue;
    public static ArrayAdapter<String> msgQueueAdapter;
    public WebView webviewActionView;
    private RelativeLayout layoutLoading;
    private static ListView dialog_ListView;


    public static class ErrorMsg {
        public String mesg, strType;

        public ErrorMsg(String msg, String type) {
            mesg = msg;
            strType = type;
        }
    }

    public CustomStatusDialog(Context context, String titleStr, String popuptype) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
        this.popupTitle = titleStr;
        this.popuptyp = popuptype;
        this.custDialog = CustomStatusDialog.this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout);
        setCancelable(false);

        Typeface tf = TypeFaceFont.getCustomTypeface(mContext);
        heading = (TextView) findViewById(R.id.text_title_dpopup);
        line = (View) findViewById(R.id.line_id);
        heading.setTypeface(tf, Typeface.BOLD);

        msgQueue = new ArrayList<String>();
        msgQueue.clear();

        msgQueueAdapter = new ArrayAdapter<String>(mContext, R.layout.list_black_text, R.id.list_content, msgQueue) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(R.id.list_content);
                textView.setTextSize(20 + PreferenceHandler.getCurrrentFontSzForApp(mContext));
                TypeFaceFont.overrideFonts(getContext(), textView);

                if (popuptyp.equals("LOGOUT")) {

                    if (position == 0) {
                        textView.setText("Syncing Data Before Logout");
                    } else {
                        view.setVisibility(View.GONE);
                    }
                } else {

                }
                return view;
            }

        };

        dialog_ListView = (ListView) findViewById(R.id.dialoglist);
        dialog_ListView.setAdapter(msgQueueAdapter);

        heading.setText(popupTitle);
        heading.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
        TypeFaceFont.overrideFonts(getContext(), heading);
        layoutLoading = (RelativeLayout) findViewById(R.id.lyt_loading);

        webviewActionView = (WebView) findViewById(R.id.webviewActionView);

        // disable scroll on touch
        webviewActionView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        webviewActionView.post(new Runnable() {
            public void run() {
                /* the desired UI update */
                //    webviewActionView.setBackgroundColor(Color.TRANSPARENT);
                webviewActionView.loadUrl("file:///android_asset/loading.html");
            }
        });

        cancel = (Button) findViewById(R.id.btn_cancel);
        cancel.setTypeface(tf, Typeface.NORMAL);
        done = (Button) findViewById(R.id.btn_done);
        done.setTypeface(tf, Typeface.NORMAL);
        cancel.setTypeface(tf);
        done.setTypeface(tf);
        cancel.setOnClickListener(this);
        done.setOnClickListener(this);

        if (popuptyp.equals("OLMP")) {
            cancel.setVisibility(View.GONE);
            //	done.setVisibility(View.VISIBLE);
            //	done.setTextSize(20 + PreferenceHandler.getCurrrentFontSzForApp(mContext));
            //done.setText("Pause");
            //	done.setPadding(0, 15, 0, 15);
        } else if (popuptyp.equals("OK")) {
            cancel.setVisibility(View.GONE);
            //	done.setVisibility(View.VISIBLE);
            //	disablePauseBtn(false);
            //	done.setText("Pause");
            //	done.setPadding(0, 15, 0, 15);
        } else if (popuptyp.equals("LOGOUT")) {
            line.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            done.setVisibility(View.GONE);
            disablePauseBtn(false);
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            //codecomment
			/*case R.id.btn_done:
			try {

				dClick.onPositiveClick(done.getText().toString());

			} catch (JSONException e) {
				e.printStackTrace();
			}
				break;

			case R.id.btn_cancel:
				dClick.onNegativeClick(done.getText().toString());
				break;*/
            //code comment
        }
    }

    public void setkeyListender(StatusDiologInterface myDiologInterface) {
        // TODO Auto-generated method stub
        dClick = myDiologInterface;
    }


    public void showHideView() {
        //done.setVisibility(View.VISIBLE);
        //done.setText("OK");
        try {
            if (webviewActionView != null) {
                webviewActionView.setVisibility(View.GONE);
            }// YD TO hide moving image
            if (layoutLoading != null) {
                layoutLoading.setVisibility(View.GONE);
            }// YD to hide moving image layout
            //code coomet
            if (this.isShowing()) {
                dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //code comment
    }

    public static void setHeightPopup() {
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        if (custDialog != null) {
            lp.copyFrom(custDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            Handler hi = new Handler(Looper.getMainLooper());
            hi.postDelayed(new Runnable() {
                @Override
                public void run() {
                    custDialog.getWindow().setAttributes(lp);
                }
            }, 0);
        }
        //lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public static void setHeightListView() {
        if (custDialog != null && dialog_ListView != null) {
            final ViewGroup.LayoutParams params = dialog_ListView.getLayoutParams();
            params.height = 250;

            Handler hi = new Handler(Looper.getMainLooper());
            hi.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog_ListView.setLayoutParams(params);
                    dialog_ListView.requestLayout();
                }
            }, 0);
        }
    }

    public void disablePauseBtn(boolean currentStatus) {
        if (done != null)
            done.setEnabled(currentStatus);
    }

}
