/**
 * Description: This program encapsulates all the logic for calculating stats from a list of location objects.
 * It receives a broadcast to start updating 
 * 
 * @authors Michael Patterson, Thomas Powell
 */

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
import android.location.Location;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StatsFragment extends Fragment {

	// sets a decimal format for the distance
	private static java.text.DecimalFormat df = new java.text.DecimalFormat( "0.00" );


	// some conversions between milliseconds and other time units
	private static int MILLIS_DAY = 86400000;
	private static int MILLIS_HOUR = 3600000;
	private static int MILLIS_MINUTE = 60000;
	private static int MILLIS_SECOND = 1000;

	private double distance, totalTime, speed;
	private Time timeStart, timeEnd;
	private ArrayList<LocationObject> list;
	private TextView totalView, timeView, speedView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.stats_fragment, container, false);

		// register for broadcasts
		IntentFilter filter = new IntentFilter("UPDATE");
		getActivity().getApplicationContext().registerReceiver(receiver, filter);


		return v;
	}

	public void update() {
		list = ((TrailActivity) getActivity()).getList();
		calculations();
		updateViews();
	}

	public void calculations() {
		// get the first and last time from the list
		distance = 0;
		timeStart = parseStringForTime(list.get(0).getTime());
		timeEnd = parseStringForTime(list.get(list.size() - 1).getTime());
		// converts start and end times to milliseconds and gets the difference
		totalTime = timeEnd.toMillis(false) - timeStart.toMillis(false); 
		boolean first = true;
		Location begin = null;
		Location end = null;
		// loops through and adds the distance from each point to the next point
		for (LocationObject locale : list) {
			if(first) {
				end = locale.getLocation();
				first = false;
			} else {
				begin = end;
				end = locale.getLocation();
				distance += begin.distanceTo(end); // in meters
			}
		}
		// gets the average speed in meters per ms and changes to meters per s
		speed = distance/totalTime*MILLIS_SECOND;
	}

	public void updateViews() {
		// define the views
		totalView = (TextView) getView().findViewById(R.id.total);
		timeView = (TextView) getView().findViewById(R.id.time);
		speedView = (TextView) getView().findViewById(R.id.speed);
		// setText on the text views to reflect the calculations
		totalView.setText("Distance: " + df.format(distance) +  " meters, Duration: " + timeFromMillis(totalTime));
		timeView.setText("Date: " + getDate() + ", Started At: " + getTime(timeStart) + ", Ended At: " + getTime(timeEnd))   ;
		speedView.setText("Average Speed: " + df.format(speed) + "meters/seconds");

	}

	public String timeFromMillis(double total) {
		// returns a string representing the duration
		// total is the duration represented as milliseconds
		Integer days = (int) (total/MILLIS_DAY);
		Integer remaining = (int) (total%MILLIS_DAY);
		Integer hours = remaining/MILLIS_HOUR;
		remaining = remaining%MILLIS_HOUR;
		Integer minutes = remaining/MILLIS_MINUTE;
		remaining = remaining%MILLIS_MINUTE;
		Integer seconds = remaining/MILLIS_SECOND;
		String formatted = seconds + " seconds.";
		if (minutes > 0 || hours > 0 || days > 0) {
			formatted = minutes + " minutes, " + formatted;
			if (hours > 0 || days > 0) {
				formatted = hours + " hours, " + formatted;
				if (days > 0) {
					formatted = days + " days, " + days;
				}
			}
		}
		return formatted;
	}

	public String getDate() {
		// returns a human readable string of the start date from the timeStart value
		return timeStart.month +  "/" + timeStart.monthDay + "/" + timeStart.year;
	}

	public String getTime(Time time) {
		// returns a human readable string from a time
		return time.hour + ":" + time.minute + ":" + time.second;
	}

	public Time parseStringForTime(String time) {
		// I don't know why the provided functions won't work for this
		// The format is YYYYMMDDTHHMMSS
		// returns a time value with these fields
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

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// re update the stats after each broadcast
			update();
		}

	};

	public void onDestroyView() {
		// cleanup
		super.onDestroyView();
		getActivity().getApplicationContext().unregisterReceiver(receiver);
	}

}
