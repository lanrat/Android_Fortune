package com.vorsk.androidfortune;

//import com.javapapers.android.form.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

public class WidgetActivity extends Activity {
	

	private static Fortune fortune;
	public static String fortuneText;
	
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
    	
    	if(f == null ){
    		
    		Log.v("WidgetActivity", " Widget Activity is called");
    		fortuneText = "updating...";
    	}
    	else{
    		fortuneText  = f.getFortuneText(true);
    	}
    }
    
    public static void displayFortuneToText(Context ctx, RemoteViews remoteViews){
    	
		remoteViews.setTextViewText(R.id.fortune_view, fortuneText);
    }
    
    /**method to set upVoted to true if the 
     * user has made a up vote.
     * @param upV true if user made an up Voted
     */
    public void countUpVote(){
    	fortune.upvote();
    }
    
    /**Method to set DownVoted to true if the 
     * user has made a down vote.
     * @param downV true if user made an down Voted
     */
    public void countDownVote(){
    	fortune.downvote();
    }
}
       

