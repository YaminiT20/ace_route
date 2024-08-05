package com.aceroute.mobile.software.utilities;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aceroute.mobile.software.R;

import java.util.ArrayList;

/**
 * Created by xelium on 11/10/16.
 */
public class ArrayAdapterwithRadiobutton extends ArrayAdapter<String> {

        String selecteditem="";
        public ArrayAdapterwithRadiobutton(Context context, int resource, String[] objects, String selecteditem) {
            super(context, resource, objects);
            this.selecteditem=selecteditem;
        }

    public ArrayAdapterwithRadiobutton(Context context, int resource, ArrayList<String> objects, String selecteditem) {
        super(context, resource, objects);
        this.selecteditem=selecteditem;
    }


    @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            try {
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                if(text1.getText().toString().toLowerCase().equals(selecteditem.toLowerCase())) {
                    text1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apptheme_btn_radio_on_holo_light, 0);
                }else{
                    text1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.apptheme_btn_radio_off_holo_light, 0);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return view;
        }

}
