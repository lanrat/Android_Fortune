package com.vorsk.androidfortune; 

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;


public class UpdateFortuneReceiver extends BroadcastReceiver {
	private static String TAG = "UpdateFortuneReceiver";
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
				return Client.getInstance().getFortune();
			}
			 protected void onPostExecute(Fortune f) {
				 FortuneActivity.createNotificationFromFortune(mContext.getApplicationContext(),f);
				 WidgetActivity.displayFortune(f);
			 }
		}
		
		// Build notification
		GetFortuneTask task = new GetFortuneTask(context);
		task.execute();
	}
	
}
