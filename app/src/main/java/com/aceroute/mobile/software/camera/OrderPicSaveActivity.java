package com.aceroute.mobile.software.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderMedia;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.fragment.BaseFragment;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.SaveMediaRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;

import java.io.File;
import java.util.ArrayList;

public class OrderPicSaveActivity extends BaseFragment implements View.OnClickListener , HeaderInterface , RespCBandServST  {

	Button saveCamerapic ,btn_edit,btn_delete;;
	EditText description;
	ImageView imagePic, backbtn;
	RelativeLayout rlt_camerapic;
	private LinearLayout editDelsavImg;

	ArrayList<String> text_Array = new ArrayList<String>();

	ArrayList<String> FilePathStrings;
	ArrayList<String> FileNameStrings;
	int positionnumber = 0;
	String sendImg2edt;
	static String actualImgSav;
	static int imgAnnotationsav;
	static String actualImgAnnSav;
	TextView header , subHeader,headsign;
	MyDialog dialog= null;
	final Context contextdialog = mActivity;
	private String sourceOfImg;//Cam or GAL
	Order activeOrderObj= null;
	private int SAVEPICTURE=1;
	public String isForm = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/*super.onCreate1(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.savecamerapic);*/
		View v = inflater.inflate(R.layout.savecamerapic, null);
		mActivity.registerHeader(this);

		mActivity.setHeaderTitle("", "ADD "+PreferenceHandler.getPictureHead(getActivity()), "");

		final String orderId = PreferenceHandler.getOrderId4Js(mActivity);
		rlt_camerapic = (RelativeLayout) v.findViewById(R.id.rlt_camerapic);
		editDelsavImg=(LinearLayout)v.findViewById(R.id.rlt_editdeleted_saveimg);
		btn_edit = (Button)v.findViewById(R.id.btn_edit_saveimg);
		btn_delete = (Button)v.findViewById(R.id.btn_deleted_saveimg);
		imagePic = (ImageView)v.findViewById(R.id.img_pic);
		/*backbtn = (ImageView)v.findViewById(R.id.back_button_savepic);
		R_imgebck=(RelativeLayout) v.findViewById(R.id.Bck_button_save);

		header = (TextView)v.findViewById(R.id.header_save_pic);
			header.setTypeface(tf);
		subHeader = (TextView)v.findViewById(R.id.sub_header_save_pic);
			header.setTypeface(tf);
		header.setText(PreferenceHandler.getOrderId4Js(mActivity));*/  //YD later TODO Header

		Typeface tf = TypeFaceFont.getCustomTypeface(mActivity.getApplicationContext());
		headsign = (TextView)v.findViewById(R.id.txt_addPic);
		headsign.setTypeface(tf, Typeface.BOLD);

		/*String S_header=PreferenceHandler.getOrderName4Js(mActivity);
		//	subHeader.setText(PreferenceHandler.getOrderName4Js(this));
		//	subHeader.setText(PreferenceHandler.getOrderName4Js(this));
			if(S_header.length()<=15){
				subHeader.setText(S_header);
			}else {
				subHeader.setText(S_header.substring(0 , 17)+"..");
			}*/// YD

		//subHeader.setText(PreferenceHandler.getOrderName4Js(this));


		Bundle extra = getArguments();
		final Uri jpegImage = Uri.parse(extra.getString("jpegImage"));
		sourceOfImg = extra.get("type").toString();
		if (sourceOfImg.equals("CAM")){
		actualImgSav = jpegImage.getPath();
		 sendImg2edt = jpegImage.toString();
		}
		else if (sourceOfImg.equals("GAL")){
			actualImgSav = Utilities.getPath(jpegImage,mActivity);
			sendImg2edt = actualImgSav;
		}

		 FilePathStrings= new ArrayList<String>();
		 FileNameStrings= new ArrayList<String>();
		description = (EditText)v.findViewById(R.id.edt_imagename);
		description.setTypeface(tf);

