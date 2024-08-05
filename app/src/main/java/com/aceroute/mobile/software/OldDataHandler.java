/*
package com.software.mobile.software;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.software.mobile.software.database.DBEngine;
import com.software.mobile.software.utilities.PreferenceHandler;
import com.software.mobile.software.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OldDataHandler extends AsyncTask<Integer, Integer, Integer> {

	public Context context;

	public OldDataHandler(Context context) {
		this.context = context;
	}

	@Override
	protected Integer doInBackground(Integer... params) {
		Log.i(Utilities.TAG, "######OldDataHandler start######");
		try {
			Log.i(Utilities.TAG, "in doInBackground of OldDataHandler.");
			Calendar processCalendar = Utilities.getCalendarInstance(context);
			//processCalendar.setTimeInMillis(System.currentTimeMillis());
			int hourOfDay = processCalendar.get(Calendar.HOUR_OF_DAY);
			Logger.i(context, Utilities.TAG,"hour of day : " + hourOfDay);
			if(hourOfDay == 0){
				long lastTime = PreferenceHandler.getOldDataCleanTime(context);
				Calendar calendar = Utilities.getCalendarInstance(context);
				long currenttime = calendar.getTimeInMillis();
				long difference = currenttime - lastTime;
				int mins = (int) (difference / Utilities.TIME_MINUTE);
				Logger.i(context, Utilities.TAG, "Hour of day is 1 and elapsed time from last saved time is : "+mins);
				// @@@@@@@ for debugging @@@@@@@ 
				// processCalendar.add(Calendar.DAY_OF_WEEK, 12);
				// mins = 61;
				// @@@@@@@ for debugging @@@@@@@
				
				// if hourOfDay is 1 and minutes processing data is greater than 60 then it should go further
				// because it means one hour is elapsed and next time hourOfDay will change so it will not come
				// in this condition.
				// and it should come in this condition after 24:00 and it is the next day and processing time is
				// recorded in PreferenceHandler.setOldDataCleanTime();
				if(mins>60){
					// getting start dates and end dates of this week 
						Date fDate = calendar.getTime();
						long firstMilli = calendar.getTimeInMillis();
						SimpleDateFormat smp = new SimpleDateFormat(
								"yyyy-MM-dd");
						String stringfdate = smp.format(fDate);
		
						calendar.add(Calendar.DAY_OF_MONTH,
								Utilities.DEFAULT_WEEK_DAYS);
						Date lastDate = calendar.getTime();
						SimpleDateFormat next_smp = new SimpleDateFormat(
								"yyyy-MM-dd");
						String todate = next_smp.format(lastDate);
						Log.e(Utilities.TAG, "Test fdate : " + stringfdate + " ****** lastDate : " + todate);
					
					Logger.i(context, Utilities.TAG, "Performing deletion in old data clean up.");
					//calendar.setTimeInMillis(System.currentTimeMillis());
					//Date processDate = new Date(processCalendar.getTimeInMillis());
					//Logger.i(context, Utilities.TAG, "date : " + processDate.toString());
					if (!Utilities.isInRangeCriteria(context, fDate)) {
						//Logger.i(context, Utilities.TAG, "date is not in range and taking date of previous week's last day");
						//processCalendar.add(Calendar.DAY_OF_WEEK, -8);
						//processDate = new Date(processCalendar.getTimeInMillis());
						//Logger.i(context, Utilities.TAG, "present date -8 day : " + processDate.toString());
						int deleteResult = DBEngine.deleteData(context, fDate, lastDate);
						Logger.i(context, Utilities.TAG, deleteResult
								+ " records has been deleted for range criteria.");
						//if (deleteResult > 0) {
							//setting date processCalendar to first day of week for getting next week data
							Logger.i(context, Utilities.TAG, "setting date processCalendar to first day of week for getting next week data");
							processCalendar = Utilities.getCalendarInstance(context);
							// @@@@@@@ for debugging @@@@@@@  
							// processCalendar.add(Calendar.DAY_OF_WEEK, 12);
							// @@@@@@@ for debugging @@@@@@@ 
							//if (calendar.getFirstDayOfWeek() == Calendar.SUNDAY)
							//	processCalendar.set(Calendar.DAY_OF_MONTH, processCalendar.getFirstDayOfWeek() + 1);
							//processCalendar.add(Calendar.DAY_OF_WEEK, 6);
							//Date fromDate1 = calendar.getTime();
							//String newFromDate1 = Utilities.getStringFromDate(context,
							//		 fromDate1, Utilities.DEFAULT_DATE_FORMAT);
							//Logger.i(context, Utilities.TAG, "From date for order request = " + newFromDate1);
							//calendar.add(Calendar.DAY_OF_WEEK, 7);
							Date fromDate = calendar.getTime();
							String newFromDate = Utilities.getStringFromDate(context,
									 fromDate, Utilities.DEFAULT_DATE_FORMAT);
							Logger.i(context, Utilities.TAG, "From date for order request = " + newFromDate);
							
							calendar.add(Calendar.DAY_OF_WEEK, 7);
							Date toDate = calendar.getTime();
							Log.i(Utilities.TAG, "todate  :" + toDate);
							String newToDate = Utilities.getStringFromDate(context,
									toDate,
									Utilities.DEFAULT_DATE_FORMAT);
							Logger.i(context, Utilities.TAG, "To date for order request = " + newToDate);
							
							//Logger.i(context, Utilities.TAG,
							//		"requesting new data for next week : newFromDate " + newFromDate1
							//				+ " & newToDate : " + newToDate);
							String timezone = Utilities.getTimeZone();
							// passing false in getorders temporary. 
							// passing toDate so that todate will not come in range and system will send
							// request for data.
							String strfdate = Utilities.getStringFromDate(context, fDate, Utilities.DEFAULT_DATE_FORMAT);
							String strtdate = Utilities.getStringFromDate(context, toDate, Utilities.DEFAULT_DATE_FORMAT);
							Logger.i(Utilities.TAG	, "Setting prefernce from date and to date :"+strfdate+","+strtdate);
							PreferenceHandler.setPrefRangeDates(context, strfdate, strtdate);
							DBEngine.getOrders(timezone, toDate, newFromDate, newToDate,
									0,false, context);
						//}
					} else {
						Logger.i(context, Utilities.TAG, "date is in range");
					}
					
					//setting old data clean time
					PreferenceHandler.setOldDataCleanTime(context, System.currentTimeMillis());
				}
				else{
					Logger.i(context, Utilities.TAG, "Not performing same hour deletion.");
				}
			}
			Logger.i(context, Utilities.TAG, "###### OldDataHandler end######");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
*/
