/**
 * Description: This is copied and refactored almost straight from vogella.com
 * It defines the SQLite Database Columns and Creation String
 * 
 * @authors Michael Patterson, Thomas Powell
 */

package edu.mines.trailTracker;

import android.database.sqlite.SQLiteDatabase;

public class LocationTable {
	//Describes the table
	public static final String TABLE_LOCATION = "location";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_PHOTO = "image";

	//creates the table
	private static final String DATABASE_CREATE = "create table " 
			+ TABLE_LOCATION
			+ "(" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_NAME + " text not null, " 
			+ COLUMN_LATITUDE + " text not null," 
			+ COLUMN_LONGITUDE + " text not null," 
			+ COLUMN_TIME + " text not null,"
			+ COLUMN_PHOTO + " text not null"
			+ ");";
	

	public static void onCreate(SQLiteDatabase db) {
		//onCreate execute the SQL statement to create the table
		db.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//on an upgrade, drop the old table and build a new one
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
		onCreate(db);
	}
}
