package com.aceroute.mobile.software.adaptor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.component.reference.Shifts;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.fragment.ManageShift;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.Utilities;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ShiftSwipeAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    Context mContext;
    ArrayList<Shifts> shiftListMap;
    ManageShift classobj;
    String shftlock;

    public ShiftSwipeAdapter(Context context, ArrayList<Shifts> shiftListMap, ManageShift obj) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.shiftListMap = shiftListMap;
        this.classobj = obj;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return shiftListMap.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return shiftListMap.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    class ViewHolder {
        TextView hrstxt, title, backview_del, backview_edit;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {
            vi = mInflater.inflate(R.layout.hours_row, null);
            holder = new ViewHolder();

            holder.hrstxt = (TextView) vi.findViewById(R.id.hrstxt);
            holder.title = (TextView) vi.findViewById(R.id.titlehrs);
            holder.backview_edit = (TextView) vi.findViewById(R.id.back_view_dummy_shift);
            holder.backview_del = (TextView) vi.findViewById(R.id.back_view_dummy1_shift);
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        final Shifts sft = shiftListMap.get(position);

        holder.hrstxt.setTag(sft);
        if (sft.tid == 1) {
            holder.hrstxt.setBackgroundColor(Color.parseColor("#34495F"));
            holder.hrstxt.setText(sft.stime + "\n" + sft.etime);
            holder.title.setText("Shift Hour");
        } else if (sft.tid == 2) {
            holder.hrstxt.setBackgroundColor(Color.parseColor("#2980B9"));
            holder.hrstxt.setText(sft.stime + "\n" + sft.etime);
            holder.title.setText("Shift Hour");
        } else if (sft.tid == 3) {
            holder.title.setText("Personal Hour");
            holder.hrstxt.setText(sft.sbreaktime + "\n" + sft.ebreaktime);
            holder.hrstxt.setBackgroundColor(Color.parseColor("#27AE60"));
        }
        TypeFaceFont.overrideFonts(mContext, vi);
        holder.hrstxt.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
        holder.title.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));

        holder.backview_edit.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
        holder.backview_del.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));

        holder.hrstxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Shifts shifts = (Shifts) v.getTag();
                Date firstDate = Utilities.yesterday();
                Date lastDate = Utilities.shiftLockDate(mContext);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date shiftDate = sdf.parse(shifts.getdt());
                    if (!(firstDate.compareTo(shiftDate) * shiftDate.compareTo(lastDate) > 0)) {
                        if (shifts != null || shifts != null || shifts != null) {
                            if (shifts.tid == 2 || shifts.tid == 0)
                                classobj.showeditshiftHr((Shifts) v.getTag(), false);
                        }
                        if (shifts != null) {
                            if (shifts.tid == 3)
                                classobj.showeditshiftHr((Shifts) v.getTag(), false);
                        }
                    } else {
                        DisplayErrorMessage("Message", "User not able to Edit shift due to shift lock", "OK");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

//					classobj.showeditshiftHr((Shifts) v.getTag(), false);
            }
        });

        return vi;
    }

    public void DisplayErrorMessage(String Headremsg, String contentMsg, String BotomMsg) {
        final MyDialog dialog = new MyDialog(mContext, Headremsg, contentMsg, BotomMsg);
        dialog.setkeyListender(new MyDiologInterface() {

            @Override
            public void onPositiveClick() throws JSONException {
                // on No button click
                dialog.dismiss();
            }

            @Override
            public void onNegativeClick() {
                // on Yes button click
                dialog.dismiss();
            }
        });
        dialog.onCreate(null);
        dialog.show();
        return;
    }


}
