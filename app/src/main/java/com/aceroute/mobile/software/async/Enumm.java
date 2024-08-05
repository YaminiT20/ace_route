package com.aceroute.mobile.software.async;

public enum Enumm {
	
	LOGIN_REQUEST(101),
	BASE_REQUEST(102);
	
	private int value;
	private Enumm(int value) {
         this.value = value;
    }
	public static int getEnumVal(Enumm name)
	{
		return name.value;
	}

};
