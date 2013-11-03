/**
 * Description: This is copied and refactored almost straight from vogella.com
 * The TableHelper is required to use the Table
 * 
 * @authors Michael Patterson, Thomas Powell
 */

package edu.mines.locationfinder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocationTableHelper extends SQLiteOpenHelper {
	/*
	 * 
	 * Give credit where credit is due: "Thanks http://www.vogella.com/articles/AndroidSQLite/article.html"
	 */
	private static final String DATABASE_NAME = "locations.db";
	private static final int DATABASE_VERSION = 1;

	public LocationTableHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		LocationTable.onCreate(database);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		LocationTable.onUpgrade(db, oldVersion, newVersion);
	}

}
