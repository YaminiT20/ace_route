package com.aceroute.mobile.software.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderPart;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.Parts;
import com.aceroute.mobile.software.dialog.MySearchDialog;
import com.aceroute.mobile.software.dialog.MySearchDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.SavePartDataRequest;
import com.aceroute.mobile.software.requests.Save_DeletePartRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class AddEditPartsFragment extends BaseFragment implements HeaderInterface, RespCBandServST{
	Context mContext;
	private EditText edt_barcode,edtMaterialCategory, edtMaterialQty;
	ImageView imv_barcode;
	EditText barcodeEdtView;
	final int BARCODER_RESULT = 101;
	private TextView txtMaterialCategoryTitle, txt_category,txt_qty;
	private WebView webviewAddOrderPart;
	private FrameLayout cont_webviewAddOrderPart;
	private LinearLayout parentView;

	
	HashMap<Long, Parts> partTypeList;
	String currentOdrId ,headerText ,tempheaderText;
	
	ArrayList<String> mCategoryArryList;
	ArrayList<Long> mCategoryArryListId;
	Long[] keys;
	long partTypeId=-1;
	String partQuant=null;
	String partBar=null;
	String partId = null;
	
	private static int SAVE_PART = 1;
	private int mheight = 500;
	private TextView edtMaterialDesc;

	private long elemtSelectedInSearchDlog= -1;

	/*@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Utilities.log(mActivity, "onAttach called for AddEditPartsFragment");
	}*/
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utilities.log(mActivity, "onCreate1 called for AddEditPartsFragment");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Utilities.log(mActivity, "onCreateView called for AddEditPartsFragment");	
		View v = inflater.inflate(R.layout.activity_add_edit_material, null);
		TypeFaceFont.overrideFonts(mActivity, v);
		mActivity.registerHeader(this);
		mContext = mActivity;

		initiViewReference(v);
		mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		return v;

	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Utilities.log(mActivity, "onActivityCreated called for AddEditPartsFragment");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Utilities.log(mActivity, "onStart called for AddEditPartsFragment");
	}
	@Override
	public void onResume() {
		super.onResume();
		Utilities.log(mActivity, "onResume called for AddEditPartsFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		Utilities.log(mActivity, "onPause called for AddEditPartsFragment");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Utilities.log(mActivity, "onStop called for AddEditPartsFragment");
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Utilities.log(mActivity, "onDestroyView called for AddEditPartsFragment");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Utilities.log(mActivity, "onDestroy called for AddEditPartsFragment");
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		Utilities.log(mActivity, "onDetach called for AddEditPartsFragment");
	}
	
    private void initiViewReference(View v)
	{		
		final Bundle bundle = this.getArguments();
		currentOdrId = bundle.getString("OrderId");
		tempheaderText = bundle.getString("MaterialType");

		headerText = PreferenceHandler.getPartHead(mActivity);//mBundle.getString("HeaderText");
		if (headerText!=null && !headerText.equals("")) {
			if (bundle.getString("MaterialType").equals("ADD PART"))
				headerText = "ADD " + headerText;
			else
				headerText = "EDIT " + headerText;
		}
		else {
			headerText = bundle.getString("MaterialType");// YD just for backup
		}

		mActivity.setHeaderTitle("", headerText, "");
		
		HashMap<Long, Parts> unsortedPartList = (HashMap<Long, Parts>) DataObject.partTypeXmlDataStore;
		try {
			partTypeList = sortPartCatagoryLst(unsortedPartList);


			keys = partTypeList.keySet().toArray(new Long[partTypeList.size()]);

		
		mCategoryArryList = new ArrayList<String>();
		mCategoryArryListId = new ArrayList<Long>();

		for(int i = 0; i < partTypeList.size(); i++)
		{
				Parts parttypeObj = partTypeList.get(keys[i]);
				if (parttypeObj.getCtid().contains("|")) {
					String[] arr = parttypeObj.getCtid().split(Pattern.quote("|"));
					String str = "";
					for (int a = 0;a<arr.length; a++) {
						str = arr[a];
						if (str.equalsIgnoreCase(PreferenceHandler.getPrefCtid(mActivity))) {
							mCategoryArryList.add(parttypeObj.getName());
							mCategoryArryListId.add(parttypeObj.getId());
						}
					}
				}


				if (parttypeObj.getCtid().equalsIgnoreCase("") || parttypeObj.getCtid().equalsIgnoreCase(PreferenceHandler.getPrefCtid(mActivity))) {
					mCategoryArryList.add(parttypeObj.getName());
					mCategoryArryListId.add(parttypeObj.getId());
			}
		}
		}
		catch (Exception e)
		{

		}
		
		parentView = (LinearLayout) v.findViewById(R.id.parentView);

		cont_webviewAddOrderPart =(FrameLayout)v.findViewById(R.id.cont_webviewAddOrderPart);
		webviewAddOrderPart = new WebView(mActivity.getApplicationContext());
			
		webviewAddOrderPart.setBackgroundColor(Color.TRANSPARENT);
		webviewAddOrderPart.loadUrl("file:///android_asset/loading.html");
		cont_webviewAddOrderPart.addView(webviewAddOrderPart);
	
		txtMaterialCategoryTitle = (TextView) v.findViewById(R.id.txtMaterialCategoryTitle);
		txtMaterialCategoryTitle.setText(bundle.getString("MaterialType"));
		
		edtMaterialCategory = (EditText) v.findViewById(R.id.edtMaterialCategoryName);
		edtMaterialQty = (EditText) v.findViewById(R.id.edtMaterialQuantity);
		imv_barcode = (ImageView) v.findViewById(R.id.imv_barcode);
		edtMaterialDesc = (TextView) v.findViewById(R.id.edtMaterialDesc);

		txt_category = (TextView) v.findViewById(R.id.txt_category_prtfrag);
		txt_qty = (TextView) v.findViewById(R.id.txt_qty_prtfrag);
		edt_barcode=(EditText)v.findViewById(R.id.edt_sku);

		edtMaterialCategory.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
		edtMaterialQty.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
		edt_barcode.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
		txt_category.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
		txt_qty.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
		edtMaterialDesc.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));

		/*if(partTypeList!=null) {
			Map.Entry<Long, Parts> entry = partTypeList.entrySet().iterator().next();// YD getting the first element of the list_cal
			//edtMaterialCategory.setText(entry.getValue().getName());
			//edtMaterialCategory.setTag(entry.getValue().getId());
		}*/

		if (tempheaderText.equals("EDIT PART"))
		{

			partTypeId = bundle.getLong("partTypeId");
			partQuant = bundle.get("partQuantity").toString();
			partBar = bundle.get("partBarcode").toString();
			partId = bundle.get("orderPartId").toString();

			if (partTypeList.get(partTypeId)!=null) {
				edtMaterialCategory.setText(partTypeList.get(partTypeId).getName());
				edtMaterialCategory.setTag(partTypeList.get(partTypeId).getId());
				elemtSelectedInSearchDlog = partTypeList.get(partTypeId).getId();
				edtMaterialQty.setText(partQuant);
				edt_barcode.setText(partBar);
				edtMaterialDesc.setText(partTypeList.get(partTypeId).getDesc());
			}
			else{
				Map.Entry<Long, Parts> entry1 = partTypeList.entrySet().iterator().next();
				edtMaterialCategory.setText(mCategoryArryList.get(0));
				edtMaterialCategory.setTag(entry1.getValue().getId());
				elemtSelectedInSearchDlog = entry1.getValue().getId();
			}

		}


		else{

			try {
				Map.Entry<Long, Parts> entry1 = partTypeList.entrySet().iterator().next();


				edtMaterialCategory.setText(mCategoryArryList.get(0));
				edtMaterialCategory.setTag(mCategoryArryListId.get(0));
				elemtSelectedInSearchDlog = mCategoryArryListId.get(0);
				edtMaterialQty.setText("1");
				edtMaterialDesc.setText(entry1.getValue().getDesc());
			}
			catch (Exception e)
			{

			}
		}		


		edtMaterialCategory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideSoftKeyboard();
				showCategoryDialog();
			}
		});	
		

		parentView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				hideSoftKeyboard();
				return false;
			}
		});


		imv_barcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(mActivity,SimpleScannerActivity.class);
				startActivityForResult(intent,BARCODER_RESULT);

			}
		});


	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Uri selectedImage;

		if (resultCode == mActivity.RESULT_OK) {

			if (requestCode == BARCODER_RESULT) {
				String barcode = data.getStringExtra("barcode_data");

				if (barcode != null) {
					edt_barcode.setText(barcode);
					//edt_barcode = null;

				}

			}

		}
	}

	MySearchDialog searchDialg;

	@SuppressLint("NewApi")
	private void showCategoryDialog()

	{
		try {
		searchDialg = new MySearchDialog(mContext, "Category", "", mCategoryArryListId,mCategoryArryList , elemtSelectedInSearchDlog);



			elemtSelectedInSearchDlog = mCategoryArryListId.get(0);
		}
		catch (Exception e)
		{

		}


		searchDialg.setkeyListender(new MySearchDiologInterface(){
			@Override
			public void onButtonClick() {
				super.onButtonClick();
				searchDialg.cancel();
			}

			@Override
			public void onItemSelected(Long idSelected, String nameSelected) {
				super.onItemSelected(idSelected, nameSelected);
                   // nameSelected=mCategoryArryList.get(0);
				edtMaterialCategory.setText(nameSelected);
				Parts parttypeObj = partTypeList.get(idSelected);
				edtMaterialCategory.setTag(String.valueOf(parttypeObj.getId()));
				edtMaterialDesc.setText(parttypeObj.getDesc());
				elemtSelectedInSearchDlog = idSelected;
				searchDialg.cancel();
			}
		});
		searchDialg.setCanceledOnTouchOutside(true);
		searchDialg.onCreate1(null);
		searchDialg.show();

		Utilities.setDividerTitleColor(searchDialg, mheight, mActivity);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(searchDialg.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.gravity = Gravity.CENTER;
		searchDialg.getWindow().setAttributes(lp);

	}
	
	/*private void showCategoryDialog()
	{
		try{
			ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity) mContext, android.R.layout.select_dialog_item, mCategoryArryList);
			AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mContext);
		        builder.setCancelable(true);
			builder.setTitle("Category");
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				@SuppressLint("NewApi")
				public void onClick(DialogInterface dialog, int position) {
					edtMaterialCategory.setText(mCategoryArryList.get(position));

					Parts parttypeObj = partTypeList.get(keys[position]);
					edtMaterialCategory.setTag(String.valueOf(parttypeObj.getId()));
					edtMaterialDesc.setText(parttypeObj.getDesc());
				}
			});
					
			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			
			final AlertDialog dialog = builder.create();
			Utilities.setAlertDialogRow(dialog, mActivity);
			dialog.show();
			//dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, 1170);// YD width , height

			Utilities.setDividerTitleColor(dialog, mheight, mActivity);
				
			Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			Utilities.setDefaultFont_12(button_negative);	
			Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Utilities.setDefaultFont_12(button_positive);
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/


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
					if (response.getId()==SAVE_PART)
					{
						if (tempheaderText.equals("ADD PART"))
							getAndUpdateNumberOfOrderParts(currentOdrId);
						goBack(mActivity.SERVICE_Thread);
					}
				}
				 else if(response.getStatus().equals("success")&& 
							response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
				  {
					 if (response.getId()==SAVE_PART)
						 goBack(mActivity.SERVICE_Thread);
				  }
			}
		
	}

	private void getAndUpdateNumberOfOrderParts(String orderId) {
		HashMap< Long , Order> orderMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
		Order odrObj = orderMap.get(Long.parseLong(orderId));
		odrObj.setCustPartCount(odrObj.getCustPartCount()+1);
		 DataObject.ordersXmlDataStore=orderMap;
	}
	private void goBack(final int threadType) {

		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mActivity.popFragments(threadType);
				cont_webviewAddOrderPart.removeAllViews();
				webviewAddOrderPart.destroy();		
			}
		});
	}

	public void showdialog(){

		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("Action Required");
		builder.setMessage("Please Select Category");

		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();

		//YD 2020 doing UI setting for dialog
		Utilities.setDividerTitleColor(alertDialog, mheight, mActivity);
		Button button_positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		Utilities.setDefaultFont_12(button_positive);

	}


	@Override
	public void headerClickListener(String callingId) {
		// TODO Auto-generated method stub
		if(callingId.equals(BaseTabActivity.HeaderDonePressed)) {
			
			/*var str = '{"url":"'+"https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi"+'",'+'"type": "post",'+'"action": "'saveorderpart'",'+
					'"data":{"oid": "'+orderId+'",'+'"id": "'0'",'+'"tid": "'+partTypeId+'",'+'"qty": "'+partQtyAdd+'",'+
			        '"stmp": "'+tstamp+'",'+'"action": "'saveorderpart'"}}';*/

			if (edtMaterialCategory.getText().toString().isEmpty()) {
					showdialog();
			} else {
				long tstamp = new Date().getTime();
				Save_DeletePartRequest req = new Save_DeletePartRequest();
				req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(getActivity()) + "/mobi");
				req.setType("post");
				req.setAction(OrderPart.ACTION_SAVE_ORDER_PART);

				SavePartDataRequest dreq = new SavePartDataRequest();
				dreq.setOid(currentOdrId); // Order Id

				if (tempheaderText.equals("ADD PART"))
					dreq.setPartId("0");
				else if (tempheaderText.equals("EDIT PART"))
					dreq.setPartId(partId);

				dreq.setTid(String.valueOf(edtMaterialCategory.getTag())); // Category Type Id

				if (String.valueOf(edtMaterialQty.getText()).equals("") || edtMaterialQty.getText() == null)
					dreq.setQuantity("0"); // Quantity
				else
					dreq.setQuantity(String.valueOf(edtMaterialQty.getText())); // Quantity

				/*if (String.valueOf(edt_barcode.getText()).equals("") || edt_barcode.getText() == null)
					dreq.setSetbarcode(""); // Quantity
				else*/



				dreq.setStmp(String.valueOf(tstamp)); // Time Stamp
				dreq.setSetbarcode(String.valueOf(edt_barcode.getText().toString())); // Time Stamp
				dreq.setTimeStamp(String.valueOf(tstamp));

				req.setDataObj(dreq);

				OrderPart.saveData(req, mActivity, AddEditPartsFragment.this, SAVE_PART);




				cont_webviewAddOrderPart.setVisibility(View.VISIBLE);
			}

			if (mActivity.getCurrentFocus() != null) {
				InputMethodManager mgr = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
				mgr.hideSoftInputFromWindow(edtMaterialQty.getWindowToken(), 0);
			}
		}
	}

	public static String strSeparator = "|";
	public static String[] convertStringToArray(String str){
		String[] arr = str.split(Pattern.quote("|"));
		convertArrayToString(arr);
		return arr;
	}

	public static String convertArrayToString(String[] array){
		String str = "";
		for (int i = 0;i<array.length; i++) {
			str = array[i];
			Log.d("TAG97",str);

		}
		return str;
	}

	public void hideSoftKeyboard() {
		if(mActivity.getCurrentFocus()!=null){
		InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
		}
	}
	
	// Sorting hashMap
		private static HashMap<Long, Parts> sortPartCatagoryLst(HashMap<Long, Parts> unsortMap) {
			// Convert Map to List
			List<Map.Entry<Long, Parts>> list =
				new LinkedList<Map.Entry<Long, Parts>>(unsortMap.entrySet());

			// Sort list_cal with comparator, to compare the Map values
			Collections.sort(list, new Comparator<Map.Entry<Long, Parts>>() {
				public int compare(Map.Entry<Long, Parts> o1,
								   Map.Entry<Long, Parts> o2) {
					return (o1.getValue().getName()).compareTo(o2.getValue().getName());
				}
			});

			// Convert sorted map back to a Map
			HashMap<Long, Parts> sortedMap = new LinkedHashMap<Long, Parts>();
			for (Iterator<Map.Entry<Long, Parts>> it = list.iterator(); it.hasNext();) {
				Map.Entry<Long, Parts> entry = it.next();
				sortedMap.put(entry.getKey(), entry.getValue());
			}
			return sortedMap;
		}

		public void loadDataOnBack(BaseTabActivity context) {
			context.registerHeader(this);
			
		}
}
