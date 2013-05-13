package com.vorsk.androidfortune;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

//TODO add CSRF Support
public class Client
{
	private static String TAG = "ServerTask";
	private static final String SERVER = "http://cse-190-fortune.herokuapp.com/server.php";
	private String userID;
	FortuneDbAdapter database;

	/**
	 * ctor for fortune client
	 * @param userID the id of the current user
	 */
	public Client(String userID)
	{
		this.userID = userID;
		this.database = FortuneDbAdapter.getInstance();
	}
	
	public void submitView(Fortune fortune)
	{
		//TODO
	}
	
	public void submitFlag(Fortune fortune)
	{
		//TODO
	}
	
	/**
	 * Submits a vote for a fortune
	 * THIS SHOULD ONLY BE CALLED FROM WITHIN FORTUNE!
	 * @param fortune the fortune
	 * @param flag true if upvote, false otherwise
	 */
	public void submitVote(Fortune fortune, boolean flag)
	{
		if (!fortune.hasVoted())
		{
			JSONObject obj = getRequestJSON();
			try {
				obj.put("vote", flag);
				String json = sendData("submitVote", obj);
			} catch (JSONException e) {
				Log.e(TAG,"JSONException");
			}
			if (flag){
				//upvote
				database.updateFortuneCol(fortune.getFortuneID(), 
						FortuneDbAdapter.KEY_UPVOTED, fortune.getUpvoted());
				database.updateFortuneCol(fortune.getFortuneID(), 
						FortuneDbAdapter.KEY_UPVOTES, fortune.getUpvotes());
			}else{
				//downvote
				database.updateFortuneCol(fortune.getFortuneID(), 
						FortuneDbAdapter.KEY_DOWNVOTED, fortune.getDownvoted());
				database.updateFortuneCol(fortune.getFortuneID(), 
						FortuneDbAdapter.KEY_DOWNVOTES, fortune.getDownvotes());
			}
		}
	}
	
	/**
	 * Submit a fortune to the server and add it to the local database
	 * @param text the message for the fortune
	 */
	public void submitFortune(String text)
	{
		JSONObject obj = getRequestJSON();
		try {
			obj.put("fortune_text", text);
			String json = sendData("getFortuneByID", obj);
			database.createFortuneFromJson(sendData("submitFortune", obj));
		} catch (JSONException e) {
			Log.e(TAG,"JSONException");
		}
	}
	
	/**
	 * get a list of all fortunes the user has created
	 * @return an arraylist of the fortunes
	 */
	public ArrayList<Fortune> getFortunesSubmitted()
	{
		return database.fetchAllByUser();
	}
	
	
	/**
	 * gets a new fortune from the database and adds it to the database
	 * @return the new fortune
	 */
	public Fortune getFortune()
	{
		JSONObject obj = getRequestJSON();
		String json = sendData("getFortune", obj);
		try {
			return database.fetchFortune(database.createFortuneFromJson(json));
		} catch (JSONException e) {
			Log.e(TAG,"JSONException");
			return null;
		}
	}
	
	/**
	 * return a fortune given a particular ID
	 * first the local DB is checked, then the remote database
	 * @param id the id of the fortune
	 * @return the fortune if found or null
	 */
	public Fortune getFortuneByID(long id)
	{
		Fortune ret = database.fetchFortune(id);
		if (ret == null)
		{
			JSONObject obj = getRequestJSON();
			try {
				obj.put("fortune_id",id );
				String json = sendData("getFortuneByID", obj);
				return database.fetchFortune(database.createFortuneFromJson(json));
			} catch (JSONException e) {
				Log.e(TAG,"JSONException");
			}
			
		}
		return ret;
	}
	
	/**
	 * getRequestJSON
	 * @return JSONObject to be sent to the server
	 */
	private JSONObject getRequestJSON()
	{
		JSONObject obj = new JSONObject();
		try {
			obj.put("user",this.userID );
		} catch (JSONException e) {
			Log.e(TAG,"JSONException");
		}
		return obj;
	}
	
	/**
	 * Sends the data to the server and returns the response
	 * @param action the string action to pass
	 * @param obj the JSON data to post
	 * @return string of html response or null if error
	 */
	private String sendData(String action,JSONObject obj)
	{
		HttpClient client = new DefaultHttpClient(); 
		HttpPost post = new HttpPost(SERVER+"?action="+action);
		post.setHeader("Content-type", "application/json");
        HttpResponse response = null;
        String responseString = null;
		try {
	        post.setEntity(new StringEntity(obj.toString(), "UTF-8"));
			response = client.execute(post,new BasicHttpContext());
		} catch (ClientProtocolException e) {
			Log.e(TAG,"ClientProtocolException");
		} catch (IOException e) {
			Log.e(TAG,"IOException");
		}
		HttpEntity responseEntity = response.getEntity();
		if(responseEntity==null) {
			return null;
		}
	    try {
			responseString = EntityUtils.toString(responseEntity);
		} catch (ParseException e) {
			Log.e(TAG,"ParseException");
		} catch (IOException e) {
			Log.e(TAG,"IOException");
		}
	    return responseString;
	}
}