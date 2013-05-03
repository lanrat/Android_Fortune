package com.vorsk.androidfortune;

import java.util.Date;

public class Fortune implements Comparable<Fortune> {
	
	private String fortuneText;
	private int upvotes = 0;
	private boolean upvoted = false;
	private boolean downvoted = false;
	private int downvotes = 0;
	private boolean flagged = false;
	private boolean owner; //true if the user created the fortune
	private Date seen;
	private Date submitted;
	

	public Fortune(String str, boolean test)
	{
		//TODO construct from json from server
	}
	
	public Fortune(Object sql)
	{
		//TODO ctor for fortune from sql db
	}
	
	public Fortune(String fortune)
	{
		//TODO ctor to create user submitted fortune
	}
	
	public void flag()
	{
		if (!this.flagged)
		{
			this.flagged = true;
			//TODO update server
		}
	}
	
	public void upvote()
	{
		if (!this.upvoted)
		{
			this.upvoted = true;
			this.upvotes++;
			if (this.downvoted)
			{
				this.downvoted = false;
				this.downvotes--;
			}
			//TODO inform the server of the change
		}
	}
	
	public void downvote()
	{
		if (!this.downvoted)
		{
			this.downvoted = true;
			this.downvotes++;
			if (this.upvoted)
			{
				this.upvoted = false;
				this.upvotes--;
			}
			//TODO inform the server of the change
		}
	}
	
	
	public String getFortune()
	{
		if (this.seen == null)
		{
			//update the date to the current time
			this.seen = new Date();
			//TODO update the date in the local data storage
		}
		return this.fortuneText;
	}
	
	public boolean owner()
	{
		return this.owner;
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
	
}
