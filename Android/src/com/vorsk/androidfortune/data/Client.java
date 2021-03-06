package com.vorsk.androidfortune.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vorsk.androidfortune.SettingsActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

//TODO add CSRF Support
/**
 * Client class in charge of interfacing with the application and all data management
 * with the local database and remote server
 */
public class Client
{
	private static final String TAG = "Client";
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
	    	Log.v(TAG,"New Client instanct is being created");
	    	instance = new Client(ctx);
	    }
	    Log.v(TAG,"Old client isntance being returned");
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
			Log.v(TAG, "Flagging fortune");
			new Thread(new Runnable() {
			    public void run() {
					JSONObject obj = getRequestJSON();
					try {
						obj.put("fortuneid", fortune.getFortuneID());
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
		Log.v(TAG,"SubmitVote: "+flag);

		new Thread(new Runnable() {
		    public void run() {
				JSONObject obj = getRequestJSON();
				try {
					obj.put("fortuneid", fortune.getFortuneID());
					obj.put("vote", flag);
					sendData("submitVote", obj);
					Log.v(TAG,"SubmitVote-send");
				} catch (JSONException e) {
					Log.e(TAG,"JSONException");
				}
				if (flag){
					//upvote
					Log.v(TAG,"SubmitVote-upvote-local");
					database.updateFortuneCol(fortune.getFortuneID(), 
							FortuneDbAdapter.KEY_UPVOTED, fortune.getUpvoted());
					database.updateFortuneCol(fortune.getFortuneID(), 
							FortuneDbAdapter.KEY_UPVOTES, fortune.getUpvotes());
				}else{
					Log.v(TAG,"SubmitVote-downvote-local");
					//downvote
					database.updateFortuneCol(fortune.getFortuneID(), 
							FortuneDbAdapter.KEY_DOWNVOTED, fortune.getDownvoted());
					database.updateFortuneCol(fortune.getFortuneID(), 
							FortuneDbAdapter.KEY_DOWNVOTES, fortune.getDownvotes());
				}
		    }
		}).start();

	}
	
	/**
	 * Updates a fortune
	 * THIS SHOULD ONLY BE CALLED FROM WITHIN FORTUNE!
	 * @param fortune the fortune
	 */
	public void updateFortune(final Fortune f)
	{
		Log.v(TAG,"Updating fortune id: "+f.getFortuneID());

		new Thread(new Runnable() {
		    public void run() {
		    	Fortune newF;
		    	JSONObject obj = getRequestJSON();
				try {
					obj.put("fortuneid",f.getFortuneID() );
					JSONArray jsonArr = sendData("getFortuneByID", obj);
					if (jsonArr == null || jsonArr.length() < 1){
						return;
					}
					newF = Fortune.createFromJSON(jsonArr.getJSONObject(0));
			    	
					//no need to manually set new updated dated. Fortune ctor
					//shoudl take care of that.
					if ( newF != null ) {
						database.updateFortune(newF);
					}
			    } catch (JSONException e) {
					Log.e(TAG,"JSONException");
			    }
		    }
		}).start();

	}
	
	/**
	 * Submit a fortune to the server and add it to the local database
	 * @param text the message for the fortune
	 */
	public boolean submitFortune(final String text)
	{
		JSONObject obj = getRequestJSON();
		try {
			obj.put("text", text);
			JSONArray response = sendData("submitFortune", obj);
			if (response == null || response.length() < 1) {
				return false;
			}
			int id = database.insertFortune(Fortune.createFromJSON(response.getJSONObject(0)));
			database.updateFortuneCol(id,FortuneDbAdapter.KEY_OWNER, true);
			database.updateFortuneCol(id,FortuneDbAdapter.KEY_UPVOTED, true);
		} catch (JSONException e) {
			Log.e(TAG,"JSONException");
			return false;
		}
		return true;

	}
	
	/**
	 * returns fortune stored locally but not by user
	 * @return an arraylist f the fortunes
	 */
	public ArrayList<Fortune> getSeenFortunes()
	{
		//TODO filter only seen fortunes
		return database.fetchAllBy(FortuneDbAdapter.KEY_OWNER+"=" + "0",
				FortuneDbAdapter.KEY_VIEWDATE+" DESC","1,20");
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
	 * Get the number of locally stored fortunes
	 * @return an arraylist of the fortunes
	 */
	public int getNumberOfLocalFortunes()
	{
		int count = database.getNumberOfRows();
		Log.v(TAG, "Number of fortunes in local db: "+count);
		return count;
	}
	
	
	/**
	 * gets a new fortune from the database and adds it to the database
	 * THIS METHOS MUST BE CALLED FROM AN ASYNCTASK OR OTHER THREAD OR IT WILL FAIL!
	 * @return the new fortune, or null if error
	 */
	public Fortune getFortune()
	{
		JSONObject obj = getRequestJSON();
		JSONObject json;
		try {
			JSONArray jsonArr = sendData("getFortune", obj);
			//TODO these check should be everywhere sendData is located
			if (jsonArr == null || jsonArr.length() == 0){
				return null;
			}
			json = jsonArr.getJSONObject(0);
		} catch (JSONException e) {
			return null;
		}
		Fortune ret = Fortune.createFromJSON(json);
		database.insertFortune(ret);
		return ret;
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
		Log.v(TAG, "Looking for fortuneID:" + id + " in local database");
		Fortune ret = database.fetchFortune(id);
		if (ret == null)
		{
			Log.v(TAG, "Looking for fortuneID:" + id + " in remote database");
			JSONObject obj = getRequestJSON();
			try {
				obj.put("fortuneid",id );
				JSONArray jsonArr = sendData("getFortuneByID", obj);
				if (jsonArr == null || jsonArr.length() < 1){
					return null;
				}
				ret = Fortune.createFromJSON(jsonArr.getJSONObject(0));
				if (ret != null) {
					database.insertFortune(ret);
				}
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
	 * Update current fortune by requesting an unseen fortune and then setting 
	 * the id of the fortune into a hidden SharedPref
	 */
	public Fortune updateCurrentFortune() {
		Log.v(TAG, "Updating current fortune");
		Fortune f = getInstance().getFortune();
		if (f != null) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
			Editor editor = prefs.edit();
			editor.putLong(SettingsActivity.KEY_CURR_FORTUNE, f.getFortuneID());
			editor.commit();
			
			Log.v(TAG, "Current fortune id: " + f.getFortuneID());
		}
		return f;
	}
	
	/*
	 * Get current Fortune
	 * @return Fortune current fortune object or null if there is none
	 */
	public Fortune getCurrentFortune() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getInstance().mContext);	
		long id = prefs.getLong(SettingsActivity.KEY_CURR_FORTUNE, -1); //do error checking??
		Log.v(TAG, "Current fortune has id of : " + id);
		
		if (id < 0) {
			return null;
		}
		Fortune fortune = getInstance().getFortuneByID(id);
		return fortune;
	}
	
	/*
	 * Get clear local database
	 */
	public void updateFortuneVotes(Fortune f) {
		FortuneDbAdapter.getInstance().updateFortuneVotes(f);
	}
		
	/*
	 * Get clear local database
	 */
	public void clearDatabase() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		Editor editor = prefs.edit();
		editor.putLong(SettingsActivity.KEY_CURR_FORTUNE, -1);
		editor.commit();
		
		FortuneDbAdapter.getInstance().removeAll();
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
	private JSONArray sendData(String action,JSONObject obj)
	{
		Log.v(TAG,"Sending data to server");
		final String ERROR_FLAG = "accepted";
		final String SERVER_DATA = "result";
		if (!enableServerCommunication)
		{
			return null;
		}
		HttpClient client = new DefaultHttpClient(); 
		String url = SERVER+"?action="+action;
		Log.v(TAG,"URL: "+url);
		HttpPost post = new HttpPost(url);
		//post.setHeader("Content-type", "application/json");
		post.setHeader("Content-type", "application/x-www-form-urlencoded");
        HttpResponse response = null;
        String responseString = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		try {
			Log.v(TAG,"POSTING: "+obj.toString());
	        //post.setEntity(new StringEntity(obj.toString(), "UTF-8"));
	        nameValuePairs.add(new BasicNameValuePair("json", obj.toString())); 
	        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = client.execute(post,new BasicHttpContext());
		} catch (ClientProtocolException e) {
			Log.e(TAG,"ClientProtocolException");
			return null;
		} catch (IOException e) {
			Log.e(TAG,"IOException");
			return null;
		}
		if (response == null) {
			Log.e(TAG,"Server Timeout");
			return null;
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
	    try {
			JSONObject responseObj = new JSONObject(responseString);
			Log.v(TAG,"JSON OBJ: "+responseObj.toString());
			Log.v(TAG,"JSON ACC: "+responseObj.has(ERROR_FLAG));
			boolean valid = responseObj.getBoolean(ERROR_FLAG);
			if (!valid)
			{
				Log.e(TAG,"Server error");
				return null;
			}
			if (responseObj.has(SERVER_DATA))
			{
				return responseObj.getJSONArray(SERVER_DATA);
			}else{
				return null;
			}
		} catch (JSONException e) {
			Log.e(TAG,"Unable to parse resposne json for accepted");
			Log.e(TAG,"RESPONSE: "+responseString);
			return null;
		}
	}
	
}
