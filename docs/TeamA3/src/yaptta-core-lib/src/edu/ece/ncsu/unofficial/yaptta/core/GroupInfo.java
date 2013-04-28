package edu.ece.ncsu.unofficial.yaptta.core;

/**
 * Data structure for holding information about a group.
 */
public class GroupInfo {
	private String name;
	private int port;
	private boolean isPrivate;
	
	public GroupInfo(String groupName, int port, boolean isPrivate) {
		this.name = groupName;
		this.port = port;
		this.isPrivate = isPrivate;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean isPrivate() {
		return isPrivate;
	}
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
}
