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
    		remoteViews.setTextViewText(R.id.upvote_count, Integer.toString(fortune.getUpvotes()));
    		remoteViews.setTextViewText(R.id.downvote_count, Integer.toString(fortune.getDownvotes()));
    		if (fortune.getUpvoted()) {
    			remoteViews.setImageViewResource(R.id.upvote_button,R.drawable.arrow_up_dark);
    		}else {
    			remoteViews.setImageViewResource(R.id.upvote_button,R.drawable.arrow_up);
    		}
    		if (fortune.getDownvoted()) {
    			remoteViews.setImageViewResource(R.id.downvote_button,R.drawable.arrow_down_dark);
    		}else {
    			remoteViews.setImageViewResource(R.id.downvote_button,R.drawable.arrow_down);
    		}
    	}
    }
    
    /**method to set upVoted to true if the 
     * user has made a up vote.
     * @param upV true if user made an up Voted
	 * @return true if successful, false otherwise
     */
    public static boolean countUpVote(Context ctx){
    	if (fortune != null) {
    		boolean ret = fortune.upvote();
    		displayFortune(ctx, fortune);
    		return ret;
    	}
    	return false;
    }
    
    /**Method to set DownVoted to true if the 
     * user has made a down vote.
     * @param downV true if user made an down Voted
  	 * @return true if successful, false otherwise
     */
    public static boolean countDownVote(Context ctx){
    	if (fortune != null) {
    		boolean ret = fortune.downvote();
    		displayFortune(ctx, fortune);
    		return ret;
    	}
    	return false;
    }
}
       

