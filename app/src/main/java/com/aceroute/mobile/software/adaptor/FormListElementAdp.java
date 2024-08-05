package com.aceroute.mobile.software.adaptor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.dialog.TypeFaceFont;

import java.util.ArrayList;

public class FormListElementAdp extends RecyclerView.Adapter<FormListElementAdp.MyViewHolder>  {
    private final LayoutInflater mInflater;
    ArrayList<String> valueToShow;
    ArrayList<String> fontList;
    Context mContext;

    public FormListElementAdp(Context mContext,  ArrayList<String> valueToShow, ArrayList<String> fontList ) {
        this.valueToShow = valueToShow;
        this.fontList = fontList;
        mInflater = LayoutInflater.from(mContext);
        this.mContext= mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = mInflater.inflate(R.layout.customtextview, parent, false);
        mContext = parent.getContext();
        return new MyViewHolder(view);
    }

    /**
     * YD 2020 This is the orignal method of RecyclerView.Adapter, used to check what happen if we dont pass the myviewHolder to its generic params
     * @param myViewHolder
     * @param position
     */
    /*@Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
    }*/

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        myViewHolder.subHeaderText.setText(this.valueToShow.get(position));

        String fontAndColor = fontList.get(position);
        if(fontAndColor != null && !fontAndColor.equals("")){
            String[] fontAndColorArr = fontAndColor!= null && fontAndColor.contains(",") ? fontAndColor.split("\\,") : null;
            if(fontAndColorArr != null){
                String font =  fontAndColorArr[0];
                Typeface tf = TypeFaceFont.getCustomTypeface(mContext);
                if(font.equals("B")){
                    myViewHolder.subHeaderText.setTypeface(tf, Typeface.BOLD);
                }
                else if(font.equals("BI")){
                    myViewHolder.subHeaderText.setTypeface(tf, Typeface.BOLD_ITALIC);
                }
                else if(font.equals("N")){
                    myViewHolder.subHeaderText.setTypeface(null, Typeface.BOLD);
                }
                else if(font.equals("NI")){
                    myViewHolder.subHeaderText.setTypeface(null, Typeface.BOLD_ITALIC);
                }

                String color =  fontAndColorArr[1];
                myViewHolder.subHeaderText.setTextColor(Color.parseColor("#"+color));
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.valueToShow.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView subHeaderText;

        private MyViewHolder(View itemView) {
            super(itemView);
            subHeaderText = (TextView) itemView.findViewById(R.id.list_content_form);
        }
    }

    /*@Override
    public int getCount() {
        return valueToShow.size();
    }

    @Override
    public Object getItem(int position) {
        return valueToShow.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class ViewHolder {
        TextView subHeaderText;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();
            vi = mInflater.inflate(R.layout.list_black_text, null);
            holder.subHeaderText = (TextView) vi.findViewById(R.id.list_content);

            vi.setTag(holder);
        }
        else
            holder = (ViewHolder) vi.getTag();

        holder.subHeaderText.setText((String)getItem(position));


        return vi;
    }*/
}
