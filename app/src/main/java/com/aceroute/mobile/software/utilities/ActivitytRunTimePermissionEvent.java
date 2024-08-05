package com.aceroute.mobile.software.utilities;

public class ActivitytRunTimePermissionEvent {

    int requestCode;
    String[] permissions;
    int[] grantResults;
    String activityName;


    public ActivitytRunTimePermissionEvent(String activityName, int requestCode, String[] permissions, int[] grantResults) {
        this.requestCode = requestCode;
        this.permissions = permissions;
        this.grantResults = grantResults;
        this.activityName=activityName;

    }
    public String[] getPermissionData(){
        return permissions;
    }

    public int[] getPermissionResultData(){
        return grantResults;
    }

    public int getResultCode()
    {
        return requestCode;
    }

    public String getCallBackName()
    {
        return activityName;
    }
}
