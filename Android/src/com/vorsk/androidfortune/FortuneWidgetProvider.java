package com.vorsk.androidfortune;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import java.util.Date;

public class FortuneWidgetProvider extends AppWidgetProvider {
	
	/**** Testing out Methods Creating a new Widget Activity*****/
	WidgetActivity widgetActivity = new WidgetActivity();
	
	//private String fortuneText = WidgetActivity.getFortuneText(true);
	
	//private static final String tag = "FortuneWidgetProvider";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
	
		// New RemoteView
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		//remoteViews.setTextViewText(R.id.fortune_view, wv.fText(true));
		
		widgetActivity.displayFortune(context, Client.getInstance(context).getCurrentFortune());
		remoteViews.setOnClickPendingIntent(R.id.up_button, buildUpButtonPendingIntent(context));
		remoteViews.setOnClickPendingIntent(R.id.down_button, buildDownButtonPendingIntent(context));
		
		pushWidgetUpdate(context, remoteViews);
		
	}
	
	public static PendingIntent buildUpButtonPendingIntent(Context context){
		Intent intent = new Intent();
		intent.setAction("up_button.intent.action.UP_VOTE");
	
		return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	public static PendingIntent buildDownButtonPendingIntent(Context context){
		Intent intent = new Intent();
		intent.setAction("down_button.intent.action.DOWN_VOTE");
	
		return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	public static void pushWidgetUpdate(Context context, RemoteViews remoteViews){
		ComponentName myWidget = new ComponentName(context, FortuneWidgetProvider.class);
		 AppWidgetManager manager = AppWidgetManager.getInstance(context);
		manager.updateAppWidget(myWidget, remoteViews);
		
	}
		
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Handle deletion of the widget.
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		// TODO Widget has been disabled.
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		// TODO Widget has been enabled.
		super.onEnabled(context);
	}
}
