package com.aceroute.mobile.software.validation;

import com.google.gson.annotations.SerializedName;

public class Vcontrols {
	
	
	@SerializedName("control_id")
	String control_id;
	
	@SerializedName("mandatory")
	String mandatory;
	
	@SerializedName("min_length")
	String min_length;
	
	@SerializedName("max_length")
	String max_length;
	
	@SerializedName("type")
	String type;
	
	@SerializedName("Validate")
	String Validate;

	@SerializedName("errorMsg")
	String errorMsg;
	
	
	
	
	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getValidate() {
		return Validate;
	}

	public void setValidate(String validate) {
		Validate = validate;
	}

	public String getControl_id() {
		return control_id;
	}

	public void setControl_id(String control_id) {
		this.control_id = control_id;
	}

	public String getMandatory() {
		return mandatory;
	}

	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}

	public String getMin_length() {
		return min_length;
	}

	public void setMin_length(String min_length) {
		this.min_length = min_length;
	}

	public String getMax_length() {
		return max_length;
	}

	public void setMax_length(String max_length) {
		this.max_length = max_length;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
