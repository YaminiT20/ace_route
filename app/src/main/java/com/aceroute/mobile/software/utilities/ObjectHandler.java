package com.aceroute.mobile.software.utilities;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.aceroute.mobile.software.SplashII;
import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderMedia;
import com.aceroute.mobile.software.component.OrderNotes;
import com.aceroute.mobile.software.component.OrderPart;
import com.aceroute.mobile.software.component.OrderTask;
import com.aceroute.mobile.software.component.reference.Assets;
import com.aceroute.mobile.software.component.reference.AssetsType;
import com.aceroute.mobile.software.component.reference.ClientLocation;
import com.aceroute.mobile.software.component.reference.ClientSite;
import com.aceroute.mobile.software.component.reference.Customer;
import com.aceroute.mobile.software.component.reference.CustomerContact;
import com.aceroute.mobile.software.component.reference.CustomerType;
import com.aceroute.mobile.software.component.reference.Form;
import com.aceroute.mobile.software.component.reference.OrderStatus;
import com.aceroute.mobile.software.component.reference.OrderTypeList;
import com.aceroute.mobile.software.component.reference.Parts;
import com.aceroute.mobile.software.component.reference.ServiceType;
import com.aceroute.mobile.software.component.reference.Shifts;
import com.aceroute.mobile.software.component.reference.Site;
import com.aceroute.mobile.software.component.reference.SiteType;
import com.aceroute.mobile.software.component.reference.Worker;
import com.aceroute.mobile.software.database.DBHandler;
import com.aceroute.mobile.software.http.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ObjectHandler {
	
	
	public static HashMap<Long, Object> createObjectFromDB(Cursor cursor, String action,
			Context context, int type) {
		HashMap<Long, Object> xml = null;
		if (action.equals(CustomerType.ACTION_CUST_TYPE)) {
			xml = createObjectForCusType(cursor, context, type);
			return xml;
		} else if (action.equals(ClientLocation.ACTION_CLIENT_LOCATION)) {
			xml = createObjectForClientLocation(cursor, context, type);
			return xml;
		} else if (action.equals(Customer.ACTION_CUSTOMER_LIST)) {
			xml = createObjectForCustomerList(cursor, context, type);
			return xml;
		} else if (action.equals(Parts.ACTION_PART_TYPE)) {
			xml = createObjectForPartType(cursor, context, type);
			return xml;
		} else if (action.equals(Worker.ACTION_WORKER_LIST)) {
			xml = createObjectForWorkerList(cursor, context, type);
			return xml;
		} else if (action.equals(ServiceType.ACTION_SERVICE_TYPE)) {
			xml = createObjectForServiceType(cursor, context, type);
			return xml;
		} else if (action.equals(OrderTypeList.ACTION_ORDER_TYPE)) {
			xml = createObjectForOrderType(cursor, context, type);
			return xml;
		} else if (action.equals(Site.ACTION_SITE)) {
			xml = createObjectForSite(cursor, context, type);
			return xml;
		}else if (action.equals(SiteType.ACTION_SITE_TYPE)) {
			xml = createObjectForSiteType(cursor, context, type);
			return xml;
		}else if (action.equals(Order.ACTION_ORDER)) {
			xml = createObjectForOrder(cursor, context, type);//need for map
			return xml;
		}  else if (action.equals(OrderTask.ACTION_GET_ORDER_TASK)) {//need for map
			xml = createObjectForOrderTask(cursor, context, type);
			return xml;
		} else if (action.equals(OrderPart.ACTION_GET_ORDER_PART)) {//need for map
			xml = createObjectForOrderPart(cursor, context, type);
			return xml;
		} else if (action.equals(OrderMedia.ACTION_GET_MEDIA)) {//need for map
			xml = createObjectForOrderMedia(cursor, context, type);
		//	Log.d("TAG77",xml.toString());
			return xml;
		} else if (action.equals(OrderNotes.ACTION_GET_NOTES)) {//need for map
			xml = createObjectForOrderNotes(cursor, context, type);
			return xml;
		} else if (action.equals(CustomerContact.ACTION_GET_CONTACT)){
			xml = createObjectForCustContact(cursor, context, type);
			return xml;
		}else if (action.equals(OrderStatus.ACTION_GET_STATUS)){
			xml = createObjectForOrderStatus(cursor, context, type);
			return xml;
		}else if (action.equals(AssetsType.ACTION_GET_ASSETS_TYPE)){
			xml = createObjectForAssetType(cursor, context, type);
			return xml;
		}
		else if (action.equals(Assets.ACTION_GET_ASSETS)){
			xml = createObjectForAsset(cursor, context, type);
			return xml;
		} else if (action.equals(ClientSite.ACTION_GET_CLIENT_SITE)) {
			xml = createObjectForClientSite(cursor, context, type);
			return xml;
		}else if (action.equals(Shifts.ACTION_GET_SHIFTS)) {
			xml = createObjectForShift(cursor, context, type);
			return xml;
		}else if(action.equals(Form.ACTION_GET_ORDER_FORM)){
			xml= createObjectForForm(cursor,context,type);
			return xml;
		}

		return null;
	}

	public static HashMap<Long, Object> createMapFromObjects(Object[] object, String action,
			Context context) {
		HashMap<Long, Object> xml = null;
		
		if (action.equals(OrderTask.ACTION_GET_ORDER_TASK)) {
			xml = createMapForOrderTaskfromObject(object, context);
			return xml;
		} else
		if (action.equals(OrderPart.ACTION_GET_ORDER_PART)) {
			xml = createMapForOrderPartfromObject(object, context);
			return xml;
		}if (action.equals(Form.ACTION_GET_ORDER_FORM)) {
            xml = createMapForOrderFormfromObject(object, context);
            return xml;
        } if (action.equals(Order.ACTION_ORDER)) {
			xml = createMapForOrderfromObject(object, context);
			return xml;
		}
		if (action.equals(OrderMedia.ACTION_GET_MEDIA)) {
			xml = createMapForOrderMediafromObject(object, context);
	//		Log.d("TAG77",xml.toString());
			return xml;
		}
		if (action.equals(Form.ACTION_SAVE_FORM)) {
			xml = createMapForFormfromObject(object, context);
		//	Log.d("TAG77",xml.toString());
			return xml;
		}

		
	/*	if (action.equals(CustomerType.ACTION_CUST_TYPE)) {
			xml = createXMLForCusType(cursor, context);
			return xml;
		} else if (action.equals(ClientLocation.ACTION_CLIENT_LOCATION)) {
			xml = createXMLForClientLocation(cursor, context);
			return xml;
		} else if (action.equals(Customer.ACTION_CUSTOMER_LIST)) {
			xml = createXMLForCustomerList(cursor, context);
			return xml;
		} else if (action.equals(Order.ACTION_ORDER)) {
			xml = createXMLForOrder(cursor, context);
			return xml;
		} else if (action.equals(Parts.ACTION_PART_TYPE)) {
			xml = createXMLForPartType(cursor, context);
			return xml;
		} else if (action.equals(Worker.ACTION_WORKER_LIST)) {
			xml = createXMLForWorkerList(cursor, context);
			return xml;
		} else if (action.equals(ServiceType.ACTION_SERVICE_TYPE)) {
			xml = createXMLForServiceType(cursor, context);
			return xml;
		} else if (action.equals(OrderTypeList.ACTION_ORDER_TYPE)) {
			xml = createXMLForOrderType(cursor, context);
			return xml;
		} else if (action.equals(OrderTask.ACTION_GET_ORDER_TASK)) {
			xml = createXMLForOrderTask(cursor, context);
			return xml;
		} else if (action.equals(OrderPart.ACTION_GET_ORDER_PART)) {
			xml = createXMLForOrderPart(cursor, context);
			return xml;
		} else if (action.equals(OrderMedia.ACTION_GET_MEDIA)) {
			xml = createXMLForOrderMedia(cursor, context);
			return xml;
		} else if (action.equals(OrderNotes.ACTION_GET_NOTES)) {
			xml = createXMLForOrderNotes(cursor, context);
			return xml;
		} else if (action.equals(Site.ACTION_SITE)) {
			xml = createXMLForSite(cursor, context);
			return xml;
		}*/
		return null;
	}
	
	private static HashMap<Long, Object> createObjectForCustomerList(Cursor cursor,
			Context context, int type) {
		try {
			if (cursor.moveToFirst()) {
				HashMap<Long, Object> custList = new HashMap<Long, Object>();
				do {
					Customer custLstObj = new Customer();
					
					custLstObj.setId(cursor.getLong(0));
					custLstObj.setTid(cursor.getLong(1));
					custLstObj.setCustomerType(cursor.getString(2));
					custLstObj.setNm(cursor.getString(3));
					
					custList.put(cursor.getLong(0),custLstObj);
					
				} while (type == DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());


				return custList;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}



	private static HashMap<Long, Object> createObjectForSiteType(Cursor cursor,
			Context context, int type) {
		try {
			if (cursor.moveToFirst()) {
				HashMap<Long, Object> siteTypeList = new HashMap<Long, Object>();
				do {
					SiteType siteTypeObj = new SiteType();
					siteTypeObj.setId(cursor.getLong(0));
					siteTypeObj.setNm(cursor.getString(1));
					siteTypeObj.setTid(Integer.valueOf(cursor.getString(2)));
					siteTypeObj.setUpd(Long.valueOf(cursor.getString(3)));
					siteTypeObj.setDtl(cursor.getString(4));
					siteTypeObj.setCap(cursor.getString(5));
					siteTypeList.put(cursor.getLong(0),siteTypeObj);
				} while (type ==DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());


				return siteTypeList;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}



	private static HashMap<Long, Object> createObjectForSite(Cursor cursor,
			Context context, int type) {
		try {
			if (cursor.moveToFirst()) {
				HashMap<Long, Object> siteList = new HashMap<Long, Object>();
				do {
					Site siteObj = new Site();
					
					siteObj.setId(cursor.getLong(0));
					siteObj.setCid(cursor.getLong(1));
					siteObj.setNm(cursor.getString(2));
					siteObj.setAdr(cursor.getString(3));
					siteObj.setAdr2(cursor.getString(4));
					siteObj.setUpd(Long.parseLong(cursor.getString(8)));
					if (cursor.getString(9)!=null)
						siteObj.setDetail(cursor.getString(9));
					else
						siteObj.setDetail("");
					
					if (cursor.getString(10)!=null)
						siteObj.setTid(Long.parseLong(cursor.getString(10)));
					else
						siteObj.setTid(-1);
					
					if (cursor.getString(11)!=null)
						siteObj.setSitetypenm(cursor.getString(11));
					else
						siteObj.setSitetypenm("");
					
					if (cursor.getString(6)!=null)
						siteObj.setGeo(cursor.getString(6));
					else
						siteObj.setGeo("");
					
					
					siteList.put(cursor.getLong(0),siteObj);
					
				} while (type ==DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());


				return siteList;
				// TODO return the original xml. Below is temp xml.
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}



	private static HashMap<Long, Object> createObjectForOrderNotes(Cursor cursor,
			Context context, int type) {
		try {
			if (cursor.moveToFirst()) {
				HashMap<Long, Object> noteList = new HashMap<Long, Object>();
				do {
					OrderNotes noteObj = new OrderNotes();
					//xml.append("<id>");
					//xml.append(""+cursor.getInt(0));
					//xml.append("</id>");
					//xml.append("<nm>"+OrderNotes.ORDER_NOTE_FIELDNAME+"</nm>");
					//xml.append("<value>"+cursor.getString(1)+"</value>");

					noteObj.setId(cursor.getLong(0));
					noteObj.setOrdernote(cursor.getString(1));
					noteList.put(cursor.getLong(0), noteObj);
					
				} while (type ==DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());
				return noteList;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			cursor.close();
		}
	}



	private static HashMap<Long, Object> createObjectForOrderMedia(Cursor cursor,
			Context context, int type) {
		String tempxml = "<data/>";
		try {
			if (cursor.moveToFirst()) {
				HashMap<Long, Object> mediaList = new HashMap<Long, Object>();
				do {
					OrderMedia mediaObj = new OrderMedia();
					
					mediaObj.setId(cursor.getLong(0));
					mediaObj.setOrderid(cursor.getLong(1));
					mediaObj.setMediatype(cursor.getInt(2));
					mediaObj.setGeocode(cursor.getString(3));
					mediaObj.setFile_desc(cursor.getString(4));
					mediaObj.setMimetype(cursor.getString(5));
					mediaObj.setFile(cursor.getString(6));
					mediaObj.setUpd_time(cursor.getLong(7));
					mediaObj.setMetapath(cursor.getString(9));
					if (cursor.getString(10)!=null)
						mediaObj.setLocalId(Long.valueOf(cursor.getString(10)));
					if (cursor.getString(11)!=null)
						mediaObj.setLocalOid(Long.valueOf(cursor.getString(11)));

					mediaObj.setFrmkey(cursor.getString(13));
					mediaObj.setFrmfiledid(cursor.getString(14));
				//	mediaObj.setFrmfiledid(cursor.getString(15));
					
					mediaList.put(cursor.getLong(0),mediaObj);
					
				} while (type ==DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());
				
				return mediaList;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			cursor.close();
		}
	}

	private static HashMap<Long, Object> createObjectForAssetType(Cursor cursor, Context context, int type) {
		try {
			HashMap<Long, Object> orderAssetTypeList = new HashMap<Long, Object>();
			if (cursor.moveToFirst()) {
				do {
					AssetsType assetType = new AssetsType();

					assetType.setId(cursor.getLong(0));
					assetType.setXid(cursor.getLong(1));
					assetType.setName(cursor.getString(2));
					assetType.setUpd(cursor.getLong(3));

					orderAssetTypeList.put(cursor.getLong(0),assetType);

				} while (type ==DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());

				return orderAssetTypeList;
				// TODO return the original xml. Below is temp xml.
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}

	private static HashMap<Long, Object> createObjectForAsset(Cursor cursor, Context context, int type) {
		try {
			HashMap<Long, Object> orderAssetList = new HashMap<Long, Object>();
			if (cursor.moveToFirst()) {
				do {
					Assets asset = new Assets();

					/*String que = "CREATE TABLE " + assestTable + "( " + Assets.ASSET_ID
							+ " INTEGER PRIMARY KEY, " + Assets.ASSET_CID + " INTEGER ,"
							+ Assets.ASSET_GEO + " TEXT ,"
							+ Assets.ASSET_CONT1 + " TEXT ,"
							+ Assets.ASSET_CONT2 + " TEXT ,"
							+ DBHandler.modifiedField + " TEXT ,"
							+ Assets.ASSET_UPD + " INTEGER ,"6
							+ Assets.ASSET_STAT + " TEXT ,"
							+ Assets.ASSET_TYPE_ID + " INTEGER,"
							+ Assets.ASSET_TYPE_ID2 + " INTEGER,"9
							+ Assets.ASSET_PID + " INTEGER,"
							+ Assets.ASSET_NUM1 + " INTEGER,"
							+ Assets.ASSET_NUM2 + " INTEGER,"
							+ Assets.ASSET_NUM3 + " INTEGER,"
							+ Assets.ASSET_NUM4 + " INTEGER,"
							+ Assets.ASSET_NUM5 + " INTEGER,"
							+ Assets.ASSET_NUM6 + " INTEGER,"
							+ Assets.ASSET_NOTE1 + " TEXT ,"
							+ Assets.ASSET_NOTE2 + " TEXT ,"
							+ Assets.ASSET_NOTE3 + " TEXT ,"
							+ Assets.ASSET_NOTE4 + " TEXT ,"
							+ Assets.ASSET_NOTE5 + " TEXT ,"
							+ Assets.ASSET_NOTE6 + " TEXT ,"
							+ Assets.ASSET_CT1 + " TEXT ,"
							+ Assets.ASSET_CT2 + " TEXT ,"
							+ Assets.ASSET_CT3 + " TEXT ,"
							+ localidField+ " INTEGER)";*/

					asset.setId(cursor.getLong(0));
					asset.setCid(cursor.getLong(1));
					asset.setGeoLoc(cursor.getString(2));
					asset.setFtid(cursor.getLong(5));
					asset.setFdata(cursor.getString(6));
					/*asset.setContact1(cursor.getLong(3));//YD 2020
					asset.setContact2(cursor.getLong(4));*/
					asset.setModify(cursor.getString(3));
					asset.setUpd(cursor.getLong(4));
					/*asset.setStatus(cursor.getString(7));//YD 2020
					asset.setTid(cursor.getLong(8));
					asset.setTid2(cursor.getLong(9));
					asset.setPid(cursor.getLong(10));
					asset.setNumber1(cursor.getLong(11));
					asset.setNumber2(cursor.getLong(12));
					asset.setNumber3(cursor.getLong(13));
					asset.setNumber4(cursor.getLong(14));
					asset.setNumber5(cursor.getLong(15));
					asset.setNumber6(cursor.getLong(16));
					asset.setNote1(cursor.getString(17));
					asset.setNote2(cursor.getString(18));
					asset.setNote3(cursor.getString(19));
					asset.setNote4(cursor.getString(20));
					asset.setNote5(cursor.getString(21));
					asset.setNote6(cursor.getString(22));
					asset.setCt1(cursor.getString(23));
					asset.setCt2(cursor.getString(24));
					asset.setCt3(cursor.getString(25));*/

					orderAssetList.put(cursor.getLong(0),asset);

				} while (type ==DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());

				return orderAssetList;
				// TODO return the original xml. Below is temp xml.
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}

	private static HashMap<Long, Object> createObjectForOrderPart(Cursor cursor,
			Context context, int type) {
		try {
			HashMap<Long, Object> orderPartList = new HashMap<Long, Object>();
			if (cursor.moveToFirst()) {
				do {
					OrderPart odrPart = new OrderPart();
					
					odrPart.setOid(cursor.getLong(0));
					odrPart.setOrder_part_id(cursor.getLong(1));
					odrPart.setPart_type_id(cursor.getLong(2));
					odrPart.setOrder_part_QTY(cursor.getString(3));

					odrPart.setPart_barcode(cursor.getString(4));
					odrPart.setUpd_time(cursor.getLong(5));


					orderPartList.put(cursor.getLong(1),odrPart);
					
				} while (type ==DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());

				return orderPartList;
				// TODO return the original xml. Below is temp xml.
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}


	private static HashMap<Long, Object> createObjectForCustContact(Cursor cursor,
																	Context context, int type) {
		try {
			HashMap<Long, Object>  custContactLst = new HashMap<Long, Object>();
			if (cursor.moveToFirst()) {
				do {
					CustomerContact custCont = new CustomerContact();

					custCont.setId(cursor.getLong(0));
					custCont.setCustomerid(cursor.getLong(1));
					custCont.setContacttel(cursor.getString(2));
					custCont.setContactname(cursor.getString(3));
					custCont.setContactType(cursor.getLong(4));
					custCont.setContactEml(cursor.getString(8));

					custContactLst.put(cursor.getLong(0),custCont);

				} while (type ==DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());

				return custContactLst;
				// TODO return the original xml. Below is temp xml.
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}

	private static HashMap<Long, Object> createObjectForOrderStatus(Cursor cursor,
																	Context context, int type) {
		try {
			HashMap<Long, Object>  orderStatLst = new HashMap<Long, Object>();
			if (cursor.moveToFirst()) {
				do {
					OrderStatus orderStat = new OrderStatus();

					orderStat.setId(cursor.getLong(0));
					orderStat.setIsgroup(cursor.getInt(1));
					orderStat.setGrpSeq(cursor.getInt(2));
					orderStat.setGrpId(cursor.getInt(3));
					orderStat.setSeq(cursor.getInt(4));
					orderStat.setNm(cursor.getString(5));
					orderStat.setAbbrevation(cursor.getString(6));
					orderStat.setIsVisible(cursor.getInt(7));

					orderStatLst.put(cursor.getLong(0),orderStat);

				} while (type ==DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());

				return orderStatLst;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}

	private static HashMap<Long, Object> createObjectForOrderTask(Cursor cursor,
			Context context, int type) {
		try {
			if (cursor.moveToFirst()) {
				HashMap<Long, Object> OrderTaskList = new HashMap<Long, Object>();
				do {
					OrderTask orderTaskObj = new OrderTask();
					orderTaskObj.setId(cursor.getLong(0));
					orderTaskObj.setOrder_task_id(cursor.getLong(1));
					orderTaskObj.setTask_id(cursor.getLong(2));
					orderTaskObj.setUpd_time(cursor.getLong(3));
					orderTaskObj.setTree_type(cursor.getString(5));// check for trim type
					orderTaskObj.setPriority(cursor.getString(6));
					orderTaskObj.setAction_status(cursor.getString(7));
					orderTaskObj.setTree_owner(cursor.getString(8));
					orderTaskObj.setTree_ht(cursor.getString(9));
					orderTaskObj.setTree_dia(cursor.getString(10));
					orderTaskObj.setTree_clearence(cursor.getString(11));
					orderTaskObj.setTree_cycle(cursor.getString(12));
					orderTaskObj.setTree_expcount(cursor.getString(13));
					orderTaskObj.setTree_actualcount(cursor.getString(14));
					orderTaskObj.setTree_timespent(cursor.getString(15));
					orderTaskObj.setTree_tm(cursor.getString(16));
					orderTaskObj.setTree_msc(cursor.getString(17));
					orderTaskObj.setTree_comment(cursor.getString(18));
					orderTaskObj.setTree_pcomment(cursor.getString(19));
					orderTaskObj.setTree_alert(cursor.getString(20));
					orderTaskObj.setTree_note(cursor.getString(21));
					orderTaskObj.setTree_geo(cursor.getString(22));
					orderTaskObj.setTree_accesspath(cursor.getString(23));
					if (cursor.getString(24)!= null && !cursor.getString(24).equals(""))
						orderTaskObj.setTree_ct1(cursor.getString(24));
					else 
						orderTaskObj.setTree_ct1("1");
					
					if (cursor.getString(25)!= null && !cursor.getString(25).equals(""))
						orderTaskObj.setTree_ct2(cursor.getString(25));
					else 
						orderTaskObj.setTree_ct2("1");
					
					if (cursor.getString(26)!= null && !cursor.getString(26).equals(""))
						orderTaskObj.setTree_ct3(cursor.getString(26));
					else 
						orderTaskObj.setTree_ct3("1");

					OrderTaskList.put(cursor.getLong(1),orderTaskObj);
					
				} while (type ==DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());


				return OrderTaskList;
				// TODO return the original xml. Below is temp xml.
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}



	private static HashMap<Long, Object> createObjectForOrderType(Cursor cursor,
			Context context, int type) {
		try {
			if (cursor.moveToFirst()) {
				HashMap<Long, Object> OrderTypeList = new HashMap<Long, Object>();
				do {
					OrderTypeList ordertypObj = new OrderTypeList();
					
					ordertypObj.setId(cursor.getLong(0));
					ordertypObj.setNm(cursor.getString(1));
					
					OrderTypeList.put(cursor.getLong(0),ordertypObj);
					
				} while (type ==DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());

				return OrderTypeList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
		
	}



	private static HashMap<Long, Object> createObjectForServiceType(Cursor cursor,
			Context context, int type) {
		try {
			if (cursor.moveToFirst()) {
				HashMap<Long, Object> taskTypeList = new HashMap<Long, Object>();
				do {
					ServiceType taskTypeObj = new ServiceType();
					taskTypeObj.setId(cursor.getLong(0));
					taskTypeObj.setNm(cursor.getString(1));
					taskTypeObj.setDtl(cursor.getString(2));
					
					taskTypeList.put(cursor.getLong(0),taskTypeObj);
					
				} while (type ==DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());

				return taskTypeList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}


	private static HashMap<Long, Object> createObjectForWorkerList(Cursor cursor,
			Context context, int type) {
		try {
			if (cursor.moveToFirst()) {
				HashMap<Long, Object> workersList = new HashMap<Long, Object>();
				do {
					Worker workerObj = new Worker();
					workerObj.setId(cursor.getLong(0));
					workerObj.setNm(cursor.getString(1));
					workerObj.setLid(cursor.getLong(2));
					workerObj.setWrkwk(cursor.getString(3));
					workerObj.setDwrkwk(cursor.getString(4));
					workerObj.setBrk(cursor.getString(5));
					workerObj.setWorkerShift(cursor.getInt(7));
					workerObj.setTid(cursor.getInt(8));
					workerObj.setVehicleId(cursor.getString(9));

					if(PreferenceHandler.getResId(context) == cursor.getLong(0)){
						SplashII.wrk_tid =cursor.getInt(8);
					}
					workersList.put(cursor.getLong(0),workerObj);
					
				} while (type ==DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());

				return workersList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}



	private static HashMap<Long, Object> createObjectForPartType(Cursor cursor,
			Context context, int type) {
		try{
			if (cursor.moveToFirst()) {
				HashMap<Long, Object> partTypeList = new HashMap<Long, Object>();
				do {
					Parts partTypeObj = new Parts();
					
					partTypeObj.setId(cursor.getLong(0));
					partTypeObj.setName(cursor.getString(1));
					partTypeObj.setDesc(cursor.getString(2));
					partTypeObj.setCtid(cursor.getString(3));
					partTypeList.put(cursor.getLong(0),partTypeObj);
					
				} while (type ==DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());

				return partTypeList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}



	private static HashMap<Long, Object> createObjectForClientLocation(Cursor cursor,
			Context context, int type) {
		// TODO Auto-generated method stub
		return null;
	}



	private static HashMap<Long, Object> createObjectForCusType(Cursor cursor, Context context,
			int type) {
		return null;
	}



	public static HashMap<Long, Object> createObjectForOrder(Cursor cursor, Context context2, int type) {//yash note
		try {
			if (cursor.moveToFirst()) {
				HashMap<Long, Object> orderList = new HashMap<Long, Object>();
				do {
					Order orderObj = new Order();
					orderObj.setId(cursor.getLong(0));
					orderObj.setFromDate(cursor.getString(1));
					orderObj.setToDate(cursor.getString(2));
					orderObj.setNm(cursor.getString(3));
					orderObj.setStatusId(Long.parseLong(cursor.getString(4)));
					
					if (cursor.getString(5)!=null || cursor.getString(5)!="")
						orderObj.setPoNumber(cursor.getString(5));
					else
						orderObj.setPoNumber("0");
					orderObj.setInvoiceNumber(cursor.getString(6));
					if (cursor.getString(7)!=null )
						orderObj.setOrderTypeId(Long.valueOf(cursor.getString(7)));

					if (cursor.getString(8)!=null )
						orderObj.setPriorityTypeId(Long.parseLong(cursor.getString(8)));
					orderObj.setPrimaryWorkerId(cursor.getString(9));
					orderObj.setSummary(cursor.getString(10));
					// xml.append("<note>" + cursor.getString(11) + "</note>");
					orderObj.setCustomerid(cursor.getLong(12));
					orderObj.setCustName(cursor.getString(13));
					orderObj.setCustSiteStreeAdd(cursor.getString(14));
					orderObj.setCustSiteSuiteAddress(cursor.getString(15));
					orderObj.setCustSiteGeocode(cursor.getString(16));
					orderObj.setCustContactName(cursor.getString(17));
					orderObj.setCustContactNumber(cursor.getString(18));
					orderObj.setCustPartCount(cursor.getLong(19));
					
					if (cursor.getLong(20)!=0 || cursor.getLong(20)!=-1)
						orderObj.setCustMetaCount(cursor.getLong(20));
					else
						orderObj.setCustMetaCount(0);
					
					/*if (cursor.getLong(21)!=0 || cursor.getLong(21)!=-1)
						orderObj.setCustServiceCount(cursor.getLong(21));
					else
						orderObj.setCustServiceCount(0);*/ //YD 2020 ordertask is not in app anymore
					
					orderObj.setEpochTime(cursor.getLong(22));
					orderObj.setStartTime(cursor.getLong(25));
					orderObj.setEndTime(cursor.getLong(27));
					orderObj.setCustSiteId(cursor.getString(26));
					orderObj.setFlg(cursor.getString(30));
					orderObj.setCustypename(cursor.getString(31));
					orderObj.setSitetypename(cursor.getString(32));
					if (cursor.getString(34)!= null)
						orderObj.setSigCount(Long.parseLong(cursor.getString(34)));
					else{
						orderObj.setSigCount(0L);
					}
					if (cursor.getString(35)!= null)
						orderObj.setAudioCount(Long.parseLong(cursor.getString(35)));
					else
						orderObj.setAudioCount(0L);
					
					if (cursor.getString(36)== null)
						orderObj.setImgCount(0);
					else

						orderObj.setImgCount(Long.parseLong(cursor.getString(36)));
					
					if (cursor.getString(37)== null)
						orderObj.setNotCount(0L);
					else
						orderObj.setNotCount(Long.parseLong(cursor.getString(37)));
					orderObj.setOrderAlert(cursor.getString(38));
					orderObj.setContactId(cursor.getLong(40));
					orderObj.setTelTypeId(cursor.getLong(41));

					if (cursor.getString(42)== null)
						orderObj.setDocCount(0);
					else
						orderObj.setDocCount(Long.parseLong(cursor.getString(42)));

					orderObj.setAssetsCount(cursor.getLong(43));
                    orderObj.setCustFormCount(cursor.getLong(44));
					orderObj.setCustids(cursor.getString(45));
					orderObj.setOrdeNotes(cursor.getString(cursor.getColumnIndex(Order.ORDER_NOTES)));
					
					orderList.put(cursor.getLong(0),orderObj);
		
				} while (type ==DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());

				return orderList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}



	private static HashMap<Long, Object> createObjectForClientSite(Cursor cursor,
																 Context context, int type) {
		try {
			if (cursor.moveToFirst()) {
				HashMap<Long, Object> clientsiteList = new HashMap<Long, Object>();
				do {
					ClientSite clientsite = new ClientSite();
					clientsite.setId(cursor.getLong(0));
					clientsite.setNm(cursor.getString(1));
					clientsite.setAdr(cursor.getString(2));
					clientsite.setAdr2(cursor.getString(3));
					clientsite.setTz(cursor.getString(4));
					clientsite.setGeo(cursor.getString(5));
					clientsite.setXid(cursor.getLong(6));
					clientsite.setUpd(cursor.getLong(7));
					clientsite.setBy(cursor.getString(8));

					clientsiteList.put(cursor.getLong(0),clientsite);
				} while (type ==DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());

				return clientsiteList;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}


	private static HashMap<Long, Object> createObjectForShift(Cursor cursor,
																   Context context, int type) {
		try {
			if (cursor.moveToFirst()) {
				HashMap<Long, Object> shiftList = new HashMap<Long, Object>();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				do {
					Shifts shift = new Shifts();
					shift.setId(cursor.getLong(0));
					shift.setTid(cursor.getLong(1));
					String dt = cursor.getString(4);
					if(dt!=null) {
						Date date=sdf.parse(dt);
						Long dttime = date.getTime();
						shift.setNm(cursor.getString(2));
						shift.setTmslot(cursor.getString(3));
						shift.setDt(cursor.getString(4));
						shift.setLid(cursor.getLong(5));
						shift.setModified(cursor.getString(6));
						shift.setLocalid(cursor.getLong(7));
						shift.setBrkSlot(cursor.getString(8));
						shift.setAddress(cursor.getString(9));
						shift.setTerri(cursor.getString(10));

						//shiftList.put(cursor.getLong(0),shift);

						ShiftDateModel dtmodel = (ShiftDateModel)shiftList.get(dttime);
						if(dtmodel!=null)
						{
							if(shift.tid == 1||shift.tid==2||shift.tid==0)
								dtmodel.setShiftlist(shift);
//								dtmodel.addUnShiftListObject(shift);
							else if (shift.tid == 3)
//								dtmodel.setShiftlist(shift);
								dtmodel.addUnShiftListObject(shift);
							else {
								dtmodel.setShiftlist(shift);
							}
						}else{
							ShiftDateModel smodel = new ShiftDateModel(date);
							if(shift.tid == 2||shift.tid==1||shift.tid==0)
								smodel.setShiftlist(shift);
//								smodel.addUnShiftListObject(shift);
							else if (shift.tid == 3)
								smodel.addUnShiftListObject(shift);
//								smodel.setShiftlist(shift);
							else {
								smodel.setShiftlist(shift);
							}
							shiftList.put(dttime,smodel);
						}
					}

				} while (type ==DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());

				return shiftList;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}


	private static HashMap<Long, Object> createObjectForForm(Cursor cursor,
																   Context context, int type) {
		try {
			if (cursor.moveToFirst()) {
				HashMap<Long, Object> formList = new HashMap<Long, Object>();
				do {
					Form form = new Form();
                    form.setOid(cursor.getLong(0));
					form.setId(cursor.getLong(1));
                    form.setFtid(cursor.getString(2));
					form.setFdata(cursor.getString(3));
                    form.setUpd(Long.valueOf(cursor.getString(4)));
					form.setFormkeyonly(cursor.getString(8));
					Log.d("TAG990",cursor.getString(8));
					formList.put(cursor.getLong(1),form);

				} while (type ==DBHandler.DB_GET_ONLY_CURRENT_RECORD?false:cursor.moveToNext());

				return formList;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return null;
	}




	public static Response getObjectForOrderStat_Prio(String type){//YD using when getting status from hardcoded string but currently using it for proirity
		Response responseObj = new Response();
		XMLHandler xmlHandler= new XMLHandler();
		HashMap<Long, Object> Stat_prioList=null;
		if (type.equals("status")){
			//Stat_prioList = xmlHandler.getOrderStatusValuesFromXML(getstatustype);//YD using when getting status from hardcoded string
		}
		if (type.equals("priority")){
			Stat_prioList = xmlHandler.getOrderPriorityValuesFromXML(getpririotytype);
		}
		
		
		if (Stat_prioList!=null){
			responseObj.setStatus("success");
			responseObj.setResponseMap(Stat_prioList);
			responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_ACTION_REQUIRED));
			return responseObj;
		}	
		else
		{
			responseObj.setStatus("success");
			responseObj.setResponseMap(Stat_prioList);
			responseObj.setErrorcode(ServiceError.getEnumValstr(ServiceError.NO_DATA));
			return responseObj;
		}
	}
	
	
	
	/*public static final String getpririotytype = "<data><orderpriority><orderpriority_id><![CDATA[1]]></orderpriority_id><orderpriority_name><![CDATA[Routine]]></orderpriority_name></orderpriority>" +
			"<orderpriority><orderpriority_id><![CDATA[2]]></orderpriority_id><orderpriority_name><![CDATA[Low]]></orderpriority_name></orderpriority>" +
			"<orderpriority><orderpriority_id><![CDATA[3]]></orderpriority_id><orderpriority_name><![CDATA[Medium]]></orderpriority_name></orderpriority>" +
			"<orderpriority><orderpriority_id><![CDATA[4]]></orderpriority_id><orderpriority_name><![CDATA[High]]></orderpriority_name></orderpriority>" +
			"<orderpriority><orderpriority_id><![CDATA[5]]></orderpriority_id><orderpriority_name><![CDATA[Accelerate]]></orderpriority_name></orderpriority>" +
			"<orderpriority><orderpriority_id><![CDATA[6]]></orderpriority_id><orderpriority_name><![CDATA[HN Urgent]]></orderpriority_name></orderpriority><orderpriority>" +
			"<orderpriority_id><![CDATA[7]]></orderpriority_id><orderpriority_name><![CDATA[HN Immediate]]></orderpriority_name></orderpriority></data>";*/
	/*
	public static final String getpririotytype =
			"<data><orderpriority><orderpriority_id><![CDATA[0]]></orderpriority_id><orderpriority_name><![CDATA[Low]]></orderpriority_name></orderpriority>" +
			"<orderpriority><orderpriority_id><![CDATA[1]]></orderpriority_id><orderpriority_name><![CDATA[Routine]]></orderpriority_name></orderpriority>" +
			"<orderpriority><orderpriority_id><![CDATA[4]]></orderpriority_id><orderpriority_name><![CDATA[Expedite]]></orderpriority_name></orderpriority>" +
			"<orderpriority><orderpriority_id><![CDATA[5]]></orderpriority_id><orderpriority_name><![CDATA[Urgent]]></orderpriority_name></orderpriority>" +
			"<orderpriority><orderpriority_id><![CDATA[6]]></orderpriority_id><orderpriority_name><![CDATA[Immediate]]></orderpriority_name></orderpriority></data>";
/*	P1: Immediate         = 6
	P2: Urgent               = 5
	P3: Important           = 4
	P4: High                   = 3
	P5: Normal               = 2
	P6: Medium              = 1
	P7: Low                    = 0*/
	public static final String getpririotytype =
			"<data><orderpriority><orderpriority_id><![CDATA[0]]></orderpriority_id><orderpriority_name><![CDATA[P7: Low]]></orderpriority_name></orderpriority>" +
			"<orderpriority><orderpriority_id><![CDATA[1]]></orderpriority_id><orderpriority_name><![CDATA[P6: Medium]]></orderpriority_name></orderpriority>" +
			"<orderpriority><orderpriority_id><![CDATA[2]]></orderpriority_id><orderpriority_name><![CDATA[P5: Normal]]></orderpriority_name></orderpriority>" +
			"<orderpriority><orderpriority_id><![CDATA[3]]></orderpriority_id><orderpriority_name><![CDATA[P4: High]]></orderpriority_name></orderpriority>" +
			"<orderpriority><orderpriority_id><![CDATA[4]]></orderpriority_id><orderpriority_name><![CDATA[P3: Important]]></orderpriority_name></orderpriority>" +
			"<orderpriority><orderpriority_id><![CDATA[5]]></orderpriority_id><orderpriority_name><![CDATA[P2: Urgent]]></orderpriority_name></orderpriority>" +
			"<orderpriority><orderpriority_id><![CDATA[6]]></orderpriority_id><orderpriority_name><![CDATA[P1: Immediate]]></orderpriority_name></orderpriority></data>";

	public static final String getstatustype = "<data>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[1]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Scheduled]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[8]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Unscheduled]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[2]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Confirmed]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[0]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[En-Route]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[3]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Started]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[4]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Completed]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[31]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Cancelled]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[32]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Follow-up]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[33]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Inaccessible]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[7]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Reschedule]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[50]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Pending]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[52]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Unpaid]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[5]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Paid]]></orderstatus_name>" +
			"</orderstatus>" +
			"</data>";
	
	// YD deleted/updated some of the status ids on 15-7-2015
	 /*
	  * "<orderstatus>" +
			"<orderstatus_id><![CDATA[33]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[No-Show]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[34]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[On-Hold]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[35]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Reschedule]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[36]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Tentative]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[50]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Invoice Pending]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[51]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Invoice Submitted]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[52]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Invoice Past-Due]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[53]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Invoice Follow-up]]></orderstatus_name>" +
			"</orderstatus>" +
			"<orderstatus>" +
			"<orderstatus_id><![CDATA[5]]></orderstatus_id>" +
			"<orderstatus_name><![CDATA[Invoice Closed]]></orderstatus_name>" +
			"</orderstatus>" +
	  * */
	
	
	
	
	private static HashMap<Long, Object> createMapForOrderPartfromObject(Object[] object, Context context) {
		String tempxml = "<data/>";
		HashMap<Long, Object> orderParts = new HashMap<Long, Object>();
		try {
			if (object.length>0) {
				OrderPart currentObject = (OrderPart)object[0];// handling only one record for now

				orderParts.put(currentObject.getOrder_part_id(), currentObject);
				return orderParts;
				// TODO return the original xml. Below is temp xml.
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
	
		}
		return null;
	}

    private static HashMap<Long, Object> createMapForOrderFormfromObject(Object[] object, Context context) {
        String tempxml = "<data/>";
        HashMap<Long, Object> orderForms = new HashMap<Long, Object>();
        try {
            if (object.length>0) {
                Form currentObject = (Form)object[0];// handling only one record for now

                orderForms.put(currentObject.getId(), currentObject);
                return orderForms;
                // TODO return the original xml. Below is temp xml.
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }
	
	private static HashMap<Long, Object> createMapForOrderTaskfromObject(Object[] object, Context context) {
		
		try {
			if (object.length>0) {
				OrderTask currentObject = (OrderTask)object[0];// handling only one record for now
				HashMap<Long, Object> orderTasks = new HashMap<Long, Object>();
				
				orderTasks.put(currentObject.getTask_id(), currentObject);
				return orderTasks;
				// TODO return the original xml. Below is temp xml.
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			//return tempxml;
		} finally {
	
		}
		return null;
	}
	
	private static HashMap<Long, Object> createMapForOrderfromObject(Object[] object, Context context) {
		
		try {
			
			
			if (object.length>0) {
				Order order = (Order)(object[0]);// assumig only one record
				HashMap<Long, Object> orderMap = new HashMap<Long, Object>();
				orderMap.put(order.getId(), order);
				return orderMap;
			}
			else
				return null;

		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	private static HashMap<Long, Object> createMapForOrderMediafromObject(Object[] object, Context context) {
		String tempxml = "<data/>";
		try {
			if (object.length>0) {
				//StringBuffer xml = new StringBuffer(xmlHeader + "<data>");
			
					OrderMedia odmedia = (OrderMedia)object[0];
					HashMap<Long, Object> orderMediaMap = new HashMap<Long, Object>();
					orderMediaMap.put(odmedia.getId(), odmedia);
					return orderMediaMap;
			
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}


	private static HashMap<Long, Object> createMapForFormfromObject(Object[] object, Context context) {
		String tempxml = "<data/>";
		try {
			if (object.length>0) {
				//StringBuffer xml = new StringBuffer(xmlHeader + "<data>");

				Form formmedia = (Form)object[0];
				HashMap<Long, Object> formMap = new HashMap<Long, Object>();
				formMap.put(Long.valueOf(formmedia.getFtid()), formmedia);
				return formMap;

			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}