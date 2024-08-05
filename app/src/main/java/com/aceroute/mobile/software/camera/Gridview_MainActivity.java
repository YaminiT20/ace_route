package com.aceroute.mobile.software.camera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.SplashII;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.audio.PlayAudioFragment;
import com.aceroute.mobile.software.audio.RecordAudioFragment;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderMedia;
import com.aceroute.mobile.software.component.reference.CustHistoryToken;
import com.aceroute.mobile.software.component.reference.CustHistoryTokenGroup;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.database.DBEngine;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.fragment.BaseFragment;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.DeleteMediaRequest;
import com.aceroute.mobile.software.requests.GetFileMetaRequest;
import com.aceroute.mobile.software.requests.SaveMediaRequest;
import com.aceroute.mobile.software.utilities.JSONHandler;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.ipaulpro.afilechooser.utils.FileUtils;

import org.json.JSONException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Gridview_MainActivity extends BaseFragment implements OnClickListener , HeaderInterface , RespCBandServST {

	// Declare variables

	protected static final int REQUEST_CAMERA = 0;
	protected static final int SELECT_FILE = 1;
	private ArrayList<String> FilePathStrings;
	private ArrayList<String> FileNameStrings;
	private ArrayList<Long> FileIdsList;
	private ArrayList<String> Formkeylist;
	private ArrayList<String> tempFilepathstring;
	private File[] listFile;
	private boolean longpressonimage = false;
	GridView grid;
	public GridViewAdapter adapter;
	public static boolean isSyncMedia = false;
	File file;
	private Button btn_camera, btn_edit, btn_delete;
	private LinearLayout rlt_editdeleted;
	int positionnumber;
	private ImageView back_button;
	RelativeLayout R_imgebck;
	public Uri uri = null;
	OrderMedia orderPicsArr[];
	TextView header, subHeader;
	public static int fmetaType = 0;
	String currentOdrId, currentOdrName, headerText;
	Context contextdialog = null;
	String TypeOfPicShow, taskId;
	String nmForThumb;

	MyDialog dialog = null;
	private int GETFILEMETA = 1;
	private int DELETEMEDIA = 2;
	private int mheight = 500;

	public static Order activeOrderObj = null;

	private static final int REQUEST_CODE_ACTIVITY_PICK_CAMERA = 22;
	private static final int REQUEST_CODE_ACTIVITY_PICK_GALLERY = 23;
	private static final int REQUEST_CODE_ACTIVITY_PICK_FILE = 24;
	private boolean isCamGalOptSeletected = false;
	private int SAVEFILE = 3;
	private ArrayList<String> FileUpdList;
	static int TIME_PICKER_INTERVAL=5;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.e("Aceroute", "OnCreateView called for Grid view main activity");
		View v = inflater.inflate(R.layout.gridview_main, null);

		if (savedInstanceState != null) {
			//Toast.makeText(mActivity, "Handling saveInstance data now.", Toast.LENGTH_LONG).show();
			Log.e("Aceroute", "saveInstance data has DataObject as : " + DataObject.ordersXmlDataStore);
			activeOrderObj = savedInstanceState.getParcelable("activeOrderObj");
			Log.e("Aceroute", "saveInstance data has active order obj as :" + activeOrderObj);

		}

		//carrying out values from bundle
		Bundle extra = this.getArguments(); // check YD
		//1= pictures  , 2= signature
		fmetaType = extra.getInt("fmetaType");
		if (fmetaType == 1) {
			ViewImage.headingFlag = 1;
		} else if (fmetaType == 2) {
			ViewImage.headingFlag = 2;
		}
		currentOdrId = extra.getString("OrderId");
		currentOdrName = extra.getString("OrderName");
		headerText = extra.getString("HeaderText");
        BaseTabActivity.setHeaderTitle("", headerText, "");

		contextdialog = mActivity.getApplicationContext();
		mActivity.registerHeader(this);

		rlt_editdeleted = (LinearLayout) v.findViewById(R.id.rlt_editdeleted);
		btn_edit = (Button) v.findViewById(R.id.btn_edit_grid);
		btn_delete = (Button) v.findViewById(R.id.btn_deleted);
		// Locate the GridView in gridview_main.xml
		grid = (GridView) v.findViewById(R.id.gridview);
		Typeface tf = TypeFaceFont.getCustomTypeface(mActivity.getApplicationContext());

		// Setting listeners for deleting from grid_cal
		grid.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View arg1,
										   int position, long arg3) {

				if(SplashII.wrk_tid >=8)
					return false;
				//commented code if for showing delete button from below on ui.
				/*longpressonimage = true;
				positionnumber = position;
				rlt_editdeleted.setVisibility(View.VISIBLE);*/
				longpressonimage = true;
				positionnumber = position;
				//	String D_title,D_desc,yes;
				//	D_title= "Delete Confirmation."; //Utilities.getStringForAppAndr(mActivity,2)
				//	D_desc= "Would you like to Delete."; //Utilities.getStringForAppAndr(mActivity,15);
				//yes=Utilities.getStringForAppAndr(Gridview_MainActivity.this,12);

				//YD using below if code for not deleting customer history file
				if (fmetaType == 4 && FileIdsList.get(positionnumber) == -2) {
					dialog = new MyDialog(mActivity, getResources().getString(R.string.msg_title), getResources().getString(R.string.msg_delete_history_file), "OK");
					dialog.setkeyListender(new MyDiologInterface() {
						@Override
						public void onPositiveClick() throws JSONException {
							dialog.dismiss();
						}

						@Override
						public void onNegativeClick() {
							dialog.dismiss();
						}
					});
					dialog.onCreate(null);
					dialog.show();
				}
				else{
					dialog = new MyDialog(mActivity, getResources().getString(R.string.msg_title), getResources().getString(R.string.msg_delete), "DELETE");
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setkeyListender(new MyDiologInterface() {


					@Override
					public void onPositiveClick() throws JSONException {
						dialog.dismiss();
					}

					@Override
					public void onNegativeClick() {
						rlt_editdeleted.setVisibility(View.GONE);

						// code to delete image
						//long fileId = orderPicsArr[positionnumber].getId();
						long fileId = FileIdsList.get(positionnumber);

						/*String requestStr = "{\"url\":\"" + "https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi"
								+ "\",\"type\":\"post\",\"data\":{\"oid\":\"" + activeOrderObj.getId()
								+ "\"," + "\"action\":\"deletefile\"," + "\"id\":\""+ fileId + "\"}}";
						  AceRouteService.jsInterface.deleteImage(requestStr);
								*/
						DeleteMediaRequest req = new DeleteMediaRequest();
						req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
						req.setType("post");
						req.setOid(String.valueOf(activeOrderObj.getId()));
						req.setAction(OrderMedia.ACTION_MEDIA_DELETE);
						req.setFileId(String.valueOf(fileId));

						OrderMedia.deleteData(req, mActivity, Gridview_MainActivity.this, DELETEMEDIA);

						dialog.dismiss();

						}
					});

					dialog.onCreate(null);
					dialog.show();

					Utilities.setDividerTitleColor(dialog, 0, mActivity);

					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
					lp.copyFrom(dialog.getWindow().getAttributes());
					lp.width = WindowManager.LayoutParams.MATCH_PARENT;
					lp.gravity = Gravity.CENTER;
					dialog.getWindow().setAttributes(lp);
				}
				return false;
			}
		});

		// Setting listeners to open gallery for pics
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				if (longpressonimage == true) {
					longpressonimage = false;
					return;
				}
				if (fmetaType==3) {

					String filename = FilePathStrings.get(position);
					String fileid   = FileIdsList.get(position).toString();
					Bundle bundle=new Bundle();
					bundle.putString("path", filename);
					bundle.putString("fileid", fileid);

					PlayAudioFragment playAudioFragment	= new PlayAudioFragment();
					playAudioFragment.setActiveOrderObj(activeOrderObj);
					playAudioFragment.setArguments(bundle);
					mActivity.pushFragments(Utilities.JOBS, playAudioFragment, true, true,BaseTabActivity.UI_Thread);
				}
				else if(fmetaType==4){
					String path = FilePathStrings.get(position);
					File file = new File(path);//YD for image path reference : storage/emulated/0/AceRoute/temp_1459696858882.jpg
					//new File("sdcard/Ankush goyal.pdf").exists();
					Intent intent = new Intent(Intent.ACTION_VIEW);//file:///storage/emulated/0/Default/HZ15-35-01.pdf

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        intent.setDataAndType(Uri.fromFile(file),"*/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    } else {
                        Uri apkURI = FileProvider.getUriForFile(
                                mActivity,
                                mActivity.getApplicationContext()
                                        .getPackageName() + ".provider", file);
                        intent.setDataAndType(apkURI, "*/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    startActivity(intent);
                }
				else{
					rlt_editdeleted.setVisibility(View.GONE);
					Intent i = new Intent(mActivity,
							ViewImage.class);
					i.putExtra("filepath", FilePathStrings);
					i.putExtra("filename", FileNameStrings);
					i.putExtra("position", position);
					i.putExtra("inboxrotate",true);
					startActivity(i);
				}

			}

		});
		btn_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(mActivity, CaptureSignature.class);

				rlt_editdeleted.setVisibility(View.GONE);
				i.putExtra("filepath", FilePathStrings);
				i.putExtra("Flag", 1);
				i.putExtra("filename", FileNameStrings);
				i.putExtra("position", positionnumber);
				startActivityForResult(i, 0);
			}
		});


		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putParcelable("activeOrderObj", activeOrderObj);
		// etc.
	}

	/*@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  // Restore UI state from the savedInstanceState.
	  // This bundle has also been passed to onCreate1.
	  boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
	  double myDouble = savedInstanceState.getDouble("myDouble");
	  int myInt = savedInstanceState.getInt("MyInt");
	  String myString = savedInstanceState.getString("MyString");
	} */

	@Override
	public void onStart() {
//		getFileMeta();
//		if (isSyncMedia) {
//			DBEngine.syncDataToSever(mActivity.getApplicationContext(), OrderMedia.TYPE_SAVE);
//			isSyncMedia = false;
//		}
		super.onStart();
		Log.e("Aceroute", "OnStart called for Grid view main activity");

		//mActivity.registerHeader(this);  commenting because causing order pic save to have instance of grid_cal page

	}

	@Override
	public void onResume() {
		Log.e("Aceroute", "onResume called for Grid view main activity");
		// TODO Auto-generated method stub
		super.onResume();
		getFileMeta();
		if (isSyncMedia) {
			DBEngine.syncDataToSever(mActivity.getApplicationContext(), OrderMedia.TYPE_SAVE);
			isSyncMedia = false;
		}
		//mActivity.registerHeader(this);
	}

	private void getFileMeta() {
		long numberOfMeta = getNumberOfOrderMeta();
		Log.e("Aceroute", "getFileMeta called for Grid view main activity at"+Utilities.getCurrentTimeInMillis());
		//if (numberOfMeta >= 0) {
			/*{"url":"'+AceRoute.appUrl+'",'+ '"action": "getfilemeta",' +'"oid":"' +orderId+'"}*/
			GetFileMetaRequest req = new GetFileMetaRequest();
			req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
			req.setAction(OrderMedia.ACTION_GET_MEDIA);
			req.setOid(currentOdrId);
			req.setFrmkey("");
			OrderMedia.getData(req, mActivity, this, GETFILEMETA);
		//}
	}

	private long getNumberOfOrderMeta() {
		Log.e("Aceroute", "getNumberOfOrderMeta called for Grid view main activity");
		return activeOrderObj.getCustMetaCount();
	}

	/*@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		retunNumOfGridElem();
	}*/

	/*private void retunNumOfGridElem() {
		int numOfElemInGrid = FilePathStrings.size();

		//To send number of element to js
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt(AceRoute.REQUEST_ACTION, 16);
		bundle.putString("NumOfElemWithFMeta", String.valueOf(numOfElemInGrid)+"#"+fmetaType);
		msg.setData(bundle);
		AceRoute.handler.sendMessage(msg);

		//finish();

	}*/


	private void openSignPage() {

		Addsign addSignFrag = new Addsign();
		//gridView_MainFrag.setActiveOrderObject(activeOrderObj);
		addSignFrag.setActiveOrderObject(activeOrderObj);
		mActivity.pushFragments(Utilities.JOBS, addSignFrag, true, true, BaseTabActivity.UI_Thread);
	}


	private void setGrid(Object data) {
		/*XMLHandler xmlHandle = new XMLHandler(mActivity);
		Object objArr[] = xmlHandle.getOrderMediaValuesFromXML(dataFrmXml);*/
		Log.i(Utilities.TAG, "setGrid Data received : " + data + " at" + Utilities.getCurrentTimeInMillis());
		HashMap<Long, OrderMedia> odrData = (HashMap<Long, OrderMedia>) data;
		Map<Long, OrderMedia> responseMap = new LinkedHashMap<Long, OrderMedia>();
		responseMap = sortHashmap(odrData);
		ArrayList<String> tbem = new ArrayList<>();


		Long[] keys = null;
		if (responseMap != null) {
			keys = responseMap.keySet().toArray(new Long[responseMap.size()]);
			orderPicsArr = new OrderMedia[responseMap.size()];
		}

		FilePathStrings = new ArrayList<String>();
		FileNameStrings = new ArrayList<String>();
		FileUpdList = new ArrayList<String>();
		FileIdsList = new ArrayList<Long>();
		Formkeylist = new ArrayList<>();
		tempFilepathstring = new ArrayList<>();

		if (responseMap != null) {
			for (int i = 0; i < responseMap.size(); i++) {
				orderPicsArr[i] = responseMap.get(keys[i]);

				/*FilePathStrings.add("https://"+ PreferenceHandler.getPrefBaseUrl(context)+"/mobi" + "?action=getfile&id="
						+ String.valueOf(orderPicsArr[i].getId()) + "&token="
						+ PreferenceHandler.getMtoken4Js(this) + "&nspace="
						+ PreferenceHandler.getCompanyId(this) + "&rid="
						+ String.valueOf(PreferenceHandler.getResId(this)));*/
				if (orderPicsArr[i].getMediatype() == fmetaType ) {

					FilePathStrings.add(orderPicsArr[i].getMetapath());
					FileIdsList.add(orderPicsArr[i].getId());
					Formkeylist.add(orderPicsArr[i].getFormKey());


					String time = getFomattedTime(orderPicsArr[i].getUpd_time());
					FileUpdList.add(time);
					if (orderPicsArr[i].getFile_desc() != null)
						FileNameStrings.add(orderPicsArr[i].getFile_desc());
					else
						FileNameStrings.add("");
				}
			}



			if (fmetaType == 4) {//YD code to add customer history to the file view  // check if opening file type(4)
			CustHistoryTokenGroup availableTokens = PreferenceHandler.getCustomerTokens(mActivity);

			if (availableTokens != null && availableTokens.getCustHistTokenGrp() != null) {
				for (Long key : availableTokens.getCustHistTokenGrp().keySet()) {
					if (availableTokens.getCustHistTokenGrp().get(key).getCustId() == activeOrderObj.getCustomerid()) {
						CustHistoryToken CurrentCustHistTokenObj = availableTokens.getCustHistTokenGrp().get(key);
						if (CurrentCustHistTokenObj.getPath() != null && !CurrentCustHistTokenObj.getPath().equals("")) {
							FilePathStrings.add(CurrentCustHistTokenObj.getPath());// YD path of xls file from
							FileIdsList.add(-2L); // YD saving -2 because we never want to delete this file
							FileUpdList.add(getFomattedTime(Utilities.getCurrentTime()));//YD have to show downloaded time but currently showing current time YD TODO
							FileNameStrings.add("Customer Order History");//YD hardcode to show in description part of grid_cal
						}
					}
				}
			}

				/*String tokenArray[] = null;
				if(availableTokens!= null){
					if (availableTokens.contains("#")) {
						tokenArray = availableTokens.split("#");
					}
					else {
						tokenArray = new String[1];
						tokenArray[0] = availableTokens;
					}

					for(int i=0; i<tokenArray.length; i++){
						String keyToken[] = tokenArray[i].split(",");
						if (keyToken[0].equals(String.valueOf(activeOrderObj.getCustomerid())) && keyToken[2]!=null && !keyToken[2].equals(" ") ){// single space because saving this way when getting at time of syncign
							{
								FilePathStrings.add(keyToken[2]);// YD path of xls file from
								FileIdsList.add(-2L); // YD saving -2 because we never want to delete this file
								FileUpdList.add(getFomattedTime(Utilities.getCurrentTime()));//YD have to show downloaded time but currently showing current time YD TODO
								FileNameStrings.add("Customer Order History");//YD hardcode to show in description part of grid_cal
							}
							break;
						}
						else {
							Log.i( "AceRoute","no need to make request to server for downloading the customer history file");
						}
					}
				}*/
		}

	}
		adapter = new GridViewAdapter(mActivity, FilePathStrings, FileNameStrings , fmetaType,FileUpdList);
		grid.setAdapter(adapter);

	}


	private String getFomattedTime(Long time) {
		String strFDate ="";
		SimpleDateFormat sdf = new SimpleDateFormat("MMM:d:yyyy hh:mm a");
		if (time > 0){
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(time);
			String strDate = String.valueOf(sdf.format(cal.getTime()).split(" ")[0].replace(":", " "));//getting April 03 2016

			String notat="";
			if ((sdf.format(cal.getTime()).split(" ")[2]).equalsIgnoreCase("PM"))
				notat = "pm";
			else if ((sdf.format(cal.getTime()).split(" ")[2]).equalsIgnoreCase("AM"))
				notat = "am";

			int hours = Integer.parseInt(sdf.format(cal.getTime()).split(" ")[1].split(":")[0]);
			int minute = Integer.parseInt(sdf.format(cal.getTime()).split(" ")[1].split(":")[1]);
			String min = String.format("%02d", getRoundedMinute(minute));
			String strTme = String.valueOf(hours + ":" + min + " " + notat);
			 strFDate = strDate+" "+strTme;
		}

		return strFDate;
	}


	public static  int getRoundedMinute(int minute){
		if(minute % TIME_PICKER_INTERVAL != 0){
			int minuteFloor = minute - (minute % TIME_PICKER_INTERVAL);
			minute = minuteFloor + (minute == minuteFloor + 1 ? TIME_PICKER_INTERVAL : 0);
			if (minute == 60)
				minute=0;
		}
		return minute;
	}

	// Sorting hashMap
	private static HashMap<Long, OrderMedia> sortHashmap(HashMap<Long, OrderMedia> unsortMap) {

		HashMap<Long, OrderMedia> sortedMap = new LinkedHashMap<Long, OrderMedia>();

		if (unsortMap!=null){
			// Convert Map to List
			List<Map.Entry<Long, OrderMedia>> list =
					new LinkedList<Map.Entry<Long, OrderMedia>>(unsortMap.entrySet());

			// Sort list_cal with comparator, to compare the Map values
			Collections.sort(list, new Comparator<Map.Entry<Long, OrderMedia>>() {
				public int compare(Map.Entry<Long, OrderMedia> o1,
								   Map.Entry<Long, OrderMedia> o2) {
					return Double.compare(o2.getValue().getUpd_time(), o1.getValue().getUpd_time());
				}
			});

			// Convert sorted map back to a Map
			for (Iterator<Map.Entry<Long, OrderMedia>> it = list.iterator(); it.hasNext();) {
				Map.Entry<Long, OrderMedia> entry = it.next();
				sortedMap.put(entry.getKey(), entry.getValue());
			}
		}
		return sortedMap;
	}

	public void successCbPicDelete(String data) {
		/*Toast.makeText(this, "Image Deleted Successfully", Toast.LENGTH_LONG)
				.show();*/
	}

	public void imageSaveSuccess(String data) {
		/*Toast.makeText(this, "Image Saved Successfully", Toast.LENGTH_LONG)
				.show();*/
	}

	public void signSaveSuccess(String data) {
		/*Toast.makeText(this, "Signature Saved Successfully", Toast.LENGTH_LONG)
				.show();*/
	}

	public void open() {

		if (PreferenceHandler.getOptionSelectedForImg(mActivity)!= null && PreferenceHandler.getOptionSelectedForImg(mActivity).equals("CAM")){
			openCam();
		}
		else if (PreferenceHandler.getOptionSelectedForImg(mActivity)!= null && PreferenceHandler.getOptionSelectedForImg(mActivity).equals("GAL")){
			openGal();
		}
		else
			pickImageOpts1(true);
		// AceRouteService.jsInterface.doAction(11);
		/*
		 * Intent intent = new
		 * Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		 * startActivityForResult(intent, 1);
		 */
	}

	/*@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.gc();
	}*/

	public void doActioncb(Uri data, String type) {

		if(type.equals("FL"))
		{
			String tistamp = String.valueOf(Utilities.getCurrentTime());
			String file_path = data.getPath();
			//file_path = Utilities.getRealPathFromURI(mActivity, data);
			Uri selectedUri = Uri.fromFile(new File(file_path));
			String fileExtension
					= MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
			Log.d(" FILES", fileExtension);
			SaveMediaRequest req = new SaveMediaRequest();
			req.setUrl("https://"+ PreferenceHandler.getPrefBaseUrl(getActivity())+"/mobi");
			req.setType("post");
			req.setId("0");
			req.setOrderId(String.valueOf(activeOrderObj.getId()));
			req.setTid("4");
			req.setFile("");
			req.setFrmkey("");
			req.setFrmfldkey("");

			if (data!= null)
				req.setDtl(replaceStr(new File(""+data).getName()));// YD using file location to show in description
			else
				req.setDtl("");
			req.setAction(OrderMedia.ACTION_MEDIA_SAVE);
			req.setTimestamp(tistamp);
			req.setMime(fileExtension);
			req.setMetapath(file_path);
			OrderMedia.saveData(req, mActivity, this, SAVEFILE);
		}
		else {
			OrderPicSaveActivity orderPicsSavFrag = new OrderPicSaveActivity();
			//gridView_MainFrag.setActiveOrderObject(activeOrderObj);
			orderPicsSavFrag.setActiveOrderObject(activeOrderObj);

			Bundle b = new Bundle();
			b.putString("jpegImage", data.toString());
			b.putString("type", type);
			b.putInt("fmetaType", 1);

			orderPicsSavFrag.setArguments(b);
			mActivity.pushFragments(Utilities.JOBS, orderPicsSavFrag, true, true, BaseTabActivity.UI_Thread);

		}
	}

