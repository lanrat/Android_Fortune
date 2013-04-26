package com.vorsk.androidfortune;

import java.util.List;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;


public class SettingsActivity extends SherlockPreferenceActivity {

	public static final String KEY_INTERVAL = "pref_notification_interval";


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		//Display fragment as main content
		getFragmentManager().beginTransaction().replace(android.R.id.content,
				new Prefs1Fragment()).commit();
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * Populate the activity with the top-level headers.
	 */
	@Override
	public void onBuildHeaders(List<Header> target) {
		//Not used right now, but may switch back to headers if we have a lot of settings
		//loadHeadersFromResource(R.xml.preference_headers, target);
	}

	/**
	 * This fragment shows the preferences for the first header.
	 */
	public static class Prefs1Fragment extends PreferenceFragment implements
	OnSharedPreferenceChangeListener {
		private ListPreference mIntervalPreference;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);
			mIntervalPreference = (ListPreference) getPreferenceScreen().findPreference(KEY_INTERVAL);
		}

		@Override
		public void onResume() {
			super.onResume();
			// Setup the initial values
			mIntervalPreference.setSummary(
					getPreferenceScreen().getSharedPreferences().getString(KEY_INTERVAL, ""));
			// Set up a listener whenever a key changes
			getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
			super.onPause();
			// Unregister the listener whenever a key changes
			getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		}

		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
				String key) {
			if (key.equals(KEY_INTERVAL)) {
				mIntervalPreference.setSummary(sharedPreferences.getString(KEY_INTERVAL, ""));
			}
		}
	}






}
