/**
 * Description: This class holds every object from the database as an instance variable.
 * It mostly allows for efficient access to these variables
 * TrailActivity holds a list of these locationObjects
 * A series of getters are provided as well
 * 
 * @authors Michael Patterson, Thomas Powell
 */

package mines.edu.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class LocationObject {
	private String name, latitude, longitude, time;
	private byte[] picture;

	public LocationObject(String n, String lat, String lon, String t, byte[] pic) {
		// basic constructor
		this.name = n;
		this.latitude = lat;
		this.longitude = lon;
		this.time = t;
		this.picture = pic;
	}

	// getters
	public String getName() {
		return name;
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

	public byte[] getPicture() {
		return picture;
	}

	public Bitmap getDecodedPicture() {
		// returns a bitmap image from the byte array
		return BitmapFactory.decodeByteArray(picture, 0, picture.length);
	}

	public LatLng getLatLng() {
		// turnts the lat and long strings into doubles, then returns a new LatLng object from those 2
		double lat = Double.parseDouble(latitude);
		double lon = Double.parseDouble(longitude);
		return new LatLng(lat, lon);
	}

	public Location getLocation() {
		// turns the lat and lon into doubles, then returns a new Location object from those 2
		double lat = Double.parseDouble(latitude);
		double lon = Double.parseDouble(longitude);
		Location l = new Location("");
		l.setLatitude(lat);
		l.setLongitude(lon);
		return l;
	}
}
