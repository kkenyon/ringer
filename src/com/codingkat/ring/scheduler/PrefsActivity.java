package com.codingkat.ring.scheduler;

import com.codingkat.ring.scheduler.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;


public class PrefsActivity extends PreferenceActivity{

@Override
protected void onCreate(Bundle savedInstanceState) {
   super.onCreate(savedInstanceState);
   addPreferencesFromResource(R.layout.preferences);
}
}
