/**
 * Project Overview: This project allows users to create trails by starting to record and just walking.
 * The user can even tag photos to locations to be viewed later.
 * We made use of the location services, google maps, content providers with sqlite, and the camera.
 * It only works for Honeycomb or newer API's due to the content provider and MapFragment not being backwards compatible.
 * This app was a large collaborative effort and we are quite pleased with the end result.
 * Future improvements could include: less blurry images, tagging notes to locations or images as well, backwards compatibility..
 * 
 * Description: The MainActivity fills a list adapter with the unique names of hikes. Uniqueness of names is important
 * because it allows the hike's many location points to be grouped together. This activity also allows a new hike to be created
 * from the action bar, which also holds settings, help, and about.Additionally, the user can edit names or delete hikes from the list.
 * 
 * Documentation Statement: This project is our own work. We only received information
 * through class discussion, developer.android.com, and stackoverflow.com, and online java references.
 * Also, class demos and some tutorials at vogella.com were particularly useful..
 * 
 * @authors Michael Patterson, Thomas Powell
 * We agreed to an even 50-50 split of points, which is most fair and most reflective of how work was split
 */

package mines.edu.activities;

import java.util.ArrayList;

import mines.edu.database.LocationContentProvider;
import mines.edu.database.LocationTable;
import mines.edu.fragments.MessageFragment;
import mines.edu.fragments.NameFragment;
import mines.edu.patterson_powell_trailtracker.R;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;



public class MainActivity extends ListActivity implements NameFragment.Listener {
	
	public static final String PREF = "MyPrefsFile"; //filename for the shared preferences file
	SharedPreferences settings; 
	
	private static final int NEW_TRAIL_DIALOG = 1;
	private static final int EDIT_NAME_DIALOG = 2;

	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;

	private ArrayAdapter<String> adapter;
	private TextView emptyList;
	private ListView list;
	private ArrayList<String> names = new ArrayList<String>();
	private String selectedWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// initialize the views and register for context(ie long clicks)
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		emptyList = (TextView)findViewById(R.id.empty);
		list = (ListView)findViewById(android.R.id.list);
		registerForContextMenu(list);

