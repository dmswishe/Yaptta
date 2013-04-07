package edu.ece.ncsu.unofficial.yaptta.util;

import java.net.*;

public class Runner {
	
	private static final String groupName = "224.27.69.5";
	private static final int groupPort = 27695;
	
	public static void main(String[] args)
	{
		try {
			String message = "This is just a test";
			byte[] messageAsBytes = message.getBytes();
			
			// Setup the socket
			java.net.MulticastSocket socket = new MulticastSocket(groupPort);
			
			// Prepare the data
			InetAddress group = InetAddress.getByName(groupName);
			DatagramPacket packet = new DatagramPacket(messageAsBytes, messageAsBytes.length, group, groupPort);
			
			// Send it out and clean up
			socket.send(packet);
			socket.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
