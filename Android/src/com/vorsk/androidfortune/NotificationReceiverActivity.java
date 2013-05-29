package com.vorsk.androidfortune;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.analytics.tracking.android.EasyTracker;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class NotificationReceiverActivity extends SherlockActivity {
	public static final String INTENT_ACTION = "action";
	public static final String INTENT_FORTUNE_ID = "fortuneID";
	public static final int INTENT_ACTION_CLICK = 1;
	public static final int INTENT_ACTION_UPVOTE = 2;
	public static final int INTENT_ACTION_DOWNVOTE = 3;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("TAG", "Got notification activity");
		setContentView(R.layout.notification_result);


		TextView text = (TextView) findViewById(R.id.textView2);
		
		int action = getIntent().getExtras().getInt(INTENT_ACTION);
		
		//get the fortune object
		Client client = Client.getInstance(getApplicationContext());
		int num = getIntent().getExtras().getInt(INTENT_FORTUNE_ID);
		Log.v("Notification Reviever","looking for fortune with id: "+num);
		Fortune f = client.getFortuneByID(num);
		Log.v("Notification Reviever","Got Fortune wth id: "+f.getFortuneID());
		f.markSeen();
		
		String result_text;
		switch (action) {
		case INTENT_ACTION_CLICK:
			result_text = "Clicked";
			break;
		case INTENT_ACTION_UPVOTE:
			result_text = "Upvote";
			f.upvote();
			break;
		case INTENT_ACTION_DOWNVOTE:
			result_text = "Downvote";
			f.downvote();
			break;
		default:
			result_text = "Unknown";
			break;
		}

		text.setText(result_text+" Fortune: "+f.getFortuneText(true));
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