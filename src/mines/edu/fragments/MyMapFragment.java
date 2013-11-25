package mines.edu.fragments;

import java.util.ArrayList;

import mines.edu.activities.TrailActivity;
import mines.edu.database.LocationObject;
import mines.edu.patterson_powell_trailtracker.R;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.Time;
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


	private static int DEFAULT_ZOOM = 16;
	
	private MapView mapView;
	private GoogleMap map;
	private String name;
	private ArrayList<LocationObject> list;




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
		
		map.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
		
		// create a receiver for new updates

		IntentFilter filter = new IntentFilter("UPDATE");
		getActivity().getApplicationContext().registerReceiver(receiver, filter);
	

		return v;
	}
	
	public void update(String n) {
		// only here to not break display and record activities
	}

	public void update() {
		map.clear();
		list = ((TrailActivity) getActivity()).getList();
		drawLines();
		moveToRecent();
	}


	public void moveToRecent() {
		// we might figure out zooming in right here
		// not a null pointer cause this is called after at least one item is added
		map.moveCamera(CameraUpdateFactory.newLatLng(list.get(list.size() - 1).getLatLng()));
	}

	public void drawLines() {
		boolean first = true;
		if (list.size() > 1) {
			LatLng begin = null;
			LatLng end = null;
			for(LocationObject locale : list) {
				if (locale.getPicture().length > 0) {
					map.addMarker(new MarkerOptions().position(locale.getLatLng())
							.title(getTime(parseStringForTime(locale.getTime()))));
				}
				if(first) {
					begin = locale.getLatLng();
					first = false;
				} else {
					end = begin;
					begin = locale.getLatLng();
					map.addPolyline(new PolylineOptions().add(begin, end).width(5).color(Color.RED));
				}
			}
		} else if (list.size() > 0) {
			map.addMarker(new MarkerOptions().position(list.get(0).getLatLng()));
		}
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// re update the map
			update();
		}

	};
	
	public Time parseStringForTime(String time) {
		// I don't know why the provided functions won't work for this
		// Assume format is YYYYMMDDTHHMMSS
		Time parsed = new Time();
		int seconds = Integer.parseInt(time.substring(13, 15));
		int minutes = Integer.parseInt(time.substring(11, 13));
		int hours = Integer.parseInt(time.substring(9, 11));
		int day = Integer.parseInt(time.substring(6, 8));
		int month = Integer.parseInt(time.substring(4, 6));
		int year = Integer.parseInt(time.substring(0, 4));
		parsed.set(seconds, minutes, hours, day, month, year);
		return parsed;
	}
	
	public String getTime(Time time) {
		return time.hour + ":" + time.minute + ":" + time.second;
	}


	public void onDestroyView() {
		super.onDestroyView();
		getActivity().getApplicationContext().unregisterReceiver(receiver);
	}
}
