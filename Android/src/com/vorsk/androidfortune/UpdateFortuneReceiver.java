package com.vorsk.androidfortune; 

import com.vorsk.androidfortune.data.Client;
import com.vorsk.androidfortune.data.Fortune;
import com.vorsk.androidfortune.widget.WidgetActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


public class UpdateFortuneReceiver extends BroadcastReceiver {
	private static String TAG = "UpdateFortuneReceiver";
	
	//we may want to change this to false for errors caused by the alarm
	public static final boolean display_errors = true;
	
	public UpdateFortuneReceiver() {
	}

	//Tell Client to update current fortune
	@Override
	public void onReceive(Context context, Intent intnt) {	
		class GetFortuneTask extends AsyncTask<Void, Void, Fortune>{
			public Context mContext = null;
			public GetFortuneTask(Context c) {
				super();
				mContext = c;
			}
			@Override
			protected Fortune doInBackground(Void... params) {
				return Client.getInstance(mContext.getApplicationContext()).updateCurrentFortune();
			}
			 protected void onPostExecute(Fortune f) {
				 if ( f != null) {
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
					
					 if (prefs.getBoolean(SettingsActivity.KEY_NOTIFICATION_ENABLE,false)) {
					 f.displayNotification(mContext);
					 }
					 WidgetActivity.displayFortune(mContext, f);
					 //TODO update home screen with new fortune
				 }else if(display_errors){
						Toast.makeText(mContext, R.string.server_error, Toast.LENGTH_SHORT).show();
				 }
			 }
		}
		
		// Build notification
		GetFortuneTask task = new GetFortuneTask(context);
		Log.v(TAG, "Alarm triggered");
		task.execute();
	}
	
}
