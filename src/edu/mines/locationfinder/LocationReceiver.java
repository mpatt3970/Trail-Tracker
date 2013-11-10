package edu.mines.locationfinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class LocationReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//Do this when the system sends the intent
		Bundle b = intent.getExtras();
		Location loc = (Location)b.get(android.location.LocationManager.KEY_LOCATION_CHANGED);
		
		if (loc != null) {
			Log.d("locationUpdate", loc.toString());
			Toast.makeText(context, loc.toString(), Toast.LENGTH_SHORT).show(); 
		}
	}
}