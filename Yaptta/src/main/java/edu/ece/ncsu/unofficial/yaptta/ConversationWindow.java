package edu.ece.ncsu.unofficial.yaptta;
	import edu.ece.ncsu.unofficial.yaptta.R;

	import android.os.Bundle;
	import android.app.Activity;
	import android.view.Menu;
public class ConversationWindow extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation_window);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.conversation_window, menu);
		return true;
	}

}
