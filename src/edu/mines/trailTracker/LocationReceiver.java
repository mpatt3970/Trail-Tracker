package edu.mines.trailTracker;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class LocationReceiver extends BroadcastReceiver {
	//sets a decimal format
	private static java.text.DecimalFormat df = new java.text.DecimalFormat( "0.000000" );

	private String name;
	private String latitude;
	private String longitude;
	private String timeStr;

	@Override
	public void onReceive(Context context, Intent intent) {
		//Do this when the system sends the intent
		Bundle b = intent.getExtras();
		Location location = (Location)b.get(android.location.LocationManager.KEY_LOCATION_CHANGED);
		name = b.getString("name");
		Log.d("Received", "location");
		if (location != null) {
			Time now = new Time();
			now.setToNow();
			timeStr = now.toString();
			latitude = df.format( location.getLatitude() );
			longitude = df.format( location.getLongitude() );
			Log.d("locationUpdate", location.toString());
			Toast.makeText(context, location.toString(), Toast.LENGTH_SHORT).show();
			saveNewLocation(context);
		}
	}

	public void saveNewLocation(Context context) {
		//store the values of the variables into a ContentValues
		ContentValues values = new ContentValues();
		values.put(LocationTable.COLUMN_NAME, name);
		values.put(LocationTable.COLUMN_LATITUDE, latitude);
		values.put(LocationTable.COLUMN_LONGITUDE, longitude);
		values.put(LocationTable.COLUMN_TIME, timeStr);
		values.put(LocationTable.COLUMN_PHOTO, "");


		//create a new Uri
		context.getContentResolver().insert(LocationContentProvider.CONTENT_URI, values);
	}
}
