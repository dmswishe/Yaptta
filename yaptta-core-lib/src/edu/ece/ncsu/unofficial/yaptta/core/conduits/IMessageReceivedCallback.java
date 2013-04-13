package edu.ece.ncsu.unofficial.yaptta.core.conduits;

import edu.ece.ncsu.unofficial.yaptta.core.messages.AbstractMessage;

public interface IMessageReceivedCallback {
	public void messageReceived(AbstractMessage response);		
};