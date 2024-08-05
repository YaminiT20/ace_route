package com.aceroute.mobile.software.async;

import android.content.Intent;

import com.aceroute.mobile.software.http.Response;

public interface RespCBandServST {
	
	public void ServiceStarter(RespCBandServST activity, Intent intent);
	void setResponseCallback(String response, Integer reqId);
	void setResponseCBActivity(Response response);
}
