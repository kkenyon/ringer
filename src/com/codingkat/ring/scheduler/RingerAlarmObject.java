package com.codingkat.ring.scheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.util.Log;

public class RingerAlarmObject implements Comparable{

	//data members
	//Context context;
	//CheckBox checkbox;
	//TextView textlabel;
	//static int alarmCount = 0;
	public static final String PREFS_NAME = "RingerApp";
	int startPendingIntentId;
	int endPendingIntentId;
	int extra;
	AlarmObjectParcel parcel;
	
	//constructor
	public RingerAlarmObject(AlarmObjectParcel parcel1, int startPI, int endPI, boolean db, int extra)
	{
		//initialize data members
		//this.context = context1;
		//this.checkbox = new CheckBox(context);
		//this.textlabel = new TextView(context);
		
		//this.startPendingIntentId = AlarmActivity.generateUniqueAlarmId();
		//this.endPendingIntentId = AlarmActivity.generateUniqueAlarmId();
		this.startPendingIntentId = startPI;
		this.endPendingIntentId = endPI;
		this.extra = extra;
		this.parcel = parcel1;
		
		//create the db entry, add a new alarm
		/*AlarmActivity.datasource.execSQL("INSERT INTO "
	            + MySQLiteHelper.TABLE_ALARMS
	            + "("+ MySQLiteHelper.COLUMN_START_PI+","+MySQLiteHelper.COLUMN_END_PI+","+MySQLiteHelper.COLUMN_REPEAT_SCHEME+","+MySQLiteHelper.COLUMN_SOUND_MODE+","+MySQLiteHelper.COLUMN_START_TIME+","+MySQLiteHelper.COLUMN_END_TIME+")"
	           + " VALUES ("+ this.startPendingIntentId + ","+ this.endPendingIntentId + ","+ this.parcel.repeatScheme + ","+ this.parcel.soundMode+ ","+ this.parcel.startTime+","+ this.parcel.endTime+");");*/
		if(db){
			AlarmActivity.datasource = new AlarmsDataSource(AlarmActivity.context);
			AlarmActivity.datasource.open();
			AlarmActivity.datasource.createAlarmEntry(this.startPendingIntentId, this.endPendingIntentId, this.parcel.repeatScheme.toString(), this.parcel.soundMode.toString(), String.valueOf(this.parcel.startTime.getTimeInMillis()), String.valueOf(this.parcel.endTime.getTimeInMillis()), extra);
			AlarmActivity.datasource.close();
			}
	}
	
	public void deleteAlarm(Context context1)
	{
		//cancel pending intents
    	AlarmManager alarmManager = (AlarmManager) context1.getSystemService(Context.ALARM_SERVICE);	
		
    	//start intent
		Intent startIntent = new Intent(context1, MyBroadcastReceiver.class);
		PendingIntent startPendingIntent = PendingIntent.getBroadcast(context1, startPendingIntentId, startIntent, 0);

		
		//end intent
		Intent endIntent = new Intent(context1, MyBroadcastReceiverRing.class);
		PendingIntent endPendingIntent = PendingIntent.getBroadcast(context1, endPendingIntentId, endIntent, 0);
		
		alarmManager.cancel(startPendingIntent);
		startPendingIntent.cancel();
		
		alarmManager.cancel(endPendingIntent);
		endPendingIntent.cancel();
		
		AlarmActivity.datasource = new AlarmsDataSource(AlarmActivity.context);
		AlarmActivity.datasource.open();
		
		List<AlarmEntry> cascadeDelete = AlarmActivity.datasource.getAlarmsByExtra(startPendingIntentId);
		//Log.v("cascade del", ""+cascadeDelete.size());
		
		for(int i = 0; i < cascadeDelete.size(); i++)
		{
			//Log.v("cascade del", ""+cascadeDelete.get(i).getStartPi());
			//Log.v("cascade del", ""+cascadeDelete.get(i).getRepeatScheme());
			//start intent
			Intent startIntent2 = new Intent(context1, MyBroadcastReceiver.class);
			PendingIntent startPendingIntent2 = PendingIntent.getBroadcast(context1, cascadeDelete.get(i).getStartPi(), startIntent2, 0);

			
			//end intent
			Intent endIntent2 = new Intent(context1, MyBroadcastReceiverRing.class);
			PendingIntent endPendingIntent2 = PendingIntent.getBroadcast(context1, cascadeDelete.get(i).getEndPi(), endIntent2, 0);
			
			alarmManager.cancel(startPendingIntent2);
			startPendingIntent2.cancel();
			
			alarmManager.cancel(endPendingIntent2);
			endPendingIntent2.cancel();
		}
		
		//update the db entry, delete the alarm
		
		AlarmEntry delAl = AlarmActivity.datasource.findAlarmByPendingIntentIds(startPendingIntentId, endPendingIntentId);
		AlarmActivity.datasource.deleteAlarm(delAl);
		AlarmActivity.datasource.close();
	}
	
