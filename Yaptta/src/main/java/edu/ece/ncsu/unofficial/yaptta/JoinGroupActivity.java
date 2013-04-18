package edu.ece.ncsu.unofficial.yaptta;

import edu.ece.ncsu.unofficial.yaptta.core.YapttaState;
import edu.ece.ncsu.unofficial.yaptta.core.conduits.ConduitException;
import edu.ece.ncsu.unofficial.yaptta.core.messages.requests.RequestGroupsRequest;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class JoinGroupActivity extends ListActivity {
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    
    YapttaState.clearKnownGroups();
    try {
		YapttaState.getCoreConduit().sendMessage(new RequestGroupsRequest());
	} catch (ConduitException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
    String[] values = new String[] { "group 1", "group 2", "group 3", "group 4" };
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_1, values);
    setListAdapter(adapter);
    
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    String item = (String) getListAdapter().getItem(position);
    Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
  }
} 
