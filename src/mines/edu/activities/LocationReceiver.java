package mines.edu.activities;

import mines.edu.database.LocationContentProvider;
import mines.edu.database.LocationTable;
import mines.edu.fragments.MyMapFragment;
import mines.edu.patterson_powell_trailtracker.R;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;

public class LocationReceiver extends BroadcastReceiver {
	//sets a decimal format
	private static java.text.DecimalFormat df = new java.text.DecimalFormat( "0.000000" );

	private String name;
	private String latitude;
	private String longitude;
	private String timeStr;
	private MyMapFragment mapFrag;

	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		//Do this when the system sends the intent
		Bundle b = intent.getExtras();
		Location location;
		//if (b.containsKey("location")) {
		//	location = (Location)b.get("location");
		//} else {
			location = (Location)b.get(android.location.LocationManager.KEY_LOCATION_CHANGED);
		//}
		name = b.getString("name");
		if (location != null) {
			Time now = new Time();
			now.setToNow();
			timeStr = now.toString();
			latitude = df.format( location.getLatitude() );
			longitude = df.format( location.getLongitude() );
			String received = "New location added at " + latitude + ", " + longitude;
			Toast.makeText(context, received, Toast.LENGTH_SHORT).show();
			saveNewLocation(context);
			
			Intent broadcast = new Intent();
			intent.setAction("NEW_LOCATION");
			context.sendBroadcast(broadcast);
		}
	}

	public void saveNewLocation(Context context) {
		// store the values of the variables into a ContentValues
		ContentValues values = new ContentValues();
		values.put(LocationTable.COLUMN_NAME, name);
		values.put(LocationTable.COLUMN_LATITUDE, latitude);
		values.put(LocationTable.COLUMN_LONGITUDE, longitude);
		values.put(LocationTable.COLUMN_TIME, timeStr);
		values.put(LocationTable.COLUMN_PHOTO, "");
		// store the values in the database
		context.getContentResolver().insert(LocationContentProvider.CONTENT_URI, values);
	}
}
