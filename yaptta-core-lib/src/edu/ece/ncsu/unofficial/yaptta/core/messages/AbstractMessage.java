package edu.ece.ncsu.unofficial.yaptta.core.messages;

import java.io.Serializable;
import java.net.InetAddress;

import edu.ece.ncsu.unofficial.yaptta.core.YapttaState;

/**
 * Serializable base class for messages.
 */
public abstract class AbstractMessage implements Serializable {
	private static final long serialVersionUID = 6393442260025558318L;

	// Whenever a message is created, automatically populate it with the current device name
	private String fromDeviceId = YapttaState.getDeviceName();
	private InetAddress sender = null;
	
	public String getFromDeviceId(){
		return this.fromDeviceId;
	}
	
	public void setFromDeviceId(String fromDeviceId) {
		this.fromDeviceId = fromDeviceId;
	}

	public InetAddress getSender() {
		return sender;
	}

	public void setSender(InetAddress sender) {
		this.sender = sender;
	}
}
