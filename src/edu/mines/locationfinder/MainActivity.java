/**
 * Description: The "Main Activity" screen, in which the user enters a name
 * for the location and clicks "Update the Location" to find his current GPS
 * coordinates. Then can click "Save this Location" to save that location
 * to a database. The contents of the database can be viewed on the LocationList
 * screen that can be accessed by clicking "My Locations."
 *
 * For the first submission, the database is not operational, as a result
 * the location list is blank. The rest of the functionality is complete.
 *
 * Documentation Statement: This project is our own work. We only received information
 * through class discussion, developer.android.com, and stackoverflow.com
 *
 * We also used the "Android SQLite database and content provider - tutorial" by Lars Vogel, at http://www.vogella.com/articles/AndroidSQLite/article.html
 *
 * @authors Michael Patterson, Thomas Powell
 * We agreed to an even 50-50 split of points, which is most fair and most reflective of how work was split
 */

package edu.mines.locationfinder;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainActivity extends ListActivity {

	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final String DEFAULT_NAME = "Some Trail";

	//sets a decimal format
	private static java.text.DecimalFormat df = new java.text.DecimalFormat( "0.000000" );


	private ArrayAdapter<String> adapter;

	private TextView emptyList;
	private ListView list;

	private LocationManager locationManager;
	private Location location;

	private String latitude;
	private String longitude;
	private String name;

	private int clickCounter = 0;

	private ArrayList<String> names = new ArrayList<String>();
	private String selectedWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_locations);

		setList((ListView)findViewById(android.R.id.list));
		fillData();
		registerForContextMenu( getListView() );


		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
		setListAdapter(adapter);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection for action bar
		switch (item.getItemId()) {
		case R.id.action_new:
			Intent intent = new Intent(this, EnterName.class);
			startActivity(intent);
			return super.onOptionsItemSelected(item);
		case R.id.action_settings:
			Intent settings = new Intent(this, Settings.class);
			startActivity(settings);
			return super.onOptionsItemSelected(item);
		case R.id.action_help:
			Intent help = new Intent(this, Help.class);
			startActivity(help);
			return super.onOptionsItemSelected(item);
		case R.id.action_about:
			Intent about = new Intent(this, About.class);
			startActivity(about);
			return super.onOptionsItemSelected(item);
		default:
			return super.onOptionsItemSelected(item);
		}
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
		} 
		cursor.close();
	}
/*
	// creates a new loader after the initLoader () call
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// loads a projection from the db
		String[] projection = { LocationTable.COLUMN_ID, LocationTable.COLUMN_NAME };
		// creates a cursorloader and returns it
		CursorLoader cursorLoader = new CursorLoader(this,
				LocationContentProvider.CONTENT_URI, projection, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data is not available anymore, delete reference
		adapter.swapCursor(null);
	}
*/

	@Override
	public boolean onContextItemSelected( MenuItem item )
	{	
		switch( item.getItemId() )
		{
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
			Uri uri =  LocationContentProvider.CONTENT_URI;
			String[] select = {selectedWord};
			Integer x = getContentResolver().delete( uri, "name = ?", select);
			
			fillData();
			adapter.remove(selectedWord);
			return super.onContextItemSelected( item );
		}
		return super.onContextItemSelected( item );
	}

	// Opens the detail activity if an entry is clicked.
	// Opens the detail activity if an entry is clicked.
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id )
	{
		/**
		 * Starts a new activity
		 * gets the uri from the id of the item clicked
		 * passes the uri of that activity to DetailActivity in an intent
		 */
		super.onListItemClick( l, v, position, id );
		
		// pass the selected name to the mapActivity
		TextView text = (TextView)v;
		String selected = text.getText().toString();
		Intent myIntent = new Intent(this, MapActivity.class);
		myIntent.putExtra("name", selected);
		startActivity(myIntent);

	}
	

	/** The menu displayed on a long touch. */
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo )
	{
		super.onCreateContextMenu( menu, v, menuInfo );
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		selectedWord = ((TextView) info.targetView).getText().toString();
		menu.add( 0, DELETE_ID, 0, R.string.delete );
	}

	public void setList(ListView list) {
		this.list = list;
	}

	public void map(View view) {
		Intent myIntent = new Intent(MainActivity.this, MapActivity.class);
		MainActivity.this.startActivity(myIntent);
	}

	public void addItems(View v) {
		names.add("Clicked : " + clickCounter++);
		adapter.notifyDataSetChanged();
	}

}
