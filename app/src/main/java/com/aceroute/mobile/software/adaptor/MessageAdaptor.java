package com.aceroute.mobile.software.adaptor;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.component.reference.MessagePanic;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.utilities.PreferenceHandler;

import java.util.HashMap;


public class MessageAdaptor extends BaseAdapter {
    LayoutInflater mInflater;
    Context mContext;
    HashMap<Long, MessagePanic> messageListMap;
    Long[] keys;

    public MessageAdaptor(Context context, Object orderMessageListMap ) {

        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.messageListMap = (HashMap<Long, MessagePanic>) orderMessageListMap;
        keys = this.messageListMap.keySet().toArray(new Long[this.messageListMap.size()]);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return messageListMap.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return messageListMap.get(keys[position]);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    class ViewHolder {
        TextView actualMsg, partQunatity,  partDtl, backview_del, backview_edit;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {
            vi = mInflater.inflate(R.layout.row_message, null);
            holder = new ViewHolder();
            holder.actualMsg = (TextView)vi.findViewById(R.id.row_message_txt);
            //holder.partQunatity = (TextView)vi.findViewById(R.id.order_part_pquantity);
            Typeface tf = TypeFaceFont.getCustomTypeface(mContext);
            /*holder.partQunatity.setTypeface(tf,  Typeface.BOLD);
            holder.partDtl = (TextView)vi.findViewById(R.id.order_part_dtl);*/

           /* holder.backview_edit = (TextView) vi.findViewById(R.id.back_view_dummy);
            holder.backview_del = (TextView) vi.findViewById(R.id.back_view_dummy1);*/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        String partNm = "";

        holder.actualMsg.setText(((MessagePanic) getItem(position)).getMessage());
        holder.actualMsg.setTag(((MessagePanic) getItem(position)).getStmp());
        holder.actualMsg.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));

        /*holder.partQunatity.setText(((MessagePanic) getItem(position)).getMessage());
        holder.partQunatity.setTextColor(Color.parseColor("#E67E22"));
        holder.partQunatity.setTextSize(50 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
        holder.partDtl.setText(partTypeList.get(((MessagePanic) getItem(position)).getPart_type_id()).getDesc());
        holder.partDtl.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));*/
        //YD setting this because showing red line between part list_cal when text is decreased
        /*holder.backview_edit.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
        holder.backview_del.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));*/

        return vi;
    }




}
