package edu.ece.ncsu.unofficial.yaptta.core.messages.requests;

public class MasterConnectRequest extends AbstractMulticastRequest {

	private static final long serialVersionUID = 5047849620547870084L;
	
	private String groupName;
	private int port;
	
	public MasterConnectRequest(String groupName, int port) {
		this.setGroupName(groupName);
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
