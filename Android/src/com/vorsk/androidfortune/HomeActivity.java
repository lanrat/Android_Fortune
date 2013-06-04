package com.vorsk.androidfortune;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.vorsk.androidfortune.data.Client;
import com.vorsk.androidfortune.data.Fortune;

import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

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
		EasyTracker.getInstance().activityStart(this);
		EasyTracker.getTracker().sendView(FRAGMENT_NAME);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	public static class FortuneFragment extends SherlockFragment {
		
		private static View mView;
		private static final String TAG = "FortuneFragment";

		/**
		 * called to update the fortune displayed on the home page
		 * @param f the fortune to display
		 */
		public static void displayFortune(Fortune f) {
			if (f == null) {
				Log.e(TAG,"Cannot display null fortune");
				return;
			}
			if (mView == null) {
				Log.e(TAG,"view instance is null");
			}
			//set the fortune text
			TextView current_fortune = (TextView)mView.findViewById(R.id.fortune_text);
			current_fortune.setText(f.getFortuneText(true));
			//set the votes
			TextView current_fortune_upcount = (TextView)mView.findViewById(R.id.upvote_count);
			TextView current_fortune_downcount = (TextView)mView.findViewById(R.id.downvote_count);
			current_fortune_upcount.setText(Integer.toString(f.getUpvotes()));
			current_fortune_downcount.setText(Integer.toString(f.getDownvotes()));
			//set the date
			TextView timeText = (TextView) mView.findViewById(R.id.fortune_time);
			SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd hh:mm a yyyy",Locale.US);
			timeText.setText(formatter.format(f.getSubmitted()));
			//TODO display additional data like views, etc...
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mView = inflater.inflate(R.layout.home, container, false);
			Fortune f = Client.getInstance(getActivity().getApplicationContext()).getCurrentFortune();
			if (f != null) {
				TextView current_fortune = (TextView)mView.findViewById(R.id.fortune_text);
				current_fortune.setText(f.getFortuneText(true));
			}
			
			mView.findViewById(R.id.upvote_info).setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Client.getInstance().getCurrentFortune().upvote();
				}
			});
			
			mView.findViewById(R.id.downvote_info).setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Client.getInstance().getCurrentFortune().downvote();
				}
			});

			return mView;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
		}
	}

}
