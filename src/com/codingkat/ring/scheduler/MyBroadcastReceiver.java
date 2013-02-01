package com.codingkat.ring.scheduler;

import java.util.Calendar;

import com.codingkat.ring.scheduler.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
	//public NotificationManager myNotificationManager;
	public static final int NOTIFICATION_ID = 1;
	//private static final int HELLO_ID = 2;
	public AlarmHistoryDataSource datasource;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		

	    //myNotificationManager = (NotificationManager) getSystemService(context.NOTIFICATION_SERVICE);
		datasource = new AlarmHistoryDataSource(context);
		datasource.open();
		
		
		
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		String soundMode = intent.getStringExtra("soundMode").toUpperCase();
		//Log.v("TAG",soundMode);
		
		//Toast.makeText(context, soundMode,Toast.LENGTH_SHORT).show();
		
		//get current ring type and save it in the app preferences as 
		//the last known ringer setting
	    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
	    
	    int counter = settings.getInt("alarmCount", 0);
		
	   // AlarmActivity.last_known_ringer_mode = am.getRingerMode();
	    //AlarmActivity.last_known_ringer_vol = am.getStreamVolume(AudioManager.STREAM_RING);
	    // We need an Editor object to make preference changes.
	    // All objects are from android.context.Context
	   SharedPreferences.Editor editor = settings.edit();

	    
	    //Log.v("unique id assign", ""+counter);

	    int icon =  R.drawable.ic_lock_silent_mode;
	    		
		if(soundMode.equals("SILENT"))
		{
			//Log.v("yes it is","Yes it is");
			am.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_ALLOW_RINGER_MODES); 
			am.setRingerMode(AudioManager.RINGER_MODE_SILENT);	
			//am.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND); 
			am.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_PLAY_SOUND); 
			//Toast.makeText(context, "silent mode",Toast.LENGTH_SHORT).show();
			icon =  R.drawable.ic_lock_silent_mode;
		}
		else if(soundMode.equals("VIBRATE"))
		{
			am.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_ALLOW_RINGER_MODES); 
			am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);	
			//am.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_VIBRATE); 
			am.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_VIBRATE); 
			//Toast.makeText(context, "vibrate mode",Toast.LENGTH_LONG).show();
			icon =  R.drawable.ic_lock_silent_mode_vibrate;
		}
		
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		
		CharSequence tickerText = StringUtils.toProperCase(soundMode)+" Mode On";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		
		Context context1 = context.getApplicationContext();
		CharSequence contentTitle = "Ringer";
		CharSequence contentText = "Your phone was set to "+ soundMode.toLowerCase();
		Intent notificationIntent = new Intent(context1, AlarmActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context1, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context1, contentTitle, contentText, contentIntent);
		
		//int noteNum = AlarmActivity.generateUniqueNotificationId(context);
		//notification.number = noteNum;
		

		mNotificationManager.notify(NOTIFICATION_ID, notification);
		
		Calendar now = Calendar.getInstance();
		
		datasource.createAlarmHistoryEntry(String.valueOf(now.getTimeInMillis()),soundMode);
		datasource.close();
		
		//update ringer mode and vol pref
	    editor.putInt("lastKnownRingerMode", am.getRingerMode());
	    editor.putInt("lastKnownStreamVol", am.getStreamVolume(AudioManager.STREAM_RING));
	    
	    // Commit the edits!
	    editor.commit();

	}

}