		/*saveCamerapic = (Button)v.findViewById(R.id.btn_camera);
		saveCamerapic.setTypeface(tf);
		saveCamerapic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String file_desc = description.getText().toString();

				if(file_desc.equals("")){
					//Utilities.showCustomDialog(OrderPicSaveActivity.this, ((AceRouteApplication)getApplicationContext()).ErrorStringArr.get(0));
					dialog = new MyDialog(contextdialog, "Slight problem with data", "Would you like to continue?","OK");
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setkeyListender(new MyDiologInterface() {

						@Override
				public void onPositiveClick() throws JSONException {

						}

						@Override
						public void onNegativeClick() {
							dialog.dismiss();

						}});

					dialog.onCreate1(null);
					dialog.show();


				}else {

				String tistamp = String.valueOf(Utilities.getCurrentTime());
				String str="";
				if (type.equals("CAM")){
					 str = "{\"url\":\""+Api.API_BASE_URL+"\",\"type\":\"post\",\"data\":{\"id\":\"0\",\"oid\":\""+orderId+"\"," +
							"\"tid\":\"1\",\"file\":\""+""+"\",\"dtl\":\""+file_desc+"\",\"action\":\"savefile\"," +
							"\"stmp\":\""+tistamp+"\",\"mime\":\"jpg\",\"metapath\":\""+actualImgSav+"\",\"copy\":\"no\"}}";
					}
				else if (type.equals("GAL")){
					 str = "{\"url\":\""+Api.API_BASE_URL+"\",\"type\":\"post\",\"data\":{\"id\":\"0\",\"oid\":\""+orderId+"\"," +
							"\"tid\":\"1\",\"file\":\""+""+"\",\"dtl\":\""+file_desc+"\",\"action\":\"savefile\"," +
							"\"stmp\":\""+tistamp+"\",\"mime\":\"jpg\",\"metapath\":\""+actualImgSav+"\",\"copy\":\"yes\"}}";
					}

				AceRouteService.jsInterface.saveImage(str,"OrderPics");
				//finish();
			//}
			}
		});*/ //YD later TODO


		imagePic.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				//editDelsavImg.setVisibility(View.VISIBLE);
				editDelsavImg.setVisibility(View.GONE);

				FileNameStrings.clear();
				FilePathStrings.clear();

				String file_desc = description.getText().toString();
				FileNameStrings.add(file_desc);
				FilePathStrings.add(sendImg2edt);

				CaptureSignature captureSignFrag = new CaptureSignature();
				Bundle b = new Bundle();
				b.putStringArrayList("filepath", FilePathStrings);
				b.putStringArrayList("filename", FileNameStrings);
				b.putString("Flag", "");
				b.putInt("position", positionnumber);
				captureSignFrag.setArguments(b);

				mActivity.pushFragments(Utilities.JOBS, captureSignFrag, true, true,BaseTabActivity.UI_Thread);
				return false;

