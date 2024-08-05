package com.aceroute.mobile.software.adaptor;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.reference.CustomerContact;
import com.aceroute.mobile.software.component.reference.SegmentModel;
import com.aceroute.mobile.software.component.reference.Site;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.fragment.CustomerDetailFragment;
import com.aceroute.mobile.software.utilities.PreferenceHandler;
import com.aceroute.mobile.software.utilities.Utilities;

import java.util.ArrayList;


public class
DirectorySwipeAdapter extends BaseAdapter {
	LayoutInflater mInflater;
	Context mContext;
	 ArrayList<CustomerDetailFragment.Items> objListMap;
	CustomerDetailFragment classobj;
	Order activeOrderObj;
	ArrayList<String> arrLstPhoneType;;
	public DirectorySwipeAdapter(Context context, ArrayList<CustomerDetailFragment.Items> objListMap, CustomerDetailFragment obj, Order activeOrderObj,ArrayList<String> arrLstPhoneType) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		this.objListMap = objListMap;
		this.classobj=obj;
		this.activeOrderObj= activeOrderObj;
		this.arrLstPhoneType=arrLstPhoneType;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return objListMap.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return objListMap.get(position);
	}

	@Override
	public long getItemId(int position) {
		CustomerDetailFragment.Items item= objListMap.get(position);
		if(item.isSwipeEnable){
			return 0;
		}
		else
		{
			return -1;
		}
	}

	class ViewHolder {
		public RelativeLayout relative;
		TextView cust_list_name,cust_list_detail,cust_list_cno,cust_list_call ,backview_del, backview_edit,address_name,address_site;
		ImageView cust_list_img;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		ViewHolder holder;

		if (convertView == null) {
			vi = mInflater.inflate(R.layout.layout_swipelist_directory, null);
			holder = new ViewHolder();
//			holder.address_name = (TextView) vi.findViewById(R.id.cust_list_name1);
//			holder.address_site = (TextView) vi.findViewById(R.id.cust_list_detail1);

			holder.cust_list_img = (ImageView) vi.findViewById(R.id.cust_list_img);
			holder.relative= (RelativeLayout)vi.findViewById(R.id.relative);
			holder.cust_list_name = (TextView) vi.findViewById(R.id.cust_list_name);
			holder.cust_list_detail = (TextView) vi.findViewById(R.id.cust_list_detail);
			holder.cust_list_cno = (TextView) vi.findViewById(R.id.cust_list_cno);
			holder.cust_list_call = (TextView) vi.findViewById(R.id.cust_list_call);
			holder.backview_edit = (TextView) vi.findViewById(R.id.back_view_dummy_dirc);
			holder.backview_del = (TextView) vi.findViewById(R.id.back_view_dummy1_dirc);
			holder.cust_list_name.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
			holder.cust_list_cno.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
			holder.cust_list_detail.setTextSize(22 + (PreferenceHandler.getCurrrentFontSzForApp(mContext)));
			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
			holder.cust_list_call.setVisibility(View.GONE);
			holder.cust_list_call.setOnClickListener(null);
			holder.cust_list_call.setTag(null);
		}

		CustomerDetailFragment.Items item= objListMap.get(position);

		if(item!=null && item.type!=null) {
			if (item.item instanceof Site) {
				Site custSite = (Site) item.item;

				Site cust = new Site();
//				holder.cust_list_name.setText(String.valueOf(custSite.getNm()));
//				holder.cust_list_detail.setText(String.valueOf(custSite.getDetail()));
				holder.cust_list_name.setText(String.valueOf(custSite.getNm()));
				holder.cust_list_detail.setText(String.valueOf(custSite.getAdr()));

				//YD adding adr and adr2 to make one common address
//				String streetAdr = String.valueOf(custSite.getAdr());
//				String suiteAdr = custSite.getAdr2();
//				if (suiteAdr!= null && suiteAdr.length()>0) {
//					if(streetAdr!= null && streetAdr.length()>0) {
//						streetAdr = suiteAdr.trim() + ", " + streetAdr.trim();
//					}else{
//						streetAdr = suiteAdr.trim();
//					}
//				}
	//			holder.cust_list_cno.setText(streetAdr.trim());

				if (activeOrderObj.getCustSiteId() != null && custSite.getId() == Long.valueOf(activeOrderObj.getCustSiteId())) {
					holder.cust_list_img.setImageResource(R.drawable.main_site);
				}else{
					holder.cust_list_img.setImageResource(R.drawable.sub_site);
				}
				/*if (item.type.equals(CustomerDetailFragment.Fix_Location)) {
					holder.cust_list_img.setImageResource(R.drawable.main_site);
				} else {
					holder.cust_list_img.setImageResource(R.drawable.sub_site);
				}*/

			}/* else if (item.type.equals(CustomerDetailFragment.Fix_Contact)) {
				holder.cust_list_call.setTag(activeOrderObj.getCustContactNumber());

				holder.cust_list_name.setText(String.valueOf(activeOrderObj.getCustContactName()));
				holder.cust_list_cno.setText(String.valueOf(activeOrderObj.getCustContactNumber()));

//				holder.address_name.setText(String.valueOf(activeOrderObj.getCustSiteStreeAdd()));
//				holder.address_site.setText(String.valueOf(activeOrderObj.getCustSiteSuiteAddress()));

				if (String.valueOf(activeOrderObj.getTelTypeId()) != null && String.valueOf(activeOrderObj.getTelTypeId()) != "" && activeOrderObj.getTelTypeId() != -1) {
					holder.cust_list_detail.setText(String.valueOf(arrLstPhoneType.get((int) activeOrderObj.getTelTypeId())));
					holder.cust_list_detail.setTag(activeOrderObj);
				}
				holder.cust_list_call.setVisibility(View.VISIBLE);
				holder.cust_list_img.setImageResource(R.drawable.main_contact_edit);

				holder.cust_list_call.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (Utilities.isSimExists(mContext)) {
							String cust_contact_number = String.valueOf(v.getTag());
							if (!cust_contact_number.equals("") || cust_contact_number != null) {
								Intent dial = new Intent(Intent.ACTION_CALL);
								dial.setData(Uri.parse("tel:" + cust_contact_number.trim()));
								mContext.startActivity(dial);
							} else
								classobj.ShowMessageDialog(mContext.getResources().getString(R.string.msg_contact_number));
						}
						else{
							classobj.ShowMessageDialog(mContext.getResources().getString(R.string.msg_sim_ntwrk));
						}
					}
				});

			}*/
			else if (item.type.equals(CustomerDetailFragment.Fix_Non_Contact)) {
				CustomerContact custCont = (CustomerContact) item.item;
				holder.cust_list_call.setTag(custCont.getContacttel());
				holder.cust_list_cno.setText(String.valueOf(custCont.getContacttel()));
				holder.cust_list_name.setText(String.valueOf(custCont.getContactname()));
				holder.cust_list_detail.setText(arrLstPhoneType.get((int) custCont.getContactType()));

				if(activeOrderObj.getContactId() == custCont.getId()) {
					holder.cust_list_img.setImageResource(R.drawable.main_contact_edit);
				}else{
					holder.cust_list_img.setImageResource(R.drawable.sub_contact);
				}


				holder.cust_list_call.setVisibility(View.VISIBLE);
				holder.cust_list_call.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						if (Utilities.isSimExists(mContext)) {
							String cust_contact_number = String.valueOf(v.getTag());
							if (!cust_contact_number.equals("") || cust_contact_number != null) {
								Intent dial = new Intent(Intent.ACTION_CALL);
								dial.setData(Uri.parse("tel:" + cust_contact_number.trim()));
								mContext.startActivity(dial);
							} else
								classobj.ShowMessageDialog(mContext.getResources().getString(R.string.msg_contact_number));
						} else {
							classobj.ShowMessageDialog(mContext.getResources().getString(R.string.msg_sim_ntwrk));
						}
					}
				});


			}

			else if (item.type.equals(CustomerDetailFragment.Fix_Segment)) {
				final SiteType custCont = (SiteType) item.item;
				holder.cust_list_name.setText(String.valueOf(custCont.getNm()));
				holder.cust_list_detail.setVisibility(View.GONE);
				//holder.cust_list_detail.setText(String.valueOf(custCont.getCap()));
				holder.cust_list_cno.setText(String.valueOf(custCont.getCap()));


				holder.cust_list_call.setVisibility(View.VISIBLE);
				holder.cust_list_call.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (Utilities.isSimExists(mContext)) {
							String cust_contact_number = String.valueOf(custCont.getCap());
							if (!cust_contact_number.equals("") || cust_contact_number != null) {
								Intent dial = new Intent(Intent.ACTION_CALL);
								dial.setData(Uri.parse("tel:" + cust_contact_number.trim()));
								mContext.startActivity(dial);
							} else
								classobj.ShowMessageDialog(mContext.getResources().getString(R.string.msg_contact_number));
						} else {
							classobj.ShowMessageDialog(mContext.getResources().getString(R.string.msg_sim_ntwrk));
						}
					}
				});

				holder.cust_list_img.setImageResource(R.drawable.segment_h);
				item.isSwipeEnable = false;
			}

			holder.cust_list_img.invalidate();
		}



		return vi;
	}
public boolean isSwipeEnabledForViewPosition(int position){
	CustomerDetailFragment.Items item= objListMap.get(position);
	if(item.type.equals(CustomerDetailFragment.Fix_Segment)){
		return false;
	}
	else
		return true;
}



}
