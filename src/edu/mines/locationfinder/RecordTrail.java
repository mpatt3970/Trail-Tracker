package edu.mines.locationfinder;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class RecordTrail extends Activity implements LocationListener {
	//sets a decimal format
	private static java.text.DecimalFormat df = new java.text.DecimalFormat( "0.000000" );

	private LocationManager locationManager;
	private Location location;

	private String name;
	private String latitude;
	private String longitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_trail);
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
			//do something
		case R.id.action_camera:
			//do something else
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void saveLocation() {
		//store the values of the variables into a ContentValues
		ContentValues values = new ContentValues();
		values.put(LocationTable.COLUMN_NAME, name);
		values.put(LocationTable.COLUMN_LATITUDE, latitude);
		values.put(LocationTable.COLUMN_LONGITUDE, longitude);

		//create a new Uri
		getContentResolver().insert(LocationContentProvider.CONTENT_URI, values);
		//show the updated list
		

	}
	
	public void updateLocation() {
		//get the location and add it to the text view "display"
		//added a uses-permission statement to the manifest
		//added implements locationlistener
		//added a locationManager and a location as instance variables

		// Register the listener with the Location Manager to receive location updates.
		this.locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, this );

	}
	
	
	//Required for implements locationlistener
		// Called when a new location is found by the network location provider.
		public void onLocationChanged( Location location )
		{
			// Remove the listener since we only want one location update.
			this.locationManager.removeUpdates( this );

			// Save the location object to pass to the find activity.
			this.setLocation(location);

			latitude = df.format( location.getLatitude() );
			longitude = df.format( location.getLongitude() );

			
		}

		//some functions that have to be implemented for location services

		public void onStatusChanged( String provider, int status, Bundle extras )
		{
		}

		public void onProviderEnabled( String provider )
		{
		}

		public void onProviderDisabled( String provider )
		{
		}
		
		public void setLocation(Location location) {
			this.location = location;
		}
}
