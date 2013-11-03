/**
 * Description: The "My Location" screen, in which the user is able to see
 * all saved GPS locations and their names in a listview. The user may scroll 
 * through the saved locations and either access or delete them.
 * 
 * @authors Michael Patterson, Thomas Powell
 */

package edu.mines.locationfinder;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class LocationList extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>
{
	/*
	 * Ok... This is making some sense. Override the loader methods to handle the LoaderManager.
	 * Then fillData() fills in the listview. 
	 */
	private static final int DELETE_ID = Menu.FIRST + 1;



	private SimpleCursorAdapter adapter;

	private TextView emptyList;
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/*
		 * Called when the activity is created
		 * Sets the View variables and fills the list from the database
		 */
		//View empty = findViewById(R.id.empty);  
		//ListView listView = (ListView)findViewById(R.id.empty);
		//listView.setEmptyView(empty);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_list);
		//setEmptyList((TextView)findViewById(R.id.empty));
		setList((ListView)findViewById(android.R.id.list));
		fillData();
		registerForContextMenu( getListView() );
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

	public ListView getList() {
		return list;
	}

	public void setList(ListView list) {
		this.list = list;
	}

	public TextView getEmptyList() {
		return emptyList;
	}

	public void setEmptyList(TextView emptyList) {
		this.emptyList = emptyList;
	}
}
