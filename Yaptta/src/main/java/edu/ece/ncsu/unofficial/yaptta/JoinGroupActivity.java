package edu.ece.ncsu.unofficial.yaptta;

import edu.ece.ncsu.unofficial.yaptta.core.YapttaState;
import edu.ece.ncsu.unofficial.yaptta.core.callbacks.SimpleMessageReceivedCallback;
import edu.ece.ncsu.unofficial.yaptta.core.conduits.ConduitException;
import edu.ece.ncsu.unofficial.yaptta.core.messages.AbstractMessage;
import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.RequestGroupsRequest;
import edu.ece.ncsu.unofficial.yaptta.core.messages.responses.RequestGroupsResponse;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class JoinGroupActivity extends ListActivity {
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
    String item = (String) getListAdapter().getItem(position);
    Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
  }
} 
