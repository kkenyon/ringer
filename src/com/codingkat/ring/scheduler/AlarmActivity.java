package com.codingkat.ring.scheduler;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import android.widget.RelativeLayout;

import com.google.ads.*;
	
import android.widget.TextView;

import com.codingkat.ring.scheduler.R;



//public class AlarmActivity extends Activity implements AdListener{
public class AlarmActivity extends Activity implements AdListener{	
	
	
	public static Context context;
	
	static int defaultStreamVolume = 50;
	
    private RelativeLayout layout;
    
    private TextView scheduleLabel;
    
    public ArrayList<CheckBox> globalcheckbox = new ArrayList<CheckBox>();
    
    public static AlarmsDataSource datasource;
    public static AlarmHistoryDataSource datasourceHist;
    
    public static final String PREFS_NAME = "RingerApp";
    
    public IntentFilter mIntentFilter;
    
    private ScrollView scrollPane;
    
    private LinearLayout linearBlock;
    
    public void onReceiveAd(Ad ad){
 
    }
    public void onFailedToReceiveAd(Ad ad, AdRequest.ErrorCode error){}
    
    public void onDismissScreen(Ad ad){}
    public void onLeaveApplication(Ad ad){}
    public void onPresentScreen(Ad ad){}

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
           case R.id.item1:Log.v("ttt", "You pressed the options menu!");
           	Intent intent = new Intent(AlarmActivity.this,PrefsActivity.class);
           	startActivity(intent);
              break;
           case R.id.item2:Log.v("ttt", "You pressed history menu!");
           		final Dialog dialog = new Dialog(this);

		   		dialog.setContentView(R.layout.custom_dialog);
		   		dialog.setTitle("History");
		   		TextView text = (TextView) dialog.findViewById(R.id.text);
		   		String histText = "";
				datasourceHist = new AlarmHistoryDataSource(this);
				datasourceHist.open();
				List<AlarmHistoryEntry> histList = datasourceHist.getAllAlarmHistory();
				for(int i = 0; i < histList.size(); i++)
				{
					//histText += histList.get(i).toString()+"\n";
					histText += StringUtils.millisTimeToFormat(histList.get(i).getDateTime(), "MM/d/yy hh:mm a")+" "+histList.get(i).getSoundMode()+"\n";
				}
		   		text.setText(histText);
				datasourceHist.close();
				
		   		dialog.show();
		   		
		   		Button closeButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
		   		closeButton.setOnClickListener(new Button.OnClickListener() {      
		   		       public void onClick(View view) { 
		   		       dialog.dismiss();     
		   		       } 
		   		});
                 break;
           case R.id.item3:
        	   //Log.v("ttt", "You pressed info menu!");
      		final Dialog dialogInfo = new Dialog(this);

	   		dialogInfo.setContentView(R.layout.custom_dialog);
	   		dialogInfo.setTitle("About Ringer");
	   		TextView textInfo = (TextView) dialogInfo.findViewById(R.id.text);
	   		textInfo.setAutoLinkMask(2);
	   		textInfo.setText("If you like this app please rate it 5 stars in the app store!  "+"\n\nYour feedback is greatly appreciated. "
	   				+"Please send comments and suggestions to \nnibirumeier@gmail.com"+
	   				"\n"+"\nThank You!");
			
	   		dialogInfo.show();
	   		