	String getDayTextHeader()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd, yyyy");
		return dateFormat.format(parcel.startTime.getTime());
	}
	String displayString()
	{ 
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

		
		//String startDay = android.text.format.DateFormat.getDateFormat(context).format(parcel.startTime.getTime());
		//String endDay = android.text.format.DateFormat.getDateFormat(context).format(parcel.endTime.getTime());
		
		StringBuilder str = new StringBuilder();
		str.append(StringUtils.toProperCase(parcel.soundMode.toString()));
		str.append(": ");
		
		switch(parcel.repeatScheme)
		{
		case ONCE:
			//str.append("[");
			//str.append(dateFormat.format(parcel.startTime.getTime()));
			//str.append("]");
			//str.append(": ");
			//str.append(android.text.format.DateFormat.getTimeFormat(context).format(parcel.startTime.getTime()));
			str.append(timeFormat.format(parcel.startTime.getTime()));
			str.append(" - ");
			str.append(timeFormat.format(parcel.endTime.getTime()));

			/*if (!startDay.equals(endDay))
			{
				str.append("[");
				str.append(android.text.format.DateFormat.getDateFormat(context).format(parcel.endTime.getTime()));
				str.append("]");
				str.append(" ");
			}*/
			//str.append(android.text.format.DateFormat.getTimeFormat(context).format(parcel.endTime.getTime()));	
			break;
		case DAILY:	
			str.append("Daily ");
			str.append(timeFormat.format(parcel.startTime.getTime()));
			str.append(" - ");
			str.append(timeFormat.format(parcel.endTime.getTime()));
			break;
		case WEEKLY:
			str.append(parcel.startTime.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG,Locale.getDefault()));
			str.append("s ");
			str.append(timeFormat.format(parcel.startTime.getTime()));
			str.append(" - ");
			str.append(timeFormat.format(parcel.endTime.getTime()));
			break;
		/*case WEEKDAYS:
			//str.append(parcel.startTime.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG,Locale.getDefault()));
			//str.append("s ");
			str.append("Mon-Fri ");
			str.append(timeFormat.format(parcel.startTime.getTime()));
			str.append(" - ");
			str.append(timeFormat.format(parcel.endTime.getTime()));
			break;
		case WEEKENDS:
			//str.append(parcel.startTime.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG,Locale.getDefault()));
			//str.append("s ");
			str.append("Sat-Sun ");
			str.append(timeFormat.format(parcel.startTime.getTime()));
			str.append(" - ");
			str.append(timeFormat.format(parcel.endTime.getTime()));
			break;*/
			
		}
		return str.toString();
	}
	
	//sort by start day ascending grouped by repeating
	public int compareTo(Object obj)
	{
		//if this.repeatcode < obj.repeatCode 
		if(this.parcel.repeatScheme.ordinal() < ((RingerAlarmObject)obj).parcel.repeatScheme.ordinal())
		{
			return -1;
		}
		else if(this.parcel.repeatScheme.ordinal() > ((RingerAlarmObject)obj).parcel.repeatScheme.ordinal())
		{
			return 1;
		}
		else if (this.parcel.repeatScheme.ordinal() == ((RingerAlarmObject)obj).parcel.repeatScheme.ordinal())
		{
			if(this.parcel.startTime.before(((RingerAlarmObject)obj).parcel.startTime))
			{
				return -1;
			}
			else if(this.parcel.startTime.after(((RingerAlarmObject)obj).parcel.startTime))
			{
				return 1;
			}
			else if(this.parcel.startTime.equals(((RingerAlarmObject)obj).parcel.startTime))
			{
				return 0;
			}
		}
		
		//Log.v("compare error", "something went wrong");
		return 0;
	}

	
	
}
