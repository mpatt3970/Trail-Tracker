package mines.edu.activities;


import java.io.ByteArrayOutputStream;

import mines.edu.fragments.MessageFragment;
import mines.edu.fragments.MyMapFragment;
import mines.edu.patterson_powell_trailtracker.R;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class RecordActivity extends Activity {


	private static long minDistance = 1;

	private Integer minTime;
	private LocationManager locationManager;
	private TextView hikeName;
	private Intent pendingIntent;
	private PendingIntent locationListenerPendingIntent;
	private String name;
	private static final int CAMERA_PIC_REQUEST = 1337;  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);

		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		hikeName = (TextView)findViewById(R.id.hike_name);
		hikeName.setText(name);
		// update the min time value based on settings
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		String minTimeStr = sharedPrefs.getString("accuracy", "3000");
		minTime = Integer.parseInt(minTimeStr);
		this.locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

		((MyMapFragment)getFragmentManager().findFragmentById(R.id.mapFragment)).update(name);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.record, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_save) {
			cancelUpdates();
			Intent save = new Intent(this, DisplayActivity.class);
			save.putExtra("name", name);
			startActivity(save);
			return super.onOptionsItemSelected(item);
		} else if (itemId == R.id.action_camera) {
			Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);  
			startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
			return super.onOptionsItemSelected(item);
		} else if (itemId == R.id.action_settings) {
			Intent settings = new Intent(this, SettingsActivity.class);
			startActivity(settings);
			return super.onOptionsItemSelected(item);
		} else if (itemId == R.id.action_help) {
			showMessage(getResources().getString(R.string.help_text));
			return super.onOptionsItemSelected(item);
		} else if (itemId == R.id.action_about) {
			showMessage(getResources().getString(R.string.about_text));
			return super.onOptionsItemSelected(item);
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if (requestCode == CAMERA_PIC_REQUEST) {  
			Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bos);
			byte[] bArray = bos.toByteArray();
			/*db = YourDBHelper.getInstance(ctx).getWritableDatabase();    
			ContentValues values = new ContentValues();         
			values.put("image", bArray);            
			db.insert(TABLE_NAME , null, values);*/
			//requestSingleUpdate()
			Toast.makeText(this, "Camera functionality to be added soon", Toast.LENGTH_LONG).show();
		}  
	} 

	public void updateLocation() {
		pendingIntent = new Intent("mines.edu.patterson_powell_trailtracker.LOCATION_READY");
		Bundle args = new Bundle();
		pendingIntent.putExtra("name", name);
		// add the most recent location
		// pendingIntent.putExtra("location", this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
		locationListenerPendingIntent = PendingIntent.getBroadcast(this, 0, pendingIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		// Register the listener with the Location Manager to receive location updates.
		this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListenerPendingIntent);
	}

	public void cancelUpdates() {
		locationManager.removeUpdates(locationListenerPendingIntent);
	}

	public void showMessage(String message) {
		Bundle args = new Bundle();
		args.putString("message", message);

		MessageFragment frag = new MessageFragment(); 
		frag.setArguments(args);
		frag.show(getFragmentManager(), "Message");
	}


	@Override
	public void onStart() {
		super.onStart();
		updateLocation();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cancelUpdates();

	}

}
