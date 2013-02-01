package com.codingkat.ring.scheduler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_ALARMS = "alarms";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_START_PI= "start_pi";
	public static final String COLUMN_END_PI= "end_pi";
	public static final String COLUMN_REPEAT_SCHEME = "repeat_scheme";
	public static final String COLUMN_SOUND_MODE= "sound_mode";
	public static final String COLUMN_START_TIME= "start_time";
	public static final String COLUMN_END_TIME= "end_time";
	public static final String COLUMN_EXTRA= "extra";
	
	public static final String TABLE_ALARM_HISTORY = "alarm_history";
	public static final String COLUMN_HIST_ID = "_id";
	public static final String COLUMN_HIST_DATE_TIME= "date_time";
	public static final String COLUMN_HIST_SOUND_MODE= "sound_mode";
	
	private static final String DATABASE_NAME = "alarms.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_ALARMS + "( " + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_START_PI
			+ " integer not null, " + COLUMN_END_PI 
			+ " integer not null, " + COLUMN_REPEAT_SCHEME 
			+ " text not null, " + COLUMN_SOUND_MODE
			+ " text not null, " + COLUMN_START_TIME
			+ " text not null, " + COLUMN_END_TIME
			+ " text not null, " + COLUMN_EXTRA 
			+ " integer not null "
			+ ");";
	
	private static final String DATABASE_CREATE2 = "create table "
			+ TABLE_ALARM_HISTORY + "( " + COLUMN_HIST_ID
			+ " integer primary key autoincrement, " + COLUMN_HIST_DATE_TIME
			+ " text not null, " + COLUMN_SOUND_MODE
			+ " text not null "
			+ ");";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		database.execSQL(DATABASE_CREATE2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");*/
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
		onCreate(db);
	}

}
