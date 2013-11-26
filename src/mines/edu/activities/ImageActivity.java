/**
 * Description: This is a very basic class to display an image as the main view
 * It receives an intent with a byte array, which holds a bitmap
 * 
 * @authors Michael Patterson, Thomas Powell
 */

package mines.edu.activities;

import mines.edu.fragments.MessageFragment;
import mines.edu.patterson_powell_trailtracker.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class ImageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);

		Intent intent = getIntent();
		byte[] picture = intent.getByteArrayExtra("image");

		ImageView image = (ImageView) findViewById(R.id.current_image);
		image.setImageBitmap(BitmapFactory.decodeByteArray(picture, 0, picture.length));
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
