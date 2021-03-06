/**
 * Description: This activity displays and can record a trail
 * It holds a list of locationobjects representing all points from the given hike
 * It updates the fragments by broadcasting to them after updating the list
 * 
 * @authors Michael Patterson, Thomas Powell
 */

package mines.edu.activities;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import mines.edu.database.LocationContentProvider;
import mines.edu.database.LocationObject;
import mines.edu.database.LocationTable;
import mines.edu.fragments.MessageFragment;
import mines.edu.patterson_powell_trailtracker.R;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class TrailActivity extends Activity {

	public static final String PREF = "MyPrefsFile"; //filename for the shared preferences file
	SharedPreferences settings; 

	private static java.text.DecimalFormat df = new java.text.DecimalFormat( "0.000000" );
	private static final int MIN_DISTANCE = 5;

	private LocationManager manager;
	private PendingIntent newLocationIntent;
	private ArrayList<LocationObject> list; // store the values in this list. Update the fragments with these values
	private Intent service;
	private Integer minTime;
	private String name;
	private boolean updating;
	private byte[] picBitMap;

	private static final int CAMERA_PIC_REQUEST = 1337; 


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trail);

		// get the name from the intent and whether this will be recording or just displaying
		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		// set the name as the text at the top
		((TextView) findViewById(R.id.nameView)).setText(name);
		list = new ArrayList<LocationObject>();

		
		settings = getSharedPreferences(PREF, 0);
		if (settings.contains("updating")) {
			updating = settings.getBoolean("updating", false);
		}
		if (updating) {
			// get the preference for frequency from the "accuracy" setting
			// a little misleading since the hardware gets the final say in how frequently locations can be polled
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			String settingStr = sharedPrefs.getString("accuracy", "10000");
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
		if (updating) {
			getMenuInflater().inflate(R.menu.trail, menu);
		} else {
			getMenuInflater().inflate(R.menu.basic, menu);
		}
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection for action bar
		switch (item.getItemId()) {
		case R.id.action_save:
			updating = false;
			stopRecording();
			return super.onOptionsItemSelected(item);
		case R.id.action_camera:
			Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);  
			startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// called when the camera returns an image.
		// Turns the image into a byte array and gets the last known location
		// Saves the image, the time, the location, and name together in the db
		if (requestCode == CAMERA_PIC_REQUEST) { 
			if (data != null && data.hasExtra("data")) {
				Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bos);
				picBitMap = bos.toByteArray();
				// get the most recent location to start
				Location recent = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (recent == null) {
					recent = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); // try getting from another provider
				} if (recent != null) {
					// if a location is valid, then save it in the db
					Time now = new Time();
					now.setToNow();
					String timeStr = now.format2445();
					String latitude = df.format(recent.getLatitude());
					String longitude = df.format(recent.getLongitude());
					ContentValues values = new ContentValues();
					values.put(LocationTable.COLUMN_NAME, name);
					values.put(LocationTable.COLUMN_LATITUDE, latitude);
					values.put(LocationTable.COLUMN_LONGITUDE, longitude);
					values.put(LocationTable.COLUMN_TIME, timeStr);
					values.put(LocationTable.COLUMN_PHOTO, picBitMap);
					// store the values in the database
					getContentResolver().insert(LocationContentProvider.CONTENT_URI, values);
					updateFragments();
				} else {
					showMessage("Failed to connect a connection with the image. Try moving and taking a new image");
				}
			}
		}
	}

	public void startRecording() {
		// begin requesting periodic location updates and send the pending intents to NewLocationService
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
		// general cleanup
		
		// reset the settings to reflect the new bool
		
		unregisterReceiver(receiver);
		invalidateOptionsMenu(); // re create the menu
		newLocationIntent.cancel();
		Log.d("yo", "stopped");
		
	}

	public void getLocations() {
		/**
		 * This function gets the list of locations
		 *
		 */
		// open a cursor, get an array of previous names, lats, lons, times, and images
		// put every item associated with this name into a list of location objects
		list = new ArrayList<LocationObject>();
		String[] projection = { LocationTable.COLUMN_ID, LocationTable.COLUMN_LATITUDE, LocationTable.COLUMN_LONGITUDE, LocationTable.COLUMN_NAME, LocationTable.COLUMN_TIME, LocationTable.COLUMN_PHOTO };
		String[] select = {name};
		Cursor cursor = getContentResolver().query(LocationContentProvider.CONTENT_URI, projection, "name=?", select, null);
		if ((cursor != null) && (cursor.getCount() > 0)) {
			cursor.moveToFirst();
			// only add it if the name matches
			if (cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME)).equals(name)) {
				list.add(new LocationObject(cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME)), cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LATITUDE)), cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LONGITUDE)), cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_TIME)), cursor.getBlob(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_PHOTO))));
			}
			while (cursor.moveToNext()) {
				if (cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME)).equals(name)) {
					list.add(new LocationObject(cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME)), cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LATITUDE)), cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LONGITUDE)), cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_TIME)), cursor.getBlob(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_PHOTO))));
				}
			}
		}
		cursor.close();
	}

	public void showMessage(String message) {
		// show a message fragment with the String parameter and a dismiss button
		Bundle args = new Bundle();
		args.putString("message", message);

		MessageFragment frag = new MessageFragment(); 
		frag.setArguments(args);
		frag.show(getFragmentManager(), "Message");
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// update when a new location broadcast is received
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
		settings = getSharedPreferences(PREF, 0);
		if (settings.contains("updating")) {
			updating = settings.getBoolean("updating", false);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		settings = getSharedPreferences(PREF, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("updating", updating);
		editor.commit();
	}

	@Override
	public void onDestroy() {
		// perform cleanup
		super.onDestroy();
		if (updating) {
			stopRecording();
		}
		
		settings = getSharedPreferences(PREF, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("updating", updating);
		editor.commit();
	}


	public ArrayList<LocationObject> getList() {
		// a getter for the list so the fragments can access it
		return list;
	}
}
