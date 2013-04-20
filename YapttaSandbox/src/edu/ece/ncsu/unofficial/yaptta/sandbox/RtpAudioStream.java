package edu.ece.ncsu.unofficial.yaptta.sandbox;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.net.rtp.*;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RtpAudioStream extends Activity{

	
	private AudioStream myAudioStream;
	private AudioStream[] myAudioStreams;
	private int numOfStreams = 0; //don't cross the streams!!
	private int maxStreams = 10;
    private AudioGroup myAudioGroup;
    private AudioManager myAudioManager;
    private int destPort = 51678;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rtpstream);
		myAudioStreams = new AudioStream[maxStreams]; //hard code make users 10
		myAudioGroup = new AudioGroup();
		myAudioGroup.setMode(AudioGroup.MODE_ECHO_SUPPRESSION);
	}
	
	/*Set the mode of the AudioStream
	 * set before connect
	 * uses MODE_NORMAL, MODE_SEND_ONLY,MODE_RECEIVE_ONLY
	 */
	public void setMode(int mode){
		myAudioStream.setMode(mode);
	}
	
	/*Mutes the current Stream
	 * mute = true Mutes the stream, False un-mutes
	 */
	public void setMute(boolean mute){
		if(mute)
			myAudioGroup.setMode(AudioGroup.MODE_MUTED);
		else
			myAudioGroup.setMode(AudioGroup.MODE_ECHO_SUPPRESSION);
	}
	
	public void addNewStream(String thisIP){
		if(numOfStreams == maxStreams) 
			return;
		
		try {
			InetAddress destAddress = (InetAddress.getAllByName(thisIP))[0];
			myAudioStreams[numOfStreams] = new AudioStream((InetAddress.getAllByName("127.0.0.1"))[0]);
			myAudioStreams[numOfStreams].setCodec(AudioCodec.PCMU);
			myAudioStreams[numOfStreams].setMode(RtpStream.MODE_NORMAL);
			myAudioStreams[numOfStreams].associate(destAddress, destPort);
			myAudioStreams[numOfStreams].join(myAudioGroup);
			numOfStreams++;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private void printDebug(){
		EditText tmpText = (EditText)findViewById(R.id.editTextLocalPortDisplay);
		tmpText.setText("" + myAudioStream.getLocalPort());
		
		tmpText = (EditText)findViewById(R.id.editTextDestPortDisplay);
		tmpText.setText("" + myAudioStream.getRemotePort());
		
	}
	
	public void connect(View view){
		popUp("trying to Connect");
		String sDestAddress;
		EditText tmpText = (EditText)findViewById(R.id.editTextOct1);		
		sDestAddress = tmpText.getText().toString();
		
		tmpText = (EditText)findViewById(R.id.editTextOct2);	
		sDestAddress = sDestAddress + "." + tmpText.getText().toString();
		
		tmpText = (EditText)findViewById(R.id.editTextOct3);	
		sDestAddress = sDestAddress + "." + tmpText.getText().toString();
		
		tmpText = (EditText)findViewById(R.id.editTextOct4);	
		sDestAddress = sDestAddress + "." + tmpText.getText().toString();
		
		tmpText = (EditText)findViewById(R.id.editTextDestPort);
		destPort = Integer.parseInt(tmpText.getText().toString());
		
		//popUp(sDestAddress);
		try {
			InetAddress destAddress = (InetAddress.getAllByName(sDestAddress))[0];
			
			myAudioManager =  (AudioManager) getSystemService(Context.AUDIO_SERVICE); 
			myAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
			myAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, 0);
			myAudioManager.setMicrophoneMute(false);
			myAudioManager.setSpeakerphoneOn(true);
			 
			
			 
			myAudioStream = new AudioStream(InetAddress.getByAddress(new byte[] {(byte)0, (byte)0, (byte)0, (byte)0 }));
			 
			// popUp("Local Port" + myAudioStream.getLocalPort());
			// popUp("" + audioStream.getLocalAddress());
			 
			 myAudioStream.setCodec(AudioCodec.PCMU);
			 myAudioStream.setMode(RtpStream.MODE_NORMAL);
			
			// myAudioStream.associate(InetAddress.getByAddress(new byte[] {(byte)192, (byte)168, (byte)0, (byte)2 }), 51468);
			 myAudioStream.associate(destAddress, destPort);
			 
			 myAudioStream.join(myAudioGroup);
			printDebug();
			
			
			
		} catch(SocketException e){
			 e.printStackTrace();
	     } catch (UnknownHostException e) {
	         e.printStackTrace();
	     } catch (Exception ex) {
	         ex.printStackTrace();
	     }
		
	}
	
	
	
	
	
	 private void popUp(CharSequence text){
		 Context context = getApplicationContext();
		 Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	 }
	
	
	
	
	
}



