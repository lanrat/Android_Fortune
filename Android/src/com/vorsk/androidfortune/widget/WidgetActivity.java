package com.vorsk.androidfortune.widget;

import com.vorsk.androidfortune.R;
import com.vorsk.androidfortune.data.Client;
import com.vorsk.androidfortune.data.Fortune;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetActivity extends Activity {
	
	private static Fortune fortune;
	private static final String TAG = "WidgetActivity";
	
	/**
	 * Default constructor 
	 */
	public WidgetActivity(){
	}
	
	/** 
	 * 
	 */
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		displayFortune(getApplicationContext(),
				Client.getInstance(getApplicationContext()).getCurrentFortune());
	}

	/** Method where fortune must be passed in 
	 *  so it can be displayed on to the widget. 
	 * @param f fortune to be passed in
	 */
    public static void displayFortune(Context context, Fortune f){
    	Log.v("WidgetActivity", " Widget Activity is udating");
    	fortune = f;
    	if (f == null) {
    		Log.e(TAG,"displaying null fortune");
    	}
		final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
    	WidgetActivity.displayFortuneToText(context, remoteViews);
		FortuneWidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
    }
    
    public static void displayFortuneToText(Context ctx, RemoteViews remoteViews){
    	if (fortune != null) {
    		remoteViews.setTextViewText(R.id.fortune_text, fortune.getFortuneText(true));
    	}
    }
    
    /**method to set upVoted to true if the 
     * user has made a up vote.
     * @param upV true if user made an up Voted
	 * @return true if successful, false otherwise
     */
    public static boolean countUpVote(){
    	if (fortune != null) {
    		return fortune.upvote();
    	}
    	return false;
    }
    
    /**Method to set DownVoted to true if the 
     * user has made a down vote.
     * @param downV true if user made an down Voted
  	 * @return true if successful, false otherwise
     */
    public static boolean countDownVote(){
    	if (fortune != null) {
    		return fortune.downvote();
    	}
    	return false;
    }
}
       

