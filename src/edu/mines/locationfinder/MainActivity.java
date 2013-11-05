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

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final int DELETE_ID = Menu.FIRST + 1;


	//sets a decimal format
	private static java.text.DecimalFormat df = new java.text.DecimalFormat( "0.000000" );

	private SimpleCursorAdapter adapter;

	private TextView emptyList;
	private ListView list;

	private LocationManager locationManager;
	private Location location;

	private String latitude;
	private String longitude;
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_locations);

		setList((ListView)findViewById(android.R.id.list));
		fillData();
		registerForContextMenu( getListView() );


	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}

	private void fillData() {
		// Fields from the database (projection)

		// Fill this array with the values that you want to grab
		// gets the projection from the table
		String[] from = new String[] { LocationTable.COLUMN_NAME };

		// Fill this array with the ids of TextViews that you want to setText in from the above array
		// Fields on the UI to which we map
		int[] to = new int[] { R.id.row };

		// creates loader
		getLoaderManager().initLoader(0, null, this);
		// creates a new cursorAdapter, using the layout from row.xml, the db projection, and the id at row.xml
		adapter = new SimpleCursorAdapter(this, R.layout.row, null, from, to, 0);
		//calls the newly created adapter
		setListAdapter(adapter);
	}

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

	@Override
	public boolean onContextItemSelected( MenuItem item )
	{
		switch( item.getItemId() )
		{
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
			Uri uri = Uri.parse( LocationContentProvider.CONTENT_URI + "/" + info.id );
			getContentResolver().delete( uri, null, null );
			fillData();
			return true;
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
		 * passes the uri of that activity to DetailActivity in  an intent
		 */
		super.onListItemClick( l, v, position, id );
		Intent intent = new Intent(this, DetailActivity.class);
		Uri locationUri = Uri.parse(LocationContentProvider.CONTENT_URI + "/" + id);
		intent.putExtra(LocationContentProvider.CONTENT_ITEM_TYPE, locationUri);
		startActivity(intent);
	}
	
	/** The menu displayed on a long touch. */
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo )
	{
		super.onCreateContextMenu( menu, v, menuInfo );
		menu.add( 0, DELETE_ID, 0, R.string.delete );
	}


	
	public void setList(ListView list) {
		this.list = list;
	}

}
