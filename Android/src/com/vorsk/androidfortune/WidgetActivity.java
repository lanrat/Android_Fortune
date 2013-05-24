package com.vorsk.androidfortune;

//import com.javapapers.android.form.R;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WidgetActivity extends Activity {
	
	private Fortune fortune;
	
	/**
	 * Default constructor 
	 */
	public WidgetActivity(){
		Date newDate = new Date();
		Fortune f = new Fortune("Good luck today", newDate);
		fortune = f;
	}
    
	/** Constructor where fortune must be passed in 
	 *  so it can be displayed on to the widget. 
	 * @param f fortune to be passed in
	 */
    public WidgetActivity(Fortune ft){
    	
    	//fortune = ft;
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
    
    /**Getter method to get the fortune text so it can 
     * be displayed on the widget
     * @param none
     * @return String returns the fortune text. 
     */
    public String fText(boolean seen){
    	String s = fortune.getFortuneText(seen);
    	return s;
    }
}
       

