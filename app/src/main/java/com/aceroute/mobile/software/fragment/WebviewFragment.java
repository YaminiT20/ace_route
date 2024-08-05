package com.aceroute.mobile.software.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.dialog.TypeFaceFont;


public class WebviewFragment extends Fragment {

	public static WebView cwv=null;
	//public static JSInterface jsinterface = null;
	private final static String ASSETS_BASE_URL = "file:///android_asset/";
	Context context;
	//AceRoute acerouteCtx;
	
	static String launchView= null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	context = getActivity();
	}
	
	@Override
	   public View onCreateView(LayoutInflater inflater,
	      ViewGroup container, Bundle savedInstanceState) {
	      /**
	       * Inflate the layout for this fragment
	       */
		
		View view = inflater.inflate(R.layout.webviewfragment, container, false);
		TypeFaceFont.overrideFonts(getActivity(), view);
		cwv=(WebView)view.findViewById(R.id.tutorialView);
		// webViewInitializer();
		cwv.getSettings().setGeolocationEnabled(true);
		cwv.getSettings().setAppCacheEnabled(true);
		cwv.getSettings().setDatabaseEnabled(true);
		cwv.getSettings().setDomStorageEnabled(true);
		cwv.getSettings().setJavaScriptEnabled(true);
		cwv.getSettings().setLoadsImagesAutomatically(true);
		cwv.getSettings().setAllowFileAccess(true); 
		//cwv.loadData("hi hello","text/plain","UTF-8");
		cwv.getSettings().setDatabasePath("/data/data/" + cwv.getContext().getPackageName() + "/databases/");

		return view;
	     // return inflater.inflate(
	     // R.layout.testfraglayone, container, false);
	   }
	
	 public void onHiddenChanged(boolean hidden) {
		    super.onHiddenChanged(hidden);
		    if (hidden) {
		        //do when hidden
		    } else {
		    	Log.i("Message", "WEbview shown");
		    	
		    }
		}
	
}