package edu.ece.ncsu.unofficial.yaptta.core.transports;

import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.AbstractRequest;

public abstract class AbstractTransport implements ITransport {
	
	protected MessageReceivedCallback msgRecvCallback = null;

	@Override
	public abstract void sendMessage(AbstractRequest request);

	@Override
	public void setMessageReceivedCallback(MessageReceivedCallback callback) {
		this.msgRecvCallback = callback;
	}

	@Override
	public MessageReceivedCallback getMessageReceivedCallback() {
		return this.msgRecvCallback;
	}

}
