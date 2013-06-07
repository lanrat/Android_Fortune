package com.vorsk.androidfortune;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.vorsk.androidfortune.data.Client;
import com.vorsk.androidfortune.data.Fortune;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationActivity extends SherlockActivity {
	public static final String INTENT_ACTION = "action";
	public static final String INTENT_FORTUNE_ID = "fortuneID";
	public static final int INTENT_ACTION_CLICK = 1;
	public static final int INTENT_ACTION_UPVOTE = 2;
	public static final int INTENT_ACTION_DOWNVOTE = 3;
	public static final int ID_ACTION_CLICK = 0;
	public static final int ID_ACTION_UPVOTE = 1;
	public static final int ID_ACTION_DOWNVOTE = 2;
	public static final int ID_NOTIFICATION = 12; // rand

	private static final String TAG = "NotificationActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("TAG", "Got notification activity");

		int action = getIntent().getExtras().getInt(INTENT_ACTION);

		NotificationManager notificationManager = (NotificationManager) getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(ID_NOTIFICATION);

		// get the fortune object
		Client client = Client.getInstance(getApplicationContext());
		int num = getIntent().getExtras().getInt(INTENT_FORTUNE_ID);
		Log.v(TAG, "looking for fortune with id: " + num);
		Fortune f = client.getFortuneByID(num);
		Log.v(TAG, "Got Fortune wth id: " + f.getFortuneID());
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
			Log.v(TAG, "Unknown Action");
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

	/**
	 * creates and displays a notification from a fortune
	 * 
	 * @param f
	 *            the fortune to display
	 * @param ctx
	 *            the context to display the notification on
	 */
	public static void displayNotificationFromFortune(Context ctx, Fortune f) {
		if (f == null) {
			Log.e(TAG, "Trying ti create notification for null fortune");
			return;
		}

		int pendingFlag = PendingIntent.FLAG_ONE_SHOT;
		int intentFlag = Intent.FLAG_ACTIVITY_NEW_TASK;

		// click action
		Intent intent = new Intent(ctx, NotificationActivity.class);
		intent.setFlags(intentFlag);
		intent.putExtra(INTENT_FORTUNE_ID, f.getFortuneID());
		intent.putExtra(INTENT_ACTION, INTENT_ACTION_CLICK);

		// upvote action
		Intent intentUp = new Intent(ctx, NotificationActivity.class);
		intentUp.setFlags(intentFlag);
		intentUp.putExtra(INTENT_FORTUNE_ID, f.getFortuneID());
		intentUp.putExtra(INTENT_ACTION, INTENT_ACTION_UPVOTE);

		// downvote action
		Intent intentDown = new Intent(ctx, NotificationActivity.class);
		intentDown.setFlags(intentFlag);
		intentDown.putExtra(INTENT_FORTUNE_ID, f.getFortuneID());
		intentDown.putExtra(INTENT_ACTION, INTENT_ACTION_DOWNVOTE);

		PendingIntent pIntent = PendingIntent.getActivity(ctx, ID_ACTION_CLICK,
				intent, pendingFlag);
		PendingIntent pIntentUp = PendingIntent.getActivity(ctx,
				ID_ACTION_UPVOTE, intentUp, pendingFlag);
		PendingIntent pIntentDown = PendingIntent.getActivity(ctx,
				ID_ACTION_DOWNVOTE, intentDown, pendingFlag);

		// notification
		Notification noti = new NotificationCompat.Builder(ctx)
				.setContentTitle(
						ctx.getResources().getString(
								R.string.notification_title))
				.setContentText(f.getFortuneText(false))
				.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent)
				.addAction(R.drawable.arrow_up, "Upvote", pIntentUp)
				.addAction(R.drawable.arrow_down, "Downvote", pIntentDown)
				.build();
		NotificationManager notificationManager = (NotificationManager) ctx
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// Hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(ID_NOTIFICATION, noti);
	}

}