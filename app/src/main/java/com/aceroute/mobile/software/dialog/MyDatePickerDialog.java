package com.aceroute.mobile.software.dialog;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.utilities.PreferenceHandler;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;

public class MyDatePickerDialog extends DatePickerDialog implements DialogInterface.OnClickListener {
	private final Context context;
	private Date maxDate;
	private Date minDate;
	private boolean isAllowedBefore;
	OnDateSetListener mCallBack;
	DatePickerInterface mOptFrag;
	DatePicker dpicker;
	public MyDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth, boolean isAllowedBefore,DatePickerInterface optFrag,int sizeOfTextInDlg) {
	    super(context,sizeOfTextInDlg, callBack, year, monthOfYear, dayOfMonth);//size of the dialog text is changed using style

		this.context = context;
		mOptFrag =optFrag;
		mCallBack = callBack;
	    this.isAllowedBefore=isAllowedBefore;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		init(year, monthOfYear, dayOfMonth);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.custom_datepicker, null);
			if (view != null) {
				setView(view);
			}
			dpicker = (DatePicker) view;
			dpicker.init(year, monthOfYear, dayOfMonth, this);
		}

	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		try {
			Class<?> internalRID = null;
			try {
				internalRID = Class.forName("com.android.internal.R$id");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			if(dpicker==null) {
				Field timePickerField = internalRID.getField("datePicker");
				this.dpicker = (DatePicker) findViewById(timePickerField
						.getInt(null));
			}

			Field month = null;
			try {
				month = internalRID.getField("month");
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}

			NumberPicker npMonth = null;
			try {
				npMonth = (NumberPicker)dpicker.findViewById(month.getInt(null));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			Field day = null;
			try {
				day = internalRID.getField("day");
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}

			NumberPicker npDay = null;
			try {
				npDay = (NumberPicker)dpicker.findViewById(day.getInt(null));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			Field year = null;
			try {
				year = internalRID.getField("year");
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}

			NumberPicker npYear = null;
			try {
				npYear = (NumberPicker)dpicker.findViewById(year.getInt(null));
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
				selectionDivider.set(npMonth, getContext().getResources().getDrawable(
						R.drawable.picker_view_holo));
				selectionDivider.set(npDay, getContext().getResources().getDrawable(
						R.drawable.picker_view_holo));
				selectionDivider.set(npYear, getContext().getResources().getDrawable(
						R.drawable.picker_view_holo));
				setNumberPickerTextColor(getContext(),npMonth, Color.BLACK);
				setNumberPickerTextColor(getContext(),npDay, Color.BLACK);
				setNumberPickerTextColor(getContext(),npYear, Color.BLACK);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (Resources.NotFoundException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}catch (Exception e){
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

	private void init(int year, int monthOfYear, int dayOfMonth){
	    Calendar cal = Calendar.getInstance();

	    cal.set(year, monthOfYear, dayOfMonth);
	    minDate = cal.getTime();

	    cal.set(year, Calendar.JANUARY, 1);
	    maxDate=cal.getTime();

		cal.set(year, monthOfYear, dayOfMonth);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which==DialogInterface.BUTTON_POSITIVE){
			BaseTabActivity.buttonClickedId=1;
			if(dpicker==null) {
				super.onClick(dialog, which);
			}else{
				mCallBack.onDateSet(dpicker,  dpicker.getYear(),  dpicker.getMonth(),  dpicker.getDayOfMonth());
			}
		}
		else{
			BaseTabActivity.buttonClickedId=0;
			mOptFrag.onCancelledBtn();
		}
	}

	//YD called when date is changed
	public void onDateChanged (DatePicker dtView, int year, int month, int day){
	    Calendar cal1 = Calendar.getInstance();
	    Date currentDate = cal1.getTime();
	    
	    Calendar resetCal = Calendar.getInstance();
	    resetCal.set(year, month, day);

//	    final Calendar resetCal = cal;
	    
	   
	    if(!isAllowedBefore &&
	    		resetCal.before(cal1) ){
	    	dtView.updateDate(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), cal1.get(Calendar.DAY_OF_MONTH));
	    }
//	    else if(resetCal.before(currentDate)){
//	        cal.setTime(resetCal.getTime());
//	        view.updateDate(resetCal.get(Calendar.YEAR), resetCal.get(Calendar.MONTH), resetCal.get(Calendar.DAY_OF_MONTH));
//	    } 
//	    else
//	    {
//	    	cal.setTime(resetCal.getTime());
//	        view.updateDate(resetCal.get(Calendar.YEAR), resetCal.get(Calendar.MONTH), resetCal.get(Calendar.DAY_OF_MONTH));
//	    }
	}


	public void setMaxDate(Date date){
	    this.maxDate = date;
	}

	public void setMinDate(Date date){
	    this.minDate = date;
	}



}
