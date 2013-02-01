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

public class MyBroadcastReceiverRing extends BroadcastReceiver {
	public static final int NOTIFICATION_ID = 1;
	public AlarmHistoryDataSource datasource;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		datasource = new AlarmHistoryDataSource(context);
		datasource.open();
		
		//make a notification
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		
		CharSequence tickerText = "Ringer On";
		long when = System.currentTimeMillis();
		int icon =  R.drawable.ic_lock_silent_mode_off;

		Notification notification = new Notification(icon, tickerText, when);
		
		Context context1 = context.getApplicationContext();
		CharSequence contentTitle = "Ringer";
		CharSequence contentText = "Your phone was set to ring";
		Intent notificationIntent = new Intent(context1, AlarmActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context1, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context1, contentTitle, contentText, contentIntent);
		
		

		mNotificationManager.notify(NOTIFICATION_ID, notification);
		//end notification code
		Calendar now = Calendar.getInstance();
		
		
		
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		//int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_RING);
		
  	    
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_RING);
	    int defaultReturnVol = settings.getInt("defaultReturnVol", maxVol);
		
	    datasource.createAlarmHistoryEntry(String.valueOf(now.getTimeInMillis()),"DEFAULT RETURN VOL "+defaultReturnVol);
		datasource.close();
		
	    // We need an Editor object to make preference changes.
	    // All objects are from android.context.Context
	    
		//am.setStreamVolume(AudioManager.STREAM_RING, maxVol, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND); 	
		am.setStreamVolume(AudioManager.STREAM_RING, defaultReturnVol, AudioManager.FLAG_PLAY_SOUND); 
	    
	    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);	
		
		
	   // am.setStreamVolume(AudioManager.STREAM_RING, lastKnownStreamVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND); 
	    
		//Toast.makeText(context, "normal mode",Toast.LENGTH_LONG).show();
		
		//run a service that cycles through the alarm list and deletes any entries that have already passed
		//Toast.makeText(context, "deleting past alarms",Toast.LENGTH_LONG).show();
		
		Intent service = new Intent(context, AlarmDeleteIntentService.class);
		
		//call another service that
		context.startService(service);
	}

}