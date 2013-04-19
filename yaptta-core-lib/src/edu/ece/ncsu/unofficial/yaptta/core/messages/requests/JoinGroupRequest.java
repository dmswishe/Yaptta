package edu.ece.ncsu.unofficial.yaptta.core.messages.requests;

public class JoinGroupRequest extends AbstractMulticastRequest {

	private static final long serialVersionUID = 5047849620547870084L;
	
	private String password;
	
	public JoinGroupRequest(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

}
