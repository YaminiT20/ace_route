package com.aceroute.mobile.software.offline;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aceroute.mobile.software.AceRouteApplication;
import com.aceroute.mobile.software.component.reference.OfflineCountry_City;
import com.aceroute.mobile.software.offline.application.DownloadPackage;
import com.aceroute.mobile.software.utilities.Utilities;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Activity which displays map packages
 * 
 * 
 */
public class MapPackagesListActivity {
    
    
    private AceRouteApplication application;

	downloadlist dlistner;

	public interface downloadlist{
		public void onCmpt(boolean val);
	}

	public void onDownloadComplete(downloadlist event){
		this.dlistner=event;
	}

	/**
     * Packages currently shown in list_cal
     */
    private List<DownloadPackage> currentPackages;
    private static List<DownloadPackage> currentPackagesAvail;// already downlaoded , for check mark

	public String cityname;
	DownloadPackage currentPackage=null;
	private boolean datamatch=false;

	//Download method variable initialise
	 private static final int NO_BYTES_INTO_ONE_MB = 1048576;
	    
	    /**
	     * Path at which download packages are temporarily stored
	     */
	    private static String packagesPath;
	    
	    
	    private ProgressBar progressBar;
	    
	    private Button startDownloadButton;
	    
	    private TextView downloadPercentage;
	    
	    /**
	     * Selected map package to be downloaded
	     */
	    private DownloadPackage dowloadPackage;
	    
	    /**
	     * Index of the current download resource
	     */
	    private int downloadResourceIndex;
	    
	    /**
	     * URLs to download resources
p	     */
	    private List<String> downloadResourceUrls;
	    
	    /**
	     * Download resources extensions
	     */
	    private List<String> downloadResourceExtensions;

		private Activity activityname;

