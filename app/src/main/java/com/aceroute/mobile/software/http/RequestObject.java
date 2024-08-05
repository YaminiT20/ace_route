package com.aceroute.mobile.software.http;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RequestObject implements Parcelable {
	
	private String data; // It is a JSON string.
	private int reqid;
	private long timeInMilliSeconds;
	private Object dataObject;
	protected String action;
	//YD below variable are used for pubnub only
	int extraAction=-1;
	int objecttype=-1;
	long idPB=-1;
	HashMap<String , String> requestMap ;

	public HashMap<String, String> getRequestMap() {
		return requestMap;
	}

	public void setRequestMap(HashMap<String, String> requestMap) {
		this.requestMap = requestMap;
	}

	public long getIdPb() {
		return idPB;
	}

	public void setIdPb(long id) {
		this.idPB = id;
	}

	public int getExtraAction() {
		return extraAction;
	}

	public void setExtraAction(int extraAction) {
		this.extraAction = extraAction;
	}

	public Object getReqDataObject() {
		return dataObject;
	}

	public void setReqDataObject(Object dataObject) {
		this.dataObject = dataObject;
	}

	public int getObjecttype() {
		return objecttype;
	}

	public void setObjecttype(int objecttype) {
		this.objecttype = objecttype;
	}
	
	public void setReqId(int id){
		reqid = id;
	}

	public int getReqId() {
		return reqid;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public long getTimeInMilliSeconds() {
		return timeInMilliSeconds;
	}
	
	public void setTimeInMilliSeconds(long timeInMilliSeconds) {
		this.timeInMilliSeconds = timeInMilliSeconds;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		 parcel.writeString(action);
		writeMap(requestMap,parcel);
	}
	
	public static final Creator<RequestObject> CREATOR = new Creator<RequestObject>() {
		 public RequestObject createFromParcel(Parcel source) {
			 RequestObject clkInOut = new RequestObject();  
			 clkInOut.action = source.readString();  
			 clkInOut.requestMap = readMap(source, "");
		     return clkInOut;
		 }

		@Override
		public RequestObject[] newArray(int size) {
			return new RequestObject[size];  
		}
	};

	public static void writeMap(Map<String,String> map, Parcel out) {
		if(map != null && map.size() > 0) {
			Bundle bundle = new Bundle();
			for(Map.Entry<String, String> entry : map.entrySet()) {
				bundle.putString(entry.getKey(), entry.getValue());
			}
			final Set<String> keySet = map.keySet();
			final String[] array = keySet.toArray(new String[keySet.size()]);
			out.writeStringArray(array);
			out.writeBundle(bundle);
		}
		else {
			out.writeStringArray(new String[0]);
			out.writeBundle(Bundle.EMPTY);
		}
	}



	/**
	 * Reads a Map from a Parcel that was stored using a String array and a Bundle.
	 *
	 * @param in   the Parcel to retrieve the map from
	 * @param type the class used for the value objects in the map, equivalent to V.class before type erasure
	 * @return     a map containing the items retrieved from the parcel
	 */
	public static  HashMap<String,String> readMap(Parcel in, String type) {

		HashMap<String,String> map = new HashMap<String,String>();
		if(in != null) {
			String[] keys = in.createStringArray();
			Bundle bundle = in.readBundle();
			for(String key : keys)
				map.put(key, bundle.getString(key));
		}
		return map;
	}
}
