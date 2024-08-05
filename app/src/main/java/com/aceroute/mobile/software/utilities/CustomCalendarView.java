package com.aceroute.mobile.software.utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.component.reference.Shifts;
import com.aceroute.mobile.software.dialog.TypeFaceFont;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

public class CustomCalendarView extends LinearLayout {
    private static int DAYS_COUNT = 42;
    private static final String DATE_FORMAT = "MMM yyyy";
    private String dateFormat;
    public int sMin = 0;
    public int eMin = 0;
    public String stime = null;
    public String etime = null;
    private Calendar currentDate = Calendar.getInstance();
    private EventHandler eventHandler = null;
    HashMap<Long, ShiftDateModel> eventlist;
    ArrayList<Date> cells;
    private boolean enablelongpress = true;
    private boolean enablePerivousmonths = true;
    private boolean enablePerivousDate = true;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private RecyclerView grid;
    CalendarRecyclerAdapter dateadapter;

    public CustomCalendarView(Context context) {
        super(context);
        TypeFaceFont.overrideFonts(getContext(), this);
    }

    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypeFaceFont.overrideFonts(getContext(), this);
        initControl(context, attrs);
    }

    public CustomCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypeFaceFont.overrideFonts(getContext(), this);
        initControl(context, attrs);
    }

    public void setenablelongpress(boolean flag) {
        enablelongpress = flag;
    }

    public void setenablePerivousmonths(boolean flag) {
        enablePerivousmonths = flag;
    }

    public void setenablePerivousDate(boolean flag) {
        enablePerivousDate = flag;
    }

    private void initControl(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_calendar, this);

        loadDateFormat(attrs);
        assignUiElements();
        assignClickHandlers();
        updateCalendar();
    }

    private void loadDateFormat(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CustomCalendarView);
        try {
            dateFormat = ta.getString(R.styleable.CustomCalendarView_dateFormat);
            if (dateFormat == null)
                dateFormat = DATE_FORMAT;
        } finally {
            ta.recycle();
        }
    }

    private void assignUiElements() {
        btnPrev = (ImageView) findViewById(R.id.calendar_prev_button);
        btnNext = (ImageView) findViewById(R.id.calendar_next_button);
        txtDate = (TextView) findViewById(R.id.calendar_date_display);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        txtDate.setText(sdf.format(currentDate.getTime()));
        txtDate.setTextSize(20 + PreferenceHandler.getCurrrentFontSzForApp(getContext()));
        grid = (RecyclerView) findViewById(R.id.calender_recycler_view);
        grid.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void assignClickHandlers() {
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cDate = Calendar.getInstance();
                cDate.add(Calendar.MONTH, 5);
                currentDate.add(Calendar.MONTH, 1);
                if (cDate.getTime().getYear() == currentDate.getTime().getYear() && cDate.getTime().getMonth() <= currentDate.getTime().getMonth()) {
                    btnNext.setVisibility(GONE);
                }
                btnPrev.setVisibility(VISIBLE);
                updateCalendar();
            }
        });

        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cDate = null;
                currentDate.add(Calendar.MONTH, -1);
                if (!enablePerivousmonths) {
                    cDate = Calendar.getInstance();
                    if (cDate.getTime().getYear() == currentDate.getTime().getYear() && cDate.getTime().getMonth() >= currentDate.getTime().getMonth()) {
                        btnPrev.setVisibility(GONE);
                    }
                }
                btnNext.setVisibility(VISIBLE);
                updateCalendar();
            }
        });
    }

    public void updateCalendar() {
        updateCalendar(eventlist);
    }

    /**
     * Display dates correctly in grid_cal
     */
    public void updateCalendar(HashMap<Long, ShiftDateModel> events) {
        Log.i("ProcessShift", "start updatecal " + Utilities.getCurrentTime());
        if (cells == null) {
            cells = new ArrayList<>();
        } else {
            cells.clear();
        }

        eventlist = events;
        Calendar cal = (Calendar) currentDate.clone();
        DAYS_COUNT = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        //YD filling cell object with the dates in the month and later display
        int incr = 0;
        while (incr < DAYS_COUNT) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = sdf.parse(sdf.format(cal.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cells.add(date);
            incr++;
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid_cal
        if (dateadapter == null) {
            dateadapter = new CalendarRecyclerAdapter(getContext(), cells);
            grid.setAdapter(dateadapter);
        } else
            dateadapter.notifyDataSetChanged();

        Log.i("ProcessShift", "end Updatecals/start render " + Utilities.getCurrentTime());
        // update title
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        txtDate.setText(sdf.format(currentDate.getTime()));
        txtDate.setTypeface(null, Typeface.BOLD);
    }

    public class GridLoader extends AsyncTask<Void, ShiftDateModel, Void> {
        Date date;
        int position;
        boolean isprevious = false;
        HashMap<Long, ShiftDateModel> eventDays;
        Context cn;
        boolean isshift = false;
        int items = 0;
        LayoutInflater inflater;
        CalendarRecyclerAdapter.ViewHolder viewHolder;

        GridLoader(Context cn, CalendarRecyclerAdapter.ViewHolder viewHolder, Date date, int position, HashMap<Long, ShiftDateModel> eventDays) {
            this.cn = cn;
            this.position = position;
            this.viewHolder = viewHolder;
            this.eventDays = eventDays;
            this.date = date;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inflater = (LayoutInflater) cn.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewHolder.unevent.setAlpha(0);
            viewHolder.unevent.setText("");
            viewHolder.unevent.setOnClickListener(null);

            viewHolder.more.setVisibility(GONE);
            viewHolder.more.setOnClickListener(null);

            viewHolder.event.setAlpha(0);
            viewHolder.event.setText("");
            viewHolder.view.setOnClickListener(null);
            viewHolder.event.setOnClickListener(null);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i("ProcessShift", "renderview" + position + " " + Utilities.getCurrentTime());
            long time = date.getTime();
            ShiftDateModel eventobj = eventDays.get(time);
            if (eventobj != null) {
                if (eventobj.extrashiftlist != null || eventobj.shift != null || eventobj.unshiftlist != null) {
                    if (!enablePerivousDate) {
//                        if (date.before(new Date()) && !DateUtils.isToday(date.getTime())) {
//                            isprevious = true;
//                        }

                        //open a dialog on calender Click
                        if (date.before(new Date()) || DateUtils.isToday(date.getTime())) {
                            isprevious = true;
                        }
                    }
                    isshift = true;
                    publishProgress(eventobj);
                }
                if (eventobj.getWorker() != null) {
                    publishProgress(eventobj);
                }


                int cn = eventobj.getUnAvialCount();
                items = cn;
                if (cn > 1)
                    cn = 1;
                for (int i = 1; i < cn; i++) {
                    publishProgress(eventobj);
//YD                    publishProgress(eventobj.unshiftlist.get(i));
                }
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(final ShiftDateModel... shiftses) {
            super.onProgressUpdate(shiftses);
            if (shiftses[0] != null) {
                if (shiftses[0].shift != null) {
                    if (shiftses[0].shift.tid == 0) {
                        viewHolder.event.setText("Time Off");
                        TypeFaceFont.overrideFonts(getContext(), viewHolder.unevent);
                        viewHolder.event.animate().setDuration(50).alpha(1);
                        viewHolder.event.setBackground(getResources().getDrawable(R.color.dayoff_background));
                        viewHolder.event.setTextColor(getResources().getColor(R.color.dayoff_text));
//                        if (!isprevious) {
//                            viewHolder.event.setOnClickListener(new OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    eventHandler.onShiftClick(shiftses[0].shift);
//                                }
//                            });
//                        }
                    } else if (shiftses[0].shift.tid == 1) {
                        viewHolder.event.setText(shiftses[0].shift.stime + "\n" + shiftses[0].shift.etime);
                        TypeFaceFont.overrideFonts(getContext(), viewHolder.unevent);
                        viewHolder.event.animate().setDuration(50).alpha(1);
                        viewHolder.event.setBackground(getResources().getDrawable(R.color.server_background));
                        viewHolder.event.setTextColor(getResources().getColor(R.color.white));

                    } else if (shiftses[0].shift.tid == 2) {
                        viewHolder.event.setText(shiftses[0].shift.stime + "\n" + shiftses[0].shift.etime);
                        TypeFaceFont.overrideFonts(getContext(), viewHolder.event);
                        viewHolder.event.animate().setDuration(50).alpha(1);
                        viewHolder.event.setBackground(getResources().getDrawable(R.color.shift_background));
                        viewHolder.event.setTextColor(getResources().getColor(R.color.white));
                        if (!isprevious) {
                            viewHolder.event.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    eventHandler.onShiftClick(shiftses[0].shift);
                                }
                            });
                        }
                    }

                } else if (shiftses[0].getWorker() != null) {
                    String worker = shiftses[0].getWorker().getWrkwk();
                    Calendar c = Calendar.getInstance();
                    viewHolder.event.setText(settimevalue(worker));
                    TypeFaceFont.overrideFonts(getContext(), viewHolder.unevent);
                    viewHolder.event.animate().setDuration(50).alpha(1);
                    viewHolder.event.setBackground(getResources().getDrawable(R.color.default_background));
                    viewHolder.event.setTextColor(getResources().getColor(R.color.white));
                }

                if (shiftses[0].unshiftlist != null) {
                    for (int i = 0; i < shiftses[0].unshiftlist.size(); i++) {
                        if (shiftses[0].unshiftlist.get(i).tid == 3) {
                            viewHolder.unevent.setText(shiftses[0].unshiftlist.get(i).sbreaktime + "\n" + shiftses[0].unshiftlist.get(i).ebreaktime);
                            TypeFaceFont.overrideFonts(getContext(), viewHolder.unevent);
                            if (shiftses[0].unshiftlist.get(i).gacc < 3) {
                                viewHolder.unevent.setBackground(getResources().getDrawable(R.color.break_background));
                                viewHolder.unevent.setTextColor(getResources().getColor(R.color.white));
                            } else {
                                viewHolder.event.setBackground(getResources().getDrawable(R.color.break_gacc_background));
                                viewHolder.event.setTextColor(getResources().getColor(R.color.white));
                            }
                            viewHolder.unevent.animate().setDuration(50).alpha(1);
                            if (!isprevious) {
                                viewHolder.unevent.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        for (int j = 0; j < shiftses[0].unshiftlist.size(); j++) {
                                            eventHandler.onShiftClick(shiftses[0].unshiftlist.get(j));
                                            break;
                                        }

                                    }
                                });
                            }
                        }
                    }
                }
            }
        }

        String settimevalue(String tm) {
            String hrarr[];
            if (tm.contains("|"))
                hrarr = tm.split(Pattern.quote("|"));
            else
                hrarr = tm.split(Pattern.quote(","));
            if (!hrarr[0].trim().equals("")) {
                sMin = Integer.parseInt(hrarr[0]);
                int min = sMin % 60;
                int hr = (sMin - min) / 60;
                String st = "am";
                if (hr > 12) {
                    hr = hr - 12;
                    st = "pm";
                }
                stime = hr + ":" + (min > 9 ? min : "0" + min) + " " + st;

                eMin = Integer.parseInt(hrarr[1]);
                min = eMin % 60;
                hr = (eMin - min) / 60;
                String st1 = "am";
                if (hr > 12) {
                    hr = hr - 12;
                    st1 = "pm";
                }
                etime = hr + ":" + (min > 9 ? min : "0" + min) + " " + st1;
            }
            return stime + "\n" + etime;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (items > 0 || isshift) {
                if (isprevious) {
                    viewHolder.view.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (eventHandler != null) {
                                eventHandler.onDayClick(date, isprevious);
                            }
                        }
                    });
                }
                if (items > 1) {
                    viewHolder.more.setVisibility(VISIBLE);
                    viewHolder.more.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (eventHandler != null) {
                                eventHandler.onDayClick(date, isprevious);
                            }
                        }
                    });
                }
            }
        }
    }

    public class CalendarRecyclerAdapter extends RecyclerView.Adapter<CalendarRecyclerAdapter.ViewHolder> {
        ArrayList<Date> cells;
        private Context context;

        public CalendarRecyclerAdapter(Context context, ArrayList<Date> days) {
            this.cells = days;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.control_calendar_day_list, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {

            Date date = cells.get(i);
            Date today = new Date();
            if (eventlist != null)
                new GridLoader(context, viewHolder, date, i, eventlist).execute();

            viewHolder.txt_date.setTypeface(null, Typeface.NORMAL);
            viewHolder.txt_date.setTextColor(Color.BLACK);
            if (date.getDate() == today.getDate() && date.getMonth() == today.getMonth() && date.getYear() == today.getYear()) {
                viewHolder.txt_date.setTypeface(null, Typeface.BOLD);
                viewHolder.txt_date.setTextColor(Color.parseColor("#8E44AD"));
            }

            viewHolder.txt_date.setText(ordinal(String.valueOf(date.getDate())));
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
            viewHolder.txt_date_name.setText(formatter.format(date));
            viewHolder.view.setTag(date);
        }

        @Override
        public int getItemCount() {
            return cells.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView txt_date;
            private TextView txt_date_name;
            private TextView event, unevent, more;
            private View view;

            public ViewHolder(View view) {
                super(view);
                int val = PreferenceHandler.getCurrrentFontSzForApp(getContext());
                this.view = view;
                txt_date = (TextView) view.findViewById(R.id._date);
                TypeFaceFont.overrideFonts(getContext(), txt_date);
                txt_date.setTextSize(20 + val);
                event = (TextView) view.findViewById(R.id.event);
                event.setTextSize(16 + val);

                txt_date_name = (TextView) view.findViewById(R.id._date_name);
                unevent = (TextView) view.findViewById(R.id.unevent);
                TextView _tmptxt = (TextView) view.findViewById(R.id._tmptxt);
                more = (TextView) view.findViewById(R.id.more);
                txt_date_name.setTextSize(16 + val);
                _tmptxt.setTextSize(16 + val);
                more.setTextSize(16 + val);
                unevent.setTextSize(16 + val);
                TypeFaceFont.overrideFonts(getContext(), txt_date_name);
                TypeFaceFont.overrideFonts(getContext(), more);

            }
        }
    }

    private String ordinal(String s) {
        int i = Integer.parseInt(s);
        String[] sufixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + sufixes[i % 10];
        }
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public interface EventHandler {
        void onDayClick(Date date, boolean isprevious);

        void onShiftClick(Shifts shiftDateModel);
    }
}
