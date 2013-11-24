package mines.edu.activities;

import java.util.ArrayList;

import mines.edu.database.LocationContentProvider;
import mines.edu.database.LocationTable;
import mines.edu.fragments.MessageFragment;
import mines.edu.fragments.NameFragment;
import mines.edu.fragments.ReEnterNameFragment;
import mines.edu.patterson_powell_trailtracker.R;
import android.app.ListActivity;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;



public class MainActivity extends ListActivity implements NameFragment.Listener {
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

	/** The menu displayed on a long touch. */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
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
		myIntent.putExtra("name", selectedWord);
		myIntent.putExtra("new_trail", false);
		startActivity(myIntent);
	}

	private void fillData() {
		// open a cursor, get an array of previous names
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
		Bundle args = new Bundle();
		args.putInt("dialogID", NEW_TRAIL_DIALOG);
		args.putBoolean("edit", false);

		NameFragment newTrail = new NameFragment();
		newTrail.setArguments(args);
		newTrail.show(getFragmentManager(), "NewTrailDialog");
	}

	public void startEdit() {
		Bundle args = new Bundle();
		args.putBoolean("edit", true);
		args.putInt("dialogID", EDIT_NAME_DIALOG);
		args.putString("prompt", selectedWord);

		NameFragment edit = new NameFragment();
		edit.setArguments(args);
		edit.show(getFragmentManager(), "EditNameDialog");
	}

	public void showMessage(String message) {
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
		boolean unique = checkName(input);
		// if it isn't unique, display an alertdialog and start an editable namefragment with the previous attempt
		if (!unique) {
			selectedWord = input;
			Bundle args = new Bundle();
			args.putString("message", getResources().getString(R.string.not_unique));
			ReEnterNameFragment reEnter = new ReEnterNameFragment();
			reEnter.setArguments(args);
			reEnter.show(getFragmentManager(), "AlertNotUniqueFragment");
		} else {
			Intent trail = new Intent(this, TrailActivity.class);
			trail.putExtra("name", input);
			trail.putExtra("new_trail", true);
			startActivity(trail);
		}
	}

	@Override
	public void onResume() {
		super.onStart();
		fillData();
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_selectable_list_item, names);
		setListAdapter(adapter);
	}

}
