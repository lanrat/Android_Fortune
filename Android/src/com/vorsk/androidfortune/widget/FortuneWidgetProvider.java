package com.vorsk.androidfortune.widget;

import com.vorsk.androidfortune.R;
import com.vorsk.androidfortune.TabsFragment;
import com.vorsk.androidfortune.data.Client;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class FortuneWidgetProvider extends AppWidgetProvider {
	
	private static final String TAG = "FortuneWidgetProvider";
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.v(TAG,"onUpdate");
	
		// getting widget id
		final int widgetIds = appWidgetIds.length;
		
		// updating the widget
		for(int i =0; i<widgetIds; i++){
			
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			WidgetActivity.displayFortune(context, Client.getInstance(context).getCurrentFortune());
			
			// displaying the fortune on to the textView
			WidgetActivity.displayFortuneToText(context, remoteViews);
			remoteViews.setOnClickPendingIntent(R.id.upvote_button, buildUpButtonPendingIntent(context));
			remoteViews.setOnClickPendingIntent(R.id.downvote_button, buildDownButtonPendingIntent(context));
			remoteViews.setOnClickPendingIntent(R.id.fortune_body, buildClickPendingIntent(context));
			pushWidgetUpdate(context, remoteViews);
		}
		
	}
	
	/**Method called when the user clicks the fortune
	 * It creates a new intent and sets its action, the action
	 * is received in FortuneWidgetIntentReceiver class.
	 * @param context
	 */
	public static PendingIntent buildClickPendingIntent(Context context){
		Intent intent = new Intent(context, TabsFragment.class);
		return PendingIntent.getActivity(context, 0, intent, 0);
	}
	
	/**Method called when the user clicks up button. 
	 * It creates a new intent and sets its action, the action
	 * is received in FortuneWidgetIntentReceiver class.
	 * @param context
	 */
	public static PendingIntent buildUpButtonPendingIntent(Context context){
		Intent intent = new Intent();
		intent.setAction("up_button.intent.action.UP_VOTE");
	
		return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	/**Method called when the user clicks down button. 
	 * It creates a new intent and sets its action, the action
	 * is received in FortuneWidgetIntentReceiver class.
	 * @param context
	 */
	public static PendingIntent buildDownButtonPendingIntent(Context context){
		Intent intent = new Intent();
		intent.setAction("down_button.intent.action.DOWN_VOTE");
	
		return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	/** Last method called onUpdate. It pushes the update
	 * after the user clicks on up or down button. 
	 * @param context
	 * @param remoteViews
	 */
	public static void pushWidgetUpdate(Context context, RemoteViews remoteViews){
		ComponentName myWidget = new ComponentName(context, FortuneWidgetProvider.class);
		 AppWidgetManager manager = AppWidgetManager.getInstance(context);
		manager.updateAppWidget(myWidget, remoteViews);
	}
		
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}
}
