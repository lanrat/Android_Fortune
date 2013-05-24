package com.vorsk.androidfortune;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class Fortune implements Comparable<Fortune> {
	
	private int fortuneID;
	private String fortuneText;
	private int upvotes = 0;
	private boolean upvoted = false;
	private boolean downvoted = false;
	private int downvotes = 0;
	private boolean flagged = false;
	private boolean owner = false; //true if the user created the fortune
	private Date seen;
	private Date submitted;
	private int views = 0; //TODO these are not stored in the DB, may not be an issue
	
	public Fortune(int id,
					String text,
					int upvotes,
					int downvotes,
					boolean upvoted,
					boolean downvoted,
					boolean flagged,
					boolean owner,
					Date seen,
					Date submitted
			){
		this.fortuneID = id;
		this.fortuneText = text;
		this.upvotes = upvotes;
		this.downvotes = downvotes;
		this.downvoted = downvoted;
		this.upvoted = upvoted;
		this.flagged = flagged;
		this.seen = seen;
		this.submitted = submitted;
	}
	
	
	/**
	 * Factory method to create a fortune from a json string
	 * @param json the json to parse
	 * @return a new fortune object or NULL if unable to parse
	 */
	public Fortune createFromJSON(String json){
		try {
			return new Fortune(json);
		} catch (JSONException e) {
			Log.e("Fortune","Could not parse JSON to Fortune");
			return null;
		}
	}
	

	/**
	 * Private constructor to create a fortune from json
	 * @param json the json to parse
	 * @throws JSONException if we are missing required fields to parse
	 */
	private Fortune(String json) throws JSONException
	{
		JSONObject data = new JSONObject(json);
		this.fortuneID = data.getInt("fortuneID");
		this.fortuneText = data.getString("text");
		this.upvotes = data.getInt("upvote");
		this.downvotes =  data.getInt("downvote");
		this.upvoted = false;	//TODO check for the real value
		this.downvoted = false; //TODO check for the real value
		this.submitted = new Date(data.getInt("uploadDate")*1000);
		this.seen = null; //TODO check for the real value
		this.views = data.getInt("views");
		//this.flagged = data.getBoolean("flagged"); //TODO use this
	}
	
	///TODO remove this
	public Fortune(Cursor c)
	{
		try {
			fortuneID = c.getInt(c.getColumnIndexOrThrow(FortuneDbAdapter.KEY_ID));
			fortuneText = c.getString(c.getColumnIndexOrThrow(FortuneDbAdapter.KEY_TEXT));
			upvotes = c.getInt(c.getColumnIndexOrThrow(FortuneDbAdapter.KEY_UPVOTES));
			upvoted = c.getInt(c.getColumnIndexOrThrow(FortuneDbAdapter.KEY_UPVOTED))!=0;
			downvotes = c.getInt(c.getColumnIndexOrThrow(FortuneDbAdapter.KEY_DOWNVOTES));
			downvoted = c.getInt(c.getColumnIndexOrThrow(FortuneDbAdapter.KEY_DOWNVOTED))!=0;
			flagged = c.getInt(c.getColumnIndexOrThrow(FortuneDbAdapter.KEY_FLAG))!=0 ;
			owner = c.getInt(c.getColumnIndexOrThrow(FortuneDbAdapter.KEY_OWNER))!=0 ;
			
			long tempTime = c.getLong(c.getColumnIndexOrThrow(FortuneDbAdapter.KEY_VIEWDATE));
			if ( tempTime > 0 ) {
				seen = new Date(tempTime*1000);
			}
			submitted = new Date( c.getLong(c.getColumnIndexOrThrow(FortuneDbAdapter.KEY_SUBMITDATE))*1000);
		} catch ( IllegalArgumentException e ) {
			Log.v("InitFortune", e.getMessage());
		} catch ( Exception e ) {
			//uh oh
		}
		
	}
	
	/**
	 * User submitted fortune
	 * @param fortune string of text
	 * @param date date submitted/seen
	 */
	public Fortune(String fortune, Date date)
	{
		fortuneText = fortune;
		seen = submitted = date; // current time when created
		owner = true;
	}
	
	
	
	
	/**
	 * returns true if the user has voted on the fortune
	 * @return
	 */
	public boolean hasVoted()
	{
		return this.upvoted || this.downvoted;
	}
	
	/**
	 * Flags the current fortune if it has not been flaged already
	 */
	public void flag()
	{
		if (!this.flagged)
		{
			this.flagged = true;
			Client.getInstance().submitFlag(this);
		}
	}
	
	/**
	 * upvotes the current function if the user has not already voted
	 */
	public void upvote()
	{
		if (!this.hasVoted())
		{
			this.upvoted = true;
			this.upvotes++;
			Client.getInstance().submitVote(this,true);
		}
	}
	
	/**
	 * downvotes the current function if it has not already been voted on
	 */
	public void downvote()
	{
		if (!this.hasVoted())
		{
			this.downvoted = true;
			this.downvotes++;
			Client.getInstance().submitVote(this,false);
		}
	}
	
	/**
	 * marked the current fortune as seen
	 */
	public void markSeen()
	{
		//update the date to the current time
		this.views++;
		this.seen = new Date();
		Client.getInstance().submitView(this);
	}
	

	/**
	 * returns the fortune text
	 * @param seen if true updated the last seen variable in the DB and server
	 * @return the string of the fortune
	 */
	public String getFortuneText(boolean seen)
	{
		if (seen)
		{
			this.markSeen();
		}
		return this.fortuneText;

		//return "Would you like to see Fast Six with me today";
	}

	@Override
	public int compareTo(Fortune another) {
		if (this.seen == null)
		{
			return -1;
		}
		if (another.seen == null)
		{
			return 1;
		}
		return this.seen.compareTo(another.seen);
	}
	
	//Getters
	public int getFortuneID() { return fortuneID; }
	public int getUpvotes() { return upvotes; }
	public boolean getUpvoted() { return upvoted; }
	public int getDownvotes() { return downvotes; }
	public boolean getDownvoted() { return downvoted; }
	public boolean getFlagged() { return flagged; }
	public boolean getOwner() { return owner; }
	public Date getSeen() { return seen; }
	public Date getSubmitted() { return submitted; }
	public int getViews() {return views; }
	
}
