package edu.ece.ncsu.unofficial.yaptta.sandbox;

import edu.ece.ncsu.unofficial.yaptta.sandbox.ListenerThread;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	private Handler toastHandler = new Handler();
	//private Runnable toastRunnable = new Runnable() {public void run() {Toast.makeText(Activity.this,...).show();}}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onListenClick(View view)
	{
		byte[] buffer = new byte[1024];
		Thread t = new Thread(new ListenerThread(this, buffer, toastHandler));
		t.start();
	}
	
	public void onSendClick(View view)
	{
		Thread t = new Thread(new SenderThread(this, ((EditText)this.findViewById(R.id.editText1)).getText().toString().getBytes(), toastHandler));
		t.start();
	}
}
