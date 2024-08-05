package com.aceroute.mobile.software.notes;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;

import java.util.HashMap;

public class OrderNote extends BaseFragment implements android.view.View.OnClickListener , HeaderInterface , RespCBandServST {
	
	TextView t1,t2,t3,head_note;
	Button noteadd;
	String notes ;
	ImageView back_button;
	TextView header , subHeader;
	RelativeLayout R_imgebck;
	
	Order activeOrderObj= null;
	private int GETNOTE=1;
	
	/*@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		String dataFrmXml1 = intent.getStringExtra("XmlData");
		int Flag1 = intent.getIntExtra("Flag", 0);
		
		if (Flag1== 0) {
			setNotes(dataFrmXml1);
		}
		if (Flag1== 1) {
			noteSaveSuccess(dataFrmXml1);
			AceRouteService.jsInterface.getNotes(PreferenceHandler
					.getRequestJs(this));
		}
	}	*/
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		/*super.onCreate1(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.order_notes);*/
		View v = inflater.inflate(R.layout.order_notes, null);
		mActivity.registerHeader(this);
		
		BaseTabActivity.setHeaderTitle("", "NOTES", "");		
		
		Typeface tf = TypeFaceFont.getCustomTypeface(mActivity.getApplicationContext());
		t3=(TextView) v.findViewById(R.id.textView4);
		t3.setTypeface(tf);
		/*noteadd=(Button) v.findViewById(R.id.notebtn_add);
		back_button=(ImageView)v.findViewById(R.id.back_button_ordrnote);
		R_imgebck=(RelativeLayout) v.findViewById(R.id.Bck_button_ordernotes);
		
		header = (TextView)v.findViewById(R.id.header_order_note);
        header.setTypeface(tf);
        
		subHeader = (TextView)v.findViewById(R.id.sub_header_order_note);
		subHeader.setTypeface(tf);
		
		header.setText(PreferenceHandler.getOrderId4Js(mActivity));
		String S_header=PreferenceHandler.getOrderName4Js(mActivity);
	//	subHeader.setText(PreferenceHandler.getOrderName4Js(this));
		if(S_header.length()<=15){
			subHeader.setText(S_header);
		}else {
			subHeader.setText(S_header.substring(0 , 17)+"..");
		}*/ //YD later TODO
		t2=(TextView) v.findViewById(R.id.Head_note);
		t2.setText("JOB NOTES"/*Utilities.getStringForAppAndr(mActivity,3)*/); 
		t2.setTypeface(tf, Typeface.BOLD);
		
		head_note=(TextView) v.findViewById(R.id.Head_note);
		head_note.setTypeface(tf, Typeface.BOLD);
		
		return v;
		
		/*noteadd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(OrderNote.this, Createnote.class);
				i.putExtra("Notes", notes );
				startActivity(i);
			}
		});
		R_imgebck.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				retunSizeOfNote();
			}
		});*/ // YD later TODO
	  /*Intent ii= getIntent();
	  String str1=ii.getStringExtra("edited");
	  t3.setText(str1);*/
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//{"url":"'+AceRoute.appUrl+'",'+'"action":"getordernotes",'+'"oid":"'+orderId+'"}// getNotes Request
				updateOrderRequest req = new updateOrderRequest();
				req.setId(String.valueOf(activeOrderObj.getId()));
				req.setAction(OrderNotes.ACTION_GET_NOTES);
					
				OrderNotes.getData(req, mActivity, this, GETNOTE);	
	}
	
	/*@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		retunSizeOfNote();
	}*/ // YD later TODO
	
	/*private void retunSizeOfNote() {
		int numOfCharInNote = notes.length();
		
		//To send number of element to js
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt(AceRoute.REQUEST_ACTION, 16);
		bundle.putString("NumOfElemWithFMeta", String.valueOf(numOfCharInNote)+"#4");
		msg.setData(bundle);
		AceRoute.handler.sendMessage(msg);
		
		//finish();
		
	}*/
	
	private void setNotes(Object data) {
		
		if (data!=null){
			HashMap<Long,OrderNotes> responseMap = (HashMap<Long,OrderNotes>)data;
			if(responseMap!=null){
				
				OrderNotes ordNotObj = responseMap.get(activeOrderObj.getId());
				notes = ordNotObj.getOrdernote();
				t3.setText(notes);
			}
		}
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
		if (response!=null)
		{
			if (response.getStatus().equals("success")&& 
					response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED)))
			{
				if (response.getId()==GETNOTE)
				{
					final Object respMap = response.getResponseMap();
					mActivity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							setNotes(respMap);
							
						}
					});
				}
			}
			else if(response.getStatus().equals("success")&& 
					response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
			{
			}
		}
		
	}

	@Override
	public void headerClickListener(String callingId) {
		if (callingId.equals(mActivity.HeaderPlusPressed))
		{
			Createnote createnoteFrag = new Createnote();
			createnoteFrag.setActiveOrderObject(activeOrderObj);
			Bundle i = new Bundle();
			i.putString("Notes", notes );
			createnoteFrag.setArguments(i);
			mActivity.pushFragments(Utilities.JOBS, createnoteFrag, true, true,BaseTabActivity.UI_Thread);
		}
	//	Utilities.hideSoftKeyboard(); // mandeep		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	public void setActiveOrderObject(Order orderObj)
	{
		activeOrderObj = orderObj;
	}

	public void loadDataOnBack(BaseTabActivity mActivity) {
		mActivity.registerHeader(this);
		BaseTabActivity.setHeaderTitle("", "NOTES", "");	
		
		updateOrderRequest req = new updateOrderRequest();
		req.setId(String.valueOf(activeOrderObj.getId()));
		req.setAction(OrderNotes.ACTION_GET_NOTES);
			
		OrderNotes.getData(req, mActivity, this, GETNOTE);
	}

}
