package edu.ece.ncsu.unofficial.yaptta.core.messages.requests;

/**
 * Request from client to master when client wishes to connect to a group. Contains password information.
 */
public class JoinGroupRequest extends AbstractMulticastRequest {

	private static final long serialVersionUID = 5047849620547870084L;
	
	private String password;
	private int port;
	
	public JoinGroupRequest(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
