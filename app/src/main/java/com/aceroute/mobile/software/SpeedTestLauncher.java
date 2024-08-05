/*
	This file is part of SpeedTest.

    SpeedTest is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SpeedTest is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SpeedTest.  If not, see <http://www.gnu.org/licenses/>.

 */
package com.aceroute.mobile.software;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.aceroute.mobile.software.async.RespCBandServST;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

public class SpeedTestLauncher {

	RespCBandServST responseObj;
	int requestId;
	public double bindListeners(RespCBandServST res, int internetSpeedRes) {
		responseObj = res;
		requestId = internetSpeedRes;
		final Thread t1 = new Thread(mWorker);
		t1.start();
		return 22;
	}



	private final Handler mHandler=new Handler(){
		@Override
		public void handleMessage(final Message msg) {
			switch(msg.what){
			case MSG_UPDATE_STATUS:
				final SpeedInfo info1=(SpeedInfo) msg.obj;
				break;
			case MSG_UPDATE_CONNECTION_TIME:
				break;
			case MSG_COMPLETE_STATUS:
				final  SpeedInfo info2=(SpeedInfo) msg.obj;
				speedToReturn = info2.kilobits;

				responseObj.setResponseCallback(String.valueOf(speedToReturn), requestId);
				break;	
			default:
				super.handleMessage(msg);		
			}
		}
	};

	private final Runnable mWorker=new Runnable(){
		
		@Override
		public void run() {
			InputStream stream=null;
			try {
				int bytesIn=0;
				//String downloadFileUrl="http://portal.aceroute.com/images/nwtest.png";
				String downloadFileUrl="http://air.aceroute.com/images/nwtest.png";
				long startCon= System.currentTimeMillis();
				URL url=new URL(downloadFileUrl);
				URLConnection con=url.openConnection();
				//con.setConnectTimeout(500);
				con.setReadTimeout(500);
				con.setUseCaches(false);
				long connectionLatency= System.currentTimeMillis()- startCon;

				stream=con.getInputStream();
				
				Log.i("AceRoute", "Stream is :" + stream);
				Message msgUpdateConnection= Message.obtain(mHandler, MSG_UPDATE_CONNECTION_TIME);
				msgUpdateConnection.arg1=(int) connectionLatency;
				mHandler.sendMessage(msgUpdateConnection);

				long start= System.currentTimeMillis();
				int currentByte=0;
				long updateStart= System.currentTimeMillis();
				long updateDelta=0;
				int  bytesInThreshold=0;

				while((currentByte=stream.read())!=-1){	
					bytesIn++;
					bytesInThreshold++;
					if(updateDelta>=UPDATE_THRESHOLD){
						int progress=(int)((bytesIn/(double)EXPECTED_SIZE_IN_BYTES)*100);
						Message msg= Message.obtain(mHandler, MSG_UPDATE_STATUS, calculate(updateDelta, bytesInThreshold));
						msg.arg1=progress;
						msg.arg2=bytesIn;
						mHandler.sendMessage(msg);
						//Reset
						updateStart= System.currentTimeMillis();
						bytesInThreshold=0;
					}
					updateDelta = System.currentTimeMillis()- updateStart;
				}

				long downloadTime=(System.currentTimeMillis()-start);
				//Prevent AritchmeticException
				if(downloadTime==0){
					downloadTime=1;
				}

				Message msg= Message.obtain(mHandler, MSG_COMPLETE_STATUS, calculate(downloadTime, bytesIn));
				msg.arg1=bytesIn;
				speedToReturn= ((SpeedInfo)msg.obj).kilobits;
				mHandler.sendMessage(msg);
			} 
			catch (MalformedURLException e) {
				e.printStackTrace();
				//Log.e(TAG, e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
					//Log.e(TAG, e.getMessage());
			}
			catch (Exception e) {
				e.printStackTrace();
				//Log.e(TAG, e.getMessage());
			}finally{
				try {
					if(stream!=null){
						stream.close();
					}
				} catch (IOException e) {
					//Suppressed
				}
			}

		}
	};

	/**
	 * Get Network type from download rate
	 * @return 0 for Edge and 1 for 3G
	 */
	private int networkType(final double kbps){
		int type=1;//3G
		//Check if its EDGE
		if(kbps<EDGE_THRESHOLD){
			type=0;
		}
		return type;
	}

	/**
	 * 	
	 * 1 byte = 0.0078125 kilobits
	 * 1 kilobits = 0.0009765625 megabit
	 * 
	 * @param downloadTime in miliseconds
	 * @param bytesIn number of bytes downloaded
	 * @return SpeedInfo containing current speed
	 */
	private SpeedInfo calculate(final long downloadTime, final long bytesIn){
		SpeedInfo info=new SpeedInfo();
		//from mil to sec
		long bytespersecond   =(bytesIn / downloadTime) * 1000;
		double kilobits=bytespersecond * BYTE_TO_KILOBIT;
		double megabits=kilobits  * KILOBIT_TO_MEGABIT;
		info.downspeed=bytespersecond;
		info.kilobits=kilobits;
		info.megabits=megabits;

		return info;
	}

	/**
	 * Transfer Object
	 * @author devil
	 *
	 */
	private static class SpeedInfo{
		public double kilobits=0;
		public double megabits=0;
		public double downspeed=0;		
	}


	//Private fields	
	private static final String TAG = SpeedTestLauncher.class.getSimpleName();
	private static final int EXPECTED_SIZE_IN_BYTES = 1048576;//1MB 1024*1024

	private static final double EDGE_THRESHOLD = 176.0;
	private static final double BYTE_TO_KILOBIT = 0.0078125;
	private static final double KILOBIT_TO_MEGABIT = 0.0009765625;

	double speedToReturn;

	private final int MSG_UPDATE_STATUS=0;
	private final int MSG_UPDATE_CONNECTION_TIME=1;
	private final int MSG_COMPLETE_STATUS=2;

	private final static int UPDATE_THRESHOLD=300;


	private DecimalFormat mDecimalFormater;

}