package com.vorsk.androidfortune;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.analytics.tracking.android.EasyTracker;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class NotificationReceiverActivity extends SherlockActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("TAG", "Got notification activity");
		setContentView(R.layout.notification_result);

		Log.v("TAG", "Got new intent");

		TextView text = (TextView) findViewById(R.id.textView2);

		text.setText(getIntent().getExtras().getString("action"));
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