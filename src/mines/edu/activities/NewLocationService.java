/**
 * Description: This service receives pending intents with a current location
 * Along with that location, it gets the time, and the current hike's name
 * It bundles all these into ContentValues and saves them to the db
 * Then it broadcasts a message to trailActivity which notifies it to update.
 * It uses an empty byte array for the image since images are handled in TrailActivity
 * 
 * @authors Michael Patterson, Thomas Powell
 */

package mines.edu.activities;


import mines.edu.database.LocationContentProvider;
import mines.edu.database.LocationTable;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.format.Time;

public class NewLocationService extends IntentService {

	//sets a decimal format
	private static java.text.DecimalFormat df = new java.text.DecimalFormat( "0.000000" );

	private String name;
	private String latitude;
	private String longitude;
	private String timeStr;

	public NewLocationService() {
		super("mines.edu.patterson_powell_trailtracker.new_location");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// get the name, time, and location
		Bundle b = intent.getExtras();
		name = b.getString("name");
		Location location = (Location)b.get(android.location.LocationManager.KEY_LOCATION_CHANGED);
		if (location != null) {
			Time now = new Time();
			now.setToNow();
			timeStr = now.format2445();
			latitude = df.format(location.getLatitude());
			longitude = df.format(location.getLongitude());
			saveNewLocation(this);
			updateList();
		}
	}

	public void saveNewLocation(Context context) {
		// store the values of the variables into a ContentValues
		ContentValues values = new ContentValues();
		values.put(LocationTable.COLUMN_NAME, name);
		values.put(LocationTable.COLUMN_LATITUDE, latitude);
		values.put(LocationTable.COLUMN_LONGITUDE, longitude);
		values.put(LocationTable.COLUMN_TIME, timeStr);
		values.put(LocationTable.COLUMN_PHOTO, new byte[0]);
		// store the values in the database
		context.getContentResolver().insert(LocationContentProvider.CONTENT_URI, values);
	}

	public void updateList() {
		// fire a broadcast to trailactivity to update its list.
		// which will trigger maps and stats to update with its own broadcast
		Intent broadcast = new Intent();
		broadcast.setAction("NEW_LOCATION");
		sendBroadcast(broadcast);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}



}
