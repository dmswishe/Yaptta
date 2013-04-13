package edu.ece.ncsu.unofficial.yaptta.sandbox;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;
import edu.ece.ncsu.unofficial.yaptta.core.messages.AbstractMessage;
import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.PingRequest;

public class ToastCallback implements edu.ece.ncsu.unofficial.yaptta.core.conduits.IMessageReceivedCallback {

	private Handler toastHandler;
	private Context context;
	
	public ToastCallback(Context context, Handler toastHandler) {
		this.context = context;
		this.toastHandler = toastHandler;
	}
	
	@Override
	public void messageReceived(AbstractMessage response) {
		
		String filler;
		if(PingRequest.class.isInstance(response)) {
			filler = ((PingRequest)response).getText();
		} else {
			filler = response.getClass().getCanonicalName();
		}
		
		final String msg = "Received: " + filler;
		toastHandler.post(new Runnable(){public void run(){Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();}});
	}

}
