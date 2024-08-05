package com.aceroute.mobile.software.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.aceroute.mobile.software.BaseTabActivity;

public class BaseFragment extends Fragment {

    static  public BaseTabActivity mActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = (BaseTabActivity) this.getActivity();
    }

    public boolean onBackPressed() {
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActivity = (BaseTabActivity) this.getActivity();
    }
}