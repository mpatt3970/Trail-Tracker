/**
 * Description: This fragment encapsulates all the map logic for our app
 * It handles drawing lines, marking images, showing the user's current location
 * It also provides a custom info window to display images and start ImageActivity on a click 
 * It updates when it receives a broadcast to do so
 * 
 * @authors Michael Patterson, Thomas Powell
 */

package mines.edu.fragments;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import mines.edu.activities.ImageActivity;
import mines.edu.activities.TrailActivity;
import mines.edu.database.LocationObject;
import mines.edu.patterson_powell_trailtracker.R;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MyMapFragment extends Fragment implements InfoWindowAdapter {


	private static int DEFAULT_ZOOM = 16; // A default zoom value, about 5 blocks wide

	private GoogleMap map;
	private ArrayList<LocationObject> list; // holds all relevant points from db
	private Map<String, LocationObject> whichImage;




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

		// create the custom info window
		map.setInfoWindowAdapter(this);
		map.setOnInfoWindowClickListener(new ImageListener());
		
		// create a receiver for new updates
		IntentFilter filter = new IntentFilter("UPDATE");
		getActivity().getApplicationContext().registerReceiver(receiver, filter);


		return v;
	}

	public void update() {
		map.clear();
		// get the current setting for map type
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		int setting = Integer.parseInt(sharedPrefs.getString("mapType", "0"));
		if (setting == 1) {
			// corresponds to hybrid map
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		} else if (setting == 2) {
			// corresponds to satellite map
			map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		} else if (setting == 3) {
			map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		} else {
			// either they chose normal or it's broken, either way set to normal
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}
		// get the most recent list of location objects, draw on and move the map
		map.clear();
		list = ((TrailActivity) getActivity()).getList();
		drawLines();
		moveToRecent();
	}


	public void moveToRecent() {
		// not a null pointer cause this is called after at least one item is added
		// focuses the camera on the most recent location
		map.moveCamera(CameraUpdateFactory.newLatLng(list.get(list.size() - 1).getLatLng()));
	}

	public void drawLines() {
		// Once there are at least two locations, start drawing lines between them
		// if an image is at a point, add a marker with a title that corresponds to the map of locationobjects and strings		
		whichImage = new TreeMap<String, LocationObject>();
		Integer counter = 0; // marks the marker so the appropriate image is displayed
		boolean first = true;
		if (list.size() > 1) {
			LatLng begin = null;
			LatLng end = null;
			for(LocationObject locale : list) {
				if (locale.getPicture().length > 0) {
					whichImage.put(counter.toString(), locale);
					map.addMarker(new MarkerOptions().position(locale.getLatLng()).title(counter.toString()));
					counter++;
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
		} 
		/* removed this to prevent a click crashing the app because the info window requires an image at the marker
		 * else if (list.size() > 0) {
			map.addMarker(new MarkerOptions().position(list.get(0).getLatLng()));
		}*/
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// re update the map
			update();
		}

	};

	public void onDestroyView() {
		// clean up the view
		super.onDestroyView();
		getActivity().getApplicationContext().unregisterReceiver(receiver);
	}

	@Override
	public View getInfoContents(Marker arg0) {
		// not needed
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// creates a view for clicking on a marker
		// get the marker's title to determine correct image
		ImageView image = new ImageView(getActivity());
		String position = arg0.getTitle();
		LocationObject locale = whichImage.get(position);
		Bitmap pic = locale.getDecodedPicture();
		image.setImageBitmap(pic);
		return image;
	}
	
	private class ImageListener implements OnInfoWindowClickListener {
		/**
		 * This private class starts imageActivity when an info window is clicked
		 * It passes along an intent with the byte array of the image from the info window
		 */
		@Override
		public void onInfoWindowClick(Marker arg0) {
			Intent intent = new Intent(getActivity(), ImageActivity.class);
			String position = arg0.getTitle();
			LocationObject locale = whichImage.get(position);
			byte[] picBytes = locale.getPicture(); // has to be passed as a byte array
			intent.putExtra("image", picBytes);
			startActivity(intent);
		}
		
	}
}
