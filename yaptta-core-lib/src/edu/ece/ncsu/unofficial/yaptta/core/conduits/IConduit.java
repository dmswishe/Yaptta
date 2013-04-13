package edu.ece.ncsu.unofficial.yaptta.core.conduits;

import edu.ece.ncsu.unofficial.yaptta.core.messages.AbstractMessage;

public interface IConduit {
		
	public void sendMessage(AbstractMessage message) throws ConduitException;
	public void setMessageReceivedCallback(IMessageReceivedCallback callback);
	public IMessageReceivedCallback getMessageReceivedCallback();

	public boolean isListening();
	public void startListening(IMessageReceivedCallback callback) throws ConduitException;
	public void stopListening();
	
}
