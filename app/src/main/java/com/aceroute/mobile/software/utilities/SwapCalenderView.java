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
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

public class SwapCalenderView extends LinearLayout {
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
    private boolean enablePerivousDate = true;
    private RecyclerView grid;
    private boolean enablePerivousmonths = true;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    CalendarRecyclerAdapter dateadapter;
    public static ArrayList<Shifts> shiftsArrayList = new ArrayList<>();
    ArrayList<Shifts> tempshiftsArrayList = new ArrayList<>();

    public SwapCalenderView(Context context) {
        super(context);
        TypeFaceFont.overrideFonts(getContext(), this);
    }

    public SwapCalenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypeFaceFont.overrideFonts(getContext(), this);
        initControl(context, attrs);
    }

    public SwapCalenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypeFaceFont.overrideFonts(getContext(), this);
        initControl(context, attrs);
    }

    private void initControl(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.swap_grid_calender, this);

        loadDateFormat(attrs);
        assignUiElements();
//        assignClickHandlers();
        updateCalendar();
    }

    private void assignUiElements() {
//        btnPrev = (ImageView) findViewById(R.id.calendar_prev_button);
//        btnNext = (ImageView) findViewById(R.id.calendar_next_button);
//        txtDate = (TextView) findViewById(R.id.calendar_date_display);
//        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
//        txtDate.setText(sdf.format(currentDate.getTime()));
//        txtDate.setTextSize(20 + PreferenceHandler.getCurrrentFontSzForApp(getContext()));
        grid = (RecyclerView) findViewById(R.id.calender_recycler_view);
        grid.setLayoutManager(new LinearLayoutManager(getContext()));
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
        if (eventlist != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                for (long temp : eventlist.keySet()) {
                    date = sdf.parse(sdf.format(temp));
                    if (eventlist.get(temp).shift != null) {
                        if (eventlist.get(temp).shift.tid == 0 || eventlist.get(temp).shift.tid == 2)
                            cells.add(date);
                    }
                    if (eventlist.get(temp).unshiftlist != null) {
                        for (int i = 0; i < eventlist.get(temp).unshiftlist.size(); i++) {
                            cells.add(date);
                        }
                    }

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Collections.sort(cells);
        }

        // update grid_cal
        if (dateadapter == null) {
            dateadapter = new CalendarRecyclerAdapter(getContext(), cells);
            grid.setAdapter(dateadapter);
        } else {
            dateadapter.notifyDataSetChanged();
            //please managed.. use notify adapter
//            dateadapter = new CalendarRecyclerAdapter(getContext(), cells);
//            grid.setAdapter(dateadapter);
        }
        Log.i("ProcessShift", "end Updatecals/start render " + Utilities.getCurrentTime());

    }

    public void setenablePerivousmonths(boolean flag) {
        enablePerivousmonths = flag;
    }


    public class GridLoader extends AsyncTask<Void, Shifts, Void> {
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
            viewHolder.event.setAlpha(0);
            viewHolder.event.setText("");
            viewHolder.view.setOnClickListener(null);
            viewHolder.event.setOnClickListener(null);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i("ProcessShift", "renderview" + position + " " + Utilities.getCurrentTime());
            long time = cells.get(position).getTime();
            ShiftDateModel eventobj = eventDays.get(time);

            if (eventobj != null) {
                if (position == 0) {
                    if (eventobj.shift != null) {
                        publishProgress(eventobj.shift);
                        tempshiftsArrayList.add(eventobj.shift);
                    } else {
                        publishProgress(eventobj.unshiftlist.get(0));
                        tempshiftsArrayList.add(eventobj.unshiftlist.get(0));
                    }
                } else if (cells.get(position - 1).getTime() != cells.get(position).getTime()) {
                    if (eventobj.shift != null) {
                        if (!enablePerivousDate) {
                            if (date.before(new Date()) || DateUtils.isToday(date.getTime())) {
                                isprevious = true;
                            }
                        }
                        isshift = true;
                        publishProgress(eventobj.shift);
                        tempshiftsArrayList.add(eventobj.shift);
                    } else {

                        if (eventobj.unshiftlist != null) {
                            try {
                                for (int i = 0; i < eventobj.unshiftlist.size(); i++) {
                                    publishProgress(eventobj.unshiftlist.get(i));
                                    tempshiftsArrayList.add(eventobj.unshiftlist.get(i));
                                    if (eventobj.unshiftlist.size() > 1)
                                        eventobj.unshiftlist.remove(i);
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        }
                    }
                } else if (cells.get(position - 1).getTime() == cells.get(position).getTime()) {
                    if (eventobj.unshiftlist != null) {
                        try {
                            for (int i = 0; i < eventobj.unshiftlist.size(); i++) {
                                if (i == 0) {
                                    publishProgress(eventobj.unshiftlist.get(i));
                                    tempshiftsArrayList.add(eventobj.unshiftlist.get(i));
                                    if (eventobj.unshiftlist.size() > 1)
                                        eventobj.unshiftlist.remove(i);
                                    break;
                                } else if (eventobj.unshiftlist.get(i).id != eventobj.unshiftlist.get(i - 1).id) {
                                    publishProgress(eventobj.unshiftlist.get(i));
                                    tempshiftsArrayList.add(eventobj.unshiftlist.get(i));
                                    if (eventobj.unshiftlist.size() > 1)
                                        eventobj.unshiftlist.remove(i);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(final Shifts... shiftses) {
            super.onProgressUpdate(shiftses);
            if (shiftses[0] != null) {
                if (shiftses[0] != null) {
                    if (shiftses[0].tid == 0) {
                        viewHolder.event.setText("Time Off");
                        TypeFaceFont.overrideFonts(getContext(), viewHolder.unevent);
                        viewHolder.event.animate().setDuration(50).alpha(1);
                        viewHolder.event.setBackground(getResources().getDrawable(R.color.dayoff_background));
                        viewHolder.event.setTextColor(getResources().getColor(R.color.dayoff_text));
                    } else if (shiftses[0].tid == 1) {
                        viewHolder.event.setText(shiftses[0].stime + "\n" + shiftses[0].etime);
                        TypeFaceFont.overrideFonts(getContext(), viewHolder.unevent);
                        viewHolder.event.animate().setDuration(50).alpha(1);
                        viewHolder.event.setBackground(getResources().getDrawable(R.color.server_background));
                        viewHolder.event.setTextColor(getResources().getColor(R.color.white));

                    } else if (shiftses[0].tid == 2) {
                        viewHolder.event.setText(shiftses[0].stime + "\n" + shiftses[0].etime);
                        TypeFaceFont.overrideFonts(getContext(), viewHolder.event);
                        viewHolder.event.animate().setDuration(50).alpha(1);
                        viewHolder.event.setBackground(getResources().getDrawable(R.color.shift_background));
                        viewHolder.event.setTextColor(getResources().getColor(R.color.white));
                        if (!isprevious) {
                            viewHolder.unevent.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    eventHandler.onShiftClick(shiftses[0]);
                                }
                            });
                        }
                    }
                }


                if (shiftses[0].tid == 3) {
                    viewHolder.event.setVisibility(GONE);
                    viewHolder.unevent.setText(shiftses[0].sbreaktime + "\n" + shiftses[0].ebreaktime);
                    TypeFaceFont.overrideFonts(getContext(), viewHolder.unevent);
                    if (shiftses[0].gacc < 3) {
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
                                eventHandler.onShiftClick(shiftses[0]);
//                                        for (int j = 0; j < shiftses[0]; j++) {
//                                            eventHandler.onShiftClick(shiftses[0].unshiftlist.get(j));
//                                        }

                            }
                        });
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
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.swap_control_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {

            Date date = cells.get(i);
            if (eventlist != null)
                new GridLoader(context, viewHolder, date, i, eventlist).execute();

            viewHolder.txt_date.setTypeface(null, Typeface.NORMAL);
            viewHolder.txt_date.setTextColor(Color.BLACK);
            SimpleDateFormat formatter1 = new SimpleDateFormat("MMM");
            viewHolder.txt_date.setText(ordinal(String.valueOf(date.getDate())));
            viewHolder.txt_date_month.setText(formatter1.format(date));
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
            viewHolder.txt_date_name.setText(formatter.format(date));
            viewHolder.view.setTag(date);
            viewHolder.checkBox.setChecked(false);
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isPressed()) {
                        if (isChecked) {
                            shiftsArrayList.add(tempshiftsArrayList.get(i));
                        } else {
                            shiftsArrayList.remove(tempshiftsArrayList.get(i));
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return cells.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView txt_date;
            private TextView txt_date_name;
            private TextView txt_date_month;
            private TextView event, unevent;
            private CheckBox checkBox;
            private View view;


            public ViewHolder(View view) {
                super(view);
                int val = PreferenceHandler.getCurrrentFontSzForApp(getContext());
                this.view = view;
                txt_date = (TextView) view.findViewById(R.id._date);
                TypeFaceFont.overrideFonts(getContext(), txt_date);
                txt_date_month = (TextView) view.findViewById(R.id._date_month);
                checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                txt_date.setTextSize(20 + val);
                txt_date_month.setTextSize(20 + val);
                event = (TextView) view.findViewById(R.id.event);
                event.setTextSize(16 + val);

                txt_date_name = (TextView) view.findViewById(R.id._date_name);
                unevent = (TextView) view.findViewById(R.id.unevent);
                txt_date_name.setTextSize(16 + val);
                unevent.setTextSize(16 + val);
                TypeFaceFont.overrideFonts(getContext(), txt_date_name);


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

