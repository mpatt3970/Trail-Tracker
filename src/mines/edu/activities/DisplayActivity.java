package mines.edu.activities;

import mines.edu.fragments.MessageFragment;
import mines.edu.fragments.MyMapFragment;
import mines.edu.patterson_powell_trailtracker.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

public class DisplayActivity extends Activity {

	private String name;
	private GoogleMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);
		
		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		
		((MyMapFragment)getFragmentManager().findFragmentById(R.id.mapFragment)).update(name);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection for action bar
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent settings = new Intent(this, SettingsActivity.class);
			startActivity(settings);
			return super.onOptionsItemSelected(item);
        case R.id.action_help:
        	showMessage(getResources().getString(R.string.help_text));
			return super.onOptionsItemSelected(item);
        case R.id.action_about:
        	showMessage(getResources().getString(R.string.about_text));
			return super.onOptionsItemSelected(item);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	
	public void showMessage(String message) {
		Bundle args = new Bundle();
		args.putString("message", message);
		
		MessageFragment frag = new MessageFragment(); 
		frag.setArguments(args);
		frag.show(getFragmentManager(), "Message");
	}
	
}
