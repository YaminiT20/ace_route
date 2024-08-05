package com.aceroute.mobile.software.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.aceroute.mobile.software.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xelium on 30/11/16.
 */
public class CustomTimePicker extends TimePicker {

    public final static int TIME_PICKER_INTERVAL = 5;

    public CustomTimePicker(Context context) {
        super(context);
    }

    public CustomTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public CustomTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setMinute(int minute) {
        super.setMinute(minute/TIME_PICKER_INTERVAL);
    }

    @Override
    public void setCurrentMinute(Integer currentMinute) {
        super.setCurrentMinute(currentMinute/TIME_PICKER_INTERVAL);
    }

    public int getActualMinute() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getMinute()*TIME_PICKER_INTERVAL;
        }else {
            return  getCurrentMinute()*TIME_PICKER_INTERVAL;
        }
    }



    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");//YD
            Field timePickerField = classForid.getField("timePicker");
            Field field = classForid.getField("minute");

            NumberPicker mMinuteSpinner = (NumberPicker) findViewById(field.getInt(null));

            mMinuteSpinner.setMinValue(0);
            mMinuteSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            List<String> displayedValues = new ArrayList<String>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            mMinuteSpinner.setDisplayedValues(displayedValues
                    .toArray(new String[0]));


            Field fieldampm = classForid.getField("amPm");


            NumberPicker isampm = null;
            try{
                isampm= (NumberPicker) findViewById(fieldampm.getInt(null));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


            Field fieldhour = classForid.getField("hour");

            NumberPicker hour = null;
            try{
                hour= (NumberPicker)findViewById(fieldhour.getInt(null));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            Class<?> numberPickerClass = null;
            try {
                numberPickerClass = Class.forName("android.widget.NumberPicker");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            Field selectionDivider = null;
            try {
                selectionDivider = numberPickerClass.getDeclaredField("mSelectionDivider");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

            try {
                selectionDivider.setAccessible(true);
                selectionDivider.set(mMinuteSpinner, getContext().getResources().getDrawable(
                        R.drawable.picker_view_holo));
                selectionDivider.set(isampm, getContext().getResources().getDrawable(
                        R.drawable.picker_view_holo));
                selectionDivider.set(hour, getContext().getResources().getDrawable(
                        R.drawable.picker_view_holo));
                setNumberPickerTextColor(getContext(),mMinuteSpinner, Color.BLACK);
                setNumberPickerTextColor(getContext(),isampm, Color.BLACK);
                setNumberPickerTextColor(getContext(),hour, Color.BLACK);
                //TypeFaceFont.ChangeFontsWithSimilarSize(getContext(),mMinuteSpinner,22 + PreferenceHandler.getCurrrentFontSzForApp(getContext()));
               // TypeFaceFont.ChangeFontsWithSimilarSize(getContext(),isampm,22 + PreferenceHandler.getCurrrentFontSzForApp(getContext()));
              //  TypeFaceFont.ChangeFontsWithSimilarSize(getContext(),hour,22 + PreferenceHandler.getCurrrentFontSzForApp(getContext()));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean setNumberPickerTextColor(Context context,NumberPicker numberPicker, int color) {
                final int count = numberPicker.getChildCount();
                for (int i = 0; i < count; i++) {
                        View child = numberPicker.getChildAt(i);
                        if (child instanceof EditText) {
                                try {
                                        Field selectorWheelPaintField = numberPicker.getClass()
                                                .getDeclaredField("mSelectorWheelPaint");
                                        selectorWheelPaintField.setAccessible(true);
                                        ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                                        ((Paint) selectorWheelPaintField.get(numberPicker)).setTextSize(convertSpToPixels(22 + PreferenceHandler.getCurrrentFontSzForApp(context),context));
                                        ((Paint) selectorWheelPaintField.get(numberPicker)).setTypeface(Typeface.createFromAsset(context.getAssets(), "www/fonts/lato/lato-regular-webfont.ttf"));

                                        ((EditText) child).setTextColor(color);
                                        ((EditText) child).setTextSize(22 + PreferenceHandler.getCurrrentFontSzForApp(context));
                                        ((EditText) child).setTypeface(Typeface.createFromAsset(context.getAssets(), "www/fonts/lato/lato-regular-webfont.ttf"));
                                        numberPicker.invalidate();
                                        return true;
                                    } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {

                                    }
                            }
                    }
                return false;
    }


    public  int convertSpToPixels(float sp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
        return px;
    }
}
