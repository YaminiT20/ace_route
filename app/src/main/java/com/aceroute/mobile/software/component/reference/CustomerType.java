package com.aceroute.mobile.software.component.reference;

public class CustomerType {
		//xml parent element idetifier
		public final static String CUSTOMERTYPE_PARENT_TAG = "ctype";

		public final static String CUSTOMER_TYPE_ID = "id";
		public final static String CUSTOMER_TYPE_NAME = "nm";
		
		public final static int TYPE = 4;
		
		public final static String ACTION_CUST_TYPE = "getcustype";
		
		private long id;
		private String nm;
		
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public String getNm() {
			return nm;
		}
		public void setNm(String nm) {
			if(nm != null && nm.length() > 30){
				nm = nm.subSequence(0, 29).toString();
			}
			this.nm = nm;
		}
}
