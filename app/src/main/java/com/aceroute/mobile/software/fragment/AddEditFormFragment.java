

package com.aceroute.mobile.software.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.SplashII;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.audio.RecordAudioFragment;
import com.aceroute.mobile.software.camera.Addsign;
import com.aceroute.mobile.software.camera.Gridview_MainActivity;
import com.aceroute.mobile.software.camera.OrderPicSaveActivity;
import com.aceroute.mobile.software.camera.ViewImage;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderMedia;
import com.aceroute.mobile.software.component.reference.DataObject;
import com.aceroute.mobile.software.component.reference.Form;
import com.aceroute.mobile.software.component.reference.Parts;
import com.aceroute.mobile.software.database.DBEngine;
import com.aceroute.mobile.software.dialog.CustomListner;
import com.aceroute.mobile.software.dialog.DatePickerInterface;
import com.aceroute.mobile.software.dialog.MyDatePickerDialog;
import com.aceroute.mobile.software.dialog.MyDialog;
import com.aceroute.mobile.software.dialog.MyDiologInterface;
import com.aceroute.mobile.software.dialog.MySearchDialog;
import com.aceroute.mobile.software.dialog.MySearchDiologInterface;
import com.aceroute.mobile.software.dialog.MySearchDiologInterfaceAlphaNum;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.DeleteMediaRequest;
import com.aceroute.mobile.software.requests.GetFileMetaRequest;
import com.aceroute.mobile.software.requests.SaveFormListRequest;
import com.aceroute.mobile.software.requests.SaveFormRequest;
import com.aceroute.mobile.software.requests.SaveMediaRequest;
import com.aceroute.mobile.software.utilities.JSONHandler;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.RadioGroupPlus;
import com.aceroute.mobile.software.utilities.ServiceError;
import com.aceroute.mobile.software.utilities.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.aceroute.mobile.software.camera.Gridview_MainActivity.getRoundedMinute;


public class AddEditFormFragment extends BaseFragment implements HeaderInterface, RespCBandServST, DatePickerInterface {

    public Map<Long, String> uridata = new HashMap<>();
    public Map<Long, String> iddata = new HashMap<>();
    MyDialog dialog = null;
    String tidforsign;
    boolean visibity = true;
    public String imgkeyss[];
    public int imagekey = 0;
    public ImageView imageCam;
    LinearLayout linearLayout;
    EditText editText;
    private int mheight = 500;
    final int BARCODER_RESULT = 101;
    EditText barcodeEdtView;
    private int DELETEMEDIA = 11;
    ArrayList<View> allviews = new ArrayList<>(); //YD this could be as below
    //private HashMap<Long, HashMap<View, String>> allviews;  where long is view id from json, View will be the View itself and string will be value we store for each view.
    //TextView textViewForDate, textViewFortime, spinnerText;
    MySearchDialog searchDialg;
    long elemtSelectedInSearchDlog = -1;
    ArrayList<String> mCategoryArryList;
    ArrayList<Long> mCategoryArryListId;
    private JSONObject finaljson;
    private long fid;
    private int SAVE_FORM = 3;//YD randomly given the value
    private String currentOdrId;
    private String formMode, workerid;
    long formId;
    int topMarginForAllView = 40;
    int bottomMarginForAllView = 30;
    private int SAVEFILE = 3;
    ImageView imageView1, imageView2;
    long tmpid = Utilities.getCurrentTimeInMillis();
    public String frmkeyValue;
    public ArrayList<String> imageviewid = new ArrayList<>();

    private static final int REQUEST_CODE_ACTIVITY_PICK_CAMERA = 22;
    String nmForThumb;
    public Uri uri = null;
    File file;
    int imageAdd = 0;
    int picturetid = 0;
    private int GETFILEMETA = 1;
    String formName, editformkey, formnNameTemp;
    int ids = 100;
    public ArrayList<String> imguris = new ArrayList<>();
    public ArrayList<String> imagecounter = new ArrayList<>();
    public ArrayList<Integer> jsoncounter = new ArrayList<>();

