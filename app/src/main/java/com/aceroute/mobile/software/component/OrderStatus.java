package com.aceroute.mobile.software.component;

public class OrderStatus { //YD CURRENTLY NOT USING ANYWHERE*************
		//xml parent element idetifier
		public final static String ORDER_PARENT_TAG = "osrv";

		public final static String ORDER_ID = "id";
		public final static String ORDER_STATUS_NAME = "name";
		public final static String ORDER_STATUS_VALUE = "value";
		public final static String ORDER_STATUS_DTLIST = "dtlist";
		public final static String ORDER_STATUS_GEO = "geo";
		public final static String NEW_ORDER_STATUS = "0";
		
		public final static int TYPE = 104;
		
		public final static String ACTION_SAVE_ORDER_STATUS = "saveorderstatus";
		// for future use
		public final static String ACTION_DELETE_ORDER_STATUS = "deleteorderstatus";
		public final static String ACTION_UPDATE_ORDER_STATUS = "updateorderstatus";
		public static final String ACTION_GET_ORDER_STATUS = "getorderstatus";
		
		private long id;
		private String name;
		private String value;
		private String dtlist;
		private String geo;
		
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getDtlist() {
			return dtlist;
		}
		public void setDtlist(String dtlist) {
			this.dtlist = dtlist;
		}
		public String getGeo() {
			return geo;
		}
		public void setGeo(String geo) {
			this.geo = geo;
		}
		
		
}