private String replaceStr(String replaceStr) {

		if(replaceStr.contains("%20")) {
			replaceStr = replaceStr.replace("%20", " ");
		}

		if(replaceStr.contains("%2B")) {
			replaceStr = replaceStr.replace("%2B", "+");
		}
		return replaceStr;
	}

	public void pickImageOpts1(boolean isFromUI) {
// YD code to add checkbox for remember me functionality
		View checkBoxView = View.inflate(mActivity, R.layout.checkbox_img_gal, null);
		CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox_img_gal);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked == true){
					isCamGalOptSeletected = true;
				}
				else{
					isCamGalOptSeletected = false;
				}

			}
		});
		checkBox.setText(" Remember");
		checkBox.setTextSize(20+ (PreferenceHandler.getCurrrentFontSzForApp(getActivity()))); //set text size programmatically if needed
		checkBox.setTextColor(mActivity.getResources().getColor(R.color.lt_light_gray));
		TypeFaceFont.overrideFonts(getActivity(),checkBoxView);


//YD old code without checkbox
		Builder builder = new Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("ADD Picture"/*Utilities.getStringForAppAndr(mActivity,0)*/);
		//builder.setTitle(getResources().getString(
		//	R.string.ID_CHOOSE_IMAGE_OPTION));
		builder.setCancelable(true);
		builder.setView(checkBoxView);
		//	builder.setIcon(R.drawable.appicon);

		builder.setNegativeButton(getResources().getString(R.string.ID_CANCEL),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Logger.i(getApplicationContext(),
						// TAG,"cancels clicked");
						dialog.dismiss();
					}
				});

		String chooseCamera = "Take from Camera";//Utilities.getStringForAppAndr(mActivity,9);
		String chooseGallery = "Select from Gallery";//Utilities.getStringForAppAndr(mActivity,17);
		CharSequence[] options = {chooseCamera, chooseGallery};
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case 0:
						// Logger.i(getApplicationContext(),
						// TAG,"option camera selected");

						if (isCamGalOptSeletected){
							PreferenceHandler.setOptionSelectedForImg(mActivity,"CAM");
						}

						openCam();
						break;
					case 1:

						if (isCamGalOptSeletected){
							PreferenceHandler.setOptionSelectedForImg(mActivity,"GAL");
						}

						openGal();
						break;
				}
			}
		});
		final AlertDialog dialog = builder.create();
		Utilities.setAlertDialogRow(dialog, mActivity);
		dialog.show();
		TypeFaceFont.overrideFonts(getActivity(),dialog.getListView());
		Utilities.setDividerTitleColor(dialog, mheight, mActivity );
		Button neutral_button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		Utilities.setDefaultFont_12(neutral_button_negative);
		Button neutral_button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		Utilities.setDefaultFont_12(neutral_button_positive);
	}

	private void openGal() {
		// Logger.i(getApplicationContext(), TAG,
		// "option gallery selected");
		// take image from gallery
		Intent pickPhoto = new Intent(
				Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(pickPhoto,
				REQUEST_CODE_ACTIVITY_PICK_GALLERY);
	}

	private void openCam() {
		Uri uri;

		Intent cameraIntent = new Intent(
				MediaStore.ACTION_IMAGE_CAPTURE);

						/*
						 * File file = new File(Environment
						 * .getExternalStorageDirectory().getAbsolutePath() +
						 * "/AceRoute");
						 */

		String compfilename = getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
				.getAbsolutePath()
				+ "/AceRoute";
//		if (Environment.getExternalStorageState().equals(
//				Environment.MEDIA_MOUNTED)) {
//			compfilename = Environment
//					.getExternalStorageDirectory()
//					.getAbsolutePath()
//					+ "/AceRoute";
//		} else {
//			compfilename = Environment.getDataDirectory()
//					.getAbsolutePath() + "/AceRoute";
//		}

		File file = new File(compfilename);
		if(!file.exists()){
			file.mkdir();
		}

		long time = System.currentTimeMillis();

//		file.delete();
//
//		try {
//			file.mkdirs();
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}

		String basepath = file.getAbsolutePath();

		try {
			file = new File(basepath + "/temp.jpg");
			if (file.delete()) {
				file.createNewFile();
			} else {
				file = new File(basepath + "/temp_" + time + ".jpg");
				nmForThumb= basepath + "/temp_" + time +"_Thumb"+ ".jpg";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i( "check",
				"path:" + file.getAbsolutePath());
		uri = Uri.fromFile(file);
		Log.i( "check",
				"uri:" + uri.toString());
		Gridview_MainActivity.this.uri = uri;
		PreferenceHandler.setUriOfPic_sign(mActivity, uri.toString());
		//String urinew = PreferenceHandler.getUriOfPic_sign(mActivity);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		} else {
			Uri apkURI = FileProvider.getUriForFile(mActivity,mActivity.getApplicationContext()
							.getPackageName() + ".provider", file);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, apkURI);
		}

		cameraIntent
				.putExtra(
						MediaStore.EXTRA_SCREEN_ORIENTATION,
						getResources().getConfiguration().orientation);
		int height = Utilities
				.getDisplayHeigth(mActivity.getApplicationContext());
		int width = Utilities
				.getDisplayWidth(mActivity.getApplicationContext());
					/* cameraIntent.setType("image/*"); */
		// cameraIntent.putExtra("crop", "true");
		cameraIntent.putExtra("aspectX", width);
		cameraIntent.putExtra("aspectY", height);
		cameraIntent.putExtra("outputX", width);
		cameraIntent.putExtra("outputY", height);
		// cameraIntent.putExtra("scale", true);
		// cameraIntent.putExtra("outputFormat",
		// Bitmap.CompressFormat.JPEG.toString());

		startActivityForResult(cameraIntent,
				REQUEST_CODE_ACTIVITY_PICK_CAMERA);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode,
								 Intent intent) {
		Uri selectedImage;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case REQUEST_CODE_ACTIVITY_PICK_CAMERA:
					selectedImage = uri;
					if (selectedImage != null) {
						try {
							// calculating size of an image
							File f = new File(selectedImage.getPath());

;
				//Romil for making thumb images
							String file_path = selectedImage.getPath();
							Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file_path), 70, 100);
							file = new File(nmForThumb);//YD making a file on the path specified

							try {
								FileOutputStream out = new FileOutputStream(file); // YD making the file location writable
								ThumbImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
							} catch (Exception e) {
								e.printStackTrace();
							}
				//romil logic ends


							long sizeofImg = f.length();

							Log.i( Utilities.TAG,
									"Uri check : " + selectedImage);
							String imagepath = Utilities.getRealPathFromURI(
									mActivity.getApplicationContext(), selectedImage);
							Log.i( Utilities.TAG,
									"imagepath : " + imagepath);
							String imagename = imagepath.substring(imagepath
									.lastIndexOf(File.separator) + 1);
							Log.i( Utilities.TAG,
									"camera picked image name " + imagename);
							String functionString = "javascript:doActioncb("
									+ SplashII.REQUEST_CODE_SHOW_PICK_IMAGE_OPTIONS
									+ ",'" + JSONHandler.JSON_SUCCESS + ","
									+ JSONHandler.JSON_DATA + imagepath
									+ JSONHandler.JSON_DATA_END
									+ JSONHandler.JSON_END + "')";
							Log.i( Utilities.TAG,
									"Camera function string : " + functionString);
							// appView.loadUrl(functionString);
							// yash
							String returnData = SplashII.REQUEST_CODE_SHOW_PICK_IMAGE_OPTIONS
									+ ",'"
									+ JSONHandler.JSON_SUCCESS
									+ ","
									+ JSONHandler.JSON_DATA
									+ imagepath
									+ JSONHandler.JSON_DATA_END
									+ JSONHandler.JSON_END;
							doActioncb(uri, "CAM");

						} catch (NullPointerException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						Log.i( Utilities.TAG,
								"Error : uri null.");
					}
					break;
				case REQUEST_CODE_ACTIVITY_PICK_GALLERY:
					if (intent == null) {
						Toast.makeText(
								mActivity,
								getResources().getString(
										R.string.ID_NO_PHOTO_PICKED),
								Toast.LENGTH_SHORT).show();
						//finish(); YD later
						return;
					} else {
						selectedImage = intent.getData();
						createThumbImage(Utilities.getPath(selectedImage, mActivity));// YD make a thumb image
						String imagepath = Utilities.getRealPathFromURI(
								mActivity, selectedImage);
						Log.i( Utilities.TAG,
								"imagename : " + imagepath);
						String imagename = imagepath.substring(imagepath
								.lastIndexOf(File.separator) + 1);
						Log.i( Utilities.TAG,
								"gallery picked image name " + imagename);
						String functionString = "javascript:doActioncb("
								+ SplashII.REQUEST_CODE_SHOW_PICK_IMAGE_OPTIONS
								+ ",'" + JSONHandler.JSON_SUCCESS + ","
								+ JSONHandler.JSON_DATA + imagepath
								+ JSONHandler.JSON_DATA_END + JSONHandler.JSON_END
								+ "')";
						Log.i( Utilities.TAG,
								"Gallery function string : " + functionString);
						// appView.loadUrl(functionString);
						// yash

						String returnData = SplashII.REQUEST_CODE_SHOW_PICK_IMAGE_OPTIONS
								+ ",'"
								+ JSONHandler.JSON_SUCCESS
								+ ","
								+ JSONHandler.JSON_DATA
								+ imagepath
								+ JSONHandler.JSON_DATA_END + JSONHandler.JSON_END;
						doActioncb(selectedImage, "GAL");
					}
					break;
				case REQUEST_CODE_ACTIVITY_PICK_FILE:
					/*Uri uri = intent.getData();//file:///storage/emulated/0/Default/HZ15-35-01.pdf
					Log.e( Utilities.TAG,
							"Uri of file on gridview : " + uri);
					doActioncb(uri, "FL");*/

					if (resultCode == mActivity.RESULT_OK) {

						final Uri uri = intent.getData();

						// Get the File path from the Uri
						String path = FileUtils.getPath(mActivity, uri);

						// Alternatively, use FileUtils.getFile(Context, Uri)
						if (path != null && FileUtils.isLocal(path)) {
							File file = new File(path);
							Uri urii = Uri.fromFile(file);
							doActioncb(urii, "FL");
						}
					}

					break;
			}
		} else {
			Log.e( Utilities.TAG,
					"RESULT CODE ERROR for request code : " + requestCode);
			/*
			 * appView.loadUrl("javascript:doActioncb(" +
			 * AceRoute.REQUEST_CODE_SHOW_PICK_IMAGE_OPTIONS + "," +
			 * JSONHandler.JSON_FAILURE + JSONHandler.JSON_END + ")");
			 */
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	public static void createThumbImage(String mediapath) {
		String file_path = mediapath;
		String thumbPath = makeThumbPath(file_path);
		Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file_path), 70, 100);
		File file = new File(thumbPath);//YD making a file on the path specified

		try {
			FileOutputStream out = new FileOutputStream(file); // YD making the file location writable
			ThumbImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String makeThumbPath(String str) {

		if (str!=null && str.contains(".")){///storage/emulated/0/AceRoute/temp_1462527798949.jpg
			String array[];
			//array = str.split("\\.",-1);
			String basePath = str.substring(0, str.lastIndexOf("."));
			String ext = str.substring(str.lastIndexOf(".")+1);

			return basePath+"_Thumb."+ext;
		}
		return null;
	}

	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if (nl != null && nl.getLength() > 0) {
			Element el = (Element) nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

	@Override
	public void ServiceStarter(RespCBandServST activity, Intent intent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setResponseCallback(String response, Integer reqId) {

	}

	@Override
	public void setResponseCBActivity(final Response response) {
		Log.i( Utilities.TAG, "Response received for meta : " + response +"at"+Utilities.getCurrentTimeInMillis());
		if (response != null) {
			if (response.getStatus().equals("success") &&
					response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED))) {
				if (response.getId() == GETFILEMETA) {

					final Object respMap = response.getResponseMap();
					DataObject.orderPicsXmlStore = response.getResponseMap();
					mActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {

							setGrid(respMap);

						}
					});

				}
				if (response.getId() == DELETEMEDIA) {

					getFileMeta();
					activeOrderObj.setCustMetaCount(activeOrderObj.getCustMetaCount() - 1);
					if (fmetaType == 1)
						activeOrderObj.setImgCount(activeOrderObj.getImgCount() - 1);
					else if (fmetaType == 2)
						activeOrderObj.setSigCount(activeOrderObj.getSigCount() - 1);
					else if (fmetaType == 3)
						activeOrderObj.setAudioCount(activeOrderObj.getAudioCount() - 1);
					else if (fmetaType == 4)
						activeOrderObj.setDocCount(activeOrderObj.getDocCount() - 1);
					DBEngine.syncDataToSever(mActivity.getApplicationContext(), OrderMedia.TYPE_SAVE);
				}

				if (response.getId() == SAVEFILE) {

					getFileMeta();
					if (fmetaType == 4){
						activeOrderObj.setCustMetaCount(activeOrderObj.getCustMetaCount()+1);
						activeOrderObj.setDocCount(activeOrderObj.getDocCount() + 1);
					}
					DBEngine.syncDataToSever(mActivity.getApplicationContext(), OrderMedia.TYPE_SAVE);
				}

			} else if (response.getStatus().equals("success") &&
					response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA))) {
				if (response.getId() == GETFILEMETA) {
					 final Object respMap = response.getResponseMap();
					mActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setGrid(respMap);
						}
					});

				}
			}
		}
	}

	private void getAndUpdateNumberOfOrderParts(String orderId) {
		HashMap<Long, Order> orderMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
		Order odrObj = orderMap.get(Long.parseLong(orderId));
		if (fmetaType == 1)
			odrObj.setImgCount(odrObj.getImgCount() - 1);
		else if (fmetaType == 2)
			odrObj.setSigCount(odrObj.getSigCount() - 1);
		else if (fmetaType == 3)
			odrObj.setAudioCount(odrObj.getSigCount() - 1);
	}

	@Override
	public void headerClickListener(String callingId) {

		if (callingId.equals(mActivity.HeaderPlusPressed)) {
			rlt_editdeleted.setVisibility(View.GONE);
			if (fmetaType == 1)
				open();
			else if (fmetaType == 2)
				openSignPage();
			else if (fmetaType == 3){
				if ( Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
					requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
							10);
				}
				else {
					RecordAudioFragment recordFragment = new RecordAudioFragment();
					recordFragment.setActiveOrderObj(activeOrderObj);
					mActivity.pushFragments(Utilities.JOBS, recordFragment, true,
							true, BaseTabActivity.UI_Thread);
				}
			}
			else if(fmetaType == 4)
				showFileChooser();
		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		//super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case 10 : {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					RecordAudioFragment recordFragment = new RecordAudioFragment();
					recordFragment.setActiveOrderObj(activeOrderObj);
					mActivity.pushFragments(Utilities.JOBS, recordFragment, true,
							true, BaseTabActivity.UI_Thread);

				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					Toast.makeText(mActivity, "Permission denied", Toast.LENGTH_SHORT).show();
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}


	public void showFileChooser() {
		//Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		//intent.setType("*/*");
		//intent.addCategory(Intent.CATEGORY_OPENABLE);

		/*try {
			startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),
					REQUEST_CODE_ACTIVITY_PICK_FILE);
		} catch (android.content.ActivityNotFoundException ex) {
			ex.printStackTrace();
		}*/

		// YD Create the ACTION_GET_CONTENT Intent from file chooser library
		Intent getContentIntent = FileUtils.createGetContentIntent();

		Intent intent = Intent.createChooser(getContentIntent, "Select a File to Upload");
		startActivityForResult(intent, REQUEST_CODE_ACTIVITY_PICK_FILE);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	public void setActiveOrderObject(Order orderObj) {
		Log.e("Aceroute", "setActiveOrderObject called for Grid view main activity and current active obj is :" + orderObj);
		activeOrderObj = orderObj;
	}

	public void loadDataOnBack(BaseTabActivity mActivity) {
		mActivity.registerHeader(this);
		mActivity.setHeaderTitle("", headerText, "");
		getFileMeta();
		if (isSyncMedia) {
			DBEngine.syncDataToSever(mActivity.getApplicationContext(), OrderMedia.TYPE_SAVE);
			isSyncMedia = false;
		}
	}

	public void notifyDataChange(Response response, final BaseTabActivity ref) {
		final Object respMap = response.getResponseMap();
		ref.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mActivity = ref;
				setGrid(respMap);
			}
		});
	}

}