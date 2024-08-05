package com.aceroute.mobile.software.component.reference;

import com.google.gson.annotations.SerializedName;

/**
 * YD No need to use @SerializedName("some label") because we are currently using the same name as the tag coming from server, using gson to save to pref
 * Created by xelium on 6/4/16.
 */
public class AssetLabel {
    public final static String PARENT_TAG  = "lbls";

    public final static String ASSET_LBL_TID  = "tid";
    public final static String ASSET_LBL_TID2  = "tid2";
    public final static String ASSET_LBL_CONT1  = "cnt1";
    public final static String ASSET_LBL_CONT2  = "cnt2";
    public final static String ASSET_LBL_STAT  = "stat";
    public final static String ASSET_LBL_PID  = "pid";
    public final static String ASSET_LBL_NUM1  = "num1";
    public final static String ASSET_LBL_NUM2  = "num2";
    public final static String ASSET_LBL_NUM3  = "num3";
    public final static String ASSET_LBL_NUM4  = "num4";
    public final static String ASSET_LBL_NUM5  = "num5";
    public final static String ASSET_LBL_NUM6  = "num6";
    public final static String ASSET_LBL_NOTE1  = "note1";
    public final static String ASSET_LBL_NOTE2  = "note2";
    public final static String ASSET_LBL_NOTE3  = "note3";
    public final static String ASSET_LBL_NOTE4  = "note4";
    public final static String ASSET_LBL_NOTE5  = "note5";
    public final static String ASSET_LBL_NOTE6  = "note6";
    public final static String ASSET_LBL_CT1  = "ct1";
    public final static String ASSET_LBL_CT2  = "ct2";
    public final static String ASSET_LBL_CT3  = "ct3";
    public final static String ASSET_REC_GEO  = "recgeo";


    public final static String ACTION_LABELS = "getlabels";

    @SerializedName("tid")
    String tid;
    String tid2;
    String cnt1;
    String cnt2;
    String pid;
    String stat;
    String num1;
    String num2;
    String num3;
    String num4;
    String num5;
    String num6;
    String note1;
    String note2;
    String note3;
    String note4;
    String note5;
    String note6;
    String ct1;
    String ct2;
    String ct3;

    public String getRecGeo() {
        return recGeo;
    }

    public void setRecGeo(String recGeo) {
        this.recGeo = recGeo;
    }

    String recGeo;

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTid2() {
        return tid2;
    }

    public void setTid2(String tid2) {
        this.tid2 = tid2;
    }

    public String getCnt1() {
        return cnt1;
    }

    public void setCnt1(String cnt1) {
        this.cnt1 = cnt1;
    }

    public String getCnt2() {
        return cnt2;
    }

    public void setCnt2(String cnt2) {
        this.cnt2 = cnt2;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getNum1() {
        return num1;
    }

    public void setNum1(String num1) {
        this.num1 = num1;
    }

    public String getNum2() {
        return num2;
    }

    public void setNum2(String num2) {
        this.num2 = num2;
    }

    public String getNum3() {
        return num3;
    }

    public void setNum3(String num3) {
        this.num3 = num3;
    }

    public String getNum4() {
        return num4;
    }

    public void setNum4(String num4) {
        this.num4 = num4;
    }

    public String getNum5() {
        return num5;
    }

    public void setNum5(String num5) {
        this.num5 = num5;
    }

    public String getNum6() {
        return num6;
    }

    public void setNum6(String num6) {
        this.num6 = num6;
    }

    public String getNote1() {
        return note1;
    }

    public void setNote1(String note1) {
        this.note1 = note1;
    }

    public String getNote2() {
        return note2;
    }

    public void setNote2(String note2) {
        this.note2 = note2;
    }

    public String getNote3() {
        return note3;
    }

    public void setNote3(String note3) {
        this.note3 = note3;
    }

    public String getNote4() {
        return note4;
    }

    public void setNote4(String note4) {
        this.note4 = note4;
    }

    public String getNote5() {
        return note5;
    }

    public void setNote5(String note5) {
        this.note5 = note5;
    }

    public String getNote6() {
        return note6;
    }

    public void setNote6(String note6) {
        this.note6 = note6;
    }

    public String getCt1() {
        return ct1;
    }

    public void setCt1(String ct1) {
        this.ct1 = ct1;
    }

    public String getCt2() {
        return ct2;
    }

    public void setCt2(String ct2) {
        this.ct2 = ct2;
    }

    public String getCt3() {
        return ct3;
    }

    public void setCt3(String ct3) {
        this.ct3 = ct3;
    }

}
