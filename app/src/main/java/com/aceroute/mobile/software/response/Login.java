package com.aceroute.mobile.software.response;

public class Login {
    //xml parent element idetifier
    public final static String LOGIN_RES_ID = "rid";
    public final static String LOGIN_TECH_NAME = "resnm";
    public final static String LOGIN_GPS_SYNC = "gpssync";
    public final static String LOGIN_BIZ_HRS = "bizhr";
    public final static String LOGIN_EDN = "edn";
    public final static String LOGIN_SMS_TEMP_ZERO = "smstmpl0";
    public final static String LOGIN_SMS_TEMP_ONE = "smstmpl1";
    public final static String LOGIN_SMS_TEMP_TWO = "smstmpl2";
    public final static String LOGIN_SMS_TEMP_THREE = "smstmpl3";
    public final static String LOGIN_SMS_TEMP_FOUR = "smstmpl4";
    public final static String LOGIN_MAP_COUNTRY = "mapctry";
    public final static String LOGIN_MAP_REGION = "maprgn";
    public final static String LOGIN_MTOKEN = "token";
    public final static String LOGIN_WORKERWEEK = "wrkwk";
    public final static String LOGIN_SERVICE_HEADING = "slbl";
    public final static String LOGIN_PART_HEADING = "plbl";
    public final static String LOGIN_OLPM_UICONFIG = "uiconfig";
    public final static String LOGIN_SHFTLOK = "shfdtlock";
    public final static String LOGIN_NSPID = "nspid";// YD using for pubnub
    public final static String LOGIN_OFFICE_GEO = "geo";
    public final static String LOGIN_SPEED = "spd";
    public final static String ACTION_ORDER = "getorders";
    public final static String ACTION_SAVE_ORDER = "saveorder";
    public final static String ACTION_SAVE_ORDER_FLD = "saveorderfld";
    public static final String CHANGE_PASSWORD = "updpcd";
    public static final String LOCCHAN = "locchg";

    private long rid = 0;
    private String gpssync = null;
    private String resnm = null;

    public String getResnm() {
        return resnm;
    }

    public void setResnm(String resnm) {
        this.resnm = resnm;
    }

    private String bizhr = null;
    private int edn = -1;
    private String workerWeek = null;
    private String smstmpl0 = null;
    private String smstmpl1 = null;
    private String smstmpl2 = null;
    private String smstmpl3 = null;
    private String smstmpl4 = null;
    private String mapctry = null;
    private String maprgn = null;
    private String token = null;
    private String loginError = "0";
    private String serviceHead = null;
    private String partHead = null;
    private String uiConfig = null;
    private long nspid;
    private String officeGeo = null;
    private int speed = 0;
    private String shftlock = null;
    private String locchan = null;

    public String getLoginError() {
        return loginError;
    }

    public void setLoginError(String loginError) {
        this.loginError = loginError;
    }

    public String getWorkerWeek() {
        return workerWeek;
    }

    public void setWorkerWeek(String workerWeek) {
        this.workerWeek = workerWeek;
    }

    public long getRid() {
        return rid;
    }

    public void setRid(long rid) {
        this.rid = rid;
    }



    public String getGpssync() {
        return gpssync;
    }

    public void setGpssync(String gpssync) {
        this.gpssync = gpssync;
    }

    public String getBizhr() {
        return bizhr;
    }

    public void setBizhr(String bizhr) {
        this.bizhr = bizhr;
    }

    public int getEdn() {
        return edn;
    }

    public void setEdn(int edn) {
        this.edn = edn;
    }

    public String getSmstmpl0() {
        return smstmpl0;
    }

    public void setSmstmpl0(String smstmpl0) {
        this.smstmpl0 = smstmpl0;
    }

    public String getSmstmpl1() {
        return smstmpl1;
    }

    public void setSmstmpl1(String smstmpl1) {
        this.smstmpl1 = smstmpl1;
    }

    public String getSmstmpl2() {
        return smstmpl2;
    }

    public void setSmstmpl2(String smstmpl2) {
        this.smstmpl2 = smstmpl2;
    }

    public String getSmstmpl3() {
        return smstmpl3;
    }

    public void setSmstmpl3(String smstmpl3) {
        this.smstmpl3 = smstmpl3;
    }

    public String getSmstmpl4() {
        return smstmpl4;
    }

    public void setSmstmpl4(String smstmpl4) {
        this.smstmpl4 = smstmpl4;
    }

    public String getMapctry() {
        return mapctry;
    }

    public void setMapctry(String mapctry) {
        this.mapctry = mapctry;
    }

    public String getMaprgn() {
        return maprgn;
    }

    public void setMaprgn(String maprgn) {
        this.maprgn = maprgn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getServiceHead() {
        return serviceHead;
    }

    public void setServiceHead(String serviceHead) {
        this.serviceHead = serviceHead;
    }

    public String getPartHead() {
        return partHead;
    }

    public void setPartHead(String partHead) {
        this.partHead = partHead;
    }

    public String getUiConfig() {
        return uiConfig;
    }

    public void setUiConfig(String uiConfig) {
        this.uiConfig = uiConfig;
    }

    public long getNspid() {
        return nspid;
    }

    public void setNspid(long nspid) {
        this.nspid = nspid;
    }

    public String getOfficeGeo() {
        return officeGeo;
    }

    public void setOfficeGeo(String officeGeo) {
        this.officeGeo = officeGeo;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setshftlock(String shftlock) {
        this.shftlock = shftlock;
    }

    public String getShftlock() {
        return shftlock;
    }

    public String getLocChan() {
        return locchan;
    }

    public void setLocChan(String locchan) {
        this.locchan = locchan;
    }
}
