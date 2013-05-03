package com.vorsk.androidfortune;

import java.util.List;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsActivity extends SherlockPreferenceActivity implements
	OnSharedPreferenceChangeListener  {

	public static final String KEY_INTERVAL = "pref_notification_interval";
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

	public void onSharedPreferenceChanged(
			SharedPreferences sharedPreferences, String key) {
		if (key.equals(KEY_INTERVAL)) {
			mIntervalPreference.setSummary(sharedPreferences.getString(
					KEY_INTERVAL, ""));
		}
	}
}
