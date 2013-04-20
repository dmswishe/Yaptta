package edu.ece.ncsu.unofficial.yaptta.core.messages.requests;

/**
 * Debugging data structure.
 */
public class PingRequest extends AbstractMulticastRequest {

	private static final long serialVersionUID = 6656483902221394212L;

	private String text;
	
	public PingRequest() {
		super();
	}
	
	public PingRequest(String text)
	{
		super();
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

}
