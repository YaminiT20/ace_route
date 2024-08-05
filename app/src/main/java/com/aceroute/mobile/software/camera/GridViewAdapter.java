package com.aceroute.mobile.software.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class GridViewAdapter extends BaseAdapter {

	// Declare variables
	private Context activity;
	private ArrayList<String> filepath;
	private ArrayList<String> filename;
	private int fmetaType;
	ArrayList<String> updateTimelist;

	private static LayoutInflater inflater = null;

	public GridViewAdapter(Context a, ArrayList<String> fpath, ArrayList<String> fname, int fmetaType,ArrayList<String> fupdlist) {
		Log.i( Utilities.TAG, "GridViewAdapter constructor called at" + Utilities.getCurrentTimeInMillis());
		activity = a;
		filepath = fpath;
		filename = fname;
		updateTimelist = fupdlist;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.fmetaType = fmetaType;
	}

	public int getCount() {
		return filepath.size();

	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i( Utilities.TAG, "getView called for position"+position+ " at" + Utilities.getCurrentTimeInMillis());
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.gridview_item, null);
		// Locate the TextView in gridview_item.xml
		Typeface tf = TypeFaceFont.getCustomTypeface(activity);
		TextView text = (TextView) vi.findViewById(R.id.text);
		TextView udpateTme = (TextView) vi.findViewById(R.id.media_updateTime);
		text.setTypeface(tf);
		udpateTme.setTypeface(tf,Typeface.BOLD);
		// Locate the ImageView in gridview_item.xml
		ImageView image = (ImageView) vi.findViewById(R.id.image);

		// Set file name to the TextView followed by the position.
		text.setText(filename.get(position));
		udpateTme.setText(updateTimelist.get(position).toString());
		text.setTextSize(16 + (PreferenceHandler.getCurrrentFontSzForApp(activity)));
		udpateTme.setTextSize(16 + (PreferenceHandler.getCurrrentFontSzForApp(activity)));

		// Code for requesting to server with post request . use this when the filepath is of server
			/*SimpleLoader imageloader = new SimpleLoader(activity);
			imageloader.DisplayImage(String.valueOf((filepath.get(position))), image);*/
		// Decode the filepath with BitmapFactory followed by the position
				//Bitmap bmp = BitmapFactory.decodeFile(filepath.get(position));


		if (fmetaType == 3) {
                image.setPadding(10, 5, 0, 5);
			    image.setScaleType(ImageView.ScaleType.CENTER_CROP);
			    String file_path = filepath.get(position).toString();
				MediaPlayer mp = MediaPlayer.create(activity, Uri.parse(file_path));
				int mili_sec = 0;
				try {
					mili_sec=mp.getDuration();
				}catch (NullPointerException e){
					e.printStackTrace();
				}
				long second = TimeUnit.MILLISECONDS.toSeconds(mili_sec);

				image.setImageResource(R.drawable.sec_audio_200);
				/*if(second<16)
			    image.setImageResource(R.drawable.sec_audio_15);

				else if(second>15 && second<31)
					image.setImageResource(R.drawable.sec_audio_30);

				else if(second>30 && second<46)
					image.setImageResource(R.drawable.sec_audio_45);

				else if(second>45 && second<61)
					image.setImageResource(R.drawable.sec_audio_60);*/

		  }
		else if (fmetaType == 4) {
			Uri selectedUri = Uri.fromFile(new File(filepath.get(position).toString()));
			//if(selectedUri!=null)
				//text.setText(new File(""+selectedUri).getName());
			if (filename.get(position).equals("Customer Order History"))// doing this for customer history file to show description like this
				text.setText("Customer Order History");
			/*else
				text.setText(new File(""+selectedUri).getName());*///YD Commenting because need to show the dtl here

			String fileExtension
					= MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
			Log.d(" FILES", fileExtension);

          if(fileExtension.equalsIgnoreCase("pdf"))
			  image.setImageResource(R.drawable.pdf_200);

		  else if(fileExtension.equalsIgnoreCase("txt") || fileExtension.equalsIgnoreCase("doc") || fileExtension.equalsIgnoreCase("docx"))
			  image.setImageResource(R.drawable.doc_200);

		  else if(fileExtension.equalsIgnoreCase("png")||fileExtension.equalsIgnoreCase("jpg")||fileExtension.equalsIgnoreCase("jpeg")||fileExtension.equalsIgnoreCase("gif"))
			  image.setImageResource(R.drawable.gif_200);

		  else if(fileExtension.equalsIgnoreCase("zip"))
			  image.setImageResource(R.drawable.zip_200);

		  else if(fileExtension.equalsIgnoreCase("mp3")||fileExtension.equalsIgnoreCase("mp4")||fileExtension.equalsIgnoreCase("mpeg")||fileExtension.equalsIgnoreCase("avi")||fileExtension.equalsIgnoreCase("flv")||fileExtension.equalsIgnoreCase("mkv")||fileExtension.equalsIgnoreCase("webm")||fileExtension.equalsIgnoreCase("3gp"))
			  image.setImageResource(R.drawable.mov_200);

		  else if(fileExtension.equalsIgnoreCase("xls")||fileExtension.equalsIgnoreCase("xlsx"))
			  image.setImageResource(R.drawable.xls_200);

		  else
			  image.setImageResource(R.drawable.log_200);

		}
		else if (fmetaType == 1){
			String s=filepath.get(position).toString();
			String basename =getThumbPath(s);

			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.init(ImageLoaderConfiguration.createDefault(activity));
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisc(false)
					.resetViewBeforeLoading(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.resetViewBeforeLoading(true)
					.build();
			if(basename!=null)
			imageLoader.displayImage("file://" + basename, image, options);

		}else {//YD using when there was no imageloaderse {
			/*Bitmap bmp = decodeSampledBitmapFromUri(filepath.get(position), 150,
					150);
			image.setPadding(10, 5, 0, 5);
			image.setScaleType(ImageView.ScaleType.CENTER_CROP);
			// Set the decoded bitmap into ImageVie
			image.setImageBitmap(bmp);*/


			// YD For Universal Image Loader
			/*Uri uri = Uri.fromFile(new File(filepath.get(position)));
			Picasso.with(activity).load(uri).into(image);*/

	//romil start
			/*String s=filepath.get(position).toString();
			String basename =getThumbPath(s);
			Log.e("file name extension",basename);*/
	//romil end

			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.init(ImageLoaderConfiguration.createDefault(activity));
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisc(false)
					.resetViewBeforeLoading(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.resetViewBeforeLoading(true)
					.build();
			imageLoader.displayImage("file://"+filepath.get(position).toString(), image, options);

		}
		return vi;
	}

	private String getThumbPath(String str) {

		if (str!=null && !str.trim().equals("")){///storage/emulated/0/AceRoute/temp_1462527798949.jpg
			String array[];
			Log.d("strrr",str+"/");
			array = str.split("\\.");

			return array[0]+"_Thumb."+array[1];
		}
		return null;
	}


	public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
		    int reqHeight) {

		   Bitmap bm = null;
		   // First decode with inJustDecodeBounds=true to check dimensions
		   final BitmapFactory.Options options = new BitmapFactory.Options();
		   options.inJustDecodeBounds = true;
		   BitmapFactory.decodeFile(path, options);

		   // Calculate inSampleSize
		   options.inSampleSize = calculateInSampleSize(options, reqWidth,
		     reqHeight);

		   // Decode bitmap with inSampleSize set
		   options.inJustDecodeBounds = false;
		   bm = BitmapFactory.decodeFile(path, options);

		   return bm;
		  }

		  public int calculateInSampleSize(

		  BitmapFactory.Options options, int reqWidth, int reqHeight) {
		   // Raw height and width of image
		   final int height = options.outHeight;
		   final int width = options.outWidth;
		   int inSampleSize = 1;

		   if (height > reqHeight || width > reqWidth) {
		    if (width > height) {
		     inSampleSize = Math.round((float) height
					 / (float) reqHeight);
		    } else {
		     inSampleSize = Math.round((float) width / (float) reqWidth);
		    }
		   }

		   return inSampleSize;
		  }
//romil start
	public static String removeExtension(String s) {

		String separator = System.getProperty("file.separator");
		String filename;

		// Remove the path upto the filename.
		int lastSeparatorIndex = s.lastIndexOf(separator);
		if (lastSeparatorIndex == -1) {
			filename = s;
		} else {
			filename = s.substring(lastSeparatorIndex + 1);
		}

		// Remove the extension.
		int extensionIndex = filename.lastIndexOf(".");
		if (extensionIndex == -1)
			return filename;

		return filename.substring(0, extensionIndex);
	}

//romil end

}