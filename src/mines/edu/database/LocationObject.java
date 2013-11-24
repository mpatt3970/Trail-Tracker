package mines.edu.database;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class LocationObject {
	private String name, latitude, longitude, time;
	
	public LocationObject(String n, String lat, String lon, String t) {
		this.name = n;
		this.latitude = lat;
		this.longitude = lon;
		this.time = t;
	}

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
	
	public LatLng getLatLng() {
		double lat = Double.parseDouble(latitude);
		double lon = Double.parseDouble(longitude);
		return new LatLng(lat, lon);
	}
	
	public Location getLocation() {
		double lat = Double.parseDouble(latitude);
		double lon = Double.parseDouble(longitude);
		Location l = new Location("");
		l.setLatitude(lat);
		l.setLongitude(lon);
		return l;
	}
}
