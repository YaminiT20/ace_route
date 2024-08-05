package com.aceroute.mobile.software.validation;

import com.google.gson.annotations.SerializedName;

public class VactivityList {
	@SerializedName("activitylist")
	AppActivity[] activitylist;

	public AppActivity[] getActivitylist() {
		return activitylist;
	}

	public void setActivitylist(AppActivity[] activitylist) {
		this.activitylist = activitylist;
	}
}
