package com.aceroute.mobile.software.requests;

import android.os.Parcel;
import android.os.Parcelable;

import com.aceroute.mobile.software.http.RequestObject;

public class SaveTaskDataRequest extends RequestObject implements Parcelable {

public SaveTaskDataRequest() { ; };
	
	public SaveTaskDataRequest(Parcel in) {
		readFromParcel(in); 
	}
	
	String orderId = "";//oid
	String taskId = "";//id
	String taskTypeId = "";//tid
	String treeWorkPresc = ""; //ltid  trim type
	String tree_priority = ""; //pid
	String tree_action_stat = ""; //stat
	
	String tree_owner = ""; //note5;
	String tree_ht = ""; //num1;
	String tree_dia = ""; //num2;
	String tree_clearance = ""; //num3;
	String tree_cycle = ""; //num4;
	String estimated_hrs = ""; //ehrs;
	
	String actual_hrs = ""; //hrs;
	String tree_time_spent = ""; //num5;
	String treeT_M = ""; //num6;
	String tree_Msc = ""; //note6;
	String tree_comment = ""; //note1;
	String tree_presc_comm = ""; //note2;
	
	String tree_Alert = ""; //note3;
	String tree_Notes = ""; //note4;
	String tree_access_comp = "";//ct1;
	String tree_setup_comp = ""; //ct2;
	String tree_pres_comp = ""; //ct3;
	String tree_access_pathLst = ""; //glist;
	
	String tree_geo = "";
	String orderEndTime = "";
	String orderEndDate = "";
	String stmp = "";
	String action = "";
	String upd = "0";
	
	
	public String getUpd() {
		return upd;
	}

