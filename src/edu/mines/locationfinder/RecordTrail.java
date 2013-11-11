package edu.mines.locationfinder;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


// looked at http://android-developers.blogspot.com/2011/06/deep-dive-into-location.html for clarification on the PendingIntent
// and this question:  http://stackoverflow.com/questions/1990855/android-how-to-get-location-information-from-intent-bundle-extras-when-using-lo
public class RecordTrail extends Activity {

	private static long minTime = 60000; // in milliseconds. the minimum time to wait before updating location again
	private static float minDistance = 300; // in meters. the minimum distance to go before updating location

	private LocationManager locationManager;
	private Location location;

	private TextView hikeName;


	private String name;
	private String latitude;
	private String longitude;
	private String timeStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_trail);

		Intent intent = getIntent();

		name = intent.getStringExtra("name");

		hikeName = (TextView)findViewById(R.id.hike_name);
		hikeName.setText(name);

		this.locationManager = (LocationManager)this.getSystemService( Context.LOCATION_SERVICE );
	}

	@Override
	public void onStart() {
		super.onStart();
		updateLocation();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.record_options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection for action bar
		switch (item.getItemId()) {
		case R.id.action_save:
			// do something
		case R.id.action_camera:
			//do something else
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void updateLocation() {
		//get the location and add it to the text view "display"
		//added a uses-permission statement to the manifest
		//added implements locationlistener
		//added a locationManager and a location as instance variables

		Intent activeIntent = new Intent("edu.mines.locationfinder.LOCATION_READY");
		activeIntent.putExtra("name", name);
		
		PendingIntent locationListenerPendingIntent = PendingIntent.getBroadcast(this, 0, activeIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		// Register the listener with the Location Manager to receive location updates.
		this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListenerPendingIntent);

	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
