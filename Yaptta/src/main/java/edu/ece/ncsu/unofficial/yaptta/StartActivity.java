package edu.ece.ncsu.unofficial.yaptta;

import edu.ece.ncsu.unofficial.yaptta.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_group);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_group, menu);
		return true;
	}
	public void onDiscoverPeersClick (View view) {
		Context context = getApplicationContext();
    	CharSequence text = "Discover Peers Button clicked!";
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show(); 
    	Intent i = new Intent(getApplicationContext(), DiscoverPeersActivity.class);
    	startActivity(i);
	}
}