				/*Intent i = new Intent(OrderPicSaveActivity.this,CaptureSignature.class);
				i.putExtra("filepath", FilePathStrings);
				i.putExtra("filename", FileNameStrings);
				i.putExtra("Flag", "");
				i.putExtra("position", positionnumber);
				startActivityForResult(i, 0);*/
			}
		});

		/*btn_edit.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						editDelsavImg.setVisibility(View.GONE);

						FileNameStrings.clear();
						FilePathStrings.clear();

						String file_desc = description.getText().toString();
						FileNameStrings.add(file_desc);
						FilePathStrings.add(sendImg2edt);

						Intent i = new Intent(OrderPicSaveActivity.this,CaptureSignature.class);

						i.putExtra("filepath", FilePathStrings);
						i.putExtra("filename", FileNameStrings);
						i.putExtra("Flag", "");
						i.putExtra("position", positionnumber);
						startActivityForResult(i, 0);
					}
		});

		btn_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				editDelsavImg.setVisibility(View.GONE);
			}
		});

		R_imgebck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//finish(); YD later
			}
		});*/

		//YD may be hidding soft keys check plz
		rlt_camerapic.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(mActivity.getCurrentFocus()!=null){
				InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
				}
			   return false;
			}
		});
			    	
	return v;
		
	}
	
	
	/*@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		int maskedAction = event.getActionMasked();

	    switch (maskedAction) {

	    case MotionEvent.ACTION_DOWN:
	    	
	    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.
					INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			
	    	break;
	    }
	    return true;
	}*/ // YD later TODO
	
	@Override
	public void onResume()
	{
		super.onResume();
		//BaseTabActivity.setHeaderTitle("", "ADD PICTURE", "");
		if (imgAnnotationsav ==1){
			imgAnnotationsav=0;
			actualImgSav = actualImgAnnSav;
			
			File imgFile = new File(actualImgAnnSav);
			if(imgFile.exists()){
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize=2;
			    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
				Log.i("yash", "show bitmap image");
				imagePic.setScaleType(ImageView.ScaleType.CENTER);
				imagePic.setImageBitmap(myBitmap);
			}
		}
		else{
			File imgFile = new File(actualImgSav);
			if(imgFile.exists()){
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize=2;
			    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
				Log.i("yash", "show bitmap image");
				imagePic.setScaleType(ImageView.ScaleType.CENTER);
			    imagePic.setImageBitmap(myBitmap);
			}
		}
	}


	public static void edtdSignedImg( File imgFile , String path, int setimageAnn )
	{
		//actualImgSav = path;
		imgAnnotationsav= setimageAnn;
		if (imgAnnotationsav==1)
		{
			actualImgAnnSav= path;
			Gridview_MainActivity.createThumbImage(path);
		}
	}
	
	
	public void edtdSignedImg(File f) {
		// TODO Auto-generated method stub
		
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
				if (response.getId()==SAVEPICTURE)
				{
//					if (isForm.equalsIgnoreCase("1")) {
//						activeOrderObj.setCustMetaCount(activeOrderObj.getCustMetaCount() + 1);
//						activeOrderObj.setImgCount(activeOrderObj.getImgCount() + 1);
//						Gridview_MainActivity.isSyncMedia = true;
//						goBack(mActivity.SERVICE_Thread);
//					} else {
						activeOrderObj.setCustMetaCount(activeOrderObj.getCustMetaCount() + 1);
						activeOrderObj.setImgCount(activeOrderObj.getImgCount() + 1);
						Gridview_MainActivity.isSyncMedia = true;

						goBack(mActivity.SERVICE_Thread);

//					}
				}
			}
			else if(response.getStatus().equals("success")&& 
					response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
			{
			}
		}
		
	}
	
	private void goBack(final int threadType) {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mActivity.popFragments(threadType);
			}
		});
	}


	@Override
	public void headerClickListener(String callingId) {
		if (callingId.equals(mActivity.HeaderDonePressed))
		{
			String file_desc = description.getText().toString();
			String tistamp = String.valueOf(Utilities.getCurrentTime());
			SaveMediaRequest req = new SaveMediaRequest();
			req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
			req.setType("post");
			req.setId("0");
			//req.setOrderGeo("23.344,77.465");
			req.setOrderId(String.valueOf(activeOrderObj.getId()));
			req.setTid("1");//4
			req.setFile("");
			req.setDtl(file_desc);//""
			req.setAction(OrderMedia.ACTION_MEDIA_SAVE);
			req.setTimestamp(tistamp);
			req.setMime("jpg");//""
			req.setMetapath(actualImgSav);
			if (sourceOfImg.equals("CAM"))
				req.setCopy("no");
			else if (sourceOfImg.equals("GAL"))
				req.setCopy("yes");
			OrderMedia.saveData(req, mActivity, this, SAVEPICTURE);

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


	public void loadDataOnBack(BaseTabActivity mActivity) {
		mActivity.registerHeader(this);
		BaseTabActivity.setHeaderTitle("", "ADD "+PreferenceHandler.getPictureHead(getActivity()), "");
		
		if (imgAnnotationsav ==1){
			imgAnnotationsav=0;
			actualImgSav = actualImgAnnSav;
			
			File imgFile = new File(actualImgAnnSav);
			if(imgFile.exists()){
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize=2;
			    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
			    Log.i("yash", "show bitmap image");
				imagePic.setScaleType(ImageView.ScaleType.CENTER);
			    imagePic.setImageBitmap(myBitmap);
			}
		}
		else{
			File imgFile = new File(actualImgSav);
			if(imgFile.exists()){
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize=2;
			    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
			    Log.i("yash", "show bitmap image");
				imagePic.setScaleType(ImageView.ScaleType.CENTER);
			    imagePic.setImageBitmap(myBitmap);
			}
		}
	}
	
}
