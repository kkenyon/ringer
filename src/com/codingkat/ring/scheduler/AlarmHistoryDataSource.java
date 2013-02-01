package com.codingkat.ring.scheduler;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AlarmHistoryDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_HIST_ID,
			MySQLiteHelper.COLUMN_HIST_DATE_TIME,
			MySQLiteHelper.COLUMN_HIST_SOUND_MODE
			};

	public AlarmHistoryDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public AlarmHistoryEntry createAlarmHistoryEntry(String date_time, String sound_mode) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_HIST_SOUND_MODE, sound_mode);
		values.put(MySQLiteHelper.COLUMN_HIST_DATE_TIME, date_time);
		long insertId = database.insert(MySQLiteHelper.TABLE_ALARM_HISTORY, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_ALARM_HISTORY,
				allColumns, MySQLiteHelper.COLUMN_HIST_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		AlarmHistoryEntry newAlarm = cursorToAlarmHistoryEntry(cursor);
		cursor.close();
		return newAlarm;
	}

	public void deleteAlarmHistory(AlarmEntry alarmHistoryEntry) {
		long id = alarmHistoryEntry.getId();
		System.out.println("Alarm History Entry deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_ALARM_HISTORY, MySQLiteHelper.COLUMN_HIST_ID
				+ " = " + id, null);
	}

	public List<AlarmHistoryEntry> getAllAlarmHistory() {
		List<AlarmHistoryEntry> alarmHistory = new ArrayList<AlarmHistoryEntry>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_ALARM_HISTORY,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			AlarmHistoryEntry alarmHistoryEntry = cursorToAlarmHistoryEntry(cursor);
			alarmHistory.add(alarmHistoryEntry);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return alarmHistory;
	}
	
	public long getLastInsertRowId() {
		
		 Cursor cursor = database.rawQuery("SELECT last_insert_rowid()", null);
		 cursor.moveToFirst();
		 if(cursor.getCount() > 1)		
		 {
		 	AlarmHistoryEntry alarmHistEntry = cursorToAlarmHistoryEntry(cursor);
		 	cursor.close();
		 	return alarmHistEntry.getId();
		 }
		 else
		 {
			 cursor.close();
			 return 0;
		 }
	}

	private AlarmHistoryEntry cursorToAlarmHistoryEntry(Cursor cursor) {
		AlarmHistoryEntry alarmHist = new AlarmHistoryEntry();
		alarmHist.setId(cursor.getLong(0));
		alarmHist.setDateTime(cursor.getString(1));
		alarmHist.setSoundMode(cursor.getString(2));
		//cursor.close();
		return alarmHist;
	}
}