		private String stateName="Hawaii";
		private ArrayList<String> namelist;
		private ArrayList<String> namelist1;
		private ArrayList<String> namelist2;
		private ArrayList<String> namelist3;
		private ArrayList<String> namelist4;
		private ArrayList<String> namelist5;
	
    
    public void methoddata(Context activity ,String statename) {
    	//YD activityname=activity;
    	stateName =statename;
        application =  AceRouteApplication.getInstance();/*.getApplication()*/;
        progressBar = new ProgressBar(activity.getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);
		downloadPercentage=new TextView(activity.getApplicationContext(), null, android.R.attr.textAppearanceSmall);
       if (application.getPackageMap() != null) {
            // map packages are already available
         currentPackages = searchByParentCode(null);
            initialMethod();
        } else {
            // map packages need to be obtained from parsing the Maps.xml file
            new Thread() {
                
                public void run() {
                    // get a parser object to parse the Maps.xml file
                    currentPackages = searchByParentCode(null);

                    Log.e( Utilities.TAG,
        					"currentPackages installed are."+currentPackages);

                   /* activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {YD*/
                            initialMethod();
                            if(datamatch){
                            	datamatch=false;
                            packagesPath = application.getMapResourcesDirPath() + "/Maps/downloads/";
                            dowloadPackage = application.getPackageMap().get(currentPackage.getCode());
                            prepareDownloadResources();
                            }
                        /*}

                    });YD*/
                }
            }.start();
       }
    }
    //String name="Hawaii";

	
    private void initialMethod() {
		// TODO Auto-generated method stub
    	namelist = new ArrayList<String>();
    	namelist1 = new ArrayList<String>();
    	namelist2 = new ArrayList<String>();
    	namelist3 = new ArrayList<String>();
    	namelist4 = new ArrayList<String>();
    	namelist5 = new ArrayList<String>();
    	for(int i=0;i<currentPackages.size();i++){
            currentPackage = currentPackages.get(i);
    	   cityname =currentPackage.getName();
    	   if (i<250)
    	   namelist.add(currentPackage.getName());
    	   if (i>=250&& i<=500)
    	   namelist1.add(currentPackage.getName());
    	   if (i>=500&& i<=750)
    	   namelist2.add(currentPackage.getName());
    	   if (i>=750&& i<=1000)
    	   namelist3.add(currentPackage.getName());
    	   if (i>=1000&& i<=1250)
    	   namelist4.add(currentPackage.getName());
    	   if (i>1250)
    	   namelist5.add(currentPackage.getName());
    	   if(cityname.equals(stateName)){
    		   datamatch=true;
    		  return;
    	   }
    	}
    	
	}
/**************************YD*s*******************************************************************/ 
    public ArrayList<OfflineCountry_City> getAllCountriesAvail()
    {
    	OfflineCountry_City ctryUS = null;
    	ArrayList<OfflineCountry_City> countryList = new ArrayList<OfflineCountry_City>();
    	
    	if (currentPackagesAvail!=null && currentPackagesAvail.size()>0)
    	{	
	    	for(int i=0;i<currentPackagesAvail.size();i++){
	    		currentPackage = currentPackagesAvail.get(i);
	    		String type = currentPackage.getType();
	    		if (type.equals("country")){
	    			OfflineCountry_City countryLst = new OfflineCountry_City();
	    			countryLst.setCode(currentPackage.getCode());
	    			countryLst.setName(currentPackage.getName());
	    			countryLst.setParentCode(currentPackage.getParentCode());
	    			countryLst.setType(currentPackage.getType());
	    			countryLst.setChildren(currentPackage.getChildrenCodes());
	    			countryLst.setSize(currentPackage.getSize());
	    			
	    			// YD using to show tick sign if already downloaded
//	    			if (packages.length>0){ // checking if already packages are available/ already downloaded
//	    	    		for (int j=0 ; j<packages.length; j++)
//	    		    	{
//	    		    		String packageNm = packages[j].getName();
//	    		    		if (packageNm.equals(currentPackage.getCode())){
//	    		    			countryLst.setDownloaded(true);
//	    		    			break;
//	    		    		}
//	    		    		if (currentPackage.getChildrenCodes().size()>0){
//	    		    			for (int k=0; k<currentPackage.getChildrenCodes().size() ; k++)
//	    		    			{
//	    		    				if (currentPackage.getChildrenCodes().get(k).equals(packageNm)){
//	    		    					countryLst.setDownloaded(true);
//	    	    		    			break;
//	    		    				}
//	    		    			}
//	    		    		}
//	    		    	}
//	    	    	}
	    			if (currentPackage.getCode().toLowerCase().equals("us"))//United States
	    				ctryUS = countryLst;
	    			else 
	    				countryList.add(countryLst);
	    		}
	    	}
    	}
    	if (countryList!=null && countryList.size()>1)
    	{
    		Collections.sort(countryList, new Comparator<OfflineCountry_City>() {
				@Override
				public int compare(OfflineCountry_City s1, OfflineCountry_City s2) {

					return s1.getName().compareToIgnoreCase(s2.getName());
				}
			});
    	}
    	/*for (int i =0; i<countryList.size();i++)
    	{
    		if (countryList.get(i).getCode().toLowerCase().equals("us")){
    			countryList.add(0,countryList.get(i));
    			countryList.remove(i+1);
    		}
    	}*/
    	if (ctryUS!=null)
    		countryList.add(0,ctryUS);
    	return countryList;
    }
    
    public ArrayList<OfflineCountry_City> getCountryById(ArrayList<String> ctryCode){
    	ArrayList<OfflineCountry_City> countryElem = new ArrayList<OfflineCountry_City>();
    	if (currentPackagesAvail!=null && currentPackagesAvail.size()>0)
    	{	
	    	for(int i=0;i<currentPackagesAvail.size();i++){
	    		currentPackage = currentPackagesAvail.get(i);
	    		if (currentPackage.getCode().equals(ctryCode.get(0))){
	    			OfflineCountry_City countryLst = new OfflineCountry_City();
	    			countryLst.setCode(currentPackage.getCode());
	    			countryLst.setName(currentPackage.getName());
	    			countryLst.setParentCode(currentPackage.getParentCode());
	    			countryLst.setType(currentPackage.getType());
	    			countryLst.setChildren(currentPackage.getChildrenCodes());
	    			countryLst.setSize(currentPackage.getSize());
	    			
//	    			if (packages.length>0){
//	    	    		for (int k=0 ; k<packages.length; k++)
//	    		    	{
//	    		    		String packageNm = packages[k].getName();
//	    		    		if (packageNm.equals(currentPackage.getCode())){
//	    		    			countryLst.setDownloaded(true);
//	    		    			break;
//	    		    		}
//	    		    	}
//	    	    	}
	    			countryElem.add(countryLst);
	    		}
	    	}
    	}	
    	
    	return countryElem;
    }
    