    //HashMap<CheckBox, TableRow> checkboxToReset = new HashMap<>();
    static String actualImgAnnSav;
    static int imgAnnotationsav;
    static String actualImgSav;
    static int ab = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.dynamic_form_layout, null);
        TypeFaceFont.overrideFonts(mActivity, vi);
        mActivity.registerHeader(this);

        try {
            initiViewReference(vi);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return vi;

    }

    @Override
    public void onResume() {
        super.onResume();
        /*int i=0;
        CheckBox [] checkBoxes = checkboxToReset.keySet().toArray(new CheckBox[checkboxToReset.size()]);

        for(TableRow tableRow : checkboxToReset.values()){\\\\\\\\\\\\\\\\
            checkBoxes[i].setWidth(tableRow.getWidth());
            i++;
        }*/

//
//        if (imgAnnotationsav ==1){
//            imgAnnotationsav=0;
//            actualImgSav = actualImgAnnSav;
//
//            File imgFile = new File(actualImgAnnSav);
//            if(imgFile.exists()){
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inSampleSize=2;
//                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
//                Log.i("yash", "show bitmap image");
//                imageView1.setScaleType(ImageView.ScaleType.CENTER);
//                imageView1.setImageBitmap(myBitmap);
//            }
//        }
//        else {
//
//            if (actualImgSav != null) {
//                File imgFile = new File(actualImgSav);
//                if (imgFile.exists()) {
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inSampleSize = 2;
//                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
//                    Log.i("yash", "show bitmap image");
//                    imageView1.setScaleType(ImageView.ScaleType.CENTER);
//                    imageView1.setImageBitmap(myBitmap);
//                }
//            }
//
//        }
    }


    @SuppressLint("ResourceType")
    public void loadDataOnBack(BaseTabActivity mActivity) {
        mActivity.registerHeader(this);
        BaseTabActivity.setHeaderTitle("", "ADD " + PreferenceHandler.getPictureHead(getActivity()), "");

        if (imgAnnotationsav == 1) {
            imgAnnotationsav = 0;
            actualImgSav = actualImgAnnSav;

            File imgFile = new File(actualImgAnnSav);
            if (imgFile.exists()) {

                BaseTabActivity.setHeaderTitle("", "ADD " + PreferenceHandler.getSignatureHead(mActivity), "");
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
                Log.i("yash", "show bitmap image");
                if (linearLayout != null && abs != 0) {
                    imageView1 = linearLayout.findViewById(abs);
                    imageView1.setPadding(5, 10, 5, 10);
                    // imageView1.setPadding(5,5,5,5);
                    imageView1.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    //imageView1.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    ImageView camImg = linearLayout.findViewWithTag(abs + "cam");

                    if (camImg != null) {
                        camImg.setAlpha(0.5f);
                        camImg.setEnabled(false);
                    }
//                deleteIdList.remove(abs);

                    imageView1.setImageBitmap(myBitmap);

                }
            }

        } else {
            BaseTabActivity.setHeaderTitle("", "ADD " + PreferenceHandler.getSignatureHead(mActivity), "");
            try {
                File imgFile = new File(actualImgSav);
                if (imgFile.exists()) {
                    BaseTabActivity.setHeaderTitle("", "ADD " + PreferenceHandler.getSignatureHead(mActivity), "");
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
                    Log.i("yash", "show bitmap image");
                    imageView1 = linearLayout.findViewById(abs);
                    imageView1.setPadding(5, 10, 5, 10);
                    imageView1.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imageView1.setImageBitmap(myBitmap);

                }
            } catch (Exception e) {

            }

        }
    }

    private void initiViewReference(View v) throws JSONException {

        //checkboxToReset.clear();
        linearLayout = v.findViewById(R.id.linear_layout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        Bundle bundle = this.getArguments();
        String formData = bundle.getString("formData");
        fid = bundle.getLong("fid");
        currentOdrId = bundle.getString("OrderId");
        workerid = bundle.getString("workerid");
        formMode = bundle.getString("FormType");
        if (this.formMode.equals("EDIT FORM")) {
            formId = bundle.getLong("FormId");
            editformkey = bundle.getString("formkey");
            Log.d("editformkey", "" + editformkey);
        }

        formnNameTemp = bundle.getString("FRM_NM");
        formName = formMode == "ADD FORM" ? "ADD " + bundle.getString("FRM_NM") : "EDIT " + bundle.getString("FRM_NM");
        mActivity.setHeaderTitle("", formName, "");

        //long tmpid = Utilities.getCurrentTimeInMillis();
        if (editformkey == null) {
            frmkeyValue = formnNameTemp + workerid + tmpid;
        } else {
            frmkeyValue = editformkey;
        }

        Log.d("formnNameTemp", "" + workerid);

        JSONObject obj = new JSONObject(formData);
        JSONArray m_jArry = obj.getJSONArray("frm");
        for (int i = 0; i < m_jArry.length(); i++) {
            JSONObject jo_inside = m_jArry.getJSONObject(i);

            //JSONObject elementJson = new JSONObject();

            int fieldTypeId = Integer.valueOf(jo_inside.getString("tid"));

            switch (fieldTypeId) {
                case 1:
                    createEditText(jo_inside, false, i);
                    break;

                case 2:
                    createEditTextArea(jo_inside, i);

                    break;

                case 3:
                    createEditText(jo_inside, true, i);
                    break;

                case 4:
                    createTextDateTime(jo_inside, true, i);
                    break;

                case 5:
                    createTextDateTime(jo_inside, false, i);
                    break;

                case 6:
                    createDropDown(jo_inside, false, i);
                    break;

                case 7:
                    createDropDown(jo_inside, true, i);
                    break;

                case 8:
                    createRadioButtons(jo_inside, false, i);
                    break;

                case 9:
                    createCheckBoxes(jo_inside, true, i);
                    break;

                case 10:
                    createHeader(jo_inside, true, i);
                    break;

                case 11:
                    CreateGeoButton(jo_inside, true, i);
                    break;

                case 12:
                    CreatePartType(jo_inside, true, i);
                    break;
                case 13: {
                    picturetid = 13;
                    createImageWithHeader(jo_inside, true, i);
                    break;
                }
                case 14:
                    picturetid = 14;
                    HashMap<Long, Order> orderList = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
                    Order odrObj = orderList.get(Long.valueOf(currentOdrId));
                    RecordAudioFragment recordFragment = new RecordAudioFragment();
                    recordFragment.setActiveOrderObj(odrObj);
                    mActivity.pushFragments(Utilities.JOBS, recordFragment, true,
                            true, BaseTabActivity.UI_Thread);

                    //r

                    //     SubmittedFormtoServer(finaljson);
                    break;
                case 15:
                    picturetid = 13;
                    createBarcodeText(jo_inside, true, i);
                    break;


                case 16: {
                    picturetid = 13;
                    createSignatureWithHeader(jo_inside, true, i);
                    break;
                }
            }
        }


        if (formMode.equals("EDIT FORM")) {
            getFileMeta();
        }
    }


    public ArrayList<ImageView> listimagevei = new ArrayList<>();
    public int abs = 0;

    private void createImageWithHeader(JSONObject jsonObject, boolean b, int i) throws JSONException {
        LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null)
            textView.setText(jsonObject.getString("lbl"));
        paramsTop.setMargins(15, topMarginForAllView, 15, 0);
        textView.setLayoutParams(paramsTop);
        textView.setTextSize(20);
        linearLayout.addView(textView);
        allviews.add(textView);

        ids++;
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400); // Width , height
        imageView1 = new ImageView(mActivity);
        lparams.setMargins(15, 5, 15, 0);
        imageView1.setBackground(getResources().getDrawable(R.drawable.roundable_edittext));
        imageView1.setId(jsonObject.getInt("id"));
        listimagevei.add(imageView1);
        Log.d("TAG878", String.valueOf(ids));
        imageView1.setTag(jsonObject);
        imageView1.setLayoutParams(lparams);
        allviews.add(imageView1);
        linearLayout.addView(imageView1);

        LinearLayout.LayoutParams lpcamera = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT); // Width , height
        final ImageView cameraImage = new ImageView(mActivity);
        lpcamera.setMargins(15, 5, 15, bottomMarginForAllView);
        lpcamera.gravity = Gravity.CENTER;
        cameraImage.setBackground(getResources().getDrawable(R.drawable.tab_snapit_selector));
        cameraImage.setId(jsonObject.getInt("id"));
        cameraImage.setTag(jsonObject.getInt("id") + "cam");
        cameraImage.setLayoutParams(lpcamera);
        allviews.add(cameraImage);
        linearLayout.addView(cameraImage);

        if (formMode.equals("EDIT FORM")) {
            cameraImage.setAlpha(0.5f);
            cameraImage.setEnabled(false);
        } else {
            cameraImage.setAlpha(1f);
            cameraImage.setEnabled(true);
        }


        cameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abs = v.getId();
                openCam(abs);
            }
        });
    }

    private void createSignatureWithHeader(JSONObject jsonObject, boolean b, int i) throws JSONException {
        LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null)
            textView.setText(jsonObject.getString("lbl"));
        paramsTop.setMargins(15, topMarginForAllView, 15, 0);
        textView.setLayoutParams(paramsTop);
        textView.setTextSize(20);
        linearLayout.addView(textView);
        allviews.add(textView);

        ids++;
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400); // Width , heig
        imageView1 = new ImageView(mActivity);
        lparams.setMargins(15, 5, 15, 0);
        imageView1.setBackground(getResources().getDrawable(R.drawable.dotted_border));
        imageView1.setId(jsonObject.getInt("id"));

        imageView1.setPadding(5, 10, 5, 10);
        listimagevei.add(imageView1);
        Log.d("TAG878", String.valueOf(ids));
        imageView1.setTag(jsonObject);
        imageView1.setLayoutParams(lparams);
        allviews.add(imageView1);
        linearLayout.addView(imageView1);

        LinearLayout.LayoutParams lpcamera = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT); // Width , height
        final ImageView cameraImage = new ImageView(mActivity);
        lpcamera.setMargins(15, 5, 15, bottomMarginForAllView);
        lpcamera.gravity = Gravity.CENTER;
        cameraImage.setBackground(getResources().getDrawable(R.drawable.tab_signit_selector));
        cameraImage.setId(jsonObject.getInt("id"));
        cameraImage.setTag(jsonObject.getInt("id") + "cam");
        cameraImage.setLayoutParams(lpcamera);
        allviews.add(cameraImage);
        linearLayout.addView(cameraImage);

        if (formMode.equals("EDIT FORM")) {
            cameraImage.setAlpha(0.5f);
            cameraImage.setEnabled(false);
        } else {
            cameraImage.setAlpha(1f);

            cameraImage.setEnabled(true);
        }

        cameraImage.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                visibity = false;
                abs = v.getId();

                HashMap<Long, Order> orderList = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
                Order odrObj = orderList.get(Long.valueOf(currentOdrId));
                Addsign addSignFrag = new Addsign();
                Bundle args = new Bundle();
                args.putString("YourKey", frmkeyValue);
                args.putString("fileId", String.valueOf(abs));


                addSignFrag.setArguments(args);
                //gridView_MainFrag.setActiveOrderObject(activeOrderObj);
                addSignFrag.setActiveOrderObject(odrObj);
                mActivity.pushFragments(Utilities.JOBS, addSignFrag, true, true, BaseTabActivity.UI_Thread);

            }

        });
    }

    public static void edtdSignedImg1(File imgFile, String path, int setimageAnn) {
        imgAnnotationsav = setimageAnn;
        if (imgAnnotationsav == 1) {
            actualImgAnnSav = path;
            createThumbImage(path);
        }
    }

    public void displayimage() {

        imageView1.setImageResource(Integer.parseInt(actualImgAnnSav));

    }


    private void openCam(int id) {
        Uri uri;

        Intent cameraIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);

        /*
         * File file = new File(Environment
         * .getExternalStorageDirectory().getAbsolutePath() +
         * "/AceRoute");
         */

        String compfilename = null;
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            compfilename = Environment
//                    .getExternalStorageDirectory()
//                    .getAbsolutePath()
//                    + "/AceRoute";
//        } else {
//            compfilename = Environment.getDataDirectory()
//                    .getAbsolutePath() + "/AceRoute";
//        }
        compfilename = getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath()
                + "/AceRoute";

        File file = new File(compfilename);

        long time = System.currentTimeMillis();

        file.delete();

        try {
            file.mkdirs();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        String basepath = file.getAbsolutePath();

        try {
            file = new File(basepath + "/temp.jpg");
            if (file.delete()) {
                file.createNewFile();
            } else {
                file = new File(basepath + "/temp_" + time + ".jpg");
                nmForThumb = basepath + "/temp_" + time + "_Thumb" + ".jpg";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("check",
                "path:" + file.getAbsolutePath());
        uri = Uri.fromFile(file);
        Log.i("check",
                "uri:" + uri.toString());
        AddEditFormFragment.this.uri = uri;
        PreferenceHandler.setUriOfPic_sign(mActivity, uri.toString());
        //String urinew = PreferenceHandler.getUriOfPic_sign(mActivity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            Uri apkURI = FileProvider.getUriForFile(mActivity, mActivity.getApplicationContext()
                    .getPackageName() + ".provider", file);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, apkURI);
        }

        cameraIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, getResources().getConfiguration().orientation);
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

        startActivityForResult(cameraIntent, id);
    }


    private void createEditText(JSONObject jsonObject, boolean isValidNumber, int position) throws JSONException {
        LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null)
            textView.setText(jsonObject.getString("lbl"));
        paramsTop.setMargins(15, topMarginForAllView, 15, 0);
        textView.setLayoutParams(paramsTop);
        textView.setTextSize(20);
        linearLayout.addView(textView);

        LinearLayout.LayoutParams paramsBottom = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        paramsBottom.gravity = Gravity.TOP;
        EditText editText = new EditText(mActivity);
        editText.setTag(jsonObject);
        editText.setSingleLine(true);
        editText.setPadding(10, 20, 20, 20);
        // editText.getText().insert(0, "  ");
        //editText.getText().toString().replace(" ", "");

        if (isValidNumber)
            editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        paramsBottom.setMargins(15, 5, 15, bottomMarginForAllView);

        editText.setLayoutParams(paramsBottom);
        editText.setBackground(getResources().getDrawable(R.drawable.roundable_edittext));

        if (formMode.equals("EDIT FORM")) {
            //editText.getText().insert(0, " ");
            editText.setText(jsonObject.getString("val"));
            // editText.getText().toString().replace(" ", "");

        }
        allviews.add(editText);
        linearLayout.addView(editText);
    }


    private void createBarcodeText(JSONObject jsonObject, boolean isValidNumber, int position) throws JSONException {

//        LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
//        TextView textView = new TextView(mActivity);
//        if (jsonObject.getString("lbl") != null)
//            textView.setText(jsonObject.getString("lbl"));
//        paramsTop.setMargins(15, topMarginForAllView, 15, 0);
//        textView.setLayoutParams(paramsTop);
//        textView.setTextSize(20);
//        linearLayout.addView(textView);
//
//        LinearLayout.LayoutParams paramsBottom = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
//        paramsBottom.gravity = Gravity.TOP;
//        EditText editText = new EditText(mActivity);
//        final ImageView broadcastcameraText = new ImageView(mActivity);
//        paramsBottom.setMargins(15, 5, 15, bottomMarginForAllView);
//        //lpcamera.gravity = Gravity.CENTER;
//        broadcastcameraText.setBackground(getResources().getDrawable(R.drawable.tab_snapit_selector));
//       // cameraImage.setId(jsonObject.getInt("id"));
//        //cameraImage.setTag(jsonObject.getInt("id")+"cam");
//        LinearLayout textLinear = new LinearLayout(mActivity);
//        textLinear.setOrientation(LinearLayout.HORIZONTAL);
//        editText.setLayoutParams(paramsBottom);
//        broadcastcameraText.setLayoutParams(paramsBottom);
//       // textLinear.setLayoutParams(paramsBottom);
//        textLinear.addView(editText);
//        textLinear.addView(broadcastcameraText);
//        editText.setTag(jsonObject);
//        editText.setSingleLine(true);
//        if(isValidNumber)
//            editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
//        paramsBottom.setMargins(15, 5, 15, topMarginForAllView);
//        editText.setLayoutParams(paramsBottom);
//        editText.setBackground(getResources().getDrawable(R.drawable.roundable_edittext));
//
//        if(formMode.equals("EDIT FORM")){
//            editText.setText(jsonObject.getString("val"));
//        }
//        allviews.add(editText);
//        allviews.add(broadcastcameraText);
//        linearLayout.addView(textLinear);
//        broadcastcameraText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               //startActivity(new Intent(mActivity, ScannedBarcodeActivity.class));
//
//            }
//        });
//    startActivityForResult(new Intent(mActivity,SimpleScannerActivity.class),BARCODER_RESULT);


        final LayoutInflater inflater = LayoutInflater.from(mActivity); // or (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewMyLayout = inflater.inflate(R.layout.layout_barcode_dynamic, null);
        TextView textView = viewMyLayout.findViewById(R.id.tv_barcode);
        final EditText edtView = viewMyLayout.findViewById(R.id.edt_barcode);
        //  edtView.getText().insert(0, "  ");
        // if(isValidNumber)
        //    edtView.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

        edtView.setTag(jsonObject);

        if (formMode.equals("EDIT FORM")) {
            //edtView.getText().insert(0, "  ");
            edtView.setText(jsonObject.getString("val"));
        }
        ImageView broadcastcameraText = viewMyLayout.findViewById(R.id.imv_barcode);
        if (jsonObject.getString("lbl") != null)
            textView.setText(jsonObject.getString("lbl"));

        broadcastcameraText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeEdtView = edtView;
                Intent intent = new Intent(mActivity, SimpleScannerActivity.class);
                startActivityForResult(intent, BARCODER_RESULT);
            }
        });

        allviews.add(edtView);
        allviews.add(broadcastcameraText);
        linearLayout.addView(viewMyLayout);
    }

    private void createEditTextArea(final JSONObject jsonObject, int position) throws JSONException {
        LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        paramsTop.gravity = Gravity.TOP;
        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null)
            textView.setText(jsonObject.getString("lbl"));
        paramsTop.setMargins(15, topMarginForAllView, 15, 0);
        textView.setLayoutParams(paramsTop);
        textView.setTextSize(20);
        linearLayout.addView(textView);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 230); // Width , height
        final EditText editText = new EditText(mActivity);

