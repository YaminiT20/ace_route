package com.aceroute.mobile.software.dialog;

import android.widget.SeekBar;

import org.json.JSONException;

/**
 * Created by yash on 4/2/16.
 */
public interface MySeekDialogInterface {
    public void onPositiveClick() throws JSONException;
    public void onNegativeClick();
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) ;
    public void onStartTrackingTouch(SeekBar seekBar) ;
    public void onStopTrackingTouch(SeekBar seekBar) ;
}