    // YD v.v.important //use to delete downloaded offline package
    //SKPackageManager.getInstance().deleteOfflinePackage(firstPackage.getName());
    //YD
    public void inializePackages(Context activity,boolean AlreadyThread)
    {
		if(AlreadyThread) {
			application = AceRouteApplication.getInstance();
			currentPackagesAvail = searchByParentCode(null);
		}else{
			new Thread(new Runnable() {
				@Override
				public void run() {
					application = AceRouteApplication.getInstance();
//					application.setPackageMap(parser.getPackageMap());
					currentPackagesAvail = searchByParentCode(null);
					dlistner.onCmpt(true);
				}
			}).start();
		}
    }
    //YD
    public ArrayList<OfflineCountry_City> getCities(List<String> citiesList)
    {
    	ArrayList<OfflineCountry_City> cityList = new ArrayList<OfflineCountry_City>();
    	
    	if (currentPackagesAvail!=null && currentPackagesAvail.size()>0)
    	{	
	    	for(int i=0;i<currentPackagesAvail.size();i++){
	    		currentPackage = currentPackagesAvail.get(i);
	    		String cityCode = currentPackage.getCode();
	    		for (int j=0; j<citiesList.size(); j++){
		    		if (cityCode.equals(citiesList.get(j))){
		    			OfflineCountry_City cityLst = new OfflineCountry_City();
		    			cityLst.setCode(currentPackage.getCode());
		    			cityLst.setName(currentPackage.getName());
		    			cityLst.setParentCode(currentPackage.getParentCode());
		    			cityLst.setType(currentPackage.getType());
		    			cityLst.setChildren(currentPackage.getChildrenCodes());
		    			cityLst.setSize(currentPackage.getSize());
		    			
		    			// YD using to show tick sign if already downloaded
//		    			if (packages.length>0){
//		    	    		for (int k=0 ; k<packages.length; k++)
//		    		    	{
//		    		    		String packageNm = packages[k].getName();
//		    		    		if (packageNm.equals(currentPackage.getCode())){
//		    		    			cityLst.setDownloaded(true);
//		    		    			break;
//		    		    		}
//		    		    	}
//		    	    	}
		    			cityList.add(cityLst);
		    			break;
		    		}
	    		}
	    	}
    	}
    	if (cityList!=null && cityList.size()>1)
    	{
    		Collections.sort(cityList, new Comparator<OfflineCountry_City>() {
				@Override
				public int compare(OfflineCountry_City s1, OfflineCountry_City s2) {

					return s1.getName().compareToIgnoreCase(s2.getName());
				}
			});
    	}
    	
    	return cityList;
    }
    
    public void downloadOfflineMap(Context activity ,final String ctryCtyCode )
    {
    	// map packages need to be obtained from parsing the Maps.xml file
    	application =AceRouteApplication.getInstance();
        new Thread() {
            
            public void run() {
                currentPackages = searchByParentCode(null);
                
                Log.e( Utilities.TAG,
    					"currentPackages installed are."+currentPackages);
                
                        initialMethod(ctryCtyCode);
                        if(datamatch){
                        	datamatch=false;
	                        
                        	packagesPath = application.getMapResourcesDirPath() + "/Maps/downloads/";
	                        dowloadPackage = application.getPackageMap().get(currentPackage.getCode());
	                        prepareDownloadResources();
                        }
            }
        }.start();
    }
    private void initialMethod(String ctryCtyCode) {
		// TODO Auto-generated method stub
    	for(int i=0;i<currentPackages.size();i++){
            currentPackage = currentPackages.get(i);
    	   cityname =currentPackage.getCode();
    	 
    	   if(cityname.equals(ctryCtyCode)){
    		   datamatch=true;
    		  return;
    	   }
    	}
    	
	}
    
    public boolean deleteOfflineMapById(String packageNm)
    {
		return false;
    }
    
    /**************************YD*e*******************************************************************/    
    
    
   public void prepareDownloadResources(){
	   /**
	     * Prepares a list_cal of download resources for the selected package to be
	     * downloaded
	     */
	     //YD  Toast.makeText(activityname.getApplicationContext(), "download  state name map"+dowloadPackage.getName(), 0).show();
	        downloadResourceUrls = new ArrayList<String>();
	        downloadResourceExtensions = new ArrayList<String>();
	        downloadResourceIndex = 0;
	    
	      downloadResource(downloadResourceUrls.get(0), downloadResourceExtensions.get(0));
    }
    
