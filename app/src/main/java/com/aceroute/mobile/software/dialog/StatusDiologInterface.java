package com.aceroute.mobile.software.dialog;

import org.json.JSONException;

public interface StatusDiologInterface {

	public void onPositiveClick(String btn) throws JSONException;
	public void onNegativeClick(String btn);
}
