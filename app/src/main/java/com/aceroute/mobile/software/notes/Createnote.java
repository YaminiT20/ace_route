package com.aceroute.mobile.software.notes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderNotes;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.fragment.BaseFragment;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.updateOrderRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;

public class Createnote extends BaseFragment implements android.view.View.OnClickListener , HeaderInterface , RespCBandServST {
	ImageView imgbck;
	RelativeLayout R_imgebck, parentView;
	Button done;
	EditText edit;
	TextView header , subHeader,t_note;
	Order activeOrderObj= null;
	private int SAVENOTE=1;
	String notes;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		/*super.onCreate1(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.createnote);*/
		View v = inflater.inflate(R.layout.createnote, null);
		mActivity.registerHeader(this);
		
		BaseTabActivity.setHeaderTitle("","EDIT NOTES","");
		
		parentView = (RelativeLayout) v.findViewById(R.id.parentView);
		
		//imgbck=(ImageView) v.findViewById(R.id.back_button_createnote);
		R_imgebck=(RelativeLayout) v.findViewById(R.id.Bck_button);
		Typeface tf = TypeFaceFont.getCustomTypeface(mActivity.getApplicationContext());
		edit=(EditText) v.findViewById(R.id.editText1);
		edit.setTypeface(tf);
		t_note=(TextView) v.findViewById(R.id.textView3);
		t_note.setText("JOB NOTES"/*Utilities.getStringForAppAndr(mActivity,3)*/);
		t_note.setTypeface(tf, Typeface.BOLD);
		
		parentView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideSoftKeyboard();
				return false;
			}
		});
		
		
		/*header = (TextView)v.findViewById(R.id.header_note_save);
		
        header.setTypeface(tf);
        
		subHeader = (TextView)v.findViewById(R.id.sub_header_note_save);
		subHeader.setTypeface(tf);
		
		header.setText(PreferenceHandler.getOrderId4Js(mActivity));
		String S_header=PreferenceHandler.getOrderName4Js(mActivity);
		//	subHeader.setText(PreferenceHandler.getOrderName4Js(this));
			if(S_header.length()<=15){
				subHeader.setText(S_header);
			}else {
				subHeader.setText(S_header.substring(0 , 17)+"..");
			}
		
		//subHeader.setText(PreferenceHandler.getOrderName4Js(this));
		
		R_imgebck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//finish();
			}
		});*/
		Bundle extra = this.getArguments();
		String notes = extra.getString("Notes");
		edit.setText(notes);
		
		//final String orderId = PreferenceHandler.getOrderId4Js(mActivity);
		
		/*done=(Button) v.findViewById(R.id.Done);
		done.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent i=new Intent(getApplicationContext(), OrderNote.class);
				String str = edit.getText().toString();
			//	Bundle bundle = new Bundle();
			//	bundle.putString("",str);
				i.putExtra("edited",str);
                startActivity(i);
				
				String notes= edit.getText().toString();
				
			String Str = "{\"url\":\""+"https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi"+"\",\"type\":\"post\",\"data\":{\"id\":\""+orderId+"\","+
					"\"name\":\"note\",\"value\":\""+notes+"\",\"action\":\"saveorderfld\"}}" ;
			
			AceRouteService.jsInterface.saveNotes(Str);
					
			}
		});*/
		return v;
		
		
	}

	@Override
	public void ServiceStarter(RespCBandServST activity, Intent intent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setResponseCallback(String response, Integer reqId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setResponseCBActivity(Response response) {
		if (notes.length()>0)
			activeOrderObj.setNotCount(1);
		else 
			activeOrderObj.setNotCount(0);
		mActivity.popFragments(mActivity.SERVICE_Thread);
		
	}

	@Override
	public void headerClickListener(String callingId) {
		if (callingId.equals(mActivity.HeaderDonePressed))
		{
			notes= edit.getText().toString();
			
			/*String Str = "{\"url\":\""+"https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi"+"\",\"type\":\"post\",\"data\":{\"id\":\""+orderId+"\","+
					"\"name\":\"note\",\"value\":\""+notes+"\",\"action\":\"saveorderfld\"}}" ;*/
			
			updateOrderRequest req = new updateOrderRequest();
			req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
			req.setType("post");
			req.setId(String.valueOf(activeOrderObj.getId()));
			req.setName("note");
			req.setValue(notes);
			req.setAction(Order.ACTION_SAVE_ORDER_FLD);
			
			OrderNotes.saveData(req, mActivity, this, SAVENOTE);
			
			hideSoftKeyboard();			
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	public void setActiveOrderObject(Order orderObj)
	{
		activeOrderObj = orderObj;
	}

	public void hideSoftKeyboard() {
		if(mActivity.getCurrentFocus()!=null){
		InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
		}
	}

	public void loadDataOnBack(BaseTabActivity mActivity) {
		mActivity.registerHeader(this);
		
	}


}
