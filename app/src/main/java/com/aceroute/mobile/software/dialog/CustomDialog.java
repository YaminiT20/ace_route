package com.aceroute.mobile.software.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.IActionOKCancel;
import com.aceroute.mobile.software.utilities.Utilities;


/**
 * @author
 * 
 * 
 *         Alert dialog setArguments bundle title, message,label ok, label
 *         cancel dialog type,interface object, layout id if any else -1.
 * 
 *         For Ok only use onActionOk callback. For OkCancel use onActionOk and
 *         onActioncalcel callback.
 * 
 *         Intermediate progress dialog
 * 
 *         setArguments bundle message,interface object, layout id if any else
 *         -1
 */

public class CustomDialog
{
	private static CustomDialog customDialog;
	private IActionOKCancel callback;
	private DIALOG_TYPE type;
	private String title, message;
	private String ok, cancel, neutral;
	private int viewID, requestCode = -1;
	private Context context;
	private BaseTabActivity mActivity;
	private boolean cancelable = true;

	public enum DIALOG_TYPE
	{
		OK_ONLY, OK_CANCEL, INTERMDIATE_PROGRESS_WITH_MSG, INTERMDIATE_PROGRESS_WO_MSG, OK_ONLY_BACK, THREE_BUTTON
	};

	/**
	 * 
	 * Alert dialog setArguments bundle title, message,label ok, label cancel
	 * dialog type,interface object[class must implement IactionOkCancel],
	 * layout id if any else -1.
	 * 
	 * For Ok only use onActionOk callback. For OkCancel use onActionOk and
	 * onActioncalcel callback.
	 * 
	 * Intermediate progress dialog setArguments bundle message,interface
	 * object, layout id if any else -1
	 */

	public static CustomDialog getInstance(Context con, IActionOKCancel callback, String message, String title, DIALOG_TYPE type, int layoutId, String labelOK, String labelCancel, int requestCode, BaseTabActivity mActivity)
	{
		customDialog = new CustomDialog();
		customDialog.context = con;
		customDialog.callback = callback;
		customDialog.type = type;
		customDialog.title = title == null ? con.getResources().getString(R.string.app_name) : title;
		customDialog.cancel = labelCancel;
		customDialog.ok = labelOK;
		customDialog.mActivity = mActivity;
		customDialog.message = message == null ? con.getResources().getString(R.string.app_name) : message;
		customDialog.requestCode = requestCode;
		return customDialog;

	}

	public static CustomDialog getInstance(Context con, IActionOKCancel callback, String message, DIALOG_TYPE type, int requestCode, BaseTabActivity mActivity)
	{
		customDialog = new CustomDialog();
		customDialog.context = con;
		customDialog.callback = callback;
		customDialog.type = type;
		customDialog.title = con.getResources().getString(R.string.app_name);
		customDialog.cancel = con.getResources().getString(R.string.cancel);
		customDialog.ok = con.getResources().getString(R.string.lbl_ok);
		customDialog.mActivity = mActivity;
		customDialog.message = message == null ? con.getResources().getString(R.string.app_name) : message;
		customDialog.requestCode = requestCode;
		return customDialog;

	}

	public static CustomDialog getInstance(Context con, IActionOKCancel callback, String message, String title, DIALOG_TYPE type, int requestCode , BaseTabActivity mActivity)
	{
		customDialog = new CustomDialog();
		customDialog.context = con;
		customDialog.callback = callback;
		customDialog.type = type;
		customDialog.title = title;
		customDialog.cancel = con.getResources().getString(R.string.cancel);
		customDialog.ok = con.getResources().getString(R.string.lbl_ok);
		customDialog.mActivity = mActivity;
		customDialog.message = message == null ? con.getResources().getString(R.string.app_name) : message;
		customDialog.requestCode = requestCode;
		return customDialog;

	}

	public void show()
	{
		ShowDialog(type);
	}

	//	public void setStyle()
	//	{
	//		setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme);
	//	}

	public static void setCancellable(boolean isCancel)
	{
		customDialog.cancelable = isCancel;
	}

