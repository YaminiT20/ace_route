package com.aceroute.mobile.software.offline;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.aceroute.mobile.software.AceRouteApplication;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.offline.CityList_holder.customButtonListener;
import com.aceroute.mobile.software.offline.application.DownloadPackage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class CitiesList extends Activity implements
        customButtonListener {  
  
	
	private List<DownloadPackage> currentPackages;
	private AceRouteApplication application;
	DownloadPackage currentPackage=null;
	HashMap<String , HashMap<String, Integer>> cityname;
	HashMap<String ,Integer> localMap;
	ArrayList<String> cityCode;
	String installPackage[];
	Thread downlod;
	
	private BlockingQueue<String> messageQueueMap;
	
    private ListView listView;
    ListAdapter adapter;
    ArrayList<String> dataItems = new ArrayList<String>();
    String[] dataarray={"manoj","vipin","puneet","abccc","tyrrr","parth","rahul","yash bhai","pramod sir","himashu sir","sahil bhai","m bhai","n bhai","b bhai","v bhai","c bhai","x bhai","l bhai","j bhai","h bhai","g bhai","f bhai","d bhai","s bhai","a bhai"};
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.citieslist_layout); 
       // String[] dataArray = getResources().getStringArray(R.array.listdata);
        cityname = new HashMap<String, HashMap<String, Integer>>();
        listView = (ListView) findViewById(R.id.li_view);
        messageQueueMap = new ArrayBlockingQueue<String>(10);
         
        application = AceRouteApplication.getInstance();
        
        if (application.getPackageMap() != null) {
            // map packages are already available
        	currentPackages = searchByParentCode(null);
            initialMethod();
//            checkInstalledPackages();
            setAdaptorForData();
        } else {
            // map packages need to be obtained from parsing the Maps.xml file
            downlod = new Thread() {
                
                public void run() {
                    // after parsing display the highest level download packages
                    currentPackages = searchByParentCode(null);
                   /* activity.runOnUiThread(new Runnable() {
                        
                        @Override
                        public void run() {YD*/
                            initialMethod();
                            /*if(datamatch){
                            	datamatch=false;
                            packagesPath = application.getMapResourcesDirPath() + "/Maps/downloads/";
                            dowloadPackage = application.getPackageMap().get(currentPackage.getCode());
                            prepareDownloadResources();
                            }*/
                        /*}
						
                    });YD*/
                            try {
								messageQueueMap.put("DoNow");
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
                           
                }
            };
            downlod.start();
            
            try {
				String msg = messageQueueMap.poll((long) 18 * 60 * 1000, TimeUnit.MILLISECONDS);
				if (msg.equals("DoNow"))
				{
//					checkInstalledPackages();
		            setAdaptorForData();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            
            
       }
        
       /* List<String> dataTemp = Arrays.asList(dataarray);  
        dataItems.addAll(dataTemp);  
        listView = (ListView) findViewById(R.id.li_view);  
        adapter = new CityList_holder(CitiesList.this, dataItems);  
        ((CityList_holder) adapter).setCustomButtonListner(CitiesList.this);  
        listView.setAdapter(adapter);  */
        
        	 
  
    }  
    
    
    
    private void setAdaptorForData() {
    	    	
    	for (int i =0 ; i<installPackage.length;i++)
    	{
    		if (cityname.containsKey(installPackage[i]))
    		{
    			String nameofcity = cityname.get(installPackage[i]).keySet().toString();
    			nameofcity=nameofcity.substring(1,nameofcity.length()-1);
    			cityname.get(installPackage[i]).remove(nameofcity);
    			cityname.get(installPackage[i]).put(nameofcity, 1);
    		}
    	}
    	
    	 adapter = new CityList_holder(CitiesList.this, cityname);  
         ((CityList_holder) adapter).setCustomButtonListner(CitiesList.this);  
         listView.setAdapter(adapter); 
    }
    
    
//    public void checkInstalledPackages()
//	{
//		SKPackage[] packages = SKPackageManager.getInstance().getInstalledPackages();
//		if (packages.length>0)
//			installPackage = new String[packages.length];
//			for (int i =0 ; i <packages.length; i++){
//			installPackage[i] = packages[i].getName();
//
//			}
//		// uninstall and delete the package from the disk
//		//SKPackageManager.getInstance().deleteOfflinePackage(firstPackage.getName());
//	}
    
    private List<DownloadPackage> searchByParentCode(String parentCode) {
        Collection<DownloadPackage> packages = application.getPackageMap().values();
        List<DownloadPackage> results = new ArrayList<DownloadPackage>();
        for (DownloadPackage pack : packages) {
                results.add(pack);
        }
        return results;
    }
    private void initialMethod() {
		// TODO Auto-generated method stub
    	for(int i=0;i<currentPackages.size();i++){
            currentPackage = currentPackages.get(i);
            //cityname.put(currentPackage.getName(),0);
            localMap = new HashMap<String, Integer>();
            localMap.put(currentPackage.getName(),0);
            cityname.put(currentPackage.getCode(),localMap);
    	}
	}
    
    @Override
    public void onButtonClickListner(int position, String value) {
        Toast.makeText(CitiesList.this, "Button click " + value,
                Toast.LENGTH_SHORT).show();
        MapPackagesListActivity downloadmap = new MapPackagesListActivity();
		//downloadmap.methoddata(getApplicationContext(),value);
  
    } 
  
}  