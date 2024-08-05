/**
 * its HttpUtils  class use for get and post request to server and send response.
 */

/**
 * @author Pramod  25/06/2014.
 * Copyright on � 2014.
 */

package com.aceroute.mobile.software.async;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtils {

	static int retrycount = 0;
	static int max_retry = 4;

	public static String get(Context context, String endpoint,
			Map<String, String> reqParams) throws IOException {
		Log.i("BARAPP", "getCalled for url : " + endpoint);
		URL url = null;
		if (endpoint == null || endpoint.equals("")) {
			Log.i("BARAPP", "Endpoint error in get request.");
			return null;
		}
		if (!endpoint.endsWith("?") && reqParams != null
				&& !endpoint.contains("?"))
			endpoint = endpoint + "?";
		String tailurl = null;
		if (reqParams != null && reqParams.size() > 0) {
			StringBuilder bodyBuilder = new StringBuilder();
			Iterator<Entry<String, String>> iterator = reqParams.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> param = iterator.next();
				bodyBuilder.append(param.getKey()).append('=')
						.append(param.getValue());
				if (iterator.hasNext()) {
					bodyBuilder.append('&');
				}
			}
			tailurl = bodyBuilder.toString();
		}
		if (tailurl == null)
			tailurl = "";
		else
			tailurl = "&" + tailurl;
		try {
			url = new URL(endpoint + tailurl);
		} catch (MalformedURLException e) {
			Log.i("BARAPP", "invalid url: " + endpoint);
		}

		HttpURLConnection conn = null;
		try {
			Log.i("BARAPP", "hitting endpoint for get : " + url.toString());
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");


			// handle the response
			int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Get failed with error code " + status);
			}

			InputStream in = conn.getInputStream();

			if (in != null) {
				StringBuilder builder = new StringBuilder();
				String line;
				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in, "UTF-8"));
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				} finally {
					in.close();
				}

				String response = builder.toString();

				if (response != null) {
					Log.i("test", "get response : " + response);
					return response;
				} else
					retrycount = 0;
				return null;
			}
		} catch (UnknownHostException ue) {
			ue.printStackTrace();
		} catch (EOFException eof) {
			if (retrycount < max_retry) {
				eof.printStackTrace();
				get(context, endpoint, reqParams);
				retrycount = 1;
			}
		} catch (Exception th) {
			th.printStackTrace();
			throw new IOException("Error in get :" + th.getMessage());
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		Log.i("BARAPP", "Unexpected error in get request.");
		return null;
	}

	
	
	public static String post(Context context, String endpoint,
			Map<String, String> params) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		StringBuffer paramString = new StringBuffer("");
		StringBuffer tempBuffer = new StringBuffer("");
		if (params != null) {
			if (params.get("key")!=null){
			Iterator<Entry<String, String>> iterator = params.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> param = iterator.next();
				if (param != null) {
					if (paramString.length() > 0) {
						paramString.append("&");
					}
					Log.i("BARAPP", "post key : " + param.getKey());
					String value;
					try {
						value = URLEncoder.encode(param.getValue(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						value = "";
						e.printStackTrace();
					}
					paramString.append(param.getKey()).append("=")
							.append(value);
				}
			}
		  }
		}
		Log.i("BARAPP", "post Stringbuffer  : " + paramString.toString());

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(endpoint);
			try {
				// Add your data
				httppost.addHeader("Content-Type",
						"application/x-www-form-urlencoded");
				if (!paramString.equals("")) {
					String data = "";
					if (tempBuffer.length() > 0) {
						data = data + tempBuffer.toString();
					}
					data = data + paramString.toString();
					if (data.endsWith("&")) {
						data = data.substring(0, data.length() - 1);
					}
					Log.i("BARAPP", "post data : " + data);
					httppost.setEntity(new ByteArrayEntity(data.getBytes()));
				}

				// Execute HTTP Post Request
				Log.i("Barapp", "Before httpclient.execute(httppost) Current time  = " + System.currentTimeMillis());
				HttpResponse response = httpclient.execute(httppost);
				Log.i("Barapp", "After httpclient.execute(httppost) Current time  = " + System.currentTimeMillis());
				int statuscode = response.getStatusLine().getStatusCode();

				Log.i("BARAPP", "Response code : " + statuscode);
				
				if (statuscode != 200) {
					return null;
				}
				HttpEntity entity = response.getEntity();
				InputStream in = null;
				if (entity != null) {
					in = entity.getContent();
				}

				if (in != null) {
					StringBuilder builder = new StringBuilder();
					String line;
					try {
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(in, "UTF-8"));
						while ((line = reader.readLine()) != null) {
							builder.append(line);
						}
					} finally {
						in.close();
					}

					String response2 = builder.toString();
					Log.i("BARAPP", "response :" + response2);
					retrycount = 0;
					return response2;
				}
			} catch (EOFException eof) {
				if (retrycount < max_retry) {
					eof.printStackTrace();
					post(context, endpoint, params);
					retrycount = 1;
				}
			} catch (Throwable th) {
				th.printStackTrace();
				throw new IOException("Error in posting :" + th.getMessage());
			}
			retrycount = 0;
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String postMultipart(Context context, String endpoint,
			Map<String, String> params, File file) {
		String boundary = "-------------" + System.currentTimeMillis();
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	
			try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(endpoint);
		//	httppost.setHeader("Content-Type", "image/jpg");
			//httppost.setHeader("Content-type", "multipart/form-data; boundary="+boundary);
			
			HttpContext localContext = new BasicHttpContext();


			  
			  MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
			try {
				// Add your data
				if(file!=null)
				{
					String mime = "image/jpg";
					ContentType ct = ContentType.create("image/jpg");
					multipartEntity.addBinaryBody("filedata", file,ct,"myfile");
				}
				if (params.get("key")!=null){
					Iterator<Entry<String, String>> iterator = params.entrySet()
							.iterator();
				
					while (iterator.hasNext()) {
						Entry<String, String> param = iterator.next();
						if (param != null) {
						
							multipartEntity.addPart("key", new StringBody(param.getValue(),  ContentType.TEXT_PLAIN));
						}
					}
							// Execute HTTP Post Request
					
						
					httppost.setEntity(multipartEntity.build());
					Log.i("Barapp", "Before httpclient.execute(httppost) Current time  = " + System.currentTimeMillis());
					HttpResponse response = httpclient.execute(httppost,localContext);
					Log.i("Barapp", "After httpclient.execute(httppost) Current time  = " + System.currentTimeMillis());
					int statuscode = response.getStatusLine().getStatusCode();
	
					Log.i("BARAPP", "Response code : " + statuscode);
	
					if (statuscode != 200) {
						return null;
					}
					HttpEntity httpentity = response.getEntity();
					InputStream in = null;
					if (httpentity != null) {
						long length = httpentity.getContentLength();
						Log.i("BARAPP", "Response length : " + length);
						
						in = httpentity.getContent();
					} //vicky It is crashing if getcontent is used
	
					if (in != null) {
						StringBuilder builder = new StringBuilder();
						String line;
						try {
							BufferedReader reader = new BufferedReader(
									new InputStreamReader(in, "UTF-8"));
							while ((line = reader.readLine()) != null) {
								builder.append(line);
							}
						} finally {
							in.close();
						}
	
						String response2 = builder.toString();
	
						Log.i("BARAPP", "response :" + response2);
						//{"errorstring":"Successfully Uploaded","errorcode":"0","status":"OK","Data"
						retrycount = 0;
						return response2;
					}
				}
			} catch (EOFException eof) {
				if (retrycount < max_retry) {
					eof.printStackTrace();
					post(context, endpoint, params);
					retrycount = 1;
				}
			} catch (Throwable th) {
				th.printStackTrace();
				throw new IOException("Error in posting :" + th.getMessage());
			}
			retrycount = 0;
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String postData(String endpoint, Map<String, String> params) {
		// Create a new HttpClient and Post Header

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(endpoint);

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			Iterator itr = params.keySet().iterator();
			while (itr.hasNext()) {
				String key = (String) itr.next();
				nameValuePairs
						.add(new BasicNameValuePair(key, params.get(key)));
			}

			Log.i("BARAPP", "postdata : " + httppost.getRequestLine());

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream in = null;
			if (entity != null) {
				in = entity.getContent();
			}

			if (in != null) {
				StringBuilder builder = new StringBuilder();
				String line;
				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in, "UTF-8"));
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				} finally {
					in.close();
				}

				String response2 = builder.toString();

				Log.i("BARAPP", "response :" + response2);
				retrycount = 0;
				return response2;
			}
			return null;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// mrbool.com/how-to-implement-shared-preferences-in-android/28370#ixzz2nFnddEp8
	public static boolean checkInternetConnection(Context context) {
		ConnectivityManager localConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if ((localConnectivityManager.getActiveNetworkInfo() != null)
				&& (localConnectivityManager.getActiveNetworkInfo()
						.isAvailable())
				&& (localConnectivityManager.getActiveNetworkInfo()
						.isConnected())) {
			return true;
		} else {
			return false;
		}
	}
	
}
