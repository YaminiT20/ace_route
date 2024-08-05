package com.aceroute.mobile.software.validation;


import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;

public class ValidationEngine {
	Context mContext;
	static boolean isValid;
	AppActivity[] appactivitylist;
	private String jsonString;
	private VactivityList vActivityList = null;
	private static ValidationEngine instance;
	Vcontrols[] vcontrols ;
	int INPUT_TYPE;
	
/*>>INPUT TYPE CASES<<
 * 1 : Email edit text field
 * 2 : Password edit text field
 * 3 : Mobile Number edit text field
 * 4 : Numeric Field
 * */
	
	
	public ValidationEngine(Activity activity) {
		this.mContext = activity;
		jsonString =loadjson(mContext);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		vActivityList = gson.fromJson(jsonString, VactivityList.class);
	}
	
	public static ValidationEngine getInstance(Activity context){
		if(ValidationEngine.instance== null) {
			instance = new ValidationEngine(context);
		}
		return instance;
	}
	
	public void ValidateView(View view,int input_type_case){
		
		switch (input_type_case) {
		
		//Email field
		case 1:
			if(view instanceof EditText){
				EditText editTxt = (EditText) view;
				ValidateEmail(editTxt.getText());
				if(isValid){
				Toast.makeText(mContext, "Valid Email", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(mContext, "Invalid Email id", Toast.LENGTH_LONG).show();
				}
				
			}else{
				Toast.makeText(mContext, "Invalid View passed!", Toast.LENGTH_LONG).show();
			}
			
			break;
			
		//Password field	
		case 2:
			if(view instanceof EditText){
				
				
			}else{
				Toast.makeText(mContext, "Invalid View passed!", Toast.LENGTH_LONG).show();
			}
			
			break;

		default:
			break;
		}
	}
	
	public static boolean ValidateEmail(Editable email) {
		String emailStr = email.toString().trim();

		String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		// "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

		if (emailStr.matches(emailPattern)) {
			isValid = true;
		} else {
			isValid = false;
		}
		return isValid;
	}
	
	public boolean ValidatePassfield(String pass,int length_limit, int min_lenght_limit){
		if(pass.length()>length_limit){
			Toast.makeText(mContext, "Maximum length of password is " + length_limit + " characters", Toast.LENGTH_LONG).show();
			return isValid = true;
		}else if(pass.length()<min_lenght_limit)
			Toast.makeText(mContext, "Minimum length of password should be " + min_lenght_limit + " characters", Toast.LENGTH_LONG).show();
			return isValid = false;
		
		
	}
	
	// Reading file from the assest folder
		public static String loadjson(Context context) {
			InputStream reader = null;
			String json = null;
			try {
			    reader = context.getApplicationContext().getAssets().open("validationJson");

			    int size = reader.available();
			    byte buffer[] = new byte[size];
			    reader.read(buffer);
			    reader.close();
			    
			    json = new String(buffer, "UTF-8");// this carry the json string
			} catch (IOException e) {
			    e.printStackTrace();
			} 
			Log.i("App", "Returning json string : " + json + "After reading the json from file.");
			return json;
			
		}
		
		public String checkValidation(Activity activity)
		{
			String errorMsg = null;
			for (int i=0; i <vcontrols.length; i++)
			{
				if (vcontrols[i].getMandatory().equals("yes"))
				{
					try {
						String controlName = vcontrols[i].getControl_id();
						int id = activity.getResources().getIdentifier(controlName, "id", activity.getPackageName());
						View view= activity.findViewById(id);
						String type = vcontrols[i].getType();
						
						if (type.equals("editText")){
							EditText et = (EditText) view;
							errorMsg = compareValidation(et,vcontrols[i]);
							if(!errorMsg.equals(""))
	{
								return errorMsg;
							}
						}
						/*if (type.equals("RadioGroup")){  //implement at the time when any of the one is mandatory
							
							RadioGroup RadGrp = (RadioGroup) view;
							if(RadGrp.getCheckedRadioButtonId()!=-1){
	                            int idd= RadGrp.getCheckedRadioButtonId();
	                            View radioButton = RadGrp.findViewById(id);
	                            int radioId = RadGrp.indexOfChild(radioButton);
	                            RadioButton btn = (RadioButton) RadGrp.getChildAt(radioId);
	                           String  getGender = (String) btn.getText();
	                            
	                            Log.i("Bar App",getGender);
						}

						}*/
						/*
						if (type.equals("radioButton")){
						}*/                                
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}		
			return "";
		}
		
		
		public String checkValidation(Fragment frag, FragmentActivity fragActivity)
		{
			String errorMsg = null;
			for (int i=0; i <vcontrols.length; i++)
			{
				if (vcontrols[i].getMandatory().equals("yes"))
				{
					try {
						String controlName = vcontrols[i].getControl_id();
						int id = frag.getResources().getIdentifier(controlName, "id", fragActivity.getPackageName());
						View view= frag.getView().findViewById(id);
						String type = vcontrols[i].getType();
						
						if (type.equals("editText")){
							EditText et = (EditText) view;
							errorMsg = compareValidation(et,vcontrols[i]);
							if(!errorMsg.equals(""))
	{
								return errorMsg;
							}
						}
						/*if (type.equals("RadioGroup")){  //implement at the time when any of the one is mandatory
							
							RadioGroup RadGrp = (RadioGroup) view;
							if(RadGrp.getCheckedRadioButtonId()!=-1){
	                            int idd= RadGrp.getCheckedRadioButtonId();
	                            View radioButton = RadGrp.findViewById(id);
	                            int radioId = RadGrp.indexOfChild(radioButton);
	                            RadioButton btn = (RadioButton) RadGrp.getChildAt(radioId);
	                           String  getGender = (String) btn.getText();
	                            
	                            Log.i("Bar App",getGender);
						}

						}*/
						/*
						if (type.equals("radioButton")){
						}*/                                
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}		
			return "";
		}
		
		
		private String compareValidation(EditText et, Vcontrols vcontrols) {
			Editable valueStr;
			valueStr = et.getText();
			if(vcontrols.getValidate().equals("Empty"))
			{
				if (valueStr.toString().trim().length()==0)
					return vcontrols.getErrorMsg();
			}
			
			if (vcontrols.getValidate().equals("Min-Max")){
				int min = Integer.parseInt(vcontrols.getMin_length());
				int max = Integer.parseInt(vcontrols.getMax_length());
				
				if (valueStr.length()<min || valueStr.length()>max)
					return vcontrols.getErrorMsg();
			}
			
			if (vcontrols.getValidate().equals("EMail")){
				//flag = Validation.isEmailAddress(et, true);
				ValidateEmail(valueStr);
				if (!isValid)
				return vcontrols.getErrorMsg();
			}
			
			/*if (operationalObjPs.getValidate().equals("PhoneNumber")){
				flag = Validation.isPhoneNumber(et, false);
				if (flag)
				return operationalObjPs.getErrorMsg();
			}*/
			if (vcontrols.getValidate().equals("Integer")){
				return vcontrols.getErrorMsg();
			}
			return "";
			
		}
	
		public void initValidation(String ActivityName)	{
			
			for (int i=0; i <vActivityList.getActivitylist().length; i++)//carry out the activity array
			{
				if (vActivityList.getActivitylist()[i].getActivityName().equals(ActivityName))
				{
					for (int j=0; j<vActivityList.getActivitylist()[i].getControllist().length; j++)//carry out the control array
					{
						vcontrols = vActivityList.getActivitylist()[i].getControllist();
						return;
						
					}
				}
			}
		}	
	

}
