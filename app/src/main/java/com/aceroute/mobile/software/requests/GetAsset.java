package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

/**
 * Created by xelium on 4/12/16.
 */
public class GetAsset extends RequestObject implements Parcelable{

        String url;
        String cid;


        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
        public String getCid() {
            return cid;
        }
        public void setCid(String oid) {
            this.cid = oid;
        }

        @Override
        public int describeContents() {
            // TODO Auto-generated method stub
            return 0;
        }
        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeString(url);
            parcel.writeString(cid);

        }

        public static final Creator<GetAsset> CREATOR = new Creator<GetAsset>() {
            public GetAsset createFromParcel(Parcel source) {
                GetAsset cusCont = new GetAsset();
                cusCont.url = source.readString();
                cusCont.cid = source.readString();
                return cusCont;
            }

            @Override
            public GetAsset[] newArray(int size) {
                return new GetAsset[size];
            }
        };


    }
