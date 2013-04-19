package edu.ece.ncsu.unofficial.yaptta.core.messages;

import java.io.Serializable;

import edu.ece.ncsu.unofficial.yaptta.core.YapttaState;

public abstract class AbstractMessage implements Serializable {
	private static final long serialVersionUID = 6393442260025558318L;

	private String fromDeviceId = YapttaState.getDeviceName();
	
	public String getFromDeviceId(){
		return this.fromDeviceId;
	}
	
	public void setFromDeviceId(String fromDeviceId) {
		this.fromDeviceId = fromDeviceId;
	}
}
