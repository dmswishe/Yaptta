package edu.ece.ncsu.unofficial.yaptta.sandbox;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Handler;
import android.widget.Toast;

public class SenderThread implements Runnable {

	private Activity a = null;
	private static final String groupName = "224.27.69.5";
	private static final int groupPort = 27695;
	private byte[] buffer = null;
	private Handler toastHandler = null;
	
	public SenderThread(Activity activity, byte[] buffer, Handler toastHandler) {
		this.a = activity;
		this.buffer = buffer;
		this.toastHandler = toastHandler;
	}
	
	@Override
	public void run() {
	
		final Context context = a.getApplicationContext();
		
		try {
			WifiManager wm = (WifiManager)a.getSystemService(Context.WIFI_SERVICE);
			MulticastLock lock = wm.createMulticastLock("yaptta");
			lock.acquire();
			
			// Setup the socket
			java.net.MulticastSocket socket = new MulticastSocket(groupPort);
			
			// Prepare the data
			InetAddress group = InetAddress.getByName(groupName);
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, groupPort);
			
			// Send it out and clean up
			socket.send(packet);
			socket.close();
			
			lock.release();
			
			final CharSequence text2 = "Sent: " + new String(buffer);
			toastHandler.post(new Runnable(){public void run(){Toast.makeText(context, text2, Toast.LENGTH_SHORT).show();}});
		} catch (Exception e) {
			final CharSequence text3 = "ERROR: " + e.toString();
			toastHandler.post(new Runnable(){public void run(){Toast.makeText(context, text3, Toast.LENGTH_SHORT).show();}});
		}
		
	}
}
