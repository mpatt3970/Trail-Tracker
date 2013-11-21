/**
 * Description: This is copied and refactored almost straight from vogella.com
 * The contentprovider allows access to the database.
 * It defines necessary methods such as insert, delete, and query. It also creates the database.
 * 
 * @authors Michael Patterson, Thomas Powell
 */

package mines.edu.database;

import java.util.Arrays;
import java.util.HashSet;


import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class LocationContentProvider extends ContentProvider {

	private LocationTableHelper database;

	private static final int LOCATIONS = 15;
	private static final int LOCATION_ID = 20;

	private static final String AUTHORITY = "mines.edu.patterson_powell_trailtracker.contentprovider";

	private static final String BASE_PATH = "database";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/database";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/database";

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, LOCATIONS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", LOCATION_ID);
	}


	@Override
	public boolean onCreate() {
		//creates the database
		database = new LocationTableHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// Uisng SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Check if the caller has requested a column which does not exists
		checkColumns(projection);

		// Set the table
		queryBuilder.setTables(LocationTable.TABLE_LOCATION);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case LOCATIONS:
			break;
		case LOCATION_ID:
			// Adding the ID to the original query
			queryBuilder.appendWhere(LocationTable.COLUMN_ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		//inserts new values at a specific URI
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		long id = 0;
		switch (uriType) {
		case LOCATIONS:
			id = sqlDB.insert(LocationTable.TABLE_LOCATION, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		//deletes the selection at that URI
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
		case LOCATIONS:
			rowsDeleted = sqlDB.delete(LocationTable.TABLE_LOCATION, selection,
					selectionArgs);
			break;
		case LOCATION_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(LocationTable.TABLE_LOCATION,
						LocationTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(LocationTable.TABLE_LOCATION,
						LocationTable.COLUMN_ID + "=" + id 
						+ " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		//changes selected values at that URI to new values

	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsUpdated = 0;
	    switch (uriType) {
	    case LOCATIONS:
	      rowsUpdated = sqlDB.update(LocationTable.TABLE_LOCATION, 
	          values, 
	          selection,
	          selectionArgs);
	      break;
	    case LOCATION_ID:
	      String id = uri.getLastPathSegment();
	      if (TextUtils.isEmpty(selection)) {
	        rowsUpdated = sqlDB.update(LocationTable.TABLE_LOCATION, 
	            values,
	            LocationTable.COLUMN_ID + "=" + id, 
	            null);
	      } else {
	        rowsUpdated = sqlDB.update(LocationTable.TABLE_LOCATION, 
	            values,
	            LocationTable.COLUMN_ID + "=" + id 
	            + " and " 
	            + selection,
	            selectionArgs);
	      }
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsUpdated;
	}

	private void checkColumns(String[] projection) {
		String[] available = { LocationTable.COLUMN_NAME,
		        LocationTable.COLUMN_LATITUDE, LocationTable.COLUMN_LONGITUDE,
		        LocationTable.COLUMN_ID };
		    if (projection != null) {
		      HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
		      HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
		      // Check if all columns which are requested are available
		      if (!availableColumns.containsAll(requestedColumns)) {
		        throw new IllegalArgumentException("Unknown columns in projection");
		      }
		    }
	}
}
