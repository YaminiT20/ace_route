package com.aceroute.mobile.software.offline;


import android.app.Activity;
import android.os.Bundle;

import com.aceroute.mobile.software.R;



public class InteractionMapActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_map);
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }
    
    @Override
    public void onPause() {
        super.onPause();

    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