	   		Button closeButton2 = (Button) dialogInfo.findViewById(R.id.dialogButtonOK);
	   		closeButton2.setOnClickListener(new Button.OnClickListener() {      
	   		       public void onClick(View view) { 
	   		       dialogInfo.dismiss();     
	   		       } 
	   		});
           		 break;
       }
       return true;
   }

	@Override
	public void onResume() {
		//Log.v("TAG", "onResume");
		//registerReceiver(mIntentReceiver, mIntentFilter);
		super.onResume();
		
		AlarmActivity.context = getApplicationContext();
		
		constructView();
		
		
		mIntentFilter = new IntentFilter();
	    mIntentFilter.addAction("com.codingkat.ring.scheduler.AlarmDeletingDone");
	    
		//initialize the database connection
		datasource = new AlarmsDataSource(this);
		datasource.open();
    	
    	displayView();
    	
    	datasource.close();
	
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    if (datasource != null) {
	    	datasource.close();
	    }
	    if (datasourceHist != null) {
	    	datasourceHist.close();
	    }
	    
	}
	@Override
	protected void onPause() {

	super.onPause();
	}
	
	//methods
	public static int generateUniqueAlarmId(Context context)
	{

	    
	    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
	    
	    int counter = settings.getInt("alarmCount", 1);
		
	    // We need an Editor object to make preference changes.
	    // All objects are from android.context.Context
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putInt("alarmCount", ++counter);

	    // Commit the edits!
	    editor.commit();
	    
	    //Log.v("unique id assign", ""+counter);
	    return counter;

	}
	
	//methods
	public static int generateUniqueNotificationId(Context context)
	{
	    
	    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
	    
	    int counter = settings.getInt("notificationCount", 1);
		
	    // We need an Editor object to make preference changes.
	    // All objects are from android.context.Context
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putInt("notificationCount", ++counter);

	    // Commit the edits!
	    editor.commit();
	    
	    //Log.v("unique notification id assign", ""+counter);
	    return counter;

	}
	
	
	
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

        //setupWidgets();

        // Check if billing is supported.

        
		super.onCreate(savedInstanceState);
		
		AlarmActivity.context = getApplicationContext();
		
		constructView();
		

		
	    
		//create info dialog
		/*Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.custom_dialog);
		dialog.setTitle("Custom Dialog");

		dialog.show();*/
		
		
		mIntentFilter = new IntentFilter();
	    mIntentFilter.addAction("com.codingkat.ring.scheduler.AlarmDeletingDone");
	    
		//initialize the database connection
		datasource = new AlarmsDataSource(this);
		datasource.open();
 
    	
    	displayView();
    	
    	datasource.close();
    	
	}
	
	
	public void constructView()
	{
		setContentView(R.layout.main2);
		
	    scheduleLabel = (TextView) findViewById(R.id.RingerSchedules);
	    
	    layout = (RelativeLayout) findViewById(R.id.mainLayout);
	    

	}
	
	
	public ArrayList<RingerAlarmObject> getAlarmList()
	{
		//get list of alarms from the db
		datasource = new AlarmsDataSource(this);
		datasource.open();
		
		List<AlarmEntry> allAlarms = datasource.getAllAlarms();
		
		ArrayList<RingerAlarmObject> alarmList = new ArrayList<RingerAlarmObject>();
		
		for(int i = 0; i < allAlarms.size(); i++)
		{
			//Log.v("time formats", allAlarms.get(i).getStartTime());
			Calendar startT = Calendar.getInstance();
			Calendar endT = Calendar.getInstance();
			String s = allAlarms.get(i).getStartTime();
			long sLong = Long.parseLong(allAlarms.get(i).getStartTime());
			startT.setTimeInMillis(sLong);
			long eLong = Long.parseLong(allAlarms.get(i).getEndTime());
			endT.setTimeInMillis(eLong);
			//Log.v("time formats", startT.toString());														
			alarmList.add(new RingerAlarmObject(new AlarmObjectParcel(RepeatSchemes.valueOf(allAlarms.get(i).getRepeatScheme()),
					startT,
					endT,
					SoundMode.valueOf(allAlarms.get(i).getSoundMode())),
					allAlarms.get(i).getStartPi(),
					allAlarms.get(i).getEndPi(), false, allAlarms.get(i).getExtra()));
		}
		
		datasource.close();
		return alarmList;
	}
	
	public static void setAlarm(AlarmManager alarmManager, SoundMode sndMode, RepeatSchemes repeatScheme, Calendar startTime, Calendar endTime, PendingIntent startPendingIntent, PendingIntent endPendingIntent, Context context, boolean writeDB, int startPendingIntentID)
	{
		//AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);	
   		//set alarm
		long interval;
		switch(repeatScheme)
		{
		case ONCE:
			if(startPendingIntent != null)
				alarmManager.set(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), startPendingIntent);
			if(endPendingIntent != null)
				alarmManager.set(AlarmManager.RTC_WAKEUP, endTime.getTimeInMillis(), endPendingIntent);

			//Log.v("TAG","end case once");
			//Log.v("alarmSet", ""+startTime.getTime().toString());
			//Log.v("alarmSet", ""+endTime.getTime().toString());
			break;
		case DAILY:
			interval = AlarmManager.INTERVAL_DAY;
			if(startPendingIntent != null)
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), interval, startPendingIntent);
			if(endPendingIntent != null)
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, endTime.getTimeInMillis(), interval, endPendingIntent);	
			break;
		case WEEKLY:
			interval = AlarmManager.INTERVAL_DAY * 7;
			if(startPendingIntent != null)	
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), interval, startPendingIntent);
			if(endPendingIntent != null)
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, endTime.getTimeInMillis(), interval, endPendingIntent);	
			break;
		/*case WEEKDAYS:
			//set alarms for mon-fri
			
			//get today, get day of week
			Calendar startDay = startTime;
			Calendar endDay = endTime;
			Log.v("weekday alarm startime", startDay.toString());
			//int day1 = alarmObj.parcel.startTime.get(Calendar.DAY_OF_WEEK);
			
			interval = AlarmManager.INTERVAL_DAY * 7;
			
			//(1-7) sun - sat
			for(int i = 0; i < 7; i++)
			{				
				if(startDay.get(Calendar.DAY_OF_WEEK) != 1 && startDay.get(Calendar.DAY_OF_WEEK) != 7)
				{
					int uniqueIDS = generateUniqueAlarmId(context);
					int uniqueIDE = generateUniqueAlarmId(context);
					
					if(writeDB)
					{
						Log.v("extra p id", String.valueOf(startPendingIntentID));
						AlarmObjectParcel parcelT = new AlarmObjectParcel(RepeatSchemes.WEEKLY, startTime, endTime, sndMode);
						RingerAlarmObject alarmObj = new RingerAlarmObject(parcelT, uniqueIDS, uniqueIDE, true, startPendingIntentID);
					}
					
					//if(startPendingIntent != null)
					//{
						//start intent
			    		Intent startIntent = new Intent(context, MyBroadcastReceiver.class);
			    		startIntent.putExtra("soundMode", sndMode.name());
			    		PendingIntent startPendingIntent2 = PendingIntent.getBroadcast(context, uniqueIDS, startIntent, 0);


						alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startDay.getTimeInMillis(), interval, startPendingIntent2);
					
					//}
					//if(endPendingIntent != null)		
					//{

			    		
			    		//end intent
			    		Intent endIntent = new Intent(context, MyBroadcastReceiverRing.class);
			    		PendingIntent endPendingIntent2 = PendingIntent.getBroadcast(context, uniqueIDE, endIntent, 0);
			    		
			    		
						alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, endDay.getTimeInMillis(), interval, endPendingIntent2);	    					
					//}
				}
					
				startDay.add(Calendar.DAY_OF_MONTH, 1);
				endDay.add(Calendar.DAY_OF_MONTH, 1);
			}
			break;
		case WEEKENDS:
			//set alarms for sat-sun
			
			//get today, get day of week
			Calendar currDay = startTime;
			Calendar currEndDay = endTime;
			//int day1 = alarmObj.parcel.startTime.get(Calendar.DAY_OF_WEEK);
			
			interval = AlarmManager.INTERVAL_DAY * 7;
			
			//(1-7) sun - sat
			for(int i = 0; i < 7; i++)
			{				
				if(currDay.get(Calendar.DAY_OF_WEEK) == 1 || currDay.get(Calendar.DAY_OF_WEEK) == 7)
				{
					int uniqueIDS = generateUniqueAlarmId(context);
					int uniqueIDE = generateUniqueAlarmId(context);
					
					if(writeDB)
					{
						Log.v("extra p id", String.valueOf(startPendingIntentID));
						AlarmObjectParcel parcelT = new AlarmObjectParcel(RepeatSchemes.WEEKLY, startTime, endTime, sndMode);
						RingerAlarmObject alarmObj = new RingerAlarmObject(parcelT, uniqueIDS, uniqueIDE, true, startPendingIntentID);
					}
					
					//if(startPendingIntent != null)	
					//{
			    		Intent startIntent = new Intent(context, MyBroadcastReceiver.class);
			    		startIntent.putExtra("soundMode", sndMode.name());
			    		PendingIntent startPendingIntent2 = PendingIntent.getBroadcast(context, uniqueIDS, startIntent, 0);

			    		
						alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, currDay.getTimeInMillis(), interval, startPendingIntent2);
					//}
					//if(endPendingIntent != null)
					//{
			    		Intent endIntent = new Intent(context, MyBroadcastReceiverRing.class);
			    		PendingIntent endPendingIntent2 = PendingIntent.getBroadcast(context, uniqueIDE, endIntent, 0);

						alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, currEndDay.getTimeInMillis(), interval, endPendingIntent2);	    					
				    
					//}
				}	
				currDay.add(Calendar.DAY_OF_MONTH, 1);
				currEndDay.add(Calendar.DAY_OF_MONTH, 1);
			}
			break;*/
		}	
    	
	}
	public void displayView()
	{

		
		ArrayList<RingerAlarmObject> alarmList = getAlarmList();
		//Log.v("display alarm list",""+ alarmList.size());
		Collections.sort(alarmList);
    	
    	ArrayList<CheckBox> checkboxes = new ArrayList<CheckBox>();
    	
    	for (int i = 0; i < alarmList.size(); i++)
    	{
    		//if(alarmList.get(i).extra == -1)
    			checkboxes.add(new CheckBox(this));
    	}
    	
    	globalcheckbox = checkboxes;
    	
    	
    	//Log.v("global checkbox list",""+ globalcheckbox.size());
    	
    	for (int i = 0; i < alarmList.size(); i++)
    	{
    		//if(alarmList.get(i).extra == -1)
    		//{
        		//Log.v("forloop", ""+i);
        		
                // test adding a radio button programmatically
        		
        		int dayHeaderId = -1;
        		
        		//Log.v("testi", ""+alarmList.get(i).getDayTextHeader());
        		//if(i!=0)
        		//Log.v("testi-1", ""+alarmList.get(i-1).getDayTextHeader());
        		
        		//display headings
        		//alarmlist must be sorted by start day ascending grouped by repeating
        		if(i == 0 || 
        			(alarmList.get(i).parcel.repeatScheme!=RepeatSchemes.ONCE && alarmList.get(i-1).parcel.repeatScheme==RepeatSchemes.ONCE) ||
        			(!alarmList.get(i).getDayTextHeader().equals(alarmList.get(i-1).getDayTextHeader()) && alarmList.get(i).parcel.repeatScheme==RepeatSchemes.ONCE))
        		{
        			//Log.v("display", ""+i);
            		TextView dayHeader = new TextView(this);
            		dayHeader.setId(alarmList.get(i).hashCode()-1);
            		dayHeaderId = dayHeader.getId();
            		if (alarmList.get(i).parcel.repeatScheme == RepeatSchemes.ONCE)
            		{
                		dayHeader.setText(alarmList.get(i).getDayTextHeader());
            		}
            		else
            		{
                		dayHeader.setText("Repeating");    			
            		}

            		//Log.v("header id", ""+dayHeader.getId());
            		
            		RelativeLayout.LayoutParams params1;
                    if(i==0)
                    {
                    	params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    	params1.addRule(RelativeLayout.BELOW, scheduleLabel.getId());  
                    }
                    else
                    {
                        params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params1.addRule(RelativeLayout.BELOW, ((CheckBox) checkboxes.get(i-1)).getId());             	
                    }
                    dayHeader.setLayoutParams(params1);
            		layout.addView(dayHeader, 0, params1);   			
        		}

        		RelativeLayout.LayoutParams params;
                ((CheckBox) checkboxes.get(i)).setText(alarmList.get(i).displayString());
                ((CheckBox) checkboxes.get(i)).setId(alarmList.get(i).hashCode());
                
                if(dayHeaderId != -1)
                {
                	params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                	params.addRule(RelativeLayout.BELOW, dayHeaderId); 
                }
                else
                {
                    params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW, ((CheckBox) checkboxes.get(i-1)).getId());            	
                }

                
                ((CheckBox) checkboxes.get(i)).setLayoutParams(params);
                
                layout.addView(((CheckBox) checkboxes.get(i)), 0, params);
                
                
    		//}
        
    	}
 
	}
	
	/** add button schedule onclick event 
	 * 
	 * @param view
	 */
	public void addSchedule(View view) {

			Intent nextActivity = new Intent(getApplicationContext(),Screen2Activity.class);
			startActivity(nextActivity);
			//finish();


	}
	public void delSchedule(View view) {

		
		ArrayList<RingerAlarmObject> alarmList = getAlarmList();
		//Log.v("display alarm list",""+ alarmList.size());
		Collections.sort(alarmList);
   
			
			//Log.v("del alarm List global checkbox", ""+globalcheckbox.size());
			//Log.v("del alarm List global checkbox", ""+globalcheckbox.toString());
			
			for(int i = 0; i < alarmList.size(); i++)
			{
				if(globalcheckbox.get(i).isChecked())
				{
					//Log.v("alarm List", alarmList.get(i).toString());
					alarmList.get(i).deleteAlarm(this);
					alarmList.remove(i);
					globalcheckbox.remove(i);
					i--;
				}
			}
			
			layout.removeAllViews();
			
			datasource = new AlarmsDataSource(this);
			datasource.open();
	    	
	    	displayView();
	    	
	    	datasource.close();

	
	}

	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.settingsmenu, menu);
	    return true;
	}
	
 
    
  
 

    
    
}

