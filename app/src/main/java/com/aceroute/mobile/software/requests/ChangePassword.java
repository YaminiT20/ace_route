package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

/**
 * Created by root on 2/2/18.
 */

public class ChangePassword extends RequestObject implements Parcelable{

    String currentPassword;
    String newPassword;
    String retypePassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRetypePassword() {
        return retypePassword;
    }

    public void setRetypePassword(String retypePassword) {
        this.retypePassword = retypePassword;
    }


    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(currentPassword);
        parcel.writeString(newPassword);
        parcel.writeString(retypePassword);
        //parcel.writeString(odrGetDate);

    }

    public static final Creator<ChangePassword> CREATOR = new Creator<ChangePassword>() {
        public ChangePassword createFromParcel(Parcel source) {
            ChangePassword syncReq = new ChangePassword();
            syncReq.currentPassword = source.readString();
            syncReq.newPassword = source.readString();
            syncReq.retypePassword = source.readString();
            // syncReq.odrGetDate = source.readString();
            return syncReq;
        }

        @Override
        public ChangePassword[] newArray(int size) {
            return new ChangePassword[size];
        }
    };
}
