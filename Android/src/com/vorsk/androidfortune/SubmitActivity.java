package com.vorsk.androidfortune;

import java.util.ArrayList;
import android.annotation.SuppressLint;
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
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
	
	//TODO remove the need for this
	@SuppressLint("NewApi")
	public static void showSubmitDiolog(final Context ctx) {
		LayoutInflater inf = LayoutInflater.from(ctx);
		View promptsView = inf.inflate(R.layout.submit_fortune_dialog, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				ctx );

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);
		
		final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialog);
		//use this bit of code to enable line wrapping
		userInput.setInputType(
			    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
		userInput.setSingleLine(true);
		userInput.setLines(4); // desired number of lines
		userInput.setHorizontallyScrolling(false);
		userInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
		
		// set dialog message
		alertDialogBuilder
			.setCancelable(true)
			.setPositiveButton("Submit",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
		    		//do nothing here. over ridden
			    }
			 })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		// create alert dialog
		final AlertDialog alertDialog = alertDialogBuilder.create();

		//TODO this is not supported in API 7
		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

		    @Override
		    public void onShow(DialogInterface dialog) {

		        Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
		        b.setOnClickListener(new View.OnClickListener() {
		        	
		            @Override
		            public void onClick(View view) {
				    	Log.v("SubmitActivity", "Attempting to submit fortune");
				    	String newFortune = userInput.getText().toString();
				    	if (newFortune != null && newFortune.length() > 5 || newFortune.length()>255)
				    	{
			            	class SubmitFortuneTask extends AsyncTask<String, Void, Boolean>{
			            		private Context ctx;
			            		public SubmitFortuneTask (Context ctx) {
			            			this.ctx = ctx;
			            		}
			            		
			        			@Override
			        			protected Boolean doInBackground(String... params) {
			        				return Client.getInstance().submitFortune(params[0]);	
			        			}
			        			@Override
			        			 protected void onPostExecute(Boolean result) {
			        				if (result) {
			        					//refresh submit list
			        					SubmitFragment.refreshSubmitted();
			        				}else {
			        					//error
			        					Toast.makeText(ctx, R.string.server_error, Toast.LENGTH_SHORT).show();
			        				}
			        			}
			            	}
			            	// Build notification
			        		SubmitFortuneTask task = new SubmitFortuneTask(ctx);
			        		task.execute(userInput.getText().toString());
			            	userInput.setText(null);
			            	
			            	// hide keyboard
			            	InputMethodManager inputManager = 
			            	        (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
			            	inputManager.hideSoftInputFromWindow(userInput.getWindowToken(), 
			            			InputMethodManager.HIDE_NOT_ALWAYS);
			            	
			                alertDialog.dismiss();
			            	
					    } else
		            		Toast.makeText(ctx, R.string.submit_text_error, Toast.LENGTH_SHORT).show();
				    	
		            }//onClick

		        });
		    
		    }//onShow
		});
		
		// show it
		alertDialog.show();
		
		
	}

	public static class SubmitFragment extends SherlockFragment {
		private static View mView;
		
		public static void refreshSubmitted() {
			if (mView == null) {
				return;
			}
			ArrayList<Fortune> list = Client.getInstance().getFortunesSubmitted(); 

			Fortune[] fortunes = list.toArray(new Fortune[list.size()]);
			
			FortuneArrayAdapter adapter = new FortuneArrayAdapter(mView.getContext(), fortunes);
			ListView lv = (ListView) mView.findViewById(R.id.submit_list);
			lv.setEmptyView(mView.findViewById(R.id.blank));
			lv.setAdapter(adapter);	
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			mView = inflater.inflate(R.layout.submit, container, false);
			return mView;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			refreshSubmitted();
		}

	}
}
