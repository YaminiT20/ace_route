package com.aceroute.mobile.software.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aceroute.mobile.software.BaseTabActivity;
import com.aceroute.mobile.software.HeaderInterface;
import com.aceroute.mobile.software.R;
import com.aceroute.mobile.software.SplashII;
import com.aceroute.mobile.software.adaptor.MessageAdaptor;
import com.aceroute.mobile.software.async.IActionOKCancel;
import com.aceroute.mobile.software.async.RespCBandServST;
import com.aceroute.mobile.software.component.reference.MessagePanic;
import com.aceroute.mobile.software.dialog.CustomDialog;
import com.aceroute.mobile.software.http.RequestObject;
import com.aceroute.mobile.software.http.Response;
import com.aceroute.mobile.software.utilities.Utilities;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.util.HashMap;

/**
 * Created by root on 2/1/18.
 */

public class MessageSos extends BaseFragment implements RespCBandServST , HeaderInterface, View.OnClickListener,IActionOKCancel {

    public static long firstOrderId = 0;
    private static int PANIC_ALARM_REQ = 1;
    EditText messageNote;
    private SwipeListView messageSwipelst;
    private CustomDialog customDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.message_sos , container, false);
        mActivity.registerHeader(this);
        mActivity.setHeaderTitle("", "Message", "");
        initiViewReference(view);
        doListViewSettings();

        checkAdaptor();
        return view;
    }

    private void checkAdaptor() {

        MessagePanic msgPan = new MessagePanic();
        msgPan.setMessage("test it now");
        msgPan.setStmp("1");

        HashMap<Long, MessagePanic>  messaheHM = new HashMap<>();
        messaheHM.put(1L, msgPan);

        MessageAdaptor msgAdapter = new MessageAdaptor(mActivity, messaheHM);
        messageSwipelst.setAdapter(msgAdapter);
    }

    private void initiViewReference(View view) {
        Button panic_alm = (Button)view.findViewById(R.id.Panic_Alarm);
        panic_alm.setOnClickListener(this);
        messageSwipelst = (SwipeListView) view.findViewById(R.id.message_swipelst);

        messageNote = (EditText)view.findViewById(R.id.message_note);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Panic_Alarm:

                HashMap<String , String> requestObj = new HashMap<String , String>();
                requestObj.put("action","message");
                //requestObj.put("rid","message");
                requestObj.put("tid","1");
                requestObj.put("geo", Utilities.getLocation(mActivity));
                requestObj.put("oid", ""+firstOrderId);
                requestObj.put("tm",""+Utilities.getCurrentTime());

                RequestObject reqObj = new RequestObject();
                reqObj.setRequestMap(requestObj);
                reqObj.setAction(MessagePanic.ACTION_WORKER_MESSAGE_PANIC);

                MessagePanic.saveData(reqObj,mActivity, this, PANIC_ALARM_REQ);

        }
    }

    @Override
    public void headerClickListener(String callingId) {
        if(callingId.equals(BaseTabActivity.HeaderDonePressed)) {

            HashMap<String , String> requestObj = new HashMap<String , String>();
            requestObj.put("action","message");
            requestObj.put("tid","0");
            requestObj.put("msg",messageNote.getText().toString());
            requestObj.put("geo", Utilities.getLocation(mActivity));
            requestObj.put("oid", ""+firstOrderId);
            requestObj.put("tm",""+Utilities.getCurrentTime());

            RequestObject reqObj = new RequestObject();
            reqObj.setRequestMap(requestObj);
            reqObj.setAction(MessagePanic.ACTION_WORKER_MESSAGE_PANIC);

            MessagePanic.saveData(reqObj,mActivity, this, PANIC_ALARM_REQ);
        }

    }

    @Override
    public void ServiceStarter(RespCBandServST activity, Intent intent) {

    }

    @Override
    public void setResponseCallback(String response, Integer reqId) {

    }

    @Override
    public void setResponseCBActivity(Response response) {

    }

    private void doListViewSettings() {
        messageSwipelst
                .setSwipeListViewListener(new BaseSwipeListViewListener() {
                    @Override
                    public void onOpened(int position, boolean toRight) {

                        messageSwipelst.closeAnimate(position);
                    }

                    @Override
                    public void onClosed(int position, boolean fromRight) {
                        View rowItem = Utilities.getViewOfListByPosition(
                                position, messageSwipelst);
                       // positionLastEdited = position;
                        RelativeLayout backlayout = (RelativeLayout) rowItem
                                .findViewById(R.id.back);
                        backlayout.setBackgroundColor(getResources().getColor(
                                R.color.color_white));
                        TextView chat = (TextView) rowItem
                                .findViewById(R.id.back_view_chat_textview);
                        TextView invite = (TextView) rowItem
                                .findViewById(R.id.back_view_invite_textview);
                        chat.setVisibility(View.INVISIBLE);
                        invite.setVisibility(View.INVISIBLE);

                        if (fromRight){// code to open custom dialog YD
							 /*customDialog = CustomDialog.getInstance(
										mActivity,
										OrderPartsFragment.this,
										getResources().getString(
												R.string.msg_edit),
										getResources().getString(
												R.string.app_name),
										DIALOG_TYPE.OK_CANCEL, 1);
								customDialog.setCancellable(false);
								customDialog.show();*/
                            // use array list_cal for data and notify the list_cal
                            long partTypeId=-1, orderPartId = 0, orderId = 0, orderUpdatedTime = 0;
                            String partQuant=null;
                           /* if (positionLastEdited!=-1)
                            {
                                OrderPart odrPrtObj = orderPartListMap.get(keys[positionLastEdited]);
                                orderPartId = odrPrtObj.getOrder_part_id();

                                partTypeId = odrPrtObj.getPart_type_id();
                                partQuant = odrPrtObj.getOrder_part_QTY();
                            }

                            AddEditPartsFragment addEditMaterialFragment = new AddEditPartsFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("MaterialType", "EDIT PART");
                            bundle.putString("OrderId", currentOdrId);
                            bundle.putString("OrderName", currentOdrName);

                            bundle.putLong("partTypeId",partTypeId );
                            bundle.putLong("orderPartId", orderPartId);
                            bundle.putString("partQuantity",partQuant );

                            addEditMaterialFragment.setArguments(bundle);
                            mActivity.pushFragments(Utilities.JOBS, addEditMaterialFragment, true, true,BaseTabActivity.UI_Thread);
                        */} else{
                            // openChatScreen(position);
                            customDialog = CustomDialog.getInstance(
                                    mActivity,
                                    MessageSos.this,
                                    getResources().getString(
                                            R.string.msg_delete),
                                    getResources().getString(
                                            R.string.msg_title),
                                    CustomDialog.DIALOG_TYPE.OK_CANCEL, 2 ,  mActivity);
                            customDialog.setCancellable(false);
                            customDialog.show();
                        }
                    }

                    @Override
                    public void onListChanged() {
                    }

                    @Override
                    public void onMove(int position, float x) {
                    }

                    @Override
                    public void onStartOpen(int position, int action,// called when swipe starts
                                            boolean right) {

                       // positionLastEdited = position;
                        View rowItem = Utilities.getViewOfListByPosition(
                                position, messageSwipelst);

                        RelativeLayout backlayout = (RelativeLayout) rowItem
                                .findViewById(R.id.back);
                        TextView chat = (TextView) rowItem
                                .findViewById(R.id.back_view_chat_textview);
                        /*TextView chat1 = (TextView) rowItem
                                .findViewById(R.id.back_view_dummy1);*/

                        TextView invite = (TextView) rowItem
                                .findViewById(R.id.back_view_invite_textview);
                       /* TextView invite1 = (TextView) rowItem
                                .findViewById(R.id.back_view_dummy);*/

                        if (right) {
                            backlayout.setBackgroundColor(getResources()
                                    .getColor(R.color.bdr_green));
                            chat.setVisibility(View.GONE);
                           // chat1.setVisibility(View.GONE);
                            invite.setVisibility(View.VISIBLE);
                           // invite1.setVisibility(View.VISIBLE);

                        } else {

                            backlayout.setBackgroundColor(getResources()

                                    .getColor(R.color.color_red));
                            chat.setVisibility(View.VISIBLE);
                           // chat1.setVisibility(View.VISIBLE);

                            invite.setVisibility(View.GONE);
                            //invite1.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onStartClose(int position, boolean right) {
                    }

                    @Override
                    public void onClickFrontView(int position) {

                    }

                    @Override
                    public void onClickBackView(int position) {
                    }

                    @Override
                    public void onDismiss(int[] reverseSortedPositions) {

                    }

                });

        //mLstVwMaterialList.setSwipeMode(SwipeListView.SWIPE_MODE_DEFAULT); // there

        messageSwipelst.setSwipeMode(SwipeListView.SWIPE_MODE_DEFAULT);

        if(SplashII.wrk_tid >=4)
            messageSwipelst.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS);
        else
            messageSwipelst.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_NONE); // there

        if(SplashII.wrk_tid >=6)
            messageSwipelst.setSwipeActionRight(SwipeListView.SWIPE_ACTION_DISMISS);
        else
            messageSwipelst.setSwipeActionRight(SwipeListView.SWIPE_ACTION_NONE);

        messageSwipelst.setOffsetLeft(convertDpToPixel()); // left side offset
        messageSwipelst.setOffsetRight(convertDpToPixel()); // right side
        // offset
        messageSwipelst.setSwipeCloseAllItemsWhenMoveList(true);
        messageSwipelst.setAnimationTime(100); // Animation time
        messageSwipelst.setSwipeOpenOnLongPress(false); // enable or disable
        // SwipeOpenOnLongPress
    }

    public int convertDpToPixel() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
		/*
		 * float px = dp * (metrics.densityDpi / 160f); return (int) px;
		 */
        return metrics.widthPixels;
    }


    @Override
    public void onActionOk(int requestCode) {
        // handle ok event here
    }

    @Override
    public void onActionCancel(int requestCode) {

    }

    @Override
    public void onActionNeutral(int requestCode) {

    }
}
