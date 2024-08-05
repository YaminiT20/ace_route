package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

import java.util.ArrayList;

public class SaveFormListRequest extends RequestObject implements Parcelable {

    String url;
    String type;
    ArrayList<SaveFormRequest> dataObj;
    public SaveFormListRequest() {
        ;
    }

    public SaveFormListRequest(Parcel in) {
        //readFromParcel(in);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<SaveFormRequest> getDataObj() {
        return dataObj;
    }

    public void setDataObj(ArrayList<SaveFormRequest> dataObj) {
        this.dataObj = dataObj;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(type);
        dest.writeTypedList(dataObj);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator CREATOR = new Creator() {

        public SaveFormListRequest createFromParcel(Parcel source) {
            SaveFormListRequest reg = new SaveFormListRequest(source);
            reg.url = source.readString();
            reg.type = source.readString();
            reg.dataObj = source.createTypedArrayList(SaveFormRequest.CREATOR);
            return reg;
        }

        public Save_DeletePartRequest[] newArray(int size) {
            return new Save_DeletePartRequest[size];
        }
    };
}