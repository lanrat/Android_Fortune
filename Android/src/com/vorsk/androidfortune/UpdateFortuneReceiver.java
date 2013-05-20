package com.vorsk.androidfortune;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;


public class UpdateFortuneReceiver extends BroadcastReceiver {
	private static String TAG = "UpdateFortuneReceiver";
	public UpdateFortuneReceiver() {
	}

	//Tell Client to update current fortune
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "onReceive Called");
		//Client.getInstance().updateCurrentFortune();
	}
	
}
