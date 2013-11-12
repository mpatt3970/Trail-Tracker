package edu.mines.locationfinder;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends Activity {
	private ArrayList<LocationPair> list;
	private String name;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		// get the name to know which hike to generate
		Intent intent = getIntent();
		name = "";
		name = intent.getStringExtra("name");
		
		list = new ArrayList<LocationPair>();
		this.getLocations();
		// Get a handle to the Map Fragment
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		boolean first = true;

		if(!list.isEmpty()) {
			LocationPair begin = null;
			LocationPair end = null;
			for(LocationPair l: list) {
				if(first) {
					end = l;
					first = false;
				} else {
					begin = end;
					end = l;
					map.addPolyline(new PolylineOptions().add(new LatLng(Double.parseDouble(begin.getLatitude()), Double.parseDouble(begin.getLongitude())), new LatLng(Double.parseDouble(end.getLatitude()), Double.parseDouble(end.getLongitude()))).width(5).color(Color.RED));
				}
			}
			map.setMyLocationEnabled(true);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(list.get(0).getLatitude()), Double.parseDouble(list.get(0).getLongitude())), 16));
		}
	}

	public void getLocations() {
		/**
		 * This function gets the list of locations
		 *
		 */
		// open a cursor, get an array of previous names and lats and lons
		String[] projection = { LocationTable.COLUMN_ID, LocationTable.COLUMN_LATITUDE, LocationTable.COLUMN_LONGITUDE, LocationTable.COLUMN_NAME };
		Cursor cursor = getContentResolver().query(LocationContentProvider.CONTENT_URI, projection, null, null, null);
		if ((cursor != null) && (cursor.getCount() > 0)) {
			cursor.moveToFirst();
			int counter  = 0;
			// only add it if the name matches
			if (cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME)).equals(name)) {
				list.add(new LocationPair(counter, cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LATITUDE)), cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LONGITUDE))));
			}
			while (cursor.moveToNext()) {
				if (cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME)).equals(name)) {
					counter++;
					list.add(new LocationPair(0, cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LATITUDE)), cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LONGITUDE))));
				}
			}
		}
		cursor.close();
	}
	
	public class LocationPair<number, latitude, longitude> {

		private final int number;
		private final String latitude;
		private final String longitude;

		public LocationPair(int number, String latitude, String longitude) {
			this.number = number;
			this.latitude = latitude;
			this.longitude = longitude;
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

	}

}