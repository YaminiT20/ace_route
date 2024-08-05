/*
package com.software.mobile.software;

import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.software.mobile.software.dialog.TypeFaceFont;
import com.software.mobile.software.http.RequestObject;
import com.software.mobile.software.offline.application.DownloadPackage;
import com.software.mobile.software.utilities.PreferenceHandler;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class AceRouteApplication extends MultiDexApplication {

    private Map<String, DownloadPackage> packageMap;
	private String mapResourcesDirPath;
	private MediaRecorder recorder = new MediaRecorder();
	private MediaPlayer mPlayer = new MediaPlayer();
	public ArrayList<String> ErrorStringArrLoc = null;
	public ArrayList<String> ErrorStringArrSer = null;
	static AceRouteApplication aceRouteAppContxt;

	public static AceRouteApplication getInstance(){
		if (aceRouteAppContxt == null)
			aceRouteAppContxt = new AceRouteApplication();
		return aceRouteAppContxt;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		TypeFaceFont.overrideFont(getApplicationContext(), "SERIF", "www/fonts/lato/lato-regular-webfont.ttf");
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc(true).cacheInMemory(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new WeakMemoryCache())
				.discCacheSize(100 * 1024 * 1024).build();

		ImageLoader.getInstance().init(config);
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	public MediaRecorder getRecorderObject() {
		return recorder;
	}
	
	public void setRecorderObject(){
		recorder = null;
		recorder = new MediaRecorder();
	}
	
	public MediaPlayer getPlayerObject() {
		return mPlayer;
	}
	
	public MediaPlayer getPlayerObject(Context context, Uri uri) {
		try {
			if(mPlayer.isPlaying()){
				mPlayer.stop();
				mPlayer.release();
				mPlayer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			mPlayer = MediaPlayer.create(context, uri);
			return mPlayer;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getCurrentDisplayDate(Context ApplicationContext){
		return PreferenceHandler.getTempDate(ApplicationContext);
	}

	public BlockingQueue<RequestObject> messageQueue = new ArrayBlockingQueue<RequestObject>(30);
	
	public BlockingQueue<RequestObject> getMessageQueue(){
		return messageQueue;
	}
	
	public void stopApplication() {
		new BaseTabActivity().finishAct();  //YD check
	}

		public Map<String, DownloadPackage> getPackageMap() {
		    return packageMap;
		}

		public void setPackageMap(Map<String, DownloadPackage> packageMap) {
		    this.packageMap = packageMap;
		}

		public String getMapResourcesDirPath() {
		    return mapResourcesDirPath;
		}
}*/

package com.aceroute.mobile.software;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.RequestObject;
import com.aceroute.mobile.software.offline.application.DownloadPackage;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class AceRouteApplication extends MultiDexApplication {
	public static final String CHANNEL_ID = "aceRoutesChannel";
	private Map<String, DownloadPackage> packageMap;
	private String mapResourcesDirPath;
	private MediaRecorder recorder = new MediaRecorder();
	private MediaPlayer mPlayer = new MediaPlayer();
	public ArrayList<String> ErrorStringArrLoc = null;
	public ArrayList<String> ErrorStringArrSer = null;
	static AceRouteApplication aceRouteAppContxt;

	public static AceRouteApplication getInstance(){
		if (aceRouteAppContxt == null)
			aceRouteAppContxt = new AceRouteApplication();
		return aceRouteAppContxt;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		createNotificationChannel();

		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());

		TypeFaceFont.overrideFont(getApplicationContext(), "SERIF", "www/fonts/lato/lato-regular-webfont.ttf");
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc(true).cacheInMemory(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new WeakMemoryCache())
				.discCacheSize(100 * 1024 * 1024).build();

		ImageLoader.getInstance().init(config);
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	public MediaRecorder getRecorderObject() {
		return recorder;
	}

	public void setRecorderObject(){
		recorder = null;
		recorder = new MediaRecorder();
	}

	public MediaPlayer getPlayerObject() {
		return mPlayer;
	}

	public MediaPlayer getPlayerObject(Context context, Uri uri) {
		try {
			if(mPlayer.isPlaying()){
				mPlayer.stop();
				mPlayer.release();
				mPlayer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			mPlayer = MediaPlayer.create(context, uri);
			return mPlayer;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getCurrentDisplayDate(Context ApplicationContext){
		return PreferenceHandler.getTempDate(ApplicationContext);
	}

	public BlockingQueue<RequestObject> messageQueue = new ArrayBlockingQueue<RequestObject>(50);

	public BlockingQueue<RequestObject> getMessageQueue(){
		return messageQueue;
	}

	public void stopApplication() {
		new BaseTabActivity().finishAct();  //YD check
	}

	public Map<String, DownloadPackage> getPackageMap() {
		return packageMap;
	}

	public void setPackageMap(Map<String, DownloadPackage> packageMap) {
		this.packageMap = packageMap;
	}

	public String getMapResourcesDirPath() {
		return mapResourcesDirPath;
	}


	private void createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "AceRoutes Channel", NotificationManager.IMPORTANCE_DEFAULT);
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			if (notificationManager != null) {
				notificationManager.createNotificationChannel(notificationChannel);
			}
		}
	}
}