//        jsonObject.put("val", editText.getText().toString());
        editText.setTag(jsonObject);
        editText.setPadding(10, 20, 20, 20);

        //  editText.getText().insert(0, "  ");
        //  editText.getText().toString().replace(" ", "");
        if (formMode.equals("EDIT FORM")) {
            //editText.getText().insert(0, " ");
            editText.setText(jsonObject.getString("val"));
            //  editText.getText().toString().replace(" ", "");
        }
        lparams.setMargins(15, 5, 15, bottomMarginForAllView);
        editText.setBackground(getResources().getDrawable(R.drawable.roundable_edittext));
        editText.setGravity(Gravity.TOP);
        editText.setLayoutParams(lparams);
        allviews.add(editText);
        linearLayout.addView(editText);
    }

    private void createTextDateTime(final JSONObject jsonObject, boolean isDate, int position) throws JSONException {

        if (!isDate) {
            LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            String stm = "08:00 am";
            DateFormat inputFormat = new SimpleDateFormat("hh:mm a");
            Date stime = null;
            try {
                stime = inputFormat.parse(stm);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            TextView textView = new TextView(mActivity);
            if (jsonObject.getString("lbl") != null)
                textView.setText(jsonObject.getString("lbl"));
            paramsTop.setMargins(15, topMarginForAllView, 15, 0);
            textView.setLayoutParams(paramsTop);
            textView.setTextSize(20);
            linearLayout.addView(textView);

            LinearLayout.LayoutParams paramsBottom = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            final TextView textViewFortime = new TextView(mActivity);
//            jsonObject.put("val", textViewFortime.getText().toString());
//            mainjson.put("data", jsonObject);
            textViewFortime.setTag(R.string.parent_value, jsonObject);//YD doing this only for AddEditFormFragment
            paramsBottom.setMargins(10, 5, 10, bottomMarginForAllView);
            textViewFortime.setHeight((int) convertPixelsToDp(340.0f, getActivity()));
            textViewFortime.setPadding(10, 10, 10, 10);
            textViewFortime.setTextSize(22.0f);
            textViewFortime.setBackground(getResources().getDrawable(R.drawable.rounded_green_fill_bg));
            textViewFortime.setLayoutParams(paramsBottom);
            textViewFortime.setTextColor(getResources().getColor(R.color.white));
            if (formMode.equals("EDIT FORM"))
                textViewFortime.setText(getDateOrTimeToShow(jsonObject.getString("val"), false));
            allviews.add(textViewFortime);
            linearLayout.addView(textViewFortime);
            textViewFortime.setTag(stime);
            textViewFortime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utilities.getTimeRange(getActivity(), Utilities.TIME_TYPE_START, ((TextView) v), textViewFortime, new CustomListner() {

                        @Override
                        public void onClick(View view) {
                            try {
                                JSONObject jsonObj = (JSONObject) view.getTag(R.string.parent_value);
                                Date CurrentValue = (Date) view.getTag();
                                String dateToSave = Utilities.convertDateToUtc(CurrentValue.getTime());

                                jsonObj.put("val", dateToSave);// 1969/12/31 20:30 -00:00
                            } catch (JSONException j) {
                                j.printStackTrace();
                            }
                        }
                    });
                }
            });
        } else {
            LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            TextView textView = new TextView(mActivity);
            if (jsonObject.getString("lbl") != null)
                textView.setText(jsonObject.getString("lbl"));
            paramsTop.setMargins(15, topMarginForAllView, 15, 0);
            textView.setLayoutParams(paramsTop);
            textView.setTextSize(20);
            linearLayout.addView(textView);
            TextView textViewForDate = new TextView(mActivity);

            LinearLayout.LayoutParams paramsBottom = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            paramsBottom.setMargins(10, 5, 10, bottomMarginForAllView);
            textViewForDate.setHeight((int) convertPixelsToDp(340.0f, getActivity()));
            textViewForDate.setPadding(10, 10, 10, 10);
            textViewForDate.setTextSize(22.0f);
            textViewForDate.setBackground(getResources().getDrawable(R.drawable.rounded_green_fill_bg));
            textViewForDate.setLayoutParams(paramsBottom);
            textViewForDate.setTextColor(getResources().getColor(R.color.white));
            textViewForDate.setTag(jsonObject);
            if (formMode.equals("EDIT FORM"))
                textViewForDate.setText(getDateOrTimeToShow(jsonObject.getString("val"), true));
            allviews.add(textViewForDate);
            linearLayout.addView(textViewForDate);
            textViewForDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard();
                    getCalender((TextView) v);
                    /* try {
                     *//*jsonObject.put("val", textViewForDate.getText().toString());
                        mainjson.put("data", jsonObject);
                        textViewForDate.setTag(mainjson);*//*
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }
            });
        }
    }

    private String getDateOrTimeToShow(String newOrderStartDate, boolean isDate) {//2020/07/15 6:15 -00:00 OR 2020/07/15 18:30 -00:00

        if (newOrderStartDate != null) {
            // handle date if status is 8 yash TODO
            SimpleDateFormat convStrToDate = new SimpleDateFormat("yyyy/MM/dd HH:mm Z");
            //SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf_date = new SimpleDateFormat("MMM dd yyyy");
            SimpleDateFormat sdf_time = new SimpleDateFormat("hh:mm a");
            Date date;
            try {
                date = convStrToDate.parse(newOrderStartDate);
                if (date != null && isDate)
                    return sdf_date.format(date);
                else if (date != null)
                    return sdf_time.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private void getCalender(TextView view) {
        try {

            Calendar cal = Calendar.getInstance();
            int mDay;
            int mYear;
            if (!view.getText().toString().trim().equals("") && !view.getText().toString().trim().equals(UnavailableFragment.sdate_str) && !view.getText().toString().trim().equals(UnavailableFragment.edate_str)) {
                Date date = new SimpleDateFormat("MMM").parse(view.getText().toString().split(" ")[0]);
                cal.setTime(date);
                mDay = Integer.valueOf(view.getText().toString().split(" ")[1]);
                mYear = Integer.valueOf(view.getText().toString().split(" ")[2]);
            } else {
                mDay = cal.get(Calendar.DAY_OF_MONTH);
                mYear = cal.get(Calendar.YEAR);
            }
            int mMonth = Integer.valueOf(cal.get(Calendar.MONTH));


            int sizeDialogStyleID = Utilities.getDialogTextSize(mActivity);

            MyDatePickerDialog dialog = new MyDatePickerDialog(mActivity, new mDateSetListener(view), mYear, mMonth, mDay, true, AddEditFormFragment.this, sizeDialogStyleID);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", dialog);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", dialog);

            if (Build.VERSION.SDK_INT >= 11) {
                dialog.getDatePicker().setCalendarViewShown(false);
            }
            dialog.setTitle("Set Date");
            dialog.show();
            Utilities.setDividerTitleColor(dialog, 0, mActivity);

            Button button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            Utilities.setDefaultFont_12(button_negative);
            Button button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Utilities.setDefaultFont_12(button_positive);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createDropDown(final JSONObject jsonObject, final boolean isCheckbox, final int position) throws JSONException {
        LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null)
            textView.setText(jsonObject.getString("lbl"));
        paramsTop.setMargins(10, topMarginForAllView, 10, 0);
        textView.setLayoutParams(paramsTop);
        // textView.setAllCaps(true);
        textView.setTextSize(20);
        linearLayout.addView(textView);
        final TextView textViewOpt = new TextView(mActivity);
        //spinnerText.setTag(position);
        //jsonObject.put("val", jsonObject.getString("ddnval"));//        mainjson.put("position", position);

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        lparams.setMargins(10, 5, 10, bottomMarginForAllView);
        textViewOpt.setHeight((int) convertPixelsToDp(340.0f, getActivity()));
        textViewOpt.setPadding(10, 1, 10, 15);
        textViewOpt.setTextSize(22.0f);

        HashMap<String, String> selectedStrAndIdMap;
        String idSelectedForRadio = null;
        if (formMode.equals("EDIT FORM")) {
            selectedStrAndIdMap = getDropDownVals(jsonObject, jsonObject.getString("val"));
            if (selectedStrAndIdMap.size() > 0) {
                String[] keySet = selectedStrAndIdMap.keySet().toArray(new String[selectedStrAndIdMap.size()]);
                idSelectedForRadio = keySet[0];
                String stringToShow = selectedStrAndIdMap.get(keySet[keySet.length - 1]);
                textViewOpt.setText(stringToShow);
            }
        }

        textViewOpt.setBackground(getResources().getDrawable(R.drawable.rounded_green_fill_bg));
        textViewOpt.setLayoutParams(lparams);
        textViewOpt.setTextColor(getResources().getColor(R.color.white));
        textViewOpt.setTag(jsonObject);
        textViewOpt.setId(Integer.valueOf(jsonObject.getString("id")));
        allviews.add(textViewOpt);
        linearLayout.addView(textViewOpt);

        final int pkid = !jsonObject.getString("pkid").equals("") ? Integer.valueOf(jsonObject.getString("pkid")) : -1;

        final String finalIdSelectedForRadio = idSelectedForRadio;
        textViewOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> arrayList = new ArrayList<String>();
                final ArrayList<String> idslist = new ArrayList<String>();//YD ddnva
                final TextView tempTextview = textViewOpt;
                try {
                    if (jsonObject.getString("ddn") != null) {
                        String str[] = jsonObject.getString("ddn").split(",");
                        String ids[] = jsonObject.getString("ddnval").split(",");
                        String fkids[] = !jsonObject.getString("ddnfk").equals("") ? jsonObject.getString("ddnfk").split(",") : null;

                        String[] parentSelectedVal = tempTextview.getTag(R.string.parent_value) != null ? tempTextview.getTag(R.string.parent_value).toString().split("\\,") : null;

                        if (parentSelectedVal == null && formMode.equals("EDIT FORM") && pkid != -1) {
                            parentSelectedVal = getParentSelectedValues(pkid);
                        }
                        for (int k = 0; k < str.length; k++) {

                            if (parentSelectedVal != null && parentSelectedVal.length > 0) {//will get the selected value of parent

                                for (int i = 0; i < parentSelectedVal.length; i++) {
                                    if (fkids[k].equals(parentSelectedVal[i])) {
                                        idslist.add(ids[k]);//YD ddnva
                                        arrayList.add(str[k]);
                                    }
                                }
                            } else {
                                idslist.add(ids[k]);//YD ddnva
                                arrayList.add(str[k]);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideSoftKeyboard();
                if (isCheckbox)
                    openCheckboxDialog(idslist, arrayList, jsonObject, tempTextview);
                else
                    showRadioDialog(idslist, arrayList, jsonObject, tempTextview, finalIdSelectedForRadio);
            }
        });
    }

    private String[] getParentSelectedValues(int pkid) {
        try {
            for (View vii : allviews) {
                if (vii.getId() == pkid) {
                    TextView textView = ((TextView) vii);
                    JSONObject tempJsonObject = (JSONObject) textView.getTag();
                    return !tempJsonObject.getString("val").equals("") ? tempJsonObject.getString("val").split("\\,") : null;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    private HashMap<String, String> getDropDownVals(JSONObject jsonObject, String val) {
        try {
            String str[] = jsonObject.getString("ddn").split(",");
            String ids[] = jsonObject.getString("ddnval").split(",");
            String[] selectedVal = val != null && !val.equals("") ? val.split("\\,") : null;

            String idSelected = null;
            String completeStrToDisplay = null;
            HashMap<String, String> stringListAndSelectedIdsMap = new HashMap<String, String>();
            int strLen = 0;

            for (int k = 0; k < ids.length; k++) {

                if (selectedVal != null && selectedVal.length > 0) {

                    for (int i = 0; i < selectedVal.length; i++) {

                        if (ids[k].equals(selectedVal[i])) {
                            if (strLen == 0) {
                                idSelected = ids[k];
                                completeStrToDisplay = str[k];

                                stringListAndSelectedIdsMap.put(idSelected, completeStrToDisplay);
                            } else {
                                idSelected = ids[k];
                                completeStrToDisplay = completeStrToDisplay + ", " + str[k];
                                stringListAndSelectedIdsMap.put(idSelected, completeStrToDisplay);
                            }

                            strLen++;
                        }
                    }
                } else break;
            }
            return stringListAndSelectedIdsMap;
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return null;
    }

    private void showRadioDialog(ArrayList<String> idslist, ArrayList<String> nmsLst, final JSONObject jsonObject, final TextView textview, String elemtSelectedInSearchDlog) {
        searchDialg = new MySearchDialog(mActivity, "Category", "", idslist, nmsLst, elemtSelectedInSearchDlog, true);

        searchDialg.setkeyListenderAlphaNum(new MySearchDiologInterfaceAlphaNum() {
            @Override
            public void onButtonClick() {
                super.onButtonClick();
                searchDialg.cancel();
            }

            @Override
            public void onItemSelected(String idSelected, String nameSelected) {
                super.onItemSelected(idSelected, nameSelected);

                String data = idSelected.toString();
                try {
                    JSONObject mainJson = new JSONObject();
                    textview.setText(nameSelected);
                    jsonObject.put("val", data);
                    //mainJson.put("fld", jsonObject);
                    textview.setTag(jsonObject);
                    searchDialg.cancel();
                    //elemtSelectedInSearchDlog = idSelected;

                    checkAndChangeValOfEffectedDropDwn(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        searchDialg.setCanceledOnTouchOutside(true);
        searchDialg.onCreate1(null);
        searchDialg.show();

        int mheight = 500;
        Utilities.setDividerTitleColor(searchDialg, mheight, mActivity);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(searchDialg.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        searchDialg.getWindow().setAttributes(lp);

    }

    private void checkAndChangeValOfEffectedDropDwn(final JSONObject jsonObject) {

        try {
            String childIdOfCurrentDropDwn;
            int i = 0;
            childIdOfCurrentDropDwn = jsonObject.get("ckid").toString();
            boolean viewNotAvailable;//YD this flag will test if the child view is available or not

            do {
                if (!childIdOfCurrentDropDwn.equals("")) {
                    viewNotAvailable = true;
                    int dropDownIdEffected = Integer.valueOf(childIdOfCurrentDropDwn);

                    for (View vii : allviews) {
                        if (vii.getId() == dropDownIdEffected) {
                            viewNotAvailable = false;
                            TextView textView = ((TextView) vii);
                            textView.setText("");
                            JSONObject tempJsonObject = (JSONObject) textView.getTag();
                            childIdOfCurrentDropDwn = tempJsonObject.get("ckid").toString();
                            //childIdOfCurrentDropDwn = ((JSONObject)tempJsonObject.get("fld")).get("ckid").toString();

                            if (i == 0)
                                vii.setTag(R.string.parent_value, jsonObject.get("val").toString());
                            i++;
                            break;
                        }
                    }
                    if (viewNotAvailable)
                        return;
                }
            } while (!childIdOfCurrentDropDwn.equals(""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void openCheckboxDialog(final ArrayList<String> idslist, final ArrayList<String> nmsLst, final JSONObject jsonObject, final TextView textview) {//YD ddnva
        try {
            final LinkedHashMap<String, String> seletedItems = new LinkedHashMap<String, String>();


            //ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_multiple_choice,textview.getId(),nmsLst){};
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_multiple_choice, nmsLst) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    TextView textView = (TextView) view.findViewById(android.R.id.text1);
                    textView.setTextSize(22 + PreferenceHandler.getCurrrentFontSzForApp(mActivity));
                    textView.setTextColor(getResources().getColor(R.color.light_gray));

                    return view;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT);
            if (PreferenceHandler.getWorkerHead(mActivity) != null && !PreferenceHandler.getWorkerHead(mActivity).equals("")) {
                builder.setTitle(jsonObject.get("lbl").toString());
            } else
                builder.setTitle("Select Workers");
            LayoutInflater inflater = mActivity.getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);
            builder.setView(dialogView);

            ListView listView = (ListView) dialogView.findViewById(R.id.list_dialog);

            listView.setAdapter(arrayAdapter);
            String valuesSelected = jsonObject.get("val").toString();
            String[] strValue = !valuesSelected.equals("") ? valuesSelected.split("\\,", -1) : null;

            for (int i = 0; strValue != null && i < strValue.length; i++) {
                int indexSelect = idslist.indexOf(strValue[i]);
                if (indexSelect >= 0) {
                    listView.setItemChecked(Integer.valueOf(indexSelect), true);
                    seletedItems.put(strValue[i], nmsLst.get(indexSelect));
                }
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
                    CheckedTextView item = (CheckedTextView) view;
                    if (item.isChecked()) {
                        if (seletedItems.get(idslist.get(position)) == null)
                            seletedItems.put(idslist.get(position), nmsLst.get(position));
                    } else {
                        //if(seletedItems.contains(idslist.get(position)))
                        seletedItems.remove(idslist.get(position));
                    }
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                }
            }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // TODO Auto-generated method stub
                    if (seletedItems.size() > 0) {
                        String selectedOptions = "";
                        String selectedNames = "";
                        int i = 0;

                        for (String key : seletedItems.keySet()) {
                            if (i == 0) {
                                selectedOptions = key;
                                selectedNames = String.valueOf(seletedItems.get(key));
                            } else {
                                selectedOptions += "," + key;
                                selectedNames = selectedNames + ", " + String.valueOf(seletedItems.get(key));
                            }
                            i++;
                        }
                        Log.i("Total Workers: ", String.valueOf(seletedItems.size()));

                        //textview.setTag(R.string.parent_value, seletedItems );
                        //odr_detail_worker_edit.setText(workerNames);


                        try {
                            JSONObject mainJson = new JSONObject();
                            textview.setText(selectedNames);
                            jsonObject.put("val", selectedOptions);
                            //mainJson.put("fld", jsonObject);
                            textview.setTag(jsonObject);
                            //searchDialg.cancel();
                            //elemtSelectedInSearchDlog = idSelected;

                            checkAndChangeValOfEffectedDropDwn(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

            Utilities.setDividerTitleColor(dialog, mheight, mActivity);
            Button neutral_button_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            Utilities.setDefaultFont_12(neutral_button_negative);
            Button neutral_button_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Utilities.setDefaultFont_12(neutral_button_positive);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createRadioButtons(JSONObject jsonObject, boolean b, int position) throws JSONException {
        LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null)
            textView.setText(jsonObject.getString("lbl"));
        paramsTop.setMargins(15, topMarginForAllView, 15, 0);
        textView.setLayoutParams(paramsTop);
        textView.setTextSize(20);
        linearLayout.addView(textView);

//Radio start
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(0, 0, 0, bottomMarginForAllView);//40 was the old value for bottom

        RadioGroupPlus.LayoutParams radioGroupLayParams = new RadioGroupPlus.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams lparamsTbRow = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparamsTbRow.setMargins(0, 0, 0, 10);

        TableRow.LayoutParams tblRowLayParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tblRowLayParams.setMargins(0, 20, 0, 0);
        tblRowLayParams.weight = 1;

        LinearLayout allTableRowViews = new LinearLayout(mActivity);
        allTableRowViews.setOrientation(LinearLayout.VERTICAL);
        allTableRowViews.setLayoutParams(radioGroupLayParams);

        String[] optionList = jsonObject.getString("ddn").split(",");
        String[] idsList = jsonObject.getString("ddnval").split(",");
        String selectedId = jsonObject.getString("val");

        TableRow tableRow = null;
        int selectedIdForRadioPlus = -1;

        for (int i = 0; i < optionList.length; i++) {

            if (i % 2 == 0) {
                tableRow = new TableRow(mActivity);
                tableRow.setLayoutParams(lparamsTbRow);
                allTableRowViews.addView(tableRow);
            }
            RadioButton radioButton = new RadioButton(mActivity);
            radioButton.setText(optionList[i]);
            radioButton.setId(Integer.valueOf(idsList[i]));
            radioButton.setGravity(Gravity.TOP);
            if (formMode.equals("EDIT FORM") && Integer.valueOf(selectedId).equals(Integer.valueOf(idsList[i]))) {
                selectedIdForRadioPlus = Integer.valueOf(idsList[i]);
                radioButton.setChecked(true);
            } else
                radioButton.setChecked(false);
            radioButton.setTextColor(Color.parseColor("#000000"));
            radioButton.setTextSize(20);
            radioButton.setLayoutParams(tblRowLayParams);
            tableRow.addView(radioButton);
        }

        RadioGroupPlus radioGroupPlus = null;
        if (formMode.equals("EDIT FORM"))
            radioGroupPlus = new RadioGroupPlus(mActivity, selectedIdForRadioPlus);
        else
            radioGroupPlus = new RadioGroupPlus(mActivity, -1);

        radioGroupPlus.setLayoutParams(lparams);

        radioGroupPlus.setTag(jsonObject);
        radioGroupPlus.addView(allTableRowViews);

        allviews.add(radioGroupPlus);
        linearLayout.addView(radioGroupPlus);

        radioGroupPlus.setOnCheckedChangeListener(new RadioGroupPlus.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroupPlus group, int checkedId) {
                try {
                    JSONObject jsonObj = (JSONObject) group.getTag();
                    jsonObj.put("val", checkedId);

                    Toast.makeText(mActivity, "button clicked ", Toast.LENGTH_LONG);
                } catch (Exception e) {

                }
            }
        });

    }


    private void createCheckBoxes(JSONObject jsonObject, boolean b, int position) throws JSONException {
        LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null)
            textView.setText(jsonObject.getString("lbl"));
        paramsTop.setMargins(15, topMarginForAllView, 15, 0);
        textView.setLayoutParams(paramsTop);
        textView.setTextSize(20);
        linearLayout.addView(textView);

// Checkbox start
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(0, 5, 0, bottomMarginForAllView);//40 was the old value for bottom

        LinearLayout.LayoutParams tableRowLlparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TableRow.LayoutParams tblRowLayParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tblRowLayParams.setMargins(0, 20, 0, 0);
        tblRowLayParams.weight = 1;

        int controlID = Integer.valueOf(jsonObject.getString("id"));

        LinearLayout allTableRowViews = new LinearLayout(mActivity);
        allTableRowViews.setOrientation(LinearLayout.VERTICAL);
        allTableRowViews.setLayoutParams(lparams);
        allTableRowViews.setId(controlID);
        allTableRowViews.setTag(jsonObject);

        String[] optionList = jsonObject.getString("ddn").split(",");
        String[] idsList = jsonObject.getString("ddnval").split(",");
        String[] selectedIds = jsonObject.getString("val").split(",");

        TableRow tableRow = null;

        for (int i = 0; i < optionList.length; i++) {

            if (i % 2 == 0) {
                tableRow = new TableRow(mActivity);
                tableRow.setLayoutParams(tableRowLlparams);
                allTableRowViews.addView(tableRow);
            }

            CheckBox checkBox = new CheckBox(mActivity);
            checkBox.setText(optionList[i]);
            checkBox.setGravity(Gravity.TOP);
            checkBox.setId(Integer.valueOf(idsList[i]));
            if (formMode.equals("EDIT FORM")) {
                for (int j = 0; j < selectedIds.length; j++) {
                    if (Long.valueOf(selectedIds[j]).equals(Long.valueOf(idsList[i]))) {
                        checkBox.setChecked(true);
                    }
                }
            } else
                checkBox.setChecked(false);
            checkBox.setTextColor(Color.parseColor("#000000"));
            checkBox.setTextSize(20);

            //YD logic to set width if there is only one element in the last row.
            if (i == optionList.length - 1 && i % 2 == 0) {
                //checkboxToReset.put(checkBox, tableRow);

                TableRow.LayoutParams tblRowLayParamsLstCheckBox = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tblRowLayParamsLstCheckBox.setMargins(0, 20, 0, 0);
                checkBox.setLayoutParams(tblRowLayParamsLstCheckBox);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                //int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;
                checkBox.setWidth(width / 2);
            } else
                checkBox.setLayoutParams(tblRowLayParams);

            tableRow.addView(checkBox);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int layoutID = ((LinearLayout) buttonView.getParent().getParent()).getId();
                    try {
                        for (int i = 0; i < allviews.size(); i++) {
                            if (allviews.get(i).getId() == layoutID) {
                                JSONObject jsonObj = (JSONObject) ((LinearLayout) allviews.get(i)).getTag();
                                String[] finalValue = !jsonObj.get("val").toString().equals("") ? jsonObj.get("val").toString().split("\\,") : null;


                                if (finalValue != null) {
                                    ArrayList<String> finalValueList = new ArrayList<String>(Arrays.asList(finalValue));
                                    boolean valueRemoved = false;
                                    for (int j = 0; j < finalValueList.size(); j++) {
                                        if (Integer.valueOf(finalValueList.get(j)).equals(buttonView.getId())) {
                                            if (isChecked) {
                                            } else {
                                                finalValueList.remove(j);
                                                valueRemoved = true;
                                            }
                                        }
                                    }
                                    if (!valueRemoved) {
                                        //Value was not present in the finalValueList
                                        if (isChecked) {
                                            finalValueList.add(String.valueOf(buttonView.getId()));
                                        }
                                    }

                                    String finalValToSav = "";
                                    if (finalValueList.size() == 0)//this will be called when last value get remove form the list.
                                        jsonObj.put("val", finalValToSav);

                                    for (int k = 0; k < finalValueList.size(); k++) {
                                        finalValToSav = k == 0 ? finalValueList.get(k) : finalValToSav + "," + finalValueList.get(k);
                                    }
                                    jsonObj.put("val", finalValToSav);

                                } else {
                                    if (isChecked)
                                        jsonObj.put("val", buttonView.getId());
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(mActivity, String.valueOf(buttonView.getId()), Toast.LENGTH_LONG);
                }
            });
        }

        allviews.add(allTableRowViews);
        linearLayout.addView(allTableRowViews);

    }


    private void createHeader(JSONObject jsonObject, boolean b, int position) throws JSONException {
        LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null)
            textView.setText(jsonObject.getString("lbl"));
        paramsTop.setMargins(15, topMarginForAllView, 15, bottomMarginForAllView);
        textView.setLayoutParams(paramsTop);
        textView.setTextSize(20);
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        linearLayout.addView(textView);
        allviews.add(textView);
    }

    private void CreateGeoButton(JSONObject jsonObject, boolean b, int i) throws JSONException {

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null) {
            textView.setText(jsonObject.getString("lbl"));
        }
        lparams.setMargins(15, topMarginForAllView, 15, bottomMarginForAllView);
        textView.setHeight((int) convertPixelsToDp(340.0f, getActivity()));
        textView.setLayoutParams(lparams);
        textView.setTextSize(20);
        if (formMode.equals("EDIT FORM") && !jsonObject.getString("val").equals("")) {
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
        }
        textView.setPadding(10, 20, 30, 10);
        textView.setBackground(getResources().getDrawable(R.drawable.rounded_green_fill_bg));
        //textView.setCheckMarkDrawable(R.drawable.check);
        //textView.drawableHotspotChanged(-30, -50);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setTag(jsonObject);
        linearLayout.addView(textView);
        allviews.add(textView);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
                String currentGeoLocation = Utilities.getLocation(mActivity);
                try {
                    if (currentGeoLocation != null && !currentGeoLocation.equals("")) {
                        JSONObject jsonObj = (JSONObject) (((TextView) v).getTag());
                        jsonObj.put("val", currentGeoLocation);
                    } else {
                        JSONObject jsonObj = (JSONObject) (((TextView) v).getTag());
                        jsonObj.put("val", "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void CreatePartType(JSONObject jsonObject, boolean b, int position) throws JSONException {
        LinearLayout.LayoutParams paramsTop = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        HashMap<Long, Parts> unsortedPartList = (HashMap<Long, Parts>) DataObject.partTypeXmlDataStore;
        TextView textView = new TextView(mActivity);
        if (jsonObject.getString("lbl") != null) {
            textView.setText(jsonObject.getString("lbl"));
        }
        paramsTop.setMargins(10, topMarginForAllView, 10, 0);
        textView.setLayoutParams(paramsTop);
        //textView.setAllCaps(true);
        textView.setTextSize(20);
        linearLayout.addView(textView);

//DropDown
        final TextView textViewOpt = new TextView(mActivity);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        lparams.setMargins(10, 5, 10, bottomMarginForAllView);
        textViewOpt.setHeight((int) convertPixelsToDp(340.0f, getActivity()));
        textViewOpt.setPadding(10, 1, 10, 15);
        textViewOpt.setTextSize(22.0f);
        textViewOpt.setText("");//YD TODO
        textViewOpt.setBackground(getResources().getDrawable(R.drawable.rounded_green_fill_bg));
        textViewOpt.setLayoutParams(lparams);
        if (formMode.equals("EDIT FORM") && !jsonObject.getString("val").equals("")) {
            textViewOpt.setText(unsortedPartList.get(Long.valueOf(jsonObject.getString("val"))).getName());
        }
        textViewOpt.setTextColor(getResources().getColor(R.color.white));
        textViewOpt.setTag(jsonObject);
        textViewOpt.setId(Integer.valueOf(jsonObject.getString("id")));
        allviews.add(textViewOpt);
        linearLayout.addView(textViewOpt);

        HashMap<Long, Parts> partTypeList = sortPartCatagoryLst(unsortedPartList);

        Long[] keys = partTypeList.keySet().toArray(new Long[partTypeList.size()]);

        mCategoryArryList = new ArrayList<String>();
        mCategoryArryListId = new ArrayList<Long>();

        for (int i = 0; i < partTypeList.size(); i++) {
            Parts parttypeObj = partTypeList.get(keys[i]);
            mCategoryArryList.add(parttypeObj.getName());
            mCategoryArryListId.add(parttypeObj.getId());
        }


        textViewOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog(mCategoryArryList, mCategoryArryListId, textViewOpt);
            }
        });
    }

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
        for (Iterator<Map.Entry<Long, Parts>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<Long, Parts> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    private void showCategoryDialog(ArrayList<String> mCategoryArryList, ArrayList<Long> mCategoryArryListId, final TextView textViewOpt) {
        searchDialg = new MySearchDialog(mActivity, "Category", "", mCategoryArryListId, mCategoryArryList, elemtSelectedInSearchDlog);

        searchDialg.setkeyListender(new MySearchDiologInterface() {
            @Override
            public void onButtonClick() {
                super.onButtonClick();
                searchDialg.cancel();
            }

            @Override
            public void onItemSelected(Long idSelected, String nameSelected) {
                super.onItemSelected(idSelected, nameSelected);

                try {
                    ((TextView) textViewOpt).setText(nameSelected);
                    JSONObject jsonObj = (JSONObject) (((TextView) textViewOpt).getTag());
                    jsonObj.put("val", idSelected);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

               /* edtMaterialCategory.setText(nameSelected);
                Parts parttypeObj = partTypeList.get(idSelected);
                edtMaterialCategory.setTag(String.valueOf(parttypeObj.getId()));
                edtMaterialDesc.setText(parttypeObj.getDesc());
                elemtSelectedInSearchDlog = idSelected;*/
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

    public void hideSoftKeyboard() {
        if (mActivity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void headerClickListener(String callingId) {


        //frmkeyValue = formnNameTemp+tmpid;
        if (editformkey == null) {
            frmkeyValue = formnNameTemp + workerid + tmpid;
        } else {
            frmkeyValue = editformkey;
        }
        Log.d("formkey", "" + frmkeyValue);

        if (callingId.equals(BaseTabActivity.HeaderDonePressed)) {
            try {
                if (DataClicked()) {


                    if (deleteIdList.size() == 0) {
                        //deletedformImage();
                        SubmittedFormtoServer(finaljson);
                    } else {
                        deletedformImage();
                    }
                    Log.d("Final Json :", finaljson + "");
                } else {
                    deletedformImage();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void SubmittedFormtoServer(JSONObject finaljson) {

        PreferenceHandler.setPrefFormListCount(getContext(), 2);
        SaveFormListRequest req = new SaveFormListRequest();
        req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(getActivity()) + "/mobi");
        req.setType("post");
        req.setAction(Form.ACTION_SAVE_FORM);
        ArrayList<SaveFormRequest> list = getFormlist();


        if (list.size() > 0) {
            req.setDataObj(list);
            Form.saveData(req, mActivity, AddEditFormFragment.this, SAVE_FORM);

        } else {
            Utilities.noPatterns(getActivity());
        }

    }


    private ArrayList<SaveFormRequest> getFormlist() {
        ArrayList<SaveFormRequest> arrayList = new ArrayList<>();
        // long tmpid = Utilities.getCurrentTimeInMillis();
        if (editformkey == null) {
            frmkeyValue = formnNameTemp + workerid + tmpid;
        } else {
            frmkeyValue = editformkey;
        }
        PreferenceHandler.setFormKeyValue(mActivity, frmkeyValue);

        SaveFormRequest saveFormRequest = new SaveFormRequest();
        saveFormRequest.setFdata(finaljson + "");
        saveFormRequest.setStmp(tmpid + "");
        saveFormRequest.setFtid(fid + "");
        if (picturetid == 13) {
            saveFormRequest.setFrmkey(frmkeyValue);

        } else {
            saveFormRequest.setFrmkey("0");
        }


        if (formMode.equals("EDIT FORM"))
            saveFormRequest.setId(formId + "");

            //saveFormRequest.setFrmkey(frmkeyValue);
        else
            saveFormRequest.setId("0");
        saveFormRequest.setOid(currentOdrId);
        arrayList.add(saveFormRequest);
        Log.d("formarray", "" + arrayList);
        return arrayList;

    }

    public String newimage;
    public String newfrmfldid;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Uri selectedImage;

        if (resultCode == mActivity.RESULT_OK) {

            if (requestCode == BARCODER_RESULT) {
                String barcode = data.getStringExtra("barcode_data");

                if (barcode != null && barcodeEdtView != null) {
                    barcodeEdtView.setText(barcode);
                    barcodeEdtView = null;

                }

            } else {
                // switch (requestCode) {
                //   case REQUEST_CODE_ACTIVITY_PICK_CAMERA:
                selectedImage = uri;
                if (selectedImage != null) {
                    try {
                        // calculating size of an image
                        File f = new File(selectedImage.getPath());
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

                        Log.i(Utilities.TAG,
                                "Uri check : " + selectedImage);
                        String imagepath = Utilities.getRealPathFromURI(
                                mActivity.getApplicationContext(), selectedImage);
                        Log.i(Utilities.TAG,
                                "imagepath : " + imagepath);
                        String imagename = imagepath.substring(imagepath
                                .lastIndexOf(File.separator) + 1);
                        Log.i(Utilities.TAG,
                                "camera picked image name " + imagename);
                        String functionString = "javascript:doActioncb("
                                + SplashII.REQUEST_CODE_SHOW_PICK_IMAGE_OPTIONS
                                + ",'" + JSONHandler.JSON_SUCCESS + ","
                                + JSONHandler.JSON_DATA + imagepath
                                + JSONHandler.JSON_DATA_END
                                + JSONHandler.JSON_END + "')";
                        Log.i(Utilities.TAG,
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

                        Log.d("IMAGEST", returnData);

                        imageCam = linearLayout.findViewById(requestCode);
                        imageCam.setImageURI(uri);
                        uridata.put((long) requestCode, uri.getPath());

//                    if(this.formMode.equals("EDIT FORM")) {
//                        String temp = fileIdMappng.get(requestCode);
//                        if(temp != null){
//                            iddata.put((long) requestCode,temp);
//                        }
//                        else {
//                            iddata.put((long) requestCode, "1");
//                        }
//                    }
//                    else {
                        iddata.put((long) requestCode, "1");
                        //   }
                        newimage = uri.getPath();
                        newfrmfldid = String.valueOf(requestCode);
                        ImageView camImg = linearLayout.findViewWithTag(requestCode + "cam");
                        if (camImg != null) {
                            camImg.setAlpha(0.5f);
                            camImg.setEnabled(false);
                        }
                        deleteIdList.remove(requestCode);

                        //         addAndRemove(requestCode);
                        imageCam.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final Dialog dialog = new Dialog(mActivity);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setCancelable(true);
                                dialog.setContentView(R.layout.customeimage);

                                ImageView img = (ImageView) dialog.findViewById(R.id.imgview);
                                img.setImageURI(uri);

                                dialog.show();
                            }
                        });

                        imageAdd = 1;


                        //     doActioncb1(uri, "CAM");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i(Utilities.TAG,
                            "Error : uri null.");
                }
            }
            //       break;

        }
         /* }else {
            Log.e( Utilities.TAG,
                    "RESULT CODE ERROR for request code : " + requestCode);

        }*/
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void addAndRemove(int requestCode) {
        Boolean imgsts = false;
        if (imageviewid != null) {
            for (int a = 0; a < imageviewid.size(); a++) {
                if (imageviewid.get(a).equalsIgnoreCase(String.valueOf(requestCode))) {
                    imgsts = true;
                    imguris.set(a, uri.getPath());
                    return;
                } else {
                    imgsts = false;
                }
            }

            if (!imgsts) {
                imguris.add(uri.getPath());
                imageviewid.add(String.valueOf(requestCode));
            }

        } else {
            imguris.add(uri.getPath());
            imageviewid.add(String.valueOf(requestCode));
        }


    }

    public void doActioncb1(Uri uri, String cam) {
        HashMap<Long, Order> orderList = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
        Order odrObj = orderList.get(Long.valueOf(currentOdrId));

        OrderPicSaveActivity orderPicsSavFrag = new OrderPicSaveActivity();
        //gridView_MainFrag.setActiveOrderObject(activeOrderObj);
        orderPicsSavFrag.setActiveOrderObject(odrObj);

        Bundle b = new Bundle();
        b.putString("jpegImage", uri.toString());
        b.putString("type", cam);
        b.putInt("fmetaType", 1);
        b.putString("isForm", "1");

        orderPicsSavFrag.setArguments(b);
        mActivity.pushFragments(Utilities.JOBS, orderPicsSavFrag, true, true, BaseTabActivity.UI_Thread);

    }

    private String replaceStr(String replaceStr) {

        if (replaceStr.contains("%20")) {
            replaceStr = replaceStr.replace("%20", " ");
        }

        if (replaceStr.contains("%2B")) {
            replaceStr = replaceStr.replace("%2B", "+");
        }
        return replaceStr;
    }

    private int SAVEPICTURE = 15;
    public String imskey[];


    public void doActioncb(Map<Long, String> data, ArrayList<String> imagsts, String type) {

        Set<Long> keySet = data.keySet();
        ArrayList<Long> lisofkeys = new ArrayList<Long>(keySet);
        Collection<String> values = data.values();
        ArrayList<String> listofvalues = new ArrayList<>(values);


        if (formMode.equalsIgnoreCase("EDIT FORM")) {

            Collection<String> idds = iddata.values();
            ArrayList<String> listofids = new ArrayList<>(idds);

            for (int i = 0; i < data.size(); i++) {
                String tistamp = String.valueOf(Utilities.getCurrentTime());
                SaveMediaRequest req = new SaveMediaRequest();
                req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(getActivity()) + "/mobi");
                req.setType("post");
                if (listofids.get(i).equalsIgnoreCase("1")) {
                    req.setId("0");
                } else {
                    req.setId(listofids.get(i));
                }
                //req.setOrderGeo("23.344,77.465");
                req.setOrderId(String.valueOf(currentOdrId));
                req.setTid("1");//4
                req.setFile("");
                req.setDtl("");//""
                req.setAction(OrderMedia.ACTION_MEDIA_SAVE);
                req.setTimestamp(tistamp);
                req.setMime("jpg");//""
                req.setMetapath(listofvalues.get(i));
                req.setCopy("no");
                req.setFrmkey(frmkeyValue);
                req.setFrmfldkey(String.valueOf(lisofkeys.get(i)));
                OrderMedia.saveData(req, mActivity, this, SAVEPICTURE);
            }
        } else {
            for (int i = 0; i < data.size(); i++) {
                String tistamp = String.valueOf(Utilities.getCurrentTime());
                SaveMediaRequest req = new SaveMediaRequest();
                req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(getActivity()) + "/mobi");
                req.setType("post");
                req.setId("0");
                //req.setOrderGeo("23.344,77.465");
                req.setOrderId(String.valueOf(currentOdrId));
                req.setTid("1");//4
                req.setFile("");
                req.setDtl("");//""
                req.setAction(OrderMedia.ACTION_MEDIA_SAVE);
                req.setTimestamp(tistamp);
                req.setMime("jpg");//""
                req.setMetapath(listofvalues.get(i));
                req.setCopy("no");
                req.setFrmkey(frmkeyValue);
                req.setFrmfldkey(String.valueOf(lisofkeys.get(i)));

                OrderMedia.saveData(req, mActivity, this, SAVEPICTURE);
            }
        }
    }


    public boolean DataClicked() throws JSONException {


        JSONArray mainJson = new JSONArray();
        finaljson = new JSONObject();
        JSONObject jsonObject;
        for (int i = 0; i < allviews.size(); i++) {
            View view = allviews.get(i);
            if (view instanceof EditText) {
                jsonObject = (JSONObject) view.getTag();
                if (jsonObject != null) {
                    String lbl = jsonObject.getString("lbl");
                    String min = jsonObject.getString("min");
                    String max = jsonObject.getString("max");
                    String req = jsonObject.getString("req");
                    String tid = jsonObject.getString("tid");

                    String editdata = ((EditText) view).getText().toString();
                    if (editdata.isEmpty()) {
                        if (Integer.valueOf(req) == 1) {
                            showErrorMessage(true, lbl, "", "", false);
                            return false;
                        } else
                            jsonObject.put("val", "");

                    } else {

                        int numberOfTextCountOrNumber = -1;
                        boolean isForNumber;
                        if (Integer.valueOf(tid) == 3) {
                            numberOfTextCountOrNumber = Integer.valueOf(editdata);
                            isForNumber = true;
                        } else {
                            numberOfTextCountOrNumber = editdata.length();
                            isForNumber = false;
                        }
                        /*if(Integer.valueOf(tid) == 3){
                            int numberToSav = Integer.valueOf(editdata);
                            if(numberToSav < Integer.valueOf(min)){
                                showErrorMessage(false, lbl, min, max);
                                return false;
                            }else if (numberToSav > Integer.valueOf(max)) {
                                showErrorMessage(false, lbl, min, max);
                                return false;
                            } else
                                jsonObject.put("val", editdata);
                        }
                        else {*/
                        if (numberOfTextCountOrNumber < Integer.valueOf(min)) {
                            showErrorMessage(false, lbl, min, max, isForNumber);
                            return false;
                        } else if (numberOfTextCountOrNumber > Integer.valueOf(max)) {
                            showErrorMessage(false, lbl, min, max, isForNumber);
                            return false;
                        } else
                            jsonObject.put("val", editdata);
                        // }
                    }
                    mainJson.put(jsonObject);
                }
            } else if (view instanceof TextView) {
                if (view.getTag() instanceof JSONObject)
                    jsonObject = (JSONObject) view.getTag();
                else
                    jsonObject = (JSONObject) view.getTag(R.string.parent_value);

                if (jsonObject != null) {
                    String lbl = jsonObject.getString("lbl");
                    String req = jsonObject.getString("req");
                    String tid = jsonObject.getString("tid");
                    String val = jsonObject.getString("val");

                    if (Integer.valueOf(tid) == 11 && val.equals("") && Integer.valueOf(req) == 1) {
                        showErrorStringMessage(lbl, "Select Geo For this Form");
                        return false;
                    } else if ((((TextView) view).getText() == null || ((TextView) view).getText().equals("")) && Integer.valueOf(req) == 1) {
                        showErrorMessage(true, lbl, "", "", false);
                        Log.e("Action Required", "This Feild is Empty " + jsonObject.getString("lbl"));
                        return false;
                    } else {
                        mainJson.put(jsonObject);
                    }
                }
            } else if (view instanceof ImageView) {

                if (view.getTag() instanceof JSONObject)
                    jsonObject = (JSONObject) view.getTag();
                else
                    jsonObject = (JSONObject) view.getTag(R.string.parent_value);

                if (jsonObject != null) {
                    String lbl = jsonObject.getString("lbl");
                    String req = jsonObject.getString("req");
                    String tid = jsonObject.getString("tid");
                    String min = jsonObject.getString("min");
                    String max = jsonObject.getString("max");
                    String nm = jsonObject.getString("nm");
                    tidforsign = jsonObject.getString("tid");


//                    if (req.equalsIgnoreCase("1") && listimagevei.get(0).getDrawable() == null) {
//
//
//                       // if (tid.equals("13")) {
//                            showErrorMessage(true, lbl, PreferenceHandler.getPictureHead(mActivity), "", false);
//                            return false;
//                        //}
//                      /*  else
//                        {
//                            showErrorMessage(true, lbl, PreferenceHandler.getSignatureHead(mActivity), "", false);
//                            return false;
//
//                        }*/
//
//
//                    }
                    boolean flag = true;
                    for (ImageView tempView : listimagevei) {

                        if (tempView == view) {
                            if (tempView.getDrawable() == null) {
                                flag = false;
                                break;
                            }
                        }
                    }

                    if (req.equalsIgnoreCase("1") && !flag) {


                        if (tid.equals("13")) {
                            showErrorMessage(true, lbl, PreferenceHandler.getPictureHead(mActivity), "", false);
                        } else {
                            showErrorMessage(true, lbl, PreferenceHandler.getSignatureHead(mActivity), "", false);
                        }


                        return false;
                    }
//                    switch (nm) {
//                        case "picture1": {
//                            if (req.equalsIgnoreCase("1") && listimagevei.get(0).getDrawable() == null) {
//                            showErrorMessage(true, lbl, PreferenceHandler.getPictureHead(mActivity), "", false);
//                            return false;
//                        }
//                        break;
//                        }
//                        case "picture2": {
//                            if (req.equalsIgnoreCase("1") && listimagevei.get(1).getDrawable() == null) {
//                                showErrorMessage(true, lbl, PreferenceHandler.getPictureHead(mActivity), "", false);
//                                return false;
//                            }
//
//                            break;
//                        }
//                        case "picture3": {
//                            if (req.equalsIgnoreCase("1") && listimagevei.get(2).getDrawable() == null) {
//                                showErrorMessage(true, lbl, PreferenceHandler.getPictureHead(mActivity), "", false);
//                                return false;
//                            }
//                            break;
//
//
//                        }
//
//
//
//                        case "sign1": {
//                            boolean flag = true;
//                            for(ImageView tempView : listimagevei) {
//
//                                if(tempView == view){
//                                    if(tempView.getDrawable() == null){
//                                        flag = false;
//                                        break;
//                                    }
//                                }
//                            }
//                            if (req.equalsIgnoreCase("1") &&  !flag) {
//                                showErrorMessage(true, lbl, PreferenceHandler.getPictureHead(mActivity), "", false);
//                                return false;
//                            }
//                            break;
//
//
//                        }
//
//                    }

                    mainJson.put(jsonObject);


                }

            } else if (view instanceof RadioGroupPlus || view instanceof LinearLayout) {
                jsonObject = view.getTag() != null && view.getTag() != null ? (JSONObject) view.getTag() : null;
                if (jsonObject != null) {
                    String lbl = jsonObject.getString("lbl");
                    String req = jsonObject.getString("req");

                    String selectedValue = jsonObject.get("val").toString();
                    if (selectedValue != null && !selectedValue.equals("")) {
                        mainJson.put(jsonObject);
                    } else if (Integer.valueOf(req) == 1) {
                        showErrorMessage(true, lbl, "", "", false);
                        return false;
                    }
                }
            }
        }
        finaljson.put("frm", mainJson);
        return true;
    }


    public boolean BackDataDataClicked() throws JSONException {


        JSONArray mainJson = new JSONArray();
        finaljson = new JSONObject();
        JSONObject jsonObject;
        for (int i = 0; i < allviews.size(); i++) {
            View view = allviews.get(i);
            if (view instanceof EditText) {
                jsonObject = (JSONObject) view.getTag();
                if (jsonObject != null) {
                    String lbl = jsonObject.getString("lbl");
                    String min = jsonObject.getString("min");
                    String max = jsonObject.getString("max");
                    String req = jsonObject.getString("req");
                    String tid = jsonObject.getString("tid");

                    String editdata = ((EditText) view).getText().toString();
                    if (!editdata.isEmpty()) {

                        //  showErrorMessage(true, lbl, "","", false);
                        return false;
                    }
                }
            } else if (view instanceof RadioGroupPlus || view instanceof LinearLayout) {
                jsonObject = view.getTag() != null && view.getTag() != null ? (JSONObject) view.getTag() : null;
                if (jsonObject != null) {
                    String lbl = jsonObject.getString("lbl");
                    String req = jsonObject.getString("req");

                    String selectedValue = jsonObject.get("val").toString();
                    String editdata = ((RadioGroupPlus) view).getTag()
                            .toString();
                    Toast.makeText(mActivity, "" + editdata, Toast.LENGTH_LONG).show();

                    Log.d("radiobutton", "" + editdata);
                    if (selectedValue != null && !selectedValue.equals("")) {
                        // showErrorMessage(true, lbl,"", "",false);
                        return false;
                    }
                }
            }


        }
        // finaljson.put("frm", mainJson);
        return true;
    }

    private void showMessageDialog(String strMsg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(strMsg)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        goBack(mActivity.SERVICE_Thread);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void showErrorMessage(boolean isDataValueEmpty, String label, String minStr, String maxStr, boolean isForNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("Action Required");
        if (isDataValueEmpty)
            if (minStr.equalsIgnoreCase("picture")) {
                builder.setMessage("\" " + label + "\" " + " is missing. Please click " + minStr.toLowerCase() + " to continue.");
            } else {

                if (minStr.equalsIgnoreCase("signature")) {

                    builder.setMessage(label + " is missing. Please add a signature to continue.");

                } else {
                    builder.setMessage(label + " is missing required data. Please enter to continue.");

                }

            }

        else {
            if (isForNumber)
                builder.setMessage(label + " must be between range of " + minStr + " and " + maxStr + " . Please enter required data to continue.");
            else
                builder.setMessage(label + " must be between " + minStr + " and " + maxStr + " characters in length. Please enter required data to continue.");
        }
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

    private void showErrorStringMessage(String label, String StringMsg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT);

        builder.setTitle("Action Required");

        builder.setMessage(label + " " + StringMsg);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

            }

        });

        //builder.show();
        //YD OR
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        //YD 2020 doing UI setting for dialog
        Utilities.setDividerTitleColor(alertDialog, mheight, mActivity);
        Button button_positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Utilities.setDefaultFont_12(button_positive);

    }

    ImageView tempimageview;

    private void DeleteFormImage(String label, String StringMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle(label);
        builder.setMessage(StringMsg);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                tempimageview = linearLayout.findViewById(viewid);
                tempimageview.setImageDrawable(null);

                ImageView camImg = linearLayout.findViewWithTag(viewid + "cam");
                if (camImg != null) {
                    camImg.setAlpha(1f);
                    camImg.setEnabled(true);
                }
                //deletedformImage();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               /* tempimageview =  linearLayout.findViewById(viewid);
                tempimageview.setImageDrawable(null);

                ImageView camImg = linearLayout.findViewWithTag(viewid+"cam");
                if(camImg!=null){
                    camImg.setAlpha(1f);
                    camImg.setEnabled(true);
                }*/
                // deletedformImage();

                dialog.dismiss();
            }
        });
        //builder.show();
        //YD OR
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        //YD 2020 doing UI setting for dialog
        Utilities.setDividerTitleColor(alertDialog, mheight, mActivity);
        Button button_positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button button_negative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Utilities.setDefaultFont_12(button_positive);
        Utilities.setDefaultFont_12(button_negative);

    }

    private void deletedformImage() {
        DeleteMediaRequest req = new DeleteMediaRequest();
        req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(getActivity()) + "/mobi");
        req.setType("post");
        req.setOid(String.valueOf(currentOdrId));
        req.setAction(OrderMedia.ACTION_MEDIA_DELETE);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < deleteIdList.size(); i++) {
            Long id = deleteIdList.get(i);
            builder.append(id);
            if (i != deleteIdList.size() - 1) {
                builder.append("|");

            }

        }

        req.setFileId(builder.toString());
        //req.setFileId(String.valueOf(imageid));

        OrderMedia.deleteData(req, mActivity, AddEditFormFragment.this, DELETEMEDIA);

    }


    @Override
    public void ServiceStarter(RespCBandServST activity, Intent intent) {

    }

    @Override
    public void setResponseCallback(String response, Integer reqId) {

    }

    private void getAndUpdateNumberOfOrderForms(String orderId) {
        HashMap<Long, Order> orderMap = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
        Order odrObj = orderMap.get(Long.parseLong(orderId));
        odrObj.setCustFormCount(odrObj.getCustFormCount() + 1);
        DataObject.ordersXmlDataStore = orderMap;

    }

    @Override
    public void setResponseCBActivity(Response response) {

        if (response != null) {
            if (response.getStatus().equals("success") && response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED))) {
                if (response.getId() == SAVE_FORM) {
                    if (formMode.equals("ADD FORM")) {
                        getAndUpdateNumberOfOrderForms(currentOdrId);
                    }

                    goBack(mActivity.SERVICE_Thread);
                    if (picturetid == 13) {
                        doActioncb(uridata, imageviewid, "CAM");

                    }


//                        else if (picturetid == 14) {
//
//                            HashMap<Long, Order> orderList = (HashMap<Long, Order>) DataObject.ordersXmlDataStore;
//                            Order odrObj = orderList.get(Long.valueOf(currentOdrId));
//                            RecordAudioFragment recordFragment = new RecordAudioFragment();
//                            recordFragment.setActiveOrderObj(odrObj);
//                            mActivity.pushFragments(Utilities.JOBS, recordFragment, true,
//                                    true, BaseTabActivity.UI_Thread);
//                        }


                }

                if (response.getId() == SAVEPICTURE) {

//                    OrderFormsFragment.isSyncMedia = true;
//                    goBack(mActivity.SERVICE_Thread);
                    OrderFormsFragment.isSyncMedia = true;
                    DBEngine.syncDataToSever(mActivity, OrderMedia.TYPE_SAVE);

                }


                if (response.getId() == GETFILEMETA) {
                    final Object respMap = response.getResponseMap();
                    DataObject.orderPicsXmlStore = response.getResponseMap();
                    //      isSyncMedia = true;
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getImagepath1(respMap);

                        }
                    });

                }

                if (response.getId() == DELETEMEDIA) {
                    tempimageview.setVisibility(View.VISIBLE);
                    getFileMeta();

                    DBEngine.syncDataToSever(mActivity.getApplicationContext(), OrderMedia.TYPE_SAVE);


                    if (deleteIdList.size() > 0) {
                        deleteIdList.clear();
                        new Handler(
                        ).postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                SubmittedFormtoServer(finaljson);
                            }
                        }, 2000);

                    }
                }

            } else if (response.getStatus().equals("success") &&
                    response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA))) {
                if (response.getId() == GETFILEMETA) {
                    final Object respMap = response.getResponseMap();
                    mActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            getImagepath1(respMap);

                        }
                    });
                }
            }

        }

    }

    private String getFomattedTime(Long time) {
        String strFDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM:d:yyyy hh:mm a");
        if (time > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            String strDate = String.valueOf(sdf.format(cal.getTime()).split(" ")[0].replace(":", " "));//getting April 03 2016

            String notat = "";
            if ((sdf.format(cal.getTime()).split(" ")[2]).equalsIgnoreCase("PM"))
                notat = "pm";
            else if ((sdf.format(cal.getTime()).split(" ")[2]).equalsIgnoreCase("AM"))
                notat = "am";

            int hours = Integer.parseInt(sdf.format(cal.getTime()).split(" ")[1].split(":")[0]);
            int minute = Integer.parseInt(sdf.format(cal.getTime()).split(" ")[1].split(":")[1]);
            String min = String.format("%02d", getRoundedMinute(minute));
            String strTme = String.valueOf(hours + ":" + min + " " + notat);
            strFDate = strDate + " " + strTme;
        }

        return strFDate;
    }


    private static HashMap<Long, OrderMedia> sortHashmap(HashMap<Long, OrderMedia> unsortMap) {

        HashMap<Long, OrderMedia> sortedMap = new LinkedHashMap<Long, OrderMedia>();

        if (unsortMap != null) {
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
            for (Iterator<Map.Entry<Long, OrderMedia>> it = list.iterator(); it.hasNext(); ) {
                Map.Entry<Long, OrderMedia> entry = it.next();
                sortedMap.put(entry.getKey(), entry.getValue());
            }
        }
        return sortedMap;
    }

    public int listofimages = 0;
    public String imageid;
    private ArrayList<Long> deleteIdList = new ArrayList<>();
    public int viewid;

    public void getImagepath1(Object data) {
        HashMap<Long, OrderMedia> odrData = (HashMap<Long, OrderMedia>) data;
        Map<Long, OrderMedia> responseMap = new LinkedHashMap<Long, OrderMedia>();
        //    PreferenceHandler.setPrefEformImagesCount(mActivity,odrData.size());
//        listofimages = odrData.size();

        responseMap = sortHashmap(odrData);
        int imagviewCounter = 0;

        // Collections.reverse(listimagevei);
        Long[] keys = null;
        if (odrData != null) {
            keys = responseMap.keySet().toArray(new Long[responseMap.size()]);
            orderPicsArr = new OrderMedia[odrData.size()];
        }


        if (responseMap != null) {
            FilePathStrings.clear();
            FileNameStrings.clear();

            for (int t = 0; t < listimagevei.size(); t++) {

                final ImageView currentImage = listimagevei.get(t);
                ImageView camImg = linearLayout.findViewWithTag(currentImage.getId() + "cam");

                final OrderMedia curOrderMedia = getImageData(responseMap, odrData, keys, currentImage.getId());
                if (curOrderMedia != null) {
                    currentImage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            viewid = v.getId();
                            imageid = String.valueOf(curOrderMedia.getId());
                            if (!deleteIdList.contains(curOrderMedia.getId())) {
                                deleteIdList.add(curOrderMedia.getId());
                                DeleteFormImage(getResources().getString(R.string.msg_title), getResources().getString(R.string.msg_delete));
                            }
                            //  DeleteFormImage(getResources().getString(R.string.msg_title), getResources().getString(R.string.msg_delete));
                            return true;

                        }
                    });

                    if (curOrderMedia != null) {
                        currentImage.setImageURI(Uri.parse(curOrderMedia.getMetapath()));
                    }

                    final int finalT = FilePathStrings.size() - 1;
                    currentImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            // Toast.makeText(mActivity,""+frmkeyValue,Toast.LENGTH_LONG).show();
                            if (!deleteIdList.contains(curOrderMedia.getId())) {

                                {
                                    if (frmkeyValue.contains("Signature") || frmkeyValue.contains("signature") || frmkeyValue.contains("sig") || frmkeyValue.contains("Sig")) {
                                        ViewImage.headingFlag = 2;
                                    } else {
                                        ViewImage.headingFlag = 1;
                                    }

                                    Intent in = new Intent(mActivity, ViewImage.class);
                                    in.putExtra("filepath", FilePathStrings);
                                    in.putExtra("filename", FileNameStrings);
                                    in.putExtra("visibleimage", true);
                                    in.putExtra("position", finalT);
                                    startActivity(in);

                                }

                            }
                        }
                    });
                }

                if (currentImage.getDrawable() != null) {
                    if (camImg != null) {
                        camImg.setAlpha(0.5f);
                        camImg.setEnabled(false);
                    }
                } else {
                    if (camImg != null) {
                        camImg.setAlpha(1f);
                        camImg.setEnabled(true);
                    }
                }

                  /*if(responseMap.size()>0) {

                      for (int i = 0; i < responseMap.size(); i++) {
                          orderPicsArr[i] = odrData.get(keys[i]);

                          FilePathStrings.add(orderPicsArr[i].getMetapath());
                          FileNameStrings.add("");
                          Filepathposition.add(orderPicsArr[i].getFrmfiledid());
                          Fileimageids.add(String.valueOf(orderPicsArr[i].getId()));

                          uridata.put(Long.valueOf(orderPicsArr[i].getFrmfiledid()), orderPicsArr[i].getMetapath());
                          iddata.put(Long.valueOf(orderPicsArr[i].getFrmfiledid()), String.valueOf(orderPicsArr[i].getId()));

                          ImageView imageView = linearLayout.findViewById(Integer.parseInt(orderPicsArr[i].getFrmfiledid()));
                          imageView.setImageURI(Uri.parse(orderPicsArr[i].getMetapath()));

                          final int finalT1 = t;
                          currentImage.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {

                                  ViewImage.headingFlag = 1;
                                  Intent in = new Intent(mActivity, ViewImage.class);
                                  in.putExtra("filepath", FilePathStrings);
                                  in.putExtra("filename", FileNameStrings);
                                  in.putExtra("position", finalT1);
                                  startActivity(in);
                              }
                          });

                          if (currentImage.getDrawable() != null) {
                              final int finalI = i;
                              currentImage.setOnLongClickListener(new View.OnLongClickListener() {
                                  @Override
                                  public boolean onLongClick(View v) {
                                      viewid = v.getId();
                                      imageid = String.valueOf(orderPicsArr[finalI].getId());
                                      DeleteFormImage(getResources().getString(R.string.msg_title), getResources().getString(R.string.msg_delete));
                                      return true;
                                  }
                              });
                          } else {
                              Toast.makeText(mActivity, "Please upload image", Toast.LENGTH_SHORT).show();
                          }


                      }
                  }else{

                      ImageView camImg = linearLayout.findViewWithTag(currentImage.getId()+"cam");

                      if (currentImage.getDrawable() != null) {
                          if(camImg!=null){
                              camImg.setAlpha(0.5f);
                              camImg.setEnabled(false);
                          }
                      }else{
                          if(camImg!=null){
                              camImg.setAlpha(1f);
                              camImg.setEnabled(true);
                          }
                      }

                  }*/

            }

        }

        FilePathStringstemp = FilePathStrings;
        Filepathpositiontemp = Filepathposition;

    }

    private OrderMedia getImageData(Map<Long, OrderMedia> responseMap, HashMap<Long, OrderMedia> odrData, Long[] keys, int id) {

        OrderMedia newOrderMedia = null;

        for (int i = 0; i < responseMap.size(); i++) {
            OrderMedia orderMedia = odrData.get(keys[i]);
            if (id == Integer.parseInt(orderMedia.getFrmfiledid())) {
                newOrderMedia = orderMedia;
                FilePathStrings.add(newOrderMedia.getMetapath());
                FileNameStrings.add("");
                Filepathposition.add(newOrderMedia.getFrmfiledid());
                Fileimageids.add(String.valueOf(newOrderMedia.getId()));
                uridata.put(Long.valueOf(newOrderMedia.getFrmfiledid()), newOrderMedia.getMetapath());
                iddata.put(Long.valueOf(newOrderMedia.getFrmfiledid()), String.valueOf(newOrderMedia.getId()));
                break;
            }
        }

        return newOrderMedia;
    }

    OrderMedia orderPicsArr[];
    ArrayList<String> Fileimagecounter;
    ArrayList<Long> FileIdsList;
    public static int us = 0;
    ArrayList<String> FilePathStrings = new ArrayList<String>();
    ArrayList<String> FileNameStrings = new ArrayList<String>();
    ArrayList<String> Filepathposition = new ArrayList<String>();
    ArrayList<String> Filepathpositiontemp = new ArrayList<String>();
    ArrayList<String> FilePathStringstemp = new ArrayList<String>();
    ArrayList<String> Fileimageids = new ArrayList<String>();

    public long mLastClickTime = 0;

