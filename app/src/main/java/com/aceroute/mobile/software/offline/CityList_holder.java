package com.aceroute.mobile.software.offline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aceroute.mobile.software.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class CityList_holder extends BaseAdapter {
	
	ArrayList<String> citiesHolder;
	ArrayList<Integer> downStateHolder;
	
	customButtonListener customListner;  
	 
    public interface customButtonListener {  
        public void onButtonClickListner(int position, String value);
    }  
    
    
    private Context context;
    //private HashMap<String, HashMap<String, Integer>> data = new HashMap<String, HashMap<String, Integer>>();  
  
    public CityList_holder(Context context, HashMap<String, HashMap<String, Integer>> cities) {
		// TODO Auto-generated constructor stub
    	this.context=context;
    	citiesHolder = new ArrayList<String>();
		downStateHolder = new ArrayList<Integer>();
    	
    	for (String key : cities.keySet())
    	{
    		HashMap<String, Integer> dataHash = cities.get(key);
    		
    		Set<Entry<String, Integer>> entrySet = dataHash.entrySet();
    		for (Entry entry : entrySet)
    		{
    			citiesHolder.add(entry.getKey().toString());
    			downStateHolder.add(Integer.parseInt((entry.getValue().toString())));
    		}
    	}
	}

	public void setCustomButtonListner(customButtonListener listener) {  
        this.customListner = listener;  
    }
	
	
	public class ViewHolder {  
        TextView text ,state;
        Button button;
    } 
    
    
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return citiesHolder.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return citiesHolder.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 final ViewHolder viewHolder;  
	        if (convertView == null) {  
	            LayoutInflater inflater = LayoutInflater.from(context);
	            convertView = inflater.inflate(R.layout.citieslist_lay_holder, null);  
	            viewHolder = new ViewHolder();  
	            viewHolder.text = (TextView) convertView
	                    .findViewById(R.id.TV_1);  
	            viewHolder.button = (Button) convertView
	                    .findViewById(R.id.btw_download);
	            viewHolder.state = (TextView) convertView.findViewById(R.id.textView_curdownstate);
	            convertView.setTag(viewHolder);  
	        }
	        else {  
	            viewHolder = (ViewHolder) convertView.getTag();  
	        }
	        
	        final String temp = getItem(position);
	        int currentdownloadSte = downStateHolder.get(position);
	        viewHolder.text.setText(temp); 

	       // final Button buttonTxtChge = viewHolder.button;

	        if (currentdownloadSte==1)
	        {
	        	viewHolder.button.setText("Downloaded");
	        	viewHolder.button.setOnClickListener(new OnClickListener() {
	       		  
			            @Override
			            public void onClick(View v) {
			            	Toast.makeText(context, "Already downloaded", Toast.LENGTH_LONG).show();
			            }  
			        });  
	        }
	        else{
		        viewHolder.button.setOnClickListener(new OnClickListener() {
		        	
		            @Override
		            public void onClick(View v) {
		                if (customListner != null) {  
		                    customListner.onButtonClickListner(position,temp);
		                    viewHolder.button.setClickable(false);
		                    viewHolder.button.setText("Downloading..");
		                    //buttonTxtChge.setText("Downloading..");
		                }  
		  
		            }  
		        });  
	        }
		return convertView;
	}
	
}






























/*ArrayAdapter<String> {  
    customButtonListener customListner;  
  
    public interface customButtonListener {  
        public void onButtonClickListner(int position,String value);  
    }  
  
    public void setCustomButtonListner(customButtonListener listener) {  
        this.customListner = listener;  
    }  
  
    private Context context;  
    private ArrayList<String> data = new ArrayList<String>();  
  
    public V_holder(Context context, ArrayList<String> dataItem) {  
        super(context, R.layout.checkboxlist, dataItem);  
        this.data = dataItem;  
        this.context = context;  
    }  
  
    @Override  
    public View getView(final int position, View convertView, ViewGroup parent) {  
        ViewHolder viewHolder;  
        if (convertView == null) {  
            LayoutInflater inflater = LayoutInflater.from(context);  
            convertView = inflater.inflate(R.layout.checkboxlist, null);  
            viewHolder = new ViewHolder();  
            viewHolder.text = (TextView) convertView  
                    .findViewById(R.id.TV_1);  
            viewHolder.button = (Button) convertView  
                    .findViewById(R.id.btw_download);  
            convertView.setTag(viewHolder);  
        } else {  
            viewHolder = (ViewHolder) convertView.getTag();  
        }  
        final String temp = getItem(position);  
        viewHolder.text.setText(temp);  
        viewHolder.button.setOnClickListener(new OnClickListener() {  
  
            @Override  
            public void onClick(View v) {  
                if (customListner != null) {  
                    customListner.onButtonClickListner(position,temp);  
                }  
  
            }  
        });  
  
        return convertView;  
    }  
  
    public class ViewHolder {  
        TextView text;  
        Button button;  
    }  
} */ 