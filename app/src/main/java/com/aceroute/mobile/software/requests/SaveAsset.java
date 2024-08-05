package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

/**
 * Created by xelium on 6/11/16.
 */
public class SaveAsset extends RequestObject implements Parcelable{

    public SaveAsset() { ; }

    public SaveAsset(Parcel in) {
        readFromParcel(in);
    }

    String url="";
    String type ="";
    String id = "";
    String custId ="";
    String OdrId="";
    String timeStmp ="";
    String geo ="";
    String tid ="";
    String tid2 ="";
    String Status ="";
    String priorityId ="";//pid
    String cnt1 ="";
    String cnt2 ="";

    String num1 ="";
    String num2 ="";
    String num3 ="";//dtl
    String num4 ="";
    String num5 ="";
    String num6 ="";//nm

    String note1 ="";
    String note2 = "";
    String note3 ="";
    String note4 ="";
    String note5 ="";
    String note6 ="";

    String ct1 ="";
    String ct2 ="";
    String ct3 ="";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getOdrId() {
        return OdrId;
    }

    public void setOdrId(String odrId) {
        OdrId = odrId;
    }

    public String getTimeStmp() {
        return timeStmp;
    }

    public void setTimeStmp(String timeStmp) {
        this.timeStmp = timeStmp;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

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

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(String priorityId) {
        this.priorityId = priorityId;
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

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(url);
        parcel.writeString(type);
        parcel.writeString(custId);
        parcel.writeString(id);
        parcel.writeString(OdrId);
        parcel.writeString(timeStmp);
        parcel.writeString(geo);

        parcel.writeString(tid);
        parcel.writeString(tid2);
        parcel.writeString(Status);
        parcel.writeString(priorityId);

        parcel.writeString(cnt1);
        parcel.writeString(cnt2);

        parcel.writeString(num1);
        parcel.writeString(num2);
        parcel.writeString(num3);
        parcel.writeString(num4);
        parcel.writeString(num5);
        parcel.writeString(num6);

        parcel.writeString(note1);
        parcel.writeString(note2);
        parcel.writeString(note3);
        parcel.writeString(note4);
        parcel.writeString(note5);
        parcel.writeString(note6);

        parcel.writeString(ct1);
        parcel.writeString(ct2);
        parcel.writeString(ct3);
    }

    public static final Creator CREATOR = new Creator() {

        public SaveAsset createFromParcel(Parcel in) {
            return new SaveAsset(in);
        }

        public SaveAsset[] newArray(int size) {
            return new SaveAsset[size];
        }
    };


    private void readFromParcel(Parcel in) {

        url = in.readString();
        type = in.readString();
        custId = in.readString();
        id = in.readString();
        OdrId = in.readString();
        timeStmp = in.readString();
        geo = in.readString();

        tid = in.readString();
        tid2 = in.readString();
        Status = in.readString();
        priorityId = in.readString();

        cnt1 = in.readString();
        cnt2 = in.readString();

        num1 = in.readString();
        num2 = in.readString();
        num3 = in.readString();
        num4 = in.readString();
        num5 = in.readString();
        num6 = in.readString();

        note1 = in.readString();
        note2 = in.readString();
        note3 = in.readString();
        note4 = in.readString();
        note5 = in.readString();
        note6 = in.readString();

        ct1 = in.readString();
        ct2 = in.readString();
        ct3 = in.readString();
    }

}
