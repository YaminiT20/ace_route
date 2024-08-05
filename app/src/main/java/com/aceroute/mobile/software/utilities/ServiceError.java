package com.aceroute.mobile.software.utilities;

public enum ServiceError {
	// Int Values
	ERROR_CODE_URL("2"),
	ERROR_CODE_UNKNOWN_ERROR("4"),
	ERROR_CODE_RESPONSE_ERROR("6"),
	RESPONSE_SYNC_PAUSED("10"),
	// string values
	NO_ACTION_REQUIRED("No Action Required"),
	LOGIN_FAIL("loginFail"),
	NO_DATA("No Data Found"),
	NO_INTERNET_CONNECTION("No Internet Connection"),
	IO_ERROR("Input/Output Error"),
	HTTP_REQUEST_FAILED("Http Req Failed"),
	UNKNOWN("unknown error");
	
	
	
	
	
	private int value;
	private ServiceError(int value) {
         this.value = value;
    }
	public static int getEnumVal(ServiceError name)
	{
		return name.value;
	}
	
	public String strval;
	private ServiceError(String strval) {
        this.strval = strval;
   }
	public static String getEnumValstr(ServiceError name)
	{
		return name.strval;
	}

};