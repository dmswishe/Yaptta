package edu.ece.ncsu.unofficial.yaptta.core.callbacks;

import edu.ece.ncsu.unofficial.yaptta.core.messages.AbstractMessage;

/**
 * Simple class meant to be overridden with just-in-time message handling.
 */
public abstract class SimpleMessageReceivedCallback implements IMessageReceivedCallback {
	public abstract void messageReceived(AbstractMessage response);
}
