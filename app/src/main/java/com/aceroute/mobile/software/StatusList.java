package com.aceroute.mobile.software;

import android.view.LayoutInflater;
import android.view.View;

public interface StatusList {//YD this has been used because need to keep two class to make status dialog ied listItem and statusList
	public int getViewType();
    public View getView(LayoutInflater inflater, View convertView);
}