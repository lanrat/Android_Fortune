package com.vorsk.androidfortune;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

//TODO add CSRF Support
/**
 * Client class in charge of interfacing with the application and all data management
 * with the local database and remote server
 */
public class Client
{
	private static String TAG = "ServerTask";
	private static final String SERVER = "http://cse-190-fortune.herokuapp.com/server.php";
	private static String userID;
	private static FortuneDbAdapter database;
	private static Client instance; //used to access this class as a static singleton
	private static boolean enableServerCommunication = true;
	private final Context mContext;

	/**
	 * constructor for fortune client
	 * @param ctx the context of the application
	 */
	private Client(Context ctx)
	{
		this.mContext = ctx;
		Client.userID = getUniqueDeviceID(ctx);
		Client.instance = this;
		//create instance and open
		Client.database = FortuneDbAdapter.getInstance(ctx).open();
	}

	/**
	 * returns the current client instance
	 * MAKE SURE IT IS INITALIZED BEFORE USE!!!
	 * @return the client instance to use
	 */
	public static Client getInstance(){
	    if (instance == null) {
	    	Log.v(TAG,"instance has not been initialized.");
	    }
	    return instance;
	}
	
	/**
	 * Instantiates the class if null and returns a new instance
	 * @param ctx the context to use for the instantiation
	 * @return an instance of the class
	 */
	public static Client getInstance(Context ctx) {
		// Use the application context, which will ensure that you 
	    // don't accidentally leak an Activity's context.
	    // See this article for more information: http://bit.ly/6LRzfx
	    if (instance == null) {
	    	instance = new Client(ctx);
	    }
	    return instance;
	}
	
	/**
	 * logs a fortune as seen by a user
	 * @param fortune the fortune to mark
	 */
	public void submitView(Fortune fortune)
	{
		if (fortune.getSeen() != null)
		{
			database.updateFortuneCol(fortune.getFortuneID(), 
					FortuneDbAdapter.KEY_VIEWDATE, fortune.getSeen());
		}
	}
	
	/**
	 * Sets the fortune as flagged in the database
	 * Should only be called by Fortune object
	 * @param fortune the fortune to flag
	 */
	public void submitFlag(final Fortune fortune)
	{
		if (!fortune.getFlagged())
		{
			new Thread(new Runnable() {
			    public void run() {
					JSONObject obj = getRequestJSON();
					try {
						obj.put("fortune_id", fortune.getFortuneID());
						sendData("submitFlag", obj);
					} catch (JSONException e) {
						Log.e(TAG,"JSONException");
						return;
					}
					database.updateFortuneCol(fortune.getFortuneID(),
							FortuneDbAdapter.KEY_FLAG, fortune.getFlagged());
			    }
			  }).start();
		}
	}
	
	/**
	 * Submits a vote for a fortune
	 * THIS SHOULD ONLY BE CALLED FROM WITHIN FORTUNE!
	 * @param fortune the fortune
	 * @param flag true if upvote, otherwise downvote
	 */
	public void submitVote(final Fortune fortune, final boolean flag)
	{
		if (!fortune.hasVoted())
		{
			new Thread(new Runnable() {
			    public void run() {
					JSONObject obj = getRequestJSON();
					try {
						obj.put("vote", flag);
						sendData("submitVote", obj);
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
			}).start();
		}
	}
	
	/**
	 * Submit a fortune to the server and add it to the local database
	 * @param text the message for the fortune
	 */
	public void submitFortune(final String text)
	{
		new Thread(new Runnable() {
		    public void run() {
				JSONObject obj = getRequestJSON();
				try {
					obj.put("fortune_text", text);
					database.createFortuneFromJson(sendData("submitFortune", obj));
				} catch (JSONException e) {
					Log.e(TAG,"JSONException");
				}
		    }
		}).start();
	}
	
	/**
	 * returns all fortunes stored locally
	 * @return an arraylist f the fortunes
	 */
	public ArrayList<Fortune> getSeenFortunes()
	{
		//TODO filter only seen fortunes
		return database.fetchAllFortunes();
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
	 * THIS METHOS MUST BE CALLED FROM AN ASYNCTASK OR OTHER THREAD OR IT WILL FAIL!
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
	 * THIS METHOS MUST BE CALLED FROM AN ASYNCTASK OR OTHER THREAD OR IT WILL FAIL!
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
	 * Utility function to generate a unique string per device
	 * @param context used to contact the Android TelephonyManager
	 * @return the unique string
	 */
	public static String getUniqueDeviceID(Context context)
	{
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

	    final String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    String id = deviceUuid.toString();
	    Log.v(TAG,"Got Device ID: "+id);
	    return id;
	}
	
	/**
	 * getRequestJSON
	 * @return JSONObject to be sent to the server
	 */
	private JSONObject getRequestJSON()
	{
		JSONObject obj = new JSONObject();
		try {
			obj.put("user",Client.userID );
		} catch (JSONException e) {
			Log.e(TAG,"JSONException");
		}
		return obj;
	}
	
	/**
	 * Sends the data to the server and returns the response
	 * @param action the string action to pass
	 * @param obj the JSON data to post
	 * @return string of response or null if error
	 */
	private String sendData(String action,JSONObject obj)
	{
		if (!enableServerCommunication)
		{
			return null;
		}
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