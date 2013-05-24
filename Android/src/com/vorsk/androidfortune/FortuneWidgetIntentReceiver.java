package com.vorsk.androidfortune;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.TextView;

public class FortuneWidgetIntentReceiver extends BroadcastReceiver{
	

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals("up_button.intent.action.UP_VOTE")){
			updateWidgetUpButtonListener(context);
		}
		else if(intent.getAction().equals("down_button.intent.action.DOWN_VOTE")){
			updateWidgetDownButtonListener(context);
		}
	}
	
	private void updateWidgetUpButtonListener(Context context){
		
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		remoteViews.setTextViewText(R.id.fortune_view, "Up Vote Clicked :-D ");
		
		// Calling countUpVote to count users up vote
		//WidgetActivity.countUpVote(true);
		
		//Refreshing Button Listener
		remoteViews.setOnClickPendingIntent(R.id.up_button, FortuneWidgetProvider.buildUpButtonPendingIntent(context));
		FortuneWidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
	}
	
	private void updateWidgetDownButtonListener(Context context){
		
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);	
		remoteViews.setTextViewText(R.id.fortune_view, "Down Vote :-( ");
		
		// calling countDownVote to count users down vote
		//WidgetActivity.countDownVote(true);
	
		
		//Refreshing Button Listener
		remoteViews.setOnClickPendingIntent(R.id.down_button, FortuneWidgetProvider.buildDownButtonPendingIntent(context));
		FortuneWidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
	}

}
