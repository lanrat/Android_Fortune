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
				return;
			}
			//set the fortune text
			TextView current_fortune = (TextView)mView.findViewById(R.id.home_fortune_text);
			current_fortune.setText(f.getFortuneText(true));
			
			//set the date
			TextView timeText = (TextView) mView.findViewById(R.id.home_fortune_date);
			SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd hh:mm a yyyy",Locale.US);
			timeText.setText(formatter.format(f.getSeen()));
			
			// has voted?
			TextView votedText = (TextView) mView.findViewById(R.id.home_voted);
			if(!f.hasVoted())
				votedText.setText("You not voted for this fortune yet");
			else if (f.getUpvoted())
				votedText.setText("You have up voted");
			else if (f.getDownvoted())
				votedText.setText("You have down voted");
			
			//set the votes
			TextView current_fortune_upcount = (TextView)mView.findViewById(R.id.home_fortune_upvotes);
			TextView current_fortune_downcount = (TextView)mView.findViewById(R.id.home_fortune_downvotes);
			current_fortune_upcount.setText("Upvotes: " + f.getUpvotes());
			current_fortune_downcount.setText("Downvotes: " + f.getDownvotes());

			
			
			
			//TODO display additional data like views, etc...
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mView = inflater.inflate(R.layout.home, container, false);

			mView.findViewById(R.id.home_upvote_image).setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Fortune f = Client.getInstance().getCurrentFortune();
					if (f != null) {
						f.upvote();
						displayFortune(f);
					}
				}
			});
			
			mView.findViewById(R.id.home_downvote_image).setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Fortune f = Client.getInstance().getCurrentFortune();
					if (f != null) {
						f.downvote();
						displayFortune(f);
					}
				}
			});
			

			//if we have no fortune to display, get one!
			if (Client.getInstance().getNumberOfLocalFortunes() == 0) {
				new UpdateFortuneReceiver().onReceive(getActivity().getApplicationContext(), null);
			}else {
				Fortune f = Client.getInstance(getActivity().getApplicationContext()).getCurrentFortune();
				//display the current fortune
				displayFortune(f);
			}
			
			return mView;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
		}
	}

}
