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

public class SettingsActivity extends SherlockPreferenceActivity {

	public static final String KEY_INTERVAL = "pref_notification_interval";

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// Display fragment as main content
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			addPreferencesFromResource(R.xml.preferences);
		} else {
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new Prefs1Fragment())
					.commit();
		}
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
	public static class Prefs1Fragment extends PreferenceFragment implements
			OnSharedPreferenceChangeListener {
		private ListPreference mIntervalPreference;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);
			mIntervalPreference = (ListPreference) getPreferenceScreen()
					.findPreference(KEY_INTERVAL);
		}

		@Override
		public void onResume() {
			super.onResume();
			// Setup the initial values
			mIntervalPreference.setSummary(getPreferenceScreen()
					.getSharedPreferences().getString(KEY_INTERVAL, ""));
			// Set up a listener whenever a key changes
			getPreferenceScreen().getSharedPreferences()
					.registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
			super.onPause();
			// Unregister the listener whenever a key changes
			getPreferenceScreen().getSharedPreferences()
					.unregisterOnSharedPreferenceChangeListener(this);
		}

		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			if (key.equals(KEY_INTERVAL)) {
				mIntervalPreference.setSummary(sharedPreferences.getString(
						KEY_INTERVAL, ""));
			}
		}
	}

}
