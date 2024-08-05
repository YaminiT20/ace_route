package com.aceroute.mobile.software.utilities;

import android.content.Context;

import com.aceroute.mobile.software.component.Order;
import com.aceroute.mobile.software.component.OrderMedia;
import com.aceroute.mobile.software.component.OrderPart;
import com.aceroute.mobile.software.component.OrderTask;
//import com.software.mobile.software.component.OrderTaskOld;
import com.aceroute.mobile.software.component.OrdersMessage;
import com.aceroute.mobile.software.component.reference.Assets;
import com.aceroute.mobile.software.component.reference.CustomerContact;
import com.aceroute.mobile.software.component.reference.Form;
import com.aceroute.mobile.software.component.reference.MessagePanic;
import com.aceroute.mobile.software.component.reference.Shifts;
import com.aceroute.mobile.software.component.reference.Site;
import com.aceroute.mobile.software.database.DBHandler;
import com.aceroute.mobile.software.requests.EditContactReq;
import com.aceroute.mobile.software.requests.EditSiteReq;
import com.aceroute.mobile.software.requests.OrderMessage;
import com.aceroute.mobile.software.requests.SaveFormRequest;
import com.aceroute.mobile.software.requests.SaveMediaRequest;
import com.aceroute.mobile.software.requests.SaveNewOrder;
import com.aceroute.mobile.software.requests.SavePartDataRequest;
import com.aceroute.mobile.software.requests.SaveShiftReq;
import com.aceroute.mobile.software.requests.SaveSiteRequest;
import com.aceroute.mobile.software.requests.SaveTaskDataRequest;
//import com.software.mobile.software.requests.SaveTaskOldDataRequest;
import com.aceroute.mobile.software.requests.updateOrderRequest;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestObjectHandler {

    static int objectListlength = 1;


    public static Object[] getOrderPartValuesFromReqObj(Context context,
                                                        SavePartDataRequest dataReq) {
        Object[] orderPartList = null;
        try {
            orderPartList = new Object[objectListlength];
            //JSONObject dataObject = JSONHandler.getJsonObject(dataString);

            for (int i = 0; i < objectListlength; i++) {
                OrderPart orderPart = new OrderPart();

                if (dataReq.getOid() != null && !dataReq.getOid().trim().equals(""))
                    orderPart.setOid(Long.parseLong(dataReq.getOid()));

                if (dataReq.getPartId() != null && !dataReq.getPartId().trim().equals(""))
                    orderPart.setOrder_part_id(Long.parseLong(dataReq.getPartId()));

                if (dataReq.getTid() != null && !dataReq.getTid().trim().equals(""))
                    orderPart.setPart_type_id(Long.parseLong(dataReq.getTid()));

                orderPart.setOrder_part_QTY(dataReq.getQuantity());
                orderPart.setPart_barcode(dataReq.getSetbarcode());

                long updatetime = 0;
                try {
                    updatetime = Long.valueOf(dataReq.getUpd());
                } catch (Exception e) {
                    updatetime = Long.valueOf(dataReq.getTimeStamp());
                    e.printStackTrace();
                }
                if (updatetime == 0) {
                    updatetime = Long.valueOf(dataReq.getTimeStamp());
                }

                orderPart.setUpd_time(updatetime);

                orderPartList[i] = orderPart;
            }
        } catch (Exception e) {
            Utilities.log(context,
                    "Exeception while filling OrderPart object : ");
            e.printStackTrace();
        }
        return orderPartList;
    }

    public static Object[] getMessageValuesFromReqObj(Context context,
                                                      HashMap<String, String> dataReq) {

        Object[] messageList = null;
        messageList = new Object[objectListlength];

        try {

            for (int i = 0; i < objectListlength; i++) {
                MessagePanic msgPanic = new MessagePanic();
                long tmpid = Utilities.getCurrentTimeInMillis();
                msgPanic.setTid(Long.valueOf(dataReq.get(MessagePanic.MESSAGE_TYPE_ID)));
                msgPanic.setOid(Long.valueOf(dataReq.get(MessagePanic.MESSAGE_ORDER_ID)));
                msgPanic.setGeo(dataReq.get(MessagePanic.MESSAGE_GEO));
                msgPanic.setMessage(dataReq.get(MessagePanic.MESSAGE_MESSAGE));
                msgPanic.setStmp("" + tmpid);

                messageList[i] = msgPanic;
            }
        } catch (Exception e) {
            Utilities.log(context,
                    "Exeception while filling OrderPart object : ");
            e.printStackTrace();
        }
        return messageList;
    }

    public static Object[] getShiftValuesFromReqObj(Context context,
                                                    ArrayList<SaveShiftReq> dataReq) {
        Object[] ShiftList = null;
        try {
            ShiftList = new Object[dataReq.size()];
            //JSONObject dataObject = JSONHandler.getJsonObject(dataString);

            for (int i = 0; i < dataReq.size(); i++) {
                Shifts shifts = new Shifts();
                long tmpid = Utilities.getCurrentTimeInMillis();
                shifts.setId(tmpid);
                shifts.setLocalid(tmpid);
                shifts.setLid(dataReq.get(i).getLid());
                shifts.setDt(dataReq.get(i).getDt());
                shifts.setTmslot(dataReq.get(i).getTmslot());
                shifts.setTid(dataReq.get(i).getTid());
                shifts.setNm(dataReq.get(i).getNm());
                if (dataReq.get(i).getBrkslot() != null)
                    shifts.setBrkSlot(dataReq.get(i).getBrkslot());
                shifts.setGacc(dataReq.get(i).getGaccc());
                shifts.setTerri(dataReq.get(i).getTerri());
                shifts.setAddress(dataReq.get(i).getAddress());
                shifts.setModified(DBHandler.modifiedNew);


                ShiftList[i] = shifts;
            }
        } catch (Exception e) {
            Utilities.log(context, "Exeception while filling OrderPart object : ");
            e.printStackTrace();
        }
        return ShiftList;
    }

    public static Object[] getFormValuesFromReqObj(Context context,
                                                   ArrayList<SaveFormRequest> dataReq) {
        Object[] FormList = null;
        try {
            FormList = new Object[dataReq.size()];
            //JSONObject dataObject = JSONHandler.getJsonObject(dataString);

            for (int i = 0; i < dataReq.size(); i++) {
                Form form = new Form();
                long tmpid = Utilities.getCurrentTimeInMillis();
                form.setStmp(tmpid);
                form.setId(Long.parseLong(dataReq.get(i).getId()));
                form.setFdata(dataReq.get(i).getFdata());
                form.setFtid(dataReq.get(i).getFtid());
                form.setOid(Long.valueOf(dataReq.get(i).getOid()));
                form.setFormkeyonly(dataReq.get(i).getFrmkey());

            //    form.setFormkeyonly("jjj"+tmpid);
                FormList[i] = form;
            }
        } catch (Exception e) {
            Utilities.log(context, "Exeception while filling Form object : ");
            e.printStackTrace();
        }
        return FormList;
    }




    public static Object[] getOrderAssetValuesFromReqObj(Context context,
                                                         ArrayList<SaveFormRequest> dataReq) {

        Object[] orderAssetList = null;
        try {
            orderAssetList = new Object[dataReq.size()];// YD this is equivalent to one
            //JSONObject dataObject = JSONHandler.getJsonObject(dataString);

            for (int i = 0; i < dataReq.size(); i++) {
                Assets orderAsset = new Assets();
                long tmpid = Utilities.getCurrentTimeInMillis();
                orderAsset.setTimeStmp(tmpid);
                orderAsset.setId(Long.parseLong(dataReq.get(i).getId()));
                orderAsset.setFdata(dataReq.get(i).getFdata());
                orderAsset.setFtid(Long.parseLong(dataReq.get(i).getFtid()));
                orderAsset.setCid(Long.parseLong(dataReq.get(i).getCustId()));
                orderAsset.setOid(Long.valueOf(dataReq.get(i).getOid()));

               /* orderAsset.setCid(Long.parseLong(dataReq.get(i).get()));
                orderAsset.setGeoLoc(dataReq.getGeo());*/
                /*orderAsset.setTid(Long.parseLong(dataReq.getTid()));

                String temp = dataReq.getTid2();
                if (temp != null && !temp.equals(""))
                    orderAsset.setTid2(Long.parseLong(dataReq.getTid2()));
				*//*else
					orderAsset.setTid2(-1);*//*

                temp = dataReq.getPriorityId();
                if (temp != null && !temp.equals(""))
                    orderAsset.setPid(Long.parseLong(dataReq.getPriorityId()));
				*//*else
					orderAsset.setPid(-1);*//*

                orderAsset.setStatus(dataReq.getStatus());

                temp = dataReq.getCnt1();
                if (temp != null && !temp.equals(""))
                    orderAsset.setContact1(Long.parseLong(dataReq.getCnt1()));

                temp = dataReq.getCnt2();
                if (temp != null && !temp.equals(""))
                    orderAsset.setContact2(Long.parseLong(dataReq.getCnt2()));

                temp = dataReq.getNum1();
                if (temp != null && !temp.equals(""))
                    orderAsset.setNumber1(Long.parseLong(dataReq.getNum1()));

                temp = dataReq.getNum2();
                if (temp != null && !temp.equals(""))
                    orderAsset.setNumber2(Long.parseLong(dataReq.getNum2()));

                temp = dataReq.getNum3();
                if (temp != null && !temp.equals(""))
                    orderAsset.setNumber3(Long.parseLong(dataReq.getNum3()));
				*//*else
					orderAsset.setNumber3(-1);*//*

                temp = dataReq.getNum4();
                if (temp != null && !temp.equals(""))
                    orderAsset.setNumber4(Long.parseLong(dataReq.getNum4()));
				*//*else
					orderAsset.setNumber4(-1);*//*

                temp = dataReq.getNum5();
                if (temp != null && !temp.equals(""))
                    orderAsset.setNumber5(Long.parseLong(dataReq.getNum5()));
//				else
//					orderAsset.setNumber5(-1);

                temp = dataReq.getNum6();
                if (temp != null && !temp.equals(""))
                    orderAsset.setNumber6(Long.parseLong(dataReq.getNum6()));


                orderAsset.setNote1(dataReq.getNote1());
                orderAsset.setNote2(dataReq.getNote2());
                orderAsset.setNote3(dataReq.getNote3());
                orderAsset.setNote4(dataReq.getNote4());
                orderAsset.setNote5(dataReq.getNote5());
                orderAsset.setNote6(dataReq.getNote6());

                orderAsset.setCt1(dataReq.getCt1());
                orderAsset.setCt2(dataReq.getCt2());
                orderAsset.setCt3(dataReq.getCt3());*/

                orderAssetList[i] = orderAsset;
            }
        } catch (Exception e) {
            Utilities.log(context,
                    "Exeception while filling OrderPart object : ");
            e.printStackTrace();
        }
        return orderAssetList;
    }

    public static Object[] getOrderTaskValuesFromReqObj(Context context,
                                                        SaveTaskDataRequest innerReqDataObj) {
        Object[] orderTaskList = null;
        try {
            orderTaskList = new Object[objectListlength];
            //JSONObject dataObject = JSONHandler.getJsonObject(dataString);
            for (int i = 0; i < objectListlength; i++) {
                OrderTask orderTask = new OrderTask();
                orderTask.setId(Long.parseLong(innerReqDataObj.getOrderId()));
                orderTask.setOrder_task_id(Long.parseLong(innerReqDataObj.getTaskId()));
                orderTask.setTask_id(Long.parseLong(innerReqDataObj.getTaskTypeId()));

                orderTask.setAction_status(innerReqDataObj.getTree_action_stat());
                orderTask.setPriority(innerReqDataObj.getTree_priority());
                orderTask.setTree_actualcount(innerReqDataObj.getActual_hrs());
                orderTask.setTree_alert(innerReqDataObj.getTree_Alert());
                orderTask.setTree_clearence(innerReqDataObj.getTree_clearance());
                orderTask.setTree_comment(innerReqDataObj.getTree_comment());
                orderTask.setTree_cycle(innerReqDataObj.getTree_cycle());
                orderTask.setTree_dia(innerReqDataObj.getTree_dia());
                orderTask.setTree_expcount(innerReqDataObj.getEstimated_hrs());
                orderTask.setTree_ht(innerReqDataObj.getTree_ht());
                orderTask.setTree_msc(innerReqDataObj.getTree_Msc());
                orderTask.setTree_note(innerReqDataObj.getTree_Notes());
                orderTask.setTree_owner(innerReqDataObj.getTree_owner());
                orderTask.setTree_pcomment(innerReqDataObj.getTree_presc_comm());
                orderTask.setTree_timespent(innerReqDataObj.getTree_time_spent());
                orderTask.setTree_tm(innerReqDataObj.getTreeT_M());
                orderTask.setTree_type(innerReqDataObj.getTreeWorkPresc());
                orderTask.setTree_geo(innerReqDataObj.getTree_geo());
                orderTask.setTree_accesspath(innerReqDataObj.getTree_access_pathLst());
                orderTask.setTree_ct1(innerReqDataObj.getTree_access_comp());
                orderTask.setTree_ct2(innerReqDataObj.getTree_pres_comp());
                orderTask.setTree_ct3(innerReqDataObj.getTree_setup_comp());
                orderTask.setOrderEndDate(innerReqDataObj.getOrderEndDate());
                orderTask.setOrderEndTime(innerReqDataObj.getOrderEndTime());

                long updatetime = 0;
                try {
                    updatetime = Long.valueOf(innerReqDataObj.getUpd());
                } catch (Exception e) {
                    updatetime = Long.valueOf(innerReqDataObj.getStmp());
                    e.printStackTrace();
                }
                if (updatetime == 0) {
                    updatetime = Long.valueOf(innerReqDataObj.getStmp());
                }
                orderTask.setUpd_time(updatetime);
                orderTaskList[i] = orderTask;
            }
        } catch (Exception e) {
            Utilities.log(context,
                    "Exeception while filling OrderTask object : ");
            e.printStackTrace();
        }
        return orderTaskList;
    }


    // by mandeep for ordertaskold

  /*  public static Object[] getOrderTaskOldValuesFromReqObj(Context context, SaveTaskOldDataRequest innerReqDataObject) {
        Object[] orderTaskList = null;

        try {
            orderTaskList = new Object[objectListlength];

            for (int i = 0; i < objectListlength; i++) {
                OrderTaskOld orderTaskObj = new OrderTaskOld();
                orderTaskObj.setOid(Long.parseLong(innerReqDataObject.getOrderId()));
                orderTaskObj.setOrder_task_id(Long.parseLong(innerReqDataObject.getTaskId()));
                orderTaskObj.setTask_type_id(Long.parseLong(innerReqDataObject.getTaskTypeId()));
                orderTaskObj.setOrder_task_RATE(innerReqDataObject.getRate());

                long updatetime = 0;
                try {
                    updatetime = Long.valueOf(innerReqDataObject.getUpd());
                } catch (Exception e) {
                    updatetime = Long.valueOf(innerReqDataObject.getStmp());
                    e.printStackTrace();
                }
                if (updatetime == 0) {
                    updatetime = Long.valueOf(innerReqDataObject.getStmp());
                }
                orderTaskObj.setUpd_time(updatetime);
                orderTaskList[i] = orderTaskObj;
            }

        } catch (Exception e) {
            Utilities.log(context, "Exeception while filling OrderTaskOld object : ");
            e.printStackTrace();
        }
        return orderTaskList;
    }*/

    // End Here

    public static Object[] getSiteValuesFromReqObj(Context context,
                                                   SaveSiteRequest saveSiteReqObject) {
        Object[] siteList = null;
        try {
            siteList = new Object[objectListlength];
            //JSONObject dataObject = JSONHandler.getJsonObject(dataString);
            for (int i = 0; i < objectListlength; i++) {
                Site st = new Site();
                st.setId(Long.parseLong(saveSiteReqObject.getId()));
                st.setCid(Long.parseLong(saveSiteReqObject.getCid()));
                st.setAdr(saveSiteReqObject.getAdr());
                st.setNm(saveSiteReqObject.getNm());
                st.setGeo(saveSiteReqObject.getGeo());
                st.setDetail(saveSiteReqObject.getDtl());
                String temp = saveSiteReqObject.getTid();
                if (temp != null && !temp.equals(""))
                    st.setTid(Long.parseLong(saveSiteReqObject.getTid()));
                temp = saveSiteReqObject.getLtpnm();
                if (temp != null && !temp.equals(""))
                    st.setSitetypenm(saveSiteReqObject.getLtpnm());

                long updatetime = 0;
                try {
                    if (saveSiteReqObject.getUpd() != null)  //YD Done because exception is comming in logs sent by arvind
                        updatetime = Long.valueOf(saveSiteReqObject.getUpd());
                    else if (saveSiteReqObject.getTstamp() != null)
                        updatetime = Long.valueOf(saveSiteReqObject.getTstamp());
                } catch (Exception e) {
                    updatetime = Long.valueOf(saveSiteReqObject.getTstamp());
                    e.printStackTrace();
                }
                if (updatetime == 0) {
                    updatetime = Long.valueOf(saveSiteReqObject.getTstamp());
                }
                st.setUpd(updatetime);
                siteList[i] = st;
            }
        } catch (Exception e) {
            Utilities.log(context,
                    "Exeception while filling OrderTask object : ");
            e.printStackTrace();
        }
        return siteList;
    }

    public static Object[] getSiteValuesFromReqObjForUpdate(Context context,
                                                            EditSiteReq edtSiteReqObj) {
        Object[] siteList = null;
        try {
            siteList = new Object[objectListlength];
            //JSONObject dataObject = JSONHandler.getJsonObject(dataString);
            for (int i = 0; i < objectListlength; i++) {
                Site st = new Site();
                st.setId(edtSiteReqObj.getId());
                st.setGeo(edtSiteReqObj.getGeo());

                siteList[i] = st;
            }
        } catch (Exception e) {
            Utilities.log(context,
                    "Exeception while filling OrderTask object : ");
            e.printStackTrace();
        }
        return siteList;
    }

    public static Object[] getSiteValuesFromReqObj(Context context,
                                                   EditSiteReq edtSiteReqObj) {
        Object[] siteList = null;
        try {
            siteList = new Object[objectListlength];
            //JSONObject dataObject = JSONHandler.getJsonObject(dataString);
            for (int i = 0; i < objectListlength; i++) {
                Site st = new Site();
                st.setId(edtSiteReqObj.getId());
                st.setNm(edtSiteReqObj.getName());
                st.setAdr(edtSiteReqObj.getAdr());
                st.setAdr2(edtSiteReqObj.getAdr2());
                st.setDetail(edtSiteReqObj.getDesc());

                siteList[i] = st;
            }
        } catch (Exception e) {
            Utilities.log(context,
                    "Exeception while filling OrderTask object : ");
            e.printStackTrace();
        }
        return siteList;
    }

    public static Object[] getOrderValuesFromReqObj(Context context,
                                                    Object requestObj, String name) {
        Object[] orderList = null;
        try {
            orderList = new Object[objectListlength];
            //JSONObject dataObject = JSONHandler.getJsonObject(dataString);
            if (name.equals(Order.ORDER_STATUS_ID)) {
                updateOrderRequest reqObj = (updateOrderRequest) requestObj;
                for (int i = 0; i < objectListlength; i++) {
                    Order order = new Order();
                    order.setId(Long.parseLong(reqObj.getId()));
                    order.setStatusId(Long.parseLong(reqObj.getValue()));
					/*String location = Utilities.getLocation(context);
					location = location.replace("#", ",");
					order.setCustSiteGeocode(location);*/ //YD TODO commenting because arvind sir said to show current location when status changes but now reverting back to old state
                    //order.setCustSiteGeocode(dataObject.get(
                    //		Order.ORDER_CUSTOMER_SITE_GEOCODE).toString());
                    orderList[i] = order;
                }
            }
            if (name.equals(Order.ORDER_CUSTOMER_SITE_GEOCODE)) {
                updateOrderRequest reqObj = (updateOrderRequest) requestObj;
                Order order = new Order();
                order.setId(Long.parseLong(reqObj.getId()));
                order.setCustSiteGeocode(reqObj.getValue());
                orderList[0] = order;

            }/* else if (name.equals(Order.KEY_NAME)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setNm(dataObject.get(Order.KEY_VALUE).toString());
					orderList[i] = order;
				}
			}
			else if(name.equals(Order.ORDER_EPOCH_TIME)) {
				Order order = new Order();
				try {
					long etm = Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString());
					order.setEpochTime(etm);
				} catch (Exception e1) {
					e1.printStackTrace();
					order.setEpochTime(0);
				}
			}
			else*/
            if (name.equals(Order.ORDER_TIME)) {//YD use this when you just need to upate time only

                updateOrderRequest reqObj = (updateOrderRequest) requestObj;
                for (int i = 0; i < objectListlength; i++) {
                    String dateTime[] = reqObj.getValue()
                            .split(",");
                    Order order = new Order();

                    try {
                        String epochtime = dateTime[0];
                        long etm = Long.valueOf(epochtime);
                        order.setEpochTime(etm);
                        order.setStartTime(etm);
                        etm = Long.valueOf(dateTime[1]);
                        order.setEndTime(etm);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        order.setEpochTime(0);
                    }

                    String fromDate = dateTime[2];
                    String toDate = dateTime[3];
                    order.setId(Long.parseLong(reqObj.getId()));

                    try {
                        fromDate = dateTime[2].replace("/", "-");
                    } catch (Exception e) {
                        e.printStackTrace();
                        fromDate = dateTime[2];
                    }

                    try {
                        toDate = dateTime[3].replace("/", "-");
                    } catch (Exception e) {
                        e.printStackTrace();
                        toDate = dateTime[3];
                    }

                    order.setToDate(toDate);
                    order.setFromDate(fromDate);
                    orderList[i] = order;
                }
            } /*else if (name.equals(Order.ORDER_CUSTOMER_ID)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setCustomerid(Long.parseLong(dataObject.get(
							Order.KEY_VALUE).toString()));
					orderList[i] = order;
				}
			} else if (name.equals(Order.ORDER_STATUS_ORDER_TYPE_ID)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setOrderTypeId(Long.parseLong(dataObject.get(
							Order.KEY_VALUE).toString()));
					orderList[i] = order;
				}
			} else if (name.equals(Order.ORDER_STATUS_PRIORITY_TYPE_ID)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setPriorityTypeId(Long.parseLong(dataObject.get(
							Order.KEY_VALUE).toString()));
					orderList[i] = order;
				}
			} else if (name.equals(Order.ORDER_STATUS_PRIMARY_WORKER_ID)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setListAddWorkerPiped(dataObject.get(Order.KEY_VALUE)
							.toString());
					orderList[i] = order;
				}
			} else if (name.equals(Order.ORDER_NOTES)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setOrdeNotes(dataObject.get(Order.KEY_VALUE)
							.toString());
					orderList[i] = order;
				}
			} else if (name.equals(Order.ORDER_STATUS_PO_NUMBER)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setPoNumber(dataObject.get(Order.KEY_VALUE)
							.toString());
					orderList[i] = order;
				}
			}

			else if (name.equals(Order.ORDER_STATUS_PO_NUMBER)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setPoNumber(dataObject.get(Order.KEY_VALUE)
							.toString());
					orderList[i] = order;
				}
			} else if (name.equals(Order.ORDER_START_TIME)) {
				// no methods available in Order class
//				for (int i = 0; i < objectListlength; i++) {
//					Order order = new Order();
//					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
//							.toString()));
//					order.setFromDate(dataObject.get(Order.KEY_VALUE)
//							.toString());
//					orderList[i] = order;
//				}
			} else if (name.equals(Order.ORDER_END_TIME)) {
				// no methods available in Order class.
//				for (int i = 0; i < objectListlength; i++) {
//					Order order = new Order();
//					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
//							.toString()));
//					order.setToDate(dataObject.get(Order.KEY_VALUE).toString());
//					orderList[i] = order;
//				}
			} else if (name.equals(Order.ORDER_START_DATE)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setFromDate(dataObject.get(Order.KEY_VALUE)
							.toString());
					orderList[i] = order;
				}
			} else if (name.equals(Order.ORDER_END_DATE)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setToDate(dataObject.get(Order.KEY_VALUE)
							.toString());
					orderList[i] = order;
				}
			} else if (name.equals(Order.ORDER_NAME)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setNm(dataObject.get(Order.KEY_VALUE)
							.toString());
					orderList[i] = order;
				}
			}else if (name.equals(Order.ORDER_STATUS_INVOICE_NUMBER)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setInvoiceNumber(dataObject.get(Order.KEY_VALUE)
							.toString());
					orderList[i] = order;
				}
			}*/ else if (name.equals(Order.ORDER_STATUS_PRIMARY_WORKER_ID)) {//YD
                for (int i = 0; i < objectListlength; i++) {
                    updateOrderRequest reqObj = (updateOrderRequest) requestObj;

                    Order order = new Order();
                    order.setId(Long.parseLong(reqObj.getId()));
                    order.setPrimaryWorkerId(String.valueOf(reqObj.getValue()));
					/*order.setListAddWorkerPiped(dataObject.get(Order.KEY_VALUE)
							.toString());*/
                    orderList[i] = order;
                }
            }/*else if (name.equals(Order.ORDER_STATUS_SUMMARY)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setSummary(dataObject.get(Order.KEY_VALUE)
							.toString());
					orderList[i] = order;
				}
			}else if (name.equals(Order.ORDER_CUSTOMER_NAME)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setCustName(dataObject.get(Order.KEY_VALUE)
							.toString());
					orderList[i] = order;
				}
			}else if (name.equals(Order.ORDER_CUSTOMER_SITE_STREET_ADDRESS)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setCustSiteStreeAdd(dataObject.get(Order.KEY_VALUE)
							.toString());
					orderList[i] = order;
				}
			}else if (name.equals(Order.ORDER_CUSTOMER_SITE_SUITE_ADDRESS)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setCustSiteSuiteAddress(dataObject.get(Order.KEY_VALUE)
							.toString());
					orderList[i] = order;
				}
			}*/ else if (name.equals(Order.ORDER_CUSTOMER_SITE_GEOCODE)) {
                for (int i = 0; i < objectListlength; i++) {
                    updateOrderRequest reqObj = (updateOrderRequest) requestObj;

                    Order order = new Order();
                    order.setId(Long.parseLong(reqObj.getId()));
                    order.setCustSiteGeocode(reqObj.getValue());
                    orderList[i] = order;
                }
            }/*else if (name.equals(Order.ORDER_CUSTOMER_CONTACT_ID)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setCustomerid(Long.parseLong(dataObject.get(Order.KEY_VALUE)
							.toString()));
					orderList[i] = order;
				}
			}else if (name.equals(Order.ORDER_CUSTOMER_CONTACT_NAME)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setCustContactName(dataObject.get(Order.KEY_VALUE)
							.toString());
					orderList[i] = order;
				}
			}else if (name.equals(Order.ORDER_CUSTOMER_CONTACT_NUMBER)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setCustContactNumber(dataObject.get(Order.KEY_VALUE)
							.toString());
					orderList[i] = order;
				}
			}else if (name.equals(Order.ORDER_CUSTOMER_PART_COUNT)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setCustPartCount(Long.parseLong(dataObject.get(Order.KEY_VALUE)
							.toString()));
					orderList[i] = order;
				}
			}else if (name.equals(Order.ORDER_CUSTOMER_SERVICE_COUNT)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setCustServiceCount(Long.parseLong(dataObject.get(Order.KEY_VALUE)
							.toString()));
					orderList[i] = order;
				}
			}else if (name.equals(Order.ORDER_CUSTOMER_MEDIA_COUNT)) {
				for (int i = 0; i < objectListlength; i++) {
					Order order = new Order();
					order.setId(Long.parseLong(dataObject.get(Order.ORDER_ID)
							.toString()));
					order.setCustMetaCount(Long.parseLong(dataObject.get(Order.KEY_VALUE)
							.toString()));
					orderList[i] = order;
				}
			}*/
			/*else if (name.equals(null)){
				String xmlString = ((PubnubRequest)requestObj).getXml();
				orderList = new XMLHandler().getOrderValuesFromXML(xmlString); //using for updating order coming from pnb
			}*/


        } catch (Exception e) {
            Utilities.log(context, "Exeception while filling Order object : ");
            e.printStackTrace();
        }
        return orderList;
    }


    public static Object getAllOrderValuesFromReqObj(Context context, SaveNewOrder req) {

        Order order = null;
        try {
            order = new Order();
            order.setId(Long.parseLong(req.getId()));
            String temp;
            temp = req.getStart_date();
            temp = temp.replace("/", "-");
            temp = temp.replace("/00", "-00");
            order.setFromDate(temp);
            temp = req.getEnd_date();
            temp = temp.replace("/", "-");
            temp = temp.replace("/00", "-00");

            order.setToDate(temp);

            order.setNm(req.getOrderNm());
            order.setStatusId(Long.parseLong(req.getOrderStatus()));
            order.setPoNumber(req.getSsd());

            temp = req.getInvoiceNum();
            if (temp == null || temp.equals(""))
                order.setInvoiceNumber("");
            else
                order.setInvoiceNumber(req.getInvoiceNum());
                order.setInvoiceNumber(req.getInvoiceNum());

            temp = req.getAlert();
            if (temp == null || temp.equals(""))
                order.setOrderAlert("");
            else
                order.setOrderAlert(req.getAlert());

            temp = req.getOrderStartTime();
            if (temp == null || temp.equals(""))
                order.setStartTime(0);
            else
                order.setStartTime(Long.parseLong(req.getOrderStartTime()));

            temp = req.getOrderEndTime();
            if (temp == null || temp.equals(""))
                order.setEndTime(0);
            else
                order.setEndTime(Long.parseLong(req.getOrderEndTime()));

            temp = req.getLocationId();
            if (temp == null || temp.equals(""))
                order.setCustSiteId("");
            else
                order.setCustSiteId(req.getLocationId());
            order.setOrderTypeId(Long.parseLong(req.getTid()));//typeid


            order.setPriorityTypeId(Long.parseLong(req.getPriorityId()));

            order.setPrimaryWorkerId(req.getResId());
            order.setPriorityTypeId(Long.parseLong(req.getPriorityId()));
            order.setSummary(req.getOrderDetail());
            order.setOrdeNotes(req.getNote());
            order.setCustomerid(Long.parseLong(req.getCustId()));

            order.setCustSiteGeocode(req.getGeo());

            if (req.getPartCount() != null && req.getPartCount() != "") {
                temp = req.getPartCount();
                if (temp == null || temp.equals(""))
                    order.setCustPartCount(0);
                else
                    order.setCustPartCount(Long.parseLong(req.getPartCount()));
            } else
                order.setCustPartCount(0);

           /* if (req.getServiceCount() != null && req.getServiceCount() != "") {
                temp = req.getServiceCount();
                if (temp == null || temp.equals(""))
                    order.setCustServiceCount(0);
                else
                    order.setCustServiceCount(Long.parseLong(req.getServiceCount()));
            } else
                order.setCustServiceCount(0);*///YD 2020 commenting because order task is not there in app now

            if (req.getMediaCount() != null && req.getMediaCount() != "") {
                temp = req.getMediaCount();
                if (temp == null || temp.equals(""))
                    order.setCustMetaCount(0);
                else
                    order.setCustMetaCount(Long.parseLong(req.getMediaCount()));
            } else
                order.setCustMetaCount(0);
            order.setType(req.getTypeD());

            if (req.getEpochtime_etm() != null && req.getEpochtime_etm() != "") {
                temp = req.getEpochtime_etm();
                if (temp == null || temp.equals(""))
                    order.setEpochTime(0);
                else
                    order.setEpochTime(Long.parseLong(req.getEpochtime_etm()));
            } else
                order.setEpochTime(0);
//YD customer Name
            if (req.getCustNm() != null) {
                temp = req.getCustTypNm();
                if (temp == null || temp.equals(""))
                    order.setCustName("");
                else
                    order.setCustName(req.getCustNm());
            }

            if (req.getCustTypNm() != null) {
                temp = req.getCustTypNm();
                if (temp == null || temp.equals(""))
                    order.setCustypename("");
                else
                    order.setCustypename(req.getCustTypNm());
            }

            if (req.getFlg() != null) {
                temp = req.getFlg();
                if (temp == null || temp.equals(""))
                    order.setFlg("");
                else
                    order.setFlg(req.getFlg());
            }

            if (req.getLocationTypNm() != null) {
                temp = req.getLocationTypNm();
                if (temp == null || temp.equals(""))
                    order.setSitetypename("");
                else
                    order.setSitetypename(req.getLocationTypNm());
            }
            if (req.getAudio() != null) {
                temp = req.getAudio();
                if (temp == null || temp.equals(""))
                    order.setAudioCount(0);
                else
                    order.setAudioCount(Long.parseLong(req.getAudio()));
            }
            if (req.getSignature() != null) {
                temp = req.getSignature();
                if (temp == null || temp.equals(""))
                    order.setSigCount(0);
                else
                    order.setSigCount(Long.parseLong(req.getSignature()));
            }
            if (req.getImages() != null) {
                temp = req.getImages();
                if (temp == null || temp.equals(""))
                    order.setImgCount(0);
                else
                    order.setImgCount(Long.parseLong(req.getImages()));
            }
            if (req.getNote() != null) {
                temp = req.getNote();
                if (temp == null || temp.equals(""))
                    order.setNotCount(0);
                else
                    order.setNotCount(Long.parseLong(req.getNote()));
            }
            return order;
        } catch (Exception e) {
            Utilities.log(context, "Exeception while filling Order object : ");
            e.printStackTrace();
            return null;
        }
    }

    public static Object[] getOrderMediaValuesFromJSON(Context context,
                                                       Object requestObj) {
        Object[] orderMediaList = null;
        try {
            SaveMediaRequest reqObj = (SaveMediaRequest) requestObj;
            orderMediaList = new Object[objectListlength];
            //JSONObject dataObject = JSONHandler.getJsonObject(dataString);
            for (int i = 0; i < objectListlength; i++) {
                OrderMedia ordermedia = new OrderMedia();
                ordermedia.setId(Long.parseLong(reqObj.getId()));
                ordermedia.setOrderid(Long.parseLong(reqObj.getOrderId()));
                ordermedia.setFile_desc(String.valueOf(reqObj.getDtl()));
                Object temp = reqObj.getOrderGeo();
                if (temp != null)
                    ordermedia.setGeocode(reqObj.getId());
                else
                    ordermedia.setGeocode("0#0");
                ordermedia.setMediatype(Integer.valueOf(reqObj.getTid()));
                ordermedia.setMimetype(String.valueOf(reqObj.getMime()));
                ordermedia.setMetapath(reqObj.getMetapath());// vicky for he time being
                ordermedia.setFrmkey(reqObj.getFrmkey());
                ordermedia.setFrmfiledid(reqObj.getFrmfldkey());

                long time = 0;
                try {
                    if (reqObj.getUpd() != null)
                        time = Long.valueOf(reqObj.getUpd());
                    else
                        time = Long.valueOf(reqObj.getTimestamp());
                } catch (Exception e) {
                    time = Long.valueOf(
                            reqObj.getTimestamp());
                    e.printStackTrace();
                }
                if (time == 0) {
                    time = Long.valueOf(
                            reqObj.getTimestamp());
                }

                ordermedia.setUpd_time(time);
                orderMediaList[i] = ordermedia;
            }
        } catch (Exception e) {
            Utilities.log(context,
                    "Exeception while filling OrderMedia object : ");
            e.printStackTrace();
        }
        return orderMediaList;
    }

    public static Object[] getOrderMessageValuesFromRequest(Context context,
                                                            Object requestObj) {
        Object[] orderMessageList = null;
        try {
            OrderMessage reqObj = (OrderMessage) requestObj;
            orderMessageList = new Object[objectListlength];
            //JSONObject dataObject = JSONHandler.getJsonObject(dataString);
            for (int i = 0; i < objectListlength; i++) {
                OrdersMessage ordermsg = new OrdersMessage();
                ordermsg.setTid(reqObj.getTid());
                ordermsg.setOid(reqObj.getOid());
                ordermsg.setCid(reqObj.getCid());
                Object temp = reqObj.getGeo();
                if (temp != null)
                    ordermsg.setGeo(reqObj.getGeo());
                else
                    ordermsg.setGeo("0#0");
                ordermsg.setCntid(reqObj.getCntid());
                ordermsg.setTel(reqObj.getTel());
                ordermsg.setTz(reqObj.getTz());
                ordermsg.setStmp(reqObj.getStmp());

                orderMessageList[i] = ordermsg;
            }
        } catch (Exception e) {
            Utilities.log(context,
                    "Exeception while filling OrderMedia object : ");
            e.printStackTrace();
        }
        return orderMessageList;
    }

    public static Object[] getOrderContactValuesFromRequest(Context context,
                                                            Object requestObj) {
        Object[] orderContactList = null;
        try {
            EditContactReq reqObj = (EditContactReq) requestObj;
            orderContactList = new Object[objectListlength];
            //JSONObject dataObject = JSONHandler.getJsonObject(dataString);
            for (int i = 0; i < objectListlength; i++) {
                CustomerContact custCont = new CustomerContact();
                custCont.setId(reqObj.getId());
                custCont.setContactname(reqObj.getName());
                custCont.setContacttel(String.valueOf(reqObj.getTell()));
                custCont.setContactType(reqObj.getTid());
                custCont.setCustomerid(reqObj.getCid());
                custCont.setContactEml(reqObj.getEmail());
                //ordermsg.setStmp(reqObj.getStmp());

                orderContactList[i] = custCont;
            }
        } catch (Exception e) {
            Utilities.log(context,
                    "Exeception while filling OrderMedia object : ");
            e.printStackTrace();
        }
        return orderContactList;
    }
}
