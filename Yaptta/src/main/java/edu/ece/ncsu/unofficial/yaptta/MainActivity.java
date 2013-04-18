package edu.ece.ncsu.unofficial.yaptta;

import edu.ece.ncsu.unofficial.yaptta.R;
import edu.ece.ncsu.unofficial.yaptta.core.YapttaConstants;
import edu.ece.ncsu.unofficial.yaptta.core.YapttaState;
import edu.ece.ncsu.unofficial.yaptta.core.callbacks.CoreServiceCallback;
import edu.ece.ncsu.unofficial.yaptta.core.conduits.ConduitException;
import edu.ece.ncsu.unofficial.yaptta.core.conduits.MulticastConduit;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize the core conduit
        MulticastConduit coreConduit = new MulticastConduit(YapttaConstants.Network.BROADCAST_PORT);
        YapttaState.setCoreConduit(coreConduit);
        try {
			coreConduit.startListening(new CoreServiceCallback(coreConduit));
		} catch (ConduitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Recall the necessary settings
        SharedPreferences settings = getSharedPreferences(YapttaConstants.UI.PREFERENCE_STORE_NAME, Context.MODE_PRIVATE);
        String deviceName = settings.getString(YapttaConstants.Preferences.DEVICE_NAME, "Yaptta Device");
        YapttaState.setDeviceName(deviceName);
        
        // ... and then update UI elements accordingly
        findEditDeviceName().setText(deviceName);
        
    }
    
    private TextView findEditDeviceName() {
    	return (TextView)findViewById(R.id.editDeviceName);
    }
    
    public void onSaveDeviceName(View view) {
    	SharedPreferences settings = getSharedPreferences(YapttaConstants.UI.PREFERENCE_STORE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(YapttaConstants.Preferences.DEVICE_NAME, findEditDeviceName().getText().toString());

        // Commit the edits!
        editor.commit();
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
    	Intent i = new Intent(getApplicationContext(), JoinGroupActivity.class);
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
    	/*Context context = getApplicationContext();
    	CharSequence text = "Current Convo Button clicked!";
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show(); */
    	Intent i = new Intent(getApplicationContext(), ConversationWindowActivity.class);
    	startActivity(i);
	}
    
}
