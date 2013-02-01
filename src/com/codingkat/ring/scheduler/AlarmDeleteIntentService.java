package com.codingkat.ring.scheduler;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmDeleteIntentService extends IntentService {

	public AlarmsDataSource datasource;
	
    public AlarmDeleteIntentService() {
        super("AlarmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    
		//initialize the database connection
		datasource = new AlarmsDataSource(this);
		datasource.open();
		
		List<AlarmEntry> allAlarms = datasource.getAllAlarms();
		
		//Log.v("auto delete service loading", ""+allAlarms.size());
		
		Calendar currentTime = Calendar.getInstance();
		
		for(int i = 0; i <allAlarms.size(); i++)
		{
			//Log.v("delete loop", ""+allAlarms.size());
			
			Calendar startT = Calendar.getInstance();
			Calendar endT = Calendar.getInstance();
			String s = allAlarms.get(i).getStartTime();
			long sLong = Long.parseLong(allAlarms.get(i).getStartTime());
			startT.setTimeInMillis(sLong);
			long eLong = Long.parseLong(allAlarms.get(i).getEndTime());
			endT.setTimeInMillis(eLong);
							
			//Log.v("getting once alarms", ""+allAlarms.get(i).getRepeatScheme());
			//Log.v("getting once alarms equals once", ""+allAlarms.get(i).getRepeatScheme().toUpperCase().equals("ONCE"));
			
			if((currentTime.equals(endT) || currentTime.after(endT)) && allAlarms.get(i).getRepeatScheme().toUpperCase().equals("ONCE"))
			{
				//cancel pending intents
		    	AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);	
				
		    	//start intent
				Intent startIntent = new Intent(this, MyBroadcastReceiver.class);
				PendingIntent startPendingIntent = PendingIntent.getBroadcast(this, allAlarms.get(i).getStartPi(), startIntent, 0);

				
				//end intent
				Intent endIntent = new Intent(this, MyBroadcastReceiverRing.class);
				PendingIntent endPendingIntent = PendingIntent.getBroadcast(this, allAlarms.get(i).getEndPi(), endIntent, 0);
				
				alarmManager.cancel(startPendingIntent);
				startPendingIntent.cancel();
				
				alarmManager.cancel(endPendingIntent);
				endPendingIntent.cancel();
				
				//update the db entry, delete the alarm
				AlarmEntry delAl = datasource.findAlarmByPendingIntentIds(allAlarms.get(i).getStartPi(), allAlarms.get(i).getEndPi());
				datasource.deleteAlarm(delAl);
				
		
			}

		}

		//Log.v("delete intent status", "alarms deleting loop done");
		
		allAlarms = datasource.getAllAlarms();
		
		//allAlarms = datasource.getAllAlarms();
		//check to see if any alarms should be reactivated by creating a new start pending intent...
		//this will be necessary to return to previously set alarm settings if there was overlap
		AlarmEntry reactiveAlrm = null;
		Calendar now = Calendar.getInstance();
		
		for(int i = 0; i <allAlarms.size(); i++)
		{
			//Log.v("reactive2 loop", ""+allAlarms.size());
			
			//if(RepeatSchemes.valueOf(allAlarms.get(i).getRepeatScheme()) != RepeatSchemes.WEEKENDS && RepeatSchemes.valueOf(allAlarms.get(i).getRepeatScheme()) != RepeatSchemes.WEEKDAYS )
			
			//{
				Calendar startT = Calendar.getInstance();
				String s = allAlarms.get(i).getStartTime();
				long sLong = Long.parseLong(allAlarms.get(i).getStartTime());
				startT.setTimeInMillis(sLong);
				
				Calendar endT = Calendar.getInstance();
				String s2 = allAlarms.get(i).getEndTime();
				long sLong2 = Long.parseLong(allAlarms.get(i).getEndTime());
				endT.setTimeInMillis(sLong2);
				
				if(RepeatSchemes.valueOf(allAlarms.get(i).getRepeatScheme()) == RepeatSchemes.WEEKLY)
				{
					//Log.v("reactive weekly", ""+allAlarms.size());
					if(now.get(Calendar.DAY_OF_WEEK) == startT.get(Calendar.DAY_OF_WEEK))
					{
						//Log.v("reactive weekly2", "day of week the same");
						if(endT.after(now))
						{
							if(reactiveAlrm == null)						
							{
								reactiveAlrm = allAlarms.get(i);
							}
							else
							{
								if(startT.after(reactiveAlrm))
								{
									reactiveAlrm = allAlarms.get(i);
								}
							}						
						}

					}
				}
				else{
					//if start time is before the current time, this alarm needs to possibly be activated
					//if there are more than one alarm that have start times before the current time then the 
					//one with the latest start time needs to be the one the ringer type is set to
					if(startT.before(now) && endT.after(now) )
					{
						if(RepeatSchemes.valueOf(allAlarms.get(i).getRepeatScheme()) == RepeatSchemes.ONCE)
						{
							if(reactiveAlrm == null)							
							{
								reactiveAlrm = allAlarms.get(i);
							}
							else
							{
								if(startT.after(reactiveAlrm))
								{
									reactiveAlrm = allAlarms.get(i);
								}
							}
						}
					
					}
					
				}					
			//}
				
				


			
		}
		
		//Log.v("delete intent status", "reactive loop done");
		
		if(reactiveAlrm != null)
		{
			
    		Intent startIntent = new Intent(this, MyBroadcastReceiver.class);
    		startIntent.putExtra("soundMode", reactiveAlrm.getSoundMode());
    		PendingIntent startPendingIntent = PendingIntent.getBroadcast(this, reactiveAlrm.getStartPi(), startIntent, 0);
    		
    		//Log.v("reactive alarm info", reactiveAlrm.toString());
    		
    		RepeatSchemes repeatScheme = RepeatSchemes.valueOf(reactiveAlrm.getRepeatScheme());
    		Calendar startTime = Calendar.getInstance();
    		startTime.setTimeInMillis(Long.parseLong(reactiveAlrm.getStartTime()));
    		Calendar endTime = Calendar.getInstance();
    		endTime.setTimeInMillis(Long.parseLong(reactiveAlrm.getEndTime()));
    		
    		
    		SoundMode sndMode = SoundMode.valueOf(reactiveAlrm.getSoundMode());
    		
    		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);	
    		
    		AlarmActivity.setAlarm(alarmManager, sndMode, repeatScheme, startTime , endTime, startPendingIntent, null, this, false, reactiveAlrm.getStartPi());
    		
		}
		
		datasource.close();
		
		Intent i = new Intent("com.codingkat.ring.scheduler.AlarmDeletingDone");
		sendBroadcast(i);

    }
}