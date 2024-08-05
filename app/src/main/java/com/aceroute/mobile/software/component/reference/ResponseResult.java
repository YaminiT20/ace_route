package com.aceroute.mobile.software.component.reference;

import android.content.Context;

import com.aceroute.mobile.software.utilities.XMLHandler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ResponseResult {
	
	//xml parent element idetifier

	
	private String response;
	public static String RESPONSE_SUCCESS = "success";
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	private String errorcode = null;
	private String success = "true";
	private Context context = null;
	XMLHandler xmlhandler;
	
	public ResponseResult(Context ctxt) {
		context = ctxt;
		xmlhandler = new XMLHandler(context);
	}
	
	public boolean parse(String response)
	{
		if(response!=null)
		{
			Document doc = xmlhandler.getDomElement(response);
			if(doc != null)
			{
				NodeList nl = doc
					.getElementsByTagName(XMLHandler.KEY_DATA);
				for (int i = 0; i < nl.getLength(); i++) {// loop should not be requiredhere
				Element e = (Element) nl.item(i);
				errorcode = xmlhandler.getValue(e,
						XMLHandler.KEY_DATA_ERROR);
				success = xmlhandler.getValue(e,
						XMLHandler.KEY_DATA_SUCCESS);
				return true;
				}
			}
		}
		return false;
	}
	
	

}
