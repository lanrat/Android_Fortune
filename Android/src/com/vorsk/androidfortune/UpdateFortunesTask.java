package com.vorsk.androidfortune;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.vorsk.androidfortune.data.Client;
import com.vorsk.androidfortune.data.Fortune;
import com.vorsk.androidfortune.widget.WidgetActivity;

public class UpdateFortunesTask  extends AsyncTask<Void, Void, Fortune>{
	public Context mContext;
	public ArrayList<Fortune> mList;
	
	public UpdateFortunesTask(Context c, ArrayList<Fortune> list) {
		super();
		mContext = c;
		mList = list;
	}
	
	//TODO add on update progress for updating one fortune at a time
	@Override
	protected Fortune doInBackground(Void... params) {
		Log.v("UpdateFortunesTask", "Updating all fortunes in a list");
		for ( Fortune f : mList ) {
			//TODO fix this
			if (f != null) {
				f.update();
			}
		}
		
		return Client.getInstance().getCurrentFortune();
	}
	 protected void onPostExecute(Fortune f) {
		 if ( f != null) {
			//show updated stuff
			 WidgetActivity.displayFortune(mContext, f);
			 HomeActivity.FortuneFragment.displayFortune(f);
		 }
		 HistoryActivity.HistoryFragment.refreshHistory();
		 SubmitActivity.SubmitFragment.refreshSubmitted();
	 }
}
