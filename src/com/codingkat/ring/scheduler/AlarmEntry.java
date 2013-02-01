package com.codingkat.ring.scheduler;

import android.database.Cursor;

public class AlarmEntry {
	private long id;
	private int start_pi;
	private int end_pi;
	private String repeat_scheme;
	private String sound_mode;
	private String start_time;
	private String end_time;
	private int extra;
	
	public AlarmEntry(Cursor cursor)
	{
		
		id = cursor.getLong(0);
		start_pi = cursor.getInt(1); 
		end_pi = cursor.getInt(2); 
		repeat_scheme = cursor.getString(3); 
		sound_mode = cursor.getString(4); 
		start_time = cursor.getString(5);
		end_time = cursor.getString(6); 
		extra = cursor.getInt(7); 
	}
	
	public AlarmEntry() {
		// TODO Auto-generated constructor stub
	}
	
	public int getExtra() {
		return extra;
	}

	public void setExtra(int extra) {
		this.extra = extra;
	}
	
	public String getEndTime() {
		return end_time;
	}

	public void setEndTime(String end_time) {
		this.end_time = end_time;
	}
	
	public String getStartTime() {
		return start_time;
	}

	public void setStartTime(String start_time) {
		this.start_time = start_time;
	}
	
	
	public String getSoundMode() {
		return sound_mode;
	}

	public void setSoundMode(String sound_mode) {
		this.sound_mode = sound_mode;
	}
	
	public String getRepeatScheme() {
		return repeat_scheme;
	}

	public void setRepeatScheme(String repeat_scheme) {
		this.repeat_scheme = repeat_scheme;
	}
	
	
	public int getEndPi() {
		return end_pi;
	}

	public void setEndPi(int end_pi) {
		this.end_pi = end_pi;
	}
	
	public int getStartPi() {
		return start_pi;
	}

	public void setStartPi(int start_pi) {
		this.start_pi = start_pi;
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
		return "id: "+id+" startpi: "+start_pi+" endpi: "+end_pi+" repeatscheme: "+repeat_scheme+" soundmode: "+sound_mode+" starttime: "+start_time+" endtime: "+end_time+" extra: "+extra;
	}
}
