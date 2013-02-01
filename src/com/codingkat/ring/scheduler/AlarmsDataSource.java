package com.codingkat.ring.scheduler;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AlarmsDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_START_PI,
			MySQLiteHelper.COLUMN_END_PI,
			MySQLiteHelper.COLUMN_REPEAT_SCHEME,
			MySQLiteHelper.COLUMN_SOUND_MODE,
			MySQLiteHelper.COLUMN_START_TIME,
			MySQLiteHelper.COLUMN_END_TIME,
			MySQLiteHelper.COLUMN_EXTRA
			};

	public AlarmsDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public AlarmEntry createAlarmEntry(int start_pi, int end_pi, String repeat_scheme, String sound_mode, String start_time, String end_time, int extra) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_START_PI, start_pi);
		values.put(MySQLiteHelper.COLUMN_END_PI, end_pi);
		values.put(MySQLiteHelper.COLUMN_REPEAT_SCHEME, repeat_scheme);
		values.put(MySQLiteHelper.COLUMN_SOUND_MODE, sound_mode);
		values.put(MySQLiteHelper.COLUMN_START_TIME, start_time);
		values.put(MySQLiteHelper.COLUMN_END_TIME, end_time);
		values.put(MySQLiteHelper.COLUMN_EXTRA, extra);
		long insertId = database.insert(MySQLiteHelper.TABLE_ALARMS, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_ALARMS,
				allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		AlarmEntry newAlarm = cursorToAlarmEntry(cursor);
		cursor.close();
		return newAlarm;
	}

	public void deleteAlarm(AlarmEntry alarmEntry) {
		long id = alarmEntry.getId();
		System.out.println("Alarm deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_ALARMS, MySQLiteHelper.COLUMN_ID
				+ " = " + id, null);
		database.delete(MySQLiteHelper.TABLE_ALARMS, MySQLiteHelper.COLUMN_EXTRA
				+ " = " + alarmEntry.getStartPi(), null);
	}
	
	public AlarmEntry findAlarmByPendingIntentIds(int startPI, int endPI)
	{
		Cursor cursor = database.rawQuery("select * from "+MySQLiteHelper.TABLE_ALARMS+" where "+MySQLiteHelper.COLUMN_START_PI+"="+startPI+" and "+MySQLiteHelper.COLUMN_END_PI+" = "+endPI, null);
		cursor.moveToFirst();
		//if(cursor.getCount() > 1)
			//Log.v("ERROR", "findAlarmByPendingIntentIds returned more than one result");
		//cursor.close();
		//return cursorToAlarmEntry(cursor);
		AlarmEntry alarm = new AlarmEntry();
		alarm.setId(cursor.getLong(0));
		alarm.setStartPi(cursor.getInt(1));
		alarm.setEndPi(cursor.getInt(2));
		alarm.setRepeatScheme(cursor.getString(3));
		alarm.setSoundMode(cursor.getString(4));
		alarm.setStartTime(cursor.getString(5));
		alarm.setEndTime(cursor.getString(6));
		alarm.setExtra(cursor.getInt(7));
		cursor.close();
		return alarm;
	}
	
	public List<AlarmEntry> getAlarmsByExtra(int extraToFind) {
		List<AlarmEntry> alarms = new ArrayList<AlarmEntry>();

		Cursor cursor = database.rawQuery("select * from "+MySQLiteHelper.TABLE_ALARMS+" where "+MySQLiteHelper.COLUMN_EXTRA+"="+extraToFind, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			AlarmEntry alarm = cursorToAlarmEntry(cursor);
			alarms.add(alarm);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return alarms;
	}

	public List<AlarmEntry> getAllAlarms() {
		List<AlarmEntry> alarms = new ArrayList<AlarmEntry>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_ALARMS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			AlarmEntry alarm = cursorToAlarmEntry(cursor);
			alarms.add(alarm);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return alarms;
	}
	
	public long getLastInsertRowId() {
		
		 Cursor cursor = database.rawQuery("SELECT last_insert_rowid()", null);
		 cursor.moveToFirst();
		 if(cursor.getCount() > 1)		
		 {
		 	AlarmEntry alarmEntry = cursorToAlarmEntry(cursor);
		 	cursor.close();
		 	return alarmEntry.getId();
		 }
		 else
		 {
			 cursor.close();
			 return 0;
		 }
	}

	private AlarmEntry cursorToAlarmEntry(Cursor cursor) {
		AlarmEntry alarm = new AlarmEntry();
		alarm.setId(cursor.getLong(0));
		alarm.setStartPi(cursor.getInt(1));
		alarm.setEndPi(cursor.getInt(2));
		alarm.setRepeatScheme(cursor.getString(3));
		alarm.setSoundMode(cursor.getString(4));
		alarm.setStartTime(cursor.getString(5));
		alarm.setEndTime(cursor.getString(6));
		alarm.setExtra(cursor.getInt(7));
		//cursor.close();
		return alarm;
	}
}