package edu.ece.ncsu.unofficial.yaptta.core;

import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.AbstractRequest;
import edu.ece.ncsu.unofficial.yaptta.core.messages.responses.AbstractResponse;

public interface IMessageInterface {
	
	public static interface MessageReceivedCallback {
		public void messageReceived(AbstractResponse response);		
	};
	
	public void sendMessage(AbstractRequest request);
	public void setMessageReceivedCallback(MessageReceivedCallback callback);
	public MessageReceivedCallback getMessageReceivedCallback();

}
