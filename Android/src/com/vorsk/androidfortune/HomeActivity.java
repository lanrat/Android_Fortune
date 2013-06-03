package com.vorsk.androidfortune;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeActivity extends SherlockFragmentActivity {
	
	public static final String FRAGMENT_NAME = "Home Screen";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentManager fm = getSupportFragmentManager();

		// Create the list fragment and add it as our sole content.
		if (fm.findFragmentById(android.R.id.content) == null) {
			FortuneFragment fragment = new FortuneFragment();
			fm.beginTransaction().add(android.R.id.content, fragment).commit();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.e("AAA","onstart");
		EasyTracker.getInstance().activityStart(this);
		EasyTracker.getTracker().sendView(FRAGMENT_NAME);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	public static class FortuneFragment extends SherlockFragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			return inflater.inflate(R.layout.home, null);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
		}
	}

}
