package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class SaveFormRequest extends RequestObject implements Parcelable {


    public static final Creator CREATOR = new Creator() {

        public SaveFormRequest createFromParcel(Parcel in) {
            return new SaveFormRequest(in);
        }

        public SaveFormRequest[] newArray(int size) {
            return new SaveFormRequest[size];
        }
    };
    String action;
    String id;
    String oid;
    String ftid;
    String fdata;
    String stmp;
    String custId;
    String frmkey;

    public String getFrmkey() {
        return frmkey;
    }

    public void setFrmkey(String frmkey) {
        this.frmkey = frmkey;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public SaveFormRequest() {
        ;
    }

    public SaveFormRequest(Parcel in) {
        readFromParcel(in);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getFtid() {
        return ftid;
    }

    public void setFtid(String ftid) {
        this.ftid = ftid;
    }

    public String getFdata() {
        return fdata;
    }

    public void setFdata(String fdata) {
        this.fdata = fdata;
    }

    public String getStmp() {
        return stmp;
    }

    public void setStmp(String stmp) {
        this.stmp = stmp;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(action);
        parcel.writeString(oid);
        parcel.writeString(ftid);
        parcel.writeString(fdata);
        parcel.writeString(stmp);
        parcel.writeString(custId);
        parcel.writeString(frmkey);
    }

    private void readFromParcel(Parcel in) {
        id = in.readString();
        action = in.readString();
        oid = in.readString();
        ftid = in.readString();
        fdata = in.readString();
        stmp = in.readString();
        custId = in.readString();
        frmkey = in.readString();
    }
}

