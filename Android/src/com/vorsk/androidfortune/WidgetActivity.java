package com.vorsk.androidfortune;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetActivity extends Activity {
	
	private static Fortune fortune;
	
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
	}

	/** Method where fortune must be passed in 
	 *  so it can be displayed on to the widget. 
	 * @param f fortune to be passed in
	 */
    public static void displayFortune(Context context, Fortune f){
    	Log.v("WidgetActivity", " Widget Activity is udating");
    	fortune = f;
    }
    
    public static void displayFortuneToText(Context ctx, RemoteViews remoteViews){
    	if (fortune != null) {
    		remoteViews.setTextViewText(R.id.fortune_view, fortune.getFortuneText(true));
    	}
    }
    
    /**method to set upVoted to true if the 
     * user has made a up vote.
     * @param upV true if user made an up Voted
     */
    public static void countUpVote(){
    	if (fortune != null) {
    		fortune.upvote();
    	}
    }
    
    /**Method to set DownVoted to true if the 
     * user has made a down vote.
     * @param downV true if user made an down Voted
     */
    public static void countDownVote(){
    	if (fortune != null) {
    		fortune.downvote();
    	}
    }
}
       

