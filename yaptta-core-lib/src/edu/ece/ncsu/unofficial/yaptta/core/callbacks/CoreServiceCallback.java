package edu.ece.ncsu.unofficial.yaptta.core.callbacks;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.annotation.TargetApi;
import android.net.rtp.AudioCodec;
import android.net.rtp.AudioStream;
import android.net.rtp.RtpStream;
import android.os.Build;
import edu.ece.ncsu.unofficial.yaptta.core.YapttaState;
import edu.ece.ncsu.unofficial.yaptta.core.conduits.ConduitException;
import edu.ece.ncsu.unofficial.yaptta.core.conduits.MulticastConduit;
import edu.ece.ncsu.unofficial.yaptta.core.messages.AbstractMessage;
import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.GroupListRequest;
import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.JoinGroupRequest;
import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.MasterConnectRequest;
import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.PeersRequest;
import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.PingRequest;
import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.RequestGroupsRequest;
import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.UpdatePeerRequest;
import edu.ece.ncsu.unofficial.yaptta.core.messages.responses.JoinGroupResponse;
import edu.ece.ncsu.unofficial.yaptta.core.messages.responses.RequestGroupsResponse;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class CoreServiceCallback implements edu.ece.ncsu.unofficial.yaptta.core.callbacks.IMessageReceivedCallback {

	private MulticastConduit coreConduitRef = null;
	
	public CoreServiceCallback(MulticastConduit ref) {
		this.coreConduitRef = ref;
	}
	
	@Override
	public void messageReceived(AbstractMessage message) {
		
		// Filter the message through the following sieve to act on it appropriately
		if(false) {}
		
		// Generic requests
		else if(PingRequest.class.isInstance(message)) processPingRequest(message);
		
		// Group and peer handling
		else if(PeersRequest.class.isInstance(message)) processPeersRequest(message);
		else if(UpdatePeerRequest.class.isInstance(message)) processUpdatePeerRequest(message);
		else if(RequestGroupsRequest.class.isInstance(message)) processRequestGroupsRequest(message);
		else if(RequestGroupsResponse.class.isInstance(message)) processRequestGroupsResponse((RequestGroupsResponse)message);
		else if(JoinGroupRequest.class.isInstance(message)) processJoinGroupRequest((JoinGroupRequest)message);
		else if(MasterConnectRequest.class.isInstance(message)) processMasterConnectRequest((MasterConnectRequest)message);
		
//		
//		String filler;
//		if(PingRequest.class.isInstance(response)) {
//			filler = ((PingRequest)response).getText();
//		} else {
//			filler = response.getClass().getCanonicalName();
//		}
//		
//		final String msg = "Received: " + filler;
//		toastHandler.post(new Runnable(){public void run(){Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();}});
	}

	private void processMasterConnectRequest(MasterConnectRequest message) {
		if(YapttaState.isGroupMaster() && YapttaState.isInGroup()) {
			if(message.getGroupName().equals(YapttaState.getGroupName())) {
				AudioStream clientStream = null;
				try {
					clientStream = new AudioStream(InetAddress.getByAddress(new byte[] {0,0,0,0}));
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				// popUp("Local Port" + myAudioStream.getLocalPort());
				// popUp("" + audioStream.getLocalAddress());
				 
				clientStream.setCodec(AudioCodec.PCMU);
				clientStream.setMode(RtpStream.MODE_NORMAL);
				
				// myAudioStream.associate(InetAddress.getByAddress(new byte[] {(byte)192, (byte)168, (byte)0, (byte)2 }), 51468);
				clientStream.associate(message.getSender(), message.getPort());
				 
				clientStream.join(YapttaState.myAudioGroup);
				
				YapttaState.clientAudioStreams.add(clientStream);
			}
		}
	}

	private void processJoinGroupRequest(JoinGroupRequest message) {
		if(YapttaState.isGroupMaster() && YapttaState.isInGroup()) {
			// Check authentication
			if(YapttaState.isGroupPrivate()) {
				JoinGroupResponse jgr = new JoinGroupResponse(message.getFromDeviceId());
				
				// Check the password
				if(YapttaState.getGroupPassword().equals(message.getPassword())) {
					jgr.setAuthenticated(true);
				} else {
					jgr.setAuthenticated(false);
				}
				
				// Send out with the auth status
				try {
					YapttaState.getCoreConduit().sendMessage(jgr);
				} catch (ConduitException e) {
					e.printStackTrace();
				}
				
			} else {
				// Open group, everyone gets in
				JoinGroupResponse jgr = new JoinGroupResponse(message.getFromDeviceId());
				jgr.setAuthenticated(true);
				try {
					YapttaState.getCoreConduit().sendMessage(jgr);
				} catch (ConduitException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void processRequestGroupsRequest(AbstractMessage response) {
		// If this device is hosting a group, respond back with the necessary info
		if(YapttaState.isGroupMaster() && YapttaState.isInGroup()) {
			RequestGroupsResponse rgr = new RequestGroupsResponse();
			rgr.setGroupName(YapttaState.getGroupName());
			rgr.setPort(0); // port doesn't matter due to RTP right now
			rgr.setPrivate(YapttaState.isGroupPrivate());
			try {
				this.coreConduitRef.sendMessage(rgr);
			} catch (ConduitException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void processRequestGroupsResponse(RequestGroupsResponse response) {
		YapttaState.addKnownGroup(response.getGroupName(), response.getPort(), response.isPrivate());
	}

	private void processUpdatePeerRequest(AbstractMessage response) {
		// TODO Auto-generated method stub
		
	}

	private void processPeersRequest(AbstractMessage response) {
		// TODO Auto-generated method stub
		
	}

	private void processPingRequest(AbstractMessage m) {
		
	}

}
