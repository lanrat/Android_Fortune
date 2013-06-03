package com.vorsk.androidfortune;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.vorsk.androidfortune.data.Client;
import com.vorsk.androidfortune.data.Fortune;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class NotificationActivity extends SherlockActivity {
	public static final String INTENT_ACTION = "action";
	public static final String INTENT_FORTUNE_ID = "fortuneID";
	public static final int INTENT_ACTION_CLICK = 1;
	public static final int INTENT_ACTION_UPVOTE = 2;
	public static final int INTENT_ACTION_DOWNVOTE = 3;
	public static final int ID_ACTION_CLICK = 0;
	public static final int ID_ACTION_UPVOTE = 1;
	public static final int ID_ACTION_DOWNVOTE = 2;
	public static final int ID_NOTIFICATION = 12; //rand
	
	private static final String TAG = "NotificationActivity";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("TAG", "Got notification activity");

		int action = getIntent().getExtras().getInt(INTENT_ACTION);
		
		NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(ID_NOTIFICATION);
		
		//get the fortune object
		Client client = Client.getInstance(getApplicationContext());
		int num = getIntent().getExtras().getInt(INTENT_FORTUNE_ID);
		Log.v(TAG,"looking for fortune with id: "+num);
		Fortune f = client.getFortuneByID(num);
		Log.v(TAG,"Got Fortune wth id: "+f.getFortuneID());
		f.markSeen();
		
		switch (action) {
		case INTENT_ACTION_CLICK:
			startActivity(new Intent(this, TabsFragment.class));
			break;
		case INTENT_ACTION_UPVOTE:
			f.upvote();
			break;
		case INTENT_ACTION_DOWNVOTE:
			f.downvote();
			break;
		default:
			Log.v(TAG,"Unknown Action");
			break;
		}
		
		finish();
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