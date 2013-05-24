package com.vorsk.androidfortune;

import android.content.Context;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class SubmitActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentManager fm = getSupportFragmentManager();

		// Create the list fragment and add it as our sole content.
		if (fm.findFragmentById(android.R.id.content) == null) {
			SubmitFragment fragment = new SubmitFragment();
			fm.beginTransaction().add(android.R.id.content, fragment).commit();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	public static class SubmitFragment extends SherlockFragment {

		private static RelativeLayout layout;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			layout = (RelativeLayout)inflater.inflate(R.layout.submit, null);
	
			View submitButton = layout.findViewById(R.id.button1);
			((Button)submitButton).setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View v) {
	            	Client.getInstance().submitFortune(
	            			((EditText)layout.findViewById(R.id.editText1)).getText().toString());
	            	
	            	// reset edit text
	            	EditText editText = (EditText)layout.findViewById(R.id.editText1);
	            	editText.setText(null);
	            	
	            	// hide keyboard
	            	InputMethodManager inputManager = 
	            	        (InputMethodManager) getSherlockActivity().
	            	            getSystemService(Context.INPUT_METHOD_SERVICE); 
	            	inputManager.hideSoftInputFromWindow(
	            	        getSherlockActivity().getCurrentFocus().getWindowToken(),
	            	        InputMethodManager.HIDE_NOT_ALWAYS); 
	            }
			});
			return layout;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
		}
		
		

	}
}
