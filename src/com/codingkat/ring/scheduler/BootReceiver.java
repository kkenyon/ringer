package com.codingkat.ring.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		//Toast.makeText(context, intent.getAction(),Toast.LENGTH_LONG).show();
		
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			//Toast.makeText(context, "alarms booting up",Toast.LENGTH_LONG).show();
			
			Intent service = new Intent(context, AlarmIntentService.class);
			context.startService(service);
			
			
		
		}
		//otherwise it has to be the alarms loading complete intent com.codingkat.ring.scheduler.AlarmLoadingDone
		else if(intent.getAction().equals("com.codingkat.ring.scheduler.AlarmLoadingDone"))
		{
			//Toast.makeText(context, "idk well see, loading done",Toast.LENGTH_LONG).show();
			//Toast.makeText(context, "Ringer Settings Loaded",Toast.LENGTH_LONG).show();
		}
	}
}