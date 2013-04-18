package edu.ece.ncsu.unofficial.yaptta.core.conduits;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

import edu.ece.ncsu.unofficial.yaptta.core.YapttaConstants;
import edu.ece.ncsu.unofficial.yaptta.core.callbacks.IMessageReceivedCallback;
import edu.ece.ncsu.unofficial.yaptta.core.messages.AbstractMessage;

public class MulticastListenerThread implements Runnable {
	
	private IMessageReceivedCallback callback;
	
	public IMessageReceivedCallback getCallback() {
		return callback;
	}

	public void setCallback(IMessageReceivedCallback callback) {
		this.callback = callback;
	}

	private final MulticastSocket socket;
	
	private volatile Boolean haltRequested = false; 
	
	public MulticastListenerThread(MulticastSocket socket, IMessageReceivedCallback callback) {
		this.callback = callback;
		this.socket = socket;
	}
	
	public void markForHalt()
	{
		synchronized(haltRequested) {
			haltRequested = true;
		}
	}
	
	@Override
	public void run() {
		// Construct a buffer for us to work with
		byte[] buffer = new byte[YapttaConstants.Network.DEFAULT_PACKET_BUFFER_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		// Loop until we're disconnected/stopped/etc
		while(!haltRequested && socket != null && socket.isBound() && !socket.isClosed())
		{
			try {
				// Block the socket until data is received
				socket.receive(packet);
				
				// Deserialize the data
				ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
				ObjectInputStream ois = new ObjectInputStream(bais);
				AbstractMessage am = (AbstractMessage) ois.readObject();
				
				// Call the data received handler as appropriate
				if(callback != null) callback.messageReceived(am);
				
				// Clean-up
				ois.close();
				bais.close();
				
				// ... then do it again!
				
			} catch (SocketTimeoutException e) {
				// just try to roll with the error (but at least log it)... it's likely due to
				// disconnection and will end the thread when it checks the condition of the loop
			} catch (Exception e) {
				// if something other than timeout occurred, give more info
				e.printStackTrace();
			}
		}
	}
}
