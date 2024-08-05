package com.aceroute.mobile.software.dialog;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TimePicker;


import com.aceroute.mobile.software.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class CustomTimePickerDialog extends TimePickerDialog {

	private final static int TIME_PICKER_INTERVAL = 5;
	private TimePicker timePicker;
	private final OnTimeSetListener callback;
	int style;
	Context context;
	public CustomTimePickerDialog(Context context, OnTimeSetListener callBack,
			int hourOfDay, int minute, boolean is24HourView,int sizeOfTextInDlg) {
		super(context, sizeOfTextInDlg, callBack, hourOfDay, minute / TIME_PICKER_INTERVAL,
				is24HourView);
		this.callback = callBack;
		this.style=sizeOfTextInDlg;
		this.context=context;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.custom_timepicker, null);
			if (view != null) {
				setView(view);
			}
			timePicker = (TimePicker) view;
			timePicker.setIs24HourView(is24HourView);
			timePicker.setCurrentHour(hourOfDay);
			timePicker.setCurrentMinute(minute);
			//timePicker.setOnTimeChangedListener(this);
		}

		// TODO Auto-generated constructor stub
	}


	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (callback != null && timePicker != null && which==DialogInterface.BUTTON_POSITIVE) {
			timePicker.clearFocus();
			callback.onTimeSet(timePicker, timePicker.getCurrentHour(),
					timePicker.getCurrentMinute() * TIME_PICKER_INTERVAL);
		}
	}



	@Override
	protected void onStop() {
	}


	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		/*LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.custom_timepicker, null);
		setView(view);*/
		try {
			Class<?> classForid = Class.forName("com.android.internal.R$id");//YD
			if(timePicker==null) {

				Field timePickerField = classForid.getField("timePicker");
				this.timePicker = (TimePicker) findViewById(timePickerField
						.getInt(null));
			}
			Field field = classForid.getField("minute");

			NumberPicker mMinuteSpinner = (NumberPicker) timePicker
					.findViewById(field.getInt(null));
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
				TypeFaceFont.overrideFonts(getContext(),mMinuteSpinner);
				TypeFaceFont.overrideFonts(getContext(),isampm);
				TypeFaceFont.overrideFonts(getContext(),hour);
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
}
