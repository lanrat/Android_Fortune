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
	
	/**field fortune is created to allow the
	 * passed in Fortune object
	 * at displayFortune method to 
	 * be accessible throughout the whole class
	 */
	private static Fortune fortune;
	private static final String TAG = "WidgetActivity";
	
	/**
	 * Default constructor 
	 */
	public WidgetActivity(){
	}
	
	/** The OnCreate method is called when the activity is 
	 * started.
	 * @Override
	 */
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		displayFortune(getApplicationContext(),
				Client.getInstance(getApplicationContext()).getCurrentFortune());
	}

	/** Method where fortune must be passed in 
	 *  so it can be displayed on to the widget. 
	 * @param context Context of current state of the application
	 * @param f fortune to be passed in
	 */
    public static void displayFortune(Context context, Fortune f){
    	Log.v("WidgetActivity", " Widget Activity is udating");
    	fortune = f;
    	if (f == null) {
    		Log.e(TAG,"displaying null fortune");
    	}
    	// creating a RemoteView to be passed in when calling displayFortuneToText method
		final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		// calling the method
    	WidgetActivity.displayFortuneToText(context, remoteViews);
    	// updating the widget
		FortuneWidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
    }
    /** Displays Fortune on the widget TextView and uses remoteViews to
     * update the Widget. This method is called in the displayFortune method
     * which is called in the onUpdate method in the FortuneWidgetProvider
     * class
     * @param ctx Context of current state of the application
     * @param remoteViews RemoteView to update the widget with Fortune
     */
    public static void displayFortuneToText(Context ctx, RemoteViews remoteViews){
    	// check if the fortune is null, if it is don't do anything
    	if (fortune != null) {
    		
    		// setting the widget TextView to the fortune to be displayed which is received by 
    		// calling the getFortuneText method in the fortune class
    		remoteViews.setTextViewText(R.id.fortune_text, fortune.getFortuneText(true));
    		
    		//setting the widget upvote_count TextView to the number of up votes by calling
    		// the getUpVotes method in the fortune class
    		remoteViews.setTextViewText(R.id.upvote_count, Integer.toString(fortune.getUpvotes()));
    		
    		//setting the widget downvote_count TextView to the number of down votes by calling
    		// the getdownVotes method in the fortune class
    		remoteViews.setTextViewText(R.id.downvote_count, Integer.toString(fortune.getDownvotes()));
    		
    		/* Bottom code mirrors the effect of pressing a button
    		 * we first check if the user up voted. If they didn't then
    		 * Exchanging the up vote button image with the arrow_up_dark image. 
    		 * If they user has already up voted then change the image back to upvote_button
    		 */
    		if (fortune.getUpvoted()) {
    			remoteViews.setImageViewResource(R.id.upvote_button,R.drawable.arrow_up_dark);
    		}else {
    			remoteViews.setImageViewResource(R.id.upvote_button,R.drawable.arrow_up);
    		}
    		
    		// This code is doing the same thing as the two lines of code above but with the down button
    		if (fortune.getDownvoted()) {
    			remoteViews.setImageViewResource(R.id.downvote_button,R.drawable.arrow_down_dark);
    		}else {
    			remoteViews.setImageViewResource(R.id.downvote_button,R.drawable.arrow_down);
    		}// end else
    	}// end outer if
    }
    
    /**method to set upVote to true if the 
     * user has made a up vote. Checks if the fortune is null
     * if it is don't do anything.
     * @param ctx Context of current state of the application
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
    
    /**Method to set DownVote to true if the 
     * user has made a down vote.Checks if the fortune is null
     * if it is don't do anything.
     * @param ctx Context of current state of the application
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
       

