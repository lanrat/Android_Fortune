package com.vorsk.androidfortune;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
