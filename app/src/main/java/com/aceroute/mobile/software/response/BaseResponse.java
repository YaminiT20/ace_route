package com.aceroute.mobile.software.response;

public class BaseResponse {
    public final static String LOGIN_NSP = "nsp";
    public final static String LOGIN_URL = "url";
    public final static String LOGIN_SUBKEY = "subkey";
    private String baseError = "0";

    public String getNsp() {
        return nsp;
    }

    public void setNsp(String nsp) {
        this.nsp = nsp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSubkey() {
        return subkey;
    }

    public void setSubkey(String subkey) {
        this.subkey = subkey;
    }

    private String nsp = null;
    private String url = null;
    private String subkey = null;

    public String getBaseError() {
        return baseError;
    }

    public void setBaseError(String baseError) {
        this.baseError = baseError;
    }
}
