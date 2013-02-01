package com.codingkat.ring.scheduler;

import com.codingkat.ring.scheduler.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.view.View.OnClickListener;

public class DefaultVolDialog extends DialogPreference {

	//EditText pin_et;
	//Button pincancel_but;
	Button pinok_but;
	SeekBar volControl;
	AudioManager audioManager;
	int maxVolume;
	int curVolume;
	SharedPreferences settings;
	
	public DefaultVolDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		 setDialogLayoutResource(R.layout.dialog_preference);
		 audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		 //maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
		 //curVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
		 
		 settings = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	  @Override
      protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
              builder.setTitle("Set Ring Volume");
              builder.setPositiveButton(null, null);
              builder.setNegativeButton(null, null);
              super.onPrepareDialogBuilder(builder);  
      }
	
	  @Override
      public void onBindDialogView(View view){
              //pin_et = (EditText)view.findViewById(R.id.pin_et);

              /*pincancel_but = (Button)view.findViewById(R.id.pincancel_but);
              pincancel_but.setOnClickListener(new OnClickListener() {
                     @Override
                     public void onClick(View v) {
                             getDialog().dismiss();
                     }
              });*/
              pinok_but = (Button)view.findViewById(R.id.pinok_but);
              pinok_but.setOnClickListener(new OnClickListener() {
                      @Override
                      public void onClick(View v) {
                    	  getDialog().dismiss();
                      }
              });
              
              
              volControl = (SeekBar)view.findViewById(R.id.volbar);
              maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
              //curVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
              curVolume = settings.getInt("defaultReturnVol", maxVolume);
              volControl.setMax(maxVolume);
              volControl.setProgress(curVolume);
              volControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                  @Override
                  public void onStopTrackingTouch(SeekBar arg0) {
                  }

                  @Override
                  public void onStartTrackingTouch(SeekBar arg0) {
                  }

                  @Override
                  public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                      audioManager.setStreamVolume(AudioManager.STREAM_RING, arg1, 0);
                      
            	    SharedPreferences.Editor editor = settings.edit();
            	    editor.putInt("defaultReturnVol", arg1);

            	    // Commit the edits!
            	    editor.commit();
            	    
            	    //Log.v("default vol shared setting assigned", "");
            	    
                  }
              });
              super.onBindDialogView(view);
      }
}