package com.vorsk.androidfortune;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.vorsk.androidfortune.data.Client;
import com.vorsk.androidfortune.data.Fortune;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class HistoryActivity extends SherlockFragmentActivity {
	
	public static final String FRAGMENT_NAME = "History Activity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentManager fm = getSupportFragmentManager();

		// Create the list fragment and add it as our sole content.
		if (fm.findFragmentById(R.id.historyFragmentId) == null) {
			HistoryFragment fragment = new HistoryFragment();
			fm.beginTransaction().add(R.id.historyFragmentId, fragment).commit();
		}
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
		EasyTracker.getTracker().sendView(FRAGMENT_NAME);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}
	
	public static class HistoryFragment extends SherlockFragment {
		
		private static View mView;
		
		public static void refreshHistory() {
			if (mView == null) {
				return;
			}
			ArrayList<Fortune> list = Client.getInstance().getSeenFortunes(); 
			//Collections.reverse(list); // reverse chronological order
			
			Fortune[] fortunes = list.toArray(new Fortune[list.size()]);
			//TODO: sorting should be done in DB, also limit fortunes
			Arrays.sort(fortunes, Collections.reverseOrder());
			
			FortuneArrayAdapter adapter = new FortuneArrayAdapter(mView.getContext(), fortunes);
			ListView lv = (ListView) mView.findViewById(R.id.history_list);
			lv.setAdapter(adapter);	
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			mView = inflater.inflate(R.layout.history, container, false);
			return mView;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			refreshHistory();	
		}

	}
}
