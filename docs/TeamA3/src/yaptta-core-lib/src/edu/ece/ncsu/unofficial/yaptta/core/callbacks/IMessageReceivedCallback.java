package edu.ece.ncsu.unofficial.yaptta.core.callbacks;

import edu.ece.ncsu.unofficial.yaptta.core.messages.AbstractMessage;

/**
 * Callback interface for the conduits.
 */
public interface IMessageReceivedCallback {
	public void messageReceived(AbstractMessage response);		
};