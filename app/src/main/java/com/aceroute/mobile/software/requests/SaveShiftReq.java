package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

/**
 * Created by xelium on 22/11/16.
 */
public class SaveShiftReq extends RequestObject implements Parcelable {

    public SaveShiftReq() {
        ;
    }

    public SaveShiftReq(Parcel in) {
        readFromParcel(in);
    }

    String action;
    long id;
    long tid;
    String nm;
    String tmslot;
    String dt;
    String adr;
    long lid;
    String brkslot;

    public String getTerri() {
        return terrgeo;
    }

    public void setTerri(String terrgeo) {
        this.terrgeo = terrgeo;
    }

    String terrgeo;


    public String getRecs() {
        return recs;
    }

    public void setRecs(String recs) {
        this.recs = recs;
    }

    String recs;

    public long getGaccc() {
        return gaccc;
    }

    public void setGaccc(long gaccc) {
        this.gaccc = gaccc;
    }

    long gaccc;

    public long getLid() {
        return lid;
    }

    public void setLid(long lid) {
        this.lid = lid;
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public void setAction(String action) {
        this.action = action;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public String getNm() {
        return nm;
    }

    public String getBrkslot() {
        return brkslot;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public String getTmslot() {
        return tmslot;
    }

    public void setTmslot(String tmslot) {
        this.tmslot = tmslot;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public void setBrkslot(String brkslot) {
        this.brkslot = brkslot;
    }

    public String getAddress() {
        return adr;
    }

    public void setAddress(String address) {
        this.adr = address;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(id);
        parcel.writeLong(tid);
        parcel.writeString(nm);
        parcel.writeString(tmslot);
        parcel.writeString(dt);
        parcel.writeString(action);
        parcel.writeLong(lid);
        parcel.writeString(terrgeo);
        parcel.writeString(brkslot);
        parcel.writeString(adr);
        parcel.writeString(recs);
    }

    public static final Creator CREATOR = new Creator() {

        public SaveShiftReq createFromParcel(Parcel in) {
            return new SaveShiftReq(in);
        }

        public SaveShiftReq[] newArray(int size) {
            return new SaveShiftReq[size];
        }
    };


    private void readFromParcel(Parcel in) {
        id = in.readLong();
        tid = in.readLong();
        nm = in.readString();
        tmslot = in.readString();
        dt = in.readString();
        action = in.readString();
        lid = in.readLong();
        terrgeo = in.readString();
        brkslot = in.readString();
        adr = in.readString();
        recs = in.readString();
    }
}
