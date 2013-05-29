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
		/*Log.v(TAG, "onReceive Called");
		//TODO
		//Fortune f = Client.getInstance().updateCurrentFortune();
		Fortune f = Client.getInstance().getFortuneByID(1);
		if ( f != null ) {
			//send notification
			FortuneActivity.createNotificationFromFortune(context.getApplicationContext(),f);
			//update widget
			WidgetActivity.displayFortune(f);
		}*/
		
		//get fortune
		final long id = 1; //TODO this is for testing
		class GetTestFortune extends AsyncTask<Void, Void, Fortune>{
			public Context mContext = null;
			public GetTestFortune(Context c) {
				super();
				mContext = c;
			}
			@Override
			protected Fortune doInBackground(Void... params) {
				return Client.getInstance().getFortuneByID(id);
			}
			 protected void onPostExecute(Fortune f) {
				 FortuneActivity.createNotificationFromFortune(mContext.getApplicationContext(),f);
				 WidgetActivity.displayFortune(f);
			 }
		}
		
		// Build notification
		GetTestFortune test = new GetTestFortune(context);
		test.execute();
		//createNotificationFromFortune(f);
	}
	
}
