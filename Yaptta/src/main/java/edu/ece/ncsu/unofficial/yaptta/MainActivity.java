package edu.ece.ncsu.unofficial.yaptta;

import edu.ece.ncsu.unofficial.yaptta.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

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
    public void onSetupClick (View view) {
    	Context context = getApplicationContext();
    	CharSequence text = "Setup Button clicked!";
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();  
    	//Intent i = new Intent(getApplicationContext(), DeviceSettingsActivity.class);
    	//startActivity(i);
	}
    public void onJoinClick (View view) {
    	Intent i = new Intent(getApplicationContext(), JoinActivity.class);
    	startActivity(i);
	}
    public void onStartClick (View view) {
    	Intent i = new Intent(getApplicationContext(), StartActivity.class);
    	startActivity(i);
	}
    public void onAboutClick (View view) {
    	Intent i = new Intent(getApplicationContext(), AboutActivity.class);
    	startActivity(i); 
	}
    public void onCurrConvoClick (View view) {
    	Context context = getApplicationContext();
    	CharSequence text = "Current Convo Button clicked!";
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();  
	}
}
