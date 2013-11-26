/**
 * Description: A very basic activity to hold the settingsFragment
 * 
 * @authors Michael Patterson, Thomas Powell
 */

package mines.edu.activities;

import mines.edu.fragments.MessageFragment;
import mines.edu.fragments.SettingsFragment;
import mines.edu.patterson_powell_trailtracker.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class SettingsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.basic, menu);
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
		// show a message fragment with the String parameter and a dismiss button
		Bundle args = new Bundle();
		args.putString("message", message);

		MessageFragment frag = new MessageFragment(); 
		frag.setArguments(args);
		frag.show(getFragmentManager(), "Message");
	}
}
