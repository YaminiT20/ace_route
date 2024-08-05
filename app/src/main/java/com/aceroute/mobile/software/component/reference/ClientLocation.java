package com.aceroute.mobile.software.component.reference;

public class ClientLocation {//yd NOT USING ANYWHERE IN CODE 21-11-2016
	
	//xml parent element idetifier
	public final static String CLIENT_PARENT_TAG = "loc";
	
	public final static String CLIENT_ID = "id";
	public final static String CLIENT_GEO = "geo";
	
	public final static int TYPE = 3;
	
	public final static String ACTION_CLIENT_LOCATION = "getclientsite_OLD"; //YD maing old because now we have compClientsiteLocation call for getclientsite
	
	private long id;
	private Geo geo;
	
	public class Geo{
		
		public final static String CLIENT_LAT = "lat";
		public final static String CLIENT_LONG = "longi";
		
		private long latitude;
		private long longitude;
		
		public long getLatitude() {
			return latitude;
		}
		public void setLatitude(long latitude) {
			this.latitude = latitude;
		}
		public long getLongitude() {
			return longitude;
		}
		public void setLongitude(long longitude) {
			this.longitude = longitude;
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Geo getGeo() {
		return geo;
	}

	public void setGeo(Geo geo) {
		this.geo = geo;
	}
}
