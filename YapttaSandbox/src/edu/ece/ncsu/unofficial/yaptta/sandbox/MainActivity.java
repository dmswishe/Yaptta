package edu.ece.ncsu.unofficial.yaptta.sandbox;

import edu.ece.ncsu.unofficial.yaptta.core.YapttaConstants;
import edu.ece.ncsu.unofficial.yaptta.core.conduits.ConduitException;
import edu.ece.ncsu.unofficial.yaptta.core.conduits.MulticastConduit;
import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.PingRequest;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	private Handler toastHandler = new Handler();
	//private Runnable toastRunnable = new Runnable() {public void run() {Toast.makeText(Activity.this,...).show();}}
	private MulticastConduit broadcastConduit = new MulticastConduit(YapttaConstants.Network.BROADCAST_PORT);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if(!broadcastConduit.isListening())
			try {
				broadcastConduit.startListening(new ToastCallback(this, toastHandler));
			} catch (ConduitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onSendClick(View view) throws ConduitException
	{
		broadcastConduit.sendMessage(new PingRequest(((EditText)this.findViewById(R.id.editText1)).getText().toString()));
	}

}
