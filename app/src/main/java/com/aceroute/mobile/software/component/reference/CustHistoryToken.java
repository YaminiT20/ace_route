package com.aceroute.mobile.software.component.reference;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xelium on 9/12/16.
 */
public class CustHistoryToken {

    @SerializedName("cid")
    long CustId;
    String CustToken;
    String Path;


    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    public String getCustToken() {
        return CustToken;
    }

    public void setCustToken(String custToken) {
        CustToken = custToken;
    }

    public long getCustId() {
        return CustId;
    }

    public void setCustId(long custId) {
        CustId = custId;
    }
}
