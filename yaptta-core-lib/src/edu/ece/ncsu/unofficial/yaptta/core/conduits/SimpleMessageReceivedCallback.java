package edu.ece.ncsu.unofficial.yaptta.core.conduits;

import edu.ece.ncsu.unofficial.yaptta.core.messages.AbstractMessage;

public abstract class SimpleMessageReceivedCallback implements IMessageReceivedCallback {
	public abstract void messageReceived(AbstractMessage response);
}
