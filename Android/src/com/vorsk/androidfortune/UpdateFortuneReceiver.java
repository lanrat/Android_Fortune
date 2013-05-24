package com.vorsk.androidfortune;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class UpdateFortuneReceiver extends BroadcastReceiver {
	private static String TAG = "UpdateFortuneReceiver";
	public UpdateFortuneReceiver() {
	}

	//Tell Client to update current fortune
	@Override
	public void onReceive(Context context, Intent intnt) {
		Log.v(TAG, "onReceive Called");
		Fortune f = Client.getInstance().updateCurrentFortune();
		
		
		//send notification
		//createNotificationFromFortune
		
		
		//temp, just for testing
	 	/*Log.v("TAG", "Sending notification");
	 	// Prepare intent which is triggered if the
		// notification is selected
		int pendingFlag = PendingIntent.FLAG_ONE_SHOT;
		int intentFlag = Intent.FLAG_ACTIVITY_CLEAR_TOP;

		Intent intent = new Intent(context, NotificationReceiverActivity.class);
		intent.setFlags(intentFlag);
		intent.putExtra("action", "Click");
		Intent intentUp = new Intent(context, NotificationReceiverActivity.class);
		intent.setFlags(intentFlag);
		intentUp.putExtra("action", "Upvote");
		Intent intentDown = new Intent(context, NotificationReceiverActivity.class);
		intent.setFlags(intentFlag);
		intentDown.putExtra("action", "Downvote");
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
				pendingFlag);
		PendingIntent pIntentUp = PendingIntent.getActivity(context, 1, intentUp,
				pendingFlag);
		PendingIntent pIntentDown = PendingIntent.getActivity(context, 2,
				intentDown, pendingFlag);

		// Build notification
		// Actions are just fake
		Notification noti = new NotificationCompat.Builder(context)
				.setContentTitle(
						context.getResources().getString(R.string.notification_title))
				.setContentText(f.getFortuneText(true))
				.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent)
				.addAction(R.drawable.arrow_up, "Upvote", pIntentUp)
				.addAction(R.drawable.arrow_down, "Downvote", pIntentDown)
				.build();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		// Hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, noti);*/
	}
	
}
