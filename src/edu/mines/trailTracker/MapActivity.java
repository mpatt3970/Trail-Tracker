package edu.mines.trailTracker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.mines.trailTracker.R;

public class MapActivity extends Activity {
	
	public static final String PREF = "MyPrefsFile"; //filename for the shared preferences file
	private SharedPreferences settings;
	
	private ArrayList<LocationPair<Integer, String, String, String>> list;
	private String name;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		// get the name to know which hike to generate
		Intent intent = getIntent();
		name = "";
		name = intent.getStringExtra("name");

		list = new ArrayList<LocationPair<Integer, String, String, String>>();
		this.getLocations();
		// Get a handle to the Map Fragment
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		boolean first = true;
		if(!list.isEmpty()) {
			LocationPair<Integer, String, String, String> start = null;
			LocationPair<Integer, String, String, String> begin = null;
			LocationPair<Integer, String, String, String> end = null;
			for(LocationPair<Integer, String, String, String> l: list) {
				if(first) {
					end = l;
					start = l;
					first = false;
				} else {
					begin = end;
					end = l;
					map.addPolyline(new PolylineOptions().add(new LatLng(Double.parseDouble(begin.getLatitude()), Double.parseDouble(begin.getLongitude())), new LatLng(Double.parseDouble(end.getLatitude()), Double.parseDouble(end.getLongitude()))).width(5).color(Color.RED));
				}
			}
			Log.d("Tag~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", start.getTime());
			map.addMarker(new MarkerOptions().title("Start of Trail").snippet("Time info here").position(new LatLng(Double.parseDouble(start.getLatitude()), Double.parseDouble(start.getLongitude()))));
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(list.get(0).getLatitude()), Double.parseDouble(list.get(0).getLongitude())), 16));
		}
	}

	public void getLocations() {
		/**
		 * This function gets the list of locations
		 *
		 */
		// open a cursor, get an array of previous names and lats and lons
		String[] projection = { LocationTable.COLUMN_ID, LocationTable.COLUMN_LATITUDE, LocationTable.COLUMN_LONGITUDE, LocationTable.COLUMN_NAME, LocationTable.COLUMN_TIME };
		Cursor cursor = getContentResolver().query(LocationContentProvider.CONTENT_URI, projection, null, null, null);
		if ((cursor != null) && (cursor.getCount() > 0)) {
			cursor.moveToFirst();
			int counter  = 0;
			// only add it if the name matches
			if (cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME)).equals(name)) {
				list.add(new LocationPair<Integer, String, String, String>(counter, cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LATITUDE)), cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LONGITUDE)), cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_TIME))));
			}
			while (cursor.moveToNext()) {
				if (cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME)).equals(name)) {
					counter++;
					list.add(new LocationPair<Integer, String, String, String>(0, cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LATITUDE)), cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LONGITUDE)), cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_TIME))));
				}
			}
		}
		cursor.close();
	}

	public class LocationPair<number, latitude, longitude, time> {

		private final int number;
		private final String latitude;
		private final String longitude;
		private final String time;

		public LocationPair(int number, String latitude, String longitude, String time) {
			this.number = number;
			this.latitude = latitude;
			this.longitude = longitude;
			this.time = time;
		}

		public int getNumber() {
			return number;
		}

		public String getLatitude() {
			return latitude;
		}

		public String getLongitude() {
			return longitude;
		}

		public String getTime() {
			return time;
		}
	}

	// lifecycle functions
	// save the name onPause and restore it onResume
	@Override
	public void onPause() {
		super.onPause();
		settings = getSharedPreferences(PREF, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("name", name);
		editor.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		settings = getSharedPreferences(PREF, 0);
		if (settings.contains("name")) {
			name = settings.getString("name", "");
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		settings = getSharedPreferences(PREF, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}

}