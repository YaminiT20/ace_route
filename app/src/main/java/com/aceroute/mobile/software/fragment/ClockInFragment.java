package com.aceroute.mobile.software.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.reference.CheckinOut;
import com.aceroute.mobile.software.dialog.TypeFaceFont;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.requests.ClockInOutRequest;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.ServiceError;


public class ClockInFragment extends BaseFragment implements RespCBandServST{
	
	private int CHECKIN_REQ=1;
	private int CHECKOUT_REQ=2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View v = inflater.inflate(R.layout.fragment_clockin, null);
		mActivity.popFragments(mActivity.UI_Thread);
		TypeFaceFont.overrideFonts(mActivity, v);
		return null;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		if (PreferenceHandler.getClockInStat(mActivity)!=null && PreferenceHandler.getClockInStat(mActivity).equals(CheckinOut.CLOCK_IN))
		{
			ClockInOutRequest clkInOut = new ClockInOutRequest();
			clkInOut.setTid("0");
			clkInOut.setType("post");
			clkInOut.setAction(CheckinOut.CC_ACTION);
			
			CheckinOut.getData(clkInOut, mActivity, this, CHECKOUT_REQ);
		}
		else 
		{
			// Request for checkin
			ClockInOutRequest clkInOut = new ClockInOutRequest();
			clkInOut.setTid("1");
			clkInOut.setType("post");
			clkInOut.setAction(CheckinOut.CC_ACTION);
			
			CheckinOut.getData(clkInOut, mActivity, this, CHECKIN_REQ);
		}
		
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
				if (response.getId()==CHECKIN_REQ)
				{
					PreferenceHandler.setClockInStat(mActivity,"1");
					mActivity.popFragments(mActivity.SERVICE_Thread);
				}
				else if (response.getId()==CHECKOUT_REQ)
				{
					PreferenceHandler.setClockInStat(mActivity,"0");
					mActivity.popFragments(mActivity.SERVICE_Thread);
				}
			}
			else if(response.getStatus().equals("success")&& 
					response.getErrorcode().equals(ServiceError.getEnumValstr(ServiceError.NO_DATA)))
			{
				
			}
		}	
		
	}

	public void loadDataOnBack(BaseTabActivity mActivity) {
		
		if (PreferenceHandler.getClockInStat(mActivity)!=null && PreferenceHandler.getClockInStat(mActivity).equals(CheckinOut.CLOCK_IN))
		{
			ClockInOutRequest clkInOut = new ClockInOutRequest();
			clkInOut.setTid("0");
			clkInOut.setType("post");
			clkInOut.setAction(CheckinOut.CC_ACTION);
			
			CheckinOut.getData(clkInOut, mActivity, this, CHECKOUT_REQ);
		}
		else 
		{
			// Request for checkin
			ClockInOutRequest clkInOut = new ClockInOutRequest();
			clkInOut.setTid("1");
			clkInOut.setType("post");
			clkInOut.setAction(CheckinOut.CC_ACTION);
			
			CheckinOut.getData(clkInOut, mActivity, this, CHECKIN_REQ);
		}
		
	}

}
