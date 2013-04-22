package com.vorsk.androidfortune;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.vorsk.androidfortune.R;

import android.os.Bundle;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.view.View;


public class CreateNotificationActivity extends SherlockActivity {


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.create_notification, menu);
        return true;
    }
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	  }

	  public void createNotification(View view) {
		  
		  Log.v("TAG","Button pressed");
		  
	    // Prepare intent which is triggered if the
	    // notification is selected
		 int pendingFlag = PendingIntent.FLAG_ONE_SHOT;
		 int intentFlag = Intent.FLAG_ACTIVITY_CLEAR_TOP;

	    Intent intent = new Intent(this, NotificationReceiverActivity.class);
	    intent.setFlags(intentFlag);
	    intent.putExtra("action", "Click");
	    Intent intentUp = new Intent(this, NotificationReceiverActivity.class);
	    intent.setFlags(intentFlag);
	    intentUp.putExtra("action", "Upvote");
	    Intent intentDown = new Intent(this, NotificationReceiverActivity.class);
	    intent.setFlags(intentFlag);
	    intentDown.putExtra("action", "Downvote");
	    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, pendingFlag);
	    PendingIntent pIntentUp = PendingIntent.getActivity(this, 1, intentUp, pendingFlag);
	    PendingIntent pIntentDown = PendingIntent.getActivity(this, 2, intentDown, pendingFlag);
	    
	    
	    // Build notification
	    // Actions are just fake
	    Notification noti = new Notification.Builder(this)
	        .setContentTitle(getResources().getString(R.string.notification_title))
	        .setContentText(getResources().getString(R.string.fortune)).setSmallIcon(R.drawable.ic_launcher)
	        .setContentIntent(pIntent)
	        .addAction(R.drawable.up, "Upvote", pIntentUp)
	        .addAction(R.drawable.down, "Downvote", pIntentDown).build();
	    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    // Hide the notification after its selected
	    noti.flags |= Notification.FLAG_AUTO_CANCEL;

	    notificationManager.notify(0, noti);

	  }
	
	
}
