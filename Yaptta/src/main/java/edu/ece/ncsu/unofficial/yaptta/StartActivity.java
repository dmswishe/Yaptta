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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_group);
		
		// Make sure we're starting with a "good" core conduit
		YapttaState.resetGroupMembership();
		
		// Generate a useful default group name
		String genGroupName = YapttaState.getDeviceName() + " Group - " + ((int)(Math.random() * 1000));
		findEditGroupName().setText(genGroupName);
	}
	
	/**
	 * Convenience method for accessing the typed group name
	 * @return
	 */
	private TextView findEditGroupName() {
		return (TextView)findViewById(R.id.editGroupName);
	}
	
	/**
	 * Click handler for the Start Group button
	 * @param view
	 * @throws Exception
	 */
	public void onStartGroupClick (View view) throws Exception {
		
		// Set up our application state
		YapttaState.setGroupMaster(true);
		YapttaState.setGroupName(findEditGroupName().getText().toString());
		YapttaState.setInGroup(true);
		
		// The below has been removed since we're now using RTP:
		//// Try to create the new multicast group (try three times to bind on a random port)
		//MulticastConduit groupConduit = null;
		//for(int i = 0; i < 3; i++) {
		//	try {
		//		groupConduit = new MulticastConduit(YapttaConstants.Network.GROUP_PORT_BASE + (int)(Math.random() * YapttaConstants.Network.GROUP_PORT_RANGE));
		//	} catch(Exception ex) {
		//		continue;
		//	}
		//	break;
		//}
		//if(groupConduit == null) throw new Exception("Unable to create new group!");
		//YapttaState.setGroupConduit(groupConduit);
		
		// Check our inputs and update the state appropriately
		YapttaState.setGroupPrivate(((RadioGroup)findViewById(R.id.rdoGroupType)).getCheckedRadioButtonId() == R.id.rdoGroupTypeInvite);
		YapttaState.setGroupPassword(((TextView)findViewById(R.id.editGroupPassword)).getText().toString());
		
		// Now just go to the conversation activity
		Intent i = new Intent(getApplicationContext(), ConversationWindowActivity.class);
		startActivity(i);
		finish();
	}
}
