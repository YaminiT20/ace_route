package com.aceroute.mobile.software.mangers;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.aceroute.mobile.software.async.HttpUtils;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.http.HttpConnection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public abstract class Manager{
	static String result;
	protected static Integer requestType;
	Activity activity;
	static String response;
	private static final int GET_REQUEST = 1;
	private static final int POST_REQUEST = 2;
	private static final int MULTIPART_REQUEST = 3;
	static RespCBandServST rcallback;
	static int reqId;

	
	public abstract String DoAction(Map reqmap,int action, int reqType, String URL,File file,int requestId,RespCBandServST obj);
	
	public Manager() {
		// TODO Auto-generated constructor stub
	}
	
	public Manager(RespCBandServST callback){
			//this.rcallback = callback;
	}
	
//Async task to perform network operations
		public static class PostRequest extends AsyncTask<Integer, integer, String> {
			String ReqJson,url;
			java.io.File File;
			Context mcContext;
	//		Dialog waitDialog;
			Map<String, String> reqMap = new HashMap<String, String>();
			
			
			public PostRequest(Map reqmap, String url, Context mContext,RespCBandServST obj){
			//this.ReqJson = Json;
			this.url = url;
			this.mcContext = mContext;
			reqMap= reqmap;
			rcallback=obj;
			
		/*	waitDialog = new Dialog(mcContext,android.R.style.Theme_Translucent_NoTitleBar);
			waitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			waitDialog.setContentView(R.layout.waitprogress_bar);
			waitDialog.setCanceledOnTouchOutside(false);*/
			
			}
			Map<String, String> reqData = new HashMap<String, String>();
	 
			@Override
			protected void onPreExecute() { 
			super.onPreExecute();
		//	waitDialog.show();
			
			}
			
			
		@Override
		protected String doInBackground(Integer... params) {
			
		//Get the request type
		requestType = params[0];
		reqId = params[1];
		
		switch (requestType) {
		
		case GET_REQUEST:
			
			Log.i("App", "Before HttpUtils.post Current time = " + System.currentTimeMillis());
			try {
				response = HttpConnection.get(mcContext,url, reqMap);
			} catch (IOException e) {e.printStackTrace();}
			Log.i("App", "After HttpUtils.post Current time  = " + System.currentTimeMillis());
			Log.i("App", "Response : " + response);
			//if(response!=null){
			if(url.endsWith("login"))
			rcallback.setResponseCallback(response,2);
			else
			rcallback.setResponseCallback(response,reqId);
			/*}else{
				Log.i("App","Response is null.."+System.currentTimeMillis());
			}*/
		break;
		
		case POST_REQUEST:
				
			Log.i("App", "Before HttpUtils.post Current time = " + System.currentTimeMillis());
				response = HttpConnection.post(mcContext,url, reqMap);
			Log.i("App", "After HttpUtils.post Current time  = " + System.currentTimeMillis());
			Log.i("App", "Response : " + response);
			if(response!=null){
			rcallback.setResponseCallback(response,reqId);
			}else{
				Log.i("App", "Response is null.." + System.currentTimeMillis());
			}
		break;
			
		case MULTIPART_REQUEST:
			
			Log.i("App", "Before HttpUtils.post multipart Current time = " + System.currentTimeMillis());
			response = HttpUtils.postMultipart(mcContext,url, reqMap, File);
			Log.i("App", "After HttpUtils.post multipart Current time  = " + System.currentTimeMillis());
			Log.i("App", "Response : " + response);
			if(response!=null){
				rcallback.setResponseCallback(response,reqId);
			}else{
				Log.i("App", "Response is null.." + System.currentTimeMillis());
			}
		break;

		default:
			break;
	   }
		return null;
	}
		@Override
		protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	//	waitDialog.dismiss();
		}
		
  }
}
