package edu.ece.ncsu.unofficial.yaptta.core.conduits;

import edu.ece.ncsu.unofficial.yaptta.core.callbacks.IMessageReceivedCallback;
import edu.ece.ncsu.unofficial.yaptta.core.messages.AbstractMessage;

/**
 * Unimplemented.
 */
public class UnicastConduit extends AbstractConduit {

	@Override
	public void sendMessage(AbstractMessage message) throws ConduitException {	
	}

	@Override
	public boolean isListening() {
		return false;
	}

	@Override
	public void startListening(IMessageReceivedCallback callback)
			throws ConduitException {
	}

	@Override
	public void stopListening() {
	}


}
