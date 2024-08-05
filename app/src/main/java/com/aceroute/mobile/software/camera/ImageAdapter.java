package com.aceroute.mobile.software.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.AsyncTask;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.utilities.PreferenceHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


//import uk.co.senab.photoview.PhotoViewAttacher;


public class ImageAdapter extends PagerAdapter {
	private ViewImage activity;
	private ArrayList<String> filepath;
	private ArrayList<String> filename;
	private LayoutInflater inflater;
	private TextView text;
	boolean imgvisibility;
	boolean inboxrotatevisibility;
	//PhotoViewAttacher mAttacher;

	static  TouchImageView imageView;
	//YD using for zooming image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	PointF startPoint = new PointF();
	PointF midPoint = new PointF();
	float oldDist = 1f;
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;
	private HashMap<Integer,View> viewMapper = new HashMap<>();

	public ImageAdapter(ViewImage a, ArrayList<String> fpath, ArrayList<String> fname) {
		activity = a;
		filepath = fpath;
		filename = fname;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public ImageAdapter(ViewImage a, ArrayList<String> fpath, ArrayList<String> fname, boolean img_visibility) {
		this(a,fpath,fname);
		imgvisibility= img_visibility;

	}

	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return super.getItemPosition(object);
	}

	@Override
	public Object instantiateItem(final ViewGroup container, final int position) {

		final View viewItem = inflater.inflate(R.layout.pager_item, container, false);
		ImageView imageView = (TouchImageView) viewItem.findViewById(R.id.image);
		viewMapper.put(position,imageView);
		new LoadImage(imageView,filepath.get(position)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		//RotateImage(imageView);

		/*BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(filepath.get(position), options);
		imageView.setImageBitmap(bitmap);*/
		//Picasso.with(activity).load(filepath.get(position)).into(imageView);
		text = (TextView) viewItem.findViewById(R.id.text_imgshow);
		/*ImageView rorateViewLeft = viewItem.findViewById(R.id.imgview_rotate);

		rorateViewLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imageView.setRotation(imageView.getRotation() + 90);

			}

		});

		if(imgvisibility) {
				rorateViewLeft.setVisibility(View.VISIBLE);

		}

		else
		{

			rorateViewLeft.setVisibility(View.GONE);
		}
*/
		/*ImageView rorateViewRight = viewItem.findViewById(R.id.imgview_rotate_right);
		rorateViewRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imageView.setRotation(-90f);
			}
		});*/
		text.setText(filename.get(position));
		TypeFaceFont.overrideFonts(activity,text);
		text.setTextSize(18 + (PreferenceHandler.getCurrrentFontSzForApp(activity)));

		imageView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
//				CaptureSignature captureSignFrag = new CaptureSignature();
//				Bundle b = new Bundle();
//				b.putStringArrayList("filepath", filepath);
//				b.putStringArrayList("filename", filename);
//				b.putString("Flag", "");
//				b.putInt("position", position);
//				captureSignFrag.setArguments(b);
//
//
//				FragmentManager manager = activity.getSupportFragmentManager();
//				FragmentTransaction ft = manager.beginTransaction();
//				ft.addToBackStack(null);
//				ft.add(R.id.realtabcontent, captureSignFrag);
//				ft.commit();

//				Intent i = new Intent(activity, CaptureSignature.class);
//
//				i.putExtra("filepath", filepath);
//				i.putExtra("Flag", 1);
//				i.putExtra("filename", filename);
//				i.putExtra("position", position);
//				activity.startActivityForResult(i, 0);

//				Toast.makeText(activity, "new click", Toast.LENGTH_SHORT).show();

				return true;
			}
		});





		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				CaptureSignature captureSignFrag = new CaptureSignature();
//				Bundle b = new Bundle();
//				b.putStringArrayList("filepath", filepath);
//				b.putStringArrayList("filename", filename);
//				b.putString("Flag", "");
//				b.putInt("position", position);
//				captureSignFrag.setArguments(b);
//
//				BaseTabActivity.mBaseTabActivity.pushFragments(Utilities.JOBS, captureSignFrag, true, true,BaseTabActivity.UI_Thread);

			}
		});


		container.addView(viewItem);

		return viewItem;

	}
	public void rotateImage(int position){
if(viewMapper.get(position) != null) {
	View imageView = viewMapper.get(position);
	imageView.setRotation(imageView.getRotation()+90);
}
	}

	public void RotateImage(TouchImageView imageView) {

		imageView.setRotation(imageView.getRotation() + 90);

	}

	class LoadImage extends AsyncTask<Object, Void, Bitmap> {

		private ImageView imv;
		private String path;

		public LoadImage(ImageView imv,String Path) {
			this.imv = imv;
			this.path = Path;
		}

		@Override
		protected Bitmap doInBackground(Object... params) {
			Bitmap bitmap = null;
			File file = new File(path);
			if (file.exists()) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				bitmap = BitmapFactory.decodeFile(path, options);

			}

			return bitmap;

		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null && imv != null) {
				imv.setImageBitmap(result);
			}
		}

	}


	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((RelativeLayout) object);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return filepath.size();
	}
	@Override
	public boolean isViewFromObject(View v, Object ob) {
		// TODO Auto-generated method stub
		return v == ((View) ob);
	}
	public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
		    int reqHeight) {

		   Bitmap bm = null;
		   // First decode with inJustDecodeBounds=true to check dimensions
		   final BitmapFactory.Options options = new BitmapFactory.Options();
		   /*options.inJustDecodeBounds = true;
		   BitmapFactory.decodeFile(path, options);*///YD commenting because change the size of the image when clicking once on image

		// Calculate inSampleSize
		  /* options.inSampleSize = calculateInSampleSize(options, reqWidth,
		     reqHeight);*///YD commenting because change the size of the image when clicking once on image

		  // Decode bitmap with inSampleSize set
		  // options.inJustDecodeBounds = false;////YD commenting because change the size of the image when clicking once on image
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

}