		// If there isn't google play, tell the user that the app won't work properly
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if(status != ConnectionResult.SUCCESS) {
			showMessage(getResources().getString(R.string.getGooglePlay));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// either start a new hike, show settings, or show a message of help or about
		// depending on which action bar item is clicked
		int itemId = item.getItemId();
		if (itemId == R.id.action_new) {
			// open the name fragment, passing it false for edit
			startNew();
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

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// on a long click, either edit or delete
		switch(item.getItemId()) {
		case DELETE_ID:
			remove(selectedWord);
			return super.onContextItemSelected(item);
		case EDIT_ID:
			startEdit();
			return super.onContextItemSelected(item);
		default:
			return super.onContextItemSelected( item );
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// show a menu on a long click
		super.onCreateContextMenu( menu, v, menuInfo );
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		selectedWord = ((TextView) info.targetView).getText().toString();
		menu.add(0, EDIT_ID, 0, R.string.edit_name);
		menu.add(0, DELETE_ID, 0, R.string.delete);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		/**
		 * Starts a new activity
		 * gets the string from the id of the item clicked
		 * passes that name to DisplayActivity
		 */
		super.onListItemClick(l, v, position, id);
		// pass the selected name to the DisplayActivity
		TextView text = (TextView)v;
		selectedWord = text.getText().toString();
		Intent myIntent = new Intent(this, TrailActivity.class);
		myIntent.putExtra("name", selectedWord);settings = getSharedPreferences(PREF, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("updating", false);
		editor.commit();
		startActivity(myIntent);
	}

	private void fillData() {
		// open a cursor, get an array of previously saved names
		// this array is used to fill the list adapter
		String[] projection = { LocationTable.COLUMN_ID, LocationTable.COLUMN_NAME };
		Cursor cursor = getContentResolver().query(LocationContentProvider.CONTENT_URI, projection, null, null, null);
		if(cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			names = new ArrayList<String>();
			names.add(cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME)));
			while (cursor.moveToNext()) {
				String nextName = cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME));
				if (! names.contains(nextName)) {
					// only add unique names
					names.add(nextName);
				}
			}
			if (emptyList != null) {
				emptyList.setText("");
			}
		}
		cursor.close();
	}


	public boolean checkName(String name) {
		/**
		 * This function check is the name has been used and returns false if it has been used, o.w. true
		 * Unique names are necessary so two hikes don't get plotted together leading to nonsensical results
		 *
		 */
		// open a cursor, get an array of previous names
		String[] projection = { LocationTable.COLUMN_ID, LocationTable.COLUMN_NAME };
		Cursor cursor = getContentResolver().query(LocationContentProvider.CONTENT_URI, projection, null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			ArrayList<String> names = new ArrayList<String>();
			names.add(cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME)));
			while (cursor.moveToNext()) {
				String nextName = cursor.getString(cursor.getColumnIndexOrThrow(LocationTable.COLUMN_NAME));
				if (! names.contains(nextName)) {
					// only add unique names
					names.add(nextName);
				}
			}
			if (names.contains(name)) {
				// if this name has already been used
				cursor.close();
				return false;
			}
			// no need for else since it will return true at the end regardless.
		}
		cursor.close();
		// if the cursor is empty, then any name is unique
		// or if names doesn't contain name
		return true;
	}

	public void remove(String toRemove) {
		// occurs when user chooses delete after a long click
		// removes from db then from the listadapter
		Uri uri =  LocationContentProvider.CONTENT_URI;
		String[] select = {toRemove};
		getContentResolver().delete( uri, "name = ?", select);
		adapter.remove(toRemove);
		// If the cursor is now null, reset the text in empty TextView
		String[] projection = { LocationTable.COLUMN_ID, LocationTable.COLUMN_NAME };
		Cursor cursor = getContentResolver().query(LocationContentProvider.CONTENT_URI, projection, null, null, null);
		if (cursor == null || !(cursor.getCount() > 0)) {
			emptyList.setText(getResources().getString(R.string.empty));
		}
		cursor.close();
	}

	public void startNew() {
		// start a name fragment with edit value of false
		Bundle args = new Bundle();
		args.putInt("dialogID", NEW_TRAIL_DIALOG);
		args.putBoolean("edit", false);

		NameFragment newTrail = new NameFragment();
		newTrail.setArguments(args);
		newTrail.show(getFragmentManager(), "NewTrailDialog");
	}

	public void startEdit() {
		// start a name fragment with edit value of true
		Bundle args = new Bundle();
		args.putBoolean("edit", true);
		args.putInt("dialogID", EDIT_NAME_DIALOG);
		args.putString("prompt", selectedWord);

		NameFragment edit = new NameFragment();
		edit.setArguments(args);
		edit.show(getFragmentManager(), "EditNameDialog");
	}

	public void showMessage(String message) {
		// display a fragment dialog with the given string and a dismiss button
		Bundle args = new Bundle();
		args.putString("message", message);

		MessageFragment frag = new MessageFragment(); 
		frag.setArguments(args);
		frag.show(getFragmentManager(), "Message");
	}


	// Callback Methods for fragments
	@Override
	public void onInputCancel(int dialogID) {
		Log.d( "DIALOG_DEMO", "No input received from input dialog with id = " + dialogID );
	}

	@Override
	public void onInputDone(int dialogID, String input, boolean edit) {
		// first check the name against all names, this will ensure it is unique and prevent conflicts in the db
		if(edit) {
			changeName(input);
		} else {
			Intent trail = new Intent(this, TrailActivity.class);
			trail.putExtra("name", input);
			settings = getSharedPreferences(PREF, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("updating", true);
			editor.commit();
			startActivity(trail);
		}
	}

	private void changeName(String newName) {
		// edits all instances of a name to a newName in the db and updates the list with the newName
		Uri uri =  LocationContentProvider.CONTENT_URI;
		String[] select = {selectedWord};
		ContentValues cv = new ContentValues();
		cv.put("name", newName);
		getContentResolver().update(uri, cv, "name = ?", select);
		fillData();
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_selectable_list_item, names);
		setListAdapter(adapter);

	}

	@Override
	public void onResume() {
		// fill the list adapter with names every time the app resumes
		super.onStart();
		fillData();
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_selectable_list_item, names);
		setListAdapter(adapter);
	}

}
