package edu.ece.ncsu.unofficial.yaptta.core;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.net.rtp.AudioGroup;
import android.net.rtp.AudioStream;
import android.os.Build;
import android.os.Handler;
import android.view.View;

import edu.ece.ncsu.unofficial.yaptta.core.conduits.MulticastConduit;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class YapttaState {
	
	private static YapttaState instance = new YapttaState();
	
	private String deviceName = "Yaptta Device";
	
	private boolean isInGroup = false;
	private boolean isGroupMaster = false;
	private boolean isGroupPrivate = false;
	
	private String groupName = "Yaptta Group";
	private String groupPassword = "password";
	
	private MulticastConduit coreConduit = null;
	private MulticastConduit groupConduit = null;
	
	private InetAddress groupHostAddress;
	private int groupHostPort;
	
	public static AudioGroup myAudioGroup;
	public static AudioStream myAudioStream;
	public static List<AudioStream> clientAudioStreams = new ArrayList<AudioStream>();
	public static AudioManager myAudioManager;

	private List<GroupInfo> knownGroups = new ArrayList<GroupInfo>();
	
	public static MulticastConduit getCoreConduit() {
		return instance.coreConduit;
	}

	public static void setCoreConduit(MulticastConduit coreConduit) {
		instance.coreConduit = coreConduit;
	}

	public static MulticastConduit getGroupConduit() {
		return instance.groupConduit;
	}

	public static void setGroupConduit(MulticastConduit groupConduit) {
		instance.groupConduit = groupConduit;
	}

	public static boolean isInGroup() {
		return instance.isInGroup;
	}

	public static void setInGroup(boolean isInGroup) {
		instance.isInGroup = isInGroup;
	}

	public static boolean isGroupPrivate() {
		return instance.isGroupPrivate;
	}

	public static void setGroupPrivate(boolean isGroupPrivate) {
		instance.isGroupPrivate = isGroupPrivate;
	}
	
	public static boolean isGroupMaster() {
		return instance.isGroupMaster;
	}

	public static void setGroupMaster(boolean isGroupMaster) {
		instance.isGroupMaster = isGroupMaster;
	}

	public static String getGroupName() {
		return instance.groupName;
	}

	public static void setGroupName(String groupName) {
		instance.groupName = groupName;
	}

	public static String getGroupPassword() {
		return instance.groupPassword;
	}

	public static void setGroupPassword(String groupPassword) {
		instance.groupPassword = groupPassword;
	}

	private YapttaState() {
	}
	
	public static String getDeviceName() {
		return instance.deviceName;
	}

	public static void setDeviceName(String deviceName) {
		instance.deviceName = deviceName;
	}
	
	public static void clearKnownGroups() {
		instance.knownGroups = new ArrayList<GroupInfo>();
	}
	
	public static List<GroupInfo> getKnownGroups() {
		return instance.knownGroups;
	}
	
	public static void addKnownGroup(String groupName, int port, boolean isPrivate) {
		// Make sure the group doesn't already exist
		boolean alreadyExists = false;
		for(GroupInfo gi : instance.knownGroups) {
			if(groupName.equals(gi.getName())) alreadyExists = true;
		}
		if(!alreadyExists) instance.knownGroups.add(new GroupInfo(groupName, port, isPrivate));
	}

	public static InetAddress getGroupHostAddress() {
		return instance.groupHostAddress;
	}

	public static void setGroupHostAddress(InetAddress groupHostAddress) {
		instance.groupHostAddress = groupHostAddress;
	}
	
	public static int getGroupHostPort() {
		return instance.groupHostPort;
	}

	public static void setGroupHostPort(int groupHostPort) {
		instance.groupHostPort = groupHostPort;
	}
	
	/**
	 * Configures the audio settings to be ready for a new group session.
	 * @param ref The activity from which this method is being called.
	 */
	public static void resetAudioHub(Activity ref) {
		
		// Close out any audio streams
		for(AudioStream as : YapttaState.clientAudioStreams) {
			as.join(null);
			as.release();
			as = null;
		}
		YapttaState.clientAudioStreams.clear();
		
		// Clear out the group
		if(YapttaState.myAudioGroup != null) YapttaState.myAudioGroup.clear();
		
		// Reset the audio mode
		YapttaState.myAudioManager =  (AudioManager) ref.getSystemService(Context.AUDIO_SERVICE); 
		YapttaState.myAudioManager.setMode(AudioManager.MODE_NORMAL);
	}

	/**
	 * Stops listening on the group conduit and removes any vital flags indicating group membership.
	 */
	public static void resetGroupMembership() {
		YapttaState.setInGroup(false);
		YapttaState.setGroupMaster(false);
		YapttaState.setGroupName(null);
		if(YapttaState.getGroupConduit() != null) YapttaState.getGroupConduit().stopListening();
		YapttaState.setGroupConduit(null);
		
	}

}
