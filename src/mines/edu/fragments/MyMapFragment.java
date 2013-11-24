package mines.edu.fragments;

import java.util.ArrayList;

import mines.edu.database.LocationContentProvider;
import mines.edu.database.LocationTable;
import mines.edu.patterson_powell_trailtracker.R;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;





public class MyMapFragment extends Fragment {

	private static final int DEFAULT_ZOOM = 16;


	private LocationManager locationManager;
	private MapView mapView;
	private GoogleMap map;
	private String name;
	private ArrayList<LatLng> list;




	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.map_fragment, container, false);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		map.setMyLocationEnabled(true);


		// Needs to call MapsInitializer before doing any CameraUpdateFactory calls
		try {
			MapsInitializer.initialize(this.getActivity());
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}

		// create a receiver for new updates

		IntentFilter filter = new IntentFilter("NEW_LOCATION");
		getActivity().getApplicationContext().registerReceiver(receiver, filter);
	

		return v;
	}

	public void update(String n) {
		map.clear();
		list = new ArrayList<LatLng>();
		name = n;
		getLocations(getActivity());
		drawLines();
		moveToStart();
	}


	public void moveToStart() {
		// move to the most recent location
		Integer index = list.size() - 1;
		if (index >= 0) {
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(list.get(index), DEFAULT_ZOOM));
		}
	}

	public void drawLines() {
		boolean first = true;
		if (list.size() > 1) {
			LatLng begin = null;
			LatLng end = null;
			for(LatLng l: list) {
				if(first) {
					end = l;
					first = false;
				} else {
					begin = end;
					end = l;
					map.addPolyline(new PolylineOptions().add(begin, end).width(5).color(Color.RED));
				}
			}
		} else if (list.size() > 0) {
			map.addMarker(new MarkerOptions().position(list.get(0)));
		}
		/*if(list.size() > 0) {
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(list.get(list.size() - 1), 16));
		}*/
	}

	public void getLocations(Context context) {
		/**
		 * This function gets the list of locations
		 *
		 */
		// open a cursor, get an array of previous names and lats and lons
		String[] projection = { LocationTable.COLUMN_ID, LocationTable.COLUMN_LATITUDE, LocationTable.COLUMN_LONGITUDE, LocationTable.COLUMN_NAME };
		Cursor cursor = context.getContentResolver().query(LocationContentProvider.CONTENT_URI, projection, null, null, null);
		if ((cursor != null) && (cursor.getCount() > 0)) {
			cursor.moveToFirst();
			// only add it if the name matches
			if (cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME)).equals(name)) {
				list.add(new LatLng(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LATITUDE))), Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LONGITUDE)))));
			}
			while (cursor.moveToNext()) {
				if (cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME)).equals(name)) {
					list.add(new LatLng(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LATITUDE))), Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_LONGITUDE)))));
				}
			}
		}
		cursor.close();
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// re update the map
			update(name);

		}

	};


	public void onDestroyView() {
		super.onDestroyView();
		getActivity().getApplicationContext().unregisterReceiver(receiver);
	}
}
