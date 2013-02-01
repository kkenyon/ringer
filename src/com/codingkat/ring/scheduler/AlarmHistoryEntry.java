package com.codingkat.ring.scheduler;

import android.database.Cursor;

public class AlarmHistoryEntry {
	private long id;
	private String date_time;
	private String sound_mode;
	
	public AlarmHistoryEntry(Cursor cursor)
	{
		
		id = cursor.getLong(0);
		date_time = cursor.getString(1);
		sound_mode = cursor.getString(2); 
		
	}
	
	public AlarmHistoryEntry() {
		// TODO Auto-generated constructor stub
	}
	
	public String getDateTime() {
		return date_time;
	}

	public void setDateTime(String date_time) {
		this.date_time = date_time;
	}
	
	public String getSoundMode() {
		return sound_mode;
	}

	public void setSoundMode(String sound_mode) {
		this.sound_mode = sound_mode;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return "id: "+id+" datetime: "+date_time+" soundmode: "+sound_mode;
	}
}
