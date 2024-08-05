package com.aceroute.mobile.software.async;

import java.io.Serializable;

public interface IActionOKCancel extends Serializable {
	public void onActionOk(int requestCode);

	public void onActionCancel(int requestCode);
	public void onActionNeutral(int requestCode);
}
