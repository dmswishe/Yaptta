package edu.ece.ncsu.unofficial.yaptta.core.conduits;

import edu.ece.ncsu.unofficial.yaptta.core.callbacks.IMessageReceivedCallback;
import edu.ece.ncsu.unofficial.yaptta.core.messages.AbstractMessage;

public class UnicastConduit extends AbstractConduit {

	@Override
	public void sendMessage(AbstractMessage message) throws ConduitException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isListening() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startListening(IMessageReceivedCallback callback)
			throws ConduitException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopListening() {
		// TODO Auto-generated method stub
		
	}


}
