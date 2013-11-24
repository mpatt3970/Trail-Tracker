package mines.edu.activities;

import java.util.ArrayList;

import mines.edu.database.LocationContentProvider;
import mines.edu.database.LocationObject;
import mines.edu.database.LocationTable;
import mines.edu.fragments.MessageFragment;
import mines.edu.patterson_powell_trailtracker.R;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class TrailActivity extends Activity {

	private static final int MIN_DISTANCE = 5;

	private LocationManager manager;
	private PendingIntent newLocationIntent;
	private ArrayList<LocationObject> list; // store the values in this list. Update the fragments with these values
	private Intent service;
	private Integer minTime;
	private String name;
	private boolean updating;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trail);

		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		updating = intent.getBooleanExtra("new_trail", true);
		// set the name as the text at the top
		((TextView) findViewById(R.id.nameView)).setText(name);
		list = new ArrayList<LocationObject>();

		if (updating) {

			// get the preference for frequency from the "accuracy" setting
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			String settingStr = sharedPrefs.getString("accuracy", "5000");
			minTime = Integer.parseInt(settingStr);
			// get the location manager
			this.manager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
			startRecording();
		} else {
			invalidateOptionsMenu(); // updates action bar by calling onPrepareOptionsMenu
			updateFragments(); // gets the list for this name, and broadcasts to the fragments
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trail, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection for action bar
		switch (item.getItemId()) {
		case R.id.action_save:
			stopRecording();
			return super.onOptionsItemSelected(item);
		case R.id.action_camera:
			Toast.makeText(this, "Camera functionality to be added soon", Toast.LENGTH_LONG).show();
			return super.onOptionsItemSelected(item);
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


	public boolean onPrepareOptionsMenu(Menu menu) {
		if (!updating) {
			menu.removeItem(R.id.action_save);
			menu.removeItem(R.id.action_camera);
		}
		return true;
	}

	public void startRecording() {
		service = new Intent(this, NewLocationService.class);
		service.putExtra("name", name);
		newLocationIntent = PendingIntent.getService(this, 0, service, PendingIntent.FLAG_UPDATE_CURRENT);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		manager.requestLocationUpdates(minTime, MIN_DISTANCE, criteria, newLocationIntent);
		IntentFilter filter = new IntentFilter("NEW_LOCATION");
		registerReceiver(receiver, filter);
	}

	public void stopRecording() {
		invalidateOptionsMenu(); // update the action bar
		updating = false;
		manager.removeUpdates(newLocationIntent);
		unregisterReceiver(receiver);
	}

	public void getLocations() {
		/**
		 * This function gets the list of locations
		 *
		 */
		// open a cursor, get an array of previous names and lats and lons
		list = new ArrayList<LocationObject>();
		String[] projection = { LocationTable.COLUMN_ID, LocationTable.COLUMN_LATITUDE, LocationTable.COLUMN_LONGITUDE, LocationTable.COLUMN_NAME, LocationTable.COLUMN_TIME };
		String[] select = {name};
		Cursor cursor = getContentResolver().query(LocationContentProvider.CONTENT_URI, projection, "name=?", select, null);
		if ((cursor != null) && (cursor.getCount() > 0)) {
			cursor.moveToFirst();
			// only add it if the name matches
			if (cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME)).equals(name)) {
				list.add(new LocationObject(cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME)), cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LATITUDE)), cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LONGITUDE)), cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_TIME))));
			}
			while (cursor.moveToNext()) {
				if (cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME)).equals(name)) {
					list.add(new LocationObject(cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME)), cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LATITUDE)), cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LONGITUDE)), cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_TIME))));
				}
			}
		}
		cursor.close();
	}

	public void showMessage(String message) {
		Bundle args = new Bundle();
		args.putString("message", message);

		MessageFragment frag = new MessageFragment(); 
		frag.setArguments(args);
		frag.show(getFragmentManager(), "Message");
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			updateFragments();
		}

	};
	
	public void updateFragments() {
		// update the list and send a broadcast to fragments
		getLocations();
		Intent broadcast = new Intent();
		broadcast.setAction("UPDATE");
		sendBroadcast(broadcast);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (updating) {

		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (updating) {
			stopRecording();
		}
	}
	
	public ArrayList<LocationObject> getList() {
		return list;
	}
}