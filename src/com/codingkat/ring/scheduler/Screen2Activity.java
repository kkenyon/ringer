package com.codingkat.ring.scheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.codingkat.ring.scheduler.R;

public class Screen2Activity extends Activity {
	
    private TextView mStartDateDisplay;
    private Button mPickStartDate;
    
    private TextView mEndDateDisplay;
    private Button mPickEndDate;

    private int mYear;
    private int mMonth;
    private int mDay;

    static final int START_DATE_DIALOG_ID = 0;
    static final int END_DATE_DIALOG_ID = 1;
    static final int START_TIME_DIALOG_ID = 3;
    static final int END_TIME_DIALOG_ID = 4;
    
	/** time picker variables **/
    private TextView mStartTimeDisplay;
    private TextView mEndTimeDisplay;
    private Button mPickStartTime;
    private Button mPickEndTime;
   
    private int mHour;
    private int mMinute;
   
    private Calendar startDateTime;
    private Calendar endDateTime;
    
    private Spinner repeatSpin;
    
    private RadioGroup soundModesGroup;
    
    //private RadioButton silentMode;
    
    //private RadioButton vibrateMode;
    
    private String[] repeatSchemesStr;
    
    SimpleDateFormat SEdateFormat = new SimpleDateFormat("EE MMM dd");
   
    
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.screen2);

	        Button doneBtn = (Button) findViewById(R.id.doneBtn);
	        doneBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					save();
					//Log.v("admob vid request","admob vid request made");
					//manager.requestAd();
				}
			});
		
		//populate the repeat schemes from the enum
		RepeatSchemes[] allRepeatSchemes = RepeatSchemes.values();
		repeatSchemesStr = new String[allRepeatSchemes.length];
		for(int i = 0; i < allRepeatSchemes.length; i++)
		{
			switch (allRepeatSchemes[i])
			{
			case ONCE:
			case DAILY:
			case WEEKLY:
				repeatSchemesStr[i] = StringUtils.toProperCase(allRepeatSchemes[i].name());
				break;

			
			}

		}
		
        // capture our View elements
		soundModesGroup = (RadioGroup) findViewById(R.id.soundMode);
		
		//silentMode = (RadioButton) findViewById(R.id.Silent);
		
		//vibrateMode = (RadioButton) findViewById(R.id.Vibrate);
		
        mStartDateDisplay = (TextView) findViewById(R.id.CurrentDay);

        mPickStartDate = (Button) findViewById(R.id.pickDate);
        
        mEndDateDisplay = (TextView) findViewById(R.id.endDay);
        mPickEndDate = (Button) findViewById(R.id.endDate);

        // add a click listener to the button
        mPickStartDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(START_DATE_DIALOG_ID);
            }
        });
        
        mPickEndDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(END_DATE_DIALOG_ID);
            }
        });

        // get the current date
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
		/**
		 * time picker
		 */
        // capture our View elements
        mStartTimeDisplay = (TextView) findViewById(R.id.startTimeLabel);
        mPickStartTime = (Button) findViewById(R.id.start_time);

        mEndTimeDisplay = (TextView) findViewById(R.id.endTimeLabel);
        mPickEndTime = (Button) findViewById(R.id.end_time);
        
        // add a click listener to the button
        mPickStartTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(START_TIME_DIALOG_ID);
            }
        });
        
        mPickEndTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(END_TIME_DIALOG_ID);
            }
        });

        // get the current time
        c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        
        repeatSpin = (Spinner) findViewById(R.id.repeatSpinner);
        
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
               android.R.layout.simple_spinner_item, repeatSchemesStr);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatSpin.setAdapter(spinnerArrayAdapter);
        
        startDateTime = Calendar.getInstance();
        endDateTime = Calendar.getInstance();
        
        endDateTime.add(Calendar.HOUR_OF_DAY,1);
     
        
	    mStartDateDisplay.setText(new StringBuilder().append(SEdateFormat.format(startDateTime.getTime())));
	    mEndDateDisplay.setText(new StringBuilder().append(SEdateFormat.format(endDateTime.getTime())));
	    mStartTimeDisplay.setText(new StringBuilder().append(android.text.format.DateFormat.getTimeFormat(this).format(startDateTime.getTime())));
	    mEndTimeDisplay.setText(new StringBuilder().append(android.text.format.DateFormat.getTimeFormat(this).format(endDateTime.getTime())));
	    
	    
	}
	

	/**
	 * time picker functions
	 */
	// updates the time we display in the TextView
	private void updateStartTimeDisplay() {
	    
		//Toast.makeText(Screen2Activity.this, "update state time display", Toast.LENGTH_LONG).show();
		Calendar userStartDateTime = Calendar.getInstance();

		userStartDateTime.set(startDateTime.get(Calendar.YEAR),startDateTime.get(Calendar.MONTH), startDateTime.get(Calendar.DATE), mHour, mMinute, 0);
		
		
		//userStartDateTime.set(Calendar.YEAR, startDateTime.get(Calendar.YEAR));
		//userStartDateTime.set(Calendar.MONTH, startDateTime.get(Calendar.MONTH));
		//userStartDateTime.set(Calendar.DATE, startDateTime.get(Calendar.DATE));
		Log.v("userStartDateTime after y m d set",""+userStartDateTime.toString());
		//userStartDateTime.set(Calendar.HOUR_OF_DAY, mHour);
		//userStartDateTime.set(Calendar.MINUTE, mMinute);
		//userStartDateTime.set(Calendar.SECOND, 0);
		
	    
	    //make syre start time is not in the past
    	Calendar now = Calendar.getInstance();
    	
    	Log.v("userStartDateTime after set",""+userStartDateTime.toString());
    	Log.v("now",""+now.toString());
    	Log.v("endDate",""+endDateTime.toString());
    	
    	Log.v("userstart date after now",""+userStartDateTime.after(now)); 
    	Log.v("userend date after start date",""+endDateTime.after(userStartDateTime)); 
    	Log.v("userstart time after end time",""+userStartDateTime.after(endDateTime)); 
    	
	    //make sure start time is not after end time
    	//if(userStartDateTime.after(now) && endDateTime.after(userStartDateTime))
    	if(endDateTime.after(userStartDateTime))
    	{
    		
    		
    		startDateTime = userStartDateTime;
    		
    	    //mStartDateDisplay.setText(new StringBuilder().append(SEdateFormat.format(startDateTime.getTime())));
    		mStartTimeDisplay.setText(new StringBuilder().append(android.text.format.DateFormat.getTimeFormat(this).format(startDateTime.getTime())));
    	}
    	//if end date is before the start date change the end date
    	if(userStartDateTime.after(endDateTime))
	    {
    		startDateTime = userStartDateTime;
	    	endDateTime = startDateTime;     
    	    
	    	endDateTime.add(Calendar.HOUR_OF_DAY,1);
	    	
    	    mEndDateDisplay.setText(new StringBuilder().append(SEdateFormat.format(startDateTime.getTime())));

    		
    	    mStartDateDisplay.setText(new StringBuilder().append(SEdateFormat.format(startDateTime.getTime())));

	    } 
	    
	    
	    //mStartTimeDisplay.setText(new StringBuilder().append(android.text.format.DateFormat.getTimeFormat(this).format(startDateTime.getTime())));
	}
	
	private void updateEndTimeDisplay() {
		
		//Toast.makeText(Screen2Activity.this, "update end time display", Toast.LENGTH_LONG).show();
		Calendar userEndTime = Calendar.getInstance();
		userEndTime.set(Calendar.YEAR, endDateTime.get(Calendar.YEAR));
		userEndTime.set(Calendar.MONTH, endDateTime.get(Calendar.MONTH));
		userEndTime.set(Calendar.DATE,endDateTime.get(Calendar.DAY_OF_MONTH));
		userEndTime.set(Calendar.HOUR_OF_DAY, mHour);
		userEndTime.set(Calendar.MINUTE, mMinute);
		userEndTime.set(Calendar.SECOND, 0);
	    
	    
	    //make sure the end time is not before the start time
	    if(!userEndTime.before(startDateTime))
	    {
	    	endDateTime = userEndTime;
	    	mEndTimeDisplay.setText(new StringBuilder().append(android.text.format.DateFormat.getTimeFormat(this).format(endDateTime.getTime())));
	    }

	}
	
	// the callback received when the user "sets" the time in the dialog
	private TimePickerDialog.OnTimeSetListener mStartTimeSetListener =
	    new TimePickerDialog.OnTimeSetListener() {
	        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	        	
	        	//if the start time is in the past do not let them do it
	            mHour = hourOfDay;
	            mMinute = minute;
	    	    
	            updateStartTimeDisplay();
		}
	};

	// the callback received when the user "sets" the time in the dialog
	private TimePickerDialog.OnTimeSetListener mEndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinute = minute;

			updateEndTimeDisplay();
			
    	    
		}
	};
    
    /*private void updateEndDateDisplay() {
    	
	    endDateTime.set(Calendar.MONTH, mMonth);
	    endDateTime.set(Calendar.DATE, mDay);
	    endDateTime.set(Calendar.YEAR, mYear);
	    
	    mEndDateDisplay.setText(new StringBuilder().append(android.text.format.DateFormat.getDateFormat(this).format(endDateTime.getTime())));
  
	    //updateRepeatList();
    }*/
    
    private DatePickerDialog.OnDateSetListener mStartDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                	
                	Calendar now = Calendar.getInstance();
                	Calendar userStartDateTime = Calendar.getInstance();
                	userStartDateTime.set(Calendar.MONTH, monthOfYear);
                	userStartDateTime.set(Calendar.DATE, dayOfMonth);
                	userStartDateTime.set(Calendar.YEAR, year);
            		userStartDateTime.set(Calendar.HOUR_OF_DAY, startDateTime.get(Calendar.HOUR_OF_DAY));
            		userStartDateTime.set(Calendar.MINUTE, startDateTime.get(Calendar.MINUTE));
            		userStartDateTime.set(Calendar.SECOND, startDateTime.get(Calendar.SECOND));
                	
            	    //make sure start time is not in the past and is not after the end date time
                	//if(userStartDateTime.after(now) && !userStartDateTime.after(endDateTime))
            		if(!userStartDateTime.after(endDateTime))
                	{
                		startDateTime = userStartDateTime;
                	    mStartDateDisplay.setText(new StringBuilder().append(SEdateFormat.format(startDateTime.getTime())));

                	}
                	//if end date is before the start date change the end date
                	if(userStartDateTime.after(endDateTime))
            	    {
                		startDateTime = userStartDateTime;
            	    	endDateTime = startDateTime;     
            	    	endDateTime.add(Calendar.HOUR_OF_DAY,1);
                	    
                	    mEndDateDisplay.setText(new StringBuilder().append(SEdateFormat.format(startDateTime.getTime())));
                	    
                	    mStartDateDisplay.setText(new StringBuilder().append(SEdateFormat.format(startDateTime.getTime())));

            	    } 

                }
	};
	
    private DatePickerDialog.OnDateSetListener mEndDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                	
                	//if the date is in the past don't let them do it
                	Calendar now = Calendar.getInstance();
                	Calendar userEndDateTime = Calendar.getInstance();
                	userEndDateTime.set(Calendar.MONTH, monthOfYear);
                	userEndDateTime.set(Calendar.DATE, dayOfMonth);
                	userEndDateTime.set(Calendar.YEAR, year);
                	userEndDateTime.set(Calendar.HOUR_OF_DAY, endDateTime.get(Calendar.HOUR_OF_DAY));
                	userEndDateTime.set(Calendar.MINUTE, endDateTime.get(Calendar.MINUTE));
                	userEndDateTime.set(Calendar.SECOND, endDateTime.get(Calendar.SECOND));

                	
                	//make sure the end time is not before the start time
                	if(!userEndDateTime.before(startDateTime))
                	{
                	    endDateTime = userEndDateTime;
                	    
                	    mEndDateDisplay.setText(new StringBuilder().append(SEdateFormat.format(endDateTime.getTime())));
              		
                	}
                	
                }
	};


	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case START_DATE_DIALOG_ID:
			return new DatePickerDialog(this, mStartDateSetListener, mYear, mMonth,
					mDay);
		case END_DATE_DIALOG_ID:
			return new DatePickerDialog(this, mEndDateSetListener, mYear, mMonth,
					mDay);
        case START_TIME_DIALOG_ID:
            return new TimePickerDialog(this,
                    mStartTimeSetListener, mHour, mMinute, false);
        case END_TIME_DIALOG_ID:
            return new TimePickerDialog(this,
                    mEndTimeSetListener, mHour, mMinute, false);
		}
		return null;
	}
	
	
	/*public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        Log.d(this.getClass().getName(), "back button pressed");
			Intent nextActivity = new Intent(getApplicationContext(),AlarmActivity.class);
			startActivity(nextActivity);
	    }
	    return super.onKeyDown(keyCode, event);
	}*/
	
	public void save() {
		
		//Log.v("TAG","begin save");
		
		Intent nextActivity = new Intent(getApplicationContext(),AlarmActivity.class);
		

		RepeatSchemes repeat;
		
		String repeatSchemeDisplayString = (String)repeatSpin.getSelectedItem();
		

			repeat = RepeatSchemes.valueOf(((String)repeatSpin.getSelectedItem()).toUpperCase());

		RadioButton selectedMode = (RadioButton) findViewById(soundModesGroup.getCheckedRadioButtonId());		
		SoundMode mode = SoundMode.valueOf(((String)selectedMode.getText()).toUpperCase());
		
		//Log.v("sound mode vib", ""+mode.name());
		
		AlarmObjectParcel ringerParcel = new AlarmObjectParcel(repeat, startDateTime, endDateTime, mode);
		
		Bundle b = new Bundle();
		b.putParcelable("alarm", ringerParcel);
		
		nextActivity.putExtras(b);
		
		//Log.v("TAG","end save");
		
		//startActivity(nextActivity);
		
		//get an information passed from another screen
    	//Bundle extras = getIntent().getExtras(); 
		Bundle extras = b;
		
    	if (extras != null)
    	{

    	    
    		//Log.v("TAG","extra processing begin");
        	AlarmObjectParcel alarmObjParcel = extras.getParcelable("alarm");
        	
        	AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);	
        	

        		//Log.v("repeat scheme fork","not weekdays or weendends");
            	RingerAlarmObject alarmObj = new RingerAlarmObject(alarmObjParcel, AlarmActivity.generateUniqueAlarmId(this), AlarmActivity.generateUniqueAlarmId(this), true, -1);
        		
            	//Log.v("TAG","before intent creation");
        		
            	//start intent
        		Intent startIntent = new Intent(this, MyBroadcastReceiver.class);
        		startIntent.putExtra("soundMode", alarmObj.parcel.soundMode.name());
        		PendingIntent startPendingIntent = PendingIntent.getBroadcast(this, alarmObj.startPendingIntentId, startIntent, 0);

        		
        		//end intent
        		Intent endIntent = new Intent(this, MyBroadcastReceiverRing.class);
        		PendingIntent endPendingIntent = PendingIntent.getBroadcast(this, alarmObj.endPendingIntentId, endIntent, 0);
        		
        		//Log.v("TAG","after intent");
        		
        		
        		
        		AlarmActivity.setAlarm(alarmManager, alarmObj.parcel.soundMode, alarmObj.parcel.repeatScheme, alarmObj.parcel.startTime, alarmObj.parcel.endTime, startPendingIntent, endPendingIntent, this, false, alarmObj.startPendingIntentId);
        		       		


    		getIntent().removeExtra("alarm");
    	}
		
    	 finish();
	}
    
}
