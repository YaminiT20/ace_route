package com.aceroute.mobile.software.dialog;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aceroute.mobile.software.utilities.PreferenceHandler;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TypeFaceFont {
	
	private static Typeface customTypeface;
	private static Typeface customTypefaceSan_sarif;

	public static Typeface getCustomTypeface(Context context) {
	    if(customTypeface == null){
	        //Only do this once for each typeface used
	        //or we will leak unnecessary memory.
	        customTypeface = Typeface.createFromAsset(context.getApplicationContext().getAssets(), "www/fonts/lato/lato-regular-webfont.ttf");
	    }
	    return customTypeface;
	}
	
	public static Typeface getCustomTypefaceSan_sarif(Context context) {
	    if(customTypefaceSan_sarif == null){
	        //Only do this once for each typeface used
	        //or we will leak unnecessary memory.
	    	customTypefaceSan_sarif = Typeface.createFromAsset(context.getApplicationContext().getAssets(), "ARIAL.TTF");
	    }
	    return customTypefaceSan_sarif;
	}

	public static void overrideFonts(final Context context, final View v) {
		try {
			if (v instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) v;
				for (int i = 0; i < vg.getChildCount(); i++) {
					View child = vg.getChildAt(i);
					overrideFonts(context, child);
				}
			} else if (v instanceof TextView) {
				((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "www/fonts/lato/lato-regular-webfont.ttf"));
			}
			else if (v instanceof Button) {
				((Button) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "www/fonts/lato/lato-regular-webfont.ttf"));
			}
		} catch (Exception e) {
		}
	}

//KB code to font style and font size for similar size text
	public static void ChangeFontsWithSimilarSize(final Context context, final View v,final int size) {
		try {
			if (v instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) v;
				for (int i = 0; i < vg.getChildCount(); i++) {
					View child = vg.getChildAt(i);
					ChangeFontsWithSimilarSize(context, child,size);
				}
			} else if (v instanceof TextView) {
				((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "www/fonts/lato/lato-regular-webfont.ttf"));
				((TextView) v).setTextSize(size + (PreferenceHandler.getCurrrentFontSzForApp(context)));
			} else if (v instanceof EditText) {
				((EditText) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "www/fonts/lato/lato-regular-webfont.ttf"));
				((EditText) v).setTextSize(size + (PreferenceHandler.getCurrrentFontSzForApp(context)));
			}
			else if (v instanceof Button) {
				((Button) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "www/fonts/lato/lato-regular-webfont.ttf"));
				((Button) v).setTextSize(size + (PreferenceHandler.getCurrrentFontSzForApp(context)));
			}
		} catch (Exception e) {
		}
	}

	public static void overrideFontsMenu(final Context context, final View v) {
		try {
			if (v instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) v;
				for (int i = 0; i < vg.getChildCount(); i++) {
					View child = vg.getChildAt(i);
					overrideFontsMenu(context, child);
				}
			} else if (v instanceof TextView) {
				((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "www/fonts/lato/lato-regular-webfont.ttf"));
				int val = PreferenceHandler.getCurrrentFontSzForApp(context);
				((TextView) v).setTextSize(21 + val);
				((TextView) v).setPadding( 35 +val, 20 +val, 35 +val, 20 +val);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {

		final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Map<String, Typeface> newMap = new HashMap<String, Typeface>();
			newMap.put("serif", customFontTypeface);
			try {
				final Field staticField = Typeface.class
						.getDeclaredField("sSystemFontMap");
				staticField.setAccessible(true);
				staticField.set(null, newMap);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			try {
				final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
				defaultFontTypefaceField.setAccessible(true);
				defaultFontTypefaceField.set(null, customFontTypeface);
			} catch (Exception e) {
				Log.e("Error", "Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride);
			}
		}
	}

}