	public void ShowDialog(DIALOG_TYPE type)
	{
		switch (type)
		{
			case OK_ONLY:
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.popupActivityThem);
				builder.setTitle(title);
				builder.setMessage(message);
				builder.setCancelable(customDialog.cancelable);
				builder.setPositiveButton(ok, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();
						if (callback != null)
							callback.onActionOk(requestCode);

					}
				});//.create().show();
				AlertDialog dialog = builder.create();
				dialog.show();
				
				TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
				messageView.setTextColor(context.getResources().getColor(R.color.light_gray));
				//messageView.setTextSize(context.getResources().getDimension(R.dimen.text_size_14));
				messageView.setTextSize(22);
				
				Utilities.setDividerTitleColor(dialog, 0,mActivity);
				
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialog.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				lp.gravity = Gravity.CENTER;
				dialog.getWindow().setAttributes(lp);
				
				Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
				Utilities.setDefaultFont_12(button_positive);	

				// Create the AlertDialog object and return it
			}
				break;
			case OK_ONLY_BACK:
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
				builder.setTitle(title);
			//	builder.setIcon(R.drawable.appicon);
				builder.setMessage(message);

				builder.setPositiveButton(ok, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();

					}
				});//.create().show();

				AlertDialog dialog = builder.create();
				dialog.show();
				
				TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
				messageView.setTextColor(context.getResources().getColor(R.color.light_gray));
			//	messageView.setTextSize(context.getResources().getDimension(R.dimen.text_size_14));
				messageView.setTextSize(22);
				Utilities.setDividerTitleColor(dialog, 0,mActivity);

				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialog.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				lp.gravity = Gravity.CENTER;
				dialog.getWindow().setAttributes(lp);

				Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
				Utilities.setDefaultFont_12(button_positive);	
				// Create the AlertDialog object and return it
			}
				break;
			case OK_CANCEL:
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
				builder.setTitle(title);
			//	builder.setIcon(R.drawable.appicon);
				builder.setMessage(message);

				
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();
						if (callback != null)
							callback.onActionCancel(requestCode);
					}
				});

				builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();
						if (callback != null)
							callback.onActionOk(requestCode);						

					}
				});
				//.create().show();
				AlertDialog dialog = builder.create();
				dialog.show();
				
				TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
				messageView.setTextColor(context.getResources().getColor(R.color.light_gray));
			//	messageView.setTextSize(context.getResources().getDimension(R.dimen.text_size_14));
				messageView.setTextSize(22);
				
				Utilities.setDividerTitleColor(dialog, 0,mActivity);
			
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialog.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				lp.gravity = Gravity.CENTER;
				dialog.getWindow().setAttributes(lp);	
	
				Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
				Utilities.setDefaultFont_12(button_negative);	
				Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
				Utilities.setDefaultFont_12(button_positive);				
				// Create the AlertDialog object and return it
			}
				break;
			case THREE_BUTTON:

			{
				AlertDialog.Builder builder = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT);
				builder.setTitle(title);
			//	builder.setIcon(R.drawable.appicon);
				builder.setMessage(message);

				builder.setPositiveButton(ok, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();
						if (callback != null)
							callback.onActionOk(requestCode);

					}
				});

				builder.setNegativeButton(cancel, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();
						if (callback != null)
							callback.onActionCancel(requestCode);

					}
				});

				builder.setNeutralButton(neutral, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();
						if (callback != null)
							callback.onActionNeutral(requestCode);

					}
				});//.create().show();
				
				AlertDialog dialog = builder.create();
				dialog.show();
				
				TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
				messageView.setTextColor(context.getResources().getColor(R.color.light_gray));
			//	messageView.setTextSize(context.getResources().getDimension(R.dimen.text_size_14));
				messageView.setTextSize(22);
				Utilities.setDividerTitleColor(dialog, 0,mActivity);

				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialog.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				lp.gravity = Gravity.CENTER;
				dialog.getWindow().setAttributes(lp);

				Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
				Utilities.setDefaultFont_12(button_negative);	
				Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
				Utilities.setDefaultFont_12(button_positive);
				Button button_natural = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
				Utilities.setDefaultFont_12(button_natural);	
				// Create the AlertDialog object and return it
			}
				break;
			default:
				break;
		}
	}

}
