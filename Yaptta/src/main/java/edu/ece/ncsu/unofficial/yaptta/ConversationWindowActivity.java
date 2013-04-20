package edu.ece.ncsu.unofficial.yaptta;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import edu.ece.ncsu.unofficial.yaptta.R;
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

		YapttaState.clientAudioStreams.clear();
		
		setupUiStatus();
		setupRtpPrereqs();
	}

	private void setupRtpPrereqs() {
		YapttaState.myAudioManager =  (AudioManager) getSystemService(Context.AUDIO_SERVICE); 
		YapttaState.myAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
		YapttaState.myAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, 0);
		YapttaState.myAudioManager.setMicrophoneMute(false);
		YapttaState.myAudioManager.setSpeakerphoneOn(true);

		YapttaState.myAudioGroup = new AudioGroup();
		YapttaState.myAudioGroup.setMode(AudioGroup.MODE_ECHO_SUPPRESSION);

		try {
			YapttaState.myAudioStream = new AudioStream(InetAddress.getByAddress(new byte[]{0,0,0,0}));
			YapttaState.myAudioStream.setCodec(AudioCodec.PCMU);
			YapttaState.myAudioStream.setMode(AudioStream.MODE_NORMAL);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		// setup the streams differently if acting as master
		if(YapttaState.isGroupMaster()) {
			// we're good for now, don't need to do anything
			try {
				YapttaState.myAudioStream.associate(InetAddress.getByName("0.0.0.0"), YapttaState.getGroupHostPort());
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Set the port here so that join requests get the right port later on
			YapttaState.setGroupHostPort(YapttaState.myAudioStream.getLocalPort());
			
		} else {
			// Setup the connection to the group master
			YapttaState.myAudioStream.associate(YapttaState.getGroupHostAddress(), YapttaState.getGroupHostPort());
			
			// Now tell the group master to connect to me
			MasterConnectRequest mcr = new MasterConnectRequest(YapttaState.getGroupName(), YapttaState.myAudioStream.getLocalPort());
			try {
				YapttaState.getCoreConduit().sendMessage(mcr);
			} catch (ConduitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		YapttaState.myAudioStream.join(YapttaState.myAudioGroup);
	}

	private void setupUiStatus() {
		// Update the UI to reflect the group we're in
		WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		InetAddress wifiIp = null;
		try {
			byte[] ipBytes = BigInteger.valueOf(wm.getConnectionInfo().getIpAddress()).toByteArray();
			byte temp;
			temp = ipBytes[0]; ipBytes[0] = ipBytes[3]; ipBytes[3] = temp;
			temp = ipBytes[1]; ipBytes[1] = ipBytes[2]; ipBytes[2] = temp;
			wifiIp = InetAddress.getByAddress(ipBytes);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String statusText;
		if(wifiIp == null) {
			statusText =  "Unknown in " + YapttaState.getGroupName();
		} else {
			InetAddress hostAddress = YapttaState.getGroupHostAddress();
			statusText =  wifiIp.getHostAddress() + " in " + YapttaState.getGroupName() + " (" + ((hostAddress != null) ? hostAddress.getHostAddress() : "unknown") + ")";
		}
		((TextView)findViewById(R.id.textSpeakingIn)).setText(statusText);
		
		// Attach the handler for the PTT button
		final Button pttBtn = (Button) findViewById(R.id.pttBtn);
		pttBtn.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//CheckBox tmpBox = (CheckBox)findViewById(R.id.checkBoxPTTStatus);
				switch( event.getAction()){
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

	/*Mutes the current Stream
	 * mute = true Mutes the stream, False un-mutes
	 */
	public void setMute(boolean mute){
		if(mute)
			YapttaState.myAudioGroup.setMode(AudioGroup.MODE_MUTED);
		else
			YapttaState.myAudioGroup.setMode(AudioGroup.MODE_ECHO_SUPPRESSION);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.conversation_window, menu);
		return true;
	}

}
