package com.codingkat.ring.scheduler;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StringUtils {

	public static String toProperCase(String s) {
	    return s.substring(0, 1).toUpperCase() +
	               s.substring(1).toLowerCase();
	}
	
	//example "EEE MMM dd, yyyy", "MM/d/yy hh:mm a"
	public static String millisTimeToFormat(String timeInMillis, String formatString)
	{
		//convert millis string to cal object
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(Long.valueOf(timeInMillis));
		
		//get formatted string
		SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
		return dateFormat.format(cal.getTime());
	}
}
