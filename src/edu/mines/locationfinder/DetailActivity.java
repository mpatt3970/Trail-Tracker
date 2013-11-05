package edu.mines.locationfinder;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class DetailActivity extends Activity /*implements LoaderManager.LoaderCallbacks<Cursor>*/ {

	private SimpleCursorAdapter adapter;
	private Uri locationUri;

	private TextView nameView;
	private TextView latView;
	private TextView longView;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		/*
		 * Called when the activity is created
		 * Sets the View variables and fills the list from the database
		 */

		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_activity);
		nameView = (TextView)findViewById(R.id.location_name);
		latView = (TextView)findViewById(R.id.location_lat);
		longView = (TextView)findViewById(R.id.location_long);
		
		Bundle extras = getIntent().getExtras();
		locationUri = extras
				.getParcelable(LocationContentProvider.CONTENT_ITEM_TYPE);

		fillData();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}
	
	private void fillData() {
	/**
	* places text into the appropriate textviews by pointing a cursor at a query at the specific uri
	*/
		// Fields from the database
		String[] projection = new String[] { LocationTable.COLUMN_NAME, LocationTable.COLUMN_LATITUDE, LocationTable.COLUMN_LONGITUDE };

		//cursor to a query of the specific uri
		Cursor cursor = getContentResolver().query(locationUri, projection, null, null,
				null);

		if (cursor != null) {
			cursor.moveToFirst();
			nameView.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(LocationTable.COLUMN_NAME)));
			latView.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(LocationTable.COLUMN_LATITUDE)));
			longView.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(LocationTable.COLUMN_LONGITUDE)));

			// always close the cursor
			cursor.close();
		}
	}

	public SimpleCursorAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(SimpleCursorAdapter adapter) {
		this.adapter = adapter;
	}
	
}
