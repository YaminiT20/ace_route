package com.aceroute.mobile.software.dialog;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.Utilities;

/**
 * Created by YD on 4/20/16 to handle custom number picker.
 */
public class MyNumberPickerDialog extends NumberPicker {

    Context mcontext;
    private EditText editText;
    int sizeDialogStyleID = Utilities.getDialogTextSize(getContext());
    public MyNumberPickerDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        mcontext =context;
        editText.setTextSize(22 + PreferenceHandler.getCurrrentFontSzForApp(mcontext));
        editText.setTextColor(Color.BLACK);
        TypeFaceFont.overrideFonts(getContext(),editText);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }

    private void updateView(View view) {
        if(view instanceof EditText){
            editText =  ((EditText) view);
            editText.setTextSize(22 + PreferenceHandler.getCurrrentFontSzForApp(getContext()));
            TypeFaceFont.overrideFonts(getContext(),editText);
           // ((EditText) view).setTextSize(20);
           // ((EditText) view).setTextColor(Color.parseColor("#333333"));
        }else if(view instanceof TextView){
            TextView text =  ((TextView) view);
            text.setTextSize(22 + PreferenceHandler.getCurrrentFontSzForApp(getContext()));
            TypeFaceFont.overrideFonts(getContext(),text);
        }
    }


}