	public void setUpd(String upd) {
		this.upd = upd;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskTypeId() {
		return taskTypeId;
	}

	public void setTaskTypeId(String orderTypeId) {
		this.taskTypeId = orderTypeId;
	}

	public String getTreeWorkPresc() {
		return treeWorkPresc;
	}

	public void setTreeWorkPresc(String treeWorkPresc) {
		this.treeWorkPresc = treeWorkPresc;
	}

	public String getTree_priority() {
		return tree_priority;
	}

	public void setTree_priority(String tree_priority) {
		this.tree_priority = tree_priority;
	}

	public String getTree_action_stat() {
		return tree_action_stat;
	}

	public void setTree_action_stat(String tree_action_stat) {
		this.tree_action_stat = tree_action_stat;
	}

	public String getTree_owner() {
		return tree_owner;
	}

	public void setTree_owner(String tree_owner) {
		this.tree_owner = tree_owner;
	}

	public String getTree_ht() {
		return tree_ht;
	}

	public void setTree_ht(String tree_ht) {
		this.tree_ht = tree_ht;
	}

	public String getTree_dia() {
		return tree_dia;
	}

	public void setTree_dia(String tree_dia) {
		this.tree_dia = tree_dia;
	}

	public String getTree_clearance() {
		return tree_clearance;
	}

	public void setTree_clearance(String tree_clearance) {
		this.tree_clearance = tree_clearance;
	}

	public String getTree_cycle() {
		return tree_cycle;
	}

	public void setTree_cycle(String tree_cycle) {
		this.tree_cycle = tree_cycle;
	}

	public String getEstimated_hrs() {
		return estimated_hrs;
	}

	public void setEstimated_hrs(String estimated_hrs) {
		this.estimated_hrs = estimated_hrs;
	}

	public String getActual_hrs() {
		return actual_hrs;
	}

	public void setActual_hrs(String actual_hrs) {
		this.actual_hrs = actual_hrs;
	}

	public String getTree_time_spent() {
		return tree_time_spent;
	}

	public void setTree_time_spent(String tree_time_spent) {
		this.tree_time_spent = tree_time_spent;
	}

	public String getTreeT_M() {
		return treeT_M;
	}

	public void setTreeT_M(String treeT_M) {
		this.treeT_M = treeT_M;
	}

	public String getTree_Msc() {
		return tree_Msc;
	}

	public void setTree_Msc(String tree_Msc) {
		this.tree_Msc = tree_Msc;
	}

	public String getTree_comment() {
		return tree_comment;
	}

	public void setTree_comment(String tree_comment) {
		this.tree_comment = tree_comment;
	}

	public String getTree_presc_comm() {
		return tree_presc_comm;
	}

	public void setTree_presc_comm(String tree_presc_comm) {
		this.tree_presc_comm = tree_presc_comm;
	}

	public String getTree_Alert() {
		return tree_Alert;
	}

	public void setTree_Alert(String tree_Alert) {
		this.tree_Alert = tree_Alert;
	}

	public String getTree_Notes() {
		return tree_Notes;
	}

	public void setTree_Notes(String tree_Notes) {
		this.tree_Notes = tree_Notes;
	}

	public String getTree_access_comp() {
		return tree_access_comp;
	}

	public void setTree_access_comp(String tree_access_comp) {
		this.tree_access_comp = tree_access_comp;
	}

	public String getTree_setup_comp() {
		return tree_setup_comp;
	}

	public void setTree_setup_comp(String tree_setup_comp) {
		this.tree_setup_comp = tree_setup_comp;
	}

	public String getTree_pres_comp() {
		return tree_pres_comp;
	}

	public void setTree_pres_comp(String tree_pres_comp) {
		this.tree_pres_comp = tree_pres_comp;
	}

	public String getTree_access_pathLst() {
		return tree_access_pathLst;
	}

	public void setTree_access_pathLst(String tree_access_pathLst) {
		this.tree_access_pathLst = tree_access_pathLst;
	}

	public String getTree_geo() {
		return tree_geo;
	}

	public void setTree_geo(String tree_geo) {
		this.tree_geo = tree_geo;
	}

	public String getOrderEndTime() {
		return orderEndTime;
	}

	public void setOrderEndTime(String orderEndTime) {
		this.orderEndTime = orderEndTime;
	}

	public String getOrderEndDate() {
		return orderEndDate;
	}

	public void setOrderEndDate(String orderEndDate) {
		this.orderEndDate = orderEndDate;
	}

	public String getStmp() {
		return stmp;
	}

	public void setStmp(String stmp) {
		this.stmp = stmp;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	
	
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		 parcel.writeString(orderId);  
		 parcel.writeString(taskId); 
		 parcel.writeString(taskTypeId); 
		 parcel.writeString(treeWorkPresc);  
		 parcel.writeString(tree_priority); 
		 parcel.writeString(tree_action_stat); 
		 
		 parcel.writeString(tree_owner);  
		 parcel.writeString(tree_ht); 
		 parcel.writeString(tree_dia); 
		 parcel.writeString(tree_clearance);  
		 parcel.writeString(tree_cycle); 
		 parcel.writeString(estimated_hrs); 
		
		 
		 parcel.writeString(actual_hrs);  
		 parcel.writeString(tree_time_spent); 
		 parcel.writeString(treeT_M); 
		 parcel.writeString(tree_Msc); 
		 parcel.writeString(tree_comment);  
		 parcel.writeString(tree_presc_comm); 
		 
		 parcel.writeString(tree_Alert);  
		 parcel.writeString(tree_Notes); 
		 parcel.writeString(tree_access_comp); 
		 parcel.writeString(tree_setup_comp); 
		 parcel.writeString(tree_pres_comp);  
		 parcel.writeString(tree_access_pathLst);
		 
		 parcel.writeString(tree_geo); 
		 parcel.writeString(orderEndTime); 
		 parcel.writeString(orderEndDate); 
		 parcel.writeString(stmp);  
		 parcel.writeString(action);
	}
	
	
	public static final Creator CREATOR = new Creator() {
		
		public SaveTaskDataRequest createFromParcel(Parcel in) {
			return new SaveTaskDataRequest(in); 
			} 
		
		public SaveTaskDataRequest[] newArray(int size) { 
			return new SaveTaskDataRequest[size];
			} 
		}; 
	
	
	private void readFromParcel(Parcel in) {
		
		orderId = in.readString();  
		taskId = in.readString();  
		taskTypeId = in.readString();  
		treeWorkPresc = in.readString();  
		tree_priority = in.readString();  
		tree_action_stat = in.readString();  
		
		tree_owner = in.readString();  
		tree_ht = in.readString();  
		tree_dia = in.readString();  
		tree_clearance = in.readString();  
		tree_cycle = in.readString();  
		estimated_hrs = in.readString();  
		
		actual_hrs = in.readString();  
		tree_time_spent = in.readString();  
		treeT_M = in.readString();  
		tree_Msc = in.readString();  
		tree_comment = in.readString();  
		tree_presc_comm = in.readString();  
		
		tree_Alert = in.readString();  
		tree_Notes = in.readString();  
		tree_access_comp = in.readString();  
		tree_setup_comp = in.readString();  
		tree_pres_comp = in.readString();  
		tree_access_pathLst = in.readString();  
		
		tree_geo = in.readString();  
		orderEndTime = in.readString();  
		orderEndDate = in.readString();  
		stmp = in.readString();  
		action = in.readString();  
	}
	
}
/*{ "url": "http://acerouteqa.appspot.com/mobi",
"type": "post",
"data": {
    "oid": "24198002",
    "id": "0",
    "tid": "21788003",
    "ltid": "22808003",
    "pid": "1",
    "stat": "0",
    "note5": "",
    "num1": "",
    "num2": "",
    "num3": "",
    "num4": "",
    "ehrs": "0",
    "hrs": "0",
    "num5": "",
    "num6": "",
    "note6": "",
    "note1": "",
    "note2": "",
    "note3": "",
    "note4": "",
    "ct1": "1",
    "ct2": "1",
    "ct3": "1",
    "glist": "",
    "geo": "",
    "orderEndTime": "1430390100000",
    "orderEndDate": "2015/04/30 10:35 -00:00",
    "stmp": "1430393626961",
    "action": "saveordertask"
}
}*/