	    private void downloadResource(final String url, final String extension) {
	        // thread download a remote resource
	        Thread downloadThread = new Thread() {
	            
	            private long lastProgressUpdateTime = System.currentTimeMillis();
	            
	            @Override
	            public void run() {
	                super.run();
	                // get the request used to download the resource at the URL
	                HttpGet request = new HttpGet(url);
	                DefaultHttpClient httpClient = new DefaultHttpClient();
	                try {
	                    // execute the request
	                    HttpResponse response = httpClient.execute(request);
	                    InputStream responseStream = response.getEntity().getContent();
	                    if (!new File(packagesPath).exists()) {
	                        new File(packagesPath).mkdirs();
	                    }
	                    // local file at temporary path where the resource is
	                    // downloaded
	                    RandomAccessFile localFile =
	                            new RandomAccessFile(packagesPath + dowloadPackage.getCode() + extension, "rw");
	                    
	                    // download the resource to the temporary path
	                    long bytesRead = localFile.length();
	                    localFile.seek(bytesRead);
	                    byte[] data = new byte[NO_BYTES_INTO_ONE_MB];
	                    while (true) {
	                        final int actual = responseStream != null ? responseStream.read(data, 0, data.length) : 0;
	                        if (actual > 0) {
	                            bytesRead += actual;
	                            localFile.write(data, 0, actual);
	                            if (downloadResourceExtensions.get(downloadResourceIndex).equals(".skm")) {
	                                // notify the UI about progress (in case of the
	                                // SKM download resource)
	                                long currentTime = System.currentTimeMillis();
	                                if (currentTime - lastProgressUpdateTime > 100) {
	                                   //YD updateDownloadProgress(bytesRead, dowloadPackage.getSize());
	                                    lastProgressUpdateTime = currentTime;
	                                }
	                            }
	                        } else {
	                            break;
	                        }
	                    }
	                    localFile.close();
	                    // notify that the download was finished
	                    updateOnFinishDownload();
	                } catch (ClientProtocolException e) {
	                    e.printStackTrace();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        };
	        // start downloading in the thread
	        downloadThread.start();
	    }
	    
	    /**
	     * Update the progress bar to show the progress of the SKM resource download
	     * @param downloadedSize size downloaded so far
	     * @param totalSize total size of the resource
	     */
	    /*private void updateDownloadProgress(final long downloadedSize, final long totalSize) {
	    	activityname.runOnUiThread(new Runnable() {
	            
	            @Override
	            public void run() {
	                int progress = (int) (progressBar.getMax() * ((float) downloadedSize / totalSize));
	                progressBar.setProgress(progress);
	                downloadPercentage.setText(((float) progress / 10) + "%");
	            }
	        });
	    }YD*/
	    
	    /**
	     * Update when a resource download was completed
	     */
	    private void updateOnFinishDownload() {
	    	/*activityname.runOnUiThread(new Runnable() {
	            
	            @Override
	            public void run() {
	                progressBar.setProgress(progressBar.getMax());
	                if (downloadResourceExtensions.get(downloadResourceIndex).equals(".skm")) {
	                    downloadPercentage.setText("100%");
	                    Toast.makeText(activityname.getApplicationContext(), "download map completed", 0).show();
	                } YD*/
	                if (downloadResourceIndex >= downloadResourceUrls.size() - 1) {
	                    // if the download of the last resource was completed -
	                    // install the package
						// at this point the downloaded package should be available
	                    // offline
	                    /*Toast.makeText(activityname,
	                            "Map of " + dowloadPackage + " is now available offline", Toast.LENGTH_SHORT).show(); YD*/
	                } else {
	                    // start downloading the next queued resource from the
	                    // package
	                    downloadResourceIndex++;
	                    downloadResource(downloadResourceUrls.get(downloadResourceIndex),
	                            downloadResourceExtensions.get(downloadResourceIndex));
	                }
	            /*}
	        });YD*/
	    }
    
    
    
   private List<DownloadPackage> searchByParentCode(String parentCode) {
        Collection<DownloadPackage> packages = application.getPackageMap().values();
        List<DownloadPackage> results = new ArrayList<DownloadPackage>();
        for (DownloadPackage pack : packages) {
                results.add(pack);
        }
        return results;
    }
   
   // Yash

}
