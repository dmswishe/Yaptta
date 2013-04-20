package edu.ece.ncsu.unofficial.yaptta.core.messages.responses;

/**
 * Response to a JoinGroupRequest. Contains information about authentication.
 */
public class JoinGroupResponse extends AbstractMulticastResponse {

	private static final long serialVersionUID = -6908960482480542180L;

	private String recipient;
	private boolean authenticated = false;

	public JoinGroupResponse(String recipient) {
		this.recipient = recipient;
	}
	
	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
	
	
}
