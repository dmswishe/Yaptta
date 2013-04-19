package edu.ece.ncsu.unofficial.yaptta;

import edu.ece.ncsu.unofficial.yaptta.core.GroupInfo;
import edu.ece.ncsu.unofficial.yaptta.core.YapttaState;
import edu.ece.ncsu.unofficial.yaptta.core.callbacks.SimpleMessageReceivedCallback;
import edu.ece.ncsu.unofficial.yaptta.core.conduits.ConduitException;
import edu.ece.ncsu.unofficial.yaptta.core.messages.AbstractMessage;
import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.JoinGroupRequest;
import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.RequestGroupsRequest;
import edu.ece.ncsu.unofficial.yaptta.core.messages.responses.JoinGroupResponse;
import edu.ece.ncsu.unofficial.yaptta.core.messages.responses.RequestGroupsResponse;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class JoinGroupActivity extends ListActivity {
	
	private Handler toastHandler = new Handler();
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		setListAdapter(adapter);

		YapttaState.clearKnownGroups();
		try {
			// Overwrite the callback with something more relevant to this activity
			YapttaState.getCoreConduit().setMessageReceivedCallback(new SimpleMessageReceivedCallback(){
				@Override
				public void messageReceived(AbstractMessage response) {
					if(RequestGroupsResponse.class.isInstance(response)) {
						final RequestGroupsResponse rgr = (RequestGroupsResponse)response;
						YapttaState.addKnownGroup(rgr.getGroupName(), rgr.getPort(), rgr.isPrivate());
						runOnUiThread(new Runnable(){
							@Override
							public void run() {
								adapter.add(rgr.getGroupName());
								adapter.notifyDataSetChanged();
							}});
					}
				}});

			// Now that the callback is established, go ahead and send out the request
			YapttaState.getCoreConduit().sendMessage(new RequestGroupsRequest());
		} catch (ConduitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// String[] values = new String[] { "group 1", "group 2", "group 3", "group 4" };


	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Get the group the user clicked
		String selectedGroupName = (String) getListAdapter().getItem(position);
		
		// Determine if it requires a password
		for(GroupInfo gi : YapttaState.getKnownGroups()) {
			if(gi.getName().equals(selectedGroupName)) {
				if(gi.isPrivate()) {
					// Ask for a password
					AlertDialog.Builder passwordPrompt = new AlertDialog.Builder(this);
					passwordPrompt.setTitle("Group Password");
					passwordPrompt.setMessage("Please provide the password for the \"" + selectedGroupName + "\"");
					final EditText passwordInput = new EditText(this);
					passwordPrompt.setView(passwordInput);
					passwordPrompt.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Proceed to connect here...
							String inputtedPassword = passwordInput.getText().toString();
							
							final ProgressDialog loading = new ProgressDialog(passwordInput.getContext());
							loading.setCancelable(true);
							loading.setMessage("Authenticating...");
							loading.show();
							
							// Setup the async authentication response when it comes
							YapttaState.getCoreConduit().setMessageReceivedCallback(new SimpleMessageReceivedCallback() {
								@Override
								public void messageReceived(AbstractMessage response) {
									if(JoinGroupResponse.class.isInstance(response)) {
										JoinGroupResponse jgr = (JoinGroupResponse)response;
										if(jgr.getRecipient().equals(YapttaState.getDeviceName())) {
											loading.dismiss();
											
											if(jgr.isAuthenticated()) {
												// Change the activity here (and get rid of the Toast)
												
												runOnUiThread(new Runnable(){
													@Override
													public void run() {
														Toast.makeText(JoinGroupActivity.this, "Authenticated!", Toast.LENGTH_SHORT).show();
													}});
											} else {
												
												runOnUiThread(new Runnable(){
													@Override
													public void run() {
														Toast.makeText(JoinGroupActivity.this, "Cannot join group: Invalid Password", Toast.LENGTH_SHORT).show();
													}});
											}
										}
									}
								}
							});
							
							// Now that the pre-reqs are setup, send the message
							JoinGroupRequest jgr = new JoinGroupRequest(inputtedPassword);
							try {
								YapttaState.getCoreConduit().sendMessage(jgr);
							} catch (ConduitException e) {
								e.printStackTrace();
							}
						}
					});
					passwordPrompt.show();
				} else {
					// Password not required, go ahead and connect
					final ProgressDialog loading = new ProgressDialog(this);
					loading.setCancelable(false);
					loading.setMessage("Connecting...");
					loading.show();
					
					// Setup the async authentication response when it comes
					YapttaState.getCoreConduit().setMessageReceivedCallback(new SimpleMessageReceivedCallback() {
						@Override
						public void messageReceived(AbstractMessage response) {
							if(JoinGroupResponse.class.isInstance(response)) {
								JoinGroupResponse jgr = (JoinGroupResponse)response;
								if(jgr.getRecipient().equals(YapttaState.getDeviceName())) {
									loading.dismiss();
									
									runOnUiThread(new Runnable(){
										@Override
										public void run() {
											Toast.makeText(JoinGroupActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
										}});
								}
							}
						}
					});
					
					// Now that the pre-reqs are setup, send the message
					JoinGroupRequest jgr = new JoinGroupRequest("");
					try {
						YapttaState.getCoreConduit().sendMessage(jgr);
					} catch (ConduitException e) {
						e.printStackTrace();
					}
				}
					 
			}
		}
		// Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();


	}
} 
