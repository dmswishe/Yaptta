package edu.ece.ncsu.unofficial.yaptta.core.conduits;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import edu.ece.ncsu.unofficial.yaptta.core.YapttaConstants;
import edu.ece.ncsu.unofficial.yaptta.core.callbacks.IMessageReceivedCallback;
import edu.ece.ncsu.unofficial.yaptta.core.messages.AbstractMessage;

/**
 * Conduit which implements UDP multicast functionality
 */
public class MulticastConduit extends AbstractConduit {
	
	private WifiManager wm;
	private MulticastLock lock;
	private MulticastSocket socket;
	private InetAddress groupAddress;
	private int groupPort;
	
	private MulticastListenerThread listenerThreadInstance;
	private Thread listenerThreadMaster;
	
	/**
	 * Creates a new multicast conduit.
	 * @param context Can be null during testing, but required if running as an Android app.
	 * @param port Port number on which the conduit should listen.
	 */
	public MulticastConduit(Context context, int port)
	{
		try
		{
			// Latch the constructor parameters
			this.groupPort = port;
			this.groupAddress = InetAddress.getByName(YapttaConstants.Network.MULTICAST_ADDRESS);
			
			// Enable multicast functionality if running in Android
			if(context != null)
			{
				wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
				lock = wm.createMulticastLock("yaptta-" + this.groupPort);
				lock.acquire();
			}
		
			// Create the multicast socket and join the group
			socket = new MulticastSocket(this.groupPort);
			socket.setSoTimeout(YapttaConstants.Network.BLOCKING_TIMEOUT);
			socket.setTrafficClass(YapttaConstants.Network.IPTOS_LOWDELAY);
			socket.joinGroup(this.groupAddress);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			if(socket == null && lock != null) {
				lock.release();
			}
		}
	}
	
	/**
	 * Creates a new multicast conduit for testing purposes (do not use in an application).
	 * @param port Port number on which the conduit should listen.
	 */
	public MulticastConduit(int port)
	{
		this(null, port);
	}
	
	public int getPort() {
		return this.groupPort;
	}
	
	@Override
	public void sendMessage(AbstractMessage request) throws ConduitException {
		try {
			// Serialize the message
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(request);
			oos.flush();
			
			// Wrap it up into a packet and send it off
			byte[] bytes = baos.toByteArray();
			final DatagramPacket packet = new DatagramPacket(bytes, bytes.length, this.groupAddress, this.groupPort);
			Thread t = new Thread(new Runnable(){
				// Need to do this in a new thread to avoid the NetworkOnMainThreadException
				@Override
				public void run() {
					try {
						socket.send(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}});
			t.start();
			
			// Clean-up
			oos.close();
			baos.close();
			
		} catch (Exception e) {
			throw new ConduitException("Unable to send message.", e);
		}
	}

	/**
	 * Determines if the conduit is monitoring the channel for new messages.
	 */
	public boolean isListening() {
		if(listenerThreadMaster != null) {
			return listenerThreadMaster.isAlive();
		} else {
			return false;
		}
	}
	
	@Override
	public void setMessageReceivedCallback(IMessageReceivedCallback callback) {
		if(isListening()) {
			listenerThreadInstance.setCallback(callback);
		}
	}

	@Override
	public void startListening(IMessageReceivedCallback callback) throws ConduitException {
		if(!isListening()) {
			listenerThreadInstance = new MulticastListenerThread(socket, callback);
			listenerThreadMaster = new Thread(listenerThreadInstance);
			listenerThreadMaster.start();			
		}
		else {
			// throw new ConduitException("Already listening.");
			setMessageReceivedCallback(callback);
		}
	}
	
	/**
	 * Tells the conduit to halt its listening thread.
	 */
	public void stopListening() {
		if(isListening()) listenerThreadInstance.markForHalt();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if(socket != null && socket.isConnected()) socket.close();
		if(lock != null) lock.release();
	}

}
