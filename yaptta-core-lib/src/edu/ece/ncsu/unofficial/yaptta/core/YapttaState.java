package edu.ece.ncsu.unofficial.yaptta.core;

import java.util.ArrayList;
import java.util.List;

import edu.ece.ncsu.unofficial.yaptta.core.conduits.MulticastConduit;

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

}