//    private void getImagepath(Object data) {
//
//        HashMap<Long, OrderMedia> odrData = (HashMap<Long, OrderMedia>) data;
//        Map<Long, OrderMedia> responseMap = new LinkedHashMap<Long, OrderMedia>();
//
//        responseMap = sortHashmap(odrData);
//        int imagviewCounter = 0;
//
//        Long[] keys = null;
//        if (odrData != null) {
//            keys = responseMap.keySet().toArray(new Long[responseMap.size()]);
//            orderPicsArr = new OrderMedia[odrData.size()];
//        }
//        Fileimagecounter = new ArrayList<String>();
//
//        ArrayList<String> imgs = new ArrayList<>();
//        ArrayList<DisplayImageOptions> imgoption = new ArrayList<>();
//
//        if (responseMap != null) {
//            for (int i = 0; i < responseMap.size(); i++) {
//                orderPicsArr[i] = odrData.get(keys[i]);
//                if (orderPicsArr[i].getFormKey() != null && orderPicsArr[i].getFormKey().equalsIgnoreCase(editformkey)) {
//
//                    FilePathStrings.add(orderPicsArr[i].getMetapath());
//                    FileNameStrings.add("");
//                    Fileimagecounter.add(orderPicsArr[i].getFrmfiledid());
//                    imgs.add(orderPicsArr[i].getFile_desc());
//
//                    }
//            }
//
//            Collections.reverse(listimagevei);
//            Collections.reverse(imgs);
//            Collections.reverse(Fileimagecounter);
//            for (int r = 0; r < FilePathStrings.size(); r++) {
//
//                if (listimagevei.get(r).getId() == Integer.parseInt(Fileimagecounter.get(r))) {
//
//                    String basename = getThumbPath(FilePathStrings.get(r));
//                    ImageLoader imageLoader = ImageLoader.getInstance();
//                    imageLoader.init(ImageLoaderConfiguration.createDefault(mActivity));
//                    DisplayImageOptions options = new DisplayImageOptions.Builder()
//                            .cacheInMemory(true)
//                            .cacheOnDisc(false)
//                            .resetViewBeforeLoading(true)
//                            .imageScaleType(ImageScaleType.EXACTLY)
//                            .build();
//
//
//                    if (basename != null) {
//                        imageLoader.displayImage("file://" + basename, listimagevei.get(r), options);
//                    }
//
//
//                }
//                }
//
//            }
//
//
//        for (int c = 0; c<listimagevei.size(); c++) {
//            //      Collections.reverse(FilePathStrings);
//            final int finalImagviewCounter = imagviewCounter;
//            final int finalC = c;
//            listimagevei.get(c).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ViewImage.headingFlag = 1;
//                  //  Collections.reverse(FilePathStrings);
//                    Intent i = new Intent(mActivity, ViewImage.class);
//                    i.putExtra("filepath", FilePathStrings);
//                    i.putExtra("filename", FileNameStrings);
//                    i.putExtra("position", finalC);
//                    startActivity(i);
//
//                }
//            });
//
//        }
//
//
//    }


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

        if (str != null && str.contains(".")) {///storage/emulated/0/AceRoute/temp_1462527798949.jpg
            String array[];
            //array = str.split("\\.",-1);
            String basePath = str.substring(0, str.lastIndexOf("."));
            String ext = str.substring(str.lastIndexOf(".") + 1);

            return basePath + "_Thumb." + ext;
        }
        return null;

    }


    private String getThumbPath(String str) {

        if (str != null && !str.trim().equals("")) {///storage/emulated/0/AceRoute/temp_1462527798949.jpg
            String array[];
            Log.d("strrr", str + "/");
            array = str.split("\\.");

            return array[0] + "_Thumb." + array[1];
        }
        return null;
    }


    private void getFileMeta() {
        //    long numberOfMeta = getNumberOfOrderMeta();
//        Log.e("Aceroute", "getFileMeta called for Grid view main activity at"+Utilities.getCurrentTimeInMillis());
//        if (numberOfMeta >= 0) {
        /*{"url":"'+AceRoute.appUrl+'",'+ '"action": "getfilemeta",' +'"oid":"' +orderId+'"}*/
        GetFileMetaRequest req = new GetFileMetaRequest();
        req.setUrl("https://" + PreferenceHandler.getPrefBaseUrl(getActivity()) + "/mobi");
        req.setAction(OrderMedia.ACTION_GET_MEDIA);
        req.setOid(currentOdrId);
        req.setFrmkey(editformkey);
        OrderMedia.getData(req, mActivity, this, GETFILEMETA);
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
    public void onCancelledBtn() {

    }

    class mDateSetListener implements DatePickerDialog.OnDateSetListener {
        TextView txt;


        public mDateSetListener(TextView view) {
            this.txt = view;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            String dateToSave = null;
            int mYear = year;
            int mMonth = monthOfYear + 1;
            int mDay = dayOfMonth;
            String editMonth;
            String editDate;
            if (mMonth < 10)
                editMonth = "0" + mMonth;
            else
                editMonth = mMonth + "";

            if (mDay < 10)
                editDate = "0" + mDay;
            else
                editDate = mDay + "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date curDate = null;
            try {
                curDate = sdf.parse(mYear + "-" + editMonth + "-" + editDate);

                Calendar cal = Calendar.getInstance();
                cal.setTime(curDate);
                SimpleDateFormat convStrToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");//have to send "2015/06/02 11:25 -00:00"
                dateToSave = Utilities.convertDateToUtcWithDateFormat(cal.getTimeInMillis(), convStrToDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String mCurrentDate = new SimpleDateFormat("MMM").format(curDate) + " " + editDate + " " + mYear;
            txt.setText(mCurrentDate);

            try {
                JSONObject jsonObj = (JSONObject) txt.getTag();
                jsonObj.put("val", dateToSave);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onBackPressed() {

        try {
            if (!BackDataDataClicked()) {

                showMessageDialog("Entered data will be lost. Are you sure you want to go back?");
                return true;
            } else {
                return false;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //  Toast.makeText(mActivity,"test",Toast.LENGTH_LONG).show();

        return true;

    }
}
