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

	public Client(String userID)
	{
		this.userID = userID;
		this.database = FortuneDbAdapter.getInstance();
	}

	public enum Action
	{
		getFortunesSubmitted, getFortune, getFortuneByID, submitVote,
		submitFortune, submitFlag, submitView 
	}
	
	public Fortune getFortune()
	{
		JSONObject obj = getRequestJSON();
		String json = sendData("getFortune", obj);
		try {
			return database.fetchFortune(database.createFortuneFromJson(json));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	public Fortune getFortuneByID(long id)
	{
		//TODO handle fortune not in DB
		//fetchFortune does not do anything if fortune not found in local DB
		return database.fetchFortune(id);
	}
	
	public ArrayList<Fortune> getFortunesSubmitted()
	{
		//TODO handle DB
		JSONObject obj = getRequestJSON();
		String json = sendData("getFortunesSubmitted", obj);
		JSONArray a; 
		try {
			JSONObject response = new JSONObject(json);
			a = response.getJSONArray("fortunes");
		} catch (JSONException e) {
			//TODO log or something
			return null;
		}
		ArrayList<Fortune> result = new ArrayList<Fortune>(a.length());
		for (int i = 0; i < a.length(); i++)
		{
			//TODO run this through the DB
			//result.add(new Fortune(a.get(i)));
		}
		return result;
		

		
	}
	
	
	
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
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	    return responseString;
	}
}