package edu.ece.ncsu.unofficial.yaptta.core.conduits;

import edu.ece.ncsu.unofficial.yaptta.core.callbacks.IMessageReceivedCallback;

public abstract class AbstractConduit implements IConduit {
	
	protected IMessageReceivedCallback msgRecvCallback = null;

	@Override
	public void setMessageReceivedCallback(IMessageReceivedCallback callback) {
		this.msgRecvCallback = callback;
	}

	@Override
	public IMessageReceivedCallback getMessageReceivedCallback() {
		return this.msgRecvCallback;
	}

}
