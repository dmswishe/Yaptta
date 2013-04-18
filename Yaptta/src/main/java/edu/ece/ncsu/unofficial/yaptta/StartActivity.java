package edu.ece.ncsu.unofficial.yaptta;

import edu.ece.ncsu.unofficial.yaptta.R;
import edu.ece.ncsu.unofficial.yaptta.core.YapttaConstants;
import edu.ece.ncsu.unofficial.yaptta.core.YapttaState;
import edu.ece.ncsu.unofficial.yaptta.core.conduits.MulticastConduit;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
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
	
	private TextView findEditGroupName() {
		return (TextView)findViewById(R.id.editGroupName);
	}
	
	public void onStartGroupClick (View view) throws Exception {
		
		// Set up our application state
		YapttaState.setGroupMaster(true);
		YapttaState.setGroupName(findEditGroupName().getText().toString());
		YapttaState.setInGroup(true);
		
		// Try to create the new multicast group (try three times to bind on a random port)
		MulticastConduit groupConduit = null;
		for(int i = 0; i < 3; i++) {
			try {
				groupConduit = new MulticastConduit(YapttaConstants.Network.GROUP_PORT_BASE + (int)(Math.random() * YapttaConstants.Network.GROUP_PORT_RANGE));
			} catch(Exception ex) {
				continue;
			}
			break;
		}
		if(groupConduit == null) throw new Exception("Unable to create new group!");
		YapttaState.setGroupConduit(groupConduit);
		
		// Now just go to the conversation activity
		Intent i = new Intent(getApplicationContext(), ConversationWindowActivity.class);
		startActivity(i);
		
//		Context context = getApplicationContext();
//    	CharSequence text = "Discover Peers Button clicked!";
//		Toast.makeText(context, text, Toast.LENGTH_SHORT).show(); 
//    	Intent i = new Intent(getApplicationContext(), DiscoverPeersActivity.class);
//    	startActivity(i);
	}
}
