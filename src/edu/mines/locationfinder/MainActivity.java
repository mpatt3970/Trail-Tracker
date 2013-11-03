/**
 * Description: The "Main Activity" screen, in which the user enters a name
 * for the location and clicks "Update the Location" to find his current GPS 
 * coordinates. Then can click "Save this Location" to save that location
 * to a database. The contents of the database can be viewed on the LocationList
 * screen that can be accessed by clicking "My Locations."
 * 
 * For the first submission, the database is not operational, as a result 
 * the location list is blank. The rest of the functionality is complete.
 * 
 * Documentation Statement: This project is our own work. We only received information
 * through class discussion, developer.android.com, and stackoverflow.com
 * 
 * We also used the "Android SQLite database and content provider - tutorial" by Lars Vogel, at http://www.vogella.com/articles/AndroidSQLite/article.html
 * 
 * @authors Michael Patterson, Thomas Powell
 * We agreed to an even 50-50 split of points, which is most fair and most reflective of how work was split
 */

package edu.mines.locationfinder;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener, LocationListener {

	//sets a decimal format
	private static java.text.DecimalFormat df = new java.text.DecimalFormat( "0.000000" );
	 
	private View locationsButton;
	private View updateButton;
	private TextView display;
	private EditText nameField;
	private View enterButton;
	private View saveButton;
	private LocationManager locationManager;
	private Location location;
	
	private String latitude;
	private String longitude;
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//set a default name
		name = "Your location";
		
		//define views by ids
		locationsButton = findViewById(R.id.displayList);
		updateButton = findViewById(R.id.updateLocation);
		display = (TextView) findViewById(R.id.displayLocation);
		nameField = (EditText) findViewById(R.id.enterText);
		enterButton = findViewById(R.id.enter);
		saveButton = findViewById(R.id.saveLocation);
		saveButton.setEnabled(false);

		//set the click listener
		locationsButton.setOnClickListener(this);
		updateButton.setOnClickListener(this);
		enterButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);

		//declare the location manager and add a reference to the system's location manager
		this.locationManager = (LocationManager)this.getSystemService( Context.LOCATION_SERVICE );
		

	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.updateLocation:
			//update locations when the proper button is clicked..
			updateLocation();
			break;
		case R.id.displayList:
			displayList();
			break;
		case R.id.enter:
			updateName();
			break;
		case R.id.saveLocation:
			saveLocation();
			break;
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
		displayList();
		
	}
	
	public void displayList() {
		Intent intent = new Intent(this, LocationList.class);
		startActivity(intent);
	}
	
	public void updateName() {
		//change the value of name only if the edittext view has any text
		if (nameField.getText().toString().length() > 0) {
			name = nameField.getText().toString();
		}
	}
	
	public void updateLocation() {
		//get the location and add it to the text view "display"
		//added a uses-permission statement to the manifest
		//added implements locationlistener
		//added a locationManager and a location as instance variables

		// First show the user a message
		display.setText( R.string.searching );

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

		// Show the location to the user.
		display.setText( name + " is at Latitude: " + latitude + " and Longitude: " + longitude );
		//allow the saveButton to function 
		saveButton.setEnabled(true);
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


	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

}
