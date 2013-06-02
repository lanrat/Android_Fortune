package com.vorsk.androidfortune;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.vorsk.androidfortune.data.Fortune;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeActivity extends SherlockFragmentActivity {

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

	/**
	 * creates and displays a notification from a fortune
	 * @param f the fortune to display
	 */
	public static void createNotificationFromFortune(Context ctx, Fortune f){
		// Prepare intent which is triggered if the
		// notification is selected
		if (f == null)
		{
			Log.e("fortune Activity","Atempting to display NULL Fortune");
			return;
		}
		int pendingFlag = PendingIntent.FLAG_ONE_SHOT;
		int intentFlag = Intent.FLAG_ACTIVITY_CLEAR_TOP;

		Intent intent = new Intent(ctx, NotificationReceiverActivity.class);
		intent.setFlags(intentFlag);
		intent.putExtra(NotificationReceiverActivity.INTENT_FORTUNE_ID, f.getFortuneID());
		intent.putExtra(NotificationReceiverActivity.INTENT_ACTION, NotificationReceiverActivity.INTENT_ACTION_CLICK);
		
		Intent intentUp = new Intent(ctx, NotificationReceiverActivity.class);
		intentUp.setFlags(intentFlag);
		intentUp.putExtra(NotificationReceiverActivity.INTENT_FORTUNE_ID, f.getFortuneID());
		intentUp.putExtra(NotificationReceiverActivity.INTENT_ACTION, NotificationReceiverActivity.INTENT_ACTION_UPVOTE);
		
		Intent intentDown = new Intent(ctx, NotificationReceiverActivity.class);
		intentDown.setFlags(intentFlag);
		intentDown.putExtra(NotificationReceiverActivity.INTENT_FORTUNE_ID, f.getFortuneID());
		intentDown.putExtra(NotificationReceiverActivity.INTENT_ACTION, NotificationReceiverActivity.INTENT_ACTION_DOWNVOTE);
		
		PendingIntent pIntent = PendingIntent.getActivity(ctx, 0, intent,
				pendingFlag);
		PendingIntent pIntentUp = PendingIntent.getActivity(ctx, 1, intentUp,
				pendingFlag);
		PendingIntent pIntentDown = PendingIntent.getActivity(ctx, 2,
				intentDown, pendingFlag);
		
		//notification
		Notification noti = new NotificationCompat.Builder(ctx)
		.setContentTitle(ctx.getResources().getString(R.string.notification_title))
		.setContentText(f.getFortuneText(false))
		.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent)
		.addAction(R.drawable.arrow_up, "Upvote", pIntentUp)
		.addAction(R.drawable.arrow_down, "Downvote", pIntentDown)
		.build();
		NotificationManager notificationManager = (NotificationManager) ctx
				.getSystemService(NOTIFICATION_SERVICE);
		// Hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, noti);
	}
}
