package com.vorsk.androidfortune;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;


public class ServerTask extends AsyncTask<ServerTask.Action, Void, Boolean>
{
	private static String TAG = "ServerTask";
	private static final String SERVER = "http://cse-190-fortune.herokuapp.com/server.php";
	private String userID;

	public ServerTask(String userID)
	{
		super();
		this.userID = userID;
	}

	public enum Action {
		getFortunesSubmitted, getFortune, getFortuneByID, submitVote,
		submitFortune, submitFlag, submitView 
	}
	
	@Override
	protected Boolean doInBackground(Action... args) {
		
		for (int i=0; i<args.length;i++)
		{
			JSONObject obj = new JSONObject();
			try {
				obj.put("user",this.userID );
				
				switch (args[i]) {
				case getFortunesSubmitted:
					
					break;
				case getFortune:
					
					break;
				case getFortuneByID:
					
					break;
				case submitVote:
					
					break;
				case submitFortune:
					
					break;
				case submitFlag:
					
					break;
				case submitView:
					
					break;
	
				default:
					Log.e(TAG,"Unkown Action");
					break;
				}
			} catch (JSONException e) {
				Log.e(TAG,"JSONException");
			}
			sendData(args[i],obj);
		}
		
		return false;
	}
	
	
	private HttpResponse sendData(Action action,JSONObject obj)
	{
		HttpClient client = new DefaultHttpClient(); 
		HttpPost post = new HttpPost(SERVER+"?action="+action.name());
		post.setHeader("Content-type", "application/json");
        HttpResponse response = null;
		try {
	        post.setEntity(new StringEntity(obj.toString(), "UTF-8"));
			response = client.execute(post,new BasicHttpContext());
		} catch (ClientProtocolException e) {
			Log.e(TAG,"ClientProtocolException");
		} catch (IOException e) {
			Log.e(TAG,"IOException");
		}
		return response;
	}
}