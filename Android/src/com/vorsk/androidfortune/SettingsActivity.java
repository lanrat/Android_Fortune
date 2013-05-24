package com.vorsk.androidfortune;

import java.util.Date;
import java.util.List;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsActivity extends SherlockPreferenceActivity implements
	OnSharedPreferenceChangeListener  {

	public static final String KEY_INTERVAL = "pref_notification_interval";
	public static final String KEY_TIME_PREF = "pref_notification_time";
	private ListPreference mIntervalPreference;
	private SharedPreferences prefs;
	
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// Display fragment as main content
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			addPreferencesFromResource(R.xml.preferences);
			 mIntervalPreference = (ListPreference) (ListPreference)getPreferenceScreen()
		                .findPreference(KEY_INTERVAL);
		} else {
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new Prefs1Fragment())
					.commit();
		}
			
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return false;
		}
	}

	/**
	 * Populate the activity with the top-level headers.
	 */
	@Override
	public void onBuildHeaders(List<Header> target) {
		// Not used right now, but may switch back to headers if we have a lot
		// of settings
		// loadHeadersFromResource(R.xml.preference_headers, target);
	}

	/**
	 * This fragment shows the preferences for the first header.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class Prefs1Fragment extends PreferenceFragment{
		public static ListPreference mIntervalPreference;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);
			mIntervalPreference = (ListPreference) getPreferenceScreen()
					.findPreference(KEY_INTERVAL);
			
			Preference button = (Preference)findPreference("pref_database_clear");
			button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			                @Override
			                public boolean onPreferenceClick(Preference arg0) {
			                	Log.v("Local Database", "clear database");
			                    FortuneDbAdapter.getInstance(null).removeAll();
			                    return true;
			                }
			            });
		}
	}

	
	@Override
	public void onResume() {
		super.onResume();
		
		/* slightly hacky way of getting the fragment's list preference.
		  I tried to do it in onCreate, but the preference is still null at that point.
		  this way, at least the deprecated code is kept together.*/
		if ( mIntervalPreference == null )
			mIntervalPreference = Prefs1Fragment.mIntervalPreference;
		
		// Setup the initial values
		mIntervalPreference.setSummary(prefs.getString(KEY_INTERVAL, ""));
		// Set up a listener whenever a key changes
		prefs.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		prefs.unregisterOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged( SharedPreferences prefs, String key) {
		if (key.equals(KEY_INTERVAL)) {
			mIntervalPreference.setSummary(prefs.getString(KEY_INTERVAL, ""));
			
		}
		
		//Update Alarm
		if ( prefs.getBoolean("pref_enable_notification",false) ) {
			long time = prefs.getLong(KEY_TIME_PREF, 0);
			Log.v("Settings", "Alarm set to " + new Date(time).toString() );
			Intent intent = new Intent(this, UpdateFortuneReceiver.class);
		    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
		        intent, PendingIntent.FLAG_CANCEL_CURRENT);
		    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		    am.cancel(pendingIntent);	
		    am.setInexactRepeating(AlarmManager.RTC_WAKEUP, time,
		       AlarmManager.INTERVAL_DAY, pendingIntent);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}
}
