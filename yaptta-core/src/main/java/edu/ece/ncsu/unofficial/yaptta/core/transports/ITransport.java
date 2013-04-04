package edu.ece.ncsu.unofficial.yaptta.core.transports;

import edu.ece.ncsu.unofficial.yaptta.core.messages.AbstractMessage;
import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.AbstractRequest;

public interface ITransport {
	
	public static interface MessageReceivedCallback {
		public void messageReceived(AbstractMessage response);		
	};
	
	public void sendMessage(AbstractRequest request);
	public void setMessageReceivedCallback(MessageReceivedCallback callback);
	public MessageReceivedCallback getMessageReceivedCallback();

}
