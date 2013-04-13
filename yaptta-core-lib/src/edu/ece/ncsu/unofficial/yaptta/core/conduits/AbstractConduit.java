package edu.ece.ncsu.unofficial.yaptta.core.conduits;

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
