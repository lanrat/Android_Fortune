package com.vorsk.androidfortune;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;


public class SettingsActivity extends SherlockPreferenceActivity implements
	OnSharedPreferenceChangeListener {

	public static final String KEY_INTERVAL = "pref_notification_interval";
    private ListPreference mIntervalPreference;
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);	
		
		 mIntervalPreference = (ListPreference) getPreferenceScreen()
	                .findPreference(KEY_INTERVAL);
	}
	
	 @Override
	 protected void onResume() {
        super.onResume();
        // Setup the initial values
        mIntervalPreference.setSummary(
        		getPreferenceScreen().getSharedPreferences().getString(KEY_INTERVAL, ""));
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }
	 
	@Override
	protected void onPause() {
	    super.onPause();
	    // Unregister the listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
    }
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        if (key.equals(KEY_INTERVAL)) {
        	mIntervalPreference.setSummary(sharedPreferences.getString(KEY_INTERVAL, ""));
        }
    }
	 
	 

}
