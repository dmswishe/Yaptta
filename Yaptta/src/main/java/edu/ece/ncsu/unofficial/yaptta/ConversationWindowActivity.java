package edu.ece.ncsu.unofficial.yaptta;
	import edu.ece.ncsu.unofficial.yaptta.R;
import edu.ece.ncsu.unofficial.yaptta.core.YapttaState;

	import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;
public class ConversationWindowActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation_window);
		
		// Update the UI to reflect the group we're in
		((TextView)findViewById(R.id.textSpeakingIn)).setText("Speaking in " + YapttaState.getGroupName());
		
		// Setup the new callbacks for the groupConduit and coreConduit here (since they were overwritten while negotiating the connection)...
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.conversation_window, menu);
		return true;
	}

}
