package com.aceroute.mobile.software.http;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.aceroute.mobile.software.utilities.XMLHandler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

public class HttpConnection {

	static int retrycount = 0;
	static int max_retry = 4;
	static int m =1;
	public static String get(Context context, String endpoint,
			Map<String, String> params) throws IOException {
		Utilities.log(context, "getCalled for url : " + endpoint);
		URL url = null;
		/*if(!Utilities.checkInternetConnection(context)){
			return XMLHandler.getXMLForErrorCode(context, JSONHandler.ERROR_CODE_INTERNET_CONNECTION);
		}*/

			if (endpoint == null || endpoint.equals("")) {
				Log.i( Utilities.TAG, "Endpoint error in get request.");
				return XMLHandler.getXMLForErrorCode(context, ServiceError.getEnumValstr(ServiceError.ERROR_CODE_URL));
			}

			if (!endpoint.endsWith("?") && params != null
					&& !endpoint.contains("?"))
				endpoint = endpoint + "?";
			String tailurl = null;
			boolean isLogin = false;
			if (params != null && params.size() > 0) {
				System.out.println("Map value and size :" + params.size());
				StringBuilder bodyBuilder = new StringBuilder();
				Iterator<Entry<String, String>> iterator = params.entrySet()
						.iterator();
				while (iterator.hasNext()) {
					Entry<String, String> param = iterator.next();
					if (param.getKey().equals("rid")) {
						isLogin = true;
					}
					bodyBuilder.append(param.getKey()).append('=')
							.append(param.getValue());
					if (iterator.hasNext()) {
						bodyBuilder.append('&');
					}
				}
				tailurl = bodyBuilder.toString();
			}

			String userdata = null;
			if (PreferenceHandler.getMtoken(context) != null
					&& PreferenceHandler.getMtoken(context).length() != 0
					&& PreferenceHandler.getCompanyId(context) != null
					&& PreferenceHandler.getCompanyId(context).length() != 0) {

				if (!isLogin) {
					userdata = "&" + Api.API_AUTH_TOKEN + "="
							+ PreferenceHandler.getMtoken(context) + "&" + Api.API_NSPACE + "="
							+ PreferenceHandler.getCompanyId(context) + "&" + Api.API_RID + "="
							+ PreferenceHandler.getResId(context);
				}
			}

			if (tailurl == null)
				if (userdata == null) {
					tailurl = "";
				} else {
					tailurl = userdata;
				}
			else {
				if (userdata != null) {
					tailurl = tailurl + userdata;
				}
			}
			if(!endpoint.endsWith("login?"))
			tailurl = tailurl + "&" + getCtsParameter();

			try {
				url = new URL(endpoint + "&" + tailurl);
				Log.i("TAG9999","Request Url:-"+String.valueOf(url));
			} catch (MalformedURLException e) {
				Log.i(Utilities.TAG, "invalid url: " + endpoint);
				return XMLHandler.getXMLForErrorCode(context, ServiceError.getEnumValstr(ServiceError.ERROR_CODE_URL));
			}

			HttpURLConnection conn = null;
			try {
				Log.i(Utilities.TAG, "hitting endpoint for get : " + url.toString());
				conn = (HttpURLConnection) url.openConnection();
				//conn.setRequestMethod("GET");
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.setConnectTimeout(50000);
				conn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded;charset=UTF-8");
				// handle the response
				int status = conn.getResponseCode();
				if (status != 200) {

					return XMLHandler.getXMLForErrorCode(context, ServiceError.getEnumValstr(ServiceError.ERROR_CODE_RESPONSE_ERROR));
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
					Log.i("TAG9999", "response :" + response);
					if(checkLogout(context,response)){
						return null;
					}
					if (m == 2)
						return null;
					if (response != null)
						return response;
					else
						retrycount = 0;
					return XMLHandler.getXMLForErrorCode(context, ServiceError.getEnumValstr(ServiceError.ERROR_CODE_UNKNOWN_ERROR));
				}
			} catch (UnknownHostException ue) {
				ue.printStackTrace();
				return XMLHandler.getXMLForErrorCode(context, ServiceError.getEnumValstr(ServiceError.NO_INTERNET_CONNECTION));
			} catch (EOFException eof) {
				if (retrycount < max_retry) {
					eof.printStackTrace();
					get(context, endpoint, params);
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
			Log.i(Utilities.TAG, "Unexpected error in get request.");
			return XMLHandler.getXMLForErrorCode(context, ServiceError.getEnumValstr(ServiceError.ERROR_CODE_UNKNOWN_ERROR));



	}



	static boolean checkLogout(Context context,String response){
		XMLHandler xmlhandler = new XMLHandler(context.getApplicationContext());
		Log.d("TAG9",xmlhandler.getValuefromXML(response,"id"));

		if(xmlhandler.getValuefromXML(response,"id").trim().equals("MobiLoginAgain")){
			if(BaseTabActivity.mBaseTabActivity!=null && !BaseTabActivity.logout){
				BaseTabActivity.logout=true;
				BaseTabActivity.mBaseTabActivity.syncDataBeforeLogoutAndMlogin("semipartial_logout");
			}
			PreferenceHandler.setMOBILOGINAGAIN(context,false);
			return true; //RJ lATER Change to app
		}else{
			return false;
		}
	}

	public static String postMultipart(Context context, String endpoint,
			Map<String, String> params, File file, String mime) {
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

					ContentType ct = ContentType.create(mime);
					multipartEntity.addBinaryBody("file", file,ct,"file");
				}

					Iterator<Entry<String, String>> iterator = params.entrySet()
							.iterator();

					while (iterator.hasNext()) {
						Entry<String, String> param = iterator.next();
						if (param != null) {

							multipartEntity.addPart(param.getKey(), new StringBody(param.getValue(),  ContentType.TEXT_PLAIN));
						}
					}
							// Execute HTTP Post Request
					if (PreferenceHandler.getMtoken(context) != null
							&& PreferenceHandler.getMtoken(context).length() != 0
							&& PreferenceHandler.getCompanyId(context) != null
							&& PreferenceHandler.getCompanyId(context).length() != 0) {
						multipartEntity.addPart(Api.API_AUTH_TOKEN, new StringBody(PreferenceHandler.getMtoken(context),  ContentType.TEXT_PLAIN));
						multipartEntity.addPart(Api.API_NSPACE, new StringBody(PreferenceHandler.getCompanyId(context),  ContentType.TEXT_PLAIN));
						multipartEntity.addPart(Api.API_RID, new StringBody(Long.toString(PreferenceHandler.getResId(context)),  ContentType.TEXT_PLAIN));


						Log.i(Utilities.TAG, "adding user data : "+Api.API_AUTH_TOKEN
								+ PreferenceHandler.getMtoken(context) + "&"
								+ Api.API_NSPACE
								+ PreferenceHandler.getCompanyId(context) + "&"
								+ Api.API_RID
								+ PreferenceHandler.getResId(context));
					}

					httppost.setEntity(multipartEntity.build());
					Log.i(Utilities.TAG, "Before httpclient.execute(httppost) Current time  = "+ System.currentTimeMillis());
					HttpResponse response = httpclient.execute(httppost,localContext);
					Log.i(Utilities.TAG, "After httpclient.execute(httppost) Current time  = "+ System.currentTimeMillis());
					int statuscode = response.getStatusLine().getStatusCode();

					Log.i(Utilities.TAG,"Response code : " + statuscode);

					if (statuscode != 200) {
						return null;
					}
					HttpEntity httpentity = response.getEntity();
					InputStream in = null;
					if (httpentity != null) {
						long length = httpentity.getContentLength();
						Log.i(Utilities.TAG, "Response length : " + length);

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

						Log.i(Utilities.TAG, "response :" + response2);
						//{"errorstring":"Successfully Uploaded","errorcode":"0","status":"OK","Data"
						retrycount = 0;
						if(m==2)
							return null;
						return response2;
					}

			}
			catch(UnknownHostException e){
				e.printStackTrace();
				return XMLHandler.getXMLForErrorCode(context, ServiceError.getEnumValstr(ServiceError.NO_INTERNET_CONNECTION));
			}
			catch (EOFException eof) {
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

	public static String postHTTPsMultipart(Context context, String endpoint,
									   Map<String, String> params, File file, String mime) {

		try {

			try {

				Log.d("requestparam" ,"params"+params);
				String charset = "UTF-8";
				String requestURL = endpoint;

					MultipartUtility multipart = new MultipartUtility(requestURL, charset);
/*
					multipart.addHeaderField("User-Agent", "CodeJava");
					multipart.addHeaderField("Test-Header", "Header-Value");*/

				Iterator<Entry<String, String>> iterator = params.entrySet()
							.iterator();
				Log.d("HttpConnection","Params "+params.toString());
					while (iterator.hasNext()) {
						Entry<String, String> param = iterator.next();
						if (param != null) {

							multipart.addFormField(param.getKey(), param.getValue());
						}
					}
					// Execute HTTP Post Request
					if (PreferenceHandler.getMtoken(context) != null
							&& PreferenceHandler.getMtoken(context).length() != 0
							&& PreferenceHandler.getCompanyId(context) != null
							&& PreferenceHandler.getCompanyId(context).length() != 0) {
						multipart.addFormField(Api.API_AUTH_TOKEN, PreferenceHandler.getMtoken(context));
						multipart.addFormField(Api.API_NSPACE, PreferenceHandler.getCompanyId(context));
						multipart.addFormField(Api.API_RID, Long.toString(PreferenceHandler.getResId(context)));


						Log.i(Utilities.TAG, "adding user data : "+Api.API_AUTH_TOKEN
								+ PreferenceHandler.getMtoken(context) + "&"
								+ Api.API_NSPACE
								+ PreferenceHandler.getCompanyId(context) + "&"
								+ Api.API_RID
								+ PreferenceHandler.getResId(context));
					}

					if(file!=null) {
						Log.d("HttpConnection","File Exist "+file.exists());
						multipart.addFilePart("file", file);

						Log.d("multipart","multipart"+multipart);
					}

					String responses = multipart.finish();
				Log.i(Utilities.TAG, "response :" + responses);

				Log.d("response","response"+responses);
					return responses;


			}
			catch(UnknownHostException e){
				e.printStackTrace();
				return XMLHandler.getXMLForErrorCode(context, ServiceError.getEnumValstr(ServiceError.NO_INTERNET_CONNECTION));
			}
			catch (EOFException eof) {
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

	public static String getCtsParameter(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		Long long1 = calendar.getTimeInMillis();
		String param = "cts="+ Long.toString(long1);
		return param;
	}
	
	public static String getRequestURL(Context context){
		
		String url = null;
		if (PreferenceHandler.getMtoken(context) != null
				&& PreferenceHandler.getMtoken(context).length() != 0
				&& PreferenceHandler.getCompanyId(context) != null
				&& PreferenceHandler.getCompanyId(context).length() != 0) {
			url = "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi"+"?"+Api.API_AUTH_TOKEN+"="
					+ PreferenceHandler.getMtoken(context) + "&"+Api.API_NSPACE+"="
					+ PreferenceHandler.getCompanyId(context) + "&"+Api.API_RID+"="
					+ PreferenceHandler.getResId(context);
			
			Log.i(Utilities.TAG, "adding user data : "+Api.API_AUTH_TOKEN
					+ PreferenceHandler.getMtoken(context) + "&"
					+ Api.API_NSPACE
					+ PreferenceHandler.getCompanyId(context) + "&"
					+ Api.API_RID
					+ PreferenceHandler.getResId(context));
		}



		return url;
		
	}

	public static String post(Context context, String endpoint,
							 Map<String, String> params)/* throws IOException */{
		Utilities.log(context, "getCalled for url : " + endpoint);
		URL url = null;
		/*if(!Utilities.checkInternetConnection(context)){
			return XMLHandler.getXMLFo
			rErrorCode(context, JSONHandler.ERROR_CODE_INTERNET_CONNECTION);
		}*/

		if (endpoint == null || endpoint.equals("")) {
			Log.i(Utilities.TAG, "Endpoint error in get request.");
			return XMLHandler.getXMLForErrorCode(context, ServiceError.getEnumValstr(ServiceError.ERROR_CODE_URL));
		}

	/*	if (!endpoint.endsWith("?") && params != null
				&& !endpoint.contains("?"))
			endpoint = endpoint + "?";*/
		String tailurl = null;
		boolean isLogin = false;
		if (params != null && params.size() > 0) {
			System.out.println("Map value and size :" + params.size());
			StringBuilder bodyBuilder = new StringBuilder();
			Iterator<Entry<String, String>> iterator = params.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> param = iterator.next();
				if (param.getKey().equals("rid")) {
					isLogin = true;
				}
				bodyBuilder.append(param.getKey()).append('=')
						.append(param.getValue());
				if (iterator.hasNext()) {
					bodyBuilder.append('&');
				}
			}

			tailurl = bodyBuilder.toString();
		}

		String userdata = null;
		if (PreferenceHandler.getMtoken(context) != null
				&& PreferenceHandler.getMtoken(context).length() != 0
				&& PreferenceHandler.getCompanyId(context) != null
				&& PreferenceHandler.getCompanyId(context).length() != 0) {

			if (!isLogin) {
				userdata = "&" + Api.API_AUTH_TOKEN + "="
						+ PreferenceHandler.getMtoken(context) + "&" + Api.API_NSPACE + "="
						+ PreferenceHandler.getCompanyId(context) + "&" + Api.API_RID + "="
						+ PreferenceHandler.getResId(context);
			}
		}

		if (tailurl == null)
			if (userdata == null) {
				tailurl = "";
			} else {
				tailurl = userdata;
			}
		else {
			if (userdata != null) {
				tailurl = tailurl + userdata;
			}
		}

		tailurl = tailurl + "&" + getCtsParameter();

		try {
			//url = new URL(endpoint + "&" + tailurl);
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			Log.i(Utilities.TAG, "invalid url: " + endpoint);
			return XMLHandler.getXMLForErrorCode(context, ServiceError.getEnumValstr(ServiceError.ERROR_CODE_URL));
		}

        Log.i(Utilities.TAG, "tailurl for request is : "+ tailurl);

		HttpURLConnection conn = null;
		try {
			Log.i(Utilities.TAG, "hitting endpoint for post : " + url.toString());
			conn = (HttpURLConnection) url.openConnection();
			//conn.setRequestMethod("GET");
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(50000);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			conn.getOutputStream().write(tailurl.getBytes());
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
		//	outputStreamWriter.write(tailurl.toString());
			outputStreamWriter.flush();
			// handle the response
			int status = conn.getResponseCode();
			if (status != 200) {
				return XMLHandler.getXMLForErrorCode(context, ServiceError.getEnumValstr(ServiceError.ERROR_CODE_RESPONSE_ERROR));
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
				Log.i(Utilities.TAG, "response :" + response);
				if(checkLogout(context,response)){
					return null;
				}
				if (m == 2)
					return null;
				if (response != null)
					return response;
				else
					retrycount = 0;
				return XMLHandler.getXMLForErrorCode(context, ServiceError.getEnumValstr(ServiceError.ERROR_CODE_UNKNOWN_ERROR));
			}
		} catch (UnknownHostException ue) {
			ue.printStackTrace();
			return XMLHandler.getXMLForErrorCode(context, ServiceError.getEnumValstr(ServiceError.NO_INTERNET_CONNECTION));
		} catch (EOFException eof) {
			if (retrycount < max_retry) {
				eof.printStackTrace();
				post(context, endpoint, params);
				retrycount = 1;
			}
		} catch (Exception th) {
			th.printStackTrace();
			//throw new IOException("Error in get :" + th.getMessage());
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		Log.i(Utilities.TAG, "Unexpected error in get request.");
		return XMLHandler.getXMLForErrorCode(context, ServiceError.getEnumValstr(ServiceError.ERROR_CODE_UNKNOWN_ERROR));



	}

}

 class MultipartUtility {
	private final String boundary;
	private static final String LINE_FEED = "\r\n";
	private HttpsURLConnection httpConn;
	private String charset;
	private OutputStream outputStream;
	private PrintWriter writer;

	public MultipartUtility(String requestURL, String charset) throws IOException {
		this.charset = charset;

		boundary = "-------------" + System.currentTimeMillis();

		URL url = new URL(requestURL);
		httpConn = (HttpsURLConnection) url.openConnection();
		httpConn.setUseCaches(false);
		httpConn.setDoOutput(true); // indicates POST method
		httpConn.setDoInput(true);
		httpConn.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + boundary);
		httpConn.setRequestProperty("User-Agent", "software");
		outputStream = httpConn.getOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
				true);
	}

	public void addFormField(String name, String value) {
		writer.append("--" + boundary).append(LINE_FEED);
		writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
				.append(LINE_FEED);
		writer.append("Content-Type: text/plain; charset=" + charset).append(
				LINE_FEED);
		writer.append(LINE_FEED);
		writer.append(value).append(LINE_FEED);
		writer.flush();
	}

	public void addFilePart(String fieldName, File uploadFile) throws IOException {
		String fileName = uploadFile.getName();
		writer.append("--" + boundary).append(LINE_FEED);
		writer.append("Content-Disposition: form-data; name=\"" + fieldName
				+ "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
		writer.append("Content-Type: "+ URLConnection.guessContentTypeFromName(fileName))
				.append(LINE_FEED);
		writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
		writer.append(LINE_FEED);
		writer.flush();

		FileInputStream inputStream = new FileInputStream(uploadFile);
		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		outputStream.flush();
		inputStream.close();

		writer.append(LINE_FEED);
		writer.flush();
	}

	public String finish() throws IOException {
		StringBuilder builder = new StringBuilder();
		writer.append(LINE_FEED).flush();
		writer.append("--" + boundary + "--").append(LINE_FEED);
		writer.close();

		// checks server's status code first
		int status = httpConn.getResponseCode();
		Log.d("status_code",""+status);
		if (status == HttpsURLConnection.HTTP_OK) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpConn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			reader.close();
			httpConn.disconnect();
		} else {
			throw new IOException("Server returned non-OK status: " + status);
		}

		return builder.toString();
	}
}

