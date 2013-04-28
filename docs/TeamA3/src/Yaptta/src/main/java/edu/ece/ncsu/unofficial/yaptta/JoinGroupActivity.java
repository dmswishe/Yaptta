package edu.ece.ncsu.unofficial.yaptta;

import edu.ece.ncsu.unofficial.yaptta.core.GroupInfo;
import edu.ece.ncsu.unofficial.yaptta.core.YapttaState;
import edu.ece.ncsu.unofficial.yaptta.core.callbacks.SimpleMessageReceivedCallback;
import edu.ece.ncsu.unofficial.yaptta.core.conduits.ConduitException;
import edu.ece.ncsu.unofficial.yaptta.core.conduits.MulticastConduit;
import edu.ece.ncsu.unofficial.yaptta.core.messages.AbstractMessage;
import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.JoinGroupRequest;
import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.RequestGroupsRequest;
import edu.ece.ncsu.unofficial.yaptta.core.messages.responses.JoinGroupResponse;
import edu.ece.ncsu.unofficial.yaptta.core.messages.responses.RequestGroupsResponse;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class JoinGroupActivity extends ListActivity {

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// Setup the reference of the array data to the displayed list
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);
		setListAdapter(adapter);

		// Clear out the running total of groups detected
		YapttaState.clearKnownGroups();
		
		// Now try the networking ops
		try {
			// First setup our callback for handling the group information responses
			YapttaState.getCoreConduit().setMessageReceivedCallback(new SimpleMessageReceivedCallback(){
				@Override
				public void messageReceived(AbstractMessage response) {
					
					// Only target RequestGroups responses
					if(RequestGroupsResponse.class.isInstance(response)) {
						
						// Update the displayed list with the most recent response added to the end
						final RequestGroupsResponse rgr = (RequestGroupsResponse)response;
						YapttaState.addKnownGroup(rgr.getGroupName(), rgr.getPort(), rgr.isPrivate());
						
						// Need to run in the UI thread so that the list draws appropriately
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
			e.printStackTrace();
		}

	}

	/**
	 * Click handler for when one of the entries in the list is clicked
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Get the group the user clicked
		final String selectedGroupName = (String) getListAdapter().getItem(position);

		// Find the GroupInfo related to the group name clicked
		for(GroupInfo thisGi : YapttaState.getKnownGroups()) {
			final GroupInfo gi = thisGi;

			if(gi.getName().equals(selectedGroupName)) {
				// Now, we need to check for authentication
				
				if(gi.isPrivate()) {
					// Ask for a password
					AlertDialog.Builder passwordPrompt = new AlertDialog.Builder(this);
					passwordPrompt.setTitle("Group Password");
					passwordPrompt.setMessage("Please provide the password for the \"" + selectedGroupName + "\"");
					final EditText passwordInput = new EditText(this);
					passwordPrompt.setView(passwordInput);
					passwordPrompt.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
						// Setup the authenticated click handler
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Proceed to connect...
							
							final String inputtedPassword = passwordInput.getText().toString();

							// Present a dialog to let the user know something is going on (should be quick)
							final ProgressDialog loading = new ProgressDialog(passwordInput.getContext());
							loading.setCancelable(true);
							loading.setMessage("Authenticating...");
							loading.show();

							// Setup the async authentication response when it comes
							YapttaState.getCoreConduit().setMessageReceivedCallback(new SimpleMessageReceivedCallback() {
								@Override
								public void messageReceived(AbstractMessage response) {
									// Filter out the packet type of interest
									if(JoinGroupResponse.class.isInstance(response)) {
										JoinGroupResponse jgr = (JoinGroupResponse)response;
										
										// Make sure the packet was intended for us
										if(jgr.getRecipient().equals(YapttaState.getDeviceName())) {
											// We got the response we're looking for, so close the loading window
											loading.dismiss();

											// Did we pass muster?
											if(jgr.isAuthenticated()) {
												// Make sure our state is properly set
												YapttaState.setGroupName(selectedGroupName);
												YapttaState.setInGroup(true);
												YapttaState.setGroupMaster(false);
												YapttaState.setGroupPassword(inputtedPassword);
												YapttaState.setGroupHostAddress(jgr.getSender());

												// Attempt to setup the conduit
												try {
													// Because we're now using RTP, the following was no longer necessary:
													//// Setup the conduit now, but we'll adjust the callback in the new activity
													//YapttaState.setGroupConduit(new MulticastConduit(gi.getPort()));

													// Now switch to the conversation
													Intent i = new Intent(getApplicationContext(), ConversationWindowActivity.class);
													startActivity(i);
													finish();
												} catch(Exception ex) {
													// There was a problem, so let the user know
													runOnUiThread(new Runnable(){
														@Override
														public void run() {
															Toast.makeText(JoinGroupActivity.this, "Error! Unable to initialize connection to group.", Toast.LENGTH_SHORT).show();
														}});
												}
											} else {
												// Let the user know they supplied the wrong password
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

							// Now that the pre-reqs (postive button click handler above) are setup, send the message
							JoinGroupRequest jgr = new JoinGroupRequest(inputtedPassword);
							try {
								YapttaState.getCoreConduit().sendMessage(jgr);
							} catch (ConduitException e) {
								e.printStackTrace();
							}
						}
					});
					
					// Now that everything else is setup, finally ask for the password (above code will be invoked as appropriate)
					passwordPrompt.show();
				} else {
					// Password not required, go ahead and connect
					final ProgressDialog loading = new ProgressDialog(this);
					loading.setCancelable(true);
					loading.setMessage("Connecting...");
					loading.show();

					// Setup the async authentication response when it comes
					YapttaState.getCoreConduit().setMessageReceivedCallback(new SimpleMessageReceivedCallback() {
						@Override
						public void messageReceived(AbstractMessage response) {
							if(JoinGroupResponse.class.isInstance(response)) {
								JoinGroupResponse jgr = (JoinGroupResponse)response;
								// Make sure this was addressed to us
								if(jgr.getRecipient().equals(YapttaState.getDeviceName())) {
									// Close the loading box
									loading.dismiss();

									// Make sure our state is properly set
									YapttaState.setGroupName(selectedGroupName);
									YapttaState.setInGroup(true);
									YapttaState.setGroupMaster(false);
									YapttaState.setGroupPassword("");
									YapttaState.setGroupHostAddress(jgr.getSender());

									// Attempt to setup the conduit
									try {
										// Because we're now using RTP, the following was no longer necessary:
										//// Setup the conduit now, but we'll adjust the callback in the new activity
										//YapttaState.setGroupConduit(new MulticastConduit(gi.getPort()));

										// Now switch to the conversation
										Intent i = new Intent(getApplicationContext(), ConversationWindowActivity.class);
										startActivity(i);
										finish();
									} catch(Exception ex) {
										// Let the user know there was a problem
										runOnUiThread(new Runnable(){
											@Override
											public void run() {
												Toast.makeText(JoinGroupActivity.this, "Error! Unable to initialize connection to group.", Toast.LENGTH_SHORT).show();
											}});
									}
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
	}
} 
