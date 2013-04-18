package edu.ece.ncsu.unofficial.yaptta.core.messages.responses;

public class RequestGroupsResponse extends AbstractMulticastResponse {

	private static final long serialVersionUID = -7590731393438802756L;
	
	private String groupName;
	private int port;
	private boolean isPrivate;
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
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
