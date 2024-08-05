package com.aceroute.mobile.software.validation;

import com.google.gson.annotations.SerializedName;

public class AppActivity {

	@SerializedName("name")
	String activityName;

	@SerializedName("controls")
	Vcontrols[] controllist;
	
	
	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	

	public Vcontrols[] getControllist() {
		return controllist;
	}

	public void setControllist(Vcontrols[] controllist) {
		this.controllist = controllist;
	}
	
}
