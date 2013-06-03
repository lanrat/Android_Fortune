package com.vorsk.androidfortune;

import java.util.ArrayList;
import java.util.Collections;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.vorsk.androidfortune.data.Client;
import com.vorsk.androidfortune.data.Fortune;

import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class SubmitActivity extends SherlockFragmentActivity {
	
	public static final String FRAGMENT_NAME = "Submit Activity";


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
		EasyTracker.getTracker().sendView(FRAGMENT_NAME);
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
			
			ArrayList<Fortune> list = Client.getInstance().getFortunesSubmitted();
			Collections.reverse(list); // reverse chronological order
			Fortune[] fortunes = list.toArray(new Fortune[list.size()]);
			
			FortuneArrayAdapter adapter = new FortuneArrayAdapter(getActivity(), fortunes);
			ListView lv = (ListView) layout.findViewById(R.id.submit_list);
			lv.setAdapter(adapter);
	
			View submitButton = layout.findViewById(R.id.submitButton);
			((Button)submitButton).setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					LayoutInflater inf = LayoutInflater.from(getActivity().getApplicationContext());
					View promptsView = inf.inflate(R.layout.submit_fortune_dialog, null);
	 
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							getActivity() );
	 
					// set prompts.xml to alertdialog builder
					alertDialogBuilder.setView(promptsView);
					
					final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialog);

					
					// set dialog message
					alertDialogBuilder
						.setCancelable(false)
						.setPositiveButton("Submit",
						  new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog,int id) {
						    	Log.v("SubmitActivity", "Attempting to submit fortune");
				            	class SubmitFortuneTask extends AsyncTask<Void, Void, Void>{
				            		private String mText;
				            		public SubmitFortuneTask (String text) {
				            			super();
				            			mText = text;
				            		}
				            		
				        			@Override
				        			protected Void doInBackground(Void... params) {
				        				Client.getInstance().submitFortune(mText);
				        				return null; //TODO what to return	
				        			}
				            	}
				            	// Build notification
				        		SubmitFortuneTask task = new SubmitFortuneTask(userInput.getText().toString());
				        		task.execute();
				            	userInput.setText(null);
				            	
				            	// hide keyboard
				            	InputMethodManager inputManager = 
				            	        (InputMethodManager) getSherlockActivity().
				            	            getSystemService(Context.INPUT_METHOD_SERVICE);
				            	inputManager.hideSoftInputFromWindow(userInput.getWindowToken(), 
				            			InputMethodManager.HIDE_NOT_ALWAYS);
						    }  
						 })
						.setNegativeButton("Cancel",
						  new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog,int id) {
							dialog.cancel();
						    }
						  });
	 
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
	 
					// show it
					alertDialog.show();
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
