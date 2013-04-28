package edu.ece.ncsu.unofficial.yaptta.core.test;

import org.junit.Before;
import org.junit.Test;

import edu.ece.ncsu.unofficial.yaptta.core.callbacks.SimpleMessageReceivedCallback;
import edu.ece.ncsu.unofficial.yaptta.core.conduits.ConduitException;
import edu.ece.ncsu.unofficial.yaptta.core.conduits.MulticastConduit;
import edu.ece.ncsu.unofficial.yaptta.core.messages.AbstractMessage;
import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.PingRequest;
import edu.ece.ncsu.unofficial.yaptta.core.YapttaConstants;

public class MulticastConduitTest {
	private MulticastConduit mc;
	
	@Before
	public void TestMulticastInit() throws ConduitException {
		mc = new MulticastConduit(YapttaConstants.Network.BROADCAST_PORT);
	}
	
	@Test
	public void TestMulticastBroadcast() throws ConduitException, InterruptedException {
		mc.startListening(new SimpleMessageReceivedCallback(){
			@Override
			public void messageReceived(AbstractMessage response) {
				System.out.println("Message received: " + response.getClass().getCanonicalName());
			}});
		for(int i = 0; i < 5; i++) {
			System.out.println("Waiting for input...");
			mc.sendMessage(new PingRequest("Running repeated JUnit Test... (" + (i+1) + "/5)"));
			Thread.sleep(1000);
		}
		mc.stopListening();
	}
}
