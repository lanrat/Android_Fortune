package com.vorsk.androidfortune.widget;

import com.vorsk.androidfortune.R;
import com.vorsk.androidfortune.data.Client;
import com.vorsk.androidfortune.data.Fortune;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class FortuneWidgetIntentReceiver extends BroadcastReceiver{
	
	private static final String TAG = "WidgetIntentReciever";
	
	/** Receives the intent and checks if it is up vote or 
	 * down vote then calls the appropriate listener. Also
	 * implements a thread to re-display the fortune. 
	 * @param context state of the widget
	 * 	      intent received intent
	 */
	@Override
	public void onReceive(final Context context, Intent intent) {
		Log.v(TAG,"onRecieve");
		final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		final Fortune current_fortune = Client.getInstance(context).getCurrentFortune();

		String vote_message = null;
		
		if (current_fortune != null)
		{
			if(intent.getAction().equals("up_button.intent.action.UP_VOTE")){
				if (WidgetActivity.countUpVote()) {
					vote_message = context.getResources().getString(R.string.up_vote);
				}else {
					vote_message = context.getResources().getString(R.string.already_voted);
				}
				remoteViews.setOnClickPendingIntent(R.id.up_button, FortuneWidgetProvider.buildUpButtonPendingIntent(context));
			}
			else if(intent.getAction().equals("down_button.intent.action.DOWN_VOTE")){
				if (WidgetActivity.countDownVote()) {
					vote_message = context.getResources().getString(R.string.down_vote);
				}else {
					vote_message = context.getResources().getString(R.string.already_voted);
				}
				remoteViews.setOnClickPendingIntent(R.id.down_button, FortuneWidgetProvider.buildDownButtonPendingIntent(context));
			}
			
			if (vote_message != null)
			{
				remoteViews.setTextViewText(R.id.fortune_view, vote_message);
				// creating a timer thread to set text view back to the fortune String
				Thread timer = new Thread(){
					public void run(){
						try{
							sleep(600);
						} catch(InterruptedException e){
							Log.e(TAG,"thread exception");
						} finally{
							remoteViews.setTextViewText(R.id.fortune_view, current_fortune.getFortuneText(true));
							FortuneWidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
						}
					}
				};
				timer.start();
			}else{
				Log.v(TAG, "vote message is null!");
			}
		}
		
		FortuneWidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
		
	}// end method

}// end class