package com.codingkat.ring.scheduler;

import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmIntentService extends IntentService {

	public AlarmsDataSource datasource;
	
    public AlarmIntentService() {
        super("AlarmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    	
		//initialize the database connection
		datasource = new AlarmsDataSource(this);
		datasource.open();
		
		List<AlarmEntry> allAlarms = datasource.getAllAlarms();
		
		//Log.v("service loading", ""+allAlarms.size());
		
		for(int i = 0; i <allAlarms.size(); i++)
		{
			if(allAlarms.get(i).getExtra() == -1)
			{
				Calendar startT = Calendar.getInstance();
				Calendar endT = Calendar.getInstance();
				String s = allAlarms.get(i).getStartTime();
				long sLong = Long.parseLong(allAlarms.get(i).getStartTime());
				startT.setTimeInMillis(sLong);
				long eLong = Long.parseLong(allAlarms.get(i).getEndTime());
				endT.setTimeInMillis(eLong);
								
				RingerAlarmObject alarmObj = new RingerAlarmObject(new AlarmObjectParcel(RepeatSchemes.valueOf(allAlarms.get(i).getRepeatScheme()),
						startT,
						endT,
						SoundMode.valueOf(allAlarms.get(i).getSoundMode())),
						allAlarms.get(i).getStartPi(),
						allAlarms.get(i).getEndPi(), false, allAlarms.get(i).getExtra());
					    	
		    	AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);	
				
		    	//Log.v("TAG","before intent creation");
				
		    	PendingIntent startPendingIntent = null;
		    	PendingIntent endPendingIntent = null;

			    	//start intent
					Intent startIntent = new Intent(this, MyBroadcastReceiver.class);
					startIntent.putExtra("soundMode", alarmObj.parcel.soundMode.name());
					startPendingIntent = PendingIntent.getBroadcast(this, alarmObj.startPendingIntentId, startIntent, 0);

					
					//end intent
					Intent endIntent = new Intent(this, MyBroadcastReceiverRing.class);
					endPendingIntent = PendingIntent.getBroadcast(this, alarmObj.endPendingIntentId, endIntent, 0);
					
		    		AlarmActivity.setAlarm(alarmManager, SoundMode.valueOf(allAlarms.get(i).getSoundMode()), RepeatSchemes.valueOf(allAlarms.get(i).getRepeatScheme()), startT, endT, startPendingIntent, endPendingIntent, this, false, alarmObj.startPendingIntentId);
					    		


				//Log.v("TAG","after intent");			
			}

			
    		//AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);	
    		
	
			
			//set alarm
			/*long interval;
			switch(alarmObj.parcel.repeatScheme)
			{
			case ONCE:
				alarmManager.set(AlarmManager.RTC_WAKEUP, alarmObj.parcel.startTime.getTimeInMillis(), startPendingIntent);
				alarmManager.set(AlarmManager.RTC_WAKEUP, alarmObj.parcel.endTime.getTimeInMillis(), endPendingIntent);
				Log.v("TAG","end case once");
				Log.v("alarmSet", ""+alarmObj.parcel.startTime.getTime().toString());
				Log.v("alarmSet", ""+alarmObj.parcel.endTime.getTime().toString());
				break;
			case DAILY:
				interval = AlarmManager.INTERVAL_DAY;
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmObj.parcel.startTime.getTimeInMillis(), interval, startPendingIntent);
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmObj.parcel.endTime.getTimeInMillis(), interval, endPendingIntent);	
				break;
			case WEEKDAYS:
			case WEEKENDS:
			case WEEKLY:
				interval = AlarmManager.INTERVAL_DAY * 7;
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmObj.parcel.startTime.getTimeInMillis(), interval, startPendingIntent);
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmObj.parcel.endTime.getTimeInMillis(), interval, endPendingIntent);	
				break;
			}	*/
	    	
		}
    	//load alarms from the db

		datasource.close();

		Intent i = new Intent("com.codingkat.ring.scheduler.AlarmLoadingDone");
		sendBroadcast(i);

    }
}