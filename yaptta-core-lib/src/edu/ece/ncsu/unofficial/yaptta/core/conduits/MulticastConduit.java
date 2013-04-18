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

public class MulticastConduit extends AbstractConduit {
	
	private WifiManager wm;
	private MulticastLock lock;
	private MulticastSocket socket;
	private InetAddress groupAddress;
	private int groupPort;
	
	private MulticastListenerThread listenerThreadInstance;
	private Thread listenerThreadMaster;
	
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
				@Override
				public void run() {
					try {
						socket.send(packet);
					} catch (IOException e) {
						// TODO Auto-generated catch block
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

	public boolean isListening() {
		if(listenerThreadMaster != null) {
			return listenerThreadMaster.isAlive();
		} else {
			return false;
		}
	}
	
	@Override
	public void startListening(IMessageReceivedCallback callback) throws ConduitException {
		if(!isListening()) {
			listenerThreadInstance = new MulticastListenerThread(socket, callback);
			listenerThreadMaster = new Thread(listenerThreadInstance);
			listenerThreadMaster.start();			
		}
		else throw new ConduitException("Already listening.");
	}
	
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
