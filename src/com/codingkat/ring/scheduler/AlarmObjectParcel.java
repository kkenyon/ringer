package com.codingkat.ring.scheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class AlarmObjectParcel implements Parcelable {

	RepeatSchemes repeatScheme;
	SoundMode soundMode;
	Calendar startTime;
	Calendar endTime;
	
	public AlarmObjectParcel(RepeatSchemes repeatScheme1, Calendar startTime1, Calendar endTime1, SoundMode soundMode1)
	{
		this.repeatScheme = repeatScheme1;
		this.startTime = startTime1;
		this.endTime = endTime1;
		this.soundMode = soundMode1;		
	}
	
	public int describeContents()
	{
		return 0;
	}
	
    public void writeToParcel(Parcel dest, int flags) 
    {
    	dest.writeString(this.repeatScheme.name());
    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    	String sDateTime = dateFormat.format(this.startTime.getTime());
    	dest.writeString(sDateTime);
    	
    	String eDateTime = dateFormat.format(this.endTime.getTime());
    	dest.writeString(eDateTime);
    	
    	dest.writeString(this.soundMode.name());
    }
    
	public static final Parcelable.Creator<AlarmObjectParcel> CREATOR = new Parcelable.Creator<AlarmObjectParcel>() {
		public AlarmObjectParcel createFromParcel(Parcel in) {
			return new AlarmObjectParcel(in);
		}

		public AlarmObjectParcel[] newArray(int size) {
			return new AlarmObjectParcel[size];
		}
	};
	
    private AlarmObjectParcel(Parcel in) {
    	this.repeatScheme = RepeatSchemes.valueOf(in.readString().toUpperCase());
    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    	Calendar sCal = Calendar.getInstance();
    	
    	try {
			Date date = (Date) dateFormat.parse(in.readString());
			//Log.v("TAG",date.toString());
	    	sCal.setTime(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.startTime = sCal;
		
		
    	Calendar eCal = Calendar.getInstance();
    	
    	try {
			Date date = (Date) dateFormat.parse(in.readString());
	    	eCal.setTime(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		this.endTime = eCal;
		
		this.soundMode = SoundMode.valueOf(in.readString().toUpperCase());
		
    }
	
}
