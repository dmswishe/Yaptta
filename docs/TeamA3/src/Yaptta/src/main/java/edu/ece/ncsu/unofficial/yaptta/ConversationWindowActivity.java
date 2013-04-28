package edu.ece.ncsu.unofficial.yaptta;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import edu.ece.ncsu.unofficial.yaptta.R;
import edu.ece.ncsu.unofficial.yaptta.core.YapttaConstants;
import edu.ece.ncsu.unofficial.yaptta.core.YapttaState;
import edu.ece.ncsu.unofficial.yaptta.core.conduits.ConduitException;
import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.MasterConnectRequest;

import android.media.AudioManager;
import android.net.rtp.AudioCodec;
import android.net.rtp.AudioGroup;
import android.net.rtp.AudioStream;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class ConversationWindowActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation_window);

		// Since the user is reconnecting, unjoin any streams and remove any references that happen to still exist
		for(AudioStream as : YapttaState.clientAudioStreams) {
			as.join(null);
			as.release();
			as = null;
		}
		YapttaState.clientAudioStreams.clear();

		// Setup stuff pertaining to the UI ...
		setupUi();

		// ... then go on to establish the RTP audio connection
		setupRtpPrereqs();
	}

	/**
	 * Setup the connection for use of the RTP library functions
	 */
	private void setupRtpPrereqs() {
		// Establish some basic settings
		YapttaState.myAudioManager =  (AudioManager) getSystemService(Context.AUDIO_SERVICE); 
		YapttaState.myAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
		YapttaState.myAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, 0);
		YapttaState.myAudioManager.setMicrophoneMute(false);
		YapttaState.myAudioManager.setSpeakerphoneOn(true);

		// Create the new RTP AudioGroup 
		if(YapttaState.myAudioGroup == null) {
			YapttaState.myAudioGroup = new AudioGroup();
		} else {
			// if it was already created, make sure any old references are gone
			YapttaState.myAudioGroup.clear();
		}
		YapttaState.myAudioGroup.setMode(AudioGroup.MODE_ECHO_SUPPRESSION);

		// Attempt to create the local AudioStream
		try {
			// Bind the stream to any interface
			YapttaState.myAudioStream = new AudioStream(InetAddress.getByAddress(new byte[]{0,0,0,0}));

			// Use the default codec
			YapttaState.myAudioStream.setCodec(YapttaConstants.Network.AUDIO_CODEC);

			// Allow duplex communication
			YapttaState.myAudioStream.setMode(AudioStream.MODE_NORMAL);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		// setup the streams differently if acting as master
		if(YapttaState.isGroupMaster()) {

			// Since we're the master, we should just associate locally since there's nothing to connect to at the moment
			try {
				YapttaState.myAudioStream.associate(InetAddress.getByName("0.0.0.0"), YapttaState.getGroupHostPort());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}

			// Update the generated local port so that join requests get the right port later on
			YapttaState.setGroupHostPort(YapttaState.myAudioStream.getLocalPort());

		} else {
			// Otherwise, we're a client, so go ahead and setup the connection to the group master
			YapttaState.myAudioStream.associate(YapttaState.getGroupHostAddress(), YapttaState.getGroupHostPort());

			// Now tell the group master to connect to me (since another random local port was created from creation of the new AudioStream above)
			MasterConnectRequest mcr = new MasterConnectRequest(YapttaState.getGroupName(), YapttaState.myAudioStream.getLocalPort());
			try {
				YapttaState.getCoreConduit().sendMessage(mcr);
			} catch (ConduitException e) {
				e.printStackTrace();
			}			
		}

		// Now that the AudioStream is setup and associated (whether master or slave), go ahead and join the hub
		YapttaState.myAudioStream.join(YapttaState.myAudioGroup);
		setMute(true); // mute until the PTT button is held
	}

	/**
	 * Initialize UI components with helpful information about the current state of the connection.
	 */
	private void setupUi() {

		// Determine what our IP address is on the WiFi network
		WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		InetAddress wifiIp = null;
		try {
			byte[] ipBytes = BigInteger.valueOf(wm.getConnectionInfo().getIpAddress()).toByteArray();
			byte temp;
			temp = ipBytes[0]; ipBytes[0] = ipBytes[3]; ipBytes[3] = temp;
			temp = ipBytes[1]; ipBytes[1] = ipBytes[2]; ipBytes[2] = temp;
			wifiIp = InetAddress.getByAddress(ipBytes);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		// Build up a string to give us more info about our connection
		String statusText = "status";
		if(wifiIp == null) {
			statusText =  "<localhost> in " + YapttaState.getGroupName();
		} else {
			InetAddress hostAddress = YapttaState.getGroupHostAddress();
			statusText =  wifiIp.getHostAddress() + " in " + YapttaState.getGroupName() + " (" + ((hostAddress != null) ? hostAddress.getHostAddress() : "master") + ")";
		}
		((TextView)findViewById(R.id.textSpeakingIn)).setText(statusText);

		// Attach the handler for the PTT button
		Button pttBtn = (Button) findViewById(R.id.pttBtn);
		pttBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Basically, unmute the mic when pressed, mute when released 
				switch( event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						setMute(false);
						break;
					case MotionEvent.ACTION_UP:
						setMute(true);
						break;
				}
				return false;
			}
		});
	}

	/**
	 * Set the muting status of the AudioStream
	 * @param mute Boolean value indicating if the stream should be muted (true) or not (false). 
	 */
	public void setMute(boolean mute){
		if(mute)
			YapttaState.myAudioGroup.setMode(AudioGroup.MODE_MUTED);
		else
			YapttaState.myAudioGroup.setMode(AudioGroup.MODE_ECHO_SUPPRESSION);
	}

	
	/**
	 * Override the operation of the back button and run the same code as the "Leave" button.
	 */
	@Override
	public void onBackPressed() {
		onLeaveClick(getCurrentFocus());
	}
	
	/**
	 * Handler for when the Leave button is clicked.
	 * @param view
	 */
	public void onLeaveClick(View view) {
		// Stop all our audio streaming
		YapttaState.resetAudioHub(this);
		
		// Leave the group
		YapttaState.resetGroupMembership();
		
		// Go back to main screen (and don't let us go back to this one)
		Intent i = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(i);
		finish();
	}
}
